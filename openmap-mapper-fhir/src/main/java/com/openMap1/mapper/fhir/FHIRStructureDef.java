package com.openMap1.mapper.fhir;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.w3c.dom.Element;

import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.ExcelReader;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * class to read a FHIR profile and create the appropriate class model and MappedStructure for mapping the FHIR XML onto 
 * some class model.
 * 
 * The FHIR profile may be either a core FHIR resource, or a profile of such a resource.
 * 
 * This class reads a single Profile resource, not a bundle of profiles. The single profile may have structures
 * profiling several different base resources.
 * 
 * The FHIR XML structure created in the MappedStructure is a bundle containing resources of all the types
 * mentioned or referenced in this profile.
 * 
 * @author Robert
 *
 */

public class FHIRStructureDef  implements StructureDefinition{
	
	boolean tracing = false;
	
	/* when this is true, an attribute 'deceased' with two types boolean and dateTime becomes 
	 * two attributes, deceasedboolean and deceaseddateTime, each with min multuplicity 0 */ 
	boolean splitDataTypes = true;
	
	public static String FHIR_NAMESPACE_URI = "http://hl7.org/fhir";
	public static String ATOM_NAMESPACE_URI = "http://www.w3.org/2005/Atom";
	public static String ATOM_NAMESPACE_PREFIX = "a";
	
	// to avoid reading and storing any profile definition more than once
	private Hashtable<String, ElementDef> resourceTypes;
	
	// path to the Structures folder of the mapper project, inside which are FHIR profiles and data types folders
	private String structureFolderPath;
	
	// root of the resource file name, with no '.profile'
	private String resourceFileRoot;
	
	// key = data type name; value = subtree
	private Hashtable<String,ElementDef> datatypeTrees;
	
	// key = data type name; value = class in the ecore model
	private Hashtable<String,EClass> datatypeClasses;
	
	// key = resource name; value = class in the ecore model
	private Hashtable<String,EClass> resourceClasses;
	
	// key = path to component in resource; value = component class
	private Hashtable<String,EClass> componentClasses;
	
	// key  = EReference to a resource class, which has not yet been given a target class; value = resource class name
	private Hashtable<EReference,String> refsToResourceClasses;
	
	// key  = EReference to a component class, which has not yet been given a target class; value = path to component class
	private Hashtable<EReference,String> refsToComponentClasses;
	
	// key = resource name for which the file is missing; value = "1"
	private Hashtable<String,String> missingResources;

	// the complete tree of ElementDefs, rooted at the top <feed> node
	private ElementDef feedTree;
	
	
	// Types of types, which are given as the Type of the root node of any data type tree, which I ignore
	private String[] typeTypes = {"Type","Structure","SharedDefinition"};
	
	// profiled resources defined in the file the user opens, not to be found as core resources
	private Vector<String> resourcesDefinedHere;
	
	// the FHIR Ecore class model
	private EPackage fhirPackage;
	
	// names of fhir packages
	private String[] packageNames = {"feed","resources","components","complexTypes","primitiveTypes"};
	
	// fhir packages
	private EPackage[] fhirPackages = new EPackage[5];
	
	// indexes of FHIR packages
	private static int FEED = 0;
	private static int RESOURCES = 1;
	private static int COMPONENTS = 2;
	private static int COMPLEXTYPES = 3;
	private static int PRIMITIVETYPES = 4;
	
	// top class of the bundle, which has containment relations to all Resource classes
	private EClass atomFeedClass;
	
	// superclass for all Resource classes
	private EClass resourceSuperClass;
	
	// resource reference class
	private EClass resourceReferenceClass;
	
	private MappedStructure ms;
	
	/* wherever we encounter 'Resource(Any)' as a resource reference, ensure that the resources in this 
	 * list (separated by '|') are available */
	private String replaceAnyBy = "List|AllergyIntolerance";
	
	//---------------------------------------------------------------------------------
	//                                  Constructor
	//---------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param profileRoot
	 * @throws MapperException
	 */
	public FHIRStructureDef(Element profileRoot,String filePath, MappedStructure ms)  throws MapperException
	{
		this.ms = ms;

		// checks and initialisation
		findStructureFolderPath(filePath);
		resourceTypes = new Hashtable<String,ElementDef>();
		datatypeTrees = new Hashtable<String,ElementDef>();
		datatypeClasses = new Hashtable<String,EClass>(); 
		resourceClasses = new Hashtable<String,EClass>();
		componentClasses = new Hashtable<String,EClass>();
		refsToResourceClasses = new Hashtable<EReference,String>() ;
		refsToComponentClasses = new Hashtable<EReference,String>() ;
		missingResources = new Hashtable<String,String>() ;
		resourcesDefinedHere = new Vector<String>();
		addPrimitiveDataTypes();

		makeInitialFHIRClassModel();
		
		// make the list of profiled resources defined in this profile
		// checkProfiledResources(profileRoot);
		
		// top ElementDefs defining the bundle
		feedTree = makeBundleElementDefs();

		// read profiles recursively, into a flat structure for the bundle holding instances of all referenced profiles
		readFHIRProfile(profileRoot, true);
		
		// extend recursive trees where data types refer to themselves, or ElementDefs refer to ElementDefs in profiles
		int extendDepth = 1;
		for (int i = 0; i < extendDepth; i++) extendElement(feedTree);
		
		// resolve references to Resource classes and component classes
		checkResourceReferences();
		resolveComponentClassReferences();
		
		// save the class model in the ClassModel folder of the project
		saveFHIRClassModel();
		
		// check that the Ecore model can be mapped onto the Java Reference implementation
		EcoreReferenceBridge bridge = new EcoreReferenceBridge(fhirPackage);
	}

	
	/**
	 * make a list of names of the resources profiles in this file, and check if any of them
	 * are not basic resources
	 * @param profileRoot
	 * @throws MapperException
	 */
	private void checkProfiledResources(Element profileRoot) throws MapperException
	{
		// check all structures defined in this profile
		for (Iterator<Element> it = XMLUtil.namedChildElements(profileRoot, "structure").iterator();it.hasNext();)
		{
			Element structureEl = it.next();
			checkChangedResource(structureEl); // probably useless
			resourcesDefinedHere.add(getResourceName(structureEl));
		}
	}
	
	
	
	//---------------------------------------------------------------------------------
	//                                  Bundle Structure
	//---------------------------------------------------------------------------------
	
	
	/**
	 * Create ElementDefs for the bundle itself (atom feed) outside any entries
	 */
	private ElementDef makeBundleElementDefs()
	{
	    // define the root Element of the bundle
		ElementDef root = addElementDef(null,"feed","feed_type",false, false,false);
		addElementDef(root,"title","title_type",false, false,false);
		addElementDef(root,"id","id_type",false, false,false);
		addElementDef(root,"updated","updated_type",false, false,false);		
		return root;
	}
	
	
	/**
	 * make the subtree of the outer bundle needed to hold the tree structure
	 * for any resource, profiled or not
	 * @param resourceName name of the resource - a core resource name or profile name, with upper case
	 * @return content ElementDef which the top resource ElementDef is to be attached to
	 */
	private ElementDef makeBundleSubtree(String resourceName)
	{
		// elements in the bundle down to the actual resource
		String profileEntryTag = "entry_" + resourceName;
		ElementDef profileEntry = addElementDef(feedTree,profileEntryTag,resourceName,true, true,true);
		addElementDef(profileEntry,"title","title_type",false, false,true);
		addElementDef(profileEntry,"id","id_type",false, false,true);
		addElementDef(profileEntry,"updated","updated_type",false, false,true);		
		addElementDef(profileEntry,"category","category_type",true, true,true);		
		ElementDef contentEl = addElementDef(profileEntry,"content","content_type",true, true,true);
		
		// add annotations to help the wrapper transform restore FHIR tag names
		profileEntry.addAnnotation("TagName", "entry");
		profileEntry.addAnnotation("XPath", ATOM_NAMESPACE_PREFIX + ":category/@term");
		profileEntry.addAnnotation("Value", resourceName);
		
		return contentEl;
	}
	
	
	//---------------------------------------------------------------------------------
	//                                  Profile Structure
	//---------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param resourceName
	 * @throws MapperException
	 */
	private ElementDef getCoreResource(String resourceName, boolean attachToBundle) throws MapperException
	{
		ElementDef rootElDef = resourceTypes.get(resourceName);

		// find the profile definition if it has not been found already
		if (rootElDef == null)
		{			
			// to avid repeat attempts when the file is missing
			resourceTypes.put(resourceName, MapperFactory.eINSTANCE.createElementDef());
			
			// read the Excel source file for the resource and make the structure
			String lowerRes = resourceName.toLowerCase();
			String filePath = structureFolderPath + "profiles\\"  + lowerRes + ".profile.xml";
			Element resourceRoot = XMLUtil.getRootElement(filePath);
			rootElDef = readFHIRProfile(resourceRoot,attachToBundle);	
		}
		return rootElDef;
	}

	
	
	/**
	 * read a profile and attach the ElementDefs to the bundle root ElementDef.
	 * This method calls itself recursively for profiles referred to, but avoiding infinite recursion
	 * There are two main use cases:
	 * 
	 * (a) profileRoot is the root element of a core resource profile, having just one <structure> element defining 
	 * all paths in the resource. This method attaches to root ElementDef for the resource structure to the bundle, 
	 * and returns the root ElementDef.
	 * 
	 * (b) profileRoot is the root of a profile defining one or more profiled resources, each based on a core resource.
	 * In this case, for each <structure> element, it first computes the ElementDef structure of the core resource 
	 * (but does not attach it to the bundle structure), 
	 * then computes a Hashtable of  ElementDefs for all paths which are modified in the profiled structure,
	 * then computes the full profiled structure and attaches it to the bundle. Returns null.
	 * 
	 * @param bundleRootDef
	 * @param profileRoot
	 * @throws MapperException
	 */
	private ElementDef readFHIRProfile(Element profileRoot, boolean attachToBundle)   throws MapperException
	{
		ElementDef resultElDef = null;
		Element structureEl = XMLUtil.firstNamedChild(profileRoot, "structure");
		Element typeEl = XMLUtil.firstNamedChild(structureEl, "type");
		String resourceName = typeEl.getAttribute("value");
		String coreResourceName = resourceName;
		boolean changesResource = false;

		/* make the attachment to the bundle tree now, before those for referenced resources, to get the 
		 * different resource subtrees in a nice order in the mapping set */
		ElementDef contentEl = null;
		if (attachToBundle) contentEl = makeBundleSubtree(resourceName);
		
		trace("making profile '" + resourceName + "'; " + changesResource);
		resourceTypes.put(resourceName, MapperFactory.eINSTANCE.createElementDef());

		Hashtable<String,ElementDef>  profilePaths = pathElementDefs(structureEl);
		trace("made profile paths for '" + resourceName + "'");

		
		// profile defining a core resource
		if (!changesResource)
		{
			resultElDef =  arrangePathsInTree(profilePaths,resourceName);
			resourceTypes.put(resourceName, resultElDef);
		}
		
		else if (changesResource)
		{
			// read the profile file and make the tree for the core resource this is based on
			ElementDef coreResource = getCoreResource(coreResourceName, false);
			// make the tree for the profiled resource by merging with the tree for the core resource
			resultElDef = mergeTrees(profilePaths,coreResource);					
		}
		
		if (attachToBundle) contentEl.getChildElements().add(resultElDef);
		
		addResourceToClassModel(resultElDef);
						
		// return the root of the profile
		return resultElDef;		
	}
	
	
	/**
	 * 
	 * @return
	 */
	private Hashtable<String,ElementDef> pathElementDefs(Element structureEl) throws MapperException
	{
		Hashtable<String,ElementDef>  profilePaths = new Hashtable<String,ElementDef>();
		Vector<Element> elementEls = XMLUtil.namedChildElements(structureEl, "element");
		for (int r = 0; r < elementEls.size(); r++)
		{
			Element elEl = elementEls.get(r);
			String path = XMLUtil.firstNamedChild(elEl, "path").getAttribute("value");
			Element defEl = XMLUtil.firstNamedChild(elEl, "definition");
			
			/* ignore paths beginning with "!" until I understand them,
			 * and ignore paths ending in '.extension' or '.modifierExtension'  */
			if ((!path.startsWith("!")) && (!path.endsWith(".extension")) && (!path.endsWith(".modifierExtension")))
			{
				/* there can be several ElementDefs from one row, from alternative type; 
				 * but this Vector only has size  > 1 if we are splitting types, splitDataTypes = true */
				Vector<ElementDef> elDefs = makeProfileElementDefs(defEl,path,r);
				for (int p = 0; p < elDefs.size(); p++)
				{
					ElementDef elDef = elDefs.get(p);
					String modPath = path;
					if (path.endsWith("[x]")) modPath = path.substring(0,path.length() -3); // strip off final '[x]'
					// if there is more than one type (as we are splitting types) add each type name
					if (elDefs.size() > 1) modPath = modPath + GenUtil.initialUpperCase(elDef.getType()); // never happens if splitDataTypes = false
					String enhancedPath  = makeEnhancedPath(modPath,null);
					if (profilePaths.get(enhancedPath) != null) message ("Duplicated path: " + enhancedPath);
					else profilePaths.put(enhancedPath, elDef);
				}
			}
			
		}
		return profilePaths;
	}
	
	/**
	 * make an enhanced path, which should be unique even in a modifying profile
	 * @param modPath
	 * @param name
	 * @param nameRef
	 * @return
	 */
	private String makeEnhancedPath(String modPath, String nameRef)
	{
		String enhancedPath = modPath;
		// the path always ends with the slice name; 
		if (nameRef != null) enhancedPath = enhancedPath + "$" + nameRef;
		return enhancedPath;
	}
	
	/**
	 * 
	 * @param profilePaths a set of ElementDefs for profile paths
	 * @return the root of the ElementDef tree
	 * @throws MapperException
	 */
	private ElementDef arrangePathsInTree(Hashtable<String,ElementDef>  profilePaths, String resourceName) throws MapperException
	{
		// arrange all ElementDefs in a tree
		ElementDef rootElDef = getRootDef(profilePaths,resourceName); // just a check for now
		for (Enumeration<String> en = profilePaths.keys();en.hasMoreElements();)
		{
			String path = en.nextElement();
			String parentPath = parentPath(path);
			ElementDef elDef = profilePaths.get(path); // one of these resets the root
			if (!parentPath.equals(""))
			{
				ElementDef parent = profilePaths.get(parentPath);
				if (parent == null) message("Element at path '" + path + "' has no parent.");
				else parent.getChildElements().add(elDef);
			}
		}
		return rootElDef;		
	}
	
	/**
	 * 
	 * @param profilePaths ElementDefs for paths where the profile modifies the core resource; 
	 * Hashtable key is the enhanced path, which should be unique
	 * @param coreResource the ElementDef tree for the core resource
	 * @return ElementDef tree for the modified resource
	 */
	private ElementDef mergeTrees(Hashtable<String,ElementDef>  profilePaths, ElementDef coreResource) throws MapperException
	{
		ElementDef profileRoot = getRootDef(profilePaths,"profile modifying " + coreResource.getName()); 
		Hashtable<String,String> uniqueSliceNames = new Hashtable<String,String>();
		extendProfiledTree(profileRoot,coreResource,profilePaths,"",uniqueSliceNames);
		return profileRoot;
	}
	
	/**
	 * recursive extension of the ElementDef tree for a profiled resource
	 * @param profileElDef current node in the profiled resource tree 
	 * @param coreElDef current node in the core resource tree
	 * @param profilePaths ElementDefs at all extended paths in the profiled resource definition
	 * @param slice current slice in the profiled resource, or "" if there is no slice
	 * @throws MapperException
	 */
	private void extendProfiledTree(ElementDef profileElDef,ElementDef coreElDef,
			Hashtable<String,ElementDef> profilePaths, String slice, 
			Hashtable<String,String> uniqueSliceNames) throws MapperException
	{
		// copy child AttributeDefs from the core ElementDef to the profiled ElementDef
		for (Iterator<AttributeDef> ia = coreElDef.getAttributeDefs().iterator();ia.hasNext();)
			profileElDef.getAttributeDefs().add((AttributeDef)ModelUtil.EClone(ia.next(), false));
		
		// find names of any child elements the profiled element already has, so they don't get duplicated
		Vector<String> previousChildNames = new Vector<String>();
		for (Iterator<ElementDef> ic = profileElDef.getChildElements().iterator();ic.hasNext();)
			previousChildNames.add(ic.next().getName());
		
		// all extensions to the profiled resource must have valid paths in the core resource; extend these
		for (Iterator<ElementDef> it = coreElDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef coreChild = it.next();
			if (!GenUtil.inVector(coreChild.getName(), previousChildNames))
			{
				Vector<ElementDef> profileChildren = getProfileSiblings(coreChild,profilePaths,slice);
				
				/* if the profile does not define any nodes at this path of this slice, reuse the core definition -
				 * but not its descendants, which may be redefined by deeper nodes */
				if (profileChildren.size() == 0)
				{
					// copy name, multiplicity, type, but not child ElementDefs and AttributeDefs (deep = false)
					ElementDef profileChild = (ElementDef)ModelUtil.EClone(coreChild,false);
					//carry on extending the child
					extendProfiledTree(profileChild,coreChild,profilePaths,slice,uniqueSliceNames);
					// add the profiled child to its parent
					profileElDef.getChildElements().add(profileChild);
				}
				
				/* the profile redefines the tree at this path, and may split it into slices. */
				boolean isSlice = (profileChildren.size() > 1);
				for (int p = 0; p < profileChildren.size(); p++)
				{
					ElementDef profileChild = profileChildren.get(p);
					checkTrueConstraint(coreChild,profileChild,isSlice,uniqueSliceNames);
					if (profileChild.getAnnotation("forbidden") == null)
					{
						String nextSlice = slice;
						if (isSlice) nextSlice = profileChild.getName();
						//carry on extending the child
						extendProfiledTree(profileChild,coreChild,profilePaths,nextSlice,uniqueSliceNames);
						// add the profiled child to its parent
						profileElDef.getChildElements().add(profileChild);					
					}
				}
			}
		}
	}
	
	/**
	 * check that an element defined in a profile is a valid constraint of the corresponding element in 
	 * the core resource it is based on.
	 * @param coreEl
	 * @param profileEl
	 * @throws MapperException
	 */
	private void checkTrueConstraint(ElementDef coreEl, ElementDef profileEl, 
			boolean isSlice,Hashtable<String,String> uniqueSliceNames) throws MapperException
	{
		if (isSlice) // checks of slices
		{
			// check that any slice element has a different name from the core element
			if (coreEl.getName().equals(profileEl.getName())) 
				throw new MapperException("A slice of element '" + coreEl.getName() + "' has not been renamed");
			// check that slice names are unique across the profile
			if (uniqueSliceNames.get(profileEl.getName()) != null)
				throw new MapperException("Slice name '" + profileEl.getName() + "' is not unique");
			uniqueSliceNames.put(profileEl.getName(), "1");
		}
		
		// checks of multiplicity
		if ((profileEl.getMinMultiplicity() == MinMult.ZERO) && (coreEl.getMinMultiplicity() == MinMult.ONE))
			throw new MapperException("Profile element '" + profileEl.getName() + "' relaxes minimum multiplicity");
		if ((profileEl.getMaxMultiplicity() == MaxMult.UNBOUNDED) && (coreEl.getMaxMultiplicity() == MaxMult.ONE))
			throw new MapperException("Profile element '" + profileEl.getName() + "' relaxes maximum multiplicity");
		
		// checks of resource types
		
	}

	
	/**
	 * 
	 * @param coreChild
	 * @param profilePaths
	 * @return
	 */
	Vector<ElementDef>  getProfileSiblings(ElementDef coreChild, Hashtable<String,ElementDef> profilePaths, String slice)
	{
		Vector<ElementDef> profileChildren = new Vector<ElementDef>();
		String slashPath = coreChild.getPath();
		StringTokenizer st = new StringTokenizer(slashPath,"/");
		int depth = st.countTokens();
		String path = "";
		while (st.hasMoreTokens())
		{
			String step  = st.nextToken();
			if (path.equals("")) path = step;
			else path = path + "." + step;
		}
		
		for (Enumeration<String> en = profilePaths.keys();en.hasMoreElements();)
		{
			String profiledPath = en.nextElement();
			int profiledDepth = new StringTokenizer(profiledPath,".").countTokens();
			if ((profiledPath.startsWith(path)) && (profiledDepth == depth) && (profiledPath.endsWith(slice)))
				profileChildren.add(profilePaths.get(profiledPath));
		}
		return profileChildren;
	}


	
	/**
	 * 
	 * @param profilePaths
	 * @return
	 * @throws MapperException
	 */
	ElementDef getRootDef(Hashtable<String,ElementDef>  profilePaths, String resourceName) throws MapperException
	{
		ElementDef root = null;
		int found = 0;
		for (Enumeration<String> en = profilePaths.keys();en.hasMoreElements();)
		{
			String path = en.nextElement();
			if (new StringTokenizer(path,".").countTokens() == 1) 
			{
				found++;
				root = profilePaths.get(path);
			}
		}
		if (found != 1) throw new MapperException("Profile for resource '" + resourceName + "' has " + found + " elements with path length 1");
		return root;
	}
	
	/**
	 * make one or more ElementDefs corresponding to the profile element.
	 * Make only one ElementDef if the profile element has zero or one types in its definition.
	 * If there is more than one type, the raw ElementDef name must end in '[x]' (eg 'value[x]')
	 * and the computed ElementDef names then end in the codes of the types (eg 'valuePeriod', 'valueString')
	 * 
	 * @param profileEl
	 * @return
	 * @throws MapperException
	 */
	private Vector<ElementDef> makeProfileElementDefs(Element defEl, String path, int row)  throws MapperException
	{
		Vector<ElementDef> els = new Vector<ElementDef>();
		
		String changedName = null;

		Vector<String> types = new Vector<String>();
		Vector<Element> typeEls = XMLUtil.namedChildElements(defEl, "type");
		/* typeEls can have size > 1 for e.g deceased[x] = boolean or dateTime,
		 * and can have size 0 for the root of a component class */
		for (int  t = 0; t < typeEls.size(); t++)
		{
			Element typeEl = typeEls.get(t);
			Element codeEl = XMLUtil.firstNamedChild(typeEl, "code");
			if (codeEl == null) throw new MapperException("No type code at path " + path);
			String typeString = codeEl.getAttribute("value");
			types.add(typeString);
		}
		
		String binding = "";
		Element bindingEl = XMLUtil.firstNamedChild(defEl, "binding");
		if (bindingEl != null)
		{
			binding = XMLUtil.firstNamedChild(bindingEl, "name").getAttribute("value");
		}

		// make at least one ElementDef, even if there are no types
		int numberOfEls = 1;
		if (splitDataTypes) numberOfEls = Math.max(1, types.size());

		for (int i = 0; i < numberOfEls; i++)
		{
			ElementDef elDef = MapperFactory.eINSTANCE.createElementDef();
			elDef.setDescription(XMLUtil.firstNamedChild(defEl, "short").getAttribute("value"));
			
			String min = XMLUtil.firstNamedChild(defEl, "min").getAttribute("value");
			String max = XMLUtil.firstNamedChild(defEl, "max").getAttribute("value");

			elDef.setMinMultiplicity(MinMult.ONE);
			if (min.equals("0")) elDef.setMinMultiplicity(MinMult.ZERO);
			if (numberOfEls > 1)  elDef.setMinMultiplicity(MinMult.ZERO);

			elDef.setMaxMultiplicity(MaxMult.ONE);
			if (max.equals("*")) elDef.setMaxMultiplicity(MaxMult.UNBOUNDED);
			if (max.equals("0")) elDef.addAnnotation("forbidden", "yes");

			String elName = getNameFromPath(path);
			if (changedName != null) 
			{
				// annotations for wrapper transform
				elDef.addAnnotation("TagName", elName);
				// do not yet know how to add annotations for XPath and value
				elName = changedName;
			}

			if (types.size() > 0)
			{
				String code = types.get(i);
				// type is a reference to one or more resource types
				if (code.startsWith("Resource")) handleResources(code,elDef,true);
				// type is a reference to a class in this or another resource
				else if (code.startsWith("@")) handleWithinResourceRef(code,elDef);
				else if (code.startsWith("=")) {} // renaming a component class; do nothing yet
				// type is a data type
				else 
				{
					// if there is more than one type, and we are not splitting them, summarise them by the superclass 'Type' of primitive types
					if ((types.size() > 1) && (!splitDataTypes)) code = "Type";
					handleDataType(code,elDef,path,binding);
				}

				elDef.setType(code);
				if (types.size() > 1)
				{
					if (!elName.endsWith("[x]")) throw new MapperException("Node with multiple types at path " + path + " not ending in '[x]'");
					elName = elName.substring(0,elName.length()-3);
					if (splitDataTypes) elName = elName + GenUtil.initialUpperCase(code);
				}
			}

			elDef.setName(elName);
			els.add(elDef);
		}
		
		return els;
		
	}
	
	/**
	 * @param code begins with '@' followed by a resource name and a path within the resource
	 * @param elDef
	 */
	private void handleWithinResourceRef(String code,ElementDef elDef) throws MapperException
	{
		// set the type of the ElementDef so it can be given the correct subtree on a later pass
		elDef.setType(code);
		
		// extract the profile name from the code
		StringTokenizer st = new StringTokenizer(code.substring(1),".");
		String resourceName = st.nextToken();
		
		// get the basic resource structure if it has not been got already
		getCoreResource(resourceName,true);
		
	}
	

	//------------------------------------------------------------------------------------------------------
	//                               References to other resources
	//------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param resourceTypeString
	 * @param elDef
	 */
	private void handleResources(String resourceTypeString, ElementDef elDef, boolean makeResources) throws MapperException
	{
		// do not do anything in the case resourceTypeString = 'Resource', where the type of resource is undefined
		if (resourceTypeString.startsWith("Resource("))
		{
			// add the structure for a reference to a resource 
			ElementDef typeEl = addElementDef(elDef, "type", "TypeOfResource",false,false,false); 
			typeEl.setDescription(resourceTypeString);
			addValueAttribute(typeEl, "ResourceTypeName", "name of the resource type referred to");
			
			ElementDef referenceEl = addElementDef(elDef, "url", "URLOfResource",false,false,false); 
			addValueAttribute(referenceEl, "ResourceURL", "URL of the resource instance referred to");
			
			ElementDef displayElEl = addElementDef(elDef, "display", "TextOfResource",true,false,false); 
			addValueAttribute(displayElEl, "ResourceURL", "Summary display text of the resource instance referred to");
			
			// find all the alternate resource types allowed
			String resourcesRequired = resourceTypeString;
			// replace 'Any' by some resources that may be required, such as 'List'
			if (resourcesRequired.equals("Resource(Any)")) resourcesRequired = "Resource(" + replaceAnyBy + ")";
			StringTokenizer st = new StringTokenizer(resourcesRequired.substring("Resource".length()),"|()");
			if (makeResources) while (st.hasMoreTokens())
			{
				String resourceName = st.nextToken();
				// if this is a profiled resource defined in this profile file, do not look for it
				if (!GenUtil.inVector(resourceName, resourcesDefinedHere))
				{
					try {getCoreResource(resourceName,true);}
					catch(MapperException ex) 
					{
						GenUtil.showMessage("Missing Profile: ", ex.getMessage());
						missingResources.put(resourceName, "1");
					}					
				}
				// for each alternate resource type, add the definition if it has not been added already
			}
		}
	}

	//------------------------------------------------------------------------------------------------------
	//                               Data type structure definitions
	//------------------------------------------------------------------------------------------------------
	
	/**
	 * primitive data types, not defined in files
	 */
	private void addPrimitiveDataTypes()
	{
		
		for (int i = 0; i < PrimitiveTypes.PRIMITIVETYPES.length;i++)
		{
			String type = PrimitiveTypes.PRIMITIVETYPES[i];
			ElementDef elDef = MapperFactory.eINSTANCE.createElementDef();
			addValueAttribute(elDef, type,"");
			datatypeTrees.put(type,elDef);			
		}
	}
	
	/**
	 * attach the appropriate data type definition to an ElementDef.
	 * Do not attach nested references to the same data type definition yet.
	 * (but handle nested references to other data types)
	 * @param code
	 * @param elDef
	 */
	private void handleDataType(String typeName, ElementDef elDef, String path, String binding) throws MapperException
	{
		if (!typeName.equals("Type")) // do nothing for the superclass 'Type' (used to summarise choices of primitive types)
		{
			//read and store the data type tree if it has not been read already
			ElementDef dataTypeElDef = datatypeTrees.get(typeName);
			if (dataTypeElDef == null) try
			{
				// put a temporary element in the datatypes table to stop recursion of the same data type
				ElementDef temp = MapperFactory.eINSTANCE.createElementDef();
				temp.setName("placeholder"); // name has no effect
				temp.setType(typeName);
				
				datatypeTrees.put(typeName, temp);
				
				 // find the Excel source file and make the data type tree
				String dataTypePath = structureFolderPath + "datatypes\\" + typeName.toLowerCase() + ".xml";
				Element datatypeRoot = XMLUtil.getRootElement(dataTypePath); // throws a MapperException if there are problems
				dataTypeElDef = getDataTypeTree(datatypeRoot,typeName);
				datatypeTrees.put(typeName, dataTypeElDef);
			}
			catch (MapperException ex) {System.out.println("Error in data type " + typeName + " at path " + path + ": " + ex.getMessage());}
			
			// copy a clone of the data type tree onto the element
			String context = "data type " + typeName + " on ElementDef " + elDef.getName();
			if (dataTypeElDef != null) copyElChildNodes(elDef, dataTypeElDef,context,binding);
		}
	}

	
	
	/**
	 * define the subtree for a complex data type
	 * @param ExcelFileRoot
	 * @param typeName
	 * @return
	 * @throws MapperException
	 */
	private ElementDef getDataTypeTree(Element ExcelFileRoot, String typeName) throws MapperException
	{
		ExcelReader dTypeBook = new ExcelReader(ExcelFileRoot);
		String elSheet = "Data Elements";
		Hashtable<String,ElementDef> typePaths = new Hashtable<String,ElementDef>();
		ElementDef root = null;
		
		if (!dTypeBook.hasWorkSheet(elSheet))  throw new MapperException("No data elements worksheet for data type " + typeName);

		int nRows = dTypeBook.rowCount(elSheet);
		if (nRows < 2) throw new MapperException("Only " + nRows + " rows defining data type " + typeName);
		// read all rows and store an Element definition for each path
		for (int r = 1; r < nRows; r++)
		{
			String path = dTypeBook.getValue(elSheet, r, "Element",true);

			// eliminate any rogue rows which do not have a valid element name
			if (path.startsWith(typeName))
			{
				String name = getNameFromPath(path);

				boolean isOptional = false;
				boolean isMultiple = false;
				String cardinality = dTypeBook.getValue(elSheet, r, "Card.",true);
				if (cardinality.startsWith("0")) isOptional = true;
				if (cardinality.endsWith("*")) isMultiple = true;

				String type = dTypeBook.getValue(elSheet, r, "Type",true);
				String shortDesc = dTypeBook.getValue(elSheet, r, "Short Name",true);
				String definition = dTypeBook.getValue(elSheet, r, "Definition",true);
				// some data type sheets do not have a column 'Binding'
				String binding = dTypeBook.getValue(elSheet, r, "Binding",false);
				
				if (!cardinality.endsWith("0"))
				{
					ElementDef elDef = MapperFactory.eINSTANCE.createElementDef();
					elDef.setName(name);
					elDef.setMinMultiplicity(MinMult.ONE);
					if (isOptional) elDef.setMinMultiplicity(MinMult.ZERO);
					elDef.setMaxMultiplicity(MaxMult.ONE);
					if (isMultiple) elDef.setMaxMultiplicity(MaxMult.UNBOUNDED);
					elDef.setDescription(definition);
					if ((binding != null) && (!binding.equals(""))) elDef.addAnnotation("Binding", binding);
					typePaths.put(path, elDef);
					
					// element has a simple type; give it a 'value' attribute
					if (type.equals(type.toLowerCase())) addValueAttribute(elDef, type,shortDesc);

					// element type is  'Type' 'Structure' 'SharedDefinition'  or a reference to another data type or Resource
					else
					{
						elDef.setType(type); // (setting the type of the root node of a data type has no effect)

						// root node of any type is marked with the 'type of the type'  - ignore
						if (GenUtil.inArray(type, typeTypes)) {} 

						/* a data type can refer to a Resource; but do not make the resource (assume it will be made other than
						 * in this data type) because when you do, it may refer to this datatype which is currently a stub,
						 * and the stub data type will then get put in the Ecore class model. */
						else if (type.startsWith("Resource")) 
						{
							System.out.println("Data type " + typeName + " refers to " + type);
							handleResources(type, elDef,false); // false = do not make the resource
						}

						// reference to another data type; if this is the same type as is being handled already, it will be a stub
						else 
						{
							String fullPath = path + " in data type " + typeName;
							// elDef has already been marked with any binding, so binding = "" is OK
							handleDataType(type, elDef, fullPath,"");							
						}
					}
				} // end of if (!cardinality.endsWith("0"))
				
			} // end of if (path.startsWith(typeName))
		} // end of loop over rows in the worksheet
		
		// arrange all rows in a tree
		for (Enumeration<String> en = typePaths.keys(); en.hasMoreElements();)
		{
			String path = en.nextElement();
			ElementDef node = typePaths.get(path);
			String parentPath = parentPath(path);
			if (parentPath.equals("")) root = node;
			else
			{
				ElementDef parent = typePaths.get(parentPath);
				if (parent == null) throw new MapperException("cannot find data type parent at path " + path);
				parent.getChildElements().add(node);
			}
		}		
		return root;
	}
	
	
	
	//---------------------------------------------------------------------------------
	//                         Extending ElemenDef trees where they self-reference
	//---------------------------------------------------------------------------------
	
	
	/**
	 * recursive descent of the ElementDef tree, looking for ElementDefs with no child elements or attributes
	 * @param elDef
	 */
	private void extendElement(ElementDef elDef) throws MapperException
	{
		String context =  "extending for data type self-reference at " + elDef.getPath();
		int depth = new StringTokenizer(elDef.getPath(),"/").countTokens();
		boolean atomElement = ((elDef.getName() != null) && (elDef.getName().startsWith(ATOM_NAMESPACE_PREFIX)));

		if ((elDef.getChildElements().size() == 0) 
				&& (elDef.getAttributeDefs().size() == 0)
				&& (!atomElement)
				&& (depth > 2))
		{
			String type = elDef.getType();
			// reference to a subtree within some resource
			if (type.startsWith("@"))
			{
				String elementPath = type.substring(1);
				StringTokenizer st = new StringTokenizer(elementPath,".");
				String resourceName = st.nextToken();
				ElementDef subtree = getResourceSubtree(resourceName,elementPath);
				copyElChildNodes(elDef, subtree, "extending for profile reference",""); // no binding
			}
			
			// data type referring recursively to itself
			else if ((!type.startsWith("Resource")) && (!type.endsWith("Resource")))
			{
				ElementDef typeSubtree = datatypeTrees.get(type);
				if (typeSubtree == null) 
					GenUtil.showMessage("Missing data type","Cannot find tree for data type '" + type + "' at path " + elDef.getPath());
				else copyElChildNodes(elDef, typeSubtree,context,""); // no binding
			}
		}
		
		// recurse to all child nodes
		else for(Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();) extendElement(it.next());
	}
	
	/**
	 * 
	 * @param resourceName
	 * @param elementPath
	 * @return
	 * @throws MapperException
	 */
	private ElementDef getResourceSubtree(String resourceName, String elementPath) throws MapperException
	{
		String prefix = ATOM_NAMESPACE_PREFIX + ":";
		// make a path string like "a:entry_Document.a:content"
		String pathToResource = prefix + "entry_" + resourceName + "." + prefix + "content";
		ElementDef entry = ModelUtil.getDescendantElementDef(feedTree, pathToResource);
		if (entry == null) throw new MapperException("Cannot find structure tree for resource " + resourceName);
		ElementDef subtree = ModelUtil.getDescendantElementDef(entry, elementPath);
		if (subtree == null) throw new MapperException("Cannot find resource subtree at path " + elementPath);
		return subtree;
	}

	
	//---------------------------------------------------------------------------------
	//                         Making the Ecore class model
	//---------------------------------------------------------------------------------
	
	String[] feedAttributes = {"authorName","authorUri","id","title","updated"};
	
	
	/**
	 * make the initial FHIR class model, to which the resource classes will be added
	 */
	private void makeInitialFHIRClassModel() throws MapperException
	{
		// the top FHIR package
		fhirPackage = EcoreFactory.eINSTANCE.createEPackage();
		fhirPackage.setName("fhir");
		// URI and a prefix might be needed for EMF code generation
		fhirPackage.setNsPrefix("f");
		fhirPackage.setNsURI("com.OpenMap1.fhir"); 
		// ensure this class model will be viewed like an RMIM in the class model view
		ModelUtil.addMIFAnnotation(fhirPackage, "RMIM", "true");
		
		// packages containing classes
		// String[] packageNames = {"feed","resources","components","complexTypes","primitiveTypes"};
		for (int p = 0; p < packageNames.length; p++)
		{
			fhirPackages[p] = EcoreFactory.eINSTANCE.createEPackage();
			fhirPackages[p].setName(packageNames[p]);
			fhirPackage.getESubpackages().add(fhirPackages[p]);
		}
		
		/* make an entry class 'AtomFeed' which will have containment associations to all 
		 * resource classes, for display in the Class Model View */
		atomFeedClass = EcoreFactory.eINSTANCE.createEClass();
		atomFeedClass.setName("AtomFeed");
		// entry annotation to make it the tree root in the class model view
		ModelUtil.addMIFAnnotation(atomFeedClass, "entry", "true");
		fhirPackages[FEED].getEClassifiers().add(atomFeedClass);
		
		// attributes of the top AtomFeed object
		for (int i = 0; i < feedAttributes.length;i++)
		{
			EAttribute feedAtt = EcoreFactory.eINSTANCE.createEAttribute();
			feedAtt.setName(feedAttributes[i]);
			feedAtt.setLowerBound(1);
			feedAtt.setEType(PrimitiveTypes.attributeType("string")); // type will be EString
			ModelUtil.addMIFAnnotation(feedAtt, "PrimitiveType", "string");
			atomFeedClass.getEStructuralFeatures().add(feedAtt);
		}
		
		// Resource superclass for all resources
		resourceSuperClass = EcoreFactory.eINSTANCE.createEClass();
		resourceSuperClass.setName("Resource");
		ModelUtil.addMIFAnnotation(resourceSuperClass, "type", "Resource");
		
		// add an attribute 'fhir_id' to Resource which is not in the FHIR reference model (will be inherited by all resources)
		EAttribute fhir_id_att = EcoreFactory.eINSTANCE.createEAttribute();
		fhir_id_att.setName("fhir_id");
		fhir_id_att.setEType(PrimitiveTypes.attributeType("string")); // type will be EString
		resourceSuperClass.getEStructuralFeatures().add(fhir_id_att);
		
		// put class Resource in the Resources package
		fhirPackages[RESOURCES].getEClassifiers().add(resourceSuperClass);
		resourceClasses.put("Resource", resourceSuperClass);
		
		// ResourceReference Class in complexDataTypes package
		resourceReferenceClass = EcoreFactory.eINSTANCE.createEClass();
		resourceReferenceClass.setName("ResourceReference");
		ModelUtil.addMIFAnnotation(resourceReferenceClass, "type", "ComplexDataType");
		
		// add properties of ResourceReference class - note no 'type' property
		String[] resourceRefAtts = {"reference","display"};
		for (int a = 0; a < resourceRefAtts.length; a++)
		{
			EAttribute att = EcoreFactory.eINSTANCE.createEAttribute();
			att.setName(resourceRefAtts[a]);
			att.setEType(PrimitiveTypes.attributeType("string")); // type will be EString
			ModelUtil.addMIFAnnotation(att, "PrimitiveType", "string");
			resourceReferenceClass.getEStructuralFeatures().add(att);
		}
		
		// put class ResourceReference in the complexDataTypes package
		fhirPackages[COMPLEXTYPES].getEClassifiers().add(resourceReferenceClass);
	}

	
	/**
	 * called every time a structure tree for a resource is made
	 * @param resultElDef subtree of the mapped structure tree for one resource
	 */
	private void addResourceToClassModel(ElementDef resultElDef)  throws MapperException
	{
		// top ElementDef of the tree is always a resource; make a class for it
		EClass resourceClass = EcoreFactory.eINSTANCE.createEClass();
		String resourceName = resultElDef.getName();
		resourceClass.setName(resourceName);
		ModelUtil.addMIFAnnotation(resourceClass, "type", "Resource");
		resourceClass.getESuperTypes().add(resourceSuperClass);
		fhirPackages[RESOURCES].getEClassifiers().add(resourceClass);
		resourceClasses.put(resourceName, resourceClass);

		// reach it by a containment relation from the top bundle class
		EReference resourceRef = EcoreFactory.eINSTANCE.createEReference();
		resourceRef.setName(resourceName.toLowerCase());
		resourceRef.setEType(resourceClass);
		resourceRef.setContainment(true);
		resourceRef.setLowerBound(0);
		resourceRef.setUpperBound(-1);
		atomFeedClass.getEStructuralFeatures().add(resourceRef);
		
		// recursive descent of the ElementDef tree
		String path = resourceName;
		for (Iterator<ElementDef> it = resultElDef.getChildElements().iterator();it.hasNext();)
			addNodeToClassModel(it.next(),resourceClass,resourceClass,path);
	}
	
	/**
	 * recurse down the ElementDef tree, adding to the class model
	 * @param elDef
	 * @param currentClass
	 */
	private void addNodeToClassModel(ElementDef elDef,EClass resourceClass, EClass currentClass,String path) throws MapperException
	{

		String name = elDef.getName();
		String nextPath = path + "." + name;
		int childEls = elDef.getChildElements().size();
		EClass nextClass = currentClass;
		boolean carryOnDownTree = false;

		// sometimes only the value attribute child of a leaf element has a type
		String type = elDef.getType();
		if (type == null) type = "";
		if ((type.equals("")) && (childEls == 0))
		{
			AttributeDef att = elDef.getNamedAttribute("value");
			if ((att != null) && (att.getType() != null)) type = att.getType();
		}
		
		boolean isResourceRoot = (type.equals("Resource"));
		boolean isAnyTypeClass = (type.equals("*"));
		boolean isSuperTypeClass = (type.equals("Type"));
		boolean isRefToComponentClass = (type.startsWith("@"));
		boolean isComponentClass = ((childEls > 0) && ((type.equals(""))||(type.startsWith("="))));
		boolean isResourceRef = (type.startsWith("Resource("));
		boolean isPrimitiveType = ((childEls == 0) && (PrimitiveTypes.isPrimitiveType(type)));
		boolean isComplexType = ((!isResourceRoot) && (!isSuperTypeClass) && (!isComponentClass) && (!isResourceRef) 
				&& (type.length() > 1) && (type.equals(GenUtil.initialUpperCase(type))));
		
		if (isResourceRoot) {}
		else if (isAnyTypeClass)
		{
			message("Cannot yet handle type '*' at " + elDef.getPath());
		}
		
		else if (isSuperTypeClass)
		{
			trace("Nothing to do for superclass 'Type' at " + elDef.getPath());
		}
		
		// EReference from the current class to a component class, in this or another resource; cannot yet set its target
		else if (isRefToComponentClass)
		{
			// add the named EReference to the current class, with no target class yet
			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setName(name);
			
			// this is intended to show the component class subtree in the class model view
			ref.setContainment(true);
			setMultiplicities(ref,elDef);
			currentClass.getEStructuralFeatures().add(ref);
			
			// record the path to the component class which will be the target of the EReference
			String pathToComponentClass = type.substring(1);
			refsToComponentClasses.put(ref, pathToComponentClass);
			
			// there is no further tree to descend
		}
		
		// make a new component class in this resource, and attach it to the resource class
		else if (isComponentClass)
		{
			// naming convention for component classes as in the Java reference implementation
			String nameStart = currentClass.getName();
			// chained component classes - do not repeat 'Component' in the name
			if (nameStart.endsWith("Component"))  nameStart = nameStart.substring(0,nameStart.length() - "Component".length());
			String componentClassName = nameStart + GenUtil.initialUpperCase(name) + "Component";
			if (type.startsWith("=")) componentClassName = type.substring(1) + "Component";
			nextClass = EcoreFactory.eINSTANCE.createEClass();
			nextClass.setName(componentClassName);
			ModelUtil.addMIFAnnotation(nextClass, "type", "Component");
			ModelUtil.addMIFAnnotation(nextClass, "inResource", resourceClass.getName());
			componentClasses.put(nextPath,nextClass);
			fhirPackages[COMPONENTS].getEClassifiers().add(nextClass);
			
			/* containment relation from the current class, which is usually a resource class
			 * but occasionally another component class */
			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setName(name);
			ref.setContainment(true);
			ref.setEType(nextClass);
			setMultiplicities(ref,elDef);
			currentClass.getEStructuralFeatures().add(ref);
			carryOnDownTree = true;
		}
		
		// reference to a resource
		else if (isResourceRef)
		{
			// make a containment association to the ResourceReference Class
			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setContainment(true);
			setMultiplicities(ref,elDef);
			ref.setName(name);
			currentClass.getEStructuralFeatures().add(ref);
			ref.setEType(resourceReferenceClass);

			// note the Resource classes referred to, so they can be added to the ecore model (? necessary?)
			String resourceNames = type.substring("Resource(".length());
			resourceNames = resourceNames.substring(0,resourceNames.length() -1); // remove final ')'
			ModelUtil.addMIFAnnotation(ref, "ResourceTypes", resourceNames);
			StringTokenizer st = new StringTokenizer(resourceNames,"|");
			while (st.hasMoreTokens())
			{
				String resourceName = st.nextToken();
				if (!resourceName.equalsIgnoreCase("Any")) refsToResourceClasses.put(ref, resourceName);
			}
				
		}
		
		// leaf nodes with primitive data types
		else if (isPrimitiveType)
		{
			// repeats allowed, so make an association to a primitive type class
			if (elDef.getMaxMultiplicity() == MaxMult.UNBOUNDED) 
			{
				String className = PrimitiveTypes.referenceClassName(type);
				EClass primitiveTypeClass = datatypeClasses.get(className);
				if (primitiveTypeClass == null) primitiveTypeClass = makePrimitiveTypeClass(type);

				EReference primitiveDataTypeRef = EcoreFactory.eINSTANCE.createEReference();
				primitiveDataTypeRef.setName(name);
				primitiveDataTypeRef.setEType(primitiveTypeClass);
				// attach the primitive data class to the current class, for the class model view tree
				primitiveDataTypeRef.setContainment(true);
				setMultiplicities(primitiveDataTypeRef,elDef);
				addBindingAnnotation(elDef,primitiveDataTypeRef);
				currentClass.getEStructuralFeatures().add(primitiveDataTypeRef);				
			}
			// no repeats allowed, so make it a direct EAttribute
			else
			{
				EAttribute theAtt = EcoreFactory.eINSTANCE.createEAttribute();
				theAtt.setName(elDef.getName());
				theAtt.setEType(PrimitiveTypes.attributeType(type));
				ModelUtil.addMIFAnnotation(theAtt, "PrimitiveType", type);
				setMultiplicities(theAtt,elDef);
				addBindingAnnotation(elDef,theAtt);
				currentClass.getEStructuralFeatures().add(theAtt);				
			}
		}
		
		else if (isComplexType)
		{
			// make the complex type EClass if it has not been made already, but do not attach it (store it in a Hashtable)
			EClass complexTypeClass = datatypeClasses.get(type);
			if (complexTypeClass == null)
			{
				complexTypeClass = EcoreFactory.eINSTANCE.createEClass();
				complexTypeClass.setName(type);
				ModelUtil.addMIFAnnotation(complexTypeClass, "type", "ComplexDataType");
				fhirPackages[COMPLEXTYPES].getEClassifiers().add(complexTypeClass);
				// make the structure of the complex type class
				String dataTypePath = type;
				// do not use elDef as root of the data type tree, as it is occasionally empty
				ElementDef dataTypeRoot = datatypeTrees.get(type);
				if (dataTypeRoot == null) throw new MapperException("No complex data type " + type);
				for (Iterator<ElementDef> it = dataTypeRoot.getChildElements().iterator();it.hasNext();)
					addNodeToClassModel(it.next(),complexTypeClass,complexTypeClass,dataTypePath);
				datatypeClasses.put(type, complexTypeClass);
			}
			
			// make the association to the complex type class
			EReference complexDataTypeRef = EcoreFactory.eINSTANCE.createEReference();
			complexDataTypeRef.setName(name);
			complexDataTypeRef.setEType(complexTypeClass);
			// so the data type tree is attached to the current class tree in the class model view
			complexDataTypeRef.setContainment(true);
			setMultiplicities(complexDataTypeRef,elDef);
			addBindingAnnotation(elDef,complexDataTypeRef);
			currentClass.getEStructuralFeatures().add(complexDataTypeRef);
		}
		
		else
		{
			System.out.println("Unhandled type '" + type + "' at path " + elDef.getPath());
		}
		
		// recursive step
		if (carryOnDownTree) for (Iterator<ElementDef> it = elDef.getChildElements().iterator();it.hasNext();)
			addNodeToClassModel(it.next(),resourceClass,nextClass,nextPath);
	}
	
	/**
	 * transfer a binding annotation from the Mapped Structure to the Ecore class model
	 * @param elDef
	 * @param ref
	 */
	private void addBindingAnnotation(ElementDef elDef, EStructuralFeature feat)
	{
		String binding = elDef.getAnnotation("Binding");
		if ((binding != null) && (!binding.equals(""))) ModelUtil.addMIFAnnotation(feat, "Binding", binding);
	}
	
	/**
	 * make a class to hold one value of a primitive type - for cases where Resources or
	 *  complex data types have repeated values of primitive type
	 * @param primitiveTypeName
	 * @return
	 */
	private EClass makePrimitiveTypeClass(String primitiveTypeName) throws MapperException
	{
		EClass typeClass = EcoreFactory.eINSTANCE.createEClass();
		String className = PrimitiveTypes.referenceClassName(primitiveTypeName);
		typeClass.setName(className);
		ModelUtil.addMIFAnnotation(typeClass, "type", "PrimitiveDataType");
		ModelUtil.addMIFAnnotation(typeClass, "PrimitiveType", primitiveTypeName);
		fhirPackages[PRIMITIVETYPES].getEClassifiers().add(typeClass);
		datatypeClasses.put(className,typeClass);

		EAttribute valAtt = EcoreFactory.eINSTANCE.createEAttribute();
		valAtt.setName("value");
		valAtt.setLowerBound(1);
		valAtt.setEType(PrimitiveTypes.attributeType(primitiveTypeName));
		ModelUtil.addMIFAnnotation(valAtt, "PrimitiveType", primitiveTypeName);
		typeClass.getEStructuralFeatures().add(valAtt);
		
		return typeClass;
	}
	
	
	/**
	 * set the multiplicities of a EAttribute or EReference from the multiplicities of an ElementDef
	 * @param feature
	 * @param elDef
	 */
	private void setMultiplicities(EStructuralFeature feature, ElementDef elDef)
	{
		feature.setLowerBound(1);
		if (elDef.getMinMultiplicity() == MinMult.ZERO) feature.setLowerBound(0);
		if (feature instanceof EReference)
		{
			EReference ref = (EReference) feature;
			ref.setUpperBound(1);
			if (elDef.getMaxMultiplicity() == MaxMult.UNBOUNDED) ref.setUpperBound(-1);
		}
	}
	
	
	/**
	 * set the ETypes of all references to Resource classes
	 */
	private void checkResourceReferences()  throws MapperException
	{
		for (Enumeration<EReference> en = refsToResourceClasses.keys(); en.hasMoreElements();)
		{
			EReference ref = en.nextElement();
			String className = refsToResourceClasses.get(ref);
			// do not try to link to a class for which the resource file was missing
			if (missingResources.get(className) == null)
			{
				EClass resourceClass = resourceClasses.get(className);
				if (resourceClass == null) message("Cannot find resource class " + className);
			}
			else message("Resource class " + className + " file was missing");
		}
	}

	
	/**
	 * set the ETypes of all references to Resource classes
	 */
	private void resolveComponentClassReferences()  throws MapperException
	{
		for (Enumeration<EReference> en = refsToComponentClasses.keys(); en.hasMoreElements();)
		{
			EReference ref = en.nextElement();
			String path = refsToComponentClasses.get(ref);
			EClass componentClass = componentClasses.get(path);
			if (componentClass == null) GenUtil.showMessage("Missing class","Cannot find component class  at path " + path);
			ref.setEType(componentClass);
		}
	}


	
	//---------------------------------------------------------------------------------
	//                         Interface StructureDefinition
	//---------------------------------------------------------------------------------


	/**
	 * find the Element and Attribute structure of some named top element (which may have a named
	 * complex type, or a locally defined anonymous type), stopping at the
	 * next complex type definitions it refers to
	 * @param String name the name of the element
	 * @return Element the EObject subtree (Element and Attribute EObjects) defined by the name.
	 * Make a clone because the Editor may do nasty things to it
	 */
	public ElementDef nameStructure(String name) throws MapperException
	{
		ElementDef  result = null;
		if (name.equals(ATOM_NAMESPACE_PREFIX+ ":feed")) result = feedTree;
		return result;
	}

	/**
	 * find the Element and Attribute structure of some complex type, stopping at the
	 * next complex type definitions it refers to
	 * @param type the name of the complex type
	 * @return the EObject subtree (Element and Attribute EObjects) defined by the type
	 * Make a clone because the Editor may do nasty things to it.
	 */
	public ElementDef typeStructure(String type) throws MapperException
	{
		ElementDef  result = null;
		if (type.equals("Bundle")) result = feedTree;
		return result;
	}

	/**
	 * 
	 * @return an array of the top-level complex types defined in the structure definition - 
	 * any of which can be the type of a mapping set
	 */
	public String[] topComplexTypes()
	{
		String[] res = new String[2];
		res[0] = "";
		res[1] = "Bundle";
		return res;
	}

	/**
	 * @return the set of namespaces defined for the structure
	 */
	public NamespaceSet NSSet()
	{
		NamespaceSet nss = new NamespaceSet();
		try {
			nss.addNamespace(new namespace("",FHIR_NAMESPACE_URI));
			nss.addNamespace(new namespace(ATOM_NAMESPACE_PREFIX,ATOM_NAMESPACE_URI));
		}
		catch (Exception ex) {}
		return nss;
	}
	
	/**
	 * 
	 * @return an array of the top-level complex types defined in the structure definition - 
	 * any of which can be the type of a mapping set
	 */
	public String[] topElementNames()
	{
		String[] res = new String[2];
		res[0] = "";
		res[1] = ATOM_NAMESPACE_PREFIX + ":feed";
		return res;
	}

	
	//---------------------------------------------------------------------------------
	//                         Interface PropertyValueSupplier
	//---------------------------------------------------------------------------------


	/**
	 * 
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return true if this property value supplier supplies values for the 
	 * model class and property, for drop-down choices in the editor
	 */
	public boolean suppliesPropertyValues(String modelClassName, String modelPropertyName)
	{
		if (modelClassName.equals("MappedStructure"))
		{
			if (modelPropertyName.equals("Top Element Type")) return true;
			if (modelPropertyName.equals("Top Element Name")) return true;
		}
		return false;
	}

	/**
	 * 
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return the values supplied by this supplier for the model class and property
	 */
	public String[] propertyValues(String modelClassName, String modelPropertyName)
	{
		if (modelClassName.equals("MappedStructure"))
		{
			if (modelPropertyName.equals("Top Element Type")) return topComplexTypes();
			if (modelPropertyName.equals("Top Element Name")) return topElementNames();
		}
		return new String[0];
	}
	
	//-------------------------------------------------------------------------------------------------------
	//                                        Utilities
	//-------------------------------------------------------------------------------------------------------
	
	
	/**
	 * set the path to the structures folder, which must contain the source profile 
	 * @param filePath
	 * @throws MapperException
	 */
	private void findStructureFolderPath(String filePath) throws MapperException
	{
		StringTokenizer st = new StringTokenizer(filePath,"\\");
		structureFolderPath = "";
		String resourceFileName = "";
		boolean foundStructuresFolder = false;
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			if (!foundStructuresFolder) structureFolderPath = structureFolderPath + step + "\\";
			if (step.equals("Structures")) foundStructuresFolder = true;
			resourceFileName = step;
		}
		StringTokenizer su = new StringTokenizer(resourceFileName,".-");
		resourceFileRoot = su.nextToken();
		
		if (!foundStructuresFolder) 
			throw new MapperException("Profile must be in the 'Structures' folder of a mapper project.");
	}
	
	/**
	 * save the FHIR Ecore model - with the same name as the resource file name before any '.',
	 * in the ClassModel folder of the project
	 * @throws MapperException
	 */
	private void saveFHIRClassModel() throws MapperException
	{
		String mappingSetLocation = ms.eResource().getURI().toString();
		StringTokenizer st = new StringTokenizer(mappingSetLocation,"/");
		String projectFolderPath = "";
		boolean foundMappingSetsFolder = false;
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			if (step.equals("MappingSets")) foundMappingSetsFolder = true;
			else if (!foundMappingSetsFolder) projectFolderPath = projectFolderPath + step + "/";
		}
		
		if (!foundMappingSetsFolder) 
			throw new MapperException("Mapping set must be in the 'MappingSets' folder of a mapper project.");

		String fhirModelLocation = projectFolderPath + "ClassModel/" + resourceFileRoot + ".ecore";
		System.out.println("FHIR class model location: " + mappingSetLocation);
		ModelUtil.savePackage(fhirModelLocation, fhirPackage);
		
	}


	
	
	/**
	 * copy the subtree of EAttributes and child ElementDefs from one
	 * ElementDef to another, without altering the top ElementDef.
	 * Usually the child elements and attributes of the target are initially empty; write a message if not
	 * @param copyEl
	 * @param template
	 */
	private void copyElChildNodes(ElementDef copyEl, ElementDef template, String context, String binding) throws MapperException
	{
		if (copyEl.getChildElements().size() > 0) System.out.println("In context '" + context + "' Target ElementDef " + copyEl.getName() + " already has child ElementDefs.");
		if (copyEl.getAttributeDefs().size() > 0) System.out.println("In context '" + context + "' Target ElementDef " + copyEl.getName() + " already has EAttributes.");

		boolean structureCopied = false;
		for (Iterator<ElementDef> it = template.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef templateChild = it.next();
			// make a deep clone of the child node with all its children
			ElementDef child = (ElementDef)ModelUtil.EClone(templateChild,true);
			copyEl.getChildElements().add(child);
			structureCopied = true;
		}
		
		for (Iterator<AttributeDef> it = template.getAttributeDefs().iterator();it.hasNext();)
		{
			AttributeDef templateAtt = it.next();
			// clone each EAttribute
			AttributeDef copyAtt = (AttributeDef)ModelUtil.EClone(templateAtt,true);
			copyEl.getAttributeDefs().add(copyAtt);
			structureCopied = true;
		}
		
		if (!structureCopied) message("No ElementDef structure copied in context " + context);
		
		// mark the top ElementDef with any binding
		if ((binding != null) && (!binding.equals(""))) copyEl.addAnnotation("Binding", binding);

	}
	
	
	/**
	 * 
	 * @param parent
	 * @param childName
	 * @return the value attribute of the named child;
	 * or null if there is no child of that name
	 * @throws MapperException
	 */
	private String getChildValue(Element parent, String childName)
	{
		String value = null;
		Element child = XMLUtil.firstNamedChild(parent, childName);
		if (child != null) value = child.getAttribute("value");
		return value;
	}
	
	/**
	 * extract an element's name from its path
	 * @param path
	 * @return
	 */
	private String getNameFromPath(String path)
	{
		String name = "";
		StringTokenizer st = new StringTokenizer(path,".");
		while (st.hasMoreTokens()) name = st.nextToken();
		return name;
	}
	
	private String parentPath(String path)
	{
		String parentPath = "";
		StringTokenizer st = new StringTokenizer(path,".");
		while (st.hasMoreTokens())
		{
			String step = st.nextToken();
			if (st.hasMoreTokens())
			{
				if (parentPath.equals("")) parentPath = step;
				else parentPath = parentPath + "." + step;
			}
		}
		return parentPath;		
	}
	
	/**
	 * 
	 * @param parent
	 * @param name
	 * @param type
	 * @param isOptional
	 * @param isMultiple
	 * @return
	 */
	private ElementDef addElementDef(ElementDef parent, String name, String type, 
			boolean isOptional, boolean isMultiple, boolean inAtomNamespace)
	{
		ElementDef elDef = MapperFactory.eINSTANCE.createElementDef();
		String tagName = name;
		if (inAtomNamespace) tagName = ATOM_NAMESPACE_PREFIX + ":" + name;
		elDef.setName(tagName);
		elDef.setType(type);

		elDef.setMinMultiplicity(MinMult.ONE);
		if (isOptional) elDef.setMinMultiplicity(MinMult.ZERO);
		elDef.setMaxMultiplicity(MaxMult.ONE);
		if (isMultiple) elDef.setMaxMultiplicity(MaxMult.UNBOUNDED);

		if (parent != null) parent.getChildElements().add(elDef);
		
		return elDef;
		
	}
	
	
	/**
	 * 
	 * @param elDef
	 * @param type
	 * @param shortDesc
	 */
	private void addValueAttribute(ElementDef elDef, String type, String shortDesc)
	{
		AttributeDef attDef = MapperFactory.eINSTANCE.createAttributeDef();
		attDef.setName("value");
		attDef.setType(type);
		attDef.setDescription(shortDesc);
		attDef.setMinMultiplicity(MinMult.ONE);
		elDef.getAttributeDefs().add(attDef);		
	}
	
	/**
	 * the resource structure changes some underlying resource if the element of the 
	 * structure with path length = 1 changes the name from the path
	 * @param structureEl
	 * @return
	 * @throws MapperException
	 */
	private boolean checkChangedResource(Element structureEl) throws MapperException
	{
		boolean changed = false;
		if (getChildValue(structureEl, "type").equals("Resource"))
		{
			Element oneStep = getElementWithOneStepPath(structureEl);
			if (oneStep == null) throw new MapperException("Resource structure has no top element");
			String path = getChildValue(oneStep,"path");
			String name = getChildValue(oneStep,"name");
			if ((name != null) && (!path.equals(name))) changed = true;
		}
		return changed;
	}
	
	/**
	 * 
	 * @param structureEl
	 * @return the name of the resource defined  by the structure
	 * @throws MapperException
	 */
	private String getResourceName(Element structureEl) throws MapperException
	{
		Element oneStep = getElementWithOneStepPath(structureEl);
		if (oneStep == null) throw new MapperException("Resource structure has no top element");
		String resourceName = getChildValue(oneStep,"path");
		String name = getChildValue(oneStep,"name");
		if (name != null) resourceName = name;
		return resourceName;
	}

	
	/**
	 * get the (presumed one) Element in the structure whose path has only one step
	 * @param structureEl
	 * @return
	 */
	private Element getElementWithOneStepPath(Element structureEl)
	{
		Element oneStep = null;
		for (Iterator<Element> iu = XMLUtil.namedChildElements(structureEl, "element").iterator();iu.hasNext();)
		{
			Element elEl = iu.next();
			String path = getChildValue(elEl,"path");
			if (new StringTokenizer(path,".").countTokens() == 1) oneStep = elEl;
		}
		return oneStep;
	}


	
	private void trace(String s) {if (tracing) message(s);}
	
	private void message(String s) {System.out.println(s);}


}
