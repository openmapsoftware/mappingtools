package com.openMap1.mapper.actions;


import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.FileSaverWizard;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.MappedStructure;

/**
 * Apply the wrapper transform which is attached to the mapping set, 
 * in the 'out' direction, i.e what is done to an instance after it 
 * has been written with the aid of a wproc file. 
 * Allows the user to choose the source file and 
 * the destination file after the wrapper transform has been applied.
 * 
 * @author robert
 *
 */
public class ApplyWrapperTransformOut extends MapperActionDelegate 
implements IObjectActionDelegate{

	public void run(IAction action) {
		try{
			// (1) find the mapped Structure and check it declares  a wrapper class
		    MappedStructure ms = mappedStructure();
		    if (!ms.hasWrapperClass())
		    	throw new MapperException("Mapped structure " + ms.getMappingSetName() + " does not declare a wrapper class.");
		    
		    // (2) get an instance of the wrapper class; second argument is the root element name
		    String rootName = "";
		    if (ms.getRootElement() != null) rootName = ms.getRootElement().getName();
		    MapperWrapper wrapper = ms.getWrapper(rootName);
		    
		    // (3)The extension of the file to open to make an 'out' transform from is always 'xml'
		    String[] exts = new String[1];
		    exts[0] = "*.xml";
		    
		    // (4) open the input file
			String path = FileUtil.getFilePathFromUser(targetPart,exts,"Select source for output wrapper transform",false);
			if (path.equals("")) return;
			
			// (5) get the root Element to pass to the wrapper transform
			Element XMLRoot = XMLUtil.getRootElement(path);
			if (XMLRoot == null) throw new MapperException("Could not open XML");
			
			// (6) apply the output wrapper transform
			Object outObject = wrapper.transformOut(XMLRoot);
			
			// (7) ask the user where to save the result
			String wizardTitle = "Save wrapper-transformed output";
			String pageTitle = "Name a file  to save the wrapper-transformed output";
			FileSaverWizard wizard = new FileSaverWizard(wizardTitle,pageTitle);
			wizard.init(PlatformUI.getWorkbench(), null);
		    WizardDialog dialog = new WizardDialog(WorkBenchUtil.getShell(),wizard);
		    dialog.open();

		    // (8) save the transformed input in an IFile, visible to Eclipse
		    IFile transformedFile = wizard.getViewSaveFile();
		    if (transformedFile != null)
		    	EclipseFileUtil.writeOutputObject(outObject, transformedFile, wrapper.transformType());
		   			
		}
		catch (Exception ex) 
		{
			showMessage("Failed to apply output wrapper transform: " + ex.getMessage());
			System.out.println("Failed to apply output wrapper transform: " + ex.getMessage());
			ex.printStackTrace();
		}		
	}

}
