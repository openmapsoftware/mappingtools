package com.openMap1.mapper.reader;


import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MDLReadException;
import com.openMap1.mapper.core.MDLWriteException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.RunIssue;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.Timer;
import com.openMap1.mapper.util.XMLOutputFile;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.writer.XMLWriter;
import com.openMap1.mapper.writer.objectGetter;
import com.openMap1.mapper.MappedStructure;

import org.eclipse.emf.ecore.EPackage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


import java.util.Vector;
import java.util.Hashtable;

/**
 * This class can  be subclassed to produce Java implementations
 * of XOReader (to read from XML into an object model)
 * and XMLWriter (to write from an object model to XML)
 * in cases where either the mappings have a regular structure
 * or you want to do something that mappings will not do.
 * 
 * This class provides two constructors that can be inherited
 * (for the reader and writer implementations respectively)
 * and implementations of some of the more boring methods in the XOReader
 * interface.
 * 
 * @author robert
 *
 */

abstract public class AbstractReaderWriter implements XOReader, objectGetter, XMLWriter{
	
	protected Element XMLFileRoot;
	
	protected MappedStructure ms;
	
	protected EPackage classModel;
	
	protected messageChannel mChan;
	
	protected objectGetter oGet;
	
	protected boolean doRunTracing;
	
	protected XMLOutputFile xout;
	
	private Timer timer;
	
	public Timer timer() {return timer;}

	   
	/**
	 * Give this XOReader or XMLWriter a timer, so the times it takes for different operations
	 * can be reported alongside other times.
	 */
   public void giveTimer(Timer newTimer, boolean addTimes)
   {
	   if ((addTimes) && (timer != null)) newTimer.addTimes(timer);
	   timer = newTimer;
   }

	//--------------------------------------------------------------------------------------------
    //	                                Constructors
	//--------------------------------------------------------------------------------------------
	
    /**
     * Constructor for implementations of XOReader, objectGetter
     */
	public AbstractReaderWriter(Element XMLFileRoot, MappedStructure ms, 
    		EPackage classModel, messageChannel mChan)  
    throws MapperException
    {
    	this.XMLFileRoot = XMLFileRoot;
    	this.ms = ms;
    	this.classModel = classModel;
    	this.mChan = mChan;
    	timer = new Timer("Reader");
    }

	
	/**
	 * Constructor for use as an XMLWriter
	 * @param oGet
	 * @param ms
	 * @param classModel
	 * @param mChan
	 * @param doRunTracing
	 * @throws MapperException
	 */
	public AbstractReaderWriter(objectGetter oGet, MappedStructure ms, 
    		EPackage classModel, messageChannel mChan, Boolean doRunTracing)  
    throws MapperException
    {
    	this.oGet = oGet;
    	this.ms = ms;
    	this.classModel = classModel;
    	this.mChan = mChan;
    	this.doRunTracing = doRunTracing.booleanValue();
    	timer = new Timer("Writer");
    }


	//--------------------------------------------------------------------------------------------
    //	                          Data retrieval methods
	//--------------------------------------------------------------------------------------------

	        
	        // the root of the Ecore class model which the document is mapped to
	        public EPackage classModel()  {return classModel;}


	        /**
	         * Vector  of objectTokens for all nodes representing objects
	        *  in any subclasses of a given class, in all subsets of those subclasses.
	        *
	        *  @param className  - the name of the class
	        *  @exception MDLReadException  - class not represented in the XML
	        *                - you ignored some exception on creating XOReader
	        */
	        abstract public Vector<objectToken> getAllObjectTokens(String className) throws MapperException;

	        
	        /**
	         * return a Vector of objectTokens for all objects in the class which you want written out
	         * in an XML document by OXWriter.
	         * These must represent each distinct object only once.
	         * The implementation here assumes the Java class returns each distinct object only once
	         */
	        public Vector<objectToken> getObjects(String className)  throws MapperException
	        	{return getAllObjectTokens(className);}


	        /**
	         * For use by EMFInstanceFactory 
	         * @param className
	         * @return
	         * @throws MapperException
	         */
	        public Vector<objectToken> getAllLocalObjectTokens(String className) throws MapperException
	        	{return getAllObjectTokens(className);}


	        /**
	         * String value of a property of some represented object
	        *
	        *  @param oRep  - the objectToken for the object
	        *  @param propertyName  - the name of the property
	        *
	        *  @exception MDLReadException   - XML does not represent the property for this class
	        *               - XML does not represent the property for this instance
	        *               - XML represents multiple values for the property
	        *               - Java class or method for a property format conversion cannot be found
	        *               - An input value for a property format conversion is not represented
	        */
	        abstract public String getPropertyValue(objectToken oTok, String propertyName) throws MapperException;



	        /**
	         * Vector of objectTokens representing objects related to the current object by some association.
	         *
	         * @param oRep - the input object at one end of the association
	         * @param assocName - name of the association
	         * @param otherClass - class or superclass of the objects to be retrieved
	         * @param otherRole - the role played by the other-end object in the association
	         *
	         * @exception MDLReadException - any argument null
	         *                     - the XML does not represent the association between the classes
	         */
	        public Vector<objectToken> getAssociatedObjectTokens(objectToken oTok,
	           String otherClass, String otherRole) throws MapperException
	        {
	        	String assocName = ModelUtil.getAssociationName(oTok.className(), otherRole, otherClass,ms());
	            return getTheAssociatedObjectReps(oTok,assocName, otherClass, -1, otherRole);
	        }
	         

	         /**
	          * Vector of objectTokens representing objects related to the current object by some association.
	          *
	          * @param oRep - the input object at one end of the association
	          * @param assocName - name of the association
	          * @param otherClass - class or superclass of the objects to be retrieved
	          * @param thisEnd - end of the association for the input object, = 1 or 2
	          *
	          * @exception MDLReadException - any argument null
	          *                     - the XML does not represent the association between the classes
	          */
	        public Vector<objectToken> getAssociatedObjectTokens(objectToken oTok, String assocName,
	            String otherClass, int thisEnd) throws MapperException
	        {
	            return getTheAssociatedObjectReps(oTok,assocName,otherClass, thisEnd, "");
	        }


	        /**
	         * return a Vector of objectTokens for objects related to this object by some association
	         * Throw a notRepresentedException if the source does not represent the association or
	         * the class at the other end.
	         * If anything else goes wrong, throw an MDLReadException.
	         * @param oneOrTwo: end of the association which we are starting from
	         * This method is just a simple renaming of a method above, required for
	         * the objectGetter interface.
	         */
	        public Vector<objectToken> getAssociatedObjects(objectToken oTok, String relation, String otherClass,
	               int oneOrTwo) throws MapperException
	        {
	        	return getAssociatedObjectTokens(oTok, relation, otherClass,oneOrTwo);
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
	          */
	        abstract public Vector<objectToken> getTheAssociatedObjectReps(objectToken oTok, String assocName,
	             String otherClass, int thisEnd, String otherRole) throws MapperException;
	    
	        public void setRoot(Node el)  throws MapperException
	          {XMLFileRoot = (Element)el;}
	    
	        abstract public String parameterClassName() throws MapperException;
	    
	        public MappedStructure ms() {return ms;}
	    
	//--------------------------------------------------------------------------------------------
    //	                          metaData methods
	//--------------------------------------------------------------------------------------------

	        public boolean checkIsRepresented(String className)
	        	{return representsObject(className);}

	        abstract public boolean representsObject(String className);

	        abstract public boolean representsProperty(String className,String property);

	        abstract public boolean representsProperty(objectRep oRep,String property);

	        public boolean representsPropertyLocally(String className,String property)
	        	{return representsProperty(className, property);}

	        abstract public boolean representsAssociationRole(String class1, String roleName, String class2);
	    
	        abstract public boolean representsAssociationRole(objectRep oRep, String roleName, String class2);

	        public boolean representsAssociationRoleLocally(String class1, String roleName, String class2)
	        	{return representsAssociationRole(class1, roleName, class2);}
	    
	        abstract public boolean representsAssociation(String class1, String assocName, String class2);

	        abstract public Hashtable<String,ClassSet> subsets(String className);
	    
	        abstract public Vector<String> getQualifiedClassNames(String bareClassName);

	    
	    //-------------------------------------------------------------------------------------------
	    // 		                       For use by EMFInstanceFactory
	    //-------------------------------------------------------------------------------------------
	 	
		/**
		 * @return ClassSets of all object mappings in this mapping set (not imported) that 
		 * are not inside a containment relation to some other class
		 * which also has an object mapping to the top mapping set
		 * Key = string form of the [class,subset]. 
		 */
	        abstract public Vector<ClassSet> outerObjectClassSets();
	        
	        
		    
		 //-------------------------------------------------------------------------------------------
		 // 		                      XMLWriter interface
		 //-------------------------------------------------------------------------------------------


	    	/**
	    	 * write the object model information from the objectGetter (set in the constructor) 
	    	 * to an output XML
	    	 *
	    	 * @return the root Element of the created XML document
	    	 * @exception MDLWriteException - any major problem detected in making the translation
	    	 */
	    	abstract public Element makeXMLDOM() throws MapperException;
	    	
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
	    	abstract public Element extendXMLDOM(Element bareElement, objectToken oTok) throws MapperException;

	    	
	    	/**
	    	 * All issues that were noted when running the translation 
	    	 * or generating XSLT. 
	    	 * outer key = string form of root path
	    	 * Inner key = a unique identifier for the issue
	    	 * @return
	    	 */
	    	abstract public Hashtable<String,Hashtable<String,RunIssue>> allRunIssues();
	    	
	        /**
	         * set the XML Output file for the writer
	         * @param xout
	         */
	    	public void setXMLOutputFile(XMLOutputFile xout)
	    	{this.xout = xout;}


}
