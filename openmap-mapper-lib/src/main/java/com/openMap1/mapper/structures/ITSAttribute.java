package com.openMap1.mapper.structures;

import java.util.StringTokenizer;

import com.openMap1.mapper.core.MapperException;

/**
 * Class to convert between a single string (stored in an EAnnotation)
 * to the items of information needed to define an attribute in a micro-ITS
 * @author robert
 *
 */

public class ITSAttribute {
	
	/**
	 * @return true if this attribute is to be included in the ITS
	 */
	public boolean isIncluded() {return isIncluded;}
	public void setIncluded(boolean isIncluded) {this.isIncluded = isIncluded;}
	private boolean isIncluded;
	
	/**
	 * @return the business name to be used for this attribute in the ITS;
	 * if "", the name in the RMIM is to be used.
	 * businessName cannot be 'L$', as that is used to denote constrained lower bound
	 */
	public String businessName() {return businessName;}
	public void setBusinessName(String businessName) {this.businessName = businessName;}
	private String businessName;
		
	/**
	 * @return true if the minimum multiplicity has been constrained from 0 (in the original model) to 1
	 */
	public boolean lowerBoundIsConstrained() {return lowerBoundIsConstrained;}
	public void setLowerBoundConstraint(boolean lowerBoundIsConstrained) {this.lowerBoundIsConstrained = lowerBoundIsConstrained;}
	private boolean lowerBoundIsConstrained;

	/**
	 * constructor for an EAttribute with no ITS annotation
	 */
	public ITSAttribute()
	{
		isIncluded = false;
		businessName = "";
		lowerBoundIsConstrained = false;
	}

	/**
	 * constructor for an EAttribute with an ITS annotation
	 */
	public ITSAttribute(String annotation) throws MapperException
	{
		isIncluded = false;
		businessName = "";
		lowerBoundIsConstrained = false;

		StringTokenizer st = new StringTokenizer(annotation,":");
		int len = st.countTokens();
		if ((len <1)|(len > 3)) 
			throw new MapperException("Cannot make ITSAttribute from '" + annotation + "'");
		String inc = st.nextToken();
		if ((!inc.equals("T")) && (!inc.equals("F")))
			throw new MapperException("Invalid first symbol in '" + annotation +"'");
		
		isIncluded = (inc.equals("T"));
		
		/* indicator 'L$' for constrained multiplicity may or may not be present, 
		 * and a business name may or may not be present */
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (token.equals("L$")) lowerBoundIsConstrained = true;
			else businessName= token;
		}
	}
	
	public String stringForm()
	{
		String res = "F:";
		if (isIncluded) res = "T:";
		if (lowerBoundIsConstrained) res = res + "L$:";
		res = res + businessName;
		return res;
	}

}
