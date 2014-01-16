package com.openMap1.mapper.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.ObjMapping;

/**
 * Action to open the class model view on the class of the 
 * selected object mapping.
 * 
 * @author robert
 *
 */

public class ShowMappedClassAction  extends Action implements IAction{
	
	private ObjMapping om;
	
	ShowMappedClassAction(ObjMapping om)
	{
		super("Show Mapped Class");
		this.om = om;
	}
	
	public void run()
	{
		ClassModelView cmv = WorkBenchUtil.getClassModelView(true);
		if (cmv != null)
		{
			String qualifiedClassName = om.getQualifiedClassName();
			cmv.showMappedClass(qualifiedClassName, om.getSubset());
		}		
	}

}
