package com.openMap1.mapper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xmi.util.XMLProcessor;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.SchemaMismatch;
import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.core.namespace;


/**
 * A collection of XML utilities, all static.
 * 
 * @author robert
 *
 */
public class XMLUtil {
	
	/**
	 * W3C defined URIs
	 */
	public static String SCHEMAURI = "http://www.w3.org/2001/XMLSchema";
    public static String SCHEMAINSTANCEPREFIX = "xsi";
	public static String SCHEMAINSTANCEURI = "http://www.w3.org/2001/XMLSchema-instance";
    public static String XSLURI = "http://www.w3.org/1999/XSL/Transform";
    
    public static String XMIURI = "http://www.omg.org/XMI";
    public static String XMISchemaLocation = "unknown";
	
	
	/**
	 * get the name of an XML element, with the namespace prefix stripped off
	 * @param el
	 * @return
	 */
	public static String getLocalName(Node el)
	{
		String locName = "";
		StringTokenizer st = new StringTokenizer(el.getNodeName(),":");
		while (st.hasMoreTokens()) locName = st.nextToken();
		return locName;
	}
	

    /**
     * Vector of child elements of an element
     */
    public static Vector<Element> childElements(Element el) {
        Vector<Element> res = new Vector<Element>();
        NodeList nodes = el.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node nd = nodes.item(i);
            if (nd instanceof Element) {
                Element eg = (Element) nd;
                res.addElement(eg);
            }
        }
        return res;
    }
    
    /**
     * return the Vector of child Elements with given local name
     * @param el
     * @param name
     * @return
     */
    public static Vector<Element> namedChildElements(Element el, String lName)
    {
    	Vector<Element> nc = new Vector<Element>();
    	for (Iterator<Element> it = childElements(el).iterator(); it.hasNext();)
    	{
    		Element en = it.next();
    		if (getLocalName(en).equals(lName)) nc.addElement(en);
    	}
    	return nc;
    }
    
    /**
     * return the first child of the element with given name, or null if there are none
     * @param el
     * @param lName
     * @return
     */
    public static Element firstNamedChild(Element el, String lName)
    {
    	if (el == null) return null;
    	Element fc = null;
    	if (namedChildElements(el,lName).size() > 0) fc = namedChildElements(el,lName).elementAt(0);
    	return fc;
    }
	

    /**
     * get the  text string in an element (eg interspersed between child elements), 
     * or "" if there is none or if the Element is null.
     * Tries to ignore white space text; but does not succeed.
     */
    public static String getText(Element el) {
        String res = "";
        if (el != null) try 
        {
        	el.normalize(); // does not help recognise white space
        	NodeList nodes = el.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) 
            	if (nodes.item(i) instanceof Text)
            	{
            		Text text = (Text)nodes.item(i);
            		// this filter seems to make no difference
            		if (!text.isElementContentWhitespace())  
            		{
            			String tData = text.getData();
            			// this seems to be an effective way to catch pure white space
            			StringTokenizer nonWhiteSpace = new StringTokenizer(tData,"\n \t");
            			if (nonWhiteSpace.countTokens() > 0 )res = res + tData;
            		}
            	}
        }
        catch (Exception e) {System.out.println("Text failure: " + e.getMessage());}
        return res;
    }
    

    /**
     * return the text value of a node, which may be an Element or attribute
     */
    public static String textValue(Node n) {
        String res = null;
        if (n == null) {
            GenUtil.message("Text-holding element or attribute node is null.");
        } else {
            if (n instanceof Element) {
                res = getText((Element) n);
            } else if (n instanceof Attr) {
                res = ((Attr) n).getValue();
            } else {
            	GenUtil.message("Node is neither an element or an attribute. ");
            	GenUtil.message("Class of node: " + n.getClass().getName());
            }
        }

        return res;
    }
    
    /**
     * 
     * @param uri
     * @return the root Element of an XML file with URI
     * @throws MapperException
     */
    public static Element getRootElement(URI uri) throws MapperException
    {
    	String location = FileUtil.editURIConverter().normalize(uri).toFileString();
    	return getRootElement(location);
    }

	
	/**
	 * return the root Element of an XML file with given location
	 * @param location
	 * @return Element the root element, or null if there is any problem
	 */
	public static Element getRootElement(String location) throws MapperException
	{
		Element root = null;
        try {
            FileInputStream fi = new FileInputStream(location);
            DocumentBuilderFactory builderFac = DocumentBuilderFactory.newInstance();
            builderFac.setNamespaceAware(true);
            root = builderFac.newDocumentBuilder().parse(fi).getDocumentElement();
        }
        catch (SAXException ex) {notify(location,ex);}
        catch (FileNotFoundException ex) {notify(location,ex);}
        catch (IOException ex) {notify(location,ex);}
        catch (ParserConfigurationException ex) {notify(location,ex);}
		return root;
	}
	
	/**
	 * return the root Element of an XML file in a given inputStream
	 * @param location
	 * @return Element the root element, or null if there is any problem
	 */
	public static Element getRootElement(InputStream fi) throws MapperException
	{
		return getDocument(fi).getDocumentElement();
	}
	
	/**
	 * return the Document of an XML file in a given inputStream
	 * @param location
	 * @return Element the root element, or null if there is any problem
	 */
	public static Document getDocument(InputStream fi) throws MapperException
	{
		Document doc = null;
        try {
            DocumentBuilderFactory builderFac = DocumentBuilderFactory.newInstance();
            builderFac.setNamespaceAware(true);
            doc = builderFac.newDocumentBuilder().parse(fi);
        }
        catch (SAXException ex) {notify("Input Stream",ex);}
        catch (FileNotFoundException ex) {notify("Input Stream",ex);}
        catch (IOException ex) {notify("Input Stream",ex);}
        catch (ParserConfigurationException ex) {notify("Input Stream",ex);}
		return doc;
	}
	
	/**
	 * return the Document of an XML file in a given inputStream
	 * @param location
	 * @return Element the root element, or null if there is any problem
	 */
	public static Document getDocument(String location) throws MapperException
	{
		try{
            FileInputStream fi = new FileInputStream(location);
            return getDocument(fi);
		}
        catch (IOException ex) {notify("Input Stream",ex);}
        return null;
	}


	private static void notify(String location, Exception ex) throws MapperException
	{
		throw new MapperException ("Exception getting XML root element from "
				+ location + "; " + ex.getMessage());
	}

	//--------------------------------------------------------------------------------------
    //	                              Output File handling
	//--------------------------------------------------------------------------------------

	    // make an output XML document

	    public static Document makeOutDoc() throws XMLException
	    {
	    	Document outDoc = null;
	        try {
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            dbf.setNamespaceAware(true);
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            outDoc = db.newDocument();
	        }
	        catch (ParserConfigurationException pce) {
	            throw new XMLException("Parser Config exception: " + pce.getMessage());
	        }
	        return outDoc;
	    }


		//--------------------------------------------------------------------------------------
	    //	                              Writing HTML
		//--------------------------------------------------------------------------------------
	    
	    public static Document makeHTMLOutDoc() throws XMLException
	    {
	    	Document outDoc = null;
	        try {
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            dbf.setNamespaceAware(true);
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            DOMImplementation domImpl = db.getDOMImplementation();
	            DocumentType docType = domImpl.createDocumentType("html",
	            		"-//W3C//DTD XHTML 1.0 Transitional//EN",                      // public identifier
	            		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");    // system identifier
	            outDoc = domImpl.createDocument("namespaceuri", "html", docType);
	        }
	        catch (Exception ex) {
	        	ex.printStackTrace();
	            throw new XMLException("Parser Config exception: " + ex.getMessage());
	        }
	        return outDoc;
	    }


		//--------------------------------------------------------------------------------------
	    //	                              Writing XML
		//--------------------------------------------------------------------------------------


	    /**
	     * write the XML output to the named file
	     * @param fileName the filename to write to
	     * @param outDoc the XML document to be written
	     * @param isFormatted if true, put in line breaks and indenting
	     * @throws XMLException if anything goes wrong
	     */
	    public static void writeOutput(Document outDoc, String fileName, boolean isFormatted)
	    throws XMLException
	    {
	        if (isFormatted) {
	            addFormatting(outDoc);
	        }
	        try {
	            TransformerFactory tf = TransformerFactory.newInstance();
	            Transformer t = tf.newTransformer();
	            Source src = new DOMSource(outDoc);
	            Result dest = new StreamResult(new File(fileName));
	            t.transform(src, dest);
	        }
	        catch (TransformerConfigurationException tce) {
	        	throw new XMLException("Transform Config exception: " + tce.getMessage());
	        }
	        catch (TransformerException te) {
	            throw new XMLException("Transform exception: " + te.getMessage());
	        }
	    }


	    /**
	     * write the XML output to the output stream
	     * @param Document Doc the XML document to be written out
	     * @param OutputStream stream the output stream to write it to
	     * @param boolean formatted : true if you want sensible line breaks and indents
	     */
	    public static void writeToStream(Document Doc, OutputStream stream, boolean formatted) 
	    throws XMLException
	    {
	        if (formatted) {
	            addFormatting(Doc,Doc.getDocumentElement(),"");
	        }
	        try {
	            TransformerFactory tf = TransformerFactory.newInstance();
	            Transformer t = tf.newTransformer();
	            Source src = new DOMSource(Doc);
	            Result dest = new StreamResult(stream);
	            t.transform(src, dest);
	        }
	        catch (TransformerConfigurationException tce) {
	            throw new XMLException("Transform Config exception: " + tce.getMessage());
	        }
	        catch (TransformerException te) {
	        	throw new XMLException("Transform exception: " + te.getMessage());
	        }
	    }

	    /**
	    * write the XML output to the named file
	    * @param fileName the filename to write to
	    * @param xslFilename the file to use to transform
	    */
	   public static void writeFormattedOutput(Document outDoc, String fileName, String xslFilename )
	   throws XMLException 
	   {
	       try {
	           StreamSource xsltSource = new StreamSource(new FileInputStream(xslFilename));
	           Result dest = new StreamResult(new File(fileName));
	           writeFormattedOutputFromXSLSource( outDoc, dest,  xsltSource);
	       } catch (FileNotFoundException e) {
	           throw new XMLException("Transform file not found exception: " + e.getMessage());
	       }
	   }

	    /**
	    * write the XML output to the named file
	    * @param fileName the filename to write to
	    * @param xslStream input stream of the xsl  to use to transform
	    */
	   public static void writeTransformedOutput(Document outDoc, String fileName, InputStream xslStream )
	   throws XMLException 
	   {
	           StreamSource xsltSource = new StreamSource(xslStream);
	           Result dest = new StreamResult(new File(fileName));
	           writeFormattedOutputFromXSLSource( outDoc, dest,  xsltSource);
	   }


	   /**
	   * create the XSLT transformed output
	   * @param xslNode the top node of the XSL DOM file used to transform
	   * @return the top node of the transformed DOM
	   */
	  public static Node makeTransformedOutput(Document outDoc, Node xslNode ) throws XMLException
	  {
	          DOMSource xsltSource = new DOMSource(xslNode);
	          DOMResult dest = new DOMResult();
	          writeFormattedOutputFromXSLSource(outDoc,dest, xsltSource);
	          return dest.getNode();
	  }

	    /**
	     * write the XML output to the named file
	     * @param dest the Result to write to
	     * @param xsltSource the xslt to use to transform
	     */
	    public static void writeFormattedOutputFromXSLSource(Document outDoc, Result dest, Source xsltSource ) 
	    throws XMLException
	    {
	        try {
	            TransformerFactory tf = TransformerFactory.newInstance();
	            Source src = new DOMSource(outDoc);
	            Templates xslt = tf.newTemplates(xsltSource);
	            // Apply the xsl file to the source file and write the result to the output file
	            Transformer trans = xslt.newTransformer();
	            // Transform the XML
	            trans.transform(src, dest);
	        }
	        catch (TransformerConfigurationException tce) {
	            throw new XMLException("Transform Config exception: " + tce.getMessage());
	        }
	        catch (TransformerException te) {
	            throw new XMLException("Transform exception: " + te.getMessage());
	        }
	    }

	    //---------------------------------------------------------------------------------------------------
	    //                                        formatting code
	    //---------------------------------------------------------------------------------------------------


	    /** This method goes through and adds formatting as necessary so that the XML is human readable
	     * TODO work out why we can't put space in front of the first element */
	    protected static void addFormatting(Document outDoc) {
	        // First we want a space before the first element
	        addFormatting(outDoc,outDoc.getDocumentElement(), "");

	    }

	    /** Goes through and adds newlines and indent to the current node and all its children
	     * @param current the current node
	     * @param indent the current level of indent this is increased recursively*/
	    private static void addFormatting(Document doc, Node current, String indent) {

	        // go through each of the children adding space as required
	        Node child = current.getFirstChild();
	        String childIndent = indent+"\t";
	        while (child != null) {
	            Node nextChild = child.getNextSibling();
	            if (child.getNodeType() != Node.TEXT_NODE) {
	                // Only if we aren't a text node do we add the space
	                current.insertBefore(doc.createTextNode("\n"+childIndent), child);
	                if (child.hasChildNodes()) {
	                    addFormatting(doc, child, childIndent);
	                }
	                if (nextChild == null) {
	                    // Because this is the last child, we need to add some space after it
	                    current.appendChild(doc.createTextNode("\n"+indent));
	                }
	            }
	            child = nextChild;
	        }
	    }

	  //--------------------------------------------------------------------------------------
//	                                XML Utilities - for writing files
	  //--------------------------------------------------------------------------------------

	      /**
	       * create an empty element in a namespace
	       *
	       * @param prefix    String: namespace prefix
	       * @param localName String: local name (after prefix  and ':')
	       * @param URI       String: namespace URI
	       * @return Element: the Element
	       */
	      public static Element NSElement(Document outDoc, String prefix, String localName, String URI)
	      throws XMLException {
	          String qualName = prefix + ":" + localName;
	          if (prefix.equals("")) qualName = localName;
	          Element en = null;
	          try {
	              en = (Element) outDoc.createElementNS(URI, qualName);
	          }
	          catch (Exception e) {
	        	  throw new XMLException ("Exception creating namespace element '" + qualName
	                      + "'. " + e.getMessage());
	          }
	          return en;
	      }

	      /**
	       * create an element in a namespace, with text content
	       *
	       * @param prefix    String: namespace prefix
	       * @param localName String: local name (after prefix  and ':')
	       * @param URI       String: namespace URI
	       * @param text      String: text content of the element
	       * @return Element: the Element
	       */
	      public static Element textNSElement(Document outDoc, String prefix, String localName, String URI, String text) 
	      throws XMLException {
	          Text t;
	          Element en = null;
	          try {
	              en = NSElement(outDoc, prefix, localName, URI);
	              t = outDoc.createTextNode(text);
	              en.appendChild(t);
	          }
	          catch (Exception e) {
	        	  throw new XMLException ("Exception creating text-filled namespace element '"
	                      + prefix + ":" + localName
	                      + "'. " + e.getMessage());
	          }
	          return en;
	      }
	      
	      
	      /**
	       * @param el
	       * @return the first text child of an element, or null  if it has none
	       */
	      public static Text firstTextChild(Element el)
	      {
	    	  Text text = null;
	    	  for (int i = 0; i < el.getChildNodes().getLength(); i++)
	    	  {
	    		  Node n = el.getChildNodes().item(i);
	    		  if (n instanceof Text) text = (Text)n;
	    	  }
	    	  return text;
	      }


	      /**
	       * create a new element in no namespace with no text content.
	       * write an error message and return null if any exception
	       *
	       * @param name String: name of the element
	       * @return Element: the element
	       */
	      public static Element newElement(Document  outDoc, String name) 
	      throws XMLException {
	          Element ei = null;
	          try {
	              ei = (Element) outDoc.createElement(name);
	          }
	          catch (Exception e) {
	              throw new XMLException ("Exception creating element '" + name
	                      + "'. " + e.getMessage());
	          }
	          return ei;
	      }

	      /**
	       * create a new element in no namespace with no text content.
	       * write an error message and return null if any exception
	       *
	       * @param name String: name of the element
	       * @param text String: the text content
	       * @return Element: the element
	       */
	      public static Element textElement(Document outDoc, String name, String text) 
	      throws XMLException {
	          Text t;
	          Element en = null;
	          try {
	              en = newElement(outDoc,name);
	              t = outDoc.createTextNode(text);
	              en.appendChild(t);
	          }
	          catch (Exception e) {
	        	  throw new XMLException ("Exception creating text-filled element '" + name
	                      + "'. " + e.getMessage());
	          }
	          return en;
	      }

	      /**
	       * add a comment below element el
	       */
	      public static void addComment(Document outDoc, Element el, String cText) {
	          Comment com = outDoc.createComment(cText);
	          el.appendChild(com);
	      }
	      
	      /**
	       * remove from a name any characters not allowed in XML tags
	       * (what are they?)
	       * @param name
	       * @return
	       */
	      public static String legalTagName(String name)
	      {
	    	  String tagName = "";
	    	  StringTokenizer st = new StringTokenizer(name," ,/;'\\");
	    	  while (st.hasMoreTokens()) tagName = tagName + st.nextToken();
	    	  return tagName;
	      }

	      /**
	       * @param location String: location of XML file
	       * @return Element: root Element of XML file
	       * @throws XMLFileOpeningException: if file cannot be found
	       */
	      public static Element readXMLFile(String location) throws XMLException {
	    	  Element root = null;
	          try {
	              FileInputStream fi = new FileInputStream(location);
	              root = readXMLFile(fi, location);
	          }
	          catch (FileNotFoundException ex) {
	              throw new XMLException("Cannot find XML file " + location);
	          }
	          return root;
	      }

	      /**
	       * Open an XML file and set its root element 'root' (an instance variable)
	       * or leave root = null if some problem.
	       */
	      public static Element readXMLFile(InputStream is, String location) throws XMLException {
	    	  Element root = null;
	          try {
	              DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	              dbf.setNamespaceAware(true);
	              DocumentBuilder db = dbf.newDocumentBuilder();
	              Document doc = db.parse(is);
	              root = doc.getDocumentElement();
	              if (root == null) {
	                  throw new XMLException("No root element in file " + location);
	              } else {
	              }
	          }
	          catch (java.io.IOException e) {
	              throw new XMLException("IO Exception opening XML file " + location);
	          }
	          catch (org.xml.sax.SAXException e) {
	              throw new XMLException("File at "
	                      + location + " is not a valid XML file. " + e.getMessage());
	          }
	          catch (Exception e) {
	              throw new XMLException("Unidentified exception opening XML file "
	                      + location + ": " + e.getMessage());
	          }
	          return root;
	      }
	      
	      
	      
	  //----------------------------------------------------------------------------------------------------
	  //                                namespaces
	  //----------------------------------------------------------------------------------------------------

	      /** 
	  	 * find all namespaces (prefix and URI) in an XML document,
	  	 * starting from the root and descending. 
	  	 * @param rootEl the root element of the document
	  	 * @return the NamespaceSet containing all namespaces
	  	 * @exception MapperException if any namespace URI is declared twice with different prefixes
	  	 */
	  	public static NamespaceSet getNameSpaceSet(Element rootEl) throws MapperException
	  	{
	  		NamespaceSet nsSet = new NamespaceSet();
	  		addNamespaceSet(rootEl,nsSet);
	  		return nsSet;
	  	}
	  	
	  	/**
	  	 * recursive descent of an XML document, picking up all namespace declarations
	  	 * @param el the current element
	  	 * @param nsSet the namespace set being built up
	  	 * @exception MapperException if any namespace URI is declared twice with different prefixes
	  	 */
	  	private static void addNamespaceSet(Element el, NamespaceSet nsSet) throws MapperException
	  	{
	  		NamedNodeMap attList = el.getAttributes();
	  		for (int i = 0; i < attList.getLength(); i++)
	  		{
	  			// find all namespace attributes on this node
	  			Node n = attList.item(i);
	  			if (n instanceof Attr)
	  			{
	  				Attr att = (Attr)n;
	  				String name = att.getName();
	  				if ((name.equals("xmlns"))|(name.startsWith("xmlns:")))
	  				{
	  					String prefix = "";
	  					if (name.length() > 6) prefix = name.substring(6);
	  					String uri = att.getValue();
	  					// if the namespace prefix has been declared already, check it was declared with the same URI
	  					namespace ns = nsSet.getByPrefix(prefix);
	  					if ((ns != null) && (!(uri.equals(ns.URI()))))
	  							throw new MapperException("XML Instance declares namespace prefix '" + prefix
	  									+ "' with uris '" + uri + "' and '" + ns.URI() + "'");
	  					else if (ns == null) nsSet.addNamespace(new namespace(prefix,uri));
	  				}
	  			}
	  		}
	  		
	  		// recurse over child elements
	  		for (int i = 0; i < el.getChildNodes().getLength();i++)
	  		{
	  			Node n = el.getChildNodes().item(i);
	  			if (n instanceof Element) addNamespaceSet((Element)n, nsSet);
	  		}
	  	}
	  	
	  	/**
	  	 * @param el an Element
	  	 * @return the ordinal position 1..N of this element amongst its sibling
	  	 * elements (of any name), as a String; 
	  	 * or '0' if the element has no parent
	  	 */
	  	public static String ordinalPosition(Element el)
	  	{
	  		String position = "0";
	    	Node parent = el.getParentNode();
	    	if (parent instanceof Element)
	    	{
	    		int pos = 0;
	    		NodeList nl = ((Element)parent).getChildNodes();
	    		for (int i = 0; i < nl.getLength();i++)
	    		{
	    			Node n = nl.item(i);
	    			if (n instanceof Element) pos++;
	    			if (n.equals(el)) position = new Integer(pos).toString();
	    		}
	    	}
	    	return position;
	  	}
	  	
		/**
		 * copy all attributes form one element to another
		 * @param fromEl
		 * @param toEl
		 */
	  	public static void copyAttributes(Element fromEl,Element toEl)
		{
			for (int a = 0; a < fromEl.getAttributes().getLength();a++)
			{
				Attr at = (Attr)fromEl.getAttributes().item(a);
				toEl.setAttribute(at.getName(), at.getValue());
			}		
		}
		
	  	/**
	  	 * copy text from one element to another
	  	 * @param fromEl
	  	 * @param toEl
	  	 * @param outDoc
	  	 */
	  	public static void copyText(Element fromEl,Element toEl, Document outDoc)
		{
			String text = XMLUtil.getText(fromEl);
			if ((text != null) && (text.length() > 0)) toEl.appendChild(outDoc.createTextNode(text));
		}

	  	
	  	/**
	  	 * 
	  	 * @param el an element
	  	 * @param attNames names of allowed attributes
	  	 * @throws MapperException if the element has any attribute with another name
	  	 */
	  	public static void checkAttributes(Element el, String[] attNames) throws MapperException
	  	{
	  		NamedNodeMap map = el.getAttributes();
	  		for (int i = 0; i < map.getLength(); i++)
	  		{
	  			String attName = map.item(i).getLocalName();
	  			if (!GenUtil.inArray(attName, attNames)) 
	  				throw new MapperException("Element '" + el.getLocalName() + "' has no attribute '" + attName + "'");
	  		}
	  	}
	  	
	  	/**
	  	 * 
	  	 * @param el an element
	  	 * @param elNames allowed names for its child elements
	  	 * @throws MapperException
	  	 */
	  	public static void checkChildElements(Element el, String[] elNames) throws MapperException
	  	{
	  		Vector<Element> children = childElements(el);
	  		for (Iterator<Element> it = children.iterator();it.hasNext();)
	  		{
	  			String childName = it.next().getLocalName();
	  			if (!GenUtil.inArray(childName, elNames)) 
	  				throw new MapperException("Element '" + el.getLocalName() + "' has no child element '" + childName + "'");
	  		}
	  	}
	  	
	  	/**
	  	 * append an XML element child to a parent Element parent, first checking that they are from 
	  	 * the same document.
	  	 * @param parent
	  	 * @param child
	  	 * @throws MapperException
	  	 */
	  	public static void appendChildWithDocumentCheck(Element parent,Element child) throws MapperException
	  	{
	  		Document doc1 = parent.getOwnerDocument();
	  		Document doc2 = child.getOwnerDocument();
	  		if (!doc1.equals(doc2))
	  		{
	  			String doc1Name = doc1.getDocumentURI();
	  			String doc2Name = doc1.getDocumentURI();
	  			String message = "Attempt to add a child node from Document '" + doc1Name 
	  					+ "' to a parent from Document '" + doc2Name + "'";
	  			throw new MapperException(message);	  					
	  		}
	  		parent.appendChild(child);
	  	}
	  	
	  	
	  	//---------------------------------------------------------------------------------------------
	  	//                          Schema Validation
	  	//---------------------------------------------------------------------------------------------

		/**
		 * validate an XML instance against the schema
		 * if there is such a schema. If there is no schema, return an empty list.
		 * (Appears not to work, so it has been temporarily bypassed - see below)
		 * @param the root element of the XML instance
		 * @return a Vector of SchemaMismatch objects, which wrap org.eclipse.emf.ecore.util.Diagnostic
		 */
		public static Vector<SchemaMismatch> schemaValidate(Element instanceRoot, URI schemaURI)
		{
			boolean bypassed = true;
			Vector<SchemaMismatch> mismatches = new Vector<SchemaMismatch>();
		    if (!bypassed) try
			{
				XMLProcessor processor = new XMLProcessor(schemaURI);
				// can throw exceptions at the next line if the instance has features not in the schema
				Resource resource = processor.load(instanceRoot, null);
			    EObject document = (EObject)resource.getContents().get(0);

			    // Validate the feed and convert the resulting diagnostic to a set of SchemaMismatches
			    Diagnostic diagnostic = Diagnostician.INSTANCE.validate(document);
			    captureDiagnosticTree(diagnostic,0,mismatches);
			}
			catch (Exception ex) 
			{
				String message = "Exception during schema validation: " + ex.getMessage();
				System.out.println(message);
				ex.printStackTrace();
				Diagnostic diagnostic = new BasicDiagnostic(1,"schema",1,message,null);
		    	mismatches.add(new SchemaMismatch(diagnostic,SchemaMismatch.SCHEMA_VALIDATION_ERROR));	    	  
			}
			return mismatches;
		}
		
		/**
		 * build up a set of SchemaMismatch objects from a diagnostic tree
		 * @param diagnostic
		 * @param level current depth in the diagnostic tree
		 * @param mismatches the Vector of schemaMismatches being built up
		 */
		private static void captureDiagnosticTree(Diagnostic diagnostic, int level,Vector<SchemaMismatch> mismatches)
		{
		    switch (diagnostic.getSeverity())
		    {
		      case Diagnostic.OK:
		    	// record nothing for an OK diagnostic  
		        break;
		      case Diagnostic.ERROR:
		      {
		    	  // do not record the overall diagnostic message
		    	  if (!(diagnostic.getMessage().startsWith("Diagnosis of")))
		    	  mismatches.add(new SchemaMismatch(diagnostic,SchemaMismatch.SCHEMA_VALIDATION_ERROR));	    	  
		      }
		        break;
		      case Diagnostic.INFO:
			      mismatches.add(new SchemaMismatch(diagnostic,SchemaMismatch.SCHEMA_VALIDATION_INFO));
		        break;
		    }
		    
		    //  recurse down the tree of diagnostics
		    for (Iterator<Diagnostic> i = diagnostic.getChildren().iterator(); i.hasNext();)
		    	captureDiagnosticTree(i.next(), level + 1, mismatches);		
		}
		
		//---------------------------------------------------------------------------------------------------------------
		//                                        Utilities for xhtml
		//---------------------------------------------------------------------------------------------------------------

		
		/**
		 * 
		 * @param doc the html document
		 * @param header array of column names
		 * @param bodyId id for the body element (for javascript to pick up), or null
		 * @return an html table element with a head, a header row and a body with an id, but no body rows
		 */
		public static Element htmlTable(Document doc, String[] header,String bodyId) throws MapperException
		{
			Element table = newElement(doc, "table");
			table.setAttribute("border", "1");
			Element thead = newElement(doc, "thead");
			table.appendChild(thead);
			Element headerRow = newElement(doc, "tr");
			thead.appendChild(headerRow);
			for (int i = 0; i < header.length; i++)
			{
				Element headerCell = XMLUtil.textElement(doc, "th", header[i]);
				headerRow.appendChild(headerCell);
			}			

			Element tbody = newElement(doc, "tbody");
			if (bodyId != null) tbody.setAttribute("id", bodyId);
			table.appendChild(tbody);

			return table;
		}
		
		/**
		 * add a row to the end of an html table
		 * @param table
		 * @param row array of cell contents
		 * @return the new number of rows in the body
		 * @throws MapperException if the table has no header, or the header size does not match the row
		 */
		public static int addhtmlTableRow(Element table, String[] row, String[] links)  throws MapperException
		{
			Document doc = table.getOwnerDocument();
			Element head = firstNamedChild(table, "thead");
			if (head == null)  throw new MapperException("Table has no head");
			Element headerRow = firstNamedChild(head, "tr");
			if (headerRow == null)  throw new MapperException("Table has no header row");
			int size = XMLUtil.namedChildElements(headerRow, "th").size();
			if (size != row.length) throw new MapperException("Cannot add a row of length " + row.length + " to a table with " + size + " columns.");

			Element body = firstNamedChild(table, "tbody");
			if (body == null)  throw new MapperException("Table has no body");
			Element newRow = newElement(doc, "tr");
			body.appendChild(newRow);

			for (int i = 0; i < row.length; i++)
			{
				// the contents of this cell are a link to somewhere
				if ((links != null) && (links[i] != null) && (!links[i].equals("")))
				{
					Element rowCell = XMLUtil.newElement(doc, "td");
					newRow.appendChild(rowCell);
					Element aCell = XMLUtil.textElement(doc, "a", row[i]);
					aCell.setAttribute("href", links[i]);
					rowCell.appendChild(aCell);					
				}
				// the contents of this cell are not a link
				else
				{
					Element rowCell = XMLUtil.textElement(doc, "td", row[i]);
					newRow.appendChild(rowCell);
				}
			}
			
			// return the new number of rows in the body
			return namedChildElements(body, "tr").size();
		}
		
		//---------------------------------------------------------------------------------------------------------------
		//                                        counts of nodes
		//---------------------------------------------------------------------------------------------------------------

		/**
		 * @param el
		 * @return the number of element and attribute nodes in an XML tree
		 */
		public static int nodeCount(Element el)
		{
			int count = 1 + el.getAttributes().getLength(); // this node and its attributes
	  		
			// contributions from child elements
	  		Vector<Element>  childElements = childElements(el);
	  		for (int i = 0; i < childElements.size(); i++)
	  			count = count + nodeCount(childElements.get(i));
	  		
	  		return count;
		}

		/**
		 * @param el
		 * @return the number of element and attribute nodes in an XML tree
		 */
		public static int nodeValueCount(Element el)
		{
			int count = el.getAttributes().getLength(); //  attributes of this node
			
			// text value of this node
			String text = getText(el);
			if ((text !=null) && (!text.equals(""))) count++;
	  		
			// contributions from child elements
	  		Vector<Element>  childElements = childElements(el);
	  		for (int i = 0; i < childElements.size(); i++)
	  			count = count + nodeValueCount(childElements.get(i));
	  		
	  		return count;
		}
		
		/**
		 * 
		 * @param el
		 * @return depth of an XML node tree
		 */
		public static int getDepth(Element el)
		{
			int depth = 1;
			if (el.getAttributes().getLength() > 0) depth = 2;
			
			int max = 0;
			Vector<Element>  childElements = childElements(el);
			for (int i = 0; i < childElements.size(); i++)
			{
				int childDepth = getDepth(childElements.get(i));
				if (childDepth > max) max = childDepth;
			}
			
			return depth + max;
		}
		
		/**
		 * print out all distinct paths in an XML subtree
		 * @param el
		 */
		public static void writeAllPaths(Element el)
		{
			Hashtable<String,String> allPaths = new Hashtable<String,String>();
			String path = "";
			collectPaths(path, allPaths,el);
			for (Enumeration<String> en = allPaths.keys();en.hasMoreElements();)
				System.out.println("path: " + en.nextElement());
		}
		
		private static void collectPaths(String path, Hashtable<String,String> allPaths,Element el)
		{
			String newPath = path + el.getLocalName() + "/";
			allPaths.put(newPath, "1");
			for (Iterator<Element> it = childElements(el).iterator(); it.hasNext();)
				collectPaths(newPath, allPaths, it.next());
		}


}
