package com.openMap1.mapper.mapping;

import javax.xml.xpath.XPathExpressionException;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XPathAPI;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.ValueCondition;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

//--------------------------------------------------------------------------------------
//                     Inner class for conditions involved in mappings
//--------------------------------------------------------------------------------------
/* There are five types of condition:
'when' conditions
(a) if a node represents objects of class conditionally, this is defined by e.g.:
        <me:when objectToLeftValue = '@age' test = 'gt' rightValue = '30'/>
(b) if a node represents a property conditionally, this is defined by e.g.:
        <me:when propertyToLeftValue = '../@pName' rightValue = 'surname' >
(c) if a node represents an association conditionally, this is defined by e.g.:
        <me:when associationToLeftValue = '../@aName' rightValue = 'godFather' >
link conditions
(d) when the representation of a property has value-sharing link conditions, e.g:
        <me:link propertyToLeftValue = "." objectToRightValue ="@name"/>
(e) when the representation of an association has value-sharing link conditions, e.g:
        <me:link associationToLeftValue = "." objectToRightValue ="@name"/>
These all represent a test in which a left-hand side (LHS) value  is got from a node
and compared with a right-hand side (RHS)value, which is either a constant or got from a node.
{object/property/association}ToLeftValue is the path from the current node to the node containing the left value
('current node' = node representing the object, property or association)
test is the test to apply. It can be '=', 'contains', 'contained', 'gt', 'le', etc.
The default for test is '='.
rightValue is the constant value to compare leftValue with,
or right Path is the path from some other node (i.e. for associations, it is one of the two
object-representing nodes) to get the right-hand value.
*/

/**
 * superclass of the two types of condition on a mapping - 
 * value conditions and cross conditions.
 * 
 * This is a wrapper class for the Mapping Model class MappingCondition
 */

public abstract class Condition
{
    /** the Mapper model object which this is a wrapper for  */
    public MappingCondition mappingCondition() {return mappingCondition;}
    protected MappingCondition mappingCondition;

    /** @return absolute path to the node at the start of a relative path; the 
     * value of the node at the end of that relative path is tested.
     * Every condition filters a set of nodes. For all when conditions,
     * and most uses of link conditions, his is the path from the root
     * to that set of nodes. */
    public Xpth rootToLHSNode() {return rootToLHSNode;}
    protected Xpth rootToLHSNode;
    
    /** relative path from the node of the mapping to the node holding the LHS value */
    public Xpth lhsEndToLeftValue() {return lhsEndToLeftValue;}
    protected Xpth lhsEndToLeftValue; /* relative path from node representing
    
    /** absolute path from the root to the node holding the LHS value */
    public Xpth rootToLeftValue() {return rootToLeftValue;}
    protected Xpth rootToLeftValue; // path from root to node carrying LHS value
    
    /** the namespace set of the set of mappings */
    public NamespaceSet NSSet() {return NSSet;}
    protected NamespaceSet NSSet;

    /** the test to be applied between LHS and RHS values, eg LHS contains RHS */
    public String test() {return test;}
    protected String test;
    
    protected Mapping map;
    protected int type; // 1,2,3 for object, property, association conditions
    
    /** further when-conditions on the node reached at the end of the path (LHS) */
    protected Vector<whenCondition> leftPathWhenConditions = new Vector<whenCondition>();

    /** further link-conditions between the node reached at the end of the LHS path
     * (LHS of nested condition) and the node at the start of the LHS path
     * (RHS of nested condition) */
    protected Vector<linkCondition> leftPathLinkConditions = new Vector<linkCondition>();
    
    public String getLeftFunction() {return mappingCondition.getLeftFunction();}
    
    //------------------------------------------------------------------------------------------
    //                 	                      Constructors
    //------------------------------------------------------------------------------------------

    /**
     * constructor from mapping model objects, for a Condition nested immediately
     * inside a mapping
     * @param mc the mapping condition
     */
    public Condition(MappingCondition mc) 
    throws MapperException
    {
    	this.mappingCondition = mc;
    	// this constructor is for conditions nested inside mappings 
    	EObject cont = mc.eContainer();
    	if (!(cont instanceof Mapping)) 
    		throw new MapperException("Container of a condition is a " + cont.getClass().getName());
    	
    	map = (Mapping)cont;
    	if (map instanceof ObjMapping) type = 1;
    	else if (map instanceof PropMapping) type = 2;
    	else if (map instanceof AssocMapping) type = 3;
    	else if (map instanceof AssocEndMapping) type = 3;
    	
    	NSSet = ModelUtil.getGlobalNamespaceSet(map);
    	// this condition filters the nodes picked out by the root path of the mapping
    	rootToLHSNode = map.getRootXPath();
    	// the relative path from them to some value-providing node is provided from the mapper editor
    	lhsEndToLeftValue = new Xpth(NSSet,mc.getLeftPath());
    	// The absolute path to the value-providing node is computed from these two
        rootToLeftValue = lhsEndToLeftValue.crossToRootPath(rootToLHSNode);    		
    	
    	test = mc.getTest().getLiteral();
    	
    	makeLeftPathConditions();
    }
    
    
    /**
     * constructor for a Condition nested inside a Condition
     * @param mc the mapper model condition that this is a wrapper for
     * @param parentCondition: this condition is a filter on the nodes used to provide values when 
     * testing the parent condition
     * @param rootToFilteredNodes the XPath from the root to the set of nodes which this 
     * condition filters
     */
    public Condition(MappingCondition mc, Condition parentCondition, Xpth rootToFilteredNodes) 
    throws MapperException
    {
    	this.mappingCondition = mc;
    	// this constructor is for conditions nested inside mappings 
    	EObject cont = mc.eContainer();
    	if (!(cont instanceof MappingCondition)) 
    		throw new MapperException("Container of a nested condition is a " + cont.getClass().getName());
    	
    	type = parentCondition.type; 
    	NSSet = parentCondition.NSSet;
    	rootToLHSNode = rootToFilteredNodes;
    	lhsEndToLeftValue = new Xpth(NSSet,mc.getLeftPath());
        rootToLeftValue = lhsEndToLeftValue.crossToRootPath(rootToLHSNode);    		
    	
    	test = mc.getTest().getLiteral();

    	makeLeftPathConditions();
    }
    
    /**
     * Make the nested whenConditions and linkConditions on the LHS path.
     * These conditions are used to filter the LHS value-providing nodes.
     */
    private void makeLeftPathConditions() throws MapperException
    {
    	for (Iterator<MappingCondition> it = mappingCondition.getLeftPathConditions().iterator();it.hasNext();)
    	{
    		MappingCondition mapCon = it.next();
    		/* a child condition filters the nodes that provide the LHS values of this condition,
    		 * by taking some relative path from them and comparing the value to a constant */
    		if (mapCon instanceof ValueCondition)
    			leftPathWhenConditions.add(new whenCondition((ValueCondition)mapCon,this,rootToLeftValue));
    		/* a child condition filters the nodes that provide the LHS values of this condition,
    		 * by taking some relative path from them and comparing the value to a value
    		 * got by taking some other relative path from the filtered nodes of this condition */
        	if (mapCon instanceof CrossCondition)
        		leftPathLinkConditions.add(new linkCondition((CrossCondition)mapCon,this,rootToLeftValue,rootToLHSNode));
    	}   	    	
    }

    //-------------------------------------------------------------------------------------------
    //                      Following paths to get values to compare
    //-------------------------------------------------------------------------------------------

    /**
     * Follow an XPath from a node to get a string value to compare 
     * with a constant (value condition) or with the value from some other node (cross condition).
     * Apply any conditions on the end node of the path to filter the set of end nodes
     * 
     * The following rules apply:
     * If no node is found, the value returned is ""
     * If more than one node is found, and one of those is the start node, then the start 
     * node is removed from the set 
     * (the case of two nodes including the start node arises in XMI)
     * If the remaining set has more than one node, the first is chosen
     * 
     * @param start the start node
     * @param path the XPath to follow
     * @param context the namespace context for the XPath to use
     * @param pathConditions conditions to apply to the end nodes to reduce the 
     * set of end nodes (hopefully to one)
     * @return the string value for comparison
     */
    protected String getStringValueAtEndOfPath(Node start, Xpth path, 
    		List<whenCondition> pathWhenConditions, 
    		List<linkCondition> pathLinkConditions, 
    		NamespaceSet context)
    throws MapperException,XPathExpressionException
    {
    	// get the initial list of nodes by following the XPath
    	Vector<Node> nl = XPathAPI.selectNodeVector(start, path.stringForm(), context);
    	
    	// filter the list by applying path conditions
    	Vector<Node> nodes = new Vector<Node>();
    	for (int i = 0; i < nl.size(); i++)
    	{
    		Node node = nl.get(i);
    		boolean nodeOK = true;

    		for (Iterator<whenCondition> iw = pathWhenConditions.iterator();iw.hasNext();)
    			nodeOK = nodeOK && (iw.next().eval1(node, context));

    		for (Iterator<linkCondition> il = pathLinkConditions.iterator();il.hasNext();)
    			nodeOK = nodeOK && (il.next().eval2(start,node,context));

    		if (nodeOK) nodes.add(node);
    	}
    	
    	// apply rules above to the filtered list
    	if (nodes.size() == 0) return "";
    	if (nodes.size() == 1) return XMLUtil.textValue(nodes.get(0));
    	if (nodes.size() > 1) for (int i = 0; i < nodes.size(); i++)
    	{
    		Node n = nodes.get(i);
    		if (!(start.equals(n)))return XMLUtil.textValue(n);    		
    	}
    	return "";
    }
    
    
    //---------------------------------------------------------------------------------------
    //               testing conditions, with possible function evaluation
    //---------------------------------------------------------------------------------------

    /**
     * Test a general condition, which may or may not involve a function on either node,
     * as well as the value of that node.
     * For a ValueCondition, rightNode is null, rightFunction is ""
     * and rightValue is some constant
     * @param leftFunction the function expression on the left node
     * @param leftValue the String value of the left node
     * @param leftNode the left node
     * @param test the test used to compare the two sides
     * @param rightFunction the function expression on the right node
     * @param rightValue the String value of the right node
     * @param rightNode the right node
     * @return the result of the test; false if any test cannot be done
     */
    boolean testCondition(String leftFunction, String leftValue, Node leftNode,
    		String test, 
    		String rightFunction, String rightValue, Node rightNode)
    {
    	boolean passes = false; // default if anything goes wrong

    	// if the test can only be done as an integer test, do it as one
    	if ((isOnlyIntegerTest(test)) &&
    			(isIntegerSide(leftFunction)) && 
    			(isIntegerSide(rightFunction)))
    	{
    		int iLeft = evalAsInteger(leftFunction, leftValue, leftNode);
    		int iRight = evalAsInteger(rightFunction, rightValue, rightNode);
    		passes = tryIntegerValues(iLeft,test,iRight);
    	}
    	
    	/* if the test is equality and there are no functions, do it as a string test 
    	 * (that will work for node values which are integers as well) */
    	else if ((test.equals("=")) && (noFunction(leftFunction)) && (noFunction(rightFunction)))
    	{
    		passes = tryStringValues(leftValue,test,rightValue);
    	}
    	
    	/* if the test is equality and there are some functions, 
    	 * do it as an integer test if possible; otherwise do it as a string test */
    	else if (test.equals("="))
    	{
    		if ((isIntegerSide(leftFunction)) && (isIntegerSide(rightFunction)))
        	{
        		int iLeft = evalAsInteger(leftFunction, leftValue, leftNode);
        		int iRight = evalAsInteger(rightFunction, rightValue, rightNode);
        		passes = tryIntegerValues(iLeft,"=",iRight);
        	}
    		else
    		{
    			String vLeft = evalAsString(leftFunction, leftValue, leftNode);
    			String vRight = evalAsString(rightFunction, rightValue, rightNode);
    			passes = tryStringValues(vLeft,"=", vRight);
    		}
    	}
    	
    	// other tests can only be done as a string test
		else if ((isStringSide(leftFunction)) && (isStringSide(rightFunction)))
		{
			String vLeft = evalAsString(leftFunction, leftValue, leftNode);
			String vRight = evalAsString(rightFunction, rightValue, rightNode);
			passes = tryStringValues(vLeft,test, vRight);
		}

    	return passes;
    }
    
    /**
     * compare two integer values
     * @param iLeft the LHS of the comparison
     * @param test the test to apply
     * @param iRight the RHS of the comparison
     * @return the result of the test
     */
    boolean tryIntegerValues(int iLeft, String test, int iRight)
    {
    	boolean res = false;
    	if (test.equals("="))  res = (iLeft == iRight);
    	if (test.equals(">"))  res = (iLeft > iRight);
    	if (test.equals("<"))  res = (iLeft < iRight);
    	return res;
    }

    boolean tryStringValues(String vLeft, String test, String vRight)
    {
        boolean res = false;
        if ((vLeft != null) && (vRight != null))
        {
            if (test.equals("="))
                {res = (vLeft.equals(vRight));}
            else if (test.equals("!="))
            	{res = !(vLeft.equals(vRight));}
            else if (test.equals("contains"))
                {res = GenUtil.contains(vLeft, vRight);}
            else if (test.equals("contained"))
                {res = GenUtil.contains(vRight, vLeft);}
            else if (test.equals("containsAsWord"))
                {res = GenUtil.containsAsWord(vLeft, vRight);}
            else if (test.equals("containedAsWord"))
                {res = GenUtil.containsAsWord(vRight, vLeft);}
            else if (test.equals("ignoreHash"))
                {res = ignoreInitial(vRight, vLeft,'#');}
            else GenUtil.message("Test '" + test + "' is not supported yet.");
        }
        return res;
    }
    
    /* Test two strings for equality, ignoring a specified initial character
     in either of them. */
    boolean ignoreInitial(String v1, String v2, char c)
    {
        boolean res = false;
        String w1,w2;
        if ((v1 != null) && (v2 !=null))
        {
            w1 = "x"; w2 = "y"; // keep the compiler happy
            if (v1.charAt(0) == c) {w1 = v1.substring(1);} else {w1 = v1;}
            if (v2.charAt(0) == c) {w2 = v2.substring(1);} else {w2 = v2;}
            res = (w1.equals(w2));
        }
        return res;
    }
    
    //--------------------------------------------------------------------------------------
    //                        Functions in conditions
    //--------------------------------------------------------------------------------------
    
    private String[] integerTest = {">","<"};
    protected boolean isOnlyIntegerTest(String test) {return GenUtil.inArray(test, integerTest);}
    
    private String[] stringTest = {"=","contains","containedBy"};
    protected boolean isStringTest(String test) {return GenUtil.inArray(test, stringTest);}
    
    /**
     * @param expression the expression in the LHS or RHS function of a condition
     * @return true if the expression can be taken as one side of an integer comparison
     */
    protected boolean isIntegerSide(String expression)
    {
    	boolean isInteger = true;
    	// if there is no function expression,assume the node values might be cast to integers
    	if (expression == null) return true;
    	if (expression.equals("")) return true;
    	
    	StringTokenizer st = new StringTokenizer(expression," +-");
    	while (st.hasMoreTokens())
    	{
    		String token = st.nextToken();
    		if (!isValidToken(token))  isInteger = false;
    		if (isPropertyValueToken(token)) isInteger = false;
    		if (isNodeValueToken(token)) isInteger = false;
    	}
    	return isInteger;
    }
    
    /**
     * @param expression
     * @return true if the expression implies no function - just using the node value
     */
    boolean noFunction(String expression)
    {
    	if (expression == null) return true;
    	if (expression.equals("")) return true;
    	return false;
    }
    
    
    /**
     * @param expression the expression given in a 'function' slot of a value or cross-condition;
     * it has been checked that this can be evaluated an an integer expression, i.e. as a set of terms 
     * separated by '+' and '-'
     * @param value the value on the node, which may or may not be used in the expression
     * @param node the node on which the expression is being evaluated
     * @return the integer value of the expression
     */
    protected int evalAsInteger(String expression, String value, Node node)
    {
    	int val = 0;
    	// no problem if the node value is not an integer - it may not be used
    	try {val = new Integer(value).intValue();} catch (Exception ex){};
    	if (expression == null) return val;
    	if (expression.equals("")) return val;
    	if (!isIntegerSide(expression)) return val;
   
    	// start evaluating a non-trivial expression
    	int exVal = 0;
    	int sign = 1; // sign of first term is positive, if '+' and '-' have not been encountered
    	StringTokenizer st = new StringTokenizer(expression," +-",true);
    	while (st.hasMoreTokens())
    	{
    		String token = st.nextToken();
    		if (!token.equals(" ")) // ignore all spaces
    		{
    			// set the sign multiplier for the next token after this one
        		if (token.equals("-")) sign = -1;
        		else if (token.equals("+")) sign = 1;
        		
        		// tokens that need to be added in with the correct sign
        		else if (isNodeValueToken(token)) exVal = exVal + sign*val;
        		else if (isIntegerToken(token)) exVal = exVal + sign*(new Integer(token).intValue());
        		else if (isPositionToken(token)) exVal = exVal + sign*findPosition(token,node);
        		else if (isLengthToken(token)) 
        			{if (value != null) exVal = exVal + sign*(value.length());}
   
        		else {System.out.println("Invalid token for integer expression: '" 
        				+ token + "' at node '" + node.getNodeName() + "'");}
    		}
    	}
    	return exVal;
    }
    
    /**
     * @param expression the expression given in a 'function' slot of a value or cross-condition;
     * it has been checked that this can be evaluated an a String expression
     * @param value the value on the node, which may or may not be used in the expression
     * @param node the node on which the expression is being evaluated
     * @return the String value of the expression
     */
    protected String evalAsString(String expression, String value, Node node)
    {
    	if (expression == null) return value;
    	if (expression.equals("")) return value;
    	if (!isStringSide(expression)) return value;
   
    	// start evaluating a non-trivial expression
    	String exVal = "";
    	StringTokenizer st = new StringTokenizer(expression," +"); // ignore space and +
    	while (st.hasMoreTokens())
    	{
    		String token = st.nextToken();
        	// concatenate the text value of the node on the String
        	if (isNodeValueToken(token)) exVal = exVal + value;
        	
        	// value of a property or pseudo-property, starting at the node
        	else if (isPropertyValueToken(token)) exVal = exVal + getPropertyValue(token, node);
        	
        	// treat the integer length of a String as a String
        	else if (isLengthToken(token))
        		{if (value != null) exVal = exVal + value.length();}

        	// concatenate a String constant on the String   
        	else exVal = exVal + token;
    	}
    	return exVal;
    }
    
    /**
     * @param expression the function expression on one side of a condition
     * @return true if it can be used n s String comparison, i.e
     * - not if it contains any invalid tokens
     * - not if it contains '-' as a separator
     */
    protected boolean isStringSide(String expression)
    {
    	boolean isStringSide = true;
    	if (noFunction(expression)) return true;
    	StringTokenizer st = new StringTokenizer(expression," +-",true);
    	while (st.hasMoreTokens())
    	{
    		String token = st.nextToken();
    		if (!isValidToken(token))  isStringSide = false;
    		if (token.equals("-"))  isStringSide = false;
    	}
    	return isStringSide;    	
    }
    
    /**
     * Most strings are valid tokens, but:
     * - if the string starts with '$', it must be $class.property
     * - if the string starts with 'position' it must be position(nodeName).
     * Otherwise anything goes
     * @param token
     * @return
     */
    private boolean isValidToken(String token)
    {
    	if (token.startsWith("$")) return isPropertyValueToken(token);
    	if (token.startsWith("position(")) return isPositionToken(token);
    	return true;
    }
    
    /**
     * @param token
     * @return true if the token is of the form '$class.property'
     */
    private boolean isPropertyValueToken(String token)
    {
    	if (!token.startsWith("$")) return false;
    	StringTokenizer st = new StringTokenizer(token.substring(1),".");
    	return (st.countTokens()== 2);
    }
    
    /**
     * @param token
     * @return true if the token is of the form 'position(nodeName)'
     * Node names may contain '.' but not any of (), or ' '
     */
    private boolean isPositionToken(String token)
    {
    	if (!token.startsWith("position("))  return false;
    	if (!token.endsWith(")")) return false;
    	String nodeNameClose = token.substring("position(".length());
    	String nodeName = nodeNameClose.substring(0,nodeNameClose.length()-1);
    	StringTokenizer parts = new StringTokenizer(nodeName,"(), ");
    	return (parts.countTokens() == 1);
    }
    
    /**
     * @param token of the form 'position(<nodeName>)' (previously checked)
     * @return the node name
     */
    private String soughtNodeName(String token)
    {
    	String nodeNameClose = token.substring("position(".length());
    	String nodeName = nodeNameClose.substring(0,nodeNameClose.length()-1);
    	return nodeName;
    }
    
    /**
     * @param token
     * @return true if the token is 'value'
     */
    private boolean isNodeValueToken(String token)
    {
    	return (token.equals("value"));
    }
    /**
     * @param token
     * @return true if the token is 'length'
     */
    private boolean isLengthToken(String token)
    {
    	return (token.equals("length"));
    }
    
    /**
     * @param token
     * @return true if the token can be read as an integer
     */
    private boolean isIntegerToken(String token)
    {
    	try {new Integer(token); return true;}
    	catch (Exception ex) {return false;}
    }
    
    //-----------------------------------------------------------------------------------------
    //                     Evaluating the position function on a node
    //-----------------------------------------------------------------------------------------

    /**
     * Find the first element with name <nodeName> amongst this element and
     * its ancestors (ascending order) and return the position of that 
     * element amongst its siblings of the same name
     * @param token a String of the form 'position(<nodeName>)'
     * @param el an Element
     * @return the position of that element amongst its siblings of the same name;
     * or -1 if an ancestor of the right name cannot be found
     * 
     */
    private int findPosition(String token, Node node)
    {
    	int pos = -1; // duff return value if the node name cannot be found
    	boolean found = false;
    	Node current = node;
    	/* If this node does not have the right name, ascend through ancestors.
    	 * Stop after finding the first ancestor node of the right name.
    	 * The document root element has position -1. */
    	while ((getParentElement(current) != null) && (!found))
    	{
    		if (XMLUtil.getLocalName(current).equals(soughtNodeName(token)))
    		{
        		if (current instanceof Attr) pos = 1; // there can be only one attribute of given name
        		else if (current instanceof Element) pos = siblingPosition((Element)current);  
        		found = true;
    		}
    		current = getParentElement(current); 
    	}
    	return pos;
    }
    
    /**
     * 
     * @param node
     * @return the parent element of an Element or attribute; or null
     * if this is the Document Root Element
     */
    private Element getParentElement(Node node)
    {
    	Element parent = null;
    	if (node instanceof Attr) parent  = ((Attr)node).getOwnerElement();
    	if (node instanceof Element)
    	{
    		Node parentNode = ((Element)node).getParentNode();
    		// if this is the document root, its parent is a Document; so leave parent null
    		if (parentNode instanceof Element) parent = (Element)parentNode;
    	}
    	return parent;
    }
    
    /**
     * @param node
     * @return the position of an Element amongst its siblings of the same local name
     * positions are returned in the convention 1...N, not the java convention 0..N-1
     */
    private int siblingPosition(Element el)
    {
    	int pos = -1;
    	Element parent = (Element)el.getParentNode();
    	// use the same (local) name as XMLUtil uses to get the children
    	Vector<Element> siblings = XMLUtil.namedChildElements(parent, XMLUtil.getLocalName(el));
    	for (int i = 0; i < siblings.size();i++)
    		if (siblings.get(i).equals(el)) pos = i + 1;
    	return pos;
    }
    
    
    //-----------------------------------------------------------------------------------------
    //            Evaluating a property or pseudo-property on a node
    //-----------------------------------------------------------------------------------------

    /**
     * TODO: not implemented yet
     */
    private String getPropertyValue(String token, Node node)
    {
    	StringTokenizer st = new StringTokenizer(token.substring(1),".");
    	if (st.countTokens() != 2) return "";
    	// String className = st.nextToken();
    	// String propName = st.nextToken();
    	return "";
    }
    
    
}
