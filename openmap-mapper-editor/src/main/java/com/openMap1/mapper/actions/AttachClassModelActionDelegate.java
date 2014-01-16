package com.openMap1.mapper.actions;

import org.eclipse.jface.action.IAction;

import org.eclipse.ui.IObjectActionDelegate;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.URI;

import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.ClassModelMaker;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.views.FeatureView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;


/**
 * Implements the action to open a UML model and attach it to a set of mappings
 * @author robert
 *
 */
public class AttachClassModelActionDelegate extends MapperActionDelegate 
    implements IObjectActionDelegate {

 

	@Override
	/**
	 * On running this action, do the following:
	 * 
	 * (1) Show a file dialogue for the user to select a UML file
	 * (2) Try to open the file and read it into an ecore model
	 * (3) If the editor is not open, open it
	 * (4) Set the 'class model location' attribute on the top 'Mapped structure' node
	 * (5) Show the class model view and give it focus.
	 * (6) If the class model already has simplification annotations for some other mapping set, warn the user
	 * (7) If the Ecore model declares a wrapper class on its entry class, declare that wrapper class on the mapping set
	 * (8) Save the mapping set
	 */
	public void run(IAction action) {
		try
		{
			// (1) show the dialog for the user to choose a file
			String[] exts = {"*.ecore","*.daml"}; 
			String path = FileUtil.getFilePathFromUser(targetPart,exts,"Select class model",false);
			if (path.equals("")) return;
			
			// (2) open the file as a Ecore model
			URI classModelURI = FileUtil.URIFromPath(path);
			EObject ecoreRoot  = ClassModelMaker.makeClassModelFromFile(classModelURI);
			if (ecoreRoot == null) return;
				
			// (3) Check if the editor is opened; if not, open it
			MapperEditor me = OpenMapperEditor(selection);
			if (me == null) return;
			
			// (4) update the property 'UMLModelURL' of the top 'MappedStructure' node (but do not save the file)
			boolean success = setUMLModelPathProperty(me, path);
			if (!success) return;

			// (5) Show the class model in the class model view
			showClassModel(me, ecoreRoot, path);
			
			// (6) If the class model already has simplification annotations for some other mapping set, warn the user
			if (FeatureView.hasBeenAnnotatedForOtherMappingSet())
				WorkBenchUtil.showMessage("Warning", "This Ecore model already has simplification annotations for" 
						+ " some other mapping set, so cannot be annotated for this mapping set.");
			
			// (7) If the Ecore model declares a wrapper class on its entry class, give that wrapper class to the mapping set
			MappedStructure ms  = WorkBenchUtil.mappingRoot(me);
			takeWrapperClassFromEcoreModel(ms);
			
			// (8) Save the mapping set
			me.doSave(null);
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			showMessage("Failed to open class model: " + ex.getMessage());
		}
	}
	
	/**
	 * If the attached ecore model declares a wrapper class on its entry class,
	 * set that to be the wrapper class of the mapping set
	 * @param me
	 */
	private void takeWrapperClassFromEcoreModel(MappedStructure ms)
	{
		LabelledEClass entryClass = WorkBenchUtil.getClassModelView(true).topLabelledEClass();
		if (entryClass != null)
		{
			String wrapperClassName = ModelUtil.getMIFAnnotation(entryClass.eClass(), "wrapperClass");
			if (wrapperClassName != null)
			{
				GlobalMappingParameters gmp = ms.getMappingParameters();
				gmp.setWrapperClass(wrapperClassName);
			}			
		}
	}

	
	


}
