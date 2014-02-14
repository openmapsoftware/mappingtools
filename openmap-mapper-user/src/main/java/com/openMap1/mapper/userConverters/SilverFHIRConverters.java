package com.openMap1.mapper.userConverters;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * class of FHIR property conversion methods for the Silverlink database
 * @author Robert
 *
 */

public class SilverFHIRConverters extends FHIRConverters{
	
	
	/**
	 * compute the start dateTime (instant) of an appointment from  its start date and time
	 * @param lookup
	 * @param startDate
	 * @param startTime
	 * @return
	 */
	static public String makeAppointmentStart(Hashtable<?,?> lookup, String startDate, String startTime)
	{
		return (startDate + "T" + addZeroSeconds(startTime) + "Z");
	}
	
	
	/**
	 * compute the end dateTime (instant) of an appointment from its start date and time and duration in minutes
	 * Assume it does not run over midnight.
	 * @param lookup
	 * @param startDate
	 * @param startTime
	 * @param duration
	 * @return
	 */
	static public String makeAppointmentEnd(Hashtable<?,?> lookup, String startDate, String startTime, String duration)
	{
		return (startDate + "T" + addZeroSeconds(addMinutes(startTime,duration))  + "Z");
	}
	
	/**
	 * if only minutes are specified, add zero seconds on the end
	 * @param minutes
	 * @return
	 */
	static String addZeroSeconds(String minutes)
	{
		String roughStartTime = minutes;
		StringTokenizer st = new StringTokenizer(roughStartTime,":");
		if (st.countTokens() == 2) roughStartTime = roughStartTime + ":00";
		
		return roughStartTime;
	}


}
