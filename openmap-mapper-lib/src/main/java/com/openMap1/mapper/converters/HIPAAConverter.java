package com.openMap1.mapper.converters;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MinMult;


/**
 * Wrapper class to convert HIPAA X12 messages into an XML form 
 * convenient for mapping.
 * 
 * @author robert
 *
 */
public class HIPAAConverter extends AbstractMapperWrapper implements MapperWrapper{
	
	private StructureDefinition X12Structure;
	
	private ElementDef X12Root;
	
	private Element X12XMLRoot;
	
	private String rootName;
	
	protected boolean tracing() {return true;}
	
	private boolean writeLineContent = true;
	
	//----------------------------------------------------------------------------------------------------
	//                                        constructor
	//----------------------------------------------------------------------------------------------------
	
	public HIPAAConverter(MappedStructure mappedStructure, Object rootNameObj) throws MapperException
	{
		super(mappedStructure, rootNameObj);

		if (!(rootNameObj instanceof String))
			throw new MapperException("Second argument of X12Converter constructor is not a String");
		
		X12Structure = mappedStructure.getStructureDefinition();
		rootName = (String)rootNameObj;
		X12Root = X12Structure.nameStructure(rootName);
	}
	
	//----------------------------------------------------------------------------------------------------
	//                      Small methods in the MapperWrapper interface
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	public int transformType() {return AbstractMapperWrapper.TEXT_TYPE;}
	
	/**
	 * 
	 * @return the file extension of the outer document
	 */
	public String fileExtension() {return "*.txt";}
	
	//----------------------------------------------------------------------------------------------------
	//                                    making an X12 xml instance
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * @param X12BarFileObj input stream of the 'vertical bar' form message
	 * @param rootName name of the root element; equals the message name
	 * @return X12.XML Document
	 * @throws MapperException if, for instance, the actual message structure 
	 * does not match the expected structure
	 */
	public Document transformIn(Object X12FileObj)
	throws MapperException
	{
		if (!(X12FileObj instanceof InputStream))
			throw new MapperException("Input for making X12.xml instance is not an InputStream");
		InputStream X12File = (InputStream)X12FileObj;

		inResultDoc = XMLUtil.makeOutDoc();
		X12XMLRoot = XMLUtil.newElement(inResultDoc, rootName);
		inResultDoc.appendChild(X12XMLRoot);
		
		Vector<String> lines = removeEmptyLines(FileUtil.getLines(X12File));
		
		ElementDef currentParent = X12Root;
		Element currentXMLParent = X12XMLRoot;
		for (int i = 0; i < lines.size(); i++)
		{
			String line = lines.get(i);
			trace("\n" + line);
			Vector<ElementDef> possibles = possibleNodes(line);
			if (possibles.size() ==0) trace("Cannot match line " + line);
			else
			{
				// choose the best ElementDef to hold the line, depending on the current parent
				ElementDef chosen = chooseElement(currentParent, possibles);
				Element newXMLParent = writeElement(currentParent, chosen, currentXMLParent,line);

				currentParent = (ElementDef)chosen.eContainer();
				currentXMLParent = newXMLParent;
			}
		}
		
		return inResultDoc;
	}
	
	/**
	 * 
	 * @param currentParent ElementDef for the node the previous Element was written under
	 * @param chosen ElementDef chosen to hold the new ElementDef
	 * @param currentXMLParent XML Element the previous Element was written under
	 * @return the new XML parent
	 * @throws MapperException
	 */
	private Element writeElement(ElementDef currentParent,ElementDef chosen, 
			Element currentXMLParent, String line)
	throws MapperException
	{
		Element newParent = null;
		Element newEl = XMLUtil.newElement(inResultDoc, chosen.getName());
		if (writeLineContent) fillLine(chosen,newEl,line,true);

		/* if the chosen ElementDef is under the current parent, carry on growing that tree
		otherwise, go back up the tree to find the lowest ancestor */
		while (!isAncestor(currentParent,chosen))
		{
			currentParent = (ElementDef)currentParent.eContainer();
			currentXMLParent = (Element)currentXMLParent.getParentNode();
		}
		newParent = attachDescendant(currentParent,chosen,currentXMLParent,newEl);
		return newParent;
	}
	
	/**
	 * Fill in the child elements that represent the content of a line in the X12
	 * @param chosen ElementDef corresponding to a line in the X12
	 * @param lineEl the Element to hold it
	 * @param line the line text
	 * @param writeProblems if true, write messages for any problems detected
	 * @return false if any problem is detected
	 */
	private boolean fillLine(ElementDef chosen,Element lineEl,String line, boolean writeProblems)
	throws MapperException
	{
		boolean OK = true;
		int childNodes = chosen.getChildElements().size();
		String[] fields = new String[childNodes];
		
		// fill in the array of fields from the line
		int fieldNo = 0;
		StringTokenizer st = new StringTokenizer(line,"*~",true);
		st.nextToken(); // strip off the first 'field' which is just the line name
		String lastToken = st.nextToken(); // strip off the first '*'
		while ((st.hasMoreTokens())  && OK)
		{
			String token = st.nextToken();
			if (token.equals("*"))
			{
				fieldNo++;
				// overflow of the elements allowed for the line
				if (fieldNo > childNodes - 1)
				{
					OK = false;
					if (writeProblems) trace("Line '" + line + "' has more than " + childNodes + fields);
				}
				// two consecutive '*' mean an empty field
				else if(lastToken.equals("*")) fields[fieldNo] = "";
			}
			else if (!(token.equals("~"))) fields[fieldNo] = token;
			lastToken = token;
		}
		
		// write Elements for the fields
		for (int f = 0; f < childNodes; f++) if (OK)
		{
			String field = fields[f];
			ElementDef child = chosen.getChildElements().get(f);

			// if the field has content, write it in an Element
			if ((field != null) && (!field.equals("")))
			{
				Element fieldEl = XMLUtil.textElement(inResultDoc, child.getName(),field);
				lineEl.appendChild(fieldEl);
			}
			// if the field has no content but it should have...
			else if (child.getMinMultiplicity() == MinMult.ONE)
			{
				OK = false;
				if (writeProblems) trace("Missing field " + (f+1) + " in line " + line);
			}
		}
		return OK;
	}
	
	/**
	 * 
	 * @param ancestor the chosen ElementDef, to hold the new Element , is somewhere below this node
	 * @param chosen will hold the new element
	 * @param currentXMLParent XML element corresponding to ancestor
	 * @param lineEl new XML element to be attached as a descendant
	 * @return the parent XML element above the new element
	 * @throws MapperException
	 */
	private Element attachDescendant(ElementDef ancestor,ElementDef chosen,
			Element currentXMLParent,Element lineEl)
	throws MapperException
	{
		// common case; line Element to be attached is a child (direct)
		if (getDistance(ancestor, chosen) == 1)
		{
			currentXMLParent.appendChild(lineEl);
			return currentXMLParent;
		}
		/* line Element is a more remote descendant; fill in the chain in between
		 * and return its immediate parent */
		else if (getDistance(ancestor, chosen) > 1)
		{
			ElementDef chosenParent = (ElementDef)chosen.eContainer();
			Element parentEl = XMLUtil.newElement(inResultDoc, chosenParent.getName());
			parentEl.appendChild(lineEl);
			/* ignore the return from the recursive call (reduced distance), 
			 * to return the direct parent of the element for the line */
			attachDescendant(ancestor, chosenParent,currentXMLParent,parentEl);
			return parentEl;
		}
		else throw new MapperException("Unexpected case at " + ancestor.getName());
	}


	
	/**
	 * 
	 * @param currentParent the parent node that the last line was attached to
	 * @param possibles  ElementDefs which one might attach this line to
	 * @return the best choice
	 * @throws MapperException
	 */
	private ElementDef chooseElement(ElementDef currentParent, Vector<ElementDef> possibles)
	throws MapperException
	{
		ElementDef chosen = possibles.get(0);
		// if there is any choice....
		if (possibles.size() > 0)
		{
			// heuristic; shortest distance from current parent
			int minDistance = 1000;
			for (int i = 0; i < possibles.size(); i++)
			{
				int distance = getDistance(currentParent, possibles.get(i));
				if (distance < minDistance)
				{
					chosen = possibles.get(i);
					minDistance = distance;
				}
			}
		}
		return chosen;
	}
	
	/**
	 * @param el1
	 * @param el2
	 * @return the number of steps in the traverse from el1 to el2
	 */
	private int getDistance(ElementDef el1, ElementDef el2) throws MapperException
	{
		if (isAncestor(el1,el2)) return steps(el1,el2);
		else
		{
			EObject parent = el1.eContainer();
			if (parent instanceof ElementDef) return (1 + getDistance((ElementDef)parent,el2));
			//this should never happen
			else throw new MapperException("No traverse between nodes");
		}
	}
	
	/**
	 * 
	 * @param anc
	 * @param desc
	 * @return true if anc is an ancestor node of desc
	 */
	private boolean isAncestor(ElementDef anc, ElementDef desc)
	{
		if (anc.equals(desc)) return true;
		EObject parent = desc.eContainer();
		if (parent instanceof ElementDef) return isAncestor(anc, (ElementDef)parent);
		return false;
	}
	
	/**
	 * desc is known to be a descendant of anc
	 * @param anc
	 * @param desc
	 * @return the number of steps between them
	 */
	private int steps(ElementDef anc, ElementDef desc)
	{
		if (anc.equals(desc)) return 0;
		else return (1 + steps(anc,(ElementDef)desc.eContainer()));		
	}

	
	/**
	 * Preferred tag names for X12 lines, depending on the qualfier that follows the line name
	 */
	private String[][] qualifierNodes = {
			{"N1","PR","N1_PayerIdentification"},
			{"N1","PE","N1_PayeeIdentification"},
	};
	
	/**
	 * @param start start of an X12 line
	 * @param qualifier qualifier field which comes immediately after the start
	 * @return preferred tag name for that start and qualifier, if there is one; or null
	 */
	private String getPreferredTagName(String start,String qualifier)
	{
		String preferred = null;
		for (int i = 0; i < qualifierNodes.length;i++)
		{
			String[] triple = qualifierNodes[i];
			if ((triple[0].equals(start)) && (triple[1].equals(qualifier)))
				preferred = triple[2];
		}
		return preferred;
	}
	
	/**
	 * @param line a line in the X12 file
	 * @return any nodes in the structure that could hold this line
	 */
	private Vector<ElementDef> possibleNodes(String line)
	{
		Vector<ElementDef> possibles = new Vector<ElementDef>();
		StringTokenizer st = new StringTokenizer(line,"*");
		String start = st.nextToken();
		String qualifier = st.nextToken();
		String preferredTag = getPreferredTagName(start,qualifier);
		for (Iterator<EObject> ie = ModelUtil.getEObjectsUnder(X12Root,MapperPackage.Literals.ELEMENT_DEF).iterator();ie.hasNext();)
		{
			ElementDef elDef = (ElementDef)ie.next();
			StringTokenizer ut = new StringTokenizer(elDef.getName(),"_");
			String tagStart = ut.nextToken();
			if ((tagStart.equals(start)) && (isLineElement(elDef))) 
			{
				/* If there is a preferred tag name for this X12 line with its 
				 * qualifier, only allow that one; otherwise keep all candidates */
				if ((preferredTag == null)||
						((preferredTag != null) && (elDef.getName().equals(preferredTag))))
							possibles.add(elDef);
			}
		}
		return possibles;
	}
	
	/**
	 * @param el an ElementDef in the structure
	 * @return true if this ElementDef corresponds to a line in the X12 message -
	 * i.e. if it has children but its name does not end in 'Loop'
	 */
	private boolean isLineElement(ElementDef el)
	{
		return ((el.getChildElements().size() > 0) && (!(el.getName().endsWith("Loop"))));
	}
	
	/**
	 * 
	 * @param lines lines read from an X12 file
	 * @return the same with empty lines removed
	 */
	private Vector<String> removeEmptyLines(Vector<String> lines)
	{
		Vector<String> result = new Vector<String>();
		for (int i = 0; i < lines.size(); i++)
			if (lines.get(i).length() > 0) result.add(lines.get(i));
		return result;
	}
	
	//----------------------------------------------------------------------------------------------------
	//                                    making an X12 text instance
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * @param X12Root Root element of a X12.xml message or segment
	 * @return String array of segment text
	 */
	public String[] transformOut(Element X12Root)
	throws MapperException
	{

		ArrayList<String> barForm = new ArrayList<String>();
		String[] result = (String[])barForm.toArray(new String[barForm.size()]);
		return result;
		
	}


}
