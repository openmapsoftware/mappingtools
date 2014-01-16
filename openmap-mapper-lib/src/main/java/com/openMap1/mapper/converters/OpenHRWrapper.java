package com.openMap1.mapper.converters;

import java.util.StringTokenizer;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.TemplateFilter;

/**
 * Applies the following in-wrapper transform to OpenHR records, and the inverse
 * out-transform:
 * 
 * (1) under <adminDomain>, converts the <person> node for the patient to
 * <person_Patient> (there may be other <person> nodes, e.g. for the doctor;
 * this one is identified by having the same child <id> as the sibling <patient>
 * node) 
 * 
 * (2) converts <event> nodes to more specific nodes like <event_weight>
 * based on the value at a path from the <event>, e.g if code/@code has value
 * '22A..00', convert to <event_weight>
 * 
 * @author Robert
 * 
 */

public class OpenHRWrapper extends AbstractMapperWrapper implements
		MapperWrapper {

	private String patientInternalId;

	// tag name conversions for <event> elements, depending on their content
	private String[][] eventConversions = {
			{ "code/@code", "22A..00", "event_weight" },
			{ "code/@code", "229..00", "event_height" },
			{ "code/@code", "unknown", "event_finding" },
			{ "eventType", "MED", "event_medication" } };
	
	private String openHRNamespace="http://www.e-mis.com/emisopen";

	// ----------------------------------------------------------------------------------------
	// Constructor and initialisation from the Ecore model
	// ----------------------------------------------------------------------------------------

	public OpenHRWrapper(MappedStructure ms, Object spare)
			throws MapperException {
		super(ms, spare);
	}

	/**
	 * @return the file extension of the outer document, with initial '*.'
	 */
	public String fileExtension() {
		return ("*.xml");
	}

	/**
	 * @return the type of document transformed to and from; see static
	 *         constants in class AbstractMapperWrapper.
	 */
	public int transformType() {
		return AbstractMapperWrapper.XML_TYPE;
	}

	// ----------------------------------------------------------------------------------------
	// In-wrapper transform
	// ----------------------------------------------------------------------------------------

	@Override
	public Document transformIn(Object incoming) throws MapperException {
		if (!(incoming instanceof Element))
			throw new MapperException("Document root is not an Element");
		Element mappingRoot = (Element) incoming;

		// find the internal id used for the patient
		patientInternalId = getPathValue(mappingRoot, "adminDomain/patient/id");

		String mappingRootPath = "/openHealthRecord";
		inResultDoc = XMLUtil.makeOutDoc();

		Element inRoot = scanDocument(mappingRoot, mappingRootPath,
				AbstractMapperWrapper.IN_TRANSFORM);
		inResultDoc.appendChild(inRoot);
		return inResultDoc;
	}

	/**
	 * default behaviour is a shallow copy - copying the element name,
	 * attributes, and text content only if the element has no child elements.
	 * to be overridden for specific paths in implementing classes
	 */
	protected Element inTransformNode(Element el, String path)
			throws MapperException {
		// copy the element with namespaces, prefixed tag name, attributes but
		// no text or child Elements
		Element copy = (Element) inResultDoc.importNode(el, false);

		// make a single <person_Patient> element
		if (path.equals("/openHealthRecord/adminDomain/person")) {
			// System.out.println("Person has id '" + getPathValue(el,"id") +
			// "'");
			if (getPathValue(el, "id").equals(patientInternalId))
				copy = renameElement(el, "person_Patient", true);
		}

		// convert <event> elements to specific types of event
		if (path.equals("/openHealthRecord/healthDomain/event")) {
			String codeValue = getPathValue(el, "code/@code"); // usual case
			for (int i = 0; i < eventConversions.length; i++) {
				String[] conversion = eventConversions[i];
				if (!conversion[0].equals("code/@code"))
					codeValue = getPathValue(el, conversion[0]);
				if (codeValue.equals(conversion[1]))
					copy = renameElement(el, conversion[2], true);
			}
		}

		// if the source element has no child elements but has text, copy the
		// text
		String text = textOnly(el);
		if (!text.equals(""))
			copy.appendChild(inResultDoc.createTextNode(text));

		return copy;
	}

	// ----------------------------------------------------------------------------------------
	// Out-wrapper transform
	// ----------------------------------------------------------------------------------------

	@Override
	public Object transformOut(Element outgoing) throws MapperException {
		String mappingRootPath = "/openHealthRecord";
		outResultDoc = XMLUtil.makeOutDoc();

		Element outRoot = scanDocument(outgoing, mappingRootPath,
				AbstractMapperWrapper.OUT_TRANSFORM);
		outResultDoc.appendChild(outRoot);
		return outResultDoc;
	}

	/**
	 * default behaviour is a shallow copy - copying the element name,
	 * attributes, and text content only if the element has no child elements.
	 * to be overridden for specific paths in implementing classes
	 */
	protected Element outTransformNode(Element el, String path)
			throws MapperException {
		// copy the element with namespaces, prefixed tag name, attributes but
		// no text or child Elements
		Element copy = (Element) outResultDoc.importNode(el, false);

		// convert a <person_Patient> element back to <person>
		if (path.equals("/openHealthRecord/adminDomain/person_Patient")) {
			copy = renameElement(el, "person", false);
		}

		// convert specific types of <event_XX> back to plain <event>
		if ((steps(path) == 3)
				&& (path.startsWith("/openHealthRecord/healthDomain/event_"))) {
			copy = renameElement(el, "event", false);
		}

		// if the source element has no child elements but has text, copy the
		// text
		String text = textOnly(el);
		if (!text.equals(""))
			copy.appendChild(outResultDoc.createTextNode(text));

		return copy;
	}

	/**
	 * copy an element and all its attributes to the new document, renaming it
	 * and putting it in the default namespace with no prefix. Do not copy text
	 * content or child elements
	 * 
	 * @param el
	 * @param newName
	 * @param isIn
	 *            true for the in-transform, false for the out-transform
	 * @return
	 * @throws MapperException
	 */
	protected Element renameElement(Element el, String newName, boolean isIn)
			throws MapperException {
		String uri = ms().getNamespaceSet().getNamespaceURI("");
		Element newEl = null;
		if (isIn)
			newEl = inResultDoc.createElementNS(uri, newName);
		else if (!isIn)
			newEl = outResultDoc.createElementNS(uri, newName);

		// set all attributes of the constrained element, including namespace
		// attributes
		for (int a = 0; a < el.getAttributes().getLength(); a++) {
			Attr at = (Attr) el.getAttributes().item(a);
			newEl.setAttribute(at.getName(), at.getValue());
		}
		return newEl;
	}

	// ----------------------------------------------------------------------------------------
	//                              XSLT In-wrapper transform
	// ----------------------------------------------------------------------------------------

	

	/**
	 * @param xout XSLT output being made
	 * @param templateFilter a filter on the templates, implemented by XSLGeneratorImpl
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'in' direction.
	 * Templates must have mode = "inWrapper"
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		// add an identity in-wrapper template for most nodes
		super.addWrapperInTemplates(xout, templateFilter);
		
		// change name of the 'person' tag
		String oldName = "oms:person[oms:id=parent::node()/oms:patient/oms:id]";
		String newName = "oms:person_Patient";
		xout.topOut().appendChild(nameChangeTemplate(xout,"inWrapper",oldName,newName));
		
		// change the name of selected event tags
		for (int i = 0; i < eventConversions.length;i++)
		{
			String[] eventConversion = eventConversions[i];
			String path = addOMSPrefix(eventConversion[0]);
			oldName = "oms:event[" + path + "='" + eventConversion[1] + "']";
			newName = "oms:" + eventConversion[2];
			xout.topOut().appendChild(nameChangeTemplate(xout,"inWrapper",oldName,newName));
		}

	}
	
	/**
	 * add the prefix 'oms:' to element tag names on an XPath
	 * @param path
	 * @return
	 */
	private String addOMSPrefix(String path)
	{
		StringTokenizer st = new StringTokenizer(path,"/");
		String newPath = "";
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			// no prefix for attribute names
			if (step.startsWith("@")) newPath = newPath + step;
			// prefix for element names
			else newPath = newPath + "oms:" + step;
			if (st.hasMoreTokens()) newPath = newPath + "/";
		}
		return newPath;
	}
	

	// ----------------------------------------------------------------------------------------
	//                              XSLT Out-wrapper transform
	// ----------------------------------------------------------------------------------------

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
		// declare a prefixed namespace for the output OpenHR URI (the same URI is also the default namespace)
		xout.topOut().setAttribute("xmlns:ohr", openHRNamespace);
		
		// add an identity out-wrapper template for most nodes
		super.addWrapperOutTemplates(xout, templateFilter);		
				
		// change back the name of the 'person_Patient' tag
		String oldName = "ohr:person_Patient";
		String newName = "person";
		xout.topOut().appendChild(nameChangeTemplate(xout,"outWrapper",oldName,newName));
		
		// change back the names of selected event tags
		for (int i = 0; i < eventConversions.length;i++)
		{
			String[] eventConversion = eventConversions[i];
			oldName = "ohr:" + eventConversion[2];
			newName = "event";
			xout.topOut().appendChild(nameChangeTemplate(xout,"outWrapper",oldName,newName));
		}

	}

}
