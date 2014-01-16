/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import com.openMap1.mapper.ConversionImplementation;
import com.openMap1.mapper.LocalPropertyConversion;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ValuePair;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Local Property Conversion</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.LocalPropertyConversionImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.LocalPropertyConversionImpl#getInConversionImplementations <em>In Conversion Implementations</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.LocalPropertyConversionImpl#getOutConversionImplementations <em>Out Conversion Implementations</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.LocalPropertyConversionImpl#getValuePairs <em>Value Pairs</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LocalPropertyConversionImpl extends EObjectImpl implements LocalPropertyConversion {
	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

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
	 * The cached value of the '{@link #getInConversionImplementations() <em>In Conversion Implementations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInConversionImplementations()
	 * @generated
	 * @ordered
	 */
	protected EList<ConversionImplementation> inConversionImplementations;

	/**
	 * The cached value of the '{@link #getOutConversionImplementations() <em>Out Conversion Implementations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutConversionImplementations()
	 * @generated
	 * @ordered
	 */
	protected EList<ConversionImplementation> outConversionImplementations;

	/**
	 * The cached value of the '{@link #getValuePairs() <em>Value Pairs</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValuePairs()
	 * @generated
	 * @ordered
	 */
	protected EList<ValuePair> valuePairs;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalPropertyConversionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.LOCAL_PROPERTY_CONVERSION;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.LOCAL_PROPERTY_CONVERSION__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConversionImplementation> getInConversionImplementations() {
		if (inConversionImplementations == null) {
			inConversionImplementations = new EObjectContainmentEList<ConversionImplementation>(ConversionImplementation.class, this, MapperPackage.LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS);
		}
		return inConversionImplementations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConversionImplementation> getOutConversionImplementations() {
		if (outConversionImplementations == null) {
			outConversionImplementations = new EObjectContainmentEList<ConversionImplementation>(ConversionImplementation.class, this, MapperPackage.LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS);
		}
		return outConversionImplementations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ValuePair> getValuePairs() {
		if (valuePairs == null) {
			valuePairs = new EObjectContainmentEList<ValuePair>(ValuePair.class, this, MapperPackage.LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS);
		}
		return valuePairs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS:
				return ((InternalEList<?>)getInConversionImplementations()).basicRemove(otherEnd, msgs);
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS:
				return ((InternalEList<?>)getOutConversionImplementations()).basicRemove(otherEnd, msgs);
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS:
				return ((InternalEList<?>)getValuePairs()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__DESCRIPTION:
				return getDescription();
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS:
				return getInConversionImplementations();
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS:
				return getOutConversionImplementations();
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS:
				return getValuePairs();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS:
				getInConversionImplementations().clear();
				getInConversionImplementations().addAll((Collection<? extends ConversionImplementation>)newValue);
				return;
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS:
				getOutConversionImplementations().clear();
				getOutConversionImplementations().addAll((Collection<? extends ConversionImplementation>)newValue);
				return;
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS:
				getValuePairs().clear();
				getValuePairs().addAll((Collection<? extends ValuePair>)newValue);
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
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS:
				getInConversionImplementations().clear();
				return;
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS:
				getOutConversionImplementations().clear();
				return;
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS:
				getValuePairs().clear();
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
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__IN_CONVERSION_IMPLEMENTATIONS:
				return inConversionImplementations != null && !inConversionImplementations.isEmpty();
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__OUT_CONVERSION_IMPLEMENTATIONS:
				return outConversionImplementations != null && !outConversionImplementations.isEmpty();
			case MapperPackage.LOCAL_PROPERTY_CONVERSION__VALUE_PAIRS:
				return valuePairs != null && !valuePairs.isEmpty();
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
		result.append(')');
		return result.toString();
	}

} //LocalPropertyConversionImpl
