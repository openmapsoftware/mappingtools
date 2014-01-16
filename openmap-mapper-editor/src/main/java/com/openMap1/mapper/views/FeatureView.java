package com.openMap1.mapper.views;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.jface.viewers.ISelectionChangedListener; 
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.openMap1.mapper.actions.MakeITSMappingsAction;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.structures.ITSAssociation;
import com.openMap1.mapper.structures.ITSAttribute;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.MappedStructure;

/**
 * superclass of AttributeView and AssociationView.
 * 
 * @author robert
 *
 */

public abstract class FeatureView extends ViewPart 
implements ISelectionChangedListener, SortableView{

	protected CheckboxTableViewer viewer;
	
	protected Table table;
	
	protected EClass selectedClass;
	
	protected LabelledEClass selectedLabelledClass;
	
	protected Hashtable<String,EStructuralFeature> features;
	
	protected Hashtable<String,Boolean> checkedFeatures;
	
	protected Vector<Vector<String>> featureRows = new Vector<Vector<String>>();
	
	protected RowSorter qrSorter;
	public RowSorter rowSorter() {return qrSorter;}
	
	protected ClassModelView classModelView() {return WorkBenchUtil.getClassModelView(false);}
	
	protected boolean isRMIMClassModel;
	
	protected boolean tracing = false;
	protected void trace(String s) {if (tracing) System.out.println(s);}
	
	//---------------------------------------------------------------------------------------------
	//                              Defining editable columns
	//---------------------------------------------------------------------------------------------
	
	// hard-wired assumptions about names and column indexes of editable columns
	int[] editableColumnNumbers ={1,6};
	String[] editableColumnNames = {"Business Name","Value"};
	
	boolean isEditable(String property) {return(GenUtil.inArray(property, editableColumnNames));}

	boolean isEditable(int column)
	{
		boolean editable = false;
		for (int i = 0; i < editableColumnNumbers.length; i++)
			if (column == editableColumnNumbers[i]) editable = true; 
		return editable;			
	}
	
	int editableColumnIndex(String property)
	{
		int index = -1;
		for (int i = 0; i < editableColumnNames.length; i++)
			if (property.equals(editableColumnNames[i])) index = editableColumnNumbers[i];
		return index;
	}
	
	protected CellEditor[] editors;

	
	//---------------------------------------------------------------------------------------------
	//                                Initialisation
	//---------------------------------------------------------------------------------------------


	/**
	 * Callback to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		viewer = new CheckboxTableViewer(table);
		ICheckStateListener listener = new CheckStateListener();
		viewer.addCheckStateListener(listener);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		qrSorter = new RowSorter(viewer, this);
		viewer.setSorter(qrSorter);
		String[] properties = setColumnHeaders();

		// define a property name for each column (which is the column header)
		viewer.setColumnProperties(properties);
		// define the cell editors for each column (only the business name and value column editors work)
		editors = editors(table, properties.length);
		viewer.setCellEditors(editors);
		// define how the editors get and change the values shown in the cells
		viewer.setCellModifier(new CellModifier());

		// make the actions that will be items on the menu of this view
		makeActions();

		// attach the menu to this view
		contributeToActionBars();
	}
	
	/**
	 * CellModifier class to make only some columns text-editable
	 * @author robert
	 */
	class CellModifier implements ICellModifier 
	{
		
		/* only the 'Business Name' and 'Value' columns are editable, and then only when the class model view 
		 * is attached to a mapping set made from an ITS. */
		public boolean canModify(Object element, String property) 
		{ 
			return ((isEditable(property))&& canEditEcoreModel()); 
		}
		
		public Object getValue(Object element, String property) 
		{
			Object value = "";
			if ((isEditable(property)) && (element instanceof Vector<?>))
				value = ((Vector<?>)element).get(editableColumnIndex(property));
			trace("\nValue of '" + property + "' is '" + value + "' at column " + editableColumnIndex(property));
			if (value == null) value="";
			return (String)value;
		}
		
		@SuppressWarnings("unchecked")
		// this seems never to get called; I never see the trace
		public void modify(Object element, String property, Object value) 
		{
			trace("call to modify");
			if ((isEditable(property)) && (element instanceof Vector<?>)) 
			{
				Vector<String> vec = (Vector<String>)element;
				vec.set(editableColumnIndex(property),(String)value );
			}
		}

	}
	
	/* subclasses AttributeView and AssociationView must define 
	 * the headers for the columns */
	abstract protected String[] setColumnHeaders();

	
	/* define an array of editors for each column of the view. 
	 * Only the editors for the business name and value columns are used */
	private CellEditor[] editors(Composite parent, int cols) 
	{
		CellEditor[] editors = new CellEditor[cols];
		for (int c =0; c < cols; c++)
		{
			TextCellEditor ed = new TextCellEditor(parent);
			ed.addListener(new CellEditorListener(ed));
			editors[c] = ed;
		}
		return editors;
	}
	
	class CellEditorListener implements ICellEditorListener
	{
		private TextCellEditor ed;
		
		CellEditorListener(TextCellEditor ed)
		{
			this.ed = ed;
		}
		
		int editorColumnIndex()
		{
			int index = -1;
			for (int i = 0; i< editors.length; i++)
				if (ed.equals(editors[i])) index = i;
			return index;
		}
		
		/* When the user presses return or moves away from the cell editor, 
		 * record the new business name in the Ecore model */
		@SuppressWarnings("unchecked")
		public void applyEditorValue() 
		{
			trace("Apply editor value to column " + editorColumnIndex());
			Object rowObj = viewer.getElementAt(table.getSelectionIndex());
			if (rowObj instanceof Vector<?>)
			{
				Vector<String> row = (Vector<String>)rowObj;
				String oldValue = row.get(editorColumnIndex());
				String newValue = (String)ed.getValue();
				trace("Old: '" + oldValue + "'; new: '" + newValue + "'");
				
				// record the edited name in the row to be displayed
				row.set(editorColumnIndex(),newValue );
				
				boolean changed = false;
				if ((oldValue == null) && (!("".equals(newValue)))) changed = true;
				if ((oldValue != null) && (!(oldValue.equals(newValue)))) changed = true;
				
				// record the edited name in the Ecore model, only if there is a change
				if (changed)
				{
					EStructuralFeature feature = features.get(rowKey(row));
					trace("Changing feature " + feature.getName());
					if (feature instanceof EAttribute)
					{
						AttributeView.applyNewValueToFeature(feature,newValue,editorColumnIndex(),selectedLabelledClass );											
					}
					else if (feature instanceof EReference)
					{
						AssociationView.applyNewValueToFeature(feature,newValue,editorColumnIndex(),selectedLabelledClass );											
					}
				}
				else trace("No change");
				
				// refresh the whole view, and force the refreshed row to be shown
				showResultAgain(rowObj);
			}
		}
		
		// if the user cancels the cell editing, do nothing
		public void cancelEditor() {}
		
		// do nothing at each keystroke in the cell editor
		public void editorValueChanged(boolean oldValidState,
                boolean newValidState) {}
				
	}
	
	
	/**
	 * do something whenever a check button is checked or unchecked
	 * @author robert
	 */
	class CheckStateListener implements ICheckStateListener
	{
		public CheckStateListener() {}
		
		public void checkStateChanged(CheckStateChangedEvent event)
		{
			boolean checkState = event.getChecked();
			Object checkedObject = event.getElement();
			// only handle the check event if the Ecore model can have edits with its current mapping set
			if ((checkedObject instanceof Vector<?>) && (canEditEcoreModel()))
			{
				String key = rowKey((Vector<?>)checkedObject);
				EStructuralFeature feature = features.get(key);
				handleCheckEvent(feature,checkState,selectedLabelledClass);
				checkedFeatures.put(key, new Boolean(checkState));
				
				// show the results again, and make sure the checked row is visible
				showResultAgain(checkedObject);
			}
		}
	}
	
	// see AttributeView and AssociationView for overrides
	public static  void handleCheckEvent(EStructuralFeature feature, boolean checkState, LabelledEClass lClass) 
	{
		if (feature instanceof EAttribute) AttributeView.handleCheckEvent(feature, checkState, lClass);
		if (feature instanceof EReference) AssociationView.handleCheckEvent(feature, checkState, lClass);
	}

	/**
	 * 
	 * @param row the fields on a row
	 * @return a key for the EAttribute or EReference, which is 
	 * unique and does not depend on fields which might be edited
	 */
	protected String rowKey(Vector<?> row)
	{
		String key = "";
		for (int i = 2; i < row.size(); i++) if (!isEditable(i))
		{
			Object obj = row.get(i);
			if (obj instanceof String) key = key + (String)obj + "$";
		}
		return key;
	}
	
	//---------------------------------------------------------------------------------------------
	//                                 Annotations 
	//---------------------------------------------------------------------------------------------
	
	public static String microITSURIStart = "urn:hl7-org:v3/microITS/";

	public static String microITSURI() 
	{
		String mappingSetName = "";
		try
		{
			String uri = WorkBenchUtil.getClassModelView(false).mappingSetURI().toString();
			StringTokenizer st = new StringTokenizer(uri,"/");
			while(st.hasMoreTokens()) mappingSetName = st.nextToken();			
		}
		catch (Exception ex)  {} // in case mappingSetURI() is null
		return (microITSURIStart + mappingSetName);
	}
	
	/**
	 * 
	 * @return true if the Ecore model in the class model view has already been annotated with some 
	 * other mapping set, not the mapping set the model is currently attached to.
	 */
	public static boolean hasBeenAnnotatedForOtherMappingSet()
	{
		boolean hasBeenAnnotated  = false;
		// fail-safe in case of null anything; return false
		ClassModelView classModelView = WorkBenchUtil.getClassModelView(false);
		if (classModelView != null)
		{
			LabelledEClass entryClass = classModelView.topLabelledEClass();
			if (entryClass != null)
			{
				/* loop over all associations of the entry class. Any simplification edit will
				 * have propagated up to annotate one of these associations */
				for (Iterator<EReference> it = entryClass.eClass().getEAllReferences().iterator(); it.hasNext();)
				{
					EReference ref = it.next();
					// check all annotations of each association
					for (Iterator<EAnnotation> ir = ref.getEAnnotations().iterator(); ir.hasNext();)
					{
						EAnnotation ann = ir.next();
						// check all microITS annotations
						if (ann.getSource().startsWith(microITSURIStart))
						{
							// if the annotation is not labelled for the current mapping set, return true.
							if (!ann.getSource().equals(microITSURI())) hasBeenAnnotated = true;
						}
					}
				}
			}
		}
		return hasBeenAnnotated;
	}
	
	/**
	 * 
	 * @return true only if the class model view is attached to a Mapper Editor,
	 * and the mapping set of the mapper editor was made from an ITS,
	 * and this ecore model has not been annotated for another mapping set
	 */
	public boolean canEditEcoreModel()
	{
		boolean canEdit = false;
		MapperEditor mapperEditor = WorkBenchUtil.getClassModelView(false).mapperEditor();
		if (mapperEditor != null)
		{
			MappedStructure mappedStructure = WorkBenchUtil.mappingRoot(mapperEditor);
			canEdit = (MakeITSMappingsAction.isMadeFromITS(mappedStructure) && (!hasBeenAnnotatedForOtherMappingSet()));
		}
		return canEdit;
	}

	/**
	 * 
	 * @param toObj a Ecore model element to which an annotation is either to be added,
	 * or extended with a new String key and value
	 * @param key the key (new or possibly already existing)
	 * @param value the new value for the key
	 */
	public static void addMicroITSAnnotation(EModelElement toObj, String key, String value)
	{
		EAnnotation ann = toObj.getEAnnotation(microITSURI());
		if (ann == null)
		{
			ann = EcoreFactory.eINSTANCE.createEAnnotation();
			ann.setSource(microITSURI());
			toObj.getEAnnotations().add(ann);			
		}		
		ann.getDetails().put(key, value);
	}
	
	/**
	 * remove a micro-ITS annotation
	 * @param toObj
	 * @param key
	 */
	public static void removeMicroITSAnnotation(EModelElement toObj, String key)
	{
		EAnnotation ann = toObj.getEAnnotation(microITSURI());
		if (ann != null)
		{
			ann.getDetails().removeKey(key);
			if (ann.getDetails().size() == 0)
			{
				toObj.getEAnnotations().remove(ann);
			}
		}		
	}

	
	/**
	 * @param toObj an Ecore model element
	 * @param key the key to an annotation detail
	 * @return the value for that key, or null if there is none
	 */
	public static String getMicroITSAnnotation(EModelElement toObj, String key)
	{
		String value = null;
		EAnnotation ann = toObj.getEAnnotation(microITSURI());
		if (ann != null) value = ann.getDetails().get(key);
		return value;
	}
	
	public static ITSAttribute getITSAttribute(EAttribute ea, String key)
	{
		ITSAttribute itsa = new ITSAttribute();
		String val = getMicroITSAnnotation(ea,key);
		if (val != null) try {itsa = new ITSAttribute(val);}
		catch (Exception ex) {}
		return itsa;
	}

	
	public static ITSAssociation getITSAssociation(EReference er, String key)
	{
		ITSAssociation itsa = new ITSAssociation();
		String val = getMicroITSAnnotation(er,key);
		if (val != null) try {itsa = new ITSAssociation(val);}
		catch (Exception ex) {}
		return itsa;
	}

	
	/**
	 * @param feature an EReference or EAttribute
	 * @return from its EAnnotation, whether it should be checked or not
	 */
	protected Boolean isChecked(EStructuralFeature feature)
	{
		boolean checked = false;
		if (selectedLabelledClass != null)
		{
			String path = selectedLabelledClass.getPath();
			String note = getMicroITSAnnotation(feature,path);
			if ((note != null) && (note.startsWith("T:"))) checked = true;			
		}
		return new Boolean(checked);
	}
	
	/**
	 * add a row to a viewer, and make the row checked or unchecked
	 * @param row
	 */
	protected void addToViewer(Vector<String> row)
	{
		viewer.add(row);
		Boolean checked = checkedFeatures.get(rowKey(row));
		if (checked == null) trace("Missing row key " + rowKey(row));
		else viewer.setChecked(row, checked.booleanValue());
	}
	
	//---------------------------------------------------------------------------------------------
	//                Listening for the selection of a new class in the Class Model View
	//---------------------------------------------------------------------------------------------
	
	public void selectionChanged(SelectionChangedEvent event)
	{
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection && ((IStructuredSelection)selection).size() == 1) {
			Object object = ((IStructuredSelection)selection).getFirstElement();

			// vanilla class model view
			if (object instanceof EClass)
			{
				isRMIMClassModel = false;
				selectedClass = (EClass)object;
				selectedLabelledClass = null;
				updateForSelection(selectedClass);
			}

			// RMIM class model view
			else if (object instanceof LabelledEClass)
			{
				isRMIMClassModel = true;
				selectedLabelledClass = (LabelledEClass)object;
				selectedClass = selectedLabelledClass.eClass();
				updateForSelection(selectedLabelledClass);
			}
		}				
	}
	
	abstract protected void updateForSelection(EClass selectedClass);

	abstract protected void updateForSelection(LabelledEClass selectedLablledClass);
	
	/**
	 * Use the locally cached results to show them again - possibly with a new sort order
	 */
	public void showResultAgain()
	{
		// if there are any previous results showing, remove them
		table.removeAll();

		// add new rows to the view, and set their check boxes
		for (Iterator<Vector<String>> it = featureRows.iterator();it.hasNext();)
			addToViewer(it.next());
	}
	
	/** 
	 * show the results again, and ensure the window shows a particular row
	 * (that has just been edited)
	 * @param rowObj
	 */
	public void showResultAgain(Object rowObj)
	{
		showResultAgain();
		
		StructuredSelection sel = new StructuredSelection(rowObj);
		viewer.setSelection(sel, true);
	}

	
	/**
	 * Add a column to the view and store information to later tell the row sorter about it. 
	 * After this has been called for all columns, RowSorter.initialiseForResult(cols, comps)
	 * should be called.
	 * @param title title of the column
	 * @param width width of the column
	 * @param comp Comparator used for sorting the column; may be null if the column is not sortable
	 * @param comps Vector of non-null Comparators
	 * @param cols Vector of table columns that are sortable (have non-null Comparators)
	 * @return the column name
	 */
	protected String addColumn(String title, int width ,
			Comparator<String> comp,
			Vector<Comparator<String>> comps,
			Vector<TableColumn> cols)
	{
		TableColumn t0 = new TableColumn(table,SWT.LEFT);
		t0.setText(title);
		t0.setWidth(width);
		// only add to the sorter information if a comparator has been supplied
		if (comp != null)
		{
			comps.add(comp);
			cols.add(t0);					
		}
		return title;
	}


	//---------------------------------------------------------------------------------------------
	//                            Menu and methods 
	//---------------------------------------------------------------------------------------------
	
	abstract protected void makeActions();

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	abstract protected void fillLocalPullDown(IMenuManager manager);
	
	

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
