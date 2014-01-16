package com.openMap1.mapper.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.w3c.dom.Element;

import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.WorkBenchUtil;

public class RestoreDataSourcesFromFile implements IObjectActionDelegate{


	public IWorkbenchPart targetPart; // where this action was invoked from
	public ISelection selection;

	public void run(IAction action) {

		try {
			
			// (1) find the location of the selected .dsr file of data sources
		    IFile DSRFile = getSelectedFile();
		    String dsrFilePath = DSRFile.getLocation().toString();
		    // System.out.println("Data Source file location: " + dsrFilePath);

		    // (2) get the root of the document, and use it to restore the data source view
		    Element root = XMLUtil.getRootElement(dsrFilePath);
		    DataSourceView dataSourceView = WorkBenchUtil.getDataSourceView(true);
		    dataSourceView.restoreViewFromDOM(root);
		    
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			WorkBenchUtil.showMessage("Error","Failed to restore data sources from file: " + ex.getMessage());
		}
		
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}


	//cache the target part so we can get the shell
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
	
	
	/**
	 * @return the dsr file containing the definition of the data sources
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

}
