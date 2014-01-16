package com.openMap1.mapper.actions;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;

import com.openMap1.mapper.converters.AbstractMapperWrapper;
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
 * in the 'in' direction, i.e what is done to an instance before it 
 * is read by mappings. Allows the user to choose the source file and 
 * the destination file after the wrapper transform has been applied.
 * 
 * @author robert
 *
 */

public class ApplyWrapperTransformIn extends MapperActionDelegate 
implements IObjectActionDelegate{

	public void run(IAction action) {
		try{
			// (1) find the mapped Structure and check it declares  a wrapper class
		    MappedStructure ms = mappedStructure();
		    
		    // temporary, just to count nodes in the tree
		    // int count = ms.countNodesInTree();
		    // System.out.println("Mappable Nodes: " + count);

		    if (!ms.hasWrapperClass())
		    	throw new MapperException("Mapped structure " + ms.getMappingSetName() + " does not declare a wrapper class.");
		    
		    // (2) get an instance of the wrapper class; spare argument is the root element name
		    String rootName = "";
		    if (ms.getRootElement() != null) rootName = ms.getRootElement().getName();
		    MapperWrapper wrapper = ms.getWrapper(rootName);
		    
		    // (3) find the extension of the file to open to make an 'in' transform from
		    String[] exts = new String[1];
		    exts[0] = wrapper.fileExtension();
		    
		    // (4) open the input file
			String path = FileUtil.getFilePathFromUser(targetPart,exts,"Select source for wrapper input transform",false);
			if (path.equals("")) return;
			
			// (5) get the Object to pass to the wrapper transform
			Object input = null;
			if (wrapper.transformType() == AbstractMapperWrapper.XML_TYPE)
			{
				input = XMLUtil.getRootElement(path);
				if (input == null) throw new MapperException("Could not open XML");				
			}
			else if (wrapper.transformType() == AbstractMapperWrapper.TEXT_TYPE)
			{
				input = new FileInputStream(new File(path));
			}
			
			// (6) apply the input transform
			Document inDoc = wrapper.transformIn(input);
			
			// (7) ask the user where to save the result
			String wizardTitle = "Save wrapper-transformed input";
			String pageTitle = "Name a file  to save the wrapper-transformed input";
			FileSaverWizard wizard = new FileSaverWizard(wizardTitle,pageTitle);
			wizard.init(PlatformUI.getWorkbench(), null);
		    WizardDialog dialog = new WizardDialog(WorkBenchUtil.getShell(),wizard);
		    dialog.open();

		    // (8) save the transformed input in an IFile, formatted and visible to Eclipse
		    IFile transformedFile = wizard.getViewSaveFile();
		    if (transformedFile != null) 
		    	EclipseFileUtil.writeOutputResource(inDoc, transformedFile, true);
		   			
		}
		catch (Exception ex) 
		{
			showMessage("Failed to apply wrapper input transform: " + ex.getMessage());
			System.out.println("Failed to apply wrapper input transform: " + ex.getMessage());
			ex.printStackTrace();
		}		
	}

}
