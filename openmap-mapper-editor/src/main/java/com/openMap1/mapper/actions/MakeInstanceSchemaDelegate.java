package com.openMap1.mapper.actions;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.structures.XSDStructure;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ConditionTest;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MultiWay;
import com.openMap1.mapper.Namespace;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.ValueCondition;


/**
 * 
 * @author robert
 * Class to make an XML schema for a persisted instance of the selected ecore model.
 * It has two subclasses MakeEcoreInstanceSchemaDelegate and MakeAlternativeInstanceSchemaDelegate,
 * which respectively make the schema for a conventional Ecore instance, and for 
 * an alternative form of persisted Ecore instance which can be mapped more easily.
 * The two subclasses differ only in the value of isAlternateSchema();
 * all the code is here (so far)
 */
public abstract class MakeInstanceSchemaDelegate extends MapperActionDelegate implements IObjectActionDelegate {

	/**
	 * @return false if making a schema for a plain ECore model instance;
	 * true if making a schema for the alternate 'mapper-friendly' form of instance, 
	 * and then making the mappings onto the Ecore model
	 */
	protected abstract boolean isAlternateSchema();
	
	/**
	 * prefix used in schemas for the XML Schema namespace
	 */
	private static String xmlSchemaPrefix = "xs";
	
	/**
	 * suffix added to ECore class names to create the corresponding complex type name
	 */
	private static String typeSuffix = "_type";
	
	/**
	 * The name used for an id attribute for all instances. Will be modified so
	 * it does not clash with any feature name in the model
	 */
	private String idName = "";
	
	/**
	 * maximum number of object mappings for one class, when it contains itself by nesting
	 */
	private int maxClassNestingDepth() 
	{    	
		return maxClassNestingDepth;
    }
	
	// initial value for no self-nesting
	private int maxClassNestingDepth = 1;
	
	/**
	 * certain classes and all their subclasses are to be excluded from the mappings. 
	 * ElementsDefs that represent them are not expanded
	 */
	private String[] unMappedClasses = {"EAnnotation","EOperation","EGenericType",
			"EFactory","EParameter","ETypeParameter","EEnum","EEnumLiteral"};
	

	
	//-----------------------------------------------------------------------------------------------
	//                               main run method
	//-----------------------------------------------------------------------------------------------
	
	public void run(IAction action)
	{
		tracing = true;
		trace("Making Ecore Instance Schema " + isAlternateSchema());
		
		try{
			// (1) find and open the Ecore model
		     if (!(selection instanceof IStructuredSelection))
		    	 throw new MapperException("Selection is not structured");
		    IStructuredSelection structured = (IStructuredSelection)selection;
		    Object object = structured.getFirstElement();
		    String fileNameRoot = "";
		    EPackage topPackage = null;
		    URI ecoreURI = null;
		    if (object instanceof IFile) {
		         IFile file = (IFile) object;
		         fileNameRoot = new StringTokenizer(file.getName(),".").nextToken();
		         ecoreURI = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		 		 EObject root = FileUtil.getEMFModelRoot(ecoreURI);
		 		 if (root == null)throw new MapperException("Cannot find ecore root");
		 		 if (!(root instanceof EPackage))
		 			 throw new MapperException("Selected file is not an ecore model");
		 		 topPackage = (EPackage)root;
		 		 trace("Top package name " + topPackage.getName());
		    }
		    else throw new MapperException("Selection is not a file");				
		    
		    // (2) Offer the user a choice of top class for the instance to be described by the schema
		    EClass topClass = userChooseTopClass(topPackage);
		    if (topClass == null) return;
		    else trace("Chosen class: " + topClass.getName());
		    
		    // (3) make an IFile for the schema; if a previous one exits, delete it (silently!)
			IProject project = getSelectedProject();
			if (project == null)throw  new MapperException("Selected Ecore file is not in a project");
			IFolder structureFolder = project.getFolder("Structures");
			if (!structureFolder.exists()) throw new MapperException("Selected ecore file is not in a project with a Structures folder");
			String schemaFileName = fileNameRoot + ".xsd";
			if (isAlternateSchema()) schemaFileName = fileNameRoot + "_alt.xsd";
			IFile schemaFile = structureFolder.getFile(schemaFileName);
			if (schemaFile.exists()) schemaFile.delete(true, null);
			
			// (4) write the schema
			writeSchema(topPackage, topClass, schemaFile);
			
			// (5) for the alternate Ecore persistent form, write the mapping sets
			if (isAlternateSchema())
			{
				EClass selfNested = selfNestedClass(topPackage); // any example self-nested class, if there are any
				if (selfNested != null)
				{
					int depth = userChooseNestingDepth(selfNested);
					if (depth == -1) return; // user cancels
					maxClassNestingDepth = depth + 1; // maxClassNestingDepth = number of mappings of self-nested classes
					fileNameRoot = fileNameRoot + "_" + depth; // different names for mapper files of different nesting depth
				}
				URI schemaURI = URI.createPlatformResourceURI("/" + project.getName() + "/Structures/" + schemaFileName,true);
				XSDSchema theSchema = XSDStructure.getXSDRoot(schemaURI);
				XSDStructure altStructure = new XSDStructure(theSchema);
				makeMappingSets(topPackage,ecoreURI,topClass,altStructure,schemaURI,project,fileNameRoot);
			}
		}
		catch (MapperException ex) 
			{showMessage("Unable to make instance schema",ex.getMessage());if (tracing) ex.printStackTrace();}
		catch (IOException ex) 
			{showMessage("Unable to make instance schema",ex.getMessage());}
		catch (CoreException ex) 
			{showMessage("Unable to make instance schema",ex.getMessage());}

	}
	
	/**
	 * @param topPackage the top package of an Ecore class model; show the user the names 
	 * of all classes in all packages  of this model
	 * (note if two classes in different packages have the same name, they are not yet 
	 * disambiguated)
	 * @return the EClass chosen by the user, or null if he cancels
	 */
	private EClass userChooseTopClass(EPackage topPackage)
	{
		EClass chosenClass = null;
		Vector<EClass> allClasses = ModelUtil.getAllClasses(topPackage);
		Vector<String> classNames = new Vector<String>();
		for (Iterator<EClass> it = allClasses.iterator();it.hasNext();)
			classNames.add(it.next().getName());
		int chosen = WorkBenchUtil.chooseOneString("Choose the root class of the Ecore instance", targetPart, classNames);
		if (chosen > -1) chosenClass = allClasses.get(chosen);
		return chosenClass;
	}

	/**
	 * @param selfNested a class which has a containment association to itself
	 * Allow the user to choose a depth of self-nesting of such classes, to be supported
	 * in the auto-generated mappings
	 * @return the depth of nesting (0 for no nesting etc.) or -1 if the user cancels
	 */
	private int userChooseNestingDepth(EClass selfNested)
	{
		int maxDepthOffered = 6;
		Vector<String> depths = new Vector<String>();
		for (int d = 0; d < maxDepthOffered + 1; d++)
			depths.add(new Integer(d).toString());
		int chosen = WorkBenchUtil.chooseOneString("Depth of nesting in the mappings for self-containing classes such as '"
				+ selfNested.getName() + "'", targetPart, depths);
		return chosen;
	}

	/**
	 * if the model has any EClasses linked to themselves by containment
	 * associations, return any one of them
	 * @param topPackage
	 * @return an EClass which has a direct containment association to itself, if there are any
	 */
	private EClass selfNestedClass(EPackage topPackage)
	{
		EClass selfNested = null;
		Vector<EClass> allClasses = ModelUtil.getAllClasses(topPackage);
		for (Iterator<EClass> it = allClasses.iterator();it.hasNext();)
		{
			EClass next = it.next();
			for (Iterator<EReference> ie = next.getEAllReferences().iterator();ie.hasNext();)
			{
				EReference ref = ie.next();
				EClassifier ec = ref.getEType();
				if ((ref.isContainment()) && (ec instanceof EClass))
				{
					EClass superC = (EClass)ec;
					if (superC.isSuperTypeOf(next)) selfNested = next;
				}
			}
		}
		return selfNested;
	}
	
	
	//-----------------------------------------------------------------------------------------------
	//               making schemas for Ecore instances - conventional and alternate
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * @param topPackage the top package of the Ecore model whose instance schema is being written
	 * @param topClass the root class of instances whose schema is being written
	 * @param schemaFile the IFile that the schema is to be written to
	 * create the schema and write it to the IFile
	 */
	private void writeSchema(EPackage topPackage, EClass topClass, IFile schemaFile) throws MapperException
	{
		Document doc = XMLUtil.makeOutDoc();
		Element schemaRoot = schemaElement(doc,"schema");
		schemaRoot.setAttribute("targetNamespace", topPackage.getNsURI());
		schemaRoot.setAttribute("xmlns:" + topPackage.getNsPrefix(), topPackage.getNsURI());
		schemaRoot.setAttribute("xmlns:xmi", XMLUtil.XMIURI);
		schemaRoot.setAttribute("elementFormDefault", "unqualified");
		doc.appendChild(schemaRoot);
		
		// Import the XMI schema 
		Element xmiImport = schemaElement(doc,"import");
		xmiImport.setAttribute("namespace", XMLUtil.XMIURI);
		xmiImport.setAttribute("schemaLocation", XMLUtil.XMISchemaLocation);
		schemaRoot.appendChild(xmiImport);
		
		// make an element declaration for the instance of the root class
		Element topClassElementDefinition = schemaElement(doc,"element");
		topClassElementDefinition.setAttribute("name", topClass.getName());
		topClassElementDefinition.setAttribute("type", topClass.getName() + typeSuffix);
		schemaRoot.appendChild(topClassElementDefinition);
		
		// pick a name for the id attribute, which will not clash with any feature name in the model
		idName = nonClashIdName(topPackage);
		
		// make a type definition for the top class - and recursively, all those it contains
		Hashtable<String,String> classesWritten = new Hashtable<String,String>();
		appendTypeDefinition(doc,schemaRoot,topClass,topClass,classesWritten, true);

		// write out to the IFile
		EclipseFileUtil.writeOutputResource(doc, schemaFile, true);
	}

	/**
	 * @param doc the schema Document
	 * @param schemaRoot the schema element
	 * @param aClass a class in the class model (reached from the root class by containment relations)
	 * @param classesWritten table of classes for which types have been defined - to avoid repetition
	 * @param isTopClass true if this is the class of the root element of the instance
	 * Make a complex type for the class and attach it to the schema; recursively do all contained classes
	 * @throws MapperException
	 */
	private void appendTypeDefinition(Document doc,Element schemaRoot,EClass topClass,EClass aClass, 
			Hashtable<String,String> classesWritten, boolean isTopClass)
	throws MapperException
	{
		// make the complex type element and append it to the schema
		Element typeElement = schemaElement(doc,"complexType");
		typeElement.setAttribute("name", aClass.getName() + typeSuffix);
		Element extendElement = typeElement;
		schemaRoot.appendChild(typeElement);
		String className = aClass.getName();
		classesWritten.put(className,"1");
		
		/* decide if this complex type extends any other. If there are any superclasses, choose the first 
		 * direct superclass that appears in the instance tree. 
		 * The choice is only made here, so does not have to be reproducible. 
		 * The type for the top class should not extend any other type, even if the top class has superclasses. */
		EClass mainSuperClass = null;
		// if (!isTopClass)
			for (Iterator<EClass> ic = aClass.getESuperTypes().iterator();ic.hasNext();)
			{
				EClass superClass = ic.next();
				// pick the first direct superclass which is at the inner end of a containment in the instance
				if ((typeAppearsInSchema(topClass, superClass)) && (mainSuperClass == null))
				{
					mainSuperClass = superClass;
					Element content = schemaElement(doc,"complexContent");
					typeElement.appendChild(content);
					Element extension = schemaElement(doc,"extension");
					extension.setAttribute("base", mainSuperClass.getName() + typeSuffix);
					content.appendChild(extension);
					extendElement = extension;					
				}
			}
		
		/* make a sequence of nested Elements for all containment associations, 
		 * and recursively define types for all the target classes. */
		Element sequence = schemaElement(doc,"sequence");
		boolean sequenceNonEmpty = false;
		for (Iterator<EReference> ir = aClass.getEAllReferences().iterator();ir.hasNext();)
		{
			EReference ref = ir.next();
			String refName = ref.getName();
			// don't include anything in the schemas for derived features
			if ((ref.isContainment()) && (ref.getEType() instanceof EClass) && (!ref.isDerived()))
			{
				EClass targetSuperClass = (EClass)ref.getEType();

				if (!inheritedFrom(ref,mainSuperClass))  // don't add those associations already inherited
				{
					sequenceNonEmpty = true;
					Element childElement = schemaElement(doc,"element");
					childElement.setAttribute("name", refName);
					childElement.setAttribute("type", targetSuperClass.getName() + typeSuffix);
					if (ref.getUpperBound() == -1) childElement.setAttribute("maxOccurs", "unbounded");
					childElement.setAttribute("minOccurs", new Integer(ref.getLowerBound()).toString());
					sequence.appendChild(childElement);					
				}
				
				// make type definitions for all subclasses of the target superclass, not repeating any type
				for (Iterator<EClass> ic = ModelUtil.getAllSubClasses(targetSuperClass).iterator();ic.hasNext();)
				{
					EClass targetClass = ic.next();
					if (classesWritten.get(targetClass.getName()) == null)
						appendTypeDefinition(doc,schemaRoot,topClass,targetClass, classesWritten, false); // false = not the top class
				}
			}
		}
		if (sequenceNonEmpty) extendElement.appendChild(sequence);
		
		/* make attribute declarations for all non-containment associations 
		 * which are not the inverses of containment relations. */
		for (Iterator<EReference> ir = aClass.getEAllReferences().iterator();ir.hasNext();)
		{
			EReference ref = ir.next();
			if ((!inheritedFrom(ref,mainSuperClass))  // don't add those associations already inherited
					&& (!ref.isContainment()) // containment relations have already been done
					&& (ref.getEType() instanceof EClass) // FIXME - what should we do with these?
					&& (!ref.isDerived())  // no derived features - they are not persisted
					&& ((ref.getEOpposite() == null)||(!ref.getEOpposite().isContainment())))
			{
				Element nonConRef = schemaElement(doc,"attribute");
				nonConRef.setAttribute("name",ref.getName());
				if (ref.getLowerBound() == 1) nonConRef.setAttribute("use", "required");
				nonConRef.setAttribute("type", "xs:string");
				extendElement.appendChild(nonConRef);
			}
		}
		
		// make attribute declarations for all properties of the class
		for (Iterator<EAttribute> ia = aClass.getEAllAttributes().iterator();ia.hasNext();)
		{
			EAttribute att = ia.next();
			// don't add those attributes already inherited or derived
			if ((!inheritedFrom(att,mainSuperClass)) && (!att.isDerived())) 
			{
				Element attEl = schemaElement(doc,"attribute");
				attEl.setAttribute("name",att.getName());
				if (att.getLowerBound() == 1) attEl.setAttribute("use", "required");
				attEl.setAttribute("type", "xs:string");
				extendElement.appendChild(attEl);
			}
		}
		
		/* for the alternate Ecore serialisation, declare a non-clashing 'id' attribute
		 * for each instance of the class. Only do this for classes which are not subclasses; 
		 * subclasses inherit it from them. */
		if ((isAlternateSchema()) && (mainSuperClass == null))
		{
			Element attEl = schemaElement(doc,"attribute");
			attEl.setAttribute("name",idName);
			attEl.setAttribute("type", "xs:string");
			extendElement.appendChild(attEl);			
		}
	}
	
	/**
	 * 
	 * @param rootClass the root class for an instance of the model
	 * @param aClass a class
	 * @return true if instances of the class may appear in the tree below the root class
	 */
	private boolean appearsInInstance(EClass rootClass, EClass aClass)
	{
		return ((!aClass.isAbstract()) && (typeAppearsInSchema(rootClass, aClass)));
	}
	
	/**
	 * @param rootClass the class of the root of the instance tree
	 * @param aClass any EClass
	 * @return true if there are containment association to the class in the tree underneath 
	 * the root class (so that anty non-abstract subclasses of the class may be represented
	 * in the instance)
	 */
	private boolean typeAppearsInSchema(EClass rootClass, EClass aClass)
	{
		Hashtable<String,String> classesTried = new Hashtable<String,String>();
		// initially true if the class appears anywhere in the containment tree under the top class
		boolean appears = typeAppearsInSchema(rootClass, aClass, classesTried);
		// but make it false if the class or any of its superclasses is on a list of classes not to map
		if (GenUtil.inArray(aClass.getName(), unMappedClasses)) appears = false;
		for (Iterator<EClass> ic = aClass.getEAllSuperTypes().iterator(); ic.hasNext();)
			if (GenUtil.inArray(ic.next().getName(), unMappedClasses)) appears = false;
		return appears;
	}
	
	/**
	 * recursive descent of the containment association tree, not revisiting any class
	 * @param aClass current class in the tree being checked for equality with the test class
	 * @param bClass class being tested for inclusion in the tree
	 * @param classesTried classes in the tree already tested
	 * @return true if bClass is in the tree below aClass
	 */
	private boolean typeAppearsInSchema(EClass aClass, EClass bClass, Hashtable<String,String> classesTried)
	{
		if (aClass.getName().equals(bClass.getName())) return true;
		classesTried.put(aClass.getName(), "1");
		for (Iterator<EReference> ir = aClass.getEAllReferences().iterator();ir.hasNext();)
		{
			EReference ref = ir.next();
			if ((ref.isContainment()) && (ref.getEType() instanceof EClass))
			{
				EClass targetSuperClass = (EClass)ref.getEType();
				// try all subclasses of the target superclass
				for (Iterator<EClass> ic = ModelUtil.getAllSubClasses(targetSuperClass).iterator();ic.hasNext();)
				{
					EClass targetClass = ic.next();
					if ((classesTried.get(targetClass.getName()) == null) && 
							(typeAppearsInSchema(targetClass,bClass,classesTried))) return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param feature an EReference or EAttribute
	 * @param superClass one of the superclasses of the class; or null
	 * @return true if the EReference or EAttribute is inherited from the superclass
	 */
	private boolean inheritedFrom(EStructuralFeature feature, EClass superClass)
	{
		if (superClass == null) return false;
		return (superClass.getEStructuralFeature(feature.getName()) != null);
	}
	
	/**
	 * 
	 * @param doc the schema document
	 * @param localName local name of an Element
	 * @return the element in the XML Schema namespace
	 * @throws MapperException
	 */
	private Element schemaElement(Document doc, String localName) throws MapperException
		{return XMLUtil.NSElement(doc, xmlSchemaPrefix, localName, XMLUtil.SCHEMAURI);}
	
	/**
	 * @param topPackage the top package of an ecore model
	 * @return the name for an object unique identifier 'id' attribute, which does not clash
	 * with the name of any other structural feature of any class in the model
	 */
	public static String nonClashIdName(EPackage topPackage)
	{
		String idRoot = "alt_id";
		String idName = idRoot;
		Vector<String> featureNames = new Vector<String>();
		getFeatureNames(featureNames,topPackage);
		int index = 1;
		while (GenUtil.inVector(idName, featureNames))
		{
			idName = idRoot + "_" + index;
			index++;
		}
		return idName;
	}
	
	/**
	 * @param featureNames: to be built up into a list of all features of all classes,
	 * in this package and all its sub-packages. Duplicate feature names are allowed.
	 * @param thePackage
	 */
	private static void getFeatureNames(Vector<String> featureNames,EPackage thePackage)
	{
		for (Iterator<EClassifier> ic = thePackage.getEClassifiers().iterator();ic.hasNext();)
		{
			EClassifier ec = ic.next();
			if (ec instanceof EClass)
			{
				EClass aClass = (EClass)ec;
				for (Iterator<EStructuralFeature> it = aClass.getEAllStructuralFeatures().iterator();it.hasNext();)
					featureNames.add(it.next().getName());
			}
		}
		
		for (Iterator<EPackage> ip = thePackage.getESubpackages().iterator();ip.hasNext();)
			getFeatureNames(featureNames,ip.next());
	}
	
	//-----------------------------------------------------------------------------------------------
	//               making mapping sets for the alternate Ecore Serialisation
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * key = qualified class name
	 * value = Vector of XPaths to object mappings, with subsets in sequence "", "s1", "s2",.....
	 */
	private Hashtable<String,Vector<String>> mappingPaths;
	
	/**
	 * usually called _after_ the path for the mapping has been allocated;
	 * but if called before any mappings are made, returns the first subset "".
	 * @param theClass an EClass
	 * @return the subset for the mapping
	 */
	private String getLatestSubset(EClass theClass)
	{
		String subset = "";
		String qualifiedClassName = ModelUtil.getQualifiedClassName(theClass);
		Vector<String> paths = mappingPaths.get(qualifiedClassName);
		if (paths == null) return subset;
		int index = paths.size() -1;
		if (index > 0) subset = "s" + index; // after "", the sequence starts at "s1"
		return subset;
	}
	
	
	/**
	 * @param theClass a class which may (or may not) be the parent of the current class
	 * @param path the path to a node where the current class is represented
	 * @return the subset of the parent class as mapped to the parent node, if it is so mapped;
	 * or null if it is not
	 */
	private String getParentNodeSubset(EClass theClass,String path)
	{
		String innerStep = "";
		StringTokenizer st = new StringTokenizer(path,"/");
		while (st.hasMoreTokens()) innerStep = st.nextToken();
		String parentPath = path.substring(0,path.length() - innerStep.length() - 1);

		String qualifiedClassName = ModelUtil.getQualifiedClassName(theClass);
		Vector<String> paths = mappingPaths.get(qualifiedClassName);
		if (paths == null) return null; // no mappings yet to the parent class - obscure case

		boolean found = false;
		String subset = ""; // return  when i= 0 test below succeeds
		for (int i = 0; i < paths.size();i++)
			if (paths.get(i).equals(parentPath))
			{
				found = true;
				if (i  > 0) subset = "s" + i; // after "", the sequence starts at "s1"				
			}
		if (!found) return null; // 'parent' class has no mapping on the parent node - can occur
		return subset;
	}

	
	
	/**
	 * 
	 * @param topPackage
	 * @param ecoreURI
	 * @param topClass
	 * @param altStructure
	 * @param schemaURI
	 * @param projectName
	 */
	private void makeMappingSets(EPackage topPackage,URI ecoreURI,EClass topClass,
			StructureDefinition altStructure,URI schemaURI, IProject project, String fileNameRoot)
	throws MapperException
	{
		String fileName = fileNameRoot + "_alt.mapper";
		IFolder mappingsFolder = project.getFolder("MappingSets");
		if (!mappingsFolder.exists()) throw new MapperException("No MappingSets folder in project");
		if ((mappingsFolder.findMember(fileName)!= null ) && (mappingsFolder.findMember(fileName).exists())) 
		{
			boolean confirm = WorkBenchUtil.askConfirm("Mapping set '" + fileName + "' already exists", 
					"Do you want to replace the existing mapping set?");
			if (!confirm) return;
		}
		
		String mapperPath = "/" + project.getName() + "/MappingSets/" + fileName;
		URI uri = URI.createURI(mapperPath);
		
		Resource mappingSet = ModelUtil.makeNewMappingSet(uri);
		MappedStructure mappedStructure = (MappedStructure)mappingSet.getContents().get(0);
		mappedStructure.setUMLModelURL(ecoreURI.toString());

		mappedStructure.setStructureURL(schemaURI.toString());
		mappedStructure.setStructureDefinition(altStructure);
		
		completePackageNamespaces(topPackage,mappedStructure.getMappingParameters());

		String topElementName = topPackage.getNsPrefix() + ":" + topClass.getName();
		String topElementType = topClass.getName() + typeSuffix;
		mappedStructure.setTopElementName(topElementName);
		mappedStructure.setTopElementType(topElementType);
		mappedStructure.setRootElement(altStructure.typeStructure(topElementType));
		
		ElementDef rootElement = mappedStructure.getRootElement();
		rootElement.setName(topElementName);
		rootElement.setExpanded(true);
		
		// recursive descent of the tree, making mappings
		mappingPaths  = new Hashtable<String,Vector<String>>();
		String path = "/" + topElementName;
		mapClasses(mappedStructure, rootElement, topPackage, topClass, topClass, altStructure, path);
		
		// for classes with more than one mapped subset, ensure non-containment associations have all mappings
		completeAssociationMappings(rootElement);

		// save the mapping set in the MappingSets folder
		ModelUtil.saveMappingSet(mappingSet);

	}
	
	/**
	 * @param topPackage the top package of a class model
	 * @param gmp GlobalMappingParameters of a mapping set
	 * Ensure that the namespace set of the mapping parameters contains the namespace
	 * of every package in the model
	 */
	private void completePackageNamespaces(EPackage topPackage, GlobalMappingParameters gmp)
	throws MapperException
	{
		Hashtable<String,String> namespaces = new Hashtable<String,String>();
		// note the namespaces already in the mapping set
		for (Iterator<Namespace> in = gmp.getNameSpaces().iterator();in.hasNext();)
		{
			Namespace ns = in.next();
			namespaces.put(ns.getURL(), ns.getPrefix());			
		}
		
		// recursive descent of all packages, adding their namespaces
		addPackageNamespace(topPackage,namespaces,gmp);		
	}
	
	/**
	 * 
	 * @param aPackage
	 * @param namespaces
	 * @param gmp
	 * @throws MapperException
	 * recursive descent of all packages, adding their namespaces to the set.
	 */
	private void addPackageNamespace(EPackage aPackage, Hashtable<String,String> namespaces, GlobalMappingParameters gmp)
	throws MapperException
	{
		String uri = aPackage.getNsURI();
		String prefix = aPackage.getNsPrefix();
		if (uri == null) throw new MapperException("Null namespace URI in package '" + aPackage.getName() + "'");
		if (prefix == null) throw new MapperException("Null namespace prefix in package '" + aPackage.getName() + "'");
		String existingPrefix = namespaces.get(uri);
		if (existingPrefix == null)
		{
			Namespace ns = MapperFactory.eINSTANCE.createNamespace();
			ns.setPrefix(prefix);
			ns.setURL(uri);
			gmp.getNameSpaces().add(ns);
		}
		else if (existingPrefix != null)
		{
			if (!existingPrefix.equals(prefix))
				throw new MapperException("Previous prefix '" + existingPrefix + "' clashes with prefix '"
						 + prefix + "' of package '" + aPackage.getName() + "'");
		}
		
		for (Iterator<EPackage> ip = aPackage.getESubpackages().iterator(); ip.hasNext();)
			addPackageNamespace(ip.next(),namespaces,gmp);
		
	}
	

	
	/**
	 * @param paths Vector of string paths
	 * @param path a path 
	 * @return the number of paths in the Vector that are sub-paths of the other path
	 */
	private int subPaths(Vector<String> paths, String path)
	{
		int count = 0;
		for (Iterator<String> it = paths.iterator();it.hasNext();)
			if (path.startsWith(it.next())) count++;
		return count;
	}
	
	/**
	 * 
	 * @param mappedStructure
	 * @param mappedElement
	 * @param topPackage
	 * @param mappedClass
	 * @param topClass
	 * @param altStructure
	 * @param path
	 * @throws MapperException
	 */
	private void mapClasses(MappedStructure mappedStructure, ElementDef mappedElement, 
			EPackage topPackage, EClass mappedClass, EClass topClass,
			StructureDefinition altStructure, String path)
	throws MapperException
	{
		/* Count the sub-paths of the current path mapped to this class, 
		 * to limit self-nesting depth */
		Vector<String> paths = mappingPaths.get(ModelUtil.getQualifiedClassName(mappedClass));
		if ((paths != null) && (subPaths(paths, path) > maxClassNestingDepth() - 1)) return;
		trace("Map class " + mappedClass.getName() + " at path " + path);
		
		// add a NodeMappingSet to hold all the object mappings and containment association mappings
		NodeMappingSet nodeMappingSet = MapperFactory.eINSTANCE.createNodeMappingSet();
		mappedElement.setNodeMappingSet(nodeMappingSet);
		
		/* Add an object mapping to the class and its concrete subclasses; 
		 * if it has more than one concrete subclass, make object mappings conditional on the value of xsi:type */
		Vector<EClass> subclasses = ModelUtil.getAllConcreteSubClasses(mappedClass);
		for (Iterator<EClass> it = subclasses.iterator(); it.hasNext();)
		{
			EClass subClass = it.next();
			String sqName = ModelUtil.getQualifiedClassName(subClass);
			Vector<String> sPaths = mappingPaths.get(sqName);
			if (sPaths == null) sPaths = new Vector<String>();
			sPaths.add(path);
			mappingPaths.put(sqName,sPaths);
			String subClassSubset = getLatestSubset(subClass);

			String subClassName = subClass.getName();
			String subClassPackageName = subClass.getEPackage().getName();
			String subClassPackagePrefix = subClass.getEPackage().getNsPrefix();
			if (subClassPackageName == null) subClassPackageName= "";
			ObjMapping om = MapperFactory.eINSTANCE.createObjMapping();
			om.setMappedClass(subClassName);
			om.setMappedPackage(subClassPackageName);
			om.setSubset(subClassSubset);
			
			// add mapping conditions if necessary
			if (subclasses.size() > 1)
			{
				ValueCondition vc = MapperFactory.eINSTANCE.createValueCondition();
				vc.setLeftPath("@xsi:type");
				vc.setRightValue(subClassName);
				if (subClassPackageName.length() > 0) vc.setRightValue(subClassPackagePrefix + ":" + subClassName);
				om.getMappingConditions().add(vc);
			}
			
			nodeMappingSet.getObjectMappings().add(om);
			
			// add property mappings for all properties of each subclass
			for (Iterator<EAttribute> ia = subClass.getEAllAttributes().iterator();ia.hasNext();)
			{
				EAttribute att = ia.next();
				if (!att.isDerived())
				{
					String attName = att.getName();
					AttributeDef attDef = mappedElement.getNamedAttribute(attName);
					if (attDef == null) throw new MapperException("Cannot find AttributeDef for property '" 
							+ attName + "' of class '" + subClass.getName() + "' at path " + path);
					
					// add the node mapping set only once to each AttributeDef
					NodeMappingSet nms = attDef.getNodeMappingSet();
					if (nms == null)
					{
						nms = MapperFactory.eINSTANCE.createNodeMappingSet();
						attDef.setNodeMappingSet(nms);					
					}
					
					PropMapping pm = MapperFactory.eINSTANCE.createPropMapping();
					pm.setMappedClass(subClassName);
					pm.setMappedPackage(subClassPackageName);
					pm.setMappedProperty(attName);
					pm.setSubset(subClassSubset);
					nms.getPropertyMappings().add(pm);
					
				} // end of if !derived section
			} // end of iteration over attributes
			
			/* Iterate over all associations of each subclass */
			for (Iterator<EReference> ir = subClass.getEAllReferences().iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				String refName = ref.getName();
				EClassifier target = ref.getEType();
				if (!ref.isDerived())
				{
					if (target instanceof EClass)
					{
						EClass targetClass = (EClass)target;
						// containment relations; take the recursive step to the child elements
						trace("EReference " + subClassName + "." + refName + "." + targetClass.getName());
						if (ref.isContainment())
						{
							ElementDef child = mappedElement.getNamedChildElement(refName);
							if (child == null) System.out.println("Cannot find child element '" 
									+ refName + "' of element '" + mappedElement.getName() + "' at path " + path);
							
							// a child element which has already been encountered in another subclass will have been expanded
							else if ((!child.isExpanded()) && (typeAppearsInSchema(topClass,targetClass)))
							{
								expandNode(child,altStructure);
								String newPath = path + "/" + child.getName();
								// recursive step; depth check for classes that contain themselves is inside the call
								mapClasses(mappedStructure, child, topPackage, targetClass, topClass, altStructure, newPath);
							}
						}  // end of isContainment section

						// non-containment relations; need to treat all target subclasses separately
						else if (!ref.isContainment())
						{
							for (Iterator<EClass> is = ModelUtil.getAllSubClasses(targetClass).iterator();is.hasNext();)
							{
								EClass targetSubClass = is.next();
								// only include associations if the target class is in the instance
								if (appearsInInstance(topClass, targetSubClass))
								{
									/* common association mapping code, for relations that are non- containments at both ends and 
									 * for relations whose EOpposite is a containment */
									AssocMapping am = MapperFactory.eINSTANCE.createAssocMapping();
									// the root node does not have a mapped containment association of the top class, or any other associations
									StringTokenizer steps = new StringTokenizer(path,"/");
									boolean addAssociationMapping = (steps.countTokens() > 1); // reset false in some cases

									// end from the other end class to the current class
									AssocEndMapping end1 = MapperFactory.eINSTANCE.createAssocEndMapping();
									end1.setMappedClass(subClassName);
									end1.setMappedPackage(subClassPackageName);
									end1.setSubset(subClassSubset);
									String roleName = MappableAssociation.NON_NAVIGABLE_ROLE_NAME;
									if (ref.getEOpposite() != null) roleName = ref.getEOpposite().getName();
									end1.setMappedRole(roleName);

									// end from the current class to the other end class
									AssocEndMapping end2 = MapperFactory.eINSTANCE.createAssocEndMapping();
									end2.setMappedClass(targetSubClass.getName());
									String targetPackage = targetSubClass.getEPackage().getName();
									if (targetPackage == null) targetPackage = "";
									end2.setMappedPackage(targetPackage);
									end2.setSubset(getLatestSubset(targetSubClass)); // wrong for self-containment; corrected below
									String inverseRole = refName;
									if (refName == null) inverseRole = MappableAssociation.NON_NAVIGABLE_ROLE_NAME;
									end2.setMappedRole(inverseRole);
									trace("made ends");

									/* every subclass should have at least one EReference whose opposite is a containment; one
									 * of these should be a containment in the next outer class. 
									 * this has a simple association mapping on the Element*/
									if (	(steps.countTokens() > 1) &&  // not on outer node
											(ref.getEOpposite() != null) &&  // EOpposite exists
											(ref.getEOpposite().isContainment())) // EOpposite is containment
									{
										end1.setRequiredForObject(true);
										/* set the paths between parent and child nodes explicitly, so for that for
										 *  self-nesting associations the default shortest path '.' is not allowed */
										end2.setObjectToAssociationPath(mappedElement.getName());
										end2.setAssociationToObjectPath("parent::" + parentNodeName(path));
										
										// correct a subset made before, which might be wrong for a self-containment association
										String correctSubset = getParentNodeSubset(targetSubClass,path);
										if (correctSubset != null)
										{
											end2.setSubset(correctSubset);
											trace("made containment");											
										}
										// a containment in a higher ancestor, not the parent(may occur for nested subsets); add no association mapping
										if (correctSubset == null)
										{
											addAssociationMapping = false;
											trace("found no parent mapping to " + targetSubClass.getName());
										}
									}

									/* for associations that are not containments in either direction, initially make one association 
									 * mapping for each subclass, with a cross-condition. Later make one for each subset of each subclass. */
									else if (steps.countTokens() > 1)
									{
										if (ref.getEOpposite() != null) am.setMultiWay(MultiWay.REDUNDANT); // these associations are represented twice

										AttributeDef attDef = mappedElement.getNamedAttribute(refName);
										if (attDef == null) throw new MapperException("Cannot find AttributeDef for association '" 
												+ refName + "' at path " + path);
										
										/* long cross-paths to pick up all possible nodes 
										 * are set up in a second pass, when all the object mappings exist; 
										 * the default cross-path might be shorter */
																		
										// add the cross-condition on end 2
										CrossCondition cc = MapperFactory.eINSTANCE.createCrossCondition();
										cc.setLeftPath("@" + refName); // XPath to the reference attribute
										cc.setRightPath("@" + idName); // object identifier attribute
										if (ref.getUpperBound() == -1) cc.setTest(ConditionTest.CONTAINSASWORD); // the left value contains the right value
										end2.getMappingConditions().add(cc);
										trace("made long-range");
									} // end of 'EOpposite is is non-containment' section
									
									addEnds(am,end1,end2);									
									if  (addAssociationMapping) nodeMappingSet.getAssociationMappings().add(am);
									
								} // end of 'target class is in instance' section								
							}// end of iteration over target subclasses					
						} // end of 'not containment' section
					} // end of 'target is EClass' section				
				} // end of ' not derived' section
			} // end of iteration over EReferences
		} // end  of iteration over concrete subclasses
	} // end of method
	
	/**
	 * @param path path to this node
	 * @return name of the parent of this node
	 */
	private String parentNodeName(String path)
	{
		String parent = "node()";  // default if you cannot fix it
		StringTokenizer st = new StringTokenizer(path,"/");
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			if (st.hasMoreTokens()) parent = step;
		}
		return parent;
	}
	
	
	/**
	 * 
	 * @param am an association mapping
	 * @param end1 one of its association end mappings
	 * @param end2 the other association end mapping
	 * Attach the two end mappings in the correct way,
	 * depending on the lexical order of the two role names
	 */
	private void addEnds(AssocMapping am, AssocEndMapping end1, AssocEndMapping end2)
	{
		String role1 = end1.getMappedRole();
		String role2 = end2.getMappedRole();
		if (role1.equals(ModelUtil.end1Role(role1, role2)))
		{
			am.setMappedEnd1(end1);			
			am.setMappedEnd2(end2);			
		}
		else
		{
			am.setMappedEnd1(end2);			
			am.setMappedEnd2(end1);			
		}
	}
	
	/**
	 * 
	 * @param child an ElementDef which has not yet been expanded
	 * @param altStructure a tree structure definition
	 * Expand the ElementDef by adding child nodes from the appropriate type
	 * @throws MapperException
	 */
	private void expandNode(ElementDef child,StructureDefinition altStructure)
	throws MapperException
	{
		child.setExpanded(true);
		ElementDef toCopy = altStructure.typeStructure(child.getType());
		if (toCopy == null) throw new MapperException("Cannot find type '" + child.getType() + "'");
		
		// two-stage copy to stop EMF blowing up
		Vector<ElementDef> elDefs = new Vector<ElementDef>();
		for (Iterator<ElementDef> ie = toCopy.getChildElements().iterator();ie.hasNext();)
			elDefs.add(ie.next());
		for (Iterator<ElementDef> ie= elDefs.iterator(); ie.hasNext();)
		    child.getChildElements().add(ie.next());
		
		Vector<AttributeDef> aDefs = new Vector<AttributeDef>();
		for (Iterator<AttributeDef>  ia = toCopy.getAttributeDefs().iterator();ia.hasNext();)
			aDefs.add(ia.next());
		for (Iterator<AttributeDef> ia = aDefs.iterator();ia.hasNext();)
			child.getAttributeDefs().add(ia.next());
	}

	
	/**
	 * When the association mappings were made in the first pass over the 
	 * mapped structure, they were only made to one subset of each target class.
	 * For mappings of non-containment associations, if there is more than one subset 
	 * of the 'long-range' class picked out by the cross-condition, then there has to be
	 * one association mapping for every subset of that class.
	 * 
	 * This method also puts in the long-range paths for associations with cross-conditions,
	 * even for mappings with only one subset.
	 * 
	 * @param anElement the element whose mappings are to be completed;and by recursive
	 * descent, all its descendant elements
	 */
	private void completeAssociationMappings(ElementDef anElement)  throws MapperException
	{
		NodeMappingSet nms = anElement.getNodeMappingSet();
		if (nms != null)
		{
			// collect the list of mappings before you start to modify it
			Vector<AssocMapping> startMappings = new Vector<AssocMapping>();
			for (Iterator<AssocMapping> ia = nms.getAssociationMappings().iterator();ia.hasNext();)
				startMappings.add(ia.next());

			for (Iterator<AssocMapping> ia = startMappings.iterator();ia.hasNext();)
			{
				AssocMapping am = ia.next();
				String longRangeClassName = getLongRangeClassName(am);
				if (longRangeClassName != null)
				{
					Vector<String> paths = mappingPaths.get(longRangeClassName);
					if (paths == null) throw new MapperException("No mapping paths for class '" + longRangeClassName + "'");
					// you need to put in long-range paths, even if the long-range class only one mapped subset
					for (int p = 0; p < paths.size(); p++)
					{
						String subset = "";
						if (p > 0) subset = "s" + p;
						cloneAssociationMapping(nms,subset,am);
					}
				}
			}			
		}
		for (Iterator<ElementDef> ie = anElement.getChildElements().iterator(); ie.hasNext();)
			completeAssociationMappings(ie.next());
	}
	
	/**
	 * 
	 * @param am an association mapping
	 * @return if onde end of this association mapping has a cross condition,
	 * return the qualified name of the class picked out by that condition;
	 * otherwise return null
	 * @throws MapperException
	 */
	private String getLongRangeClassName(AssocMapping am) throws MapperException
	{
		String longRange = null;
		// find the end with a cross-condition , if there is one
		for (int e = 0; e< 2; e++)
		{
			AssocEndMapping end = am.getMappedEnd(e);
			if (end.getCrossConditions().size() > 0) 
				longRange = ModelUtil.getQualifiedClassName(end.getMappedClass(), end.getMappedPackage());			
		}
		return longRange;
	}
	
	/**
	 * 
	 * @param nms a NodeMappingSet
	 * @param subset a subset string of a class mapping
	 * @param am and AssocMapping
	 * make a clone of the association mapping, which may differ only in the subset of 
	 * the long-range class (picked out by a cross-condition).
	 * If this subset differs from the original ,add the mapping to the set.
	 * @throws MapperException
	 */
	private void cloneAssociationMapping(NodeMappingSet nms,String subset,AssocMapping am)
	throws MapperException
	{
		boolean differentSubset = false;
		AssocMapping clone = MapperFactory.eINSTANCE.createAssocMapping();
		clone.setMultiWay(MultiWay.REDUNDANT);
		for (int e = 0; e < 2; e++)
		{
			AssocEndMapping end = am.getMappedEnd(e);
			AssocEndMapping cloneEnd = MapperFactory.eINSTANCE.createAssocEndMapping();
			cloneEnd.setMappedClass(end.getMappedClass());
			cloneEnd.setMappedPackage(end.getMappedPackage());
			cloneEnd.setMappedRole(end.getMappedRole());
			cloneEnd.setSubset(end.getSubset()); // may be reset below
			if (end.getCrossConditions().size() > 0)  
			{
				differentSubset = (!end.getSubset().equals(subset));
				CrossCondition cc = end.getCrossConditions().get(0);
				cloneEnd.setSubset(subset);
				CrossCondition cloneCC = MapperFactory.eINSTANCE.createCrossCondition();
				cloneCC.setLeftPath(cc.getLeftPath());
				cloneCC.setRightPath(cc.getRightPath());
				cloneCC.setTest(cc.getTest());
				cloneEnd.getMappingConditions().add(cloneCC);
				
				/* the long cross paths must be set using the correct subsets for each mapping,
				 * even for existing mappings if no new mapping is being added */
				AssocEndMapping otherEnd = am.getMappedEnd(1-e);
				if (!differentSubset) setLongPaths(end, otherEnd.getQualifiedClassName(),otherEnd.getSubset());
				else setLongPaths(cloneEnd, otherEnd.getQualifiedClassName(),otherEnd.getSubset());
			}
			if (e == 0) clone.setMappedEnd1(cloneEnd); 
			else if (e == 1)clone.setMappedEnd2(cloneEnd); 
		}
		// only add this association mapping if it has a target subset different from the original
		if (differentSubset) nms.getAssociationMappings().add(clone);
	}

	
	/**
	 * @param aem an association end mapping, for the 'long' end that has a cross-condition 
	 * @param qualifiedClassName the class name of the object whose object mapping is on the same node
	 * as this association (end)mapping
	 * @param subset the subset of the object whose object mapping is on the same node
	 * as this association (end)mapping
	 * Set up the cross-paths of the association end mapping to go up to the root and down 
	 * to the appropriate node, so that if the default cross-path is too short, it is not taken
	 */
	private void setLongPaths(AssocEndMapping aem, String qualifiedClassName, String subset)
	throws MapperException
	{
		/* object to association path; the down section of the path leads to the node 
		 * of the object mapping which is the same node as this association mapping is on */
		aem.setObjectToAssociationPath(makeCrossPath(qualifiedClassName, subset));
		
		// association to object path; leads to the object mapping of the target class
		aem.setAssociationToObjectPath(makeCrossPath(aem.getQualifiedClassName(), aem.getSubset()));
	}
	
	/**
	 * @param qualifiedClassName
	 * @param subset
	 * @return a cross path which goes up to the root and down to the set of 
	 * nodes containing object mappings to this class and subset
	 */
	private String makeCrossPath(String qualifiedClassName, String subset)
	throws MapperException
	{
		String downPath = "";
		try{
			Vector<String> paths = mappingPaths.get(qualifiedClassName);
			// the path to subset "" is first in the Vector; then subsets "s1", "s2", etc.
			if (subset.equals("")) downPath = paths.get(0);
			else downPath = paths.get(new Integer(subset.substring(1)).intValue());			
		}
		catch(Exception ex) {throw new MapperException("Failed to calculate cross path for class'" 
				+ qualifiedClassName + " ', subset '" + subset + "'; " + ex.getMessage());}

		// strip off the first '/' of the path and add 'ancestor::' in stead
		return ("ancestor::" + downPath.substring(1));		
	}


}
