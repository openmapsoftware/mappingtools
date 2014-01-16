package com.openMap1.mapper.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;

/**
 * Class to read an Excel workbook from its XML form
 * 
 * @author RPW
 *
 */

public class ExcelReader {
	
	// XML root of the workbook
	private Element root;
	
	// key = sheet name; value = root <table> element of sheet
	private Hashtable<String,Element> sheetRoots;
	
	// key = sheet name; value = Vector of column names, taken from the first row
	private Hashtable<String,Vector<String>> columnNames;
	
	// key = sheet name; value = Integer number of rows, including the header row if there is one
	private Hashtable<String,Integer> sheetRowCount;
	
	//-----------------------------------------------------------------------------------------------
	//                                    constructor and initial checks
	//-----------------------------------------------------------------------------------------------
	
	public ExcelReader(Element root) throws MapperException
	{
		this.root = root;
		initialise();
		
		findSheets();
	}
	
	private void initialise()
	{
		sheetRoots = new Hashtable<String,Element>() ;
		columnNames = new Hashtable<String,Vector<String>>() ;
		sheetRowCount = new Hashtable<String,Integer>() ;		
	}
	
	/**
	 * find information about the worksheets in the workbook (sheet names and column headers) 
	 * and make some checks
	 * @throws MapperException
	 */
	private void findSheets() throws MapperException
	{
		if (!root.getLocalName().equals("Workbook")) throw new MapperException("Root element is not 'Workbook'");

		Vector<Element> sheets = XMLUtil.namedChildElements(root, "Worksheet");
		for (int s = 0; s < sheets.size(); s++)
		{
			Element sheet = sheets.get(s);
			String sheetName = sheet.getAttribute("ss:Name");
			
			Element table = XMLUtil.firstNamedChild(sheet, "Table");
			if (table == null) throw new MapperException("Sheet '" + sheetName + "' has no Table element");
			sheetRoots.put(sheetName, table);
			
			Vector<Element> rows = XMLUtil.namedChildElements(table, "Row");
			sheetRowCount.put(sheetName, new Integer(rows.size()));
			Vector<String> headers = new Vector<String>();
			if (rows.size() > 0)
			{
				Element headerRow = rows.get(0);
				Vector<Element> cells = XMLUtil.namedChildElements(headerRow, "Cell");
				for (int c = 0; c < cells.size();c++)
				{
					String header = "";
					Element data = XMLUtil.firstNamedChild(cells.get(c), "Data");
					if (data != null) header = XMLUtil.getText(data);
					headers.add(header);
				}
			}
			columnNames.put(sheetName, headers);
		}		
	}

	
	//-----------------------------------------------------------------------------------------------
	//                                       read operations
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param sheetName
	 * @return true if the named worksheet exists
	 */
	public boolean hasWorkSheet(String sheetName) {return (sheetRoots.get(sheetName) != null);}
	
	public List<String> getSheetNames()
	{
		Vector<String> names = new Vector<String>();
		for (Enumeration<String> en = sheetRoots.keys(); en.hasMoreElements();) names.add(en.nextElement());
		return names;
	}
	
	/**
	 * 
	 * @param sheetName
	 * @param columnName
	 * @return true if the worksheet has the named column
	 * @throws MapperException
	 */
	public boolean hasColumn(String sheetName, String columnName) throws MapperException
	{
		if (!hasWorkSheet(sheetName)) throw new MapperException("No worksheet '" + sheetName + "'");
		return (GenUtil.inVector(columnName, columnNames.get(sheetName)));
	}
	
	/**
	 * 
	 * @param sheetName
	 * @return number of rows in the worksheet, including the header row
	 * @throws MapperException
	 */
	public int rowCount(String sheetName) throws MapperException
	{
		Integer count = sheetRowCount.get(sheetName);
		if (count == null) throw new MapperException("No worksheet '" + sheetName + "'");
		return count.intValue();
	}
	
	/**
	 * 
	 * @param sheetName
	 * @param rowNumber
	 * @param colName
	 * @param mustHaveColumn
	 * @return the value of the cell, at the row of the sheet , with the column name
	 * @throws MapperException
	 */
	public String getValue(String sheetName, int rowNumber, String colName, boolean mustHaveColumn) throws MapperException
	{
		Element table = sheetRoots.get(sheetName);
		if (table == null) throw new MapperException("No worksheet '" + sheetName + "'");
		
		if ((rowNumber < 0) || (rowNumber > rowCount(sheetName) - 1))
				throw new MapperException("Row number " + rowNumber + " is outside range 0.." + (rowCount(sheetName) - 1) );
		
		if (!hasColumn(sheetName,colName)) 
		{
			if (mustHaveColumn) throw new MapperException("Worksheet '" + sheetName + "' has no column '" + colName);
			// if it is allowed not to find the column, return an empty value
			else return "";
		}
		
		Element row = XMLUtil.namedChildElements(table, "Row").get(rowNumber);
		
		// find the column index c for this column name
		Vector<String> colNames = columnNames.get(sheetName);
		int col = -1;
		for (int c = 0; c < colNames.size();c++) if (colNames.get(c).equals(colName)) col = c;
		
		// loop over all columns for which there is data
		String value = ""; // default 
		int colNumber = -1; // to get zero after one increment

		Vector<Element> cells = XMLUtil.namedChildElements(row, "Cell");
		for (int i = 0; i < cells.size(); i++)
		{
			Element cell = cells.get(i);
			String cellContent = "";
			Element dataEl = XMLUtil.firstNamedChild(cell, "Data");
			if (dataEl != null) cellContent = XMLUtil.getText(dataEl); // some cells have no data
			
			// find the column index 0..N that this cell contains
			String index = cell.getAttribute("ss:Index");
			if (index.length() > 0) colNumber = new Integer(index).intValue() - 1;
			// if no index is given, column is next after previous column
			else colNumber = colNumber + 1;
			
			// take the cell content when column index matches
			if (colNumber == col) value = cellContent;			
		}
		return value;
	}
	


}
