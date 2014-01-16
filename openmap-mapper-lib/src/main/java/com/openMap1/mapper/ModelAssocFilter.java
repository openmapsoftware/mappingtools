/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EReference;

import com.openMap1.mapper.ModelFilter;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Assoc Filter</b></em>'.
 * 
 * This is a model-based filter on the containing ObjMapping, 
 * saying that it only represents objects which have a certain association
 * in the class model, to some other object represented in  the document.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ModelAssocFilter#getRoleName <em>Role Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.ModelAssocFilter#getOtherClassName <em>Other Class Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.ModelAssocFilter#getOtherPackageName <em>Other Package Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.ModelAssocFilter#getOtherSubset <em>Other Subset</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getModelAssocFilter()
 * @model
 * @generated
 */
public interface ModelAssocFilter extends ModelFilter {
	/**
	 * Returns the value of the '<em><b>Role Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Role Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Role Name</em>' attribute.
	 * @see #setRoleName(String)
	 * @see com.openMap1.mapper.MapperPackage#getModelAssocFilter_RoleName()
	 * @model
	 * @generated
	 */
	String getRoleName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ModelAssocFilter#getRoleName <em>Role Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * The association role name used to navigate from the object at the 
	 * other end of the association to this object.
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Role Name</em>' attribute.
	 * @see #getRoleName()
	 * @generated
	 */
	void setRoleName(String value);

	/**
	 * Returns the value of the '<em><b>Other Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The class of the object at the other end of the association
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Other Class Name</em>' attribute.
	 * @see #setOtherClassName(String)
	 * @see com.openMap1.mapper.MapperPackage#getModelAssocFilter_OtherClassName()
	 * @model
	 * @generated
	 */
	String getOtherClassName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ModelAssocFilter#getOtherClassName <em>Other Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Other Class Name</em>' attribute.
	 * @see #getOtherClassName()
	 * @generated
	 */
	void setOtherClassName(String value);

	/**
	 * Returns the value of the '<em><b>Other Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Other Package Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Other Package Name</em>' attribute.
	 * @see #setOtherPackageName(String)
	 * @see com.openMap1.mapper.MapperPackage#getModelAssocFilter_OtherPackageName()
	 * @model
	 * @generated
	 */
	String getOtherPackageName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ModelAssocFilter#getOtherPackageName <em>Other Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Other Package Name</em>' attribute.
	 * @see #getOtherPackageName()
	 * @generated
	 */
	void setOtherPackageName(String value);

	/**
	 * Returns the value of the '<em><b>Other Subset</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The subset of the class of the object at the other end of
	 * the association
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Other Subset</em>' attribute.
	 * @see #setOtherSubset(String)
	 * @see com.openMap1.mapper.MapperPackage#getModelAssocFilter_OtherSubset()
	 * @model
	 * @generated
	 */
	String getOtherSubset();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ModelAssocFilter#getOtherSubset <em>Other Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Other Subset</em>' attribute.
	 * @see #getOtherSubset()
	 * @generated
	 */
	void setOtherSubset(String value);

	/**
	 * <!-- begin-user-doc -->
	 * check that the association used as a filter exists in the class model;
	 * but only check it if the other end object has an object mapping and its class exists.
	 * Then, while the user can provide both role names, the only one I check is the role
	 * from the other end class which gets to this one. 
	 * So the name of this method is wrong.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean classHasRoleFromOtherClass(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that an object mapping for the class and subset at the other
	 * end of the association exists in the set of mappings.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean otherObjectMappingExists(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * get the association from the other end class which points to the object
	 * which this is a filter on.
	 * Note this method checks the association role name, not its destination class
	 * @return
	 */
	public EReference getModelAssociation();
	
	/**
	 * the association name, automatically calculated from the role names
	 * @return
	 */
	public String getAssocName();
	
	/**
	 * the end 1 or 2, , automatically calculated from the role names
	 */
	public int getOtherEnd();
	
	/**
	 * the opposite end role name
	 */
	public String getOtherRoleName();

} // ModelAssocFilter
