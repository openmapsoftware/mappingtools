package com.openMap1.mapper.views;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EAttribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import com.openMap1.mapper.query.MatchResult;
import com.openMap1.mapper.query.CellContent;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.XMLException;

/**
 * A tabular view of the results of the last query run.
 * 
 * @author robert
 *
 */

public class QueryResultView extends ViewPart implements SortableView,SaveableView {

	private TableViewer viewer;
	
	private Table table;
	
	private Hashtable<String,Vector<CellContent>> result;
	public void setResult(Hashtable<String,Vector<CellContent>> result) {this.result = result;}
	
	private RowSorter qrSorter;
	public RowSorter rowSorter() {return qrSorter;}
	
	private Vector<String> columnHeaders = new Vector<String>();
	
	protected Action saveQueryResultAction;
	
	private String queryText = "";
	public void setQueryText(String text) {queryText = text;}
	
	private DataSourceView dataSourceView;
	/** for use in testing to stop it going to the workbench to get the view */
	public void setDataSourceView(DataSourceView dataSourceView)
		{this.dataSourceView = dataSourceView;}
	
	
	
	//---------------------------------------------------------------------------------------------
	//                                Initialisation
	//---------------------------------------------------------------------------------------------


	/**
	 * Callback to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		// create the viewer and its table
		createViewer(parent);

		// make the actions that will be items on the menu of this view
		makeActions();

		// attach the menu to this view
		contributeToActionBars();
		
	}
	
	/** this method is used in testing, to create a viewer,
	 * without trying to contribute to the action bars
	 */ 
	public void createViewer(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setLabelProvider(new QueryResultTableLabelProvider());
		table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		qrSorter = new RowSorter(viewer, this);
		viewer.setSorter(qrSorter);		
	}

	class QueryResultTableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object element, int index)
		{
			String text = "";
			if ((element instanceof Vector<?>) && (index < ((Vector<?>)element).size()))
			{
				Object obj = ((Vector<?>)element).elementAt(index);
				if (obj instanceof CellContent) text = ((CellContent)obj).getText();
			}
			return text;
		}
		
		public Image getColumnImage(Object element, int index) {return null;}
		
	}
	
	//---------------------------------------------------------------------------------------------
	//                                Receiving and showing query results
	//---------------------------------------------------------------------------------------------
	
	/**
	 * make table columns as follows:
	 * a column for the data source codes
	 * if addPairColumn = true (fast matching), a column for the pair number
	 * if addScoreColumn = true (fast matching), a column for the matching score
	 * The same set of table header names must be made by MatchResult.makeTableHeaders
	 */
	public void makeTableColumns(Vector<String[]> headers, boolean addMatchColumns, boolean addCountColumn) throws MapperException
	{
		// build these up to tell the query sorter which columns might be sorted on
		Vector<Comparator<String>> comparators = new Vector<Comparator<String>>();
		Vector<TableColumn> usedTableColumns = new Vector<TableColumn>();
		
		// clear previous column headers
		columnHeaders = new Vector<String>();
		for (int i = 0; i < table.getColumnCount(); i++) table.getColumn(i).setText("");
		
		// make or reuse the first column, for the codes of the query sources which contributed a row
		TableColumn firstCol = makeOrReuseColumn(0,"Codes", 60);
		comparators.add(RowSorter.stringComparator);
		usedTableColumns.add(firstCol);
		int extraColumns = 1;
		
		//make columns used for fast matching results
		if (addMatchColumns)
		{
			for (int i = 0; i < MatchResult.matchColumnNames.length; i++)
			{
				TableColumn pairCol = makeOrReuseColumn(extraColumns,MatchResult.matchColumnNames[i], 60);
				comparators.add(RowSorter.numberComparator);
				usedTableColumns.add(pairCol);
				extraColumns++;							
			}
		}
		
		// set other new column headers, reusing columns or making new ones
		for (int c = 0; c < headers.size(); c++)
		{
			String className= headers.get(c)[0];
			String attributeName= headers.get(c)[1];
			String header = className + "." + attributeName;
			TableColumn col = makeOrReuseColumn(extraColumns + c, header, 120);
			
			// choose the comparator for sorting based on the type of the EAttribute
			comparators.add(getComparator(className,attributeName));
			usedTableColumns.add(col);
		}
		
		// add a count column
		if (addCountColumn)
		{
			TableColumn lastCol = makeOrReuseColumn(extraColumns + headers.size(),"Count", 60);
			comparators.add(RowSorter.numberComparator);
			usedTableColumns.add(lastCol);			
		}

		
		// tell the query sorter about this set of columns
		qrSorter.initialiseForResult(usedTableColumns, comparators);
	}
	
	/**
	 * create a new table column, or reuse an existing one, 
	 * and set its parameters
	 * @param columnIndex
	 * @param columnName
	 * @param columnWidth
	 * @return the TableColumn
	 */
	private TableColumn makeOrReuseColumn(int columnIndex, String columnName, int columnWidth)
	{
		TableColumn col = null;
		if (columnIndex < table.getColumnCount()) col = table.getColumn(columnIndex); // reuse a column
		else {col = new TableColumn(table,SWT.LEFT);} // make a new column
		col.setText(columnName);
		columnHeaders.add(columnName);
		col.setWidth(columnWidth);
		return col;		
	}
	
	/**
	 * choose the comparator for sorting based on the type of the EAttribute
	 * @param className
	 * @param attributeName
	 * @return
	 */
	private Comparator<String> getComparator(String className, String attributeName)
	{
		// default: compare the Strings as Strings if anything goes wrong
		Comparator<String> comp = RowSorter.stringComparator;
		
		// catch any Exception (eg missing class model) silently
		try{
			EPackage classModel= WorkBenchUtil.getDataSourceView(true).getClassModelPackage();
			EClass ec = (EClass)classModel.getEClassifier(className);
			EAttribute ea = (EAttribute)ec.getEStructuralFeature(attributeName);
			String attTypeName = ea.getEAttributeType().getName();
			
			// cater for any non-String types that are recognised
			if (attTypeName.equals("EInt")) comp = RowSorter.numberComparator;
			
		}
		catch (Exception ex) {}
		return comp;
	}
	
	/**
	 * Having set up the column headers, show a new set of results.
	 * @param unorderedResults
	 */
	public  void showNewUnorderedResult(Hashtable<String,Vector<CellContent>> unorderedResults)
	{
		// if there are any previous results showing, remove them
		table.removeAll();

		setResult(unorderedResults); // resets the cached query result
		
		// add new rows
		for (Iterator<String> it = result.keySet().iterator();it.hasNext();) 
			{viewer.add(result.get(it.next()));}
	}
	
	/**
	 * Having set up the column headers, show a new set of results.
	 * @param unorderedResults
	 */
	public  void showNewOrderedResult(Vector<Vector<CellContent>> orderedResults)
	{
		// if there are any previous results showing, remove them
		table.removeAll();
		result = new Hashtable<String,Vector<CellContent>>();
		
		// add new rows, saving them in sortable form
		int order = 0;
		for (Iterator<Vector<CellContent>> it = orderedResults.iterator();it.hasNext();)
		{
			Vector<CellContent> next = it.next();
			result.put("k_"+ order, next);
			viewer.add(next);
			order++;
		}
	}
	
	/**
	 * Use the locally cached results to show them again - with a new sort order
	 */
	public void showResultAgain()
	{
		// if there are any previous results showing, remove them
		table.removeAll();

		// add new rows
		for (Iterator<String> it = result.keySet().iterator();it.hasNext();) 
			{viewer.add(result.get(it.next()));}
		
	}
	
	//---------------------------------------------------------------------------------------------
	//                            Menu and methods for saving query results
	//---------------------------------------------------------------------------------------------
	
	private void makeActions()
	{
		saveQueryResultAction = new Action() {
			public void run() {
				doSaveViewContents();
			}
		};
		saveQueryResultAction.setText("Save Query Results");
		saveQueryResultAction.setToolTipText("Save the results of the query as XML or tab-separated values");
		saveQueryResultAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

	}
	
	protected void doSaveViewContents()
	{
		ViewSaver vs = new ViewSaver(this,"Save query results",
				"Choose a new file in which to save the query results");
		vs.saveResults();		
	}
	
	
	
	public Element getViewContents(Document doc)
	{
		ViewSaver vs = new ViewSaver(this,"Title not shown",
				"Title not shown");
		return vs.resultDOM(doc);		
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(saveQueryResultAction);
	}
	
	//---------------------------------------------------------------------------------------------
	//                           Implementing the interface SaveableView
	//---------------------------------------------------------------------------------------------
	
	/** @return the menu item to save the contents of the view */
	public Action saveViewContentsAction() {return saveQueryResultAction;}
	
	/** headers of columns in the view  */
	public Vector<String> columnHeaders() {return columnHeaders;}
		
	/** the label provider for the view */
	public ITableLabelProvider labelProvider() {return new QueryResultTableLabelProvider();}
	
	/** the TableViewer for the view */
	public TableViewer tableViewer() {return viewer;}
	
	public void fillHeaderElement(Document doc, Element header)
	{
		try {
			// save query text
			Element queryTextElement = XMLUtil.textElement(doc, "QueryText", queryText);
			header.appendChild(queryTextElement);
			
			// save the set of defined sort columns
			Element sortElement = XMLUtil.newElement(doc, "Sort");
			header.appendChild(sortElement);
			for (int s = 0; s < qrSorter.selectedSorters().size();s++)
			{
				SortInfo si = qrSorter.selectedSorters().get(s);
				Element column = XMLUtil.newElement(doc, "Column");
				column.setAttribute("index", new Integer(si.columnIndex()).toString());
				if (si.descending()) column.setAttribute("descending","true");
				sortElement.appendChild(column);
			}

			// save the set of data sources used
			if (dataSourceView == null) dataSourceView = WorkBenchUtil.getDataSourceView(true);
			// the header of this save file includes the save file for the query source view
			ViewSaver vs = new ViewSaver(dataSourceView,"Title not shown",
					"Title not shown");
			header.appendChild(vs.resultDOM(doc));
		}
		catch (XMLException ex) {System.out.println("Cannot write query view header: " + ex.getMessage());}
	}

	/**
	 * for testing purposes, restore the selection of sort columns from 
	 * a saved query result view
	 * @param sortEl the <Sort> element of a saved query result view
	 */
	public void restoreSortColumns(Element sortEl)
	{
		qrSorter.restoreSortColumns(sortEl);
	}
	
	//---------------------------------------------------------------------------------------------
	//    Using the view to write out errors in parsing, strategising or executing the query
	//---------------------------------------------------------------------------------------------
	
	/**
	 * display messages for all errors found when parsing, finding strategy, or executing the query
	 */
	public void writeErrors(Vector<String[]> errors)
	{

		// if there are any previous results showing, remove them
		table.removeAll();

		String[] header = {"Code","Error"};
		int[] width = {40,500};

		setQueryText("Errors in query");

		// build these up to tell the query sorter which columns might be sorted on
		Vector<Comparator<String>> comparators = new Vector<Comparator<String>>();
		Vector<TableColumn> usedTableColumns = new Vector<TableColumn>();

		// clear previous column headers
		columnHeaders = new Vector<String>();
		for (int i = 0; i < table.getColumnCount(); i++) table.getColumn(i).setText("");
		int existingColumns = table.getColumnCount(); // may be more or less than you need
		
		for (int i = 0; i < 2; i++)
		{
			TableColumn col = null;
			if (i < existingColumns) col = table.getColumn(i); // reuse a column
			else {col = new TableColumn(table,SWT.LEFT);} // make a new column
			col.setText(header[i]);
			columnHeaders.add(header[i]);
			col.setWidth(width[i]);
			comparators.add(RowSorter.stringComparator);
			usedTableColumns.add(col);			
		}

		// tell the query sorter about this set of columns
		qrSorter.initialiseForResult(usedTableColumns, comparators);
		
		
		for (Iterator<String[]> it = errors.iterator();it.hasNext();)
		{
			String[] next = it.next();
			Vector<CellContent> row = new Vector<CellContent>();
			for (int i= 0; i < 2; i++) row.add(new CellContent(next[i]));
			viewer.add(row);
		}
				
	}
	
	//---------------------------------------------------------------------------------------------
	//                                Odds and ends
	//---------------------------------------------------------------------------------------------

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}


}
