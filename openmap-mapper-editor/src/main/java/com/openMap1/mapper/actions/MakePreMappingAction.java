package com.openMap1.mapper.actions;

import java.util.StringTokenizer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Action;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;

import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.Annotations;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.Note;

/**
 * Action to make a pre-mapping on the selected node.
 * This is a Note on an Annotation, instructing the tools
 * to make a mapping to a class or to a property of the class selected in the class model.
 * 
 * (All these mappings will be made later in one command, adding in the other 
 * class mappings and association mappings required higher in the structure tree)
 * 
 * @author robert
 *
 */


public class MakePreMappingAction extends Action implements IAction {

	private NodeDef nodeToMap;
	private EditingDomain domain;
	private String className;
	private String packageName;
	private String propertyName;
	private Object fromClassModel;
	private boolean isPropertyMapping;
	
	public MakePreMappingAction(EditingDomain domain, NodeDef nd, 
			String classProp, EClass selectedClass, Object fromClassModel,boolean isPropertyMapping)
	{
		// classProp determines the name of the menu item
		super(classProp);
		nodeToMap = nd;
		this.domain = domain;
		propertyName = "";
		if (isPropertyMapping)
		{
			StringTokenizer st = new StringTokenizer(classProp,":");
			st.nextToken(); // ignore the superclass name which was used in the menu item
			propertyName = st.nextToken();			
		}
		className = selectedClass.getName(); // the mapping records the class selected in the class model view
		packageName = selectedClass.getEPackage().getName();
		this.fromClassModel = fromClassModel;
		this.isPropertyMapping = isPropertyMapping;
	}
	
	/**
	 * Create a new Note object for a mapping to the property or class,
	 * then add it to the Annotations below current Node in one AddCommand.
	 * If there is no Annotations, make one, add the Note to it, and set the
	 * Annotations object on the Node in one SetCommand. 
	 */
	public void run() 
	{
		// get information uniquely identifying the class
		String classHandle = "";
		if (fromClassModel instanceof EClass) 
		{
			classHandle = packageName + "." + className;
			if (packageName == null) classHandle = className;			
		}
		else if (fromClassModel instanceof LabelledEClass) 
			classHandle = ((LabelledEClass)fromClassModel).getPath() + "/" + className;

		// make the Note to make a new class mapping or property mapping
		Note nt = MapperFactory.eINSTANCE.createNote();
		nt.setKey("Pre-map " + classHandle);
		nt.setValue("");
		if (isPropertyMapping) nt.setValue(propertyName);
		
		// try to find the NodeMappingSet to add the Note to
		Annotations ann  = nodeToMap.getAnnotations();

		// if there is a NodeMappingSet, just add the mapping to it
		if (ann != null)
		{
			AddCommand ac = new AddCommand(domain,
				ann,
				MapperPackage.eINSTANCE.getAnnotations_Notes(),
				nt);
			domain.getCommandStack().execute(ac);
		}
		
		/* if there is no NodeMappingSet, make one, add the mapping to it, 
		 * and set it on the Node */
		else if (ann == null)
		{
			ann = MapperFactory.eINSTANCE.createAnnotations();
			ann.getNotes().add(nt);
			SetCommand sc = new SetCommand(domain,
				nodeToMap,
				MapperPackage.eINSTANCE.getNodeDef_Annotations(),
				ann);
			domain.getCommandStack().execute(sc);
		}
	}

}
