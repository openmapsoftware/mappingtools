package com.openMap1.mapper.converters;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;

/** 
 * Class to do post-processing on an EMF Ecore model got
 * from the LRA class model through mappings.
 * 
 * The conversion consists of changing any EReference, whose target class 
 * is in the uml kernel package, to an EAttribute of the appropriate type.
 * 
 * @author Robert
 *
 */

public class LRAConverter {
	
	private boolean tracing = false;
	
	//------------------------------------------------------------------------------------------------------------
	//                   Convert from a dynamic EObject model to a true ECore model
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param model an Ecore model made generically of DynamicEObjectImpls
	 * @param targetLocation where it is going to be saved
	 * Convert the model to an Ecore model made with the EMF ECore package, and save 
	 * it with the same name to the target location - so it overwrites the first model.
	 */
	public void saveAsEcore(EObject model,URI modelInstanceURI) throws MapperException
	{
		trace("Overwrite of " + modelInstanceURI.toString());

		// copy to a true Ecore model
		EPackage newModel = copyModel(model);
		
		// convert the EReferences to kernel datatype classes, to EAttributes - only if the top package is 'lra'
		String packageName = newModel.getName();
		boolean doPostProcess = ((packageName != null) && (packageName.equals("lra")));
		if (doPostProcess) postProcess(newModel);
		
		trace("Model converted");

		// register the Ecore package
		EcorePackage.eINSTANCE.getEFactoryInstance(); 

		ResourceSet resourceSet = new ResourceSetImpl();

		// register the factory
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
			put("ecore", new XMIResourceFactoryImpl());

		// create and fill the resource
		Resource resource = resourceSet.createResource(modelInstanceURI);
		resource.getContents().add(newModel);

		// save the resource
		try {
			resource.save(null);
			trace("written");
		}
		catch (IOException ex) {throw new MapperException(ex.getMessage());}		
	}
	
	
	/**
	 * 
	 * @param model an Ecore model made generically of DynamidEObjectImpls
	 * @return the model converted to the classes of Ecore - which 'knows' it is an instance of the Ecore model
	 */
	private EPackage copyModel(EObject model)
	{
		// lookup table from old to new objects
		Hashtable<EObject,EObject> newObjects = new Hashtable<EObject,EObject>();

		// recursive copy, doing only attributes and containment relations, and filling the lookup table
		EPackage newModel = (EPackage)copyTree(model,newObjects);
		
		// copy across non-containment EReferences, using the lookup table
		copyNonContainments(model, newObjects);
		
		return newModel;
	}
	
	/**
	 * @param modelObj a subtree of an Ecore model made generically of DynamidEObjectImpls
	 * @param newObjects lookup table from old to new objects, to be filled
	 * @return the subtree copied to proper ECore classes, doing only EAttributes 
	 * and non-containment EReferences
	 */
	private EObject copyTree(EObject modelObj,Hashtable<EObject,EObject> newObjects)
	{
		ENamedElement result = EcoreFactory.eINSTANCE.createEPackage();

		String className = modelObj.eClass().getName(); // name of the metamodel class
		int classId = getClassId(className);
		trace("Copying " + className);

		// create the appropriate ECore object, and store it in the lookup table
		if (classId == EPACKAGE) 
		{
			result = EcoreFactory.eINSTANCE.createEPackage();
			result.setName((String)getValue(modelObj,"name"));
			
			EList<?> subPackages = (EList<?>)getValue(modelObj,"eSubpackages");
			for (Iterator<?> ip = subPackages.iterator();ip.hasNext();)
				(((EPackage)result).getESubpackages()).add((EPackage)copyTree((EObject)ip.next(),newObjects));
			
			EList<?> classes = (EList<?>)getValue(modelObj,"eClassifiers");
			for (Iterator<?> ip = classes.iterator();ip.hasNext();)
			{
				EObject next = (EObject)ip.next();
				String cName = (String)getValue(next,"name"); // name of the class in the model
				if (!badClass(cName)) (((EPackage)result).getEClassifiers()).add((EClass)copyTree(next,newObjects));
			}
			
		}
		
		if (classId == ECLASS) 
		{
			result = EcoreFactory.eINSTANCE.createEClass();
			result.setName((String)getValue(modelObj,"name"));
			
			EList<?> references = (EList<?>)getValue(modelObj,"eStructuralFeatures");
			for (Iterator<?> ip = references.iterator();ip.hasNext();)
				(((EClass)result).getEStructuralFeatures()).add((EReference)copyTree((EObject)ip.next(),newObjects));
		}
		if (classId == EATTRIBUTE) 
		{
			result = EcoreFactory.eINSTANCE.createEAttribute();
			result.setName((String)getValue(modelObj,"name"));
			((EAttribute)result).setLowerBound((Integer)getValue(modelObj,"lowerBound"));
		}
		if (classId == EREFERENCE) 
		{
			result = EcoreFactory.eINSTANCE.createEReference();
			result.setName((String)getValue(modelObj,"name"));
			((EReference)result).setLowerBound((Integer)getValue(modelObj,"lowerBound"));
			((EReference)result).setUpperBound((Integer)getValue(modelObj,"upperBound"));
		}
		newObjects.put(modelObj, result);
		
		
		return result;
	}
	
	// spurious classes that can be removed at the first pass (there are no EReferences to them)
	private boolean badClass(String cName)
	{
		boolean badClass = ((cName == null)||
				(cName.equals("Text"))||
				(cName.startsWith("$diagram"))||
				(cName.startsWith("extract")));
		return badClass;
	}
	
	/**
	 * 
	 * @param obj an EObject in the Ecore model
	 * @param featureName the name of some feature
	 * @return the value of the feature (an EList if it is multi-valued)
	 */
	private Object getValue(EObject obj, String featureName)
	{
		Object value = null;
		for (Iterator<EStructuralFeature> it = obj.eClass().getEAllStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature feature = it.next();
			if (feature.getName().equals(featureName)) value = obj.eGet(feature);
		}
		return value;
	}
	
	/**
	 * @param model a subtree of an Ecore model made generically of DynamidEObjectImpls
	 * @param newModel the subtree copied to proper Ecore classes, but not yet with non-containment ralations
	 * @param newObjects already-filled lookup table from old to new objects
	 * copy the non-containment relations across from the old to the new EObject, using the lookup table;
	 * and recursively descend via containment relations
	 */
	private void copyNonContainments(EObject modelObj, Hashtable<EObject,EObject> newObjects)
	{
		EObject newModelObj = newObjects.get(modelObj);
		if (newModelObj == null) return;
		int id = getClassId(modelObj);
		
		if (id == ECLASS)
		{
			EList<?> supers = (EList<?>)getValue(modelObj,"eSuperTypes");
			for (Iterator<?> ip = supers.iterator();ip.hasNext();)
			{
				EObject oldSuper = (EObject)ip.next();
				EObject newSuper = newObjects.get(oldSuper);
				if (newSuper != null) (((EClass)newModelObj).getESuperTypes()).add((EClass)newSuper);							
			}
		}
		
		else if (id == EREFERENCE)
		{
			EReference ref = (EReference)newModelObj;
			Object oldTarget = getValue(modelObj,"eType");
			if (oldTarget != null)
			{
				EClass target = (EClass)newObjects.get((EObject)oldTarget);
				ref.setEType(target);
				
				// if the target class is in a sub-package of the datatypes package, make the EReference a containment
				EPackage pack = target.getEPackage();
				if (pack != null)
				{
					EPackage superP = pack.getESuperPackage();
					if ((superP != null) && (superP.getName().equals("datatypes")))
						ref.setContainment(true);
				}
			}
		}
		
		// recursively descend the containment tree
		for (Iterator<EObject> it = modelObj.eContents().iterator(); it.hasNext();)
			copyNonContainments(it.next(),newObjects);
	}
	
	private static int EPACKAGE = 0;
	private static int ECLASS = 1;
	private static int EREFERENCE = 2;
	private static int EATTRIBUTE = 3;
	
	/**
	 * @param className the name of an ECore class
	 * @return and integer code for the class
	 */
	private int getClassId(String className)
	{
		int id = -1;
		if (className.equals("EPackage")) id = EPACKAGE;
		else if (className.equals("EClass")) id = ECLASS;
		else if (className.equals("EReference")) id = EREFERENCE;
		else if (className.equals("EAttribute")) id = EATTRIBUTE;
		else System.out.println("Unrecognised ECore class " + className);
		return id;
	}
	
	private int getClassId(EObject modelObj)
	{
		return getClassId(modelObj.eClass().getName());
	}
	
	//------------------------------------------------------------------------------------------------------------
	//                    After conversion to a true Ecore model, post-processing
	//------------------------------------------------------------------------------------------------------------
	
	
	private void postProcess(EPackage classModel)
	{
		Vector<EClass> allClasses = ModelUtil.getAllClasses(classModel);
		for (Iterator<EClass> it = allClasses.iterator(); it.hasNext();)
		{
			EClass theClass = it.next();
			postProcessLRAClass(theClass);
		}
		
	}
	
	private void postProcessLRAClass(EClass theClass)
	{
		// remove any '.' from the class name
		theClass.setName(removeStops(theClass.getName()));
		
		// look at EReferences
		for (Iterator<EReference> ir = theClass.getEReferences().iterator();ir.hasNext();)
		{
			EReference ref = ir.next();
			EClass target = (EClass)ref.getEType();
			// EReferences with maximum multiplicity 1 and no target class get converted to EAttributes of data type EString
			if (target == null)
			{
				// only add an EAttribute if the max multiplicity is 1
				if (ref.getUpperBound() == 1)
				{
					// make the EAttribute
					theClass.getEStructuralFeatures().add(makeAttribute(ref));	
					trace("Attribute " + ref.getName() + "; EString");					
				}
				
				// remove the EReference (even if the max multiplicity is -1)
				theClass.getEStructuralFeatures().remove(ref);					
			}
			// EReference points to a datatypes kernel or derived class; convert it to an EAttribute of the right data type
			else if (target != null)
			{
				String targetPackageName = target.getEPackage().getName();
				if ((targetPackageName.equals("kernel"))|(targetPackageName.equals("derived")))
				{
					// make the EAttribute
					theClass.getEStructuralFeatures().add(makeAttribute(ref));	
					trace("Attribute " + ref.getName() + "; type " + target.getName());
					
					// remove the EReference
					theClass.getEStructuralFeatures().remove(ref);					
				}
			}
		}
	}
	
	/*
	 * replace  '.' by underscore in a name
	 */
	private String removeStops(String name)
	{
		String newName = "";
		StringTokenizer st = new StringTokenizer(name,".");
		while (st.hasMoreTokens())
		{
			newName = newName + st.nextToken();
			if (st.hasMoreTokens()) newName = newName + "_";
		}
		return newName;
	}
	
	private EAttribute makeAttribute(EReference ref)
	{
		EAttribute att = EcoreFactory.eINSTANCE.createEAttribute();
		EClass target = (EClass)ref.getEType();
		att.setName(ref.getName());
		att.setEType(getDataType(target));
		att.setLowerBound(ref.getLowerBound());
		return att;
	}
	
	private EDataType getDataType(EClass target)
	{
		EDataType theType = EcorePackage.eINSTANCE.getEString(); // default if no better
		if (target != null)
		{
			String typeName = target.getName();
			String superTypeName = getSuperName(target);
			if (superTypeName.equals("String")) {}
			// do not let Integer convert up to Real
			else if (typeName.equals("Integer")) {theType = EcorePackage.eINSTANCE.getEInt();}
			else if (superTypeName.equals("Real")) {theType = EcorePackage.eINSTANCE.getEDouble();}
			else if (superTypeName.equals("Boolean")) {theType = EcorePackage.eINSTANCE.getEBoolean();}			
		}
		return theType;
	}
	
	// name of a class or its highest superclass - which will often turn out to be 'String'
	private String getSuperName(EClass target)
	{
		if (target.getESuperTypes().size() > 0)
			return getSuperName(target.getESuperTypes().get(0));
		return target.getName();
	}

	//------------------------------------------------------------------------------------------------------------------
	//                                         Checks and trivia
	//------------------------------------------------------------------------------------------------------------------
	
	
	public void modelCheck(EObject root)
	{
		System.out.println("\nCheck of superclasses");
		for (Iterator<EObject> it = root.eAllContents();it.hasNext();)
		{
			EObject node = it.next();
			if (node.eClass().getName().equals("EClass"))
			{
				String answer = "Class " + getName(node) + "; superclasses ";
				boolean noSupers = true;
				for (Iterator<EReference> ir = node.eClass().getEAllReferences().iterator(); ir.hasNext();)
				{
					EReference ref = ir.next();
					if (ref.getName().equals("eSuperTypes"))
					{
						EList<?> supers = (EList<?>)node.eGet(ref);
						for (Iterator<?> ic = supers.iterator(); ic.hasNext();)
						{
							EObject next = (EObject)ic.next();
							answer = answer + getName(next) + " ";
							noSupers = false;
						}
					}
				}
				if (noSupers) System.out.println(answer);
			}
		}
	}
	
	private String getName(EObject node)
	{
		String className = null;
		for (Iterator<EAttribute> ia = node.eClass().getEAllAttributes().iterator();ia.hasNext();)
		{
			EAttribute att = ia.next();
			if (att.getName().equals("name")) className = (String)node.eGet(att);
		}
		return className;
	}

	private void trace(String s) {if (tracing) System.out.println(s);}


}
