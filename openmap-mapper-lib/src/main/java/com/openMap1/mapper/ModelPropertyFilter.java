/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;

import com.openMap1.mapper.ConditionTest;
import com.openMap1.mapper.ModelFilter;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Property Filter</b></em>'.
 * 
 * This model-based filter constrains which object in the class model 
 * are represented by the containing object mapping, using a comparison of
 * one of the properties of the object with a constant value.
 * e.g 'this node type represents cars with capacity > 3000 cc'
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ModelPropertyFilter#getPropertyName <em>Property Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.ModelPropertyFilter#getValue <em>Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.ModelPropertyFilter#getTest <em>Test</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getModelPropertyFilter()
 * @model
 * @generated
 */
public interface ModelPropertyFilter extends ModelFilter {
	/**
	 * Returns the value of the '<em><b>Property Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name of the property used in the comparison
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property Name</em>' attribute.
	 * @see #setPropertyName(String)
	 * @see com.openMap1.mapper.MapperPackage#getModelPropertyFilter_PropertyName()
	 * @model
	 * @generated
	 */
	String getPropertyName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ModelPropertyFilter#getPropertyName <em>Property Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Property Name</em>' attribute.
	 * @see #getPropertyName()
	 * @generated
	 */
	void setPropertyName(String value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The value the property is compared against
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see com.openMap1.mapper.MapperPackage#getModelPropertyFilter_Value()
	 * @model
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ModelPropertyFilter#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

	/**
	 * Returns the value of the '<em><b>Test</b></em>' attribute.
	 * The literals are from the enumeration {@link com.openMap1.mapper.ConditionTest}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The test used to cmmpare the property against the value, e.g '='
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Test</em>' attribute.
	 * @see com.openMap1.mapper.ConditionTest
	 * @see #setTest(ConditionTest)
	 * @see com.openMap1.mapper.MapperPackage#getModelPropertyFilter_Test()
	 * @model
	 * @generated
	 */
	ConditionTest getTest();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ModelPropertyFilter#getTest <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Test</em>' attribute.
	 * @see com.openMap1.mapper.ConditionTest
	 * @see #getTest()
	 * @generated
	 */
	void setTest(ConditionTest value);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the class has the property in the class model
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean classHasProperty(DiagnosticChain diagnostics, Map<?, ?> context);

} // ModelPropertyFilter
