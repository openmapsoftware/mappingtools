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
import com.openMap1.mapper.ConversionArgument;
import com.openMap1.mapper.ConversionImplementation;
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
import com.openMap1.mapper.ModelAssocFilter;
import com.openMap1.mapper.ModelFilter;
import com.openMap1.mapper.ModelFilterSet;
import com.openMap1.mapper.ModelPropertyFilter;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.NodeDef;
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

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.openMap1.mapper.MapperPackage
 * @generated
 */
public class MapperSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MapperPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MapperSwitch() {
		if (modelPackage == null) {
			modelPackage = MapperPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case MapperPackage.ANNOTATIONS: {
				Annotations annotations = (Annotations)theEObject;
				T result = caseAnnotations(annotations);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.ASSOC_END_MAPPING: {
				AssocEndMapping assocEndMapping = (AssocEndMapping)theEObject;
				T result = caseAssocEndMapping(assocEndMapping);
				if (result == null) result = caseMapping(assocEndMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.ASSOC_MAPPING: {
				AssocMapping assocMapping = (AssocMapping)theEObject;
				T result = caseAssocMapping(assocMapping);
				if (result == null) result = caseMapping(assocMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.ATTRIBUTE_DEF: {
				AttributeDef attributeDef = (AttributeDef)theEObject;
				T result = caseAttributeDef(attributeDef);
				if (result == null) result = caseNodeDef(attributeDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.CLASS_DETAILS: {
				ClassDetails classDetails = (ClassDetails)theEObject;
				T result = caseClassDetails(classDetails);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.CONVERSION_ARGUMENT: {
				ConversionArgument conversionArgument = (ConversionArgument)theEObject;
				T result = caseConversionArgument(conversionArgument);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.CONVERSION_IMPLEMENTATION: {
				ConversionImplementation conversionImplementation = (ConversionImplementation)theEObject;
				T result = caseConversionImplementation(conversionImplementation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.CROSS_CONDITION: {
				CrossCondition crossCondition = (CrossCondition)theEObject;
				T result = caseCrossCondition(crossCondition);
				if (result == null) result = caseMappingCondition(crossCondition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.ELEMENT_DEF: {
				ElementDef elementDef = (ElementDef)theEObject;
				T result = caseElementDef(elementDef);
				if (result == null) result = caseNodeDef(elementDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.FIXED_PROPERTY_VALUE: {
				FixedPropertyValue fixedPropertyValue = (FixedPropertyValue)theEObject;
				T result = caseFixedPropertyValue(fixedPropertyValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS: {
				GlobalMappingParameters globalMappingParameters = (GlobalMappingParameters)theEObject;
				T result = caseGlobalMappingParameters(globalMappingParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.IMPORT_MAPPING_SET: {
				ImportMappingSet importMappingSet = (ImportMappingSet)theEObject;
				T result = caseImportMappingSet(importMappingSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.JAVA_CONVERSION_IMPLEMENTATION: {
				JavaConversionImplementation javaConversionImplementation = (JavaConversionImplementation)theEObject;
				T result = caseJavaConversionImplementation(javaConversionImplementation);
				if (result == null) result = caseConversionImplementation(javaConversionImplementation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.LOCAL_PROPERTY_CONVERSION: {
				LocalPropertyConversion localPropertyConversion = (LocalPropertyConversion)theEObject;
				T result = caseLocalPropertyConversion(localPropertyConversion);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.MAPPED_STRUCTURE: {
				MappedStructure mappedStructure = (MappedStructure)theEObject;
				T result = caseMappedStructure(mappedStructure);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.MAPPING: {
				Mapping mapping = (Mapping)theEObject;
				T result = caseMapping(mapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.MAPPING_CONDITION: {
				MappingCondition mappingCondition = (MappingCondition)theEObject;
				T result = caseMappingCondition(mappingCondition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.MODEL_ASSOC_FILTER: {
				ModelAssocFilter modelAssocFilter = (ModelAssocFilter)theEObject;
				T result = caseModelAssocFilter(modelAssocFilter);
				if (result == null) result = caseModelFilter(modelAssocFilter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.MODEL_FILTER: {
				ModelFilter modelFilter = (ModelFilter)theEObject;
				T result = caseModelFilter(modelFilter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.MODEL_FILTER_SET: {
				ModelFilterSet modelFilterSet = (ModelFilterSet)theEObject;
				T result = caseModelFilterSet(modelFilterSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.MODEL_PROPERTY_FILTER: {
				ModelPropertyFilter modelPropertyFilter = (ModelPropertyFilter)theEObject;
				T result = caseModelPropertyFilter(modelPropertyFilter);
				if (result == null) result = caseModelFilter(modelPropertyFilter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.NAMESPACE: {
				Namespace namespace = (Namespace)theEObject;
				T result = caseNamespace(namespace);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.NODE_DEF: {
				NodeDef nodeDef = (NodeDef)theEObject;
				T result = caseNodeDef(nodeDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.NODE_MAPPING_SET: {
				NodeMappingSet nodeMappingSet = (NodeMappingSet)theEObject;
				T result = caseNodeMappingSet(nodeMappingSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.NOTE: {
				Note note = (Note)theEObject;
				T result = caseNote(note);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.OBJ_MAPPING: {
				ObjMapping objMapping = (ObjMapping)theEObject;
				T result = caseObjMapping(objMapping);
				if (result == null) result = caseMapping(objMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.PARAMETER_CLASS: {
				ParameterClass parameterClass = (ParameterClass)theEObject;
				T result = caseParameterClass(parameterClass);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.PARAMETER_CLASS_VALUE: {
				ParameterClassValue parameterClassValue = (ParameterClassValue)theEObject;
				T result = caseParameterClassValue(parameterClassValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.PROP_MAPPING: {
				PropMapping propMapping = (PropMapping)theEObject;
				T result = casePropMapping(propMapping);
				if (result == null) result = caseMapping(propMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.PROPERTY_CONVERSION: {
				PropertyConversion propertyConversion = (PropertyConversion)theEObject;
				T result = casePropertyConversion(propertyConversion);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.VALUE_CONDITION: {
				ValueCondition valueCondition = (ValueCondition)theEObject;
				T result = caseValueCondition(valueCondition);
				if (result == null) result = caseMappingCondition(valueCondition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.VALUE_PAIR: {
				ValuePair valuePair = (ValuePair)theEObject;
				T result = caseValuePair(valuePair);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION: {
				XSLTConversionImplementation xsltConversionImplementation = (XSLTConversionImplementation)theEObject;
				T result = caseXSLTConversionImplementation(xsltConversionImplementation);
				if (result == null) result = caseConversionImplementation(xsltConversionImplementation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Annotations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Annotations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAnnotations(Annotations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Assoc End Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Assoc End Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAssocEndMapping(AssocEndMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Assoc Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Assoc Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAssocMapping(AssocMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attribute Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attribute Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttributeDef(AttributeDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Class Details</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Class Details</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseClassDetails(ClassDetails object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Conversion Argument</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Conversion Argument</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConversionArgument(ConversionArgument object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Conversion Implementation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Conversion Implementation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConversionImplementation(ConversionImplementation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Cross Condition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Cross Condition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCrossCondition(CrossCondition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Element Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Element Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseElementDef(ElementDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Fixed Property Value</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Fixed Property Value</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFixedPropertyValue(FixedPropertyValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Global Mapping Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Global Mapping Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGlobalMappingParameters(GlobalMappingParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Import Mapping Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Import Mapping Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseImportMappingSet(ImportMappingSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Java Conversion Implementation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Java Conversion Implementation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseJavaConversionImplementation(JavaConversionImplementation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Property Conversion</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Property Conversion</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLocalPropertyConversion(LocalPropertyConversion object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mapped Structure</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mapped Structure</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMappedStructure(MappedStructure object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMapping(Mapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mapping Condition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mapping Condition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMappingCondition(MappingCondition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Assoc Filter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Assoc Filter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseModelAssocFilter(ModelAssocFilter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Filter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Filter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseModelFilter(ModelFilter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Filter Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Filter Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseModelFilterSet(ModelFilterSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Property Filter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Property Filter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseModelPropertyFilter(ModelPropertyFilter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Namespace</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Namespace</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamespace(Namespace object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Node Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Node Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNodeDef(NodeDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Node Mapping Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Node Mapping Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNodeMappingSet(NodeMappingSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Note</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Note</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNote(Note object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Obj Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Obj Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseObjMapping(ObjMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Class</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Class</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterClass(ParameterClass object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Class Value</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Class Value</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterClassValue(ParameterClassValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Prop Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Prop Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePropMapping(PropMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Property Conversion</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Property Conversion</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePropertyConversion(PropertyConversion object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Value Condition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Value Condition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseValueCondition(ValueCondition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>XSLT Conversion Implementation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>XSLT Conversion Implementation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseXSLTConversionImplementation(XSLTConversionImplementation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Value Pair</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Value Pair</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseValuePair(ValuePair object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //MapperSwitch
