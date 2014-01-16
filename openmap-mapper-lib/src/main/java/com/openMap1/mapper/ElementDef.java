/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;

import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.messageChannel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * 
 * Represents an Element in an XML structure definition.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.ElementDef#isExpanded <em>Expanded</em>}</li>
 *   <li>{@link com.openMap1.mapper.ElementDef#getMaxMultiplicity <em>Max Multiplicity</em>}</li>
 *   <li>{@link com.openMap1.mapper.ElementDef#getChildElements <em>Child Elements</em>}</li>
 *   <li>{@link com.openMap1.mapper.ElementDef#getAttributeDefs <em>Attribute Defs</em>}</li>
 *   <li>{@link com.openMap1.mapper.ElementDef#getImportMappingSet <em>Import Mapping Set</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getElementDef()
 * @model
 * @generated
 */
public interface ElementDef extends NodeDef {
	
	/**
	 * Returns the value of the '<em><b>Expanded</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If this Element has a complex type, then when expanded = false, the structure
	 * below that complex type in the structure definition has not been expanded in this 
	 * mapping set - so the element is a leaf of the mapped structure tree.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expanded</em>' attribute.
	 * @see #setExpanded(boolean)
	 * @see com.openMap1.mapper.MapperPackage#getElementDef_Expanded()
	 * @model
	 * @generated
	 */
	boolean isExpanded();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ElementDef#isExpanded <em>Expanded</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expanded</em>' attribute.
	 * @see #isExpanded()
	 * @generated
	 */
	void setExpanded(boolean value);

	/**
	 * Returns the value of the '<em><b>Max Multiplicity</b></em>' attribute.
	 * The literals are from the enumeration {@link com.openMap1.mapper.MaxMult}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Maximum multiplicity of this element, seen as a child of its parent.
	 * May be 1 , or -1 meaning unbounded.
	 * (Max Multiplicity = 0 is captured by use = 'prohibited')
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Multiplicity</em>' attribute.
	 * @see com.openMap1.mapper.MaxMult
	 * @see #setMaxMultiplicity(MaxMult)
	 * @see com.openMap1.mapper.MapperPackage#getElementDef_MaxMultiplicity()
	 * @model
	 * @generated
	 */
	MaxMult getMaxMultiplicity();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ElementDef#getMaxMultiplicity <em>Max Multiplicity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Multiplicity</em>' attribute.
	 * @see com.openMap1.mapper.MaxMult
	 * @see #getMaxMultiplicity()
	 * @generated
	 */
	void setMaxMultiplicity(MaxMult value);

	/**
	 * Returns the value of the '<em><b>Child Elements</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ElementDef}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Elements that are child elements of this Element, in the structure definition.
	 * This association defines the tree structure of the mapped document, except for
	 * attributes (see below)
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child Elements</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getElementDef_ChildElements()
	 * @model containment="true"
	 * @generated
	 */
	EList<ElementDef> getChildElements();

	/**
	 * Returns the value of the '<em><b>Attribute Defs</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.AttributeDef}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Attributes of this Element, in the structure definition.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute Defs</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getElementDef_AttributeDefs()
	 * @model containment="true"
	 * @generated
	 */
	EList<AttributeDef> getAttributeDefs();

	/**
	 * Returns the value of the '<em><b>Import Mapping Set</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Import Mapping Set</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Import Mapping Set</em>' containment reference.
	 * @see #setImportMappingSet(ImportMappingSet)
	 * @see com.openMap1.mapper.MapperPackage#getElementDef_ImportMappingSet()
	 * @model containment="true"
	 * @generated
	 */
	ImportMappingSet getImportMappingSet();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.ElementDef#getImportMappingSet <em>Import Mapping Set</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Import Mapping Set</em>' containment reference.
	 * @see #getImportMappingSet()
	 * @generated
	 */
	void setImportMappingSet(ImportMappingSet value);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that ff this element has not been epxpanded, it has no child element or attribute nodes.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean noChildrenIfNotExpanded(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that if this element has been expanded, it has all the correct child 
	 * elements and attributes as in the structure definition
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean hasAllChildrenIfExpanded(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check that the maximum mulitpplicity of this element, as given in
	 * this mapping set, agrees with the structure definition.
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean hasCorrectMaxMultiplicity(DiagnosticChain diagnostics, Map<?, ?> context);
	
	/**
	 * return a named child element, or null if there is none
	 * @param name
	 * @return
	 */
	public ElementDef getNamedChildElement(String name);
	
	/**
	 * return a named attribute, or null if there is none
	 * @param name
	 * @return
	 */
	public AttributeDef getNamedAttribute(String name);
	
	/**
	 * 
	 * @param path String form a relative XPath, beginning with '/'
	 * @return the descendant Node reached by that path; or null if there is none
	 */
	public NodeDef getDescendantByPath(String path);

    // method must deliver an ElementDef to be used in recursion
    public ElementDef uniqueSubtreeED();

    /** for recursive writing or element trees */
    public void writeNested(messageChannel mChan, String indent); 
    
    /**
     * @return true if the type of the Element has the XML schema 'mixed' property;
     * default is false.
     */
    public boolean isMixed();
    
    /**
     * assert that the element does or does not have the XML schema 'mixed' property
     * @param mixed true if the Element is mixed
     */
    public void setIsMixed(boolean mixed);
    
    /**
     * @param typeNames: names of ElementDef types, to stop infinite recursion
     * @return the number of mappable nodes (ElementDefs and AttributeDefs)
     * in the subtree of this element, stopping the recursion at any repeated type
     */
    public int countNodesInSubtree(Vector<String> typeNames, StructureDefinition strucDef)
    	throws MapperException;
    

} // Element
