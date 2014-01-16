package com.openMap1.mapper.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.XSDStructure;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xsd.XSDSchema;

/**
 * A collection of file handling utilities, all static.
 * @author robert
 *
 */

public class FileUtil {
	
	/**
	 * @return the string 'platform:/resource/'
	 */
	public static String platformPreface() {return platformPreface;}
	private static String platformPreface= "platform:/resource/";


	
	/**
	 * Get a file location from the user
	 * @param part IWorkbenchPart - used only to get a shell for the dialogue
	 * @param allowedExts String array of allowed file extensions, of the form {"*.uml","*.xsd"}
	 * @param title the title for the dialogue box
	 * @param saveFile: if true, the dialogue is to save a file; if false, to open an existing file
	 * @return String the file path selected , such as "C:\blah\blah.uml" ; or "" if none was chosen
	 */
	public static String getFilePathFromUser(IWorkbenchPart part, String[] allowedExts,String title, boolean saveFile)
	{
		String path = "";
		FileDialog fd = null;
		Shell parent = part.getSite().getShell();
		if (saveFile) fd = new FileDialog(parent,SWT.SAVE);	
		else fd = new FileDialog(parent,SWT.OPEN);	
		fd.setText(title);
		fd.setFilterExtensions(allowedExts); 
		fd.open();
		if (!fd.getFileName().equals(""))path = fd.getFilterPath() + "\\" + fd.getFileName();
		return path;
	}
	
	/**
	 * @return the root of the workspace
	 */
	static IWorkspaceRoot getRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();		
	}
	
	/**
	 * 
	 * @param resourceLocation a location in the workspace, starting with "platform:/resource/"
	 * @return the absolute location
	 * @throws MapperException
	 */
	public static String absoluteLocation(String resourceLocation)
	throws MapperException
	{
		return absoluteLocation(workspaceRootPath(),resourceLocation);
	}
	
	/**
	 * @param rootPath path to the root of the workspace
	 * @param resourceLocation a location in the workspace, starting with "platform:/resource/"
	 * @return the absolute location
	 * @throws MapperException
	 */
	public static String absoluteLocation(String rootPath, String resourceLocation)
			throws MapperException
	{
		String rStart = "platform:/resource/";
		if (!(resourceLocation.startsWith(rStart))) throw new MapperException("Resource location '" 
				+ resourceLocation + "' does not start with '" + rStart + "'");
		String relLoc = resourceLocation.substring(rStart.length());
		return (rootPath + "/" + relLoc); 		
	}
	
	/**
	 * 
	 * @param absoluteLocation an absolute file location, which is in the current workspace
	 * @return the relative location in the workspace, starting with "platform:/resource/"
	 * @throws MapperException if the absolute location is not in the workspace
	 */
	public static String resourceLocation(String absoluteLocation) throws MapperException
	{
		String rStart = "platform:/resource/";
		String aStart = removeInitialSlashes(workspaceRootPath());
		// message("Adjusted workspace root path: " + aStart);

		String absPath = removeInitialSlashes(forwardSlashForm(absoluteLocation));
		// message("Absolute path to file: " + absPath);
		if (absPath.startsWith("file:")) absPath = removeInitialSlashes(removeFilePrefix(absPath));
		
		boolean inWorkspace = (absPath.startsWith(aStart));
		if (!inWorkspace) throw new MapperException("Absolute location '" 
				+ absPath + "' is not in the current workspace");
		String relLoc = absPath.substring(aStart.length() + 1);
		String relPath = (rStart + relLoc);
		// message("Computed relative path " + relPath);
		return relPath; 	
	}
	
	/**
	 * remove any number of initial '/' from a string
	 * @param s
	 */
	private static String removeInitialSlashes(String s)
	{
		String result = s;
		while (result.startsWith("/")) result = result.substring(1);
		return result;
	}
	
	
	/**
	 * remove the prefix 'file:' from any file path, leaving a string which on PCs will look like '/C:/...'.
	 * (Previously I removed 6 characters to give 'C:/...'. This seems to work as well on PCs,
	 * and the change is made in order to work on Macs)
	 * @param filePath
	 * @return
	 */
	public static String removeFilePrefix(String filePath)
	{
		return filePath.substring(5);
	}


	/**
	 * @return a URIConverter that will normalise 'platform:/resource/' URIs to absolute file paths
	 */
	public static URIConverter editURIConverter()
	{
		URIConverter uc = new ExtensibleURIConverterImpl();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		URI uri1 = URI.createURI("platform:/resource/");
		URI uri2 = URI.createURI("file:/" + root.getLocation().toString() + "/");
		uc.getURIMap().put(uri1,uri2);
		return uc;
	}
	
	/**
	 * @param filePath a file path as returned by an SWT FileDialog, such as "C:\blah\blah.uml"
	 * @return if the path is in the workspace, the platform:resource URI
	 * or the file URI if the path is not in the workspace
	 */
	public static URI URIFromPath(String filePath)
	{
		URI uri = null;
		if (forwardSlashForm(filePath).startsWith(workspaceRootPath() + "/"))
		{
			// remove the root path, but not the first '/' after it
			String relPath = forwardSlashForm(filePath).substring(workspaceRootPath().length());
			uri = URI.createPlatformResourceURI(relPath,true);
		}
		else
		{
			uri = URI.createFileURI(forwardSlashForm(filePath));
		}
		return uri;
	}
	
	/**
	 * @return absolute file path to the root of the Eclipse workspace
	 */
	public static String workspaceRootPath()
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getLocation().toString();		
	}
	
	public static String getResourceURIString(String fileURIString)
	{
		String uriString = null;
		String filePrefix = "file:/";
		String rawLocation = fileURIString.substring(filePrefix.length());
		// the +1 below removes an extra '/'
		if (rawLocation.startsWith(workspaceRootPath()))
			uriString = platformPreface() + rawLocation.substring(workspaceRootPath().length()+1);
		return uriString;		
	}
	

	public static void saveResource(Resource resource)
	{
		  try{
				// Save the contents of the resource to the file system.
				Map<Object, Object> options = new HashMap<Object, Object>();
				options.put(XMLResource.OPTION_ENCODING, "UTF-8");
				resource.save(options);
			  }
			  catch (Exception ex) {}						
	}

	
	/**
	 * 
	 * @return true if we are running inside Eclipse; false otherwise.
	 * This relies on throwing an Exception if classes within Eclipse 
	 * (org/osgi/framework/BundleActivator) cannot be found; 
	 * so it needs that library not to be in the classpath.
	 */
	public static boolean isInEclipse()
	{
		boolean inEclipse = true;
		try {workspaceRootPath();}
		catch (NoClassDefFoundError ex) {inEclipse = false;}
		catch (IllegalStateException ex) {inEclipse = false;}
		return inEclipse;
	}
	
	/**
	 * @param filePath an absolute file path, with forward slashes as separators
	 * @return true if the file path is in the Eclipse workspace
	 */
	public static boolean isInWorkSpace(String filePath)
	{
		return (forwardSlashForm(filePath).startsWith(workspaceRootPath() + "/"));		
	}
	
	/**
	 * Convert a file path so the separators are all '/', not '\'
	 * @param path
	 */
	public static String forwardSlashForm(String path)
	{
		String fsPath = "";
		StringTokenizer st = new StringTokenizer(path,"\\");
		while (st.hasMoreTokens())
		{
			fsPath = fsPath + st.nextToken();
			if (st.hasMoreTokens()) fsPath = fsPath + "/";
		}
		if (path.endsWith("\\")) fsPath = fsPath + "/";
		return fsPath;
	}
	
	public static String fSlashForm(String path)
	{
		char[] newVersion = new char[path.length()];
		for (int i = 0; i < path.length();i++)
		{
			char c = path.charAt(i);
			if (c == '\\') c = '/';
			newVersion[i] = c;
		}
		String result = new String(newVersion);
		return result;
	}
	
	/**
	 * this method should return a correct location for the Resource
	 * containing an EObject, both inside and outside Eclipse
	 * @param eo
	 * @return
	 */
	public static String fileLocation(EObject eo)
	{
		return editURIConverter().normalize(eo.eResource().getURI()).toString();
	}
	
	/**
	 * Open a file as an EMF model
	 * @param uriString the string form of the URI of the model file
	 * @return the EObject root of the model; or null if failed to open
	 */
	public static EObject getEMFModelRoot(String uriString) throws IOException
	{
		return getEMFModelRoot(URI.createURI(uriString));
	}
	
	/**
	 * Open a file as an EMF model
	 * @param uri the URI of the model file
	 * @return the EObject root of the model; or null if failed to open
	 */
	public static EObject getEMFModelRoot(URI uri) throws IOException
	{
		return (EObject)getEMFResource(uri).getContents().get(0);
	}
	
	public static MappedStructure getMappingSet(URI uri) throws MapperException
	{
		EObject root = null;
		String exMessage = "Could not open mapping set at " + uri.toString() + ": ";
		try {root = getEMFModelRoot(uri);}
		catch (IOException ex) {throw new MapperException(exMessage + ex.getMessage());}
		if (root == null) {throw new MapperException(exMessage + "Null root.");}
		if (!(root instanceof MappedStructure)) {throw new MapperException(exMessage + "Root is not a MappedStructure.");}
		return (MappedStructure)root;	
	}

	
	
	/**
	 * Open a file as an EMF model
	 * @param uri the URI of the model file
	 * @return the resource; or null if failed to open
	 */
	public static Resource getEMFResource(URI uri) throws IOException
	{
	    ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
		   put(uri.fileExtension(),new XMIResourceFactoryImpl());

		Resource emfResource = resourceSet.createResource(uri);
		emfResource.load(null);
		return emfResource;
	}
	
	public static XSDStructure userChooseStructure(IWorkbenchPart targetPart) throws MapperException
	{
		XSDStructure xsd = null;
		// show a dialog for the user to choose a schema, if he wants to order the EReferences correctly
		String[] exts = {"*.xsd"}; 
		String schemaFilePath = FileUtil.getFilePathFromUser(targetPart,exts,"Select XML schema",false);			
		if (schemaFilePath.equals("")) return xsd;
		
		//  Open the schema
		URI uri = FileUtil.URIFromPath(schemaFilePath);
		XSDSchema theSchema = XSDStructure.getXSDRoot(uri);

		// find the tree StructureDefinition from the schema 
		if (theSchema != null) xsd = new XSDStructure(theSchema);
		
		return xsd;		
	}


	/**
	 * @param classModelFileLocation full file path to the ecore file
	 * @return the EPackage for the class model
	 * @throws IOException
	 */
	public static EPackage getClassModel(String classModelFileLocation)
	throws MapperException
	{
		// register the Ecore package
		EcorePackage.eINSTANCE.getEFactoryInstance(); 

		ResourceSet resourceSet = new ResourceSetImpl();

		// register the factory
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
			put("ecore", new XMIResourceFactoryImpl());

		// create the resource
		URI uri = URI.createURI("file:/" + classModelFileLocation);
		Resource resource = resourceSet.createResource(uri);

		// read the resource and make the class model
		try {
			resource.load(null);
			EPackage classModel = (EPackage)resource.getContents().get(0);			
			return classModel;		
		}
		catch (IOException ex) {throw new MapperException(ex.getMessage());}		
	}
	
	/**
	 * 
	 * @param mapperLocation the location of the mapping set
	 * @return the location of the project folder, without a final '/'
	 * @throws MapperException
	 */
	public static String projectFolderLocation(String mapperLocation) throws MapperException
	{
		String mapLoc = forwardSlashForm(mapperLocation);
		// URI.toString gives a 'file:/' prefix which we do not want
		if (mapLoc.startsWith("file:/")) mapLoc = removeFilePrefix(mapLoc);

		// copy the mapping set location up to and not including the 'MappingSets' folder
		String folderLoc = ""; 
		StringTokenizer st = new StringTokenizer(mapLoc,"/.",true);
		boolean foundMappingFolder = false;
		while ((st.hasMoreTokens())&& !foundMappingFolder)
		{
			String tok = st.nextToken();
			if (tok.equals("MappingSets")) foundMappingFolder = true;
			if (!foundMappingFolder) folderLoc = folderLoc + tok;
		}
		
		// throw an exception if no MappingSets folder was found
		if (!foundMappingFolder) throw new MapperException("Unexpected location for mapping set, not in 'MappingSets' folder: " + mapLoc);
		
		int len = folderLoc.length();
		return folderLoc.substring(0,len-1);		
	}

	
	/**
	 * finds a mapping set referred to from a node in soem mapping set by 
	 * @param node
	 * @param platformURL
	 * @return
	 * @throws MapperException
	 */
	public static MappedStructure getImportedMappedStructure(EObject node, String platformURL)
	throws MapperException
	{
		if (isInEclipse())
		{
			return getMappingSet(URI.createURI(platformURL));
		}
		else try
		{
			String importingLocation = node.eResource().getURI().toString();
			return getMappedStructure(getImportedLocation(platformURL,importingLocation));
		}
		catch (IOException ex) {throw new MapperException(ex.getMessage());}
	}
	
	/**
	 * 
	 * @param platformURL a URL of a mapping set, 
	 * expressed as 'platform:/resource/<project>/MappingSets/<folders>/imported.mapper'
	 * @param importingLocation the file location of the importing mapping set, expressed as
	 * 'file:/<drive letter>:/<outer folders>/MappingSets/<more folders>/importing.mapper'
	 * @return the correct path to imported.mapper, in the form
	 * '<drive letter>:/<outer folders>/MappingSets/<folders>/imported.mapper';
	 * 
	 * i.e it replaces 'platform:/resource/<project>/' at the front by 'C:/<folders>/'
	 * The initial trail <outer folders> should not include a 'MappingSets' folder
	 * 
	 */
	public static String getImportedLocation(String platformURL, String importingLocation)
	{
		// tidy up the importing location, just in case
		String impLoc = forwardSlashForm(importingLocation);
		if (impLoc.startsWith("file:/")) impLoc = removeFilePrefix(impLoc);
		
		// remove the back end from the importing file location
		int startLength = impLoc.length() - afterFolder("MappingSets",impLoc).length();
		String start = impLoc.substring(0,startLength);
		
		// add the back end of the imported file location
		String importedLocation = start + afterFolder("MappingSets",platformURL);
		return importedLocation;
	}
	
	/**
	 * From 
	 * @param folderName the name of a folder in a path
	 * @param path a String  'blah/blah/<folderName>/more/fileName'
	 * @return '/more/fileName'
	 */
	private static String afterFolder(String folderName, String path)
	{
		boolean foundFolder = false;
		String newPath = "";
		// pass through the slashes
		StringTokenizer st = new StringTokenizer(path,"/",true);
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			if (foundFolder) newPath = newPath + step;
			if (step.equals(folderName)) foundFolder = true;
		}
		return newPath;
	}

	
	/**
	 * 
	 * @param mapperFileLocation full file path to the mapper file
	 * @return the MappedStructure (mapping set) at that location
	 * @throws MapperException
	 * @throws IOException
	 */
	public static MappedStructure getMappedStructure(String mapperFileLocation)
	throws IOException
	{
		// register the mapper package
		MapperPackage.eINSTANCE.getEFactoryInstance(); 

		ResourceSet resourceSet = new ResourceSetImpl();

		// register the factory
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
			put("mapper", new XMIResourceFactoryImpl());

		// create the resource
		String uriString = "file:/" + mapperFileLocation;
		URI uri = URI.createURI(uriString);
		Resource resource = resourceSet.createResource(uri);
		if (resource == null) System.out.println("Null resource at " + uriString);

		// read the resource and make the mapped Structure
		resource.load(null);
		MappedStructure ms = (MappedStructure)resource.getContents().get(0);
		
		return ms;		
	}
	
	
	public static String getXSLLocation(String xslFileName, MappedStructure referenceMS)
	{
		URI mapperFileURI = referenceMS.eResource().getURI();

		String mapperLoc = FileUtil.editURIConverter().normalize(mapperFileURI).toString();
		if (mapperLoc.startsWith("file:/")) mapperLoc = removeFilePrefix(mapperLoc);
		String xslLoc = "";
		StringTokenizer st = new StringTokenizer(mapperLoc,"/\\",true);
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (st.hasMoreTokens()) xslLoc = xslLoc + token;
			else xslLoc = xslLoc + xslFileName;
		}
		return xslLoc;		
	}


    
    /**
     * @param URIString a URI in the workspace, 
     * which may or may not begin with 'platform:/resource/'
     * @return the IProject of IPRoject handle containing that URI
     */
	public static IProject getProject(String URIString)
    {
    	String pathString = URIString;
    	if (pathString.startsWith(platformPreface))
    		pathString = pathString.substring(platformPreface.length());
    	StringTokenizer st = new StringTokenizer(pathString,"/\\");
		return getRoot().getProject(st.nextToken());
    }
	
	/**
	 * Get an image from an icon folder in the com.openMap1.mapper.edit plugin
	 * @param ImageFileName
	 * @return
	 */
	public static Image getImage(String ImageFileName)
	{
		Image im = null;
		try {
			String pluginURLString = "platform:/plugin/com.openMap1.mapper.edit/icons/full/obj16/" + ImageFileName + ".gif";
			URL u1 = new URL(pluginURLString);
			URL u2= FileLocator.toFileURL(u1);
			// strip off the first 5 characters 'file:' from the URL
			String imageFileLocation  = removeFilePrefix(u2.toString());
			// System.out.println("Image file at '" + imageFileLocation + "'");
			im = new Image(null,imageFileLocation);
		}
		catch (IOException ex) {System.out.println("IO Exception getting image " + ImageFileName + ": " + ex.getMessage());}
		return im;
	}
	
	// strip off anything before slashes in a path name, to leave the file name
	/**
	 * @param path full path name to a file
	 * @return the file name
	 */
	public static String getFileName(String path)
	{
		String res = "";
		StringTokenizer st = new StringTokenizer(path, "/\\");
		while (st.hasMoreTokens()) {res = st.nextToken();}
		return res;		
	}
	
	/**
	 * @param path full path name to a file
	 * @return the path to the folder, including the final '/' or '\'
	 */
	public static String getFolder(String path)
	{
		String fileName = getFileName(path);
		return path.substring(0,path.length() - fileName.length());
	}
	
	/**
	 * @param path the full path to a file
	 * @param fileName the name of a sibling file, in the same folder
	 * @return path to the sibling file
	 */
	public static String siblingFilePath(String path,String fileName)
	{
		int pathLength = path.length() - getFileName(path).length();
		return path.substring(0,pathLength) + fileName;
	}
	
	//--------------------------------------------------------------------------------------------------
	//                        methods for placing and finding .wproc files
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * derive the location of a wproc file from the corresponding mapper file location
	 * (got by URI.toString(), which gives '/' separators).
	 * The path is the same except that:
	 * (1) the 'MappingSets' folder is replaced by the 'Translators' folder (subfolders are the same)
	 * (2) The file extension '.mapper' is replaced by '.wproc'
	 */
	public static String wProcLocation(String mapperLocation)
	{
		String mapLoc = FileUtil.forwardSlashForm(mapperLocation);
		// URI.toString gives a 'file:/' prefix which we do not want
		if (mapperLocation.startsWith("file:/")) mapLoc = removeFilePrefix(mapperLocation);
		String wProcLoc = "";
		// treat the delimiters  '/'. '.' as tokens (pass them through)
		StringTokenizer st = new StringTokenizer(mapLoc,"/.",true);
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			if (tok.equals("MappingSets")) tok = "Translators";
			if (tok.equals("mapper") && (!st.hasMoreTokens())) tok = "wproc";
			wProcLoc = wProcLoc + tok;
		}
		return wProcLoc;
	}
	
	
	/**
	 * derive the location of a Ecore file from the corresponding mapper file location
	 * (got by URI.toString(), which gives '/' separators),
	 * and from the EMFModelURI string in the mapping set.
	 * This is for use outside Eclipse, assuming the Mapper project folder
	 * structure has been copied
	 */
	public static String ecoreFileLocation(String mapperLocation, String umlModelURI) throws MapperException
	{

		String ecoreFileLoc = projectFolderLocation(mapperLocation) + "/"; 
		// copy the Ecore model location after and including the 'ClassModel' folder
		// treat the delimiters  '/'. '.' as tokens (pass them through)
		StringTokenizer st = new StringTokenizer(umlModelURI,"/.",true);
		boolean foundClassModelFolder = false;
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			if (tok.equals("ClassModel")) foundClassModelFolder = true;
			if (foundClassModelFolder) ecoreFileLoc = ecoreFileLoc + tok;
		}		
		return ecoreFileLoc;
	}
	
	
	//--------------------------------------------------------------------------------------------------
	//                                         reading text files
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * read a text file at an absolute file location
	 */
	public static FileInputStream getTextFile(String location) throws MapperException
	{
		FileInputStream fi = null;
		try {
			fi = new FileInputStream(location);
		}
		catch (Exception ex) {throw new MapperException("Cannot read text file at '" + location + "': " + ex.getMessage());}
		return fi;
	}
	
	/**
	 * 
	 * @param location an absolute file location
	 * @return the lines of a text file at the location
	 * @throws MapperException
	 */
	public static Vector<String> textLines(String location) throws MapperException
	{
		Vector<String> lines = new Vector<String>();
		FileInputStream fiz = getTextFile(location);
        InputStreamReader isr = new InputStreamReader(fiz);

        LineNumberReader lnr = new LineNumberReader(isr);
        try {
            String line = lnr.readLine();
            while (line != null)
            {
                lines.add(line);
                line = lnr.readLine();
            }
            lnr.close();        	
        }
		catch (Exception ex) {throw new MapperException("Failure reading read text file at '" + location + "': " + ex.getMessage());}

        return lines;
	}
	
	/**
	 * 
	 * @param fileLocation
	 * @return the parsed rows of a csv file, including the header row
	 * @throws MapperException
	 */
	public static Vector<String[]> getCSVRows(String fileLocation) throws MapperException
	{
		Vector<String> lines = textLines(fileLocation);
		if (lines.size() < 1) throw new MapperException("No lines in csv file");
		StringTokenizer cols = new StringTokenizer(lines.get(0),",");
		
		Vector<String[]> csvRows = new Vector<String[]>();
		for (int i = 0; i < lines.size(); i++)
			csvRows.add(parseCSVLine(cols.countTokens(),lines.get(i)));
		
		return csvRows;
	}
	
	
	/**
	 * @param bufferedReader reader of a text file
	 * @return each line of the file as a String
	 * @throws MapperException
	 */
	public static Vector<String> getLines(InputStream inputFile) throws MapperException
	{
		Reader reader = new InputStreamReader(inputFile);
		BufferedReader bufferedReader = new BufferedReader(reader);
		Vector<String> lines = new Vector<String>();
		String line = "";
		while (line != null) try
		{
			line = bufferedReader.readLine();
			if (line != null) lines.add(line);
		}
		catch (IOException ex) {throw new MapperException(ex.getMessage());}
		return lines;
	}
	
	/**
	 * treats the contents of cells as follows:
	 * remove any new lines, which will screw up a csv file
	 * if there are no ',' or '"', no other change
	 * any '"' is doubled up
	 * if there are any ',', the whole cell contents are enclosed in two '"'
	 * This means ',' which occur after an odd number of '"' are there in the text;
	 * whereas ',' which occur after an even number of '"' are cell separators
	 * @param cells
	 * @return
	 */
	public static String makeCSVLine(String[] cells)
	{
		String line = "";
		for (int i = 0; i < cells.length; i++)
		{
			String cell = "";
			
			// double up any existing '"', and remove any new lines
			StringTokenizer st = new StringTokenizer(removeNewLines(cells[i]),"\"",true);
			while (st.hasMoreTokens())
			{
				String token = st.nextToken();
				cell = cell + token;
				if (token.equals("\"")) cell = cell + token;
			}
			
			// if the cell has any ',', enclose it all in two outer '"'
			int commas = new StringTokenizer(cell,",",true).countTokens() - 1;
			if (commas > 0) cell = "\"" + cell + "\"";
			
			// add the cell and a separator comma to the line
			line = line + cell;
			if (i < cells.length - 1) line = line + ",";
			
		}
		return line;
	}
	
	/**
	 * remove any new lines from a text String, replacing them by spaces
	 * @param text
	 * @return
	 */
	public static String removeNewLines(String text)
	{
		byte[] newLineChars = {13,10};
		byte[] rawBytes = text.getBytes();
		byte[] newBytes = new byte[rawBytes.length];
		
		for (int b = 0; b < rawBytes.length; b++)
		{
			byte raw = rawBytes[b];
			for (int i = 0; i < 2; i++) if (raw == newLineChars[i]) raw = ' ';
			newBytes[b] = raw;
		}		
		return new String(newBytes);		
	}

	/**
	 * read a line of a csv file, expected to have not more than columns separated fields,
	 * and return a string array of the fields, including "" for any initial ',' 
	 * or for final fields not supplied.
	 * Deals with commas and quotes in cells as handled by makeCSVLine
	 * @param columns max number of columns allowed
	 * @param line
	 * @return String array of field values
	 * @throws MapperException
	 */
	public static String[] parseCSVLine(int columns, String line) throws MapperException
	{
		String[] field = new String[columns];
		// break on either comma or quote, and retain them both as tokens
		StringTokenizer st = new StringTokenizer(line,",\"",true);
		int col = 0;
		field[0] = "";
		boolean evenQuotes = true;
		while (st.hasMoreTokens())
		{
			String val = st.nextToken();
			if (val.equals("\""))
			{
				evenQuotes = !evenQuotes;
				/* always consume a quote on its own without adding it to the cell contents */
				if (st.hasMoreTokens()) 
				{
					val = st.nextToken(); // may be '"' ','  or something else
					if (val.equals("\"")) evenQuotes = !evenQuotes;
				}
				else val = "";  // quote at the end of the line
			}

			// a comma after an even number of quotes marks a new cell
			if ((val.equals(",")) && evenQuotes) // no 'else' because the first condition may have fired and changed val
			{
				col++;					
				if (col > columns -1) throw new MapperException("Too many columns in csv line '" + line + "'");
				field[col]="";
			}

			// at this point val can be '"'  ','  or something else; whatever it is, add it to the cell
			else field[col] = field[col] + val;
		}
		// trailing fields not even given ','
		if (col < columns - 1)
			for (int c = col + 1; c < columns; c++) field[c] = "";
		return field;
	}
	
	/**
	 * read a line of a csv file, expected to have not more than columns separated fields,
	 * and return a string array of the fields, including "" for and initial ',' or two successive ',',
	 * or for final fields not supplied.
	 * FIXME: does not deal with commas within the csv fields.
	 * @param columns max number of columns allowed
	 * @param line
	 * @return String array of field values
	 * @throws MapperException
	 */
	public static String[] oldParseCSVLine(int columns, String line) throws MapperException
	{
		String[] field = new String[columns];
		StringTokenizer st = new StringTokenizer(line,",",true);
		int col = 0;
		boolean emptyField = true;
		while (st.hasMoreTokens())
		{
			if (col > columns -1) throw new MapperException("Too many columns in csv line '" + line + "'");
			String val = st.nextToken();
			if (val.equals(","))
			{
				if (emptyField) // initial ',', or two successive ','
				{
					field[col]="";
					col++;					
				}
				emptyField = true;
			}
			else // non-empty field
			{
				field[col] = val;
				col++;
				emptyField = false;
			}
		}
		// trailing fields not even given ','
		if (col < columns)
			for (int c = col; c < columns; c++) field[c] = "";
		return field;
	}
	
	//--------------------------------------------------------------------------------------------------
	//                                         writing csv files
	//--------------------------------------------------------------------------------------------------
	
	public static void writeCSVFile(String location, Vector<String[]> csvRows) throws MapperException
	{
		try
		{
	    	FileOutputStream fo = new FileOutputStream(location);
	    	for (int i = 0; i < csvRows.size(); i++)
	    	{
	    		String[] row = csvRows.get(i);
	    		String rowText = makeCSVLine(row);
	    		nl(rowText, fo);
	    	}
	    	fo.close();	
		}
		catch (Exception ex) {ex.printStackTrace();throw new MapperException(ex.getMessage());}		
	}
	
	//--------------------------------------------------------------------------------------------------
	//                                         writing text files
	//--------------------------------------------------------------------------------------------------

	private static byte[] newLine = {13,10};

    // write out a String followed by a new line
	public static  void nl(String s, OutputStream fo)
    {
        try{
        fo.write(s.getBytes());
        fo.write(newLine);
        }
        catch (IOException e)
            {message("Exception writing line '" + s + "': " + e.getMessage());}
    }

    // write out a String followed by no new line
    public static  void nnl(String s, OutputStream fo)
    {
        try{
        fo.write(s.getBytes());
        }
        catch (IOException e)
            {message("Exception writing line '" + s + "': " + e.getMessage());}
    }

    public static  void quoted(String s, FileOutputStream fo)
    {
        byte[] quote = {'"'};
        try{
            fo.write(quote);
            fo.write(s.getBytes());
            fo.write(quote);
        }
        catch (IOException e)
            {message("Exception writing quoted string '" + s + "': " + e.getMessage());}
    } 
    
    //--------------------------------------------------------------------------------------------------
    //                        Base64 Encoding (for PDF files)
    //--------------------------------------------------------------------------------------------------
    
    /**
     * encode a binary input as a base64 String
     * @param input
     * @return
     */
    public static String base64Encode(byte[] input)
    {
    	byte[] encoded = new Base64().encode(input);
    	String result = new String(encoded);
    	return result;
    }

    
    //--------------------------------------------------------------------------------------------------
    //                               Trivia
    //--------------------------------------------------------------------------------------------------
    
    public static void message(String s) {System.out.println(s);}

}
