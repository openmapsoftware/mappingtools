package com.openMap1.mapper.util;

import java.util.Date;

/**
 * Class used to record and write out timing
 * statistics for the Mapper tools
 * 
 * @author robert
 *
 */

public class Timer {
	
	private int nClocks = 100;
	
	// constants defining clocks to use
	public static int TRANSLATION_TEST = 0;
	public static int COMPILE = 1;
	public static int TRANSLATE = 2;
	public static int STRUCTURE_TEST = 3;
	public static int MAKE_EMF_INSTANCE = 4;
	public static int COMPARE_EMF_INSTANCE = 5;
	public static int SHOW_VIEW = 6;
	public static int REFRESH_DATA_SOURCE = 7;
	public static int GET_OBJECTS = 8;
	public static int GET_PROPERTIES = 9;
	public static int GET_ASSOCIATIONS = 10;
	public static int GET_METADATA = 11;
	public static int SET_XML_ROOT = 12;
	public static int MAPPING_INITIALISE = 13;
	public static int MAKE_NODE_INDEX = 14;
	public static int XPATH = 15;
	public static int INDEXED_XPATH = 16;
	public static int UNINDEXED_XPATH = 17;
	public static int ASSOCIATION_PRELIMINARIES = 18;
	public static int ASSOCIATION_CORE = 19;
	public static int FILTER_NODE_VECTOR = 20;
	public static int INDEXED_TOTAL = 21;
	public static int FIRST_END = 22;
	public static int SECOND_END = 23;
	public static int OTHER_CORE = 24;
	public static int FILL_OBJECT = 25;
	public static int REP_PROPERTY = 26;
	public static int REP_OBJECT = 27;
	public static int REP_ASSOCIATION = 28;
	public static int EMF_PRELIMINARIES = 29;
	public static int MAKE_OBJECTGETTER = 30;
	public static int MAKE_TRANSLATOR = 31;
	public static int CURRENT_SUSPECT = 32;
	public static int INCLUSION_FILTER = 33;
	public static int WPROC_STEP = 34;
	// next 13 reserved for WProc steps
	public static int EXECUTE_PROCEDURES = 48;
	public static int CALL_PROCEDURES = 49;
	public static int OUTPUT_PROPERTY = 50;
	public static int FIND_PROCEDURES = 51;
	public static int EMF_NON_CONTAINMENT = 52;
	public static int MAPPING_TEXT = 53;
	public static int LINK_FILTER = 54;
	public static int WHEN_FILTER = 55;
	public static int EDITOR_INIT = 56;
	public static int SET_FOCUS = 57;
	public static int MAPPINGS_VIEW = 58;
	public static int CLASS_MODEL_VIEW = 59;
	public static int NOTE_MAPPING = 60;
	public static int SHOW_MAPPINGS = 61;
	
	
	// clock names for reporting results
	private String[] clockName = {
			"Translation test",
			"Compile",
			"Translate",
			"Structure test",
			"Make EMF Instance",
			"Compare EMF Instances",
			"Show Translation summary view",
			"Refresh data source",
			"Get Objects",
			"Get Properties",
			"Get Associations",
			"Get Metadata",
			"Set XML Root",
			"Initialise Mappings",
			"Make Node Indexes",
			"XPath",
			"Use Indexes for XPath",
			"No Indexes for XPath",
			"Association Preliminaries",
			"Association Core",
			"Filter nodes",
			"Indexed total",
			"First end",
			"Second End",
			"Other core",
			"Fill Object",
			"Represents Property",
			"Represents Object",
			"Represents Association",
			"EMF Preliminaries",
			"Make ObjectGetter",
			"Make Writer",
			"Current suspect",
			"Inclusion Filter",
			"WProc no step",
			"Add element",
			"Fill element",
			"Add to context",
			"Set text",
			"Add attribute",
			"Get property",
			"Get constant",
			"Related properties",
			"Set when value",
			"Get all objects",
			"Association instance",
			"Grouping property",
			"un-prime",
			"Execute write procedures",
			"Call procedure",
			"Output property",
			"Find procedure",
			"Non containment links",
			"Object mapping text",
			"Link filter",
			"When filter",
			"Editor init",
			"Set focus",
			"Mappings View",
			"Class Model View",
			"Note mapping",
			"Show mappings",
			"",
			"",
			"",
			"",
			"",
	};
	
	public Clock[] clocks = new Clock[nClocks];
	
	private String timerName;
	
	/**
	 * make a new time , with a name that will be output when it reports
	 * @param timerName
	 */
	public Timer(String timerName)
	{
		for (int c = 0; c < nClocks; c++)
			clocks[c] = new Clock(c);
		this.timerName = timerName;
	}
	
	/**
	 * start a clock, identified by an integer clock index,
	 * but do not change the name by which it will be reported
	 * @param clockIndex
	 */
	public void start(int clockIndex)
	{
		clocks[clockIndex].start();
	}
	
	/**
	 * start a clock, identified by an integer clock index,
	 * and change the name by which it will be reported
	 * @param clockIndex
	 * @param newClockName
	 */
	public void start(int clockIndex, String newClockName)
	{
		clockName[clockIndex] = newClockName;
		clocks[clockIndex].start();
	}

	
	/**
	 * stop a clock, identified by an integer clock index
	 * @param clockIndex
	 */
	public void stop(int clockIndex)
	{
		clocks[clockIndex].stop();
	}
	
	/**
	 * report timings for all clocks that have been used
	 */
	public void report()
	{
		System.out.println("");
		System.out.println("Timings from timer '" + timerName + "'");
		String header = "#  \t Calls \tTotal  \tMax \tClock";
		System.out.println(header);
		for (int c = 0; c < nClocks; c++) clocks[c].report();
	}
	
	public String timerName() {return timerName;}
	
	public void addTimes(Timer timer)
	{
		for (int c = 0; c < nClocks; c++) clocks[c].addTimes(timer.clocks[c]);			
	}

	private class Clock
	{
		public int calls;		
		private boolean used;
		private int index;
		
		public long timeSpent;
		private long lastStart;
		private long maxTime;
		
		private Clock(int index)
		{
			calls = 0;
			used = false;
			timeSpent = 0;
			lastStart = 0;
			maxTime = 0;
			this.index = index;
		}
		
		private void start()
		{
			used = true;
			lastStart = new Date().getTime();
		}
		
		private void stop()
		{
			calls++;
			long interval = new Date().getTime() - lastStart;
			if (interval > maxTime) maxTime = interval;
			timeSpent = timeSpent + interval;
		}
		
		private String clockName()
		{
			String name = "unknown";
			if (index < clockName.length) name = clockName[index];
			return name;
		}
		
		private void report()
		{
			if (used) 
				System.out.println(" " + index + "\t"  + calls  + "\t" 
						+ timeSpent + "\t" + maxTime + "\t" + clockName());
		}
		
		private void addTimes(Clock cl)
		{
			calls = calls + cl.calls;
			timeSpent = timeSpent + cl.timeSpent;
		}
	}

}
