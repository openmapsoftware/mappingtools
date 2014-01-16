/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import java.util.Map;



import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.Xpth;

import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.LocalPropertyConversion;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Prop Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.PropMappingImpl#getMappedProperty <em>Mapped Property</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropMappingImpl#getPropertyType <em>Property Type</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropMappingImpl#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropMappingImpl#getObjectToPropertyPath <em>Object To Property Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropMappingImpl#getLocalPropertyConversion <em>Local Property Conversion</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PropMappingImpl extends MappingImpl implements PropMapping {
	/**
	 * The default value of the '{@link #getMappedProperty() <em>Mapped Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedProperty()
	 * @ordered
	 */
	protected static final String MAPPED_PROPERTY_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getMappedProperty() <em>Mapped Property</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedProperty()
	 * @generated
	 * @ordered
	 */
	protected String mappedProperty = MAPPED_PROPERTY_EDEFAULT;

	/**
	 * The default value of the '{@link #getPropertyType() <em>Property Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyType()
	 * @ordered
	 */
	protected static final String PROPERTY_TYPE_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getPropertyType() <em>Property Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyType()
	 * @generated
	 * @ordered
	 */
	protected String propertyType = PROPERTY_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultValue()
	 * @ordered
	 */
	protected static final String DEFAULT_VALUE_EDEFAULT = "";

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
	 * The default value of the '{@link #getObjectToPropertyPath() <em>Object To Property Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default path set to "", not null; because it can easily be edited to ""
	 * <!-- end-user-doc -->
	 * @see #getObjectToPropertyPath()
	 * @ordered
	 */
	protected static final String OBJECT_TO_PROPERTY_PATH_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getObjectToPropertyPath() <em>Object To Property Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObjectToPropertyPath()
	 * @generated
	 * @ordered
	 */
	protected String objectToPropertyPath = OBJECT_TO_PROPERTY_PATH_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLocalPropertyConversion() <em>Local Property Conversion</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocalPropertyConversion()
	 * @generated
	 * @ordered
	 */
	protected LocalPropertyConversion localPropertyConversion;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PropMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.PROP_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappedProperty() {
		return mappedProperty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappedProperty(String newMappedProperty) {
		String oldMappedProperty = mappedProperty;
		mappedProperty = newMappedProperty;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROP_MAPPING__MAPPED_PROPERTY, oldMappedProperty, mappedProperty));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPropertyType() {
		return propertyType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPropertyType(String newPropertyType) {
		String oldPropertyType = propertyType;
		propertyType = newPropertyType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROP_MAPPING__PROPERTY_TYPE, oldPropertyType, propertyType));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROP_MAPPING__DEFAULT_VALUE, oldDefaultValue, defaultValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getObjectToPropertyPath() {
		return objectToPropertyPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setObjectToPropertyPath(String newObjectToPropertyPath) {
		String oldObjectToPropertyPath = objectToPropertyPath;
		objectToPropertyPath = newObjectToPropertyPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROP_MAPPING__OBJECT_TO_PROPERTY_PATH, oldObjectToPropertyPath, objectToPropertyPath));
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalPropertyConversion getLocalPropertyConversion() {
		return localPropertyConversion;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLocalPropertyConversion(LocalPropertyConversion newLocalPropertyConversion, NotificationChain msgs) {
		LocalPropertyConversion oldLocalPropertyConversion = localPropertyConversion;
		localPropertyConversion = newLocalPropertyConversion;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION, oldLocalPropertyConversion, newLocalPropertyConversion);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocalPropertyConversion(LocalPropertyConversion newLocalPropertyConversion) {
		if (newLocalPropertyConversion != localPropertyConversion) {
			NotificationChain msgs = null;
			if (localPropertyConversion != null)
				msgs = ((InternalEObject)localPropertyConversion).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION, null, msgs);
			if (newLocalPropertyConversion != null)
				msgs = ((InternalEObject)newLocalPropertyConversion).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION, null, msgs);
			msgs = basicSetLocalPropertyConversion(newLocalPropertyConversion, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION, newLocalPropertyConversion, newLocalPropertyConversion));
	}

	/**
	 * <!-- begin-user-doc -->
	 * Check that the class has the mapped property, 
	 * or that the class and subset has the mapped pseudo-property
	 * (the drop-down which offered pseudo-properties in the editor gives pseudo-properties
	 * for all subsets. Now, for validation ,we know a subset)
	 * <!-- end-user-doc -->
	 */
	public boolean classHasProperty(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean classHasProperty = ModelUtil.hasPropertyOrPseudoProperty(getMappedClass(), getMappedPackage(), getSubset(),getMappedProperty(), this);
		if (!classHasProperty) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.PROP_MAPPING__CLASS_HAS_PROPERTY,
						 ("Class '" + getMappedClass() + "' does not have property '" + getMappedProperty() + "'"),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that there is an object mapping of the same class and subset 
	 * as the property mapping; 
	 */
	public boolean objectMappingExists(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean objectMappingExists = false;
		// System.out.println("Checking property mapping " + getClassSet().className() + " (" + getClassSet().subset() + ")");
		try {
			ObjMapping om = ModelUtil.getObjectMapping(ModelUtil.getModelRoot(this), getClassSet());
			objectMappingExists = (om != null);
		}
		catch (MapperException ex) {objectMappingExists = false;}
		if (!objectMappingExists) {
			if (diagnostics != null) {
				// System.out.println("bad");
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.PROP_MAPPING__OBJECT_MAPPING_EXISTS,
						 ("Property mapping requires a single object mapping for class " 
								 + getClassSet().stringForm() 
								 +  "; there is none."),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the path from the object node to the property node
	 * (which by default is the shortest path, but may be overridden)
	 * is a valid path
	 * <!-- end-user-doc -->
	 */
	public boolean objectToPropertyPathIsValid(DiagnosticChain diagnostics, Map<?,?> context) {
		// 'false' means do not check uniqueness (yet)
		boolean isValidPath = validObjectToPropertyPath(false);
		if (!isValidPath) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.PROP_MAPPING__OBJECT_TO_PROPERTY_PATH_IS_VALID,
						 "path from object mapping node to property mapping node is not valid",
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * check if a path is valid; possibly also check if it is unique
	 * @param mustBeUnique
	 * @return
	 */
	private boolean validObjectToPropertyPath(boolean mustBeUnique)
	{
		boolean isValidPath = true;
		try{
			Xpth path = getObjectToPropertyXPath();
			NodeDef mappedNode = ModelUtil.mappingNode(ModelUtil.getObjectMapping(this));
			if (mustBeUnique)
			isValidPath = ModelUtil.isRelativeDefinitePath(mappedNode, path, mustBeUnique, false);
			else isValidPath = ModelUtil.isRelativePath(mappedNode, path);
		}
		catch (MapperException ex) {isValidPath = false;}
		return isValidPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check (if there are no cross conditions)
	 * that the shortest path from the property node to the object node
	 * leads to a unique node.
	 * This validation condition has been abolished, because it is over-zealous.
	 * There can be occasions where the property belongs to many object instances;
	 * e.g the instances may be grouped together because they have the same value of the property
	 * <!-- end-user-doc -->
	 */
	public boolean objectIsUniqueFromPropertyNode(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean uniqueToObject = true;
		if (!uniqueToObject) {
			if (diagnostics != null) {
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the path from the object node to the property node gives a unique node
	 * (if there are no cross-conditions, and if the cross path is valid - previously checked)
	 * <!-- end-user-doc -->
	 */
	public boolean propertyIsUniqueFromObjectNode(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean isUniquePath = true;
		if ((this.getCrossConditions().size() == 0) && (validObjectToPropertyPath(false)))
		{
			isUniquePath = validObjectToPropertyPath(true);
		}
		if (!isUniquePath) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.PROP_MAPPING__PROPERTY_IS_UNIQUE_FROM_OBJECT_NODE,
						 "Path from object mapping node to property mapping node does not lead to a unique node" +
						 ", and there are no cross-conditions",
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
			case MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION:
				return basicSetLocalPropertyConversion(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * If the subset is unset, this method must return ""
	 * <!-- end-user-doc -->
	 */
	public String getSubset() {
		if (subset == null) subset = "";
		return subset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.PROP_MAPPING__MAPPED_PROPERTY:
				return getMappedProperty();
			case MapperPackage.PROP_MAPPING__PROPERTY_TYPE:
				return getPropertyType();
			case MapperPackage.PROP_MAPPING__DEFAULT_VALUE:
				return getDefaultValue();
			case MapperPackage.PROP_MAPPING__OBJECT_TO_PROPERTY_PATH:
				return getObjectToPropertyPath();
			case MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION:
				return getLocalPropertyConversion();
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
			case MapperPackage.PROP_MAPPING__MAPPED_PROPERTY:
				setMappedProperty((String)newValue);
				return;
			case MapperPackage.PROP_MAPPING__PROPERTY_TYPE:
				setPropertyType((String)newValue);
				return;
			case MapperPackage.PROP_MAPPING__DEFAULT_VALUE:
				setDefaultValue((String)newValue);
				return;
			case MapperPackage.PROP_MAPPING__OBJECT_TO_PROPERTY_PATH:
				setObjectToPropertyPath((String)newValue);
				return;
			case MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION:
				setLocalPropertyConversion((LocalPropertyConversion)newValue);
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
			case MapperPackage.PROP_MAPPING__MAPPED_PROPERTY:
				setMappedProperty(MAPPED_PROPERTY_EDEFAULT);
				return;
			case MapperPackage.PROP_MAPPING__PROPERTY_TYPE:
				setPropertyType(PROPERTY_TYPE_EDEFAULT);
				return;
			case MapperPackage.PROP_MAPPING__DEFAULT_VALUE:
				setDefaultValue(DEFAULT_VALUE_EDEFAULT);
				return;
			case MapperPackage.PROP_MAPPING__OBJECT_TO_PROPERTY_PATH:
				setObjectToPropertyPath(OBJECT_TO_PROPERTY_PATH_EDEFAULT);
				return;
			case MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION:
				setLocalPropertyConversion((LocalPropertyConversion)null);
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
			case MapperPackage.PROP_MAPPING__MAPPED_PROPERTY:
				return MAPPED_PROPERTY_EDEFAULT == null ? mappedProperty != null : !MAPPED_PROPERTY_EDEFAULT.equals(mappedProperty);
			case MapperPackage.PROP_MAPPING__PROPERTY_TYPE:
				return PROPERTY_TYPE_EDEFAULT == null ? propertyType != null : !PROPERTY_TYPE_EDEFAULT.equals(propertyType);
			case MapperPackage.PROP_MAPPING__DEFAULT_VALUE:
				return DEFAULT_VALUE_EDEFAULT == null ? defaultValue != null : !DEFAULT_VALUE_EDEFAULT.equals(defaultValue);
			case MapperPackage.PROP_MAPPING__OBJECT_TO_PROPERTY_PATH:
				return OBJECT_TO_PROPERTY_PATH_EDEFAULT == null ? objectToPropertyPath != null : !OBJECT_TO_PROPERTY_PATH_EDEFAULT.equals(objectToPropertyPath);
			case MapperPackage.PROP_MAPPING__LOCAL_PROPERTY_CONVERSION:
				return localPropertyConversion != null;
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
		result.append(" (mappedProperty: ");
		result.append(mappedProperty);
		result.append(", propertyType: ");
		result.append(propertyType);
		result.append(", defaultValue: ");
		result.append(defaultValue);
		result.append(", objectToPropertyPath: ");
		result.append(objectToPropertyPath);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * Two mappings (usually in different mapping sets) are equivalent if they 
	 * refer to the same thing in the Class model (eg the same class, the same property)
	 * and with the same subset.
	 * Two mappings in the same mapping set should never be equivalent.
	 * @param m
	 * @return
	 */
	public boolean equivalentTo(Mapping m)
	{
		boolean eq = false;
		if (m instanceof PropMapping) 
		{
			PropMapping pm = (PropMapping)m;
			eq = ((pm.getClassSet().equals(getClassSet())) 
					&& (pm.getMappedProperty().equals(getMappedProperty())));
		}
		return eq;
	}

	/**
	 * Xpth form of the path from the owning object to this property
	 * If a cross path has not been supplied, calculates the default shortest XPath.
	 * @return
	 * @throws MapperException
	 */
	public Xpth getObjectToPropertyXPath() throws MapperException
	{
		Xpth path = null;
		// if a non-default cross-path has been supplied
		if ((getObjectToPropertyPath() != null) && (!getObjectToPropertyPath().equals("")))
			{path = new Xpth(ModelUtil.getGlobalNamespaceSet(this),getObjectToPropertyPath());}
		// if no default cross-path has been supplied
		else
		{
			ObjMapping om = ModelUtil.getObjectMapping(this);
			if (om != null) path = om.getRootXPath().defaultCrossPath(this.getRootXPath());
			else if (om == null)
			{
				
			}
		}
		return path;
	}
	
	/**
	 * details of this mapping, to be written out in the details column of the Mappings view
	 */
	public String getOwnDetails()
	{
		String details = super.getOwnDetails();
		if (!(getObjectToPropertyPath().equals(""))) 
			details = details + "cross path = '" + getObjectToPropertyPath() + "';";
		if ((getDefaultValue() != null) && (!(getDefaultValue().equals(""))))
			details = details + "default = '" + getDefaultValue() + "';";
		return details;
	}

} //PropMappingImpl
