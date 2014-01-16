package com.openMap1.mapper.actions;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;

/**
 * Abstract superclass for actions invoked on a set of mappings
 * @author robert
 *
 */
public abstract class MapperActionDelegate implements IObjectActionDelegate {

	public IWorkbenchPart targetPart; // where this action was invoked from
	public ISelection selection;
	
	protected boolean tracing = true;
	

	@Override
	//cache the target part so we can get the shell
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
	
	/**
	 * @param selection the object the user right-clicked to get this action
	 * @return the file path to it
	 */
	public String getFilePath(ISelection selection)
	{
		String path = "not found";
		if (selection instanceof IStructuredSelection)
		{
			Object el = ((IStructuredSelection)selection).getFirstElement();
			if (el instanceof IFile)
			{
				IFile file = (IFile)el;
				path = file.getLocationURI().toString();
			}
		}
		return path;		
	}
	
	/**
	 * @param selection
	 * @return the selected IFile
	 */
	public IFile getIFile(ISelection selection)
	{
		IFile file = null;
		if (selection instanceof IStructuredSelection)
		{
			Object el = ((IStructuredSelection)selection).getFirstElement();
			if (el instanceof IFile) file = (IFile)el;
		}
		return file;		
	}

	
	/**
	 * given a selection in some workbench part, try to open it with the MapperEditor
	 * @param selection the selection
	 * @return MapperEditor the editor, or null if there is any failure
	 */
	public MapperEditor OpenMapperEditor(ISelection selection)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		boolean mappingsOpened = false;
		IEditorPart editorPart = null;
		if (selection instanceof IStructuredSelection)
		{
			Object el = ((IStructuredSelection)selection).getFirstElement();
			if (el instanceof IFile)
			{
				IFile file = (IFile)el;
				try{
					editorPart = IDE.openEditor(page,file, "com.openMap1.mapper.presentation.MapperEditorID");
					mappingsOpened = true;
				}
				catch(PartInitException ex) 
				{
					showMessage("Exception opening mapper editor: " + ex.getMessage());
				}
			}
		}
		if ((mappingsOpened) && (editorPart instanceof MapperEditor))
		{
			return (MapperEditor)editorPart;
		}
		// catch-all for failures
		showMessage("Failed to open mappings file with mappings editor");
		return null;
	}
	
	@Override
	public void run(IAction action) {

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	protected void showMessage(String title, String message) {
		MessageDialog.openInformation(
			targetPart.getSite().getShell(),
			title,
			message);
	}

	/**
	 * default if you can't be bothered to make up a message title
	 * @param message
	 */
	protected void showMessage(String message) 
		{showMessage("Error",message);}
	
	/**
	 * @return the selected mapped structure
	 */
	public MappedStructure mappedStructure()
	{
		if (getSelectedFile() != null) try
		{
			URI fileURI = URI.createPlatformResourceURI(getSelectedFile().getFullPath().toString(),false);
			return FileUtil.getMappingSet(fileURI);
		}
		catch (MapperException ex) {showMessage(ex.getMessage());} 
		return null;
	}
	
	/**
	 * @return the file containing the selected mapping set or the selected XSLT
	 */
	protected IFile getSelectedFile()
	{
		if (selection instanceof IStructuredSelection)
		{
			Object el = ((IStructuredSelection)selection).getFirstElement();
			if (el instanceof IFile) return (IFile)el;
		}
		return null;
	}
	
	protected String getMapperFileRoot()
	{
		if (getSelectedFile() != null)
		{
			String fileName = getSelectedFile().getName();
			String extension = ".mapper";
			return fileName.substring(0,fileName.length() - extension.length());
		}
		return null;
	}
	
	/**
	 * @return the project in which the selected mapping set is located
	 */
	protected IProject getSelectedProject()
	{
		if (getSelectedFile() != null) return getSelectedFile().getProject();
		return null;
	}

	/**
	 * update the property 'UMLModelURL' of the top 'MappedStructure' node (but do not save the file yet)
	 */
	public boolean setUMLModelPathProperty(MapperEditor me, String path)
	{
		// check the mappings have been opened in the editor
		MappedStructure ms  = WorkBenchUtil.mappingRoot(me);
		if (ms == null) return false;
		
		/* if possible, convert the path to a 'platform:/resource/' URI string;
		 * otherwise convert it to a 'file:/' URI string  */
		String storedPath = FileUtil.URIFromPath(path).toString();
		
		// make an editing command to set the URL, and execute it
		EditingDomain ed = me.editingDomain();		
		SetCommand sc = new SetCommand(ed,ms,
					MapperPackage.eINSTANCE.getMappedStructure_UMLModelURL(),
					storedPath);
		ed.getCommandStack().execute(sc);	
		return true;
	}
	
	/**
	 * Show the class model in the class model view
	 * @param ecoreRoot the EObject root of the class model
	 */
	public void showClassModel(MapperEditor me, EObject ecoreRoot, String path)
	{
		ClassModelView cmv = WorkBenchUtil.getClassModelView(true);
		if (cmv != null)
		{
			cmv.initiateForMapperEditor(me, ecoreRoot, path);
		}
		else
		{
			showMessage("Cannot find class model view");
		}		
	}
	
	/**
	 * 
	 * @return the root package of the Ecore model selected for this menu selection
	 * @throws MapperException
	 */
	public EPackage getSelectedEcoreModel() throws MapperException
	{
	    EPackage topPackage = null;
		try
		{
		     if (!(selection instanceof IStructuredSelection))
		    	 throw new MapperException("Selection is not structured");
		    IStructuredSelection structured = (IStructuredSelection)selection;
		    Object object = structured.getFirstElement();
		    URI ecoreURI = null;
		    if (object instanceof IFile) {
		         IFile file = (IFile) object;
		         ecoreURI = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		 		 EObject root = FileUtil.getEMFModelRoot(ecoreURI);
		 		 if (root == null)throw new MapperException("Cannot find ecore model root");
		 		 if (!(root instanceof EPackage))
		 			 throw new MapperException("Selected file is not an ecore model");
		 		 topPackage = (EPackage)root;
		 		 trace("Top package name " + topPackage.getName());
		    }
		    else throw new MapperException("Selection is not a file");				
		}
		catch (IOException ex) {throw new MapperException(ex.getMessage());}
		return topPackage;
		
	}
	
	protected void trace(String s)
	{
		if (tracing) System.out.println(s);
	}

}
