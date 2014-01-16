package com.openMap1.mapper.actions;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

/**
 * This action will do the following:
 *  
 * on a set of mappings and its simplified class model, using refinement annotations on the class model:
 * (1) to note which classes in the model have been 'promoted' to some ancestor class
 * which is linked to the promoted class by a 1..1 relationship.
 * (thus a promoted class might be represented on the same node of an XML as the ancestor class;
 * although the mappings of the promoted class are not actually moved)
 * (2) to add some associations to the promoted  class to the class model, if they have not been added
 * already
 * (3) Where directed, to change some association mappings from other classes to be association 
 * mappings to the newly added associations from the promoted classes
 * 
 * @author Robert
 *
 */
public class RefineMappingsAction  extends Action implements IAction{
	
	private MapperEditor mapperEditor;
	
    private ClassModelView classModelView;
    
	/** the package which is the root of the model */
	private EPackage ecoreRoot;
	
	// URI used for refinement annotations on the simplified ECore model
	public static String REFINEMENT_URI = "refineModel";
	
	// classes that are promoted to be represented on the same node as an ancestor class
	private Hashtable<String,String> promotedClasses;

	// classes for which a superclass has been defined
	private Hashtable<String,String> superClasses;

	// classes for which all associations are to be replicated on another class
	private Hashtable<String,String> allTransfers;	
	
	// associations which are to be transferred to a promoted class
	private Hashtable<String,Hashtable<String,String>> transfers;
	
	boolean tracing = true;

	
	public RefineMappingsAction()
	{
		super("Refine Model and Mappings");
	}

	public void run()
	{
		classModelView = WorkBenchUtil.getClassModelView(false);
		if (classModelView != null) try
		{
			ecoreRoot = classModelView.ecoreRoot();
			LabelledEClass rootClass = ClassModelView.getRootLabelledEClass(ecoreRoot);

			// check that the package structure is as expected for a simplified class model
			// FIXME - there is no longer a single main package so this won't work
			EPackage mainPackage = ecoreRoot;

			String mappingSetURIString = classModelView.mappingSetURI().toString();
			mapperEditor = WorkBenchUtil.getMapperEditor(mappingSetURIString);
			if (mapperEditor != null)
			{
				MappedStructure mappedStructure = (MappedStructure)mapperEditor.getEditingDomain().getResourceSet().getResources().get(0).getContents().get(0);
				refineModelAndMappings(mainPackage,rootClass,mappedStructure);
			}
		}
		catch (Exception ex) 
		{
			WorkBenchUtil.showMessage("Error",ex.getMessage());
			ex.printStackTrace();
		}					
	}

	private void refineModelAndMappings(EPackage mainPackage,LabelledEClass rootClass, MappedStructure mappedStructure) throws MapperException
	{
		// read and check the annotations
		readAnnotations(mainPackage, rootClass);
		trace("Annotation checks succeeded");
		
		// add the transferred associations to the class model, if they have not been added previously
		addTransferredAssociations(mainPackage);
		trace("Transferred associations added");
		
		// do not move the mappings for promoted classes - it is not necessary
		// movePromotedMappings(mappedStructure, rootClass);
		// trace("Mappings promoted");
		
		// transfer association mappings so they involve associations to the promoted classes
		transferAssociationMappings(mappedStructure, rootClass);
		trace("Association mappings transferred");
		
		// save the changes
		FileUtil.saveResource(ecoreRoot.eResource());							
		FileUtil.saveResource(mappedStructure.eResource());	
		trace("Changes saved");
	}
	
	/**
	 * For any associations that are to be transferred to a promoted class, add the associations
	 * to the promoted class in the class model, if they have not been added already
	 * @param mainPackage
	 * @throws MapperException
	 */
	private void addTransferredAssociations(EPackage mainPackage) throws MapperException
	{
		// classes for which all association mappings are to be transferred
		for (Enumeration<String> en = allTransfers.keys(); en.hasMoreElements();)
		{
			String fromClassName = en.nextElement();
			EClass fromClass = (EClass)mainPackage.getEClassifier(fromClassName);			
			String toClassName = allTransfers.get(fromClassName);
			EClass toClass = (EClass)mainPackage.getEClassifier(toClassName);
			
			for (Iterator<EStructuralFeature> it = fromClass.getEStructuralFeatures().iterator(); it.hasNext();)
			{
				EStructuralFeature next = it.next();
				if (next instanceof EReference) addOrCheckTransferredAssociation(fromClass,toClass,next.getName());
			}
		}

		// classes for which selected association mappings are to be transferred
		for (Enumeration<String> en = transfers.keys(); en.hasMoreElements();)
		{
			String fromClassName = en.nextElement();
			EClass fromClass = (EClass)mainPackage.getEClassifier(fromClassName);

			Hashtable<String,String> classTransfers = transfers.get(fromClassName);
			for (Enumeration<String> ea = classTransfers.keys(); ea.hasMoreElements();)
			{
				String assocName = ea.nextElement();
				String toClassName = classTransfers.get(assocName);
				EClass toClass = (EClass)mainPackage.getEClassifier(toClassName);
				addOrCheckTransferredAssociation(fromClass,toClass,assocName);
			}
		}		
	}
	
	/**
	 * add an association involving a promoted class to the class model;
	 * or if it already there, check it is correct
	 * @param fromClass
	 * @param toClass
	 * @param assocName
	 * @throws MapperException
	 */
	private void addOrCheckTransferredAssociation(EClass fromClass,EClass toClass,String assocName) 
	throws MapperException
	{
		EReference sourceRef = (EReference)fromClass.getEStructuralFeature(assocName);
		EReference newRef = (EReference)toClass.getEStructuralFeature(assocName);
		// if the transfer class already has the transferred association, check it
		if (newRef != null)
		{
			String preface = "Association '" + assocName + "' already exists on class '" 
			+ toClass.getName() + "' with wrong ";
			if (!sourceRef.getEType().equals(newRef.getEType()))
				throw new MapperException(preface + " target class '" + newRef.getEType().getName() + "'");
			if (sourceRef.getLowerBound() != newRef.getLowerBound())
				throw new MapperException(preface + " min multiplicity " + newRef.getLowerBound());
			if (sourceRef.getUpperBound() != newRef.getUpperBound())
				throw new MapperException(preface + " min multiplicity " + newRef.getUpperBound());
			if (sourceRef.isContainment() != newRef.isContainment())
				throw new MapperException(preface + " containment " + newRef.isContainment());
		}
		// if the transfer class does not have the transferred association, add it
		else if (newRef == null)
		{
			newRef = EcoreFactory.eINSTANCE.createEReference();
			newRef.setName(assocName);
			newRef.setEType(sourceRef.getEType());
			newRef.setLowerBound(sourceRef.getLowerBound());					
			newRef.setUpperBound(sourceRef.getUpperBound());
			newRef.setContainment(sourceRef.isContainment());
			toClass.getEStructuralFeatures().add(newRef);
		}		
	}

	/**
	 * For any class which has been promoted, so its object mapping should be on the same node
	 * as the object mapping for an ancestor class, move the object mapping and the association 
	 * mapping to the node with the mapping of the promotion target class.
	 * There has to be a direct association from the promotion target class to the promoted class - 
	 * so that there are no intermediate classes and associations.
	 * 
	 * This method is currently not used, as it is not necessary to move up the mappings -
	 * XML reading and writing works OK without moving them.
	 * 
	 * @param mappedStructure
	 * @param rootClass
	 * @throws MapperException
	 */
	@SuppressWarnings("unused")
	private void movePromotedMappings(MappedStructure mappedStructure,LabelledEClass rootClass) throws MapperException
	{
		for (Enumeration<String> en = promotedClasses.keys(); en.hasMoreElements();)
		{
			String promotedClassName = en.nextElement();
			LabelledEClass promotedClass = rootClass.getDescendant(promotedClassName);
			String qualifiedPromotedName = ModelUtil.getQualifiedClassName(promotedClass.eClass());
			ClassSet promotedCSet = new ClassSet(qualifiedPromotedName,"");
			ObjMapping promotedObjectMapping = ModelUtil.getObjectMapping(mappedStructure, promotedCSet);
			if (promotedObjectMapping == null) 
				throw new MapperException("Cannot find object mapping for promoted class " + promotedCSet.stringForm());

			String promotionTargetName = promotedClasses.get(promotedClassName);
			LabelledEClass targetClass = rootClass.getDescendant(promotionTargetName);
			String qualifiedTargetName = ModelUtil.getQualifiedClassName(targetClass.eClass());
			ClassSet targetCSet = new ClassSet(qualifiedTargetName,"");
			ObjMapping targetMapping = ModelUtil.getObjectMapping(mappedStructure, targetCSet);
			if (targetMapping == null) 
				throw new MapperException("Cannot find object mapping for promotion target class " + targetCSet.stringForm());

			AssocMapping promotedAssociationMapping = getPromotedAssociationMapping(promotedObjectMapping,targetCSet);
			NodeMappingSet fromSet = (NodeMappingSet)promotedObjectMapping.eContainer();
			NodeMappingSet toSet = (NodeMappingSet)targetMapping.eContainer();
			
			fromSet.getObjectMappings().remove(promotedObjectMapping);
			toSet.getObjectMappings().add(promotedObjectMapping);
			fromSet.getAssociationMappings().remove(promotedAssociationMapping);
			toSet.getAssociationMappings().add(promotedAssociationMapping);
			
			if (fromSet.countAllMappings() == 0)
			{
				ElementDef fromNode = (ElementDef)fromSet.eContainer();
				fromNode.removeNodeMappingSet();
			}
		}
		
	}

	/**
	 * @param promotedObjectMapping
	 * @param targetCSet
	 * @return a direct association mapping from the promotion target class to the promoted class,
	 * on the same node as the object mapping of the promoted class
	 * @throws MapperException if there is no such association
	 */
	AssocMapping getPromotedAssociationMapping(ObjMapping promotedObjectMapping,ClassSet targetCSet) throws MapperException
	{
		AssocMapping assocMapping = null;
		ClassSet promotedCSet = promotedObjectMapping.getClassSet();
		NodeMappingSet nms = (NodeMappingSet)promotedObjectMapping.eContainer();
		for (Iterator<AssocMapping> it = nms.getAssociationMappings().iterator(); it.hasNext();)
		{
			AssocMapping next = it.next();
			if ((next.getMappedEnd1().getClassSet().equals(targetCSet)) 
					&& (next.getMappedEnd2().getClassSet().equals(promotedCSet)))
				assocMapping = next;
		}
		if (assocMapping == null) throw new MapperException("Cannot find direct association mapping from class " + targetCSet.stringForm() 
				+ " to class " + promotedCSet.stringForm());
		return assocMapping;
	}

	/**
	 * transfer association mappings, so that they involve a promoted class, rather than 
	 * their original outer class. The inner class and the role name are unchanged.
	 * @param mappedStructure
	 * @param rootClass
	 * @throws MapperException
	 */
	private void transferAssociationMappings(MappedStructure mappedStructure,LabelledEClass rootClass) throws MapperException
	{
		// classes for which all association mappings are to be transferred to a promoted class
		for (Enumeration<String> en = allTransfers.keys(); en.hasMoreElements();)
		{
			String fromClassName = en.nextElement();
			LabelledEClass fromClass = rootClass.getDescendant(fromClassName);
			ClassSet fromCSet = new ClassSet(ModelUtil.getQualifiedClassName(fromClass.eClass()),"");

			String toClassName = allTransfers.get(fromClassName);
			// LabelledEClass toClass = rootClass.getDescendant(toClassName);
			// ClassSet toCSet = new ClassSet(ModelUtil.getQualifiedClassName(toClass.eClass()),"");

			for (Iterator<EObject> it = mappedStructure.eAllContents(); it.hasNext();)
			{
				EObject next = it.next();
				if (next instanceof AssocMapping)
				{
					AssocMapping aMap = (AssocMapping)next;
					AssocEndMapping aeMap = aMap.getMappedEnd1();
					if (aeMap.getClassSet().equals(fromCSet)) aeMap.setMappedClass(toClassName);
				}
			}
		}
		
		// classes for which selected  association mappings are to be transferred to a promoted class
		for (Enumeration<String> en = transfers.keys(); en.hasMoreElements();)
		{
			String fromClassName = en.nextElement();
			LabelledEClass fromClass = rootClass.getDescendant(fromClassName);
			ClassSet fromCSet = new ClassSet(ModelUtil.getQualifiedClassName(fromClass.eClass()),"");
			
			Hashtable<String,String> classTransfers = transfers.get(fromClassName);
			for (Enumeration<String> ep = classTransfers.keys(); ep.hasMoreElements();)
			{
				String assocName = ep.nextElement();
				String toClassName = classTransfers.get(assocName);
				LabelledEClass toClass = rootClass.getDescendant(toClassName);
				ClassSet toCSet = new ClassSet(ModelUtil.getQualifiedClassName(toClass.eClass()),"");

				for (Iterator<EObject> it = mappedStructure.eAllContents(); it.hasNext();)
				{
					EObject next = it.next();
					if (next instanceof AssocMapping)
					{
						AssocMapping aMap = (AssocMapping)next;
						AssocEndMapping aeMap = aMap.getMappedEnd1();
						if ((aeMap.getClassSet().equals(fromCSet)) && (aMap.getMappedEnd2().getMappedRole().equals(assocName)))
							aeMap.setMappedClass(toCSet.className());
					}
				}
			}
		}

	}


	/**
	 * read and check the refinement annotations on a simplified class model
	 * @param mainPackage
	 * @param rootClass
	 * @throws MapperException
	 */
	private void readAnnotations(EPackage mainPackage, LabelledEClass rootClass) throws MapperException
	{
		promotedClasses = new Hashtable<String,String>();
		superClasses = new Hashtable<String,String>();
		allTransfers = new Hashtable<String,String>();
		transfers = new Hashtable<String,Hashtable<String,String>>();

		// find any promoted classes and superclasses in the model; there must be at least one promoted class
		for (Iterator<EClassifier> it = mainPackage.getEClassifiers().iterator(); it.hasNext();)
		{
			EClassifier next = it.next();
			String className = next.getName();
			if (next instanceof EClass)
			{
				// read annotations for promotion, superclass and transfer of all associations on each class
				EAnnotation note = next.getEAnnotation(REFINEMENT_URI);
				if (note != null)
				{
					String promoteToClass = note.getDetails().get("promote");
					if (promoteToClass != null) promotedClasses.put(className, promoteToClass);

					String superClass = note.getDetails().get("superclass");
					if (superClass != null) superClasses.put(className, superClass);
					
					String allTransferClass = note.getDetails().get("transferAll");
					if (allTransferClass != null) allTransfers.put(className, allTransferClass);
				}
				
				// read specific association transfer annotations on EReferences
				for (Iterator<EStructuralFeature> ir = ((EClass)next).getEStructuralFeatures().iterator(); ir.hasNext();)
				{
					EStructuralFeature feat  = ir.next();
					String refName = feat.getName();
					if (feat instanceof EReference)
					{
						EAnnotation rNote = feat.getEAnnotation(REFINEMENT_URI);
						if (rNote != null)
						{
							String transferToClass = rNote.getDetails().get("transfer");
							if (transferToClass != null)
							{
								Hashtable<String,String> classTransfers = transfers.get(className);
								if (classTransfers == null) classTransfers = new Hashtable<String,String>();
								classTransfers.put(refName, transferToClass);
								transfers.put(className,classTransfers);
								trace("Transfer " + className + "." + refName + " to " + transferToClass);
							}
						}						
					}
				}
			}
		}
		
		// check that there are some promotions
		if (promotedClasses.size() == 0) throw new MapperException("No classes in the mapper class model have been promoted, so it cannot be refined.");
		
		// check that all promotions are to an ancestor class (the promotion target class) connected by 1..1 associations
		for (Enumeration<String> en = promotedClasses.keys(); en.hasMoreElements();)
		{
			String promotedClassName = en.nextElement();
			String promotionTarget = promotedClasses.get(promotedClassName);
			LabelledEClass promoted = rootClass.getDescendant(promotedClassName);
			if (promoted == null) throw new MapperException("Promoted class '" + promotedClassName + "' not found in class tree");
			if (promoted.countOccurrences(promotionTarget) == 0) 
				throw new MapperException("Class '" + promotionTarget + "' is not an ancestor of promoted class '" + promotedClassName + "'");
			if (!promoted.isOneToOneDescendant(promotionTarget))
				throw new MapperException("Path from class '" + promotionTarget + "' to '" + promotedClassName + "' is not all 1..1 associations");
		}
		
		// check that all association transfers are to a promoted class, from a class under its promotion target connected by 1..1 associations
		for (Enumeration<String> en = allTransfers.keys(); en.hasMoreElements();)
		{
			String fromClass = en.nextElement();
			LabelledEClass source = rootClass.getDescendant(fromClass);
			if (source == null) throw new MapperException("Source class for association transfer '" + fromClass + "' is not found in class tree");
			
			String toClass = allTransfers.get(fromClass);
			checkTransferClasses(source, toClass, "any association");			
		}
		
		// check that all individual association transfers are to a promoted class, from a class under its promotion target connected by 1..1 associations
		for (Enumeration<String> en = transfers.keys(); en.hasMoreElements();)
		{
			String fromClass = en.nextElement();
			LabelledEClass source = rootClass.getDescendant(fromClass);
			if (source == null) throw new MapperException("Source class for association transfer '" + fromClass + "' is not found in class tree");

			Hashtable<String,String> classTransfers = transfers.get(fromClass);
			for (Enumeration<String> ea = classTransfers.keys(); ea.hasMoreElements();)
			{
				String assocName = ea.nextElement();
				String toClass = classTransfers.get(assocName);
				checkTransferClasses(source, toClass, assocName);
			}
		}
	}
	
	/**
	 * check that a class which some association mappings are being transferred from, to a promoted class,
	 * is appropriate for the transfer - that both classes are 1..1 descendants of the promotion target class
	 * @param source
	 * @param toClass
	 * @param assocName
	 * @throws MapperException
	 */
	private void checkTransferClasses(LabelledEClass source, String toClass, String assocName) throws MapperException
	{
		String fromClass = source.eClass().getName();
		String promotionTarget = promotedClasses.get(toClass);
		if (promotionTarget == null) throw new MapperException("Association " + assocName + " from class "  + fromClass +
				" cannot be transferred to class " + toClass + ", because that class has not been promoted.");
		if (source.countOccurrences(promotionTarget) == 0) 
			throw new MapperException("Source class for association transfer '" + 
					fromClass + "' is not under the promotion target class  '" + promotionTarget + "'");
		if (!source.isOneToOneDescendant(promotionTarget))
			throw new MapperException("Path from class '" + promotionTarget 
					+ "' to source class for association transfer '" + source + "' is not all 1..1 associations");		
	}
	
	private void trace(String s) {if (tracing) System.out.println(s);}



}
