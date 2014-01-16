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

import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.MultiWay;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.ClassSet;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapping</b></em>'.
 * 
 * This is the superclass of all the mapping classes. It contains
 * all the features that they have in common, and some which are shared by
 * all of them except AssocMapping.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.Mapping#getMappedClass <em>Mapped Class</em>}</li>
 *   <li>{@link com.openMap1.mapper.Mapping#getMappedPackage <em>Mapped Package</em>}</li>
 *   <li>{@link com.openMap1.mapper.Mapping#getSubset <em>Subset</em>}</li>
 *   <li>{@link com.openMap1.mapper.Mapping#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.Mapping#getMappingConditions <em>Mapping Conditions</em>}</li>
 *   <li>{@link com.openMap1.mapper.Mapping#getMultiWay <em>Multi Way</em>}</li>
 *   <li>{@link com.openMap1.mapper.Mapping#isBreakPoint <em>Break Point</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getMapping()
 * @model abstract="true"
 * @generated
 */
public interface Mapping extends EObject {
	/**
	 * Returns the value of the '<em><b>Mapped Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Name of the mapped class
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped Class</em>' attribute.
	 * @see #setMappedClass(String)
	 * @see com.openMap1.mapper.MapperPackage#getMapping_MappedClass()
	 * @model
	 * @generated
	 */
	String getMappedClass();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.Mapping#getMappedClass <em>Mapped Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped Class</em>' attribute.
	 * @see #getMappedClass()
	 * @generated
	 */
	void setMappedClass(String value);

	/**
	 * Returns the value of the '<em><b>Mapped Package</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mapped Package</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped Package</em>' attribute.
	 * @see #setMappedPackage(String)
	 * @see com.openMap1.mapper.MapperPackage#getMapping_MappedPackage()
	 * @model
	 * @generated
	 */
	String getMappedPackage();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.Mapping#getMappedPackage <em>Mapped Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped Package</em>' attribute.
	 * @see #getMappedPackage()
	 * @generated
	 */
	void setMappedPackage(String value);

	/**
	 * Returns the value of the '<em><b>Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Name of the subset of the class involved in this mapping. 
	 * Can be the default '' if there is only one object mapping for the class.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subset</em>' attribute.
	 * @see #setSubset(String)
	 * @see com.openMap1.mapper.MapperPackage#getMapping_Subset()
	 * @model
	 * @generated
	 */
	String getSubset();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.Mapping#getSubset <em>Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subset</em>' attribute.
	 * @see #getSubset()
	 * @generated
	 */
	void setSubset(String value);

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
	 * @see com.openMap1.mapper.MapperPackage#getMapping_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.Mapping#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Mapping Conditions</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.MappingCondition}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * This mapping only applies 
	 * when the values on nodes of the structure satisfy all the conditions. 
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapping Conditions</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getMapping_MappingConditions()
	 * @model containment="true"
	 * @generated
	 */
	EList<MappingCondition> getMappingConditions();

	/**
	 * Returns the value of the '<em><b>Multi Way</b></em>' attribute.
	 * The literals are from the enumeration {@link com.openMap1.mapper.MultiWay}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Usually the default 'none'. May be 'choice' if there are several alternative
	 *  ways in which the XML defines the value of a property or the targets of
	 *  an association, or 'redundant' if the XML defines these things redundantly. 
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Multi Way</em>' attribute.
	 * @see com.openMap1.mapper.MultiWay
	 * @see #setMultiWay(MultiWay)
	 * @see com.openMap1.mapper.MapperPackage#getMapping_MultiWay()
	 * @model
	 * @generated
	 */
	MultiWay getMultiWay();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.Mapping#getMultiWay <em>Multi Way</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Multi Way</em>' attribute.
	 * @see com.openMap1.mapper.MultiWay
	 * @see #getMultiWay()
	 * @generated
	 */
	void setMultiWay(MultiWay value);
	
	/**
	 * Returns the value of the '<em><b>Break Point</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Break Point</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Break Point</em>' attribute.
	 * @see #setBreakPoint(boolean)
	 * @see com.openMap1.mapper.MapperPackage#getMapping_BreakPoint()
	 * @model
	 * @generated
	 */
	boolean isBreakPoint();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.Mapping#isBreakPoint <em>Break Point</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Break Point</em>' attribute.
	 * @see #isBreakPoint()
	 * @generated
	 */
	void setBreakPoint(boolean value);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the mapped class exists in the class model
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean mappedClassIsInClassModel(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * not in the genmodel and not visible to the mapper editor
	 * @return the absolute XPath from the root to the mapped node
	 */
	public Xpth getRootXPath() throws MapperException;
	
	/**
	 * @return the absolute XPath from the root to the mapped node, as a String
	 */
	public String getStringRootPath() throws MapperException;
	
	/**
	 * not in the genmodel and not visible to the mapper editor
	 * @return the (class, subset) of the mapping
	 * 
	 * Might have thrown an exception if classname or subset were null; but 
	 * as these both default to "", this is treated as very rare, 
	 * so a System.out message rather than an exception
	 */
	public ClassSet getClassSet(); 
	
	/**
	 * Two mappings (usually in different mapping sets) are equivalent if they 
	 * refer to the same thing in the Class model (eg the same class, the same property)
	 * and with the same subset.
	 * Two mappings in the same mapping set should never be equivalent
	 * @param m
	 * @return
	 */
	public boolean equivalentTo(Mapping m);
	

	/**
	 * The node which this mapping is attached to
	 * @return
	 */
	public NodeDef mappedNode();
	
	/**
	 * 
	 * @return a list of cross-conditions on this mapping, linking it to some other node
	 */
	public EList<CrossCondition> getCrossConditions();
	
	/**
	 * class name for use in label, with subset in brackets if nonempty
	 */
	public String labelClassName();
	
	/**
	 * @return class name preceded by the package name and '.', if the package name is non-empty
	 */
	public String getQualifiedClassName();
	
	/**
	 * details of this mapping, to be written out in the Comments column of the Mappings view
	 */
	public String getDetails();
	
	/**
	 * details of the child nodes of this mapping, to be written out in the details column of the Mappings view
	 */
	public String getChildDetails();
	
	

} // Mapping
