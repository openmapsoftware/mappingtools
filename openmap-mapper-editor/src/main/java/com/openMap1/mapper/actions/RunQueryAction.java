package com.openMap1.mapper.actions;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;
import java.io.InputStream;
import java.awt.Color;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.core.resources.IFile;

import com.openMap1.mapper.query.DataSource;
import com.openMap1.mapper.query.MatchResult;
import com.openMap1.mapper.query.QueryManager;
import com.openMap1.mapper.query.QueryParser;
import com.openMap1.mapper.query.CellContent;


import com.openMap1.mapper.views.QueryResultView;
import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.WorkBenchUtil;

import com.openMap1.mapper.presentation.QueryEditor;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * Action to run the current query on the active data sources in the Data
 * Source View, and either 
 * (a) show the results in the Query Results View; or
 * (b) save the rests in one csv file per data source
 * 
 * This is the abstract superclass of two classes, to do each of these.
 * 
 * @author robert
 *
 */

public abstract class RunQueryAction extends Action{
	
	private DataSourceView dataSourceView = null;
	
	private QueryEditor queryEditor = null;
	
	private String queryText = null;
	
	private int maxQuerySize = 10000; // who would write a query bigger than 10000 characters?
	
	protected boolean tracing = false;
	
	// all errors detected when parsing, defining strategy, or executing the query
	protected Vector<String[]> errors;
	
	public RunQueryAction()
	{
		super();
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		// setActionDefinitionId("RunQuery");
	}
	
	/**
	 * the Data Source View must be set before run()
	 * @param dataSourceView
	 */
	public void setDataSourceView(DataSourceView dataSourceView)
		{this.dataSourceView = dataSourceView;}
	
	/**
	 * 
	 * @param text The query text, which  must be set before running
	 */
	public void setQueryText(String text)
		{queryText = text;}
	
	public void setQueryEditor(QueryEditor queryEditor)
		{this.queryEditor = queryEditor;}
	
	static int SHOW_RESULTS_IN_VIEW = 0;
	static int SAVE_RESULTS_IN_FILES = 1;
	static int SHOW_SQL = 2;
	static int SAVE_RDB_XML = 3;
	
	abstract int function();

	public void run() {
		
		// initialise the list of errors
		errors = new Vector<String[]>();

		// check there are some active query sources
		if (dataSourceView == null)
		{
			recordError("", "Cannot run query; there is no data source view open",errors);
			writeErrors(errors);
			return;
		}

		/* Try to refresh all data sources. If any cannot be refreshed, they 
		 * are made inactive.  true means allow the user to try a new user name and password, for a relational source */
		dataSourceView.refreshAllActiveSources(true);
		
		Vector<DataSource> activeQuerySources = dataSourceView.getActiveDataSources();
		if (activeQuerySources.size() == 0)
		{
			recordError("","Cannot run query; there are no active data sources",errors);
			writeErrors(errors);
			return;
		}
		
		trace("Active data sources: " + activeQuerySources.size());
		
		
		// get hold of the query text
		if (queryEditor != null) try
		{
			// save the current state of the query
			queryEditor.doSave(null);
			// get the saved file
			IEditorInput input = queryEditor.getEditorInput();
			if (input instanceof FileEditorInput)
			{
				IFile savedFile = ((FileEditorInput)input).getFile();
				InputStream stream = savedFile.getContents();
				byte[] bytes = new byte[maxQuerySize];
				int chars = stream.read(bytes);
				setQueryText(new String(bytes,0,chars));

				if ((function() == SHOW_RESULTS_IN_VIEW)||(function() == SHOW_SQL)||(function() == SAVE_RDB_XML))
					evaluateAndMergeQuery(queryText,activeQuerySources,WorkBenchUtil.getQueryResultView(true),errors, tracing);
				else if (function() == SAVE_RESULTS_IN_FILES)
					evaluateAndSaveQuery(queryText,activeQuerySources);
				
				if (function() == SHOW_SQL) showSQLQueries();
				if (function() == SAVE_RDB_XML) saveRDBXML();
			}
			else throw new MapperException("Editor input is not from a file");
		}
		catch (Exception ex) 
		{
			if (ex.getMessage() == null) 
				GenUtil.surprise(ex,"RunQueryAction.run");
			recordError("", "Cannot evaluate query: " +ex.getMessage(),errors);
		}
	}
	
	protected void evaluateAndSaveQuery(String queryText,Vector<DataSource> dataSources) throws MapperException
	{
		throw new MapperException("method should have been overridden");
	}
	
	/**
	 * 
	 * @param queryText the text of a query
	 * @param dataSources Vector of data sources it is to be evaluated against
	 * @throws MapperException
	 */
	public static void evaluateAndMergeQuery(String queryText,Vector<DataSource> dataSources, 
			QueryResultView queryResultView,Vector<String[]> errors, boolean tracing) 
	throws MapperException
	{
		boolean mergeDuplicates = true;
		QueryParser queryParser = QueryManager.evaluateQuery(queryText,dataSources, errors,tracing,mergeDuplicates);
		
		// the count column must be numerically sortable; only use it when there is just one data source
		boolean addCountColumn = (dataSources.size() == 1);
		
        Hashtable<String,Vector<CellContent>> mergedResults = new Hashtable<String,Vector<CellContent>>();
        for (Iterator<DataSource> iq = dataSources.iterator();iq.hasNext();)
        {
        	DataSource dataSource = iq.next();
        	if (dataSource.hasResult())
        	{
        		mergeOneResult(dataSource,mergedResults,addCountColumn);
        	}        	
        }
        
		if ((mergedResults.size() > 0) && (errors.size() == 0))
			displayUnorderedResult(mergedResults, queryParser,queryResultView,queryText,addCountColumn);
		else if (mergedResults.size() == 0)
			recordError("", "Query delivered no result rows from any data source",errors);
		
		if (errors.size() > 0) writeErrors(errors);
        
	}
	
	/**
	 * (for fast matching)
	 * display the results of a query on one or two data sources, un-merged,
	 * limiting to a maximum number of records
	 * @param queryText
	 * @param queryParser
	 * @param dataSources
	 * @param queryResultView
	 * @param maxRowsPerSource
	 */
	public static void displayUnMergedResults(String queryText,QueryParser queryParser,Vector<DataSource> dataSources, 
			QueryResultView queryResultView,int maxRowsPerSource)
	{
        

        Hashtable<String,Vector<CellContent>> addedResults = new Hashtable<String,Vector<CellContent>>();
        for (Iterator<DataSource> iq = dataSources.iterator();iq.hasNext();)
        {
        	DataSource dataSource = iq.next();
        	if (dataSource.hasResult())
        	{
        		addOneResult(dataSource,addedResults,maxRowsPerSource);
        	}        	
        }
        
		if (addedResults.size() > 0)
		try
		{
	        // the count column is not used for match results
	        boolean addCountColumn = false;
			displayUnorderedResult(addedResults, queryParser,queryResultView,queryText,addCountColumn);					
		}
		catch (MapperException ex) 
		{
			WorkBenchUtil.showMessage("Error", ex.getMessage());
			ex.printStackTrace();
		}
	}

	

	/**
	 * add the query results from one data source to the set of merged results from all sources - for query results;
	 * merging if necessary with identical result rows from other sources
	 * @param qs
	 * @param mergedResults
	 * @param addCountColumn
	 */
	private static void mergeOneResult(DataSource qs,Hashtable<String,Vector<CellContent>> mergedResults,boolean addCountColumn)
	{
		for (Iterator<String> it = qs.keyedResults().keySet().iterator();it.hasNext();)
		{
			String rowKey = it.next();
			Vector<CellContent> row = qs.keyedResults().get(rowKey);
			Vector<CellContent> existingRow = mergedResults.get(rowKey);
			
			/* this exact row has not occurred before from any source. Mark it
			 * with the short name of this source in its first cell, and store it in the merged results */
			if (existingRow == null)
			{
				Vector<CellContent> newRow = new Vector<CellContent>();
				
				// cell with code of this data source 
				CellContent firstCell = new CellContent(qs.getCode(),Color.black);
				newRow.add(firstCell);
				
				// cells with query result content
				for (Iterator<CellContent> ir = row.iterator();ir.hasNext();) newRow.add(ir.next());

				// only add a count column when there is just one data source, so no previous row like this one
				if (addCountColumn)
				{
					Integer count = qs.getResultCount(rowKey);
					CellContent lastCell = new CellContent(count.toString(),Color.black);
					newRow.add(lastCell);
				}
				
				// store the row
				mergedResults.put(rowKey, newRow);
			}
			/* If the row has been found previously, append the short name of this source 
			 * onto the short names of 
			 * previous sources which have given the identical row. */
			else if (existingRow != null)
			{
				existingRow.get(0).setText(existingRow.get(0).getText() + qs.getCode());
			}
		}
	}

	
	
	/**
	 * add the result rows from one data source, with no possible merge with
	 * result rows from other data sources, up to a maximum number of rows
	 * @param qs the data source
	 * @param addedResults
	 * @param maxRows
	 */
	private static void addOneResult(DataSource qs,Hashtable<String,Vector<CellContent>> addedResults, int maxRows)
	{
		int resultRows = 0;
		for (Iterator<String> it = qs.keyedResults().keySet().iterator();it.hasNext();)
		{
			String rowKey = it.next();
			if (resultRows > maxRows)  return;			
			
			// mark the row as coming from this data source
			Vector<CellContent> row = qs.keyedResults().get(rowKey);
			Vector<CellContent> newRow = MatchResult.addFrontCell(row,qs.getCode());
			addedResults.put(rowKey, newRow);
			resultRows++;
		}
	}

	
	private static void displayUnorderedResult(Hashtable<String,Vector<CellContent>> mergedResults, 
			QueryParser queryParser, QueryResultView queryResultView, String queryText, boolean addCountColumn)	throws MapperException
	{
		queryResultView.setQueryText(queryText);
		queryResultView.makeTableColumns(queryParser.getColumnHeaders(true), false, addCountColumn); // false = not a fast match result display
		queryResultView.showNewUnorderedResult(mergedResults);
		WorkBenchUtil.page().activate(queryResultView);
	}
	
	private static void writeErrors(Vector<String[]> errors)
	{
		QueryResultView queryResultView = WorkBenchUtil.getQueryResultView(true);
		queryResultView.writeErrors(errors);
		WorkBenchUtil.page().activate(queryResultView);
	}
	
	
	private static void recordError(String code,String message, Vector<String[]> errors)
	{
		String[] errorRow = new String[2];
		errorRow[0] = code;
		errorRow[1] = message;
		errors.add(errorRow);		
	}
	
	private void trace(String s) {if (tracing) System.out.println(s);}
	
	/**
	 * 
	 */
	private void showSQLQueries()
	{
		int shown = 0;
		for (Iterator<DataSource> it = dataSourceView.getActiveDataSources().iterator();it.hasNext();)
		{
			DataSource ds = it.next();
			if (ds.isRelational())
			{
				WorkBenchUtil.showMessage("SQL for relational data source " + ds.getCode(), ds.getSqlText());
				shown++;
			}
		}
		if (shown == 0) WorkBenchUtil.showMessage("Error", "There are no active relational data sources to show their query SQL");
	}
	
	/**
	 * 
	 */
	private void saveRDBXML() throws MapperException
	{
		int shown = 0;
		for (Iterator<DataSource> it = dataSourceView.getActiveDataSources().iterator();it.hasNext();)
		{
			DataSource ds = it.next();
			if (ds.isRelational())
			{
				String[] types = {"*.xml"};
				String filePath = FileUtil.getFilePathFromUser(queryEditor,types , ("Save XML from RDBMS source " + ds.getCode()), true);
				if ((filePath != null) && (filePath.length() > 1)) 
					XMLUtil.writeOutput(ds.getRootNode().getOwnerDocument(), filePath, true);
				shown++;
			}
		}
		if (shown == 0) WorkBenchUtil.showMessage("Error", "There are no active relational data sources to save their XML");
	}


}
