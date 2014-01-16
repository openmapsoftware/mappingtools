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

import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.ParameterClassValue;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Import Mapping Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ImportMappingSet#getMappingSetURI <em>Mapping Set URI</em>}</li>
 *   <li>{@link com.openMap1.mapper.ImportMappingSet#getParameterClassValues <em>Parameter Class Values</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getImportMappingSet()
 * @model
 * @generated
 */
public interface ImportMappingSet extends EObject {
	/**
	 * Returns the value of the '<em><b>Mapping Set URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mapping Set URI</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapping Set URI</em>' attribute.
	 * @see #setMappingSetURI(String)
	 * @see com.openMap1.mapper.MapperPackage#getImportMappingSet_MappingSetURI()
	 * @model
	 * @generated
	 */
	String getMappingSetURI();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ImportMappingSet#getMappingSetURI <em>Mapping Set URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapping Set URI</em>' attribute.
	 * @see #getMappingSetURI()
	 * @generated
	 */
	void setMappingSetURI(String value);

	/**
	 * Returns the value of the '<em><b>Parameter Class Values</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ParameterClassValue}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Class Values</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Class Values</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getImportMappingSet_ParameterClassValues()
	 * @model containment="true"
	 * @generated
	 */
	EList<ParameterClassValue> getParameterClassValues();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean canFindMappingSet(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean mappingSetHasSameClassModel(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean mappingSetParametersMatch(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean mappingSetStructureMatches(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * 
	 * @return the root MappedStructure node of the imported mapping set
	 */
	public MappedStructure getImportedMappingSet();
	
	/**
	 * 
	 * @return the Element of this mapping set (parent of this node)
	 * which is doing the importing
	 */
	public ElementDef getImportingElement();

} // ImportMappingSet
