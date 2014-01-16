package com.openMap1.mapper.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.NamespaceException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.XpthException;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.reader.objectRep;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.ConversionArgument;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ParameterClass;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.PropertyConversion;
import com.openMap1.mapper.ValueCondition;

/**
 * A collection of utilities to act on the EMF mapper model, 
 * all static.
 * @author robert
 *
 */


public class ModelUtil {
	
	//--------------------------------------------------------------------------------
	//      General utilities for EObjects, Mapped Structures and Ecore models
	//--------------------------------------------------------------------------------
	
	/**
	 * @param node any node in the model tree
	 * @return the root node of the tree. 
	 * For a mapper model, this should be a MappedStructure.
	 */
	public static EObject getModelRoot(EObject node)
	{
		return node.eResource().getContents().get(0);
	}

	
	/**
	 * @param node any node in the model tree
	 * @return the top MappedStructure node.
	 */
	public static MappedStructure getMappedStructure(EObject node)
	{
		return (MappedStructure)node.eResource().getContents().get(0);
	}

	public static EPackage getClassModelRoot(EObject node)
	{
		EPackage classModelRoot = null;
		EObject root = getModelRoot(node);
		if (root instanceof MappedStructure) try
		{
			MappedStructure ms = (MappedStructure)root;
			classModelRoot = ms.getClassModelRoot();
		}
		catch (MapperException ex) {}
		return classModelRoot;
	}
	
	/**
	 * 
	 * @param parent
	 * @param name
	 * @return
	 */
	public static ElementDef getChildElementDef(ElementDef parent, String name)
	{
		ElementDef child = null;
		for (Iterator<ElementDef> it = parent.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef next = it.next();
			if ((next.getName() != null) && (next.getName().equals(name))) child = next;
		}
		return child;
	}
	
	/**
	 * 
	 * @param parent
	 * @param path
	 * @return
	 */
	public static ElementDef getDescendantElementDef(ElementDef parent, String path)
	{
		StringTokenizer st = new StringTokenizer(path, ".");
		ElementDef next = parent;
		while ((st.hasMoreTokens()) && (next != null))
		{
			String step = st.nextToken();
			next = getChildElementDef(next,step);
		}
		return next;
	}

	
	/**
	 * @param node any node in the model tree
	 * @return the global mapping parameters
	 */
	public static GlobalMappingParameters getGlobalMappingParameters (EObject node)
	{
		MappedStructure ms = (MappedStructure) getModelRoot(node);
		return ms.getMappingParameters();
	}
	
	/**
	 * @param node any node in the model tree
	 * @return the global mapping parameters
	 */
	public static NamespaceSet getGlobalNamespaceSet (EObject node) throws NamespaceException
	{
		MappedStructure ms = (MappedStructure) getModelRoot(node);
		if (ms.getMappingParameters() != null)
		return new NamespaceSet(ms.getMappingParameters());
		else return new NamespaceSet();
	}
	
	/**
	 * @param root root of some subtree of a EMF model EObject tree
	 * @param theClass an EMF model EClass
	 * @return a List of all objects of that class or a subclass under the root 
	 */
	public static List<EObject> getEObjectsUnder(EObject root, EClass theClass)
	{
		Vector<EObject> instances = new Vector<EObject>();
		addInstancesUnder(instances, root, theClass);
		return instances;
	}
	
	private static void addInstancesUnder(Vector<EObject> instances, EObject root, EClass theClass)
	{
		if (theClass.isSuperTypeOf(root.eClass())) {instances.add(root);}
		for (Iterator<EObject> it = root.eContents().iterator(); it.hasNext();)
			{addInstancesUnder(instances,it.next(),theClass);}		
	}
	
	/**
	 * 
	 * @param eo an EObject
	 * @return the path of containment association names from the root of the ECore 
	 * instance to this object
	 */
	public static String pathFromRoot(EObject eo)
	{
		String path = "";
		if (eo == null) return path;
		EObject container = eo.eContainer();
		if (container == null) return path;
		else
		{
			EClass thisClass = eo.eClass();
			EClass containerClass = container.eClass();
			for (Iterator<EReference> ir = containerClass.getEAllReferences().iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				EClassifier reffed = ref.getEType();
				if ((reffed instanceof EClass) && (((EClass)reffed)).isSuperTypeOf(thisClass))
					path = pathFromRoot(container) + "/" + ref.getName();
			}
		}
		return path;
	}
	
	/**
	 * 
	 * @param subClass
	 * @param superClass
	 * @return true if subclass is a subclass of superclass, or the same class
	 */
	public static boolean isSubClass(EClass subClass, EClass superClass)
	{
		if (subClass.equals(superClass)) return true;
		for (Iterator<EClass> it = subClass.getEAllSuperTypes().iterator();it.hasNext();)
		{
			EClass superC = it.next();
			if (superC.equals(superClass)) return true;
		}
		return false;
	}
	
	//-------------------------------------------------------------------------------------
	//                                   Mappings
	//-------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param m a property mapping or association end mapping or object mapping
	 * @return the object mapping of the class and subset involved
	 * (there should be exactly one; throws a MappingException if there is more than one,
	 * or less than one, or if the mapping is an association mapping)
	 */
	public static ObjMapping getObjectMapping(Mapping m) throws MapperException
	{
		if (m instanceof AssocMapping)
			{throw new MapperException("Association mappings do not define a class and subset");} 
		ObjMapping om = getObjectMapping(getModelRoot(m),m.getClassSet());
		if (om == null) throw new MapperException("Found no object mapping with class '"
				+ m.getMappedClass() + "' and subset '" + m.getSubset() + "'");
		return om;
	}

	/**
	 * 
	 * @param eo any object in a mapping model tree - typically the root,
	 * but *not* the target object mapping itself
	 * @param cs the required class and subset
	 * @return an object mapping in the subtree below the chosen node,
	 * with the required class and subset, or null if there are none;
	 * throw a MapperException if there is more than one 
	 */
	public static ObjMapping getObjectMapping(EObject eo, ClassSet cs) 
	throws MapperException
	{
		ObjMapping fom = null;
		int found = 0;
		for (Iterator<EObject> it = eo.eAllContents(); it.hasNext();)
		{
			EObject nex = it.next();
			if (nex instanceof ObjMapping)
			{
				ObjMapping om = (ObjMapping)nex;
				if (cs.equals(om.getClassSet())) 
				{
					fom = om; 
					found++;
				}
			}
		}
		if (found > 1) throw new MapperException("There are " + found + " object mappings with class "
				+ cs.stringForm());
		return fom;
	}
	
	/**
	 * 
	 * @param n any Node in the mapped structure
	 * @return absolute path from the root to that node
	 * @throws XpthException
	 */
	public static String getRootPath(NodeDef n) throws XpthException
	{
		return buildPathFrom(n,"");
	}
	
	/**
	 * 
	 * @param n any Node in the mapped structure
	 * @return absolute path from the root to that node
	 * @throws XpthException
	 */
	public static Xpth getRootXpth(NodeDef n) throws XpthException
	{
		String XPathString = buildPathFrom(n,"");
		return new Xpth(getGlobalNamespaceSet(n),XPathString);
	}
	
	private static String buildPathFrom(NodeDef currentNode, String pathBelowNode) 
		throws XpthException
	{
		String thisStep = "";
		if (currentNode instanceof AttributeDef) thisStep = "/@" + currentNode.getName();
		else if (currentNode instanceof ElementDef) thisStep = "/" + currentNode.getName();
		String pathIncludingNode = thisStep + pathBelowNode;

		EObject parent = currentNode.eContainer();
		if (parent instanceof MappedStructure) return pathIncludingNode;
		else if (parent instanceof NodeDef)
			{return buildPathFrom((NodeDef)parent,pathIncludingNode);}
		else throw new XpthException("Container of node '" + currentNode.getName() + "' is a "
				+ parent.getClass().getName());
	}
	
	/**
	 * @param root the root of the mapped Structure	 * 
	 * @param rootPath an absolute definite path from the root of the mapped structure
	 * @return the Node at the end of the path, or null if there is none
	 * @throws MapperException if any step along the path leads to more than one child node
	 * or if the path is not an absolute definite descending path
	 */
	public static NodeDef getNodeDef(MappedStructure root, Xpth rootPath) throws MapperException
	{
		NodeDef nd = null;
		if (rootPath.size() > 0)
		{
			nd = root.getRootElement();
			if ((nd != null) && (nd.getName().equals(rootPath.nodeName(0))))
			{
				if (rootPath.size() == 1) return nd;
				else for (int s = 1; s < rootPath.size(); s++) 
					if ((nd != null) && (nd instanceof ElementDef))
					{
						String stepName = rootPath.nodeName(s);
						boolean isSAttribute = rootPath.axis(s).equals("attribute");
						nd = namedChildNode((ElementDef)nd,stepName,isSAttribute);
					}
			}
		}
		return nd;
	}
	
	/**
	 * 
	 * @param parent a parent Element
	 * @param name the name of a child element or attribute (beginning with '@')
	 * @return the named child node, or null if there is none
	 * @throws MapperException if there is more than one child of that name7u6
	 * 
	 */
	public static NodeDef namedChildNode(ElementDef parent, String name, boolean isAttribute) 
	  throws MapperException
	  {
		NodeDef nd = null;
		int found = 0;
		if (isAttribute)
			for (Iterator<AttributeDef> it = parent.getAttributeDefs().iterator(); it.hasNext();)
			{
				AttributeDef child = it.next();
				if (child.getName().equals(name)) {nd = child; found++;}			
			}
		else
			for (Iterator<ElementDef> it = parent.getChildElements().iterator(); it.hasNext();)
			{
				ElementDef child = it.next();
				if (child.getName().equals(name)) {nd = child; found++;}			
			}
		if (found > 1) throw new MapperException("Found " + found + " child nodes with name '"
		 	+ name + "' beneath Element '" + parent.getName() + "'");
		return nd;
	  }
	
	
	/**
	 * 
	 * @param className qualfied name of a class, prefixed by the package name
	 * @param node any node in the mapping set
	 * @return class details, if they have been specified
	 */
	public static ClassDetails getClassDetailsX(String className, EObject node)
	{
		ClassDetails cd = null;
		for (Iterator<ClassDetails> it = getGlobalMappingParameters(node).getClassDetails().iterator(); it.hasNext();)
		{
			ClassDetails next = it.next();
			if (next.getQualifiedClassName().equals(className)) cd = next;
		}
		return cd;
	}
	
	/**
	 * @param cs a class and subset
	 * 
	 * @param propName a property name
	 * @param node any node in the mapping set
	 * @return all property mappings for the class, subset and property. 
	 * There can be more than one if the mappings are multiway - choice or redundant
	 */
	public static Vector<PropMapping> getPropertyMappings(ClassSet cs, String propName, EObject node)
	{
		Vector<PropMapping> pmv = new Vector<PropMapping>();
		for (Iterator<EObject> it = getModelRoot(node).eAllContents();it.hasNext();)
		{
			EObject next = it.next();
			if (next instanceof PropMapping)
			{
				PropMapping pm = (PropMapping)next;
				if ((pm.getClassSet().equals(cs)) && (pm.getMappedProperty().equals(propName)))
					{pmv.add(pm);}
			}
		}
		return pmv;
	}
	
	//-------------------------------------------------------------------------------------
	//                         Convenience methods for creating mappings
	//-------------------------------------------------------------------------------------
	
	/**
	 * make a clone tree from the tree of ElementDefs and AttributeDefs toClone, 
	 * and add it to the children of ElementDef parent. (parent may be null)
	 * Do not clone mappings or annotations.
	 */
	public static ElementDef cloneTree(ElementDef parent, ElementDef toClone)
	{
		ElementDef copyEl = MapperFactory.eINSTANCE.createElementDef();
		
		copyEl.setName(toClone.getName());
		copyEl.setDefaultValue(toClone.getDefaultValue());
		copyEl.setDescription(toClone.getDescription());
		copyEl.setMinMultiplicity(toClone.getMinMultiplicity());
		copyEl.setMaxMultiplicity(toClone.getMaxMultiplicity());
		copyEl.setFixedValue(toClone.getFixedValue());
		copyEl.setType(toClone.getType());
		
		// copy across all AttributeDefs
		for (Iterator<AttributeDef> it = toClone.getAttributeDefs().iterator(); it.hasNext();)
			cloneAttributeDef(copyEl,it.next());
		
		// recursively copy across ElementDef subtrees
		for (Iterator<ElementDef> it = toClone.getChildElements().iterator();it.hasNext();)
			cloneTree(copyEl,it.next());

		if (parent != null) parent.getChildElements().add(copyEl);
		return copyEl;
	}

	/**
	 * clone an attribute def (without mappings) and copy to a new parent ElementDef
	 * @param parent
	 * @param toClone
	 * @return
	 */
	public static AttributeDef cloneAttributeDef(ElementDef parent, AttributeDef toClone)
	{
		AttributeDef copyAtt = MapperFactory.eINSTANCE.createAttributeDef();
		
		copyAtt.setName(toClone.getName());
		copyAtt.setDefaultValue(toClone.getDefaultValue());
		copyAtt.setDescription(toClone.getDescription());
		copyAtt.setFixedValue(toClone.getFixedValue());
		copyAtt.setMinMultiplicity(toClone.getMinMultiplicity());
		copyAtt.setType(toClone.getType());

		parent.getAttributeDefs().add(copyAtt);
		return copyAtt;
	}

	/**
	 * return the Node Mapping set on a node; make
	 * a new one if necessary
	 */
	public static NodeMappingSet getNodeMappingSet(NodeDef nd)
	{
		NodeMappingSet nms = nd.getNodeMappingSet();
		if (nms == null)
		{
			nms = MapperFactory.eINSTANCE.createNodeMappingSet();
			nd.setNodeMappingSet(nms);
		}
		return nms;
	}
	
	/**
	 * add an ObjMapping to a set of mappings; set its clasc, package, and subset
	 */
	public static ObjMapping addObjMapping(NodeMappingSet nms, String className, String packageName, String subset)
	{
		ObjMapping om = MapperFactory.eINSTANCE.createObjMapping();
		om.setMappedClass(className);
		om.setMappedPackage(packageName);
		om.setSubset(subset);
		nms.getObjectMappings().add(om);
		return om;
	}
	
	/**
	 * add a property mapping, for a property of an object defined by an object mapping
	 * @param nms
	 * @param om
	 * @param propName
	 * @return
	 */
	public static PropMapping addPropMapping(NodeMappingSet nms,ObjMapping om, String propName)
	{
		PropMapping pm = MapperFactory.eINSTANCE.createPropMapping();
		pm.setMappedClass(om.getMappedClass());
		pm.setMappedPackage(om.getMappedPackage());
		pm.setSubset(om.getSubset());
		pm.setMappedProperty(propName);
		nms.getPropertyMappings().add(pm);
		return pm;
	}
	
	/**
	 * add a ValueCondition to a Mapping; set the left path and right value
	 * (test is '=' and there is no function applied)
	 * @param om
	 * @param path
	 * @param value
	 * @return the ValueCondition
	 */
	public static ValueCondition addValueCondition(Mapping m, String path, String value)
	{
		ValueCondition vc = MapperFactory.eINSTANCE.createValueCondition();
		vc.setLeftPath(path);
		vc.setRightValue(value);
		m.getMappingConditions().add(vc);
		return vc;
	}
	
	/**
	 * add a fixed property value to an ObjMapping; 
	 * set the property name and the value
	 * @param om
	 * @param property
	 * @param value
	 * @return the FixedPropertyValue
	 */
	public static FixedPropertyValue addFixedPropertyValue(ObjMapping om, String property, String value)
	{
		FixedPropertyValue fp = MapperFactory.eINSTANCE.createFixedPropertyValue();
		fp.setMappedProperty(property);
		fp.setFixedValue(value);
		om.getFixedPropertyValues().add(fp);
		return fp;
	}
	
	/**
	 * add a simple association mapping between two mapped classes, with no further mapping 
	 * conditions, and making the inner class dependent on the mapping.
	 * The associaiton role name is on the inner end.
	 * @param nms
	 * @param om1
	 * @param om2
	 * @param roleName
	 * @return the association mapping
	 */
	public static AssocMapping addSimpleAssocMapping(NodeMappingSet nms, ObjMapping om1, ObjMapping om2, String roleName)
	{
		AssocMapping am = MapperFactory.eINSTANCE.createAssocMapping();

		// end 1
		AssocEndMapping am1 = MapperFactory.eINSTANCE.createAssocEndMapping();
		am1.setMappedClass(om1.getMappedClass());
		am1.setMappedPackage(om1.getMappedPackage());
		am1.setSubset(om1.getSubset());
		am1.setMappedRole("");
		am.setMappedEnd1(am1);

		// end 2
		AssocEndMapping am2 = MapperFactory.eINSTANCE.createAssocEndMapping();
		am2.setMappedClass(om2.getMappedClass());
		am2.setMappedPackage(om2.getMappedPackage());
		am2.setSubset(om2.getSubset());
		am2.setMappedRole(roleName);
		am2.setRequiredForObject(true);
		am.setMappedEnd2(am2);

		nms.getAssociationMappings().add(am);
		return am;
	}
	
	//-------------------------------------------------------------------------------------
	//                         Convenience methods on an EMF ECore model
	//-------------------------------------------------------------------------------------
	
	/**
	 * @param packageName name of a package in the ecore model
	 * @param node any node in the mapped structure
	 * @return the package object, or null if none found
	 */
	public static EPackage getEPackage(String packageName,EObject node)
	{
		EPackage thePackage = null;
		try
		{
			EPackage cmRoot = ((MappedStructure)getModelRoot(node)).getClassModelRoot();
			if (cmRoot != null) thePackage = getPackageInPackage(cmRoot,packageName);
		}
		catch (MapperException ex) {GenUtil.surprise(ex, "ModelUtil.getEPackage");}
		return thePackage;		
	}
	
	/**
	 * recursive search through a package and its sub-packages for 
	 * a package of a given name
	 * @param thePackage top package being searched
	 * @param packageName name of the package sought
	 * @return
	 */
	public static EPackage getPackageInPackage(EPackage thePackage,String packageName)
	{
		EPackage result = null;
		// null Package name can count as Package name "", because Ecore will only store null, not ""
		if ((thePackage.getName() == null) && (packageName == null))
			result = thePackage;
		else if ((thePackage.getName() == null) && ("".equals(packageName)))
			result = thePackage;
		else if ((thePackage.getName() != null) && (thePackage.getName().equals(packageName)))
			result = thePackage;
		else for (Iterator<EPackage> it = thePackage.getESubpackages().iterator();it.hasNext();)
		{
			EPackage subPackage = it.next();
			if (getPackageInPackage(subPackage,packageName) != null)
				result = getPackageInPackage(subPackage,packageName);
		}
		return result;
	}
	
	/**
	 * 
	 * @param className a name
	 * @param packageName the package the class is in
	 * @param node any node in the mapping set tree
	 * @return the EClass if it exists; or null if it does not
	 */
	public static EClass getEClass(String className,String packageName,EObject node)
	{
		EClass theClass = null;
		EPackage thePackage = getEPackage(packageName,node);
		if (thePackage != null)
			for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			if ((next instanceof EClass) && (next.getName().equals(className))) 
				theClass = (EClass)next;
		}			
		return theClass;
	}
	
	/**
	 * 
	 * @param start an EClass
	 * @param refName the name of an EReference
	 * @return the EClass at the end of the EReference, if it exists; or null otherwise
	 */
	public static EClass getReferencedClass(EClass start, String refName)
	{
		EClass refClass = null;
		for (Iterator<EReference> ir = start.getEAllReferences().iterator();ir.hasNext();)
		{
			EReference ref = ir.next();
			if ((ref.getName().equals(refName)) && (ref.getEType() instanceof EClass))
				refClass = (EClass)ref.getEType();
		}
		return refClass;
	}
	
	/**
	 * 
	 * @param className a name
	 * @param node any node in the mapping set tree
	 * @return true if the name is the name of a class in the class model;
	 * false if it is not, or if the class model does not exist
	 */
	public static boolean isInClassModel(String className,String packageName,EObject node)
	{
		return (getEClass(className, packageName, node) != null);
	}
	
	/**
	 * 
	 * @param fromClass name of a class
	 * @param rolename of an association
	 * @param toClass name of a class
	 * @param node any object in the mapped structure
	 * @return true of there as an association from.role(to), inherited or not.
	 */
	public static boolean associationExists(String fromClass, String fromPackage,
			String role, String toClass, String toPackage,EObject node)
	{
		boolean exists = false;
		EClass from = getEClass(fromClass,fromPackage,node);
		EClass to = getEClass(toClass,toPackage,node);
		if ((from != null) && (to != null))
		{
			for (Iterator<EReference> it = from.getEAllReferences().iterator();it.hasNext();)
			{
				EReference ref = it.next();
				if ((ref.getName().equals(role)) 
						&& (ref.getEReferenceType().isSuperTypeOf(to))) exists = true;
			}
			
		}
		return exists;
	}
	
	/**
	 * write out the values of all EAttributes and EReferences of an EObject
	 * @param eo
	 */
	public static void writeAllFeatures(EObject eo)
	{
		EClass ec = eo.eClass();
		System.out.println("Features of object of class " + ec.getName());
		for (Iterator<EAttribute> it = ec.getEAllAttributes().iterator();it.hasNext();)
		{
			EAttribute ea = it.next();
			Object val = eo.eGet(ea);
			String att = ("Attribute " + ea.getName());
			if (val != null)
				{att = att + " (" + val.getClass().getName() + "): " + val.toString();}
			else {att = att + " (null)";}
			System.out.println(att);			
		}
		
		for (Iterator<EReference> it = ec.getEAllReferences().iterator();it.hasNext();)
		{
			EReference er = it.next();
			Object val = eo.eGet(er);
			String ref = ("Reference " + er.getName());
			if (val == null) ref = ref + "(null)";
			else
			{
				if (val instanceof EList<?>) ref = ref + "(" + ((EList<?>)val).size() + ")";
				else {ref = ref + " (" + val.getClass().getName() + "): " + val.toString();}
			}
			System.out.println(ref);
		}
		System.out.println("");
	}
	
	/**
	 * clone an EAttribute
	 * @param ea
	 * @return
	 */
	public static EAttribute cloneEAttribute(EAttribute ea)
	{
		EAttribute theAtt = EcoreFactory.eINSTANCE.createEAttribute();
		theAtt.setName(ea.getName());
		theAtt.setEType(ea.getEType()); 
		theAtt.setLowerBound(ea.getLowerBound());
		copyMifAnnotations(ea,theAtt);
		return theAtt;
	}
	
		
	/**
	 * clone an EReference, giving it the same target class as the original
	 * @param er
	 * @return
	 */
	public static EReference cloneEReference(EReference er)
	{
		EReference theRef = EcoreFactory.eINSTANCE.createEReference();
		theRef.setName(er.getName());
		theRef.setEType(er.getEType()); 
		theRef.setLowerBound(er.getLowerBound());
		theRef.setUpperBound(er.getUpperBound());
		theRef.setContainment(er.isContainment());
		copyMifAnnotations(er,theRef);
		return theRef;
	}
	
	/**
	 * clone an EClass, putting it in the same EPackage as the original, with a different name
	 * @param ec
	 * @return the cloned EClass
	 */
	public static EClass cloneEClass(EClass ec, String newName)
	{
		EClass theClass = EcoreFactory.eINSTANCE.createEClass();
		theClass.setName(newName);
		ec.getEPackage().getEClassifiers().add(theClass);
		for (Iterator<EStructuralFeature> it = ec.getEStructuralFeatures().iterator(); it.hasNext();)
		{
			EStructuralFeature next = it.next();
			if (next instanceof EAttribute)
				theClass.getEStructuralFeatures().add(cloneEAttribute((EAttribute)next));
			if (next instanceof EReference)
				theClass.getEStructuralFeatures().add(cloneEReference((EReference)next));
		}
		copyMifAnnotations(ec,theClass);
		return theClass;		
	}
	
	/**
	 * clone an EClass, putting it in the same EPackage as the original, with a different name
	 * and not cloning any EAttributes or EReferences with names in a list 
	 * and no
	 * @param ec
	 * @return the cloned EClass
	 */
	public static EClass cloneEClassWithoutFeatures(EClass ec, String newName, Vector<String> featureNames)
	{
		EClass theClass = EcoreFactory.eINSTANCE.createEClass();
		theClass.setName(newName);
		ec.getEPackage().getEClassifiers().add(theClass);
		for (Iterator<EStructuralFeature> it = ec.getEStructuralFeatures().iterator(); it.hasNext();)
		{
			EStructuralFeature next = it.next();
			if (!GenUtil.inVector(next.getName(), featureNames))
			{
				if (next instanceof EAttribute)
					theClass.getEStructuralFeatures().add(cloneEAttribute((EAttribute)next));
				if (next instanceof EReference)
					theClass.getEStructuralFeatures().add(cloneEReference((EReference)next));				
			}
		}
		copyMifAnnotations(ec,theClass);
		return theClass;		
	}

	
	//-------------------------------------------------------------------------------------
	//                         Mapped properties and pseudo-properties
	//-------------------------------------------------------------------------------------
	
	/**
	 * get all properties of a class, direct or inherited
	 * @param className
	 * @param node
	 * @return
	 */
	public static Vector<String> getProperties(String className, String packageName, EObject node)
	{
		Vector<String> propNames = new Vector<String>();
		EClass cl = getEClass(className, packageName,  node);
		if (cl != null) for (Iterator<EAttribute> iw = cl.getEAllAttributes().iterator();iw.hasNext();)
			propNames.add(iw.next().getName());
		return propNames;
	}
	
	/**
	 * get all properties and pseudo-properties of a class and subset
	 * @param className
	 * @param subset
	 * @param node any node in the mapping set
	 * @return
	 */
	public static Vector<String> getPropertiesAndPseudoProperties(String className, String packageName,String subset, EObject node)
	{
		Vector<String> props = getProperties(className,packageName,node);
		Vector<String> pProps = getPseudoProperties(className,subset,node);
		for (Iterator<String> it = pProps.iterator();it.hasNext();) props.add(it.next());
		return props;
	}
	
	/**
	 * 
	 * @param className
	 * @param packageName
	 * @param subset
	 * @param propName
	 * @param node any ode n the mapping set
	 * @return true if the property is a property of the class, 
	 * or a pseudo-property of the class and subset
	 */
			
	public static boolean hasPropertyOrPseudoProperty
		(String className, String packageName, String subset, String propName, EObject node)
	{
		return GenUtil.inVector(propName, getPropertiesAndPseudoProperties(className,packageName,subset,node));
	}
	/**
	 * @param className
	 * @param packageName
	 * @param propName
	 * @param node any ode n the mapping set
	 * @return true if the property is a property of the class, 
	 */
			
	public static boolean hasProperty
		(String className, String packageName,String propName, EObject node)
	{
		return GenUtil.inVector(propName, getProperties(className,packageName,node));
	}
	
	/**
	 * return a Hashtable of all pseudo-properties of any class
	 * (things which are converted into real properties in the class model)
	 * These are all the arguments of any 'in' conversions and 
	 * results of any 'out' conversions, sor all subset mappings
	 * @param className qualfied name of the class
	 * @param node any node in the mapping set
	 * @return Hashtable of all pseudo-property names (key) and their mapping subsets (value) 
	 * The value is not much use, since the same pseudo-property may be used for several subsets
	 * and only one will be recorded.
	 */
	public static Hashtable<String,String> getPseudoProperties(String className, NodeDef node)
	{
		MappedStructure ms = (MappedStructure) getModelRoot(node);
		// store pseudo-properties in a Hashtable to avoid duplicates and note the subsets
		Hashtable<String,String> ppSet = new Hashtable<String,String>();
		if (ms.getMappingParameters() != null)
		for (Iterator<ClassDetails> it = ms.getMappingParameters().getClassDetails().iterator();it.hasNext();)
		{
			ClassDetails cd = it.next();
			if (cd.getQualifiedClassName().equals(className))
			{
				// find all results of 'out' conversions and arguments of 'in' conversions for the subset
				for (Iterator<PropertyConversion> iw = cd.getPropertyConversions().iterator();iw.hasNext();)
				{
					PropertyConversion pc = iw.next();
					if (pc.getSense().getLiteral().equals("out")) ppSet.put(pc.getResultSlot(),pc.getSubset());
					else if (pc.getSense().getLiteral().equals("in"))
						for (Iterator<ConversionArgument> ix = pc.getConversionArguments().iterator();ix.hasNext();)
							{ppSet.put(ix.next().getPropertyName(),pc.getSubset());}						
				}
			}
		}
		return ppSet;
	}
	
	/**
	 * get names of all pseudo-properties of a given class  and subset
	 * @param className
	 * @param subset
	 * @param node any node in the mapped structure
	 * @return
	 */
	public static Vector<String> getPseudoProperties(String className, String subset, EObject node)
	{
		MappedStructure ms = (MappedStructure) getModelRoot(node);
		Vector<String> ppNames = new Vector<String>();
		if (ms.getMappingParameters() != null)
		for (Iterator<ClassDetails> it = ms.getMappingParameters().getClassDetails().iterator();it.hasNext();)
		{
			ClassDetails cd = it.next();
			if (cd.getQualifiedClassName().equals(className))
			{
				// find all results of 'out' conversions and arguments of 'in' conversions for the subset
				for (Iterator<PropertyConversion> iw = cd.getPropertyConversions().iterator();iw.hasNext();)
				{
					PropertyConversion pc = iw.next();
					if (pc.getSubset().equals(subset))
					{
						if (pc.getSense().getLiteral().equals("out")) ppNames.add(pc.getResultSlot());
						else if (pc.getSense().getLiteral().equals("in"))
							for (Iterator<ConversionArgument> ix = pc.getConversionArguments().iterator();ix.hasNext();)
								{ppNames.add(ix.next().getPropertyName());}												
					}
				}
			}
		}
		return ppNames;
	}
	
	
	//---------------------------------------------------------------------------------------
	//                               Paths in the mapped structure
	//---------------------------------------------------------------------------------------


	/**
	 * 
	 * @param node a Node in the Mapped structure
	 * @param path an XPath
	 * @return true if the XPath is a relative path, 
	 * and any 'parent' , 'child' or 'attribute' steps match the node names from the 
	 * start node
	 */
	public static boolean isRelativePath(NodeDef node, Xpth path) throws MapperException
	{
		boolean isRPath = false;
		try
		{
			if (!path.fromRoot()) // absolute paths fail
			{
				// 'stay here' is always a valid path
				if (path.axis(0).equals("self"))
				{
					if (path.nodeName(0).equals("node()"))isRPath = true;
					if (path.nodeName(0).equals(node.getName()))isRPath = true;
				}
				
				// ancestor step must be the only ascending step ('ancestor-or-self' is not yet allowed)
				else if (path.axis(0).equals("ancestor"))
				{
					EObject parent = node.eContainer();
					while ((!isRPath) && (parent instanceof ElementDef)) // fail when you reach MappedStructure
					{
						// if an ancestor name matches, check the descent from that ancestor
						if (((ElementDef)parent).getName().equals(path.nodeName(0)))
						{
							// single-step ancestor paths are valid
							if (path.size() == 1) isRPath = true;
							// otherwise test the descending part
							else isRPath = isDescendingPath((ElementDef)parent, path.removeOuterStep());
						}
						parent = parent.eContainer(); // keep trying ancestors
					}
				}
	
				// parent step: check the containing element name
				else if ((path.axis(0).equals("parent")) && (node.eContainer() instanceof ElementDef))
				{
					ElementDef parent = (ElementDef)node.eContainer();
					// the node test 'node()' will match any node name
					if ((parent.getName().equals(path.nodeName(0)))|
							(path.nodeName(0).equals("node()")))
					{
						if (path.size() == 1) isRPath = true; // no more steps to check
						else if (path.size() > 1)
						{
							// recursive check of remaining path
							isRPath = isRelativePath(parent,path.removeOuterStep());
						}
					}
				}
				else if ((path.axis(0).equals("child")) && (node instanceof ElementDef))
				{
					isRPath = isDescendingPath((ElementDef)node, path);
				}
				else if (path.axis(0).equals("attribute")) 
				{
					if (node instanceof ElementDef)
					isRPath = (((ElementDef)node).getNamedAttribute(path.nodeName(0)) != null);
				}
				// other axes lead to failure
			}
		}
		catch (XpthException ex) {return false;}
		return isRPath;
	}
		
	
	/**
	 * 
	 * @param node a Node in the Mapped structure
	 * @param path an XPath
	 */
	public static boolean isDescendingPath(ElementDef node, Xpth path) throws MapperException
	{
		boolean isDPath = false;
		if ((path.axis(0).equals("descendant"))|(path.axis(0).equals("descendant-or-self")))
		{
			isDPath = true; // there is not much checking you can do after a descendant step
		}
		else if (path.axis(0).equals("child"))
		{
			// find a matching child element name
			for (Iterator<ElementDef> it = node.getChildElements().iterator();it.hasNext();)
			{
				ElementDef child = it.next();
				/* the node name must match; child::node() is not allowed */
				if (child.getName().equals(path.nodeName(0)))
				{
					if (path.size() == 1) isDPath = true; // no more steps to check
					else if (path.size() > 1)
					{
						// recursive step - check the remainder of the path against the child element
						isDPath = isDescendingPath(child,path.removeOuterStep());
					}
				}
			}
			
		}
		// must match an attribute name
		else if ((path.axis(0).equals("attribute")) && (path.size() == 1))
		{
			for (Iterator<AttributeDef> it = node.getAttributeDefs().iterator();it.hasNext();)
				if (it.next().getName().equals(path.nodeName(0))) isDPath = true;
		}
		return isDPath;
	}

	
	/**
	 * 
	 * @param node a Node in the Mapped structure
	 * @param path an XPath
	 * @param mustbeUnique if true, the path must lead to at most one node
	 * @param isNonOptional if true, the path must lead to at least one node
	 * @return true if the XPath is a relative path, 
	 * every step has an 'ancestor', 'parent','attribute'  or 'child' axis,
	 * every node is named and matches the structure, and (when mustbeUnique is true)
	 * if the path leads to at most one node.
	 */
	public static boolean isRelativeDefinitePath(NodeDef node, Xpth path, 
			boolean mustBeUnique,
			boolean isNonOptional)
	{
		boolean isRDPath = false;
		try
		{
			/* indefinite paths (other than ancestor paths which are allowed)
			 * are filtered out by their 'descendant' steps. */
			if (!path.fromRoot()) 
			{
				// 'stay here' is always a valid and unique path
				if (path.axis(0).equals("self"))
				{
					if (path.nodeName(0).equals("node()"))isRDPath = true;
					if (path.nodeName(0).equals(node.getName()))isRDPath = true;
				}
				
				// ancestor step must be the only ascending step ('ancestor-or-self' is not yet allowed)
				else if (path.axis(0).equals("ancestor"))
				{
					EObject parent = node.eContainer();
					while ((!isRDPath) && (parent instanceof ElementDef)) // fail when you reach MappedStructure
					{
						// if an ancestor name matches, check the descent from that ancestor
						if (((ElementDef)parent).getName().equals(path.nodeName(0)))
						{
							// single-step ancestor paths are valid and unique
							if (path.size() == 1) isRDPath = true;
							// otherwise test the descending part
							else isRDPath = isDescendingRelativeDefinitePath
								((ElementDef)parent, path.removeOuterStep(), mustBeUnique,isNonOptional);
						}
						parent = parent.eContainer();
					}
				}
	
				// parent step: check the containing element name, but uniqueness is guaranteed for the step
				else if ((path.axis(0).equals("parent")) && (node.eContainer() instanceof ElementDef))
				{
					ElementDef parent = (ElementDef)node.eContainer();
					// the node test 'node()' will match any node name
					if ((parent.getName().equals(path.nodeName(0)))|
							(path.nodeName(0).equals("node()")))
					{
						if (path.size() == 1) isRDPath = true; // no more steps to check
						else if (path.size() > 1)
						{
							// recursive check of remaining path
							isRDPath = isRelativeDefinitePath(parent,path.removeOuterStep(), mustBeUnique,isNonOptional);
						}
					}
				}
				else if ((path.axis(0).equals("child")) && (node instanceof ElementDef))
				{
					isRDPath = isDescendingRelativeDefinitePath((ElementDef)node, path, mustBeUnique,isNonOptional);
				}
				else if (path.axis(0).equals("attribute")) 
				{
					if (node instanceof ElementDef)
					isRDPath = (((ElementDef)node).getNamedAttribute(path.nodeName(0)) != null);
				}
				// other axes lead to failure
			}
		}
		catch (XpthException ex) {return false;}
		return isRDPath;
	}

	
	/**
	 * 
	 * @param node a Node in the Mapped structure
	 * @param path an XPath
	 * @param mustbeUnique if true, the path must lead to at most one node
	 * @param isNonOptional if true, the path must lead to at least one node
	 * @return true if the XPath is a relative path, every step has the 'child' axis,
	 * every node is named and matches the structure, and (if mustbeUnique = true)
	 * the path leads to at most one node.
	 */
	public static boolean isDescendingRelativeDefinitePath(ElementDef node, Xpth path, 
			boolean mustBeUnique,
			boolean isNonOptional)
	{
		boolean isDDPath = false;
		try
		{
			if ((path.definite()) && (!path.fromRoot())) 
			{
				if (path.axis(0).equals("child"))
				{
					// find a matching child element name
					for (Iterator<ElementDef> it = node.getChildElements().iterator();it.hasNext();)
					{
						ElementDef child = it.next();
						/* the node name must match, and possibly uniqueness and/or non-optionality may be required */
						if ((child.getName().equals(path.nodeName(0))) &&
							((!mustBeUnique)|(child.getMaxMultiplicity()== MaxMult.ONE)) &&
							((!isNonOptional)|(child.getMinMultiplicity()== MinMult.ONE)))						
						{
							if (path.size() == 1) isDDPath = true; // no more steps to check
							else if (path.size() > 1)
							{
								// recursive step - check the remainder of the path against the child element
								isDDPath = isDescendingRelativeDefinitePath
									(child,path.removeOuterStep(),mustBeUnique,isNonOptional);
							}
						}
					}
				}
				// an attribute must be the last step in a path, and is unique; match the name
				else if ((path.axis(0).equals("attribute")) && (path.size() == 1))
				{
					for (Iterator<AttributeDef> it = node.getAttributeDefs().iterator();it.hasNext();)
					{
						AttributeDef ad = it.next();
						if (ad.getName().equals(path.nodeName(0)) && 
								((!isNonOptional)|(ad.getMinMultiplicity()== MinMult.ONE))) isDDPath = true;
					}
				}
				// other axes, such as descendant, cause failure
			}			
		}
		catch (XpthException ex) {return false;}
		return isDDPath;
	}
	
	/**
	 * @param node any Element in the mapped structure
	 * @param mustBeUnique
	 * @return a Vector of all definite relative paths from a node.
	 * If mustBeUnique is true, these can include only paths that lead to one node.
	 * Steps have only 'child' or 'parent' axes. 
	 */
	public static Vector<Xpth> getRelativeDefinitePaths(NodeDef node, boolean mustBeUnique)
	{
		Vector<Xpth> paths = new Vector<Xpth>();
		try {
			// the 'stay here' path is allowed from any node
			paths.add(new Xpth(getGlobalNamespaceSet(node),"."));

			// find any descending paths from this node
			if (node instanceof ElementDef)
		    {
				Vector<Xpth> dPaths = getDRDPaths((ElementDef)node, null, null, mustBeUnique);
				for (Iterator<Xpth> ix = dPaths.iterator();ix.hasNext();) 
					paths.add(ix.next());
			} 
		
			// climb parents, adding paths to them and their descendants
			Xpth nodePath = new Xpth(getGlobalNamespaceSet(node));
			while (node.eContainer() instanceof ElementDef) // stops at the MappedStructure node
			{
				ElementDef parent = (ElementDef)node.eContainer();
				Xpth parentPath  = nodePath.addInnerStep("parent::" + parent.getName());
				// exclude each node on the ascent, from the paths back down from its higher parents
				Vector<Xpth> pPaths = getDRDPaths(parent, parentPath, node, mustBeUnique);
				for (Iterator<Xpth> ix = pPaths.iterator();ix.hasNext();) 
					paths.add(ix.next());
				node = parent;	// to iterate up to ancestors
				nodePath = parentPath; // to keep building a multiple 'parent::' path
			}
		}
		catch (MapperException ex)	{GenUtil.surprise(ex, "ModelUtil.getRelativeDefinitePaths");}
		return paths;
	}
	
	/**
	 * get all descending definite relative paths from a node.
	 * If mustBeUnique is true, these can include only paths that lead to one node.
	 * Steps have only 'child' axes. 
	 */
	public static Vector<Xpth> getDescendingRelativeDefinitePaths(ElementDef node, boolean mustBeUnique)
	{
		return getDRDPaths(node, null, null, mustBeUnique); 
	}
	
	/*
	 * recursive finding of all descending relative definite paths from a node, 
	 * adding them as inner steps to the path so far
	 * (if it is non-null ;otherwise starting a new path)
	 * If excludeChild is not null, do not include a path that goes back down to that child node.
	 */
	private static Vector<Xpth> getDRDPaths(ElementDef node, Xpth pathSoFar, NodeDef excludeChild, boolean mustBeUnique)
	{
		Vector<Xpth> paths = new Vector<Xpth>();
		try {
			// if non-null, the path to this node is part of the result set
			if (pathSoFar != null) paths.add(pathSoFar);

			// at the start of the recursion, we need an empty path to build on
			else if (pathSoFar == null) pathSoFar = new Xpth(getGlobalNamespaceSet(node));
			
			// recursion through child elements
			for (Iterator<ElementDef> it = node.getChildElements().iterator();it.hasNext();)
			{
				ElementDef child = it.next();
				// to avoid paths which go up and then come back down to the same node
				boolean skipChild = ((excludeChild != null)&& (child.getName().equals(excludeChild.getName())));
				// sometimes go only to unique child nodes
				if ((!skipChild) && ((!mustBeUnique)|(child.getMaxMultiplicity() == MaxMult.ONE)))
				{
					Xpth extended = pathSoFar.addInnerStep("child::" + child.getName());
					Vector<Xpth> childPaths = getDRDPaths(child, extended, null, mustBeUnique);
					for (Iterator<Xpth> ix = childPaths.iterator();ix.hasNext();) 
						paths.add(ix.next());
				}
			}
			
			// add paths for attributes
			for (Iterator<AttributeDef> it = node.getAttributeDefs().iterator();it.hasNext();)
			{
				Xpth extended = pathSoFar.addInnerStep("attribute::" + it.next().getName());
				paths.add(extended);
			}			
		}
		catch (MapperException ex) {GenUtil.surprise(ex, "ModelUtil.getDRDPaths");}
		return paths;		
	}
	
	/**
	 * 
	 * @param start a Node in the mapped structure
	 * @param end another Node
	 * @return true if the shortest cross path from the start node to the end node 
	 * can only result in one of the end node
	 */
	public static boolean uniqueTraverse(NodeDef start, NodeDef end)
	{
		boolean unique = true;

		// find the trail of all nodes up from the start node to the root of the mapped structure
		Vector<NodeDef> startAncestors = new Vector<NodeDef>();
		EObject stanc = start;
		while (stanc instanceof NodeDef)
		{
			startAncestors.add((NodeDef)stanc);
			stanc = stanc.eContainer();
		}
		
		/* check if the end node or any of its ancestors is in this trail, 
		 * failing if you encounter any multiple branch going up from the end */
		boolean found = false;
		EObject eanc = end;
		while ((unique) && (!found) && (eanc instanceof NodeDef))
		{
			// if this node is in the ancestors of the start node, succeed
			for (Iterator<NodeDef> it = startAncestors.iterator();it.hasNext();)
				if (it.next().equals(eanc)) found = true;

			// otherwise, if this is a multiple child of its parent, fail
			if ((!found) && (eanc instanceof ElementDef) 
					&& (((ElementDef)eanc).getMaxMultiplicity() == MaxMult.UNBOUNDED)) unique = false;

			// try the parent node
			eanc = eanc.eContainer();
		}
		return unique;		
	}
	
	/**
	 * 
	 * @param m any mapping
	 * @return the Node of the mapped structure which contains it
	 */
	public static NodeDef mappingNode(Mapping m)
	{
		// Node/MappingSet/AssocMapping/AssocEndMapping
		if (m instanceof AssocEndMapping) return (NodeDef)(m.eContainer().eContainer().eContainer());
		// Node/MappingSet/Any other type of mapping
		return (NodeDef)(m.eContainer().eContainer());
	}
	
	/**
	 * @param m any mapping
	 * @return true if the shortest path to its object mapping leads to a unique node
	 * @throws MapperException if there is no Object mapping
	 */
	public static boolean hasUniqueShortestPathToObjectMapping(Mapping m) throws MapperException
	{
			ObjMapping om = getObjectMapping(m);
			return uniqueTraverse(mappingNode(m),mappingNode(om));
	}
	
	/**
	 * follow the XPath path from the node. Return true if the min or max cardinality is one
	 * (max if isMax = true; min if isMax = false)
	 * Throw a Mapper Exception if the path is invalid, 
	 * or does not lead to nodes in the structure
	 */
	static private boolean cardinalityIsOne(NodeDef node, Xpth path, boolean isMax) throws MapperException
	{
		if (path.size() == 0) throw new MapperException("Cannot match empty path");
		
		// 'self' step has min and max cardinality 1 if the node name matches
		if (path.axis(0).equals("self"))
		{
			if (path.nodeName(0).equals("node()")) return true;
			if (path.nodeName(0).equals(node.getName())) return true;
			throw new MapperException("'self' step expected node '" + path.nodeName(0) 
					+ "' but found node '" + node.getName() + "'");
		}

		// 'parent' step does not alter min and max cardinality of the path
		else if (path.axis(0).equals("parent"))
		{
			EObject p = node.eContainer();
			if ((p instanceof NodeDef) && (path.nodeName(0).equals(((NodeDef)p).getName())))
			{
				if (path.size() == 1) return true;
				else return cardinalityIsOne((NodeDef)p,path.removeOuterStep(),isMax);
			}
			else throw new MapperException("Failed to match 'parent' step '" + path.nodeName(0) + "'");
		}
		
		// 'ancestor' step does not alter min and max cardinality of the path
		else if (path.axis(0).equals("ancestor"))
		{
			boolean found = false;
			EObject p = node.eContainer();
			//search up the tree to match the node name
			while ((p instanceof NodeDef) && (!found))
			{
				NodeDef parent = (NodeDef)p;
				found = (path.nodeName(0).equals(parent.getName()));
				if (found)
				{
					if (path.size() == 1) return true;
					else return cardinalityIsOne(parent,path.removeOuterStep(),isMax);					
				}
				p = p.eContainer();
			}
			if (!found) throw new MapperException("Failed to match 'ancestor' step '" + path.nodeName(0) + "'");
		}

		// 'child' step can alter min and max cardinality of the path
		else if (path.axis(0).equals("child"))
		{
			if (!(node instanceof ElementDef)) throw new MapperException("'child' step from Attribute");
			for (Iterator<ElementDef> it = ((ElementDef)node).getChildElements().iterator();it.hasNext();)
			{
				ElementDef child = it.next();
				if (path.nodeName(0).equals(child.getName()))
				{
					if (isMax && (child.getMaxMultiplicity()== MaxMult.UNBOUNDED)) return false;
					else if (!isMax && (child.getMinMultiplicity()== MinMult.ZERO)) return false;
					else if (path.size() == 1) return true;
					else return cardinalityIsOne(child,path.removeOuterStep(),isMax);
				}
			}
			throw new MapperException("Failed to match 'child' step '" + path.nodeName(0) + "'");
		}

		// 'attribute' step can alter min cardinality of the path
		else if (path.axis(0).equals("attribute"))
		{
			if (!(node instanceof ElementDef)) throw new MapperException("'attribute' step from Attribute");
			for (Iterator<AttributeDef> it = ((ElementDef)node).getAttributeDefs().iterator();it.hasNext();)
			{
				AttributeDef at = it.next();
				if (path.nodeName(0).equals(at.getName()))
				{
					if (!isMax && (at.getMinMultiplicity()== MinMult.ZERO)) return false;
					else if (path.size() == 1) return true;
					else throw new MapperException("XPath cannot have steps after 'attribute' step");
				}
			}
			throw new MapperException("Failed to match 'attribute' step '" + path.nodeName(0) + "'");
		}

		// other axes in steps are not supported
		else throw new MapperException("Failed to match axis '" + path.axis(0) + "'");

		return false; // to satisfy the compiler; but inaccessible
	}
	
	/**
	 * @param node a node in the mapped structure
	 * @param path a valid path from the node
	 * @return the max cardinality of the path; 1 or -1 (means unbounded)
	 * @throws MapperException if the path is invalid or does not lead to a node
	 */
	static public int getMaxCardinality(NodeDef node, Xpth path) throws MapperException
	{
		boolean isMax = true;
		if (cardinalityIsOne(node,path, isMax)) return 1;
		else return -1;
	}
	
	/**
	 * @param node a node in the mapped structure
	 * @param path a valid path from the node
	 * @return the min cardinality of the path; 1 or 0
	 * @throws MapperException if the path is invalid or does not lead to a node
	 */
	static public int getMinCardinality(NodeDef node, Xpth path) throws MapperException
	{
		boolean isMax = false;
		if (cardinalityIsOne(node,path, isMax)) return 1;
		else return 0;
	}
	
	//-------------------------------------------------------------------------------------
	//                               Unique identifiers
	//-------------------------------------------------------------------------------------
	
	

    /** Return a Vector of vectors. Each one is a Vector
    of property names which together form a unique identifier for
    objects of the class - including those unique
    identifiers it inherits.*/
    public static Vector<Vector<String>> uniqueIdentifiers(String className, EPackage classModel)
    {
        Vector<Vector<String>> res = new Vector<Vector<String>>();
        if (classModel != null)
        {
        	for (Iterator<EClassifier> it = classModel.getEClassifiers().iterator();it.hasNext();)
        	{
        		EClassifier ec = it.next();
        		if ((ec instanceof EClass) && (ec.getName().equals(className)))
        		{
        			EClass ecc = (EClass)ec;
        			// find unique identifiers of the class itself
    				addIdentifiersForClass(ecc,res);

    				// find unique identifiers of any superclasses
        			for (Iterator<EClass> ix = ecc.getEAllSuperTypes().iterator();ix.hasNext();)
        			{
        				EClass supC = ix.next();
        				addIdentifiersForClass(supC,res);
        			}
        		}
        	}
        }
        return res;
    }
    
    private static void addIdentifiersForClass(EClass supC, Vector<Vector<String>> res)
    {
		for (Iterator<EAnnotation> iz = supC.getEAnnotations().iterator();iz.hasNext();)
		{
			EAnnotation ea = iz.next();
			EMap<String,String> ed =  ea.getDetails();
			for (Iterator<String> ik = ed.keySet().iterator(); ik.hasNext();)
			{
				String key = ik.next();
				if (key.startsWith("identifier")) 
				{
					String ident = ed.get(key);
					StringTokenizer st = new StringTokenizer(ident,", ");
					Vector<String> idv = new Vector<String>();
					while (st.hasMoreTokens()) idv.add(st.nextToken());
					res.add(idv);        								
				}
			}
		}    	
    }
	
	//--------------------------------------------------------------------------------------------
	//                                       annotations
	//--------------------------------------------------------------------------------------------

    private static String mifNamespaceURI = "urn:hl7-org:v3/mif2";
	public static String mifNamespaceURI() {return mifNamespaceURI;}
	
	private static String genModelURI = "http://www.eclipse.org/emf/2002/GenModel";
	public static String genModelURI() {return genModelURI;}
	
	/**
	 * 
	 * @param toObj a Ecore model element to which an annotation is either to be added,
	 * or extended with a new String key and value
	 * @param key the key (new or possibly already existing)
	 * @param value the new value for the key
	 */
	public static void addMIFAnnotation(EModelElement toObj, String key, String value)
	{
		EAnnotation ann = toObj.getEAnnotation(mifNamespaceURI);
		if (ann == null)
		{
			ann = EcoreFactory.eINSTANCE.createEAnnotation();
			ann.setSource(mifNamespaceURI);
			toObj.getEAnnotations().add(ann);			
		}		
		ann.getDetails().put(key, value);
	}
	
	/**
	 * copy MIF annotations from one EObject to another
	 * @param fromObj
	 * @param toObj
	 */
	public static void copyMifAnnotations(EModelElement fromObj,EModelElement toObj)
	{
		copySomeAnnotations(fromObj, toObj,  mifNamespaceURI);
	}
	
	/**
	 * copy annotations with some namespace from one object to another
	 * @param fromObj
	 * @param toObj
	 * @param namespaceURI
	 */
	public static void copySomeAnnotations(EModelElement fromObj,EModelElement toObj,  String namespaceURI)
	{
		EAnnotation ann = fromObj.getEAnnotation(namespaceURI);
		if (ann != null)
		{
			EMap<String,String> details = ann.getDetails();
			Hashtable<String,String> det = new Hashtable<String,String>();
			for (Iterator<Entry<String,String>> it = details.iterator();it.hasNext();)
			{
				Entry<String,String> next = it.next();
				det.put(next.getKey(), next.getValue());
			}

			EAnnotation ann2 = toObj.getEAnnotation(namespaceURI);
			if (ann2 == null)
			{
				ann2 = EcoreFactory.eINSTANCE.createEAnnotation();
				ann2.setSource(namespaceURI);
				toObj.getEAnnotations().add(ann2);			
			}

			for (Enumeration<String> en = det.keys(); en.hasMoreElements();)
			{
				String key = en.nextElement();
				ann2.getDetails().put(key, det.get(key));
			}
		}
	}
	
	/**
	 * 
	 * @param toObj
	 * @param key
	 * @param value
	 */
	public static void removeMIFAnnotation(EModelElement toObj, String key)
	{
		EAnnotation ann = toObj.getEAnnotation(mifNamespaceURI);
		if (ann != null)
		{
			EMap<String,String> details = ann.getDetails();
			if (details.get(key) != null)
			{
				details.removeKey(key);
				if (details.size() == 0) toObj.getEAnnotations().remove(ann);
			}
		}		
	}
	
	
	/**
	 * copy an ECore annotation from one object to another - 
	 * maybe overwriting the values of existing annotations with the same 
	 * source and the same key
	 * @param toObject
	 * @param note
	 */
	public static void copyAnnotation(EModelElement toObject, EAnnotation note)
	{
		String source = note.getSource();

		// if the target does not have an annotation with this source, make one
		EAnnotation existing = toObject.getEAnnotation(source);
		if (existing == null)
		{
			existing = EcoreFactory.eINSTANCE.createEAnnotation();
			existing.setSource(source);
			toObject.getEAnnotations().add(existing);					
		}
		
		// transfer values for all keys, overwriting if values already exist
		for (Iterator<String> it = note.getDetails().keySet().iterator();it.hasNext();)
		{
			String key = it.next();
			existing.getDetails().put(key, note.getDetails().get(key));
		}
	}

	
	/**
	 * @param toObj an Ecore model element
	 * @param key the key to an annotation detail
	 * @return the value for that key, or null if there is none
	 */
	public static String getMIFAnnotation(EModelElement toObj, String key)
	{
		String value = null;
		EAnnotation ann = toObj.getEAnnotation(mifNamespaceURI);
		if (ann != null) value = ann.getDetails().get(key);
		return value;
	}

	/**
	 * 
	 * @param toObj an Ecore model element
	 * @return the details of the MIF annotation
	 */
	public static EMap<String,String> getMIFAnnotationDetails(EModelElement toObj)
	{
		EMap<String,String> details = new BasicEMap<String,String>();
		EAnnotation ann = toObj.getEAnnotation(mifNamespaceURI);
		if (ann != null) details = ann.getDetails();
		return details;
	}
	

	
    
    /**
     * @param el an EModelObject in an ecore model
     * @param key the key of the annotation detail
     * @return one detail of an annotation to an EObject, by key
     */
    public static String getEAnnotationDetail(EModelElement el, String key)
    {
    	String detail = null;
		for (Iterator<EAnnotation> iz = el.getEAnnotations().iterator();iz.hasNext();)
		{
			EAnnotation ea = iz.next();
			if (detail == null) detail = ea.getDetails().get(key);
		}    	
    	return detail;
    }
    
   /**
     * remove all EReferences from a class, leaving its EAttributes unchanged
     * @param theClass
     */
    static public void removeEReferences(EClass theClass)
    {
    	Vector<EAttribute> atts = new Vector<EAttribute>();
    	for (Iterator<EAttribute> ia = theClass.getEAttributes().iterator();ia.hasNext();)
    		atts.add(ia.next());
    	theClass.getEStructuralFeatures().clear();
    	for (Iterator<EAttribute> ia = atts.iterator();ia.hasNext();)
    		theClass.getEStructuralFeatures().add(ia.next());
    }
    
	
	/**
	 * @param node any node in the mapping set
	 * @param className a class name
	 * @return true if the class is one of the parameter classes of the mapping set
	 * Appears not to be used
	 */
	public static boolean isParameterClassx(EObject node, String className)
	{
		boolean isPar = false;
		MappedStructure root= getMappedStructure(node);
		for (Iterator<ParameterClass> it = root.getParameterClasses().iterator(); it.hasNext();)
			if (it.next().getClassName().equals(className)) isPar = true;
		return isPar;
	}

	
	/**
	 * the association name is now calculated automatically by
	 * concatenating the association end names; or if they only differ 
	 * by a final '_1' and '_2', by removing the final '_1' and '_2'
	 * @param role1 the role name of end 1 (the lexically first role name)
	 * @param role2 the role name of end 2 (lexically last role name)
	 * @return the association name
	 */
	public static String assocName(String r1, String r2)
	{
		String role1 = r1;
		String role2 = r2;
		// unexpected cases when either role is missing
		if (role1 == null) return role2;
		if (role2 == null) return role1;
		
		// if either role is non-navigable (role name ""), the association name is the other role name
		if (role1.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME)) return role2;
		if (role2.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME)) return role1;

		// reverse the roles if necessary, to ensure role1 is lexically first
		if (role1.compareTo(role2) > 0)
		{
			String r = role1;
			role1 = role2;
			role2 = r;
		}
		
		String assocName = role1 + "|" + role2; 

		/* legacy case; for mapping sets which had association names but no role names,
		 * give them role names starting with the association name, ending in '_1' and '_2' 
		 * and reconstruct the association name from them */
		if ((role1.length() == role2.length()) && 
			(role1.length() > 2) &&
			(role1.endsWith("_1")) && 
			(role2.endsWith("_2")))
		{
			String t1 = role1.substring(0,role1.length() - 2);
			String t2 = role2.substring(0,role2.length() - 2);
			if (t1.equals(t2)) assocName = t2;
		}
		return assocName;		
	}
	
	/**
	 * from two association role names, return the role that will 
	 * point to end 1 under the association naming and end convention
	 * @param r1 a role name
	 * @param r2 the inverse association role name
	 * @return the role which is lexically first
	 */
	public static String end1Role(String r1, String r2)
	{
		// if r2 is lexically first, return it
		String role1 = r1;
		if (r1.compareTo(r2) > 0) role1 = r2;

		/* if either r1 or r2 is non-navigable (nonexistent in the Ecore model,
		 * the navigable role points to end 2 */
		if (r2.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME)) role1 = r2;
		if (r1.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME)) role1 = r1;

		return role1;
	}
	
	/**
	 * from two association role names, return the role that will 
	 * point to end 2 under the association naming and end convention
	 * @param r1 a role name
	 * @param r2 the inverse association role name
	 * @return the role which is lexically last
	 */
	public static String end2Role(String r1, String r2)
	{
		// if r2 is lexically first, return r1
		String role2 = r2;
		if (r1.compareTo(r2) > 0) role2 = r1;

		/* if either r1 or r2 is non-navigable (nonexistent in the Ecore model,
		 * the non-navigable role points to end 1 */
		if (r2.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME)) role2 = r1;
		if (r1.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME)) role2 = r2;

		return role2;
	}
	
	/**
	 * @param className
	 * @param packageName
	 * @return class name preceded by the package name and '.', if the package name is not empty
	 */
	public static String getQualifiedClassName(String className,String packageName)
	{
		String qName = className;
		if ((packageName != null) && (!packageName.equals(""))) qName = packageName + "." + className;
		return qName;
	}

	
	/**
	 * return an association name given only one role name, as in class1.role.class2.
	 * The association name is the role name if the reference has no opposite.
	 * @param class1
	 * @param role
	 * @param class2
	 * @param node
	 * @return the association name calculated automatically, or null if there is any problem
	 */
	public static String getAssociationName(String qualifiedClassName1,String role,
			String qualifiedClassName2,  EObject node)
	{
		String class1 = getBareClassName(qualifiedClassName1);
		String packageName1 = getPackageName(qualifiedClassName1);
		String class2 = getBareClassName(qualifiedClassName2);
		String packageName2 = getPackageName(qualifiedClassName2);

		EPackage package1 = getEPackage(packageName1,node);
		EPackage package2 = getEPackage(packageName2,node);
		return getAssociationName(class1, package1, role, class2, package2);
	}
	
	/**
	 * return an association name given only one role name, as in class1.role.class2.
	 * The association name is the role name if the reference has no opposite.
	 * @param class1
	 * @param role
	 * @param class2
	 * @param node
	 * @return the association name calculated automatically, or null if there is any problem
	 */
	public static String getAssociationName(String class1,EPackage package1, String role, String class2, EPackage package2)
	{
		String assocName = role; // remains so if there is no inverse or if it is non-navigable
		if (role.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME))
			System.out.println("Cannot find an inverse role to '" + 
					MappableAssociation.NON_NAVIGABLE_ROLE_NAME + "' because there might be several");
		String invRole = getInverseRole(class1, package1,role, class2, package2);
		if ((invRole != null)&& (!(invRole.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME))))
			assocName = assocName(role,invRole);
		return assocName;
	}
	
	/**
	 * @param qualifiedClassName1 the start class
	 * @param role the role name to get to the target class
	 * @param qualifiedClassName2 the target class
	 * @param node any node in the mapped structure referring to the class model
	 * @return the inverse role name, or null if there is any problem
	 */
	public static String getInverseRole(String qualifiedClassName1,  String role, String qualifiedClassName2, EObject node)
	{
		String class1 = getBareClassName(qualifiedClassName1);
		String packageName1 = getPackageName(qualifiedClassName1);
		EPackage package1 = getEPackage(packageName1,node);
		String class2 = getBareClassName(qualifiedClassName2);
		String packageName2 = getPackageName(qualifiedClassName2);
		EPackage package2 = getEPackage(packageName2,node);
		
		return getInverseRole(class1, package1, role, class2, package2);
	}

	
	/**
	 * @param class1
	 * @param package1
	 * @param role
	 * @param class2
	 * @param package2
	 * @return
	 */
	public static String getInverseRole(String class1, EPackage package1, 
			String role, String class2, EPackage package2)
	{

		String invRole = MappableAssociation.NON_NAVIGABLE_ROLE_NAME;
		if ((package1 == null)|(package2 == null)) return invRole;
		EClass c1 = (EClass)package1.getEClassifier(class1);
		EClass c2 = (EClass)package2.getEClassifier(class2);
		if ((c1 == null)|(c2 == null)) return invRole;
		else if (!(role.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME)))
		{
			for (Iterator<EReference> it = c1.getEAllReferences().iterator(); it.hasNext();)
			{
				EReference er = it.next();
				EClass c2s = er.getEReferenceType();
				if ((er.getName()!= null) && (er.getName().equals(role)) && (c2s != null) && (c2s.isSuperTypeOf(c2)))
				{
					EReference inverse = er.getEOpposite();
					if (inverse != null) // usual case
					{
						invRole= inverse.getName();
					}
					else // no EOpposite; non-navigable role
					{
						invRole = MappableAssociation.NON_NAVIGABLE_ROLE_NAME;
					}
				}
			}
		}
		else if (role.equals(MappableAssociation.NON_NAVIGABLE_ROLE_NAME))
		{
			System.out.println("Cannot yet get the name of the inverse role to '"
					+ MappableAssociation.NON_NAVIGABLE_ROLE_NAME + "'");
		}
		return invRole;
	}

    /**
     * Extract a role name for end 0 or 1 from an association name, according to the automatic
     * naming of associations. But does not support the legacy asscoaition naming, 
     * and in any case this method is not used - thus the wierd name
     * @param assocName
     * @param end01
     * @return
     */
	public static String roleNameX(String assocName, int end01) throws MapperException
    {
		if ((end01 < 0)|(end01 >1)) throw new MapperException ("Invalid end: " + end01);
    	StringTokenizer st = new StringTokenizer(assocName,"|");
    	if (st.countTokens() == 2) // usual case
    	{
    		String[] role = new String[2];
    		role[0] = st.nextToken();
    		role[1] = st.nextToken();
    		// if role[0] is lexically after role[1], reverse ends
    		if (role[0].compareTo(role[1]) > 0) return role[1-end01];
    		return role[end01];
    	}
    	else if (st.countTokens() == 1) 
    	{
    		/* this method used to add '_1' or '_2' to the end to support the legacy 
    		 * convention or naming roles. Now it no longer does - it supports the new convention
    		 * that when one end is non-navigable, the association name is the navigable
    		 * role name, and the navigable role leads to end 1 of (1,2) i.e
    		 * to end 0 of (0,1). But since this method is not used, it all does not matter.*/
    		if (end01 == 0) return assocName;
    		if (end01 == 1) return MappableAssociation.NON_NAVIGABLE_ROLE_NAME;
    	}
    	else throw new MapperException ("Invalid association name: " + assocName);
    	return ""; // to satisfy the compiler
    }
	
	//-------------------------------------------------------------------------------------------
	//                     handling nested packages and qualified class names
	//-------------------------------------------------------------------------------------------
    
    /**
     * get a named class from the ECore class model
     * @param qualifiedClassName the class name, preceded by the package name and '.' if non-empty
     * @return
     */
    public static EClass getNamedClass(EPackage topPackage, String qualifiedClassName)
    {
    	String packageName = getPackageName(qualifiedClassName);
    	EPackage thePackage = getNamedPackage(topPackage,packageName);
    	String className = getBareClassName(qualifiedClassName);
    	if (thePackage != null) return getEClass(thePackage, className);
    	else return null;
    }
	
	/**
	 * @param className
	 * @return the named class
	 */
	public static EClass getEClass(EPackage thePackage, String className)
	{
		if (thePackage == null) return null;
		EClassifier ec = thePackage.getEClassifier(className);
		if ((ec != null) && (ec instanceof EClass)) return (EClass)ec;
		else return null;
	}
	
	/**
	 * 
	 * @param theClass
	 * @param attName the name of an EAttribute, which may be inherited
	 * @return the EAttribute, or null if there is none of that name
	 */
	public static EAttribute getNamedAttribute(EClass theClass, String attName)
	{
		EAttribute att = null;
		for (Iterator<EAttribute> it = theClass.getEAllAttributes().iterator();it.hasNext();)
		{
			EAttribute a = it.next();
			if (a.getName().equals(attName)) att = a;
		}
		return att;
	}
	
    /**
     * 
     * @param topPackage
     * @param packageName name of the required package; must not be null
     * @return the top package or a sub-package within it with the required name;
     * null package name matches with "".
     */
    public static EPackage getNamedPackage(EPackage topPackage, String packageName)
    {
    	EPackage thePackage = null;
    	if (packageName.equals(topPackage.getName())) thePackage = topPackage;
    	if ((packageName.equals("")) && (topPackage.getName()== null))
    		thePackage = topPackage;
    	for (Iterator<EPackage> it = topPackage.getESubpackages().iterator();it.hasNext();)
    	{
    		EPackage child = it.next();
    		if (thePackage == null) thePackage = getNamedPackage(child,packageName);
    	}
    	return thePackage;
    }
    
    /**
     * @param qualifiedClassName the class name, preceded by the package name and '.' if non-empty
     * @return the package name, or "" if there is none
     */
    public static String getPackageName(String qualifiedClassName)
    {
    	String packageName = "";
    	StringTokenizer st = new StringTokenizer(qualifiedClassName,".");
    	// if there are any full stops, return the string before the first
    	if (st.countTokens() > 1) packageName = st.nextToken();
    	return packageName;
    }
    
    /**
     * @param qualifiedClassName the class name, preceded by the package name and '.' if non-empty
     * @return the package name, or "" if there is none
     */
    public static String getBareClassName(String qualifiedClassName)
    {
    	String cName = qualifiedClassName;
    	String packageName = getPackageName(qualifiedClassName);
    	// if there are any full stops, remove the package name and the first full stop
    	if (!packageName.equals("")) cName = qualifiedClassName.substring(packageName.length() + 1);
    	return cName;
    }
    
    /**
     * 
     * @param theClass an EClass
     * @return the class name, preceded by the package name and '.' if nonempty
     */
    public static String getQualifiedClassName(EClass theClass)
    {
    	String qName = theClass.getName();
    	String packageName = theClass.getEPackage().getName();
    	if ((packageName != null) && (!packageName.equals(""))) 
    		qName = packageName + "." + qName;
    	return qName;
    }
    
    /**
     * @param theClass an EClass
     * @return the outermost package, containing the class either directly
     * or in a sub-package
     * 
     */
    public static EPackage getTopPackage(EClass theClass)
    {
    	EPackage direct = theClass.getEPackage();
    	while (direct.getESuperPackage() != null) direct = direct.getESuperPackage();
    	return direct;
    }
    
    /**
     * 
     * @param pack a package
     * @return a Hashtable whose key = qualified class name
     * and value = a Vector of qualified class names of all classes that directly 
     * contain that class by a containment relation
     */
    public static Hashtable<String,Vector<String>> parentTable(EPackage pack)
    {
    	Hashtable<String,Vector<String>> pTable = new Hashtable<String,Vector<String>>();
		for (Iterator<EClass> it = getAllClasses(pack).iterator(); it.hasNext();)
		{
			EClass next = it.next();
			String parentName = getQualifiedClassName(next);
			for (Iterator<EReference> ir = next.getEAllReferences().iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				if (ref.isContainment())
				{
					EClassifier contained = ref.getEType();
					if (contained instanceof EClass)
					{
						String childName = getQualifiedClassName((EClass)contained);
						Vector<String> parents = pTable.get(childName);
						if (parents == null) parents = new Vector<String>();
						parents.add(parentName);
						pTable.put(childName, parents);
					}
				}
			}
		}
    	return pTable;
    }

    
    /**
     * @param thePackage a package
     * @return a Vector of all classes in the package or its sub-packages to any depth
     */
    public static Vector<EClass> getAllClasses(EPackage thePackage)
    {
    	Vector<EClass> classes = new Vector<EClass>();
    	addClasses(classes,thePackage);
    	return classes;
    }
    
    private static void addClasses(Vector<EClass> classes, EPackage thePackage)
    {
    	for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator();it.hasNext();)
    	{
    		EClassifier next = it.next();
    		if (next instanceof EClass) classes.add((EClass)next);
    	}
    	
    	for (Iterator<EPackage> it = thePackage.getESubpackages().iterator();it.hasNext();)
    		addClasses(classes,it.next());    	
    }

	
	/**
	 * 
	 * @param qualifiedClassName the package name (if not null) and class name of some class
	 * @param classModel the top package of the class model
	 * @return a Vector of qualified names of all subclasses, including itself, in any package
	 * @throws MapperException
	 */
    public static Vector<String> allSubclassNames(String qualifiedClassName, EPackage classModel) throws MapperException
	{
    	EClass theClass = null;
    	String className = null;
    	String packageName = null;

    	/* if the class name contains '.', there must be a non-null package; if not,
    	 * the first part of the class name would be taken as a package name. */
    	StringTokenizer st = new StringTokenizer(qualifiedClassName,".");
    	if (st.countTokens() == 1) className = st.nextToken();
    	else if (st.countTokens() > 1) 
    	{
    		packageName = st.nextToken(); // before the first '.'
    		className = qualifiedClassName.substring(packageName.length()+1); // after
    	}

    	if (packageName== null) theClass = (EClass)classModel.getEClassifier(className);
    	else if (packageName.equals(classModel.getName())) theClass = (EClass)classModel.getEClassifier(className);
    	else 
    	{
    		EPackage thePackage = getNamedPackage(classModel,packageName);
    		theClass = (EClass)thePackage.getEClassifier(className);    	
    	}
    	if (theClass == null) throw new MapperException("Cannot find class " + qualifiedClassName);
    	
    	// find the names of all subclasses, including itself
    	Vector<EClass> inheritors = getAllSubClasses(theClass);
    	Vector<String> subclassNames = new Vector<String>();
    	for (Iterator<EClass> it = inheritors.iterator();it.hasNext();) 
    		subclassNames.add(getQualifiedClassName(it.next()));
    	
    	return subclassNames;
		
	}
	
	/**
	 * 
	 * @param ec a class
	 * @return a Vector of all classes which inherit from the class, 
	 * including the class itself
	 */
	public static Vector<EClass> getAllSubClasses(EClass ec)
	{
		Vector<EClass> inheritors = new Vector<EClass>();
		EPackage ep = getTopPackage(ec);
		for (Iterator<EClass> it = getAllClasses(ep).iterator(); it.hasNext();)
		{
			EClass next = it.next();
			// isSuperTypeOf includes the class itself
			if (ec.isSuperTypeOf(next)) inheritors.add(next);
		}
		return inheritors;
	}
	/**
	 * 
	 * @param ec a class
	 * @return a Vector of all concrete classes which inherit from the class, 
	 * including the class itself
	 */
	public static Vector<EClass> getAllConcreteSubClasses(EClass ec)
	{
		Vector<EClass> inheritors = new Vector<EClass>();
		EPackage ep = getTopPackage(ec);
		for (Iterator<EClass> it = getAllClasses(ep).iterator(); it.hasNext();)
		{
			EClass next = it.next();
			// isSuperTypeOf includes the class itself
			if ((ec.isSuperTypeOf(next)) && (!next.isAbstract())) inheritors.add(next);
		}
		return inheritors;
	}
	
	/**
	 * 
	 * @param topPackage a package which may contain nested packages
	 * @param bareClassName an unqualified class name (with no package name)
	 * @return a list of all classes in the top package or its nested packages
	 * which have the required bare class name
	 */
	public static List<EClass> getAllNamedClasses(EPackage topPackage, String bareClassName)
	{
		Vector<EClass> classes = new Vector<EClass>();
		
		addNamedClasses(topPackage,bareClassName,classes);
		return classes;
	}
	
	/**
	 * recursive descent of nested packages, looking for all classes with a given name
	 * @param topPackage
	 * @param bareClassName
	 * @param classes
	 */
	private static void addNamedClasses(EPackage topPackage,String bareClassName,Vector<EClass> classes)
	{
		EClassifier named = topPackage.getEClassifier(bareClassName);
		if ((named != null) && (named instanceof EClass)) classes.add((EClass)named);
		for (Iterator<EPackage> it = topPackage.getESubpackages().iterator();it.hasNext();)
			addNamedClasses(it.next(),bareClassName,classes);
	}

	
	
	//---------------------------------------------------------------------------------
	//                     cloning EObjects
	//---------------------------------------------------------------------------------
	
	/**
	 * @param obj an object in the Mapper model
	 * @return a clone of any EObject in the Mapper model, including all of its EAttributes
	 * and recursively its contained EObjects if deep = true. Non-containment relations are not 
	 * yet supported.
	 */
	public static EObject EClone(EObject obj, boolean deep)
	{
		if (obj == null) return null;

		// clone the EObject
		EObject clone = MapperFactory.eINSTANCE.create(obj.eClass());
		
		// clone the values of all its EAttributes
		for (Iterator<EAttribute> it = obj.eClass().getEAllAttributes().iterator();it.hasNext();)
		{
			EAttribute att = it.next();
			clone.eSet(att, obj.eGet(att));
		}
		
		// for deep copy, clone all its containment EReferences, and their target EObjects
		if (deep) for (Iterator<EReference> it = obj.eClass().getEAllReferences().iterator();it.hasNext();)
		{
			EReference ref = it.next();
			if (ref.isContainment())
			{
				if (ref.getUpperBound() == 1)
				{
					Object target = obj.eGet(ref);
					if ((target != null) && (target instanceof EObject))
						clone.eSet(ref, EClone((EObject)target,deep));
				}
				else if (ref.getUpperBound() == -1)
				{
					Object targets = obj.eGet(ref);
					if ((targets != null) && (targets instanceof EList<?>))
					{
						EList<EObject> cont = new BasicEList<EObject>();
						EList<?> targetList = (EList<?>)targets;
						for (Iterator<?> iw = targetList.iterator();iw.hasNext();)
						{
							Object target = iw.next();
							if (target instanceof EObject) cont.add(EClone((EObject)target,deep));								
						}
						clone.eSet(ref,cont);
					}
				}					
			}
		}
		return clone;
	}
	
	//------------------------------------------------------------------------------------------
	//                           Making and saving Ecore models
	//------------------------------------------------------------------------------------------
	/**
	 * write out an Ecore package as a resource.
	 */
	public static void savePackage(String filePath, EPackage topPackage) throws MapperException
	{
		URI instanceURI = URI.createURI(filePath);
		
		// create the resource that is to be filled with the model instance
		ResourceSet resourceSet = new ResourceSetImpl();
		// register the factory
		if (instanceURI != null)
		{
			String extension = ".ecore";
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
				put(extension, new XMIResourceFactoryImpl());
			
		}
		Resource modelResource = resourceSet.createResource(instanceURI);

		// add the package to the resource
		modelResource.getContents().add(topPackage);

		// save the resource
			try {modelResource.save(null);}
			catch (IOException ex) 
			   {throw new MapperException("Failed to save EMF model resource: " + ex.getMessage());}
	}
	
	/**
	 * create a model object
	 * @param qualifiedClassName
	 * @param classModel
	 * @return
	 */
	public static EObject createModelObject(String qualifiedClassName, EPackage classModel) throws MapperException
	 {
		String packageName = ModelUtil.getPackageName(qualifiedClassName);
		EPackage thePackage = ModelUtil.getNamedPackage(classModel, packageName);
		EClass theClass = ModelUtil.getNamedClass(classModel, qualifiedClassName);
		if (theClass != null) 
		{			
			if (!theClass.isAbstract()) return thePackage.getEFactoryInstance().create(theClass);
			else throw new MapperException("Abstract class " + qualifiedClassName);
		}
		else throw new MapperException("No EClass " + qualifiedClassName + " in package " + classModel.getNsPrefix()); 
	 }


	
	//------------------------------------------------------------------------------------------
	//                           Making and saving mapping sets
	//------------------------------------------------------------------------------------------
	
	/**
	 * save a mapping set
	 * @param resource a fully constructed mapping set
	 * @throws MapperException
	 */
	public static MappedStructure saveNewMappingSet(String location) throws MapperException
	{
		  try{
			    URI mappingSetURI = URI.createURI(location);
			    Resource resource = makeNewMappingSet(mappingSetURI);
				// Save the contents of the resource to the file system.
				Map<Object, Object> options = new HashMap<Object, Object>();
				options.put(XMLResource.OPTION_ENCODING, "UTF-8");
				resource.save(options);
				return (MappedStructure) resource.getContents().get(0);
		}
		catch (Exception ex) {throw new MapperException(ex.getMessage());}
	}
	
	/**
	 * @param fileURI the URI where a mapping set is to be located
	 * @return the mapping set resource (with no structure yet)
	 */
	public static Resource makeNewMappingSet(URI fileURI)
	{
		// Create a resource set and a resource for this file.
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(fileURI);

		// Add the initial model object to the contents.
		EObject rootObject = createInitialModel();
		resource.getContents().add(rootObject); 
		
		return resource;
	}
	
	/**
	 * save a mapping set
	 * @param resource a fully constructed mapping set
	 * @throws MapperException
	 */
	public static void saveMappingSet(Resource resource) throws MapperException
	{
		  try{
				// Save the contents of the resource to the file system.
				Map<Object, Object> options = new HashMap<Object, Object>();
				options.put(XMLResource.OPTION_ENCODING, "UTF-8");
				resource.save(options);
			  }
			  catch (Exception ex) {throw new MapperException(ex.getMessage());}				
	}

	/**
	 * @return an initial empty mapping set
	 */
	public static MappedStructure createInitialModel() {
		MappedStructure rootObject = MapperFactory.eINSTANCE.createMappedStructure();
		GlobalMappingParameters globalObject = MapperFactory.eINSTANCE.createGlobalMappingParameters();
		rootObject.setMappingParameters(globalObject);
		return rootObject;
	}

    
    /**
     * @param reps a Vector of objectReps
     * @return the corresponding Vector of objectTokens
     */
    public static Vector<objectToken> toTokens(Vector<objectRep> reps)
    {
    	Vector<objectToken> toks = new Vector<objectToken>();
    	for (Iterator<objectRep> ir = reps.iterator(); ir.hasNext();) toks.add(ir.next());
    	return toks;
    }
    
    /**
     * @param toks a Vector of objectTokens
     * @return the Vector cast to objectReps
     * @throws MapperException if any cannot be cast
     */
    public static Vector<objectRep> toReps(Vector<objectToken> toks)
    throws MapperException
    {
    	Vector<objectRep> reps = new Vector<objectRep>();
    	try 
    	{
        	for (Iterator<objectToken> it = toks.iterator(); it.hasNext();) reps.add((objectRep)it.next());    		
    	}
    	catch (Exception ex) {throw new MapperException("objectToken is not an objectRep");}
    	return reps;
    }

    
    /**
     * @param tok an objectToken
     * @return the token cast to an objectRep
     * @throws MapperException if it cannot be cast
     */
    public static  objectRep toRep(objectToken tok) throws MapperException
    {
    	objectRep rep = null;
    	try {rep = (objectRep)tok;}
    	catch (Exception ex) {throw new MapperException("objectToken is not an objectRep");}
    	return rep;    	
    }
    
    //----------------------------------------------------------------------------------------------------------
    //                                              tracing
    //----------------------------------------------------------------------------------------------------------
    
	
	public static void traceRefNames(EPackage thePackage, String className)
	{
		for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
				if ((next.getName().equals(className)) && (next instanceof EClass))
					traceRefNames((EClass)next);
		}
	}
    
    public static void traceRefNames(EClass theClass)
	{
		String refNames = "";
		for (Iterator<EReference> it = theClass.getEReferences().iterator(); it.hasNext();)
			refNames = refNames + it.next().getName() + " ";
		trace("Class " + theClass.getName() + " has associations " + refNames);
	}
	
	
	public static void traceAttNames(EClass theClass)
	{
		String refNames = "";
		for (Iterator<EAttribute> it = theClass.getEAttributes().iterator(); it.hasNext();)
			refNames = refNames + it.next().getName() + " ";
		trace("Class " + theClass.getName() + " has attributes " + refNames);
	}
	

	/**
	 * RIM class structure
	 */

	public static String[] ActSubclasses = 
		{"Act","Procedure","Observation","PatientEncounter","SubstanceAdministration","Organizer","Supply"};

	public static String[] RoleSubclasses = {"Role"};
	
	
	public static String addSuffixToFileName(String fullLocation,String suffix) throws MapperException
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



	
	private static void trace(String s) {System.out.println(s);}


    

}
