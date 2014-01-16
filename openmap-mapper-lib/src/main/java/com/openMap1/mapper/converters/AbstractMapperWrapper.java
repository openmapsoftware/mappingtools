package com.openMap1.mapper.converters;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLOutputFile;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XPathAPI;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.BasicTemplateFilter;
import com.openMap1.mapper.writer.TemplateFilter;
import com.openMap1.mapper.MappedStructure;

/**
 * Abstract class that can be used as the superclass of any 
 * class implementing the MapperWrapper interface, which 
 * can be used as a wrapper class on a set of mappings -
 * to apply an 'in' transform to any instance before it is read by mappings, 
 * or the reverse 'out' transform to any instance 
 * after it has been written by wproc procedures.
 * 
 * @author robert
 *
 */

abstract public class AbstractMapperWrapper implements MapperWrapper{
	
	public static int IN_PRE_SCAN = 0;
	public static int IN_TRANSFORM = 1;
	public static int OUT_PRE_SCAN = 2;
	public static int OUT_TRANSFORM = 3;
	
	
	protected Document inResultDoc;
	
	protected Document outResultDoc;

	public MappedStructure ms() {return ms;}
	private MappedStructure ms;
	
	/**
	 *  constants defining the types of files transformed to and from
	 */
	public static int  XML_TYPE = 0;
	public static int  TEXT_TYPE = 1;
	
	
	public static String V3PREFIX = "v3";
	
	public static String V3NAMESPACEURI = "urn:hl7-org:v3";
	
	
	//----------------------------------------------------------------------------------------------------------
	//                                                   constructor
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare spare argument, just in case....
	 */
	public AbstractMapperWrapper(MappedStructure ms, Object spare) throws MapperException
	{
		this.ms = ms;
		// when this wrapper is acting as an output wrapper, it may need access to the input reader's wrapper instance
		resetSpareArgument(spare);
	}
	
	public void resetSpareArgument(Object spare) throws MapperException
	{
		if ((spare != null) && (spare instanceof MDLXOReader))
		{
			MDLXOReader reader = (MDLXOReader)spare;
			inputWrapper = reader.ms().getWrapper();
		}		
	}
	
	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	abstract public int transformType();
	
	/**
	 * 
	 * @return the file extension of the outer document, with initial '*.'
	 */
	abstract public String fileExtension();
	
	/**
	 * 
	 * @param incoming; must be of class Element or InputStream
	 * @return the result of the in wrapper transform
	 */
	abstract public Document transformIn(Object incoming) throws MapperException;

	
	/**
	 * @param outgoing the root element produced by the XMLWriter when 
	 * writing out from a class model instance (seen through an objectGetter)
	 * @return the result of the out wrapper transform; 
	 * must be of class Document or OutputStream
	 */
	abstract public Object transformOut(Element outgoing) throws MapperException;
	
	/**
	 * When certain XML subtrees are to be passed through a translation
	 * unchanged, they are indexed by a string key by an input wrapper transform
	 * and then made available to an output wrapper transform
	 * to be overridden by any wrapper class providing this facility
	 * @return a table of subtrees, with random string keys
	 */
	public Hashtable<String,Element> keptSubtrees() {return keptSubtrees;}
	
	/**
	 * for the CDA API for Java, it is necessary to be able to set up the table
	 * converting string keys to XML subtrees, as if an XML instance had been
	 * read in
	 * @param keptSubtrees
	 */
	public void setKeptSubtrees(Hashtable<String,Element> keptSubtrees) 
	{
		if (keptSubtrees != null)
		{
			this.keptSubtrees = keptSubtrees;
			keptSubtreesSet = true;
		}
	}


	protected Hashtable<String,Element> keptSubtrees = null;
	
	protected boolean keptSubtreesSet = false;
	
	protected int keyIndex = 0;
		
	/*  may be set non-null when this instance is acting as an output wrapper  */
	protected MapperWrapper inputWrapper = null;
	
	//---------------------------------------------------------------------------------
	//                      XSLT for wrapper transforms
	//---------------------------------------------------------------------------------
	
	/**
	 * @param xout the XSL output file
	 * @param templateFilter has a boolean method to say if a template should be included
	 * append the  templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'in' direction.
	 * Templates must have mode = "inWrapper"
	 * @throws MapperException
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		xout.topOut().appendChild(identityTemplate(xout,"inWrapper"));
	}

	/**
	 * @param xout the XSL output file
	 * @param templateFilter has a boolean method to say if a template should be included
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'out' direction.
	 * Templates must have mode = "outWrapper"
	 * @throws MapperException
	 */
	public void addWrapperOutTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		xout.topOut().appendChild(identityTemplate(xout,"outWrapper"));
	}
	
	/**
	 * make a standalone wrapper XSLT file at the specified file location
	 * @param xout an empty xsl output file which has been created, and will be written to a location
	 * after the end of this call
	 * @param isInWrapper - true if it is to be an in-wrapper transform, false for an out-wrapper transform
	 * This default implementation makes a wrapper transform which changes nothing.
	 * Subclasses override the called methods to provide templates to change specific tag names.
	 * **** DO NOT OVERRIDE THIS METHOD; OVERRIDE THE METHODS IT CALLS ********
	 */
	public void makeStandaloneWrapperXSLT(XSLOutputFile xout, boolean isInWrapper)  throws MapperException
	{
		makeHeaderForStandaloneWrapperXSLT( xout, isInWrapper);
		
		TemplateFilter templateFilter = new BasicTemplateFilter(ms());		
		if (isInWrapper)
		{
			addWrapperInTemplates(xout,templateFilter);			
		}
		else
		{
			addWrapperOutTemplates(xout,templateFilter);
		}
	}
	
	public void makeHeaderForStandaloneWrapperXSLT(XSLOutputFile xout, boolean isInWrapper)  throws MapperException
	{
		// plain starter template of the correct mode
		xout.topOut().appendChild(starterTemplate(xout,wrapperMode(isInWrapper),false));
	}

	
	/**
	 * @param isInWrapper
	 * @return the mode to be used for wrapper XSLT templates for in- or out-wrapper
	 */
	public static String wrapperMode(boolean isInWrapper)
	{
		if (isInWrapper) return "inWrapper";
		return "outWrapper";
	}
	
	/**
	 * write out a plain starter template:
	 
	<xsl:template match="/">
		<xsl:apply-templates mode="outWrapper"/>
	</xsl:template>

	 * or a starter template with a path parameter:
	 
	<xsl:template match="/">
		<xsl:apply-templates mode="outWrapper">
				<xsl:with-param name="path" select="''"/>
		</xsl:apply-templates>
	</xsl:template>

	 * @param xout the XSL file being written
	 * @param mode the mode of the templates, typically 'inWrapper' or 'outWrapper'
	 * @param withPathParameter if true, give the applied template a path parameter
	 * @return a starter template, for use when generating standalone wrapper templates,
	 * where the generate templates pass a path as parameter
	 * @throws MapperException
	 */
	public static Element starterTemplate(XSLOutputFile xout,String mode,boolean withPathParameter) throws MapperException
	{
		Element starterTemp = xout.XSLElement("template");
		starterTemp.setAttribute("match", "/");
		
		Element applyEl = xout.XSLElement("apply-templates");
		applyEl.setAttribute("mode", mode);
		starterTemp.appendChild(applyEl);
		
		if (withPathParameter)
		{
			Element paramEl = xout.XSLElement("with-param");
			paramEl.setAttribute("name", "path");
			paramEl.setAttribute("select", "''");
			applyEl.appendChild(paramEl);
		}
				
		return starterTemp;		
	}

	
	/**	 
	 * @param xout XSLT output file
	 * @param mode mode for the identity template
	 * @return the identity template, which recursively copies an XML without change - 
	 * but more specific templates with the same mode can change named elements

	<!--   modified identity template    -->

	<xsl:template match="*" mode="outWrapper">
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates mode="outWrapper"/>
		</xsl:copy>
	</xsl:template>
	
	 * @throws MapperException
	 */
	public static Element identityTemplate(XSLOutputFile xout,String mode) throws MapperException
	{
		Element identityTemp = xout.XSLElement("template");
		identityTemp.setAttribute("match", "*");
		identityTemp.setAttribute("mode", mode);
    	
    	Element copyEl = xout.XSLElement("copy");
    	identityTemp.appendChild(copyEl);

		Element copyOfEL = xout.XSLElement("copy-of");
		copyOfEL.setAttribute("select", "@*");
		copyEl.appendChild(copyOfEL);

		Element applyIdent = xout.XSLElement("apply-templates");
		applyIdent.setAttribute("mode", mode);
		copyEl.appendChild(applyIdent);
		
		return identityTemp;		
	}
	
	/**	 
	 * @param xout XSLT output file
	 * @param mode mode for the template
	 * @param oldName node name to be replaced (possibly with condtions in [] after)
	 * @param newName the tag name to replace it
	 * @return the identity template, which recursively copies an XML without change - 
	 * but more specific templates with the same mode can change named elements

	<!--   template  to change the name of selected elements  -->

	<xsl:template match="oldName[conditions]" mode="outWrapper">
		<newName>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates mode="outWrapper"/>
		</newName>
	</xsl:template>
	
	 * @throws MapperException
	 */
	public static Element nameChangeTemplate(XSLOutputFile xout,String mode, String oldName, String newName) throws MapperException
	{
		Element renameTemp = xout.XSLElement("template");
		renameTemp.setAttribute("match", oldName);
		renameTemp.setAttribute("mode", mode);
    	
    	Element newNameEl = xout.newElement(newName);
    	renameTemp.appendChild(newNameEl);

		Element copyOfEL = xout.XSLElement("copy-of");
		copyOfEL.setAttribute("select", "@*");
		newNameEl.appendChild(copyOfEL);

		Element applyIdent = xout.XSLElement("apply-templates");
		applyIdent.setAttribute("mode", mode);
		newNameEl.appendChild(applyIdent);
		
		return renameTemp;		
	}


	/**	 
	 * @param xout XSLT output file
	 * @param mode mode for the identity template
	 * @return an identity template, which recursively copies an XML without change,
	 * and which also passes down the XPath in the source document as a parameter. 

	<!--   special identity template which passes the current path as a parameter   -->

	<xsl:template match="*" mode="inWrapper">
		<xsl:param name="path"/>
		<xsl:variable name="newPath" select="concat($path,'/',local-name(.))"/>
		<xsl:copy>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates mode="inWrapper">
				<xsl:with-param name="path" select="$newPath"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	
	 * @throws MapperException
	 */
	public static Element identityPathTemplate(XSLOutputFile xout,String mode) throws MapperException
	{
		Element identityTemp = xout.XSLElement("template");
		identityTemp.setAttribute("match", "*");
		identityTemp.setAttribute("mode", mode);
		
		Element paramEl = xout.XSLElement("param");
		paramEl.setAttribute("name", "path");
    	identityTemp.appendChild(paramEl);
		
		Element varEl = xout.XSLElement("variable");
		varEl.setAttribute("name", "newPath");
		varEl.setAttribute("select", "concat($path,'/',local-name(.))");
    	identityTemp.appendChild(varEl);
    	
    	Element copyEl = xout.XSLElement("copy");
    	identityTemp.appendChild(copyEl);

		Element copyOfEL = xout.XSLElement("copy-of");
		copyOfEL.setAttribute("select", "@*");
		copyEl.appendChild(copyOfEL);

		Element applyIdent = xout.XSLElement("apply-templates");
		applyIdent.setAttribute("mode", mode);
		copyEl.appendChild(applyIdent);
		
		Element withEl = xout.XSLElement("with-param");
		withEl.setAttribute("name", "path");
		withEl.setAttribute("select", "$newPath");
		applyIdent.appendChild(withEl);
		
		return identityTemp;		
	}

	
	/**
	 * add a template with a 'path' parameter to the XSLT transform, of the form (with parameters of this method in {} ):

	<xsl:template xmlns:v3="urn:hl7-org:v3" match="v3:{matchTagName}{conditions}" mode="outWrapper" priority="{priority}">
		<xsl:param name="path"/>
		<xsl:choose>
			<xsl:when test="$path='{requiredPath}'>
				<xsl:variable name="newPath" select="concat($path,'/','{addToPath}')"/>
				<v3:{tagName}>
					<xsl:copy-of select="@*" />
					<xsl:apply-templates mode="outWrapper">
						<xsl:with-param name="path" select="$newPath"/>
					</xsl:apply-templates>
				<v3:{tagName}>
			</xsl:when>
			<xsl:when ....>
				....
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	 *  Templates added already are stored in Hashtable templateMatches. 
	 *  If there is already a template with the same match (same matchTagName and conditions), 
	 *  but with different path, do not add a new template but add a new 'when' element 
	 *  to the existing template.
	 *  
	 * @param xout output file for the XSLT
	 * @param mode the mode of the template:  'inWrapper' or 'outWrapper'
	 * @param matchTagName tag name to be matched to apply this template
	 * @param conditions extra conditions to be matched to apply this template, inside a []
	 * @param requiredPath value of 'path' parameter to pick out the 'when' clause which is executed
	 * @param addToPath tag name to add to the 'path' parameter passed on to templates applied by this template
	 * @param tagName tag name to add to the XML output
	 * @param priority priority of this template (should be higher priority with longer conditions)
	 * @param templateMatches Hashtable of templates stored by match conditions, so there is only one template
	 * per match conditions, which may have several <when> elements for different paths
	 *
	 * @return the <when> element which is put in a new or existing template
	 * @throws MapperException
	 */
	public static Element addPathTemplate(XSLOutputFile xout,
			String mode,
			String matchTagName,
			String conditions,
			String requiredPath, 
			String addToPath,
			String tagName,
			String priority,
			Hashtable<String,Element> templateMatches)  throws MapperException
	{
		// template element
		Element templateEl = xout.XSLElement("template");
		templateEl.setAttribute("mode", mode);
		templateEl.setAttribute("priority", priority);
		templateEl.setAttribute("xmlns:v3", V3NAMESPACEURI);
		
		// add conditions to the match
		String match = addPrefix(matchTagName) + conditions;
		templateEl.setAttribute("match",match);
		
		// param element
		Element paramEl = xout.XSLElement("param");
		paramEl.setAttribute("name", "path");
		templateEl.appendChild(paramEl);
		
		// choose Element
		Element chooseEl = xout.XSLElement("choose");
		templateEl.appendChild(chooseEl);
		
		// when subtree
		Element whenEl = whenSubtree(xout,requiredPath, addToPath, tagName, mode);
		chooseEl.appendChild(whenEl);
						
		/* write the template to the xslt, if a template of this match and mode has not already been written.
		 * If a template with this match and mode has been written, add the <when> element to its <choose> */
		String matchAndMode = match + "_" + mode;
		Element templateWithThisMatchAndMode = templateMatches.get(matchAndMode);
		if (templateWithThisMatchAndMode == null)
		{
			xout.topOut().appendChild(templateEl);
			templateMatches.put(matchAndMode,templateEl);
		}
		else if (templateWithThisMatchAndMode != null)
		{
			Element  existingChoose = XMLUtil.firstNamedChild(templateWithThisMatchAndMode, "choose");
			XMLUtil.appendChildWithDocumentCheck(existingChoose, whenEl);
		}
		
		return whenEl;
		
	}
	
	/**
	 * If a name has no namespace prefix, it is in the v3 namespace, so add the 'v3' prefix to it.
	 * Otherwise add nothing.
	 * @param name
	 * @return
	 */
	private static String addPrefix(String name)
	{
		String result = name;
		StringTokenizer parts = new StringTokenizer(name,":");
		if (parts.countTokens() == 1) result = "v3:" + name;
		return result;
	}
	
	
	/**
	 * 
	 * @param xout
	 * @param requiredPath
	 * @param addToPath
	 * @param tagName
	 * @param firstChild
	 * @param applyMode
	 * @return
	 * @throws MapperException
	 */
	private static Element whenSubtree(XSLOutputFile xout, 
			String requiredPath, String addToPath, String tagName, String applyMode)  throws MapperException
	{
		// make the when test the constrained path of the tag conversion's parent
		Element whenEl = xout.XSLElement("when");
		whenEl.setAttribute("test", "$path='" + requiredPath + "'");
		
		// variable holding the new path 
		Element varEl = xout.XSLElement("variable");
		varEl.setAttribute("name", "newPath");
		varEl.setAttribute("select", "concat($path,'/','" + addToPath + "')");
		whenEl.appendChild(varEl);

		// make element with modified tag name, in the V3 namespace (if it has no prefix already)
		Element cdaEl = xout.newElement(addPrefix(tagName));
		whenEl.appendChild(cdaEl);

		// copy the attributes of the element with modified tag name
		Element copyOfEL = xout.XSLElement("copy-of");
		copyOfEL.setAttribute("select", "@*");
		cdaEl.appendChild(copyOfEL);
						
		// apply-templates to recurse down the output XML tree
		Element applyEl = xout.XSLElement("apply-templates");
		applyEl.setAttribute("mode", applyMode);
		cdaEl.appendChild(applyEl);
		
		// pass on the path as a parameter
		Element withEl = xout.XSLElement("with-param");
		withEl.setAttribute("name", "path");
		withEl.setAttribute("select", "$newPath");
		applyEl.appendChild(withEl);

		return whenEl;
	}

	
	/**
	 * Open the xsl file at the given location, and import into the main XSL
	 * file all templates in it (except the starter template if removeStarterTemplate = true).
	 * @param xout XSL output file being populated
	 * @param xslLocation
	 * @param removeStarterTemplate if true, remove the top template with match '/'
	 * (removeStarterTemplate is set false for generation of a standalone wrapper transform)
	 * @throws MapperException
	 */
	protected void addTemplatesFromFile(XMLOutputFile xout,String xslLocation, boolean removeStarterTemplate) 
	throws MapperException
	{
		try{
			Element xslRoot = XMLUtil.getRootElement(xslLocation);
			
			// collect all templates and top-level variables in one list
			Vector<Element> templates = XMLUtil.namedChildElements(xslRoot, "template");
			Vector<Element> variables = XMLUtil.namedChildElements(xslRoot, "variable");
			for (Iterator<Element> iv = variables.iterator();iv.hasNext();) templates.add(iv.next());
			
			for (Iterator<Element> it = templates.iterator();it.hasNext();)
			{
				Element templateOrVariable = it.next();
				// if removeStarterTemplate is true, the top template of the standalone XSLT file is not included 
				if ((!(templateOrVariable.getAttribute("match").equals("/")))|(!removeStarterTemplate))
				{
					Element imported = (Element)xout.outDoc().importNode(templateOrVariable, true);
					xout.topOut().appendChild(imported);
				}
			}
		}
		catch (Exception ex) {throw new MapperException("Cannot open file " + xslLocation);}		
	}
	
	

	
	//---------------------------------------------------------------------------------
	//                      Useful methods for subclasses
	//---------------------------------------------------------------------------------
	
	

	
	/**
	 * recursive scan of an XML document, keeping track of the XPath, 
	 * so that overrides in implementing classes can do specific 
	 * things at specific paths
	 */
	protected Element scanDocument(Element el, String path, int scanType)  throws MapperException
	{
		Element result = processNode(el,path,scanType);
		/* if processing the node has done special processing which 
		 * added child nodes to the result, do not add them again */
		if ((result != null) && (XMLUtil.childElements(result).size() > 0)) {}
		else for (Iterator<Element> ie = XMLUtil.childElements(el).iterator();ie.hasNext();)
		{
			Element child = ie.next();
			String step = XMLUtil.getLocalName(child);
			String newPath = path + "/" + step;
			Element childResult = scanDocument(child,newPath,scanType);
			if ((result != null) && (childResult != null)) 
				result.appendChild(childResult);
		}
		return result;
	}
	
	/**
	 * Process one node in a
	 * recursive scan of an XML document, keeping track of the XPath, 
	 * so that overrides in implementing classes can do specific 
	 * things at specific paths
	 * @param el
	 * @return
	 */
	protected Element processNode(Element el, String path, int scanType) throws MapperException
	{
		if (scanType == IN_PRE_SCAN) return inPreScanNode(el,path);
		else if (scanType == IN_TRANSFORM) return inTransformNode(el,path);
		else if (scanType == OUT_PRE_SCAN) return outPreScanNode(el,path);
		else if (scanType == OUT_TRANSFORM) return outTransformNode(el,path);
		return el;
	}
	
	//  to be overridden in implementing classes
	protected Element inPreScanNode(Element el, String path)  throws MapperException
	{return null;}
	
	/**
	 * default behaviour is a shallow copy - copying the element name, attributes,
	 * and text content only if the element has no child elements.
	 * to be overridden for specific paths in implementing classes
	 */
	protected Element inTransformNode(Element el, String path)  throws MapperException
	{
		// copy the element with namespaces, prefixed tag name, attributes but no text or child Elements
		Element copy = (Element)inResultDoc.importNode(el, false);
		
		// if the source element has no child elements but has text, copy the text
		String text = textOnly(el);
		if (!text.equals("")) copy.appendChild(inResultDoc.createTextNode(text));
		
		return copy;
	}
	
	//  to be overridden in implementing classes
	protected Element outPreScanNode(Element el, String path)  throws MapperException
	{return null;}
	
	/**
	 * default behaviour is a shallow copy - copying the element name, attributes,
	 * and text content only if the element has no child elements.
	 * to be overridden for specific paths in implementing classes
	 */
	protected Element outTransformNode(Element el, String path)  throws MapperException
	{
		// copy the element with namespaces, prefixed tag name, attributes but no text or child Elements
		Element copy = (Element)outResultDoc.importNode(el, false);
		
		// if the source element has no child elements but has text, copy the text
		String text = textOnly(el);
		if (!text.equals("")) copy.appendChild(outResultDoc.createTextNode(text));
		
		return copy;
	}
	
	/**
	 * 
	 * @param source an Element
	 * @return the text of the Element, if it has no child Elements;
	 * otherwise return ""
	 */
	protected String textOnly(Element el)
	{
		String text = "";
		if (XMLUtil.childElements(el).size() == 0) 
		{
        	NodeList nodes = el.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) 
            	if (nodes.item(i) instanceof Text)
            	{
            		Text t = (Text)nodes.item(i);
            		text = text + t.getData();
            	}			
		}
		return text;
	}
	
	/**
	 * Follow an XPath which is expected to give a unique value, and return it
	 * @param el
	 * @param path
	 * @return the value, or "" if no node is found
	 * @throws MapperException if more than 1 node is found, or for any XPath failure
	 */
	protected String getPathValue(Element el, String path) throws MapperException
	{
		boolean isAttribute = path.contains("@");
		Vector<Node> targetNodes = new Vector<Node>();
		String value = "";

		try {targetNodes = XPathAPI.selectNodeVector(el, path, ms().getNamespaceSet());}
		catch(Exception ex) {throw new MapperException("Failed follow XPath: " + path + " from element " 
				+ el.getLocalName() + "; " + ex.getMessage());}
		
		if (targetNodes.size() > 1) throw new MapperException("Following XPath: " + path 
				+ " from element " + el.getLocalName() + " gave  " + targetNodes.size() + " nodes.");
		
		if ((targetNodes.size() == 1))
		{
			Node node = targetNodes.get(0);
			if (isAttribute) value = ((Attr)node).getNodeValue();
			else value = textOnly((Element)node);
		}		
		return value;		
	}
	
	//--------------------------------------------------------------------------------------------------------
	//   Altering the templates produced by XSLGenerator, so they pass through text subtrees unchanged
	//--------------------------------------------------------------------------------------------------------
	
	/**
	 * Alter the templates written by XSLGenerator so that:
	 * (a) <text> elements in the input are expected to be in the V3 namespace (if they are not already)
	 * (b) the subtrees below <text> elements are passed through unchanged
	 * @param xout the XSL output file
	 * @param fromV3ToNoNamespace true if the transform is from a full CDA message (in the V3 namespace)
	 * to a simplified message (in no namespace); false for the reverse direction
	 */
	protected void passThroughTextSubtrees(XSLOutputFile xout, boolean fromV3ToNoNamespace) throws MapperException
	{
		trace("Changing templates for text; from V3 is "  + fromV3ToNoNamespace);
		// ensure the V3 namespace is declared on the top element of the XSLT.
		xout.topOut().setAttribute("xmlns:" + V3PREFIX, AbstractMapperWrapper.V3NAMESPACEURI);
				
		Vector<String> textModes = findTextModes(xout,fromV3ToNoNamespace);
		
		boolean replaced = enhanceTextTemplates(textModes, xout,fromV3ToNoNamespace);
		
		// if any templates have been replaced, add the copy template
		if (replaced) 
		{
			if (CDAConverter.SIMPLE_MESSAGE_IN_V3_NAMESPACE) addSimpleCopyTemplate(xout);
			else addNamespaceChangingCopyTemplates(xout,fromV3ToNoNamespace);
		}
	}
	
	/**
	 * 
	 * @param xout
	 * @return Vector of all the modes for templates for <text> nodes
	 */
	private Vector<String> findTextModes(XSLOutputFile xout, boolean fromV3ToNoNamespace)
	{
		/* pass over all templates, making sure <text> nodes are expected 
		 * in the V3 namespace, and finding the modes of templates applied to them */
		Vector<String> textModes = new Vector<String>();
		for (Iterator<Element> it = XMLUtil.childElements(xout.topOut()).iterator();it.hasNext();)
		{
			Element temp = it.next();
			// find a child element of the template not in the xsl namespace (defining an element to be written out)
			for (Iterator<Element> iu = XMLUtil.childElements(temp).iterator();iu.hasNext();)
			{
				Element child = iu.next();
				String uri = child.getNamespaceURI();
				if ((uri == null)|((uri != null) && (!uri.equals(XMLUtil.XSLURI))))
				{
					// go over child elements of the element being written out
					NodeList nl = child.getChildNodes();
					for (int i = 0; i < nl.getLength(); i++)
					{
						if (nl.item(i) instanceof Element)
						{
							Element el = (Element)nl.item(i);
							// look for xsl:variable elements whose 'select' ends in 'text', and note their position
							if ((el.getLocalName() != null) && (el.getLocalName().equals("variable")))
							{
								String select = el.getAttribute("select");
								// the last step in the path must be 'text' with or without a prefix
								StringTokenizer steps = new StringTokenizer(select,"/");
								String step = "";
								while (steps.hasMoreTokens()) step = steps.nextToken();
								if (((step.equals("text"))|(step.endsWith(":text"))) && (i < nl.getLength() -2))
								{
									trace("Node name " + child.getLocalName());
									trace("Select " + select);
									// find the name of the next variable
									String vName = ((Element)nl.item(i+1)).getAttribute("name");
									// find the mode of the apply-templates whose select is that variable
									String mode = findApplyMode(nl,vName);
									if (mode != null)
									{
										textModes.add(mode);
										trace("Change template with mode " + mode);									
									}
								}
							}							
						}
					}
				}
			}
		}
		return textModes;
	}
	
	/**
	 * 
	 * @param textModes
	 * @param xout
	 * @return
	 */
	private boolean enhanceTextTemplates(Vector<String> textModes, XSLOutputFile xout, boolean fromV3ToNoNamespace)
	throws MapperException
	{
		boolean replaced = false;
		for (Iterator<Element> it = XMLUtil.childElements(xout.topOut()).iterator();it.hasNext();)
		{
			Element temp = it.next();
			String mode = temp.getAttribute("mode");
			if (GenUtil.inVector(mode, textModes))
			{
				replaced = true;

				// find the child element with local name 'text'
				Element textChild = XMLUtil.firstNamedChild(temp, "text");
				if (textChild != null) 
				{
					// remove any child of the <text> element which copied text contents, like <xsl:value-of select="$textConten_13"/>
					Element valueChild =XMLUtil.firstNamedChild(textChild, "value-of");
					if (valueChild != null) textChild.removeChild(valueChild);
									
					// add a child to the <text> element, to apply a copy template 
					Element apply = xout.XSLElement("apply-templates");
					apply.setAttribute("mode", "copy");
					apply.setAttribute("select", "node()");
					textChild.appendChild(apply);					
				}
				// else throw new MapperException("Found no expected text element in template of mode " + mode);
				
			}
		}
		return replaced;
	}
	
	/**
	 * add two templates:
	 * 
	 
	 <xsl:template match="element()" mode="copy">
		<xsl:element name="{local-name()}"  namespace="">
			<xsl:apply-templates mode="copy" select="@*|node()"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="@*|text()" mode="copy">
		<xsl:copy/>
	</xsl:template>

	 * namespace="" if fromV3ToNoNamespace = true, 
	 * or namespace="v3" if fromV3ToNoNamespace = false
	 * 
	 * @param xout
	 * @throws MapperException
	 */
	private void addNamespaceChangingCopyTemplates(XSLOutputFile xout, boolean fromV3ToNoNamespace) throws MapperException
	{
		
		// first template
		Element copyElTemplate = xout.XSLElement("template");
		copyElTemplate.setAttribute("match", "element()");
		copyElTemplate.setAttribute("mode", "copy");

		// add an element child, copying the name and with namespace depending on direction of translation
		Element el = xout.XSLElement("element");
		el.setAttribute("name", "{local-name()}");
		String namespace= "";
		if (!fromV3ToNoNamespace) namespace = V3NAMESPACEURI;
		el.setAttribute("namespace", namespace);
		copyElTemplate.appendChild(el);

		// add a child to apply the copy template again
		Element apply = xout.XSLElement("apply-templates");
		apply.setAttribute("select", "@*|node()");
		apply.setAttribute("mode", "copy");
		el.appendChild(apply);
		
		// second template		
		Element copyOtherTemplate = xout.XSLElement("template");
		copyOtherTemplate.setAttribute("match", "@*|text()");
		copyOtherTemplate.setAttribute("mode", "copy");
		
		Element copyEl = xout.XSLElement("copy");
		copyOtherTemplate.appendChild(copyEl);

		xout.topOut().appendChild(copyElTemplate);
		xout.topOut().appendChild(copyOtherTemplate);		
	}
	
	/**
	 * add a simple copy template, which does not change namespaces
	 * @param xout
	 * @throws MapperException
	 */
	private void addSimpleCopyTemplate(XSLOutputFile xout) throws MapperException
	{
		Element copyOtherTemplate = xout.XSLElement("template");
		copyOtherTemplate.setAttribute("match", "@*|node()");
		copyOtherTemplate.setAttribute("mode", "copy");
		
		Element copyEl = xout.XSLElement("copy");
		copyOtherTemplate.appendChild(copyEl);

		// add a child to apply the copy template again
		Element apply = xout.XSLElement("apply-templates");
		apply.setAttribute("select", "@*|node()");
		apply.setAttribute("mode", "copy");
		copyEl.appendChild(apply);

		xout.topOut().appendChild(copyOtherTemplate);				
	}

	/**
	 * @param nl a list of child elements of an element
	 * @param vName the name of a variable
	 * @return find any apply-templates element in the node list , whose select
	 * matches the variable name, and return its mode;
	 * if not found return null
	 */
	private String findApplyMode(NodeList nl,String vName)
	{
		String mode = null;
		for (int i = 0; i < nl.getLength(); i++)
		{
			Element el = (Element)nl.item(i);
			if ((el.getLocalName().equals("apply-templates")) && (el.getAttribute("select").equals("$" + vName)))
				mode = el.getAttribute("mode");
		}
		return mode;
	}
	
	
	//-----------------------------------------------------------------------------------------
	//            copying subtrees under <text> nodes unchanged from input to output
	//-----------------------------------------------------------------------------------------

	/**
	 * save subtrees below <text> nodes in a Hashtable, to be recovered by the output
	 * wrapper transform. Put the key to the Hashtable as text content of the element
	 */
	protected Element saveInputTextSubtree(Element el) throws MapperException
	{
		if (keptSubtrees == null) throw new MapperException("Table to save text subtrees has not been initialised");

		// retain the subtree in the table, with a generated key
		String key = "key_" + keyIndex;
		keyIndex++;
		keptSubtrees.put(key, el);

		// copy the element with namespaces, prefixed tag name, attributes but no element subtree, and the text key
		Element copy = (Element)inResultDoc.importNode(el, false);
		copy.appendChild(inResultDoc.createTextNode(key));
		return copy;		
	}
	
	/**
	 * recover a subtree below a <text> element, as saved by the input wrapper transform, 
	 * and put it in the output-wrapped result.
	 * @param el <text> element whose text content is the key to the hashtable of subtrees, if it exists
	 * @param path path to this element (for error messages only)
	 * @return <text> element with its subtree, for inclusion in the output
	 * @throws MapperException
	 */
	protected Element recoverInputTextSubtree(Element el, String path) throws MapperException
	{
		String key = XMLUtil.getText(el);
		// subtrees have been explicitly set for this output wrapper transform
		if (keptSubtreesSet)
		{
			Element subtree = keptSubtrees.get(key);
			if (subtree != null)
			{
				Element copy = (Element)outResultDoc.importNode(subtree, true);
				return copy;					
			}
			// input wrapper failed to save this <text> subtree 
			else throw new MapperException("Found no subtree for <text> node with key '" + key + "' at path '" + path + "'");
		}
		// the input wrapper transform saved text subtrees
		else if ((inputWrapper != null) && (inputWrapper.keptSubtrees() != null))
		{
			Element subtree = inputWrapper.keptSubtrees().get(key);
			if (subtree != null)
			{
				Element copy = (Element)outResultDoc.importNode(subtree, true);
				return copy;					
			}
			// input wrapper failed to save this <text> subtree 
			else throw new MapperException("Found no saved subtree for <text> node with key '" + key + "' at path '" + path + "'");
		}
		// if the input wrapper transform did not save text subtrees, do not fail
		else
		{
			// copy the element with namespaces, prefixed tag name, attributes but no child Elements, and unchanged text content
			Element copy = (Element)outResultDoc.importNode(el, false);
			copy.appendChild(outResultDoc.createTextNode(XMLUtil.getText(el)));
			return copy;				
		}		
	}
	
	//-----------------------------------------------------------------------------------------
	//                                             trivia
	//-----------------------------------------------------------------------------------------

	protected void trace(String s) {if (tracing()) System.out.println(s);}
		
	protected boolean tracing() {return true;}
	
	protected int steps(String path) {return (new StringTokenizer(path,"/").countTokens());}


}
