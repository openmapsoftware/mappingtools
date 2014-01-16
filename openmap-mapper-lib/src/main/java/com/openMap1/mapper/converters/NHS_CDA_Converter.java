package com.openMap1.mapper.converters;

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

public class NHS_CDA_Converter extends NHS_CDA_Wrapper implements MapperWrapper{

	

	//----------------------------------------------------------------------------------------
	//                                        Constructor
	//----------------------------------------------------------------------------------------

	/**
	 * 
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare spare argument, just in case....
	 */
	public NHS_CDA_Converter(MappedStructure ms, Object spare)  throws MapperException
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
	protected Element constrainedElement(String newTagName,Element cdaElement,ElementDef topElementDef)
	throws MapperException
	{
		Element constrainedEl = moveInElementOnly(newTagName,cdaElement);
		
		// add child Elements and recurse
		for (Iterator<Element> ie = XMLUtil.childElements(cdaElement).iterator();ie.hasNext();)
		{
			Element cdaChild = ie.next();
			// by taking the local name, you change tag names 'messageType' and 'contentId' the V3 namespace
			String cdaName = XMLUtil.getLocalName(cdaChild);
			String nextTagName = cdaName;

			/* conversion of the tag name is needed if the current child element in the CDA has a child element
			 * contentId whose extension attribute defines a template */
			String templateAtt = "";
			Element contentChild = XMLUtil.firstNamedChild(cdaChild, "contentId");
			if (contentChild != null) templateAtt = contentChild.getAttribute("extension");
			
			/* typical form of templateAtt is 'COCD_TP145001UK03#AssignedAuthorSDS' (using a class name) */
			if (!templateAtt.equals(""))
			{
				StringTokenizer st = new StringTokenizer(templateAtt,"#");
				if (st.countTokens() != 2) throw new MapperException("In-wrapper transform: ContentId extension attribute '" + templateAtt + "' has not internal '#'");
				st.nextToken();
				String className = st.nextToken();
				// this is the way I split tag names like 'entry' according to which template they lead to
				nextTagName = cdaName + "_" + className;
			}
			
			Element constrainedChild = null;
			// for <text> elements, save the child subtree in a table
			if (nextTagName.equals("text")) constrainedChild = saveInputTextSubtree(cdaChild);
			// for all other elements, recurse. The ElementDef is not used in this recursion
			else constrainedChild = constrainedElement(nextTagName,cdaChild,topElementDef);

			constrainedEl.appendChild(constrainedChild);										
		}					
		return constrainedEl;
	}
	


	

	//----------------------------------------------------------------------------------------
	//                     Out-Wrapper  Transform
	//----------------------------------------------------------------------------------------


	/**
	 * recursive descent, making the CDA Element and its subtree from the 
	 * corresponding element in the translation result
	 * @param newTagName tag name to be given to the element in the result of the out-transformation
	 * @param resultElement Element in the translation output
	 * @return templated CDA element corresponding to the translation result element
	 */
	protected Element outWrappedV3Element(String newTagName,Element resultElement, ElementDef topElementDef)
	throws MapperException
	{	
		String tag = newTagName;
		String uri = resultElement.getNamespaceURI();

		/* put elements 'messageType' and contentId in the NHS namespace 
		 * (there are usually no contentId elements in the in-wrapped instance; they are made below) */
		if ((tag.equals("messageType")) || (tag.equals("contentId")))
		{
			tag = NHSPREFIX + ":" + tag;
			uri = NHSURI;
		}
		
		/* if this element has a 'templateId' grandchild element, 
		 * use its 'extension' attribute to shorten the name of this element - 
		 * e.g converting 'entry_Finding' to 'entry' */ 
		String ext = instanceGrandChildTemplateIdExtension(resultElement,true);
		if ((ext != null) && (renamableTag(resultElement)))
		{
			StringTokenizer st = new StringTokenizer(ext,"#");
			if (st.countTokens() != 2) throw new MapperException("Out-wrapper transform: templateId extension attribute '" + ext + "' has no internal '#'");
			st.nextToken();
			String className = st.nextToken();
			// this is the way I split tag names like 'entry' according to which template they lead to
			if (tag.endsWith("_" + className))
			{
				int newLength = tag.length() - className.length() - 1;
				tag = tag.substring(0,newLength);					
			}
			else  // this can happen -  some renamable tags do not get renamed, because they have no contentId child; but record it anyway
			{
				System.out.println("Tag name '" + tag + "' does not end with '" + className + "'");
			}
		}

		// create the element in the out-wrapped document
		Element cdaEl = outResultDoc.createElementNS(uri, tag);
		
		// give it a contentId child if needed
		giveContentIdChild(ext,cdaEl,resultElement);
				
		// copy across all attributes to the templated CDA element, including namespace attributes
		XMLUtil.copyAttributes(resultElement,cdaEl);
		
		// add the NHS namespace declaration to the top element
		if (newTagName.equals(topClassName))
			cdaEl.setAttribute(("xmlns:" + NHSPREFIX),NHSURI);
		
		// if there are no child elements, copy any text content
		if (XMLUtil.childElements(resultElement).size() == 0)
			XMLUtil.copyText(resultElement, cdaEl, outResultDoc);
		
		// add child Elements and recurse
		for (Iterator<Element> ie = XMLUtil.childElements(resultElement).iterator();ie.hasNext();)
		{
			Element constChild = ie.next();
			String nextTagName = XMLUtil.getLocalName(constChild);
			
			Element cdaChild = null;
			// for text elements, recover the subtree from a hashtable stored by the input wrapper
			if (nextTagName.equals("text")) cdaChild = recoverInputTextSubtree(constChild, "unknown CDA path");
			// otherwise recurse (not using the ElementDef)
			else cdaChild = outWrappedV3Element(nextTagName,constChild,topElementDef);	

			cdaEl.appendChild(cdaChild);										
		}					
		return cdaEl;
	}
	
	
	//----------------------------------------------------------------------------------------
	//      XSLT versions of wrapper transforms, for inclusion in full XSLT transforms
	//----------------------------------------------------------------------------------------
	
	protected String wrapperXSLFileName(boolean isIn)
	{
		if (isIn) return "wrapperXSLBase/NHSTemplatedInWrapper.xsl";
		return "wrapperXSLBase/NHSTemplatedOutWrapper.xsl";
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
		super.addWrapperInTemplates(xout, templateFilter,true);
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
		super.addWrapperOutTemplates(xout, templateFilter,true);		
	}
	
}

