package com.openMap1.mapper.query;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.ecore.EPackage;

import com.openMap1.mapper.core.MapperException;

public class QueryManager {

	
	/**
	 * Evaluate a query against a number of data sources, and set the results in each data source.
	 * @param queryText
	 * @param dataSources
	 * @param errors
	 * @param tracing
	 * @return  the query parser used for one of the data sources
	 * @throws MapperException
	 */
	public static QueryParser evaluateQuery(String queryText,
			Vector<DataSource> dataSources, 
			Vector<String[]> errors, 
			boolean tracing,
			boolean mergeDuplicates) 
	throws MapperException
	{
        boolean parsable = true;

        /* we now use the same query parser for all sources;
        all data sources must have the same class model, so use the first. */
        EPackage classModel = dataSources.get(0).getMappedStructure().getClassModelRoot();
        QueryParser queryParser = new QueryParserImpl_Ecore(classModel,"X",errors,tracing);
    	parsable = queryParser.parse(queryText);
    	
    	//writeParserResult(queryParser);
    	
    	QueryStrategy queryStrategy = new QueryStrategyImpl(queryParser);
		queryStrategy.defineStrategy();
       
        
        for (Iterator<DataSource> it = dataSources.iterator();it.hasNext();)
        {
        	DataSource dataSource = it.next();
        	dataSource.unsetResult();
        	
        	if (parsable) try
        	{
        		dataSource.setColumnHeaders(queryParser.getColumnHeaders(false));
        		QueryExecutor queryExecutor = new QueryExecutor(dataSource, queryParser, queryStrategy, false);
        		queryExecutor.executeQuery(queryText,mergeDuplicates);
        		dataSource.setResult(queryExecutor.resultVector(), queryExecutor.countVector());
        	}
        	catch (Exception ex)
        	{
        		ex.printStackTrace();
        		recordError(dataSource.getCode(),ex.getMessage(),errors);
        	}
        }
        
        return queryParser;
	}
	
	
	public static void recordError(String code,String message, Vector<String[]> errors)
	{
		String[] errorRow = new String[2];
		errorRow[0] = code;
		errorRow[1] = message;
		errors.add(errorRow);		
	}
	
	
	static void writeParserResult(QueryParser queryParser)
	{
		message("\nQuery classes " + queryParser.queryClasses().size());
		for (int i = 0; i < queryParser.queryClasses().size();i++)
			message(queryParser.queryClasses().get(i).identifier());

		message("\nLink associations " + queryParser.linkAssociations().size());
		for (Enumeration<LinkAssociation> en = queryParser.linkAssociations().elements();en.hasMoreElements();)
		{
			LinkAssociation next = en.nextElement();
			message("from " + next.startClass().identifier() + " to " + next.endClass().identifier());
		}
	}

		static void message(String s) {System.out.println(s);}

}
