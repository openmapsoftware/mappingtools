package com.openMap1.mapper.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.openMap1.mapper.views.DebugView;

/**
 * Action to stop debugging a mapping set.
 * 
 * @author robert
 *
 */

public class DebugQuitAction extends Action implements IViewActionDelegate{
	
	private DebugView debugView;
	
	public void init(IViewPart part)
	{
		if (part instanceof DebugView) debugView = (DebugView)part;
	}
	
	public void run(IAction action) 
	{
		debugView.doQuit();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}


}
