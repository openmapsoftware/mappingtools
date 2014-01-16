package com.openMap1.mapper.presentation;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

import org.eclipse.ui.editors.text.TextEditor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.openMap1.mapper.actions.RunQuerySaveXMLAction;
import com.openMap1.mapper.actions.RunQueryShowSQLAction;
import com.openMap1.mapper.actions.RunQueryToFileAction;
import com.openMap1.mapper.actions.RunQueryToViewAction;
import com.openMap1.mapper.actions.InsertQueryTextAction;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.views.DataSourceView;
import com.openMap1.mapper.views.ClassModelView;


import com.openMap1.mapper.util.ModelUtil;

public class QueryEditor extends TextEditor implements ISelectionChangedListener{
	
	private Hashtable<String,LabelledEClass> menuStartClasses = new Hashtable<String,LabelledEClass>();
	
	private RunQueryToViewAction runQueryViewAct;
	private RunQueryToFileAction runQueryFileAct;
	private RunQueryShowSQLAction runQueryShowSQLAct;
	private RunQuerySaveXMLAction runQuerySaveXMLAction;
	
	private boolean tracing = false;
	
	private int maximumMenuDepth = 2;
	
	//---------------------------------------------------------------------------------------
	//                  Noting classes suitable for query when selected
	//---------------------------------------------------------------------------------------
	
	/**
	 * If a class is selected in the class model view, and that class is mapped in all
	 * query sources of the query source view, note that class for use in query
	 * editor menus.
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		trace("selection changed");
		ISelection selection = event.getSelection();
		DataSourceView dsv = WorkBenchUtil.getDataSourceView(true);
		if (selection instanceof IStructuredSelection && ((IStructuredSelection)selection).size() == 1) {
			Object object = ((IStructuredSelection)selection).getFirstElement();
			LabelledEClass lec = null;
			if (object instanceof EClass) lec = new LabelledEClass((EClass)object);
			else if (object instanceof LabelledEClass) lec = (LabelledEClass) object;
			if ((lec != null) && (dsv != null) && (dsv.classMappedInAllActiveDataSources(lec)))
				menuStartClasses.put(lec.eClass().getName(), lec);
		}		
	}
	
	/**
	 * if the data source view shows any sources, they must all be mapped to
	 * the same class model. 
	 * That class model is used in menus to add fields to this query.
	 * Ensure the correct class model is showing in the class model view, 
	 * and make this editor a listener for selections of classes in that view
	 */
	private void connectToClassModel()
	{
		trace("Connect to class model");
		DataSourceView dsv = WorkBenchUtil.getDataSourceView(false);
		ClassModelView cmv = WorkBenchUtil.getClassModelView(false);
		if ((dsv != null) && (cmv != null) && (dsv.getClassModelPackage() != null))
				{cmv.initiateForQueryEditor(this, dsv.getClassModelPackage(), dsv.getClassModelURIString());}
	}

	/**
	 * When this editor gets focus, check the correct class model is showing and connect to it
	 */
	public void setFocus() 
	{
		connectToClassModel();
	}
	
	/**
	 * When this editor initialises, check the correct class model is showing and connect to it
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site,input);
		connectToClassModel();
	}
	
	//---------------------------------------------------------------------------------------
	//                  Creating actions and the cascade menu of fields to insert
	//---------------------------------------------------------------------------------------
	
	
	protected void createActions()
	{
		super.createActions();

		runQueryViewAct = new RunQueryToViewAction();
		setAction("RunQuery",runQueryViewAct);

		runQueryFileAct = new RunQueryToFileAction();
		setAction("SaveQueryResult",runQueryFileAct);
		
		runQueryShowSQLAct = new RunQueryShowSQLAction();
		setAction("ShowSQL",runQueryShowSQLAct);
		
		runQuerySaveXMLAction = new RunQuerySaveXMLAction();
		setAction("SaveXML",runQuerySaveXMLAction);
	}
	
	protected void editorContextMenuAboutToShow(IMenuManager menu) {

		runQueryViewAct.setDataSourceView(WorkBenchUtil.getDataSourceView(true));
		runQueryViewAct.setQueryEditor(this);

		runQueryFileAct.setDataSourceView(WorkBenchUtil.getDataSourceView(true));
		runQueryFileAct.setQueryEditor(this);

		runQueryShowSQLAct.setDataSourceView(WorkBenchUtil.getDataSourceView(true));
		runQueryShowSQLAct.setQueryEditor(this);

		runQuerySaveXMLAction.setDataSourceView(WorkBenchUtil.getDataSourceView(true));
		runQuerySaveXMLAction.setQueryEditor(this);

		try {
		addAction(menu, "RunQuery"); 
		addAction(menu, "SaveQueryResult"); 
		addAction(menu, "ShowSQL"); 
		addAction(menu, "SaveXML"); 
		addTextInsertActions(menu);
		super.editorContextMenuAboutToShow(menu);
		} 
		catch (Exception ex) {System.out.println ("Menu exception: " + ex.getMessage());}
	}
	
	/**
	 * add the cascading sub-menus of classes, properties and associations
	 */
	private void addTextInsertActions(IMenuManager menu)
	{
		trace("add menu items");
		MenuManager insertTextSubMenu = new MenuManager("Insert");
		for (Iterator <String> it = menuStartClasses.keySet().iterator();it.hasNext();)
		{
			String className = it.next();
			trace("add " + className);
			LabelledEClass theClass = menuStartClasses.get(className);
			MenuManager classSubMenu = new MenuManager(className);
			insertTextSubMenu.add(classSubMenu);
			// add menu items for the properties of this class
			trace("adding properties" );
			addPropertyMenuItems(classSubMenu,theClass,className);
			// add sub-menus for associations from this class
			Hashtable <String,EClass> visitedClasses = new Hashtable <String,EClass>();
			visitedClasses.put(className, theClass.eClass());
			addAssociationMenuItems(classSubMenu,theClass, visitedClasses, className,maximumMenuDepth);
		}
		// the 'Insert' sub-menu will only get shown if there are some class sub-menus below it
		menu.add(insertTextSubMenu); 
	}
	
	/**
	 * Add menu items and Actions to put text into the query for the properties 
	 * of a class
	 * @param classSubMenu sub-menu for the class reached by the menu trail
	 * @param ec EClass the class reached by the menu trail
	 * @param trailName text to be put into the query, up to an including the current class name
	 */
	private void addPropertyMenuItems(MenuManager classSubMenu, LabelledEClass lec, String trailName)
	{
		DataSourceView qsv = WorkBenchUtil.getDataSourceView(true);
		if (qsv == null) return;
		for (Iterator <EAttribute> it = lec.eClass().getEAllAttributes().iterator();it.hasNext();)
		{
			String propName = it.next().getName();
			// only add a property if it has property mappings in all active query sources
			if (qsv.propertyMappedInAllActiveDataSources(lec,propName))
			{
				String fieldName = trailName + "." + propName + " ";
				classSubMenu.add(new InsertQueryTextAction(this, propName, fieldName));
			}
		}
	}
	
	
	/**
	 * Add sub-menus for the associations of a class, leading to menu items and actions
	 * for the properties of classes reached by those associations
	 * @param classSubMenu sub-menu for the class reached by the menu trail
	 * @param ec EClass the class reached by the menu trail
	 * @param visitedClasses Vector of classes visited already in the trail
	 * of associations, not to be visited again
	 * @param trailName text to be put into the query, up to an including the current class name
	 */
	private void addAssociationMenuItems(MenuManager classSubMenu, LabelledEClass lec, 
			Hashtable <String,EClass> visitedClasses, String trailName,int menuDepth)
	{
		trace("Adding associations " + trailName);
		DataSourceView qsv = WorkBenchUtil.getDataSourceView(true);
		if (qsv == null) return;
		for (Iterator<EReference> it = lec.eClass().getEAllReferences().iterator(); it.hasNext();)
		{
			EReference er = it.next();
			String roleName = er.getName();
			EClass otherSuper = er.getEReferenceType();
			// iterate over all subclasses at the other end which inherit the association
			for (Iterator<EClass> is = ModelUtil.getAllSubClasses(otherSuper).iterator();is.hasNext();)
			{
				EClass other = is.next();
				LabelledEClass lother = new LabelledEClass(other);
				// do not include any association which would get back to a class visited already
				if (visitedClasses.get(other.getName()) == null)
				{
					/*  only add a sub-menu for an association if it has mappings in all
					 * active query sources  */
					if (qsv.associationMappedInAllActiveDataSources(lec,roleName,other))
					{
						// menu item names are '(role)class'
						String subMenuName = "(" + roleName + ")" + other.getName();
						String newTrailName = trailName + "." + subMenuName;
						MenuManager linkSubMenu = new MenuManager(subMenuName);
						classSubMenu.add(linkSubMenu);
						// add menu items for the properties of the class reached by the link
						addPropertyMenuItems(linkSubMenu,lother,newTrailName);
						/* add sub-menus for the further associations of the class reached by the link, 
						 * to a maximum depth. */
						if (menuDepth > 0)
						addAssociationMenuItems(linkSubMenu,lother, 
								newVisitedClasses(visitedClasses,other), newTrailName, menuDepth -1);
					}				
				}
			}
		}
	}
	
	/**
	 * @return a Hashtable of classes visited so far, extended by one more class 
	 * - without altering the original
	 */
	private Hashtable <String,EClass> newVisitedClasses
		(Hashtable <String,EClass> visitedClasses, EClass nextClass)
	{
		Hashtable <String,EClass> newVisitedClasses = new Hashtable <String,EClass>();
		newVisitedClasses.put(nextClass.getName(), nextClass);
		for (Iterator<String> ix = visitedClasses.keySet().iterator(); ix.hasNext();)
		{
			String cName = ix.next();
			newVisitedClasses.put(cName, visitedClasses.get(cName));
		}
		return newVisitedClasses;		
	}


	
	//---------------------------------------------------------------------------------------
	//                  Inserting text after menu selections
	//---------------------------------------------------------------------------------------
	
	/**
	 * insert a string of new text at the cursor
	 * @param text
	 */
	public void insertTextAtCursor(String text)
	{
		String currentQueryText = getDocumentProvider().getDocument(getEditorInput()).get();
	    String cursor = getCursorPosition();
	    System.out.println(cursor);
	    // do not allow text inserts in or before the initial 'Select'
	    if (insertPosition(cursor) > 5)
	    {
		    String newQueryText = insertAtCursor(currentQueryText,text,insertPosition(cursor));
		    getDocumentProvider().getDocument(getEditorInput()).set(newQueryText);	    	
	    }
	}
	
	/**
	 * number of characters of existing text before the new text insert
	 * @param cursor
	 * @return
	 */
	private int insertPosition(String cursor)
	{
		int pos = -1;		
		// strip off the initial '1 : ', convert to int and subtract 1
		if ((cursor != null) && (cursor.startsWith("1 : ")))
			pos = new Integer(cursor.substring(4)).intValue() -1;
		return pos;
	}
	
	private String insertAtCursor(String prevText,String insertText,int insertPos)
	{
		return (prevText.substring(0,insertPos) + insertText + prevText.substring(insertPos));
	}
	
	private void trace(String s) {if (tracing)  System.out.println(s);}

}
