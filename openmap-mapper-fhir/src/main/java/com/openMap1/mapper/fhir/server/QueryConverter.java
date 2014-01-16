package com.openMap1.mapper.fhir.server;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.mapping.MDLBase;
import com.openMap1.mapper.mapping.AssociationMapping;
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
	
	// the resource class
	private EClass resourceClass;
	
	// parameters of the FHIR search, got from the url
	private Map<String,String[]> params;
	
	/* searches supported for this resource, provided the database has the required mappings.
	 * One search row has columns: server,resource,LHS,type,conditions */
	private Vector<String[]> searches;
	
	public QueryConverter(FHIRServlet servlet,  Map<String,String[]> params, Vector<String[]> searches) throws MapperException
	{
		this.servlet = servlet;
		this.params = params;
		this.searches = searches;
		
		classModel = servlet.getMappedStructure(servlet.serverName(), servlet.resourceName()).getClassModelRoot();
		resourceClass = ModelUtil.getNamedClass(classModel, "resources." + servlet.resourceName());
		
		writeParams();
	}
	
	
	/**
	 * 
	 * @return the object query needed to do the search
	 * @throws MapperException
	 */
	public String queryString() throws MapperException
	{
		// first query just returns FHIR ids for eligible resources
		String query = "Select " + servlet.resourceName() + ".fhir_id Where ";

		// loop over different conditions in the search
		for (Iterator<String> it = params.keySet().iterator(); it.hasNext();)
		{
			// LHS of FHIR search condition
			String param = it.next();
			
			// RHS of FHIR search condition
			String[] values = params.get(param);
			if (values.length > 1) throw new MapperException("Multi-value conditions are not yet handled");
			String value = values[0];
			// token type searches can have one or two values that need to be checked
			StringTokenizer ss = new StringTokenizer(value,"|");
			int valuesToTest = ss.countTokens(); // 1, or may be 2 for a token search . Then the 2 values are [system uri,value]

			// check that the LHS is a supported search, and find the paths it requires
			String paths = null;
			String searchType = null;
			for (int i = 0; i < searches.size(); i++)
			{
				String[] searchRow = searches.get(i);
				String lhs = searchRow[2];
				if (param.equals(lhs)) 
				{
					searchType = searchRow[3];
					paths = searchRow[4];
				}
			}
			if (paths == null) throw new MapperException("Server " + servlet.serverName() + ", resource " + servlet.resourceName() + " does not support search '" + param + "'");
			
			// check that all paths needed for the condition are mapped; token searches will need two paths if the RHS contains '|'
			StringTokenizer st = new StringTokenizer(paths,"; ");
			int pathsDefined = st.countTokens(); // 2 for a token search [system, value], 1 for all other searches
			if ((pathsDefined > 1) && (!searchType.equals("token"))) throw new MapperException("Only one path for a non-token search");
			// iterate over 1 or 2 paths, matching up with RHS values
			while (st.hasMoreTokens())
			{
				String path = st.nextToken();
				// if there are 2 paths and only one value to test, use the second path (the value path, not the uri path) to test it
				if (pathsDefined > valuesToTest) path = st.nextToken();
				checkMappings(path);
				
				String rhsValue = ss.nextToken();
				query = query + servlet.resourceName() + "." + path + " =  '" + rhsValue + "' AND ";
			}
		}
		// remove the last " AND "
		query = query.substring(0, query.length() - 5);
		message(query);
		return query;
	}
	
	/**
	 * check if the mappings of the database support the search
	 * @param path
	 * @throws MapperException
	 */
	private void checkMappings(String path) throws MapperException
	{
		MDLBase mdl = servlet.getReader(servlet.serverName(), servlet.resourceName());
		EClass current = resourceClass;
		StringTokenizer steps = new StringTokenizer(path,".");
		while (steps.hasMoreTokens())
		{
			String currentClassName = ModelUtil.getQualifiedClassName(current);
			String featName = steps.nextToken();
			EStructuralFeature feat = current.getEStructuralFeature(featName);
			if (feat == null) throw new MapperException("No feature " + featName + " for search path " + path);
			// all steps except the last step are EReferences - check the association mapping exists
			if (steps.hasMoreTokens())
			{
				EClass next = (EClass)((EReference)feat).getEType();
				String nextClassName = ModelUtil.getQualifiedClassName(next);
				if (!mdl.representsAssociationRoleLocally(currentClassName, featName, nextClassName)) 
					throw new MapperException("Database has no mapping for link '" + featName + "'");
				current = next;
			}
			// the last step is an EAttribute
			else
			{
				if (!mdl.representsPropertyLocally(currentClassName, featName))
					throw new MapperException("Database has no mapping for property '" + featName + "'");
			}
		}
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
