/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.Annotations;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.writer.TreeElement;
import com.openMap1.mapper.core.Xpth;
/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node</b></em>'.
 * 
 * Structure trees are built from Nodes. This  is the superclass 
 * of the two types of node (Element and Attribute) used in XML documents.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.NodeDef#getName <em>Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.NodeDef#getType <em>Type</em>}</li>
 *   <li>{@link com.openMap1.mapper.NodeDef#getMinMultiplicity <em>Min Multiplicity</em>}</li>
 *   <li>{@link com.openMap1.mapper.NodeDef#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.NodeDef#getFixedValue <em>Fixed Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.NodeDef#getNodeMappingSet <em>Node Mapping Set</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getNodeDef()
 * @model abstract="true"
 */
public interface NodeDef extends EObject, TreeElement  {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Element or attribute name- as defined for instance in XML schema
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see com.openMap1.mapper.MapperPackage#getNodeDef_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.NodeDef#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Type of the element or attribute, as defined for instance in XML schema
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see com.openMap1.mapper.MapperPackage#getNodeDef_Type()
	 * @model
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.NodeDef#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

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
	 * @see com.openMap1.mapper.MapperPackage#getNodeDef_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.NodeDef#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Min Multiplicity</b></em>' attribute.
	 * The literals are from the enumeration {@link com.openMap1.mapper.MinMult}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Minimum multiplicity of the node, seen as a child of its parent. May be 0 or 1.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Multiplicity</em>' attribute.
	 * @see com.openMap1.mapper.MinMult
	 * @see #setMinMultiplicity(MinMult)
	 * @see com.openMap1.mapper.MapperPackage#getNodeDef_MinMultiplicity()
	 * @model
	 * @generated
	 */
	MinMult getMinMultiplicity();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.NodeDef#getMinMultiplicity <em>Min Multiplicity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Multiplicity</em>' attribute.
	 * @see com.openMap1.mapper.MinMult
	 * @see #getMinMultiplicity()
	 * @generated
	 */
	void setMinMultiplicity(MinMult value);

	/**
	 * Returns the value of the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Default value of the node if none is explicitly provided - as for instance in XML schema
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default Value</em>' attribute.
	 * @see #setDefaultValue(String)
	 * @see com.openMap1.mapper.MapperPackage#getNodeDef_DefaultValue()
	 * @model
	 * @generated
	 */
	String getDefaultValue();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.NodeDef#getDefaultValue <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default Value</em>' attribute.
	 * @see #getDefaultValue()
	 * @generated
	 */
	void setDefaultValue(String value);

	/**
	 * Returns the value of the '<em><b>Fixed Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Fixed value of the node - as for instance in XML schema
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fixed Value</em>' attribute.
	 * @see #setFixedValue(String)
	 * @see com.openMap1.mapper.MapperPackage#getNodeDef_FixedValue()
	 * @model
	 * @generated
	 */
	String getFixedValue();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.NodeDef#getFixedValue <em>Fixed Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fixed Value</em>' attribute.
	 * @see #getFixedValue()
	 * @generated
	 */
	void setFixedValue(String value);

	/**
	 * Returns the value of the '<em><b>Node Mapping Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The NodeMappingSet is used to group together all mappings on this node -
	 * and has no other purpose.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Node Mapping Set</em>' containment reference.
	 * @see #setNodeMappingSet(NodeMappingSet)
	 * @see com.openMap1.mapper.MapperPackage#getNodeDef_NodeMappingSet()
	 * @model containment="true"
	 * @generated
	 */
	NodeMappingSet getNodeMappingSet();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.NodeDef#getNodeMappingSet <em>Node Mapping Set</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Node Mapping Set</em>' containment reference.
	 * @see #getNodeMappingSet()
	 * @generated
	 */
	void setNodeMappingSet(NodeMappingSet value);
	
	/**
	 * reset the node mapping set to null
	 */
	public void removeNodeMappingSet();

	/**
	 * Returns the value of the '<em><b>Annotations</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Annotations</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Annotations</em>' containment reference.
	 * @see #setAnnotations(Annotations)
	 * @see com.openMap1.mapper.MapperPackage#getNodeDef_Annotations()
	 * @model containment="true"
	 * @generated
	 */
	Annotations getAnnotations();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.NodeDef#getAnnotations <em>Annotations</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Annotations</em>' containment reference.
	 * @see #getAnnotations()
	 * @generated
	 */
	void setAnnotations(Annotations value);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that if this node has a fixed value , it has no default -
	 * and vice versa/
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean notBothDefaultAndFixed(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean hasCorrectDefaultOrFixedValue(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the minimum multiplicity, as stored in this mapping set,
	 * agrees with the minimum multiplidity in the structure definition
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean hasCorrectMinMultiplicity(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that iin the structure definition, this node is part of the contents of
	 * the element which (according to this mapping set) contains it.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean inStructureOfContainingElement(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * @return path of element or attribute names from the root element down to this node.
	 * This acts as an XPath from the root of a document (even though for imported mapping
	 * sets it is not) and so begins with '/'.
	 */
	public String getPath();
	
	/**
	 * to be set when reading 'use = prohibited' or 'maxOccurs = 0' from XML schema to let 
	 * this node in some type that is being restricted
	 * @return
	 */
	public boolean useIsProhibited();
	
	/**
	 * set the useIsProhibited flag
	 * @param prohibited
	 */
	public void setUseIsProhibited(boolean prohibited);
	
	/**
	 * True if there are mappings anywhere in the subtree beneath this node
	 * @return
	 */
	public boolean hasMappingsInSubTree();
	
	/**
	 * True if there are mappings on this node
	 * @return
	 */
	public boolean hasMappingsOnNode();
	
	/**
	 * true if the relative path from this node leads to a unique node
	 */
	public boolean uniquePath(Xpth relPath);
	
	/**
	 * true if the relative path from this node leads to at least one node
	 */
	public boolean nonOptionalPath(Xpth relPath);
	
	/**
	 * Add an annotation on this node; 
	 * or replace the existing annotation with this key
	 */
	public void addAnnotation(String key, String value);
	
	/**
	 * get an annotation with given key on this node; 
	 * or null if there is no annotation with that key
	 */
	public String getAnnotation(String key);

} // Node
