package com.openMap1.mapper.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import org.eclipse.ui.IWorkbenchPart;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditorPlugin;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ParameterClass;
import com.openMap1.mapper.ParameterClassValue;

/**
 * Action to create a new mapping set for the type of the selected
 * Element, and to import the new mapping set on the element.
 * 
 * @author robert
 *
 */

public class CreateNewMappingSetForTypeAction extends Action implements IAction {

	private StructureDefinition structureDef;
	private ElementDef importingElement;
	private EditingDomain domain;
	private IWorkbenchPart part;

	protected MapperPackage mapperPackage = MapperPackage.eINSTANCE;
	protected MapperFactory mapperFactory = mapperPackage.getMapperFactory();
	
	public CreateNewMappingSetForTypeAction(IWorkbenchPart part, EditingDomain domain, 
			ElementDef el, StructureDefinition structureDef){
		super("Create and import new Mapping Set of type '" + el.getType() + "'");
		this.part = part;
		importingElement = el;
		this.domain = domain;
		this.structureDef = structureDef;
	}

	public void run() 
	{
		// (1) get a location for the new mapping set from the user
	    String[] modelExts= {".mapper"};
		final String mappingSetPath = FileUtil.getFilePathFromUser(part,modelExts,"Select location for new mapping set",true);
		if (mappingSetPath.equals("")) return;
		URI mappingSetURI = FileUtil.URIFromPath(mappingSetPath);
		
		// (2) create the new mapping set and save it (probably in the workspace - location chosen by user)
		try {
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.createResource(mappingSetURI);
			EObject rootObject = createMappedStructure();
			resource.getContents().add(rootObject);

			// Save the resource
			Map<Object, Object> options = new HashMap<Object, Object>();
			options.put(XMLResource.OPTION_ENCODING, "UTF-8");
			resource.save(options);
		}
		catch (Exception exception) {
			MapperEditorPlugin.INSTANCE.log(exception);
		}
		
		/* (3) Mark the selected element on the current mapping set as importing the new mapping set 
		 * If the importing element has just one class mapping, make it the parameter class value
		 * of the import. */
		ImportMappingSet ims = mapperFactory.createImportMappingSet();
		ims.setMappingSetURI(mappingSetURI.toString());
		String[] oneMappedClass = singleMappedClass(importingElement);
		if (oneMappedClass != null)
		{
			ParameterClassValue pcv = mapperFactory.createParameterClassValue();
			pcv.setMappedPackage(oneMappedClass[0]);
			pcv.setMappedClass(oneMappedClass[1]);
			pcv.setSubset(oneMappedClass[2]);
			ims.getParameterClassValues().add(pcv);
		}
		SetCommand sc = new SetCommand(domain,importingElement,
				MapperPackage.eINSTANCE.getElementDef_ImportMappingSet(),
				ims);
		domain.getCommandStack().execute(sc);				
	}
	
	// create the new mapping set
	private MappedStructure createMappedStructure() {

		// create a bare mapping set
		MappedStructure root = mapperFactory.createMappedStructure();
		
		// copy some features from the existing top MappedStructure node
		MappedStructure currentRoot = (MappedStructure)ModelUtil.getModelRoot(importingElement);
		root.setUMLModelURL(currentRoot.getUMLModelURL());
		root.setStructureURL(currentRoot.getStructureURL());
		// clone is necessary to stop the new mapping set stealing from this mapping set
		root.setMappingParameters(currentRoot.getMappingParameters().cloneNamespacesOnly());
		root.setStructureType(currentRoot.getStructureType());
		
		// add an expanded root Element with the correct structure to the new root
		root.setTopElementType(importingElement.getType());
		try{
			ElementDef newStructure = structureDef.typeStructure(importingElement.getType());
			newStructure.setExpanded(true);
			newStructure.setMaxMultiplicity(MaxMult.ONE);
			root.setRootElement(newStructure);			

			/* if the importing element has just one mapped class, set this class to be the 
			 * parameter class of the imported mapping set, and make one object mapping to
			 * the class on the root element of the imported mapping set */
			String[] oneMappedClass = singleMappedClass(importingElement);
			if (oneMappedClass != null)
			{
				// add the parameter class
				ParameterClass pc  = MapperFactory.eINSTANCE.createParameterClass();
				pc.setPackageName(oneMappedClass[0]);
				pc.setClassName(oneMappedClass[1]);
				root.getParameterClasses().add(pc);
				
				// add the object mapping, on a NodeMappingSet
				NodeMappingSet nms = MapperFactory.eINSTANCE.createNodeMappingSet();
				ObjMapping om = MapperFactory.eINSTANCE.createObjMapping();
				om.setMappedPackage(oneMappedClass[0]);
				om.setMappedClass(oneMappedClass[1]);
				om.setSubset(""); // default anyway
				nms.getObjectMappings().add(om);
				newStructure.setNodeMappingSet(nms);
			}
		}
		catch (MapperException ex){System.out.println(ex.getMessage());}
		return root;
	}
	
	/**
	 * @return if the importing Element has a single class mapping on it,
	 * return an array of the mapped class package name, class name and subset; otherwise return null
	 */
	public static String[] singleMappedClass(ElementDef importingElement)
	{
		String[] mappedClass = null;
		List<EObject> objMappings = ModelUtil.getEObjectsUnder(importingElement, MapperPackage.eINSTANCE.getObjMapping());
		if (objMappings.size() == 1)
		{
			mappedClass = new String[3];
			ObjMapping om = (ObjMapping)objMappings.get(0);
			mappedClass[0] = om.getMappedPackage();
			mappedClass[1] = om.getMappedClass();
			mappedClass[2] = om.getSubset();
		}
		return mappedClass;
	}


}
