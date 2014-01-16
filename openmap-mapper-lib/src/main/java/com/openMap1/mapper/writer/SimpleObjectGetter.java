package com.openMap1.mapper.writer;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.w3c.dom.Node;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.reader.SimpleObjectRep;
import com.openMap1.mapper.structures.MappableAssociation;

import com.openMap1.mapper.util.ModelUtil;

/**
 * SimpleObjectGetter provides objects, properties and links from a class model  
 * instance to an XMLWriter, having allowed this class model instance to be set up first
 * by atomic calls to add objects, properties and links. 
 * @author robert
 *
 */
public class SimpleObjectGetter implements objectGetter {
	
	/* key = className; value = a Hashtable whose key is the object key and whose value is
	 * the objectToken. */
	private Hashtable <String , Hashtable<String,objectToken>> objects;
	
	/* key = object key; value = a Hashtable whose key is a property name and whose value 
	 * id the value of the property for the object. */
	private Hashtable<String,Hashtable<String,Object>> properties;
	
	/* key = first object key; value = a Hashtable whose key is a role name and whose 
	 * value is a Hashtable (key: second object key; value: second object token) */
	private Hashtable<String,Hashtable<String,Hashtable<String,objectToken>>> links;
	
	/* For use in looking up associations by association name.
	 * key = association name. value = role name at end 1 */
	private Hashtable<String,String> end1Role;

	/* For use in looking up associations by association name.
	 * key = association name. value = role name at end 2 */
	private Hashtable<String,String> end2Role;
	
	private EPackage classModel;
	private int keyIndex;
	
	private boolean tracing = false;
	private void trace(String s) {if (tracing) System.out.println(s);}
	
	//-----------------------------------------------------------------------------------
	//                              constructor
	//-----------------------------------------------------------------------------------
	
	public SimpleObjectGetter(EPackage classModel)
	{
		this.classModel = classModel;
		keyIndex = 0;
		initialise();
	}
	
    /**
     * set the root of the XML instance being read
     * As there is no XML instance , this has no effect; it is just there to satisfy
     * the objectGetter interface.
     * @param el
     * @throws MapperException
     */
     public void setRoot(Node el)  throws MapperException
     {
     }

	
	/**
	 * initialise this objectGetter to have an empty class model instance
	 */
	public void initialise()
	{
		objects = new Hashtable<String, Hashtable<String,objectToken>>();
		properties = new Hashtable<String,Hashtable<String,Object>>();
		links = new Hashtable<String,Hashtable<String,Hashtable<String,objectToken>>>();
		end1Role = new Hashtable<String,String>();
		end2Role = new Hashtable<String,String>();
	}
	
	//-----------------------------------------------------------------------------------
	//                methods to build up the class model instance
	//-----------------------------------------------------------------------------------
	
	/**
	 * add a new object to the class model
	 * @param className the class of the object
	 * @return the objectToken for the new object 
	 * - to be used to set its properties and associations
	 * @exception MapperException if the class is not in the class model
	 */
	public objectToken addObject(String className) throws MapperException
	{
		trace("SimpleObjectGetter adding object of class " + className);
		if (!classIsInModel(className)) 
			throw new MapperException("Class '" + className + "' is not in the class model.");

		SimpleObjectRep oRep = new SimpleObjectRep(className, nextKey());
		Hashtable<String,objectToken> inThisClass = objects.get(className);
		if (inThisClass == null) inThisClass = new Hashtable<String,objectToken>();
		inThisClass.put((String)oRep.objectKey(), oRep);
		objects.put(className,inThisClass);
		return oRep; 
	}
	
	private String nextKey()
	{
		String key = "k_" + keyIndex;
		keyIndex++;
		return key;
	}
	
	/**
	 * set the value of a property of an object
	 * @param obj the object whose property is to be set
	 * @param propName the name of the property
	 * @param PropValue the new value of the property
	 * @throws MapperException if the property is not in the class model or the object does not exist.
	 */
	public void setProperty(objectToken obj, String propName, Object propValue)
	throws MapperException
	{
		if (!propertyIsInModel(obj.className(),propName))
			throw new MapperException("Property '" + propName + "' of class '" 
					+ obj.className() + "' is not in the class model.");
		if (!objectExists(obj))
			throw new MapperException("Cannot set property '" 
					+ propName + "' because the object does not exist.");
		if (!(propValue instanceof String))
			throw new MapperException("Only String values of property '" + propName + "' are handled");

		Hashtable<String,Object> objProps = properties.get(obj.objectKey());
		if (objProps == null) objProps = new Hashtable<String,Object>();
		objProps.put(propName, propValue);
		properties.put((String)obj.objectKey(), objProps);
	}
	
	/**
	 * record a link (association instance) between two objects, and the inverse link if it exists
	 * @param obj1
	 * @param role
	 * @param obj2
	 * @throws MapperException if the association does not exist.
	 */
	public void addLink(objectToken obj1, String role, objectToken obj2)
	throws MapperException
	{
		trace("Adding association "+ obj1.className() + ">" + role + ">" + obj2.className());
		if (!associationIsInModel(obj1.className(),role,obj2.className()))
			throw new MapperException("There is no association '"
					+ role + "' from class '" + obj1.className() 
					+ "' to class '" + obj2.className()+ "'");
		addAssociation(obj1,role,obj2);

		String inverseName = MappableAssociation.NON_NAVIGABLE_ROLE_NAME;
		EReference inverse = inverseAssociation(obj1.className(),role,obj2.className());
		if (inverse != null)
		{
			addAssociation(obj2, inverse.getName(),obj1);
			inverseName = inverse.getName();
		}
		/*
		else throw new MapperException("The association '"
					+ role + "' from class '" + obj1.className() 
					+ "' to class '" + obj2.className()+ "' has no inverse.");
		*/
		
		saveAssociationName(role,inverseName);
	}
	
	/* store a link in the Hashtable of links */
	private void addAssociation(objectToken obj1, String role, objectToken obj2)
	{
    	/* key = first object key; value = a Hashtable whose key is a role name and whose 
    	 * value is a Hashtable (key: second object key; value: second object token) 
    	private Hashtable<String,Hashtable<String,Hashtable<String,objectToken>>> links; */

		Hashtable<String,Hashtable<String,objectToken>> linksForObject = links.get(obj1.objectKey());
		if (linksForObject == null)
			linksForObject = new Hashtable<String,Hashtable<String,objectToken>>();
		
		Hashtable<String,objectToken> linksForRole = linksForObject.get(role);
		if (linksForRole == null) 
			linksForRole = new Hashtable<String,objectToken>();
		
		linksForRole.put((String)obj2.objectKey(), obj2);
		linksForObject.put(role, linksForRole);
		links.put((String)obj1.objectKey(), linksForObject);
	}
	
	/**
	 * 
	 * @param role
	 * @param invRole
	 */
	private void saveAssociationName(String role, String invRole)
	{
		String assocName = ModelUtil.assocName(role, invRole);
		end1Role.put(assocName,ModelUtil.end1Role(role, invRole));
		end2Role.put(assocName,ModelUtil.end2Role(role, invRole));
	}
	
	
	//-----------------------------------------------------------------------------------
	//                methods in the objectGetter interface
	//-----------------------------------------------------------------------------------

    /**
     * return a Vector of objectTokens for all objects in the class which you want written out
     * in an XML document by OXWriter.
     * The Vecotr includes objects in subclasses of the named class
     * These must represent each distinct object only once.
     * If it cannot return a Vector of unique objectTokens for distinct objects,
     * or if the source does not represent the class at all, throws a notRepresentedException.
     * If anything else goes wrong, throw an MDLReadException.
     */
    public Vector<objectToken> getObjects(String className)  throws MapperException
    {
		if (!classIsInModel(className)) 
			throw new MapperException("Class '" + className + "' is not in the class model.");
    	Vector<objectToken> result = new Vector<objectToken>(); 

    	EClass namedClass = getClass(className);
    	for (Iterator<EClass> it = ModelUtil.getAllSubClasses(namedClass).iterator();it.hasNext();)
    	{
    		EClass subClass = it.next();
    		String subclassName = ModelUtil.getQualifiedClassName(subClass);
        	Hashtable<String,objectToken> classObjects = objects.get(subclassName);
        	if (classObjects != null)
        		for (Enumeration<objectToken> en = classObjects.elements(); en.hasMoreElements();)
        			result.add(en.nextElement());
    	}
    	return result;    	
    }

    /**
     * return the value of a property of an object, given its objectToken.
     * Throw a notRepresentedException if the source does not represent the property.
     * If anything else goes wrong, throw an MDLReadException.
     */
    public String getPropertyValue(objectToken obj, String propertyName) throws MapperException
    {
    	String value = "";
		if (!propertyIsInModel(obj.className(),propertyName))
			throw new MapperException("Property '" + propertyName + "' of class '" 
					+ obj.className() + "' is not in the class model.");
		Hashtable<String,Object> objProps = properties.get(obj.objectKey());
		if (objProps == null) return value;
		Object valObj = objProps.get(propertyName);
		if (valObj == null) return value;
		if (!(valObj instanceof String))
			throw new MapperException("Only String values of property '" + propertyName + "' are handled");
		value = (String)valObj;	
		trace("Returning value '" + value + "' for property " + propertyName);
    	return value;
    }

    /**
     * return a Vector of objectTokens for objects related to this object by some association,
     * which are all in subclasses of some named class.
     * If anything  goes wrong, throw a MapperException.
     */
    public Vector<objectToken> getAssociatedObjects(objectToken oTok, String relation, String otherClassName,
           int oneOrTwo) throws MapperException
    {
    	trace("Follow association " + oTok.className() + " > " + relation + " > " + otherClassName + " end " + oneOrTwo);
    	Vector<objectToken> result = new Vector<objectToken>();

    	EClass otherClass = getClass(otherClassName);
    	if (otherClass == null) 
    		throw new MapperException("Unknown other end class '" + otherClassName +
    				"' for association '" + relation + "'");

    	/* the relation name will  not be recognised if there are no instances of it; 
    	 * so do not throw an exception. */
    	String role1 = end1Role.get(relation);
    	String role2 = end2Role.get(relation);
    	trace("Roles: " + role1 + " and " + role2);
    	if ((role1 == null)|(role2 == null)) return result;
    		

    	/* oneOrTwo is defined to be the end of the start object. 
    	 * The Hashtable links has recorded the role name to get to the other end object.
    	 * If the start object is end 1, the other end object is end 2, so we need role2 to get to it. */
    	String role = role1;
    	if (oneOrTwo == 1) role = role2;

    	
    	/* key = first object key; value = a Hashtable whose key is a role name and whose 
    	 * value is a Hashtable (key: second object key; value: second object token) 
    	private Hashtable<String,Hashtable<String,Hashtable<String,objectToken>>> links; */

    	Hashtable<String,Hashtable<String,objectToken>> linksForObj = links.get(oTok.objectKey());
    	if (linksForObj == null) return result;
    	Hashtable<String,objectToken> linksForRole = linksForObj.get(role);
    	if (linksForRole == null) return result;
    	/* get all objects atthe other end of the association which are in 
    	 * subclasses of the named end class. */
    	for (Enumeration<objectToken> en = linksForRole.elements();en.hasMoreElements();)
    	{
    		objectToken ot = en.nextElement();
    		EClass actOtherClass = getClass(ot.className());
    		if (otherClass.isSuperTypeOf(actOtherClass)) result.add(ot);
    	}

    	trace("results: " + result.size());
    	return result;    	
    }


	//-----------------------------------------------------------------------------------
	//                           checks against the class model
	//-----------------------------------------------------------------------------------
	
	/**
	 * check the cardinalities of every Attribute and Association in the model instance
	 */
	public void checkCardinalties() throws MapperException
	{
		String errorList = "";
		for (Enumeration<String> en = objects.keys();en.hasMoreElements();)
		{
			String className = en.nextElement();

			// a bad class should have been detected earlier
			EClass objClass = getClass(className);
			if (objClass == null) throw new MapperException ("Unexpected class '" + className + "'");
			String classPreface = "An object of class '" + className + "' ";
			
			// loop over all objects in the class
			Hashtable<String,objectToken> inClass = objects.get(className);
			for (Enumeration<objectToken> ep = inClass.elements();ep.hasMoreElements();)
			{
				objectToken obj = ep.nextElement();
				
				// check that the object has all mandatory attributes
				for (Iterator<EAttribute> it = objClass.getEAllAttributes().iterator();it.hasNext();)
				{
					EAttribute att = it.next();
					if (att.getLowerBound() == 1)
					{
						String val = getPropertyValue(obj,att.getName());
						if (val.equals("")) errorList = errorList + classPreface +
								"has no mandatory property '" + att.getName() + "';";
					}
				}
				
				/* check the min and max cardinalities of all associations. 
				 * These refer to all possible classes at the other end of the association; 
				 * so the counts sum over all other end classes. */
				for (Iterator<EReference> it = objClass.getEAllReferences().iterator();it.hasNext();)
				{
					EReference ref = it.next();
					int min = ref.getLowerBound();
					int max = ref.getUpperBound();
					int actual = 0;

					/* key = first object key; value = a Hashtable whose key is a role name and whose 
			    	 * value is a Hashtable (key: second object key; value: second object token) 
			    	private Hashtable<String,Hashtable<String,Hashtable<String,objectToken>>> links; */

					Hashtable<String,Hashtable<String,objectToken>> linksForObj = links.get(obj.objectKey());
					if (linksForObj != null)
					{
						Hashtable<String,objectToken> linksForRole = linksForObj.get(ref.getName());
						if (linksForRole != null) actual = linksForRole.size();
					}
					
					if ((actual ==0) && (min == 1))
						errorList = errorList + classPreface +
						   "has a missing mandatory association '" + ref.getName() + "';";
					if ((actual > 1) && (max == 1))
						errorList = errorList + classPreface +
						   "has cardinality " + actual + " (>1) for association '" + ref.getName() + "';";
				}
			}
		}
		if (errorList.length() > 0) throw new MapperException(errorList);
	}
    
    /**
     * true if the named class is in the class model
     * FIXME - yet to deal with nested packages
     */
    private boolean classIsInModel(String qualifiedClassName)
    {
    	return (ModelUtil.getNamedClass(classModel,qualifiedClassName) != null);
    }
    
    /**
     * 
     * @param className
     * @return the named EClass
     */
    private EClass getClass(String className)
    {
    	return ModelUtil.getNamedClass(classModel, className);
    }
    
    /**
     * true if the named property is in the class model
     */
    private boolean propertyIsInModel(String qualifiedClassName, String propName)
    {
    	EClass theClass = ModelUtil.getNamedClass(classModel, qualifiedClassName);
    	if (theClass == null) return false;
    	for (Iterator<EAttribute> it = theClass.getEAllAttributes().iterator();it.hasNext();)
    	    if (it.next().getName().equals(propName)) return true;
    	return false;    	
    }
    
    private boolean objectExists(objectToken obj)
    {
    	Hashtable<String,objectToken> objectsInClass = objects.get(obj.className());
    	if (objectsInClass == null) return false;
    	return (objectsInClass.get(obj.objectKey()) != null);
    }
    
    /**
     * @param class1
     * @param role
     * @param class2
     * @return true if the named association between the two  classes is in  the model
     */
    private boolean associationIsInModel(String class1, String role, String class2)
    {
    	boolean isInModel = false;
    	EClass c1 = ModelUtil.getNamedClass(classModel, class1);
    	EClass c2 = ModelUtil.getNamedClass(classModel, class2);
    	if (c1 == null) return false;
    	if (c2 == null) return false;
    	for (Iterator<EReference> it = c1.getEAllReferences().iterator();it.hasNext();)
    	{
    		EReference er = it.next();
    		if (er.getName().equals(role))
    		{
    			EClassifier target = er.getEType();
    			if ((target instanceof EClass) && (((EClass)target).isSuperTypeOf(c2)))
    				isInModel = true;
    		}
    	}
    	return isInModel;
    }
    
    /**
     * 
     * @param class1
     * @param role
     * @param class2
     * @return the inverse association, if there is one and if the inverse exists
     */
    private EReference inverseAssociation(String class1, String role, String class2)
    {
    	EReference ref = null;
    	EClass c1 = ModelUtil.getNamedClass(classModel, class1);
    	EClass c2 = ModelUtil.getNamedClass(classModel, class2);
    	if (c1 == null) return null;
    	if (c2 == null) return null;
    	for (Iterator<EReference> it = c1.getEAllReferences().iterator();it.hasNext();)
    	{
    		EReference er = it.next();
    		if (er.getName().equals(role))
    		{
    			EClassifier target = er.getEType();
    			if ((target instanceof EClass) && (((EClass)target).isSuperTypeOf(c2)))
    				ref = er.getEOpposite();
    		}
    	}
    	return ref;

    }
    
    private boolean checking = true;
    
    public XOReader reader() throws MapperException
    {
    	if (checking) throw new MapperException("SimpleObjectGetter does not implement method reader()");
    	return null;
    }
    
    
}
