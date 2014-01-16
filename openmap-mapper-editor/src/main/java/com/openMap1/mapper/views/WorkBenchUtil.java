package com.openMap1.mapper.views;

import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.dialogs.ListDialog;

import org.eclipse.swt.widgets.Shell;


import com.openMap1.mapper.presentation.FileSaverWizard;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.presentation.QueryEditor;
import com.openMap1.mapper.MappedStructure;

/**
 * A set of static utility methods to do with the Eclipse workbench and
 * its user interface.
 * 
 * @author robert
 *
 */
public class WorkBenchUtil {
	
	public static IWorkbenchPage page()
		{return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();}
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the class model view 
	 */
	public static ClassModelView getClassModelView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.ClassModelView";
		return (ClassModelView)getView(id, forceCreate);
	} 
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the data source view 
	 */
	public static DataSourceView getDataSourceView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.DataSourceView";
		return (DataSourceView)getView(id, forceCreate);
	}
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the translation issue view 
	 */
	public static TranslationIssueView getTranslationIssueView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.TranslationIssueView";
		return (TranslationIssueView)getView(id, forceCreate);
	}
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the translation summary view 
	 */
	public static  TranslationSummaryView getTranslationSummaryView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.TranslationSummaryView";
		return (TranslationSummaryView)getView(id, forceCreate);
	}
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the attribute view 
	 */
	public static AttributeView getAttributeView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.AttributeView";
		return (AttributeView)getView(id, forceCreate);
	}
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the association view 
	 */
	public static AssociationView getAssociationView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.AssociationView";
		return (AssociationView)getView(id, forceCreate);
	}
		
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the query result view 
	 */
	public static QueryResultView getQueryResultView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.QueryResultView";
		return (QueryResultView)getView(id, forceCreate);
	}
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the query result view 
	 */
	public static MappingsView getMappingsView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.MappingsView";
		return (MappingsView)getView(id, forceCreate);
	}
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the debug view 
	 */
	public static DebugView getDebugView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.DebugView";
		return (DebugView)getView(id, forceCreate);
	}
	
	/**
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return the debug instance view 
	 */
	public static DebugInstanceView getDebugInstanceView(boolean forceCreate)
	{
		String id = "com.openMap1.mapper.views.DebugInstanceView";
		return (DebugInstanceView)getView(id, forceCreate);
	}
	
	/**
	 * @param id the id of a view
	 * @param forceCreate when true, if the view does not yet exist, create and show it.
	 * When false, if the view does not yet exist, return null
	 * @return a view 
	 */
	public static IViewPart getView(String id, boolean forceCreate)
	{
		IViewPart iv = null;
		if (page() != null)
		{
			iv = page().findView(id);
			if ((iv == null) && forceCreate)
				try {iv = page().showView(id);}
				catch (PartInitException ex) 
					{System.out.println("View problem: " + ex.getMessage());}			
		}
		return iv;		
	}

	/**
	 * find the MappedStructure root node of the mapping set connected to the editor
	 * @param me the MapperEditor
	 * @return MappedStructure root node of the mapping set, or  null
	 */
	public static MappedStructure mappingRoot(MapperEditor me)
	{
		URI resourceURI = EditUIUtil.getURI(me.getEditorInput());
		EditingDomain ed = me.editingDomain();
		boolean loadOnDemand = true; // see org.eclipse.emf.ecore.resource.ResourceSet for complex explanation 
		Resource mappingResource = ed.getResourceSet().getResource(resourceURI, loadOnDemand);			
		Object mappingRoot = mappingResource.getContents().get(0);	
		if (mappingRoot instanceof MappedStructure) return (MappedStructure)mappingRoot;
		return null;
	}
	
	/**
	 * 
	 * @param mappingSetURIString
	 * @return the editor instance which is editing this mapping set;
	 * or null if it cannot be found
	 */
	public static MapperEditor getMapperEditor(String mappingSetURIString)
	{
		MapperEditor me = null;
		if (page() != null)
		{
			IEditorReference[] editorRef = page().getEditorReferences();
			for (int i = 0; i < editorRef.length; i++)
			{
				IEditorPart ep = editorRef[i].getEditor(false);
				if ((ep != null) && (ep.getEditorInput() != null))
				{
					URI resourceURI = EditUIUtil.getURI(ep.getEditorInput());					
					if (resourceURI.toString().equals(mappingSetURIString)) 
						me = (MapperEditor)ep;					
				}
			}
		}
		return me;
	}
	/**
	 * write out the URIs of the files being worked on by all editors
	 */
	public static void writeEditorURIs()
	{
		if (page() != null)
		{
			IEditorReference[] editorRef = page().getEditorReferences();
			for (int i = 0; i < editorRef.length; i++)
			{
				IEditorPart ep = editorRef[i].getEditor(false);
				if ((ep != null) && (ep.getEditorInput() != null))
				{
					URI resourceURI = EditUIUtil.getURI(ep.getEditorInput());	
					System.out.println(resourceURI.toString());
				}
			}
		}
	}
	
	/**
	 * 
	 * @param queryURIString
	 * @return the editor instance which is editing this query;
	 * or null if it cannot be found
	 */
	public static QueryEditor getQueryEditor(String mappingSetURIString)
	{
		QueryEditor qe = null;
		if (page() != null)
		{
			IEditorReference[] editorRef = page().getEditorReferences();
			for (int i = 0; i < editorRef.length; i++)
			{
				IEditorPart ep = editorRef[i].getEditor(false);
				if ((ep != null) &&(ep.getEditorInput() != null))
				{
					URI resourceURI = EditUIUtil.getURI(ep.getEditorInput());
					if (resourceURI.toString().equals(mappingSetURIString)) 
						qe = (QueryEditor)ep;					
				}
			}
		}
		return qe;
	}
	
	/**
	 * @return a shell foe messages and dialogues
	 */
	public static Shell getShell()
	{
		return PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
	}
	
	/**
	 * show an informative message with an OK button to continue
	 * @param title
	 * @param message
	 */
	public static void showMessage(String title, String message)
	{
		MessageDialog.openInformation(
				getShell(),
				title,
				message);		
	}
	
	/**
	 * ask the user to confirm some action
	 * @param title
	 * @param message
	 * @return true if the user confirms
	 */
	public static boolean askConfirm(String title, String message)
	{
		return MessageDialog.openConfirm(
				getShell(),
				title,
				message);		
	}
	
	/**
	 * ask the user for some text input, and block until it has been provided
	 * @param title
	 * @param message
	 * @return the users input, or null if cancelled
	 */
	public static String askInput(String title, String message)
	{
		InputDialog dialog = new InputDialog(getShell(),title, message,null,null);
		dialog.setBlockOnOpen(true);
		dialog.open();
		String input = dialog.getValue();
		if (dialog.getReturnCode() == Window.CANCEL) input = null;
		return input;
	}
	
	/**
	 * ask for a user name and password for a database
	 * @param databaseURL
	 * @return an array [user name, password].
	 * if there is any cancel, put a null in the user name.
	 */
	public String[] getUserNameAndPassword(String databaseURL)
	{
		String[] result = new String[2];
		
		// ask for user name
		result[0] = askInput("User Name", "User name for database at " + databaseURL);
		
		// ask for password; but if there is a cancel on the user name, go no further
		if (result[0] !=null) result[1] = askInput("Password", "Password for database at " + databaseURL);

		// if there is a cancel on the password, make the user name null (password may be "")
		if (result[1] == null) result[0] = null;
				
		return result;
	}
	
	
	/**
	 * Let the user choose one of a list of Strings
	 * @param title title of the dialog
	 * @param targetPart IWorkBenchPart to call this from
	 * @param choices Vector of Strings to choose from
	 * @return index of the choice; or -1 if the user cancelled
	 */
	public static int chooseOneString(String title, IWorkbenchPart targetPart, Vector<String> choices)
	{
		int pos = -1;
		ListDialog listDialog = new ListDialog(targetPart.getSite().getShell()); 
		listDialog.setContentProvider(new ArrayContentProvider());
		listDialog.setLabelProvider(new LabelProvider());
		listDialog.setTitle(title);
		listDialog.setInput(choices.toArray());
		listDialog.open();
		if (listDialog.getReturnCode()== ListDialog.CANCEL) return pos;
		try
		{
			String chosen = (String)(listDialog.getResult()[0]);
			for (int i = 0; i < choices.size(); i++) if (choices.get(i).equals(chosen)) pos = i;			
		}
		catch (Exception ex)
		{
			showMessage("Error","You must choose a root class for the EMF Ecore instance");
		}
		return pos;
	}

	
	public static IFile makeOutputIFile(String wizardTitle, String pageTitle)
	{
		FileSaverWizard wizard = new FileSaverWizard(wizardTitle,pageTitle);
		wizard.init(PlatformUI.getWorkbench(), null);
	    WizardDialog dialog = new WizardDialog(WorkBenchUtil.getShell(),wizard);
	    dialog.open();
	    return wizard.getViewSaveFile();
	}
	
	/**
	 * 
	 * @return the open Mapper Editor, if one is open
	 */
	public static MapperEditor getOpenMapperEditor()
	{
		IEditorPart part = page().getActiveEditor();
		if ((part !=null) && (part instanceof MapperEditor)) return (MapperEditor)part;
		return null;
	}

}
