package com.openMap1.mapper.query;

import com.openMap1.mapper.userConverters.DBConnect;
import com.openMap1.mapper.structures.DBStructure;

import com.openMap1.mapper.core.RDBConnectException;
import com.openMap1.mapper.core.RDBReadException;
import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.core.MapperException;

import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.FileUtil;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

import java.io.IOException;
import java.io.FileOutputStream;

import org.w3c.dom.Element;
import org.w3c.dom.Document;


/**
 * Class to extract data and metadata from a Relational database and express it 
 * as if it were in an XML document, whose XPaths are of the form
 * '/database/TABLENAME/record/COLUMNNAME'
 * 
 * @author robert worden
 *
 */
public class RDBToXMLBase  {
	
	protected Document XMLOutput;
	protected Document XSDOutput;

	protected Element XMLRoot; // root node of the constructed XML document
	public Element XMLRoot() {return XMLRoot;}


  /* filenames for XML and XSD outputs, with full paths but without
  file extensions .XML and .XSD */
  private String fileNameRoot;
  public String fileNameRoot() {return fileNameRoot;}

  private String XSDPrefix = "xs";
  public String XSDPrefix() {return XSDPrefix;}
  public String XSDURI() {return XMLUtil.SCHEMAURI;}

  /*  This class will output XML versions of records in the tables,
  unless you force it not to. */
  private boolean outputXML = true;
  public void banXMLOutput() {outputXML = false;}
  public boolean outputXML() {return outputXML;}

  /* There will be no restriction on the number of records output per table,
  unless you impose one via the steering file. */
  private int maxRecords = -1;
  public void setMaxRecords(int maxR) {maxRecords = maxR;}
  public int maxRecords()  {return maxRecords;}

  /* If this is true, then the columns in any record output for a table in
  response to an SQL query will be in the order of the database schema.
  Otherwise they will be in the order specified in the query
  (or for '*' selection of all column, an arbitrary order) */
  public boolean orderColumnsAsSchema = true;
  
  protected DBStructure dbStructure;
  public DBStructure dbStructure() {return dbStructure;}
  
  //-----------------------------------------------------------------------------------------
  //                              Constructors
  //-----------------------------------------------------------------------------------------

  
  /**
   * @param DBStructure: a database that has been connected to, with metadata extractable
   * @param fileNameRoot filename before '.xml' or '.xsd' for output of data as XML
   * or structure as an XSD
   */
  public RDBToXMLBase(DBStructure dbStructure, String fileNameRoot)
  {
	  this.dbStructure = dbStructure;
	  this.fileNameRoot = fileNameRoot;
  }

  /**
   * @param odbc jdbc connect string for a database that does not require username and password.
   * When odbc name is not known, use null for first argument
   * @param fileNameRoot filename before '.xml' or '.xsd' for output of data as XML
   * or structure as an XSD
   */
  public RDBToXMLBase(String odbc,String fileNameRoot) throws RDBConnectException
  {
	  DBConnect dbc = new DBConnect(odbc,"","",null);
	  if (dbc.con() == null) throw new RDBConnectException
	  	("Failed to connect to database at '" + odbc + "'");
	  dbStructure = new DBStructure(dbc.con());
	  this.fileNameRoot = fileNameRoot;
  }

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
  public RDBToXMLBase(String connectString, String user, String password, String fileNameRoot)
  throws RDBConnectException
  {
	  DBConnect dbc = new DBConnect(connectString,user,password,null);
	  if (dbc.con() == null) throw new RDBConnectException
	  	("Failed to connect to database at '" + connectString + "'");
	  dbStructure = new DBStructure(dbc.con());
	  this.fileNameRoot = fileNameRoot;
  }

  //--------------------------------------------------------------------------------------
  //                           XML File manipulation
  //--------------------------------------------------------------------------------------


  /**
   * Create the RNG DOM, the XSD DOM, and the XML DOM for the whole database
   * If createFiles is true, output them.
   */
  public boolean createDOMs(boolean createFiles) throws MapperException
  {
	  
	  XMLOutput = XMLUtil.makeOutDoc();
	  XSDOutput = XMLUtil.makeOutDoc();
	  
      Hashtable<String,String> tables = new Hashtable<String,String>();
      outputSchemaInformation(createFiles,tables);

	    if (maxRecords > 0) message("Maximum number of records: " + maxRecords);
      if (outputXML())
      {
          makeXMLDOM();
      }
      return true;
  }

  private void outputSchemaInformation(boolean createFiles, Hashtable<String,String> tables) throws MapperException
  {
      writeSQLFile();
      makeXSDDOM(tables,false);
      if (createFiles)
      {
    	  XMLUtil.writeOutput(XSDOutput, (fileNameRoot + ".xsd"), true);
      }
  }


    /* When integer values are stored in Excel, it returns them with a final '.0'. Remove it.
    (I should check that all preceding characters are numbers or '-', but do not yet do so)  */
    protected String removePointZero(String inVal)
    {
        String res = inVal;
        int len = inVal.length();
        if ((len > 2) && (inVal.substring(len-2).equals(".0")))
            {res = inVal.substring(0,len-2);}
        return res;
    }


  //--------------------------------------------------------------------------------------
  //                      Make an XML DOM for the complete database
  //--------------------------------------------------------------------------------------

    /**
     * Make an XML document for the whole database
     */
    public Element makeXMLDOM() throws XMLException, RDBReadException
    {
  	  XMLOutput = XMLUtil.makeOutDoc();
      XMLRoot = XMLUtil.newElement(XMLOutput, "database");
      XMLOutput.appendChild(XMLRoot);
      for (Enumeration<String> en = dbStructure.tableColumns().keys(); en.hasMoreElements();)
      {
          String tName = (String)en.nextElement();
          Element table = XMLUtil.newElement(XMLOutput,tName);
          XMLRoot.appendChild(table);
          doTable(tName,table);
      }
      return XMLRoot;
  }

    /**
     * Make the portion of an XML DOM for a table of the database, given the table header Element
     * @param tName table name
     * @param table Element representing the table, to be extended
     * @throws RDBReadException
     * @throws XMLException
     */
    protected void doTable(String tName, Element table) throws RDBReadException, XMLException
  {
      String qName, sql, column, colVal;
      boolean more;
      Statement st;
      ResultSet r = null;
      Vector<String> colNames;
      Element record, field;
      int i;

      colNames = (Vector<String>)dbStructure.tableColumns().get(tName);
      if (dbStructure.isExcel()){qName = "[" + tName + "$]";}
      else {qName = tName;}
      sql = ("SELECT * FROM " + qName + ";");
      try{
          st = dbStructure.con().createStatement();
          r = st.executeQuery(sql);
      }
      catch (SQLException ex)
          {throw new RDBReadException("retrieving ResultSet from table '"
              + tName + "' " + ex.getMessage() + "; SQL = '" + sql + "'");}
      if (r != null) try{
          more = r.next();
          int records = 0;
          while (more)
          {
              String cols = "";
              if ((records > maxRecords())|(maxRecords() < 0))
              {
                  record = XMLUtil.newElement(XMLOutput,"record");
                  table.appendChild(record);
                  for (i = 0; i < colNames.size(); i++)
                  {
                      column = (String)colNames.elementAt(i);
                      cols = cols + column + " ";
                      colVal = r.getString(column);
                      if (colVal == null) colVal = "";
                      field = XMLUtil.textElement(XMLOutput,column,colVal);
                      record.appendChild(field);
                  }
              }
              records++;
              more = r.next();
          }
      }
      catch (SQLException ex)
          {throw new RDBReadException("reading records from table '" + tName + "' " + ex.getMessage());}
  }

  //--------------------------------------------------------------------------------------
  //           Make an XML Schema definition of the XML structure derived from the database
  //--------------------------------------------------------------------------------------

    /**
     * Make an XML Schema definition of the XML structure derived from the database
     * @param selective: If true, only make XSD definitions for selected tables.
     * @param tables Key = selected table name; value = any String
     */
    public Element XSDDOMRoot(Hashtable<String,String> selectedTables, boolean selective) throws XMLException
  {
      makeXSDDOM(selectedTables, selective);
      return XSDOutput.getDocumentElement();
  }

    /**
     * @param selective: If true, only make XSD definitions for selected tables.
     * @param tables Key = selected table name; value = any String
     */
  protected void makeXSDDOM(Hashtable<String,String> tables, boolean selective) throws XMLException
  {
      Element schema,DBElement, DBTypeElement, allElement;

      schema = XSDElement("schema");
      schema.setAttribute("xmlns:" + XSDPrefix,XMLUtil.SCHEMAURI);
      schema.setAttribute("elementFormDefault","qualified");
      XSDOutput.appendChild(schema);

      DBElement = XSDElement("element");
      DBElement.setAttribute("name","database");
      schema.appendChild(DBElement);

      DBTypeElement = XSDElement("complexType");
      DBElement.appendChild(DBTypeElement);
      allElement = XSDElement("all");
      DBTypeElement.appendChild(allElement);

      for (Enumeration<String> en = dbStructure.tableColumns().keys(); en.hasMoreElements();)
      {
          String tName = en.nextElement();
          String typeName = tName + "Type";

          if ((!selective)|(tables.get(tName) != null))
          {
              Element tableEl = XSDElement("element");
              tableEl.setAttribute("name",tName);
              tableEl.setAttribute("type",typeName);
              allElement.appendChild(tableEl);

              Element tableTypeEl = XSDElement("complexType");
              tableTypeEl.setAttribute("name",typeName);
              doTableXSD(tName,tableTypeEl);
              schema.appendChild(tableTypeEl);
          }
      }
  }

  private void doTableXSD(String tName, Element tableTypeEl) throws XMLException
  {

      Element topSeqEl = XSDElement("sequence");
      tableTypeEl.appendChild(topSeqEl);

      Element recordEl = XSDElement("element");
      recordEl.setAttribute("name","record");
      recordEl.setAttribute("minOccurs","0");
      recordEl.setAttribute("maxOccurs","unbounded");
      topSeqEl.appendChild(recordEl);

      Element rTypeEl = XSDElement("complexType");
      recordEl.appendChild(rTypeEl);

      Element seqEl = XSDElement("sequence");
      rTypeEl.appendChild(seqEl);

      Vector<String> colNames = (Vector<String>)dbStructure.tableColumns().get(tName);
      for (int i = 0; i < colNames.size(); i++)
      {
          String column = colNames.elementAt(i);
          Element field = XSDElement("element");
          field.setAttribute("name",column);
          field.setAttribute("minOccurs","0");
          field.setAttribute("type", XSDPrefix + ":string");
          seqEl.appendChild(field);
      }
  }

  private Element XSDElement(String name) throws XMLException
      {return XMLUtil.NSElement(XSDOutput,XSDPrefix,name,XMLUtil.SCHEMAURI);}


  //--------------------------------------------------------------------------------------
  //                  write a basic skeleton SQL schema for the database
  //--------------------------------------------------------------------------------------

    private FileOutputStream fo;

    /**
     * write a basic skeleton SQL schema for the database
     * @throws MapperException
     */
    public void writeSQLFile() throws MapperException
  {
	  try{
		  // open the output file
	      String location = fileNameRoot + ".sql";
	      fo = new FileOutputStream(location);

	      // fill output sql file
	      writeSQLContent();

	      // close output sql file
	      fo.close();		  
	  }
	  catch (IOException ex) {throw new MapperException(ex.getMessage());}
  }

  // fill output sql file
   private void writeSQLContent()
   {
      nl("CONNECT 'c:dblocation.dmfile.gdb'");
      nl("USER 'MDLUSER' PASSWORD 'pwd';");

      for (Enumeration<String> en = dbStructure.tableColumns().keys(); en.hasMoreElements();)
      {
          String tName = en.nextElement();
          writeSQLForTable(tName);
      }

      nl("");
      nl("EXIT;");
   }

   // write basic SQL for a table, excluding key and type information
   private void writeSQLForTable(String tName)
   {
      nl("");
      nl("CREATE TABLE " + tName);
      nl("(");

      Vector<String> columns = dbStructure.tableColumns().get(tName);
      for (int i = 0; i < columns.size(); i++)
      {
          String colName = (String)columns.elementAt(i);
          nnl("      " + colName + "    VARCHAR(60) ");
          if (i < columns.size() -1) {nl(",");}
          else {nl("");}
      }

      nl(");");
      nl("COMMIT;");
   }


    //-------------------------------------------------------------------------------------
    //                         bits & bobs
    //-------------------------------------------------------------------------------------
    
   private void nl(String s) {FileUtil.nl(s,fo);}
   private void nnl(String s) {FileUtil.nnl(s,fo);}

   protected void message(String s) {System.out.println(s);}


}
