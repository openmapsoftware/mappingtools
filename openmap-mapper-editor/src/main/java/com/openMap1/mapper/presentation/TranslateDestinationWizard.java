package com.openMap1.mapper.presentation;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import com.openMap1.mapper.query.DataSource;


/**
 * Allow the user to choose one destination file for each active data source.
 * The file either holds the results of a translation (xml) or generated XSLT (xsl)
 * @author robert
 *
 */
public class TranslateDestinationWizard  extends Wizard{
	
	private Vector<DataSource> activeSources;
	
	private Vector<IFile> destinationFiles = new Vector<IFile>();
	public Vector<IFile> destinationFiles() {return destinationFiles;}
	
	private boolean isXSLTGeneration;
	
	// required extension for result files ='xml' or 'txt'
	private String resultExtension;
	
	protected Vector<WizardNewFileCreationPage> pages 
	= new Vector<WizardNewFileCreationPage>();
		
	public TranslateDestinationWizard(Vector<DataSource> activeSources, 
			boolean isXSLTGeneration,String resultExtension)
	{
		super();
		this.activeSources = activeSources;
		this.isXSLTGeneration = isXSLTGeneration;
		this.resultExtension = resultExtension;
	}
	
	/**
	 * The wizard only succeeds if files are created for all active data sources
	 */
	public boolean performFinish() {
		boolean success =true;
		for (Iterator<WizardNewFileCreationPage> it = pages.iterator();it.hasNext();)
		{
			WizardNewFileCreationPage page = it.next();
			IFile file = page.createNewFile();
			success = success && (file != null) && (file.exists());
			if (success) destinationFiles.add(file);
		}
		if (!success) destinationFiles = new Vector<IFile>();
		return success;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Choose destination files for translation");
		if (isXSLTGeneration) setWindowTitle("Choose destination files for generated XSLT");
	}

	public void addPages() {
		for (Iterator<DataSource> it = activeSources.iterator();it.hasNext();)
		{
			DataSource ds = it.next();
			WizardNewFileCreationPage destinationPage = 
				new WizardNewFileCreationPage("FilePage", new StructuredSelection());
			String pageTitle = "Choose a new file to store the result of translation from data source "
				+ ds.instanceURIString();
			destinationPage.setFileExtension(resultExtension);
			if (isXSLTGeneration) 
			{
				pageTitle = "Choose a new file to store generated XSLT to translate from mapped source "
					+ ds.mappingSetURIString();
				destinationPage.setFileExtension("xsl");				
			}
			destinationPage.setTitle(pageTitle);
			addPage(destinationPage);	
			pages.add(destinationPage);
		}
	}

}
