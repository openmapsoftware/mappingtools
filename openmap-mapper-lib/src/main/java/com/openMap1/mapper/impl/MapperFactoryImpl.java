/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import com.openMap1.mapper.Annotations;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.ConditionTest;
import com.openMap1.mapper.ConversionArgument;
import com.openMap1.mapper.ConversionSense;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.JavaConversionImplementation;
import com.openMap1.mapper.LocalPropertyConversion;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.ModelAssocFilter;
import com.openMap1.mapper.ModelFilterSet;
import com.openMap1.mapper.ModelPropertyFilter;
import com.openMap1.mapper.MultiWay;
import com.openMap1.mapper.Namespace;
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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MapperFactoryImpl extends EFactoryImpl implements MapperFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MapperFactory init() {
		try {
			MapperFactory theMapperFactory = (MapperFactory)EPackage.Registry.INSTANCE.getEFactory("http:///com/openMap1/mapper.ecore"); 
			if (theMapperFactory != null) {
				return theMapperFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MapperFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MapperFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case MapperPackage.ANNOTATIONS: return createAnnotations();
			case MapperPackage.ASSOC_END_MAPPING: return createAssocEndMapping();
			case MapperPackage.ASSOC_MAPPING: return createAssocMapping();
			case MapperPackage.ATTRIBUTE_DEF: return createAttributeDef();
			case MapperPackage.CLASS_DETAILS: return createClassDetails();
			case MapperPackage.CONVERSION_ARGUMENT: return createConversionArgument();
			case MapperPackage.CROSS_CONDITION: return createCrossCondition();
			case MapperPackage.ELEMENT_DEF: return createElementDef();
			case MapperPackage.FIXED_PROPERTY_VALUE: return createFixedPropertyValue();
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS: return createGlobalMappingParameters();
			case MapperPackage.IMPORT_MAPPING_SET: return createImportMappingSet();
			case MapperPackage.JAVA_CONVERSION_IMPLEMENTATION: return createJavaConversionImplementation();
			case MapperPackage.LOCAL_PROPERTY_CONVERSION: return createLocalPropertyConversion();
			case MapperPackage.MAPPED_STRUCTURE: return createMappedStructure();
			case MapperPackage.MODEL_ASSOC_FILTER: return createModelAssocFilter();
			case MapperPackage.MODEL_FILTER_SET: return createModelFilterSet();
			case MapperPackage.MODEL_PROPERTY_FILTER: return createModelPropertyFilter();
			case MapperPackage.NAMESPACE: return createNamespace();
			case MapperPackage.NODE_MAPPING_SET: return createNodeMappingSet();
			case MapperPackage.NOTE: return createNote();
			case MapperPackage.OBJ_MAPPING: return createObjMapping();
			case MapperPackage.PARAMETER_CLASS: return createParameterClass();
			case MapperPackage.PARAMETER_CLASS_VALUE: return createParameterClassValue();
			case MapperPackage.PROP_MAPPING: return createPropMapping();
			case MapperPackage.PROPERTY_CONVERSION: return createPropertyConversion();
			case MapperPackage.VALUE_CONDITION: return createValueCondition();
			case MapperPackage.VALUE_PAIR: return createValuePair();
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION: return createXSLTConversionImplementation();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case MapperPackage.CONDITION_TEST:
				return createConditionTestFromString(eDataType, initialValue);
			case MapperPackage.CONVERSION_SENSE:
				return createConversionSenseFromString(eDataType, initialValue);
			case MapperPackage.MAX_MULT:
				return createMaxMultFromString(eDataType, initialValue);
			case MapperPackage.MIN_MULT:
				return createMinMultFromString(eDataType, initialValue);
			case MapperPackage.MULTI_WAY:
				return createMultiWayFromString(eDataType, initialValue);
			case MapperPackage.STRUCTURE_TYPE:
				return createStructureTypeFromString(eDataType, initialValue);
			case MapperPackage.DIAGNOSTIC_CHAIN:
				return createDiagnosticChainFromString(eDataType, initialValue);
			case MapperPackage.MAP:
				return createMapFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case MapperPackage.CONDITION_TEST:
				return convertConditionTestToString(eDataType, instanceValue);
			case MapperPackage.CONVERSION_SENSE:
				return convertConversionSenseToString(eDataType, instanceValue);
			case MapperPackage.MAX_MULT:
				return convertMaxMultToString(eDataType, instanceValue);
			case MapperPackage.MIN_MULT:
				return convertMinMultToString(eDataType, instanceValue);
			case MapperPackage.MULTI_WAY:
				return convertMultiWayToString(eDataType, instanceValue);
			case MapperPackage.STRUCTURE_TYPE:
				return convertStructureTypeToString(eDataType, instanceValue);
			case MapperPackage.DIAGNOSTIC_CHAIN:
				return convertDiagnosticChainToString(eDataType, instanceValue);
			case MapperPackage.MAP:
				return convertMapToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Annotations createAnnotations() {
		AnnotationsImpl annotations = new AnnotationsImpl();
		return annotations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AssocEndMapping createAssocEndMapping() {
		AssocEndMappingImpl assocEndMapping = new AssocEndMappingImpl();
		return assocEndMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AssocMapping createAssocMapping() {
		AssocMappingImpl assocMapping = new AssocMappingImpl();
		return assocMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributeDef createAttributeDef() {
		AttributeDefImpl attributeDef = new AttributeDefImpl();
		return attributeDef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ClassDetails createClassDetails() {
		ClassDetailsImpl classDetails = new ClassDetailsImpl();
		return classDetails;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConversionArgument createConversionArgument() {
		ConversionArgumentImpl conversionArgument = new ConversionArgumentImpl();
		return conversionArgument;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CrossCondition createCrossCondition() {
		CrossConditionImpl crossCondition = new CrossConditionImpl();
		return crossCondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ElementDef createElementDef() {
		ElementDefImpl elementDef = new ElementDefImpl();
		return elementDef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FixedPropertyValue createFixedPropertyValue() {
		FixedPropertyValueImpl fixedPropertyValue = new FixedPropertyValueImpl();
		return fixedPropertyValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GlobalMappingParameters createGlobalMappingParameters() {
		GlobalMappingParametersImpl globalMappingParameters = new GlobalMappingParametersImpl();
		return globalMappingParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImportMappingSet createImportMappingSet() {
		ImportMappingSetImpl importMappingSet = new ImportMappingSetImpl();
		return importMappingSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public JavaConversionImplementation createJavaConversionImplementation() {
		JavaConversionImplementationImpl javaConversionImplementation = new JavaConversionImplementationImpl();
		return javaConversionImplementation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalPropertyConversion createLocalPropertyConversion() {
		LocalPropertyConversionImpl localPropertyConversion = new LocalPropertyConversionImpl();
		return localPropertyConversion;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MappedStructure createMappedStructure() {
		MappedStructureImpl mappedStructure = new MappedStructureImpl();
		return mappedStructure;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelAssocFilter createModelAssocFilter() {
		ModelAssocFilterImpl modelAssocFilter = new ModelAssocFilterImpl();
		return modelAssocFilter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelFilterSet createModelFilterSet() {
		ModelFilterSetImpl modelFilterSet = new ModelFilterSetImpl();
		return modelFilterSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelPropertyFilter createModelPropertyFilter() {
		ModelPropertyFilterImpl modelPropertyFilter = new ModelPropertyFilterImpl();
		return modelPropertyFilter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Namespace createNamespace() {
		NamespaceImpl namespace = new NamespaceImpl();
		return namespace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NodeMappingSet createNodeMappingSet() {
		NodeMappingSetImpl nodeMappingSet = new NodeMappingSetImpl();
		return nodeMappingSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Note createNote() {
		NoteImpl note = new NoteImpl();
		return note;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ObjMapping createObjMapping() {
		ObjMappingImpl objMapping = new ObjMappingImpl();
		return objMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterClass createParameterClass() {
		ParameterClassImpl parameterClass = new ParameterClassImpl();
		return parameterClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterClassValue createParameterClassValue() {
		ParameterClassValueImpl parameterClassValue = new ParameterClassValueImpl();
		return parameterClassValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PropMapping createPropMapping() {
		PropMappingImpl propMapping = new PropMappingImpl();
		return propMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PropertyConversion createPropertyConversion() {
		PropertyConversionImpl propertyConversion = new PropertyConversionImpl();
		return propertyConversion;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ValueCondition createValueCondition() {
		ValueConditionImpl valueCondition = new ValueConditionImpl();
		return valueCondition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ValuePair createValuePair() {
		ValuePairImpl valuePair = new ValuePairImpl();
		return valuePair;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public XSLTConversionImplementation createXSLTConversionImplementation() {
		XSLTConversionImplementationImpl xsltConversionImplementation = new XSLTConversionImplementationImpl();
		return xsltConversionImplementation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionTest createConditionTestFromString(EDataType eDataType, String initialValue) {
		ConditionTest result = ConditionTest.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConditionTestToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConversionSense createConversionSenseFromString(EDataType eDataType, String initialValue) {
		ConversionSense result = ConversionSense.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertConversionSenseToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MaxMult createMaxMultFromString(EDataType eDataType, String initialValue) {
		MaxMult result = MaxMult.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertMaxMultToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MinMult createMinMultFromString(EDataType eDataType, String initialValue) {
		MinMult result = MinMult.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertMinMultToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MultiWay createMultiWayFromString(EDataType eDataType, String initialValue) {
		MultiWay result = MultiWay.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertMultiWayToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StructureType createStructureTypeFromString(EDataType eDataType, String initialValue) {
		StructureType result = StructureType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertStructureTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DiagnosticChain createDiagnosticChainFromString(EDataType eDataType, String initialValue) {
		return (DiagnosticChain)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDiagnosticChainToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * fixed to remove a compiler warning of an unchecked type cast; 
	 * but it did not work
	 * <!-- end-user-doc -->
	 */
	public Map<?,?> createMapFromString(EDataType eDataType, String initialValue) {
		Object result = super.createFromString(eDataType, initialValue);
		if (result instanceof Map<?,?>) return (Map<?,?>)result;
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertMapToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MapperPackage getMapperPackage() {
		return (MapperPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MapperPackage getPackage() {
		return MapperPackage.eINSTANCE;
	}

} //MapperFactoryImpl
