package com.openMap1.mapper.mapping;

import java.util.Iterator;
import java.util.Vector;


import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.XpthException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.ClassSet;

import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.writer.XSLGenerator;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ValueCondition;

import org.w3c.dom.Node;

/**
 * mapping relating the values at two different XML nodes.
 * wrapper class for the model class CrossCondition
 * @author robert
 *
 */
public class linkCondition extends Condition
{
	/* the Mapper model object for which this is a wrapper */
	protected CrossCondition crossCondition() {return (CrossCondition)mappingCondition;}
	
	/** path from the root to the node at the RHS end of the path*/
	public Xpth rootToRHSEnd() {return rootToRHSEnd;}
	private Xpth rootToRHSEnd = null;
	
	/** relative path from node representing end object to node carrying RHS value */
	private Xpth rhsEndToRightValue = null; 
    public Xpth rhsEndToRightValue() {return rhsEndToRightValue;}

    /** path from the document root to the RHS value */
    public Xpth rootToRightValue() {return rootToRightValue;}
	private Xpth rootToRightValue = null; // path from root to node carrying RHS value

    /** class and subset of the mapped object */
    public ClassSet objectCSet() {return objectCSet;}
    private ClassSet objectCSet; // the class and subset of the object this is a link to

    /** for association link conditions, the end of the association; 1 or 2 
     * used by ProcedureWriter for link conditions on mappings*/
    public int end() {return end;}
    private int end; // for association link conditions, the end 1 or 2 that this refers to
    
    /** further when-conditions on the node at the start of the path (RHS) */
    protected Vector<whenCondition> rightPathWhenConditions = new Vector<whenCondition>();

    /** further link-conditions between the node reached at the end of the RHS path
     * (LHS of nested condition) and the node at the start of the RHS path
     * (RHS of nested condition) */
    protected Vector<linkCondition> rightPathLinkConditions = new Vector<linkCondition>();
    
    //------------------------------------------------------------------------------------------
    //                 	                      Constructors
    //------------------------------------------------------------------------------------------
    
    /**
     * Constructor for a linkCondition nested immediately inside a mapping
     * @param cc the CrossCondition for which this is a wrapper
     * @param end for association link conditions, the end of the association; 1 or 2
     */
    public linkCondition(CrossCondition cc, int end)
    throws MapperException
    {
    	super(cc);
        this.end = end;
        ObjMapping om = ModelUtil.getObjectMapping(map);
        objectCSet = om.getClassSet();
        rootToRHSEnd = om.getRootXPath();
        rhsEndToRightValue = new Xpth(NSSet(),cc.getRightPath());
        rootToRightValue = rhsEndToRightValue.crossToRootPath(rootToRHSEnd);
        
        makeRightPathConditions();
    }
    
    /**
     * constructor for a linkCondition nested inside another condition.
     * It may be inside a whenCondition or a linkCondition. In these
     * cases objectCSet and end are undefined; so assume they are never used
     * 
     * @param cc the Mapper CrossCondition this is a wrapper for
     * @param parentCondition the link- or when- condition this is nested inside
     * @param rootToLHSEnd the XPath from the root to the end node of the path which
     * this is a condition on
     * @param rootToRHSEnd the XPath from the root to the start node of the path which
     * this is a condition on
     */
    public linkCondition(CrossCondition cc, Condition parentCondition, Xpth rootToFilteredNodes, Xpth rootToRHSEnd) 
    throws MapperException
    {
    	super(cc,parentCondition,rootToFilteredNodes);

    	end = 0; // assume end is irrelevant for a nested link condition
    	objectCSet = null; //assume not needed for an nested link condition, 
    	
        this.rootToRHSEnd = rootToRHSEnd;
    	rhsEndToRightValue = new Xpth(NSSet(),cc.getRightPath());
        rootToRightValue = rhsEndToRightValue.crossToRootPath(rootToRHSEnd);

    	makeRightPathConditions();
    }
    
    /**
     * Make the nested whenConditions and linkConditions on the RHS path.
     * These conditions are used to filter the RHS value-providing nodes
     */
    private void makeRightPathConditions() throws MapperException
    {
    	for (Iterator<MappingCondition> it = crossCondition().getRightPathConditions().iterator();it.hasNext();)
    	{
    		MappingCondition mapCon = it.next();
    		/* a child condition filters the nodes that provide the RHS values of this condition,
    		 * by taking some relative path from them and comparing the value to a constant */
    		if (mapCon instanceof ValueCondition)
    			rightPathWhenConditions.add(new whenCondition((ValueCondition)mapCon,this,rootToRightValue));
    		/* a child condition filters the nodes that provide the RHS values of this condition,
    		 * by taking some relative path from them and comparing the value to a value
    		 * got by taking some other relative path from the filtered nodes of this condition */
        	if (mapCon instanceof CrossCondition)
        		rightPathLinkConditions.add(new linkCondition((CrossCondition)mapCon,this,rootToRightValue,rootToRHSEnd));
    	}   	    	
    }
    
    //------------------------------------------------------------------------------------------
    //                 	           Evaluating the condition
    //------------------------------------------------------------------------------------------
    
    public String leftValue(Node LHS, NamespaceSet context) throws MapperException
    {
    	String left = null;
        try{
        	left = getStringValueAtEndOfPath(LHS,lhsEndToLeftValue,
        			leftPathWhenConditions,leftPathLinkConditions,context);
        }
        catch (Exception e) 
        {throw new MapperException("Terminated because of XPath problem: " + e.getMessage());}
        return left;
    }
    
    public String rightValue(Node RHS, NamespaceSet context) throws MapperException
    {
    	String right = null;
        try{
        	right = getStringValueAtEndOfPath(RHS,rhsEndToRightValue,
        			rightPathWhenConditions,rightPathLinkConditions,context);
        }
        catch (Exception e) 
              {throw new MapperException("Terminated because of XPath problem: " + e.getMessage());}
        return right;
    }

    /** evaluate this condition given two nodes
    defining the LHS and the RHS.
    If either node does not exist, take its value as "". */
    public boolean eval2(Node LHS,Node RHS,NamespaceSet context) throws MapperException
    {
        boolean res = false;
        try{
        	String left = leftValue( LHS,  context);
        	String right = rightValue(RHS, context);
        	
        	res = testCondition(crossCondition().getLeftFunction(),left,LHS,
        			test,
        			crossCondition().getRightFunction(),right,RHS);

            // a straight string equality with both sides null or empty should fail
        	if ((crossCondition().getLeftFunction().equals("")) &&
        		(crossCondition().getRightFunction().equals("")) &&
        		(test.equals("=")) &&
                (left.equals("")) && 
                (right.equals(""))) res = false;
        }
        catch (Exception e) 
        {
        	GenUtil.surprise(e,"eval XPath exception: "); 
              throw new MapperException(e.getMessage());
        }
        return res;
    }

    /**
     *  return the expression to be included in an XPath expression to test this link condition.
    If fromObject is true, the XPath starts at the object node
    and ends at the property or association node.
    Otherwise it starts at the property or association node, and ends at the object node.
    startVar is the XSL variable representing the node-set at the start of the path;
    but if it is null, use current() in stead. */
    public String XPathTest(XSLGenerator XX,boolean fromObject, String startVar) throws XpthException
    {
      String stPos = "current()";
      if (startVar != null) stPos = "$" + startVar;
        String res = "";
        if (test.equals("="))
        {
            if (fromObject)
              {res = "(" 
            	  + XX.convertPathPrefixes(lhsEndToLeftValue.stringForm())
                  + " = " + stPos + "/" 
                  + XX.convertPathPrefixes(rhsEndToRightValue.stringForm()) + ")";}
            else
              {res = "(" + stPos + "/" 
            	  + XX.convertPathPrefixes(lhsEndToLeftValue.stringForm())
                  + " = " 
                  + XX.convertPathPrefixes(rhsEndToRightValue.stringForm()) + ")";}
        }
        // this interpretation of 'containsAsWord' could go wrong; adding ' ' would make it better
        else if (test.equals("containsAsWord"))
        {
            if (fromObject)
              {res = "(contains(" 
            	  + XX.convertPathPrefixes(lhsEndToLeftValue.stringForm())
                  + ", " + stPos + "/" 
                  + XX.convertPathPrefixes(rhsEndToRightValue.stringForm()) + "))";}
            else
              {res = "(contains(" + stPos + "/" 
            	  + XX.convertPathPrefixes(lhsEndToLeftValue.stringForm())
                  + ", " 
                  + XX.convertPathPrefixes(rhsEndToRightValue.stringForm()) + "))";}
        }
        else
        {
            GenUtil.message("XSLT generation for link conditions currently only supports tests '=' and 'containsAsWord'.");
        }
        return res;
    }
    
    public String stringForm()
    {
    	String form = lhsEndToLeftValue().stringForm() + "(" + getLeftFunction() + ") " 
    		 + test + " "
    		 + rhsEndToRightValue().stringForm() + "[" + end + "]";
    	return form;
    }
}
