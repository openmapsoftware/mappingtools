package com.openMap1.mapper.query;

import com.openMap1.mapper.core.RDBConnectException;
import com.openMap1.mapper.core.RDBReadException;
import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.core.MapperException;

import com.openMap1.mapper.structures.DBStructure;
import com.openMap1.mapper.util.XMLUtil;

import org.w3c.dom.Element;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Class to extract data and metadata from a Relational database and express it 
 * as if it were in an XML document, whose XPaths are of the form
 * '/database/TABLENAME/record/COLUMNNAME'
 * and to do so selectively, with the selection of records determined by a 
 * set of SQL queries
 *
 */
public class RDBReader extends RDBToXMLBase
{
	
	// SQL results from all queries (currently one query) created from an object model query
	private Vector<ResultSet> sqlResults;
	
	/* to avoid storing duplicate records in the XML subtree for any table;
    key = table name, value = a Hashtable<String,String> of record keys ( = string-concatenated field values) */
    private Hashtable<String, Hashtable<String,String>> allRecordsStored;
    
    /**
     * @return  maximum number of SQL result rows to be included in the XML DOM returned by one call of DOMFromSQL
     */
    public int getPartitionSize() {return partitionSize;}
    public void setPartitionSize(int partitionSize) {this.partitionSize = partitionSize;}
    private int partitionSize = 200;
    
    // which one of the Vector of SQL result sets is currently being returned in the DOM
    private int currentSQLResultSet;
    
    // total number of SQL result rows from all SQL queries that have been put into DOMs so far
    private int SQLRowsConverted;
    
    /* when converting SQL rows into XML elements, stop whenever the number of rows converted 
     * is a whole multiple of the partition size. */
    private boolean partitionBoundary() {return ((SQLRowsConverted - partitionSize*(SQLRowsConverted/partitionSize)) == 0);}
    
    /**
     * @return true if all SQL result rows have been converted to XML, 
     * so that all result sets have been exhausted
     */
    public boolean convertedAllRows() {return (currentSQLResultSet > sqlResults.size() - 1);}


	  
	  //-----------------------------------------------------------------------------------------
	  //                              Constructors
	  //-----------------------------------------------------------------------------------------

	
	/**
	   * @param DBStructure: a database that has been connected to, with metadata extractable
	   * @param fileNameRoot filename before '.xml' or '.xsd' for output of data as XML
	   * or structure as an XSD
	   */	
	 public RDBReader(DBStructure dbStructure, String fileNameRoot)
	  	{super(dbStructure,fileNameRoot);}


	  /**
	   * @param odbc jdbc connect string for a database that does not require username and password.
	   * When odbc name is not known, use null for first argument
	   * @param fileNameRoot filename before '.xml' or '.xsd' for output of data as XML
	   * or structure as an XSD
	   */

    public RDBReader(String odbc,String fn) throws RDBConnectException {super(odbc,fn);}

    /**
     * Constructor for a database that requires user name and password
     *
     * @param connectString  - the jdbc/odbc connect string for the DBMS
     * @param user - user name required by the DBMS
     * @param password - password required by the database
     * @param fileNameRoot filename before '.xml' or '.xsd' for output of data as XML
     * or structure as an XSD
     *
     * @exception RDBConnectException - if the database connection does not succeed
     * @exception RDBConnectException - if the MDL does not match the database
     */
    public RDBReader(String connectString, String user, String password, String fileNameRoot)
    throws RDBConnectException
    {super(connectString, user, password, fileNameRoot);}

    //--------------------------------------------------------------------------------------
    //   Make an XML DOM from a set of SQL queries, all derived from the same object query
    //--------------------------------------------------------------------------------------
    
    /**
     * to be called before returning a sequence of DOMs from 
     * @param queries  - - to make a local copy of all the SQL result sets
     */
    public void initiateQuery(Vector<SQLQuery> queries) throws MapperException
    {
    	sqlResults = new Vector<ResultSet>();
    	
        // retrieve the data and store the ResultSets for subsequent calls to DOMFromSQL 
        for (int i = 0; i < queries.size(); i++)
        {
            SQLQuery sql = queries.elementAt(i);
            String queryText = sql.stringForm();
            // message("Query text: " + queryText);
            try{
                Statement st = dbStructure.con().createStatement();
                ResultSet r = st.executeQuery(queryText);
                sqlResults.add(r);
            }
            catch (SQLException ex)
                {throw new RDBReadException("retrieving records from query '" + sql.stringForm() + "' " + ex.getMessage());}
        }
        
        // initialise counters to track partitioning of SQL results
        currentSQLResultSet = 0;
        SQLRowsConverted = 0;
    	
    }
        
    /**
     * set up the XML DOM with all table elements required to store answers to a set of queries
     * @param queries
     * @throws MapperException
     */
    private void setUpTableElements(Vector<SQLQuery> queries) throws MapperException
    {
    	XMLOutput = XMLUtil.makeOutDoc();

        // add the top Element
        XMLRoot = XMLUtil.newElement(XMLOutput,"database");
        XMLOutput.appendChild(XMLRoot);


        for (int i = 0; i < queries.size(); i++)
        {
            SQLQuery sql = queries.elementAt(i);
            // ensure there are table elements for all tables in the query
            for (Enumeration<String> it = sql.allTables().keys(); it.hasMoreElements();)
            {
                String table = it.nextElement();
                /* add an element for the table if there is none already;
                and create an empty Hashtable<String,String> to keep track of records stored, to avoid duplicates */
                if (XMLUtil.namedChildElements(XMLRoot,table).size() == 0)
                {
                    Element elTable = XMLUtil.newElement(XMLOutput,table);
                    XMLRoot.appendChild(elTable);
                }
            }
        }    	
    }

    /**
     * build up a DOM of relational data from results of running several queries,
     * in blocks of up to partitionSize rows.
     * This is called successively for each block. 
     */
    public Element DOMFromSQL(Vector<SQLQuery> queries) throws MapperException
    {
    	
    	// System.out.println("Getting rows from " + SQLRowsConverted);

    	// need to set up table elements for each DOM
    	setUpTableElements(queries);
    	
    	// reset the structure to avoid duplicate record rows in any table
    	allRecordsStored = new Hashtable<String, Hashtable<String,String>>();

    	/* remember which result set you were working through; the ResultSet remembers how far you have got through it. */
    	int firstResultSet = currentSQLResultSet;
    	boolean doNextResultSet = true;
        for (int i = firstResultSet; i < queries.size(); i++) if (doNextResultSet)
        {
        	try
            {
                ResultSet r = sqlResults.get(i);
                doNextResultSet = addToDOM(queries.get(i),r);
                if (doNextResultSet) currentSQLResultSet++;
            }
        	catch (SQLException ex) {throw new MapperException("SQL Exception reading result row: " + ex.getMessage());}
        }
        return XMLRoot;
    }

    /**
     * Build up a DOM from the ResultSet of one query, in blocks of up to
     * partitionSize result rows 
     */
    private boolean addToDOM(SQLQuery sql, ResultSet r)
    throws MapperException, SQLException
    {
        boolean partitionBoundary = false;
        boolean more = r.next();

        // iterate over rows in the ResultSet, until they are exhausted or you hit a partition boundary
        while ((more) && (!partitionBoundary)) 
        {
        	Vector<ResultCell> resRow = makeResultRow(sql, r);
        	String row = writableRow(resRow);
            
            // add this row to the DOM - checking non-SQL tests and not making duplicates in tables
        	boolean addable = sql.satisfies(resRow);
        	message("Row addable? " + addable + ": " + row);
            if (addable) addRowToDOM(sql, resRow);

            // increment the number of SQL rows converted, and re-test whether this is a partition boundary
            SQLRowsConverted++;
            partitionBoundary = partitionBoundary();
            // only step on through the resultSet if you have not hit a partition boundary
            if (!partitionBoundary) more = r.next();
        }
        message("Result rows from SQL:" + SQLRowsConverted);
        
      // if you did not stop on a partition boundary, you completed this result set  
      return !partitionBoundary;
    }
    
    /**
     * 
     * @param sql
     * @param r
     * @return
     * @throws SQLException
     */
    private Vector<ResultCell> makeResultRow(SQLQuery sql, ResultSet r) throws SQLException
    {
        /* Convert one row of the ResultSet into a Vector of resultCells  */
    	// message("row of result set");
        Vector<ResultCell> resRow  = new Vector<ResultCell>();
        int colIndex = 1;
        /* assume the order of this enumeration (the order of columns in the query, or the resultset)
         * is reproducible, between making the String form of the query (which used the same enumeration), 
         * and getting results from the result set by column index below. */
        for (Enumeration<String> en = sql.outputColumns().keys();en.hasMoreElements();)
        {
            String key = en.nextElement(); // key is just 'table.column'
            String[] tabCol = (String[])sql.outputColumns().get(key);
            String colVal = r.getString(colIndex);
            if (dbStructure.isExcel()&&(colVal != null)) colVal = removePointZero(colVal);
            colIndex++;
            resRow.addElement(new ResultCell(tabCol[0],tabCol[1],colVal));
        }
        return resRow;
    }

    /* Add to the DOM tree for one row of a ResultSet - expressed as a vector of result cells;
    only add it if it satisfies all the conditions of the SQL query - even those that
    could not be passed to the SQL.  */
    private boolean addRowToDOM(SQLQuery sql, Vector<ResultCell> resRow)
    throws MapperException
    {
        boolean success = false;
        // test all conditions, in case some of them could not be passed to the SQL
        if (sql.satisfies(resRow))
        {
        	success = true;
            // deal separately with each table represented in the row
            for (Enumeration<String> it = sql.allTables().keys(); it.hasMoreElements();)
            {
                String table = it.nextElement();
                Vector<ResultCell> tableRow = resTableRow(resRow,table);
                if (tableRow.size() > 0) // test not necessary?
                {
                	boolean storedInTable = storeIfNew(table,tableRow);
                	message("Stored in table " + table + "? " + storedInTable);
                }
            }
        }
        return success;
    }

    // find the subset of a result row associated with a particular table
    private Vector<ResultCell> resTableRow(Vector<ResultCell> resRow, String table)
    {
        Vector<ResultCell> result = new Vector<ResultCell>();
        for (int i = 0; i < resRow.size(); i++)
        {
            ResultCell rc = resRow.elementAt(i);
            if (rc.tableName.equals(table)) result.addElement(rc);
        }
        return result;
    }

    /* Store the portion of a result row for a given table under the DOM node
    for that table, if an identical row portion has not been stored there already. */
    private boolean storeIfNew(String table, Vector<ResultCell> tableRow) throws RDBReadException
    {
        Hashtable<String,String> tableKeys = allRecordsStored.get(table);
        if (tableKeys == null) tableKeys = new Hashtable<String,String>();
    	// writeRow(tableRow);

    	boolean stored = false;
        Vector<Element> tabElements = XMLUtil.namedChildElements(XMLRoot,table); // there should be just one of these
        if (tabElements.size() == 0)
        	{throw new RDBReadException("RDB extract error: no element for table '" + table + "'");}
        else if (tabElements.size() > 1)
        	{throw new RDBReadException("RDB extract error: " + tabElements.size() + " elements for table '" + table + "'");}
        else if (tabElements.size() == 1) try
        {
            Element tabEl = tabElements.elementAt(0);
            String key = valueKey(tableRow);
            // message("key: " + key);
            if (tableKeys.get(key) == null)
            {
            	stored = true;
                tableKeys.put(key,"1"); // so this record will not be duplicated later
                allRecordsStored.put(table, tableKeys);
                Element record = XMLUtil.newElement(XMLOutput,"record");
                tabEl.appendChild(record);

                // either order the column nodes under the <record> node as in the relational schema...
                if (orderColumnsAsSchema)
                {
                    Vector<String> tableCols = dbStructure.tableColumns().get(table);
                    for (int col = 0; col < tableCols.size(); col++)
                    {
                        String column = (String)tableCols.elementAt(col);
                        ResultCell rc = findCellForColumn(tableRow,column);
                        if (rc != null) writeResultCell(rc,record);
                    }
                }

                // ... or order the column nodes under the <record> node as in the SQL query
                else for (int i = 0; i < tableRow.size(); i++)
                {
                    ResultCell rc = tableRow.elementAt(i);
                    writeResultCell(rc,record);
                }
            }
        }
        catch (Exception e)
            {throw new RDBReadException("Unidentified exception writing data from database to XML: " + e.getMessage());}
        // message("Stored: " + stored);
        return stored;
    }

    // append the text value of a column  under a <record> node
    private void writeResultCell(ResultCell rc,Element record) throws XMLException
    {
        Element field = XMLUtil.textElement(XMLOutput,rc.columnName,rc.value);
        if (field != null) {record.appendChild(field);}
        else {throw new XMLException("Cannot write value '" + rc.value + "' in element '" + rc.columnName + "'");}
    }

    /* find the result cell for a named column, if it exists;
    otherwise return null. */
    private ResultCell findCellForColumn(Vector<ResultCell> tableRow, String column)
    {
        ResultCell rc = null;
        for (int i = 0; i < tableRow.size(); i++)
        {
            ResultCell rt = tableRow.elementAt(i);
            if (column.equals(rt.columnName)) rc = rt;
        }
        return rc;
    }


    /* construct a key (which is almost guaranteed to be unique, but is not quite)
    from the field values in a table subset of a result row. */
    private String valueKey(Vector<ResultCell> tableRow)
    {
        String res = "";
        for (int i = 0; i < tableRow.size(); i++)
        {
            ResultCell rc = tableRow.elementAt(i);
            res = res + rc.value;
            // cell values would need to contain this obscure string to fake uniqueness
            res = res + "_|£";
        }
        return res;
    }
    

    /**
     * When integer values are stored in Excel, it returns them with a final '.0'. Remove it.
     * (I should check that all preceding characters are numbers or '-', but do not yet do so)
     */
    protected String removePointZero(String inVal)
    {
        String res = inVal;
        int len = inVal.length();
        if ((len > 2) && (inVal.substring(len-2).equals(".0")))
            {res = inVal.substring(0,len-2);}
        return res;
    }


    //--------------------------------------------------------------------------------------
    //                      Make an XML DOM for parts of the database
    //--------------------------------------------------------------------------------------

   

    public void outputSchemaInformation(boolean createFiles, Hashtable<String, String> tables) throws MapperException
    {
        writeSQLFile();
    	XSDOutput = XMLUtil.makeOutDoc();
        makeXSDDOM(tables,false);
        if (createFiles)
        {
            XMLUtil.writeOutput(XSDOutput, fileNameRoot() + ".xsd", true);
        }
    }
  

    //--------------------------------------------------------------------------------------
    //                               Diagnostics
    //--------------------------------------------------------------------------------------
    
    private String writableRow(Vector<ResultCell> tableRow)
    {
    	String row = "";
    	for (int i = 0; i < tableRow.size();i++)
    	{
    		ResultCell cell = tableRow.get(i);
    		if (i == 0) row = row + cell.tableName + " > ";
    		row = row + cell.columnName + ":" + cell.value + " ";
    	}
    	return row;
    }
    



}
