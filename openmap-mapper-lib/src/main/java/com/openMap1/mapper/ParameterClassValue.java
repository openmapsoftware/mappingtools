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

import com.openMap1.mapper.core.ClassSet;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter Class Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ParameterClassValue#getMappedClass <em>Mapped Class</em>}</li>
 *   <li>{@link com.openMap1.mapper.ParameterClassValue#getMappedPackage <em>Mapped Package</em>}</li>
 *   <li>{@link com.openMap1.mapper.ParameterClassValue#getSubset <em>Subset</em>}</li>
 *   <li>{@link com.openMap1.mapper.ParameterClassValue#getParameterIndex <em>Parameter Index</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getParameterClassValue()
 * @model
 * @generated
 */
public interface ParameterClassValue extends EObject {
	/**
	 * Returns the value of the '<em><b>Mapped Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mapped Class</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped Class</em>' attribute.
	 * @see #setMappedClass(String)
	 * @see com.openMap1.mapper.MapperPackage#getParameterClassValue_MappedClass()
	 * @model
	 * @generated
	 */
	String getMappedClass();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ParameterClassValue#getMappedClass <em>Mapped Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped Class</em>' attribute.
	 * @see #getMappedClass()
	 * @generated
	 */
	void setMappedClass(String value);

	/**
	 * Returns the value of the '<em><b>Mapped Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mapped Package</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped Package</em>' attribute.
	 * @see #setMappedPackage(String)
	 * @see com.openMap1.mapper.MapperPackage#getParameterClassValue_MappedPackage()
	 * @model
	 * @generated
	 */
	String getMappedPackage();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ParameterClassValue#getMappedPackage <em>Mapped Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped Package</em>' attribute.
	 * @see #getMappedPackage()
	 * @generated
	 */
	void setMappedPackage(String value);

	/**
	 * Returns the value of the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Subset</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subset</em>' attribute.
	 * @see #setSubset(String)
	 * @see com.openMap1.mapper.MapperPackage#getParameterClassValue_Subset()
	 * @model
	 * @generated
	 */
	String getSubset();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ParameterClassValue#getSubset <em>Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subset</em>' attribute.
	 * @see #getSubset()
	 * @generated
	 */
	void setSubset(String value);

	/**
	 * Returns the value of the '<em><b>Parameter Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Index</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Index</em>' attribute.
	 * @see #setParameterIndex(int)
	 * @see com.openMap1.mapper.MapperPackage#getParameterClassValue_ParameterIndex()
	 * @model
	 * @generated
	 */
	int getParameterIndex();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ParameterClassValue#getParameterIndex <em>Parameter Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameter Index</em>' attribute.
	 * @see #getParameterIndex()
	 * @generated
	 */
	void setParameterIndex(int value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean mappedClassIsInClassModel(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean mappingExistsForParameterClassValue(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * the ClassSet of the class and subset
	 * @return
	 */
	public ClassSet getClassSet();

	/**
	 * @return class name preceded by the package name and '.', if it is nonempty
	 */
	public String getQualifiedClassName();

} // ParameterClassValue
