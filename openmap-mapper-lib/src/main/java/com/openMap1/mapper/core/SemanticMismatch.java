package com.openMap1.mapper.core;

import org.eclipse.emf.ecore.*;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.util.ModelUtil;
/**
 * Class used to record discrepancies between translation 
 * sources and results at the semantic level - i.e 
 * discrepancies between Ecore model instances derived from
 * translation results and sources
 * 
 * @author robert
 *
 */
public class SemanticMismatch extends TranslationIssue {
	
	public static int SEMANTIC_MISSING_CLASS = 0;
	public static int SEMANTIC_MISSING_LINK = 1;
	public static int SEMANTIC_MISSING_PROPERTY_VALUE = 2;
	public static int SEMANTIC_EXTRA_CLASS = 3;
	public static int SEMANTIC_EXTRA_LINK = 4;
	public static int SEMANTIC_EXTRA_PROPERTY_VALUE = 5;
	public static int SEMANTIC_INCORRECT_PROPERTY_VALUE = 6;
	public static int SEMANTIC_INCORRECT_TARGET_CLASS = 7;
	public static int SEMANTIC_INCORRECT_LINK_CARDINALITY = 8;
	
	
	// instance variables set in the constructor
	private EObject ownerObject;
	private EStructuralFeature feature;
	private EObject targetObject;
	
	// int nature, occurrences, String expected and String actual are inherited from TranslationIssue
	
	/**
	 * @return Class involved - missing instances, extra instances, or
	 * missing or incorrect property values
	 */
	public String className() {return ModelUtil.getQualifiedClassName(ownerObject.eClass());}
	
	/**
	 * @return Property involved - in missing or incorrect property values
	 */
	public String featureName() 
	{
		if (feature == null) return "";
		return feature.getName();
	}
	
	
	/**
	 * when the 'expected ' and 'actual' fields record numbers of occurrences,
	 * add one actual occurrence
	 */
	public void addOccurrence() {occurrences++;}
	
	/**
	 * @return path of containment association names from root of the ECore instance
	 * to the owner EObject
	 */
	public String path() {return ModelUtil.pathFromRoot(ownerObject);}
	

	
	private String sourceCode, resultCode;
	
	public String sourceCode() {return sourceCode;}
	
	/**
	 * @param nature nature of the problem - unexpected name, missing, or repeated
	 * @param expected expected value of some property
	 * @param actual actual wrong value of that property
	 */
	public SemanticMismatch( 
			int nature, 
			String expected, 
			String actual,
			EObject ownerObject,
			EStructuralFeature feature,
			EObject targetObject,
			String sourceCode,String resultCode)
	{
		super(nature,expected,actual);
		this.ownerObject = ownerObject;
		this.feature = feature;
		this.targetObject = targetObject;
		this.sourceCode = sourceCode;
		this.resultCode = resultCode;
	}
	
	public boolean missingFault()
	{
		return ((nature == SEMANTIC_MISSING_CLASS)|
				(nature == SEMANTIC_MISSING_LINK)|
				(nature == SEMANTIC_MISSING_PROPERTY_VALUE));
	}

	
	public boolean extraFault()
	{
		return ((nature == SEMANTIC_EXTRA_CLASS)|
				(nature == SEMANTIC_EXTRA_LINK)|
				(nature == SEMANTIC_EXTRA_PROPERTY_VALUE));
	}
	
	/**
	 * @return description of the mismatch
	 */
	public String description()  
	{
		String description = "";
		if (nature == SEMANTIC_MISSING_CLASS) 
			{description = "Missing object of class '" + className() + "'";}
		if (nature == SEMANTIC_MISSING_LINK) 
			{description = "Missing link from object of class '" 
				+ className() + "' with role name '" + featureName() + "'";}
		if (nature == SEMANTIC_MISSING_PROPERTY_VALUE) 
			{description = "Missing property '" + featureName() + "' in class '" + className() + "'";}
		if (nature == SEMANTIC_EXTRA_CLASS) 
			{description = "Unexpected object of class '" + className() + "'";}
		if (nature == SEMANTIC_EXTRA_LINK) 
			{description = "Unexpected link from object of class '" 
			+ className() + "' with role name '" + featureName() + "'";}
		if (nature == SEMANTIC_EXTRA_PROPERTY_VALUE) 
			{description = "Unexpected property '" + featureName() + "' in class '" + className() + "'";}
		if (nature == SEMANTIC_INCORRECT_PROPERTY_VALUE) 
			{description = "Incorrect value of property '" 
				+ featureName() + "' in class '" + className() + "'; should be '" 
				+ expected + "' but is '" + actual + "'"  ;}
		if (nature == SEMANTIC_INCORRECT_TARGET_CLASS) 
			{description = "Link '" + featureName() + " from class '" + className() + "' "
			+ " has incorrect target class '" + actual + "' which should be '" + expected + "'";}
		if (nature == SEMANTIC_INCORRECT_LINK_CARDINALITY) 
		{description = "Link '" + featureName() + "' from class '" + className() + "' "
			+ " has incorrect cardinality " + actual + " which should be " + expected;}
		return description;
	}
	
	/**
	 * 
	 * @param col column index 0..N
	 * @return the entry for this translation issue in the column col
	 */	
	public String cellContents(int col)
	{
		String cell = "";
		if (col == CODE) cell = fileName;
		else if (col == TYPE) cell = "Semantic";
		else if (col == OCCURRENCES) cell = new Integer(occurrences).toString();
		else if (col == DESCRIPTION) cell = description();
		else if (col == CAUSEORLOCATION) cell = path() + "; " + describeMissingMappings();
		
		return  cell;
	}
	
	/** 
	 * @return a key for storing SemanticMismatch object in a Hashtable, so that 
	 * further occurrences of the same fault do not lead to duplicate records
	 */
	public String key()
	{
		String key =  "semantic_" + nature + "$" + className() + ":" + featureName() ;
		if (!missingFault() && !extraFault())
			key = key + "(" + expected + "," + actual + ")";
		return key;
	}
	
	//------------------------------------------------------------------------------------
	//				Tracing the origin of missing EObjects in missing mappings
	//------------------------------------------------------------------------------------
	
	
	/**
	 * check that there is a mapping to this class, property or link
	 * in source and result mappings. 
	 * FIXME: does not take proper account of mappings to subclasses
	 * 
	 * @param classObject the EClass of the object that is missing in the result, or which has
	 * some feature missing
	 * @param featureObject the EAttribute or EReference which is missing
	 * @param sourceReader the XOReader for the source - which should have all the mappings
	 * @param resultReader the XOReader for the result - which may have gaps (or some gaps may
	 * come from earlier translations, if the results comes from a chain of more than one translation)
	 */
	public boolean checkMissingMapping(XOReader sourceReader, XOReader resultReader)  
	throws MapperException
	{
		/* FIXME; this can be activeted to avoid the reader calls below, 
		 * which can be highly expensive when there are many missing mapping sets */
		// if (true) return false;
		if (sourceReader == null) return false;
		if (resultReader == null) return false;
		missingMapping = false;
		/* it would be rather bizarre for the source mapping to be missing; but 
		 * sometimes apparently happens because the check does not take account of inheritance yet */
		boolean missingSourceMapping = false; // not used
		missingMappingDescription = "";

		if (nature == SEMANTIC_MISSING_CLASS)
		{
			missingMappingDescription = "object mapping for class '" + className() + "'";
			if (!sourceReader.representsObject(className()))
				missingSourceMapping = true;
			if (!resultReader.representsObject(className()))
				missingMapping = true;
		}
		else if (nature == SEMANTIC_MISSING_PROPERTY_VALUE)
		{
			missingMappingDescription = "property mapping for '" + className() + ":"
			 + featureName() + "'";
			if (!sourceReader.representsProperty(className(), featureName()))
				missingSourceMapping = true;
			if (!resultReader.representsProperty(className(), featureName()))
				missingMapping = true;
		}
		else if (nature == SEMANTIC_MISSING_LINK)
		{
			String targetClassName = ModelUtil.getQualifiedClassName(targetObject.eClass());
			missingMappingDescription = "association mapping for '" + className() + "."
			 + featureName() + "." + targetClassName + "'";
			if (!sourceReader.representsAssociationRole(className(), featureName(),targetClassName))
				missingSourceMapping = true;
			if (!resultReader.representsAssociationRole(className(), featureName(),targetClassName))
				missingMapping = true;
		}
		if (missingSourceMapping) {}
		return missingMapping;
	}

	
	private String describeMissingMappings()
	{
		String missing = "";
		if (missingMapping) missing = "No " + missingMappingDescription + " in source " + resultCode;
		return missing;
	}

}
