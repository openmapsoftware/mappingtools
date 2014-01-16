package com.openMap1.mapper.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XMLUtil;

public class FHIRClient {
	
	private String baseURL;
	
	public FHIRClient(String baseURL)
	{
		this.baseURL = baseURL;
	}
	
	/**
	 * send a FHIR search and return the XML response
	 * @param search the search  or read string, e.g 'Patient/24'
	 * @return
	 * @throws MapperException
	 */
	public Element getFHIRResponse(String search) throws MapperException
	{
		Element root = null;
		try
		{
			URL url = new URL(baseURL + search);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        InputStream stream = conn.getInputStream();
			root = XMLUtil.getRootElement(stream);
		}
		catch (IOException ex) {throw new MapperException(ex.getMessage());}
		return root;
	}

}
