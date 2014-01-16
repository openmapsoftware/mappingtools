package com.openMap1.mapper.actions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.SystemMessageChannel;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.DebugInstanceView;
import com.openMap1.mapper.views.DebugView;
import com.openMap1.mapper.views.WorkBenchUtil;

import com.openMap1.mapper.mapping.DebugRow;
import com.openMap1.mapper.query.RDBReader;
import com.openMap1.mapper.reader.DebugPostBox;
import com.openMap1.mapper.reader.EMFInstanceFactoryImpl;
import com.openMap1.mapper.reader.EMFInstanceFactory;
import com.openMap1.mapper.reader.GenericEMFInstanceFactoryImpl;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.structures.DBStructure;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.StructureType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Makes an instance of the ecore model, using the selected mapping set 
 * to read an xml file provided by the user.
 * 
 * This action may also be done in debugging mode, pausing on mappings
 * which have a breakpoint.
 * 
 * @author robert
 *
 */
public class MakeModelInstanceActionDelegate extends MapperActionDelegate 
implements IObjectActionDelegate,Runnable{
	
	private MappedStructure mapRoot;	
	private EMFInstanceFactory mf;
	
	private boolean debug;
	
	// instance variables needed by the run() method to run debug in another thread
	private Vector<objectToken> oReps;
	private String instancePath;
	private XOReader xor;
	private DebugPostBox debugPostBox;
    private String topClassName;
	
	public void run(IAction action) {
		try{
			
			String path = "";
			debug = (action.getId().equals("com.openMap1.mapper.Debug"));
			if (debug) showDebugView();

			// (1) find and open the mapping set
			mapRoot = mappedStructure();
			
			// (1b) possibly repair package names, if they have been changed in the class model
			if (repairPackages())
			{
				boolean saveRepairs = WorkBenchUtil.askConfirm("Package Name Changes", "Do you want to save changes of package names in mappings?");
				if (saveRepairs)
				{
					FileUtil.saveResource(mapRoot.eResource());
				}
			}
			
			if (mapRoot.getStructureType() == StructureType.RDBMS)
			{
				// (2a) for RDBMS sources, get a location to make an XML Instance file, and make it 
			    String[] modelExts= {};
				path = FileUtil.getFilePathFromUser(targetPart,modelExts,"Select location to make XML Instance from database:",true);
				if (path.equals("")) return;
				makeXMLInstanceFromDatabase(mapRoot,path);
			}
			else
			{
			    // (2b) show the dialog for the user to choose an input instance file
				String[] exts = mapRoot.getExtensions(); 
				path = FileUtil.getFilePathFromUser(targetPart,exts,"Select Instance",false);
				if (path.equals("")) return;				
			}
			
			// (3) Open the input file (possibly applying an input wrapper transformation)
			Element XMLRoot = mapRoot.getXMLRoot(path);
			if (XMLRoot == null) throw new MapperException("Could not open XML");
					    
		    // (5) create the XML Reader
			xor = mapRoot.getXOReader(XMLRoot, null, new SystemMessageChannel());
		    if (xor == null) throw new MapperException("Cannot create XML Reader");			
			
			/* (6) find which class is to be top of the Ecore tree (a) from an annotation
			 * on the class model, or (b) if there is no annotation, from the user */
			topClassName = null;
			EClass topClass = EMFInstanceFactoryImpl.getRecommendedTopClass(mapRoot.getClassModelRoot());
			if (topClass != null) topClassName = topClass.getName();
			if (topClassName == null) topClassName = askUserForTopClassName(xor,debug);
			if (topClassName == null) return;
			
			/* (7) if the XML represents no instances of the top class, fail with a message.  */
			oReps = xor.getAllLocalObjectTokens(topClassName);
			if (oReps.size() == 0) throw new MapperException
				("The XML instance represents no objects of the top class '" + topClassName + "'");
		    
		    // (8) get a location for the output EMF instance (make it up if debugging, to save a dialogue)
			// (9) create one Ecore model instance for each objectRep found for the class (if debugging, in anaother thread)
			instancePath = "";
			if (!debug)
			{
			    String[] modelExts= {};
				instancePath = FileUtil.getFilePathFromUser(targetPart,modelExts,"Select location for Model Instance",true);
				if (instancePath.equals("")) return;				

				makeEcoreInstances();
			}
			else if ((debug) && (xor instanceof MDLXOReader))
			{
				// don't bother the user for a file name
				instancePath = new StringTokenizer(path,".").nextToken() + ".debug";
				
				// open the debug instance view and show the XML tree in it
				DebugInstanceView debugInstanceView = WorkBenchUtil.getDebugInstanceView(true);
				debugInstanceView.setXMLRoot(XMLRoot);

				// set up the thread for debugging, the Debug view and the postbox
				Thread readerThread = new Thread(this);
				debugPostBox = new DebugPostBox(readerThread);
				DebugView debugView = WorkBenchUtil.getDebugView(false);
				debugView.setDebugPostbox(debugPostBox);
				debugView.setDebugInstanceView(debugInstanceView);

				// prepare the XOReader to run in debug mode
				MDLXOReader reader = (MDLXOReader)xor;
				reader.setDebugPostbox(debugPostBox);
				
				// kick off creating the EMF instance, in the reader thread
				debugPostBox.getReaderThread().start();
				
				// wait for the reader thread to halt or complete. In either case, this action completes
				debugView.awaitHaltOrResult();
				
				// forget the top class name, to give the user a chance to set it next time
				topClassName = null;
			}
			else throw new MapperException("XOReader does not use mappings in the mapping set, so cannot be debugged");
			
		}
		catch (Exception ex) 
		{
			showMessage("Failed to write EMF instance: " + ex.getMessage());
			System.out.println("Failed to write EMF instance: " + ex.getMessage());
			for (int i = 0; i < ex.getStackTrace().length; i++) System.out.println(ex.getStackTrace()[i].toString());
		}		
	}
	
	/**
	 * for the Runnable interface, to make a new thread  for debugging
	 */
	public void run()
	{
		try 
		{
			// get the objects of the top class again, to debug object mappings
			oReps = xor.getAllLocalObjectTokens(topClassName);
			// make the EMF instance for each object retrieved
			makeEcoreInstances();
		}
		catch (MapperException ex) {showMessage("Failed to write EMF instance in debug: " + ex.getMessage());}
		debugPostBox.setCompleted(true);
	}
	
	
	/**
	 * Method to make Ecore instances, having defined the start objects (oReps)
	 * and the base file name for the results (instancePath).
	 * This method is called in the main thread (for normal making of an EMF instance)
	 * or ins a separate thread (for debugging)
	 * @throws MapperException
	 */
	private void makeEcoreInstances() throws MapperException
	{
		for (int index = 0; index < oReps.size(); index ++)
		{
			String fileName = instancePath;
			// if there is more than one objectRep, distinguish the file names for each one
			if (oReps.size() > 1) fileName = addIndexToFileName(instancePath, index + 1);
			URI instanceURI = FileUtil.URIFromPath(fileName);
			
			mf = new GenericEMFInstanceFactoryImpl();
			mf.setNsUri(mapRoot.getClassModelRoot().getNsURI());
			mf.setNsPrefix(mapRoot.getClassModelRoot().getNsPrefix());
			mf.createModelInstance(xor,instanceURI,oReps.get(index));				
		}
		
	}
	
	/**
	 * give the user a list of all classes represented in the XML instance,
	 * (which have mappings in the top mapping set - not an imported one) 
	 * for which there is one or more instance in the XML instance - 
	 * so he can choose the top class for the ECore model instance.
	 * @param xor XOReader which uses the mappings to read an XML instance
	 * @return the name of the top class chosen by the user 
	 * ; return null if he cancels or if there were none to choose from
	 */
	private String askUserForTopClassName(XOReader xor, boolean debug) throws MapperException
	{
		Vector<String> labels = new Vector<String>();
		Vector<String> cNames = new Vector<String>();
		Hashtable<String,String> classesSought = new Hashtable<String,String>();
		for (Iterator<EClass> it = ModelUtil.getAllClasses(xor.classModel()).iterator();it.hasNext();)
		{
			EClass theClass = it.next();
			String className = ModelUtil.getQualifiedClassName(theClass);
			if ((classesSought.get(className) == null) && (xor.representsObject(className)))
			{
				classesSought.put(className,"1");
				Vector<objectToken> oReps = xor.getAllLocalObjectTokens(className);
				if (oReps.size() > 0) 
				{
					String cName = className + "(" + oReps.size() + ")";
					labels.add(cName);
					cNames.add(className);			
				}				
			}
		}
		if (labels.size() == 0)
		{
			showMessage("Cannot make EMF Model Instance",
					"Found no instances of any class represented in the XML");
			return null; // no choices available
		}
		int chosen = WorkBenchUtil.chooseOneString("Choose top class for EMF Instance", targetPart, labels);
		if (chosen == -1) return  null; //user cancelled
		return cNames.get(chosen);
	}
	
	
	/**
	 * @param fileName a file name entered by the user
	 * @param index an integer index
	 * @return the file name with '_' and the index appended to its root
	 */
	private String addIndexToFileName(String fileName, int index)
	{
		String newFileName = "";
		StringTokenizer st = new StringTokenizer(fileName,".",true);
		newFileName = newFileName + st.nextToken() + "_" + index;
		while (st.hasMoreTokens()) newFileName = newFileName + st.nextToken();
		return newFileName;
	}
	
	//--------------------------------------------------------------------------------------------------
	//                   make and store an XML Instance from a mapped Relational Database
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * Make an XML Instance from the database denoted in the mapped structure,
	 * and store it at the defined location
	 * FIXME this only works for small databases; need some dialogue and mechanism 
	 * to make it selective for large databases
	 */
	private void makeXMLInstanceFromDatabase(MappedStructure mapRoot,String path)
	throws MapperException
	{
        DBStructure database = (DBStructure)mapRoot.getStructureDefinition();
        RDBReader rxq = new RDBReader(database,path);
        Element rootNode = rxq.makeXMLDOM();
        Document doc = rootNode.getOwnerDocument();
        if (doc == null) System.out.println("Null document");
        System.out.println("Root element name: " + XMLUtil.getLocalName(rootNode));
        URI uri = FileUtil.URIFromPath(path);
    	IFile file =  EclipseFileUtil.getFile(uri.toString());
    	System.out.println("File path: " + uri.toString());
        if (file == null) System.out.println("null file");
    	EclipseFileUtil.writeOutputResource(doc, file, true);
	}

	
	//--------------------------------------------------------------------------------------------------------------
	//     Repair Package names in mappings in a Mapping Set, when package names have been changed in the mapped Ecore model
	//--------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * find all mappings in the mapping set.
	 * For each one, find the class in the class model regardless of its package, 
	 * and change the package name to be correct, if necessary
	 * @return true if any changes have been made
	 * @throws MapperException if any mapped class name is not fund in any package
	 */
	private boolean repairPackages() throws MapperException 
	{
		EPackage classModel = mapRoot.getClassModelRoot();
		ElementDef root = mapRoot.getRootElement();
		
		if (root != null) return repairPackagesOnNode(classModel,root);
		else return false;
	}
	
	/**
	 * recursive descent down a mapping set, 
	 * finding all mappings and changing the package name if necessary
	 * @param classModel
	 * @param nDef
	 * @return
	 * @throws MapperException
	 */
	private boolean repairPackagesOnNode(EPackage classModel, NodeDef nDef)  throws MapperException
	{
		boolean changesMade = false;
		NodeMappingSet nms = nDef.getNodeMappingSet();
		if (nms != null)
		{
			for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator();it.hasNext();)
				if (repairMapping(classModel,it.next())) changesMade = true;
			
			for (Iterator<PropMapping> it = nms.getPropertyMappings().iterator();it.hasNext();)
				if (repairMapping(classModel,it.next())) changesMade = true;
			
			for (Iterator<AssocMapping> it = nms.getAssociationMappings().iterator();it.hasNext();)
			{
				AssocMapping am = it.next();
				if (repairMapping(classModel,am.getMappedEnd1())) changesMade = true;
				if (repairMapping(classModel,am.getMappedEnd2())) changesMade = true;
			}
		}
		
		if (nDef instanceof ElementDef)
		{
			ElementDef elDef = (ElementDef)nDef;
			
			for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
				if (repairPackagesOnNode(classModel, it.next())) changesMade = true;
			
			for (Iterator<AttributeDef> it = elDef.getAttributeDefs().iterator();it.hasNext();)
				if (repairPackagesOnNode(classModel, it.next())) changesMade = true;
		}
		
		return changesMade;
	}
	
	/**
	 * repair one mapping, changing the package name if necessary
	 * @param classModel
	 * @param m
	 * @return true if the package name has been changed
	 * @throws MapperException if the class name cannot be fond in any package
	 */
	private boolean repairMapping(EPackage classModel,Mapping m) throws MapperException
	{
		boolean changedPackage = false;
		String className = m.getMappedClass();
		String packageName = m.getMappedPackage();

		// if the class can be found in the right package, do not look in other packages
		EPackage ownPackage = ModelUtil.getNamedPackage(classModel, packageName);
		if ((ownPackage != null) && (ModelUtil.getEClass(ownPackage, className) != null)) return false;
		
		// otherwise, look for the class in all packages
		boolean found = false;
		for (Iterator<EPackage> it = classModel.getESubpackages().iterator();it.hasNext();)
		{
			EPackage pack = it.next();
			EClass theClass = ModelUtil.getEClass(pack, className);
			if (theClass != null)
			{
				found = true;
				m.setMappedPackage(pack.getName());
				changedPackage = true;
			}
		}
		if (!found) throw new MapperException("Cannot find mapped class '" + className + "' in any package");
		
		return changedPackage;
	}

	
	//--------------------------------------------------------------------------------------------------
	//                                          Debugging
	//--------------------------------------------------------------------------------------------------
	
	private void showDebugView()
	{
		WorkBenchUtil.getDebugView(true).showNewResult(new Vector<DebugRow>());
		WorkBenchUtil.page().activate(WorkBenchUtil.getDebugView(true));
	}



}
