package com.openMap1.mapper.util;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.XMLException;

public class XSLOutputFile extends XMLOutputFile {


    //---------------------------------------------------------------------------------------------------
    //                                    methods for XSLT files
    //---------------------------------------------------------------------------------------------------


    /**
     * a prefix for the W3C XML schema namespace URI
     */
    public String XSLPrefix = "xsl";

    
    /**
     * Make an Element in the W3C XML schema namespace, with prefix as defined by XSLPrefix.
     * @param name the name after the prefix and ':'
     * @return the new Element
     * @throws XMLException
     */
    public Element XSLElement(String name) throws XMLException
    {
        return XMLUtil.NSElement(outDoc(),XSLPrefix, name, XMLUtil.XSLURI);
    }
    
    /**
     * 
     * @param name
     * @param text
     * @return an XSL element with text content
     * @throws XMLException
     */
    public Element XSLTextElement(String name, String text)  throws XMLException
    {
        return XMLUtil.textNSElement(outDoc(), XSLPrefix, name, XMLUtil.XSLURI, text);    	
    }
    
	/**
	 * add a test template, of a form like:
	 *
	<xsl:template match="*" mode="test">
		<xsl:variable name="vTest" select="."/>
		<xsl:message>local name: [<xsl:value-of select="local-name()"/>]</xsl:message>
		<xsl:message>namespace uri: [<xsl:value-of select="namespace-uri()"/>]</xsl:message>
	</xsl:template>
	
	to be applied as in:
	
		<xsl:apply-templates mode="test" select="$variable"/>
		
	by hand-written additions to transforms, to get diagnostic information
	about node-valued variables.

	 * @param xout
	 */
	public void addTestTemplate() throws MapperException
	{
		Element tempEl = XSLElement("template");
		tempEl.setAttribute("match", "*");
		tempEl.setAttribute("mode", "test");
		
		Element varEl = XSLElement("variable");
		varEl.setAttribute("name", "vTest");
		varEl.setAttribute("select", ".");
		tempEl.appendChild(varEl);
		
		String diagnostic_1 = "local name: [<xsl:value-of select='local-name()'/>]";
		Element messEl_1 = XSLTextElement("message", diagnostic_1);
		tempEl.appendChild(messEl_1);
		
		String diagnostic_2 = "namespace uri: [<xsl:value-of select='namespace-uri()''/>]";
		Element messEl_2 = XSLTextElement("message", diagnostic_2);
		tempEl.appendChild(messEl_2);
				
		topOut().appendChild(tempEl);		
	}


    /**
     * add a parameter <xsl:param> to a template, if there is not
     * a parameter or a variable of that name already.
     * Return true if one was added.
     */
    public boolean addParameter(String paramName, Element templateNode) throws XMLException
    {
        boolean res = false;
        if ((!GenUtil.inVector(paramName, parameters(templateNode))) &&
                (!GenUtil.inVector(paramName, allVariables(templateNode)))) 
        {
            Element paramEl = XSLElement("param");
            paramEl.setAttribute("name", paramName);

            NodeList nl = templateNode.getChildNodes();
            Node first = nl.item(0);
            templateNode.insertBefore(paramEl, first);
            res = true;
        }
        return res;
    }

    /**
     * Vector of names of parameters of the current template
     */
    public Vector<String> parameters(Element templateNode) 
    {
        Vector<String> params = new Vector<String>();
        Vector<Element> defNodes = XMLUtil.namedChildElements(templateNode, "param");
        for (int j = 0; j < defNodes.size(); j++) {
            Element defNode = defNodes.elementAt(j);
            params.addElement(defNode.getAttribute("name"));
        }
        return params;
    }

    /**
     * Vector of names of variables declared at the top level of the current template
     */
    public Vector<String> topVariables(Element templateNode) 
    {
        Vector<String> vars = new Vector<String>();
        Vector<Element> defNodes = XMLUtil.namedChildElements(templateNode, "variable");
        for (int j = 0; j < defNodes.size(); j++) {
            Element defNode = defNodes.elementAt(j);
            vars.addElement(defNode.getAttribute("name"));
        }
        return vars;
    }

    /**
     * Vector of names of variables declared at any level of the current template
     */
    public Vector<String> allVariables(Element templateNode) {
        Vector<String> vars = new Vector<String>();
        Vector<Element> defNodes = XMLUtil.namedChildElements(templateNode, "variable");
        // top level variables
        for (int j = 0; j < defNodes.size(); j++) {
            Element defNode = defNodes.elementAt(j);
            vars.addElement(defNode.getAttribute("name"));
        }
        // variables declared inside other elements
        Vector<Element> allNodes = childElements(templateNode);
        for (int j = 0; j < allNodes.size(); j++) {
            Element cNode = allNodes.elementAt(j);
            Vector<String> childVars = allVariables(cNode);
            for (int k = 0; k < childVars.size(); k++) {
                vars.addElement(childVars.elementAt(k));
            }
        }
        return vars;
    }

    /**
     * check for a clashing variable or parameter name before adding it to a template
     */
    public boolean clashes(String varName, Element templateNode) {
        boolean clash = false;
        if (GenUtil.inVector(varName, allVariables(templateNode))) clash = true;
        if (GenUtil.inVector(varName, parameters(templateNode))) clash = true;
        return clash;
    }

    /**
     * true if the list of variables of the template has a variable made from a class name
     * by appending something beginning with '_' to it.
     */
    public boolean hasClassVar(String className, Element templateNode) {
        boolean has = false;
        Vector<String> vars = allVariables(templateNode);
        for (int i = 0; i < vars.size(); i++) {
            String var = vars.elementAt(i);
            if (var.equals(className)) has = true; // used for subset = ""
            if (var.startsWith(className + "_")) has = true; // used for all other subsets
        }
        return has;
    }

    /**
     * If any variable declared beneath element toAdd clashes with any variable or parameter
     * already declared under templateNode, return the name of the clashing variable.
     * <p/>
     * Otherwise return null.
     */
    public String mergeClash(Element toAdd, Element templateNode) {
        String clashVar = null;
        Vector<String> addVars = allVariables(toAdd);
        for (int i = 0; i < addVars.size(); i++) {
            String vName = addVars.elementAt(i);
            if (clashes(vName, templateNode)) clashVar = vName;
        }
        return clashVar;
    }
    
    /**
     * 
     * @param parent Element to which an xsl:variable element is to be added
     * @param varEl an xsl:variable element
     * 
     * Add the xsl:variable element as a child of the parent element, 
     * before any xsl:call-template or xsl:apply-templates or xsl:if elements -
     * to ensure variables are always declared before they are used, even when
     * parameters are belatedly added to the calls and applies in the hookup process
     */
    public void addVariableBeforeCallOrApply(Element parent, Element varEl)
    {
        NodeList nl = parent.getChildNodes();
        boolean found = false;
        for (int i = 0; i < nl.getLength(); i++)
        {
            Node next = nl.item(i);
            if ((next instanceof Element) && (!found))
            {
            	Element el = (Element)next;
            	if ((el.getLocalName().equals("call-template"))|
            			(el.getLocalName().equals("apply-templates"))|
        			    (el.getLocalName().equals("if")))
            	{
            		parent.insertBefore(varEl, el);
            		found = true;
            	}
            }        	
        }
        if (!found) parent.appendChild(varEl);    	
    }

}
