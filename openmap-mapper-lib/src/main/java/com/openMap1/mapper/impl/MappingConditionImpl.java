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
import java.util.Iterator;

import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.ConditionTest;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;

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
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping Condition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.MappingConditionImpl#getLeftPath <em>Left Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingConditionImpl#getLeftFunction <em>Left Function</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingConditionImpl#getTest <em>Test</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingConditionImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappingConditionImpl#getLeftPathConditions <em>Left Path Conditions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class MappingConditionImpl extends EObjectImpl implements MappingCondition {
	/**
	 * The default value of the '{@link #getLeftPath() <em>Left Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * The default LHS path is ""; should always be edited by the user
	 * <!-- end-user-doc -->
	 * @see #getLeftPath()
	 * @ordered
	 */
	protected static final String LEFT_PATH_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getLeftPath() <em>Left Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeftPath()
	 * @generated
	 * @ordered
	 */
	protected String leftPath = LEFT_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getLeftFunction() <em>Left Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * The default is "" rather than null
	 * <!-- end-user-doc -->
	 * @see #getLeftFunction()
	 * @ordered
	 */
	protected static final String LEFT_FUNCTION_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getLeftFunction() <em>Left Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeftFunction()
	 * @generated
	 * @ordered
	 */
	protected String leftFunction = LEFT_FUNCTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getTest() <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTest()
	 * @generated
	 * @ordered
	 */
	protected static final ConditionTest TEST_EDEFAULT = ConditionTest.EQUALS;

	/**
	 * The cached value of the '{@link #getTest() <em>Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTest()
	 * @generated
	 * @ordered
	 */
	protected ConditionTest test = TEST_EDEFAULT;

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
	 * The cached value of the '{@link #getLeftPathConditions() <em>Left Path Conditions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeftPathConditions()
	 * @generated
	 * @ordered
	 */
	protected EList<MappingCondition> leftPathConditions;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MappingConditionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.MAPPING_CONDITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLeftPath() {
		return leftPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeftPath(String newLeftPath) {
		String oldLeftPath = leftPath;
		leftPath = newLeftPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING_CONDITION__LEFT_PATH, oldLeftPath, leftPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLeftFunction() {
		return leftFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeftFunction(String newLeftFunction) {
		String oldLeftFunction = leftFunction;
		leftFunction = newLeftFunction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING_CONDITION__LEFT_FUNCTION, oldLeftFunction, leftFunction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConditionTest getTest() {
		return test;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTest(ConditionTest newTest) {
		ConditionTest oldTest = test;
		test = newTest == null ? TEST_EDEFAULT : newTest;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING_CONDITION__TEST, oldTest, test));
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
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPING_CONDITION__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MappingCondition> getLeftPathConditions() {
		if (leftPathConditions == null) {
			leftPathConditions = new EObjectContainmentEList<MappingCondition>(MappingCondition.class, this, MapperPackage.MAPPING_CONDITION__LEFT_PATH_CONDITIONS);
		}
		return leftPathConditions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the left path is a valid path that leads to a node in  the structure.
	 * Only validate mapping conditions on mappings - not mapping conditions
	 * on the nodes used in other mapping conditions
	 * <!-- end-user-doc -->
	 */
	public boolean leftPathIsValid(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean pathIsValid = true;
		String exMessage = "";
		try {
			// if this is a mapping condition on a mapping (not a nested mapping condition)
			if (eContainer() instanceof Mapping)
			{
				NodeDef mappedNode = ModelUtil.mappingNode((Mapping)eContainer());
				// 'false' means do not test uniqueness - because the path may not be unique without other conditions
				pathIsValid = ModelUtil.isRelativePath(mappedNode, getLHSPath());				
			}
		}
		catch (MapperException ex) {pathIsValid = false; exMessage = ex.getMessage();}
		if (!pathIsValid) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.MAPPING_CONDITION__LEFT_PATH_IS_VALID,
						 "The path to the left-hand side of a mapping condition is not valid, or leads to no nodes" + exMessage,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check the LHS path leads to a single node, giving a single value to test. 
	 * Only do this test if: 
	 * (1) it is a mapping condition on a mapping (not an nested mapping condition)
	 * (2)It has not nested mapping conditions (which might get a unique node from
	 * a non-unique path)
	 * <!-- end-user-doc -->
	 */
	public boolean leftPathGivesUniqueNode(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean pathIsUnique = true;
		// only check the path for uniqueness if it is valid (tested in previous validation)
		try {
			// if this mapping condition is not nested, and has no nested conditions, do the test
			if ((eContainer() instanceof Mapping) && (getLeftPathConditions().size() == 0))
			{
				NodeDef mappedNode = ModelUtil.mappingNode((Mapping)eContainer());
				boolean pathIsValid = ModelUtil.isRelativePath(mappedNode, getLHSPath());
				if (pathIsValid) pathIsUnique = ModelUtil.isRelativeDefinitePath(mappedNode, getLHSPath(), true, false);				
			}
		}
		catch (MapperException ex) {} // exception should have been caught in the previous test
		if (!pathIsUnique) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.MAPPING_CONDITION__LEFT_PATH_GIVES_UNIQUE_NODE,
						 "The path to the left-hand side of a mapping condition may lead to more than one node",
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
	public boolean leftFunctionIsValid(DiagnosticChain diagnostics, Map<?, ?> context) {
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
						 MapperValidator.MAPPING_CONDITION__LEFT_FUNCTION_IS_VALID,
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
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH_CONDITIONS:
				return ((InternalEList<?>)getLeftPathConditions()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH:
				return getLeftPath();
			case MapperPackage.MAPPING_CONDITION__LEFT_FUNCTION:
				return getLeftFunction();
			case MapperPackage.MAPPING_CONDITION__TEST:
				return getTest();
			case MapperPackage.MAPPING_CONDITION__DESCRIPTION:
				return getDescription();
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH_CONDITIONS:
				return getLeftPathConditions();
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
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH:
				setLeftPath((String)newValue);
				return;
			case MapperPackage.MAPPING_CONDITION__LEFT_FUNCTION:
				setLeftFunction((String)newValue);
				return;
			case MapperPackage.MAPPING_CONDITION__TEST:
				setTest((ConditionTest)newValue);
				return;
			case MapperPackage.MAPPING_CONDITION__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH_CONDITIONS:
				getLeftPathConditions().clear();
				getLeftPathConditions().addAll((Collection<? extends MappingCondition>)newValue);
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
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH:
				setLeftPath(LEFT_PATH_EDEFAULT);
				return;
			case MapperPackage.MAPPING_CONDITION__LEFT_FUNCTION:
				setLeftFunction(LEFT_FUNCTION_EDEFAULT);
				return;
			case MapperPackage.MAPPING_CONDITION__TEST:
				setTest(TEST_EDEFAULT);
				return;
			case MapperPackage.MAPPING_CONDITION__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH_CONDITIONS:
				getLeftPathConditions().clear();
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
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH:
				return LEFT_PATH_EDEFAULT == null ? leftPath != null : !LEFT_PATH_EDEFAULT.equals(leftPath);
			case MapperPackage.MAPPING_CONDITION__LEFT_FUNCTION:
				return LEFT_FUNCTION_EDEFAULT == null ? leftFunction != null : !LEFT_FUNCTION_EDEFAULT.equals(leftFunction);
			case MapperPackage.MAPPING_CONDITION__TEST:
				return test != TEST_EDEFAULT;
			case MapperPackage.MAPPING_CONDITION__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MapperPackage.MAPPING_CONDITION__LEFT_PATH_CONDITIONS:
				return leftPathConditions != null && !leftPathConditions.isEmpty();
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
		result.append(" (leftPath: ");
		result.append(leftPath);
		result.append(", leftFunction: ");
		result.append(leftFunction);
		result.append(", test: ");
		result.append(test);
		result.append(", description: ");
		result.append(description);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * return the XPath from the mapped node to the node 
	 * defining the LHS of the condition
	 */
	public Xpth getLHSPath() throws MapperException
	{
		return new Xpth(ModelUtil.getGlobalNamespaceSet(this),getLeftPath());
	}
	

	
	public static int OBJECT= 0;
	public static int PROPERTY= 1;
	public static int ASSOCIATION= 2;
	public static int LHS= 3;
	public static int RHS= 4;
	
	/**
	 * @return a MappingConditionImpl static int constant
	 * OBJECT, PROPERTY, ASSOCIATION, LHS, or RHS
	 * which defines what this is a condition on
	 */
	public int conditionOn()
	{
		int type = -1;
		if (eContainer() instanceof ObjMapping) type= OBJECT;
		if (eContainer() instanceof PropMapping) type= PROPERTY;
		if (eContainer() instanceof AssocEndMapping) type= ASSOCIATION;

		if (eContainer() instanceof MappingCondition)
			for (Iterator<MappingCondition> it = ((MappingCondition)eContainer()).
				getLeftPathConditions().iterator();it.hasNext();)
					if (it.next().equals(this)) type = LHS;

		if (eContainer() instanceof CrossCondition)
			for (Iterator<MappingCondition> it = ((CrossCondition)eContainer()).
				getRightPathConditions().iterator();it.hasNext();)
					if (it.next().equals(this)) type = RHS;

		return type;
	}
	
	/**
	 * details of this mapping condition, to be written out in the details column of the Mappings view
	 */
	public String getDetails()
	{
		return getLHS() + " " + this.getTest().getLiteral() + " " + getRHS();
	}
	
	/**
	 * description of the left-hand-side of a condition
	 * @return
	 */
	public String getLHS()
	{
		if (leftFunction.equals("")) return "(" + getLeftPath() + ")";
		return leftFunction + "{" + getLeftPath() + "}";
	}
	
	abstract public String getRHS(); 

	
	


} //MappingConditionImpl
