package com.openMap1.mapper.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.util.XMLUtil;

/**
 * Class used for noting warnings and errors when compiling a set of
 * mappings into XML writing procedures
 * @author robert
 *
 */
public class CompilationIssue extends TranslationIssue implements SortableRow{
	
	public static int COMPILE_PLACEHOLDER = -1;
	public static int COMPILE_MULTIPLE_CONDITION_VALUES = 0;
	public static int COMPILE_OVER_SPECIFIC_PATH_SPEC = 1;
	public static int COMPILE_SELF_ASSOCIATION = 2;
	public static int COMPILE_CONDITION_VALUE_TYPE = 3;
	public static int COMPILE_GROUPING = 4;
	public static int COMPILE_TEXT_CONTENT_DISALLOWED = 5;
	public static int COMPILE_CROSS_CONDITION = 6;
	public static int COMPILE_DEPENDENT_OBJECT = 7;
	public static int COMPILE_REQUIRED_ASSOCIATION = 8;
	public static int COMPILE_INDEPENDENT_OBJECTS = 9;
	public static int COMPILE_NOT_RECOGNISED = 10;
	public static int COMPILE_ASSOCIATION_MAPPINGS = 11;
	public static int COMPILE_IMPORT_PARAMETER_CLASSES = 12;
		
	private String pathString;
	
	public String pathString() {return pathString;}
	
	private String  problemType;
	
	private String code = "";
	public void setCode(String code) {this.code = code;}
	
	public CompilationIssue(
			int nature, 
			String expected, 
			String actual,
			String problemType,
			Xpth path)
	{
		super(nature,expected,actual);
		this.problemType = problemType;
		pathString = path.stringForm();
	}
	
	/**
	 * constructor to make a CompilationIssue recorded on an
	 * element in a wproc XML file
	 * @param el the Element describing the issue
	 * @throws XpthException
	 */
	public CompilationIssue(Element el) throws XpthException
	{
		super(new Integer(el.getAttribute("nature")).intValue(),
				el.getAttribute("expected"),
				el.getAttribute("actual"));
		problemType = el.getAttribute("problemType");
		pathString = el.getAttribute("path");
		occurrences = 1;
		if (!(el.getAttribute("occurrences").equals("")))
			occurrences = new Integer(el.getAttribute("occurrences")).intValue();
	}

	/**
	 * To save in the WProc XML file on the WProc node, including its number of occurrences
	 * @param doc
	 * @return
	 * @throws XMLException
	 */
	public Element procNode(Document doc) throws XMLException
	{
		Element el = XMLUtil.newElement(doc, "CompilationIssue");
		el.setAttribute("nature", new Integer(nature).toString());
		el.setAttribute("occurrences", new Integer(occurrences).toString());
		el.setAttribute("problemType", problemType);
		el.setAttribute("expected", expected);
		el.setAttribute("actual", actual);
		el.setAttribute("path", pathString);		
		return el;		
	}

	/**
	 * 
	 * @param col column index 0..N
	 * @return the entry for this translation issue in the column col
	 */	
	public String cellContents(int col)
	{
		String cell = "";
		if (col == CODE) cell = code;
		else if (col == TYPE) cell = "Compile";
		else if (col == OCCURRENCES) cell = new Integer(occurrences).toString();
		else if (col == DESCRIPTION) cell = description();
		else if (col == CAUSEORLOCATION) cell = fileName + ":" + pathString;		
		return  cell;
	}
	
	public String description()
	{
		return problemType;
	}
	
	/** key for storing all compilation  issues at a given path,
	 * without duplicates */	
	public String key()
	{
		return description() + nature + pathString;
	}
	


}
