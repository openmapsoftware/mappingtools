/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.core.MapperException;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Assoc Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.AssocMapping#getMappedEnd1 <em>Mapped End1</em>}</li>
 *   <li>{@link com.openMap1.mapper.AssocMapping#getMappedEnd2 <em>Mapped End2</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getAssocMapping()
 * @model
 * @generated
 */
public interface AssocMapping extends Mapping {
	/**
	 * Returns the value of the '<em><b>Mapped End1</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The AssocEndMapping object for 'end 1' of the association. 
	 * This is the end whose role name (for going to that end)
	 * is lexically first.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped End1</em>' containment reference.
	 * @see #setMappedEnd1(AssocEndMapping)
	 * @see com.openMap1.mapper.MapperPackage#getAssocMapping_MappedEnd1()
	 * @model containment="true"
	 * @generated
	 */
	AssocEndMapping getMappedEnd1();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.AssocMapping#getMappedEnd1 <em>Mapped End1</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped End1</em>' containment reference.
	 * @see #getMappedEnd1()
	 * @generated
	 */
	void setMappedEnd1(AssocEndMapping value);

	/**
	 * Returns the value of the '<em><b>Mapped End2</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The AssocEndMapping object for 'end 2' of the association. 
	 * This is the end whose role name (for going to that end)
	 * is lexically last.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped End2</em>' containment reference.
	 * @see #setMappedEnd2(AssocEndMapping)
	 * @see com.openMap1.mapper.MapperPackage#getAssocMapping_MappedEnd2()
	 * @model containment="true"
	 * @generated
	 */
	AssocEndMapping getMappedEnd2();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.AssocMapping#getMappedEnd2 <em>Mapped End2</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped End2</em>' containment reference.
	 * @see #getMappedEnd2()
	 * @generated
	 */
	void setMappedEnd2(AssocEndMapping value);
	
	/**
	 * e ranges over 0..1 for ends 1..2
	 * @return the AssocEndMapping object for that end of the association
	 */
	public AssocEndMapping getMappedEnd(int e) throws MapperException;
	
	/**
	 * the association name is now calculated automatically by
	 * concatenating the association end names; or if they only differ 
	 * by a final '_1' and '_2', by removing the final '_1' and '_2'
	 * @return
	 */
	public String getMappedAssociation();
	
	

} // AssocMapping
