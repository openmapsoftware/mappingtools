package com.openMap1.mapper.reader;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import com.openMap1.mapper.reader.EMFInstanceFactory;
import com.openMap1.mapper.reader.EMFInstanceFactoryImpl;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.Timer;


/**
 * The interface implemented by this class has one method, to make a resource
 * containing an instance of an EMF model, using an
 * XML document which represents an instance of the model
 * and a set of mappings which define how the XML instance 
 * represents the instance of the model
 * 
 * This class is intended to work without importing any of the generated
 * classes for a model.
 * 
 * @author robert
 *
 */

public class GenericEMFInstanceFactoryImpl extends EMFInstanceFactoryImpl
   implements EMFInstanceFactory{
	
	//------------------------------------------------------------------------------------
	//                        constructor makes a timer
	//-----------------------------------------------------------------------------------
	
	public GenericEMFInstanceFactoryImpl()
	{
		timer = new Timer("EMF Instance factory");
	}
	
	//------------------------------------------------------------------------------------
	//                        code dependent on the specific model
	//-----------------------------------------------------------------------------------
	
	/** create a model object with the right class (without using a generated package)*/
	public EObject createModelObject(String qualifiedClassName)
	 {
		classModel.setNsPrefix(NSPrefix);
		classModel.setNsURI(NSUri);
		String packageName = ModelUtil.getPackageName(qualifiedClassName);
		EPackage thePackage = ModelUtil.getNamedPackage(classModel, packageName);
		EClass theClass = ModelUtil.getNamedClass(classModel, qualifiedClassName);
		if (theClass != null) 
		{			
			if (!theClass.isAbstract()) return thePackage.getEFactoryInstance().create(theClass);
			else 
			{
				System.out.println("Abstract class " + qualifiedClassName);
				return null;
			} 
		}
		else {System.out.println("Null EObject of class " + qualifiedClassName);} 
		return null;
	 }

}
