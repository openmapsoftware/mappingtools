/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Value Pair</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ValuePair#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.ValuePair#getStructureValue <em>Structure Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.ValuePair#getModelValue <em>Model Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.ValuePair#isPreferredIn <em>Preferred In</em>}</li>
 *   <li>{@link com.openMap1.mapper.ValuePair#isPreferredOut <em>Preferred Out</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getValuePair()
 * @model
 * @generated
 */
public interface ValuePair extends EObject {
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
	 * @see com.openMap1.mapper.MapperPackage#getValuePair_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ValuePair#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Structure Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Structure Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Structure Value</em>' attribute.
	 * @see #setStructureValue(String)
	 * @see com.openMap1.mapper.MapperPackage#getValuePair_StructureValue()
	 * @model
	 * @generated
	 */
	String getStructureValue();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ValuePair#getStructureValue <em>Structure Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Structure Value</em>' attribute.
	 * @see #getStructureValue()
	 * @generated
	 */
	void setStructureValue(String value);

	/**
	 * Returns the value of the '<em><b>Model Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Model Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Model Value</em>' attribute.
	 * @see #setModelValue(String)
	 * @see com.openMap1.mapper.MapperPackage#getValuePair_ModelValue()
	 * @model
	 * @generated
	 */
	String getModelValue();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ValuePair#getModelValue <em>Model Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Model Value</em>' attribute.
	 * @see #getModelValue()
	 * @generated
	 */
	void setModelValue(String value);

	/**
	 * Returns the value of the '<em><b>Preferred In</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Preferred In</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Preferred In</em>' attribute.
	 * @see #setPreferredIn(boolean)
	 * @see com.openMap1.mapper.MapperPackage#getValuePair_PreferredIn()
	 * @model
	 * @generated
	 */
	boolean isPreferredIn();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ValuePair#isPreferredIn <em>Preferred In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Preferred In</em>' attribute.
	 * @see #isPreferredIn()
	 * @generated
	 */
	void setPreferredIn(boolean value);

	/**
	 * Returns the value of the '<em><b>Preferred Out</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Preferred Out</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Preferred Out</em>' attribute.
	 * @see #setPreferredOut(boolean)
	 * @see com.openMap1.mapper.MapperPackage#getValuePair_PreferredOut()
	 * @model
	 * @generated
	 */
	boolean isPreferredOut();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ValuePair#isPreferredOut <em>Preferred Out</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Preferred Out</em>' attribute.
	 * @see #isPreferredOut()
	 * @generated
	 */
	void setPreferredOut(boolean value);

} // ValuePair
