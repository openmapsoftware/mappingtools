package com.openMap1.mapper.actions;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.core.resources.IFile;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.query.CellContent;
import com.openMap1.mapper.query.DataSource;
import com.openMap1.mapper.query.QueryManager;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.views.WorkBenchUtil;

public class RunQueryToFileAction extends RunQueryAction {

	public RunQueryToFileAction()
	{
		super();
		setText("Save Query Results");
		setToolTipText("Run this query, using the currently active data sources, and save the results in csv files");
	}

	/**
	 *  to tell the superclass what to do in run()
	 */
	int function() {return SAVE_RESULTS_IN_FILES;}
	
	/**
	 * 
	 */
	protected void evaluateAndSaveQuery(String queryText,Vector<DataSource> dataSources) throws MapperException
	{
		// evaluate the query and put the results in each active data source
		boolean mergeDuplicates = true;
		QueryManager.evaluateQuery(queryText,dataSources, errors,tracing,mergeDuplicates);
		
		for (int i = 0; i < dataSources.size(); i++)
		{
			DataSource dataSource = dataSources.get(i);
			
			// ask for a file to save the results
			String title = "File to save results for data source " + dataSource.getCode();
			IFile theFile = WorkBenchUtil.makeOutputIFile(title, title);
			if (theFile != null)
			{
				//WorkBenchUtil.showMessage("Saving data", "Source " + dataSource.getCode());
				writeResults(theFile,dataSource);
			}
		}
		
	}
	
	/**
	 * 
	 * @param theFile
	 * @param dataSource
	 * @throws MapperException
	 */
	private void writeResults(IFile theFile, DataSource dataSource) throws MapperException
	{
		Vector<String[]> rows = new Vector<String[]>();
		
		// make header row of csv file
		Vector<CellContent> headerRow = dataSource.getHeaderRow();
		String[] csvHeadRow = new String[headerRow.size() + 2];
		csvHeadRow[0] = "Codes";
		for (int col = 0; col < headerRow.size(); col++) csvHeadRow[col+ 1] = headerRow.get(col).getText();
		csvHeadRow[headerRow.size() + 1] = "Count";
		rows.add(csvHeadRow);

		// make data rows of csv file
		for (Enumeration<String> en = dataSource.keyedResults().keys(); en.hasMoreElements();)
		{
			String key = en.nextElement();
			Vector<CellContent> row = dataSource.keyedResults().get(key);

			// fill in a data row
			String[] csvRow = new String[row.size() + 2];
			csvRow[0] = dataSource.getCode();
			for (int col = 0; col < row.size(); col++) csvRow[col+ 1] = row.get(col).getText();
			csvRow[row.size() + 1] = dataSource.getResultCount(key).toString();
			rows.add(csvRow);
		}
		
		// write out the file
		EclipseFileUtil.writeCSVFile(rows, theFile);
	}



}
