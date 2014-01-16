package com.openMap1.mapper.actions;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;

public class MakeEcoreFromXMIActionDelegate 
implements IObjectActionDelegate{

	public IWorkbenchPart targetPart; // where this action was invoked from
	public ISelection selection;
	
	private Hashtable<String,EClass> allClasses;

	private Hashtable<String,Element> assocEls;
	
	public void run(IAction action) {

		String projectName = getSelectedProject().getName();
		
		try {
			
			// (1) find the location of the selected XMI file
		    IFile XMIFile = getSelectedFile();
		    String xmiFilePath = XMIFile.getLocation().toString();
		    // System.out.println("XMI file location: " + xmiFilePath);

		    // (2) Open the XMI as an XML file
			Element XMIRoot = XMLUtil.getRootElement(xmiFilePath);
			if (XMIRoot == null) throw new MapperException("Cannot open XMI file at " + xmiFilePath);
			
			// (3) create the Ecore model
			EPackage topPackage = makeEcoreModel(XMIRoot);

			//the Ecore model is stored in the ClassModel folder of the same project as the selected XMI file
			String ecoreFolderPath = "platform:/resource/" + projectName + "/ClassModel/";
		    String fileName = XMIFile.getName();
		    String ecoreModelLocation = ecoreFolderPath + fileName.substring(0,fileName.length()-3) + "ecore";
			
			ModelUtil.savePackage(ecoreModelLocation, topPackage);
			
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			showMessage("Failed to convert XMI file to Ecore model: " + ex.getMessage());
		}
	}
	
	/**
	 * 
	 * @param XMIRoot
	 * @return
	 * @throws MapperException
	 */
	private EPackage  makeEcoreModel(Element XMIRoot) throws MapperException
	{
		allClasses = new Hashtable<String,EClass>() ;
		assocEls = new Hashtable<String,Element>() ;

		Element modelEl = XMLUtil.firstNamedChild(XMIRoot, "Model");
		if (modelEl == null) throw new MapperException("Failed to find top 'Model' element");
		
		Vector<Element> topPackages = XMLUtil.namedChildElements(modelEl, "packagedElement");
		if (topPackages.size() != 1)  throw new MapperException("Found " + topPackages.size() + " top packages");
		
		Element topPackageEl = topPackages.get(0);
		EPackage topPackage = EcoreFactory.eINSTANCE.createEPackage();
		
		// make all packages and classes, with their attributes
		makeClassesAndSubPackages(topPackage,topPackageEl);
		
		// make all two-ended associations
		for (Enumeration<Element> en = assocEls.elements();en.hasMoreElements();)
			addTwoEndedAssociation(en.nextElement());
		
		// make all associations
		addOneEndedAssociations(topPackageEl);

		return topPackage;		
	}

	/**
	 * create all classes in this package, and recursively create sub-packages and their classes
	 * @param thePackage
	 * @param thePackageEl
	 */
	private void makeClassesAndSubPackages(EPackage thePackage,Element thePackageEl) throws MapperException
	{
		// name the package
		String packageName = thePackageEl.getAttribute("name");
		thePackage.setName(packageName);
		
		// add all classes in the package, and recursively add sub-packages
		for (Iterator<Element> it = XMLUtil.namedChildElements(thePackageEl, "packagedElement").iterator(); it.hasNext();)
		{
			Element next = it.next();
			String type = next.getAttribute("xmi:type");

			if (type.equals("uml:Class"))
			{
				addClass(thePackage,next);
			}

			// store associations for the next pass
			if (type.equals("uml:Association"))
			{
				String assocId = next.getAttribute("xmi:id");
				assocEls.put(assocId, next);
			}

			if (type.equals("uml:Package"))
			{
				EPackage nextPackage = EcoreFactory.eINSTANCE.createEPackage();
				thePackage.getESubpackages().add(nextPackage);
				makeClassesAndSubPackages(nextPackage, next);
			}
		}		
	}
	
	/**
	 * 
	 * @param assocEl
	 */
	private void addTwoEndedAssociation(Element assocEl)  throws MapperException
	{
		Vector<Element> ends = XMLUtil.namedChildElements(assocEl, "ownedEnd");
		if (ends.size() == 2)
		{
			//create the two EReferences
			EReference ref0 = makeEReference(ends.get(0));
			EReference ref1 = makeEReference(ends.get(1));

			// set the owning class of each EReference
			((EClass)ref0.getEType()).getEStructuralFeatures().add(ref1);
			((EClass)ref1.getEType()).getEStructuralFeatures().add(ref0);
			
			// make them opposites of each other
			ref0.setEOpposite(ref1);
		}
	}

	/**
	 * 
	 * @param theClass
	 * @param assocEl
	 * @throws MapperException
	 */
	private void addOneEndedAssociation(EClass theClass,Element assocEl) throws MapperException
	{
		Vector<Element> ends = XMLUtil.namedChildElements(assocEl, "ownedEnd");
		if (ends.size() == 1)
		{
			//create the EReference
			EReference ref = makeEReference(ends.get(0));
			theClass.getEStructuralFeatures().add(ref);
		}		
	}

	
	/**
	 * 
	 * @param ownedEnd
	 * @return an EReference made from it
	 * @throws MapperException
	 */
	private EReference makeEReference(Element ownedEnd) throws MapperException
	{
		EReference ref = EcoreFactory.eINSTANCE.createEReference();
		ref.setName(ownedEnd.getAttribute("name"));

		String typeId = XMLUtil.firstNamedChild(ownedEnd, "type").getAttribute("xmi:idref");
		EClass target = allClasses.get(typeId);
		if (target == null) throw new MapperException("Cannot find target class with id " + typeId);
		ref.setEType(target);

		Element lower = XMLUtil.firstNamedChild(ownedEnd, "lowerValue");
		if (lower != null) ref.setLowerBound(new Integer(lower.getAttribute("value")).intValue());
		Element upper = XMLUtil.firstNamedChild(ownedEnd, "upperValue");
		if (upper != null) ref.setUpperBound(new Integer(upper.getAttribute("value")).intValue());

		return ref;
	}


	/**
	 * add associations and superclass relations, recursively going down through packages
	 * @param thePackage
	 * @param thePackageEl
	 */
	private void addOneEndedAssociations(Element thePackageEl) throws MapperException
	{
		for (Iterator<Element> it = XMLUtil.namedChildElements(thePackageEl, "packagedElement").iterator(); it.hasNext();)
		{
			Element next = it.next();
			String type = next.getAttribute("xmi:type");

			if (type.equals("uml:Class"))
			{
				secondPassClass(next);
			}

			if (type.equals("uml:Package"))
			{
				addOneEndedAssociations(next);
			}
		}		
	}

	/**
	 * add a class and its attributes to a package
	 * @param thePackage
	 * @param classEl
	 * @throws MapperException
	 */
	private void addClass(EPackage thePackage,Element classEl)  throws MapperException
	{
		String id = classEl.getAttribute("xmi:id");
		EClass theClass = EcoreFactory.eINSTANCE.createEClass();
		theClass.setName(classEl.getAttribute("name"));
		allClasses.put(id, theClass);
		thePackage.getEClassifiers().add(theClass);
		
		for (Iterator<Element> it = XMLUtil.namedChildElements(classEl, "ownedAttribute").iterator(); it.hasNext();)
		{
			Element attEl = it.next();
			if (attEl.getAttribute("association").equals(""))
			{
				EAttribute theAtt = EcoreFactory.eINSTANCE.createEAttribute();
				theAtt.setName(attEl.getAttribute("name"));
				theClass.getEStructuralFeatures().add(theAtt);				
			}
		}
	}
	
	/**
	 * 
	 * @param classEl
	 * @throws MapperException
	 */
	private void secondPassClass(Element classEl) throws MapperException
	{
		String id = classEl.getAttribute("xmi:id");
		EClass theClass = allClasses.get(id);
		if (theClass == null) throw new MapperException("Cannot find class with id " + id);

		// find all superclasses of the class
		for (Iterator<Element> it = XMLUtil.namedChildElements(classEl, "generalization").iterator(); it.hasNext();)
		{
			Element genEl = it.next();
			String genId = genEl.getAttribute("general");
			EClass genClass = allClasses.get(genId);
			if (genClass == null) System.out.println("Cannot find superclass with id " + genId + " for class " + theClass.getName());
			else theClass.getESuperTypes().add(genClass);
		}		
		
		// add one-ended associations
		for (Iterator<Element> it = XMLUtil.namedChildElements(classEl, "ownedAttribute").iterator(); it.hasNext();)
		{
			Element attEl = it.next();
			String assocId = attEl.getAttribute("association");
			if (!assocId.equals(""))
			{
				Element assocEl = assocEls.get(assocId);
				if (assocEl == null)  {} //throw new MapperException("Cannot find association with id " + assocId);
				else addOneEndedAssociation(theClass,assocEl);
			}
		}
	}

	
	//----------------------------------------------------------------------------------------------
	//                                    Eclipse plumbing
	//----------------------------------------------------------------------------------------------

	
	/**
	 * @return the project in which the selected mif file is located
	 */
	protected IProject getSelectedProject()
	{
		if (getSelectedFile() != null) return getSelectedFile().getProject();
		return null;
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	protected void showMessage(String title, String message) {
		MessageDialog.openInformation(
			targetPart.getSite().getShell(),
			title,
			message);
	}

	/**
	 * default if you can't be bothered to make up a message title
	 * @param message
	 */
	protected void showMessage(String message) 
		{showMessage("Error",message);}


	//cache the target part so we can get the shell
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
	
	/**
	 * @return the file containing the selected XMI
	 */
	protected IFile getSelectedFile()
	{
		if (selection instanceof IStructuredSelection)
		{
			Object el = ((IStructuredSelection)selection).getFirstElement();
			if (el instanceof IFile) return (IFile)el;
		}
		return null;
	}

}
