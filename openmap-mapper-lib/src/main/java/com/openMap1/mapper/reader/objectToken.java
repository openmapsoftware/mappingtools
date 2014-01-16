package com.openMap1.mapper.reader;

import com.openMap1.mapper.core.ClassSet;

/**
 * Objects implementing this interface are used as tokens, passed from an objectGetter
 * to an OXWriter representing objects in the class model to be written, and passed back from
 * the OXWriter to an objectGetter to find values of properties and associations of the objects.
 *
 * An implementation of objectGetter must recognise instances of its own implementation of objectToken;
 * when it is passed one back, it must know what object it represents,
 * in order to get its properties and associations.
 */
public interface objectToken {

/**
 * the class of the object
 */
public String className();

/** 
 * the subset of the represented object 
 * */
public String subset();

/**
 *  return ClassSet ( = class and subset) in the source of the object - e.g. the XML
source document where the object is represented. */
public ClassSet cSet();




/**
 * a key which uniquely identifies the object, and is suitable for use as the key
 * in a Hashtable of all objects in a class.
 *
 * The key must implement hashcode() and equals() - not returning
 * true for two different objects.
 * 
 * Note the key may be the same for two objects in different classes, eg if they are represented
 * on the same node on an XML document
 */
public Object objectKey();

/**
 * 
 * @return the XOReader which created this object token
 */
public XOReader reader();


/**
 * empty objectTokens denoting no object are used in queries
 * @return
 */
public boolean isEmpty();

}