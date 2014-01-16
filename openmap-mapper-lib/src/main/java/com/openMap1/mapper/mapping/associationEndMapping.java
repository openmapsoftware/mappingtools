package com.openMap1.mapper.mapping;

import java.util.*;

import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.PropMapping;

import org.eclipse.emf.ecore.EPackage;

/**
 * the mapping to one end of an association.
 * both associationEndMappings of an associationMapping are created
 * with the same nodePath - the path from the root to the association node.
 * 
 * This class is a wrapper around the model class AssocEndMapping
 *
 * @author Robert Worden
 * @version 1.0
 */
public class associationEndMapping extends MappingTwo
{
	public AssocEndMapping aem() {return (AssocEndMapping)map();}
	
    private String minCardinality; // "0", "1", "M", or "undefined"
    private String maxCardinality; // "1", "M", or "undefined"

    /* minCardinality and maxCardinality as defined in the MDL.
    These are either undefined  (value '') or '1' - i.e the MDL can only
    make the min and max cardinalities more constrained, than either the
    object model or the XML structure would imply.
    It cannot make them less constrained. */
    private String minCardinalityFromMDL = "";
    private String maxCardinalityFromMDL = "";

    /** role name for the end (UML 'association name' */
    public String roleName() {return aem().getMappedRole();}

    /** XPath from association node to object at this end */
    public Xpth assocToObj()throws MapperException 
    	{return aem().getAssociationToObjectXPath();}

    /** XPath from object at this end to association */
    public Xpth objToAssoc() throws MapperException
    	{return aem().getObjectToAssociationXPath();}

    /** minCardinality and maxCardinality from the object model
     *  - not defined in MDL, but are only set up
     * when validating MDL, from the class model in check 4; and used in check 9.
     * "0", "1", "M", or "undefined" */
    public String minCardinality() {return minCardinality;}
    /** set minCardinality */
    public void setMinCardinality(String s) {minCardinality = s;}
    /** maxCardinality and maxCardinality from the object model
     *  - not defined in MDL, but are only set up
     * when validating MDL, from the class model in check 4; and used in check 9.
     *  "1", "M", or "undefined" */
    public String maxCardinality() {return maxCardinality;}
    /** set max cardinality */
    public void setMaxCardinality(String s) {maxCardinality = s;}

    
    /** minCardinality  as defined in the MDL.
    Either undefined  (value '') or '1' - i.e the MDL can only
    make the min and max cardinalities more constrained, than either the
    object model or the XML structure would imply.
    It cannot make them less constrained. */
    public String minCardinalityFromMDL() {return minCardinalityFromMDL;}
    /** maxCardinality  as defined in the MDL.
    Either undefined  (value '') or '1' - i.e the MDL can only
    make the min and max cardinalities more constrained, than either the
    object model or the XML structure would imply.
    It cannot make them less constrained. */
    public String maxCardinalityFromMDL() {return maxCardinalityFromMDL;}
    /** if true, this association is an inclusion condition for the class/subset */

    public boolean required() {return aem().isRequiredForObject();}
    
    public int end() {return aem().getEnd();}
    
    public associationEndMapping getOtherEndMapping() {return otherEndMapping;}
    public void setOtherEndMapping(associationEndMapping otherEndMapping)
    	{this.otherEndMapping = otherEndMapping;}
    private associationEndMapping otherEndMapping = null;

    /**
     * mapping for one end of an association
     *
     * @param aem the AssocEndMapping
     * @param md messageChannel: for writing messages
     */
    public associationEndMapping(AssocEndMapping aem, messageChannel md) throws MapperException
    {
        super(aem, md);
        mappingType = MappingTwo.ASSOCIATION;

        minCardinality = "undefined";
        maxCardinality = "undefined";
        // if (minFromMDL.equals("1")) minCardinalityFromMDL = "1";
        // if (maxFromMDL.equals("1")) maxCardinalityFromMDL = "1";
    }

    /** simple write of the end mapping */
    public void write()
    {
        mChan().message("Association end mapping to class " + cSet().stringForm() + " at end " + end());
        writeConditions();
    }

    /** true if the path from the association node to the object is a pure ascent */
    public boolean pureAscent() throws MapperException
    {
        return (assocToObj().pureAscent());
    }

    /** true if the path from the association node to the object node is 'stay here' */
    public boolean identity()  throws MapperException
    {
        return (assocToObj().selfPath());
    }

    /** XPaths for link conditions */
    public Vector<Xpth> linkConditionPaths()
    {
      Vector<Xpth> res = new Vector<Xpth>();
      for (int i = 0; i < linkConditions().size();i++)
      {
        linkCondition lc = linkConditions().elementAt(i);
        res.addElement(lc.rhsEndToRightValue());
      }
      return res;
    }

    /** XPaths for equality link conditions */
    public Vector<Xpth> equalityLinkConditionPaths()
    {
      Vector<Xpth> res = new Vector<Xpth>();
      for (int i = 0; i < linkConditions().size();i++)
      {
        linkCondition lc = linkConditions().elementAt(i);
        if (lc.test.equals("="))
          {res.addElement(lc.rhsEndToRightValue());}
      }
      return res;
    }
 
    /** true if the association node represents multiple instances of the association,
    to multiple objects of this end class. */
    public int multiInstanceLinks()
    {
      int multis = 0;
      for (int i = 0; i < linkConditions().size();i++)
      {
        linkCondition lc = linkConditions().elementAt(i);
        if (lc.test.equals("containsAsWord")) multis++;
      }
      return multis;
    }
    /** true if the association node has any link conditions in a set of nodes
    defined by their root paths */
    public boolean hasLinkValuesIn(Vector<Xpth> nodePaths)
    {
      boolean hasVals = false;
      for (int i = 0; i < linkConditions().size();i++)
      {
        linkCondition lc = linkConditions().elementAt(i);
        if (lc.rootToLeftValue().oneOf(nodePaths)) hasVals = true;
      }
      return hasVals;
    }

    /** simple description */
    public String description()
    {
          return("mapping for association end at class '" + className() + "'");
    }

    /** true if the link conditions of an association end mapping guarantee that
    the path from the association node to the object node will only give one object -
    i.e. if the link conditions pick out unique values for all properties in a
    unique identifier of the object, as defined by the MDL in MD. */
    public boolean uniqueLinkConditions()  throws MapperException
    {
        boolean res = false;
        EPackage classModelRoot = ModelUtil.getClassModelRoot(aem());
        Vector<Vector<String>> uids = ModelUtil.uniqueIdentifiers(className(),classModelRoot);
        // can only be true if there are some link conditions and some unique identifiers
        if ((linkConditions().size() > 0) && (uids.size() > 0))
            // try out all unique identifiers, to see if any are defined by the link conditions
            for (int i = 0; i < uids.size(); i++)
            {
            	Vector<String> uid = uids.elementAt(i);
                boolean uidDefined = true;
                // loop over all properties in a unique identifier
                for (Iterator<String> it = uid.iterator();it.hasNext();)
                if (uidDefined)
                {
                    String propName = it.next();
                    boolean propDefined = false;
                    /* there may be several property mappings - choice or redundant;
                    If any mappings are defined by the link conditions, assume the property is defined. */
                    Vector<PropMapping> propMappings = ModelUtil.getPropertyMappings(cSet(),propName,aem());
                    if (propMappings.size() == 0)  {uidDefined = false;}
                    else for (int k = 0; k < propMappings.size(); k++)
                    {
                        PropMapping pm = propMappings.elementAt(k);
                        if (definedPropertyMapping(pm)) propDefined = true;
                    }
                    if (!propDefined) uidDefined = false;
                }
                if (uidDefined) res = true;
            }
        return res;
    }
    /** true if one of the link conditions in this association mapping
    is sufficient to define the value of the property in the property mapping.*/
    private boolean definedPropertyMapping(PropMapping pm) throws MapperException
    {
        boolean res = false;
        for (int i = 0; i < linkConditions().size(); i++)
        {
            linkCondition lc = linkConditions().elementAt(i);
            // double condition too stringent??
            if ((lc.rootToRightValue().equalPath(pm.getRootXPath())) &&
                (lc.rhsEndToRightValue().equalPath(pm.getObjectToPropertyXPath()))) res = true;
        }
        return res;
    }
}
