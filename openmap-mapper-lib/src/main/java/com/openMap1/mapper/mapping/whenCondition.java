package com.openMap1.mapper.mapping;


import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.writer.XSLGenerator;

import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.XpthException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.ValueCondition;

import org.w3c.dom.Node;

/**
 *
 * A condition in a mapping, which must hold for the node to represent what
 * the mapping says it represents.
 * The condition involves the value of one XML node, related to some constant value. 
 * This is a wrapper class for the model class ValueCondition
 *
 * @author Robert Worden
 * @version 1.0
 */
public class whenCondition extends Condition
{

    private String rightValue;
    /** the value on the right-hand side of the condition */
    public String rightValue() {return rightValue;}
    
    
    //------------------------------------------------------------------------------------------
    //                 	                      Constructors
    //------------------------------------------------------------------------------------------
   
    /**
     * Constructor for a whenCondition nested inside a Mapping
     * @param vc
     * @throws MapperException
     */
    public whenCondition(ValueCondition vc)
    throws MapperException
    {
    	super(vc);
        rightValue = vc.getRightValue();    	
    }
    
    
    /**
     * constructor for a whenCondition nested inside a Condition
     */
    public whenCondition(ValueCondition vc, Condition parentCondition, Xpth rootToStart) 
    throws MapperException
    {
    	super(vc,parentCondition,rootToStart);
        rightValue = vc.getRightValue();    	
    }

    
    //------------------------------------------------------------------------------------------
    //                 	           Evaluating the condition
    //------------------------------------------------------------------------------------------

    /**
     * evaluate this condition from a node defining the LHS
     *
     * @param LHS Node node defining the LHS
     * @param documentEl the document element of the document being read (added argument 9/08)
     * @return boolean the result of evaluating the condition
     * Return false with error message if no node exists
     */
    public boolean eval1(Node LHS, NamespaceSet context)
    {
        boolean res = false;
        try{
        	String left = getStringValueAtEndOfPath(LHS,lhsEndToLeftValue,leftPathWhenConditions,leftPathLinkConditions,context);
        	
        	res = testCondition( mappingCondition.getLeftFunction(),left,LHS,
        			test,
        			"",rightValue,null);
        }
        catch (Exception e) {GenUtil.message("eval XPath exception: " + e.getMessage());}
        return res;
    }
    
    //------------------------------------------------------------------------------------------
    //                 	           Other stuff
    //------------------------------------------------------------------------------------------

    /** true if two 'when' conditions are mutually exclusive,
    i.e. can never both be satisfied at once. */
    public boolean mutualExclusive(whenCondition wc) throws XpthException
    {
        return ((((test.equals("="))&&(wc.test.equals("=")))|
                ((test.equals("ignoreHash")) && (wc.test.equals("ignoreHash"))))
                && (rootToLeftValue.equalPath(wc.rootToLeftValue))
                && !(rightValue.equals(wc.rightValue)));
    }
    
    /** string form of the condition */
    public String XPathForm(XSLGenerator XX) throws MapperException
    {
        if (!test.equals("=")) {throw new MapperException("All when-conditions should be '=' tests.");}
        return ("(" + XX.convertPathPrefixes(lhsEndToLeftValue.stringForm()) 
        		+ " = '" + rightValue + "')");
    }


}
