/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.structures.StructureDefinition;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Global Mapping Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.GlobalMappingParameters#getMappingClass <em>Mapping Class</em>}</li>
 *   <li>{@link com.openMap1.mapper.GlobalMappingParameters#getWrapperClass <em>Wrapper Class</em>}</li>
 *   <li>{@link com.openMap1.mapper.GlobalMappingParameters#getNameSpaces <em>Name Spaces</em>}</li>
 *   <li>{@link com.openMap1.mapper.GlobalMappingParameters#getClassDetails <em>Class Details</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getGlobalMappingParameters()
 * @model
 * @generated
 */
public interface GlobalMappingParameters extends EObject {
	
	/**
	 * Returns the value of the '<em><b>Mapping Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mapping Class</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapping Class</em>' attribute.
	 * @see #setMappingClass(String)
	 * @see com.openMap1.mapper.MapperPackage#getGlobalMappingParameters_MappingClass()
	 * @model
	 * @generated
	 */
	String getMappingClass();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.GlobalMappingParameters#getMappingClass <em>Mapping Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapping Class</em>' attribute.
	 * @see #getMappingClass()
	 * @generated
	 */
	void setMappingClass(String value);

	/**
	 * Returns the value of the '<em><b>Wrapper Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Wrapper Class</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wrapper Class</em>' attribute.
	 * @see #setWrapperClass(String)
	 * @see com.openMap1.mapper.MapperPackage#getGlobalMappingParameters_WrapperClass()
	 * @model
	 * @generated
	 */
	String getWrapperClass();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.GlobalMappingParameters#getWrapperClass <em>Wrapper Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wrapper Class</em>' attribute.
	 * @see #getWrapperClass()
	 * @generated
	 */
	void setWrapperClass(String value);

	/**
	 * Returns the value of the '<em><b>Name Spaces</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.Namespace}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * we need to define the prefixes whereby MDL will refer to the various 
	 * namespaces in the values of MDL attributes, such as the XPaths. 
	 * This is done by Namespace objects under the GlobalMappingParameters 
	 * object. Each such object defines a Namespace by its prefix and its URI.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name Spaces</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getGlobalMappingParameters_NameSpaces()
	 * @model containment="true"
	 * @generated
	 */
	EList<Namespace> getNameSpaces();

	/**
	 * Returns the value of the '<em><b>Class Details</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ClassDetails}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Gives details of any class in the class model which are concerned with this mapping
	 * set rather than any other. Currently these consists of the property conversions
	 * needed for properties of that class.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class Details</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getGlobalMappingParameters_ClassDetails()
	 * @model containment="true"
	 * @generated
	 */
	EList<ClassDetails> getClassDetails();
	
	/**
	 * Add the namespaces from a structure definition
	 * @param structureDef
	 */
	public void addNamespaces(StructureDefinition structureDef);
	
	/**
	 * @return a clone of this set of global mapping parameters, 
	 * copying over only the namespaces, not the class details.
	 * (used to put on a newly created imported mapping set)
	 */
	public GlobalMappingParameters cloneNamespacesOnly();

} // GlobalMappingParameters
