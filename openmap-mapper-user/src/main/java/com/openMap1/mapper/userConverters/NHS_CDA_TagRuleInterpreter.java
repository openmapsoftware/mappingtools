package com.openMap1.mapper.userConverters;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;



public class NHS_CDA_TagRuleInterpreter{
	
	// column names in the csv file
	public static String TAG_RULE_HEADER_ROW = "CDA_Tag,Model_Tag,test,classCode,typeCode,moodCode,Parent_Path,Parent_Test,other_tests";
	
	/* columns in the csv file defining CDA tag name rules */
	static int CDA_TAG = 0;
	static int TEMPLATED_TAG = 1;
	static int NAME_TEST = 2;
	static int CLASSCODE = 3;
	static int TYPECODE = 4;
	static int MOODCODE = 5;
	static int PARENT_PATH = 6;
	static int PARENT_TEST = 7;
	static int OTHER_TESTS = 8;

	
	/**
	 * Try out all possible cda name conversions, to see if any match the rules
	 * @param fullTagName
	 * @param parentPath
	 * @param fixedValues
	 * @param CDATagRules
	 * @return Hashtable of cda tag names which pass the tests
	 */
	public static Hashtable<String,String> getCDATagNames(String fullTagName, String parentPath,
			Vector<String[]> fixedValues, Vector<String[]> CDATagRules)
	{
		Hashtable<String,String> cdaNames = new Hashtable<String,String>();
		for (int i = 0; i < CDATagRules.size(); i++)
		{
			String[] tagRule = CDATagRules.get(i);
			
			// test the tag name; "" in the slot tagRule[NAME_TEST] means no test
			boolean namePasses = someStringTest(fullTagName,tagRule[TEMPLATED_TAG],tagRule[NAME_TEST]);
			
			// test the classCode; can be one of several values , separated by '|'
			boolean classCodePasses = true;
			String classCode = getFixedValue(fixedValues,"@classCode");
			StringTokenizer st = new StringTokenizer(tagRule[CLASSCODE],"|");
			if (st.countTokens() > 0) // no test if classCode slot is empty
			{
				classCodePasses = false;
				while (st.hasMoreTokens()) if (st.nextToken().equals(classCode)) classCodePasses = true;
			}
			
			// test the typeCode; can be one of several values , separated by '|'
			boolean typeCodePasses = true;
			String typeCode = getFixedValue(fixedValues,"@typeCode");
			st = new StringTokenizer(tagRule[TYPECODE],"|");
			if (st.countTokens() > 0) // no test if typeCode slot is empty
			{
				typeCodePasses = false;
				while (st.hasMoreTokens()) if (st.nextToken().equals(typeCode)) typeCodePasses = true;
			}
			
			// test the moodCode; can be one of several values , separated by '|'
			boolean moodCodePasses = true;
			String moodCode = getFixedValue(fixedValues,"@moodCode");
			st = new StringTokenizer(tagRule[MOODCODE],"|");
			if (st.countTokens() > 0) // no test if typeCode slot is empty
			{
				moodCodePasses = false;
				while (st.hasMoreTokens()) if (st.nextToken().equals(moodCode)) moodCodePasses = true;
			}
			
			// test the parent path
			boolean parentNamePasses = someStringTest(parentPath,tagRule[PARENT_PATH],tagRule[PARENT_TEST]);
			

			// FIXME; code for other tests looks very limited, compared to the way they are used in the spreadsheet
			// test other fixed value conditions; AND of tests separated by ';'
			boolean otherTestPasses = true;
			st = new StringTokenizer(tagRule[OTHER_TESTS],";"); 
			while (st.hasMoreTokens()) // no test if 'other tests' slot is empty
			{
				String otherTest = st.nextToken();
				otherTestPasses = otherTestPasses && passesOtherTest(fixedValues,otherTest);
			}
			
			if (namePasses && classCodePasses && typeCodePasses && moodCodePasses && parentNamePasses && otherTestPasses) 
				cdaNames.put(tagRule[CDA_TAG],new Integer(i).toString());
		}
		return cdaNames;
	}
	
	private static boolean someStringTest(String fullTagName, String cdaTagName,String theTest)
	{
		boolean namePasses = true;
		if (theTest.equals("equals")) namePasses = fullTagName.equals(cdaTagName);
		else if (theTest.equals("!equals")) namePasses = !fullTagName.equals(cdaTagName);
		else if (theTest.equals("contains")) namePasses = fullTagName.contains(cdaTagName);
		else if (theTest.equals("!contains")) namePasses = !fullTagName.contains(cdaTagName);
		else if (theTest.equals("startsWith")) namePasses = fullTagName.startsWith(cdaTagName);
		else if (theTest.equals("!startsWith")) namePasses = !fullTagName.startsWith(cdaTagName);
		else if (theTest.equals("endsWith")) namePasses = fullTagName.endsWith(cdaTagName);
		else if (theTest.equals("!endsWith")) namePasses = !fullTagName.endsWith(cdaTagName);
		else if (theTest.equals("")) namePasses = true;
		else trace("Invalid tag name test: '" + theTest + "'");
		
		return namePasses;
	}
	
	/**
	 * 
	 * @param fixedValues
	 * @param otherTest
	 * @return
	 */
	private static boolean passesOtherTest(Vector<String[]> fixedValues, String otherTest)
	{
		boolean passes = false;
		// only equality tests 'path=value'
		StringTokenizer st = new StringTokenizer(otherTest,"=");
		if (st.countTokens() != 2) return false;
		String path = st.nextToken();
		String value = st.nextToken();

		// test against all known fixed values at the node
		for (int i = 0; i < fixedValues.size(); i++)
		{
			String[] fv  =fixedValues.get(i);
			if ((fv[0].equals(path)) && (fv[1].equals(value))) passes = true;
		}
		return passes;
	}
	
	/**
	 * 
	 * @param fixedValues
	 * @param path
	 * @return
	 */
	private static String getFixedValue(Vector<String[]> fixedValues, String path)
	{
		String fixedValue= "";
		for (int i = 0; i < fixedValues.size(); i++)
		{
			String[] fv = fixedValues.get(i);
			if (fv[0].equals(path)) fixedValue = fv[1];
		}
		return fixedValue;
	}
	
	private static void trace(String s) {System.out.println(s);}

}
