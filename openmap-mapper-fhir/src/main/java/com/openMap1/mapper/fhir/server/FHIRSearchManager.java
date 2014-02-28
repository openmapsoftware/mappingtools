package com.openMap1.mapper.fhir.server;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Narrative.NarrativeStatus;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.query.QueryExecutor;
import com.openMap1.mapper.query.QueryParser;
import com.openMap1.mapper.query.QueryParserImpl_Ecore;
import com.openMap1.mapper.query.QueryStrategy;
import com.openMap1.mapper.query.QueryStrategyImpl;
import com.openMap1.mapper.query.RDBReader;
import com.openMap1.mapper.query.SQLQuery;
import com.openMap1.mapper.reader.EMFInstanceFactory;
import com.openMap1.mapper.reader.GenericEMFInstanceFactoryImpl;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.structures.DBStructure;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;

public class FHIRSearchManager {
	
	private OpenMapsFhirService fhirService;
	
	private String serverName;
	
	private String serverType;
	
	// key = resource id; value = narrative for the resource
	private Hashtable<String,Narrative> allNarratives;
	
	private boolean tracing = false;
	
	/**
	 * 
	 * @param fhirService
	 * @throws MapperException
	 */
	public FHIRSearchManager(OpenMapsFhirService fhirService,String serverName, String serverType)  throws MapperException
	{
		this.fhirService = fhirService;
		this.serverName = serverName;
		this.serverType = serverType;
		
	}
	
	private EPackage getClassModel(String resourceName) throws MapperException
	{
		EPackage classModel = fhirService.getMappedStructure(serverName, resourceName).getClassModelRoot();
		if (classModel == null) throw new MapperException("Cannot get class model for resource " + resourceName);
		return classModel;
	}
	
	/**
	 * 
	 * @param resourceName
	 * @param query
	 * @param ids
	 * @throws MapperException
	 */
	public void buildFHIRIds(String resourceName, String query, String[] substitutes, Hashtable<String,String> ids) throws MapperException
	{
		// make a parser for the object query
		// message("parsing object query");
		Vector<String[]> errors = new Vector<String[]>();
        QueryParser queryParser = new QueryParserImpl_Ecore(getClassModel(resourceName),"X",errors,tracing);

        // possibly replace quoted strings '%0', '%1' in the query text by fhir ids of refrenced resources
        String newQuery = query;
        if (substitutes != null) 
        {
        	newQuery = replaceAllInQuery(query, substitutes);
        }
        
        // parse the query
    	boolean parsable = queryParser.parse(newQuery);
    	if (!parsable)  throw new MapperException("Cannot parse object query " + newQuery);
    	
		// define the query strategy (ordering of the QueryClasses made by the parser)
    	QueryStrategy queryStrategy = new QueryStrategyImpl(queryParser);
		queryStrategy.defineStrategy();
		
		// define subsets for all QueryClasses (needed to make an SQL query)
		MDLXOReader reader = fhirService.getReader(serverName, resourceName);
		queryStrategy.setSubsets("X",reader);
		
		// set up the DOM to retrieve FHIR ids for all resources matching the search
		setReaderDOMForFHIRIds(reader,queryParser);
		        
        // execute the query against the DOM
        QueryExecutor executor = new QueryExecutor(reader,queryParser,queryStrategy);
        executor.initialiseQuery();
        executor.calculateResult(true); // merge duplicate fhir ids,which should not exist
        executor.setResultVector();
        
        // collect the resource ids
        Vector<Vector<String[]>> id_results = executor.resultVector();
        for (Iterator<Vector<String[]>> it = id_results.iterator();it.hasNext();)
        {
        	Vector<String[]> resRow = it.next();
        	String[] firstCell = resRow.get(0);
        	String fhir_id = firstCell[2];
        	ids.put(fhir_id, "1");
        }
	}
	
	/**
	 * replace a series of quoted strings '%0', '%1' etc in an object query 
	 * by elements 0, 1, etc of a string array (which are fhir ids of referenced resources)
	 * @param query
	 * @param substitutes
	 * @return
	 */
	private String replaceAllInQuery(String query, String[] substitutes)
	{
		String newQuery = query;
		for (int s = 0; s < substitutes.length; s++)
		{
			String toReplace = "%" + s;   // '%0', '%1', etc
			String nextQuery = replaceInQuery(newQuery, substitutes[s],toReplace);
			newQuery = nextQuery;
		}
		return newQuery;
	}
	
	/**
	 * replace '<placeholder>' in single quotes by '<substitute>' in single quotes
	 * @param query
	 * @param substitute
	 * @param placeHolder
	 * @return
	 */
	private String replaceInQuery(String query, String substitute, String placeHolder)
	{
		StringTokenizer st = new StringTokenizer(query,"'",true);
		String replaced = "";
		while (st.hasMoreTokens())
		{
			String piece = st.nextToken();
			if (piece.equals(placeHolder)) piece = substitute;
			replaced = replaced + piece;
		}
		return replaced;
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 * @throws MapperException
	 */
	@SuppressWarnings("unchecked")
	public EObject getResourceBundle(String resourceName,  Hashtable<String,String> ids) throws MapperException
	{
        Vector<EObject> resources = new Vector<EObject>();
        allNarratives = new Hashtable<String,Narrative>() ;
        for (Enumeration<String> en = ids.keys();en.hasMoreElements();)
        {
        	String fhir_id = en.nextElement();
        	
        	EObject resourceObject  = getResource(resourceName,fhir_id);
        	if (resourceObject != null)
        	{
        		resources.add(resourceObject);
        		Narrative nar = makeNarrative(resourceObject,resourceName);
        		if (nar != null) allNarratives.put(fhir_id, nar);
        	}
        }
        
        // create the top AtomFeed EObject, and add the resources to it
		MDLXOReader reader = fhirService.getReader(serverName, resourceName);
        EObject feedObject = ModelUtil.createModelObject("feed.AtomFeed", reader.classModel());
        EClass feedClass = ModelUtil.getNamedClass(reader.classModel(), "feed.AtomFeed");
        EStructuralFeature resourceFeature = feedClass.getEStructuralFeature(GenUtil.initialLowerCase(resourceName));
        if (resourceFeature == null) throw new MapperException("Cannot find resource feature of AtomFeed object");
        Object featureVal = feedObject.eGet(resourceFeature);
        if (featureVal instanceof EList<?>)
        	for (int i = 0; i < resources.size();i++) ((EList<EObject>)featureVal).add(resources.get(i));
        else throw new MapperException("resource feature is not a list");		
        
        
		return feedObject;
	}
	
	/**
	 * 
	 * @param reader
	 * @param queryParser
	 * @throws MapperException
	 */
	private void setReaderDOMForFHIRIds(MDLXOReader reader,QueryParser queryParser) throws MapperException
	{
		
		// RDBMS server; execute the SQL query and build the DOM from it
		if (serverType.equals("RDBMS"))
		{
	        // make an RDBReader
	        DBStructure database = fhirService.getDBStructure(serverName);
	        RDBReader rdbReader = new RDBReader(database,"noFile");

			// convert the object model query into (one) SQL query to populate an XML DOM
	        Vector<SQLQuery> queries = queryParser.makeSQLQueries("X", database);
	        
	        // run the SQL query and convert the result set to an XML DOM
	        rdbReader.initiateQuery(queries);
	        Element rootNode = rdbReader.DOMFromSQL(queries);
	        reader.setRoot(rootNode);
		}
		
		// XML based server; just use the whole DOM
		else if (serverType.equals("XML"))
		{
			reader.setRoot(fhirService.getDocumentRoot(serverName));
		}
	}
	
	
	/**
	 * 
	 * @param resourceName
	 * @param fhir_id
	 * @return
	 * @throws MapperException
	 */
	public EObject getResource(String resourceName, String fhir_id) throws MapperException
	{
		// object query to return all attributes of the resource and all its component classes
		String objectQuery = "select " + resourceName + ".** where " + resourceName + ".fhir_id = '" + fhir_id + "'";

		// Parse the object query
		Vector<String[]> errors = new Vector<String[]>();
        QueryParser queryParser = new QueryParserImpl_Ecore(getClassModel(resourceName),"X",errors,tracing);
    	boolean parsable = queryParser.parse(objectQuery);
    	if (!parsable)  throw new MapperException("Cannot parse object query " + objectQuery);
		
		// define the query strategy (ordering of the QueryClasses made by the parser), in order to define the mapping subsets
    	QueryStrategy queryStrategy = new QueryStrategyImpl(queryParser);
		queryStrategy.defineStrategy();
		MDLXOReader reader = fhirService.getReader(serverName, resourceName);
		queryStrategy.setSubsets("X",reader);
		
		Element rootNode = getDOMForOneResource(queryParser);		        
        return getResourceFromDOM(reader,rootNode, resourceName, fhir_id);        
	}
	
	/**
	 * 
	 * @param queryParser
	 * @return
	 * @throws MapperException
	 */
	private Element getDOMForOneResource(QueryParser queryParser) throws MapperException
	{
		Element rootNode = null;
		
		// RDBMS case; execute the SQL query and build the DOM from it
		if (serverType.equals("RDBMS"))
		{
			// define the SQL query
	        DBStructure database = fhirService.getDBStructure(serverName);
	        Vector<SQLQuery> queries = queryParser.makeSQLQueries("X", database);
	        
	        // run the SQL query and convert the result set to an XML DOM
	        RDBReader rdbReader = new RDBReader(database,"noFile");
	        rdbReader.initiateQuery(queries);
	        rootNode = rdbReader.DOMFromSQL(queries);
		}
		
		// XML case; just use the whole DOM
		else if (serverType.equals("XML"))
		{
			rootNode = fhirService.getDocumentRoot(serverName);
		}
		
        return rootNode;
	}
	
	/**
	 * 
	 * @param reader
	 * @param rootNode
	 * @param resourceName
	 * @param id used only for error messages
	 * @return the EObject for a resource, as represented by an XML DOM through mappings
	 * @throws MapperException
	 */
	private EObject getResourceFromDOM(MDLXOReader reader, Element rootNode, String resourceName, String id) throws MapperException
	{
        reader.setRoot(rootNode);
		EObject resourceObject = null;
        // get the objectToken for the resource
        Vector<objectToken> resourceObjects = reader.getAllLocalObjectTokens("resources." + resourceName);
        // the equality condition on fhir id should mean there is at most one resource object
        if (resourceObjects.size() > 1) throw new MapperException(resourceObjects.size() + " resources of type " + resourceName + " with id " + id);
        
        // allow for cases where no resource objectToken is found - return null
        if (resourceObjects.size() > 0)
        {
        	objectToken topObjectToken = resourceObjects.get(0);
        	URI noFile = GenericEMFInstanceFactoryImpl.DO_NOT_SAVE_URI();
        	EMFInstanceFactory factory = new GenericEMFInstanceFactoryImpl();
        	// note - res is an EMF Resource, not a FHIR resource
        	Resource res = factory.createModelInstance(reader, noFile, topObjectToken);
        	if (res.getContents().size() > 0) resourceObject = res.getContents().get(0);
        }        
        return resourceObject;
	}
	
	/**
	 * 
	 * @param serverName
	 * @return
	 * @throws MapperException
	 */
	public EObject getConformance(String serverName) throws MapperException
	{
		Element rootNode = getConformanceDomForServer(serverName);
		MDLXOReader conformanceReader = fhirService.getReader(serverName, "Conformance");
		if (conformanceReader == null) throw new MapperException("Cannot find conformance mappings for server " + serverName);
        return getResourceFromDOM(conformanceReader,rootNode, "Conformance", "no_id");
	}
	
	public Element getConformanceDomForServer(String serverName) throws MapperException
	{
		Document doc = XMLUtil.makeOutDoc();
		Element root = XMLUtil.newElement(doc, "database");
		doc.appendChild(root);
		
		Element servers = XMLUtil.newElement(doc, "servers");
		root.appendChild(servers);
		Element resources = XMLUtil.newElement(doc, "resources");
		root.appendChild(resources);
		Element searches = XMLUtil.newElement(doc, "searches");
		root.appendChild(searches);
		
		Element serverRecord = XMLUtil.newElement(doc, "record"); 
		servers.appendChild(serverRecord);
		String[] details = fhirService.getServerDetails(serverName);
		for (int i = 0; i < details.length;i++)
		{
			String colName = fhirService.getServerInformationKeys()[i];
			Element colEl = XMLUtil.textElement(doc, colName, details[i]);
			serverRecord.appendChild(colEl);
		}
		
		Map<String,String[]> resourceDetails = fhirService.getResourceDetailsForServer(serverName);
		for (String resourceName : resourceDetails.keySet()) {
			Element resourceRecord = XMLUtil.newElement(doc, "record");
			resources.appendChild(resourceRecord);
			details = resourceDetails.get(resourceName);
			for (int i = 0; i < details.length;i++)
			{
				String colName = fhirService.getResourceInformationKeys()[i];
				Element colEl = XMLUtil.textElement(doc, colName, details[i]);
				resourceRecord.appendChild(colEl);
			}
			
			Vector<String[]> allSearchDetails = fhirService.getSearches(serverName, resourceName);
			if (allSearchDetails != null) for (int s = 0; s < allSearchDetails.size();s++)
			{
				String[] searchDetails = allSearchDetails.get(s);
				Element searchRecord = XMLUtil.newElement(doc, "record");
				searches.appendChild(searchRecord);
				for (int d = 0; d < searchDetails.length;d++)
				{
					String colName = fhirService.getSearchInformationKeys()[d];
					Element colEl = XMLUtil.textElement(doc, colName, searchDetails[d]);
					searchRecord.appendChild(colEl);
				}
			}
		}
		return root;
	}

	
	// ----------------------------------------------------------------------------------------------------
	//                                 Narratives for Resources
	// ----------------------------------------------------------------------------------------------------
	
	public Narrative getNarrative(String fhir_id) {return allNarratives.get(fhir_id);}
	
	/**
	 * 
	 * @param resource
	 * @return a Narrative object in the FHIR reference implementation
	 */
	public Narrative makeNarrative(EObject resource, String resourceName) throws MapperException
	{		
		// read a template DOM and fill in its values from the resource
		Element tableEl = makeNarrativeDom(resource, resourceName);
		
		if (tableEl != null)
		{
			// make a generated Narrative
			Narrative narrative = new Narrative();
			narrative.setStatusSimple(NarrativeStatus.generated);
			
			// convert the filled template DOM to Xhtml, as in the reference implementation, and add it to the Narrative
			XhtmlNode node = makeXhtmlNode(tableEl);
			narrative.setDiv(node);			
			return narrative;
		}
		return null;
		
	}
	
	/**
	 * make an XhtmlNode with all its descendants from a DOM node and its descendants
	 * @param DOMNode
	 * @return
	 */
	private XhtmlNode makeXhtmlNode(Element DOMNode) throws MapperException
	{
		String nodeName = XMLUtil.getLocalName(DOMNode);
		if (nodeName == null) throw new MapperException("Null DOM node name");
		String text = XMLUtil.getText(DOMNode);
		Vector<Element> childEls = XMLUtil.childElements(DOMNode);
		
		XhtmlNode  node = new XhtmlNode(NodeType.Element,nodeName);		
		// XhtmlNode  node = new XhtmlNode();node.setNodeType(NodeType.Element);node.setName(nodeName);	
		
		
		
		if ((text != null) && (text.length() > 0))
		{
			XhtmlNode textNode = new XhtmlNode(NodeType.Text);
			//XhtmlNode textNode = new XhtmlNode();textNode.setNodeType(NodeType.Text);
			
			textNode.setContent(text);
			node.getChildNodes().add(textNode);
		}
		
		NamedNodeMap map = DOMNode.getAttributes();
		for (int n = 0; n < map.getLength();n++)
		{
			Node nd = map.item(n);
			if (nd instanceof Attr)
			{
				Attr att = (Attr)nd;
				String attName = att.getName();
				String attVal = att.getValue();
				node.setAttribute(attName, attVal);
			}
		}
		
		for (int i = 0; i < childEls.size(); i++) node.getChildNodes().add(makeXhtmlNode(childEls.get(i)));

		return node;
	}
	
	/**
	 * get a template narrative DOM and fill it out with values from the resource
	 * @param resource
	 * @param resouceName
	 * @return
	 */
	private Element makeNarrativeDom(EObject resource, String resourceName) throws MapperException
	{
		// get the narrative template DOM for the resource type
		Element templateEl = fhirService.getNarrativeTemplate(serverName, resourceName);	
		if (templateEl != null)
		{
			Document doc = XMLUtil.makeOutDoc();
			return filledElement(doc, templateEl, resource);		
		}
		return null;
	}
	
	/**
	 * recursive descent of the narrative template, filling it with values from the resource
	 * @param doc
	 * @param templateEl
	 * @param resource
	 * @return
	 * @throws MapperException
	 */
	private Element filledElement(Document doc, Element templateEl, EObject resource) throws MapperException
	{
		Element filledEl = null;
		String elName = XMLUtil.getLocalName(templateEl);
		String content = XMLUtil.getText(templateEl);
		
		// substitute the text content of this element if required to
		if ((content.length() > 2) && (content.startsWith("$")))
		{
			String separator = content.substring(1,2);
			String paths = content.substring(2);
			content = getPathsValue(resource,paths,separator);
		}
		if (content.length() > 0) filledEl = XMLUtil.textElement(doc, elName, content);
		else filledEl = XMLUtil.newElement(doc, elName);
		
		// copy across attributes, except 'repeat'
		NamedNodeMap nl = templateEl.getAttributes();
		for (int n = 0; n < nl.getLength();n++)
		{
			Node nd = nl.item(n);
			if (nd instanceof Attr)
			{
				Attr att = (Attr)nd;
				if (!att.getName().equals("repeat")) filledEl.setAttribute(att.getName(), att.getValue());
			}
		}
		
		// iterate over child nodes
		Vector<Element> children = XMLUtil.childElements(templateEl);
		for (int c = 0; c < children.size(); c++)
		{
			Element templateChild = children.get(c);
			String repeat = templateChild.getAttribute("repeat");
			// no repeat; stay where you are in the resource EObject
			if (repeat.length() == 0) filledEl.appendChild(filledElement(doc,templateChild,resource));
			// repeat required; find all descendant EObjects
			else if (repeat.length() > 0)
			{
				Vector<EObject> resourceChildren = getRepeats(resource,repeat);
				for (int r = 0; r < resourceChildren.size(); r++)
				{
					EObject resourceChild = resourceChildren.get(r);
					filledEl.appendChild(filledElement(doc,templateChild, resourceChild));
				}
			}
		}		
		return filledEl;		
	}
	
	/**
	 * 
	 * @param resource
	 * @param path
	 * @return
	 * @throws MapperException
	 */
	private Vector<EObject> getRepeats(EObject resource,String path) throws MapperException
	{
		Vector<EObject> repeats = new Vector<EObject>();
		StringTokenizer steps = new StringTokenizer(path, ".");
		String firstStep = steps.nextToken();
		String rest = "";
		if (steps.hasMoreTokens()) rest = path.substring(firstStep.length() +1);

		EStructuralFeature feat = resource.eClass().getEStructuralFeature(firstStep);
		if (feat == null) throw new MapperException("Feature '" + firstStep + "' in path '" + path + "' not recognised");
		Object nextObj = resource.eGet(feat);
		if (nextObj instanceof EObject)
		{
			if (rest.equals("")) repeats.add((EObject)nextObj);
			else repeats = getRepeats((EObject)nextObj,rest);
		}
		else if (nextObj instanceof EList<?>)
		{
			EList<?> nextList = (EList<?>)nextObj;
			for (int i = 0; i < nextList.size();i++)
			{
				EObject obj = (EObject)nextList.get(i);
				if (rest.length() > 0)
				{
					Vector<EObject> further = getRepeats(obj,rest);
					for (int f = 0; f < further.size(); f++) repeats.add(further.get(f));
				}
				else repeats.add(obj);
			}
		}
		return repeats;
	}

	
	
	
	/**
	 * follow several paths in an EObject and return the concatenated string values
	 * @param resource
	 * @param paths
	 * @param separator
	 * @return
	 * @throws MapperException
	 */
	private String getPathsValue(EObject resource, String paths,String separator) throws MapperException
	{
		StringTokenizer st = new StringTokenizer(paths,"+ ");
		String value = "";
		while (st.hasMoreTokens()) value = value + getPathValue(resource, st.nextToken()) + separator;
		return value.substring(0,value.length() - separator.length());
	}
	
	/**
	 * follow a path in a EObject and return an attribute value
	 * @param resource
	 * @param path
	 * @return
	 * @throws MapperException
	 */
	private String getPathValue(EObject resource, String path) throws MapperException
	{
		EObject current = resource;
		StringTokenizer st = new StringTokenizer(path,".");
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			EClass currentClass = current.eClass();
			EStructuralFeature feat = currentClass.getEStructuralFeature(step);
			if (feat == null) throw new MapperException("Feature '" + step + "' in path '" + path + "' not recognised");
			Object nextObj = current.eGet(feat);

			// associations along the path
			if (st.hasMoreTokens())
			{
				if (nextObj instanceof EObject) current = (EObject)nextObj;
				// when an EReference has maxMult > 1, take the first element
				else if (nextObj instanceof EList<?>) current =  (EObject)((EList)nextObj).get(0);
			}

			// property at the final step of the path
			else if (nextObj == null) return "--";
			else if (nextObj instanceof String) return (String)nextObj;
			else throw new MapperException ("Property " + feat + " is not a string");
		}
		return "";
		
	}


	/**
	 * diagnostic recursive write of xhtml node names
	 * @param node
	 * @param level
	 */
	private void writeNode(XhtmlNode node, int level)
	{
		for (int i = 0; i < node.getChildNodes().size(); i++) writeNode(node.getChildNodes().get(i), level +1);
		
	}

}
