package com.openMap1.mapper.fhir.server;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.mapping.MDLBase;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;

/**
 * class to convert a FHIR search of resource into a Mapper query language query
 * @author Robert
 *
 */

public class QueryConverter {
	
	// the FHIR servlet that received the query
	private FHIRServlet servlet;
			
	// the FHIR class model for the resource
	private EPackage classModel;
	
	// the server
	private String serverName;
	
	// the resource 
	private String resourceName;
	
	// parameters of the FHIR search, got from the url
	private Map<String,String[]> params;
	
	/* searches supported for this resource, provided the database has the required mappings.
	 * One search row has columns: server,resource,LHS,type,conditions */
	private Vector<String[]> searches;
	
	// comparisons supported in FHIR queries
	private int EQUAL = 0;
	private int BEFORE = 1; //date before or number less than
	private int AFTER = 2; // date after or number greater than
	private int BEFORE_OR_EQUAL = 3; 
	private int AFTER_OR_EQUAL = 4;
	
	// string values in the searches csv file saying which searches are supported for a resource
	private String[] fhirTests = {"equal","<",">","<=",">="};
	
	public QueryConverter(FHIRServlet servlet,  Map<String,String[]> params,String serverName, String resourceName) throws MapperException
	{
		this.servlet = servlet;
		this.params = params;
		this.serverName = serverName;
		this.resourceName = resourceName;
		
		searches = servlet.getSearches(serverName, resourceName);
		
		classModel = servlet.getMappedStructure(serverName, resourceName).getClassModelRoot();
		
		writeParams();
	}
	
	/**
	 * 
	 * @return the object query needed to do the search
	 * @throws MapperException
	 */
	public Vector<String> queryStrings() throws MapperException
	{
		// query just returns FHIR ids for eligible resources
		String query = "Select " + resourceName + ".fhir_id where ";
		
		// to store chained queries, and to track the variables to be replaced by fhir ids from chained queries in the main query
		Vector<String> chainedQueries = new Vector<String>();

		// loop over conditions in the search
		for (Iterator<String> it = params.keySet().iterator(); it.hasNext();)
		{
			// if there is a chained query, it will return FHIR ids for referenced resources
			String chainedQuery = null;

			// LHS of FHIR search condition
			String fullParam = it.next();
			// possible LHS include 'family:exact' and 'subject:Patient' and 'subject:Patient.name'. The last two are reference searches.
			String refType = null; // the name of the referenced resource
			String refSearch = null; // for chained searches, the search LHS
			StringTokenizer paramParts = new StringTokenizer(fullParam,":");
			String param = paramParts.nextToken(); // the part of the LHS before any ':'
			if (paramParts.hasMoreTokens())
			{
				String type = paramParts.nextToken(); // the part of the LHS after the ':'
				// exact string search - keep full LHS with ':exact'
				if (type.equals("exact")) param = fullParam;
				/* typed reference search such as 'subject:Patient=23' or 'subject:Patient.name=peter' ; 
				 * use short LHS and remember the type. Chained reference searches are only supported when the reference type is defined explicitly */
				else 
				{
					StringTokenizer refParts = new StringTokenizer(type, ".");
					if (refParts.countTokens() > 2) throw new MapperException("Cannot yet handle double-chained searches");
					refType = refParts.nextToken(); // type of resource which is referenced, e.g. 'Patient'
					if (refParts.hasMoreTokens()) // chained query
					{
						refSearch = refParts.nextToken(); // search type on the referenced resource, e.g 'name'
						chainedQuery = "Select " + refType + ".fhir_id where ";
					}
				}
			}
			
			// RHS of FHIR search condition
			String[] values = params.get(fullParam);
			if (values.length > 1) throw new MapperException("Multi-value conditions are not yet handled");
			String value = values[0];
			
			// check that the LHS is a supported search, and find the paths it requires
			String[] pathTypes = getSearchPathsAndType(param,searches);
			
			// add one or two conditions to the query on the main resource, except if they  belong in a chained query
			if (chainedQuery == null) query = addConditionsToQuery(query, resourceName, param, refType, pathTypes, value);
			
			// when this condition is a chained query
			else if (chainedQuery != null)
			{
				String toReplace = "%" + chainedQueries.size(); // %0, %1, etc. for different chained queries on referenced resources

				// add a condition on the main query, whose RHS '%n' will be replaced by an id of the referenced resource
				query = addConditionsToQuery(query,resourceName, param, refType, pathTypes, toReplace);
				
				// find supported searches on the referenced resource
				Vector<String[]> refSearches = servlet.getSearches(serverName, refType);
				
				// find the paths required, and other parameters of the search on the referenced resource
				String[] refPathTypes = getSearchPathsAndType(refSearch,refSearches);
				
				// add one or two conditions to the query on the referenced resource
				chainedQuery = addConditionsToQuery(chainedQuery, refType, refSearch, null, refPathTypes, value);
				
				// remove the last " AND " from the chained query
				chainedQuery = chainedQuery.substring(0, chainedQuery.length() - 5);
				message("Chained object query: " + chainedQuery);
				
				// save the chained query
				chainedQueries.add(chainedQuery);
			}

		}
		
		// remove the last " AND " from the main query
		query = query.substring(0, query.length() - 5);
		message("Main resource object query: " + query);
		
		// if there were any chained queries, they come before the main query
		chainedQueries.add(query);
		return chainedQueries;
	}
	
	/**
	 * 
	 * @param query the query to which one or more conditions are to be added
	 * @param resourceName the resource being queried
	 * @param param query LHS which defines paths in the resource
	 * @param refType for reference search conditions, the type of the referenced resource
	 * @param pathTypes array giving [paths, search type, allowed conditions, solo]
	 * @param value RHS of the condition (or of two conditions, if it contains '|')
	 * @throws MapperException
	 */
	private String addConditionsToQuery(String query, String resourceName, String param, String refType, String[] pathTypes, String value) throws MapperException
	{
		String newQuery = query;
		
		// unpack the array parameter
		String paths = pathTypes[0];
		String searchType = pathTypes[1];
		String fhirConditionString = pathTypes[2];
		
		// token searches can have one or two values, separated by '|'; and only support equality tests on either
		StringTokenizer ss = new StringTokenizer(value,"|");
		int valuesToTest = ss.countTokens(); // 1, or may be 2 for a token search . Then the 2 values are [system uri,value]

		// look at the beginning of the value to find out what the test is
		int FHIRTest = -1;
		if (value.startsWith(">")) FHIRTest = AFTER;
		else if (value.startsWith("<")) FHIRTest = BEFORE;
		else FHIRTest = EQUAL;

		// check that all paths needed for the condition are mapped; token searches will need two paths if the RHS contains '|'
		StringTokenizer st = new StringTokenizer(paths,"; ");
		int pathsDefined = st.countTokens(); // 2 for a token search [system, value], 1 for all other searches
		if ((pathsDefined > 1) && (!searchType.equals("token"))) throw new MapperException("Only one path for a non-token search");
		// iterate over 1 or 2 paths, matching up with RHS values (from the other StringTokenizer ss)
		while (st.hasMoreTokens())
		{
			String path = st.nextToken();
			// if there are 2 paths and only one value to test, use the second path (the value path, not the uri path) to test it
			if (pathsDefined > valuesToTest) path = st.nextToken(); // stops further iteration
			// for reference searches, servers.csv may optionally leave out the last ".reference" in the path
			if ((searchType.equals("reference")) && (!path.endsWith(".reference"))) path = path + ".reference";

			// check that there is a chain of mappings, leading to a property mapping for this path
			checkMappings(resourceName,path,refType);
			
			// compose the condition for the Object query
			String rhsValue = ss.nextToken(); // when there are two values to test, these two StringTokenizers (st and ss) go in step
			String theTest = fhirTests[FHIRTest];
			// for all tests except '=', strip a comparator such as '<' off the start of the RHS to test
			if (FHIRTest != EQUAL) rhsValue = rhsValue.substring(theTest.length());
			
			// check that the FHIR search test is supported for the resource
			Vector<String> allowedFhirConditions = new Vector<String>();
			StringTokenizer sf = new StringTokenizer(fhirConditionString,";");
			while (sf.hasMoreTokens()) allowedFhirConditions.add(sf.nextToken());
			if (!GenUtil.inVector(theTest, allowedFhirConditions)) 
					throw new MapperException("FHIR search test '" + theTest
							+ "' is not supported for resource " + resourceName + ", search on " + param);
			
			String condition = "=";
			if (searchType.equals("date"))
			{
				// date equality is a test of interval overlap, which can be done with 'startsWith' on the date String.
				if (FHIRTest == EQUAL) condition = "startsWith";
				// date ranges are done by 'before' and 'after' tests on the date string
				if (FHIRTest == BEFORE) condition = "before";
				if (FHIRTest == AFTER) condition = "after";
			}
			
			// non-exact string search: the '=' test means 'contains', and ignores case
			if ((searchType.equals("string")) && (!param.endsWith(":exact")) && (FHIRTest == EQUAL))  condition = "contains";
			
			// for reference searches, the '=' test condition is what we want; no change needed

			// add the condition to the object query. Paths begin with the resource class name.
			newQuery = newQuery + resourceName + "." + path + " " + condition + " '" + rhsValue + "' AND ";
		}
		
		return newQuery;	
	}
	
	/**
	 * 
	 * @param param
	 * @param searches
	 * @return
	 * @throws MapperException
	 */
	private String[] getSearchPathsAndType(String param, Vector<String[]> searches) throws MapperException
	{
		// check that the LHS is a supported search, and find the paths it requires
		String paths = null;
		String searchType = null;
		String fhirConditionString = null;
		String solo = null;
		for (int i = 0; i < searches.size(); i++)
		{
			String[] searchRow = searches.get(i);
			String lhs = getValue(searchRow,"LHS");
			if (param.equals(lhs)) 
			{
				searchType = getValue(searchRow,"type");
				paths = getValue(searchRow,"paths");
				fhirConditionString = getValue(searchRow,"conditions");
				solo = getValue(searchRow,"solo");;
			}
		}
		if (paths == null) throw new MapperException("Server " + servlet.serverName() + ", resource " + servlet.resourceName() 
				+ " does not support search '" + param + "'");
		
		String[] result = new String[4];
		result[0] = paths;
		result[1] = searchType;
		result[2] = fhirConditionString;
		result[3] = solo;
		return result;
	}
	
	/**
	 * 
	 * @param searchRow
	 * @param col
	 * @return the value of a naemd column in a row of the searches table
	 * @throws MapperException
	 */
	private String getValue(String[]searchRow, String col) throws MapperException
	{
		int cc = -1;
		for (int c =0 ; c < servlet.searchColHeaders().length;c++) 
			if (col.equals(servlet.searchColHeaders()[c])) cc = c;
		if (cc == -1) throw new MapperException("Searches table has no column '" + col + "'");
		return searchRow[cc];
	}
	
	/**
	 * check if the mappings of the database support the search
	 * @param path the path , at the end of which there should be a property mapping
	 * @param refType for types reference searches, the type expected (not checked against mappings yet; 
	 * could be checked if the 'reference' property mapping were annotated with the type of resource referred to)
	 * @throws MapperException
	 */
	private void checkMappings(String resourceName,String path,String refType) throws MapperException
	{
		MDLBase mdl = servlet.getReader(servlet.serverName(), servlet.resourceName());
		EClass current = getResourceClass(resourceName);
		StringTokenizer steps = new StringTokenizer(path,".");
		while (steps.hasMoreTokens())
		{
			String featName = steps.nextToken();
			EStructuralFeature feat = current.getEStructuralFeature(featName);
			if (feat == null) throw new MapperException("No feature '" + featName + "' in search path " + path + " from resource class " + resourceName);

			String currentClassName = ModelUtil.getQualifiedClassName(current);
			// all steps except the last step are EReferences - check the association mapping exists
			if (steps.hasMoreTokens())
			{
				EClass next = (EClass)((EReference)feat).getEType();
				String nextClassName = ModelUtil.getQualifiedClassName(next);
				if (!mdl.representsAssociationRoleLocally(currentClassName, featName, nextClassName)) 
					throw new MapperException("Database has no mapping for link '" + featName + "' in path " + path +  " from resource class " + resourceName);
				current = next;
			}
			// the last step is an EAttribute
			else
			{
				if (!mdl.representsPropertyLocally(currentClassName, featName))
					throw new MapperException("Database has no mapping for property '" + featName + "' via path " + path +  " from resource class " + resourceName);
			}
		}
	}
	
	
	/**
	 * 
	 * @param resourceName
	 * @return
	 * @throws MapperException
	 */
	private EClass getResourceClass(String resourceName) throws MapperException
	{
		EClass resourceClass = ModelUtil.getNamedClass(classModel, "resources." + resourceName);
		if (resourceClass == null) throw new MapperException("Cannot find resource class for " + resourceName);
		return resourceClass;
	}
	

	
	/**
	 * 
	 */
	private void writeParams()
	{
		for (Iterator<String> it = params.keySet().iterator(); it.hasNext();)
		{
			String param = it.next();
			String[] values = params.get(param);
			String pVal  = param;
			for (int i = 0; i < values.length;i++)
				pVal = pVal + "\t" + values[i];
			message("Parameter and values: " + pVal);
		}
		
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	//                                        odds & sods
	// ----------------------------------------------------------------------------------------------------
	


	
	private void message(String s) {servlet.message(s);}



}
