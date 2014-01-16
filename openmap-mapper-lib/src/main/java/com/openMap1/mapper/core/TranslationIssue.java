package com.openMap1.mapper.core;

import java.util.Vector;


/**
 * abstract superclass of the two classes StructureMismatch and SemanticMismatch which 
 * are used to record and display problems found in translation tests.
 * @author robert
 *
 */
abstract public class TranslationIssue implements SortableRow{

	
	public static String[] columnTitle = {"Code","Type","Occs","Description",
		"Cause, or Location in Result"};
	
	public static int[] columnWidth = {50,80,50,400,300};
	
	protected static int CODE = 0;
	protected static int TYPE = 1;
	protected static int OCCURRENCES = 2;
	protected static int DESCRIPTION = 3;
	protected static int CAUSEORLOCATION = 4;

	/**
	 * int constants STRING, NUMBER defined in SortableRow which define how
	 * each column is to be sorted
	 */
	public static int[] sortType() 
	{
		int len = columnTitle.length;
		int[] sType = new int[len];
		sType[CODE] = STRING;
		sType[TYPE] = STRING;
		sType[OCCURRENCES] = NUMBER;
		sType[DESCRIPTION] = STRING;
		sType[CAUSEORLOCATION] = STRING;
		return sType;
	}
	
	/**
	 * @return nature of the mismatch - see static constants of the class
	 */
	public int nature() {return nature;}
	protected int nature;
	
	/**
	 * @return expected value of some property of the node,
	 * or property of an object in a class, or expected number of occurrences
	 * of a class or property
	 */
	public String expected() {return expected;}
	protected String expected;
	
	/**
	 * @return actual value of some property of the node, 
	 * or property of an object in a class, or number of occurrences
	 * of a class or property,
	 * when it is not equal to the expected value or number of occurrences
	 */
	public String actual() {return actual;}
	protected String actual;
	
	/**
	 * @return name of the file in which the mismatch occurred
	 * (not set in the constructor)
	 */
	public String fileName() {return fileName;}
	protected String fileName = "";
	public void setFileName(String fileName) {this.fileName = fileName;}
	
	// instance variables maintained by this class and its subclasses
	protected int occurrences;
	public void addOccurrence() {occurrences++;}
	
	protected boolean missingMapping = false; // true if a missing object or feature is caused by a missing mapping
	String missingMappingDescription = "";
	
	/**
	 * @param pathString path to the bad node
	 * @param nature nature of the problem - unexpected name, missing, or repeated
	 * @param expected expected value of some property
	 * @param actual actual wrong value of that property
	 */
	public TranslationIssue( 
			int nature, 
			String expected, 
			String actual)
	{
		this.nature = nature;
		this.expected = expected;
		this.actual = actual;
		occurrences = 1; // will be increased if this issue is duplicated
	}
	
	/**
	 * @return description of the mismatch and where it occurred
	 */
	abstract public String description();
	
	/**
	 * @param col column index 0..N
	 * @return the entry for this translation issue in the column col
	 */
	abstract public String cellContents(int col);

	
	/**
	 * for an object to serve as a sortable row for class RowSorter,
	 * it must be able to present the cell contents as a Vector.
	 * @return
	 */
	public Vector<String> rowVector()
	{
		Vector<String> row = new Vector<String>();
		for (int i = 0; i < columnTitle.length;i++) row.add(cellContents(i));
		return row;
	}
	
	


}
