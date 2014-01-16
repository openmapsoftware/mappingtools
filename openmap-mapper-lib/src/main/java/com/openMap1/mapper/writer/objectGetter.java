package com.openMap1.mapper.writer;

import com.openMap1.mapper.core.*;
import com.openMap1.mapper.reader.*;


import java.util.*;

import org.w3c.dom.Node;
/**
 * required interface for a source of objects, properties and associations
 * to be output in an XML file.
 *
 * Initially will have only one implementing  class, XMLObjectGetter
 * which brings together an XML file and the definition of that XML file in
 * MDL to read objects, properties and associations from the file.
 *
 */
public interface objectGetter
{
    
    /**
     * set the root of the XML instance being read
     * @param el
     * @throws MapperException
     */
         public void setRoot(Node el)  throws MapperException;

      /**
      * return a Vector of objectTokens for all objects in the class which you want written out
      * in an XML document by OXWriter.
      * These must represent each distinct object only once.
      * If it cannot return a Vector of unique objectTokens for distinct objects,
      * or if the source does not represent the class at all, throws a notRepresentedException.
      * If anything else goes wrong, throw an MDLReadException.
      */
     public Vector<objectToken> getObjects(String className)  throws MapperException;

     /**
      * return the value of a property of an object, given its objectToken.
      * Throw a notRepresentedException if the source does not represent the property.
      * If anything else goes wrong, throw an MDLReadException.
      */
     public String getPropertyValue(objectToken oTok, String propertyName) throws MapperException;

     /**
      * return a Vector of objectTokens for objects related to this object by some association
      * Throw a notRepresentedException if the source does not represent the association or
      * the class at the other end.
      * If anything else goes wrong, throw an MDLReadException.
      * @param oneOrTwo: end of the association which we are starting from
      */
     public Vector<objectToken> getAssociatedObjects(objectToken oTok, String relation, String otherClass,
            int oneOrTwo) throws MapperException;
     
     
     /**
      * the XOReader which this objectGetter uses
      * @return
      */
     public XOReader reader() throws MapperException;
}
