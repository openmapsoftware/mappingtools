package com.openMap1.mapper.converters;

import java.util.Hashtable;

/**
 * property value conversions for the Oasis eMPR application
 * @author Robert
 *
 */

public class DemoConverter  {
	
	
	/**
	 * concatenate up to five parts of a person's name, separated by ' '
	 * @param ht not used
	 * @param name0
	 * @param name1
	 * @param name2
	 * @param name3
	 * @param name4
	 * @return the full name
	 */
	public static String concatenate5NameParts(Hashtable<?,?> ht, 
			String name0,
			String name1,
			String name2,
			String name3,
			String name4)
	{
		return (name0 + " " + name1 + " " + name2 + " " + name3 + " " + name4);
	}

	/**
	 * concatenate up to three parts of a person's name, separated by ' '
	 * @param ht not used
	 * @param name0
	 * @param name1
	 * @param name2
	 * @return the full name
	 */
	public static String concatenate3NameParts(Hashtable<?,?> ht, 
			String name0,
			String name1,
			String name2)
	{
		return (name0 + " " + name1 + " " + name2);
	}

}
