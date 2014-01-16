package com.openMap1.mapper.core;

public class RunTranslateException extends MDLWriteException {
	
	public RunIssue issue() {return issue;}
	private RunIssue issue;

	// to remove some compiler warning
	static final long serialVersionUID = 0;


	  public RunTranslateException(String message, RunIssue issue) {
		  super(message);
		  this.issue = issue;
		  }

}
