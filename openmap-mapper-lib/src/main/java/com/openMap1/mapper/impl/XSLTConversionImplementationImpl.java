/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.XSLTConversionImplementation;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XSLT Conversion Implementation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.XSLTConversionImplementationImpl#getTemplateName <em>Template Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.XSLTConversionImplementationImpl#getTemplateFileURI <em>Template File URI</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XSLTConversionImplementationImpl extends ConversionImplementationImpl implements XSLTConversionImplementation {
	/**
	 * The default value of the '{@link #getTemplateName() <em>Template Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemplateName()
	 * @generated
	 * @ordered
	 */
	protected static final String TEMPLATE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTemplateName() <em>Template Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemplateName()
	 * @generated
	 * @ordered
	 */
	protected String templateName = TEMPLATE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getTemplateFileURI() <em>Template File URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemplateFileURI()
	 * @generated
	 * @ordered
	 */
	protected static final String TEMPLATE_FILE_URI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTemplateFileURI() <em>Template File URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemplateFileURI()
	 * @generated
	 * @ordered
	 */
	protected String templateFileURI = TEMPLATE_FILE_URI_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected XSLTConversionImplementationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.XSLT_CONVERSION_IMPLEMENTATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTemplateName(String newTemplateName) {
		String oldTemplateName = templateName;
		templateName = newTemplateName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_NAME, oldTemplateName, templateName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTemplateFileURI() {
		return templateFileURI;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTemplateFileURI(String newTemplateFileURI) {
		String oldTemplateFileURI = templateFileURI;
		templateFileURI = newTemplateFileURI;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_FILE_URI, oldTemplateFileURI, templateFileURI));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_NAME:
				return getTemplateName();
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_FILE_URI:
				return getTemplateFileURI();
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
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_NAME:
				setTemplateName((String)newValue);
				return;
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_FILE_URI:
				setTemplateFileURI((String)newValue);
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
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_NAME:
				setTemplateName(TEMPLATE_NAME_EDEFAULT);
				return;
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_FILE_URI:
				setTemplateFileURI(TEMPLATE_FILE_URI_EDEFAULT);
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
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_NAME:
				return TEMPLATE_NAME_EDEFAULT == null ? templateName != null : !TEMPLATE_NAME_EDEFAULT.equals(templateName);
			case MapperPackage.XSLT_CONVERSION_IMPLEMENTATION__TEMPLATE_FILE_URI:
				return TEMPLATE_FILE_URI_EDEFAULT == null ? templateFileURI != null : !TEMPLATE_FILE_URI_EDEFAULT.equals(templateFileURI);
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
		result.append(" (templateName: ");
		result.append(templateName);
		result.append(", templateFileURI: ");
		result.append(templateFileURI);
		result.append(')');
		return result.toString();
	}

} //XSLTConversionImplementationImpl
