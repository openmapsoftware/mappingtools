package com.openMap1.mapper.util;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Element;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EClass; 
import org.eclipse.emf.ecore.EDataType; 
import org.eclipse.emf.ecore.EReference; 
import org.eclipse.emf.ecore.EPackage; 
import org.eclipse.emf.ecore.EcoreFactory; 
import org.eclipse.emf.ecore.EAttribute; 

import org.eclipse.emf.common.util.URI;


import com.openMap1.mapper.core.MapperException;


/**
 * Class to create an EMF Ecore class model from a DAML model.
 * 
 * @author robert
 *
 */
public class ClassModelFromDAML extends ClassModelMaker {
	
	private String filePath;
	
	/* We need to pre-store the class superclass relation because we do not know 
	 * what order the classes will be declared in. 
	 * For each class name, store a Vector of its superclasses. */
	private Hashtable<String, Vector<String>> classToSupers = new Hashtable<String, Vector<String>>();
	
	private Hashtable<String,EDataType> dataTypes = new Hashtable<String,EDataType>();

	public ClassModelFromDAML(URI uri){
		/* The URI may be a resource URI or a file URI; normalize it to the latter, and 
		 * then remove the String 'file:' from the normalised uri */
		filePath = FileUtil.editURIConverter().normalize(uri).toFileString();
	}
	
	/**
	 * create a EMF ecore model from the DAML file with location set in the constructor,
	 * and return its EPackage root 
	 * @return
	 */
	public EObject getRootOfUMLModel() throws MapperException
	{
		EObject ecoreRoot = null;
		Element damlRoot = XMLUtil.getRootElement(filePath);
		if (damlRoot != null) ecoreRoot = makeUMLModel(damlRoot);		
		return ecoreRoot;
	}
	
	/**
	 * Create an EMF ecore model from a DAML file as emitted by XMuLator
	 * @param damlRoot
	 * @return
	 */
	private EObject makeUMLModel(Element damlRoot)
	{
		classToSupers = new Hashtable<String, Vector<String>>();
		dataTypes = new Hashtable<String,EDataType>();
		EPackage ecoreRoot = null;
		if (XMLUtil.getLocalName(damlRoot).equals("RDF"))
		{
			for (Iterator<Element> it = XMLUtil.childElements(damlRoot).iterator();it.hasNext();)
			{
				Element el = it.next();
				String elType = XMLUtil.getLocalName(el);
				// 
				if (elType.equals("Ontology"))
				{
					ecoreRoot = EcoreFactory.eINSTANCE.createEPackage();
					ecoreRoot.setName("unnamed");
				}
				
				else if ((elType.equals("Class")) && (ecoreRoot != null)) try
				{
					handleClass(ecoreRoot, el);				
				}
				catch (Exception ex) {System.out.println("Exception handling class");}
				
				else if (elType.equals("DatatypeProperty")) try
				{
					handleProperty(ecoreRoot,el);
				}
				catch (Exception ex) {System.out.println("Exception handling property");}

				else if (elType.equals("ObjectProperty")) try
				{
					handleAssociation(ecoreRoot, el);
				}
				catch (Exception ex) {System.out.println("Exception handling association");}
				
				else show("Unrecognised DAML Element: " + elType);
			}
			// link classes with their superclasses
			if (ecoreRoot != null)
			for (Enumeration<String> en = classToSupers.keys(); en.hasMoreElements();)
			{
				String cName = en.nextElement();
				EClass c = (EClass)ecoreRoot.getEClassifier(cName);
				Vector<String> superNames = classToSupers.get(cName);
				if ((superNames != null) && (c != null))
					for (Iterator<String> iv = superNames.iterator(); iv.hasNext();)
					{
						EClass superC = (EClass)ecoreRoot.getEClassifier(iv.next());
						if (superC != null) c.getESuperTypes().add(superC);
					}
			}
		}
		else show("Root element of DAML file is not an 'RDF' element");
		return ecoreRoot;
	}
	
	private void handleClass(EPackage ecoreRoot, Element el)
	{
		String className = XMLUtil.getText(XMLUtil.firstNamedChild(el,"label"));
		EClass theClass = EcoreFactory.eINSTANCE.createEClass();
		theClass.setName(className);
		ecoreRoot.getEClassifiers().add(theClass);

		Vector<String> supers = new Vector<String>();					
		for (Iterator<Element> iu = XMLUtil.namedChildElements(el,"subClassOf").iterator(); iu.hasNext();)
		{
			String superName = iu.next().getAttribute("rdf:resource");	
			supers.addElement(superName);
		}
		classToSupers.put(className,supers);		
	}
	
	private void handleProperty(EPackage ecoreRoot, Element el)
	{
		String propName = XMLUtil.getText(XMLUtil.firstNamedChild(el,"label"));
		String className = XMLUtil.firstNamedChild(el,"domain").getAttribute("rdf:resource");
		String type = XMLUtil.firstNamedChild(el,"range").getAttribute("rdf:resource");
		// the value of 'type' is usually 'http://www.w3.org/2001/XMLSchema#string'
		EDataType dt = getEDataType(ecoreRoot, type);

		// assume all classes appear in the DAML file before any property, so the owning class can be found
		EClass c = (EClass)ecoreRoot.getEClassifier(className);
		if (c == null) {show("Cannot find class '" + className + "' for property '" + propName + "'");}
		else
		{
			EAttribute ea  = EcoreFactory.eINSTANCE.createEAttribute();
			ea.setName(propName);
			ea.setEType(dt);
			c.getEStructuralFeatures().add(ea);
		}		
	}
	
	/**
	 * Ensure the EPackage contains each required EDatatype only once
	 * @param type the full type name from the DAML file
	 * @return the EDataType created for it
	 */
	private EDataType getEDataType(EPackage ecoreRoot, String type)
	{
		// if the EDataType has already been made, just return it
		EDataType dt = dataTypes.get(type);
		// otherwise, make it and return it
		if (dt == null)
		{
			// find what Ecore data type name is most appropriate
			String eTypeName = "EString"; // fallback if type name not recognised
			StringTokenizer st = new StringTokenizer(type,"#");
			String shortType = "";
			while (st.hasMoreTokens()) {shortType = st.nextToken();} // i.e the type name after the last "#"
			if (shortType.equals("string")) eTypeName = "EString";
			else {System.out.println("New type name: " + shortType);}
			
			// make the EDataType and add it to the package
			dt = EcoreFactory.eINSTANCE.createEDataType();
			dt.setName(eTypeName);
			ecoreRoot.getEClassifiers().add(dt);
			
			// record it, so it won't be made again
			dataTypes.put(type,dt);
		}
		return dt;
	}
	
	private void handleAssociation(EPackage ecoreRoot, Element el)
	{
		String assocName = XMLUtil.getText(XMLUtil.firstNamedChild(el,"label"));
		Element endEl1 = XMLUtil.firstNamedChild(el,"domain"); 
		Element endEl2 = XMLUtil.firstNamedChild(el,"range"); 
		String className1 = endEl1.getAttribute("rdf:resource");
		String maxCardinality1 = endEl1.getAttribute("cardinality");
		String className2 = endEl2.getAttribute("rdf:resource");					
		String maxCardinality2 = endEl2.getAttribute("cardinality");
		// assume all classes appear in the DAML file before any associations, so both end classes can be found
		EClass c1 = (EClass)ecoreRoot.getEClassifier(className1);
		EClass c2 = (EClass)ecoreRoot.getEClassifier(className2);
		if (c1 == null) {show("Cannot find class '" + className1 + "' for association '" + assocName + "'");}
		else if (c2 == null) {show("Cannot find class '" + className2 + "' for association '" + assocName + "'");}
		else
		{
			// DAML does not define role names, so make them from the association name
			EReference r1 = EcoreFactory.eINSTANCE.createEReference();
			r1.setName(assocName + "_1");
			r1.setUpperBound(upperBound(maxCardinality1));
			/* Class EReference has no method 'setEReferenceType' analogous to 'getEReferenceType', 
			 * trying to set it reflectively gives a null pointer. Set it by setEType. 
			 * This is because EReferenceType is  derived feature, presumably derived from EType by 
			 * a class cast. */
			r1.setEType(c2);
			c1.getEStructuralFeatures().add(r1);
			

			EReference r2 = EcoreFactory.eINSTANCE.createEReference();
			r2.setName(assocName + "_2");
			r2.setUpperBound(upperBound(maxCardinality2));
			r2.setEType(c1);
			c2.getEStructuralFeatures().add(r2);
			
			r1.setEOpposite(r2);
			r2.setEOpposite(r1);
		}		
	}


	
	/**
	 * upper bound is 1 if cardinality is specified as 1;
	 * otherwise (cardinality unspecified) it is -1.
	 * @param cardinality
	 * @return
	 */
	private int upperBound(String cardinality)
	{
		if (cardinality.equals("1")) return 1;
		return -1;
	}
	
	private void show(String s) {System.out.println(s);}

}
