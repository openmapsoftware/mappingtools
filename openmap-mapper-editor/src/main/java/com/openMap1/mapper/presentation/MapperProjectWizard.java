package com.openMap1.mapper.presentation;

import java.io.InputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;

import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IFolder;

import com.openMap1.mapper.util.EclipseFileUtil;

/**
 * Wizard to create a Mapper project.
 * 
 * This consists of the standard Mapper project folder structure,
 * and a text file template.query in the Query folder
 * @author robert
 *
 */
public class MapperProjectWizard extends Wizard implements INewWizard {
	
	private WizardNewMapperProjectCreationPage newProjPage;
	public WizardNewMapperProjectCreationPage newProjPage() {return newProjPage;}
	private IWorkbenchWindow workbenchWindow;
	
	// names of sub-folders in a Project folder
	public static String[] projectFolder = 
		{"ClassModel","Structures","MappingSets","Instances","Query","Results","Translators","Tests"};
	
	// indexes of folders
	public static int CLASSMODELFOLDER = 0;
	public static int STRUCTUREFOLDER = 1;
	public static int MAPPINGSETFOLDER = 2;
	public static int INSTANCEFOLDER = 3;
	public static int QUERYFOLDER = 4;
	public static int RESULTFOLDER = 5;
	public static int TRANSLATORFOLDER = 6;
	public static int TESTFOLDER = 7;

	@Override
	public boolean performFinish() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String projName = newProjPage.getProjectName();
		IProject project = root.getProject(projName);
		try {
			project.create(null);
			project.open(null);
			// make all the folders
			for (int f = 0; f < projectFolder.length; f++)
			{
				IFolder folder = project.getFolder(projectFolder[f]);
				folder.create(false, true, null);
				
				// create an empty query template, in the Query folder
				if (projectFolder[f].equals("Query"))
				{
					String fileName = "template.query";
					IFile templateFile = folder.getFile(fileName);
					InputStream fileStream = EclipseFileUtil.textStream("select   where");
					templateFile.create(fileStream, false, null);					
				}
			}
		}
		catch (Exception ex) 
		{
			MessageDialog.openError(workbenchWindow.getShell(), "Error creating mapper project: ", ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		workbenchWindow = workbench.getActiveWorkbenchWindow();
	}
	
	public void addPages()
	{
		newProjPage = new WizardNewMapperProjectCreationPage("Mapper project creation");
		newProjPage.setTitle("New Mapper Project");
		newProjPage.setDescription("A Mapper Project to hold the mappings of one or more data sources onto a class model");
		addPage(newProjPage);
	}
	
	/**
	 * This subclass has the ability to set the project name programmatically
	 * rather than by user input
	 * @author robert
	 *
	 */
	public class WizardNewMapperProjectCreationPage extends WizardNewProjectCreationPage{
		
		private boolean projectNameHasBeenSuppliedForTesting;
		private String projectNameSuppliedForTesting = "";
		
		WizardNewMapperProjectCreationPage(String name) 
		{
			super(name);
			projectNameHasBeenSuppliedForTesting = false;
		}
		
		public void supplyProjectNameForTesting(String projectName)
		{
			projectNameHasBeenSuppliedForTesting = true;
			projectNameSuppliedForTesting = projectName;
		}
		
		/**
		 * override the normal project name (as entered by the user)
		 * only if another project name has been supplied for testing
		 * (in which case the wizard was never shown)
		 */
		public String getProjectName()
		{
			if (!projectNameHasBeenSuppliedForTesting) return super.getProjectName();
			else return projectNameSuppliedForTesting;
		}
	}
	
	/*
	 * Template for WorkspaceModifyOperation
	 * (I think not necessary because project.create() and project.open() throw CoreExceptions 
	 * and therefore may be workspace modify operations

		try {
			WorkspaceModifyOperation operation =
				new WorkspaceModifyOperation() {
					protected void execute(IProgressMonitor progressMonitor) {
						try {
							
						} // end of try within execute
						catch (Exception ex) {}
						finally {progressMonitor.done();}
					} // end of execute
			}; // end of WorkspaceModifyOperation			
		} // end of try within performFinish
		catch (Exception ex) {}

	 *
	 */

}
