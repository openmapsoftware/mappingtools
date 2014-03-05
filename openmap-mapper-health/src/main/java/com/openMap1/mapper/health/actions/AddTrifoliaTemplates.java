package com.openMap1.mapper.health.actions;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.w3c.dom.Element;

import com.openMap1.mapper.actions.MapperActionDelegate;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.health.cda.TrifoliaConstraint;
import com.openMap1.mapper.health.cda.TrifoliaTemplate;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;

public class AddTrifoliaTemplates extends MapperActionDelegate
implements IObjectActionDelegate{
	
	// the class model that is being extended, and will be saved to a new location
	EPackage classModel;
	
	// the templates that have been read in
	private Vector<TrifoliaTemplate> templates;
	
	// templates keyed by template id
	private Hashtable<String,TrifoliaTemplate> templatesById;
	
	// key = a template id; value = all EClasses having that template
	private Hashtable<String,Vector<EClass>> classesByTemplate;
	
	// key = an EClass; value = all EReferences to that class
	private Hashtable<EClass,Vector<EReference>> associationsByTargetClass;
	
	 // key = a templateId; value = a list of the templateIds that contain it
	private Hashtable<String,Vector<TrifoliaTemplate>> allContainingTemplates;
	
	// content of a csv file with one row per template constraint
	private Vector<String[]> csvOfConstraints;
	
	// column headers for csv file
	private String[] csvHeader = {"Template_Name","Template_id","XPath","Constraint_id","Card.","Value","Comment","Response"};
	
	
	public void run(IAction action) 
	{		
		try
		{
			if (selection instanceof IStructuredSelection)
			{
				Object el = ((IStructuredSelection)selection).getFirstElement();
				if (el instanceof IFile)
				{
					// (1) get the selected Ecore class model
					classModel = EclipseFileUtil.getClassModel((IFile)el);
					
					// (2) get the Trifolia XML file
					String[] exts = {"*.xml"};
					String path = FileUtil.getFilePathFromUser(targetPart,exts,"Select Trifolia template export file",false);
					if (path.equals("")) return;
					Element xmlRoot = XMLUtil.getRootElement(path);
					if (xmlRoot == null) throw new MapperException("Could not open Trifolia XML");
					String topTagName = xmlRoot.getLocalName();
					if (!(topTagName.equals("TemplateExport"))) throw new MapperException("Trifolia root element must be 'TemplateExport'");
					
					// (3) read the templates
					templates = new Vector<TrifoliaTemplate>() ;
					templatesById = new Hashtable<String,TrifoliaTemplate>();
					Vector<Element> templateEls = XMLUtil.namedChildElements(xmlRoot, "Template");
					for (int i = 0; i < templateEls.size(); i++)
					{
						TrifoliaTemplate template = new TrifoliaTemplate(templateEls.get(i));
						templatesById.put(template.oid(), template);
						templates.add(template);
					}
					
					// (4) pre-index the Ecore class model
					indexClassModel();
					
					// (5) index the templates by where they occur
					indexTemplates();
					
					// (6) Create the extra classes and associations
					createTemplateClasses();
					
					// (7) Prune the sections
					pruneSections();
					
					// (8) Save the modified Ecore model
					String sourceModelLocation = classModel.eResource().getURI().toString();
					String templatedModelLocation = ModelUtil.addSuffixToFileName(sourceModelLocation,"trifolia");
					ModelUtil.savePackage(templatedModelLocation, classModel);
					
					// (9) save the csv file of constraints
					int len = templatedModelLocation.length(); // to strip off 'ecore' and add 'csv'
					String csvFileLocation = templatedModelLocation.substring(0,len - 5) + "csv";
					if (csvFileLocation.startsWith("file:")) csvFileLocation = csvFileLocation.substring(6);
					FileUtil.writeCSVFile(csvFileLocation, csvOfConstraints);
				}
			}
		}
		catch (Exception ex) 
		{
			showMessage("Error","Failed to capture Trifolia templates: " + ex.getMessage());
			ex.printStackTrace();
		}				

	}
	
	
	/**
	 * 
	 * @throws MapperException
	 */
	private void createTemplateClasses() throws MapperException
	{
		// prepare to make the csv file of constraints
		csvOfConstraints = new Vector<String[]>();
		csvOfConstraints.add(csvHeader);
		
		// first pass - pick up templates which are further constraints on C-CDA templates, at document and section level only
		for (Iterator<TrifoliaTemplate> it = templates.iterator();it.hasNext();)
		{
			TrifoliaTemplate template = it.next();
			// do nothing for (C-CDA) templates which are already in the templated model
			if (classesByTemplate.get(template.oid()) == null)
			{
				if (template.templateType().equals("document")) makeDocumentBCTPSClass(template);

				// pick up only templates with a non-empty implied template oid, section level
				String impliedOID = template.impliedTemplateOid();
				if ((!(impliedOID.equals(""))) && (template.templateType().equals("section")))
				{
					template.setHandled(true);
					// find the classes using the C-CDA template, to be further constrained
					Vector<EClass> classesToConstrain = classesByTemplate.get(impliedOID);
					if (classesToConstrain == null)
						throw new MapperException("Cannot find C-CDA template " + impliedOID + " for " + template.title() + " to constrain.");
					for (Iterator<EClass> iu = classesToConstrain.iterator();iu.hasNext();)
					{
						// this class will be an act clone
						EClass classToConstrain = iu.next();
						
						// make a template clone and add it to the same package
						EClass templateClone = makeTemplateClone(classToConstrain,template);
						
						// make the nested template classes for section templates
						makeNestedTemplateClasses(templateClone,template);

						// find the association to it from an ActRelationship clone, unless it is the root
						Vector<EReference> refs = associationsByTargetClass.get(classToConstrain);
						{
							if (refs.size() > 1) message(refs.size() + " associations to C-CDA class for template " + template.title());
							for (int r = 0; r < refs.size(); r++)
							{
								EReference refToAct = refs.get(r);

								// find the ActRelationship parent
								EClass pinkParent = (EClass)refToAct.eContainer();
								
								// find all associations to the parent (there may be several, from different templated classes)
								Vector<EReference> parentRefs = associationsByTargetClass.get(pinkParent);
								if (parentRefs == null) throw new MapperException("No associations to parent C-CDA class for template " + template.title());
								
								for (int p = 0; p < parentRefs.size(); p++)
								{
									EReference refToParent = parentRefs.get(p);
									
									// find the grandparent EClass
									EClass grandParent = (EClass)refToParent.eContainer();
									// make the new association to an ActRelationship, a new ActRelationship, and a new association to the templated class
									makeCloneChain(grandParent,refToParent,pinkParent,refToAct,templateClone,template);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param template
	 * @throws MapperException
	 */
	private void makeDocumentBCTPSClass(TrifoliaTemplate template) throws MapperException
	{
		// the package in which the entry package must be
		EPackage headerPackage = ModelUtil.getNamedPackage(classModel, "cdaHeader");
		if (headerPackage== null) throw new MapperException("No CDA header package");

		// General Header constraints is the entry class
		EClass genHeader = (EClass)headerPackage.getEClassifier("GeneralHeaderConstraints");
		if (genHeader== null) throw new MapperException("No general header constraints class");
		if (!(ModelUtil.getMIFAnnotation(genHeader, "template").equals(template.impliedTemplateOid())))
			throw new MapperException("Unexpected template id for general header constraints");
		
		// make a template clone and add it to the same package
		EClass templateClone = makeTemplateClone(genHeader,template);
		
		// re-define the one entry class
		ModelUtil.removeMIFAnnotation(genHeader, "entry");
		ModelUtil.addMIFAnnotation(templateClone, "entry", "true");

		// there are templates beneath the 'component' node, so change its name
		EStructuralFeature feat = templateClone.getEStructuralFeature("component");
		if (feat != null)
		{
			feat.setName("component_T");
			ModelUtil.addMIFAnnotation(feat, "CDA_Name", "component");
		}
	}


	
	/**
	 * recursive descent of BCTPS templates, making template classes and chains of associations 
	 * and intermediate classes to them
	 * @param BCTPSClass
	 * @param outerTemplate
	 * @throws MapperException
	 */
	private void makeNestedTemplateClasses(EClass BCTPSClass, TrifoliaTemplate outerTemplate) throws MapperException
	{
		for (Enumeration<String> en = outerTemplate.nestedTemplates().keys();en.hasMoreElements();)
		{
			String nestedTemplateId = en.nextElement();
			TrifoliaTemplate template = templatesById.get(nestedTemplateId);
			if (isBCTPSTemplate(nestedTemplateId))
			{
				String path = outerTemplate.nestedTemplates().get(nestedTemplateId);
				// follow the path of the template constraint, storing results in arrays
				EClass[] classes = new EClass[10];
				EReference[] refs = new EReference[10];
				classes[0] = BCTPSClass;
				StringTokenizer steps = new StringTokenizer(path, "/");
				if (steps.countTokens() < 2) throw new MapperException("Path " + path + " is too short");
				int s = 0;
				boolean following = true;
				while ((steps.hasMoreTokens()) && (following))
				{
					String step = steps.nextToken();
					EStructuralFeature feat = classes[s].getEStructuralFeature(step);
					if (feat == null) 
					{
						following = false;
						message("**** Cannot follow step '" + step + "' of path " + path + " from class " + classes[s].getName());
					}
					else
					{
						refs[s] = (EReference)feat;
						classes[s+1] = (EClass)(refs[s].getEType());
						s++;
					}
				}
				
				// if the whole path was followed successfully, make a clone class and a path to it
				if (following)
				{

					// make a templated clone of the last class in the chain, or a templated class
					EClass classToCopy = classes[s];
					Vector<EClass> templatedClassesToCopy = classesByTemplate.get(template.impliedTemplateOid());
					// FIXME; should not pick just the first; pick the one with only one templateId
					if (templatedClassesToCopy != null) classToCopy = templatedClassesToCopy.get(0);
					EClass templateClone = makeTemplateClone(classToCopy,template);
					message("Made template class for " + template.title() + " of type " + template.templateType());

					if (s > 2) 
					{
						message("Long path " + path + " for template " + template.title());
						makeLongCloneChain(classes,refs, s, templateClone,template);
					}
					// make clone classes and associations for the end of the path
					else makeCloneChain(classes[s-2],refs[s-2],classes[s-1],refs[s-1],templateClone,template);
					
					// recursive step
					makeNestedTemplateClasses(templateClone,template);
				}
			}
		}
	}
	
	private boolean isBCTPSTemplate(String templateId)
	{
		return (templateId.startsWith("2.16.840.1.113883.10.20.30"));
	}

	
	/**
	 * @param classToConstrain
	 * @param template
	 * @return template-constrained class
	 */
	private EClass makeTemplateClone(EClass classToConstrain,TrifoliaTemplate template) throws MapperException
	{
		// the name of the new class depends only on the template, and begins with 'S_' or 'E_'
		String newName = template.bookmark();
		// clone the class, associations and attributes in the same package as its source
		EClass tClone = ModelUtil.cloneEClass(classToConstrain, newName);
		// record that this class has this template id (should also record that it has other template ids - but that does not matter)
		addClassByTemplate(template.oid(),tClone);
		
		// add the new templateId, with the lowest possible 'template_x' key
		boolean added = false;
		for (int i = 0; i < 8; i++) if (!added)
		{
			String  mifKey = "template_" + i;
			if (i==0) mifKey = "template";
			if (ModelUtil.getMIFAnnotation(tClone, mifKey) == null)
			{
				added = true;
				ModelUtil.addMIFAnnotation(tClone, mifKey, template.oid());
			}
		}
		
		// reflect the template fixed value constraints
		Hashtable<String,String> fixedValues = template.allFixedValueConstraints();
		for (Enumeration<String> en = fixedValues.keys(); en.hasMoreElements();)
		{
			String path = en.nextElement();
			// if any path is not valid, just write a message; do not repeat template id constraints
			if ((checkPath(classToConstrain,path)) && (!(path.endsWith("templateId/@root"))))
				ModelUtil.addMIFAnnotation(tClone, "constraint:" + path, fixedValues.get(path));
		}
		
		// reflect the cardinality constraints of the top-level template constraints, in the cloned class
		for (Iterator<TrifoliaConstraint> iu = template.getConstraints().iterator();iu.hasNext();)
		{
			TrifoliaConstraint constraint = iu.next();
			// add to the csv file for this constraint and all nested constraints
			constraint.addToCSVFile(tClone.getName(), template.oid(),csvOfConstraints);
			String featName = constraint.context();
			if (featName.startsWith("@")) featName = featName.substring(1);
			EStructuralFeature feat = tClone.getEStructuralFeature(featName);
			if (feat != null)
			{
				if (constraint.minCardinality() == 1)
				{
					feat.setLowerBound(1);
					// message(tClone.getName() + "." + featName + " lower 1");
				}
				if ((constraint.maxCardinality() == 1) && (feat instanceof EReference))
				{
					((EReference)feat).setUpperBound(1);
					// message(tClone.getName() + "." + featName + " upper 1");
				}
			}
			else if (feat == null)
				message("Cannot find feature '" + featName + "' of class " + tClone.getName());
		}
		
		return tClone;
	}
	
	/**
	 * check that a path is valid from a class
	 * @param theClass
	 * @param path
	 * @return false, and write a message, if the path is not valid
	 */
	private boolean checkPath(EClass theClass,String path)
	{
		boolean result = true;
		StringTokenizer steps = new StringTokenizer(path,"/");
		EClass current = theClass;
		while ((steps.hasMoreTokens()) && (result))
		{
			String step = steps.nextToken();
			String featName = step;
			if (step.startsWith("@")) featName = featName.substring(1);
			EStructuralFeature  feat = current.getEStructuralFeature(featName);
			if (feat == null) 
			{
				result = false;
				message("*** Cannot follow path " + path + " from class " + theClass.getName() + " at step " + step);
			}
			if ((!step.startsWith("@")) && (result)) current = (EClass)(((EReference)feat).getEType());
		}
		return result;
	}


	
	/**
	 * 
	 * @param grandParent
	 * @param refToParent
	 * @param pinkParent
	 * @param refToAct
	 * @param classToConstrain
	 * @param template
	 */
	private void makeCloneChain(EClass grandParent, 
			EReference refToParent,
			EClass pinkParent,
			EReference  refToAct,
			EClass templateClone,
			TrifoliaTemplate template)
	{
		// make the intermediate ActRelationship class
		String pinkName = pinkClassName(pinkParent,template);
		EClass clonedPinkParent = ModelUtil.cloneEClass(pinkParent, pinkName);
		
		// add the association from the grandparent
		String pinkAssocName = GenUtil.initialLowerCase(pinkName);
		EReference clonedRefToParent = ModelUtil.cloneEReference(refToParent);
		clonedRefToParent.setName(pinkAssocName);
		clonedRefToParent.setEType(clonedPinkParent);
		// if the original ref to the parent had no CDA name change, the cloned ERef needs one
		if (ModelUtil.getMIFAnnotation(refToParent, "CDA_Name") == null)
			ModelUtil.addMIFAnnotation(clonedRefToParent, "CDA_Name", refToParent.getName());
		grandParent.getEStructuralFeatures().add(clonedRefToParent);
		
		// alter the association from the ActRelationship to the Act, to have the required name and target class
		String previousName = refToAct.getName();
		EReference refToAlter = (EReference)clonedPinkParent.getEStructuralFeature(previousName);
		refToAlter.setName(GenUtil.initialLowerCase(templateClone.getName()));
		// if the original ref to the act had no CDA name change, the cloned ERef needs one
		if (ModelUtil.getMIFAnnotation(refToAct, "CDA_Name") == null)
			ModelUtil.addMIFAnnotation(refToAlter, "CDA_Name", previousName);
		// alter the target class
		refToAlter.setEType(templateClone);
	}
	
	/**
	 * for a chain of length len (typically 2 or 4) the classes are
	 * classes[0] classes[1] templateClone, or 
	 * classes[0] classes[1] classes[2] classes[3] templateClone,
	 * with 2 or 4 refs, from each class to the next.
	 * Make copies of all classes except [0] and templateClone, and copies of all refs
	 *  
	 * @param classes
	 * @param refs
	 * @param len
	 * @param templateClone
	 * @param template
	 */
	private void makeLongCloneChain(EClass[] classes, EReference[] refs, int len,
			EClass templateClone,
			TrifoliaTemplate template)
	{
		EClass[] newClass = new EClass[10];
		// make the chain of classes
		for (int d = 0; d < len; d++)
		{
			String newClassName = makeNewClassName(d,len,classes[d],template);
			if (d == 0) newClass[d] = classes[d];
			else newClass[d] = ModelUtil.cloneEClass(classes[d], newClassName);
		}
		
		// make the chain of associations
		for (int d = 0; d < len; d++)
		{
			EClass source = newClass[d];
			EClass target = templateClone;
			if (d < len - 1) target = newClass[d+1];
			
			EReference newRef = ModelUtil.cloneEReference(refs[d]);
			String refName = GenUtil.initialLowerCase(target.getName());
			newRef.setName(refName);
			newRef.setEType(target);
			// if the original ref had no CDA name change, the cloned ERef needs one
			if (ModelUtil.getMIFAnnotation(refs[d], "CDA_Name") == null)
				ModelUtil.addMIFAnnotation(newRef, "CDA_Name", refs[d].getName());
			source.getEStructuralFeatures().add(newRef);
		}

	}
	
	// index to make sure cloned classes have different names
	private int tp_index = 1;
	
	private String makeNewClassName(int d, int len, EClass oldClass, TrifoliaTemplate template)
	{
		String newClassName = oldClass.getName();
		if (d > 0)
		{
			// for len = 4 this covers d = 1 (ActRelationship) and 2 (Act)
			if (d < len - 1) 
			{
				newClassName = newClassName + "_TP" + tp_index;
				tp_index++;
			}
			// for len = 4 this covers d = 3 (ActRelationship)
			else newClassName = newClassName + "_" + template.bookmark();
		}
		
		return newClassName;
	}
	
	
	/**
	 * 
	 * @param pinkParent
	 * @param template
	 * @return a suitable name for an ActRelationship class
	 */
	private String pinkClassName(EClass pinkParent, TrifoliaTemplate template)
	{
		// strip off any 'S_' or 'E_' from template bookmark;
		String newName = template.bookmark();
		if ((newName.startsWith("S_"))||(newName.startsWith("E_"))) newName = newName.substring(2);

		String pinkName = pinkParent.getName();
		if (pinkName.startsWith("Component")) newName = "Component_" + newName;
		else if (pinkName.startsWith("EntryRelationship")) newName = "EntryRel_" + newName;
		else if (pinkName.startsWith("Entry")) newName = "Entry_" + newName;
		else newName = pinkName + newName;

		return newName;
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	//                                                    Pre-indexing
	//-------------------------------------------------------------------------------------------------------------------


	/**
	 * 
	 * @throws MapperException
	 */
	private void indexClassModel() throws MapperException
	{
		classesByTemplate = new Hashtable<String,Vector<EClass>>();
		associationsByTargetClass = new Hashtable<EClass,Vector<EReference>>();
		int templateUses = 0;
		int refCount = 0;
		int classCount = 0;
		
		String isRMIM = ModelUtil.getMIFAnnotation(classModel, "RMIM");
		if (!((isRMIM != null) && (isRMIM.equals("true")))) throw new MapperException("Class model is not an RMIM-style model");
		
		for (Iterator<EPackage> it = classModel.getESubpackages().iterator();it.hasNext();)
		{
			EPackage subPackage = it.next();
			for (Iterator<EClassifier> iu = subPackage.getEClassifiers().iterator();iu.hasNext();)
			{
				EClassifier next = iu.next();
				if (next instanceof EClass)
				{
					EClass theClass = (EClass)next;
					classCount++;
					
					// index classes by their template ids.
					// assume there can be no more than 6 templates on a class; record template annotations
					for (int i = 0; i < 7; i++)
					{
						// how templates are described in MIF annotations
						String templateKey = "template_" + i;
						if (i == 0) templateKey = "template";
						String templateId = ModelUtil.getMIFAnnotation(theClass, templateKey);
						if (templateId != null)
						{
							addClassByTemplate(templateId,theClass);
							templateUses++;
						}
					}
					
					// index associations by their target class
					for (Iterator<EStructuralFeature> iv = theClass.getEStructuralFeatures().iterator();iv.hasNext();)
					{
						EStructuralFeature feat = iv.next();
						if (feat instanceof EReference)
						{
							EReference ref = (EReference)feat;
							EClass target = (EClass)ref.getEType();
							if (target != null)
							{
								Vector<EReference> references = associationsByTargetClass.get(target);
								if (references == null) references = new Vector<EReference>();
								references.add(ref);
								associationsByTargetClass.put(target, references);
								refCount++;
							}
						}
					}
				}
			}			
		}
		
		message("Template uses: " + templateUses + "; ref count: " + refCount + "; class count: " + classCount);		
	}
	
	private void addClassByTemplate(String templateId, EClass theClass)
	{
		Vector<EClass> classes = classesByTemplate.get(templateId);
		if (classes == null) classes = new Vector<EClass>();
		classes.add(theClass);
		classesByTemplate.put(templateId, classes);
	}
	
	/**
	 *  index all template nesting constraints
	 */
	private void indexTemplates()
	{
		allContainingTemplates = new Hashtable<String,Vector<TrifoliaTemplate>>();
		int links = 0;		
		for (Iterator<TrifoliaTemplate> it = templates.iterator();it.hasNext();)
		{
			TrifoliaTemplate template = it.next();
			Hashtable<String,String> containedTemplatePaths = template.nestedTemplates();
			for (Enumeration<String> en = containedTemplatePaths.keys();en.hasMoreElements();)
			{
				String containedOid = en.nextElement();
				Vector<TrifoliaTemplate> outerTemplates = allContainingTemplates.get(containedOid);
				if (outerTemplates == null) outerTemplates = new Vector<TrifoliaTemplate>();
				outerTemplates.add(template);
				allContainingTemplates.put(containedOid, outerTemplates);
				links++;
			}
		}		
		message("Template nesting constraints: " + links);
	}
	
	/**
	 * retain only a few sections under StructuredBody
	 */
	private void pruneSections() throws MapperException
	{
		// find the 'StructuredBody' class in some package
		EClass sBody = null;
		for (Iterator<EPackage> it = classModel.getESubpackages().iterator();it.hasNext();)
		{
			EClassifier next = it.next().getEClassifier("StructuredBody");
			if ((next != null) && (next instanceof EClass)) sBody = (EClass)next;
		}
		if (sBody == null) throw new MapperException("Cannot find StructuredBody class");
		
		// make a List of names of all the features we want to retain
		Vector<String> wantedFeatures = new Vector<String>();
		Vector<EStructuralFeature> allFeatures = new Vector<EStructuralFeature>();
		for (Iterator<EStructuralFeature> it = sBody.getEStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature feat = it.next();
			allFeatures.add(feat);
			String featName = feat.getName();
			if (feat instanceof EAttribute) wantedFeatures.add(featName);
			else if (feat instanceof EReference)
			{
				EReference ref = (EReference)feat;
				EClass target = (EClass)ref.getEType();
				if (featName.contains("BCTPS")) wantedFeatures.add(featName);
				else if (featName.contains("AllergiesSectionEntriesOptional")) wantedFeatures.add(featName);
				else if (featName.contains("SocialHistorySection")) wantedFeatures.add(featName);
				else if (featName.equals("typeId")) wantedFeatures.add(featName);
				else if (target.getEPackage().getName().equals("datatypes")) wantedFeatures.add(featName);
			}
		}
		
		// remove all EReferences not marked for retention
		for (Iterator<EStructuralFeature> it = allFeatures.iterator();it.hasNext();)
		{
			EStructuralFeature feat = it.next();
			if (!GenUtil.inVector(feat.getName(), wantedFeatures)) sBody.getEStructuralFeatures().remove(feat);
		}
	}

	
	private void message(String s) {System.out.println(s);}

}
