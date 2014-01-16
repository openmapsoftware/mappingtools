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
import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.ConditionTest;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.Xpth;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapping Condition</b></em>'.
 * 
 * Abstract superclass of the two different types of mapping  condition 
 * - ValueCondition and CrossCondition
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.MappingCondition#getLeftPath <em>Left Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappingCondition#getLeftFunction <em>Left Function</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappingCondition#getTest <em>Test</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappingCondition#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappingCondition#getLeftPathConditions <em>Left Path Conditions</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getMappingCondition()
 * @model abstract="true"
 * @generated
 */
public interface MappingCondition extends EObject {
	/**
	 * Returns the value of the '<em><b>Left Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * XPath from a node in the structure 
	 * (which holds an object, property or association mapping) to 
	 * some other uniquely defined node which provides a value 
	 * for the left-hand-side of the condition being tested.
	 *
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Left Path</em>' attribute.
	 * @see #setLeftPath(String)
	 * @see com.openMap1.mapper.MapperPackage#getMappingCondition_LeftPath()
	 * @model
	 * @generated
	 */
	String getLeftPath();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappingCondition#getLeftPath <em>Left Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Left Path</em>' attribute.
	 * @see #getLeftPath()
	 * @generated
	 */
	void setLeftPath(String value);

	/**
	 * Returns the value of the '<em><b>Left Function</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Left Function</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Left Function</em>' attribute.
	 * @see #setLeftFunction(String)
	 * @see com.openMap1.mapper.MapperPackage#getMappingCondition_LeftFunction()
	 * @model
	 * @generated
	 */
	String getLeftFunction();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappingCondition#getLeftFunction <em>Left Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Left Function</em>' attribute.
	 * @see #getLeftFunction()
	 * @generated
	 */
	void setLeftFunction(String value);

	/**
	 * Returns the value of the '<em><b>Test</b></em>' attribute.
	 * The literals are from the enumeration {@link com.openMap1.mapper.ConditionTest}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The test to be applied when comparing left and right hand sides of thhe condition
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Test</em>' attribute.
	 * @see com.openMap1.mapper.ConditionTest
	 * @see #setTest(ConditionTest)
	 * @see com.openMap1.mapper.MapperPackage#getMappingCondition_Test()
	 * @model
	 * @generated
	 */
	ConditionTest getTest();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappingCondition#getTest <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Test</em>' attribute.
	 * @see com.openMap1.mapper.ConditionTest
	 * @see #getTest()
	 * @generated
	 */
	void setTest(ConditionTest value);

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
	 * @see com.openMap1.mapper.MapperPackage#getMappingCondition_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappingCondition#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Left Path Conditions</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.MappingCondition}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Left Path Conditions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Left Path Conditions</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getMappingCondition_LeftPathConditions()
	 * @model containment="true"
	 * @generated
	 */
	EList<MappingCondition> getLeftPathConditions();

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the XPath from the mapped node to the node that defines 
	 * the left-hand side of the condition test is a valid XPath 
	 * for this structure.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean leftPathIsValid(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the XPath from the mapped node to the node that defines 
	 * the left-hand side of the condition test can only lead to one node,
	 * giving a unique value to be tested.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean leftPathGivesUniqueNode(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean leftFunctionIsValid(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * return the XPath from the mapped node to the node 
	 * defining the LHS of the condition
	 */
	public Xpth getLHSPath() throws MapperException;
	
	/**
	 * @return a MappingConditionImpl static int constant
	 * OBJECT, PROPERTY, ASSOCIATION, LHS, or RHS
	 * which defines waht this is a condition on
	 */
	public int conditionOn();
	
	/**
	 * details of this mapping condition, to be written out in the details column of the Mappings view
	 */
	public String getDetails();

} // MappingCondition
