package com.openMap1.mapper.actions;

import java.util.Vector;

import org.w3c.dom.Document;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;

import org.eclipse.core.runtime.CoreException;

import com.openMap1.mapper.presentation.TranslateDestinationWizard;
import com.openMap1.mapper.query.DataSource;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.core.MapperException;


import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.WorkBenchUtil;

/**
 * Class to do a translation, using generated XSLT. Steps:
 * 
 * (1) check that there are some active data sources to translate from. The XSL
 * does not enable a check that they have the correct class model.
*  
 * (2) For each active source, show a Wizard file selection page to choose a destination file
 * for the translated XML 
 * 
 * (3) Do the translation for each active source.
 */	


public class DoXSLTranslateActionDelegate extends MapperActionDelegate
implements IObjectActionDelegate{

	public void run(IAction action) {
		System.out.println("Running XSLT");

		/* (1) check that there are some active data sources */
		DataSourceView dataSourceView = WorkBenchUtil.getDataSourceView(true);
		if (dataSourceView == null) 
		   {showMessage("There is no data source view to define sources for the translation");return;}
		Vector<DataSource> activeSources = dataSourceView.getActiveDataSources();
		if (activeSources.size() == 0)
		   {showMessage("There are no active data sources for the translation");return;}
		
		/* (2) For each active source, show a Wizard file selection page to choose 
		 * a destination file (for the translated XML, or the XSLT) */
		TranslateDestinationWizard destWizard = 
			new TranslateDestinationWizard(activeSources,false,"xml");
		destWizard.init(PlatformUI.getWorkbench(),(IStructuredSelection)selection);
	    WizardDialog dialog = new WizardDialog(WorkBenchUtil.getShell(),destWizard);
	    dialog.open();
	    
	    /*  (3) For each active source, do the translation. */
	    IFile XSLFile = getSelectedFile();
	    for (int i = 0; i < destWizard.destinationFiles().size(); i++) try 
	    {
	    	IFile destFile = destWizard.destinationFiles().get(i);
	    	DataSource ds = activeSources.get(i);
	    	IFile sourceFile = ds.getInstanceFile();
	    	if (!((sourceFile != null) && (sourceFile.exists()))) 
	    		showMessage("Non-existent data source at " + ds.instanceURIString());
	    	else doXSLTranslate(sourceFile,destFile,XSLFile);
	    }
	    catch (Exception ex) 
	    {
	    	GenUtil.surprise(ex,"Translating by XSLT");
	    	showMessage("Failed to do XSLT translation: " + ex.getMessage());
	    }
	}
	
	private void doXSLTranslate(IFile sourceFile,IFile destFile,IFile XSLFile)
	throws MapperException, CoreException
	{
		Document sourceDoc = XMLUtil.getDocument(sourceFile.getContents());
		EclipseFileUtil.writeTransformedOutputResource(sourceDoc, destFile, XSLFile);
	}

}
