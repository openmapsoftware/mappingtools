package com.openMap1.mapper.query;

import java.util.Vector;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.reader.MDLXOReader;

public interface QueryStrategy {
	
    
    /**
     *  define a query strategy, or throw a QueryStrategyException if you cannot
     * @throws MapperException
     * @throws QueryStrategyException
     */
    public void defineStrategy() throws MapperException, QueryStrategyException;
    
	/**
	 * define the mapping subsets for each queryClass in a DataSource
	 */
	public void setSubsets(DataSource ds) throws MapperException;

	public void setSubsets(String code, MDLXOReader reader) throws MapperException;


	  //----------------------------------------------------------------------------------
	  //                                New Access methods
	  //----------------------------------------------------------------------------------
	    
	    /**
	     * @return ordered list of QueryClasses for the best strategy
	     */
	    public Vector<QueryClass> bestStrategy();
	    
	    /**
	     * @param order
	     * @return the single link association leading to the QueryClass at position order in the best strategy
	     */
	    public LinkAssociation getLink(int order);


		  //----------------------------------------------------------------------------------
		  //                                Old Access methods
		  //----------------------------------------------------------------------------------
		    
    /**
     *  name of class at position order = 0.. nClasses-1 in the strategy order 
     *   */
    public String old_strategyClass(int order);
    





}
