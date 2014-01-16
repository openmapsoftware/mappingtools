package com.openMap1.mapper.structures;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.RDBConnectException;
import com.openMap1.mapper.core.Xpth;

import com.openMap1.mapper.core.PropertyValueSupplier;

import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;

public class DBStructure implements StructureDefinition,PropertyValueSupplier{
	
    public Connection con() {return con;}
	private Connection con;

	private DatabaseMetaData meta;
    private String DBProduct; // name of DBMS, eg Excel

    public boolean isExcel() {return isExcel;}
    private boolean isExcel = false; // true if this is an Excel 'database'

    public boolean isAccess() {return isAccess;}
    private boolean isAccess = false; // true if this is an Access database

    /* Set this string to get table metadata only from one schema */
    private String userSchemaName = null;
    public void  setUserSchemaName(String schema) {userSchemaName = schema;}

    /* Set this string to get table metadata only from one catalog */
    private String catalogName = null;
    public void  setCatalogName(String catalog) {catalogName = catalog;}

    /* On getting odbc metadata, will only write out all catalog and schema names if
    you ask it to. */
    private boolean writeCatalogsAndSchemas = false;
    public void forceWriteCatalogsAndSchemas() {writeCatalogsAndSchemas = true;}

    /** key = table name (with no '$' for Excel) IN UPPER CASE; 
     * value = Vector of column names IN UPPER CASE. */
    public Hashtable<String, Vector<String>> tableColumns() {return tableColumns;}
    private Hashtable<String, Vector<String>> tableColumns = new Hashtable<String, Vector<String>>();

    /** key = table name (with no '$' for Excel) IN UPPER CASE; 
     * value = Vector of column type names (UPPER CASE).
     * eg VARCHAR, NUMBER, INTEGER */
    public Hashtable<String, Vector<String>> tableColumnTypes() {return tableColumnTypes;}
    private Hashtable<String, Vector<String>> tableColumnTypes = new Hashtable<String, Vector<String>>();

    /** key = table name (with no '$' for Excel); 
     * value = Vector of primary key column names. */
    public Hashtable<String, Vector<String>> tableKeyColumns() {return tableKeyColumns;}
    private Hashtable<String, Vector<String>> tableKeyColumns = new Hashtable<String, Vector<String>>();


    public boolean foundPrimaryKeys() {return foundPrimaryKeys;}
    private boolean foundPrimaryKeys = false; // becomes true if odbc metadata gives primary key columns

    public void setPrimaryKey(String tName, Vector<String> columns) {tableKeyColumns.put(tName,columns);}
    //--------------------------------------------------------------------------------------------------
    //                                constructor
    //--------------------------------------------------------------------------------------------------
    
	public DBStructure (Connection con) throws RDBConnectException
	{
		this.con = con;
		if (con == null) throw new RDBConnectException("Failed to connect to database");
		getSchemaData();
		processMetaData();
	}

    //--------------------------------------------------------------------------------------------------
    //                                methods to get metadata
    //--------------------------------------------------------------------------------------------------
    
	
	public void getSchemaData() throws RDBConnectException
	{
		try{
			meta = con.getMetaData();
			DBProduct = meta.getDatabaseProductName();
		}
	      catch (SQLException ex)
		    {throw new RDBConnectException("Failed to get database metadata " + ex.getMessage());}		
	}


	  /* Retrieve table names, column names and if possible primary key column names from
	  the odbc metadata. */
	  public void processMetaData() throws RDBConnectException
	  {
	      Hashtable<String, String> tableNames = new Hashtable<String, String>();
	      boolean more,c_more;
	      String name, pName,column;
	      Vector<String> colNames;
	      ResultSet r_tabs, r_cols;
	      String [] types = {"TABLE"}; // sometimes used a last argument of getTables call (not for Excel)
	      if (DBProduct.startsWith("EXCEL"))
	         {isExcel = true; types = null;}
	      else if (DBProduct.equalsIgnoreCase("ACCESS")) // in case we need to enclose access column names in '[]' in queries
	            {isAccess = true; }

	      if (writeCatalogsAndSchemas)
	      {
	        try{
	          ResultSet r_cats = meta.getCatalogs();
	          more = r_cats.next();
	          while (more)
	          {
	              // get table name and convert it to upper case
	              String catName = r_cats.getString("TABLE_CAT");
	              message("Catalog: " + catName);
	              more = r_cats.next();
	          }
	          r_cats.close();
	        }
	        catch (SQLException ex)
			        {throw new RDBConnectException("Failure getting catalog  names: " + ex.getMessage());}
	        try{
	          ResultSet r_schemas = meta.getSchemas();
	          more = r_schemas.next();
	          while (more)
	          {
	              // get table name and convert it to upper case
	              String catName = r_schemas.getString("TABLE_SCHEM");
	              message("Schema: " + catName);
	              more = r_schemas.next();
	          }
	          r_schemas.close();
	        }
	        catch (SQLException ex)
			      {throw new RDBConnectException("Failure getting schema  names: " + ex.getMessage());}
	      }
	      // get basic table and column data
	      try{
	          r_tabs = meta.getTables(catalogName,userSchemaName,"%",types);
	          more = r_tabs.next();
	          while (more)
	          {
	              // get table name and convert it to upper case
	              name = r_tabs.getString("TABLE_NAME").toUpperCase();
	              pName = name;
	              /* Excel odbc seems to return two sets of table names - one with final '$', one without.
	               Use only the first. */
	              if (isExcel)
	              {
	                  if (name.endsWith("$"))
	                  {
	                      pName = pName.substring(0, pName.length() - 1); //strip off last '$'
	                      tableNames.put(name, pName); // key has final '$', value does not
	                  }
	              }
	              else {tableNames.put(name,pName);}
	              more = r_tabs.next();
	          }
	          r_tabs.close();

	          for (Enumeration<String> en = tableNames.keys();en.hasMoreElements();)
	          {
	              name = en.nextElement();
	              pName = tableNames.get(name);
	              // get column names for table
	              try{
	                  colNames = new Vector<String>();
	                  Vector<String> colTypes = new Vector<String>();
	                  r_cols = meta.getColumns(catalogName,userSchemaName,name,"%");
	                  c_more = r_cols.next();
	                  while (c_more)
	                  {
	                      // column names in upper case
	                      column = r_cols.getString("COLUMN_NAME").toUpperCase();
	                      String typeName = r_cols.getString("TYPE_NAME");
	                      // metadata from embedded Apache Derby duplicates column names; avoid this
	                      if (!GenUtil.inVector(column, colNames)) 
	                      {
		                      colNames.addElement(column);
		                      colTypes.addElement(typeName);
	                      }
	                      c_more = r_cols.next();
	                  }
	                  if (colNames.size() > 0)
	                  {
	                      tableColumns.put(pName,colNames);
	                      tableColumnTypes.put(pName,colTypes);
	                  }
	                  //else {throw new RDBConnectException("Found no columns in odbc metadata for table '" + pName + "'");}
	                  r_cols.close();
	              }
	              catch (SQLException ex)
			    {throw new RDBConnectException("Failure getting column names: "+ ex.getMessage());}

	              /* get primary key columns for table; but do not try to for Excel.
	              Failures issue a warning but are not fatal. */
	              if (!isExcel) try{
	                  Vector<String> keyColNames = new Vector<String>();
	                  r_cols = meta.getPrimaryKeys(catalogName,userSchemaName,name);
	                  c_more = r_cols.next();
	                  while (c_more)
	                  {
	                      column = r_cols.getString("COLUMN_NAME").toUpperCase();
	                      keyColNames.addElement(column);
	                      c_more = r_cols.next();
	                  }
	                  r_cols.close();
	                  if (keyColNames.size() > 0) {tableKeyColumns.put(pName,keyColNames);foundPrimaryKeys = true;}
	                  //else {message("Found no primary key columns in odbc metadata for table '" + pName + "'");}
	              }
	              catch (SQLException ex)
	                  {message("Failure getting primary key column names for table '" + pName + "': " + ex.getMessage());}
	          }
	      }
	      catch (SQLException ex)
			    {throw new RDBConnectException("Failure getting table  names: " + ex.getMessage());}
	  }
	  
	  
	  //----------------------------------------------------------------------------------------------
	  //     Making the structure available to the Mapper Editor; interface StructureDefinition
	  //----------------------------------------------------------------------------------------------

		/**
		 * find the Element and Attribute structure of some named top element, stopping at the
		 * next complex type definitions it refers to. 
		 * The only element names recognised are 'database' and the table names; all other 
		 * names cause it to return null
		 * @param String name the name of the element
		 * @return ElementDef the EObject subtree (ElementDef and AttributeDef EObjects) defined by the name
		 */
		public ElementDef nameStructure(String name)
		{
			ElementDef rootEl = null;
			
			// root element of the whole database
			if ((name != null) && (name.equals("database")))
			{
				rootEl = MapperFactory.eINSTANCE.createElementDef();
				rootEl.setName("database");
				rootEl.setType("databaseType");
				rootEl.setMinMultiplicity(MinMult.ONE);
				rootEl.setMaxMultiplicity(MaxMult.ONE);
				rootEl.setExpanded(true);
				
				for (Iterator<String> it = tableColumns.keySet().iterator();it.hasNext();)
				{
					// add one Element under the 'database' element for each table
					String tableName = it.next();
					ElementDef tableEl = MapperFactory.eINSTANCE.createElementDef();
					tableEl.setName(tableName);
					tableEl.setType(tableName + "Type");
					tableEl.setMinMultiplicity(MinMult.ONE);
					tableEl.setMaxMultiplicity(MaxMult.ONE);
					tableEl.setExpanded(false);
					rootEl.getChildElements().add(tableEl);
				}
			}
			
			// root element of some table
			else if (name != null)
				for (Iterator<String> it = tableColumns.keySet().iterator();it.hasNext();)
					if (it.next().equals(name))
					{
						rootEl = MapperFactory.eINSTANCE.createElementDef();
						rootEl.setName(name);
						rootEl.setType(name + "Type");
						rootEl.setMinMultiplicity(MinMult.ONE);
						rootEl.setMaxMultiplicity(MaxMult.ONE);
						rootEl.setExpanded(true);
						
						// add one 'record' element under the table
						ElementDef recordEl = MapperFactory.eINSTANCE.createElementDef();
						recordEl.setName("record");
						recordEl.setMinMultiplicity(MinMult.ZERO);
						recordEl.setMaxMultiplicity(MaxMult.UNBOUNDED);
						recordEl.setExpanded(true);
						rootEl.getChildElements().add(recordEl);
						
						// add one element for each column under the 'record' element
						Vector<String> colNames = tableColumns.get(name);
						Vector<String> colTypes = tableColumnTypes.get(name);
						for (int c = 0; c < colNames.size(); c++)
						{
							String colName = colNames.get(c);
							String colType = colTypes.get(c);
							ElementDef colEl = MapperFactory.eINSTANCE.createElementDef();
							colEl.setName(colName);
							colEl.setType(colType);
							colEl.setMinMultiplicity(MinMult.ZERO);
							colEl.setMaxMultiplicity(MaxMult.ONE);
							colEl.setExpanded(true);
							recordEl.getChildElements().add(colEl);
						}
					}
			return rootEl;
		}

		/**
		 * find the Element and Attribute structure of some complex type, stopping at the
		 * next complex type definitions it refers to
		 * The only element names recognised are 'databaseType' and the table names
		 * followed by 'Type'; all other types cause it to return null
		 * @param type the name of the complex type
		 * @return the EObject subtree (ElementDef and AttributeDef EObjects) defined by the type
		 */
		public ElementDef typeStructure(String type)
		{
			ElementDef typeStruct = null;
			if ((type != null) && (type.endsWith("Type")))
			{
				String name = type.substring(0,type.length() - 4);
				return nameStructure(name);
			}
			return typeStruct;			
		}
	  
	  /**
	   * Create the structure of nested elements which corresponds to a database, or
	   * to XML extracted from the database (eg in the query tool)
	   * @return
	   */
	  public ElementDef getDatabaseStructure()
	  {
		  return nameStructure("database");
	  }

		/**
		 * 
		 * @return an array of the top-level complex types defined in the structure definition - 
		 * any of which can be the type of a mapping set
		 */
		public String[] topComplexTypes()
		{
			ArrayList<String> allTypes = new ArrayList<String>();
			allTypes.add(""); // the default choice on the menu, before any choice is made, is ""
			allTypes.add("databaseType"); // type of top database node
			// add one type for each table of the database
			for (Iterator<String> it = tableColumns.keySet().iterator();it.hasNext();)
			{
				String tableName = it.next();
				allTypes.add(tableName + "Type");
			}
			String[] res = new String[allTypes.size()];
			return allTypes.toArray(res);
		}

		/**
		 * 
		 * @return an array of the top-level complex types defined in the structure definition - 
		 * any of which can be the type of a mapping set
		 */
		public String[] topNames()
		{
			ArrayList<String> allTypes = new ArrayList<String>();
			allTypes.add(""); // the default choice on the menu, before any choice is made, is ""
			allTypes.add("database"); // type of top database node
			// add one type for each table of the database
			for (Iterator<String> it = tableColumns.keySet().iterator();it.hasNext();)
			{
				String tableName = it.next();
				allTypes.add(tableName);
			}
			String[] res = new String[allTypes.size()];
			return allTypes.toArray(res);
		}
		

		  // return the column type, or 'VARCHAR' if the table name or column name are not found
		  public String getColumnType(String table,String column)
		  {
		      String cType = "VARCHAR"; //default if things go wrong.
		      Vector<String> colNames = tableColumns.get(table);
		      Vector<String> colTypes = tableColumnTypes.get(table);
		      if (colNames != null)
		          for (int i = 0; i < colNames.size(); i++)
		          {
		              String col = (String)colNames.elementAt(i);
		              if (col.equals(column)) cType = (String)colTypes.elementAt(i);
		          }
		      return cType;
		  }


	  //----------------------------------------------------------------------------------
	  //            implementing the interface PropertyValueSupplier
	  //----------------------------------------------------------------------------------
	  
		
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
			String[]  empty = {};
			if ((modelClassName.equals("MappedStructure")) && 
					(modelPropertyName.equals("Top Element Type"))) return topComplexTypes();
			if ((modelClassName.equals("MappedStructure")) && 
					(modelPropertyName.equals("Top Element Name"))) return topNames();
			return empty;
		}
		  
	//----------------------------------------------------------------------------------
	//                       interface StructureDefinition
	//----------------------------------------------------------------------------------

	    /** a database structure involves no namespaces - sensibly */
	    public NamespaceSet NSSet() {return new NamespaceSet();}
		

	    /** check that for any node reached by an XPath rp from the root of the document,
	    * the relative path relP from that node is a valid XPath and leads to a unique node in the
	    * XML structure xs.
	    * <p>
	    * Write an error message if the path is not unique and writeMessage = true.
	    * <p>
	    * Only return true if the cross path is unique in all possible tree structures for the document,
	    * i.e with all possible root elements.
	    * <p>
	    * SIDE-EFFECT: records that the path from the root to the target of the cross path is used in the MDL.
	    */
	    public boolean uniquePath(NamespaceSet sourceNSS, Xpth rp, Xpth relP, boolean writeMessage)
	    throws MapperException
	    {
	    	return false;
	    }

	    /** check that for any node reached by an XPath rp from the root of the document,
	    * the relative path relP from that node is a valid XPath and always leads at lest one node in the
	    * XML structure xs.
	    * <p>
	    * Only return true if the path is non-optional, ie gives at least one node,
	    * for all compatible tree structures (i.e root nodes of the document).
	    * <p>
	    * SIDE-EFFECT - records MDL use of the path to the final node on the relative path
	    */
	    public boolean nonOptionalPath(NamespaceSet sourceNSS, Xpth rp, Xpth relP)
	    throws MapperException
	    {
	    	return false;
	    }
	  
	  //----------------------------------------------------------------------------------
	  //                                 trivia
	  //----------------------------------------------------------------------------------

	  private void message(String s) {System.out.println(s);}

}
