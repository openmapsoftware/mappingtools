package com.openMap1.mapper.fhir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.hl7.fhir.instance.formats.Composer;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.AtomFeed;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.RunIssue;
import com.openMap1.mapper.reader.AbstractReaderWriter;
import com.openMap1.mapper.reader.EMFInstanceFactory;
import com.openMap1.mapper.reader.EMFInstanceFactoryImpl;
import com.openMap1.mapper.reader.EObjectRep;
import com.openMap1.mapper.reader.GenericEMFInstanceFactoryImpl;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.reader.objectRep;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.writer.XMLWriter;
import com.openMap1.mapper.writer.objectGetter;

public class FHIRMapper  extends AbstractReaderWriter implements XOReader, objectGetter, XMLWriter{
	
	private EObject fhirInstance;
	
	// key = qualified class name; value = vector of all objectTokens for the class
	private Hashtable<String,Vector<objectToken>> allObjectTokens;
	
	/* key 1 = node in the FHIR instance
	 * key 2 = class of an object with an EReference to it
	 * key 3 = name of the EReference
	 * element = Vector of objectTokens for EObjects in the class with the EReferecne to the first EObject */
	private Hashtable<EObject,Hashtable<String,Hashtable<String,Vector<objectToken>>>> inverseRelations;
	
	private objectToken outerObjectToken;
	
	private boolean doCheck = true;
	
	private boolean tracing = true;

	private static String TEMPORARY_READ_FILE = "/eclipseTempReadFile.xml";
	private static String TEMPORARY_WRITE_FILE = "/eclipseTempWriteFile.xml";
	
	private EcoreReferenceBridge bridge;

	
	//---------------------------------------------------------------------------------
	//                              Constructor for XOReader
	//---------------------------------------------------------------------------------

    /**
     * constructor for XOReader and objectGetter uses
     */

	public FHIRMapper(Element XMLFileRoot, MappedStructure ms,
			EPackage classModel, messageChannel mChan) throws MapperException 
	{
		super(XMLFileRoot, ms, classModel, mChan);
		
		trace("Done superclass constructor");
		
		setRoot(XMLFileRoot);
		
	}

	/**
	 * this method:
	 * (a) converts the root element to an inputStream, by an ugly method
	 * (b) reads the stream into an AtomFeed instance of the Java FHIR reference implementation
	 * (c) converts the reference implementation instance into an EMF model instance
	 */
	public void setRoot(Node el) throws MapperException 
	{
		if (!(el.getLocalName().equals("feed"))) throw new MapperException("Root node is not a 'feed' element");

		// write out the DOM in order to read it in as a stream for the AtomParser
		trace("writing out DOM");
		String tempFileLocation = EclipseFileUtil.workspaceRoot() + TEMPORARY_WRITE_FILE;
    	XMLUtil.writeOutput(el.getOwnerDocument(),tempFileLocation,false);
		trace("written DOM to stream");
    	try{
    		FileInputStream fileStream = new FileInputStream(tempFileLocation);
    		
    		// parse the input to get an AtomFeed object of the reference implementation
    		trace("Parsing input stream");
    		XmlParser parser = new XmlParser();
    		// allow unknown new tag names; but does not work for contained resources (new parser instance)
    		parser.setAllowUnknownContent(true);
    		AtomFeed feed = parser.parseGeneral(fileStream).getFeed();
    		
    		// delete the temporary file
    		File file = new File(tempFileLocation);
    		file.delete();
    		
    		// convert the reference model instance to an ECore model instance
    		trace("converting to Ecore model instance");
    		bridge = new EcoreReferenceBridge(classModel());
    		fhirInstance = bridge.getEcoreModelInstance(feed);
    		trace("Ecore model instance created");
    		// testWriteInstance(fhirInstance,classModel);
    	}
    	catch (Exception ex) 
    	{
    		ex.printStackTrace();
    		throw new MapperException("Failed either to make AtomFeed Instance, or to convert it to an ECore instance: " + ex.getMessage());
    	}
    	
    	// refresh the object tokens
    	makeAllObjectTokens();
	}
	
	public void setInputRoot(Node el) throws MapperException {setRoot(el);} 
	
	
	/**
	 * make the tables of all ObjectTokens needed for this to function
	 * as an XOReader of objectGetter
	 */
	private void makeAllObjectTokens()
	{
		allObjectTokens = new Hashtable<String,Vector<objectToken>>() ;
		inverseRelations = new Hashtable<EObject,Hashtable<String,Hashtable<String,Vector<objectToken>>>>() ;
		outerObjectToken = makeObjectToken(fhirInstance);
		writeAllObjectTokens();		
	}


	
	/**
	 * recursive descent of containment relations in the EMF instance, 
	 * to make all objectTokens
	 * @param obj
	 */
	@SuppressWarnings("unchecked")
	private objectToken makeObjectToken(EObject obj)
	{
		EClass theClass = obj.eClass();
		// qualified class names
		String className = theClass.getEPackage().getName() + "." + theClass.getName();
		trace("Making object token for " + className);
		Vector<objectToken> tokens = allObjectTokens.get(className);
		if (tokens == null) tokens = new Vector<objectToken>();
		objectToken result = new EObjectRep(obj,this);
		tokens.add(result);
		allObjectTokens.put(className,tokens);
		
		for (Iterator<EStructuralFeature> it = theClass.getEStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature feat = it.next();
			if (feat instanceof EReference)
			{
				EReference ref = (EReference)feat;
				String refName = ref.getName();
				Object value = obj.eGet(ref);
				if (value != null)
				{
					if (ref.getUpperBound() == 1) 
					{
						EObject eValue = (EObject)value;
						if (ref.isContainment()) makeObjectToken(eValue);
						addInverseRelation(eValue,className,refName,result);
					}
					else if (value instanceof List)
					{
						List<Object> lVal = (List<Object>)value;
						for (Iterator<Object> iu = lVal.iterator(); iu.hasNext();)
						{
							EObject eValue = (EObject)iu.next();
							if (ref.isContainment()) makeObjectToken(eValue);
							addInverseRelation(eValue,className,refName,result);
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param eValue EObject whose inverse relations are being recorded
	 * @param className qualified class name for the object at the other end
	 * @param refName name of the relation whose inverse is being stored
	 * @param owner objectToken for the object at the other end of the relation
	 * 
	 * e.g. eValue is a Patient resource object; className = feed.AtomFeed; refName = 'patient'; owner = objectToken for the AtomFeed object 
	 */
	private void addInverseRelation(EObject eValue, String className,String refName, objectToken owner)
	{
		Hashtable<String,Hashtable<String,Vector<objectToken>>> inverseRelationsForObject = inverseRelations.get(eValue);
		if (inverseRelationsForObject == null) inverseRelationsForObject = new Hashtable<String,Hashtable<String,Vector<objectToken>>>();

		Hashtable<String,Vector<objectToken>> inversesForObjectAndClass = inverseRelationsForObject.get(className);
		if (inversesForObjectAndClass == null) inversesForObjectAndClass = new Hashtable<String,Vector<objectToken>>();
		
		Vector<objectToken> inversesForObjectClassRef  = inversesForObjectAndClass.get(refName);
		if (inversesForObjectClassRef == null) inversesForObjectClassRef = new Vector<objectToken>();
		
		inversesForObjectClassRef.add(owner);
		inversesForObjectAndClass.put(refName, inversesForObjectClassRef);
		inverseRelationsForObject.put(className,inversesForObjectAndClass);
		inverseRelations.put(eValue, inverseRelationsForObject);
	}
	
	
	/**
	 * 
	 * @param eValue
	 * @param className
	 * @param refName
	 * @return objectTokens for objects of a named class that have a named EReferecne to this object
	 */
	private Vector<objectToken> getInverseRelatedObjects(EObject eValue, String className, String refName)
	{
		Vector<objectToken> result = new Vector<objectToken>();
		Hashtable<String,Hashtable<String,Vector<objectToken>>> inverseRelationsForObject = inverseRelations.get(eValue);
		if (inverseRelationsForObject != null)
		{
			Hashtable<String,Vector<objectToken>> inversesForObjectAndClass = inverseRelationsForObject.get(className);
			if (inversesForObjectAndClass != null)
			{
				Vector<objectToken> inversesForObjectClassRef  = inversesForObjectAndClass.get(refName);
				if (inversesForObjectClassRef != null) result = inversesForObjectClassRef;				
			}			
		}		
		return result;
	}




    /**
     * Vector  of objectTokens for all nodes representing objects
    *  in any subclasses of a given class, in all subsets of those subclasses.
    *
    *  @param className  - the name of the class
    *  @exception MapperException  - class not represented in the XML
    *                - you ignored some exception on creating XOReader
    */
	public Vector<objectToken> getAllObjectTokens(String className)
			throws MapperException {
		Vector<objectToken> tokens = allObjectTokens.get(className);
		if (tokens == null) tokens = new Vector<objectToken>();
		return tokens;
	}
	

	/**
	 * there cannot be importing or imported FHIR mapping sets (yet)
	 * so 'local' objects are the same as all objects
	 */
	public Vector<objectToken> getAllLocalObjectTokens(String className)
			throws MapperException {
		return getAllObjectTokens(className);
	}



    /**
     * String value of a property of some represented object
    *
    *  @param oTok  - the objectToken for the object
    *  @param propertyName  - the name of the property
    *
    **/
	public String getPropertyValue(objectToken oTok, String propertyName)
			throws MapperException 
	{		
		String propVal = null;		
		EObject theObject = ((EObjectRep)oTok).theObject();
		EClass theClass = theObject.eClass();
		
		EStructuralFeature feat = theClass.getEStructuralFeature(propertyName);
		if (feat == null) throw new MapperException("No feature '" + propertyName + "' of class '" + theClass.getName() + "'in FHIR class model");

		if (feat instanceof EAttribute)
		{
			Object result = theObject.eGet(feat);
			if (result != null)
			{
				if (result instanceof String) propVal = (String)result;
				else if (result instanceof Boolean) propVal = result.toString();
				else if (result instanceof Integer) propVal = result.toString();
				else throw new MapperException("Value of property '" + propertyName + "' of class '"
						+ theObject.eClass().getName() + "' is not handled yet in the FHIR objectGetter: " + result.getClass().getName());
			}
		}
		else throw new MapperException("Feature '" + propertyName + "' is an association in the FHIR class model");
		return propVal;
	}

    /**
     * Vector of objectTokens representing objects related to the current object by some association.
     *
     * @param oRep - the input object at one end of the association
     * @param otherClassQualifiedName - class or superclass of the objects to be retrieved
     * @param otherRole - the role played by the other-end object in the association
     *
     */
    public Vector<objectToken> getAssociatedObjectTokens(objectToken oTok,
       String otherClassQualifiedName, String otherRole) throws MapperException
    {
		EClass otherClass = ModelUtil.getNamedClass(classModel, otherClassQualifiedName);
    	Vector<objectToken> result = new Vector<objectToken>();

		EObject theObject = ((EObjectRep)oTok).theObject();
		EClass theClass = theObject.eClass();
		EStructuralFeature feat = theClass.getEStructuralFeature(otherRole);
		if (feat == null) 
		{ 
			throw new MapperException("No feature '" +  otherRole
					+ "' from class '" + theClass.getName() + "' to class '"
					+ otherClassQualifiedName + "' in FHIR Ecore class model");
		}
		if (feat instanceof EReference)
		{
			EReference ref = (EReference)feat;
			EClassifier otherEnd = ref.getEType(); // may be a superclass of the supplied other class
			String otherEndName = otherEnd.getEPackage().getName() + "." + otherEnd.getName();
			// if the two classes are not the same, check the superclass relation
			if (!otherClassQualifiedName.equals(otherEndName))
			{
				boolean foundSuper = false;
				for(Iterator<EClass> ic = otherClass.getESuperTypes().iterator(); ic.hasNext();) 
					if (ic.next().getName().equals(otherEnd.getName())) foundSuper = true;
				if (!foundSuper) throw new MapperException("Other end class " + otherClassQualifiedName
						+ " is not " + otherEnd.getName() + " in association role " 
						+ otherRole + " from class " + oTok.className());
			}
			
			Object value = theObject.eGet(feat);
			
			// if you find one or more objects at the end of the association, you need to check they are in the right class
			if (value != null)
			{
				if (ref.getUpperBound() == 1) 
				{
					EObject eVal = (EObject)value;
					EClass foundClass = eVal.eClass();
					String foundClassName = foundClass.getEPackage().getName() + "." + foundClass.getName();
					if (foundClassName.equals(otherClassQualifiedName)) result.add(new EObjectRep(eVal,this));
				}
				else if (value instanceof List)
				{
					List<Object> lVal = (List<Object>)value;
					for (Iterator<Object> iu = lVal.iterator(); iu.hasNext();)
					{
						Object next = iu.next();
						EObject eVal = (EObject)next;
						EClass foundClass = eVal.eClass();
						String foundClassName = foundClass.getEPackage().getName() + "." + foundClass.getName();
						if (foundClassName.equals(otherClassQualifiedName)) result.add(new EObjectRep(eVal,this));
					}
				}
			}
		}
		else throw new MapperException("Feature '" + otherRole + "' is an EAttribute in the FHIR class model");
    	return result;
    }
    
	/**
	 * From ModelUtil: 'The association name is the role name if the reference has no opposite.'
	 * Sometimes called with thisEnd = 2 when checking association conditions. In these cases, must look up 
	 * object related by inverse associations, as these are not present in the Ecore class model
	 */
    public Vector<objectToken> getAssociatedObjectTokens(objectToken oTok,
			String assocName, String otherClass, int thisEnd)
			throws MapperException 
	{
    	String otherRole = assocName;
		EObject theObject = ((EObjectRep)oTok).theObject();
    	if (thisEnd == 2) return getInverseRelatedObjects(theObject, otherClass, assocName);
    	else return getAssociatedObjectTokens(oTok,otherClass,otherRole);
	}

    
    /**
     * n method required by the abstract superclass but should not be used
     */
    public Vector<objectToken> getTheAssociatedObjectReps(objectToken oTok, String assocName,
            String otherClass, int thisEnd, String otherRole) throws MapperException
    {
    	if (doCheck) throw new MapperException("Method getTheAssociatedObjectReps in class FHIRMapper should not be called");
    	return new Vector<objectToken>();
    }

    /**
     * If the mapping set of this XOReader it imported by some
     * other mapping set, the qualified name of the parameter class.
     * The importing node or some ancestor of it must represent an instance of the 
     * parameter class.
     * @return
     * @throws MapperException
     */
	public String parameterClassName() throws MapperException {
		return "No parameter class";
	}

	/**
	 * 
	 */
	public boolean representsObject(String qualifiedClassName) {
		EClass theClass = ModelUtil.getNamedClass(classModel, qualifiedClassName);
		return (theClass != null);
	}
	

	
	private String bareClassName(String qualifiedClassName)
	{
		String bareName = "";
		StringTokenizer st  = new StringTokenizer(qualifiedClassName,".");
		while (st.hasMoreTokens()) bareName = st.nextToken();
		return bareName;
	}

	/**
	 * 
	 */
	public boolean representsProperty(String className, String property) {
		boolean represents = false;
		EClass theClass = ModelUtil.getNamedClass(classModel, className);
		if (theClass != null)
		{
			represents = theClass.getEStructuralFeature(property) != null;
		}
		return represents;
	}

	/**
	 * 
	 */
	public boolean representsProperty(objectRep oRep, String property) {
    	if (doCheck) message("Method representsProperty(objectRep oRep, String property) should not be called");
		return false;
	}

	/**
	 * 
	 */
	public boolean representsAssociationRole(String class1, String roleName,
			String class2) {
		boolean represents = false;
		EClass theClass = ModelUtil.getNamedClass(classModel, class1);
		if (theClass != null)
		{
			represents = theClass.getEStructuralFeature(roleName) != null;
		}
		return represents;
	}

	/**
	 * 
	 */
	public boolean representsAssociationRole(objectRep oRep, String roleName,
			String class2) {
    	if (doCheck) message("Method representsAssociationRole(objectRep oRep,... should not be called");
		return false;
	}

	/**
	 * 
	 */
	public boolean representsAssociation(String class1, String assocName,
			String class2) {
    	if (doCheck) message("Method representsAssociation(String class1, String assocName,... should not be called");
		return false;
	}

	/**
	 * 
	 */
	public Hashtable<String, ClassSet> subsets(String className) {
    	if (doCheck) message("Method subsets(String className) should not be called");
		return null;
	}

	/**
	 * 
	 */
	public Vector<String> getQualifiedClassNames(String bareClassName) {
		Vector<String> qualNames = new Vector<String>();
		for (Iterator<EPackage> it = classModel.getESubpackages().iterator();it.hasNext();)
		{
			EPackage pack = it.next();
			if (pack.getEClassifier(bareClassName) != null) qualNames.add(pack.getName() + "." + bareClassName);
		}
		return qualNames;
	}

	/**
	 * 
	 */
	public Vector<ClassSet> outerObjectClassSets() {
		Vector<ClassSet> outers = new Vector<ClassSet>();
		outers.add(outerObjectToken.cSet());
		return outers;
	}
	
	public XOReader reader() {return this;}
	
	//----------------------------------------------------------------------------------------------------------
	//                    Constructor and methods for XMLWriter interface
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * Constructor for use as an XMLWriter
	 * @param oGet
	 * @param ms
	 * @param classModel
	 * @param mChan
	 * @param doRunTracing
	 * @throws MapperException
	 */
	public FHIRMapper(objectGetter oGet, MappedStructure ms, 
    		EPackage classModel, messageChannel mChan, Boolean doRunTracing)  
    throws MapperException
    {
		super(oGet,ms,classModel,mChan, doRunTracing);
    }


	@Override
	public Element makeXMLDOM() throws MapperException {
		
		// find the top AtomFeed object represented by the input
		// ((FHIRMapper)oGet).writeClasses(); // diagnostic
		Vector<objectToken> atomFeedObjects = oGet.getObjects("feed.AtomFeed");
		if (atomFeedObjects.size() != 1) throw new MapperException("There should be just one AtomFeed object represented, not " + atomFeedObjects.size());
		objectToken topObjectToken = atomFeedObjects.get(0);
		
		// use the objectGetter's reader to make an Ecore model instance
		EMFInstanceFactory factory = new GenericEMFInstanceFactoryImpl();
		Resource modelResource = factory.createModelInstance(oGet.reader(),EMFInstanceFactoryImpl.DO_NOT_SAVE_URI(), topObjectToken);
		EObject modelInstance = modelResource.getContents().get(0);
		
		// convert the Ecore model instance to an instance of the Java Reference Implementation
		EcoreReferenceBridge bridge = new EcoreReferenceBridge(classModel);
		AtomFeed feed = bridge.getReferenceModelFeed(modelInstance);
		
		// serialize the reference implementation instance
		String tempFileLocation = EclipseFileUtil.workspaceRoot() + TEMPORARY_READ_FILE;
		try{
			FileOutputStream stream = new FileOutputStream(tempFileLocation);
			Composer composer = new XmlComposer();
			composer.compose(stream, feed, false);			
		}
		catch (Exception ex) {throw new MapperException("Failed to serialize reference implementation instance: " + ex.getMessage());}
		
		Element root = XMLUtil.getRootElement(tempFileLocation);
		File file = new File(tempFileLocation);
		file.delete();
		
		return root;
	}

	@Override
	public Element extendXMLDOM(Element bareElement, objectToken oTok)
			throws MapperException {
		if (doCheck) throw new MapperException("FHIRMapper does not support method extendXMLDOM");
		return null;
	}

	public Hashtable<String, Hashtable<String, RunIssue>> allRunIssues() {
		return new Hashtable<String, Hashtable<String, RunIssue>>();
	}
	
	private void writeAllObjectTokens()
	{
		trace("\nObjects represented");
		for (Enumeration<String> en = this.allObjectTokens.keys();en.hasMoreElements();)
		{
			String className = en.nextElement();
			Vector<objectToken> toks = allObjectTokens.get(className);
			trace(className + ": " + toks.size());
		}
	}

	// diagnostic
	private void writeClasses()
	{
		message("\nAll classes");
		for (Enumeration<String> en = allObjectTokens.keys(); en.hasMoreElements();) message(en.nextElement());
	}
	
	/**
	 * write out an instance of an ECore model for test purposes
	 * @param instance
	 * @param classModel
	 */
	private void testWriteInstance(EObject instance,EPackage classModel) throws MapperException
	{
		// make a location for the instance, in the same folder as the class model
		URI modelUri = classModel.eResource().getURI();
		StringTokenizer st = new StringTokenizer(modelUri.toString(),".");
		String extension = "mod";
		URI instanceUri = URI.createURI(st.nextToken() + "." + extension);
		message("URI for test instance: " + instanceUri.toString());
		
		// make a resource set etc.
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(extension, new XMIResourceFactoryImpl());
		Resource instanceResource = resourceSet.createResource(instanceUri);
		instanceResource.getContents().add(instance);
		
		// save it
		try {instanceResource.save(null);}
		catch (IOException ex) 
		   {throw new MapperException("Failed to save EMF model resource: " + ex.getMessage());}
		message("Wrote test instance");

	}

	
	private void trace(String s) {if (tracing) System.out.println(s);}
	
	private void message(String s) {System.out.println(s);}

}
