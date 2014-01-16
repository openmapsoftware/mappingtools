/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;

import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.ConversionArgument;
import com.openMap1.mapper.ConversionImplementation;
import com.openMap1.mapper.ConversionSense;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.PropertyConversion;

import java.util.Collection;
import java.util.Vector;

import java.util.Map;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Property Conversion</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.PropertyConversionImpl#getSubset <em>Subset</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropertyConversionImpl#getResultSlot <em>Result Slot</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropertyConversionImpl#getSense <em>Sense</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropertyConversionImpl#getConversionImplementations <em>Conversion Implementations</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropertyConversionImpl#getConversionArguments <em>Conversion Arguments</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.PropertyConversionImpl#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PropertyConversionImpl extends EObjectImpl implements PropertyConversion {
	/**
	 * The default value of the '{@link #getSubset() <em>Subset</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default subset altered  to ""
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
	 * The default value of the '{@link #getResultSlot() <em>Result Slot</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResultSlot()
	 * @generated
	 * @ordered
	 */
	protected static final String RESULT_SLOT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getResultSlot() <em>Result Slot</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResultSlot()
	 * @generated
	 * @ordered
	 */
	protected String resultSlot = RESULT_SLOT_EDEFAULT;

	/**
	 * The default value of the '{@link #getSense() <em>Sense</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSense()
	 * @generated
	 * @ordered
	 */
	protected static final ConversionSense SENSE_EDEFAULT = ConversionSense.IN;

	/**
	 * The cached value of the '{@link #getSense() <em>Sense</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSense()
	 * @generated
	 * @ordered
	 */
	protected ConversionSense sense = SENSE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getConversionImplementations() <em>Conversion Implementations</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConversionImplementations()
	 * @generated
	 * @ordered
	 */
	protected EList<ConversionImplementation> conversionImplementations;

	/**
	 * The cached value of the '{@link #getConversionArguments() <em>Conversion Arguments</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConversionArguments()
	 * @generated
	 * @ordered
	 */
	protected EList<ConversionArgument> conversionArguments;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PropertyConversionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.PROPERTY_CONVERSION;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROPERTY_CONVERSION__SUBSET, oldSubset, subset));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getResultSlot() {
		return resultSlot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setResultSlot(String newResultSlot) {
		String oldResultSlot = resultSlot;
		resultSlot = newResultSlot;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROPERTY_CONVERSION__RESULT_SLOT, oldResultSlot, resultSlot));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConversionSense getSense() {
		return sense;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSense(ConversionSense newSense) {
		ConversionSense oldSense = sense;
		sense = newSense == null ? SENSE_EDEFAULT : newSense;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROPERTY_CONVERSION__SENSE, oldSense, sense));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConversionImplementation> getConversionImplementations() {
		if (conversionImplementations == null) {
			conversionImplementations = new EObjectContainmentEList<ConversionImplementation>(ConversionImplementation.class, this, MapperPackage.PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS);
		}
		return conversionImplementations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConversionArgument> getConversionArguments() {
		if (conversionArguments == null) {
			conversionArguments = new EObjectContainmentEList<ConversionArgument>(ConversionArgument.class, this, MapperPackage.PROPERTY_CONVERSION__CONVERSION_ARGUMENTS);
		}
		return conversionArguments;
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
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.PROPERTY_CONVERSION__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * for 'in' conversions only, check that the result property is in the class model.
	 * Only do the check if the class exists (checked elsewhere)
	 * <!-- end-user-doc -->
	 */
	public boolean classHasResultProperty(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean propertyExists = true;
		String className = "";  
		String packageName = "";  
		EObject cd = eContainer();
		if (cd instanceof ClassDetails) 
		{
			className = ((ClassDetails)cd).getClassName();
			packageName = ((ClassDetails)cd).getPackageName();
		}
		if ((this.getSense() == ConversionSense.IN) 
				&& (ModelUtil.isInClassModel(className, packageName,this)))			
		{
			propertyExists = ModelUtil.hasProperty(className, packageName,getResultSlot(), this);
		}
		if (!propertyExists) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.PROPERTY_CONVERSION__CLASS_HAS_RESULT_PROPERTY,
						 ("Class '" + className + "' has no property '" + getResultSlot() + "'"),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * give a warning if a property conversion has no implementation in either Java or XSLT
	 * <!-- end-user-doc -->
	 */
	public boolean hasImplementation(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean hasAnImplementation = (getConversionImplementations().size()  > 0);
		if (!hasAnImplementation) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.WARNING,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.PROPERTY_CONVERSION__HAS_IMPLEMENTATION,
						 "Property conversion has no implementations",
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
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS:
				return ((InternalEList<?>)getConversionImplementations()).basicRemove(otherEnd, msgs);
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_ARGUMENTS:
				return ((InternalEList<?>)getConversionArguments()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.PROPERTY_CONVERSION__SUBSET:
				return getSubset();
			case MapperPackage.PROPERTY_CONVERSION__RESULT_SLOT:
				return getResultSlot();
			case MapperPackage.PROPERTY_CONVERSION__SENSE:
				return getSense();
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS:
				return getConversionImplementations();
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_ARGUMENTS:
				return getConversionArguments();
			case MapperPackage.PROPERTY_CONVERSION__DESCRIPTION:
				return getDescription();
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
			case MapperPackage.PROPERTY_CONVERSION__SUBSET:
				setSubset((String)newValue);
				return;
			case MapperPackage.PROPERTY_CONVERSION__RESULT_SLOT:
				setResultSlot((String)newValue);
				return;
			case MapperPackage.PROPERTY_CONVERSION__SENSE:
				setSense((ConversionSense)newValue);
				return;
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS:
				getConversionImplementations().clear();
				getConversionImplementations().addAll((Collection<? extends ConversionImplementation>)newValue);
				return;
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_ARGUMENTS:
				getConversionArguments().clear();
				getConversionArguments().addAll((Collection<? extends ConversionArgument>)newValue);
				return;
			case MapperPackage.PROPERTY_CONVERSION__DESCRIPTION:
				setDescription((String)newValue);
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
			case MapperPackage.PROPERTY_CONVERSION__SUBSET:
				setSubset(SUBSET_EDEFAULT);
				return;
			case MapperPackage.PROPERTY_CONVERSION__RESULT_SLOT:
				setResultSlot(RESULT_SLOT_EDEFAULT);
				return;
			case MapperPackage.PROPERTY_CONVERSION__SENSE:
				setSense(SENSE_EDEFAULT);
				return;
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS:
				getConversionImplementations().clear();
				return;
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_ARGUMENTS:
				getConversionArguments().clear();
				return;
			case MapperPackage.PROPERTY_CONVERSION__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
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
			case MapperPackage.PROPERTY_CONVERSION__SUBSET:
				return SUBSET_EDEFAULT == null ? subset != null : !SUBSET_EDEFAULT.equals(subset);
			case MapperPackage.PROPERTY_CONVERSION__RESULT_SLOT:
				return RESULT_SLOT_EDEFAULT == null ? resultSlot != null : !RESULT_SLOT_EDEFAULT.equals(resultSlot);
			case MapperPackage.PROPERTY_CONVERSION__SENSE:
				return sense != SENSE_EDEFAULT;
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_IMPLEMENTATIONS:
				return conversionImplementations != null && !conversionImplementations.isEmpty();
			case MapperPackage.PROPERTY_CONVERSION__CONVERSION_ARGUMENTS:
				return conversionArguments != null && !conversionArguments.isEmpty();
			case MapperPackage.PROPERTY_CONVERSION__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
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
		result.append(" (subset: ");
		result.append(subset);
		result.append(", resultSlot: ");
		result.append(resultSlot);
		result.append(", sense: ");
		result.append(sense);
		result.append(", description: ");
		result.append(description);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * For any 'in' property conversion, check that there is no direct mapping for the property
	 * and subset.
	 * (this validation really belongs on the child PropertyConversion)
	 * <!-- end-user-doc -->
	 */
	public boolean convertedPropertyIsNotRepresentedDirectly()
	{
		boolean OK = true;
		if (this.getSense() == ConversionSense.IN) try
		{
			ClassDetails cd = (ClassDetails)eContainer();
			ClassSet cs = new ClassSet(cd.getQualifiedClassName(),getSubset());
			Vector<PropMapping> pmv = ModelUtil.getPropertyMappings(cs, getResultSlot(), this);
			if (pmv.size() > 0) OK = false;
		}
		catch (MapperException ex) {OK = false;GenUtil.surprise(ex, "PropertyConversion.convertedPropertyIsNotRepresentedDirectly");}
		return OK;
	}

} //PropertyConversionImpl
