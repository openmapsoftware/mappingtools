package com.openMap1.mapper.actions;

import org.eclipse.jface.action.Action;

import com.openMap1.mapper.presentation.QueryEditor;

/**
 * Action to insert text in the current query
 * 
 * @author robert
 *
 */

public class InsertQueryTextAction extends Action{

	private QueryEditor queryEditor = null;
	private String insertText;
	
	public InsertQueryTextAction(QueryEditor queryEditor, String menuText, String insertText)
	{
		super(menuText);
		this.insertText = insertText;
		this.queryEditor = queryEditor;
	}
	
	public void run() {
		queryEditor.insertTextAtCursor(insertText);
	}

}
