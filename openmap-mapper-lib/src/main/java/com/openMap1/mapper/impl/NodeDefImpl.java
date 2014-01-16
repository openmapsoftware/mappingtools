/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import java.util.Vector;
import java.util.Iterator;


import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.ModelUtil;

import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.Annotations;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.Note;

import java.util.Map;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.NodeDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeDefImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeDefImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeDefImpl#getMinMultiplicity <em>Min Multiplicity</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeDefImpl#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeDefImpl#getFixedValue <em>Fixed Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeDefImpl#getNodeMappingSet <em>Node Mapping Set</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeDefImpl#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class NodeDefImpl extends EObjectImpl implements NodeDef {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default name set to ""
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected String type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * RW - changed default to ""
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getMinMultiplicity() <em>Min Multiplicity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinMultiplicity()
	 * @generated
	 * @ordered
	 */
	protected static final MinMult MIN_MULTIPLICITY_EDEFAULT = MinMult.ZERO;

	/**
	 * The cached value of the '{@link #getMinMultiplicity() <em>Min Multiplicity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinMultiplicity()
	 * @generated
	 * @ordered
	 */
	protected MinMult minMultiplicity = MIN_MULTIPLICITY_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultValue()
	 * @ordered
	 */
	protected static final String DEFAULT_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultValue()
	 * @generated
	 * @ordered
	 */
	protected String defaultValue = DEFAULT_VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getFixedValue() <em>Fixed Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFixedValue()
	 * @generated
	 * @ordered
	 */
	protected static final String FIXED_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFixedValue() <em>Fixed Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFixedValue()
	 * @generated
	 * @ordered
	 */
	protected String fixedValue = FIXED_VALUE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getNodeMappingSet() <em>Node Mapping Set</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNodeMappingSet()
	 * @generated
	 * @ordered
	 */
	protected NodeMappingSet nodeMappingSet;

	/**
	 * The cached value of the '{@link #getAnnotations() <em>Annotations</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAnnotations()
	 * @generated
	 * @ordered
	 */
	protected Annotations annotations;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodeDefImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.NODE_DEF;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(String newType) {
		String oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MinMult getMinMultiplicity() {
		return minMultiplicity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMinMultiplicity(MinMult newMinMultiplicity) {
		MinMult oldMinMultiplicity = minMultiplicity;
		minMultiplicity = newMinMultiplicity == null ? MIN_MULTIPLICITY_EDEFAULT : newMinMultiplicity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__MIN_MULTIPLICITY, oldMinMultiplicity, minMultiplicity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDefaultValue(String newDefaultValue) {
		String oldDefaultValue = defaultValue;
		defaultValue = newDefaultValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__DEFAULT_VALUE, oldDefaultValue, defaultValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFixedValue() {
		return fixedValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFixedValue(String newFixedValue) {
		String oldFixedValue = fixedValue;
		fixedValue = newFixedValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__FIXED_VALUE, oldFixedValue, fixedValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NodeMappingSet getNodeMappingSet() {
		return nodeMappingSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNodeMappingSet(NodeMappingSet newNodeMappingSet, NotificationChain msgs) {
		NodeMappingSet oldNodeMappingSet = nodeMappingSet;
		nodeMappingSet = newNodeMappingSet;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__NODE_MAPPING_SET, oldNodeMappingSet, newNodeMappingSet);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNodeMappingSet(NodeMappingSet newNodeMappingSet) {
		if (newNodeMappingSet != nodeMappingSet) {
			NotificationChain msgs = null;
			if (nodeMappingSet != null)
				msgs = ((InternalEObject)nodeMappingSet).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.NODE_DEF__NODE_MAPPING_SET, null, msgs);
			if (newNodeMappingSet != null)
				msgs = ((InternalEObject)newNodeMappingSet).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.NODE_DEF__NODE_MAPPING_SET, null, msgs);
			msgs = basicSetNodeMappingSet(newNodeMappingSet, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__NODE_MAPPING_SET, newNodeMappingSet, newNodeMappingSet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Annotations getAnnotations() {
		return annotations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAnnotations(Annotations newAnnotations, NotificationChain msgs) {
		Annotations oldAnnotations = annotations;
		annotations = newAnnotations;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__ANNOTATIONS, oldAnnotations, newAnnotations);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAnnotations(Annotations newAnnotations) {
		if (newAnnotations != annotations) {
			NotificationChain msgs = null;
			if (annotations != null)
				msgs = ((InternalEObject)annotations).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.NODE_DEF__ANNOTATIONS, null, msgs);
			if (newAnnotations != null)
				msgs = ((InternalEObject)newAnnotations).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.NODE_DEF__ANNOTATIONS, null, msgs);
			msgs = basicSetAnnotations(newAnnotations, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.NODE_DEF__ANNOTATIONS, newAnnotations, newAnnotations));
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that his node does not have both a default and a fixed value
	 * <!-- end-user-doc -->
	 */
	public boolean notBothDefaultAndFixed(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean notBoth = true;
		if ((getDefaultValue() != null) && (getFixedValue() != null)) notBoth = false;
		if (!notBoth) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.NODE_DEF__NOT_BOTH_DEFAULT_AND_FIXED,
						 "Node should not have both a default value and a fixed value.",
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that either the default value or the fixed value matches that of the mapped
	 * structure.
	 * For this purpose, null counts as the same as ''; because it often 
	 * starts as null, and is easy to edit to '', but you can't edit it back.
	 *
	 * <!-- end-user-doc -->
	 */
	public boolean hasCorrectDefaultOrFixedValue(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean hasCorrectValue = true;
		String errorMess = "";
		if (nodeInStructureDefinition() != null)
		{
			String fixedValue = "";
			if (getFixedValue() != null) fixedValue = getFixedValue();

			String fixedValueFromStructure = "";
			if (nodeInStructureDefinition().getFixedValue() != null) 
				fixedValueFromStructure = nodeInStructureDefinition().getFixedValue();
			
			hasCorrectValue = (fixedValue.equals(fixedValueFromStructure));
			
			if (!hasCorrectValue) 
			{
				errorMess = "Fixed value should be '" + nodeInStructureDefinition().getFixedValue() 
					+ "' but is '" + getFixedValue() + "'";
			}
			else
			{
				String defaultValue = "";
				if (getDefaultValue() != null) defaultValue = getDefaultValue();
				
				String defaultValueFromStructure = "";
				if (nodeInStructureDefinition().getDefaultValue() != null) 
				{
					defaultValueFromStructure = nodeInStructureDefinition().getDefaultValue();
				}

				hasCorrectValue = (defaultValue.equals(defaultValueFromStructure));
				if (!hasCorrectValue) 
				{
					errorMess = "Default value should be '" + nodeInStructureDefinition().getDefaultValue() 
						+ "' but is '" + getDefaultValue() + "'";
				}

				//Fix for string 'null' in schema validation
				if ((getDefaultValue() == null) && 
					(nodeInStructureDefinition().getDefaultValue() != null) && 
					(nodeInStructureDefinition().getDefaultValue().equals("null")))
					{
						hasCorrectValue = true;
						errorMess = "";
					}
			}
		}
		if (!hasCorrectValue) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.NODE_DEF__HAS_CORRECT_DEFAULT_OR_FIXED_VALUE,
						 errorMess,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check the minimum multiplicity of this node (element or attribute)
	 * against the min multiplicity as given by the external structure definition
	 * <!-- end-user-doc -->
	 */
	public boolean hasCorrectMinMultiplicity(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean minMultiplicityOK = true;
		/* If this is the root node, do not check.
		 * If you cannot find this node, do not check; the next check, 
		 * or the check of some higher node, will give a message */
		if ((eContainer() instanceof NodeDef) && (nodeInStructureDefinition() != null))
		{
			minMultiplicityOK = (getMinMultiplicity() == nodeInStructureDefinition().getMinMultiplicity());
		}
		if (!minMultiplicityOK) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.NODE_DEF__HAS_CORRECT_MIN_MULTIPLICITY,
						 ("Min multiplicity mismatch at node '"  + getName() + "'; "
								 + getMinMultiplicity().getName() + " is not  "
							     + nodeInStructureDefinition().getMinMultiplicity().getName()
								 + " as in the structure definition. "),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that this node exists under its parent node, 
	 * in the external structure definition
	 * <!-- end-user-doc -->
	 */
	public boolean inStructureOfContainingElement(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean nodeExists = true;
		String parentName = "";
		/* If this is the root node, do not check.
		 * If you cannot find the parent of this node, do not check; 
		 * the check of some higher node will have given a message */
		if ((eContainer() instanceof NodeDef) && (definedParent() != null))
		{
			nodeExists = (nodeInStructureDefinition() != null);
			parentName = ((NodeDef)eContainer()).getName();
			// virtual attributes inserted to track element ordinal position should not cause failures
			if (getName().equals(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE)) nodeExists = true;
		}
		if (!nodeExists) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.NODE_DEF__IN_STRUCTURE_OF_CONTAINING_ELEMENT,
						 ("Node '" + getName() + "' is not found under element '"
								 + parentName + "' in the structure definition."),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapperPackage.NODE_DEF__NODE_MAPPING_SET:
				return basicSetNodeMappingSet(null, msgs);
			case MapperPackage.NODE_DEF__ANNOTATIONS:
				return basicSetAnnotations(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.NODE_DEF__NAME:
				return getName();
			case MapperPackage.NODE_DEF__TYPE:
				return getType();
			case MapperPackage.NODE_DEF__DESCRIPTION:
				return getDescription();
			case MapperPackage.NODE_DEF__MIN_MULTIPLICITY:
				return getMinMultiplicity();
			case MapperPackage.NODE_DEF__DEFAULT_VALUE:
				return getDefaultValue();
			case MapperPackage.NODE_DEF__FIXED_VALUE:
				return getFixedValue();
			case MapperPackage.NODE_DEF__NODE_MAPPING_SET:
				return getNodeMappingSet();
			case MapperPackage.NODE_DEF__ANNOTATIONS:
				return getAnnotations();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MapperPackage.NODE_DEF__NAME:
				setName((String)newValue);
				return;
			case MapperPackage.NODE_DEF__TYPE:
				setType((String)newValue);
				return;
			case MapperPackage.NODE_DEF__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MapperPackage.NODE_DEF__MIN_MULTIPLICITY:
				setMinMultiplicity((MinMult)newValue);
				return;
			case MapperPackage.NODE_DEF__DEFAULT_VALUE:
				setDefaultValue((String)newValue);
				return;
			case MapperPackage.NODE_DEF__FIXED_VALUE:
				setFixedValue((String)newValue);
				return;
			case MapperPackage.NODE_DEF__NODE_MAPPING_SET:
				setNodeMappingSet((NodeMappingSet)newValue);
				return;
			case MapperPackage.NODE_DEF__ANNOTATIONS:
				setAnnotations((Annotations)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MapperPackage.NODE_DEF__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MapperPackage.NODE_DEF__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case MapperPackage.NODE_DEF__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MapperPackage.NODE_DEF__MIN_MULTIPLICITY:
				setMinMultiplicity(MIN_MULTIPLICITY_EDEFAULT);
				return;
			case MapperPackage.NODE_DEF__DEFAULT_VALUE:
				setDefaultValue(DEFAULT_VALUE_EDEFAULT);
				return;
			case MapperPackage.NODE_DEF__FIXED_VALUE:
				setFixedValue(FIXED_VALUE_EDEFAULT);
				return;
			case MapperPackage.NODE_DEF__NODE_MAPPING_SET:
				setNodeMappingSet((NodeMappingSet)null);
				return;
			case MapperPackage.NODE_DEF__ANNOTATIONS:
				setAnnotations((Annotations)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MapperPackage.NODE_DEF__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MapperPackage.NODE_DEF__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case MapperPackage.NODE_DEF__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MapperPackage.NODE_DEF__MIN_MULTIPLICITY:
				return minMultiplicity != MIN_MULTIPLICITY_EDEFAULT;
			case MapperPackage.NODE_DEF__DEFAULT_VALUE:
				return DEFAULT_VALUE_EDEFAULT == null ? defaultValue != null : !DEFAULT_VALUE_EDEFAULT.equals(defaultValue);
			case MapperPackage.NODE_DEF__FIXED_VALUE:
				return FIXED_VALUE_EDEFAULT == null ? fixedValue != null : !FIXED_VALUE_EDEFAULT.equals(fixedValue);
			case MapperPackage.NODE_DEF__NODE_MAPPING_SET:
				return nodeMappingSet != null;
			case MapperPackage.NODE_DEF__ANNOTATIONS:
				return annotations != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", type: ");
		result.append(type);
		result.append(", description: ");
		result.append(description);
		result.append(", minMultiplicity: ");
		result.append(minMultiplicity);
		result.append(", defaultValue: ");
		result.append(defaultValue);
		result.append(", fixedValue: ");
		result.append(fixedValue);
		result.append(')');
		return result.toString();
	}
	
	//---------------------------------------------------------------------------------------------------
	//              methods used to validate mapped structure against structure definition
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * return the Element whose structure 
	 * (in an external structure definition such as an XML schema) defines
	 * the position of this node in the structure.
	 * This is either the nearest ancestor element with a complex type defined, 
	 * or (if there is nothing nearer) the root element.
	 * This element itself should not be returned, unless it is the root element, even if it has a type; 
	 * return the nearest ancestor with a type.
	 */
	protected ElementDef structureDefiningElement()
	{
		// if this node is the root of the mapped structure, return it
		if (eContainer() instanceof MappedStructure) return (ElementDef)this;
		// otherwise return a proper ancestor which has a type or is the root
		else return structureDefiningAncestor(((ElementDef)eContainer()));
	}
	
	/**
	 * recursive search for the ancestor which defines the structure of which
	 * this node is a part
	 * @param el
	 * @return
	 */
	private ElementDef structureDefiningAncestor(ElementDef el)
	{
		// if this ancestor node has a type, it is the answer
		if ((el.getType() != null ) && (!el.getType().equals(""))) return el;
		
		// if there are no further ancestors, this is the root and has to be the answer
		if (el.eContainer() instanceof MappedStructure) return el;
		
		// otherwise, some ancestor of this ancestor is the answer
		return structureDefiningAncestor((ElementDef)el.eContainer());
	}
	
	
	/**
	 * get the proper structure (as defined by the external structure definition)
	 * below the structure-defining ancestor of this node.
	 * @return
	 */
	protected ElementDef definedAncestorStructure()
	{
		ElementDef ed = null;
		try
		{
			StructureDefinition structureDef = ((MappedStructure)ModelUtil.getModelRoot(this)).getStructureDefinition();
			if (structureDef != null)
			{
				ElementDef el = structureDefiningElement();
				// structure defining ancestor has a type; get the proper structure for that type
				if ((el.getType() != null ) && (!el.getType().equals(""))) 
					return structureDef.typeStructure(el.getType());
				// structure defining ancestor has no type; get the proper structure for its name
				else return structureDef.nameStructure(el.getName());
			}
			
		}
		catch (MapperException ex){System.out.println(ex.getMessage());}
		return ed; // if you cannot find the external structure definition
	}
	
	
	/**
	 * get the proper parent of this node, as defined by the external structure definition; 
	 * or null if there is any problem, such as a mismatch somewhere 
	 * in the tree above this node between the actual structure
	 * in this mapping file and the proper structure. 
	 * @return
	 */
	protected ElementDef definedParent()
	{
		ElementDef actualAncestor = structureDefiningElement(); // ancestor in the mapped structure
		ElementDef properAncestor = definedAncestorStructure(); // ancestor in the external structure definition
		if ((actualAncestor != null) && (properAncestor != null))
		{
			/* find the path of element names from the actual ancestor 
			 * down to the actual parent of this node, not including
			 * the actual ancestor itself (whose name cannot be checked) */
			Vector<String> path = new Vector<String>();
			EObject el = this.eContainer();
			while ((el instanceof ElementDef) && (!el.equals(actualAncestor)))
			{
				path.insertElementAt(((ElementDef)el).getName(), 0);
				el = el.eContainer();
			}
			
			/*  follow the path of element names down from the proper ancestor to the proper parent.
			 * The name of the proper ancestor is not defined by the XSD type definition,
			 * so cannot be checked */
			ElementDef current = properAncestor;
			// current becomes null if the path cannot be followed down at any stage
			for (int i = 0; i < path.size(); i++) if (current != null)
				{current = current.getNamedChildElement(path.get(i));}
			return current;				
		}
		return null;
	}
	
	/**
	 * get the equivalent of this node, as defined by the external structure 
	 * definition, or null if there is any problem, such as a mismatch somewhere 
	 * in the tree above this node, between the actual structure
	 * in this mapping file and the proper structure. 
	 * @return
	 */
	protected NodeDef nodeInStructureDefinition()
	{
		NodeDef dn = null;
		ElementDef parent = definedParent();
		if (parent != null)
		{
			if (this instanceof ElementDef) dn = parent.getNamedChildElement(getName());
			if (this instanceof AttributeDef) dn = parent.getNamedAttribute(getName());
		}
		return dn;	
	}
	
	//---------------------------------------------------------------------------------------------------
	//                                 miscellaneous methods
	//---------------------------------------------------------------------------------------------------
	

	/**
	 * @return path of element or attribute names from the root element down to this node.
	 * This acts as an XPath from the root of a document (even though for imported mapping
	 * sets it is not) and so begins with '/'.
	 */
	public String getPath()
	{
		String path = getName();
		// the default node name "" means 'undefined'
		if (path.equals("")) path = "node()";
		if (this instanceof AttributeDef) path = "@" + path;
		if (eContainer() instanceof NodeDef)
		{
			NodeDef parent = (NodeDef)eContainer();
			path = parent.getPath() + "/" + path;
		}
		else path = "/" + path; // initial '/' makes it an XPath from the root of a document
		return path;
	}
	
	protected boolean useIsProhibited = false;
	
	/**
	 * to be set when reading 'use = prohibited' or 'maxOccurs = 0' from XML schema to let 
	 * this node in some type that is being restricted
	 * @return
	 */
	public boolean useIsProhibited() {return useIsProhibited;}
	
	/**
	 * set the useIsProhibited flag
	 * @param prohibited
	 */
	public void setUseIsProhibited(boolean prohibited) {useIsProhibited = prohibited;}

	
	/**
	 * True if there are mappings anywhere in the subtree beneath this node
	 * @return
	 */
	public boolean hasMappingsInSubTree()
	{
		boolean hasMappings = false;
		if (getNodeMappingSet() != null) hasMappings = true;
		if ((!hasMappings) && (this instanceof ElementDef))
		{
			for (Iterator<ElementDef> it = ((ElementDef)this).getChildElements().iterator();it.hasNext();)
				if (it.next().hasMappingsInSubTree()) hasMappings = true;
			for (Iterator<AttributeDef> it = ((ElementDef)this).getAttributeDefs().iterator();it.hasNext();)
				if (it.next().hasMappingsInSubTree()) hasMappings = true;
		}
		return hasMappings;
	}
	
	
	/**
	 * True if there are mappings on this node
	 * @return
	 */
	public boolean hasMappingsOnNode()
	{
		boolean hasMappings = false;
		if (getNodeMappingSet() != null) hasMappings = true;
		return hasMappings;
		
	}
	
	/**
	 * true if the relative path from this node leads to a unique node
	 */
	public boolean uniquePath(Xpth relPath)
	{
		return ModelUtil.isRelativeDefinitePath(this, relPath, true, false);
	}

	
	/**
	 * true if the relative path from this node leads to at least one node
	 */
	public boolean nonOptionalPath(Xpth relPath)
	{
		return ModelUtil.isRelativeDefinitePath(this, relPath, false, true);		
	}
	
	
	/**
	 * Add an annotation on this node; 
	 * or replace the existing annotation with this key
	 */
	public void addAnnotation(String key, String value)
	{
		// add an annotations child node if there is not one already
		Annotations annotations = getAnnotations();
		if (annotations == null)
		{
			annotations = MapperFactory.eINSTANCE.createAnnotations();
			setAnnotations(annotations);
		}
		
		// if the note with this key exists already, reset its value
		boolean found = false;
		for (Iterator<Note> it = annotations.getNotes().iterator();it.hasNext();)
		{
			Note next = it.next();
			if (next.getKey().equals(key)) 
			{
				next.setValue(value);
				found = true;
			}
		}
		
		// if a note with this key does not exist, add one
		if (!found)
		{
			Note note = MapperFactory.eINSTANCE.createNote();
			note.setKey(key);
			note.setValue(value);
			annotations.getNotes().add(note);
		}
	}
	
	/**
	 * get an annotation with given key on this node; 
	 * or null if there is no annotation with that key
	 */
	public String getAnnotation(String key)
	{
		String value = null;
		Annotations annotations = getAnnotations();
		if (annotations != null)
		{
			for (Iterator<Note> it = annotations.getNotes().iterator();it.hasNext();)
			{
				Note next = it.next();
				if (next.getKey().equals(key)) value = next.getValue();
			}			
		}
		return value;
	}
	
	/**
	 * reset the node mapping set to null
	 */
	public void removeNodeMappingSet() {nodeMappingSet = null;}


	
	//--------------------------------------------------------------------------------------------
	//         Interface TreeElement  - parts common to ElementDefs and AttributeDefs
	//--------------------------------------------------------------------------------------------


    /** tag name, including namespace prefix */
    public String tagName() {return getName();}
    
    public boolean isOptional() {return this.getMinMultiplicity() == MinMult.ZERO;}

	
	
} //NodeImpl
