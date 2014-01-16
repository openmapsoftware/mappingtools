package com.openMap1.mapper.converters;

import java.util.Hashtable;

/**
 * Simple example of a property conversion class, with two different conversions:
 * (1) engine capacity, from cubic centimetres to cubic inches
 * (2) vehicle type. from a small code to a descriptive name.
 * 
 * @author robert
 *
 */

public class VehicleConverter {
	
	private static double ccConversion = 16.4;
	
	public static Hashtable<?,?> convertccIn_Initialise() {return new Hashtable<String,String>();}
	public static Hashtable<?,?> convertccOut_Initialise() {return new Hashtable<String,String>();}
	
	/** convert integer cubic inches to integer cc */
	public static String convertccIn(Hashtable<?,?> ht, String cuInch)
	{
		Integer cuInchCap = new Integer(cuInch);
		Double ccCap = (ccConversion*cuInchCap);
		return new Long(Math.round(ccCap)).toString();
	}
	
	/** convert integer cc to integer cubic inches 
	 * Rounding will not give a precise round trip. */
	public static String convertccOut(Hashtable<?,?> ht, String cc)
	{
		Integer ccCap = new Integer(cc);
		Double cuInchCap = (ccCap/ccConversion);
		return new Long(Math.round(cuInchCap)).toString();
	}
	
	/**
	 * to illustrate putting lookups into a Hashtable on initialisation
	 * - stored conversions between type strings and codes, which might 
	 * typically come from a database
	 */
	private static String[][] typeToCode =
		{
		{"saloon","SL"},
		{"coupe","CP"},
		{"gas guzzler","SUV"},
		{"articulated","AR"},
		{"light","LT"},
		{"heavy","HV"}
		};

	/**
	 * pre-store a lookup table for typeCode => type conversion
	 * @return
	 */
	private static Hashtable<String,String> convertTypeIn_Initialise() 
	{
		Hashtable<String,String> ht = new Hashtable<String,String>();
		for (int i = 0; i < typeToCode.length;i++)
		{
			String[] conv = typeToCode[i];
			ht.put(conv[1], conv[0]); // typeCode is key
		}
		return ht;
	}
	
	
	/**
	 * pre-store a lookup table for type => typeCode conversion
	 * @return
	 */
	private static Hashtable<String,String> convertTypeOut_Initialise() 
	{
		Hashtable<String,String> ht = new Hashtable<String,String>();
		for (int i = 0; i < typeToCode.length;i++)
		{
			String[] conv = typeToCode[i];
			ht.put(conv[0], conv[1]); // type is key
		}
		return ht;
	}

	public static String convertTypeIn(Hashtable<String,String> ht, String typeCode)
	{
		// initialise the lookup table on the first call only
		if (ht.size() == 0) ht = convertTypeIn_Initialise();

		String type = ht.get(typeCode);
		if (type == null) type = "";
		return type;
	}

	public static String convertTypeOut(Hashtable<String,String> ht, String type)
	{
		// initialise the lookup table on the first call only
		if (ht.size() == 0) ht = convertTypeOut_Initialise();

		String typeCode  = ht.get(type);
		if (typeCode == null) typeCode = "";
		return typeCode;
	}

}
