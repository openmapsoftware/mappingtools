package com.openMap1.mapper.query;


import com.openMap1.mapper.reader.EObjectRep;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.reader.objectToken;

import com.openMap1.mapper.converters.CSV_Wrapper;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.notRepresentedException;

import com.openMap1.mapper.structures.DBStructure;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * The class which executes a query against the data sources, 
 * once the query has been parsed and a query strategy decided.
 * 
 * @author robert
 *
 */
public class QueryExecutor
{
	// the data source the query is being evaluated against (for use in the query tool)   
    private DataSource dataSource;
    
    // the XOreader being used, if there is no data source (for use in the FHIR server)
    private MDLXOReader reader;

	private boolean tracing = true;
	
	private boolean writeSQLQuery = true;
	
    private QueryParser parser;
    private QueryStrategy strategy;

    // if true, force all outputs to be in upper case
    private boolean forceUpperCase = false;
    
    // if duplicates are not to be removed, this index is used in the key to distinguish duplicate rows
    private int resultIndex;
    
    // table of objectTokens, from which result Vectors are calculated
    private Vector<Vector<objectToken>> objectTable;
    
    // Table of results (property values), with a key to merge duplicated rows if mergeDuplicates = true
    private Hashtable<String, Vector<String[]>> resultTable;    
    
    // has the same key as Result Table. Value = the number of result rows merged
    private Hashtable<String,Integer> countTable;

    /** 
     * Vector of results, derived from the Hashtable resultTable after it is filled  */
    public Vector<Vector<String[]>> resultVector() {return resultVector;} 
    private Vector<Vector<String[]>> resultVector = new Vector<Vector<String[]>>();
    
    /**
     * In the same order as resultVector, the number of rows merged for
     */
    public Vector<Integer> countVector() {return countVector;}
    private Vector<Integer> countVector;
    
    //-------------------------------------------------------------------------------------------------
    //                                      Constructors
    //-------------------------------------------------------------------------------------------------

    /**
     * 
     * @param dataSource
     * @param p
     * @param s
     * @param forceUpper
     * @throws MapperException
     */
    public QueryExecutor(DataSource dataSource, QueryParser p, QueryStrategy s, boolean forceUpper) throws MapperException
    {
        this.dataSource = dataSource;
        reader = null;
        parser = p;
        strategy = s;
        forceUpperCase = forceUpper;
    }
    
    /**
     * 
     * @param reader
     * @param p
     * @param s
     * @throws MapperException
     */
    public QueryExecutor(MDLXOReader reader, QueryParser p, QueryStrategy s) throws MapperException
    {
    	dataSource = null;
    	this.reader = reader;
    	parser = p;
    	strategy = s;
    	forceUpperCase = false;
    }
    
    private XOReader reader() throws MapperException
    {
    	if (reader != null) return reader;
    	if (dataSource != null) return dataSource.getReader();
    	return null;
    }
    

    
    
    //------------------------------------------------------------------------------------------------------
    //                     Top-level execution of query - break data sources into manageable size
    //------------------------------------------------------------------------------------------------------

    public boolean executeQuery (String query, boolean mergeDuplicates) throws MapperException, QueryStrategyException
    {
    	if (dataSource == null) throw new MapperException("Null data source for QueryExecutor.executeQuery");
        boolean res = true;
        
        // prepare to accumulate results (and maybe merge/count duplicates) over all partitions of large data sources
        initialiseQuery();
        
        // find mapping subsets for all query classes (currently only used for relational data sources)
        strategy.setSubsets(dataSource);

        /* Relational data source; may need to segment large ResultSets into smaller ones to 
         * generate XML DOMs of reasonable size  */
        if (dataSource.isRelational())
        {
        	// at this point, the data source must be connected to its database; so make an RDBReader
            DBStructure database = new DBStructure(dataSource.con());
            RDBReader rdbReader = new RDBReader(database,"noFile");

            // convert the object model query into (one) SQL query to populate an XML DOM
            Vector<SQLQuery> queries = parser.makeSQLQueries(dataSource.getCode(), database);
            if (writeSQLQuery) message("SQL query " + queries.get(0).stringForm());
            dataSource.setSQLText(queries.get(0).stringForm());
            
            // run the SQL queries and save the result sets
            rdbReader.initiateQuery(queries);
            
            // work through results in partitions, each time making a DOM and answering the query from it
            while (!rdbReader.convertedAllRows())
            {
                Element rootNode = rdbReader.DOMFromSQL(queries);
                dataSource.setRootNode(rootNode); // in case we want to save it later
                reader().setRoot(rootNode);    	    	        	
                calculateResult(mergeDuplicates) ;       	        	            	
            }            
        }
        
        /* CSV file data source; may need to segment very large files into smaller ones to 
         * generate successive XML DOMs of reasonable size, rather than one huge one  */
        else if (dataSource.isCSVSource())
        {
        	CSV_Wrapper csvWrapper = (CSV_Wrapper)dataSource.getMappedStructure().getWrapper();
        	// make the csv source ready to read from the beginning of the csv file
        	csvWrapper.initialise();
        	boolean hasMoreRows = true;
        	while (hasMoreRows)
        	{
        		// for each successive call, advance the row in the csv file and create a DOM for the rows consumed
        		dataSource.renewDOM();
        		calculateResult(mergeDuplicates) ;       	        	
                hasMoreRows = csvWrapper.hasMoreRows();
        	}
        }
        
        /* plain XML data source already available as DOM; no possibility of segmenting */
        else
        {
        	calculateResult(mergeDuplicates) ;       	        	
        }

        setResultVector();
        trace("result rows: " + resultVector.size());

        return res;
    }
    
    public void initialiseQuery()
    {
        resultTable = new Hashtable<String, Vector<String[]>>();
        countTable = new Hashtable<String,Integer>();
        resultIndex = 0;
    }
    
    public void setResultVector()
    {
        // move all results and counts from the Hashtables to the Vectors
        resultVector = new Vector<Vector<String[]>>();
        countVector = new Vector<Integer>();
        for (Enumeration<String> en = resultTable.keys(); en.hasMoreElements();)
        {
        	String key = en.nextElement();
        	Vector<String[]> row = resultTable.get(key);
        	Integer count = countTable.get(key);
        	resultVector.add(row);
        	countVector.add(count);
        }
    }
    

    //------------------------------------------------------------------------------------------------
	//                   calculation of query results - storing a matrix of objectTokens
    //------------------------------------------------------------------------------------------------

    /**
     * accumulate query results from one DOM into the table resultTable
     * @param mergeDuplicates
     * @throws MapperException
     * @throws QueryStrategyException
     */
    public void calculateResult(boolean mergeDuplicates) throws MapperException, QueryStrategyException
    {
    	objectTable = new Vector<Vector<objectToken>>() ;
        String className = strategy.bestStrategy().get(0).className();
        Vector<objectToken> nodes = reader().getAllObjectTokens(className);
        trace("\nExecuting:\nFound " + nodes.size() + " objects in class " + className);
        for (int i = 0; i < nodes.size(); i++)
        {
        	// for each node found, make a Vector of 1 objectToken for the 1 QueryClass done so far
            Vector<objectToken> previousOReps = new Vector<objectToken>();
            objectToken oRep = nodes.elementAt(i);
            previousOReps.addElement(oRep);
            int order = 0;
            doClassStep(order,previousOReps,mergeDuplicates);
        }
        
        trace("\nCalculating properties");
        // calculate and store result rows from object rows
        addToResultTable(mergeDuplicates);
    }

    /** Recursion through classes in the query strategy:
    *
    *   order goes from 0.. nClasses - 1 through classes in strategy order
    *   previousOReps is a Vector of 0..order objectTokens for all previous classes in the strategy
    *   results is a vector of arrays [class,property,value] which will form one row
    *      of the output table, when the recursion bottoms out at order = nClasses-1.
    *
    *   steps done:
    *   (1) filter by conditions on this class
    *   (2) extend results vector by properties in this class to be output
    *   (3) if this is not the last class in the strategy, find nodes representing
    *       objects in the next class linked by the linking association,
    *       (to one of the previous classes)
    *       and call this recursively for all those objectTokens
    *   (4) if this is the last class in the strategy, write out a row of the answer
    */
    void doClassStep(int order, Vector<objectToken> previousOReps, 
    		boolean mergeDuplicates)
    throws MapperException, QueryStrategyException
    {
        // latest class in the strategy
    	QueryClass lastClass = strategy.bestStrategy().get(order);
        String className = lastClass.className();
        trace("Class step, order " + order + "; class "+ className);
        // objectToken for latest object in the strategy
        objectToken oRep = previousOReps.elementAt(order);
        // filter by conditions on this class
        trace("Conditions " + testConditions(lastClass,oRep,previousOReps));
        if (testConditions(lastClass,oRep,previousOReps))
        {
            // if there are any more classes left in the strategy...
            if (order < strategy.bestStrategy().size() - 1)
            {
                // find associated objects in the next class of the strategy (or one empty objectToken in some cases)
            	Vector<objectToken> nextObjs = nextobjectTokenVect(previousOReps,order);
                for (int i = 0; i < nextObjs.size(); i++)
                {
                	objectToken nextObj = (objectToken)nextObjs.elementAt(i);

                	// clone as this Vector will be extended several times; then add one new ObjectToken to it
                    Vector<objectToken> nextOReps = new Vector<objectToken>();
                    for (Iterator<objectToken> it = previousOReps.iterator();it.hasNext();)nextOReps.add(it.next());
                    nextOReps.addElement(nextObj);

                    // recurse for each associated object found in the next class
                    doClassStep(order + 1, nextOReps,mergeDuplicates);
                }
            }
            else // if this is the last class in the strategy
            {
            	trace("found a row");
                // store one row of the result vector
                objectTable.add(previousOReps);
            }
        }
    }
    

    /**
     * test all conditions on the objectToken of the QueryClass which can be tested,
     * given the partial result of the strategy.
     * Some conditions cannot yet be tested because the required objects are not yet in the partial
     * result; those conditions will be tested later in the evaluation
     * @param theClass
     * @param theToken
     * @param partialResult
     * @return
     * @throws MapperException
     */
    private boolean testConditions(QueryClass theClass, objectToken theToken, Vector<objectToken> partialResult)
    throws MapperException
    {
    	boolean passes = true;
    	// any condition on a class not represented must fail; but we do not yet know there are any
    	// find each condition which depends on this QueryClass and evaluate it
    	for (Iterator<QueryCondition> it  = parser.conditions().iterator();it.hasNext();)
    	{
    		QueryCondition condition = it.next();
    		// this class is the left hand side of a condition, relating it to a constant or a property of some other class
    		if (condition.queryClass().equals(theClass)) 
    		{
    			if (theToken.isEmpty()) passes = false;
    			else passes = passes && condition.evaluate(strategy.bestStrategy(), partialResult);
    		}
    		// this class is the right hand side of a condition, relating it to a property of some other class
    		if ((condition.otherQueryClass() != null) && (condition.otherQueryClass().equals(theClass)))
    		{
    			if (theToken.isEmpty()) passes = false;
    			else passes = passes && condition.evaluate(strategy.bestStrategy(), partialResult);
    		}
    	}
    	return passes;
    }


    /**
     * 
     * @param previousOReps a Vector of objectTokens 0..order for previous classes in the strategy.
     * @param order = 0...(N-2) where N is the number of distinct classes in the query.
     * @return a Vector of possible objectTokens to go at position (order + 1) in a Vector of partial results
     * @throws MapperException
     * @throws QueryStrategyException
     */
    private Vector<objectToken> nextobjectTokenVect(Vector<objectToken> previousOReps, int order)
    throws MapperException, QueryStrategyException
    {
        Vector<objectToken> res = new Vector<objectToken>();

        // find the link association to the next class from one of the previous classes
        LinkAssociation link = strategy.getLink(order + 1);
        
        // case where there is a link association - follow it
        if (link != null)
        {
            // find the objectToken for the start class, by checking against the strategy
            objectToken oRep = getObjectToken(previousOReps,link.startClass());
            
            // following an association from an empty objectToken leads to just one empty objectToken
            if (oRep.isEmpty())
            {
            	res.add(new EObjectRep());
            }
            // otherwise try to follow the association
            else if (!oRep.isEmpty()) try
            {
                res = reader().getAssociatedObjectTokens(oRep, link.assocName(), link.endClass().className(),  link.startEnd());
                trace("Got " + res.size() + " associated objects of class '" + link.endClass().className() + "'");      	
            }

            // if the association is not represented in the data source, return one empty ObjectToken
            catch (notRepresentedException ex) {res.add(new EObjectRep());}
        }
        
        // case where there is no link association - get all objectReps of the next class, for any subset
        else if (link == null)
        {
        	try
        	{
        		QueryClass nextClass = strategy.bestStrategy().get(order + 1);
            	res = reader().getAllObjectTokens(nextClass.className());
        	}
            // if the class is not represented in the data source, return one empty ObjectToken
            catch (notRepresentedException ex) {res.add(new EObjectRep());}
        }
        

        return res;
    }
    
    /**
     * 
     * @param partialResult
     * @param theClass
     * @return the objectToken for a QueryClass, taken from a partial or complete result row
     * @throws QueryStrategyException
     */
    private objectToken getObjectToken(Vector<objectToken> partialResult, QueryClass theClass) throws QueryStrategyException
    {
        objectToken oRep = null;
        for (int i = 0; i < partialResult.size(); i++)
        	if (strategy.bestStrategy().get(i).equals(theClass)) oRep = partialResult.get(i);
        if (oRep == null)  {throw new QueryStrategyException("objectToken for class '" + theClass.className()+ " not found during query execution");}
    	return oRep;
    }

    //-----------------------------------------------------------------------------------------------------------------------
	//                   calculation of query results - adding to a table of result values, from a table of objects
    //----------------------------------------------------------------------------------------------------------------------- 
    
    /**
     * add the property values for objects in one objectTable to the resultTable
     */
    private void addToResultTable(boolean mergeDuplicates) throws QueryStrategyException, MapperException
    {
    	trace("Object rows: " + objectTable.size());
    	// step though rows of the object table
    	for (int row = 0; row < objectTable.size(); row++)
    	{
    		Vector<objectToken> objectRow = objectTable.get(row);
    		Vector<String[]> resultRow = new Vector<String[]>();
    		
    		// go over columns - cells of the result row
    		for (int col = 0; col < parser.writeFields().size();col++)
    		{
    			WriteField field = parser.writeFields().get(col);
    			String[] resultCell = new String[3];
    			resultCell[0] = field.queryClass().className(); // qualified class name
    			resultCell[1] = field.propName();
    			objectToken oRep = getObjectToken(objectRow,field.queryClass());
    			// empty objectTokens give '--' for all properties
    			if (oRep.isEmpty()) resultCell[2] = "--";
    			else try
    			{
    				resultCell[2] = reader().getPropertyValue(oRep, field.propName());
    				if (forceUpperCase) resultCell[2] = resultCell[2].toUpperCase();
    			}
    			// use '--' for any property not represented in the data source
    			catch (notRepresentedException ex) {resultCell[2] = "--";}
    			
    			// trace("adding result cell");
    			resultRow.add(resultCell);
    		}
    		
			// trace("adding result row");
    		// store the complete result row
    		storeOneResultRow(resultRow,mergeDuplicates);
    	}
    }
    
    
    
    /**
     * store one row of the result vector, eliminating duplicates if mergeDuplicates = true
     * @param resultRow
     * @param mergeDuplicates
     */
    private void storeOneResultRow(Vector<String[]>resultRow, boolean mergeDuplicates)
    {
    	// store the result, maybe merging duplicates
    	String key = resultKey(resultRow,mergeDuplicates);
        resultTable.put(key,resultRow); 

    	// track the number of merged duplicate rows
    	Integer count = countTable.get(key);
    	if (count == null) count = new Integer(0);
    	countTable.put(key, new Integer(count.intValue() + 1));
    }
    
    /**
     * make a key for storing results to detect identical query results.
     * It is a string of the concatenated values of all cells in the row.
     * results is a Vector of String[3], where the last element[2] is the value.
     * if mergeDuplicates = false, use an increasing result index to make all results 
     * unique and not to merge duplicates
     */
    private String resultKey(Vector<String[]> results, boolean mergeDuplicates)
    {
        String res = "";
        for (int i = 0; i < results.size(); i++)
        {
            String[] result = (String[])results.elementAt(i);
            res = res + "$%" + result[2]; /// separator is unlikely to occur in data
        }
        if (!mergeDuplicates)
        {
        	res = res + "%" + resultIndex;
        	resultIndex++;
        }
        return res;
    }

    


//-------------------------------------------------------------------------------------
//                                  trivia
//-------------------------------------------------------------------------------------

    public void message(String s) {System.out.println(s);}
    
    private void trace(String s) {if (tracing) message(s);}

}
