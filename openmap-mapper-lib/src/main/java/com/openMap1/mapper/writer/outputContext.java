package com.openMap1.mapper.writer;
import java.util.*;

import com.openMap1.mapper.mapping.*;
import com.openMap1.mapper.core.*;
import com.openMap1.mapper.NodeDef;

/**
 * represents the context when outputting XML - mainly the node of
 * the output document you are on, and what objects are uniquely defined (represented)
 * by that node and higher nodes.
 *
 * All paths have namespace prefixes as in the output XML structure definition,
 * not the MDL.
 */

public class outputContext {

      // definite path from the root to the node of the current procedure
      private Xpth rootPath;
      
      private NodeDef contextNode = null;
      public NodeDef contextNode() {return contextNode;}
      
      protected MDLBase MD;

      /* stack of classSets for anticipated or actual uniquely defined objects - those
      represented by outer nodes, or objects which can be got from them
      by following M:1 associations.
      Should match the classSets in the runtime subtreeContext at the same node. */
      private Vector<ClassSet> uniqueCSets;

      /* whenValue objects for anticipated choices of values of when-condition
      nodes, above this node or in its unique subtree.
      Hashtable has key = root path string form, value = whenValue object
      Should match the whenValues in the runtime structureContext at the same node. */
      private Hashtable<String, whenValue> whenValues;

      public Xpth rootPath() {return rootPath;}
      public Vector<ClassSet> uniqueCSets() {return uniqueCSets;}
      public Hashtable<String, whenValue> whenValues() {return whenValues;}

      public void addUniqueCSet(ClassSet cs) {uniqueCSets.addElement(cs);}

      outputContext(MDLBase md, Xpth rp)
      {
          rootPath = rp;
          MD = md;
          contextNode = MD.ms().getNodeDefByPath(rp.stringForm());
          uniqueCSets = new Vector<ClassSet>();
          whenValues = new Hashtable<String, whenValue>();
      }


      // deep copy as starting point for changes to a new version
      public outputContext copyOC() throws MDLWriteException
      {
          int i;
          outputContext oc = new outputContext(MD,rootPath().copy());
          for (i = 0; i < uniqueCSets.size(); i++)
              {oc.addUniqueCSet(uniqueCSets.elementAt(i));}
          for (Enumeration<whenValue> en = whenValues.elements(); en.hasMoreElements();)
              {oc.setWhenValue(en.nextElement());}
          return oc;
      }

      /* at some time I might make checks on paths and throw an exception -
      */
      public void setWhenValue(whenValue wv) throws MDLWriteException
      {
          whenValues.put(wv.rootPath().stringForm(),wv);
      }

      public void setRootPath(Xpth rp) throws MDLWriteException
      {
          rootPath = rp;
      }



/* true if this context contains a when-condition value
which matches wv in both root path and value. */
public boolean matchesWhenValue(whenValue wv)
{
    boolean res = false;
    for (Enumeration<whenValue> en = whenValues.elements(); en.hasMoreElements();)
    {
        whenValue ww = en.nextElement();
        if (ww.equals(wv)) res = true;
    }
    return res;
}

// true if the object stack has one or more objects in this class and subset
public boolean hasObject(ClassSet cSet)
{
    boolean res = false;
    for (int i = 0; i < uniqueCSets.size(); i++)
        if (cSet.equals(uniqueCSet(i))) {res = true;}
    return res;
}

private ClassSet uniqueCSet(int i) {return uniqueCSets.elementAt(i);}


/* filter a vector of association mappings, so the result contains only
associations involving objects in the runtime context. */
public Vector<AssociationMapping> vetAssocs(Vector<AssociationMapping> allAssocs)  throws MapperException
{
    Vector<AssociationMapping> assocs = new Vector<AssociationMapping>();
    for (int i = 0; i < allAssocs.size(); i++)
    {
        AssociationMapping am = (AssociationMapping)allAssocs.elementAt(i);
        if ((hasEndInContext(am.nodePath(),am.assocEnd(0)))|
            (hasEndInContext(am.nodePath(),am.assocEnd(1))))
            {assocs.addElement(am);}
    }
    return assocs;
}

/* find the end  ( = 1 or 2) of an association which connects to an object not in the runtime context.
Return 0 if neither end connects. */
public int endNotInContext(AssociationMapping am)  throws MapperException
{
    int res = 0;
    if (!hasEndInContext(am.nodePath(),am.assocEnd(0))) {res = 1;}
    else if (!hasEndInContext(am.nodePath(),am.assocEnd(1))) {res = 2;}
    return res;
}

/* True if this end of the association connects to an object already in the runtime context.
*/
public boolean hasEndInContext(Xpth rootPath,associationEndMapping aem)
throws MapperException
{
    boolean res = false;
    NodeDef mappedNode = MD.ms().getNodeDefByPath(rootPath.stringForm());
    if (hasObject(aem.cSet()))
    {
        /* the object may be of a ClassSet that is in context; but the actual object is only in context
        if the relative path from the association node to the object node leads to a unique node,
        i.e. if it was in the unique subtree of some higher context. */
        res = mappedNode.uniquePath(aem.assocToObj());
    }
    return res;
}


public String contextClasses()
{
    String toWrite = "Classes in context: ";
    for (int i = 0; i < uniqueCSets.size(); i++)
    {
        ClassSet cs = uniqueCSets.elementAt(i);
        toWrite = toWrite + cs.stringForm()+ " ";
    }
    return toWrite;
}



}
