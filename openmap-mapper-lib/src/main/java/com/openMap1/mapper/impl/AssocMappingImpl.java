/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import java.util.Map;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.DiagnosticChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Assoc Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.AssocMappingImpl#getMappedEnd1 <em>Mapped End1</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.AssocMappingImpl#getMappedEnd2 <em>Mapped End2</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AssocMappingImpl extends MappingImpl implements AssocMapping {
	/**
	 * The cached value of the '{@link #getMappedEnd1() <em>Mapped End1</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedEnd1()
	 * @generated
	 * @ordered
	 */
	protected AssocEndMapping mappedEnd1;

	/**
	 * The cached value of the '{@link #getMappedEnd2() <em>Mapped End2</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedEnd2()
	 * @generated
	 * @ordered
	 */
	protected AssocEndMapping mappedEnd2;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AssocMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.ASSOC_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AssocEndMapping getMappedEnd1() {
		return mappedEnd1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMappedEnd1(AssocEndMapping newMappedEnd1, NotificationChain msgs) {
		AssocEndMapping oldMappedEnd1 = mappedEnd1;
		mappedEnd1 = newMappedEnd1;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.ASSOC_MAPPING__MAPPED_END1, oldMappedEnd1, newMappedEnd1);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappedEnd1(AssocEndMapping newMappedEnd1) {
		if (newMappedEnd1 != mappedEnd1) {
			NotificationChain msgs = null;
			if (mappedEnd1 != null)
				msgs = ((InternalEObject)mappedEnd1).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.ASSOC_MAPPING__MAPPED_END1, null, msgs);
			if (newMappedEnd1 != null)
				msgs = ((InternalEObject)newMappedEnd1).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.ASSOC_MAPPING__MAPPED_END1, null, msgs);
			msgs = basicSetMappedEnd1(newMappedEnd1, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ASSOC_MAPPING__MAPPED_END1, newMappedEnd1, newMappedEnd1));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AssocEndMapping getMappedEnd2() {
		return mappedEnd2;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMappedEnd2(AssocEndMapping newMappedEnd2, NotificationChain msgs) {
		AssocEndMapping oldMappedEnd2 = mappedEnd2;
		mappedEnd2 = newMappedEnd2;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.ASSOC_MAPPING__MAPPED_END2, oldMappedEnd2, newMappedEnd2);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappedEnd2(AssocEndMapping newMappedEnd2) {
		if (newMappedEnd2 != mappedEnd2) {
			NotificationChain msgs = null;
			if (mappedEnd2 != null)
				msgs = ((InternalEObject)mappedEnd2).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.ASSOC_MAPPING__MAPPED_END2, null, msgs);
			if (newMappedEnd2 != null)
				msgs = ((InternalEObject)newMappedEnd2).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.ASSOC_MAPPING__MAPPED_END2, null, msgs);
			msgs = basicSetMappedEnd2(newMappedEnd2, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ASSOC_MAPPING__MAPPED_END2, newMappedEnd2, newMappedEnd2));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapperPackage.ASSOC_MAPPING__MAPPED_END1:
				return basicSetMappedEnd1(null, msgs);
			case MapperPackage.ASSOC_MAPPING__MAPPED_END2:
				return basicSetMappedEnd2(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.ASSOC_MAPPING__MAPPED_END1:
				return getMappedEnd1();
			case MapperPackage.ASSOC_MAPPING__MAPPED_END2:
				return getMappedEnd2();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MapperPackage.ASSOC_MAPPING__MAPPED_END1:
				setMappedEnd1((AssocEndMapping)newValue);
				return;
			case MapperPackage.ASSOC_MAPPING__MAPPED_END2:
				setMappedEnd2((AssocEndMapping)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MapperPackage.ASSOC_MAPPING__MAPPED_END1:
				setMappedEnd1((AssocEndMapping)null);
				return;
			case MapperPackage.ASSOC_MAPPING__MAPPED_END2:
				setMappedEnd2((AssocEndMapping)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MapperPackage.ASSOC_MAPPING__MAPPED_END1:
				return mappedEnd1 != null;
			case MapperPackage.ASSOC_MAPPING__MAPPED_END2:
				return mappedEnd2 != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * e ranges over 0..1 for ends 1..2
	 * @return
	 */
	public AssocEndMapping getMappedEnd(int e) throws MapperException
	{
		AssocEndMapping aem = null;
		if ((e < 0)|(e > 1)) throw new MapperException("Invalid association end index: " + e);
		if (e == 0) aem = getMappedEnd1();
		if (e == 1) aem = getMappedEnd2();
		return aem;
	}

	/**
	 * Two mappings (usually in different mapping sets) are equivalent if they 
	 * refer to the same thing in the Class model (eg the same class, the same property)
	 * and with the same subset.
	 * Two mappings in the same mapping set should never be equivalent.
	 * @param m
	 * @return
	 */
	public boolean equivalentTo(Mapping m)
	{
		boolean eq = false;
		if (m instanceof AssocMapping) 
		{
			AssocMapping am = (AssocMapping)m;
			eq = ((am.getMappedAssociation().equals(getMappedAssociation())) &&
					(am.getMappedEnd1().equivalentTo(getMappedEnd1())) &&
					(am.getMappedEnd2().equivalentTo(getMappedEnd2())));
		}
		return eq;
	}

	/**
	 * Override validation of the class name, from the superclass Mapper, 
	 * because it is not appropriate for this class.
	 */
	public boolean mappedClassIsInClassModel
		(DiagnosticChain diagnostics, Map<?,?> context) {return true;}
	
	/**
	 * the association name is now calculated automatically by
	 * concatenating the association end names; or if they only differ 
	 * by a final '_1' and '_2', by removing the final '_1' and '_2'
	 * @return
	 */
	public String getMappedAssociation()
	{
		String role1 = "";
		if (getMappedEnd1() != null) role1 = getMappedEnd1().getMappedRole();
		String role2 = "";
		if (getMappedEnd2() != null) role2 = getMappedEnd2().getMappedRole();
		return ModelUtil.assocName(role1,role2);
	}

} //AssocMappingImpl
