package com.openMap1.mapper.fhir.server;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XMLUtil;

public class Conformance {
	
	private FHIRServlet servlet;
	
	public Conformance(FHIRServlet servlet)
	{
		this.servlet = servlet;
	}
	
	/**
	 * 
	 * @return an XML DOM of the resource table and search table extracts for the current server
	 * @throws MapperException
	 */
	public Element makeConfigDom() throws MapperException
	{
		String serverName = servlet.serverName();
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
		String[] details = servlet.getServerDetails(serverName);
		for (int i = 0; i < details.length;i++)
		{
			String colName = servlet.serverColHeaders()[i];
			Element colEl = XMLUtil.textElement(doc, colName, details[i]);
			serverRecord.appendChild(colEl);
		}

		Hashtable<String,String[]> resourceDetails = servlet.getResourceDetailsForServer(serverName);
		servlet.message("resources: " + resourceDetails.size());
		for (Enumeration<String> en = resourceDetails.keys();en.hasMoreElements();)
		{
			String resourceName = en.nextElement();
			Element resourceRecord = XMLUtil.newElement(doc, "record");
			resources.appendChild(resourceRecord);
			details = resourceDetails.get(resourceName);
			for (int i = 0; i < details.length;i++)
			{
				String colName = servlet.resourceColHeaders()[i];
				Element colEl = XMLUtil.textElement(doc, colName, details[i]);
				resourceRecord.appendChild(colEl);
			}
			
			Vector<String[]> allSearchDetails = servlet.getSearches(serverName, resourceName);
			if (allSearchDetails != null) for (int s = 0; s < allSearchDetails.size();s++)
			{
				String[] searchDetails = allSearchDetails.get(s);
				Element searchRecord = XMLUtil.newElement(doc, "record");
				searches.appendChild(searchRecord);
				for (int d = 0; d < searchDetails.length;d++)
				{
					String colName = servlet.searchColHeaders()[d];
					Element colEl = XMLUtil.textElement(doc, colName, searchDetails[d]);
					searchRecord.appendChild(colEl);
				}
			}
		}
		
		return root;
	}
	
	
	

}
