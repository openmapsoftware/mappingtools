/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ValuePair;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Value Pair</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ValuePairImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ValuePairImpl#getStructureValue <em>Structure Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ValuePairImpl#getModelValue <em>Model Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ValuePairImpl#isPreferredIn <em>Preferred In</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ValuePairImpl#isPreferredOut <em>Preferred Out</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ValuePairImpl extends EObjectImpl implements ValuePair {
	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getStructureValue() <em>Structure Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStructureValue()
	 * @ordered
	 */
	protected static final String STRUCTURE_VALUE_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getStructureValue() <em>Structure Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStructureValue()
	 * @generated
	 * @ordered
	 */
	protected String structureValue = STRUCTURE_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getModelValue() <em>Model Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModelValue()
	 * @ordered
	 */
	protected static final String MODEL_VALUE_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getModelValue() <em>Model Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModelValue()
	 * @generated
	 * @ordered
	 */
	protected String modelValue = MODEL_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #isPreferredIn() <em>Preferred In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPreferredIn()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PREFERRED_IN_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isPreferredIn() <em>Preferred In</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPreferredIn()
	 * @generated
	 * @ordered
	 */
	protected boolean preferredIn = PREFERRED_IN_EDEFAULT;

	/**
	 * The default value of the '{@link #isPreferredOut() <em>Preferred Out</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPreferredOut()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PREFERRED_OUT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isPreferredOut() <em>Preferred Out</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPreferredOut()
	 * @generated
	 * @ordered
	 */
	protected boolean preferredOut = PREFERRED_OUT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValuePairImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.VALUE_PAIR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.VALUE_PAIR__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getStructureValue() {
		return structureValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStructureValue(String newStructureValue) {
		String oldStructureValue = structureValue;
		structureValue = newStructureValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.VALUE_PAIR__STRUCTURE_VALUE, oldStructureValue, structureValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getModelValue() {
		return modelValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModelValue(String newModelValue) {
		String oldModelValue = modelValue;
		modelValue = newModelValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.VALUE_PAIR__MODEL_VALUE, oldModelValue, modelValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isPreferredIn() {
		return preferredIn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPreferredIn(boolean newPreferredIn) {
		boolean oldPreferredIn = preferredIn;
		preferredIn = newPreferredIn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.VALUE_PAIR__PREFERRED_IN, oldPreferredIn, preferredIn));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isPreferredOut() {
		return preferredOut;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPreferredOut(boolean newPreferredOut) {
		boolean oldPreferredOut = preferredOut;
		preferredOut = newPreferredOut;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.VALUE_PAIR__PREFERRED_OUT, oldPreferredOut, preferredOut));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.VALUE_PAIR__DESCRIPTION:
				return getDescription();
			case MapperPackage.VALUE_PAIR__STRUCTURE_VALUE:
				return getStructureValue();
			case MapperPackage.VALUE_PAIR__MODEL_VALUE:
				return getModelValue();
			case MapperPackage.VALUE_PAIR__PREFERRED_IN:
				return isPreferredIn() ? Boolean.TRUE : Boolean.FALSE;
			case MapperPackage.VALUE_PAIR__PREFERRED_OUT:
				return isPreferredOut() ? Boolean.TRUE : Boolean.FALSE;
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
			case MapperPackage.VALUE_PAIR__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MapperPackage.VALUE_PAIR__STRUCTURE_VALUE:
				setStructureValue((String)newValue);
				return;
			case MapperPackage.VALUE_PAIR__MODEL_VALUE:
				setModelValue((String)newValue);
				return;
			case MapperPackage.VALUE_PAIR__PREFERRED_IN:
				setPreferredIn(((Boolean)newValue).booleanValue());
				return;
			case MapperPackage.VALUE_PAIR__PREFERRED_OUT:
				setPreferredOut(((Boolean)newValue).booleanValue());
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
			case MapperPackage.VALUE_PAIR__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MapperPackage.VALUE_PAIR__STRUCTURE_VALUE:
				setStructureValue(STRUCTURE_VALUE_EDEFAULT);
				return;
			case MapperPackage.VALUE_PAIR__MODEL_VALUE:
				setModelValue(MODEL_VALUE_EDEFAULT);
				return;
			case MapperPackage.VALUE_PAIR__PREFERRED_IN:
				setPreferredIn(PREFERRED_IN_EDEFAULT);
				return;
			case MapperPackage.VALUE_PAIR__PREFERRED_OUT:
				setPreferredOut(PREFERRED_OUT_EDEFAULT);
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
			case MapperPackage.VALUE_PAIR__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MapperPackage.VALUE_PAIR__STRUCTURE_VALUE:
				return STRUCTURE_VALUE_EDEFAULT == null ? structureValue != null : !STRUCTURE_VALUE_EDEFAULT.equals(structureValue);
			case MapperPackage.VALUE_PAIR__MODEL_VALUE:
				return MODEL_VALUE_EDEFAULT == null ? modelValue != null : !MODEL_VALUE_EDEFAULT.equals(modelValue);
			case MapperPackage.VALUE_PAIR__PREFERRED_IN:
				return preferredIn != PREFERRED_IN_EDEFAULT;
			case MapperPackage.VALUE_PAIR__PREFERRED_OUT:
				return preferredOut != PREFERRED_OUT_EDEFAULT;
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
		result.append(" (description: ");
		result.append(description);
		result.append(", structureValue: ");
		result.append(structureValue);
		result.append(", modelValue: ");
		result.append(modelValue);
		result.append(", preferredIn: ");
		result.append(preferredIn);
		result.append(", preferredOut: ");
		result.append(preferredOut);
		result.append(')');
		return result.toString();
	}

} //ValuePairImpl
