package com.openMap1.mapper.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IObjectActionDelegate;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.writer.objectGetter;


/**
 * Action to run a translation from the mappings and wproc files.
 * 
 * @author robert
 *
 */

public class TranslateActionDelegate extends MakeTranslationActionDelegate
implements IObjectActionDelegate{

	
	public boolean isXSLTGeneration() {return false;}

	/**
	 * this method is defined to remove the compiler dependence of 
	 * MakeTranslationSActionDelegate on the class that implements interface XSLGenerator
	 * @param reader
	 * @param destFile
	 * @param oGet
	 */
	public void doXSLTGeneration(XOReader reader,IFile destFile,objectGetter oGet) throws MapperException
	{
		throw new MapperException("This menu action should not ask for XSLT Generation");
	}

}
