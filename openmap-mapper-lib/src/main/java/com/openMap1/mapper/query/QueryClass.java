package com.openMap1.mapper.query;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;

import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;

/**
 * denotes an occurrence of a class in a query , which is detected
 * by the parser as being distinct from any other occurrence of the class.
 * 
 * 'small' classes may occur several times in the same query, at the end of different 
 * association chains, in which case they are distinct.
 * 
 * @author Robert
 *
 */

public class QueryClass extends QueryMappingUser{
	
	// true if the class must be present for a row of the query result
	public boolean isCore() {return isCore;}
	//core status can be set, but not unset
	public void setCore() {isCore = true;}
	private boolean isCore = false;
		
	// the underlying EClass
	private EClass theClass;
	
	// chain of associations leading to this class in the query text, in the form 'link1.link2....'
	private String assocChain;
	
	// key = data source code; value  = the object mapping used in that data source 
	private Hashtable<String,ObjMapping> mappings;
	
	public QueryClass(EClass theClass, String assocChain,  QueryParser parser)
	{
		super(parser);
		this.theClass = theClass;
		this.assocChain = assocChain;
		mappings = new Hashtable<String,ObjMapping>(); 
	}
	
	/**
	 * @return qualified class name
	 */
	public String className() {return ModelUtil.getQualifiedClassName(theClass);}
	
	public String assocChain() {return assocChain;}
	
	public EClass getEClass() {return theClass;}
	
	public String identifier() 
	{
		String id = "[" + className();
		if (!assocChain.equals("")) id = id + "/" + assocChain;
		id = id + "]";
		return id;
	}
	
	/**
	 * test of equality of two query classes
	 * @param otherClass
	 * @return
	 */
	public boolean equals(QueryClass otherClass)
	{
		return ((className().equals(otherClass.className())) && (assocChain().equals(otherClass.assocChain)));
	}
	
	/**
	 * test of matching of two query classes - 
	 * if the underlying classes match, and if their association chains match exactly
	 * @param otherClass
	 * @return
	 */
	public boolean matches(QueryClass otherClass)
	{
		boolean matches = false;
		if (className().equals(otherClass.className()))
		{
			// now require an exact match of association chains
			if (assocChain().equals(otherClass.assocChain())) matches = true;
		}
		return matches;
	}
	
	/**
	 * find the matching QueryClass in a list of existing QueryClasses, if there is a match;
	 * otherwise return null.
	 * @param others
	 * @return
	 */
	public QueryClass matchInList(List<QueryClass> others)
	{
		QueryClass matcher = null;
		for (Iterator<QueryClass> it = others.iterator();it.hasNext();)
		{
			QueryClass other = it.next();
			if (matches(other)) matcher = other;
		}
		return matcher;
	}
	
	/**
	 * set the mapping to this QueryClass in the DataSource with given code
	 * @param code
	 * @param subset
	 */
	public void setMapping(String code, ObjMapping mapping)
	{
		mappings.put(code, mapping);
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public ObjMapping getMapping(String code)
	{
		return mappings.get(code);
	}
	
	/**
	 * 
	 * @param code
	 * @return the subset for mappings to this QueryClass in the DataSource with given code;
	 * or null if there is no mapping
	 */
	public String getSubset(String code)
	{
		String subset = null;
		if (mappings.get(code) != null) subset = mappings.get(code).getSubset();
		return subset;
	}
	
	
	/**
	 * add all tables, columns and conditions to an SQLQuery to ensure it retrieves the smallest DOM
	 * required to support a query
	 */
	public void buildQuery(SQLQuery query, String code) throws MapperException
	{
		ObjMapping mapping = mappings.get(code);
		if (mapping != null)
		{
			// add 0 or 1 tables for the object mapping
			String tName = getTableName(mapping);
			if (tName != null) // tName can be null for a class mapped to the top 'database' node
			{
				query.addTable(tName,isCore);
				
				// add columns and SQL conditions for any ValueConditions in the object mapping
				for (Iterator<MappingCondition> it = mapping.getMappingConditions().iterator();it.hasNext();)
					handleMappingCondition(it.next(),mapping,query,code,isCore);
			}
			
		}
	}


}
