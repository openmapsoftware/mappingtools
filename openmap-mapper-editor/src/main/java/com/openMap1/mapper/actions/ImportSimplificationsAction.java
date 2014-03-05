package com.openMap1.mapper.actions;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.FeatureView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

/**
 * this action imports message simplification annotations from one Ecore model to another.
 * The importing Ecore model is the one attached to the current mapping set.
 * The Ecore model imported from is selected by a file dialogue. 
 * 
 * Imports are grouped by top-level RMIMs  immediately below the root class, 
 * which are packages in the Ecore models;
 * so the importing model and the model imported from must share some RMIMs.
 * While importing annotations for one RMIM, existing annotations are removed from the RMIM in the importing
 * Ecore model.
 * 
 * Recursion down the tree of RMIM classes (LabelledEClass objects) is cut off before repeat of any class
 * name. This will affect simplification of recursive models or recursive data types. 
 * It could be altered to allow some maximum depth of nesting of the same class name.
 * 
 * @author Robert
 *
 */
public class ImportSimplificationsAction  extends Action implements IAction{
	
	private MapperEditor mapperEditor;
	
    private ClassModelView classModelView;
    
    private String importedAnnotationURI;
    
	/** the package which is the root of the model */
	private EPackage ecoreRoot;
	
	/**
	 * @param theClass
	 * @return true or false, to cut off recursion at 2 nested occurrences of a class name
	 */
	private boolean continueRecursion(LabelledEClass theClass) {return (theClass.nameOccurrences() < MAX_CLASS_OCCURRENCES);}
	
	private int MAX_CLASS_OCCURRENCES = 2;
	
	// another cutoff for recursion, as the first seems not to work
	private int MAX_DEPTH = 20; 

	/* records those classes in the model being imported from, which have a different package
	 * from the root class and have some simplification annotations in their sub-tree */
	private Hashtable<String,LabelledEClass> packageChangeRootClasses;
	
	// keys to annotations are paths to LabelledEClasses with these prefixes
	static String[] pathPrefixes = {"fixed:",""};

	
	boolean tracing = true;
	
	public ImportSimplificationsAction()
	{
		super("Import Simplifications from another Ecore Model");
	}
	
	public void run()
	{
		classModelView = WorkBenchUtil.getClassModelView(false);
		if (classModelView != null) try
		{
			// check the importing Ecore model is an RMIM model (LabelledEClass tree)
			ecoreRoot = classModelView.ecoreRoot();
			if (!ClassModelView.isRMIMRoot(ecoreRoot)) 
				throw new MapperException("Importing class model is not an RMIM class model");
			LabelledEClass importingRoot = ClassModelView.getRootLabelledEClass(ecoreRoot);

			// user selects the Ecore model to import simplifications from
			String mappingSetURIString = classModelView.mappingSetURI().toString();
			mapperEditor = WorkBenchUtil.getMapperEditor(mappingSetURIString);
			if (mapperEditor != null)
			{
				String[] exts = {"*.ecore"}; 
				String path = FileUtil.getFilePathFromUser(mapperEditor,exts,"Select Ecore Model",false);
				if (path.equals("")) return;
				
				EPackage importedEcoreRoot = FileUtil.getClassModel(path);
				if (!ClassModelView.isRMIMRoot(importedEcoreRoot)) 
					throw new MapperException("Imported class model is not an RMIM class model");
				LabelledEClass importedRoot = ClassModelView.getRootLabelledEClass(importedEcoreRoot);
				
				// find the URI used for imported annotations (there must be exactly 1)
				importedAnnotationURI = getITSAnnotationURI(importedEcoreRoot);
				if (importedAnnotationURI == null) 
					throw new MapperException("Selected class model has no simplifications to import");
				
				// do the real work of importing
				importSimplificationsFromRoot(importedRoot, importingRoot);	
				
				// say how many were imported; the count is wrong and has been removed
				String message = "Simplification annotations have been imported.";
				WorkBenchUtil.showMessage("Simplifications imported",message);
				
				// save the resulting Ecore model with imports
				FileUtil.saveResource(ecoreRoot.eResource());
			}
		}
		catch (Exception ex) 
		{
			WorkBenchUtil.showMessage("Error",ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * does the real work of importing simplification annotations
	 * @param importedRoot root class of the RMIM Ecore model being imported from
	 * @param importingRoot root class of the importing model
	 * @throws MapperException
	 */
	private int importSimplificationsFromRoot(LabelledEClass importedRoot, LabelledEClass importingRoot)  throws MapperException
	{
		// set up a table of classes which change package from the root class package, and have simplifications to import under them
		packageChangeRootClasses = new Hashtable<String,LabelledEClass>();
		String topImportedPackageName = importedRoot.eClass().getEPackage().getName();
		int depth = 0;
		findPackageRootImportedClasses(importedRoot, topImportedPackageName,depth);
		
		/* descent of the importing model, looking for package changes.
		 * if the package-changing class matches one with simplifications in its subtree in the imported model,
		 * copy across the simplifications */
		String topImportingPackageName = importingRoot.eClass().getEPackage().getName();
		findPackageChangingImportingClasses(importingRoot, topImportingPackageName);
		
		// import simplifications in the top package
		int simplifications = importSimplifications(importedRoot,importingRoot,true,topImportingPackageName,importedAnnotationURI);
		return simplifications;
	}
	
	/**
	 * recursively add to a table of classes which change package from the root, and have simplifications to import under them
	 * @param importedClass current node of the LabelledEClass tree
	 * @param topPackageName name of top package 
	 * @throws MapperException
	 */
	private void findPackageRootImportedClasses(LabelledEClass importedClass, String topPackageName,int depth) throws MapperException
	{
		String importedPackageName = importedClass.eClass().getEPackage().getName();

		// for any change of package name, stop the recursion; and note if there are simplifications in the subtree
		if (!importedPackageName.equals(topPackageName))
		{
			// simplifications in a data type class directly under the top package will not be imported
			if ((!importedPackageName.equals("datatypes")) && (hasSimplificationInSubtree(importedClass,importedAnnotationURI)))
			{
				if (!importedClass.isSingleChild())
					throw new MapperException("Package changing class at path '" + importedClass.getPath() + "' is not under a 1..1 association");
				packageChangeRootClasses.put(importedClass.getPath(), importedClass);
			}
			return;
		}
		
		// search the subtree
  		for (Iterator<LabelledEClass> it = importedClass.getChildren().iterator(); it.hasNext();)
  		{
  			LabelledEClass childClass = it.next();
  			if ((continueRecursion(childClass)) && (depth < MAX_DEPTH))
  			{
  				trace("Importing depth " + depth + " at class " + childClass.getQualifiedClassName());
  				findPackageRootImportedClasses(childClass,topPackageName,depth + 1);
  			}
  		}
			
	}

	/**
	 * Recursive scan of importing classes, looking for package changes
	 * and if so doing any necessary imports of simplification annotations beneath the parent of 
	 * the package-changing class.
	 * The parent is typically an ActRelationship or a Participation
	 * @param importingClass
	 * @param topPackageName
	 * @throws MapperException
	 */
	private void findPackageChangingImportingClasses(LabelledEClass importingClass, String topPackageName) throws MapperException
	{
		String packageName = importingClass.eClass().getEPackage().getName();

		// for any change of package name, stop the recursion; and import any simplifications in the subtree
		if (!packageName.equals(topPackageName))
		{
			LabelledEClass importedClass = packageChangeRootClasses.get(importingClass.getPath());
			if ((importedClass != null) && (packageName.equals(importedClass.eClass().getEPackage().getName())))
			{
				// mark as used all the associations from the root down to the parent of the importing class
				importingClass.parent().markWithAncestors(true);
				// mark the subtree below the parent of the importing class, following the markings of the imported class subtree
				importSubtreeSimplifications(importedClass.parent(),importingClass.parent());
				// rename the association to the parent of the importing class
				renameAssociationTo(importedClass.parent(),importingClass.parent());
			}
			return;
		}
		
		// search the subtree
		for (Iterator<LabelledEClass> it = importingClass.getChildren().iterator(); it.hasNext();)
  		{
  			LabelledEClass childClass = it.next();
  			if (continueRecursion(childClass)) findPackageChangingImportingClasses(childClass,topPackageName);
  		}
	}
	
	/**
	 * The trees under importingClass and importedClass should match exactly.
	 * Simplification annotations from the imported class subtree are copied
	 * across to the importing class subtree.
	 * Existing simplifications in the importing class subtree are removed.
	 * @param importedClass
	 * @param importingClass
	 * @throws MapperException if the subtrees do not match
	 */
	private int importSubtreeSimplifications(LabelledEClass importedClass,LabelledEClass importingClass)
	throws MapperException
	{
		
		// import simplification annotations for each EAttribute or EReference of this class
		// in importing simplifications, require an exact match of features on this EClass; it comes from the same template MIF
		int simplifications = importClassSimplifications(importedClass,importingClass, true,importedAnnotationURI);
		
		// recurse over child LabelledEClasses which have annotations beneath them
		for (Iterator<LabelledEClass> ic = importingClass.getChildren().iterator(); ic.hasNext();)
		{
			LabelledEClass child = ic.next();
			String childName = child.eClass().getName();
			String assocName = child.associationName();
			LabelledEClass importedChild = importedClass.getNamedAssocAndClassChild(assocName,childName);
			if ((importedChild != null) &&(hasSimplificationInSubtree(importedChild,importedAnnotationURI))) 
				simplifications = simplifications + importSubtreeSimplifications(importedChild, child);
		}
		return simplifications;
	}
	
	/**
	 * import all simplification annotations in the top package; stop the recursion
	 * at any class which changes packages (to a package other than the data types package), 
	 * or which is not matched exactly in the imported model,
	 * or which does not have annotations beneath it
	 *
	 * @param importingClass
	 * @param importedClass
	 * @param topImportingPackageName
	 * @return the number of simplification annotations imported
	 * @throws MapperException
	 */
	public static int importSimplifications(LabelledEClass importedClass,LabelledEClass importingClass, 
			boolean stopAtPackageChange, String topImportingPackageName, String annotationURI)
	throws MapperException
	{
		int simplifications = 0;
		// (possibly) stop recursion at any class which changes package, except to the data types package
		String packageName = importingClass.eClass().getEPackage().getName();
		if ((stopAtPackageChange) && (!packageName.equals(topImportingPackageName)) && (!packageName.equals("datatypes"))) return 0;
		
		// import simplification annotations for each EAttribute or EReference of this class
		// in importing simplifications, do not require an exact match of features on this EClass
		simplifications = simplifications + importClassSimplifications(importedClass,importingClass, false,annotationURI);
		
		// recurse over child LabelledEClasses which are matched and when the matches have annotations beneath them
		for (Iterator<LabelledEClass> ic = importingClass.getChildren().iterator(); ic.hasNext();)
		{
			LabelledEClass child = ic.next();
			LabelledEClass importedChild = importedClass.getNamedAssocAndClassChild(child.associationName(),child.eClass().getName());
			if ((importedChild != null) && (hasSimplificationInSubtree(importedChild,annotationURI)))
			{
				simplifications = simplifications + importSimplifications(importedChild,child, stopAtPackageChange,topImportingPackageName,annotationURI);				
			}
		}
		return simplifications;
	}
	

	
	/**
	 * Import the simplification annotations on the EAttributes and EReferences of one LabelledEClass.
	 * @param fromClass
	 * @param toClass
	 * @param mustMatchExactly if true, just write a warning for any mismatch
	 * @param annotationURI
	 * @return the number of simplification annotations imported
	 * @throws MapperException
	 */
	public static int importClassSimplifications(LabelledEClass fromClass,LabelledEClass toClass, 
			boolean mustMatchExactly, String annotationURI)
	throws MapperException
	{
		int simplifications = 0;
		String className = fromClass.eClass().getName();
		
		// import simplification annotations for each EAttribute or EReference of this class
		for (Iterator<EStructuralFeature> is = toClass.eClass().getEStructuralFeatures().iterator(); is.hasNext();)
		{
			EStructuralFeature toFeature = is.next();
			String featureName = toFeature.getName();
			EStructuralFeature fromFeature = fromClass.eClass().getEStructuralFeature(featureName);
			if ((fromFeature == null) && (mustMatchExactly)) 
				System.out.println("Feature " + featureName + " is missing but expected in imported class " + className);
			
			if (fromFeature != null)
			{
				// remove existing annotations
				EAnnotation existingNote = toFeature.getEAnnotation(FeatureView.microITSURI());
				if (existingNote != null)
				{
					for (int p = 0; p > pathPrefixes.length;p++)
					{
						String prefixedToPath = pathPrefixes[p] + toClass.getPath();
						String value = existingNote.getDetails().get(prefixedToPath);
						if (value != null)
						{
							// remove this key of the annotation
							existingNote.getDetails().removeKey(prefixedToPath);
							// if the annotation has no other keys, remove it altogether
							if (existingNote.getDetails().size() == 0)
							{
								toFeature.getEAnnotations().remove(existingNote);
							}
						}
					}
				}
				
				// import annotations
				EAnnotation fromNote = fromFeature.getEAnnotation(annotationURI);
				if (fromNote != null)
				{
					for (int p = 0; p < pathPrefixes.length;p++)
					{
						String prefixedFromPath = pathPrefixes[p] + fromClass.getPath();
						String prefixedToPath = pathPrefixes[p] + toClass.getPath();
						String value = fromNote.getDetails().get(prefixedFromPath);
						if (value != null) 
						{
							FeatureView.addMicroITSAnnotation(toFeature, prefixedToPath, value);
							simplifications++;
						}
					}
				}				
			}
		}
		return simplifications;
	}

	
	/**
	 * @param importedClass
	 * @param importingClass
	 * The association to the imported class from its parent has been renamed. Apply the same renaming annotation
	 * to the association to the importing class from its parent
	 */
	private void renameAssociationTo(LabelledEClass importedClass,LabelledEClass importingClass)
	{
		// find the associations to the classes, in the imported and importing class models
		LabelledEClass importedParent = importedClass.parent();
		LabelledEClass importingParent = importingClass.parent();
		EReference importedRef = (EReference)importedParent.eClass().getEStructuralFeature(importedClass.associationName());
		EReference importingRef = (EReference)importingParent.eClass().getEStructuralFeature(importingClass.associationName());

		// import the annotation
		EAnnotation note = importedRef.getEAnnotation(importedAnnotationURI);
		if (note != null)
		{
			String value = note.getDetails().get(importedParent.getPath());
			if (value != null) FeatureView.addMicroITSAnnotation(importingRef, importingParent.getPath(), value);
		}
	}


	
	/** 
	 * search an Ecore model for any micro-ITS annotations, and return the URI they use.
	 * Throw an exception if they use more than one URI.
	 * @param model
	 * @return
	 */
	public static String getITSAnnotationURI(EPackage model) throws MapperException
	{
		String annotationURI = null;

		// iterate over all packages under the top package
		for (Iterator<EPackage> ip = model.getESubpackages().iterator();ip.hasNext();)
		{
			EPackage subPackage = ip.next();
			// Iterate over all EClasses in the subpackage
			for (Iterator<EClassifier> ic = subPackage.getEClassifiers().iterator();ic.hasNext();)
			{
				EClassifier ecl = ic.next();
				if (ecl instanceof EClass)
				{
					// iterate over all EAttributes and EReferences of the class
					for (Iterator<EStructuralFeature> is = ((EClass)ecl).getEStructuralFeatures().iterator();is.hasNext();)
					{
						EStructuralFeature feature = is.next();
						// iterate over all microITS EAnnotations of the structural feature
						for (Iterator<EAnnotation> ia = feature.getEAnnotations().iterator();ia.hasNext();)
						{
							EAnnotation note = ia.next();
							if (note.getSource().startsWith(FeatureView.microITSURIStart))
							{
								String nextURI = note.getSource();
								if (annotationURI != null)
								{
									if (!nextURI.equals(annotationURI)) 
										throw new MapperException("Ecore model has more than one simplification mapping set '" + nextURI 
												 + "' and '" + annotationURI + "'");
								}
								else annotationURI = nextURI;
							}
						}
					}
				}
			}
		}
		return annotationURI;
	}
	

	/**
	 * @param node
	 * @param classesSeen
	 * @return true if the node has any EAnnotations with the correct source in its subtree.
	 * There is no need to search the whole subtree, since if there is any simplification on
	 * any node, there are annotations on all nodes above it in the tree.
	 * So it is only necessary to search the top EReferences and EAnnotations
	 */
	public static boolean hasSimplificationInSubtree(LabelledEClass node, String annotationURI)
	{
		// iterate over all EAttributes and EReferences of the class, looking for annotations with the correct source and the correct path
		for (Iterator<EStructuralFeature> is = node.eClass().getEStructuralFeatures().iterator();is.hasNext();)
		{
			EAnnotation note = is.next().getEAnnotation(annotationURI);
			if ((note != null) && (note.getDetails().get(node.getPath()) != null)) return true;
		}				
		return false;		
	}
	
	private void trace(String s) {if (tracing) System.out.println(s);}
	static void message(String s) {System.out.println(s);}

}
