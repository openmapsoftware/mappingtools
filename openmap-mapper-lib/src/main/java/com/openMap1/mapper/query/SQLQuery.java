package com.openMap1.mapper.query;

import com.openMap1.mapper.structures.DBStructure;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.core.RDBConnectException;
import com.openMap1.mapper.core.MapperException;

import java.util.*;

/**
 * this class represents a restricted type of query
 * (with only AND connectors between conditions)
 * to be executed on a Relational database when constructing a DOM
 * to answer a query on the object model.
 * 
 * If some of the tests (such as 'contains') cannot be passed through
 * to the SQL, nevertheless the object of this class retains those tests
 * and can check a returned record (represented as a Vector
 * of [table,column,value] string arrays against all the tests.
 */
public class SQLQuery {
	
	// if true, make all tables core to the query, so there are no left joins
	private boolean makeAllTablesCore = false;

private DBStructure database; // the database this sql will be run against

/**  String names of all core tables  */
public Hashtable<String,String> coreTables() {return coreTables;}
private Hashtable<String,String> coreTables;

/**  String names of all tables involved in the query */
public Hashtable<String,String> allTables() {return allTables;}
private Hashtable<String,String> allTables;


public Vector<String> nonCoreTables() {return nonCoreTables;}
private Vector<String> nonCoreTables;

/** all columns to be output.
key = String form table.column.
value = String array [table,column] */
public Hashtable<String, String[]> outputColumns() {return outputColumns;}
private Hashtable<String, String[]> outputColumns;

/** conditions to be applied, of the form column [test] constant value.
key = string form of condition, as passed to SQL .
value = object of inner class valCondition.
The tests are held both in the form of the query tool and the equivalent SQL form. */
public Hashtable<String, valCondition> valConditions() {return valConditions;}
private Hashtable<String, valCondition> valConditions;

/** conditions to be applied, of the form column [test] column.
key = string form of condition.
value = object of inner class crossCondition.   */
public Hashtable<String, crossCondition> crossConditions() {return crossConditions;}
private Hashtable<String, crossCondition> crossConditions;

// true if the 'database' is Excel and so needs strange table names like [person$]
private boolean isExcel;
  // true if the database is Access and we want to enclose column names in '[]'
private boolean isAccess;

private boolean tracing = false;

//-----------------------------------------------------------------------------------------
//                         constructor
//-----------------------------------------------------------------------------------------


public SQLQuery(DBStructure db) throws RDBConnectException
{
    allTables = new Hashtable<String,String>();
    coreTables = new Hashtable<String,String>();
    nonCoreTables = new Vector<String>();
    outputColumns = new Hashtable<String, String[]>();
    valConditions = new Hashtable<String, valCondition>();
    crossConditions = new Hashtable<String, crossCondition>();
    database = db;
    isExcel = database.isExcel();
    isAccess = database.isAccess();
    if (tracing) writeAllTables();
}

private void writeAllTables()
{
	message("\nAll Tables");
	for (Enumeration<String> en = database.tableColumns().keys();en.hasMoreElements();)
	{
		message("Table " + en.nextElement());
	}
}

/**  SQL string form  of the query */
public String stringForm() throws MapperException
{
        String query = "SELECT";
        int fieldNumber = 0;
        for (Enumeration<String> en = outputColumns.keys(); en.hasMoreElements();)
        {
            fieldNumber++;
            String key = en.nextElement(); // table.column, with no difference for Excel
            String[] tabCol = outputColumns.get(key);
            String table = tabCol[0];
            if (isExcel) table = "[" + table + "$]";
            String column = tabCol[1];
            if (isAccess) column = "[" + column + "]"; // put column names in [] for Access only
            query = query + " " + table + "." + column;
            if (fieldNumber < outputColumns.size()) {query = query + ", ";}
        }
        
        // backstop; if there are no columns, select all columns to make it legal SQL
        if (fieldNumber == 0) query = query + " * ";

        // add FROM <core tables>
        query = query + " FROM ";
        int tableNumber = 0;
        for (Enumeration<String> it = coreTables().keys(); it.hasMoreElements();)
        {
            tableNumber++;
            String table = it.nextElement();
            if (isExcel) table = "[" + table + "$]";
            query = query + " " + table;
            if (tableNumber < coreTables.size()) query = query + ", ";
        }
        
        // add (zero or more) LEFT JOIN <non-core table> ON <non-core cross conditions>        
        for (int nc = 0; nc < nonCoreTables.size();nc++)
        {
        	String nonCoreTable = nonCoreTables.get(nc);
        	query = query + " LEFT OUTER JOIN " + nonCoreTable + " ON ";
        	query = query + addLeftJoinConditions(nonCoreTable);
        }

        // add value conditions and core cross-conditions
        if ((valConditions.size() > 0)|(crossConditions.size() > 0))
        {
            int condNumber = 0;
            for (Enumeration<valCondition> en = valConditions.elements(); en.hasMoreElements();)
            {
                valCondition vc = en.nextElement();
                // leave any value condition out if its test cannot be converted to SQL
                if (!vc.SQLTest.equals("NO SQL TEST"))
                {
                    if (condNumber == 0) {query = query + " WHERE ";}
                    else  {query = query + " AND ";}
                    condNumber++;
                    // storing conditions puts Excel funnies in keys if necessary
                    query = query + " " + vc.stringForm();
                }
            }

            //the key in the cross conditions hashtable is the string form of the condition
            for (Enumeration<String> en = crossConditions.keys(); en.hasMoreElements();)
            {
            	String queryText = en.nextElement();
            	crossCondition cc = crossConditions.get(queryText);
            	// do not repeat non-core cross-conditions (they appear in LEFT JOIN ... ON..)
            	if (cc.isCore)
            	{
                    if (condNumber == 0) {query = query + " WHERE ";}
                    else  {query = query + " AND ";}
                    condNumber++;
                    // storing conditions puts Excel funnies in keys if necessary
                    query = query + " " + queryText;
            	}
            }
        }
        return query;
}

/**
 * add conditions to come after 'ON' for a left join table
 * @param nonCoreTable
 * @return
 * @throws MapperException
 */
private String  addLeftJoinConditions(String nonCoreTable) throws MapperException
{
	String conds = "";
	int condNumber  = 0;
    for (Enumeration<String> en = crossConditions.keys(); en.hasMoreElements();)
    {
    	String queryText = en.nextElement();
    	crossCondition cc = crossConditions.get(queryText);
    	// should the left joined table only be in the RHS of the condition??
    	if (queryText.startsWith(nonCoreTable)) queryText = cc.reversedStringForm();
    	
    	// find the other table in a condition involving this table
    	String otherTable = "";
    	if (cc.leftTable.equals(nonCoreTable)) otherTable = cc.rightTable;
    	if (cc.rightTable.equals(nonCoreTable)) otherTable = cc.leftTable;
    	
    	// only add left join conditions for previous tables, core or non-core
    	if (alreadyMentioned(nonCoreTable,otherTable))
    	{
    		if (condNumber > 0) conds = conds + " AND ";
    		condNumber++;
    		if (cc.isCore) throw new MapperException("ON cross-condition is core to the query: " + queryText);
    		conds = conds + queryText;
    	}   
    }
    
	if (condNumber == 0) throw new MapperException("No left join ON conditions for table " + nonCoreTable);
	return conds;
}

/**
 * return true if either:
 * (1) the other table is a core table
 * (2) the other table occurs in the list of non-core tables, before this non-core table
 * @param nonCoreTable
 * @param otherTable
 * @return
 */
private boolean alreadyMentioned(String nonCoreTable,String otherTable)
{
	// if the other table is a core table, return true
	if (coreTables.get(otherTable) != null) return true;
	
	// if the other table comes earlier than this table in the list of non-core tables, return true
	else for (int i = 0; i < nonCoreTables.size(); i++)
	{
		if (nonCoreTables.get(i).equals(nonCoreTable)) return false;
		if (nonCoreTables.get(i).equals(otherTable)) return true;
	}
	return false;
}


//------------------------------------------------------------------------------------------
//                    validation of table and column names when building a query
//------------------------------------------------------------------------------------------

private void checkTableName(String table) throws MapperException
{
	// message("checking table " + table);
	if (database.tableColumns().get(table) == null)
		throw new MapperException("Database has no table '" + table + "'");
}

private void checkColumnName(String table, String column) throws MapperException
{
	if (database.tableColumns().get(table) == null)
		throw new MapperException("Database has not got a table '" + table + "'");
	if (!GenUtil.inVector(column, database.tableColumns().get(table)))
		throw new MapperException("Database has no column '" + column + "' in table '" + table + "'");
}


//------------------------------------------------------------------------------------------
//                          public methods to build up the query
//------------------------------------------------------------------------------------------

/**
 * add a named table to the query. This method assumes core tables come first in the natural order
 * of QueryClasses, because classes in query conditions are added before classes that appear only
 * in write fields. 
 * Once a class has been added as core, it cannot be added as non-core
 */
public void addTable(String table,boolean isCore) throws MapperException
{
	checkTableName(table);
	allTables.put(table, "1");
    if (isCore||makeAllTablesCore) coreTables.put(table,"1");
    else if ((coreTables.get(table) == null) 
    		&& (!makeAllTablesCore)
    		&& (!GenUtil.inVector(table, nonCoreTables))) 
    			nonCoreTables.add(table);
}

/**
 * add a column required in the SQL resultSet
 */
public void addOutputColumn(String table, String column, boolean isCore) throws MapperException
{
	addTable(table,isCore);
    String key = table + "." + column;
    String[] value = new String[2];
    value[0] = table;
    value[1] = column;
    outputColumns.put(key, value);
}

/** Add a condition relating a field to a constant.
Also require that field in the output of the query, so it can be used testing the XML output */
public void addValCondition(String table, String column, String queryTest, String value)  throws MapperException
{
	addTable(table,true); // table must be core to the query
    String colType =database.getColumnType(table,column);
    String sqlt = SQLTest(queryTest);  // may be null
    // store the condition even if there is no SQL equivalent of the test.
    valCondition vc = new valCondition(table, column, queryTest, sqlt, value,colType);
    valConditions.put(vc.stringForm(),vc); // store conditions without duplicating any
    addOutputColumn(table, column,true);
}

/** return the SQL equivalent of a test in the object query language,
 or null if there is no equivalent. */
private String SQLTest(String test)
{
    String[][] tConvert  = {{"=","="},{">",">"},{">=",">="},{"<","<"},{"=<","=<"}
       /* ,{"contains","CONTAINING"}, {"startsWith","STARTING WITH"}*/ };
    // Excel odbc does not seem to support the last two operators
    String sqlt = null;
    for (int i = 0; i < tConvert.length; i++)
        { if (test.equals(tConvert[i][0])) sqlt = tConvert[i][1];}
    return sqlt;
}


/** Add a condition equating two fields.
Also require both fields in the output of the query */
public void addCrossCondition(String lTable, String lColumn, String queryTest, String rTable, String rColumn, boolean isCore)
throws MapperException
{
	checkColumnName(lTable,lColumn);
	checkColumnName(rTable,rColumn);
    String sqlt = SQLTest(queryTest);  // may be null
    if (sqlt != null)
    {
        crossCondition cc = new crossCondition(lTable, lColumn, sqlt, rTable,rColumn,isCore);
        crossConditions.put(cc.stringForm(),cc);
        addOutputColumn(lTable, lColumn,isCore);
        addOutputColumn(rTable, rColumn,isCore);
    }
    else
      {throw new MapperException("Cross condition should not involve test '"
          + queryTest + "' which cannot be converted to SQL");}
}

//-------------------------------------------------------------------------------------------------
//                                  inner classes for individual conditions
//-------------------------------------------------------------------------------------------------

private class SQLCondition
{
    String leftTable;
    String leftColumn;
    String SQLTest;

    SQLCondition(String lt,String lc,String tst)
    {
        leftTable = lt;
        leftColumn = lc;
        SQLTest = tst;
        if (SQLTest == null) {SQLTest = "NO SQL TEST";}
    }

    String stringForm()
    {
        String table = leftTable;
        if (isExcel) table = "[" + table + "$]";
        String column = leftColumn;
        if (isAccess) column = "[" + column + "]";
        return (table + "." + column + " " + SQLTest + " ");
    }
} // end of class SQLCondition

private class valCondition extends SQLCondition
{
    String value;
    String typeName;
    String queryTest;

    valCondition(String lt,String lc,String qTest, String tst,String val,String type)
    {
        // tst is the SQL test, which is null if the query test will not translate to SQL
        super(lt,lc,tst);
        value = val;
        typeName = type;
        queryTest = qTest; // the test as in the query.
    }

    /* put quotes around a string value only if it is not a number
    and the column is not of type 'VARCHAR' */
    String RHS()
    {
        String res = "'" + value + "'";
        if ((isNumber(value)) && (typeName != null) && !(typeName.equals("VARCHAR"))) res = value;
        return res;
    }

    String stringForm()
        {return (super.stringForm() + RHS());}

    public boolean passeOneTest(Vector<ResultCell> record) throws MapperException
    {
        boolean sat = false;
        String LHS = getValue(record,leftTable,leftColumn);
        String RHS = value;
        if (LHS != null) sat = QueryCondition.testOneCondition(LHS,queryTest,RHS);
        return sat;
    }
} // end of class valCondition

/* for all cross conditions, we assume the query test form (such as '=')
is a valid sql test form. */
class crossCondition extends SQLCondition
{
	boolean isCore;
    String rightTable;
    String rightColumn;

    crossCondition(String lt,String lc,String tst,String rt,String rc, boolean core)
    {
        super(lt,lc,tst);
        rightTable = rt;
        rightColumn = rc;
        isCore = core;
    }

    String stringForm()
    {
        String table = rightTable;
        if (isExcel) table = "[" + table + "$]";
        String column = rightColumn;
        if (isAccess) column = "[" + column + "]";
        return (super.stringForm() + table + "." + column);
    }
    
    String reversedStringForm()
    {
    	return (rightTable + "." + rightColumn + " " + SQLTest + " " + leftTable + "." + leftColumn);
    }

    public boolean passesOneTest(Vector<ResultCell> record) throws MapperException
    {
        boolean sat = false;
        String LHS = getValue(record,leftTable,leftColumn);
        String RHS = getValue(record,rightTable,rightColumn);
        if ((LHS != null) && (RHS != null)) sat = QueryCondition.testOneCondition(LHS,SQLTest,RHS);
        return sat;
    }
}

//-------------------------------------------------------------------------------------------------
//                               end of inner classes
//-------------------------------------------------------------------------------------------------

private String getValue(Vector<ResultCell> record, String table, String column)
{
    String val = null;
    for (int i = 0; i < record.size(); i++)
    {
        ResultCell rc = record.elementAt(i);
        if ((rc.tableName.equals(table)) && (rc.columnName.equals(column)))  val = rc.value;
    }
    return val;
}



// return true if a string can be interpreted as a number
private boolean isNumber(String val)
{
    boolean res = false;
    try
    {
        new Double(val);
        res = true;
    }
    catch (Exception e) {res = false;}
    return res;
}

/* Test a record against all the conditions in this SQLQuery object,
even if not all of them could be translated to SQL.
record is a Vector of resultCell objects. */
public boolean  satisfies(Vector<ResultCell> record) throws MapperException
{
    boolean sat = true;
    for (Enumeration<valCondition> en = valConditions.elements(); en.hasMoreElements();)
    {
        valCondition vc = en.nextElement();
        if (!vc.passeOneTest(record)) sat = false;
    }
    if (sat) for (Enumeration<crossCondition> en = crossConditions.elements(); en.hasMoreElements();)
    {
        crossCondition cc = en.nextElement();
        if (!cc.passesOneTest(record)) sat = false;
    }
    return sat;
}

private void message(String s) {System.out.println(s);}

}
