package com.openMap1.mapper.util;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.eclipse.core.resources.IFile;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.core.namespace;

/**
 * class for XML files being written out.
 * 
 * @author robert
 *
 */

public class XMLOutputFile extends XMLFile{
	
	private Document outDoc;
	public Document outDoc() {return outDoc;}
	
	public Element topOut() {return topOut;}
	private Element topOut;
	
	//--------------------------------------------------------------------------------
	//                           Constructor
	//--------------------------------------------------------------------------------

	/**
	 * make a new XML output Document (without saying where it will be written to)
	 */
	public XMLOutputFile()
	{
		super(); // to ensure it has a NamespaceSet (initially empty)
		try{
		outDoc = XMLUtil.makeOutDoc();
		}
		catch (XMLException ex) {GenUtil.surprise(ex, "XMLOutputFile.constructor");}
	}
	
	/**
	 * set an Element el (which has been made by this XMLOutFile instance) to be the top 
	 * Element of the document.
	 * @param el the Element which is to be the root of the Document
	 */
	public void setTopOut(Element el) 
	{
		topOut = el;
		outDoc.appendChild(topOut);
	}


	/**
	 * make a new element in this output file, without a namespace and without text content -
	 * not yet attached to any other Element
	 * @param name: name of the Element
	 * @return the newly made Element
	 */
	public Element newElement(String name) 
	{
		Element el = null;
		try {
			el =  XMLUtil.newElement(outDoc, name);
		}
		catch (XMLException ex) {GenUtil.surprise(ex, "XMLOutputFile.newElement");}
		return el;
	}
	
	/**
	 * make a new element in this output file, with a namespace prefix and without text content -
	 * not yet attached to any other Element	 * 
	 * @param prefix the namespace prefix 
	 * @param localName the name after the prefix and ':'
	 * @param URI the namespace URI
	 * @return the new Element
	 */
	public Element NSElement(String prefix, String localName, String URI)
	{
		Element el = null;
		try {
			el = XMLUtil.NSElement(outDoc, prefix, localName, URI);
		}
		catch (XMLException ex) {GenUtil.surprise(ex, "XMLFile.NSElement");}
		return el;
	}
	
	/**
	 * make a new element in this output file, without a namespace and with text content -
	 * not yet attached to any other Element
	 * @param name: name of the Element
	 * @param text the text content
	 * @return the newly made Element
	 */
	public Element textElement(String name, String text)
	{
		Element el = null;
		try {
			el = XMLUtil.textElement(outDoc, name, text);
		}
		catch (XMLException ex) {GenUtil.surprise(ex, "XMLFile.textElement");}
		return el;
	}

	/**
	 * write the output to a file. 
	 * Note that this file will not be visible to Eclipse.
	 * @param fileName
	 * @throws XMLException
	 */
	public void writeOutput(String fileName) throws XMLException
	{
		XMLUtil.writeOutput(outDoc, fileName, true);
	}
	
	/**
	 * write the XML Document to an IFile, making the result visible to Eclipse.
	 * The IFile may either be a handle (exists() == false) or a newly created file
	 * (exists() == true) with no contents.
	 * @param file the IFile
	 * @throws XMLException
	 */
	public void writeOutput(IFile file) throws MapperException
	{
		EclipseFileUtil.writeOutputResource(outDoc, file, true);
	}
	
	/**
	 * Add the namespace declarations to the top Element of the document
	 */
	public void addNamespaceAttributes() 
	{
		if (topOut() != null) 
		{
			for (int i = 0; i < NSSet().size();i++)
			{
				namespace ns = NSSet().getByIndex(i);
				String attName = "xmlns";
				if (!(ns.prefix().equals(""))) attName = attName + ":" + ns.prefix();
				topOut().setAttribute(attName, ns.URI());
			}
		}
		else System.out.println("Null top element when adding namespace attributes");
	}

}
