package com.openMap1.mapper.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem; 
import org.eclipse.jface.action.MenuManager;


import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.structures.MappableAssociation;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.ObjMapping;

/**
 * Class to create mapper editor menu actions depending on the model class
 * selected in the mapped class model view, and possibly on other criteria.
 * 
 * @author robert
 *
 */
public class AddMapperEditorActions {

    private MapperEditor mapperEditor;
    
    private MappedStructure mappedStructure;
    
    private List<IAction> actionsForModelObject;
	public List<IAction> actionsForModelObject() {return actionsForModelObject;}

    private List<IContributionItem> subMenusForModelObject;
	public List<IContributionItem> subMenusForModelObject() {return subMenusForModelObject;}
	
	public AddMapperEditorActions(MapperEditor mapperEditor) {
		this.mapperEditor = mapperEditor;
		Object thing = mapperEditor.getEditingDomain().getResourceSet().getResources().get(0).getContents().get(0);
		if (thing instanceof MappedStructure) 
		{
			mappedStructure = (MappedStructure)thing;
		}
		else System.out.println("Not editing a mapped structure");
	}
	
	/**
	 * Create menu actions depending on the model class
	 * selected in the mapped class model view, and possibly on other criteria.
	 * @param domain the mapper editor editing domain
	 * @param object the object selected in the mapper editor
	 */
	public void createActionsForModelObject(EditingDomain domain, Object object, Object selectedFromClassModelView)
	{
		actionsForModelObject = new ArrayList<IAction>();
		subMenusForModelObject = new ArrayList<IContributionItem>();
		
		if (object instanceof ElementDef)
		{
			ElementDef el = (ElementDef)object;
			createActionsForElement(domain, el, selectedFromClassModelView);
		}

		else if (object instanceof AttributeDef)
		{
			AttributeDef at = (AttributeDef)object;
			createActionsForAttribute(domain, at, selectedFromClassModelView);
		}
		
		else if (object instanceof ObjMapping)
		{
			ObjMapping om = (ObjMapping)object;
			createActionsForMapping(om);
		}

	}
	
	/**
	 * Actions when an Element node is selected in the mapped structure tree
	 * @param domain the mapper editor editing domain
	 * @param el the Element node selected
	 * @param fromClassModel the object currently selected in the mapped class model view
	 */
	private void createActionsForElement(EditingDomain domain, ElementDef el, Object fromClassModel)
	{
		/* for any element of a complex type, 
		 * which has not yet been expanded in the tree, add actions to expand it, 
		 * or to create a new mapping set,  or to link to an existing one. */
		if ((el.getType() != null) && (!el.getType().equals("")) 
				&& (attachedStructure() != null))
		 {
			if (!el.isExpanded()) actionsForModelObject.add(new ExtendElementTreeAction(domain,el,attachedStructure()));
			actionsForModelObject.add(new CreateNewMappingSetForTypeAction(mapperEditor,domain,el,attachedStructure()));
			actionsForModelObject.add(new ImportMappingSetForTypeAction(mapperEditor,domain,el));
		 }
		
		/* If the ElementDef has any child ElementDefs of unbounded max multiplicity,
		 * add an option to add an AttributeDef with name ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE 
		 * to all its child elements */
		if (hasUnboundedChildElements(el))
		{
			actionsForModelObject.add(new AddVirtualPositionAttributeAction(mapperEditor,domain,el));			
		}
		
		EClass selectedClass = extractEClassFrom(fromClassModel);
		
		// if a Class has been selected in the Class Model View, add the necessary mapping actions
		if (selectedClass != null)
		{
			// always add an action to map to the class
			String qualifiedName = ModelUtil.getQualifiedClassName(selectedClass);
			String nextSubset = nextSubset(mappingRoot(el),qualifiedName);
			actionsForModelObject.add(new MakeObjectMappingAction(domain, el, selectedClass.getName(),
					selectedClass.getEPackage().getName(),nextSubset,""));
			
			// for V3 Mappings to an RMIM class model, add a chain mapping menu item
			if (fromClassModel instanceof LabelledEClass)
			   makeChainMappingAction(domain,el,(LabelledEClass)fromClassModel);

			/* add Property and Association Mapping actions 
			only if there is already an object mapping to the selected class */
			addPropertyMappingActions(domain, el, selectedClass, fromClassModel);
			addAssociationMappingActions(domain, el, selectedClass,fromClassModel);
			
			// add an auto-mapping sub-menu
			addAutoMappingMenu(domain, el, selectedClass,fromClassModel);
		}
	}
	
	/**
	 * @param el an ElementDef
	 * @return true if it has any child ElementDefs of unbounded max multiplicity,
	 * or if it has more than one child ElementDef
	 */
	private boolean hasUnboundedChildElements(ElementDef el)
	{
		boolean unbounded = false;
		int children = 0;
		for (Iterator<ElementDef> it = el.getChildElements().iterator();it.hasNext();)
		{
			ElementDef child = it.next();
			if (child.getMaxMultiplicity() == MaxMult.UNBOUNDED) unbounded = true;
			children++;
		}
		if (children > 1) unbounded = true;
		return unbounded;
	}
	
	/**
	 * @param fromClassModel The object selected in the class model view is either an EClass (vanilla class model view)
	 * or a LabelledEClass (RMIM class model view)
	 * @return the EClass extracted from it, in either case
	 */
	private EClass extractEClassFrom(Object fromClassModel)
	{
		EClass selectedClass = null;
		if (fromClassModel != null)
		{
			if (fromClassModel instanceof EClass) 
				selectedClass = (EClass)fromClassModel;
			if (fromClassModel instanceof LabelledEClass) 
				selectedClass = ((LabelledEClass)fromClassModel).eClass();
		}
		return selectedClass;
	}
	
	
	/**
	 * Actions when an Attribute node is selected in the mapped structure tree
	 * @param domain the mapper editor editing domain
	 * @param at the Attribute node selected
	 * @param fromClassModel the object currently selected in the mapped class model view
	 */
	private void createActionsForAttribute(EditingDomain domain, AttributeDef at, Object fromClassModel)
	{
		EClass selectedClass = extractEClassFrom(fromClassModel);

		// if a Class has been selected in the Class Model View, add the necessary mapping actions
		if (selectedClass != null)
		{
			/* add Property and Association Mapping actions 
			only if there is already an object mapping to the selected class */
			addPropertyMappingActions(domain, at, selectedClass,fromClassModel);
			addAssociationMappingActions(domain, at, selectedClass,fromClassModel);

			// add an auto-mapping sub-menu
			addAutoMappingMenu(domain, at, selectedClass,fromClassModel);
		}
		
	}
	
	private MappedStructure mappingRoot(NodeDef nd)
	{
		return ModelUtil.getMappedStructure(nd);
	}
	
	//------------------------------------------------------------------------------------------------
	//                              Auto-Mapping (advance property mapping) Menu       
	//------------------------------------------------------------------------------------------------

	private void addAutoMappingMenu(EditingDomain domain, NodeDef nd, EClass selectedClass, Object fromClassModel)
	{
		MenuManager autoSubMenu = new MenuManager("Pre-map");
		subMenusForModelObject.add(autoSubMenu);

		// add menu items to pre-map any property of the class, or of a superclass
		Vector<String> allProperties = allPropertyNames(selectedClass,"");
		for (Iterator<String> it = allProperties.iterator(); it.hasNext();)
		{
			String classProp = it.next(); // of the form 'class:property'
			autoSubMenu.add(new MakePreMappingAction(domain, nd, classProp,selectedClass,fromClassModel,true));
		}

		// add a menu item to pre-map the class
		String className = selectedClass.getName();
		autoSubMenu.add(new MakePreMappingAction(domain, nd,className,selectedClass,fromClassModel,false));
		
	}

	
	//------------------------------------------------------------------------------------------------
	//                            Property Mapping sub-menu                            
	//------------------------------------------------------------------------------------------------

	/**
	 * If there are any object mappings to the class selected in the mapped class model view,
	 * make actions to add a property mapping to that class on the selected structure node
	 * @param domain
	 * @param nd
	 * @param selectedClass
	 */
	private void addPropertyMappingActions(EditingDomain domain, NodeDef nd, EClass selectedClass, Object fromClassModel)
	{
		Vector<String> subsets = getMappedSubsets(mappingRoot(nd), fromClassModel);
		if (subsets.size() > 0)
		{
			MenuManager propSubMenu = new MenuManager("Map Property");
			subMenusForModelObject.add(propSubMenu);
			for (Iterator<String> is = subsets.iterator();is.hasNext();)
			{
				String subset = is.next();
				Vector<String> allProperties = allPropertyNames(selectedClass,subset);
				addPseudoPropertyNames(selectedClass,nd,allProperties,subset);
				for (Iterator<String> it = allProperties.iterator(); it.hasNext();)
				{
					String classProp = it.next(); // of the form 'class:property'
					propSubMenu.add(new MakePropertyMappingAction(domain, nd, classProp,selectedClass,subset,""));
				}
			}
		}
		
	}
	
	/**
	 * If there are any object mappings to the class selected in the mapped class model view,
	 * make actions to add any allowed association mapping to that class on the selected structure node
	 * @param domain
	 * @param nd
	 * @param selectedClass
	 */
	private void addAssociationMappingActions(EditingDomain domain, NodeDef nd, EClass selectedClass, Object fromClassModel)
	{
		Vector<String> subsets = getMappedSubsets(mappingRoot(nd), fromClassModel);
		if (subsets.size() > 0)
		{
			MenuManager assocSubMenu = new MenuManager("Map Association");
			subMenusForModelObject.add(assocSubMenu);
			//find existing object mappings with their subsets
			Hashtable <String,Vector<String>> theClassMappings = allClassMappings(mappingRoot(nd));
			for (Iterator<String> is = subsets.iterator();is.hasNext();)
			{
				String thisSubset = is.next();
				//find associations from the selected class to these classes
				Vector<MappableAssociation> mappableAssociations = getMappableAssociations(selectedClass,thisSubset, theClassMappings);
				for (Iterator<MappableAssociation> it = mappableAssociations.iterator(); it.hasNext();)
				{
					MappableAssociation am = it.next();					
					assocSubMenu.add(new MakeAssociationMappingAction(domain, nd, am,""));
				}				
			}
		}		
	}
	
	/**
	 * @param root root of the MappedStructure
	 * @param fromClassModel a node selected in the class model view; EClass or LabelledEClass
	 * @return the mapped subsets of the class in the mapping. If it s an RMIM view, 
	 * these are only those consistent with the position of the class in the RMIM
	 */
	private Vector<String> getMappedSubsets(EObject root, Object fromClassModel)
	{
		Vector<String> subsets = new Vector<String>();
		if (fromClassModel instanceof EClass)
			subsets = mappedSubsets(root, ModelUtil.getQualifiedClassName((EClass)fromClassModel));
		if (fromClassModel instanceof LabelledEClass)
			subsets = restrictedMappedSubsets(root,(LabelledEClass)fromClassModel);
		return subsets;
	}

	
	/**
	 * @param root the MappedStructure root of a mapping set
	 * @param qualifiedClassName a class name preceded by its package name, if nonempty
	 * @return a Vector of all mapped subsets; an empty Vector if there are no mappings
	 */
	public static Vector<String> mappedSubsets(EObject root, String qualifiedClassName)
	{
		Vector<String> subsets = new Vector<String>();
		if (allClassMappings(root).get(qualifiedClassName) != null)
			subsets = allClassMappings(root).get(qualifiedClassName);
		return subsets;
	}
	
	/**
	 * @param root the MappedStructure root of a mapping set
	 * @param qualifiedClassName a class name preceded by its package name, if nonempty
	 * @return the subset name to use for the next object mapping to that class,
	 * avoiding all clashes with subset names already used
	 */
	public static String nextSubset(EObject root, String qualifiedClassName)
	{
		String subset = "";
		Vector<String> subsets = mappedSubsets(root,qualifiedClassName);
		if (subsets.size() > 0)
		{
			boolean clash = true;
			int index = 1;
			while (clash)
			{
				subset = "s" + index;
				clash = GenUtil.inVector(subset, subsets);
				index++;
			}
		}
		return subset;
	}
	
	
	/**
	 * Find all class mappings in a subtree of the mapped structure tree
	 * @param EObject root of the subtree
	 * @return Hashtable<String,Vector<String>> for each qualified class name, a Vector of its mapped subsets,
	 * or null if there are no mappings
	 */
	public static Hashtable<String,Vector<String>> allClassMappings(EObject root)
	{
		Hashtable<String,Vector<String>> addClassMappings = new Hashtable<String,Vector<String>>();
		addClassMappings(addClassMappings,root);
		return addClassMappings;
	}
		
	/**
	 * recursive descent of a model tree, finding all mapped subsets 
	 * of all classes
	 * @param classMappings
	 * @param node
	 */
	public static  void addClassMappings(Hashtable<String,Vector<String>> classMappings, EObject node)
	{
	    if (node instanceof ObjMapping)
	    {
	    	ObjMapping om = (ObjMapping)node;
	    	String qClassName = om.getQualifiedClassName(); // package name first
	    	String subset = om.getSubset();
	    	Vector<String> subsetsMapped = classMappings.get(qClassName);
	    	if (subsetsMapped == null) subsetsMapped = new Vector<String>();
	    	subsetsMapped.addElement(subset);
	    	classMappings.put(qClassName, subsetsMapped);
	    }
	    for (Iterator<EObject> it = node.eContents().iterator(); it.hasNext();)
	    	addClassMappings(classMappings,it.next());
	}
	
	/**
	 * 
	 * @param selectedClass an ecore EClass object
	 * @return a Vector of 'className:propertyName' for the Class and all its superclasses
	 */
	private Vector<String> allPropertyNames(EClass selectedClass,String subset)
	{
		Vector<String> allProps = new Vector<String>();
		addPropertyNames(allProps,selectedClass,subset);
		return allProps;
	}
	
	/**
	 * Add to the Vector allProperties an entry 'className:pseudoPropertyName' for every
	 * pseudo-property of the class (regardless of the mapped subset)
	 * (Pseudo-properties in the mapped structure are converted to properties in the class
	 * model by property conversions)
	 * @param selectedClass
	 * @param nd a Node in the mapped structure
	 * @param allProperties the Vector that is to be extended with the pseudo-properties
	 */
	private void addPseudoPropertyNames(EClass selectedClass,NodeDef nd, Vector<String>allProperties, String subset)
	{
		String className = subClassName(selectedClass,subset);
		String qualifiedClassName = qualifiedSubClassName(selectedClass,subset);
		Hashtable<String,String> pseudoProps = ModelUtil.getPseudoProperties(qualifiedClassName,nd);
		for (Iterator<String> it = pseudoProps.keySet().iterator();it.hasNext();)
			{allProperties.add(className + ":" + it.next());}
	}

	
	/**
	 * add 'className:propertyName' for all properties of this class, recursively going
	 * to superclasses , so properties of this class come first on the list
	 * @param allProps
	 * @param c
	 */
	private void addPropertyNames(Vector<String> allProps,EClass c,String subset)
	{
		for (Iterator<EAttribute> it = c.getEAttributes().iterator();it.hasNext();)
			{allProps.addElement(subClassName(c,subset) + ":" + it.next().getName());}
		for (Iterator<EClass> iu = c.getESuperTypes().iterator();iu.hasNext();)
			{addPropertyNames(allProps,iu.next(),subset);}		
	}
	
	/**
	 * @param ec a class
	 * @param subset a mapped subset
	 * @return the qualfied class name , followed by the subset name in brackets if not empty
	 */
	private String qualifiedSubClassName(EClass ec, String subset)
	{
		String subName = ModelUtil.getQualifiedClassName(ec);
		if (!subset.equals("")) subName = subName + "(" + subset + ")";
		return subName;
	}

	/**
	 * @param ec a class
	 * @param subset a mapped subset
	 * @return the unqualified class name , followed by the subset name in brackets if not empty
	 */
	private String subClassName(EClass ec, String subset)
	{
		String subName = ec.getName();
		if (!subset.equals("")) subName = subName + "(" + subset + ")";
		return subName;
	}
	
	/**
	 * find all associations from the selected class (or one of its superclasses)
	 * to some other mapped class (or one of its superclasses)
	 * @param EClass the selectedClass
	 * @param Hashtable allClassMappings; the keys to this are the names of mapped classes
	 * @return
	 */
	private Vector<MappableAssociation> getMappableAssociations
		(EClass selectedClass, String thisEndSubset, Hashtable<String,Vector<String>> allClassMappings)
	{
		Vector<MappableAssociation> assocs = new Vector<MappableAssociation>();
		EClass originalClass = selectedClass;
		EClass endClass = selectedClass;
		addMappableAssociations(assocs, originalClass, endClass, thisEndSubset, allClassMappings);			
		return assocs;		
	}

	/**
	 * recurse up through superclasses finding associations, 
	 * but labelling them always with the original class
	 * @param assocs Vector of MappableAssociation objects being built up
	 * @param originalClass the original class selected in the mapped class model view
	 * @param endClass the original class or a superclass -end of the association
	 * @param allClassMappings supplies the names of all classes currently mapped
	 */
	private void addMappableAssociations(Vector<MappableAssociation> assocs, 
			EClass originalClass, EClass endClass, String thisEndSubset,
			Hashtable<String,Vector<String>>  allClassMappings)
	{
		// check all the associations of this class
		for (Iterator<EReference> it =  endClass.getEReferences().iterator();it.hasNext();)
		{
			EReference ref = it.next();
			/* pick out only those associations whose other end class (or some subclasses of it) 
			 * are mapped, and note the actual mapped subclasses */
			for (Iterator<EClassSubset> iw = mappedSubClasses(ref, allClassMappings).iterator();iw.hasNext();)
			{
				EClassSubset ecs = iw.next();
				MappableAssociation ma = new MappableAssociation(originalClass, thisEndSubset,ecs.eClass,ecs.subset, ref,false);
				assocs.addElement(ma);
			}
		}
			
		// repeat the checks for all superclasses of the selected class
		for (Iterator<EClass> it = endClass.getESuperTypes().iterator(); it.hasNext();)
			{addMappableAssociations(assocs,originalClass,it.next(),thisEndSubset, allClassMappings);}
	}
	
	
	/**
	 * if the other end class of the EReference (or any of its subclasses)
	 * are mapped classes, return a Vector the mapped classes; 
	 * otherwise return an empty Vector
	 * @param am
	 * @param allClassMappings
	 * @return
	 */
	private Vector<EClassSubset> mappedSubClasses(EReference am, Hashtable<String,Vector<String>> allClassMappings)
	{
		Vector<EClassSubset> result = new Vector<EClassSubset>();
		EClass endSuperClass = (EClass)am.getEType();
		// find all subclasses of the class at the other end of the association
		Vector<EClass> endSubClasses = new Vector<EClass>();
		try {endSubClasses = mappedStructure.getAllSubClasses(endSuperClass);}
		catch (Exception  ex) {}
		for (Iterator<EClass> it = endSubClasses.iterator();it.hasNext();)
		{
			EClass endSubClass = it.next();
			String qualifiedClassName = ModelUtil.getQualifiedClassName(endSubClass);
			Vector<String> subsets = allClassMappings.get(qualifiedClassName);
			// find all mapped subsets of each subclass
			if (subsets != null) for (Iterator<String> is = subsets.iterator();is.hasNext();)
			{
				String subset = is.next();
				result.add(new EClassSubset(endSubClass,subset));
			}
		}
		return result;
	}
	
	private class EClassSubset{
		EClass eClass;
		String subset;
		
		EClassSubset(EClass eClass,String subset)
		{
			this.eClass = eClass;
			this.subset = subset;
		}
	}

	
	/**
	 * 
	 * @return the Structure definition attached to the current mapping set
	 */
	private StructureDefinition attachedStructure() 
	{ 
		StructureDefinition res = null;
		try
		{
			res =  WorkBenchUtil.mappingRoot(mapperEditor).getStructureDefinition();			
		}
		catch (MapperException  ex) {}
		return res;
	}
	
	//-----------------------------------------------------------------------------------------------
	//                      Chain Mapping Menu Item for V3 RMIM mappings 
	//-----------------------------------------------------------------------------------------------
	
	 private void makeChainMappingAction(EditingDomain domain,ElementDef el,LabelledEClass selected)
	 {
		 EObject root = mappingRoot(el);
		 Vector<String> subsets = getMappedSubsets(root, selected);
		 String ancestorMappedClass = null;
		 String ancestorMappedSubset = null;
		 /* You can make chain mappings even if the class is already mapped, because you may
		  * want to make repeated mappings to data type classes like II */
		 {
			 boolean classIsMapped = false;
			 LabelledEClass current  = selected;

			 // Iterate up through parent classes until you find one that is mapped
			 while ((current != null)  && (!classIsMapped))
			 {
				 // find the subset to use when mapping this class
				 String className = ModelUtil.getQualifiedClassName(current.eClass());
				 String subsetToMap = nextSubset(root,className);
				 current.setSubsetToMap(subsetToMap);

				 // find the next parent class and check if it exists and is mapped
				 current = current.parent();
				 if (current != null)
				 {
					 subsets = getMappedSubsets(mappingRoot(el), current);
					 if (subsets.size() > 0) // found the first ancestor class already mapped
					 {
						 classIsMapped = true;
						 // record the mapped class and subset, for use in the top association mapping
						 ancestorMappedClass = ModelUtil.getQualifiedClassName(current.eClass());
						 ancestorMappedSubset = subsets.get(0);						 
					 }
				 }
			 } // end of iteration over ancestor classes
			 
			 if (ancestorMappedSubset != null)
				 actionsForModelObject.add(new MakeChainMappingAction(domain,el,ancestorMappedClass,ancestorMappedSubset,selected));
		 }
	 }

		
		/**
		 * @param root the MappedStructure root of a mapping set
		 * @param selected a node selected in the RMIM class model view
		 * @return the restricted list of mapped subsets for the selected class, 
		 * which are also consistent with its position in the RMIM tree.
		 * 
		 * The restricted list is generally expected to have length 1.
		 * 
		 * If there is one or more mapped subset, but the selected RMIM class does not have a
		 * chain of association mappings linking it to ancestor classes, then the result is an
		 * empty Vector. (an object mapping on its own does not define which instance of an RMIM
		 * class, eg which instance of a data type class, it refers to)
		 */
		private Vector<String> restrictedMappedSubsets(EObject root, LabelledEClass selected)
		{
			Vector<String> filteredSubsets = null;
			// get the long list of mapped subsets, restricted by the qualified class name
			Vector<String> subsets = mappedSubsets(root,ModelUtil.getQualifiedClassName(selected.eClass()));
			// Only try to restrict the list if there is more than one subset
			if ((subsets != null) && (subsets.size() == 1)) return subsets;
			if ((subsets != null) && (subsets.size() > 1))
			{
				String assocName = selected.associationName();
				LabelledEClass parent = selected.parent();
				// The top class of the RMIM has no parent class, so its subsets cannot be filtered
				if (parent != null)
				{
					filteredSubsets = new Vector<String>();
					// find the restricted mapped subsets of the parent
					Vector<String> parentSubsets = restrictedMappedSubsets(root, parent);
					// and the parent class must have one or more object mappings
					if ((parentSubsets != null) && (parentSubsets.size() > 0))
					{
						// try out all mapped subsets in the long list
						for (Iterator<String> is = subsets.iterator();is.hasNext();)
						{
							String subset = is.next();
							// for each subset, try out all mapped subsets of the parent (but there should be only 1) 
							for (Iterator<String> it = parentSubsets.iterator();it.hasNext();)
							{
								String pSubset = it.next();
								// retain the subset only if the association mapping exists
								if (mappedAssociationExists(root, parent.eClass(),pSubset,selected.eClass(),subset,assocName))
									filteredSubsets.add(subset);					
							}
						}
					}
				}
			}
			if (filteredSubsets != null) subsets = filteredSubsets;
			return subsets;
		}
		
		/**
		 * @param root MappedStructure root
		 * @param parent parent class
		 * @param parentSubset mapped subset of parent class
		 * @param selected class selected in the class model view
		 * @param subset a possible subset of the selected class
		 * @param assocName role name to go from the parent class to the selected class
		 * @return true if the association mapping exits with all these parameters
		 */
		private boolean mappedAssociationExists(EObject root, EClass parent,String parentSubset,
				EClass selected,String subset,String assocName)
		{
			boolean exists = false;
			List<EObject> assocMappings = ModelUtil.getEObjectsUnder(root, MapperPackage.eINSTANCE.getAssocMapping());
			for (Iterator<EObject> it = assocMappings.iterator();it.hasNext();)
			{
				AssocMapping am = (AssocMapping)it.next();
				AssocEndMapping navigable  = am.getMappedEnd2();
				AssocEndMapping other = am.getMappedEnd1();
				if ((navigable.getQualifiedClassName().equals(ModelUtil.getQualifiedClassName(selected))) &&
					(navigable.getSubset().equals(subset)) &&
					(navigable.getMappedRole().equals(assocName)) &&
					(other.getQualifiedClassName().equals(ModelUtil.getQualifiedClassName(parent))) &&
					(other.getSubset().equals(parentSubset)))
						exists = true;
			}
			return exists;
		}
		
		//----------------------------------------------------------------------------------
		//    Expanding the class model tree to show the class of the selected mapping
		//----------------------------------------------------------------------------------
		

		private void createActionsForMapping(ObjMapping om)
		{
			actionsForModelObject.add(new ShowMappedClassAction(om));			
		}

}
