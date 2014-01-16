package com.openMap1.mapper.query;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.core.MapperException;

public class WriteField extends QueryMappingUser{
	
	private QueryClass queryClass;
	public QueryClass queryClass() {return queryClass;}
	
	private String propName;
	public String propName() {return propName;}
		
	public WriteField(QueryClass queryClass,String propName, QueryParser parser)
	{
		super(parser);
		this.queryClass = queryClass;
		this.propName = propName;
	}
	
	/**
	 * to keep the old QueryParser interface working
	 * @return
	 */
	public Vector<String> propertyVector()
	{
		Vector<String> propVec = new Vector<String>();
		propVec.add(queryClass.className());
		propVec.add(propName);
		propVec.add("present");
		return propVec;
	}
	
	/**
	 * 
	 * @return true if this is  valid field to write out
	 */
	public boolean valid()
	{
		EStructuralFeature feat = queryClass.getEClass().getEStructuralFeature(propName);
		return ((feat != null) && (feat instanceof EAttribute));
	}
	
	
	/**
	 * add all tables, columns and conditions to an SQLQuery to ensure it retrieves the smallest DOM
	 * required to support a query
	 */
	public void buildQuery(SQLQuery query, String code) throws MapperException
	{
		if (allMappings == null) message("Null mappings");
		// there may be several property mappings because of property conversions
		Vector<PropMapping> pMaps = allMappings.get(code);
		// false means the property mapping, being only involved in a write field, is not core to the query
		if (pMaps != null) for (Iterator<PropMapping> it = pMaps.iterator();it.hasNext();)
			handlePropMapping(it.next(), query, code,false);
	}

}
