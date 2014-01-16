package com.openMap1.mapper.actions;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.FeatureView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

public class PasteSimplificationsAction  extends Action implements IAction{

    private ClassModelView classModelView;
    
    private DataSourceView dataSourceView;
    
    boolean tracing = true;

	/** the package which is the root of the model */
	private EPackage ecoreRoot;

	private LabelledEClass selectedLabelledEClass;

	private LabelledEClass copiedLabelledEClass;
	
	public PasteSimplificationsAction()
	{
		super("Paste Simplifications");
	}

	public void run()
	{
		classModelView = WorkBenchUtil.getClassModelView(false);
		dataSourceView = WorkBenchUtil.getDataSourceView(false);

		if ((classModelView != null) && (dataSourceView != null)) try
		{
			selectedLabelledEClass = classModelView.getSelectedLabelledEClass();
			
			if (selectedLabelledEClass == null)
				throw new MapperException("No class is selected to paste simplifications to");

			copiedLabelledEClass = dataSourceView.getCopiedLabelledEClass();
			String sourceAnnotationURI = dataSourceView.getAnnotationURI();
			
			if (copiedLabelledEClass == null)
				throw new MapperException("No class has been copied, for its simplifications to be pasted to the selected class");
			
			// check that source and target have the same RIM class or data type class
			String sourceRIMClass = copiedLabelledEClass.getRIMorDataTypeClassName();
			String targetRIMClass = selectedLabelledEClass.getRIMorDataTypeClassName();
			if (sourceRIMClass == null) throw new MapperException("Copied node has no RIM class");
			if (!compatibleRIMClasses(sourceRIMClass,targetRIMClass))
				throw new MapperException("Copied node has RIM class '" + sourceRIMClass + "' but target node has RIM class '" + targetRIMClass + "'");

			ecoreRoot = classModelView.ecoreRoot();
			if (ecoreRoot == null) throw new MapperException("Cannot find root package of class model");
			
			// find out the URI to use for simplification annotations
			String targetAnnotationURI = ImportSimplificationsAction.getITSAnnotationURI(ecoreRoot);
			if (targetAnnotationURI == null) 
			{
				MappedStructure ms = WorkBenchUtil.mappingRoot(classModelView.mapperEditor());
				targetAnnotationURI = FeatureView.microITSURIStart + ms.getMappingSetName();
				trace("Target annotation uri: " + targetAnnotationURI);
			}
			
			// if the target already has simplifications, let the user opt out of replacing them
			if (ImportSimplificationsAction.hasSimplificationInSubtree(selectedLabelledEClass, targetAnnotationURI))
			{
				if (!WorkBenchUtil.askConfirm("Warning", "Selected node already has simplifications. Do you want to replace them?")) return;
			}

			// copy the simplifications from the subtree beneath the 'copied' class to the subtree beneath the selected class
			int simplifications = ImportSimplificationsAction.importSimplifications(copiedLabelledEClass, selectedLabelledEClass, false, "", sourceAnnotationURI);
			WorkBenchUtil.showMessage("Simplifications Pasted", simplifications + " simplifications pasted into to subtree");

			// ensure the associations leading down to the selected class are annotated as used
			selectedLabelledEClass.markWithAncestors(true);
			
			// save the changes
			FileUtil.saveResource(ecoreRoot.eResource());							
		}
		catch (Exception ex) 
		{
			WorkBenchUtil.showMessage("Error",ex.getMessage());
			if (tracing) ex.printStackTrace();
		}					
	}
	
	/* classes which are compatible for copying and pasting simplifications */
	private String[] ActSubclasses = {"Act","Procedure","Observation","PatientEncounter","SubstanceAdministration"};
	
	/**
	 * 
	 * @param sourceClass
	 * @param targetClass
	 * @return true if it is possible to copy simplification annotations from one class to the other
	 */
	private boolean compatibleRIMClasses(String sourceClass, String targetClass)
	{
		if (sourceClass.equals(targetClass)) return true;
		if ((GenUtil.inArray(sourceClass, ActSubclasses)) && (GenUtil.inArray(targetClass, ActSubclasses))) return true;
		return false;
	}
	
	private void trace(String s) {if (tracing) System.out.println(s);}


}
