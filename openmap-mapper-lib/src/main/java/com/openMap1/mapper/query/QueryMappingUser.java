package com.openMap1.mapper.query;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.ValueCondition;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.mapping.propertyMapping;

/**
 * 
 * Superclass of QueryClass, LinkAssociation, WriteField and QueryCondition,
 * supporting methods needed when setting up an SQLQuery.
 * 
 * Has some methods common only to WriteField and QueryCondition, 
 * defined because they both depend on a set of PropMappings
 * 
 * 
 * @author Robert
 *
 */

abstract public class QueryMappingUser {
	
	protected QueryParser parser;
	
	
	/* for each data source, all property mappings needed to populate the field.
	 * There may be more than one for each property in a write field or condition, because of property conversions. */
	protected Hashtable<String,Vector<PropMapping>> allMappings;

	
	public QueryMappingUser(QueryParser parser)
	{
		this.parser = parser;
		allMappings = new Hashtable<String,Vector<PropMapping>>(); 		
	}
	
	/**
	 * set up all the property mappings needed to populate the field from the data source.
	 * There may be more than one because of property conversions
	 * @param code
	 * @param mainPropMapping
	 */
	public void addMappings(String code, propertyMapping propMapping)
	{
		Vector<PropMapping> mappings = allMappings.get(code);
		if (mappings == null) mappings = new Vector<PropMapping>();
		// for fixed values in object mappings, the class cast won't work
		if (propMapping.map() instanceof PropMapping) mappings.add((PropMapping)propMapping.map());
		allMappings.put(code,mappings);
	}
	
	/**
	 * @param code
	 * @return all the property mappings needed to populate the field from the data source.
	 * There may be more than one, because of property conversions
	 */
	public Vector<PropMapping> getMappings(String code)
	{
		return allMappings.get(code);
	}
	
	/**
	 * add all tables, columns and conditions to an SQLQuery to ensure it retrieves the smallest DOM
	 * required to support a query
	 */
	abstract void buildQuery(SQLQuery query, String code) throws MapperException;
	
	/**
	 * 
	 * @param mapping a mapping to a relational database source
	 * @return the table name of the mapped node
	 * @throws MapperException if the XPath of the mapping does not have the required form for a mapping to a 
	 * relational database
	 */
	protected String getTableName(Mapping mapping) throws MapperException
	{
		String tableName = null;
		String path = mapping.getStringRootPath();
		int pathType = getPathType(path);
		// mappings to the top database node have no table
		if (pathType == DATABASE) {}
		// for mapppings to 'record' or to a column, pick out the second step in the path 'database/<TABLE>/record/..
		else if ((pathType == RECORD) || (pathType == COLUMN))
		{
			StringTokenizer st = new StringTokenizer(path,"/");
			st.nextToken();
			tableName = st.nextToken();
		}
		else if (pathType == INVALID) throw new MapperException("Invalid path for RDBMS mapping: '" + path + "'");
		return tableName;
	}
	
	public static int DATABASE = 0;
	public static int RECORD = 1;
	public static int COLUMN = 2;
	public static int INVALID = 3;
	
	/**
	 * 
	 * @return one of the allowed path types for paths in mappings to RDBMS,
	 * of the INVALID value
	*/
	protected int getPathType(String path)
	{
		int type = INVALID;
		StringTokenizer steps = new StringTokenizer(path,"/");

		// 'database' (possible for Object mappings)
		if ((steps.countTokens() == 1) && 
				(steps.nextToken().equals("database"))) type = DATABASE;

		 // 'database/<Table name>/record' (possible for Object and Association mappings) 
		 if ((steps.countTokens() == 3) && 
				(steps.nextToken().equals("database"))
				&& (steps.nextToken().length() > 0)
				&& (steps.nextToken().equals("record"))) type = RECORD;

		 // 'database/<Table name>/record/<Column name> (possible for property mappings) */
		 if ((steps.countTokens() == 4) && 
				(steps.nextToken().equals("database"))
				&& (steps.nextToken().length() > 0)
				&& (steps.nextToken().equals("record"))) type = COLUMN;

		return type;
	}
	
	/**
	 * 
	 * @param relPath
	 * @return from a relative path (involved in mapping conditions), the column name (which is the last step)
	 */
	protected String getColName(String relPath)
	{
		StringTokenizer steps = new StringTokenizer(relPath,"/");
		String column = "";
		while (steps.hasMoreTokens()) column = steps.nextToken();
		return column;
	}
	
	/**
	 * if a relative path (involved in a mapping condition) 
	 * includes a table name, by ending in <table name>/record/<column name>,
	 * return the table name; otherwise return null
	 * @param relPath
	 * @return
	 */
	protected String getTableName(String relPath) throws MapperException
	{
		String tableName = null;
		StringTokenizer steps = new StringTokenizer(relPath,"/");
		int is = 0;
		int ns = steps.countTokens();
		if (ns > 2) while (steps.hasMoreTokens())
		{
			is++;
			String step = steps.nextToken();
			if (is == ns - 2) tableName = step; // e.g step 1 of 3, or 2 of 4, etc.
			if ((is == ns - 1) && (!step.equals("record"))) // next step must be 'record'
				throw new MapperException("Relative path '" + relPath + "' in a mapping to an RDBMS does not end in <Table name>/record/<column name> ");
		}
		return tableName;
	}
	
	
	/**
	 * make all the additions to an SQLQuery necessary to handle a mapping condition -
	 * which may be a value condition or a cross condition.
	 * Tables need not be added - because they have been added when handling the 
	 * containing mappings.
	 * But columns for RHS and LHS, and SQL conditions for their relation, need to be added
	 * @param mc
	 * @param map
	 * @param query
	 * @param code
	 */
	protected void handleMappingCondition(MappingCondition mc, Mapping mapping, SQLQuery query, String code, boolean isCore) throws MapperException
	{
		String leftTable = getTableName(mapping);
		String leftCol = getColName(mc.getLeftPath());
		query.addOutputColumn(leftTable, leftCol,isCore);

		if (mc instanceof ValueCondition)
		{
			ValueCondition vc = (ValueCondition)mc;
			query.addValCondition(leftTable, leftCol, vc.getTest().toString(), vc.getRightValue());
		}
		else if (mc instanceof CrossCondition)
		{
			CrossCondition cc = (CrossCondition)mc;
			String objClass = mapping.getQualifiedClassName();
			String subset = mapping.getSubset();
			ObjMapping om = getObjMapping(objClass,subset, code);
			String rightTable = getTableName(om);
			String rightCol = getColName(cc.getRightPath());
			query.addOutputColumn(rightTable, rightCol,isCore);
			query.addCrossCondition(leftTable, leftCol, cc.getTest().toString(), rightTable, rightCol,isCore);
		}
	}
	
	/**
	 * 
	 * @param objClass
	 * @param subset
	 * @param code
	 * @return
	 */
	protected ObjMapping getObjMapping(String objClass,String subset,String code)  throws MapperException
	{
		ObjMapping om = null;
		for (Iterator<QueryClass> it = parser.queryClasses().iterator();it.hasNext();)
		{
			QueryClass qc = it.next();
			if (qc.className().equals(objClass))
			{
				ObjMapping omp = qc.getMapping(code);
				if ((omp != null) && (omp.getSubset().equals(subset))) om = omp;
			}
		}
		if (om == null) throw new MapperException("Cannot find object mapping for class " + objClass + ", subset '" + subset + "'");
		return om;
	}
	
	/**
	 * 
	 * @param mapping
	 * @param query
	 * @param code
	 */
	protected void handlePropMapping(PropMapping mapping,SQLQuery query,String code, boolean isCore) throws MapperException
	{
		String path = mapping.getStringRootPath();
		// only allowed case - property value is mapped to a column
		if (getPathType(path) == QueryMappingUser.COLUMN)
		{
			String tName = getTableName(mapping);
			query.addTable(tName,isCore);
			query.addOutputColumn(tName, getColName(path),isCore);
			
			// handle all mapping conditions
			for (Iterator<MappingCondition> iu = mapping.getMappingConditions().iterator();iu.hasNext();)
				handleMappingCondition(iu.next(),mapping,query,code,isCore);
		}
		else throw new MapperException("Invalid path to a property mapping in an RDBMS: '" + path + "'");
	}


	protected void message(String s) {System.out.println(s);}
}
