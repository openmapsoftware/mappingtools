package com.openMap1.mapper.converters;

/**
 * This class implements an XOReader for V3 XML which is equivalent 
 * to an MDLXOreader driven by V3-V3 mappings - but it can be done simply in Java
 * because the mappings are so regular in form
 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MDLWriteException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.RunIssue;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.impl.ElementDefImpl;
import com.openMap1.mapper.reader.AbstractReaderWriter;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.reader.objectRep;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLOutputFile;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.writer.MappedXMLWriter;
import com.openMap1.mapper.writer.XMLWriter;
import com.openMap1.mapper.writer.objectGetter;
import com.openMap1.mapper.MappedStructure;

/**
 * Java mapping class for the HL7  V3 XML ITS - equivalent to 
 * a set of mappings between the V3 XML ITS and the RIM-based V3 class model
 * 
 * @author robert
 *
 */

public class V3_XML_ITS extends AbstractReaderWriter implements XOReader, objectGetter, XMLWriter{
	
	/**
	 * all objectReps in the XML instance
	 * First String key  = qualified class name
	 * Second String key = XPath to node = subset of the objectRep
	 * Vector = all objectReps with that path
	 */
	private Hashtable<String, Hashtable<String,Vector<objectRep>>> allObjectReps = 
		new Hashtable<String, Hashtable<String,Vector<objectRep>>>();
	
	private static String V3NAMESPACEURI = "urn:hl7-org:v3";
	
	private static String V3NAMESPACEPREFIX = "";

	//private static String VOCABULARYNAMESPACEURI = "urn:hl7-org:v3/voc";
	
	//private boolean hasV3Namespace;
	
	//---------------------------------------------------------------------------------
	//                              Constructors
	//---------------------------------------------------------------------------------

    /**
     * constructor for XOReader and objectGetter uses
     */
	public V3_XML_ITS(Element XMLFileRoot, MappedStructure ms, 
    		EPackage classModel, messageChannel mChan)  
    throws MapperException
    {
    	super(XMLFileRoot, ms, classModel,mChan);
    	findAllObjectReps();
    }
	

    /**
     * constructor for XMLWriter uses
     * @param oGet
     * @param ms
     * @param classModel
     * @param mChan
     * @param doRunTracing
     * @throws MapperException
     */
	public V3_XML_ITS(objectGetter oGet, MappedStructure ms, 
    		EPackage classModel, messageChannel mChan, Boolean doRunTracing)  
    throws MapperException
    {
    	super(oGet, ms, classModel,mChan,doRunTracing);
    }
	
    /**
     * When the root element is reset, all the objectReps must be reset
     */
	public void setRoot(Node el)  throws MapperException
    {
    	super.setRoot(el);
    	findAllObjectReps();
    }

    
    /**
     * set the root of the XML instance being read
     * @param el
     * @throws MapperException
     */
         public void setInputRoot(Node el)  throws MapperException
         {
        	 oGet.setRoot(el);
         }

	//---------------------------------------------------------------------------------
	//                           Handling namespaces
	//---------------------------------------------------------------------------------

	/** set the namespaces in the output XML file to be
	  * the same as those in the output structure definition, in both prefix and URI
	  * - except we do not want the XML Schema namespace in the output namespaces. */
	  public void setOutputNamespaces() throws MapperException
	  {
	      xout.setNSSet(new NamespaceSet());
	      for (int i = 0; i < ms().getNamespaceSet().size(); i++)
	      {
	          namespace ns = ms().getNamespaceSet().getByIndex(i);
	          if (!(ns.URI().equals(XMLUtil.SCHEMAURI))) xout.NSSet().addNamespace(ns);
	      }
	  }
	
	//---------------------------------------------------------------------------------
	//                  Finding all objectReps in the XML instance
	//---------------------------------------------------------------------------------
	
	private void findAllObjectReps()
	throws MapperException
	{
		allObjectReps = new Hashtable<String, Hashtable<String,Vector<objectRep>>>();
		EClass theClass = getEntryClass();
		String path = "/" + theClass.getName();
		// check that the tag name of the top element is the entry class name
		if (theClass.getName().equals(XMLFileRoot.getLocalName())) 
			addObjectReps(theClass,XMLFileRoot,path);
	}

	/**
	 * recursive descent of the whole XML Instance, recording
	 * an objecRep for every node reached through associations in the 
	 * Ecore model
	 * @param theClass
	 * @param el
	 * @param path
	 * @throws MapperException
	 */
	private void addObjectReps(EClass theClass,Element el,String path)
	throws MapperException
	{
		addObjectRep(theClass,el,path);
		for (Iterator<Element>ie = XMLUtil.childElements(el).iterator();ie.hasNext();)
		{
			Element childEl = ie.next();
			String tagName = XMLUtil.getLocalName(childEl);
			String newPath = path + "/" + tagName;
			EStructuralFeature feature = theClass.getEStructuralFeature(tagName);
			// if the tag name is not an association role name in the model, ignore 
			if ((feature != null) && (feature instanceof EReference))
			{
				EReference ref = (EReference)feature;
				EClass childClass = (EClass)ref.getEType();
				addObjectReps(childClass,childEl,newPath);
			}
		}
	}
	
	/**
	 * record a single objectRep for a class on an Element
	 * @param theClass
	 * @param el
	 * @param path
	 * @throws MapperException
	 */
	private void addObjectRep(EClass theClass,Element el,String path)
	throws MapperException
	{
		String className = ModelUtil.getQualifiedClassName(theClass);
		Hashtable<String,Vector<objectRep>> objectReps = allObjectReps.get(className);
		if (objectReps == null) objectReps = new Hashtable<String,Vector<objectRep>>();
		Vector<objectRep> repsForPath = objectReps.get(path);
		if (repsForPath == null) repsForPath = new Vector<objectRep>();
		// use the path as a subset for the objectRep
		repsForPath.add(new objectRep(el,className,path,this));
		objectReps.put(path, repsForPath);
		allObjectReps.put(className, objectReps);
	}


    
    /**
     * @return the single entry class to the RMIM
     * @throws MapperException if there are none, or more than one
     */
    private EClass getEntryClass() throws MapperException
    {
    	EClass entry = null;
		/* find the one EClass marked as the entry class, and set it as the root of the tree; 
		 * iterate over all packages to find it*/
		for (Iterator<EPackage> ip = classModel.getESubpackages().iterator();ip.hasNext();)
		{
			EPackage rmimPackage = ip.next();
			// iterate over classes in the package
			for (Iterator<EClassifier> ic = rmimPackage.getEClassifiers().iterator(); ic.hasNext();)
			{
				EClassifier ec = ic.next();
				if ((ec instanceof EClass) && (ModelUtil.getEAnnotationDetail(ec, "entry") != null))
				{
					if (entry != null) throw new MapperException("More than one entry class in V3 RMIM");
					entry = (EClass)ec;
				}
			}
		}
		if (entry == null) throw new MapperException("no entry classese in V3 RMIM");
    	return entry;
    }


	//--------------------------------------------------------------------------------------------
    //	                          Data retrieval methods
	//--------------------------------------------------------------------------------------------


    /**
     * Vector  of objectTokens for all nodes representing objects
    *  in any subclasses of a given class, in all subsets of those subclasses.
    *  @param className  - the name of the class
    *  Note this implementation does not address subclasses - not relevant for V3?
    */
    public Vector<objectToken> getAllObjectTokens(String className) throws MapperException
    {
    	Vector<objectToken> result = new Vector<objectToken>();
    	Hashtable<String,Vector<objectRep>> oReps = allObjectReps.get(className);
    	if (oReps != null) for (Enumeration<Vector<objectRep>> en = oReps.elements();en.hasMoreElements();)
    	{
    		Vector<objectRep> reps = en.nextElement();
    		for (Iterator<objectRep> ir = reps.iterator();ir.hasNext();)
    			result.add(ir.next());
    	}
    	return result;
    }
    
    /**
     * check that any incoming objectRep is of the form you expect
     * @param oRep
     * @throws MapperException
     */
    private void checkObjectRep(objectRep oRep) throws MapperException
    {
    	String description = " class " + oRep.className() + "; subset: " + oRep.subset();
    	
    	// only accept objectReps from this reader
    	if (oRep.reader() != this)
    		throw new MapperException("objectRep from another XOReader " 
    				+ oRep.reader().ms().getMappingSetName() + ": " + description);
    	
    	// the node representing an object must be an Element
    	if (!(oRep.objNode() instanceof Element))
    		throw new MapperException("Mapped mode is not an element, but is a " 
    				+ oRep.objNode().getClass().getName() + ": "+ description);
    	
    	// the subset must be a path beginning with "";
    	if (!(oRep.subset().startsWith("/")))
    		throw new MapperException("Mapped mode path is not valid: " + description);
    }

    /**
     * String value of a property of some represented object
    *
    *  @param oRep  - the objectToken for the object
    *  @param propertyName  - the name of the property
    */
    public String getPropertyValue(objectToken oTok, String propertyName) throws MapperException
    {
    	if (oTok instanceof objectRep)
    	{
    		objectRep oRep = (objectRep)oTok;
    		checkObjectRep(oRep);
    		Element el = (Element)oRep.objNode();
  
    		// special attribute 'textContent'
    		if (propertyName.equals("textContent")) return XMLUtil.getText(el);
    		// special attribute 'element_position'
    		else if (propertyName.equals("element_position")) return XMLUtil.ordinalPosition(el);
    		// all other attributes
    		else return el.getAttribute(propertyName);
    	}
    	else throw new MapperException("Object token is not an objectRep when getting property "
    			+ propertyName + " of class " + oTok.className());
    }
    
    /**
     * Main method for following associations through mappings; the other two methods in the XOReader
     * interface can be got by simple calls to this method, as shown in MDLXOReader.
     * 
     * @param oTok object token for the start object
     * @param assocName association name, usually composed from the two end role names
     * @param otherClass qualified class name at the target end of the association
     * @param thisEnd end of the start object, if the role name is not specified; or -1 if it is
     * @param otherRole role name leading to the target end; or "" if not specified
     * @return object tokens for objects reached by the association
     * @throws MapperException
     * 
     * For V3 RMIMs, the navigable end
     */
   public Vector<objectToken> getTheAssociatedObjectReps(objectToken oTok, String assocName,
        String otherClass, int thisEnd, String otherRole) throws MapperException
   {
		Vector<objectToken> result = new Vector<objectToken>();
	   if (!(oTok instanceof objectRep))
	    	throw new MapperException("Object token is not an objectRep when following association "
	    			+ assocName + " of class " + oTok.className());
		objectRep oRep = (objectRep)oTok;
		checkObjectRep(oRep);
		Element el = (Element)oRep.objNode();

		EClass parent = ModelUtil.getNamedClass(classModel, oTok.className());
		if (parent == null) throw new MapperException("Cannot find parent class " + oTok.className());

	   String roleName = "";

	   /* a call with role not specified (as used e.g. by writing procedures) 
	    * so you need to work out the role name*/
   		if (otherRole.equals(""))
   		{
   			// usually expect to navigate from end 1 to end 2
   			if (thisEnd == 1) {roleName = assocName;} // because the other end role name is always ""

   			/* when testing inclusion filters in an XOWriter, the reader is asked
   			 *  to navigate the association in the opposite direction. Return the one object,
   			 *  if it has the correct association to this object */
   			else if (thisEnd == 2)
   			{
   				Node parentNode = oRep.objNode().getParentNode();
   				String pathToThisObject = oRep.subset();
   				String pathToParent = removeLastStep(pathToThisObject); // the subset of parent objectRep
   				String step = lastStep(pathToThisObject);
 
   				// check that the parent class has the named association to the start class
   				EClass parentClass = ModelUtil.getNamedClass(classModel, otherClass);
   				if ((parentClass != null) && (parentClass.getEStructuralFeature(step) != null))
   				{
   					EReference ref = (EReference)parentClass.getEStructuralFeature(step);
   					EClass child = (EClass)ref.getEType();
   					if (ModelUtil.getQualifiedClassName(child).equals(oRep.className()))
   					{
   		  				result.add(new objectRep(parentNode,otherClass,pathToParent,this));
   					}
   				}
   				// if the step does not match, leave the result Vector empty
   				return result;
   			}
   		}
   		/* call with role name specified  */
   		else
   		{
   			// expect the end to be set (conventionally) to -1
   			if (thisEnd != -1) throw new MapperException("Expected end -1 for specified role '" + otherRole + "'");
   			roleName = otherRole;
   			
   		}
   		
   		EStructuralFeature ref = parent.getEStructuralFeature(roleName);
   		if (ref == null) throw new MapperException("Cannot find association " + roleName + " of class " + oTok.className());
   		String otherEndClassName = ModelUtil.getQualifiedClassName((EClass)((EReference)ref).getEType());
		// extend the XPath used as a subset
   		String otherSubset = oRep.subset() + "/" + roleName;

   		Vector<Element> children = XMLUtil.namedChildElements(el, roleName);
   		for (Iterator<Element> ic = children.iterator();ic.hasNext();)
   		{
   			Element child = ic.next();
			objectRep cRep = new objectRep(child,otherEndClassName,otherSubset,this);
			result.add(cRep);
   		}
   		return result;
   }
   
   /**
    * @param path
    * @return the path wit the last step removed
    */
   private String removeLastStep(String path)
   {
	   String result = "";
	   StringTokenizer st = new StringTokenizer(path,"/");
	   while (st.hasMoreTokens())
	   {
		   String step = st.nextToken();
		   if (st.hasMoreTokens()) result = result + "/" + step;
	   }
	   return result;
   }
   
   /**
    * @param path
    * @return the last step of the path
    */
   private String lastStep(String path)
   {
	   String result = "";
	   StringTokenizer st = new StringTokenizer(path,"/");
	   while (st.hasMoreTokens()) result = st.nextToken();
	   return result;
   }
    

   public String parameterClassName() throws MapperException 
   {
	   return ModelUtil.getQualifiedClassName(getEntryClass());
   }

   
	//--------------------------------------------------------------------------------------------
   //	                          metaData methods
	//--------------------------------------------------------------------------------------------


	/**
	 * @return true if the V3 XML represents objects of this class name (qualified).
	 * Assume that if it is in the Ecore model, the V3 represents it.      
	 */
   public boolean representsObject(String className) 
	        	{return (ModelUtil.getNamedClass(classModel, className) != null);}
   
   /**
    * 
    * @param oRep an objectRep
    * @return true if the class is represented with the subset of the objectRep
    * (i.e is represented on a node with that path).
    * The result returned here depends on the XML instance
    */
   private boolean representsObject(objectRep oRep)
   {
	   Hashtable<String,Vector<objectRep>> objectReps = allObjectReps.get(oRep.className());
	   return (objectReps.get(oRep.subset())!= null);
   }

	/**
	 * @return true if the V3 XML represents the property of the class with qualified name       
	 */
   public boolean representsProperty(String className,String property) 
   {
	        	boolean represents = false;
        		EClass theClass = ModelUtil.getNamedClass(classModel, className);
	        	if (theClass != null)
	        	{
	        		for (Iterator<EAttribute> ia = theClass.getEAllAttributes().iterator();ia.hasNext();)
	        			if (ia.next().getName().equals(property)) represents = true;
	        	}
	        	return represents;
	}

   /**
    * A class always has the same properties, no matter what subset is represented
    * (i.e what V3 XML node it is represented on)
    */	        
   public boolean representsProperty(objectRep oRep,String property) 
   {
	   if (representsObject(oRep)) return representsProperty(oRep.className(),property);
	   return false;
   }

   /**
    * if any association exists in the V3 RMIM, it is represented in the XML ITS
    */
   public boolean representsAssociationRole(String class1, String roleName, String class2) 
   {
	   EClass ec1 = ModelUtil.getNamedClass(classModel, class1);
	   EClass ec2 = ModelUtil.getNamedClass(classModel, class2);
	   EStructuralFeature ref = ec1.getEStructuralFeature(roleName);
	   if ((ec1 == null)|(ec2 == null)|(ref == null)) return false;
	   EClass ec3 = (EClass)((EReference)ref).getEType();
	   return (ec3.isSuperTypeOf(ec2));
   }
	    	        
   /**
    * A class always has the same associations, no matter what subset is represented
    * (i.e what node it is represented on)
    */
   public boolean representsAssociationRole(objectRep oRep, String roleName, String class2)
   {
	   if (representsObject(oRep)) return representsAssociationRole(oRep.className(), roleName, class2);
	   return false;
   }
 
   /**
    * When the role name at end 1 is "" (as it always is in the V3 RMIM) the association name
    * is the same as the other end role name       
    */
   public boolean representsAssociation(String class1, String assocName, String class2)
   {
	   return representsAssociationRole(class1, assocName, class2);
   }

	      
   /**
    * @return all classSets represented for a class, in the instance;
    * answer depends on the instance
    */
   public Hashtable<String,ClassSet> subsets(String className)
   {
	   Hashtable<String,ClassSet> subsets = new Hashtable<String,ClassSet>();
	   Hashtable<String,Vector<objectRep>> objectReps = allObjectReps.get(className);
	   if (objectReps != null) try
	   {
		   for (Enumeration<String> en = objectReps.keys();en.hasMoreElements();)
		   {
			   String sub = en.nextElement();
			   ClassSet cs = new ClassSet(className, sub);
			   subsets.put(sub, cs);
		   }
	   }
	   catch (Exception ex) {} // exception creating a ClassSet is not expected
	   return subsets;
   }
	    
	       
   /**
    * @return the qualified class names for the classes with the same name in all packages
    */
   public Vector<String> getQualifiedClassNames(String bareClassName)
   {
	   Vector<String> qualNames = new Vector<String>();
	   // there is only one level of sub-packages in a V3 RMIM
	   for (Iterator<EPackage> ip = classModel.getESubpackages().iterator();ip.hasNext();)
	   {
		   EPackage pack = ip.next();
		   EClass ec = (EClass) pack.getEClassifier(bareClassName);
		   if (ec != null) qualNames.add(ModelUtil.getQualifiedClassName(ec));
	   }
	   return qualNames;
   }

	    
	    //-------------------------------------------------------------------------------------------
	    // 		                       For use by EMFInstanceFactory
	    //-------------------------------------------------------------------------------------------
	 	
		/**
		 * @return classSets of all object mappings in this mapping set (not imported) that 
		 * are not inside a containment relation to some other class
		 * which also has an object mapping to the top mapping set
		 * Key = string form of the [class,subset]. 
		 * For a V3 RMIM, only the entry class counts.
		 */
	        public Vector<ClassSet> outerObjectClassSets()
	        {
	        	Vector<ClassSet> outers = new Vector<ClassSet>();
	        	try
	        	{
	        		String className = ModelUtil.getQualifiedClassName(getEntryClass());
	        		Hashtable<String,Vector<objectRep>> objectReps = allObjectReps.get(className);
	        		if (objectReps != null) for (Enumeration<String> en = objectReps.keys();en.hasMoreElements();)
	        		{
	        			String sub = en.nextElement();
	        			ClassSet cs = new ClassSet(className, sub);
	        			outers.add(cs);
	        		}
	        	}
	        	catch (Exception ex) {} // exception creating a ClassSet is not expected
	        	return outers;
	        }

	        
	        /**
	         * the XOReader which this objectGetter uses
	         * @return
	         */
	        public XOReader reader() throws MapperException {return this;}

		    
			 //-------------------------------------------------------------------------------------------
			 // 		                      XMLWriter interface
			 //-------------------------------------------------------------------------------------------

	        /**
	         * set the XML Output file for the writer
	         * @param xout
	         */
	    	public void setXMLOutputFile(XMLOutputFile xout)
	    	{
	    		super.setXMLOutputFile(xout);
	        	try {setOutputNamespaces();}
	        	catch (MapperException ex) 
	        		{System.out.println("Exception setting output namespaces: " + ex.getMessage());}
	    	}

		    	/**
		    	 * write the object model information from the objectGetter (set in the constructor) 
		    	 * to an output XML
		    	 *
		    	 * @return the root Element of the created XML document
		    	 * @exception MDLWriteException - any major problem detected in making the translation
		    	 */
		    	public Element makeXMLDOM() throws MapperException
		    	{
		    		xout = new XMLOutputFile();
		    		xout.setNSSet(ms.getNamespaceSet());

		    		//the data  source should represent just one object of the entry class
		    		String entryClassName = ModelUtil.getQualifiedClassName(getEntryClass());
		    		Vector<objectToken> rootTokens = oGet.getObjects(entryClassName);
		    		if (rootTokens.size() != 1)
		    			throw new MapperException("Data source represents " + rootTokens.size() 
		    					+ " objects of the RMIM entry class " + entryClassName);

		    		// the root element has the same name as the entry class
		    		Element root = xout.NSElement(V3NAMESPACEPREFIX, getEntryClass().getName(), V3NAMESPACEURI);
		    		// extend it with all child elements etc.
		    		root =  extendXMLDOM(root,rootTokens.get(0));
		    		xout.setTopOut(root);
		    		xout.addNamespaceAttributes();
		    		
		    		// remove the element-ordering attributes, while putting the elements in the right order
		    		return MappedXMLWriter.orderOutputElements(root);
		    	}
		    	
		    	/**
		    	 * Extend some Element of an output XML DOM (which represents some object
		    	 * in the object model, or has an ancestor element which represents that object)
		    	 * producing a subtree which represents the properties of that object, subordinate
		    	 * objects related to it, and their properties.
		    	 * 
		    	 * @param bareElement the Element to be extended
		    	 * @param oTok objectToken for the parameter class object, which the Element 
		    	 * to be extended (or one of its ancestors) represents
		    	 * @return the extended Element
		    	 * @throws MapperException if there is any major problem
		    	 */
		    	public Element extendXMLDOM(Element bareElement, objectToken oTok) throws MapperException
		    	{
		    		// find where you are in the V3 RMIM
		    		EClass theClass = ModelUtil.getNamedClass(classModel, oTok.className());
		    		if (theClass == null) throw new MapperException("Cannot find class " + oTok.className());

		    		// add attributes or text content to the Element for properties of the object
		    		for (Iterator<EAttribute> ia = theClass.getEAllAttributes().iterator();ia.hasNext();)
		    		{
		    			String attName = ia.next().getName();
		    			try {
		    				// will throw a notRepresentedException if the property is not represented in the data source
		    				String value = oGet.getPropertyValue(oTok, attName);
		    				if (!value.equals(""))
		    				{
		    					if (attName.equals("textContent"))
		    					{
		    						Text text = xout.outDoc().createTextNode(value);
		    						bareElement.appendChild(text);
		    					}
		    					else if (attName.equals("element_position"))
		    					{
		    						bareElement.setAttribute(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE, value);
		    					}
		    					else bareElement.setAttribute(attName, value);
		    				}
		    			}
		    			// silent catch if the property is not represented in the data source
		    			catch (MapperException ex) {}
		    		}
		    		
		    		// follow all containment references and recursively add child elements
		    		for (Iterator<EReference> ir = theClass.getEAllReferences().iterator();ir.hasNext();)
		    		{
		    			EReference ref = ir.next();
		    			if (ref.isContainment()) try
		    			{
		    				String otherClass = ModelUtil.getQualifiedClassName((EClass)ref.getEType());

		    				/* The starting end of the association is 1. Because one of the role names is "",
		    				 * the association name is the same as the role name.
		    				 * This call can throw a notRepresentedException  */
		    				Vector<objectToken> tokens = oGet.getAssociatedObjects(oTok, ref.getName(), otherClass, 1);
		    				for (Iterator<objectToken> it = tokens.iterator();it.hasNext();)
		    				{
		    		    		Element childEl = xout.NSElement(V3NAMESPACEPREFIX, ref.getName(), V3NAMESPACEURI);
		    					extendXMLDOM(childEl,it.next());
		    					bareElement.appendChild(childEl);
		    				}
		    			}
		    			// silent catch if the association is not represented in the data source
		    			catch (MapperException ex) {}		    				
		    		}
		    		return bareElement;
		    	}

		    	
		    	/**
		    	 * All issues that were noted when running the translation 
		    	 * outer key = string form of root path
		    	 * Inner key = a unique identifier for the issue
		    	 * @return nothing - the Java notes no issues
		    	 */
		    	public Hashtable<String,Hashtable<String,RunIssue>> allRunIssues()
		    	{
		    		return new Hashtable<String,Hashtable<String,RunIssue>>();
		    	}
		    	


}
