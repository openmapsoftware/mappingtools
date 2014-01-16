package com.openMap1.mapper.views;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import org.eclipse.jface.viewers.ISelectionChangedListener; 

import com.openMap1.mapper.mapping.AssociationMapping;
import com.openMap1.mapper.structures.ITSAssociation;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.core.MapperException;

/**
 * Shows the associations of the currently selected class in
 * the Class Model View, and their mappings.
 * 
 * @author robert
 *
 */

public class AssociationView extends FeatureView 
implements ISelectionChangedListener, SortableView
{
	
	//---------------------------------------------------------------------------------------------
	//                                Initialisation
	//---------------------------------------------------------------------------------------------


	/**
	 * Callback to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		viewer.setLabelProvider(new AssociationTableLabelProvider());

		/* the next call was put in because I do not know in what order this view 
		 * and the class model view are created at startup, so I wanted to make the 
		 * link in either case. But it makes the class model view call itself recursively.
		 * Now the connection is made when a class model is attached to the class model view. */
		// if (classModelView() != null) classModelView().connectToAssociationView(this);
	}

	class AssociationTableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object element, int index)
		{
			String text = "";
			if ((element instanceof Vector<?>) && (index < ((Vector<?>)element).size()))
			{
				Object obj = ((Vector<?>)element).get(index);
				if (obj instanceof String) text = (String)obj;
			}
			return text;
		}
		
		public Image getColumnImage(Object element, int index) {return null;}
		
	}
	
		protected String[] setColumnHeaders()
	{
			// build these up to tell the query sorter which columns might be sorted on
			Vector<Comparator<String>> comps = new Vector<Comparator<String>>();
			Vector<TableColumn> cols = new Vector<TableColumn>();
			Vector<String> props = new Vector<String>();

			props.add(addColumn("Collapse", 60, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("Business Name", 120, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("Used", 50, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("Level", 50, RowSorter.numberComparator, comps,cols));
			props.add(addColumn("From Class", 100, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("Role", 100, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("Min", 35, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("Max", 35, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("To Class", 100, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("Inverse Role", 100, RowSorter.stringComparator, comps,cols));
			props.add(addColumn("Mappings", 300, RowSorter.stringComparator, comps, cols)); 

			// tell the query sorter about this set of columns
			qrSorter.initialiseForResult(cols, comps);
			
			// record the column names (properties) to enable cell editors
			String[] properties = new String[props.size()];
			for (int i = 0; i < props.size(); i++) properties[i] = props.get(i);
			return properties;
	}
	
	//---------------------------------------------------------------------------------------------
	//                Listening for the selection of a new class in the Class Model View
	//---------------------------------------------------------------------------------------------
	
	protected void updateForSelection(LabelledEClass selectedLabelledClass)
	{
		updateForSelection(selectedLabelledClass.eClass());
	}
	
	protected void updateForSelection(EClass selectedClass)
	{
		// if there are any previous results showing, remove them
		table.removeAll();

		features = new Hashtable<String,EStructuralFeature>();
		checkedFeatures = new Hashtable<String,Boolean>();
		featureRows = new Vector<Vector<String>>();
		setAssociations(selectedClass,selectedClass,0);

		// add new rows to the view, and set their check boxes
		for (Iterator<Vector<String>> it = featureRows.iterator();it.hasNext();)
			addToViewer(it.next());		
	}
	



	/**
	 * add to the associations vector 'features' the associations of this class, 
	 * then recursively add those of its superclasses, keeping
	 * track of the level of inheritance
	 * @param superClass
	 * @param level
	 */
	private void setAssociations(EClass originalClass, EClass superClass,int level)
	{
		for (Iterator<EReference> it = superClass.getEReferences() .iterator();it.hasNext();)
		{
			EReference er = it.next();
			ITSAssociation itsa = getITSAssociation(er,selectedLabelledClass);

			Vector<String> row = new Vector<String>();
			row.add(""); // collapse flag
			row.add(itsa.businessName()); // business name
			String used = "";
			if (itsa.attsIncluded()) used = "yes";
			if (itsa.childrenAreOrdered()) used = "ordered";
			row.add(used); // used flag
			row.add(new Integer(level).toString()); // level of inheritance
			row.add(superClass.getName());  // class the association is inherited from
			row.add(er.getName()); // role name to get to the other end
			// lower bound, as possibly constrained for this path
			row.add(new Integer(getConstrainedLowerBound(er,selectedLabelledClass)).toString());
			// upper bound, as possibly constrained for this path
			String maxVal = "1";
			if (getConstrainedUpperBound(er,selectedLabelledClass) == -1) maxVal = "*";
			row.add(maxVal);
			EClass otherEndClass = er.getEReferenceType();
			String className = "no class";
			if (otherEndClass != null) className = otherEndClass.getName();
			row.add(className); // class name at the other end
			if (er.getEOpposite() != null) // inverse role if it exists
				row.add(er.getEOpposite().getName());
			else row.add("-");
			row.add(getAssociationMappingText(originalClass,superClass,er.getName(),otherEndClass));
			
			// record the row, the EReference, and whether it is to be checked
			features.put(rowKey(row), er);
			featureRows.add(row);
			checkedFeatures.put(rowKey(row), isChecked(er));
		}
		for (Iterator<EClass> ic = superClass.getESuperTypes().iterator();ic.hasNext();)
			setAssociations(originalClass,ic.next(),level + 1);
	}

	
	private String getAssociationMappingText (EClass originalClass, EClass superClass, String roleName, EClass otherEndClass )
	{
		String[] c1c2 = new String[2];
		String text = "";
		String originalClassName= ModelUtil.getQualifiedClassName(originalClass);
		try {
			if ((classModelView() !=  null)
					&& (classModelView().mdlBase() != null)
					&& (otherEndClass != null))
				for (Iterator<EClass> it = classModelView().mdlBase().ms().getAllSubClasses(otherEndClass).iterator(); 
				it.hasNext();)
				{
					EClass otherSubClass = it.next();
					String otherSubClassName= ModelUtil.getQualifiedClassName(otherSubClass);
					c1c2[0] = originalClassName + "$" + otherSubClassName;
					c1c2[1] = otherSubClassName + "$" + originalClassName;
					for (int end = 0; end < 2; end++)
					{
						Vector<AssociationMapping> aMaps = 
							classModelView().mdlBase().associationMappingsByClass1Class2(c1c2[end]);
						for (int a = 0; a < aMaps.size();a++) 
						{
							AssociationMapping am = aMaps.get(a);
							// pick out the role name that gets to the other end class
							if (am.assocEnd(1-end).roleName().equals(roleName))
							{
								String path = am.nodePath().stringForm();
								if (path == null) path = "[no path found]";
								if (am.assocEnd(1-end).hasSubset()) 
									path = path + "(" + am.assocEnd(1-end).subset() + ")";
								if (am.assocEnd(end).hasSubset()) 
									path = path + "(" + am.assocEnd(end).subset() + ")";
								text = text + path + ";   ";
							}
						}
					}
				}
			
		}
		catch (MapperException ex) {GenUtil.surprise(ex,"AssociationView.getAssociationMappingText");}
		return text;
	}
	
	//---------------------------------------------------------------------------------------------
	//                     Handling check box switches and business name edits
	//---------------------------------------------------------------------------------------------

	public static void handleCheckEvent(EStructuralFeature feature, boolean checkState, LabelledEClass lClass) 
	{
		if (feature instanceof EReference)
		{
			EReference er = (EReference)feature;
			ITSAssociation itsa = getITSAssociation(er,lClass);
			itsa.setCollapsed(checkState);
			putITSAssociation(er,itsa,lClass);			
		}
	}

	
	public static void applyNewValueToFeature(EStructuralFeature feature, String newBusinessName, int columnIndex, LabelledEClass lClass)
	{
		// business name column
		if ((feature instanceof EReference) && (columnIndex == 1))
		{
			EReference er = (EReference)feature;
			ITSAssociation itsa = getITSAssociation(er,lClass);
			itsa.setBusinessName(newBusinessName);
			putITSAssociation(er,itsa,lClass);			
		}
		
	}
	
	/**
	 * @param er an EAttribute
	 * @return its ITSAttibute (the default one if it has no EAnnotation for this path)
	 * @throws MapperException
	 */
	public static ITSAssociation getITSAssociation(EReference er,LabelledEClass lClass)
	{
		ITSAssociation itsa = new ITSAssociation();
		if (lClass != null)
		{
			try
			{
				String path = lClass.getPath();
				String note = getMicroITSAnnotation(er,path);
				if (note != null) itsa = new ITSAssociation(note);			
			}
			catch (MapperException ex) {}			
		}
		return itsa;
	}
	
	/**
	 * @param ref
	 * @param lClass
	 * @return the lower bound of an EAttribute, as it may have been constrained by the user for this path
	 */
	public static int getConstrainedLowerBound(EReference ref, LabelledEClass lClass)
	{
		int lower = ref.getLowerBound();
		if (getITSAssociation(ref,lClass).lowerBoundIsConstrained()) lower = 1;
		return lower;
	}
	
	/**
	 * @param ref
	 * @param lClass
	 * @return the lower bound of an EAttribute, as it may have been constrained by the user for this path
	 */
	public static int getConstrainedUpperBound(EReference ref, LabelledEClass lClass)
	{
		int upper = ref.getUpperBound();
		if (getITSAssociation(ref,lClass).upperBoundIsConstrained()) upper = 1;
		return upper;
	}
	
	
	/**
	 * @param er an EReference
	 * @param itsa ITSAssociation to be put in its ITS annotation for this path
	 */
	public static void putITSAssociation(EReference er,ITSAssociation itsa,LabelledEClass lClass)
	{
		String path = lClass.getPath();
		addMicroITSAnnotation(er, path, itsa.stringForm());		
	}

	
	//---------------------------------------------------------------------------------------------
	//                            Menu and methods 
	//---------------------------------------------------------------------------------------------
	
	private Action constrainLowerBoundAction;
	private Action constrainUpperBoundAction;
	private Action revertBoundsAction;
	private Action deleteAssociationAction;
	private Action orderChildNodesAction;
	
	protected void makeActions()
	{
		constrainLowerBoundAction = new Action() {
			public void run() {
				doConstrainLowerBound();
			}
		};
		constrainLowerBoundAction.setText("Constrain lower bound to 1");		

		constrainUpperBoundAction = new Action() {
			public void run() {
				doConstrainUpperBound();
			}
		};
		constrainUpperBoundAction.setText("Constrain upper bound to 1");	
		

		revertBoundsAction = new Action() {
			public void run() {
				doRevertBounds();
			}
		};
		revertBoundsAction.setText("Revert bounds to model values");		

		deleteAssociationAction = new Action() {
			public void run() {
				doDeleteAssociation();
			}
		};
		deleteAssociationAction.setText("Delete Association");		

		orderChildNodesAction = new Action() {
			public void run() {
				doOrderChildNodes();
			}
		};
		orderChildNodesAction.setText("Order child nodes");		

}

	protected void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(constrainLowerBoundAction);
		manager.add(constrainUpperBoundAction);
		manager.add(revertBoundsAction);
		manager.add(orderChildNodesAction);
		manager.add(deleteAssociationAction);
	}
	
	@SuppressWarnings("unchecked")
	private EReference getSelectedRef()
	{
		EReference ref = null;
		Object rowObj = viewer.getElementAt(table.getSelectionIndex());
		if ((rowObj != null) && (rowObj instanceof Vector<?>))
		{
			Vector<String> row = (Vector<String>)rowObj;
			EStructuralFeature feature = features.get(rowKey(row));
			if (feature instanceof EReference) ref = (EReference)feature;
		}		
		return ref;
	}
	
	/**
	 * constrain the min multiplicity of the selected association to 1 for this path only, if it is not 1;
	 *  and note that it has been constrained
	 */
	private void doConstrainLowerBound()
	{
		EReference ref = getSelectedRef();
		if (ref != null)
		{
			if (ref.getLowerBound() == 1) WorkBenchUtil.showMessage("Error", "Lower bound of association '" + ref.getName() + "' is already 1");
			else
			{
				ITSAssociation itsa = getITSAssociation(ref,selectedLabelledClass);
				itsa.setLowerBoundConstraint(true);
				putITSAssociation(ref,itsa,selectedLabelledClass);
				updateForSelection(selectedClass);
			}
		}
	}

	
	/**
	 * constrain the max multiplicity of the selected association to 1 for this path only, if it is not 1; 
	 * and note that it has been constrained
	 */
	private void doConstrainUpperBound()
	{
		EReference ref = getSelectedRef();
		if (ref != null)
		{
			if (ref.getUpperBound() == 1) WorkBenchUtil.showMessage("Error", "Upper bound of association '" + ref.getName() + "' is already 1");
			else
			{
				ITSAssociation itsa = getITSAssociation(ref,selectedLabelledClass);
				itsa.setUpperBoundConstraint(true);
				putITSAssociation(ref,itsa,selectedLabelledClass);
				updateForSelection(selectedClass);
			}
		}
		
	}
	
	/**
	 * revert the min and max multiplicity of the selected association to their unconstrained values
	 */
	private void doRevertBounds()
	{
		EReference ref = getSelectedRef();
		if (ref != null)
		{
			boolean changesMade = false;
			ITSAssociation itsa = getITSAssociation(ref,selectedLabelledClass);
			
			// if the lower bound has been constrained, revert it
			if (itsa.lowerBoundIsConstrained())
			{
				itsa.setLowerBoundConstraint(false);
				changesMade = true;
			}
			
			// if the upper bound has been constrained, revert it
			if (itsa.upperBoundIsConstrained())
			{
				itsa.setUpperBoundConstraint(false);
				changesMade = true;
			}
			
			if (changesMade) 
			{
				putITSAssociation(ref,itsa,selectedLabelledClass);
				updateForSelection(selectedClass);
			}
			else WorkBenchUtil.showMessage("Error", "Neither bound of association '" + ref.getName() + "' has been changed.");
			
		}
		
	}
	
	/**
	 * annotate an association so that the child nodes beneath it will be ordered
	 * in the simplified class model and mappings
	 */
	private void doOrderChildNodes()
	{
		EReference ref = getSelectedRef();
		if (ref != null)
		{
			ITSAssociation itsa = getITSAssociation(ref,selectedLabelledClass);
			
			// toggle on this command - need a way to see which way you are toggling
			itsa.setChildrenAreOrdered(!itsa.childrenAreOrdered());
			putITSAssociation(ref,itsa,selectedLabelledClass);
			updateForSelection(selectedClass);
		}
		
	}
	
	/**
	 * irreversibly delete an association (but not the class at the end of it)
	 */
	private void doDeleteAssociation()
	{
		EReference ref = getSelectedRef();		
		selectedClass.getEStructuralFeatures().remove(ref);
		updateForSelection(selectedClass);
	}

	


}
