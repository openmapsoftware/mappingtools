/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Vector;



import com.openMap1.mapper.util.ClassModelMaker;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.converters.AbstractMapperWrapper;
import com.openMap1.mapper.converters.CSV_Wrapper;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.namespace;

import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.writer.MappedXMLWriter;
import com.openMap1.mapper.writer.XMLWriter;
import com.openMap1.mapper.writer.objectGetter;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.xsd.XSDSchema;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.StructureMismatch;
import com.openMap1.mapper.core.SchemaMismatch;
import com.openMap1.mapper.userConverters.DBConnect;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.structures.DBStructure;
import com.openMap1.mapper.structures.V2StructureDef;
import com.openMap1.mapper.structures.XSDStructure;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.ParameterClass;
import com.openMap1.mapper.ParameterClassValue;
import com.openMap1.mapper.StructureType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapped Structure</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getRootElement <em>Root Element</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getUMLModelURL <em>UML Model URL</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getStructureType <em>Structure Type</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getStructureURL <em>Structure URL</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getTopElementType <em>Top Element Type</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getTopElementName <em>Top Element Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getMappingParameters <em>Mapping Parameters</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.MappedStructureImpl#getParameterClasses <em>Parameter Classes</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MappedStructureImpl extends EObjectImpl implements MappedStructure {
	
	private StructureDefinition structureDefinition = null;
	
	private EPackage classModelRoot = null;
	
	private MapperWrapper wrapper = null;
	
	
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getRootElement() <em>Root Element</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRootElement()
	 * @generated
	 * @ordered
	 */
	protected ElementDef rootElement;

	/**
	 * The default value of the '{@link #getUMLModelURL() <em>UML Model URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * default UML model URI is not null
	 * <!-- end-user-doc -->
	 * @see #getUMLModelURL()
	 * @ordered
	 */
	protected static final String UML_MODEL_URL_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getUMLModelURL() <em>UML Model URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUMLModelURL()
	 * @generated
	 * @ordered
	 */
	protected String uMLModelURL = UML_MODEL_URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getStructureType() <em>Structure Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStructureType()
	 * @generated
	 * @ordered
	 */
	protected static final StructureType STRUCTURE_TYPE_EDEFAULT = StructureType.XSD;

	/**
	 * The cached value of the '{@link #getStructureType() <em>Structure Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStructureType()
	 * @generated
	 * @ordered
	 */
	protected StructureType structureType = STRUCTURE_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getStructureURL() <em>Structure URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStructureURL()
	 * @generated
	 * @ordered
	 */
	protected static final String STRUCTURE_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStructureURL() <em>Structure URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStructureURL()
	 * @generated
	 * @ordered
	 */
	protected String structureURL = STRUCTURE_URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getTopElementType() <em>Top Element Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopElementType()
	 * @generated
	 * @ordered
	 */
	protected static final String TOP_ELEMENT_TYPE_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getTopElementType() <em>Top Element Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopElementType()
	 * @generated
	 * @ordered
	 */
	protected String topElementType = TOP_ELEMENT_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getTopElementName() <em>Top Element Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopElementName()
	 * @generated
	 * @ordered
	 */
	protected static final String TOP_ELEMENT_NAME_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getTopElementName() <em>Top Element Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopElementName()
	 * @generated
	 * @ordered
	 */
	protected String topElementName = TOP_ELEMENT_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getMappingParameters() <em>Mapping Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappingParameters()
	 * @generated
	 * @ordered
	 */
	protected GlobalMappingParameters mappingParameters;

	/**
	 * The cached value of the '{@link #getParameterClasses() <em>Parameter Classes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterClasses()
	 * @generated
	 * @ordered
	 */
	protected EList<ParameterClass> parameterClasses;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MappedStructureImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.MAPPED_STRUCTURE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ElementDef getRootElement() {
		return rootElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRootElement(ElementDef newRootElement, NotificationChain msgs) {
		ElementDef oldRootElement = rootElement;
		rootElement = newRootElement;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT, oldRootElement, newRootElement);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRootElement(ElementDef newRootElement) {
		if (newRootElement != rootElement) {
			NotificationChain msgs = null;
			if (rootElement != null)
				msgs = ((InternalEObject)rootElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT, null, msgs);
			if (newRootElement != null)
				msgs = ((InternalEObject)newRootElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT, null, msgs);
			msgs = basicSetRootElement(newRootElement, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT, newRootElement, newRootElement));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUMLModelURL() {
		return uMLModelURL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUMLModelURL(String newUMLModelURL) {
		String oldUMLModelURL = uMLModelURL;
		uMLModelURL = newUMLModelURL;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__UML_MODEL_URL, oldUMLModelURL, uMLModelURL));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StructureType getStructureType() {
		return structureType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStructureType(StructureType newStructureType) {
		StructureType oldStructureType = structureType;
		structureType = newStructureType == null ? STRUCTURE_TYPE_EDEFAULT : newStructureType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__STRUCTURE_TYPE, oldStructureType, structureType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getStructureURL() {
		return structureURL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStructureURL(String newStructureURL) {
		String oldStructureURL = structureURL;
		structureURL = newStructureURL;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__STRUCTURE_URL, oldStructureURL, structureURL));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTopElementType() {
		return topElementType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTopElementType(String newTopElementType) {
		String oldTopElementType = topElementType;
		topElementType = newTopElementType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE, oldTopElementType, topElementType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTopElementName() {
		return topElementName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTopElementName(String newTopElementName) {
		String oldTopElementName = topElementName;
		topElementName = newTopElementName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_NAME, oldTopElementName, topElementName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GlobalMappingParameters getMappingParameters() {
		return mappingParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMappingParameters(GlobalMappingParameters newMappingParameters, NotificationChain msgs) {
		GlobalMappingParameters oldMappingParameters = mappingParameters;
		mappingParameters = newMappingParameters;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS, oldMappingParameters, newMappingParameters);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMappingParameters(GlobalMappingParameters newMappingParameters) {
		if (newMappingParameters != mappingParameters) {
			NotificationChain msgs = null;
			if (mappingParameters != null)
				msgs = ((InternalEObject)mappingParameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS, null, msgs);
			if (newMappingParameters != null)
				msgs = ((InternalEObject)newMappingParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS, null, msgs);
			msgs = basicSetMappingParameters(newMappingParameters, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS, newMappingParameters, newMappingParameters));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ParameterClass> getParameterClasses() {
		if (parameterClasses == null) {
			parameterClasses = new EObjectContainmentEList<ParameterClass>(ParameterClass.class, this, MapperPackage.MAPPED_STRUCTURE__PARAMETER_CLASSES);
		}
		return parameterClasses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that the class mode lcan be found
	 * <!-- end-user-doc -->
	 */
	public boolean canFindClassModel(DiagnosticChain diagnostics, Map<?,?> context) {
		classModelRoot = null; // in case it has been cached, and the file location changed.
		boolean canfindModel = false;
		try {canfindModel = (getClassModelRoot() != null);}
		catch (MapperException ex) {}
		if (!canfindModel) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.MAPPED_STRUCTURE__CAN_FIND_CLASS_MODEL,
						 ("Cannot find class model at " + getUMLModelURL()),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * If the mapped structure is an XSD, 
	 * check that the structure definition can  be found
	 * <!-- end-user-doc -->
	 */
	public boolean canFindStructureDefinition(DiagnosticChain diagnostics, Map<?,?> context) {
		structureDefinition = null; // in case it has been cached, and the file location changed
		boolean canFindStructure = true;
		// EMF may  throw a ResourceException if it can't find the resource
		try {canFindStructure = (getStructureDefinition() != null);}
		catch (Exception ex) {canFindStructure = false;} 
		if (!canFindStructure) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.MAPPED_STRUCTURE__CAN_FIND_STRUCTURE_DEFINITION,
						 ("Cannot find structure definition at " + getStructureURL()),
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
			case MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT:
				return basicSetRootElement(null, msgs);
			case MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS:
				return basicSetMappingParameters(null, msgs);
			case MapperPackage.MAPPED_STRUCTURE__PARAMETER_CLASSES:
				return ((InternalEList<?>)getParameterClasses()).basicRemove(otherEnd, msgs);
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
			case MapperPackage.MAPPED_STRUCTURE__NAME:
				return getName();
			case MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT:
				return getRootElement();
			case MapperPackage.MAPPED_STRUCTURE__UML_MODEL_URL:
				return getUMLModelURL();
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_TYPE:
				return getStructureType();
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_URL:
				return getStructureURL();
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE:
				return getTopElementType();
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_NAME:
				return getTopElementName();
			case MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS:
				return getMappingParameters();
			case MapperPackage.MAPPED_STRUCTURE__PARAMETER_CLASSES:
				return getParameterClasses();
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
			case MapperPackage.MAPPED_STRUCTURE__NAME:
				setName((String)newValue);
				return;
			case MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT:
				setRootElement((ElementDef)newValue);
				return;
			case MapperPackage.MAPPED_STRUCTURE__UML_MODEL_URL:
				setUMLModelURL((String)newValue);
				return;
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_TYPE:
				setStructureType((StructureType)newValue);
				return;
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_URL:
				setStructureURL((String)newValue);
				return;
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE:
				setTopElementType((String)newValue);
				return;
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_NAME:
				setTopElementName((String)newValue);
				return;
			case MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS:
				setMappingParameters((GlobalMappingParameters)newValue);
				return;
			case MapperPackage.MAPPED_STRUCTURE__PARAMETER_CLASSES:
				getParameterClasses().clear();
				getParameterClasses().addAll((Collection<? extends ParameterClass>)newValue);
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
			case MapperPackage.MAPPED_STRUCTURE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT:
				setRootElement((ElementDef)null);
				return;
			case MapperPackage.MAPPED_STRUCTURE__UML_MODEL_URL:
				setUMLModelURL(UML_MODEL_URL_EDEFAULT);
				return;
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_TYPE:
				setStructureType(STRUCTURE_TYPE_EDEFAULT);
				return;
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_URL:
				setStructureURL(STRUCTURE_URL_EDEFAULT);
				return;
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE:
				setTopElementType(TOP_ELEMENT_TYPE_EDEFAULT);
				return;
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_NAME:
				setTopElementName(TOP_ELEMENT_NAME_EDEFAULT);
				return;
			case MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS:
				setMappingParameters((GlobalMappingParameters)null);
				return;
			case MapperPackage.MAPPED_STRUCTURE__PARAMETER_CLASSES:
				getParameterClasses().clear();
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
			case MapperPackage.MAPPED_STRUCTURE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MapperPackage.MAPPED_STRUCTURE__ROOT_ELEMENT:
				return rootElement != null;
			case MapperPackage.MAPPED_STRUCTURE__UML_MODEL_URL:
				return UML_MODEL_URL_EDEFAULT == null ? uMLModelURL != null : !UML_MODEL_URL_EDEFAULT.equals(uMLModelURL);
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_TYPE:
				return structureType != STRUCTURE_TYPE_EDEFAULT;
			case MapperPackage.MAPPED_STRUCTURE__STRUCTURE_URL:
				return STRUCTURE_URL_EDEFAULT == null ? structureURL != null : !STRUCTURE_URL_EDEFAULT.equals(structureURL);
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE:
				return TOP_ELEMENT_TYPE_EDEFAULT == null ? topElementType != null : !TOP_ELEMENT_TYPE_EDEFAULT.equals(topElementType);
			case MapperPackage.MAPPED_STRUCTURE__TOP_ELEMENT_NAME:
				return TOP_ELEMENT_NAME_EDEFAULT == null ? topElementName != null : !TOP_ELEMENT_NAME_EDEFAULT.equals(topElementName);
			case MapperPackage.MAPPED_STRUCTURE__MAPPING_PARAMETERS:
				return mappingParameters != null;
			case MapperPackage.MAPPED_STRUCTURE__PARAMETER_CLASSES:
				return parameterClasses != null && !parameterClasses.isEmpty();
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
		result.append(" (name: ");
		result.append(name);
		result.append(", uMLModelURL: ");
		result.append(uMLModelURL);
		result.append(", structureType: ");
		result.append(structureType);
		result.append(", structureURL: ");
		result.append(structureURL);
		result.append(", topElementType: ");
		result.append(topElementType);
		result.append(", topElementName: ");
		result.append(topElementName);
		result.append(')');
		return result.toString();
	}

	/**
	 * Set a structure definition, and add its namespaces to the mapping parameters
	 */
	public void setStructureDefinition(StructureDefinition structureDefinition) 
	{
		this.structureDefinition = structureDefinition;
		getMappingParameters().addNamespaces(structureDefinition);
	}

	/**
	 * Get a Structure Definition, cache it and return it - or return null if none can be found
	 */
	public StructureDefinition getStructureDefinition() throws MapperException
	{
		if (structureDefinition == null) try
		{
			// XML schema or csv structure definition
			if (getStructureType()== StructureType.XSD)
			{
				String uriString = getStructureURL();
				if ((uriString != null) && !(uriString.equals("")))
				{
					if (uriString.endsWith("xsd"))
					{
						URI uri = URI.createURI(uriString);
						XSDSchema theSchema = XSDStructure.getXSDRoot(uri);
						if (theSchema != null) 
						{
							structureDefinition = new XSDStructure(theSchema);
						}						
					}
					else if (uriString.endsWith("csv"))
					{
						Object input = null;
						try
						{
							input = new FileInputStream(new File(uriString));			
						}
						catch (Exception ex)
							{throw new MapperException("Cannot open csv file at '" + uriString + "'");}
						CSV_Wrapper csvStructure = new CSV_Wrapper(this,null);
						csvStructure.getStructure(input);
						structureDefinition = csvStructure;
					}
				}
			}
			
			// V2 structure definition
			if (getStructureType()== StructureType.V2)
			{
				String uriString = getStructureURL();
				if ((uriString != null) && !(uriString.equals("")))
				{
					URI uri = URI.createURI(uriString);
					Element mwbRoot = XMLUtil.getRootElement(uri);
					structureDefinition = new V2StructureDef(mwbRoot);
				}
			}
			
			// RDBMS structure definition; cannot get structure definition, as there is no user name and password
			else if (getStructureType() == StructureType.RDBMS)
			{
			}
		}
		catch (Exception ex) 
		{
			System.out.println(ex.getMessage());
			structureDefinition = null;
			throw new MapperException("Cannot find structure definition: " + ex.getMessage());
		}
		return structureDefinition;
	}

	
	/**
	 * connect to a relational database and return its structure definition
	 * @param userName
	 * @param password
	 * @return
	 * @throws MapperException
	 */
	public StructureDefinition connectToRDB(String userName, String password) throws MapperException
	{
		if (getStructureType() == StructureType.RDBMS) try
		{
			String jdbcConnectString = getStructureURL();
			if ((jdbcConnectString != null) && !(jdbcConnectString.equals("")))
			{
				DBConnect dbConnect = new DBConnect(jdbcConnectString,userName,password,null);
				dbConnect.connect();
				structureDefinition = new DBStructure(dbConnect.con());
			}
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			throw new MapperException("Cannot connect to Relational database: " + ex.getMessage());
		}
		return structureDefinition;		
	}
	
	/**
	 * Set the class model root
	 */
	public void setClassModelRoot(EPackage classModelRoot) 
		{this.classModelRoot = classModelRoot;}

	/**
	 * Get the class model root - from the resource if necessary
	 */
	public EPackage getClassModelRoot() throws MapperException
	{
		// if the root has not yet been got (e.g. when the editor was opened) get it
		if (classModelRoot == null)
		{
			if ((getUMLModelURL() != null) && !(getUMLModelURL().equals("")))
			{
				try {
					URI classModelURI = URI.createURI(getUMLModelURL());
					/* if not running in Eclipse, assume that the Eclipse project folder
					 * structure defines the relative location of the class model and the mapped structure */
					if (!FileUtil.isInEclipse())
					{
						String mappingLocation = eResource().getURI().toString();
						String classModelLocation = FileUtil.ecoreFileLocation(mappingLocation, getUMLModelURL());
						classModelURI = URI.createURI("file:/" + classModelLocation);
					}
					EObject umlRoot = ClassModelMaker.makeClassModelFromFile(classModelURI);
					if (umlRoot instanceof EPackage) setClassModelRoot((EPackage)umlRoot);
				}
				catch (Exception ex) 
				 {
					ex.printStackTrace();
					throw new MapperException("Exception getting class model root: " + ex.getMessage());
				 } // class model root remains null, so caught below
			}
		}
		return classModelRoot;
	}
	
	
	/**
	 * @return name of the file containing the class model
	 */
	public String getClassModelFileName()
	{
		String res = "'undefined'";
		if (getUMLModelURL() != null)
		{
			StringTokenizer st = new StringTokenizer(getUMLModelURL(),"/\\");
			while (st.hasMoreTokens()) res = st.nextToken();
		}
		return res;
	}

	/**
	 * @return name of the file containing the structure to be mapped
	 */
	public String getStructureFileName()
	{
		String res = "'undefined'";
		if (getStructureURL() != null)
		{
			StringTokenizer st = new StringTokenizer(getStructureURL(),"/\\");
			while (st.hasMoreTokens()) res = st.nextToken();			
		}
		return res;
	}
	
	/**
	 * @param path String form of a descending path, starting with "/"
	 * @return the Element or Attribute reached by that path; or null if there is none.
	 */
	public NodeDef getNodeDefByPath(String path)
	{
		if (path == null) return null;
		if (!path.startsWith("/")) return null;
		StringTokenizer st = new StringTokenizer(path,"/");
		String first = st.nextToken();
		ElementDef root = getRootElement();
		if (root.getName().equals(first))
		{
			if (path.length() == first.length() + 1) return root;
			else return root.getDescendantByPath(path.substring(first.length() + 1));			
		}
		else return null;
	}
	
	//---------------------------------------------------------------------------------------------
	//						 validating XML Instances against the mapped structure
	//---------------------------------------------------------------------------------------------

	
	/**
	 * check that the XML instance with this root conforms to the 
	 * mapped structure; return a list of mismatches
	 * @param rootEl root element of the document being checked
	 * @return Vector of all structure mismatches detected (duplicates not removed)
	 * @throws MapperException eg if the document associates a namespace URI with more than one prefix,
	 * (XML allows this but I don't yet; I could do) or mentions a namespace URI not known in the mapping set
	 */
	public Vector<StructureMismatch> checkInstance(Element rootEl) throws MapperException
	{
		Vector<StructureMismatch> mismatches = new Vector<StructureMismatch>();
		/* If there is a java mapping class, then that should make some checks (not yet implemented)
		 * but meanwhile, normal checks of the instance, such as two namespaces with the same prefix, are not appropriate */
		if ((mappingParameters != null) 
				&& (mappingParameters.getMappingClass() != null) 
				&& (!mappingParameters.getMappingClass().equals("")))
		{
			/* FIXME - make the java mapping class interface support some checks. */
		}
		else
		{
			NamespaceSet instanceNSSet = XMLUtil.getNameSpaceSet(rootEl);
			checkInstance(rootEl,true,"",instanceNSSet,mismatches);			
		}
		return mismatches;
	}
	
	
	/**
	 * check that the XML instance with this root conforms to the 
	 * mapped structure; build a list of mismatches where it does not
	 * @param rootEl
	 * @param isRoot true if this mapping set applies from the root of the document
	 * @param path if isRoot is true, ignored. If isRoot is false, the path from the root 
	 * of the document, with no final '/'
	 * @param mismatches Vector of StructureMismatch objects, to be built up.
	 * @throws MapperException eg if the document associates a namespace URI with more than one prefix,
	 * (XML allows this but I don't yet; I could do) or mentions a namespace URI not known in the mapping set
	 */
	public void checkInstance(Element rootEl, boolean isRoot, String path, NamespaceSet instanceNSSet,
			Vector<StructureMismatch> mismatches) 
	throws MapperException
	{
		// if the mapping set uses a Java class, do not try to match the structure
		if ((getMappingParameters() != null) && 
				!(getMappingParameters().getMappingClass().equals(""))) return;
		
		// only check the element name at the top of the mapping set if it is not an imported mapping set
		String prefixConvertedName = convertToMappedStructurePrefix(rootEl.getNodeName(),instanceNSSet,false);
		if ((isRoot) && (!(prefixConvertedName.equals(getRootElement().getName()))))
			mismatches.add(new StructureMismatch(path,StructureMismatch.STRUCTURE_UNEXPECTED_NAME,
					getRootElement().getName(),rootEl.getNodeName()));
		
		String thePath = path;
		if (isRoot) thePath = "/" + getRootElement().getName();
		checkElementMatch(rootEl,getRootElement(),thePath,instanceNSSet, mismatches);
	}
	
	/**
	 * recursive descent of elements in an XML instance, checking that they match the mapped 
	 * structure
	 * @param el Element in the instance
	 * @param edd corresponding ElementDef in the mapped structure; its name has already been checked
	 * @param path string form of XPath from the root of the document
	 * @param instanceNSSet namespaces, with prefixes as in the instance
	 * @param mismatches Vector of all structural mismatches, being built up
	 * @throws MapperException if the instance mentions a namespace with URI not found in the mapping set
	 */
	private void checkElementMatch(Element el, ElementDef edd, String path, NamespaceSet instanceNSSet,
			Vector<StructureMismatch> mismatches)
			throws MapperException
	{
		// For checking multiplicity. key = prefixed name, as in the mapped structure; value = 'one', 'many'
		Hashtable<String,String> elementOccs = new Hashtable<String,String>();
		Hashtable<String,String> attributeOccs = new Hashtable<String,String>();

		if (!edd.isExpanded())
		{
			// if the Element imports a mapping set, check the structure against that
			if (edd.getImportMappingSet() != null)
			{
				MappedStructure ms = edd.getImportMappingSet().getImportedMappingSet();
				if (ms != null ) ms.checkInstance(el, false, path,instanceNSSet, mismatches);
				else mismatches.add(new StructureMismatch(path,StructureMismatch.STRUCTURE_MISSING_IMPORTED_MAPPING_SET,
						edd.getImportMappingSet().getMappingSetURI(),""));
			}
			/* if the Element definition has not been expanded, 
			 * and does not import a mapping set, it cannot be checked;do nothing */
		}

		// make all checks on this Element before any recursive descent
		else if (edd.isExpanded())
		{
			// check for Elements not in the structure
			for (int i = 0; i < el.getChildNodes().getLength(); i++)
			{
				Node nd = el.getChildNodes().item(i);
				// convert namespace prefixes to those used in the mapping set 
				String name = convertToMappedStructurePrefix(nd.getNodeName(),instanceNSSet,false);
				if (nd instanceof Element)
				{
					// check there is a child of this name in the structure
					if (edd.getNamedChildElement(name) == null)
						mismatches.add(new StructureMismatch(path,StructureMismatch.STRUCTURE_UNEXPECTED_NAME,"",name));
					// if there is such a child, record that the instance has one or many
					else if (elementOccs.get(name) == null) elementOccs.put(name,"one");
					else if (elementOccs.get(name).equals("one")) elementOccs.put(name,"many");
				}
			}
			
			// check for attributes not in the structure; allow namespace attributes
			for (int i = 0; i < el.getAttributes().getLength();i++)
			{
				Node nd = el.getAttributes().item(i);
				boolean isNamespaceAttribute = ((nd.getNodeName().equals("xmlns"))|(nd.getNodeName().startsWith("xmlns:")));
				if ((nd instanceof Attr) && !(isNamespaceAttribute))
				{
					String name = convertToMappedStructurePrefix(nd.getNodeName(),instanceNSSet,true);
					// check there is an attribute of this name in the structure
					if (edd.getNamedAttribute(name) == null)
						mismatches.add(new StructureMismatch(path,StructureMismatch.STRUCTURE_UNEXPECTED_NAME,"",name));
					// if there is such an attribute, record that the instance has it
					else attributeOccs.put(name,"one");
				}
			}
			
			// check multiplicities of child elements 
			for (Iterator<ElementDef> it = edd.getChildElements().iterator(); it.hasNext();)
			{
				ElementDef cd = it.next();
				String name = cd.getName();
				String occs = elementOccs.get(name);
				if ((cd.getMinMultiplicity() == MinMult.ONE) && (occs == null))
					mismatches.add(new StructureMismatch(path,StructureMismatch.STRUCTURE_MISSING,name,""));
				if ((cd.getMaxMultiplicity() == MaxMult.ONE) && (occs != null) && (occs.equals("many"))) 
					mismatches.add(new StructureMismatch(path,StructureMismatch.STRUCTURE_REPEATED,name,""));
			}
			
			/* check existence of required attributes.
			 * In structure definitions, 'xsi:type' is given min  multiplicity 1 so that 
			 * mappings can depend on it and generation will work; but since it does not 
			 * a required attribute, there should be no warning  when it is missing. */
			
			for (Iterator<AttributeDef> it = edd.getAttributeDefs().iterator(); it.hasNext();)
			{
				AttributeDef ad = it.next();
				if ((ad.getMinMultiplicity() == MinMult.ONE) 
						&& (attributeOccs.get(ad.getName())== null)
						&& (!(ad.getName().equals("xsi:type"))))
					mismatches.add(new StructureMismatch(path,StructureMismatch.STRUCTURE_MISSING,ad.getName(),""));
			}
			
			// recursive descent of the instance tree
			for (int i = 0; i < el.getChildNodes().getLength(); i++)
			{
				Node nd = el.getChildNodes().item(i);
				String name = nd.getNodeName();
				String newPath = path + "/" + name;
				if ((nd instanceof Element) && (edd.getNamedChildElement(name) != null))
					checkElementMatch((Element)nd,edd.getNamedChildElement(name),newPath,instanceNSSet,mismatches);
			}
			
		}
	}
	
	
	
	/** if an element or attribute name in an XML instance has a prefix, 
	 * convert the prefix to that used in the mapping set
	 * @param name the name, with prefix as in the instance
	 * @param instanceNSSet the namespace URIs and prefixes used in the instance
	 * @param isAttribute true if this is an attribute name
	 * @return the name with prefix as in the mapping set
	 * @throws MapperException if a namespace URI in the instance cannot be matched in hte mapping set
	 */
	private String convertToMappedStructurePrefix(String name, NamespaceSet instanceNSSet, boolean isAttribute) throws MapperException
	{
		String convName = name;
		StringTokenizer st = new StringTokenizer(name,":");
		// if there is no prefix
		if (st.countTokens() == 1)
		{
			// a namespace with no prefix may not exist; if so, do nothing
			namespace noPrefix = instanceNSSet.getByPrefix("");
			// if an attribute name has no prefix, it is in no namespace; so do not try to convert it
			if ((noPrefix != null) && !isAttribute)
			{
				String prefix = getMappingSetPrefix(noPrefix.URI());
				if (prefix == null) throw new MapperException("Cannot find namespace with URI '"
						+ noPrefix.URI() + "' in mapping set " + getMappingSetName());
				convName = addPrefix(prefix,st.nextToken());
			}
		}
		else if (st.countTokens() == 2)
		{
			namespace withPrefix = instanceNSSet.getByPrefix(st.nextToken());
			if (withPrefix != null)
			{
				String prefix = getMappingSetPrefix(withPrefix.URI());
				if (prefix == null) throw new MapperException("Cannot find namespace with URI '"
						+ withPrefix.URI() + "'");
				convName = addPrefix(prefix,st.nextToken());
			}			
		}
		return convName;
	}
	
	/**
	 * @param uri the uri of  namespace
	 * @return the prefix used for this URI in the mapping set; or null if the URI is not found
	 */
	private String getMappingSetPrefix(String uri)
	{
		String prefix = null;
		if (getMappingParameters() != null)
		for (Iterator<Namespace> it = getMappingParameters().getNameSpaces().iterator();it.hasNext();)
		{
			Namespace ns = it.next();
			if (ns.getURL().equals(uri)) prefix = ns.getPrefix();
		}
		return prefix;
	}
	
	/**
	 * add a prefix to a local element or attribute name
	 * @param prefix; must not be null. "" if there is no prefix.
	 * @param localName
	 * @return the prefixed name
	 */
	private String addPrefix(String prefix, String localName)
	{
		if (prefix.equals("")) return localName;
		return (prefix + ":" + localName);
	}
	
	//---------------------------------------------------------------------------------------------
	//						 validating XML Instances against an XML schema
	//---------------------------------------------------------------------------------------------
	
	/**
	 * validate an XML instance against the schema which defines this mapped structure,
	 * if there is such a schema. If there is no schema, return an empty list.
	 * (Not yet tested outside Eclipse)
	 * @param the root element of the XML instance
	 * @return a Vector of SchemaMismatch objects, which wrap org.eclipse.emf.ecore.util.Diagnostic
	 */
	public Vector<SchemaMismatch> schemaValidate(Element instanceRoot) throws MapperException
	{
		Vector<SchemaMismatch> mismatches = new Vector<SchemaMismatch>();
		// if there is no XML schema, return an empty list
		if ((getStructureType() == StructureType.XSD) && (getStructureURL() !=  null) && (!getStructureURL().equals(""))) 
		{
			URI schemaURI = URI.createURI(getStructureURL());
			mismatches = XMLUtil.schemaValidate(instanceRoot, schemaURI);
		}
		return mismatches;
	}
	
	
	//---------------------------------------------------------------------------------------------
	//						           the class model view
	//---------------------------------------------------------------------------------------------

	private boolean classModelViewIsRefreshed = false;
	
	/**
	 * @return true if the set of mappings visible to the class model view had been refreshed since
	 * this mapping set was opened with an editor, or since a mapping was added or removed
	 */
	public boolean classModelViewIsRefreshed() {return classModelViewIsRefreshed;}
	
	/**
	 * set the variable which defines the result of classModelViewIsRefreshed; 
	 * It is false when this mapping set is created, and should be set false when
	 * any mappings are edited.
	 * set it true when the class model view is refreshed
	 * @param fresh
	 */
	public void setClassModelViewIsRrefreshed(boolean fresh)
		{classModelViewIsRefreshed = fresh;}
	
	//--------------------------------------------------------------------------------------
	//                         mappings to database structures
	//---------------------------------------------------------------------------------------

	/**
	 * set a database Structure
	 */
	public void setDBStructure(DBStructure dbStructure)
	{
		setTopElementName("database");
		setRootElement(dbStructure.getDatabaseStructure());
	}
	
	//--------------------------------------------------------------------------------------
	//                         for translation
	//---------------------------------------------------------------------------------------
	
	/**
	 * If true, the XML writing procedures which were last created
	 * are still current and are in the location writeProceduresURI()  -
	 * so they do not need to be re-created before running a trnalsation or generating 
	 * XSLT
	 * @return  true if the mapping set has not been edited since the write procedures were created
	 */
	public boolean hasCurrentWriteProcedures() {return false;}
	
	
	/**
	 * @return  the URI of the XML writing pporcedures, if they exist - or "" if they do not
	 */
	public String writeProceduresURI() {return("");}
	
	
	/**
	 * @return all the mapping sets imported by any Element in this mapping set.
	 * key = URI string of the mapping set
	 * value = the mapping set
	 */
	public Hashtable<String,MappedStructure> getDirectImportedMappingSets()
	{
		Hashtable<String,MappedStructure> impSets = new Hashtable<String,MappedStructure>();
		for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(this, 
					MapperPackage.Literals.IMPORT_MAPPING_SET).iterator();it.hasNext();)
		{
			ImportMappingSet ims = (ImportMappingSet)it.next();
			String path = ims.getMappingSetURI().toString();
			if (impSets.get(path) == null)
			{
				MappedStructure ms = ims.getImportedMappingSet();
				if (ms != null) impSets.put(path, ms);	
				else System.out.println("Found no imported mapping set at "+ path);
			}
		}
		return impSets;
	}
	
	private Hashtable<String,MappedStructure> impMSByCSet = new Hashtable<String,MappedStructure>();
	private Hashtable<String,ImportMappingSet> impMSObjectByCSet = null;

	/**
	 * @param cSet a (class, subset)
	 * @return if there is an imported mapping set for the (class,subset) return it;
	 * otherwise return null
	 */
	public MappedStructure getDirectImportedMappingSet(ClassSet cSet) throws MapperException
	{
		MappedStructure imported = impMSByCSet.get(cSet.stringForm());
		if (imported == null)
		{
			// first call; set up table of imported mapping set URLs
			if (impMSObjectByCSet == null)
			{
				impMSObjectByCSet = new Hashtable<String,ImportMappingSet>();
				for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(this, 
						MapperPackage.Literals.IMPORT_MAPPING_SET).iterator();it.hasNext();)
				{
					ImportMappingSet ims = (ImportMappingSet)it.next();
					String path = ims.getMappingSetURI().toString();
					List<ParameterClassValue> pvList = ims.getParameterClassValues();
					if (pvList.size() != 1) throw new MapperException("Imported mapping set at '" 
							+ path + "' has " + pvList.size() + " parameter class values, but should have 1.");
					ParameterClassValue pv = pvList.get(0);
					ClassSet cs = pv.getClassSet();
					impMSObjectByCSet.put(cs.stringForm(), ims);
				}
			} 
			
			// all calls; see if there is an imported mapping set for this class set
			ImportMappingSet imscs = impMSObjectByCSet.get(cSet.stringForm());
			if (imscs != null)
			{
				imported = imscs.getImportedMappingSet();
				impMSByCSet.put(cSet.stringForm(),imported);
			}
		}
		return imported;
	}
	
	/**
	 * @return all the mapping sets imported directly or indirectly by this
	 * mapping set, including itself, with no duplicates
	 * key = URI string of the mapping set
	 * value = the mapping set
	 */
	public Hashtable<String,MappedStructure> getAllImportedMappingSets()
	{
		Hashtable<String,MappedStructure> allSets = new Hashtable<String,MappedStructure>();
		recordMappingSets(this,allSets);
		return allSets;
	}
	
	/**
	 * record this mapping set and all those it imports,
	 * directly or indirectly, removing duplicates
	 * @param mappedStructure start mapping set
	 * @param mappingSets built up to record all the mapping sets
	 */
	private void recordMappingSets(MappedStructure mappedStructure,
			Hashtable<String,MappedStructure> mappingSets)
	{
		String location = mappedStructure.eResource().getURI().toString();
		// cut off recursion if the mapping set is recorded already - avoid infinite loops
		if (mappingSets.get(location) == null)
		{
			mappingSets.put(location, mappedStructure);
			for (Enumeration<MappedStructure> en = mappedStructure.getDirectImportedMappingSets().elements();en.hasMoreElements();)
			{
				MappedStructure ms = en.nextElement();
				recordMappingSets(ms,mappingSets);
			}
		}
	}

	
	/**
	 * @return the root Element of the write procedures file which was made from this
	 * MappedStructure (its location and name are known automatically from the 
	 * location and name of this resource)
	 * @throws MapperException if the Procedures file cannot be found
	 */
	public Element procedureFileRoot() throws MapperException
	{
		// find the file location of this resource
		URI uri = eResource().getURI();
		String location = uri.toString();
		if (FileUtil.isInEclipse())
			location = FileUtil.editURIConverter().normalize(uri).toString();
		
		// calculate the location of the procedures file
		String procLocation = FileUtil.wProcLocation(location);
		// return its root Element
		return XMLUtil.getRootElement(procLocation);
	}

	
	/**
	 * For use only inside Eclipse; outside Eclipse returns true.
	 * @return true if this mapping set has been edited since the last
	 * compilation (creation of the WProc file), or if the WProc file does not exist.
	 * 
	 * When projects are imported into a namespace, I assume the wproc files are up to date;
	 * but the timestamps will be set by the import process and are equal to those of the mapper files.
	 * 
	 * So a wproc file is considered up to date if its last modified time is up
	 * to 10 seconds before the last modified time of the mapper file - i.e I
	 * assume it takes at least 10 seconds to modify a mapper file.
	 */
	public boolean hasChangedSinceCompile()
	{
		// if there is a java mapping class, you never need a wproc file
		if ((getMappingParameters() != null) 
				&& (getMappingParameters().getMappingClass() != null)
				&& (!getMappingParameters().getMappingClass().equals(""))) return false;
				
		// if not in eclipse, get one anyway
		if (!FileUtil.isInEclipse()) return true;
		
		// bug here comparing getTimeStamp() (for a resource) to getLocalTimeStamp() (for an Ifile)
		long wpDate = EclipseFileUtil.wProcFileDate(this);
		if (wpDate == 0) return true; // when the WProc file does not exist
		long mapperDate = eResource().getTimeStamp();
		// the wproc file is allowed to be up to 10 seconds older than the mapper file
		int timeLag = 10000;
		return (mapperDate > wpDate + timeLag);
	}
	
	
	/**
	 * @return the name of the mapping set
	 */
	public String getMappingSetName()
	{
		String name = "";
		String url = eResource().getURI().toString();
		StringTokenizer st = new StringTokenizer(url,"/\\");
		while(st.hasMoreTokens()) name = st.nextToken();
		return name;
	}
	
	/**
	 * Get a special mapping class, givne its name.
	 * Usually use Class.forNameI(), but for the FHIR mapping class, use the classLoader of the plugin
	 * @param javaClassName
	 * @return
	 * @throws MapperException
	 */
	private Class<?> getMappingClass(String javaClassName) throws MapperException
	{
		Class<?> theClass = null;
		
        // special case for the FHIR Mapping class; use the class loader of the FHIR plugin
        if (javaClassName.equals("com.openMap1.mapper.fhir.FHIRMapper"))
        {
        	Bundle bundle = Platform.getBundle("com.openMap1.mapper.fhir");
        	if (bundle != null)
        	{
        		try {theClass = bundle.loadClass(javaClassName);}
    	        catch (ClassNotFoundException ex)
	            	{throw new MapperException("FHIR plugin cannot load mapping class '" + javaClassName + "': " + ex.getMessage());}
        	}
        	else if (bundle == null) throw new MapperException("FHIR plugin is not present in Eclipse.");
        }
        
        // other mapping classes; try to load by Class.forName(..)
        else 
        {
        	try {theClass = Class.forName(javaClassName);}
        	catch (ClassNotFoundException ex)
            	{throw new MapperException("Failed to find mapping class '" + javaClassName + "': " + ex.getMessage());}
        }
        
		return theClass;
	}

	/**
	 * return the XML reader for the mapping set  - 
	 * either an MDLXOReader from the mappings, or a 
	 * instance of a supplied Java class implementing XOReader
	 * @param dataSource the source of information to be read in
	 * @param classModelRoot (optional) root of the class model; 
	 * if null the MappedStructure will attempt to find te class model root for itself
	 * (make it non-null for standalone applications, where the platform:resource URL 
	 * used in the mapping set will not work) 
	 */
	public XOReader getXOReader(Object dataSource, EPackage classModelRoot, messageChannel mc) throws MapperException
	{
		XOReader reader = null;
		Object instance = null;
		
		/* if the class model root is non-null, use it to override whatever 
		 * the mapping set would try to find (useful for standalone applications, where
		 * the mapping set may try to use an Eclipse platform: resource URL) */
		if (classModelRoot == null) classModelRoot = getClassModelRoot();
		else setClassModelRoot(classModelRoot);
		
		String mappingClass = null;
		if  (  (getMappingParameters() != null) && 
				(!getMappingParameters().getMappingClass().equals("")))
			mappingClass = getMappingParameters().getMappingClass();
		
		
		// if the mapped structure defines a different Java class to read XML, as if through mappings; make an instance of the class
		if (mappingClass != null)
		{
			// make the class
			String javaClassName = mappingClass;
	        Class<?> theClass = getMappingClass(javaClassName);
	        
	        
	        /* make an instance of the class; assume the class 
	         * has two constructors - the first with four Parameters, 
	         * which are the same as the MDLXOReader constructor below */
	        String stage = "";
	        int arg = 0;
	        try
	        {
		        Constructor<?>[] constructors = theClass.getConstructors();	
		        // try all constructors, looking for one with 4 arguments
		        boolean foundConstructor = false;
		        for (int c = 0; c < constructors.length;c++)
		        {
			        Class<?>[] parameterTypes = constructors[c].getParameterTypes();
			        if (parameterTypes.length == 4) 
			        {
			        	foundConstructor = true;
			        	stage = " class cast";
				        // the next 4 calls will throw a class cast exception if the argument types do not match
			        	arg = 1;parameterTypes[0].cast(dataSource); 
			        	arg = 2;parameterTypes[1].cast(this);
			        	arg = 3;parameterTypes[2].cast(classModelRoot);
			        	arg = 4;parameterTypes[3].cast(mc);
			        	

				        stage = " set";
			        	Object[] initArgs = new Object[4];
			        	arg = 1;initArgs[0] = dataSource;
			        	arg = 2;initArgs[1] = this;
			        	arg = 3;initArgs[2]= classModelRoot;
			        	arg = 4;initArgs[3]= mc;

			        	
			        	for (int i = 0; i < 4;i++)
			        	{
			        		boolean nonull = (initArgs[i] != null);
			        	}

				        stage = " make instance";
				        arg = 0;
			        	instance = constructors[c].newInstance(initArgs);			        	
			        }		        	
		        }
		        if (!foundConstructor) throw new MapperException("Found no constructor with 4 parameters");
	        }
	        catch (InvocationTargetException ex)
	        {
	        	Exception cause = (Exception)ex.getCause();
	        	cause.printStackTrace();
	        	Exception target  = (Exception)ex.getTargetException();
	        	System.out.println("Mapper class exception: " + cause.getMessage());
	        	throw new MapperException("Mapper class exception at stage " + stage + ": " + cause.getMessage() + ";" + target.getMessage());
	        }
	        catch (Exception ex)
            {
	        	System.out.println("Mapper class Exception");
	        	ex.printStackTrace();
	        	throw new MapperException("Failed to make reader instance of mapping class '" + javaClassName 
	        			+ "': at stage " + stage + ", argument " + arg + ": " + ex.getMessage());
	        }
	        
	        // check that the class implements XOReader
	        if (instance == null)
            	{throw new MapperException("Null reader instance of mapping class '" + javaClassName + "'");}
	        if (instance instanceof XOReader) reader = (XOReader)instance;
	        else throw new MapperException("Supplied mapping class '" + javaClassName + "' does not implement the XOReader interface." );
		}
		
		// more usual case - a mapped MDLXOReader
		else
		{			
			// check the class of the data source; but it can be null, for RDBMS sources
			if ((dataSource != null) && 
			    (!(dataSource instanceof Element))) throw new MapperException("Data source for mappings is not an XML Element");
						
			reader = new MDLXOReader((Element)dataSource,this,classModelRoot,mc);			
		}
		return reader;
	}
	
	
	/**
	 * @param oGet an objectGetter which gives the XMLWriter the object to be written to XML
	 * @param classModelRoot (optional) root of the class model. If null, this
	 * mappedStructure will attempt to find the class model root from its URL.
	 * (supply the class model root for standalone applications, where the platform:resource URL in the 
	 * mapping set may not work)
	 * @param mc message channel for error messages
	 * @return the XML Writer for the mapping set - either a MappedXMLWriter
	 * using the mappings, or an instance of a supplied Java class implementing XMLWriter
	 * @throws MapperException
	 */
	public XMLWriter getXMLWriter(objectGetter oGet,EPackage classModelRoot, messageChannel mc, boolean runTracing) throws MapperException
	{
		XMLWriter writer = null;
		Object instance = null;
		Boolean doTracing = new Boolean(runTracing);
		
		/* if a non-null class model root has been supplied, make it override whatever
		 * the mapping set may try to find (mainly for standalone applications,
		 * where the platform:resource URL in the mapping set will not work */
		if (classModelRoot == null) classModelRoot = getClassModelRoot();
		else  setClassModelRoot(classModelRoot);

		// if the mapped structure defines a Java class to read XML, as if through mappings
		if (    (getMappingParameters() != null) && 
				(!getMappingParameters().getMappingClass().equals("")))
		{
			// make the class
			String javaClassName = getMappingParameters().getMappingClass();
	        Class<?> theClass = getMappingClass(javaClassName);
	        
	        /* make an instance of the class; try all constructors,
	         * looking for one with with five Parameters, 
	         * which are the same as the MappedXMLWriter constructor below */
	        String stage = "";
	        int arg = 0;
	        try
	        {
		        Constructor<?>[] constructors = theClass.getConstructors();	
		        boolean constructorFound = false;
		        for (int c = 0; c < constructors.length;c++)
		        {
			        Class<?>[] parameterTypes = constructors[c].getParameterTypes();
			        if (parameterTypes.length == 5)
			        {
			        	constructorFound = true;
			        	stage = " class cast";
				        // the next 5 calls will throw a class cast exception if the argument types do not match
				        arg = 1; parameterTypes[0].cast(oGet); 
				        arg = 2;parameterTypes[1].cast(this);
				        arg = 3;parameterTypes[2].cast(classModelRoot);
				        arg = 4;parameterTypes[3].cast(mc);
				        arg = 5;parameterTypes[4].cast(doTracing);

				        stage = " set";
				        Object[] initArgs = new Object[5];
				        arg = 1;initArgs[0] = oGet;
				        arg = 2;initArgs[1] = this;
				        arg = 3;initArgs[2]= classModelRoot;
				        arg = 4;initArgs[3]= mc;
				        arg = 5;initArgs[4]= doTracing;

				        stage = " make instance";
				        arg = 0;
				        instance = constructors[c].newInstance(initArgs);		        				        	
			        }
		        }
		        if (!constructorFound) throw new MapperException("Found no constructor with 5 arguments");
	        }
	        catch (Exception ex)
	        {
	        	throw new MapperException("Failed to make writer instance of mapping class '" + javaClassName 
        			+ "': at stage " + stage + ", argument " + arg + ": " + ex.getMessage());
	        }

	        // check that the class implements XMLWriter
	        if (instance == null)
            	{throw new MapperException("Null writer instance of mapping class '" + javaClassName + "'");}
	        if (instance instanceof XMLWriter) writer = (XMLWriter)instance;
	        else throw new MapperException("Supplied mapping class '" + javaClassName + "' does not implement the XMLWriter interface." );
		}
		
		// usual case - a mapped XMLWriter
		else
		{
			writer = new MappedXMLWriter(oGet,this,classModelRoot,mc, runTracing);			
		}
		
		return writer;
	}

	
	/**
	 * @return true if reading and writing XML into the class model is
	 * done by a hand-coded Java class, rather than by the mapping-driven 
	 * code in the tools
	 */
	public boolean isJavaMapper()
	{
		boolean isJava = false;
		if (    (getMappingParameters() != null) && 
				(!getMappingParameters().getMappingClass().equals(""))) isJava = true;
		return isJava;
	}
	
	
	/**
	 * @return a namespace set including all the namespaces on the Global Mapping parameters
	 * @throws MapperException
	 */
	public NamespaceSet getNamespaceSet() throws MapperException
	{
		   NamespaceSet NSSet = new NamespaceSet();
		   if ((getMappingParameters() != null) && (getMappingParameters().getNameSpaces() != null))
		   for (Iterator<Namespace> it = getMappingParameters().getNameSpaces().iterator(); it.hasNext();)
		   {
			   Namespace NS = it.next();
			   NSSet.addNamespace(new namespace(NS.getPrefix(),NS.getURL()));
		   }
		   return NSSet;
		
	}
	
	/**
	 * @return true if the mapping set declares a wrapper class
	 */
	public boolean hasWrapperClass()
	{
		if (getMappingParameters()  == null) return false;
		String javaClassName = getMappingParameters().getWrapperClass();
		return (!(javaClassName.equals("")));		
	}
	
	/**
	 * 
	 * @param spare spare argument for the instance of the wrapper class, in case needed
	 * @return an instance of the wrapper class for this mapping set
	 */
	public MapperWrapper getWrapper(Object spare) throws MapperException
	{
		/* if there is already a wrapper instance, its 'spare' object argument 
		 * (usually an MDLXOReader) will need to be refreshed. */
		if (wrapper != null)
		{
			wrapper.resetSpareArgument(spare);
		}
		else if (wrapper == null)
		{
			Object instance = null;
			if (getMappingParameters()  == null) return null;
			String javaClassName = getMappingParameters().getWrapperClass();
			if (javaClassName.equals("")) return null;

			// make the class
	        Class<?> theClass = null;
	        try {theClass = Class.forName(javaClassName);}
	        catch (ClassNotFoundException ex)
	            {throw new MapperException("Failed to find wrapper class '" + javaClassName + "': " + ex.getMessage());}
	        
	        /* make an instance of the class; there should be one constructor with two 
	         * parameters, the first of which is a MappedStructure; and there may be others,
	         * not used here, which must not have just 2 arguments */
	        String stage = "";
	        int arg = 0;
	        try
	        {
		        Constructor<?>[] constructors = theClass.getConstructors();	
		        boolean constructorFound = false;
		        for (int c = 0; c < constructors.length;c++)
		        {
			        Class<?>[] parameterTypes = constructors[c].getParameterTypes();
			        if (parameterTypes.length == 2)
			        {
			        	constructorFound = true;
			        	stage = " class cast";
				        // the next calls will throw a class cast exception if the argument type does not match
				        arg = 1; parameterTypes[0].cast(this); 
				        arg = 2; parameterTypes[1].cast(spare); 

				        stage = " set";
				        Object[] initArgs = new Object[2];
				        arg = 1;initArgs[0] = this;
				        arg = 2;initArgs[1] = spare;

				        stage = " make instance";
				        arg = 0;
				        instance = constructors[c].newInstance(initArgs);		        				        	
			        }
		        }
		        if (!constructorFound) throw new MapperException("Found no constructor for wrapper class with 2 arguments");
	        }
	        catch (Exception ex)
	        {
	        	ex.printStackTrace();
	        	throw new MapperException("Failed to make instance of wrapper class '" + javaClassName 
	    			+ "': at stage " + stage + ", argument " + arg + ": " + ex.getMessage());
	        }

	        // check that the class implements MapperWrapper
	        if (instance == null)
	        	{throw new MapperException("Null instance of wrapper class '" + javaClassName + "'");}
	        if (instance instanceof MapperWrapper) wrapper = (MapperWrapper)instance;
	        else throw new MapperException("Supplied wrapper class '" + javaClassName 
	        		+ "' does not implement the MapperWrapper interface." );			
		}

        return wrapper;
	}
	
	/**
	 * @return an instance of the wrapper class for this mapping set, 
	 * with the usual choice for the second 'spare' argument of the constructor
	 * (the String name of the top ElementDef)
	 */
	public MapperWrapper getWrapper() throws MapperException
	{
		String spare = "";
		if (getRootElement() != null) spare = rootElement.getName();

		if ((wrapper == null) && (hasWrapperClass()))
		{
			wrapper = getWrapper(spare);					
		}

		/* if there is already a wrapper instance, its 'spare' object argument 
		 * (maybe an MDLXOReader) will need to be refreshed. */
		else if ((wrapper != null) && (hasWrapperClass()))
		{
			wrapper.resetSpareArgument(spare);
		}
		return wrapper;
	}
	
	/**
	 * 
	 * @param root root of the input XML document
	 * @return the input XML pjut throught thew input wrapper transform, if there is one
	 * @throws MapperException
	 */
	public Element getInWrappedXML(Element root) throws MapperException
	{
		if (getWrapper() != null) return getWrapper().transformIn(root).getDocumentElement();
		else return root;
	}
	
	/**
	 * @param instancePath the path to a data instance (typically got from a file choose dialogue)
	 * @return the XML document root Element to be given to the XOReader. This is either the root of the document at
	 * the given location (if there is no wrapper class);
	 * or if there is a wrapper class, it is the root of the Document got by applying the wrapper
	 * 'in' transform to the file whose location is given
	 */
	public Element getXMLRoot(String instancePath) throws MapperException
	{
		Element XMLRoot = null;
		String instanceLocation = instancePath;
		if (instanceLocation == null) throw new MapperException("Null location for data instance");
		/* sometimes the path comes from a file dialogue , so is absolute; but if
		 * it is a 'platform:/resource' path, it needs to be converted to absolute */
		if (instanceLocation.startsWith("platform:"))
		{
			URI XMLURI = URI.createURI(instancePath);
			instanceLocation = FileUtil.editURIConverter().normalize(XMLURI).toFileString();
		}
		
		if (hasWrapperClass()) try
		{
			MapperWrapper wrapper = getWrapper();

			Object input = null;
			if (wrapper.transformType() == AbstractMapperWrapper.XML_TYPE)
			{
				input = XMLUtil.getRootElement(instanceLocation);
				if (input == null) throw new MapperException("Could not open XML");				
			}
			else if (wrapper.transformType() == AbstractMapperWrapper.TEXT_TYPE)
			{
				input = new FileInputStream(new File(instanceLocation));
			}
			
			// apply the input transform
			XMLRoot = applyInputTransform(input);
		}
		catch (Exception ex) {ex.printStackTrace();throw new MapperException(ex.getMessage());}
		else
		{
			XMLRoot = XMLUtil.getRootElement(instanceLocation);			
		}
		return XMLRoot;		
	}

	/**
	 * @param stream input stream from a data instance
	 * @return the XML document root to be given to the XOReader. This is either the root of the document
	 * in the stream (if there is no wrapper class);
	 * or if there is a wrapper class, it is the root of the Document got by applying the wrapper
	 * 'in' transform to the stream
	 */
	public Element getXMLRoot(InputStream stream) throws MapperException
	{
		Element XMLRoot = null;
		
		if (hasWrapperClass()) try
		{
			MapperWrapper wrapper = getWrapper();

			Object input = null;
			if (wrapper.transformType() == AbstractMapperWrapper.XML_TYPE)
			{
				input = XMLUtil.getRootElement(stream);
				if (input == null) throw new MapperException("Could not open XML");				
			}
			else if (wrapper.transformType() == AbstractMapperWrapper.TEXT_TYPE)
			{
				input = stream;
			}
			
			// apply the input transform
			XMLRoot = applyInputTransform(input);
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
		else
		{
			XMLRoot = XMLUtil.getRootElement(stream);			
		}
		return XMLRoot;				
	}
	
	private Element applyInputTransform(Object input) throws MapperException
	{
		Document inDoc = getWrapper().transformIn(input);
		
		// simple implementation that seems to lose information with CCRTransform
		//return inDoc.getDocumentElement();
		
    	/* try making a temporary file 'eclipseTempFile.xml' (not visible in Eclipse); 
    	 * solves the problem with CCRTransform, I know not why. */
		String tempFileLocation = "";
		if (FileUtil.isInEclipse())
		{
			tempFileLocation = EclipseFileUtil.workspaceRoot() + TEMPORARY_XML_FILE;			
		}
		else
		{
			String mappingLocation = eResource().getURI().toString();
			tempFileLocation = FileUtil.projectFolderLocation(mappingLocation) + TEMPORARY_XML_FILE;			
		}
    	XMLUtil.writeOutput(inDoc,tempFileLocation,true);
		return XMLUtil.getRootElement(tempFileLocation);			
	}
	
	/*copied this static string from class EclipseFileUtilinto this class, 
	 * to avoid having to load EclipseFileUtil; not sure what Eclipse libraries it needs */
	private static String TEMPORARY_XML_FILE = "/eclipseTempFile.xml";

	/**
	 * @param outputRoot the root of an XML tree made by the XOWriter for this mapping set
	 * @param spare object to be passed to the output wrapper transform
	 * @return the result of applying the wrapper 'out' transformation ,if there is one.
	 * This result is either an XML Document (if there is no wrapper, or if the wrapper type is XMl)
	 * or a String[] array (if the wrapper type is text)
	 */
	public Object makeOutputObject(Element outputRoot, Object spare) throws MapperException
	{
		Object outputObj = null;
		if (hasWrapperClass()) 
		{
			outputObj = getWrapper(spare).transformOut(outputRoot);
		}
		else
		{
			outputObj = outputRoot.getOwnerDocument();			
		}
		return outputObj;		
	}
	
	
	/**
	 * @return static constant from class AbstractMapperWrapper  defining the file type - XML or text
	 */
	public int getInstanceFileType() throws MapperException
	{
		int type = AbstractMapperWrapper.XML_TYPE;  // default if there is no wrapper class
		if (this.hasWrapperClass())
		{
			type = getWrapper().transformType();
		}
		return type;
	}

	
	/**
	 * @return the (usually one) file extensions for input files to the 
	 * XOReader - depending on whether this mapping set has a wrapper class
	 */
	public String[] getExtensions() throws MapperException
	{
		String[] exts = new String[1];
		if (hasWrapperClass()) 
		{
			exts[0] = getWrapper().fileExtension();
		}
		else exts[0] = "*.xml";
		return exts;		
	}
	
	// for fast lookup of the subclasses of a class, including itself
	private Hashtable<String,Vector<EClass>> allSubClasses = null;

	
	/**
	 * @param ec a class
	 * @return a Vector of all classes which inherit from the class, 
	 * including the class itself
	 */
	public Vector<EClass> getAllSubClasses(EClass ec) throws MapperException
	{
		if (ec == null) return new Vector<EClass>();
		if ((allSubClasses == null)||(allSubClasses.size() == 0))  setUpSubClasses();
		Vector<EClass> subclasses =  allSubClasses.get(ModelUtil.getQualifiedClassName(ec));
		/* this covers up for some failures - eg having the wrong top package, leading to no hits,
		 * by returning at least the class itself - better than nothing */
		if (subclasses == null)
		{
			subclasses = new Vector<EClass>();
			subclasses.add(ec);
		}
		return subclasses;
	}
	
	/**
	 * set up a Hashtable of all subclasses of any class, for use in getAllSubClasses.
	 */
	private void setUpSubClasses() throws MapperException
	{
		allSubClasses = new Hashtable<String,Vector<EClass>>();
		for (Iterator<EClass> it = ModelUtil.getAllClasses(getClassModelRoot()).iterator(); it.hasNext();)
		{
			EClass subC = it.next();
			String cName = ModelUtil.getQualifiedClassName(subC);
			Vector<EClass> subclasses = allSubClasses.get(cName);
			/* If a class is encountered before any of its proper subclasses, 
			 * ensure it is counted amongst its own subclasses*/
			if (subclasses == null) 
			{
				subclasses = new Vector<EClass>();
				subclasses.add(subC);
				allSubClasses.put(cName,subclasses);
			}

			// getEAllSuperTypes does not return the class itself; only proper subclasses
			for (Iterator<EClass> ic = subC.getEAllSuperTypes().iterator();ic.hasNext();)
			{
				EClass superC  = ic.next();
				String dName = ModelUtil.getQualifiedClassName(superC);
				Vector<EClass> subclasses2 = allSubClasses.get(dName);
				/* If a class is encountered after any of its proper subclasses, 
				 * ensure it is counted amongst its own subclasses*/
				if (subclasses2 == null) 
				{
					subclasses2 = new Vector<EClass>();
					subclasses2.add(superC);
				}
				subclasses2.add(subC);
				allSubClasses.put(dName,subclasses2);
			}
		}
	}

    /**
     * @return the number of mappable nodes (ElementDefs and AttributeDefs)
     * in the whole tree, stopping the recursion at any repeated type
     */
    public int countNodesInTree() throws MapperException
    {
    	int  count = 0;
    	if (getRootElement() != null)
    	{
    		Vector<String> typeNames = new Vector<String>();
    		count = getRootElement().countNodesInSubtree(typeNames, getStructureDefinition());
    	}
    	return count;
    }
    
    
    @SuppressWarnings("unused")
	private void trace(String s) {System.out.println(s);}

	
} //MappedStructureImpl
