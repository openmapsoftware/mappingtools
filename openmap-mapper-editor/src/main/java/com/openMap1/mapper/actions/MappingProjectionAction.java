package com.openMap1.mapper.actions;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.ValueCondition;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

public class MappingProjectionAction extends Action implements IAction{

	
	// the current mapper editor, with a full CDA mapping set open
	private MapperEditor mapperEditor;
	
    // the source mapped structure, open in the mapper editor (a full CDA mapped onto a green class model)
	private MappedStructure sourceMappedStructure;

    // the middle mapping structure, identified by the user (a green CDA, mapped onto some other class model such as FHIR)
	private MappedStructure middleMappedStructure;

    // the result mapping structure, holding the projected mappings
	private MappedStructure resultMappedStructure;

	public MappingProjectionAction()
	{
		super("Make Mapping Projections");
	}

	/**
	 * perform the menu action
	 */
	public void run()
	{
		mapperEditor = WorkBenchUtil.getOpenMapperEditor();
			if (mapperEditor != null)
			{
				sourceMappedStructure = WorkBenchUtil.mappingRoot(mapperEditor);
				if (sourceMappedStructure.getRootElement() != null) try
				{
					// (1) get a location for the middle green mapping set from the user
				    String[] modelExts= {"*.mapper"};
					final String mappingSetPath = FileUtil.getFilePathFromUser(mapperEditor,modelExts,"Select mappings from green XML to other class model",false);
					if (mappingSetPath.equals("")) return;
					URI mappingSetURI = FileUtil.URIFromPath(mappingSetPath);					
					middleMappedStructure = FileUtil.getMappingSet(mappingSetURI);
					
					// (2) check that the green tree structure of the middle mapping set matches the class model of the source mapping set
					checkClassModelMatches();
					message("Mapped class model location: " + middleMappedStructure.getUMLModelURL());
					
					// (3) get a location for the new projected mapping set from the user
					final String newMappingSetPath = FileUtil.getFilePathFromUser(mapperEditor,modelExts,"Select a location for the new projected mapping set",true);
					if (newMappingSetPath.equals("")) return;
					URI newMappingSetURI = FileUtil.URIFromPath(newMappingSetPath);

					// (4) create the new projected mapping set
					ResourceSet resourceSet = new ResourceSetImpl();
					Resource resource = resourceSet.createResource(newMappingSetURI);
					resultMappedStructure = MapperFactory.eINSTANCE.createMappedStructure();
					resource.getContents().add(resultMappedStructure);
					
					// (5) make the result mapped structure have the same XML structure as the source
					makeResultStructure();
					
					// (6) create the appropriate mappings in the result mapped structure
					makeProjectedMappings();

					// (8) Save the resource
					Map<Object, Object> options = new HashMap<Object, Object>();
					options.put(XMLResource.OPTION_ENCODING, "UTF-8");
					resource.save(options);
		

				}
				catch (Exception ex) 
				{
					ex.printStackTrace();
					WorkBenchUtil.showMessage("Error projecting mappings",ex.getMessage());
				}
			}
	}
	
	/**
	 * check that the tree structure of the middle mapping set matches the class model of the source mapping set
	 */
	private void checkClassModelMatches() throws MapperException
	{
		EPackage sourceClassModel = sourceMappedStructure.getClassModelRoot();
		LabelledEClass entryClass = ClassModelView.getRootLabelledEClass(sourceClassModel);
		ElementDef root = middleMappedStructure.getRootElement();
		
		if (!entryClass.eClass().getName().equals(root.getName()))
			throw new MapperException("Entry class name '" + entryClass.eClass().getName() 
					+ "' does not match mapping set entry node '" + root.getName() + "'");
		
		checkChildMatches(entryClass,root);
	}
	
	/**
	 * recursive descent to check that a mapped class model matches a mapped structure
	 * @param lClass
	 * @param elDef
	 * @throws MapperException
	 */
	private void checkChildMatches(LabelledEClass lClass, ElementDef elDef)  throws MapperException
	{
		int classChildren = lClass.getChildren().size();
		int nodeChildren = elDef.getChildElements().size();
		if (classChildren < nodeChildren) throw new MapperException("Class " + lClass.eClass().getName() 
				+ " has " + classChildren + " child classes, but the mapping node has " + nodeChildren + " child nodes.");
		
		// for each child node, find the mapping node which matches the association name
		for (Iterator<LabelledEClass> it = lClass.getChildren().iterator(); it.hasNext();)
		{
			LabelledEClass childClass = it.next();
			String childName = childClass.associationName();
			ElementDef childNode = elDef.getNamedChildElement(childName);
			if (childNode != null) checkChildMatches(childClass,childNode);
			// missing nodes in the middle mapping set are not a problem
			// else throw new MapperException("Class " + childName + " has no corresponding mapped node.");
		}
	}

	
	/**
	 * make the result mapped structure have the same XML tree as the source.
	 */
	private void makeResultStructure() throws MapperException
	{
		ElementDef sourceRoot = sourceMappedStructure.getRootElement();
		ElementDef resultRoot = copyNodeTree(sourceRoot);
		resultMappedStructure.setRootElement(resultRoot);
		resultMappedStructure.setClassModelRoot(middleMappedStructure.getClassModelRoot());
		resultMappedStructure.setUMLModelURL(middleMappedStructure.getUMLModelURL());
		
		GlobalMappingParameters sourceParams = sourceMappedStructure.getMappingParameters();
		GlobalMappingParameters resultParams = sourceParams.cloneNamespacesOnly();
		resultParams.setWrapperClass(sourceParams.getWrapperClass());
		resultMappedStructure.setMappingParameters(resultParams);
	}
	
	/**
	 * recursive descent of a mapping structure tree, copying all ElementDefs and AttributeDefs
	 * @param sourceEl
	 * @return
	 */
	private ElementDef copyNodeTree(ElementDef sourceEl)
	{
		ElementDef copyEl = MapperFactory.eINSTANCE.createElementDef();
		copyEl.setName(sourceEl.getName());
		copyEl.setMinMultiplicity(sourceEl.getMinMultiplicity());
		copyEl.setMaxMultiplicity(sourceEl.getMaxMultiplicity());
		copyEl.setDescription(sourceEl.getDescription());
		copyEl.setType(sourceEl.getType());
		
		for (Iterator<AttributeDef> it = sourceEl.getAttributeDefs().iterator();it.hasNext();)
		{
			AttributeDef sourceAtt = it.next();
			AttributeDef copyAtt = MapperFactory.eINSTANCE.createAttributeDef();
			copyAtt.setName(sourceAtt.getName());
			copyAtt.setMinMultiplicity(sourceAtt.getMinMultiplicity());
			copyAtt.setDescription(sourceAtt.getDescription());
			copyAtt.setType(sourceAtt.getType());
			
			copyEl.getAttributeDefs().add(copyAtt);
		}
		
		for (Iterator<ElementDef> it = sourceEl.getChildElements().iterator();it.hasNext();)
			copyEl.getChildElements().add(copyNodeTree(it.next()));
		
		return copyEl;
	}
	
	/**
	 * 
	 */
	private void makeProjectedMappings() throws MapperException
	{
		ElementDef sourceElDef = sourceMappedStructure.getRootElement();
		ElementDef middleElDef = middleMappedStructure.getRootElement();
		ElementDef resultElDef = resultMappedStructure.getRootElement();
		projectMappings(sourceElDef,middleElDef,resultElDef);
	}
	
	/**
	 * 
	 * @param sourceElDef
	 * @param middleElDef
	 * @param resultElDef
	 */
	private void projectMappings(ElementDef sourceElDef,ElementDef middleElDef,ElementDef resultElDef) throws MapperException
	{
		ElementDef nextMiddleElDef = middleElDef;
		ObjMapping sourceObjMapping = null;
		AssocMapping sourceAssocMapping = null;
		NodeMappingSet sourceNMS = sourceElDef.getNodeMappingSet();
		if (sourceNMS != null)
		{
			// full => green mappings have at most one object mapping and one association mapping per node
			int nAssocMappings = sourceNMS.getAssociationMappings().size();
			if (nAssocMappings > 1) throw new MapperException("More than one association mapping at source path " + sourceElDef.getPath());
			if (nAssocMappings == 1)
			{
				sourceAssocMapping = sourceNMS.getAssociationMappings().get(0);
				String assocName = sourceAssocMapping.getMappedEnd2().getMappedRole();
				nextMiddleElDef = middleElDef.getNamedChildElement(assocName);
				if (nextMiddleElDef == null) 
					throw new MapperException("Cannot find child '" + assocName + "' of node at green path " + middleElDef.getPath()
							+ " and full path " + sourceElDef.getPath());
			}

			// full => green mappings have at most one object mapping per node, and if so they may have one mapping of an attribute 'textContent'
			int nObjMappings = sourceNMS.getObjectMappings().size();
			if (nObjMappings > 1) throw new MapperException("More than one object mapping at source path " + sourceElDef.getPath());
			if (nObjMappings == 1)
			{
				sourceObjMapping = sourceNMS.getObjectMappings().get(0);
				NodeMappingSet middleNMS = nextMiddleElDef.getNodeMappingSet();
				if (middleNMS != null) 
				{
					transferMappings(resultElDef,sourceObjMapping,middleNMS);
					
					// 'textContent' property mappings on ElementDefs
					int nPropMappings = sourceNMS.getPropertyMappings().size();
					if (nPropMappings > 1) throw new MapperException("More than one property mapping at source path " + sourceElDef.getPath());
					if (nPropMappings == 1)
					{
						PropMapping sourcePropMap = sourceNMS.getPropertyMappings().get(0);
						if (!sourcePropMap.getMappedProperty().equals("textContent"))
							throw new MapperException("Property mapping at source path " + sourceElDef.getPath() + " is not to property textContent");
						if (middleNMS.getPropertyMappings().size() > 0)
						{
							PropMapping middlePropMap = middleNMS.getPropertyMappings().get(0);
							resultElDef.getNodeMappingSet().getPropertyMappings().add(copyPropMapping(middlePropMap));
							if (middleNMS.getPropertyMappings().size() > 1) message("More than 1 property mapping at path " + nextMiddleElDef.getPath());
						}
					}
				}				
			}
		}
		
		// deal with property mappings on AttributeDefs
		for (Iterator<AttributeDef> it = sourceElDef.getAttributeDefs().iterator(); it.hasNext();)
		{
			AttributeDef sourceAttDef = it.next();
			AttributeDef resultAttDef = resultElDef.getNamedAttribute(sourceAttDef.getName());
			if (resultAttDef == null) throw new MapperException("No result attribute " + sourceAttDef.getName());
			NodeMappingSet sourceAttNMS = sourceAttDef.getNodeMappingSet();
			// some attributeDefs in the source mapping set are just for fixed values, e.g. classCode
			if (sourceAttNMS != null)
			{
				int attMappings = sourceAttNMS.getPropertyMappings().size();
				if (attMappings != 1) throw new MapperException(attMappings + " mappings on AttributeDef " + sourceAttDef.getName());
				PropMapping sourceAttMap = sourceAttNMS.getPropertyMappings().get(0);
				String middleAttName = sourceAttMap.getMappedProperty();
				if (middleAttName.equals("order_property")) middleAttName = "element_position_virtual_att";
				AttributeDef middleAttDef = nextMiddleElDef.getNamedAttribute(middleAttName);
				if (middleAttDef != null)
				{
					NodeMappingSet middleAttNMS = middleAttDef.getNodeMappingSet();
					if (middleAttNMS != null)
					{
						PropMapping middlePropMapping = middleAttNMS.getPropertyMappings().get(0);
						NodeMappingSet resultAttNMS = MapperFactory.eINSTANCE.createNodeMappingSet();
						resultAttNMS.getPropertyMappings().add(copyPropMapping(middlePropMapping));
						resultAttDef.setNodeMappingSet(resultAttNMS);
						if (middleAttNMS.getPropertyMappings().size() > 1) message("More than 1 property mapping on attribute at path " + nextMiddleElDef.getPath());
					}
				}
				// this can happen if the middle node tree has not been fully expanded
				else message("Missing attribute " + middleAttName + " at path " + nextMiddleElDef.getPath());
			}
		}
		
		// recursive step
		for (Iterator<ElementDef> it = sourceElDef.getChildElements().iterator();it.hasNext();)
		{
			ElementDef sourceChild = it.next();
			ElementDef resultChild = resultElDef.getNamedChildElement(sourceChild.getName());
			if (resultChild == null) throw new MapperException("Cannot find result mapping set node '" + sourceChild.getName() + "'");
			if (hasMappingsInSubtree(nextMiddleElDef)) projectMappings(sourceChild, nextMiddleElDef, resultChild);
		}
	}
	
	/**
	 * 
	 * @param resultElDef
	 * @param sourceObjMapping
	 * @param middleNMS
	 */
	private void transferMappings(ElementDef resultElDef,ObjMapping sourceObjMapping,NodeMappingSet middleNMS)  throws MapperException
	{
		// key = the name of some mapped class; value = true if it is independent
		Hashtable<String,Boolean> representedClasses  = new Hashtable<String,Boolean>();
		NodeMappingSet resultNMS = MapperFactory.eINSTANCE.createNodeMappingSet();

		// transfer all object mappings, and record which classes are represented
		for (Iterator<ObjMapping> it = middleNMS.getObjectMappings().iterator();it.hasNext();)
		{
			ObjMapping om = it.next();
			representedClasses.put(om.getMappedClass(), new Boolean(true));
			resultNMS.getObjectMappings().add(copyObjMapping(om));
		}
		
		// transfer all association mappings, and record which classes are independent
		for (Iterator<AssocMapping> it = middleNMS.getAssociationMappings().iterator();it.hasNext();)
		{
			AssocMapping am = it.next();
			makeClassesDependent(am,representedClasses);
			resultNMS.getAssociationMappings().add(copyAssocMapping(am));
		}
		
		// move the fixed value conditions to the object mapping of the (one) class which is independent
		int numberIndependent = 0;
		for (Iterator<ObjMapping> it = resultNMS.getObjectMappings().iterator();it.hasNext();)
		{
			ObjMapping om = it.next();
			if (representedClasses.get(om.getMappedClass()).booleanValue())
			{
				numberIndependent++;
				// transfer all fixed value conditions from the full => green mapping set, to the full => other mapping set
				for (Iterator<MappingCondition> iu = sourceObjMapping.getMappingConditions().iterator();iu.hasNext();)
				{
					MappingCondition mc = iu.next();
					if (mc instanceof ValueCondition)
					{
						ValueCondition sourceVC = (ValueCondition)mc;
						ValueCondition resultVC = MapperFactory.eINSTANCE.createValueCondition();
						resultVC.setLeftPath(sourceVC.getLeftPath());
						resultVC.setRightValue(sourceVC.getRightValue());
						om.getMappingConditions().add(resultVC);
					}
				}
			}
		}
		
		// if there are any classes represented on a node of the middle mapping set, just one of them must be independent
		if ((representedClasses.size() > 0) && (numberIndependent != 1)) 
			throw new MapperException("There are " + numberIndependent + " independent object mappings on the node at full path " + resultElDef.getPath());
		
		resultElDef.setNodeMappingSet(resultNMS);
	}
	
	/**
	 * note  any represented class which is rendered dependent by this mapping, 
	 * i.e owned by another class represented on the same node
	 * @param am
	 * @param representedClasses
	 */
	private void makeClassesDependent(AssocMapping am, Hashtable<String,Boolean> representedClasses)
	{
		if (am.getMappedEnd2().isRequiredForObject())
		{
			String owningClass = am.getMappedEnd1().getMappedClass();
			String ownedClass = am.getMappedEnd2().getMappedClass();
			// if any class is 'owned' by one of the other represented classes, it is not independent
			if (representedClasses.get(owningClass) != null) representedClasses.put(ownedClass, new Boolean(false));			
		}
	}
	
	/**
	 * 
	 * @param middleMapping
	 * @return
	 */
	private ObjMapping copyObjMapping(ObjMapping middleMapping)
	{
		ObjMapping resultMapping = MapperFactory.eINSTANCE.createObjMapping();
		resultMapping.setMappedClass(middleMapping.getMappedClass());
		resultMapping.setMappedPackage(middleMapping.getMappedPackage());
		resultMapping.setSubset(middleMapping.getSubset());
		
		// transfer all fixed values
		for (Iterator<FixedPropertyValue> it = middleMapping.getFixedPropertyValues().iterator();it.hasNext();)
		{
			FixedPropertyValue fpv = it.next();
			FixedPropertyValue nfpv =MapperFactory.eINSTANCE.createFixedPropertyValue();
			nfpv.setMappedProperty(fpv.getMappedProperty());
			nfpv.setFixedValue(fpv.getFixedValue());
			nfpv.setValueType(fpv.getValueType());
			resultMapping.getFixedPropertyValues().add(nfpv);
		}
		
		/*  will throw ConcurrentModificationException - and not sure that transferring mapping conditions will work - they depend on paths.
		for (Iterator<MappingCondition> it = middleMapping.getMappingConditions().iterator();it.hasNext();)
			resultMapping.getMappingConditions().add(it.next()); */
		return resultMapping;
	}
	
	/**
	 * 
	 * @param middleMapping
	 * @return
	 */
	private PropMapping copyPropMapping(PropMapping middleMapping)
	{
		PropMapping resultMapping = MapperFactory.eINSTANCE.createPropMapping();
		resultMapping.setMappedClass(middleMapping.getMappedClass());
		resultMapping.setMappedPackage(middleMapping.getMappedPackage());
		resultMapping.setSubset(middleMapping.getSubset());
		resultMapping.setMappedProperty(middleMapping.getMappedProperty());
		resultMapping.setLocalPropertyConversion(middleMapping.getLocalPropertyConversion());
		return resultMapping;		
	}
	
	/**
	 * 
	 * @param middleMapping
	 * @return
	 */
	private AssocMapping copyAssocMapping(AssocMapping middleMapping) throws MapperException
	{
		AssocMapping resultMapping = MapperFactory.eINSTANCE.createAssocMapping();
		for (int e = 0; e < 2; e++)
		{
			AssocEndMapping sourceEnd = middleMapping.getMappedEnd(e);
			AssocEndMapping resultEnd = MapperFactory.eINSTANCE.createAssocEndMapping();
			resultEnd.setMappedClass(sourceEnd.getMappedClass());
			resultEnd.setMappedPackage(sourceEnd.getMappedPackage());
			resultEnd.setMappedRole(sourceEnd.getMappedRole());
			resultEnd.setSubset(sourceEnd.getSubset());
			resultEnd.setRequiredForObject(sourceEnd.isRequiredForObject());
			
			if (e == 0) resultMapping.setMappedEnd1(resultEnd);
			if (e == 1) resultMapping.setMappedEnd2(resultEnd);
		}
		return resultMapping;
	}
	
	/**
	 * 
	 * @param elDef
	 * @return
	 */
	private boolean hasMappingsInSubtree(ElementDef elDef)
	{
		boolean hasMappings = (elDef.getNodeMappingSet() != null);		
		if (!hasMappings)
		{
			for (Iterator<ElementDef> it = elDef.getChildElements().iterator(); it.hasNext();)
				if (hasMappingsInSubtree(it.next())) hasMappings = true;
		}		
		return hasMappings;
	}



	
	private void message(String s) {System.out.println(s);}

}
