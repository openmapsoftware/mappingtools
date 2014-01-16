package com.openMap1.mapper.mapping;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.SortableRow;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.ConversionImplementation;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.JavaConversionImplementation;
import com.openMap1.mapper.LocalPropertyConversion;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.ModelFilter;
import com.openMap1.mapper.ModelFilterSet;
import com.openMap1.mapper.MultiWay;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ParameterClassValue;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.ValuePair;
import com.openMap1.mapper.XSLTConversionImplementation;

/**
 * An instance of this class is a row in the Mappings View, consistent with the Open Mapping Language definition. 
 * 
 * There is one row for each:
 * - object mapping
 * - property mapping 
 * - association end mapping
 * 
 * There is no row for an association mapping.
 * @author robert
 *
 */

public class OpenMappingRow implements SortableRow{
	
	protected Mapping mapping;
	public Mapping mapping() {return mapping;}
	
	protected ImportMappingSet importSet;
	public ImportMappingSet importSet() {return importSet;}
	
	protected boolean isImport;
		
	// core mapping columns
	public static int MAPTYPE = 0;
	public static int MAPPEDCLASS = 1;
	public static int FEATURE = 2;
	public static int XPATH = 3;
	public static int COMMENTS = 4;
	
	// extended mapping columns
	public static int CONDITION = 5;
	public static int KEY = 6;
	public static int FILTER = 7;
	public static int APEX = 8;
	public static int CONVERT_IN = 9;
	public static int CONVERT_OUT = 10;
	public static int MODULE = 11;
	public static int ALTERNATES = 12;
	
	public static String[] columnTitle = 
		{"Type", "Class", "Feature","XPath","Comments",
			"Condition","Key","Filter","Apex",
			"Convert_In","Convert_Out","Module","Alternates"};
	
	public static int[] columnWidth = 
		{60,100,150,300,100,
			200,100,100,200,
			100,100,300,60};

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
	 * constructor for an object mapping, property mapping,
	 * or association end mapping
	 */
	public OpenMappingRow(Mapping mapping){
		this.mapping = mapping;
		isImport = false;
	}

	
	/**
	 * constructor for a call or macro import of a mapping set
	 */
	public OpenMappingRow(ImportMappingSet importSet){
		this.importSet = importSet;
		isImport = true;
	}

	
	public String cellContents(int colNumber)
	{
		if (colNumber == MAPTYPE) return mappingType();
		if (colNumber == MAPPEDCLASS) return owningClassName();
		if (colNumber == FEATURE) return featureName();
		if (colNumber == XPATH) return path();
		if (colNumber == COMMENTS) return comments();
		if (colNumber == CONDITION) return condition();
		if (colNumber == KEY) return key();
		if (colNumber == FILTER) return filter();
		if (colNumber == APEX) return apex();
		if (colNumber == CONVERT_IN) return convert(true);
		if (colNumber == CONVERT_OUT) return convert(false);
		if (colNumber == MODULE) return module();
		if (colNumber == ALTERNATES) return alternates();
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
	
	
	/**
	 * @return true if this mapping is to be shown in the mappings view.
	 * now true for association ends which are not navigable
	 */
	public boolean isShowable()
	{
		boolean showable = true;
		
		if (isImport) {} // import rows are always showable

		// never show association mappings
		else if (mapping instanceof AssocMapping) showable = false;
		
		// legacy code - fixed to return true 
		else if (mapping instanceof AssocEndMapping) 
		{
			showable = true; // now both association end mappings always show
			String role = ((AssocEndMapping)mapping).getMappedRole();
			if ((role != null) && (!role.equals(""))) showable = true;
		}
		return showable;		
	}


	
	//------------------------------------------------------------------------------------------
	//                           Methods to compute column contents
	//------------------------------------------------------------------------------------------
	
	/**
	 * @return content of 'Type' column. Type is preceded by '*' if this mapping is a break
	 */
	private String mappingType()
	{
		String type = "";

		// do not  yet support macro import mapping sets
		if (isImport) type = "call";
		
		// types of mappings
		else if (mapping instanceof ObjMapping)  type = "object";
		else if (mapping instanceof PropMapping)  type = "attrib";
		else if (mapping instanceof AssocEndMapping)  type = "assoc";

		if (isBreak()) type = "*" + type;
		return type;
	}
	
	/**
	 * @return true if this mapping or its parent association mapping has a breakpoint; otherwise false
	 */
	private boolean isBreak()
	{
		boolean isBreak = false;
		if (isImport) {}
		else if (mapping.isBreakPoint()) isBreak = true;
		else if (mapping instanceof AssocEndMapping)
		{
			EObject parent = mapping.eContainer();
			if ((parent instanceof Mapping) && (((Mapping)parent).isBreakPoint())) isBreak = true;
		}
		return isBreak;		
	}

	
	/**
	 * 'Class' column
	 * @return name of the class owning this feature (or the mapped class)
	 * with subset name in brackets if non-empty
	 */
	private String owningClassName()
	{
		String cName = "";
		// for imports, define the class and subset
		if (isImport) 
		{
			boolean foundOne = false;
			for (Iterator<ParameterClassValue> it = importSet.getParameterClassValues().iterator();it.hasNext();)
			{
				ParameterClassValue pcv = it.next();
				if (foundOne) cName = cName + "; "; // should not occur as we now only allow one parameter class
				cName = cName + pcv.getMappedPackage() + "." + pcv.getMappedClass();
				String subset = pcv.getSubset();
				if (!subset.equals("")) cName = cName + "(" + subset + ")";
				foundOne = true;
			}
		}
		
		else if (mapping instanceof ObjMapping)  cName = mapping.labelClassName();
		else if (mapping instanceof PropMapping)  cName = mapping.labelClassName();
		// for association end mappings, get the class which is target class of the other end mapping
		if (mapping instanceof AssocEndMapping)
		{
			AssocEndMapping aem = ((AssocEndMapping)mapping).otherEndMapping();
			cName = aem.labelClassName();
		}
		return cName;
	}
	
	/**
	 * @return content of the 'Feature' column
	 */
	private String featureName()
	{
		String name = "";
		if (isImport) {}

		else if (mapping instanceof ObjMapping)  name = "";
		else if (mapping instanceof PropMapping)  name = ((PropMapping)mapping).getMappedProperty();
		else if (mapping instanceof AssocEndMapping) 
		{
			AssocEndMapping aem = (AssocEndMapping)mapping;
			
			String role = aem.getMappedRole();
			// if there is no role, identify the other end mapping, to pair up the ends unambiguously
			if (role.equals("")) 
			{
				AssocEndMapping opposite = aem.otherEndMapping();
				role = "(-" + opposite.getMappedRole() +  ")";
			}
			
			name = role + "." + aem.getMappedClass();
			if ((aem.getSubset() != null) && !(aem.getSubset().equals("")))
				name = name + "(" + aem.getSubset() + ")";
		}
		return name;
	}
	
	/**
	 * @return content of the 'XPath' column
	 */
	private String path()
	{
		String path = "";
		// for an Import Mapping set, get the XPath of the importing node
		if (isImport) 
		{
			ElementDef importingEl = (ElementDef)importSet.eContainer();
			path = importingEl.getPath();
		}
		// for mappings, the XPath of the mapped node
		else
		{
			try {path = mapping.getStringRootPath();}
			catch(Exception ex) 
				{path = ex.getMessage();System.out.println("Mapping row exception: " + path);}			
		}
		return path;
	}
	
	/**
	 * 
	 * @return content of the 'Comments' column
	 */
	private String comments()
	{
		String comments = "";
		if (isImport) {}
		else comments = mapping.getDescription();
		if (comments == null) comments = ""; // there were some nulls coming through, but now fixed in the mapper package
		return comments;
	}
	
	/**
	 * 
	 * @return content of the 'Condition' column
	 */
	private String condition()
	{
		String conds = "";
		if (isImport) {}
		else
		{
			boolean foundOne = false;
			for (Iterator<MappingCondition> it = mapping.getMappingConditions().iterator();it.hasNext();)
			{
				MappingCondition mc = it.next();
				String  cond = mc.getDetails();
				if (foundOne) conds = conds + "; ";
				conds = conds + cond;
				foundOne = true;
			}			
		}
		return conds;
	}
	
	/**
	 * 
	 * @return content of the 'Filter' column
	 */
	private String filter()
	{
		String filter = "";
		boolean foundOne = false;
		if (isImport) {}
		else if (mapping instanceof ObjMapping)
		{
			// filters representing fixed property values
			for (Iterator<FixedPropertyValue> it = ((ObjMapping)mapping).getFixedPropertyValues().iterator();it.hasNext();)
			{
				FixedPropertyValue fpv = it.next(); 
				if (foundOne) filter = filter + "; ";
				filter = filter + fpv.getDetails();
				foundOne = true;
			}
			
			// FIXME: filters from association mappings required for the object (harder to implement, so not yet done
			
			// filters which were stated as filters
			ModelFilterSet mfs = ((ObjMapping)mapping).getModelFilterSet();
			if (mfs != null)
			{
				for (Iterator<ModelFilter> it = mfs.getModelFilters().iterator();it.hasNext();)
				{
					ModelFilter mf = it.next();
					if (foundOne) filter = filter + "; ";
					filter = filter + mf.getFilterColumnText();
					foundOne = true;
				}
			}
		}
		return filter;
	}
	
	/**
	 * FIXME - keys  for multiply represented objects are not yet in the mapper metamodel
	 * @return content of the 'Key' column
	 */
	private String key()
	{
		String key = "";
		if (isImport) {}
		else if (mapping instanceof ObjMapping)
		{
			ObjMapping om = (ObjMapping)mapping;
			if (om.isMultiplyRepresented())
			{
				
			}
		}
		return key;
	}
	
	/**
	 * temporary version which calculates the Apex from a cross-path.
	 * For property mappings, this is the object-to-property path.
	 * For association nodes, there is a choice of two paths: object to association,
	 * or the reverse.  They should have the same apex.
	 * I arbitrarily choose object to association for this case
	 * @return content of the 'Apex' column
	 */
	private String apex()
	{
		String apexPath = "";
		if (isImport) {}
		else try
		{
			//check if any non-default cross path has been specified
			Xpth crossPath = null;
			if (mapping instanceof PropMapping)
			{
				PropMapping pm = (PropMapping)mapping;
				if (!pm.getObjectToPropertyPath().equals("")) crossPath = pm.getObjectToPropertyXPath();
			}			
			else if (mapping instanceof AssocEndMapping)
			{
				AssocEndMapping am = (AssocEndMapping)mapping;
				if (!am.getObjectToAssociationPath().equals("")) crossPath = am.getAssociationToObjectXPath();
			}
			
			// if some cross path has been specified
			if (crossPath != null)
			{
				// find the node name for the apex step of the path (could it be 'node()'? No)
				String apexName = crossPath.step(crossPath.apexIndex()).nodeTest();
				// in theory this path might be ambiguous if the node name repeats, but use it anyway
				apexPath = "//" + apexName;
			}
		}
		catch (MapperException ex) {} // don't fuss about XPath exceptions
		return apexPath;
	}
	
	/**
	 * 
	 * @return content of the 'Convert_In' and 'Convert_Out' columns
	 */
	private String convert(boolean inOut)
	{
		String convert = "";
		if (isImport) {}
		else if (mapping instanceof PropMapping)
		{
			PropMapping pm = (PropMapping)mapping;
			LocalPropertyConversion lpc = pm.getLocalPropertyConversion();
			if (lpc != null)
			{
				boolean foundOne = false;
				EList<ConversionImplementation> ciList = null;
				if (inOut) ciList = lpc.getInConversionImplementations();
				else ciList = lpc.getOutConversionImplementations();
				if ((ciList != null) && (ciList.size() > 0))
				{
					for (Iterator<ConversionImplementation> it = ciList.iterator();it.hasNext();)
					{
						ConversionImplementation ci = it.next();
						if (foundOne) convert = convert + "; ";
						if (ci instanceof JavaConversionImplementation)
						{
							JavaConversionImplementation jci = (JavaConversionImplementation)ci;
							convert = convert + "Java " +
								jci.getPackageName() + "." +
								jci.getClassName() + "." +
								jci.getMethodName();
						}
						else if (ci instanceof XSLTConversionImplementation)
						{
							XSLTConversionImplementation xci = (XSLTConversionImplementation)ci;
							convert = convert + "XSLT template " + xci.getTemplateName() + 
								" at " + xci.getTemplateFileURI();
						}
						foundOne = true;
					}
				}
				// if no conversions have been found, look for a local table of value pairs
				else if (inOut)
				{
					EList<ValuePair> vpl = lpc.getValuePairs();
					if ((vpl != null) && (vpl.size() > 0))
						for (Iterator<ValuePair> ip = vpl.iterator();ip.hasNext();)
						{
							ValuePair vp = ip.next();
							if (foundOne) convert = convert + ", ";
							convert = convert + "[" + vp.getStructureValue() + "," + vp.getModelValue() + "]";
							foundOne = true;
						}
				}
			}
		}
		return convert;
	}
	
	/**
	 * @return content of the 'Module' column
	 */
	private String module()
	{
		String module = "";
		if (isImport)
		{
			module = importSet.getMappingSetURI();
		}
		return module;
	}
	
	/**
	 * @return content of the 'Alternates' column
	 */
	private String alternates()
	{
		String alternates = "";
		if (isImport) {}
		else
		{
			if (mapping.getMultiWay() == MultiWay.REDUNDANT) alternates = "all";
			if (mapping.getMultiWay() == MultiWay.CHOICE) alternates = "some";
		}
		return alternates;
	}

}
