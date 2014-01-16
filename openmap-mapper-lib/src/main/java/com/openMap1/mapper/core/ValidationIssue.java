package com.openMap1.mapper.core;

/**
 * Issue that gives one row of the Translation Issues View after
 * a translation test, arising from validation of a mapping set.
 * 
 * @author robert
 *
 */

public class ValidationIssue extends TranslationIssue {
	
	private String mappingSetName;
	
	private String code = "";
	public void setCode(String code) {this.code = code;}

	/**
	 * This class rather bends the fields of its superclass, to make it hold data from
	 * org.eclipse.emf.common.util.Diagnostic
	 * @param nature: used to hold Diagnostic.getCode()
	 * @param expected: used to hold the class name of the node involved
	 * @param actual: used to hold Diagnostic.getMessage() - the description
	 */
	public ValidationIssue(
			int nature, 
			String expected, 
			String actual,
			String mappingSetName)
	{
		super(nature,expected,actual);
		this.mappingSetName = mappingSetName;
	}
	
	public String description() {return actual;}

	/**
	 * 
	 * @param col column index 0..N
	 * @return the entry for this translation issue in the column col
	 */	
	public String cellContents(int col)
	{
		String cell = "";
		if (col == CODE) cell = code;
		else if (col == TYPE) cell = "Validate";
		else if (col == OCCURRENCES) cell = new Integer(occurrences).toString();
		else if (col == DESCRIPTION) cell = description();
		else if (col == CAUSEORLOCATION) cell = mappingSetName + ": validate to locate occurrences";		
		return  cell;
	}

}
