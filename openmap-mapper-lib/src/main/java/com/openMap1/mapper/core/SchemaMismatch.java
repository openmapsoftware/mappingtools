package com.openMap1.mapper.core;

import org.eclipse.emf.common.util.Diagnostic;

import java.util.StringTokenizer;
/**
 * Class for recording discrepancies between an XML instance
 * and the structure definition in the mapped structure of 
 * which it is supposed to be an instance, as detected
 * by XML schema validation
 * 
 * This class wraps org.eclipse.emf.ecore.util.Diagnostic, 
 * so that it can be displayed in the Translation Issues view

 * @author robert
 *
 */
public class SchemaMismatch extends TranslationIssue {
	
	public static int SCHEMA_VALIDATION_ERROR = 0;
	public static int SCHEMA_VALIDATION_INFO = 1;
	
	private Diagnostic diagnostic;
	public Diagnostic diagnostic() {return diagnostic;}
	
	protected String[] structureIssue = {
			"Validation Error",
			"Validation Info"
	};
	
	protected String structureIssue(int nature) {return structureIssue[nature];}
	
	/**
	 * path to the node where this mismatch was found; cannot yet extract from Diagnostic
	 */ 
	public String pathString() {return pathString;}
	protected String pathString;
	
	/**
	 * @param pathString path to the bad node
	 * @param nature nature of the problem - unexpected name, missing, or repeated
	 * @param expected expected value of some property
	 * @param actual actual wrong value of that property
	 */
	public SchemaMismatch(Diagnostic diagnostic, int nature)
	{
		super(nature,"","");
		this.diagnostic = diagnostic;
		pathString = "";
	}
	
	/**
	 * @return description of the mismatch
	 */
	public String description()
	{
		String description = tidyUp(diagnostic.getMessage());
		return description;
	}
	
	// Strings to remove from the diagnostic message
	private String[] toRemove = {"org.eclipse.emf.ecore.impl.DynamicEObjectImpl/"};
	
	/**
	 * tidy up an XML schema diagnostic so that
	 * (1) It does not contain Strings like '@197886c' 
	 * which are not reproducible from one run to the next
	 * (because these strings screw up the text comparison in JUnit regression tests)
	 * (2) It does not contain some long, un-informative Strings
	 * @param message the raw diagnostic message
	 * @return the tidied up message
	 */
	private String tidyUp(String message)
	{
		// remove the first '@' and all characters between it and the first '{'
		StringTokenizer atBreak = new StringTokenizer(message,"@{",true);
		String reproducible = "";
		boolean bracketFound = false;
		boolean start = true;
		while (atBreak.hasMoreTokens())
		{
			String next = atBreak.nextToken();
			// accept the substring before the first '@' or bracket
			if (start) {reproducible = reproducible + next; start = false;}
			// accept nothing else before the first bracket; accept it and everything after it
			if (next.equals("{")) bracketFound = true;
			if (bracketFound) reproducible = reproducible + next;
		}
		
		// remove some long boring strings, which start on distinctive characters ("'")
		String tidy = "";
		StringTokenizer boring = new StringTokenizer(reproducible,"'",true); // retain separators as tokens
		while (boring.hasMoreTokens())
		{
			String next = boring.nextToken();
			for (int i = 0; i < toRemove.length; i++)
			{
				if (next.startsWith(toRemove[i])) next = next.substring(toRemove[i].length());
				tidy = tidy + next;
			}
		}
		return tidy;
	}

	
	/**
	 * @param col column index 0..N
	 * @return the entry for this translation issue in the column col
	 */
	public String cellContents(int col)
	{
		String cell = "";
		if (col == CODE) cell = fileName;
		else if (col == TYPE) cell = "XML Schema";
		else if (col == DESCRIPTION) cell = description();
		else if (col == CAUSEORLOCATION) cell = pathString();
		return  cell;
	}

}

