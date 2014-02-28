package com.openMap1.mapper.fhir.server;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.instance.formats.Composer;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

@Controller
@RequestMapping(FhirController.PATH)
public class FhirController {

	public final static String PATH = "/fhir";
	
	private static enum SupportedMethod {SIMPLEREAD,CONFORMANCE,SEARCH};
	private static enum SupportedResource {PATIENT,CONFORMANCE};
	
	@Autowired
	private FhirService service;

	@RequestMapping(value = "**", method = RequestMethod.GET)
	public @ResponseBody
	void handle(HttpServletRequest request, @RequestParam Map<String,String> requestParameters, HttpServletResponse response) throws Exception {		
		// Extract useful components from URL
		String pathWithinServlet = (String) request
				.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		StringTokenizer st = new StringTokenizer(pathWithinServlet, "/");
		String controllerPath;
		String[] databases;
		String resourceName;
		String resourceId = null;
		try {
			controllerPath = st.nextToken();
			String rawDatabases = st.nextToken();
			databases = rawDatabases.split("\\+");
			resourceName = st.nextToken();
			if (st.hasMoreTokens()) {
				 resourceId = st.nextToken();
			}
		} catch (NoSuchElementException e) {
			throw new OpenMapsException("Malformed URL");
		}
		
		// Get operation
		SupportedMethod operation = null;
		if (resourceId != null) {
			operation = SupportedMethod.SIMPLEREAD;
		} else if (resourceName.equals("Conformance") || resourceName.equals("metadata")) {
			operation = SupportedMethod.CONFORMANCE;
		} else {
			operation = SupportedMethod.SEARCH;
		}
		
		// Call operation on service
		if (operation.equals(SupportedMethod.SIMPLEREAD)) {
			Resource resource = service.processSimpleRead(databases[0], resourceName, resourceId);
			returnResponse(resource, response);
		} else if (operation.equals(SupportedMethod.CONFORMANCE)) {
			Resource resource = service.processConformanceOperation(databases[0]);
			returnResponse(resource, response);
		} else if (operation.equals(SupportedMethod.SEARCH)) {
			AtomFeed feed = service.processSearch(databases, resourceName, requestParameters);
			returnResponse(feed, response);
		}
		
	}
	
	public void returnResponse(Resource r, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml");			
        ServletOutputStream so = response.getOutputStream();
		Composer composer = new XmlComposer();
		composer.compose(so, r, true);
	}
	
	public void returnResponse(AtomFeed f, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml");			
        ServletOutputStream so = response.getOutputStream();
		Composer composer = new XmlComposer();
		composer.compose(so, f, true);
	}

	public String getControllerPath() {
		return FhirController.PATH;
	}

}
