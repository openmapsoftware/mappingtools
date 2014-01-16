/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import org.eclipse.emf.ecore.EObject;

import com.openMap1.mapper.util.MapperValidator;

import java.util.Iterator;
import java.util.Map;

import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.ObjMapping;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Assoc End Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.AssocEndMappingImpl#getMappedRole <em>Mapped Role</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.AssocEndMappingImpl#getObjectToAssociationPath <em>Object To Association Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.AssocEndMappingImpl#getAssociationToObjectPath <em>Association To Object Path</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.AssocEndMappingImpl#isRequiredForObject <em>Required For Object</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AssocEndMappingImpl extends MappingImpl implements AssocEndMapping {
	/**
	 * The default value of the '{@link #getMappedRole() <em>Mapped Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedRole()
	 * @generated
	 * @ordered
	 */
	protected static final String MAPPED_ROLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMappedRole() <em>Mapped Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappedRole()
	 * @generated
	 * @ordered
	 */
	protected String mappedRole = MAPPED_ROLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getObjectToAssociationPath() <em>Object To Association Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * The default has been altered to "" to avoid confusion, as I think it can
	 * easily be accidentally edited to ""
	 * <!-- end-user-doc -->
	 * @see #getObjectToAssociationPath()
	 * @ordered
	 */
	protected static final String OBJECT_TO_ASSOCIATION_PATH_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getObjectToAssociationPath() <em>Object To Association Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObjectToAssociationPath()
	 * @generated
	 * @ordered
	 */
	protected String objectToAssociationPath = OBJECT_TO_ASSOCIATION_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getAssociationToObjectPath() <em>Association To Object Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * The default has been altered to "" to avoid confusion, as I think it can
	 * easily be accidentally edited to ""
	 * <!-- end-user-doc -->
	 * @see #getAssociationToObjectPath()
	 * @ordered
	 */
	protected static final String ASSOCIATION_TO_OBJECT_PATH_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getAssociationToObjectPath() <em>Association To Object Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssociationToObjectPath()
	 * @generated
	 * @ordered
	 */
	protected String associationToObjectPath = ASSOCIATION_TO_OBJECT_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #isRequiredForObject() <em>Required For Object</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequiredForObject()
	 * @generated
	 * @ordered
	 */
	protected static final boolean REQUIRED_FOR_OBJECT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRequiredForObject() <em>Required For Object</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequiredForObject()
	 * @generated
	 * @ordered
	 */
	protected boolean requiredForObject = REQUIRED_FOR_OBJECT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AssocEndMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.ASSOC_END_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappedRole() {
		return mappedRole;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappedRole(String newMappedRole) {
		String oldMappedRole = mappedRole;
		mappedRole = newMappedRole;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ASSOC_END_MAPPING__MAPPED_ROLE, oldMappedRole, mappedRole));
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
	public String getObjectToAssociationPath() {
		return objectToAssociationPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setObjectToAssociationPath(String newObjectToAssociationPath) {
		String oldObjectToAssociationPath = objectToAssociationPath;
		objectToAssociationPath = newObjectToAssociationPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH, oldObjectToAssociationPath, objectToAssociationPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAssociationToObjectPath() {
		return associationToObjectPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAssociationToObjectPath(String newAssociationToObjectPath) {
		String oldAssociationToObjectPath = associationToObjectPath;
		associationToObjectPath = newAssociationToObjectPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH, oldAssociationToObjectPath, associationToObjectPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isRequiredForObject() {
		return requiredForObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRequiredForObject(boolean newRequiredForObject) {
		boolean oldRequiredForObject = requiredForObject;
		requiredForObject = newRequiredForObject;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ASSOC_END_MAPPING__REQUIRED_FOR_OBJECT, oldRequiredForObject, requiredForObject));
	}

	/**
	 * <!-- begin-user-doc -->
	 * This was a check that the class at the other end of the association has
	 *  an eReference, with the correct role name, leading to the 
	 *  class at this end of the association.
	 * <!-- end-user-doc -->
	 */
	public boolean classHasRoleToClass(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean roleOK = (referenceToThisEndClass() != null);
		// if there is no association to this end, so the role name is "", the mapping may be OK
		if (getMappedRole().equals("")) roleOK = true;
		String className = "<cannot find class>";
		try {className = getOtherEndClass().getName();} 
		catch (Exception ex) {roleOK = false;} // if you can't find the class, it is a fault
		if (!roleOK) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ASSOC_END_MAPPING__CLASS_HAS_ROLE_TO_CLASS,
						 ("Class '" + className + "' at the other end of the association mapping "
						  + " has no association with role '" + getMappedRole()
						  + "' to this end class '" + getMappedClass() + "'"),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * check that the role name is some role of the other end
	 * class, pointing to the class at this end;
	 * if it is so, return the EReference
	 * @return
	 */
	private EReference referenceToThisEndClass()
	{
		EReference ref  = null;
		try{
			EClass thisClass = ModelUtil.getEClass(getMappedClass(),getMappedPackage(),this);
			
			// check all the associations of the other end class for this role and (inherited) class
			if ((thisClass != null) && (getOtherEndClass() != null))
			for (Iterator<EReference> it = getOtherEndClass().getEAllReferences().iterator();it.hasNext();)
			{
				EReference next = it.next();
				if ((next.getName() != null) && (next.getName().equals(getMappedRole())))
				{
					EClassifier end = next.getEType();
					if ((end instanceof EClass) && 
						(((EClass)end).isSuperTypeOf(thisClass))) ref = next;						
				}
			}
		}
		catch (Exception ex) {ref = null;}
		return ref;
	}
	
	/**
	 * 
	 * @return the EClass at the other end of this association
	 * @throws MapperException
	 */
	public EClass getOtherEndClass() 
	{
		return ModelUtil.getEClass(getOtherEndMapping().getMappedClass(),getOtherEndMapping().getMappedPackage(),this);		
	}
	
	/**
	 * 
	 * @return the mapping for the other end of this association
	 * @throws MapperException
	 */
	public AssocEndMapping getOtherEndMapping() 
	{
		AssocEndMapping aem = null;
		try{
			// get the other end of the association mapping
			AssocMapping am = (AssocMapping)eContainer();
			// getEnd() gives 1 or 2, so eOther is 1 or 0
			int eOther = 2 - getEnd(); 
			aem = am.getMappedEnd(eOther);					
		}
		catch (MapperException ex) 
		  {GenUtil.surprise(ex,"AssocEndMappingImpl.getOtherEndMapping");}
		return aem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that there is an object mapping 
	 * of the same class and subset as the association end mapping; 
	 * <!-- end-user-doc -->
	 */
	public boolean objectMappingExists(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean objectMappingExists = false;
		try {
			ObjMapping om = ModelUtil.getObjectMapping(ModelUtil.getModelRoot(this), getClassSet());
			if (om != null) objectMappingExists = true;
		}
		catch (MapperException ex) {objectMappingExists = false;}
		if (!objectMappingExists) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ASSOC_END_MAPPING__OBJECT_MAPPING_EXISTS,
						 ("Link mapping requires a single object mapping for class " 
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
	 * check the path from the object mapping node to the association 
	 * mapping node is valid
	 * <!-- end-user-doc -->
	 */
	public boolean objectToAssociationPathIsValid(DiagnosticChain diagnostics, Map<?,?> context) {
		// 'false' means do not check uniqueness
		boolean isValidPath = validObjectToAssociationPath(false);
		if (!isValidPath) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH_IS_VALID,
						 "path from object mapping node to association mapping node is not valid",
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
	public boolean validObjectToAssociationPath(boolean mustBeUnique)
	{
		boolean isValidPath = true;
		try{
			Xpth path = getObjectToAssociationXPath();
			NodeDef mappedNode = ModelUtil.mappingNode(ModelUtil.getObjectMapping(this));
			// only definite paths can be unique
			if (mustBeUnique)
				isValidPath = ModelUtil.isRelativeDefinitePath(mappedNode, path, mustBeUnique,false);
			else isValidPath = ModelUtil.isRelativePath(mappedNode, path);
		}
		catch (MapperException ex) {isValidPath = false;}
		return isValidPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * write warnings if the paths from the other end object to the association node and then 
	 * on to this end object give a multiplicity which does not match that in the class model.
	 * Two possible warnings: for min and max cardinalities. 
	 * Only do the checks  if the paths are valid.
	 * <!-- end-user-doc -->
	 */
	public boolean PathMatchesCardinality(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean matchesMinCardinality = true;
		boolean matchesMaxCardinality = true;
		String minWarning = "";
		String maxWarning = "";
		String assocName = "";
		// only do the checks if the paths are valid and the association exists in the class model
		boolean isUnique = false;
		if (       (getOtherEndClass() != null)  //fails if there is no class model
				&& (getOtherEndMapping().validObjectToAssociationPath(isUnique)) 
				&& (validAssocToObjectPath(isUnique)) && (referenceToThisEndClass() != null)) try
		{
			assocName = getOtherEndClass().getName() + "." + getMappedRole() + "(" + getMappedClass() + ")";

			// find start nodes and XPaths for the two successive traverses needed to navigate the association
			NodeDef startNode = ModelUtil.mappingNode(ModelUtil.getObjectMapping(getOtherEndMapping()));
			Xpth path1 = getOtherEndMapping().getObjectToAssociationXPath();
			NodeDef assocNode = ModelUtil.mappingNode(this);
			Xpth path2 = getAssociationToObjectXPath();
			
			// find cardinalities implied by the paths
			int min = 0;
			if ((ModelUtil.getMinCardinality(startNode,path1) == 1) &&
				(ModelUtil.getMinCardinality(assocNode,path2) == 1)) min = 1;
			int max = -1;
			if ((ModelUtil.getMaxCardinality(startNode,path1) == 1) &&
				(ModelUtil.getMaxCardinality(assocNode,path2) == 1)) max = 1;
			
			// find cardinalities in the class model
			int minFromModel = referenceToThisEndClass().getLowerBound(); // can be 1, or 0
			int maxFromModel = referenceToThisEndClass().getUpperBound(); // can be 1, -1(unbounded) or -2 (undefined)
			if (maxFromModel == -2 ) maxFromModel = -1;  // treat undefined as unbounded
			
			// warnings for min cardinality
			minWarning = "Min multiplicity of " + assocName + " is " 
					+ minFromModel + " in the class model, " + min + " from the mappings";
			// if the mappings give lower multiplicity than the class model, there is always a problem
			if (minFromModel > min) {matchesMinCardinality = false;}
			// if the mappings give higher multiplicity than the model, cross-conditions might correct it
			if ((minFromModel < min) && (getCrossConditions().size() == 0)
					&& (getOtherEndMapping().getCrossConditions().size() == 0))
			{
				matchesMinCardinality = false;
				minWarning = minWarning + ", and there are no cross-conditions.";
			}

			// warnings for max cardinality
			maxWarning = "Max multiplicity of " + assocName + " is " 
			+ multString(maxFromModel) + " in the class model, " + multString(max) + " from the mappings";
			// if the mapping paths give lower multiplicity than the class model, there is always a problem
			if ((max == 1) && (maxFromModel == -1)) {matchesMaxCardinality = false;}
			// if the mapping paths give higher multiplicity than the model, cross-conditions might correct it
			if ((max == -1) && (maxFromModel == 1) && (getCrossConditions().size() == 0)
					&& (getOtherEndMapping().getCrossConditions().size() == 0))
			{
				matchesMaxCardinality = false;
				maxWarning = maxWarning + ", and there are no cross-conditions.";
			}
		}
		catch (MapperException ex) {GenUtil.surprise(ex, "AssocEndMappingImpl.PathMatchesCardinality");}

		if (!matchesMinCardinality) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.WARNING,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ASSOC_END_MAPPING__PATH_MATCHES_CARDINALITY,
						 minWarning,
						 new Object [] { this }));
			}
			return false;
		}

		if (!matchesMaxCardinality) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.WARNING,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ASSOC_END_MAPPING__PATH_MATCHES_CARDINALITY,
						 maxWarning,
						 new Object [] { this }));
			}
			return false;
		}

		return true;
	}
	
	private String multString(int max)
	{
		if (max == 1) return "1";
		if (max == -1) return "unbounded";
		return "unknown";
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the path from the association node to the object node
	 * (which may have been supplied by the user, or may be the default shortest path)
	 * is a valid path in the structure
	 * <!-- end-user-doc -->
	 */
	public boolean AssociationToObjectPathIsValid(DiagnosticChain diagnostics, Map<?,?> context) {
		// do not demand a unique path; there may be cross-conditions
		boolean isValidPath = validAssocToObjectPath(false);
		if (!isValidPath) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH_IS_VALID,
						 "Path from association node to object node is not a valid path",
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
	public boolean validAssocToObjectPath(boolean mustBeUnique)
	{
		boolean isValidPath = true;
		try{
			Xpth path = getAssociationToObjectXPath();
			// System.out.println(path.stringForm());
			NodeDef mappedNode = ModelUtil.mappingNode(this);
			if (mustBeUnique)
				// only definite paths can be unique
			isValidPath = ModelUtil.isRelativeDefinitePath(mappedNode, path, mustBeUnique,false);
			else isValidPath = ModelUtil.isRelativePath(mappedNode, path);
		}
		catch (MapperException ex) {isValidPath = false;}
		return isValidPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check (if there are no cross conditions)
	 * that the  path from the association end node to the object node
	 * leads to a unique node
	 * <!-- end-user-doc -->
	 */
	public boolean objectIsUniqueFromAssociation(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean uniqueToObject = true;
		if (getCrossConditions().size() == 0) try // need only test when there are no cross-conditions
		{
			// if the default shortest path has not been overridden
			if (getAssociationToObjectPath().equals(""))
				{uniqueToObject = ModelUtil.hasUniqueShortestPathToObjectMapping(this);}

			// shortest path has been overridden; but if the supplied shortest path is invalid, that has been noted already
			else if (validAssocToObjectPath(false))
				{uniqueToObject = validAssocToObjectPath(true);} // if it is valid, check it for uniqueness
		}
		catch (MapperException ex) {} // missing object mapping; already detected, do not add misleading messages
		if (!uniqueToObject) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ASSOC_END_MAPPING__OBJECT_IS_UNIQUE_FROM_ASSOCIATION,
						 "The association mapping for association '" + this.getMappedRole() 
						 + "' of class " + getClassSet().stringForm() 
						 + " cannot be linked to a single instance of the object mapping, " +
						 "and there are no cross-conditions to make it unique.",
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
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.ASSOC_END_MAPPING__MAPPED_ROLE:
				return getMappedRole();
			case MapperPackage.ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH:
				return getObjectToAssociationPath();
			case MapperPackage.ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH:
				return getAssociationToObjectPath();
			case MapperPackage.ASSOC_END_MAPPING__REQUIRED_FOR_OBJECT:
				return isRequiredForObject() ? Boolean.TRUE : Boolean.FALSE;
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
			case MapperPackage.ASSOC_END_MAPPING__MAPPED_ROLE:
				setMappedRole((String)newValue);
				return;
			case MapperPackage.ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH:
				setObjectToAssociationPath((String)newValue);
				return;
			case MapperPackage.ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH:
				setAssociationToObjectPath((String)newValue);
				return;
			case MapperPackage.ASSOC_END_MAPPING__REQUIRED_FOR_OBJECT:
				setRequiredForObject(((Boolean)newValue).booleanValue());
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
			case MapperPackage.ASSOC_END_MAPPING__MAPPED_ROLE:
				setMappedRole(MAPPED_ROLE_EDEFAULT);
				return;
			case MapperPackage.ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH:
				setObjectToAssociationPath(OBJECT_TO_ASSOCIATION_PATH_EDEFAULT);
				return;
			case MapperPackage.ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH:
				setAssociationToObjectPath(ASSOCIATION_TO_OBJECT_PATH_EDEFAULT);
				return;
			case MapperPackage.ASSOC_END_MAPPING__REQUIRED_FOR_OBJECT:
				setRequiredForObject(REQUIRED_FOR_OBJECT_EDEFAULT);
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
			case MapperPackage.ASSOC_END_MAPPING__MAPPED_ROLE:
				return MAPPED_ROLE_EDEFAULT == null ? mappedRole != null : !MAPPED_ROLE_EDEFAULT.equals(mappedRole);
			case MapperPackage.ASSOC_END_MAPPING__OBJECT_TO_ASSOCIATION_PATH:
				return OBJECT_TO_ASSOCIATION_PATH_EDEFAULT == null ? objectToAssociationPath != null : !OBJECT_TO_ASSOCIATION_PATH_EDEFAULT.equals(objectToAssociationPath);
			case MapperPackage.ASSOC_END_MAPPING__ASSOCIATION_TO_OBJECT_PATH:
				return ASSOCIATION_TO_OBJECT_PATH_EDEFAULT == null ? associationToObjectPath != null : !ASSOCIATION_TO_OBJECT_PATH_EDEFAULT.equals(associationToObjectPath);
			case MapperPackage.ASSOC_END_MAPPING__REQUIRED_FOR_OBJECT:
				return requiredForObject != REQUIRED_FOR_OBJECT_EDEFAULT;
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
		result.append(" (mappedRole: ");
		result.append(mappedRole);
		result.append(", objectToAssociationPath: ");
		result.append(objectToAssociationPath);
		result.append(", associationToObjectPath: ");
		result.append(associationToObjectPath);
		result.append(", requiredForObject: ");
		result.append(requiredForObject);
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
		if (m instanceof AssocEndMapping) 
		{
			AssocEndMapping am = (AssocEndMapping)m;
			eq = ((am.getClassSet().equals(getClassSet())) 
					&& (am.getMappedRole().equals(getMappedRole())));
		}
		return eq;
	}

	/**
	 * The node which this mapping is attached to.
	 * Container is AssocMapping; its container is NodeMappingSet; its container is Node
	 * @return
	 */
	public NodeDef mappedNode()
	{
		NodeDef mn = (NodeDef)(this.eContainer().eContainer().eContainer());
		return mn;
	}
	
	// cached instance variable
	private Xpth objectToAssociationXPath = null;
	
	/**
	 * Xpth from the object mapping of the end class to this node.
	 * If a path has not been provided, so it is still the default "",
	 * calculate the default shortest path.
	 * @return
	 */
	public Xpth getObjectToAssociationXPath() throws MapperException
	{
		if (objectToAssociationXPath == null)
		{
			if ((getObjectToAssociationPath() != null) && (!getObjectToAssociationPath().equals("")))
				{objectToAssociationXPath = new Xpth(ModelUtil.getGlobalNamespaceSet(this),getObjectToAssociationPath());}
			else
			{
				ObjMapping om = ModelUtil.getObjectMapping(this);
				objectToAssociationXPath = om.getRootXPath().defaultCrossPath(this.getRootXPath());
			}
			
		}
		return objectToAssociationXPath;		
	}
		
	// cached instance variable
	private Xpth associationToObjectXPath = null;
	
	
	/**
	 * Xpth from this node to the object mapping of the end class.
	 * If a path has not been provided and it is still the default "", 
	 * calculate the default shortest path.
	 * @return
	 */
	public Xpth getAssociationToObjectXPath()  throws MapperException
	{
		if (associationToObjectXPath == null)
		{
			if ((getAssociationToObjectPath() != null) && (!getAssociationToObjectPath().equals("")))
				{associationToObjectXPath = new Xpth(ModelUtil.getGlobalNamespaceSet(this),getAssociationToObjectPath());}
			else
			{
				ObjMapping om = ModelUtil.getObjectMapping(this);
				associationToObjectXPath = this.getRootXPath().defaultCrossPath(om.getRootXPath());
			}			
		}
		return associationToObjectXPath;		
	}
	
	
	/**
	 * end = 1 or 2 is now calculated automatically using the association 
	 * from the owning AssocMapping
	 * @return
	 */
	public int getEnd()
	{
		int end = 0; // returned if there is any error
		EObject container = eContainer();
		if (container instanceof AssocMapping)
		{
			AssocMapping am = (AssocMapping)container;
			if (am.getMappedEnd1().equals(this)) end = 1;
			if (am.getMappedEnd2().equals(this)) end = 2;
		}
		return end;
	}
	
	/**
	 * details of this mapping, to be written out in the details column of the Mappings view
	 */
	public String getOwnDetails()
	{
		String details = super.getOwnDetails();
		if (this.isRequiredForObject()) details = details + "required;";
		if (!(getObjectToAssociationPath().equals(""))) 
			details = details + "cross path from object = '" + getObjectToAssociationPath() + "';";
		if (!(getAssociationToObjectPath().equals(""))) 
			details = details + "cross path to object = '" + getAssociationToObjectPath() + "';";
		return details;
	}

	
	/**
	 * @return the association end mapping for the other end of this association
	 */
	public AssocEndMapping otherEndMapping()
	{
		AssocEndMapping aem = null;
		try{
			AssocMapping am = (AssocMapping)eContainer();
			aem = am.getMappedEnd(2-getEnd()); // convert [1,2] to [2,1]					
		}
		catch(Exception ex) {}
		return aem;
	}

} //AssocEndMappingImpl
