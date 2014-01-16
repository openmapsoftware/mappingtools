package com.openMap1.mapper.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Action;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;

import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;

/**
 * Action to make an object mapping,
 * from the selected node to the class selected in the class model.
 * 
 * @author robert
 *
 */

public class MakeObjectMappingAction extends Action implements IAction {

	private ElementDef elementToMap;
	private EditingDomain domain;
	private String className;
	private String packageName;
	private String subset;
	private String description;
	
	public MakeObjectMappingAction(EditingDomain domain, ElementDef el, 
			String className,String packageName, String subset,String description){
		super("Map to Class '" + className + "'");
		this.elementToMap = el;
		this.domain = domain;
		this.className = className;
		this.packageName = packageName;
		this.subset = subset;
		this.description = description;
	}
	
	/**
	 * Create a new ObjMapping object for a mapping to the class,
	 * then add it to the NodeMappingSet below current Element in one AddCommand.
	 * If there is no NodeMappingSet, make one, add the mapping to it, and set the
	 * NodeMappingSet on the Element in one SetCommand. 
	 */
	public void run() 
	{
		// make the object mapping to be added
		ObjMapping om = MapperFactory.eINSTANCE.createObjMapping();
		om.setMappedClass(className);
		om.setMappedPackage(packageName);
		om.setSubset(subset);
		om.setDescription(description);
		
		// try to find the NodeMappingSet to add it to
		NodeMappingSet ns = elementToMap.getNodeMappingSet();

		// if there is a NodeMappingSet, just add the mapping to it
		if (ns != null)
		{
			AddCommand ac = new AddCommand(domain,
				ns,
				MapperPackage.eINSTANCE.getNodeMappingSet_ObjectMappings(),
				om);
			domain.getCommandStack().execute(ac);
		}

		/* if there is no NodeMappingSet, make one, add the mapping to it, 
		 * and set it on the Element */
		else if (ns == null)
		{
			ns = MapperFactory.eINSTANCE.createNodeMappingSet();
			ns.getObjectMappings().add(om);
			SetCommand sc = new SetCommand(domain,
				elementToMap,
				MapperPackage.eINSTANCE.getNodeDef_NodeMappingSet(),
				ns);
			domain.getCommandStack().execute(sc);
		}
	}

}
