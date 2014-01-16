package com.openMap1.mapper.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.openMap1.mapper.views.DebugView;

/**
 * Action when debugging a mapping set to complete all steps in the 
 * action of the current breakpointed mapping on the current node,
 * and then to run on to the next breakpointed mapping or the next node which uses
 * the current mapping.
 * 
 * @author robert
 *
 */
public class DebugRunAction extends Action implements IViewActionDelegate{
	
	private DebugView debugView;
	
	public void init(IViewPart part)
	{
		if (part instanceof DebugView) debugView = (DebugView)part;
	}
	
	public void run(IAction action) 
	{
		debugView.doRun();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}


}
