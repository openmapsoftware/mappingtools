package com.openMap1.mapper.reader;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.openMap1.mapper.core.ClassSet;

/**
 * objectToken derived from an ECore model
 * (developed for use in the FHIR mapper interface)
 * @author Robert
 *
 */

public class EObjectRep implements objectToken{

    private EObject theObject;
    private String className; // qualified class name  - includes package name
    private XOReader reader; // the XOReader which found this objectRep
    
    private String subset;
    
    public boolean isEmpty() {return isEmpty;}
    private boolean isEmpty;
    
    
   /**
    * constructor for an object which exists in a class model
    * @param theObject
    * @param reader
    */
    public EObjectRep(EObject theObject,XOReader reader)
   {
	   this.theObject = theObject;
	   EClass theClass = theObject.eClass();
	   className = theClass.getEPackage().getName() + "." +  theClass.getName();
	   subset = getPath(theObject);
	   this.reader = reader;
	   isEmpty = false;
   }
    
    /**
     * constructor for an empty EObjectRep
     */
    public EObjectRep()
    {
    	isEmpty = true;
    }
   
   // the mapping subset is the path of containment relations in the instance to get to the object
   private String getPath(EObject theObject)
   {
	   String path = "";
	   EObject outer = theObject.eContainer();
	   if (outer != null)
	   {
		   for (Iterator<EStructuralFeature> it = outer.eClass().getEStructuralFeatures().iterator();it.hasNext();)
		   {
			   EStructuralFeature feat = it.next();
			   if (feat instanceof EReference)
			   {
				   EReference ref = (EReference)feat;
				   Object value = outer.eGet(ref);
				   if ((value != null) && (ref.isContainment()))
				   {
					   if (ref.getUpperBound() == 1)
					   {
						   if (value == theObject) path = getPath(outer) + ref.getName() + ".";
					   }
					   if (ref.getUpperBound() == -1)
					   {
						   List<EObject> lVal = (List<EObject>) value;
						   for (Iterator<EObject> iu = lVal.iterator();iu.hasNext();)
						   {
							   EObject next = iu.next();
							   if (next == theObject) path = getPath(outer) + ref.getName() + ".";
						   }
					   }
				   }
			   }
		   }
	   }
	   return path;
   }

   /**
    * 
    */
   public String className() {
		return className;
	}
	

    /** the subset of the represented object */
    public String subset(){return subset;}

	
    /** return ClassSet ( = class and subset) in the source of the object - e.g. the XML
    source document where the object is represented. */
    public ClassSet cSet()
    {
    	ClassSet cs = null;
        try {cs = new ClassSet(className,subset);} 
        catch (Exception ex) {} // null class name or subset are impossible - constructor
        return cs;
    }


	public Object objectKey() {
		return theObject;
	}

	public XOReader reader() {
		return reader;
	}
	
	public EObject theObject() {return theObject;}

}
