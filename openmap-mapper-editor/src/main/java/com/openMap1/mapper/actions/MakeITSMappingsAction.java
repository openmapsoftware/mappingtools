package com.openMap1.mapper.actions;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.ValueCondition;
import com.openMap1.mapper.converters.CDAConverter;
import com.openMap1.mapper.converters.NHS_CDA_Wrapper;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.impl.ElementDefImpl;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.structures.ITSAssociation;
import com.openMap1.mapper.structures.ITSAttribute;
import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.userConverters.NHS_CDA_TagRuleInterpreter;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.AssociationView;
import com.openMap1.mapper.views.AttributeView;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.FeatureView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

/**
 * @author Robert
 *
 */

public class MakeITSMappingsAction  extends Action implements IAction{

	boolean tracing = true;
	
	private MapperEditor mapperEditor;
	
    private MappedStructure mappedStructure;
    
    private String mappingSetURIString;
    
    private ClassModelView classModelView;
		
	// wrapper class and package as defined in a mapping set - e.g 'com.OpenMap1.mapper.converters.NHS_CDA_Converter'
	private String fullMappingsWrapperClassName = "";
	
	/* wrapper class for simple mappings to CDA. The only role of this wrapper is to substitute keys for
	 * rendered html subtrees, so those subtrees are passed through a translation unchanged */
	private String simpleMappingsWrapperClassName = "com.openMap1.mapper.converters.NHS_Simplified_CDA";

	/** the package which is the root of the model */
	public EPackage ecoreRoot() {return ecoreRoot;}
	private EPackage ecoreRoot;
	
	private String CDATagFileName = "CDATagNames.csv";
	
	public boolean isNHSMIF() 
	{
		if (ecoreRoot != null) return (ModelUtil.getMIFAnnotation(ecoreRoot, "isNHSMIF") != null);
		return false;
	}
	
	public String messageName() 
	{
		if (ecoreRoot != null) return (ModelUtil.getMIFAnnotation(ecoreRoot, "message"));
		return null;
	}
	
	// names of the wrapper classes used for NHS-style CDAs and V3 messages
	public static String NHSV3WrapperClass = "com.openMap1.mapper.converters.NHS_V3_Converter";
	public static String NHSCDATemplatedWrapperClass = "com.openMap1.mapper.converters.NHS_CDA_Converter";
	public static String NHSCDAWireWrapperClass = "com.openMap1.mapper.converters.NHS_CDA_Wire2";
	
	// value of attribute npfitlc:contentId/@root
	public static String NHSContentIdRoot = "2.16.840.1.113883.2.1.3.2.4.18.16";
	
	
	public static String[][] NHSFixedAttributes = {{"II","extension"},{"CV","code"},{"CD","code"},{"CS","code"},{"BL","value"}};

	
	// name of the wrapper class for other CDAs
	public static String CDAWrapperClass = "com.openMap1.mapper.converters.CDAConverter";

	/* if true, suppress fixed value mappings for the text attributes
	 * (representation, mediaType and partType) on name parts and address parts,
	 * because many example instances don't seem to have them 
	 * (are they added by schema validation?) */
	private boolean suppressFixedTextMappings = true;
	
	private String[]  suppressableAttribute = {"representation","mediaType","partType"};
	
	private int warningState;
		
	private int WARNINGS_ASK_USER = 1; // on the first warning message, ask if the user wants more
	private int WARNINGS_SHOW_ALL = 2; // the user has asked to show all warning messages
	private int WARNINGS_SUPPRESS = 3; // the user has asked to suppress warning messages
	
	// used as a prefix in the description property of mapping set ElementDef nodes, to convey the wire-form CDA name for the schema
	public static String CDA_NAME = "CDA name:";
	
	// used as a prefix in the description property of mapping set AttributeDef nodes, to convey fixed values for the schema
	public static String FIXED = "fixed:";
	
	/* Outer key = class name in simple class model (the class may appear in many LabelleEClasses)
	 * Inner key = LabelledEClass path, from the root of simple class model to the parent of that class
	 * Value = subset for that occurrence of the class (usually "") */
	private Hashtable<String,Hashtable<String,String>> subsetsForPaths;
	
	// true if the simplified class model is to have multiple packages, one per template or section
	private boolean multiPackages = true;
	
	/*  key = name for the template, as in an annotation on the full class model; 
	 * value = template id, as in th package name in the full class model
	 */
	private Hashtable<String,String> TemplatePackageIds = new Hashtable<String,String>();

	
	//----------------------------------------------------------------------------------------------------------
	//                                Constructor
	//----------------------------------------------------------------------------------------------------------

	public MakeITSMappingsAction()
	{
		super("Make Simplified Model and Mappings");
	}
	
	
	/**
	 * Save the simplified Ecore class model, and mappings of the simplified
	 * and full message structures onto it
	 */
	public void run()
	{
		warningState = WARNINGS_ASK_USER;
		
		classModelView = WorkBenchUtil.getClassModelView(false);
		if (classModelView != null) try
		{
			ecoreRoot = classModelView.ecoreRoot();
			mappingSetURIString = classModelView.mappingSetURI().toString();
			mapperEditor = WorkBenchUtil.getMapperEditor(mappingSetURIString);
			if (mapperEditor != null)
			{
				mappedStructure = WorkBenchUtil.mappingRoot(mapperEditor);
				// check the Ecore model has some attributes included for this mapping set
				if (hasIncludedAttributes(ecoreRoot))
				{
					// complete the annotations (for NHS fixed values, and text nodes) , and save the ecore model
					LabelledEClass entryClass = classModelView.topLabelledEClass();
					completeAnnotations(entryClass);					
					saveEcoreModel();

					/* Create a simplified Ecore class model and two mapping sets - 
					 * for the simple message and the full message mapped onto it.
					 * ( offer the user an option to constrain the simple message by a schema - no longer used) */
					/* String prompt = "Optionally select Mapping Set to constrain simple message structure";
					masterStructure = askUserForMappedStructure(mapperEditor, prompt); */

					MappedStructure masterStructure = null;
					makeSimpleModelAndMappings(masterStructure);
					WorkBenchUtil.showMessage("Completed","Made simplified message, model, and mappings");

				}
				else WorkBenchUtil.showMessage("Error", "The RMIM class model has no attributes annotated to be included in an ITS for this mapping set");
			}
			else WorkBenchUtil.showMessage("Error", "No Mapper Editor found");				
		}
		catch (Exception ex) 
		{
			WorkBenchUtil.showMessage("Error",ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param model an ECore model
	 * @return true if the model has any EAttributes annotated to be included in
	 * the mapping set currently active in the editor
	 * (does not check the detail of the annotations; they could conceivably not include
	 * any of the attributes, but this would be perverse)
	 */
	private boolean hasIncludedAttributes(EPackage model)
	{
		boolean hasIncluded = false;		
		for (Iterator<EClass> ic = ModelUtil.getAllClasses(model).iterator();ic.hasNext();)
			if (hasAnySimplificationAnnotations(ic.next()))  hasIncluded = true;
		return hasIncluded;
	}
	
	/**
	 * 
	 * @param theClass
	 * @return true if this EClass has any simplification annotations on its EReferences or EAttributes
	 */
	private boolean hasAnySimplificationAnnotations(EClass theClass)
	{
		boolean hasAnnotations = false;
		for (Iterator<EStructuralFeature> ia = theClass.getEStructuralFeatures().iterator();ia.hasNext();)
		{
			EStructuralFeature ea = ia.next();
			EAnnotation ann = ea.getEAnnotation(FeatureView.microITSURI());
			if (ann != null)
			{
				hasAnnotations = true;
			}
		}
		return hasAnnotations;
	}
	
	/**
	 * 
	 * @param theClass
	 * @return true if this LabelledEClass has any simplification annotations on its EReferences or EAttributes, 
	 * for the specific path to the LabelledEClass
	 */
	private boolean hasSimplificationAnnotations(LabelledEClass theClass)
	{
		boolean hasAnnotations = false;
		for (Iterator<EStructuralFeature> ia = theClass.eClass().getEStructuralFeatures().iterator();ia.hasNext();)
		{
			EStructuralFeature ea = ia.next();
			EAnnotation ann = ea.getEAnnotation(FeatureView.microITSURI());
			if (ann != null)
			{
				String value = ann.getDetails().get(theClass.getPath());
				if (value != null) hasAnnotations = true;
			}
		}
		return hasAnnotations;
	}

	
	/**
	 * recursive descent of the LabelleEClass tree, as far as there are simplification annotations,
	 * completing the simplification annotations:
	 * 
	 * (a) where NHS RMIM defines fixed values on II or CV or BL data types, 
	 * which have given 'fixed att value' annotations in the Ecore model, add the simplification annotations
	 * 
	 * (b) retain text subtrees, for any text node whose parent has annotations
	 * 
	 * @param theClass
	 */
	private void completeAnnotations(LabelledEClass theClass) throws MapperException
	{
		// mark text nodes to be retained, if their parent node has simplification annotations
		if ((theClass.associationName() != null) && (theClass.associationName().equals("text")))
		{
			// add a 'textContent' EAttribute to the class if it does not yet exist
			SimplificationsFromExamplesAction.addTextContentAttribute(theClass.eClass());
			SimplificationsFromExamplesAction.markAttributeUsedOrFixed(theClass,"textContent", "a", "a#",false);										
		}

		if (hasSimplificationAnnotations(theClass))
		{
			// marking these attributes should be idempotent; does not matter if you do it many times
			if (isNHSMIF()) SimplificationsFromExamplesAction.mark_CS_CV_CD_II_BL_Attributes(theClass);
			
			// recursive descent - one more step if this class has annotations
			for (Iterator<LabelledEClass> it = theClass.getChildren().iterator();it.hasNext();)
				completeAnnotations(it.next());
		}
	}

	
	private static String MADE_FROM_ITS = "Made from ITS";
	
	/* check that this mapping set is either new (no structure at all) 
	 * or has been made from an ITS.*/
	public static boolean isMadeFromITS(MappedStructure mappedStructure)
	{
		// if there is no mapped structure to overwrite, return true
		if (mappedStructure.getRootElement() == null) return true;
		// if there is a mapped structure, return true if it was made from an ITS
		return (mappedStructure.getRootElement().getDescription().equals(MADE_FROM_ITS));
	}
	
	//-----------------------------------------------------------------------------------------------------
	//            adding mappings to the simplified and full mapping set
	//-----------------------------------------------------------------------------------------------------
	
	
	/**
	 * set the multiplicities of a child ElementDef according to the 
	 * cardinalities of the EReference
	 * @param next
	 * @param ref
	 */
	private void setMultiplicities(ElementDef next,EReference ref,LabelledEClass fullClass)
	{
		next.setMinMultiplicity(MinMult.ONE);
		if (AssociationView.getConstrainedLowerBound(ref, fullClass) == 0) next.setMinMultiplicity(MinMult.ZERO);

		next.setMaxMultiplicity(MaxMult.ONE);		
		if (AssociationView.getConstrainedUpperBound(ref, fullClass) == -1) next.setMaxMultiplicity(MaxMult.UNBOUNDED);
	}
	
	/**
	 * add an object mapping on an ElementDef; 
	 * if there is an association from a parent class, add the association mapping
	 * @param node node on which to add mappings
	 * @param lClass the LabelledEClass to be mapped
	 * @param parentRef EReference from the parent class (null if there is none)
	 * @param parentSubset subset of the parent class object mapping (null if there is none)
	 * @param thisSubset the subset to use for this class mapping (if null, make one up)
	 * @return the new class mapping
	 */
	//** (not implemented) if lClass has a value condition annotation, add value conditions to the mappings in the full mapping set only
	private ObjMapping  makeObjectAndAssocMapping(ElementDef node,LabelledEClass lClass,
			EReference parentRef, LabelledEClass parent)
	{
		String parentSubset = null;
		if (parent != null) parentSubset = getSubset(parent.eClass().getName(),parent.parent());		
		String thisSubset = getSubset(lClass.eClass().getName(),lClass.parent());

		// add a node mapping set if it has not been added already
		NodeMappingSet nodeMaps = node.getNodeMappingSet();
		if (nodeMaps == null)
		{
			nodeMaps = MapperFactory.eINSTANCE.createNodeMappingSet();
			node.setNodeMappingSet(nodeMaps);
		}
		
		
		// make and add an object mapping
		ObjMapping om = MapperFactory.eINSTANCE.createObjMapping();
		om.setMappedClass(lClass.eClass().getName());
		om.setMappedPackage(lClass.eClass().getEPackage().getName());
		String qualifiedClassName = ModelUtil.getQualifiedClassName(lClass.eClass());
		String subset = thisSubset;
		if (subset == null) subset = AddMapperEditorActions.nextSubset(mappedStructure, qualifiedClassName);
		om.setSubset(subset);
		nodeMaps.getObjectMappings().add(om);
		
		// add an association mapping, if there is a parent object mapping
		if (parentSubset != null)
		{
			MappableAssociation mass =  new MappableAssociation(lClass.parent().eClass(),parentSubset,
					lClass.eClass(),subset,parentRef,true);
			AssocMapping am = MapperFactory.eINSTANCE.createAssocMapping();

			for (int end = 1; end < 3; end ++)
			{
				AssocEndMapping aem = MapperFactory.eINSTANCE.createAssocEndMapping();
				aem.setMappedRole(mass.roleName(end));
				aem.setMappedClass(mass.endClass(end).getName());
				aem.setMappedPackage(mass.endClass(end).getEPackage().getName());
				aem.setSubset(mass.getSubset(end));
				aem.setRequiredForObject(mass.requiredForEnd(end));
				if (end == 1) am.setMappedEnd1(aem);
				if (end == 2) am.setMappedEnd2(aem);
			}
			nodeMaps.getAssociationMappings().add(am);
		}
				
		return om;		
	}

	
	/**
	 * add a property mapping on the AttributeDef node
	 * @param nDef
	 * @param eClass
	 * @param subset subset of the object mapping, to match
	 * @param ea
	 */
	private void addPropertyMapping(NodeDef nDef,LabelledEClass lClass,EAttribute ea)
	{
		EClass eClass = lClass.eClass();
		String subset = getSubset(eClass.getName(),lClass.parent());
		
		// add a node mapping set to hold the mapping
		NodeMappingSet nodeMaps = nDef.getNodeMappingSet();
		if (nodeMaps == null)
		{
			nodeMaps = MapperFactory.eINSTANCE.createNodeMappingSet();
			nDef.setNodeMappingSet(nodeMaps);			
		}
		
		// create and add the property mapping
		PropMapping pm = MapperFactory.eINSTANCE.createPropMapping();
		pm.setMappedClass(eClass.getName());
		pm.setMappedPackage(eClass.getEPackage().getName());
		pm.setMappedProperty(ea.getName());
		pm.setSubset(subset);
		nodeMaps.getPropertyMappings().add(pm);		
	}
	
	//-----------------------------------------------------------------------------------------------------------------
	//             Make a simplified class model, and two mapped structures, for the simple and full 
	//             message structures mapped to it.
	//-----------------------------------------------------------------------------------------------------------------
	
	// the top package of the simplified class model, which all classes are in It is called com.OpenMap1.cda
	private EPackage topPackage;
	
	private Hashtable<String,EClass> simpleClasses;
	
	// the created mapping set of the simple message structure onto the simple Ecore class model
	private MappedStructure simpleMappings;
	
	// the created mapping set of the full V3 message structure onto the simple Ecore class model
	private MappedStructure fullMappings;
	
	/* to keep track of which attribute in the simple mapping set corresponds to each attribute in
	 * the full mapping set. Used only for NHS RMIMs, but set up in all cases.
	 * Key = node in full mapping set; value = node in simple mapping set */
	private Hashtable<NodeDef,NodeDef> matchedNodes;
	
	/* used to write out a csv file of path-to-path attribute mappings between the simple and full structures */
	private Vector<String[]> attributeMappings;
	// header row for the csv file of attribute mappings
	private String[] attributeMappingsHeader = {"Green CDA XPath","Templated full CDA XPath","Normative full CDA XPath","Data Type","Comments"};
	
	/* used to write out a file of warnings found when making the simple class model and mappings */
	private Vector<String[]> warnings;
	// header row for the csv file of attribute mappings
	private String[] warningsHeader = {"Path","Node","Warning"};
	
	/**
	 * ask the user for a mapped structure (typically attached to a schema)
	 *  to constrain the simple message structure
	 * @return the mapped structure
	 * @throws MapperException
	 */
	private MappedStructure askUserForMappedStructure(MapperEditor mapperEditor, String prompt) throws MapperException
	{
		MappedStructure ms = null;
		String[] exts = {"*.mapper"}; 
		String mapperFileLocation = FileUtil.getFilePathFromUser(mapperEditor,exts,prompt,false);	
		if (!mapperFileLocation.equals(""))try {ms = FileUtil.getMappedStructure(mapperFileLocation);}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
		return ms;
	}

	
	private void makeSimpleModelAndMappings(MappedStructure masterStructure) throws MapperException
	{
		// empty things
		simpleClasses = new Hashtable<String,EClass>();
		subsetsForPaths = new Hashtable<String,Hashtable<String,String>>();
		matchedNodes = new Hashtable<NodeDef,NodeDef>();
		attributeMappings = new Vector<String[]>();
		attributeMappings.add(attributeMappingsHeader);
		warnings = new Vector<String[]>();
		warnings.add(warningsHeader);
		
		// make the empty simple class model and two mapping sets.
		makeInitialModelAndMappings();
		
		// make the top class of the simple class model
		LabelledEClass topFullClass = classModelView.topLabelledEClass();
		String topClassName = topFullClass.eClass().getName();
		LabelledEClass topSimpleClass = findOrMakeClass(getSimplePackageName(topFullClass.eClass()),topClassName,null,null);
		// ensure this class will be the root of the tree in the class model view
		ModelUtil.addMIFAnnotation(topSimpleClass.eClass(), "entry", "true");		

		// make the top node of the simple mapping set, and map the top class to it
		ElementDef topSimpleNode = MapperFactory.eINSTANCE.createElementDef();
		topSimpleNode.setName(topClassName);
		topSimpleNode.setExpanded(true);
		simpleMappings.setRootElement(topSimpleNode);
		simpleMappings.setTopElementName(topClassName);
		makeObjectAndAssocMapping(topSimpleNode,topSimpleClass,null,null);

		// make the top node of the full mapping set, and map the top class to it
		ElementDef topFullNode = MapperFactory.eINSTANCE.createElementDef();
		topFullNode.setName(topClassName);
		// for MDHT templated subclasses of ClinicalDocument, name the top node 'ClinicalDocument'
		if (isClinicalDocumentClass(topFullClass)) topFullNode.setName("ClinicalDocument");
		topFullNode.setExpanded(true);
		fullMappings.setRootElement(topFullNode);
		fullMappings.setTopElementName(topClassName);
		ObjMapping topObjectMapping = makeObjectAndAssocMapping(topFullNode,topSimpleClass,null,null);
		addFixedValueConditions(topObjectMapping, topFullClass,null);
		
		// recurse down the full class model
		fillMappingNodes(topFullClass,topSimpleClass,topFullNode,topSimpleNode, false, false);
		
		addNodesForFixedValues(topFullNode,topFullClass);
		
		// possibly constrain the simple mapped structure to be a subset of an existing structure
		if (masterStructure != null) topSimpleNode = constrainSimpleStructure(topSimpleNode,masterStructure);
		
		/* for NHS CDA, alter the full mapping set to have correct CDA order, note CDA tag names
		 * and mark both mapping sets with data types  */
		if (isNHSMIF()) 
		{
			orderFullMappingSet();
			markMappingSetsWithTypes();
		}
		
		// add annotations to make javadoc for EMG generated java classes
		addJavaDocAnnotations(fullMappings,topPackage);
		
		// save the final state of the simple class model and the two mapping sets
		FileUtil.saveResource(topPackage.eResource());
		FileUtil.saveResource(simpleMappings.eResource());
		FileUtil.saveResource(fullMappings.eResource());
		
		// make and save a thin mapping set (full mapping set without all its fixed value conditions)
		makeThinMappingSet();
		
		// save the csv file of attribute mappings
		String csvMappingsLocation = changeExtension(addSuffixToFileName(mappingSetURIString, "mappings"),"csv");
		IFile csvMappingsFile = EclipseFileUtil.getFile(csvMappingsLocation);
		EclipseFileUtil.writeCSVFile(finalMappingRows(attributeMappings), csvMappingsFile);
		
		// save the csv file of warnings
		String csvWarningsLocation = changeExtension(addSuffixToFileName(mappingSetURIString, "warnings"),"csv");
		IFile csvWarningsFile = EclipseFileUtil.getFile(csvWarningsLocation);
		EclipseFileUtil.writeCSVFile(warnings, csvWarningsFile);
	}
	
	
	/**
	 * 
	 * @param labelledClass and entry class of an RMIM class model
	 * @return true if this class mentions the CDA wrapper class in its annotations
	 */
	private boolean isClinicalDocumentClass(LabelledEClass labelledClass)
	{
		boolean isClinicalDoc = false;
		String wrapperClass = ModelUtil.getMIFAnnotation(labelledClass.eClass(), "wrapperClass");
		if ((wrapperClass != null) && (wrapperClass.equals(CDAWrapperClass))) isClinicalDoc = true;
		return isClinicalDoc;		
	}
	
	/**
	 * add a row to the csv file of warnings, and sometimes give a warning to the user - if he 
	 * has not asked to suppress warnings
	 * @param path
	 * @param att
	 * @param message message to be put in the row of the warnings csv file
	 * @param warningMessage message to be shown interactively
	 */
	private void warnAndAddWarningRow(String path, String att, String message, String warningMessage)
	{
		// show a warning if the user has not decided to suppress warnings
		if (warningState != WARNINGS_SUPPRESS)
		{
			WorkBenchUtil.showMessage("Warning",warningMessage);
			// if the user has not yet been asked whether to suppress warnings, ask once
			if (warningState == WARNINGS_ASK_USER)
			{
				boolean suppress = WorkBenchUtil.askConfirm("Future Warning Messages", "Suppress further warning messages (which can be found in the Warnings csv file) ?");
				if (suppress) warningState = WARNINGS_SUPPRESS;
				else warningState = WARNINGS_SHOW_ALL;
			}			
		}		

		// always add a row to the csv file of warnings
		String [] warningRow = new String[3];
		warningRow[0] = path;
		warningRow[1] = att;
		warningRow[2] = message;
		warnings.add(warningRow);		
	}
	
	 /*
	  * Setting the grouping node in the simple structure and the object mapping in the full structure
	 * as side-effects of method makeNextGroupingClass does not work.
	 * So I have had to make nextGroupingNode an instance variable of the class,
	 * so it can set them.
	 */
	
	private ElementDef nextGroupingNode;
	
	/**
	 * object mappings in the full mapping set have some fixed value conditions.
	 * Ensure that the full mapping set has the AttributeDef nodes to carry those values, and 
	 * that the descriptions of those nodes say what the fixed values are.
	 * This method does recursion down the mapped structure, finding the corresponding LabelledEClass
	 * @param fullNode
	 * @param fullClass
	 */
	private void addNodesForFixedValues(ElementDef fullNode,LabelledEClass fullClass) throws MapperException
	{
		addNodesForFixedValuesOnOneElement(fullNode, fullClass);
		
		for (Iterator<ElementDef> it = fullNode.getChildElements().iterator();it.hasNext();)
		{
			ElementDef nextFullNode = it.next();
			String fullClassName = removePrefix(nextFullNode.getName());
			LabelledEClass nextFullClass = fullClass.getNamedAssocChild(fullClassName);
			if (nextFullClass == null) WorkBenchUtil.showMessage("Error in source class model","Cannot find association in source class model for ElementDef '" + nextFullNode.getName() + "'");
			addNodesForFixedValues(nextFullNode,nextFullClass);
		}
	}
	
	private String removePrefix(String nodeName)
	{
		String name = "";
		StringTokenizer st = new StringTokenizer(nodeName,":");
		while (st.hasMoreTokens()) name = st.nextToken();
		return name;
	}
	
	/**
	 * adding nodes to the full mapping set for fixed values.
	 * This method iterates over all fixed values on mappings on one ElementDef
	 * @param fullNode
	 * @param fullClass
	 */
	private void addNodesForFixedValuesOnOneElement(ElementDef fullNode,LabelledEClass fullClass) throws MapperException
	{
		NodeMappingSet nms = fullNode.getNodeMappingSet();
		if (nms != null) for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator();it.hasNext();)
		{
			ObjMapping objMapping = it.next();
			for (Iterator<MappingCondition> iu = objMapping.getMappingConditions().iterator();iu.hasNext();)
			{
				MappingCondition mappingCondition = iu.next();
				if (mappingCondition instanceof ValueCondition)
				{
					ValueCondition valueCondition = (ValueCondition)mappingCondition;
					String path = valueCondition.getLeftPath();
					String value = valueCondition.getRightValue();
					addNodesForOneFixedValue(fullNode,fullClass,path, value);
				}
			}
		}
	}
	
	/**
	 * recursive descent of the path in one fixed value on an ObjMapping,
	 * adding ElementDefs and AttributeDefs where necessary, and noting the
	 * fixed value on the AttributeDef 
	 * @param fullNode
	 * @param fullClass
	 * @param path
	 * @param value
	 */
	private void addNodesForOneFixedValue(ElementDef fullNode,LabelledEClass fullClass,String path, String value)  throws MapperException
	{
		String step = firstNode(path);
		// end of the path; add an attributeDef if needed, and note its value
		if (step.startsWith("@"))
		{
			String attName = step.substring(1);
			AttributeDef attDef = fullNode.getNamedAttribute(attName);
			if (attDef == null)
			{
				attDef = MapperFactory.eINSTANCE.createAttributeDef();
				attDef.setName(attName);
				attDef.setMinMultiplicity(MinMult.ONE);
				fullNode.getAttributeDefs().add(attDef);
			}
			attDef.setDescription(FIXED + value);
		}
		// an ElementDef along the path; find or make the ElementDef, and recurse
		else
		{
			ElementDef childEl = fullNode.getNamedChildElement(step);
			if (childEl == null)
			{
				childEl = MapperFactory.eINSTANCE.createElementDef();
				childEl.setName(putInCorrectNamespace(step));
				childEl.setMinMultiplicity(MinMult.ONE);
				childEl.setMaxMultiplicity(MaxMult.ONE);
				addAtCorrectPosition(fullNode,childEl,fullClass);
			}
			LabelledEClass fullChildClass = fullClass.getNamedAssocChild(removePrefix(step));
			String newPath = restOfPath(path);
			if (newPath.equals("")) throw new MapperException("Fixed value condition on element '" + step + "': value = " + value);
			addNodesForOneFixedValue(childEl,fullChildClass,newPath,value);
		}
	}
	
	/**
	 * Use the list of ERefences of the class in the full class model to find the correct position in
	 * the childElemenDefs of an ElementDef, to put a new child; and put it there
	 * @param fullNode
	 * @param childEl
	 * @param fullClass
	 * @throws MapperException
	 */
	private void addAtCorrectPosition(ElementDef fullNode,ElementDef childEl, LabelledEClass fullClass) throws MapperException
	{
		EList<EReference> refList = fullClass.eClass().getEAllReferences();
		int correctPosition = 0;
		// increase the target position by 1 for every ElementDef found before the new element in the list of associations of the class
		boolean foundInRefList = false;
		for (Iterator<EReference> it = refList.iterator();it.hasNext();)
		{
			String refName = it.next().getName();
			if (refName.equals(removePrefix(childEl.getName()))) foundInRefList = true;
			if ((fullNode.getNamedChildElement(refName) != null) && (!foundInRefList)) correctPosition++;
		}
		if (!foundInRefList) throw new MapperException("Found no correct position for new child element '" + childEl.getName() + "'");
		
		// add the newElementDef at the end of the list, then move it to the right position
		EList<ElementDef> children = fullNode.getChildElements();
		children.add(childEl);
		children.move(correctPosition, childEl);
	}

	
	/**
	 * name of the first step in a path
	 * @param path
	 * @return
	 */
	private String firstNode(String path)
	{
		StringTokenizer steps = new StringTokenizer(path,"/");
		return steps.nextToken();
	}

	
	/**
	 * remainder of a path, when the first step has been taken off
	 * @param path
	 * @return
	 */
	private String restOfPath(String path)
	{
		StringTokenizer steps = new StringTokenizer(path,"/");
		steps.nextToken();
		String rest = "";
		while (steps.hasMoreTokens())
		{
			rest = rest + steps.nextToken();
			if (steps.hasMoreTokens()) rest = rest + "/";
		}
		return rest;
	}



	
	/**
	 * recurse down the full class model, making the simple class model and the
	 * simple and full message mapping sets
	 * @param fullClass  current class in the full class model (drives the recursion)
	 * @param simpleClass current class in the simple class model
	 * @param fullNode current node in the full mapping set
	 * @param simpleNode current node in the simple mapping set
	 * @param zeroLowerBound true if some association with lower bound 0 
	 * has been encountered since the last simple class
	 * @throws MapperException
	 */
	private void fillMappingNodes(LabelledEClass fullClass,LabelledEClass simpleClass,
			ElementDef fullNode,ElementDef simpleNode, boolean zeroLowerBound,boolean ordered)
	throws MapperException
	{
		for (Iterator<EAttribute> ia = fullClass.eClass().getEAllAttributes().iterator();ia.hasNext();)
		{
			EAttribute att = ia.next();
			fillAttributeNode(att,fullClass,simpleClass,fullNode,simpleNode,zeroLowerBound);
		} 
		
		for (Iterator<EReference> ir = fullClass.eClass().getEAllReferences().iterator();ir.hasNext();)
		{
			EReference ref = ir.next();
			fillAssociationNode(ref,fullClass,simpleClass,fullNode,simpleNode,zeroLowerBound,ordered);
		} 	
	}
	
	/**
	 * add a position attributeDef to an ElementDef - which may be in the simple or full mapping set
	 * @param elDef
	 */
	private AttributeDef addPositionAttributeDef(ElementDef elDef)
	{
		AttributeDef posAtt = MapperFactory.eINSTANCE.createAttributeDef();
		posAtt.setName(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE);
		elDef.getAttributeDefs().add(posAtt);
		return posAtt;
	}
	
	public static String POSITION_PROPERTY_NAME = "order_property";
	
	/**
	 * add a position EAttribute to a class in the simple class model, to be mapped to the position attributes
	 * in simple and full mapping sets
	 * @param theClass
	 */
	private EAttribute addPositionEAttribute(LabelledEClass theClass)
	{
		EStructuralFeature feat = theClass.eClass().getEStructuralFeature(POSITION_PROPERTY_NAME);
		if ((feat != null) && (feat instanceof EAttribute)) return (EAttribute)feat;
		else
		{
			EAttribute pos = EcoreFactory.eINSTANCE.createEAttribute();
			pos.setName(POSITION_PROPERTY_NAME);
			pos.setEType(EcorePackage.eINSTANCE.getEInt()); 
			theClass.eClass().getEStructuralFeatures().add(pos);
			return pos;
		}
	}
	
	/**
	 * @param ref
	 * @param fullClass
	 * @param simpleClass
	 * @param fullNode
	 * @param simpleNode
	 * @param zeroLowerBound
	 * @param ordered  if the parent node required its child nodes to be ordered
	 * @throws MapperException
	 */
	private void fillAssociationNode(EReference ref,LabelledEClass fullClass,LabelledEClass simpleClass,
			ElementDef fullNode,ElementDef simpleNode, boolean zeroLowerBound, boolean ordered)
	throws MapperException
	{
		String sourceClassName = fullClass.eClass().getName();
		LabelledEClass nextFullClass = fullClass.getNamedAssocChild(ref.getName());
		ITSAssociation itsa = FeatureView.getITSAssociation(ref, fullClass.getPath());

		
		if ((!itsa.attsIncluded()) && (AssociationView.getConstrainedLowerBound(ref, fullClass) == 1)
				&& (!nextFullClass.hasSomeFixedValue()) && (!ref.getName().equals("contentId")))
			warnMissingAssociation(ref, fullClass);
		
		/* put contentId nodes in the full mapping set, with no mappings or child nodes,
		 * so the wrapper transform will know when to add them */
		if (ref.getName().equals("contentId"))
		{
			ElementDef nextFullNode = MapperFactory.eINSTANCE.createElementDef();
			String fullNodeName = putInCorrectNamespace(ref.getName());
			nextFullNode.setName(fullNodeName);
			// multiplicities do not matter, as only the existence of the node is used
			fullNode.getChildElements().add(nextFullNode);			
		}

		/* carry on down the full class model only if the association has included attributes below it */
		if (itsa.attsIncluded())
		{
			// true if this node requires its child nodes to be ordered 
			boolean orderChildNodes = itsa.childrenAreOrdered();
			
			// warn the user if a <text> association has been renamed or collapsed
			if (ref.getName().equals("text")) warnIfTextElementRenamed(fullClass, itsa);
							
			// make a child node in the full mapping set, whether or not the association is collapsed
			ElementDef nextFullNode = MapperFactory.eINSTANCE.createElementDef();
			String fullNodeName = putInCorrectNamespace(ref.getName());
			nextFullNode.setName(fullNodeName);
			// initially take multiplicities from the association
			setMultiplicities(nextFullNode,ref,fullClass);
			// but if the association is collapsed, set max multiplicity  in the full mapping set to 1
			if (itsa.isCollapsed()) nextFullNode.setMaxMultiplicity(MaxMult.ONE);
			
			// add a position attribute if the parent node required it
			AttributeDef fullPosAttDef = null;
			if (ordered) fullPosAttDef  = addPositionAttributeDef(nextFullNode);

			// mark the full mapping node with the CDA tag name (to be used when writing a schema)
			String CDATagName = ModelUtil.getMIFAnnotation(ref, "CDA_Name");
			if (CDATagName != null) nextFullNode.setDescription(CDA_NAME + CDATagName);
			nextFullNode.setExpanded(true);
			fullNode.getChildElements().add(nextFullNode);
			
			// will be set and passed down the recursion
			ElementDef nextSimpleNode = null;
			LabelledEClass nextSimpleClass = null;
			boolean nextZeroLowerBound = true;
			
			// if the association is collapsed, do not change the simple node or the simple class 
			if (itsa.isCollapsed())
			{
				nextSimpleNode = simpleNode;
				nextSimpleClass = simpleClass;
				/* if any association in a chain of collapsed associations has lower bound 0,
				 * then the association in the simple class model has lower bound 0 */
				nextZeroLowerBound = (zeroLowerBound || (AssociationView.getConstrainedLowerBound(ref, fullClass) == 0));
				// recursive step
				if (nextFullClass != null) 
					fillMappingNodes(nextFullClass,nextSimpleClass,nextFullNode,nextSimpleNode,nextZeroLowerBound,orderChildNodes);
				else System.out.println("Null class at full mapping set path " + nextFullNode.getPath());
			}

			/* if the association is not collapsed and is manually retained for included attributes below it, 
			 * make a new node in the simple mappings, and find or create a new class in the simple model */
			else if ((!itsa.isCollapsed()) && (itsa.attsIncluded()))
			{
				
				//** ASSUMPTION: grouping nodes are not mixed with conditional node names in the same annotation
				// detect renamed nodes and grouping nodes
				String suggestedSimpleNodeName = ref.getName();
				Vector<String> groupingNodeNames = new Vector<String>();
				StringTokenizer st = new StringTokenizer(itsa.businessName(),"/"); // may have 0, 1, or many tokens
				while (st.hasMoreTokens())
				{
					String nodeName = st.nextToken();
					groupingNodeNames.add(nodeName);
					suggestedSimpleNodeName = nodeName; // set to the last, if there are any; may be changed below
				}
				
				//** (not implemented) parse suggestedSimpleNodeName into N conditional node names, and iterate over them.
				//** (not implemented) For each name, check that the condition value matches the condition value of simpleClass.
				//** (not implemented) Must always rename associations after a conditional branch, if simpleClass has a condition value

				
				/* When there are no grouping nodes, hang the new class on the parent simple class
				 * when there are grouping nodes, check if they hang in sequence from the current parent simple class */
				LabelledEClass groupingClass = simpleClass;
				ElementDef groupingNode = simpleNode;
				
				/* when the annotation has a business name with one or more '/' such as 'group/node' , 
				 * introducing grouping nodes. Don't treat the last element, which is not a grouping node. */
				for (int i = 0; i < groupingNodeNames.size()-1; i++)
				{
					String nextGroupingNodeName = groupingNodeNames.get(i);
					/* check if the grouping class is already a child class of the parent class in the simple class model;
					 * if not, make it */ 
					LabelledEClass nextGroupingClass = groupingClass.getNamedAssocChild(nextGroupingNodeName);
					nextGroupingNode = groupingNode.getNamedChildElement(nextGroupingNodeName);

					if (nextGroupingClass == null)
						nextGroupingClass =  makeNextGroupingClass(nextGroupingNodeName, sourceClassName,
								 groupingClass,  groupingNode, fullNode,  simpleClass);

					// ready for end of iteration, or next round of iteration
					groupingClass = nextGroupingClass;
					groupingNode = nextGroupingNode;
				} // end of grouping node code
				
				/* things to do whether or not there is a grouping class */
				
				// make or find the class in the simple class model
				String nextSimpleClassName = makeClassName(suggestedSimpleNodeName);
				nextSimpleClass = findOrMakeClass(getSimplePackageName(nextFullClass.eClass()),nextSimpleClassName,suggestedSimpleNodeName,groupingClass);

				// give the class an ordering attribute if it needs one
				EAttribute posAtt = null;
				if (ordered) posAtt = addPositionEAttribute(nextSimpleClass);
				
				//** (not implemented) if the simple node name has a path with a value condition, annotate these on the simple class
								
				// make the containment association in the simple class model - possibly with a de-duped name
				EReference newRef = makeSimpleModelERef(groupingClass, nextSimpleClass, 
					 suggestedSimpleNodeName,  sourceClassName);
				// when you add a LabelledEClass child, it has an association name - but is it right?
				if (groupingClass.getNamedAssocChild(newRef.getName()) == null) groupingClass.getChildren().add(nextSimpleClass);

				/* make and name the new simple node and add it beneath its expected parent, if it is not there already. 
				 * If it is there already, there should be a check of the multiplicities, but I have not yet implemented it.*/
				nextSimpleNode = groupingNode.getNamedChildElement(newRef.getName());
				if (nextSimpleNode == null)
				{
					nextSimpleNode = MapperFactory.eINSTANCE.createElementDef();
					nextSimpleNode.setExpanded(true);
					nextSimpleNode.setName(newRef.getName()); 
					groupingNode.getChildElements().add(nextSimpleNode);
					String desc = "From association '" + ref.getName() + "' of class '" + sourceClassName + "'";
					nextSimpleNode.setDescription(desc);
				}
				
				// if the parent requires ordering of its children, add the simple AttributeDef and the mappings
				if (ordered)
				{
					AttributeDef simplePosAttDef = addPositionAttributeDef(nextSimpleNode);
					addPropertyMapping(simplePosAttDef,nextSimpleClass,posAtt);
					addPropertyMapping(fullPosAttDef,nextSimpleClass,posAtt);
				}

				// get the multiplicities right in the simple class model and the simple mapping set
				setCollapsedMultiplicities(fullClass,nextSimpleNode, ref,newRef,zeroLowerBound);
				nextZeroLowerBound = false;
				
				// add mappings, if there is not one already in the simple mapping set
				if (!mappingExistsAlready(nextSimpleNode,nextSimpleClass))
				{
					makeObjectAndAssocMapping(nextSimpleNode,nextSimpleClass,newRef,groupingClass);
					//** (not implemented) if nextSimpleClass has a value condition annotation, make the full mappings conditional
					ObjMapping nextFullObjectMapping = makeObjectAndAssocMapping(nextFullNode,nextSimpleClass,newRef,groupingClass);
					addFixedValueConditions(nextFullObjectMapping, nextFullClass,ref);					
				}
				
				// recursive step
				fillMappingNodes(nextFullClass,nextSimpleClass,nextFullNode,nextSimpleNode,nextZeroLowerBound,orderChildNodes);

				//** (not implemented) end of new iteration over value conditions
				
			} // end of ((!itsa.isCollapsed()) && (itsa.attsIncluded())) section
			
		}  // end of ((itsa.attsIncluded())||(autoRetain)) section		
	}
	
	/**
	 * 
	 * @param el
	 * @param theClass
	 * @return true if the ElementDef already has a mapping to the class - not sensitive to subsets
	 */
	private boolean mappingExistsAlready(ElementDef el, LabelledEClass theClass)
	{
		boolean found = false;
		NodeMappingSet nms = el.getNodeMappingSet();
		if (nms != null) for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator();it.hasNext();)
		{
			ObjMapping om = it.next();
			if (om.getClassSet().className().equals(ModelUtil.getQualifiedClassName(theClass.eClass()))) found = true;
		}
		return found;
	}



	
	/**
	 * 
	 * @param att
	 * @param fullClass
	 * @param simpleClass
	 * @param fullNode
	 * @param simpleNode
	 * @param fullObjMapping
	 * @param zeroLowerBound
	 * @throws MapperException
	 */
	private void fillAttributeNode(EAttribute att,LabelledEClass fullClass,LabelledEClass simpleClass,
			ElementDef fullNode,ElementDef simpleNode, boolean zeroLowerBound)
	throws MapperException
	{
		ITSAttribute itsAtt = FeatureView.getITSAttribute(att, fullClass.getPath());
		String fixedValue = fullClass.getAnnotatedFixedValue(att.getName());
		String sourceClassName = fullClass.eClass().getName();

		// warn where an attribute required in the full message cannot be supplied
		if ((!itsAtt.isIncluded()) && (fixedValue == null)
				&& (AttributeView.getConstrainedLowerBound(att, fullClass) == 1) && (!att.getName().equals("xsi:type")))
			warnRequiredAttributeMissing(att, fullClass);
		
		if (itsAtt.isIncluded())
		{
			/* warn the user if an attribute with a fixed value has been included in the simple model;
			 * it will not be included, as this would lead to gaps in translations */
			if (fixedValue != null) warnIfFixedAttIncluded(att, fullClass, fixedValue);

			else if (fixedValue == null)
			{
				String suggestedAttName = att.getName();
				if (!(itsAtt.businessName().equals(""))) suggestedAttName = itsAtt.businessName();
							
				//** (not implemented) parse suggestedAttName to get N different conditional names, and iterate over them
				//** (not implemented) for each, check that the condition value matches the condition value of simpleClass
				//** (not implemented) must always rename attributes after a conditional branch, if simpleClass has a condition value

				// add the property to the simple class model, if it has not been added before; this may change the suggested name
				EAttribute theAtt = addAttributeToSimpleClassModel(fullClass, simpleClass, att,suggestedAttName, sourceClassName,zeroLowerBound);
								
				AttributeDef simpleAtt = null;
				AttributeDef fullAtt = null;

				// add an Attribute node to the simple mapping set, with possibly changed name, and map it
				// add an Attribute node to the full mapping set, with the unchanged name, if it does not exist already
				//** (not implemented) if the attribute has a condition value, add a mapping condition
				if (!(att.getName().equals("textContent")))
				{
					simpleAtt = addSimpleAttribute(att, fullClass, simpleClass, simpleNode, sourceClassName, theAtt,zeroLowerBound);
					fullAtt = addFullAttribute(att, fullClass, simpleClass, fullNode, simpleAtt, theAtt);					
				}				
				
				// for textContent nodes, add a property mapping on the ElementDef in each mapping set
				else 
				{
					addPropertyMapping(simpleNode,simpleClass, theAtt);					
					addPropertyMapping(fullNode,simpleClass, theAtt);
					matchedNodes.put(fullNode, simpleNode); // note the correspondence between the two mapping sets
				}
				
				// add a row to the csv file of path-to-path mappings
				addPathToPathMapping(att, fullNode, simpleNode, simpleAtt,fullAtt);
				
				//** (not implemented) end of new iteration over conditional attribute names
				
			} // end of (fixedValue == null) section
		} // end of (itsAtt.isIncluded()) section		
	}


	private void warnRequiredAttributeMissing(EAttribute att,
			LabelledEClass fullClass) {
		{
			String warning = "Attribute " + att.getName() + " at path " + fullClass.getPath() + 
				" is required in the full message, but has not been included in the simplified message, and has no fixed value.";
			warnAndAddWarningRow(fullClass.getPath(),"@" + att.getName(),"Attribute is required but not included or fixed",warning);
		}
	}


	private AttributeDef addFullAttribute(EAttribute att,
			LabelledEClass fullClass,LabelledEClass simpleClass,  ElementDef fullNode,
			AttributeDef simpleAtt, EAttribute theAtt) {
		AttributeDef fullAtt;
		{
			fullAtt = fullNode.getNamedAttribute(att.getName());
			if (fullAtt== null)
			{
				fullAtt = MapperFactory.eINSTANCE.createAttributeDef();
				fullAtt.setName(att.getName());
				setAttributeMultiplicity(fullAtt,AttributeView.getConstrainedLowerBound(att,fullClass));
				fullNode.getAttributeDefs().add(fullAtt);						
			}
			addPropertyMapping(fullAtt,simpleClass, theAtt);					
			matchedNodes.put(fullAtt, simpleAtt); // note the correspondence between the two mapping sets
		}
		return fullAtt;
	}


	private AttributeDef addSimpleAttribute(EAttribute att,
			LabelledEClass fullClass,LabelledEClass simpleClass,  ElementDef simpleNode,
			String sourceClassName, EAttribute theAtt, boolean zeroLowerBound) {
		AttributeDef simpleAtt;
		{
			simpleAtt = MapperFactory.eINSTANCE.createAttributeDef();
			simpleAtt.setName(theAtt.getName());
			String desc = "From attribute '" + att.getName() + "' of class '" + sourceClassName + "'";
			simpleAtt.setDescription(desc);
			setAttributeMultiplicity(simpleAtt,AttributeView.getConstrainedLowerBound(att,fullClass));
			if (zeroLowerBound) setAttributeMultiplicity(simpleAtt,0);
			simpleNode.getAttributeDefs().add(simpleAtt);				
			addPropertyMapping(simpleAtt,simpleClass, theAtt);					
		}
		return simpleAtt;
	}


	private void warnIfFixedAttIncluded(EAttribute att,
			LabelledEClass fullClass, String fixedValue) {
		{
			String warning = "Attribute '" + att.getName() + "' at path " + fullClass.getPath() +
				" has fixed value '" + fixedValue + "', so cannot be included in the simple message.";
			warnAndAddWarningRow(fullClass.getPath(),"@" + att.getName(),"Attribute has a fixed value so cannot be included in the simple message.",warning);
		}
	}

	private void warnMissingAssociation(EReference ref, LabelledEClass fullClass) {
		{
			String warning = "Association " + ref.getName() + " at path " + fullClass.getPath()
			 	+ " is required in the full message, but no attributes below this association have been included in the simple message. ";
			warnAndAddWarningRow(fullClass.getPath(),ref.getName(),"Association is required but nothing below it is included",warning);
		}
	}


	
	
	private void addPathToPathMapping(EAttribute att, ElementDef fullNode,
			ElementDef simpleNode, AttributeDef simpleAtt, AttributeDef fullAtt) {
		{
			String fullPath = fullNode.getPath();
			String simplePath = simpleNode.getPath();
			if (!(att.getName().equals("textContent")))
			{
				fullPath = fullPath + "/@" + fullAtt.getName();
				//** (not implemented) add any value condition in [] to fullPath
				simplePath = simplePath + "/@" + simpleAtt.getName();
			}
			String[] csvRow = new String[5];
			csvRow[0] = simplePath;
			csvRow[1] = fullPath;
			csvRow[2] = "";
			csvRow[3] = "";
			csvRow[4] = "";
			attributeMappings.add(csvRow);					
		}
	}


	/**
	 * 
	 * @param fullClass
	 * @param itsa
	 */
	private void warnIfTextElementRenamed(LabelledEClass fullClass,
			ITSAssociation itsa) 
	{
		{
			String preface = "The association 'text' at path " + fullClass.getPath();
			String rename = itsa.businessName();
			if ((rename != null) && (!rename.equals("")))
			{
				String warning = preface + " has been renamed; its text subtree will not be retained.";
				warnAndAddWarningRow(fullClass.getPath(),"text","Text node renamed; full text subtree will not be retained",warning);
			}
			if (itsa.isCollapsed())
			{
				String warning = preface + " has been flattened; its text subtree will not be retained.";
				warnAndAddWarningRow(fullClass.getPath(),"text","Text node flattened; full text subtree will not be retained",warning);
			}
		}
	}
	
	/**
	 * make a grouping class, its association to its parent class, the grouping node in 
	 * the simple message structure, and the mappings in simple and full mapping sets
	 * 
	 * return the grouping class. 
	 * Setting the grouping node in the simple structure
	 * as side-effects of method makeNextGroupingClass does not work.
	 * So I have had to make nextGroupingNode an instance variable of the class.
	 * 
	 * @param nextGroupingNodeName
	 * @param sourceClassName
	 * @param groupingClass
	 * @param groupingNode
	 * @param fullNode
	 * @param simpleClass
	 * @throws MapperException
	 * @return the next grouping class
	 */
	private LabelledEClass makeNextGroupingClass(String nextGroupingNodeName, String sourceClassName,
			LabelledEClass groupingClass, ElementDef groupingNode, 
			ElementDef fullNode, LabelledEClass simpleClass)
			throws MapperException
	{
		String nextGroupingClassName = makeClassName(nextGroupingNodeName);
	    String packageName = groupingClass.eClass().getEPackage().getName();
		LabelledEClass nextGroupingClass = findOrMakeClass(packageName,nextGroupingClassName,nextGroupingNodeName,groupingClass);
		groupingClass.getChildren().add(nextGroupingClass);							

		// make the containment association to the grouping class in the simple class model - failing if the name changes
		EReference nextGroupingRef = makeSimpleModelERef(groupingClass, nextGroupingClass, 
				nextGroupingNodeName,  sourceClassName);
		if (!nextGroupingRef.getName().equals(nextGroupingNodeName))
			throw new MapperException("Cannot change grouping node name from '" + nextGroupingNodeName + "' to '" + nextGroupingRef.getName() + "'");

		// make and name the grouping node, and add it beneath its expected parent
		nextGroupingNode = MapperFactory.eINSTANCE.createElementDef();
		nextGroupingNode.setExpanded(true);
		nextGroupingNode.setName(nextGroupingNodeName); 
		nextGroupingNode.setDescription("Grouping association");
		groupingNode.getChildElements().add(nextGroupingNode);
		
		// set multiplicities appropriate for a grouping node
		nextGroupingRef.setLowerBound(1);
		nextGroupingRef.setUpperBound(1);
		nextGroupingNode.setMinMultiplicity(MinMult.ONE);
		nextGroupingNode.setMaxMultiplicity(MaxMult.ONE);
		
		// make mappings to the next grouping class in simple and full mapping sets
		// simple mappings to the grouping class on the grouping node
		makeObjectAndAssocMapping(nextGroupingNode,nextGroupingClass,nextGroupingRef,groupingClass);

		// full mappings to the next grouping class on the node representing the parent class
		ElementDef parentMappingNode = classMappingNode(fullNode,simpleClass);
		makeObjectAndAssocMapping(parentMappingNode,nextGroupingClass,nextGroupingRef,groupingClass);
		// this object mapping has no fixed value conditions
		
		return nextGroupingClass;
	}
	
	/**
	 * for full mapping sets for NHS CDAs, some elements must be put in the NHS localisation namespace
	 * @param fullNodeName
	 * @return
	 */
	private String putInCorrectNamespace(String fullNodeName)
	{
		String result = fullNodeName;
		if (isNHSMIF())
		{
			if (GenUtil.inArray(fullNodeName, NHS_CDA_Wrapper.IN_NHS_NAMESPACE))
				result = NHS_CDA_Wrapper.NHSPREFIX + ":" + fullNodeName;
		}
		return result;
	}
	
	/**
	 * 
	 * @param fullNode
	 * @param simpleClass
	 * @return fullNode or the node above it which has a mapping to class simpleClass
	 */
	private ElementDef  classMappingNode(ElementDef fullNode,LabelledEClass simpleClass) throws MapperException
	{
		String className = simpleClass.eClass().getName();
		ElementDef mappingNode = fullNode;
		boolean found = false;
		while ((!found) && (mappingNode != null))
		{
			if (mappingNode.getNodeMappingSet() != null)
			{
				EList<ObjMapping> mappings = mappingNode.getNodeMappingSet().getObjectMappings();
				for (Iterator<ObjMapping> it = mappings.iterator();it.hasNext();)
				{
					ObjMapping mapping = it.next();
					if (mapping.getMappedClass().equals(className)) found = true;
				}
			}
			
			if (!found)
			{
				EObject parent = mappingNode.eContainer();
				if (parent instanceof ElementDef) mappingNode = (ElementDef)parent;
				else mappingNode = null;
			}
		}
		if (!found) throw new MapperException("Cannot find mapping for class " + className + " in path " + fullNode.getPath());
		return mappingNode;
	}
			
	
	
	/**
	 * make a new EAttribute in the simple class model, if necessary.
	 * If the EAttribute with the suggested name already exists, reuse it.
	 * @param simpleClass
	 * @param att
	 * @param suggestedAttName
	 * @param sourceClassName
	 * @return
	 */
	private EAttribute addAttributeToSimpleClassModel(LabelledEClass fullClass,LabelledEClass simpleClass, EAttribute att,
			String suggestedAttName,String sourceClassName, boolean zeroLowerBound)
	{
		String aName = suggestedAttName;
		// find if the simple class already has an EAttribute of this name
		EStructuralFeature previous = simpleClass.eClass().getEStructuralFeature(aName);
		if ((previous != null) && (previous instanceof EAttribute))
		{
			// extend the 'sourceClass' annotation if necessary; but I don't think it is used
			String previousSources = ModelUtil.getMIFAnnotation(previous, "sourceClass");
			StringTokenizer st = new StringTokenizer(previousSources,";");
			boolean foundSource = false;
			while (st.hasMoreTokens()) if (st.nextToken().equals(sourceClassName)) foundSource = true;
			if (!foundSource) ModelUtil.addMIFAnnotation(previous,"sourceClass",previousSources + ";" + sourceClassName);
			return (EAttribute)previous;
		}
		
		EAttribute theAtt = EcoreFactory.eINSTANCE.createEAttribute();
		theAtt.setName(aName);
		theAtt.setEType(EcorePackage.eINSTANCE.getEString()); 
		theAtt.setLowerBound(AttributeView.getConstrainedLowerBound(att, fullClass));
		if (zeroLowerBound) theAtt.setLowerBound(0);
		ModelUtil.addMIFAnnotation(theAtt, "sourceClass", sourceClassName);
		simpleClass.eClass().getEStructuralFeatures().add(theAtt);
		return theAtt;		
	}
	
	/**
	 * make a new EReference in the simple class model, if necessary.
	 * If the ERef with the suggested name already exists, reuse it.
	 * @param simpleClass
	 * @param nextSimpleClass
	 * @param suggestedSimpleNodeName
	 * @param fullClassName
	 * @return
	 */
	private EReference makeSimpleModelERef(LabelledEClass simpleClass, LabelledEClass nextSimpleClass, 
			String suggestedSimpleNodeName, String sourceClassName)
	{
		String refName = suggestedSimpleNodeName;
		// find if the simple class already has an EReference of this name
		EStructuralFeature previous = simpleClass.eClass().getEStructuralFeature(refName);
		if ((previous != null) && (previous instanceof EReference))
		{
			// extend the 'sourceClass' annotation if necessary; but I don't think it is used
			String previousSources = ModelUtil.getMIFAnnotation(previous, "sourceClass");
			StringTokenizer st = new StringTokenizer(previousSources,";");
			boolean foundSource = false;
			while (st.hasMoreTokens()) if (st.nextToken().equals(sourceClassName)) foundSource = true;
			if (!foundSource) ModelUtil.addMIFAnnotation(previous,"sourceClass",previousSources + ";" + sourceClassName);
			return (EReference)previous;
		}

		// make the containment association in the simple class model
		EReference newRef = EcoreFactory.eINSTANCE.createEReference();
		newRef.setName(refName);
		newRef.setContainment(true);
		newRef.setEType(nextSimpleClass.eClass());
		
		// annotate it with the full class it came from
		ModelUtil.addMIFAnnotation(newRef, "sourceClass", sourceClassName);

		simpleClass.eClass().getEStructuralFeatures().add(newRef);
		return newRef;
		
	}
	
	

	
	/**
	 * add all necessary fixed value condition to an object mapping in the full mapping set
	 * @param fullObjMapping
	 * @param fullClass
	 * @throws MapperException
	 */
	private void addFixedValueConditions(ObjMapping fullObjMapping, LabelledEClass fullClass, EReference ref)  throws MapperException
	{
		String path = "";
		@SuppressWarnings("unused")
		String refName = "none";
		if (ref !=null) refName = ref.getName();
		@SuppressWarnings("unused")
		String className = fullObjMapping.getMappedClass();
		// System.out.println("Fixed values on object mapping " + className  + " with full class " + fullClass.eClass().getName() + " and ref " + refName);
		addDeepFixedValueConditions(path, fullObjMapping, fullClass,ref);
	}
	

	
	/**
	 * recursion down 1..1 associations to add all necessary fixed value conditions to an object mapping in the full mapping set
	 * @param path path through associations so far  - e.g "" or "className/"
	 * @param fullObjMapping
	 * @param fullClass
	 * @throws MapperException
	 */
	private void addDeepFixedValueConditions(String path, ObjMapping fullObjMapping, LabelledEClass fullClass, EReference ref) throws MapperException
	{
		StringTokenizer st = new StringTokenizer(path,"/");
		int maxDepth = 8;
		if (st.countTokens() > maxDepth) throw new MapperException("Path '" + path + "' goes too deep when looking for fixed attribute values");
		
		/* deal with NHS fixed values, annotated on associations to data type classes.
		 * These include extensions of templateIds, which are II */
		if (ref != null) // ref is null for the top call from ClinicalDocument
		{
			String fixedVal = ModelUtil.getEAnnotationDetail(ref,"fixed att value");
			if (fixedVal != null)
			{
				String attName = getNHSFixedAttributeName(fullClass.eClass().getName());
				basicAddFixedValue(path, fullObjMapping, attName, fixedVal);				
			}
			/* we do not have the value of extension for an npfitlc:contentId attribute attached to it as an annotation.
			 * find it out from a grandchild templateId extension value annotation. */
			else if (ref.getName().equals("contentId"))
			{
				String extensionValue = getGrandchildTemplateIdExtension(ref);
				if (extensionValue != null) 
				{
					basicAddFixedValue(path, fullObjMapping, "root", NHSContentIdRoot);				
					basicAddFixedValue(path, fullObjMapping, "extension", extensionValue);				
				}
			}
		}
		
		// find attributes of this class with fixed values
		for (Iterator<EAttribute> it = fullClass.eClass().getEAllAttributes().iterator();it.hasNext();)
		{
			EAttribute att = it.next();
			String attName = att.getName();
			// fixed values of some attributes may be suppressed
			if ((suppressFixedTextMappings) && (GenUtil.inArray(attName, suppressableAttribute))) {}
			else
			{
				// get any kind of fixed value of the attribute - from the MIF file, or set by the user
				String fixedVal = fullClass.getAnnotatedFixedValue(attName);
				if (fixedVal != null) basicAddFixedValue(path, fullObjMapping, attName, fixedVal);				
			}
		}
		
		// find the names of associations to grouping classes
		Hashtable<String,String> groupers = groupingAssociations(fullClass);
		
		/* recursion down through all 1..1 associations, 
		 * leaving out those which have attributes beneath them and are not flattened, because fixed values
		 * below those nodes will go to a lower object mapping */
		for (Iterator<EReference> it = fullClass.eClass().getEAllReferences().iterator();it.hasNext();)
		{
			EReference nextRef = it.next();
			if ((AssociationView.getConstrainedLowerBound(nextRef, fullClass) == 1) 
					&& (AssociationView.getConstrainedUpperBound(nextRef, fullClass) == 1))
			{
				/* do nothing if there is a lower object mapping to hang the fixed values on 
				 * - that is , if the association has retained attributes beneath it, it is not collapsed,
				 * and its name is not the name of an association to a grouping class */
				ITSAssociation itsa = FeatureView.getITSAssociation(nextRef, fullClass.getPath());
				if ((itsa.attsIncluded())
						&& (!itsa.isCollapsed()) 
						&& (groupers.get(itsa.businessName()) == null)) {}
				else
				{
					LabelledEClass target = fullClass.getNamedAssocChild(nextRef.getName());
					String newPath = path + putInCorrectNamespace(nextRef.getName()) + "/";
					if (target != null) addDeepFixedValueConditions(newPath,fullObjMapping,target,nextRef);	
					else System.out.println("Null class at path " + newPath);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param ref a 'contentId' reference from some class in the source class model
	 * @return if the class has a child class with a templateId child, return the fixed att value annotation of that ref,
	 * which is the template id. Otherwise return null.
	 */
	private String getGrandchildTemplateIdExtension(EReference ref)
	{
		EClass ownerClass = (EClass)ref.eContainer();
		String idValue = null;
		// look for a child class with a templateId association
		for (Iterator<EReference> it = ownerClass.getEAllReferences().iterator();it.hasNext();)
		{
			EClass childClass = (EClass)it.next().getEType();
			EStructuralFeature feat = childClass.getEStructuralFeature("templateId");
			if (feat != null)
			{
				String fixedVal = ModelUtil.getEAnnotationDetail(feat,"fixed att value");
				if (fixedVal != null) idValue = fixedVal;
			}
		}
		return idValue;
	}

	
	/**
	 * collect the names of all associations to grouping classes immediately beneath this class
	 * @param fullClass
	 * @return
	 */
	private Hashtable<String,String> groupingAssociations(LabelledEClass fullClass)
	{
		Hashtable<String,String> assocs = new Hashtable<String,String>();
		for (Iterator<EReference> it = fullClass.eClass().getEAllReferences().iterator();it.hasNext();)
		{
			EReference nextRef = it.next();
			ITSAssociation itsa = FeatureView.getITSAssociation(nextRef, fullClass.getPath());
			if ((itsa != null) && (itsa.businessName() != null))
			{
				StringTokenizer st = new StringTokenizer(itsa.businessName(),"/");
				// for some associations, the grouping ref name is followed by '/' and sme other ref name
				if (st.countTokens() > 1)
				{
					String groupingRef = st.nextToken();
					assocs.put(groupingRef, "1");
				}
			}
		}
		return assocs;
	}
	


	private void basicAddFixedValue(String path, ObjMapping fullObjMapping,
			String attName, String fixedVal) {
		{
			String attPath  = path + "@" + attName;
			
			// make the Value condition and add it, without making duplicates (unnecessary?)
			ValueCondition vc = MapperFactory.eINSTANCE.createValueCondition();
			vc.setLeftPath(attPath);
			vc.setRightValue(fixedVal);
			if (getValueCondition(fullObjMapping,attPath) == null)
				fullObjMapping.getMappingConditions().add(vc);
		}
	}

	private String getNHSFixedAttributeName(String dataTypeName) throws MapperException
	{
		String attName="";
		for (int i = 0; i < NHSFixedAttributes.length; i++)
		{
			String[] fixedAtt = NHSFixedAttributes[i];
			if (dataTypeName.equals(fixedAtt[0])) attName = fixedAtt[1];
		}
		if (attName.equals("")) throw new MapperException("No defined attribute for fixed value of class '" + dataTypeName + "'");
		return attName;
	}

	
	
	private ValueCondition getValueCondition(ObjMapping fullObjMapping,String path)
	{
		ValueCondition vc = null;
		for (Iterator<MappingCondition> ic = fullObjMapping.getMappingConditions().iterator();ic.hasNext();)
		{
			MappingCondition mc = ic.next();
			if (mc instanceof ValueCondition)
			{
				ValueCondition v = (ValueCondition)mc;
				if (v.getLeftPath().equals(path)) vc = v;
			}
		}
		return vc;
	}
	
	/**
	 * set the multiplicities of a child ElementDef 
	 * and an EReference in the simplified model according to the 
	 * multiplicities of the EReference in the full model,
	 * with min multiplicity pushed down to zero if some higher association was 
	 * collapsed and had min multiplicity zero.
	 * @param next
	 * @param ref
	 */
	private void setCollapsedMultiplicities(LabelledEClass fullClass, ElementDef next,EReference ref, EReference newRef, boolean zeroLowerBound)
	{
		next.setMinMultiplicity(MinMult.ONE);
		newRef.setLowerBound(1);
		if ((AssociationView.getConstrainedLowerBound(ref, fullClass) == 0)|| zeroLowerBound) 
		{
			next.setMinMultiplicity(MinMult.ZERO);
			newRef.setLowerBound(0);
		}

		next.setMaxMultiplicity(MaxMult.ONE);
		if (AssociationView.getConstrainedUpperBound(ref, fullClass) == -1) next.setMaxMultiplicity(MaxMult.UNBOUNDED);
		newRef.setUpperBound(ref.getUpperBound());
	}
	
	private void setAttributeMultiplicity(AttributeDef attDef, int min)
	{
		if (min == 0) attDef.setMinMultiplicity(MinMult.ZERO);
		if (min == 1) attDef.setMinMultiplicity(MinMult.ONE);
	}

	
	/**
	 * make a class name from an association name, by converting its initial letter
	 * to upper case
	 * (if the initial letter is already upper case, write a gentle warning to the console.
	 * having class names and association names coincide is not a disaster)
	 * @param assocName
	 * @return
	 * @throws MapperException
	 */
	private String makeClassName(String assocName) throws MapperException
	{
		String first = assocName.substring(0, 1);
		String lowerFirst = first.toLowerCase();
		if (!(first.equals(lowerFirst))) System.out.println("Association name '" + assocName + "' should ideally start in lower case.");
		String upperFirst = first.toUpperCase();
		return (upperFirst + assocName.substring(1));
	}
	
	/**
	 * 
	 * @param className a class in the simple class model
	 * @param parent the parent LabelledEClass
	 * @return the subset to be used in mappings to this occurrence of the class.
	 * Different parent paths are guaranteed to give different subsets
	 */
	private String getSubset(String className, LabelledEClass parent)
	{
		String subset = "";
		// only one subset "" is needed for the root class which has no parent
		if (parent == null) return subset;
		
		Hashtable<String,String> subsetsForThisClass = subsetsForPaths.get(className);
		
		if (subsetsForThisClass == null)
		{
			subset = ""; // for clarity
			subsetsForThisClass = new Hashtable<String,String>();
			subsetsForThisClass.put(parent.getPath(), subset);
			subsetsForPaths.put(className, subsetsForThisClass);
		}
		else if (subsetsForThisClass != null)
		{
			subset = subsetsForThisClass.get(parent.getPath());
			if (subset == null)
			{
				subset = "s" + subsetsForThisClass.size(); // must be different from all existing ones
				subsetsForThisClass.put(parent.getPath(), subset);
				subsetsForPaths.put(className, subsetsForThisClass);
			}
		}
		
		// trace("subset for class " + className + " at path " + parent.getPath() + " : '" + subset + "'");
		return subset;
	}
	
	/**
	 * 
	 * @param fullClass current class in the full CDA class model, corresponding to a node in the CDA
	 * @return an appropriate name for the package of the simple class model, which the node in the 
	 * simple and full messages are going to be mapped to. This package name is 'CDA_DT' for data type classes,
	 * or 'CDA_' followed by a readable template name for any RIM class
	 * 
	 */
	private String getSimplePackageName(EClass fullClass)
	{
		EPackage fullPackage = selfOrChild(fullClass).getEPackage();
		if (fullPackage.getName().equals("datatypes")) return "datatypes";

		String readablePackageName = ModelUtil.getMIFAnnotation(fullPackage, "name"); // e.g. 'PatientUniversal'
		if (readablePackageName == null) readablePackageName = fullPackage.getName(); // e.g. 'POCD_RM010011GB02' ;should not happen
		// package names cannot have spaces, and start in lower case
		String simplePackageName = removeSpaces(readablePackageName);
		
		// store the link between template name and template id
		TemplatePackageIds.put(simplePackageName, fullPackage.getName());
		
		return simplePackageName;
	}
	
	/**
	 * 
	 * @param s
	 * @return s with spaces removed, and initial letter in lower case
	 */
	private String removeSpaces(String s)
	{
		String result = "";
		// remove spaces
		StringTokenizer st = new StringTokenizer(s," ");
		while (st.hasMoreTokens()) result = result + st.nextToken();
		// make the first letter lower case
		result = result.substring(0, 1).toLowerCase() + result.substring(1);
		return result;
	}
	
	/**
	 * 
	 * @param theClass
	 * @return the class itself, or, for an ActRelationship or a Participation, 
	 * the one child class which is a RIM class
	 */
	private EClass selfOrChild(EClass theClass)
	{
		EClass result = theClass;
		String[] dropClasses = {"ActRelationship","Participation"};
		String RIMClass = ModelUtil.getMIFAnnotation(theClass,"RIM Class");
		// for ActRelationships and Participations, return the child class which is not a data type class
		if ((RIMClass != null) && (GenUtil.inArray(RIMClass, dropClasses)))
		{
			for (Iterator<EReference> it = theClass.getEReferences().iterator();it.hasNext();)
			{
				EReference ref = it.next();
				EClass target = (EClass)ref.getEType();
				if (!target.getEPackage().getName().equals("datatype")) result = target;
			}
		}
		return result;
	}
	
	// for generating namespace prefixes for packages in the simle class model
	private int prefixIndex = 1;
	
	/**
	 * 
	 * @param packageName
	 * @return
	 */
	private EPackage findOrMakePackage(String packageName)
	{
		EPackage namedPackage = null;
		for (Iterator<EPackage> it = topPackage.getESubpackages().iterator();it.hasNext();)
		{
			EPackage next = it.next();
			if (next.getName().equals(packageName)) namedPackage = next;
		}
		
		if (namedPackage == null)
		{
			namedPackage = EcoreFactory.eINSTANCE.createEPackage();
			namedPackage.setName(packageName);
			namedPackage.setNsPrefix("cd" + prefixIndex);
			namedPackage.setNsURI("com.openMap1." + packageName);
			String templateId = TemplatePackageIds.get(packageName);
			if (templateId != null) addDocumentation(namedPackage, "Package for CDA Template " + templateId);
			prefixIndex++;
			topPackage.getESubpackages().add(namedPackage);
		}
		return namedPackage;
	}

	
	/**
	 * If an EClass does not exist, make it and wrap it up in a LabelledEClass, with subset ""
	 * If it does exist, wrap it up in a LabelledEClass with a new unique subset
	 * Link the labelledEClass to a parent if the parent is not null
	 * @param className
	 * @param assocName
	 * @param parent
	 * @return
	 * @throws MapperException
	 */
	private LabelledEClass findOrMakeClass(String packageName,String className,String assocName,LabelledEClass parent) throws MapperException
	{
		// special to ensure that class 'Text' is always in the datatypes package
		if (className.equals("Text")) packageName = "datatypes";
		
		EPackage thePackage = findOrMakePackage(packageName);
		String qualifiedClassName = packageName + "." + className;
		EClass aClass = null;
		LabelledEClass result = null;
		
		
		// (a) EClass is already a child of the same parent LabelledEClass; nothing new to make
		if (parent != null)	result = parent.getNamedAssocAndClassChild(assocName, className);
		if (result != null) return result;

		// (b) EClass has been made as a child of a different parent
		aClass = simpleClasses.get(qualifiedClassName);

		// (c) EClass does not exist yet; make it
		if (aClass == null)
		{
			aClass = EcoreFactory.eINSTANCE.createEClass();
			aClass.setName(className);
			thePackage.getEClassifiers().add(aClass);
			simpleClasses.put(qualifiedClassName,aClass);
		}

		if (assocName == null) result = new LabelledEClass(aClass);
		else result = new LabelledEClass(aClass,assocName,parent);

		return result;
	}
	
	
	/**
	 * 
	 * @throws MapperException
	 */
	private void makeInitialModelAndMappings() throws MapperException
	{
		//  make an empty simplified Ecore model
		topPackage = EcoreFactory.eINSTANCE.createEPackage();
		String topPackageName="ccda"; // non-UK CDAs are expected to be consolidated CDA
		if (isNHSMIF()) topPackageName = "cda";
		topPackage.setName(topPackageName);
		// top package needs a URI and a prefix for EMF code generation
		topPackage.setNsPrefix("cd");
		topPackage.setNsURI("com.OpenMap1.cda1"); // can the uri be the same as the name?

		// ensure this class model will be viewed as an RMIM in the class model view
		ModelUtil.addMIFAnnotation(topPackage, "RMIM", "true");	
		
		// if a message name has been defined, add it as an annotation on the top package
		if (messageName() != null) ModelUtil.addMIFAnnotation(topPackage, "messageName", messageName());	

		// this model will not be used by a CDA Wrapper class. Point to the model which will.
		String sourceModelFileName = "";
		String sourceModelURL = mappedStructure.getUMLModelURL();
		StringTokenizer st = new StringTokenizer(sourceModelURL,"/");
		while (st.hasMoreTokens()) sourceModelFileName = st.nextToken();
		ModelUtil.addMIFAnnotation(topPackage, "CDAWrapperModel", sourceModelFileName);	

		// save the empty Ecore model
		String sourceModelLocation = ecoreRoot.eResource().getURI().toString();
		System.out.println("Source model location: " + sourceModelLocation);
		String simpleModelLocation = addSuffixToFileName(sourceModelLocation,"simple");
		ModelUtil.savePackage(simpleModelLocation, topPackage);
		
		// make and save an empty mapping set for the simple message structure
		String simpleMappingLocation = addSuffixToFileName(mappingSetURIString,"simple");
		simpleMappings = ModelUtil.saveNewMappingSet(simpleMappingLocation);
		simpleMappings.setUMLModelURL(simpleModelLocation);
		simpleMappings.setClassModelRoot(topPackage);
		// conditionally, the mapping set is in the V3 namespace with no prefix
		if (CDAConverter.SIMPLE_MESSAGE_IN_V3_NAMESPACE)
			addNamespace("",CDAConverter.V3NAMESPACEURI,simpleMappings);
		
		// make and save an empty mapping set for the full message structure
		String fullMappingLocation = addSuffixToFileName(mappingSetURIString,"full");
		fullMappings = ModelUtil.saveNewMappingSet(fullMappingLocation);
		fullMappings.setUMLModelURL(simpleModelLocation);
		fullMappings.setClassModelRoot(topPackage);
		
		/* the full mappings may have the same wrapper class as the open mapping set, 
		 * but if that has no wrapper class, the wrapper class is taken from an annotations on
		 * the entry class of the full class model*/
		fullMappingsWrapperClassName = mappedStructure.getMappingParameters().getWrapperClass();
		if (fullMappingsWrapperClassName.equals(""))
		{
			EClass topClass = classModelView.topLabelledEClass().eClass();
			fullMappingsWrapperClassName = ModelUtil.getMIFAnnotation(topClass, "wrapperClass");
		}
		
		/* For NHS RMIMs, there are two possible wrapper classes (CDA wired, and plain V3).
		 * The CDA wired wrapper class will be applied later if the user chooses to read a CDA mapping set. */
		if (isNHSMIF()) 
		{
			fullMappingsWrapperClassName = NHSV3WrapperClass;

			// add the NHS localisation namespace to the full mapping set
			addNamespace(NHS_CDA_Wrapper.NHSPREFIX,NHS_CDA_Wrapper.NHSURI,fullMappings);
		}
		
		// if the user has added any namespaces to the open mapping set, transfer them to the full mapping set
		for (Iterator<Namespace> it = mappedStructure.getMappingParameters().getNameSpaces().iterator();it.hasNext();)
		{
			Namespace nSpace = it.next();
			addNamespace(nSpace.getPrefix(),nSpace.getURL(),fullMappings);
		}
		
		// make the full mappings use the V3 namespace with no prefix
		addNamespace("",CDAConverter.V3NAMESPACEURI,fullMappings);
		addNamespace("xsi",XMLUtil.SCHEMAINSTANCEURI,fullMappings);
		
		/* attach the correct wrapper class to the full mappings; 
		 * and a wrapper to the simple mappings to pass through html subtrees unchanged*/
		if (fullMappingsWrapperClassName != null)
		{
			fullMappings.getMappingParameters().setWrapperClass(fullMappingsWrapperClassName);
			simpleMappings.getMappingParameters().setWrapperClass(simpleMappingsWrapperClassName);			
		}
		
		
	}
	
	/**
	 * add a namespace declaration to a mapping set. 
	 * It is OK to have the same namespace URI with two prefixes, but not to repeat any prefix.
	 * @param prefix
	 * @param uri
	 * @param mappings
	 */
	private void addNamespace(String prefix, String uri, MappedStructure mappings)
	{
		boolean hasPrefixAlready = false;
		for (Iterator<Namespace> it = mappings.getMappingParameters().getNameSpaces().iterator();it.hasNext();)
			if (it.next().getPrefix().equals(prefix)) hasPrefixAlready = true;
		if (!hasPrefixAlready)
		{
			Namespace ns = MapperFactory.eINSTANCE.createNamespace();
			ns.setPrefix(prefix);
			ns.setURL(uri); 
			mappings.getMappingParameters().getNameSpaces().add(ns);					
		}
	}
	
	private String addSuffixToFileName(String fullLocation,String suffix) throws MapperException
	{
		String result = null;
		String[] exts = {".mapper",".ecore"};
		for (int i = 0; i < exts.length; i++)
		{
			if (fullLocation.endsWith(exts[i]))
			{
				int len = fullLocation.length() - exts[i].length();
				result = fullLocation.substring(0,len) + "_" + suffix + exts[i];
			}			
		}
		if (result == null) throw new MapperException("Invalid file extension in location '" + fullLocation + "'");
		return result;
	}
	
	private String changeExtension(String fullLocation, String newExtension) throws MapperException
	{
		StringTokenizer st = new StringTokenizer(fullLocation,".");
		if (st.countTokens() != 2) throw new MapperException("Unexpected file path: " + fullLocation);
		return (st.nextToken() + "." + newExtension);
	}
	
	/**
	 * constrain the mapped structure under topSimpleNode so that 
	 * its tree structure is a subset of the tree structure in masterStructure,
	 * ignoring different namespaces and allowing different names on the top node
	 * @param topSimpleNode
	 * @param masterStructure
	 */
	private ElementDef constrainSimpleStructure(ElementDef topSimpleNode,MappedStructure masterStructure) throws MapperException
	{
		ElementDef topMasterNode = masterStructure.getRootElement();
		StructureDefinition master = masterStructure.getStructureDefinition();
		return constrainSimpleSubtree(topSimpleNode,topMasterNode,master);
	}

	/**
	 * recursive descent, removing nodes from the simple structure which do not match nodes in the master structure
	 * @param simpleNode
	 * @param masterNode
	 * @param master
	 * @return
	 * @throws MapperException
	 */
	private ElementDef constrainSimpleSubtree(ElementDef simpleNode,ElementDef masterNode,StructureDefinition master) throws MapperException
	{
		ElementDef masterEl = masterNode;
		if (!masterNode.isExpanded()) masterEl = master.typeStructure(masterNode.getType());
		if (masterEl == null) throw new MapperException("Cannot expand constraining mapped structure at node " + masterNode.getName());

		// filter the list of simple child elements to be a subset of those in the master structure
		EList<ElementDef> newChildElements = new BasicEList<ElementDef>();
		for (Iterator<ElementDef> it = simpleNode.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef simpleChild = it.next();
			ElementDef masterChild = getMasterChild(masterEl,simpleChild.getName());
			if (masterChild != null) newChildElements.add(constrainSimpleSubtree(simpleChild,masterChild, master));
			else System.out.println("Removed simple message node at " + simpleChild.getPath());
		}

		simpleNode.eSet(MapperPackage.Literals.ELEMENT_DEF__CHILD_ELEMENTS, newChildElements);
		return simpleNode;
	}

	/**
	 * 
	 * @param masterEl
	 * @param name
	 * @return a child element of element masterEl, which has the name 'name', apart from namespace prefixes;
	 * or null if there is no such element
	 */
	private ElementDef getMasterChild(ElementDef masterEl, String name)
	{
		ElementDef result = null;
		for (Iterator<ElementDef> it = masterEl.getChildElements().iterator(); it.hasNext();)
		{
			String localName = "";
			ElementDef mc = it.next();
			// remove any namespace prefix from the node name
			StringTokenizer st = new StringTokenizer(mc.getName(),":");
			while (st.hasMoreTokens()) localName = st.nextToken();
			if (name.equals(localName)) result = mc;
		}
		return result;
	}

	//-------------------------------------------------------------------------------------
	//            Final changes to full mapping set for NHS CDAs - to annotate nodes
	//            with CDA node names (for use in the wrapper transform) and reorder nodes,
	//            and to mark attributes on the simple and full mapping sets with data types
	//-------------------------------------------------------------------------------------
	
	//-------------------------------------------------------------------------------------
	//      Marking attributes of simple and full mapping sets with data types
	//-------------------------------------------------------------------------------------

	/**
	 * 
	 */
	private void markMappingSetsWithTypes() throws MapperException
	{
		String prompt = "Open specific mapping set for data types of attributes";
		String annotationKey = "specificCDAWithDataTypes";
		MappedStructure CDAStructure = getCDAWireMappingSet(prompt,annotationKey);

		// user may not choose a CDA mapping set; in that case do nothing to the full mapping set
		if (CDAStructure != null) 
		{
			ElementDef CDARoot = CDAStructure.getRootElement();
			ElementDef fullRoot = fullMappings.getRootElement();
			
			findDataTypes(CDAStructure,CDARoot,fullRoot);			
		}
	}
	
	/**
	 * 
	 * @param CDAStructure
	 * @param CDAElDef
	 * @param fullElDef
	 * @throws MapperException
	 */
	private void findDataTypes(MappedStructure CDAStructure,ElementDef CDAElDef, ElementDef fullElDef)
	 throws MapperException
	{
		ElementDef cdaEl = CDAElDef;
		if (!CDAElDef.isExpanded()) cdaEl = CDAStructure.getStructureDefinition().typeStructure(CDAElDef.getType());
		if (cdaEl == null) throw new MapperException("Cannot find structure for node " 
				+ CDAElDef.getName() + " of type " + CDAElDef.getType());
		cdaEl.setName(CDAElDef.getName());
		
		// find types of all the attributes, and mark them
		for (Iterator<AttributeDef> it = fullElDef.getAttributeDefs().iterator();it.hasNext();)
		{
			AttributeDef fullAtt = it.next();
			AttributeDef cdaAtt = cdaEl.getNamedAttribute(fullAtt.getName()) ;
			if (cdaAtt == null)
			{
				trace("Cannot find attribute " + fullAtt.getName() + " at path " + fullElDef.getPath());
			}
			else
			{
				String type = cdaAtt.getType();
				if ((type != null) && (!type.equals("")))
				{
					AttributeDef simpleAtt = (AttributeDef)matchedNodes.get(fullAtt);
					fullAtt.setType(type);
					if (simpleAtt != null) simpleAtt.setType(type);
				}
			}
		}
		
		// recursive descent of the full mapping set tree
		for (Iterator<ElementDef> it = fullElDef.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef fullChild = it.next();
			String childName = fullChild.getName();
			// if the child node does not have a CDA node name, find the right one for it
			ElementDef cdaChild = cdaEl.getNamedChildElement(childName);
			
			if (cdaChild == null)
			{
				// try the cda name of the full mapping set node
				String description = fullChild.getDescription();
				if ((description != null) && (description.startsWith(CDA_NAME)))
				{
					String cdaName = description.substring(CDA_NAME.length());
					cdaChild = cdaEl.getNamedChildElement(cdaName);					
				}
				if (cdaChild == null) trace("Cannot find child element " + childName + " at path " + fullElDef.getPath());				
			}			

			if (cdaChild != null) findDataTypes(CDAStructure, cdaChild,fullChild);
		}
	}

	
	//-------------------------------------------------------------------------------------
	//       Adding CDA wire-form paths and data types to the mapping csv file
	//-------------------------------------------------------------------------------------
	
	
	/**
	 * tidy up rows of a mappings csv file by removing the top element name from the paths
	 * @param mappingRows
	 * @return
	 * @throws MapperException
	 */
	private Vector<String[]> finalMappingRows(Vector<String[]> mappingRows)  throws MapperException
	{
		int[] removeChars = {0,0,0};
		// find the length of the top element name + 1 for '/', if there are any rows
		if (mappingRows.size() > 1) 
		{
			for (int col = 0; col < 3; col++)
			{
				StringTokenizer st = new StringTokenizer(mappingRows.get(1)[col],"/");
				if (st.hasMoreTokens()) removeChars[col] = st.nextToken().length() + 1;							
			}
		}
		Vector<String[]> finalRows = new Vector<String[]>();
		// do nothing to the header row
		finalRows.add(mappingRows.get(0));

		for (int i = 1; i < mappingRows.size(); i++)
		{
			String[] csvRow = mappingRows.get(i);
			String fullPath = csvRow[1];
			// remove the initial "/ClinicalDocument" or other top element name from the first 3 columns
			csvRow[0] = csvRow[0].substring(removeChars[0]);
			csvRow[1] = csvRow[1].substring(removeChars[1]);
			csvRow[2] = cdaWireXPath(fullPath).substring(removeChars[2]);
			csvRow[4] = dataTypeName(fullPath);
			finalRows.add(csvRow);
		}
		return finalRows;
	}
	
	/**
	 * return the cda wire form path corresponding to a path in the full mapping set
	 */
	private String cdaWireXPath(String fullMappingSetXPath) throws MapperException
	{
		String cdaPath = "";
		ElementDef fullEl = fullMappings.getRootElement();
		StringTokenizer st = new StringTokenizer(fullMappingSetXPath,"/");
		boolean top = true;
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			String cdaStep = step;
			if (!step.startsWith("@"))
			{
				if (!top) fullEl = fullEl.getNamedChildElement(step);
				if (fullEl == null) throw new MapperException("Cannot find child element " + step + " in full mapping set");
				String desc = fullEl.getDescription();
				if (desc.startsWith(CDA_NAME)) cdaStep = desc.substring(CDA_NAME.length());				
			}
			cdaPath = cdaPath + "/" + cdaStep;
			top = false;
		}
		return cdaPath;
	}
	
	private String dataTypeName(String fullMappingSetXPath) throws MapperException
	{
		String typeName = "";
		ElementDef fullEl = fullMappings.getRootElement();
		StringTokenizer st = new StringTokenizer(fullMappingSetXPath,"/");
		boolean top = true;
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			if (step.startsWith("@"))
			{
				AttributeDef attDef = fullEl.getNamedAttribute(step.substring(1));
				if (attDef == null) throw new MapperException("Cannot find attribute " + step.substring(1) + " in full mapping set");
				typeName = attDef.getType();
			}
			else
			{
				if (!top) fullEl = fullEl.getNamedChildElement(step);
				if (fullEl == null) throw new MapperException("Cannot find child element " + step + " in full mapping set");
			}
			top = false;
		}
		return typeName;
		
	}
	
	
	//-------------------------------------------------------------------------------------
	//       Ordering the full mapping set, and marking it with CDA tag names
	//-------------------------------------------------------------------------------------
	
	/**
	 * If the ecore model has a note of a cda wire form mapping set to use
	 * to order the full mapping set, open it.
	 * If not, ask the user for such a mapping set, and if provided , note
	 * it in the ecore model.
	 */
	private MappedStructure getCDAWireMappingSet(String prompt, String annotationKey) throws MapperException
	{
		boolean newMappingSetChosen = false;
		 MappedStructure CDAStructure = null;
		 String wireMappingSetURL = ModelUtil.getMIFAnnotation(ecoreRoot, annotationKey);
		 
		 // if the ecore model has recorded a wire-form mapping set, use it
		 if (wireMappingSetURL != null) try
		 {
			 String absLocation = FileUtil.absoluteLocation(wireMappingSetURL);
			 CDAStructure = FileUtil.getMappedStructure(absLocation);
		 }
		 // if anything goes wrong, allow the user to nominate a mapping set
		 catch (Exception ex) {trace(ex.getMessage());wireMappingSetURL = null;}

		 // if the ecore model has not recorded a good wire-form mapping set, ask the user for one
		 if (wireMappingSetURL == null)
		 {
				CDAStructure = askUserForMappedStructure(mapperEditor, prompt);	
				newMappingSetChosen = true;
		 }
		 
		 /* if the ecore model has not recorded a wire-form mapping set, but the user has provided one,
		  * record the one the user provided in the ecore model, and save it */
		 if ((newMappingSetChosen) && (CDAStructure != null))
		 {
			 String absPath = CDAStructure.eResource().getURI().toString();
			 wireMappingSetURL = FileUtil.resourceLocation(absPath);
			 ModelUtil.addMIFAnnotation(ecoreRoot, annotationKey, wireMappingSetURL);
			 saveEcoreModel();
		 }
		
		return CDAStructure;
	}
	
	/**
	 * 
	 */
	private void orderFullMappingSet() throws MapperException
	{
		String prompt = "Open generic CDA Mapping set for wire form tag names";
		String annotationKey = "wireFormCDA";
		MappedStructure CDAStructure = getCDAWireMappingSet(prompt,annotationKey);

		// user may not choose a CDA mapping set; in that case do nothing to the full mapping set
		if (CDAStructure != null)
		{
			
			ElementDef CDARoot = CDAStructure.getRootElement();
			ElementDef fullRoot = fullMappings.getRootElement();
			
			// the csv file for CDA tag names is in the same folder as the CDA mapping set. Remove 'file:/' from the front.
			String CDALocation = FileUtil.removeFilePrefix(CDAStructure.eResource().getURI().toString());
			String CDATagFileLocation = FileUtil.getFolder(CDALocation) + CDATagFileName;
			Vector<String> CDATagFileLines = FileUtil.textLines(CDATagFileLocation);
			trace("CDA tag name ruleset has : " + CDATagFileLines.size() + " rows.");
			Vector<String[]> CDATagRules = parseCDARules(CDATagFileLines);

			renameAndOrderSubtree(CDAStructure,CDARoot,fullRoot,CDATagRules);
			
			// reset the wrapper class to be the wrapper class for wire-form CDA
			fullMappings.getMappingParameters().setWrapperClass(NHSCDAWireWrapperClass);
		}		
	}
	
	/**
	 * recursive descent of the full mapping set tree:
	 *  - in the description field, where the cda wire form tag name is different , give the cda tag name
	 *  - order child nodes in the correct cda order
	 * @param CDAStructure
	 * @param CDAElDef
	 * @param fullElDef
	 * @throws MapperException
	 */
	private void renameAndOrderSubtree(MappedStructure CDAStructure,ElementDef CDAElDef,
			ElementDef fullElDef,Vector<String[]> CDATagRules)
	 throws MapperException
	{
		// trace("renaming " + fullElDef.getPath());
		ElementDef cdaEl = CDAElDef;
		if (!CDAElDef.isExpanded()) cdaEl = CDAStructure.getStructureDefinition().typeStructure(CDAElDef.getType());
		if (cdaEl == null) throw new MapperException("Cannot find structure for node " 
				+ CDAElDef.getName() + " of type " + CDAElDef.getType());
		cdaEl.setName(CDAElDef.getName());
		String parentPath = fullElDef.getPath();
		
		// collect available CDA tag names in case the rules give no candidates
		String availableCDANames = "";
		for (Iterator<ElementDef> it = cdaEl.getChildElements().iterator();it.hasNext();)
		{
			ElementDef candidateChild = it.next();
			String CDAName = candidateChild.getName();
			availableCDANames = availableCDANames + CDAName + ", ";
		}

		
		// nodes not recognised in CDA, but which the user chooses to retain
		Vector<ElementDef> userRetainedNodes = new Vector<ElementDef>();
		
		/* go over child nodes in full mapping set, finding CDA node names where necessary, 
		 * and recursing when you find a unique CDA tag name. */
		for (Iterator<ElementDef> it = fullElDef.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef fullChild = it.next();
			String childName = fullChild.getName();
						
			// if the child node does not have a CDA node name, find the right name for it
			ElementDef cdaChild = cdaEl.getNamedChildElement(childName);
			if (cdaChild == null)
			{
				Vector<String[]> fixedValues = getObjectMappingValueConditions(fullChild);
				Hashtable<String,String> cdaNames = NHS_CDA_TagRuleInterpreter.getCDATagNames(childName, parentPath, fixedValues,CDATagRules);
				if (cdaNames.size() == 0) 
				{
					String message = "The CDA naming ruleset suggested no candidate CDA tag names for node '" 
						+ childName + "' as a child of CDA node '" + cdaEl.getName()
						+ "'. Available CDA child nodes are: " + availableCDANames + "; Retain node in any case?";
					boolean retain = WorkBenchUtil.askConfirm("Warning", message);
					if (retain) userRetainedNodes.add(fullChild);
					System.out.println(message);
				}
				else cdaChild = uniqueCDAChild(cdaEl,cdaNames,fullChild,userRetainedNodes);
				if (cdaChild != null) 
				{
					fullChild.setDescription(CDA_NAME + cdaChild.getName());
					//trace("Gave CDA name '" + cdaChild.getName() + "' to node '" + fullChild.getName() + "'");
				}
			}
			// only recurse to child subtree if a unique CDA node has been found
			if (cdaChild != null) renameAndOrderSubtree(CDAStructure,cdaChild,fullChild,CDATagRules);
		}
		
		// order child nodes to match the normative CDA order
		EList<ElementDef> orderedChildEls = new BasicEList<ElementDef>();
		// first include nodes which were not matched but which the user chose to retain		
		for (Iterator<ElementDef> ix = userRetainedNodes.iterator(); ix.hasNext();) orderedChildEls.add(ix.next());
		// then CDA ordered nodes
		for (Iterator<ElementDef> iu = cdaEl.getChildElements().iterator(); iu.hasNext();)
		{
			ElementDef cdaChild = iu.next();
			String cdaName = cdaChild.getName();
			// there may be several elements in the full message with the same CDA name, e.g. 'entry'; add them all and count them
			int matchingElementsFound = 0;
			for (Iterator<ElementDef> iv = fullElDef.getChildElements().iterator(); iv.hasNext();)
			{
				ElementDef fullChild = iv.next();
				String desc = fullChild.getDescription();
				int len = CDA_NAME.length();
				if ((fullChild.getName().equals(cdaName)) |
				         ((desc != null) && (desc.length() > len) 
				        		 && (desc.startsWith(CDA_NAME)) 
				        		 && (desc.substring(len).equals(cdaName))))
				{
					 orderedChildEls.add(fullChild);
					 matchingElementsFound++;
				}
			}
			if (matchingElementsFound > 1) System.out.println("Found " + matchingElementsFound + " elements with CDA name '" + cdaName + "'");
		}
		// reset the list of child elements to the new order
		fullElDef.eSet(MapperPackage.eINSTANCE.getElementDef_ChildElements(), orderedChildEls);
	}
	
	/**
	 * 
	 * @param cdaEl a CDA element 
	 * @param cdaNames a list of possible CDA names, which may have repeats
	 * @return
	 * @throws MapperException
	 */
	private ElementDef uniqueCDAChild(ElementDef cdaEl, Hashtable<String,String> cdaNames, 
			ElementDef fullChild, Vector<ElementDef> userRetainedNodes) throws MapperException
	{
		ElementDef cdaChild = null;
		int found = 0;
		String foundNames = "";
		// try out all CDA child node names against all names suggested by the rules
		String CDAChildNames = "";
		for (Iterator<ElementDef> it = cdaEl.getChildElements().iterator();it.hasNext();)
		{
			ElementDef candidateChild = it.next();
			String CDAName= candidateChild.getName();
			CDAChildNames = CDAChildNames + CDAName + " ";
			// try to match this CDA child name against any of the candidates from the rules
			if (cdaNames.get(CDAName) != null)
			{
				found++;
				cdaChild = candidateChild;
				foundNames = foundNames + CDAName + ", ";
			}
		}
		
		// write a warning and return null if there is not a unique matching CDA name
		if (found != 1)
		{
			String ruleNumbers = "";
			for (Enumeration<String> en = cdaNames.elements(); en.hasMoreElements();) ruleNumbers = ruleNumbers + en.nextElement() + " ";
			
			String message = ("There are " + found + " matches between the child nodes of CDA node '" 
			+ cdaEl.getName() + "' and the " + cdaNames.size() 
			+ " names suggested for node '" + fullChild.getName() + "' by rules " + ruleNumbers + ".");
			if (found > 1) message = message +  " These matches are: " + foundNames + ".";
			message = message + " Candidates from rules are: " + GenUtil.singleKeyString(cdaNames) + ".";
			message = message + " CDA child node names are "  + CDAChildNames + ".  Retain node in any case?";

			boolean retain = WorkBenchUtil.askConfirm("Warning", message );
			if (retain) userRetainedNodes.add(fullChild);
			System.out.println(message);
			cdaChild = null;
		}
			
		return cdaChild;
	}
	
	/**
	 * 
	 * @param fullChild a node in the mapping set
	 * @return details of all mapping conditions on object mappings on the node
	 */
	private Vector<String[]> getObjectMappingValueConditions(ElementDef fullChild)
	{
		Vector<String[]> conditions = new Vector<String[]>();
		
		/* pick up conditions from object mappings on this node and all ancestor nodes, 
		 * for ancestors taking off the path to this node */
		String takeFromPaths = "";			
		EObject ancestor = fullChild;
		while (ancestor instanceof ElementDef)
		{
			ElementDef elAncestor = (ElementDef)ancestor;
			addToFixedValueConditions(elAncestor,conditions,takeFromPaths ,"");			
			takeFromPaths = elAncestor.getName() + "/" + takeFromPaths;
			ancestor = ancestor.eContainer();
		}
				
		return conditions;
	}
	
	/**
	 * add to the Vector of fixed value conditions, from the object mappings on this node.
	 * if addToPaths is non-empty, add it on the front of every path
	 * if takeFromPaths is non-empty, only include paths from which it can be taken away.
	 * @param elDef
	 * @param conditions
	 * @param takeFromPaths
	 * @param addToPaths
	 */
	private void addToFixedValueConditions(ElementDef elDef, Vector<String[]> conditions, String takeFromPaths, String addToPaths)
	{
		NodeMappingSet nms = elDef.getNodeMappingSet();
		if (nms != null) for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator(); it.hasNext();)
		{
			ObjMapping om = it.next();
			for (Iterator<MappingCondition> iu = om.getMappingConditions().iterator();iu.hasNext();)
			{
				MappingCondition mc = iu.next();
				if (mc instanceof ValueCondition)
				{
					ValueCondition vc = (ValueCondition)mc;
					/* always add to the front of paths, even if addToPaths is empty 
					 * addToPaths and takeFromPaths are never both non-empty. */
					String path = addToPaths + vc.getLeftPath();

					String[] cond = new String[2];
					cond[0] = path;
					cond[1] = vc.getRightValue();
					if (takeFromPaths.equals("")) conditions.add(cond);
					// if takeFromPaths is non-empty, the path must start with it
					else if (path.startsWith(takeFromPaths))
					{
						cond[0] = path.substring(takeFromPaths.length());
						conditions.add(cond);
					}
				}
			}
		}		
	}

	
	public static Vector<String[]> parseCDARules(Vector<String> CDATagFileLines) throws MapperException
	{
		Vector<String[]> rules = new Vector<String[]>();

		// halt if column headers have changed
		String expectedFirstLine = NHS_CDA_TagRuleInterpreter.TAG_RULE_HEADER_ROW;
		if (!expectedFirstLine.equals(CDATagFileLines.get(0)))
			throw new MapperException("Column headers in CDA Tag Name Rule file have changed, from '" 
					+ expectedFirstLine + "' to '" + CDATagFileLines.get(0) + "'");
		StringTokenizer header = new StringTokenizer(CDATagFileLines.get(0),",");
		int columns = header.countTokens();

		// first line is column headers and is not used other than for checking
		for (int i = 1; i < CDATagFileLines.size(); i++)
		{
			String[] ruleLine = FileUtil.oldParseCSVLine(columns, CDATagFileLines.get(i));
			rules.add(ruleLine);
		}
		return rules;
	}
	
	//------------------------------------------------------------------------------------------------------------
	//      make and save a thin mapping set (full mapping set without all its fixed value conditions)
	//------------------------------------------------------------------------------------------------------------

	
	
	
	private void makeThinMappingSet()
	{
		// make the thin mapping set as a copy of the full mapping set (which has already been saved)
		MappedStructure thinMappings = fullMappings;
		URI fullURI = thinMappings.eResource().getURI();
		thinMappings.eResource().setURI(makeThinURI(fullURI));
		
		// recursively thin out the thin mapping set
		thinOutMappings(thinMappings.getRootElement());
			
		// save the thin mapping set
		FileUtil.saveResource(thinMappings.eResource());		
	}
	
	
	/**
	 * thin out the value conditions on all object mappings, retaining only value conditions to 
	 * extension attributes
	 * @param node
	 */
	private void thinOutMappings(ElementDef node)
	{
		NodeMappingSet nms = node.getNodeMappingSet();
		if (nms != null) for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator();it.hasNext();)
		{
			ObjMapping om = it.next();
			// build up a new list of mapping conditions, containing only value conditions to extension attributes
			EList<MappingCondition> newConditions = new BasicEList<MappingCondition>();
			for (Iterator<MappingCondition> iu = om.getMappingConditions().iterator(); iu.hasNext();)
			{
				MappingCondition mc = iu.next();
				if (mc instanceof ValueCondition)
				{
					ValueCondition vc = (ValueCondition)mc;
					if (vc.getLeftPath().endsWith("extension")) newConditions.add(vc);
				}
				else newConditions.add(mc);
			}
			// reset the list of mapping conditions
			om.eSet(MapperPackage.eINSTANCE.getMapping_MappingConditions(), newConditions);
		}
		
		// recursion to child nodes
		for (Iterator<ElementDef> it = node.getChildElements().iterator(); it.hasNext();)
			thinOutMappings(it.next());
			
	}
	
	private URI makeThinURI(URI fullURI)
	{
		String fullString = fullURI.toString();
		String fullEnd = "_full.mapper";
		String thinString = fullString.substring(0,fullString.length() - fullEnd.length()) + "_thin.mapper";
		return URI.createURI(thinString);		
	}
	

	//-------------------------------------------------------------------------------------
	//                         JavaDoc annotations for EMF-generated code
	//-------------------------------------------------------------------------------------
	

	private String javaDocURI = "http://www.eclipse.org/emf/2002/GenModel";
	private String  javaDocKey = "documentation";
	
	/**
	 * add annotations to make javadoc for EMF generated java classes
	 * @param fullMappings
	 * @param topPackage
	 */
	private void addJavaDocAnnotations(MappedStructure fullMappings,EPackage topPackage)  throws MapperException
	{
		ElementDef rootNode = fullMappings.getRootElement();
		String path = null;
		String templateId = null;
		makeJavaDocAnnotations(rootNode,topPackage,path,templateId);
	}
	
	/**
	 * recursive descent of the full mapping set, making JavaDoc annotations on the simple class model
	 * @param node
	 * @param topPackage
	 * @param path CDA path to the node
	 * @param templateId latest template id encountered along the path
	 */
	private void makeJavaDocAnnotations(ElementDef node,EPackage topPackage,String path, String templateId) throws MapperException
	{
		// calculate next path
		String nodeName = wireTagName(node);
		String newPath = nodeName;
		if (path != null) newPath = path + "/" + nodeName;
		
		String newTemplateId = templateId;
		
		// find any object mapping on this node, and annotate the class in the Ecore model
		NodeMappingSet nms = node.getNodeMappingSet();
		if (nms != null)
		{
			// annotate the class represented on this node
			for (Iterator<ObjMapping> it = nms.getObjectMappings().iterator(); it.hasNext();)
			{
				ObjMapping om = it.next();
				if (getTemplateId(om) != null) newTemplateId = getTemplateId(om);

				EClass mappedClass = getMappedSimpleClass(topPackage, om.getMappedPackage(),om.getMappedClass());
				addDocumentation(mappedClass,mappingDocString(node,om, newPath, newTemplateId));
			}
			
			// annotate the association represented on this node
			for (Iterator<AssocMapping> it = nms.getAssociationMappings().iterator(); it.hasNext();)
			{
				AssocMapping am = it.next();
				AssocEndMapping aem1 = am.getMappedEnd1();
				AssocEndMapping aem2 = am.getMappedEnd2();

				EClass parentClass = getMappedSimpleClass(topPackage, aem1.getMappedPackage(),aem1.getMappedClass());
				EStructuralFeature ref = parentClass.getEStructuralFeature(aem2.getMappedRole());
				if (ref == null) throw new MapperException("cannot find association " + aem2.getMappedRole() + " of class " + aem1.getMappedClass());
				String docString = "<p>Represented at CDA node: <b>" + newPath + "</b></p>";
				addDocumentation(ref,docString);
			}
			
			// document any property mappings to the pseudo-attribute 'textContent' on this element
			documentPropertyMappings(nms, newPath,topPackage,newTemplateId);
		}
		
		// document property mappings on attribute nodes
		for (Iterator<AttributeDef> it = node.getAttributeDefs().iterator();it.hasNext();) documentAttribute(it.next(),newPath,topPackage,newTemplateId);
		
		// recursion to child nodes
		for (Iterator<ElementDef> it = node.getChildElements().iterator();it.hasNext();) makeJavaDocAnnotations(it.next(),topPackage, newPath, newTemplateId);
	}
	
	/**
	 * 
	 * @param topPackage
	 * @param packageName
	 * @param className
	 * @return
	 * @throws MapperException
	 */
	private EClass getMappedSimpleClass(EPackage topPackage, String packageName,String className) throws MapperException
	{
		EPackage thePackage = null;
		for (Iterator<EPackage> it = topPackage.getESubpackages().iterator();it.hasNext();)
		{
			EPackage next = it.next();
			if (next.getName().equals(packageName)) thePackage = next;
		}
		if (thePackage == null) throw new MapperException("Cannot find package " + packageName);
		EClass result = (EClass)thePackage.getEClassifier(className);
		if (result == null) throw new MapperException("Cannot find class " + className + " in package " + packageName);
		return result;
	}
	
	/**
	 * document any property mappings on Attribute nodes
	 * @param attDef
	 * @param path
	 * @param topPackage
	 * @throws MapperException
	 */
	private void documentAttribute(AttributeDef attDef, String path, EPackage topPackage, String templateId) throws MapperException
	{
		String attPath = path + "/@" + attDef.getName();
		NodeMappingSet nms = attDef.getNodeMappingSet();
		if (nms != null) documentPropertyMappings(nms, attPath, topPackage, templateId);
	}
	
	/**
	 * document property mappings on ElementDef or AttributeDef nodes
	 * @param nms
	 * @param path
	 * @param topPackage
	 * @throws MapperException
	 */
	private void documentPropertyMappings(NodeMappingSet nms, String path, EPackage topPackage, String templateId)  throws MapperException
	{
		for (Iterator<PropMapping> it = nms.getPropertyMappings().iterator(); it.hasNext();)
		{
			PropMapping pm = it.next();
			EClass mappedClass = getMappedSimpleClass(topPackage, pm.getMappedPackage(), pm.getMappedClass());
			EStructuralFeature mappedFeat = mappedClass.getEStructuralFeature(pm.getMappedProperty());
			if (mappedFeat == null)  throw new MapperException("Cannot find mapped property " + pm.getMappedClass() + ":" + pm.getMappedProperty());
			if (mappedFeat instanceof EAttribute)
			{
				EAttribute mappedProp = (EAttribute)mappedFeat;
				
				String lastLink = "";
				StringTokenizer st = new StringTokenizer(path,"/");
				while (st.hasMoreTokens()) 
				{
					String link = st.nextToken();
					if (!link.startsWith("@")) lastLink = link;
				}

				String docString = "";
				if (mappedProp.getName().equals(POSITION_PROPERTY_NAME))
					docString = "<p>The ordinal position 1..N of the '" + lastLink 
						+ "' node beneath its parent node</p>";

					else docString = "<p>Represented at CDA node: <b>" + path + "</b></p>";
				
				addDocumentation(mappedProp,docString);				
			}
			else
			{
				WorkBenchUtil.showMessage("Javadoc error", "Property "
						+ pm.getMappedProperty() + " of class " +  pm.getMappedClass() + " is also an association.");
			}
		}		
	}
	
	/**
	 * 
	 * @param om
	 * @return any template id on this object mapping
	 */
	private String getTemplateId(ObjMapping om)
	{
		String templateId = null;
		for (Iterator<MappingCondition> it = om.getMappingConditions().iterator();it.hasNext();)
		{
			MappingCondition mc = it.next();
			if (mc instanceof ValueCondition)
			{
				ValueCondition vc = (ValueCondition)mc;
				String vPath = vc.getLeftPath();
				if (vPath.equals("npfitlc:contentId/@extension")) templateId = vc.getRightValue();
			}
		}
		return templateId;
	}
	
	/**
	 * 
	 * @param templateId
	 * @return a link to the DMS
	 * @throws MapperException
	 */
	private String dmsLink(String templateId) throws MapperException
	{
		String href = "../../../../Domains/";

		if (templateId == null)
		{
			// FIXME - need to generalise to other CDA profiles
			String noTemplate = "NonCodedCDADocument/Tabular%20View/POCD_HD010011GB02-NoEdit.htm#ClinicalDocument";
			href = href + noTemplate;
		}
		else
		{
			StringTokenizer st = new StringTokenizer(templateId,"#");
			if (st.countTokens() != 2) throw new MapperException("Unexpected form of template id: " + templateId);
			href = href + "Templates/Tabular%20View/" + st.nextToken() + "-NoEdit.htm#" + st.nextToken();
		}
		
		return ("; see <a href='" + href + "'<b>DMS</b></a>");
	}
	
	/**
	 * preface to the documentation on a Java class or feature, when the message name is defined.
	 * An erxtra paragraph with the message name in bold - so that when one class or feature is
	 * used in several messages, the documentation for each is distinguished.
	 * @return
	 */
	private String docPreface()
	{
		String preface = "";
		if (messageName() != null) preface = "<p>In message <b>'" + messageName() + "'</b></p>";
		return preface;
	}
	
	/**
	 * 
	 * @param node
	 * @param om
	 * @param path
	 * @return
	 */
	private String mappingDocString(ElementDef node, ObjMapping om, String path, String templateId) throws MapperException
	{
		// provide a link to the DMS, only for NHS CDAs.
		String docString = "<p>Represented at CDA node: <b>" + path + "</b>";
		 if (isNHSMIF()) docString = docString + dmsLink(templateId);
		 docString = docString +  "</p>";

		String templateString = "";
		if (templateId != null) templateString = "<p>Template: " + templateId + "</p>";

		String fixedValueString = "";
		boolean foundFixedValue = false;
		for (Iterator<MappingCondition> it = om.getMappingConditions().iterator();it.hasNext();)
		{
			MappingCondition mc = it.next();
			if (mc instanceof ValueCondition)
			{
				ValueCondition vc = (ValueCondition)mc;
				String vPath = vc.getLeftPath();
				if (   (!vPath.endsWith("templateId/@root")) 
					&& (!vPath.endsWith("templateId/@extension"))
					&& (!vPath.equals("npfitlc:contentId/@root")))
				{
					// start a list of fixed values
					if (!foundFixedValue) {fixedValueString = "<p>Fixed Values (required or created) in CDA XML:</p><ul>";}
					// next fixed value
					fixedValueString = fixedValueString + "<li>'" + vc.getRightValue() + "' at path <b>" + truePath(node, vPath) + "</b></li>";
					foundFixedValue = true;
				}
			}
		}
		// end any list of fixed values
		if (foundFixedValue) fixedValueString = fixedValueString + "</ul>";
		return docString + templateString + fixedValueString + "<p> </p>";
	}
	
	/**
	 * convert tag names to wire tag names in a path to a fixed value
	 * @param node
	 * @param vPath
	 * @return
	 */
	private String truePath(ElementDef node, String vPath)
	{
		String truePath = "";
		ElementDef current = node;
		StringTokenizer st = new StringTokenizer(vPath,"/",true);
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			if (step.startsWith("@")) truePath = truePath + step;
			else if (step.equals("/")) truePath = truePath + step;
			else
			{
				if (current != null) current = current.getNamedChildElement(step);
				if (current == null) truePath = truePath + step;
				else truePath = truePath + wireTagName(current);		
			}
		}
		return truePath;
	}
	
	/**
	 * 
	 * @param mappedEobject
	 * @param docString
	 */
	private void addDocumentation(EModelElement mappedEobject,String docString)
	{
		EAnnotation ann = mappedEobject.getEAnnotation(javaDocURI);

		// if this is the first documentation for this class or feature, add it with the preface defining the message
		if (ann == null)
		{
			ann = EcoreFactory.eINSTANCE.createEAnnotation();
			ann.setSource(javaDocURI);
			ann.getDetails().put(javaDocKey, docPreface() + docString);
			mappedEobject.getEAnnotations().add(ann);			
		}

		// append any subsequent documentation on the same class or feature
		else if (ann != null)
		{
			String previousDoc = ann.getDetails().get(javaDocKey);
			String newDoc = previousDoc + docString;
			ann.getDetails().put(javaDocKey, newDoc);
		}
	}

	
	/**
	 * 
	 * @param elDef
	 * @return
	 */
	private String wireTagName(ElementDef elDef)
	{
		String wireName = elDef.getName();
		String desc = elDef.getDescription();
		if ((desc != null) && (desc.startsWith(CDA_NAME))) wireName = desc.substring(CDA_NAME.length());
		return wireName;
	}


	

	//-------------------------------------------------------------------------------------
	//                           Saving changes
	//-------------------------------------------------------------------------------------
	
	private void saveEcoreModel()
	{
		FileUtil.saveResource(ecoreRoot.eResource());
	}

		
	private void trace(String s) {if (tracing) System.out.println(s);}


}
