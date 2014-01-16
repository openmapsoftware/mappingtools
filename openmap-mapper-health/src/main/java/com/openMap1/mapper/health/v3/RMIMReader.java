package com.openMap1.mapper.health.v3;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.w3c.dom.Element;

import com.openMap1.mapper.actions.MakeITSMappingsAction;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.health.cda.TemplateCollection;
import com.openMap1.mapper.structures.XSDStructure;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ParameterClass;
import com.openMap1.mapper.ParameterClassValue;
import com.openMap1.mapper.PropMapping;

/**
 * Class to read a V3 RMIM, and all the CMETs it references directly or indirectly, from
 * their MIF files.
 * 
 * @author robert
 *
 */
public class RMIMReader {
	
	//-----------------------------------------------------------------------------------------------
	//                                   instance variables
	//-----------------------------------------------------------------------------------------------
	
	private boolean tracing = false;
	public boolean tracing() {return tracing;}
	
	// to support file dialogues
	private IWorkbenchPart targetPart;
	
	// name of the wrapper class that implements mappings for the V3 XML ITS
	public static String V3JavaMappingClass = "com.openMap1.mapper.converters.V3_XML_ITS";
	
	// name of the wrapper class that converts internal to external CDA form, and back
	public static String CDAWrapperClass = "com.openMap1.mapper.converters.CDAConverter";
	
	/**
	 * @return true if reading a MIF file from the NHS MIM
	 */
	public boolean isNHSMIF() {return isNHSMIF;}
	private boolean isNHSMIF = false; // will be reset if NHS features are spotted, as below
	
	/* NHS MIF file names look like 'COCD_TP147013UK03-BloodPressureRef' and have 13 characters before 'UK' 
	 * or POCD_RM010011GB01_NonCodedCDADocument.mif with 13 characters before 'GB' */
	private boolean isNHSMIFName(String mifFileRoot) 
	{
		String realmName = mifFileRoot.substring(13);
		return ((realmName.startsWith("UK"))|(realmName.startsWith("GB")));
	}
	
	// the root of the TemplateConfig file for NHS MIF files
	private Element NHSConfigRoot;
	
	private Element rootElement;
	
	private String mifFilePath;
	
	private String mifFolderPath;
	
	// the root of the top MIF file name is now used as the name of the ecore model
	public String mifFileRoot() {return mifFileRoot;}
	private String mifFileRoot;
	
	private EPackage topPackage;
	public EPackage topPackage() {return topPackage;}
	
	private EPackage dataTypePackage;
	public EPackage dataTypePackage() {return dataTypePackage;}
	
	public V3DataTypeHandler v3DataTypeHandler() {return v3DataTypeHandler;}
	private V3DataTypeHandler v3DataTypeHandler;
		
	public V3RMIM topRMIM() {return topRMIM;}
	private V3RMIM topRMIM;
	
	public String rmimName() {return topRMIM.rmimId();}
	
	private Hashtable<String,String> CMETFileNames = new Hashtable<String,String>();
	
	private Hashtable<String,String> CMETEntryClasses = new Hashtable<String,String>();
		
	private String projectName;
	
	private IFolder v3RMIMsFolder;
	
	public static String DATATYPE_PACKAGE_NAME = "datatypes";
	
	private boolean makeDataTypeMappings = true;

	public static String VOCABULARYNAMESPACEURI = "urn:hl7-org:v3/voc";

	public static String VOCABULARYNAMESPACEPREFIX = "voc";

	public static String CDAPREFIX = "cda";

	private boolean CMETIndexRead = false;
	// true if the index of CMETs has been read, so it know where CMETs are
	public boolean CMETIndexRead() {return CMETIndexRead;}
	
	/** three-letter codes and long names for domains in the NHS template config file */
	public Hashtable<String,String> NHSDomains() {return NHSDomains;}
	private Hashtable<String,String> NHSDomains = new Hashtable<String,String>();
	
	/**
	 * First key = mapping set name ( = type name).
	 * Second key = concatenated class name and package name
	 * Integer = 1, 2, etc to make the next allocated subset 's1', 's2', etc.
	 */
	private Hashtable<String,Hashtable<String,Integer>> subsetTable
	 = new Hashtable<String,Hashtable<String,Integer>>();
	
	/**
	 * 
	 * @return record of the CMETs in which one class occurs
	 * key  class name; value = Vector of CMET names
	 */
	public Hashtable<String,Vector<String>> classOccurrences() {return classOccurrences;};
	private Hashtable<String,Vector<String>> classOccurrences;

	/**
	 * @return classes referenced in an association but not defined.
	 * key = class name; value = an RMIM in which the problem was detected
	 */
	public Hashtable<String,String> missingClasses() {return missingClasses;}
	private Hashtable<String,String> missingClasses;

	/**
	 * @return data types used but not defined in the data types MIF.
	 * key = data type name; value = an RMIM in which the problem was detected
	 */
	public Hashtable<String,String> missingDataTypes() {return missingDataTypes;}
	private Hashtable<String,String> missingDataTypes;
	
	/**
	 * @return CMETs referenced but not found in a MIF file
	 * key = CMET name; value = an RMIM in which the problem was detected
	 */
	public Hashtable<String,String> missingCMETs() {return missingCMETs;}
	private Hashtable<String,String> missingCMETs;
		
	/**
	 * @return CMETs which are already being read and analysed, to avoid
	 * reading any CMET more than once.
	 * key = CMET name; value = "1"
	 */
	public Hashtable<String,String> startedCMETs() {return startedCMETs;}
	private Hashtable<String,String> startedCMETs;

	/**
	 * CMETs that have been read and analysed.
	 * key = CMET name; value = RMIM analysis class instance
	 */
	public Hashtable<String,V3RMIM> referencedCMETs() {return referencedCMETs;}
	private Hashtable<String,V3RMIM> referencedCMETs;
	
	/**
	 * @param CMETName a referenced CMET
	 * @return the V3RMIM which analyses it
	 */
	public V3RMIM getReferencedCMET(String CMETName)
	{return referencedCMETs.get(CMETName);}
	
	/**
	 * V3 data types which are represented in the Ecore model as EAttributes,
	 * rather than as associations (EReferences) to the data type class
	 * [0] = V3 type name, [1] = Ecore data type name
	 */
	private static String[][] EcoreTypes = {{"ST","EString"},{"INT","EInt"}};
	
	/**
	 * @param V3DataType the name of a V3 data type
	 * @return  if the V3 data type is to be represented in the Ecore model as 
	 * an EAttribute of  some Ecore type, return the name of that type;
	 * otherwise return null
	 */
	public static String EcoreDataTypeName(String V3DataType)
	{
		String ecoreType = null;
		for (int i = 0; i < EcoreTypes.length; i++)
		{
			String[] typePair = EcoreTypes[i];
			if (typePair[0].equals(V3DataType)) ecoreType = typePair[1];
		}
		return ecoreType;
	}
	
	/**
	 * @param dataTypeName the name of an Ecore data type
	 * @return the meta object for that type
	 */
	public static EDataType getEcoreDataType(String dataTypeName)
	{
		if (dataTypeName.equals("EString")) return EcorePackage.eINSTANCE.getEString();
		if (dataTypeName.equals("EInt")) return EcorePackage.eINSTANCE.getEInt();
		if (dataTypeName.equals("EBigDecimal"))return EcorePackage.eINSTANCE.getEBigDecimal();
		return null;
	}
	
	/**
	 * paths to '.ent' files which are the top templates to be applied to a CDA MIF
	 */
	// private Vector<String> topTemplatePaths;
	
	//---------------------------------------------------------------------------------------------------
	//                        Constructor - reads the MIF file and makes the Ecore model
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param rootElement
	 * @param mifFilePath
	 * @param projectName
	 * @param templateUsageRoot
	 * @param useJavaMappings
	 * @param targetPart
	 * @throws MapperException
	 */
	public RMIMReader(Element rootElement, XSDStructure xsd, String mifFilePath, String projectName, 
			Element templateUsageRoot, boolean useJavaMappings, IWorkbenchPart targetPart)
	throws MapperException
	{
		this.mifFilePath = mifFilePath;
		this.rootElement = rootElement;
		this.projectName = projectName;
		this.targetPart = targetPart;
		
		String mifFileName = FileUtil.getFileName(mifFilePath);
		StringTokenizer st = new StringTokenizer(mifFileName, ".");
		mifFileRoot = st.nextToken();
		String ecoreFolderPath = "platform:/resource/" + projectName + "/ClassModel/";
		String ecoreFilePath = ecoreFolderPath + mifFileRoot + ".ecore";

		int folderLen = mifFilePath.length() - mifFileName.length() - 1;
		mifFolderPath = mifFilePath.substring(0, folderLen);
		
		boolean isCDA = isCDAMIFName(mifFilePath);
		if (isCDA && (templateUsageRoot == null))
			throw new MapperException("A CDA MIF file can only be read in conjunction with a template usage file and some template files");
		
		isNHSMIF = isNHSMIFName(mifFileRoot);
		if (isNHSMIF) trace("Reading NHS-style CDA");

		topPackage = EcoreFactory.eINSTANCE.createEPackage();
		topPackage.setName(packageName(mifFilePath));
		// ensure this class model will be viewed as an RMIM in the class model view
		ModelUtil.addMIFAnnotation(topPackage, "RMIM", "true");

		startedCMETs = new Hashtable<String,String>(); 
		referencedCMETs = new Hashtable<String,V3RMIM>();
		classOccurrences = new Hashtable<String,Vector<String>>();

		missingCMETs = new Hashtable<String,String>(); 
		missingDataTypes = new Hashtable<String,String>(); 
		missingClasses = new Hashtable<String,String>();
		
		// check the MIF version is supported
		mifVersionString = rootElement.getAttribute("schemaVersion");
		// NHS MIMs approximate to MIF 2.1 in some respects
		if (isNHSMIF() && (mifVersionString.equals(""))) mifVersionString = "2.1";
		if (!GenUtil.inArray(mifVersionString, supportedMIFVersion))
			throw new MapperException("MIF version '" + mifVersionString + "' is not supported.");
		trace("Assumed MIF Version: " + mifVersionString);

		// read CMET lookup table 
		readCMETLookupTable();
		trace("Read " + CMETFileNames.size() + " CMET file lookups");
		
		// read entry class name conversions for NHS MIFs
		if (isNHSMIF())
		{
			readEntryNameConversionFile(ecoreFolderPath);
		}
		

		// read data types
		boolean putElementsInV3Namespace = isCDA;
		if (isNHSMIF()) putElementsInV3Namespace = true;
		v3DataTypeHandler  = new V3DataTypeHandler(this,projectName,putElementsInV3Namespace);
		dataTypePackage = v3DataTypeHandler.readDataTypeSchema(ecoreFilePath, makeDataTypeMappings);
		trace("Read " + dataTypePackage.getEClassifiers().size() + " data types");
		
		// recursive descent, creating each CMET once only and storing it in referencedCMETs()
		String rmimTrail = "";
		boolean isTopRMIM = true;
		topRMIM = new V3RMIM(this,rootElement,mifFilePath,rmimTrail,isTopRMIM);
		trace("Read " + referencedCMETs.size() + " CMETs");
		
		// mark the entry class or choice of the top RMIM as the entry point for the whole model
		topRMIM.markEntryClass();
		
		// second pass; ensure each CMET knows about the others it needs, as V3Name objects
		topRMIM.linkCMETs();
		for (Enumeration<V3RMIM> en = referencedCMETs.elements(); en.hasMoreElements();)
		{
			V3RMIM cmet = en.nextElement();
			cmet.linkCMETs();
		}
		
		// third pass; read associations in all CMETs, and add infrastructure root attributes
		topRMIM.readAssociations();
		topRMIM.addInfrastructureRootAttributes();
		for (Enumeration<V3RMIM> en = referencedCMETs.elements(); en.hasMoreElements();)
		{
			V3RMIM cmet = en.nextElement();
			cmet.readAssociations();
			cmet.addInfrastructureRootAttributes();
		}
		trace("Made associations and added infrastructure root attributes");
				
		// record anything that could not be found
		writeProblems();
		
		// note how many times each class name occurs
		boolean isFalse = false;
		if (isFalse) writeClassOccurrences();
		
		// define the top classes for making mapping sets
		List<EClass> topClasses = topRMIM.getEntryV3Name().getAllEClasses();
		
		// order EReferences in the Ecore model from the schema, if the user provides one
		if (xsd != null) orderFromSchema(topClasses,xsd);
		
		/* read the template files and use them to constrain the Ecore model;
		 * but NHS CDAs are not done this way (templates are RMIMs; already read) */
		if (isCDA && !isNHSMIF)
		{
			TemplateCollection templateCollection = new TemplateCollection(templateUsageRoot,this);
			templateCollection.readTemplateFiles();
			templateCollection.resolveAllTemplates();
			templateCollection.checkTemplateLevels();

			// remake the Ecore model as the constrained model
			topPackage = templateCollection.makeConstrainedCDAECoreModel(ecoreFilePath);
			/* redefine the list of top classes to make mapping sets 
			 * (the list has only 1 class in it, ClinicalDocument) */
			topClasses = templateCollection.topClasses();
			trace("Constrained Ecore model");
		}
		
		// annotate the entry class to show the NHS wrapper class, if necessary; and the top package
		if (isNHSMIF)
		{
			  ModelUtil.addMIFAnnotation(topClasses.get(0), "wrapperClass", MakeITSMappingsAction.NHSV3WrapperClass);		
			  ModelUtil.addMIFAnnotation(topPackage, "isNHSMIF", "true");					
		}
		
		checkClassesInPackages();
		
		/* for NHS MIFs, ensure there are no ActRelationship or Participation clones with more than one
		 * Act or Role child classes  */
		if (isNHSMIF()) normaliseLinkClasses();

		// make the Mapping Sets for RMIMs
		MIFStructure mifStructure = new MIFStructure(topClasses, topPackage, this);
		if (tracing && isFalse) writeMIFStructures(mifStructure);
		trace("Making mapping sets: " + mifStructure.allStructures().size());
		makeRMIMMappingSets(ecoreFilePath,mifStructure,useJavaMappings,topClasses,isCDA);
	}
	
	
	
	/**
	 * checking there are no associations to classes 
	 * not in a package
	 */
	private void checkClassesInPackages()
	{
		trace("Model package");
		for (Iterator<EPackage> ip = topPackage.getESubpackages().iterator();ip.hasNext();)
		{
			EPackage subPack = ip.next();
			trace("Package " + subPack.getName() + ": " + subPack.getEClassifiers().size() + " classes");
			Vector<EClass> classes = new Vector<EClass>();
			for (Iterator<EClassifier> it = subPack.getEClassifiers().iterator();it.hasNext();)
			{
				EClassifier next = it.next();
				if (next instanceof EClass) classes.add((EClass)next);
			}
			for (Iterator<EClass> it = classes.iterator();it.hasNext();)
			{
				EClassifier next = it.next();
				if (next instanceof EClass)
				{
					EClass theClass = (EClass)next;
					for (Iterator<EReference> ir = theClass.getEAllReferences().iterator();ir.hasNext();)
					{
						EReference ref = ir.next();
						EClassifier target = ref.getEType();
						if (target == null)
						{
							theClass.getEStructuralFeatures().remove(ref);
							trace("Class " + theClass.getName() + " has association " + ref.getName() 
									+ " to a null class");
							
						}
						else if (target.getEPackage() == null)
						{
							trace("Class " + theClass.getName() + " has association " + ref.getName() 
									+ " to class " + target.getName() + " which has no package.");
							// try to find a class of the required name in the package of the outer class
							EClassifier newTarget = subPack.getEClassifier(target.getName());
							if (newTarget != null)
							{
								ref.setEType(newTarget);
								trace("recovered; found class '" + target.getName() + "' in package '" + subPack.getName() + "'");
							}
							else
							{
								theClass.getEStructuralFeatures().remove(ref);								
								trace("cannot recover; class '" + target.getName() + "' not found in package '" + subPack.getName() + "'");
							}
							
						}
					}
				}
			}
		}
	}
	
	/**
	 * diagnostic write of the ElementDefs in the MIF structure, and their sizes
	 * @param mifStructure
	 */
	private void writeMIFStructures(MIFStructure mifStructure)
	{
		trace("MIF structures");
		for (Enumeration<String> en = mifStructure.allStructures().keys();en.hasMoreElements();)
		{
			String typeName = en.nextElement();
			ElementDef elDef = mifStructure.allStructures().get(typeName);
			trace("Structure: " + typeName + "; element size: " + mifStructure.getElementSize(elDef));
		}
	}

	
	/**
	 * @param path full path to the MIF file
	 * @return root of the file name followed by '_model', for use as an Ecore package name
	 */
	private String packageName(String path)
	{
		StringTokenizer st = new StringTokenizer(FileUtil.getFileName(path),".");
		return st.nextToken() + "_model";
	}
	
	/**
	 * read in the directory of CMETs and the data type definition file
	 * @throws MapperException
	 */
	private void readCMETLookupTable() throws MapperException
	{
		if (isNHSMIF())
		{
			readNHSTemplateConfigFile();
			return;
		}
		
		String CMETFileName = null;
		// up to MIF 2.1, a fixed name for the CMET index file
		if ((mifVersionString.equals("2.0"))|(mifVersionString.equals("2.1")))
		{
			CMETFileName = "cmetList.mif";
		}
		// MIF 2.1.3 allows different names for the CMET index file
		if (mifVersionString.equals("2.1.3"))
		{
			// index to CMET files
			Element CMETFileElement = XMLUtil.firstNamedChild(rootElement, "importedCommonModelElementPackage");
			if (CMETFileElement != null) CMETFileName = mifFileName(CMETFileElement);
			// fallback which works for the May 2009 Ballot pack
			else CMETFileName ="DEFN=UV=IFC=1.8.3.coremif";
		}
		
		if (CMETFileName != null)
		{
			// the CMET index file is in the same folder as all RMIMs
			String CMETPath = FileUtil.siblingFilePath(mifFilePath,CMETFileName);
			Element CMETFileRoot = XMLUtil.getRootElement(CMETPath);
			makeCMETIndex(CMETFileRoot);
			CMETIndexRead = true;			
		}
	}
	
	/**
	 * For NHS MIF files, read the appropriate version of TemplateConfig.xml 
	 */
	private void readNHSTemplateConfigFile() throws MapperException
	{
		File mifFolder = new File(mifFolderPath);
		File[] mifFiles = mifFolder.listFiles();
		if (mifFiles == null) throw new MapperException("No folder at '" + mifFolderPath + "'");
		for (int f = 0; f < mifFiles.length; f++)
		{
			File mifFile = mifFiles[f];
			String fileName = mifFile.getName();
			if (fileName.endsWith("TemplateConfig.xml"))
			{
				NHSConfigRoot = XMLUtil.getRootElement(mifFile.getAbsolutePath());
				// store domain codes from the template config file
				Element domainList = XMLUtil.firstNamedChild(NHSConfigRoot, "DomainList");
				if (domainList == null) throw new MapperException("Cannot find domain list in template config file '"  + fileName + "'");
				Vector<Element> domainEls = XMLUtil.namedChildElements(domainList,"domain");
				NHSDomains = new Hashtable<String,String>();
				for (Iterator<Element> it = domainEls.iterator(); it.hasNext(); )
				{
					Element domainEl = it.next();
					NHSDomains.put(domainEl.getAttribute("id"), XMLUtil.getText(domainEl));
				}
			}
		}
		if (NHSConfigRoot == null) throw new MapperException("Cannot find Template Config file");
		trace("Found template config file");
	}
	
	/**
	 * find a given template file in the MIF folder. 
	 * This is a mif file whose name begins with the template name.
	 * Sometimes in a template name like 'POCD_MT010006UK01' , one of 'MT' 'HD' or 'RM'
	 * needs to be changed to one of the others to get a match. Write a warning if this happens.
	 * @param templateName
	 * @return
	 * @throws MapperException
	 */
	public String getTemplateFilename(String templateName) throws MapperException
	{
		String[] name_sections = {"MT","RM", "HD"};
		
		String fName = null;
		File mifFolder = new File(mifFolderPath);
		File[] mifFiles = mifFolder.listFiles();
		if (mifFiles == null) throw new MapperException("No folder at '" + mifFolderPath + "'");
		
		// try matching the file name with no change of 'MT' etc.
		for (int f = 0; f < mifFiles.length; f++)
		{
			String fileName = mifFiles[f].getName();
			if (fileName.startsWith(templateName)) fName= fileName;
		}
		
		// try matching the file name with some change of 'MT' to 'RM' etc.
		for (int i = 0; i < name_sections.length;i++) if (fName == null)
		{
			String changedTemplateName = changeTemplateName(templateName,name_sections[i]);
			trace("Changing template name from '" + templateName + "' to '" + changedTemplateName);
			for (int f = 0; f < mifFiles.length; f++)
			{
				String fileName = mifFiles[f].getName();
				if (fileName.startsWith(changedTemplateName)) fName= fileName;
			}
		}

		// still no match found - give up.
		if (fName == null) throw new MapperException("Cannot find Template RMIM for '" + templateName + "'");
		return fName;
	}
	
	/**
	 * change a template name like 'POCD_MT010006UK01' to 'POCD_RM010006UK01'.
	 * @param templateName e.g. 'POCD_MT010006UK01'
	 * @param middleTwoChars e.g. 'RM'
	 * @return e.g 'POCD_RM010006UK01'
	 */
	private String changeTemplateName(String templateName, String middleTwoChars)
	{
		String newName = templateName.substring(0,5) + middleTwoChars + templateName.substring(7,17);
		return newName;
	}
	
	/**
	 * 
	 * @param constraintId an NHS constraint id such as 'NPFIT-000014#Role'
	 * @param domainName a domain identifier such as 'HSC'
	 * @param parentRMIM the identifier of the parent MIF, such as 'POCD_MT010002UK01'
	 * @return a list of identifiers for RMIM templates, such as 'COCD_TP145018UK03'
	 */
	public Vector<String> getTemplateIds(String constraintId, String domainName, String parentRMIM)
	{
		System.out.println("Constraint " + constraintId + " domain " + domainName + " parent " + parentRMIM);
		Vector<String> ids = new Vector<String>();
		Vector<Element> templateEls = XMLUtil.namedChildElements(NHSConfigRoot, "Template");
		// find the <Template> element with the correct constraint 
		for (Iterator<Element> it = templateEls.iterator();it.hasNext();)
		{
			Element tempEl = it.next();
			if ((tempEl.getAttribute("id").equals(constraintId)) && (tempEl.getAttribute("status").equals("A")))
			{
				Element parent = XMLUtil.firstNamedChild(tempEl, "parentModel");
				for (Iterator<Element> iu = XMLUtil.namedChildElements(parent, "domain").iterator();iu.hasNext();)
				{
					Element domain = iu.next();
					// find parent RMIM ids for this domain
					if (domain.getAttribute("id").equals(domainName))
					{
						Vector<Element> pidEls = XMLUtil.namedChildElements(domain, "id");
						// check that one of the parent ids matches the actual parent RMIM
						for (Iterator<Element> ie = pidEls.iterator(); ie.hasNext();)
							if (matchable(XMLUtil.getText(ie.next()),(parentRMIM)))
							{
								Vector<Element> idEls = XMLUtil.namedChildElements(tempEl, "id");
								// copy across the template RMIM ids
								for (Iterator<Element> ig = idEls.iterator(); ig.hasNext();)
									ids.add(XMLUtil.getText(ig.next()));							
							}
					}					
				}
			}
		}
		System.out.println(GenUtil.singleString(ids));
		return ids;
	}
	
	/**
	 * 
	 * @param id1 an RMIM id, like 'POCD_MT010001UK01'
	 * @param id2 another RMIM id, like 'POCD_RM010001UK01'
	 * @return true if the two match except for the mismatch of 'RM' and 'MT'
	 */
	private boolean matchable(String id1, String id2)
	{
		boolean match = false;
		try
		{
			String p1 = id1.substring(0,4);
			String p2 = id2.substring(0,4);
			String e1 = id1.substring(7);
			String e2 = id2.substring(7);
			match = ((p1.equals(p2)) && (e1.equals(e2)));
		}
		catch (Exception ex) {}
		return match;		
	}
	
	
	/**
	 * @param fileNameStart the start of the name of an NHS MIF file
	 * @return the root element of the file
	 * @throws MapperException
	 */
	protected Element getNHSMIFRoot(String fileNameStart)  throws MapperException
	{
		Element root = null;
		File mifFolder = new File(mifFolderPath);
		File[] mifFiles = mifFolder.listFiles();
		if (mifFiles == null) throw new MapperException("No folder at '" + mifFolderPath + "'");
		for (int f = 0; f < mifFiles.length; f++)
		{
			File mifFile = mifFiles[f];
			String fileName = mifFile.getName();
			if (fileName.startsWith(fileNameStart))
			{
				root = XMLUtil.getRootElement(mifFile.getAbsolutePath());
			}
		}
		if (root == null) throw new MapperException("Cannot find MIF file with name starting '" + fileNameStart + "'");
		return root;
	}
	
	
	/* data types - I am now using the data type schema to define data type classes; but
	 * retain the following code in case I need to check the version of the data types */
	
	/*
	Element dataTypeFileElement = XMLUtil.firstNamedChild(rootElement, "importedDatatypeModelPackage");
	if (dataTypeFileElement == null) throw new MapperException("RMIM file has no 'importedDatatypeModelPackage' element");
	String dataTypeFileName = mifFileName(dataTypeFileElement);
	// the data type MIF file is in the same folder as all RMIMs
	String dataTypeFilePath = FileUtil.siblingFilePath(path,dataTypeFileName);
	Element dataTypeFileRoot = XMLUtil.getRootElement(dataTypeFilePath);
	*/

	
	/**
	 * @return the entry class for the whole RMIM - not used
	 */
	public EClass entryClass() throws MapperException
	{
		List<EClass> topClasses = topRMIM.getEntryV3Name().getAllEClasses();
		if (topClasses.size() != 1) throw new MapperException("RMIM does not have one top class");
		return topClasses.get(0);
	}
	
	//--------------------------------------------------------------------------------------------
	//               lookup from CMET names to CMET file names
	//--------------------------------------------------------------------------------------------
	
	/**
	 * @param CMETFileRoot the root element of the CMET defining file. 
	 * Set up the index from CMET names to CMET file names
	 */
	private void makeCMETIndex(Element CMETFileRoot)  throws MapperException
	{
		CMETFileNames = new Hashtable<String,String>();
		CMETEntryClasses = new Hashtable<String,String>();
		if (!CMETFileRoot.getLocalName().equals("staticModelInterfacePackage"))
			throw new MapperException("CMET Index file does not have root element 'staticModelInterfacePackage'");
		for (Iterator<Element> it = XMLUtil.namedChildElements(CMETFileRoot, "commonModelElementDefinition").iterator();it.hasNext();)
		{
			Element el = it.next();
			String CMETName = el.getAttribute("name");
			Element child = XMLUtil.firstNamedChild(el, "boundStaticModel");
			if (child != null)
			{
				/*  In 'COCT_HD010001UV01.mif', 'CO' = subSection, 'CT' = domain,
				 *  '010001' = id, 'UV' = realmNamespace, '01' = version.   */
				String CMETFileName = child.getAttribute("subSection") + child.getAttribute("domain")
				+ "_HD" + child.getAttribute("id") + child.getAttribute("realmNamespace")
				+ child.getAttribute("version") + ".mif";
				CMETFileNames.put(CMETName, CMETFileName);			
			}
			Element entry = XMLUtil.firstNamedChild(el, "entryClass");
			if (entry != null)
			{
				String entryClassName = entry.getAttribute("name");
				CMETEntryClasses.put(CMETName,entryClassName);
			}
		}
	}
	
	/**
	 * @param CMETName the name of a CMET
	 * @return the name of the file it is defined in
	 */
	public String getCMETFilename(String CMETName) throws MapperException
	{
		if (!CMETIndexRead) throw new MapperException("RMIM file has no 'importedCommonModelElementPackage' element, so cannot look up CMET file names");		
		return CMETFileNames.get(CMETName);
	}

	/**
	 * @param CMETName the name of a CMET
	 * @return the name of its entry class
	 */
	public String getCMETEntryClassName(String CMETName)
	{return CMETEntryClasses.get(CMETName);}
	//--------------------------------------------------------------------------------------------
	//                                       miscellaneous
	//--------------------------------------------------------------------------------------------


	/**
	 * @return the prefix for the mif namespace used in the top RMIM file
	 */ 
	// private String prefix() {return rootElement.lookupPrefix(rootElement.getNamespaceURI()) + ":";}
	
	/**
	 * 
	 * @param el and element defining the name of a common file - either
	 * the data types file or the CMET name to file name conversion file
	 * @return the file name
	 */
	private String mifFileName(Element el)
	{
		return (el.getAttribute("root") + "=" + el.getAttribute("realmNamespace")
				+ "=" + el.getAttribute("artifact") + "=" + el.getAttribute("version") + ".coremif");
	}
	
	/**
	 * write out the Ecore package as a resource.
	 */
	public void writePackage(String filePath) throws MapperException
	{
		ModelUtil.savePackage(filePath, topPackage);
	}
	
	/**
	 * For test purposes, write all problems detected to the system console
	 */
	private void writeProblems()
	{
		if (missingCMETs.size() > 0) writeMissingThings("Missing CMETS", missingCMETs, false);
		if (missingClasses.size() > 0) writeMissingThings("Missing Classes", missingClasses, true);
		if (missingDataTypes.size() > 0) writeMissingThings("Missing Data Types", missingDataTypes, true);
	}
	
	private void writeMissingThings(String title, Hashtable<String,String> missingThings, boolean writePlace)
	{
		System.out.println("\n" + title + ": " + missingThings.size());
		for (Enumeration<String> en = missingThings.keys(); en.hasMoreElements();)
		{
			String thing = en.nextElement();
			String place = missingThings.get(thing);
			if (writePlace) System.out.println(thing + "\t" + place);
			else System.out.println(thing);
		}
	}
	
	/**
	 * write out all classes which occur more than once,
	 * listing the CMETs they occur in
	 */
	private void writeClassOccurrences()
	{
		System.out.println("\nClasses which occur more than once:");
		int repeats = 0;
		for (Enumeration<String> en = classOccurrences.keys();en.hasMoreElements();)
		{
			String className = en.nextElement();
			Vector<String> occs = classOccurrences.get(className);
			if (occs.size() > 1)
			{
				repeats++;
				String cmets = className + "(" + occs.size() + ") : \t";
				for (int i = 0; i < occs.size(); i++)
					cmets = cmets + occs.get(i) + "\t";
				System.out.println(cmets);			
			}
		}
		System.out.println("\n" + repeats + " repeated classes");
	}
	
	//-------------------------------------------------------------------------------------------------
	//                        Making V3-V3 Mapping sets for each RMIM
	//-------------------------------------------------------------------------------------------------
	
	/**
	 * if useJavaMappings = false, make one V3 mapping set for every type definition in the MIF structure, except for those below a minimum
	 * size (number of ElementDefs)
	 * if useJavaMappings = true, just make one top Java mapping set for each entry class, which 
	 * invokes the Java mapping class for the V3 XML ITS
	 */
	private void makeRMIMMappingSets(String ecoreFilePath, MIFStructure mifStructure, 
			boolean useJavaMappings, List<EClass> topClasses, boolean isCDA) 
	throws MapperException
	{
		int mappingSetsMade = 0;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);		
		IFolder mappingsFolder = project.getFolder("MappingSets");
		v3RMIMsFolder = mappingsFolder.getFolder("V3RMIMs");
		if (!v3RMIMsFolder.exists()) 
		{
			try {v3RMIMsFolder.create(true, true, null);} // force, local, no progress monitor 
			catch (Exception ex) {throw new MapperException("Cannot make folder for data type mapping sets");}
		}

		for (Enumeration<String> en = mifStructure.allStructures().keys();en.hasMoreElements();)
		{
			String typeName = en.nextElement();
			ElementDef elementDef = mifStructure.allStructures().get(typeName);
			EClass theClass = mifStructure.getEClassForType(typeName);
			// if using Java mappings, make only (small) mapping sets for the top classes of the top RMIM
			if (useJavaMappings && (isTopClass(theClass,topClasses)))
			{
				makeMappingSetForRMIM(ecoreFilePath, mifStructure,typeName,elementDef,theClass,useJavaMappings,isCDA);
			}

			// if not using Java mappings, make mapping sets for all MIF structures
			else if (!useJavaMappings)
			{
				makeMappingSetForRMIM(ecoreFilePath, mifStructure,typeName,elementDef,theClass,useJavaMappings,isCDA);
				mappingSetsMade++;				
			}
		}
		trace("Mapping sets made: " + mappingSetsMade);
	}
	
	/**
	 * 
	 * @param theClass
	 * @param topClasses
	 * @return true if this class is oone of the top classes of the RMIM
	 */
	private boolean isTopClass(EClass theClass,List<EClass> topClasses)
	{
		boolean isTop = false;
		String cName = theClass.getName();
		for (Iterator<EClass> ic = topClasses.iterator(); ic.hasNext();)
			if (ic.next().getName().equals(cName)) isTop = true;
		return isTop;
	}

	private void makeMappingSetForRMIM(String ecoreFilePath, MIFStructure mifStructure,
			String typeName,ElementDef elementDef,EClass topClass, 
			boolean useJavaMappings, boolean isCDA)
	throws MapperException
	{
		// do not re-make a mapping set if it exists from a previous run and has not been deleted
		IFile existingMappingSet = v3RMIMsFolder.getFile(typeName + ".mapper");
		if (!(existingMappingSet.exists()))
		{
			// (1) Make an empty mapping set in the 'V3RMIMs' folder of the MappingSets folder
			String mappingSetLocation = getRMIMMappingSetLocation(projectName,typeName);
			URI uri = URI.createURI(mappingSetLocation);			
			Resource mappingSet = ModelUtil.makeNewMappingSet(uri);
			MappedStructure mappedStructure = (MappedStructure)mappingSet.getContents().get(0);
			mappedStructure.setUMLModelURL(ecoreFilePath);
			
			// (2) make the MIF structure the structure definition for the mapping set (with its namespaces)
			mappedStructure.setStructureURL("");
			mappedStructure.setStructureDefinition(mifStructure);
			
			// (3) make the type name the top complex type for the schema
			mappedStructure.setTopElementType(typeName);
			ElementDef newStructure = mifStructure.typeStructure(typeName);
			mappedStructure.setRootElement(newStructure);
			
			// option to make only the top mapping set, which uses Java mappings to implement the V3 XML ITS
			if (useJavaMappings)
			{
				GlobalMappingParameters gmp = mappedStructure.getMappingParameters();
				gmp.setMappingClass(V3JavaMappingClass);
				// apply the CDA wrapper class if necessary
				if (isCDA) gmp.setWrapperClass(CDAWrapperClass);
				else if (isNHSMIF()) gmp.setWrapperClass(MakeITSMappingsAction.NHSV3WrapperClass);
			}
			
			// option to make all mappings and a load of imported mapping sets
			else if (!useJavaMappings)
			{
				// (4) recursive descent of the structure tree, making all mappings
				newStructure.setExpanded(true);
				makeMappings(mifStructure,typeName,newStructure, topClass);				
				
				// (5) add the correct parameter class to the top node
				EClass theClass = mifStructure.getEClassForType(typeName);
				addParameterClass(mappedStructure,theClass);
			}
			
			// (6) Save the mapping set
			ModelUtil.saveMappingSet(mappingSet);			
		}
		
	}
	
	/**
	 * @param typeName the name of the mapping set
	 * @param elementDef a node in the mapped structure
	 * @param mappedClass  the class that is to be mapped to that node
	 * @return the subset allocated to the mapped class
	 * make all necessary mappings or imports on this node and its descendants
	 */
	private String makeMappings(MIFStructure mifStructure,String typeName,ElementDef elementDef, EClass mappedClass)
	{
		EPackage thePackage = mappedClass.getEPackage();
		// make the object mapping for this class
		String subset = getSubset(typeName,mappedClass.getName(),thePackage.getName());
		addObjectMapping(elementDef,thePackage,mappedClass.getName(),subset);
		
		// make all the property mappings
		for (Iterator<AttributeDef> it = elementDef.getAttributeDefs().iterator(); it.hasNext();)
		{
			AttributeDef attDef = it.next();
			addPropertyMapping(attDef,mappedClass,attDef.getName(),subset);
		}
		
		// make all object mappings to child classes, and the association mappings to them
		for (Iterator<ElementDef> it  = elementDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef childElement = it.next();
			EReference ref = (EReference)mappedClass.getEStructuralFeature(childElement.getName());
			EClass childClass = (EClass)ref.getEType();
			String importURI = "";
			
			/* if the child node is not expanded, it must either import a mapping set - 
			 * for an CMET or a data type; of if the CMET is too small to have a mapping set, not import it
			 * but expand it in the structure */
			if (!childElement.isExpanded())
			{
				String childType = childElement.getType();
				boolean dataTypeImport = childClass.getEPackage().getName().equals(DATATYPE_PACKAGE_NAME);
				// data types always trigger an import of the data type mapping set
				if (dataTypeImport) 
					importURI = getDataTypeMappingSetLocation(projectName,childType);
				else if (!dataTypeImport)
					importURI = getRMIMMappingSetLocation(projectName,childType);
			}
			
			// recursive call to make the object mapping for the child class, and all its descendant mappings
			String childSubset = "";
			childSubset = makeMappings(mifStructure,typeName,childElement,childClass);
			// make the association mapping to the child class
			addAssociationMapping(childElement,mappedClass,subset,childClass,childSubset);
			
			// if there is an import mapping set node to make, make it
			if (!importURI.equals("")) addImportMappingSet(childElement, importURI, childClass,childSubset);
		}
		return subset;
	}
		
	//-------------------------------------------------------------------------------------------------
	//                        Utility methods for making mapping sets
	//-------------------------------------------------------------------------------------------------
	

	
	/**
	 * @param projectName the project
	 * @param typeName the RMIM complex type, which is also the mapping set name
	 * location of an RMIM mapping set 
	 */
	protected static String getRMIMMappingSetLocation(String projectName,String typeName)
	{
		return "platform:/resource/" + projectName 
		+ "/MappingSets/V3RMIMs/" + typeName + ".mapper";
	}

	
	/**
	 * @param projectName the project
	 * @param typeName the data type name, which is also the mapping set name
	 * @return location of a data type mapping set
	 */
	protected static String getDataTypeMappingSetLocation(String projectName,String typeName)
	{
		return "platform:/resource/" + projectName 
		+ "/MappingSets/V3DataTypes/" + typeName + ".mapper";
	}



	/**
	 * @param mappingSet name of the mapping set
	 * @param className name of the class for which an object mapping is to be made
	 * @param package the class is in
	 * @return a new subset name, to avoid two or more object mappings to the same class
	 * in the same mapping set, without different subsets.
	 * The first subset allocated to each class  is the default ""
	 */
	protected String getSubset(String mappingSet, String className, String packageName)
	{
		String subset = "";
		Hashtable<String,Integer> subsetsForMappingSet = subsetTable.get(mappingSet);
		if (subsetsForMappingSet == null) subsetsForMappingSet = new Hashtable<String,Integer>();
		String cpName = className + "$" + packageName;
		Integer subsetIndex = subsetsForMappingSet.get(cpName);
		if (subsetIndex == null) subsetIndex = new Integer(1);
		else
		{
			subset = "s" + subsetIndex.intValue();
			subsetIndex = new Integer(subsetIndex.intValue() + 1);
		}
		subsetsForMappingSet.put(cpName, subsetIndex);
		subsetTable.put(mappingSet, subsetsForMappingSet);
		return subset;
	}
	
	/**
	 * add an object mapping (with its nodeMappingSet) to an ElementDef
	 * @param node an ElementDef in the mapped structure
	 * @param className a class name
	 */
	protected static void addObjectMapping(ElementDef node,EPackage aPackage,String className, String subset)
	{
		NodeMappingSet nodeSet = MapperFactory.eINSTANCE.createNodeMappingSet();
		node.setNodeMappingSet(nodeSet);

		ObjMapping om = MapperFactory.eINSTANCE.createObjMapping();
		om.setMappedClass(className);
		om.setSubset(subset);
		om.setMappedPackage(aPackage.getName());
		nodeSet.getObjectMappings().add(om);		
	}
	
	
	/**
	 * make a property mapping on a node
	 * @param nodeDef an ElementDef or AttributeDef node on the mapped structure
	 * @param aClass the class owning the property to be mapped
	 * @param propertyName property to be mapped
	 */
	protected static void addPropertyMapping(NodeDef nodeDef, EClass aClass, String propertyName, String subset)
	{
		// add a NodeMappingSet to the ElementDef or AttributeDef, if it has not already got one
		NodeMappingSet nodeSet = nodeDef.getNodeMappingSet();
		if (nodeSet == null)
		{
			nodeSet = MapperFactory.eINSTANCE.createNodeMappingSet();
			nodeDef.setNodeMappingSet(nodeSet);			
		}
		
		// add a property mapping to the NodeMappingSet
		PropMapping pm = MapperFactory.eINSTANCE.createPropMapping();
		pm.setMappedClass(aClass.getName());
		pm.setSubset(subset);
		pm.setMappedProperty(propertyName);
		pm.setMappedPackage(aClass.getEPackage().getName());
		nodeSet.getPropertyMappings().add(pm);				
	}
	
	/**
	 * add an association mapping on a node, which already has a node mapping set.
	 * The child class is mapped on the same node.
	 * The association from the parent class (mapped to a higher node) is not navigable back to the parent
	 * @param child the elementDef which the mapping will be on. Its node name is  the association role name
	 * @param parentClass the parent class
	 * @param childClass the child class
	 */
	protected static void addAssociationMapping(ElementDef child, EClass parentClass, String parentSubset,
			EClass childClass, String childSubset)
	{
		NodeMappingSet nodeMappingSet = child.getNodeMappingSet(); // exists because there is an object mapping
		AssocMapping am = MapperFactory.eINSTANCE.createAssocMapping();
		
		// the navigable end to the child class is end 2 of the association
		AssocEndMapping aem = MapperFactory.eINSTANCE.createAssocEndMapping();
		aem.setMappedRole(child.getName());
		aem.setMappedClass(childClass.getName());
		aem.setSubset(childSubset);
		aem.setMappedPackage(childClass.getEPackage().getName());
		aem.setRequiredForObject(true);
		am.setMappedEnd2(aem);
		
		// the non-navigable end to the parent class is end 1 of the association
		AssocEndMapping afm = MapperFactory.eINSTANCE.createAssocEndMapping();
		afm.setMappedRole("");
		afm.setMappedClass(parentClass.getName());
		afm.setSubset(parentSubset);
		afm.setMappedPackage(parentClass.getEPackage().getName());
		am.setMappedEnd1(afm);
		
		nodeMappingSet.getAssociationMappings().add(am);		
	}


	/**
	 * add an import mapping set to an ElementDef node
	 * @param child the node to add the import on
	 * @param mappingSetURI
	 * @param rootClass
	 */
	protected static void addImportMappingSet(ElementDef child, 
			String mappingSetURI, EClass rootClass, String subset)
	{
		ImportMappingSet importMappingSet = MapperFactory.eINSTANCE.createImportMappingSet();
		importMappingSet.setMappingSetURI(mappingSetURI);
		child.setImportMappingSet(importMappingSet);
		ParameterClassValue parameterClassValue = MapperFactory.eINSTANCE.createParameterClassValue();
		parameterClassValue.setParameterIndex(0);
		parameterClassValue.setMappedClass(rootClass.getName());
		parameterClassValue.setMappedPackage(rootClass.getEPackage().getName());
		parameterClassValue.setSubset(subset);
		importMappingSet.getParameterClassValues().add(parameterClassValue);		
	}
	
	/**
	 * add a parameter class on the head of a mapping set
	 * @param mappedStructure
	 * @param theClass
	 */
	protected static void addParameterClass(MappedStructure mappedStructure, EClass theClass)
	{
		ParameterClass parameterClass = MapperFactory.eINSTANCE.createParameterClass();
		parameterClass.setClassName(theClass.getName());
		parameterClass.setPackageName(theClass.getEPackage().getName());
		parameterClass.setParameterIndex(0);
		mappedStructure.getParameterClasses().add(parameterClass);		
	}
	
	//----------------------------------------------------------------------------------------------
	//                      dealing with MIF versions
	//----------------------------------------------------------------------------------------------
	
	/**
	 * return one of the static constants MIF_2_0, MIF_2_1, or MIF_2_1_3
	 */
	public int mifVersion()
	{
		int version = 0;
		for (int i = 0; i < supportedMIFVersion.length; i++)
			if ((mifVersionString != null) && (mifVersionString.equals(supportedMIFVersion[i])))
				version = i+1;
		return version;
	}
	
	public static int MIF_2_0 = 1;
	public static int MIF_2_1 = 2;
	public static int MIF_2_1_3 = 3;
	public static int MIF_2_1_4 = 4;
	public static int MIF_2_1_5 = 5;
	public static int MIF_2_1_6 = 6;
		
	private String mifVersionString; 
	
	/**
	 * @return version of MIF in use. Currently supports "2.0","2.1","2.1.3 - 6";
	 */
	public String mifVersionString() {return mifVersionString;}
	
	private String[] supportedMIFVersion = {"2.0","2.1","2.1.3","2.1.4","2.1.5","2.1.6"};
	
	private static String[] mifNamespaceURI = {"urn:hl7-org:v3/mif",
		"urn:hl7-org:v3/mif2",
		"urn:hl7-org:v3/mif2",
		"urn:hl7-org:v3/mif2",
		"urn:hl7-org:v3/mif2",
		"urn:hl7-org:v3/mif2"};
	
	/**
	 * @return the MIF namespace URI appropriate to the version of MIF in use
	 */
	public String mifNamespaceURI()
	{
		String uri = "";
		for (int i = 0; i < supportedMIFVersion.length; i++)
			if ((mifVersionString != null) && (mifVersionString.equals(supportedMIFVersion[i])))
				uri = mifNamespaceURI[i];
		return uri;
	}
	
	//---------------------------------------------------------------------------------------------------
	//                 HL7 Clinical Document Architecture (CDA) and templates
	//---------------------------------------------------------------------------------------------------
	
	
	/**
	 * @param mifFilePath path to a MIF file
	 * @return true if it is the MIF for a CDA (Clinical Document Architecture)
	 * which needs to be constrained by templates
	 */
	public static boolean isCDAMIFName(String mifFilePath)
	{
		String mifFileName = FileUtil.getFileName(mifFilePath);
		return ((mifFileName.startsWith("POCD_HD000040"))|
				(mifFileName.startsWith("POCD_MT000040"))|
				(mifFileName.startsWith("POCD_RM000040")));
	}
	

	//---------------------------------------------------------------------------------------------------
	//			Order EReferences in the Ecore model from the schema, if the user provides one
	//---------------------------------------------------------------------------------------------------

	/**
	 * currently this only operates if there is just one top class, to serve as a starting
	 * point for descent of the schema
	 */
	private void orderFromSchema(List<EClass>topClasses,XSDStructure xsd) throws MapperException
	{
		if (topClasses.size() == 1)
		{
			EClass rootClass = topClasses.get(0);
			String rootName = rootClass.getName();
			
			// check that the root class name is an allowed top element name for the structure
			if (!xsd.isTopElementName(rootName))
				{showMessage("Cannot order elements","XML Schema does not allow root element name '" + rootName + "'");return;}
			
			// recursive descent of containment eReferences of the Ecore model, ordering them to match the schema
			ElementDef topElement = xsd.nameStructure(rootName);
			Hashtable<String,EClass> classNames = new Hashtable<String,EClass>();
			orderEReferences(rootClass,topElement,xsd,classNames);
		}
	}

	/**
	 * recursive descent of containment eReferences of the Ecore model, ordering them to match the schema
	 * This method assumes that all EReferences in the Ecore model are containments.
	 * @param theClass
	 * @param theStructure
	 */
	private void orderEReferences(EClass theClass,ElementDef theElement,
			XSDStructure xsd, Hashtable<String,EClass> classNames) throws MapperException
	{
		String className = theClass.getName();
		
		// avoid infinite recursion, if this class is already being done
		if (classNames.get(className) != null) return;
		// record that this class is being done, so as not to do it again recursively
		classNames.put(className,theClass);
		
		// expand the element definition if necessary
		ElementDef expanded= theElement;
		if ((!theElement.isExpanded()) && (theElement.getType() != null))
			expanded = xsd.typeStructure(theElement.getType());
		
		// set up a new empty EList of EAttributes and EReferences, and copy the EAttributes to it
		BasicEList<EStructuralFeature> newFeatures = allAttributes(theClass);
		
		// find the child elements in the correct order for the structure, and build up the new list of EReferences
		for (Iterator<ElementDef> it = expanded.getChildElements().iterator();it.hasNext();)
		{
			ElementDef childEl = it.next();

			// strip any namespace prefix from the child node name
			String refName= "";
			StringTokenizer st = new StringTokenizer(childEl.getName(),":");
			while (st.hasMoreTokens()) refName= st.nextToken();
			
			// ignore any lack of infrastructureRoot attributes for the moment
			if ((refName.equals("realmCode"))|(refName.equals("templateId"))|(refName.equals("typeId"))) {}

			// look for EReferences with the same name, or which had the same name before NHS template treatment
			else for (Iterator<EReference> ir = namedReferences(theClass, refName).iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				newFeatures.add(ref);
				
				// recursive step
				EClass childClass = (EClass)ref.getEType();
				orderEReferences(childClass,childEl,xsd,classNames);
			}
		}
		
		// check that all EReferences from the class have been matched by element names in the schema; if not, add unmatched EReferences
		checkRefsMatched(theClass,newFeatures);
		
		// apply the new ordered list of EAttributes and EReferences to the class
		theClass.eSet(EcorePackage.eINSTANCE.getEClass_EStructuralFeatures(), newFeatures);
	}
	
	
	/**
	 * check that every EReference in an EClass has been matched by an element name in the schema,
	 * and included in the new list of structural features; if it has not, include it.
	 * @param theClass
	 * @param newFeatures
	 * @throws MapperException if any EReference has not been matched
	 */
	private void checkRefsMatched(EClass theClass,BasicEList<EStructuralFeature> newFeatures)
	{
		for (Iterator<EStructuralFeature> ig = theClass.getEStructuralFeatures().iterator(); ig.hasNext();)
		{
			EStructuralFeature feat = ig.next();
			boolean found = false;
			for (Iterator<EStructuralFeature> it = newFeatures.iterator();it.hasNext();)
				if (it.next().getName().equals(feat.getName())) found = true;
			if (!found)
			{
				trace("***Did not find feature '" + feat.getName() + "' of class '" + theClass.getName() + "' in the schema.");
				newFeatures.add(feat);
			}
		}
	}


	
	/**
	 * @param theClass
	 * @return a list of the EAttributes of the EClass
	 */
	private BasicEList<EStructuralFeature> allAttributes(EClass theClass)
	{
		BasicEList<EStructuralFeature> newFeatures = new BasicEList<EStructuralFeature>();
		for (Iterator<EStructuralFeature> ig = theClass.getEStructuralFeatures().iterator(); ig.hasNext();)
		{
			EStructuralFeature feat = ig.next();
			if (feat instanceof EAttribute) newFeatures.add(feat);
		}
		return newFeatures;
	}
	
	/**
	 * 
	 * @param theClass
	 * @param refName
	 * @return the one Ereference which has the required refName, or a Vector of all those
	 * EReferences which had that name, before it was changed by NHS template treatment.
	 */
	private Vector<EReference> namedReferences(EClass theClass, String refName)
	{
		Vector<EReference> refs = new Vector<EReference>();
		EStructuralFeature theFeature = theClass.getEStructuralFeature(refName);
		if ((theFeature != null) && (theFeature instanceof EReference)) refs.add((EReference)theFeature);
		else if (isNHSMIF())
			for (Iterator<EStructuralFeature> it = theClass.getEStructuralFeatures().iterator();it.hasNext();)
			{
				EStructuralFeature next = it.next();
				if ((next instanceof EReference) && (refName.equals(ModelUtil.getMIFAnnotation(next, "NHSOriginalRole"))))
					refs.add((EReference)next);
			}
		if (refs.size() == 0) trace("Missing ref: " + theClass.getName() + "." + refName);
		return refs;
	}

	//---------------------------------------------------------------------------------------------------
	//     Converting template entry class names, to avoid clashes in class names for NHS templates
	//---------------------------------------------------------------------------------------------------
	
	private Hashtable<String,String> alteredNHSEntryClassNames = new Hashtable<String,String>();
	private Hashtable<String,String> originalNHSEntryClassNames = new Hashtable<String,String>();
	
	private String entryNameConversionFileName = "TemplateEntryNames.csv";
	
	private String entryConversionColumnNames = "Template,Entry_Name,Altered_Entry_Name";

	
	/**
	 * 
	 * @param ecoreFolderPath
	 * @throws MapperException
	 */
	private void readEntryNameConversionFile(String ecoreFolderPath) throws MapperException
	{
		String relativeFilePath = ecoreFolderPath + entryNameConversionFileName;
		String filePath = FileUtil.absoluteLocation(relativeFilePath);
		try
		{
			Vector<String> fileLines = FileUtil.textLines(filePath);
			if (!entryConversionColumnNames.equals(fileLines.get(0))) 
				throw new MapperException("Columns of template entry conversion csv file should be "
						+ entryConversionColumnNames + " but are "  + fileLines.get(0));
			for (int i = 1; i < fileLines.size(); i++)
			{
				String[] row = FileUtil.oldParseCSVLine(3, fileLines.get(i));
				
				// template id => original expected entry class name
				originalNHSEntryClassNames.put(row[0], row[1]);
				// template id => altered entry class name
				alteredNHSEntryClassNames.put(row[0], row[2]);
			}
		}
		catch (Exception ex)
			{WorkBenchUtil.showMessage("Warning","Could not read template entry class name conversion file at " + filePath);}
	}

	/**
	 * 
	 * @param templateId
	 * @return an altered entry class name for the template, or null if there is no alteration
	 */
	public String getOriginalNHSEntryClassName(String templateId)
		{return originalNHSEntryClassNames.get(templateId);}

	/**
	 * 
	 * @param templateId
	 * @return an altered entry class name for the template, or null if there is no alteration
	 */
	public String getAlteredNHSEntryClassName(String templateId)
		{return alteredNHSEntryClassNames.get(templateId);}
	//---------------------------------------------------------------------------------------------------
	//                          Normalising Link Classes
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * Used for NHS templated CDA models.
	 * Link Classes are clones of ActRelationships and Participations.
	 * In a 'normalised' Ecore model, each ActRelationship clone has only one Act clone child, linked to it by a 1..1
	 * association. Similarly Participations and Role clones.
	 * In a non-normalised model, an ActRelationship clone may have more than on Act child (a choice), all with
	 * multiplicity 0..1 and with different templateIds.
	 * This method converts any non-normalised model to a normalised model, by:
	 * (1) Splitting the association from the parent class to the link class, giving the split associations
	 * names ending in '_A', '_B' and so on.
	 * (2) Splitting the link classes, with the same name suffixes
	 * (3) each link class has only one 1..1 association to an Act or Role clone.
	 */
	private void normaliseLinkClasses()  throws MapperException
	{
		trace("Normalising split link classes");
		// normalise link classes in all packages under the top package
		for (Iterator<EPackage> it = topPackage.getESubpackages().iterator();it.hasNext();)
			normaliseLinkClasses(it.next());
	}
	
	/**
	 * normalise all link classes within a package.
	 * Assumes that the parent class is within the same package as the link class - throws an exception if not so
	 * @param thePackage
	 */
	private void normaliseLinkClasses(EPackage thePackage)  throws MapperException
	{
		String[] linkClass = {"ActRelationship","Participation"};

		// make a temporary list of classes in the package as you will be modifying it
		Vector<EClassifier> allClasses = new Vector<EClassifier>();
		for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator();it.hasNext();) allClasses.add(it.next());
		
		for (Iterator<EClassifier> ix = allClasses.iterator();ix.hasNext();)
		{
			EClassifier next = ix.next();
			if (next instanceof EClass)
			{
				EClass theClass = (EClass)next;
				String rimClass = ModelUtil.getMIFAnnotation(theClass, "RIM Class");
				// link classes may be clones of ActRelationship or Participation
				for (int linkIndex = 0; linkIndex < 2; linkIndex++)
				{
					if ((rimClass != null) && (rimClass.equals(linkClass[linkIndex])))
					{
						Vector<EReference> rimChildRefs = findRimChildRefs(theClass,linkIndex);
						// normalise any link class with associations to more than one RIM class
						if (rimChildRefs.size() > 1) normaliseOneLinkClass(theClass,rimChildRefs);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param theClass an ActRelationship or Participatoin clone class
	 * @param linkIndex 0 for ActRelationship, 1 for Role
	 * @return a Vector of associations to child Act or Role clone classes
	 */
	private Vector<EReference> findRimChildRefs(EClass theClass,int linkIndex)
	{
		Vector<EReference> rimRefs = new Vector<EReference>();
		for (Iterator<EReference> it = theClass.getEAllReferences().iterator();it.hasNext();)
		{
			EReference ref = it.next();
			EClass target = (EClass)ref.getEType();
			String targetRimClass = ModelUtil.getMIFAnnotation(target, "RIM Class");
			if ((targetRimClass != null) && (GenUtil.inArray(targetRimClass, linkChildClass(linkIndex))))
				rimRefs.add(ref);
		}
		return rimRefs;
	}
	
	/**
	 * normalise one link class which has been found to have more than one association toa RIM child class
	 * @param theClass
	 * @param rimChildRefs
	 */
	private void normaliseOneLinkClass(EClass theClass,Vector<EReference> rimChildRefs) throws MapperException
	{
		EPackage thePackage = theClass.getEPackage();
		int found = 0;

		// make a temporary list of classes in the package as you will be modifying it
		Vector<EClassifier> allClasses = new Vector<EClassifier>();
		for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator();it.hasNext();) allClasses.add(it.next());

		// find the parent class in the same package which has an association to the link class - throw an exception if not found
		for (Iterator<EClassifier> iy = allClasses.iterator();iy.hasNext();) 
		{
			EClassifier next = iy.next();
			if (next instanceof EClass)
			{
				EClass parentClass = (EClass)next;
				//make a temporary list of references , as you may modify the list
				Vector<EReference> refs = new Vector<EReference>();
				for (Iterator<EReference> ix = parentClass.getEAllReferences().iterator();ix.hasNext();) refs.add(ix.next());

				for (Iterator<EReference> iu = refs.iterator();iu.hasNext();)
				{
					EReference ref = iu.next();
					EClass target = (EClass)ref.getEType();
					if (target.getName().equals(theClass.getName()))
					{
						normaliseALinkClass(parentClass, ref,rimChildRefs);

						// the association has been replaced by two or more new ones - so remove it
						boolean linkRemoved = parentClass.getEStructuralFeatures().remove(ref);
						if (!linkRemoved) throw new MapperException("Failed to remove association to split link class " 
								+ ref.getName() + " from class " + parentClass.getName());					
						found++;
					}
				}

				
			}
		}

		if (found != 1) throw new MapperException("There is not one parent class of split link class '" 
		+ theClass.getName() + "' in the same package '" + thePackage.getName() + "'; there are " + found);
		
		// remove the old link class from the package
		boolean classRemoved = thePackage.getEClassifiers().remove(theClass);
		if (!classRemoved) throw new MapperException("Failed to remove split link class " +theClass.getName() + " from package " + thePackage.getName());
	}
	
	/**
	 * 
	 * @param parentClass
	 * @param parentRef
	 * @param rimChildRefs
	 */
	private void normaliseALinkClass(EClass parentClass,EReference parentRef, Vector<EReference> rimChildRefs) throws MapperException
	{
		trace("Normalising split link class " + parentRef.getEType().getName() + " in package " + parentClass.getEPackage().getName());
		String[] suffixes = {"_A","_B","_C","_D","_E"};

		// collect names of associations not to be cloned on the new link classes
		Vector<String> refNames = new Vector<String>();
		for (Iterator<EReference> it = rimChildRefs.iterator(); it.hasNext();) refNames.add(it.next().getName());

		// make one new link class for each association from the old link class to a child
		int index = 0;
		for (Iterator<EReference> it = rimChildRefs.iterator(); it.hasNext();)
		{
			EReference childRef = it.next();
			makeNewLinkClassAndRef(parentClass, parentRef, childRef,suffixes[index], refNames);
			index++;
		}
	}
	
	/**
	 * 
	 * @param thePackage
	 * @param parentClass
	 * @param parentRef
	 * @param childRef
	 * @param index
	 */
	private void makeNewLinkClassAndRef(EClass parentClass, EReference parentRef, EReference childRef,String suffix,Vector<String> refNames)
	{
		// make the new link class, with no associations to child RIM classes
		EClass oldLinkClass = (EClass)parentRef.getEType();
		String newClassName = oldLinkClass.getName() + suffix;
		EClass newLinkClass = ModelUtil.cloneEClassWithoutFeatures(oldLinkClass, newClassName, refNames);
		
		// add one association from the new link class to the correct child RIM class
		EReference newChildRef = ModelUtil.cloneEReference(childRef);
		newChildRef.setLowerBound(1);
		newLinkClass.getEStructuralFeatures().add(newChildRef);
		
		// add a renamed association from the parent class to the new link class
		EReference newParentRef = EcoreFactory.eINSTANCE.createEReference();
		newParentRef.setName(parentRef.getName() + suffix);
		newParentRef.setEType(newLinkClass); 
		newParentRef.setLowerBound(parentRef.getLowerBound());
		newParentRef.setUpperBound(parentRef.getUpperBound());
		parentClass.getEStructuralFeatures().add(newParentRef);
		
	}


	
	/**
	 * 
	 * @param linkIndex 0 for ActRelationship, 1 for Participation
	 * @return a list of possible child classes for the class above
	 */
	private String[] linkChildClass(int linkIndex)
	{
		if (linkIndex == 0) return ModelUtil.ActSubclasses;
		else return ModelUtil.RoleSubclasses;
	}



	//---------------------------------------------------------------------------------------------------
	//                                         Trivia
	//---------------------------------------------------------------------------------------------------

	private void trace(String s) {if (tracing) System.out.println(s);}
	
	protected void showMessage(String title, String message) {
		MessageDialog.openInformation(
			targetPart.getSite().getShell(),
			title,
			message);
	}

}
