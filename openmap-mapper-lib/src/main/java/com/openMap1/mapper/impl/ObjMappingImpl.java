/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.Xpth;

import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.ModelFilterSet;
import com.openMap1.mapper.ObjMapping;

import java.util.Collection;
import java.util.Map;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Obj Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ObjMappingImpl#getRootPath <em>Root Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ObjMappingImpl#isMultiplyRepresented <em>Multiply Represented</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ObjMappingImpl#getModelFilterSet <em>Model Filter Set</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ObjMappingImpl#getFixedPropertyValues <em>Fixed Property Values</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ObjMappingImpl extends MappingImpl implements ObjMapping {
	/**
	 * The default value of the '{@link #getRootPath() <em>Root Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default is "", meaning 'do not override the path computed from the node position'
	 * <!-- end-user-doc -->
	 * @see #getRootPath()
	 * @ordered
	 */
	protected static final String ROOT_PATH_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getRootPath() <em>Root Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRootPath()
	 * @generated
	 * @ordered
	 */
	protected String rootPath = ROOT_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #isMultiplyRepresented() <em>Multiply Represented</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMultiplyRepresented()
	 * @generated
	 * @ordered
	 */
	protected static final boolean MULTIPLY_REPRESENTED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isMultiplyRepresented() <em>Multiply Represented</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMultiplyRepresented()
	 * @generated
	 * @ordered
	 */
	protected boolean multiplyRepresented = MULTIPLY_REPRESENTED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getModelFilterSet() <em>Model Filter Set</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModelFilterSet()
	 * @generated
	 * @ordered
	 */
	protected ModelFilterSet modelFilterSet;

	/**
	 * The cached value of the '{@link #getFixedPropertyValues() <em>Fixed Property Values</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFixedPropertyValues()
	 * @generated
	 * @ordered
	 */
	protected EList<FixedPropertyValue> fixedPropertyValues;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ObjMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.OBJ_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRootPath(String newRootPath) {
		String oldRootPath = rootPath;
		rootPath = newRootPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.OBJ_MAPPING__ROOT_PATH, oldRootPath, rootPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * If the subset in unset, this method must return ""
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
	public boolean isMultiplyRepresented() {
		return multiplyRepresented;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMultiplyRepresented(boolean newMultiplyRepresented) {
		boolean oldMultiplyRepresented = multiplyRepresented;
		multiplyRepresented = newMultiplyRepresented;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.OBJ_MAPPING__MULTIPLY_REPRESENTED, oldMultiplyRepresented, multiplyRepresented));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModelFilterSet getModelFilterSet() {
		return modelFilterSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetModelFilterSet(ModelFilterSet newModelFilterSet, NotificationChain msgs) {
		ModelFilterSet oldModelFilterSet = modelFilterSet;
		modelFilterSet = newModelFilterSet;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET, oldModelFilterSet, newModelFilterSet);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModelFilterSet(ModelFilterSet newModelFilterSet) {
		if (newModelFilterSet != modelFilterSet) {
			NotificationChain msgs = null;
			if (modelFilterSet != null)
				msgs = ((InternalEObject)modelFilterSet).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET, null, msgs);
			if (newModelFilterSet != null)
				msgs = ((InternalEObject)newModelFilterSet).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET, null, msgs);
			msgs = basicSetModelFilterSet(newModelFilterSet, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET, newModelFilterSet, newModelFilterSet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<FixedPropertyValue> getFixedPropertyValues() {
		if (fixedPropertyValues == null) {
			fixedPropertyValues = new EObjectContainmentEList<FixedPropertyValue>(FixedPropertyValue.class, this, MapperPackage.OBJ_MAPPING__FIXED_PROPERTY_VALUES);
		}
		return fixedPropertyValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that there are no other object mappings with the same class and subset
	 * <!-- end-user-doc -->
	 */
	public boolean subsetIsUniqueWithinClass(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean oneObjectMapping = true;
		String errorMessage = "";
		try
		{
			EObject root = ModelUtil.getModelRoot(this);
			ModelUtil.getObjectMapping(root,this.getClassSet());
		}
		catch (MapperException ex) 
		{
			oneObjectMapping= false;
			errorMessage = ex.getMessage();
		}
		if (!oneObjectMapping) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.OBJ_MAPPING__SUBSET_IS_UNIQUE_WITHIN_CLASS,
						 errorMessage,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public boolean rootPathIsConsistentWithNodePosition(DiagnosticChain diagnostics, Map<?, ?> context) {
		// TODO: implement this method
		// -> specify the condition that violates the invariant
		// -> verify the details of the diagnostic, including severity and message
		// Ensure that you remove @generated or mark it @generated NOT
		boolean isFalse = false;
		if (isFalse) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.OBJ_MAPPING__ROOT_PATH_IS_CONSISTENT_WITH_NODE_POSITION,
						 "",
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
			case MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET:
				return basicSetModelFilterSet(null, msgs);
			case MapperPackage.OBJ_MAPPING__FIXED_PROPERTY_VALUES:
				return ((InternalEList<?>)getFixedPropertyValues()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.OBJ_MAPPING__ROOT_PATH:
				return getRootPath();
			case MapperPackage.OBJ_MAPPING__MULTIPLY_REPRESENTED:
				return isMultiplyRepresented() ? Boolean.TRUE : Boolean.FALSE;
			case MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET:
				return getModelFilterSet();
			case MapperPackage.OBJ_MAPPING__FIXED_PROPERTY_VALUES:
				return getFixedPropertyValues();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MapperPackage.OBJ_MAPPING__ROOT_PATH:
				setRootPath((String)newValue);
				return;
			case MapperPackage.OBJ_MAPPING__MULTIPLY_REPRESENTED:
				setMultiplyRepresented(((Boolean)newValue).booleanValue());
				return;
			case MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET:
				setModelFilterSet((ModelFilterSet)newValue);
				return;
			case MapperPackage.OBJ_MAPPING__FIXED_PROPERTY_VALUES:
				getFixedPropertyValues().clear();
				getFixedPropertyValues().addAll((Collection<? extends FixedPropertyValue>)newValue);
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
			case MapperPackage.OBJ_MAPPING__ROOT_PATH:
				setRootPath(ROOT_PATH_EDEFAULT);
				return;
			case MapperPackage.OBJ_MAPPING__MULTIPLY_REPRESENTED:
				setMultiplyRepresented(MULTIPLY_REPRESENTED_EDEFAULT);
				return;
			case MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET:
				setModelFilterSet((ModelFilterSet)null);
				return;
			case MapperPackage.OBJ_MAPPING__FIXED_PROPERTY_VALUES:
				getFixedPropertyValues().clear();
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
			case MapperPackage.OBJ_MAPPING__ROOT_PATH:
				return ROOT_PATH_EDEFAULT == null ? rootPath != null : !ROOT_PATH_EDEFAULT.equals(rootPath);
			case MapperPackage.OBJ_MAPPING__MULTIPLY_REPRESENTED:
				return multiplyRepresented != MULTIPLY_REPRESENTED_EDEFAULT;
			case MapperPackage.OBJ_MAPPING__MODEL_FILTER_SET:
				return modelFilterSet != null;
			case MapperPackage.OBJ_MAPPING__FIXED_PROPERTY_VALUES:
				return fixedPropertyValues != null && !fixedPropertyValues.isEmpty();
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
		result.append(" (rootPath: ");
		result.append(rootPath);
		result.append(", multiplyRepresented: ");
		result.append(multiplyRepresented);
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
		if (m instanceof ObjMapping) 
		{
			eq = (m.getClassSet().equals(getClassSet()));
		}
		return eq;
	}

	
	/**
	 * not in the genmodel and not visible to the mapper editor
	 * @return the absolute XPath from the root to the mapped node
	 * If the user has specified an XPath by editing the RootPath field, its value overrides
	 * the inherited value, which is computed form the node position
	 */
	public Xpth getRootXPath() throws MapperException
	{
		// normal value if a root path has not been entered by the editor.
		if (getRootPath().equals("")) return super.getRootXPath();
		NamespaceSet NSSet = ModelUtil.getGlobalNamespaceSet(this);
		return new Xpth(NSSet,getRootPath());
	}

	
	/**
	 * details of this mapping, to be written out in the details column of the Mappings view
	 */
	public String getOwnDetails()
	{
		String details = super.getOwnDetails();
		if (isMultiplyRepresented()) details = details + "multiply represented;";
		return details;
	}

} //ObjMappingImpl
