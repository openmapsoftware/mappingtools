/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.ConversionArgument;
import com.openMap1.mapper.ConversionSense;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.PropertyConversion;

import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Conversion Argument</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ConversionArgumentImpl#getPropertyName <em>Property Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConversionArgumentImpl extends EObjectImpl implements ConversionArgument {
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConversionArgumentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.CONVERSION_ARGUMENT;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.CONVERSION_ARGUMENT__PROPERTY_NAME, oldPropertyName, propertyName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * For 'out' conversions only, if the class exists (checked elsewhere)
	 * check that the class has the property
	 * <!-- end-user-doc -->
	 */
	public boolean classHasProperty(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean propertyExists = true;
		String className = "";
		String packageName = "";
		EObject pc = eContainer();
		EObject cd = pc.eContainer();
		if (cd instanceof ClassDetails) 
		{
			className = ((ClassDetails)cd).getClassName();
			packageName = ((ClassDetails)cd).getPackageName();
		}
		if ((pc instanceof PropertyConversion)
				&& (((PropertyConversion)pc).getSense() == ConversionSense.OUT) 
				&& (ModelUtil.isInClassModel(className, packageName, this)))			
		{
			propertyExists = ModelUtil.hasProperty(className, packageName,getPropertyName(), this);
		}
		if (!propertyExists) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.CONVERSION_ARGUMENT__CLASS_HAS_PROPERTY,
						 ("Class '" + className + "' has no property '" + getPropertyName() + "'"),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * For 'in' conversions only, provided that the class is in the model (checked elsewhere)
	 * check that there is a property mapping for the property and subset.
	 * Warning only.
	 * <!-- end-user-doc -->
	 */
	public boolean propertyMappingExists(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean pmExists = true;
		String className = "";
		String packageName = "";
		ClassSet cs = null;
		EObject pc = eContainer();
		EObject cd = pc.eContainer();
		if (cd instanceof ClassDetails)
		{
			className = ((ClassDetails)cd).getClassName();
			packageName = ((ClassDetails)cd).getPackageName();
		}
		if ((pc instanceof PropertyConversion)
				&& (((PropertyConversion)pc).getSense() == ConversionSense.IN) 
				&& (ModelUtil.isInClassModel(className, packageName, this)))			
		{
			pmExists = false;
			PropertyConversion pCon = (PropertyConversion)pc;
			try {cs = new ClassSet(className,packageName,pCon.getSubset());}
			catch (MapperException ex) {} // arguments of constructor will not be null
			// iterate over all property mappings (expensive??)
			for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(ModelUtil.getModelRoot(this), 
					MapperPackage.Literals.PROP_MAPPING).iterator();it.hasNext();)
			{
				PropMapping pm = (PropMapping)it.next();
				if ((pm.getMappedClass().equals(className)) 
						&& (pm.getMappedPackage().equals(packageName)) 
						&& (pm.getSubset().equals(pCon.getSubset())) 
						&& (pm.getMappedProperty().equals(getPropertyName()))) 
					pmExists = true;
			}
		}
		if (!pmExists) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.WARNING,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.CONVERSION_ARGUMENT__PROPERTY_MAPPING_EXISTS,
						 ("There is no property mapping for class " + cs.stringForm() + ", property '" + getPropertyName() + "'"),
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
			case MapperPackage.CONVERSION_ARGUMENT__PROPERTY_NAME:
				return getPropertyName();
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
			case MapperPackage.CONVERSION_ARGUMENT__PROPERTY_NAME:
				setPropertyName((String)newValue);
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
			case MapperPackage.CONVERSION_ARGUMENT__PROPERTY_NAME:
				setPropertyName(PROPERTY_NAME_EDEFAULT);
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
			case MapperPackage.CONVERSION_ARGUMENT__PROPERTY_NAME:
				return PROPERTY_NAME_EDEFAULT == null ? propertyName != null : !PROPERTY_NAME_EDEFAULT.equals(propertyName);
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
		result.append(')');
		return result.toString();
	}

} //ConversionArgumentImpl
