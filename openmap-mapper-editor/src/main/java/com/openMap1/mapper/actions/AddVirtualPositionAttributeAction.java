package com.openMap1.mapper.actions;

import java.util.Iterator;

import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.impl.ElementDefImpl;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;

/**
 * adds a virtual position attribute to an XML element in a mapping set, for cases where
 * the position of that element can be used, eg in a mapping condition.
 * @author robert
 *
 */

public class AddVirtualPositionAttributeAction extends Action implements IAction{

	private ElementDef elementToModifyChildren;
	private EditingDomain domain;
	
	public AddVirtualPositionAttributeAction(MapperEditor mapperEditor,EditingDomain domain,
			ElementDef elementToModifyChildren)
	{
		super("Add Virtual Position Attributes to Child Elements");
		this.elementToModifyChildren = elementToModifyChildren;
		this.domain = domain;
	}
	
	/** 
	 * add an AttributeDef (with name given by ElementdefImpl.ELEMENT_POSITION_ATTRIBUTE)
	 * to every child ElementDef of the selected ElementDef
	 */
	public void run()
	{
		for (Iterator<ElementDef> it = elementToModifyChildren.getChildElements().iterator();it.hasNext();)
		{
			ElementDef child = it.next();
			AttributeDef virtualAtt = MapperFactory.eINSTANCE.createAttributeDef();
			virtualAtt.setName(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE);
			AddCommand ac = new AddCommand(domain,child,
					MapperPackage.eINSTANCE.getElementDef_AttributeDefs(),
					virtualAtt);			
			domain.getCommandStack().execute(ac);							
		}
	}

}
