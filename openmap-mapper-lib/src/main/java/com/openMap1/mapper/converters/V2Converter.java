package com.openMap1.mapper.converters;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;

/**
 * Class with methods to convert V2 message instances between
 * the vertical bar form and the XML form
 * @author robert
 *
 */
public class V2Converter  extends AbstractMapperWrapper implements MapperWrapper{
	
	// default separators - may be changed by the message header MSH segment
	private String fieldSeparator = "|";
	private String componentSeparator = "^";
	private String subComponentSeparator = "&";
	private String repetitionSeparator = "~";
	private String escapeCharacter = "\\";
	
	private StructureDefinition V2Structure;
	
	private ElementDef V2Root;
	
	private String rootName;
	
	private Document doc;
	
	public static String V2_NAMESPACE_URI = "urn:hl7-org:v2xml";
	
	protected boolean tracing() {return false;}
	
	/* if strict = true, whenever it encounters a problem in an in-wrapper transform it throws an exception,
	 * i.e stops the transform.
	 * if strict = false, it just writes a message to the console */
	private boolean strict = false;
	
	private void throwOrWriteMessage(String warning) throws MapperException
	{
		if (strict) throw new MapperException(warning);
		else System.out.println(warning);
	}
	
	//----------------------------------------------------------------------------------------------------
	//                                        constructor
	//----------------------------------------------------------------------------------------------------
	
	public V2Converter(MappedStructure mappedStructure, Object rootNameObj) throws MapperException
	{
		super(mappedStructure, rootNameObj);

		if (!(rootNameObj instanceof String))
			throw new MapperException("Second argument of V2Converter constructor is not a String");
		
		V2Structure = mappedStructure.getStructureDefinition();
		this.rootName = (String)rootNameObj;

		V2Root = V2Structure.nameStructure(rootName);
		if (V2Root == null) 
			throw new MapperException("V2 Structure definition does not define the message '" + rootName + "'");
		
		if (escapeCharacter == null) {} // to avoid a compiler warning that it is unused
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
	//                                    round trip test
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * convert a bar-coded V2 message to V2.xml, then back again, 
	 * and throw a MapperException at the first discrepancy found
	 */
	public void doRoundTripTest(FileInputStream V2BarFile)
	throws MapperException
	{
		doc = XMLUtil.makeOutDoc();
		Element root = XMLUtil.newElement(doc, rootName);
		root.setAttribute("xmlns", V2_NAMESPACE_URI);
		doc.appendChild(root);
		
		Vector<String> lines = FileUtil.getLines(V2BarFile);
		
		// fill out the root element of the V2.xml file
		writeLines(root,V2Root, lines);
		
		// use the V2.xml file to make a round trip back to bar coded form
		String[] newLines = transformOut(root);
		
		// compare the start and end of the round trip
		int minLines = Math.min(lines.size(), newLines.length);
		for (int l = 0; l < minLines;l++)
			compareLine(lines.get(l),newLines[l]);
		
		if (lines.size() != newLines.length)
			throw new MapperException("There are " + lines.size() 
					+ " segments in the original, " + newLines.length 
					+ " segments in the round trip result.");
		
	}
	
	/**
	 * @param line a segment line of the input V2 message
	 * @param newLine the same line of the round trip result
	 * @throws MapperException if there is any difference between them
	 */
	private void compareLine(String line, String newLine) throws MapperException
	{
		int minLength = Math.min(line.length(),newLine.length());
		for (int c = 0; c < minLength; c++)
			if (line.charAt(c) != newLine.charAt(c))
				throw new MapperException("Round trip discrepancy at the last character of '" 
						+ newLine.substring(0, c + 1) + "'; original was '" + line + "'");
		
		if (line.length() != newLine.length())
			throw new MapperException ("Round trip line length " + newLine.length() 
					+ " does not match input line length " + line.length() + " at line '"
					+ newLine + "'");
	}
	
	//----------------------------------------------------------------------------------------------------
	//                                    writing a V2.xml instance
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * @param V2BarFileObj input stream of the 'vertical bar' form message
	 * @param rootName name of the root element; equals the message name
	 * @return V2.XML Document
	 * @throws MapperException if, for instance, the actual message structure 
	 * does not match the expected structure
	 */
	public Document transformIn(Object V2BarFileObj)
	throws MapperException
	{
		if (!(V2BarFileObj instanceof InputStream))
			throw new MapperException("Input for making V2.xml instance is not an InputStresm");
		InputStream V2BarFile = (InputStream)V2BarFileObj;

		doc = XMLUtil.makeOutDoc();
		Element root = XMLUtil.newElement(doc, rootName);
		root.setAttribute("xmlns", V2_NAMESPACE_URI);
		doc.appendChild(root);
		
		Vector<String> lines = FileUtil.getLines(V2BarFile);
		trace("Converting " + lines.size() + " lines to XML");
		writeLines(root,V2Root, lines);
		return doc;
	}
	
	
	/**
	 * add Elements beneath a root Element, (one per segment but sometimes grouped into
	 * segment groups) for a V2 bar-encoded message
	 * @param V2Root ElementDef of the V2 structure being constructed
	 * @param lines lines from the vertical bar form message, one per segment
	 */
	private void writeLines(Element root, ElementDef V2Root, Vector<String> lines)
	throws MapperException
	{
		V2Root = expandElementDefinition(V2Root);
		int elementDefs = V2Root.getChildElements().size();
		int elementDefInMessage = 0; // top-level ElementDef we are currently trying to match
		boolean[] matched = new boolean[elementDefs]; // track which ElementDefs are matched
		for (int i = 0; i < elementDefs;i++) matched[i] = false;
		
		/* do all segments in the vertical bar message. This is not a simple iteration,
		 * as the line position pos may be moved forward several steps by a segment group */
		int pos = 0;
		while (pos < lines.size())
		{
			String line = lines.get(pos);
			String segmentName = line.substring(0, 3);
			trace("line " + pos + ": " + line);

			// try to match the current segment with the current or some later ElementDef
			boolean found = false; // true if the segment in the bar encoded message is matched
			while ((!found) && (elementDefInMessage < elementDefs))
			{
				if (pos > lines.size() - 1) return;
				int oldPos = pos;
				// current top-level ElementDef
				ElementDef currentDef = getChildElementDef(V2Root,elementDefInMessage);	
				trace("Matching ElementDef " + currentDef.getName());
				
				// current top level elementDef is a segment
				if (currentDef.getName().equals(segmentName))
				{
					found = true;
					matched[elementDefInMessage] = true;
					// make the subtree for the matched segment
					writeSegment(root,line,currentDef);
					// if this segment cannot be repeated, move on to the next segment definition
					if (currentDef.getMaxMultiplicity() == MaxMult.ONE) elementDefInMessage++;
					pos++;
				}
				
				// current top level elementDef is a segment group
				else if (isSegmentGroup(currentDef))
				{
					/* try to match the current line and subsequent lines with the start of a segment group; 
					 *  (pos - oldPos) is the number of lines successfully consumed. */	
					boolean isCompulsory = (currentDef.getMinMultiplicity() == MinMult.ONE);
					pos = writeSegmentGroup(isCompulsory,root,lines,pos,currentDef);
					// record successful match of segment group
					if (pos > oldPos) matched[elementDefInMessage] = true;
					// match failed; this is OK if the segment group was optional, or has been matched already, but not otherwise
					else if ((pos == oldPos) 
							&& (currentDef.getMinMultiplicity() == MinMult.ONE)
							&& !matched[elementDefInMessage])
					{
						String warning = "Failed to match obligatory segment group '" + currentDef.getName() + "'";
						throwOrWriteMessage(warning);
					}
					// if this segment group cannot be repeated, move on to the next segment or group definition
					if (currentDef.getMaxMultiplicity() == MaxMult.ONE) elementDefInMessage++;
				}
				
				// failed to match an obligatory segment or segment group
				else if (currentDef.getMinMultiplicity() == MinMult.ONE) 
				{
					String warning = "Failed to match obligatory segment or segment group '"
							+ currentDef.getName() + "'";
					throwOrWriteMessage(warning);
				}

				// if the current segment definition cannot be matched, try to match the line with the next definition
				else elementDefInMessage++;
			} // end of loop over ElementDefs being matched against the current line

			if (!found) 
			{
				String warning = ("Cannot match segment name " + segmentName + " of line " + (pos + 1) 
						+ ": '" + line + "' with the structure definition");
				throwOrWriteMessage(warning);
			}

		} // end of loop over line number pos of the bar-coded message

		// run out of text; check that no later segments or segment groups require more text
		if (elementDefInMessage + 1 < elementDefs)
			for (int ed = elementDefInMessage + 1; ed < elementDefs; ed++)
				checkIfTextNeeded(V2Root.getChildElements().get(ed));
	}
	
	/**
	 * @param elDef and ElementDef for a segment or segment group in the V2 message definition
	 * @throws MapperException if there is some segment inside the group which requires some text
	 */
	private void checkIfTextNeeded(ElementDef elDef) throws MapperException
	{
		// no problem if the segment or group is optional
		if (elDef.getMinMultiplicity() == MinMult.ONE)
		{
			if (!isSegmentGroup(elDef)) 
				throw new MapperException("Message text required to match segment " + elDef.getName());
			else for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
				checkIfTextNeeded(it.next());
		}
	}
	
	private boolean isSegmentGroup(ElementDef elDef)
	{
		String nodeType = elDef.getAnnotation("V2NodeType");
		return ((nodeType != null) && (nodeType.equals("SegGroup")));
	}
	
	/**
	 * @param lines Vector of line in the vertical bar message, one per segment
	 * @param pos current position in the list of lines being analysed
	 * @param currentDef ElementDef defining a segment group, that the current line
	 * should be the first segment of
	 * @return the next position in the Vector of lines after this segment group
	 * has been completely analysed (If the position has not been increased, the
	 * analysis is unsuccessful)
	 */
	private int writeSegmentGroup(boolean isCompulsory,Element root,Vector<String> lines, int pos,ElementDef groupDef)
	throws MapperException
	{
		int newPos = pos; // newPos is the current unmatched segment
		if (newPos > lines.size() -1) return newPos; // to avoid spurious checks

		// make the Element for the segment group (attach it only if something matches)
		Element segGroup = XMLUtil.newElement(doc, groupDef.getName());

		// iterate over all segment definitions and segment group definitions in the group
		groupDef = expandElementDefinition(groupDef);
		trace ("Segment group " + groupDef.getName());
		List<ElementDef> groupMembers = groupDef.getChildElements();
		int defPosLast = -5;
		for (int defPos = 0; defPos < groupMembers.size(); defPos++)
		{
			boolean repeat = (defPos == defPosLast);
			defPosLast = defPos;
			ElementDef segDef = groupMembers.get(defPos);
			trace("Member " + segDef.getName() + " defPos " + defPos + " newPos " + newPos);
			boolean childIsCompulsory = isCompulsory && (segDef.getMinMultiplicity() == MinMult.ONE);
			if (isSegmentGroup(segDef))
			{
				int groupStartPos = newPos;
				newPos = writeSegmentGroup(childIsCompulsory, segGroup, lines, newPos,segDef);
				// if the segment group has matched and can be repeated, let it try again
				if ((newPos > groupStartPos) && (segDef.getMaxMultiplicity() == MaxMult.UNBOUNDED)) defPos--;
			}
			else if (!isSegmentGroup(segDef))
			{
				if (newPos < lines.size())
				{
					String line = lines.get(newPos);
					String segName = line.substring(0,3);
					
					// match of the text segment with the segment definition
					if (segName.equals(segDef.getName()))
					{
						writeSegment(segGroup,line,segDef);	
						newPos++; // move on to the next line
						/* if the segment definition can repeat, 
						 * make it repeat to try it again against the next text segment */
						if (segDef.getMaxMultiplicity() == MaxMult.UNBOUNDED) defPos--;
					}
					
					// if the segment group is compulsory and this child is compulsory, but not matched...
					else if ((childIsCompulsory) && (!repeat))
					{
						throwOrWriteMessage("In compulsory segment group " + groupDef.getName()+ " failed to match compulsory segment '" + segDef.getName() + "'");
					}

					// failure to match an obligatory segment (in the optional group) is failure to match the group
					else if ((segDef.getMinMultiplicity() == MinMult.ONE) && (!repeat))
					{
						// some previous segment in the group has matched, and now an obligatory segment has failed
						if (newPos > pos) 
						{
							throwOrWriteMessage("In segment group " + groupDef.getName() 
									+ " failed to match obligatory segment " + segDef.getName() + 
									" after matching some previous segment");
						}
						// no matches in the optional group - fail and return so the next segment or group can be tried
						else return newPos;
					}				
					
				}
				// have run out of text to match
				else if (newPos > lines.size() -1)
				{
					if ((segDef.getMinMultiplicity() == MinMult.ONE) && (segDef.getMaxMultiplicity() == MaxMult.ONE))
					{
						throwOrWriteMessage("Run out of message to match structure definition in segment " 
								+ segDef.getName() + " of group " + groupDef.getName());						
					}
				}
			}
		}

		// only attach the segment group if it contains some segments
		if (newPos > pos) 
		{
			trace("Attach group " + groupDef.getName());
			root.appendChild(segGroup);
		}
		
		// if a compulsory segment group has not matched, throw an Exception
		else if ((newPos == pos) && isCompulsory)
			throwOrWriteMessage("Failed to match compulsory segment group '" + groupDef.getName() + "'");
		return newPos;
	}
	
	/**
	 * @param root root of document , or segment group element under which the segment element is written
	 * @param line text of the line in the bar-form message
	 * @param segDef ElementDef defining the segment
	 * @throws MapperException
	 */
	private void writeSegment(Element root,String line,ElementDef segDef)
	throws MapperException
	{
		String segmentName = segDef.getName();
		trace("Writing segment " + segmentName);
		Element segElement = XMLUtil.newElement(doc, segmentName);
		root.appendChild(segElement);
		int mshDoneAlready = 0; 

		segDef = expandElementDefinition(segDef);
		List<ElementDef> fieldDefs = segDef.getChildElements();

		
		/* the message header may change the separators from the usual default 
		 * A message header MSH|^~\&|LAB|767543|ADT|767543|199003141304-0500||ACK^^ACK|XX3657|P|2.4
		 * becomes 
		 * <MSH>
                <MSH.1>|</MSH.1>
                <MSH.2>^~\&amp;</MSH.2>
                <MSH.3>
                    <HD.1>LAB</HD.1>
                </MSH.3>
		  etc. */
		if (segmentName.equals("MSH"))
		{
			fieldSeparator = line.substring(3,4);
			componentSeparator = line.substring(4,5);
			repetitionSeparator = line.substring(5,6);
			escapeCharacter = line.substring(6,7);
			subComponentSeparator = line.substring(7,8);
			String msh2 = line.substring(4,8);
			// put in the MSH.1 field (whose content is just the field separator) and MSH.2
			writeField(segElement,fieldSeparator,fieldDefs.get(0),0);
			writeField(segElement,msh2,fieldDefs.get(1),1);
			mshDoneAlready = 2;
		}
		
		StringTokenizer st = new StringTokenizer(line,fieldSeparator,true); // return all '|' as tokens
		st.nextToken(); // remove the first token, which is the segment name
		st.nextToken(); // remove the first '|'
		// remove the content of MSH.2 and the next '|'
		if (segmentName.equals("MSH")) {st.nextToken();st.nextToken();}
		
		int fieldPos = mshDoneAlready; // 0 for most segments, 2 for MSH
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			// track the field position; 0 after first '|', except for MSH
			if (token.equals(fieldSeparator)) fieldPos++;

			// some non-empty string between field separators
			else 
			{
				if (fieldPos < fieldDefs.size())
				{
					ElementDef fieldDef = fieldDefs.get(fieldPos);
					StringTokenizer fr = new StringTokenizer(token,repetitionSeparator);
					if ((fr.countTokens() > 1) && (fieldDef.getMaxMultiplicity() == MaxMult.ONE))
						throwOrWriteMessage("Field " + (fieldPos + 1) + " of segment " + segmentName + " cannot repeat.");
					while (fr.hasMoreTokens())
						writeField(segElement,fr.nextToken(),fieldDef,fieldPos);									
				}
				else throwOrWriteMessage("Segment " + segmentName + " has more than " + fieldDefs.size() + " fields");
			}
		}
	}
	
	/**
	 * @param segElement Element for the segment
	 * @param fieldText text of one field (non-empty)
	 * @param fieldDef ElementDef defining the field
	 */
	private void writeField(Element segElement,String fieldText, ElementDef fieldDef,int fieldPos)
	throws MapperException
	{
		String fieldName = fieldDef.getName();
		// System.out.println("Field " + fieldName + ": '" + fieldText + "'");
		Element fieldElement = null; // may turn  out to be a text element or not

		fieldDef = expandElementDefinition(fieldDef);
		List<ElementDef> componentDefs = fieldDef.getChildElements();

		// field data type is composite and has components
		if ((componentDefs != null) && (componentDefs.size() > 0))
		{
			fieldElement = XMLUtil.newElement(doc, fieldName); // field Element contains no text directly
			StringTokenizer st = new StringTokenizer(fieldText,componentSeparator,true); // return all '^' as tokens
			int comp = 0; 
			boolean exceededLength = false;
			while (st.hasMoreTokens())
			{
				String token = st.nextToken();
				// track component position
				if (token.equals(componentSeparator)) comp++;
				else // non-empty string before or between or after '^'
				{
					if (comp < componentDefs.size())
					{
						ElementDef compDef = componentDefs.get(comp);
						writeComponent(fieldElement,token,compDef);						
					}
					else // failure condition
					{
						exceededLength = true;
						if (!st.hasMoreTokens()) comp++; // if there is a text field after the last separator
					}
				}
			}
			if (exceededLength) throwOrWriteMessage("Field '" + fieldName + "' ( field no. " + (fieldPos + 1) 
					+ " '" + fieldDef.getDescription() 
					+ "', type '" + fieldDef.getType() + "') of segment " + XMLUtil.getLocalName(segElement)
					+ " '" + fieldText + "' "
					+ " has " + comp + " components, more than allowed " + componentDefs.size());
		}
		// field data type is elementary and has no components
		else
		{
			fieldElement = XMLUtil.textElement(doc, fieldName, fieldText);
		}
		segElement.appendChild(fieldElement);
	}
	
	/**
	 * @param fieldElement XML Element for the field
	 * @param component String for the component
	 * @param compDef ElementDef defining the component structure
	 * @throws MapperException
	 */
	private void writeComponent(Element fieldElement,String component,ElementDef compDef)
	throws MapperException
	{
		String compName = compDef.getName();
		Element compElement = null;

		compDef = expandElementDefinition(compDef);
		List<ElementDef> subComponentDefs = compDef.getChildElements();

		// component data type is composite and has sub-components
		if ((subComponentDefs != null) && (subComponentDefs.size() > 0))
		{
			compElement = XMLUtil.newElement(doc, compName); // component Element contains no text directly
			StringTokenizer st = new StringTokenizer(component,subComponentSeparator,true); // return all '~^' as tokens
			int comp = 0; 
			while (st.hasMoreTokens())
			{
				String token = st.nextToken();
				// track component position
				if (token.equals(subComponentSeparator)) comp++;
				else // non-empty string before or between '~'
				{
					if (comp < subComponentDefs.size())
					{
						ElementDef subCompDef = subComponentDefs.get(comp);
						Element subComp = XMLUtil.textElement(doc, subCompDef.getName(), token);
						compElement.appendChild(subComp);
					}
					else throwOrWriteMessage("Component has more than " + subComponentDefs.size() + " sub-components");
				}
			}
		}
		else
		{
			compElement = XMLUtil.textElement(doc, compName, component);
		}
		fieldElement.appendChild(compElement);
	}

	
	private ElementDef getChildElementDef(ElementDef parent, int position)
	{
		ElementDef child = null;
		List<ElementDef> children = parent.getChildElements();
		if ((position > -1) && (position < children.size()))child = children.get(position);
		return child;
	}
	
	/**
	 * Expand the structure definition of any ElementDef
	 * @param elementDef
	 */
	private ElementDef expandElementDefinition(ElementDef elementDef) throws MapperException
	{
		ElementDef newElDef = elementDef;
		if (!elementDef.isExpanded()) 
		{
			String type = elementDef.getType();
			newElDef = V2Structure.typeStructure(type);
			if (newElDef == null) throw new MapperException("Cannot find structure definition of type '" + type + "'");
			newElDef.setExpanded(true);
			
			/* retain the name and the description of the unexpanded element - as the type definition may have been taken
			 * from a different usage of the type in the MWB file */
			newElDef.setName(elementDef.getName());
			newElDef.setDescription(elementDef.getDescription());
		}
		return newElDef;
	}

	
	//----------------------------------------------------------------------------------------------------
	//                            writing a V2 message instance in bar-hat notation 
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * @param IFolder a folder in the Eclipse workspace
	 * @param V2Root root element of a V2.XML message or segment
	 * @param fileName name of the file to be created
	 * Creates a file containing the message in 'bar-hat' form
	 */
	public void makeBarCodedForm(IFolder folder, Element V2Root, String fileName) throws MapperException
	{
		String[] messageText  = transformOut(V2Root);
		if (messageText.length < 1) throw new MapperException("V2 message text has no lines");
		IFile theFile =  folder.getFile(fileName);
		try{
			InputStream firstLine = EclipseFileUtil.textStream(messageText[0]);
			theFile.create(firstLine, false, null);
			for (int line = 1; line < messageText.length; line++)
				EclipseFileUtil.appendLine(messageText[line], theFile);
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
	}

	
	/**
	 * @param V2Root Root element of a V2.xml message or segment
	 * @return String array of segment text
	 */
	public String[] transformOut(Element V2Root)
	throws MapperException
	{
		/* currently there is no check of the top Element name. For some messages it 
		 * is not the message name; e.g. ADT_A28 has top element name ADT_A05. */
		
		// check there is a V2 namespace declaration on the top Element
		if (!checkV2Namespace(V2Root)) throw new MapperException
			("V2.XML document does not declare the V2 namespace '" + V2_NAMESPACE_URI + "'");

		ArrayList<String> barForm = new ArrayList<String>();
		
		// can also handle single segments
		if (isSegmentElement(V2Root))  barForm.add(getSegmentText(V2Root));
		
		// iterate over segments and segment groups, picking out segments
		else for (Iterator<Element> it = XMLUtil.childElements(V2Root).iterator();it.hasNext();)
		{
			Element segEl = it.next();
			// segments at the top level
			if (isSegmentElement(segEl)) barForm.add(getSegmentText(segEl));
			else handleSegmentGroup(segEl,barForm);
		}
		String[] result = (String[])barForm.toArray(new String[barForm.size()]);
		
		// add a final '|' to the final segment of the result
		result[result.length -1] = result[result.length -1] + fieldSeparator;
		return result;
	}
	
	/**
	 * @param root root element of some XML
	 * @return true id the root element (or some element beneath it)
	 * declares the V2 namespace
	 */
	private boolean checkV2Namespace(Element root)
	{
		boolean hasV2Namespace = false;
		try{
			NamespaceSet nss = XMLUtil.getNameSpaceSet(root);
			if (nss.getByURI(V2_NAMESPACE_URI) != null) hasV2Namespace = true;
		}
		catch (Exception ex) {}
		return hasV2Namespace;		
	}
	
	/**
	 * add text for all segments directly in this group, or in groups in this group
	 * @param segEl Element representing a segment group
	 * @param barForm ArrayList of text segments
	 * @throws MapperException
	 */
	private void handleSegmentGroup(Element segEl,ArrayList<String> barForm) throws MapperException
	{
		for (Iterator<Element> ig = XMLUtil.childElements(segEl).iterator();ig.hasNext();)
		{
			Element child = ig.next();
			if (isSegmentElement(child)) barForm.add(getSegmentText(child));
			else handleSegmentGroup(child,barForm);
		}
	}

	
	/**
	 * @param el
	 * @return true is el is a segment element.
	 * Its name must be three characters, and it must have a child element whose
	 * name is the same three characters followed by '.N' where N
	 * can be read as an Integer
	 */
	private boolean isSegmentElement(Element el)
	{
		boolean isSegment = false;
		String segName = XMLUtil.getLocalName(el);
		if (segName.length() == 3)
		{
			Vector<Element> children = XMLUtil.childElements(el);
			if ((children != null) && (children.size() > 0))
			{
				String childName = XMLUtil.getLocalName(children.get(0));
				StringTokenizer st = new StringTokenizer(childName,".");
				if ((st.countTokens() == 2) && (st.nextToken().equals(segName))) try
				{
					new Integer(st.nextToken());
					isSegment = true; // it is a segment only if this throws no exception
				}
				catch (Exception ex) {}
			}
		}
		return isSegment;
	}

	/**
	 * @param segEl Element representing a segment in V2.xml
	 * @return the text representing the same segment in bar-coded form
	 * @throws MapperException
	 */
	private String getSegmentText(Element segEl) throws MapperException
	{
		int minSeparator = 0; 
		String segText = XMLUtil.getLocalName(segEl); // segment name first
		if (segText.equals("MSH")) {readMessageHeader(segEl); minSeparator = 1;}

		int maxIndex = maxChildIndex(segEl) + 1;
		String tagRoot = childTagRoot(segEl);
		for (int index = 1; index < maxIndex; index++)
		{
			// one separator before each field, including the first, except for MSH 
			if (index > minSeparator) segText = segText + fieldSeparator;

			String tagName= tagRoot + "." + index;
			Vector<Element> fieldEls = XMLUtil.namedChildElements(segEl, tagName);
			for (int f = 0; f < fieldEls.size(); f++)
			{
				// repeated field
				if (f > 0) segText = segText + repetitionSeparator;
				segText = segText + getFieldText(fieldEls.get(f));		
			}
		}		
		return segText;
	}
	
	/**
	 * 
	 * @param el an element, all of whose child elements have tag names XXX.N, where 
	 * N is an integer, 1 or higher
	 * @return the maximum value of N found
	 * @throws MapperException if any child tag name does not have this form
	 */
	private int maxChildIndex(Element el) throws MapperException
	{
		int maxVal = 0;
		int val = 0;
		for (Iterator<Element> it = XMLUtil.childElements(el).iterator();it.hasNext();)
		{
			String fieldName = XMLUtil.getLocalName(it.next());
			StringTokenizer st = new StringTokenizer(fieldName,".");
			if (st.countTokens() != 2) throw new MapperException("Tag name '" + fieldName + "' does not have one '.'");
			st.nextToken();
			try {val = new Integer(st.nextToken()).intValue();}
			catch (Exception ex) {throw new MapperException("Cannot extract field number from tag name '" 
					+ fieldName + "': " + ex.getMessage());}
			if (val > maxVal) maxVal = val;
		}
		return maxVal;
	}
	
	private String childTagRoot(Element el) throws MapperException
	{
		String tag = "";
		String nextTag = "";
		for (Iterator<Element> it = XMLUtil.childElements(el).iterator();it.hasNext();)
		{
			String fieldName = XMLUtil.getLocalName(it.next());
			StringTokenizer st = new StringTokenizer(fieldName,".");
			if (st.countTokens() != 2) throw new MapperException("Tag name '" + fieldName + "' does not have one '.'");
			nextTag = st.nextToken();
			if (tag.equals("")) tag = nextTag;
			if (!tag.equals(nextTag)) throw new MapperException("V2 Child tag names should be the same, but are'" + tag + "' and '" + nextTag + "'");
		}
		return tag;
	}

	
	/**
	 * @param fieldEl Element representing a field in V2.xml
	 * @return text representing the field in bar-encoded form
	 * @throws MapperException
	 */
	private String getFieldText(Element fieldEl) throws MapperException
	{
		String fieldText = "";
		int maxIndex = maxChildIndex(fieldEl) + 1;
		String tagRoot = childTagRoot(fieldEl);
		if (!tagRoot.equals(""))
		{
			for (int index = 1; index < maxIndex; index++)
			{
				if (index > 1) fieldText = fieldText + componentSeparator;
				String tagName = tagRoot + "." + index;
				Element compEl = XMLUtil.firstNamedChild(fieldEl, tagName);				
				if (compEl != null)
					fieldText = fieldText + getComponentText(compEl);
			}
		}
		else fieldText = XMLUtil.getText(fieldEl);
		// the separator in the place of MSH.1 will get added as a separator
		if (XMLUtil.getLocalName(fieldEl).equals("MSH.1")) fieldText = "";
		return fieldText;
	}

	
	/**
	 * @param componentEl Element representing a component in V2.xml
	 * @return text representing the component in bar-encoded form
	 * @throws MapperException
	 */
	private String getComponentText(Element componentEl) throws MapperException
	{
		String componentText = "";
		Vector<Element> subComponents = XMLUtil.childElements(componentEl);
		if ((subComponents != null) && (subComponents.size() > 0))
		{
			int lastSubComponentFound = 0;
			int subComponentNumber = 0;
			for (Iterator<Element> ic = subComponents.iterator(); ic.hasNext();)
			{
				Element subCompEl = ic.next();
				String subCompName  = XMLUtil.getLocalName(subCompEl);
				StringTokenizer parts = new StringTokenizer(subCompName,".");
				parts.nextToken(); // data type name
				try {subComponentNumber = new Integer(parts.nextToken()).intValue();}
				catch (Exception ex) {throw new MapperException("Cannot extract sub-component number from tag name '" 
						+ subCompName + "': " + ex.getMessage());}
				
				if (lastSubComponentFound != 0)
					for (int i = lastSubComponentFound; i < subComponentNumber; i++)
						componentText = componentText + subComponentSeparator;
				
				lastSubComponentFound = subComponentNumber;
				componentText = componentText + XMLUtil.getText(subCompEl);
			}
		}
		else componentText = XMLUtil.getText(componentEl);
		return componentText;
	}

	
	/**
	 * read and possibly reset any special characters from the message header
	 * @param segEl message header element
	 */
	private void readMessageHeader(Element segEl)
	{
		Element msh1 = XMLUtil.firstNamedChild(segEl, "MSH.1");
		fieldSeparator = XMLUtil.getText(msh1);

		Element msh2 = XMLUtil.firstNamedChild(segEl, "MSH.2");
		String specialChars = XMLUtil.getText(msh2);
		componentSeparator = specialChars.substring(0,1);
		repetitionSeparator = specialChars.substring(1,2);
		escapeCharacter = specialChars.substring(2,3);
		// don't read the subcomponent separator as '&' has been escaped to '\&amp;'		
	}
	
	
	
	
}
