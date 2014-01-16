package com.openMap1.mapper.util;

import java.util.Vector;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XMLUtil;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.w3c.dom.*;

/**
 * class for XML files being read.
 * 
 * @author robert
 *
 */
public class XMLInputFile extends XMLFile {
	
	private Element root;
	public Element root() {return root;}
	
	//--------------------------------------------------------------------------------
	//                           Constructor
	//--------------------------------------------------------------------------------
	
	/**
	 * Ensure that every XMLFile has a NamespaceSet, initially with no namespaces in it
	 */
	public XMLInputFile()
	{
		super();
	}
	
	/**
	 * set the root Element,  if the XML is not being read in now
	 * @param el
	 */
	public void setRootElement (Element el)
	{
		root = el;
	}
	
	
	
	/**
	 * read an XML file at a given file location
	 * @param fileName
	 * @throws MapperException
	 */
	public void readXMLFile(String fileName) throws MapperException
	{
		root = XMLUtil.getRootElement(fileName);
	}

	
	/**
	 * read an XML file from an input stream
	 * @param stream
	 * @throws MapperException
	 */
	public void readXMLFile(InputStream stream) throws MapperException
	{
		root = XMLUtil.getRootElement(stream);
	}
	
	/**
	 * read an XML file from an Eclipse IFile
	 * @param file
	 * @throws MapperException
	 */
	public void readXMLFile(IFile file) throws MapperException
	{
		try{
		root = XMLUtil.getRootElement(file.getContents());
		}
		catch (Exception ex){throw new MapperException("Failed to read XML IFile: " + ex.getMessage());}
	}


    /**
     * do a deep search of an element tree for all elements of given tag name
     *
     * @param el  Element: the element which is the root of the tree
     * @param tag String: the tag name we are looking for
     * @return Vector: Vector of all elements in the tree with the tag name as its local name
     */
    public Vector<Element> deepSearch(Element el, String tag) {
        Vector<Element> res = new Vector<Element>();
        if ((el.getLocalName() != null) && (el.getLocalName().equals(tag))) res.addElement(el);
        NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node nd = nl.item(i);
            if (nd instanceof Element) {
                Vector<Element> cr = deepSearch((Element) nd, tag);
                for (int j = 0; j < cr.size(); j++) {
                    res.addElement(cr.elementAt(j));
                }
            }
        }
        return res;
    }


}
