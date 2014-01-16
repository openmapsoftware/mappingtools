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

import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.ModelFilterSet;



/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Obj Mapping</b></em>'.
 * This is an object mapping ,which declares that certain nodes in the 
 * structure represent object of some class in the class model, under 
 * conditions defined by child nodes.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ObjMapping#getRootPath <em>Root Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.ObjMapping#isMultiplyRepresented <em>Multiply Represented</em>}</li>
 *   <li>{@link com.openMap1.mapper.ObjMapping#getModelFilterSet <em>Model Filter Set</em>}</li>
 *   <li>{@link com.openMap1.mapper.ObjMapping#getFixedPropertyValues <em>Fixed Property Values</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getObjMapping()
 * @model
 * @generated
 */
public interface ObjMapping extends Mapping {
	/**
	 * Returns the value of the '<em><b>Root Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Beware - this is not the root path to the mapped node. 
	 * It is empty unless it has been  edited by the user (e.g to set an indefinite root path
	 * such as '//a'; and I don't think indefinite root paths are yet 
	 * properly supported by the tools.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root Path</em>' attribute.
	 * @see #setRootPath(String)
	 * @see com.openMap1.mapper.MapperPackage#getObjMapping_RootPath()
	 * @model
	 */
	String getRootPath();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ObjMapping#getRootPath <em>Root Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root Path</em>' attribute.
	 * @see #getRootPath()
	 * @generated
	 */
	void setRootPath(String value);

	/**
	 * Returns the value of the '<em><b>Multiply Represented</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Usually the default 'false'. True only if there can be multiple representations
	 * of the same object in the same class by this mapping.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Multiply Represented</em>' attribute.
	 * @see #setMultiplyRepresented(boolean)
	 * @see com.openMap1.mapper.MapperPackage#getObjMapping_MultiplyRepresented()
	 * @model
	 * @generated
	 */
	boolean isMultiplyRepresented();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ObjMapping#isMultiplyRepresented <em>Multiply Represented</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Multiply Represented</em>' attribute.
	 * @see #isMultiplyRepresented()
	 * @generated
	 */
	void setMultiplyRepresented(boolean value);

	/**
	 * Returns the value of the '<em><b>Model Filter Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The filter set is a set of conditions defined only in terms of the class model
	 * (not the mapped structure) which define which objects of the class are 
	 * represented by this mapping.
	 * e.g a purchase order XML document contains only purchase order lines 
	 * which are part of a particular purchase order. No mapping describes
	 * all objects of some class in the universe.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Model Filter Set</em>' containment reference.
	 * @see #setModelFilterSet(ModelFilterSet)
	 * @see com.openMap1.mapper.MapperPackage#getObjMapping_ModelFilterSet()
	 * @model containment="true"
	 * @generated
	 */
	ModelFilterSet getModelFilterSet();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ObjMapping#getModelFilterSet <em>Model Filter Set</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Model Filter Set</em>' containment reference.
	 * @see #getModelFilterSet()
	 * @generated
	 */
	void setModelFilterSet(ModelFilterSet value);

	/**
	 * Returns the value of the '<em><b>Fixed Property Values</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.FixedPropertyValue}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If every instance of the class represented by this mapping
	 * has the same fixed values of some properties, then the properties and their fixed
	 * values are defined here.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fixed Property Values</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getObjMapping_FixedPropertyValues()
	 * @model containment="true"
	 * @generated
	 */
	EList<FixedPropertyValue> getFixedPropertyValues();

	/**
	 * <!-- begin-user-doc -->
	 * Vaidation check that this subset name is not used for any other object mapping to the same class
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean subsetIsUniqueWithinClass(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean rootPathIsConsistentWithNodePosition(DiagnosticChain diagnostics, Map<?, ?> context);

} // ObjMapping
