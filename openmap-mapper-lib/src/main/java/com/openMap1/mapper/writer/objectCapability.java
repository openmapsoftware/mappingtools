/* see classes which implement this interface:
currently only XOCapability
*/
package com.openMap1.mapper.writer;
import java.util.*;

import com.openMap1.mapper.mapping.*;
import com.openMap1.mapper.core.*;

/**
 * metadata interface defining  the capabilities of anty objectGetter
 * 
 * @author robert
 *
 */
public interface objectCapability
{
    /**
     *  return the URI of the object model which this objectGetter implements
     */
     public String objectModelURIX() throws MDLReadException;

     /**
     *  whether or not the source represents the class at all;
     *  true if it represents any subset or subclass.
     *  */
    public boolean representsClassX(String className);

    /**
     *  true if the source recognises that class subClass inherits
     *  from class superClass (and so e.g. retrieves objects of the subClass if
     *  you ask for objects of the superClass)
     *  */
    public boolean inheritsFromX(String subClass,String superClass);

     /**  whether any object in the class is represented uniquely
    * (if false, the source may return several versions of the same object).
    * True only if objects are uniquely represented for all represented subsets
    * and subclasses.     */
    public boolean uniqueObjectX(String className);

    /* a Vector of filter objects (filterAssoc or filterProperty)
    for all inclusion filters. If the source represents several subsets
    of the class, or subclasses, only those filters common to all subsets
    and subclasses are returned. */
    public Vector<filter> inclusionFiltersX(String className);

    /* whether the source represents the property.
    If it represents subsets of the class or subclasses,
    returns true if the property is represented for any subsets and subclasses
    i.e. if it can ever be returned. */
    public boolean representsPropertyX(String className, String propertyName);

    /* whether the source represents the property as optional, i.e. sometimes absent.
     True if it is optional or not represented for any subset or subclass.
     False only if the property is represented and non-optional for all subsets
     and subclasses, i.e. can be relied upon to be present. */
    public boolean optionalPropertyX(String className, String propertyName) throws MapperException;

    /* If the object source is aware of any combination of properties
    which constitute a unique identifier for objects of the class, and if it
    is capable of returning those properties, and if they are all
    non-optional, return a Vector of the property names.
    if mustBeNonOptional is true, all the properties in the key must be non-optional
    Otherwise return null. */
    public Vector<String> primaryKeyX(String className, boolean mustBeNonOptional) throws MapperException;

    /* whether the source represents the association.
    If it represents subsets of either class or subclasses,
    returns true if the association is represented for any subsets and subclasses
    of the objects at both ends - i.e. if associated objects can ever be returned. */
    public boolean representsAssociationX(String class1, String assocName, String class2);

    /* end = 1 or 2 for class1 or class2 in the association.
    returns an array with minCardinality for the end in element 0,
    maxCardinality for the end in element 1.
    Allowed values are "0", "1", "N" and "N1" (may be 1 or N;not known).
    Min Cardinality = 1 only if it is 1 for all subsets and subclasses,
    i.e. if the object can be relied upon to be present.
    Max Cardinality = N if it is N for any subset and subclass,
    i.e if there is any possibility of returning more than one object.
    Therefore max Cardinality= 1 only if it is 1 for all subsets and subclasses
    at both ends of the association - if at most one associated object can ever
    be returned.*/
    public String[] cardinalityX(String class1, String assocName, String class2, int end) throws MapperException;
}
