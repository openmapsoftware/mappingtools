package com.openMap1.mapper.views;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.mapping.propertyMapping;
import com.openMap1.mapper.structures.ITSAttribute;
import com.openMap1.mapper.util.ModelUtil;

import org.eclipse.jface.viewers.ISelectionChangedListener; 

/**
 * Shows the attributes of the currently selected class in
 * the Class Model View, and their mappings.
 * 
 * @author robert
 *
 */
public class AttributeView extends FeatureView
implements ISelectionChangedListener, SortableView{

	
	
	//---------------------------------------------------------------------------------------------
	//                                Initialisation
	//---------------------------------------------------------------------------------------------


	/**
	 * Callback to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		viewer.setLabelProvider(new AttributeTableLabelProvider());

		/* the next call was put in because I do not know in what order this view 
		 * and the class model view are created at startup, so I wanted to make the 
		 * link in either case. But it makes the class model view call itself recursively.
		 * Now the connection is made when a class model is attached to the class model view. */
		// if (classModelView() != null) classModelView().connectToAttributeView(this);
	}

	class AttributeTableLabelProvider extends LabelProvider implements ITableLabelProvider
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

		props.add(addColumn("Include", 50, RowSorter.stringComparator, comps,cols));
		props.add(addColumn("Business Name", 120, RowSorter.stringComparator, comps,cols));
		props.add(addColumn("Level", 50, RowSorter.numberComparator, comps,cols));
		props.add(addColumn("From Class", 100, RowSorter.stringComparator, comps,cols));
		props.add(addColumn("Attribute", 100, RowSorter.stringComparator, comps,cols));
		props.add(addColumn("Min", 35, RowSorter.stringComparator, comps,cols));
		props.add(addColumn("Value", 100, RowSorter.stringComparator, comps,cols));
		props.add(addColumn("Type", 100, RowSorter.stringComparator, comps,cols));
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
	
	protected void updateForSelection(EClass selectedClass)
	{
		LabelledEClass lc = new LabelledEClass(selectedClass,"", null);
		updateForSelection(lc);
	}

	
	protected void updateForSelection(LabelledEClass selectedLabelledClass)
	{
		// if there are any previous results showing, remove them
		table.removeAll();

		features = new Hashtable<String,EStructuralFeature>();
		checkedFeatures = new Hashtable<String,Boolean>();
		featureRows = new Vector<Vector<String>>();
		setAttributes(selectedLabelledClass,selectedLabelledClass.eClass(),0);

		// add new rows to the view, and set their check boxes
		for (Iterator<Vector<String>> it = featureRows.iterator();it.hasNext();)
			addToViewer(it.next());
	}
	

	/**
	 * add to the attributes vector 'features' the attributes of this class, 
	 * then recursively add those of its superclasses, keeping
	 * track of the level of inheritance. 
	 * @param superClass
	 * @param level
	 */
	private void setAttributes(LabelledEClass labelledClass, EClass superClass,int level) 
	{
		for (Iterator<EAttribute> it = superClass.getEAttributes().iterator();it.hasNext();)
		{
			EAttribute ea = it.next();
			String attName= ea.getName(); // for debugging
			ITSAttribute itsa = getITSAttribute(ea,selectedLabelledClass);

			Vector<String> row = new Vector<String>();
			row.add(""); // include flag
			row.add(itsa.businessName()); // business name
			row.add(new Integer(level).toString());
			row.add(superClass.getName());
			row.add(ea.getName());
			// get the lower bound as it may have been constrained by the user
			row.add(new Integer(getConstrainedLowerBound(ea,selectedLabelledClass)).toString());
			
			String value = "";
			// normal RMIM case with no superclasses - preferred way to find fixed values
			if (superClass.equals(labelledClass.eClass())) try
			{
				value = labelledClass.getAnnotatedFixedValue(ea.getName());
			}
			catch (MapperException ex) {trace(ex.getMessage());}
			// I need to keep this code in case of superclasses
			else 
			{
				// find any fixed value specified in the RMIM before annotation
				value = ModelUtil.getEAnnotationDetail(ea, "fixed value");
				if (value == null) value = "";
				// find any fixed value requested specifically by annotation of the REcore model; this takes precedence
				String specificFixedValue = getFixedValue(ea);
				if (specificFixedValue != null) value = specificFixedValue;				
			}
			row.add(value);


			String type = "-";
			if (ea.getEAttributeType() != null) type = ea.getEAttributeType().getName(); 
			row.add(type);
			row.add(mappingText(labelledClass.eClass(),superClass,ea.getName()));
			
			// record the row, the EAttribute, and whether it is to be checked
			featureRows.add(row);
			features.put(rowKey(row), ea);
			checkedFeatures.put(rowKey(row), isChecked(ea));
		}
		for (Iterator<EClass> ic = superClass.getESuperTypes().iterator();ic.hasNext();)
			setAttributes(labelledClass,ic.next(),level + 1);
	}
	
	
	private String mappingText (EClass originalClass, EClass superClass, String propName)
	{
		String text = "";
		if ((classModelView() !=  null) && (classModelView().mdlBase() != null))
		{
			String className = ModelUtil.getQualifiedClassName(originalClass);
			Vector<propertyMapping> propMappings = classModelView().mdlBase().propertyMappingsByClassName(className);
			for (int p = 0; p < propMappings.size(); p++)
				if (propMappings.get(p).propertyName().equals(propName))
				{
					propertyMapping pm = propMappings.get(p);
					String path = pm.nodePath().stringForm();
					if (pm.hasSubset()) path = path + "(" + pm.subset() + ")";
					text = text + path + ";   ";
				
				}
			// I have not added the number of mappings at the front, to preserve sort order of paths in the view
			// if (mappings > 1) text =  "(" + new Integer(mappings).toString() + "): " +text;
		}
		return text;
	}
	
	//---------------------------------------------------------------------------------------------
	//                     Handling check box switches and business name edits
	//---------------------------------------------------------------------------------------------

	/**
	 * When the state of a check box changes,
	 * (a) record it on the EAttribute affected
	 * (b) for all ancestor EAssociations, set or unset their 'used' flags appropriately
	 */
	public static void handleCheckEvent(EStructuralFeature feature, boolean checkState, LabelledEClass lClass) 
	{
		if (feature instanceof EAttribute)
		{
			// change the included flag on the attribute affected
			EAttribute ea = (EAttribute)feature;
			ITSAttribute itsa = getITSAttribute(ea,lClass);
			itsa.setIncluded(checkState);
			putITSAttribute(ea,itsa,lClass);
			
			/* for all ancestor EAssociations, set or unset the used flag.
			 * Ascend the tree until you find a flag that is already set correctly,
			 * or you reach the top of the tree */
			LabelledEClass current = lClass;
			while (current != null)
			{
				boolean oldUsedState = current.isMarkedUsedInMicroITS();
				boolean newUsedState = current.isActuallyUsedInMicroITS();
				if (newUsedState != oldUsedState) // need to mark and ascend
				{
					current.markAsUsedInMicroITS(newUsedState);
					current = current.parent(); // null if current was top of the tree
				}
				else if (newUsedState == oldUsedState) current = null; // need go no further
			}
		}
	}
	
	public static void applyNewValueToFeature(EStructuralFeature feature, String newValue, int columnIndex, LabelledEClass lClass)
	{
		// business name column
		if ((feature instanceof EAttribute) && (columnIndex == 1))
		{
			EAttribute ea = (EAttribute)feature;
			ITSAttribute itsa = getITSAttribute(ea,lClass);
			itsa.setBusinessName(newValue);
			putITSAttribute(ea,itsa,lClass);
		}
		
		// value column
		if ((feature instanceof EAttribute) && (columnIndex == 6))
		{
			EAttribute ea = (EAttribute)feature;
			if (lClass != null)
			{
				String attKey = "fixed:" + lClass.getPath();
				// after editing to '', remove any previous fixed value
				if (newValue.equals("")) FeatureView.removeMicroITSAnnotation(ea, attKey);
				// specific fixed value requested for this path on the annotated example instance
				else FeatureView.addMicroITSAnnotation(ea, attKey, newValue);						
			}
		}		

	}


	
	/**
	 * @param ea an EAttribute
	 * @return its ITSAttibute (the default one if it has no EAnnotation for this path)
	 * @throws MapperException
	 */
	public static ITSAttribute getITSAttribute(EAttribute ea, LabelledEClass lClass)
	{
		ITSAttribute itsa = new ITSAttribute();
		if (lClass != null)
		{
			try
			{
				String path = lClass.getPath();
				String note = getMicroITSAnnotation(ea,path);
				if (note != null) itsa = new ITSAttribute(note);			
			}
			catch (MapperException ex) {}			
		}
		return itsa;
	}
	
	/**
	 * @param ea
	 * @param lClass
	 * @return the lower bound of an EAttribute, as it may have been constrained by the user for this path
	 */
	public static int getConstrainedLowerBound(EAttribute ea, LabelledEClass lClass)
	{
		int lower = ea.getLowerBound();
		if (getITSAttribute(ea,lClass).lowerBoundIsConstrained()) lower = 1;
		return lower;
	}
	
	/**
	 * get a specific fixed value for this attribute which has been annotated on the Ecore model; 
	 * or null if there is none
	 * @param ea
	 * @return
	 */
	private String getFixedValue(EAttribute ea)
	{
		String fixed = null;
		if (selectedLabelledClass != null)
		{
			String attKey = "fixed:" + selectedLabelledClass.getPath();
			// specific fixed value requested for this path on the annotated example instance
			fixed = FeatureView.getMicroITSAnnotation(ea, attKey);
		}
		return fixed;
	}
	
	/**
	 * @param ea an EAttribute
	 * @param itsa ITSAttribute to be put in its ITS annotation for this path
	 */
	public static void putITSAttribute(EAttribute ea,ITSAttribute itsa, LabelledEClass lClass)
	{
		if (lClass != null)
		{
			String path = lClass.getPath();
			addMicroITSAnnotation(ea, path, itsa.stringForm());					
		}
	}

	//---------------------------------------------------------------------------------------------
	//                            Menu and methods 
	//---------------------------------------------------------------------------------------------
	
	private Action constrainLowerBoundAction;
	private Action revertBoundsAction;
	
	protected void makeActions()
	{
		constrainLowerBoundAction = new Action() {
			public void run() {
				doConstrainLowerBound();
			}
		};
		constrainLowerBoundAction.setText("Constrain lower bound to 1");		

		revertBoundsAction = new Action() {
			public void run() {
				doRevertBounds();
			}
		};
		revertBoundsAction.setText("Revert lower bound to 0");		
	}

	protected void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(constrainLowerBoundAction);
		manager.add(revertBoundsAction);
	}
	
	@SuppressWarnings("unchecked")
	private EAttribute getSelectedAtt()
	{
		EAttribute att = null;
		Object rowObj = viewer.getElementAt(table.getSelectionIndex());
		if ((rowObj != null) && (rowObj instanceof Vector<?>))
		{
			Vector<String> row = (Vector<String>)rowObj;
			EStructuralFeature feature = features.get(rowKey(row));
			if (feature instanceof EAttribute) att = (EAttribute)feature;
		}		
		return att;
	}
	
	/**
	 * constrain the min multiplicity of the selected attribute to 1, if it is not 1, for this path only; 
	 * and note that it has been constrained.
	 * Do not actually change the value in the Ecore model, as that would show up in lots of other places besides 
	 * the path you actually want to change it for.
	 */
	private void doConstrainLowerBound()
	{
		EAttribute att = getSelectedAtt();
		if (att != null)
		{
			if (att.getLowerBound() == 1) WorkBenchUtil.showMessage("Error", "Lower bound of attribute '" + att.getName() + "' is already 1");
			else
			{
				ITSAttribute itsa = getITSAttribute(att,selectedLabelledClass);
				itsa.setLowerBoundConstraint(true);
				putITSAttribute(att,itsa,selectedLabelledClass);
				updateForSelection(selectedClass);
			}
		}
	}

	/**
	 * revert the min multiplicity of the selected association to its unconstrained value 0
	 */
	private void doRevertBounds()
	{
		EAttribute att = getSelectedAtt();
		if (att != null)
		{
			ITSAttribute itsa = getITSAttribute(att,selectedLabelledClass);
			
			// if the lower bound has been constrained, revert it
			if (itsa.lowerBoundIsConstrained())
			{
				itsa.setLowerBoundConstraint(false);
				putITSAttribute(att,itsa,selectedLabelledClass);
				updateForSelection(selectedClass);
			}
			else WorkBenchUtil.showMessage("Error", "Lower bound of attribute '" + att.getName() + "' has not been changed.");			
		}
		
	}

	

}
