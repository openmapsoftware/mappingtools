package com.openMap1.mapper.userConverters;

import java.util.StringTokenizer;

/**
 * class of generally useful FHIR property conversion methods, 
 * to be inherited or used by specific FHIR converter class for specific databases or XMLs.
 * 
 * @author Robert
 *
 */

public class FHIRConverters {
	
	/**
	 * add a duration in minutes to a time, assuming it does not run over midnight
	 * (most appointments do not)
	 * @param startTime
	 * @param minutes
	 * @return startTime string with minutes added
	 */
	static String addMinutes(String startTime, String minutes) 
	{
		int hours = 0;
		int mins = 0;
		int secs = 0;
		int duration = 0;
		String hourString = "00";
		String minString = "00";
		try
		{
			StringTokenizer st = new StringTokenizer(startTime,":");
			if (st.countTokens() > 3) throw new Exception("Too many time units in " + startTime);
			hours = new Integer(st.nextToken()).intValue();
			if (st.hasMoreTokens()) mins = new Integer(st.nextToken()).intValue();
			if (st.hasMoreTokens()) secs = new Integer(st.nextToken()).intValue();
			duration  = new Integer(minutes).intValue();
			
			int newMins = mins + duration;
			int addHours = newMins/60;
			hours = hours + addHours;
			mins = newMins - 60*addHours;
			
			hourString = new Integer(hours).toString();
			if (hours < 10) hourString = "0" + hourString;
			minString = new Integer(mins).toString();
			if (mins < 10) minString = "0" + minString;
		}
		catch (Exception ex) {message("Time error: " + ex.getMessage() + " when adding " + minutes + " to " + startTime);}
		
		return (hourString + ":" + minString + ":" + secs);
	}
	
	static void message(String s) {System.out.println(s);}

}
