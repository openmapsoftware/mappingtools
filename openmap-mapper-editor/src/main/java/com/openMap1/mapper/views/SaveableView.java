package com.openMap1.mapper.views;

import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.ui.IWorkbenchPart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface which any view class must implement
 * if its contents are to be saveable as XML or a csv file.
 * 
 * @author robert
 *
 */

public interface SaveableView extends IWorkbenchPart{
	
	/** @return the label provider for the view */
	public ITableLabelProvider labelProvider();
	
	/** @return the TableViewer for the view */
	public TableViewer tableViewer();
	
	/** @return headers of columns in the view  */
	public Vector<String> columnHeaders();
	
	/** @return the menu item to save the contents of the view */
	public Action saveViewContentsAction();
	
	/**
	 * The ViewSaver passes back a header Element to the view, which can be filled 
	 * with view-specific information
	 * @param doc the Document - needed to create new Elements
	 * @param header the Header element to be filled
	 */
	public void fillHeaderElement(Document doc, Element header);

}
