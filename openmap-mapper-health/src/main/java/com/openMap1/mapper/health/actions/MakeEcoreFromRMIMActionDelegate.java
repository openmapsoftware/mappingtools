package com.openMap1.mapper.health.actions;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.health.cda.CDATemplate;
import com.openMap1.mapper.health.cda.TemplateCollection;
import com.openMap1.mapper.health.cda.TemplateSet;
import com.openMap1.mapper.health.v3.RMIMReader;
import com.openMap1.mapper.structures.XSDStructure;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;

/**
 * Action to make an Ecore class model from  a V3 RMIM (.mif file)
 * 
 * @author robert
 *
 */
public class MakeEcoreFromRMIMActionDelegate 
implements IObjectActionDelegate{

	public IWorkbenchPart targetPart; // where this action was invoked from
	public ISelection selection;
	
	// if true, use a Java mapping class rather than making all the mappings explicitly
	private boolean useJavaMappings = true;
	
	
	@Override
	public void run(IAction action) {

		String mifFilePath = "";
		Element templateUsageRoot = null;
		
		/* (0) check that there is a data types schema at the expected location in the project 
		 * (this is just a check; the result is not used. RMIMReader calls V3DataTypeHandler to the same location)*/
		String projectName = getSelectedProject().getName();
		String structureFolder = "platform:/resource/" + projectName + "/Structures/";
		String schemaLocation = structureFolder + "coreschemas/datatypes.xsd";
		try 
		{
			URI uri = URI.createURI(schemaLocation);
			XSDSchema schema = XSDStructure.getXSDRoot(uri);
			if (schema == null) throw new MapperException("Data types schema is null");
		}
		catch (Exception ex)
		{
			showMessage("Cannot open V3 data types schema expected at '"
					+ schemaLocation + "': " + ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		// (1a) find the location of the selected MIF file
	    IFile MIFFile = getSelectedFile();
	    mifFilePath = MIFFile.getLocation().toString();
	    System.out.println("MIF file location: " + mifFilePath);
				
		try{
			// (1b) if the user chooses a CDA MIF, allow him to choose a template usage file
			if (RMIMReader.isCDAMIFName(mifFilePath)) 
			{
				String usageFilePath = getTemplateUsageFilePath();
				if (usageFilePath.equals("")) throw new MapperException("No Template Usage file supplied");
				else
				{
					templateUsageRoot = XMLUtil.getRootElement(usageFilePath);
					
					/* if the template usage file is new and empty, try  to make a default template usage 
					 * file by reading all the templates in sub-folders of the specified folder. */
					if (templateUsageRoot.getAttribute("new").equals("yes"))
					{
						String schematronFolderLocation = templateUsageRoot.getAttribute("schematronFolderLocation");
						makeInitialTemplateUsageFile(usageFilePath,schematronFolderLocation);
					}
					
					Vector<Element> templateSetEls = XMLUtil.namedChildElements(templateUsageRoot, "templateSet");
					if (templateSetEls.size() == 0) throw new MapperException("No template sets in template usage file");
				}
			}

			// (2) Open the RMIM MIF as an XML file
			Element MIFRoot = XMLUtil.getRootElement(mifFilePath);
			if (MIFRoot == null) throw new MapperException("Cannot opem MIF file at " + mifFilePath);

			//the ecore model is stored in the ClassModel folder of the same project as the selected MappedStructure
			String ecoreFolderPath = "platform:/resource/" + projectName + "/ClassModel/";

			/* (3) Read the MIF file into an ECore class model (and if there are any templates, read them) 
			 * useJavaMappings = true, so there is just one mapping set to invoke the Java mappings.
			 * The user may optionally choose a schema to order elements */
			XSDStructure xsd = FileUtil.userChooseStructure(targetPart);
			RMIMReader rmimReader = new RMIMReader(MIFRoot, xsd, mifFilePath, projectName,templateUsageRoot, useJavaMappings, targetPart);
			
			// save the Ecore file, with the same file name root as the MIF file 
			String filePath = ecoreFolderPath + rmimReader.mifFileRoot() + ".ecore";
			rmimReader.writePackage(filePath);
			
			WorkBenchUtil.showMessage("Completed", "Made Ecore model '" + rmimReader.mifFileRoot() + ".ecore' from MIF file");
			
			
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			showMessage("Failed to convert MIF file to Ecore model: " + ex.getMessage());
		}

	}
	
	//---------------------------------------------------------------------------------------------------
	//                 HL7 Clinical Document Architecture (CDA) and templates
	//---------------------------------------------------------------------------------------------------

	
	/**
	 * Allow the user to select a template usage file; 
	 * return "" if he cancels.
	 */
	private String getTemplateUsageFilePath()
	{
		String title = "Select Template Usage File";
		String[] exts = {"*.xml"};
		return FileUtil.getFilePathFromUser(targetPart,exts,title,false);			
	}

	
	
	
	/**
	 * make an initial template usage file, for later editing, by reading all schematrons
	 * in the sub-folders of a specified folder
	 * @param usageFilePath file path to the empty template usage file; modify the file name to make the new one
	 * @param schematronFolderLocation location of the schematron 
	 * @return the root element of the new template usage file
	 */
	private void makeInitialTemplateUsageFile(String usageFilePath, String schematronFolderLocation)
	throws MapperException
	{
		
		// make the new template usage file
		Document outDoc = XMLUtil.makeOutDoc();
		Element collection = XMLUtil.newElement(outDoc, "templateCollection");
		collection.setAttribute("schematronFolderLocation", schematronFolderLocation);
		collection.setAttribute("name", "blah");
		outDoc.appendChild(collection);

		// make a dummy template collection
		TemplateCollection templateCollection = new TemplateCollection(collection,null);
		
		// look for schematrons in the folder
		File folder = new File(schematronFolderLocation);
		String[] folders = folder.list();
		for (int i = 0; i < folders.length; i++)
		{
			String subFolderName = folders[i];
			StringTokenizer st = new StringTokenizer(subFolderName,".");
			if (st.countTokens() == 1)
			{
				String subFolderPath = schematronFolderLocation + "/" + subFolderName;
				File subFolder = new File(subFolderPath);
				String[] contents = subFolder.list();
				// find the '.sch' file(s) in each sub-folder
				for (int j = 0; j < contents.length; j++)
				{
					if (contents[j].endsWith(".sch"))
					{
						/* 10/2010: In one folder for C32 there are two .sch files, HandP.IHE.PCC.sch and HandP.sch 
						 * For the moment, I arbitrarily pick the latter. */
						if (!contents[j].endsWith(".PCC.sch"))
						{
							Element setEl = XMLUtil.newElement(outDoc, "templateSet");
							setEl.setAttribute("name", ("blah_" + i + j));
							setEl.setAttribute("schFileName", contents[j]);
							setEl.setAttribute("subfolder", subFolderName);
							collection.appendChild(setEl);
							
							Element[] levels = addEmptyLevels(outDoc,setEl);
							
							String schFilePath = subFolderPath + "/" + contents[j];
							TemplateSet tempSet = new TemplateSet(templateCollection,schFilePath,setEl);
							tempSet.readTemplateFile(schFilePath, true);
														
							for (Enumeration<String> en = tempSet.templates().keys(); en.hasMoreElements();)
							{
								String key = en.nextElement();
								CDATemplate template = tempSet.templates().get(key);
								Element tempEl = XMLUtil.newElement(outDoc, "template");
								tempEl.setAttribute("id", template.fullTemplateId());
								tempEl.setAttribute("name", template.getTrialTitle());
								
								for (Iterator<String> is = template.constrainedTemplateIds().iterator();is.hasNext();)
								{
									Element con = XMLUtil.textElement(outDoc, "constrains", is.next());
									tempEl.appendChild(con);
								}

								int level = template.heuristicLevel();
								levels[level].appendChild(tempEl);
							}
						}
					}
				}
			}
		}
		
		// save the new template usage file
		String relativeFilePath = FileUtil.resourceLocation(newUsageFilePath(usageFilePath));
		EclipseFileUtil.writeOutputResource(outDoc, relativeFilePath, true);
	}
	
	/**
	 * add empty <templateLevel> elements to the initial template usage file
	 * @param outDoc
	 * @param setEl
	 * @return
	 * @throws MapperException
	 */
	private Element[] addEmptyLevels(Document outDoc, Element setEl) throws MapperException
	{
		Element[] levels = new Element[4];
		for (int i = 0; i < 4; i++)
		{
			levels[i] = XMLUtil.newElement(outDoc, "templateLevel");
			levels[i].setAttribute("level", TemplateSet.levelNames[i]);
			setEl.appendChild(levels[i]);
		}
		return levels;
	}

	
	/**
	 * make a slightly different file name for the initial template usage file, 
	 * so it does not overwrite the empty template usage file which caused it to be created
	 * @param usageFilePath path to the empty template usage file
	 * @return path to the modified file name
	 * @throws MapperException
	 */
	private String newUsageFilePath(String usageFilePath) throws MapperException
	{
		String newPath = "";
		if (usageFilePath.endsWith(".xml"))
		{
			String path = usageFilePath.substring(0, usageFilePath.length()-4);
			newPath = path + "_1" + ".xml";
		}
		else throw new MapperException("Template usage file name does not end in '.xml'");
		return newPath;
	}
	
	//----------------------------------------------------------------------------------------------
	//                                    Eclipse plumbing
	//----------------------------------------------------------------------------------------------

	
	/**
	 * @return the project in which the selected mif file is located
	 */
	protected IProject getSelectedProject()
	{
		if (getSelectedFile() != null) return getSelectedFile().getProject();
		return null;
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


	//cache the target part so we can get the shell
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
	
	/**
	 * @return the file containing the selected MIF
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
