/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.ConversionImplementation;
import com.openMap1.mapper.ValuePair;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Local Property Conversion</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.LocalPropertyConversion#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.LocalPropertyConversion#getInConversionImplementations <em>In Conversion Implementations</em>}</li>
 *   <li>{@link com.openMap1.mapper.LocalPropertyConversion#getOutConversionImplementations <em>Out Conversion Implementations</em>}</li>
 *   <li>{@link com.openMap1.mapper.LocalPropertyConversion#getValuePairs <em>Value Pairs</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getLocalPropertyConversion()
 * @model
 * @generated
 */
public interface LocalPropertyConversion extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see com.openMap1.mapper.MapperPackage#getLocalPropertyConversion_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.LocalPropertyConversion#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>In Conversion Implementations</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ConversionImplementation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>In Conversion Implementations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>In Conversion Implementations</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getLocalPropertyConversion_InConversionImplementations()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConversionImplementation> getInConversionImplementations();

	/**
	 * Returns the value of the '<em><b>Out Conversion Implementations</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ConversionImplementation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Out Conversion Implementations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Out Conversion Implementations</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getLocalPropertyConversion_OutConversionImplementations()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConversionImplementation> getOutConversionImplementations();

	/**
	 * Returns the value of the '<em><b>Value Pairs</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ValuePair}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value Pairs</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value Pairs</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getLocalPropertyConversion_ValuePairs()
	 * @model containment="true"
	 * @generated
	 */
	EList<ValuePair> getValuePairs();

} // LocalPropertyConversion
