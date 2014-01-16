package com.openMap1.mapper.query;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.emf.ecore.EReference;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;

/**
 * class for a link association in a query
 * @author Robert
 *
 */

public class LinkAssociation extends QueryMappingUser{
	
	private QueryClass startClass;
	public QueryClass startClass() {return startClass;}
	
	private QueryClass endClass;
	public QueryClass endClass() {return endClass;}
	
	private EReference ref;
	public EReference ref() {return ref;}
	
	private Hashtable<String,AssocMapping> mappings;
	
	/**
	 * 
	 * @param startClass
	 * @param ref
	 * @param endClass
	 */
	public LinkAssociation(QueryClass startClass,EReference ref, QueryClass endClass, QueryParser parser)
	{
		super(parser);
		this.startClass = startClass;
		this.ref = ref;
		this.endClass = endClass;
		mappings = new Hashtable<String,AssocMapping>(); 
	}
	
	/**
	 * @return the full association name, as used in MDL association mappings
	 * If the EReference has no opposite, this is just the EReference (role) name.
	 * If it has an opposite, the full name is the two role names concatenated,
	 * (in lexicographic order) with '|' between
	 */
	public String assocName()
	{
		EReference opposite = ref.getEOpposite();
		if (opposite == null) return ref.getName();
		else return ModelUtil.assocName(ref.getName(), opposite.getName());
	}
	
	/**
	 * @return the 'start end' 1 or 2 as used in MDL association mappings
	 * If the EReference has no opposite, this is always 1.
	 * If there is an opposite, return 2 only if the opposite role name comes first in the
	 * association name.
	 */
	public int startEnd()
	{
		EReference opposite = ref.getEOpposite();
		if (opposite == null) return 1;
		else if (assocName().startsWith(ref.getName() + "|")) return 1;
		else return 2;		
	}

	/**
	 * array form of link association ,for use in earlier query tools
	 * @return
	 */
	public String[] arrayForm()
	{
		String[] array = new String[3];
		array[0] = startClass.className();
		array[1] = assocName();
		array[2] = endClass.className();
		return array;
	}
	
	/**
	 * @return key for storing all link associations in a hashtable
	 */
	public String key()
	{
		return (startClass.identifier() + "_" + assocName() + endClass.identifier());
	}
	
	/**
	 * remember one association mapping for a data source
	 * @param code
	 * @param mapping
	 */
	public void setMapping(String code, AssocMapping mapping)
	{
		mappings.put(code,mapping);
	}
	
	/**
	 * 
	 * @param code
	 * @return one association mapping for a data source, or null if there is none
	 */
	public AssocMapping getMapping(String code)
	{
		return mappings.get(code);
	}

	/**
	 * add all tables, columns and conditions to an SQLQuery to ensure it retrieves the smallest DOM
	 * required to support a query
	 */
	public void buildQuery(SQLQuery query, String code)  throws MapperException
	{
		// this link condition is core if the QueryClass it leads to is core
		boolean isCore = endClass.isCore();
		AssocMapping mapping = getMapping(code);
		if (mapping != null)
		{
			// add a table for the association mapping
			String tName = getTableName(mapping);
			if (tName == null) throw new MapperException("No table for association mapping");
			query.addTable(tName,isCore);
			
			// add columns and SQL conditions for any ValueConditions in the association mapping
			for (Iterator<MappingCondition> it = mapping.getMappingConditions().iterator();it.hasNext();)
				handleMappingCondition(it.next(),mapping,query,code,isCore);
			
			// association end mappings; mainly pick up cross-conditions
			for (int end = 0; end < 2; end++)
			{
				AssocEndMapping aem = mapping.getMappedEnd(end);
				for (Iterator<MappingCondition> it = aem.getMappingConditions().iterator();it.hasNext();)
					handleMappingCondition(it.next(),aem,query,code,isCore);
			}
			
		}
	}

}
