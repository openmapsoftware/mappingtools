package com.openMap1.mapper.reader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.BasicEList;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.openMap1.mapper.converters.LRAConverter;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.PropertyConversionException;
import com.openMap1.mapper.core.RunIssue;
import com.openMap1.mapper.core.notRepresentedException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.Timer;


/**
 * The interface implemented by this class has one method, to make a resource
 * containing an instance of an EMF model, using an
 * XML document which represents an instance of the model
 * and a set of mappings which define how the XML instance 
 * represents the instance of the model
 * 
 * @author robert worden
 *
 */
public abstract class EMFInstanceFactoryImpl implements  EMFInstanceFactory{
	
	// if true, trace the steps of creating the EMF instance
	private boolean tracing = false;
	
	// if true, write out the model objects stored before traversing non-containment relations
	private boolean writeModelObjects = false;
	
	// if true, write out timing statistics for making the instance
	private boolean timing = false;

	protected Timer timer = null;

	private XOReader xor;
	
	/**
	 * @return The Ecore model for the  Factory-generated instance.
	 * Compared to the input Ecore model, this model may have an extra class 'Container' (or 'Container_N')
	 * which has a containment association to every class not otherwise contained.
	 */
	public EPackage modelForInstance() {return classModel;}
	protected EPackage classModel;
	
	protected EFactory factory;
	
	/**
	 * @return true if the Factory-generated instance has an extra class 'Container' (or 'Container_N')
	 * which has a containment association to every class not otherwise contained.
	 */
	public boolean hasAddedContainerClass() {return hasAddedContainerClass;}
	private boolean hasAddedContainerClass = false;
	
	/* for each qualified class name, store a Hashtable from objectToken key to represented EObject */
	private Hashtable<String,Hashtable<Object,EObject>> savedModelObjects 
	= new Hashtable<String,Hashtable<Object,EObject>>();
	
	/* record cases where traversal of a non-containment link reaches an object 
	 * which has not been found by traversal of containment links. 
	 * Outer key = class name; inner key = role traversed; Integer = number of occurrences. */
	private Hashtable<String,Hashtable<String,Integer>> failedLookups;
	
	/** if an extra container class gets added to the class model, this is its name */
	private String containerClassName = "";
	
	protected String NSPrefix = "generic";

	/**
	 * Set the namespace prefix for the model
	 * @param NSPrefix
	 */	
	public void setNsPrefix(String NSPrefix) {this.NSPrefix = NSPrefix;}
	
	protected String NSUri = "http://www.com.openMap1.mapper/test";

	/**
	 * Set the namespace URI for the model
	 * @param NSUri
	 */	
	public void setNsUri(String NSUri) {this.NSUri = NSUri;}
	
	
	public static URI DO_NOT_SAVE_URI() {return URI.createURI("noFile.ecore");}


	//------------------------------------------------------------------------------------
	//                        code dependent on the specific EMF model
	//-----------------------------------------------------------------------------------
	
	/** use the generated factory to create a model object with the right class name */
	abstract public EObject createModelObject(String qualifiedClassName) throws MapperException;
		
	//------------------------------------------------------------------------------------
	//                        
	//-----------------------------------------------------------------------------------
	
	/** use the generated factory to convert a property from a String to the right type */
	public static Object convertPropertyType(EAttribute att, String val) throws MapperException
	{
		String attType = att.getEAttributeType().getName();
		if (attType.equals("EString")) return val;

		else if (attType.equals("EInt")) 
		{
			Integer i = new Integer(0);
			try{i = new Integer(val);}
			catch (Exception ex)
			{System.out.println("Cannot convert value '" + val + "' to Integer; stored value 0");}
			return i;
		}
		
		else if (attType.equals("EBoolean")) 
		{
			Boolean b = new Boolean(false);
			try{b = new Boolean(val);}
			catch (Exception ex)
			{System.out.println("Cannot convert value '" + val + "' to Boolean; stored value 'false'");}
			return b;
		}
		
		else if (attType.equals("EFloat")) 
		{
			Float f = new Float(0.0);
			try{f = new Float(val);}
			catch (Exception ex)
			{System.out.println("Cannot convert value '" + val + "' to Float; stored value '0.0'");}
			return f;
		}

		else throw new MapperException("Attribute '" + att.getName() + "' type not recognised: " + attType);
	}
	

	//----------------------------------------------------------------------------------------
	//	                         API called from the Menu command
	//----------------------------------------------------------------------------------------
	
	public void giveTimer(Timer timer)
	{
		this.timer = timer;
	}
	
	/**
	 * @param xor the reader which uses mappings to extract EMF model instance information 
	 * @param modelInstanceURI the URI at which the new model instance resource is to be stored;
	 * cannot be null as the extension (e.g .ecore) is used to define what kind of model it is.
	 * if you do not wish to save the resource, use a URI like 'noFile.ecore' 
	 * @param topobjectToken the objectToken of the instance which is to be the root of the Ecore tree
	 */
	public Resource createModelInstance(XOReader xor, URI modelInstanceURI, objectToken topobjectToken)
		throws MapperException		
	{
		
		
		// start a timer and give a copy to the XOReader
		if (timer == null) timer = new Timer("");
		timer.start(Timer.MAKE_EMF_INSTANCE);
		xor.giveTimer(timer, false);

		failedLookups = new Hashtable<String,Hashtable<String,Integer>>();
		
		// create the resource that is to be filled with the model instance
		ResourceSet resourceSet = new ResourceSetImpl();
		// register the factory
		if (modelInstanceURI != null)
		{
			String extension = modelInstanceURI.fileExtension();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
				put(extension, new XMIResourceFactoryImpl());
			
		}
		else throw new MapperException("Null URI not allowed; to not save the Resource, use e.g. NoFile.ecore");
		Resource modelResource = resourceSet.createResource(modelInstanceURI);
		if (modelResource == null) System.out.println("Null model resource");
		
		// restart the list of all runtime issues
		allRunIssues = new Hashtable<String,RunIssue>();

		// find the Ecore model that the new resource is to hold an instance of
		this.xor = xor;
		if (xor.classModel() == null)
		   {throw new MapperException("Cannot find class model");}
		classModel = xor.classModel(); 
				
		// find or create the EClass which is the container of all the others
		EClass topClass = ModelUtil.getNamedClass(classModel, topobjectToken.className());
		String topClassName = ModelUtil.getQualifiedClassName(topClass);
		EObject rootObject = createModelObject(topClassName);
		trace("Created top object of class '" + topClassName + "'");
		
		
		// recursively fill out the attributes and containment relations of the root object
		// System.out.println("Filling the model");
		timer.start(Timer.FILL_OBJECT);
		fillObject(rootObject,topobjectToken);
		timer.stop(Timer.FILL_OBJECT);
	
		
		// diagnostic look at the modelObjects stored
		if (writeModelObjects) writeModelObjects(rootObject);
		
		// recursively do all non-containment links for this object and the objects it contains
		// System.out.println("Non containments");
		timer.start(Timer.EMF_NON_CONTAINMENT);
		doNonContainmentLinks(rootObject,topobjectToken);
		timer.stop(Timer.EMF_NON_CONTAINMENT);
				
		// add the root object to the resource
		modelResource.getContents().add(rootObject);
		
		// one-off test of Ecore models
		// if (writeModelObjects) eCoreTest(rootObject);
		 				
		// save the resource, unless 'noFile' has been used
		if ((modelInstanceURI != null) && (!modelInstanceURI.toString().startsWith("noFile")))
			try {modelResource.save(null);}
			catch (IOException ex) 
			   {throw new MapperException("Failed to save EMF model resource: " + ex.getMessage());}
			
		/* note where traversing non-containment links led to objects not previously 
		 * found by traversing containment links */
		writeFailedLookups();
		
		 /* overwrite of dynamic Ecore models, putting a native Ecore model to the same location.
		  * This overcomes some strange glitch in Ecore models */
		 LRAConverter conv = new LRAConverter();
		 if (topClass.getName().equals("EPackage")) 
		 {
			 /* this only does LRA special post-processing,
			  * converting some EReferences to EAttributes,
			  * if the top package name is 'lra' */
			 conv.saveAsEcore(rootObject,modelInstanceURI);
		 }

		// report all timings
		timer.stop(Timer.MAKE_EMF_INSTANCE);
		if (timing) timer.report();

		return modelResource;
	}

	//-------------------------------------------------------------------------------------------------------
	//	    API for translation tests, when there may be an extra container object added at the root
	//-------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param xor the reader which uses mappings to extract EMF model instance information 
	 * @param modelInstanceURI the URI at which the new model instance resource is to be stored;
	 * or null if it is not to be stored anywhere
	 * @param forceContainer: if forceContainer is true, the Factory will act as if
	 * there is a new root class,
	 * (called 'Container' or 'Container_N' to avoid class name clashes) in the EMF model.
	 * This class has a containment relation 'contains' to any class which is not contained
	 * in some other class. This is done so that all object instances can be shown in the EMF instance
	 * with a single root.
	 * <p>
	 * When forceContainer is false, the Factory will only add a new root class 'Container'
	 * if it is necessary - i.e. if there more than one 'root' class, and so there
	 * is no one class in the Ecore model which has
	 * a containment relation (direct or indirect) to all the other classes. 
	 * <p>
	 * In this case, when the Factory does not introduce a new Container class
	 * it may fail and throw a MapperException if there is not 
	 * exactly one instance of the top class of the ECore model represented in the XML.
	 * <p>
	 * Whether forceContainer is true or false, the Factory does not alter the stored EMF model; 
	 * it uses an in-memory modified copy, and makes that publicly accessible.
	 * 
	 */
	public Resource createModelInstanceInTranslationTest(XOReader xor, URI modelInstanceURI, boolean forceContainer)
		throws MapperException
	{
		// start a timer and give a copy to the XOReader
		if (timer == null) timer = new Timer("");
		timer.start(Timer.MAKE_EMF_INSTANCE);
		timer.start(Timer.EMF_PRELIMINARIES);
		xor.giveTimer(timer, false);

		failedLookups = new Hashtable<String,Hashtable<String,Integer>>();

		// create the resource that is to be filled with the model instance
		ResourceSet resourceSet = new ResourceSetImpl();
		// register the factory
		if (modelInstanceURI != null)
		{
			String extension = modelInstanceURI.fileExtension();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
				put(extension, new XMIResourceFactoryImpl());
			
		}
		Resource modelResource = resourceSet.createResource(modelInstanceURI);
		if (modelResource == null) System.out.println("Null model resource");
		
		// restart the list of all runtime issues
		allRunIssues = new Hashtable<String,RunIssue>();

		// find the Ecore model that the new resource is to hold an instance of
		this.xor = xor;
		if (xor.classModel() == null)
		   {throw new MapperException("Cannot find class model");}
		classModel = xor.classModel(); // it would be hard work to make a copy without stripping the classes out
				
		// find all object mappings for objects not nested inside some other mapped object. Key = classSet name
		Vector<ClassSet> previousOuters = xor.outerObjectClassSets();

		// find or add the new single outer class, and make an instance of it
		EClass topClass = addContainerClassIfNecessary(classModel,previousOuters,forceContainer);
		EObject rootObject = createModelObject(ModelUtil.getQualifiedClassName(topClass));
		
		/* get the objectTokens for all the classes that were outer 
		 * before any container class was added. Key = ClassSet name. */
		Hashtable<String,Vector<objectToken>> topobjectTokens = getTopObjects(previousOuters);
		
		/* note the top objects in a lookup table modelObjects, 
		 * later used for non-containment associations */
		recordModelObjects(topobjectTokens);
		timer.stop(Timer.EMF_PRELIMINARIES);
		
		/* fill in the properties of all top instances, and recursively traverse their
		 * containment associations; and if a container class has been added to the class model, 
		 * make each top object contained inside it */ 		
		for (Enumeration<String> en = topobjectTokens.keys();en.hasMoreElements();)
		{
			String classSetName = en.nextElement();
			/* to hold all the objects of this class contained in the Container object 
			 * FIXME - case when two classSets have the same class name; ELists should be merged */
			EList<EObject> contList = new BasicEList<EObject>();

			Vector<objectToken> oReps = topobjectTokens.get(classSetName);
			if (oReps.size() > 0)
			{
				String qualifiedClassName = oReps.get(0).className();
				for (Iterator<objectToken> ir = oReps.iterator(); ir.hasNext();)
				{
					objectToken oRep = ir.next();
					EObject topObject = savedModelObjects.get(qualifiedClassName).get(oRep.objectKey());
					// add this object to a containment list for objects of this class in the top 'Container' object
					contList.add(topObject);
					// recursively fill out the attributes and containment relations of this object
					timer.start(Timer.FILL_OBJECT);
					fillObject(topObject,oRep);
					timer.stop(Timer.FILL_OBJECT);
					// if there is no extra containment class, there should be one real object to be root
					if (!hasAddedContainerClass) rootObject = topObject;
				}
				
				// store the top objects of this class in the correct feature of the container object
				if (hasAddedContainerClass)
				{
					// find the correct containment association to get to an object of this class
					boolean containmentFound = false;
					String className = ModelUtil.getBareClassName(qualifiedClassName);
					for (Iterator<EReference> iRef = topClass.getEAllReferences().iterator();iRef.hasNext();)
					{
						EReference er = iRef.next();
						if (er.getEType().getName().equals(className)) 
						{
							rootObject.eSet(er, contList);
							containmentFound = true;
						}
					}
					if (!containmentFound)
						throw new MapperException("Found no containment relation for objects of class '" 
								+ qualifiedClassName + "'");
				}
				
			}
		}
		
		/* fill in all the non-containment associations, after all model objects have been made. */
		timer.start(Timer.EMF_NON_CONTAINMENT);
		for (Enumeration<String> en = topobjectTokens.keys();en.hasMoreElements();)
		{
			String classSetName = en.nextElement();
			Vector<objectToken> oReps = topobjectTokens.get(classSetName);
			if (oReps.size() > 0)
			{
				String qualifiedClassName = oReps.get(0).className();
				for (Iterator<objectToken> ir = oReps.iterator(); ir.hasNext();)
				{
					objectToken oRep = ir.next();
					EObject topObject = savedModelObjects.get(qualifiedClassName).get(oRep.objectKey());
					// recursively do all non-containment links for this object and the objects it contains
					doNonContainmentLinks(topObject,oRep);
				}				
			}
		}
		timer.stop(Timer.EMF_NON_CONTAINMENT);
		
		// add the root object to the resource
		modelResource.getContents().add(rootObject);
				
		// save the resource
		if (modelInstanceURI != null)
			try {modelResource.save(null);}
			catch (IOException ex) 
			   {throw new MapperException("Failed to save EMF model resource: " + ex.getMessage());}
			
		// clean up afterwards
		removeContainerClassIfNecessary(classModel);
		
		/* note where traversing non-containment links led to objects not previously 
		 * found by traversing containment links */
		writeFailedLookups();

		return modelResource;
	}
	
	/**
	 * diagnostic write of the numbers of stored model objects, by class
	 */
	private void writeModelObjects(EObject rootObject)
	{
		System.out.println("\n\nModel objects found by class");
		for (Enumeration<String> en = savedModelObjects.keys();en.hasMoreElements();)
		{
			String className = en.nextElement();
			Hashtable<Object,EObject> forClass = savedModelObjects.get(className);
			
			// find which EObjects have no container; or if they do, what their parent class is
			int noContainer = 0;
			String parentClassName = "nonexistent";
			for (Enumeration<EObject> eo = forClass.elements(); eo.hasMoreElements();)
			{
				EObject next = eo.nextElement();
				EObject parent = next.eContainer();
				if (parent == null) noContainer++;
				else parentClassName = parent.eClass().getName();
			}
						
			System.out.println(className + ": " + forClass.size() + "; " + noContainer 
					+ " without container; parent class " + parentClassName);
		}
		
		System.out.println("\n\nClass Tree");
		writeTree(rootObject, 0);
	}
	
	/*
	 * write out the tree structure of an EMF model
	 */
	private void writeTree(EObject obj, int level)
	{
		String className = obj.eClass().getName();
		System.out.println(level + ": " + className);
		for (Iterator<EObject> it = obj.eContents().iterator();it.hasNext();)
			writeTree(it.next(),level+1);
	}

	
	/**
	 * @param classModel an Ecore model, headed by an EPackage
	 * @return the EClass which is recommended as top class for Ecore
	 * instances of the model, in an annotation on the top EPackage object;
	 * or null if there is no such annotation
	 */
	public static EClass getRecommendedTopClass(EPackage classModel)
	{
		EClass recClass = null;
		String className = ModelUtil.getEAnnotationDetail(classModel, "topClass");
		if (className != null) recClass = ModelUtil.getNamedClass(classModel, className);
		return recClass;
	}
	
	//-------------------------------------------------------------------------------------
	//                Main recursive methods to fill in the Ecore instance
	//-------------------------------------------------------------------------------------
	
	/**
	 * fill in all the properties of an EObject that you can get from the 
	 * mappings, and fill in its containment associations - 
	 * recursively filling in the contained objects.
	 * @param obj
	 * @param oRep
	 */
	private void fillObject(EObject obj, objectToken oRep) throws MapperException
	{
		String className = obj.eClass().getName();
		String task = "Properties of " + className  + "(" + oRep.cSet().stringForm() + ")";
		trace(task);
		// find values for all mapped properties of the class
		for (Iterator<EAttribute> it = obj.eClass().getEAllAttributes().iterator();it.hasNext();)
		{
			EAttribute att = it.next();
			String attName = att.getName();
			// if the XML represents the value of this property, set it on the object
			// temporarily forgive failure to convert properties
			
			// the next call is very expensive, so just catch the exception silently in stead
			boolean representsProp = true; // xor.representsProperty(oRep, attName);
			if (representsProp) try
			{
				String propVal = xor.getPropertyValue(oRep, attName);
				// assume that "" means nothing more than 'default' which is there already
				if ((propVal != null) && (!propVal.equals("")))
				{
					obj.eSet(att, convertPropertyType(att,propVal));
					trace(attName + ": " + propVal);
				}
			}
			catch (PropertyConversionException ex) 
			{
				int nature = RunIssue.RUN_XSLT_PROPERTY_CONVERSION;
				noteRunIssue(nature, ex.getMessage());
			}
			catch (notRepresentedException ex) 
			{
				/*
				int nature = RunIssue.RUN_PROPERTY_REPRESENTED;
				noteRunIssue(nature, ex.getMessage());
				*/
			}
		}
		
		task  = "Containments of " + obj.eClass().getName()  + "(" + oRep.cSet().stringForm() + ")";
		trace(task);
		// get all objects reached by mapped containment associations, and fill them in
		for (Iterator<EReference> it = obj.eClass().getEAllReferences().iterator();it.hasNext();)
		{
			EReference ref = it.next();
			EClass childSuperClass = (EClass)ref.getEType();
			String roleName = ref.getName();
			if (roleName == null) roleName = "null role name";
			trace("Role name " + roleName);
			if (ref.isContainment())
			{
				EList<EObject> cont = new BasicEList<EObject>();
				// try all subclasses of the class at the other end of the association
				List<EClass> scList = xor.ms().getAllSubClasses(childSuperClass);
				for (Iterator<EClass> iw = scList.iterator(); iw.hasNext();)
				{
					EClass childClass = iw.next();
					if (!childClass.isAbstract())
					{
						String childClassName = ModelUtil.getQualifiedClassName(childClass);
						trace("Subclass " + childClassName);
						
						/* the call to representsAssociationRole  is so expensive that I now just 
						 * catch the nonrepresented Exception silently. */
						boolean representsAssoc = true; // xor.representsAssociationRole(oRep,roleName, childClassName);
						if (representsAssoc) try
						{
							task = "following " + oRep.cSet().stringForm() + "." + roleName + "." + childClassName;
							trace(task);
							Vector<objectToken> childObjects = 
								xor.getAssociatedObjectTokens(oRep, childClassName, roleName);
							if (childObjects.size() > 0) trace("found "  + childObjects.size());
							for (Iterator<objectToken> ix = childObjects.iterator();ix.hasNext();)
							{
								objectToken cRep = ix.next();
								String childSubClassName = cRep.className();
								trace("class of objectToken " + childSubClassName);
								/* Avoid the same object being found twice through different superclasses in the
								 * association being followed */
								if (childSubClassName.equals(childClassName))
								{
									EObject child = createModelObject(childSubClassName);
									fillObject(child,cRep);
									cont.add(child);
									trace("adding class " + childSubClassName);
									
									// store lookup for non-containment associations
									Hashtable<Object,EObject> childObjTable = savedModelObjects.get(childSubClassName);
									if (childObjTable == null) childObjTable = new Hashtable<Object,EObject>();
									childObjTable.put(cRep.objectKey(),child); 
									savedModelObjects.put(childSubClassName, childObjTable);									
								}
							}
						}
						catch (notRepresentedException ex) // now caught silently 
						{
							/*
							int nature = RunIssue.RUN_ASSOCIATION_REPRESENTED;
							String problem = ("Containment association not represented: " + 
									thisCName + "." + roleName + "." + childClassName + " " + 
									ex.getMessage());
							noteRunIssue(nature, problem);
							*/
						}
						
					}
				}
				// set the reference feature just once for all subclasses of the association end class
				if (ref.getUpperBound() == -1)
				{	
					obj.eSet(ref, cont);
					trace("setting single-valued " + ref.getName());
				}
				else if (ref.getUpperBound() == 1)
				{
					if (cont.size() > 0) obj.eSet(ref, cont.get(0));					
					trace("setting multi-valued " + ref.getName() + " with " + cont.size() + " values");
				}
				else trace("Failed to store child object");
			}
		}
	}
	
	/**
	 * find all non-containment links of an object which are mapped,
	 * and record them in the EMF model.
	 * Do the same for all contained objects.
	 * @param obj
	 * @param oRep
	 * @throws MapperException
	 */
	private void doNonContainmentLinks(EObject obj, objectToken oRep) throws MapperException
	{
		trace("Non-containments of " + obj.eClass().getName() 
				+ "(" + oRep.className() + ")");
		/* look at all links of this object, containment or not */
		for (Iterator<EReference> it = obj.eClass().getEAllReferences().iterator();it.hasNext();)
		{
			EReference ref = it.next();
			boolean oppositeOfContainment = ((ref.getEOpposite() != null)
					&& (ref.getEOpposite().isContainment()));
			// Need we look at the opposite of the containment link which got to this object? I think not.
			if ((!oppositeOfContainment) && (ref.getEType() != null)) 
			{
				EClass reffedSuperClass = (EClass)ref.getEType();
				String thisCName = ModelUtil.getQualifiedClassName(obj.eClass());
				String roleName = ref.getName();
				// build up a list of referenced objects over all subclasses of the other end object
				EList<EObject> reffedList = new BasicEList<EObject>();
				// try all subclasses of the class at the other end of the association
				List<EClass> scList = xor.ms().getAllSubClasses(reffedSuperClass);
				for (Iterator<EClass> iw = scList.iterator(); iw.hasNext();)
				{
					String reffedClassName = ModelUtil.getQualifiedClassName(iw.next());
					trace("following " + oRep.className() + "." + roleName + "." + reffedClassName);
					// Check if this class has been retrieved by a containment association
					Hashtable<Object,EObject> reffedTable = savedModelObjects.get(reffedClassName);
					if (reffedTable != null)
					{
						// then next check is very expensive, so in stead, catch the exception silently
						boolean representsAssoc = true; // xor.representsAssociationRole(oRep, roleName, reffedClassName);
						// look at both containment and non-containment associations, doing different things for each
						if (representsAssoc) try
						{
							Vector<objectToken> linkedObjects = 
									xor.getAssociatedObjectTokens(oRep, reffedClassName, roleName);
							trace("Found: " + linkedObjects.size());

							for (Iterator<objectToken> ix = linkedObjects.iterator();ix.hasNext();)
							{
								objectToken cRep = ix.next();
								EObject reffed = reffedTable.get(cRep.objectKey());
								if (reffed == null) 
								{
									trace("Failed to find referenced object in " + reffedTable.size() 
											+ " objects of class " + reffedClassName);
									recordFailedLookup(reffedClassName, (thisCName + "." + roleName));
								}
								else if (reffed != null)
								{
									reffedList.add(reffed);
									/* if this is a containment association, do the non-containment
									*  links of the contained object */
									if (ref.isContainment()) doNonContainmentLinks(reffed,cRep);							
								}							
							}
						}
						catch (notRepresentedException ex) // now caught silently, as it can happen
						{
							/*
							int nature = RunIssue.RUN_PROPERTY_CONVERSION;
							String problem = ("non-containment association not represented: " + 
									thisCName + "." + roleName + "." + reffedClassName + " " + 
									ex.getMessage());
							noteRunIssue(nature, problem);
							*/
						}
					}
				}
				
				/* if this is not a containment, and its opposite is not a containment
				 * (which will have been dealt with already) store the links on this object */
				boolean oppositeIsContainment = ((ref.getEOpposite() != null) && 
						(ref.getEOpposite().isContainment()));

				if (!oppositeIsContainment && (!ref.isContainment()) && (reffedList.size() > 0)) 
				{
					trace("Noting " + reffedList.size() + " references " + roleName);
					if (ref.getUpperBound() == -1) obj.eSet(ref, reffedList);				
					else if (ref.getUpperBound() == 1) obj.eSet(ref, reffedList.get(0));

					// checks
					else System.out.println("Failed to set non-containment " + ref.getName());
					Object newValue = obj.eGet(ref);
					if (newValue == null) System.out.println("Null value non-containment " + ref.getName());
					else if (ref.getUpperBound() == -1)
					{
						EList<?> oList = (EList<?>)newValue;
						if (oList.size() != reffedList.size())
							System.out.println("Cardinality mismatch of non-containment " + ref.getName() 
									+ " " + oList.size() + " " + reffedList.size());
					}
				}
				
			}
		}
	}
	
	/**
	 * record occasions when an object found by traversing a 
	 * non-containment association has not been previously found
	 * by traversing a containment relation
	 * @param className class of the object not found in lookup
	 * @param roleName role by which it was found
	 */
	private void recordFailedLookup(String className, String roleName)
	{
		Hashtable<String,Integer> failuresForClass = failedLookups.get(className);
		if (failuresForClass == null) failuresForClass = new Hashtable<String,Integer>();
		Integer failsForRole = failuresForClass.get(roleName);
		if (failsForRole == null) failsForRole = new Integer(0);
		failsForRole = new Integer(failsForRole.intValue() + 1);
		failuresForClass.put(roleName, failsForRole);
		failedLookups.put(className, failuresForClass);
	}
	
	private void writeFailedLookups()
	{
		for (Enumeration<String> en = failedLookups.keys();en.hasMoreElements();)
		{
			String className = en.nextElement();
			Hashtable<String,Integer> failuresForClass = failedLookups.get(className);
			for (Enumeration<String> ep = failuresForClass.keys();ep.hasMoreElements(); )
			{
				String roleName = ep.nextElement();
				Integer count = failuresForClass.get(roleName);
				System.out.println(count.intValue() + " failed lookups of class " 
						+ className + " found by association " + roleName);
			}
		}
	}

	
	/**
	 * @param previousOuters all the classes that were outer (which were not at the inner end of any
	 * containment relation) before any outer 'Container' class was added
	 * 
	 * @return for every class that was outer in the un-modified class
	 * model, the key is the ClassSet name and the value is a Vector of objectTokens found.
	 * 
	 * if hasAddedContainerClass = false, it is expected that there will be only one class
	 * and only one objectToken in that class.
	 */
	private Hashtable<String,Vector<objectToken>> getTopObjects(Vector<ClassSet> previousOuters)
	throws MapperException
	{
		Hashtable<String,Vector<objectToken>> topObjects = new Hashtable<String,Vector<objectToken>>();
		
		for (Iterator<ClassSet> io = previousOuters.iterator();io.hasNext();)
		{
			ClassSet cs = io.next();
			String qualifiedClassName = cs.className();
			Vector<objectToken> topClassInstances = new Vector<objectToken>();
			// get all instances of the class and filter them by subset
			Vector<objectToken> allTopClassInstances = xor.getAllLocalObjectTokens(qualifiedClassName);
			for (Iterator<objectToken> it = allTopClassInstances.iterator();it.hasNext();)
			{
				objectToken oRep = it.next();
				if (oRep.subset().equals(cs.subset())) topClassInstances.add(oRep);
			}
			topObjects.put(cs.stringForm(), topClassInstances);			
		}
		
		return topObjects;
	}
	
	/**
	 * Make a copy of the Ecore class model so you can change it 
	 * (by adding new classes and associations)
	 * without changing the original.
	 * 
	 * To make it without ripping the classes out of the original, I would 
	 * have to make copies of all the classes. It does not seem necessary
	 * to go to this length, as the changed class model is never persisted.
	 *  
	 * @param originalModelthe original class model
	 * @return the copy that can be modified
	 */
	/* private EPackage copyClassModel(EPackage originalModel)
	{
		EPackage newPackage = EcoreFactory.eINSTANCE.createEPackage();
		newPackage.setName(originalModel.getName());
		newPackage.setNsPrefix(originalModel.getNsPrefix());
		newPackage.setNsURI(originalModel.getNsURI());
		EList<EClassifier> cont = new BasicEList<EClassifier>();
		for (Iterator<EClassifier> it = originalModel.getEClassifiers().iterator(); it.hasNext();)
			cont.add(it.next());
		newPackage.eSet(EcorePackage.eINSTANCE.getEPackage_EClassifiers(), cont);
		return newPackage;
	} */
	
	/**
	 * if necessary (because there is more than one outer class)
	 * or if forced to do so, add a new class 'Container' with 
	 * a containment relation to all classes that were previously outer.
	 * @param classModel the class model
	 * @param forceContainer if true, you must add a new outer class
	 */
	private EClass addContainerClassIfNecessary(EPackage classModel, Vector<ClassSet> previousOuters, boolean forceContainer)
	throws MapperException
	{
		if ((previousOuters.size() != 1)|forceContainer)
		{
			// create the new container class
			EClass newClass = EcoreFactory.eINSTANCE.createEClass();
			containerClassName = newClassName("Container",classModel);
			newClass.setName(containerClassName);
			
			/* give it a containment relation to all classes that were previously outer classes; 
			 * avoid adding any class twice if it has more than one outer mapping */
			Hashtable<String,String> classNames = new Hashtable<String,String>();
			for (Iterator<ClassSet> io = previousOuters.iterator();io.hasNext();)
			{
				ClassSet cs = io.next();
				if (classNames.get(cs.className()) == null)
				{
					classNames.put(cs.className(),"1");
					EClass oc = ModelUtil.getNamedClass(classModel, cs.className());
					if (oc == null) throw new MapperException("Cannot find mapped class '" + cs.className() + "'");
					EReference eRef = EcoreFactory.eINSTANCE.createEReference();
					eRef.setEType(oc);
					eRef.setName("contains_" + oc.getName());
					eRef.setUpperBound(-1);  // means unbounded
					eRef.setContainment(true);
					newClass.getEStructuralFeatures().add(eRef);
				}
			}
			
			// add the new class to the top package of the new class model, and record it has been done
			classModel.getEClassifiers().add(newClass);
			hasAddedContainerClass = true;
			return newClass;
		}
		
		// when there is a unique top class and you are not forcing a container class...
		else if ((previousOuters.size() == 1) && (!forceContainer))
		{
			for (Iterator<ClassSet> io = previousOuters.iterator();io.hasNext();)
			{
				ClassSet cs = io.next();
				EClass oc = ModelUtil.getNamedClass(classModel, cs.className());
				if (oc == null) throw new MapperException("Cannot find unique top mapped class '" + cs.className() + "'");
				return oc;
			}
		}
		return null; // for the compiler
	}
	
	/**
	 * set the class model attached to the XOReader back to its original state
	 * @param classModel
	 */
	private void removeContainerClassIfNecessary(EPackage classModel)
	{
		if (!(containerClassName).equals(""))
		{
			EClassifier ec = classModel.getEClassifier(containerClassName);
			classModel.getEClassifiers().remove(ec);
		}
	}

	
	/**
	 * If necessary ,put some suffix onto a class name to stop
	 * it clashing with any existing class name in the model
	 * @param className the proposed name
	 * @param classModel the class model
	 * @return the non-clashing name
	 */
	private String newClassName(String className,EPackage classModel)
	{
		String cName = className;
		boolean clashes = true;
		int index = 0;
		while (clashes)
		{
			index++;
			clashes = false;
			for (Iterator<EClass> it = ModelUtil.getAllClasses(classModel).iterator();it.hasNext();)
				if (it.next().getName().equals(cName)) clashes = true;
			if (clashes) cName = className + "_" + index;
		}
		return cName;
	}

	
	/**
	 * Used only in translation test.
	 * build up the Hashtable (key - objectToken key; value = empty EObject)
	 * for all objects in the classes that were outer in the Ecore
	 * model before an outer Container class was added
	 * @param topobjectTokens
	 */
	private void recordModelObjects(Hashtable<String,Vector<objectToken>> topobjectTokens) throws MapperException
	{
		// iterate over all the classes that were previously outer
		for (Enumeration<String> en = topobjectTokens.keys();en.hasMoreElements();)
		{
			String classSetName = en.nextElement();
			Hashtable<Object,EObject> topObjects = new Hashtable<Object,EObject>();
			Vector<objectToken> reps = topobjectTokens.get(classSetName);
			String qualifiedClassName = null;
			for (Iterator<objectToken> it = reps.iterator();it.hasNext();)
			{
				objectToken oRep = it.next();
				qualifiedClassName = oRep.className();
				EObject topObject = createModelObject(qualifiedClassName);
				topObjects.put(oRep.objectKey(), topObject);				
			}
			
			// if more than one subset of a top class is represented, collect all objectTokens
			if (qualifiedClassName != null) 
			{
				Hashtable<Object,EObject> previousTopObjects = savedModelObjects.get(qualifiedClassName);
				if (previousTopObjects != null)
					for (Enumeration<Object> eo  = previousTopObjects.keys(); eo.hasMoreElements();)
					{
						Object key = eo.nextElement();
						topObjects.put(key, previousTopObjects.get(key));
					}
				savedModelObjects.put(qualifiedClassName,topObjects);
			}
		}		
	}

	
	//----------------------------------------------------------------------------------------
	//                            recording runtime issues
	//----------------------------------------------------------------------------------------
	
	/**
	 * @return a summary of any exceptions thrown or reader problems encountered
	 * when making the instance, with duplicates removed.
	 * The RunIssues do not contain the XPaths in the source where the exception occurred.
	 */
	public List<RunIssue> runIssues()
	{
		Vector<RunIssue> issues = new Vector<RunIssue>();
		for (Enumeration<RunIssue> en = allRunIssues.elements();en.hasMoreElements();)
			issues.add(en.nextElement());
		return issues;
	}
	
	protected Hashtable<String,RunIssue> allRunIssues;
	
	
	/**
	 * note a runtime issue, with no XPath information,
	 *  counting occurrences of duplicates
	 * @param nature constant defining the broad nature of the issue - see class RunIssue
	 * @param problem text description of the problem
	 */
	private void noteRunIssue(int nature, String problem)
	{
		RunIssue ri = new RunIssue(nature,"","",problem,new Xpth(new NamespaceSet()),0);
		RunIssue existingIssue = allRunIssues.get(ri.key());
		if (existingIssue != null) existingIssue.addOccurrence();
		else allRunIssues.put(ri.key(),ri);
		trace("Run issue " + nature + ": " + problem);
	}
	
	/** 
	 * test of Ecore models; test that the subclass relationship is not circular 
	 * */
	@SuppressWarnings("unused")
	private void eCoreTest(EObject rootObject)
	{
		System.out.println("\nTest the subclass relation is not circular");
		if (rootObject instanceof EPackage)
		{
			for (Iterator<EClass>it = ModelUtil.getAllClasses((EPackage)rootObject).iterator();it.hasNext();)
			{
				EClass ec = it.next();
				System.out.println("\nClass " + ec.getName());
				
				String supers = "Immediate superclasses: ";
				for (Iterator<EClass> is = ec.getESuperTypes().iterator();is.hasNext();)
					supers = supers + is.next().getName() + " ";
				System.out.println(supers);

				supers = "All superclasses: ";
				for (Iterator<EClass> is = ec.getEAllSuperTypes().iterator();is.hasNext();)
					supers = supers + is.next().getName() + " ";
				System.out.println(supers);
			}
		}
	}
	
	private void trace (String s) {if (tracing) System.out.println(s);}



}
