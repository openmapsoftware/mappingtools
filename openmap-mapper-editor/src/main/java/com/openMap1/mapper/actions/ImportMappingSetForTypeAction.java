package com.openMap1.mapper.actions;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.ui.IWorkbenchPart;

import com.openMap1.mapper.presentation.MapperEditorPlugin;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ParameterClassValue;

/**
 * Action to make the selected element import some existing mapping set 
 * which matches the complex type of the element.
 * 
 * @author robert
 *
 */

public class ImportMappingSetForTypeAction extends Action implements IAction {

	private ElementDef importingElement;
	private EditingDomain domain;
	private IWorkbenchPart part;

	protected MapperPackage mapperPackage = MapperPackage.eINSTANCE;
	protected MapperFactory mapperFactory = mapperPackage.getMapperFactory();
	
	public ImportMappingSetForTypeAction(IWorkbenchPart part, EditingDomain domain, ElementDef el){
		super("Import Mapping Set of type '" + el.getType() + "'");
		this.part = part;
		importingElement = el;
		this.domain = domain;
	}

	public void run() 
	{
		// (1) get a location for the new mapping set from the user
	    String[] modelExts= {"*.mapper"};
		final String mappingSetPath = FileUtil.getFilePathFromUser(part,modelExts,"Select mapping set to import",false);
		if (mappingSetPath.equals("")) return;
		URI mappingSetURI = FileUtil.URIFromPath(mappingSetPath);
		
		// (2) open the mapping set
		EObject rootObject = null;
		try{
			rootObject = FileUtil.getEMFModelRoot(mappingSetURI);
		}
		catch (Exception exception) {
			MapperEditorPlugin.INSTANCE.log(exception);}
		if (!(rootObject instanceof MappedStructure)) return;
		MappedStructure ms = (MappedStructure)rootObject;
		
		// (3) check the type of the imported mapping set (other checks are left to validation)
		if (!(ms.getTopElementType().equals(importingElement.getType())))
		{
			MessageDialog.openError(part.getSite().getShell(),
					"Imported type mismatch",
					"Mapping set structure type '" + ms.getTopElementType() 
					+ "' does not match importing element type '" + importingElement.getType() + "'");
			return;
		}
		
		/* (4) Mark the selected element on the current mapping set as importing the new mapping set. 
		 * If the importing ElementDef has a single mapped class, make it the parameter class value
		 * for the import. */
		ImportMappingSet ims = mapperFactory.createImportMappingSet();
		ims.setMappingSetURI(mappingSetURI.toString());
		String[] oneMappedClass = CreateNewMappingSetForTypeAction.singleMappedClass(importingElement);
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
	

}
