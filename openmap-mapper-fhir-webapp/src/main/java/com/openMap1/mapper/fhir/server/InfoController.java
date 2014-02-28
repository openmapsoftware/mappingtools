package com.openMap1.mapper.fhir.server;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InfoController {
	
	@Autowired
	private FhirController fhirController;

	@RequestMapping("/")
	public String infoHandler(HttpServletRequest request, Model model) {
		// TODO Improve
		String relBaseUrl = request.getContextPath()+request.getServletPath()+fhirController.getControllerPath();
		relBaseUrl = relBaseUrl.replace("//", "/");
		model.addAttribute("baseUrl", relBaseUrl);
		return "demo";
	}
}
