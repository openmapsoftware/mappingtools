package com.openMap1.mapper.actions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.query.DataSource;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

public class WriteCrossMappingsAction extends Action{
	
	private DataSourceView dataSourceView;
	
	private boolean tracing = true;
	private void trace(String s) {if (tracing) System.out.println(s);}

	private Vector<DataSource> chosenSources = new Vector<DataSource>();
	
	private IProject theProject;
	
	private IFolder mappingSetFolder;
	
	private Vector<String[]> mappingRows;
	
	//private String NO_MAPPINGS = "$No mappings in data source";
	
	private boolean testing = false;
	
	
	//-----------------------------------------------------------------------------------------
	//                                 constructor
	//-----------------------------------------------------------------------------------------
	
	public WriteCrossMappingsAction(DataSourceView dataSourceView)
	{
		this.dataSourceView = dataSourceView;
		setText("Write Cross-mappings Table");
		setToolTipText("Write a table of cross-mappings between active data sources");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		setEnabled(testing);
	}
	
	public void run()
	{
		trace("Writing cross-mappings");
		
		try
		{
			// (1)  Check that there are some active data sources 
			dataSourceView.refreshAllActiveSources(false);
			if (dataSourceView.getActiveDataSources().size() == 0)
				{throw new MapperException("There are no active data sources for which to write cross-mappings");}	
			
			// (2) choose the data sources, without duplicates
			chooseSourcesForCrossMappings();
			
			// (3) set up the IFile to be written to
			theProject = chosenSources.get(0).getProject();
			mappingSetFolder = theProject.getFolder("MappingSets");
			String fileName = "crossMappings_" + activeCodes() + ".csv";
			IFile crossMappingsFile = mappingSetFolder.getFile(fileName);
			if (crossMappingsFile.exists()) crossMappingsFile.delete(true, null);
			
			// (4) Initialise the rows to be written to the table
			mappingRows = new Vector<String[]>();
			makeHeaderRow();
			
			// (5) find the root class of the mapped class model
			EPackage mappedModel = chosenSources.get(0).getFreshMappedStructure().getClassModelRoot();
			if (mappedModel == null) throw new MapperException("Cannot find mapped class model");
			LabelledEClass rootLabelledClass = ClassModelView.getRootLabelledEClass(mappedModel);
			if (rootLabelledClass == null) throw new MapperException("There is no entry class for the mapped class model");
			EClass rootClass = rootLabelledClass.eClass();
			
			// (6) recursive descent of the class model
			String[] subsets = new String[chosenSources.size()];
			// assume that mappings to the root class all have only the subset ""
			for (int i = 0; i < chosenSources.size(); i++) subsets[i] = "";
			String assocName = "";
			addMappingRows(assocName,rootClass,subsets);
			
			// (7) write out the file
			EclipseFileUtil.writeCSVFile(mappingRows, crossMappingsFile);

		}
		catch (Exception ex) 
		{
			WorkBenchUtil.showMessage("Error", ex.getMessage());
			ex.printStackTrace();
		}

	}
	
	
	private void chooseSourcesForCrossMappings() throws MapperException
	{
		chosenSources = new Vector<DataSource>();
		// to avoid choosing more than one data source for any mapping set
		Hashtable<String,XOReader> mappingSetsByURI = new Hashtable<String,XOReader>();
		for (Iterator<DataSource> it = dataSourceView.getActiveDataSources().iterator(); it.hasNext();)
		{
			DataSource ds = it.next();
			// happens once per mapping set; choose the first active data source per mapping set
			if (mappingSetsByURI.get(ds.mappingSetURIString()) == null)
			{
				mappingSetsByURI.put(ds.mappingSetURIString(), ds.getReader());
				chosenSources.add(ds);
			}
		}
	}
	
	private String activeCodes()
	{
		String codes = "";
		for (int i = 0; i < chosenSources.size();i++)
		{
			String code = chosenSources.get(i).getCode();
			codes = codes + code;
		}
		return codes;
	}
	
	private void makeHeaderRow()
	{
		int size = 3 + chosenSources.size();
		String[] row = new String[size];
		row[0] = "Association";
		row[1] = "Class";
		row[2] = "Property";
		for (int i = 0; i < chosenSources.size();i++)
		{
			String code = chosenSources.get(i).getCode();
			row[3 + i] = "XPath in source " + code;
		}
		mappingRows.add(row);
	}
	
	/**
	 * recursive descent of the mapped class model as far as there are mappings in any mapping set,
	 * writing rows of mapping XPaths to the csv file
	 * @param theClass
	 * @param subsets
	 */
	private void addMappingRows(String assocName, EClass theClass, String[] subsets)
	{
		String[] mappingRow = new String[3 + chosenSources.size()];
		mappingRow[0] = assocName;
		mappingRow[1] = ModelUtil.getQualifiedClassName(theClass);

		// write a row for the object mappings
		mappingRow[2] = "";
		
		// write rows for the property mappings
		
		// recursive descent through containment associations
	}


}
