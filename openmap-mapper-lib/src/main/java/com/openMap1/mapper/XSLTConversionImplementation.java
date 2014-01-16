/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import com.openMap1.mapper.ConversionImplementation;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XSLT Conversion Implementation</b></em>'.
 * 
 * Declares that an XSLT implementation of the containing
 *  property conversion function exists.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.XSLTConversionImplementation#getTemplateName <em>Template Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.XSLTConversionImplementation#getTemplateFileURI <em>Template File URI</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getXSLTConversionImplementation()
 * @model
 * @generated
 */
public interface XSLTConversionImplementation extends ConversionImplementation {
	/**
	 * Returns the value of the '<em><b>Template Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Name of the XSLT template which implements the property conversion.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Template Name</em>' attribute.
	 * @see #setTemplateName(String)
	 * @see com.openMap1.mapper.MapperPackage#getXSLTConversionImplementation_TemplateName()
	 * @model
	 * @generated
	 */
	String getTemplateName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.XSLTConversionImplementation#getTemplateName <em>Template Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Template Name</em>' attribute.
	 * @see #getTemplateName()
	 * @generated
	 */
	void setTemplateName(String value);

	/**
	 * Returns the value of the '<em><b>Template File URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Location and name of the file containing the XSLT template
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Template File URI</em>' attribute.
	 * @see #setTemplateFileURI(String)
	 * @see com.openMap1.mapper.MapperPackage#getXSLTConversionImplementation_TemplateFileURI()
	 * @model
	 * @generated
	 */
	String getTemplateFileURI();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.XSLTConversionImplementation#getTemplateFileURI <em>Template File URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Template File URI</em>' attribute.
	 * @see #getTemplateFileURI()
	 * @generated
	 */
	void setTemplateFileURI(String value);

} // XSLTConversionImplementation
