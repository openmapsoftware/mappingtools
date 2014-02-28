package com.openMap1.mapper.fhir.server;

import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.fhir.EcoreReferenceBridge;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.structures.DBStructure;
import com.openMap1.mapper.userConverters.DBConnect;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;

@Service
public class OpenMapsFhirService implements FhirService {

	@Autowired
	private ResourceLoader resourceLoader;

	private static enum DBTypes {
		RDBMS, XML
	};

	private String[] serverInformationKeys; // The headers in the servers.csv
											// file
	Map<String, String[]> serverInformation; // Key is server name
	Map<String, DBStructure> serverStructures;
	Map<String, Element> serverXmlDoms;

	String[] resourceInformationKeys;
	Map<String, Map<String, String[]>> resourceInformation;
	Map<String, Map<String, MDLXOReader>> serverResourceReaders;

	String[] searchInformationKeys;
	Map<String, Map<String, Vector<String[]>>> searchInformation;

	Map<String, Map<String, Element>> narrativeInformation;

	@Autowired
	public OpenMapsFhirService(ResourceLoader resourceLoader) throws MapperException, IOException {
		this.resourceLoader = resourceLoader;
		initServerList();
	}

	public void initServerList() throws MapperException, IOException {
		// Load server information
		serverInformation = new Hashtable<String, String[]>();
		org.springframework.core.io.Resource r = resourceLoader
				.getResource("classpath:serviceDefs/servers.csv");
		
		Vector<String[]> csvRows = FileUtil.getCSVRows(r.getFile()
				.getAbsolutePath());
		serverInformationKeys = csvRows.get(0);
		for (int i = 1; i < csvRows.size(); i++) {
			String[] row = csvRows.get(i);
			String serverName = row[0];
			serverInformation.put(serverName, row);
		}
		// Load resource information
		initResources();
		// Load search information
		initSearches();
		// Load narrative information
		initNarratives();

		// Establish RDBMS and XML Servers?
		serverStructures = new HashMap<String, DBStructure>();
		serverXmlDoms = new HashMap<String, Element>();
		for (String serverName : serverInformation.keySet()) {
			DBTypes serverType = DBTypes.valueOf(getServerParameter(serverName,
					"type"));
			if (serverType.equals(DBTypes.RDBMS)) {
				DBStructure structure = buildDBStruct(serverName);
				serverStructures.put(serverName, structure);
			} else if (serverType.equals(DBTypes.XML)) {
				Element domRoot = buildDOM(serverName);
				serverXmlDoms.put(serverName, domRoot);
			}
		}
		// Establish Server Resource Readers?
		serverResourceReaders = new HashMap<String, Map<String, MDLXOReader>>();
		for (String serverName : serverInformation.keySet()) {
			Map<String, MDLXOReader> readersForServerResources = new Hashtable<String, MDLXOReader>();
			// Get all resources for server
			Map<String, String[]> resourceInfoForServer = resourceInformation
					.get(serverName);
			if (resourceInfoForServer == null) {
				continue;
			}
			for (String resourceName : resourceInfoForServer.keySet()) {
				// Build reader for resource
				String[] resourceDetails = getResourceDetails(serverName,
						resourceName);
				try {
					String mappingProjectFolder = resourceDetails[2];
					String mappingSetName = resourceDetails[3];
					org.springframework.core.io.Resource mappingResource = resourceLoader
							.getResource("classpath:" + "mappings/"
									+ mappingProjectFolder + "/MappingSets/"
									+ mappingSetName);
					String path = mappingResource.getFile().getAbsolutePath();
					MappedStructure mappingSet = FileUtil
							.getMappedStructure(path);
					EPackage classModel = mappingSet.getClassModelRoot();
					// null = XML root (can be set later); null = messageChannel
					MDLXOReader reader = new MDLXOReader(null, mappingSet,
							classModel, null);

					readersForServerResources.put(resourceName, reader);
				} catch (IOException ex) {
					throw new MapperException(ex.getMessage());
				}
			}
			serverResourceReaders.put(serverName, readersForServerResources);
		}
	}

	private void initResources() throws MapperException, IOException {
		resourceInformation = new HashMap<String, Map<String, String[]>>();
		org.springframework.core.io.Resource serverResource = resourceLoader
				.getResource("classpath:serviceDefs/resources.csv");
		Vector<String[]> csvRows = FileUtil.getCSVRows(serverResource.getFile()
				.getAbsolutePath());
		resourceInformationKeys = csvRows.get(0);
		for (int i = 1; i < csvRows.size(); i++) {
			String[] row = csvRows.get(i);
			String serverName = row[0];
			String resourceName = row[1];
			Map<String, String[]> resourcesForServer = resourceInformation
					.get(serverName);
			if (resourcesForServer == null) {
				resourcesForServer = new Hashtable<String, String[]>();
			}
			resourcesForServer.put(resourceName, row);
			resourceInformation.put(serverName, resourcesForServer);
		}
	}

	private String[] getResourceDetails(String serverName, String resourceName)
			throws MapperException {
		Map<String, String[]> resources = resourceInformation.get(serverName);
		return resources.get(resourceName);
	}

	private void initSearches() throws MapperException, IOException {
		searchInformation = new HashMap<String, Map<String, Vector<String[]>>>();
		org.springframework.core.io.Resource r = resourceLoader
				.getResource("classpath:serviceDefs/searches.csv");
		Vector<String[]> csvRows = FileUtil.getCSVRows(r.getFile()
				.getAbsolutePath());
		searchInformationKeys = csvRows.get(0);
		for (int i = 1; i < csvRows.size(); i++) {
			String[] row = csvRows.get(i);
			String serverName = row[0];
			String resourceName = row[1];
			Map<String, Vector<String[]>> searchesForServer = searchInformation
					.get(serverName);
			if (searchesForServer == null) {
				searchesForServer = new Hashtable<String, Vector<String[]>>();
			}
			Vector<String[]> searchesForResource = searchesForServer
					.get(resourceName);
			if (searchesForResource == null) {
				searchesForResource = new Vector<String[]>();
			}
			searchesForResource.add(row);
			searchesForServer.put(resourceName, searchesForResource);
			searchInformation.put(serverName, searchesForServer);
		}
	}

	public Vector<String[]> getSearches(String serverName, String resourceName)
			throws MapperException {
		Vector<String[]> result = null;
		Map<String, Vector<String[]>> searchesForServer = searchInformation
				.get(serverName);
		if (searchesForServer != null) {
			result = searchesForServer.get(resourceName);
		}

		return result;
	}

	private void initNarratives() throws MapperException, IOException {
		narrativeInformation = new HashMap<String, Map<String, Element>>();
		for (String serverName : serverInformation.keySet()) {
			Map<String, Element> templatesForServer = new HashMap<String, Element>();
			Map<String, String[]> resourceDetails;
			try {
				resourceDetails = getResourceDetailsForServer(serverName);
			} catch (MapperException e) {
				// TODO Handle the case no resources exist
				continue;
			}
			for (String resourceName : resourceDetails.keySet()) {
				String[] details = resourceDetails.get(resourceName);
				String narrativeFileName = details[4];
				if ((narrativeFileName != null)
						&& (!narrativeFileName.equals(""))) {
						org.springframework.core.io.Resource r = resourceLoader
								.getResource("classpath:narratives/" + narrativeFileName);
						Element root = XMLUtil.getRootElement(r.getFile().getAbsolutePath());
						templatesForServer.put(resourceName, root);
				}
			}
			narrativeInformation.put(serverName, templatesForServer);
		}
	}

	private Element buildDOM(String serverName) throws IOException,
			MapperException {
		// server parameter 'url' specifies file location in the web service
		// folder (such as 'FHIR_a') in the Tomcat webapps folder
		org.springframework.core.io.Resource r = resourceLoader
				.getResource("classpath:"
						+ getServerParameter(serverName, "url"));
		String fileLocation = r.getFile().getAbsolutePath();
		return XMLUtil.getRootElement(fileLocation);
	}

	private DBStructure buildDBStruct(String serverName) throws MapperException {
		String serverUrl = getServerParameter(serverName, "url");
		if (serverUrl.startsWith("embedded")) {
			serverUrl = makeEmbeddedJDBCUrl(serverUrl);
		}
		String serverUsername = getServerParameter(serverName, "username");
		String serverPassword = getServerParameter(serverName, "password");
		String serverSchema = getServerParameter(serverName, "schema");
		DBConnect connector = new DBConnect(serverUrl, serverUsername,
				serverPassword, serverSchema);
		DBStructure structure = null;
		try {
			if (connector.connect()) {
				Connection con = connector.con();
				// this looks up database metadata
				structure = new DBStructure(con);
			}
		} catch (Exception ex) {
			throw new MapperException(ex.getMessage());
		}
		return structure;
	}

	/**
	 * the convention in the server table for denoting an embedded Derby
	 * database in folder <DBFolder> inside the Tomcat FHIR folder is
	 * 'embedded/<DBFolder>',
	 * 
	 * @param url
	 * @return the jdbc connect String for embedded Derby to connect to the
	 *         database
	 */
	private String makeEmbeddedJDBCUrl(String url) {
		String shortUrl = url.substring("embedded/".length());
		String result = "jdbc:derby:classpath:" + shortUrl;
		return result;
	}

	public int getNumberOfServers() {
		return serverInformation.size();
	}

	public String getServerParameter(String serverName, String parameter)
			throws MapperException {
		for (int c = 0; c < serverInformationKeys.length; c++) {
			if (serverInformationKeys[c].equals(parameter)) {
				return serverInformation.get(serverName)[c];
			}
		}
		throw new MapperException("No server parameter " + parameter);
	}

	public String[] getResourceInformationKeys() {
		return resourceInformationKeys;
	}
	
	public Element getNarrativeTemplate(String serverName, String resourceName) throws MapperException
	{
		Element result = null;
		try {
			result = narrativeInformation.get(serverName).get(resourceName);
		} catch (NullPointerException e) {
			throw new MapperException("");
		}
		return result;
	}

	public Map<String, String[]> getResourceDetailsForServer(String serverName)
			throws MapperException {
		Map<String, String[]> result = resourceInformation.get(serverName);
		if (result == null) {
			throw new MapperException(null);
		}
		return result;
	}

	public String[] getServerDetails(String serverName) throws MapperException {
		String[] serverDetails = serverInformation.get(serverName);
		if (serverDetails == null) {
			throw new MapperException(null);
		}
		return serverDetails;
	}

	public String[] getServerInformationKeys() {
		return serverInformationKeys;
	}

	public String[] getSearchInformationKeys() {
		return searchInformationKeys;
	}

	@Override
	public Resource processSimpleRead(String serverName, String resourceName,
			String resourceId) throws MapperException {
		FHIRSearchManager manager = new FHIRSearchManager(this, serverName,
				getServerParameter(serverName, "type"));
		EObject resource = manager.getResource(resourceName, resourceId);
		return convertToResource(resource, manager, serverName, resourceName,
				resourceId);
	}

	public Element getDocumentRoot(String serverName) throws MapperException {
		Element result = serverXmlDoms.get(serverName);
		if (result == null) {
			throw new MapperException(null);
		}
		return result;
	}

	private Resource convertToResource(EObject resource,
			FHIRSearchManager manager, String serverName, String resourceName,
			String resourceId) throws MapperException {
		if (resource != null) {
			try {
			EPackage classModel = getMappedStructure(serverName, resourceName)
					.getClassModelRoot();
			EcoreReferenceBridge bridge = new EcoreReferenceBridge(classModel);
			Resource refModelResource = bridge
					.makeReferenceModelResource(resource);
			Narrative narrative  = manager.makeNarrative(resource,resourceName);
			if (narrative != null) { refModelResource.setText(narrative);
			}
			return refModelResource;
			} catch (Exception e) {
				throw new MapperException("");
			}
		} else {
			return null;
		}
	}

	public MappedStructure getMappedStructure(String serverName,
			String resourceName) throws MapperException {
		return getReader(serverName, resourceName).ms();
	}

	public MDLXOReader getReader(String serverName, String resourceName)
			throws MapperException {
		Map<String, MDLXOReader> serverResourceReader = serverResourceReaders
				.get(serverName);
		return serverResourceReader.get(resourceName);
	}

	public DBStructure getDBStructure(String serverName) throws MapperException {
		DBStructure result = serverStructures.get(serverName);
		if (result == null) {
			throw new MapperException(null);
		}
		return result;
	}

	@Override
	public Resource processConformanceOperation(String serverName) throws MapperException {
		String resourceName= "Conformance";
		String serverType = getServerParameter(serverName, "type");
		FHIRSearchManager manager = new FHIRSearchManager(this,serverName,serverType);
		EObject resource = manager.getConformance(serverName);
		return convertToResource(resource, manager, serverName, resourceName,
				"");
	}

	@Override
	public AtomFeed processSearch(String[] serverNames, String resourceName,
			Map<String, String> parameters) throws MapperException {
		// make an AtomFeed per server and merge them before sending
		Map<String,String[]> cleanParams  = new HashMap<String, String[]>();
		for (Entry<String, String> e : parameters.entrySet()) {
			cleanParams.put(e.getKey(), e.getValue().split("\\+"));
		}
		String allServerNames = "";
		Vector<AtomFeed> feeds = new Vector<AtomFeed>();
		for (int s = 0; s < serverNames.length;s++)
		{
			// convert a FHIR search into an object query 
			String serverName = serverNames[s];
			String serverType = getServerParameter(serverName, "type");
			allServerNames = allServerNames + serverName + " ";

			QueryConverter converter = new QueryConverter(this,cleanParams,serverName, resourceName);
			Vector<String> objQueries = converter.queryStrings();
			FHIRSearchManager manager = new FHIRSearchManager(this,serverName,serverType);
			Hashtable<String,String> ids = new Hashtable<String,String>();
			EObject result = null;
			AtomFeed feed = null;
			
			// non-chained queries
			if (objQueries.size() == 1)
			{
				// execute the object query and build a list of FHIR ids 
				manager.buildFHIRIds(resourceName, objQueries.get(0), null, ids);
				
				// return an EObject (containing all the resulting resources) for the AtomFeed
				result = manager.getResourceBundle(resourceName, ids);
				feed = makeFeedWithNarratives(serverName, resourceName, result, manager);
			}
			
			// chained queries on one or more referenced resources
			else if (objQueries.size() > 1)
			{
				Vector<Hashtable<String,String>> allRefIds = new Vector<Hashtable<String,String>>();
				Vector<AtomFeed> allFeeds = new Vector<AtomFeed>();

				// collect FHIR ids from chained queries on referenced resources
				for (int chain = 0; chain < objQueries.size() - 1; chain++)
				{
					String chainedQuery = objQueries.get(chain);
					
					// find all ids of the referenced resource
					String refResourceName = getSearchedResource(chainedQuery);
					Hashtable<String,String> refIds = new Hashtable<String,String>();
					manager.buildFHIRIds(refResourceName, chainedQuery, null, refIds);
					allRefIds.add(refIds);
					
					// make a bundle containing the referenced resources
					EObject refResult = manager.getResourceBundle(refResourceName, refIds);
					AtomFeed refFeed = makeFeedWithNarratives(serverName, resourceName, refResult, manager);
					allFeeds.add(refFeed);
				}
				
				/* for every combination of ids of referenced resources, build up the list of ids of the queried resource (set union) */
				Vector<String[]> idProduct = cartesianProduct(allRefIds);
				String objQuery = objQueries.get(objQueries.size() - 1); // main object query, on the queried resource
				for (int i = 0; i < idProduct.size();i++)
				{
					String[] idArray = idProduct.get(i);
					// run the query with one combination of referenced resource ids, to collect more ids of the queried resource
					manager.buildFHIRIds(resourceName, objQuery, idArray, ids);
				}
								
				// make an EObject (containing all the resulting queried resources) for the AtomFeed
				result = manager.getResourceBundle(resourceName, ids);
				AtomFeed queriedResourceFeed = makeFeedWithNarratives(serverName, resourceName, result, manager);
				
				// merge the feed containing the queried resources (first) with the feeds containing the referenced resources  
				allFeeds.insertElementAt(queriedResourceFeed, 0);
				feed = mergeFeeds(allFeeds);
			} 
						
			// collect feeds from different servers in a Vector to be merged
			feeds.add(feed);
		}
		
		// merge feeds from all servers and send the response
		AtomFeed merged = mergeFeeds(feeds);
		merged.setTitle("Search " + resourceName + " in " + allServerNames);
		return merged;
	}
	
	private static AtomFeed mergeFeeds(Vector<AtomFeed> feeds)
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
	
	private static String getSearchedResource(String query)
	{
		// the resource in mentioned in the second word of the query
		StringTokenizer st = new StringTokenizer(query," ");
		st.nextToken(); // 'select'
		String resPart = st.nextToken(); // this is <Resource>.fhir_id
		
		StringTokenizer su = new StringTokenizer(resPart,".");
		return su.nextToken();
	}
	
	private static Vector<String[]> cartesianProduct(Vector<Hashtable<String,String>> allIds)
	{
		Vector<String[]> product = new Vector<String[]>();
		Hashtable<String,String> firstIds = allIds.get(0);
		allIds.remove(0);

		if (allIds.size() == 0)
		{
			for (Enumeration<String> en = firstIds.keys();en.hasMoreElements();)
			{
				String[] keys = new String[1];
				keys[0] = en.nextElement();
				product.add(keys);
			}
		}

		else if (allIds.size() > 0)
		{
			Vector<String[]> smaller = cartesianProduct(allIds);
			for (Enumeration<String> en = firstIds.keys();en.hasMoreElements();)
			{
				String key = en.nextElement();
				for (int s = 0; s < smaller.size(); s++)
				{
					String[] prev = smaller.get(s);
					String[] now = new String[prev.length + 1];
					now[0] = key;
					for (int p = 0; p < prev.length; p++) now[p+1] = prev[p];
					product.add(now);
				}
			}
		}
		
		return product;
	}
	
	private AtomFeed makeFeedWithNarratives(String serverName, String resourceName, EObject result, FHIRSearchManager manager) throws MapperException
	{
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
		return feed;
	}

}
