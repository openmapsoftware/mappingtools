package com.openMap1.mapper.writer;

import com.openMap1.mapper.mapping.*;
import com.openMap1.mapper.core.*;
import com.openMap1.mapper.util.messageChannel;

import java.util.*;

//------------------------------------------------------------------------------------------
//  abstract superclass for sets of representations  - on one node, or on a unique subtree
//------------------------------------------------------------------------------------------

/** a mapping (object,property or association) may be in the repSet for a single node
 * for two different reasons:
 * <p>
 * - either because it is a mapping to that node
 * - or because its conditions take a value from that node.
 * <p>
 * Both can happen at once - i.e. a condition on a representation at a node
 * can take its LHS value from the same node.
 * In that case, the mapping only occurs once in the Vector 'maps'.
 * <p>
 * The unique subtree of a node includes that node itself and any nodes which are non-optional,
 * single children of that node, recursively downwards.
 * The repSet for a unique subtree includes all mappings on any of these nodes.
*/
    abstract public class repSet
    {
        /** MDL for the output XML, which provides all the mappings */
        protected MDLBase MD;
        /** path to the node */
        protected Xpth rootPath;
        
        protected messageChannel mChan;

        /** one Xpth to this node, or all XPths to nodes on the unique subtree */
        Vector<Xpth> nodePaths;

        private Vector<MappingTwo> objectMaps; // object mappings on this node or nodes
        private Vector<MappingTwo> propertyMaps; // property mappings on this node or nodes
        private Vector<MappingTwo> assocMaps; // association mappings on this node or nodes

        /** object, property or association mappings with when-condition values on this node or nodes */
        Vector<MappingTwo> whenMaps;

        /**  property or association mappings with link condition values on this node or nodes */
        Vector<MappingTwo> linkMaps;

        private boolean independentErrorWritten = false;
        /** path to the node */
        public Xpth rootPath() {return rootPath;}
        /** one Xpth to this node, or all XPths to nodes on the unique subtree */
        public Vector<Xpth> nodePaths() {return nodePaths;}
        /** object mappings on this node or nodes */
        public Vector<MappingTwo> objectMaps() {return objectMaps;}
        /** property mappings on this node or nodes */
        public Vector<MappingTwo> propertyMaps() {return propertyMaps;}
        /** association mappings on this node or nodes */
        public Vector<MappingTwo> assocMaps() {return assocMaps;}
        /** mappings with when-conditions on this node or nodes */
        public Vector<MappingTwo> whenMaps() {return whenMaps;}
        /** mappings with link conditions on this node or nodes */
        public Vector<MappingTwo> linkMaps() {return linkMaps;}
        protected void addNodePath(Xpth xp) {nodePaths.addElement(xp);}
        protected void addObjectMap(objectMapping om) {objectMaps.addElement(om);}
        protected void addPropertyMap(propertyMapping pm) {propertyMaps.addElement(pm);}
        protected void addAssocMap(AssociationMapping am) {assocMaps.addElement(am);}
        protected void addWhenMap(MappingTwo m) {whenMaps.addElement(m);}
        protected void addLinkMap(MappingTwo m) {linkMaps.addElement(m);}
//---------------------------------------------------------------------------
//                                 constructors
//---------------------------------------------------------------------------
        // constructor for a repSet for a single node, including all mappings
        /**
         *
         * @param os StructureDefinition definition of output XML sructure tree
         * @param md MDLBase mappings to output XML
         * @param rp Xpth path to this node
         */
        public repSet(MDLBase md, Xpth rp)
        {
            MD = md;
            mChan = MD.mChan();
            rootPath = rp;
            initialMaps();
            nodePaths.addElement(rp);
        }

        /** set vectors of mappings empty */
        public void initialMaps()
        {
            nodePaths = new Vector<Xpth>();
            objectMaps = new Vector<MappingTwo>();
            propertyMaps = new Vector<MappingTwo>();
            assocMaps = new Vector<MappingTwo>();
            whenMaps = new Vector<MappingTwo>();
            linkMaps = new Vector<MappingTwo>();
        }
        /** add the mappings of another repset to the mappings of this one */
        protected void appendMaps(repSet rs)
        {
            objectMaps = addMappings(objectMaps,rs.objectMaps());
            propertyMaps = addMappings(propertyMaps,rs.propertyMaps());
            assocMaps = addMappings(assocMaps,rs.assocMaps());
            whenMaps = addMappings(whenMaps,rs.whenMaps());
            linkMaps = addMappings(linkMaps,rs.linkMaps());
        }

        /** true if there are no mappings or conditions on this node */
        public boolean noMeaning()
        {
            return ((objectMaps.size() == 0 ) &&
                    (propertyMaps.size() == 0 ) &&
                    (assocMaps.size() == 0 ) &&
                    (whenMaps.size() == 0 ) &&
                    (linkMaps.size() == 0 ));
        }
        
        protected Vector<MappingTwo> addMappings(Vector<MappingTwo> v1, Vector<MappingTwo> v2)
        {
        	Vector<MappingTwo> res = new Vector<MappingTwo>();
        	for (int i = 0; i < v1.size(); i++) res.add(v1.get(i));
        	for (int i = 0; i < v2.size(); i++) res.add(v2.get(i));
        	return res;
        }
        
        protected Vector<Xpth> addXpths(Vector<Xpth> v1, Vector<Xpth> v2)
        {
        	Vector<Xpth> res = new Vector<Xpth>();
        	for (int i = 0; i < v1.size(); i++) res.add(v1.get(i));
        	for (int i = 0; i < v2.size(); i++) res.add(v2.get(i));
        	return res;
        }
        
        protected Vector<nodeRepSet> addNodeRepSets(Vector<nodeRepSet> v1, Vector<nodeRepSet> v2)
        {
        	Vector<nodeRepSet> res = new Vector<nodeRepSet>();
        	for (int i = 0; i < v1.size(); i++) res.add(v1.get(i));
        	for (int i = 0; i < v2.size(); i++) res.add(v2.get(i));
        	return res;
        }

        //---------------------------------------------------------------------------
        //                                 access methods
        //---------------------------------------------------------------------------

        /**
         * true if this is one of the node paths of the repset
         * @param rootPath Xpth
         * @return boolean
         */
        public boolean hasNodePath(Xpth rootPath) throws MapperException
    {
        boolean res = false;
        for (int i = 0; i < nodePaths.size(); i++)
        {
            if (nodePath(i).equals(rootPath)) res = true;
        }
        return res;
    }
    /** return the ith XPath to a node */
    public Xpth nodePath(int i) throws MapperException 
    {
        Xpth res = null;
        if ((i > -1) && (i < nodePaths.size())) {res = (Xpth)nodePaths.elementAt(i);}
        else message("Invalid node path index for repSet: " + i);
        return res;
    }

    	/** true if this node or subtree represents objects and possibly other things */
        public boolean hasObjects() throws MapperException
            {return (objectMaps().size() > 0);}

        /** true if this node represents objects and a nested association
        for one of them */
        public  boolean hasNestedAssociation()  throws MapperException
            {return (hasObjects() && (nestedAssociation() != null));}

        /**  Vector of associationMappings, any of whose link conditions take
        a value from this node.  */
        public Vector<MappingTwo> associationConditions()
        {
            Vector<MappingTwo> res = new Vector<MappingTwo>();
            for (int i = 0; i < linkMaps.size(); i++)
            {
            	MappingTwo mp = linkMaps.elementAt(i);
                if (mp instanceof AssociationMapping)
                    {res.addElement(mp);}
            }
            return res;
        }

        /** If this node represents an object of some class,
        and represents by nesting an association between it
        and an object represented by an outer node,
        return  the association mapping. */
        public AssociationMapping nestedAssociation()
        throws MapperException {
            int i;
            AssociationMapping am,res;
            res = null;
            for (i =0; i < assocMaps().size(); i++)
            {
                am = getAssociationRep(i);
                if (am.simpleNesting()) res = am;
            }
            return res;
        }

        /** return object representation m = 0...objectReps()-1 */
        public objectMapping getObjectRep(int m)
        {
            objectMapping res = null;
            if ((m < 0)|(m > objectMaps.size()-1)) {message("Invalid index for objectMapping in set: " + m);}
            else {res = (objectMapping)objectMaps.elementAt(m);}
            return res;
        }

        /** return object representation for a named class and subset */
        public objectMapping namedObjectRep(ClassSet cSet)
        {
            objectMapping res = null;
            for (int i=0; i < objectMaps().size(); i++)
            {
                objectMapping om = getObjectRep(i);
                if (om.cSet().equals(cSet)) res = om;
            }
            return res;
        }

        /** return property representation m = 0...propertyMaps().size()-1 */
        public propertyMapping getPropertyRep(int m)
        {
            propertyMapping res = null;
            if ((m < 0)|(m > propertyMaps.size()-1)) {message("Invalid index for propertyMapping in set: " + m);}
            else {res = (propertyMapping)propertyMaps.elementAt(m);}
            return res;
        }

        /** number of non-fixed property mappings */
        public int nonFixedPropertyMaps()
        {
            int res = 0;
            for (int i = 0; i < propertyMaps.size(); i++)
            {
                propertyMapping pm = (propertyMapping)propertyMaps.elementAt(i);
                if (!pm.fixed()) res++;
            }
            return res;
        }

        /** return one of the non-fixed property mappings */
        public propertyMapping nonFixedPropertyMap(int m)
        {
            int count = 0;
            propertyMapping res = null;
            if ((m < 0)|(m > nonFixedPropertyMaps()-1)) {message("Invalid index for non-fixed propertyMapping in set: " + m);}
            else for (int i = 0; i < propertyMaps.size(); i++)
            {
                propertyMapping pm = (propertyMapping)propertyMaps.elementAt(i);
                if (!pm.fixed())
                {
                    if (count == m) res = pm;
                    count++;
                }
            }
            return res;
        }

        /** returns a self-association
        involving a class/subset of object at both ends, if this repSet has one */
        public AssociationMapping selfAssociation(ClassSet cSet) throws MapperException 
        {
            AssociationMapping res = null;
            for (int i = 0; i < assocMaps.size(); i++)
            {
                AssociationMapping am = getAssociationRep(i);
                if ((am.assocEnd(0).cSet().equals(cSet)) &&
                    (am.assocEnd(1).cSet().equals(cSet))) {res = am;}
            }
            return res;
        }

        /** return association representation m = 0...assocMaps().size()-1 */
        public AssociationMapping getAssociationRep(int m)
        {
            AssociationMapping res = null;
            if ((m < 0)|(m > assocMaps.size()-1)) {message("Invalid index for associationMapping in set: " + m);}
            else {res = (AssociationMapping)assocMaps.elementAt(m);}
            return res;
        }

        /** find all association mappings for associations involving a (class,subset)
        * at either end of the association.
        * <p>
        * It can happen that the same association is represented twice (redundantly) in the same
        * unique subtree. In that case, this method only returns a single 'best' association
        * mapping. Currently 'best' = first discovered, i.e a mapping on the top node is preferred over one
        * in a lower node. */
        public Vector<AssociationMapping> bestAssocsTo(ClassSet cSet)  throws MapperException 
        {
            AssociationMapping am;
            Hashtable<String, AssociationMapping> assocs = new Hashtable<String, AssociationMapping>();
            for (int i = 0; i < assocMaps.size(); i++)
            {
                am = getAssociationRep(i);
                if (((am.assocEnd(0).cSet().equals(cSet))|(am.assocEnd(1).cSet().equals(cSet))) &&
                    (assocs.get(am.fullName()) == null))
                  {assocs.put(am.fullName(),am);};
            }
            Vector<AssociationMapping> res = new Vector<AssociationMapping>();
            for (Enumeration<AssociationMapping> en = assocs.elements(); en.hasMoreElements();)
                {res.addElement(en.nextElement());}
            return res;
        }

        /** return one of the link conditions whose LHS or RHS value is stored in this node or unique subtree -
        assuming that any mapping can have at most one link condition value in one subtree?.*/
        public linkCondition linkCondition(int i)  throws MapperException 
        {
            linkCondition lc,lt;
            lc = null;
            if ((i < 0)|(i > linkMaps().size()-1)) message("Invalid index for link condition mapping: " + i);
            else
            {
                MappingTwo m = (MappingTwo)linkMaps().elementAt(i);
                if (m instanceof propertyMapping)
                {
                    lt = linkConditionOnUniqueSubtree(m);
                    if (lt != null) {lc = lt;}
                }
                else if (m instanceof AssociationMapping) for (int end = 0; end < 2; end++)
                {
                    AssociationMapping am = (AssociationMapping)m;
                    lt = linkConditionOnUniqueSubtree(am.assocEnd(end));
                    if (lt != null) {lc = lt;}
                }
            }
            if (lc == null) {message("Failed to find link condition " + i);}
            return lc;
      }


        private linkCondition linkConditionOnUniqueSubtree(MappingTwo m) throws XpthException
      {
          linkCondition lc = null;
          // for (int i = 0; i < nodePaths().size(); i++) {message("node path " + i + " " + ((Xpth)nodePaths().elementAt(i)).stringForm());}
          for (int k = 0; k < m.linkConditions().size(); k++)
          {
              linkCondition lt = m.linkConditions().elementAt(k);
              if (lt.rootToLeftValue().compatibleOneOf(nodePaths())) lc = lt;
              if (lt.rootToRightValue().compatibleOneOf(nodePaths())) lc = lt;
              // message("Left: " + lt.rootToLeftValue().stringForm() + " Right: " + lt.rootToRightValue().stringForm());
          }
          return lc;
      }

        //-------------------------------------------------------------------------------
        //        Allowed combinations of representations on one node
        //-------------------------------------------------------------------------------

        /*
Some of the rules to be validated here:
Rules for object and association representations:
(1) If a node contains no object representations, it can contain any number
    of association representations, but only if they have mutually exclusive
    'when' conditions - so that each instance of the node can only represent
    one association. (e.g for a node which generically represents one of
    several different associations)
(2) If a node contains just one object representation, then all
    the association representations on the same node must
    have that object on one end - so a node cannot simultaneously represent
    an object and an unrelated association.
(3) If a node contains more than one object representation,
    then there must be a primary object (whose inclusion filters
    do not depend on any of the other objects, and which may or may not
    have a nesting association to some outer object).
    All other (secondary) objects on the node must have inclusion filters which
    depend, directly or indirectly, on the primary object.
(4) When a node contains N object representations
    (which do not have mutually exclusive when-conditions)
    it must also represent linking associations
    between all the objects. These must form a tree rooted at
    the primary object. Every one of these associations must have
    all three nodes (association node and two end nodes) identical,
    and have no link conditions, and must have one end (the end
    furthest from the primary object in the tree) 'required'
    to provide the necessary inclusion filter.
(5) A node with N object representations can have any number of other
    association representations, but each one of these must involve an
    object represented on the node.
Rules for property and link condition representations:
(1) A node cannot represent two or more properties of the same object,
    unless their inclusion filters are mutually exclusive (as in
    a generic representation of several properties)
(2) If a node represents two properties of different objects in different classes,
    the objects must be related by some association,
    which I think should not be an M:N association.
(3) If a node represents the RHS of a link condition for several
    association ends, the class of the association end must be the same
    in all cases.
(4) If a node represents a property in some class and the RHS of a link
    condition for an association end, the association end class must be
    the same as the property class.
*/

        /**  return the objectMapping for the one object in this repSet
	which is not dependent on any of the others
	or null if there is any problem (error message was output) */
public objectMapping primaryObject()
{
    objectMapping res = null;
    if (objectMaps.size() > 0)
    {
        Vector<MappingTwo> allObjs = allObjectReps();
        if (allObjs != null)
            {res = (objectMapping)allObjs.elementAt(0);}
    }
    return res;
}
 /** return a Vector of all objectMappings in this repSet,
 * in order of their dependencies; the primary object first, with
 * every other object occurring after the objects it is dependent on.
 * <p>
 * Returns null if there is any error.
 */
 public Vector<MappingTwo> allObjectReps()
 {
    int i;
    objectMapping om,primary;
    Vector<MappingTwo> res,someObjects;
    int independent;
    res = new Vector<MappingTwo>();
    primary = null;
    if (objectMaps.size() > 1)
    {
        // find the one class which is not dependent on any of the others
        independent = 0;
        String independentClasses = "";
        for (i = 0; i < objectMaps.size(); i++)
        {
            om = (objectMapping)objectMaps.elementAt(i);
            if (!dependentOn(om,objectMaps))
            {
                independent++;
                primary = om;
                independentClasses = independentClasses + " " + om.cSet().stringForm();
            }
        }
        if ((independent == 0) && !independentErrorWritten)
        {
            res = null;
            message("Error: every object represented on node '"
                + rootPath.stringForm()
                + "' depends on some of the others represented on the same node.");
            objectMapsMessage(mChan);
            message("");
            independentErrorWritten = true; // only write this message once
        }
        else if ((independent  > 1) && !independentErrorWritten)
        {
            res = null;
            message("Error: there is more than one independent object represented on node '"
                + rootPath.stringForm() + "' and its unique subtree");
            message("which depend on none of the others.");
            message("Independent classes: " + independentClasses);
            objectMapsMessage(mChan);
            message("");
            independentErrorWritten = true; // only write this message once
        }
        else if (independent == 1)
        {
            res.addElement(primary);
            someObjects = MappingTwo.vCopy(objectMaps);
            someObjects.removeElement(primary);
            addNonDependents(res,someObjects);
        }
    }
    else {res = objectMaps;}  // case when there is only one object represented
    return res;
 }
 /** write out all mappings in the repset */
 public void writeMappings()
 {
	 messageChannel mChan = MD.mChan();
    objectMapsMessage(mChan);
    propertyMapsMessage(mChan);
    associationMapsMessage(mChan);
 }
 private void objectMapsMessage(messageChannel mChan)
 {
    String omessage;
    if (objectMaps.size() > 0)
    {
        omessage = ("The classes represented are: ");
        for (int i = 0; i < objectMaps.size(); i++)
        {
            objectMapping om = (objectMapping)objectMaps.elementAt(i);
            omessage = omessage + (om.cSet().stringForm() + " ");
        }
    }
    else {omessage = ("No classes represented.");}
    mChan.message(omessage);
 }
 private void propertyMapsMessage(messageChannel mChan)
 {
    String pmessage = ("Properties represented are: ");
    for (int i = 0; i < propertyMaps.size(); i++)
    {
        propertyMapping pm = (propertyMapping)propertyMaps.elementAt(i);
        pmessage = pmessage + (pm.cSet().stringForm() + ":" + pm.propertyName() + " ");
    }
    if (propertyMaps.size() > 0) mChan.message(pmessage);
 }
 private void associationMapsMessage(messageChannel mChan)
 {
    String amessage = ("Associations represented are: ");
    for (int i = 0; i < assocMaps.size(); i++)
    {
        AssociationMapping am = (AssociationMapping)assocMaps.elementAt(i);
        amessage = amessage + (am.fullName() + " ");
    }
    if (assocMaps.size() > 0) mChan.message(amessage);
 }
 /* true if the object mapping om has a dependency on one of
  the (class,subsets) in objectReps.
  Check this by finding an association filters of the object,
  where it depends on one of the objects in objectReps */
 private boolean dependentOn(objectMapping om, Vector<MappingTwo> objectReps)
 {
    objectMapping omp;
    filterAssoc fa;
    int i,j;
    boolean res = false;
    for (j = 0 ; j < om.filterAssocs().size(); j++)
    {
        fa = om.filterAssocs().elementAt(j);
        if (!fa.failToInclude()) for (i = 0; i < objectReps.size(); i++)
        {
            omp = (objectMapping)objectReps.elementAt(i);
            if (fa.depCSet().equals(omp.cSet())) res = true;
        }
    }
    return res;
 }
 /* add objectMappings from remnants to vec, in order such
 that no object depends on objects later in the list. */
 private void addNonDependents(Vector<MappingTwo> vec,Vector<MappingTwo> remnants)
 {
    objectMapping om;
    int i;
    Vector<MappingTwo> newRemnants;
    boolean found = false;
    for (i = 0; i < remnants.size(); i++) if (!found)
    {
        om = (objectMapping)remnants.elementAt(i);
        if (!dependentOn(om,remnants))
        {
            found = true;
            vec.addElement(om);
            newRemnants = MappingTwo.vCopy(remnants); 
            newRemnants.removeElement(om);
            addNonDependents(vec,newRemnants);
        }
    }
 }
 /** return the association mapping which links this object to the
 object it is dependent on in this repSet, or null if there is none. */
 public AssociationMapping linkAssociation(objectMapping om)  throws MapperException 
 {
    int i,j,k; 
    AssociationMapping am,res;
    associationEndMapping aem,bem;
    objectMapping omp;
    res = null;
    // check all association mappings  on this node in this repSet.
    for (i = 0; i < assocMaps.size(); i++)
    {
            am = (AssociationMapping)assocMaps.elementAt(i);
            for (j = 0; j < 2; j++)
            {
                aem = am.assocEnd(j);
                bem = am.assocEnd(1-j);
                // current object is at end aem
                if ((aem.cSet().equals(om.cSet())) && (aem.required()))
                {
                    // other end object bem mst be represented in this repSet
                    for (k = 0; k < objectMaps().size(); k++)
                    {
                        omp = (objectMapping)objectMaps().elementAt(k);
                        if (bem.cSet().equals(omp.cSet())) {res = am;}
                    }
                    if (res == null)
                    {
                        message("");
                        message("Link association '" + am.assocName() + "' from object in class " + om.cSet().stringForm());
                        message("links class " + bem.cSet().stringForm() + " not represented on the same node.");
                    }
                }
            }
    }
    return res;
 }
 /** return true if this object mapping is not dependent
 on any other object mapping on this node */
 public boolean independentObject(objectMapping om)
    {return !dependentOn(om,objectMaps());}
  /** return a vector of all object mappings which are not not dependent
 on any other object mapping on this node */
public Vector<objectMapping> getIndependentObjects()
 {
    int i;
    objectMapping om;
    Vector<objectMapping> res = new Vector<objectMapping>();
    for (i = 0; i < objectMaps().size(); i++)
    {
        om = (objectMapping)objectMaps().elementAt(i);
        if (independentObject(om)) {res.addElement(om);}
    }
    return res;
 }
 /** check that all objects represented in this node, except the
 * primary object, have some link association to another object
 * represented on this node.
 * <p>
 * Used by MDLChecker? */
 public boolean hasLinkAssociationReps()  throws MapperException 
 {
    int i;
    boolean res;
    Vector<MappingTwo> oReps;
    objectMapping om;
    res = true;
    oReps = allObjectReps();
    // check all objects except the primary object have a linking association
    for (i = 0; i < oReps.size(); i++)
    {
        om = (objectMapping)oReps.elementAt(i);
        if ((!independentObject(om)) && (linkAssociation(om) == null))
        {
            res = false;
            message("");
            message("At node '" + rootPath.stringForm() + "' the object of class "
                + om.cSet().stringForm());
            message("has no linking association to other objects on the node.");
        }
    }
    return res;
 }
//-------------------------------------------------------------------------------
//                           Odds & sods
//-------------------------------------------------------------------------------
    /** message to system console */
    void message(String s) {mChan.message(s);}
}
