package com.openMap1.mapper.actions;

public class RunQueryToViewAction extends RunQueryAction{
	
	public RunQueryToViewAction()
	{
		super();
		setText("Run Query");
		setToolTipText("Run this query, using the currently active data sources, and show results in the query results view");
	}
	
	int function() {return SHOW_RESULTS_IN_VIEW;}

}
