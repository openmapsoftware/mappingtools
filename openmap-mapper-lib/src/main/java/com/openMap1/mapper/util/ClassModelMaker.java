package com.openMap1.mapper.util;



import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;

import com.openMap1.mapper.core.MapperException;
import java.io.IOException;

/**
 * Harness for classes that make EMF class models from
 * other representations of class models.
 * 
 * @author robert
 *
 */
public class ClassModelMaker {

	/**
	 * Use one of a number of file types to make a UML model, and return its package root node
	 * @param uriString String the path to the file
	 * @return EObject umlRoot the Package root of the UML2 EMF model
	 * @throws Exception
	 */
	public static EObject makeClassModelFromFile(URI uri) throws MapperException, IOException
	{
		EObject ecoreRoot = null;
		if (uri.fileExtension().equals("ecore"))
		{
			// register the ecore package
			EcorePackage.eINSTANCE.getEFactoryInstance(); 
			ecoreRoot = FileUtil.getEMFModelRoot(uri);
		}
		else if (uri.fileExtension().equals("daml"))
		{
			ClassModelFromDAML cmd = new ClassModelFromDAML(uri);
			ecoreRoot = cmd.getRootOfUMLModel();
		}
		return ecoreRoot;
	}
	

}
