package com.openMap1.mapper.core;

/**
 * NamespaceException is made a subclass of XpthException because otherwise
 * it is too tedious to track how NamespaceExceptions propagate up through class Xpth,
 * which has loads of XpthExceptions.
 * @author robert
 *
 */
public class NamespaceException extends XpthException{

	// to remove some compiler warning
	static final long serialVersionUID = 0;
	
	public NamespaceException (String s) {super(s);}

}
