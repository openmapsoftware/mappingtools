package com.openMap1.mapper.actions;

import java.util.Iterator;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.FeatureView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

public class CopySimplificationsAction  extends Action implements IAction{

    private ClassModelView classModelView;
    
    private DataSourceView dataSourceView;

    
    boolean tracing = true;

	/** the package which is the root of the model */
	private EPackage ecoreRoot;

	private LabelledEClass selectedLabelledEClass;
	
	private String annotationURI;
	
	public CopySimplificationsAction()
	{
		super("Copy Simplifications");
	}

	public void run()
	{

		classModelView = WorkBenchUtil.getClassModelView(false);
		dataSourceView = WorkBenchUtil.getDataSourceView(false);

		if ((classModelView != null) && (dataSourceView != null)) try
		{
			selectedLabelledEClass = classModelView.getSelectedLabelledEClass();
			
			if (selectedLabelledEClass == null)
				throw new MapperException("No class is selected in the Class Model View");
			
			ecoreRoot = classModelView.ecoreRoot();
			if (ecoreRoot == null)
				throw new MapperException("Cannot find root package of class model");

			// find out the URI used for simplification annotations
			annotationURI = ImportSimplificationsAction.getITSAnnotationURI(ecoreRoot);
			if (annotationURI == null)
				throw new MapperException("There are no simplification annotations in the class model");
			
			int copiedSimplifications = countSimplifications(selectedLabelledEClass);
			
			if (!ImportSimplificationsAction.hasSimplificationInSubtree(selectedLabelledEClass, annotationURI))
				throw new MapperException("There are no simplification annotations to copy below the selected class");
			
			// remember the selected labelled EClass in the class model view, and the annotation uri
			dataSourceView.setCopiedLabelledEClass(selectedLabelledEClass);
			dataSourceView.setAnnotationURI(annotationURI);
			
			WorkBenchUtil.showMessage("Copied", "Copied " + copiedSimplifications + " simplifications from subtree.");
		}
		catch (Exception ex) 
		{
			WorkBenchUtil.showMessage("Error",ex.getMessage());
			ex.printStackTrace();
		}					
	}
	
	/**
	 * count all simplification annotations in the subtree of a class
	 * @param lClass
	 * @return
	 */
	private int countSimplifications(LabelledEClass lClass)
	{
		int count = 0;
		for (Iterator<EStructuralFeature> is = lClass.eClass().getEStructuralFeatures().iterator(); is.hasNext();)
		{
			EStructuralFeature feature = is.next();
			boolean recurse = false;

			// count annotations on EReferences and EAttributes
			EAnnotation existingNote = feature.getEAnnotation(FeatureView.microITSURI());
			if (existingNote != null)
			{
				// go over all possible keys for simplification annotations
				for (int p =0 ; p < ImportSimplificationsAction.pathPrefixes.length;p++)
				{
					String prefixedPath = ImportSimplificationsAction.pathPrefixes[p] + lClass.getPath();
					String value = existingNote.getDetails().get(prefixedPath);
					if (value != null)
					{
						count++;
						recurse = true;
					}
				}
			}	
			
			// recurse down the tree only if there is an annotation on an EReference for this path, maybe with  prefix
			if ((feature instanceof EReference) && (recurse))
			{
				LabelledEClass child  = lClass.getNamedAssocChild(feature.getName());
				if (child != null) count = count + countSimplifications(child);
			}

		}
		return count;		
	}
	
	@SuppressWarnings("unused")
	private void trace(String s) {if (tracing) System.out.println(s);}


}
