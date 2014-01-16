package com.openMap1.mapper.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EPackage;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.converters.AbstractMapperWrapper;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.MappedStructure;

/**
 * static file utilities that require the classes IFile and IPath - which I cannot find
 * in jar files, so cannot deploy outside Eclipse
 * @author robert
 *
 */
public class EclipseFileUtil {
	
	public static String TEMPORARY_XML_FILE = "/eclipseTempFile.xml";
	public static String TEMPORARY_TEXT_FILE = "/eclipseTempFile.TXT";
	
	
	public static String getAbsoluteLocation(IFolder folder)
	{
		return folder.getFullPath().toString();
	}
    
    /**
     * @param URIString a URI in the workspace, 
     * which may or may not begin with 'platform:/resource/'
     * @return the IFile or IFile handle at that URI
     */
	public static IFile getFile(String URIString)
    {
    	String pathString = URIString;
    	if (pathString.startsWith(FileUtil.platformPreface()))
    		pathString = pathString.substring(FileUtil.platformPreface().length()-1); 
    	IFile file =  FileUtil.getRoot().getFile(new Path(pathString));    		
    	return file;
    }
	
	/**
	 * 
	 * @param file
	 * @return resource location of an IFile
	 */
	public static String getResourceLocation(IFile file)
	{
		String start = FileUtil.platformPreface(); // string 'platform:/resource/'
		start = start.substring(0,start.length()-1); // remove the final '/'
		return start + file.getFullPath().toString();
	}
	
	public static EPackage getClassModel(IFile file)  throws MapperException
	{
		IFolder folder = (IFolder) file.getParent();
		// this does not introduce '%20' if filenames have spaces
		String path = folder.getLocationURI().toString() + "/" + file.getName();	
		String filePath = FileUtil.removeFilePrefix(path); // remove 'file:'
		return FileUtil.getClassModel(filePath);
	}
	
	
	public static Element getRoot(IFile file) throws MapperException
	{
		IFolder folder = (IFolder) file.getParent();
		// this does not introduce '%20' if filenames have spaces
		String path = folder.getLocationURI().toString() + "/" + file.getName();	
		String filePath = FileUtil.removeFilePrefix(path); // remove 'file:'
		Element root = XMLUtil.getRootElement(filePath);
		return root;
	}
	
	public static Vector<String> textLines(IFile file) throws MapperException
	{
		IFolder folder = (IFolder) file.getParent();
		// this does not introduce '%20' if filenames have spaces
		String path = folder.getLocationURI().toString() + "/" + file.getName();	
		String filePath = FileUtil.removeFilePrefix(path); // remove 'file:'
		return FileUtil.textLines(filePath);
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws MapperException
	 */
	public static Vector<String[]> csvRows(IFile file) throws MapperException
	{
		Vector<String> lines = textLines(file);
		// count the number of columns from the header row
		StringTokenizer st = new StringTokenizer(lines.get(0),",");
		int columns = st.countTokens();
		Vector<String[]> rows = new Vector<String[]>();
		for (int i = 0; i < lines.size(); i++)
			rows.add(FileUtil.parseCSVLine(columns, lines.get(i)));
		return rows;
	}



	/**
	 * @param mapperLocation the location of a mapper file
	 * @return the last changed date of the corresponding WProc file,
	 * in milliseconds since Jan 1 1970, if it exists;
	 * or 0 if it does not exist
	 */
	public static long wProcFileDate(MappedStructure mappedStructure)
	{
		long wpDate = 0;		
		try{
			IFile wpFile = proceduresFile(mappedStructure);
			if (wpFile != null)
			{
				boolean exists = wpFile.exists(); // separate line for debugging
				if (exists) wpDate = wpFile.getLocalTimeStamp();			
			}			
		}
		catch(Exception ex) {}		
		return wpDate;
	}
	
	/**
	 * get the procedures file for the current Mapping file 
	 * from the standard location in this project.
	 * This is the same sub-folder of the Translators folder as the sub-folder of
	 * the MappingSets folder holding the mapping set; make the folder if necessary
	 * @return
	 */
	static public IFile proceduresFile(MappedStructure mappedStructure) throws MapperException
	{
		String resourceLocation = mappedStructure.eResource().getURI().toString();
		// deal with an initial '//' which occurs on macs
		if (resourceLocation.startsWith("file://")) resourceLocation = "file:/" + resourceLocation.substring(7);
		// System.out.println("Mapped structure location: " + resourceLocation);
		// this line has been removed, as it is always overridden by the next line
		// if (resourceLocation.startsWith("file:/")) resourceLocation = FileUtil.removeFilePrefix(resourceLocation);
		if (!resourceLocation.startsWith("platform:/resource")) 
			resourceLocation = FileUtil.resourceLocation(resourceLocation);

		// find or create the chain of subfolders required
    	IFolder wProcFolder = EclipseFileUtil.makeWProcFolders(resourceLocation);
    	// return the handle to a file (which may not yet exist) in the inner folder 
    	IFile proceduresFile = wProcFolder.getFile(EclipseFileUtil.wProcFileName(resourceLocation));
    	return proceduresFile;		
	}
	
	/**
	 * append a line of text, which must not be more than about 1000 characters
	 * (to avoid a thread-jamming issue in this Noddy implementation)
	 * @param text the text to be appended
	 * @param file the file to append it to
	 */
	static public void appendLine(String text, IFile file) throws MapperException
	{
		try
		{
			file.appendContents(textStream(text), true, true, null);
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}		
	}

	/**
	 * 
	 * @param text a text string less than 1000 characters long
	 * @return an InputStream of the text - to create an IFile or append to one
	 * @throws MapperException
	 */
	static public InputStream textStream(String text) throws MapperException
	{
		PipedInputStream in = new PipedInputStream();
		String line = text;
		if (text.length() > 1000)
		{
			System.out.println("Line truncated to 1000 chars: " + text);
			line = text.substring(0,1000);
		}
		try
		{
			PipedOutputStream out = new PipedOutputStream(in);	
			FileUtil.nl(line, out);
			out.close();
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
		return in;
	}

	
	static void trace(String s) {System.out.println(s);}
	
	
	
	
	/**
	 * derive the name of a Wproc file from a full path to a mapping set file.
	 * The name is the same as the mapping set file name, except for the extension
	 * 'wproc'
	 * @param mapperLocation
	 * @return
	 */
	public static String wProcFileName(String mapperLocation)
	{
		String fileName = "";
		StringTokenizer st = new StringTokenizer(FileUtil.wProcLocation(mapperLocation),"/");
		while (st.hasMoreTokens()) fileName = st.nextToken();
		return fileName;
	}

	
	/**
	 * Works only inside Eclipse,  where there is a project.
	 * finds and if necessary creates a set of nested folders in the project workspace
	 * as needed to write a wproc file inside the innermost folder
	 * @param mapperLocation the string to the corresponding mapper file 
	 * @return the innermost folder
	 */
	public static IFolder makeWProcFolders(String mapperLocation) throws MapperException
	{
		IProject theProject = FileUtil.getProject(mapperLocation);
		if ((theProject == null)|((theProject != null) && (!theProject.exists())))
			throw new MapperException
				("Cannot find project for WProc file at " + mapperLocation);

		IFolder transFolder = theProject.getFolder("Translators");
		if ((transFolder == null)|((transFolder != null) && (!transFolder.exists())))
			throw new MapperException
				("There is no 'Translators' folder in project " + theProject.getName());
		IFolder currentFolder = transFolder;

		StringTokenizer st = new StringTokenizer(mapperLocation,"/");
		// start when you have gone beyond the MappingSet folder
		boolean foundMapperFolder = false;
		while (st.hasMoreTokens()) try
		{
			String folderName = st.nextToken();
			// the last token is not a folder; it is the file name
			if ((foundMapperFolder) && (st.hasMoreTokens()))
			{
				// try to find the next folder inside the current folder
				IFolder nextFolder = currentFolder.getFolder(folderName);
				// if it does not exist, create it
				if (!nextFolder.exists()) {nextFolder.create(true,false,null);}
				// get ready for the next nested folder
				currentFolder = nextFolder;
			}
			if (folderName.equals("MappingSets")) foundMapperFolder = true;
		}
		catch (Exception ex) 
			{throw new MapperException("Exception creating folder for wproc file: " + ex.getMessage());}
		return currentFolder;
	}
	
	/**
	 * write the result of an XMLWriter or wrapper transform to an IFile,
	 * so it will be known to Eclipse
	 * @param output output object - XML Element or String[] array
	 * @param theFile the IFile to be written to
	 * @param fileType constant defined in class AbstractMapperWrapper
	 * @throws MapperException
	 */
	public static void writeOutputObject(Object output,IFile theFile, int fileType)
	throws MapperException
	{
		if (fileType == AbstractMapperWrapper.XML_TYPE)
		{
			if (!(output instanceof Document)) 
				throw new MapperException("Output is not a Document but is a " + output.getClass().getName());
			Document outDoc = (Document)output;
			writeOutputResource(outDoc, theFile, true);
		}
		else if (fileType == AbstractMapperWrapper.TEXT_TYPE)
		{
			if (!(output instanceof String[])) 
				throw new MapperException("Output is not a String array is a " + output.getClass().getName());
			String[] text = (String[])output;
			writeOutputText(text, theFile);
		}
	}
	
	
	/**
	 * wrtie out a csv file, expressed as a Vector of String arrays
	 * @param csvRows
	 * @param theFile
	 * @throws MapperException
	 */
	public static void writeCSVFile(Vector<String[]> csvRows, IFile theFile)
	throws MapperException
	{
		try
		{
			// write to a temporary file
	    	String root = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	    	String tempFileLocation = root + TEMPORARY_TEXT_FILE;
	    	FileUtil.writeCSVFile(tempFileLocation, csvRows);
	    	
	    	// read the temporary file into the IFile
			FileInputStream fileStream = new FileInputStream(tempFileLocation);
			// delete any existing IFile before writing this one
			if (theFile.exists()) theFile.delete(true, null);
			theFile.create(fileStream, false, null);			
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
	}

	/**
	 * 
	 * @param text
	 * @param theFile
	 * @throws MapperException
	 */
	public static void writeOutputText(String[] text, IFile theFile)
	throws MapperException
	{
		try
		{
			// write the text array to a temporary file
	    	String root = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	    	String tempFileLocation = root + TEMPORARY_TEXT_FILE;
	    	FileOutputStream fo = new FileOutputStream(tempFileLocation);
	    	for (int i = 0; i < text.length; i++) FileUtil.nl(text[i],fo);
	    	fo.close();	
	    	
	    	// read the temporary file into the IFile
			FileInputStream fileStream = new FileInputStream(tempFileLocation);
			if (theFile.exists()) {theFile.appendContents(fileStream, false, false, null);}
			else {theFile.create(fileStream, false, null);}			
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
	}

    /**
     * write XML to an IFile. This implementation is a kludge which make a temporary 
     * XML file somewhere outside the workspace, and leaves it there.
     * 
     * @param outDoc the XML document to be written
     * @param theFile the IFile, which may exist or may just be a handle. 
     * If it already exists, append the XML to its existing contents
     * @param isFormatted if true, put in line breaks and indenting
     * @throws XMLException if anything goes wrong
     */
    public static void writeOutputResource(Document outDoc, IFile theFile, boolean isFormatted)
    throws MapperException
    {
    	if (outDoc == null) throw new MapperException("Null Document being written to file " + theFile.getName());
    	String tempFileLocation = workspaceRoot() + TEMPORARY_XML_FILE;
    	// System.out.println("Temporary file location: " + tempFileLocation);
    	XMLUtil.writeOutput(outDoc,tempFileLocation,isFormatted);
    	try{
    		FileInputStream fileStream = new FileInputStream(tempFileLocation);
    		if (theFile.exists()) {theFile.appendContents(fileStream, false, false, null);}
    		else {theFile.create(fileStream, false, null);}

    	}
    	catch (Exception ex) {throw new XMLException(ex.getMessage());}
    }
    
    /**
     * 
     * @param outDoc
     * @param resourceLocation
     * @param isFormatted
     * @throws XMLException
     */
    public static void writeOutputResource(Document outDoc, String resourceLocation, boolean isFormatted)
    throws MapperException
    {
        IProject theProject = FileUtil.getProject(resourceLocation);

        // find the path to the folder within the project
        String start = "platform:/resource/" + theProject.getName();
        String folderPath = FileUtil.getFolder(resourceLocation).substring(start.length() + 1);
        folderPath = folderPath.substring(0, folderPath.length()-1); // remove final '/'
        
        // find the file name
        String fileName = FileUtil.getFileName(resourceLocation);
        
        // make the IFile handle and make sure the file does not already exist
        IFolder theFolder = theProject.getFolder(folderPath);
        IFile newFile = theFolder.getFile(fileName);
        try {if (newFile.exists()) newFile.delete(true, null);}
        catch (CoreException ex) {throw new MapperException("Failed to detete file: " + ex.getMessage());}
        
        // write the XML to the IFile
        writeOutputResource(outDoc, newFile,isFormatted);
    }
    
    public static String workspaceRoot()
    {
    	return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();    	
    }

    
    /**
     * write XML to an IFile. This implementation is a kludge which make a temporary 
     * XML file somewhere in the workspace, and leaves it there.
     * 
     * @param outDoc the XML document to be written
     * @param outputFile the IFile, which may exist or may just be a handle. 
     * If it already exists, append the XM to its existing contents
     * @param isFormatted if true, put in line breaks and indenting
     * @throws XMLException if anything goes wrong
     */
    public static void writeTransformedOutputResource(Document outDoc, IFile outputFile, IFile xslFile)
    throws XMLException
    {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    	String tempFileLocation = root.getLocation().toString() + "/mapperTemporaryFile.xml";
    	try{
	    	InputStream xslStream = xslFile.getContents();
	    	XMLUtil.writeTransformedOutput(outDoc,tempFileLocation,xslStream);
    		FileInputStream fileStream = new FileInputStream(tempFileLocation);
    		if (outputFile.exists()) outputFile.appendContents(fileStream, false, false, null);
    		else outputFile.create(fileStream, false, null);
    	}
    	catch (Exception ex) {throw new XMLException(ex.getMessage());}
    }
    
    //-----------------------------------------------------------------------------------------------
    //               Saving and restoring information about the user or the session
    //               (1) the last selected class in the class model view
    //-----------------------------------------------------------------------------------------------

	
	// number of different ECore models whose last selected class can be stored
    private static int MAX_STACK_SIZE = 5;
    
    /**
     * @return the path to the latest selected class, or null 
     * if none is stored for this model
     */
	public static String getSelectedClassPath(String model)
    {
    	String path = null;
    	String[][] classStack = readStack();
    	for (int i = 0; i < MAX_STACK_SIZE; i++)
    		if (classStack[i][0].equals(model)) path = classStack[i][1];
		return path;
    }
	
	
	/**
	 * save the class path for this model, updating the LRU stack of models and class paths
	 * @param model
	 * @param path
	 */
	public static void saveClassPath(String model, String path) throws MapperException
	{
    	String[][] oldClassStack = readStack(); // previous version of stack
		String[][] classStack = new String[MAX_STACK_SIZE][2]; // stack to be stored in file
		for (int i = 0; i < MAX_STACK_SIZE; i++) classStack[i][0] = "";

		// put the model and path at the top of the stack to be stored
		int newStackPos = 0;
		classStack[newStackPos][0] = model;
		classStack[newStackPos][1] = path;

		// update the rest of the stack to be stored
		for (int i = 0; i < MAX_STACK_SIZE; i++)
		{
			// re-store class paths for all models except the one just added or replaced
			if (!oldClassStack[i][0].equals(model))
			{
				newStackPos++;
				if (newStackPos < MAX_STACK_SIZE) // never try to exceed stack size
				{
					classStack[newStackPos][0] = oldClassStack[i][0];
					classStack[newStackPos][1] = oldClassStack[i][1];
				}				
			}			
		}
		
		writeStack(classStack);
	}
	
	private static String[][] readStack()
	{
		String[][] classStack = new String[MAX_STACK_SIZE][2];
		for (int i = 0; i <MAX_STACK_SIZE; i++) classStack[i][0] = "";

		try
		{
			Element classRoot = XMLUtil.getRootElement(getSelectedClassFileLocation());
			Vector<Element> classEls = XMLUtil.namedChildElements(classRoot, "class");
			for (int i = 0; i < classEls.size(); i++) if (i < MAX_STACK_SIZE)
			{
				Element classEl = classEls.get(i);
				classStack[i][0] = classEl.getAttribute("model");
				classStack[i][1] = classEl.getAttribute("path");
			}
		}
		catch (Exception ex) {} 
		return classStack;
	}
	
	/**
	 * make or replace the local file holding the path to the last selected class
	 * in the class model view
	 */
	private static void writeStack(String[][] classStack) throws MapperException
	{
		try{
			Document classDoc = XMLUtil.makeOutDoc();
			Element rootEl = XMLUtil.newElement(classDoc, "SelectedClasses");
			classDoc.appendChild(rootEl);
			for (int i = 0; i < MAX_STACK_SIZE; i++) if (!classStack[i][0].equals(""))
			{
				Element classEl = XMLUtil.newElement(classDoc,"class");
				classEl.setAttribute("model", classStack[i][0]);
				classEl.setAttribute("path", classStack[i][1]);
				rootEl.appendChild(classEl);
			}
			XMLUtil.writeOutput(classDoc, getSelectedClassFileLocation(), true);				
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
	}

	
	/**
	 * @return the location and name of the file containing the path to the last selected classes
	 */
	private static String getSelectedClassFileLocation()
	{
		Bundle  thisPlugin = Platform.getBundle("com.openMap1.mapper");
		IPath path = Platform.getStateLocation(thisPlugin);
		String location = path.toString() + "/selectedClasses.xml";
		return location;		
	}

}
