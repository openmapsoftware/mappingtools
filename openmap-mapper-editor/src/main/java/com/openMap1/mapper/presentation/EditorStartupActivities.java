package com.openMap1.mapper.presentation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.emf.common.util.URI;

import com.openMap1.mapper.util.ClassModelMaker;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.MappingsView;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.MappedStructure;
/**
 * Does all activities when the editor opens a new mapping set, beyond
 * what the generated EMF editor does.
 * @author robert
 *
 */
public class EditorStartupActivities {
	
	private Resource resource;
	private MappedStructure mappingRoot;
	private MapperEditor me;
	
	public EditorStartupActivities(MapperEditor me, Resource resource)
	{
		this.resource = resource;
		this.me = me;
	}
	
	/**
	 * 
	 * @return
	 */
	public Resource doStartupChecks() 
	{
		try{
			Object root = resource.getContents().get(0);
			//this case will probably have been caught somehow already, but...
			if (!(root instanceof MappedStructure))
			{
				addError("Root object is not a mapped structure");
				return resource;
			}
			mappingRoot = (MappedStructure)root;
			
			// if the set of mappings has a class model, open it
			openClassModelView();

			// if the set of mappings has a structure definition, open it.
			openStructure();
			
			// open the mappings view
			openMappingsView();
			
		}
		catch (MapperException ex) {addError(ex.getMessage());}
		
		return resource;
	}
	
	/**
	 * if the field 'UMLModelURL' of the top 'MappedStructure' node is set to anything
	 * except '', attempt to open the UML model and then open the Class Model view;
	 * log a warning if this fails.
	 */
	private void openClassModelView()
	{
		String modelURI = mappingRoot.getUMLModelURL();
		if ((modelURI != null) && !(modelURI.equals("")))
		{
			EObject umlRoot = null;
			try {
				URI uri = URI.createURI(modelURI);
				umlRoot = ClassModelMaker.makeClassModelFromFile(uri);
				if (umlRoot instanceof EPackage)
				{
					mappingRoot.setClassModelRoot((EPackage)umlRoot);
				}
				else addError("Class model root is not an EPackage");
			}
			catch (Exception ex)
			{
				addWarning("Cannot open class model: " + ex.getClass().getName() + ": " + ex.getMessage());
				return;
			}
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) // happens if the editor was open when you closed down, so is opened at startup
			{
				ClassModelView cmv = WorkBenchUtil.getClassModelView(false);
				if (cmv != null) 
				{
					cmv.initiateForMapperEditor(me, umlRoot, modelURI);
					page.activate(cmv); // supposed to show the page, but seems to have no effect
				}
			}
		}
	}
	
	private void openMappingsView()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// if (page != null) // happens if the editor was open when you closed down, so is opened at startup
		{
			MappingsView mv = WorkBenchUtil.getMappingsView(false);
			if (mv != null) 
			{
				mv.initiateForMapperEditor(me);
				page.activate(mv); // supposed to show the page, but seems to have no effect
			}
		}
		
	}
	
	/**
	 * first attempt at logging errors on startup
	 * @param message
	 */
	private void addError(String message)
	{
		XMIException xe = new XMIException(message);
		resource.getErrors().add(xe);		
	}
	
	/**
	 * first attempt at logging warnings on startup
	 * @param message
	 */
	private void addWarning(String message)
	{
		XMIException xe = new XMIException(message);
		resource.getWarnings().add(xe);		
	}
	
	/**
	 * if the field 'StructureURL' of the top 'MappedStructure' node is set to anything
	 * except '', attempt to open the structure definition;
	 * log a warning if this fails.
	 */
	private void openStructure() throws MapperException
	{
		String uriString = mappingRoot.getStructureURL();
			 if((uriString != null) && !(uriString.equals("")))
				{
					StructureDefinition structureDef = mappingRoot.getStructureDefinition();
					 /* this was removed as it gives a fatal error message
					  * if (structureDef == null) 
						addWarning("Cannot open structure definition at '" + uriString + "'"); */
					if (structureDef != null)
					{
						// if the top element type has been defined, check it is one of the types in the schema
						String topElementType = mappingRoot.getTopElementType();
						if (!checkTopTypes(topElementType,structureDef))
						{
							addWarning("The attached structure definition does not have a type '" + topElementType + "'");						
						}
						else
						{
							/* notify the editor's value set provider that there is a new Structure definition
							to supply allowed values for the properties MappedStructure:Top Element Type" 
							and MappedStructure:Top Element Name"  */
							me.propertyValueSetProvider().notifyNewValueSupplier("MappedStructure", "Top Element Type", structureDef);
							me.propertyValueSetProvider().notifyNewValueSupplier("MappedStructure", "Top Element Name", structureDef);
						}			
					}					
				}
			 
	}
	
	/**
	 * check that the complex type in the 'top element type' property of the Mapped Structure
	 * is one of the allowed types for the attached schema, or is null (un-chosen)
	 * @param topType
	 * @param xsd
	 * @return
	 */
	private boolean checkTopTypes(String topType, StructureDefinition structureDef)
	{
		boolean found = false;
		if (topType == null) return true;
		else for (int i = 0; i < structureDef.topComplexTypes().length; i++)
			if (structureDef.topComplexTypes()[i].equals(topType)) found = true;
		return found;
	}

}
