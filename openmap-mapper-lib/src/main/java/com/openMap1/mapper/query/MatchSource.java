package com.openMap1.mapper.query;

import java.util.Vector;

public interface MatchSource {

    /**
     * @return the total number of result rows
     */
	public int resultSize();
    	
    /**
     * 
     * @param row
     * @return
     */
	public Vector<CellContent> getRow(int row);
    
	
    /**
     * @return [class name, property name] for each column header
     */
	public Vector<String[]> getColumnHeaders();




    /**
     * @param row
     * @param isMatched record that this row has been matched or not
     */
	public void setMatched(int row, boolean isMatched);
	
	/**
	 * state that no result row has yet been matched
	 */
	public void setAllUnMatched();
    
    /**
     * @param row
     * @return true if this row has been matched
     */
	public boolean isMatched(int row);
    
    /**
     * @return the number of unmatched rows
     */
	public int countUnMatched();
        
    /**
     * @return the short code A,B, C, etc of the data source
     */
	public String getCode();
    
    /**
     * @return the data source type - mainly XML or RDBMS
     */
	public String sourceType();

    public String instanceURIString();
    public String mappingSetURIString();
    public String classModelURIString();
    
    public String getShortName();


	
}
