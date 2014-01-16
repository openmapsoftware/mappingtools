package com.openMap1.mapper.core;

/**
 * subclass of TranslationIssue for issues that arise at 
 * run time during translation or construction of EMF Ecore instances.
 * 
 * @author robert
 *
 */

public class RunIssue extends TranslationIssue implements SortableRow{
	
	private Xpth path;
	
	private String  problemType;
	
	private int step;
	
	public static int RUN_FATAL_ERROR = 0;
	public static int RUN_CONTEXT = 1;
	public static int RUN_ROOT_PATH = 2;
	public static int RUN_PROPERTY_CONVERSION = 3;
	public static int RUN_PROPERTY_REPRESENTED = 4;
	public static int RUN_OBJECT_REPRESENTED = 5;
	public static int RUN_ASSOCIATION_REPRESENTED = 6;
	public static int RUN_NULL_ELEMENT = 7;
	public static int RUN_OBJECT_IN_CONTEXT = 8;
	public static int RUN_GROUP_NUMBERS = 9;
	public static int RUN_OUTPUT_MAPPING = 10;
	public static int RUN_INCLUSION_FILTER = 11;
	public static int RUN_OBJECT_TOKEN = 12;
	public static int RUN_DUPLICATE_ELEMENT = 13;
	public static int RUN_REPRESENTED = 14;
	
	/**
	 * runtime issues which should stop the attempt to translate
	 * @return true if the error should stop the attempt to translate.
	 * NOTE: some of these seem overkill. 3/9/09 removed RUN_OBJECT_IN_CONTEXT
	 * from the list of  fatal errors
	 */
	public boolean isFatal()
	{
		return (
				(nature == RUN_FATAL_ERROR)|
				(nature == RUN_CONTEXT)|
				(nature == RUN_ROOT_PATH)|
				(nature == RUN_NULL_ELEMENT));
	}
	
	
	public static int RUN_XSLT_VARIABLE = 20;
	public static int RUN_XSLT_CONTEXT = 21;
	public static int RUN_XSLT_ROOT_PATH = 22;
	public static int RUN_XSLT_PROPERTY_CONVERSION = 23;
	public static int RUN_XSLT_PARAMETER = 24;
	public static int RUN_XSLT_TEMPLATE = 25;
	public static int RUN_XSLT_ASSOCIATION = 26;
	public static int RUN_XSLT_GROUPING = 27;
	public static int RUN_XSLT_EXCEPTION = 28;
	
	public boolean isXSLTIssue() {return (nature > 19);}
	
	
	/**
	 * Issues which XSLT cannot handle for some reason, 
	 * but which still may allow a viable transformation
	 * @return
	 */
	public boolean nonFatalXSLTIssue()
	{
		return (
				(nature == RUN_XSLT_PROPERTY_CONVERSION)|
				(nature == RUN_XSLT_GROUPING)
				);
	}
	
	
	public RunIssue(
			int nature, 
			String expected, 
			String actual,
			String problemType,
			Xpth path,
			int step)
	{
		super(nature,expected,actual);
		this.problemType = problemType;
		this.path = path;
		this.step = step;
	}
	
	// key for storing run issues to eliminate duplicates and just count occurrences
	public String key()
	{
		return (problemType + nature + step);
	}
	
	/**
	 * increment the number of occurrences of this issue
	 */
	public void addOccurrence() {occurrences++;}


	/**
	 * 
	 * @param col column index 0..N
	 * @return the entry for this translation issue in the column col
	 */	
	public String cellContents(int col)
	{
		String cell = "";
		if (col == CODE) cell = fileName;
		else if (col == TYPE)
		{
			cell = "Run";
			if (isXSLTIssue()) cell = "XSLT";
		}
		else if (col == OCCURRENCES) cell = new Integer(occurrences).toString();
		else if (col == DESCRIPTION) cell = description();
		else if (col == CAUSEORLOCATION) 
		{
			cell = path.stringForm();	
			if (path.size() == 0) cell = "No path";
		}
		return  cell;
	}
	
	public String description()
	{
		return problemType;
	}
	

}
