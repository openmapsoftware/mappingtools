package com.openMap1.mapper.actions;

import org.eclipse.ui.IObjectActionDelegate;

/**
 * Action to make the XML schema that describes an EMF Ecore instance
 * 
 * @author robert
 *
 */
public class MakeEcoreInstanceSchemaDelegate extends MakeInstanceSchemaDelegate implements IObjectActionDelegate{

	protected boolean isAlternateSchema() {return false;}

}
