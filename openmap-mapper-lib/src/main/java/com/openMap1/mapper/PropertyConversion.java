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

import com.openMap1.mapper.ConversionArgument;
import com.openMap1.mapper.ConversionImplementation;
import com.openMap1.mapper.ConversionSense;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Conversion</b></em>'.
 * 
 * <p>
 * A property conversion is a declaration that some property is represented in 
 * the XML or database structure by a different set of values from those in the central object model,
 * so some conversion methods are necessary.
 * The class of the properties involved is defined by the containing ClassDetails object.
* </p>
  * 
* <p>
  * There are conversion 'in' from the XML to the object model and 'out' from
 * the class model to the XML.
* </p>
  * 
* <p>
  * The property conversion defines where the result goes. It has child ConversionArguments
 * which define the arguments to the function and ConversionImplementations which define
 * what implementations of the function are available.
* </p>
  * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.PropertyConversion#getSubset <em>Subset</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropertyConversion#getResultSlot <em>Result Slot</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropertyConversion#getSense <em>Sense</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropertyConversion#getConversionImplementations <em>Conversion Implementations</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropertyConversion#getConversionArguments <em>Conversion Arguments</em>}</li>
 *   <li>{@link com.openMap1.mapper.PropertyConversion#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getPropertyConversion()
 * @model
 * @generated
 */
public interface PropertyConversion extends EObject {

	/**
	 * Returns the value of the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * There may be different property conversions for different subset mappings of the class. 
	 * This defines the subset.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subset</em>' attribute.
	 * @see #setSubset(String)
	 * @see com.openMap1.mapper.MapperPackage#getPropertyConversion_Subset()
	 * @model
	 * @generated
	 */
	String getSubset();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropertyConversion#getSubset <em>Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subset</em>' attribute.
	 * @see #getSubset()
	 * @generated
	 */
	void setSubset(String value);

	/**
	 * Returns the value of the '<em><b>Result Slot</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * For 'in' conversions, the result slot is a property of the class.
	 * For 'out' conversions, it is a pseudo-property which must be mapped.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result Slot</em>' attribute.
	 * @see #setResultSlot(String)
	 * @see com.openMap1.mapper.MapperPackage#getPropertyConversion_ResultSlot()
	 * @model
	 * @generated
	 */
	String getResultSlot();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropertyConversion#getResultSlot <em>Result Slot</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Result Slot</em>' attribute.
	 * @see #getResultSlot()
	 * @generated
	 */
	void setResultSlot(String value);

	/**
	 * Returns the value of the '<em><b>Sense</b></em>' attribute.
	 * The literals are from the enumeration {@link com.openMap1.mapper.ConversionSense}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The sense of the conversion is either 'in' or 'out'
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sense</em>' attribute.
	 * @see com.openMap1.mapper.ConversionSense
	 * @see #setSense(ConversionSense)
	 * @see com.openMap1.mapper.MapperPackage#getPropertyConversion_Sense()
	 * @model
	 * @generated
	 */
	ConversionSense getSense();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropertyConversion#getSense <em>Sense</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sense</em>' attribute.
	 * @see com.openMap1.mapper.ConversionSense
	 * @see #getSense()
	 * @generated
	 */
	void setSense(ConversionSense value);

	/**
	 * Returns the value of the '<em><b>Conversion Implementations</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ConversionImplementation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * All available implementations of the conversion function - 
	 * currently either in Java or XSLT
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conversion Implementations</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getPropertyConversion_ConversionImplementations()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConversionImplementation> getConversionImplementations();

	/**
	 * Returns the value of the '<em><b>Conversion Arguments</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ConversionArgument}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The arguments of the conversion function. 
	 * For an 'in' conversion, these arguments define the pseudo-properties, which 
	 * must be mapped for the class and subset.
	 * For an 'out' conversion, the arguments must be properties of the class.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conversion Arguments</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getPropertyConversion_ConversionArguments()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConversionArgument> getConversionArguments();

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see com.openMap1.mapper.MapperPackage#getPropertyConversion_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.PropertyConversion#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that for an 'in' conversion, the result is a property
	 * of the class in the model.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean classHasResultProperty(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the property conversion has some implementation.
	 * If not, it is a warning rather than an error.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean hasImplementation(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * <!-- begin-user-doc -->
	 * For any 'in' property conversion, check that there is no direct mapping for the property
	 * and subset.
	 * (this validation really belongs on the child PropertyConversion)
	 * <!-- end-user-doc -->
	 */
	public boolean convertedPropertyIsNotRepresentedDirectly();

} // PropertyConversion
