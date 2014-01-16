/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.List;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.core.ClassSet;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node Mapping Set</b></em>'.
 * 
 * A NodeMappingSet is used to group together the mappings on one node
 * of the structure. It has no other meaning.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.NodeMappingSet#getObjectMappings <em>Object Mappings</em>}</li>
 *   <li>{@link com.openMap1.mapper.NodeMappingSet#getPropertyMappings <em>Property Mappings</em>}</li>
 *   <li>{@link com.openMap1.mapper.NodeMappingSet#getAssociationMappings <em>Association Mappings</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getNodeMappingSet()
 * @model
 * @generated
 */
public interface NodeMappingSet extends EObject {
	/**
	 * Returns the value of the '<em><b>Object Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ObjMapping}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Object Mappings</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Object Mappings</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getNodeMappingSet_ObjectMappings()
	 * @model containment="true"
	 * @generated
	 */
	EList<ObjMapping> getObjectMappings();

	/**
	 * Returns the value of the '<em><b>Property Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.PropMapping}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Property Mappings</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property Mappings</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getNodeMappingSet_PropertyMappings()
	 * @model containment="true"
	 * @generated
	 */
	EList<PropMapping> getPropertyMappings();

	/**
	 * Returns the value of the '<em><b>Association Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.AssocMapping}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Association Mappings</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Association Mappings</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getNodeMappingSet_AssociationMappings()
	 * @model containment="true"
	 * @generated
	 */
	EList<AssocMapping> getAssociationMappings();
	
	/**
	 * 
	 * @return the total number of mappings of all types in this mapping set
	 */
	public int countAllMappings();
	
	/**
	 * 
	 * @return a list of all object mappings which are independent on this node - 
	 * i.e not at the dependent end of some associaton mapping to a class/subset which 
	 * is also represented on this node
	 */
	public List<ObjMapping> independentObjectMappings();
	
	
    /**
     * true if a (class.subset) is represented on this node
     * @param cSet
     * @return
     */
	public boolean classSetIsRepresented(ClassSet cSet);


} // NodeMappingSet
