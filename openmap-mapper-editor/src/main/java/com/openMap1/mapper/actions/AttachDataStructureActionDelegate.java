package com.openMap1.mapper.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import org.eclipse.xsd.XSDSchema; // a kind of EObject

import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.presentation.DatabaseConnectWizard;
import com.openMap1.mapper.structures.SQLParser;
import com.openMap1.mapper.structures.XSDStructure;
import com.openMap1.mapper.structures.DBStructure;

import com.openMap1.mapper.converters.CSV_Wrapper;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.StructureType;

/**
 * Action to read in an XML schema  or RDBMS schema or csv file and use it as the basis for a mapped structure.
 * After doing this action, the user needs to choose a complex type or element to be the 
 * root of the mapped structure, setting the attribute of the top Mapped Structure node
 * @author robert
 *
 */
public class AttachDataStructureActionDelegate  extends MapperActionDelegate
    implements IObjectActionDelegate {
	
	static String CSV_WRAPPER_CLASSNAME = "com.openMap1.mapper.converters.CSV_Wrapper";

	@Override
	/**
	 * On running this action, do the following:
	 * 
	 * (1) Find out what kind of structure definition the MappedStructure uses - XSD or RDBMS or csv
	 * (2) Show a file dialogue for the user to select an XSD or csv file, or a connection dialogue
	 *     to make a database connection
	 * (3) Try to open the XSD file and read it into an ecore model,
	 *     or open the csv 
	 *     or to open the database to get its metadata
	 * (4) If the editor is not open, open it
	 * (5) Set the 'data structure location' attribute on the top 'Mapped structure' node
	 * (6) Find the set of allowed values for the top element type
	 * (7) attach the XSDStructure to the root MappedStructure object
	 */
	public void run(IAction action) {
		
		// (1) find out if the selected mapper file is expecting an XSD structure, or an RDMBS, or a csv file
		boolean isDatabaseStructure =  
			(mappedStructure().getStructureType().equals(StructureType.RDBMS));
		
		String wrapperClassName = mappedStructure().getMappingParameters().getWrapperClass();
		boolean isCSVStructure = ((wrapperClassName != null) && (wrapperClassName.equals(CSV_WRAPPER_CLASSNAME)));

		try{
			if (isDatabaseStructure) runForDatabaseStructure();
			else if (isCSVStructure) runForCSVStructure();
			// the last else case covers structure types: XSD, V2
			else runForXSDStructure();
		}
		catch (MapperException ex) 
		{
			ex.printStackTrace();
			showMessage(ex.getMessage());
		}
	}
	
	private void runForDatabaseStructure() throws MapperException
	{
		// allow the user to open an sql schema file
		String path = "";
		String[] exts = {"*.sql"}; 
		path = FileUtil.getFilePathFromUser(targetPart,exts,"Select SQL schema file",false);	
		
		// the user opened an SQL file
		if (!(path.equals(""))) try
		{
			FileInputStream sqlStream = new FileInputStream(path);
			Vector<String> lines = FileUtil.getLines(sqlStream);
			SQLParser parser = new SQLParser(lines);
			
			parser.parse();
			
			// If the editor is not open, open it
			MapperEditor me = OpenMapperEditor(selection);
			if (me == null) return;
			
			// expand the structure
			ElementDef newRoot = parser.makeTableStructure();
			setTreeStructure(me,newRoot);

		}
		catch (FileNotFoundException ex) {throw new MapperException(ex.getMessage());}
		
		/* the user cancelled the opportunity to open an SQL file, so 
		 * give the opportunity to open a database connection */
		else if (path.equals(""))
		{
			// (2) show a dialog for the user to choose a database connection
		    DatabaseConnectWizard wizard = new DatabaseConnectWizard();
		    wizard.init(PlatformUI.getWorkbench(),(IStructuredSelection)selection);
		    WizardDialog dialog = new WizardDialog
		         (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),wizard);
		    dialog.open();
		    path = wizard.dbConnect().connectString();
			
			// (3) get schema information from the database
			Connection con = wizard.dbConnect().con(); 
			DBStructure dbStructure = new DBStructure(con);
			
			// (4) If the editor is not open, open it
			MapperEditor me = OpenMapperEditor(selection);
			if (me == null) return;

			// (5) Set the 'data structure URL' property on the top 'Mapped structure' node
			setStructureURLProperty(me, path);
			
			// (6) find the set of allowed values for the top element type and name
			me.propertyValueSetProvider().notifyNewValueSupplier
			  ("MappedStructure","Top Element Type", dbStructure);
			me.propertyValueSetProvider().notifyNewValueSupplier
			  ("MappedStructure","Top Element Name", dbStructure);

			// (7) attach the database Structure to the root MappedStructure object
			WorkBenchUtil.mappingRoot(me).setStructureDefinition(dbStructure);		
		}

	}
	
	
	/**
	 * get a file name for an XML schema and read it
	 * (overridden for the V2 case)
	 * @throws MapperException
	 */
	public void runForXSDStructure() throws MapperException
	{
		String path = "";
		
		// (2) show a dialog for the user to choose an XSD file
		String[] exts = {"*.xsd"}; 
		path = FileUtil.getFilePathFromUser(targetPart,exts,"Select Data Structure Definition",false);			
		if (path.equals("")) return;
		
		// (3) Try to open the file and read it into an ECore model
		URI uri = FileUtil.URIFromPath(path);
		XSDSchema theSchema = XSDStructure.getXSDRoot(uri);
		if (theSchema == null) {showMessage("Failed to open XML Schema");return;}
		
		// (4) If the editor is not open, open it
		MapperEditor me = OpenMapperEditor(selection);
		if (me == null) return;
		
		// (5) Set the 'data structure URL' property on the top 'Mapped structure' node
		setStructureURLProperty(me, uri.toString());
		
		// (6) find the set of allowed values for the top element type and name
		XSDStructure xsd = new XSDStructure(theSchema);
		me.propertyValueSetProvider().notifyNewValueSupplier
		  ("MappedStructure","Top Element Type", xsd);
		me.propertyValueSetProvider().notifyNewValueSupplier
		  ("MappedStructure","Top Element Name", xsd);
		
		// (7) attach the XSDStructure to the root MappedStructure object
		mappedStructure().setStructureDefinition(xsd);
		
		System.out.println("Final namespaces: " + mappedStructure().getMappingParameters().getNameSpaces().size());
		
	}

	
	/**
	 * extract a csv mapping tree structure from an example csv file
	 * @throws MapperException
	 */
	private void runForCSVStructure() throws MapperException
	{
		String path = "";
		
		// (2) show a dialog for the user to choose a CSV file
		String[] exts = {"*.csv"}; 
		path = FileUtil.getFilePathFromUser(targetPart,exts,"Select Example of CSV Data Structure",false);			
		if (path.equals("")) return;
		
		// (3) Try to open the csv file
		Object input = null;
		try
		{
			input = new FileInputStream(new File(path));			
		}
		catch (Exception ex)
			{throw new MapperException("Cannot open csv file at '" + path + "'");}
		
		// (4) If the editor is not open, open it
		MapperEditor me = OpenMapperEditor(selection);
		if (me == null) return;
		
		// (5) Set the 'data structure URL' property on the top 'Mapped structure' node
		setStructureURLProperty(me, path);
		
		// (6) find the set of allowed values for the top element type and name
		CSV_Wrapper csvStructure = new CSV_Wrapper(WorkBenchUtil.mappingRoot(me),null);
		csvStructure.getStructure(input);
		me.propertyValueSetProvider().notifyNewValueSupplier
		  ("MappedStructure","Top Element Type", csvStructure);
		me.propertyValueSetProvider().notifyNewValueSupplier
		  ("MappedStructure","Top Element Name", csvStructure);
		
		// (7) attach the XSDStructure to the root MappedStructure object
		mappedStructure().setStructureDefinition(csvStructure);		
	}

	/**
	 * update the property 'XSDURL' of the top 'MappedStructure' node (but do not save the file)
	 */
	public boolean setStructureURLProperty(MapperEditor me, String path)
	{
		MappedStructure ms  = WorkBenchUtil.mappingRoot(me);
		if (ms == null) return false;
		EditingDomain ed = me.editingDomain();
		SetCommand sc2 = new SetCommand(ed,ms,
				MapperPackage.eINSTANCE.getMappedStructure_StructureURL(),
				path);
		ed.getCommandStack().execute(sc2);	
		return true;
	}
	
	/**
	 * update the ElementDef tree structure attached to the top 'MappedStructure' node (but do not save the file)
	 */
	public boolean setTreeStructure(MapperEditor me, ElementDef newRoot)
	{
		MappedStructure ms  = WorkBenchUtil.mappingRoot(me);
		if (ms == null) return false;
		EditingDomain ed = me.editingDomain();
		SetCommand sc2 = new SetCommand(ed,ms,
				MapperPackage.eINSTANCE.getMappedStructure_RootElement(),
				newRoot);
		ed.getCommandStack().execute(sc2);	
		return true;
	}


}
