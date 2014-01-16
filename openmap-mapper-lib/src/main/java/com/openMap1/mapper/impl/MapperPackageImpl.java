/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import com.openMap1.mapper.util.MapperValidator;
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
import com.openMap1.mapper.MapperFactory;
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
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MapperPackageImpl extends EPackageImpl implements MapperPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass annotationsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass assocEndMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass assocMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass attributeDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass classDetailsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conversionArgumentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conversionImplementationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass crossConditionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass elementDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass fixedPropertyValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalMappingParametersEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass importMappingSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass javaConversionImplementationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localPropertyConversionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mappedStructureEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mappingConditionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelAssocFilterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelFilterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelFilterSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelPropertyFilterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namespaceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass nodeDefEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass nodeMappingSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass noteEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass objMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterClassEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterClassValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertyConversionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass valueConditionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xsltConversionImplementationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass valuePairEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum conditionTestEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum conversionSenseEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum maxMultEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum minMultEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum multiWayEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum structureTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType diagnosticChainEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType mapEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see com.openMap1.mapper.MapperPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private MapperPackageImpl() {
		super(eNS_URI, MapperFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * I have added a workaround to put the correct MapperPackageImpl in the 
	 * EMF EPackage registry (rather than the EPackage.Descriptor which is currently there),
	 * just before using the package registry,
	 * to avoid a ClassNotFoundExcpetion when the Descriptor tries to load 
	 * com.openMap1.mapper.MapperPackage.
	 * See notes of 28/10/08 for detailed traces of this bug - I don't know how it 
	 * arose, when I was deleting/importing projects the day before
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 */
	public static MapperPackage init() {
		if (isInited) return (MapperPackage)EPackage.Registry.INSTANCE.getEPackage(MapperPackage.eNS_URI);
		
		// RW fix to avoid the classNotFoundException - see notes of 28/10/08
		EPackage.Registry.INSTANCE.put(eNS_URI, new MapperPackageImpl());
		
		// Obtain or create and register package
		MapperPackageImpl theMapperPackage = (MapperPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof MapperPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new MapperPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theMapperPackage.createPackageContents();

		// Initialize created meta-data
		theMapperPackage.initializePackageContents();

		// Register package validator
		EValidator.Registry.INSTANCE.put
			(theMapperPackage, 
			 new EValidator.Descriptor() {
				 public EValidator getEValidator() {
					 return MapperValidator.INSTANCE;
				 }
			 });

		// Mark meta-data to indicate it can't be changed
		theMapperPackage.freeze();

		return theMapperPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAnnotations() {
		return annotationsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAnnotations_Notes() {
		return (EReference)annotationsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAssocEndMapping() {
		return assocEndMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssocEndMapping_MappedRole() {
		return (EAttribute)assocEndMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssocEndMapping_ObjectToAssociationPath() {
		return (EAttribute)assocEndMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssocEndMapping_AssociationToObjectPath() {
		return (EAttribute)assocEndMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssocEndMapping_RequiredForObject() {
		return (EAttribute)assocEndMappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAssocMapping() {
		return assocMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAssocMapping_MappedEnd1() {
		return (EReference)assocMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAssocMapping_MappedEnd2() {
		return (EReference)assocMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAttributeDef() {
		return attributeDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getClassDetails() {
		return classDetailsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getClassDetails_ClassName() {
		return (EAttribute)classDetailsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getClassDetails_PackageName() {
		return (EAttribute)classDetailsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getClassDetails_PropertyConversions() {
		return (EReference)classDetailsEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConversionArgument() {
		return conversionArgumentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getConversionArgument_PropertyName() {
		return (EAttribute)conversionArgumentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getConversionImplementation() {
		return conversionImplementationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCrossCondition() {
		return crossConditionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCrossCondition_RightPath() {
		return (EAttribute)crossConditionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCrossCondition_RightFunction() {
		return (EAttribute)crossConditionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCrossCondition_RightPathConditions() {
		return (EReference)crossConditionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getElementDef() {
		return elementDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getElementDef_Expanded() {
		return (EAttribute)elementDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getElementDef_MaxMultiplicity() {
		return (EAttribute)elementDefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getElementDef_ChildElements() {
		return (EReference)elementDefEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getElementDef_AttributeDefs() {
		return (EReference)elementDefEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getElementDef_ImportMappingSet() {
		return (EReference)elementDefEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getFixedPropertyValue() {
		return fixedPropertyValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getFixedPropertyValue_MappedProperty() {
		return (EAttribute)fixedPropertyValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getFixedPropertyValue_FixedValue() {
		return (EAttribute)fixedPropertyValueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getFixedPropertyValue_ValueType() {
		return (EAttribute)fixedPropertyValueEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGlobalMappingParameters() {
		return globalMappingParametersEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGlobalMappingParameters_MappingClass() {
		return (EAttribute)globalMappingParametersEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGlobalMappingParameters_WrapperClass() {
		return (EAttribute)globalMappingParametersEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGlobalMappingParameters_NameSpaces() {
		return (EReference)globalMappingParametersEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGlobalMappingParameters_ClassDetails() {
		return (EReference)globalMappingParametersEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getImportMappingSet() {
		return importMappingSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getImportMappingSet_MappingSetURI() {
		return (EAttribute)importMappingSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getImportMappingSet_ParameterClassValues() {
		return (EReference)importMappingSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getJavaConversionImplementation() {
		return javaConversionImplementationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJavaConversionImplementation_ClassName() {
		return (EAttribute)javaConversionImplementationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJavaConversionImplementation_MethodName() {
		return (EAttribute)javaConversionImplementationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJavaConversionImplementation_PackageName() {
		return (EAttribute)javaConversionImplementationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalPropertyConversion() {
		return localPropertyConversionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLocalPropertyConversion_Description() {
		return (EAttribute)localPropertyConversionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalPropertyConversion_InConversionImplementations() {
		return (EReference)localPropertyConversionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalPropertyConversion_OutConversionImplementations() {
		return (EReference)localPropertyConversionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalPropertyConversion_ValuePairs() {
		return (EReference)localPropertyConversionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMappedStructure() {
		return mappedStructureEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappedStructure_Name() {
		return (EAttribute)mappedStructureEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMappedStructure_RootElement() {
		return (EReference)mappedStructureEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappedStructure_UMLModelURL() {
		return (EAttribute)mappedStructureEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappedStructure_StructureType() {
		return (EAttribute)mappedStructureEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappedStructure_StructureURL() {
		return (EAttribute)mappedStructureEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappedStructure_TopElementType() {
		return (EAttribute)mappedStructureEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappedStructure_TopElementName() {
		return (EAttribute)mappedStructureEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMappedStructure_MappingParameters() {
		return (EReference)mappedStructureEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMappedStructure_ParameterClasses() {
		return (EReference)mappedStructureEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMapping() {
		return mappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMapping_MappedClass() {
		return (EAttribute)mappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMapping_MappedPackage() {
		return (EAttribute)mappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMapping_Subset() {
		return (EAttribute)mappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMapping_Description() {
		return (EAttribute)mappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMapping_MappingConditions() {
		return (EReference)mappingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMapping_MultiWay() {
		return (EAttribute)mappingEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMapping_BreakPoint() {
		return (EAttribute)mappingEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMappingCondition() {
		return mappingConditionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappingCondition_LeftPath() {
		return (EAttribute)mappingConditionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappingCondition_LeftFunction() {
		return (EAttribute)mappingConditionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappingCondition_Test() {
		return (EAttribute)mappingConditionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMappingCondition_Description() {
		return (EAttribute)mappingConditionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMappingCondition_LeftPathConditions() {
		return (EReference)mappingConditionEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModelAssocFilter() {
		return modelAssocFilterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelAssocFilter_RoleName() {
		return (EAttribute)modelAssocFilterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelAssocFilter_OtherClassName() {
		return (EAttribute)modelAssocFilterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelAssocFilter_OtherPackageName() {
		return (EAttribute)modelAssocFilterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelAssocFilter_OtherSubset() {
		return (EAttribute)modelAssocFilterEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModelFilter() {
		return modelFilterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelFilter_Description() {
		return (EAttribute)modelFilterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModelFilterSet() {
		return modelFilterSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelFilterSet_Description() {
		return (EAttribute)modelFilterSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getModelFilterSet_ModelFilters() {
		return (EReference)modelFilterSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModelPropertyFilter() {
		return modelPropertyFilterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelPropertyFilter_PropertyName() {
		return (EAttribute)modelPropertyFilterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelPropertyFilter_Value() {
		return (EAttribute)modelPropertyFilterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModelPropertyFilter_Test() {
		return (EAttribute)modelPropertyFilterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNamespace() {
		return namespaceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNamespace_URL() {
		return (EAttribute)namespaceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNamespace_Prefix() {
		return (EAttribute)namespaceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNodeDef() {
		return nodeDefEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNodeDef_Name() {
		return (EAttribute)nodeDefEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNodeDef_Type() {
		return (EAttribute)nodeDefEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNodeDef_Description() {
		return (EAttribute)nodeDefEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNodeDef_MinMultiplicity() {
		return (EAttribute)nodeDefEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNodeDef_DefaultValue() {
		return (EAttribute)nodeDefEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNodeDef_FixedValue() {
		return (EAttribute)nodeDefEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNodeDef_NodeMappingSet() {
		return (EReference)nodeDefEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNodeDef_Annotations() {
		return (EReference)nodeDefEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNodeMappingSet() {
		return nodeMappingSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNodeMappingSet_ObjectMappings() {
		return (EReference)nodeMappingSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNodeMappingSet_PropertyMappings() {
		return (EReference)nodeMappingSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNodeMappingSet_AssociationMappings() {
		return (EReference)nodeMappingSetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNote() {
		return noteEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNote_Key() {
		return (EAttribute)noteEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNote_Value() {
		return (EAttribute)noteEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getObjMapping() {
		return objMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getObjMapping_RootPath() {
		return (EAttribute)objMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getObjMapping_MultiplyRepresented() {
		return (EAttribute)objMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getObjMapping_ModelFilterSet() {
		return (EReference)objMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getObjMapping_FixedPropertyValues() {
		return (EReference)objMappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameterClass() {
		return parameterClassEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterClass_ClassName() {
		return (EAttribute)parameterClassEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterClass_PackageName() {
		return (EAttribute)parameterClassEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterClass_ParameterIndex() {
		return (EAttribute)parameterClassEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameterClassValue() {
		return parameterClassValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterClassValue_MappedClass() {
		return (EAttribute)parameterClassValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterClassValue_MappedPackage() {
		return (EAttribute)parameterClassValueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterClassValue_Subset() {
		return (EAttribute)parameterClassValueEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameterClassValue_ParameterIndex() {
		return (EAttribute)parameterClassValueEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPropMapping() {
		return propMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropMapping_MappedProperty() {
		return (EAttribute)propMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropMapping_PropertyType() {
		return (EAttribute)propMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropMapping_DefaultValue() {
		return (EAttribute)propMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropMapping_ObjectToPropertyPath() {
		return (EAttribute)propMappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPropMapping_LocalPropertyConversion() {
		return (EReference)propMappingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPropertyConversion() {
		return propertyConversionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropertyConversion_Subset() {
		return (EAttribute)propertyConversionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropertyConversion_ResultSlot() {
		return (EAttribute)propertyConversionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropertyConversion_Sense() {
		return (EAttribute)propertyConversionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPropertyConversion_ConversionImplementations() {
		return (EReference)propertyConversionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPropertyConversion_ConversionArguments() {
		return (EReference)propertyConversionEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropertyConversion_Description() {
		return (EAttribute)propertyConversionEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getValueCondition() {
		return valueConditionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValueCondition_RightValue() {
		return (EAttribute)valueConditionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getXSLTConversionImplementation() {
		return xsltConversionImplementationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getXSLTConversionImplementation_TemplateName() {
		return (EAttribute)xsltConversionImplementationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getXSLTConversionImplementation_TemplateFileURI() {
		return (EAttribute)xsltConversionImplementationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getValuePair() {
		return valuePairEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValuePair_Description() {
		return (EAttribute)valuePairEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValuePair_StructureValue() {
		return (EAttribute)valuePairEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValuePair_ModelValue() {
		return (EAttribute)valuePairEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValuePair_PreferredIn() {
		return (EAttribute)valuePairEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValuePair_PreferredOut() {
		return (EAttribute)valuePairEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getConditionTest() {
		return conditionTestEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getConversionSense() {
		return conversionSenseEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getMaxMult() {
		return maxMultEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getMinMult() {
		return minMultEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getMultiWay() {
		return multiWayEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getStructureType() {
		return structureTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getDiagnosticChain() {
		return diagnosticChainEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getMap() {
		return mapEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MapperFactory getMapperFactory() {
		return (MapperFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		annotationsEClass = createEClass(ANNOTATIONS);
		createEReference(annotationsEClass, ANNOTATIONS__NOTES);

		assocEndMappingEClass = createEClass(ASSOC_END_MAPPING);
		createEAttribute(assocEndMappingEClass, ASSOC_END_MAPPING__MAPPED_ROLE);
		createEAttribute(assocEndMappingEClass, ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH);
		createEAttribute(assocEndMappingEClass, ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH);
		createEAttribute(assocEndMappingEClass, ASSOC_END_MAPPING__REQUIRED_FOR_OBJECT);

		assocMappingEClass = createEClass(ASSOC_MAPPING);
		createEReference(assocMappingEClass, ASSOC_MAPPING__MAPPED_END1);
		createEReference(assocMappingEClass, ASSOC_MAPPING__MAPPED_END2);

		attributeDefEClass = createEClass(ATTRIBUTE_DEF);

		classDetailsEClass = createEClass(CLASS_DETAILS);
		createEAttribute(classDetailsEClass, CLASS_DETAILS__CLASS_NAME);
		createEAttribute(classDetailsEClass, CLASS_DETAILS__PACKAGE_NAME);
		createEReference(classDetailsEClass, CLASS_DETAILS__PROPERTY_CONVERSIONS);

		conversionArgumentEClass = createEClass(CONVERSION_ARGUMENT);
		createEAttribute(conversionArgumentEClass, CONVERSION_ARGUMENT__PROPERTY_NAME);

		conversionImplementationEClass = createEClass(CONVERSION_IMPLEMENTATION);

		crossConditionEClass = createEClass(CROSS_CONDITION);
		createEAttribute(crossConditionEClass, CROSS_CONDITION__RIGHT_PATH);
		createEAttribute(crossConditionEClass, CROSS_CONDITION__RIGHT_FUNCTION);
		createEReference(crossConditionEClass, CROSS_CONDITION__RIGHT_PATH_CONDITIONS);

		elementDefEClass = createEClass(ELEMENT_DEF);
		createEAttribute(elementDefEClass, ELEMENT_DEF__EXPANDED);
		createEAttribute(elementDefEClass, ELEMENT_DEF__MAX_MULTIPLICITY);
		createEReference(elementDefEClass, ELEMENT_DEF__CHILD_ELEMENTS);
		createEReference(elementDefEClass, ELEMENT_DEF__ATTRIBUTE_DEFS);
		createEReference(elementDefEClass, ELEMENT_DEF__IMPORT_MAPPING_SET);

		fixedPropertyValueEClass = createEClass(FIXED_PROPERTY_VALUE);
		createEAttribute(fixedPropertyValueEClass, FIXED_PROPERTY_VALUE__MAPPED_PROPERTY);
		createEAttribute(fixedPropertyValueEClass, FIXED_PROPERTY_VALUE__FIXED_VALUE);
		createEAttribute(fixedPropertyValueEClass, FIXED_PROPERTY_VALUE__VALUE_TYPE);

		globalMappingParametersEClass = createEClass(GLOBAL_MAPPING_PARAMETERS);
		createEAttribute(globalMappingParametersEClass, GLOBAL_MAPPING_PARAMETERS__MAPPING_CLASS);
		createEAttribute(globalMappingParametersEClass, GLOBAL_MAPPING_PARAMETERS__WRAPPER_CLASS);
		createEReference(globalMappingParametersEClass, GLOBAL_MAPPING_PARAMETERS__NAME_SPACES);
		createEReference(globalMappingParametersEClass, GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS);

		importMappingSetEClass = createEClass(IMPORT_MAPPING_SET);
		createEAttribute(importMappingSetEClass, IMPORT_MAPPING_SET__MAPPING_SET_URI);
		createEReference(importMappingSetEClass, IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES);

		javaConversionImplementationEClass = createEClass(JAVA_CONVERSION_IMPLEMENTATION);
		createEAttribute(javaConversionImplementationEClass, JAVA_CONVERSION_IMPLEMENTATION__CLASS_NAME);
		createEAttribute(javaConversionImplementationEClass, JAVA_CONVERSION_IMPLEMENTATION__METHOD_NAME);
		createEAttribute(javaConversionImplementationEClass, JAVA_CONVERSION_IMPLEMENTATION__PACKAGE_NAME);

		localPropertyConversionEClass = createEClass(LOCAL_PROPERTY_CONVERSION);
		createEAttribute(localPropertyConversionEClass, LOCAL_PROPERTY_CONVERSION__DESCRIPTION);
		createEReference(localPropertyConversionEClass, LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS);
		createEReference(localPropertyConversionEClass, LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS);
		createEReference(localPropertyConversionEClass, LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS);

		mappedStructureEClass = createEClass(MAPPED_STRUCTURE);
		createEAttribute(mappedStructureEClass, MAPPED_STRUCTURE__NAME);
		createEReference(mappedStructureEClass, MAPPED_STRUCTURE__ROOT_ELEMENT);
		createEAttribute(mappedStructureEClass, MAPPED_STRUCTURE__UML_MODEL_URL);
		createEAttribute(mappedStructureEClass, MAPPED_STRUCTURE__STRUCTURE_TYPE);
		createEAttribute(mappedStructureEClass, MAPPED_STRUCTURE__STRUCTURE_URL);
		createEAttribute(mappedStructureEClass, MAPPED_STRUCTURE__TOP_ELEMENT_TYPE);
		createEAttribute(mappedStructureEClass, MAPPED_STRUCTURE__TOP_ELEMENT_NAME);
		createEReference(mappedStructureEClass, MAPPED_STRUCTURE__MAPPING_PARAMETERS);
		createEReference(mappedStructureEClass, MAPPED_STRUCTURE__PARAMETER_CLASSES);

		mappingEClass = createEClass(MAPPING);
		createEAttribute(mappingEClass, MAPPING__MAPPED_CLASS);
		createEAttribute(mappingEClass, MAPPING__MAPPED_PACKAGE);
		createEAttribute(mappingEClass, MAPPING__SUBSET);
		createEAttribute(mappingEClass, MAPPING__DESCRIPTION);
		createEReference(mappingEClass, MAPPING__MAPPING_CONDITIONS);
		createEAttribute(mappingEClass, MAPPING__MULTI_WAY);
		createEAttribute(mappingEClass, MAPPING__BREAK_POINT);

		mappingConditionEClass = createEClass(MAPPING_CONDITION);
		createEAttribute(mappingConditionEClass, MAPPING_CONDITION__LEFT_PATH);
		createEAttribute(mappingConditionEClass, MAPPING_CONDITION__LEFT_FUNCTION);
		createEAttribute(mappingConditionEClass, MAPPING_CONDITION__TEST);
		createEAttribute(mappingConditionEClass, MAPPING_CONDITION__DESCRIPTION);
		createEReference(mappingConditionEClass, MAPPING_CONDITION__LEFT_PATH_CONDITIONS);

		modelAssocFilterEClass = createEClass(MODEL_ASSOC_FILTER);
		createEAttribute(modelAssocFilterEClass, MODEL_ASSOC_FILTER__ROLE_NAME);
		createEAttribute(modelAssocFilterEClass, MODEL_ASSOC_FILTER__OTHER_CLASS_NAME);
		createEAttribute(modelAssocFilterEClass, MODEL_ASSOC_FILTER__OTHER_PACKAGE_NAME);
		createEAttribute(modelAssocFilterEClass, MODEL_ASSOC_FILTER__OTHER_SUBSET);

		modelFilterEClass = createEClass(MODEL_FILTER);
		createEAttribute(modelFilterEClass, MODEL_FILTER__DESCRIPTION);

		modelFilterSetEClass = createEClass(MODEL_FILTER_SET);
		createEAttribute(modelFilterSetEClass, MODEL_FILTER_SET__DESCRIPTION);
		createEReference(modelFilterSetEClass, MODEL_FILTER_SET__MODEL_FILTERS);

		modelPropertyFilterEClass = createEClass(MODEL_PROPERTY_FILTER);
		createEAttribute(modelPropertyFilterEClass, MODEL_PROPERTY_FILTER__PROPERTY_NAME);
		createEAttribute(modelPropertyFilterEClass, MODEL_PROPERTY_FILTER__VALUE);
		createEAttribute(modelPropertyFilterEClass, MODEL_PROPERTY_FILTER__TEST);

		namespaceEClass = createEClass(NAMESPACE);
		createEAttribute(namespaceEClass, NAMESPACE__URL);
		createEAttribute(namespaceEClass, NAMESPACE__PREFIX);

		nodeDefEClass = createEClass(NODE_DEF);
		createEAttribute(nodeDefEClass, NODE_DEF__NAME);
		createEAttribute(nodeDefEClass, NODE_DEF__TYPE);
		createEAttribute(nodeDefEClass, NODE_DEF__DESCRIPTION);
		createEAttribute(nodeDefEClass, NODE_DEF__MIN_MULTIPLICITY);
		createEAttribute(nodeDefEClass, NODE_DEF__DEFAULT_VALUE);
		createEAttribute(nodeDefEClass, NODE_DEF__FIXED_VALUE);
		createEReference(nodeDefEClass, NODE_DEF__NODE_MAPPING_SET);
		createEReference(nodeDefEClass, NODE_DEF__ANNOTATIONS);

		nodeMappingSetEClass = createEClass(NODE_MAPPING_SET);
		createEReference(nodeMappingSetEClass, NODE_MAPPING_SET__OBJECT_MAPPINGS);
		createEReference(nodeMappingSetEClass, NODE_MAPPING_SET__PROPERTY_MAPPINGS);
		createEReference(nodeMappingSetEClass, NODE_MAPPING_SET__ASSOCIATION_MAPPINGS);

		noteEClass = createEClass(NOTE);
		createEAttribute(noteEClass, NOTE__KEY);
		createEAttribute(noteEClass, NOTE__VALUE);

		objMappingEClass = createEClass(OBJ_MAPPING);
		createEAttribute(objMappingEClass, OBJ_MAPPING__ROOT_PATH);
		createEAttribute(objMappingEClass, OBJ_MAPPING__MULTIPLY_REPRESENTED);
		createEReference(objMappingEClass, OBJ_MAPPING__MODEL_FILTER_SET);
		createEReference(objMappingEClass, OBJ_MAPPING__FIXED_PROPERTY_VALUES);

		parameterClassEClass = createEClass(PARAMETER_CLASS);
		createEAttribute(parameterClassEClass, PARAMETER_CLASS__CLASS_NAME);
		createEAttribute(parameterClassEClass, PARAMETER_CLASS__PACKAGE_NAME);
		createEAttribute(parameterClassEClass, PARAMETER_CLASS__PARAMETER_INDEX);

		parameterClassValueEClass = createEClass(PARAMETER_CLASS_VALUE);
		createEAttribute(parameterClassValueEClass, PARAMETER_CLASS_VALUE__MAPPED_CLASS);
		createEAttribute(parameterClassValueEClass, PARAMETER_CLASS_VALUE__MAPPED_PACKAGE);
		createEAttribute(parameterClassValueEClass, PARAMETER_CLASS_VALUE__SUBSET);
		createEAttribute(parameterClassValueEClass, PARAMETER_CLASS_VALUE__PARAMETER_INDEX);

		propMappingEClass = createEClass(PROP_MAPPING);
		createEAttribute(propMappingEClass, PROP_MAPPING__MAPPED_PROPERTY);
		createEAttribute(propMappingEClass, PROP_MAPPING__PROPERTY_TYPE);
		createEAttribute(propMappingEClass, PROP_MAPPING__DEFAULT_VALUE);
		createEAttribute(propMappingEClass, PROP_MAPPING__OBJECT_TO_PROPERTY_PATH);
		createEReference(propMappingEClass, PROP_MAPPING__LOCAL_PROPERTY_CONVERSION);

		propertyConversionEClass = createEClass(PROPERTY_CONVERSION);
		createEAttribute(propertyConversionEClass, PROPERTY_CONVERSION__SUBSET);
		createEAttribute(propertyConversionEClass, PROPERTY_CONVERSION__RESULT_SLOT);
		createEAttribute(propertyConversionEClass, PROPERTY_CONVERSION__SENSE);
		createEReference(propertyConversionEClass, PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS);
		createEReference(propertyConversionEClass, PROPERTY_CONVERSION__CONVERSION_ARGUMENTS);
		createEAttribute(propertyConversionEClass, PROPERTY_CONVERSION__DESCRIPTION);

		valueConditionEClass = createEClass(VALUE_CONDITION);
		createEAttribute(valueConditionEClass, VALUE_CONDITION__RIGHT_VALUE);

		valuePairEClass = createEClass(VALUE_PAIR);
		createEAttribute(valuePairEClass, VALUE_PAIR__DESCRIPTION);
		createEAttribute(valuePairEClass, VALUE_PAIR__STRUCTURE_VALUE);
		createEAttribute(valuePairEClass, VALUE_PAIR__MODEL_VALUE);
		createEAttribute(valuePairEClass, VALUE_PAIR__PREFERRED_IN);
		createEAttribute(valuePairEClass, VALUE_PAIR__PREFERRED_OUT);

		xsltConversionImplementationEClass = createEClass(XSLT_CONVERSION_IMPLEMENTATION);
		createEAttribute(xsltConversionImplementationEClass, XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_NAME);
		createEAttribute(xsltConversionImplementationEClass, XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_FILE_URI);

		// Create enums
		conditionTestEEnum = createEEnum(CONDITION_TEST);
		conversionSenseEEnum = createEEnum(CONVERSION_SENSE);
		maxMultEEnum = createEEnum(MAX_MULT);
		minMultEEnum = createEEnum(MIN_MULT);
		multiWayEEnum = createEEnum(MULTI_WAY);
		structureTypeEEnum = createEEnum(STRUCTURE_TYPE);

		// Create data types
		diagnosticChainEDataType = createEDataType(DIAGNOSTIC_CHAIN);
		mapEDataType = createEDataType(MAP);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters
		addETypeParameter(mapEDataType, "T");
		addETypeParameter(mapEDataType, "T1");

		// Set bounds for type parameters

		// Add supertypes to classes
		assocEndMappingEClass.getESuperTypes().add(this.getMapping());
		assocMappingEClass.getESuperTypes().add(this.getMapping());
		attributeDefEClass.getESuperTypes().add(this.getNodeDef());
		crossConditionEClass.getESuperTypes().add(this.getMappingCondition());
		elementDefEClass.getESuperTypes().add(this.getNodeDef());
		javaConversionImplementationEClass.getESuperTypes().add(this.getConversionImplementation());
		modelAssocFilterEClass.getESuperTypes().add(this.getModelFilter());
		modelPropertyFilterEClass.getESuperTypes().add(this.getModelFilter());
		objMappingEClass.getESuperTypes().add(this.getMapping());
		propMappingEClass.getESuperTypes().add(this.getMapping());
		valueConditionEClass.getESuperTypes().add(this.getMappingCondition());
		xsltConversionImplementationEClass.getESuperTypes().add(this.getConversionImplementation());

		// Initialize classes and features; add operations and parameters
		initEClass(annotationsEClass, Annotations.class, "Annotations", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAnnotations_Notes(), this.getNote(), null, "notes", null, 0, -1, Annotations.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(assocEndMappingEClass, AssocEndMapping.class, "AssocEndMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAssocEndMapping_MappedRole(), ecorePackage.getEString(), "mappedRole", null, 0, 1, AssocEndMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssocEndMapping_ObjectToAssociationPath(), ecorePackage.getEString(), "objectToAssociationPath", null, 0, 1, AssocEndMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssocEndMapping_AssociationToObjectPath(), ecorePackage.getEString(), "associationToObjectPath", null, 0, 1, AssocEndMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssocEndMapping_RequiredForObject(), ecorePackage.getEBoolean(), "requiredForObject", null, 0, 1, AssocEndMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(assocEndMappingEClass, ecorePackage.getEBoolean(), "classHasRoleToClass", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		EGenericType g1 = createEGenericType(this.getMap());
		EGenericType g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(assocEndMappingEClass, ecorePackage.getEBoolean(), "objectMappingExists", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(assocEndMappingEClass, ecorePackage.getEBoolean(), "objectToAssociationPathIsValid", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(assocEndMappingEClass, ecorePackage.getEBoolean(), "PathMatchesCardinality", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(assocEndMappingEClass, ecorePackage.getEBoolean(), "AssociationToObjectPathIsValid", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(assocEndMappingEClass, ecorePackage.getEBoolean(), "objectIsUniqueFromAssociation", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(assocMappingEClass, AssocMapping.class, "AssocMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAssocMapping_MappedEnd1(), this.getAssocEndMapping(), null, "mappedEnd1", null, 0, 1, AssocMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAssocMapping_MappedEnd2(), this.getAssocEndMapping(), null, "mappedEnd2", null, 0, 1, AssocMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(attributeDefEClass, AttributeDef.class, "AttributeDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(classDetailsEClass, ClassDetails.class, "ClassDetails", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getClassDetails_ClassName(), ecorePackage.getEString(), "className", null, 0, 1, ClassDetails.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getClassDetails_PackageName(), ecorePackage.getEString(), "packageName", null, 0, 1, ClassDetails.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClassDetails_PropertyConversions(), this.getPropertyConversion(), null, "propertyConversions", null, 0, -1, ClassDetails.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(classDetailsEClass, ecorePackage.getEBoolean(), "classIsInClassModel", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(classDetailsEClass, ecorePackage.getEBoolean(), "onlyOneOutConversionPerPseudoPropertyAndSubset", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(classDetailsEClass, ecorePackage.getEBoolean(), "onlyOneInConversionPerPropertyAndSubset", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(classDetailsEClass, ecorePackage.getEBoolean(), "convertedPropertyIsNotRepresentedDirectly", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(conversionArgumentEClass, ConversionArgument.class, "ConversionArgument", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConversionArgument_PropertyName(), ecorePackage.getEString(), "propertyName", null, 0, 1, ConversionArgument.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(conversionArgumentEClass, ecorePackage.getEBoolean(), "classHasProperty", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(conversionArgumentEClass, ecorePackage.getEBoolean(), "propertyMappingExists", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(conversionImplementationEClass, ConversionImplementation.class, "ConversionImplementation", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(crossConditionEClass, CrossCondition.class, "CrossCondition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCrossCondition_RightPath(), ecorePackage.getEString(), "rightPath", null, 0, 1, CrossCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCrossCondition_RightFunction(), ecorePackage.getEString(), "rightFunction", null, 0, 1, CrossCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCrossCondition_RightPathConditions(), this.getMappingCondition(), null, "rightPathConditions", null, 0, -1, CrossCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(crossConditionEClass, ecorePackage.getEBoolean(), "rightPathIsValid", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(crossConditionEClass, ecorePackage.getEBoolean(), "rightPathGivesUniqueNode", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(crossConditionEClass, ecorePackage.getEBoolean(), "rightFunctionIsValid", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(elementDefEClass, ElementDef.class, "ElementDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getElementDef_Expanded(), ecorePackage.getEBoolean(), "expanded", null, 0, 1, ElementDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getElementDef_MaxMultiplicity(), this.getMaxMult(), "maxMultiplicity", null, 0, 1, ElementDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getElementDef_ChildElements(), this.getElementDef(), null, "childElements", null, 0, -1, ElementDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getElementDef_AttributeDefs(), this.getAttributeDef(), null, "attributeDefs", null, 0, -1, ElementDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getElementDef_ImportMappingSet(), this.getImportMappingSet(), null, "importMappingSet", null, 0, 1, ElementDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(elementDefEClass, ecorePackage.getEBoolean(), "noChildrenIfNotExpanded", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(elementDefEClass, ecorePackage.getEBoolean(), "hasAllChildrenIfExpanded", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(elementDefEClass, ecorePackage.getEBoolean(), "hasCorrectMaxMultiplicity", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(fixedPropertyValueEClass, FixedPropertyValue.class, "FixedPropertyValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFixedPropertyValue_MappedProperty(), ecorePackage.getEString(), "mappedProperty", null, 0, 1, FixedPropertyValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFixedPropertyValue_FixedValue(), ecorePackage.getEString(), "fixedValue", null, 0, 1, FixedPropertyValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFixedPropertyValue_ValueType(), ecorePackage.getEString(), "valueType", null, 0, 1, FixedPropertyValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(fixedPropertyValueEClass, ecorePackage.getEBoolean(), "classHasProperty", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(globalMappingParametersEClass, GlobalMappingParameters.class, "GlobalMappingParameters", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGlobalMappingParameters_MappingClass(), ecorePackage.getEString(), "mappingClass", null, 0, 1, GlobalMappingParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGlobalMappingParameters_WrapperClass(), ecorePackage.getEString(), "wrapperClass", null, 0, 1, GlobalMappingParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGlobalMappingParameters_NameSpaces(), this.getNamespace(), null, "nameSpaces", null, 0, -1, GlobalMappingParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGlobalMappingParameters_ClassDetails(), this.getClassDetails(), null, "classDetails", null, 0, -1, GlobalMappingParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(importMappingSetEClass, ImportMappingSet.class, "ImportMappingSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getImportMappingSet_MappingSetURI(), ecorePackage.getEString(), "mappingSetURI", null, 0, 1, ImportMappingSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getImportMappingSet_ParameterClassValues(), this.getParameterClassValue(), null, "parameterClassValues", null, 0, -1, ImportMappingSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(importMappingSetEClass, ecorePackage.getEBoolean(), "canFindMappingSet", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(importMappingSetEClass, ecorePackage.getEBoolean(), "mappingSetHasSameClassModel", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(importMappingSetEClass, ecorePackage.getEBoolean(), "mappingSetParametersMatch", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(importMappingSetEClass, ecorePackage.getEBoolean(), "mappingSetStructureMatches", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(javaConversionImplementationEClass, JavaConversionImplementation.class, "JavaConversionImplementation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getJavaConversionImplementation_ClassName(), ecorePackage.getEString(), "className", null, 0, 1, JavaConversionImplementation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJavaConversionImplementation_MethodName(), ecorePackage.getEString(), "methodName", null, 0, 1, JavaConversionImplementation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJavaConversionImplementation_PackageName(), ecorePackage.getEString(), "packageName", null, 0, 1, JavaConversionImplementation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(localPropertyConversionEClass, LocalPropertyConversion.class, "LocalPropertyConversion", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLocalPropertyConversion_Description(), ecorePackage.getEString(), "description", null, 0, 1, LocalPropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalPropertyConversion_InConversionImplementations(), this.getConversionImplementation(), null, "inConversionImplementations", null, 0, -1, LocalPropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalPropertyConversion_OutConversionImplementations(), this.getConversionImplementation(), null, "outConversionImplementations", null, 0, -1, LocalPropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalPropertyConversion_ValuePairs(), this.getValuePair(), null, "valuePairs", null, 0, -1, LocalPropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(mappedStructureEClass, MappedStructure.class, "MappedStructure", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMappedStructure_Name(), ecorePackage.getEString(), "name", null, 0, 1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMappedStructure_RootElement(), this.getElementDef(), null, "rootElement", null, 0, 1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappedStructure_UMLModelURL(), ecorePackage.getEString(), "uMLModelURL", null, 0, 1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappedStructure_StructureType(), this.getStructureType(), "structureType", null, 0, 1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappedStructure_StructureURL(), ecorePackage.getEString(), "structureURL", null, 0, 1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappedStructure_TopElementType(), ecorePackage.getEString(), "topElementType", null, 0, 1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappedStructure_TopElementName(), ecorePackage.getEString(), "topElementName", null, 0, 1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMappedStructure_MappingParameters(), this.getGlobalMappingParameters(), null, "mappingParameters", null, 0, 1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMappedStructure_ParameterClasses(), this.getParameterClass(), null, "parameterClasses", null, 0, -1, MappedStructure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(mappedStructureEClass, ecorePackage.getEBoolean(), "canFindClassModel", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(mappedStructureEClass, ecorePackage.getEBoolean(), "canFindStructureDefinition", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(mappingEClass, Mapping.class, "Mapping", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMapping_MappedClass(), ecorePackage.getEString(), "mappedClass", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMapping_MappedPackage(), ecorePackage.getEString(), "mappedPackage", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMapping_Subset(), ecorePackage.getEString(), "subset", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMapping_Description(), ecorePackage.getEString(), "description", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMapping_MappingConditions(), this.getMappingCondition(), null, "mappingConditions", null, 0, -1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMapping_MultiWay(), this.getMultiWay(), "multiWay", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMapping_BreakPoint(), ecorePackage.getEBoolean(), "breakPoint", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(mappingEClass, ecorePackage.getEBoolean(), "mappedClassIsInClassModel", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(mappingConditionEClass, MappingCondition.class, "MappingCondition", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMappingCondition_LeftPath(), ecorePackage.getEString(), "leftPath", null, 0, 1, MappingCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappingCondition_LeftFunction(), ecorePackage.getEString(), "leftFunction", null, 0, 1, MappingCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappingCondition_Test(), this.getConditionTest(), "test", null, 0, 1, MappingCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappingCondition_Description(), ecorePackage.getEString(), "description", null, 0, 1, MappingCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMappingCondition_LeftPathConditions(), this.getMappingCondition(), null, "leftPathConditions", null, 0, -1, MappingCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(mappingConditionEClass, ecorePackage.getEBoolean(), "leftPathIsValid", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(mappingConditionEClass, ecorePackage.getEBoolean(), "leftPathGivesUniqueNode", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(mappingConditionEClass, ecorePackage.getEBoolean(), "leftFunctionIsValid", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(modelAssocFilterEClass, ModelAssocFilter.class, "ModelAssocFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModelAssocFilter_RoleName(), ecorePackage.getEString(), "roleName", null, 0, 1, ModelAssocFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModelAssocFilter_OtherClassName(), ecorePackage.getEString(), "otherClassName", null, 0, 1, ModelAssocFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModelAssocFilter_OtherPackageName(), ecorePackage.getEString(), "otherPackageName", null, 0, 1, ModelAssocFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModelAssocFilter_OtherSubset(), ecorePackage.getEString(), "otherSubset", null, 0, 1, ModelAssocFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(modelAssocFilterEClass, ecorePackage.getEBoolean(), "classHasRoleFromOtherClass", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(modelAssocFilterEClass, ecorePackage.getEBoolean(), "otherObjectMappingExists", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(modelFilterEClass, ModelFilter.class, "ModelFilter", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModelFilter_Description(), ecorePackage.getEString(), "description", null, 0, 1, ModelFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(modelFilterSetEClass, ModelFilterSet.class, "ModelFilterSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModelFilterSet_Description(), ecorePackage.getEString(), "description", null, 0, 1, ModelFilterSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getModelFilterSet_ModelFilters(), this.getModelFilter(), null, "modelFilters", null, 0, -1, ModelFilterSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(modelPropertyFilterEClass, ModelPropertyFilter.class, "ModelPropertyFilter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModelPropertyFilter_PropertyName(), ecorePackage.getEString(), "propertyName", null, 0, 1, ModelPropertyFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModelPropertyFilter_Value(), ecorePackage.getEString(), "value", null, 0, 1, ModelPropertyFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModelPropertyFilter_Test(), this.getConditionTest(), "test", null, 0, 1, ModelPropertyFilter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(modelPropertyFilterEClass, ecorePackage.getEBoolean(), "classHasProperty", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(namespaceEClass, Namespace.class, "Namespace", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNamespace_URL(), ecorePackage.getEString(), "uRL", null, 0, 1, Namespace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNamespace_Prefix(), ecorePackage.getEString(), "prefix", null, 0, 1, Namespace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(nodeDefEClass, NodeDef.class, "NodeDef", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNodeDef_Name(), ecorePackage.getEString(), "name", null, 0, 1, NodeDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNodeDef_Type(), ecorePackage.getEString(), "type", null, 0, 1, NodeDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNodeDef_Description(), ecorePackage.getEString(), "description", null, 0, 1, NodeDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNodeDef_MinMultiplicity(), this.getMinMult(), "minMultiplicity", null, 0, 1, NodeDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNodeDef_DefaultValue(), ecorePackage.getEString(), "defaultValue", null, 0, 1, NodeDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNodeDef_FixedValue(), ecorePackage.getEString(), "fixedValue", null, 0, 1, NodeDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNodeDef_NodeMappingSet(), this.getNodeMappingSet(), null, "nodeMappingSet", null, 0, 1, NodeDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNodeDef_Annotations(), this.getAnnotations(), null, "annotations", null, 0, 1, NodeDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(nodeDefEClass, ecorePackage.getEBoolean(), "notBothDefaultAndFixed", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(nodeDefEClass, ecorePackage.getEBoolean(), "hasCorrectDefaultOrFixedValue", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(nodeDefEClass, ecorePackage.getEBoolean(), "hasCorrectMinMultiplicity", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(nodeDefEClass, ecorePackage.getEBoolean(), "inStructureOfContainingElement", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(nodeMappingSetEClass, NodeMappingSet.class, "NodeMappingSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNodeMappingSet_ObjectMappings(), this.getObjMapping(), null, "objectMappings", null, 0, -1, NodeMappingSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNodeMappingSet_PropertyMappings(), this.getPropMapping(), null, "propertyMappings", null, 0, -1, NodeMappingSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNodeMappingSet_AssociationMappings(), this.getAssocMapping(), null, "associationMappings", null, 0, -1, NodeMappingSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(noteEClass, Note.class, "Note", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNote_Key(), ecorePackage.getEString(), "key", null, 0, 1, Note.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNote_Value(), ecorePackage.getEString(), "value", null, 0, 1, Note.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(objMappingEClass, ObjMapping.class, "ObjMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getObjMapping_RootPath(), ecorePackage.getEString(), "rootPath", null, 0, 1, ObjMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getObjMapping_MultiplyRepresented(), ecorePackage.getEBoolean(), "multiplyRepresented", null, 0, 1, ObjMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getObjMapping_ModelFilterSet(), this.getModelFilterSet(), null, "modelFilterSet", null, 0, 1, ObjMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getObjMapping_FixedPropertyValues(), this.getFixedPropertyValue(), null, "fixedPropertyValues", null, 0, -1, ObjMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(objMappingEClass, ecorePackage.getEBoolean(), "subsetIsUniqueWithinClass", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(objMappingEClass, ecorePackage.getEBoolean(), "rootPathIsConsistentWithNodePosition", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(parameterClassEClass, ParameterClass.class, "ParameterClass", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameterClass_ClassName(), ecorePackage.getEString(), "className", null, 0, 1, ParameterClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameterClass_PackageName(), ecorePackage.getEString(), "packageName", null, 0, 1, ParameterClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameterClass_ParameterIndex(), ecorePackage.getEInt(), "parameterIndex", null, 0, 1, ParameterClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(parameterClassEClass, ecorePackage.getEBoolean(), "classIsInClassModel", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(parameterClassEClass, ecorePackage.getEBoolean(), "objectMappingExists", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(parameterClassValueEClass, ParameterClassValue.class, "ParameterClassValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameterClassValue_MappedClass(), ecorePackage.getEString(), "mappedClass", null, 0, 1, ParameterClassValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameterClassValue_MappedPackage(), ecorePackage.getEString(), "mappedPackage", null, 0, 1, ParameterClassValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameterClassValue_Subset(), ecorePackage.getEString(), "subset", null, 0, 1, ParameterClassValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameterClassValue_ParameterIndex(), ecorePackage.getEInt(), "parameterIndex", null, 0, 1, ParameterClassValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(parameterClassValueEClass, ecorePackage.getEBoolean(), "mappedClassIsInClassModel", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(parameterClassValueEClass, ecorePackage.getEBoolean(), "mappingExistsForParameterClassValue", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(propMappingEClass, PropMapping.class, "PropMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPropMapping_MappedProperty(), ecorePackage.getEString(), "mappedProperty", null, 0, 1, PropMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPropMapping_PropertyType(), ecorePackage.getEString(), "propertyType", null, 0, 1, PropMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPropMapping_DefaultValue(), ecorePackage.getEString(), "defaultValue", null, 0, 1, PropMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPropMapping_ObjectToPropertyPath(), ecorePackage.getEString(), "objectToPropertyPath", null, 0, 1, PropMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPropMapping_LocalPropertyConversion(), this.getLocalPropertyConversion(), null, "localPropertyConversion", null, 0, 1, PropMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(propMappingEClass, ecorePackage.getEBoolean(), "classHasProperty", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(propMappingEClass, ecorePackage.getEBoolean(), "objectMappingExists", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(propMappingEClass, ecorePackage.getEBoolean(), "objectToPropertyPathIsValid", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(propMappingEClass, ecorePackage.getEBoolean(), "propertyIsUniqueFromObjectNode", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(propertyConversionEClass, PropertyConversion.class, "PropertyConversion", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPropertyConversion_Subset(), ecorePackage.getEString(), "subset", null, 0, 1, PropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPropertyConversion_ResultSlot(), ecorePackage.getEString(), "resultSlot", null, 0, 1, PropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPropertyConversion_Sense(), this.getConversionSense(), "sense", null, 0, 1, PropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPropertyConversion_ConversionImplementations(), this.getConversionImplementation(), null, "conversionImplementations", null, 0, -1, PropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPropertyConversion_ConversionArguments(), this.getConversionArgument(), null, "conversionArguments", null, 0, -1, PropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPropertyConversion_Description(), ecorePackage.getEString(), "description", null, 0, 1, PropertyConversion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(propertyConversionEClass, ecorePackage.getEBoolean(), "classHasResultProperty", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(propertyConversionEClass, ecorePackage.getEBoolean(), "hasImplementation", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getDiagnosticChain(), "diagnostics", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getMap());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(valueConditionEClass, ValueCondition.class, "ValueCondition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getValueCondition_RightValue(), ecorePackage.getEString(), "rightValue", null, 0, 1, ValueCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(valuePairEClass, ValuePair.class, "ValuePair", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getValuePair_Description(), ecorePackage.getEString(), "description", null, 0, 1, ValuePair.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getValuePair_StructureValue(), ecorePackage.getEString(), "structureValue", null, 0, 1, ValuePair.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getValuePair_ModelValue(), ecorePackage.getEString(), "modelValue", null, 0, 1, ValuePair.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getValuePair_PreferredIn(), ecorePackage.getEBoolean(), "preferredIn", null, 0, 1, ValuePair.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getValuePair_PreferredOut(), ecorePackage.getEBoolean(), "preferredOut", null, 0, 1, ValuePair.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(xsltConversionImplementationEClass, XSLTConversionImplementation.class, "XSLTConversionImplementation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getXSLTConversionImplementation_TemplateName(), ecorePackage.getEString(), "templateName", null, 0, 1, XSLTConversionImplementation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXSLTConversionImplementation_TemplateFileURI(), ecorePackage.getEString(), "templateFileURI", null, 0, 1, XSLTConversionImplementation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(conditionTestEEnum, ConditionTest.class, "ConditionTest");
		addEEnumLiteral(conditionTestEEnum, ConditionTest.EQUALS);
		addEEnumLiteral(conditionTestEEnum, ConditionTest.GT);
		addEEnumLiteral(conditionTestEEnum, ConditionTest.LT);
		addEEnumLiteral(conditionTestEEnum, ConditionTest.CONTAINS);
		addEEnumLiteral(conditionTestEEnum, ConditionTest.CONTAINEDBY);
		addEEnumLiteral(conditionTestEEnum, ConditionTest.CONTAINSASWORD);
		addEEnumLiteral(conditionTestEEnum, ConditionTest.CONTAINEDBYASWORD);
		addEEnumLiteral(conditionTestEEnum, ConditionTest.NOT_EQUALS);

		initEEnum(conversionSenseEEnum, ConversionSense.class, "ConversionSense");
		addEEnumLiteral(conversionSenseEEnum, ConversionSense.IN);
		addEEnumLiteral(conversionSenseEEnum, ConversionSense.OUT);

		initEEnum(maxMultEEnum, MaxMult.class, "MaxMult");
		addEEnumLiteral(maxMultEEnum, MaxMult.ONE);
		addEEnumLiteral(maxMultEEnum, MaxMult.UNBOUNDED);

		initEEnum(minMultEEnum, MinMult.class, "MinMult");
		addEEnumLiteral(minMultEEnum, MinMult.ZERO);
		addEEnumLiteral(minMultEEnum, MinMult.ONE);

		initEEnum(multiWayEEnum, MultiWay.class, "MultiWay");
		addEEnumLiteral(multiWayEEnum, MultiWay.NONE);
		addEEnumLiteral(multiWayEEnum, MultiWay.REDUNDANT);
		addEEnumLiteral(multiWayEEnum, MultiWay.CHOICE);

		initEEnum(structureTypeEEnum, StructureType.class, "StructureType");
		addEEnumLiteral(structureTypeEEnum, StructureType.XSD);
		addEEnumLiteral(structureTypeEEnum, StructureType.RDBMS);
		addEEnumLiteral(structureTypeEEnum, StructureType.V2);

		// Initialize data types
		initEDataType(diagnosticChainEDataType, DiagnosticChain.class, "DiagnosticChain", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(mapEDataType, Map.class, "Map", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //MapperPackageImpl
