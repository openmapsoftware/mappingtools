package com.openMap1.mapper.fhir.server;

import java.util.Map;

import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Resource;

import com.openMap1.mapper.core.MapperException;

public interface FhirService {

	public Resource processSimpleRead(String serverName, String resourceName, String resourceId) throws MapperException;
	
	public Resource processConformanceOperation(String serverName) throws MapperException;
	
	public AtomFeed processSearch(String[] serverNames, String resourceName, Map<String, String> parameters) throws MapperException;
}
