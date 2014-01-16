package com.openMap1.mapper.health.actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;

import com.openMap1.mapper.actions.MapperActionDelegate;
import com.openMap1.mapper.converters.V2Converter;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.FileSaverWizard;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.StructureType;

/**
 * Action to make a V2 XML instance from a bar-hat V2 instance; superseded
 * by use of the V2 wrapper 'in' transform
 * 
 * @author robert
 *
 */
public class MakeV2XMLInstanceActionDelegate extends MapperActionDelegate 
implements IObjectActionDelegate{
	
	private FileInputStream V2BarStream;

	public void run(IAction action) {
		
		try{
		
			// (1) check that the selected mapping set is a V2 mapping set
			MapperEditor me = OpenMapperEditor(selection);
			if (me == null) throw new MapperException("Cannot open Mapper Editor");
			MappedStructure ms  = WorkBenchUtil.mappingRoot(me);
			if (ms == null) throw new MapperException("Mapper Editor has no mapped structure open");
			if (!(ms.getStructureType() == StructureType.V2))
				throw new MapperException("Mapping set is not a V2 mapping set");
			
			// (2) get the location of the V2 text file from the user
			String[] exts = {"*.txt"}; 
			String V2BarFile = FileUtil.getFilePathFromUser(targetPart,exts,"Select Bar Encoded V2 Message",false);			
			if (V2BarFile.equals("")) return;
			// open a file stream and mark the beginning to re-read it
			try{V2BarStream = new FileInputStream(V2BarFile);}
			catch (FileNotFoundException ex) {throw new MapperException("Cannot find file at '" + V2BarFile + "'");}
			
			// (3) get a location for the V2 xml file from the user
			String wizardTitle = "V2.XML File";
			String pageTitle = "Choose a name and location for the V2.XML file";
			FileSaverWizard wizard = new FileSaverWizard(wizardTitle,pageTitle);
			wizard.init(PlatformUI.getWorkbench(), null);
		    WizardDialog dialog = new WizardDialog(WorkBenchUtil.getShell(),wizard);
		    dialog.open();

		    IFile v2XMLFile = wizard.getViewSaveFile();
		    if (v2XMLFile != null) 
		    {
		    	String rootName = ms.getTopElementName();
		    	V2Converter v2Converter = new V2Converter(ms, rootName);
		    	Document v2XMLDoc = v2Converter.transformIn(V2BarStream);
		    	EclipseFileUtil.writeOutputResource(v2XMLDoc, v2XMLFile, true);
		    			    	
		    	// do the round trip comparison, throwing an Exception at the first fault
				try {V2BarStream = new FileInputStream(V2BarFile);}
		    	catch (Exception ex)  {throw new MapperException(ex.getMessage());}
		    	v2Converter.doRoundTripTest(V2BarStream);
		    }
		}
		catch (MapperException ex) {showMessage("Error","Failed to write V2.xml file: " + ex.getMessage());}				
	}


}
