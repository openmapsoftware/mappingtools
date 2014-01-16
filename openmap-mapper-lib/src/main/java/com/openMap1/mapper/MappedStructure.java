/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

// added by RW
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.ParameterClass;
import com.openMap1.mapper.StructureType;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.writer.XMLWriter;
import com.openMap1.mapper.writer.objectGetter;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.SchemaMismatch;
import com.openMap1.mapper.core.StructureMismatch;
import com.openMap1.mapper.core.NamespaceSet;

import org.w3c.dom.Element;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapped Structure</b></em>'.
 * 
 * The MappedStructure is the root node of a mapping set, which is stored as a '.mapper' 
 * resource.  Properties of the MappedStructure define the location of the file 
 * (typically XSD) being mapped, and of the Ecore class model it is mapped to.
 * 
 * Beneath it is zero or one GlobalMappingParameters object, and a 
 * root Element which is the root of the structure tree being mapped onto some class model.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getName <em>Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getRootElement <em>Root Element</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getUMLModelURL <em>UML Model URL</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getStructureType <em>Structure Type</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getStructureURL <em>Structure URL</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getTopElementType <em>Top Element Type</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getTopElementName <em>Top Element Name</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getMappingParameters <em>Mapping Parameters</em>}</li>
 *   <li>{@link com.openMap1.mapper.MappedStructure#getParameterClasses <em>Parameter Classes</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.openMap1.mapper.MapperPackage#getMappedStructure()
 * @model
 * @generated
 */
public interface MappedStructure extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name of a MappedStructure is currently not used. 
	 * The name and location of the containing resource are sufficient.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappedStructure#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Root Element</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * This  element is the root of the structure tree being mapped.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root Element</em>' containment reference.
	 * @see #setRootElement(ElementDef)
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_RootElement()
	 * @model containment="true"
	 * @generated
	 */
	ElementDef getRootElement();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappedStructure#getRootElement <em>Root Element</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root Element</em>' containment reference.
	 * @see #getRootElement()
	 * @generated
	 */
	void setRootElement(ElementDef value);

	/**
	 * Returns the value of the '<em><b>UML Model URL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The name and location of the definition of the class model being mapped.
	 * Usually it is an Ecore model.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>UML Model URL</em>' attribute.
	 * @see #setUMLModelURL(String)
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_UMLModelURL()
	 * @model
	 * @generated
	 */
	String getUMLModelURL();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappedStructure#getUMLModelURL <em>UML Model URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>UML Model URL</em>' attribute.
	 * @see #getUMLModelURL()
	 * @generated
	 */
	void setUMLModelURL(String value);

	/**
	 * Returns the value of the '<em><b>Structure Type</b></em>' attribute.
	 * The literals are from the enumeration {@link com.openMap1.mapper.StructureType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Defines what type of structure is being mapped; 'XSD' or  'RDBMS' 
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Structure Type</em>' attribute.
	 * @see com.openMap1.mapper.StructureType
	 * @see #setStructureType(StructureType)
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_StructureType()
	 * @model
	 * @generated
	 */
	StructureType getStructureType();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappedStructure#getStructureType <em>Structure Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Structure Type</em>' attribute.
	 * @see com.openMap1.mapper.StructureType
	 * @see #getStructureType()
	 * @generated
	 */
	void setStructureType(StructureType value);

	/**
	 * Returns the value of the '<em><b>Structure URL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * File name and location of the file defining the structure bieng mapped
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Structure URL</em>' attribute.
	 * @see #setStructureURL(String)
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_StructureURL()
	 * @model
	 * @generated
	 */
	String getStructureURL();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappedStructure#getStructureURL <em>Structure URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Structure URL</em>' attribute.
	 * @see #getStructureURL()
	 * @generated
	 */
	void setStructureURL(String value);

	/**
	 * Returns the value of the '<em><b>Top Element Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The user may select the top element type to ensure that the structure
	 * being mapped is the structure of that complex type in the (XSD) structure 
	 * definition.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Top Element Type</em>' attribute.
	 * @see #setTopElementType(String)
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_TopElementType()
	 * @model
	 * @generated
	 */
	String getTopElementType();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappedStructure#getTopElementType <em>Top Element Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Top Element Type</em>' attribute.
	 * @see #getTopElementType()
	 * @generated
	 */
	void setTopElementType(String value);

	/**
	 * Returns the value of the '<em><b>Top Element Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The user may select the top element name 
	 * (as an alternative to selecting the top element name)
	 * to ensure that the structure
	 * being mapped is the structure of that named Element in the (XSD) structure 
	 * definition.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Top Element Name</em>' attribute.
	 * @see #setTopElementName(String)
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_TopElementName()
	 * @model
	 * @generated
	 */
	String getTopElementName();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappedStructure#getTopElementName <em>Top Element Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Top Element Name</em>' attribute.
	 * @see #getTopElementName()
	 * @generated
	 */
	void setTopElementName(String value);
	
	/**
	 * Returns the value of the '<em><b>Mapping Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * The one GlobalMappingParameters object for this mapping set.
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mapping Parameters</em>' containment reference.
	 * @see #setMappingParameters(GlobalMappingParameters)
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_MappingParameters()
	 * @model containment="true"
	 * @generated
	 */
	GlobalMappingParameters getMappingParameters();

	/**
	 * Sets the value of the '{@link com.openMap1.mapper.MappedStructure#getMappingParameters <em>Mapping Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mapping Parameters</em>' containment reference.
	 * @see #getMappingParameters()
	 * @generated
	 */
	void setMappingParameters(GlobalMappingParameters value);

	/**
	 * Returns the value of the '<em><b>Parameter Classes</b></em>' containment reference list.
	 * The list contents are of type {@link com.openMap1.mapper.ParameterClass}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Classes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Classes</em>' containment reference list.
	 * @see com.openMap1.mapper.MapperPackage#getMappedStructure_ParameterClasses()
	 * @model containment="true"
	 * @generated
	 */
	EList<ParameterClass> getParameterClasses();

	/**
	 * <!-- begin-user-doc -->
	 * Validation check to ensure that the class model URI points to an actual class model
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean canFindClassModel(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * <!-- begin-user-doc -->
	 * Validation check to ensure that the structure definition URI points to a valid structure definition
	 * <!-- end-user-doc -->
	 * @model diagnosticsDataType="com.openMap1.mapper.DiagnosticChain" contextDataType="com.openMap1.mapper.Map<?, ?>"
	 * @generated
	 */
	boolean  canFindStructureDefinition(DiagnosticChain diagnostics, Map<?, ?> context);

	/**
	 * Set a structure definition, and add its namespaces to the mapping parameters
	 */
	public void setStructureDefinition(StructureDefinition structureDefinition);

	/**
	 * get a Structure definition
	 */
	public StructureDefinition getStructureDefinition() throws MapperException;
	
	/**
	 * connect to a relational database and return its structure definition
	 * @param userName
	 * @param password
	 * @return
	 * @throws MapperException
	 */
	public StructureDefinition connectToRDB(String userName, String password) throws MapperException;

	/**
	 *  set the class model root
	 */
	public void setClassModelRoot(EPackage classModelRoot);

	/**
	 * get the class model root
	 */
	public EPackage getClassModelRoot() throws MapperException;

	/**
	 * @return name of the file containing the class model
	 */
	public String getClassModelFileName();

	/**
	 * @return name of the file containing the structure to be mapped
	 */
	public String getStructureFileName();
	
	/**
	 * @param path String form of a descending path, starting with "/"
	 * @return the Element or Attribute reached by that path; or null if there is none.
	 */
	public NodeDef getNodeDefByPath(String path);
	
	/**
	 * check that the XML instance with this root conforms to the 
	 * mapped structure; return a list of mismatches
	 * @param rootEl root element of the document being checked
	 * @return Vector of all structure mismatches detected (duplicates not removed)
	 * @throws MapperException eg if the document associates a namespace URI with more than one prefix
	 * @throws MapperException eg if the document associates a namespace URI with more than one prefix,
	 * (XML allows this but I don't yet; I could do) or mentions a namespace URI not known in the mapping set
	 */
	public Vector<StructureMismatch> checkInstance(Element rootEl) throws MapperException;
	
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
			Vector<StructureMismatch> mismatches) throws MapperException; 
	
	
	/**
	 * validate an XML instance against the schema which defines this mapped structure,
	 * if there is such a schema. If there is no schema, return an empty list.
	 * (Not yet tested outside Eclipse)
	 * @param the root element of the XML instance
	 * @return a Vector of SchemaMismatch objects, which wrap org.eclipse.emf.ecore.util.Diagnostic
	 */
	public Vector<SchemaMismatch> schemaValidate(Element instanceRoot) throws MapperException;

	/**
	 * @return true if the set of mappings visible to the class model view had been refreshed since
	 * this mapping set was opened with an editor, or since a mapping was added or removed
	 */
	public boolean classModelViewIsRefreshed();
	
	/**
	 * set the variable which defines the result of classModelViewIsRefreshed; 
	 * It is false when this mapping set is created, and should be set false when
	 * any mappings are edited.
	 * set it true when the class model view is refreshed
	 * @param fresh
	 */
	public void setClassModelViewIsRrefreshed(boolean fresh);
	
	/**
	 * If true, the XML writing procedures which were last created
	 * are still current and are in the location writeProceduresURI()  -
	 * so they do not need to be re-created before running a trnalsation or generating 
	 * XSLT
	 * @return  true if the mapping set has not been edited since the write procedures were created
	 */
	public boolean hasCurrentWriteProcedures();
	
	
	/**
	 * @return  the URI of the XML writing procedures, if they exist - or "" if they do not
	 */
	public String writeProceduresURI();
	
	/**
	 * @return all the mapping sets imported by any Element in this mapping set.
	 * key = URI string of the mapping set
	 * value = the mapping set
	 */
	public Hashtable<String,MappedStructure> getDirectImportedMappingSets();
	
	
	/**
	 * @param cSet a (class, subset)
	 * @return if there is an imported mapping set for the (class,subset) return it;
	 * otherwise return null
	 */
	public MappedStructure getDirectImportedMappingSet(ClassSet cSet) throws MapperException;
	
	/**
	 * 
	 * @return all the mapping sets imported directly or indirectly by this
	 * mapping set, including itself, with no duplicates
	 * key = URI string of the mapping set
	 * value = the mapping set
	 */
	public Hashtable<String,MappedStructure> getAllImportedMappingSets();
	
	/**
	 * @return the root Element of the write procedures file which was made from this
	 * MappedStructure (its location and name are known automatically from the 
	 * location and name of this resource)
	 * @throws MapperException if the Procedures file cannot be found
	 */
	public Element procedureFileRoot() throws MapperException;
	
	/**
	 * For use only inside Eclipse; outside Eclipse returns true.
	 * @return true if this mapping set has been edited since the last
	 * compilation (creation of the WProc file) or if the WProc file does not exist.
	 */
	public boolean hasChangedSinceCompile();
	
	
	/**
	 * @return the name of the mapping set
	 */
	public String getMappingSetName();
	
	/**
	 * return the XML reader for the mapping set  - 
	 * either an MDLXOReader from the mappings, or a 
	 * instance of a supplied Java class implementing XOReader
	 * @param dataSource the source of information to be read in
	 * @param classModelRoot (optional) root of the class model; 
	 * if null the MappedStructure will attempt to find the class model root for itself
	 * (make it non-null for standalone applications, where the platform:resource URL 
	 * used in the mapping set will not work) 
	 */
	public XOReader getXOReader(Object dataSource, EPackage classModelRoot, messageChannel mc) 
	throws MapperException;
	
	/**
	 * @param oGet an objectGetter which gives the XMLWriter the object to be written to XML
	 * @param classModelRoot (optional) root of the class model. If null, this
	 * mappedStructure will attempt to find the class model root from its URL.
	 * (supply a non-null class model root for standalone applications, 
	 * where the platform:resource URL in the mapping set may not work)
	 * @param mc message channel for error messages
	 * @return the XML Writer for the mapping set - either a MappedXMLWriter
	 * using the mappings, or an instance of a supplied Java class implementing XMLWriter
	 * @throws MapperException
	 */
	public XMLWriter getXMLWriter(objectGetter oGet,EPackage classModelRoot, messageChannel mc, boolean runTracing)
	throws MapperException;
	
	/**
	 * @return true if reading and writing XML into the class model is
	 * done by a hand-coded Java class, rather than by the mapping-driven 
	 * code in the tools
	 */
	public boolean isJavaMapper();
	
	/**
	 * @param spare spare argument for the instance of the wrapper class, in case needed
	 * @return an instance of the wrapper class for this mapping set
	 */
	public MapperWrapper getWrapper(Object spare) throws MapperException;

	/**
	 * @return an instance of the wrapper class for this mapping set, 
	 * with a default choice for the second 'spare' argument of the constructor
	 * (the String name of the top ElementDef)
	 * but as far as I can tell (15/12/10) this default choice of spare argument
	 * is never used. 
	 * So the spare argument is really spare, and can be used at will, 
	 * eg the output wrapper class may want access to the input wrapper class instance.
	 */
	public MapperWrapper getWrapper() throws MapperException;
	
	/**
	 * @return true if the mapping set declares a wrapper class
	 */
	public boolean hasWrapperClass();
	
	/**
	 * @return the (usually one) file extensions for input files to the 
	 * XOReader - depending on whether this mapping set has a wrapper class
	 */
	public String[] getExtensions() throws MapperException;
	
	/**
	 * 
	 * @param root root of the input XML document
	 * @return the input XML pjut throught thew input wrapper transform, if there is one
	 * @throws MapperException
	 */
	public Element getInWrappedXML(Element root) throws MapperException;
	
	/**
	 * @param instancePath the path to a data instance (typically got from a file choose dialogue)
	 * @return the XML document root to be given to the XOReader. This is either the root of the document at
	 * the given location (if there is no wrapper class);
	 * or if there is a wrapper class, it is the root of the Document got by applying the wrapper
	 * 'in' transform to the file whose location is given
	 */
	public Element getXMLRoot(String instancePath) throws MapperException;

	/**
	 * @param stream input stream from a data instance
	 * @return the XML document root to be given to the XOReader. This is either the root of the document
	 * in the stram (if there is no wrapper class);
	 * or if there is a wrapper class, it is the root of the Document got by applying the wrapper
	 * 'in' transform to the stream
	 */
	public Element getXMLRoot(InputStream stream) throws MapperException;
	
	/**
	 * @param outputRoot the root of an XML tree made by the XOWriter for this mapping set
	 * @param spare object to be passed to the output wrapper transform ,if needed
	 * @return the result of applying the wrapper 'out' transformation ,if there is one
	 */
	public Object makeOutputObject(Element outputRoot, Object spare) throws MapperException;
	
	/**
	 * @return static constant from class AbstractMapperWrapper  defining the file type - XML or text
	 */
	public int getInstanceFileType() throws MapperException;
	
	/**
	 * @return a namespace set including all the namespaces on the Global Mapping parameters
	 * @throws MapperException
	 */
	public NamespaceSet getNamespaceSet() throws MapperException;
	
	/**
	 * @param ec a class
	 * @return a Vector of all classes which inherit from the class, 
	 * including the class itself
	 */
	public Vector<EClass> getAllSubClasses(EClass ec) throws MapperException;

    /**
     * @return the number of mappable nodes (ElementDefs and AttributeDefs)
     * in the whole tree, stopping the recursion at any repeated type
     */
    public int countNodesInTree()
    	throws MapperException;
	
	
} // MappedStructure
