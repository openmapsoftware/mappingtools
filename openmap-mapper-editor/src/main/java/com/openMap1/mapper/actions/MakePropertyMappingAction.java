package com.openMap1.mapper.actions;

import java.util.StringTokenizer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Action;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;

import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.PropMapping;

/**
 * Action to make a property mapping,
 * from the selected node to a property of the class selected in the class model.
 * 
 * @author robert
 *
 */


public class MakePropertyMappingAction extends Action implements IAction {

	private NodeDef nodeToMap;
	private EditingDomain domain;
	private String className;
	private String packageName;
	private String propertyName;
	private String subset;
	private String description;
	
	public MakePropertyMappingAction(EditingDomain domain, NodeDef nd, 
			String classProp, EClass selectedClass,String subset, String description){
		super(classProp);
		nodeToMap = nd;
		this.domain = domain;
		StringTokenizer st = new StringTokenizer(classProp,":");
		st.nextToken(); // ignore the superclass name which was used in the menu item
		propertyName = st.nextToken();
		className = selectedClass.getName(); // the mapping records the class selected in the class model view
		packageName = selectedClass.getEPackage().getName();
		this.subset = subset;
		this.description = description;
	}
	
	/**
	 * Create a new PropMapping object for a mapping to the property,
	 * then add it to the NodeMappingSet below current Node in one AddCommand.
	 * If there is no NodeMappingSet, make one, add the mapping to it, and set the
	 * NodeMappingSet on the Node in one SetCommand. 
	 */
	public void run() 
	{
		// make the property mapping
		PropMapping pm = MapperFactory.eINSTANCE.createPropMapping();
		pm.setMappedClass(className);
		pm.setMappedProperty(propertyName);
		pm.setMappedPackage(packageName);
		pm.setSubset(subset);
		pm.setDescription(description);
		
		// try to find the NodeMappingSet to add it to
		NodeMappingSet ns = nodeToMap.getNodeMappingSet();

		// if there is a NodeMappingSet, just add the mapping to it
		if (ns != null)
		{
			AddCommand ac = new AddCommand(domain,
				ns,
				MapperPackage.eINSTANCE.getNodeMappingSet_PropertyMappings(),
				pm);
			domain.getCommandStack().execute(ac);
		}
		
		/* if there is no NodeMappingSet, make one, add the mapping to it, 
		 * and set it on the Node */
		else if (ns == null)
		{
			ns = MapperFactory.eINSTANCE.createNodeMappingSet();
			ns.getPropertyMappings().add(pm);
			SetCommand sc = new SetCommand(domain,
				nodeToMap,
				MapperPackage.eINSTANCE.getNodeDef_NodeMappingSet(),
				ns);
			domain.getCommandStack().execute(sc);
		}
	}

}
