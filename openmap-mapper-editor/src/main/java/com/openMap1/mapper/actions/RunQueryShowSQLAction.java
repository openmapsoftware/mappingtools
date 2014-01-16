package com.openMap1.mapper.actions;

public class RunQueryShowSQLAction  extends RunQueryAction{
	
	public RunQueryShowSQLAction()
	{
		super();
		setText("Show Query SQL");
		setToolTipText("Show SQL query text for any active RDBMS data sources");
	}
	
	int function() {return SHOW_SQL;}

}
