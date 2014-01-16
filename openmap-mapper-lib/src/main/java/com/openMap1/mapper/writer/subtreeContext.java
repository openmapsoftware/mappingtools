package com.openMap1.mapper.writer;

import com.openMap1.mapper.core.*;
import com.openMap1.mapper.reader.*;
import com.openMap1.mapper.mapping.*;

import java.util.*;

/**
 * 
 *  defines what makes one subtree of the output XML different from another
 *  with the same root path.
 *  
 *  Consists of:
 *  (1) a set of objectTokens for objects uniquely defined by elements
 *  above this subtree.
 *  (2) a set of when-condition values - values for the RHS of when-conditions and root paths
 *  of nodes where those values are to be stored.
 *  
 *  The set of objectTokens includes objects represented on the elements above this subtree,
 *  and any objects related to them by M:1 associations.
*/

public class subtreeContext extends outputContext
{

/* Vector of objectTokens for objects directly represented on ancestor nodes;
stays in step with uniqueCSets. */
private Vector<objectToken> directObjects;

private objectGetter oGet; // input object getter
private ProcedureClass outputMDL; // output mappings

public subtreeContext(MDLBase md, Xpth rp, objectGetter og, ProcedureClass omdl)
{
    super(md, rp);
    oGet = og;
    outputMDL = omdl;

    directObjects = new Vector<objectToken>();
}

// make a deep copy of this subtree context
public subtreeContext copySC() throws MDLWriteException
{
    int i;
    subtreeContext ct = new subtreeContext(MD,rootPath().copy(),oGet,outputMDL);
    for (i = 0; i < uniqueCSets().size(); i++)
        {ct.addUniqueCSet(uniqueCSets().elementAt(i));}
    for (Enumeration<whenValue> en = whenValues().elements(); en.hasMoreElements();)
        {ct.setWhenValue(en.nextElement());}
    for (i = 0; i < directObjects.size(); i++)
        {ct.directObjects.addElement(directObjects.elementAt(i));}
    return ct;
}

/* get the top objectToken in the context stack , of any class and subset.
This is used for XSLT generation, where the top object in the context
determines the current node in the input XML.
Will need to deal later with overloading of nodes - classes related to the top
class by M:1 associations  */
public objectToken getTopObjectToken()
{
    objectToken res = null;
    int size = directObjects.size();
    if (size > 0) {res = directObjects.elementAt(size -1);}
    return res;
}

/* get the top objectToken in the stack for an object in a given class and (output) subset.
Note we do _not_ use the subset information in the objectRep, because that
refers to subsets in the input XML, not in the output XML as we require.
Input class can be a subclass of output class - need to deal with that properly later.*/
public objectToken getObjectTokenByOutputClassSet(ClassSet cSet) throws MDLWriteException
{
    objectToken res = null;

    /* try to find the top objectToken amongst those directly stored in the context;
     i.e. the last matching one in the vector. */
    for (int i = 0; i < uniqueCSets().size(); i++)
    {
        objectToken oTok = directObjects.elementAt(i);
        ClassSet cs = uniqueCSets().elementAt(i);
        // message("Trying ClassSet " + cs.stringForm());
        if (cSet.equals(cs)) {res = oTok;}
    }

    if (outputMDL.runTracing() && (res != null))
        {message("Got token for object of class " + cSet.stringForm() + " from context");}

    return res;
}

/**
 * 
 * @param cSet an input classSet
 * @return any objectRep in the context that has the input clasSet
 * Used olny in XSLT generation
 * @throws MDLWriteException
 */
public objectRep getObjectRepByInputClassSet(ClassSet cSet) throws MDLWriteException
{
    objectRep res = null;
    
    for (int i = 0; i < directObjects.size(); i++)
    {
    	objectToken oTok = directObjects.get(i);
    	if (oTok instanceof objectRep)
    	{
    		objectRep oRep = (objectRep)oTok;
    		if (oRep.cSet().equals(cSet)) res = oRep;
    	}
    }
    return res;
}
/* add an object in a class and subset to the top of the runtime stack.
Do nothing and return false if the class name does not match the expected ClassSet   */
public boolean addObject(objectToken oTok, ClassSet cSet)
{
    boolean res = true;
    if (!(oTok.className().equals(cSet.className())))
    {
        message("Different class names '"
          + oTok.className() + "' and '" + cSet.className() + "'");
        res = false;
    }
    if (res)
    {
        directObjects.addElement(oTok);
        addUniqueCSet(cSet);
    }
    return res;
}


void message(String s) {System.out.println(s);}

}
