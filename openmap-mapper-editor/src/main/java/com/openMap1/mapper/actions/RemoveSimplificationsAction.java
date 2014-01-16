package com.openMap1.mapper.actions;

import java.util.Iterator;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.FeatureView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

public class RemoveSimplificationsAction  extends Action implements IAction{

    boolean tracing = true;

	/** the package which is the root of the model */
	private EPackage ecoreRoot;

    private ClassModelView classModelView;
	
	private LabelledEClass selectedLabelledEClass;
	
	public RemoveSimplificationsAction()
	{
		super("Remove Simplifications");
	}

	public void run()
	{
		classModelView = WorkBenchUtil.getClassModelView(false);

		if (classModelView != null) try
		{
			ecoreRoot = classModelView.ecoreRoot();
			if (ecoreRoot == null) throw new MapperException("Cannot find root package of class model");

			selectedLabelledEClass = classModelView.getSelectedLabelledEClass();
			if (selectedLabelledEClass == null)
				throw new MapperException("No class is selected in the Class Model View");

			// remove the annotations from the class and its descendants
			int removed = removeAnnotations(selectedLabelledEClass);

			/* ensure the associations leading down to the selected class 
			 * are not annotated as used unless they have other descendants used*/
			selectedLabelledEClass.markWithAncestors(false);
			
			// save the changes
			FileUtil.saveResource(ecoreRoot.eResource());
			
			// show completion
			WorkBenchUtil.showMessage("Removed","Removed " + removed + " simplifications from subtree.");
		}
		catch (Exception ex) 
		{
			WorkBenchUtil.showMessage("Error",ex.getMessage());
			if (tracing) ex.printStackTrace();
		}					
	}
	
	/**
	 * recursive removal of simplification annotations from a LabelledEClass and its descendants
	 * @param lClass
	 * @return total number of simplfications removed
	 */
	private int removeAnnotations(LabelledEClass lClass)
	{
		int removed = 0;
		for (Iterator<EStructuralFeature> is = lClass.eClass().getEStructuralFeatures().iterator(); is.hasNext();)
		{
			EStructuralFeature feature = is.next();

			// remove annotations from EReferences and EAttributes
			EAnnotation existingNote = feature.getEAnnotation(FeatureView.microITSURI());
			if (existingNote != null)
			{
				String value = existingNote.getDetails().get(lClass.getPath());
				if (value != null)
				{
					removed++;
					existingNote.getDetails().removeKey(lClass.getPath());
					if (existingNote.getDetails().size() == 0)
					{
						feature.getEAnnotations().remove(existingNote);
					}
					
					// recurse down the tree only if there is an annotation on an EReference
					if (feature instanceof EReference)
					{
						LabelledEClass child  = lClass.getNamedAssocChild(feature.getName());
						if (child != null) removed = removed + removeAnnotations(child);
					}
				}
			}
			
		}
		return removed;
		
	}

}
