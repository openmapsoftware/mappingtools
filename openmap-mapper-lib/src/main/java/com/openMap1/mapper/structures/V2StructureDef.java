package com.openMap1.mapper.structures;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.openMap1.mapper.converters.V2Converter;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;

import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Define the structure for a Mapped structure, getting 
 * the information from a template file from the 
 * HL7 V2 Messaging WorkBench (MWB).
 * 
 * ElementDefs in this structure have the following annotations:
 * 
 * key = V2NodeType; possible values  = 'Message', 'Segment', 'Field', 'Component', 'SubComponent'
 * key = length; value = the integer length as a String
 * key = V2DataType; value = V2 data type
 * key = HL7Version; value = the V2 version, e.g. 2.5.1
 * key = table; value = the numeric identifier of the V2 table, e.g. 0301
 * 
 * @author robert
 *
 */
public class V2StructureDef implements StructureDefinition{
	
	private Element mwbRoot;
	
	private Hashtable<String,ElementDef> elementsByName = new Hashtable<String,ElementDef>();
	private Hashtable<String,ElementDef> elementsByType = new Hashtable<String,ElementDef>();
	
	private String HL7Version;
	
	private String[] mwbNodeTypes = {"SegGroup","Segment","Field","Component","SubComponent"};

	
	//---------------------------------------------------------------------------------
	//                                  Constructor
	//---------------------------------------------------------------------------------
	
	public V2StructureDef(Element mwbRoot)  throws MapperException
	{
		this.mwbRoot = mwbRoot;
		readMWBFile();
	}

	
	//---------------------------------------------------------------------------------
	//                   Reading the Messaging Workbench Template File
	//---------------------------------------------------------------------------------
	
	private void readMWBFile() throws MapperException
	{
		elementsByName = new Hashtable<String,ElementDef>();
		elementsByType = new Hashtable<String,ElementDef>();
		
		if (mwbRoot.getTagName().equals("HL7v2xConformanceProfile"))
		{
			HL7Version = mwbRoot.getAttribute("HL7Version");
			Element top = XMLUtil.firstNamedChild(mwbRoot, "HL7v2xStaticDef");
			if (top == null)throw new MapperException("Found no 'HL7v2xStaticDef' Element in MWB file");
			
		    // define the root Element of the message
			ElementDef elDef = MapperFactory.eINSTANCE.createElementDef();
			String messageName = top.getAttribute("MsgStructID");
			String messageType = messageName + "_Type";
			elDef.setName(messageName);
			elDef.setType(messageType);
			elDef.setMinMultiplicity(MinMult.ONE);
			elDef.setMaxMultiplicity(MaxMult.ONE);
			elDef.addAnnotation("HL7Version", HL7Version);
			
			// store the top element in Hashtables to support the StructureDefinition interface
			elementsByName.put(messageName, elDef);
			elementsByType.put(messageType, elDef);

			// define the (segment and segment group) children of the root element
			for (int i = 0; i < top.getChildNodes().getLength();i++)
			{
				Node nd = top.getChildNodes().item(i) ;
				// position is not used in segment names, so node position = 0 will do
				if (isV2Node(nd)) 
				{
					// build the subtree of the top node down to the next typed nodes
					ElementDef child = readMWBElement(messageName,elDef,(Element)nd,0,false);
					elDef.getChildElements().add(child);
					
					// build subtrees below the next typed nodes
					readMWBElement(messageName,elDef,(Element)nd,0,true);
				}
			}
		}
		else throw new MapperException("Root element of MWB file has wrong tag name '"
				+ mwbRoot.getTagName() + "'");
	}
	
	/**
	 * @param node a Node in an MWB template file
	 * @return true if this is one of the V2 node types
	 */
	private boolean isV2Node(Node node)
	{
		return ((node instanceof Element) && (GenUtil.inArray(node.getNodeName(), mwbNodeTypes)));
	}
	
	/**
	 * read an Element of an MWB profile file at any level below the message,
	 * and define the ElementDef in the structure. Store the ElementDef in Hashtables
	 * for use by the StructureDef interface
	 * 
	 * @param messageName: the name o the whole message, eg ADT_A01
	 * @param parent the parent ElementDef
	 * @param el the element in the MWB file 
	 * @param nodePos position of this node as a child of its parent
	 * @param topOfSubtree true if this ElementDef is to be stored in the Hashtables
	 * as the top of a structure subtree; false if it is part of some other subtree
	 * @return the ElementDef for this node of the V2 structure
	 */
	private ElementDef readMWBElement(String messageName,ElementDef parent, Element el, int nodePos, boolean topOfSubtree)
	{
		ElementDef elDef = MapperFactory.eINSTANCE.createElementDef();
		// values 'SegGroup', 'Segment','Field', 'Component', 'SubComponent' 
		String V2NodeType = el.getTagName();
		elDef.addAnnotation("V2NodeType", V2NodeType);
		String mwbName = el.getAttribute("Name");
		String mwbLongName = el.getAttribute("LongName");
		// String mwbUsage = el.getAttribute("Usage");
		
		String mwbTable = el.getAttribute("Table");
		if (!mwbTable.equals("")) elDef.addAnnotation("Table", mwbTable);

		String mwbLength = el.getAttribute("Length");
		if (!mwbLength.equals("")) elDef.addAnnotation("Length", mwbLength);
		String mwbDataType = el.getAttribute("Datatype");
		if (!mwbDataType.equals("")) 
		{
			elDef.setType(mwbDataType);
			// offer no editor option to expand the node if it has no child nodes
			if (!hasChildElements(el)) elDef.setExpanded(true);
			elDef.addAnnotation("V2DataType", mwbDataType);
		}

		String mwbMin = el.getAttribute("Min");
		if (mwbMin.equals("0")) elDef.setMinMultiplicity(MinMult.ZERO);
		if (mwbMin.equals("1")) elDef.setMinMultiplicity(MinMult.ONE);

		String mwbMax = el.getAttribute("Max");
		if (mwbMax.equals("1")) elDef.setMaxMultiplicity(MaxMult.ONE);
		if (mwbMax.equals("*")) elDef.setMaxMultiplicity(MaxMult.UNBOUNDED);
		
		// define the Element name, type, and descriptive long name for the different V2 node types
		if (V2NodeType.equals("SegGroup"))
		{
			//Element names for segment groups are prefixed by the message name
			String elementName = messageName + "." + mwbName;
			elDef.setName(elementName);
			elDef.setType(elementName + "_Type");
			elDef.setDescription(mwbLongName);
		}
		else if (V2NodeType.equals("Segment"))
		{
			// Element names for segments are the segment names
			elDef.setName(mwbName);
			elDef.setType(mwbName + "_Type");
			elDef.setDescription(mwbLongName);
		}
		else
		{
			String parentDataType = parent.getAnnotation("V2DataType");
			if (V2NodeType.equals("Field"))
				elDef.setName(parent.getName() + "." + nodePos);
			else if ((parentDataType != null)&&(!(parentDataType.equals(""))))
				elDef.setName(parentDataType + "." + nodePos);
			else System.out.println("Cannot name element for " + V2NodeType
					+ " '" + mwbName + "'");
			elDef.setDescription(mwbName);
		}
		
		// define the children of this element
		int childPos = 1;
		for (int i = 0; i < el.getChildNodes().getLength();i++)
		{
			Node nd = el.getChildNodes().item(i) ;
			if (isV2Node(nd)) 
			{
				// build the subtree of the top node down to the next typed nodes
				if (topOfSubtree|(!hasType(elDef)))
				{
					ElementDef child = readMWBElement(messageName,elDef,(Element)nd,childPos,false);
					elDef.getChildElements().add(child);					
				}
				
				// build subtrees below the next typed nodes
				readMWBElement(messageName,elDef,(Element)nd,childPos,true);
				childPos++;
			}
		}

		// store the element for retrieval in the StructureDef interface
		if ((hasType(elDef)) && topOfSubtree)
		{
			elementsByName.put(elDef.getName(), elDef);
			elementsByType.put(elDef.getType(), elDef);			
		}
		
		return elDef;
	}
	
	/**
	 * @param elDef an ElementDef
	 * @return true if this ElementDef has a type, and so has an entry
	 * in the Hashtable of types and stops the tree of some types ancestor ElementDef
	 */
	private boolean hasType(ElementDef elDef)
	{
		return ((elDef.getType() != null) && (!elDef.getType().equals("")));
	}
	
	/**	 
	 * @param el an Element in an MWB file
	 * @return true if this Element has any child nodes that represent V2 nodes
	 */
	private boolean hasChildElements(Element el)
	{
		boolean hasChildren = false;
		for (int i = 0; i < el.getChildNodes().getLength();i++)
			if (isV2Node(el.getChildNodes().item(i))) hasChildren = true;
		return hasChildren;
	}


	//---------------------------------------------------------------------------------
	//                         Interface StructureDefinition
	//---------------------------------------------------------------------------------

	/**
	 * find the Element and Attribute structure of some named top element (which may have a named
	 * complex type, or a locally defined anonymous type), stopping at the
	 * next complex type definitions it refers to
	 * @param String name the name of the element
	 * @return Element the EObject subtree (Element and Attribute EObjects) defined by the name.
	 * Make a clone because the Editor may do nasty things to it
	 */
	public ElementDef nameStructure(String name) throws MapperException
	{
		ElementDef  result = null;
		EObject eDef = ModelUtil.EClone(elementsByName.get(name),true);
		if (eDef instanceof ElementDef) result = (ElementDef)eDef;
		return result;
	}

	/**
	 * find the Element and Attribute structure of some complex type, stopping at the
	 * next complex type definitions it refers to
	 * @param type the name of the complex type
	 * @return the EObject subtree (Element and Attribute EObjects) defined by the type
	 * Make a clone because the Editor may do nasty things to it.
	 */
	public ElementDef typeStructure(String type) throws MapperException
	{
		ElementDef  result = null;
		EObject eDef = ModelUtil.EClone(elementsByType.get(type),true);
		if (eDef instanceof ElementDef) result = (ElementDef)eDef;
		return result;
	}
	
	/**
	 * 
	 * @return an array of the top-level complex types defined in the structure definition - 
	 * any of which can be the type of a mapping set
	 */
	public String[] topComplexTypes()
	{
		ArrayList<String> allTopTypes = new ArrayList<String>();
		allTopTypes.add(""); // the default choice on the menu, before any choice is made, is ""
		for (Enumeration<String> en = elementsByType.keys(); en.hasMoreElements();)
		{
			String name = en.nextElement();
			StringTokenizer st = new StringTokenizer(name,".");
			// exclude any type names with a '.' in them
			if (st.countTokens() == 1) allTopTypes.add(name);				
		}
		String[] res = new String[allTopTypes.size()];
		return allTopTypes.toArray(res);
	}

	
	/**
	 * 
	 * @return an array of the top-level complex types defined in the structure definition - 
	 * any of which can be the type of a mapping set
	 */
	public String[] topElementNames()
	{
		ArrayList<String> allTopNames = new ArrayList<String>();
		allTopNames.add(""); // the default choice on the menu, before any choice is made, is ""
		for (Enumeration<String> en = elementsByName.keys(); en.hasMoreElements();)
		{
			String name = en.nextElement();
			StringTokenizer st = new StringTokenizer(name,".");
			// exclude all names with a '.' in them
			if (st.countTokens() == 1) allTopNames.add(name);				
		}
		String[] res = new String[allTopNames.size()];
		return allTopNames.toArray(res);
	}


	/**
	 * @return the set of namespaces defined for the structure
	 */
	public NamespaceSet NSSet()
	{
		NamespaceSet nss = new NamespaceSet();
		try {nss.addNamespace(new namespace("",V2Converter.V2_NAMESPACE_URI));}
		catch (Exception ex) {}
		return nss;
	}
	
	//---------------------------------------------------------------------------------
	//                         Interface PropertyValueSupplier
	//---------------------------------------------------------------------------------


	/**
	 * 
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return true if this property value supplier supplies values for the 
	 * model class and property, for drop-down choices in the editor
	 */
	public boolean suppliesPropertyValues(String modelClassName, String modelPropertyName)
	{
		if (modelClassName.equals("MappedStructure"))
		{
			if (modelPropertyName.equals("Top Element Type")) return true;
			if (modelPropertyName.equals("Top Element Name")) return true;
		}
		return false;
	}

	/**
	 * 
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return the values supplied by this supplier for the model class and property
	 */
	public String[] propertyValues(String modelClassName, String modelPropertyName)
	{
		if (modelClassName.equals("MappedStructure"))
		{
			if (modelPropertyName.equals("Top Element Type")) return topComplexTypes();
			if (modelPropertyName.equals("Top Element Name")) return topElementNames();
		}
		return new String[0];
	}


}
