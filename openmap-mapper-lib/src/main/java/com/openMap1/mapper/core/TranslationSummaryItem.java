package com.openMap1.mapper.core;

import java.util.Vector;

/**
 * Data item that gives one row of the Translation Summary View
 * after running a translation test.
 * 
 * @author robert
 *
 */
public class TranslationSummaryItem implements SortableRow{
	
	
	/**
	 * Make a new TranslationSummaryItem , before recording anything in it
	 * @param resultCode
	 */
	public TranslationSummaryItem(String resultCode)
	{
		this.resultCode = resultCode;
		translationIssues = new Vector<TranslationIssue>();
		sourceItemCount = 0;
		resultItemCount = 0;
	}

	/**
	 * column titles for the Translation Summary View
	 */
	static public String[] columnTitle = {
		"Code",
		"Steps",
		"Source items",
		"Percent",
		"Issues"
	};
	
	/**
	 * column widths for the Translation Summary View
	 */
	static public int[] columnWidth = {50,50,80,60,60};
	
	static int CODE = 0;
	static int STEPS = 1;
	static int SOURCE_ITEMS = 2;
	static int PERCENT_FIT = 3;
	static int ISSUES = 4;
	
	
	/**
	 * int constants STRING, NUMBER defined in RowSorter which define how
	 * each column is to be sorted
	 */
	public static int[] sortType() 
	{
		int len = columnTitle.length;
		int[] sType = new int[len];
		sType[CODE] = STRING;
		sType[STEPS] = NUMBER;
		sType[SOURCE_ITEMS] = NUMBER;
		sType[PERCENT_FIT] = NUMBER;
		sType[ISSUES] = NUMBER;
		return sType;
	}

	/**
	 * @param col column index
	 * @return column contents for the Translation Summary View
	 */
	public String cellContents(int col)
	{
		String cell = "";
		if (col == CODE) cell = resultCode();
		if (col == STEPS) cell = new Integer(resultCode().length() -1).toString();
		if (col == SOURCE_ITEMS) cell = new Integer(sourceItemCount).toString();
		if (col == PERCENT_FIT) cell = new Integer(percentFit()).toString();
		if (col == ISSUES) cell = new Integer(translationIssues.size()).toString();
		return cell;
	}
	
	/**
	 * for an object to serve as a sortable row for class RowSorter,
	 * it must be able to present the cell contents as a Vector.
	 * @return
	 */
	public Vector<?> rowVector()
	{
		Vector<String> row = new Vector<String>();
		for (int i = 0; i < columnTitle.length;i++) row.add(cellContents(i));
		return row;
	}



	/**
	 * @return coded name of the translation test result that this summarises,
	 * such as 'AB'
	 */
	public String resultCode() {return resultCode;}
	private String resultCode;
	
	/**
	 * @return number of information items (objects, links, or attribute values)
	 * in the first source of the translation chain giving the result
	 */
	public int sourceItemCount() {return sourceItemCount;}
	public void setSourceItemCount(int sourceItemCount) {this.sourceItemCount = sourceItemCount;}
	private int sourceItemCount;
	
	
	/**
	 * @return number of information items (objects, links, or attribute values)
	 * in the final result of the translation chain, which exactly match the 
	 * corresponding information items in the source
	 */
	public int resultItemCount() {return resultItemCount;}
	public void setResultItemCount(int resultItemCount) {this.resultItemCount = resultItemCount;}
	private int resultItemCount;
	
	/**
	 * @return the matching result item count as a percentage of the source item count
	 */
	public int percentFit()
	{
		if (sourceItemCount == 0) return 0;
		double numerator = 100*resultItemCount;
		double denominator = sourceItemCount;
		return (new Double(numerator/denominator).intValue());
	}
	
	/**
	 * @return the distinct translation issues detected
	 */
	public Vector<TranslationIssue> translationIssues() {return translationIssues;}
	private Vector<TranslationIssue> translationIssues;
	
	/** record a validation issue  */
	public void addValidationIssue(ValidationIssue vi) {translationIssues.add(vi);}
	
	/** record a compilation issue  */
	public void addCompilationIssue(CompilationIssue ci) {translationIssues.add(ci);}
	
	/** record a runtime issue  */
	public void addRunIssue(RunIssue ri) {translationIssues.add(ri);}
	
	/** record a schema mismatch in a result */
	public void addSchemaMismatch(SchemaMismatch sm) {translationIssues.add(sm);}
	
	/** record a structure mismatch in a result */
	public void addStructureMismatch(StructureMismatch sm) {translationIssues.add(sm);}
	
	/** record a semantic mismatch in a result */
	public void addSemanticMismatch(SemanticMismatch sm) {translationIssues.add(sm);}


}
