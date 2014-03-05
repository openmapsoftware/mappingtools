package com.openMap1.mapper.query;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;

import com.openMap1.mapper.LocalPropertyConversion;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.ValuePair;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.util.GenUtil;

/**
 * a query condition, which can take one of two forms:
 * 
 *  - class.property (relation) constant value
 *  - class.property (relation) class.property
 *  
 *  In both cases they must refer to QueryClasses, as one class may occur in different parts of a query.
 *  
 * @author Robert
 *
 */

public class QueryCondition extends QueryMappingUser{
	
	private QueryClass queryClass;
	public QueryClass queryClass() {return queryClass;}
	
	private String propName;
	public String propName() {return propName;}
	
	private String relation;
	public String relation() {return relation;}
	
	private String constantValue;
	public String constantValue() {return constantValue;}
	
	private QueryClass otherQueryClass;
	public QueryClass otherQueryClass() {return otherQueryClass;}
	
	private String otherPropName;
	public String otherPropName() {return otherPropName;}
	
	/* these appear never to be used
	  
	private Hashtable<String,Boolean> leftIsDirect;
	private Hashtable<String,Boolean> rightIsDirect;
	
	public void setLeftIsDirect(String code, boolean direct) {leftIsDirect.put(code, new Boolean(direct));}
	public void setRightIsDirect(String code, boolean direct) {rightIsDirect.put(code, new Boolean(direct));}
	
	public boolean leftIsDirect(String code) {return((leftIsDirect.get(code) != null) && (leftIsDirect.get(code).booleanValue()));}
	public boolean rightIsDirect(String code) {return((rightIsDirect.get(code) != null) && (rightIsDirect.get(code).booleanValue()));}
	*/ 

	private String stringForm;
	public String stringForm() {return stringForm;}
	
	/**
	 * constructor for constant value conditions
	 * @param queryClass
	 * @param propName
	 * @param relation
	 * @param constantValue
	 */
	public QueryCondition(QueryClass queryClass,String propName, String relation, String constantValue, QueryParser parser)
	{
		super(parser);
		this.queryClass = queryClass;
		this.propName = propName;
		this.relation = relation;
		this.constantValue = constantValue;
		this.otherQueryClass = null;
		this.otherPropName = null;
		makeStringForm();
	}
	
	/**
	 * constructor for a condition between two property values
	 * @param queryClass
	 * @param propName
	 * @param relation
	 * @param otherQueryClass
	 * @param otherPropName
	 */
	public QueryCondition(QueryClass queryClass,String propName, String relation, QueryClass otherQueryClass, String otherPropName, QueryParser parser)
	{
		super(parser);
		this.queryClass = queryClass;
		this.propName = propName;
		this.relation = relation;
		this.constantValue = null;
		this.otherQueryClass = otherQueryClass;
		this.otherPropName = otherPropName;
		makeStringForm();
	}
	
	/**
	 * what kind of condition this is
	 * @return
	 */
	public boolean isConstantCondition() {return (constantValue != null);}
	
	private void makeStringForm()
	{
		stringForm = queryClass.className() + "." + propName + " " + relation;
		if (isConstantCondition()) stringForm = stringForm + " '" + constantValue + "'";
		else stringForm = stringForm + " " + otherQueryClass.className() + "." + otherPropName;				
	}


	//-------------------------------------------------------------------------------------
	//	                         test query conditions
	//-------------------------------------------------------------------------------------
	
	
	/**
	 * Evaluate this query condition when a partial result is available.
	 * The partial result always contains an objectToken for the QueryClass, and may or may not 
	 * contain an objectToken for the other class (which may come later in the strategy).
	 * If the test depends on another class which comes later in the strategy, return true.
	 * @return
	 */
	public boolean evaluate(Vector<QueryClass> strategy, Vector<objectToken> partialResult) throws MapperException
	{
		objectToken left = findObject(queryClass,strategy, partialResult);
		/* if the LHS object of the query has not yet been found, make the test 
		 * pass for now, as it will be evaluated later in the strategy.
		 * but if the LHS is not represented or not found, make the test fail */
		if (left == null) return true;
		if (left.isEmpty()) return false;
		if (isConstantCondition()) return test(left,null);

		else
		{
			objectToken right = findObject(otherQueryClass,strategy, partialResult);
			
			/* if the RHS object of the query has not yet been found, make the test 
			 * pass for now, as it will be evaluated later in the strategy.
			 * but if the RHS is not represented or not found, make the test fail */
			if (right == null) return true;
			if (right.isEmpty()) return false;
			else return test(left,right);
		}
	}

	/**
	 * find the objectToken needed to evaluate one side of this condition, if the object is in the partial result;
	 * otherwise return null.
	 * @param objClass
	 * @param strategy
	 * @param partialResult
	 * @return
	 */
	private objectToken findObject(QueryClass objClass, Vector<QueryClass> strategy, Vector<objectToken> partialResult)
	{
		objectToken oTok = null;
		
		// find the position of this QueryClass in the strategy
		int pos = 1000;
		for (int i = 0; i < strategy.size();i++)
			if (objClass.equals(strategy.get(i))) pos = i;
		
		// if an objectToken for the QueryClass is in the partial result, return it
		if (pos < partialResult.size()) oTok = partialResult.get(pos);
		
		return oTok;
	}

		/**
		 * 
		 * @param oRep
		 * @param otherORep
		 * @return
		 * @throws MapperException
		 */
	    public boolean test(objectToken oRep, objectToken otherORep) throws MapperException
	    {
	    	// empty objectTokens fail all tests
	    	if (oRep.isEmpty()) return false;

	    	String leftVal = oRep.reader().getPropertyValue(oRep,  propName);

	    	String rightVal = constantValue;
	    	if (rightVal == null) 
	    	{
	    		// comparisons against empty objects fail
	    		if (otherORep.isEmpty()) return false;
	    		rightVal = oRep.reader().getPropertyValue(otherORep, otherPropName);
	    	}
	    	
	    	return testOneCondition(leftVal, relation, rightVal);	    	
	    }

	    /**
	     * 
	     * @param left String value for the left-hand side
	     * @param test String denoting the test to be applied
	     * @param right String value of the right-hand side
	     * @return true if the test is passed; false if not, or if any arguments are null
	     * @throws MapperException
	     */
	    public static boolean testOneCondition(String left, String test, String right) throws MapperException
	    {
	        boolean res = false;

	        if (test == null) return true; // if the test is undefined, pass, as records will be filtered later
	        
	        // for null or empty values, the query result display shows "--". Make the tests work using that value
	        if ((left == null)|((left != null) && (left.equals("")))) left = "--";

	        // if the right-hand side is null, the test returns false
	        if (right != null)
	        {
	            // text equality test does not ignore case, because RDB retrieval has not ignored it
	            if (test.equals("=")) {res = (left.equals(right));}
	            
	            // number tests
	            else if ((test.equals(">"))|
	                    (test.equals(">="))|
	                    (test.equals("<"))|
	                    (test.equals("<=")))
	            {
	            try{
	                float f1 = new Float(left).floatValue();
	                try{
	                    float f2 = new Float(right).floatValue();
	                    if (test.equals(">")) {res = (f1 > f2);}
	                    else if (test.equals(">=")) {res = (f1 >= f2);}
	                    else if (test.equals("<")) {res = (f1 < f2);}
	                    else if (test.equals("<=")) {res = (f1 <= f2);}
	                }
	                catch (NumberFormatException e2)
	                    {throw new MapperException("Cannot convert '" + right + "' to a number. " + e2.getMessage());}
	            }
	            catch (NumberFormatException e1)
	                {throw new MapperException("Cannot convert '" + left + "' to a number. "+ e1.getMessage());}
	            }
	            // the 'contains' and 'containedIn' tests ignore case
	            else if (test.equalsIgnoreCase("contains"))
	                {res = GenUtil.contains(left.toUpperCase(),right.toUpperCase());}
	            else if (test.equalsIgnoreCase("notContains"))
	            	{res = !GenUtil.contains(left.toUpperCase(),right.toUpperCase());}
	            // the 'startsWith' test ignores case
	            else if (test.equalsIgnoreCase("startsWith"))
	                {res = left.toUpperCase().startsWith(right.toUpperCase());}
	            else if (test.equalsIgnoreCase("in"))
	            	{res = testInclusion(true,left,right);}
	            else if (test.equalsIgnoreCase("notIn"))
	        		{res = testInclusion(false,left,right);}
	            else if (test.equalsIgnoreCase("before"))
	        		{res = (left.compareTo(right) < 0);}
	            else if (test.equalsIgnoreCase("after"))
	        		{res = (left.compareTo(right) > 0);}
	        }
	        return res;
	    }
	    
	    /**
	     * test whether the leftValue is included in a set of values denoted by the right value, 
	     * ignoring case
	     * @param keepIncluded if true, return true iff the left value is one of the right values
	     * if false, return true if the left value is not one of the right values
	     * @param leftValue the value to be compared
	     * @param rightValue the set of values - separated by a '|' symbol
	     * @return the result of the test
	     */
	    static boolean testInclusion(boolean keepIncluded, String leftValue, String rightValue)
	    {
	    	boolean included = false;
	    	StringTokenizer st = new StringTokenizer(rightValue,"|");
	    	while ((st.hasMoreTokens()) && (!included))
	    	{
	    		if (st.nextToken().equalsIgnoreCase(leftValue)) included = true;
	    	}
	    	boolean result = included;
	    	if (!keepIncluded) result = !included;
	    	return result;    	
	    }

		
		/**
		 * FIXME - should allow for non-local property conversions (usiallu cannot make an SQL condition)
		 * add all tables, columns and conditions to an SQLQuery to ensure it retrieves the smallest DOM
		 * required to support a query
		 */
		public void buildQuery(SQLQuery query, String code) throws MapperException
		{
			// there may be several property mappings because of property conversions
			Vector<PropMapping> pMaps = allMappings.get(code);
			
			// 'true' means the property mapping, being involved in a query condition, is core to the query
			if (pMaps != null) for (Iterator<PropMapping> it = pMaps.iterator();it.hasNext();)
				handlePropMapping(it.next(), query, code,true);
			
			//  only add an SQL condition when there are no non-local property conversions, i.e. just one property mapping for each side
			boolean canAddSQLCondition = false;
			if ((isConstantCondition()) && (getMappings(code).size() == 1)) canAddSQLCondition = true;
			if ((!isConstantCondition()) && (getMappings(code).size() == 2)) canAddSQLCondition = true;
			
			if (canAddSQLCondition)
			{
				PropMapping leftProp = getMappings(code).get(0);

				// if there is a local property conversion with defined value pairs, try to find the structure value for the supplied model value
				String valueToCompare = constantValue;
				LocalPropertyConversion conversion = leftProp.getLocalPropertyConversion();
				if ((conversion != null) && (isConstantCondition()))
				{
					valueToCompare = null;
					EList<ValuePair> pairs = conversion.getValuePairs();
					if (pairs != null) for (Iterator<ValuePair> it = pairs.iterator();it.hasNext();)
					{
						ValuePair pair = it.next();
						if (pair.getModelValue().equals(constantValue)) valueToCompare = pair.getStructureValue();
					}
				}
				
				// add the SQL condition for a fixed value - possibly for conditions other than '='
				if ((isConstantCondition()) && (valueToCompare != null))
				{
					query.addValCondition(getTableName(leftProp), getColName(leftProp.getStringRootPath()), relation, valueToCompare);
				}
				
				// add the SQL condition for an equality condition between two property values, 
				else if ((!isConstantCondition()) && (relation.equals("=")))
				{
					// there are exactly two Property Mappings; the RHS property is the second of them
					PropMapping rightProp = getMappings(code).get(1);
					
					// we can only add an SQL link condition if neither property mapping uses a property conversion
					if ((leftProp.getLocalPropertyConversion() == null) && (rightProp.getLocalPropertyConversion() == null))
						query.addCrossCondition(getTableName(leftProp), getColName(leftProp.getStringRootPath()), 
							"=", getTableName(rightProp), getColName(rightProp.getStringRootPath()),true);					
				}
				
			}
		}
		


}
