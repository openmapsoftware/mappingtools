package com.openMap1.mapper.health.actions;

import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;

import com.openMap1.mapper.Annotations;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.actions.MapperActionDelegate;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;

public class MakeMappingsFromDataItems extends MapperActionDelegate
implements IObjectActionDelegate{
	
	
	public void run(IAction action) 
	{		
		try
		{
			if (selection instanceof IStructuredSelection)
			{
				Object el = ((IStructuredSelection)selection).getFirstElement();
				if (el instanceof IFile)
				{
					
					IFile file = (IFile)el;
					String fileLocation = EclipseFileUtil.getResourceLocation(file);
					message("Making mapping set from data items in csv file at " + fileLocation);
					int len = fileLocation.length();
					String mapperLocation = fileLocation.substring(0, len - 3) + "mapper";
					
					// (1) Read and parse the csv file
					Vector<String> csvLines = EclipseFileUtil.textLines(file);
					Vector<String[]> csvRows = new Vector<String[]>();
					int cols = 3;
					for (int i = 0; i < csvLines.size(); i++)
						csvRows.add(FileUtil.parseCSVLine(cols, csvLines.get(i)));
					
					// (2) Make and save the mapping set
					makeFlatMappedStructure(csvRows,mapperLocation);
				}
			}
		}
		catch (Exception ex) 
		{
			showMessage("Error","Failed to make mapping set from data items csv: " + ex.getMessage());
			ex.printStackTrace();
		}				
	}
	
	/**
	 * 
	 * @param csvRows
	 * @return
	 */
	private MappedStructure makeFlatMappedStructure(Vector<String[]>csvRows, String mapperLocation) throws MapperException
	{
		MappedStructure flatStructure = ModelUtil.saveNewMappingSet(mapperLocation);
		
		ElementDef root = MapperFactory.eINSTANCE.createElementDef();
		root.setName("root");
		flatStructure.setRootElement(root);
		
		ElementDef lastLeaf = null;
		for (int r = 1; r < csvRows.size(); r++)
		{
			String[]row = csvRows.get(r);
			// rows with a first column value define a new node
			if (!(row[0].equals("")))
			{
				if (lastLeaf != null) root.getChildElements().add(lastLeaf);
				lastLeaf = MapperFactory.eINSTANCE.createElementDef();
				lastLeaf.setName(row[0]);
				lastLeaf.setDescription(row[1]);
				lastLeaf.setType(row[2]);
			}
			// rows without a first column define a possible value for the previous node, to go in an annotation
			else if (row[0].equals(""))
			{
				StringTokenizer st = new StringTokenizer(row[1],"=");
				if (st.countTokens() > 0)
				{
					String key = st.nextToken();
					String value = "";
					if (st.hasMoreTokens()) value = st.nextToken();
					lastLeaf.addAnnotation(key, value);
				}
			}
		}
		if (lastLeaf != null) root.getChildElements().add(lastLeaf);
		
		// save the completed mapping set
		FileUtil.saveResource(flatStructure.eResource());
		
		return flatStructure;
	}


	
	private void message(String s) {System.out.println(s);}

}
