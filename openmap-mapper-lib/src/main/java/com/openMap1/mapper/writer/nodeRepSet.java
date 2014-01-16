package com.openMap1.mapper.writer;


import com.openMap1.mapper.mapping.*;
import com.openMap1.mapper.core.*;
import com.openMap1.mapper.util.*;
import com.openMap1.mapper.NodeDef;


import java.util.*;

//------------------------------------------------------------------------------------------
//          class for sets of representations on one node
//------------------------------------------------------------------------------------------

/**
 *  class for sets of representations on one node
 *
 * @author Robert Worden
 * @version 1.0
 */
public class nodeRepSet extends repSet {
	
		private NodeDef mappedNode = null;
		public NodeDef mapperNode() {return mappedNode;}


        /* if this node holds a when-condition value,
        whenVals holds the set of possible string values */
        private Vector<String> whenVals;

        /* if this  node holds a when-condition value,
        alreadyFixed is true if the value has already been fixed by decisions made when code generating
        for a higher node, ie if its value is already in the context. */
        private boolean  alreadyFixed;

        /** if this  node holds a when-condition value,
        alreadyFixed is true if the value has already been fixed by decisions made when code generating
        for a higher node, ie if its value is already in the context. */
        public boolean alreadyFixed() {return alreadyFixed;}

        /** if this node holds a when-condition value,
        whenVals holds the set of possible string values */
        public Vector<String> whenVals() {return whenVals;}

        /* if this node has no mappings, mostSpecificPathSpec remains null;
        otherwise it becomes the most specific path spec of any of the
        mappings (not yet including condition values) to this node. */
        private Xpth mostSpecificPathSpec = null;

        /* the maximum number of definite 'child' or 'attribute' steps inside the
        innermost '//' step of any XPath for a mapping to the node;
        will be rootPath.size() if there is any mapping with a definite path. */
        private int maxInnerDefiniteSteps = 0;

        /** the maximum number of definite 'child' or 'attribute' steps inside the
        innermost '//' step of any XPath for a mapping to the node;
        will be rootPath.size() if there is any mapping with a definite path. */
        public int maxInnerDefiniteSteps() {return maxInnerDefiniteSteps;}

        private int maxOuterDefiniteSteps = 0;
        /** the maximum number of definite steps from the root to any
        '//' step for any maping in the set. */
        public int maxOuterDefiniteSteps() {return maxOuterDefiniteSteps;}

        /** minimum number of steps from the root in the actual root path which
        are left undefined in all mappings to the node.*/
        public int minUndefinedSteps() {return (rootPath.size() - maxInnerDefiniteSteps);}

        public Xpth pathSpec() {return mostSpecificPathSpec;}

        // whether any of the mappings are to a definite XPath
        private boolean hasDefinitePaths = false;
        // whether any of the mappings are to an indefinite XPath
        private boolean hasIndefinitePaths = false;

        /** whether any of the mappings are to a definite XPath */
        public boolean hasDefinitePaths() {return hasDefinitePaths;}
        /** whether any of the mappings are to an indefinite XPath */
        public boolean hasIndefinitePaths() {return hasIndefinitePaths;}

//---------------------------------------------------------------------------
//                                 constructors
//---------------------------------------------------------------------------


        /**
         * constructor for a repSet for a single node, including all mappings
         *
         * @param os StructureDefinition output XML tree structure
         * @param md MDLBase mappings to output XML
         * @param rp Xpth path to the node
         * @throws MDLReadException
         */
        public nodeRepSet(MDLBase md, Xpth rp) throws MapperException
        {
            super(md,rp);
            mappedNode = MD.ms().getNodeDefByPath(rp.stringForm());
            outputContext oc = new outputContext(md,rp); // oc has no whenValues
            findMappings(rp,oc);
            /* If the node has no mappings on it , then it should have
            minUndefinedSteps() = 0, i.e. maxInnerDefiniteSteps = rootPath.size() */
            if (noMeaning())
                {maxInnerDefiniteSteps = rootPath.size();}
            alreadyFixed = false;
        }

    /**
     * constructor for a repSet for a single node, including only
     * those mappings applicable in a context.
     *
     * @param os StructureDefinition output XML tree structure
     * @param md MDLBase mappings to output XML
     * @param rp Xpth path to the node
     * @param oc outputContext The context contains a set of when-condition values from nodes
     *  in unique subtrees of nodes above this one - i.e all when-condition values which
     *  have this node in their scope subtrees.
     *
     * @throws MDLReadException
     */
    public nodeRepSet(MDLBase md, Xpth rp, outputContext oc)
    throws MapperException
        {
            super(md,rp);
            findMappings(rp,oc);
            alreadyFixed = whenValueFixed(rp,oc);
        }


    /** return true if a when-condition value on the node with root path rp
    has been already fixed in the context oc.  */
    private boolean whenValueFixed(Xpth rp, outputContext oc)
    {
        boolean fixed = false;
        if (oc.whenValues().get(rp.stringForm()) != null) fixed = true;
        return fixed;
    }


    /* Find all mappings to a definite XPath xp. These include
    mappings to indefinite XPaths which are compatible with xp.
    Do not include any mappings which are not compatible with the output context oc,
    (because oc has when-condition values set, and the mappings have different values)*/
    private void findMappings(Xpth xp, outputContext oc) throws MapperException
    {
        if (xp == null)
            {throw new MDLReadException("Tried to find mappings to a node with null XPath.");}
        whenVals = new Vector<String>();
        Xpth xq = xp.convertPrefixes(MD.NSSet());

        // fast retrieval of mappings with this definite path, and any with indefinite paths
        Vector<MappingTwo> candMappings = MD.indefinitePathMappings();
        Vector<MappingTwo> defPathMappings = (Vector<MappingTwo>)MD.mappingsByDefinitePath.get(xq.stringForm());
        if (defPathMappings != null) {candMappings = addMappings(defPathMappings,MD.indefinitePathMappings());}

        for (int c = 0; c < candMappings.size(); c++)
        {
            MappingTwo m = (MappingTwo)candMappings.elementAt(c);

            if (m instanceof objectMapping)
            {
               objectMapping om = (objectMapping)m;
               if (om.applicable(oc))
               {
                   addWhenVal(om,xp);
                   // the 'compatible' test converts namespace prefixes when comparing
                   if (xp.compatible(om.nodePath())) {addObjectMap(om);storeSpecificPath(om.nodePath());}
                   if (xp.compatibleOneOf(om.whenConditionPaths()))
                       {addWhenMap(om);storeCompatibleSpecificPaths(xp,om.whenConditionPaths());}
               }
            }

            else if (m instanceof propertyMapping)
            {
               propertyMapping pm = (propertyMapping)m;
               // the property mapping must not clash with any when values, nor must its object mapping
               if ((pm.applicable(oc)) && (applicableObjectMapping(pm,oc)))
               {
                   addWhenVal(pm,xp);
                   if (xp.compatible(pm.nodePath())) {addPropertyMap(pm);storeSpecificPath(pm.nodePath());}
                   if (xp.compatibleOneOf(pm.whenConditionPaths()))
                       {addWhenMap(pm);storeCompatibleSpecificPaths(xp,pm.whenConditionPaths());}
                   if (xp.compatibleOneOf(pm.linkConditionPaths()))
                       {addLinkMap(pm);storeCompatibleSpecificPaths(xp,pm.linkConditionPaths());}
               }
            }

            else if (m instanceof AssociationMapping)
            {
               AssociationMapping am = (AssociationMapping)m;
               // the association mapping must not clash with any when values, nor must either of its end object mappings
               if ((am.applicable(oc))&& (applicableObjectMappings(am,oc)))
               {
                   addWhenVal(am,xp);
                   if (xp.compatible(am.nodePath())) {addAssocMap(am);storeSpecificPath(am.nodePath());}
                   if (xp.compatibleOneOf(am.whenConditionPaths()))
                       {addWhenMap(am);storeCompatibleSpecificPaths(xp,am.whenConditionPaths());}
                   if (xp.compatibleOneOf(am.linkConditionPaths()))
                       {addLinkMap(am);storeCompatibleSpecificPaths(xp,am.linkConditionPaths());}
               }
            }
        }
    }
    
    /** return true if either one of the two object mappings at the end of an association 
     * mapping are applicable in the context */
    private boolean applicableObjectMappings(AssociationMapping am, outputContext oc)
    {
    	boolean applicable = false;
    	for (int end = 0; end < 2; end++) try
    	{
    		associationEndMapping aem = am.assocEnd(end);
    		objectMapping om = MD.namedObjectMapping(aem.cSet());
    		if (om.applicable(oc)) applicable = true;
    	}
    	catch (Exception ex) 
    	{
    		System.out.println("Exception testing applicability of object mapping for association mapping: " + ex.getMessage());
    		applicable = false;
    	}
    	return applicable;

    }

    
    private boolean applicableObjectMapping(propertyMapping pm, outputContext oc)
    {
    	boolean applicable = true;
    	objectMapping om = MD.namedObjectMapping(pm.cSet());
    	try {if (!om.applicable(oc)) applicable = false;}
        catch (Exception ex) 
        {
        	System.out.println("Exception testing applicability of object mapping for property mapping: " + ex.getMessage());
        	applicable = false;
        }
    	return applicable;

    }


    /* keep a running tally of the most specific path specification
    of any mapping to this node, and the maximum number of inner definite steps
    Initially mostSpecificPathSpec is null. */
    private void storeCompatibleSpecificPaths(Xpth xp,Vector<Xpth> paths)
    throws MapperException
    {
        Vector<Xpth> cPaths = xp.compatibleSubset(paths);
        for (int i = 0; i < cPaths.size(); i++)
        {
            Xpth path = cPaths.elementAt(i);
            storeSpecificPath(path);
        }
    }

    private void storeSpecificPath(Xpth path) throws XpthException
    {
        Xpth newPath;
        if (path == null) {message("null mapping path");}

        int innerSteps = path.innerDefiniteSteps();
        if (innerSteps > maxInnerDefiniteSteps)
            {maxInnerDefiniteSteps = innerSteps;}

        int outerSteps = path.outerDefinitePart().size();
        if (outerSteps > maxOuterDefiniteSteps)
            {maxOuterDefiniteSteps = outerSteps;}

        if (path.definite())
            {hasDefinitePaths = true;}
        else {hasIndefinitePaths = true;}

        newPath = path;
        if (newPath.asSpecificAs(mostSpecificPathSpec))
          {mostSpecificPathSpec = newPath;}
    }

    /** if a mapping mp has any when-conditions taking their LHS value from
    the node with root path xp, add their RHS test values to vector whenVals. 
    Only include when-conditions which constrain the value on the node directly,
    with no intervening function (which could eg depend only on the node position)*/
    void addWhenVal(MappingTwo mp, Xpth xp)
    {
        for (int i = 0; i <  mp.whenConditions().size(); i++)
        {
            whenCondition wc = mp.whenCondition(i);
            if ((wc.rootToLeftValue().equalPath(xp)) &&
                (!(GenUtil.inVector(wc.rightValue(),whenVals))))
            {
            	// do not include when-conditions with a function
            	if (wc.getLeftFunction().equals(""))
            		whenVals.addElement(wc.rightValue());
            	else {}
            }
        }
    }

    /** write out all mappings on the node and mappings with conditions on the node */
    public void write(messageChannel mChan)
    {
    	mChan.message("**** nodeRepSet for node '" + rootPath.stringForm() + "'");

        String xx = "";
        if (noMeaning()) {xx = "No mappings; ";} else {xx = "Has mappings; ";}
        if (hasDefinitePaths()) {xx = xx + "definite paths; ";} else {xx = xx + "no definite paths; ";}
        if (hasIndefinitePaths()) {xx = xx + "indefinite paths. ";} else {xx = xx + "no indefinite paths. ";}
        mChan.message(xx);
        mChan.message("Def outers: " + maxOuterDefiniteSteps() +  " Def inners: " + maxInnerDefiniteSteps());

        writeMappings(mChan,"Object mappings",objectMaps());
        writeMappings(mChan,"Property mappings",propertyMaps());
        writeMappings(mChan,"Association mappings",assocMaps());
        writeMappings(mChan,"Mappings with when-conditions",whenMaps());
        writeMappings(mChan,"Mappings with link conditions",linkMaps());
    }

    private void writeMappings(messageChannel mChan, String title,Vector<MappingTwo> mappings)
    {
        if (mappings.size() > 0)
        {
            message(title);
            for (int i = 0; i < mappings.size(); i++)
            {
                MappingTwo m = mappings.elementAt(i);
                m.write(mChan);
            }
        }
    }

}
