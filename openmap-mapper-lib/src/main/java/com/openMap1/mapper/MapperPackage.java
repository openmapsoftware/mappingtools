/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.openMap1.mapper.MapperFactory
 * @model kind="package"
 * @generated
 */
public interface MapperPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "mapper";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http:///com/openMap1/mapper.ecore";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "com.openMap1.mapper";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MapperPackage eINSTANCE = com.openMap1.mapper.impl.MapperPackageImpl.init();

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.AnnotationsImpl <em>Annotations</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.AnnotationsImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getAnnotations()
	 * @generated
	 */
	int ANNOTATIONS = 0;

	/**
	 * The feature id for the '<em><b>Notes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATIONS__NOTES = 0;

	/**
	 * The number of structural features of the '<em>Annotations</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATIONS_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.MappingImpl <em>Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.MappingImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMapping()
	 * @generated
	 */
	int MAPPING = 15;

	/**
	 * The feature id for the '<em><b>Mapped Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__MAPPED_CLASS = 0;

	/**
	 * The feature id for the '<em><b>Mapped Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__MAPPED_PACKAGE = 1;

	/**
	 * The feature id for the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__SUBSET = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Mapping Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__MAPPING_CONDITIONS = 4;

	/**
	 * The feature id for the '<em><b>Multi Way</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__MULTI_WAY = 5;

	/**
	 * The feature id for the '<em><b>Break Point</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__BREAK_POINT = 6;

	/**
	 * The number of structural features of the '<em>Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.AssocEndMappingImpl <em>Assoc End Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.AssocEndMappingImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getAssocEndMapping()
	 * @generated
	 */
	int ASSOC_END_MAPPING = 1;

	/**
	 * The feature id for the '<em><b>Mapped Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__MAPPED_CLASS = MAPPING__MAPPED_CLASS;

	/**
	 * The feature id for the '<em><b>Mapped Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__MAPPED_PACKAGE = MAPPING__MAPPED_PACKAGE;

	/**
	 * The feature id for the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__SUBSET = MAPPING__SUBSET;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__DESCRIPTION = MAPPING__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Mapping Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__MAPPING_CONDITIONS = MAPPING__MAPPING_CONDITIONS;

	/**
	 * The feature id for the '<em><b>Multi Way</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__MULTI_WAY = MAPPING__MULTI_WAY;

	/**
	 * The feature id for the '<em><b>Break Point</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__BREAK_POINT = MAPPING__BREAK_POINT;

	/**
	 * The feature id for the '<em><b>Mapped Role</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__MAPPED_ROLE = MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Object To Association Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH = MAPPING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Association To Object Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH = MAPPING_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Required For Object</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING__REQUIRED_FOR_OBJECT = MAPPING_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Assoc End Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_END_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.AssocMappingImpl <em>Assoc Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.AssocMappingImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getAssocMapping()
	 * @generated
	 */
	int ASSOC_MAPPING = 2;

	/**
	 * The feature id for the '<em><b>Mapped Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__MAPPED_CLASS = MAPPING__MAPPED_CLASS;

	/**
	 * The feature id for the '<em><b>Mapped Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__MAPPED_PACKAGE = MAPPING__MAPPED_PACKAGE;

	/**
	 * The feature id for the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__SUBSET = MAPPING__SUBSET;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__DESCRIPTION = MAPPING__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Mapping Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__MAPPING_CONDITIONS = MAPPING__MAPPING_CONDITIONS;

	/**
	 * The feature id for the '<em><b>Multi Way</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__MULTI_WAY = MAPPING__MULTI_WAY;

	/**
	 * The feature id for the '<em><b>Break Point</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__BREAK_POINT = MAPPING__BREAK_POINT;

	/**
	 * The feature id for the '<em><b>Mapped End1</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__MAPPED_END1 = MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Mapped End2</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING__MAPPED_END2 = MAPPING_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Assoc Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSOC_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.MappedStructureImpl <em>Mapped Structure</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.MappedStructureImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMappedStructure()
	 * @generated
	 */
	int MAPPED_STRUCTURE = 14;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.MappingConditionImpl <em>Mapping Condition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.MappingConditionImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMappingCondition()
	 * @generated
	 */
	int MAPPING_CONDITION = 16;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.NodeDefImpl <em>Node Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.NodeDefImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getNodeDef()
	 * @generated
	 */
	int NODE_DEF = 22;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF__NAME = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF__TYPE = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Min Multiplicity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF__MIN_MULTIPLICITY = 3;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF__DEFAULT_VALUE = 4;

	/**
	 * The feature id for the '<em><b>Fixed Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF__FIXED_VALUE = 5;

	/**
	 * The feature id for the '<em><b>Node Mapping Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF__NODE_MAPPING_SET = 6;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF__ANNOTATIONS = 7;

	/**
	 * The number of structural features of the '<em>Node Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_DEF_FEATURE_COUNT = 8;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.AttributeDefImpl <em>Attribute Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.AttributeDefImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getAttributeDef()
	 * @generated
	 */
	int ATTRIBUTE_DEF = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF__NAME = NODE_DEF__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF__TYPE = NODE_DEF__TYPE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF__DESCRIPTION = NODE_DEF__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Min Multiplicity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF__MIN_MULTIPLICITY = NODE_DEF__MIN_MULTIPLICITY;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF__DEFAULT_VALUE = NODE_DEF__DEFAULT_VALUE;

	/**
	 * The feature id for the '<em><b>Fixed Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF__FIXED_VALUE = NODE_DEF__FIXED_VALUE;

	/**
	 * The feature id for the '<em><b>Node Mapping Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF__NODE_MAPPING_SET = NODE_DEF__NODE_MAPPING_SET;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF__ANNOTATIONS = NODE_DEF__ANNOTATIONS;

	/**
	 * The number of structural features of the '<em>Attribute Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_DEF_FEATURE_COUNT = NODE_DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ClassDetailsImpl <em>Class Details</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ClassDetailsImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getClassDetails()
	 * @generated
	 */
	int CLASS_DETAILS = 4;

	/**
	 * The feature id for the '<em><b>Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_DETAILS__CLASS_NAME = 0;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_DETAILS__PACKAGE_NAME = 1;

	/**
	 * The feature id for the '<em><b>Property Conversions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_DETAILS__PROPERTY_CONVERSIONS = 2;

	/**
	 * The number of structural features of the '<em>Class Details</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_DETAILS_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ConversionArgumentImpl <em>Conversion Argument</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ConversionArgumentImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getConversionArgument()
	 * @generated
	 */
	int CONVERSION_ARGUMENT = 5;

	/**
	 * The feature id for the '<em><b>Property Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERSION_ARGUMENT__PROPERTY_NAME = 0;

	/**
	 * The number of structural features of the '<em>Conversion Argument</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERSION_ARGUMENT_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ConversionImplementationImpl <em>Conversion Implementation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ConversionImplementationImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getConversionImplementation()
	 * @generated
	 */
	int CONVERSION_IMPLEMENTATION = 6;

	/**
	 * The number of structural features of the '<em>Conversion Implementation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONVERSION_IMPLEMENTATION_FEATURE_COUNT = 0;

	/**
	 * The feature id for the '<em><b>Left Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_CONDITION__LEFT_PATH = 0;

	/**
	 * The feature id for the '<em><b>Left Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_CONDITION__LEFT_FUNCTION = 1;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ElementDefImpl <em>Element Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ElementDefImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getElementDef()
	 * @generated
	 */
	int ELEMENT_DEF = 8;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.FixedPropertyValueImpl <em>Fixed Property Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.FixedPropertyValueImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getFixedPropertyValue()
	 * @generated
	 */
	int FIXED_PROPERTY_VALUE = 9;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.GlobalMappingParametersImpl <em>Global Mapping Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.GlobalMappingParametersImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getGlobalMappingParameters()
	 * @generated
	 */
	int GLOBAL_MAPPING_PARAMETERS = 10;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.JavaConversionImplementationImpl <em>Java Conversion Implementation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.JavaConversionImplementationImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getJavaConversionImplementation()
	 * @generated
	 */
	int JAVA_CONVERSION_IMPLEMENTATION = 12;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ModelFilterImpl <em>Model Filter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ModelFilterImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getModelFilter()
	 * @generated
	 */
	int MODEL_FILTER = 18;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ModelAssocFilterImpl <em>Model Assoc Filter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ModelAssocFilterImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getModelAssocFilter()
	 * @generated
	 */
	int MODEL_ASSOC_FILTER = 17;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ModelFilterSetImpl <em>Model Filter Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ModelFilterSetImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getModelFilterSet()
	 * @generated
	 */
	int MODEL_FILTER_SET = 19;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ModelPropertyFilterImpl <em>Model Property Filter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ModelPropertyFilterImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getModelPropertyFilter()
	 * @generated
	 */
	int MODEL_PROPERTY_FILTER = 20;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.NamespaceImpl <em>Namespace</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.NamespaceImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getNamespace()
	 * @generated
	 */
	int NAMESPACE = 21;

	/**
	 * The feature id for the '<em><b>Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_CONDITION__TEST = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_CONDITION__DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Left Path Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_CONDITION__LEFT_PATH_CONDITIONS = 4;

	/**
	 * The number of structural features of the '<em>Mapping Condition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_CONDITION_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.CrossConditionImpl <em>Cross Condition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.CrossConditionImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getCrossCondition()
	 * @generated
	 */
	int CROSS_CONDITION = 7;

	/**
	 * The feature id for the '<em><b>Left Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION__LEFT_PATH = MAPPING_CONDITION__LEFT_PATH;

	/**
	 * The feature id for the '<em><b>Left Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION__LEFT_FUNCTION = MAPPING_CONDITION__LEFT_FUNCTION;

	/**
	 * The feature id for the '<em><b>Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION__TEST = MAPPING_CONDITION__TEST;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION__DESCRIPTION = MAPPING_CONDITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Left Path Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION__LEFT_PATH_CONDITIONS = MAPPING_CONDITION__LEFT_PATH_CONDITIONS;

	/**
	 * The feature id for the '<em><b>Right Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION__RIGHT_PATH = MAPPING_CONDITION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Right Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION__RIGHT_FUNCTION = MAPPING_CONDITION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Right Path Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION__RIGHT_PATH_CONDITIONS = MAPPING_CONDITION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Cross Condition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CROSS_CONDITION_FEATURE_COUNT = MAPPING_CONDITION_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__NAME = NODE_DEF__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__TYPE = NODE_DEF__TYPE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__DESCRIPTION = NODE_DEF__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Min Multiplicity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__MIN_MULTIPLICITY = NODE_DEF__MIN_MULTIPLICITY;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__DEFAULT_VALUE = NODE_DEF__DEFAULT_VALUE;

	/**
	 * The feature id for the '<em><b>Fixed Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__FIXED_VALUE = NODE_DEF__FIXED_VALUE;

	/**
	 * The feature id for the '<em><b>Node Mapping Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__NODE_MAPPING_SET = NODE_DEF__NODE_MAPPING_SET;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__ANNOTATIONS = NODE_DEF__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Expanded</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__EXPANDED = NODE_DEF_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Max Multiplicity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__MAX_MULTIPLICITY = NODE_DEF_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Child Elements</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__CHILD_ELEMENTS = NODE_DEF_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Attribute Defs</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__ATTRIBUTE_DEFS = NODE_DEF_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Import Mapping Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF__IMPORT_MAPPING_SET = NODE_DEF_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Element Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_DEF_FEATURE_COUNT = NODE_DEF_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Mapped Property</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIXED_PROPERTY_VALUE__MAPPED_PROPERTY = 0;

	/**
	 * The feature id for the '<em><b>Fixed Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIXED_PROPERTY_VALUE__FIXED_VALUE = 1;

	/**
	 * The feature id for the '<em><b>Value Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIXED_PROPERTY_VALUE__VALUE_TYPE = 2;

	/**
	 * The number of structural features of the '<em>Fixed Property Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIXED_PROPERTY_VALUE_FEATURE_COUNT = 3;

	/**
	 * The feature id for the '<em><b>Mapping Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_MAPPING_PARAMETERS__MAPPING_CLASS = 0;

	/**
	 * The feature id for the '<em><b>Wrapper Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_MAPPING_PARAMETERS__WRAPPER_CLASS = 1;

	/**
	 * The feature id for the '<em><b>Name Spaces</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_MAPPING_PARAMETERS__NAME_SPACES = 2;

	/**
	 * The feature id for the '<em><b>Class Details</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS = 3;

	/**
	 * The number of structural features of the '<em>Global Mapping Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_MAPPING_PARAMETERS_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ImportMappingSetImpl <em>Import Mapping Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ImportMappingSetImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getImportMappingSet()
	 * @generated
	 */
	int IMPORT_MAPPING_SET = 11;

	/**
	 * The feature id for the '<em><b>Mapping Set URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_MAPPING_SET__MAPPING_SET_URI = 0;

	/**
	 * The feature id for the '<em><b>Parameter Class Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES = 1;

	/**
	 * The number of structural features of the '<em>Import Mapping Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_MAPPING_SET_FEATURE_COUNT = 2;

	/**
	 * The feature id for the '<em><b>Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA_CONVERSION_IMPLEMENTATION__CLASS_NAME = CONVERSION_IMPLEMENTATION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA_CONVERSION_IMPLEMENTATION__METHOD_NAME = CONVERSION_IMPLEMENTATION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA_CONVERSION_IMPLEMENTATION__PACKAGE_NAME = CONVERSION_IMPLEMENTATION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Java Conversion Implementation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JAVA_CONVERSION_IMPLEMENTATION_FEATURE_COUNT = CONVERSION_IMPLEMENTATION_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.LocalPropertyConversionImpl <em>Local Property Conversion</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.LocalPropertyConversionImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getLocalPropertyConversion()
	 * @generated
	 */
	int LOCAL_PROPERTY_CONVERSION = 13;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_PROPERTY_CONVERSION__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>In Conversion Implementations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS = 1;

	/**
	 * The feature id for the '<em><b>Out Conversion Implementations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS = 2;

	/**
	 * The feature id for the '<em><b>Value Pairs</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS = 3;

	/**
	 * The number of structural features of the '<em>Local Property Conversion</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_PROPERTY_CONVERSION_FEATURE_COUNT = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Root Element</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__ROOT_ELEMENT = 1;

	/**
	 * The feature id for the '<em><b>UML Model URL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__UML_MODEL_URL = 2;

	/**
	 * The feature id for the '<em><b>Structure Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__STRUCTURE_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Structure URL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__STRUCTURE_URL = 4;

	/**
	 * The feature id for the '<em><b>Top Element Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__TOP_ELEMENT_TYPE = 5;

	/**
	 * The feature id for the '<em><b>Top Element Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__TOP_ELEMENT_NAME = 6;

	/**
	 * The feature id for the '<em><b>Mapping Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__MAPPING_PARAMETERS = 7;

	/**
	 * The feature id for the '<em><b>Parameter Classes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE__PARAMETER_CLASSES = 8;

	/**
	 * The number of structural features of the '<em>Mapped Structure</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPED_STRUCTURE_FEATURE_COUNT = 9;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FILTER__DESCRIPTION = 0;

	/**
	 * The number of structural features of the '<em>Model Filter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FILTER_FEATURE_COUNT = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_ASSOC_FILTER__DESCRIPTION = MODEL_FILTER__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Role Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_ASSOC_FILTER__ROLE_NAME = MODEL_FILTER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Other Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_ASSOC_FILTER__OTHER_CLASS_NAME = MODEL_FILTER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Other Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_ASSOC_FILTER__OTHER_PACKAGE_NAME = MODEL_FILTER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Other Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_ASSOC_FILTER__OTHER_SUBSET = MODEL_FILTER_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Model Assoc Filter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_ASSOC_FILTER_FEATURE_COUNT = MODEL_FILTER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FILTER_SET__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Model Filters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FILTER_SET__MODEL_FILTERS = 1;

	/**
	 * The number of structural features of the '<em>Model Filter Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FILTER_SET_FEATURE_COUNT = 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_PROPERTY_FILTER__DESCRIPTION = MODEL_FILTER__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Property Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_PROPERTY_FILTER__PROPERTY_NAME = MODEL_FILTER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_PROPERTY_FILTER__VALUE = MODEL_FILTER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_PROPERTY_FILTER__TEST = MODEL_FILTER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Model Property Filter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_PROPERTY_FILTER_FEATURE_COUNT = MODEL_FILTER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>URL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMESPACE__URL = 0;

	/**
	 * The feature id for the '<em><b>Prefix</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMESPACE__PREFIX = 1;

	/**
	 * The number of structural features of the '<em>Namespace</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMESPACE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.NodeMappingSetImpl <em>Node Mapping Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.NodeMappingSetImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getNodeMappingSet()
	 * @generated
	 */
	int NODE_MAPPING_SET = 23;

	/**
	 * The feature id for the '<em><b>Object Mappings</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_MAPPING_SET__OBJECT_MAPPINGS = 0;

	/**
	 * The feature id for the '<em><b>Property Mappings</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_MAPPING_SET__PROPERTY_MAPPINGS = 1;

	/**
	 * The feature id for the '<em><b>Association Mappings</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_MAPPING_SET__ASSOCIATION_MAPPINGS = 2;

	/**
	 * The number of structural features of the '<em>Node Mapping Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_MAPPING_SET_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.NoteImpl <em>Note</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.NoteImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getNote()
	 * @generated
	 */
	int NOTE = 24;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Note</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ObjMappingImpl <em>Obj Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ObjMappingImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getObjMapping()
	 * @generated
	 */
	int OBJ_MAPPING = 25;

	/**
	 * The feature id for the '<em><b>Mapped Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__MAPPED_CLASS = MAPPING__MAPPED_CLASS;

	/**
	 * The feature id for the '<em><b>Mapped Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__MAPPED_PACKAGE = MAPPING__MAPPED_PACKAGE;

	/**
	 * The feature id for the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__SUBSET = MAPPING__SUBSET;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__DESCRIPTION = MAPPING__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Mapping Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__MAPPING_CONDITIONS = MAPPING__MAPPING_CONDITIONS;

	/**
	 * The feature id for the '<em><b>Multi Way</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__MULTI_WAY = MAPPING__MULTI_WAY;

	/**
	 * The feature id for the '<em><b>Break Point</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__BREAK_POINT = MAPPING__BREAK_POINT;

	/**
	 * The feature id for the '<em><b>Root Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__ROOT_PATH = MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Multiply Represented</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__MULTIPLY_REPRESENTED = MAPPING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Model Filter Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__MODEL_FILTER_SET = MAPPING_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Fixed Property Values</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING__FIXED_PROPERTY_VALUES = MAPPING_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Obj Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJ_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ParameterClassImpl <em>Parameter Class</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ParameterClassImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getParameterClass()
	 * @generated
	 */
	int PARAMETER_CLASS = 26;

	/**
	 * The feature id for the '<em><b>Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS__CLASS_NAME = 0;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS__PACKAGE_NAME = 1;

	/**
	 * The feature id for the '<em><b>Parameter Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS__PARAMETER_INDEX = 2;

	/**
	 * The number of structural features of the '<em>Parameter Class</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ParameterClassValueImpl <em>Parameter Class Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ParameterClassValueImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getParameterClassValue()
	 * @generated
	 */
	int PARAMETER_CLASS_VALUE = 27;

	/**
	 * The feature id for the '<em><b>Mapped Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS_VALUE__MAPPED_CLASS = 0;

	/**
	 * The feature id for the '<em><b>Mapped Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS_VALUE__MAPPED_PACKAGE = 1;

	/**
	 * The feature id for the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS_VALUE__SUBSET = 2;

	/**
	 * The feature id for the '<em><b>Parameter Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS_VALUE__PARAMETER_INDEX = 3;

	/**
	 * The number of structural features of the '<em>Parameter Class Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_CLASS_VALUE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.PropMappingImpl <em>Prop Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.PropMappingImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getPropMapping()
	 * @generated
	 */
	int PROP_MAPPING = 28;

	/**
	 * The feature id for the '<em><b>Mapped Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__MAPPED_CLASS = MAPPING__MAPPED_CLASS;

	/**
	 * The feature id for the '<em><b>Mapped Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__MAPPED_PACKAGE = MAPPING__MAPPED_PACKAGE;

	/**
	 * The feature id for the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__SUBSET = MAPPING__SUBSET;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__DESCRIPTION = MAPPING__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Mapping Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__MAPPING_CONDITIONS = MAPPING__MAPPING_CONDITIONS;

	/**
	 * The feature id for the '<em><b>Multi Way</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__MULTI_WAY = MAPPING__MULTI_WAY;

	/**
	 * The feature id for the '<em><b>Break Point</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__BREAK_POINT = MAPPING__BREAK_POINT;

	/**
	 * The feature id for the '<em><b>Mapped Property</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__MAPPED_PROPERTY = MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Property Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__PROPERTY_TYPE = MAPPING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__DEFAULT_VALUE = MAPPING_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Object To Property Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__OBJECT_TO_PROPERTY_PATH = MAPPING_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Local Property Conversion</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING__LOCAL_PROPERTY_CONVERSION = MAPPING_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Prop Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROP_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.PropertyConversionImpl <em>Property Conversion</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.PropertyConversionImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getPropertyConversion()
	 * @generated
	 */
	int PROPERTY_CONVERSION = 29;

	/**
	 * The feature id for the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_CONVERSION__SUBSET = 0;

	/**
	 * The feature id for the '<em><b>Result Slot</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_CONVERSION__RESULT_SLOT = 1;

	/**
	 * The feature id for the '<em><b>Sense</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_CONVERSION__SENSE = 2;

	/**
	 * The feature id for the '<em><b>Conversion Implementations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS = 3;

	/**
	 * The feature id for the '<em><b>Conversion Arguments</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_CONVERSION__CONVERSION_ARGUMENTS = 4;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_CONVERSION__DESCRIPTION = 5;

	/**
	 * The number of structural features of the '<em>Property Conversion</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_CONVERSION_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ValueConditionImpl <em>Value Condition</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ValueConditionImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getValueCondition()
	 * @generated
	 */
	int VALUE_CONDITION = 30;

	/**
	 * The feature id for the '<em><b>Left Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_CONDITION__LEFT_PATH = MAPPING_CONDITION__LEFT_PATH;

	/**
	 * The feature id for the '<em><b>Left Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_CONDITION__LEFT_FUNCTION = MAPPING_CONDITION__LEFT_FUNCTION;

	/**
	 * The feature id for the '<em><b>Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_CONDITION__TEST = MAPPING_CONDITION__TEST;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_CONDITION__DESCRIPTION = MAPPING_CONDITION__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Left Path Conditions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_CONDITION__LEFT_PATH_CONDITIONS = MAPPING_CONDITION__LEFT_PATH_CONDITIONS;

	/**
	 * The feature id for the '<em><b>Right Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_CONDITION__RIGHT_VALUE = MAPPING_CONDITION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Value Condition</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_CONDITION_FEATURE_COUNT = MAPPING_CONDITION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.XSLTConversionImplementationImpl <em>XSLT Conversion Implementation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.XSLTConversionImplementationImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getXSLTConversionImplementation()
	 * @generated
	 */
	int XSLT_CONVERSION_IMPLEMENTATION = 32;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.impl.ValuePairImpl <em>Value Pair</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.impl.ValuePairImpl
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getValuePair()
	 * @generated
	 */
	int VALUE_PAIR = 31;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_PAIR__DESCRIPTION = 0;

	/**
	 * The feature id for the '<em><b>Structure Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_PAIR__STRUCTURE_VALUE = 1;

	/**
	 * The feature id for the '<em><b>Model Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_PAIR__MODEL_VALUE = 2;

	/**
	 * The feature id for the '<em><b>Preferred In</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_PAIR__PREFERRED_IN = 3;

	/**
	 * The feature id for the '<em><b>Preferred Out</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_PAIR__PREFERRED_OUT = 4;

	/**
	 * The number of structural features of the '<em>Value Pair</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_PAIR_FEATURE_COUNT = 5;

	/**
	 * The feature id for the '<em><b>Template Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_NAME = CONVERSION_IMPLEMENTATION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Template File URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_FILE_URI = CONVERSION_IMPLEMENTATION_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>XSLT Conversion Implementation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int XSLT_CONVERSION_IMPLEMENTATION_FEATURE_COUNT = CONVERSION_IMPLEMENTATION_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.ConditionTest <em>Condition Test</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.ConditionTest
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getConditionTest()
	 * @generated
	 */
	int CONDITION_TEST = 33;


	/**
	 * The meta object id for the '{@link com.openMap1.mapper.ConversionSense <em>Conversion Sense</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.ConversionSense
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getConversionSense()
	 * @generated
	 */
	int CONVERSION_SENSE = 34;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.MaxMult <em>Max Mult</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.MaxMult
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMaxMult()
	 * @generated
	 */
	int MAX_MULT = 35;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.MinMult <em>Min Mult</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.MinMult
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMinMult()
	 * @generated
	 */
	int MIN_MULT = 36;


	/**
	 * The meta object id for the '{@link com.openMap1.mapper.MultiWay <em>Multi Way</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.MultiWay
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMultiWay()
	 * @generated
	 */
	int MULTI_WAY = 37;

	/**
	 * The meta object id for the '{@link com.openMap1.mapper.StructureType <em>Structure Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.openMap1.mapper.StructureType
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getStructureType()
	 * @generated
	 */
	int STRUCTURE_TYPE = 38;


	/**
	 * The meta object id for the '<em>Diagnostic Chain</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.DiagnosticChain
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getDiagnosticChain()
	 * @generated
	 */
	int DIAGNOSTIC_CHAIN = 39;

	/**
	 * The meta object id for the '<em>Map</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.Map
	 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMap()
	 * @generated
	 */
	int MAP = 40;


	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.Annotations <em>Annotations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Annotations</em>'.
	 * @see com.openMap1.mapper.Annotations
	 * @generated
	 */
	EClass getAnnotations();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.Annotations#getNotes <em>Notes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Notes</em>'.
	 * @see com.openMap1.mapper.Annotations#getNotes()
	 * @see #getAnnotations()
	 * @generated
	 */
	EReference getAnnotations_Notes();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.AssocEndMapping <em>Assoc End Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Assoc End Mapping</em>'.
	 * @see com.openMap1.mapper.AssocEndMapping
	 * @generated
	 */
	EClass getAssocEndMapping();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.AssocEndMapping#getMappedRole <em>Mapped Role</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapped Role</em>'.
	 * @see com.openMap1.mapper.AssocEndMapping#getMappedRole()
	 * @see #getAssocEndMapping()
	 * @generated
	 */
	EAttribute getAssocEndMapping_MappedRole();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.AssocEndMapping#getObjectToAssociationPath <em>Object To Association Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Object To Association Path</em>'.
	 * @see com.openMap1.mapper.AssocEndMapping#getObjectToAssociationPath()
	 * @see #getAssocEndMapping()
	 * @generated
	 */
	EAttribute getAssocEndMapping_ObjectToAssociationPath();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.AssocEndMapping#getAssociationToObjectPath <em>Association To Object Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Association To Object Path</em>'.
	 * @see com.openMap1.mapper.AssocEndMapping#getAssociationToObjectPath()
	 * @see #getAssocEndMapping()
	 * @generated
	 */
	EAttribute getAssocEndMapping_AssociationToObjectPath();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.AssocEndMapping#isRequiredForObject <em>Required For Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required For Object</em>'.
	 * @see com.openMap1.mapper.AssocEndMapping#isRequiredForObject()
	 * @see #getAssocEndMapping()
	 * @generated
	 */
	EAttribute getAssocEndMapping_RequiredForObject();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.AssocMapping <em>Assoc Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Assoc Mapping</em>'.
	 * @see com.openMap1.mapper.AssocMapping
	 * @generated
	 */
	EClass getAssocMapping();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.AssocMapping#getMappedEnd1 <em>Mapped End1</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Mapped End1</em>'.
	 * @see com.openMap1.mapper.AssocMapping#getMappedEnd1()
	 * @see #getAssocMapping()
	 * @generated
	 */
	EReference getAssocMapping_MappedEnd1();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.AssocMapping#getMappedEnd2 <em>Mapped End2</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Mapped End2</em>'.
	 * @see com.openMap1.mapper.AssocMapping#getMappedEnd2()
	 * @see #getAssocMapping()
	 * @generated
	 */
	EReference getAssocMapping_MappedEnd2();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.AttributeDef <em>Attribute Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute Def</em>'.
	 * @see com.openMap1.mapper.AttributeDef
	 * @generated
	 */
	EClass getAttributeDef();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ClassDetails <em>Class Details</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class Details</em>'.
	 * @see com.openMap1.mapper.ClassDetails
	 * @generated
	 */
	EClass getClassDetails();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ClassDetails#getClassName <em>Class Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class Name</em>'.
	 * @see com.openMap1.mapper.ClassDetails#getClassName()
	 * @see #getClassDetails()
	 * @generated
	 */
	EAttribute getClassDetails_ClassName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ClassDetails#getPackageName <em>Package Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Name</em>'.
	 * @see com.openMap1.mapper.ClassDetails#getPackageName()
	 * @see #getClassDetails()
	 * @generated
	 */
	EAttribute getClassDetails_PackageName();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.ClassDetails#getPropertyConversions <em>Property Conversions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Property Conversions</em>'.
	 * @see com.openMap1.mapper.ClassDetails#getPropertyConversions()
	 * @see #getClassDetails()
	 * @generated
	 */
	EReference getClassDetails_PropertyConversions();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ConversionArgument <em>Conversion Argument</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Conversion Argument</em>'.
	 * @see com.openMap1.mapper.ConversionArgument
	 * @generated
	 */
	EClass getConversionArgument();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ConversionArgument#getPropertyName <em>Property Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Property Name</em>'.
	 * @see com.openMap1.mapper.ConversionArgument#getPropertyName()
	 * @see #getConversionArgument()
	 * @generated
	 */
	EAttribute getConversionArgument_PropertyName();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ConversionImplementation <em>Conversion Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Conversion Implementation</em>'.
	 * @see com.openMap1.mapper.ConversionImplementation
	 * @generated
	 */
	EClass getConversionImplementation();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.CrossCondition <em>Cross Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cross Condition</em>'.
	 * @see com.openMap1.mapper.CrossCondition
	 * @generated
	 */
	EClass getCrossCondition();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.CrossCondition#getRightPath <em>Right Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Right Path</em>'.
	 * @see com.openMap1.mapper.CrossCondition#getRightPath()
	 * @see #getCrossCondition()
	 * @generated
	 */
	EAttribute getCrossCondition_RightPath();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.CrossCondition#getRightFunction <em>Right Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Right Function</em>'.
	 * @see com.openMap1.mapper.CrossCondition#getRightFunction()
	 * @see #getCrossCondition()
	 * @generated
	 */
	EAttribute getCrossCondition_RightFunction();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.CrossCondition#getRightPathConditions <em>Right Path Conditions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Right Path Conditions</em>'.
	 * @see com.openMap1.mapper.CrossCondition#getRightPathConditions()
	 * @see #getCrossCondition()
	 * @generated
	 */
	EReference getCrossCondition_RightPathConditions();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ElementDef <em>Element Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Def</em>'.
	 * @see com.openMap1.mapper.ElementDef
	 * @generated
	 */
	EClass getElementDef();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ElementDef#isExpanded <em>Expanded</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expanded</em>'.
	 * @see com.openMap1.mapper.ElementDef#isExpanded()
	 * @see #getElementDef()
	 * @generated
	 */
	EAttribute getElementDef_Expanded();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ElementDef#getMaxMultiplicity <em>Max Multiplicity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Multiplicity</em>'.
	 * @see com.openMap1.mapper.ElementDef#getMaxMultiplicity()
	 * @see #getElementDef()
	 * @generated
	 */
	EAttribute getElementDef_MaxMultiplicity();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.ElementDef#getChildElements <em>Child Elements</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Child Elements</em>'.
	 * @see com.openMap1.mapper.ElementDef#getChildElements()
	 * @see #getElementDef()
	 * @generated
	 */
	EReference getElementDef_ChildElements();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.ElementDef#getAttributeDefs <em>Attribute Defs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attribute Defs</em>'.
	 * @see com.openMap1.mapper.ElementDef#getAttributeDefs()
	 * @see #getElementDef()
	 * @generated
	 */
	EReference getElementDef_AttributeDefs();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.ElementDef#getImportMappingSet <em>Import Mapping Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Import Mapping Set</em>'.
	 * @see com.openMap1.mapper.ElementDef#getImportMappingSet()
	 * @see #getElementDef()
	 * @generated
	 */
	EReference getElementDef_ImportMappingSet();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.FixedPropertyValue <em>Fixed Property Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Fixed Property Value</em>'.
	 * @see com.openMap1.mapper.FixedPropertyValue
	 * @generated
	 */
	EClass getFixedPropertyValue();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.FixedPropertyValue#getMappedProperty <em>Mapped Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapped Property</em>'.
	 * @see com.openMap1.mapper.FixedPropertyValue#getMappedProperty()
	 * @see #getFixedPropertyValue()
	 * @generated
	 */
	EAttribute getFixedPropertyValue_MappedProperty();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.FixedPropertyValue#getFixedValue <em>Fixed Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fixed Value</em>'.
	 * @see com.openMap1.mapper.FixedPropertyValue#getFixedValue()
	 * @see #getFixedPropertyValue()
	 * @generated
	 */
	EAttribute getFixedPropertyValue_FixedValue();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.FixedPropertyValue#getValueType <em>Value Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Type</em>'.
	 * @see com.openMap1.mapper.FixedPropertyValue#getValueType()
	 * @see #getFixedPropertyValue()
	 * @generated
	 */
	EAttribute getFixedPropertyValue_ValueType();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.GlobalMappingParameters <em>Global Mapping Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Global Mapping Parameters</em>'.
	 * @see com.openMap1.mapper.GlobalMappingParameters
	 * @generated
	 */
	EClass getGlobalMappingParameters();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.GlobalMappingParameters#getMappingClass <em>Mapping Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapping Class</em>'.
	 * @see com.openMap1.mapper.GlobalMappingParameters#getMappingClass()
	 * @see #getGlobalMappingParameters()
	 * @generated
	 */
	EAttribute getGlobalMappingParameters_MappingClass();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.GlobalMappingParameters#getWrapperClass <em>Wrapper Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wrapper Class</em>'.
	 * @see com.openMap1.mapper.GlobalMappingParameters#getWrapperClass()
	 * @see #getGlobalMappingParameters()
	 * @generated
	 */
	EAttribute getGlobalMappingParameters_WrapperClass();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.GlobalMappingParameters#getNameSpaces <em>Name Spaces</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Name Spaces</em>'.
	 * @see com.openMap1.mapper.GlobalMappingParameters#getNameSpaces()
	 * @see #getGlobalMappingParameters()
	 * @generated
	 */
	EReference getGlobalMappingParameters_NameSpaces();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.GlobalMappingParameters#getClassDetails <em>Class Details</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Class Details</em>'.
	 * @see com.openMap1.mapper.GlobalMappingParameters#getClassDetails()
	 * @see #getGlobalMappingParameters()
	 * @generated
	 */
	EReference getGlobalMappingParameters_ClassDetails();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ImportMappingSet <em>Import Mapping Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Import Mapping Set</em>'.
	 * @see com.openMap1.mapper.ImportMappingSet
	 * @generated
	 */
	EClass getImportMappingSet();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ImportMappingSet#getMappingSetURI <em>Mapping Set URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapping Set URI</em>'.
	 * @see com.openMap1.mapper.ImportMappingSet#getMappingSetURI()
	 * @see #getImportMappingSet()
	 * @generated
	 */
	EAttribute getImportMappingSet_MappingSetURI();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.ImportMappingSet#getParameterClassValues <em>Parameter Class Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Class Values</em>'.
	 * @see com.openMap1.mapper.ImportMappingSet#getParameterClassValues()
	 * @see #getImportMappingSet()
	 * @generated
	 */
	EReference getImportMappingSet_ParameterClassValues();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.JavaConversionImplementation <em>Java Conversion Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Java Conversion Implementation</em>'.
	 * @see com.openMap1.mapper.JavaConversionImplementation
	 * @generated
	 */
	EClass getJavaConversionImplementation();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.JavaConversionImplementation#getClassName <em>Class Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class Name</em>'.
	 * @see com.openMap1.mapper.JavaConversionImplementation#getClassName()
	 * @see #getJavaConversionImplementation()
	 * @generated
	 */
	EAttribute getJavaConversionImplementation_ClassName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.JavaConversionImplementation#getMethodName <em>Method Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Method Name</em>'.
	 * @see com.openMap1.mapper.JavaConversionImplementation#getMethodName()
	 * @see #getJavaConversionImplementation()
	 * @generated
	 */
	EAttribute getJavaConversionImplementation_MethodName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.JavaConversionImplementation#getPackageName <em>Package Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Name</em>'.
	 * @see com.openMap1.mapper.JavaConversionImplementation#getPackageName()
	 * @see #getJavaConversionImplementation()
	 * @generated
	 */
	EAttribute getJavaConversionImplementation_PackageName();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.LocalPropertyConversion <em>Local Property Conversion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Property Conversion</em>'.
	 * @see com.openMap1.mapper.LocalPropertyConversion
	 * @generated
	 */
	EClass getLocalPropertyConversion();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.LocalPropertyConversion#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.openMap1.mapper.LocalPropertyConversion#getDescription()
	 * @see #getLocalPropertyConversion()
	 * @generated
	 */
	EAttribute getLocalPropertyConversion_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.LocalPropertyConversion#getInConversionImplementations <em>In Conversion Implementations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>In Conversion Implementations</em>'.
	 * @see com.openMap1.mapper.LocalPropertyConversion#getInConversionImplementations()
	 * @see #getLocalPropertyConversion()
	 * @generated
	 */
	EReference getLocalPropertyConversion_InConversionImplementations();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.LocalPropertyConversion#getOutConversionImplementations <em>Out Conversion Implementations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Out Conversion Implementations</em>'.
	 * @see com.openMap1.mapper.LocalPropertyConversion#getOutConversionImplementations()
	 * @see #getLocalPropertyConversion()
	 * @generated
	 */
	EReference getLocalPropertyConversion_OutConversionImplementations();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.LocalPropertyConversion#getValuePairs <em>Value Pairs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Value Pairs</em>'.
	 * @see com.openMap1.mapper.LocalPropertyConversion#getValuePairs()
	 * @see #getLocalPropertyConversion()
	 * @generated
	 */
	EReference getLocalPropertyConversion_ValuePairs();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.MappedStructure <em>Mapped Structure</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mapped Structure</em>'.
	 * @see com.openMap1.mapper.MappedStructure
	 * @generated
	 */
	EClass getMappedStructure();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappedStructure#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getName()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EAttribute getMappedStructure_Name();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.MappedStructure#getRootElement <em>Root Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Root Element</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getRootElement()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EReference getMappedStructure_RootElement();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappedStructure#getUMLModelURL <em>UML Model URL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>UML Model URL</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getUMLModelURL()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EAttribute getMappedStructure_UMLModelURL();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappedStructure#getStructureType <em>Structure Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Structure Type</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getStructureType()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EAttribute getMappedStructure_StructureType();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappedStructure#getStructureURL <em>Structure URL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Structure URL</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getStructureURL()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EAttribute getMappedStructure_StructureURL();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappedStructure#getTopElementType <em>Top Element Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Top Element Type</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getTopElementType()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EAttribute getMappedStructure_TopElementType();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappedStructure#getTopElementName <em>Top Element Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Top Element Name</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getTopElementName()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EAttribute getMappedStructure_TopElementName();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.MappedStructure#getMappingParameters <em>Mapping Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Mapping Parameters</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getMappingParameters()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EReference getMappedStructure_MappingParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.MappedStructure#getParameterClasses <em>Parameter Classes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Classes</em>'.
	 * @see com.openMap1.mapper.MappedStructure#getParameterClasses()
	 * @see #getMappedStructure()
	 * @generated
	 */
	EReference getMappedStructure_ParameterClasses();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.Mapping <em>Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mapping</em>'.
	 * @see com.openMap1.mapper.Mapping
	 * @generated
	 */
	EClass getMapping();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Mapping#getMappedClass <em>Mapped Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapped Class</em>'.
	 * @see com.openMap1.mapper.Mapping#getMappedClass()
	 * @see #getMapping()
	 * @generated
	 */
	EAttribute getMapping_MappedClass();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Mapping#getMappedPackage <em>Mapped Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapped Package</em>'.
	 * @see com.openMap1.mapper.Mapping#getMappedPackage()
	 * @see #getMapping()
	 * @generated
	 */
	EAttribute getMapping_MappedPackage();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Mapping#getSubset <em>Subset</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Subset</em>'.
	 * @see com.openMap1.mapper.Mapping#getSubset()
	 * @see #getMapping()
	 * @generated
	 */
	EAttribute getMapping_Subset();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Mapping#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.openMap1.mapper.Mapping#getDescription()
	 * @see #getMapping()
	 * @generated
	 */
	EAttribute getMapping_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.Mapping#getMappingConditions <em>Mapping Conditions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Mapping Conditions</em>'.
	 * @see com.openMap1.mapper.Mapping#getMappingConditions()
	 * @see #getMapping()
	 * @generated
	 */
	EReference getMapping_MappingConditions();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Mapping#getMultiWay <em>Multi Way</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Multi Way</em>'.
	 * @see com.openMap1.mapper.Mapping#getMultiWay()
	 * @see #getMapping()
	 * @generated
	 */
	EAttribute getMapping_MultiWay();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Mapping#isBreakPoint <em>Break Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Break Point</em>'.
	 * @see com.openMap1.mapper.Mapping#isBreakPoint()
	 * @see #getMapping()
	 * @generated
	 */
	EAttribute getMapping_BreakPoint();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.MappingCondition <em>Mapping Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mapping Condition</em>'.
	 * @see com.openMap1.mapper.MappingCondition
	 * @generated
	 */
	EClass getMappingCondition();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappingCondition#getLeftPath <em>Left Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Left Path</em>'.
	 * @see com.openMap1.mapper.MappingCondition#getLeftPath()
	 * @see #getMappingCondition()
	 * @generated
	 */
	EAttribute getMappingCondition_LeftPath();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappingCondition#getLeftFunction <em>Left Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Left Function</em>'.
	 * @see com.openMap1.mapper.MappingCondition#getLeftFunction()
	 * @see #getMappingCondition()
	 * @generated
	 */
	EAttribute getMappingCondition_LeftFunction();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappingCondition#getTest <em>Test</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Test</em>'.
	 * @see com.openMap1.mapper.MappingCondition#getTest()
	 * @see #getMappingCondition()
	 * @generated
	 */
	EAttribute getMappingCondition_Test();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.MappingCondition#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.openMap1.mapper.MappingCondition#getDescription()
	 * @see #getMappingCondition()
	 * @generated
	 */
	EAttribute getMappingCondition_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.MappingCondition#getLeftPathConditions <em>Left Path Conditions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Left Path Conditions</em>'.
	 * @see com.openMap1.mapper.MappingCondition#getLeftPathConditions()
	 * @see #getMappingCondition()
	 * @generated
	 */
	EReference getMappingCondition_LeftPathConditions();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ModelAssocFilter <em>Model Assoc Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model Assoc Filter</em>'.
	 * @see com.openMap1.mapper.ModelAssocFilter
	 * @generated
	 */
	EClass getModelAssocFilter();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelAssocFilter#getRoleName <em>Role Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Role Name</em>'.
	 * @see com.openMap1.mapper.ModelAssocFilter#getRoleName()
	 * @see #getModelAssocFilter()
	 * @generated
	 */
	EAttribute getModelAssocFilter_RoleName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelAssocFilter#getOtherClassName <em>Other Class Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Other Class Name</em>'.
	 * @see com.openMap1.mapper.ModelAssocFilter#getOtherClassName()
	 * @see #getModelAssocFilter()
	 * @generated
	 */
	EAttribute getModelAssocFilter_OtherClassName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelAssocFilter#getOtherPackageName <em>Other Package Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Other Package Name</em>'.
	 * @see com.openMap1.mapper.ModelAssocFilter#getOtherPackageName()
	 * @see #getModelAssocFilter()
	 * @generated
	 */
	EAttribute getModelAssocFilter_OtherPackageName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelAssocFilter#getOtherSubset <em>Other Subset</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Other Subset</em>'.
	 * @see com.openMap1.mapper.ModelAssocFilter#getOtherSubset()
	 * @see #getModelAssocFilter()
	 * @generated
	 */
	EAttribute getModelAssocFilter_OtherSubset();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ModelFilter <em>Model Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model Filter</em>'.
	 * @see com.openMap1.mapper.ModelFilter
	 * @generated
	 */
	EClass getModelFilter();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelFilter#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.openMap1.mapper.ModelFilter#getDescription()
	 * @see #getModelFilter()
	 * @generated
	 */
	EAttribute getModelFilter_Description();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ModelFilterSet <em>Model Filter Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model Filter Set</em>'.
	 * @see com.openMap1.mapper.ModelFilterSet
	 * @generated
	 */
	EClass getModelFilterSet();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelFilterSet#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.openMap1.mapper.ModelFilterSet#getDescription()
	 * @see #getModelFilterSet()
	 * @generated
	 */
	EAttribute getModelFilterSet_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.ModelFilterSet#getModelFilters <em>Model Filters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Model Filters</em>'.
	 * @see com.openMap1.mapper.ModelFilterSet#getModelFilters()
	 * @see #getModelFilterSet()
	 * @generated
	 */
	EReference getModelFilterSet_ModelFilters();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ModelPropertyFilter <em>Model Property Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model Property Filter</em>'.
	 * @see com.openMap1.mapper.ModelPropertyFilter
	 * @generated
	 */
	EClass getModelPropertyFilter();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelPropertyFilter#getPropertyName <em>Property Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Property Name</em>'.
	 * @see com.openMap1.mapper.ModelPropertyFilter#getPropertyName()
	 * @see #getModelPropertyFilter()
	 * @generated
	 */
	EAttribute getModelPropertyFilter_PropertyName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelPropertyFilter#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see com.openMap1.mapper.ModelPropertyFilter#getValue()
	 * @see #getModelPropertyFilter()
	 * @generated
	 */
	EAttribute getModelPropertyFilter_Value();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ModelPropertyFilter#getTest <em>Test</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Test</em>'.
	 * @see com.openMap1.mapper.ModelPropertyFilter#getTest()
	 * @see #getModelPropertyFilter()
	 * @generated
	 */
	EAttribute getModelPropertyFilter_Test();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.Namespace <em>Namespace</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Namespace</em>'.
	 * @see com.openMap1.mapper.Namespace
	 * @generated
	 */
	EClass getNamespace();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Namespace#getURL <em>URL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>URL</em>'.
	 * @see com.openMap1.mapper.Namespace#getURL()
	 * @see #getNamespace()
	 * @generated
	 */
	EAttribute getNamespace_URL();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Namespace#getPrefix <em>Prefix</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Prefix</em>'.
	 * @see com.openMap1.mapper.Namespace#getPrefix()
	 * @see #getNamespace()
	 * @generated
	 */
	EAttribute getNamespace_Prefix();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.NodeDef <em>Node Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Node Def</em>'.
	 * @see com.openMap1.mapper.NodeDef
	 * @generated
	 */
	EClass getNodeDef();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.NodeDef#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see com.openMap1.mapper.NodeDef#getName()
	 * @see #getNodeDef()
	 * @generated
	 */
	EAttribute getNodeDef_Name();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.NodeDef#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see com.openMap1.mapper.NodeDef#getType()
	 * @see #getNodeDef()
	 * @generated
	 */
	EAttribute getNodeDef_Type();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.NodeDef#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.openMap1.mapper.NodeDef#getDescription()
	 * @see #getNodeDef()
	 * @generated
	 */
	EAttribute getNodeDef_Description();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.NodeDef#getMinMultiplicity <em>Min Multiplicity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Multiplicity</em>'.
	 * @see com.openMap1.mapper.NodeDef#getMinMultiplicity()
	 * @see #getNodeDef()
	 * @generated
	 */
	EAttribute getNodeDef_MinMultiplicity();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.NodeDef#getDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see com.openMap1.mapper.NodeDef#getDefaultValue()
	 * @see #getNodeDef()
	 * @generated
	 */
	EAttribute getNodeDef_DefaultValue();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.NodeDef#getFixedValue <em>Fixed Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fixed Value</em>'.
	 * @see com.openMap1.mapper.NodeDef#getFixedValue()
	 * @see #getNodeDef()
	 * @generated
	 */
	EAttribute getNodeDef_FixedValue();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.NodeDef#getNodeMappingSet <em>Node Mapping Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Node Mapping Set</em>'.
	 * @see com.openMap1.mapper.NodeDef#getNodeMappingSet()
	 * @see #getNodeDef()
	 * @generated
	 */
	EReference getNodeDef_NodeMappingSet();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.NodeDef#getAnnotations <em>Annotations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Annotations</em>'.
	 * @see com.openMap1.mapper.NodeDef#getAnnotations()
	 * @see #getNodeDef()
	 * @generated
	 */
	EReference getNodeDef_Annotations();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.NodeMappingSet <em>Node Mapping Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Node Mapping Set</em>'.
	 * @see com.openMap1.mapper.NodeMappingSet
	 * @generated
	 */
	EClass getNodeMappingSet();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.NodeMappingSet#getObjectMappings <em>Object Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Object Mappings</em>'.
	 * @see com.openMap1.mapper.NodeMappingSet#getObjectMappings()
	 * @see #getNodeMappingSet()
	 * @generated
	 */
	EReference getNodeMappingSet_ObjectMappings();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.NodeMappingSet#getPropertyMappings <em>Property Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Property Mappings</em>'.
	 * @see com.openMap1.mapper.NodeMappingSet#getPropertyMappings()
	 * @see #getNodeMappingSet()
	 * @generated
	 */
	EReference getNodeMappingSet_PropertyMappings();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.NodeMappingSet#getAssociationMappings <em>Association Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Association Mappings</em>'.
	 * @see com.openMap1.mapper.NodeMappingSet#getAssociationMappings()
	 * @see #getNodeMappingSet()
	 * @generated
	 */
	EReference getNodeMappingSet_AssociationMappings();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.Note <em>Note</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Note</em>'.
	 * @see com.openMap1.mapper.Note
	 * @generated
	 */
	EClass getNote();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Note#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see com.openMap1.mapper.Note#getKey()
	 * @see #getNote()
	 * @generated
	 */
	EAttribute getNote_Key();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.Note#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see com.openMap1.mapper.Note#getValue()
	 * @see #getNote()
	 * @generated
	 */
	EAttribute getNote_Value();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ObjMapping <em>Obj Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Obj Mapping</em>'.
	 * @see com.openMap1.mapper.ObjMapping
	 * @generated
	 */
	EClass getObjMapping();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ObjMapping#getRootPath <em>Root Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Root Path</em>'.
	 * @see com.openMap1.mapper.ObjMapping#getRootPath()
	 * @see #getObjMapping()
	 * @generated
	 */
	EAttribute getObjMapping_RootPath();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ObjMapping#isMultiplyRepresented <em>Multiply Represented</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Multiply Represented</em>'.
	 * @see com.openMap1.mapper.ObjMapping#isMultiplyRepresented()
	 * @see #getObjMapping()
	 * @generated
	 */
	EAttribute getObjMapping_MultiplyRepresented();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.ObjMapping#getModelFilterSet <em>Model Filter Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Model Filter Set</em>'.
	 * @see com.openMap1.mapper.ObjMapping#getModelFilterSet()
	 * @see #getObjMapping()
	 * @generated
	 */
	EReference getObjMapping_ModelFilterSet();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.ObjMapping#getFixedPropertyValues <em>Fixed Property Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Fixed Property Values</em>'.
	 * @see com.openMap1.mapper.ObjMapping#getFixedPropertyValues()
	 * @see #getObjMapping()
	 * @generated
	 */
	EReference getObjMapping_FixedPropertyValues();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ParameterClass <em>Parameter Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Class</em>'.
	 * @see com.openMap1.mapper.ParameterClass
	 * @generated
	 */
	EClass getParameterClass();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ParameterClass#getClassName <em>Class Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class Name</em>'.
	 * @see com.openMap1.mapper.ParameterClass#getClassName()
	 * @see #getParameterClass()
	 * @generated
	 */
	EAttribute getParameterClass_ClassName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ParameterClass#getPackageName <em>Package Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Name</em>'.
	 * @see com.openMap1.mapper.ParameterClass#getPackageName()
	 * @see #getParameterClass()
	 * @generated
	 */
	EAttribute getParameterClass_PackageName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ParameterClass#getParameterIndex <em>Parameter Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parameter Index</em>'.
	 * @see com.openMap1.mapper.ParameterClass#getParameterIndex()
	 * @see #getParameterClass()
	 * @generated
	 */
	EAttribute getParameterClass_ParameterIndex();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ParameterClassValue <em>Parameter Class Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Class Value</em>'.
	 * @see com.openMap1.mapper.ParameterClassValue
	 * @generated
	 */
	EClass getParameterClassValue();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ParameterClassValue#getMappedClass <em>Mapped Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapped Class</em>'.
	 * @see com.openMap1.mapper.ParameterClassValue#getMappedClass()
	 * @see #getParameterClassValue()
	 * @generated
	 */
	EAttribute getParameterClassValue_MappedClass();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ParameterClassValue#getMappedPackage <em>Mapped Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapped Package</em>'.
	 * @see com.openMap1.mapper.ParameterClassValue#getMappedPackage()
	 * @see #getParameterClassValue()
	 * @generated
	 */
	EAttribute getParameterClassValue_MappedPackage();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ParameterClassValue#getSubset <em>Subset</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Subset</em>'.
	 * @see com.openMap1.mapper.ParameterClassValue#getSubset()
	 * @see #getParameterClassValue()
	 * @generated
	 */
	EAttribute getParameterClassValue_Subset();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ParameterClassValue#getParameterIndex <em>Parameter Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parameter Index</em>'.
	 * @see com.openMap1.mapper.ParameterClassValue#getParameterIndex()
	 * @see #getParameterClassValue()
	 * @generated
	 */
	EAttribute getParameterClassValue_ParameterIndex();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.PropMapping <em>Prop Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Prop Mapping</em>'.
	 * @see com.openMap1.mapper.PropMapping
	 * @generated
	 */
	EClass getPropMapping();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.PropMapping#getMappedProperty <em>Mapped Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mapped Property</em>'.
	 * @see com.openMap1.mapper.PropMapping#getMappedProperty()
	 * @see #getPropMapping()
	 * @generated
	 */
	EAttribute getPropMapping_MappedProperty();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.PropMapping#getPropertyType <em>Property Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Property Type</em>'.
	 * @see com.openMap1.mapper.PropMapping#getPropertyType()
	 * @see #getPropMapping()
	 * @generated
	 */
	EAttribute getPropMapping_PropertyType();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.PropMapping#getDefaultValue <em>Default Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Value</em>'.
	 * @see com.openMap1.mapper.PropMapping#getDefaultValue()
	 * @see #getPropMapping()
	 * @generated
	 */
	EAttribute getPropMapping_DefaultValue();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.PropMapping#getObjectToPropertyPath <em>Object To Property Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Object To Property Path</em>'.
	 * @see com.openMap1.mapper.PropMapping#getObjectToPropertyPath()
	 * @see #getPropMapping()
	 * @generated
	 */
	EAttribute getPropMapping_ObjectToPropertyPath();

	/**
	 * Returns the meta object for the containment reference '{@link com.openMap1.mapper.PropMapping#getLocalPropertyConversion <em>Local Property Conversion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Local Property Conversion</em>'.
	 * @see com.openMap1.mapper.PropMapping#getLocalPropertyConversion()
	 * @see #getPropMapping()
	 * @generated
	 */
	EReference getPropMapping_LocalPropertyConversion();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.PropertyConversion <em>Property Conversion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property Conversion</em>'.
	 * @see com.openMap1.mapper.PropertyConversion
	 * @generated
	 */
	EClass getPropertyConversion();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.PropertyConversion#getSubset <em>Subset</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Subset</em>'.
	 * @see com.openMap1.mapper.PropertyConversion#getSubset()
	 * @see #getPropertyConversion()
	 * @generated
	 */
	EAttribute getPropertyConversion_Subset();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.PropertyConversion#getResultSlot <em>Result Slot</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result Slot</em>'.
	 * @see com.openMap1.mapper.PropertyConversion#getResultSlot()
	 * @see #getPropertyConversion()
	 * @generated
	 */
	EAttribute getPropertyConversion_ResultSlot();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.PropertyConversion#getSense <em>Sense</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sense</em>'.
	 * @see com.openMap1.mapper.PropertyConversion#getSense()
	 * @see #getPropertyConversion()
	 * @generated
	 */
	EAttribute getPropertyConversion_Sense();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.PropertyConversion#getConversionImplementations <em>Conversion Implementations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Conversion Implementations</em>'.
	 * @see com.openMap1.mapper.PropertyConversion#getConversionImplementations()
	 * @see #getPropertyConversion()
	 * @generated
	 */
	EReference getPropertyConversion_ConversionImplementations();

	/**
	 * Returns the meta object for the containment reference list '{@link com.openMap1.mapper.PropertyConversion#getConversionArguments <em>Conversion Arguments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Conversion Arguments</em>'.
	 * @see com.openMap1.mapper.PropertyConversion#getConversionArguments()
	 * @see #getPropertyConversion()
	 * @generated
	 */
	EReference getPropertyConversion_ConversionArguments();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.PropertyConversion#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.openMap1.mapper.PropertyConversion#getDescription()
	 * @see #getPropertyConversion()
	 * @generated
	 */
	EAttribute getPropertyConversion_Description();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ValueCondition <em>Value Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Value Condition</em>'.
	 * @see com.openMap1.mapper.ValueCondition
	 * @generated
	 */
	EClass getValueCondition();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ValueCondition#getRightValue <em>Right Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Right Value</em>'.
	 * @see com.openMap1.mapper.ValueCondition#getRightValue()
	 * @see #getValueCondition()
	 * @generated
	 */
	EAttribute getValueCondition_RightValue();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.XSLTConversionImplementation <em>XSLT Conversion Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>XSLT Conversion Implementation</em>'.
	 * @see com.openMap1.mapper.XSLTConversionImplementation
	 * @generated
	 */
	EClass getXSLTConversionImplementation();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.XSLTConversionImplementation#getTemplateName <em>Template Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Template Name</em>'.
	 * @see com.openMap1.mapper.XSLTConversionImplementation#getTemplateName()
	 * @see #getXSLTConversionImplementation()
	 * @generated
	 */
	EAttribute getXSLTConversionImplementation_TemplateName();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.XSLTConversionImplementation#getTemplateFileURI <em>Template File URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Template File URI</em>'.
	 * @see com.openMap1.mapper.XSLTConversionImplementation#getTemplateFileURI()
	 * @see #getXSLTConversionImplementation()
	 * @generated
	 */
	EAttribute getXSLTConversionImplementation_TemplateFileURI();

	/**
	 * Returns the meta object for class '{@link com.openMap1.mapper.ValuePair <em>Value Pair</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Value Pair</em>'.
	 * @see com.openMap1.mapper.ValuePair
	 * @generated
	 */
	EClass getValuePair();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ValuePair#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see com.openMap1.mapper.ValuePair#getDescription()
	 * @see #getValuePair()
	 * @generated
	 */
	EAttribute getValuePair_Description();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ValuePair#getStructureValue <em>Structure Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Structure Value</em>'.
	 * @see com.openMap1.mapper.ValuePair#getStructureValue()
	 * @see #getValuePair()
	 * @generated
	 */
	EAttribute getValuePair_StructureValue();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ValuePair#getModelValue <em>Model Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Model Value</em>'.
	 * @see com.openMap1.mapper.ValuePair#getModelValue()
	 * @see #getValuePair()
	 * @generated
	 */
	EAttribute getValuePair_ModelValue();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ValuePair#isPreferredIn <em>Preferred In</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Preferred In</em>'.
	 * @see com.openMap1.mapper.ValuePair#isPreferredIn()
	 * @see #getValuePair()
	 * @generated
	 */
	EAttribute getValuePair_PreferredIn();

	/**
	 * Returns the meta object for the attribute '{@link com.openMap1.mapper.ValuePair#isPreferredOut <em>Preferred Out</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Preferred Out</em>'.
	 * @see com.openMap1.mapper.ValuePair#isPreferredOut()
	 * @see #getValuePair()
	 * @generated
	 */
	EAttribute getValuePair_PreferredOut();

	/**
	 * Returns the meta object for enum '{@link com.openMap1.mapper.ConditionTest <em>Condition Test</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Condition Test</em>'.
	 * @see com.openMap1.mapper.ConditionTest
	 * @generated
	 */
	EEnum getConditionTest();

	/**
	 * Returns the meta object for enum '{@link com.openMap1.mapper.ConversionSense <em>Conversion Sense</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Conversion Sense</em>'.
	 * @see com.openMap1.mapper.ConversionSense
	 * @generated
	 */
	EEnum getConversionSense();

	/**
	 * Returns the meta object for enum '{@link com.openMap1.mapper.MaxMult <em>Max Mult</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Max Mult</em>'.
	 * @see com.openMap1.mapper.MaxMult
	 * @generated
	 */
	EEnum getMaxMult();

	/**
	 * Returns the meta object for enum '{@link com.openMap1.mapper.MinMult <em>Min Mult</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Min Mult</em>'.
	 * @see com.openMap1.mapper.MinMult
	 * @generated
	 */
	EEnum getMinMult();

	/**
	 * Returns the meta object for enum '{@link com.openMap1.mapper.MultiWay <em>Multi Way</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Multi Way</em>'.
	 * @see com.openMap1.mapper.MultiWay
	 * @generated
	 */
	EEnum getMultiWay();

	/**
	 * Returns the meta object for enum '{@link com.openMap1.mapper.StructureType <em>Structure Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Structure Type</em>'.
	 * @see com.openMap1.mapper.StructureType
	 * @generated
	 */
	EEnum getStructureType();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.DiagnosticChain <em>Diagnostic Chain</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Diagnostic Chain</em>'.
	 * @see org.eclipse.emf.common.util.DiagnosticChain
	 * @model instanceClass="org.eclipse.emf.common.util.DiagnosticChain"
	 * @generated
	 */
	EDataType getDiagnosticChain();

	/**
	 * Returns the meta object for data type '{@link java.util.Map <em>Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Map</em>'.
	 * @see java.util.Map
	 * @model instanceClass="java.util.Map" typeParameters="T T1"
	 * @generated
	 */
	EDataType getMap();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MapperFactory getMapperFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.AnnotationsImpl <em>Annotations</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.AnnotationsImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getAnnotations()
		 * @generated
		 */
		EClass ANNOTATIONS = eINSTANCE.getAnnotations();

		/**
		 * The meta object literal for the '<em><b>Notes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANNOTATIONS__NOTES = eINSTANCE.getAnnotations_Notes();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.AssocEndMappingImpl <em>Assoc End Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.AssocEndMappingImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getAssocEndMapping()
		 * @generated
		 */
		EClass ASSOC_END_MAPPING = eINSTANCE.getAssocEndMapping();

		/**
		 * The meta object literal for the '<em><b>Mapped Role</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSOC_END_MAPPING__MAPPED_ROLE = eINSTANCE.getAssocEndMapping_MappedRole();

		/**
		 * The meta object literal for the '<em><b>Object To Association Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH = eINSTANCE.getAssocEndMapping_ObjectToAssociationPath();

		/**
		 * The meta object literal for the '<em><b>Association To Object Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH = eINSTANCE.getAssocEndMapping_AssociationToObjectPath();

		/**
		 * The meta object literal for the '<em><b>Required For Object</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSOC_END_MAPPING__REQUIRED_FOR_OBJECT = eINSTANCE.getAssocEndMapping_RequiredForObject();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.AssocMappingImpl <em>Assoc Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.AssocMappingImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getAssocMapping()
		 * @generated
		 */
		EClass ASSOC_MAPPING = eINSTANCE.getAssocMapping();

		/**
		 * The meta object literal for the '<em><b>Mapped End1</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ASSOC_MAPPING__MAPPED_END1 = eINSTANCE.getAssocMapping_MappedEnd1();

		/**
		 * The meta object literal for the '<em><b>Mapped End2</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ASSOC_MAPPING__MAPPED_END2 = eINSTANCE.getAssocMapping_MappedEnd2();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.AttributeDefImpl <em>Attribute Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.AttributeDefImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getAttributeDef()
		 * @generated
		 */
		EClass ATTRIBUTE_DEF = eINSTANCE.getAttributeDef();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ClassDetailsImpl <em>Class Details</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ClassDetailsImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getClassDetails()
		 * @generated
		 */
		EClass CLASS_DETAILS = eINSTANCE.getClassDetails();

		/**
		 * The meta object literal for the '<em><b>Class Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_DETAILS__CLASS_NAME = eINSTANCE.getClassDetails_ClassName();

		/**
		 * The meta object literal for the '<em><b>Package Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_DETAILS__PACKAGE_NAME = eINSTANCE.getClassDetails_PackageName();

		/**
		 * The meta object literal for the '<em><b>Property Conversions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_DETAILS__PROPERTY_CONVERSIONS = eINSTANCE.getClassDetails_PropertyConversions();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ConversionArgumentImpl <em>Conversion Argument</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ConversionArgumentImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getConversionArgument()
		 * @generated
		 */
		EClass CONVERSION_ARGUMENT = eINSTANCE.getConversionArgument();

		/**
		 * The meta object literal for the '<em><b>Property Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONVERSION_ARGUMENT__PROPERTY_NAME = eINSTANCE.getConversionArgument_PropertyName();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ConversionImplementationImpl <em>Conversion Implementation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ConversionImplementationImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getConversionImplementation()
		 * @generated
		 */
		EClass CONVERSION_IMPLEMENTATION = eINSTANCE.getConversionImplementation();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.CrossConditionImpl <em>Cross Condition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.CrossConditionImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getCrossCondition()
		 * @generated
		 */
		EClass CROSS_CONDITION = eINSTANCE.getCrossCondition();

		/**
		 * The meta object literal for the '<em><b>Right Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CROSS_CONDITION__RIGHT_PATH = eINSTANCE.getCrossCondition_RightPath();

		/**
		 * The meta object literal for the '<em><b>Right Function</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CROSS_CONDITION__RIGHT_FUNCTION = eINSTANCE.getCrossCondition_RightFunction();

		/**
		 * The meta object literal for the '<em><b>Right Path Conditions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CROSS_CONDITION__RIGHT_PATH_CONDITIONS = eINSTANCE.getCrossCondition_RightPathConditions();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ElementDefImpl <em>Element Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ElementDefImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getElementDef()
		 * @generated
		 */
		EClass ELEMENT_DEF = eINSTANCE.getElementDef();

		/**
		 * The meta object literal for the '<em><b>Expanded</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_DEF__EXPANDED = eINSTANCE.getElementDef_Expanded();

		/**
		 * The meta object literal for the '<em><b>Max Multiplicity</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_DEF__MAX_MULTIPLICITY = eINSTANCE.getElementDef_MaxMultiplicity();

		/**
		 * The meta object literal for the '<em><b>Child Elements</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_DEF__CHILD_ELEMENTS = eINSTANCE.getElementDef_ChildElements();

		/**
		 * The meta object literal for the '<em><b>Attribute Defs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_DEF__ATTRIBUTE_DEFS = eINSTANCE.getElementDef_AttributeDefs();

		/**
		 * The meta object literal for the '<em><b>Import Mapping Set</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_DEF__IMPORT_MAPPING_SET = eINSTANCE.getElementDef_ImportMappingSet();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.FixedPropertyValueImpl <em>Fixed Property Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.FixedPropertyValueImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getFixedPropertyValue()
		 * @generated
		 */
		EClass FIXED_PROPERTY_VALUE = eINSTANCE.getFixedPropertyValue();

		/**
		 * The meta object literal for the '<em><b>Mapped Property</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FIXED_PROPERTY_VALUE__MAPPED_PROPERTY = eINSTANCE.getFixedPropertyValue_MappedProperty();

		/**
		 * The meta object literal for the '<em><b>Fixed Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FIXED_PROPERTY_VALUE__FIXED_VALUE = eINSTANCE.getFixedPropertyValue_FixedValue();

		/**
		 * The meta object literal for the '<em><b>Value Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FIXED_PROPERTY_VALUE__VALUE_TYPE = eINSTANCE.getFixedPropertyValue_ValueType();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.GlobalMappingParametersImpl <em>Global Mapping Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.GlobalMappingParametersImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getGlobalMappingParameters()
		 * @generated
		 */
		EClass GLOBAL_MAPPING_PARAMETERS = eINSTANCE.getGlobalMappingParameters();

		/**
		 * The meta object literal for the '<em><b>Mapping Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GLOBAL_MAPPING_PARAMETERS__MAPPING_CLASS = eINSTANCE.getGlobalMappingParameters_MappingClass();

		/**
		 * The meta object literal for the '<em><b>Wrapper Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GLOBAL_MAPPING_PARAMETERS__WRAPPER_CLASS = eINSTANCE.getGlobalMappingParameters_WrapperClass();

		/**
		 * The meta object literal for the '<em><b>Name Spaces</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GLOBAL_MAPPING_PARAMETERS__NAME_SPACES = eINSTANCE.getGlobalMappingParameters_NameSpaces();

		/**
		 * The meta object literal for the '<em><b>Class Details</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS = eINSTANCE.getGlobalMappingParameters_ClassDetails();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ImportMappingSetImpl <em>Import Mapping Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ImportMappingSetImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getImportMappingSet()
		 * @generated
		 */
		EClass IMPORT_MAPPING_SET = eINSTANCE.getImportMappingSet();

		/**
		 * The meta object literal for the '<em><b>Mapping Set URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPORT_MAPPING_SET__MAPPING_SET_URI = eINSTANCE.getImportMappingSet_MappingSetURI();

		/**
		 * The meta object literal for the '<em><b>Parameter Class Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES = eINSTANCE.getImportMappingSet_ParameterClassValues();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.JavaConversionImplementationImpl <em>Java Conversion Implementation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.JavaConversionImplementationImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getJavaConversionImplementation()
		 * @generated
		 */
		EClass JAVA_CONVERSION_IMPLEMENTATION = eINSTANCE.getJavaConversionImplementation();

		/**
		 * The meta object literal for the '<em><b>Class Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA_CONVERSION_IMPLEMENTATION__CLASS_NAME = eINSTANCE.getJavaConversionImplementation_ClassName();

		/**
		 * The meta object literal for the '<em><b>Method Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA_CONVERSION_IMPLEMENTATION__METHOD_NAME = eINSTANCE.getJavaConversionImplementation_MethodName();

		/**
		 * The meta object literal for the '<em><b>Package Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JAVA_CONVERSION_IMPLEMENTATION__PACKAGE_NAME = eINSTANCE.getJavaConversionImplementation_PackageName();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.LocalPropertyConversionImpl <em>Local Property Conversion</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.LocalPropertyConversionImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getLocalPropertyConversion()
		 * @generated
		 */
		EClass LOCAL_PROPERTY_CONVERSION = eINSTANCE.getLocalPropertyConversion();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCAL_PROPERTY_CONVERSION__DESCRIPTION = eINSTANCE.getLocalPropertyConversion_Description();

		/**
		 * The meta object literal for the '<em><b>In Conversion Implementations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS = eINSTANCE.getLocalPropertyConversion_InConversionImplementations();

		/**
		 * The meta object literal for the '<em><b>Out Conversion Implementations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS = eINSTANCE.getLocalPropertyConversion_OutConversionImplementations();

		/**
		 * The meta object literal for the '<em><b>Value Pairs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS = eINSTANCE.getLocalPropertyConversion_ValuePairs();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.MappedStructureImpl <em>Mapped Structure</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.MappedStructureImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMappedStructure()
		 * @generated
		 */
		EClass MAPPED_STRUCTURE = eINSTANCE.getMappedStructure();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPED_STRUCTURE__NAME = eINSTANCE.getMappedStructure_Name();

		/**
		 * The meta object literal for the '<em><b>Root Element</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAPPED_STRUCTURE__ROOT_ELEMENT = eINSTANCE.getMappedStructure_RootElement();

		/**
		 * The meta object literal for the '<em><b>UML Model URL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPED_STRUCTURE__UML_MODEL_URL = eINSTANCE.getMappedStructure_UMLModelURL();

		/**
		 * The meta object literal for the '<em><b>Structure Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPED_STRUCTURE__STRUCTURE_TYPE = eINSTANCE.getMappedStructure_StructureType();

		/**
		 * The meta object literal for the '<em><b>Structure URL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPED_STRUCTURE__STRUCTURE_URL = eINSTANCE.getMappedStructure_StructureURL();

		/**
		 * The meta object literal for the '<em><b>Top Element Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPED_STRUCTURE__TOP_ELEMENT_TYPE = eINSTANCE.getMappedStructure_TopElementType();

		/**
		 * The meta object literal for the '<em><b>Top Element Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPED_STRUCTURE__TOP_ELEMENT_NAME = eINSTANCE.getMappedStructure_TopElementName();

		/**
		 * The meta object literal for the '<em><b>Mapping Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAPPED_STRUCTURE__MAPPING_PARAMETERS = eINSTANCE.getMappedStructure_MappingParameters();

		/**
		 * The meta object literal for the '<em><b>Parameter Classes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAPPED_STRUCTURE__PARAMETER_CLASSES = eINSTANCE.getMappedStructure_ParameterClasses();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.MappingImpl <em>Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.MappingImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMapping()
		 * @generated
		 */
		EClass MAPPING = eINSTANCE.getMapping();

		/**
		 * The meta object literal for the '<em><b>Mapped Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING__MAPPED_CLASS = eINSTANCE.getMapping_MappedClass();

		/**
		 * The meta object literal for the '<em><b>Mapped Package</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING__MAPPED_PACKAGE = eINSTANCE.getMapping_MappedPackage();

		/**
		 * The meta object literal for the '<em><b>Subset</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING__SUBSET = eINSTANCE.getMapping_Subset();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING__DESCRIPTION = eINSTANCE.getMapping_Description();

		/**
		 * The meta object literal for the '<em><b>Mapping Conditions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAPPING__MAPPING_CONDITIONS = eINSTANCE.getMapping_MappingConditions();

		/**
		 * The meta object literal for the '<em><b>Multi Way</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING__MULTI_WAY = eINSTANCE.getMapping_MultiWay();

		/**
		 * The meta object literal for the '<em><b>Break Point</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING__BREAK_POINT = eINSTANCE.getMapping_BreakPoint();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.MappingConditionImpl <em>Mapping Condition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.MappingConditionImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMappingCondition()
		 * @generated
		 */
		EClass MAPPING_CONDITION = eINSTANCE.getMappingCondition();

		/**
		 * The meta object literal for the '<em><b>Left Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_CONDITION__LEFT_PATH = eINSTANCE.getMappingCondition_LeftPath();

		/**
		 * The meta object literal for the '<em><b>Left Function</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_CONDITION__LEFT_FUNCTION = eINSTANCE.getMappingCondition_LeftFunction();

		/**
		 * The meta object literal for the '<em><b>Test</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_CONDITION__TEST = eINSTANCE.getMappingCondition_Test();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_CONDITION__DESCRIPTION = eINSTANCE.getMappingCondition_Description();

		/**
		 * The meta object literal for the '<em><b>Left Path Conditions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAPPING_CONDITION__LEFT_PATH_CONDITIONS = eINSTANCE.getMappingCondition_LeftPathConditions();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ModelAssocFilterImpl <em>Model Assoc Filter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ModelAssocFilterImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getModelAssocFilter()
		 * @generated
		 */
		EClass MODEL_ASSOC_FILTER = eINSTANCE.getModelAssocFilter();

		/**
		 * The meta object literal for the '<em><b>Role Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_ASSOC_FILTER__ROLE_NAME = eINSTANCE.getModelAssocFilter_RoleName();

		/**
		 * The meta object literal for the '<em><b>Other Class Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_ASSOC_FILTER__OTHER_CLASS_NAME = eINSTANCE.getModelAssocFilter_OtherClassName();

		/**
		 * The meta object literal for the '<em><b>Other Package Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_ASSOC_FILTER__OTHER_PACKAGE_NAME = eINSTANCE.getModelAssocFilter_OtherPackageName();

		/**
		 * The meta object literal for the '<em><b>Other Subset</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_ASSOC_FILTER__OTHER_SUBSET = eINSTANCE.getModelAssocFilter_OtherSubset();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ModelFilterImpl <em>Model Filter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ModelFilterImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getModelFilter()
		 * @generated
		 */
		EClass MODEL_FILTER = eINSTANCE.getModelFilter();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_FILTER__DESCRIPTION = eINSTANCE.getModelFilter_Description();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ModelFilterSetImpl <em>Model Filter Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ModelFilterSetImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getModelFilterSet()
		 * @generated
		 */
		EClass MODEL_FILTER_SET = eINSTANCE.getModelFilterSet();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_FILTER_SET__DESCRIPTION = eINSTANCE.getModelFilterSet_Description();

		/**
		 * The meta object literal for the '<em><b>Model Filters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL_FILTER_SET__MODEL_FILTERS = eINSTANCE.getModelFilterSet_ModelFilters();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ModelPropertyFilterImpl <em>Model Property Filter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ModelPropertyFilterImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getModelPropertyFilter()
		 * @generated
		 */
		EClass MODEL_PROPERTY_FILTER = eINSTANCE.getModelPropertyFilter();

		/**
		 * The meta object literal for the '<em><b>Property Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_PROPERTY_FILTER__PROPERTY_NAME = eINSTANCE.getModelPropertyFilter_PropertyName();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_PROPERTY_FILTER__VALUE = eINSTANCE.getModelPropertyFilter_Value();

		/**
		 * The meta object literal for the '<em><b>Test</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL_PROPERTY_FILTER__TEST = eINSTANCE.getModelPropertyFilter_Test();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.NamespaceImpl <em>Namespace</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.NamespaceImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getNamespace()
		 * @generated
		 */
		EClass NAMESPACE = eINSTANCE.getNamespace();

		/**
		 * The meta object literal for the '<em><b>URL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAMESPACE__URL = eINSTANCE.getNamespace_URL();

		/**
		 * The meta object literal for the '<em><b>Prefix</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAMESPACE__PREFIX = eINSTANCE.getNamespace_Prefix();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.NodeDefImpl <em>Node Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.NodeDefImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getNodeDef()
		 * @generated
		 */
		EClass NODE_DEF = eINSTANCE.getNodeDef();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE_DEF__NAME = eINSTANCE.getNodeDef_Name();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE_DEF__TYPE = eINSTANCE.getNodeDef_Type();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE_DEF__DESCRIPTION = eINSTANCE.getNodeDef_Description();

		/**
		 * The meta object literal for the '<em><b>Min Multiplicity</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE_DEF__MIN_MULTIPLICITY = eINSTANCE.getNodeDef_MinMultiplicity();

		/**
		 * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE_DEF__DEFAULT_VALUE = eINSTANCE.getNodeDef_DefaultValue();

		/**
		 * The meta object literal for the '<em><b>Fixed Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE_DEF__FIXED_VALUE = eINSTANCE.getNodeDef_FixedValue();

		/**
		 * The meta object literal for the '<em><b>Node Mapping Set</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE_DEF__NODE_MAPPING_SET = eINSTANCE.getNodeDef_NodeMappingSet();

		/**
		 * The meta object literal for the '<em><b>Annotations</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE_DEF__ANNOTATIONS = eINSTANCE.getNodeDef_Annotations();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.NodeMappingSetImpl <em>Node Mapping Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.NodeMappingSetImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getNodeMappingSet()
		 * @generated
		 */
		EClass NODE_MAPPING_SET = eINSTANCE.getNodeMappingSet();

		/**
		 * The meta object literal for the '<em><b>Object Mappings</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE_MAPPING_SET__OBJECT_MAPPINGS = eINSTANCE.getNodeMappingSet_ObjectMappings();

		/**
		 * The meta object literal for the '<em><b>Property Mappings</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE_MAPPING_SET__PROPERTY_MAPPINGS = eINSTANCE.getNodeMappingSet_PropertyMappings();

		/**
		 * The meta object literal for the '<em><b>Association Mappings</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE_MAPPING_SET__ASSOCIATION_MAPPINGS = eINSTANCE.getNodeMappingSet_AssociationMappings();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.NoteImpl <em>Note</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.NoteImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getNote()
		 * @generated
		 */
		EClass NOTE = eINSTANCE.getNote();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTE__KEY = eINSTANCE.getNote_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTE__VALUE = eINSTANCE.getNote_Value();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ObjMappingImpl <em>Obj Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ObjMappingImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getObjMapping()
		 * @generated
		 */
		EClass OBJ_MAPPING = eINSTANCE.getObjMapping();

		/**
		 * The meta object literal for the '<em><b>Root Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OBJ_MAPPING__ROOT_PATH = eINSTANCE.getObjMapping_RootPath();

		/**
		 * The meta object literal for the '<em><b>Multiply Represented</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute OBJ_MAPPING__MULTIPLY_REPRESENTED = eINSTANCE.getObjMapping_MultiplyRepresented();

		/**
		 * The meta object literal for the '<em><b>Model Filter Set</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OBJ_MAPPING__MODEL_FILTER_SET = eINSTANCE.getObjMapping_ModelFilterSet();

		/**
		 * The meta object literal for the '<em><b>Fixed Property Values</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OBJ_MAPPING__FIXED_PROPERTY_VALUES = eINSTANCE.getObjMapping_FixedPropertyValues();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ParameterClassImpl <em>Parameter Class</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ParameterClassImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getParameterClass()
		 * @generated
		 */
		EClass PARAMETER_CLASS = eINSTANCE.getParameterClass();

		/**
		 * The meta object literal for the '<em><b>Class Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_CLASS__CLASS_NAME = eINSTANCE.getParameterClass_ClassName();

		/**
		 * The meta object literal for the '<em><b>Package Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_CLASS__PACKAGE_NAME = eINSTANCE.getParameterClass_PackageName();

		/**
		 * The meta object literal for the '<em><b>Parameter Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_CLASS__PARAMETER_INDEX = eINSTANCE.getParameterClass_ParameterIndex();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ParameterClassValueImpl <em>Parameter Class Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ParameterClassValueImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getParameterClassValue()
		 * @generated
		 */
		EClass PARAMETER_CLASS_VALUE = eINSTANCE.getParameterClassValue();

		/**
		 * The meta object literal for the '<em><b>Mapped Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_CLASS_VALUE__MAPPED_CLASS = eINSTANCE.getParameterClassValue_MappedClass();

		/**
		 * The meta object literal for the '<em><b>Mapped Package</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_CLASS_VALUE__MAPPED_PACKAGE = eINSTANCE.getParameterClassValue_MappedPackage();

		/**
		 * The meta object literal for the '<em><b>Subset</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_CLASS_VALUE__SUBSET = eINSTANCE.getParameterClassValue_Subset();

		/**
		 * The meta object literal for the '<em><b>Parameter Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_CLASS_VALUE__PARAMETER_INDEX = eINSTANCE.getParameterClassValue_ParameterIndex();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.PropMappingImpl <em>Prop Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.PropMappingImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getPropMapping()
		 * @generated
		 */
		EClass PROP_MAPPING = eINSTANCE.getPropMapping();

		/**
		 * The meta object literal for the '<em><b>Mapped Property</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROP_MAPPING__MAPPED_PROPERTY = eINSTANCE.getPropMapping_MappedProperty();

		/**
		 * The meta object literal for the '<em><b>Property Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROP_MAPPING__PROPERTY_TYPE = eINSTANCE.getPropMapping_PropertyType();

		/**
		 * The meta object literal for the '<em><b>Default Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROP_MAPPING__DEFAULT_VALUE = eINSTANCE.getPropMapping_DefaultValue();

		/**
		 * The meta object literal for the '<em><b>Object To Property Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROP_MAPPING__OBJECT_TO_PROPERTY_PATH = eINSTANCE.getPropMapping_ObjectToPropertyPath();

		/**
		 * The meta object literal for the '<em><b>Local Property Conversion</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROP_MAPPING__LOCAL_PROPERTY_CONVERSION = eINSTANCE.getPropMapping_LocalPropertyConversion();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.PropertyConversionImpl <em>Property Conversion</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.PropertyConversionImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getPropertyConversion()
		 * @generated
		 */
		EClass PROPERTY_CONVERSION = eINSTANCE.getPropertyConversion();

		/**
		 * The meta object literal for the '<em><b>Subset</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY_CONVERSION__SUBSET = eINSTANCE.getPropertyConversion_Subset();

		/**
		 * The meta object literal for the '<em><b>Result Slot</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY_CONVERSION__RESULT_SLOT = eINSTANCE.getPropertyConversion_ResultSlot();

		/**
		 * The meta object literal for the '<em><b>Sense</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY_CONVERSION__SENSE = eINSTANCE.getPropertyConversion_Sense();

		/**
		 * The meta object literal for the '<em><b>Conversion Implementations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS = eINSTANCE.getPropertyConversion_ConversionImplementations();

		/**
		 * The meta object literal for the '<em><b>Conversion Arguments</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTY_CONVERSION__CONVERSION_ARGUMENTS = eINSTANCE.getPropertyConversion_ConversionArguments();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY_CONVERSION__DESCRIPTION = eINSTANCE.getPropertyConversion_Description();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ValueConditionImpl <em>Value Condition</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ValueConditionImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getValueCondition()
		 * @generated
		 */
		EClass VALUE_CONDITION = eINSTANCE.getValueCondition();

		/**
		 * The meta object literal for the '<em><b>Right Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE_CONDITION__RIGHT_VALUE = eINSTANCE.getValueCondition_RightValue();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.XSLTConversionImplementationImpl <em>XSLT Conversion Implementation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.XSLTConversionImplementationImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getXSLTConversionImplementation()
		 * @generated
		 */
		EClass XSLT_CONVERSION_IMPLEMENTATION = eINSTANCE.getXSLTConversionImplementation();

		/**
		 * The meta object literal for the '<em><b>Template Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_NAME = eINSTANCE.getXSLTConversionImplementation_TemplateName();

		/**
		 * The meta object literal for the '<em><b>Template File URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_FILE_URI = eINSTANCE.getXSLTConversionImplementation_TemplateFileURI();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.impl.ValuePairImpl <em>Value Pair</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.impl.ValuePairImpl
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getValuePair()
		 * @generated
		 */
		EClass VALUE_PAIR = eINSTANCE.getValuePair();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE_PAIR__DESCRIPTION = eINSTANCE.getValuePair_Description();

		/**
		 * The meta object literal for the '<em><b>Structure Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE_PAIR__STRUCTURE_VALUE = eINSTANCE.getValuePair_StructureValue();

		/**
		 * The meta object literal for the '<em><b>Model Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE_PAIR__MODEL_VALUE = eINSTANCE.getValuePair_ModelValue();

		/**
		 * The meta object literal for the '<em><b>Preferred In</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE_PAIR__PREFERRED_IN = eINSTANCE.getValuePair_PreferredIn();

		/**
		 * The meta object literal for the '<em><b>Preferred Out</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE_PAIR__PREFERRED_OUT = eINSTANCE.getValuePair_PreferredOut();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.ConditionTest <em>Condition Test</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.ConditionTest
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getConditionTest()
		 * @generated
		 */
		EEnum CONDITION_TEST = eINSTANCE.getConditionTest();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.ConversionSense <em>Conversion Sense</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.ConversionSense
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getConversionSense()
		 * @generated
		 */
		EEnum CONVERSION_SENSE = eINSTANCE.getConversionSense();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.MaxMult <em>Max Mult</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.MaxMult
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMaxMult()
		 * @generated
		 */
		EEnum MAX_MULT = eINSTANCE.getMaxMult();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.MinMult <em>Min Mult</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.MinMult
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMinMult()
		 * @generated
		 */
		EEnum MIN_MULT = eINSTANCE.getMinMult();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.MultiWay <em>Multi Way</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.MultiWay
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMultiWay()
		 * @generated
		 */
		EEnum MULTI_WAY = eINSTANCE.getMultiWay();

		/**
		 * The meta object literal for the '{@link com.openMap1.mapper.StructureType <em>Structure Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.openMap1.mapper.StructureType
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getStructureType()
		 * @generated
		 */
		EEnum STRUCTURE_TYPE = eINSTANCE.getStructureType();

		/**
		 * The meta object literal for the '<em>Diagnostic Chain</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.DiagnosticChain
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getDiagnosticChain()
		 * @generated
		 */
		EDataType DIAGNOSTIC_CHAIN = eINSTANCE.getDiagnosticChain();

		/**
		 * The meta object literal for the '<em>Map</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.Map
		 * @see com.openMap1.mapper.impl.MapperPackageImpl#getMap()
		 * @generated
		 */
		EDataType MAP = eINSTANCE.getMap();
		
		// to force recompile

	}

} //MapperPackage
