package com.openMap1.mapper.converters;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ValueCondition;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.TemplateFilter;

/**
 * abstract superclass of the two different NHS CDA wrapper transforms:
 * 
 * (1) NHS_V3_Converter, for usual V3 with RMIMs
 * (2) NHS_CDA_Wire2, for wire format CDA
 * 
 * Collects together what they have in common
 * 
 * @author Robert
 *
 */

abstract public class NHS_CDA_Wrapper  extends AbstractMapperWrapper implements MapperWrapper{
	
	protected boolean tracing() {return false;}
	
	protected static String topClassName = "ClinicalDocument";

	public static String NHSPREFIX = "npfitlc";
	
	public static String NHSURI = "NPFIT:HL7:Localisation";
	
	public static String CONTENTID_ROOT = "2.16.840.1.113883.2.1.3.2.4.18.16";
	
	public static String TEMPLATEID_ROOT = "2.16.840.1.113883.2.1.3.2.4.18.2";
	
	public static String[] IN_NHS_NAMESPACE = {"messageType","contentId","recipientRoleCode"};
	

	//----------------------------------------------------------------------------------------
	//                                        Constructor
	//----------------------------------------------------------------------------------------

	/**
	 * 
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare spare argument, just in case....
	 */
	public NHS_CDA_Wrapper(MappedStructure ms, Object spare)  throws MapperException
	{
		super(ms,spare);
	}
	
	/**
	 * @return the file extension of the outer document, with initial '*.'
	 */
	public String fileExtension() {return ("*.xml");}

	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	public int transformType() {return AbstractMapperWrapper.XML_TYPE;}

	//----------------------------------------------------------------------------------------
	//                     In-Wrapper  Transform
	//----------------------------------------------------------------------------------------


	public Document transformIn(Object CDARootObj) throws MapperException 
	{
		// initialise for saving subtrees under <text> nodes
		keptSubtrees = new Hashtable<String,Element>();
		keyIndex = 0;
		
		if (!(CDARootObj instanceof Element)) throw new MapperException("CDA root is not an Element");
		Element CDARoot = (Element)CDARootObj;
		inResultDoc = XMLUtil.makeOutDoc();

		String namespaceURI = CDARoot.getNamespaceURI();
		if (namespaceURI == null) throw new MapperException("CDA root element has no namespace");
		if (!namespaceURI.equals(CDAConverter.V3NAMESPACEURI))
			throw new MapperException("CDA root element namespace '" + namespaceURI 
					+ "' is not the HL7 V3 namespace '" + CDAConverter.V3NAMESPACEURI + "'");

		// do not change the top tag name, in case it is not 'ClinicalDocument' when simplifying CMETs
		String newTagName = CDARoot.getLocalName();
		ElementDef topElementDef = ms().getRootElement();
		Element constrainedRoot = constrainedElement(newTagName,CDARoot,topElementDef);
		inResultDoc.appendChild(constrainedRoot);
		
		return inResultDoc;
	}
	
	abstract Element constrainedElement(String newTagName, Element CDARoot,ElementDef topElementDef) throws MapperException;

	protected Element moveInElementOnly(String newTagName,Element cdaElement)
	{
		// keep elements in the same namespace (V3 or NPfIT)
		Element constrainedEl = inResultDoc.createElementNS(cdaElement.getNamespaceURI(), newTagName);
		
		// copy across all attributes to the constrained element, including namespace attributes
		for (int a = 0; a < cdaElement.getAttributes().getLength();a++)
		{
			Attr at = (Attr)cdaElement.getAttributes().item(a);
			constrainedEl.setAttribute(at.getName(), at.getValue());
		}
		
		// add RIM structural attributes with constant values to name parts
		addRIMStructuralAttributes(constrainedEl);
		
		// if there are no child elements, copy any text content
		if (XMLUtil.childElements(cdaElement).size() == 0)
		{
			String text = XMLUtil.getText(cdaElement);
			if ((text != null) && (text.length() > 0)) constrainedEl.appendChild(inResultDoc.createTextNode(text));
		}
		return constrainedEl;
		
	}
	
	/**
	 * add RIM structural attributes with constant values to name parts
	 * and to names with no parts
	 * @param el
	 */
	protected void addRIMStructuralAttributes(Element el)
	{
		String[] nameParts = {"name","prefix","given","family","suffix"};
		String[] partTypes = {"","PFX","GIV","FAM","SFX"};
		String tag = el.getLocalName();
		if ((GenUtil.inArray(tag, nameParts)) && (XMLUtil.childElements(el).size() == 0))
		{
			el.setAttribute("mediaType","text/plain");
			el.setAttribute("representation","TXT");
			for (int i = 0; i < nameParts.length;i++)
				if ((tag.equals(nameParts[i])) && (partTypes[i].length() > 0))
					el.setAttribute("partType", partTypes[i]);
		}
	}

	//----------------------------------------------------------------------------------------
	//                     Out-Wrapper  Transform
	//----------------------------------------------------------------------------------------


	public Document transformOut(Element constrainedRoot) throws MapperException {
		outResultDoc = XMLUtil.makeOutDoc();

		String namespaceURI = constrainedRoot.getNamespaceURI();
		if (namespaceURI == null) throw new MapperException("CDA root element has no namespace");
		if (!namespaceURI.equals(CDAConverter.V3NAMESPACEURI))
			throw new MapperException("CDA root element namespace '" + namespaceURI 
					+ "' is not the HL7 V3 namespace '" + CDAConverter.V3NAMESPACEURI + "'");

		// do not change the top tag name, in case it is not 'ClinicalDocument' when simplifying CMETs
		String newTagName = constrainedRoot.getLocalName();
		ElementDef topElementDef = ms().getRootElement();
		Element cdaRoot = outWrappedV3Element(newTagName,constrainedRoot,topElementDef);
		outResultDoc.appendChild(cdaRoot);
		
		return outResultDoc;
	}
	
	abstract Element outWrappedV3Element(String newTagName,Element constrainedElement,ElementDef topElementDef) throws MapperException;

	/**
	 * 
	 * @param el an element in the message before out-wrapper transform
	 * @return if the element has a 'templateId' grandchild beneath
	 * a child node, the value of its 'extension' attribute.
	 * If hasInWrappedNodeNames is true, the child node must have a
	 * tag name like 'COCD_TP145022UK02.AssignedEntitySDS'  with a template name before '.'
	 * 
	 */
	protected String instanceGrandChildTemplateIdExtension(Element el, boolean hasInWrappedNodeNames)
	{
		String extension = "";
		for (Iterator<Element> ie = XMLUtil.childElements(el).iterator();ie.hasNext();)
		{
			Element child = ie.next();
			
			if ((!hasInWrappedNodeNames)|(isTemplateTag(child.getLocalName())))
			{
				Element templateId = XMLUtil.firstNamedChild(child, "templateId");
				if (templateId != null) extension = templateId.getAttribute("extension");				
			}
		}
		return extension;
	}
	

	/**
	 * 
	 * @param elDef an ElementDef  node in a full mapping set
	 * @return if it has an object mapping with path
	 *  like 'COCD_TP145022UK02.AssignedEntitySDS/templateId/@extension'
	 * the value of the condition which fixes the extension attribute,
	 *  or if an object mapping requiring that value is on a parent or a child
	 * Otherwise return "". 
	 */
	protected String mappingGrandChildTemplateIdExtension(ElementDef elDef)
	{
		/* the mappings which define the template id are usually on this node (Participation or ActRelationship),
		 * not on the child Act or Role */
		String extension = getTemplateIdExtension(elDef,3,null);

		// sometimes the relevant mappings can be on the parent ElementDef; but the path must go down through this node
		if (extension.equals("")) 
		{
			extension = getAncestorExtension(elDef,"",3);
		}

		// the relevant mappings can be on a child Act or Role, if the association has not been flattened
		if (extension.equals("")) for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef child = it.next();
			if (isTemplateTag(child.getName())) extension = getTemplateIdExtension(child,2,null);
		}

		return extension;		
	}
	
	/**
	 * recursive ascent of ancestor ElementDefs, looking for an object mapping fixed value to define the grandchild template
	 * id of this node
	 * @param elDef
	 * @param path
	 * @param length
	 * @return
	 */
	private String getAncestorExtension(ElementDef elDef, String path, int length)
	{
		String extension = "";
		String nextPath = elDef.getName() + "/" + path;
		int nextLength = length + 1;
		EObject outer = elDef.eContainer();
		if (outer instanceof ElementDef) 
		{
			ElementDef parent = (ElementDef)outer;
			extension = getTemplateIdExtension(parent,nextLength,nextPath);
			// if a node does not give a real extension, try its parent
			if (extension.equals("")) extension = getAncestorExtension(parent,nextPath,nextLength);
		}
		return extension;
	}
	
	/**
	 * search all object mappings on a node for value conditions on templateId extensions,
	 * with a given length of the value condition path. 
	 * If one is found, return the extension value
	 * @param elDef an ElementDef with maybe some object mappings on it
	 * @param pathLength an intyeger 3 or more
	 * @param requiredFirstNode - if non-null, the first node on the  path must be this
	 * @return
	 */
	protected String getTemplateIdExtension(ElementDef elDef,int pathLength, String requiredFirstNode)
	{
		String extension = "";
		NodeMappingSet nms = elDef.getNodeMappingSet();
		if (nms != null)
		{
			// find the appropriate value condition on any object mapping
			for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator();it.hasNext();)
			{
				ObjMapping om = it.next();
				for (Iterator<MappingCondition> iu = om.getMappingConditions().iterator();iu.hasNext();)
				{
					MappingCondition mc = iu.next();
					String ext = getTemplateIdExtension(mc,pathLength,requiredFirstNode);
					if (!ext.equals(""))  extension = ext;
				}
			}
		}
		return extension;
	}
	
	/**
	 * find the value of the attribute 'extension' of a templateId node,
	 * required for a value condition
	 * on an object mapping, with a given path length of the condition path
	 * @param mc
	 * @param pathLength an integer 3 or more
	 * @param requiredFirstNode - if non-null, the first node on the  path must be this
	 * @return
	 */
	private String getTemplateIdExtension(MappingCondition mc, int pathLength, String requiredFirstNode)
	{
		String extension = "";
		if (mc instanceof ValueCondition)
		{
			// find a value condition with path ending like 'COCD_TP145201GB01.PatientRole/templateId/@extension'
			ValueCondition vc = (ValueCondition)mc;
			StringTokenizer path = new StringTokenizer(vc.getLeftPath(),"/");
			if (path.countTokens() == pathLength) 
			{
				if (pathLength == 2)
				{
					if ((path.nextToken().equals("templateId"))
							&& (path.nextToken().equals("@extension")))
						extension = vc.getRightValue();										
				}
				else if (pathLength > 2)
				{
					// strip off all except the last 3 steps
					for (int i = 0; i < pathLength - 3; i++) path.nextToken();
					// check the last 3 steps of the path
					if ((isTemplateTag(path.nextToken()))
						&& (path.nextToken().equals("templateId"))
						&& (path.nextToken().equals("@extension")))
					extension = vc.getRightValue();					
				}
				
				// if there is a condition on the first node of the path, and it fails, return ""
				if ((requiredFirstNode != null) && (!vc.getLeftPath().startsWith(requiredFirstNode))) extension = "";
			}
		}
		return extension;
	}
	
	/**
	 * @param tagName
	 * @return true if the tag name starts with a template id, then a '.'
	 * This test has now been disabled to return true always; some cases do not obey it, and 
	 * the other tests (looking for templateId/@extension) appear strict enough
	 */
	private boolean isTemplateTag(String tagName)
	{
		StringTokenizer childTag = new StringTokenizer(tagName,".");
		@SuppressWarnings("unused")
		boolean strictTemplateTag = ((childTag.countTokens() == 2) && (childTag.nextToken().length() == "COCD_TP145022UK02".length()));	
		return true;
	}


	/**
	 * 
	 * @param theClass
	 * @return true if the class is a class that has its templateId 
	 * on a grandchild node rather than a child node, and has a contentId child
	 */
	protected boolean renamableTag(Element el)
	{
		String type = el.getAttribute("typeCode");
		return (type.length() > 0);
	}
	
	protected void giveContentIdChild(String ext,Element cdaEl, Element resultElement)
	{
		if ((ext != null) && (!ext.equals("")) && (renamableTag(resultElement)) 
				&& (XMLUtil.firstNamedChild(resultElement, "contentId") == null))
			cdaEl.appendChild(contentIdElement(outResultDoc,ext));
	}
	
	/**
	 * 
	 * @param outDoc
	 * @param extension
	 * @return a contentId element in the NHS local namespace
	 */
	protected Element contentIdElement(Document outDoc, String extension)
	{
		Element contentIdEl = outDoc.createElementNS(NHSURI, NHSPREFIX + ":contentId");
		contentIdEl.setAttribute("root", CONTENTID_ROOT);
		contentIdEl.setAttribute("extension", extension);
		return contentIdEl;
	}
	
	/**
	 * 
	 * @param outDoc
	 * @param extension
	 * @return a templateId element in the V3 namespace
	 */
	protected Element templateIdElement(Document outDoc, String extension)
	{
		Element templateIdEl = outDoc.createElementNS(V3NAMESPACEURI, "templateId");
		templateIdEl.setAttribute("root", TEMPLATEID_ROOT);
		templateIdEl.setAttribute("extension", extension);
		return templateIdEl;
	}
	
	//----------------------------------------------------------------------------------------
	//      XSLT versions of wrapper transforms, for inclusion in full XSLT transforms
	//----------------------------------------------------------------------------------------
	

	/**
	 * @param xout XSLT output being made
	 * @param templateFilter a filter on the templates, implemented by XSLGeneratorImpl
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'in' direction.
	 * Templates must have mode = "inWrapper"
	 * 
	 * This method works by reading a file which is a standalone XSL version
	 * of the in-wrapper transform, removing the top template,
	 * then adding all other templates and variables to the full XSLT.
	 * 
	 *  XSLGenerator provides a variable:

	<xsl:variable name="inWrapper_result">
		<xsl:apply-templates mode="inWrapper" />
	</xsl:variable>
	
	which replaces the top template:

    <xsl:template match="/">
		<xsl:apply-templates mode="inWrapper"/>
	</xsl:template>

	 *  of the standalone XSLT file
	 * @throws MapperException
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter, boolean addTemplatesFromFile)  
	throws MapperException
	{
		
		
		// declare namespaces at the top of the xslt
		declareNamespaces(xout);

		// alter generated templates to pass through subtrees under text nodes unchanged
		passThroughTextSubtrees(xout,true);
		
		if (addTemplatesFromFile)
		{
			/* the standalone NHS wrapper in-transform should be in a file NHSInWrapper.xsl, 
			 * in a sub-folder of the same folder as the NHS mapper file. */
			boolean isInWrapper = true;
			String xslLocation = templateFilter.getXSLLocation(wrapperXSLFileName(isInWrapper),isInWrapper);

			boolean removeStarterTemplate = true;
			addTemplatesFromFile(xout,xslLocation,removeStarterTemplate);					
		}
				
	}
	
	/**
	 * @param xout XSLT output being made
	 * @param templateFilter a filter on the templates to be included, implemented by XSLGeneratorImpl
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'out' direction.
	 * Templates must have mode = "outWrapper"
	 * @throws MapperException
	 */
	public void addWrapperOutTemplates(XSLOutputFile xout, TemplateFilter templateFilter, boolean addTemplatesFromFile)
	throws MapperException
	{		
		// declare namespaces at the top of the xslt
		declareNamespaces(xout);
		
		// alter generated templates to pass through subtrees under text nodes unchanged
		passThroughTextSubtrees(xout,false);
		
		if (addTemplatesFromFile)
		{
			/* the standalone NHS wrapper out-transform should be in a file NHSOutWrapper.xsl, 
			 * in a subfolder of the same folder as the mapper file. */
			boolean isInWrapper = false;
			String xslLocation = templateFilter.getXSLLocation(wrapperXSLFileName(isInWrapper),isInWrapper);		
					
			boolean removeStarterTemplate	= true;
			addTemplatesFromFile(xout,xslLocation,removeStarterTemplate);					
		}

	}
	
	/**
	 * declare V3 and NHS namespaces at the top of an XSLT transform file
	 * @param xout
	 */
	protected void declareNamespaces(XSLOutputFile xout)
	{
		xout.topOut().setAttribute("xmlns:" + NHSPREFIX, NHSURI);
		xout.topOut().setAttribute("xmlns:" + AbstractMapperWrapper.V3PREFIX, AbstractMapperWrapper.V3NAMESPACEURI);		
	}
	
	/**
	 * make a standalone wrapper XSLT file at the specified file location
	 * @param xout an empty xsl output file which has been created, and will be written to a location
	 * after the end of this call
	 * @param isInWrapper - true if it is to be an in-wrapper transform, false for an out-wrapper transform
	 */
	public void makeHeaderForStandaloneWrapperXSLT(XSLOutputFile xout, boolean isInWrapper) throws MapperException
	{
		
		// declare namespaces at the top of the xslt
		declareNamespaces(xout);
				
		/* the standalone NHS wrapper out-transform should be in a file, 
		 * in the same folder as the mapper file. */
		String xslLocation = FileUtil.getXSLLocation(wrapperXSLFileName(isInWrapper),ms());		
		boolean removeStarterTemplate= false;
		addTemplatesFromFile(xout,xslLocation,removeStarterTemplate);		
	}

	
	abstract String wrapperXSLFileName(boolean isIn);
	
	protected void trace(String s) {if (tracing()) System.out.println(s);}
	

}
