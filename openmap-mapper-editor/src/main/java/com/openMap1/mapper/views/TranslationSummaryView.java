package com.openMap1.mapper.views;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.TranslationSummaryItem;
import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.util.XMLUtil;

/**
 * tabular view of the summary results of a translation test.
 * 
 * @author robert
 *
 */
public class TranslationSummaryView extends ViewPart implements SortableView,SaveableView {

	public TableViewer tableViewer() {return viewer;}
	private TableViewer viewer;
	
	/** the label provider for the view */
	public ITableLabelProvider labelProvider() {return new TranslationSummaryTableLabelProvider();}

	private Table table;
	
	private RowSorter tiSorter;
	public RowSorter rowSorter() {return tiSorter;}
	
	public Vector<String> columnHeaders() {return columnHeaders;}
	private Vector<String> columnHeaders = new Vector<String>();
	
	private Vector<TranslationSummaryItem> result;
	public void setResult(Vector<TranslationSummaryItem> result)  {this.result = result;}

	private Action saveTestSummaryAction;
	

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

		
		public void createViewer(Composite parent)
		{
			viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			viewer.setLabelProvider(new TranslationSummaryTableLabelProvider());
			table = viewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			tiSorter = new RowSorter(viewer, this);
			viewer.setSorter(tiSorter);
			makeTableColumns();
			
			// connect the issues view to listen to changes to this view
			 connectToTranslationIssueView();
		}
		/**
		 * hook up the translation issue view so it can listen to selections in this view.
		 * (a protected method in case that view is made last, and needs to call this method)
		 * @param qe
		 */
		protected void connectToTranslationIssueView() 
		{
			TranslationIssueView tiv = WorkBenchUtil.getTranslationIssueView(false);
			if (tiv != null) viewer.addPostSelectionChangedListener(tiv);
		}
		
		class TranslationSummaryTableLabelProvider extends LabelProvider implements ITableLabelProvider
		{
			public String getColumnText(Object element, int index)
			{
				String text = "";
				if ((element instanceof TranslationSummaryItem))
					text = ((TranslationSummaryItem)element).cellContents(index);
				return text;
			}
			
			public Image getColumnImage(Object element, int index) {return null;}
			
		}
	
		public void makeTableColumns()
		{
			Vector<Comparator<String>> comparators = new Vector<Comparator<String>>();
			Vector<TableColumn> usedTableColumns = new Vector<TableColumn>();
			columnHeaders = new Vector<String>();
			for (int col = 0; col< TranslationSummaryItem.columnTitle.length; col++)
			{
				TableColumn column = new TableColumn(table,SWT.LEFT);
				usedTableColumns.add(column);
				String headerName = TranslationSummaryItem.columnTitle[col];
				column.setText(headerName);
				columnHeaders.add(headerName);
				column.setWidth(TranslationSummaryItem.columnWidth[col]);

				Comparator<String> comp = 
					RowSorter.comparatorForType(TranslationSummaryItem.sortType()[col]);
				comparators.add(comp);
				
			}
			tiSorter.initialiseForResult(usedTableColumns, comparators);
		}

		
		/**
		 * Having set up the column headers, show a new set of results.
		 * @param results
		 */
		public  void showNewResult(Vector<TranslationSummaryItem> results)
		{
			// in case you have not managed to do this already...
			 connectToTranslationIssueView();

			 // if there are any previous results showing, remove them
			table.removeAll();

			setResult(results); // resets the cached result
			
			// add new rows
			for (Iterator<TranslationSummaryItem> it = result.iterator();it.hasNext();)
				viewer.add(it.next());
		}
		
		/**
		 * Use the locally cached results to show them again - with a new sort order
		 */
		public void showResultAgain()
		{
			// if there are any previous results showing, remove them
			table.removeAll();

			// add the same rows again to the view, to sort
			for (Iterator<TranslationSummaryItem> it = result.iterator();it.hasNext();)
				viewer.add(it.next());
			
		}
		
		
		//---------------------------------------------------------------------------------------------
		//                            Menu and methods for saving test results
		//---------------------------------------------------------------------------------------------
		
		private void makeActions()
		{
			saveTestSummaryAction = new Action() {
				public void run() {
					doSaveViewContents();
				}
			};
			saveTestSummaryAction.setText("Save Test Results");
			saveTestSummaryAction.setToolTipText("Save the results of the tests as XML or tab-separated values");
			saveTestSummaryAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		}
		
		private void doSaveViewContents()
		{
			ViewSaver vs = new ViewSaver(this,"Save test results",
					"Choose a new file in which to save the test results, as shown in the translation summary and translation issue views");
			vs.saveResults();		
		}
		

		private void contributeToActionBars() {
			IActionBars bars = getViewSite().getActionBars();
			fillLocalPullDown(bars.getMenuManager());
		}

		private void fillLocalPullDown(IMenuManager manager) {
			manager.add(saveTestSummaryAction);
		}

		/** @return the menu item to save the contents of the view */
		public Action saveViewContentsAction() {return saveTestSummaryAction;}
		
		public void fillHeaderElement(Document doc, Element header)
		{
			try {				
				// save the set of defined sort columns
				Element sortElement = XMLUtil.newElement(doc, "Sort");
				header.appendChild(sortElement);
				for (int s = 0; s < tiSorter.selectedSorters().size();s++)
				{
					SortInfo si = tiSorter.selectedSorters().get(s);
					Element column = XMLUtil.newElement(doc, "Column");
					column.setAttribute("index", new Integer(si.columnIndex()).toString());
					if (si.descending()) column.setAttribute("descending","true");
					sortElement.appendChild(column);
				}

				// the header of this save file includes the save file for the data source view
				DataSourceView dataSourceView = WorkBenchUtil.getDataSourceView(true);
				ViewSaver vs = new ViewSaver(dataSourceView,"Title not shown",
						"Title not shown");
				header.appendChild(vs.resultDOM(doc));

				// the header of this save file includes the saved translation issues view
				TranslationIssueView translationIssueView = WorkBenchUtil.getTranslationIssueView(true);
				ViewSaver vt = new ViewSaver(translationIssueView,"Title not shown",
						"Title not shown");
				header.appendChild(vt.resultDOM(doc));
			}
			catch (XMLException ex) {System.out.println("Cannot write translation summary view header: " + ex.getMessage());}
		}
		
		public Element getViewContents(Document doc)
		{
			ViewSaver vs = new ViewSaver(this,"Title not shown",
					"Title not shown");
			return vs.resultDOM(doc);		
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

