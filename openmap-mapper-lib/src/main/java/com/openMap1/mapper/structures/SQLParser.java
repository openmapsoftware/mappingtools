package com.openMap1.mapper.structures;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.core.MapperException;

public class SQLParser {
	
	private Vector<String> sqlLines;
	
	// content of lines outside all '()', with keys  '%&k_1' etc substituted for any '()'
	private Vector<String> outerLines = new Vector<String>();
	
	// contents of all '()', with keys substituted for any nested '()'
	private Hashtable<String,String> bracketContents = new Hashtable<String,String>();
	
	// start of all keys used to index bracket contents
	private String keyStart = "%&k_";
	
	// index used to generate unique keys
	private int keyIndex = 1;
	
	// generate unique new keys
	private String newKey()
	{
		String key = keyStart + keyIndex;
		keyIndex++;
		return key;
	}
	
	// vector of table names
	private Vector<String> tableNames = new Vector<String>();
	
	// names of fields in each table; key = table name
	private Hashtable<String,Vector<String>> fieldNames = new Hashtable<String,Vector<String>>();
	
	// names of fields in each table; key = table name
	private Hashtable<String,Vector<String>> fieldTypes = new Hashtable<String,Vector<String>>();
	
	
	//------------------------------------------------------------------------------------------------
	// 									    constructor
	//------------------------------------------------------------------------------------------------
	
	public SQLParser(Vector<String> sqlLines)
	{
		this.sqlLines = sqlLines;
		message("SQL lines: " + sqlLines.size());
	}
	
	//------------------------------------------------------------------------------------------------
	// 						parsing SQL to extract table definitions
	//------------------------------------------------------------------------------------------------
	
	
	public void parse() throws MapperException
	{

		// replace line breaks by spaces, to have a single line to deal with
		String singleline = "";
		for (int i = 0; i < sqlLines.size(); i++)
		{
			String line = sqlLines.get(i);
			// ignore comment lines beginning with "--"
			if (line.startsWith("--")) {}
			else {singleline = singleline + line + " ";}
		}
		
		// prepare to process all brackets
		int bracketLevel = 0;
		String[] current = new String[10];
		String[] key = new String[10];
		for (int b = 0; b < 10; b++) {current[b] = ""; key[b] = "";}
		StringTokenizer st = new StringTokenizer(singleline,"()",true);
		
		// extract contents of brackets at all levels
		while (st.hasMoreTokens())
		{
			String next = st.nextToken();
			if (next.equals("("))
			{
				// make a key for the contents of this bracket
				key[bracketLevel+1] = newKey();
				// at the current level, replace the bracket and its contents by the key in a separator
				current[bracketLevel] = current[bracketLevel] + "<" + key[bracketLevel+1] + ">";
				// start the contents of the next level of bracket
				bracketLevel++;
				current[bracketLevel] = "";
			}
			else if (next.equals(")"))
			{
				// store the contents of the bracket you are closing
				bracketContents.put(key[bracketLevel], current[bracketLevel]);
				// go back to the next outer bracket
				bracketLevel--;
			}
			else
			{
				// add to the text at the current bracket level
				current[bracketLevel] = current[bracketLevel] + next;
			}
		}
		if (bracketLevel != 0) throw new MapperException("Bracket imbalance: " + bracketLevel);
		String outerLine = current[0];
		
		// find outer level strings and their brackets
		StringTokenizer su = new StringTokenizer(outerLine,"<>");
		String outer = "";
		while (su.hasMoreTokens())
		{
			String lineKey = su.nextToken();
			if (lineKey.startsWith(keyStart)) parseOuterLine(outer,lineKey);
			else outer = lineKey;
		}
	}
	
	/**
	 * parse an outer line and the contents of its brackets, looking only for 'CREATE TABLE' outer lines
	 * @param outer
	 * @param key
	 */
	private void parseOuterLine(String outer,String key)
	{
		StringTokenizer st = new StringTokenizer(outer, " ");
		// need exactly 'CREATE TABLE <Name>' ; table fields are inside key.
		if (st.countTokens() == 3)
		{
			String first = st.nextToken();
			if (first.equals("CREATE")) 
			{
				String second = st.nextToken();
				if (second.equals("TABLE"))
				{
					String tableName = st.nextToken();
					String fields = bracketContents.get(key);
					recordTable(tableName,fields);
				}
			}
		}
	}
	
	/**
	 * @param tableName
	 * @param fields
	 */
	private void recordTable(String tableName,String fields)
	{
		tableNames.add(tableName);

		Vector<String> names = new Vector<String>();
		Vector<String> types = new Vector<String>();
		StringTokenizer st = new StringTokenizer(fields,",");
		while (st.hasMoreTokens())
		{
			String fieldEntry = st.nextToken();
			StringTokenizer fe = new StringTokenizer(fieldEntry," <>");
			if (fe.countTokens() > 1)
			{
				String fieldName = fe.nextToken();
				names.add(fieldName);
				String fieldType = fe.nextToken();
				// next token, if it exists, is the key to look up bracket contents
				if (fe.hasMoreTokens()) fieldType = fieldType + "(" + bracketContents.get(fe.nextToken()) + ")";
				types.add(fieldType);
			}
		}		
		fieldNames.put(tableName, names);
		fieldTypes.put(tableName, types);
	}
	
	//----------------------------------------------------------------------------------------------------
	//                      Outputs of field and table definitions
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * write a bare summary of the tables
	 */
	public void writeTables()
	{
		message("Tables:");
		for (int i = 0; i < tableNames.size(); i++)
		{
			String tableLine = tableNames.get(i) + "\t";
			Vector<String> fNames = fieldNames.get(tableNames.get(i));
			for (int f = 0; f < fNames.size(); f++)
				tableLine = tableLine + fNames.get(f) + ";";
			message(tableLine);
		}
	}
	
	
	/**
	 * 
	 * @param ms
	 * @throws MapperException
	 */
	public ElementDef makeTableStructure() throws MapperException
	{
		ElementDef root = MapperFactory.eINSTANCE.createElementDef();
		root.setName("database");
		root.setMinMultiplicity(MinMult.ONE);
		root.setExpanded(true);
		
		// make an ElementDef for each table in the database schema
		for (int t = 0; t < tableNames.size();t++)
		{
			String tableName = tableNames.get(t);
			ElementDef tableEl = MapperFactory.eINSTANCE.createElementDef();
			tableEl.setName(tableName);
			// some tables may be missing in some XML instances
			tableEl.setMinMultiplicity(MinMult.ZERO);
			tableEl.setMaxMultiplicity(MaxMult.ONE);
			tableEl.setExpanded(true);
			root.getChildElements().add(tableEl);

			// add a repeating <record> elementDef for each table
			ElementDef recordEl = MapperFactory.eINSTANCE.createElementDef();
			recordEl.setName("record");
			recordEl.setMinMultiplicity(MinMult.ZERO);
			recordEl.setMaxMultiplicity(MaxMult.UNBOUNDED);
			recordEl.setExpanded(true);
			tableEl.getChildElements().add(recordEl);
			
			// add column elementDefs for the table
			Vector<String> fields = fieldNames.get(tableName);
			Vector<String> types = fieldTypes.get(tableName);
			for (int f = 0; f < fields.size(); f++)
			{
				String field = fields.get(f);
				String type = types.get(f);
				ElementDef fieldEl = MapperFactory.eINSTANCE.createElementDef();
				fieldEl.setName(field);
				fieldEl.setType(type);
				fieldEl.setMinMultiplicity(MinMult.ZERO);
				fieldEl.setMaxMultiplicity(MaxMult.ONE);
				fieldEl.setExpanded(true);
				recordEl.getChildElements().add(fieldEl);
			}
		}
		return root;
	}

	
	private void message(String s) {System.out.println(s);}

}
