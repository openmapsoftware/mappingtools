package com.openMap1.mapper.mapping;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.XpthException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.ClassSet;

import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.AssocMapping;

import java.util.Iterator;
import java.util.Vector;

/**
 *  an association (link) mapping.
 * 
 * This is a wrapper around the model class AssocMapping.
 * 
 * @author Robert Worden
 * @version 1.0
 */

public class AssociationMapping extends MappingTwo
{
	/** Override: there is no single class or subset associated with an association mapping 
	 * - they are associated with the  ends. */
	public ClassSet cSet() {return null;}
	/** Override: there is no single class or subset associated with an association mapping 
	 * - they are associated with the  ends. */
	public String className() {return null;}
	/** Override: there is no single class or subset associated with an association mapping 
	 * - they are associated with the  ends. */
	public String subset() {return null;}
	
	public AssocMapping am() {return (AssocMapping)map();}

        /**
         *
         * @param md messageChannel: for writing messages
         */
        public AssociationMapping(AssocMapping am, messageChannel md) throws MapperException
        {
            super(am, md);
            mappingType = MappingTwo.ASSOCIATION;
            // make the two association end mappings
            for (int e = 0; e < 2; e++)
            {assocEnd[e] = new associationEndMapping(am.getMappedEnd(e),md);}
            // set the opposite end mapping for each end
            for (int e = 0; e < 2; e++) assocEnd[e].setOtherEndMapping(assocEnd[1-e]);
        }

    private associationEndMapping[]  assocEnd = new associationEndMapping[2];

    /** the association name (not defined in UML) */
    public String assocName() {return am().getMappedAssociation();}

    /**
     * the association end mapping for one of the ends of the association
     *
     * @param end int end = 0 or 1     *
     * @return associationEndMapping the association end mapping
     */
    public associationEndMapping  assocEnd(int end) throws MapperException
    {
        associationEndMapping aem = null;
        if (end == 0) {aem = assocEnd[0];}
        else if (end == 1) {aem = assocEnd[1];}
        else {throw new MapperException("Invalid end " + end + " getting mapping for association '" + assocName() + "': " + end);}
        return aem;
    }


    /**
     * write a simple description of the mapping
     */
    public void write()
    {
        mChan().message("");
        mChan().message("Association mapping of " + fullName() + " to node " + nodePath().stringForm());
        writeConditions();
        for (int i = 0; i < 2; i++) 
        	try {assocEnd(i).write();} 
               catch(MapperException ex) {}
    }

    /** full association name, with classes and subsets at both ends */
    public String fullName()
    {
        String  res = "[undefined]" + assocName() + "[undefined]";
        if ((assocEnd[0] != null) && (assocEnd[1] != null))
            {res = ("[" + assocEnd[0].getClassSet().stringForm() + "]"
                    + assocName()
                    + "[" + assocEnd[1].getClassSet().stringForm() + "]");}
        return res;
    }

    /** True if this is a self-association, i.e a representation of an association
    with the same class (and subset) at both ends */
    public boolean self()
    {
        return (assocEnd[0].getClassSet().equals(assocEnd[1].getClassSet()));
    }

    /** a simple nesting association is one in which
     * <p>
    * the path from the association node to one object node is a pure ascent (parent:: or ancestor::)
    * <p>
    * the path from the association node to the other object is "." (stay here)
    */
    public boolean simpleNesting() throws MapperException
    {
        return ((assocEnd[0].pureAscent() & assocEnd[1].identity())|
                (assocEnd[1].pureAscent() & assocEnd[0].identity()));
    }

    /** if either end (0 or 1) of the association has a pure ancestor path,
    and the other end has an identity path,
    return the ancestor end 0 or 1. Otherwise return -1. */
    public int ancestorEnd() throws MapperException
    {
        int j, res;
        res = -1;
        for (j = 0; j < 2; j++) if
          ((assocEnd[j].pureAscent()) && (assocEnd[1-j].identity()))
            {res = j;}
        return res;
    }

    /** if either end (0 or 1) of the association has an identity path,
    and the other end has an ancestor path,
    return the identity end 0 or 1. Otherwise return -1. */
    public int identityEnd() throws MapperException
    {
        int j, res;
        res = -1;
        for (j = 0; j < 2; j++) if
          ((assocEnd[j].identity()) && (assocEnd[1-j].pureAscent()))
            {res = j;}
        return res;
    }

    /** find the end = 1 or 2 which has a given role name, or throw an exception */
    public int endForRole(String roleName) throws MapperException
    {
        int end = -2;
        for (int i = 0; i < 2; i++)
            if (assocEnd[i].roleName().equals(roleName)) {end = i + 1;}
        if (end == -2)
            {throw new MapperException("Association '" + assocName()
                + "' has no role '" + roleName + "'");}
        return end;
    }

    /** Ensure that any simple nesting association has
    the 'stay here' association end marked as 'required',
    so that the association is an inclusion filter for that class. */
    /*
    void makeSimpleNestingRequired()
    {
        if (simpleNesting())
        {
            //message("Required association " + fullName());
            if (assocEnd[0].identity()) {assocEnd[0].setRequired(true);}
            else if (assocEnd[1].identity()) {assocEnd[1].setRequired(true);}
        }
    } */

    /** XPaths to nodes required to evaluate link conditions for this representation */
    public Vector<Xpth> linkConditionPaths()
    {
        int i,j;
        Vector<Xpth> res = new Vector<Xpth>();
        for (j = 0; j < 2; j++)
        {
            associationEndMapping aem = assocEnd[j];
            for (i = 0; i < aem.linkConditions().size(); i++)
            {
                linkCondition c = aem.linkConditions().elementAt(i);
                res.addElement(c.rootToLeftValue());
                res.addElement(c.rootToRightValue());
            }
        }
        return res;
    }

    /** If this assocation is required for just one of the objects at the two
    * ends of the association, return which one (1 or 2)
    * <p>
    * Otherwise return 0.
    * Throw an exception if required for both ends. */
    public int requiredEnd() throws MapperException
    {
        int i,found,res;
        found = 0;
        res = 0;
        for (i = 0; i < 2; i++)
            if (assocEnd[i].required())
            {
                found++;
                res = i+1;
            }
        if (found > 0)
            {throw new MapperException ("Association " + fullName() + " has both ends required.");}
        return res;
    }

    /** simple string description */
    public String description()
    {
          return("mapping for association " + fullName() );
    }

    /** return 1 or 2 if end 1 or 2 has class and subset cSet.
     * <p>
    * return -1 if neither end has this class and subset.
    * <p>
    * return 2 if both ends have this class and subset.
    */
    public int classEnd(ClassSet cSet)
    {
          int end = -1;
          for (int i = 0; i < 2; i++)
            if (assocEnd[i].cSet().equals(cSet)) {end = i + 1;}
          return end;
    }

    /** maximum absolute path length to this mapping, or to any of its when-condition or link
    condition values */
    public int mappingDepth() throws MapperException
    {
        int depth = super.mappingDepth();
        for (int i = 0; i < 2; i++)
        {
            if (assocEnd(i).mappingDepth() > depth) depth = assocEnd(i).mappingDepth();
        }
        return depth;
    }


    /** maximum inner path length inside the '//' step to this mapping,
    or to any of its when-condition or link condition values */
    public int innerDepth() throws XpthException
    {
        int depth = super.innerDepth();
        for (int i = 0; i < 2; i++) try
        {
            if (assocEnd(i).innerDepth() > depth) depth = assocEnd(i).innerDepth();
        }
        catch (MapperException ex) {GenUtil.surprise(ex, "MDLBase.innerDepth");}
        return depth;
    }

    /** return the classSet which has a given role name, or null if there is none */
    public ClassSet getClassSet(String roleName)
    {
        ClassSet cSet = null;
        for (int end = 0; end < 2; end++) try {
            if (roleName.equals(assocEnd(end).roleName()))
                {cSet = assocEnd(end).cSet();}
        }
        catch (MapperException ex) {GenUtil.surprise(ex, "MDLBase.getClassSet");}
        return cSet;
    }

    /** return the class at the other end of the association from the named class, or null if there is none */
    public String otherEndClassName(String className)
    {
        String oClass = null;
        for (int end = 0; end < 2; end++)
            if (assocEnd[end].className().equals(className))
                {oClass = assocEnd[1-end].className();}
        return oClass;
    }

    
    public static Vector<AssociationMapping> vCopyAssociationMapping(Vector<AssociationMapping> v)
    {
   	 Vector<AssociationMapping> res = new Vector<AssociationMapping>();
   	 for (Iterator<AssociationMapping> it = v.iterator();it.hasNext();) res.add(it.next());
   	 return res;
    }

}
