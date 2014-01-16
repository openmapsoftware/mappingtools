/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.util.MapperValidator;
import java.util.Collection;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.ObjMapping;

import java.util.Map;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Cross Condition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.CrossConditionImpl#getRightPath <em>Right Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.CrossConditionImpl#getRightFunction <em>Right Function</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.CrossConditionImpl#getRightPathConditions <em>Right Path Conditions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CrossConditionImpl extends MappingConditionImpl implements CrossCondition {
	/**
	 * The default value of the '{@link #getRightPath() <em>Right Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default RHS path is "", but should in all cases be edited by the user
	 * <!-- end-user-doc -->
	 * @see #getRightPath()
	 * @ordered
	 */
	protected static final String RIGHT_PATH_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getRightPath() <em>Right Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRightPath()
	 * @generated
	 * @ordered
	 */
	protected String rightPath = RIGHT_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getRightFunction() <em>Right Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * the default is "", not null
	 * <!-- end-user-doc -->
	 * @see #getRightFunction()
	 * @ordered
	 */
	protected static final String RIGHT_FUNCTION_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getRightFunction() <em>Right Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRightFunction()
	 * @generated
	 * @ordered
	 */
	protected String rightFunction = RIGHT_FUNCTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getRightPathConditions() <em>Right Path Conditions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRightPathConditions()
	 * @generated
	 * @ordered
	 */
	protected EList<MappingCondition> rightPathConditions;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CrossConditionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.CROSS_CONDITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRightPath() {
		return rightPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRightPath(String newRightPath) {
		String oldRightPath = rightPath;
		rightPath = newRightPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.CROSS_CONDITION__RIGHT_PATH, oldRightPath, rightPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRightFunction() {
		return rightFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRightFunction(String newRightFunction) {
		String oldRightFunction = rightFunction;
		rightFunction = newRightFunction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.CROSS_CONDITION__RIGHT_FUNCTION, oldRightFunction, rightFunction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MappingCondition> getRightPathConditions() {
		if (rightPathConditions == null) {
			rightPathConditions = new EObjectContainmentEList<MappingCondition>(MappingCondition.class, this, MapperPackage.CROSS_CONDITION__RIGHT_PATH_CONDITIONS);
		}
		return rightPathConditions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the path from the object mapping to the RHS
	 * node of the cross condition is a valid path, and leads to a node
	 * Only do the test if this is a condition on a mapping, not a nested condition.
	 * <!-- end-user-doc -->
	 */
	public boolean rightPathIsValid(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean pathIsValid = true;
		String exMessage = "";
		try {
			// if this is a condition on a mapping
			if (eContainer() instanceof Mapping)
			{
				ObjMapping om = ModelUtil.getObjectMapping((Mapping)eContainer());
				NodeDef mappedNode = ModelUtil.mappingNode(om);
				//the first  'false' means do not test uniqueness (yet)
				pathIsValid = ModelUtil.isRelativePath(mappedNode, getRHSPath());				
			}
		}
		catch (MapperException ex) {pathIsValid = false; exMessage = ex.getMessage();}
		if (!pathIsValid) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.CROSS_CONDITION__RIGHT_PATH_IS_VALID,
						 "The path to the right-hand side of a cross-condition is not valid, or leads to no nodes" + exMessage,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check the RHS path leads to a single node, giving a single value to test.
	 * Only check if the path is valid, and if this is a condition on a mapping, 
	 * and if this condition has no nested conditions (which might get a unique
	 * node form a non-unique path)
	 * <!-- end-user-doc -->
	 */
	public boolean rightPathGivesUniqueNode(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean pathIsUnique = true;
		// only check the path for uniqueness if it is valid (tested in previous validation)
		try {
			// only check if this is a condition on a mapping, and has no nested conditions
			if ((eContainer() instanceof Mapping) && (getRightPathConditions().size() == 0))
			{
				ObjMapping om = ModelUtil.getObjectMapping((Mapping)eContainer());
				NodeDef mappedNode = ModelUtil.mappingNode(om);
				boolean pathIsValid = ModelUtil.isRelativePath(mappedNode, getRHSPath());
				if (pathIsValid) pathIsUnique = ModelUtil.isRelativeDefinitePath(mappedNode, getRHSPath(), true, false);				
			}
		}
		catch (MapperException ex) {} // exception should have been caught in the previous test
		if (!pathIsUnique) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.CROSS_CONDITION__RIGHT_PATH_GIVES_UNIQUE_NODE,
						 "The path to the right-hand side of a cross-condition may lead to more than one node",
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
	public boolean rightFunctionIsValid(DiagnosticChain diagnostics, Map<?, ?> context) {
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
						 MapperValidator.CROSS_CONDITION__RIGHT_FUNCTION_IS_VALID,
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
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH_CONDITIONS:
				return ((InternalEList<?>)getRightPathConditions()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH:
				return getRightPath();
			case MapperPackage.CROSS_CONDITION__RIGHT_FUNCTION:
				return getRightFunction();
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH_CONDITIONS:
				return getRightPathConditions();
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
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH:
				setRightPath((String)newValue);
				return;
			case MapperPackage.CROSS_CONDITION__RIGHT_FUNCTION:
				setRightFunction((String)newValue);
				return;
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH_CONDITIONS:
				getRightPathConditions().clear();
				getRightPathConditions().addAll((Collection<? extends MappingCondition>)newValue);
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
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH:
				setRightPath(RIGHT_PATH_EDEFAULT);
				return;
			case MapperPackage.CROSS_CONDITION__RIGHT_FUNCTION:
				setRightFunction(RIGHT_FUNCTION_EDEFAULT);
				return;
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH_CONDITIONS:
				getRightPathConditions().clear();
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
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH:
				return RIGHT_PATH_EDEFAULT == null ? rightPath != null : !RIGHT_PATH_EDEFAULT.equals(rightPath);
			case MapperPackage.CROSS_CONDITION__RIGHT_FUNCTION:
				return RIGHT_FUNCTION_EDEFAULT == null ? rightFunction != null : !RIGHT_FUNCTION_EDEFAULT.equals(rightFunction);
			case MapperPackage.CROSS_CONDITION__RIGHT_PATH_CONDITIONS:
				return rightPathConditions != null && !rightPathConditions.isEmpty();
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
		result.append(" (rightPath: ");
		result.append(rightPath);
		result.append(", rightFunction: ");
		result.append(rightFunction);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * return the XPath from the mapped node to the node 
	 * defining the LHS of the condition
	 */
	public Xpth getRHSPath() throws MapperException
	{
		return new Xpth(ModelUtil.getGlobalNamespaceSet(this),getRightPath());
	}
	
	/**
	 * for writing out in the Mappings view
	 */
	public String getRHS()
	{
		if (getRightFunction().equals("")) return "(" + getRightPath() + ")";
		else return (getRightFunction() + "{" + getRightPath() + "}");
	}

} //CrossConditionImpl
