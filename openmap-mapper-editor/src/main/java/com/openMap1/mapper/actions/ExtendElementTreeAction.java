package com.openMap1.mapper.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Action;

import org.eclipse.emf.edit.domain.EditingDomain;

import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.commands.ExtendElementForTypeCommand;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.ElementDef;

/**
 * Action to extend the element tree of the mapping set,
 * using the structure for the complex type of the selected Element
 * 
 * @author robert
 *
 */

public class ExtendElementTreeAction extends Action implements IAction {

	private StructureDefinition structureDef;
	private ElementDef elementToExtend;
	private EditingDomain domain;
	
	public ExtendElementTreeAction(EditingDomain domain, ElementDef el, StructureDefinition structureDef){
		super("Extend Tree");
		this.elementToExtend = el;
		this.domain = domain;
		this.structureDef = structureDef;
	}
	
	/**
	 * Create a new element for the type subtree, then execute a command to
	 * reset the child elements and attributes of the selected element to those of 
	 * the new Element - not changing any other features except isExpanded (e.g. mappings)
	 */
	public void run() 
	{
		try{
			ElementDef newStructure = structureDef.typeStructure(elementToExtend.getType());
			if (newStructure == null) throw new MapperException("Cannot find structure for type '" + elementToExtend.getType() + "'");
			ExtendElementForTypeCommand ec = new ExtendElementForTypeCommand(domain,elementToExtend,newStructure);
			domain.getCommandStack().execute(ec);							
		}
		catch (MapperException ex)
		{
			WorkBenchUtil.showMessage("Cannot extend tree", ex.getMessage());
			System.out.println(ex.getMessage());
		}
		catch (Exception ex){ex.printStackTrace();}
	}

}
