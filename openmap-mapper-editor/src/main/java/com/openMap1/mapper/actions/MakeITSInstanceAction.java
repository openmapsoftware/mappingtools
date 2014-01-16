package com.openMap1.mapper.actions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.openMap1.mapper.converters.CDAConverter;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.structures.XSDStructure;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLOutputFile;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;

public class MakeITSInstanceAction extends Action implements IAction{
	
	
	private MapperEditor mapperEditor;
	
    private MappedStructure mappedStructure;
    
    /* in expanding un-expanded elements, the maximum number of nested repeats of the same type */
    private int MAX_TYPE_REPEATS = 1;
    
    /* for elements that can repeat, the number of repeats generated in the xml */
    private int MULTI_REPEAT = 1;
    
    boolean tracing = true;
    
	
	/* index used to put different attribute values on different string attributes of the skeleton instance */
	private int stringIndex = 0;

	
	/* index used to put different attribute values on different integer or float attributes of the skeleton instance */
	private int numberIndex = 0;
	
	/* index to count the number of boolean values 'false' put in the generated instance */
	private int booleanIndex = 0;
	
	// number of vales copied across from a previous example
	private int previousValuesUsed = 0;
	
	// variable numbers in some previous example, to be retained, keyed by XPath
	private Hashtable<String,Integer> previousVariablesByPath;
	
	// if a previous example occupies some range of variable numbers, this is the lowest new variable number to use, to avoid clashes
	private int lowestNewVariableNumber = 0;

	public MakeITSInstanceAction()
	{
		super("Make Example Message and Schema");
	}
	/**
	 * Make a skeleton instance of the XML, and a schema (allowing either to be cancelled independently)
	 */
	public void run()
	{
		mapperEditor = WorkBenchUtil.getOpenMapperEditor();
			if (mapperEditor != null)
			{
				mappedStructure = WorkBenchUtil.mappingRoot(mapperEditor);
				if (mappedStructure.getRootElement() != null) try
				{
					writeSkeletonInstance();
					writeSchema();
				}
				catch(Exception ex) 
				{
					errorMessage(ex.getMessage());
					ex.printStackTrace();
				}
				else errorMessage("No root element in mapping set");				
			}
			else errorMessage("No Mapping set currently open in the Mapper Editor");				
	}
	
	//------------------------------------------------------------------------------------------
	//                             Making a skeleton instance
	//------------------------------------------------------------------------------------------
	
	private void writeSkeletonInstance() throws MapperException
	{
		askUserForPreviousExample();
		
		IFile instanceFile = WorkBenchUtil.makeOutputIFile("Skeleton XML Instance", "Choose file name for example message instance");
		if (instanceFile != null)
		{
			XMLOutputFile outFile = new XMLOutputFile();
			ElementDef rootDef = mappedStructure.getRootElement();
			Element root = outFile.newElement(rootDef.getName());
			addNamespaceAttributes(root);
			outFile.setTopOut(root);
			// if simple messages are in the V3 namespace, give it a namespace attribute
			if (CDAConverter.SIMPLE_MESSAGE_IN_V3_NAMESPACE) root.setAttribute("xmlns", CDAConverter.V3NAMESPACEURI);
			previousValuesUsed = 0;
			stringIndex = 0;
			numberIndex = 0; 
			booleanIndex = 0;
			
			// recursive descent, expanding elements as needed
			String path = ""; 
			fillElement(outFile,rootDef,root,path);
			
			int dataValues = previousValuesUsed + stringIndex + numberIndex + booleanIndex;

			/* Write out the instance. ask the user for a file name. */
			EclipseFileUtil.writeOutputResource(outFile.outDoc(),instanceFile,true);			
			WorkBenchUtil.showMessage("Completed", "Wrote message instance with " 
					+ dataValues + " arbitrary data values ");			
		}
		else WorkBenchUtil.showMessage("Cancelled","No message instance written");
	}
	
	/**
	 * 
	 * @throws MapperException
	 */
	private void askUserForPreviousExample() throws MapperException
	{
		previousVariablesByPath = new Hashtable<String,Integer>();

		String[] exts = {"*.xml"}; 
		String filePath = FileUtil.getFilePathFromUser(mapperEditor,exts,"Select Previous example to retain variable numbers",false);			
		if (filePath.equals("")) return;
		
		Element root = XMLUtil.getRootElement(filePath);
		int variables = noteAllVariables(root,"");	
		System.out.println("variables noted: " + variables + "; " + previousVariablesByPath.size() + "." + lowestNewVariableNumber);
	}
	
	/**
	 * recursively note all variables in an XML element and all its descendants
	 * @param el
	 * @param path
	 */
	private int noteAllVariables(Element el,String path)
	{
		int count = 0;
		String newPath = path + "/" + XMLUtil.getLocalName(el); // all paths begin with '/'
		String textContent = XMLUtil.getText(el);
		if (!textContent.equals("")) {noteVariable(textContent,newPath);count++;}
		
		Vector<Element> children = XMLUtil.childElements(el);
		for (Iterator<Element> it = children.iterator();it.hasNext();)
			{count = count + noteAllVariables(it.next(),newPath);}
				
		NamedNodeMap nn = el.getAttributes();
		for (int i = 0; i < nn.getLength();i++)
		{
			Node n = nn.item(i);
			if (n instanceof Attr)
			{
				Attr att = (Attr)n;
				String attPath = newPath + "/@" + att.getName();
				String val = att.getValue();
				if (!val.equals("")) noteVariable(val,attPath);
				count++;
			}
		}
		return count;
	}
	
	/**
	 * note the XPath to a number variable in a previous example, and increment the highest number found in the previous example
	 * @param text
	 * @param path
	 */
	private void noteVariable(String text, String path)
	{
		// text variable value should always be an Integer; ignore silently if not
		try
		{
			Integer val = new Integer(text);
			int ival = val.intValue();
			previousVariablesByPath.put(path, val);
			if (ival > lowestNewVariableNumber) lowestNewVariableNumber = ival;
		}
		catch (Exception ex) {System.out.println("non-Integer value: " + text);}
	}

	

	
	/**
	 * recursively fill elements of the sample xml instance with distinct data values
	 * @param outFile
	 * @param elDef
	 * @param el
	 */
	private void fillElement(XMLOutputFile outFile,ElementDef elDef,Element el, String path) throws MapperException
	{
		String newPath = path + "/" + XMLUtil.getLocalName(el);
		// expand the element definition if necessary
		ElementDef currentEl = elDef;
		if (!elDef.isExpanded()) currentEl = mappedStructure.getStructureDefinition().typeStructure(elDef.getType());
		if (currentEl == null) currentEl = elDef; // revert if there is no type definition
		if (typeRepeats(elDef) > MAX_TYPE_REPEATS) currentEl = elDef; // revert if too deep recursion

		// if the text content of the Element is mapped to something, add text content
		NodeMappingSet nms = currentEl.getNodeMappingSet();
		boolean fillText =  ((nms != null) && (nms.getPropertyMappings().size() == 1)); 
		
		// the element has no attributes or child elements, add text content
		if ((currentEl.getChildElements().size() == 0) && (currentEl.getAttributeDefs().size() == 0)) fillText = true;
		
		if (fillText)
		{
			Text tCon = outFile.outDoc().createTextNode(nextDataValue(currentEl,newPath));
			el.appendChild(tCon);
		}
				
		// add attributes to the element, with a different value for each attribute
		for (Iterator<AttributeDef> ia = currentEl.getAttributeDefs().iterator();ia.hasNext();)
		{
			AttributeDef ad = ia.next();
			String attPath = newPath + "/@" + ad.getName();
			String value = nextDataValue(ad,attPath);
			try {el.setAttribute(ad.getName(),value);}
			catch (Exception ex)
			{
				WorkBenchUtil.showMessage("Error","Failed to write value '" + value + "' to XML attribute '" 
						+ ad.getName() + "' of element '" + currentEl.getName() + "'. Check invalid characters in XML attribute name.");
			}
		}
		
		// add child elements recursively. If more than one is allowed, add MULTI_REPEAT of them.
		for (Iterator<ElementDef> ie = currentEl.getChildElements().iterator();ie.hasNext();)
		{
			ElementDef childDef = ie.next();
			int numberOfRepeats = 1;
			if (childDef.getMaxMultiplicity() == MaxMult.UNBOUNDED) numberOfRepeats = MULTI_REPEAT;
			for (int repeat = 0; repeat < numberOfRepeats; repeat++)
			{
				Element childEl = outFile.newElement(childDef.getName());
				el.appendChild(childEl);
				fillElement(outFile,childDef,childEl, newPath);				
			}
		}
	}
	
	/**
	 * 
	 * @param elDef
	 * @return true if the element is a text node - i.e if its name without namespace is 'text'
	private boolean isTextNode(ElementDef elDef)
	{
		StringTokenizer st = new StringTokenizer(elDef.getName(),":");
		String localName = "";
		while (st.hasMoreTokens()) localName = st.nextToken();
		if (localName.equals("text")) return true;
		return false;
	}
	 */
	
	/**
	 * 
	 * @param nDef
	 * @param path  - the XPTH to the node - not strictly necessary, as it can be got from the NodeDef -
	 * but hthie path is calculated identically for the old instance and the new
	 * @return another distinct data value, of the correct type
	 */
	private String nextDataValue(NodeDef nDef,String path)
	{
		// if a previous example had a variable at this XPath, reuse that value
		Integer previousValue = previousVariablesByPath.get(path);
		if (previousValue != null) 
		{
			previousValuesUsed++;
			return previousValue.toString();
		}
		
		// if a previous example used up a range of variable numbers, start higher for new variables, to avoid duplicates
		int bigNumber = 11001; // to avoid small number strings
		if (lowestNewVariableNumber > bigNumber) bigNumber = lowestNewVariableNumber + 1;

		String value = "";
		if (isBooleanValue(nDef))
		{
			value = "false";
			booleanIndex++;
		}

		else if (isNumberValue(nDef))
		{
			value = new Integer(bigNumber + numberIndex).toString();
			numberIndex++;
		}
		
		else
		{
			value = "a_" + stringIndex;
			stringIndex++;
		}
		return value;
		
	}
	
	/**
	 * what the hell - make them all numbers
	 * @param nDef
	 * @return
	 */
	private boolean isNumberValue(NodeDef nDef)
	{
		boolean isNumber = true;
		return isNumber;
	}

	private boolean isBooleanValue(NodeDef nDef) 
	{
		boolean isBoolean = false;
		if ((nDef.getType() != null) && (nDef.getType().equals("boolean"))) isBoolean = true;
		if (nDef.getName().equals("refusalInd")) isBoolean = true;
		return isBoolean;
	}
	
	/**
	 * 
	 * @param elDef
	 * @return the number of times the type of this ElementDef has occurred in its ancestors
	 */
	private int typeRepeats(ElementDef elDef)
	{
		int repeats = 0;
		String type = elDef.getType();
		if (type != null)
		{
			EObject current = elDef.eContainer();
			while (current instanceof ElementDef)
			{
				ElementDef parent = (ElementDef)current;
				String pType = parent.getType();
				if ((pType != null)&& (pType.equals(type))) repeats++;
				current = parent.eContainer();
			}
		}
		return repeats;
	}

	/**
	 * add namespace declarations to the root element of the xml instance
	 * @param root
	 */
	private void addNamespaceAttributes(Element root)
	{
		if (mappedStructure.getMappingParameters() != null)
		{
			EList<Namespace> nsList = mappedStructure.getMappingParameters().getNameSpaces();
			if (nsList != null) for (Iterator<Namespace> it = nsList.iterator();it.hasNext();)
			{
				Namespace ns = it.next();
				String prefix = ns.getPrefix();
				String url = ns.getURL();
				if (prefix.equals("")) root.setAttribute("xmlns", url);
				else root.setAttribute("xmlns:" + prefix, url);
			}
		}
	}
	
	//------------------------------------------------------------------------------------------
	//                             Making an XML Schema
	//------------------------------------------------------------------------------------------
	
	
	/* schemas to be included to define all the types that may be used  */
	private String[] includeLocations = 
			{"../dt/infrastructureRoot.xsd",
			"../VocabularySchemas/CDAVocab.xsd",
			"../dt/datatypeflavours.xsd"};

    private String XSDPrefix = "xs";
    
    private Element schemaElement(XMLOutputFile outFile, String elName)
    {
    	return outFile.NSElement(XSDPrefix, elName, XMLUtil.SCHEMAURI);
    }
    
    /**
     * used to ensure elements of the same name get different complex types only if they have
     * different structures beneath them
     */
    private Hashtable<String,ElementDef> complexTypes;
	
	private void writeSchema() throws MapperException
	{
		/* Ask the user for a location */ 
		IFile instanceFile = WorkBenchUtil.makeOutputIFile("Generate XML Schema", "Choose name for schema");
		if (instanceFile != null)
		{
			complexTypes = new Hashtable<String,ElementDef>();

			// make the root element of the schema
			XMLOutputFile outFile = new XMLOutputFile();
			Element root = schemaElement(outFile,"schema");
			root.setAttribute("xmlns:" + XSDPrefix,XMLUtil.SCHEMAURI);
			root.setAttribute("elementFormDefault", "qualified");
			outFile.setTopOut(root);
			
			// if simple messages are in the V3 namespace, add the namespace attribute as target namespace
			if (CDAConverter.SIMPLE_MESSAGE_IN_V3_NAMESPACE) 
			{
				root.setAttribute("xmlns", CDAConverter.V3NAMESPACEURI);
				root.setAttribute("targetNamespace", CDAConverter.V3NAMESPACEURI);
			}

			
			ElementDef rootDef = mappedStructure.getRootElement();
			
			// include datatypes schemas, if any attributes have declared types
			if (attributesHaveTypes(rootDef))
			{
				for (int i = 0; i < includeLocations.length; i++)
				{
					Element includeEl = schemaElement(outFile,"include");
					includeEl.setAttribute("schemaLocation",includeLocations[i]);
					root.appendChild(includeEl);					
				}
			}
			
			// declaration for the root element
			Element declaration = schemaElement(outFile,"element");
			declaration.setAttribute("name", rootDef.getName());
			root.appendChild(declaration);
			
			// make the anonymous complex type of the root element (and recursive descent)
			makeType(outFile,declaration,rootDef,false);
			
			EclipseFileUtil.writeOutputResource(outFile.outDoc(),instanceFile,true);	
			
			// attach the new schema to the mapping set
			String schemaLocation = EclipseFileUtil.getResourceLocation(instanceFile);
			mappedStructure.setStructureURL(schemaLocation);
			FileUtil.saveResource(mappedStructure.eResource());
			
			// open the schema to work out enumerated simple data types
			resolveEnumeratedDataTypes();
			
			WorkBenchUtil.showMessage("Completed","XML schema written");
		}
		else WorkBenchUtil.showMessage("Cancelled","No XML schema written");

	}
	
	/**
	 * 
	 * @param elDef
	 * @return true if any of the AttributeDefs in the tree beneath this 
	 * ElementDef have types other than ""
	 */
	private boolean attributesHaveTypes(ElementDef elDef)
	{
		boolean attsHaveTypes = false;
		for (Iterator<AttributeDef> it = elDef.getAttributeDefs().iterator();it.hasNext();)
			if (!it.next().getType().equals("")) attsHaveTypes = true;
		
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
			if (attributesHaveTypes(it.next())) attsHaveTypes = true;

		return attsHaveTypes;
	}
	
	/**
	 * Recursively make all complex types  for this ElementDef and all others
	 * under it in the tree, and attach them to the top element of the output file
	 * 
	 * @param outFile the output XML file
	 * @param schemaEl the element the type definition is to be attached to
	 * @param elDef the ElementDef the complex type describes
	 * @param makeTypeName if true, give the complex type a name
	 * @return the complex type name, or null if there is none
	 */
	private String makeType(XMLOutputFile outFile, Element schemaEl, ElementDef elDef,boolean makeTypeName) throws MapperException
	{
		// if the element already has a type, use that type name
		String typeName= elDef.getType(); // "" if the element has no pre-defined type
		
		// give the type a new name if one is required, and mark the element with the type
		if ((makeTypeName) && (typeName.equals(""))) 
		{
			typeName = newComplexTypeName(elDef);
			elDef.setType(typeName);
		}
		
		if (typeName == null) throw new MapperException("Null type for element " + elDef.getName());
		
		// if the complex type is new, define it
		if (complexTypes.get(typeName) == null)
		{
			complexTypes.put(typeName, elDef);  //record use of the type name

			Element typeEl = schemaElement(outFile,"complexType");
			if (!typeName.equals("")) typeEl.setAttribute("name", typeName);
			schemaEl.appendChild(typeEl);
			
			// mixed type if the element represents any properties
			if (containsText(elDef)) typeEl.setAttribute("mixed","true");
			
			// describe the child elements of the type
			Element seqEl = schemaElement(outFile,"sequence");
			int children = 0;
			for(Iterator<ElementDef> ie = elDef.getChildElements().iterator();ie.hasNext();)
			{
				children++;
				ElementDef cDef = ie.next();
				Element childEl = schemaElement(outFile,"element");
				childEl.setAttribute("name",nodeNameForSchema(cDef));
				if (cDef.getMaxMultiplicity() == MaxMult.UNBOUNDED)
					childEl.setAttribute("maxOccurs","unbounded");
				if (cDef.getMinMultiplicity() == MinMult.ZERO)
					childEl.setAttribute("minOccurs","0");
				seqEl.appendChild(childEl);
				
				String childTypeName = "";
				
				// Expand the child element definition if necessary
				ElementDef childDef = cDef;
				if ((!cDef.isExpanded()) && (typeRepeats(cDef) < MAX_TYPE_REPEATS)) 
					childDef = mappedStructure.getStructureDefinition().typeStructure(cDef.getType());
				if (childDef == null) childDef = cDef; // revert if there is no type definition

				// if a child element has no child elements and no attributes, give it type 'xs:string'
				if ((childDef.getChildElements().size() == 0) && (childDef.getAttributeDefs().size() == 0))
					childTypeName = XSDPrefix + ":string";
				
				// allow an element with no child nodes to have a string value, and do not recurse
				else if (hasNoChildren(childDef)) childTypeName = XSDPrefix + ":string";
				
				// otherwise, recursively make the type for the child element and all its descendants
				else childTypeName = makeType(outFile, outFile.topOut(),childDef, true);

				childEl.setAttribute("type",childTypeName);				
			}
			if (children > 0) typeEl.appendChild(seqEl);
			
			// describe the attributes of the type
			for (Iterator<AttributeDef> ia = elDef.getAttributeDefs().iterator(); ia.hasNext();)
			{
				AttributeDef attDef = ia.next();
				Element attEl = schemaElement(outFile,"attribute");
				attEl.setAttribute("name", attDef.getName());

				String type = XSDPrefix + ":string";
				if (!attDef.getType().equals("")) type = attDef.getType();
				attEl.setAttribute("type", type);

				if (attDef.getMinMultiplicity() == MinMult.ONE)
					attEl.setAttribute("use", "required");
				typeEl.appendChild(attEl);
				
				// pick up any fixed value constraint from the description of the AttributeDef 
				if (attDef.getDescription().startsWith(MakeITSMappingsAction.FIXED))
					attEl.setAttribute("fixed", attDef.getDescription().substring(MakeITSMappingsAction.FIXED.length()));
			}			
		}		
		return typeName;
	}
	
	/**
	 * @param el
	 * @return node name to be used in the schema - either the name of the ElementDef, or a CDA Name given in its description
	 */
	private String nodeNameForSchema(ElementDef el)
	{
		String nodeName = el.getName();
		String desc = el.getDescription();
		if (desc.startsWith(MakeITSMappingsAction.CDA_NAME)) nodeName = desc.substring(MakeITSMappingsAction.CDA_NAME.length());
		return nodeName;
	}
	
	
	/**
	 * An element must be able to contain text if it has any property mappings
	 * @param elDef
	 * @return
	 */
	private boolean containsText(ElementDef elDef)
	{
		boolean hasText = false;
		if (elDef.getNodeMappingSet() != null)
			hasText = elDef.getNodeMappingSet().getPropertyMappings().size() > 0;
		return hasText;
	}
	
	/**
	 * true if an element has no child nodes
	 * @param elDef
	 * @return
	 */
	private boolean hasNoChildren(ElementDef elDef)
	{
		return ((elDef.getChildElements().size() == 0) && (elDef.getAttributeDefs().size() == 0));
	}
	
	
	/**
	 * 
	 * @param name
	 * @return a complex type name, based on the name, that has not been used before,
	 * or is an exact match for one that has been used before
	 */
	private String newComplexTypeName(ElementDef currentElDef)
	{
		// the first choice name has not been used, or has been used and the type matches
		String name = currentElDef.getName();
		String typeName = name + "_type";
		ElementDef typeElDef = complexTypes.get(typeName);
		boolean alreadyUsed = (typeElDef != null);
		if ((alreadyUsed) && (congruent(currentElDef, typeElDef))) return typeName;
		if (!alreadyUsed) return typeName;

		// test a sequence of possible names, until you find one that is unused or matches
		int index = 1;
		while (alreadyUsed)
		{
			typeName = name + "_type_" + index;
			index++;
			typeElDef = complexTypes.get(typeName);
			alreadyUsed = (typeElDef != null);
			if ((alreadyUsed) && (congruent(currentElDef, typeElDef))) return typeName;
			if (!alreadyUsed) return typeName;
		}
		return typeName; // should be unreachable
	}
	
	/**
	 * @param elDef1
	 * @param elDef2
	 * @return true if the two elements are congruent, i.e can be described by the same complex type
	 */
	private boolean congruent(ElementDef elDef1, ElementDef elDef2)
	{
		// they must have the same name, and the same number of attributes and child elements
		boolean congruent = ((elDef1.getName().equals(elDef2.getName())) 
				&& (elDef1.getAttributeDefs().size() == elDef2.getAttributeDefs().size())
				&& (elDef1.getChildElements().size() == elDef2.getChildElements().size()));
		if (!congruent) return false;

		// check the AttributeDefs match in name and min multiplicity and type and fixed values
		for (Iterator<AttributeDef> it = elDef1.getAttributeDefs().iterator(); it.hasNext();)
		{
			AttributeDef at1 = it.next();
			AttributeDef at2 = elDef2.getNamedAttribute(at1.getName());
			if (at2 == null) return false;
			if (at1.getMinMultiplicity().getValue() != at2.getMinMultiplicity().getValue()) return false;
			if (!at1.getType().equals(at2.getType())) return false; // types are not null
			// if either attribute has a fixed value, they must both have the same fixed value
			if ((at1.getDescription().startsWith(MakeITSMappingsAction.FIXED))||
					(at2.getDescription().startsWith(MakeITSMappingsAction.FIXED)))
			{
				if (!at1.getDescription().equals(at2.getDescription())) return false;
			}
		}

		// check the child ElementDefs match in name and multiplicity and type
		for (Iterator<ElementDef> it = elDef1.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef ed1 = it.next();
			ElementDef ed2 = elDef2.getNamedChildElement(ed1.getName());
			if (ed2 == null) return false;
			if (ed1.getMinMultiplicity().getValue() !=ed2.getMinMultiplicity().getValue()) return false;
			if (ed1.getMaxMultiplicity().getValue() !=ed2.getMaxMultiplicity().getValue()) return false;
			if (!ed1.getType().equals(ed2.getType())) return false; // types are not null
			// if the two elements already have a matching type, there is no need to check further
			if (!ed1.getType().equals("")) return true;
			// if they have no type, recurse further (because types are never expanded, this will not diverge)
			if (!congruent(ed1,ed2)) return false;
		}
		return true;
	}
	
	//------------------------------------------------------------------------------------------
	//                    Open the schema to work out enumerated simple data types
	//------------------------------------------------------------------------------------------
	
	private void resolveEnumeratedDataTypes() throws MapperException
	{
		XSDStructure schemaStructure = (XSDStructure)mappedStructure.getStructureDefinition();
		
		Hashtable<String,Vector<String>>  typeValues = new Hashtable<String,Vector<String>>();
		
		/* iterate over the mapped structure, finding all simple types of AttributeDefs 
		 * or ElementDefs that have property mappings */
		ElementDef root = mappedStructure.getRootElement();
		resolveEnumeratedTypes(root,schemaStructure,typeValues);
		
		// save the values in attribute descriptions in the mapping set
		try {mappedStructure.eResource().save(null);}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
	}
	
	
	/**
	 * 
	 * @param elDef
	 * @param schemaStructure
	 */
	private void resolveEnumeratedTypes(ElementDef elDef,XSDStructure schemaStructure,Hashtable<String,Vector<String>> typeValues)
	{
		for (Iterator<AttributeDef> it = elDef.getAttributeDefs().iterator();it.hasNext();)
		{
			AttributeDef attDef = it.next();
			String type = attDef.getType();
			// get the values only once for each type
			if (typeValues.get(type)== null) 
			{
				try
				{
					Vector<String> values = schemaStructure.getSimpleTypeEnumeratedValues(type);
					if (values.size() > 0)
					{
						// trace("Enumerated values for type " + type + ": " + GenUtil.singleString(values));					
						typeValues.put(type, values);						
					}
				}
				catch (MapperException ex) 
				{
					trace(ex.getMessage());
					typeValues.put(type, new Vector<String>());
				}
			}

			// mark up the AttributeDef in the mapping set, if it has any enumerated values
			Vector<String> values = typeValues.get(type);
			if ((values != null) && (values.size() > 0))
			{
				Vector<String> quotedValues = new Vector<String>();
				for (Iterator<String> ix = values.iterator();ix.hasNext();)
					quotedValues.add("'" + ix.next() + "'");
				attDef.setDescription("Allowed values: " + GenUtil.singleString(quotedValues));
			}
		}
		
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef child = it.next();
			resolveEnumeratedTypes(child,schemaStructure,typeValues);
		}
	}


	
	//------------------------------------------------------------------------------------------
	//                                    Trivia
	//------------------------------------------------------------------------------------------

	
	private void trace(String s)  {if (tracing) System.out.println(s);}
	
	private void errorMessage(String s)
	{
		WorkBenchUtil.showMessage("Error",s);
	}

}
