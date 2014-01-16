package com.openMap1.mapper.views;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.presentation.DatabaseConnectWizard;
import com.openMap1.mapper.query.DataSource;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.StructureMismatch;
import com.openMap1.mapper.core.TranslationIssue;
import com.openMap1.mapper.actions.TranslationTestAction;
import com.openMap1.mapper.actions.WriteCrossMappingsAction;

import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.SystemMessageChannel;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.StructureType;

/**
 * shows a set of data sources. Each data source is a row of the view, and consists of
 * a mapping set, an instance mapped, and the class model it is mapped to.
 * All data sources must have the same class model. Any
 * data source may be active or not.
 * 
 * @author robert
 *
 */
public class DataSourceView extends ViewPart implements SaveableView{

	private CheckboxTableViewer viewer;
	private Table tab;

	private Action addDataSourceAction;
	private Action saveDataSourceSetAction;
	private Action restoreDataSourceSetAction;
	private Action removeAllDataSourcesAction;
	private Action removeSelectedDataSourcesAction;
	private Action translationTestAction;
	private Action crossMappingsAction;
	private Action defineFHIRSearchAction;
	
	// this is the model, which should be kept synchronised with the viewer
	private Vector<DataSource> dataSources = new Vector<DataSource>();
	public Vector<DataSource> dataSources() {return dataSources;}
	
	private String instanceLocation; 
	
	private Vector<String> columnHeaders;
	
    
    private LabelledEClass copiedLabelledEClass = null;
    /** set the node that has been copied, for later paste of simplification annotations */
    public void setCopiedLabelledEClass(LabelledEClass copied) {copiedLabelledEClass= copied;}
    /** get the node that has been copied, for later paste of simplification annotations */
    public LabelledEClass getCopiedLabelledEClass() {return copiedLabelledEClass;}
    
    private String annotationURI;
    /** set the annotation uri */
    public void setAnnotationURI(String annotationURI) {this.annotationURI = annotationURI;}
    /** get the annotation uri */
    public String getAnnotationURI() {return annotationURI;}


	//-------------------------------------------------------------------------------------
	//                          Refresh all data sources
	//-------------------------------------------------------------------------------------
	
	/**
	 * Ensure that all active data sources are connected to an up-to-date mapping set and XML
	 * instance (e.g before running a query, or before a translation test) 
	 * in case either the mapping set or the instance has been edited since the data source was made
	 * 
	 * If any data source cannot be refreshed (eg because the mapping set has been
	 * deleted or its name changed) write a warning and make that source inactive
	 */
	public void refreshAllActiveSources(boolean allowNewPasswords)
	{
		// refresh the 'active' status of all data sources, from the checkboxes in the view table
		getActiveDataSources();

		// now address only data sources which are active
		for (Iterator<DataSource> it = dataSources().iterator();it.hasNext();)
		{
			DataSource ds = it.next();
			if (ds.isActive())
			{
				boolean connected = false;
				String reason = "";				
				try 
				{
					MappedStructure mapStructure = ds.getFreshMappedStructure();
					
					// relational data sources; if not connected, possibly ask the user for a user name and password
					if (mapStructure.getStructureType() == StructureType.RDBMS)
					{
						if ((!ds.isConnected()) && (allowNewPasswords)) getNewUserNameAndPassword(ds); // may throw an Exception
						connected = ds.isConnected();
					}
					
					// non-relational sources; refresh, in case the XML instance has changed
					else if (mapStructure.getStructureType() != StructureType.RDBMS)
					{ 
						ds.refresh();
						connected = true;
					}
				}
				// something unexpected; record the reason
				catch (MapperException ex)
				{
					reason = "(" + ex.getMessage() + ")";
					connected = false;
				}
				
				// if you could not open the data source file or database for any reason, make it inactive
				if (!connected)
				{
					ds.setIsActive(false); // make the data source know it is inactive
					viewer.setChecked(ds, false); // make the view and the checkbox know it is inactive
					String warning = "Data source " + ds.getCode()
						+ " could not be refreshed" + reason + ", and has been made inactive.";
					MessageDialog.openError(getSite().getShell(), "Data sources changed", warning);						
				}
			}
		}
	}
	
	/**
	 * get a new user name and password for a relational data source, and attempt to connect it
	 * @param ds
	 * @return
	 * @throws MapperException e.g if the user name and password are not accepted
	 */
	private boolean getNewUserNameAndPassword(DataSource ds) throws MapperException
	{
		boolean dataSourceIsConnected = false;
		MappedStructure mapStructure = ds.getFreshMappedStructure();

		// allow the user to input a new user name and password for the database
	    DatabaseConnectWizard wizard = new DatabaseConnectWizard();
	    wizard.init(PlatformUI.getWorkbench(),null); // no selection has been right-clicked
	    WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),wizard);
	    // don't allow the user to input a new jdbc connect string; that is defined in the mapping set
	    wizard.fixJDBCString(mapStructure.getStructureURL());
	    dialog.open();

	    if (wizard.finishedSuccessfully()) // user did not cancel
	    {
			String userName = wizard.getUserName();
			String password = wizard.getPassword();
			// mapStructure.connectToRDB(userName, password); // seems redundant given next line
			ds.connect(userName, password); // may throw an Exception
			dataSourceIsConnected = ds.isConnected();
	    }
		
		return dataSourceIsConnected;
	}

	//-------------------------------------------------------------------------------------
	//                          Initialisation
	//-------------------------------------------------------------------------------------

	/**
	 * This  creates the viewer and initializes it.
	 */
	public void createPartControl(Composite parent) {
		
		// create the viewer and its table
		createViewer(parent);
				
		// make the actions that will be items on the menu of this view
		makeActions();

		// attach the menu to this view
		contributeToActionBars();
		
	}
	
	public void createViewer(Composite parent)
	{
		tab = new Table(parent, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer = new CheckboxTableViewer(tab);
		viewer.setLabelProvider(new DataSourceTableLabelProvider());
		tab.setHeaderVisible(true);
		tab.setLinesVisible(true);
		makeTableColumns(tab);		
		
		/* add a listener for selection changes, which tells the data sources 
		 * which ones are selected. This is not currently used, because selecting
		 * the drop-down menu immediately de-selects all the rows; so you 
		 * cannot make a drop-down menu item (or even its enablement) depend on the selected rows. 
		 * A brilliant catch-22.
		 */ 
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// de-select all data sources
				for (Iterator<DataSource> it = dataSources.iterator();it.hasNext();)
					it.next().setIsSelected(false);
				// select only those in the selection
				for (Iterator<?> is = ((IStructuredSelection)event.getSelection()).iterator();is.hasNext();)
					((DataSource)is.next()).setIsSelected(true);
			}
		});

		//  populate the viewer with any existing Data sources, remembered by init(..)from the last session
		for (Iterator <DataSource> it = dataSources.iterator();it.hasNext();)
		{
			DataSource qs = it.next();
			viewer.add(qs);
			viewer.setChecked(qs, qs.isActive());
		}
	}
	
	private void makeTableColumns(Table tab)
	{
		columnHeaders = new Vector<String>();
		String[] columnNames = new String[]{"Code","Type","Source","Mappings","Class Model"};
		int[] columnWidths = new int[] {40,60,200,200,200};
		int[] columnAlignments = new int[] {SWT.LEFT,SWT.LEFT,SWT.LEFT,SWT.LEFT,SWT.LEFT};
		for (int i = 0; i < columnNames.length;i++)
		{
			TableColumn col = new TableColumn(tab,columnAlignments[i]);
			col.setText(columnNames[i]);
			col.setWidth(columnWidths[i]);
			columnHeaders.add(columnNames[i]);
		}
	}
	
	class DataSourceTableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object element, int index)
		{
			DataSource qs = (DataSource)element;
			switch (index)
			{
				case 0: return qs.getCode();
				case 1: return qs.sourceType();
				case 2: return stripPlatform(qs.instanceURIString());
				case 3: return stripPlatform(qs.mappingSetURIString());
				case 4: return stripPlatform(qs.classModelURIString());
				default: return ("unknown " + index);
			}
		}
		
		// strip the repetitive prefix from platform URIs
		private String stripPlatform(String uri)
		{
			String prefix = "platform:/resource";
			String stripped = uri;
			if (stripped.startsWith(prefix)) stripped = stripped.substring(prefix.length());
			return stripped;
		}
		
		public Image getColumnImage(Object element, int index) {return null;}
		
	}

	//-------------------------------------------------------------------------------------
	//                                     Menu
	//-------------------------------------------------------------------------------------
	
	private void makeActions()
	{
		addDataSourceAction = new Action() {
			public void run() {
				doAddDataSource();
			}
		};
		addDataSourceAction.setText("Add a Data Source");
		addDataSourceAction.setToolTipText("Add a set of mappings and an XML Instance to be queried");
		addDataSourceAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		removeSelectedDataSourcesAction = new Action() {
			public void run() {
				doRemoveInactiveDataSources();
			}
		};
		removeSelectedDataSourcesAction.setText("Remove inactive data sources");
		removeSelectedDataSourcesAction.setToolTipText("Remove inactive (unchecked) Data Sources");
		removeSelectedDataSourcesAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		// removeSelectedDataSourcesAction.setEnabled(false);

		removeAllDataSourcesAction = new Action() {
			public void run() {
				doClearAllDataSources();
			}
		};
		removeAllDataSourcesAction.setText("Remove all Data Sources");
		removeAllDataSourcesAction.setToolTipText("Remove all Data Sources");
		removeAllDataSourcesAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		translationTestAction = new TranslationTestAction(getSite().getShell(),this);
		

		saveDataSourceSetAction = new Action() {
			public void run() {
				doSaveDataSourceSet();
			}
		};
		saveDataSourceSetAction.setText("Save Data Source Set to file");
		saveDataSourceSetAction.setToolTipText("Create an XML file from which this Data source set can be re-created");
		saveDataSourceSetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		restoreDataSourceSetAction = new Action() {
			public void run() {
				loadSavedDataSourceSet();
			}
		};
		restoreDataSourceSetAction.setText("Restore Data Source Set from file");
		restoreDataSourceSetAction.setToolTipText("Re-create a Data source set from an XML file");
		restoreDataSourceSetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		crossMappingsAction = new WriteCrossMappingsAction(this);

		defineFHIRSearchAction = new Action() {
			public void run() {
				doDefineFHIRSearch();
			}
		};
		defineFHIRSearchAction.setText("Define FHIR Search");
		defineFHIRSearchAction.setToolTipText("Define a FHIR search for use on all active networked FHIR data sources");
		defineFHIRSearchAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));



	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(addDataSourceAction);
		manager.add(removeSelectedDataSourcesAction);
		manager.add(removeAllDataSourcesAction);
		manager.add(saveDataSourceSetAction);
		manager.add(translationTestAction);
		manager.add(restoreDataSourceSetAction);
		manager.add(crossMappingsAction);
		manager.add(defineFHIRSearchAction);
	}

	//------------------------------------------------------------------------------------------
	//                        saving the state of the view
	//------------------------------------------------------------------------------------------
	
	/** @return the label provider for the view */
	public ITableLabelProvider labelProvider() {return new DataSourceTableLabelProvider();}
	
	/** @return the TableViewer for the view */
	public TableViewer tableViewer() {return viewer;}
	
	/** @return headers of columns in the view  */
	public Vector<String> columnHeaders() {return columnHeaders;}
	
	/** @return the menu item to save the contents of the view */
	public Action saveViewContentsAction() {return saveDataSourceSetAction;}
	
	/**
	 * The ViewSaver passes back a header Element to the view, which can be filled 
	 * with view-specific information
	 * @param doc the Document - needed to create new Elements
	 * @param header the Header element to be filled
	 */
	public void fillHeaderElement(Document doc, Element header)
	{
		// nothing yet in the header
	}
	/**
	 * create an XML file which can be used to re-create this Data source set
	 */
	private void doSaveDataSourceSet()
	{
		ViewSaver vs = new ViewSaver(this,"Save data source set",
				"Choose a new file in which to save the data source set");
		vs.saveResults();
	}
	

	//------------------------------------------------------------------------------------------
	//                        Adding a new Data source
	//------------------------------------------------------------------------------------------
	
	/**
	 * Allow the user to select a mapping set and (if it is a mapping to an XML structure)
	 * an instance document to be queried.
	 * Then create the DataSource object and add it to the table of Data Sources.
	 */
	private boolean doAddDataSource()
	{
		// reset only for relational data sources
		String userName = "";
		String password = "";
		
		String[] extensions = {"*.mapper"};
		String mappingSetPath = FileUtil.getFilePathFromUser(this, extensions, "Select mapping set", false);
		if (!("".equals(mappingSetPath))) try
		{
			// get the mapping set
			URI mappingSetURI = FileUtil.URIFromPath(mappingSetPath);
			MappedStructure mapStructure = FileUtil.getMappingSet(mappingSetURI);	
			if (mapStructure != null)
			{
				messageChannel mc = new SystemMessageChannel();
				XOReader xor = null;
				boolean carryOn = true;
				Element XMLRoot = null;
				
				// check that this mapping set uses the same class model as others in the Data source set
				String cmFileName = mapStructure.getClassModelFileName();
				if ((classModelFileName() != null) && (!(classModelFileName().equals(cmFileName))))
				{
					String message = "Mappings must use the same class model '"
					+ classModelFileName() + "' as other Data sources, not '" + cmFileName + "'";
					MessageDialog.openError(getSite().getShell(), "Mismatch of class models", message);
					return false;
				}
				
				/*  If the mappings are to an XML schema, ask the user for the location of an instance
				 * and attempt to open it. */
				if ((mapStructure.getStructureType() == StructureType.XSD)|
						(mapStructure.getStructureType() == StructureType.V2))
				{
					XMLRoot  = getXMLInstance(mapStructure);
					if (XMLRoot != null)
					{
						EPackage classModel = mapStructure.getClassModelRoot();
						xor = mapStructure.getXOReader(XMLRoot, classModel, mc);
						
						// check that the XML instance conforms to the mapped structure
						carryOn = true;
						Vector<StructureMismatch> mismatches = mapStructure.checkInstance(XMLRoot);
						// show any problems in the Translation Issue View, and check the user wants to carry on
						if (mismatches.size() > 0)
						{
							TranslationIssueView tiv = WorkBenchUtil.getTranslationIssueView(false);
							if (tiv != null)
							{
								Vector<TranslationIssue> mm = new Vector<TranslationIssue>();
								for (Iterator<StructureMismatch> it = mismatches.iterator();it.hasNext();)
									mm.add(it.next());
								tiv.showNewResult(mm);
								tiv.setFocus();
							}
							
							String message = "There are " + mismatches.size()
							+ " mismatches between the XML instance and the mapped structure;"
							+ " (see Translation Issue View). Do you want to continue?";
							carryOn = MessageDialog.openConfirm(getSite().getShell(), "XML Structure Mismatch", message);
						}				
					}					
				}
				
				/* If the mappings are mappings to a relational database, check 
				 * the database can be opened; and leave XMLRoot null, as it is created for each query */
				else if (mapStructure.getStructureType() == StructureType.RDBMS)
				{
					carryOn = false;
					// see if the database will connect with an empty user name and password
					try {mapStructure.connectToRDB(userName, password);carryOn = true;}
					// if not, let the user enter a name and password (but not alter the jdbc connect string)
					catch (MapperException ex)
					{
					    DatabaseConnectWizard wizard = new DatabaseConnectWizard();
					    wizard.init(PlatformUI.getWorkbench(),null); // no selection has been right-clicked
					    WizardDialog dialog = new WizardDialog
					         (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),wizard);
					    wizard.fixJDBCString(mapStructure.getStructureURL());
					    dialog.open();
						userName = wizard.getUserName();
						password = wizard.getPassword();
						try {mapStructure.connectToRDB(userName, password);carryOn = true;}
						catch (Exception e) {}
					}
					if (carryOn)
					{
						xor = mapStructure.getXOReader(XMLRoot, null, mc); // reader opened with a null XMLRoot
						instanceLocation = mapStructure.getStructureURL(); // use the jdbc connect string as a location						
					}
				}

				// if an XOReader has been successfully created...
				if ((xor != null) && (carryOn))
				{
					String sourceType = mapStructure.getStructureType().getLiteral();
					DataSource qs = new DataSource(xor,mappingSetURI,XMLRoot,sourceType,instanceLocation);
					// qs.connect(userName,password);
					dataSources.add(qs);
					setShortNames();
					viewer.add(qs);
					viewer.setChecked(qs, true);
					return true;
				}				
			}
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			if (ex.getMessage() == null) GenUtil.surprise(ex,"DataSourceView.doAddDataSource");
			String message = "Null pointer exception; see console trace for location";
			try {if (ex.getMessage() != null) message = ex.getMessage();} catch (Exception e) {}
			MessageDialog.openError(getSite().getShell(), "Exception opening Data source", message);
		}
		return false;
	}
	
	/**
	 * 
	 * @return the root of an XML Instance, at a location specified by the user; also sets
	 * its location instanceLocation as a side-effect
	 * @throws MapperException if the XML instance cannot be opened
	 */
	private Element getXMLInstance(MappedStructure ms) throws MapperException
	{
		Element XMLRoot = null;
		
		// get the XML instance, asking the user for its location
		String[] exts = ms.getExtensions();
		String instancePath = FileUtil.getFilePathFromUser(this, exts, "Select instance for data source", false);
		if (!("".equals(instancePath)))
		{
			// this will apply any wrapper 'in' transformations to the input if needed
			XMLRoot = ms.getXMLRoot(instancePath);
			// convert from absolute path to platform-relative path
			URI XMLURI = FileUtil.URIFromPath(instancePath); 
			instanceLocation = XMLURI.toString();
		}
		
		/* for FHIR data sources, if the user does not define an XML instance, let him 
		 * provide the URL of a FHIR server */
		else if ((ms.getMappingParameters() != null) 
				&& ("com.openMap1.mapper.fhir.FHIRMapper".equals(ms.getMappingParameters().getMappingClass())))
		{
			String fhirServerLocation = WorkBenchUtil.askInput("FHIR server URL","Please provide a URL for a FHIR server");
			if (fhirServerLocation != null)
			{
				if (fhirServerLocation.startsWith("http"))
				{
					if (!fhirServerLocation.endsWith("/")) fhirServerLocation = fhirServerLocation + "/";
					instanceLocation = fhirServerLocation;
					// provide some arbitrary XML root for the reader; it will be changed later
					Document doc = XMLUtil.makeOutDoc();
					XMLRoot = XMLUtil.NSElement(doc, "a", "feed", "http://www.w3.org/2005/Atom");
					doc.appendChild(XMLRoot);
				}
				else WorkBenchUtil.showMessage("Error", "FHIR server URL should start with 'http'");
			}
		}
		
		return XMLRoot;
	}
	
	/**
	 * remove all Data sources currently showing in the view
	 */
	public void doClearAllDataSources()
	{
		for (Iterator<DataSource> it = dataSources.iterator();it.hasNext();) 
			viewer.remove(it.next());
		dataSources = new Vector<DataSource>();
	}

	/**
	 * remove all Data sources which are not currently active
	 */
	public void doRemoveInactiveDataSources()
	{
		for (Iterator<DataSource> it = dataSources.iterator();it.hasNext();) 
		{
			DataSource ds = it.next();
			/* note that the active flag in a data source is not immediately set when the box is checked,
			 * so we need to look at the viewer to update the active flag on each data source */
			ds.setIsActive(viewer.getChecked(ds));
			viewer.remove(ds);
		}

		Vector<DataSource> newDataSources = new Vector<DataSource>();
		for (int i = 0; i < dataSources.size(); i++)
			if (dataSources.get(i).isActive()) newDataSources.add(dataSources.get(i));
		dataSources = newDataSources;
		setShortNames(); // need to set short names correctly before adding the data sources to the viewer

		for (int i = 0; i < dataSources.size(); i++)
		{
			DataSource ds = dataSources.get(i);
			viewer.add(ds);
			viewer.setChecked(ds, true);
		}
	}
	
	/**
	 * @return the name of the single class model file that is used for all Data sources,
	 * or null if there are no Data sources
	 */
	public String classModelFileName()
	{
		if (dataSources().size() == 0) return null;
		return dataSources().get(0).classModelName();
	}
	
	public Vector<DataSource> getActiveDataSources()
	{
		Vector<DataSource> aqs = new Vector<DataSource>();
		for (Iterator<DataSource> it = dataSources().iterator();it.hasNext();)
		{
			DataSource qs = it.next();
			qs.setIsActive(viewer.getChecked(qs));
			if (qs.isActive()) aqs.add(qs);
		}
		return aqs;
	}
	
	/**
	 * @param code
	 * @return the data source with a given code 'A', 'B' etc.
	 */
	public DataSource getDataSourceWithCode(String code)
	{
		DataSource ds = null;
		for (Iterator<DataSource> it = dataSources().iterator();it.hasNext();)
		{
			DataSource qs = it.next();
			if (qs.getCode().equals(code)) ds = qs;
		}
		return ds;
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	

	//------------------------------------------------------------------------------------------
	//                     Defining a FHIR search for all active FHIR data sources
	//------------------------------------------------------------------------------------------
	

	
	private void doDefineFHIRSearch()
	{
		boolean someFHIRSources = false;
		String currentFHIRSearch ="";
		getActiveDataSources();
		for (Iterator<DataSource> it = dataSources.iterator();it.hasNext();)
		{
			DataSource ds = it.next();
			if ((ds.isActive()) && (ds.isNetworkedFHIRSource())) 
			{
				someFHIRSources = true;
				if (ds.fhirSearch() != null) currentFHIRSearch = ds.fhirSearch();
			}
		}
		
		if (someFHIRSources)
		{
			String search = WorkBenchUtil.askInput("FHIR search", "Previous search: '" + currentFHIRSearch + "'");
			if (search != null)
			{
				for (Iterator<DataSource> it = dataSources.iterator();it.hasNext();)
				{
					DataSource ds = it.next();
					if ((ds.isActive()) && (ds.isNetworkedFHIRSource()))
					{
						ds.setFhirSearch(search);
					}
				}
			}
		}
		else WorkBenchUtil.showMessage("Error", "There are no active networked FHIR data sources");
	}

	//------------------------------------------------------------------------------------------
	//              support for making menu items in the Query editor
	//------------------------------------------------------------------------------------------
	
	/**
	 * return the Package of the class model of the Data sources in this view; or null
	 */
	public EPackage getClassModelPackage()
	{
		if (dataSources().size() > 0) try
		{
			return dataSources.get(0).getReader().ms().getClassModelRoot();
		}
		catch (MapperException ex) {}
		return null;
	}
	
	/**
	 * return the URI string of the class model of the Data sources in this view; or null
	 */
	public String getClassModelURIString()
	{
		if (dataSources().size() > 0) 
			return dataSources.get(0).classModelURIString();
		else return null;		
	}
	
	/**
	 * @param ec an EClass object (selected in the class model view)
	 * @return true if that class is in the class model 
	 * of the Data sources in this Data source set
	 */
	public boolean isInDataSourceClassModel(EClass ec)
	{
		if (getClassModelPackage() != null)
		{
			EPackage thePackage = ec.getEPackage();
			return (ModelUtil.getEClass(thePackage, ec.getName()) != null);
		}
		else return false;
	}
	
	
	/**
	 * 
	 * @param ec a class in the Data sources class model
	 * @return true if there is some mapping to the class in the 
	 * mappings of every active Data source
	 */
	public boolean classMappedInAllActiveDataSources(LabelledEClass lec)
	{
		boolean mapped = false;
		if ((isInDataSourceClassModel(lec.eClass())) && (getActiveDataSources().size() > 0))
		{
			mapped = true;
			for (Iterator<DataSource> it = getActiveDataSources().iterator();it.hasNext();) try
			{
				DataSource ds = it.next();
				/* For every active data source, there must be some XOReader (imported or 
				 * otherwise) that locally represents the class */
				mapped = mapped && (lec.getLocalReader(ds) != null);
			}
			catch (MapperException  ex) {mapped = false;}
		}
		return mapped;
	}
	
	
	/**
	 * 
	 * @param ec a class in the Data sources class model
	 * @param propName name of a property of that class
	 * @return true if there is some mapping to the property in the 
	 * mappings of every active Data source
	 */
	public boolean propertyMappedInAllActiveDataSources(LabelledEClass lec, String propName)
	{
		boolean mapped = false;
		if (isInDataSourceClassModel(lec.eClass()))
		{
			String className = lec.getQualifiedClassName();
			mapped = true;
			for (Iterator<DataSource> it = getActiveDataSources().iterator();it.hasNext();) try
			{
				DataSource qs = it.next();
				XOReader reader = lec.getLocalReader(qs);
				mapped = mapped && (reader != null) && (reader.representsPropertyLocally(className, propName));
			}
			catch (MapperException  ex) {mapped = false;}
		}
		return mapped;
	}
	
	/**
	 * 
	 * @param ec a class in the Data sources class model
	 * @param roleName role name of an association from that class
	 * @return true if there is some mapping to the association in the 
	 * mappings of every active Data source
	 */
	public boolean associationMappedInAllActiveDataSources(LabelledEClass lec, String roleName, EClass target)
	{
		boolean mapped = false;
		if (isInDataSourceClassModel(lec.eClass()))
		{
			mapped = true;
			String className = lec.getQualifiedClassName();
			String targetClassName = ModelUtil.getQualifiedClassName(target);
			for (Iterator<DataSource> it = getActiveDataSources().iterator();it.hasNext();) try
			{
				DataSource qs = it.next();
				XOReader reader = lec.getLocalReader(qs);
				mapped = mapped && (reader != null) && (reader.representsAssociationRoleLocally
						  (className, roleName, targetClassName));
			}
			catch (MapperException  ex) {mapped = false;}
		}
		return mapped;
	}

	//------------------------------------------------------------------------------------------
	//                        short codes for Data sources
	//------------------------------------------------------------------------------------------
	
	public void setShortNames()
	{
		for (int i = 0; i < dataSources.size(); i++)
			dataSources.get(i).setCode(shortName(i));
	}
	
	private String shortName(int i)
	{
		String[] names = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R"};
		if (i < 18) return names[i];
		return names[0];
	}

	//------------------------------------------------------------------------------------------
	//              restoring the state of the view from an XML file
	//------------------------------------------------------------------------------------------

	private void loadSavedDataSourceSet()
	{
		String[] extensions = {"*.xml"};
		String viewSavePath = FileUtil.getFilePathFromUser(this, extensions, 
				"Choose a saved Data source view", false);
		if (!("".equals(viewSavePath))) try
		{
			Element root = XMLUtil.readXMLFile(viewSavePath);
			restoreViewFromDOM(root);
		}
		catch (MapperException ex) 
			{MessageDialog.openError(getSite().getShell(), 
					"Exception Loading Data Sources", ex.getMessage());}
	}

	
	/**
	 * restore a Data source view from the root element of 
	 * a saved Data source view file
	 * @param viewContents root of the saved Data source view
	 * @throws MapperException if anything goes wrong - for instance if it
	 * is the wrong type of XML file
	 */
	public void restoreViewFromDOM(Element viewContents) throws MapperException
	{
		String view = viewContents.getAttribute("view");
		if (!"Data Sources".equals(view)) 
			throw new MapperException("XML File is not a saved Data source view");

		String prefix = "platform:/resource";
		doClearAllDataSources();
		int row = 0;

		Element tableContent = XMLUtil.firstNamedChild(viewContents, "TableContent");
		if (tableContent != null)
		for (Iterator<Element> ir = XMLUtil.namedChildElements(tableContent, "row").iterator();ir.hasNext();)
		{
			try
			{
				Element rowEl = ir.next();
				Element typeEl = XMLUtil.firstNamedChild(rowEl,"Type");
				String type = typeEl.getTextContent();

				Element source = XMLUtil.firstNamedChild(rowEl,"Source");
				String sourceLoc = source.getTextContent();
				if (sourceLoc.startsWith("/")) sourceLoc = prefix + sourceLoc;
				
				Element mappings = XMLUtil.firstNamedChild(rowEl,"Mappings");
				String mapLoc = mappings.getTextContent();
				if (mapLoc.startsWith("/")) mapLoc = prefix + mapLoc;
				URI mappingSetURI = URI.createURI(mapLoc);

				// System.out.println("making source '" + sourceLoc + "'");
				DataSource qs = new DataSource(mappingSetURI,type,sourceLoc);
				qs.setCode(shortName(row));
				qs.setIsActive(true);
				dataSources.add(qs);
				viewer.add(qs);
				viewer.setChecked(qs, true);
				row++;
			}
			// if anything goes wrong for any Data source, just miss it out, with this message
			catch (Exception ex) 
				{System.out.println("Cannot load Data source: " + ex.getMessage());}
		}
	}

	//------------------------------------------------------------------------------------------
	//      saving and restoring the state of the view between successive sessions
	//------------------------------------------------------------------------------------------
	
	private static final String TAG_MAPPING_SET_URI = "mappingSetURI";
	private static final String TAG_INSTANCE_URI = "instanceURI";
	private static final String TAG_SOURCE_TYPE = "sourceType";
	private static final String TAG_ACTIVE = "active";
	private static final String TAG_TYPE = "DataSource";
	
	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		for (int i = 0; i < viewer.getTable().getItemCount();i++)
		{
			IMemento mem = memento.createChild(TAG_TYPE);
			DataSource qs = (DataSource)viewer.getElementAt(i);
			mem.putString(TAG_MAPPING_SET_URI, qs.mappingSetURIString());
			mem.putString(TAG_INSTANCE_URI, qs.instanceURIString());
			mem.putString(TAG_SOURCE_TYPE, qs.sourceType());
			if (viewer.getChecked(qs)) mem.putString(TAG_ACTIVE, TAG_ACTIVE);
		}	
	}
	
	/**
	 * re-initialise the model from its state at last closedown
	 * Note the viewer does not exist when this runs.
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);
		if (memento != null)
		{
			IMemento[] mems = memento.getChildren(TAG_TYPE);
			for (int i = 0; i < mems.length;i++)
			{
				IMemento mem = mems[i];
				String instanceURIString = mem.getString(TAG_INSTANCE_URI);
				String sourceType = mem.getString(TAG_SOURCE_TYPE);
				try 
				{
					URI mappingSetURI = URI.createURI(mem.getString(TAG_MAPPING_SET_URI));
					DataSource qs = new DataSource(mappingSetURI,sourceType,instanceURIString);
					dataSources.add(qs);					
					boolean active = false;
					String act = mem.getString(TAG_ACTIVE);
					if ((act != null) && (act.equals(TAG_ACTIVE))) active = true;
					qs.setIsActive(active);
				}
				catch (Exception ex)
				{System.out.println("Could not re-create Data Source for mappings at '" 
						+ mem.getString(TAG_MAPPING_SET_URI) + "', XML instance at '"
						+ instanceURIString + "': " + ex.getMessage());}
			}
			setShortNames();			
		}
	}
	

}
