package com.openMap1.mapper.converters;

import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.XMLUtil;


/**
 * 
 * @author Robert
 * The only functions of this wrapper class are:
 * 
 * As an input wrapper, to extract subtrees below <text> nodes
 * and index them by string keys in the Hashtable keptSubtrees.
 * 
 * As an output wrapper, to use the string content of <text> nodes
 * as keys into a Hashtable (set up by the input wrapper class) to 
 * recover the subtrees below the <text> nodes
 */
public class NHS_Simplified_CDA extends AbstractMapperWrapper implements MapperWrapper{

	
	/**
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare used to provide the input reader, 
	 */
	public NHS_Simplified_CDA(MappedStructure ms, Object spare)  throws MapperException
	{
		super(ms,spare);
	}

	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	public int transformType() {return AbstractMapperWrapper.XML_TYPE;}
	
	/**
	 * 
	 * @return the file extension of the outer document, with initial '*.'
	 */
	public String fileExtension() {return "*.xml";}

	
	//--------------------------------------------------------------------------------------------
	//                     Transform methods in the MapperWrapper Interface
	//--------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param incoming; must be of class Element or InputStream
	 * @return the result of the in wrapper transform
	 */
	public Document transformIn(Object incoming) throws MapperException
	{
		// initialise the table of xml subtrees to pass to the output, and the key for that table
		keptSubtrees = new Hashtable<String,Element>();
		keyIndex = 0;
		
		if (!(incoming instanceof Element)) throw new MapperException("Document root is not an Element");
		Element mappingRoot = (Element)incoming;

		String mappingRootPath = "/ClinicalDocument";
		inResultDoc = XMLUtil.makeOutDoc();
				
		// see override of scanDocument below
		Element inRoot = scanDocument(mappingRoot, mappingRootPath, AbstractMapperWrapper.IN_TRANSFORM);
		inResultDoc.appendChild(inRoot);
		return inResultDoc;		
	}
	

	
	/**
	 * @param outgoing the root element produced by the XMLWriter when 
	 * writing out from a class model instance (seen through an objectGetter)
	 * @return the result of the out wrapper transform; 
	 * must be of class Document or OutputStream
	 */
	public Object transformOut(Element outgoing) throws MapperException
	{
		String mappingRootPath = "/ClinicalDocument";
		outResultDoc = XMLUtil.makeOutDoc();

		// see override of scanDocument below
		Element outRoot = scanDocument(outgoing, mappingRootPath, AbstractMapperWrapper.OUT_TRANSFORM);
		outResultDoc.appendChild(outRoot);
		return outResultDoc;
		
	}

	/**
	 * recursive scan of document,
	 * making changes only at <text> nodes 
	 */
	protected Element scanDocument(Element el, String path, int scanType)  throws MapperException
	{
		String tagName = XMLUtil.getLocalName(el);   // el.getLocalName() gives null here in the out transform

		/* <text> node in input; retain the subtree in a Hashtable, and do not pass the subtree to 
		 * the in-wrapped document. Pass only the text key. */
		if ((tagName != null) && (tagName.equals("text")) && (scanType == AbstractMapperWrapper.IN_TRANSFORM))
		{
			return saveInputTextSubtree(el);
		}
		
		/* <text> node in output; look up the subtree in the input Hashtable, and pass it to 
		 * the out-wrapped document */
		else if ((tagName != null) && (tagName.equals("text")) && (scanType == AbstractMapperWrapper.OUT_TRANSFORM))
		{
			return recoverInputTextSubtree(el,path);
		}
		
		// all other nodes; carry on recursion which makes no change
		else return super.scanDocument(el, path, scanType);
	}


}
