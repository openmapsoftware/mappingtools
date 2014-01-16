package com.openMap1.mapper.converters;

import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.TemplateFilter;

public class FACEWrapper extends AbstractMapperWrapper implements MapperWrapper{
	
	public static String FACE_PREFIX = "face";
	
	public static String FACE_URI = "http://schemas.facecode.com/webservices/2010/01/";

	//----------------------------------------------------------------------------------------
	//                     Constructor and initialisation from the Ecore model
	//----------------------------------------------------------------------------------------
	
	public FACEWrapper(MappedStructure ms, Object spare) throws MapperException
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
	//                             In-wrapper transform
	//----------------------------------------------------------------------------------------

	@Override
	public Document transformIn(Object incoming) throws MapperException 
	{
		if (!(incoming instanceof Element)) throw new MapperException("Document root is not an Element");
		Element mappingRoot = (Element)incoming;
		
		String mappingRootPath = "/GetIndicativeBudget";
		inResultDoc = XMLUtil.makeOutDoc();
				
		Element inRoot = scanDocument(mappingRoot, mappingRootPath, AbstractMapperWrapper.IN_TRANSFORM);
		inResultDoc.appendChild(inRoot);
		return inResultDoc;		
	}
	/**
	 * default behaviour is a shallow copy - copying the element name, attributes,
	 * and text content only if the element has no child elements.
	 * to be overridden for specific paths in implementing classes
	 */
	protected Element inTransformNode(Element el, String path)  throws MapperException
	{
		// copy the element with namespaces, prefixed tag name, attributes but no text or child Elements
		Element copy = (Element)inResultDoc.importNode(el, false);
		
		// convert <FaceCompletedItem> elements to specific types of item
		if (XMLUtil.getLocalName(el).equals("FaceCompletedItem"))
		{
			String questionCode = getPathValue(el,"QuestionId");
			String newName = "FaceCompletedItem_" + questionCode;
			copy = renameElement(el, newName, true);
		}

		// if the source element has no child elements but has text, copy the text
		String text = textOnly(el);
		if (!text.equals("")) copy.appendChild(inResultDoc.createTextNode(text));
		
		return copy;
	}

	//----------------------------------------------------------------------------------------
	//                             Out-wrapper transform
	//----------------------------------------------------------------------------------------

	@Override
	public Object transformOut(Element outgoing) throws MapperException 
	{
		String mappingRootPath = "/Envelope";
		outResultDoc = XMLUtil.makeOutDoc();

		Element outRoot = scanDocument(outgoing, mappingRootPath, AbstractMapperWrapper.OUT_TRANSFORM);
		outResultDoc.appendChild(outRoot);
		return outResultDoc;
	}

	/**
	 * default behaviour is a shallow copy - copying the element name, attributes,
	 * and text content only if the element has no child elements.
	 * to be overridden for specific paths in implementing classes
	 */
	protected Element outTransformNode(Element el, String path)  throws MapperException
	{
		// copy the element with namespaces, prefixed tag name, attributes but no text or child Elements
		Element copy = (Element)outResultDoc.importNode(el, false);
		
		// convert specific types of <FaceCompletedItem_XX> back to plain <FACECompletedItem>
		if (XMLUtil.getLocalName(el).startsWith("FaceCompletedItem"))
		{
			copy = renameElement(el,"FaceCompletedItem",false);			
		}

		// if the source element has no child elements but has text, copy the text
		String text = textOnly(el);
		if (!text.equals("")) copy.appendChild(outResultDoc.createTextNode(text));
		
		return copy;
	}
	/**
	 * copy an element and all its attributes to the new document, renaming it
	 * and putting it in no namespace.
	 * @param el
	 * @param newName
	 * @param isIn true for the in-transform, false for the out-transform
	 * @return
	 * @throws MapperException
	 */
	protected Element renameElement(Element el, String newName, boolean isIn) throws MapperException
	{
		Element newEl = null;
		if (isIn) newEl = inResultDoc.createElementNS(FACE_URI, newName);
		else if (!isIn) newEl = outResultDoc.createElementNS(FACE_URI, newName);
		
		// set all attributes of the constrained element, including namespace attributes
		for (int a = 0; a < el.getAttributes().getLength();a++)
		{
			Attr at = (Attr)el.getAttributes().item(a);
			newEl.setAttribute(at.getName(), at.getValue());
		}
		return newEl;
	}
	
	//--------------------------------------------------------------------------------------------------------------
	//                                   XSLT Wrapper Transforms
	//--------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * @param xout XSLT output being made
	 * @param templateFilter a filter on the templates, implemented by XSLGeneratorImpl
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'in' direction.
	 * Templates must have mode = "inWrapper"
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		// see class AbstractMapperWrapper - adds a plain identity template
		super.addWrapperInTemplates(xout, templateFilter);
		
		// add the FACE namespace
		xout.topOut().setAttribute("xmlns:" + FACE_PREFIX, FACE_URI);
		
		for (Iterator<ElementDef> it = findFACEItemsElementDefs(ms()).iterator();it.hasNext();)
		{
			ElementDef FACEItem = it.next();
			String tagName = FACEItem.getName();
			if (tagName.startsWith("FaceCompletedItem_"))
			{
				String questionId = tagName.substring("FaceCompletedItem_".length());
				addInTemplate(xout,tagName,questionId);
			}
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
	public void addWrapperOutTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		// see class AbstractMapperWrapper - adds a plain identity template
		super.addWrapperOutTemplates(xout, templateFilter);	

		// add the FACE namespace
		xout.topOut().setAttribute("xmlns:" + FACE_PREFIX, FACE_URI);
		
		for (Iterator<ElementDef> it = findFACEItemsElementDefs(ms()).iterator();it.hasNext();)
		{
			ElementDef FACEItem = it.next();
			String tagName = FACEItem.getName();
			if (tagName.startsWith("FaceCompletedItem_"))
			{
				String questionId = tagName.substring("FaceCompletedItem_".length());
				addOutTemplate(xout,tagName,questionId);
			}
		}
	}
	
	/**
	 * add an in-wrapper template of the form 
	
	<xsl:template match="face:FaceCompletedItem[face:QuestionId='F14_14_46_11_15_33T61_38']" mode="inWrapper">
		<face:FaceCompletedItem_F14_14_46_11_15_33T61_38>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates mode="inWrapper"/>
		</face:FaceCompletedItem_F14_14_46_11_15_33T61_38>
	</xsl:template>

	 * @param xout
	 * @param tagName
	 * @param questionId
	 */
	private void addInTemplate(XSLOutputFile xout,String tagName,String questionId)  throws MapperException
	{
		Element tempEl = xout.XSLElement("template");
		tempEl.setAttribute("match", FACE_PREFIX + ":FaceCompletedItem[" + FACE_PREFIX + ":QuestionId='" + questionId + "']");
		tempEl.setAttribute("mode", "inWrapper");
		
	    Element FACEEl = xout.NSElement(FACE_PREFIX, tagName, FACE_URI);
	    tempEl.appendChild(FACEEl);
	    
	    addApplyChildren(xout,FACEEl,"inWrapper");
	    
	    xout.topOut().appendChild(tempEl);
	}

	
	/**
	 * add an out-wrapper template of the form 
	
	<xsl:template match="face:FaceCompletedItem_F14_14_46_11_15_33T61_38" mode="outWrapper">
		<face:FaceCompletedItem>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates mode="outWrapper"/>
		</face:FaceCompletedItem>
	</xsl:template>

	 * @param xout
	 * @param tagName
	 * @param questionId
	 */
	private void addOutTemplate(XSLOutputFile xout,String tagName,String questionId)  throws MapperException
	{
		Element tempEl = xout.XSLElement("template");
		tempEl.setAttribute("match", FACE_PREFIX + ":" + tagName);
		tempEl.setAttribute("mode", "outWrapper");
		
	    Element FACEEl = xout.NSElement(FACE_PREFIX, "FaceCompletedItem", FACE_URI);
	    tempEl.appendChild(FACEEl);
	    
	    addApplyChildren(xout,FACEEl,"outWrapper");
	    
	    xout.topOut().appendChild(tempEl);
	}
	
    /**
     * add two child nodes to a template to carry on copying down the tree
     * @param xout
     * @param FACEEl
     * @param mode
     * @throws MapperException
     */
	private void addApplyChildren(XSLOutputFile xout,Element FACEEl, String mode) throws MapperException
    {
    	Element copyOfEl = xout.XSLElement("copy-of");
    	copyOfEl.setAttribute("select", "@*");
    	FACEEl.appendChild(copyOfEl);

    	Element applyEl = xout.XSLElement("apply-templates");
    	applyEl.setAttribute("mode", mode);
    	FACEEl.appendChild(applyEl);
}

	
	/**
	 * 
	 * @param mappedStructure
	 * @return a lit of nodes in the mapping set which are children of the 'Items' node
	 * @throws MapperException
	 */
	public static EList<ElementDef> findFACEItemsElementDefs(MappedStructure mappedStructure) throws MapperException
	{
		ElementDef msRoot = mappedStructure.getRootElement();
		if (msRoot == null) throw new MapperException("No root element in mapping set");
		if (!msRoot.getName().equals("GetIndicativeBudget")) 
			throw new MapperException("Root Element of mapping set must be called 'GetIndicativeBudget'");

		// there must be a chain of child ElementDefs with the names below; throw an exception if not
		ElementDef payload = findChildElementDef(msRoot,"payload");
		ElementDef items = findChildElementDef(payload,"Items");
				
		return items.getChildElements();		
	}
	
	/**
	 * 
	 * @param parent
	 * @param childName
	 * @return the child ElementDef with given name
	 * @throws MapperException if it does not exist
	 */
	private static ElementDef findChildElementDef(ElementDef parent, String childName) throws MapperException
	{
		ElementDef child = parent.getNamedChildElement(childName);
		if (child == null) throw new MapperException("Mapping set node '" + parent.getName() + "' has no child '" + childName + "'");
		return child;
	}
	


}
