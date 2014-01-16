package com.openMap1.mapper.query;

import java.util.Hashtable;
import java.util.Vector;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.DBStructure;

public interface QueryParser {
	
    
    /** parse query text and return true if it is OK. */
    public boolean parse(String query);
    
    //-----------------------------------------------------------------------------------------
    //                           New API
    //-----------------------------------------------------------------------------------------

	/**
	 * @return list of all query classes
	 */
	public Vector<QueryClass> queryClasses();

	/**
	 * @return vector of  WriteFields to be written out
	 */
	public Vector<WriteField> writeFields();
	
    /**
     * vector of conditions on properties
     */
    public Vector<QueryCondition> conditions();
    
    /**
     * @return hashtable of link associations, 
     * keyed by association name
     */
    public Hashtable<String, LinkAssociation> linkAssociations();
    
        
	/**
	 * 
	 * @param removePackageNames
	 * @return pairs [className, propertyName] for column headers
	 */
    public Vector<String[]> getColumnHeaders(boolean removePackageNames);
    
    
    //--------------------------------------------------------------------------------------
    //				Generating SQL queries against relational data sources
    //--------------------------------------------------------------------------------------
    
    /**
     * 
     * @param code the code of a relational data source
     * @return a Vector of SQLQuery objects to populate the XML needed to answer the query;
     * currently there is only one SQL Query needed
     * @throws MapperException if the data source does not have relational structure
     */
    public Vector<SQLQuery> makeSQLQueries(String code, DBStructure database)  throws MapperException;





}
