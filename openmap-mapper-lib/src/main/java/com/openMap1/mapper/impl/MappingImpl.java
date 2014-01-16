/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;



import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.MultiWay;
import com.openMap1.mapper.NodeDef;

import java.util.Collection;
import java.util.Iterator;

import java.util.Map;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.BasicEList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.MappingImpl#getMappedClass <em>Mapped Class</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingImpl#getMappedPackage <em>Mapped Package</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingImpl#getSubset <em>Subset</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingImpl#getMappingConditions <em>Mapping Conditions</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingImpl#getMultiWay <em>Multi Way</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingImpl#isBreakPoint <em>Break Point</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class MappingImpl extends EObjectImpl implements Mapping {
	/**
	 * The default value of the '{@link #getMappedClass() <em>Mapped Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * changed so the default mapped class name is not null, but ""
	 * <!-- end-user-doc -->
	 * @see #getMappedClass()
	 * @ordered
	 */
	protected static final String MAPPED_CLASS_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getMappedClass() <em>Mapped Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedClass()
	 * @generated
	 * @ordered
	 */
	protected String mappedClass = MAPPED_CLASS_EDEFAULT;

	/**
	 * The default value of the '{@link #getMappedPackage() <em>Mapped Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedPackage()
	 * @ordered
	 */
	protected static final String MAPPED_PACKAGE_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getMappedPackage() <em>Mapped Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedPackage()
	 * @generated
	 * @ordered
	 */
	protected String mappedPackage = MAPPED_PACKAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSubset() <em>Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * changed so the default subset is not null, but ""
	 * <!-- end-user-doc -->
	 * @see #getSubset()
	 * @ordered
	 */
	protected static final String SUBSET_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getSubset() <em>Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubset()
	 * @generated
	 * @ordered
	 */
	protected String subset = SUBSET_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
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
	 * The cached value of the '{@link #getMappingConditions() <em>Mapping Conditions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappingConditions()
	 * @generated
	 * @ordered
	 */
	protected EList<MappingCondition> mappingConditions;

	/**
	 * The default value of the '{@link #getMultiWay() <em>Multi Way</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMultiWay()
	 * @generated
	 * @ordered
	 */
	protected static final MultiWay MULTI_WAY_EDEFAULT = MultiWay.NONE;

	/**
	 * The cached value of the '{@link #getMultiWay() <em>Multi Way</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMultiWay()
	 * @generated
	 * @ordered
	 */
	protected MultiWay multiWay = MULTI_WAY_EDEFAULT;

	/**
	 * The default value of the '{@link #isBreakPoint() <em>Break Point</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isBreakPoint()
	 * @generated
	 * @ordered
	 */
	protected static final boolean BREAK_POINT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isBreakPoint() <em>Break Point</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isBreakPoint()
	 * @generated
	 * @ordered
	 */
	protected boolean breakPoint = BREAK_POINT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappedClass() {
		return mappedClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappedClass(String newMappedClass) {
		String oldMappedClass = mappedClass;
		mappedClass = newMappedClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING__MAPPED_CLASS, oldMappedClass, mappedClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappedPackage() {
		return mappedPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * altered to avoid storing a null value from an ecore model
	 * <!-- end-user-doc -->
	 */
	public void setMappedPackage(String newMappedPackage) {
		String oldMappedPackage = mappedPackage;
		if (newMappedPackage == null) newMappedPackage = "";
		mappedPackage = newMappedPackage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING__MAPPED_PACKAGE, oldMappedPackage, mappedPackage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSubset() {
		return subset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSubset(String newSubset) {
		String oldSubset = subset;
		subset = newSubset;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING__SUBSET, oldSubset, subset));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MappingCondition> getMappingConditions() {
		if (mappingConditions == null) {
			mappingConditions = new EObjectContainmentEList<MappingCondition>(MappingCondition.class, this, MapperPackage.MAPPING__MAPPING_CONDITIONS);
		}
		return mappingConditions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MultiWay getMultiWay() {
		return multiWay;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMultiWay(MultiWay newMultiWay) {
		MultiWay oldMultiWay = multiWay;
		multiWay = newMultiWay == null ? MULTI_WAY_EDEFAULT : newMultiWay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING__MULTI_WAY, oldMultiWay, multiWay));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isBreakPoint() {
		return breakPoint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBreakPoint(boolean newBreakPoint) {
		boolean oldBreakPoint = breakPoint;
		breakPoint = newBreakPoint;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING__BREAK_POINT, oldBreakPoint, breakPoint));
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the class name refers to a class in the class model.
	 * This invariant is appropriate for all subclasses (type of mapping) except 
	 * association mappings, for which it is overridden to pass
	 * <!-- end-user-doc -->
	 */
	public boolean mappedClassIsInClassModel(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean inClassModel = false;
		boolean packageExists = (ModelUtil.getEPackage(getMappedPackage(), this) != null);
		if (packageExists)inClassModel = ModelUtil.isInClassModel(getMappedClass(), getMappedPackage(),this);
		if (!inClassModel) {
			String message = "Package '" + getMappedPackage() 
					+ "' of mapped class '" + getMappedClass() + "' is not in the class model";
			if (packageExists) message = ("Mapped class '" + getMappedClass() 
					+ "' is not in package '" + getMappedPackage() + "'");
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.MAPPING__MAPPED_CLASS_IS_IN_CLASS_MODEL,
						 message,
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
			case MapperPackage.MAPPING__MAPPING_CONDITIONS:
				return ((InternalEList<?>)getMappingConditions()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.MAPPING__MAPPED_CLASS:
				return getMappedClass();
			case MapperPackage.MAPPING__MAPPED_PACKAGE:
				return getMappedPackage();
			case MapperPackage.MAPPING__SUBSET:
				return getSubset();
			case MapperPackage.MAPPING__DESCRIPTION:
				return getDescription();
			case MapperPackage.MAPPING__MAPPING_CONDITIONS:
				return getMappingConditions();
			case MapperPackage.MAPPING__MULTI_WAY:
				return getMultiWay();
			case MapperPackage.MAPPING__BREAK_POINT:
				return isBreakPoint() ? Boolean.TRUE : Boolean.FALSE;
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
			case MapperPackage.MAPPING__MAPPED_CLASS:
				setMappedClass((String)newValue);
				return;
			case MapperPackage.MAPPING__MAPPED_PACKAGE:
				setMappedPackage((String)newValue);
				return;
			case MapperPackage.MAPPING__SUBSET:
				setSubset((String)newValue);
				return;
			case MapperPackage.MAPPING__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MapperPackage.MAPPING__MAPPING_CONDITIONS:
				getMappingConditions().clear();
				getMappingConditions().addAll((Collection<? extends MappingCondition>)newValue);
				return;
			case MapperPackage.MAPPING__MULTI_WAY:
				setMultiWay((MultiWay)newValue);
				return;
			case MapperPackage.MAPPING__BREAK_POINT:
				setBreakPoint(((Boolean)newValue).booleanValue());
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
			case MapperPackage.MAPPING__MAPPED_CLASS:
				setMappedClass(MAPPED_CLASS_EDEFAULT);
				return;
			case MapperPackage.MAPPING__MAPPED_PACKAGE:
				setMappedPackage(MAPPED_PACKAGE_EDEFAULT);
				return;
			case MapperPackage.MAPPING__SUBSET:
				setSubset(SUBSET_EDEFAULT);
				return;
			case MapperPackage.MAPPING__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MapperPackage.MAPPING__MAPPING_CONDITIONS:
				getMappingConditions().clear();
				return;
			case MapperPackage.MAPPING__MULTI_WAY:
				setMultiWay(MULTI_WAY_EDEFAULT);
				return;
			case MapperPackage.MAPPING__BREAK_POINT:
				setBreakPoint(BREAK_POINT_EDEFAULT);
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
			case MapperPackage.MAPPING__MAPPED_CLASS:
				return MAPPED_CLASS_EDEFAULT == null ? mappedClass != null : !MAPPED_CLASS_EDEFAULT.equals(mappedClass);
			case MapperPackage.MAPPING__MAPPED_PACKAGE:
				return MAPPED_PACKAGE_EDEFAULT == null ? mappedPackage != null : !MAPPED_PACKAGE_EDEFAULT.equals(mappedPackage);
			case MapperPackage.MAPPING__SUBSET:
				return SUBSET_EDEFAULT == null ? subset != null : !SUBSET_EDEFAULT.equals(subset);
			case MapperPackage.MAPPING__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MapperPackage.MAPPING__MAPPING_CONDITIONS:
				return mappingConditions != null && !mappingConditions.isEmpty();
			case MapperPackage.MAPPING__MULTI_WAY:
				return multiWay != MULTI_WAY_EDEFAULT;
			case MapperPackage.MAPPING__BREAK_POINT:
				return breakPoint != BREAK_POINT_EDEFAULT;
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
		result.append(" (mappedClass: ");
		result.append(mappedClass);
		result.append(", mappedPackage: ");
		result.append(mappedPackage);
		result.append(", subset: ");
		result.append(subset);
		result.append(", description: ");
		result.append(description);
		result.append(", multiWay: ");
		result.append(multiWay);
		result.append(", breakPoint: ");
		result.append(breakPoint);
		result.append(')');
		return result.toString();
	}
		
	
	/**
	 * not in the genmodel and not visible to the mapper editor
	 * @return the absolute XPath from the root to the mapped node
	 */
	public Xpth getRootXPath() throws MapperException
	{
		Xpth rootPath = null; // sometimes when mappings have been deleted, can't find a path
		if (eContainer() != null)
		{
			/* Work back to the containing node:
			 * all mappings except Association End mappings are below
			 * a NodeMappingSet,which is below a node of the mapped structure */
			EObject container = eContainer().eContainer();
			/* Association end mappings are below an association mapping 
			 * which is below a NodeMappingSet */
			if (this instanceof AssocEndMapping) 
			   {container = container.eContainer();}
			
			if (!(container instanceof NodeDef)) 
				throw new MapperException("EObject containing a node mapping set is a " 
						+ container.getClass().getName());
			
			rootPath = ModelUtil.getRootXpth((NodeDef)container);			
		}
		return rootPath;
	}

	/**
	 * @return the absolute XPath from the root to the mapped node, as a String
	 */
	public String getStringRootPath() throws MapperException
	{
		String rootPath = null; // sometimes when mappings have been deleted, can't find a path
		if (eContainer() != null)
		{
			/* Work back to the containing node:
			 * all mappings except Association End mappings are below
			 * a NodeMappingSet,which is below a node of the mapped structure */
			EObject nodeContainer = eContainer().eContainer();
			/* Association end mappings are below an association mapping 
			 * which is below a NodeMappingSet */
			if (this instanceof AssocEndMapping) 
			   {nodeContainer = nodeContainer.eContainer();}
			
			if (!(nodeContainer instanceof NodeDef)) 
				throw new MapperException("EObject containing a node mapping set is a " 
						+ nodeContainer.getClass().getName());
			
			rootPath = ModelUtil.getRootPath((NodeDef)nodeContainer);			
		}
		//else throw new MapperException("No container for mapping");
		return rootPath;
		
	}

	
	/**
	 * @return class name preceded by the package name and '.', if the package name is non-empty
	 */
	public String getQualifiedClassName()
	{
		String qName = getMappedClass();
		if ((getMappedPackage() != null)&& (!getMappedPackage().equals(""))) 
			qName = getMappedPackage() + "." + qName;
		return qName;
	}
	
	/**
	 * @return the (class, subset) of the mapping, using the class
	 * name preceded by the package name, if non-empty
	 * Null class or subset are treated as very rare.
	 */
	public ClassSet getClassSet()
	{
		ClassSet cs = null;
		try{cs = new ClassSet(getQualifiedClassName(),getSubset());}
		catch (MapperException ex) {GenUtil.surprise(ex,"MappingImpl.getClassSet");}
		return cs;
	}
	
	/**
	 * Two mappings (usually in different mapping sets) are equivalent if they 
	 * refer to the same thing in the Class model (eg the same class, the same property)
	 * and with the same subset.
	 * Two mappings in the same mapping set should never be equivalent.
	 * The method in MappingImpl just returns false, and should be overridden 
	 * in the specific mapping classes.
	 * @param m
	 * @return
	 */
	public boolean equivalentTo(Mapping m) {return false;}
	

	/**
	 * The node which this mapping is attached to
	 * Container is NodeMappingSet; its container is a Node
	 * Must be overridden for AssocEndMappings
	 * @return
	 */
	public NodeDef mappedNode()
	{
		NodeDef mn = (NodeDef)(this.eContainer().eContainer());
		return mn;
	}
	
	/**
	 * 
	 * @return a list of cross-conditions on this mapping, linking it to some other node
	 */
	public EList<CrossCondition> getCrossConditions()
	{
		BasicEList<CrossCondition> cList = new BasicEList<CrossCondition>();
		for (Iterator<MappingCondition> it = getMappingConditions().iterator();it.hasNext();)
		{
			MappingCondition mc = it.next();
			if (mc instanceof CrossCondition) cList.add((CrossCondition)mc);
		}
		return cList;
	}

	
	/**
	 * class name for use in label, with subset in brackets if nonempty
	 * getMappedClass() and getSubset() default to "", not null.
	 */
	public String labelClassName()
	{
		String label = getMappedClass();
		if (label.equals("")) label = "'undefined'";
		if (!getSubset().equals("")) label = label + "(" + getSubset() + ")";
		return label;
	}
	
	/**
	 * details of this mapping, to be written out in the Mappings view.
	 * This method is not to be overridden; the two methods it calls may be
	 */
	public String getDetails()
	{
		return getOwnDetails() + getChildDetails();
	}
	
	
	/**
	 * details of this mapping (not its child nodes), 
	 * to be written out in the details column of the Mappings view
	 */
	public String getOwnDetails()
	{
		String details = "";
		if (getMultiWay() != MultiWay.NONE) 
			details = "multiway = '" + getMultiWay().getLiteral() + "';";
		return details;
	}
	
	/**
	 * details of the child nodes of this mapping, to be written out in the details column of the Mappings view
	 */
	public String getChildDetails()
	{
		String childDetails = "";
		for (Iterator<MappingCondition> it = this.getMappingConditions().iterator();it.hasNext();)
		{
			MappingCondition mc = it.next();
			childDetails = childDetails + "[" + mc.getDetails() + "]; ";
		}
		return childDetails;
	}


} //MappingImpl
