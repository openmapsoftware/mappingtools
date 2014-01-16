package com.openMap1.mapper.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.openMap1.mapper.actions.MakeITSMappingsAction;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.mapping.AssociationMapping;
import com.openMap1.mapper.mapping.objectMapping;
import com.openMap1.mapper.query.DataSource;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.XOReader;
import com.openMap1.mapper.structures.ITSAssociation;
import com.openMap1.mapper.structures.ITSAttribute;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.NodeDef;

/**
 * Class denoting an EClass in the RMIM view and the other information 
 * needed to construct the RMIM Tree View.
 * This information is:
 * - its parent LabelledEClass
 * - the association that reached it.
 * 
 * The class is also used as a wrapper for EClasses more generally - 
 * first to avoid duplicating code that has a lot in common for
 * EClasses and LabelledEClass, and second to put any useful methods in the wrapper.
 * Uses a different constructor when not an RMIM class.
 */
public class LabelledEClass{
	
	/**
	 * @return the LabelledEClass that is parent to this one
	 * in the RMIM tree diagram
	 */
	public LabelledEClass parent() {return parent;}
	private LabelledEClass parent;
	
	// see cached implementation getChildren below
	private ArrayList<LabelledEClass> children;

	
	/**
	 * @return the name of the association that reached this node of the RMIM tree
	 * from its parent node
	 */
	public String associationName() {return associationName;}
	private String associationName;

	
	/**
	 * @return the EClass that this is a wrapper for
	 */
	public EClass eClass() {return eClass;}
	private EClass eClass;
	
	private String subsetToMap = null;
	
	/**
	 * @return the subset to use when making an object mapping to this class
	 */
	public String subsetToMap() {return subsetToMap;}
	
	public void setSubsetToMap(String subsetToMap) {this.subsetToMap = subsetToMap;}
	
	/**
	 * @return true if a mapping to this class is about to be made
	 * (i.e if subsetToMap has been set to a non-null value)
	 */
	public boolean makeMapping() {return (subsetToMap != null);}
	
	public boolean isRMIMClass() {return isRMIMClass;}
	private boolean isRMIMClass;
	
	public boolean isMapped() {return (!objectMappingText.equals(""));}
	
	/**
	 * @return text for the class model view describing the location of the object mapping
	 */
	public String getObjectMappingText() {return objectMappingText;}
	private String objectMappingText = "";
	public void setObjectMappingText(String text) {objectMappingText = text;}
	
	/**
	 * @return the subset that has been used in an object mapping for this EClass
	 */
	public String getMappedSubset() {return mappedSubset;}
	private String mappedSubset = null;
	public void setMappedSubset(String subset) {mappedSubset = subset;}

	
	//---------------------------------------------------------------------------------------
	//                                     constructor
	//---------------------------------------------------------------------------------------
	
	/**
	 * Constructor for RMIM classes, which have a position in an RMIM tree and a parent class
	 * @param eClass the EClass object of which this is a wrapper
	 * @param associationName the association that reached it
	 * @param parent the parent node in the RMIM tree
	 */
	public LabelledEClass(EClass eClass,String associationName,LabelledEClass parent)
	{
		this.eClass = eClass;
		this.associationName = associationName;
		this.parent = parent;
		isRMIMClass = true;
	}
	
	/**
	 * Constructor for use as a wrapper for any EClass
	 * @param eClass
	 */
	public LabelledEClass(EClass eClass)
	{
		this.eClass = eClass;
		this.associationName = null;
		this.parent = null;
		isRMIMClass = false;
	}
	
	
	/**
	 * @return the child LabelledEClass objects of this node
	 * cached implementation so it does not carry on creating new sets of children
	 * change 5/13 RPW so that any node reached by a non-containment relation has no child nodes
	 */
	public ArrayList<LabelledEClass> getChildren()
	{
		if (children == null)
		{
			children = new ArrayList<LabelledEClass>();
			boolean showsSomeChildren = true;
			if (parent() != null)
			{
				EStructuralFeature thisRef = parent().eClass().getEStructuralFeature(associationName);
				showsSomeChildren =  ((EReference)thisRef).isContainment();
			}
			if (showsSomeChildren) for (Iterator<EReference> it = eClass.getEReferences().iterator();it.hasNext();)
			{
				EReference ref = it.next();
				String assocName = ref.getName();
				EClassifier ec = ref.getEType();
				
				if (ec instanceof EClass)
				{
					LabelledEClass child = new LabelledEClass((EClass)ec, assocName, this);
					children.add(child);
				}
			}			
		}
		return children;
	}
	
	/**
	 * @param refName
	 * @return the LabelledEClass child reached by the given association name,
	 * if there is one; null otherwise
	 */
	public LabelledEClass getNamedAssocChild(String refName)
	{
		LabelledEClass child = null;
		for (Iterator<LabelledEClass> il = getChildren().iterator();il.hasNext();)
		{
			LabelledEClass candidate = il.next();
			if (candidate.associationName.equals(refName)) child = candidate;
		}
		return child;
	}
	
	/**
	 * @param refName
	 * @param className
	 * @return the LabelledEClass child reached by the given association name, with the given class name,
	 * if there is one; null otherwise
	 */
	public LabelledEClass getNamedAssocAndClassChild(String refName, String className)
	{
		LabelledEClass child = null;
		for (Iterator<LabelledEClass> il = getChildren().iterator();il.hasNext();)
		{
			LabelledEClass candidate = il.next();
			if ((candidate.associationName.equals(refName)) && (candidate.eClass().getName().equals(className)))
				child = candidate;
		}
		return child;
	}
	
	/**
	 * 
	 * @return the EReference pointing from the parent eClass to this eClass
	 */
	public EReference getRefToThisClass()
	{
		EReference ref = null;
		if (parent() != null)
		{
			for (Iterator<EStructuralFeature> it = parent().eClass().getEStructuralFeatures().iterator();it.hasNext();)
			{
				EStructuralFeature next = it.next();
				if (next instanceof EReference)
				{
					EReference r = (EReference)next;
					if ((r.getName().equals(associationName)) && (r.getEType().getName().equals(eClass().getName()))) ref = r;
				}
			}
		}
		return ref;
	}
	
	
	/**
	 * @return true if the class is now used in the micro-ITS, either because it has an EAttribute
	 * which is marked as included on the EAnnotation for the path to this LabelledEClass,
	 * or because it has an EReference which is marked as used (because there are EAttributes
	 * which are included, somewhere below the EReference)
	 */
	public boolean isActuallyUsedInMicroITS()
	{
		boolean inITS = false;
		
		// check if any EAttribute is included in the micro-ITS
		for (Iterator<EAttribute> ia = eClass.getEAllAttributes().iterator();ia.hasNext();)
		{
			EAttribute ea = ia.next();
			String note = FeatureView.getMicroITSAnnotation(ea,getPath());
			if (note != null) try
			{
				ITSAttribute itsa = new ITSAttribute(note);
				if (itsa.isIncluded()) inITS = true;
			}
			catch (MapperException ex) {}

		}
		
		// check if any EReference is marked as used (not recursive)
		for (Iterator<EReference> it = eClass.getEReferences().iterator();it.hasNext();)
		{
			EReference ref = it.next();
			String note = FeatureView.getMicroITSAnnotation(ref,getPath());
			if (note != null) try
			{
				ITSAssociation itsa = new ITSAssociation(note);
				if (itsa.attsIncluded()) inITS = true;
			}
			catch (MapperException ex) {}
		}
		return inITS;
	}
	
	/**
	 * @return true if the class has been marked as used in the ITS, 
	 * on the annotation of its parent association for the correct path.
	 */
	public boolean isMarkedUsedInMicroITS()
	{
		boolean marked = false;
		if (parent() != null)
		{
			String parentPath = parent().getPath();
			// find the EReference from the parent with the correct name
			for (Iterator<EReference> ir = parent().eClass().getEReferences().iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				if (ref.getName().equals(associationName())) try
				{
					// find the EAnnotation with the correct path
					String note = FeatureView.getMicroITSAnnotation(ref,parentPath);
					if (note != null)
					{
						ITSAssociation itsa = new ITSAssociation(note);	
						marked = itsa.attsIncluded();
					}
				}
				catch (MapperException ex) {}				
			}
		}
		return marked;
	}
	
	/**
	 * mark the EReference leading to this EClass, with the appropriate path,
	 * as having descendant attributes included or not (if used is true or false)
	 * @param used
	 */
	public void markAsUsedInMicroITS(boolean used)
	{
		// If this is the top LabelledEClass, there is no EReference leading to it
		if (parent() != null)
		{
			String parentPath = parent().getPath();
			for (Iterator<EReference> ir = parent().eClass().getEReferences().iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				if (ref.getName().equals(associationName())) try
				{
					String note = FeatureView.getMicroITSAnnotation(ref,parentPath);
					if (used)
					{
						ITSAssociation itsa = new ITSAssociation();
						if (note != null) itsa = new ITSAssociation(note);	
						itsa.setAttsIncluded(true);
						FeatureView.addMicroITSAnnotation(ref, parentPath, itsa.stringForm());								
					}
					else if ((!used) && (note != null))
					{
						FeatureView.removeMicroITSAnnotation(ref, parentPath);
					}
				}
				catch (MapperException ex) {}				
			}
		}
	}
	
	/* for all ancestor EAssociations, set or unset the used flag.
	 * Ascend the tree until you find a flag that is already set correctly,
	 * or you reach the top of the tree */
	public void markWithAncestors(boolean used)
	{
		// mark the association from the parent leading to this class
		markAsUsedInMicroITS(used);

		// mark all ancestors until you come to one which is already correctly marked
		LabelledEClass current = parent();
		while (current != null)
		{
			boolean oldUsedState = current.isMarkedUsedInMicroITS();
			boolean newUsedState = current.isActuallyUsedInMicroITS();
			if (newUsedState != oldUsedState) // need to mark and ascend
			{
				current.markAsUsedInMicroITS(newUsedState);
				current = current.parent(); // null if current was top of the tree
			}
			else if (newUsedState == oldUsedState) current = null; // need go no further
		}
		
	}
	
	/**
	 * @return the path of association names to this EClass - with the class
	 * name of the top class at its start
	 */
	public String getPath()
	{
		if (associationName == null) return eClass.getName();
		if (parent() == null) return eClass.getName(); // not sure how this occurs when associationName != null, but it does
		return parent().getPath() + "/" + associationName;
	}
	
	/**
	 * 
	 * @param className
	 * @return a descendant LabelledEClass with a given name, or null if there is none
	 * Note this could fail if there is an ancestor with the same name as a descendant.
	 */
	public LabelledEClass getDescendant(String className)
	{
		if (className.equals(eClass().getName())) return this;
		// cut off infinite recursion, but crudely; this can cut it off if an ancestor name coincides
		if (nameOccurrences() > 1) return null;
		for (Iterator<LabelledEClass> it = getChildren().iterator(); it.hasNext();)
		{
			LabelledEClass child = it.next();
			if (child.getDescendant(className) != null) return child.getDescendant(className);
		}
		return null;
	}
	
	/**
	 * @param className
	 * @return the number of times a class with a given name occurs in the path from the root class to this class
	 */
	public int countOccurrences(String className)
	{
		int occs = 0;
		if (parent() != null) occs = parent().countOccurrences(className);
		if (eClass().getName().equals(className)) occs++;
		return occs;
	}
	
	/**
	 * @param className
	 * @return true if the path from the named ancestor class to this class consists only of 1..1 associations
	 */
	public boolean isOneToOneDescendant(String className)
	{
		if (className.equals(eClass().getName())) return true;
		if (parent() == null) return false;
		EReference fromParent = (EReference)parent().eClass().getEStructuralFeature(associationName);
		if (fromParent.getLowerBound() == 0) return false;
		if (fromParent.getUpperBound() == -1) return false;
		return parent().isOneToOneDescendant(className);
	}
	
	/**
	 * 
	 * @return the number of times this class name occurs in the path from the root class to this class
	 */
	public int nameOccurrences() {return countOccurrences(eClass().getName());}
	
	
	/**
	 * @return true if this EClass is a single child of its parent class
	 */
	public boolean isSingleChild()
	{
		if (parent() != null)
		{
			EReference ref = (EReference)parent().eClass().getEStructuralFeature(associationName());
			if ((ref != null) && (ref.getUpperBound() == 1)) return true;
		}
		return false;
	}
	
	//---------------------------------------------------------------------------------------------
	//                      methods applicable for  any wrapped EClass
	//---------------------------------------------------------------------------------------------
	
	 /**
	 * @param dataSource a data source, which defines a top-level XOReader.
	 * If this is an RMIM class, it is assumed that the top ancestor of this LabelledEClass is represented
	 * in the top  of the data source, and that this LabelledEClass
	 * is represented either in that XOReader or one that is imported by a chain of imports.
	 * 
	 * @return the XOReader which represents this class and its associations and properties
	 * (i.e if this class is represented on an importing node, it returns the imported );
	 * or return null if there is any problem.
	 */
	public XOReader getLocalReader(DataSource dataSource)  throws MapperException
	{
		XOReader reader = null;

		if (!(dataSource.getReader() instanceof MDLXOReader))
		{
			if (dataSource.getReader().representsObject(ModelUtil.getQualifiedClassName(this.eClass())))
				reader = dataSource.getReader();
		}
		
		else if (dataSource.getReader() instanceof MDLXOReader)
		{
			MDLXOReader mReader = (MDLXOReader)dataSource.getReader();
			
			/* RMIM classes; avoid going over all imported mapping sets by going back
			 * through ancestors in the RMIM tree, assuming that the last 
			 * ancestor is represented in the top mapping set */
			if (isRMIMClass())
			{
				/* the end of the recursion; return the reader that represents this class. For RMIM classes
				 * this is expected always to be the top reader, so it will not search through imported readers*/
				if (parent() == null)
				{
					if (mReader.representsObjectLocally(getQualifiedClassName())) 
						reader = dataSource.getReader();
				}
				/* recursive step; find the local reader of the parent object. If that represents this
				 * class on an importing node, return the imported XOReader */
				else
				{
					MDLXOReader parentReader = (MDLXOReader)parent().getLocalReader(dataSource);
					if ((parentReader != null) && (parentReader.representsObjectLocally(getQualifiedClassName())))
					{
						// find the Association Mappings with the correct parent class, role name and child class
						Vector<AssociationMapping> amv = parentReader.getAssociationRoleMappingsLocal
							(parent.getQualifiedClassName(), associationName, getQualifiedClassName());
						if (amv.size() > 0) try // should be at most 1
						{
							AssociationMapping am = amv.get(0);
							NodeDef nDef = am.mappedNode();
							/* if the node of the association mapping (which is also expected to be
							 * the node representing the child object) imports a mapping set,
							 * return the XOReader for the mapping set; otherwise return the XOReader of the parent */
							if (nDef instanceof ElementDef)
							{
								ImportMappingSet ims = ((ElementDef)nDef).getImportMappingSet();
								if (ims != null) reader = parentReader.getImportedReader(ims);
								else reader = parentReader;
							}
						}
						catch (MapperException ex) {GenUtil.surprise(ex,"getLocalReader");}
					}
				}			
			}
			/* non-RMIM classes; first find the top XOReader that represents the class, and then check 
			 * if the node representing the class imports another mapping set. If so, 
			 * return the imported mapping set */
			else if (!isRMIMClass())
			{
				// get the top reader representing this class locally - which may not be the imported reader you want.
				MDLXOReader topReader = (MDLXOReader)mReader.readerRepresentingObject(getQualifiedClassName());
				if (topReader != null)
				{
					reader = topReader;
					/* if any of the object mappings to the class in that mapping set are on a Node that imports, 
					 * return the importing mapping set rather than the importing one. */
					Vector<objectMapping> oMaps = topReader.objectMappingsByClassName(getQualifiedClassName());
					for (Iterator<objectMapping> it = oMaps.iterator(); it.hasNext();)
					{
						NodeDef nDef = it.next().mappedNode();
						if (nDef instanceof ElementDef) try
						{
							ImportMappingSet ims = ((ElementDef)nDef).getImportMappingSet();
							if (ims != null) reader = topReader.getImportedReader(ims);
						}
						catch (MapperException ex) {GenUtil.surprise(ex,"getLocalReader");}
					}
				}
			}
			
		}
		
		return reader;
	}
	
	
	public String getQualifiedClassName()
	{
		return ModelUtil.getQualifiedClassName(eClass());
	}
	
	public String getRIMorDataTypeClassName()
	{
		if (eClass().getEPackage().getName().equals("datatypes")) return eClass().getName();
		return ModelUtil.getMIFAnnotation(eClass(),"RIM Class");
	}
	
	/**
	 * @return true if this class has a fixed value on any attribute
	 */
	public boolean hasSomeFixedValue() throws MapperException
	{
		boolean hasFixedValue = false;
		for (Iterator<EStructuralFeature> it = this.eClass().getEStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature feat = it.next();
			if ((feat instanceof EAttribute) && (getAnnotatedFixedValue(feat.getName()) != null)) hasFixedValue = true;
		}
		return hasFixedValue;
	}
	
	/**
	 * get the fixed value of some attribute of the EClass, by comparing four values:
	 * (1) the value from a MIF annotation with key 'fixed value' on the EAttribute
	 * (2) the value from a MIF annotation like '<details key="constraint:code/@code" value="10160-0"/>'  on the eClass or an ancestor
	 * (3) the value from a MIF annotation with key 'fixed att value' on the EReference to the class
	 * (4) the value from an annotation like '<details key="fixed:ClinicalDocument/messageType" value="POCD_MT010011GB01"/>'
	 * with an annotation source like "urn:hl7-org:v3/microITS/cda.mapper" which depends on the mapping set.
	 * If (4) exists, it takes priority over (1) or (2) or (3).
	 * If (1) or (2) or (3) give different values, throw an Exception.
	 * @param attName
	 * @return the fixed value as above, or null if there is none
	 * @throws MapperException if there is no EAttribute of this name, or if values (1) and (2) clash
	 */
	public String getAnnotatedFixedValue(String attName) throws MapperException
	{
		String fixedValue = null;
		EStructuralFeature feat = eClass().getEStructuralFeature(attName);
		if ((feat != null) && (feat instanceof EAttribute))
		{
			EAttribute att = (EAttribute) feat;
			
			// get a MIF fixed value from the EAttribute
			String fixedVal1 = ModelUtil.getEAnnotationDetail(att, "fixed value");
			
			// get a MIF fixed value from the EClass or an ancestor EClass
			String path = "@" + attName;
			String fixedVal2 = getAncestorFixedValue(path);
			
			// get a MIF fixed value from the EReference (NHS MIF files only)
			String fixedVal3 = null;
			if (getRefToThisClass() != null)
			{
				String fv = ModelUtil.getEAnnotationDetail(getRefToThisClass(), "fixed att value"); // may be null - no problem
				String[][] classAtts = MakeITSMappingsAction.NHSFixedAttributes;
				for (int i = 0; i < classAtts.length;i++)
				{
					String[] classAtt = classAtts[i];
					if ((classAtt[0].equals(eClass.getName())) && (classAtt[1].equals(attName))) fixedVal3 = fv;
				}
			}
			
			checkEquals(fixedVal1,fixedVal2,attName);
			checkEquals(fixedVal1,fixedVal3,attName);
			checkEquals(fixedVal2,fixedVal3,attName);
					
			// take whichever of the three is not null
			if (fixedVal1 != null) fixedValue = fixedVal1;
			if (fixedVal2 != null) fixedValue = fixedVal2;
			if (fixedVal3 != null) fixedValue = fixedVal3;

			// specific fixed value requested for this path; takes precedence over the others
			String attKey = "fixed:" + getPath();
			String fixedVal4 = FeatureView.getMicroITSAnnotation(att, attKey);
			if (fixedVal4 != null) fixedValue = fixedVal4;			
		}
		else System.out.println("Class " + eClass.getName() + " has no attribute " + attName);
		return fixedValue;
	}
	
	private void checkEquals(String fixedVal1,String fixedVal2,String attName) throws MapperException
	{
		// if the first two values are different, throw an Exception (should check all combinations)
		if ((fixedVal1 != null) && (fixedVal2 != null) && (!fixedVal1.equals(fixedVal2)))
			throw new MapperException ("Different annotations on EClass '" + eClass().getName() 
					+ "' for attribute '" + attName + "' give different fixed values '"
					+ fixedVal2 + "' and '" + fixedVal1 + "'");		
	}
	
	/**
	 * get the fixed value of an attribute from an annotation on the EClass or an ancestor, with a path to the attribute
	 * @param path
	 * @return
	 */
	public String getAncestorFixedValue(String path)
	{
		String value = ModelUtil.getMIFAnnotation(eClass(), "constraint:" + path);
		if ((value == null) && (parent() != null))
		{
			String parentPath = associationName + "/" + path;
			value = parent().getAncestorFixedValue(parentPath);
		}
		return value;	
	}
	
	
	/**
	 * @param path
	 * @return  true if the MIF annotations define any
	 * fixed values for this class or any in its subtree
	 */
	public boolean hasMIFFixedValuesInSubtree()
	{
		return hasMIFFixedValuesInSubtree("");
	}

	
	/**
	 * @param path
	 * @return when path = "", return true if the MIF annotations define any
	 * fixed values for this class or any in its subtree
	 */
	boolean hasMIFFixedValuesInSubtree(String path)
	{
		boolean hasFixedValues = false;
		
		// find if any MIF annotations on this class define a fixed value starting with the required path
		String detailPrefix = "constraint:" + path;
		EMap<String,String> details = ModelUtil.getMIFAnnotationDetails(eClass());
		for (Iterator<Map.Entry<String, String>> it = details.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry<String, String> ent  = it.next();
			if (ent.getKey().startsWith(detailPrefix)) hasFixedValues = true;
		}
		
		// if not, check the ancestors
		if ((!hasFixedValues) && (parent() != null))
		{
			String parentPath = associationName + "/" + path;
			hasFixedValues = parent().hasMIFFixedValuesInSubtree(parentPath);
		}
		return hasFixedValues;
	}
}
