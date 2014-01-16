package com.openMap1.mapper.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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

import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.mapping.OpenMappingRow;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.Timer;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;

/**
 * A tabular view of all the mappings in the mapping set being edited.
 * 
 * @author robert
 *
 */

public class MappingsView extends ViewPart 
implements ISelectionChangedListener,SortableView,SaveableView{

	public TableViewer tableViewer() {return viewer;}
	private TableViewer viewer;
	
	private Table table;
	
	private RowSorter tiSorter;
	public RowSorter rowSorter() {return tiSorter;}
	
	public Vector<String> columnHeaders() {return columnHeaders;}
	private Vector<String> columnHeaders = new Vector<String>();
	
	private Vector<OpenMappingRow> result;
	public void setResult(Vector<OpenMappingRow> result)  {this.result = result;}


	/** the label provider for the view */
	public ITableLabelProvider labelProvider() {return new MappingsTableLabelProvider();}
	
	private MapperEditor mapperEditor = null;

	private Timer timer = new Timer("Mappings View");

	private Action saveMappingsViewAction;

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
			viewer.setLabelProvider(new MappingsTableLabelProvider());
			table = viewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			tiSorter = new RowSorter(viewer, this);
			viewer.setSorter(tiSorter);
			makeTableColumns();
			
		}
		
		class MappingsTableLabelProvider extends LabelProvider implements ITableLabelProvider
		{
			public String getColumnText(Object element, int index)
			{
				String text = "";
				if ((element instanceof OpenMappingRow))
					text = ((OpenMappingRow)element).cellContents(index);
				return text;
			}
			
			public Image getColumnImage(Object element, int index) {return null;}
			
		}
	
		public void makeTableColumns()
		{
			Vector<Comparator<String>> comparators = new Vector<Comparator<String>>();
			Vector<TableColumn> usedTableColumns = new Vector<TableColumn>();
			columnHeaders = new Vector<String>();
			for (int col = 0; col< OpenMappingRow.columnTitle.length; col++)
			{
				TableColumn column = new TableColumn(table,SWT.LEFT);
				usedTableColumns.add(column);
				String headerName = OpenMappingRow.columnTitle[col];
				column.setText(headerName);
				columnHeaders.add(headerName);
				column.setWidth(OpenMappingRow.columnWidth[col]);

				Comparator<String> comp = 
					RowSorter.comparatorForType(OpenMappingRow.sortType()[col]);
				comparators.add(comp);
				
			}
			tiSorter.initialiseForResult(usedTableColumns, comparators);
		}

		
		/**
		 * Having set up the column headers, show a new set of results.
		 * @param results
		 */
		public  void showNewResult(Vector<OpenMappingRow> results)
		{
			// if there are any previous results showing, remove them
			table.removeAll();

			setResult(results); // resets the cached result
			
			// add new rows
			for (Iterator<OpenMappingRow> it = result.iterator();it.hasNext();)
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
			for (Iterator<OpenMappingRow> it = result.iterator();it.hasNext();)
				viewer.add(it.next());			
		}
		
		//---------------------------------------------------------------------------------------------
		//                            Menu and methods for saving mappings
		//---------------------------------------------------------------------------------------------
		
		private void makeActions()
		{
			saveMappingsViewAction = new Action() {
				public void run() {
					doSaveViewContents();
				}
			};
			saveMappingsViewAction.setText("Save Mappings View");
			saveMappingsViewAction.setToolTipText("Save the Mappings view as XML");
			saveMappingsViewAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		}
		
		private void doSaveViewContents()
		{
			ViewSaver vs = new ViewSaver(this,"Save Mappings View",
					"Choose a new file in which to save the mappings view");
			vs.saveResults();		
		}
		

		private void contributeToActionBars() {
			IActionBars bars = getViewSite().getActionBars();
			fillLocalPullDown(bars.getMenuManager());
		}

		private void fillLocalPullDown(IMenuManager manager) {
			manager.add(saveMappingsViewAction);
		}

		/** @return the menu item to save the contents of the view */
		public Action saveViewContentsAction() {return saveMappingsViewAction;}
		
		
		//---------------------------------------------------------------------------------------------
		//               
		//---------------------------------------------------------------------------------------------

		/**
		 * connect the view to a Mapper Editor and its mapping set
		 */
		public void initiateForMapperEditor(MapperEditor me)
		{
			mapperEditor = me;

			viewer.addSelectionChangedListener
			(new ISelectionChangedListener() {
				 public void selectionChanged(SelectionChangedEvent event) {
					 handleMappingSelection(event.getSelection());
				 }
			 });

			noteMappings();
			timer.start(Timer.SHOW_MAPPINGS);
			showResultAgain();
			timer.stop(Timer.SHOW_MAPPINGS);
			// timer.report();
		}
		
		/**
		 * tell the Mapper Editor which mapping has been selected, so it will open 
		 * on that mapping
		 * @param selection the selection of a row in this view
		 */
		private void handleMappingSelection(ISelection selection)
		{
			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				Iterator<?> selectedElements = ((IStructuredSelection)selection).iterator();
				if (selectedElements.hasNext()) {
					Object selectedElement = selectedElements.next();
					if (selectedElement instanceof OpenMappingRow)
					{
						OpenMappingRow selectedRow = (OpenMappingRow)selectedElement;
						ArrayList<Object> selectionList = new ArrayList<Object>();
						// pass the underlying mapping or import EObject to the mapper editor
						if (selectedRow.mapping() != null)
							selectionList.add(selectedRow.mapping());
						else if (selectedRow.importSet() != null)
							selectionList.add(selectedRow.importSet());
						mapperEditor.selectionViewer().setSelection(new StructuredSelection(selectionList));
					}
				}
			}
		}
		
		public void selectionChanged(SelectionChangedEvent event)
		{
			ISelection selection = event.getSelection();
			if (selection instanceof IStructuredSelection && ((IStructuredSelection)selection).size() == 1) {
				Object object = ((IStructuredSelection)selection).getFirstElement();
				System.out.println(object.getClass().getName());
			}
			noteMappings();
			showResultAgain();
		}
		
		private void noteMappings()
		{
			if (mapperEditor != null)
			{
				result = new Vector<OpenMappingRow>();
				MappedStructure ms = WorkBenchUtil.mappingRoot(mapperEditor);
				
				// make rows for mappings
				for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(ms,MapperPackage.Literals.MAPPING).iterator();it.hasNext();)
				{
					EObject next = it.next();
					// make rows for object, association end, and property mappings
					if ((next instanceof Mapping) 
							&& (!(next instanceof AssocMapping)))
					{
						OpenMappingRow mr = new OpenMappingRow((Mapping)next);
						result.add(mr);
					}
				}

				// make rows for import mapping sets
				for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(ms,MapperPackage.Literals.IMPORT_MAPPING_SET).iterator();it.hasNext();)
				{
					EObject next = it.next();
					OpenMappingRow mr = new OpenMappingRow((ImportMappingSet)next);
					result.add(mr);														
				}
			}
		}

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
			catch (XMLException ex) {System.out.println("Cannot write mappings view header: " + ex.getMessage());}							
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
