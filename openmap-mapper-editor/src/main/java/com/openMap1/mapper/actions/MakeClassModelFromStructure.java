package com.openMap1.mapper.actions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.views.WorkBenchUtil;

public class MakeClassModelFromStructure extends MapperActionDelegate implements IObjectActionDelegate {
	
	private EPackage topPackage;
	private EPackage mainPackage;
	private Hashtable<String,EClass> allClasses;
	private StructureDefinition structureDef;
	private MappedStructure mappedStructure;

	public void run(IAction action) 
	{
		try 
		{
			mappedStructure = mappedStructure();
			structureDef = mappedStructure.getStructureDefinition();
			if (structureDef == null) throw new MapperException("There is no structure definition to expand");

			String modelLocation = makeInitialModel();
			allClasses = new Hashtable<String,EClass>();

			ElementDef rootNode = mappedStructure.getRootElement();
			rootNode.setExpanded(true);
			ElementDef newRoot = rootNode;
			// automatic expansion of all nodes can make the mapping set too big
			newRoot = expandAllNodes(rootNode);

			descendStructureTree(null,null,newRoot);
			mappedStructure.setRootElement(newRoot);

			// save the results
			ModelUtil.savePackage(modelLocation, topPackage);
			mappedStructure.setUMLModelURL(modelLocation);
			ModelUtil.saveMappingSet(mappedStructure.eResource());
		}
		catch (Exception ex)
		{
			WorkBenchUtil.showMessage("Error",ex.getMessage());
			ex.printStackTrace();			
		}
	}
	
	/**
	 * expand all nodes in the tree structure
	 * @param node
	 * @return the node expanded, with all its descendants expanded
	 * @throws MapperException
	 */
	private ElementDef expandAllNodes(ElementDef node)  throws MapperException
	{
		//FIXME - deal with recursive nesting of nodes.
		if (!node.isExpanded()) 
		{
			ElementDef newNode = structureDef.typeStructure(node.getType()); 
			if (newNode == null) 
				throw new MapperException("No structure for node " + node.getName() + " of type " + node.getType());

			// two-stage replacement to avoid concurrent modification exception
			Vector<ElementDef> newChildren = new Vector<ElementDef>();
			for (Iterator<ElementDef> it = newNode.getChildElements().iterator();it.hasNext();)
			{
				ElementDef child = it.next();
				newChildren.add(expandAllNodes(child));
			}
			for (Iterator<ElementDef> it = newChildren.iterator();it.hasNext();)
				node.getChildElements().add(it.next());

			for (Iterator<AttributeDef> it = newNode.getAttributeDefs().iterator();it.hasNext();)
			{
				AttributeDef att = it.next();
				node.getAttributeDefs().add(att);
			}
			node.setExpanded(true);
		}		

		else if (node.isExpanded()) 
		{
			for (Iterator<ElementDef> it = node.getChildElements().iterator();it.hasNext();)
			{
				ElementDef child = it.next();
				expandAllNodes(child);
			}
		}
		return node;
	}
	
	
	/**
	 * recursive descent of the mapped structure tree, making EClasses, 
	 * EReferences and EAttributes as appropriate
	 *
	 * @param ParentClass class represented by the parent node of this node
	 * @param node  current node in the tree
	 */
	private void descendStructureTree(EClass parentClass, String parentSubset, ElementDef node) throws MapperException
	{

		// add a node mapping set 
		NodeMappingSet nodeMaps = MapperFactory.eINSTANCE.createNodeMappingSet();
		node.setNodeMappingSet(nodeMaps);

		/* if this node has child nodes, make a class and an association 
		 * from class represented by the parent node; and descend */
		if (node.getChildElements().size() > 0)
		{
			// make the class if it does not exist already
			String className = makeClassName(node.getName());
			EClass aClass = allClasses.get(className);
			if (aClass == null)
			{
				aClass = EcoreFactory.eINSTANCE.createEClass();
				aClass.setName(className);
				mainPackage.getEClassifiers().add(aClass);
				allClasses.put(className,aClass);
				// ensure the top class will be the root of the tree in the class model view
				if (parentClass == null) ModelUtil.addMIFAnnotation(aClass, "entry", "true");										
			}
			
			// make and add an object mapping
			ObjMapping om = MapperFactory.eINSTANCE.createObjMapping();
			om.setMappedClass(className);
			om.setMappedPackage(aClass.getEPackage().getName());
			String qualifiedClassName = ModelUtil.getQualifiedClassName(aClass);
			String subset = AddMapperEditorActions.nextSubset(mappedStructure, qualifiedClassName);
			om.setSubset(subset);
			nodeMaps.getObjectMappings().add(om);
			
			// make the association from the parent class, if it exists
			if (parentClass != null)
			{
				String refName = className.toLowerCase();
				EReference newRef = EcoreFactory.eINSTANCE.createEReference();
				newRef.setName(refName);
				newRef.setContainment(true);
				newRef.setEType(aClass);
				newRef.setLowerBound(node.getMinMultiplicity().getValue());
				newRef.setUpperBound(node.getMaxMultiplicity().getValue());
				parentClass.getEStructuralFeatures().add(newRef);
				
				// add an association mapping
				MappableAssociation mass =  new MappableAssociation(parentClass,parentSubset, aClass,subset,newRef,true);
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

			// recursive descent
			for (Iterator<ElementDef> it = node.getChildElements().iterator(); it.hasNext();)
			{
				ElementDef child = it.next();
				descendStructureTree(aClass,subset,child);
			}				
			
		}
		
		/* if this node has no child nodes, make it an EAttribute of the class 
		 * represented by the parent node; warn if it has multiplicity > 1 */
		else if (node.getChildElements().size() == 0)
		{
			if (parentClass == null) throw new MapperException("Cannot make a class model with only one node");
			String attName = makeClassName(node.getName()).toLowerCase();
			EAttribute theAtt = EcoreFactory.eINSTANCE.createEAttribute();
			theAtt.setName(attName);
			theAtt.setEType(EcorePackage.eINSTANCE.getEString()); 
			theAtt.setLowerBound(node.getMinMultiplicity().getValue());
			parentClass.getEStructuralFeatures().add(theAtt);
			
			// create and add the property mapping
			PropMapping pm = MapperFactory.eINSTANCE.createPropMapping();
			pm.setMappedClass(parentClass.getName());
			pm.setMappedPackage(parentClass.getEPackage().getName());
			pm.setMappedProperty(attName);
			pm.setSubset(parentSubset);
			nodeMaps.getPropertyMappings().add(pm);		
		}
	}
	
	private String makeClassName(String nodeName)
	{
		String className = "";
		StringTokenizer st = new StringTokenizer(nodeName,".");
		while (st.hasMoreTokens())
		{
			className = className + st.nextToken();
			if (st.hasMoreTokens()) className = className + "_";
		}
		return className;
		
	}

	
	private String makeInitialModel() throws MapperException
	{
		//  make an empty simplified Ecore model
		topPackage = EcoreFactory.eINSTANCE.createEPackage();
		topPackage.setName("top");
		// ensure this class model will be viewed tree-like, as if it were an RMIM in the class model view
		ModelUtil.addMIFAnnotation(topPackage, "RMIM", "true");	

		// add the main package which all the classes will be in
		mainPackage = EcoreFactory.eINSTANCE.createEPackage();
		mainPackage.setName("main");
		ModelUtil.addMIFAnnotation(mainPackage, "name", "main");	
		topPackage.getESubpackages().add(mainPackage);

		// save the empty Ecore model
		String mappingSetLocation = mappedStructure.eResource().getURI().toString();
		StringTokenizer st = new StringTokenizer(mappingSetLocation,".");
		// FIXME - should really put the class model in the ClassModel folder of the project
		String modelLocation = st.nextToken() + ".ecore";
		ModelUtil.savePackage(modelLocation, topPackage);
		return modelLocation;
	}

}
