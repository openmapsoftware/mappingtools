/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ValueCondition;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Value Condition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ValueConditionImpl#getRightValue <em>Right Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ValueConditionImpl extends MappingConditionImpl implements ValueCondition {
	/**
	 * The default value of the '{@link #getRightValue() <em>Right Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRightValue()
	 * @generated
	 * @ordered
	 */
	protected static final String RIGHT_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRightValue() <em>Right Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRightValue()
	 * @generated
	 * @ordered
	 */
	protected String rightValue = RIGHT_VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValueConditionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.VALUE_CONDITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRightValue() {
		return rightValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRightValue(String newRightValue) {
		String oldRightValue = rightValue;
		rightValue = newRightValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.VALUE_CONDITION__RIGHT_VALUE, oldRightValue, rightValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.VALUE_CONDITION__RIGHT_VALUE:
				return getRightValue();
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
			case MapperPackage.VALUE_CONDITION__RIGHT_VALUE:
				setRightValue((String)newValue);
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
			case MapperPackage.VALUE_CONDITION__RIGHT_VALUE:
				setRightValue(RIGHT_VALUE_EDEFAULT);
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
			case MapperPackage.VALUE_CONDITION__RIGHT_VALUE:
				return RIGHT_VALUE_EDEFAULT == null ? rightValue != null : !RIGHT_VALUE_EDEFAULT.equals(rightValue);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (rightValue: ");
		result.append(rightValue);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * for writing out in the Mappings view
	 */
	public String getRHS()
	{
		return ("'" + this.getRightValue() + "'");
	}


} //ValueConditionImpl
