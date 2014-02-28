package com.openMap1.mapper.fhir.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="An error occured")
public class OpenMapsException extends RuntimeException {
	
	public OpenMapsException(String msg) {
		super(msg);
	}

}
