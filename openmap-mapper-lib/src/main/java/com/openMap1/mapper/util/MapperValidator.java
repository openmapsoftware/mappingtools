/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.util;

import com.openMap1.mapper.Annotations;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.ConditionTest;
import com.openMap1.mapper.ConversionArgument;
import com.openMap1.mapper.ConversionImplementation;
import com.openMap1.mapper.ConversionSense;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.JavaConversionImplementation;
import com.openMap1.mapper.LocalPropertyConversion;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.ModelAssocFilter;
import com.openMap1.mapper.ModelFilter;
import com.openMap1.mapper.ModelFilterSet;
import com.openMap1.mapper.ModelPropertyFilter;
import com.openMap1.mapper.MultiWay;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.Note;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ParameterClass;
import com.openMap1.mapper.ParameterClassValue;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.PropertyConversion;
import com.openMap1.mapper.StructureType;
import com.openMap1.mapper.ValueCondition;
import com.openMap1.mapper.ValuePair;
import com.openMap1.mapper.XSLTConversionImplementation;

import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;

import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.EObjectValidator;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see com.openMap1.mapper.MapperPackage
 * @generated
 */
public class MapperValidator extends EObjectValidator {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final MapperValidator INSTANCE = new MapperValidator();

	/**
	 * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Diagnostic#getSource()
	 * @see org.eclipse.emf.common.util.Diagnostic#getCode()
	 * @generated
	 */
	public static final String DIAGNOSTIC_SOURCE = "com.openMap1.mapper";

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Has Role To Class' of 'Assoc End Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ASSOC_END_MAPPING__CLASS_HAS_ROLE_TO_CLASS = 1;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Object Mapping Exists' of 'Assoc End Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ASSOC_END_MAPPING__OBJECT_MAPPING_EXISTS = 2;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Object To Association Path Is Valid' of 'Assoc End Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH_IS_VALID = 3;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Path Matches Cardinality' of 'Assoc End Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ASSOC_END_MAPPING__PATH_MATCHES_CARDINALITY = 4;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Association To Object Path Is Valid' of 'Assoc End Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH_IS_VALID = 5;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Object Is Unique From Association' of 'Assoc End Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ASSOC_END_MAPPING__OBJECT_IS_UNIQUE_FROM_ASSOCIATION = 6;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Is In Class Model' of 'Class Details'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CLASS_DETAILS__CLASS_IS_IN_CLASS_MODEL = 7;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Only One Out Conversion Per Pseudo Property And Subset' of 'Class Details'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CLASS_DETAILS__ONLY_ONE_OUT_CONVERSION_PER_PSEUDO_PROPERTY_AND_SUBSET = 8;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Only One In Conversion Per Property And Subset' of 'Class Details'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CLASS_DETAILS__ONLY_ONE_IN_CONVERSION_PER_PROPERTY_AND_SUBSET = 9;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Converted Property Is Not Represented Directly' of 'Class Details'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CLASS_DETAILS__CONVERTED_PROPERTY_IS_NOT_REPRESENTED_DIRECTLY = 10;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Has Property' of 'Conversion Argument'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CONVERSION_ARGUMENT__CLASS_HAS_PROPERTY = 11;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Property Mapping Exists' of 'Conversion Argument'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CONVERSION_ARGUMENT__PROPERTY_MAPPING_EXISTS = 12;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Right Path Is Valid' of 'Cross Condition'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CROSS_CONDITION__RIGHT_PATH_IS_VALID = 13;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Right Path Gives Unique Node' of 'Cross Condition'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CROSS_CONDITION__RIGHT_PATH_GIVES_UNIQUE_NODE = 14;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Right Function Is Valid' of 'Cross Condition'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int CROSS_CONDITION__RIGHT_FUNCTION_IS_VALID = 15;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'No Children If Not Expanded' of 'Element Def'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ELEMENT_DEF__NO_CHILDREN_IF_NOT_EXPANDED = 16;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Has All Children If Expanded' of 'Element Def'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ELEMENT_DEF__HAS_ALL_CHILDREN_IF_EXPANDED = 17;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Has Correct Max Multiplicity' of 'Element Def'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int ELEMENT_DEF__HAS_CORRECT_MAX_MULTIPLICITY = 18;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Has Property' of 'Fixed Property Value'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int FIXED_PROPERTY_VALUE__CLASS_HAS_PROPERTY = 19;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Can Find Mapping Set' of 'Import Mapping Set'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int IMPORT_MAPPING_SET__CAN_FIND_MAPPING_SET = 20;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Mapping Set Has Same Class Model' of 'Import Mapping Set'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int IMPORT_MAPPING_SET__MAPPING_SET_HAS_SAME_CLASS_MODEL = 21;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Mapping Set Parameters Match' of 'Import Mapping Set'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int IMPORT_MAPPING_SET__MAPPING_SET_PARAMETERS_MATCH = 22;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Mapping Set Structure Matches' of 'Import Mapping Set'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int IMPORT_MAPPING_SET__MAPPING_SET_STRUCTURE_MATCHES = 23;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Can Find Class Model' of 'Mapped Structure'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MAPPED_STRUCTURE__CAN_FIND_CLASS_MODEL = 24;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Can Find Structure Definition' of 'Mapped Structure'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MAPPED_STRUCTURE__CAN_FIND_STRUCTURE_DEFINITION = 25;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Mapped Class Is In Class Model' of 'Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MAPPING__MAPPED_CLASS_IS_IN_CLASS_MODEL = 26;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Left Path Is Valid' of 'Mapping Condition'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MAPPING_CONDITION__LEFT_PATH_IS_VALID = 27;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Left Path Gives Unique Node' of 'Mapping Condition'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MAPPING_CONDITION__LEFT_PATH_GIVES_UNIQUE_NODE = 28;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Left Function Is Valid' of 'Mapping Condition'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MAPPING_CONDITION__LEFT_FUNCTION_IS_VALID = 29;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Has Role From Other Class' of 'Model Assoc Filter'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MODEL_ASSOC_FILTER__CLASS_HAS_ROLE_FROM_OTHER_CLASS = 30;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Other Object Mapping Exists' of 'Model Assoc Filter'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MODEL_ASSOC_FILTER__OTHER_OBJECT_MAPPING_EXISTS = 31;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Has Property' of 'Model Property Filter'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int MODEL_PROPERTY_FILTER__CLASS_HAS_PROPERTY = 32;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Not Both Default And Fixed' of 'Node Def'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int NODE_DEF__NOT_BOTH_DEFAULT_AND_FIXED = 33;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Has Correct Default Or Fixed Value' of 'Node Def'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int NODE_DEF__HAS_CORRECT_DEFAULT_OR_FIXED_VALUE = 34;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Has Correct Min Multiplicity' of 'Node Def'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int NODE_DEF__HAS_CORRECT_MIN_MULTIPLICITY = 35;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'In Structure Of Containing Element' of 'Node Def'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int NODE_DEF__IN_STRUCTURE_OF_CONTAINING_ELEMENT = 36;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Subset Is Unique Within Class' of 'Obj Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int OBJ_MAPPING__SUBSET_IS_UNIQUE_WITHIN_CLASS = 37;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Root Path Is Consistent With Node Position' of 'Obj Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int OBJ_MAPPING__ROOT_PATH_IS_CONSISTENT_WITH_NODE_POSITION = 38;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Is In Class Model' of 'Parameter Class'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PARAMETER_CLASS__CLASS_IS_IN_CLASS_MODEL = 39;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Object Mapping Exists' of 'Parameter Class'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PARAMETER_CLASS__OBJECT_MAPPING_EXISTS = 40;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Mapped Class Is In Class Model' of 'Parameter Class Value'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PARAMETER_CLASS_VALUE__MAPPED_CLASS_IS_IN_CLASS_MODEL = 41;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Mapping Exists For Parameter Class Value' of 'Parameter Class Value'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PARAMETER_CLASS_VALUE__MAPPING_EXISTS_FOR_PARAMETER_CLASS_VALUE = 42;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Has Property' of 'Prop Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PROP_MAPPING__CLASS_HAS_PROPERTY = 43;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Object Mapping Exists' of 'Prop Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PROP_MAPPING__OBJECT_MAPPING_EXISTS = 44;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Object To Property Path Is Valid' of 'Prop Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PROP_MAPPING__OBJECT_TO_PROPERTY_PATH_IS_VALID = 45;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Property Is Unique From Object Node' of 'Prop Mapping'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PROP_MAPPING__PROPERTY_IS_UNIQUE_FROM_OBJECT_NODE = 46;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Class Has Result Property' of 'Property Conversion'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PROPERTY_CONVERSION__CLASS_HAS_RESULT_PROPERTY = 47;

	/**
	 * The {@link org.eclipse.emf.common.util.Diagnostic#getCode() code} for constraint 'Has Implementation' of 'Property Conversion'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final int PROPERTY_CONVERSION__HAS_IMPLEMENTATION = 48;

	/**
	 * A constant with a fixed name that can be used as the base value for additional hand written constants.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 48;

	/**
	 * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MapperValidator() {
		super();
	}

	/**
	 * Returns the package of this validator switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EPackage getEPackage() {
	  return MapperPackage.eINSTANCE;
	}

	/**
	 * public form of the validate method used for testing
	 * @param classifierID
	 * @param value
	 * @param diagnostics
	 * @param context
	 * @return
	 */
	public boolean publicValidate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context)
	  {return validate(classifierID,value,diagnostics,context);}

	/**
	 * Calls <code>validateXXX</code> for the corresponding classifier of the model.
	 * <!-- begin-user-doc -->
	 * do not remove the generated tag - this needs to be regenerated when new
	 * model classes have validation
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		switch (classifierID) {
			case MapperPackage.ANNOTATIONS:
				return validateAnnotations((Annotations)value, diagnostics, context);
			case MapperPackage.ASSOC_END_MAPPING:
				return validateAssocEndMapping((AssocEndMapping)value, diagnostics, context);
			case MapperPackage.ASSOC_MAPPING:
				return validateAssocMapping((AssocMapping)value, diagnostics, context);
			case MapperPackage.ATTRIBUTE_DEF:
				return validateAttributeDef((AttributeDef)value, diagnostics, context);
			case MapperPackage.CLASS_DETAILS:
				return validateClassDetails((ClassDetails)value, diagnostics, context);
			case MapperPackage.CONVERSION_ARGUMENT:
				return validateConversionArgument((ConversionArgument)value, diagnostics, context);
			case MapperPackage.CONVERSION_IMPLEMENTATION:
				return validateConversionImplementation((ConversionImplementation)value, diagnostics, context);
			case MapperPackage.CROSS_CONDITION:
				return validateCrossCondition((CrossCondition)value, diagnostics, context);
			case MapperPackage.ELEMENT_DEF:
				return validateElementDef((ElementDef)value, diagnostics, context);
			case MapperPackage.FIXED_PROPERTY_VALUE:
				return validateFixedPropertyValue((FixedPropertyValue)value, diagnostics, context);
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS:
				return validateGlobalMappingParameters((GlobalMappingParameters)value, diagnostics, context);
			case MapperPackage.IMPORT_MAPPING_SET:
				return validateImportMappingSet((ImportMappingSet)value, diagnostics, context);
			case MapperPackage.JAVA_CONVERSION_IMPLEMENTATION:
				return validateJavaConversionImplementation((JavaConversionImplementation)value, diagnostics, context);
			case MapperPackage.LOCAL_PROPERTY_CONVERSION:
				return validateLocalPropertyConversion((LocalPropertyConversion)value, diagnostics, context);
			case MapperPackage.MAPPED_STRUCTURE:
				return validateMappedStructure((MappedStructure)value, diagnostics, context);
			case MapperPackage.MAPPING:
				return validateMapping((Mapping)value, diagnostics, context);
			case MapperPackage.MAPPING_CONDITION:
				return validateMappingCondition((MappingCondition)value, diagnostics, context);
			case MapperPackage.MODEL_ASSOC_FILTER:
				return validateModelAssocFilter((ModelAssocFilter)value, diagnostics, context);
			case MapperPackage.MODEL_FILTER:
				return validateModelFilter((ModelFilter)value, diagnostics, context);
			case MapperPackage.MODEL_FILTER_SET:
				return validateModelFilterSet((ModelFilterSet)value, diagnostics, context);
			case MapperPackage.MODEL_PROPERTY_FILTER:
				return validateModelPropertyFilter((ModelPropertyFilter)value, diagnostics, context);
			case MapperPackage.NAMESPACE:
				return validateNamespace((Namespace)value, diagnostics, context);
			case MapperPackage.NODE_DEF:
				return validateNodeDef((NodeDef)value, diagnostics, context);
			case MapperPackage.NODE_MAPPING_SET:
				return validateNodeMappingSet((NodeMappingSet)value, diagnostics, context);
			case MapperPackage.NOTE:
				return validateNote((Note)value, diagnostics, context);
			case MapperPackage.OBJ_MAPPING:
				return validateObjMapping((ObjMapping)value, diagnostics, context);
			case MapperPackage.PARAMETER_CLASS:
				return validateParameterClass((ParameterClass)value, diagnostics, context);
			case MapperPackage.PARAMETER_CLASS_VALUE:
				return validateParameterClassValue((ParameterClassValue)value, diagnostics, context);
			case MapperPackage.PROP_MAPPING:
				return validatePropMapping((PropMapping)value, diagnostics, context);
			case MapperPackage.PROPERTY_CONVERSION:
				return validatePropertyConversion((PropertyConversion)value, diagnostics, context);
			case MapperPackage.VALUE_CONDITION:
				return validateValueCondition((ValueCondition)value, diagnostics, context);
			case MapperPackage.VALUE_PAIR:
				return validateValuePair((ValuePair)value, diagnostics, context);
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION:
				return validateXSLTConversionImplementation((XSLTConversionImplementation)value, diagnostics, context);
			case MapperPackage.CONDITION_TEST:
				return validateConditionTest((ConditionTest)value, diagnostics, context);
			case MapperPackage.CONVERSION_SENSE:
				return validateConversionSense((ConversionSense)value, diagnostics, context);
			case MapperPackage.MAX_MULT:
				return validateMaxMult((MaxMult)value, diagnostics, context);
			case MapperPackage.MIN_MULT:
				return validateMinMult((MinMult)value, diagnostics, context);
			case MapperPackage.MULTI_WAY:
				return validateMultiWay((MultiWay)value, diagnostics, context);
			case MapperPackage.STRUCTURE_TYPE:
				return validateStructureType((StructureType)value, diagnostics, context);
			case MapperPackage.DIAGNOSTIC_CHAIN:
				return validateDiagnosticChain((DiagnosticChain)value, diagnostics, context);
			case MapperPackage.MAP:
				return validateMap((Map<?, ?>)value, diagnostics, context);
			default:
				return true;
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAnnotations(Annotations annotations, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(annotations, diagnostics, context);
	}

	/* taken out of method 'validate' to avoid a compiler warning that I could find no way round; 
	 * I don't think it is used anyway; but it will be reinstated on regeneration
	case MapperPackage.MAP:
		return validateMap((Map<?,?>)value, diagnostics, context); */

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAssocEndMapping(AssocEndMapping assocEndMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateMapping_mappedClassIsInClassModel(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateAssocEndMapping_classHasRoleToClass(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateAssocEndMapping_objectMappingExists(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateAssocEndMapping_objectToAssociationPathIsValid(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateAssocEndMapping_PathMatchesCardinality(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateAssocEndMapping_AssociationToObjectPathIsValid(assocEndMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateAssocEndMapping_objectIsUniqueFromAssociation(assocEndMapping, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classHasRoleToClass constraint of '<em>Assoc End Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAssocEndMapping_classHasRoleToClass(AssocEndMapping assocEndMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return assocEndMapping.classHasRoleToClass(diagnostics, context);
	}

	/**
	 * Validates the objectMappingExists constraint of '<em>Assoc End Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAssocEndMapping_objectMappingExists(AssocEndMapping assocEndMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return assocEndMapping.objectMappingExists(diagnostics, context);
	}

	/**
	 * Validates the objectToAssociationPathIsValid constraint of '<em>Assoc End Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAssocEndMapping_objectToAssociationPathIsValid(AssocEndMapping assocEndMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return assocEndMapping.objectToAssociationPathIsValid(diagnostics, context);
	}

	/**
	 * Validates the PathMatchesCardinality constraint of '<em>Assoc End Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAssocEndMapping_PathMatchesCardinality(AssocEndMapping assocEndMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return assocEndMapping.PathMatchesCardinality(diagnostics, context);
	}

	/**
	 * Validates the AssociationToObjectPathIsValid constraint of '<em>Assoc End Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAssocEndMapping_AssociationToObjectPathIsValid(AssocEndMapping assocEndMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return assocEndMapping.AssociationToObjectPathIsValid(diagnostics, context);
	}

	/**
	 * Validates the objectIsUniqueFromAssociation constraint of '<em>Assoc End Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAssocEndMapping_objectIsUniqueFromAssociation(AssocEndMapping assocEndMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return assocEndMapping.objectIsUniqueFromAssociation(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAssocMapping(AssocMapping assocMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(assocMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(assocMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(assocMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(assocMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(assocMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(assocMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(assocMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateMapping_mappedClassIsInClassModel(assocMapping, diagnostics, context);
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateAttributeDef(AttributeDef attributeDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_notBothDefaultAndFixed(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_hasCorrectDefaultOrFixedValue(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_hasCorrectMinMultiplicity(attributeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_inStructureOfContainingElement(attributeDef, diagnostics, context);
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateClassDetails(ClassDetails classDetails, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validateClassDetails_classIsInClassModel(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validateClassDetails_onlyOneOutConversionPerPseudoPropertyAndSubset(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validateClassDetails_onlyOneInConversionPerPropertyAndSubset(classDetails, diagnostics, context);
		if (result || diagnostics != null) result &= validateClassDetails_convertedPropertyIsNotRepresentedDirectly(classDetails, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classIsInClassModel constraint of '<em>Class Details</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateClassDetails_classIsInClassModel(ClassDetails classDetails, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return classDetails.classIsInClassModel(diagnostics, context);
	}

	/**
	 * Validates the onlyOneOutConversionPerPseudoPropertyAndSubset constraint of '<em>Class Details</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateClassDetails_onlyOneOutConversionPerPseudoPropertyAndSubset(ClassDetails classDetails, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return classDetails.onlyOneOutConversionPerPseudoPropertyAndSubset(diagnostics, context);
	}

	/**
	 * Validates the onlyOneInConversionPerPropertyAndSubset constraint of '<em>Class Details</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateClassDetails_onlyOneInConversionPerPropertyAndSubset(ClassDetails classDetails, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return classDetails.onlyOneInConversionPerPropertyAndSubset(diagnostics, context);
	}

	/**
	 * Validates the convertedPropertyIsNotRepresentedDirectly constraint of '<em>Class Details</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateClassDetails_convertedPropertyIsNotRepresentedDirectly(ClassDetails classDetails, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return classDetails.convertedPropertyIsNotRepresentedDirectly(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateConversionArgument(ConversionArgument conversionArgument, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(conversionArgument, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(conversionArgument, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(conversionArgument, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(conversionArgument, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(conversionArgument, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(conversionArgument, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(conversionArgument, diagnostics, context);
		if (result || diagnostics != null) result &= validateConversionArgument_classHasProperty(conversionArgument, diagnostics, context);
		if (result || diagnostics != null) result &= validateConversionArgument_propertyMappingExists(conversionArgument, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classHasProperty constraint of '<em>Conversion Argument</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateConversionArgument_classHasProperty(ConversionArgument conversionArgument, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return conversionArgument.classHasProperty(diagnostics, context);
	}

	/**
	 * Validates the propertyMappingExists constraint of '<em>Conversion Argument</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateConversionArgument_propertyMappingExists(ConversionArgument conversionArgument, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return conversionArgument.propertyMappingExists(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateConversionImplementation(ConversionImplementation conversionImplementation, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(conversionImplementation, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateCrossCondition(CrossCondition crossCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftPathIsValid(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftPathGivesUniqueNode(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftFunctionIsValid(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateCrossCondition_rightPathIsValid(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateCrossCondition_rightPathGivesUniqueNode(crossCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateCrossCondition_rightFunctionIsValid(crossCondition, diagnostics, context);
		return result;
	}

	/**
	 * Validates the rightPathIsValid constraint of '<em>Cross Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateCrossCondition_rightPathIsValid(CrossCondition crossCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return crossCondition.rightPathIsValid(diagnostics, context);
	}

	/**
	 * Validates the rightPathGivesUniqueNode constraint of '<em>Cross Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateCrossCondition_rightPathGivesUniqueNode(CrossCondition crossCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return crossCondition.rightPathGivesUniqueNode(diagnostics, context);
	}

	/**
	 * Validates the rightFunctionIsValid constraint of '<em>Cross Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateCrossCondition_rightFunctionIsValid(CrossCondition crossCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return crossCondition.rightFunctionIsValid(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateElementDef(ElementDef elementDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_notBothDefaultAndFixed(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_hasCorrectDefaultOrFixedValue(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_hasCorrectMinMultiplicity(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_inStructureOfContainingElement(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateElementDef_noChildrenIfNotExpanded(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateElementDef_hasAllChildrenIfExpanded(elementDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateElementDef_hasCorrectMaxMultiplicity(elementDef, diagnostics, context);
		return result;
	}

	/**
	 * Validates the noChildrenIfNotExpanded constraint of '<em>Element Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateElementDef_noChildrenIfNotExpanded(ElementDef elementDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return elementDef.noChildrenIfNotExpanded(diagnostics, context);
	}

	/**
	 * Validates the hasAllChildrenIfExpanded constraint of '<em>Element Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateElementDef_hasAllChildrenIfExpanded(ElementDef elementDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return elementDef.hasAllChildrenIfExpanded(diagnostics, context);
	}

	/**
	 * Validates the hasCorrectMaxMultiplicity constraint of '<em>Element Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateElementDef_hasCorrectMaxMultiplicity(ElementDef elementDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return elementDef.hasCorrectMaxMultiplicity(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateFixedPropertyValue(FixedPropertyValue fixedPropertyValue, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(fixedPropertyValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(fixedPropertyValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(fixedPropertyValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(fixedPropertyValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(fixedPropertyValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(fixedPropertyValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(fixedPropertyValue, diagnostics, context);
		if (result || diagnostics != null) result &= validateFixedPropertyValue_classHasProperty(fixedPropertyValue, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classHasProperty constraint of '<em>Fixed Property Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateFixedPropertyValue_classHasProperty(FixedPropertyValue fixedPropertyValue, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return fixedPropertyValue.classHasProperty(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateGlobalMappingParameters(GlobalMappingParameters globalMappingParameters, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(globalMappingParameters, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateImportMappingSet(ImportMappingSet importMappingSet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validateImportMappingSet_canFindMappingSet(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validateImportMappingSet_mappingSetHasSameClassModel(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validateImportMappingSet_mappingSetParametersMatch(importMappingSet, diagnostics, context);
		if (result || diagnostics != null) result &= validateImportMappingSet_mappingSetStructureMatches(importMappingSet, diagnostics, context);
		return result;
	}

	/**
	 * Validates the canFindMappingSet constraint of '<em>Import Mapping Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateImportMappingSet_canFindMappingSet(ImportMappingSet importMappingSet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return importMappingSet.canFindMappingSet(diagnostics, context);
	}

	/**
	 * Validates the mappingSetHasSameClassModel constraint of '<em>Import Mapping Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateImportMappingSet_mappingSetHasSameClassModel(ImportMappingSet importMappingSet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return importMappingSet.mappingSetHasSameClassModel(diagnostics, context);
	}

	/**
	 * Validates the mappingSetParametersMatch constraint of '<em>Import Mapping Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateImportMappingSet_mappingSetParametersMatch(ImportMappingSet importMappingSet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return importMappingSet.mappingSetParametersMatch(diagnostics, context);
	}

	/**
	 * Validates the mappingSetStructureMatches constraint of '<em>Import Mapping Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateImportMappingSet_mappingSetStructureMatches(ImportMappingSet importMappingSet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return importMappingSet.mappingSetStructureMatches(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateJavaConversionImplementation(JavaConversionImplementation javaConversionImplementation, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(javaConversionImplementation, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateLocalPropertyConversion(LocalPropertyConversion localPropertyConversion, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(localPropertyConversion, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMappedStructure(MappedStructure mappedStructure, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(mappedStructure, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(mappedStructure, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(mappedStructure, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(mappedStructure, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(mappedStructure, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(mappedStructure, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(mappedStructure, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappedStructure_canFindClassModel(mappedStructure, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappedStructure_canFindStructureDefinition(mappedStructure, diagnostics, context);
		return result;
	}

	/**
	 * Validates the canFindClassModel constraint of '<em>Mapped Structure</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMappedStructure_canFindClassModel(MappedStructure mappedStructure, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return mappedStructure.canFindClassModel(diagnostics, context);
	}

	/**
	 * Validates the canFindStructureDefinition constraint of '<em>Mapped Structure</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMappedStructure_canFindStructureDefinition(MappedStructure mappedStructure, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return mappedStructure.canFindStructureDefinition(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMapping(Mapping mapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(mapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(mapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(mapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(mapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(mapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(mapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(mapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateMapping_mappedClassIsInClassModel(mapping, diagnostics, context);
		return result;
	}

	/**
	 * Validates the mappedClassIsInClassModel constraint of '<em>Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMapping_mappedClassIsInClassModel(Mapping mapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return mapping.mappedClassIsInClassModel(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMappingCondition(MappingCondition mappingCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftPathIsValid(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftPathGivesUniqueNode(mappingCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftFunctionIsValid(mappingCondition, diagnostics, context);
		return result;
	}

	/**
	 * Validates the leftPathIsValid constraint of '<em>Mapping Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMappingCondition_leftPathIsValid(MappingCondition mappingCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return mappingCondition.leftPathIsValid(diagnostics, context);
	}

	/**
	 * Validates the leftPathGivesUniqueNode constraint of '<em>Mapping Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMappingCondition_leftPathGivesUniqueNode(MappingCondition mappingCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return mappingCondition.leftPathGivesUniqueNode(diagnostics, context);
	}

	/**
	 * Validates the leftFunctionIsValid constraint of '<em>Mapping Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMappingCondition_leftFunctionIsValid(MappingCondition mappingCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return mappingCondition.leftFunctionIsValid(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateModelAssocFilter(ModelAssocFilter modelAssocFilter, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(modelAssocFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(modelAssocFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(modelAssocFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(modelAssocFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(modelAssocFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(modelAssocFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(modelAssocFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validateModelAssocFilter_classHasRoleFromOtherClass(modelAssocFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validateModelAssocFilter_otherObjectMappingExists(modelAssocFilter, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classHasRoleFromOtherClass constraint of '<em>Model Assoc Filter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateModelAssocFilter_classHasRoleFromOtherClass(ModelAssocFilter modelAssocFilter, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return modelAssocFilter.classHasRoleFromOtherClass(diagnostics, context);
	}

	/**
	 * Validates the otherObjectMappingExists constraint of '<em>Model Assoc Filter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateModelAssocFilter_otherObjectMappingExists(ModelAssocFilter modelAssocFilter, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return modelAssocFilter.otherObjectMappingExists(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateModelFilter(ModelFilter modelFilter, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(modelFilter, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateModelFilterSet(ModelFilterSet modelFilterSet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(modelFilterSet, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateModelPropertyFilter(ModelPropertyFilter modelPropertyFilter, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(modelPropertyFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(modelPropertyFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(modelPropertyFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(modelPropertyFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(modelPropertyFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(modelPropertyFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(modelPropertyFilter, diagnostics, context);
		if (result || diagnostics != null) result &= validateModelPropertyFilter_classHasProperty(modelPropertyFilter, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classHasProperty constraint of '<em>Model Property Filter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateModelPropertyFilter_classHasProperty(ModelPropertyFilter modelPropertyFilter, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return modelPropertyFilter.classHasProperty(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNamespace(Namespace namespace, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(namespace, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNodeDef(NodeDef nodeDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_notBothDefaultAndFixed(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_hasCorrectDefaultOrFixedValue(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_hasCorrectMinMultiplicity(nodeDef, diagnostics, context);
		if (result || diagnostics != null) result &= validateNodeDef_inStructureOfContainingElement(nodeDef, diagnostics, context);
		return result;
	}

	/**
	 * Validates the notBothDefaultAndFixed constraint of '<em>Node Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNodeDef_notBothDefaultAndFixed(NodeDef nodeDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return nodeDef.notBothDefaultAndFixed(diagnostics, context);
	}

	/**
	 * Validates the hasCorrectDefaultOrFixedValue constraint of '<em>Node Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNodeDef_hasCorrectDefaultOrFixedValue(NodeDef nodeDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return nodeDef.hasCorrectDefaultOrFixedValue(diagnostics, context);
	}

	/**
	 * Validates the hasCorrectMinMultiplicity constraint of '<em>Node Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNodeDef_hasCorrectMinMultiplicity(NodeDef nodeDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return nodeDef.hasCorrectMinMultiplicity(diagnostics, context);
	}

	/**
	 * Validates the inStructureOfContainingElement constraint of '<em>Node Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNodeDef_inStructureOfContainingElement(NodeDef nodeDef, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return nodeDef.inStructureOfContainingElement(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNodeMappingSet(NodeMappingSet nodeMappingSet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(nodeMappingSet, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNote(Note note, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(note, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateObjMapping(ObjMapping objMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateMapping_mappedClassIsInClassModel(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateObjMapping_subsetIsUniqueWithinClass(objMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateObjMapping_rootPathIsConsistentWithNodePosition(objMapping, diagnostics, context);
		return result;
	}

	/**
	 * Validates the subsetIsUniqueWithinClass constraint of '<em>Obj Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateObjMapping_subsetIsUniqueWithinClass(ObjMapping objMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return objMapping.subsetIsUniqueWithinClass(diagnostics, context);
	}

	/**
	 * Validates the rootPathIsConsistentWithNodePosition constraint of '<em>Obj Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateObjMapping_rootPathIsConsistentWithNodePosition(ObjMapping objMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return objMapping.rootPathIsConsistentWithNodePosition(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateParameterClass(ParameterClass parameterClass, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(parameterClass, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(parameterClass, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(parameterClass, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(parameterClass, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(parameterClass, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(parameterClass, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(parameterClass, diagnostics, context);
		if (result || diagnostics != null) result &= validateParameterClass_classIsInClassModel(parameterClass, diagnostics, context);
		if (result || diagnostics != null) result &= validateParameterClass_objectMappingExists(parameterClass, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classIsInClassModel constraint of '<em>Parameter Class</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateParameterClass_classIsInClassModel(ParameterClass parameterClass, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return parameterClass.classIsInClassModel(diagnostics, context);
	}

	/**
	 * Validates the objectMappingExists constraint of '<em>Parameter Class</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateParameterClass_objectMappingExists(ParameterClass parameterClass, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return parameterClass.objectMappingExists(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateParameterClassValue(ParameterClassValue parameterClassValue, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(parameterClassValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(parameterClassValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(parameterClassValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(parameterClassValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(parameterClassValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(parameterClassValue, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(parameterClassValue, diagnostics, context);
		if (result || diagnostics != null) result &= validateParameterClassValue_mappedClassIsInClassModel(parameterClassValue, diagnostics, context);
		if (result || diagnostics != null) result &= validateParameterClassValue_mappingExistsForParameterClassValue(parameterClassValue, diagnostics, context);
		return result;
	}

	/**
	 * Validates the mappedClassIsInClassModel constraint of '<em>Parameter Class Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateParameterClassValue_mappedClassIsInClassModel(ParameterClassValue parameterClassValue, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return parameterClassValue.mappedClassIsInClassModel(diagnostics, context);
	}

	/**
	 * Validates the mappingExistsForParameterClassValue constraint of '<em>Parameter Class Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateParameterClassValue_mappingExistsForParameterClassValue(ParameterClassValue parameterClassValue, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return parameterClassValue.mappingExistsForParameterClassValue(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePropMapping(PropMapping propMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validateMapping_mappedClassIsInClassModel(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validatePropMapping_classHasProperty(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validatePropMapping_objectMappingExists(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validatePropMapping_objectToPropertyPathIsValid(propMapping, diagnostics, context);
		if (result || diagnostics != null) result &= validatePropMapping_propertyIsUniqueFromObjectNode(propMapping, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classHasProperty constraint of '<em>Prop Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePropMapping_classHasProperty(PropMapping propMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return propMapping.classHasProperty(diagnostics, context);
	}

	/**
	 * Validates the objectMappingExists constraint of '<em>Prop Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePropMapping_objectMappingExists(PropMapping propMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return propMapping.objectMappingExists(diagnostics, context);
	}

	/**
	 * Validates the objectToPropertyPathIsValid constraint of '<em>Prop Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePropMapping_objectToPropertyPathIsValid(PropMapping propMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return propMapping.objectToPropertyPathIsValid(diagnostics, context);
	}

	/**
	 * Validates the propertyIsUniqueFromObjectNode constraint of '<em>Prop Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePropMapping_propertyIsUniqueFromObjectNode(PropMapping propMapping, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return propMapping.propertyIsUniqueFromObjectNode(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePropertyConversion(PropertyConversion propertyConversion, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(propertyConversion, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(propertyConversion, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(propertyConversion, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(propertyConversion, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(propertyConversion, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(propertyConversion, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(propertyConversion, diagnostics, context);
		if (result || diagnostics != null) result &= validatePropertyConversion_classHasResultProperty(propertyConversion, diagnostics, context);
		if (result || diagnostics != null) result &= validatePropertyConversion_hasImplementation(propertyConversion, diagnostics, context);
		return result;
	}

	/**
	 * Validates the classHasResultProperty constraint of '<em>Property Conversion</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePropertyConversion_classHasResultProperty(PropertyConversion propertyConversion, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return propertyConversion.classHasResultProperty(diagnostics, context);
	}

	/**
	 * Validates the hasImplementation constraint of '<em>Property Conversion</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePropertyConversion_hasImplementation(PropertyConversion propertyConversion, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return propertyConversion.hasImplementation(diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateValueCondition(ValueCondition valueCondition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validate_EveryMultiplicityConforms(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftPathIsValid(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftPathGivesUniqueNode(valueCondition, diagnostics, context);
		if (result || diagnostics != null) result &= validateMappingCondition_leftFunctionIsValid(valueCondition, diagnostics, context);
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateXSLTConversionImplementation(XSLTConversionImplementation xsltConversionImplementation, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(xsltConversionImplementation, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateValuePair(ValuePair valuePair, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(valuePair, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateConditionTest(ConditionTest conditionTest, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateConversionSense(ConversionSense conversionSense, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMaxMult(MaxMult maxMult, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMinMult(MinMult minMult, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMultiWay(MultiWay multiWay, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateStructureType(StructureType structureType, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateDiagnosticChain(DiagnosticChain diagnosticChain, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateMap(Map<?, ?> map, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	}

	/**
	 * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		// TODO
		// Specialize this to return a resource locator for messages specific to this validator.
		// Ensure that you remove @generated or mark it @generated NOT
		return super.getResourceLocator();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	/*public boolean validateMap(Map map, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return true;
	} */

} //MapperValidator
