/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ParameterClass;
import com.openMap1.mapper.ParameterClassValue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Import Mapping Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ImportMappingSetImpl#getMappingSetURI <em>Mapping Set URI</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ImportMappingSetImpl#getParameterClassValues <em>Parameter Class Values</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ImportMappingSetImpl extends EObjectImpl implements ImportMappingSet {
	/**
	 * The default value of the '{@link #getMappingSetURI() <em>Mapping Set URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappingSetURI()
	 * @generated
	 * @ordered
	 */
	protected static final String MAPPING_SET_URI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMappingSetURI() <em>Mapping Set URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappingSetURI()
	 * @generated
	 * @ordered
	 */
	protected String mappingSetURI = MAPPING_SET_URI_EDEFAULT;

	/**
	 * The cached value of the '{@link #getParameterClassValues() <em>Parameter Class Values</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterClassValues()
	 * @generated
	 * @ordered
	 */
	protected EList<ParameterClassValue> parameterClassValues;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ImportMappingSetImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.IMPORT_MAPPING_SET;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMappingSetURI() {
		return mappingSetURI;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappingSetURI(String newMappingSetURI) {
		String oldMappingSetURI = mappingSetURI;
		mappingSetURI = newMappingSetURI;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.IMPORT_MAPPING_SET__MAPPING_SET_URI, oldMappingSetURI, mappingSetURI));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ParameterClassValue> getParameterClassValues() {
		if (parameterClassValues == null) {
			parameterClassValues = new EObjectContainmentEList<ParameterClassValue>(ParameterClassValue.class, this, MapperPackage.IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES);
		}
		return parameterClassValues;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the imported mapping set can be found
	 * <!-- end-user-doc -->
	 */
	public boolean canFindMappingSet(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean canFindMappingSet = (getImportedMappingSet() != null);
		if (!canFindMappingSet) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.IMPORT_MAPPING_SET__CAN_FIND_MAPPING_SET,
						 "Cannot find imported mapping set at " + getMappingSetURI(),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the imported mapping set uses the same class model as this one
	 * <!-- end-user-doc -->
	 */
	public boolean mappingSetHasSameClassModel(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean sameClassModel = true;
		// only make this check if the imported mapping set can be found
		if (getImportedMappingSet() != null)
		{
			MappedStructure ms = (MappedStructure)ModelUtil.getModelRoot(this);
			sameClassModel = (ms.getUMLModelURL().equals(getImportedMappingSet().getUMLModelURL()));			
		}
		if (!sameClassModel) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.IMPORT_MAPPING_SET__MAPPING_SET_HAS_SAME_CLASS_MODEL,
						 "Class model of imported mapping set is at '" 
						 + getImportedMappingSet().getUMLModelURL() + "' , not the same as the class model of this mapping set.",
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that every actual class passed to the imported mapping set
	 * is a subclass of the corresponding parameter class. 
	 * There are two error cases per parameter class: the parameter class is not a 
	 * superclass of the parameter value class, or the parameter class does not exist
	 * <!-- end-user-doc -->
	 */
	public boolean mappingSetParametersMatch(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean parametersMatch = true;
		String errorMess = "Parameter class values are not matched in imported mapping set: ";
		// only make the check if the imported mapping set can be found and has the same class model
		if ((getImportedMappingSet() != null) && (mappingSetHasSameClassModel(null,null)))
		{
			for (Iterator<ParameterClassValue> it = getParameterClassValues().iterator(); it.hasNext();)
			{
				ParameterClassValue pcv = it.next();
				String error = checkParameterClass(pcv);
				if (error.length() > 0)
				{
					parametersMatch = false;
					errorMess = errorMess + error;
				}
			}
		}
		if (!parametersMatch) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.IMPORT_MAPPING_SET__MAPPING_SET_PARAMETERS_MATCH,
						 errorMess,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * for every parameter value class which is to be passed to the imported mapping set,
	 * check that the class is in the class model, that a parameter class with the 
	 * same index  exists, and is a superclass of the parameter value class.
	 * @param pcv the parameter value class being passed to the imported mapping set
	 * @return an error message; "" if there is no error
	 */
	private String checkParameterClass(ParameterClassValue pcv)
	{
		String error = "";
		EPackage classModel = ModelUtil.getClassModelRoot(this);
		if (classModel != null)
		{
			EClass ecv = ModelUtil.getNamedClass(classModel, pcv.getQualifiedClassName());
			if (ecv == null) {error = "Parameter class value '" + pcv.getQualifiedClassName() + "' is not in the class model";}
			else
			{
				boolean foundSuperClass = false;
				for (Iterator<ParameterClass> it = getImportedMappingSet().getParameterClasses().iterator(); it.hasNext();)
				{
					ParameterClass pc = it.next();
					// only compare parameters with the same integer index
					if (pc.getParameterIndex() == pcv.getParameterIndex())
					{
						EClass ec = ModelUtil.getNamedClass(classModel, pc.getQualifiedClassName());
						if (ec == null) {error = "Parameter class '" + pc.getQualifiedClassName() + "' of the imported mapping set is not in the class model";}
						else
						{
							if (ec.isSuperTypeOf(ecv)) foundSuperClass = true;
							else error = "Parameter class '" + pc.getQualifiedClassName() 
								+ "' of the imported mapping set is not a superclass of the parameter value class " +
								pcv.getQualifiedClassName() + "';";
	
						}
					}
				}
				if ((!foundSuperClass) && (error.equals("")))
					error = ("The imported mapping set has no parameter class with index " +  pcv.getParameterIndex());
			}
		}
		return error;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the complex type name of root element of the imported mapping set
	 * matches the complex type name of the importing element; 
	 * if they do match, go on to check the detailed structure match
	 * <!-- end-user-doc -->
	 */
	public boolean mappingSetStructureMatches(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean structureMatches = true;
		String errorMess = "";
		// only make the check if the imported mapping set can be found
		if (getImportedMappingSet() != null)
		{
			String importingType = getImportingElement().getType();
			String importedType = getImportedMappingSet().getTopElementType();
			if (!(importingType.equals(importedType)))
			{
				structureMatches = false;
				errorMess = ("The imported mapping set's top element type '" + importedType
						 + "' doe not match the type '" +  importingType + "' of the importing element.");				
			}
			// if the type names match, do a detailed structure check			
			else try
			{
				// structure tree the importing element would be expected to have, if it were expanded
				StructureDefinition structureDef = ModelUtil.getMappedStructure(this).getStructureDefinition();
				if (structureDef != null) // otherwise it cannot be checked
				{
					ElementDef importingStructure = structureDef.typeStructure(importingType);
					// structure tree of the imported mapping set
					ElementDef importedStructure = getImportedMappingSet().getRootElement();
					
					errorMess = "Detailed structure mismatch in imported mapping set: ";
					int len = errorMess.length();
					errorMess = detailedStructureMatch(importingStructure, importedStructure, errorMess);
					// the message is extended if there are any problems
					structureMatches = (errorMess.length() == len);					
				}
			}
			catch (MapperException ex){System.out.println(ex.getMessage());structureMatches = false;}
		}
		if (!structureMatches) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.IMPORT_MAPPING_SET__MAPPING_SET_STRUCTURE_MATCHES,
						 errorMess,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * For every element or attribute in the importing structure, there should be 
	 * a corresponding element or attribute (with same name and multiplicity)
	 * in the imported structure. 
	 * 
	 * If there are extra nodes in the imported structure, even with mappings on them, 
	 * it is allowed; the XOReader will just not find the nodes in the instance.
	 * Done by recursive descent, always extending the error message
	 * @param importingStructure
	 * @param importedStructure
	 * @param errorMess
	 */
	private String detailedStructureMatch(ElementDef importing,ElementDef imported, String errorMess)
	{
		for (Iterator<ElementDef> it = importing.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef childing = it.next();
			ElementDef childed = imported.getNamedChildElement(childing.getName());
			if (childed == null)
				{errorMess = errorMess + "missing Element '" + childing.getName() + "'; ";}
			else
			{
				String multing = "[" + childing.getMinMultiplicity().getLiteral() 
						+ "," + childing.getMaxMultiplicity().getLiteral() + "]";
				String multed = "[" + childed.getMinMultiplicity().getLiteral() 
				+ "," + childed.getMaxMultiplicity().getLiteral() + "]";
				if (!multing.equals(multed))
					{errorMess = errorMess + ("multiplicity mismatch at Element '" + childing.getName() 
						+ "': " + multing + " vs " + multed + ";  "); }
				// only check further if the imported element has been expanded
				if (childed.isExpanded())
					errorMess = detailedStructureMatch(childing, childed, errorMess);				
			}
		}
		
		for (Iterator<AttributeDef> it = importing.getAttributeDefs().iterator(); it.hasNext();)
		{
			AttributeDef atting = it.next();
			AttributeDef atted = imported.getNamedAttribute(atting.getName());
			if (atted == null)
				{errorMess = errorMess + "missing Attribute '" + atting.getName() + "'; ";}
			else
			{
				String multing = atting.getMinMultiplicity().getLiteral(); 
				String multed = atted.getMinMultiplicity().getLiteral();
				if (!multing.equals(multed))
					{errorMess = errorMess + ("multiplicity mismatch at Attribute '" + atting.getName() 
						+ "': " + multing + " vs " + multed + ";  "); }
			}
		}
		return errorMess;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapperPackage.IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES:
				return ((InternalEList<?>)getParameterClassValues()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.IMPORT_MAPPING_SET__MAPPING_SET_URI:
				return getMappingSetURI();
			case MapperPackage.IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES:
				return getParameterClassValues();
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
			case MapperPackage.IMPORT_MAPPING_SET__MAPPING_SET_URI:
				setMappingSetURI((String)newValue);
				return;
			case MapperPackage.IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES:
				getParameterClassValues().clear();
				getParameterClassValues().addAll((Collection<? extends ParameterClassValue>)newValue);
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
			case MapperPackage.IMPORT_MAPPING_SET__MAPPING_SET_URI:
				setMappingSetURI(MAPPING_SET_URI_EDEFAULT);
				return;
			case MapperPackage.IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES:
				getParameterClassValues().clear();
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
			case MapperPackage.IMPORT_MAPPING_SET__MAPPING_SET_URI:
				return MAPPING_SET_URI_EDEFAULT == null ? mappingSetURI != null : !MAPPING_SET_URI_EDEFAULT.equals(mappingSetURI);
			case MapperPackage.IMPORT_MAPPING_SET__PARAMETER_CLASS_VALUES:
				return parameterClassValues != null && !parameterClassValues.isEmpty();
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
		result.append(" (mappingSetURI: ");
		result.append(mappingSetURI);
		result.append(')');
		return result.toString();
	}
	
	private MappedStructure importedMappingSet = null;
	
	/**
	 * cached implementation to return the imported mapping set
	 * @return the root MappedStructure node of the imported mapping set
	 */
	public MappedStructure getImportedMappingSet()
	{
		if (importedMappingSet == null) try
		{
			importedMappingSet = FileUtil.getImportedMappedStructure(this, getMappingSetURI());
		}
		catch (Exception ex) {} // leaves importedMappingSet null
		return importedMappingSet;
	}
	
	
	/**
	 * 
	 * @return the Element of this mapping set (parent of this node)
	 * which is doing the importing
	 */
	public ElementDef getImportingElement()
	{
		return (ElementDef)eContainer();
	}

} //ImportMappingSetImpl
