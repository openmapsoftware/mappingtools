package com.openMap1.mapper.writer;

import com.openMap1.mapper.mapping.*;
import com.openMap1.mapper.core.*;
import com.openMap1.mapper.reader.*;


import java.util.*;

import org.w3c.dom.Node;


/**
 *  returns objects (as objectReps) properties and associations
 * from an input XML file, using mappings and XOReader to do so. 
 * */
public class XMLObjectGetter implements objectGetter
{
    private MDLXOReader xread; // an XML to object reader, primed with appropriate MDL.
    public MDLXOReader inputXMLReader() {return xread;}

    public XMLObjectGetter(MDLXOReader xr)
    {
        xread = xr;
    }
    
    /**
     * set the root of the XML instance being read
     * @param el
     * @throws MapperException
     */
         public void setRoot(Node el)  throws MapperException
         {
        	 xread.setRoot(el);
         }

    
    /* get a vector of objectReps for all objects in a class.
    These must be unique,  i.e. represent each distinct object only once,
    even though the XML source may not represent objects uniquely.
    If the XML source does not represent objects uniquely, and it
    does not represent any combination of primary key fields to make them
    unique, then this method must throw a notRepresentedException,
    to conform to the objectGetter interface.
    If the source represents objects uniquely, just get them from it.
    If not, look for a set of primary key fields the source represents; make a Hashtable
    of objectTokens keyed by the primary key.
    */
    /**
     * get a vector of objectReps for all objects in a class.
     * These must be unique,  i.e. represent each distinct object only once,
     * even though the XML source may not represent objects uniquely.
     * If the XML source does not represent objects uniquely, and it
     * does not represent any combination of primary key fields to make them
     * unique, then this method must throw a notRepresentedException,
     * to conform to the objectGetter interface.
     * If the source represents objects uniquely, just get them from it.
     * If not, look for a set of primary key fields the source represents; make a Hashtable
     * of objectTokens keyed by the primary key.
     * 
     * Note this method only looks in the top mapping set - 
     * it does not search recursively through all imported
     * mapping sets
     */
    public Vector<objectToken> getObjects(String className) throws MapperException
    {
        Vector<objectToken> res = null;
        // search only for mappings in the top mapping set
        Vector<objectToken> allReps = xread.getAllLocalObjectTokens(className);
        Vector<objectToken> allTokens = new Vector<objectToken>();
        for (int i = 0; i < allReps.size();i++) allTokens.add(allReps.get(i));
        if (uniqueObject(className))
            {res = allTokens;}
        else
        {
            /* Do not demand that primary key properties be represented non-optionally
            by the source. If two objects both have a missing primary key property, they
            may be treated as the same object. */
            Vector<String> primeKey = primaryKey(className);
            if (primeKey != null)
            {
                Hashtable<String, objectToken> tokens = new Hashtable<String, objectToken>();
                /* store in a Hashtable keyed by concatenated primary key property values,
                to remove duplicates. */
                for (int i = 0; i < allTokens.size(); i++)
                {
                    objectToken oTok = (objectToken)allTokens.elementAt(i);
                    String key = primeKeyString(oTok,primeKey);
                    tokens.put(key,oTok);
                }
                // retrieve from the Hashtable into the result Vector
                res = new Vector<objectToken>();
                for (Enumeration<objectToken> en = tokens.elements(); en.hasMoreElements();)
                {
                    objectToken ot = en.nextElement();
                    res.addElement(ot);
                }
            }
            else
              {throw new notRepresentedException(
                    "Expected primary key properties are not available from the source.");}
        }
        return res;
    }
    /*  whether each object in the class is represented uniquely
    (if false, the source may return several versions of the same object).
    True only if objects are uniquely represented for all represented subsets
    and subclasses.     */
    public boolean uniqueObject(String className)
    {
        int i;
        boolean res = true;
        ClassSet cs;
        objectMapping om;
        Vector<ClassSet> scs = xread.subClassSets(className);
        for (i = 0; i < scs.size(); i++)
        {
            cs = scs.elementAt(i);
            om = xread.namedObjectMapping(cs);
            /* if any subclass or subset is represented non-uniquely,
            you cannot guarantee unique representation. */
            if ((om != null) && (!om.isUnique())) res = false;
       }
        return res;
    }
    /* If the object source is aware of any combination of properties
    which constitute a unique identifier for objects of the class, and if it
    is capable of returning those properties, return a Vector of the property names.
    Some of the properties may be optional.
    Otherwise return null. */
    private Vector<String> primaryKey(String className) throws MDLReadException
    {
        Vector<String> pKey = null;
        Vector<Vector<String>> candidates = xread.uniqueIdentifiers(className);
        if (candidates != null)
        {
            boolean found = false;
            for (int i = 0; i < candidates.size(); i++) if (!found)
            {
            	Vector<String> candidateKey = candidates.elementAt(i);
                boolean OK = true;
                for (int j = 0; j < candidateKey.size(); j++)
                {
                    String field = candidateKey.elementAt(j);
                    if (!representsProperty(className,field))  OK = false;
                }
                if (OK)
                {
                    found = true;
                    pKey = candidateKey;
                }
            }
        }
        return pKey;
    }
    /* construct a key string unique to each unique object, by concatenating the values
    of a set of unique identifier properties, with some constant garbage in between
    to minimise the probability of accidental duplicates. */
    private String primeKeyString(objectToken oTok, Vector<String> keyProperties) throws MapperException
    {
        String res = "key";
        for (int i = 0; i < keyProperties.size(); i++)
        {
            String propName = (String)keyProperties.elementAt(i);
            String propVal = getPropertyValue(oTok,propName);
            res = res + "$%" + propName + "[" + propVal + "]";
        }
        return res;
    }
    /* whether the source represents the property.
    If it represents subsets of the class or subclasses,
    returns true if the property is represented for any subsets and subclasses
    i.e. if it can ever be returned.
    Not true for converted properties.
    True for any property not directly represented, but given by a format
    conversion from represented converted properties.
     */
    private boolean representsProperty(String className, String propertyName)
    {
        ClassSet cs;
        boolean res= false;
        Vector<ClassSet> scs = xread.subClassSets(className);
        for (int i = 0; i < scs.size(); i++)
        {
            cs = (ClassSet)scs.elementAt(i);
            if  (xread.allTruePropertyReps(cs).get(propertyName) != null) res = true;
        }
        return res;
    }
    
    public String getPropertyValue(objectToken oTok, String propertyName) throws MapperException
    {
        return xread.getPropertyValue(oTok, propertyName);
    }

    /**
     * FIXME - surely if getObjects remove duplicate representations of the same object, 
     * by  using unique identifier properties, this method should do the same
     */
    public Vector<objectToken> getAssociatedObjects(objectToken oTok, String relation, String otherClass,
            int oneOrTwo) throws MapperException
    {
        return xread.getAssociatedObjectTokens(oTok, relation, otherClass, oneOrTwo);
    }
    
    
    /**
     * the XOReader which this objectGetter uses
     * @return
     */
    public XOReader reader() throws MapperException {return xread;}

}
