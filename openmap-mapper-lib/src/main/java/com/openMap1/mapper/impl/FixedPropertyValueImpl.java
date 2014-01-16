/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ObjMapping;

import java.util.Map;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Fixed Property Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.FixedPropertyValueImpl#getMappedProperty <em>Mapped Property</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.FixedPropertyValueImpl#getFixedValue <em>Fixed Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.FixedPropertyValueImpl#getValueType <em>Value Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FixedPropertyValueImpl extends EObjectImpl implements FixedPropertyValue {
	/**
	 * The default value of the '{@link #getMappedProperty() <em>Mapped Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedProperty()
	 * @generated
	 * @ordered
	 */
	protected static final String MAPPED_PROPERTY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMappedProperty() <em>Mapped Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedProperty()
	 * @generated
	 * @ordered
	 */
	protected String mappedProperty = MAPPED_PROPERTY_EDEFAULT;

	/**
	 * The default value of the '{@link #getFixedValue() <em>Fixed Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFixedValue()
	 * @generated
	 * @ordered
	 */
	protected static final String FIXED_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFixedValue() <em>Fixed Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFixedValue()
	 * @generated
	 * @ordered
	 */
	protected String fixedValue = FIXED_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getValueType() <em>Value Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueType()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValueType() <em>Value Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueType()
	 * @generated
	 * @ordered
	 */
	protected String valueType = VALUE_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FixedPropertyValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.FIXED_PROPERTY_VALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappedProperty() {
		return mappedProperty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappedProperty(String newMappedProperty) {
		String oldMappedProperty = mappedProperty;
		mappedProperty = newMappedProperty;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.FIXED_PROPERTY_VALUE__MAPPED_PROPERTY, oldMappedProperty, mappedProperty));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFixedValue() {
		return fixedValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFixedValue(String newFixedValue) {
		String oldFixedValue = fixedValue;
		fixedValue = newFixedValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.FIXED_PROPERTY_VALUE__FIXED_VALUE, oldFixedValue, fixedValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getValueType() {
		return valueType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValueType(String newValueType) {
		String oldValueType = valueType;
		valueType = newValueType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.FIXED_PROPERTY_VALUE__VALUE_TYPE, oldValueType, valueType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the class of the object mapping (if it exists) has the property.
	 * Can fixed properties be pseudo-properties, i.e inputs to property conversions? Yes.
	 * <!-- end-user-doc -->
	 */
	public boolean classHasProperty(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean classHasProp = true;
		// only make the check if class of the object mapping is in the class model
		ObjMapping om = (ObjMapping)eContainer();
		if (ModelUtil.isInClassModel(om.getMappedClass(), om.getMappedPackage(),this))
		{
			classHasProp = ModelUtil.hasPropertyOrPseudoProperty(om.getMappedClass(), om.getMappedPackage(),om.getSubset(), getMappedProperty(), this);
		}
		if (!classHasProp) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.FIXED_PROPERTY_VALUE__CLASS_HAS_PROPERTY,
						 "Class '" + om.getMappedClass() + "' has no property '" + getMappedProperty() + "'",
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.FIXED_PROPERTY_VALUE__MAPPED_PROPERTY:
				return getMappedProperty();
			case MapperPackage.FIXED_PROPERTY_VALUE__FIXED_VALUE:
				return getFixedValue();
			case MapperPackage.FIXED_PROPERTY_VALUE__VALUE_TYPE:
				return getValueType();
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
			case MapperPackage.FIXED_PROPERTY_VALUE__MAPPED_PROPERTY:
				setMappedProperty((String)newValue);
				return;
			case MapperPackage.FIXED_PROPERTY_VALUE__FIXED_VALUE:
				setFixedValue((String)newValue);
				return;
			case MapperPackage.FIXED_PROPERTY_VALUE__VALUE_TYPE:
				setValueType((String)newValue);
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
			case MapperPackage.FIXED_PROPERTY_VALUE__MAPPED_PROPERTY:
				setMappedProperty(MAPPED_PROPERTY_EDEFAULT);
				return;
			case MapperPackage.FIXED_PROPERTY_VALUE__FIXED_VALUE:
				setFixedValue(FIXED_VALUE_EDEFAULT);
				return;
			case MapperPackage.FIXED_PROPERTY_VALUE__VALUE_TYPE:
				setValueType(VALUE_TYPE_EDEFAULT);
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
			case MapperPackage.FIXED_PROPERTY_VALUE__MAPPED_PROPERTY:
				return MAPPED_PROPERTY_EDEFAULT == null ? mappedProperty != null : !MAPPED_PROPERTY_EDEFAULT.equals(mappedProperty);
			case MapperPackage.FIXED_PROPERTY_VALUE__FIXED_VALUE:
				return FIXED_VALUE_EDEFAULT == null ? fixedValue != null : !FIXED_VALUE_EDEFAULT.equals(fixedValue);
			case MapperPackage.FIXED_PROPERTY_VALUE__VALUE_TYPE:
				return VALUE_TYPE_EDEFAULT == null ? valueType != null : !VALUE_TYPE_EDEFAULT.equals(valueType);
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
		result.append(" (mappedProperty: ");
		result.append(mappedProperty);
		result.append(", fixedValue: ");
		result.append(fixedValue);
		result.append(", valueType: ");
		result.append(valueType);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * @return text description of the fixed value, for the Filter column in Mappings view
	 */
	public String getDetails()
	{
		return (getMappedProperty() + " = '" + getFixedValue() + "'");
	}

} //FixedPropertyValueImpl
