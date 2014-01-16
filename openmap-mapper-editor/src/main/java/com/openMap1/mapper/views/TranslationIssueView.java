package com.openMap1.mapper.views;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

import com.openMap1.mapper.core.TranslationIssue;
import com.openMap1.mapper.core.TranslationSummaryItem;
import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.util.XMLUtil;

/**
 * tabular view of detailed issues arising in translation tests
 * 
 * @author robert
 *
 */
public class TranslationIssueView extends ViewPart 
implements ISelectionChangedListener,SortableView,SaveableView {

	public TableViewer tableViewer() {return viewer;}
	private TableViewer viewer;
	
	private Table table;
	
	private RowSorter tiSorter;
	public RowSorter rowSorter() {return tiSorter;}
	
	public Vector<String> columnHeaders() {return columnHeaders;}
	private Vector<String> columnHeaders = new Vector<String>();
	
	private Vector<TranslationIssue> result;
	public void setResult(Vector<TranslationIssue> result)  {this.result = result;}

	/** the label provider for the view */
	public ITableLabelProvider labelProvider() {return new TranslationIssueTableLabelProvider();}


	private Action saveTranslationIssuesAction;

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
			viewer.setLabelProvider(new TranslationIssueTableLabelProvider());
			table = viewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			tiSorter = new RowSorter(viewer, this);
			viewer.setSorter(tiSorter);
			makeTableColumns();
		}
		
		class TranslationIssueTableLabelProvider extends LabelProvider implements ITableLabelProvider
		{
			public String getColumnText(Object element, int index)
			{
				String text = "";
				if ((element instanceof TranslationIssue))
					text = ((TranslationIssue)element).cellContents(index);
				return text;
			}
			
			public Image getColumnImage(Object element, int index) {return null;}
			
		}
	
		public void makeTableColumns()
		{
			Vector<Comparator<String>> comparators = new Vector<Comparator<String>>();
			Vector<TableColumn> usedTableColumns = new Vector<TableColumn>();
			columnHeaders = new Vector<String>();
			for (int col = 0; col< TranslationIssue.columnTitle.length; col++)
			{
				TableColumn column = new TableColumn(table,SWT.LEFT);
				usedTableColumns.add(column);
				String headerName = TranslationIssue.columnTitle[col];
				column.setText(headerName);
				columnHeaders.add(headerName);
				column.setWidth(TranslationIssue.columnWidth[col]);

				Comparator<String> comp = 
					RowSorter.comparatorForType(TranslationIssue.sortType()[col]);
				comparators.add(comp);
				
			}
			tiSorter.initialiseForResult(usedTableColumns, comparators);
		}

		
		/**
		 * Having set up the column headers, show a new set of results.
		 * @param results
		 */
		public  void showNewResult(Vector<TranslationIssue> results)
		{
			// if there are any previous results showing, remove them
			table.removeAll();

			setResult(results); // resets the cached result
			
			// add new rows
			for (Iterator<TranslationIssue> it = result.iterator();it.hasNext();)
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
			for (Iterator<TranslationIssue> it = result.iterator();it.hasNext();)
				viewer.add(it.next());
			
		}
		
		//---------------------------------------------------------------------------------------------
		//                Listening for the selection of a new row in the Transaction Summary View View
		//---------------------------------------------------------------------------------------------
		
		public void selectionChanged(SelectionChangedEvent event)
		{
			ISelection selection = event.getSelection();
			if (selection instanceof IStructuredSelection && ((IStructuredSelection)selection).size() == 1) {
				Object object = ((IStructuredSelection)selection).getFirstElement();
				if (object instanceof TranslationSummaryItem)
				{
					TranslationSummaryItem tsi = (TranslationSummaryItem)object;
					showNewResult(tsi.translationIssues());
					//viewer.getControl().setVisible(true);
					WorkBenchUtil.page().activate(this);
				}
			}				
		}
		
		//---------------------------------------------------------------------------------------------
		//                                Saving the view
		//---------------------------------------------------------------------------------------------

		
		private void makeActions()
		{
			saveTranslationIssuesAction = new Action() {
				public void run() {
					doSaveViewContents();
				}
			};
			saveTranslationIssuesAction.setText("Save Translation Issues");
			saveTranslationIssuesAction.setToolTipText("Save the translation issues as XML or tab-separated values");
			saveTranslationIssuesAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		}

		
		private void doSaveViewContents()
		{
			ViewSaver vs = new ViewSaver(this,"Save test results",
					"Choose a new file in which to save the translation issue view");
			vs.saveResults();		
		}
		

		private void contributeToActionBars() {
			IActionBars bars = getViewSite().getActionBars();
			fillLocalPullDown(bars.getMenuManager());
		}

		private void fillLocalPullDown(IMenuManager manager) {
			manager.add(saveTranslationIssuesAction);
		}

		
		public Element getViewContents(Document doc)
		{
			ViewSaver vs = new ViewSaver(this,"Title not shown",
					"Title not shown");
			return vs.resultDOM(doc);		
		}
		
		/** @return the menu item to save the contents of the view.
		 * This view can also be saved as 
		 * part of the saved test summary view, in  the header */
		public Action saveViewContentsAction() 
		{
			TranslationIssueView tiv = WorkBenchUtil.getTranslationIssueView(false);
			return tiv.saveViewContentsAction();
		}

		/** 
		 * When this view is saved,  it can be part of the header of the translation 
		 * summary view, which has other header information; or it can be saved on its own
		 * Only the sort orders are stored in this header
		 */
		public void fillHeaderElement(Document doc, Element header)
		{
			try{
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
			}
			catch (XMLException ex) {System.out.println("Cannot write translation issue view header: " + ex.getMessage());}							
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
