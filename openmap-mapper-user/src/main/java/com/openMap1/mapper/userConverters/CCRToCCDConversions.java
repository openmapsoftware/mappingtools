package com.openMap1.mapper.userConverters;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Conversions of property values between ASTM CCR and HL7  CCD.
 * 
 * @author robert
 *
 */

public class CCRToCCDConversions {
	
	/**
	 * To convert from a CCR date such as '1999-11' to the form '199911' used in the CCD example,
	 * simply remove the dashes
	 * @param lookup a lookup table - ignored
	 * @param CCRDate
	 * @return the date in the form used by the CCD example
	 */
	static public String CCRToCCDDate(Hashtable<?,?> lookup, String CCRDate)
	{
		String CCDDate = "";
		StringTokenizer st = new StringTokenizer(CCRDate,"-");
		while (st.hasMoreTokens()) CCDDate = CCDDate + st.nextToken();
		return CCDDate;
	}
	
	/**
	 * To convert from a CCD date such as '199911' to the form '1999-11' used in the CCR example,
	 * or '19991104' to '1999-11-04'
	 * insert a dash after 4 characters and after 6 characters, if necessary
	 * @param lookup a lookup table - ignored
	 * @param CCDDate
	 * @return the date in the form used by the CCR example
	 */
	static public String CCDToCCRDate(Hashtable<?,?> lookup, String CCDDate)
	{
		String CCRDate = CCDDate;
		if (CCDDate.length() == 6) CCRDate = CCDDate.substring(0,4) + "-" + CCDDate.substring(4,6);
		if (CCDDate.length() == 8) CCRDate = CCDDate.substring(0,4) 
			+ "-" + CCDDate.substring(4,6)+ "-" + CCDDate.substring(6,8);
		return CCRDate;
	}

}
