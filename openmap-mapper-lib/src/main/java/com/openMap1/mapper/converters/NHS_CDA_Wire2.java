package com.openMap1.mapper.converters;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ValueCondition;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.TemplateFilter;

/**
 * Wrapper class for NHS CDA wire format
 * New simpler version started 17/8/12, which does not need to add and remove npfitlc:contentId elements
 * @author Robert
 *
 */


public class NHS_CDA_Wire2  extends NHS_CDA_Wrapper implements MapperWrapper{
	

	//----------------------------------------------------------------------------------------
	//                                        Constructor
	//----------------------------------------------------------------------------------------

	/**
	 * 
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare spare argument, just in case....
	 */
	public NHS_CDA_Wire2(MappedStructure ms, Object spare)  throws MapperException
	{
		super(ms,spare);
	}
	
	

	//----------------------------------------------------------------------------------------
	//                               In-Wrapper  Transform
	//----------------------------------------------------------------------------------------

	/**
	 * recursion down the CDA tree, applying the in-wrapper name change to each element
	 * @param newTagName new tag name to be applied to this element
	 * @param cdaElement current element in the cda tree
	 * @param elDef current element definition in the full mapping set
	 * @return the element to include in the in-wrapped XML
	 */
	protected Element constrainedElement(String newTagName, Element cdaElement,ElementDef elDef) throws MapperException
	{
		String cdaName = XMLUtil.getLocalName(cdaElement);
		if (!cdaName.equals(newTagName)) trace("constrained Element " + newTagName + " for " + cdaName);
		// move across the element, with the tag name it has been given, and its attributes
		Element constrainedEl = moveInElementOnly(newTagName,cdaElement);
		
		// add child Elements and recurse
		for (Iterator<Element> ie = XMLUtil.childElements(cdaElement).iterator();ie.hasNext();)
		{
			Element cdaChild = ie.next();
			String childName = XMLUtil.getLocalName(cdaChild);
			trace("trying child " + childName);
			// find the child node in the mapping set which has this name as a node name, or has the cda name in its description
			ElementDef childElDef = getChildDef(elDef,cdaChild);
			
			// you may not find it, because the subtree has been pruned out of the 'full' mapping set
			if (childElDef != null)
			{
				trace("found");
				Element constrainedChild = null;
				// for <text> elements, save the child subtree in a table
				if (childElDef.getName().equals("text")) constrainedChild = saveInputTextSubtree(cdaChild);
				// for all other elements, pass the correct child name down the recursion
				else constrainedChild = constrainedElement(childElDef.getName(),cdaChild,childElDef);

				constrainedEl.appendChild(constrainedChild);				
			}

		}
		return constrainedEl;
	}
	
	/**
	 * @param elDef
	 * @param cdaName
	 * @return a child ElementDef which either has the same node name as the CDA element,
	 * or, if there are none with that name, has the CDA element name defined in its Description,
	 *  and which has a matching template id on a grandchild <templateId> node; 
	 *  or null if none are found.
	 * @throws MapperException if there is more than one is found
	 */
	private ElementDef getChildDef(ElementDef elDef,Element cdaEl) throws MapperException
	{
		String cdaName = cdaEl.getLocalName();
		String foundNames = "";
		String templateIds = "";
		Vector<ElementDef> firstPass = new Vector<ElementDef>();
		Vector<ElementDef> secondPass = new Vector<ElementDef>();
		Vector<ElementDef> thirdPass = new Vector<ElementDef>();

		// first pass - filter on CDA tag name alone. If there is a unique child which matches, take it
		boolean uniqueCandidate = false;
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef candidate = it.next();
			if (cdaTagName(candidate).equals(cdaName)) 
			{
				firstPass.add(candidate);
				uniqueCandidate = ((candidate.getMaxMultiplicity() == MaxMult.ONE) 
						&& (candidate.getMinMultiplicity() == MinMult.ONE));
			}
		}
		if ((firstPass.size() == 1) && uniqueCandidate) return firstPass.get(0);

		// second pass - filter on template id, if it exists
		for (Iterator<ElementDef> it = firstPass.iterator(); it.hasNext();)
		{
			ElementDef candidate = it.next();
			//candidates must match the mapping set on their grandchild template id, if the mapping set defines one or the instance has one
			String mappingTemplateId = mappingGrandChildTemplateIdExtension(candidate);
			String instanceTemplateId = instanceGrandChildTemplateIdExtension(cdaEl,false);
			trace("Node " + candidate.getName() + " has grandchild template ids '" + mappingTemplateId + "' and '" + instanceTemplateId + "'");
			// if there are no template ids, both the mapping method and the instance method return template id = ""

			if (mappingTemplateId.equals(instanceTemplateId)) 
			{
				secondPass.add(candidate);
				templateIds = templateIds + "'" + mappingTemplateId + "' ";
			}
		}
		if (secondPass.size() == 1) return secondPass.get(0);
		
		// third pass - filter on typecode
		for (Iterator<ElementDef> it = secondPass.iterator(); it.hasNext();)
		{
			ElementDef candidate = it.next();
			String typeCodeInInstance = cdaEl.getAttribute("typeCode");
			String typeCodeInMappingSet = fixedValueInMappingSet("@typeCode",candidate);
			if (typeCodeInInstance.equals(typeCodeInMappingSet))
			{
				thirdPass.add(candidate);
				foundNames = foundNames + candidate.getName() + ", ";
			}
		}
		if (thirdPass.size() == 1) return thirdPass.get(0);
		if (thirdPass.size() == 0) return null;

		// found = 0 is allowed (node removed from full mapping set), but found > 1 is not (ambiguity)
		throw new MapperException("Found " + thirdPass.size() + " child nodes of element '" 
				+ elDef.getName() + "' with names " + foundNames + " defined to have CDA tag name '" + cdaName + "' with template ids " + templateIds);

	}
	
	private String fixedValueInMappingSet(String path, ElementDef elDef)
	{
		String fixedValue = "";
		NodeMappingSet nms = elDef.getNodeMappingSet();
		if (nms != null) for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator();it.hasNext();)
		{
			ObjMapping objMapping = it.next();
			for (Iterator<MappingCondition> iu = objMapping.getMappingConditions().iterator();iu.hasNext();)
			{
				MappingCondition mc = iu.next();
				if (mc instanceof ValueCondition)
				{
					ValueCondition vc = (ValueCondition)mc;
					if (vc.getLeftPath().equals(path))  fixedValue = vc.getRightValue();
				}
			}
		}
		return fixedValue;		
	}
	
	/**
	 * 
	 * @param elDef
	 * @return the cda tag name for a result element; either the name of the 
	 * result element itself (with namespace prefix removed), or, if different, got from the description
	 */
	private String cdaTagName(ElementDef elDef)
	{
		// remove any namespace prefix from elementDef name, to compare local names
		String cdaName = getLocalName(elDef);

		// Description fields are 'CDA name:<name>' for nodes whose names need changing
		String prefix = "CDA name:";
		String description = elDef.getDescription();
		if ((description != null) && (description.startsWith(prefix))) 
			cdaName = description.substring(prefix.length());

		return cdaName;
	}
	
	/**
	 * 
	 * @param elDef
	 * @return name of the ElementDef, with any namespace prefix removed
	 */
	private String getLocalName(ElementDef elDef)
	{
		String cdaName = null;
		StringTokenizer st = new StringTokenizer(elDef.getName(),":");
		while (st.hasMoreTokens()) cdaName = st.nextToken();
		return cdaName;
	}



	//----------------------------------------------------------------------------------------
	//                                  Out-Wrapper  Transform
	//----------------------------------------------------------------------------------------

	/**
	 * recursion down the translation result tree, applying the out-wrapper name change to each element
	 * @param cdaTagName new tag name to be applied to this element
	 * @param resultElement current element in the translation result tree
	 * @param elDef current element definition in the full mapping set
	 * @return the element to include in the out-wrapped XML (wire format CDA)
	 */
	protected Element outWrappedV3Element(String cdaTagName, Element resultElement, ElementDef elDef) throws MapperException
	{
		String tag = cdaTagName;
		String uri = resultElement.getNamespaceURI();

		// create the element in the out-wrapped document
		Element cdaEl = outResultDoc.createElementNS(uri, tag);
		
		// copy across all attributes to the templated CDA element, including namespace attributes
		XMLUtil.copyAttributes(resultElement,cdaEl);
		
		// if there are no child elements, copy any text content
		if (XMLUtil.childElements(resultElement).size() == 0) XMLUtil.copyText(resultElement, cdaEl, outResultDoc);
 
		// add child Elements and recurse
		for (Iterator<Element> ie = XMLUtil.childElements(resultElement).iterator();ie.hasNext();)
		{
			Element resultChild = ie.next();
			String resultName = XMLUtil.getLocalName(resultChild);
			
			// find the child node in the mapping set with the same local name
			ElementDef childElDef = getChildWithLocalName(elDef,resultName);
			if (childElDef == null)
				throw new MapperException("Cannot find child element definition for result node '" + resultName + "'");

			Element cdaChild = null;
			// for text elements, recover the subtree from a hashtable stored by the input wrapper
			if (resultName.equals("text")) cdaChild = recoverInputTextSubtree(resultChild, "unknown CDA path");
			// otherwise pass the correct cda name for the child down the recursion
			else cdaChild = outWrappedV3Element(cdaTagName(childElDef),resultChild,childElDef);	
			cdaEl.appendChild(cdaChild);
		}
		return cdaEl;
	}
	
	ElementDef getChildWithLocalName(ElementDef elDef, String localName)
	{
		ElementDef child = null;
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef next = it.next();
			if (getLocalName(next).equals(localName)) child = next;
		}
		return child;
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
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		// 'false' means do not read hand-written templates from a file
		super.addWrapperInTemplates(xout, templateFilter,false);
		addGeneratedCDAWireTemplates(xout, true); // 'true' means in-wrapper
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
		// 'false' means do not read hand-written templates from a file
		super.addWrapperOutTemplates(xout, templateFilter,false);		
		
		addGeneratedCDAWireTemplates(xout,false); // 'false' means out-wrapper
	}
	
	

	
	/**
	 * add path-sensitive templates for the CDA wire form in-wrapper or out-wrapper transform
	 * @param xout
	 * @throws MapperException
	 */
	private void addGeneratedCDAWireTemplates(XSLOutputFile xout, boolean isInWrapper) throws MapperException
	{
		ElementDef root = ms().getRootElement();
		
		// add a general template to copy nodes with no name change, and do not generate any specific ones to do so
		xout.topOut().appendChild(identityPathTemplate(xout, mode(isInWrapper)));
		
		// note which tag names may need to be changed, at some path where they occur
		cdaNamesToChange = new Hashtable<String,String>();
		fullMappingSetNamesToChange = new Hashtable<String,String>();
		addTagNamesToChange(root);

		// String = match condition of template; Element = template root element
		Hashtable<String,Element> templateMatches = new Hashtable<String,Element>();
		
		boolean addNamePreservingTemplates = false;	
		addWireTemplates(xout,root,null,isInWrapper,
				templateMatches,addNamePreservingTemplates); // null = parent tag name
	}
	
	
	/*for the in-wrapper transform, which cda tag names need to be changed at some paths 
	 * (maybe not all their paths) 
	 * String = CDA tag name; String = "1" */
	private Hashtable<String,String> cdaNamesToChange;
	
	/*for the out-wrapper transform, which full mapping set tag names need to be changed at some paths 
	 * (maybe not all their paths)
	 * String = full mapping set node name; String = "1"  */
	private Hashtable<String,String> fullMappingSetNamesToChange;
	
	/**
	 * recursive descent of the full mapping set tree, noting which tag names (both cda tag names and 
	 * full mapping set tag names) need to be changed for some path at which they occur
	 * @param root
	 */
	private void addTagNamesToChange(ElementDef elDef)
	{
		String cdaName = cdaTagName(elDef);
		if (!elDef.getName().equals(cdaName))
		{
			cdaNamesToChange.put(cdaName, "1");
			fullMappingSetNamesToChange.put(elDef.getName(), "1");
		}

		for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
			addTagNamesToChange(it.next());
	}
	
	/**
	 * 
	 * @param mappingSetName
	 * @param cdaName
	 * @param isInWrapper
	 * @return true if this tag name may need a name change for some paths in the wrapper transform
	 */
	private boolean mayNeedNameChange(String mappingSetName, String cdaName, boolean isInWrapper)
	{
		boolean mayChange = false;
		if (isInWrapper) mayChange = (cdaNamesToChange.get(cdaName) != null);
		else mayChange = (fullMappingSetNamesToChange.get(mappingSetName) != null);
		return mayChange;
	}

	
	/**
	 * @param isInWrapper
	 * @return the appropriate mode for in and out wrapper templates
	 */
	protected String mode(boolean isInWrapper)
	{
		String mode = "outWrapper";
		if (isInWrapper) mode="inWrapper";
		return mode;
	}
	
	/**
	 * recursive descent of the full mapping set, adding templates to change tag names where needed.
	 * Tag names which need a name change for some path need to have a 'name change' template for
	 * every path, even if it does not change the name for some paths.
	 * Otherwise, the name change template would always have higher priority than the default no-change 
	 * template, but for some paths would do nothing.
	 * @param xout
	 * @param elDef
	 * @param cdaParentName
	 * @param isInWrapper
	 * @param templateMatches
	 * @param parentSwitchedOutMode
	 * @throws MapperException
	 */
	private void addWireTemplates(XSLOutputFile xout, ElementDef elDef, String cdaParentName,
			boolean isInWrapper,
			Hashtable<String,Element> templateMatches,
			boolean addNamePreservingTemplates) throws MapperException
	{
		String cdaName = cdaTagName(elDef);
		String elName = elDef.getName();
		
		// add templates which don't change tag names only if required to do so
		boolean addTemplates = ((addNamePreservingTemplates)||(mayNeedNameChange(elName,cdaName,isInWrapper)));
		
		/* add a name change template for those elements in the v3 namespace that need it for any of their paths, 
		 * and (if required) for those which never have a name change for any path
		 * (where the cda name is always the same as the ElementDef name);
		 * because of the namespace condition, there is no in or out wrapper template for messageType*/
		if ((addTemplates) && (!GenUtil.inArray(cdaName, IN_NHS_NAMESPACE)))
		{
			if (isInWrapper) addWireInTemplate(xout,elDef,cdaName,cdaParentName,templateMatches);
			else addWireOutTemplate(xout,elDef,cdaName,templateMatches);			
		}
		
		// recurse down the mapping set, to do the same for descendants
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef childDef = it.next();
			addWireTemplates(xout,childDef,cdaName,isInWrapper,
					templateMatches,addNamePreservingTemplates);
		}
	}
	


	/**
	 * 
	 * @param xout
	 * @param elDef
	 * @param cdaName
	 * @param cdaParentName
	 * @param templateMatches
	 * @throws MapperException
	 */
	private void addWireInTemplate(XSLOutputFile xout,ElementDef elDef,
			String cdaName,String cdaParentName, Hashtable<String,Element> templateMatches)  throws MapperException
	{
		String tagName = elDef.getName(); // tag name to add to the result
		boolean highPriority = false; // priority 2 if false, 3 if true
		
		String parentMatchCondition = "";
		if (cdaParentName != null) parentMatchCondition ="[parent::" + V3PREFIX + ":" + cdaParentName + "]";
		
		// if the node itself has a templateId, use that in the matching conditions
		String templateMatchCondition = "";
		String nodeTemplateId = getTemplateId(elDef);
		int found = 0;
		if (nodeTemplateId != null)
		{
			templateMatchCondition = "[" + V3PREFIX + ":templateId/@extension='" + nodeTemplateId + "']";
			highPriority = true;
			found++;
		}

		// look for templateId beneath any 1..1 child nodes to use in match conditions
		String childTemplateMatchCondition = "";
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef child = it.next();
			if ((child.getMinMultiplicity() == MinMult.ONE) && (child.getMaxMultiplicity() == MaxMult.ONE))
			{
				String childTemplateId = getTemplateId(child);
				if (childTemplateId != null)
				{
					childTemplateMatchCondition =  "[" + V3PREFIX + ":" + cdaTagName(child) + "/" 
					+ V3PREFIX + ":templateId/@extension='" + childTemplateId + "']"; 						
					highPriority = true;
					found++;
				}
			}
		}

		if (found == 0) System.out.println("Node at path '" + elDef.getPath() 
				+  "' has no <templateId> node or 1..1 child elements with <templateId> nodes.");
		
		//find any typeCode condition for the match
		String typeCodeMatchCondition = "";
		String typeCode = fixedValueInMappingSet("@typeCode", elDef);
		if (!typeCode.equals("")) 
		{
			typeCodeMatchCondition =  "[@typeCode='" + typeCode + "']"; 
		}
		
		// collect together whatever match conditions have been found; logical 'and' of all []
		String matchCondition = parentMatchCondition + templateMatchCondition + childTemplateMatchCondition + typeCodeMatchCondition;
		
		// find the path to the parent element
		String parentPath = "";
		if (elDef.eContainer() instanceof ElementDef) parentPath = ((ElementDef)elDef.eContainer()).getPath();
		
		// give higher priority to templates whose match condition includes a template id
		String priority = "2";
		if (highPriority) priority = "3";
		
		// add the path template, to match on the CDA name and add an element with the in-wrapped tag name
		addPathTemplate(xout, "inWrapper",  cdaName, matchCondition, parentPath, tagName, tagName, priority, templateMatches);
	}
	
	
	/**
	 * 
	 * @param elDef
	 * @return the templateId directly beneath an ElementDef of the full mapping set, if it has one; null otherwise
	 */
	private String getTemplateId(ElementDef elDef)
	{
		String templateId = null;
		ElementDef template = elDef.getNamedChildElement("templateId");
		if (template != null)
		{
			AttributeDef extension = template.getNamedAttribute("extension");
			if (extension != null)
			{
				String desc = extension.getDescription();
				if (desc.startsWith("fixed:")) templateId = desc.substring("fixed:".length());
			}
		}
		return templateId;
	}


	/**
	 * 
	 * @param xout
	 * @param elDef
	 * @param cdaName
	 * @param templateMatches
	 * 
	 * @throws MapperException
	 */
	private void addWireOutTemplate(XSLOutputFile xout,ElementDef elDef,
			String cdaName,Hashtable<String,Element> templateMatches)  throws MapperException
	{
		String tagName = elDef.getName(); // tag name to match on, and add to the path
		
		// no conditions to match on
		String condition = ""; 
		
		// find the path to the parent element
		String parentPath = "";
		if (elDef.eContainer() instanceof ElementDef) parentPath = ((ElementDef)elDef.eContainer()).getPath();
		
		// add the path template, to add an element with the CDA name
		addPathTemplate(xout, "outWrapper", tagName, condition, parentPath, tagName, cdaName, "2", templateMatches);		
	}
	
	/**
	
	/**
	 * names of template files for hand-coded parts of the wire wrapper in and out transforms; not used
	 */
	protected String wrapperXSLFileName(boolean isInWrapper)
	{
		return "not used";
	}


	private void message(String s) {System.out.println(s);}


}
