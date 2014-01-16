package com.openMap1.mapper.commands;

import java.util.Iterator;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.command.AddCommand;

import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperPackage;

/**
 * This command first sets the feature 'isExpanded' to true.
 * Then it sets the feature 'Child Element' of the supplied Element node
 * to the same feature of the new structure Element.
 * Then it does the same for the Attributes.
 * It does not change any other features of the element, such as its name or its mappings
 * @author robert
 *
 */
public class ExtendElementForTypeCommand extends CompoundCommand {

	public ExtendElementForTypeCommand(EditingDomain domain,
			ElementDef elementToExtend,
			ElementDef newStructure)
	{
		super(0);

		// set the 'isExtended' property
		append(new SetCommand(domain,elementToExtend,
				MapperPackage.eINSTANCE.getElementDef_Expanded(),
				new Boolean(true)));

		/* set the Child Elements feature:
		 * because the command can only be done on an Element that has not yet been
		 * expanded (and so has no Child Elements to replace) it is only necessary to Add
		 * the new Child Elements 
		 */
		for (Iterator<ElementDef> it = newStructure.getChildElements().iterator(); it.hasNext();)
		{
			append(new AddCommand(domain,elementToExtend,
					MapperPackage.eINSTANCE.getElementDef_ChildElements(),
					it.next()));			
		}

		/* set the Attributes feature:
		 * because the command can only be done on an Element that has not yet been
		 * expanded (and so has no Attributes to replace) it is only necessary to Add
		 * the new Attributes
		 */
		for (Iterator<AttributeDef> it = newStructure.getAttributeDefs().iterator(); it.hasNext();)
		{
			append(new AddCommand(domain,elementToExtend,
					MapperPackage.eINSTANCE.getElementDef_AttributeDefs(),
					it.next()));			
		}
	}
	
	// use the superclass execute() to execute the full list of commands
	

}
