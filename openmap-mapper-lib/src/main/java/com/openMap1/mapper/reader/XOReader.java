package com.openMap1.mapper.reader;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MDLReadException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.Timer;
import com.openMap1.mapper.MappedStructure;

import org.eclipse.emf.ecore.EPackage;
import org.w3c.dom.Node;


import java.util.Vector;
import java.util.Hashtable;

/**
 * This is the interface used to read an XML instance 
 * and convert information in the XML instance into class model terms. 
 * 
 * @author robert
 * @version 1.0
 */
public interface XOReader
{

//--------------------------------------------------------------------------------------------
//                          Data retrieval methods
//--------------------------------------------------------------------------------------------

        
        // the root of the ecore class model which the document is mapped to
        public EPackage classModel();


        /**
         * Vector  of objectTokens for all nodes representing objects
        *  in any subclasses of a given class, in all subsets of those subclasses.
        *
        *  @param className  - the name of the class
        *  @exception MapperException  - class not represented in the XML
        *                - you ignored some exception on creating XOReader
        */
        public Vector<objectToken> getAllObjectTokens(String className) throws MapperException;


        /**
         * For use by EMFInstanceFactory 
         * @param className
         * @return
         * @throws MapperException
         */
        public Vector<objectToken> getAllLocalObjectTokens(String className) throws MapperException;


        /**
         * String value of a property of some represented object
        *
        *  @param oTok  - the objectToken for the object
        *  @param propertyName  - the name of the property
        *
        *  @exception MDLReadException   - XML does not represent the property for this class
        *               - XML does not represent the property for this instance
        *               - XML represents multiple values for the property
        *               - Java class or method for a property format conversion cannot be found
        *               - An input value for a property format conversion is not represented
        */
        public String getPropertyValue(objectToken oTok, String propertyName) throws MapperException;



        /**
         * Vector of objectTokens representing objects related to the current object by some association.
         *
         * @param oTok - the input object at one end of the association
         * @param assocName - name of the association
         * @param otherClass - class or superclass of the objects to be retrieved
         * @param otherRole - the role played by the other-end object in the association
         *
         * @exception MapperException - any argument null
         *                     - the XML does not represent the association between the classes
         */
         public Vector<objectToken> getAssociatedObjectTokens(objectToken oTok,
           String otherClass, String otherRole) throws MapperException;
         

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
            String otherClass, int thisEnd) throws MapperException;

         
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
         public Vector<objectToken> getTheAssociatedObjectReps(objectToken oTok, String assocName,
             String otherClass, int thisEnd, String otherRole) throws MapperException;


    
    /**
     * set the root of the XML instance being read
     * @param el
     * @throws MapperException
     */
         public void setRoot(Node el)  throws MapperException;
    
    /**
     * If the mapping set of this XOReader it imported by some
     * other mapping set, the qualified name of the parameter class.
     * The importing node or some ancestor of it must represent an instance of the 
     * parameter class.
     * @return
     * @throws MapperException
     */
         public String parameterClassName() throws MapperException;
    
    /**
     * The mapper model instance representing the mapping set of this reader
     * @return
     */
         public MappedStructure ms();
    
//--------------------------------------------------------------------------------------------
//                          metaData methods
//--------------------------------------------------------------------------------------------

    /**
     * @return  false if a class or its subclasses are not represented ,
     * in this m apping set or any it imports
     */
         public boolean checkIsRepresented(String className);

         /** 
          * True if the XML directly represents objects of this class (not subclasses) 
          * This method recurses down through all imported mapping sets, which might be expensive.
          * */
    public boolean representsObject(String className);

    /**  true if the property in an actual property (not a converted property)
     *  of one of the subsets of this class.
     *  This method recurses through all imported mapping sets, so may be expensive. */
    public boolean representsProperty(String className,String property);

    /**
     * @param oRep an objectRep for a represented object
     * @param propName a property of the object
     * @return true if the property is represented. 
     * Looks only in the mapping set
     * that represents the object, and those it imports directly.
     * This method assumes that a property can only be represented in the same mapping
     * set as the objectRep, or in one directly imported (where the object class
     * is the parameter class)
     * (assuming that mapping sets do not pass on their parameter classes as parameter
     * class values to other imported mapping sets)
     */
    public boolean representsProperty(objectRep oRep,String property);

    /**
     * @param className
     * @param property
     * @return  true if the property is represented in the mapping set of 
     * this XOReader - not any it imports.
     */
    public boolean representsPropertyLocally(String className,String property);

    /** true if the XML represents this association from class 1 by the named role to class 2,
     * with the classes at either end of the association. 
     * Checks all imported mapping sets , so can be expensive. */
    public boolean representsAssociationRole(String class1, String roleName, String class2);
    
    /**
     * @param oRep an objectRep for a represented object
     * @param roleName an association from the object
     * @param class2 the class at the other end
     * @return true if the association is represented, with the subset in the objectRep. 
     * Looks only in the mapping set
     * that represents the object, and those it imports directly.
     * This method assumes that an association can only be represented in the same mapping
     * set as the objectRep, or in one directly imported (where the object class
     * is the parameter class)
     * (assuming that mapping sets do not pass on their parameter classes as parameter
     * class values to other imported mapping sets)
     */
    public boolean representsAssociationRole(objectRep oRep, String roleName, String class2);

    /** true if the XML represents this association from class 1 by the named role to class 2,
     * with the classes at either end of the association.  */
    public boolean representsAssociationRoleLocally(String class1, String roleName, String class2);
    
    /**
     * Obsolete method that uses the 'association name' which is 
     * now a compound of the two role names for each end of the association.
     * @param class1
     * @param assocName
     * @param class2
     * @return true if the XML represents the association
     */
    public boolean representsAssociation(String class1, String assocName, String class2);

    /**
     * all the different subsets of the class which are represented
     * in the mapping set of this XOReader - not the imported ones.
     */
    public Hashtable<String,ClassSet> subsets(String className);
    
    /**
     * A  qualified class name is the class name preceded by the package name
     * @param bareClassName
     * @return all qualified class names that have this bare class name
     */
    public Vector<String> getQualifiedClassNames(String bareClassName);

    
    //-------------------------------------------------------------------------------------------
    // 		                       For use by EMFInstanceFactory
    //-------------------------------------------------------------------------------------------
 	
	/**
	 * @return [class,subset] all object mappings in this mapping set (not imported) that 
	 * are not inside a containment relation to some other class
	 * which also has an object mapping to the top mapping set
	 * Key = string form of the [class,subset]. 
	 */
 	public Vector<ClassSet> outerObjectClassSets();

    
    //-------------------------------------------------------------------------------------------
    // 		                       Performance monitoring
    //-------------------------------------------------------------------------------------------
 	
 	/**
 	 * Give this XOReader a timer, so the times it takes for different operations
 	 * can be reported alongside other times.
 	 */
 	public void giveTimer(Timer timer, boolean addTimes);
 	
 	/**
 	 * the timer on this XOReader
 	 * @return
 	 */
 	public Timer timer();

}
