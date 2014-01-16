package com.openMap1.mapper.actions;

import java.util.Enumeration;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import com.openMap1.mapper.core.MapperException;

import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.LicenceDialog;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.presentation.TranslateDestinationWizard;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.SOAPClient;

import com.openMap1.mapper.query.DataSource;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.writer.MappedXMLWriter;
import com.openMap1.mapper.writer.XMLObjectGetter;
import com.openMap1.mapper.writer.objectGetter;
import com.openMap1.mapper.writer.ProcedureCompiler;
import com.openMap1.mapper.writer.ProcedureWriterStub;
import com.openMap1.mapper.MappedStructure;

import java.util.Vector;

/**
 * superclass for action classes TranslateActionDelegate to do a translation
 * from every active data source to the selected mapping set,
 * and MakeXSLTActionDelegate to generate XSLT.
 * 
 * In both cases the sequence is :
 * 
 * (1) Find the active sources in the sources view, and check that they have the same class
 *  model as the mapping set for the structure being translated to; 
 *  if not show an error dialogue
 *  
 * (2) For each active source, show a Wizard file selection page to choose a destination file
 * (for the translated XML, or the XSLT) 
 * 
 * (3) Check if the mapping set has an up-to-date writing procedure file; if not, create it 
 * using interface ProcedureCompiler
 * 
 * (4) For each active source, either do the translation using class Translator, or
 * generate the XSLT using class XSLGenerator
 * 
 * @author robert
 *
 */

public abstract class MakeTranslationActionDelegate extends MapperActionDelegate
implements IObjectActionDelegate {
	
	protected boolean runTracing = false;
	
	protected boolean tracing = false;
	
	abstract public boolean isXSLTGeneration();
	
	public void run(IAction action) {
	
	try{
		
		// (0) for XSLT Generation, check that the user has a licence
		if (isXSLTGeneration())
		{
			String email = SOAPClient.getStoredEmail();
			if (email == null)
				throw new MapperException("You cannot generate XSLT without first running some Java translations.");
			
			boolean newKeySupplied = false;
			String xslKey = SOAPClient.getXSLTKey(); // existing key, if there is one; "" otherwise
			
			// only one chance to set the licence key and accept the licence terms
			if (!LicenceDialog.isValidXSLKey(xslKey))
			{
				newKeySupplied = true;
				Shell shell = targetPart.getSite().getShell();
				LicenceDialog licenceDialog = new LicenceDialog(shell, "Mapper XSLT Generator License", LicenceDialog.XSLT_licence_text());
				xslKey = (String)licenceDialog.open();
			}
			if (xslKey.equals(LicenceDialog.DECLINED))
				throw new MapperException("Licence terms declined");
			if (!LicenceDialog.isValidXSLKey(xslKey))
				throw new MapperException("Invalid licence key");
			
			// if a valid new licence key has been supplied, save it
			if (newKeySupplied) SOAPClient.setXSLTKey(xslKey);
		}

		/* (1) check that there are some active data sources, 
		 * and that the class model of the mapping set is that of the active sources */
		DataSourceView dataSourceView = WorkBenchUtil.getDataSourceView(true);
		if (dataSourceView == null) 
		   {showMessage("There is no data source view to define sources for the translation");return;}
		Vector<DataSource> activeSources = dataSourceView.getActiveDataSources();
		if (activeSources.size() == 0)
		   {showMessage("There are no active data sources for the translation");return;}
		DataSource ds1 = activeSources.get(0);
		String targetClassModel = mappedStructure().getUMLModelURL();
		String sourceClassModel = ds1.getReader().ms().getUMLModelURL();
		if (!sourceClassModel.equals(targetClassModel))
		   {showMessage("Data source mappings and target mappings do not have the same class model");return;}
		
		/* (2) For each active source, show a Wizard file selection page to choose 
		 * a destination file (for the translated XML, or the XSLT) */
		String resultFileExtension = mappedStructure().getExtensions()[0].substring(2);
		TranslateDestinationWizard destWizard = 
			new TranslateDestinationWizard(activeSources,isXSLTGeneration(),resultFileExtension);
		destWizard.init(PlatformUI.getWorkbench(),(IStructuredSelection)selection);
	    WizardDialog dialog = new WizardDialog(WorkBenchUtil.getShell(),destWizard);
	    dialog.open();
	    
	    /* (3) make up-to-date writing procedure files for the mapping set and all 
	     * those it imports; (done remotely using ProcedureCompilerStub)  */
	    makeAllProcedureFiles(mappedStructure());
	    
	    /*  (4) For each active source, either do the translation using class Translator, or
	     *      generate the XSLT using class XSLGenerator  */
	    for (int i = 0; i < destWizard.destinationFiles().size(); i++) 
	    {
	    	IFile destFile = destWizard.destinationFiles().get(i);
	    	DataSource ds = activeSources.get(i);
	    	XOReader reader = ds.getReader();
	    	objectGetter oGet = null;
	    	if (reader instanceof MDLXOReader)
    		   oGet = new XMLObjectGetter((MDLXOReader)reader);
	    	else if (reader instanceof objectGetter)
	    		oGet = (objectGetter)reader;
	    	else throw new MapperException("Cannot make objectGetter");

	    	if (!isXSLTGeneration())
	    	{
	    		MappedXMLWriter translator = new MappedXMLWriter(EclipseFileUtil.proceduresFile(mappedStructure()),oGet,
	    				mappedStructure(), null, destFile, runTracing);	    		
	    		translator.writeXML();
	    	}
	    	else if (isXSLTGeneration())
	    	{
	    		doXSLTGeneration(reader,destFile,oGet);
	    	}
	    }
	}
	catch (Exception ex) 
	    {
	    	GenUtil.surprise(ex,"Translating");
	    	showMessage("Failed to run writing procedures: " + ex.getMessage());
	    }		
	}
	
	/**
	 * this method is defined to remove the compiler dependence of 
	 * MakeTranslationSActionDelegate on the class that implements interface XSLGenerator
	 * @param reader
	 * @param destFile
	 * @param oGet
	 */
	abstract public void doXSLTGeneration(XOReader reader,IFile destFile,objectGetter oGet) throws MapperException;
	
	/**
	 * Make and store the wproc files for a mapping set and all those
	 * which it imports, directly or indirectly
	 * @param mappedStructure
	 * @throws MapperException
	 * @throws CoreException
	 */
	public static void makeAllProcedureFiles(MappedStructure mappedStructure)
	throws MapperException, CoreException
	{
		for (Enumeration<MappedStructure> en = mappedStructure.getAllImportedMappingSets().elements();en.hasMoreElements();)
			makeProcedureFile(en.nextElement());
	}
	
	/**
	 * make and store a wproc file
	 * @param mappedStructure the mapping set it corresponds to
	 * @return the ProcedureCompiler that wrote the IFile (and knows the CompilationIssues)
	 * @throws MapperException
	 * @throws CoreException
	 */
	public static ProcedureCompiler makeProcedureFile(MappedStructure mappedStructure)
		throws MapperException, CoreException
	{
		// make a handle for the procedures file in the correct sub-folder of the Translators folder
    	IFile proceduresFile = EclipseFileUtil.proceduresFile(mappedStructure);
    	// (the handle should never be null)
    	if (proceduresFile == null) throw new MapperException("Null Procedures file");

    	// (message channel is null - the work is done on the server)
    	ProcedureCompiler procWriter =  new ProcedureWriterStub(mappedStructure,null);

    	// create the write procedures and save them
    	boolean codeTrace = false; // trace is on the server
    	procWriter.generateProcedures(proceduresFile,codeTrace);
    	
    	return procWriter;
	}
	
	protected void trace(String s) {if (tracing) System.out.println(s);}

}
