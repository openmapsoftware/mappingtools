/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.ModelFilter;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Filter Set</b></em>'.
 * 
 * This object is a wrapper for all the model-based filters on an object mapping. 
 * It has no other meaning.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ModelFilterSet#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.ModelFilterSet#getModelFilters <em>Model Filters</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getModelFilterSet()
 * @model
 * @generated
 */
public interface ModelFilterSet extends EObject {
	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * This might be used to describe the significance of the set of model-based filters.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see com.openMap1.mapper.MapperPackage#getModelFilterSet_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ModelFilterSet#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Model Filters</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ModelFilter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The model-based filters which apply to the containing ObjMapping.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Model Filters</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getModelFilterSet_ModelFilters()
	 * @model containment="true"
	 * @generated
	 */
	EList<ModelFilter> getModelFilters();

} // ModelFilterSet
