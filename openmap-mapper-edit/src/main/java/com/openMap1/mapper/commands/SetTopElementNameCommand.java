package com.openMap1.mapper.commands;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.command.SetCommand;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;

/**
 * This command first sets the property 'Top Element Name' of the top 'MappedStructure' node
 * (as is expected after it has been altered with the drop-down property editor).
 * It then sets the single child root element to be an element of the required complex 
 * type, with the subtree of elements and attributes as defined by the name and its type.
 * @author robert
 *
 */
public class SetTopElementNameCommand extends CompoundCommand {

	private EditingDomain domain;
	private EStructuralFeature feature;
	private MappedStructure ms;
	private String elName;
	private StructureDefinition structureDef;
	
	public SetTopElementNameCommand(EditingDomain domain,
			EObject owner,
			EStructuralFeature fr,
			Object value)
	{
		super(0);
		this.domain = domain;
		this.feature = fr;
		if ((owner instanceof MappedStructure) && (value instanceof String)) try
		{
			elName = (String)value;
			ms = (MappedStructure)owner;
			structureDef = ms.getStructureDefinition();
			append(new SetCommand(domain,ms,feature,elName));
		}
		catch (MapperException ex) {}
	}
	
	public void execute()
	{
		// execute the first appended command, to set the 'Top Element Name' property of the MappedStructure
		super.execute();

		// attach the root Element object, and its subtree defined by the type, to the MappedStructure object
		EStructuralFeature f = MapperPackage.Literals.MAPPED_STRUCTURE__ROOT_ELEMENT;
		try{
			ElementDef newEl = structureDef.nameStructure(elName);
			appendAndExecute(new SetCommand(domain,ms,f,newEl));
			
			// set the top Element type property of the mapped Structure to be that of the actual top Element
			EStructuralFeature g = MapperPackage.Literals.MAPPED_STRUCTURE__TOP_ELEMENT_TYPE;
			appendAndExecute(new SetCommand(domain,ms,g,newEl.getType()));
			
			/* At one stage (5/09) I set the expanded flag to true on the newly added Element,
			 * in order to avoid a validation error; but this was wrong, because usually
			 * the top element has a type, which has not been expanded */
			/*
			appendAndExecute(new SetCommand(domain,newEl,
					MapperPackage.eINSTANCE.getElementDef_Expanded(),
					new Boolean(true)));
			*/							
		}
		catch (MapperException ex){System.out.println(ex.getMessage());}
	}

}
