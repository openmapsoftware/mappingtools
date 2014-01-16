package com.openMap1.mapper.views;

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

import com.openMap1.mapper.mapping.DebugRow;
import com.openMap1.mapper.reader.DebugPostBox;

/**
 * Shows the trace of debugging activity, with one row for each step.
 * 
 * @author robert
 *
 */

public class DebugView extends ViewPart 
implements SaveableView{

	private Action saveDebugViewAction;
	private Action stepAction;
	private Action quitAction;
	private Action runAction;

	private boolean tracing = false;
	
	public TableViewer tableViewer() {return viewer;}
	private TableViewer viewer;
	
	private Table table;
	
	private RowSorter tiSorter;
	public RowSorter rowSorter() {return tiSorter;}
	
	public Vector<String> columnHeaders() {return columnHeaders;}
	private Vector<String> columnHeaders = new Vector<String>();
	
	private Vector<DebugRow> result;
	public void setResult(Vector<DebugRow> result)  {this.result = result;}
	

	/** the label provider for the view */
	public ITableLabelProvider labelProvider() {return new DebugTableLabelProvider();}
	

	public DebugPostBox getDebugPostBox() {return debugPostBox;}
	private DebugPostBox debugPostBox = null;
	
	private DebugInstanceView debugInstanceView;
	public void setDebugInstanceView (DebugInstanceView debugInstanceView)
		{this.debugInstanceView = debugInstanceView;}
	

	
	//---------------------------------------------------------------------------------------
	//                               Initialisation
	//---------------------------------------------------------------------------------------

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
			viewer.setLabelProvider(new DebugTableLabelProvider());
			table = viewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			viewer.setSorter(tiSorter);
			makeTableColumns();
			
		}
		
		class DebugTableLabelProvider extends LabelProvider implements ITableLabelProvider
		{
			public String getColumnText(Object element, int index)
			{
				String text = "";
				if ((element instanceof DebugRow))
					text = ((DebugRow)element).cellContents(index);
				return text;
			}
			
			public Image getColumnImage(Object element, int index) {return null;}
			
		}
	
		public void makeTableColumns()
		{
			Vector<TableColumn> usedTableColumns = new Vector<TableColumn>();
			columnHeaders = new Vector<String>();
			for (int col = 0; col< DebugRow.columnTitle.length; col++)
			{
				TableColumn column = new TableColumn(table,SWT.LEFT);
				usedTableColumns.add(column);
				String headerName = DebugRow.columnTitle[col];
				column.setText(headerName);
				columnHeaders.add(headerName);
				column.setWidth(DebugRow.columnWidth[col]);

				
			}
		}

		
		/**
		 * Having set up the column headers, show a new set of results.
		 * @param results
		 */
		public  void showNewResult(Vector<DebugRow> results)
		{
			// if there are any previous results showing, remove them
			table.removeAll();

			setResult(results); // resets the cached result
			
			// add new rows
			for (Iterator<DebugRow> it = result.iterator();it.hasNext();)
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
			for (Iterator<DebugRow> it = result.iterator();it.hasNext();)
			{
				DebugRow row = it.next();
				viewer.add(row);							
			}
		}
		
		
		/**
		 * Get the next debug row form the debug post box and show it.
		 * At the same time, fill in the result of doing the last debug row
		 */
		public void showNextRow()
		{
				DebugRow newRow = debugPostBox.getDebugRow();
				if (newRow != null)
				{
					if (result.size() > 0) setResultOfLastRow(debugPostBox.getLastResult());
					newRow.setIndex(result.size() + 1);
					result.add(newRow);
					showResultAgain();
					// scroll to ensure the user can see the last few rows
					viewer.reveal(newRow);
					debugPostBox.setDebugRow(null);
				}
		}
		
		/**
		 * put a result in the latest row of the debug view - but do not show it yet
		 * @param newResult the result to go in the latest row of the debug view
		 */
		public void setResultOfLastRow(String newResult)
		{
			DebugRow lastRow = result.get(result.size()-1);
			lastRow.setResult(newResult);
			debugPostBox.setLastResult("");
		}
		
	
	//---------------------------------------------------------------------------------------------
	//                            Menu and methods 
	//---------------------------------------------------------------------------------------------
	
	private void makeActions()
	{
		saveDebugViewAction = new Action() {
			public void run() {
				doSaveViewContents();
			}
		};
		saveDebugViewAction.setText("Save Debug View");
		saveDebugViewAction.setToolTipText("Save the Debug view");
		saveDebugViewAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		

		stepAction = new Action() {
			public void run() {
				doStep();
			}
		};
		stepAction.setText("Step");
		stepAction.setToolTipText("Step within this mapping");

		runAction = new Action() {
			public void run() {
				doRun();
			}
		};
		runAction.setText("Run");
		stepAction.setToolTipText("Run to next breakpoint mapping");

		quitAction = new Action() {
			public void run() {
				doQuit();
			}
		};
		quitAction.setText("Terminate");
		quitAction.setToolTipText("Terminate Debug");

	}
	
	private void doSaveViewContents()
	{
		ViewSaver vs = new ViewSaver(this,"Save Debug View",
				"Choose a new file in which to save the debug view");
		vs.saveResults();		
	}
	
	@SuppressWarnings("deprecation")
	public void doStep()
	{
		if ((!debugPostBox.getCompleted())  && (!debugPostBox.getTerminated()))
		{
	    	debugPostBox.setHalted(false);
			debugPostBox.getReaderThread().resume();
			awaitHaltOrResult();			
		}
	}

	@SuppressWarnings("deprecation")
	public void doRun()
	{
		if ((!debugPostBox.getCompleted())  && (!debugPostBox.getTerminated()))
		{
	    	debugPostBox.setHalted(false);
	    	debugPostBox.setRunOn(true);
			debugPostBox.getReaderThread().resume();
			awaitHaltOrResult();			
		}
	}

	@SuppressWarnings("deprecation")
	public void doQuit()
	{
		debugPostBox.setTerminated(true);
		debugPostBox.getReaderThread().stop();
		debugPostBox.setDebugRow(new DebugRow(DebugRow.TERMINATED)); 
    	showNextRow();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(saveDebugViewAction);
		manager.add(stepAction);
		manager.add(runAction);
		manager.add(quitAction);
	}

	/** @return the menu item to save the contents of the view */
	public Action saveViewContentsAction() {return saveDebugViewAction;}
	
	/** for the interface SaveableView; needs do nothing */
	public void fillHeaderElement(Document doc, Element header)
	{}
	
	//---------------------------------------------------------------------------------------------
	//                                Debugging
	//---------------------------------------------------------------------------------------------
	
	/**
	 * Give access to the Debug Postbox, to pick up new rows to display
	 * @param debugPostBox
	 */
	public void setDebugPostbox(DebugPostBox debugPostBox) {this.debugPostBox = debugPostBox;}
	
    /**
     * Go into a wait loop, waiting for a signal from the reader thread that
     * it has either halted on a break point, or completed, or terminated. 
     * In the first case, write the latest row to the debug view and complete.
     * In the second and third cases, write a special row and complete.
     * @param debugView
     * @param debugPostBox
     */
	@SuppressWarnings("deprecation")
	public void awaitHaltOrResult()
    {
    	while ((!debugPostBox.getCompleted()) 
    			&& (!debugPostBox.getHalted())
			    && (!debugPostBox.getTerminated()))
    	{
    		try {wait(10);}
        	catch (Exception ex) {}
    	}
    	if (debugPostBox.getCompleted())
    	{
    		debugPostBox.setDebugRow(new DebugRow(DebugRow.COMPLETED)); 
        	showNextRow();
        	debugPostBox.getReaderThread().stop();
    	}
    	if (debugPostBox.getTerminated())
    	{
			debugPostBox.getReaderThread().stop();
    	}
    	else if (debugPostBox.getHalted())
    	{
    		debugInstanceView.showSelectedNode(debugPostBox.getDebugNode());
    		debugPostBox.setDebugNode(null);
        	showNextRow();
        	debugPostBox.setHalted(false);
			WorkBenchUtil.page().activate(WorkBenchUtil.getDebugInstanceView(true));
			if (debugPostBox.getRunOn()) doStep();
    	}
    	trace("finished wait");
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
	
	private void trace(String s) {if (tracing) System.out.println(s);}
	

}
