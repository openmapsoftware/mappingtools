/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.PropertyConversion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class Details</b></em>'.
 * 
 * Collects together any details of the class which pertain to this mapping set specifically.
 * 
 * Currently these include only the property conversions needed for these mappings.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ClassDetails#getClassName <em>Class Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.ClassDetails#getPackageName <em>Package Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.ClassDetails#getPropertyConversions <em>Property Conversions</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getClassDetails()
 * @model
 * @generated
 */
public interface ClassDetails extends EObject {
	/**
	 * Returns the value of the '<em><b>Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name of the class
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class Name</em>' attribute.
	 * @see #setClassName(String)
	 * @see com.openMap1.mapper.MapperPackage#getClassDetails_ClassName()
	 * @model
	 * @generated
	 */
	String getClassName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ClassDetails#getClassName <em>Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class Name</em>' attribute.
	 * @see #getClassName()
	 * @generated
	 */
	void setClassName(String value);

	/**
	 * Returns the value of the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Package Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Package Name</em>' attribute.
	 * @see #setPackageName(String)
	 * @see com.openMap1.mapper.MapperPackage#getClassDetails_PackageName()
	 * @model
	 * @generated
	 */
	String getPackageName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ClassDetails#getPackageName <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package Name</em>' attribute.
	 * @see #getPackageName()
	 * @generated
	 */
	void setPackageName(String value);

	/**
	 * Returns the value of the '<em><b>Property Conversions</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.PropertyConversion}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * All the property value conversions, both 'in' from the document representation to
	 * the class model representation, and in the other 'out' direction, needed for 
	 * this class in this mapping set
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property Conversions</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getClassDetails_PropertyConversions()
	 * @model containment="true"
	 * @generated
	 */
	EList<PropertyConversion> getPropertyConversions();

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the class exists in the class model
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean classIsInClassModel(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean onlyOneOutConversionPerPseudoPropertyAndSubset(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean onlyOneInConversionPerPropertyAndSubset(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean convertedPropertyIsNotRepresentedDirectly(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * @return class name preceded by the package name and '.', if it is nonempty
	 */
	public String getQualifiedClassName();
	

} // ClassDetails
