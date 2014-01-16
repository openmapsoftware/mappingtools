/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.MapperException;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Assoc End Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.AssocEndMapping#getMappedRole <em>Mapped Role</em>}</li>
 *   <li>{@link com.openMap1.mapper.AssocEndMapping#getObjectToAssociationPath <em>Object To Association Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.AssocEndMapping#getAssociationToObjectPath <em>Association To Object Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.AssocEndMapping#isRequiredForObject <em>Required For Object</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getAssocEndMapping()
 * @model
 * @generated
 */
public interface AssocEndMapping extends Mapping {
	
	/**
	 * Returns the value of the '<em><b>Mapped Role</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name of the association end (role) leading to 
	 * the object at this end of the association
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapped Role</em>' attribute.
	 * @see #setMappedRole(String)
	 * @see com.openMap1.mapper.MapperPackage#getAssocEndMapping_MappedRole()
	 * @model
	 * @generated
	 */
	String getMappedRole();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.AssocEndMapping#getMappedRole <em>Mapped Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapped Role</em>' attribute.
	 * @see #getMappedRole()
	 * @generated
	 */
	void setMappedRole(String value);

	/**
	 * Returns the value of the '<em><b>Object To Association Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * XPath from the node representing an object (instance) at thisend of the association 
	 * to the node representing 
	 * this association.
	 * </p>
	 * <p>
	 * Need not be supplied if that path is the shortest possible path between those two nodes -
	 * which it usually is.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Object To Association Path</em>' attribute.
	 * @see #setObjectToAssociationPath(String)
	 * @see com.openMap1.mapper.MapperPackage#getAssocEndMapping_ObjectToAssociationPath()
	 * @model
	 * @generated
	 */
	String getObjectToAssociationPath();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.AssocEndMapping#getObjectToAssociationPath <em>Object To Association Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Object To Association Path</em>' attribute.
	 * @see #getObjectToAssociationPath()
	 * @generated
	 */
	void setObjectToAssociationPath(String value);

	/**
	 * Returns the value of the '<em><b>Association To Object Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * XPath from  the node representing 
	 * this association to the node representing the object 
	 * (instance) at this end of the association 
	 * </p>
	 * <p>
	 * Need not be supplied if that path is the shortest possible path between those two nodes -
	 * which it usually is.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Association To Object Path</em>' attribute.
	 * @see #setAssociationToObjectPath(String)
	 * @see com.openMap1.mapper.MapperPackage#getAssocEndMapping_AssociationToObjectPath()
	 * @model
	 * @generated
	 */
	String getAssociationToObjectPath();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.AssocEndMapping#getAssociationToObjectPath <em>Association To Object Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Association To Object Path</em>' attribute.
	 * @see #getAssociationToObjectPath()
	 * @generated
	 */
	void setAssociationToObjectPath(String value);

	/**
	 * Returns the value of the '<em><b>Required For Object</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * True if: only object having this association are represented in the document
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required For Object</em>' attribute.
	 * @see #setRequiredForObject(boolean)
	 * @see com.openMap1.mapper.MapperPackage#getAssocEndMapping_RequiredForObject()
	 * @model
	 * @generated
	 */
	boolean isRequiredForObject();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.AssocEndMapping#isRequiredForObject <em>Required For Object</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required For Object</em>' attribute.
	 * @see #isRequiredForObject()
	 * @generated
	 */
	void setRequiredForObject(boolean value);
	
	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the class at the other end of the association has
	 * an association with this role name, leading to objects of  this end's class,
	 * taking account of inheritance at both ends.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean classHasRoleToClass(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that there exists an object mapping for this class and
	 * subset, somewhere iin this mapping set.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean objectMappingExists(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the XPath from the node representing the object at this end
	 * of the association, to the node representing the association, is a valid XPth in this structure.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean objectToAssociationPathIsValid(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation checks that the XPaths traversed from the object at the other end of the association,
	 * to the node representing the association and then on to the node representing the
	 * object at this end, are consistent with the min and max cardinalities of the association.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean PathMatchesCardinality(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the XPath from  the node representing the association, 
	 * to the node representing the object at this end
	 * of the association, is a valid XPth in this structure.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean AssociationToObjectPathIsValid(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * validation check (if there are no cross conditions)
	 * that the  path from the association end node to the object node
	 * leads to a unique node
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean objectIsUniqueFromAssociation(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * Xpth from the object mapping of the end class to this node.
	 * If a path has not been provided, calculate the default shortest path.
	 * @return
	 */
	public Xpth getObjectToAssociationXPath() throws MapperException;
	
	
	/**
	 * Xpth from this node to the object mapping of the end class.
	 * If a path has not been provided, calculate the default shortest path.
	 * @return
	 */
	public Xpth getAssociationToObjectXPath() throws MapperException;
	
	/**
	 * check a valid path without writing diagnostics
	 * @param isUnique
	 * @return
	 */
	public boolean validObjectToAssociationPath(boolean isUnique);
	
	/**
	 * end = 1 or 2 is now calculated automatically using the association 
	 * from the owning AssocMapping
	 * @return
	 */
	public int getEnd();
	
	/**
	 * @return the association end mapping for the other end of this association
	 */
	public AssocEndMapping otherEndMapping();

} // AssocEndMapping
