/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import com.openMap1.mapper.MappingCondition;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Value Condition</b></em>'.
 * 
 * A Value Condition is a mapping condition 
 * (so the mapping only represents what it purports to represent if the condition is true)
 * which depends on nodes in the structure.
 * 
 *  It requires the value on some node, reached by an XPath from the mapped node,
 *  to be compared with some constant value.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ValueCondition#getRightValue <em>Right Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getValueCondition()
 * @model
 * @generated
 */
public interface ValueCondition extends MappingCondition {

	/**
	 * Returns the value of the '<em><b>Right Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * This is the constant value, on the right-hand side of the condition,
	 * against which the value from the structure node is compared.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Right Value</em>' attribute.
	 * @see #setRightValue(String)
	 * @see com.openMap1.mapper.MapperPackage#getValueCondition_RightValue()
	 * @model
	 * @generated
	 */
	String getRightValue();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ValueCondition#getRightValue <em>Right Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Right Value</em>' attribute.
	 * @see #getRightValue()
	 * @generated
	 */
	void setRightValue(String value);

} // ValueCondition
