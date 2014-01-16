package com.openMap1.mapper.util;

import com.openMap1.mapper.core.NamespaceSet;
import java.util.*;

import org.w3c.dom.*;

/**
 * Class for the construction and use of XML output documents
 * @author robert
 *
 */
public class XMLFile {
	
	private NamespaceSet NSSet;
	public NamespaceSet NSSet() {return NSSet;}
	public void setNSSet(NamespaceSet NSSet) {this.NSSet = NSSet;}
	
	//--------------------------------------------------------------------------------
	//                           Constructor
	//--------------------------------------------------------------------------------
	
	/**
	 * Ensure that every XMLFile has a NamespaceSet, initially with no namespaces in it
	 */
	public XMLFile()
	{
		NSSet = new NamespaceSet();
	}
	
	//--------------------------------------------------------------------------------
	//                     Convenience access methods
	//--------------------------------------------------------------------------------
			
	public Element singleChild(Element el, String name)
	{return XMLUtil.firstNamedChild(el, name);}
		
	public Vector<Element> namedChildElements(Element el, String name)
	{
		return XMLUtil.namedChildElements(el, name);
	}
	
	public String getLocName(Element el)
	{
		return el.getTagName();
	}
	
	public Vector<Element> childElements(Element el) {return XMLUtil.childElements(el);}
	

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
