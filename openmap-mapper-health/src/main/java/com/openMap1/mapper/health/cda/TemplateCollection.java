package com.openMap1.mapper.health.cda;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.converters.CDAConverter;
import com.openMap1.mapper.health.v3.ConcreteClass;
import com.openMap1.mapper.health.v3.RMIMReader;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * represents the full collection of templates defined in the template usage file
 * @author robert
 *
 */

public class TemplateCollection {
	
	private boolean tracing = true; // for trace of activity
	public boolean tracing() {return tracing;}
	
	private Element templateUsageRoot; // root element of the template usage file
		
	private RMIMReader rmimReader;// reads the unconstrained CDA RMIM

	private Vector<TemplateSet> templateSets;// template sets, eg CCD, HITSP C32, etc.
	
	private EPackage copyModelPackage; // the overall package containing the constrained Ecore model
	
	private EPackage copyRMIMPackage; // the package for constrained RMIM classes
	
	private EPackage copyDataTypePackage; // the data types package in the constrained model
	
	private EClass constrainedTopClass; // root class of the constrained model
	
	private boolean makeDataTypeMappings = true;
	
	/**
	 * @return List of top classes of the constrained Ecore model, used in making mapping sets
	 */
	public List<EClass> topClasses()
	{
		Vector<EClass> topClasses = new Vector<EClass>();
		topClasses.add(constrainedTopClass);
		return topClasses;
	}
	
	/**
    * If a CDA document states that it complies with HITSP C32 
    * (i.e has the HITSP C32 template 1 on its ClinicalDocument node), 
    * and HITSP C32 contains a template H, which is a further constraint on CCD template C,
    *  then is the document 
    *  (a) allowed to contain nodes which are constrained only by C? 
    *  [strictTemplateConstraint = false] 
    *  or (b) must every node which is constrained by C also be constrained by H?	 
    *  [strictTemplateConstraint = true] 
    *  See Dev notes 5 November 2009.
    *  */
	// private boolean strictTemplateConstraint = false;
	
	/**
	 * long name = CDA class name, followed by sorted concatenated template ids
	 * short name = CDA class name, followed by a template name, followed by an index to make it unique
	 */
	private Hashtable<String,EClass> longNamesToClasses;
	private Hashtable<String,String> shortToLongNames;

	//---------------------------------------------------------------------------------------------
	//                      Constructor and reading template files
	//---------------------------------------------------------------------------------------------
	
	public TemplateCollection(Element templateUsageRoot, RMIMReader rmimReader)
	{
		this.templateUsageRoot = templateUsageRoot;
		this.rmimReader = rmimReader;
		templateSets = new Vector<TemplateSet>();
	}

	/**
	 * read the top template files, with file paths as defined in the template usage file 
	 */
	public void readTemplateFiles()
	throws MapperException
	{
		String[] topChildren = {"templateSet"};
		String[] topAtts = {"name","schematronFolderPath"};
		XMLUtil.checkChildElements(templateUsageRoot, topChildren);
		XMLUtil.checkAttributes(templateUsageRoot, topAtts);

		String schematronFolderPath = templateUsageRoot.getAttribute("schematronFolderPath");
		Vector<Element> tempSetEls = XMLUtil.namedChildElements(templateUsageRoot, "templateSet");
		for (int i = 0; i < tempSetEls.size(); i++)
		{
			Element templateSetEl = tempSetEls.get(i);
			
			String fileName = templateSetEl.getAttribute("schFileName");
			String folderName = templateSetEl.getAttribute("subFolder");
			String templatePath = schematronFolderPath + "/" + folderName + "/" + fileName;
			TemplateSet templateSet = new TemplateSet(this,templatePath,templateSetEl);
			addTemplateSet(templateSet);
		}
	}
	
	/**
	 * add a template set to the collection
	 * @param templateSet the template set to be added
	 */
	public void addTemplateSet(TemplateSet templateSet)
	{
		templateSets.add(templateSet);
	}
	
	/**
	 * check that all  parents of every template exist, and are at a lower level than it.
	 * check that if any template constrains another one, 
	 * (a) the constrained template exists
	 * (b) the constrained template is at the same level as the constraining template
	 * (c) the constrained and constraining template can exist inside the same outer template
	 * (d) the constrains relation is not circular
	 * Throw an exception on any violation.
	 */
	public void checkTemplateLevels() throws MapperException
	{
		// check all templates in all template sets
		for (Iterator<CDATemplate> it = allTemplates(); it.hasNext();)
		{
			CDATemplate temp = it.next();
			Vector<CDATemplate> temps = new Vector<CDATemplate>();
			checkConstraintsNotCircular(temp,temps);
			int level = temp.level();

			// find all parents; throw an exception if any do not exist
			for (Iterator<CDATemplate> ip = temp.allParentTemplates().iterator(); ip.hasNext();)
			{
				CDATemplate parent = ip.next();
				// any parent's level must be no higher than the template's own level
				if (parent.level() > level) 
					throw new MapperException(temp.descriptiveName() 
							+ " has a lower level than its parent " + parent.descriptiveName());
			}
			
			// find all constrained templates; throw an exception if any do not exist
			for (Iterator<CDATemplate> ic = temp.constrainedTemplates().iterator(); ic.hasNext();)
			{
				CDATemplate con = ic.next();
				// any constrained template's level must be equal to the template level
				if (con.level() != level) 
					throw new MapperException(temp.descriptiveName() 
							+ " has level unequal to its constrained template " + con.descriptiveName());
				
				/* every template that this template can be inside must be one of those
				 * which its constrained template can be inside */
				Vector<CDATemplate> constrainedInside = con.allParentTemplates();
				for (Iterator<CDATemplate> ip = temp.allParentTemplates().iterator(); ip.hasNext();)
				{
					CDATemplate parent = ip.next();
					if (!parent.oneOf(constrainedInside)) 
						throw new MapperException(temp.descriptiveName() + " can be inside " + parent.descriptiveName() 
								+ " but constrains " + con.descriptiveName() + " which cannot.");
				}
			}
		}
	}
	
	/**
	 * check that the 'constrains' relation between templates is not circular
	 * @param temp a template
	 * @param temps the chain of templates that constrain one another to this point
	 * @throws MapperException
	 */
	private void checkConstraintsNotCircular(CDATemplate temp, Vector<CDATemplate> temps)
	throws MapperException
	{
		Vector<CDATemplate> newTemps = new Vector<CDATemplate>();
		for (Iterator<CDATemplate> it = temps.iterator(); it.hasNext();)
		{
			CDATemplate next = it.next();
			newTemps.add(next);
			if (next.fullTemplateId().equals(temp.fullTemplateId()))
				throw new MapperException("Circular 'constrains' relation between templates at template '"
						+ temp.fullTemplateId() + "'");
		}
		newTemps.add(temp);
		
		for (Iterator<CDATemplate> iu = temp.directConstrainedTemplates().iterator(); iu.hasNext();)
		{
			CDATemplate deeper = iu.next();
			checkConstraintsNotCircular(deeper, newTemps);
		}
	}

	
	//---------------------------------------------------------------------------------------------
	//                               Accessors and iterators over templates
	//---------------------------------------------------------------------------------------------
	
	/**
	 * @return a plain iterator over all CDATemplate objects, in no special order
	 */
	public Iterator<CDATemplate> allTemplates()
	{
		Vector<CDATemplate> templates = new Vector<CDATemplate>();
		for (Iterator<TemplateSet> is = templateSets.iterator(); is.hasNext();)
		{
			for (Enumeration<CDATemplate> en = is.next().templates().elements();en.hasMoreElements();)
				templates.add(en.nextElement());
		}
		return templates.iterator();
	}
	
	/**
	 * @param templateId a template id
	 * @return the template with that id, or null if there is none
	 */
	public CDATemplate getTemplate(String templateId)
	{
		CDATemplate temp = null;
		for (Iterator<CDATemplate> it = allTemplates();it.hasNext();)
		{
			CDATemplate candidate = it.next();
			if (candidate.fullTemplateId().equals(templateId)) temp = candidate;
		}
		return temp;
	}
	
	
	/**
	 * @param name: name of a template set, e.g. 'CCD'
	 * @return the template set; or null if there is none
	 */
	public TemplateSet getTemplateSet(String name)
	{
		TemplateSet result = null;
		for (int i = 0; i < templateSets.size();i++)
			if (templateSets.get(i).getName().equals(name)) result = templateSets.get(i);
		return result;
	}

	/**
	 * @param context a CDAContext (mentioning various templates)
	 * @return all rules applicable in this context - because their template
	 * is on one of the nodes and the further path from the template node to the rule
	 * node is compatible with the context.
	 * As a side-effect, sets the context step number for each rule
	 */
	public Iterator<TemplateRule> applicableRules(TemplatedPath context)
	{
		Vector<TemplateRule> rules = new Vector<TemplateRule>();
		// loop over context steps
		for (int s = 0; s < context.length(); s++)
		{
			// loop over templates on each context step
			for (Iterator<String> is = context.step(s).templateIds().iterator();is.hasNext();)
			{
				CDATemplate template = getTemplate(is.next());
				// loop over rules on  each context
				for (Iterator<TemplateRule> ir = template.getRules().iterator(); ir.hasNext();)
				{
					TemplateRule rule = ir.next();
					if (rule.matchesContext(context, s)) rules.add(rule);
				}
			}
		}
		return rules.iterator();
	}

	//---------------------------------------------------------------------------------------------
	//                                    Resolving templates
	//---------------------------------------------------------------------------------------------
	
	/**
	 * Resolve all templates. A template is resolved when all the full contexts
	 * in which it can appear have been found.
	 */
	public void resolveAllTemplates()  throws MapperException
	{
		trace("****Resolving templates");
		// resolve the root templates of all template sets
		for (Iterator<TemplateSet> it = this.templateSets.iterator();it.hasNext();)
		{
			TemplateSet tSet = it.next();
			String rootTemplateId = tSet.rootTemplateId();
			CDATemplate rootTemplate = tSet.getTemplateByFullId(rootTemplateId);
			if (rootTemplate == null) throw new MapperException("No root template found for template set " + tSet.getName());

			TemplatedPath rootContext = new TemplatedPath();
			ContextStep rootStep = new ContextStep(null,getEntryClass().getName());
			rootContext.addStep(rootStep);
			rootTemplate.resolveRootContext(rootContext);
			// trace("Resolved root template " + rootTemplate.templateId() + " to context " + rootContext.stringForm());
		}

		/* iterate over all templates and resolve them. No template can be resolved
		 * until all the templates it can appear directly inside have been resolved. 
		 * Keep iterating over all templates until no templates are newly resolved in one pass. */
		boolean someResolvedThisPass = true;
		while (someResolvedThisPass)
		{
			someResolvedThisPass = false;
			for (Iterator<CDATemplate> it = allTemplates(); it.hasNext();)
			{
				CDATemplate template = it.next();
				/* on each pass, choose only templates that have not been resolved but 
				 * whose parent templates have all been resolved */
				if ((!template.resolved()) && (template.parentsAllResolved()))
				{
					someResolvedThisPass = true;

					// attempt to set the node name for the template, from its defining assertions
					template.setNodeNames();
					/* resolve the template contexts from its parent contexts (even if it 
					 * has no node names - to mark it resolved and stop the iteration)*/
					template.resolve();
					
					// if some node names were found
					if (template.nodeNames().size() > 0)
					{
						trace("");
						trace ("Template " + template.getLocalId() + "(" + template.name() 
								+ ") of template set " + template.templateSet().getName() +  " has " + template.allContexts().size() + " contexts and node names "
								+ template.allNodeNames());
						for (int t = 0; t < template.allContexts().size(); t++)
							trace(template.allContexts().get(t).stringForm());
					}
					else if (template.nodeNames().size() == 0)
						trace("No node names found for template " + template.getLocalId() + " of template set " + template.templateSet().getName());
				}
			}
		}
		writeUnresolvedTemplates();
	}
	
	public void writeUnresolvedTemplates()
	{
		trace("\nTemplates not resolved:");
		for (Iterator<CDATemplate> it = allTemplates(); it.hasNext();)
		{
			CDATemplate template = it.next();
			if (!template.resolved())  trace(template.getLocalId());
		}		
	}

	
	//---------------------------------------------------------------------------------------------
	//                                  Constraining the Ecore model
	//---------------------------------------------------------------------------------------------

	/**
	 * Make a constrained Ecore model, by applying the resolved 
	 * templates to the CDA Ecore model
	 */
	public EPackage makeConstrainedCDAECoreModel(String ecoreFilePath)
	throws MapperException
	{
		trace("Constraining CDA Ecore model");
		// writeAllTemplates();
		
		longNamesToClasses = new Hashtable<String,EClass>();
		shortToLongNames = new Hashtable<String,String>();

		// create three packages for the constrained model (instance variables of this class)
		createCopyPackages(ecoreFilePath);

		// create an EClass for the top class of the constrained model, and confirm it
		EClass cdaTopClass = getEntryClass();
		constrainedTopClass = findOrMake(cdaTopClass.getName());
		shallowCopy(cdaTopClass,constrainedTopClass);
		confirmInPackage(constrainedTopClass);		
		Vector<EClass> newClasses  = extendClassList(new Vector<EClass>(),constrainedTopClass);
		
		// initial template context, without any templates. Null association for the first step.
		TemplatedPath context = extendContext(new TemplatedPath(),null,cdaTopClass.getName());
		
		/* add templates to the top node and the initial context; 
		 * there is only one group of contexts that can co-exist, i.e all the templates for that node */
		Vector<CDATemplate>  topTemplates = templatesForTopContext(context);
		addTemplatesToContext(context,topTemplates);
		addTemplateAnnotations(context,constrainedTopClass,topTemplates);
		
		// recursive descent of the CDA model
		extendConstrainedCDAModel(cdaTopClass, newClasses, context);
		
		return copyModelPackage;
	}
	
	/**
	 * Create the packages that will be used for the constrained CDA Ecore model
	 */
	private void createCopyPackages(String ecoreFilePath) throws MapperException
	{
		// make the overall model package
		copyModelPackage = EcoreFactory.eINSTANCE.createEPackage();
		copyModelPackage.setName("constrainedCDAModel");
		copyModelPackage.setNsPrefix("cdam");
		copyModelPackage.setNsURI("CDAModel");
		// ensure this class model will be viewed as a templated RMIM in the class model view
		ModelUtil.addMIFAnnotation(copyModelPackage, "RMIM", "true");
		ModelUtil.addMIFAnnotation(copyModelPackage, "templated", "true");

		// add a package for the CDA RMIM
		copyRMIMPackage = EcoreFactory.eINSTANCE.createEPackage();
		copyRMIMPackage.setName(CDAConverter.constrainedRMIMPackageName);
		copyRMIMPackage.setNsPrefix(RMIMReader.CDAPREFIX);
		copyRMIMPackage.setNsURI(CDAConverter.V3NAMESPACEURI);
		copyModelPackage.getESubpackages().add(copyRMIMPackage);

		// re-use the data types package, adding it as a sub-package
		copyDataTypePackage = rmimReader.v3DataTypeHandler().readDataTypeSchema(ecoreFilePath,makeDataTypeMappings);
		copyModelPackage.getESubpackages().add(copyDataTypePackage);	
	}
	
	
	/**
	 * 
	 * @param topPackage
	 * @return true if this model is a templated RMIM
	 */
	public static boolean isConstrainedRMIM(EPackage topPackage)
	{
		String isRMIM = ModelUtil.getEAnnotationDetail(topPackage, "RMIM");
		if (isRMIM == null) return false;
		if (!isRMIM.equals("true")) return false;

		String isTemplated = ModelUtil.getEAnnotationDetail(topPackage, "templated");
		if (isTemplated == null) return false;
		if (!isTemplated.equals("true")) return false;

		return true;
	}

	
	/**
	 * recursive descent of the CDA Ecore model, to fill out the constrained model, 
	 * applying constraints and splitting classes as defined by templates.
	 * 
	 * @param cdaClass a class in the unconstrained CDA RMIM model
	 * @param copyClasses Vector of classes for the steps of the current context, in the constrained model.
	 * The last of these corresponds to the class cdaClass; 
	 * it has been confirmed (put in the RMIM package) and
	 * shallow copied from cdaClass (i.e its immediate child classes exist), 
	 * but its child classes have not yet been put in the package.
	 * @param templatedPath the template context.
	 * 
	 * There are always some constraints in the subtree below the final node of the 
	 * context; this method is not called otherwise
	 */
	private void extendConstrainedCDAModel(EClass cdaClass, Vector<EClass> copyClasses, 
			TemplatedPath templatedPath)
	throws MapperException
	{
		// get the class at the end of the chain, which is being worked on
		EClass copyClass = copyClasses.get(copyClasses.size() - 1);
		String tPath = templatedPath.stringForm();
		String cName = copyClass.getName();
		trace("Extend at trail " + tPath + " and class " + cName);

		/* 1.	Check if the cardinality of the class node is constrained 
		 * by any template rule. If so, apply the constraint, checking back 
		 * up the tree as far as the context node of the constraining rule 
		 * (lowerBound or upperBound of all EReferences must be constrained) */
		int maxIs1Step = cardinalityConstrainedFromStep(templatedPath, true);
		if (maxIs1Step > -1) constrainCardinalityFromStep(templatedPath, copyClasses,maxIs1Step,true);

		int minIs1Step = cardinalityConstrainedFromStep(templatedPath, false);
		if (minIs1Step > -1) constrainCardinalityFromStep(templatedPath, copyClasses,maxIs1Step,false);
				
		/* 2.	For each attribute of the eClass, iterate over all applicable templates 
		 * (all rules, all constraining assertions) to see if any constrain the attribute 
		 * value.  */
		// fixAttributes(copyClass, context);
		
		// to record which associations need to be removed, because templated copies of them have been made
		Hashtable<String,String> associationsToRemove = new Hashtable<String,String>();
		
		/* main loop over child classes of the copied class. 
		 * Copy the reference list first,in case you add to it (?) */
		Vector<EReference> refs = new Vector<EReference>();
		for (Iterator<EReference>  iz = copyClass.getEAllReferences().iterator();iz.hasNext();) {refs.add(iz.next());}
		for (Iterator<EReference>  ir = refs.iterator();ir.hasNext();)
		{
			EReference copyRef = ir.next();
			String copyRefName = copyRef.getName();
			// get the corresponding association in the unconstrained model
			String modelRefName = ModelUtil.getEAnnotationDetail(copyRef, "CDA_Name");
			// if (modelRefName != null) trace("Renamed ref '" + copyRefName + "' at " + nameTrail(copyClasses));
			if (modelRefName == null) modelRefName = copyRefName;
			// FIXME: get round this problem properly
			if (modelRefName.endsWith("_T")) modelRefName = modelRefName.substring(0,modelRefName.length()-2);
			
			/* 3.	For each EReference of the class, check if the target node brings in 
			 * any more templates.  */
			EClass modelChildClass = getChildClass(cdaClass,modelRefName);
			EClass copyChildClass = getChildClass(copyClass,copyRefName);
			TemplatedPath childContext = extendContext(templatedPath,modelRefName,modelChildClass.getName());
			Vector<Vector<CDATemplate>> templateSets = templateSetsForContext(childContext);
			// if (!isDataTypeClass(modelChildClass)) trace("Found " + templates.size() + " template groups for child class " + modelChildClass.getName());

			if (isDataTypeClass(modelChildClass))
			{
				// trace("Data type class " + modelChildClass.getName());
				/* Nothing to do now. copyClass has been extended by shallowCopy, which 
				 * made copyChildClass to be the appropriate data type class. 
				 * This {} is put in to make the subsequent 'elses' not fire for 
				 * a data type class. */
			}

			/* If the target node is an ActRelationship and has no templates itself, 
			 * go to the descendant Act clones (try all possible choices – encounter, supply, etc….) 
			 * to see if they bring in any more templates. If  so, split the ActRelationship */
			else if ((CDAConverter.isActRelationship(modelChildClass)) && (templateSets.size() == 0))
			{
				// the copy ActRelationship needs its Act child classes for both cases below
				shallowCopy(modelChildClass,copyChildClass);

				/* If the ActRelationship clone has no templates on its Act clone 
				 * child classes, carry on the recursion */
				int branches = actChildrenWithTemplates(modelChildClass, childContext);
				// trace("branches: " + branches);
				if (branches == 0)
				{
					boolean constraints = doNoTemplatesCase(childContext,modelChildClass, copyClass,  copyRef, 
							copyChildClass,copyClasses);
					if (constraints) associationsToRemove.put(copyRef.getName(),"1");
				}

				else if (branches > 0)
				{
					/* note that you are eventually going to remove this association to the ActRelationship class, 
					 * because there are templated versions of it. */
					associationsToRemove.put(copyRef.getName(), "1");

					int branch = 0;
					// find all Act classes beneath the ActRelationship, and their contexts
					for (Iterator<EReference> ix = modelChildClass.getEAllReferences().iterator();ix.hasNext();)
					{
						EReference nextRef = ix.next();
						String nextRefName = nextRef.getName();
						EClass modelActClass = getChildClass(modelChildClass,nextRefName);
						if (!isDataTypeClass(modelActClass))
						{
							TemplatedPath actContext = extendContext(childContext,nextRefName,modelActClass.getName());
							templateSets = templateSetsForContext(actContext);
							// trace("Act class " + modelActClass.getName() + " has " + templates.size() + " exclusive template groups");
							
							// do nothing for this Act clone class if it has no templates (others do)
							if (templateSets.size() > 0)
							{
								for (Iterator<Vector<CDATemplate>> ics = templateSets.iterator();ics.hasNext();)
								{
									Vector<CDATemplate> coexistent = ics.next();
									
									EClass templatedActClass = rearrangeActRelationship(coexistent,cdaClass,copyClasses,copyRef,
											modelChildClass,copyChildClass,nextRef,
											modelActClass,actContext,branch,branches);
									if (templatedActClass != null)
										addTemplateAnnotations(actContext,templatedActClass,coexistent);
									
								} // end of loop over mutually exclusive sets of templates
								
								branch++; // count the Act clones with templates																

							} // end of 'if (templates.size() > 0)' section
							
						} // end of 'if (!isDataTypeClass(modelActClass))' section
						
					} // end of loop over all Act classes beneath the ActRelationship
					
				} // end of 'if (branches > 0)' section
											
			} // end of 'if (isActRelationship(modelChildClass))' section
			
			/* 4.	If no templates are found on the target node, check if there are any templates or 
			 * constraints in the subtree. If not, switch to plain copy of the tree. 
			 * If there are still deeper templates:
				•	Make a shallow copy of the class and give it a new unique name
				•	Clone the context and add a step, adding no new templates
				•	Recursively descend
	         */
			else if (templateSets.size() == 0)
			{
				boolean constraints = doNoTemplatesCase(childContext,modelChildClass,copyClass, copyRef, 
						copyChildClass,copyClasses);
				if (constraints) associationsToRemove.put(copyRef.getName(),"1");
			}
			
			/* 6.	If any templates are found, split the templates into mutually 
			 * exclusive sets. Iterate over the sets. 
			 * For each mutually exclusive set:
				•	Make a shallow copy of the class with a new unique name
				•	Clone the context, and add the new templates on the appropriate node
				•	Recursively descend
	        */
			else if (templateSets.size() > 0) 
			{
				trace("Template sets: " + templateSets.size() + " for path " + childContext.stringForm());
				// loop over groups of mutually consistent templates
				for (Iterator<Vector<CDATemplate>> ics = templateSets.iterator();ics.hasNext();)
				{
					Vector<CDATemplate> coexistent = ics.next();
					String templateName = mostSpecificTemplate(coexistent).name();
					
					// if the template class exists, find it and attach it; otherwise make it
					boolean templateClassExistedAlready = templateClassExists(cdaClass,copyRef,coexistent); 
					EClass cloneChildClass = makeOrFindTemplateClass(cdaClass,copyClass,copyRef,templateName,coexistent);				
					
					// if the template class was newly made, continue the recursion
					if (!templateClassExistedAlready)
					{
						addTemplateAnnotations(templatedPath,cloneChildClass,coexistent);
						Vector<EClass> cloneClasses = extendClassList(copyClasses,cloneChildClass);
						TemplatedPath cloneContext = childContext.clone();
						addTemplatesToContext(cloneContext,coexistent);
						extendConstrainedCDAModel(modelChildClass, cloneClasses,cloneContext);					
					}					
				}
				/* note that you are going to remove this association to the class, 
				 * because there are templated versions of it. */
				associationsToRemove.put(copyRef.getName(), "1");
				
			} // end of 'if (templates.size() > 0)' section
			
		} // end of main loop over EReferences	
		
		// remove the associations that have been templated, for some classes
		if (removeRenamedRelations(copyClass))
			removeAssociations(copyClass,associationsToRemove);
				
	}
	
	

	/**
	 * @param coexistent a Vector of templates that can coexist on the same class
	 * @return the name of the most specific template 
	 * - one which is not constrained by any of the others.
	 * It does not matter if there is more than one, and this method makes an arbitrary choice.
	 */
	private CDATemplate mostSpecificTemplate(Vector<CDATemplate> coexistent) throws MapperException
	{
		CDATemplate mostSpecific = null;
		String allNames = ""; // for message in case of exception
		for (Iterator<CDATemplate> it = coexistent.iterator();it.hasNext();)
		{
			CDATemplate temp = it.next();
			allNames = allNames + temp.descriptiveName() + "; ";
			// check if this template is constrained by any others in the set
			boolean constrained = false;
			for (Iterator<CDATemplate> ic = coexistent.iterator();ic.hasNext();)
			{
				CDATemplate con = ic.next();
				if (con.constrains(temp)) constrained = true;
			}
			// remember any unconstrained template
			if (!constrained) mostSpecific = temp;
		}
		if (mostSpecific == null) throw new MapperException("Cannot find an unconstrained template in set " + allNames);
		return mostSpecific;
	}
	
	
	/**
	 * @param theClass a class made by the template specialisation
	 * @return true if, for any association renamed,
	 * the original should be removed from the model.
	 * This is only so for classes that are expected to have just one child class
	 */
	private boolean removeRenamedRelations(EClass theClass)
	{
		String[] singleChildRIMClasses = {"ActRelationship","Participation"};
		boolean remove = false;
		String RIMClass = ModelUtil.getEAnnotationDetail(theClass, "RIM Class");
		if ((RIMClass != null) && (GenUtil.inArray(RIMClass, singleChildRIMClasses))) remove = true;
		return remove;
	}
	
	/**
	 * Handle the case where no new templates are found on the latest
	 * extension of the context path
	 * @param context the new context - no templates on its last step
	 * @param modelClass  the new child class in the unconstrained CDA model
	 * @param copyParentClass the parent class in the constrained copy model
	 * @param copyRef the EReference from the constrained parent to the constrained child
	 * @param copyClass the constrained child class
	 * @param copyClasses Vector of copied classes, matching newContext except for the last element
	 *
	 * Check if there are any templates or constraints in the subtree. 
	 * If not, switch to a plain copy of the tree. 
	 * If there are still deeper templates:
		•	Make a shallow copy of the class and give it a new unique name
		•	Clone the context and add a step, adding no new templates
		•	Recursively descend
     */
	private boolean doNoTemplatesCase(TemplatedPath context,EClass modelClass,
			EClass copyParentClass, EReference copyRef, EClass copyClass, 
			Vector<EClass> copyClasses)
	throws MapperException
	{
		// trace("No-template case for class " + modelClass.getName() + " at context " + context.stringForm());
		boolean constraints = constraintsInSubtree(context);
		// no changes to come in subtree; use plain copy (if the class is not already in the package)
		if (!constraints) 
		{
			if (copyRMIMPackage.getEClassifier(copyClass.getName()) == null)
			{
				// no need for a name change, as descendant subtree is unconstrained
				confirmInPackage(copyClass);
				plainCopy(modelClass, copyRef,copyClass);				
			}
			// if the class is already in the package,do nothing
		}
		/* changes are expected in the subtree from rules in existing templates, or from new 
		 * templates; make a copy with a new name, and extend recursively */
		else
		{
			EClass copyCloneClass = copyWithNewUniqueName(copyParentClass,copyRef,"T"); // look at parent to change its EReference name; no template name				

			// put the class with a new name in the package
			confirmInPackage(copyCloneClass); // put in package, now its name is fixed	
			String CDAName = ModelUtil.getEAnnotationDetail(copyCloneClass, "CDA_Name");
			shallowCopy(modelClass, copyCloneClass); // destroys previous annotations
			ModelUtil.addMIFAnnotation(copyCloneClass, "CDA_Name", CDAName);
			Vector<EClass> newCopyClasses = extendClassList(copyClasses,copyCloneClass);
			extendConstrainedCDAModel(modelClass, newCopyClasses,context);
		}		
		// trace("Finished no template case for class " + modelClass.getName() + "; result " + constraints);
		return constraints;
	}
	
	/**
	 * make the rearranged ActRelationship clone class, with just one Act clone class beneath it,
	 * for one branch of a split caused by templates on one or more Act children of
	 * the one ActRelationship class in the unconstrained model
	 * 
	 * @param coexistent a set of templates that can coexist on one Act clone node
	 * @param copyClasses Vector of classes down the current branch of the model, down to the
	 * parent of the ActRelationship class
	 * @param refToActRel association from the ActRelationship's parent to the ActRelationship
	 * @param modelActRelClass ActRelationship class in the unconstrained model
	 * @param copyActRelClass ActRelationship class in the constrained model, which is to be split
	 * @param refToAct association from the ActRelationship to the current child Act clone 
	 * @param modelActClass current Act clone class, which has templates
	 * @param actContext context path as far as the Act clone class
	 * @return the templated Act class if it has to be made; null if it exists already
	 * @throws MapperException
	 */
	private EClass rearrangeActRelationship(Vector<CDATemplate> coexistent,
			EClass modelParentClass, Vector<EClass> copyClasses, EReference refToActRel,
			EClass modelActRelClass, EClass copyActRelClass,EReference refToAct,
			EClass modelActClass,TemplatedPath actContext,int branch, int branches)
	throws MapperException
	{
		// trace("ActRelationship class " + modelActRelClass.getName() + " with Act child " + modelActClass.getName());
		
		// parent class of the ActRelationship, in the constrained copy
		EClass copyParentClass = copyClasses.get(copyClasses.size() - 1);
		// current Act class in the copy of the model
		EClass copyActClass = getChildClass(copyActRelClass,refToAct.getName());

		// use the the most specific (constraining) template name in all new names, and note its local id
		String templateName = mostSpecificTemplate(coexistent).name();
		@SuppressWarnings("unused")
		String localId = mostSpecificTemplate(coexistent).getLocalId(); /// only used for tracing

		/* need to differentiate different Act classes which the template may match inside the 
		 * ActRelationship class, when looking for existing template classes with a given name */
		if (branches > 1)  templateName = templateName + "_" + modelActClass.getName();
		
		boolean templateActRelClassExisted = templateClassExists(modelParentClass,refToActRel,coexistent); 		
		EClass templateActRelClass = makeOrFindTemplateClass(modelParentClass,copyParentClass,refToActRel,templateName,coexistent);
		EClass templateActClass = null; // return non-null only if it has to be made
		
		// if the ActRelationship templated class is new, make the tree beneath it
		if (!templateActRelClassExisted)
		{
			Vector<EClass> actRelClasses = extendClassList(copyClasses,templateActRelClass);

			/* remove all associations of the renamed ActRelationship class, 
			 * and add one association to the copy Act clone */
			ModelUtil.removeEReferences(templateActRelClass);

			// make an EReference, to copy in makeOrFindTemplateClass
			EReference newRefToAct = EcoreFactory.eINSTANCE.createEReference();
			newRefToAct.setName(refToAct.getName()); // old name; will be ignored
			newRefToAct.setLowerBound(1);
			newRefToAct.setUpperBound(1);
			newRefToAct.setContainment(true);
			newRefToAct.setEType(copyActClass);
			
			templateActClass = makeOrFindTemplateClass (modelActRelClass,templateActRelClass,newRefToAct,templateName,coexistent);
			// trace("Classes for template " + localId + ": " + templateActRelClass.getName() + " " + templateActClass.getName());				
			
			// add the templates to the context, and carry on the recursion
			TemplatedPath actContextClone = actContext.clone();
			addTemplatesToContext(actContextClone,coexistent);
			Vector<EClass> actClasses = extendClassList(actRelClasses,templateActClass);
			extendConstrainedCDAModel(modelActClass, actClasses,actContextClone);				
			
		}
		// trace("Finished ActRelationship class " + modelActRelClass.getName());	
		return templateActClass;
	}
	
	private void addTemplateAnnotations(TemplatedPath context, EClass templatedClass,Vector<CDATemplate> coexistent)
	{
		for (int i = 0; i < coexistent.size(); i++)
		{
			CDATemplate template = coexistent.get(i);
			String suffix = "";
			if (i > 0) suffix = "_" + i;
			String templateKey = "template" + suffix;

			// add one annotation saying the template is on the EClass
			ModelUtil.addMIFAnnotation(templatedClass, templateKey, template.fullTemplateId());

			// add annotations for each constraint the template puts on descendant nodes
			template.addConstraintAnnotations(context, templatedClass,suffix);
		}
	}



	//---------------------------------------------------------------------------------------------
	//                    Small methods in support of constraining the CDA Ecore model
	//---------------------------------------------------------------------------------------------

	/**
	 * @param context
	 * @param templates
	 * record the ids of all the templates on the inner step of a context
	 */
	private void addTemplatesToContext(TemplatedPath context,Vector<CDATemplate> templates)
	{
		int lastStep = context.length() -1;
		for (int t = 0; t < templates.size();t++) 
			context.step(lastStep).addTemplateId(templates.get(t).fullTemplateId());		
	}
	
	/**
	 * confirm that a class will now not be renamed or split, 
	 * by adding it to the RMIM package of the constrained model;
	 * throw an Exception if it is already in the package
	 * @param theClass
	 */
	private void confirmInPackage(EClass theClass)
	throws MapperException
	{
		if (copyDataTypePackage.getEClassifier(theClass.getName()) != null)
			throw new MapperException("Class " + theClass.getName() + " is already in the copy data  types package.");
		if (copyRMIMPackage.getEClassifier(theClass.getName()) != null)
			throw new MapperException("Class " + theClass.getName() + " is already in the copy RMIM package.");
		copyRMIMPackage.getEClassifiers().add(theClass);
		//trace("Added class " + theClass.getName() + " to copy RMIM package");
	}
	
		
	/**
	 * @param classes classes on the steps of the current context
	 * @param newClass class to go on the next step
	 * @return extended list of classes
	 */
	private Vector<EClass> extendClassList(Vector<EClass> classes, EClass newClass)
	{
		Vector<EClass> newClasses = new Vector<EClass>();
		for (int i = 0; i < classes.size(); i++) newClasses.add(classes.get(i));
		newClasses.add(newClass);
		return newClasses;
	}
	
	
	/**
	 * remove named associations from a class
	 * @param theClass
	 * @param associationsToRemove
	 */
	private void removeAssociations(EClass theClass, Hashtable<String,String> associationsToRemove)
	{
		for (Enumeration<String> en = associationsToRemove.keys();en.hasMoreElements();)
		{
			EStructuralFeature ref = theClass.getEStructuralFeature(en.nextElement());
			if (ref != null) 
			{
				theClass.getEStructuralFeatures().remove(ref);
				trace("Removing association " + ref.getName() + " from class "  + theClass.getName());
			}
		}		
	}

	
	@SuppressWarnings("unused")
	private String nameTrail(Vector<EClass> classes)
	{
		String trail = "";
		for (Iterator<EClass> it = classes.iterator();it.hasNext();)
			trail = trail + "/" + it.next().getName();
		return trail;
	}
	
	private TemplatedPath extendContext(TemplatedPath context, String assocName, String className)
	{
		TemplatedPath newContext = context.clone();
		newContext.addStep(new ContextStep(assocName,className));
		return newContext;
	}
	
	/**
	 * @param actRelClass and ActRelationship clone
	 * @param context the context at that class
	 * @return the number of Act child classes which carry any templates
	 */
	private int actChildrenWithTemplates(EClass actRelClass, TemplatedPath context)
	throws MapperException
	{
		int branches = 0;
		// find all Act classes beneath the ActRelationship, and their contexts
		for (Iterator<EReference> ix = actRelClass.getEAllReferences().iterator();ix.hasNext();)
		{
			EReference nextRef = ix.next();
			String nextRefName = nextRef.getName();
			EClass modelActClass = getChildClass(actRelClass,nextRefName);
			TemplatedPath nextContext = extendContext(context,nextRefName,modelActClass.getName());
			Vector<Vector<CDATemplate>> templateSets = templateSetsForContext(nextContext);
			if (templateSets.size() > 0)  branches++;
		}
		return branches;
	}
	
	/**
	 * @param context a CDA context
	 * @return true if there are expected to be template constraints
	 * in the subtree below the final node of the context -
	 * either because some templates in the context have rules and assertions
	 * that reach into the subtree, or because there are other
	 * templates in the subtree
	 */
	private boolean constraintsInSubtree(TemplatedPath context) throws MapperException
	{
		return ((someRulesAffectSubtree(context)) || (moreTemplatesInSubtree(context)));
	}

	
	/**
	 * 
	 * @param parentModelClass
	 * @param ref
	 * @param templates
	 * @return true if the template class exists already, so there is no need to extend 
	 * it downwards
	 * @throws MapperException
	 */
	private boolean templateClassExists(EClass parentModelClass, EReference ref, Vector<CDATemplate> templates) 
	throws MapperException
	{
		String refName = ModelUtil.getEAnnotationDetail(ref, "CDA_Name");
		if (refName == null) refName = ref.getName();
		EClass noTemplateModelClass = getChildClass(parentModelClass,refName);

		String key = longName(noTemplateModelClass,templates);
		return  (longNamesToClasses.get(key) != null);
	}
	
	/**
	 * Either find a template class already made in the constrained RMIM package, and attach it to its parent;
	 * Or make it, shallow copy it, put it in the package and attach it to its parent. 
	 * 
	 * @param parentModelClass the class in the unconstrained model whose copy is to be
	 * parent of the template class
	 * @param parentCopyClass the class in the constrained model which is to be
	 * parent of the template class
	 * @param ref the association that the association to the templated class is to be made from
	 * @param templateName the template name
	 * 
	 * @return the template class
	 * @throws MapperException
	 */
	private EClass makeOrFindTemplateClass(EClass parentModelClass, EClass parentCopyClass, 
			EReference ref, String templateName,Vector<CDATemplate> templates)
	throws MapperException
	{
		EClass templatedClass = null;
		boolean existsAlready = templateClassExists(parentModelClass,ref, templates);
	
		String refName = ModelUtil.getEAnnotationDetail(ref, "CDA_Name");
		if (refName == null) refName = ref.getName();
		EClass noTemplateModelClass = getChildClass(parentModelClass,refName);
		String longName = longName(noTemplateModelClass,templates);
		
		if (existsAlready)
		{
			templatedClass = longNamesToClasses.get(longName);
		}
		else
		{
			String templateClassName = makeNewTemplatedName(noTemplateModelClass.getName(), templateName);
			templatedClass = EcoreFactory.eINSTANCE.createEClass();
			templatedClass.setName(templateClassName);

			trace("making new class '" + templateClassName + "' from '" + noTemplateModelClass.getName() + "'");
			// ModelUtil.traceRefNames(noTemplateModelClass);
			shallowCopy(noTemplateModelClass,templatedClass);
			// ModelUtil.traceRefNames(templatedClass);

			ModelUtil.addMIFAnnotation(templatedClass, "CDA_Name", noTemplateModelClass.getName());
			confirmInPackage(templatedClass);
			longNamesToClasses.put(longName, templatedClass);
			shortToLongNames.put(templateClassName, longName);			
		}

		String templateRefName = makeRefName(templatedClass.getName());
		if (parentCopyClass.getEStructuralFeature(templateRefName)== null)
		{
			EReference newRef = EcoreFactory.eINSTANCE.createEReference();
			newRef.setName(templateRefName);
			newRef.setEType(templatedClass);
			newRef.setLowerBound(ref.getLowerBound());
			newRef.setUpperBound(ref.getUpperBound());
			newRef.setContainment(ref.isContainment());
			ModelUtil.addMIFAnnotation(newRef, "CDA_Name", refName);
			parentCopyClass.getEStructuralFeatures().add(newRef);			
		}
		
		return templatedClass;
	}

	
	/**
	 * make a new name for a constrained class, which has not been used before
	 * @param oldName name of a class in the un-templated model
	 * @param templateName the template name which is to be included in the new name
	 * @return a unique new name
	 */
	private String makeNewTemplatedName(String oldName, String templateName)
	{
		String stem = oldName + "_" + GenUtil.gaplessForm(templateName);
		String name = stem;
		int index = 1;
		while (shortToLongNames.get(name) != null)
		{
			name = stem + "_" + index;
			index++;
		}
		return name;
	}
	
	/**
	 * @param className
	 * @return the className converted to a reference name, by making
	 * its first letter lower case
	 */
	private String makeRefName(String className)
	{
		String initial = className.substring(0,1);
		String remainder = className.substring(1);
		return (initial.toLowerCase() + remainder);
	}
	
	
	/**
	 * @param temps
	 * @return a unique long name, made from a class name and a set of templates, 
	 * in a way that does not depend on their order
	 */
	private String longName(EClass modelClass, Vector<CDATemplate> temps)
	{
		String key = modelClass.getName();
		Vector<String> keys = new Vector<String>();
		for (Iterator<CDATemplate> it = temps.iterator();it.hasNext();) keys.add(it.next().fullTemplateId());
		Collections.sort(keys);
		for (Iterator<String> iu = keys.iterator(); iu.hasNext();) {key = key + "_" + iu.next();}
		return key;
	}
	/**
	 * 
	 * @param parentClass
	 * @param ref
	 * @param templateName
	 * Create a new ERreference on the parent class and a new target class for the EReference -
	 * copied from the supplied EReference and its target class.
	 * Then give a new unique name to the new EReference and new target class
	 * @return the new target class
	 */
	private EClass copyWithNewUniqueName(EClass parentClass, EReference ref, String templateName)
	throws MapperException
	{
		EReference newRef = EcoreFactory.eINSTANCE.createEReference();
		newRef.setName(ref.getName());
		newRef.setLowerBound(ref.getLowerBound());
		newRef.setUpperBound(ref.getUpperBound());
		newRef.setContainment(ref.isContainment());
		parentClass.getEStructuralFeatures().add(newRef);
		
		EClassifier oldInner = ref.getEType();
		if (oldInner == null)
			throw new MapperException("Association '" + ref.getName() 
					+ "' from class '"  + parentClass.getName() + "' has no target class.");
		
		EClass newInner = EcoreFactory.eINSTANCE.createEClass();
		newInner.setName(oldInner.getName());
		newRef.setEType(newInner);
		
		return giveNewUniqueName(parentClass, newRef,templateName);
	}
	
	/**
	 * @param parentClass an EClass in the constrained ECore model, which has already been put in an EPackage
	 * @param ref a containment association, whose name is as in the unconstrained model, and which has 
	 * a class at the end of it, named as in the unconstrained model.
	 * @param templateName the name of a template which gave rise to this association and class
	 * Give a new name to the association and the inner class, incorporating the template name
	 * if that is not empty.
	 * Ensure that the association name is unique within the parent class, and the the class name 
	 * is unique within the package of the parent class.
	 * Annotate both the association and the inner class to say what their names were in
	 * the unconstrained model.
	 * Add the inner class to the EPackage of the parent class
	 * @return the inner class
	 * 
	 */
	private EClass giveNewUniqueName(EClass parentClass, EReference ref, String templateName)
	throws MapperException
	{
		String gaplessName = GenUtil.gaplessForm(templateName);
		// non-clashing reference name
		String oldRefName = ref.getName();
		String newRefRoot = oldRefName + "_" + gaplessName;
		String newRefName = newRefRoot;
		int refIndex = 0;
		while (refNameClash(parentClass,newRefName))
		{
			refIndex++;
			newRefName = newRefRoot + "_" + refIndex;
		}
		ref.setName(newRefName);
		ModelUtil.addMIFAnnotation(ref, "CDA_Name", oldRefName);

		// non-clashing class name
		EClass innerClass = (EClass)ref.getEType();
		String oldClassName = innerClass.getName();
		EPackage thePackage = parentClass.getEPackage();
		if (thePackage == null) throw new MapperException("Class " + parentClass.getName() 
				+ " is not in a package when renaming association " + newRefName);
		String newClassRoot = oldClassName + "_" + gaplessName;
		String newClassName = newClassRoot;
		int classIndex = 0;
		while (classNameClash(thePackage,newClassName))
		{
			classIndex++;
			newClassName = newClassRoot + "_" + classIndex;
		}
		innerClass.setName(newClassName);
		ModelUtil.addMIFAnnotation(innerClass, "CDA_Name", oldClassName);
		return innerClass;
		
	}
	
	/**
	 * @param parentClass
	 * @param newRefName
	 * @return true if the proposed EReference name clashes with one already on the EClass
	 */
	private boolean refNameClash(EClass parentClass,String newRefName)
	{
		boolean clash = false;
		for (Iterator<EReference> ir = parentClass.getEAllReferences().iterator(); ir.hasNext();)
			if (ir.next().getName().equals(newRefName)) clash = true;
		return clash;
	}
	
	/**
	 * @param thePackage
	 * @param newClassName
	 * @return true if the proposed new class name clashes with one already in the package
	 */
	private boolean classNameClash(EPackage thePackage,String newClassName)
	{
		return (thePackage.getEClassifier(newClassName) != null);
	}
	
	/**
	 * 
	 * @param parent a parent class
	 * @param refName the name of a containment association
	 * @return the child EClass reached through the association; must not be null
	 */
	private EClass getChildClass(EClass parent,String refName) throws MapperException
	{
		EClass child = null;
		String refNames = "";
		for (Iterator<EReference> ir = parent.getEAllReferences().iterator();ir.hasNext();)
		{
			EReference ref  = ir.next();
			refNames = refNames + ref.getName() + " ";
			if (ref.getName().equals(refName)) child = (EClass)ref.getEType();
		}
		if (child == null) throw new MapperException("Class '" + parent.getName() 
				+ "' has no child class via association '" 
				+ refName + "'; but has associations " + refNames);
		return child;
	}
	
	/**
	 * @param context the current context path
	 * @return true if some of the rules in some of the templates in this
	 * context imply constraints on nodes in the subtree below the current 
	 * final node of the context
	 */
	private boolean someRulesAffectSubtree(TemplatedPath context)
	{
		boolean someAffect = false;
		for (Iterator<TemplateRule> ir = applicableRules(context);ir.hasNext();)
		{
			TemplateRule rule = ir.next();
			if (rule.constrainsSubtreeBelow(context, rule.getContextStep())) someAffect = true;
		}
		return someAffect;
	}
	
	//-------------------------------------------------------------------------------------------
	//           Applying constraints to the Ecore model (will be changed)
	//-------------------------------------------------------------------------------------------
	
	
	/**
	 * @param context a context path
	 * @param newClasses Vector of EClasses for each step of the path
	 * @param startStep context step of a rule constraining a cardinality
	 * @param isMaximum true if it is a max cardinality; false if a min cardinality
	 * Set the upper or lower bounds of all the named EReferences along the path to 1,
	 * so as to guarantee that the min or max  cardinality of the whole constrained path is 1
	 */
	private void constrainCardinalityFromStep(TemplatedPath context, Vector<EClass> newClasses,
			int startStep, boolean isMaximum)
	{
		// iterate over steps from the rule step to the penultimate step of the context
		for (int s = startStep; s < context.length()-1; s++)
		{
			// from the EClass on the step, get the association whose name is the name of the next step
			for (Iterator<EReference> ir = newClasses.get(s).getEAllReferences().iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				if (ref.getName().equals(context.step(s+1).associationName()))
				{
					// set the lower or upper bound of the association to 1.
					if (isMaximum) ref.setUpperBound(1);
					else ref.setLowerBound(1);
				}				
			}
		}
	}
	
	//------------------------------------------------------------------------------------------------
	//                         Copying parts of the Ecore class model
	//------------------------------------------------------------------------------------------------
	
	/**
	 * @param model the model EClass which a copy is being made of
	 * @param copyParentRef the reference to the copy in the copy model
	 * @param copy an empty EClass which is to be the copy 
	 * Make 'copy' a  copy of 'model'  - having all its EAttributes with the same values,
	 * and all its EReferences having target classes made by recursive descent, only if
	 * they do not exist in the copy package already. 
	 * All References to data type classes are copied as EReferences to the appropriate class
	 * in the copy data type package. Copy all annotations.
	 * Inherited EAttributes and EReferences get put onto the copy directly,
	 * rather than being inherited - so this copy does not preserve the inheritance hierarchy
	 */
	private void plainCopy(EClass model, EReference copyParentRef,EClass copy)
	throws MapperException
	{
		
		/* for data type classes in the unconstrained model, set the copy 
		 * to be the class with the same name in the copy data types package. */
		if (isDataTypeClass(model))
		{
			copy = getDataTypeClass(model.getName(),copyDataTypePackage);
			copyParentRef.setEType(copy);
			return;
		}

		// trace("Plain copy of class " + model.getName());

		/* make the new copy class a shallow copy of the model class, with empty 
		 * target classes for the containment EReferences, no yet in the package. */
		shallowCopy(model, copy);
		
		/* make up the target classes by recursive descent, unless they can be
		 * found already in the copy package.  */
		for (Iterator<EReference> ir = copy.getEAllReferences().iterator();ir.hasNext();)
		{
			EReference copyRef = ir.next();
			if (copyRef.isContainment())
			{
				// find the EReference of the same name in the model
				for (Iterator<EReference> is = model.getEAllReferences().iterator();is.hasNext();)
				{
					EReference modelRef = is.next();
					if (modelRef.getName().equals(copyRef.getName()))
					{
						EClass modelTarget = (EClass)modelRef.getEType();
						String targetClassName = modelTarget.getName();

						// if the target class already exists in the package, use that version
						EClassifier properTarget = copyRMIMPackage.getEClassifier(targetClassName);
						if (properTarget != null) copyRef.setEType(properTarget);

						/* if the target class has just been made by 
						 * shallowCopy and so does not exist in the package, 
						 * fill it out recursively and put it in the package */
						else
						{
							EClass target = (EClass)copyRef.getEType();
							if (target.getEPackage() == null) confirmInPackage(target);
							plainCopy(modelTarget,copyRef,target);								
						}
					}
				}				
			}
		}
	}
	
	/**
	 * @param model the model EClass which a copy is being made of
	 * @param copy an empty EClass 
	 * Make 'copy' a shallow copy of 'model'  - having all its EAttributes with the same values,
	 * and all its EReferences to RMIM classes with empty EClasses as targets. 
	 * All References to data type classes are copied as EReferences to the appropriate class
	 * in the copy data type package. Copy all annotations.
	 * Inherited EAttributes and EReferences get put onto the copy directly,
	 * rather than being inherited - so this copy does not preserve the inheritance hierarchy
	 */
	private void shallowCopy(EClass model, EClass copy) throws MapperException
	{
		/* do NOT set the name of the copy class from the name of the model class; 
		 * sometimes it has been changed. */

		ModelUtil.copyMifAnnotations(model, copy);
		
		// in case the copy has been shallow copied before
		copy.getEStructuralFeatures().clear();
		
		for (Iterator<EAttribute> ia = model.getEAllAttributes().iterator();ia.hasNext();)
			copy.getEStructuralFeatures().add(copyAttribute(ia.next()));
		
		for (Iterator<EReference> ir = model.getEAllReferences().iterator(); ir.hasNext();)
		{
			EReference ref = ir.next();
			// copy the EReference, make and name the target class (do not put it in the package)
			EReference copyRef = copyReference(ref);
			copy.getEStructuralFeatures().add(copyRef);
		}
	}
	
	/**
	 * @param modelRef
	 * @return a copy of the EReference, with as its target EClass:
	 * (a) the named class in the copy RMIM package, if it exists there already
	 * (b) otherwise, a new empty class not in the RMIM package
	 */
	private EReference copyReference(EReference modelRef)
	throws MapperException
	{
		EReference copy = EcoreFactory.eINSTANCE.createEReference();
		copy.setName(modelRef.getName());
		copy.setLowerBound(modelRef.getLowerBound());
		copy.setUpperBound(modelRef.getUpperBound());
		copy.setContainment(modelRef.isContainment());
		EClass modelInner = (EClass)modelRef.getEType();
		if (modelInner == null) throw new MapperException("Found no target class for association '" 
				+ modelRef.getName() + "'");

		EClass target = null;
		/* for EReferences to data type classes, make the reference
		 * point to the appropriate data type class in the new data type package*/ 
		String classType = " data type ";
		if (isDataTypeClass(modelInner))
			target = getDataTypeClass(modelInner.getName(),copyDataTypePackage);			
		else 
		{
			classType = " RMIM ";
			target = findOrMake(modelInner.getName());  // set only the name of the target class
		}
		if (target == null) throw new MapperException("Failed to make copy of" + classType + "class '" 
				+ modelInner.getName() + "' reached by association '" + modelRef.getName() + "'" );
		copy.setEType(target);
		
		
		ModelUtil.copyMifAnnotations(modelRef, copy);
		return copy;
	}
	
	/**
	 * @param modelAtt
	 * @return a copy of the EAttribute
	 */
	private EAttribute copyAttribute(EAttribute modelAtt)
	{
		EAttribute copy = EcoreFactory.eINSTANCE.createEAttribute();
		copy.setName(modelAtt.getName());
		copy.setLowerBound(modelAtt.getLowerBound());
		copy.setEType(modelAtt.getEType());
		copy.setDefaultValue(modelAtt.getDefaultValue());
		ModelUtil.copyMifAnnotations(modelAtt, copy);
		return copy;
	}
	
	/**
	 * @param className a class name
	 * @return a class with that name - found in the copy RMIM package,
	 * or newly made if not found
	 */
	private EClass findOrMake(String className)
	{
		EClass newClass = (EClass)copyRMIMPackage.getEClassifier(className);
		if (newClass != null) return newClass;
		newClass = EcoreFactory.eINSTANCE.createEClass();
		newClass.setName(className);
		return newClass;
	}
	
	/**
	 * 
	 * @param className
	 * @param datatypePackage
	 * @return the named class in the data type package
	 */
	private EClass getDataTypeClass(String className,EPackage datatypePackage) throws MapperException
	{
		EClass theClass = null;
		for (Iterator<EClassifier> it = datatypePackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			if ((next instanceof EClass) && (next.getName().equals(className))) 
				theClass = (EClass)next;
		}
		if (theClass == null) throw new MapperException("Cannot find data type class '" + className + "'");
		return theClass;
	}
	
	
	/**
	 * 
	 * @param modelClass a class in the unconstrained CDA model
	 * @return true if it is a data types class
	 */
	private boolean isDataTypeClass(EClass modelClass)
	{
		return (modelClass.getEPackage().getName().equals(RMIMReader.DATATYPE_PACKAGE_NAME));
	}


	
	/**
	 * @return the top ClinicalDocument class of the CDA RMIM
	 */
	public EClass getEntryClass()
	{
		return ((ConcreteClass)(rmimReader.topRMIM()).getEntryV3Name()).eClass();		
	}
	
	//-------------------------------------------------------------------------------------------------
	//                               Handling templates and contexts
	//-------------------------------------------------------------------------------------------------
	
	/**
	 * @param context template context, which has template ids attached to different steps
	 * @param isMaxCardinality: if true, this method refers to max cardinality;
	 * if false, it refers to min cardinality
	 * @return -1 if no template in the context constrains the cardinality of the 
	 * end node.
	 * If some rules in some templates constrain the min or max cardinality of the end node 
	 * (as defined by isMaxCardinality) to be 1, return the smallest step number from which the
	 * cardinality is constrained to 1. 
	 */
	private int cardinalityConstrainedFromStep(TemplatedPath context, boolean isMaxCardinality)
	{
		int constrainingStep = 100;
		for (Iterator<TemplateRule> ir = applicableRules(context); ir.hasNext();)
		{
			TemplateRule rule = ir.next();
			int ruleStep = rule.getContextStep();
			for (Iterator<TemplateAssertion> it = rule.constrainingAssertions().iterator();it.hasNext();)
			{
				TemplateAssertion ta = it.next();
				if ((isMaxCardinality) && (ta.testNode().finalNodeMustBeSingle(context, ruleStep)))
					constrainingStep = ruleStep;
				else if ((!isMaxCardinality) && (ta.testNode().finalNodeMustExist(context, ruleStep)))
					constrainingStep = ruleStep;
			}
		}
		if (constrainingStep == 100) constrainingStep = -1; // no constraints were found
		return constrainingStep;
	}
	
	/**
	 * @param context a template context (= path from the CDA root, with constraints)
	 * @return true if there are any templates on nodes which are descendants 
	 * of the leaf node of the context (not the leaf node itself)
	 */
	private boolean moreTemplatesInSubtree(TemplatedPath context) throws MapperException
	{
		boolean some = false;
		for (Iterator<CDATemplate> it = allTemplates(); it.hasNext();)
		{
			CDATemplate template = it.next();
			for (Iterator<TemplatedPath> ic = template.extendingContexts(context).iterator();ic.hasNext();)
				if (ic.next().length() > context.length()) some = true;
		}
		return some;
	}
	
	/**
	 * @param context a template context
	 * @return all templates compatible with the contexts. 
	 * The outer Vector is a Vector of mutually exclusive sets of templates.
	 * Templates in one of the inner Vectors can co-exist on the same node.
	 * (Two templates cannot co-exist on the same node unless one of them 
	 * is declared in the template usage file to constrain the other)
	 */
	private Vector<Vector<CDATemplate>> templateSetsForContext(TemplatedPath context)  throws MapperException
	{
		// find all templates for the context, irrespective of which ones can coexist on one node
		Vector<CDATemplate> ungrouped = new Vector<CDATemplate>();	
		for (Iterator<CDATemplate> it = allTemplates(); it.hasNext();)
		{
			CDATemplate temp = it.next();
			if (temp.isCompatibleWithContext(context)) ungrouped.add(temp);
		}
		
		// group the templates into sets that can co-exist on the same node
		if (ungrouped.size() == 0) return new Vector<Vector<CDATemplate>>();
		return groupTemplates(ungrouped);
	}
	
	/**
	 * For the top context (ClinicalDocument node) all templates which are allowed
	 * by the template usage file must appear; so there is only one group of 
	 * mutually compatible templates
	 * @param context
	 * @return
	 * @throws MapperException
	 */
	private Vector<CDATemplate> templatesForTopContext(TemplatedPath context)  throws MapperException
	{
		// find all templates for the context, irrespective of which ones can coexist on one node
		Vector<CDATemplate> ungrouped = new Vector<CDATemplate>();	
		for (Iterator<CDATemplate> it = allTemplates(); it.hasNext();)
		{
			CDATemplate temp = it.next();
			if (temp.isCompatibleWithContext(context)) ungrouped.add(temp);
		}
		return ungrouped;
	}

		
	/**
	 * @param ungrouped a flat Vector of templates
	 * @return Vector of groups of templates that can coexist on one node
	 */
	private Vector<Vector<CDATemplate>> groupTemplates(Vector<CDATemplate> ungrouped)  throws MapperException
	{
		//trace("Grouping " + ungrouped.size());
		Vector<Vector<CDATemplate>> grouped = new Vector<Vector<CDATemplate>>();
		if (ungrouped.size() > 10) return groupSectionTemplates(ungrouped);
		else
		{
			// subsets becomes 2**N
			int subsets = 1;
			for (int i = 0; i < ungrouped.size(); i++) subsets = 2*subsets;
			
			// j ranges from 1 to 2**N - 1
			for (int j=1; j < subsets; j++)
			{
				Vector<CDATemplate> testSet = makeSubset(j, ungrouped);
				if (isViable(testSet)) grouped.add(testSet);							
			}
		}
		//trace("Groups found: " + grouped.size());
		return grouped;	
	}
	
	/**
	 * 
	 * @param j an integer ranging from 1 to 2**N - 1; it has  N bits of 0 or 1
	 * @param ungrouped a Vector of N templates
	 * @return a subset of the Vector, using the bits of j to select templates
	 */
	private Vector<CDATemplate>  makeSubset(int j, Vector<CDATemplate> ungrouped)
	{
		Vector<CDATemplate> subset = new Vector<CDATemplate>();
		int index = j;
		for (int i = 0; i < ungrouped.size(); i++)
		{
			int rounded = (index/2)*2;
			if (index == rounded + 1) subset.add(ungrouped.get(i));
			index = index/2;
		}
		return subset;
	}
	
	/**
	 * @param testSet
	 * @return true if the test set of templates is viable. This means that 
	 * for every template in the set:
	 * (a) it is compatible with every other template in the set
	 * (b) every other template that it constrains is in the set
	 */
	private boolean isViable(Vector<CDATemplate> testSet)
	throws MapperException
	{
		for (int i = 0; i < testSet.size(); i++)
		{
			CDATemplate temp = testSet.get(i);
			if (!hasAllConstrained(temp,testSet)) return false;
			for (Iterator<CDATemplate> iu = testSet.iterator();iu.hasNext();)
				if (!temp.canBeOnSameNodeAs(iu.next())) return false;
		}
		return true;
	}
	
	/**
	 * @param temp
	 * @param testSet
	 * @return true if the test set contains all the templates that temp constrains
	 */
	private boolean hasAllConstrained(CDATemplate temp, Vector<CDATemplate> testSet)
	throws MapperException
	{
		for (Iterator<CDATemplate> it = temp.constrainedTemplates().iterator();it.hasNext();)
		{
			CDATemplate consTemp = it.next();
			boolean found = false;
			for (Iterator<CDATemplate> iu = testSet.iterator();iu.hasNext();)
				if (iu.next().fullTemplateId().equals(consTemp.fullTemplateId())) found = true;
			if (!found) return false;
		}
		return true;
	}

	
	/**
	 * @param ungrouped the set of all section templates
	 * @return all groups of section templates that can coexist on the same node
	 * Each group consists only of one template and all the others it constrains,
	 * directly or indirectly
	 * @throws MapperException
	 */
	private Vector<Vector<CDATemplate>> groupSectionTemplates(Vector<CDATemplate> ungrouped)  throws MapperException
	{
		Vector<Vector<CDATemplate>> groups = new Vector<Vector<CDATemplate>>();
		for (Iterator<CDATemplate> it = ungrouped.iterator(); it.hasNext();)
		{
			CDATemplate temp = it.next();
			if (hasAllConstrained(temp, ungrouped))
			{
				Vector<CDATemplate> group = new Vector<CDATemplate>();
				group.add(temp);
				for (Iterator<CDATemplate> iu = temp.constrainedTemplates().iterator();iu.hasNext();)
					group.add(iu.next());
				groups.add(group);
			}
		}
		return groups;		
	}

	
	/**
	 * @param temp
	 * @param soFar
	 * @return true of the template temp is anywhere in the Vector of Vectors of templates
	 */
	@SuppressWarnings("unused")
	private boolean alreadyFound(CDATemplate temp, Vector<Vector<CDATemplate>> soFar)
	{
		String id = temp.fullTemplateId();
		boolean found = false;
		for (int outer = 0; outer < soFar.size(); outer++)
		{
			Vector<CDATemplate> set = soFar.get(outer);
			for (int inner = 0; inner < set.size(); inner++)
				if (id.equals(set.get(inner).fullTemplateId())) found = true;
		}
		return found;
	}


	//---------------------------------------------------------------------------------------------------
	//                                         Trivia
	//---------------------------------------------------------------------------------------------------

	/**
	 * write out all templates, with the short forms of their contexts
	 */
	@SuppressWarnings("unused")
	private void writeAllTemplates() throws MapperException
	{
		System.out.println("All Templates");
		for (Iterator<CDATemplate> it = allTemplates();it.hasNext();)
		{
			CDATemplate temp = it.next();
			System.out.println("Template " + temp.fullTemplateId());
			for (Iterator<TemplatedPath> ic = temp.allContexts().iterator();ic.hasNext();)
			{
				TemplatedPath con = ic.next();
				System.out.println(con.shortStringForm());
			}
		}
	}
	
	private void trace(String s) {if (tracing()) System.out.println(s);}
	



}
