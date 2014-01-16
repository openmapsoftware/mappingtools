package com.openMap1.mapper.mapping;

import java.util.Vector;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.core.SortableRow;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;

/**
 * An instance of this class is a row in the Mappings View, previous to the definition of the Open Mapping Language. 
 * 
 * There is one row for each:
 * - object mapping
 * - property mapping (fixed or variable)
 * - association end mapping
 * 
 * Note there is no row for an association mapping.
 * @author robert
 *
 */

public class MappingsRow implements SortableRow{
	
	protected Mapping mapping;
	public Mapping mapping() {return mapping;}
	
	private boolean isFixedPropertyMapping = false;
	
	protected FixedPropertyValue fixedPropertyValue;
	
	public static int MAPTYPE = 0;
	public static int MAPPEDCLASS = 1;
	public static int FEATURE = 2;
	public static int PATH = 3;
	public static int BREAK = 4;
	public static int DETAILS = 5;
	
	public static String[] columnTitle = 
		{"Type", "Class", "Feature","Path","Break","Details"};
	
	public static int[] columnWidth = {60,100,150,300,50,400};

	/**
	 * int constants STRING, NUMBER defined in SortableRow which define how
	 * each column is to be sorted
	 */
	public static int[] sortType() 
	{
		int len = columnTitle.length;
		int[] sType = new int[len];
		for (int i = 0; i < len; i++) sType[i] = STRING;
		return sType;
	}
	
	//-----------------------------------------------------------------------------
	//                                   constructor
	//-----------------------------------------------------------------------------
	
	/**
	 * constructor for an object mapping, variable property mapping,
	 * or association end mapping
	 */
	public MappingsRow(Mapping mapping){
		this.mapping = mapping;
	}
	
	/**
	 * constructor for a fixed property mapping
	 * @param fixedPropertyValue
	 */
	public MappingsRow(FixedPropertyValue fixedPropertyValue){
		this.fixedPropertyValue = fixedPropertyValue;
		mapping = (Mapping)fixedPropertyValue.eContainer();
		isFixedPropertyMapping = true;
	}
	
	public String cellContents(int colNumber)
	{
		if (colNumber == MAPTYPE) return mappingType();
		if (colNumber == MAPPEDCLASS) return owningClassName();
		if (colNumber == FEATURE) return featureName();
		if (colNumber == PATH) return path();
		if (colNumber == BREAK) return breakString();
		if (colNumber == DETAILS) 
		{
			if (isFixedPropertyMapping) return fixedPropertyValue.getDetails();
			else return mapping.getDetails();
		}
		return "";
	}
	
	/**
	 * for interface SortableRow
	 */
	public Vector<String> rowVector()
	{
		Vector<String> row = new Vector<String>();
		for (int i = 0; i < columnTitle.length;i++)
			row.add(cellContents(i));
		return row;
	}
	
	private String mappingType()
	{
		String type = "";
		if (mapping instanceof ObjMapping)  type = "object";
		if (mapping instanceof PropMapping)  type = "property";
		if (mapping instanceof AssocEndMapping)  type = "assoc";
		if (isFixedPropertyMapping)  type = "property";
		return type;
	}
	
	/**
	 * @return name of the class owning this feature (or the mapped class)
	 * with subset name in brackets if non-empty
	 */
	private String owningClassName()
	{
		String cName = "";
		if (mapping instanceof ObjMapping)  cName = mapping.labelClassName();
		if (mapping instanceof PropMapping)  cName = mapping.labelClassName();
		// for association end mappings, get the class which is target class of the other end mapping
		if (mapping instanceof AssocEndMapping)
		{
			AssocEndMapping aem = ((AssocEndMapping)mapping).otherEndMapping();
			cName = aem.labelClassName();
		}
		return cName;
	}
	
	/**
	 * @return true if this mapping is to be shown in the mappings view.
	 * i.e false for association ends which are not navigable
	 */
	public boolean isShowable()
	{
		boolean showable = true;
		if (mapping instanceof AssocMapping) showable = false;
		if (mapping instanceof AssocEndMapping) 
		{
			showable = false;
			String role = ((AssocEndMapping)mapping).getMappedRole();
			if ((role != null) && (!role.equals(""))) showable = true;
		}
		return showable;		
	}

	
	private String featureName()
	{
		String name = "";
		if (mapping instanceof ObjMapping)  name = "";
		if (mapping instanceof PropMapping)  name = ((PropMapping)mapping).getMappedProperty();
		if (mapping instanceof AssocEndMapping) 
		{
			AssocEndMapping aem = (AssocEndMapping)mapping;
			name = aem.getMappedRole() + "." + aem.getMappedClass();
			if ((aem.getSubset() != null) && !(aem.getSubset().equals("")))
				name = name + "(" + aem.getSubset() + ")";
		}
		if (isFixedPropertyMapping) name = fixedPropertyValue.getMappedProperty();
		return name;
	}
	
	private String path()
	{
		String path = "";
		try {path = mapping.getStringRootPath();}
		catch(Exception ex) 
			{path = ex.getMessage();System.out.println("Mapping row exception: " + path);}
		return path;
	}
	
	/**
	 * @return a 'B' if this mapping or its parent has a breakpoint; otherwise ''
	 */
	private String breakString()
	{
		String breakString = "";
		if (mapping.isBreakPoint()) breakString = "B";
		if (mapping instanceof AssocEndMapping)
		{
			EObject parent = mapping.eContainer();
			if ((parent instanceof Mapping) && (((Mapping)parent).isBreakPoint())) breakString = "B";
		}
		return breakString;		
	}

}
