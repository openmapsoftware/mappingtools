/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.Note;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annotations</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.Annotations#getNotes <em>Notes</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getAnnotations()
 * @model
 * @generated
 */
public interface Annotations extends EObject {
	/**
	 * Returns the value of the '<em><b>Notes</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.Note}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Notes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Notes</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getAnnotations_Notes()
	 * @model containment="true"
	 * @generated
	 */
	EList<Note> getNotes();
	

} // Annotations
