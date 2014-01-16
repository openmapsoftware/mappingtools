package com.openMap1.mapper.mapping;

import org.w3c.dom.Node;

import com.openMap1.mapper.core.MapperException;

/**
 * A row of the Debug View.
 * 
 * @author robert
 *
 */
public class DebugRow {
	
	public String mappingTypeString() {return mappingTypeString[mappingType];}
	private String[] mappingTypeString = {"object","property","assoc",""};
	
	public int mappingType() {return mappingType;}
	protected int mappingType = 3; // unless altered, gives an empty field from mappingTypeString[3]
	
	public int index() {return index;}
	private int index;
	
	public String className() {return className;}
	private String className = "";

	public String feature() {return feature;}
	private String feature = "";


	public String description() {return description;}
	private String description;
	
	public String result() {return result;}
	private String result = "";
	
	public static int ROWINDEX = 0;
	public static int MAPPING_TYPE = 1;
	public static int CLASSNAME = 2;
	public static int FEATURE = 3;
	public static int DESCRIPTION = 4;
	public static int RESULT = 5;
	
	// types of XPath to display
	public static int TO_OBJECT = 0;
	public static int OBJECT_TO_PROPERTY = 1;
	public static int OBJECT_TO_ASSOCIATION = 2;
	public static int ASSOCIATION_TO_OBJECT = 3;
	
	// short description of type of XPath
	private static String[] xPathTypeString = {"[=>O]","[O=>P]","[O=>A]","[A=>O]"};
	
	// types of DebugRow with only a description and possibly a result
	public static int RETURN_OBJECTS = 0;
	public static int FOUND_ASSOCIATION_NODES = 1;
	public static int COMPLETED = 2;
	public static int TERMINATED = 3;
	
	// description field for these rows
	private static String[] rowDescription = 
		{"Return objects",
		 "Found association nodes",
		"DEBUG RUN COMPLETED",
		"DEBUG RUN TERMINATED"};

	
	public static String[] columnTitle =  {"Step", "Type", "Class","Feature","To do", "Result"};
	
	public static int[] columnWidth = {60,80,100,100,300,300};
	
	/**
	 * Vanilla constructor
	 * @param index
	 * @param mappingType
	 * @param className
	 * @param feature
	 * @param description
	 * @param result
	 */
	public DebugRow(int mappingType, String className, String feature, String description, String result)
	{
		this.mappingType = mappingType;
		this.className = className;
		this.feature = feature;
		this.description = description;
		this.result = result;
	}
	
	/**
	 * constructor for a break on a mapping
	 * @param m the mapping
	 * @param xPathType see static constants above
	 * @param isIndexed true if the reader can use a node index rather than an XPath
	 */
	public DebugRow(MappingTwo m, int xPathType, boolean isIndexed) throws MapperException
	{
		mappingType = m.mappingType();
		className = m.cSet().prettyForm(); // show subsets
		feature = "";
		description = "XPath " + xPathTypeString[xPathType] + ": ";
		if (isIndexed) description = "Indexed " + description;
		try
		{
			if (m instanceof objectMapping)
			{
				if (xPathType != TO_OBJECT) {throw new MapperException("Bad DebugRow constructor for object mapping");}
				objectMapping om = (objectMapping)m;
				description = description + om.nodePath().stringForm();
			}
			else if (m instanceof propertyMapping)
			{
				if (xPathType != OBJECT_TO_PROPERTY) {throw new MapperException("Bad DebugRow constructor for property maping");}
				propertyMapping pm = (propertyMapping)m;
				feature = pm.propertyName();
				description = description + pm.objectToProperty().stringForm();
				if (pm.fixed()) description = "Fixed property mapping";
			}
			else if (m instanceof associationEndMapping)
			{
				associationEndMapping am = (associationEndMapping)m;
				if (xPathType == OBJECT_TO_ASSOCIATION) 
				{
					associationEndMapping otherEnd = am.getOtherEndMapping();
					// write out the class and feature name only on the first leg
					feature = otherEnd.roleName() + "." + otherEnd.cSet().prettyForm();
					description = description + am.objToAssoc().stringForm();
				}
				else if (xPathType == ASSOCIATION_TO_OBJECT)
				{
					// write out no mapping type, class or feature name for the second leg
					mappingType = 3;
					className = "";
					description = description + am.assocToObj().stringForm(); 					
				}
				else {throw new MapperException("Bad DebugRow constructor for association mapping");}
			}
		}
		catch (MapperException ex)	{System.out.println(ex.getMessage());}		
	}
	
	/**
	 * Constructor for a break to test a node against one or more conditions
	 * @param node
	 * @param nConditions
	 */
	public DebugRow (Node node, int nodeNumber, int nodes, int nConditions)
	{
		String end = " condition";
		if (nConditions > 1) end = " conditions";
		String nodePos = "";
		if (nodes > 1) nodePos = " " + (nodeNumber + 1) + " of " + nodes;
		description = "Test node" + nodePos + " '" + node.getNodeName() + "' against "  + nConditions + end;
	}
	
	/**
	 * Constructor for a break to test one condition on a node
	 * @param node
	 * @param conditionNo
	 * @param cond
	 */
	public DebugRow(Node node, int conditionNo, Condition cond)
	{
		description = "Test condition " + (conditionNo + 1) + ": " + cond.mappingCondition().getDetails();
	}
	
	/**
	 * Constructor for computing a property value from a node
	 * @param m
	 * @param node
	 */
	public DebugRow(MappingTwo m, Node node)
	{
		description = "Compute property value from node '" + node.getNodeName() + "'";
	}
	
	/**
	 * Constructor for rows with nothing but a description and possibly a result
	 * - returning objects 
	 * - finding association nodes
	 * - completion of the debug run
	 * - termination of the debug run
	 */
	public DebugRow(int rowType)
	{
		description = rowDescription[rowType];
	}
	
	public String cellContents(int colNumber)
	{
		if (colNumber == ROWINDEX) return new Integer(index).toString();
		if (colNumber == MAPPING_TYPE) return mappingTypeString[mappingType];
		if (colNumber == CLASSNAME) return className;
		if (colNumber == FEATURE) return feature;
		if (colNumber == DESCRIPTION) return description;
		if (colNumber == RESULT) return result;
		return "";
	}
	
	public void setResult(String result) {this.result = result;}
	
	public void setIndex(int index) {this.index = index;}


}
