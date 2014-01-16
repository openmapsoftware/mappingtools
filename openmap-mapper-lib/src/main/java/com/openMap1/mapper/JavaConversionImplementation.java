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
 * A representation of the model object '<em><b>Java Conversion Implementation</b></em>'.
 * 
 * Identifies where to find an implementation of a property conversion fucntion in Java
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.JavaConversionImplementation#getClassName <em>Class Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.JavaConversionImplementation#getMethodName <em>Method Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.JavaConversionImplementation#getPackageName <em>Package Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getJavaConversionImplementation()
 * @model
 * @generated
 */
public interface JavaConversionImplementation extends ConversionImplementation {
	/**
	 * Returns the value of the '<em><b>Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name of the class where the conversion method is found
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class Name</em>' attribute.
	 * @see #setClassName(String)
	 * @see com.openMap1.mapper.MapperPackage#getJavaConversionImplementation_ClassName()
	 * @model
	 * @generated
	 */
	String getClassName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.JavaConversionImplementation#getClassName <em>Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class Name</em>' attribute.
	 * @see #getClassName()
	 * @generated
	 */
	void setClassName(String value);

	/**
	 * Returns the value of the '<em><b>Method Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name of the conversion method
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Method Name</em>' attribute.
	 * @see #setMethodName(String)
	 * @see com.openMap1.mapper.MapperPackage#getJavaConversionImplementation_MethodName()
	 * @model
	 * @generated
	 */
	String getMethodName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.JavaConversionImplementation#getMethodName <em>Method Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Method Name</em>' attribute.
	 * @see #getMethodName()
	 * @generated
	 */
	void setMethodName(String value);

	/**
	 * Returns the value of the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The package contaiining the class containing the method
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Package Name</em>' attribute.
	 * @see #setPackageName(String)
	 * @see com.openMap1.mapper.MapperPackage#getJavaConversionImplementation_PackageName()
	 * @model
	 * @generated
	 */
	String getPackageName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.JavaConversionImplementation#getPackageName <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package Name</em>' attribute.
	 * @see #getPackageName()
	 * @generated
	 */
	void setPackageName(String value);

} // JavaConversionImplementation
