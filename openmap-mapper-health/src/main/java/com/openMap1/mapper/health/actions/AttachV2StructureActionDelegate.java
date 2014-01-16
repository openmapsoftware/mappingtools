package com.openMap1.mapper.health.actions;

import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IObjectActionDelegate;
import org.w3c.dom.Element;

import com.openMap1.mapper.actions.AttachDataStructureActionDelegate;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.structures.V2StructureDef;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.StructureType;

/**
 * Action to attach a V2 structure definition to a mapping set
 * 
 * @author robert
 *
 */
public class AttachV2StructureActionDelegate  extends AttachDataStructureActionDelegate
implements IObjectActionDelegate{

	
	/**
	 * get a file name for an MWB V2 structure definition and read it
	 */
	public void runForXSDStructure() throws MapperException
	{
		String path = "";
		
		// (2) show a dialog for the user to choose an MWB file
		String[] exts = {"*.xml"}; 
		path = FileUtil.getFilePathFromUser(targetPart,exts,"Select V2 Message Structure Definition",false);			
		if (path.equals("")) return;
		
		// (3) Try to open the file and read it
		Element mwbRoot = XMLUtil.getRootElement(path); // throws a MapperException if there are problems
		
		// (4) If the editor is not open, open it
		MapperEditor me = OpenMapperEditor(selection);
		if (me == null) return;
		
		// (5) Set the 'data structure URL' property on the top 'Mapped structure' node
		URI uri = FileUtil.URIFromPath(path);
		setStructureURLProperty(me, uri.toString());
		WorkBenchUtil.mappingRoot(me).setStructureType(StructureType.V2);
		
		// (6) find the set of allowed values for the top element type and name
		V2StructureDef v2StructureDef = new V2StructureDef(mwbRoot);
		me.propertyValueSetProvider().notifyNewValueSupplier
		  ("MappedStructure","Top Element Type", v2StructureDef);
		me.propertyValueSetProvider().notifyNewValueSupplier
		  ("MappedStructure","Top Element Name", v2StructureDef);
		
		// (7) attach the Structure Definition to the root MappedStructure object
		WorkBenchUtil.mappingRoot(me).setStructureDefinition(v2StructureDef);
		
	}

}
