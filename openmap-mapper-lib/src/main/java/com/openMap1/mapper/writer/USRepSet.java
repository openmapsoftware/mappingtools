package com.openMap1.mapper.writer;


import com.openMap1.mapper.mapping.*;
import com.openMap1.mapper.core.*;
import com.openMap1.mapper.util.messageChannel;

import java.util.*;

//------------------------------------------------------------------------------------------
//          class for sets of representations on a unique subtree (US)
//------------------------------------------------------------------------------------------

/**
 * class for sets of representations on a unique subtree (US)
 *
 * @author Robert Worden
 * @version 1.0
 */
public class USRepSet extends repSet {


        private Hashtable<String, Vector<String>> subtreeWhenVals;
        /**  subTreeWhenVals has key = root path string form of a node in the unique subtree,
        * and value = the whenVals Vector for the node.
        * <p>
        * Only includes those when-condition nodes whose values were not fixed
        * at any higher node, i.e nodes which are not in the unique subtree of a higher node. */
        public Hashtable<String, Vector<String>> subtreeWhenVals() {return subtreeWhenVals;}

        private TreeElement subTree;
        /** The unique subtree - those nodes below this one with mincardinality = maxCardinality = 1 */
        public TreeElement subTree() {return subTree;}

        private int minUndefinedSteps;
        /** minimum number of steps from the root in the actual root path which
        * are left undefined in all mappings to any node in the unique subtree.*/
        public int minUndefinedSteps() {return minUndefinedSteps;}

        private int maxOuterDefiniteSteps = 0;
        /** the maximum number of definite steps from the root to any
        '//' step for any mapping in the set. */
        public int maxOuterDefiniteSteps() {return maxOuterDefiniteSteps;}

        private boolean hasDefinitePaths = false;
        /** whether any of the mappings are to a definite XPath */
        public boolean hasDefinitePaths() {return hasDefinitePaths;}

        private boolean hasIndefinitePaths = false;
        /** whether any of the mappings are to an indefinite XPath */
        public boolean hasIndefinitePaths() {return hasIndefinitePaths;}

        private Vector<nodeRepSet> nodeRepSets = new Vector<nodeRepSet>();
        /** Vector of all the individual nodeRepsets that make up this USRepSet */
        public Vector<nodeRepSet> nodeRepSets() {return nodeRepSets;}


//---------------------------------------------------------------------------
//                                 constructors
//---------------------------------------------------------------------------


    /**
     * constructor for a repSet for a unique subtree,
     *  including only mappings applicable in the subtree context.
     *
     * @param os StructureDefinition output XML tree structure definition
     * @param md MDLBase mappings to output XML
     * @param rp Xpth path to this node
     * @param rootTree treeElement
     * @param oc outputContext include only mappings applicable in the subtree context.
     * @throws MDLReadException
     */
    public USRepSet(MDLBase md, Xpth rp, TreeElement rootTree, outputContext oc)
         throws MapperException
        {
            super(md,rp);
            nodeRepSet thisNodeRepSet = new nodeRepSet(MD,rp,oc);
            nodeRepSets.addElement(thisNodeRepSet);
            appendMaps(thisNodeRepSet);
            hasDefinitePaths = thisNodeRepSet.hasDefinitePaths();
            hasIndefinitePaths = thisNodeRepSet.hasIndefinitePaths();
            minUndefinedSteps = thisNodeRepSet.minUndefinedSteps();
            maxOuterDefiniteSteps = thisNodeRepSet.maxOuterDefiniteSteps();
            subtreeWhenVals = new Hashtable<String, Vector<String>>();
            // do not add when-condition value vectors for values already fixed at a higher node
            if ((!(thisNodeRepSet.alreadyFixed())) && (thisNodeRepSet.whenVals().size() > 0))
                {subtreeWhenVals.put(rp.stringForm(),thisNodeRepSet.whenVals());}
            TreeElement uSub =rootTree.fromRootPath(rp,true).uniqueSubtree();
            //message("Unique subtree size: " + uSub.size());
            subTree = uSub;
            for (int i = 0; i < uSub.childTreeElements().size(); i++)
            {
                TreeElement child = (TreeElement)uSub.childTreeElements().elementAt(i);
                Xpth rpp = rp.addInnerStep(child.tagName());
                addRepSetInfo(subtreeWhenVals,rpp,rootTree,oc);
            }
            for (int i = 0; i < uSub.attributes().size(); i++)
            {
                Xpth rpp = rp.addInnerStep("@" + uSub.attribute(i));
                nodeRepSet attReps = new nodeRepSet(MD,rpp,oc);
                appendMaps(attReps);
                if (attReps.hasDefinitePaths()) hasDefinitePaths = true;
                if (attReps.hasIndefinitePaths()) hasIndefinitePaths = true;
                nodePaths().addElement(rpp);
                nodeRepSets.addElement(attReps);
                // do not add when-condition value vectors for values already fixed at a higher node
                if ((!(attReps.alreadyFixed())) && (attReps.whenVals().size() > 0))
                  {subtreeWhenVals.put(rpp.stringForm(),attReps.whenVals());}
            }
        }

        /** find the repSet for a unique child node, and add all its information
        to this repSet. */
        void addRepSetInfo(Hashtable<String, Vector<String>> subtreeWhenVals,
            Xpth rpp, TreeElement rootTree, outputContext oc) throws MapperException
        {
                USRepSet cRep = new USRepSet(MD,rpp,rootTree,oc);
                appendMaps(cRep);
                nodeRepSets = addNodeRepSets(nodeRepSets,cRep.nodeRepSets());
                if (cRep.hasDefinitePaths()) hasDefinitePaths = true;
                if (cRep.hasIndefinitePaths()) hasIndefinitePaths = true;
                if (cRep.minUndefinedSteps() < minUndefinedSteps)
                    {minUndefinedSteps = cRep.minUndefinedSteps();}
                if (cRep.maxOuterDefiniteSteps() > maxOuterDefiniteSteps)
                    {maxOuterDefiniteSteps = cRep.maxOuterDefiniteSteps();}
                addXpths(nodePaths(),cRep.nodePaths());
                for (Enumeration<String> en = cRep.subtreeWhenVals().keys(); en.hasMoreElements();)
                {
                    String rpn = en.nextElement();
                    Vector<String> values = cRep.subtreeWhenVals().get(rpn);
                    subtreeWhenVals.put(rpn,values);
                }
        }

      /**
       * constructor for a copy of an existing USRepSet, including only
       * mappings dependent on one primary object mapping.
       *
       * @param usr USRepSet
       * @param primary objectMapping
       */
      public USRepSet(USRepSet usr, objectMapping primary)  throws MapperException
      {
          //set up empty Vectors of mappings
          super(usr.MD,usr.rootPath);

          // copy all trivia
          nodePaths = usr.nodePaths();
          subtreeWhenVals = usr.subtreeWhenVals();
          subTree = usr.subTree();
          minUndefinedSteps = usr.minUndefinedSteps();
          maxOuterDefiniteSteps = usr.maxOuterDefiniteSteps();
          hasDefinitePaths = usr.hasDefinitePaths();
          hasIndefinitePaths = usr.hasIndefinitePaths();
          nodeRepSets = usr.nodeRepSets();

          // copy across all mappings with condition values in the subtree
          whenMaps = usr.whenMaps();
          linkMaps = usr.linkMaps();

          // copy from usr all mappings dependent on one primary mapping
          copyDependentMappings(usr,primary);
      }

      // copy from another USRepSet usr all mappings dependent on one primary mapping
      private void copyDependentMappings(USRepSet usr, objectMapping primary)
      throws MapperException
      {
          ClassSet pcs = primary.cSet();

          // copy the primary object mapping
          addObjectMap(primary);

          //copy all property mappings for the primary ClassSet
          for (int i = 0; i < usr.propertyMaps().size(); i++)
          {
              propertyMapping pm = (propertyMapping)usr.propertyMaps().elementAt(i);
              if (pm.cSet().equals(pcs)) addPropertyMap(pm);
          }

          /* copy the association mappings that the primary ClassSet is dependent on,
          recursively find all classes dependent on the primary class through associations, and copy their mappings */
          for (int i = 0; i < usr.assocMaps().size(); i++)
          {
              AssociationMapping am = (AssociationMapping)usr.assocMaps().elementAt(i);
              for (int thisEnd = 0; thisEnd < 2; thisEnd++)
              {
                  associationEndMapping thisMap = am.assocEnd(thisEnd);
                  associationEndMapping thatMap = am.assocEnd(1 -thisEnd);
                  if (pcs.equals(thisMap.cSet()))
                  {
                      // add the association mapping that this object depends on
                      if (thisMap.required()) {addAssocMap(am);}
                      // find other classes dependent on this class through associations
                      else if ((!thisMap.required()) && (thatMap.required()))
                      {
                          for (int tc = 0; tc < usr.objectMaps().size(); tc++)
                          {
                              objectMapping om = (objectMapping)usr.objectMaps().elementAt(tc);
                              if (thatMap.cSet().equals(om.cSet())) copyDependentMappings(usr,om);
                          }
                      }
                  }
              }
          }
      }

//---------------------------------------------------------------------------
//       iterating over possible combinations of when-condition values
//---------------------------------------------------------------------------

/**  the total number of combinations of when-condition values,
* for a unique-subtree repSet (not a single-node repSet). */
public int whenCombinations()
{
    int res = 1;
    for (Enumeration<Vector<String>> en = subtreeWhenVals.elements(); en.hasMoreElements();)
    {
        Vector<String> v = en.nextElement();
        res = res*v.size();
    }
    return res;
}

/** return a Vector of whenValue objects for one particular allowed combination
* of when-condition values. */
public Vector<whenValue> whenCombination(int index) throws MapperException
{
    Vector<whenValue> res;
    Vector<Integer> coordinateVector;
    String value, pathString;
    int i,coordinate;
    Xpth whenPath;

    res = new Vector<whenValue>();
    if ((index < 0)|(index > whenCombinations() - 1))
        {throw new MapperException("Invalid index for combination of when-condition values: " + index
            + " is not in range 0.." + (whenCombinations() - 1));}
    else
    {
    	Vector<Integer> ranges = new Vector<Integer>();
        Vector<String> pathStrings = new Vector<String>();
        Vector<Vector<String>> valueVectors = new Vector<Vector<String>>();
        for (Enumeration<String> en = subtreeWhenVals.keys(); en.hasMoreElements();)
        {
            pathString = en.nextElement();
            Vector<String> values = subtreeWhenVals.get(pathString);
            pathStrings.addElement(pathString);
            valueVectors.addElement(values);
            ranges.addElement(new Integer(values.size()));
        }
        coordinateVector = intIndex(ranges,index);
        for (i = 0; i < ranges.size(); i++)
        {
            coordinate = ((Integer)coordinateVector.elementAt(i)).intValue();
            Vector<String> values = (Vector<String>)valueVectors.elementAt(i);
            value = (String)values.elementAt(coordinate);
            pathString = (String)pathStrings.elementAt(i);
            whenPath = new Xpth(rootPath.NSSet(),pathString);
            // if there is a property mapping to this node, use it in the whenValue constructor
            ClassSet cs = null;
            String propName = null;
            for (int j = 0; j < propertyMaps().size(); j++)
            {
                propertyMapping pm = (propertyMapping)propertyMaps().elementAt(j);
                if (whenPath.equalPath(pm.nodePath()))
                {
                    cs = pm.cSet();
                    propName = pm.propertyName();
                }
            }
            res.addElement(new whenValue(whenPath,value,cs,propName));
        }
    }
    return res;
}

/* given a vector of ranges (iRange, jRange, kRange...)
and an integer 0 < n < (iRange * jRange * ...),
return a vector (i,j,k....)
such that (0 < i < iRange), (0 < j < jRange), .... and
n = i + iRange*(j + jRange*(k + ..))).
That is:
n = i + iRange* m, (so m = n/iRange; and  i = n - m*iRange)
m = j + jRange* p
..
p = k
*/
private Vector<Integer> intIndex(Vector<Integer> ranges, int n)
{
    int iRange, m, i;
    Vector<Integer> res,newRanges;
    res = new Vector<Integer>();
    if ((n > prodSize(ranges) -1)|(n < 0))
        {message("Product index error: " + n + " is not between 0 and " + (prodSize(ranges)-1));}
    else if (ranges.size() == 1)
        {res.addElement(new Integer(n));}
    else if (ranges.size() > 1)
    {
        iRange = ((Integer)ranges.elementAt(0)).intValue();
        m = n/iRange;
        i = n - m*iRange;
        newRanges = trimFirst(ranges);
        res = intIndex(newRanges,m);
        res.insertElementAt(new Integer(i),0);
    }
    return res;
}

// the product of the values in a Vector of Integers
private int prodSize(Vector<Integer> v)
{
    int res = 1;
    for (int i = 0; i < v.size(); i++)
        {res =res*((Integer)v.elementAt(i)).intValue();}
    return res;
}

// remove the first element from a Vector
private Vector<Integer> trimFirst(Vector<Integer> v)
{
    Vector<Integer> w = new Vector<Integer>();
    for (int i = 1; i < v.size(); i++) {w.addElement(v.elementAt(i));}
    return w;
}

/** write the unique subtree - those nodes below this one with mincardinality = maxCardinality = 1 */
public void writeUniqueSubtree(messageChannel mChan)
{
    mChan.message("Unique subtree structure: ");
    subTree.writeNested(mChan);
}

/** write subtree nodes and when values */
public void writeWhenVals(messageChannel mChan)
{
    if (subtreeWhenVals.size() > 0)
    {
        message("Subtree nodes and when values: ");
        for (Enumeration<String> en = subtreeWhenVals.keys();en.hasMoreElements();)
        {
            String path = en.nextElement();
            Vector<String> vals = subtreeWhenVals.get(path);
            String line = "Path '" + path + "'; Values: ";
            for (int i = 0; i < vals.size(); i++)
            {
                String val = (String)vals.elementAt(i);
                line = line + "'" + val + "' ";
            }
            mChan.message(line);
        }
    }
}

public void write(messageChannel mChan)
{
	mChan.message("******  USRepSet for node '" + rootPath.stringForm());
    String xx = "";
    if (noMeaning()) {xx = "No mappings; ";} else {xx = "Has mappings; ";}
    if (hasDefinitePaths()) {xx = xx + "definite paths; ";} else {xx = xx + "no definite paths; ";}
    if (hasIndefinitePaths()) {xx = xx + "indefinite paths. ";} else {xx = xx + "no indefinite paths. ";}
    mChan.message(xx);
    mChan.message("Def outers: " + maxOuterDefiniteSteps() +  " Def inners: " + maxInnerDefiniteStepsToTopNode());
    for (int i = 0; i < nodeRepSets.size(); i++)
    {
        nodeRepSet nrs = (nodeRepSet)nodeRepSets.elementAt(i);
        nrs.write(mChan);
    }
}


        /* the maximum number of inner steps from the top node of the unique subtree
        to a '//' step which are defined in any mapping in the subtree. Between 1 and rootPath.size()
        (= rootPath.size() if there are any mappings with definite XPaths)*/
        public int maxInnerDefiniteStepsToTopNode()
        {
            int res = (rootPath.size() - minUndefinedSteps);
            // in case the indefinite mappings are to nodes below the root of this subtree
            if ((res == 0) & hasIndefinitePaths) res = 1;
            return res;
        }

}
