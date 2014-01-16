package com.openMap1.mapper.core;

import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.core.XpthException;
import com.openMap1.mapper.core.NamespaceException;

import java.util.*;

//-------------------------------------------------------------------------------------
//                            XPath class
//-------------------------------------------------------------------------------------
/**
 *
 * an instance represents an XPath in an XML document.
 *
 *
 * @author Robert Worden
 * @version 1.0
 */
    public class Xpth
    {
    	private Vector<step> steps = new Vector<step>(); // Vector of step objects

    	private NamespaceSet NSSet;

    	/** the set of namespaces used for node names in this XPath */
    	public NamespaceSet NSSet() {return NSSet;}

    	/* true if this is a path from the root node
        false if it is a path from the current node. */
        private boolean fromRoot = false;

        /**
         * @return boolean true if this is a path from the root node; false if it is a path from the current node.
         */
        public boolean fromRoot() {return fromRoot;}

        /** set true if this is a path from the root node; set false if it is a path from the current node. */
        public void setFromRoot(boolean b) {fromRoot = b;}

//-------------------------------------------------------------------------------------
//                            Constructors
//-------------------------------------------------------------------------------------

        
        /**
         * constructor for a general (absolute or relative) XPath.
         *
         * @param nss NamespaceSet namespaces used in steps  in this path
         * @param XPath String string form of the path (begins with '/' for a path from the root)
         * empty paths are allowed; but since their stringForm() is 'empty path' we must make the round trip compatible.
         * <p>
         * so '' or 'empty path' are both recognised
         */
        public Xpth(NamespaceSet nss, String XPath) throws XpthException
        {
            NSSet = nss;
            makePath(XPath);
        }
        

        private void makePath(String XPath) throws XpthException
        {
        	String rest;
            if (XPath == null)
            {
                throw new XpthException("Attempt to form null XPath");
            }
            /*empty paths are allowed; but since their stringForm()
            is 'empty path' we must make the round trip compatible. */
            else if ((XPath.equals(""))|(XPath.equals("empty path")))
            {
                // leave vector 'steps' empty and fromRoot false
            }
            else
            {
                // recognise paths from the root, stripping off the first '/'
                if (XPath.charAt(0) == '/')
                {
                    fromRoot = true;
                    rest = XPath.substring(1);
                    /* if XPath started  with '//', rest now starts with '/', so the first
                    step string will be "", so the first step will have axis 'descendant-or-self'. */
                }
                else
                {
                    fromRoot = false;
                    rest = XPath;
                }
                Vector<String> stepStrings = stepStringVector(rest);
                // extend vector of steps
                for (int i = 0; i < stepStrings.size(); i++)
                {
                	addStep(stepStrings.elementAt(i));
                }
            }        	
        }
        
        /**
         * constructor for new empty (relative) XPath.
         *
         * @param nss NamespaceSet
         */
        public Xpth(NamespaceSet nss)
        {
            NSSet = nss;
        }
        
        /**
         * 
         * @return a copy of this Xpth
         */
        public Xpth copy()
        {
        	Xpth copy = new Xpth(NSSet);
        	try {copy = new Xpth(NSSet, stringForm());}
        	// if this Xpth is valid, copying it will not cause problems
        	catch (XpthException ex) {GenUtil.surprise(ex,"Xpth.copy");}
        	return copy;
        }

        /** convert a string XPath into a Vector of Strings for steps in the path separated by '/',
            * outermost step string first.
            * <p>
	    * Always return some Vector - with one element if there are no '/' in the path. */
	    private Vector<String> stepStringVector(String XPath)
	    {
	        Vector<String> res = new Vector<String>();
	        if (theInnerString(XPath).equals(XPath)) {res.addElement(XPath);}
	        else
	        {
	            res = stepStringVector(removeInnerString(XPath));
	            res.addElement(theInnerString(XPath));
	        }
	        return res;
	    }
 
	    /** find the innermost substring n3 of a path represented as n1/n2/n3;
     * <p>
    * or the empty string "" if path ends in '/', such as n1/n2/n3/ ;
    * <p>
    * or the whole string if there is no '/'. */
    private String theInnerString(String nodePath)
    {
        int i,len,last;
        boolean found;
        String res;
        res = "erroneously not set";
        found = false;
        len = nodePath.length();
        // find the position of the last slash
        last = -1;
        for (i = 0; i < len; i++)
            if (nodePath.charAt(i) == '/') {last = i; found = true;}
        // substring after last slash
        if (found)
        {
            if (last < len-1) {res = nodePath.substring(last+1);}
            else {res = "";} // last  = (len-1) means slash was the very last character
        }
        else {res = nodePath;} // no slash found
        return res;
    }
 
    /** remove the innermost element of a path represented as n1/n2/n3
    * converting it to n1/n2.   n3 might be an attribute node such as '@name'.
    * <p>
    * If the path contains no '/', return the whole path.
    * <p>
    * If the path starts with '/' and there are no other '/', return "". */
    private String removeInnerString(String nodePath)
    {
        int i,len,last;
        boolean found;
        String res;
        res = "wrongly not set";
        found = false;
        len = nodePath.length();
        // find the position of the last '/' character
        last = 0;
        for (i = 0; i < len; i++)
            if (nodePath.charAt(i) == '/') {last = i; found = true;}
        // substring before that character
        if (found)
        {
            if (last > 0) {res = nodePath.substring(0,last);}
            else {res = "";} // last = 0; slash was very first character
        }
        else {res = nodePath;}
        return res;
    }

//-------------------------------------------------------------------------------------
//                            Inner class for steps, and related methods
//-------------------------------------------------------------------------------------
    
    
    
	public static boolean ascending(String axis)
	{
		return ((axis.equals("parent"))|
				(axis.equals("ancestor"))|(axis.equals("ancestor-or-self")));
	}
	  
	public static boolean descending(String axis)
	{
		return ((axis.equals("child"))|(axis.equals("attribute"))|
				(axis.equals("descendant"))|(axis.equals("descendant-or-self")));
	}
    

    /**
     *
     * <p>Title: </p>
     *
     * <p>Description: Inner class for steps in XPaths, and related methods.</p>
     *
     * @author Robert Worden
     * @version 1.0
     */
public class step
{

	String axis;
	/** the axis is parent, child, ancestor, etc. */
    public String axis() {return axis;}
    
    String nodeTest;
    /** the node test is the node name (without '@') or node(), etc. */
    public String nodeTest() {return nodeTest;}

    boolean isNamedNode;
    String[] axes = {"ancestor","ancestor-or-self","attribute","child",
        "descendant","descendant-or-self","following","following-sibling",
        "namespace","parent","preceding","preceding-sibling","self"};

    public step(String stepString) throws XpthException
    {
        // short forms
        // initial '//' or '//' anywhere else
        if (stepString.equals(""))
        {
            axis = "descendant-or-self";
            nodeTest = "node()";
        }
        // self
        else if (stepString.equals("."))
        {
            axis = "self";
            nodeTest = "node()";
        }
        // attributes
        else if (stepString.startsWith("@"))
        {
            axis = "attribute";
            nodeTest = stepString.substring(1);
        }
        // abbreviated parent step
        else if (stepString.startsWith(".."))
        {
            axis = "parent";
            if (stepString.length() == 2)
                {nodeTest = "node()";}
            else if (stepString.length() > 2)
                {nodeTest = stepString.substring(2);}
        }
        // wild card elements
        else if (stepString.equals("*"))
        {
            axis = "child";
            nodeTest = "node()";
        }
        // long forms
        else
        {
            axis = beforeColons(stepString);
            if (axis.equals("")) {axis = "child";}
            if (! GenUtil.inArray(axis,axes))
                {throw new XpthException("Invalid axis for XPath: " + axis);}
            nodeTest = afterColons(stepString);
        }
        isNamedNode = true;
        if ((nodeTest.equals("*"))|
            (nodeTest.equals("node()"))|
            (nodeTest.equals("comment()"))|
            (nodeTest.equals("text()"))|
            (nodeTest.equals("processing-instruction()")))
                {isNamedNode = false;}
    }
    
    String stringForm()
    {
        String res = "undefined step";
        if ((axis.equals("descendant-or-self")) && (nodeTest.equals("node()"))) {res = "";}
        // the long form of 'self::node()' seems to be needed by Saxon to allow [] expressions after
        // else if ((axis.equals("self")) && (nodeTest.equals("node()"))) {res = ".";}
        else if (axis.equals("attribute")) {res = "@" + nodeTest;}
        else if (axis.equals("child")) {res = nodeTest;}
        else {res = axis + "::" + nodeTest;}
        return res;
    }

    public boolean isAttribute() {return(axis.equals("attribute"));}

    /**
     * @return true if this step is compatible with a namespace set.
     * If the node test is a node name with a prefix, the prefix must be one
     * of those in the namespace set, or "xmlns"
     */
    boolean isNamespaceCompatible(NamespaceSet nss)
    {
    	boolean compatible = true;
    	StringTokenizer st = new StringTokenizer(nodeTest,":");
    	if (st.countTokens() > 1) // if there is a prefix....
    	{
    		String prefix = st.nextToken(); // ... it is the part before the ':'
    		compatible = ((nss.getByPrefix(prefix) != null)|(prefix.equals("xmlns")));
    	}
    	return compatible;
    }
}

public step step(int i) throws XpthException
{
    if ((i < 0)|(i > steps.size() - 1)) {throw new XpthException("Step index: " + i
        + " out of range 0.." + (steps.size() - 1) + " in path '" + stringForm() + "'.");}
    step res = steps.elementAt(i);
    return res;
}

// the substring of s before the first "::" pair; or "" if there is no pair.
private String beforeColons(String s)
{
    String res = "";
    int i = colonPos(s);
    if (i > 0) {res = s.substring(0,i);}
    return res;
}

// the substring of s after the first "::"pair; or the whole string if there is no pair.
private String afterColons(String s)
{
    String res = s;
    int i = colonPos(s);
    if (i > 0) {res = s.substring(i+2);}
    return res;
}

/* position in a string of the first of the first-found '::' pair of colons,
or -1 if there is no such pair in the string. */
private int colonPos(String s)
{
    int i,res;
    res = -1;
    boolean found = false;
    for (i = 0; i < s.length()-2; i++) if (!found)
    {
        if ((s.charAt(i) == ':') && (s.charAt(i+1) == ':'))
            {res = i; found = true;}
    }
    return res;
}
//-------------------------------------------------------------------------------------
//                            modifiers and creators of paths
//-------------------------------------------------------------------------------------
        /**
         * add an inner node, changing this Xpth.
         * @throws XpthException if the step string is not valid
         * @param stepString String
         */
        public void addStep(String stepString)  throws XpthException
        {
        	step s = new step(stepString);
        	if (!s.isNamespaceCompatible(NSSet)) 
        		throw new XpthException("Step '" + stepString 
        				+ "' is not in any namespace of the XPath to which it is being added.");
            steps.addElement(s);
        }

        /**
         * create a new Xpth with an inner step added.
         *
         * @param stepString String the step (node name)
         * @return Xpth; this XPath, modified by adding the extra inner step
         */
        public Xpth addInnerStep(String stepString) throws XpthException
        {
            Xpth res = null;
            if (emptyPath())
            {
                res = new Xpth(NSSet,"/" + stepString);
                res.fromRoot = true;
            }
            else
            {
                res = new Xpth(NSSet,stringForm()); // copy of this Xpth
                res.fromRoot = fromRoot;
                res.addStep(stepString);
            }
            return res;
        }

        /**
         * create a new Xpth with an outer step added - not a root path.
         *
         * @param step String the step (node name)
         * @return Xpth this XPath, modified by adding the extra inner step
         */
        public Xpth addOuterStep(String step)  throws XpthException
        {
            Xpth res = new Xpth(NSSet,step);
            for (int i = 0; i < size(); i++)
            {
                res.addStep(stepString(i));
            }
            return res;
        }

    /** new Xpth with the outermost step removed. */
    public Xpth removeOuterStep()  throws XpthException
    {
        Xpth xp = new Xpth(NSSet);
        xp.fromRoot = false;
        if (emptyPath()) {throw new XpthException("Cannot remove the outer step from an empty path.");}
        // do nothing if steps.size() == 1, leaving xp.steps with zero elements
        if (steps.size() > 1) for (int i = 1; i < steps.size(); i++)
            {xp.addStep(step(i).stringForm());}
        return xp;
    }

    /** new Xpth with the innermost step removed.*/
    public Xpth removeInnerStep()  throws XpthException
    {
        Xpth xp = new Xpth(NSSet);
        xp.fromRoot = fromRoot;
        if (emptyPath()) {throw new XpthException("Cannot remove the inner step from an empty path.");}
        // do nothing if steps.size() == 1, leaving xp.steps with zero elements
        if (steps.size() > 1) for (int i = 0; i < steps.size()-1; i++)
            {xp.addStep(step(i).stringForm());}
        return xp;
    }

    /**
     * convert all namespace prefixes in tag names in this XPath, i.e match URIs to change prefixes.
     *
     * @param toNSSet NamespaceSet - the new namespace set
     * @return Xpth - the XPth with modified prefixes
     */
    public Xpth convertPrefixes(NamespaceSet toNSSet) throws XpthException,NamespaceException
    {
        int i;
        String cName;
        Xpth res = new Xpth(toNSSet);
        res.fromRoot = fromRoot;
        for (i = 0; i < size(); i++)
        {
            cName = nodeName(i);
            /* do not convert prefixes of attributes; they should not have them;
             * and do not convert the 'node()' test. */
            if        ((axis(i) != null) 
            		&& (!axis(i).equals("attribute")) 
            		&& (!nodeName(i).equals("node()")))
            {
            		cName = NSSet.convertPrefix(cName,toNSSet);
            }
            res.addStep(axis(i) + "::" + cName);
        }
        return res;
    }

    /**
     * create a new Xpth which includes just the steps up to step i of this path.
     *
     * @param i int steps 0..i of this path are included
     * @return Xpth the resulting path
     */
    public Xpth truncateTo(int i)  throws XpthException
    {
        Xpth res = null;
        if ((i > -1) && (i < size()))
        {
            res = new Xpth(NSSet);
            for (int j = 0; j < i+1; j++) {res.addStep(step(j).stringForm());}
            res.setFromRoot(fromRoot);
        }
        else {throw new XpthException("Invalid step " + i + " in truncating path '" + stringForm() + "'");}
        return res;
    }
    
//-------------------------------------------------------------------------------------
//                            access methods - whole path
//-------------------------------------------------------------------------------------
    
    /** number of steps in the path */
    public int size() {return steps.size();}
    
        /** number of steps inside the innermost 'descendant-or-child' step; or 0 for a definite path */
        public int innerSize()  throws XpthException
        {
            int s = 0;
            if (!definite()) for (int i = 0; i < steps.size(); i++)
            {
                s++;
                if (step(i).axis.equals("descendant-or-self")) s = 0;
            }
            return s;
        }

        /** true if this is an empty path */
        public boolean emptyPath() {return size() == 0;}
        /** true if this is the 'stay here' path '.' */

        public boolean selfPath()  throws XpthException
            {return ((size() == 1) && (step(0).axis.equals("self")));}

        /** short string form of the path. */
        public String stringForm() 
        {
            String res = "";
            if (fromRoot) {res = "/";}
            for (int i = 0; i < steps.size(); i++)
            {
                try {res = res + step(i).stringForm();}
                // Exception i out of range cannot happen
                catch (XpthException ex) {GenUtil.surprise(ex,"Xpth.stringForm");}
                if (i < steps.size()-1) res = res + "/";
            }
            if (size() == 0) {res = "empty path";}
            return res;
        }

        /** true if this is a path to an attribute node */
	    public boolean isAttributePath()  throws XpthException
            {return (step(steps.size()-1).isAttribute());}


	    
	    /**
             * true if the path contains a step with this axis.
             *
             * @param axis String valid values are "ancestor","ancestor-or-self","attribute","child",
             * <p>
             * "descendant","descendant-or-self","following","following-sibling",
             * <p>
             * "namespace","parent","preceding","preceding-sibling","self"
             * @return boolean
             */
    public boolean hasAxis(String axis) throws XpthException
    {
        boolean res = false;
        for (int i = 0; i < size(); i++)
            if (step(i).axis.equals(axis)) res = true;
        return res;
    }
    /** true if the path is a pure ascent with one or more steps */
    public boolean pureAscent()  throws XpthException
    {
        boolean res = false;
        boolean ascendingStep;
        if (size() > 0)
        {
            res = true;
            for (int i = 0; i < size(); i++)
            {
                ascendingStep = false;
                if (step(i).axis.equals("parent")) ascendingStep = true;
                if (step(i).axis.equals("ancestor")) ascendingStep = true;
                if (step(i).axis.equals("ancestor-or-self")) ascendingStep = true;
                if (!ascendingStep) res = false;
            }
        }
        return res;
    }
    /** true if the path is a pure descent with one or more steps */
    public boolean pureDescent()  throws XpthException
    {
        boolean res = false;
        boolean descendingStep;
        if (size() > 0)
        {
            res = true;
            for (int i = 0; i < size(); i++)
            {
                descendingStep = false;
                if (step(i).axis.equals("child")) descendingStep = true;
                if (descendantAxis(step(i).axis)) descendingStep = true;
                if (step(i).axis.equals("attribute")) descendingStep = true;
                if (!descendingStep) res = false;
            }
        }
        return res;
    }
    
    /** a path is definite if it has only 'child', 'parent' or 'attribute' axes in all its steps,
    * <p>
    * and if node names are defined for all steps; or if it is '.'*/
    public boolean definite() throws XpthException, NamespaceException
    {
        boolean defAx,defName;
        String ax,nn;
        boolean res = true;
        for (int i = 0; i < size(); i++)
        {
            ax = axis(i);
            nn = nodeName(i);
            defAx = ((ax.equals("child"))|(ax.equals("parent"))|(ax.equals("attribute")));
            defName = !(nn.equals("node()")); // more indefinite cases to deal with?
            res = res & defAx & defName;
        }
        if (selfPath()) res = true; // special case
        return res;
    }

    /** the number of named 'child' or 'attribute' steps inside the innermost 'descendant-or-child'
    * step, or unnamed node step, if there is one of either;
    * <p>
    * otherwise the total number of steps.*/
    public int innerDefiniteSteps()  throws XpthException, NamespaceException
    {
        boolean stillDef = true;
        int res = 0;
        for (int i = size() -1; i > -1; i--)
        {
            String ax = axis(i);
            String nn = nodeName(i);
            stillDef = (stillDef &&
                        ((ax.equals("child"))|(ax.equals("attribute"))) &&
                        !(nn.equals("node()")));
            if (stillDef) res++;
        }
        return res;
    }
    /** number of occurrences of a tag name (with namespace  prefix) in the path */
    public int occurrences(String tagName)  throws XpthException, NamespaceException
    {
        int occ = 0;
        for (int i = 0; i < size(); i++)
        {
            if (nodeName(i).equals(tagName)) occ++;
        }
        return occ;
    }
//-------------------------------------------------------------------------------------
//                           comparisons with other paths
//-------------------------------------------------------------------------------------
        /** true if this path is exactly equal to a path xp,
        which may have a different set of namespace prefixes. */
        public boolean equalPath(Xpth xp)  
        {
            boolean res, match;
            Xpth xq;
            res = false;
            if (size() == xp.size()) try
            {
                res = true;
                // convert the other path to have this path's prefixes
                try {
                xq = xp.convertPrefixes(NSSet);
                }
                /* If a namespace URI in one path cannot be matched in the other, 
                 * the XPaths are not equal  */
                catch (NamespaceException ex) {return false;}
                for (int i = 0; i < size(); i++) if (res)
                {
                    match = ((nodeName(i).equals(xq.nodeName(i))) &&
                             (axis(i).equals(xq.axis(i))));
                    if (!match) {res = false;}
                }
            }
            /* methods like axis(int i) throw  exceptions when i is out of range - 
             * but that can never happen. */
            catch (XpthException ex) {GenUtil.surprise(ex,"Xpth.equalPath");}
            return res;
        }
        /** true if this pure-descending root path  is compatible with another
        * pure descending root path - i.e if they can both in some cases lead to
        * the same node.
        * <p>
        * This version is a bit lazy; it only checks outwards from the innermost nodes
        * to the first descendant-or-self axis, and then inwards from the outermost node to the first
        * unspecified axis.
        * <p>
        * So it can check paths of form /a/b/c,  //b/c, a//b/c on either side.
        * Paths with more than one '//', such as a//b//c, have not yet been analysed properly. */
        public boolean compatible(Xpth xp)  throws XpthException
        {
            Xpth xq;
            int p1,p2;
            boolean res,finished;
            res = false;
            if (pureDescent() && fromRoot && xp.pureDescent() && xp.fromRoot)
            {
                res = true;
                // convert the other path to have this path's prefixes
                try{
                xq = xp.convertPrefixes(NSSet);
                }
                /* if a namespace uri in one path cannot be matched in the other, 
                 * they are not compatible. */
                catch (NamespaceException ex) {return false;}
               /* (1) work outwards from the two innermost nodes, until you hit a problem (fail)
                or a descendant-or-self axis (succeed)   */
                p1 = size() -1;
                p2 = xq.size() -1;
                // loop until you finish or run out of one or other path
                finished = false;
                while (!finished & (p1 > -1) & (p2 > -1))
                {
                    if (axis(p1).equals("descendant-or-self")) {finished = true;} // succeed
                    else if (xq.axis(p2).equals("descendant-or-self")) {finished = true;}  //succeed
                    else if (!axis(p1).equals(xq.axis(p2))) {finished = true; res = false;} // eg attribute and child axes
                    //   *** should also allow for unspecified 'node()' here
                    else if (!nodeName(p1).equals(xq.nodeName(p2))) {finished = true; res = false;}
                    p1--;
                    p2--;
                }
                /* if the while loop ran out by p1 or p2 going negative, without hitting a '//',
                (which would have set finished = true) then all steps were 'child' axes
                and the paths are only compatible if they have equal length. */
                if (!finished && !(size() == xq.size())) res = false;
               /* (2) work inwards from the two outermost nodes, until you hit a problem (fail)
                or a descendant-or-self axis (succeed)   */
                if (res)
                {
                  p1 = 0;
                  p2 = 0;
                  // loop until you finish or run out of one or other path
                  finished = false;
                  while (!finished & (p1 < size()) & (p2 < xq.size()))
                  {
                    if (axis(p1).equals("descendant-or-self")) {finished = true;} // succeed
                    else if (xq.axis(p2).equals("descendant-or-self")) {finished = true;}  //succeed
                    else if (!axis(p1).equals(xq.axis(p2))) {finished = true; res = false;} // eg attribute and child axes
                    //   *** should also allow for unspecified 'node()' here
                    else if (!nodeName(p1).equals(xq.nodeName(p2))) {finished = true; res = false;}
                    p1++;
                    p2++;
                  }
                }
            }
            else if (emptyPath() && xp.emptyPath()) {res = true;}
            else if (emptyPath()|xp.emptyPath()) {res = false;}
            else
              {throw new XpthException("Failed XPath compatibility check: both paths are not pure descent from the root - '"
                  + stringForm() + "' and '" + xp.stringForm() + "'");}
            return res;
        }

        /** true if this path is one of a vector of paths */
        public boolean oneOf(Vector<Xpth> v) 
        {
            boolean res = false;
            for (int i = 0; i < v.size(); i++)
                if (equalPath(v.elementAt(i))) {res = true;}
            return res;
        }

        /** true if this path is compatible with one of a vector of paths */
        public boolean compatibleOneOf(Vector<Xpth> v)  throws XpthException
        {
            boolean res = false;
            for (int i = 0; i < v.size(); i++)
                if (compatible(v.elementAt(i))) {res = true;}
            return res;
        }

        /** return the subset of a vector of paths which are compatible with this path */
        public Vector<Xpth> compatibleSubset(Vector<Xpth> v)  throws XpthException
        {
            Vector<Xpth> res = new Vector<Xpth>();
            for (int i = 0; i < v.size(); i++)
                if (compatible(v.elementAt(i))) {res.addElement(v.elementAt(i));}
            return res;
        }

        /** for two definite paths from the root, find the longest common sub-path.
         * <P>
         * i.e the set of steps from the root which match.
         * <P>
         * write a warning and return null if either is not a definite path from the root.
         * <P>
         * Assume the paths are in the same namespace. */
        public Xpth commonPath(Xpth other)  throws XpthException
        {
          Xpth common = null;
          if (!(fromRoot())) {throw new XpthException("Path '" + stringForm() + "' is not a path from the root");}
          else if (!(definite())) {throw new XpthException("Path '" + stringForm() + "' is not a definite path");}
          else if (!(other.fromRoot())) {throw new XpthException("Path '" + other.stringForm() + "' is not a path from the root");}
          else if (!(other.definite())) {throw new XpthException("Path '" + other.stringForm() + "' is not a definite path");}
          else
          {
              common = new Xpth(NSSet);
              common.fromRoot = true;
              NamespaceSet thisNSS = NSSet;
              NamespaceSet thatNSS = other.NSSet;
              boolean match = true;
               for (int i = 0; i < size(); i++) if ((match) && (i < other.size()))
               {
                   match = match && ((thisNSS.equalNodeName(nodeName(i),thatNSS,other.nodeName(i))) &&
                            (axis(i).equals(other.axis(i))));
                   if (match) common.addStep(step(i).stringForm());
               }
          }
          return common;
        }

        /** true if this path from the root contains the other path as a subpath */
        public boolean containsPath(Xpth xp) 
        {
            NamespaceSet thisNSS, thatNSS;
            boolean res,match;
            res = false;
            if (size() > xp.size() - 1) try
            {
                res = true;
                thisNSS = NSSet;
                thatNSS = xp.NSSet;
                for (int i = 0; i < xp.size(); i++) if (res)
                {
                    match = ((thisNSS.equalNodeName(nodeName(i),thatNSS,xp.nodeName(i))) &&
                             (axis(i).equals(xp.axis(i))));
                    if (!match) {res = false;}
                }
            }
            // index out of range exceptions do not happen
            catch (XpthException ex) {GenUtil.surprise(ex,"Xpth.containsPath");}
            return res;
        }

        /** true if the other path from the root contains this path as a subpath */
        public boolean containedBy(Xpth xp)
            {return xp.containsPath(this);}

        /** true if this path is contained by any one of a vector of paths */
        public boolean containedByOneOf(Vector<Xpth> v)
        {
            boolean res = false;
            for (int i = 0; i < v.size(); i++)
                if ((v.elementAt(i).containsPath(this))) {res = true;}
            return res;
        }

/** True if this XPath is at least as specific as some other XPath - i.e if it has fewer 'descendant' steps,
         * <p>
* or the same number of descendant steps and no fewer specified node names*/
public boolean asSpecificAs(Xpth xp) throws XpthException
{
    boolean res = true;
    if (xp != null)
    {
        if (descendantSteps() > xp.descendantSteps())
            {res = false;}
        else if ((descendantSteps() == xp.descendantSteps()) &&
              (size() < xp.size()))
          {res = false;}
    }
    return res;
}

/** number of descendant steps in the path */
public int descendantSteps() 
{
    int res = 0;
    for (int i = 0; i < size(); i++) try
    {
        if ((step(i).axis.equals("descendant"))|
            (step(i).axis.equals("descendant-or-self"))) res++;
    }
    // index out of range exceptions do not happen
    catch (XpthException ex) {GenUtil.surprise(ex,"Xpth.descendantSteps");}
    return res;
}

/** for a definite Xpth consisting of a sequence of child steps,
this returns true if any sub-sequence of match steps is repeated. */
public boolean repeatsNodeSequence(int match)  throws XpthException
{
    boolean res = false;
    if (definite() && (match > 0) && (match < size() - 1))
    {
        int maxStart = size() - match;
        for (int i = 0; i < maxStart + 1; i++)
        {
            for (int j = 0; j < maxStart + 1; j++) if ((i > j) && !res)
            {
                boolean thisMatch = true;
                for (int k = 0; k < match; k++)
                {
                    String mi = step(i+k).stringForm();
                    String mj = step(j+k).stringForm();
                    if (!mi.equals(mj)) thisMatch = false;
                }
                if (thisMatch) res = true;
            }
        }
    }
    return res;
}
/** insert a 'descendant-or-self' step so that there are beforeEnd child steps inside it.
 * <p>
* beforeEnd must be at least 1, so there is at least 1 child step after the '//' step. */
public Xpth insertDescendantStep(int beforeEnd)  throws XpthException
{
    Xpth res = new Xpth(NSSet);
    if (!fromRoot())
        {throw new XpthException("insertDescendantStep is intended for use on root paths, not on '" + stringForm() + "'");}
    else if (!definite())
        {throw new XpthException("insertDescendantStep is intended for use on definite paths, not on '" + stringForm() + "'");}
    else if ((beforeEnd < 1)|(beforeEnd > size()))
        {throw new XpthException("beforeEnd = " + beforeEnd + " is out of range for path '" + stringForm() + "'");}
    else for (int i = 0; i < size(); i++)
    {
        if (i == (size() - beforeEnd)) res.addStep(""); // insert descendant-or-child step
        res.addStep(step(i).stringForm());
    }
    res.setFromRoot(true);
    return res;
}
/** If this XPath has a '//' descendant-or-self step, return the part outside that step;
 * <p>
* or if it is definite, return the whole path.*/
public Xpth outerDefinitePart() throws XpthException
{
    Xpth res = new Xpth(NSSet);
    boolean descendantStepFound = false;
    if (!fromRoot())
        {throw new XpthException("outerDefinitePart is intended for use on root paths, not on '" + stringForm() + "'");}
    for (int i = 0; i < size(); i++)
    {
        if (step(i).axis.equals("descendant-or-self")) descendantStepFound = true;
        if (!descendantStepFound) {res.addStep(step(i).stringForm());}
    }
    res.setFromRoot(true);
    return res;
}
/** return a root XPath with outerSteps 'child' steps from the root,
* followed by a 'descendant-or-self' step, followed by the innermost innerSteps 'child'
* steps to the innermost node of this path.*/
public Xpth bridgeEnds(int outerSteps, int innerSteps)  throws XpthException
{
    Xpth res = null;
    if (fromRoot() && definite())
    {
        if (outerSteps + innerSteps > size())
        {
            res = copy();
        }
        else if ((outerSteps > -1) && (innerSteps > 0))
        {
            res = new Xpth(NSSet);
            res.setFromRoot(true);
            for (int i = 0; i < outerSteps; i++)
                {res.addStep(step(i).stringForm());}
            res.addStep("");
            for (int i = 0; i < innerSteps; i++)
                {res.addStep(step(size() - innerSteps + i).stringForm());}
        }
        else {throw new XpthException("Invalid outer & inner for Xpth.bridgeEnds: " + outerSteps + " and "
                + innerSteps + " at path '" + stringForm() + "'");}
    }
    else {throw new XpthException ("bridgEnds is intended for definite root paths, not '" + stringForm() + "'");}
    return res;
}
//-------------------------------------------------------------------------------------
//                            access methods - individual steps
//-------------------------------------------------------------------------------------

/** the name of a node involved in a step */
        public String nodeName(int i)  throws XpthException, NamespaceException
            {return step(i).nodeTest;}
        /** axis involved in a step */
        public String axis(int i)  throws XpthException
            {return step(i).axis;}
        /** the short string form of a step */
        public String stepString(int i)  throws XpthException
            {return step(i).stringForm();}
        /** the string form of the innermost step node name */
        public String innerStepString()  throws XpthException
            {return step(size()-1).stringForm();}
        /** compare two node names, allowing that they may have different prefixes for the same namespace
        */

        public boolean equalForNS(String here, String there, Xpth xp) throws XpthException
        {
            NamespaceSet thisNSS, thatNSS;
            thisNSS = NSSet;
            thatNSS = xp.NSSet;
            return (thisNSS.equalNodeName(here,thatNSS,there));
        }
    /** check the node names for two steps can match, allowing for possible namespace prefix changes. */
    public boolean stepMatch(step s1, Xpth p, step s2) throws XpthException
    {
        boolean res = false;
        res = equalForNS(s1.nodeTest,s2.nodeTest,p);
        if (s1.nodeTest.equals("node()")) res = true;
        if (s2.nodeTest.equals("node()")) res = true;
        return res;
    }
//-------------------------------------------------------------------------------------
//                            cross paths
//-------------------------------------------------------------------------------------
    /** calculate the default (= shortest) cross path between
    * two nodes, whose paths from the root node are this Xpth and end.
    * <p>
    * Either path may have descendant '//' axes within it. */
    public Xpth defaultCrossPath(Xpth end) throws XpthException
    {
        boolean found = false;
        int i,j,match;
        String axis,revAxis;
        Xpth res;
        /* axes you can expect to find above the innermost node of the
        */
        String[] allowedAxes = {"child","descendant","descendant-or-self"}; // note "attribute" excluded
        res = new Xpth(NSSet);
        res.fromRoot = false;
        // both paths must be paths from the root.
        if (!fromRoot)
            {throw new XpthException("Cannot form cross path from  non-root start path '" + stringForm() + "',");}
        else if (!end.fromRoot)
            {throw new XpthException("Cannot form cross path from non-root end path '" + end.stringForm() + "',");}
        // if the paths could arrive at the same node, use the 'stay here' cross path
        else if (compatible(end))
            {res.addStep(".");}
        else if ((end.definite()) && (definite()))
            {res = betweenDefinitePaths(end);}
        else
        {
            match = 0;
            /* work up from the innermost node of this path, constructing
            the reverse path, and looking for a nodeName matching some node in
            the other path. */
            for (i = 0; i < size(); i++) if (!found)
            {
                j = size() - i - 1; // j goes from size()-1 down to zero
                axis = step(j).axis;
                /* for all but the innermost node, check axis and
                add ascending parent or ancestor steps to the result. */
                if ((i > 0) && (GenUtil.inArray(axis,allowedAxes)))
                {
                    revAxis = "wrong";
                    if (axis.equals("child")) {revAxis = "parent";}
                    if (axis.equals("descendant")) {revAxis = "ancestor";}
                    if (axis.equals("descendant-or-self")) {revAxis = "ancestor-or-self";}
                    res.addStep(revAxis + "::" + nodeName(j));
                }
                else if ((i > 0) &&!(GenUtil.inArray(axis,allowedAxes)))
                {
                    throw new XpthException("Unexpected axis: " + axis + " when calculating cross path from an absolute path." 
                    		+ "   Start path: '" + stringForm() + "' ; End path: '" + end.stringForm() + "'");
                }
                // stop when you find a matching node name (possibly when i = 0, but not for attributes)
                if ((step(j).isNamedNode) && (GenUtil.inArray(axis,allowedAxes)))
                {
                        match = end.innerMostMatchingStep(nodeName(j));
                        found = (match > -1);
                }
            } // end of loop over i
            // add descending steps from the other path
            if (found) for (i = match + 1; i < end.size(); i++)
                {res.addStep(end.stepString(i));}
            // no matching node names found
            else if (!found)
            {
                /* if both top nodes have defined names,
                we know they do not match, which is an unexpected failure */
                if (step(0).isNamedNode && end.step(0).isNamedNode)
                {
                    throw new XpthException("Failed to find cross path between two paths whose top steps are:"
                    		+ stepString(0) +  "; "  + end.stepString(0));
                }
                else res = end;
            } // end of !found case
        } // end of 'else' complicated case
        return res;
    }
    /* This Xpth and another Xpth 'end' are both definite paths
    (all steps are 'child' or 'attribute').
    Find the shortest cross path between them, by descent from the root.
    */
    private Xpth betweenDefinitePaths(Xpth end)  throws XpthException
    {
        Xpth res = new Xpth(NSSet);
        res.fromRoot = false;
        // find the minimum length of the two paths
        int smaller = size();
        if (end.size() < smaller)  smaller = end.size();
        /* find the lowest node between the two paths with node name matches all
        the way down to that node. */
        int lowestMatch = -1;
        boolean matched = true;
        for (int i = 0; i < smaller; i++) if (matched)
        {
            matched = ((end.axis(i).equals(axis(i))) && (end.nodeName(i).equals(nodeName(i))));
            if (matched) lowestMatch = i;
        }
        // no match even at the top node - error
        if (lowestMatch == -1)
        {
            throw new XpthException("Failed to find cross path between two definite paths whose top steps are:" 
            		+ stepString(0) + " ; " + end.stepString(0));
        }
        else
        {
            String apexName = nodeName(lowestMatch);
            // if there is any ascent needed to the apex...
            if (lowestMatch < size() - 1)
            {
                res.addStep("ancestor::" + apexName);
            }
            // if there is any descent needed from the apex
            if (lowestMatch < end.size() -1)
            {
                for (int i = lowestMatch + 1; i < end.size(); i++)
                {
                    res.addStep(end.step(i).stringForm());
                }
            }
        }
        return res;
    }
    /* return the index of the innermost step in this path
    whose nodeName matches the given nodeName; or -1 if no step matches.*/
    private int innerMostMatchingStep(String nodeName)  throws XpthException
    {
        int i,j,res;
        res = -1;
        boolean found = false;
        for (i = 0; i < size(); i++) if (!found)
        {
            j = size() - i - 1;
            if (nodeName(j).equals(nodeName))
                {found = true; res = j;}
        }
        return res;
    }
    /* Check that this path is a valid crosspath from path1 to path2.
    * They may all have different namespace prefixes.
    * <p>
    * The check has 3 parts:
    * <p>
    * (1) this has the right form for a crosspath - having a pure ascending part first, an apex step and a pure descending part last.
    * <p>
    * (2) the pure ascending part can match path1.
    * <p>
    * (3) the pure descending part can match path2. */
    public boolean checkCrossPath(Xpth path1, Xpth path2)  throws XpthException
    {
        boolean passed = false;
        int apex = apexIndex();
        // error cases
        if (apex == -2)
            {throw new XpthException("Path '" + stringForm() + "' is not in the correct form for a cross path.");}
        else if ((!path1.pureDescent())|(!path1.fromRoot))
            {throw new XpthException("Path '" + path1.stringForm() + "' is not in the correct form for a path from the root.");}
        else if ((!path2.pureDescent())|(!path1.fromRoot))
            {throw new XpthException("Path '" + path2.stringForm() + "' is not in the correct form for a path from the root.");}
        // genuine cases...
        else if (selfPath()) // cross path is '.'; two root paths must be compatible
            {passed = path1.compatible(path2);}
        else if (fromRoot) // cross path is a root path; it must be compatible with target path.
            {passed = this.compatible(path2);}
        else
        {
            passed = true;
            // need not check ascending part for a pure descending path
            if (apex > -1)
                {passed = checkAscendingPart(apex,path1);}
            // always check the apex, even for a pure ascending path
            passed = passed && checkDescendingPart(apex,path2);
        }
        if (!passed) crossError(path1,path2);
        return passed;
    }
    void crossError(Xpth path1, Xpth path2)  throws XpthException
    {
        throw new XpthException("Path '" + stringForm() + "' is not a valid cross path from '" 
        		+ path1.stringForm() + "' to '" + path2.stringForm() + "'.");
    }
    /** if this is in the form of a cross path, it has first a pure ascending part
    * (with only axes 'parent' or 'ancestor') terminated by an apex step
    * (which is the last ascending step)
    * <p>
    * then a pure descending part (with only axes 'child' 'descendant' or 'attribute').
    * <p>
    * If it has this form, return the index 0...size()-1 of the apex - the step which 
    * ends at the node nearest the root of the document.
    * <p>
    * Returns -1 for a pure descending path, size()-1 for a pure ascending path.
    * <p>
    * Return -2 if this does not have the 'ascending/descending' form of a cross path.
    */
    public int apexIndex()  throws XpthException
    {
        int i,res;
        boolean crossForm = true;
        boolean descentStarted = false;
        res = -1;
        for (i = 0; i < size(); i++)
        {
            if ((axis(i).equals("child"))|
                (descendantAxis(axis(i)))|
                (axis(i).equals("attribute")))
                {descentStarted = true;}
            if (!descentStarted) {res = i;}
            if ((descentStarted) &&
                ((axis(i).equals("parent"))|(ancestorAxis(axis(i)))))
                {crossForm =false;}
        }
        if (!crossForm) {res = -2;}
        return res;
    }
    /* check that the descending steps apex..size()-1 of this path can match
    some descent of path 'path'.
    Start at the innermost nodes and check upwards.
    As long as both paths have only 'child' steps, node names must match
    one-to-one.
    As soon as path has any 'descendant' steps the test passes
    - as the rest of this path up to apex can fit in the
    unspecified nodes in the descendant gap. ('getout' below)
    If this path has descendant steps, they may skip steps in path but still
    all node names must match. */
    private boolean checkAscendingPart(int apex,Xpth path)  throws XpthException
    {
        return (ascendingMatchIndex(apex,path) > -1);
    }

    /** the index of the step in path to which this cross path matches */
    public int matchIndex(Xpth path)  throws XpthException
        {return ascendingMatchIndex(apexIndex(),path);}
    /* return the index of the step in path which matches the apex node (index apex) of
    this cross path, or the index of the descendant step of path which the apex
    matches beneath; or -1 if there is no possible match.
    In cases where the match is a 'getout' of the apex fitting in to
    a 'descendant' step of path, this method does not check
    for real matches higher up in path. */

    private int ascendingMatchIndex(int apex,Xpth path)  throws XpthException
    {
        int pos,pos2;
        boolean undecided, fixed2, slideMatch, passed,getout;
        undecided = true; // no failure has yet been detected
        passed = false; // not yet passed
        getout = false; // the match was not achieved by fitting into a 'descendant' step
        pos2 = path.size() - 2 ; /* the step in  the root path we are trying to match;
        (the first node of the cross path is a parent of a start node,
        which is the last step of the root path) */
        pos = 0; // the step in this path we are trying to match
        // (first node of cross path is a parent of the start node)
        fixed2 = axis(0).equals("parent"); // or it might be "ancestor"
        while (undecided && (pos < apex + 1) && (pos2 > -1))
        {
             // match against one fixed step of path
             if (fixed2)
             {
                 if (!stepMatch(step(pos),path,path.step(pos2))) // failed
                 {
                        undecided = false;
                        passed = false;
                        writeStepMisMatch(path,pos,pos2);
                 }
                 else // passed; move a step up both paths
                 {
                    pos++;
                    pos2--;
                    if ((pos2 > -1) && (descendantAxis(path.axis(pos2)))) // getout
                        {passed = true; undecided = false; getout = true;}
                 }
             }
             /* this path 'descendant' step;
             keep looking for matches, decrementing pos2 */
             else if (!fixed2)
             {
                slideMatch = false;// no match found yet
                while (!slideMatch && (pos2 < path.size()) && (pos2 > -1)) //pos2 decrement loop
                {
                    slideMatch = stepMatch(step(pos),path,path.step(pos2));
                    pos2--;
                    if (!slideMatch && (pos2 > -1) && (descendantAxis(path.axis(pos2)))) // getout
                        {passed = true; undecided = false; slideMatch = true; getout = true;}
                }
                if (slideMatch) {pos++;}
                else // failed
                {
                    undecided = false;
                    passed = false;
                }
            }
            /* if the current step in this path is a 'descendant',
            can slide match path to match the next step. */
            if (pos < size()) fixed2 = step(pos).axis.equals("child");
        }
        if (pos == apex + 1) {passed = true;} // all nodes have been matched
        if (passed)
        {
            if (!getout) pos2++; //to compensate for last decrement of pos2 after a real match
        }
        else //failed
        {
            pos2 = -1;
            throw new XpthException("Failed to match ascending part of cross path '" + stringForm()
            		+ "' with root path '" + path.stringForm() + "'");
        }
        return pos2;
    }
    void  writeStepMisMatch(Xpth path,int pos,int pos2)  throws XpthException
    {
        throw new XpthException ("Failed to match step " + pos
            + " '" + step(pos).stringForm() + "' of cross path '" + stepString(pos) + "'"
            + "with step " + pos2
            + " '" + path.stepString(pos2) + "' of root path '" + path.stringForm() + "'");
    }
    /* check that the descending steps apex..size()-1 of this path can match
    some descent of path 'path'.
    Start at the innermost nodes and check upwards.
    As long as both paths have only 'child' steps, node names must match
    one-to-one.
    As soon as path1 has any 'descendant' steps the test passes
    - as the rest of this path up to apex can fit in the
    unspecified nodes in the descendant gap. ('getout' below)
    If this path has descendant steps, they may skip steps in path2 but still
    all node names must match. */
    private boolean checkDescendingPart(int apex, Xpth path) throws XpthException
    {
        int pos,pos2,minStep;
        boolean undecided, fixed2, slideMatch, passed;
        undecided = true; // no failure has yet been detected
        passed = false; // not yet passed
        fixed2 = true; // you can compare against only one node in path
        pos2 = path.size() - 1 ; // the step in path we are trying to match
        pos = size() - 1; // the step in this path we are trying to match
        minStep = apex;
        if (minStep < 0) minStep = 0;
        while (undecided && (pos > minStep-1) && (pos2 > -1))
        {
             // match against one fixed step of path
             if (fixed2)
             {
                 if (!stepMatch(step(pos),path,path.step(pos2))) // failed
                 {
                        undecided = false;
                        passed = false;
                        writeStepMisMatch(path,pos,pos2);
                 }
                 else // passed; move a step up both paths
                 {
                    pos--;
                    pos2--;
                    if ((pos2 > -1) && (descendantAxis(path.axis(pos2)))) // getout
                        {passed = true; undecided = false;}
                 }
             }
             /* this path 'descendant' step;
             keep looking for matches, decrementing pos2 */
             else if (!fixed2)
             {
                slideMatch = false;// no match found yet
                while (!slideMatch && (pos2 < path.size())) //pos2 decrement loop
                {
                    slideMatch = stepMatch(step(pos),path,path.step(pos2));
                    pos2--;
                    if ((pos2 > -1) && (descendantAxis(path.axis(pos2)))) //getout
                        {passed = true; undecided = false; slideMatch = true;}
                }
                if (slideMatch) {pos--;}
                else // failed
                {
                    undecided = false;
                    passed = false;
                }
                }
                /* if the current step in this path is a 'descendant',
                can slide match path to match the next step. */
                if (pos > -1) fixed2 = step(pos).axis.equals("child");
            }
        if (pos == minStep - 1) {passed = true;} // all nodes have been matched
        if (!passed)
        {
            throw new XpthException("Failed to match descending part of cross path '" + stringForm() 
            		+ "' with root path '" + path.stringForm() + "'");
        }
        return passed;
    }

    /** path1 is a path from the root to a node.
     * <p>
    * this path is a relative path from that node to a second node.
    * <p>
    * return the path from the root to the second node.
     */
    public Xpth crossToRootPath(Xpth path1) throws XpthException
    {
        Xpth rootPath;
        int apex,down,i;
        rootPath = null;
        apex = apexIndex();
        if (path1 == null)
            {throw new XpthException("Null starting root path when finding root path to the end of cross path '"
                + stringForm());}
        else if (apex == -2)
            {throw new XpthException("Path '" + stringForm()
                + "' is not of the correct form for a cross path.");}
        else if ((!path1.pureDescent())|(!path1.fromRoot))
            {throw new XpthException("Path '" + path1.stringForm()
                + "' is not of the correct form for a path from the root.");}
        else if (selfPath())
            {rootPath = path1;}
        // special case of one or more parent steps followed by descent steps 
        else if (step(0).axis.equals("parent"))
        {
        	rootPath = path1;
        	int stepNo = 0;
        	while (step(stepNo).axis.equals("parent"))
        	{
        		// remove one inner step from the root path for each parent step of the cross path
        		rootPath = rootPath.removeInnerStep();
        		stepNo++;
        	}
        	if (step(stepNo).axis.equals("ancestor"))
        		throw new XpthException("Cannot yet support cross XPaths with parent steps followed by an ancestor step");
        	// get the new inner step of the root path
        	step innerStep = rootPath.step(rootPath.size() -1);
        	//check it for compatibility with the apex node of the cross path
        	if (compatibleNodeTest(step(apex).nodeTest,innerStep.nodeTest))
        	{
        		// add the descending steps of the cross path
                for (i = apex+1; i < size(); i++)
                	{rootPath.addStep(stepString(i));}        		
        	}
        	else throw new XpthException("Cross path mismatch; step '" + step(0).stringForm()
                    + "' does not match step '" + innerStep.stringForm() 
                    + "' in cross path " + stringForm() + " from " + path1.stringForm());
        }
        else
        {
            //find index 'down' of last node in path1 to be copied to result
            if (apex > -1) // crosspath has some ascent part
                {down = ascendingMatchIndex(apex,path1);}
            else // cross path is pure descending
                {down = path1.size()-1;}
            rootPath = new Xpth(NSSet);
            rootPath.fromRoot = true;
            // add steps from path1 above the matching node
            for (i = 0; i < down + 1; i++)
                {rootPath.addStep(path1.stepString(i));}
            // add descending steps from this cross path.
            if (down > -1)
            {
                // pure descending cross path
                if (apex == -1)
                {
                    for (i = 0; i < size(); i++)
                        {rootPath.addStep(stepString(i));}
                }
                // exact match of apex node name; do not repeat apex step
                else if (nodeName(apex).equals(path1.nodeName(down)))
                {
                    for (i = apex+1; i < size(); i++)
                        {rootPath.addStep(stepString(i));}
                }
                // 'getout' - apex nodename fits below a descendant node of path1; repeat apex name
                else if (descendantAxis(path1.axis(down)))
                {
                     for (i = apex; i < size(); i++)
                        {rootPath.addStep(stepString(i));}
               }
                else
               {
                    rootPath = null;
                    throw new XpthException("cross path matching anomaly; step '" + stepString(apex)
                            + "' does not match step '" + path1.stepString(down) 
                            + "' in cross path " + stringForm() + " from " + path1.stringForm());
               }
            }
        }
        return rootPath;
    }
    
    /**
     * check if the node tests of two steps are compatible
     * @param test1 node test of one step
     * @param test2 node test of abother step
     * @return true if they are compatible
     */
    private boolean compatibleNodeTest(String test1,String test2)
    {
    	if (test1.equals("node()")) return true; // 'node()' will match anything
    	if (test2.equals("node()")) return true;
    	if (test1.equals(test2)) return true;
    	return false;
    }
//-------------------------------------------------------------------------------------
//                            junk
//-------------------------------------------------------------------------------------

    private boolean descendantAxis(String axis)
	{
	    return ((axis.equals("descendant")|(axis.equals("descendant-or-self"))));
	}

    private boolean ancestorAxis(String axis)
	{
	    return ((axis.equals("ancestor")|(axis.equals("ancestor-or-self"))));
	}


    }
