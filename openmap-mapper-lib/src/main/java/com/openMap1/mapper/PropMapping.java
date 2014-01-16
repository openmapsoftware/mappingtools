/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;

import com.openMap1.mapper.LocalPropertyConversion;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.MapperException;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Prop Mapping</b></em>'.
 * 
 * A property mapping states that this node in the structure defines the value of a
 * property of some object in the object model.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.PropMapping#getMappedProperty <em>Mapped Property</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropMapping#getPropertyType <em>Property Type</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropMapping#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropMapping#getObjectToPropertyPath <em>Object To Property Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropMapping#getLocalPropertyConversion <em>Local Property Conversion</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getPropMapping()
 * @model
 * @generated
 */
public interface PropMapping extends Mapping {

	/**
	 * Returns the value of the '<em><b>Mapped Property</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name of the property in the object model, whose value is given on this node of the structure
	 * (if the contents of the node give the value directly); or, if the contents of the node are
	 * input to some property conversion function to give the value of the property,
	 * this is the name of a 'pseudo-property' which is an argument of the conversion 
	 * function.
	 * Pseudo-property names should not clash with real property names.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped Property</em>' attribute.
	 * @see #setMappedProperty(String)
	 * @see com.openMap1.mapper.MapperPackage#getPropMapping_MappedProperty()
	 * @model
	 * @generated
	 */
	String getMappedProperty();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropMapping#getMappedProperty <em>Mapped Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped Property</em>' attribute.
	 * @see #getMappedProperty()
	 * @generated
	 */
	void setMappedProperty(String value);

	/**
	 * Returns the value of the '<em><b>Property Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>The data type of the mapped property or pseudo-property. Possibly not used?
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property Type</em>' attribute.
	 * @see #setPropertyType(String)
	 * @see com.openMap1.mapper.MapperPackage#getPropMapping_PropertyType()
	 * @model
	 * @generated
	 */
	String getPropertyType();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropMapping#getPropertyType <em>Property Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Property Type</em>' attribute.
	 * @see #getPropertyType()
	 * @generated
	 */
	void setPropertyType(String value);

	/**
	 * Returns the value of the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * This is the value the property in the object model is assumed to take, if the 
	 * node is missing from the structure. 
	 * </p>
	 * <p>
	 * Note that a ‘Default’ property value in the mapping has no effect on writing XML 
	 * – the XML writer will write any supplied value of a property from an object model 
	 * instance to the XML, whether or not it is the default value. If the object model 
	 * instance has no value for the property, the XML writer will not write a default 
	 * value to the XML. The default attribute in a property mapping means
	 *  “if it is missing from the XML, assume the default value in the object model 
	 *  instance” – not the other way round. Object model instances are not expected 
	 *  to have missing property values.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default Value</em>' attribute.
	 * @see #setDefaultValue(String)
	 * @see com.openMap1.mapper.MapperPackage#getPropMapping_DefaultValue()
	 * @model
	 * @generated
	 */
	String getDefaultValue();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropMapping#getDefaultValue <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default Value</em>' attribute.
	 * @see #getDefaultValue()
	 * @generated
	 */
	void setDefaultValue(String value);

	/**
	 * Returns the value of the '<em><b>Object To Property Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * XPath from the node representing an object (instance) to the node representing 
	 * this property value of that instance.
	 * </p>
	 * <p>
	 * Need not be supplied if that path is the shortest possible path between those two nodes -
	 * which it usually is.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Object To Property Path</em>' attribute.
	 * @see #setObjectToPropertyPath(String)
	 * @see com.openMap1.mapper.MapperPackage#getPropMapping_ObjectToPropertyPath()
	 * @model
	 * @generated
	 */
	String getObjectToPropertyPath();
	

	/**
	 * Xpth form of the path from the owning object to this property
	 * @return
	 * @throws MapperException
	 */
	public Xpth getObjectToPropertyXPath() throws MapperException;

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropMapping#getObjectToPropertyPath <em>Object To Property Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Object To Property Path</em>' attribute.
	 * @see #getObjectToPropertyPath()
	 * @generated
	 */
	void setObjectToPropertyPath(String value);

	/**
	 * Returns the value of the '<em><b>Local Property Conversion</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Local Property Conversion</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Local Property Conversion</em>' containment reference.
	 * @see #setLocalPropertyConversion(LocalPropertyConversion)
	 * @see com.openMap1.mapper.MapperPackage#getPropMapping_LocalPropertyConversion()
	 * @model containment="true"
	 * @generated
	 */
	LocalPropertyConversion getLocalPropertyConversion();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropMapping#getLocalPropertyConversion <em>Local Property Conversion</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Local Property Conversion</em>' containment reference.
	 * @see #getLocalPropertyConversion()
	 * @generated
	 */
	void setLocalPropertyConversion(LocalPropertyConversion value);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the mapped class has the property - or, if this 
	 * is a pseudo-property, that it has been declared in the property
	 * conversions in the GlobalMappingParameters object.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean classHasProperty(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that there is an object mapping for the same
	 * class and subset, somewhere in this mapping set.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean objectMappingExists(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the XPath from the object node to the property node,
	 * if supplied, is a valid XPath in this structure.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean objectToPropertyPathIsValid(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the XPath from the object node to the property node, 
	 * whether the default shortest path or otherwise, leads to a single node
	 * so as to give a unique vaue for this property
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean propertyIsUniqueFromObjectNode(DiagnosticChain diagnostics, Map<?, ?> context);

} // PropMapping
