package com.openMap1.mapper.converters;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.TemplateFilter;

public class NHS_V3_Converter extends NHS_CDA_Wrapper implements MapperWrapper{

	

	//----------------------------------------------------------------------------------------
	//                                        Constructor
	//----------------------------------------------------------------------------------------

	/**
	 * 
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare spare argument, just in case....
	 */
	public NHS_V3_Converter(MappedStructure ms, Object spare)  throws MapperException
	{
		super(ms,spare);
	}
	
	

	//----------------------------------------------------------------------------------------
	//                     In-Wrapper  Transform
	//----------------------------------------------------------------------------------------

	/**
	 * recursive descent, making the constrained CDA Element and its subtree from the 
	 * corresponding element in the CDA document
	 * @param newTagName tag name to be given to the element in the result of the in-transformation
	 * @param cdaElement Element in the CDA xml structure
	 * @return Constrained element corresponding to the CDA element
	 */
	protected Element constrainedElement(String newTagName,Element cdaElement,ElementDef elDef)
	throws MapperException
	{
		// move in the element with its new tag name and attributes
		Element constrainedEl = moveInElementOnly(newTagName,cdaElement);
		
		// add child Elements and recurse
		for (Iterator<Element> ie = XMLUtil.childElements(cdaElement).iterator();ie.hasNext();)
		{
			Element cdaChild = ie.next();
			String cdaName = XMLUtil.getLocalName(cdaChild);

			// for elements in the nhs namespace, add the prefix used in the full mapping set, to match that name
			String uri = cdaChild.getNamespaceURI();
			if ((uri != null) && (uri.equals(NHSURI))) cdaName = NHSPREFIX + ":" + cdaName;

			ElementDef childDef = bestMatchingChildNode(elDef,cdaName);	
			if (childDef != null)
			{
				// this tag name has the prefix 'npfitlc:' for element in the nhs namespace
				String nextTagName = childDef.getName();
				Element constrainedChild = null;

				// for <text> elements, save the child subtree in a table
				if (nextTagName.equals("text")) constrainedChild = saveInputTextSubtree(cdaChild);

				// for all other elements, including those in the nhs namespace, recurse. 
				else constrainedChild = constrainedElement(nextTagName,cdaChild,childDef);

				constrainedEl.appendChild(constrainedChild);														
			}			
		}					
		return constrainedEl;
	}
	

	/**
	 * find the child elementDef which best matches the node of the instance:
	 * - exact match if there is one
	 * - otherwise the node whose name starts with the tag name followed by '_', if there is one
	 * - otherwise null
	 * @param parent
	 * @param tagName
	 * @return
	 */
	protected ElementDef bestMatchingChildNode(ElementDef parent, String tagName) throws MapperException
	{
		ElementDef match = parent.getNamedChildElement(tagName);
		if (match == null)
		{
			int starters = 0;
			for (Iterator<ElementDef> it = parent.getChildElements().iterator();it.hasNext();)
			{
				ElementDef child = it.next();
				if (child.getName().startsWith(tagName + "_"))
				{
					starters++;
					match = child;
				}
			}
			if (starters > 1) throw new MapperException("Mapping set child nodes of '" + parent.getName() + "'give " + starters +
					" possible matches with instance tag name '" + tagName + "'");
		}
		return match;
	}
	

	//----------------------------------------------------------------------------------------
	//                     Out-Wrapper  Transform
	//----------------------------------------------------------------------------------------


	/**
	 * recursive descent, making the V3 Element and its subtree from the 
	 * corresponding element in the translation result
	 * @param tagName tag name to be given to the element in the result of the out-transformation
	 * @param resultElement Element in the translation output
	 * @return V3 element corresponding to the translation result element
	 */
	protected Element outWrappedV3Element(String tagName,Element resultElement, ElementDef topElementDef)
	throws MapperException
	{	
		String tag = outWrappedTagName(tagName);
		String uri = resultElement.getNamespaceURI();

		// create the element in the out-wrapped document
		Element v3El = outResultDoc.createElementNS(uri, tag);
		
		// copy across all attributes to the templated CDA element, including namespace attributes
		XMLUtil.copyAttributes(resultElement,v3El);
		
		// add the NHS namespace declaration to the top element
		if (tagName.equals(topClassName))
			v3El.setAttribute(("xmlns:" + NHSPREFIX),NHSURI);
		
		// if there are no child elements, copy any text content
		if (XMLUtil.childElements(resultElement).size() == 0)
			XMLUtil.copyText(resultElement, v3El, outResultDoc);
		
		// add child Elements and recurse
		for (Iterator<Element> ie = XMLUtil.childElements(resultElement).iterator();ie.hasNext();)
		{
			Element constChild = ie.next();
			String nextTagName = XMLUtil.getLocalName(constChild);
			
			Element v3Child = null;
			// for text elements, recover the subtree from a hashtable stored by the input wrapper
			if (nextTagName.equals("text")) v3Child = recoverInputTextSubtree(constChild, "unknown V3 path");
			// otherwise recurse (not using the ElementDef)
			else v3Child = outWrappedV3Element(nextTagName,constChild,topElementDef);	

			v3El.appendChild(v3Child);										
		}					
		return v3El;
	}
	
	/**
	 * some rather ad hoc changes to get the our-wrapped tag name from the in-wrapped tag name
	 * @param tagName
	 * @return
	 */
	private String outWrappedTagName(String tagName)
	{
		String result = tagName;
		// to detect tag names with a template id before a full stop (roles and acts)
		StringTokenizer points = new StringTokenizer(tagName,"."); 
		
		// participations and ActRelationships; keep anything before the first '_'
		if (points.countTokens() == 1) 
		{
			StringTokenizer underscores = new StringTokenizer(tagName,"_");  
			// take the whole name, if there is no '_', or anything before the first '_'
			result = underscores.nextToken();
		}
		// acts and roles
		else if (points.countTokens() == 2)
		{
			String beforePoint = points.nextToken();
			String afterPoint = points.nextToken();
			StringTokenizer underscores = new StringTokenizer(afterPoint,"_"); 
			// if there are some '_' after the '.', drop the last '_' and what follows it
			if (underscores.countTokens() > 1)
			{
				String lastPiece= "";
				while (underscores.hasMoreTokens()) lastPiece = underscores.nextToken(); 
				afterPoint = afterPoint.substring(0, afterPoint.length() - lastPiece.length() -1);
			}
			result = beforePoint + "." + afterPoint;
		}
		return result;
	}
	
	
	//----------------------------------------------------------------------------------------
	//      XSLT versions of wrapper transforms, for inclusion in full XSLT transforms
	//----------------------------------------------------------------------------------------
	
	protected String wrapperXSLFileName(boolean isIn)
	{
		return "not used";
	}
	

	/**
	 * @param xout XSLT output being made
	 * @param templateFilter a filter on the templates, implemented by XSLGeneratorImpl
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'in' direction.
	 * Templates must have mode = "inWrapper"
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		// declare namespaces and add templates to pass through text subtrees unchanged; no file of fixed templates
		super.addWrapperInTemplates(xout, templateFilter,false);
		
		// add a general template to copy nodes with no name change
		xout.topOut().appendChild(identityPathTemplate(xout, "inWrapper"));
		
		ElementDef root = ms().getRootElement();
		Hashtable<String,Element> templateMatches = new Hashtable<String,Element>();
		Hashtable<String,String> nodeNames = nodeNamesToChange(true);
		addTagNameChangeTemplates(xout,root,templateMatches,true,"",nodeNames);

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
		// declare namespaces and add templates to pass through text subtrees unchanged; no file of fixed templates
		super.addWrapperOutTemplates(xout, templateFilter,false);		
		
		// add a general template to copy nodes with no name change, and do not generate any specific ones to do so
		xout.topOut().appendChild(identityPathTemplate(xout, "outWrapper"));

		ElementDef root = ms().getRootElement();
		Hashtable<String,Element> templateMatches = new Hashtable<String,Element>();
		Hashtable<String,String> nodeNames = nodeNamesToChange(false);
		addTagNameChangeTemplates(xout,root,templateMatches,false,"",nodeNames);
	}
	
	/**
	 * collect the names of all nodes whose names need to be changed for any path, in the in-wrapper or the out-wrapper transform
	 * @param isIn
	 * @return
	 */
	private Hashtable<String,String> nodeNamesToChange(boolean isIn)
	{
		Hashtable<String,String> nodeNames = new Hashtable<String,String>();
		ElementDef root = ms().getRootElement();
		addNodeNamesToChange(root,isIn,nodeNames);
		return nodeNames;
	}
	
	/**
	 * recursive descent of full mapping set, finding all node names which need to change
	 * @param elDef
	 * @param isIn
	 * @param nodeNames
	 */
	private void addNodeNamesToChange(ElementDef elDef, boolean isIn, Hashtable<String,String> nodeNames)
	{
		String inTagName = elDef.getName();
		String outTagName = outWrappedTagName(inTagName);
		
		if (!inTagName.equals(outTagName))
		{
			if (isIn) nodeNames.put(outTagName, "1");
			else if (!isIn) nodeNames.put(inTagName, "1");
		}
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef childDef = it.next();
			addNodeNamesToChange(childDef, isIn, nodeNames);
		}		
	}

	
	/**
	 * 
	 * @param xout
	 * @param elDef
	 * @param templateMatches
	 * @param isIn
	 * @param path
	 * @param nodeNames
	 * @throws MapperException
	 */
	private void addTagNameChangeTemplates(XSLOutputFile xout,ElementDef elDef, Hashtable<String,Element> templateMatches, 
			boolean isIn, String path, Hashtable<String,String> nodeNames) throws MapperException
	{
		String inTagName = elDef.getName();
		String prefixedInTagName = V3PREFIX + ":" + inTagName;

		String outTagName = outWrappedTagName(inTagName);
		String prefixedOutTagName = V3PREFIX + ":" + outTagName;

		String priority="3";

		String addToPath = "";
		if (isIn) addToPath = outTagName; 
		else addToPath = inTagName;
		String newPath = path + "/" + addToPath;
		
		/* if this node name needs to be changed for any path, add a template or a when-condition for this path 
		 * (even though the node name might not change for this path) */
		if (nodeNames.get(addToPath) != null)
		{
			if (isIn) addPathTemplate(xout,"inWrapper",prefixedOutTagName,"",path,addToPath,prefixedInTagName,priority, templateMatches);
			else if (!isIn) addPathTemplate(xout,"outWrapper",prefixedInTagName,"",path,addToPath,prefixedOutTagName,priority, templateMatches);
		}
		
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef childDef = it.next();
			addTagNameChangeTemplates(xout,childDef,templateMatches,isIn,newPath,nodeNames);
		}
	}

	
}

