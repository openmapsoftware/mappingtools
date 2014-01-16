package com.openMap1.mapper.health.v3;

/**
 * This class provides data describing the V3
 * Reference Information Model (RIM)
 * @author robert
 *
 */
public class V3RIM {

	
	// HL7 colouring of RIM classes (repeated in ClassModelView)
	static String[] redClasses = {"Act", "ControlAct", 
		"Observation","DiagnosticImage","PublicHealthCase",
		"Supply", "Diet", "DeviceTask",
		"FinancialContract","InvoiceElement","FinancialTransaction","Account",
		"PatientEncounter","SubstanceAdministration","WorkingList","Exposure",
		"Procedure"};
	static String[] pinkClasses = {"ActRelationship"};
	static String[] blueClasses = {"Participation","ManagedParticipation"};
	static String[] yellowClasses = {"Role", "RoleLink","Patient",
		"LicensedEntity","QualifiedEntity","Access","Employee"};
	static String[] greenClasses = {"Place","Person",
		"LivingSubject","NonPersonLivingSubject","Organization",
		"Material","Device","ManufacturedMaterial","Container"};
	
	/**
	 * classes used to define the RIM structural attributes, and their
	 * subclasses
	 */
	static String[][] rimSubClasses =
	{	{"Act", "ControlAct", 
			"Observation","DiagnosticImage","PublicHealthCase",
			"Supply", "Diet", "DeviceTask",
			"FinancialContract","InvoiceElement","FinancialTransaction","Account",
			"PatientEncounter","SubstanceAdministration","WorkingList","Exposure",
			"Procedure","Document"},
		{"ActRelationship"},
		{"Participation","ManagedParticipation"},
		{"Role","Patient",
			"LicensedEntity","QualifiedEntity","Access","Employee"},
		{"RoleLink"},
		{"Entity","Place","Person",
			"LivingSubject","NonPersonLivingSubject","Organization",
			"Material","Device","ManufacturedMaterial","Container"}};

	/**
	 * classes used to define the RIM structural attributes, and their
	 * structural attributes.
	 * The classes must be in the same order as the previous array rimSubClasses
	 */
	static String[][] rimStructuralAttributes =
	{	{"Act","moodCode","classCode","negationInd","levelCode"},
		{"ActRelationship","typeCode","ActRelationshipInversionInd",
			"contextControlCode","contextConductionInd","negationInd"},
		{"Participation","typeCode","contextControlCode"},
		{"Role","classCode","negationInd"},
		{"RoleLink","typeCode"},
		{"Entity","classCode","determinerCode"}};
	
	/**
	 * @param className name of a RIM class
	 * @param attName name of a RIM attribute
	 * @return true if it is a RIM structural attribute
	 */
	static public boolean isRIMStructuralAttribute(String className, String attName)
	{
		boolean structural = false;

		// find which core RIM class this is a subclass of
		int coreClassIndex = -1;
		for (int i = 0; i < rimSubClasses.length; i++)
		{
			String[] subClassSet = rimSubClasses[i];
			for (int j = 0; j < subClassSet.length; j++)
				if (subClassSet[j].equals(className)) coreClassIndex = i;
		}
		
		// find if this is a structural attribute of the core class
		if (coreClassIndex > -1)
		{
			String[] atts = rimStructuralAttributes[coreClassIndex];
			for (int j = 1; j < atts.length; j++)
				if (atts[j].equals(attName)) structural = true;
		}
		return structural;
	}



}
