package com.openMap1.mapper.writer;

import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.openMap1.mapper.core.MDLWriteException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.RunIssue;

import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.util.Timer;
import com.openMap1.mapper.util.XMLOutputFile;

/**
 * the capability of writing XML, eg using wproc files.
 *  
 * @author robert
 *
 */
public interface XMLWriter {

    
    /**
     * set the root of the XML instance being read
     * @param el
     * @throws MapperException
     */
         public void setInputRoot(Node el)  throws MapperException;

	/**
	 * write the object model information from the objectGetter (set in the constructor) 
	 * to an output XML
	 *
	 * @return the root Element of the created XML document
	 * @exception MDLWriteException - any major problem detected in making the translation
	 */
	public Element makeXMLDOM() throws MapperException;
	
	/**
	 * Extend some Element of an output XML DOM (which represents some object
	 * in the object model, or has an ancestor element which represents that object)
	 * producing a subtree which represents the properties of that object, subordinate
	 * objects related to it, and their properties.
	 * 
	 * @param bareElement the Element to be extended
	 * @param oTok objectToken for the parameter class object, which the Element 
	 * to be extended (or one of its ancestors) represents
	 * @return the extended Element
	 * @throws MapperException if there is any major problem
	 */
	public Element extendXMLDOM(Element bareElement, objectToken oTok) throws MapperException;

	
	/**
	 * All issues that were noted when running the translation 
	 * or generating XSLT. 
	 * outer key = string form of root path
	 * Inner key = a unique identifier for the issue
	 * @return
	 */
	public Hashtable<String,Hashtable<String,RunIssue>> allRunIssues();
	
    /**
     * set the XML Output file for the writer
     * @param xout
     */
	public void setXMLOutputFile(XMLOutputFile xout);
	
	
	/**
	 * pass a timer to the XMLWriter, so timings can be written elsewhere
	 * @param timer
	 */
	public void giveTimer(Timer timer, boolean addTimes);


}
