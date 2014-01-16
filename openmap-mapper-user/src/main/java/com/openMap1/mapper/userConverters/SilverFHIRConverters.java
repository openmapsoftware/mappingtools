package com.openMap1.mapper.userConverters;

import java.util.Hashtable;

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
		return (startDate + "T" + startTime + "Z");
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
		return (startDate + "T" + addMinutes(startTime,duration)  + "Z");
	}


}
