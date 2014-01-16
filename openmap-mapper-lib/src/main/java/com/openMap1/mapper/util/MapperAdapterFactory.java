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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.openMap1.mapper.MapperPackage
 * @generated
 */
public class MapperAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MapperPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MapperAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = MapperPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MapperSwitch<Adapter> modelSwitch =
		new MapperSwitch<Adapter>() {
			@Override
			public Adapter caseAnnotations(Annotations object) {
				return createAnnotationsAdapter();
			}
			@Override
			public Adapter caseAssocEndMapping(AssocEndMapping object) {
				return createAssocEndMappingAdapter();
			}
			@Override
			public Adapter caseAssocMapping(AssocMapping object) {
				return createAssocMappingAdapter();
			}
			@Override
			public Adapter caseAttributeDef(AttributeDef object) {
				return createAttributeDefAdapter();
			}
			@Override
			public Adapter caseClassDetails(ClassDetails object) {
				return createClassDetailsAdapter();
			}
			@Override
			public Adapter caseConversionArgument(ConversionArgument object) {
				return createConversionArgumentAdapter();
			}
			@Override
			public Adapter caseConversionImplementation(ConversionImplementation object) {
				return createConversionImplementationAdapter();
			}
			@Override
			public Adapter caseCrossCondition(CrossCondition object) {
				return createCrossConditionAdapter();
			}
			@Override
			public Adapter caseElementDef(ElementDef object) {
				return createElementDefAdapter();
			}
			@Override
			public Adapter caseFixedPropertyValue(FixedPropertyValue object) {
				return createFixedPropertyValueAdapter();
			}
			@Override
			public Adapter caseGlobalMappingParameters(GlobalMappingParameters object) {
				return createGlobalMappingParametersAdapter();
			}
			@Override
			public Adapter caseImportMappingSet(ImportMappingSet object) {
				return createImportMappingSetAdapter();
			}
			@Override
			public Adapter caseJavaConversionImplementation(JavaConversionImplementation object) {
				return createJavaConversionImplementationAdapter();
			}
			@Override
			public Adapter caseLocalPropertyConversion(LocalPropertyConversion object) {
				return createLocalPropertyConversionAdapter();
			}
			@Override
			public Adapter caseMappedStructure(MappedStructure object) {
				return createMappedStructureAdapter();
			}
			@Override
			public Adapter caseMapping(Mapping object) {
				return createMappingAdapter();
			}
			@Override
			public Adapter caseMappingCondition(MappingCondition object) {
				return createMappingConditionAdapter();
			}
			@Override
			public Adapter caseModelAssocFilter(ModelAssocFilter object) {
				return createModelAssocFilterAdapter();
			}
			@Override
			public Adapter caseModelFilter(ModelFilter object) {
				return createModelFilterAdapter();
			}
			@Override
			public Adapter caseModelFilterSet(ModelFilterSet object) {
				return createModelFilterSetAdapter();
			}
			@Override
			public Adapter caseModelPropertyFilter(ModelPropertyFilter object) {
				return createModelPropertyFilterAdapter();
			}
			@Override
			public Adapter caseNamespace(Namespace object) {
				return createNamespaceAdapter();
			}
			@Override
			public Adapter caseNodeDef(NodeDef object) {
				return createNodeDefAdapter();
			}
			@Override
			public Adapter caseNodeMappingSet(NodeMappingSet object) {
				return createNodeMappingSetAdapter();
			}
			@Override
			public Adapter caseNote(Note object) {
				return createNoteAdapter();
			}
			@Override
			public Adapter caseObjMapping(ObjMapping object) {
				return createObjMappingAdapter();
			}
			@Override
			public Adapter caseParameterClass(ParameterClass object) {
				return createParameterClassAdapter();
			}
			@Override
			public Adapter caseParameterClassValue(ParameterClassValue object) {
				return createParameterClassValueAdapter();
			}
			@Override
			public Adapter casePropMapping(PropMapping object) {
				return createPropMappingAdapter();
			}
			@Override
			public Adapter casePropertyConversion(PropertyConversion object) {
				return createPropertyConversionAdapter();
			}
			@Override
			public Adapter caseValueCondition(ValueCondition object) {
				return createValueConditionAdapter();
			}
			@Override
			public Adapter caseValuePair(ValuePair object) {
				return createValuePairAdapter();
			}
			@Override
			public Adapter caseXSLTConversionImplementation(XSLTConversionImplementation object) {
				return createXSLTConversionImplementationAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.Annotations <em>Annotations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.Annotations
	 * @generated
	 */
	public Adapter createAnnotationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.AssocEndMapping <em>Assoc End Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.AssocEndMapping
	 * @generated
	 */
	public Adapter createAssocEndMappingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.AssocMapping <em>Assoc Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.AssocMapping
	 * @generated
	 */
	public Adapter createAssocMappingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.AttributeDef <em>Attribute Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.AttributeDef
	 * @generated
	 */
	public Adapter createAttributeDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ClassDetails <em>Class Details</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ClassDetails
	 * @generated
	 */
	public Adapter createClassDetailsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ConversionArgument <em>Conversion Argument</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ConversionArgument
	 * @generated
	 */
	public Adapter createConversionArgumentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ConversionImplementation <em>Conversion Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ConversionImplementation
	 * @generated
	 */
	public Adapter createConversionImplementationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.CrossCondition <em>Cross Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.CrossCondition
	 * @generated
	 */
	public Adapter createCrossConditionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ElementDef <em>Element Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ElementDef
	 * @generated
	 */
	public Adapter createElementDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.FixedPropertyValue <em>Fixed Property Value</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.FixedPropertyValue
	 * @generated
	 */
	public Adapter createFixedPropertyValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.GlobalMappingParameters <em>Global Mapping Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.GlobalMappingParameters
	 * @generated
	 */
	public Adapter createGlobalMappingParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ImportMappingSet <em>Import Mapping Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ImportMappingSet
	 * @generated
	 */
	public Adapter createImportMappingSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.JavaConversionImplementation <em>Java Conversion Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.JavaConversionImplementation
	 * @generated
	 */
	public Adapter createJavaConversionImplementationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.LocalPropertyConversion <em>Local Property Conversion</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.LocalPropertyConversion
	 * @generated
	 */
	public Adapter createLocalPropertyConversionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.MappedStructure <em>Mapped Structure</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.MappedStructure
	 * @generated
	 */
	public Adapter createMappedStructureAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.Mapping <em>Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.Mapping
	 * @generated
	 */
	public Adapter createMappingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.MappingCondition <em>Mapping Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.MappingCondition
	 * @generated
	 */
	public Adapter createMappingConditionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ModelAssocFilter <em>Model Assoc Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ModelAssocFilter
	 * @generated
	 */
	public Adapter createModelAssocFilterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ModelFilter <em>Model Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ModelFilter
	 * @generated
	 */
	public Adapter createModelFilterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ModelFilterSet <em>Model Filter Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ModelFilterSet
	 * @generated
	 */
	public Adapter createModelFilterSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ModelPropertyFilter <em>Model Property Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ModelPropertyFilter
	 * @generated
	 */
	public Adapter createModelPropertyFilterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.Namespace <em>Namespace</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.Namespace
	 * @generated
	 */
	public Adapter createNamespaceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.NodeDef <em>Node Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.NodeDef
	 * @generated
	 */
	public Adapter createNodeDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.NodeMappingSet <em>Node Mapping Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.NodeMappingSet
	 * @generated
	 */
	public Adapter createNodeMappingSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.Note <em>Note</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.Note
	 * @generated
	 */
	public Adapter createNoteAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ObjMapping <em>Obj Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ObjMapping
	 * @generated
	 */
	public Adapter createObjMappingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ParameterClass <em>Parameter Class</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ParameterClass
	 * @generated
	 */
	public Adapter createParameterClassAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ParameterClassValue <em>Parameter Class Value</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ParameterClassValue
	 * @generated
	 */
	public Adapter createParameterClassValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.PropMapping <em>Prop Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.PropMapping
	 * @generated
	 */
	public Adapter createPropMappingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.PropertyConversion <em>Property Conversion</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.PropertyConversion
	 * @generated
	 */
	public Adapter createPropertyConversionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ValueCondition <em>Value Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ValueCondition
	 * @generated
	 */
	public Adapter createValueConditionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.XSLTConversionImplementation <em>XSLT Conversion Implementation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.XSLTConversionImplementation
	 * @generated
	 */
	public Adapter createXSLTConversionImplementationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link com.openMap1.mapper.ValuePair <em>Value Pair</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see com.openMap1.mapper.ValuePair
	 * @generated
	 */
	public Adapter createValuePairAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //MapperAdapterFactory
