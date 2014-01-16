/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import java.util.Iterator;
import java.util.Map;


import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ModelAssocFilter;
import com.openMap1.mapper.ObjMapping;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model Assoc Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ModelAssocFilterImpl#getRoleName <em>Role Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ModelAssocFilterImpl#getOtherClassName <em>Other Class Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ModelAssocFilterImpl#getOtherPackageName <em>Other Package Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ModelAssocFilterImpl#getOtherSubset <em>Other Subset</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelAssocFilterImpl extends ModelFilterImpl implements ModelAssocFilter {
	/**
	 * The default value of the '{@link #getRoleName() <em>Role Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * This is the role name to navigate to the class whose instances must have 
	 * the association. Therefore the role name cannot be the 'non-navigable' role name
	 * <!-- end-user-doc -->
	 * @see #getRoleName()
	 * @ordered
	 */
	protected static final String ROLE_NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getRoleName() <em>Role Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * This is the role name to navigate to the class whose instances must have 
	 * the association. Therefore the role name cannot be the 'non-navigable' role name
	 * <!-- end-user-doc -->
	 * @see #getRoleName()
	 * @generated
	 * @ordered
	 */
	protected String roleName = ROLE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getOtherClassName() <em>Other Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherClassName()
	 * @ordered
	 */
	protected static final String OTHER_CLASS_NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getOtherClassName() <em>Other Class Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherClassName()
	 * @generated
	 * @ordered
	 */
	protected String otherClassName = OTHER_CLASS_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getOtherPackageName() <em>Other Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherPackageName()
	 * @generated
	 * @ordered
	 */
	protected static final String OTHER_PACKAGE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOtherPackageName() <em>Other Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherPackageName()
	 * @generated
	 * @ordered
	 */
	protected String otherPackageName = OTHER_PACKAGE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getOtherRoleName() <em>Other Role Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherRoleName()
	 * @ordered
	 */
	protected static final String OTHER_ROLE_NAME_EDEFAULT = "";

	/**
	 * The default value of the '{@link #getOtherSubset() <em>Other Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherSubset()
	 * @ordered
	 */
	protected static final String OTHER_SUBSET_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getOtherSubset() <em>Other Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOtherSubset()
	 * @generated
	 * @ordered
	 */
	protected String otherSubset = OTHER_SUBSET_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelAssocFilterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.MODEL_ASSOC_FILTER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRoleName(String newRoleName) {
		String oldRoleName = roleName;
		roleName = newRoleName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MODEL_ASSOC_FILTER__ROLE_NAME, oldRoleName, roleName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOtherClassName() {
		return otherClassName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOtherClassName(String newOtherClassName) {
		String oldOtherClassName = otherClassName;
		otherClassName = newOtherClassName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MODEL_ASSOC_FILTER__OTHER_CLASS_NAME, oldOtherClassName, otherClassName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOtherPackageName() {
		return otherPackageName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOtherPackageName(String newOtherPackageName) {
		String oldOtherPackageName = otherPackageName;
		otherPackageName = newOtherPackageName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MODEL_ASSOC_FILTER__OTHER_PACKAGE_NAME, oldOtherPackageName, otherPackageName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOtherSubset() {
		return otherSubset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOtherSubset(String newOtherSubset) {
		String oldOtherSubset = otherSubset;
		otherSubset = newOtherSubset;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MODEL_ASSOC_FILTER__OTHER_SUBSET, oldOtherSubset, otherSubset));
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the association used as a filter exists in the class model;
	 * but only check it if the other end object has an object mapping and its class exists.
	 * Then, while the user can provide both role names, the only one I check is the role
	 * from the other end class which gets to this one. 
	 * So the name of this method is wrong.
	 * <!-- end-user-doc -->
	 */
	public boolean classHasRoleFromOtherClass(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean hasRole = true;
		String thisClassName = "";
		String thisPackageName = "";
		if ((otherObjectMappingExists()) 
				&& (ModelUtil.isInClassModel(getOtherClassName(),getOtherPackageName(), this)))
		{
			EObject om = eContainer().eContainer();
			if (om instanceof ObjMapping)
			{
				thisClassName = ((ObjMapping)om).getMappedClass();
				thisPackageName = ((ObjMapping)om).getMappedPackage();
				hasRole = ModelUtil.associationExists(getOtherClassName(),getOtherPackageName(), 
						getRoleName(), thisClassName, thisPackageName, this);
			}
		}
		if (!hasRole) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.MODEL_ASSOC_FILTER__CLASS_HAS_ROLE_FROM_OTHER_CLASS,
						 ("Class '" + getOtherClassName() + "' has no association '"
								 + getRoleName() + "' to class '" + thisClassName + "'"),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Check that the class and subset required for the other end of this association
	 * is mapped somewhere
	 * <!-- end-user-doc -->
	 */
	public boolean otherObjectMappingExists(DiagnosticChain diagnostics, Map<?,?> context) {
		if (!otherObjectMappingExists()) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.MODEL_ASSOC_FILTER__OTHER_OBJECT_MAPPING_EXISTS,
						 ("There is no object mapping for class " + otherClassSet().stringForm()),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}
	
	private boolean otherObjectMappingExists()
	{
		ObjMapping om = null;
		try {om = ModelUtil.getObjectMapping(ModelUtil.getModelRoot(this), otherClassSet());}
		catch (Exception ex) {return false;}		
		return (om != null);
	}
	
	private ClassSet otherClassSet()
	{
		ClassSet cs = null;
		String qName = ModelUtil.getQualifiedClassName(getOtherClassName(), getOtherPackageName());
		try {cs = new ClassSet(qName,getOtherSubset());}
		catch (Exception ex) {} // should not happen
		return cs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.MODEL_ASSOC_FILTER__ROLE_NAME:
				return getRoleName();
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_CLASS_NAME:
				return getOtherClassName();
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_PACKAGE_NAME:
				return getOtherPackageName();
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_SUBSET:
				return getOtherSubset();
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
			case MapperPackage.MODEL_ASSOC_FILTER__ROLE_NAME:
				setRoleName((String)newValue);
				return;
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_CLASS_NAME:
				setOtherClassName((String)newValue);
				return;
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_PACKAGE_NAME:
				setOtherPackageName((String)newValue);
				return;
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_SUBSET:
				setOtherSubset((String)newValue);
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
			case MapperPackage.MODEL_ASSOC_FILTER__ROLE_NAME:
				setRoleName(ROLE_NAME_EDEFAULT);
				return;
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_CLASS_NAME:
				setOtherClassName(OTHER_CLASS_NAME_EDEFAULT);
				return;
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_PACKAGE_NAME:
				setOtherPackageName(OTHER_PACKAGE_NAME_EDEFAULT);
				return;
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_SUBSET:
				setOtherSubset(OTHER_SUBSET_EDEFAULT);
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
			case MapperPackage.MODEL_ASSOC_FILTER__ROLE_NAME:
				return ROLE_NAME_EDEFAULT == null ? roleName != null : !ROLE_NAME_EDEFAULT.equals(roleName);
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_CLASS_NAME:
				return OTHER_CLASS_NAME_EDEFAULT == null ? otherClassName != null : !OTHER_CLASS_NAME_EDEFAULT.equals(otherClassName);
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_PACKAGE_NAME:
				return OTHER_PACKAGE_NAME_EDEFAULT == null ? otherPackageName != null : !OTHER_PACKAGE_NAME_EDEFAULT.equals(otherPackageName);
			case MapperPackage.MODEL_ASSOC_FILTER__OTHER_SUBSET:
				return OTHER_SUBSET_EDEFAULT == null ? otherSubset != null : !OTHER_SUBSET_EDEFAULT.equals(otherSubset);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (roleName: ");
		result.append(roleName);
		result.append(", otherClassName: ");
		result.append(otherClassName);
		result.append(", otherSubset: ");
		result.append(otherSubset);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * get the association from the other end class which points to the object
	 * which this is a filter on.
	 * Note this method checks the association role name, not its destination class
	 * @return the Ecore Ereference
	 */
	public EReference getModelAssociation()
	{
		EReference ref = null;
		EPackage root = ModelUtil.getClassModelRoot(this);
		EClassifier oc = root.getEClassifier(otherClassName);
		if ((oc != null) && (oc instanceof EClass))
		{
			EClass otherClass = (EClass)oc;
			for (Iterator<EReference> it = otherClass.getEAllReferences().iterator();it.hasNext();)
			{
				EReference er = it.next();
				if (er.getName().equals(getRoleName())) ref = er;
			}
		}
		return ref;
	}
	
	
	/**
	 * the association name, automatically calculated from the role names
	 * @return
	 */
	public String getAssocName()
	{
		String s1 = getRoleName();
		String s2 = null;
		// if there is no EOpposite to the EReference to this class, association name = role name
		if ((getModelAssociation() != null) && (getModelAssociation().getEOpposite() != null))
			{s2 = getModelAssociation().getEOpposite().getName();}	
		return ModelUtil.assocName(s1, s2);
	}
	
	/**
	 * the end 1 or 2, automatically calculated from the role names
	 * If the EReference to get to this class has no EOpposite, then this 
	 * class is the 'to' class of end 1 so the other end is the 'to' class of end 2
	 */
	public int getOtherEnd()
	{
		int end = 2; // to return if you can't find both ends of the association
		if ((getModelAssociation() != null) && (getModelAssociation().getEOpposite() != null))
		{
			String s1 = getRoleName();
			String s2 = getModelAssociation().getEOpposite().getName();	
			end = 2;
			// if the other end role name is lexically first, it is end 1
			if (s1.compareTo(s2) > 0) end = 1;
		}
		return end;
	}
	
	/**
	 * the opposite end role name, or "" if it can't be found
	 */
	public String getOtherRoleName()
	{
		String s2 = MappableAssociation.NON_NAVIGABLE_ROLE_NAME;
		if ((getModelAssociation() != null) && (getModelAssociation().getEOpposite() != null))
		{s2 = getModelAssociation().getEOpposite().getName();}
		return s2;
	}

	
	/**
	 * 
	 * @return description of the filter to go in the Filter column of the Mappings View
	 */
	public String getFilterColumnText()
	{
		String filter = getRoleName() + " FROM " + getOtherPackageName() + "." + getOtherClassName();
		return filter;
	}

} //ModelAssocFilterImpl
