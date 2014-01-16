package com.openMap1.mapper.query;

import java.awt.Color;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XMLUtil;

public class MatchResult {
	
	// three types of match result
	public static int MATCHED_PAIRS = 0;
	public static int UNMATCHED_LEFT = 1;
	public static int UNMATCHED_RIGHT = 2;
	
	private int resultType;
	public int resultType() {return resultType;}
	
	public static String[] matchColumnNames = {"Match","Pair","Score"};

	private String matchQueryText = "";
	public String getMatchQueryText() {return matchQueryText;}
	
	public Vector<Vector<CellContent>> matchResults;
	
	public MatchSource matchSource() {return matchSource;}
	
	private MatchSource matchSource;
	
	/**
	 * make a new MatchResults object with no results yet in  it
	 * @param resultType
	 */
	public MatchResult(int resultType)
	{
		this.resultType = resultType;
		matchResults = new Vector<Vector<CellContent>>();
	}
	
	/**
	 * clear any results previously saved in this MatchResult
	 */
	public void clear()
	{
		matchResults = new Vector<Vector<CellContent>>();		
	}
	
	
	/**
	 * called from class Matcher, after a match invoked from a match window, to save the
	 * results here so they can be shown in the Query Results view.
	 * Adds the new match results to any already held here.
	 * @param matchQueryText
	 * @param matchQueryParser
	 * @param newMatchResults
	 */
	public void saveMatchResult(String matchQueryText,MatchSource matchSource,Vector<Vector<CellContent>> newMatchResults)
	throws MapperException
	{
		if (matchQueryText == null) throw new MapperException("No query text when saving match result");
		this.matchQueryText = matchQueryText;
		this.matchSource = matchSource;
		for (int i = 0; i < newMatchResults.size(); i++) matchResults.add(newMatchResults.get(i));
	}
	
	/**
	 * Populate the XML Document with the match results, in the same format as
	 * is output from the Query Results View
	 * @param doc empty Document to be populated
	 * @param useShortColumnNames if true, omit package name and class name before property name, in column headers
	 * and in tag names for cells
	 */
	public Element resultTree(Document doc, boolean useShortColumnNames) throws MapperException
	{
		Element root = doc.createElement("ViewContents");

		boolean addMatchColumns = true;
		Vector<String> headers = makeTableHeaders(matchSource, addMatchColumns);

		Element headerEl = makeHeader(headers,doc,useShortColumnNames);
		root.appendChild(headerEl);

		Element content = makeContent(headers,doc,useShortColumnNames);
		root.appendChild(content);
		
		return root;
	}

	/**
	 * Populate the XML Document with the match results, in the same format as
	 * is output from the Query Results View, but with rows rearranged for the web service:
	 * - the right-hand record comes first
	 * - if it matches with more than one left-hand record, do no show repeats of the right-hand record
	 * @param doc empty Document to be populated
	 * @param useShortColumnNames if true, omit package name and class name before property name, in column headers
	 * and in tag names for cells
	 */
	public Element webServiceResultTree(Document doc, boolean useShortColumnNames) throws MapperException
	{
		Element root = doc.createElement("ViewContents");

		boolean addMatchColumns = true;
		Vector<String> headers = makeTableHeaders(matchSource, addMatchColumns);

		Element headerEl = makeHeader(headers,doc,useShortColumnNames);
		root.appendChild(headerEl);

		Element content = makeWebServiceContent(headers,doc,useShortColumnNames);
		root.appendChild(content);
		
		return root;
	}
	
	/**
	 * make the header element of a result tree
	 * @param headers
	 * @param doc
	 * @param useShortColumnNames
	 * @return
	 * @throws MapperException
	 */
	private Element makeHeader (Vector<String> headers, Document doc, boolean useShortColumnNames) throws MapperException
	{
		Element headerEl = doc.createElement("TableColumns");
		for (int i = 0; i < headers.size(); i++)
		{
			String columnName = maybeShorten(headers.get(i),useShortColumnNames);
			Element columnEl = XMLUtil.textElement(doc, "column", columnName);
			headerEl.appendChild(columnEl);
		}
		return headerEl;
	}

	
	/**
	 * make the body of a result tree
	 * @param headers
	 * @param doc
	 * @param useShortColumnNames
	 * @return
	 * @throws MapperException
	 */
	private Element makeContent (Vector<String> headers, Document doc, boolean useShortColumnNames) throws MapperException
	{
		Element content = doc.createElement("TableContent");
		for (int rowIndex = 0; rowIndex < matchResults.size(); rowIndex++)
		{
			Vector<CellContent> row = matchResults.get(rowIndex);
			if (row.size() != headers.size()) throw new MapperException("Row " + rowIndex + " has size " + row.size() + " not equal to header size " + headers.size());

			Element rowEl = doc.createElement("row");
			content.appendChild(rowEl);
			for (int cellIndex = 0; cellIndex < row.size(); cellIndex++)
			{
				CellContent cellContent = row.get(cellIndex);
				String tagName = maybeShorten(headers.get(cellIndex),useShortColumnNames);
				Element cellEl = XMLUtil.textElement(doc,tagName , cellContent.getText());
				rowEl.appendChild(cellEl);
			}
		}
		return content;
	}

	/**
	 * make the body of a result tree
	 * @param headers
	 * @param doc
	 * @param useShortColumnNames
	 * @return
	 * @throws MapperException
	 */
	private Element makeWebServiceContent (Vector<String> headers, Document doc, boolean useShortColumnNames) throws MapperException
	{
		Element content = doc.createElement("TableContent");
		for (int rowIndex = 0; rowIndex < matchResults.size(); rowIndex++)
		{
			// alter the row index to rearrange and omit rows
			int alteredRowIndex = alteredIndex(rowIndex);
			
			if (alteredRowIndex > -1) // if row is not omitted....
			{
				Vector<CellContent> row = matchResults.get(alteredRowIndex);
				if (row.size() != headers.size()) throw new MapperException("Row " + rowIndex + " has size " + row.size() + " not equal to header size " + headers.size());

				Element rowEl = doc.createElement("row");
				content.appendChild(rowEl);
				for (int cellIndex = 0; cellIndex < row.size(); cellIndex++)
				{
					CellContent cellContent = row.get(cellIndex);
					String tagName = maybeShorten(headers.get(cellIndex),useShortColumnNames);
					Element cellEl = XMLUtil.textElement(doc,tagName , cellContent.getText());
					rowEl.appendChild(cellEl);
				}				
			}
			
		}
		return content;
	}
	
	/**
	 * for output to the web service, rearrange the rows of the table so the RHS row comes first, and 
	 * omit some rows to omit repeats of the RHS row.
	 */
	private int alteredIndex(int index)
	{
		int alteredIndex = index;

		// make the first row (LHS record, index 0) come second, and the second row (RHS record, index 1) come first
		if (index == 0) alteredIndex = 1;
		if (index == 1) alteredIndex = 0;
		
		// miss out subsequent rows with odd index 3,5,..., as these are repeats of the RHS record
		if ((index > 2) && (index - 2*(index/2) > 0)) alteredIndex = -1;
		
		return alteredIndex;
	}
	
	/**
	 * 
	 * @param fullName
	 * @param doShorten
	 * @return if doShorten is true, only the part after the last '.'; otherwise the full name, 
	 * except excluding package names
	 */
	private String maybeShorten(String fullName, boolean doShorten)
	{
		String result = "";
		StringTokenizer st = new StringTokenizer(fullName,".");

		// if there is a package name before the class name, always remove it
		if (st.countTokens() == 3) st.nextToken();

		// if we are shortening, remove any class name
		if ((st.countTokens() == 2) && doShorten) st.nextToken();

		while (st.hasMoreTokens()) 
		{
			result = result + st.nextToken();
			// '.' after class name and before property name
			if (st.hasMoreTokens()) result = result + ".";
		}
		return result;
	}
	
	/**
	 * Works out table header names in exactly the same way as QueryResultView.MakeTableColumns
	 * @param queryParser
	 * @param addPairColumn
	 * @param addScoreColumn
	 * @return
	 */
	private Vector<String> makeTableHeaders(MatchSource matchSource, boolean addMatchColumns)
	{
		Vector<String> headers = new Vector<String>();
		
		// fixed columns at the front
		headers.add("Codes");
		if (addMatchColumns) for (int i = 0; i < matchColumnNames.length;i++) headers.add(matchColumnNames[i]);

		// columns from the query
		Vector<String[]> headerRow = matchSource.getColumnHeaders();
		for (int c = 0; c < headerRow.size(); c++)
		{
			String[] header = headerRow.get(c);
			headers.add(header[0] + "." + header[1]);
		}
		return headers;
	}

	
	static public Vector<CellContent> addFrontCell(Vector<CellContent> row, String frontCell)
	{
		Vector<CellContent> newRow = new Vector<CellContent>();
		CellContent firstCell = new CellContent(frontCell,Color.black);
		newRow.add(firstCell);
		for (Iterator<CellContent> ir = row.iterator();ir.hasNext();) newRow.add(ir.next());
		return newRow;
	}

}
