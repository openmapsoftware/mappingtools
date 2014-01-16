package com.openMap1.mapper.converters;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * Wrapper class to convert between a csv file and the XML normally
 * extracted from relational databases.
 * XPaths in this XML are /database/<TABLE_NAME>/record/<COLUMN_NAME>.
 * 
 * For a csv file representing a single table, the first row of the file defines the column
 * names, and by convention the table is called 'TABLE'.
 * 
 * For a csv representing several tables, the contents of the first row are '$TABLE <TABLE_NAME>',
 * the second row defines columns in this table,
 * and any subsequent rows starting with '$TABLE' define other tables in the same way.
 * 
 * if the csv file has more than segmentSize lines, then it is converted to several different DOMs in
 * units of segmentSize. This option is only available if the csv file represents one table only,
 * without an initial  row '$TABLE <TABLE_NAME>'.
 * 
 * @author Robert
 *
 */

public class CSV_Wrapper extends AbstractMapperWrapper implements MapperWrapper, StructureDefinition {
	
	
	/**
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare spare argument, set to name of topElementDef
	 */
	public CSV_Wrapper(MappedStructure ms, Object spare) throws MapperException
	{
		super(ms,spare);
		
		// for the StructureDefinition interface; define the root node of a csv structure as a database structure
		databaseElDef = MapperFactory.eINSTANCE.createElementDef();
		setNameAndType("database","database_type",databaseElDef);
		
	}

	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	public int transformType() {return AbstractMapperWrapper.TEXT_TYPE;}
	
	/**
	 * 
	 * @return the file extension of the outer document, with initial '*.'
	 */
	public String fileExtension() {return "*.csv";}

	
	//----------------------------------------------------------------------------------------------------
	//              reading the csv to an xml instance - possibly in segments of segmentSize lines
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * @return the maximum number of csv lines to be converted to an XML DOM in one call of transformIn
	 */
	public int getSegmentSize() {return segmentSize;}
	public void setSegmentSize(int segmentSize) {this.segmentSize = segmentSize;}
	private int segmentSize = 2000;
	
	/**
	 * true only if there has been a previous call of transformIn, and because of the segment size restriction 
	 * there are still some more csv lines to convert to a DOM
	 */
	private boolean started = false;
	
	/**
	 * next line of the csv file to be converted to XML. Initial value = after one table header line has been read
	 */
	private int currentLine = 1;
	
	/**
	 * lines of the csv file, as strings
	 */
	private Vector<String> lines = new Vector<String>();
	
	private boolean needsSegmentation() {return (lines.size() > segmentSize  - 1);}
	
	/**
	 * @return true if transformIn should be called again to convert more csv rows to a DOM
	 */
	public boolean hasMoreRows() {return (currentLine < lines.size());}
	
	/**
	 * @param lineNo
	 * @return  true if the line number is on a boundary for segmentation - a whole multiple of the segment size
	 */
	private boolean segmentBoundary(int lineNo)
	{
		return ((lineNo - segmentSize*(lineNo/segmentSize)) == 0);
	}
	
	public void initialise()
	{
		started = false;
		currentLine = 1;
	}

	/**
	 * @param csvFileObj input stream of the csv file
	 * @param rootName name of the root element; always 'database'
	 * @throws MapperException if, for instance, the actual structure 
	 * does not match the expected structure
	 */
	public Document transformIn(Object csvFileObj)
	throws MapperException
	{
		Document doc = XMLUtil.makeOutDoc();
		Element root = XMLUtil.newElement(doc, "database");
		doc.appendChild(root);

		// to be done only on the first call
		if (!started)
		{
			if (!(csvFileObj instanceof InputStream))
				throw new MapperException("Input for making xml instance is not an InputStream");
			InputStream csvFile = (InputStream)csvFileObj;
			
			lines = FileUtil.getLines(csvFile);
			if (lines.size() < 3) throw new MapperException("Cannot convert a csv file with only " + lines.size() + " lines");
			
			if ((needsSegmentation()) && (lines.get(0).startsWith("$TABLE")))
				throw new MapperException("CSV files longer than " + segmentSize + " lines cannot contain more than one table");
			
			currentLine = 1; // in case the whole file has been read before
		}

		// csv file is long enough to need segmentation and only has one table, called 'TABLE'
		if (needsSegmentation())
		{
			// make or remake the table element for that table
			Element tableEl = XMLUtil.newElement(doc, "TABLE");
			root.appendChild(tableEl);
			// first row defines the columns
			Vector<String> columns = getColumns(lines.get(0), "TABLE");
			
			// read rows into that table from the current line, until you hit a segment boundary or the end of the file
			boolean segmentBoundary = false;
			while ((hasMoreRows()) && (!segmentBoundary))
			{
				String dataLine = lines.get(currentLine);
				// end of this table and start of another table - not expected for large csv files
				if (dataLine.startsWith("$TABLE")) 
					throw new MapperException("New table is not allowed in large csv file at line " + currentLine);
				addRow(doc, tableEl, columns, dataLine);
				// check for a segment boundary, then move on so as not to get stuck at the next call
				segmentBoundary = segmentBoundary(currentLine);
				currentLine++;
			}
			
			/* if this is not the end of the file, prepare to read the next segment. currentLine persists to the next call.
			 * If this is the end of the file, be ready to read the whole file again */
			started = hasMoreRows();
		}

		// csv file does not need segmentation and may contain several tables
		else if (!needsSegmentation())
		{
			readLines(doc, root);	
			currentLine = lines.size(); // so that hasMoreRows() will return false now
		}

		return doc;
	}
	
	
	/**
	 * read all the lines of the csv file, and convert them to XML
	 * @param doc
	 * @param database
	 * @param lines
	 * @throws MapperException
	 */
	private void readLines(Document doc, Element database) throws MapperException
	{
		int currentLine = 0;
		// iteration to process tables
		while (currentLine > -1) currentLine = processNextTable(currentLine, doc, database);
	}
	
	/**
	 * read all lines of the csv file for one table, and convert them to XML
	 * @param line first line of the lines for this table
	 * @param doc
	 * @param database top 'database' element
	 * @param lines
	 * @return the line for the start of the next table, or -1 if there is none
	 * @throws MapperException
	 */
	private int processNextTable(int line, Document doc, Element database)  throws MapperException
	{
		int size = lines.size();
		int newLine = line;

		// get the table name and create an element for it
		String firstLine = lines.get(newLine);
		String tableName = "TABLE";
		if (firstLine.startsWith("$TABLE")) 
		{
			tableName = getTableName(firstLine);
			newLine++;
		}
		Element tableEl = XMLUtil.newElement(doc, tableName);
		database.appendChild(tableEl);
		
		// get the column names
		if (newLine > size -1) throw new MapperException("There is no line to define the columns of table " + tableName);
		Vector<String> columns = getColumns(lines.get(newLine), tableName);
		
		// get the rows
		newLine++;
		while (size > newLine)
		{
			String dataLine = lines.get(newLine);
			// end of this table and start of another table
			if (dataLine.startsWith("$TABLE")) return newLine;
			addRow(doc, tableEl, columns, dataLine);
			newLine++;
		}
		
		// end of the csv file
		return -1;		
	}
	
	/**
	 * get a table name from a line defining it
	 * @param line
	 * @return
	 * @throws MapperException
	 */
	private String getTableName(String line)  throws MapperException
	{
		// strip off trailing commas from empty cells of spreadsheet
		StringTokenizer cols = new StringTokenizer(line,",");
		String firstCol = cols.nextToken();
		
		// strip off initial '$TABLE' 
		StringTokenizer st = new StringTokenizer(firstCol," ");
		if (st.countTokens() != 2) throw new MapperException("Line '" + line + "' does not define a single table name");
		st.nextToken();
		
		// return the table name
		return st.nextToken();
	}

	/**
	 * get a Vector of column names from a line defining it
	 * @param columnLine
	 * @param tableName
	 * @return
	 * @throws MapperException
	 */
	private Vector<String> getColumns(String columnLine, String tableName) throws MapperException
	{
		Vector<String>  cols = new Vector<String>();
		StringTokenizer st = new StringTokenizer(columnLine,",");
		if (st.countTokens() == 0) throw new MapperException("Empty line defining the columns of table " + tableName);
		while (st.hasMoreTokens()) 
		{
			String colName = st.nextToken();
			// filter for miscellaneous characters not allowed in tag names
			StringTokenizer ct = new StringTokenizer(colName," ;.",true);
			if (ct.countTokens() > 1) throw new MapperException("Disallowed XML tag name '" + colName + "'");

			// remove any enclosing double quotes in column headers
			if ((colName.startsWith("\"")) && (colName.endsWith("\""))) colName = colName.substring(1,colName.length() - 1);
			cols.add(colName);
		}
		return cols;
	}

	/**
	 * add XML for a new row to a table, if the csv line can be read
	 * @param doc
	 * @param tableEl
	 * @param columns
	 * @param dataLine
	 * @return true if the csv line could be read
	 * @throws MapperException if the line had too many cells
	 */
	private boolean addRow(Document doc, Element tableEl, Vector<String> columns, String dataLine) throws MapperException
	{
		boolean success = true;		
		Vector<String> cells = new Vector<String>();
		
		/* try to read the csv line; if there is any failure, just miss the line out,
		 * recording it to the console */
		try {cells = GenUtil.parseCSVLine(dataLine);}
		catch (Exception ex) 
			{success = false;System.out.println("Exception: " + ex.getMessage() + " reading csv line " + dataLine);}
		if (cells.size() > columns.size()) throw new MapperException("row size " + cells.size()
				+ "is greater than the number of columns " + columns.size() + " in row '" + dataLine + "'");
		
		// add lines successfully read, to the XML DOM
		if (success)
		{
			Element record = XMLUtil.newElement(doc, "record");
			for (int i = 0; i < cells.size(); i++)
			{
				String value = cells.get(i);
				// do not create XML elements for empty field values
				if ((value != null) && (!value.equals("")))
				{
					Element field = XMLUtil.textElement(doc, columns.get(i), value);
					record.appendChild(field);							
				}
			}
			tableEl.appendChild(record);			
		}

		return success;
	}

	//----------------------------------------------------------------------------------------------------
	//                                    writing a csv instance - not yet implemented
	//----------------------------------------------------------------------------------------------------

	public String[] transformOut(Element csvRoot)
	throws MapperException
	{
		int i = 1;
		if (6==(7-i)) throw new MapperException("CSV wrapper class out-transform not yet implemented");
		String[] result = null;
		return result;
	}

	
	//----------------------------------------------------------------------------------------------------
	//                    interface StructureDefinition
	//----------------------------------------------------------------------------------------------------
	
	private ElementDef databaseElDef = null;
	
	private Hashtable<String,ElementDef> elementsByName = new Hashtable<String,ElementDef>();
	
	private Hashtable<String,ElementDef> elementsByType = new Hashtable<String,ElementDef>();
	
	/**
	 * give an ElementDef a name and a type, and populate the lookup tables
	 * @param name
	 * @param type
	 * @param elDef
	 */
	private void setNameAndType(String name, String type, ElementDef elDef)
	{
		elDef.setName(name);
		elDef.setType(type);
		elDef.setExpanded(true);
		elementsByName.put(name, elDef);
		elementsByType.put(type, elDef);
	}

	/**
	 * find the Element and Attribute structure of some named top element (which may have a named
	 * complex type, or a locally defined anonymous type), stopping at the
	 * next complex type definitions it refers to
	 * @param String name the name of the element
	 * @return Element the EObject subtree (Element and Attribute EObjects) defined by the name
	 */
	public ElementDef nameStructure(String name) throws MapperException
	{
		return elementsByName.get(name);
	}

	/**
	 * find the Element and Attribute structure of some complex type, stopping at the
	 * next complex type definitions it refers to
	 * @param type the name of the complex type
	 * @return the EObject subtree (Element and Attribute EObjects) defined by the type
	 */
	public ElementDef typeStructure(String type) throws MapperException
	{
		return elementsByType.get(type);
	}
	
	/**
	 * 
	 * @return an array of the top-level complex types defined in the structure definition - 
	 * any of which can be the type of a mapping set
	 */
	public String[] topComplexTypes()
	{
		String[] types = {"","database_type"};
		return types;
	}
	
	/**
	 * 
	 * @return an array of the top-level element names defined in the structure definition - 
	 * any of which can be the type of a mapping set
	 */
	public String[] topElementNames()
	{
		String[] names = {"","database"};
		return names;
	}

	/**
	 * @return the set of namespaces defined for the structure
	 */
	public NamespaceSet NSSet()
	{
		return new NamespaceSet();
	}
	
	
	/**
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return true if this property value supplier supplies values for the 
	 * model class and property
	 */
	public boolean suppliesPropertyValues(String modelClassName, String modelPropertyName)
	{
		if ((modelClassName.equals("MappedStructure")) && 
				(modelPropertyName.equals("Top Element Type"))) return true;
		if ((modelClassName.equals("MappedStructure")) && 
				(modelPropertyName.equals("Top Element Name"))) return true;
		return false;
	}
	
	/**
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return the values supplied by this supplier for the model class and property
	 */
	public String[] propertyValues(String modelClassName, String modelPropertyName)
	{
		String[] vals = {};
		if ((modelClassName.equals("MappedStructure")) && 
				(modelPropertyName.equals("Top Element Type"))) return topComplexTypes();
		if ((modelClassName.equals("MappedStructure")) && 
				(modelPropertyName.equals("Top Element Name"))) return topElementNames();			
		return vals;
	}

	
	/**
	 * needs to be called to find out the structure
	 * @param csvFileObj
	 * @throws MapperException
	 */
	public void getStructure(Object csvFileObj)
	throws MapperException
	{
		if (!(csvFileObj instanceof InputStream))
			throw new MapperException("Input for getting structure definition is not an InputStream");
		InputStream csvFile = (InputStream)csvFileObj;

		
		Vector<String> lines = FileUtil.getLines(csvFile);
		if (lines.size() < 3) throw new MapperException("Cannot get structure defnition from a csv file with only " + lines.size() + " lines");

		int currentLine = 0;
		// iteration to process tables
		while (currentLine > -1) currentLine = processNextTableLines(currentLine, lines);

	}
	
	/**
	 * 
	 * @param line
	 * @param lines
	 * @return
	 * @throws MapperException
	 */
	private int processNextTableLines(int line, Vector<String> lines)  throws MapperException
	{
		int newLine = line;

		// get the table name 
		String firstLine = lines.get(newLine);
		String tableName = "TABLE";
		if (firstLine.startsWith("$TABLE")) 
		{
			tableName = getTableName(firstLine);
			newLine++;  // ready to read column names for the table
		}

		// and create an elementDef for the table, with a child 'record' ElementDef
		ElementDef tableDef = MapperFactory.eINSTANCE.createElementDef();
		setNameAndType(tableName, tableName + "_Type",tableDef);
		tableDef.setMinMultiplicity(MinMult.ONE);
		tableDef.setMaxMultiplicity(MaxMult.ONE);
		databaseElDef.getChildElements().add(tableDef);

		ElementDef recordDef = MapperFactory.eINSTANCE.createElementDef();
		setNameAndType("record", "record_" + tableName + "_Type",recordDef);
		recordDef.setMinMultiplicity(MinMult.ZERO);
		recordDef.setMaxMultiplicity(MaxMult.UNBOUNDED);
		tableDef.getChildElements().add(recordDef);

		// get the column names 
		if (newLine > lines.size() -1) throw new MapperException("There is no line to define the columns of table " + tableName);
		Vector<String> columns = getColumns(lines.get(newLine), tableName);
		newLine++; // move on to a row containing data content of this table, or a new table
		
		// add ElementDefs for the columns
		for (int col = 0; col < columns.size();col++)
		{
			ElementDef cellDef = MapperFactory.eINSTANCE.createElementDef();
			String colName = columns.get(col);
			setNameAndType(colName, colName + "_Type",cellDef);
			cellDef.setMinMultiplicity(MinMult.ZERO);
			cellDef.setMaxMultiplicity(MaxMult.ONE);
			recordDef.getChildElements().add(cellDef);
		}
		
		// read through data rows to the next table-defining rows
		boolean tableFound = false;
		while ((!tableFound) && (newLine < lines.size()))
		{
			String nextLine = lines.get(newLine);
			tableFound = (nextLine.startsWith("$TABLE"));
			newLine++;
		}
		if (!tableFound) newLine = -1;
		
		return newLine;
	}
		


}
