/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import org.eclipse.emf.ecore.EFactory;

import com.openMap1.mapper.Annotations;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.ConversionArgument;
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
import com.openMap1.mapper.ModelAssocFilter;
import com.openMap1.mapper.ModelFilterSet;
import com.openMap1.mapper.ModelPropertyFilter;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.Note;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ParameterClass;
import com.openMap1.mapper.ParameterClassValue;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.PropertyConversion;
import com.openMap1.mapper.ValueCondition;
import com.openMap1.mapper.ValuePair;
import com.openMap1.mapper.XSLTConversionImplementation;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.openMap1.mapper.MapperPackage
 * @generated
 */
public interface MapperFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MapperFactory eINSTANCE = com.openMap1.mapper.impl.MapperFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Annotations</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Annotations</em>'.
	 * @generated
	 */
	Annotations createAnnotations();

	/**
	 * Returns a new object of class '<em>Assoc End Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Assoc End Mapping</em>'.
	 * @generated
	 */
	AssocEndMapping createAssocEndMapping();

	/**
	 * Returns a new object of class '<em>Assoc Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Assoc Mapping</em>'.
	 * @generated
	 */
	AssocMapping createAssocMapping();

	/**
	 * Returns a new object of class '<em>Attribute Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attribute Def</em>'.
	 * @generated
	 */
	AttributeDef createAttributeDef();

	/**
	 * Returns a new object of class '<em>Class Details</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Class Details</em>'.
	 * @generated
	 */
	ClassDetails createClassDetails();

	/**
	 * Returns a new object of class '<em>Conversion Argument</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Conversion Argument</em>'.
	 * @generated
	 */
	ConversionArgument createConversionArgument();

	/**
	 * Returns a new object of class '<em>Cross Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Cross Condition</em>'.
	 * @generated
	 */
	CrossCondition createCrossCondition();

	/**
	 * Returns a new object of class '<em>Element Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Element Def</em>'.
	 * @generated
	 */
	ElementDef createElementDef();

	/**
	 * Returns a new object of class '<em>Fixed Property Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Fixed Property Value</em>'.
	 * @generated
	 */
	FixedPropertyValue createFixedPropertyValue();

	/**
	 * Returns a new object of class '<em>Global Mapping Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Global Mapping Parameters</em>'.
	 * @generated
	 */
	GlobalMappingParameters createGlobalMappingParameters();

	/**
	 * Returns a new object of class '<em>Import Mapping Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Import Mapping Set</em>'.
	 * @generated
	 */
	ImportMappingSet createImportMappingSet();

	/**
	 * Returns a new object of class '<em>Java Conversion Implementation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Java Conversion Implementation</em>'.
	 * @generated
	 */
	JavaConversionImplementation createJavaConversionImplementation();

	/**
	 * Returns a new object of class '<em>Local Property Conversion</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Property Conversion</em>'.
	 * @generated
	 */
	LocalPropertyConversion createLocalPropertyConversion();

	/**
	 * Returns a new object of class '<em>Mapped Structure</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Mapped Structure</em>'.
	 * @generated
	 */
	MappedStructure createMappedStructure();

	/**
	 * Returns a new object of class '<em>Model Assoc Filter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Model Assoc Filter</em>'.
	 * @generated
	 */
	ModelAssocFilter createModelAssocFilter();

	/**
	 * Returns a new object of class '<em>Model Filter Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Model Filter Set</em>'.
	 * @generated
	 */
	ModelFilterSet createModelFilterSet();

	/**
	 * Returns a new object of class '<em>Model Property Filter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Model Property Filter</em>'.
	 * @generated
	 */
	ModelPropertyFilter createModelPropertyFilter();

	/**
	 * Returns a new object of class '<em>Namespace</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Namespace</em>'.
	 * @generated
	 */
	Namespace createNamespace();

	/**
	 * Returns a new object of class '<em>Node Mapping Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Node Mapping Set</em>'.
	 * @generated
	 */
	NodeMappingSet createNodeMappingSet();

	/**
	 * Returns a new object of class '<em>Note</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Note</em>'.
	 * @generated
	 */
	Note createNote();

	/**
	 * Returns a new object of class '<em>Obj Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Obj Mapping</em>'.
	 * @generated
	 */
	ObjMapping createObjMapping();

	/**
	 * Returns a new object of class '<em>Parameter Class</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter Class</em>'.
	 * @generated
	 */
	ParameterClass createParameterClass();

	/**
	 * Returns a new object of class '<em>Parameter Class Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter Class Value</em>'.
	 * @generated
	 */
	ParameterClassValue createParameterClassValue();

	/**
	 * Returns a new object of class '<em>Prop Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Prop Mapping</em>'.
	 * @generated
	 */
	PropMapping createPropMapping();

	/**
	 * Returns a new object of class '<em>Property Conversion</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Property Conversion</em>'.
	 * @generated
	 */
	PropertyConversion createPropertyConversion();

	/**
	 * Returns a new object of class '<em>Value Condition</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Value Condition</em>'.
	 * @generated
	 */
	ValueCondition createValueCondition();

	/**
	 * Returns a new object of class '<em>Value Pair</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Value Pair</em>'.
	 * @generated
	 */
	ValuePair createValuePair();

	/**
	 * Returns a new object of class '<em>XSLT Conversion Implementation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>XSLT Conversion Implementation</em>'.
	 * @generated
	 */
	XSLTConversionImplementation createXSLTConversionImplementation();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	MapperPackage getMapperPackage();

} //MapperFactory
