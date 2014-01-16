package com.openMap1.mapper.health.cda;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.GenUtil;

public class OCLExpression {
	
	private boolean tracing;
	private void trace(String s) {if (tracing) System.out.println(s);}
	
	private Vector<String[]> constraintPaths;
	public Vector<String[]> constraintPaths() {return constraintPaths;}
	
	private String className;
	
	private String packageName;
	
	private String constraintName;
	
	private Vector<String> templatedPackages;
	
	private String OCLText;
	public String OCLText() {return OCLText;}
	
	private String[] selectors = {"select","exists","one","forAll","implies"};
	
	private String[] connectors = {"or","xor","and","not","implies","true","false","let","in"};
	
	private String[] giveUpWords = {"let"};
	
	private String[] ignorableStarts = {"first","asSequence","oclAsType","oclIsKindOf","size","isEmpty","oclIsUndefined"};
	
	/**
	 * some OCL constraints are defined by methods rather than paths from the root node.
	 * In those cases , the method follows a path from the root node, and this array defines the path.
	 */
	private String[][] methodPaths = {
			{"all","getAllSections","component.structuredBody.component.section"},
			{"all","getSections","component.structuredBody.component.section"},
			{"all","getSection","ancestor::section"},
			{"all","getEntryTargets","entry.clinicalStatement"},
			{"all","getEntryRelationshipTargets","entryRelationship.clinicalStatement"},
			{"all","getSubstanceAdministrations","entryOrEntryRelationship.substanceAdministration"},
			{"all","getSupplies","entryOrEntryRelationship.supply"},
			{"all","getObservations","entryOrEntryRelationship.observation"},
			{"all","getActs","entryOrEntryRelationship.act"},
			{"all","getOrganizers","entryOrEntryRelationship.organizer"},
			{"all","getEncounters","entryOrEntryRelationship.encounter"},
			{"all","getProcedures","entryOrEntryRelationship.procedure"},
			{"MedicationActivity","getParticipantRoles","consumable.manufacturedProduct"}
	};
	
	/* In the array methodPaths above, the value 'clinicalStatement' stands for any of the following: */
	public static String[] CLINICAL_STATEMENT_VALUES = 
		{"observation","regionOfInterest","observationMedia","substanceAdministration",
		"supply","procedure","encounter","act","organizer"};
	
	
	//-----------------------------------------------------------------------------------------------------------------
	//                               Constructor
	//-----------------------------------------------------------------------------------------------------------------
	
	public OCLExpression(String packageName,String className, 
			Vector<String> templatedPackages, String OCLText, String constraintName, boolean tracing) throws MapperException
	{
		this.packageName = packageName;
		this.className = className;
		this.templatedPackages = templatedPackages;
		this.OCLText = GenUtil.replaceLineBreaksBySpaces(OCLText);
		this.constraintName = constraintName;
		this.tracing = tracing;
		
		if (parseThisOCL()) constraintPaths = getAllConstraints();
	}
	
	/**
	 * 
	 * @return false if for various reasons I don't want to parse the OCL,
	 * or true if none of those conditions apply, and 
	 * this OCL constraint mentions any class in a templated package of the model - e.g 'ccd'
	 */
	public boolean parseThisOCL()
	{
		if (OCLText.contains(" let ")) return false; // these introduce variables and are to complex to handle
		if (OCLText.contains("getSections(")) return false; // these have very long paths to poke down into section templates
		return mentionsClassInTemplatedPackage(OCLText);
	}
	
	
	/**
	 * @return true if the text mentions any class in a templated package of the model - e.g 'ccd'
	 */
	private boolean mentionsClassInTemplatedPackage(String text)
	{
		boolean mentions = false;
		for (int i = 0; i < templatedPackages.size(); i++)
		{
			String packName = templatedPackages.get(i);
			if (text.contains(packName + "::")) mentions = true;
		}
		return mentions;
	}
	
	/**
	 * to consume strings like 'self.participant->one(partic : cda::Participant2 | partic.oclIsKindOf(ccd::PatientAwareness))'
	 * and other cases dealt with more or less messily
	 */
	public Vector<String[]> getAllConstraints() throws MapperException
	{
		Vector<String[]> constraints = new Vector<String[]>();
		trace("OCL Text: " + OCLText);
		String pString = parseBrackets(OCLText); // replace the contents of any (..)  by ($key) to look up
		String path = "";
		readDeBracketedExpression(path, pString, constraints);
		
		return constraints;
	}
	
	/**
	 * 
	 * @param pString
	 * @param constraints
	 * @throws MapperException
	 */
	private void readDeBracketedExpression(String path, String pString, Vector<String[]> constraints) throws MapperException
	{
		trace("Reading debracketed: " + pString);
		// consume de-bracketed expression like 'self.getSubstanceAdministrations($k1)->exists($k3) or self.getSupplies($k4)->exists($k6)'
		StringTokenizer s1 = new StringTokenizer(pString,"-> ="); // spaces and OCL separators outside brackets
		while (s1.hasMoreTokens())
		{
			String phrase = s1.nextToken();

			if (phrase.startsWith("self.")) path = getSelfPath(phrase, path,constraints);

			else if (GenUtil.inArray(phrase, connectors)) {path = "";} // ignore 'or', 'and', 'implies' etc., but reset the path 
			
			else if (GenUtil.inArray(phrase, giveUpWords)) {return;} // Don't try to parse OCL containing 'let' 
			
			else if (hasIgnorableStart(phrase)) {}

			else if (isInteger(phrase))  {}// ignore an integer value from 'size = 1'

			else if (hasSelectorStart(phrase)) {handleSelector(path, getBracketKey(phrase),constraints);}
			
			else if ((phrase.startsWith("($")) && (phrase.endsWith(")")))  // plain brackets with a key in them
			{
				String contents = fragments.get(getBracketKey(phrase));
				readDeBracketedExpression(path,contents,constraints);
			}

			else throw new MapperException("OCL phrase starts with '" + phrase + "'");
		}
	}
	
	/**
	 * 
	 * @param first
	 * @return
	 */
	private boolean hasIgnorableStart(String first)
	{
		boolean isIgnorable = false;
		for (int i = 0; i < ignorableStarts.length;i++)
		{
			if (first.startsWith(ignorableStarts[i])) isIgnorable = true;
			if (first.startsWith("." + ignorableStarts[i])) isIgnorable = true;
		}
		return isIgnorable;
	}
	
	/**
	 * 
	 * @param first
	 * @return
	 */
	private boolean hasSelectorStart(String first)
	{
		boolean isSelector = false;
		for (int i = 0; i < selectors.length;i++)
		{
			if (first.startsWith(selectors[i])) isSelector = true;
			if (first.startsWith("." + selectors[i])) isSelector = true;
		}
		return isSelector;
	}
	
	/**
	 * 
	 * @param first a string like 'one($k5)' or just '($k5)'
	 * @return the key to the bracket contents, e.g '$k5'
	 */
	private String getBracketKey(String first)
	{
		String key = "";
		StringTokenizer st = new StringTokenizer(first,"()");
		key = st.nextToken();
		if (st.hasMoreTokens()) key = st.nextToken();
		return key;
	}

	
	/**
	 * consume any substring starting with 'self.' and return the path following the 'self.'.
	 * Also, if there is anything interesting in a final bracket, use it to make constraints 
	 * @param selfString
	 * @param path
	 * @param constraints
	 */
	private String getSelfPath(String selfString, String pathStart, Vector<String[]> constraints) throws MapperException
	{
		trace("Handling 'self' string '" + selfString + "'");
		StringTokenizer st = new StringTokenizer(selfString,"()");
		String path = st.nextToken().substring(5);  // remove "self."
		if (!pathStart.equals("")) path = pathStart + "." + path;
		if (st.hasMoreTokens()) // path is a method ending in '(..)' which is replaced by ($k5); look up the real path
		{
			path = lookupPath(path);
			String inBracket = fragments.get(st.nextToken()); // may be empty, or may contain a constrained class name
			// deduce constraints from method arguments
			if (mentionsClassInTemplatedPackage(inBracket)) addMethodArgumentConstraint(constraints, inBracket,path);
		}
		return path;		
	}
	
	/**
	 * 
	 * @param method
	 * @return the path used by that method to return a node
	 * @throws MapperException
	 */
	private String lookupPath(String method)  throws MapperException
	{
		trace("Looking up path '" + method + "'");
		// some things which are not method names
		for (int i = 0; i < this.ignorableStarts.length; i++) 
		{
			String ignorable = ignorableStarts[i];
			if (method.endsWith(ignorable)) return method.substring(0,method.length() - ignorable.length());
		}

		// normal method name lookup
		String path = "";
		int priority = 0;
		for (int i = 0; i < methodPaths.length;i++)
		{
			String [] trial = methodPaths[i];
			// matches specific to the class  - apply whenever found
			if (trial[0].equals(className))
			{
				if (trial[1].equals(method))
				{
					path = trial[2];
					priority = 2;
				}				
			}
			// general matches - apply only if no class-specific match has been found
			else if ((trial[0].equals("all")) && (priority == 0))
			{
				if (trial[1].equals(method))
				{
					path = trial[2];
					priority = 1;
				}				
			}
		}
		if (priority == 0) throw new MapperException("No path supplied for method '"
		+ method + "' in class " + className + " of package " + packageName);
		trace("Looked up path '" + path + "'");
		return path;
	}
	

	
	/**
	 * 
	 * @param path
	 * @param bracketKey
	 * @param constraints
	 */
	private void handleSelector(String path, String bracketKey,Vector<String[]> constraints) throws MapperException
	{
		String inBracket = fragments.get(bracketKey); // contents of bracket after selector replaced by a key
		if (inBracket == null) throw new MapperException("No bracket contents after selector keyword");
		if (path.equals("")) trace("Empty path for selector bracket contents '" + inBracket + "'");
		else
		{
			Vector<String[]> cons = getConstraints(path,inBracket);
			for (int i = 0; i < cons.size(); i++) constraints.add(cons.get(i));					
		}
	}
	
	/**
	 * process  a part of an OCL expression like 'self.getEntryRelationshipTargets(vocab::x_ActRelationshipEntryRelationship::SUBJ, ccd::ProblemAct)'
	 * where one of the arguments of the method defines a constrained class
	 * @param constraints a Vector of constraints to be added to
	 * @param inBracket the contents of the bracket defining the function arguments
	 * @param path the path deduced from the function name
	 * add one or more String arrays of 4 elements: 
	 * element 0 is a path from the owning node
	 * element 1 is the package of the constrained class
	 * element 2 is the constrained subclass
	 * element 4 is the constraint name
	 */
	private void addMethodArgumentConstraint(Vector<String[]> constraints, String  inBracket,String path)
	{
		trace("Adding method argument from '" + inBracket + "'");
		StringTokenizer args = new StringTokenizer(inBracket,", ");
		while (args.hasMoreTokens())
		{
			String arg = args.nextToken();
			if (mentionsClassInTemplatedPackage(arg))
			{
				String[] constraint = new String[4];
				constraint[0] = path;
				constraint[1] = templatePackageName(arg);
				constraint[2] = constrainedClassName(arg);
				constraint[3] = constraintName;
				constraints.add(constraint);
			}
		}
	}
	
	
	/**
	 * 
	 * @param s
	 * @return true if s is an integer
	 */
	private boolean isInteger(String s)
	{
		boolean isNumber = false;
		try {new Integer(s); isNumber= true;}
		catch (Exception ex) {}
		return isNumber;
	}
		
	
	
	/**
	 * @param path the path to get to a variable
	 * @param inBracket a String of a form like:  'partic : cda::Participant2 | partic.oclIs(ccd::PatientAwareness)',
	 * got from inside a bracket and containing 'oclIs(ccd::'
	 * @return a Vector of String arrays of 3 elements: 
	 * element 0 is a path from the owning node
	 * element 1 is the package of the constrained class
	 * element 2 is the constrained subclass
	 */
	private Vector<String[]> getConstraints(String path, String inBracket) throws MapperException
	{
		trace("getConstraints with path '" + path + "' and bracket contents '" + inBracket + "'");
		
		// split around the central '|'
		StringTokenizer split = new StringTokenizer(inBracket,"|");
		if (split.countTokens() != 2)  throw new MapperException("Bracket contents '" + inBracket + "' do not have one '|'");
		String varDeclaration = split.nextToken(); // variable declaration before the '|'
		String pathConstraint = split.nextToken(); // the rest after the '|'
		
		// parse the variable declaration; keep only part before ':' as in 'partic : cda::Participant2' or just 'act '
		StringTokenizer decl = new StringTokenizer(varDeclaration,": ");
		String varName = decl.nextToken();
		
		// parse the remaining path constraints (there may be several separated by 'and' or 'or' with spaces and brackets)
		Vector<String[]> constraints = parsePathConstraints( pathConstraint, path, varName);
		return constraints;
	}
	
	/**
	 * 
	 * @param pathConstraint a String like 'partic.oclIs(ccd::PatientAwareness)' , but which may contain extra brackets
	 * @param path to be prepended on any path in this text section
	 * @param varName e.g 'partic', to be replaced by the path
	 * 
	 * @return a Vector of String arrays of 4 elements: 
	 * element 0 is a path from the owning node
	 * element 1 is the package of the constrained class
	 * element 2 is the constrained subclass
	 * element 3 is the constraint name
	 */
	private Vector<String[]> parsePathConstraints(String pathConstraint,String path,String varName)
	throws MapperException
	{
		trace("Parsing path constraint '" + pathConstraint + "'");
		Vector<String[]> constraints = new Vector<String[]>();
		StringTokenizer rem = new StringTokenizer(pathConstraint," ");
		while (rem.hasMoreTokens())
		{
			String section = rem.nextToken();
			// looking for one section like 'partic.oclIsKindOf(ccd::PatientAwareness)'
			if (section.contains("oclIsKindOf"))
			{
				String[] constraint = getKindOfConstraint(path, varName, section);
				if (constraint != null) constraints.add(constraint);
			}
			else if (section.startsWith("($")) // section is like '($k5)'
			{
				String key = section.substring(1,section.length()-1);
				String  inBracket = fragments.get(key); 
				if (inBracket != null)
				{
					Vector<String[]> cons = parsePathConstraints(inBracket, path, varName);
					for (int i = 0; i < cons.size(); i++) constraints.add(cons.get(i));										
				}
				else throw new MapperException("Cannot resolve bracket '" + section + "'");
			}
		}
		return constraints;
	}
	
	/**
	 * 
	 * @param path to be prepended on any path in this text section
	 * @param varName e.g 'partic', to be replaced by the path
	 * @param section e.g 'partic.oclIsKindOf(ccd::PatientAwareness)'
	 * @param fragments
	 * @return a string array of dimension 4:
	 * element [0]  the path
	 * element [1]  package name of the constrained class, e.g. 'ccd'
	 * element [2]  class name of the constrained class, e.g. 'PatientAwareness'
	 * element [3] is the constraint name
	 */
	private String[] getKindOfConstraint(String path,String varName,String section)
	{
		trace("Kind of constraint: " + section);
		String[] constraint = new String[4];

		String fullPath = path + ".";
		boolean foundKeyword = false;
		StringTokenizer st = new StringTokenizer(section,"().");
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (token.equals(varName)) {} // the initial variable name is replaced by the path to the variable
			else if (token.equals("oclIsKindOf")) {foundKeyword = true;}
			else if (!foundKeyword) fullPath = fullPath + token + "."; // extend the path
			else if (token.startsWith("$")) // find package name and class name inside the bracket, having got bracket contents from the key
			{
				String fullClassName = fragments.get(token);
				constraint[1] = templatePackageName(fullClassName);
				constraint[2] = constrainedClassName(fullClassName);
				constraint[3] = constraintName;
			}
		}
		// path, with final '.' removed
		if (foundKeyword) constraint[0] = fullPath.substring(0,fullPath.length()-1); 
		if ((constraint[0] == null)|(constraint[1] == null)) return null;
		return constraint;
	}
	
	/**
	 * 
	 * @param fullClassName e.g 'ccd::PatientAwareness'
	 * @return the package name, e.g. 'ccd', as long as it is a package with template constraints;
	 * if not return null
	 */
	private String templatePackageName(String fullClassName)
	{
		StringTokenizer st = new StringTokenizer(fullClassName," :"); // allow for random spaces too
		String pName = st.nextToken();
		if (!GenUtil.inVector(pName, templatedPackages)) pName = null;
		return pName;
	}
	
	/**
	 * 
	 * @param fullClassName e.g 'ccd::PatientAwareness'
	 * @return the class name, e.g 'PatientAwareness'
	 */
	private String constrainedClassName(String fullClassName)
	{
		StringTokenizer st = new StringTokenizer(fullClassName," :"); // allow for random spaces too
		if (st.countTokens() != 2) return null;
		st.nextToken();
		return st.nextToken();
	}

	
	
	/**
	 * 
	 * @return a series of [path,package,class, constraint name] constraints
	 */
	public String showConstraintPaths()
	{
		String paths = "";
		for (int i = 0; i < constraintPaths.size();i++)
		{
			String[] constraint = constraintPaths.get(i);
			paths = paths + "[" + constraint[0] + "," + constraint[1] + "," + constraint[2] + ","  + constraint[3] + "] ";
		}
		return paths;
	}

	
	//--------------------------------------------------------------------------------------------------
	//                         Separating out the contents of brackets
	//--------------------------------------------------------------------------------------------------
	
	private Hashtable<String,String> fragments = new Hashtable<String,String>();
	
	private int keyIndex = 1;
	
	/**
	 * parse the bracket structure of a String to any level up to 20, 
	 * denoting the contents of any bracket by a key starting with '$'
	 * @param input
	 * @return the top level string, with ($k2) for the contensts of top-level brackets
	 * @throws MapperException if brackets do not balance
	 */
	private String parseBrackets(String input) throws MapperException
	{
		String[] stack = new String[20];  //maximum depth of nesting of brackets
		stack[0] = "";
		int level = 0;
		StringTokenizer st = new StringTokenizer(input,"()",true);
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			// start a new level of nesting
			if (step.equals("(")) 
			{
				level++;
				stack[level] = "";
			}
			// close off a level, and remember it by a key
			else if (step.equals(")")) 
			{
				String key = storeFragment(stack[level]);
				level--;
				stack[level] = stack[level] + "(" + key + ")";
			}
			// build up the current level
			else
			{
				stack[level] = stack[level] + step;
			}
		}
		if (level != 0) throw new MapperException("Brackets unbalanced by " + level + " in OCL expression '" + input + "'");
		return stack[0];
	}
	
	/**
	 * store a fragment of text under a unique key, and return the key
	 * @param fragment
	 * @return
	 */
	private String storeFragment(String fragment)
	{
		String key = "$k" + keyIndex;
		keyIndex++;
		fragments.put(key, fragment);
		return key;
	}

}
