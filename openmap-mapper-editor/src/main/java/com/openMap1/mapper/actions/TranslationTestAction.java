package com.openMap1.mapper.actions;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.List;

import org.w3c.dom.Element;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.widgets.Shell;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.openMap1.mapper.query.DataSource;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.StructureMismatch;
import com.openMap1.mapper.core.SchemaMismatch;
import com.openMap1.mapper.core.SemanticMismatch;
import com.openMap1.mapper.core.TranslationIssue;
import com.openMap1.mapper.core.ValidationIssue;
import com.openMap1.mapper.core.RunIssue;
import com.openMap1.mapper.core.TranslationSummaryItem;
import com.openMap1.mapper.core.CompilationIssue;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.XpthException;

import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.EcoreMatcher;
import com.openMap1.mapper.util.SystemMessageChannel;
import com.openMap1.mapper.util.Timer;
import com.openMap1.mapper.util.XMLUtil;

import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.GenericEMFInstanceFactoryImpl;
import com.openMap1.mapper.reader.EMFInstanceFactory;
import com.openMap1.mapper.reader.XOReader;

import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.WorkBenchUtil;

import com.openMap1.mapper.writer.ProcedureCompiler;
import com.openMap1.mapper.writer.XMLWriter;
import com.openMap1.mapper.writer.XMLObjectGetter;
import com.openMap1.mapper.writer.objectGetter;
import com.openMap1.mapper.MappedStructure;


/**
 * This class makes a comprehensive test of translations between the N active data
 * sources (with codes A,B, C..). It tests all self-translations (A=>A via the class model),
 * all pairwise translations A=>B, all simple round-trips A=>B=>A, 
 * and one 'grand round trip' A=>B=>C=>A.
 * 
 * It does this by going through the following steps:
 * 
 * (0) Check that there are some active data sources, and choose the first active data source
 * for each mapping set to use in the tests
 * 
 * (1) Clear all files (resulting from previous tests) from the 'Tests' folder. Clear
 * the Translation Test Summary and Translation Issue views.
 * 
 * (2) Validate each mapping set and prepare write procedure files (*.wproc) for all mapping sets involved, 
 * in the 'Translators' folder. 
 * 
 * (3)  Do all translations, putting the results in the 'Tests' folder
 * 
 * (4) Test that each translation source or result conforms to the structure recorded in
 * its mapping set, or record discrepancies 
 * (done twice - once against the schema, once against the mapped structure)
 * 
 * (5) Make Ecore model instances from all translation sources and targets,
 * putting them in the 'Tests' folder
 * 
 * (6) For every translation, test that the result Ecore model instance 
 * is a subset of the source Ecore instance, or record discrepancies 
 * 
 * (7) For every translation, check that the gaps in the result ECore instance 
 * (with respect to the source Ecore instance) are all expected because of gaps 
 * in the mappings (eg for a self-translation A=>A, there should be no gaps); 
 * or record discrepancies
 * 
 * (8) Summarise results for each tested translation in the Translation Summary view, 
 * and show all discrepancies in the Test Issues view. Save these views in the Tests folder.
 * 
 * @author robert
 *
 */
public class TranslationTestAction  extends Action{
	
	private boolean validateMappingSets = true;
	
	private boolean tracing = false; // for a trace of general progress
	
	private boolean doRunTracing = false; // for a trace of the XML Writer
	
	private Timer timer;// for performance testing
	
	private Shell shell; // for error messages
	
	private DataSourceView dataSourceView;
	
	private Vector<DataSource> chosenSources = new Vector<DataSource>();
	
	private IProject theProject;
	
	private IFolder testFolder;
	
	// all the chains of translation that need to be done to  complete the tests.
	private Vector<TranslationChain> translationChains = new Vector<TranslationChain>();
	
	// all mismatches of structure or semantics between translation result file (or source files)
	private Vector<TranslationIssue> allMismatches = new Vector<TranslationIssue>();
	
	// for each source or translation result, keep a summary of all translation issues
	private Hashtable<String,TranslationSummaryItem> translationSummaryItems;
	
	// all Ecore model instances, keyed by the name root, eg 'ABC'
	private Hashtable<String,Resource> ecoreInstances = new Hashtable<String,Resource>();
	
	//-----------------------------------------------------------------------------------------
	//                                 constructor
	//-----------------------------------------------------------------------------------------
	
	public TranslationTestAction(Shell shell, DataSourceView dataSourceView)
	{
		this.shell = shell;
		this.dataSourceView = dataSourceView;
		setText("Translation Test");
		setToolTipText("Test generated translations by traverses and round-trips between all active data sources");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		// setActionDefinitionId("TranslationTest");
		timer = new Timer("Translation Test");		
	}

	public void run() {
		
		String failureMessage = "";
		try{
			// initialise
			trace("Start translation test");
			timer.start(Timer.TRANSLATION_TEST);
			
			/* (0)  Check that there are some active data sources, and choose the first active data source
	         * for each mapping set to use in the tests. No chance to reset the password for an RDB source */
			dataSourceView.refreshAllActiveSources(false);
			if (dataSourceView.getActiveDataSources().size() == 0)
				{showMessage("There are no active data sources with which to test translations");return;}
			failureMessage = "Failed to choose data sources for test: ";
			chooseSourcesForTest();
			trace("Refreshed data sources");
			
			/* (1) Clear out the 'Tests' folder of the appropriate project (the project
			 * which contains the common class model of all the data sources),
			 * and do other initialisation */
			failureMessage = "Failed to clear out test folder: ";
			clearTestFolder();
			allMismatches = new Vector<TranslationIssue>();
			translationSummaryItems = new Hashtable<String,TranslationSummaryItem>();
			ecoreInstances = new Hashtable<String,Resource>();
			trace("Cleared test folder");
			
			/* (2) Prepare write procedure files for all mapping sets involved */
			failureMessage = "Failed to prepare write procedures: ";
			prepareWriteProcedures();
			trace("Prepared write procedures");
			Runtime.getRuntime().gc();
			
			/* (3) Do all translations, putting the results in the 'Tests' folder */
			failureMessage = "Exception when doing translations: ";
			doTranslations();
			if (tracing) timer.report(); // first timings before making EMF instances
			trace("Done all translations");
			 		
			 /* (4) Test that each translation result conforms to the structure recorded in
			 * its mapping set, or record discrepancies */
			failureMessage = "Exception when testing result structures: ";
			testResultStructures();
			trace("Tested result structures");
			
			 /* (5) Make Ecore model instances from all translation sources and targets,
			 * putting them in the 'Tests' folder */
			failureMessage = "Exception when creating Ecore model instances: ";
			makeECoreModelInstances();
			trace("Made Ecore model instances");
			 
			 /* (6) For every translation, test that the result Ecore model instance 
			 * is a subset of the source Ecore instance, or record discrepancies  */
			failureMessage = "Exception when comparing Ecore model instances: ";
			compareSourceAndTargetEcoreInstances();
			trace("Compared source and target instances");

			 /* (7) For every translation, check that the gaps in the result ECore instance 
			 * (with respect to the source Ecore instance) are all expected because of gaps 
			 * in the mappings (eg for a self-translation A=>A, there should be no gaps); 
			 * or record discrepancies */
			failureMessage = "Exception checking gaps in target instances: ";
			checkGapsInTargetECoreInstances();
			trace("Checked gaps in target instances");
					
			 /* (8) Summarise results for each tested translation in the Translation Summary view, 
			 * and show all discrepancies in the test discrepancy view. Save these views in the Tests folder. */ 
			timer.start(Timer.SHOW_VIEW);
			showTranslationSummaryView();
			timer.stop(Timer.SHOW_VIEW);
			trace("Shown Translation Summary View");
			timer.stop(Timer.TRANSLATION_TEST);
			if (tracing) timer.report();
			
		}
		catch (Exception ex)
		{
			showMessage(failureMessage + ex.getMessage());
			ex.printStackTrace();
			showTranslationSummaryView();
			return; 
		}
		
		
	}
	
	private void chooseSourcesForTest() throws MapperException
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

	
	/* (1) Clear out the 'Tests' folder of the appropriate project (the project
	 * which contains the common class model of all the data sources) */
	private void clearTestFolder() throws CoreException
	{
		theProject = chosenSources.get(0).getProject();
		testFolder = theProject.getFolder("Tests");
		if (!testFolder.exists())
			{showMessage("There is no test folder in project '" + theProject.getName() + "'");return;}
		// deletes have to be done in reverse order (length decreases)
		int start = testFolder.members().length -1;
		for (int i = start ; i > -1; i--)
		{
			IResource ir = testFolder.members()[i];
			ir.delete(true, null);
		}
	}
	
	/* (2)Validate all mapping sets involved, and prepare write procedure files for them */
	private void prepareWriteProcedures() throws MapperException, CoreException
	{
		for (Iterator<DataSource> it = chosenSources.iterator(); it.hasNext();)
		{
			DataSource ds = it.next();
			MappedStructure mainMappedStructure = ds.getReader().ms();
			ds.getReader().giveTimer(timer, true);
			timer.start(Timer.COMPILE);

			// make and note a translation summary item for this source
			TranslationSummaryItem tsItem = new TranslationSummaryItem(ds.getCode());
			translationSummaryItems.put(ds.getCode(), tsItem);
			
			// iterate over the main mapping set for the data source, and any others it imports
			for (Enumeration<MappedStructure> en = mainMappedStructure.getAllImportedMappingSets().elements(); en.hasMoreElements();)
			{
				MappedStructure mappedStructure = en.nextElement();
				// (possibly) validate the mapping set and record any diagnostics
				if (validateMappingSet(mappedStructure))
				{
					trace("Validating " + mappedStructure.eResource().getURI().toString());
					validateMappingSet(mappedStructure,tsItem, ds.getCode());
				    trace("Validated");					
				}
							
				// make the procedures
			    trace("Making write procedure for " + mappedStructure.eResource().getURI().toString());
				ProcedureCompiler procWriter = MakeTranslationActionDelegate.makeProcedureFile(mappedStructure);
			    noteCompileIssues(procWriter,tsItem,ds.getCode());
			    
			    trace("Made or found write procedure for " + mappedStructure.eResource().getURI().toString());
			}			
			timer.stop(Timer.COMPILE);
		}
	}
	
	/**
	 * @param mappedStructure a mapping set
	 * @return true if it is to be validated, false if not.
	 * V3 mapping sets, which are auto-generated in specific named folders, are 
	 * not to be validated, because:
	 * (a) being auto-generated, they have no faults; or if they do, the user can do nothing about them
	 * (b) There are lots of them, so Eclipse will run out of memory validating them 
	 */
	private boolean validateMappingSet(MappedStructure mappedStructure)
	{
		boolean validate = validateMappingSets;
		String location = mappedStructure.eResource().getURI().toString();
		StringTokenizer st = new StringTokenizer(location,"/");
		while (st.hasMoreTokens())
		{
			String folder = st.nextToken();
			if (folder.equals("V3DataTypes")) validate = false;
			if (folder.equals("V3RMIMs")) validate = false;			
		}
		return validate;
	}
	
	/** 
	 * validate a mapping set and note any issues for display in the translation issues view
	 * @param mappedStructure the mapping set
	 */
	private void validateMappingSet(MappedStructure mappedStructure, TranslationSummaryItem tsItem, String code)
	{
		String mappingSetName = FileUtil.getFileName(mappedStructure.eResource().getURI().toString());
		// set up for validation
		MapperValidator mv = new MapperValidator();
		BasicDiagnostic allDiagnostics = new BasicDiagnostic();
		Map<Object,Object> context = null;

		// validate all nodes in the mapped structure tree
		for (Iterator<EObject> it = mappedStructure.eAllContents(); it.hasNext();)
		{
			EObject node = it.next();
			mv.publicValidate(node.eClass().getClassifierID(), node, allDiagnostics,context);
		}
		
		// key - issues description, to remove duplicates
		Hashtable<String,ValidationIssue> issues = new Hashtable<String,ValidationIssue>();
		
		// collect the validation issues arising, removing duplicates
		int remove = "com.openMap1.mapper.impl.".length();
		for (Iterator<Diagnostic> it = allDiagnostics.getChildren().iterator();it.hasNext();)
		{
			Diagnostic d = it.next();
			String identifier = d.getData().get(0).getClass().getName().substring(remove);
			ValidationIssue vi = new ValidationIssue(d.getCode(),identifier,d.getMessage(),mappingSetName);
			vi.setCode(code);
			ValidationIssue vprev = issues.get(vi.description());
			if (vprev != null) {vprev.addOccurrence();}
			else issues.put(vi.description(), vi);
		}
		
		// note the issues against the translation summary item
		for (Enumeration<ValidationIssue> en = issues.elements();en.hasMoreElements();)
		{
			ValidationIssue vi = en.nextElement();
			allMismatches.add(vi);
			tsItem.addValidationIssue(vi);
		}
	}
	
    private void noteCompileIssues(ProcedureCompiler procWriter, TranslationSummaryItem tsItem, String code)
    {
    	for (Enumeration<String> en = procWriter.getCompilationIssues().keys();en.hasMoreElements();)
    	{
    		String pathString = en.nextElement();
    		List<CompilationIssue> vci = procWriter.getCompilationIssues().get(pathString);
    		for (Iterator<CompilationIssue> it = vci.iterator(); it.hasNext();)
    		{
    			CompilationIssue ci = it.next();
   			    ci.setCode(code);
    			allMismatches.add(ci);
    			tsItem.addCompilationIssue(ci);
    		}
    	}
    }


	/* (3) Do all translations, putting the results in the 'Tests' folder */
	private void doTranslations() throws MapperException, CoreException
	{		
		/* (a) copy each source file into the test folder, 
		 * with names like 'A.xml' using short codes for Data Sources. 
		 * (the structure of each source file gets checked against its structure definition later)*/
		for (Iterator<DataSource> it = chosenSources.iterator(); it.hasNext();)
		{
			DataSource ds = it.next();
			/* get the extension (with no initial '*') for files before 
			 * any 'in' wrapper transform. This will be '.xml' of '.txt'  */
			IFile newFile = testFolder.getFile(new Path(ds.getCode()+ ds.getExtension()));
			newFile.create(ds.getInstanceFile().getContents(), true, null);
			
			/* if any data source uses a wrapper transform, store the in-transformed version 
			 * of the file in the tests folder (for user investigations; not used otherwise)  */
			if (ds.getReader().ms().hasWrapperClass())
			{
				Element inFileRoot = ds.getReader().ms().getXMLRoot(ds.getInstanceFile().getContents());
				IFile newInFile = testFolder.getFile(new Path(ds.getCode()+ "_in.xml"));
				EclipseFileUtil.writeOutputResource(inFileRoot.getOwnerDocument(), newInFile, true);
			}
		}
		trace("Created source file copies for translation");
		
		/* (b) make a list of all translation chains that will be done   */
		makeTranslationChainList();
		trace("Made translation chain lists");
		
		/* (c) do all the translations; for each one, make a translation 
		 * summary item for the final result */
		for (int t = 0; t < translationChains.size(); t++)
		{
			TranslationChain tc = translationChains.get(t); 
			tc.doTranslationChain();
			trace("Done translation chain " + t);
			
		}
		
	}
	
	private void makeTranslationChainList()
	{
		translationChains = new Vector<TranslationChain>();
		
		// include all A=>B translations and self-translations A=>A
		for (Iterator<DataSource> is = chosenSources.iterator(); is.hasNext();)
		{
			DataSource ds = is.next();
			for (Iterator<DataSource> it = chosenSources.iterator(); it.hasNext();)
			{
				DataSource dt = it.next();
				translationChains.add(new TranslationChain(ds,dt));
			}
		}
		
		// include all A=>B=>A round trips
		for (Iterator<DataSource> is = chosenSources.iterator(); is.hasNext();)
		{
			DataSource ds = is.next();
			for (Iterator<DataSource> it = chosenSources.iterator(); it.hasNext();)
			{
				DataSource dt = it.next();
				if (dt != ds) 
				{
					TranslationChain tc = new TranslationChain(ds,dt);
					tc.addLink(ds);
					translationChains.add(tc);				
				}
			}
		}
		
		// if there are 3 or more active sources, include a full round trip
		if (chosenSources.size() > 2)
		{
			// make first link A=>B
			TranslationChain tc = 
				new TranslationChain(chosenSources.get(0),chosenSources.get(1));
			// add further links to C, D, etc.
			for (int s = 2; s < chosenSources.size();s++)
				tc.addLink(chosenSources.get(s));
			// add final link back to A
			tc.addLink(chosenSources.get(0));
			translationChains.add(tc);				
		}
	}
	
	
	 /* (4) Test that each translation source or result conforms to the structure recorded in
	 * its mapping set, or record the discrepancies */
	private void testResultStructures() throws CoreException,MapperException
	{
		for (int m = 0; m < testFolder.members().length; m++)
		{
			timer.start(Timer.STRUCTURE_TEST);
			IResource res = testFolder.members()[m];
			if ((res instanceof IFile) && 
					((res.getName().endsWith("xml"))|(res.getName().endsWith("txt"))))
			{
					IFile resultFile = (IFile)testFolder.members()[m];
					String name = resultFile.getName();
					
					// find the full code of the source or result, and get the Translation Summary Item
					String fullResultCode = name.substring(0,name.length() - 4);
					// do not look at results inside wrapper transformations - saved only for user inspection
					if (!(fullResultCode.endsWith("_in")))
					{
						TranslationSummaryItem tsi = translationSummaryItems.get(fullResultCode);
						if (tsi == null)
							throw new MapperException("Cannot find translation summary item for code '" + fullResultCode + "'");

						// find the structure code of the result structure; from a name 'ABC.xml', pick out the 'C'
						String resultStructureCode = resultStructureCode(fullResultCode);
						DataSource structureSource = getSourceByCode(resultStructureCode);
						if (structureSource == null)
							throw new MapperException("Cannot find data source for code '" + resultStructureCode + "'");

						checkStructure(resultFile,structureSource,tsi);						
					}
			}
			timer.stop(Timer.STRUCTURE_TEST);
		}
	}
	
	/** find the structure code of the result structure; from a name 'ABC', pick out the 'C' */
	private String resultStructureCode(String fullResultCode)
		{return  fullResultCode.substring(fullResultCode.length()-1, fullResultCode.length());}
	
	/** find the first source code of the result structure; from a name 'ABC', pick out the 'A' */
	private String sourceCode(String fullResultCode)
		{return  fullResultCode.substring(0, 1);}
	
	/**
	 * @param code a code 'A' ,'B' etc allocated to data sources in the data sources view
	 * @return the active data source with that code
	 */
	private DataSource getSourceByCode(String code)
	{
		DataSource ds = null;
		for (int c = 0; c < chosenSources.size(); c++)
			if (chosenSources.get(c).getCode().equals(code))
				ds = chosenSources.get(c);
		return ds;
	}
	
	/**
	 * check that the result of a translation (or an instance input to one)
	 * conforms to the structure definition in the mapped structure;
	 * record discrepancies where it does not - both in the global list
	 * of issues and in the Translation Summary Item for the translation.
	 * 
	 * The check is made on two ways - once by XML schema validation (if there is a schema)
	 * and once against the Mapped Structure tree. The test against the mapped structure
	 * tree is the weaker of the two.
	 * 
	 * @param resultFile the source or result file whose structure is tested
	 * @param structureSource the data source which defines the required structure
	 * @param tsi the translation summary item under which all issues are stored
	 */
	private void checkStructure(IFile resultFile,DataSource structureSource,TranslationSummaryItem tsi) 
	{
		// Get the result file, if necessary doing an 'in' wrapper transformation
		try{
			MappedStructure mapStructure = structureSource.getReader().ms();
			Element root = mapStructure.getXMLRoot(resultFile.getContents());
			
			// do the schema validation (if there is a schema) and record the result
			Vector<SchemaMismatch> schemaMismatches = mapStructure.schemaValidate(root);

			// label the schema mismatches by the file, and save them
			for (Iterator<SchemaMismatch> it = schemaMismatches.iterator();it.hasNext();)
			{
				SchemaMismatch sm = it.next();
				sm.setFileName(resultFile.getName());
				allMismatches.add(sm);
				tsi.addSchemaMismatch(sm);
			}

			// do the (weaker) validation against the mapped structure, and record the result
			Vector<StructureMismatch> structureMismatches = mapStructure.checkInstance(root); 
			
			// label the structure mismatches by the file, and save them
			for (Iterator<StructureMismatch> it = structureMismatches.iterator();it.hasNext();)
			{
				StructureMismatch sm = it.next();
				sm.setFileName(resultFile.getName());
				allMismatches.add(sm);
				tsi.addStructureMismatch(sm);
			}
			
		}
		// catch an exception - assume it arises in applying the wrapper transform
		catch (Exception ex)
		{
			StructureMismatch sm = new StructureMismatch("",StructureMismatch.STRUCTURE_IN_WRAPPER_TRANSFORM, ex.getMessage(),"");
			sm.setFileName(resultFile.getName());
			allMismatches.add(sm);
			tsi.addStructureMismatch(sm);			
		}
	}


	
	 /* (5) Make Ecore model instances from all translation sources and targets,
	 * putting them in the 'Tests' folder */
	private void makeECoreModelInstances() throws MapperException, CoreException
	{
		ecoreInstances = new Hashtable<String,Resource>();
		EMFInstanceFactory instanceFactory = new GenericEMFInstanceFactoryImpl();
		instanceFactory.giveTimer(timer);
		// Iterate over all '.xml' or '.txt' files (sources and results) in the tests folder
		// record all the resources in the folder before you start adding to it
		int len = testFolder.members().length;
		IResource[] xmlResults = new IResource[len];
		for (int m = 0; m < len; m++) xmlResults[m] = testFolder.members()[m];
		// now start adding new files to the tests folder
		for (int m = 0; m < len; m++)
		{
			IResource res = xmlResults[m];
			String name = res.getName();
			if ((res instanceof IFile) && ((name.endsWith("xml"))|(name.endsWith("txt"))))
			{
					IFile resultFile = (IFile)res;
					String nameRoot = name.substring(0,name.length() - 4); // remove '.xml' or '.txt'
					// do not look at results inside a wrapper in-transformation (made only for user inspection)
					if (!(nameRoot.endsWith("_in")))
					{
						TranslationSummaryItem tsi = translationSummaryItems.get(nameRoot);
						if (tsi == null)
							throw new MapperException("Cannot find translation summary item for code '" + nameRoot + "'");

						String resultCode = resultStructureCode(nameRoot);
						DataSource structureSource = getSourceByCode(resultCode);
						if (structureSource == null)
							throw new MapperException("Cannot find data source for code '" + resultCode + "'");
						XOReader reader = structureSource.getReader();
						
						try{
							// make up a URI for the ECore instance, in the tests folder
							URI uri = URI.createURI("/" + theProject.getName() + "/Tests/" + nameRoot + ".model");
							// point the data source reader at the correct instance (maybe after an 'in' wrapper transform)
							Element root = reader.ms().getXMLRoot(resultFile.getContents());
							reader.setRoot(root);
							
							// make and save the Ecore model instance. 'true' means 'create a Container object'
							trace("Making EMF Instance " + nameRoot);
							timer.start(Timer.MAKE_EMF_INSTANCE);
							Resource rs = instanceFactory.createModelInstanceInTranslationTest(reader, uri, true);
							timer.stop(Timer.MAKE_EMF_INSTANCE);
							
							// record any problems (eg exceptions thrown by property conversion code)
							List<RunIssue> issues = instanceFactory.runIssues();
							for (Iterator<RunIssue> it = issues.iterator();it.hasNext();)
							{
								RunIssue ri = it.next();
								ri.setFileName(nameRoot);
								tsi.addRunIssue(ri);
								allMismatches.add(ri);
							}
							ecoreInstances.put(nameRoot, rs);						
							
						}
						// catch an exception - assume it arises in applying the wrapper transform
						catch (Exception ex)
						{
							StructureMismatch sm = new StructureMismatch("",StructureMismatch.STRUCTURE_IN_WRAPPER_TRANSFORM, ex.getMessage(),"");
							sm.setFileName(resultFile.getName());
							allMismatches.add(sm);
							tsi.addStructureMismatch(sm);			
						}
											
					}
			}
		}		
	}
	


	 
	 /* (6) For every translation, test that the result Ecore model instance 
	 * is a subset of the source Ecore instance, or record discrepancies  */
	private void compareSourceAndTargetEcoreInstances() throws CoreException,MapperException
	{
		for (Enumeration<String> en = ecoreInstances.keys();en.hasMoreElements();)
		{
			String name = en.nextElement();
			if (name.length() > 1) // do only results, not sources
			{
				TranslationSummaryItem tsi = translationSummaryItems.get(name);
				if (tsi == null)
					throw new MapperException("Cannot find the translation summary item for code '" + name + "'");

				// the source code is the first character of the result name
				String sourceCode = name.substring(0,1);
				Resource source = ecoreInstances.get(sourceCode);
				Resource result = ecoreInstances.get(name);
				trace("**Match " + sourceCode + " and " + name);
				// compare each result with its source
				timer.start(Timer.COMPARE_EMF_INSTANCE);
				compareEcoreInstances(source, result, name, tsi);						
				timer.stop(Timer.COMPARE_EMF_INSTANCE);
			}
		}
	}
	
	private void compareEcoreInstances(Resource rSource, Resource rResult, String resultName,
			TranslationSummaryItem tsi) 
	throws MapperException
	{
		 if ((rSource != null) && (rResult != null))
		 {
			 EObject sourceRoot = rSource.getContents().get(0);
			 EObject resultRoot = rResult.getContents().get(0);
			 // ensure that arbitrarily assigned text keys will not fail to match
			 EcoreMatcher.equaliseTextKeys(sourceRoot);
			 EcoreMatcher.equaliseTextKeys(resultRoot);
			 String sourceCode = sourceCode(resultName);
			 String resultCode = resultStructureCode(resultName);
			 EcoreMatcher matcher = new EcoreMatcher(sourceRoot, resultRoot, 
					 sourceCode,resultCode,
					 getSourceByCode(sourceCode).getReader(),
					 getSourceByCode(resultCode).getReader());
			 
			 // find all semantic mismatches
			 Hashtable<String, SemanticMismatch> mismatches 
			 	= new Hashtable<String, SemanticMismatch>();
			 int sourceSize = matcher.treeMatch(sourceRoot, sourceRoot, mismatches); // expect no mismatches
			 // int resultSize = matcher.treeMatch(resultRoot, resultRoot, mismatches); // expect no mismatches; just for testing
			 int matches = matcher.treeMatch(sourceRoot, resultRoot, mismatches);
			 tsi.setSourceItemCount(sourceSize);
			 tsi.setResultItemCount(matches);
			 // System.out.println("Semantic scores: " + sourceSize + " " + resultSize + " " + matches);
			 

			// label the semantic mismatches by the file, and save them
			for (Enumeration<SemanticMismatch> en = mismatches.elements();en.hasMoreElements();)
			{
				SemanticMismatch sm = en.nextElement();
				sm.setFileName(resultName);
				allMismatches.add(sm);
				tsi.addSemanticMismatch(sm);
			}
		 }
		 else throw new MapperException("Cannot find Ecore instance resources to check " 
					+ resultName);		
	}
	

	 /* (7) For every translation, check that the gaps in the result ECore instance 
	 * (with respect to the source Ecore instance) are all expected because of gaps 
	 * in the mappings (eg for a self-translation A=>A, there should be no gaps); 
	 * or record discrepancies */
	private void checkGapsInTargetECoreInstances(){}
	
	
	 /* (8) Summarise results for each tested translation in the Translation Summary view, 
	 * and show all discrepancies in the test issue view. Save these views in the Tests folder. */ 
	private void showTranslationSummaryView()
	{
		Vector<TranslationSummaryItem> tSumItems = new Vector<TranslationSummaryItem>();
		for (Enumeration<TranslationSummaryItem> en = translationSummaryItems.elements();en.hasMoreElements();)
			{tSumItems.add(en.nextElement());}
		WorkBenchUtil.getTranslationSummaryView(true).showNewResult(tSumItems);
		WorkBenchUtil.getTranslationIssueView(true).showNewResult(allMismatches);
		WorkBenchUtil.page().activate(WorkBenchUtil.getTranslationSummaryView(true));
	}
	
	
	protected void showMessage(String title, String message) {
		MessageDialog.openInformation(
			shell,
			title,
			message);
	}

	/**
	 * default if you can't be bothered to make up a message title
	 * @param message
	 */
	protected void showMessage(String message) 
		{showMessage("Error",message);}
	
	//----------------------------------------------------------------------------------------
	//                          inner class for chains of Translations
    //----------------------------------------------------------------------------------------
	
	class TranslationChain
	{
		// Vector of data sources
		Vector<DataSource> dataSourceChain = new Vector<DataSource>();
		
		TranslationChain(DataSource start, DataSource target)
		{
			dataSourceChain.add(start);
			dataSourceChain.add(target);
		}
		
		void addLink(DataSource newEnd)
		{
			dataSourceChain.add(newEnd);
		}
		
		/** appears not to be used */
		Vector<String> resultsNeeded()
		{
			Vector<String> needed = new Vector<String>();
			// first translation in the chain
			String result = dataSourceChain.get(0).getCode() + dataSourceChain.get(1).getCode();
			needed.add(result);
			// subsequent translations, each using the result of the previous translation
			for (int i = 2; i < dataSourceChain.size(); i++)
			{
				String link = dataSourceChain.get(i).getCode();
				result = result + link;
				needed.add(result);
			}
			return needed;
		}
		
		/**
		 * For a translation chain ABCD, do all the translations
		 * needed to make all intermediate results and the final result.
		 * first make AB from A, 
		 * then make ABC from AB
		 * then make ABCD from ABC;
		 * but do not re-make any if they have been made already
		 */
		void doTranslationChain() throws MapperException, CoreException
		{
			DataSource previousSource = dataSourceChain.get(0);
			String previousResultFileName = previousSource.getCode();
			for (int t = 1; t < dataSourceChain.size();t++)
			{
				DataSource nextSource = dataSourceChain.get(t);
				String nextResultFileName = previousResultFileName + nextSource.getCode();
				IFile resultFile = testFolder.getFile(nextResultFileName + nextSource.getExtension());
				// if this result file has not been made already, make it now
				if (!resultFile.exists())
				{
					// make and record a Translation Summary Item
					TranslationSummaryItem tsi = new TranslationSummaryItem(nextResultFileName);
					translationSummaryItems.put(nextResultFileName, tsi);

					// get the instance or result file which is the source for this translation
					String sourceName = previousResultFileName + previousSource.getExtension();
					IFile sourceFile = testFolder.getFile(sourceName);

					doOneTranslation(previousSource,sourceFile,nextSource,resultFile,tsi);
				} // end of if (!resultFile.exists())
				
				//make it move on to the next translation; 
				previousResultFileName = nextResultFileName;
				previousSource = nextSource;
			}
		}
	}
	
	/**
	 * 
	 * @param previousSource the data source which defines the source language for this translation 
	 * @param sourceFile the instance which is the source for this translation
	 * (may be the result of a previous translatioon to that language)
	 * @param nextSource the data source which defines the target language for this translation 
	 * @param resultFile the IFile which the result is to be put into
	 * @param tsi the translation summary item to collect issues in this translation
	 */
	private void doOneTranslation(DataSource previousSource,IFile sourceFile,
			DataSource nextSource, IFile resultFile, 
			TranslationSummaryItem tsi)
	{
		try
		{
			if (sourceFile.exists())
			{
				timer.start(Timer.TRANSLATE);
				timer.start(Timer.MAKE_OBJECTGETTER);
				// make the XML reader of the previous source point to the correct result file
				Element sourceRoot  = previousSource.getReader().ms().getXMLRoot(sourceFile.getContents());
				XOReader reader = previousSource.getReader();
				reader.setRoot(sourceRoot);
				objectGetter oGet = null;
				if (reader instanceof MDLXOReader)
					oGet = new XMLObjectGetter((MDLXOReader)reader);
				else if (reader instanceof objectGetter)
					oGet = (objectGetter)reader;
				else throw new MapperException("Cannot make objectGetter for source " + previousSource.mappingSetName());
				timer.stop(Timer.MAKE_OBJECTGETTER);
				
				// do the translation
				trace("Starting translation from " + sourceFile.getName() + " to make " + resultFile.getName());
				MappedStructure nextMapStructure = nextSource.getReader().ms();
				timer.start(Timer.MAKE_TRANSLATOR);
				XMLWriter translator = nextMapStructure.getXMLWriter(oGet, null, new SystemMessageChannel(), doRunTracing);
				translator.giveTimer(timer, false);
				timer.stop(Timer.MAKE_TRANSLATOR);
				trace("Translator made for " + nextMapStructure.getMappingSetName());
				Element resultRoot = translator.makeXMLDOM();
				trace("Completed translation from " + sourceFile.getName() + " to make " + resultFile.getName());
				if (resultRoot == null)
					showMessage("Null root element in result of translation " + resultFile.getName());
				else 
				{
					trace("Result root before wrapper: " + XMLUtil.getLocalName(resultRoot));
					// apply output wrapper transform if necessary; sometimes it depends on the input reader, to store XML fragments
					Object output = nextMapStructure.makeOutputObject(resultRoot,reader);
					// write the output file in the appropriate form (XML or text)
					EclipseFileUtil.writeOutputObject(output,resultFile,nextMapStructure.getInstanceFileType());
					
					/* if the result data source has a wrapper transform, store the result before 
					 * the out-transform is done, for user examination (not used otherwise)*/
					if (nextMapStructure.hasWrapperClass())
					{
						StringTokenizer st = new StringTokenizer(resultFile.getName(),".");
						String resultNameRoot = st.nextToken();
						IFile inResultFile = testFolder.getFile(new Path(resultNameRoot + "_in.xml"));
						EclipseFileUtil.writeOutputResource(resultRoot.getOwnerDocument(), inResultFile, true);						
					}
				}

				Hashtable<String,Hashtable<String,RunIssue>> runIssues = translator.allRunIssues();
				for (Enumeration<String> en = runIssues.keys();en.hasMoreElements();)
				{
					String pathString = en.nextElement();
					Hashtable<String,RunIssue>  issues = runIssues.get(pathString);
					for (Enumeration<RunIssue> ep = issues.elements();ep.hasMoreElements();)
					{
						RunIssue ri = ep.nextElement();
					    ri.setFileName(tsi.resultCode());
						allMismatches.add(ri);
						tsi.addRunIssue(ri);
					}
				}			
				timer.stop(Timer.TRANSLATE);
			}
			else throw new MapperException("File " + sourceFile.getName() + " unexpectedly not found.");		
			
		}
		/* any Exception when running the translation is recorded as a run issue, 
		 * so it does not throw the whole translation test. */
		catch (Exception ex) 
		{
			ex.printStackTrace();
			try{
				Xpth dummy = new Xpth(new NamespaceSet(),"");
				String message = ex.getMessage();
				if (message == null) message = "Null pointer exception";
				RunIssue ri = new RunIssue(RunIssue.RUN_FATAL_ERROR,"","",message,dummy,0);				
				tsi.addRunIssue(ri);
			}
			catch (XpthException ey) {}
			timer.stop(Timer.TRANSLATE);
		}
	}
	
	private void trace(String s) 
	{
		if (tracing) 
		{
			System.out.println(s);
			//GenUtil.writeMemory();
		}
	}


}
