package com.openMap1.mapper.fhir;

import org.eclipse.ui.IObjectActionDelegate;
import org.w3c.dom.Element;

import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.actions.AttachDataStructureActionDelegate;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;

public class AttachFHIRProfileActionDelegate   extends AttachDataStructureActionDelegate
implements IObjectActionDelegate{
	
	/**
	 * get a file name for an FHIR structure definition and read it
	 */
	public void runForXSDStructure() throws MapperException
	{
		String path = "";
		
		// (2) show a dialog for the user to choose a file
		String[] exts = {"*.xml"}; 
		path = FileUtil.getFilePathFromUser(targetPart,exts,"Select FHIR profile",false);	
		System.out.println("Path:" + path);
		if (path.equals("")) return;
		
		// (3) Try to open the file and read it
		Element fhirProfileRoot = XMLUtil.getRootElement(path); // throws a MapperException if there are problems
		
		// (4) If the editor is not open, open it
		MapperEditor me = OpenMapperEditor(selection);
		if (me == null) return;
		MappedStructure ms = WorkBenchUtil.mappingRoot(me);
		
		// (5) Set the 'data structure URL' property on the top 'Mapped structure' node
		/* URI uri = FileUtil.URIFromPath(path);
		setStructureURLProperty(me, uri.toString());
		WorkBenchUtil.mappingRoot(me).setStructureType(StructureType.XSD); */
		
		// (6) find the set of allowed values for the top element type and name
		FHIRStructureDef fhirStructureDef = new FHIRStructureDef(fhirProfileRoot,path,ms);
		me.propertyValueSetProvider().notifyNewValueSupplier
		  ("MappedStructure","Top Element Type", fhirStructureDef);
		me.propertyValueSetProvider().notifyNewValueSupplier
		  ("MappedStructure","Top Element Name", fhirStructureDef);
		
		// (7) attach the Structure Definition to the root MappedStructure object
		ms.setStructureDefinition(fhirStructureDef);
		
		// attach the top node we want
		ElementDef topNode = fhirStructureDef.typeStructure("Bundle");
		ms.setRootElement(topNode);
		
	}



}
