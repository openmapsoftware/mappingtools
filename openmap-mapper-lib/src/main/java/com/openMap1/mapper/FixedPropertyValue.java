/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Fixed Property Value</b></em>'.
 * 
 * This defines the value of some property which has the same fixed value for every
 * object represented by the containing ObjMapping
 * 
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.FixedPropertyValue#getMappedProperty <em>Mapped Property</em>}</li>
 *   <li>{@link com.openMap1.mapper.FixedPropertyValue#getFixedValue <em>Fixed Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.FixedPropertyValue#getValueType <em>Value Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getFixedPropertyValue()
 * @model
 * @generated
 */
public interface FixedPropertyValue extends EObject {
	/**
	 * Returns the value of the '<em><b>Mapped Property</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name of the property
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped Property</em>' attribute.
	 * @see #setMappedProperty(String)
	 * @see com.openMap1.mapper.MapperPackage#getFixedPropertyValue_MappedProperty()
	 * @model
	 * @generated
	 */
	String getMappedProperty();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.FixedPropertyValue#getMappedProperty <em>Mapped Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped Property</em>' attribute.
	 * @see #getMappedProperty()
	 * @generated
	 */
	void setMappedProperty(String value);

	/**
	 * Returns the value of the '<em><b>Fixed Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The fixed value of the property
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fixed Value</em>' attribute.
	 * @see #setFixedValue(String)
	 * @see com.openMap1.mapper.MapperPackage#getFixedPropertyValue_FixedValue()
	 * @model
	 * @generated
	 */
	String getFixedValue();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.FixedPropertyValue#getFixedValue <em>Fixed Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fixed Value</em>' attribute.
	 * @see #getFixedValue()
	 * @generated
	 */
	void setFixedValue(String value);

	/**
	 * Returns the value of the '<em><b>Value Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The type of the property which has the fixed value - 
	 * not clear why it is needed here
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value Type</em>' attribute.
	 * @see #setValueType(String)
	 * @see com.openMap1.mapper.MapperPackage#getFixedPropertyValue_ValueType()
	 * @model
	 * @generated
	 */
	String getValueType();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.FixedPropertyValue#getValueType <em>Value Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Type</em>' attribute.
	 * @see #getValueType()
	 * @generated
	 */
	void setValueType(String value);

	/**
	 * <!-- begin-user-doc -->
	 * check that the class of the object mapping (if it exists) has the property.
	 * Can fixed properties be pseudo-properties, i.e inputs to property conversions? Yes.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean classHasProperty(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * @return text description of the fixed value, for the Filter column in Mappings view
	 */
	public String getDetails();

} // FixedPropertyValue
