package com.openMap1.mapper.health.actions;
/**
 * This action is used on a mapping set to some V3 RMIM.
 * Wherever the mapping set has a mapping to some RMIM class, 
 * and where the annotations on that class in the ECore model say
 * that some RIM structural attribute of the class has a fixed value, 
 * this command adds a fixed value property mapping on the same node as the class
 * mapping.
 * 
 * The effect is that an RMIM instance made from the mapped XML through the 
 * mappings will have the correct values of RIM structural attributes, for all RMIM 
 * class instances.
 * 
 * Conversely, when writing the XML, the fixed values are required on an RMIM instance for
 * the XML writer to make the mapped node. 
 * 
 * When the RMIM is a CDA RMIM that has been constrained by templates, then for every node
 * in the XML mapped to a templated class, the template constraints (annotations on the templated
 * class) lead to fixed mappings to descendant classes, associations, and fixed properties
 */

import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;

import com.openMap1.mapper.actions.AddMapperEditorActions;
import com.openMap1.mapper.actions.MakeAssociationMappingAction;
import com.openMap1.mapper.actions.MakeObjectMappingAction;
import com.openMap1.mapper.actions.MapperActionDelegate;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.views.WorkBenchUtil;

import com.openMap1.mapper.health.commands.MapStructuralAttributesCommand;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MappingCondition;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ValueCondition;


/**
 * This action is used on a mapping set to some V3 RMIM.
 * Wherever the mapping set has a mapping to some RMIM class, 
 * and where the annotations on that class in the ECore model say
 * that some RIM structural attribute of the class has a fixed value, 
 * this command adds a fixed value property mapping on the same node as the class
 * mapping.
 * 
 * The effect is that an RMIM instance made from the mapped XML through the 
 * mappings will have the correct values of RIM structural attributes, for all RMIM 
 * class instances.
 * 
 * Conversely, when writing the XML, the fixed values are required on an RMIM instance for
 * the XML writer to make the mapped node. 
 * 
 * When the RMIM is a CDA RMIM that has been constrained by templates, then for every node
 * in the XML mapped to a templated class, the template constraints (annotations on the templated
 * class) lead to fixed mappings to descendant classes, associations, and fixed properties
 */
public class MapRIMStructuralAttributesAction extends MapperActionDelegate implements IObjectActionDelegate{

	private EditingDomain domain;
	
	
	public void run(IAction action) {
		try
		{
			// (1) Check if the editor is opened; if not, open it
			MapperEditor me = OpenMapperEditor(selection);
			if (me == null) return;
			domain = me.editingDomain();	
			MappedStructure ms  = WorkBenchUtil.mappingRoot(me);
			
			addV3FixedValues(ms);
			
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			showMessage("Failed to map RIM Structural attributes or template constraints: " + ex.getMessage());
		}
	}
	
	public void addV3FixedValues(MappedStructure ms) throws MapperException
	{
		// (2) check that the mappings are to an RMIM class model
		EPackage classModel = ms.getClassModelRoot();
		String RMIMtype = ModelUtil.getEAnnotationDetail(classModel, "RMIM");
		boolean isRMIM = ((RMIMtype != null) && (RMIMtype.equals("true")));
		if (!isRMIM)
		{
			showMessage("The mapped class model is not an RMIM class model.");
			return;
		}
		
		// (3) Make and execute one composite command to add the RIM Structural Attributes
		MapStructuralAttributesCommand msac = new MapStructuralAttributesCommand(domain,ms);
		domain.getCommandStack().execute(msac);	
		
		/* (4) if the RMIM is templated, add the fixed mappings required by the templates,
		 * in a series of separate commands */
		String templated = ModelUtil.getEAnnotationDetail(classModel, "templated");
		boolean isTemplated = ((templated != null) && (templated.equals("true")));
		if (isTemplated) mapTemplateConstraints(ms, classModel);
		
	}
	
	/**
	 * Find all nodes mapped to classes that have template annotations on them,
	 * and make mappings to enforce the template constraints in an RMIM got 
	 * by reading the mapped XML
	 * @param ms the mapped structure to which the fixed mappings will be added
	 * @param classModel the RMIM class model, with annotations for template constraints
	 * @throws MapperException
	 */
	private void mapTemplateConstraints(MappedStructure ms,  EPackage classModel)
	throws MapperException
	{
		// iterate over all object mappings in the mapping set
		for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(ms, MapperPackage.eINSTANCE.getObjMapping()).iterator();it.hasNext();)
		{
			ObjMapping om = (ObjMapping)it.next();
			
			// find the mapped class and check if it is templated
			EClass mappedClass = ModelUtil.getNamedClass(classModel, om.getQualifiedClassName());
			if (mappedClass != null) 
			{
				// iterate over all constraint annotations
				for (Iterator<EAnnotation> iz = mappedClass.getEAnnotations().iterator();iz.hasNext();)
				{
					EAnnotation ea = iz.next();
					EMap<String,String> ed =  ea.getDetails();
					for (Iterator<String> ik = ed.keySet().iterator(); ik.hasNext();)
					{
						String key = ik.next();
						if (key.startsWith("constraint"))
						{
							// find the suffix, such as "" or "_1" etc, of the constraint
							String suffix = getSuffix(key);
							// there should be a template on the class with the same suffix
							String templateId = ModelUtil.getEAnnotationDetail(mappedClass, "template" + suffix);
							if (templateId == null) 
								throw new MapperException("Class " + mappedClass.getName()
										+ " has no template defined as supplying its constraints 'constraint" + suffix + "'");
							String path = key.substring(("constraint" + suffix + ":").length());
							String attValue = ed.get(key);
							applyTemplateConstraint(ms, classModel, mappedClass, om, path, attValue, templateId);
						}
					}
				}				
			}
		}		
	}
	
	/**
	 * add all the fixed mappings necessary to make the RMIM instance
	 * obey one template constraint.
	 * @param ms the MappedStructure of all mappings (assumed no imports)
	 * @param mappedClass the mapped class with a template constraint
	 * @param om an object mapping for the class
	 * @param path the RMIM association from the mapped class to the constrained property
	 * @param attValue the value set by the constraint
	 * @param templateId id of thhe template supplying the constraint
	 */
	private void applyTemplateConstraint(MappedStructure ms, EPackage classModel, EClass mappedClass, 
			ObjMapping om, String path, String attValue, String templateId)
	throws MapperException
	{
		/* if the path has any association steps before its final attribute step, 
		 * traverse these associations to find the last class already mapped to any node in the subtree
		 * below the node mapped to the class, with the required association mappings; this will often be the class itself */
		ObjMapping lastMapping = om;
		boolean foundChildMapping = true;
		String shortPath = "";
		StringTokenizer steps = new StringTokenizer(path,"/");
		while (steps.hasMoreTokens())
		{
			String step = steps.nextToken();
			if (step.startsWith("@")) shortPath = shortPath + step;
			else 
			{
				if (foundChildMapping)
				{
					ObjMapping nextObjMapping = getDescendantObjMapping(lastMapping,step,classModel);
					if (nextObjMapping != null) lastMapping = nextObjMapping;
					else foundChildMapping = false;				
				}
				if (!foundChildMapping) shortPath = shortPath + step + "/";
			}
		} // end of loop over steps in the full path
		
		// make the mappings, starting at the last class in the trail that is already mapped
		makeRequiredMappings(ms,classModel,lastMapping,shortPath,attValue,templateId);
	}
	
	/**
	 * @param lastMapping an object mapping
	 * @param step an association (role name) from the mapped class
	 * @return any mapping to the class at the end of the association, in the subtree
	 * below the mapped node, which also has an association mapping from the first mapped 
	 * class with the correct subsets; or null if there is none
	 */
	private ObjMapping getDescendantObjMapping(ObjMapping lastMapping,String step, EPackage classModel)
	throws MapperException
	{
		ObjMapping result = null;
		ElementDef mappedElement = (ElementDef)lastMapping.eContainer().eContainer();
		EClass mappedClass = ModelUtil.getNamedClass(classModel, lastMapping.getQualifiedClassName());
		EReference ref = getAssociationWithPossiblyChangedName(mappedClass,step);
		if (ref == null) throw new MapperException("Found no association '" 
				+ step + "' from class " + mappedClass.getName());
		EClass target = (EClass)ref.getEType();
		String targetClassName = ModelUtil.getQualifiedClassName(target);

		// find all object mappings to the child class under the original mapped node
		for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(mappedElement, MapperPackage.eINSTANCE.getObjMapping()).iterator();it.hasNext();)
		{
			ObjMapping childMapping = (ObjMapping)it.next();
			String mappedClassName = childMapping.getQualifiedClassName();
			if (mappedClassName.equals(targetClassName))  
			{
				// check for an association mapping on the same node, with the required classes, subsets and association name
				NodeMappingSet nms = (NodeMappingSet)childMapping.eContainer();
				for (Iterator<AssocMapping> ia = nms.getAssociationMappings().iterator();ia.hasNext();)
				{
					AssocMapping am = ia.next();
					AssocEndMapping ae1 = am.getMappedEnd1();
					AssocEndMapping ae2 = am.getMappedEnd2();
					// end 1 is the outer end
					if     ((ae1.getQualifiedClassName().equals(lastMapping.getQualifiedClassName())) &&
							(ae1.getSubset().equals(lastMapping.getSubset())) &&
							(ae2.getQualifiedClassName().equals(childMapping.getQualifiedClassName())) &&
							(ae2.getSubset().equals(childMapping.getSubset())) &&
							(ae2.getMappedRole().equals(ref.getName())))
									result = childMapping;
				}
			}
		}
		return result;
	}
	

	/**
	 * @param mappedClass
	 * @param step  an association name - which is the original in CDA before name changes were made 
	 * @return
	 */
	private EReference getAssociationWithPossiblyChangedName(EClass mappedClass,String step)
	{
		// first try to follow the association with unchanged name
		EStructuralFeature feature = mappedClass.getEStructuralFeature(step);
		if (feature != null) return (EReference)feature;

		// if there is no association with this name, look for an association which had that name before it was changed
		else if (feature == null)
		{
			for (Iterator<EReference> ir = mappedClass.getEAllReferences().iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				String originalName = ModelUtil.getEAnnotationDetail(ref, "CDA_Name");
				if ((originalName != null) && (step.equals(originalName)))
					return ref;
			}
		}
		return null;
	}

	
	/**
	 * Make all the new mappings required for this template, on one node
	 * (the node containing the  last object mapping that already exists on the trail)
	 * @param ms the mapped structure
	 * @param classModel the CDA constrained RMIM
	 * @param lastMapping the last mapping on the trail from the templated class which already exists
	 * @param shortPath the path of associations and final name of the constrained attribute
	 * @param attValue the value the attribute is to have
	 * @param temmplateId id of the template that gave this constraint
	 */
	private void makeRequiredMappings(MappedStructure ms, EPackage classModel,
			ObjMapping lastMapping,String shortPath, String attValue, String templateId)
	throws MapperException
	{
		// description, to go on all added mappings
		String description = "from template " + templateId;
		// node to put all the new mappings on
		ElementDef mappedNode = (ElementDef)lastMapping.eContainer().eContainer();
		// current mapped class
		EClass mappedClass = ModelUtil.getNamedClass(classModel, lastMapping.getQualifiedClassName());
		// current object mapping
		ObjMapping currentObjMapping = lastMapping;
		// previous fixed value of property mapping
		String previousFixedValue = "";

		StringTokenizer steps = new StringTokenizer(shortPath,"/");
		while (steps.hasMoreTokens())
		{
			// subset of current object mapping 
			String parentSubset = currentObjMapping.getSubset();

			String step = steps.nextToken();
			// final step; add the property mapping
			if (step.startsWith("@"))
			{
				String attName = step.substring(1);
				EStructuralFeature feature = mappedClass.getEStructuralFeature(attName);
				if ((feature != null)  && (feature instanceof EAttribute))
				{
					// do not add a fixed value mapping if there is one already for this attribute
					boolean hasFixedValue = false;
					for (Iterator<FixedPropertyValue> iv = currentObjMapping.getFixedPropertyValues().iterator();iv.hasNext();)
					{
						FixedPropertyValue fpv = iv.next();
						if (fpv.getMappedProperty().equals(attName)) 
						{
							hasFixedValue = true;
							previousFixedValue = fpv.getFixedValue();
						}
					}
					
					// check that the template constraint is not trying ot change the fixed value 
					if (hasFixedValue)
					{
						if (!previousFixedValue.equals(attValue))
							throw new MapperException("Attempt to change the fixed value of property '"
									+ attName + "' of class " + mappedClass.getName() + " from '"
						            + previousFixedValue + "' to '" + attValue + "' by template " + templateId);
					}
					
					// add the fixed value property mapping
					else if (!hasFixedValue)
					{
						FixedPropertyValue fpv = MapperFactory.eINSTANCE.createFixedPropertyValue();
						fpv.setMappedProperty(attName);
						fpv.setFixedValue(attValue);
						fpv.setValueType("string");
						currentObjMapping.getFixedPropertyValues().add(fpv);
						System.out.println("Setting property " + attName + " of class " 
								+ mappedClass.getName() + " to '" + attValue + "'");
					}
				}
				else throw new MapperException("Cannot find attribute '" + attName 
						+ "' of class " + mappedClass.getName() + " constrained by template " + templateId);
			}
			
			// non-final association steps; add the object mapping and association mapping
			else if (!step.startsWith("@"))
			{
				EReference ref = getAssociationWithPossiblyChangedName(mappedClass,step);
				if (ref != null) 
				{
					// make the object mapping for the child class, and then find it
					EClass childClass = (EClass)ref.getEType();
					String childPackageName = childClass.getEPackage().getName();
					String childSubset = AddMapperEditorActions.nextSubset(ms, ModelUtil.getQualifiedClassName(childClass));
					new MakeObjectMappingAction(domain, mappedNode,childClass.getName(),
							childPackageName, childSubset,description).run();
					ObjMapping childObjMapping = getObjectMapping(mappedNode,childClass.getName(),
							childPackageName, childSubset);
					if (childObjMapping == null)
						throw new MapperException("Cannot find object mapping just made for class " + childClass.getName());
					
					// copy any value conditions from the current object mapping to the new object mapping
					copyValueConditions(currentObjMapping,childObjMapping);
					
					// make the association mapping to the child class, required for the child class
					boolean makeRequired = true;
					MappableAssociation mass = new MappableAssociation(mappedClass,parentSubset,
							childClass, childSubset,ref,makeRequired);
					new MakeAssociationMappingAction(domain,mappedNode,mass,description).run();	
					
					// be ready for the next step - association or property
					mappedClass = childClass;
					currentObjMapping = childObjMapping;
				}
				else throw new MapperException("Cannot find association '" + step 
						+ "' of class " + mappedClass.getName()+ " in constraint from template " + templateId);				
			}
		}
	}
	
	/**
	 * @param mappedNode
	 * @param className
	 * @param packageName
	 * @param subset
	 * @return an object mapping for the class, package and subset on the node, if it exists; 
	 * otherwise null
	 */
	private ObjMapping getObjectMapping(ElementDef mappedNode,String className,
			String packageName, String subset)
	{
		ObjMapping  om = null;
		for (Iterator<ObjMapping> im = mappedNode.getNodeMappingSet().getObjectMappings().iterator();im.hasNext();)
		{
			ObjMapping next = im.next();
			if     ((next.getMappedClass().equals(className)) &&
					(next.getMappedPackage().equals(packageName)) &&
					(next.getSubset().equals(subset)))
						om = next;
		}
		return om;
	}
	
	/**
	 * copy all value conditions from the current object mapping to the new object mapping
	 */
	private void copyValueConditions(ObjMapping currentObjMapping,ObjMapping childObjMapping)
	{
		for (Iterator<MappingCondition> im = currentObjMapping.getMappingConditions().iterator();im.hasNext();)
		{
			MappingCondition mc = im.next();
			if (mc instanceof ValueCondition)
			{
				ValueCondition vc = (ValueCondition)mc;
				ValueCondition vcNew = MapperFactory.eINSTANCE.createValueCondition();
				vcNew.setLeftPath(vc.getLeftPath());
				vcNew.setDescription(vc.getDescription());
				vcNew.setLeftFunction(vc.getLeftFunction());
				vcNew.setRightValue(vc.getRightValue());
				vcNew.setTest(vc.getTest());
				childObjMapping.getMappingConditions().add(vcNew);
			}
		}
	}

	
	/**
	 * @param key a key such as 'constraint_1:code/@code'
	 * @return the suffix after 'constraint' and before the ':'
	 */
	private String getSuffix(String key)
	{
		StringTokenizer st = new StringTokenizer(key,":");
		return (st.nextToken().substring("constraint".length()));
	}

}
