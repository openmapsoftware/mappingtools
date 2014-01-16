package com.openMap1.mapper.actions;

import org.eclipse.ui.IObjectActionDelegate;


/**
 * Action to make the schema for an 'Alternate EMF instance' - like a usual EMF instance, 
 * except that the targets of non-containment references are represented by key 
 * values, not by their position in the document.
 * 
 * 
 * @author robert
 *
 */
public class MakeAlternateInstanceSchemaDelegate extends MakeInstanceSchemaDelegate implements IObjectActionDelegate{

	protected boolean isAlternateSchema() {return true;}

}
