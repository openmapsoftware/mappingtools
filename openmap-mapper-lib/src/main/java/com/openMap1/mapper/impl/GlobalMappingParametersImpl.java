/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import java.util.Iterator;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Namespace;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Global Mapping Parameters</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.GlobalMappingParametersImpl#getMappingClass <em>Mapping Class</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.GlobalMappingParametersImpl#getWrapperClass <em>Wrapper Class</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.GlobalMappingParametersImpl#getNameSpaces <em>Name Spaces</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.GlobalMappingParametersImpl#getClassDetails <em>Class Details</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GlobalMappingParametersImpl extends EObjectImpl implements GlobalMappingParameters {
	/**
	 * The default value of the '{@link #getMappingClass() <em>Mapping Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default value changed to ""
	 * <!-- end-user-doc -->
	 * @see #getMappingClass()
	 * @ordered
	 */
	protected static final String MAPPING_CLASS_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getMappingClass() <em>Mapping Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappingClass()
	 * @generated
	 * @ordered
	 */
	protected String mappingClass = MAPPING_CLASS_EDEFAULT;

	/**
	 * The default value of the '{@link #getWrapperClass() <em>Wrapper Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default value changed to ""
	 * <!-- end-user-doc -->
	 * @see #getWrapperClass()
	 * @ordered
	 */
	protected static final String WRAPPER_CLASS_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getWrapperClass() <em>Wrapper Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWrapperClass()
	 * @generated
	 * @ordered
	 */
	protected String wrapperClass = WRAPPER_CLASS_EDEFAULT;

	/**
	 * The cached value of the '{@link #getNameSpaces() <em>Name Spaces</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNameSpaces()
	 * @generated
	 * @ordered
	 */
	protected EList<Namespace> nameSpaces;

	/**
	 * The cached value of the '{@link #getClassDetails() <em>Class Details</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassDetails()
	 * @generated
	 * @ordered
	 */
	protected EList<ClassDetails> classDetails;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GlobalMappingParametersImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.GLOBAL_MAPPING_PARAMETERS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappingClass() {
		return mappingClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappingClass(String newMappingClass) {
		String oldMappingClass = mappingClass;
		mappingClass = newMappingClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.GLOBAL_MAPPING_PARAMETERS__MAPPING_CLASS, oldMappingClass, mappingClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getWrapperClass() {
		return wrapperClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWrapperClass(String newWrapperClass) {
		String oldWrapperClass = wrapperClass;
		wrapperClass = newWrapperClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.GLOBAL_MAPPING_PARAMETERS__WRAPPER_CLASS, oldWrapperClass, wrapperClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Namespace> getNameSpaces() {
		if (nameSpaces == null) {
			nameSpaces = new EObjectContainmentEList<Namespace>(Namespace.class, this, MapperPackage.GLOBAL_MAPPING_PARAMETERS__NAME_SPACES);
		}
		return nameSpaces;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ClassDetails> getClassDetails() {
		if (classDetails == null) {
			classDetails = new EObjectContainmentEList<ClassDetails>(ClassDetails.class, this, MapperPackage.GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS);
		}
		return classDetails;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__NAME_SPACES:
				return ((InternalEList<?>)getNameSpaces()).basicRemove(otherEnd, msgs);
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS:
				return ((InternalEList<?>)getClassDetails()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__MAPPING_CLASS:
				return getMappingClass();
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__WRAPPER_CLASS:
				return getWrapperClass();
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__NAME_SPACES:
				return getNameSpaces();
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS:
				return getClassDetails();
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
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__MAPPING_CLASS:
				setMappingClass((String)newValue);
				return;
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__WRAPPER_CLASS:
				setWrapperClass((String)newValue);
				return;
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__NAME_SPACES:
				getNameSpaces().clear();
				getNameSpaces().addAll((Collection<? extends Namespace>)newValue);
				return;
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS:
				getClassDetails().clear();
				getClassDetails().addAll((Collection<? extends ClassDetails>)newValue);
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
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__MAPPING_CLASS:
				setMappingClass(MAPPING_CLASS_EDEFAULT);
				return;
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__WRAPPER_CLASS:
				setWrapperClass(WRAPPER_CLASS_EDEFAULT);
				return;
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__NAME_SPACES:
				getNameSpaces().clear();
				return;
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS:
				getClassDetails().clear();
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
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__MAPPING_CLASS:
				return MAPPING_CLASS_EDEFAULT == null ? mappingClass != null : !MAPPING_CLASS_EDEFAULT.equals(mappingClass);
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__WRAPPER_CLASS:
				return WRAPPER_CLASS_EDEFAULT == null ? wrapperClass != null : !WRAPPER_CLASS_EDEFAULT.equals(wrapperClass);
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__NAME_SPACES:
				return nameSpaces != null && !nameSpaces.isEmpty();
			case MapperPackage.GLOBAL_MAPPING_PARAMETERS__CLASS_DETAILS:
				return classDetails != null && !classDetails.isEmpty();
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
		result.append(" (mappingClass: ");
		result.append(mappingClass);
		result.append(", wrapperClass: ");
		result.append(wrapperClass);
		result.append(')');
		return result.toString();
	}

	/**
	 * Add the namespaces from a structure definition
	 * @param structureDef
	 */
	public void addNamespaces(StructureDefinition structureDef)
	{
		for (int i = 0; i < structureDef.NSSet().size(); i++)
		{
			namespace ns = structureDef.NSSet().getByIndex(i);
			Namespace NS = MapperFactory.eINSTANCE.createNamespace();
			NS.setPrefix(ns.prefix());
			NS.setURL(ns.URI());
			getNameSpaces().add(NS);
		}
	}
	
	
	/**
	 * @return a clone of this set of global mapping parameters, 
	 * copying over only the namespaces, not the class details.
	 * (used to put on a newly created imported mapping set)
	 */
	public GlobalMappingParameters cloneNamespacesOnly()
	{
		GlobalMappingParameters globalMappingParameters = MapperFactory.eINSTANCE.createGlobalMappingParameters();
		for (Iterator<Namespace> in = getNameSpaces().iterator(); in.hasNext();)
		{
			Namespace ns = in.next();
			Namespace newNS = MapperFactory.eINSTANCE.createNamespace();
			newNS.setPrefix(ns.getPrefix());
			newNS.setURL(ns.getURL());
			globalMappingParameters.getNameSpaces().add(newNS);
		}
		return globalMappingParameters;
	}


} //GlobalMappingParametersImpl
