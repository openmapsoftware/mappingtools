package com.openMap1.mapper.presentation;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class FileSaverWizard  extends Wizard{
	
	private String wizardTitle;
	private String pageTitle;
	
	protected WizardNewFileCreationPage savedViewFileCreationPage;
	
	private IFile viewSaveFile = null;
	public IFile getViewSaveFile() {return viewSaveFile;}
	
	public FileSaverWizard(String wizardTitle, String pageTitle)
	{
		super();
		this.wizardTitle = wizardTitle;
		this.pageTitle = pageTitle;
	}
	
	public boolean performFinish() {
		viewSaveFile = savedViewFileCreationPage.createNewFile();
		return (viewSaveFile != null);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(wizardTitle);
	}

	public void addPages() {
		savedViewFileCreationPage = 
			new WizardNewFileCreationPage("FilePage", new StructuredSelection());
		savedViewFileCreationPage.setTitle(pageTitle);
		addPage(savedViewFileCreationPage);
	}

}
