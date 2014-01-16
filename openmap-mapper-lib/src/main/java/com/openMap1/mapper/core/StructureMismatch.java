package com.openMap1.mapper.core;

/**
 * Class for recording discrepancies between an XML instance
 * and the structure definition in the mapped structure of 
 * which it is supposed to be an instance.
 *  
 * This test is weaker than schema validation, which is also done when there 
 * is a schema.
 * 
 * @author robert
 *
 */
public class StructureMismatch extends TranslationIssue {
	
	public static int STRUCTURE_UNEXPECTED_NAME = 0;
	public static int STRUCTURE_UNEXPECTED_VALUE = 1;
	public static int STRUCTURE_MISSING = 2;
	public static int STRUCTURE_REPEATED = 3;
	public static int STRUCTURE_MISSING_IMPORTED_MAPPING_SET = 4;
	public static int STRUCTURE_IN_WRAPPER_TRANSFORM = 5;
	public static int STRUCTURE_OUT_WRAPPER_TRANSFORM = 6;
	public static int STRUCTURE_OTHER_ISSUE = 7;
	
	protected String[] structureIssue = {
			"Incorrent node name",
			"Incorrect value",
			"Missing node",
			"Repeated single node",
			"No imported mapping set",
			"Cannot apply in-wrapper transform",
			"Cannot apply out-wrapper transform",
			"Other issue"
	};
	
	protected String structureIssue(int nature) {return structureIssue[nature];}
	
	/**
	 * path to the node where this mismatch was found
	 */ 
	public String pathString() {return pathString;}
	protected String pathString;
	
	/**
	 * @param pathString path to the bad node
	 * @param nature nature of the problem - unexpected name, missing, or repeated
	 * @param expected expected value of some property
	 * @param actual actual wrong value of that property
	 */
	public StructureMismatch(String pathString, 
			int nature, 
			String expected, 
			String actual)
	{
		super(nature,expected,actual);
		this.pathString = pathString;
	}
	
	/**
	 * @return description of the mismatch and where it occurred
	 */
	public String description()
	{
		String description = "";
		if (nature == STRUCTURE_UNEXPECTED_NAME)
			description = "Unexpected node '" + actual + "'";
		else if (nature == STRUCTURE_UNEXPECTED_VALUE)
			description = "Node with expected value '" 
			+ expected + "', actual value '" + actual + "'";
		else if (nature == STRUCTURE_MISSING)
			description = "Missing node with name '" + expected + "'";
		else if (nature == STRUCTURE_REPEATED)
			description = "Node which should be single is repeated";
		else if (nature == STRUCTURE_MISSING_IMPORTED_MAPPING_SET)
			description = "Cannot find imported mapping set";
		else if (nature == STRUCTURE_IN_WRAPPER_TRANSFORM)
			description = "Cannot apply in-wrapper transform: " + expected;
		else if (nature == STRUCTURE_OUT_WRAPPER_TRANSFORM)
			description = "Cannot apply out-wrapper transform: " + expected;
		else if (nature == STRUCTURE_OTHER_ISSUE)
			description = actual;
		return description;
	}

	
	/**
	 * @param col column index 0..N
	 * @return the entry for this translation issue in the column col
	 */
	public String cellContents(int col)
	{
		String cell = "";
		if (col == CODE) cell = fileName;
		else if (col == TYPE) cell = "Structure";
		else if (col == DESCRIPTION) cell = description();
		else if (col == CAUSEORLOCATION) cell = pathString();
		return  cell;
	}

}
