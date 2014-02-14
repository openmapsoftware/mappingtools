package com.openMap1.mapper.fhir;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Code;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceFactory;
import org.hl7.fhir.instance.model.String_;
import org.hl7.fhir.instance.model.Type;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;

/**
 * This class provides a bridge between:
 * (a) any Ecore class model generated  (as a mapping target) from a FHIR resource or profile
 * (b) the class model of the FHIR Java reference implementation
 * 
 * Given an instance of (a), it will return an instance of (b).
 * Given an instance of (b), it will return an instance of (a).
 * 
 * @author RPW
 *
 */

public class EcoreReferenceBridge {
	
	
	private boolean tracing = false;
	
	private EPackage eCoreModel;
	
	public static String REFERENCE_MODEL_PACKAGE = "org.hl7.fhir.instance.model.";
	
	/**
	 * resource, component and datatype classes of the reference implementation required by the Ecore model
	 *  key - class name; value = the Class object
	 */
	private Hashtable<String,Class<?>> refModelClasses;
	
	/**
	 * code binding classes of the reference implementation required by the Ecore model
	 *  key - class name; value = the Class object
	 */
	private Hashtable<String,Class<?>> bindingClasses;
	
	/**
	 * code binding factory classes of the reference implementation required by the Ecore model
	 *  key - class name; value = the Class object
	 */
	private Hashtable<String,Class<?>> bindingFactories;

	
	/**
	 *  setter and getter methods of the reference model classes
	 *  first key = class name; second key = feature name in Ecore model; value = setter or getter method
	 */
	private Hashtable<String,Hashtable<String,Method>> getters;
	private Hashtable<String,Hashtable<String,Method>> setters;
	
	// these reference model classes are sometimes needed as arguments to setter  methods
	private String[] specialClasses = {"ResourceReference","Type"};
	
	// lookup table from Ecore resources to the ids they were given when making the reference model instance
	private Hashtable<EObject,String> readEcoreResourceToId;
	
	
	/* lookup table from read in resource id to created Ecore form of resource.
	 * In case two resources of different resource type have the same id, store multiple resources per id */
	private Hashtable<String,Vector<EObject>> readIdToCreatedEcoreResource;
	
	// names of fhir packages
	public static String[] packageNames = {"feed","resources","components","complexTypes","primitiveTypes"};
	
	// fhir packages
	private EPackage[] fhirPackages = new EPackage[5];
	
	// indexes of FHIR packages
	public static int FEED = 0;
	public static int RESOURCES = 1;
	public static int COMPONENTS = 2;
	public static int COMPLEXTYPES = 3;
	public static int PRIMITIVETYPES = 4;
	
	public Hashtable<String,String> getModelErrors() {return modelErrors;}
	private Hashtable<String,String> modelErrors;
	
	public Hashtable<String,String> getInstanceErrors() {return instanceErrors;}
	private Hashtable<String,String> instanceErrors;

	
	//---------------------------------------------------------------------------------------------------
	//                       Constructor and checks of the Ecore model
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * constructor; checks that all the necessary classes can be found in the reference implementation
	 * @param eCoreModel
	 * @throws MapperException if any classes in the Ecore model do not have equivalents 
	 * in the Java reference implementation
	 */
	public EcoreReferenceBridge(EPackage eCoreModel) throws MapperException
	{
		this.eCoreModel = eCoreModel;
		initialise();
		
		checkModel();
	}
	
	/**
	 * initialisation 
	 */
	private void initialise()
	{
		// reference model classes
		refModelClasses = new Hashtable<String,Class<?>>() ;
		bindingClasses = new Hashtable<String,Class<?>>() ;
		bindingFactories = new Hashtable<String,Class<?>>() ;
		
		// setter and getter methods of the reference model classes
		getters = new Hashtable<String,Hashtable<String,Method>>() ;
		setters = new Hashtable<String,Hashtable<String,Method>>() ;

		// tables needed to resolve resource references (which may be made before the resources)
		readEcoreResourceToId = new Hashtable<EObject,String>();
		
		readIdToCreatedEcoreResource = new Hashtable<String,Vector<EObject>>() ;
		
		// recording errors in checking the model or making an instance
		modelErrors = new Hashtable<String,String>();
		instanceErrors = new Hashtable<String,String>();

	}
	
	/**
	 * check that all classes in the Ecore model can be found in the FHIR Java reference implementation
	 * @throws MapperException
	 */
	public void checkModel() throws MapperException
	{
		// model should  have a top package 'fhir' containing all other packages
		String packageName = eCoreModel.getName();
		if (!packageName.equals("fhir"))
			throw new MapperException("Top package is called '" + packageName + "' but should be called 'fhir");
		
		// find other packages in order
		int p = 0;
		boolean AtomFeedFound = false;
		for (Iterator<EPackage> ip = eCoreModel.getESubpackages().iterator();ip.hasNext();)
		{
			fhirPackages[p] = ip.next();
			if (!fhirPackages[p].getName().equals(packageNames[p]))
				throw new MapperException("Unexpected sub-package name: "  + fhirPackages[p].getName());

			// check all classes in the Ecore model
			for (Iterator<EClassifier> it = fhirPackages[p].getEClassifiers().iterator();it.hasNext();)
			{
				EClassifier next = it.next();
				if (next instanceof EClass)
				{
					EClass theClass = (EClass)next;
					// String classType = ModelUtil.getMIFAnnotation(theClass, "type");
					if (theClass.getName().equals("AtomFeed")) AtomFeedFound = true;
					checkClass(theClass);
					
					/* for any bindings on EAttributes of primitive type 'code', find the binding Enumeration and factory.
					 * Complex data type Demographics has bindings on associations gender and maritalStatus, but 
					 * these do not have binding enumerations in the reference implementation */
					for (Iterator<EStructuralFeature> iu = theClass.getEStructuralFeatures().iterator();iu.hasNext();)
					{
						EStructuralFeature feat = iu.next();
						String binding = ModelUtil.getMIFAnnotation(feat, "Binding");
						if ((binding != null) && (!binding.equals("")))
						{
							String primitiveType = ModelUtil.getMIFAnnotation(feat, "PrimitiveType");
							if ((primitiveType != null) && (primitiveType.equals("code")) && (feat instanceof EAttribute)) 
								getBindingClass(binding,theClass);							
						}
					}
				}
			}
			p++;
		}
		
		
		// find a few special classes in the reference model
		for (int c = 0; c < specialClasses.length;c++)
		{
			String className = specialClasses[c];
			Class<?> special = getSpecialReferenceClass(className);
			refModelClasses.put(className, special);
		}
		
		// primitive data type classes
		for (int c = 0; c < PrimitiveTypes.PRIMITIVETYPES.length;c++)
		{
			String type = PrimitiveTypes.PRIMITIVETYPES[c];
			String className = PrimitiveTypes.referenceClassName(type);
			Class<?> special = getSpecialReferenceClass(className);
			refModelClasses.put(className, special);
		}
		
		/* when all reference model classes have been found, find the setter methods 
		 * (which need to have classes as their arguments)*/ 
		findAllSetterMethods();
		
		if (!AtomFeedFound) throw new MapperException("Class model has no 'AtomFeed' class");
	}
	
	/**
	 * 
	 * @param theClass
	 * @throws MapperException
	 */
	private void checkClass(EClass theClass) throws MapperException
	{
		String className = theClass.getName();
		String inResource = ModelUtil.getMIFAnnotation(theClass, "inResource");
		if ((inResource != null) && (inResource.equals("List"))) inResource = "List_";
		Class<?> refClass = makeReferenceClass(theClass,inResource);
		if (refClass != null) refModelClasses.put(className, refClass);
	}
	
	//--------------------------------------------------------------------------------------------------------
	// 			    Getting the Classes and Methods of the Reference Implementation
	//--------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param className
	 * @return
	 * @throws MapperException
	 */
	private Class<?> makeReferenceClass(EClass theClass, String inResource) throws MapperException
	{
		String fullClassName = theClass.getName();
		// deal with special class 'List'; inResource has already been fixed for this
		if (fullClassName.equals("List")) fullClassName = "List_";
		if (inResource != null) fullClassName = inResource + "$" + fullClassName;
		Class<?> refClass = null;
		String referenceClassName = REFERENCE_MODEL_PACKAGE + fullClassName;
		try 
		{
			refClass = Class.forName(referenceClassName);
			trace("Found class " + referenceClassName);
			
			Hashtable<String,Method> classGetters = new Hashtable<String,Method>();
			
			for (Iterator<EStructuralFeature> it = theClass.getEStructuralFeatures().iterator();it.hasNext();)
			{
				EStructuralFeature feature = it.next();
				
				if ((fullClassName.equals("AtomFeed")) && (feature instanceof EReference)) {}
				// the special attribute 'fhir_id' is not in the referecne implementation
				else if (!(feature.getName().equals("fhir_id")))
				{
					Method getter = getGetter(refClass,feature);
					if (getter != null) classGetters.put(feature.getName(),getter);
				}
			}			
			trace("Getters: " + writeFoundMethods(classGetters));
			getters.put(theClass.getName(), classGetters);
		}
		catch (Exception ex) 
		{
			trace("Cannot find reference model class " + referenceClassName + "; " + ex.getMessage());
			modelErrors.put(referenceClassName, "Cannot find reference model class " + referenceClassName);
		}		
		return refClass;
	}
	
	/**
	 * 
	 * @param binding
	 * @param theClass
	 */
	private void getBindingClass(String binding, EClass theClass)
	{
		// find the class containing the binding Enum. If the direct owning class is a Component class, the containing resource class holds the binding
		String containingClassName = theClass.getName();
		String inResource = ModelUtil.getMIFAnnotation(theClass, "inResource");
		if (inResource != null) containingClassName = inResource;
		if (containingClassName.equals("List")) containingClassName = "List_";
		
		// there is no consistent rule for binding class names; so this hack is necessary
		String modBinding = binding;
		String[] badBindings = 
				{"SensitivityType",
				"SensitivityStatus",
				"ParticipantRequired",
				"ParticipationStatus",
				"SlotStatus",
				"ActStatus",
				"QuantityCompararator"};   //sic
		if (GenUtil.inArray(binding, badBindings)) 
			modBinding = GenUtil.initialUpperCase(binding.toLowerCase());
		
		String bindingClassName = REFERENCE_MODEL_PACKAGE + containingClassName + "$" + modBinding;
		String bindingFactoryName = bindingClassName + "EnumFactory";
		try
		{
			Class<?> bindingClass = Class.forName(bindingClassName);
			trace("Found binding class " + bindingClassName);
			if (bindingClasses.get(binding) != null) trace("More than one class for binding " + binding);
			bindingClasses.put(binding, bindingClass);

			Class<?> bindingFactory = Class.forName(bindingFactoryName);
			trace("Found binding factory " + bindingFactoryName);
			if (bindingFactories.get(binding) != null) trace("More than one factory class for binding " + binding);
			bindingFactories.put(binding, bindingFactory);
			
		}
		catch (Exception ex) 
		{
			// two bindings in data type 'Attachment' will not be found as they are not FHIR-defined
			if (binding.equals("Language")) {}
			else if (binding.equals("MimeType")) {}
			else 
			{
				message("*** Failed to find binding class " + bindingClassName + " in class " + containingClassName);
				modelErrors.put(theClass.getName(), "Cannot find binding class " + bindingClassName + " in class " + containingClassName);
			}
		}
	}

	
	/**
	 * 
	 * @param className
	 * @return
	 */
	private Class<?> getSpecialReferenceClass(String className)
	{
		Class<?> refClass = null;
		String referenceClassName = REFERENCE_MODEL_PACKAGE + className;
		try 
		{
			refClass = Class.forName(referenceClassName);
			trace("Found special reference class or primitive data type class " + referenceClassName);
		}
		catch (Exception ex) 
		{
			modelErrors.put(referenceClassName, "Cannot find reference model class " + referenceClassName);
			trace("Cannot find reference model class " + referenceClassName );
		}		
		return refClass;		
	}
	
	/**
	 * 
	 * @param refClass
	 * @param feature
	 * @return
	 */
	private Method getGetter(Class<?> refClass, EStructuralFeature feature)
	{
		Method getter = null;
		String featName  = feature.getName();
		// naming convention in reference implementation
		if (featName.equals("class")) featName = "class_";
		String refModelName = ModelUtil.getMIFAnnotation(feature, "RefModelName");
		if (refModelName != null) featName = refModelName;
		String getterName = "get" + GenUtil.initialUpperCase(featName);
		// getter methods have no arguments
		Class<?>[] args = new Class<?>[0];
		try
		{
			// getMethod, rather than getDeclaredMethod, allows for methods inherited from superclasses
			getter = refClass.getMethod(getterName, args);
		}
		catch (Exception ex) 
		{
			modelErrors.put(refClass.getName(), "Cannot find getter method " + getterName);
			trace("Cannot find getter method " + getterName + " of class " + refClass.getName());
		}
		return getter;
	}
	
	/**
	 * 
	 * @param methods
	 * @return a string of Ecore model feature names , for which getters or setters have been found
	 */
	private String writeFoundMethods(Hashtable<String,Method> methods)
	{
		String methodNames = "";
		for (Enumeration<String> en = methods.keys(); en.hasMoreElements();) methodNames = methodNames + en.nextElement() + "; ";
		return methodNames;
	}
	
	/**
	 * 
	 */
	private void findAllSetterMethods()
	{
		for (Enumeration<String> en = refModelClasses.keys(); en.hasMoreElements();)
		{
			String ecoreClassName = en.nextElement();
			Class<?> refClass = refModelClasses.get(ecoreClassName);
			EClass theClass = getNamedClass(ecoreClassName);

			Hashtable<String,Method> classSetters = new Hashtable<String,Method>();

			if (theClass != null)  // there is no Ecore class for the special classes
			{
				for (Iterator<EStructuralFeature> it = theClass.getEStructuralFeatures().iterator();it.hasNext();)
				{
					EStructuralFeature feature = it.next();
					
					// there are no setter methods for associations with unbounded multiplicity - use the getter and add
					if ((feature instanceof EReference) && (((EReference)feature).getUpperBound() == -1)) {}
					
					else
					{
						// do nothing for the associations from AtomFeed to resources - use entiry in stead
						if ((ecoreClassName.equals("AtomFeed")) && ((feature instanceof EReference))) {}
						// the special attribute 'fhir_id' is not in the reference model
						else if (!(feature.getName().equals("fhir_id")))
						{
							Method setter = getSetter(theClass,refClass,feature);
							if (setter != null) classSetters.put(feature.getName(),setter);												
						}
					}
				}
				
				trace("Setters for Ecore class " + ecoreClassName + ": " + writeFoundMethods(classSetters));
				setters.put(theClass.getName(), classSetters);				
			}
		}
	}
	
	/**
	 * get a named class in any package, assuming that the same class name 
	 * does not occur in different packages
	 * @param className
	 * @return
	 */
	private EClass getNamedClass(String className)
	{
		EClass namedClass = null;
		for (int p = 0; p < packageNames.length; p++)
		{
			EClassifier  next = fhirPackages[p].getEClassifier(className);
			if ((next != null) && (next instanceof EClass))  namedClass = (EClass)next;
		}
		return namedClass;
	}


	
	/**
	 * 
	 * @param refClass
	 * @param feature
	 * @return
	 */
	private Method getSetter(EClass ownerClass, Class<?> refClass, EStructuralFeature feature)
	{
		String refClassName = refClass.getName();
		String featureName = feature.getName();
		Method setter = null;
		EClassifier type  = feature.getEType();
		String refModelName = ModelUtil.getMIFAnnotation(feature, "RefModelName");
		if (refModelName != null) featureName = refModelName;
		String setterName = "set" + GenUtil.initialUpperCase(featureName);
		if (setterName.equals("setClass")) setterName = "setClass_";
		Class<?>[] args = new Class<?>[1];

		// EReferences with upper bound 1, whose type is a class
		if (type instanceof EClass) try
		{
			args[0] = refModelClasses.get(type.getName());
			// correct argument type for resource references
			if (ModelUtil.getMIFAnnotation(type, "type").equals("Resource")) 
				args[0] = refModelClasses.get("ResourceReference");
			if (args[0] != null) setter = refClass.getDeclaredMethod(setterName, args);
			else 
			{
				modelErrors.put(refClassName, "Cannot find class " + type.getName() + " as argument of " + setterName);
				trace("Cannot find class " + type.getName() + " as argument of " + setterName + " in class " + refClassName);
			}
		}
		catch (Exception ex) 
		{
			modelErrors.put(refClassName,"Cannot find EReference setter method " + setterName);
			trace("Cannot find EReference setter method " + setterName + " of class " + refClassName);
		}
		
		// EAttributes; get the class for the primitive data type
		if (type instanceof EDataType) try
		{
			Hashtable<String,Method> classGetters = getters.get(ownerClass.getName());
			if (classGetters == null) throw new MapperException("No getters found for class " + refClassName);
			Method getter = classGetters.get(featureName);
			if (getter == null) throw new MapperException("No getter found for feature " + featureName + " of class " + refClassName);
			
			// make the setter argument type the same as the getter result type
			args[0] = getter.getReturnType();
			// getMethod, rather than getDeclaredMethod, also gets inherited methods
			setter = refClass.getMethod(setterName, args);
		}
		catch (Exception ex) 
		{
			modelErrors.put(refClassName,"Exception finding setter method " + setterName);
			trace("Exception finding setter method " 
					+ setterName + " of class " + refClassName + ";" + ex.getMessage());
		}
				
		return setter;
	}
	
	//---------------------------------------------------------------------------------------------------
	//          Convert an Ecore model instance to a FHIR reference implementation instance
	//---------------------------------------------------------------------------------------------------	
	
	/**
	 * convert an instance of the Ecore model into an instance of the FHIR Java reference implementation
	 * @param inputObject the Ecore representation of a bundle of resources
	 * @return the FHIR reference implementation representation of a bundle
	 */
	public AtomFeed getReferenceModelFeed(EObject inputObject) throws MapperException
	{
		
		EClass inputClass = inputObject.eClass();
		// input EObject must be an instance of ECore class 'AtomFeed'
		String topClassName = inputClass.getName();
		if (!topClassName.equals("AtomFeed"))
			throw new MapperException("top Ecore model object is not of class 'AtomFeed'");
		
		// the package of the input object must be the same as the package read in the constructor
		EPackage instancePackage = (EPackage)inputObject.eClass().getEPackage().eContainer();
		compareEcoreModels(instancePackage,eCoreModel);
		// eCoreModel = instancePackage;
		/*
		if (!instancePackage.equals(eCoreModel))
			throw new MapperException("Ecore instance is not an instance of the model given to the Ecore to reference implementation bridge");
		*/
		AtomFeed feed = new AtomFeed();

		String authorName = (String)inputObject.eGet(inputClass.getEStructuralFeature("authorName"));
		if (authorName != null) feed.setAuthorName(authorName);

		String authorUri = (String)inputObject.eGet(inputClass.getEStructuralFeature("authorUri"));
		if (authorUri != null) feed.setAuthorUri(authorUri);

		String id = (String)inputObject.eGet(inputClass.getEStructuralFeature("id"));
		if (id != null) feed.setId(id);

		String title = (String)inputObject.eGet(inputClass.getEStructuralFeature("title"));
		if (title != null) feed.setTitle(title);
		
		// the time the AtomFeed was updated is now - ignore any value from the EObject
		feed.setUpdated(new DateAndTime(Calendar.getInstance()));
		//feed.setUpdated(Calendar.getInstance());
		
		// follow EReferences of the Ecore AtomFeed object to resources
		for (Iterator<EStructuralFeature> it = inputObject.eClass().getEStructuralFeatures().iterator(); it.hasNext();)
		{
			EStructuralFeature feat = it.next();
						
			// resources of the allowed types
			if (feat instanceof EReference) try
			{
				Object value = inputObject.eGet(feat);
				// all EReferences from AtomFeed have max multiplicity unbounded, so should deliver lists
				if ((value != null) && (value instanceof List<?>))
				{
					List<?> vList = (List<?>) value;
					for (Iterator<?> ir = vList.iterator(); ir.hasNext();)
					{
						Object next = ir.next();
						if (next instanceof EObject)
						{
							EObject resource = (EObject)next;
							// this starts the recursion down into the resource structure
							Resource refResource  = makeReferenceModelResource(resource);
							EStructuralFeature id_feat = resource.eClass().getEStructuralFeature("fhir_id");
							if (id_feat == null) throw new MapperException("Resource " + resource.eClass().getName() + "  has not any 'fhir_id' feature for the FHIR logical id");
							String resourceId = (String)resource.eGet(id_feat);
							if (resourceId == null)throw new MapperException("Resource " + resource.eClass().getName() + "  has no FHIR logical id");
							readEcoreResourceToId.put(resource, resourceId);
							// title of the AtomFeed entry is the resource type, followed by its id 
							String entryTitle = resource.eClass().getName() + ": " + resourceId;
							addResource(feed, refResource, entryTitle, resourceId);
						}
					}
				}
				else if (!(value instanceof List<?>)) {throw new MapperException("Value of feature " + feat.getName() + " is not a list.");}
			}
			catch (Exception ex) {ex.printStackTrace(); throw new MapperException("failed to add resource " + feat.getName());}
		}

		return feed;
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 * @throws MapperException
	 */
	public Resource makeReferenceModelResource(EObject resource) throws MapperException
	{
		return (Resource)makeReferenceModelObject(resource, null);
	}

	
	/**
	 * recursive descent down the ECore resource object structure, making the reference model object structure
	 * @param eCoreObject
	 * @return
	 * @throws MapperException
	 */
	private Object makeReferenceModelObject(EObject eCoreObject, Object resourceObject) throws MapperException
	{
		Object result = null;

		EClass eCoreClass = eCoreObject.eClass();
		String eCoreClassName = eCoreClass.getName();
		
		Hashtable<String,Method> settersOfClass = setters.get(eCoreClassName);
		Hashtable<String,Method> gettersOfClass = getters.get(eCoreClassName);

		String objectType = ModelUtil.getMIFAnnotation(eCoreClass, "type");
		if (objectType == null) throw new MapperException("Ecore object of class " + eCoreClassName + " has no reference model type");
		Class<?> referenceModelClass = refModelClasses.get(eCoreClassName);
		if (referenceModelClass == null) throw new MapperException("Reference model class " + eCoreClassName + " not found");
		
		 try
		{
			 if (objectType.equals("Resource"))
			 {
				 result =  ResourceFactory.createResource(eCoreClassName);
			 }
			 else if (objectType.equals("Component"))
			 {
				 /* make the component; there is no factory in the reference implementation to do this. 
				  * Component classes, being inner classes, have a constructor with one parameter which 
				  * is the instance of the outer class.
				  * They may also have other constructors with more parameters */
				 if (resourceObject == null) 
					 throw new MapperException("Component class " + eCoreClassName + "has no containing resource");
				 Class<?>[] constructorArgtypes = new Class[1];
				 constructorArgtypes[0] = resourceObject.getClass();
				 // trace("predicted param class: " + constructorArgtypes[0].getName());
				 Object[] constructorArgs = new Object[1];
				 constructorArgs[0] = resourceObject;
				 Constructor<?>[] constructors = referenceModelClass.getConstructors();
				 int foundConstructors = 0;
				 // look for the constructor with only one parameter, of the correct class
				 for (int c = 0; c < constructors.length;c++)
				 {
					 Constructor<?> cons = constructors[c];
					 Class<?>[] paramTypes = cons.getParameterTypes();
					 showParamTypes(paramTypes);
					 if (paramTypes.length == 0)
					 {
						 foundConstructors++;
						 constructorArgs = new Object[0];
						 result = cons.newInstance(constructorArgs);
					 }
					 else if (paramTypes.length == 1)
					 {
						 String actualParamType = resourceObject.getClass().getName();
						 String expectedParamType = paramTypes[0].getName();
						 if (actualParamType.equals(expectedParamType))
						 {
							 foundConstructors++;
							 result = cons.newInstance(constructorArgs);
						 }
					 }
				 }
				 if (foundConstructors != 1) throw new MapperException(foundConstructors + " constructors with one parameter for class " + referenceModelClass.getName());
			 }
			 else if (objectType.equals("ComplexDataType"))
			 {
				 result =  ResourceFactory.createType(eCoreClassName);
			 }
			 else if (objectType.equals("PrimitiveDataType"))
			 {
				 result = makePrimitiveTypeValue(eCoreObject,eCoreClassName);
			 }
			 else 
			 {
				 trace("Unrecognised type of ECore model object:" + objectType);
			 }
				
			 // cast the reference model object to the correct class (is this necessary?)
			 result = referenceModelClass.cast(result);
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			instanceErrors.put(eCoreClassName, "failed to make reference model object");
			trace("failed to make reference model object of class " + eCoreClassName + ": " + ex.getMessage());
		}
		
		
		// primitive data type objects have one attribute 'value' which has already been dealt with by makePrimitiveTypeValue
		 if ((result != null)  && (!objectType.equals("PrimitiveDataType")))
			for (Iterator<EStructuralFeature> it = eCoreObject.eClass().getEStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature feat = it.next();
			String featureName = feat.getName();
			Object value = eCoreObject.eGet(feat);
						
			if (value != null)
			{
				// EAttributes of resources, components and complex types are all marked with a primitive type, and have a setter method
				if (feat instanceof EAttribute)
				{
					String primitiveType = ModelUtil.getMIFAnnotation(feat,"PrimitiveType");
					if (primitiveType == null) throw new MapperException("Attribute '" + featureName + "' of class " + eCoreClassName + " has no primitive type ");
					Object refValue = PrimitiveTypes.typeValue(value, primitiveType);					
					applySetterMethod(settersOfClass, feat, eCoreObject.eClass(),result, eCoreClassName,resourceObject, refValue);
				}

				/* EReferences with max cardinality 1 deliver a value and have a setter method (unless they are a resource reference); 
				those with max cardinality > 1 deliver a list, and do not have a setter */
				else if (feat instanceof EReference)
				{
					if (feat.getUpperBound() == 1)
					{
						EObject target = (EObject)value;
						//recursive step
						if (resourceObject == null) resourceObject = result;
						Object refValue = makeReferenceModelObject(target,resourceObject);
						if (refValue != null) applySetterMethod(settersOfClass, feat, eCoreObject.eClass(),result, eCoreClassName,resourceObject, refValue);														
					}
					else if (feat.getUpperBound() == -1)
					{
						if (value instanceof List)
						{
							List<?> lValue = (List<?>)value;
							for (Iterator<?> iv = lValue.iterator(); iv.hasNext();)
							{
								Object next = iv.next();
								EObject target = (EObject)next;
								// recursive step
								if (resourceObject == null) resourceObject = result;
								Object refValue = makeReferenceModelObject(target,resourceObject);
								if (refValue != null) addToGetterResult(gettersOfClass,featureName,result,eCoreClassName,refValue);
							}
						}
						else throw new MapperException("Value of feature " + featureName + " of class " + eCoreClassName + " is not a list");
					}
				}
			}
		}		
		return result;
	}
	

	/**
	 * show the parameter types of a constructor
	 * @param paramTypes
	 */
	private void showParamTypes(Class<?>[] paramTypes)
	{
		String types = "Parameter types ";
		for (int p = 0; p < paramTypes.length;p++) types = types + paramTypes[p].getName() + " ";
		message(types);
	}
	
	/**
	 * 
	 * @param setter
	 * @param featureName
	 * @param target
	 * @param targetClassName
	 * @param value
	 * @throws MapperException
	 */
	private void applySetterMethod(Hashtable<String,Method> settersOfClass, 
			EStructuralFeature feat, 
			EClass theClass,
			Object target, 
			String targetClassName, 
			Object resourceObject,
			Object value)
	{
		String featureName = feat.getName();
		try
		{
			if (settersOfClass == null) {throw new MapperException("No setter methods for class " + targetClassName);}
			Method setter = settersOfClass.get(featureName);
			
			if (setter != null)
			{ 
				Object setValue = value;
				String binding = ModelUtil.getMIFAnnotation(feat, "Binding");
				String primitiveType = ModelUtil.getMIFAnnotation(feat, "PrimitiveType");
				// ignore bindings except for EAttributes of primitive type 'code'
				if (binding != null) trace("Binding " + binding + " of feature " + feat.getName() + " of class " + theClass.getName() + " with primitive type " + primitiveType);
				if ((binding != null) && (primitiveType != null) && (primitiveType.equals("code")))
				{			
					// boundValue returns null for an invalid code value
					trace("getting bound value");
					setValue = boundValue(value,binding,feat,theClass,target,resourceObject);
				}
				
				if (setValue != null)
				{
					// setter methods have one parameter
					Class<?> setterParam = setter.getParameterTypes()[0];
					// cast the value to that class
					Object[] params = new Object[1];
					params[0] = setterParam.cast(setValue);
					// apply the setter method
					setter.invoke(target, params);
				}
			}
			else 
			{
				instanceErrors.put(targetClassName, "No setter method for feature " + featureName);
				trace("No setter method for feature " + featureName + " of class " + targetClassName);
			}
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			instanceErrors.put(targetClassName, "Failed to apply setter method " + featureName);
			trace("Failed to apply setter method " + featureName  + " of class " + targetClassName + "; ");
		}		
	}
	
	/**
	 * 
	 * @param value
	 * @param binding
	 * @param feat
	 * @param target
	 * @param resourceObject
	 * @return
	 * @throws MapperException
	 */
	private Object boundValue(Object value,String binding, EStructuralFeature feat,EClass theClass, Object target, Object resourceObject) throws Exception
	{
		Object setValue = null;
		// if there is no binding, return this value
		if (binding == null) return value;
		else if (binding.equals("ResourceType"))
		{
			return value;
		}
		else if ((!binding.equals("")) && (feat instanceof EAttribute))
		{
			if (!(value instanceof Code)) throw new MapperException("Bound value is not a code");
			// get the binding factory class
			Class<?> bindingFactory = bindingFactories.get(binding);
			if (bindingFactory == null) throw new MapperException("Cannot find binding factory class " + binding);
			// message("Binding factory class: " + bindingFactory.getName());
			
			/* make an instance of the binding factory class; 
			 * if it is a static class, the constructor has no parameters,
			 * or if it is not, the constructor has one argument, the enclosing resource or complex data type object
			 */
			Constructor<?> binder = bindingFactory.getConstructors()[0]; //assume there is only one constructor
			Class<?>[] paramTypes = binder.getParameterTypes();
			int nTypes = paramTypes.length;
			Object[] constructorArgs = new Object[nTypes];
			if (nTypes == 1)// when the binding factory class is not static (does this ever occur?)
			{
				constructorArgs[0] = target;
				// if the target object is of a component class, use the containing resource in the constructor
				if ((resourceObject != null) && (target.getClass().getName().contains("$"))) {constructorArgs[0] = resourceObject;}
			}
			Object binderInstance = binder.newInstance(constructorArgs);
			
			// use the binder instance to make an Enum
			Class<?>[] argtypes = new Class[1];
			argtypes[0] = Class.forName("java.lang.String");
			Object[] args = new Object[1];
			args[0] = ((Code)value).getValue();
			Method codeSetter = null;
			try {codeSetter = bindingFactory.getDeclaredMethod("fromCode", argtypes);}
			catch (Exception ex) {throw new MapperException("Binding factory for '" + binding + "' has no 'fromCode' method");}

			// exception will be thrown by an invalid code value
			Object res = null;
			try 
			{
				res = codeSetter.invoke(binderInstance, args);
			}
			catch (Exception ex)
			{
				res = null;
				setValue = null;
				instanceErrors.put(theClass.getName(), "Invalid code value '" + (String)args[0] + "' for binding '" + binding + "'");
				message ("Invalid code value '" + (String)args[0] + "' for binding '" + binding + "'");
			}
			
			// convert the instance to an Enumeration, depending on the binding
			if (res != null) 
			{
				setValue = makeEnumerationGeneric(binding, res, feat, theClass);
			}
		}
		return setValue; // null if the code value was invalid
	}
	
	
	/**
	 * 
	 * @param binding
	 * @param res
	 * @param feat
	 * @param theClass
	 * @return
	 * @throws MapperException
	 */
	private org.hl7.fhir.instance.model.Enumeration<?> makeEnumerationGeneric(String binding, Object res, EStructuralFeature feat, EClass theClass) throws MapperException
	{
		// message("\nMaking Generic Enumeration for binding " + binding);
		org.hl7.fhir.instance.model.Enumeration<?> instance = null;
		
		try
		{
			Class<?> enumerationClass = Class.forName("org.hl7.fhir.instance.model.Enumeration");
			Constructor<?>[] cons = enumerationClass.getConstructors();
			// find a constructor with one argument
			for (int c = 0; c < cons.length; c++)
			{
				Constructor<?> con = cons[c];
				Class<?>[] argClasses = con.getParameterTypes();
				if (argClasses.length == 1)
				{
					Object[] args = new Object[1];
					args[0] = (Enum<?>)res;
					instance = (org.hl7.fhir.instance.model.Enumeration<?>) con.newInstance(args);
				}
			}
		}
		catch (Exception ex) {ex.printStackTrace();}
		return instance;
	}

	
	/**
	 * 
	 * @param gettersOfClass
	 * @param featureName
	 * @param target
	 * @param targetClassName
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void addToGetterResult(Hashtable<String,Method> gettersOfClass, String featureName,Object target, String targetClassName, Object value)
	throws MapperException
	{
		try
		{
			if (gettersOfClass == null) {throw new MapperException("No getter methods for class " + targetClassName);}
			Method getter = gettersOfClass.get(featureName);
			
			if (getter != null)
			{
				// getter methods have no parameters
				Object[] params = new Object[0];
				// apply the getter method which gives a list result, and add to it
				((List<Object>)getter.invoke(target, params)).add(value);
			}
			else trace("No getter method for feature " + featureName + " of class " + targetClassName);

		}
		catch (Exception ex) {throw new MapperException("Failed to add to result of getter method " + featureName 
				+ " of class " + targetClassName + "; " + ex.getMessage());}		
		
	}

	
	 /**
	  *  all Ecore classes representing primitive types should have an EAnnotation PrimitiveType, defining their primitive type,
	  * and a single attribute 'value' of the appropriate EDataType
	  * */
	private Type makePrimitiveTypeValue(EObject eCorePrimitiveTypeObject, String parentEcoreClassName) throws MapperException
	{
		EClass eCoreClass = eCorePrimitiveTypeObject.eClass();
		String ecoreClassName = eCoreClass.getName();
		String primitiveType = ModelUtil.getMIFAnnotation(eCoreClass,"PrimitiveType");
		if (primitiveType == null) throw new MapperException("Object of class " + ecoreClassName + " has no primitive type ");
		
		EStructuralFeature valueAtt = eCoreClass.getEStructuralFeature("value");
		if (valueAtt == null) throw new MapperException("Class " + ecoreClassName + " has no value attribute ");
		
		Object value = eCorePrimitiveTypeObject.eGet(valueAtt);	
		if (value == null) {message("Null value attribute of class " + ecoreClassName); return null;}
		else return PrimitiveTypes.typeValue(value, primitiveType);
	}
	
	
	
	//---------------------------------------------------------------------------------------------------
	//               Convert a FHIR reference implementation instance to an ECore model instance 
	//---------------------------------------------------------------------------------------------------
	
	
	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param feed
	 * @return
	 * @throws MapperException
	 */
	public EObject getEcoreModelInstance(AtomFeed feed) throws MapperException
	{
		EClass atomFeedClass = (EClass)fhirPackages[FEED].getEClassifier("AtomFeed");
		EObject atomFeedObject = createModelObject("feed.AtomFeed");
		Hashtable<String,Method> atomFeedGetters = getters.get("AtomFeed");
		
		// some string attributes of the AtomFeed
		for (Iterator<EStructuralFeature> it = atomFeedClass.getEStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature feat = it.next();
			String featName = feat.getName();			
			if (feat instanceof EAttribute) try
			{
				EAttribute att = (EAttribute)feat;
				Method getter = atomFeedGetters.get(featName);
				if (getter == null) throw new MapperException("Missing AtomFeed getter: " + featName);
				Class<?> resultType = getter.getReturnType();
				Object[] args = new Object[0];
				if (resultType.getName().equals("String_"))
				{
					String_ val = (String_)getter.invoke(feed, args);
					if (val != null) atomFeedObject.eSet(att, val.getValue());
				}
				else if (resultType.getName().equals("java.lang.String"))
				{
					String val = (String)getter.invoke(feed, args);
					trace("AtomFeed attribute: " + featName + "; value: " + val + "; changeable: " + att.isChangeable());
					if (val != null) atomFeedObject.eSet(att, val);
				}
				// else message("Non-string AtomFeed attribute: " + featName + " of class " + resultType.getName());
			}
			catch (Exception ex) {ex.printStackTrace();throw new MapperException("Failed to find AtomFeed attribute " + featName);}
		}
		
		// resources bundled in the feed
		for (Iterator<AtomEntry<? extends Resource>> it = feed.getEntryList().iterator();it.hasNext();)
		{
			AtomEntry entry = it.next();
			String id = entry.getId();
			Resource resource = entry.getResource();
			addResourceToECoreModel(resource,atomFeedClass,atomFeedObject,id);			
			addContainedResourcesToECoreModel(resource,atomFeedClass,atomFeedObject);
		}

		return atomFeedObject;
	}
	
	/**
	 * 
	 * @param resource
	 * @param atomFeedObject
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	private void addResourceToECoreModel(Resource resource,EClass atomFeedClass, EObject atomFeedObject,String id) throws MapperException
	{
		String resourceType = resource.getResourceType().name();
		// EReference name is the resource name with all lower case
		EStructuralFeature feat = atomFeedClass.getEStructuralFeature(resourceType.toLowerCase());
		if (feat != null)
		{
			// all features of the AtomFeed Ecore object should be lists, to hold many resources
			if (atomFeedObject.eGet(feat) instanceof List<?>)
			{
				EObject resourceObject = makeEcoreObject(resource, (EReference)feat,"");
				// set the FHIR id as a special attribute of the resource object
				EStructuralFeature id_feat = resourceObject.eClass().getEStructuralFeature("fhir_id");
				if (id_feat == null) throw new MapperException("Resource has no attribute 'fhir_id'");
				resourceObject.eSet(id_feat, id);
				
				// note the object with its id, to resolve resource references
				Vector<EObject> objectsWithId = readIdToCreatedEcoreResource.get(id);
				if (objectsWithId == null) objectsWithId = new Vector<EObject>();
				objectsWithId.add(resourceObject);
				readIdToCreatedEcoreResource.put(id, objectsWithId);
				
				((List<EObject>)atomFeedObject.eGet(feat)).add(resourceObject);
			}
			else throw new MapperException("Resource EReference of atomfeed should have upper bound -1");
		}
		else trace("Ecore model does not have resource " + resourceType);
	}
	
	/**
	 * if any resource has contained resources in the reference implementation, 
	 * de-contain them and convert them into child object of the AtomFeed bundle
	 * in the Ecore model instance
	 * @param resource
	 * @param atomFeedObject
	 * @throws MapperException
	 */
	private void addContainedResourcesToECoreModel(Resource resource,EClass atomFeedClass, EObject atomFeedObject) throws MapperException
	{
		for (Iterator<Resource> it = resource.getContained().iterator();it.hasNext();)
		{
			Resource next = it.next();
			String id = next.getXmlId();
			addResourceToECoreModel(next,atomFeedClass,atomFeedObject,id);
			addContainedResourcesToECoreModel(next, atomFeedClass, atomFeedObject);
		}
	}

	
	

	
	/**
	 * 
	 * @param refModelObject
	 * @param parentFeature
	 * @return
	 * @throws MapperException
	 */
	@SuppressWarnings("unchecked")
	private EObject makeEcoreObject(Object refModelObject, EReference parentFeature,String path) throws MapperException
	{
		String newPath = path + parentFeature.getName() + ".";
		EClass eCoreClass = (EClass)parentFeature.getEType();
		String className = eCoreClass.getName();
		String qualifiedClassName = eCoreClass.getEPackage().getName() + "." + className;
		EObject result = createModelObject(qualifiedClassName);
		Class<?> refModelClass = refModelClasses.get(className);
		if (refModelClass == null) throw new MapperException("No reference model class " + className);
		refModelObject = refModelClass.cast(refModelObject);
		if (refModelObject == null) throw new MapperException("Cannot cast reference model object to class " + className);
		
		// iterate over all attributes and associations found in the reference model
		Hashtable<String,Method> classGetters = getters.get(className);		
		if (classGetters != null) 
		{
			for (Enumeration<String> en = classGetters.keys();en.hasMoreElements();) 
			{
				// find the getter method and result type in the reference model
				String featName = en.nextElement();
				Method getter = classGetters.get(featName);
				if (getter == null) throw new MapperException("Cannot find getter method " + featName + " of class " + eCoreClass.getName());
				// this only works if the getter does not return a list
				Class<?> resultType = getter.getReturnType();

				// find the corresponding feature and result type in the Ecore model
				EStructuralFeature feat = eCoreClass.getEStructuralFeature(featName);
				if (feat == null) throw new MapperException("Cannot find Ecore model feature " + featName + " of class " + eCoreClass.getName() + " at path " + path);
				EClassifier featureType = feat.getEType();
				if (featureType == null) throw new MapperException("Null type of Ecore model feature " + featName + " of class " + eCoreClass.getName() + " at path " + path);
				if (feat instanceof EReference) 
				{
					//objectType = ModelUtil.getMIFAnnotation(featureType, "type");
					resultType = refModelClasses.get(featureType.getName()); // works for all EReferences
				}
				if (resultType == null) trace("Null result type for feature " + featName + " of class " + eCoreClass.getName());
				
				// get the result value
				Object valObj = null;
				Object[] args = new Object[0];
				Class<?>[] argTypes = new Class[0];
				try 
				{
					// use the getter for the (resource or component or complex data type) class to get an object of a primitive data type object
					valObj = getter.invoke(refModelObject, args);
				}
				catch (Exception ex) {throw new MapperException("Method invocation failure: " + ex.getMessage());}
				
				if ((valObj != null) && (resultType != null))
				{
					// multiple values
					if (valObj instanceof List)
					{
						for (Iterator<?> io = ((List<?>)valObj).iterator();io.hasNext();)
						{
							Object next = io.next();
							next = resultType.cast(next);
							if (next == null) throw new MapperException("Cannot cast multiple value to class " + resultType.getClass().getName());
							// recursive step
							EObject child = makeEcoreObject(next,(EReference)feat,newPath);
							Object featureVal = result.eGet(feat);
							if (featureVal instanceof List<?>) ((List<EObject>)featureVal).add(child);
						}
					}
					// single values
					else
					{
						if (feat instanceof EReference)
						{
							valObj = resultType.cast(valObj);
							if (valObj == null) throw new MapperException("Cannot cast single value to class " + resultType.getClass().getName());
							// recursive step
							EObject child = makeEcoreObject(valObj,(EReference)feat,newPath);
							result.eSet(feat,child);
						}
						else if (feat instanceof EAttribute)
						{
							boolean resultCanBeConverted = true;
							// valObj may be an instance of a Primitive data type class, or a String, or an enumerated code
							Object resultObj = valObj;
				
							// Strings can be passed direct to eSet
							if (resultObj instanceof String) {}
				
							// all primitive data type classes have a method 'getValue'
							else if (PrimitiveTypes.isPrimitiveDataTypeClass(valObj.getClass()))
							{
								try
								{
									Method valueMethod = valObj.getClass().getMethod("getValue", argTypes);
									// find the result of 'getValue' on the primitive data type object
									resultObj = valueMethod.invoke(valObj, args);
								}
								catch (Exception ex) {throw new MapperException("Could not apply getValue method of primitive data type object: " + ex.getMessage());}
							}

							// codes with bindings return FHIR Enumerations which need to be converted to String codes
							else if (valObj instanceof org.hl7.fhir.instance.model.Enumeration)
							{
								String binding = ModelUtil.getMIFAnnotation(feat, "Binding");
								if ((binding != null) && (!binding.equals(""))) resultObj = getEnumeratedCode((org.hl7.fhir.instance.model.Enumeration<?>)valObj,binding);
								else 
								{
									trace("No binding found for enumeration value of feature " + feat.getName());
									resultCanBeConverted = false;
								}
							}
							
							else throw  new MapperException("Failed to recognise feature type "  + valObj.getClass().getName() + " of EAttribute " + feat.getName());
							
							if (resultCanBeConverted) PrimitiveTypes.setEcoreFeature(result,(EAttribute)feat,resultObj);
						}
						
					}
				}
			}
		}
		else trace("No getter methods for class " + className);
		
		return result;
	}
	
	/**
	 * If a feature value is a code with bindings,
	 * convert the Enumeration returned by the 
	 * @param feat
	 * @param resultObj
	 * @return
	 */
	private String getEnumeratedCode(org.hl7.fhir.instance.model.Enumeration<?> valObj,String binding) throws MapperException
	{
		String result = "";
		
		// get the binding class and conversion method
		Class<?> bindingClass = bindingClasses.get(binding);
		if (bindingClass == null) throw new MapperException("Cannot find binding class for '" + binding + "'");
		Class<?>[] argtypes = new Class[0];
		Object[] args = new Object[0];
		Method codeGetter = null;
		try {codeGetter = bindingClass.getDeclaredMethod("toCode", argtypes);}
		catch (Exception ex) {throw new MapperException("Binding class for '" + binding + "' has no 'toCode' method");}
		
		Object next = valObj.getValue();
		String elementType = next.getClass().getName();
		next = bindingClass.cast(next);
		if (next == null) {throw new MapperException("Enumeration delivers type '" +  elementType + "' for binding '" + binding + "'");}
		try {result = (String)codeGetter.invoke(next, args);}
		catch (Exception ex) {throw new MapperException("Failed to invoke 'toCode' method for binding '" + binding + "'");}			

		return result;
	}
	
	
	
	//---------------------------------------------------------------------------------------------------
	//                                    Utilities
	//---------------------------------------------------------------------------------------------------
	
	
	/** create a model object with the right class (without using a generated package)*/
	private EObject createModelObject(String qualifiedClassName) throws MapperException
	 {
		StringTokenizer st = new StringTokenizer(qualifiedClassName,".");
		if (st.countTokens() != 2) throw new MapperException("No package name");
		String packageName = st.nextToken();
		String className = st.nextToken();
		EPackage thePackage = null;
		for (int p = 0; p< packageNames.length; p++) 
			if (packageNames[p].equals(packageName)) thePackage = fhirPackages[p];
		if (thePackage == null) throw new MapperException("Package " + packageName + " not found");

		EClass theClass = (EClass)thePackage.getEClassifier(className);
		if (theClass != null) 
		{			
			if (!theClass.isAbstract()) return thePackage.getEFactoryInstance().create(theClass);
			else 
			{
				System.out.println("Abstract class " + className);
				return null;
			} 
		}
		else {System.out.println("Null EObject of class " + className);} 
		return null;
	 }
	
	/**
	 * a crude comparison of two Ecore models to check they are the same model - 
	 * even if they are different Java objects
	 * @param model1
	 * @param model2
	 * @throws MapperException if any difference is found
	 */
	private void compareEcoreModels(EPackage model1,EPackage model2) throws MapperException
	{
		if (!model1.getName().equals(model2.getName())) 
			throw new MapperException("Package names " + model1.getName() + " and " + model2.getName() + " do not match");
		for (Iterator<EClassifier> it = model1.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier c1 = it.next();
			EClassifier c2 = model2.getEClassifier(c1.getName());
			if (c2 == null) throw new MapperException("Model 2 does not have EClassifier " + c1.getName());
			if ((c1 instanceof EClass) && (c2 instanceof EClass))
			{
				EClass cc1 = (EClass)c1;
				EClass cc2 = (EClass)c2;
				for (Iterator<EStructuralFeature> iu = cc1.getEStructuralFeatures().iterator();iu.hasNext();)
				{
					EStructuralFeature f1 = iu.next();
					EStructuralFeature f2 = cc2.getEStructuralFeature(f1.getName());
					if (f2 == null) throw new MapperException("Class " + cc2.getName() + " has missing feature " + f1.getName() + " in model 2");
				}
				if (cc1.getEStructuralFeatures().size() != cc1.getEStructuralFeatures().size()) 
					throw new MapperException("Class " + cc1.getName() + " has non-matching numbers of features");
			}
		}
		if (model1.getEClassifiers().size() != model2.getEClassifiers().size()) throw new MapperException("Numbers of EClassifiers do not match");
	}
	
	/**
	 * diagnostic
	 * @param theClass
	 */
	private void writeAllMethods(Class<?> theClass)
	{
		message("\nAll constructors of class " + theClass.getName());
		Constructor<?>[] allConstructors = theClass.getConstructors();
		for (int i = 0; i < allConstructors.length; i++)
		{
			message("number of parameters: " + allConstructors[i].getParameterTypes().length);
			for (int j = 0; j < allConstructors[i].getParameterTypes().length; j++)
			{
				Class<?> param = allConstructors[i].getParameterTypes()[j];
				message("Parameter class: " + param.getName());
			}
		}

		message("\nAll declared methods of class " + theClass.getName());
		Method[] allMethods = theClass.getDeclaredMethods();
		for (int i = 0; i < allMethods.length; i++)
		{
			message(allMethods[i].getName());
		}

		message("\nAll member methods of class " + theClass.getName());
		Method[] allMemberMethods = theClass.getMethods();
		for (int i = 0; i < allMemberMethods.length; i++)
		{
			message(allMemberMethods[i].getName());
		}
	}


	
	/**
	 * add any Resource to an atom feed
	 * @param feed
	 * @param r
	 * @param title
	 * @param id
	 * @return
	 */
	private String addResource(AtomFeed feed, Resource r, String title, String id) {
		AtomEntry e = new AtomEntry();
		e.setUpdated(new DateAndTime(Calendar.getInstance()));
		//e.setUpdated(Calendar.getInstance());
		e.setResource(r);
		e.setTitle(title);
		e.setId(id);
		// e.setCategory(r.getResourceType().toString()); // removed as method no longer recognised
		feed.getEntryList().add(e);
		return id;
	}
	
	private void message(String s) {System.out.println(s);}
	
	private void trace(String s) {if (tracing) message(s);}

	

}
