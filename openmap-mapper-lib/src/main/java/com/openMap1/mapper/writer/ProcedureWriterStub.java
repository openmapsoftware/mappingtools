package com.openMap1.mapper.writer;

import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.resources.IFile;

import com.openMap1.mapper.core.CompilationIssue;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.SystemMessageChannel;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.SOAPClient;
import com.openMap1.mapper.MappedStructure;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;

/**
 * stub class which compiles a wproc procedure by invoking the 
 * translation compiler web service
 * @author robert
 *
 */

public class ProcedureWriterStub implements ProcedureCompiler{
	
	private MappedStructure mappedStructure;
	private messageChannel mChan;
	
	Hashtable<String,List<CompilationIssue>> compilationIssues 
		= new Hashtable<String,List<CompilationIssue>>();
	
	private boolean tracing = false;
	

    public ProcedureWriterStub(MappedStructure mappedStructure, messageChannel mChan) 
    throws MapperException
    {
    	this.mappedStructure = mappedStructure;
    	// a message channel can do nothing in the stub, as the messages are written on the server
    	this.mChan = mChan;
    	if (this.mChan != null) this.mChan.close(); // to satisfy the compiler
    }

    //----------------------------------------------------------------------------------------------------
    //               Compiling a wproc file - either directly, or using the compiler web service 
    //----------------------------------------------------------------------------------------------------
    
    /*
     * One of the two versions of method
     * public Element generateProcedures(boolean codeTrace) 
     * must be made inactive:
     * 
     * (1) to deploy the tools directly calling class ProcedureWriter:
     *  - a copy of class ProcedureWriter must be in this package com.openMap1.mapper.writer
     *  - the first (small) version of method generateProcedures must be active
     *  - the second (long) version must be commented out, or harmlessly renamed (preferred).
     *  
     * (2) to deploy the tools using the compiler web service:
     *  - there should be no copy of class ProcedureWriter in this package com.openMap1.mapper.writer
     *  - the first (small) version of method generateProcedures is commented out (to avoid compiler errors)
     *  - the second (long) version is active.
     */

	/**
	 * direct version  - comment out, and reactivate the other version to use the compiler web server
	 */
    public Element generateProcedures(boolean codeTrace) 
		    throws MapperException
		    {
				trace("starting direct creation wproc file, without using compiler web service");				
				ProcedureWriter procWriter = new ProcedureWriter(mappedStructure,new SystemMessageChannel());
				return procWriter.generateProcedures(codeTrace);
		    }
    
    
	/**
	 * Generate XML writing procedures from a mapper file 
	 * (supplied in the constructor), only if necessary
	 * (i.e if the mapper file has been edited since the last generate)
	 * @param codeTrace if true, write out a code trace file (on the server)
	 * @return the root Element of the procedures file
	 * @throws MapperException
	 */
	public Element generateProceduresX(boolean codeTrace) 
    throws MapperException
    {
		trace("checking for existing wproc file");
		/* if the mapper file has not been edited since the WProc procedures file was made,
		 * just return the root element of the existing procedures file */
		if (!(mappedStructure.hasChangedSinceCompile()))
		{
			trace("Existing wproc file found");
			return mappedStructure.procedureFileRoot();			
		}
		
		trace("composing SOAP body");
		/* the three XML subtrees in the SOAP  body are:
		 * (1) the header, giving email address and key 
		 * (2) the mapping set XML and 
		 * (3) the class model XML */
		Element[] request = new Element[3];
		
		/* get a request header with an email and key, 
		 * either by finding them in a file in the plugin storage area,
		 * or by getting an email address from the user 
		 * (to send with an empty key from the server) */
		request[0] = SOAPClient.getEmailAndKey();
		if (request[0] == null) throw new MapperException("Translation compile request not completed");

		// find the location of the mapping set file on the file system
		URI mappingSetURI = mappedStructure.eResource().getURI();
		String mappingSetlocation = FileUtil.editURIConverter().normalize(mappingSetURI).toString();
		mappingSetlocation = FileUtil.removeFilePrefix(mappingSetlocation); // strip off the prefix 'file:/'

		// read the mapping file as XML and find its root
		trace("Reading mapping set");
		Element mappingRoot = XMLUtil.readXMLFile(mappingSetlocation);
		request[1] = mappingRoot;

		// find the location of the class Model file on the file system
		trace("finding class model");
		URI classModelURI = mappedStructure.getClassModelRoot().eResource().getURI();
		String location = FileUtil.editURIConverter().normalize(classModelURI).toString();
		location = FileUtil.removeFilePrefix(location); // strip off the prefix 'file:/'

		// read the class model file as XML and find its root
		Element classModelRoot = XMLUtil.readXMLFile(location);
		request[2] = classModelRoot;

		// put the three elements in a SOAP request, and get the returned XML
		trace("sending SOAP compile request");
		SOAPClient soapClient = new SOAPClient();
		Element[] reply = soapClient.getReply(SOAPClient.COMPILE_REQUEST, request);

		trace("handling compiler reply");
		Element envelope = reply[0];
		if (envelope.getTagName().equals("Envelope"))
		{
			Element headerEl = XMLUtil.firstNamedChild(envelope, "replyHeader");
			if (headerEl == null) throw new MapperException("No header on reply from compiler server");
			int replyType = new Integer(headerEl.getAttribute("replyType")).intValue();
			
			// if the server has sent back a new translation service key, store it
			String newKey = headerEl.getAttribute("newKey");
			if (!newKey.equals("")) SOAPClient.makeKeyFile(SOAPClient.getStoredEmail(), newKey);
			
			// error arose when compiling; throw an exception to display it
			if (replyType == SOAPClient.SHOW_ERROR_MESSAGE)
				throw new MapperException("Failed to compile " + mappedStructure.getMappingSetName() + ": "
						+ headerEl.getAttribute("replyText"));
						
			// successful compilation
			else if (replyType == SOAPClient.USE_SUCCESSFUL_COMPILE)
			{
				trace("successful compile");
				Element procFileRoot = XMLUtil.firstNamedChild(envelope, "procedures");
				if (procFileRoot != null)
				{
					readCompilationIssues(procFileRoot);
					return procFileRoot;					
				}
				else throw new MapperException("No procedures file returned");				
			}			
			else throw new MapperException("Reply type " + replyType + " not recognised");
		}
		else throw new MapperException("Reply element is not 'envelope' but : " + envelope.getTagName());
    }
	
	
	/**
	 * Generate XML writing procedures from a mapper file 
	 * (supplied in the constructor).
	 * If the mapper file has not changed since the last generate, the call
	 * generateProcedures(codeTrace) will take the lazy option, but we still make 
	 * a new IFile from it (might not be in the usual place)
	 * @param proceduresFile the IFile to write the result out to
	 * @param codeTrace if true, write out a code trace file (on the server)
	 * @throws MapperException
	 */
	public void generateProcedures(IFile proceduresFile, boolean codeTrace) 
    throws MapperException
    {
		/* find the root of the procedures file, using the other API in the interface */
		Element procFileRoot = generateProcedures(codeTrace);
				
		// write the document out to the supplied IFile handle
		if (procFileRoot != null)
		try{
			// if there is already a procedures file in the handle, delete it 
			if (proceduresFile.exists()) proceduresFile.delete(true, null);

			Document doc = XMLUtil.makeOutDoc();
			Node newRoot = doc.importNode(procFileRoot, true); // true = deep copy
			doc.appendChild(newRoot);
			// false = not formatted; otherwise you just keep adding more white space to the same files
			EclipseFileUtil.writeOutputResource(doc, proceduresFile, false); 		
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}

		else if (procFileRoot == null)
			throw new MapperException("No Procedures file handle found");
    }


	/**
	 * Compilation issues, noted on the compilation server, have been attached to nodes
	 * in the wproc file returned. Read them from the XML and store them 
	 * @param procFileRoot
	 */
	private void readCompilationIssues(Element procFileRoot)
	throws MapperException
	{
		String mappingSetName = FileUtil.getFileName(mappedStructure.eResource().getURI().toString());
		compilationIssues = new Hashtable<String,List<CompilationIssue>>();
		addCompilationIssues(procFileRoot,compilationIssues,mappingSetName);
	}
	
	// recursive descent of the wproc XML structure, collecting compilation issues
	private void addCompilationIssues(Element el, 
			Hashtable<String,List<CompilationIssue>>compilationIssues, String mappingSetName)
	throws MapperException
	{
		// collect all compilation issues on this node
		for (Iterator<Element> it = XMLUtil.namedChildElements(el, "CompilationIssue").iterator();it.hasNext();)
		{
			CompilationIssue ci = new CompilationIssue(it.next());
			ci.setFileName(mappingSetName);
			String path = ci.pathString();
			List<CompilationIssue> lc = compilationIssues.get(path);
			if (lc == null) lc = new Vector<CompilationIssue>();
			lc.add(ci);
			compilationIssues.put(path, lc);
		}

		// go down through all descendant 'element' nodes, collecting issues
		for (Iterator<Element> it = XMLUtil.namedChildElements(el, "element").iterator();it.hasNext();)
			addCompilationIssues(it.next(),compilationIssues,mappingSetName);
	}


	
    /** Code generation warnings, indexed by XPath to the node involved  */
    public Hashtable<String,List<CompilationIssue>> getCompilationIssues()
    {
    	return compilationIssues;
    }
    
    private void trace(String s) {if (tracing) System.out.println(s);}

}
