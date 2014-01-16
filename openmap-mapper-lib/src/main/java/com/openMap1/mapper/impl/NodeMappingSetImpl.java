/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Node Mapping Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.NodeMappingSetImpl#getObjectMappings <em>Object Mappings</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeMappingSetImpl#getPropertyMappings <em>Property Mappings</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.NodeMappingSetImpl#getAssociationMappings <em>Association Mappings</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NodeMappingSetImpl extends EObjectImpl implements NodeMappingSet {
	/**
	 * The cached value of the '{@link #getObjectMappings() <em>Object Mappings</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObjectMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<ObjMapping> objectMappings;

	/**
	 * The cached value of the '{@link #getPropertyMappings() <em>Property Mappings</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<PropMapping> propertyMappings;

	/**
	 * The cached value of the '{@link #getAssociationMappings() <em>Association Mappings</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssociationMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<AssocMapping> associationMappings;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodeMappingSetImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.NODE_MAPPING_SET;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ObjMapping> getObjectMappings() {
		if (objectMappings == null) {
			objectMappings = new EObjectContainmentEList<ObjMapping>(ObjMapping.class, this, MapperPackage.NODE_MAPPING_SET__OBJECT_MAPPINGS);
		}
		return objectMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PropMapping> getPropertyMappings() {
		if (propertyMappings == null) {
			propertyMappings = new EObjectContainmentEList<PropMapping>(PropMapping.class, this, MapperPackage.NODE_MAPPING_SET__PROPERTY_MAPPINGS);
		}
		return propertyMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<AssocMapping> getAssociationMappings() {
		if (associationMappings == null) {
			associationMappings = new EObjectContainmentEList<AssocMapping>(AssocMapping.class, this, MapperPackage.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS);
		}
		return associationMappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapperPackage.NODE_MAPPING_SET__OBJECT_MAPPINGS:
				return ((InternalEList<?>)getObjectMappings()).basicRemove(otherEnd, msgs);
			case MapperPackage.NODE_MAPPING_SET__PROPERTY_MAPPINGS:
				return ((InternalEList<?>)getPropertyMappings()).basicRemove(otherEnd, msgs);
			case MapperPackage.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS:
				return ((InternalEList<?>)getAssociationMappings()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.NODE_MAPPING_SET__OBJECT_MAPPINGS:
				return getObjectMappings();
			case MapperPackage.NODE_MAPPING_SET__PROPERTY_MAPPINGS:
				return getPropertyMappings();
			case MapperPackage.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS:
				return getAssociationMappings();
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
			case MapperPackage.NODE_MAPPING_SET__OBJECT_MAPPINGS:
				getObjectMappings().clear();
				getObjectMappings().addAll((Collection<? extends ObjMapping>)newValue);
				return;
			case MapperPackage.NODE_MAPPING_SET__PROPERTY_MAPPINGS:
				getPropertyMappings().clear();
				getPropertyMappings().addAll((Collection<? extends PropMapping>)newValue);
				return;
			case MapperPackage.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS:
				getAssociationMappings().clear();
				getAssociationMappings().addAll((Collection<? extends AssocMapping>)newValue);
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
			case MapperPackage.NODE_MAPPING_SET__OBJECT_MAPPINGS:
				getObjectMappings().clear();
				return;
			case MapperPackage.NODE_MAPPING_SET__PROPERTY_MAPPINGS:
				getPropertyMappings().clear();
				return;
			case MapperPackage.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS:
				getAssociationMappings().clear();
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
			case MapperPackage.NODE_MAPPING_SET__OBJECT_MAPPINGS:
				return objectMappings != null && !objectMappings.isEmpty();
			case MapperPackage.NODE_MAPPING_SET__PROPERTY_MAPPINGS:
				return propertyMappings != null && !propertyMappings.isEmpty();
			case MapperPackage.NODE_MAPPING_SET__ASSOCIATION_MAPPINGS:
				return associationMappings != null && !associationMappings.isEmpty();
		}
		return super.eIsSet(featureID);
	}
	/**
	 * 
	 * @return the total number of mappings of all types in this mapping set
	 */
	public int countAllMappings()
	{
		return (getObjectMappings().size() + getPropertyMappings().size() + getAssociationMappings().size());
	}
	
	/**
	 * 
	 * @return a list of all object mappings which are independent on this node - 
	 * i.e not at the dependent end of some association mapping to a class/subset which 
	 * is also represented on this node
	 */
	public List<ObjMapping> independentObjectMappings()
	{
		Vector<ObjMapping> independentMappings = new Vector<ObjMapping>();
		Hashtable<String,String> dependentClassSets = new Hashtable<String,String>();
		
		/// build the table of all classSets which are dependent on others represented here
		for (Iterator<AssocMapping> it = getAssociationMappings().iterator();it.hasNext();) try
		{
			AssocMapping am = it.next();
			for (int end = 0; end < 2; end++)
			{
				AssocEndMapping aem = am.getMappedEnd(end);
				AssocEndMapping other = am.getMappedEnd(1-end);
				if ((aem.isRequiredForObject()) && (classSetIsRepresented(other.getClassSet())))
						dependentClassSets.put(aem.getClassSet().stringForm(), "1");
			}
		}
		catch (MapperException ex) {System.out.println("Invalid association mapping without an end");}
		
		// collect all object mappings which are not dependent
		for (Iterator<ObjMapping> it = getObjectMappings().iterator();it.hasNext();)
		{
			ObjMapping om = it.next();
			if (dependentClassSets.get(om.getClassSet().stringForm()) == null) independentMappings.add(om);
		}
		
		return independentMappings;
	}
	
	
    /**
     * true if a (class.subset) is represented on this node
     * @param cSet
     * @return
     */
	public boolean classSetIsRepresented(ClassSet cSet)
    {
    	boolean isRepresented = false;    	
    	for (Iterator<ObjMapping> it = getObjectMappings().iterator();it.hasNext();)
    		if (cSet.equals(it.next().getClassSet())) isRepresented = true;
    	return isRepresented;
    }
	
	private void message(String s) {System.out.println(s);}


} //NodeMappingSetImpl
