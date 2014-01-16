package com.openMap1.mapper.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.openMap1.mapper.views.DebugView;

/**
 * Action when debugging a mapping set to take the next step and 
 * display it in the debug view.
 * 
 * @author robert
 *
 */

public class DebugStepAction extends Action implements IViewActionDelegate{
	
	private DebugView debugView;
	
	public void init(IViewPart part)
	{
		if (part instanceof DebugView) debugView = (DebugView)part;
	}
	
	public void run(IAction action) 
	{
		debugView.doStep();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}


}
