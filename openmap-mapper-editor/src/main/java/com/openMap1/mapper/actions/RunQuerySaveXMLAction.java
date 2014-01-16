package com.openMap1.mapper.actions;

public class RunQuerySaveXMLAction extends RunQueryAction{

	public RunQuerySaveXMLAction()
	{
		super();
		setText("Save XML from RDBMS retrievals");
		setToolTipText("Run this query, using the currently active data sources, and save the XML from the RDBMS retrievals");
	}
	
	int function() {return SAVE_RDB_XML;}

}
