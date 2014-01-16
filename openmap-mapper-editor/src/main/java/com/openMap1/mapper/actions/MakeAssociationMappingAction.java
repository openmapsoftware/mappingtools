package com.openMap1.mapper.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Action;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;

import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;

/**
 * Action to make an association mapping
 * 
 * @author robert
 *
 */

public class MakeAssociationMappingAction extends Action implements IAction {

	private NodeDef nodeToMap;
	private EditingDomain domain;
	private MappableAssociation mass;
	private String description;
	
	public MakeAssociationMappingAction(EditingDomain domain, NodeDef nd, MappableAssociation mass,
			String description){
		super(mass.associationMenuLabel());
		nodeToMap = nd;
		this.domain = domain;
		this.mass = mass;
		this.description = description;
	}
	
	/**
	 * Create a new AssocMapping object, give it two AssocEndMapping children,
	 * then add it to the NodeMappingSet below current Node (Element or Attribute) in one AddCommand.
	 * If there is no NodeMappingSet, make one, add the mapping to it, and set the
	 * NodeMappingSet on the Node in one SetCommand. 
	 */
	public void run() 
	{
		// make the association mapping and its two child nodes
		AssocMapping am = MapperFactory.eINSTANCE.createAssocMapping();
		am.setDescription(description);

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
		
		// try to find the NodeMappingSet to add it to
		NodeMappingSet ns = nodeToMap.getNodeMappingSet();

		// if there is a NodeMappingSet, just add the mapping to it
		if (ns != null)
		{
			AddCommand ac = new AddCommand(domain,
				ns,
				MapperPackage.eINSTANCE.getNodeMappingSet_AssociationMappings(),
				am);
			domain.getCommandStack().execute(ac);
		}
		
		/* if there is no NodeMappingSet, make one, add the mapping to it, 
		 * and set it on the Node */
		else if (ns == null)
		{
			ns = MapperFactory.eINSTANCE.createNodeMappingSet();
			ns.getAssociationMappings().add(am);
			SetCommand sc = new SetCommand(domain,
				nodeToMap,
				MapperPackage.eINSTANCE.getNodeDef_NodeMappingSet(),
				ns);
			domain.getCommandStack().execute(sc);
		}
	}

}
