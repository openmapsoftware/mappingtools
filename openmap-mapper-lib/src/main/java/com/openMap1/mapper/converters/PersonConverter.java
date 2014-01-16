package com.openMap1.mapper.converters;

import java.util.Hashtable;

/**
 * Simple example of a property conversion class - 
 * converts between age and date of birth.
 * 
 * @author robert
 *
 */

public class PersonConverter {
	
	private static Integer thisYear = new Integer(2008); 
	
	public static Hashtable<?,?> convertAgeIn_Initialise() {return new Hashtable<String,String>();}
	public static Hashtable<?,?> convertAgeOut_Initialise() {return new Hashtable<String,String>();}
	
	public static String convertAgeIn(Hashtable<?,?> ht, String yearBorn)
	{
		Integer age = thisYear - new Integer(yearBorn);
		return age.toString();
	}
	
	public static String convertAgeOut(Hashtable<?,?> ht, String age)
	{
		Integer yearBorn = thisYear - new Integer(age);
		return yearBorn.toString();
	}
	
	

}
