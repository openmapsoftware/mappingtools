package com.openMap1.mapper.util;

import javax.xml.soap.*;
import javax.xml.soap.Node;

import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.InputDialog;
import org.osgi.framework.Bundle;
import org.w3c.dom.*;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.impl.MapperPlugin;

/**
 * Composes and sends SOAP messages, and receives replies,
 * when the Eclipse Mapper 
 * tools are acting as clients for the translation compiler web service.
 * 
 * @author robert
 *
 */

public class SOAPClient {
	
	// types of request header that can be sent to the server
	public static int COMPILE_REQUEST = 0;
	public static int MANAGER_REQUEST = 1;
	
	// types of reply header that can return from the server
	public static int SHOW_ERROR_MESSAGE = 0;
	public static int USE_SUCCESSFUL_COMPILE = 1;
	public static int USE_MANAGER_RESULT = 2;
	
	private int SERVERS_TO_TRY = 3;
	
	private boolean tracing = false;
	

    
    /**
     * the addresses of the web services to be tried out are stored 
     * in file plugin.properties for the Mapper plugin,
     * There are three to be tried in order, with names '_TS_ServiceAddress_1' , 2 and 3
     * @param i = 1,2, or 3 in successive attempts
     * @return the URL of the web service
     */
    private String getAddress(int i)
    {
    	String pluginParameterName = "_TS_ServiceAddress_" + i;
    	return MapperPlugin.INSTANCE.getString(pluginParameterName);
    }
    
    public static String serviceNamespaceURI = "http://openMap1.com/ws/ProcCompiler/";    
    public static String serviceNamespacePrefix = "p0";

    public static String xsdPrefix = "xsd";
    public static String SOAPURI = "http://schemas.xmlsoap.org/soap/envelope/";
    public static String SOAPPrefix = "soapenv";

    private SOAPConnectionFactory soapConnectionFactory;
    private SOAPConnection connection;
    private SOAPFactory soapFactory;
    private MessageFactory factory;
    private SOAPMessage message;
    
    /* namespaces for the document being sent. key = prefix; value = URI. 
     * This assumes a namespace does not appear with more than one prefix. */
    private Hashtable<String,String> namespaces;
    
    public SOAPClient() throws MapperException
    {
    	try
    	{
        	soapConnectionFactory = SOAPConnectionFactory.newInstance();
        	connection = soapConnectionFactory.createConnection();
        	soapFactory = SOAPFactory.newInstance();
        	factory = MessageFactory.newInstance();
    	}
		catch (SOAPException ex) {throw new MapperException("SOAP Exception: " + ex.getMessage());}
    }
	
	/**
	 * main method to send a SOAP message and get the XML out of the reply
	 * @param requestType must be COMPILE_REQUEST
	 * @param request XML Elements to be sent
	 * @return XML Elements sent back
	 * @throws MapperException
	 */
    public Element[] getReply(int requestType, Element[] request) throws MapperException
	{
    	trace("getReply");
    	Element[] reply = new Element[0];
		try
		{
			SOAPBody body = getEmptySOAPBody();
			if (	(requestType == COMPILE_REQUEST)|
					(requestType == MANAGER_REQUEST))
			{
				try
				{
					namespaces = new Hashtable<String,String>();
			        Name bodyName = soapFactory.createName("CServiceRequest", serviceNamespacePrefix, serviceNamespaceURI);
			        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);			

			        // put all elements of the array request in the SOAP body
			        for (int i = 0; i < request.length; i++)
			        	addSoapElement(bodyElement,request[i]);
					
				    // try out 3 service addresses in turn
			        SOAPMessage response = null;
			        response = getResponse(1);

				    if (response == null)
				    	throw new MapperException("Empty reply from translation service.");
				    reply = handleResponse(response);					
				    connection.close();
				}
				catch (Exception ex) {throw new MapperException(ex.getMessage());}
			}
			else throw new MapperException("Unsupported request type: " + requestType);
		}
		catch (SOAPException ex) {throw new MapperException("SOAP Exception: " + ex.getMessage());}
		return reply;
	}
    
    /**
     * make a limited number of attempts to get a SOAP response from different 
     * server addresses - each  time trying the next one if you get a 'Message send failed'
     * @param attempt integer attempt number - from 1 to SERVERS_TO_TRY
     * @return the response
     * @throws MapperException if some other exception occurred, of if no attempt succeeded
     * @throws MalformedURLException 
     */
    private SOAPMessage getResponse(int attempt) throws MapperException, MalformedURLException
    {
        SOAPMessage response = null;
        URL endpoint = new URL(getAddress(attempt));
        try{response = connection.call(message, endpoint);}
	    catch (SOAPException ex)
	    {
	    	// SOAPException soapEx = (SOAPException)ex;
	    
	    	if (attempt < SERVERS_TO_TRY)
	    	{
	    		System.out.println("Failed to send to "  + getAddress(attempt));
	    		response = getResponse(attempt + 1);
	    	}
	    	else throw new MapperException(ex.getMessage());
	    }
	    catch (Exception ex)
	    {
	    	System.out.println("Non-SOAP Exception sending message to translation service");
	    	ex.printStackTrace();
	    }
	    return response;
    }
	
	/** 
	 * true if an exception message contains the string ' Message send failed'
	 * @param message
	 * @return
	    Not used now - 26/6/2009
	private boolean sendFailed(String exMessage)
	{
		boolean failed = false;
		StringTokenizer st = new StringTokenizer(exMessage," ");
		while (st.hasMoreTokens())
		{
			if (    (st.nextToken().equalsIgnoreCase("Message")) &&
					(st.hasMoreTokens()) && (st.nextToken().equals("send")) &&
					(st.hasMoreTokens()) && (st.nextToken().equals("failed"))) 
			failed = true;
		}
		return failed;
	} */
	
	/**
	 * Create an empty SOAP body, doing other necessary SOAP stuff
	 * @return a SOAP Body for the request
	 * @throws SOAPException
	 */
	private SOAPBody getEmptySOAPBody() throws SOAPException
	{
		// create the message (instance variable)
    	message = factory.createMessage();
        message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");

        // Add a SoapAction header to the http, which is necessary
        MimeHeaders hd = message.getMimeHeaders();
        hd.addHeader("SOAPAction", "urn:openMap1SoapAction");

        // make the envelope
        SOAPPart sp = message.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();	        
        se.addNamespaceDeclaration(xsdPrefix, XMLUtil.SCHEMAURI);
        se.addNamespaceDeclaration(XMLUtil.SCHEMAINSTANCEPREFIX, XMLUtil.SCHEMAINSTANCEURI);

        // the SOAP header has nothing in it yet, so it can be left off
        SOAPHeader header = message.getSOAPHeader();
        header.detachNode();  

        // make the body
        SOAPBody body = message.getSOAPBody();
        return body;		
	}
	
	/**
	 * extract XML Elements from the response
	 * @param response SOAP message from the server
	 * @return array of XML Elements in the message
	 * @throws SOAPException
	 */
	private Element[] handleResponse(SOAPMessage response) throws SOAPException
	{
        SOAPBody soapBody = response.getSOAPBody();
    	Vector<Element> nodes = new Vector<Element>();

        // put the reply into a Vector of Elements
    	for (Iterator<?> it =  soapBody.getChildElements(); it.hasNext();)
        {
      	  Node next = (Node)it.next();
      	  if (next instanceof SOAPBodyElement) nodes.add((SOAPBodyElement)next);
        }

        // turn a Vector of Elements into an Array of Elements
    	Element[] reply = new Element[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) reply[i] = nodes.elementAt(i);
		return reply;
	}
	
	/**
	 * add a tree of SOAPElements to a SOAP parent element,
	 * copying a tree of DOM Elements
	 * @param sel the SOAP Element to be added to
	 * @param el root of the DOM tree
	 */
	private void addSoapElement(SOAPElement sel,Element el) throws SOAPException
	{
		// record any new namespace declarations on this element
		addNamespaces(el);

		// create the child SOAPElement with correct name, and add it to the parent
		String tagName = el.getTagName();
		Name name = soapFactory.createName(getLocalName(tagName));
		String prefix = getPrefix(tagName);
		if (prefix != null)
		{
			String uri = namespaces.get(prefix);
			if (uri == null) System.out.println("Found no namespace for Element prefix'" + prefix + "'");
			else name = soapFactory.createName(getLocalName(tagName),prefix,uri);
		}
	    SOAPElement child =  sel.addChildElement(name);
	    
	    // add all attributes to the child node (including namespace declarations)
	    NamedNodeMap attMap = el.getAttributes();
	    for (int a = 0; a < attMap.getLength();a++)
	    {
	    	org.w3c.dom.Node nd = attMap.item(a);
	    	if (nd instanceof Attr)
	    	{
	    		Attr at = (Attr)nd;
	    		String attPrefix = getPrefix(at.getName());
	    		Name attName = soapFactory.createName(at.getName());
	    		child.addAttribute(attName, at.getValue());
	    		if ((attPrefix != null) && (attPrefix.equals("xmlns")))
	    		{
	    			// System.out.println("Declared '" + getLocalName(at.getName()) + "' by '" + attName.getQualifiedName() + "'");
	    		}
	    	}
	    }
	    
	    //add the correct descendant Elements and text elements to the new child
	    NodeList nl = el.getChildNodes();
	    for (int i = 0; i < nl.getLength();i++)
	    {
	    	org.w3c.dom.Node n = nl.item(i);
	    	if (n instanceof org.w3c.dom.Text)
	    	{
	    		org.w3c.dom.Text t = (org.w3c.dom.Text)n;
	    		String text = t.getWholeText();
	    		child.addTextNode(text);
	    	}
	    	else if (n instanceof Element) 
	    	{
	    		Element e = (Element)n;
	    		addSoapElement(child,e);
	    	}
	    }
	}
	
	// record any new namespace declarations on this element
	private void addNamespaces(Element el)
	{
	    NamedNodeMap attMap = el.getAttributes();
	    for (int a = 0; a < attMap.getLength();a++)
	    {
	    	org.w3c.dom.Node nd = attMap.item(a);
	    	if (nd instanceof Attr)
	    	{
	    		Attr at = (Attr)nd;
	    		String attName = at.getName();
	    		if (attName.startsWith("xmlns"))
	    		{
	    			String prefix = "";
	    			if (attName.length() > 6) prefix = attName.substring(6);
	    			String uri = at.getValue();
	    			namespaces.put(prefix, uri);
	    			//System.out.println("Recorded namespace " + prefix);  
	    		}
	    	}
	    }
		
	}

	/**
	 * @param tagName
	 * @return the prefix from a tag name, or null if there is none
	 */
	private String getPrefix(String tagName)
	{
		StringTokenizer st = new StringTokenizer(tagName,":");
		if (st.countTokens() == 1) return null;
		return st.nextToken();
	}

	/**
	 * @param tagName
	 * @return  the part of a tag name after ':', or the whole name if there is no ':'
	 */
	private String getLocalName(String tagName)
	{
		StringTokenizer st = new StringTokenizer(tagName,":");
		if (st.countTokens() == 1) return tagName;
		st.nextToken();// remove prefix
		return st.nextToken();
	}
    
    //-----------------------------------------------------------------------------------
    //                      Getting the user's email address and key
    //-----------------------------------------------------------------------------------
	
	/**
	 * @return the location and name of the key file
	 */
	public static String keyFileLocation()
	{
		Bundle  thisPlugin = Platform.getBundle("com.openMap1.mapper");
		IPath path = Platform.getStateLocation(thisPlugin);
		String location = path.toString() + "/key.xml";
		return location;		
	}
    
    /**
     * @return the email address which the tool has stored, or null 
     * if none is stored
     */
	public static String getStoredEmail()
    {
    	String email = null;
		try
		{
			Element keyRoot = XMLUtil.getRootElement(keyFileLocation());
			email = keyRoot.getAttribute("email");
		}
		catch (Exception ex) {} 
		return email;
    }
    /**
     * @return the key for use of the xslt plugin which the tool has stored, or null 
     * if none is stored
     */
	public static String getXSLTKey()
    {
    	String xslKey = null;
		try
		{
			Element keyRoot = XMLUtil.getRootElement(keyFileLocation());
			xslKey = keyRoot.getAttribute("xslkey");
		}
		catch (Exception ex) {} 
		return xslKey;
    }

    /**
     * @return the translation service key which the tool has stored, or null 
     * if none is stored
     */    
	public static String getStoredKey()
    {
    	String key = null;
		try
		{
			Element keyRoot = XMLUtil.getRootElement(keyFileLocation());
			key = keyRoot.getAttribute("key");
		}
		catch (Exception ex) {} 
		return key;
    }
    
    /**
     * persuade the user to enter an email address, and store it in the local file.
     * @return null if the user cancels
     */
    public static String getEmailFromUser() throws MapperException
    {
    	String prompt = "Please enter an email address to get a new key for the compilation service";
		String email = requestTextFromUser(prompt);
		if (email != null) makeKeyFile(email,"");
		return email;    	
    }

	
	/**
	 * show a dialogue with a prompt to the user, and return the text entered
	 * @param the prompt text
	 * @return the text entered
	 * (if the user clicks OK) or null if he cancels.
	 */
	public static String requestTextFromUser(String prompt)
	{
		String text = null;
		// null = shell; null = title; "" = initial text; null = text validator
		InputDialog inputDialog = new InputDialog(null,null,prompt,"",null);
		if (inputDialog.open() == InputDialog.OK) text = inputDialog.getValue();
		return text;
	}
	
	/**
	 * make or replace the local file holding the user's email address and 
	 * key for the translation service
	 */
	public static void makeKeyFile(String email, String compilerKey) throws MapperException
	{
		try{
			Document keyDoc = XMLUtil.makeOutDoc();
			Element rootEl = XMLUtil.newElement(keyDoc, "CompileKey");
			keyDoc.appendChild(rootEl);
			rootEl.setAttribute("key",compilerKey);
			rootEl.setAttribute("email",email);
			XMLUtil.writeOutput(keyDoc, keyFileLocation(), true);				
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
	}
	
	/**
	 * set the xslt generation key, remembering the user's email and the compile key
	 * @param xslKey
	 * @throws MapperException if theere is no key file
	 */
	public static void setXSLTKey(String xslKey)  throws MapperException
	{
		try{
			Element oldKeyRoot = XMLUtil.getRootElement(keyFileLocation());
			Document keyDoc = XMLUtil.makeOutDoc();
			Element rootEl = XMLUtil.newElement(keyDoc, "CompileKey");
			keyDoc.appendChild(rootEl);
			rootEl.setAttribute("key",oldKeyRoot.getAttribute("key"));
			rootEl.setAttribute("email",oldKeyRoot.getAttribute("email"));
			rootEl.setAttribute("xslkey",xslKey);
			XMLUtil.writeOutput(keyDoc, keyFileLocation(), true);				
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
	}
	
	/**
	 * find the user's email address and key for the translation service,
	 * from a file 'key.xml' in the plugin storage area; 
	 * 
	 * If there is no such file, get an email address from the user, 
	 * and send an empty key (which will cause the server to make a
	 * new key and return it with the compile result)
	 *
	 * @return Element 'requestHeader' with three attributes 'requestType', 'email' and 'key'
	 * 'requestType' is SOAPClient.COMPILE_REQUEST.
	 * or return null if the user cancels the dialogue requesting an email address.
	 * @throws XMLException
	 */
	public static Element getEmailAndKey() throws MapperException
	{
		Element el = null;

		String email = getStoredEmail();
		if (email == null) email = getEmailFromUser();
		if (email == null) return null; // if the user cancelled the dialogue to enter the email address

		// if the key has been found, make out a request header for a compilation
		Document doc = XMLUtil.makeOutDoc();
		el = XMLUtil.newElement(doc, "requestHeader");
		el.setAttribute("requestType",new Integer(SOAPClient.COMPILE_REQUEST).toString());
		el.setAttribute("email", email);
		el.setAttribute("key",getStoredKey());	// will be "" if the user has just entered an email		

		return el;
	}
	
	
	private void trace(String s) {if (tracing) System.out.println(s);}

}
