package com.openMap1.mapper.fhir.server;

import java.util.Hashtable;
import java.util.Iterator;
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
	
	private FHIRServlet servlet;
		
	// the FHIR class model for the resource
	private EPackage classModel;
	
	// key = resource id; value = narrative for the resource
	private Hashtable<String,Narrative> allNarratives;
	
	private boolean tracing = false;
	
	/**
	 * 
	 * @param servlet
	 * @throws MapperException
	 */
	public FHIRSearchManager(FHIRServlet servlet)  throws MapperException
	{
		this.servlet = servlet;
		
		classModel = servlet.getMappedStructure(servlet.serverName(), servlet.resourceName()).getClassModelRoot();
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 * @throws MapperException
	 */
	@SuppressWarnings("unchecked")
	public EObject getFHIRSearchResult(String query) throws MapperException
	{
		// Parse the object query
		message("parsing object query");
		Vector<String[]> errors = new Vector<String[]>();
        QueryParser queryParser = new QueryParserImpl_Ecore(classModel,"X",errors,tracing);
    	boolean parsable = queryParser.parse(query);
    	if (!parsable)  throw new MapperException("Cannot parse object query " + query);
    	
		message("defining query strategy");
		// define the query strategy (ordering of the QueryClasses made by the parser)
    	QueryStrategy queryStrategy = new QueryStrategyImpl(queryParser);
		queryStrategy.defineStrategy();
		
		message("defining mapping subsets");
		// define subsets for all QueryClasses (needed to make an SQL query)
		MDLXOReader reader = servlet.getReader(servlet.serverName(), servlet.resourceName());
		queryStrategy.setSubsets("X",reader);
		
    	message("Making RDBReader");
        // make an RDBReader
        DBStructure database = servlet.getDBStructure(servlet.serverName());
        RDBReader rdbReader = new RDBReader(database,"noFile");

        message("defining SQL query");
		// convert the object model query into (one) SQL query to populate an XML DOM
        Vector<SQLQuery> queries = queryParser.makeSQLQueries("X", database);
        
        message("Running SQL query");
        // run the SQL query and convert the result set to an XML DOM
        rdbReader.initiateQuery(queries);
        Element rootNode = rdbReader.DOMFromSQL(queries);
        reader.setRoot(rootNode);
        
        // execute the query against the DOM
        message("executing DOM query for fhir_ids");
        QueryExecutor executor = new QueryExecutor(reader,queryParser,queryStrategy);
        executor.initialiseQuery();
        executor.calculateResult(true); // merge duplicate fhir ids,which should not exist
        executor.setResultVector();
        
        // collect the resource EObjects
        Vector<Vector<String[]>> id_results = executor.resultVector();
        Vector<EObject> resources = new Vector<EObject>();
        message("collecting " + id_results.size() + " resource EObjects");
        allNarratives = new Hashtable<String,Narrative>() ;
        for (Iterator<Vector<String[]>> it = id_results.iterator();it.hasNext();)
        {
        	Vector<String[]> resRow = it.next();
        	String[] firstCell = resRow.get(0);
        	String fhir_id = firstCell[2];
        	
        	EObject resourceObject  = getResource(servlet.resourceName(),fhir_id);
        	if (resourceObject == null) message("Failed to retrieve resource with id " + fhir_id);
        	else 
        	{
        		resources.add(resourceObject);
        		message("making narrative");
        		Narrative nar = makeNarrative(resourceObject,servlet.resourceName());
        		if (nar != null) allNarratives.put(fhir_id, nar);
        		else message("no narrative for resource " + servlet.resourceName());
        	}
        }
        
        // create the top AtomFeed EObject, and add the resources to it
        message("creating top AtomFeed object to hold " + resources.size() + "resources");
        EObject feedObject = ModelUtil.createModelObject("feed.AtomFeed", reader.classModel());
        EClass feedClass = ModelUtil.getNamedClass(reader.classModel(), "feed.AtomFeed");
        EStructuralFeature resourceFeature = feedClass.getEStructuralFeature(GenUtil.initialLowerCase(servlet.resourceName()));
        if (resourceFeature == null) throw new MapperException("Cannot find resource feature of AtomFeed object");
        Object featureVal = feedObject.eGet(resourceFeature);
        if (featureVal instanceof EList<?>)
        	for (int i = 0; i < resources.size();i++) ((EList<EObject>)featureVal).add(resources.get(i));
        else throw new MapperException("resource feature is not a list");		
        message("added resources to top AtomFeed object");
        
        
		return feedObject;
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
		message("Query: " + objectQuery);

		// Parse the object query
		Vector<String[]> errors = new Vector<String[]>();
        QueryParser queryParser = new QueryParserImpl_Ecore(classModel,"X",errors,tracing);
    	boolean parsable = queryParser.parse(objectQuery);
    	if (!parsable)  throw new MapperException("Cannot parse object query " + objectQuery);
		
		// define the query strategy (ordering of the QueryClasses made by the parser), in order to define the mapping subsets
    	QueryStrategy queryStrategy = new QueryStrategyImpl(queryParser);
		queryStrategy.defineStrategy();
		MDLXOReader reader = servlet.getReader(servlet.serverName(), resourceName);
		queryStrategy.setSubsets("X",reader);
		
		// define the SQL query
        DBStructure database = servlet.getDBStructure(servlet.serverName());
        Vector<SQLQuery> queries = queryParser.makeSQLQueries("X", database);
        
        // run the SQL query and convert the result set to an XML DOM
        RDBReader rdbReader = new RDBReader(database,"noFile");
        rdbReader.initiateQuery(queries);
        Element rootNode = rdbReader.DOMFromSQL(queries);
        
        return getResourceFromDOM(reader,rootNode, resourceName, fhir_id);
        
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
        else message("No top object found");
        
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
		Element rootNode = new Conformance(servlet).makeConfigDom();
		MDLXOReader conformanceReader = servlet.getReader(serverName, "Conformance");
		if (conformanceReader == null) throw new MapperException("Cannot find conformance mappings for server " + serverName);
        return getResourceFromDOM(conformanceReader,rootNode, "Conformance", "no_id");
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
		
		XhtmlNode  node = new XhtmlNode();		
		node.setName(nodeName);
		node.setNodeType(NodeType.Element);
		
		if ((text != null) && (text.length() > 0))
		{
			XhtmlNode textNode = new XhtmlNode();
			textNode.setNodeType(NodeType.Text);
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
		Element templateEl = servlet.getNarrativeTemplate(servlet.serverName(), resourceName);	
		if (templateEl != null)
		{
			Document doc = XMLUtil.makeOutDoc();
			return filledElement(doc, templateEl, resource);		
		}
		else message("found no template");
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
		message("Level " + level + ": " + node.getName());
		for (int i = 0; i < node.getChildNodes().size(); i++) writeNode(node.getChildNodes().get(i), level +1);
		
	}


	
	// ----------------------------------------------------------------------------------------------------
	//                                        odds & sods
	// ----------------------------------------------------------------------------------------------------
	


	
	private void message(String s) {servlet.message(s);}


}
