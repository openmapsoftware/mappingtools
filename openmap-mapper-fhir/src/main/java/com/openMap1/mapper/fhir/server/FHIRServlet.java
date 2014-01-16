package com.openMap1.mapper.fhir.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.hl7.fhir.instance.formats.Composer;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.fhir.EcoreReferenceBridge;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.structures.DBStructure;
import com.openMap1.mapper.userConverters.DBConnect;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;

public class FHIRServlet  extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	// http session is used mainly for caching, to avoid repeating expensive operations
	public HttpSession session() {return session;}
	private HttpSession session;
	
	// name of the server in the current requestcheckResourceName
	public String serverName() {return serverName;}
	private String serverName;
	
	// name of the resource in the current request
	public String resourceName() {return resourceName;}
	private String resourceName;
	
	// true for a simple read like GET [base-url]/Patient/23
	private boolean isSimpleRead;
	
	// the id supplied for a simple read
	private String simpleReadId;
	
	// true for a conformance operation
	private boolean isConformanceOperation;
	
	// http status codes
	private int statusCode;
	public int getStatusCode() {return statusCode;}
	public void setStatusCode(int code) {statusCode = code;}
	
	public void throwError(int status, String message) throws MapperException
	{
		statusCode = status;
		throw new MapperException(message);
	}
	
	/**
	 * @return column headers for the csv file of servers
	 */
	public String[] serverColHeaders() {return serverColHeaders;}
	private String[] serverColHeaders;

	/**
	 * @return column headers for the csv file of resources
	 */
	public String[] resourceColHeaders() {return resourceColHeaders;}
	private String[] resourceColHeaders;
		
	/**
	 * @return column headers for the csv file of searches
	 */
	public String[] searchColHeaders() {return searchColHeaders;}
	private String[] searchColHeaders;
    
    //---------------------------------------------------------------------------------------
	//                       Mechanics of the connection: receiving GETs
    //---------------------------------------------------------------------------------------
    /**
     */
	public void doGet(HttpServletRequest request,
            HttpServletResponse response)
    		throws ServletException, IOException 
    {
		try
		{
			// session is used only to cache things, to avoid repeating expensive operations
			session = request.getSession();	
						
			// default error code
			statusCode = HttpServletResponse.SC_BAD_REQUEST; // 400

			String uri = request.getRequestURI();
			Map<String,String[]> params = request.getParameterMap();

			// parse the URI to get the database connection for the server(s) and mapping set for the resource
			String[] servers = parseURI(uri);
			
			if (isSimpleRead)
			{
				serverName = servers[0];
				processSimpleRead(response);
			}
			
			else if (isConformanceOperation)
			{
				serverName = servers[0];
				processConformanceOperation(response);
			}
			
			else 
			{
				processSearch(servers, params,response);
			}
			
			message("sent FHIR response");

		}
		catch (Exception ex) {ex.printStackTrace();makeError(response, ex.getMessage());}
    }
	
	/**
	 * 
	 * @param uri
	 * @throws MapperException
	 */
	private String[] parseURI(String uri) throws MapperException
	{
		message("parsing URI '" + uri + "'");
		StringTokenizer st = new StringTokenizer(uri,"/");
		// first step (Tomcat folder name) is not checked now
		st.nextToken();
		String step2 = st.nextToken();
		if (!(step2.equals("farm"))) throw new MapperException("Step 2 of URI '" + uri + "' should be 'farm'");
		
		// third step must be a sequence of allowed allowed server names, separated by '+'
		String serverNameList = st.nextToken();
		// fourth step must be an allowed resource name for all the servers, or 'metadata' to get the Conformance for one server
		resourceName = st.nextToken();
		// 'metadata' is a request for the Conformance resource
		if (resourceName.equals("metadata")) resourceName = "Conformance";
		
		// there may be one more step, for a simple read by id
		isSimpleRead = false;
		if (st.hasMoreTokens())
		{
			simpleReadId = st.nextToken();
			isSimpleRead = true;
		}
		

		StringTokenizer serverTokens = new StringTokenizer(serverNameList,"+");
		String[] servers = new String[serverTokens.countTokens()];
		int index = 0;
		while (serverTokens.hasMoreTokens())
		{
			serverName = serverTokens.nextToken();
			servers[index] = serverName;
			checkServerName(serverName);
			getDBStructure(serverName);
			message("Made connection to database for server '" + serverName + "'");
			
			checkResourceName(serverName,resourceName);
			getMappedStructure(serverName, resourceName);
			message("Found mappings for resource '" + resourceName + "'");
			index++;
		}
		if ((servers.length > 1) && (isSimpleRead)) 
			throw new MapperException("Cannot do a simple read on more than one server");
		if ((servers.length > 1) && (isConformanceOperation)) 
			throw new MapperException("Cannot do a conformance operation on more than one server");
		return servers;
	}

	
	/**
	 * 
	 * @param response
	 * @throws Exception
	 */
	private void processSimpleRead(HttpServletResponse response) throws Exception
	{
		FHIRSearchManager manager = new FHIRSearchManager(this);
		EObject resource = manager.getResource(resourceName, simpleReadId);
		sendResource(resource,response,manager,simpleReadId);
		
	}
	
	/**
	 * 
	 * @param response
	 * @throws Exception
	 */
	private void processConformanceOperation (HttpServletResponse response) throws Exception
	{
		resourceName= "Conformance";
		FHIRSearchManager manager = new FHIRSearchManager(this);
		EObject resource = manager.getConformance(serverName);
		sendResource(resource,response,manager,"no id");
	}
	
	/**
	 * 
	 * @param resource
	 * @param response
	 * @throws Exception
	 */
	private void sendResource(EObject resource,HttpServletResponse response, FHIRSearchManager manager, String id) throws Exception
	{
		if (resource != null)
		{
			EPackage classModel = getMappedStructure(serverName, resourceName).getClassModelRoot();
			EcoreReferenceBridge bridge = new EcoreReferenceBridge(classModel);
			Resource refModelResource = bridge.makeReferenceModelResource(resource);
			Narrative narrative  = manager.makeNarrative(resource,resourceName);
			if (narrative != null) refModelResource.setText(narrative);
			sendResourceResponse(response, refModelResource);
		}
		else sendErrorResponse(response, ("Found no " + resourceName + " resource with id " + id), statusCode);
	}
	

	
	/**
	 * 
	 * @param servers
	 * @param params
	 * @throws MapperException
	 */
	private void processSearch(String[] servers, Map<String,String[]> params, HttpServletResponse response) throws Exception
	{
		// make an AtomFeed per server and merge them before sending
		String allServerNames = "";
		Vector<AtomFeed> feeds = new Vector<AtomFeed>();
		for (int s = 0; s < servers.length;s++)
		{
			// convert a FHIR search into an object query 
			serverName = servers[s];
			allServerNames = allServerNames + serverName + " ";
			Vector<String[]> searches = getSearches(serverName, resourceName);
			QueryConverter converter = new QueryConverter(this,params,searches);	
			String objQuery = converter.queryString();
			
			// execute the object query and return an EObject (containing all the resulting resources) for the AtomFeed
			FHIRSearchManager manager = new FHIRSearchManager(this);
			EObject result = manager.getFHIRSearchResult(objQuery);
			
			// convert the result into an instance of the FHIR reference model
			EPackage classModel = getMappedStructure(serverName, resourceName).getClassModelRoot();
			EcoreReferenceBridge bridge = new EcoreReferenceBridge(classModel);
			AtomFeed feed = bridge.getReferenceModelFeed(result);
			feed.setTitle(serverName);
			
			// set the narratives of all the resources in the feed
			for (Iterator<AtomEntry<?>> it = feed.getEntryList().iterator();it.hasNext();)
			{
				AtomEntry<?> entry = it.next();
				Resource resource = entry.getResource();
				Narrative narrative  = manager.getNarrative(entry.getId());
				if (narrative != null) resource.setText(narrative);
			}
			feeds.add(feed);
		}
		
		// send the response
		AtomFeed merged = mergeFeeds(feeds);
		merged.setTitle("Search " + resourceName + " in " + allServerNames);
		sendAtomFeedResponse(response,merged);
	}
	
	/**
	 * send a message back to the client, with some status code such as 400
	 * @param response
	 * @param errorMess
	 */
	private void makeError(HttpServletResponse response, String errorMess)
	{
		message("Error on server: " + errorMess);
		try {sendErrorResponse(response,errorMess,statusCode);}
		catch (Exception ey) {message("Exception handling exception: " + ey.getMessage());}
	}
	
	
	/**
	 * 
	 * @param serverName
	 * @throws MapperException
	 */
	private void checkServerName(String serverName) throws MapperException
	{
		String[] details = getServerDetails(serverName);
		if (details == null) throw new MapperException("Server '" + serverName + "' does not exist");
	}
	
	/**
	 * 
	 * @param serverName
	 * @param resourceName
	 * @throws MapperException
	 */
	private void checkResourceName(String serverName,String resourceName) throws MapperException
	{
		isConformanceOperation = (resourceName.equals("Conformance"));
		String[] details = getResourceDetails(serverName,resourceName);
		if (details == null) throw new MapperException("Resource '" + resourceName + "' of server '" + serverName + "' does not exist");
	}
	
	/**
	 * merge the entries in a set of AtomFeeds
	 * @param feeds
	 * @return
	 */
	private AtomFeed mergeFeeds(Vector<AtomFeed> feeds)
	{
		AtomFeed result = null;
		if (feeds.size() > 0)
		{
			result = feeds.get(0);
			for (int f = 1; f < feeds.size();f++)
				for (Iterator<AtomEntry<?>> it = feeds.get(f).getEntryList().iterator();it.hasNext();)
					result.getEntryList().add(it.next());				
		}
		return result;
	}
	
	// ----------------------------------------------------------------------------------------------------
	//             session-cached access to csv rows defining servers, resources, and searches
	// ----------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param serverName
	 * @return a row of the server csv file, with elements [server name, database connect string,  user name, password]
	 */
	public String[] getServerDetails(String serverName)  throws MapperException
	{
		@SuppressWarnings("unchecked")
		Hashtable<String,String[]> allServers = (Hashtable<String,String[]>)session.getAttribute("servers");
		if (allServers == null)
		{
			allServers = readServerCSVFile();
			session.setAttribute("servers", allServers);
		}
		return (allServers.get(serverName));
	}
	
	/**
	 * 
	 * @return
	 */
	private Hashtable<String,String[]> readServerCSVFile()  throws MapperException
	{
		Hashtable<String,String[]> allServers = new Hashtable<String,String[]>();
		String fileLocation = session.getServletContext().getRealPath("serviceDefs/servers.csv");
		message("Reading server file at '" + fileLocation + "'");
		Vector<String[]> csvRows = FileUtil.getCSVRows(fileLocation);
		serverColHeaders = csvRows.get(0);
		for (int i = 1; i < csvRows.size(); i++)
		{
			String[] row = csvRows.get(i);
			String serverName = row[0];
			allServers.put(serverName, row);
		}
		return allServers;
	}

	
	/**
	 * @param serverName
	 * @return resource details for all resources defined for the server
	 * or null if the server does not exist
	 */
	public Hashtable<String,String[]> getResourceDetailsForServer(String serverName)  throws MapperException
	{
		@SuppressWarnings("unchecked")
		Hashtable<String,Hashtable<String,String[]>> allResources = 
			(Hashtable<String,Hashtable<String,String[]>>)session.getAttribute("resources");
		if (allResources == null)
		{
			allResources = readResourceCSVFile();
			session.setAttribute("resources", allResources);
		}		
		return allResources.get(serverName);
	}
	
	/**
	 * 
	 * @param serverName
	 * @param resourceName
	 * @return a row of the server csv file, with elements [server name, resource name, mapping folder, mapping set]
	 * or null if the server does no exist, or the resource does not exist
	 */
	private String[] getResourceDetails(String serverName, String resourceName) throws MapperException
	{
		String[] resourceDetails = null;
		Hashtable<String,String[]> resourcesForServer = getResourceDetailsForServer(serverName);
		if (resourcesForServer != null) resourceDetails = resourcesForServer.get(resourceName);
		return resourceDetails;
	}
	
	
	/**
	 * 
	 * @param serverName
	 * @param resourceName
	 * @return the root element of the narrative template for the resource
	 * @throws MapperException
	 */
	public Element getNarrativeTemplate(String serverName, String resourceName) throws MapperException
	{
		return getNarrativeTemplatesForServer(serverName).get(resourceName);
	}
	
	
	/**
	 * 
	 * @param serverName
	 * @return
	 */
	private Hashtable<String,Element> getNarrativeTemplatesForServer(String serverName) throws MapperException
	{
		@SuppressWarnings("unchecked")
		Hashtable<String,Hashtable<String,Element>> allNarrativeTemplates = 
				(Hashtable<String,Hashtable<String,Element>>)session.getAttribute("narrativeTemplates");
		if (allNarrativeTemplates == null) allNarrativeTemplates = new Hashtable<String,Hashtable<String,Element>>();
		Hashtable<String,Element> templatesForServer = allNarrativeTemplates.get(serverName);
		if (templatesForServer == null)
		{
			templatesForServer = new Hashtable<String,Element>();
			Hashtable<String,String[]> resourceDetails = getResourceDetailsForServer(serverName);
			for (Enumeration<String> en = resourceDetails.keys();en.hasMoreElements();)
			{
				String resourceName = en.nextElement();
				String[] details = resourceDetails.get(resourceName);
				String narrativeFileName = details[4];
				if ((narrativeFileName != null) && (!narrativeFileName.equals(""))) try
				{
					String fileLocation = session.getServletContext().getRealPath("narratives/" + narrativeFileName);
					message("Reading narrative template file at '" + fileLocation + "'");
					Element root = XMLUtil.getRootElement(fileLocation);
					templatesForServer.put(resourceName, root);
				}
				catch (Exception ex) {message("failed to read template file");}
			}
			
			allNarrativeTemplates.put(serverName,templatesForServer);
			session.setAttribute("narrativeTemplates",allNarrativeTemplates);
		}
		return templatesForServer;
	}
	
	/**
	 * 
	 * @param serverName
	 * @param resourceName
	 * @return
	 * @throws MapperException
	 */
	public MappedStructure getMappedStructure(String serverName, String resourceName) throws MapperException
	{
		return getReader(serverName,resourceName).ms();
	}
	
	/**
	 * cached access to the MDLXOReader for a resource in a server
	 * the MDLXOReader, rather than the MappedStrcuture, is cached because it is more epxensive to make,
	 * and the MappedStructure can be derived from it.
	 * @param serverName
	 * @param resourceName
	 * @return
	 */
	public MDLXOReader getReader(String serverName, String resourceName) throws MapperException
	{
		String[] resourceDetails = getResourceDetails(serverName,resourceName);
		if (resourceDetails == null) throw new MapperException("No resource '" + resourceName + "' in server '" + serverName + "'");
		MDLXOReader reader = null;
		
		@SuppressWarnings("unchecked")
		Hashtable<String,Hashtable<String,MDLXOReader>> allReaders = 
			(Hashtable<String,Hashtable<String,MDLXOReader>>)session.getAttribute("readers");
		if (allReaders == null) allReaders = new Hashtable<String,Hashtable<String,MDLXOReader>>();
		
		Hashtable<String,MDLXOReader> readersForServer = allReaders.get(serverName);
		if (readersForServer == null) readersForServer = new Hashtable<String,MDLXOReader>();
		
		reader = readersForServer.get(resourceName);
		
		if (reader == null) try
		{
			String mappingProjectFolder = resourceDetails[2];
			String mappingSetName = resourceDetails[3];
			String path = session.getServletContext().getRealPath("mappings/" + mappingProjectFolder + "/MappingSets/" + mappingSetName);
			message("Reading mapping set at '" + path + "'");
			MappedStructure mappingSet = FileUtil.getMappedStructure(path);
			EPackage classModel = mappingSet.getClassModelRoot();
			// null = XML root (can be set later); null = messageChannel
			reader = new MDLXOReader(null,mappingSet,classModel,null);
			
			readersForServer.put(resourceName, reader);
			allReaders.put(serverName, readersForServer);
			session.setAttribute("readers", allReaders);			
		}
		catch (IOException ex) {throw new MapperException(ex.getMessage());}
		
		return reader;
	}
	
	/**
	 * 
	 * @return
	 */
	private Hashtable<String,Hashtable<String,String[]>> readResourceCSVFile()  throws MapperException
	{
		Hashtable<String,Hashtable<String,String[]>> allResources = new Hashtable<String,Hashtable<String,String[]>>();
		String fileLocation = session.getServletContext().getRealPath("serviceDefs/resources.csv");
		message("Reading resource file at '" + fileLocation + "'");
		Vector<String[]> csvRows = FileUtil.getCSVRows(fileLocation);
		resourceColHeaders = csvRows.get(0);
		for (int i = 1; i < csvRows.size(); i++)
		{
			String[] row = csvRows.get(i);
			String serverName = row[0];
			String resourceName = row[1];
			Hashtable<String,String[]> resourcesForServer = allResources.get(serverName);
			if (resourcesForServer == null) resourcesForServer = new Hashtable<String,String[]>();
			resourcesForServer.put(resourceName, row);
			allResources.put(serverName, resourcesForServer);
		}
		return allResources;
	}
	
	/**
	 * 
	 * @param serverName
	 * @param resourceName
	 * @return
	 * @throws MapperException
	 */
	public Vector<String[]> getSearches(String serverName,String resourceName) throws MapperException
	{
		Vector<String[]> searches = null;
		@SuppressWarnings("unchecked")
		Hashtable<String,Hashtable<String,Vector<String[]>>> allSearches = 
				(Hashtable<String,Hashtable<String,Vector<String[]>>>)session.getAttribute("searches");
		if (allSearches == null)
		{
			allSearches = readSearchesCSVFile();
			session.setAttribute("searches", allSearches);
		}
		
		Hashtable<String,Vector<String[]>> searchesForServer = allSearches.get(serverName);
		if (searchesForServer != null) searches = searchesForServer.get(resourceName);
		
		return searches;
	}
	
	/**
	 * 
	 * @return
	 * @throws MapperException
	 */
	private Hashtable<String,Hashtable<String,Vector<String[]>>> readSearchesCSVFile() throws MapperException
	{
		Hashtable<String,Hashtable<String,Vector<String[]>>> allSearches = new Hashtable<String,Hashtable<String,Vector<String[]>>>();
		String fileLocation = session.getServletContext().getRealPath("serviceDefs/searches.csv");
		Vector<String[]> csvRows = FileUtil.getCSVRows(fileLocation);
		searchColHeaders = csvRows.get(0);
		for (int i = 1; i < csvRows.size(); i++)
		{
			String[] row = csvRows.get(i);
			String serverName = row[0];
			String resourceName = row[1];
			Hashtable<String,Vector<String[]>> searchesForServer = allSearches.get(serverName);
			if (searchesForServer == null) searchesForServer = new Hashtable<String,Vector<String[]>>();
			Vector<String[]> searchesForResource = searchesForServer.get(resourceName);
			if (searchesForResource == null) searchesForResource = new Vector<String[]>();
			searchesForResource.add(row);
			searchesForServer.put(resourceName, searchesForResource);
			allSearches.put(serverName, searchesForServer);
		}
		return allSearches;
	}

	
	
	// ----------------------------------------------------------------------------------------------------
	//                      session-cached access to DBStructures (which provide database connections)
	// ----------------------------------------------------------------------------------------------------
	
	/**
	 * cached access to server database structures (which give connections)
	 * @param serverName
	 * @return
	 * @throws MapperException
	 */
	public DBStructure getDBStructure(String serverName) throws MapperException
	{
		DBStructure structure = null;
		Connection con = null;
		String[] serverDetails = getServerDetails(serverName);
		if (serverDetails == null) throw new MapperException("No server '" + serverName + "'");
		
		@SuppressWarnings("unchecked")
		Hashtable<String,DBStructure> structures = (Hashtable<String,DBStructure>)session.getAttribute("dbStructures");
		if (structures == null) structures = new Hashtable<String,DBStructure>();
		
		structure = structures.get(serverName);
		if (structure == null)
		{
			String url = serverDetails[1];
			// convention in the server table to denote a database using embedded Derby
			if (url.startsWith("embedded")) url = makeEmbeddedURL(url);
			
			String userName = serverDetails[2];
			String password = serverDetails[3];
			String schema = serverDetails[4];
			
			DBConnect connector = new DBConnect(url, userName, password, schema);
			try
			{
				if (connector.connect())
				{
					con = connector.con();
					// this looks up database metadata
					structure = new DBStructure(con);
					structures.put(serverName, structure);
					session.setAttribute("dbStructures",structures);
				}
			}
			catch (Exception ex) {ex.printStackTrace();throw new MapperException(ex.getMessage());}
		}
		
		return structure;
	}
	
	/**
	 * the convention in the server table for denoting an embedded Derby database in folder <DBFolder>
	 * inside the Tomcat FHIR folder is 'embedded/<DBFolder>', 
	 * @param url
	 * @return the jdbc connect String for embedded Derby to connect to the database
	 */
	private  String makeEmbeddedURL(String url)
	{
		String shortURL = url.substring("embedded/".length());
		String result = "jdbc:derby:" + session.getServletContext().getRealPath(shortURL);
		return result;
	}
	
	// ----------------------------------------------------------------------------------------------------
	//                                   responses from the server
	// ----------------------------------------------------------------------------------------------------

	
	/**
	 * @param statusCode
	 * @param response
	 * @param responseDoc
	 * @throws IOException
	 * @throws MapperException
	 */
	private void sendXMLResponse(HttpServletResponse response, Document responseDoc, int statusCode) throws IOException, MapperException
	{
		// status code zero is reinterpreted as 'OK' (200)
		if (statusCode > 0) response.setStatus(statusCode);
		else if (statusCode == 0) response.setStatus(HttpServletResponse.SC_OK);

		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("text/xml");			
        ServletOutputStream so = response.getOutputStream();
        XMLUtil.writeToStream(responseDoc, so, true);
	}
	
	/**
	 * send the contents of an AtomFeed as an XML response to the client
	 * @param response
	 * @param feed
	 * @throws Exception
	 */
	private void sendAtomFeedResponse(HttpServletResponse response,AtomFeed feed) throws Exception
	{
		response.setContentType("text/xml");			
        ServletOutputStream so = response.getOutputStream();
		Composer composer = new XmlComposer();
		composer.compose(so, feed, true);	// true = pretty				
	}
	
	/**
	 * send a single resource as an XML response to the client
	 * @param response
	 * @param resource
	 * @throws Exception
	 */
	private void sendResourceResponse(HttpServletResponse response, Resource resource) throws Exception
	{
		response.setContentType("text/xml");			
        ServletOutputStream so = response.getOutputStream();
		Composer composer = new XmlComposer();
		composer.compose(so, resource, true);	// true = pretty				
	}
	
	/**
	 * 
	 * @param response
	 * @param text
	 * @param statusCode
	 * @throws IOException
	 * @throws MapperException
	 */
	private void sendTextResponse(HttpServletResponse response, String text, int statusCode) throws IOException, MapperException
	{
		// status code zero is reinterpreted as 'OK' (200)
		if (statusCode > 0) response.setStatus(statusCode);
		else if (statusCode == 0) response.setStatus(HttpServletResponse.SC_OK);

		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
        out.println(text);
	}

	
	/**
	 * 
	 * @param response
	 * @param text
	 * @param statusCode
	 * @throws IOException
	 * @throws MapperException
	 */
	private void sendErrorResponse(HttpServletResponse response, String text, int statusCode) throws IOException, MapperException
	{
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("text/xml");
		message("Sending error '" + text + "' with status code " + statusCode);
        response.sendError(statusCode, text);
        message("sent");
	}


	
	// ----------------------------------------------------------------------------------------------------
	//                                        odds & sods
	// ----------------------------------------------------------------------------------------------------
	


	
	protected void message(String s) 
	{
		System.out.println(s);
		session.getServletContext().log(s);
	}


}
