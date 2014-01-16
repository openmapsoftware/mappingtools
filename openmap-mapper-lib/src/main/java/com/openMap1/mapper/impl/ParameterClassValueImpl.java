/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ParameterClassValue;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameter Class Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ParameterClassValueImpl#getMappedClass <em>Mapped Class</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ParameterClassValueImpl#getMappedPackage <em>Mapped Package</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ParameterClassValueImpl#getSubset <em>Subset</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ParameterClassValueImpl#getParameterIndex <em>Parameter Index</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ParameterClassValueImpl extends EObjectImpl implements ParameterClassValue {
	/**
	 * The default value of the '{@link #getMappedClass() <em>Mapped Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default is "" to avoid exceptions making the ClassSet
	 * <!-- end-user-doc -->
	 * @see #getMappedClass()
	 * @ordered
	 */
	protected static final String MAPPED_CLASS_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getMappedClass() <em>Mapped Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedClass()
	 * @generated
	 * @ordered
	 */
	protected String mappedClass = MAPPED_CLASS_EDEFAULT;

	/**
	 * The default value of the '{@link #getMappedPackage() <em>Mapped Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedPackage()
	 * @ordered
	 */
	protected static final String MAPPED_PACKAGE_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getMappedPackage() <em>Mapped Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedPackage()
	 * @generated
	 * @ordered
	 */
	protected String mappedPackage = MAPPED_PACKAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSubset() <em>Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default subset is ""
	 * <!-- end-user-doc -->
	 * @see #getSubset()
	 * @ordered
	 */
	protected static final String SUBSET_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getSubset() <em>Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubset()
	 * @generated
	 * @ordered
	 */
	protected String subset = SUBSET_EDEFAULT;

	/**
	 * The default value of the '{@link #getParameterIndex() <em>Parameter Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterIndex()
	 * @generated
	 * @ordered
	 */
	protected static final int PARAMETER_INDEX_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getParameterIndex() <em>Parameter Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterIndex()
	 * @generated
	 * @ordered
	 */
	protected int parameterIndex = PARAMETER_INDEX_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterClassValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.PARAMETER_CLASS_VALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappedClass() {
		return mappedClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappedClass(String newMappedClass) {
		String oldMappedClass = mappedClass;
		mappedClass = newMappedClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_CLASS, oldMappedClass, mappedClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappedPackage() {
		return mappedPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappedPackage(String newMappedPackage) {
		String oldMappedPackage = mappedPackage;
		mappedPackage = newMappedPackage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_PACKAGE, oldMappedPackage, mappedPackage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSubset() {
		return subset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSubset(String newSubset) {
		String oldSubset = subset;
		subset = newSubset;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PARAMETER_CLASS_VALUE__SUBSET, oldSubset, subset));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getParameterIndex() {
		return parameterIndex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParameterIndex(int newParameterIndex) {
		int oldParameterIndex = parameterIndex;
		parameterIndex = newParameterIndex;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PARAMETER_CLASS_VALUE__PARAMETER_INDEX, oldParameterIndex, parameterIndex));
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the class exists in the class model
	 * <!-- end-user-doc -->
	 */
	public boolean mappedClassIsInClassModel(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean isInClassModel = true;
		String message = "";
		// only make the check if you can find the class model
		if (ModelUtil.getClassModelRoot(this) != null)
		{
			isInClassModel = false;
			EPackage thePackage = ModelUtil.getEPackage(getMappedPackage(), this);
			if (thePackage != null)
			{
				isInClassModel = (thePackage.getEClassifier(getMappedClass()) != null);
				if (!isInClassModel) message = ("Parameter class '" 
					+ getQualifiedClassName() + "' is not in the class model.");
			}
			else message = "Package '" + getMappedPackage() + "' is not in the class model.";
		}			
		if (!isInClassModel) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.PARAMETER_CLASS_VALUE__MAPPED_CLASS_IS_IN_CLASS_MODEL,
						 message,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that there is an object mapping for the class and subset on the element
	 * making the import
	 * <!-- end-user-doc -->
	 */
	public boolean mappingExistsForParameterClassValue(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean mappingExists = false;
		ElementDef importingElement = (ElementDef)this.eContainer().eContainer();
		NodeMappingSet nms = importingElement.getNodeMappingSet();
		if (nms != null) for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator(); it.hasNext();)
		{
			ObjMapping om = it.next();
			if (om.getMappedClass().equals(getMappedClass()) && 
				(om.getSubset().equals(getSubset()))) mappingExists = true;
		}
		if (!mappingExists) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.PARAMETER_CLASS_VALUE__MAPPING_EXISTS_FOR_PARAMETER_CLASS_VALUE,
						 "There is no object mapping for the class and subset on the importing element",
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
			case MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_CLASS:
				return getMappedClass();
			case MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_PACKAGE:
				return getMappedPackage();
			case MapperPackage.PARAMETER_CLASS_VALUE__SUBSET:
				return getSubset();
			case MapperPackage.PARAMETER_CLASS_VALUE__PARAMETER_INDEX:
				return new Integer(getParameterIndex());
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
			case MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_CLASS:
				setMappedClass((String)newValue);
				return;
			case MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_PACKAGE:
				setMappedPackage((String)newValue);
				return;
			case MapperPackage.PARAMETER_CLASS_VALUE__SUBSET:
				setSubset((String)newValue);
				return;
			case MapperPackage.PARAMETER_CLASS_VALUE__PARAMETER_INDEX:
				setParameterIndex(((Integer)newValue).intValue());
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
			case MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_CLASS:
				setMappedClass(MAPPED_CLASS_EDEFAULT);
				return;
			case MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_PACKAGE:
				setMappedPackage(MAPPED_PACKAGE_EDEFAULT);
				return;
			case MapperPackage.PARAMETER_CLASS_VALUE__SUBSET:
				setSubset(SUBSET_EDEFAULT);
				return;
			case MapperPackage.PARAMETER_CLASS_VALUE__PARAMETER_INDEX:
				setParameterIndex(PARAMETER_INDEX_EDEFAULT);
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
			case MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_CLASS:
				return MAPPED_CLASS_EDEFAULT == null ? mappedClass != null : !MAPPED_CLASS_EDEFAULT.equals(mappedClass);
			case MapperPackage.PARAMETER_CLASS_VALUE__MAPPED_PACKAGE:
				return MAPPED_PACKAGE_EDEFAULT == null ? mappedPackage != null : !MAPPED_PACKAGE_EDEFAULT.equals(mappedPackage);
			case MapperPackage.PARAMETER_CLASS_VALUE__SUBSET:
				return SUBSET_EDEFAULT == null ? subset != null : !SUBSET_EDEFAULT.equals(subset);
			case MapperPackage.PARAMETER_CLASS_VALUE__PARAMETER_INDEX:
				return parameterIndex != PARAMETER_INDEX_EDEFAULT;
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
		result.append(" (mappedClass: ");
		result.append(mappedClass);
		result.append(", mappedPackage: ");
		result.append(mappedPackage);
		result.append(", subset: ");
		result.append(subset);
		result.append(", parameterIndex: ");
		result.append(parameterIndex);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * the ClassSet of the class and subset - using the qualified class name
	 * @return
	 */
	public ClassSet getClassSet()
	{
		ClassSet cs = null;
		try{
			cs = new ClassSet(getQualifiedClassName(),getSubset());
		}
		catch (MapperException ex) {}
		return cs;
	}
	/**
	 * @return class name preceded by the package name and '.', if it is nonempty
	 */
	public String getQualifiedClassName()
	{
		String qName = getMappedClass();
		if ((getMappedPackage() != null) && (!getMappedPackage().equals("")))
			qName = getMappedPackage() + "." + getMappedClass();
		return qName;
	}

} //ParameterClassValueImpl
