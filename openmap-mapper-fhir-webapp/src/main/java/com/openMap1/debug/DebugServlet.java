package com.openMap1.debug;

import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DebugServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        
        URL[] urls = ((URLClassLoader)cl).getURLs();
 
        for(URL url: urls){
        	System.out.println(url.getFile());
        }
	}

}
