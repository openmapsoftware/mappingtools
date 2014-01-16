package com.openMap1.mapper.actions;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.ElementDef;

/**
 * Action to make a chain of association mappings and object mappings 
 * from some RMIM class that is already mapped, down to the selected class;
 * all mappings on the same node
 * @author robert
 *
 */
public class MakeChainMappingAction extends Action implements IAction {
	
	private EditingDomain domain;
	private ElementDef ed;
	private String ancestorMappedSubset;
	private String ancestorMappedClass;
	private LabelledEClass selected;
	
	public MakeChainMappingAction(EditingDomain domain, ElementDef ed,
			String ancestorMappedClass, String ancestorMappedSubset,
			LabelledEClass selected)
	{
		super("Make Chain Mapping to " + selected.eClass().getName());
		this.domain =domain;
		this.ed = ed;
		this.ancestorMappedClass = ancestorMappedClass;
		this.ancestorMappedSubset = ancestorMappedSubset;
		this.selected = selected;
	}
	
	public void run()
	{
		boolean foundMappedClass =  false;
		LabelledEClass current = selected;
		// iterate over ancestor classes, making object and association mappings
		while (!foundMappedClass)
		{
			// make the object mapping
			EClass child = current.eClass();
			String childClassName = child.getName();
			String childPackageName = child.getEPackage().getName();
			String childSubset = current.subsetToMap();
			String refName = current.associationName();
			new MakeObjectMappingAction(domain,ed,childClassName,childPackageName,childSubset,"").run();
			
			// iterate to the parent class, and make the association mapping
			current = current.parent();
			if (current != null) // playing safe
			{
				EClass parent = current.eClass();
				String parentClass = ModelUtil.getQualifiedClassName(parent);
				foundMappedClass = (parentClass.equals(ancestorMappedClass));
				String parentSubset = current.subsetToMap();
				if (foundMappedClass) parentSubset = ancestorMappedSubset;
				EReference ref = (EReference)parent.getEStructuralFeature(refName);
				// true = make the association mapping be required for the child
				MappableAssociation mass = new MappableAssociation(parent,parentSubset,child, childSubset,ref,true);
				new MakeAssociationMappingAction(domain,ed,mass,"").run();				
			}
			else foundMappedClass = true; // to  make it stop if parent == null (unexpected in any case)
		}
	}

}
