/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import java.util.Map;


import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.ConditionTest;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ModelPropertyFilter;
import com.openMap1.mapper.ObjMapping;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model Property Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ModelPropertyFilterImpl#getPropertyName <em>Property Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ModelPropertyFilterImpl#getValue <em>Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ModelPropertyFilterImpl#getTest <em>Test</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelPropertyFilterImpl extends ModelFilterImpl implements ModelPropertyFilter {
	/**
	 * The default value of the '{@link #getPropertyName() <em>Property Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROPERTY_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPropertyName() <em>Property Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyName()
	 * @generated
	 * @ordered
	 */
	protected String propertyName = PROPERTY_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected String value = VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getTest() <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTest()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionTest TEST_EDEFAULT = ConditionTest.EQUALS;

	/**
	 * The cached value of the '{@link #getTest() <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTest()
	 * @generated
	 * @ordered
	 */
	protected ConditionTest test = TEST_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelPropertyFilterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.MODEL_PROPERTY_FILTER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPropertyName(String newPropertyName) {
		String oldPropertyName = propertyName;
		propertyName = newPropertyName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MODEL_PROPERTY_FILTER__PROPERTY_NAME, oldPropertyName, propertyName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(String newValue) {
		String oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MODEL_PROPERTY_FILTER__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionTest getTest() {
		return test;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTest(ConditionTest newTest) {
		ConditionTest oldTest = test;
		test = newTest == null ? TEST_EDEFAULT : newTest;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MODEL_PROPERTY_FILTER__TEST, oldTest, test));
	}

	/**
	 * <!-- begin-user-doc -->
	 * Check that the class being filtered has this property.
	 * Only make the check if the class exists; 
	 * a nonexistent class is detected by another check.
	 * <!-- end-user-doc -->
	 */
	public boolean classHasProperty(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean hasProperty = true;
		String thisClassName = "";
		String thisPackageName = "";
		EObject om = eContainer().eContainer();
		if (om instanceof ObjMapping) 
		{
			thisClassName = ((ObjMapping)om).getMappedClass();
			thisPackageName = ((ObjMapping)om).getMappedPackage();
		}
		if (ModelUtil.isInClassModel(thisClassName, thisPackageName,this))
			hasProperty = ModelUtil.hasProperty(thisClassName, thisPackageName, getPropertyName(), this);
		if (!hasProperty) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.MODEL_PROPERTY_FILTER__CLASS_HAS_PROPERTY,
						 "Class '" + thisClassName + "' has no property '" + getPropertyName() + "'",
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
			case MapperPackage.MODEL_PROPERTY_FILTER__PROPERTY_NAME:
				return getPropertyName();
			case MapperPackage.MODEL_PROPERTY_FILTER__VALUE:
				return getValue();
			case MapperPackage.MODEL_PROPERTY_FILTER__TEST:
				return getTest();
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
			case MapperPackage.MODEL_PROPERTY_FILTER__PROPERTY_NAME:
				setPropertyName((String)newValue);
				return;
			case MapperPackage.MODEL_PROPERTY_FILTER__VALUE:
				setValue((String)newValue);
				return;
			case MapperPackage.MODEL_PROPERTY_FILTER__TEST:
				setTest((ConditionTest)newValue);
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
			case MapperPackage.MODEL_PROPERTY_FILTER__PROPERTY_NAME:
				setPropertyName(PROPERTY_NAME_EDEFAULT);
				return;
			case MapperPackage.MODEL_PROPERTY_FILTER__VALUE:
				setValue(VALUE_EDEFAULT);
				return;
			case MapperPackage.MODEL_PROPERTY_FILTER__TEST:
				setTest(TEST_EDEFAULT);
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
			case MapperPackage.MODEL_PROPERTY_FILTER__PROPERTY_NAME:
				return PROPERTY_NAME_EDEFAULT == null ? propertyName != null : !PROPERTY_NAME_EDEFAULT.equals(propertyName);
			case MapperPackage.MODEL_PROPERTY_FILTER__VALUE:
				return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
			case MapperPackage.MODEL_PROPERTY_FILTER__TEST:
				return test != TEST_EDEFAULT;
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
		result.append(" (propertyName: ");
		result.append(propertyName);
		result.append(", value: ");
		result.append(value);
		result.append(", test: ");
		result.append(test);
		result.append(')');
		return result.toString();
	}
	
	
	/**
	 * 
	 * @return description of the filter to go in the Filter column of the Mappings View
	 */
	public String getFilterColumnText()
	{
		String filter = getPropertyName() + " " + getTest() + " '" + getValue() + "'";
		return filter;
	}


} //ModelPropertyFilterImpl
