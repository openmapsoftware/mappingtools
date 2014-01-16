package com.openMap1.mapper.structures;

import java.util.StringTokenizer;

import com.openMap1.mapper.core.MapperException;
/**
 * Class to convert between a single string (stored in an EAnnotation)
 * to the items of information needed to define an association in a micro-ITS
 * @author robert
 */

public class ITSAssociation {
	
	/**
	 * @return true if this association is to be collapsed into its parent in the ITS
	 */
	public boolean isCollapsed() {return isCollapsed;}
	public void setCollapsed(boolean isCollapsed) {this.isCollapsed = isCollapsed;}
	private boolean isCollapsed;
	
	/**
	 * @return true if some descendant attributes of this association
	 * are to be included in the ITS
	 */
	public boolean attsIncluded() {return attsIncluded;}
	public void setAttsIncluded(boolean attsIncluded) {this.attsIncluded = attsIncluded;}
	private boolean attsIncluded;
	
	/**
	 * @return true if the minimum multiplicity has been constrained from 0 (in the original model) to 1
	 */
	public boolean lowerBoundIsConstrained() {return lowerBoundIsConstrained;}
	public void setLowerBoundConstraint(boolean lowerBoundIsConstrained) {this.lowerBoundIsConstrained = lowerBoundIsConstrained;}
	private boolean lowerBoundIsConstrained;
	
	
	/**
	 * @return true if the maximum multiplicity has been constrained from * (-1) (in the original model) to 1
	 */
	public boolean upperBoundIsConstrained() {return upperBoundIsConstrained;}
	public void setUpperBoundConstraint(boolean upperBoundIsConstrained) {this.upperBoundIsConstrained = upperBoundIsConstrained;}
	private boolean upperBoundIsConstrained;
	
	/**
	 * @return true if the child nodes of this association are to be ordered, in the simplified 
	 * class model and the mappings to it
	 */
	public boolean childrenAreOrdered() {return childrenAreOrdered;}
	public void setChildrenAreOrdered(boolean childrenAreOrdered) {this.childrenAreOrdered = childrenAreOrdered;}
	private boolean childrenAreOrdered;

	/**
	 * @return the business name to be used for this association in the ITS;
	 * if "", the name in the RMIM is to be used.
	 * Business names for nodes cannot be 'L$' or 'U$', because those indicate constrained multiplicities
	 */
	public String businessName() {return businessName;}
	public void setBusinessName(String businessName) {this.businessName = businessName;}
	private String businessName;
	
	/**
	 * constructor for an EReference with no ITS annotation
	 */
	public ITSAssociation()
	{
		isCollapsed = false;
		attsIncluded = false;
		businessName = "";
		upperBoundIsConstrained = false;
		lowerBoundIsConstrained = false;
		childrenAreOrdered = false;
	}
	
	/**
	 * constructor for an EReference with an ITS annotation
	 */
	public ITSAssociation(String annotation) throws MapperException
	{
		isCollapsed = false;
		attsIncluded = false;
		businessName = "";
		upperBoundIsConstrained = false;
		lowerBoundIsConstrained = false;

		StringTokenizer st = new StringTokenizer(annotation,":");
		int len = st.countTokens();
		if ((len <2)|(len > 6)) 
			throw new MapperException("Cannot make ITSAssociation from '" + annotation + "'");

		// 'collapse' indicator 'T' or 'F' is always present
		String collapse = st.nextToken();
		if ((!collapse.equals("T")) && (!collapse.equals("F")))
			throw new MapperException("Invalid first symbol in '" + annotation +"'");
		isCollapsed = (collapse.equals("T"));

		// 'attributes included' indicator 'I' or 'E' is always present
		String atts = st.nextToken();
		if ((!atts.equals("I")) && (!atts.equals("E")))
			throw new MapperException("Invalid second symbol in '" + annotation +"'");
		attsIncluded = (atts.equals("I"));
		
		/* indicators 'L$' and 'U$' for constrained multiplicity may or may not be present,
		 * indicator 'C$' for ordered child nodes may or may not be present 
		 * and a business name may or may not be present */
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (token.equals("L$")) lowerBoundIsConstrained = true;
			else if (token.equals("U$")) upperBoundIsConstrained = true;
			else if (token.equals("C$")) childrenAreOrdered = true;
			else businessName= token;
		}

	}
	
	/**
	 * String form to be stored in the Ecore model annotation
	 * @return
	 */
	public String stringForm()
	{
		String res = "F:";
		if (isCollapsed) res = "T:";
		if (attsIncluded) 
			{res = res + "I:";}
		else 
			{res = res + "E:";}
		if (lowerBoundIsConstrained) res = res + "L$:";
		if (upperBoundIsConstrained) res = res + "U$:";
		if (childrenAreOrdered) res = res + "C$:";
		res = res + businessName;
		return res;
	}
	
	/**
	 * @return true if this is the default state of the association ,needing no annotation
	 */
	public boolean noAnnotationNeeded()
	{
		return (!isCollapsed) 
				&& (!attsIncluded) 
				&& (!lowerBoundIsConstrained) 
				&& (!upperBoundIsConstrained)
				&& (!childrenAreOrdered)
				&& (businessName.equals(""));
	}

}
