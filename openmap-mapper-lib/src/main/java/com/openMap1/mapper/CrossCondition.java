/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;

import org.eclipse.emf.common.util.EList;

import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.Xpth;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cross Condition</b></em>'.
 * 
 * A cross condition relates the values on two different nodes of the structure. 
 * The right hand value is taken from a node related by an XPath to an object mapping, 
 * and the left hand mapping is takne from some other tpye of mapping (property, association, 
 * or association end)
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.CrossCondition#getRightPath <em>Right Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.CrossCondition#getRightFunction <em>Right Function</em>}</li>
 *   <li>{@link com.openMap1.mapper.CrossCondition#getRightPathConditions <em>Right Path Conditions</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getCrossCondition()
 * @model
 * @generated
 */
public interface CrossCondition extends MappingCondition {

	/**
	 * Returns the value of the '<em><b>Right Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * XPath from an object mapping (of the same class and subset as the 
	 * mapping which is subject to the condition) to some node
	 * which supplies the value for the right-hand side of the condition
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Right Path</em>' attribute.
	 * @see #setRightPath(String)
	 * @see com.openMap1.mapper.MapperPackage#getCrossCondition_RightPath()
	 * @model
	 * @generated
	 */
	String getRightPath();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.CrossCondition#getRightPath <em>Right Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Right Path</em>' attribute.
	 * @see #getRightPath()
	 * @generated
	 */
	void setRightPath(String value);

	/**
	 * Returns the value of the '<em><b>Right Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Right Function</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Right Function</em>' attribute.
	 * @see #setRightFunction(String)
	 * @see com.openMap1.mapper.MapperPackage#getCrossCondition_RightFunction()
	 * @model
	 * @generated
	 */
	String getRightFunction();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.CrossCondition#getRightFunction <em>Right Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Right Function</em>' attribute.
	 * @see #getRightFunction()
	 * @generated
	 */
	void setRightFunction(String value);

	/**
	 * Returns the value of the '<em><b>Right Path Conditions</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.MappingCondition}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Right Path Conditions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Right Path Conditions</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getCrossCondition_RightPathConditions()
	 * @model containment="true"
	 * @generated
	 */
	EList<MappingCondition> getRightPathConditions();

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the XPath from the object mapping node to the node that defines 
	 * the right-hand side of the condition test is a valid XPath 
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean rightPathIsValid(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the XPath from the object mapping node to the node that defines 
	 * the right-hand side of the condition test can only lead to one node,
	 * giving a unique value to be tested.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean rightPathGivesUniqueNode(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean rightFunctionIsValid(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * return the XPath from the mapped node to the node 
	 * defining the LHS of the condition
	 */
	public Xpth getRHSPath() throws MapperException;

} // CrossCondition
