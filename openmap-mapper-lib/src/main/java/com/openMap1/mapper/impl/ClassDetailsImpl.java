/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.ConversionSense;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.PropertyConversion;

import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;

import java.util.Map;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class Details</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ClassDetailsImpl#getClassName <em>Class Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ClassDetailsImpl#getPackageName <em>Package Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ClassDetailsImpl#getPropertyConversions <em>Property Conversions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ClassDetailsImpl extends EObjectImpl implements ClassDetails {
	/**
	 * The default value of the '{@link #getClassName() <em>Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassName()
	 * @ordered
	 */
	protected static final String CLASS_NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getClassName() <em>Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassName()
	 * @generated
	 * @ordered
	 */
	protected String className = CLASS_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getPackageName() <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageName()
	 * @ordered
	 */
	protected static final String PACKAGE_NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getPackageName() <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageName()
	 * @generated
	 * @ordered
	 */
	protected String packageName = PACKAGE_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPropertyConversions() <em>Property Conversions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyConversions()
	 * @generated
	 * @ordered
	 */
	protected EList<PropertyConversion> propertyConversions;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ClassDetailsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.CLASS_DETAILS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setClassName(String newClassName) {
		String oldClassName = className;
		className = newClassName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.CLASS_DETAILS__CLASS_NAME, oldClassName, className));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPackageName(String newPackageName) {
		String oldPackageName = packageName;
		packageName = newPackageName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.CLASS_DETAILS__PACKAGE_NAME, oldPackageName, packageName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PropertyConversion> getPropertyConversions() {
		if (propertyConversions == null) {
			propertyConversions = new EObjectContainmentEList<PropertyConversion>(PropertyConversion.class, this, MapperPackage.CLASS_DETAILS__PROPERTY_CONVERSIONS);
		}
		return propertyConversions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check the class exists in the class model
	 * <!-- end-user-doc -->
	 */
	public boolean classIsInClassModel(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean classInModel = ModelUtil.isInClassModel(getClassName(), getPackageName(),this);
		if (!classInModel) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.CLASS_DETAILS__CLASS_IS_IN_CLASS_MODEL,
						 "Class '" + getClassName() + "' is not in the class model",
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that there is not more than one 'out' conversion for any pseudo-property and subset
	 * <!-- end-user-doc -->
	 */
	public boolean onlyOneOutConversionPerPseudoPropertyAndSubset(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean notMoreThanOne = true;
		Hashtable<String,String> outConvs = new Hashtable<String,String>();
		String errorMess = "There is more than one 'out' conversion for the pseudo-properties: ";
		for (Iterator<PropertyConversion> it = getPropertyConversions().iterator();it.hasNext();)
		{
			PropertyConversion pc = it.next();
			if (pc.getSense() == ConversionSense.OUT)
			{
				String pName = pc.getResultSlot();
				if (!pc.getSubset().equals("")) pName = pName+ "(" + pc.getSubset() + ")";
				if (outConvs.get(pName) != null)
				{
					notMoreThanOne= false;
					errorMess = errorMess + pName + " ";
				}
				outConvs.put(pName,"1");
			}
		}
		if (!notMoreThanOne) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.CLASS_DETAILS__ONLY_ONE_OUT_CONVERSION_PER_PSEUDO_PROPERTY_AND_SUBSET,
						 errorMess,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that there is not more than one 'in' conversion for any property and subset
	 * <!-- end-user-doc -->
	 */
	public boolean onlyOneInConversionPerPropertyAndSubset(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean notMoreThanOne = true;
		Hashtable<String,String> inConvs = new Hashtable<String,String>();
		String errorMess = "There is more than one 'in' conversion for the properties: ";
		for (Iterator<PropertyConversion> it = getPropertyConversions().iterator();it.hasNext();)
		{
			PropertyConversion pc = it.next();
			if (pc.getSense() == ConversionSense.IN)
			{
				String pName = pc.getResultSlot();
				if (!pc.getSubset().equals("")) pName = pName+ "(" + pc.getSubset() + ")";
				if (inConvs.get(pName) != null)
				{
					notMoreThanOne= false;
					errorMess = errorMess + pName + " ";
				}
				inConvs.put(pName,"1");
			}
		}
		if (!notMoreThanOne) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.CLASS_DETAILS__ONLY_ONE_IN_CONVERSION_PER_PROPERTY_AND_SUBSET,
						 errorMess,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * For any 'in' property conversion, check that there is no direct mapping for the property
	 * and subset.
	 * (this validation should really belong on the child PropertyConversion)
	 * <!-- end-user-doc -->
	 */
	public boolean convertedPropertyIsNotRepresentedDirectly(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean noPropertyRepresentedDirectly = true;
		String propsRepresented = "Converted properties are also represented directly in mappings: ";
		for (Iterator<PropertyConversion> it = getPropertyConversions().iterator();it.hasNext();)
		{
			PropertyConversion pc = it.next();
			if (!pc.convertedPropertyIsNotRepresentedDirectly())
			{
				noPropertyRepresentedDirectly = false;
				propsRepresented = propsRepresented + "'" + pc.getResultSlot() + "' ";
			}
		}
		if (!noPropertyRepresentedDirectly) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.CLASS_DETAILS__CONVERTED_PROPERTY_IS_NOT_REPRESENTED_DIRECTLY,
						 propsRepresented,
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
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapperPackage.CLASS_DETAILS__PROPERTY_CONVERSIONS:
				return ((InternalEList<?>)getPropertyConversions()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.CLASS_DETAILS__CLASS_NAME:
				return getClassName();
			case MapperPackage.CLASS_DETAILS__PACKAGE_NAME:
				return getPackageName();
			case MapperPackage.CLASS_DETAILS__PROPERTY_CONVERSIONS:
				return getPropertyConversions();
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
			case MapperPackage.CLASS_DETAILS__CLASS_NAME:
				setClassName((String)newValue);
				return;
			case MapperPackage.CLASS_DETAILS__PACKAGE_NAME:
				setPackageName((String)newValue);
				return;
			case MapperPackage.CLASS_DETAILS__PROPERTY_CONVERSIONS:
				getPropertyConversions().clear();
				getPropertyConversions().addAll((Collection<? extends PropertyConversion>)newValue);
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
			case MapperPackage.CLASS_DETAILS__CLASS_NAME:
				setClassName(CLASS_NAME_EDEFAULT);
				return;
			case MapperPackage.CLASS_DETAILS__PACKAGE_NAME:
				setPackageName(PACKAGE_NAME_EDEFAULT);
				return;
			case MapperPackage.CLASS_DETAILS__PROPERTY_CONVERSIONS:
				getPropertyConversions().clear();
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
			case MapperPackage.CLASS_DETAILS__CLASS_NAME:
				return CLASS_NAME_EDEFAULT == null ? className != null : !CLASS_NAME_EDEFAULT.equals(className);
			case MapperPackage.CLASS_DETAILS__PACKAGE_NAME:
				return PACKAGE_NAME_EDEFAULT == null ? packageName != null : !PACKAGE_NAME_EDEFAULT.equals(packageName);
			case MapperPackage.CLASS_DETAILS__PROPERTY_CONVERSIONS:
				return propertyConversions != null && !propertyConversions.isEmpty();
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
		result.append(" (className: ");
		result.append(className);
		result.append(", packageName: ");
		result.append(packageName);
		result.append(')');
		return result.toString();
	}

	/**
	 * @return class name preceded by the package name and '.', if it is nonempty
	 */
	public String getQualifiedClassName()
	{
		String qName = getClassName();
		if ((getPackageName() != null) && (!getPackageName().equals("")))
			qName = getPackageName() + "." + getClassName();
		return qName;
	}

} //ClassDetailsImpl
