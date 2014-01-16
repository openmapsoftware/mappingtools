package com.openMap1.mapper.structures;

import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.TemplateFilter;

/**
 * This interface is required for classes used to transform
 * data on the way 'in' before reading with mappings, or 'out'
 * after writing by mapping-generated WProc files.
 *  
 * @author robert
 *
 */
public interface MapperWrapper {
	
	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	public int transformType();
	
	/**
	 * 
	 * @return the file extension of the outer document, with initial '*.'
	 */
	public String fileExtension();
	
	/**
	 * 
	 * @param incoming; must be of class Element or InputStream
	 * @return the result of the in wrapper transform
	 */
	public Document transformIn(Object incoming) throws MapperException;

	
	/**
	 * @param outgoing the root element produced by the XMLWriter when 
	 * writing out from a class model instance (seen through an objectGetter)
	 * @return the result of the out wrapper transform; 
	 * must be of class Document or OutputStream
	 */
	public Object transformOut(Element outgoing) throws MapperException;
	
	/**
	 * @param xout the XSL output file
	 * @param templateFilter has a boolean method to say if a template should be included
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'in' direction.
	 * Templates must have mode = "inWrapper"
	 * @throws MapperException
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException;

	/**
	 * @param xout the XSL output file
	 * @param templateFilter has a boolean method to say if a template should be included
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'out' direction.
	 * Templates must have mode = "outWrapper"
	 * @throws MapperException
	 */
	public void addWrapperOutTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException;
	
	/**
	 * make a standalone wrapper XSLT file 
	 * @param xout an empty xsl output file which has been created, and will be written to a location
	 * after the end of this call
	 * @param isInWrapper - true if it is to be an in-wrapper transform, false for an out-wrapper transform
	 */
	public void makeStandaloneWrapperXSLT(XSLOutputFile xout, boolean isInWrapper)  throws MapperException;
	
	
	/**
	 * When certain XML subtrees are to be passed through a translation
	 * unchanged, they are indexed by a string key by an input wrapper transform
	 * and then made available to an output wrapper transform
	 * @return a table of subtrees, with random string keys
	 */
	public Hashtable<String,Element> keptSubtrees();
	
	/**
	 * for the CDA API for Java, it is necessary to be able t oset up the table
	 * converting string keys to XML subtrees, as if an XML instance had been
	 * read in
	 * @param keptSubtrees
	 */
	public void setKeptSubtrees(Hashtable<String,Element> keptSubtrees);
	
	/*
	 * When a wrapper is acting as an output wrapper, it may need access to the input reader's wrapper instance. 
	 * So when the same output wrapper may be used with several different input wrappers (as in a translation test)
	 * there is a need to refresh the input wrapper - which is passed to it inside an MDLXOReader inside the 'spare'
	 * object argument of the constructor for AbstractMapperWrapper
	 *
	 */
	public void resetSpareArgument(Object spare) throws MapperException;
	
	


}
