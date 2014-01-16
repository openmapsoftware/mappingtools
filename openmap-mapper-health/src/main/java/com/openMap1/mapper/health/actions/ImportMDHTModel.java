package com.openMap1.mapper.health.actions;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.openMap1.mapper.actions.MakeITSMappingsAction;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.views.WorkBenchUtil;
import com.openMap1.mapper.health.cda.OCLExpression;

/**
 * this class imports an MDHT CDA class model, to create and store a CDA class model
 * in a form suitable for attaching to a mapping set and doing message simplification
 * @author Robert
 *
 */

public class ImportMDHTModel implements IObjectActionDelegate{

	public IWorkbenchPart targetPart; // where this action was invoked from
	public ISelection selection;
	
	protected boolean tracing = true;
	
	/* key = package name. Element - package in the MDHT model */
	private Hashtable<String,EPackage> mdhtPackages;
	
	/* key = superclass 'id' = <package name>|<class name> .
	 * element = a Hashtable with key= MDHT EClass of the subclass, element = "1" */
	private Hashtable<String,Hashtable<EClass,String>> mdhtSubClasses;
	
	/* key = class name; value = class in cda package of constrained class model;
	 * used to check we do not add two classes with the same name from different MDHT model packages */
	private Hashtable<String,EClass> addedClasses;
	
	/* top package of the constrained model */
	private EPackage topPackage;
		
	/* package of the new constrained model in which non-data type classes are all made */
	private EPackage constrainedCDAPackage;

	/* package of the new constrained model in which data type classes are all made */
	private EPackage constrainedDatatypesPackage;
	
	/* strings used as keys of annotations in unresolved associations, to identify the target class  */
	private String MDHTCLASS = "mdhtClass";
	private String MDHTPACKAGE = "mdhtPackage";
	
	private String UML_GENMODEL_SOURCE = "http://www.eclipse.org/uml2/1.1.0/GenModel";
	private String MDHT_ANNOTATION_SOURCE = "http://www.openhealthtools.org/mdht/uml/cda/annotation";
	private String EXTENDED_METADATA_SOURCE = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
	
	// key of an annotation on EReferences, to be used by the wrapper class
	private String CDA_NAME = "CDA_Name";
	
	// calculated from the set of all MDHT packages, by subtraction of those with no constraints
	private Vector<String> templatedPackages = new Vector<String>();
	
	// initial value for a count
	private int oclConstraintNumber = 0;
	
	/* file to be read containing extra permissions for templated classes to appear, in the form 
	 * [qualified owner class name, XPath, qualified permitted class name] */
	private String extraPermissionsFileName = "extraPermissions.csv";
	
	/* file to be written out containing all permissions read in or inferred from OCL constraints, in the form 
	 * [qualified owner class name, XPath, qualified permitted class name, OCL constraint names] */
	private String allPermissionsFileName = "allPermissions.csv";
	
	/* file to be written out containing all OCL constraints read, and whether they were
	 * successfully parsed */
	private String constraintsFileName = "allConstraints.csv";
	
	/* file containing template id combinations of sections to be included in the 
	 * mapped class model view, in the form:
	 * [qualified class name, set of template ids, section class name] */
	private String sectionFilterFileName = "sectionFilter.csv";
	
	/* file containing names, template ids and superclasses of classes with no permissions  */
	private String noPermissionsFileName= "classesWithNoPermissions.csv";
	
	
	/* key = qualified  class name of MDHT class.  Value =  a set of unique String arrays with three elements - 
	 * a path,  the name of the constrained CDA class it reaches, and a list of constraints.*/
	private Hashtable<String,Hashtable<String,String[]>> allPermittedPaths = new Hashtable<String,Hashtable<String,String[]>>();
	
	
	/* used to write out a csv file of classes which have no permissions */
	private Vector<String[]> classesWithNoPermissions;
	// header row for the csv file of classes with no permissions
	private String[] classesWithNoPermissionsHeader = {"Class","TemplateId","SuperClasses"};
	
	// header row for file of all OCL constraints
	private String[] constraintsFileHeader = {"Class","Constraint","Status","Number","OCL"};
	
	/* used to write out a csv file of template permissions used (read in or inferred from OCL constraints) */
	private Vector<String[]> templatePermissions;
	// header row for the csv file of all template permissions
	private String[] templatePermissionsHeader = {"Owning_Class","CDA_Path","Permitted_Class","Owning_TemplateId","PermittedTemplateId","OCL_Constraints"};
	
	// trim the full list of ordered packages according to what packages are in scope 
	private String[] packageOrder() 
	{
		// size the array
		int size = 0;
		for (int i = 0; i < fullPackageOrder.length; i++) if (mdhtPackages.get(fullPackageOrder[i]) != null) size++;
		String[] packageOrder = new String[size];
		// fill the array
		size = 0;
		for (int i = 0; i < fullPackageOrder.length; i++) if (mdhtPackages.get(fullPackageOrder[i]) != null) {packageOrder[size] = fullPackageOrder[i];size++;}
		return packageOrder;
	}
	
	private String[] fullPackageOrder = {"consol","ccd","ihe","hitsp"}; 
	
	// packages which have no template constraints
	private String[] nonTemplatedPackages= {"cda","rim","datatypes"};
	
	/**
	 * when this is false, the only classes which can inherit template ids from their superclasses
	 * (and thus have more than one template id) are subclasses of ClinicalDocument.
	 * In CCDA only this node appears to have more than one template id 
	 */
	private boolean inheritTemplateIds = false;
	
	/**
	 * the MDHT consolidated CDA model has a widespread fixed value for 'contextConductionInd'
	 * which is 'implies' . Most example messages I see have the value 'true'.
	 * This constant determines the fixed value  - or if null, implies there is no fixed value
	 */
	private String fixedContextConductionInd = "true";
	
	/**
	 * if this is true, remove from the model the original CDA associations from which
	 * the templated associations were made. 
	 * For open templates (the US norm) leave the original CDA associations in.
	 */
	private boolean makeClosedTemplates = false;
	
	//----------------------------------------------------------------------------------------------
	//                                       Main run method
	//----------------------------------------------------------------------------------------------

	/**
	 * 
	 */
	public void run(IAction action) {
		try
		{
						
			// Open the MDHT model
			EPackage mdhtEntryPackage = getECoreModel();
			if (mdhtEntryPackage == null) return;
			
			URI EcoreURI = mdhtEntryPackage.eResource().getURI();
			String absoluteEcoreLocation = FileUtil.removeFilePrefix(EcoreURI.toString()); // remove 'file:/'
			String resourceECoreLocation = FileUtil.resourceLocation(absoluteEcoreLocation);
			
			trace("Entry MDHT Package: " + mdhtEntryPackage.getName());
			
			// find all MDHT packages which this model depends on
			findAllMDHTPackages(mdhtEntryPackage);
			EPackage mdhtCDAPackage = mdhtPackages.get("cda");
			if (mdhtCDAPackage == null) throw new MapperException("Cannot find CDA package");
			EPackage mdhtDatatypesPackage = mdhtPackages.get("datatypes");
			if (mdhtDatatypesPackage == null) throw new MapperException("Cannot find data types package");
			trace("\n*** Found " + mdhtPackages.size() + " mdht packages");
			
			// find MDHT subclass relations
			findMDHTSubclasses();
			// summariseMDHTSubclasses();
			trace("\n*** Found MDHT subclass relations.");
			
			// find all relevant OCL annotations, in all packages that have them
			Vector<String[]> OCLRows = new Vector<String[]>();
			OCLRows.add(constraintsFileHeader);
			for (Enumeration<String> en = mdhtPackages.keys();en.hasMoreElements();)
			{
				String pName = en.nextElement();
				if (!GenUtil.inArray(pName, nonTemplatedPackages))
				{
					trace("*** Reading OCL annotations in package " + pName);
					EPackage pack = mdhtPackages.get(pName);
					findOCLAnnotations(pack,OCLRows);
				}
			}
			
			// write out all the OCL annotations found
			String OCLFileLocation = getCSVFileLocation(resourceECoreLocation, constraintsFileName);
			trace("*** writing OCL constraints to " + OCLFileLocation);
			IFile oclFile = EclipseFileUtil.getFile(OCLFileLocation);
			EclipseFileUtil.writeCSVFile(OCLRows, oclFile);
			
			// extend the OCL permissions, by reading from a file, and by subclass extension
			trace("\n**** Extending OCL permissions");
			extendOCLPermissions(absoluteEcoreLocation,resourceECoreLocation);
			
			// make the packages of the constrained CDA model
			topPackage = EcoreFactory.eINSTANCE.createEPackage();
			topPackage.setName("constrainedCDAModel");
			// ensure this class model will be viewed tree-like, as if it were an RMIM in the class model view
			ModelUtil.addMIFAnnotation(topPackage, "RMIM", "true");	
			constrainedCDAPackage = EcoreFactory.eINSTANCE.createEPackage();
			constrainedCDAPackage.setName("CDA");
			constrainedDatatypesPackage = EcoreFactory.eINSTANCE.createEPackage();
			constrainedDatatypesPackage.setName("datatypes");
			topPackage.getESubpackages().add(constrainedCDAPackage);
			topPackage.getESubpackages().add(constrainedDatatypesPackage);
			trace("\n*** Made packages of constrained model.");
			
			// for checks that we don't add the same class twice
			addedClasses = new Hashtable<String,EClass>();
			
			// import all datatype classes, and resolve their associations
			importDatatypes(mdhtDatatypesPackage);
			trace("\n*** Imported data type classes.");
			
			// import all classes in the CDA package and all their subclasses, with CDA associations
			importCDAClasses(constrainedCDAPackage);
			trace("*** Imported CDA classes and subclasses.");
			
			// mark the entry class for the model
			String entryClassName = markEntryClass(mdhtEntryPackage);
			trace("\n*** Marked entry class '" + entryClassName + "'");
			
			// apply OCL constraints to classes, making associations to their subclasses
			trace("\n*** Apply OCL constraints.");
			applyOCLConstraints();
			
			// attach target classes properly to associations
			resolveAllAssociations(constrainedCDAPackage);
			trace("\n*** Attached target classes to associations.");
			
			// for closed templates, prune CDA associations that now have templated versions
			if (makeClosedTemplates) // generally should be false for US realm
			{
				prune_CDA_Associations(constrainedCDAPackage);
				trace("\n*** Pruned CDA associations");
			}
			
			// sort out the strange 'typeId' association from the top ClinicalDocument node
			sortTypeId();
			
			// move classes to packages based on section templates 
			moveClassesToPackages(entryClassName);
			
			// find isolated classes, that have no association to them per se
			trace("\n*** Finding Isolated classes.");
			findIsolatedClasses(resourceECoreLocation);
			// trace("\n*** Listing subclass relations.");
			// showSubclassRelations();
			
			// filter sections to those manually chosen in a csv file
			trace("\n*** Filtering sections.");
			filterSections(absoluteEcoreLocation);
			
			
			// Save the new model
		    String[] modelExts= {};
			String modelPath = FileUtil.getFilePathFromUser(targetPart,modelExts,"Select location for Imported Model",true);
			if (!modelPath.equals(""))
			{
				ModelUtil.savePackage(FileUtil.resourceLocation(modelPath), topPackage);
				WorkBenchUtil.showMessage("Completed", "Saved imported Ecore model " + FileUtil.getFileName(modelPath));
				trace("*** Saved constrained model.");
			}
			else WorkBenchUtil.showMessage("Cancelled","No Ecore model saved");
		}
		catch (Exception ex) 
		{
			WorkBenchUtil.showMessage("Error", ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------
	//                                         Sorting out packages
	//---------------------------------------------------------------------------------------------------------------
	
	/**
	 * populate the Hashtable of all MDHT packages
	 * @param startPackage
	 */
	private void findAllMDHTPackages(EPackage startPackage) throws MapperException
	{
		mdhtPackages = new Hashtable<String,EPackage>() ;
		templatedPackages = new Vector<String>();
		addPackages(startPackage);
		for (Enumeration<String> en = mdhtPackages.keys(); en.hasMoreElements();)
		{
			String packName = en.nextElement();
			trace("MDHT package: " + packName);	
			if (!GenUtil.inArray(packName, nonTemplatedPackages)) templatedPackages.add(packName);
		}
	}

	
	/**
	 * add all MDHT packages, by recording the package of every class in the initial package,
	 * all of its  superclasses, and all the classes they are associated to
	 * @param mdhtPackage
	 */
	private void addPackages(EPackage mdhtPackage) throws MapperException
	{
		for (Iterator<EClassifier> it = mdhtPackage.getEClassifiers().iterator();it.hasNext();) 
		{
			EClassifier next = it.next();
			if (next instanceof EClass)
			{
				addPackagesFromSuperClasses((EClass)next);
			}
		}
	}
	
	/**
	 * 
	 * @param theClass
	 * @throws MapperException
	 */
	private void addPackagesFromSuperClasses(EClass theClass) throws MapperException
	{
		addPackages(theClass);
		for (Iterator<EClass> iu = theClass.getESuperTypes().iterator();iu.hasNext();)
		{
			EClassifier nextSuper = iu.next();
			if (nextSuper.getName() == null) trace("A superclass of " + theClass.getName() 
					+ " in package "  + theClass.getEPackage().getName() + " is has a null name");
			if ((nextSuper != null) && (nextSuper.getName() != null) 
					&& (nextSuper instanceof EClass)) addPackagesFromSuperClasses((EClass)nextSuper);
		}				
		
	}



	/**
	 * 
	 * @param theClass
	 */
	private void addPackages(EClass theClass) throws MapperException
	{
		if (theClass == null) throw new MapperException("Null class");
		addOwnPackage(theClass);
		for (Iterator<EStructuralFeature> iu = theClass.getEStructuralFeatures().iterator();iu.hasNext();)
		{
			EStructuralFeature feature = iu.next();
			if (feature instanceof EReference)
			{
				// trace("Following ref " + feature.getName());
				EClass reffedClass = (EClass)((EReference)feature).getEType();
				if (reffedClass == null) throw new MapperException("No target class for association " 
						+ feature.getName() + " from class " + theClass.getName());
				addOwnPackage(reffedClass);
			}
		}
	}
	
	private void addOwnPackage(EClass theClass) throws MapperException
	{
		EPackage thePackage = theClass.getEPackage();
		if (thePackage == null) throw new MapperException("No package for class " + theClass.getName());
		if (thePackage.getName() == null) throw new MapperException("No name for package of class " + theClass.getName());
		mdhtPackages.put(thePackage.getName(), thePackage);
	}
	
	
	//---------------------------------------------------------------------------------------------------------------
	//                          Recording which MDHT classes are subclasses of other classes
	//---------------------------------------------------------------------------------------------------------------



	/**
	 * record all the proper subclasses of any class
	 */
	private void findMDHTSubclasses()
	{
		mdhtSubClasses = new Hashtable<String,Hashtable<EClass,String>>();
		// loop over all MDHT packages
		for (Enumeration<String> en = mdhtPackages.keys(); en.hasMoreElements();)
		{
			String packageName = en.nextElement();
			EPackage thePackage = mdhtPackages.get(packageName);
			// loop over all classes in the package
			for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator();it.hasNext();) 
			{
				EClassifier next = it.next();
				if (next instanceof EClass)
				{
					EClass theClass = (EClass)next;
					// loop over all superclasses of the class
					for (Iterator<EClass> iu = theClass.getEAllSuperTypes().iterator();iu.hasNext();)
					{
						EClassifier nextSuper = iu.next();
						if (nextSuper instanceof EClass)
						{
							// record the class as a subclass of its superclass
							EClass superC = (EClass)nextSuper;
							if ((!theClass.getName().equals(superC.getName()))
									&& (superC.getName() != null)&& (superC.getEPackage() != null))
							{
								String superId =  superC.getEPackage().getName() + "|" +  superC.getName();
								Hashtable<EClass,String> subs = mdhtSubClasses.get(superId);
								if (subs == null) subs = new Hashtable<EClass,String>();
								subs.put(theClass,"1");
								mdhtSubClasses.put(superId, subs);								
							}
						}
					}				
				}
			}
		}
	}
	
	
	/**
	 * write a summary of how many subclasses there are for each CDA class
	 */
	@SuppressWarnings("unused")
	private void summariseMDHTSubclasses()
	{
		for (Enumeration<String> en = mdhtSubClasses.keys(); en.hasMoreElements();)
		{
			String classKey = en.nextElement();
			Hashtable<EClass,String> subs = mdhtSubClasses.get(classKey);
			trace(classKey + ": " + subs.size());
		}
	}

	//---------------------------------------------------------------------------------------------------------------
	//                      				Reading OCL annotations
	//---------------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param theClass
	 * @return
	 */
	private Hashtable<String,String[]> getOCLPermissions(EClass mdhtClass)
	{
		Hashtable<String,String[]> constraints =  allPermittedPaths.get(ModelUtil.getQualifiedClassName(mdhtClass));
		if (constraints == null) constraints = new Hashtable<String,String[]>();
		return constraints;
	}
	
	/**
	 *  list relevant OCL annotations, which mention classes in the leaf package (eg ccd)
	 * @param mdhtPackage
	 */
	private void findOCLAnnotations(EPackage mdhtPackage,Vector<String[]> OCLRows) throws MapperException
	{
		trace("*** finding constraints from OCL annotations in package " + mdhtPackage.getName());
		for (Iterator<EClassifier> it = mdhtPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			if (next instanceof EClass)
			{
				EClass theClass = (EClass)next;
				boolean writeConstraints = false;
				Hashtable<String,String[]> oclPermissions = findOCLPermissions(theClass,writeConstraints, OCLRows);
				
				String qualifiedClassName = ModelUtil.getQualifiedClassName(theClass);
				allPermittedPaths.put(qualifiedClassName, oclPermissions);
			}
		}
	}
	
	/**
	 * 
	 * @param theClass
	 * @throws MapperException
	 */
	private Hashtable<String,String[]> findOCLPermissions(EClass theClass, boolean writeConstraint,Vector<String[]> OCLRows) throws MapperException
	{
		String packageName = theClass.getEPackage().getName();
		String className = theClass.getName();
		Hashtable<String,String[]> permittedPaths = new Hashtable<String,String[]>();
		for (Iterator<EOperation> iu = theClass.getEOperations().iterator();iu.hasNext();)
		{
			EOperation op = iu.next();
			String constraintName = op.getName();
			EAnnotation ann = op.getEAnnotation(UML_GENMODEL_SOURCE);
			if (ann != null)
			{
				String OCLText = ann.getDetails().get("body");
				String[] OCLRow = new String[5];
				OCLRow[0] = className;
				OCLRow[1] = constraintName;
				OCLRow[2] = "read";
				OCLRow[3] = "-";
				OCLRow[4] = OCLText;
				if (OCLText != null) try
				{
					OCLExpression expr = new OCLExpression(packageName,className,templatedPackages,OCLText,constraintName,false);
					if (expr.parseThisOCL()) 
					{
						oclConstraintNumber++;
					    OCLRow[2] =  new Integer(expr.constraintPaths().size()).toString() + " paths";
						OCLRow[3] = new Integer(oclConstraintNumber).toString();
						OCLRow[4] = expr.OCLText();
						resolveConstraintPaths(theClass,expr,permittedPaths);
					}
					else OCLRow[2] = "no parse";
				}
				catch (Exception ex) {OCLRow[2] = "Exception: "  + ex.getMessage();}
				OCLRows.add(OCLRow);
			}
		}
		return permittedPaths;
	}

	
	/**
	 * 
	 * @param mdhtPackage
	 * @param theClass
	 * @param expr
	 * @throws MapperException
	 */
	private void resolveConstraintPaths(EClass theClass,OCLExpression expr,Hashtable<String,String[]> permittedPaths) throws MapperException
	{
		for (Iterator<String[]> it = expr.getAllConstraints().iterator();it.hasNext();)
		{
			// resolve any ambiguous paths in the constraint, and check that the path can be followed
			String[] constraint = it.next();
			String ambiguousPath = constraint[0];
			String targetClassPackageName = constraint[1];
			EPackage targetClassPackage = mdhtPackages.get(targetClassPackageName);
			String targetClassName = constraint[2];
			String constraintName = constraint[3];
			EClass targetClass = (EClass)targetClassPackage.getEClassifier(targetClassName);
			String truePath = pathToClass(theClass,ambiguousPath,targetClass);
			if (truePath == null) throw new MapperException("Cannot follow path " + ambiguousPath + " from class "
						+ theClass.getName() + " to class " + targetClass.getName());

			// store the constraint without duplicates in the class
			addPermission(truePath,ModelUtil.getQualifiedClassName(targetClass),constraintName,permittedPaths);
		}
	}
	
	/**
	 * resolve an ambiguous path into the actual path that can be followed, sorting out two issues:
	 * (1) the choice between 'entry' and 'entryRelationship' steps depends if the parent class is 
	 * a section or an entry
	 * (2) the last step may be the ambiguous 'clinicalStatement', which needs to be resolved to act or procedure etc.
	 * @param parentClass
	 * @param ambiguousPath a path of association names, which may be ambiguous because of a last 'clinicalStatement' step
	 * @param childClass
	 * @return the unambiguous path, or null if it cannot be followed
	 */
	private String pathToClass(EClass parentClass, String ambiguousPath, EClass childClass)
	{
		EClass currentClass = parentClass;
		boolean hasPath = true;
		String path = "";
		StringTokenizer steps = new StringTokenizer(ambiguousPath,".");
		String step = "";
		// as long as you can follow steps, transcribe them to the true path
		while (steps.hasMoreTokens() && hasPath)
		{
			step = steps.nextToken();
			
			// resolve the 'entry or entryRelationship or component' choice
			if (step.equals("entryOrEntryRelationship"))
			{
				if (isSubClass(parentClass,"Section")) step = "entry";
				else if (isSubClass(parentClass,"Organizer")) step = "component";
				else step = "entryRelationship";
			}
			
			EStructuralFeature feat = currentClass.getEStructuralFeature(step);
			if ((feat != null) && (feat instanceof EReference))
			{
				EReference ref  = (EReference)feat;
				currentClass = (EClass)ref.getEType();
				path = path + step + ".";
			}
			else hasPath = false;				
		}
		
		// the last step may be the ambiguous 'clinicalStatement' which cannot be followed; if so, find out what step it really is
		if (step.equals("clinicalStatement"))
		{
			for (Iterator<EStructuralFeature> it = currentClass.getEAllStructuralFeatures().iterator();it.hasNext();)
			{
				EStructuralFeature feat = it.next();
				if (feat instanceof EReference)
				{
					EClass foundTarget = (EClass)((EReference)feat).getEType();
					step = feat.getName();
					if ((ModelUtil.isSubClass(childClass, foundTarget))
							&& (GenUtil.inArray(step, OCLExpression.CLINICAL_STATEMENT_VALUES))) return (path + step);
				}
			}
		}
		
		// the result of following the path should be a superclass of the target class
		if ((hasPath) && (ModelUtil.isSubClass(childClass, currentClass))) return path;
		return null;
	}
	
	/**
	 * 
	 * @param theClass
	 * @return true if the class is a subclass of CDA class Section.
	 */
	private boolean isSubClass(EClass theClass, String superName)
	{
		boolean sectionClone = false;
		for (Iterator<EClass> it = theClass.getEAllSuperTypes().iterator();it.hasNext();)
		{
			EClass superC = it.next();
			if (superC.getName().equals(superName)) sectionClone = true;
		}
		return sectionClone;
	}


	//---------------------------------------------------------------------------------------------------------------
	//                      Extending OCL permissions, by reading a file, then making subclass extensions
	//---------------------------------------------------------------------------------------------------------------
	
	
	
	private void extendOCLPermissions(String EcoreLocation, String resourceECoreLocation) throws MapperException
	{
		trace("OCL path permissions before extension: " + totalPermissions());
		
		// read a csv file of extra permissions
		String extraPermissionFileLocation = getCSVFileLocation(EcoreLocation, extraPermissionsFileName);
		Vector<String> permissionFileLines = new Vector<String>();
		try {
			permissionFileLines = FileUtil.textLines(extraPermissionFileLocation);			
		}
		catch (Exception ex) {throw new MapperException("Cannot open permissions file at '" + extraPermissionFileLocation + "': " + ex.getMessage());}
		
		// extend with permissions from a csv file
		for (int i = 1; i < permissionFileLines.size(); i++) // miss out the header row
		{
			String line = permissionFileLines.get(i);
			StringTokenizer st = new StringTokenizer(line,",");
			if (st.countTokens() == 3)
			{
				String masterClassName = st.nextToken();
				String path = st.nextToken();
				String permittedClassName = st.nextToken();

				EClass masterClass = getNamedMDHTClass(masterClassName);
				if (masterClass == null) throw new MapperException("Cannot find MDHT master class " + masterClassName);
				EClass permittedClass = getNamedMDHTClass(permittedClassName);
				if (permittedClass == null) throw new MapperException("Cannot find MDHT permitted class " + permittedClassName);

				Hashtable<String,String[]> permissions = getOCLPermissions(masterClass);
				String origin = "from file row " + new Integer(i).toString();
				addPermission(path,permittedClassName,origin,permissions);
				allPermittedPaths.put(masterClassName, permissions);
			}
			else throw new MapperException("Invalid line in permissions csv file: '" + line + "'");
		}
		
		trace("OCL path permissions after extension from file: " + totalPermissions());
		
		extendPermissionsBySubClasses();

		trace("OCL path permissions after extension by subclasses: " + totalPermissions());
		
		showAllPermissions(resourceECoreLocation);
		
	}
	
	/**
	 * used for all additions to the permissions data; store a string array of dimension 2 with no duplicates
	 * @param path
	 * @param permittedClassName
	 * @param permissions a table of permissions for the owner class. key = <path>_$<permitted class> ;
	 * value array = [path,permitted class,constraints].(If  > 1 constraint, separated by ';')
	 */
	private void addPermission(String path,String permittedClassName,String constraintName, Hashtable<String,String[]> permissions)
	{
		String[] cons = new String[3];
		// ensure the path has no final '.'
		String p = path;
		if (p.endsWith(".")) p = p.substring(0, p.length()-1);
		cons[0] = p;

		cons[1] = permittedClassName;
		cons[2] = constraintName;
		String key = cons[0] + "_$" + cons[1];
		// if there is an existing permission with this permitted class and path, collect all the constraint names which led to it
		String[] oldCons = permissions.get(key);
		if (oldCons != null) cons[2] = oldCons[2] + ";" + constraintName;

		permissions.put(key, cons);
	}

	
	/**
	 * 
	 * @return the permissions csv file is in the top MDHT folder - i.e two folders
	 * above the folder holding the mdht ecore file which was right-clicked
	 */
	private String getCSVFileLocation(String EcoreLocation, String fileName)
	{
		StringTokenizer st = new StringTokenizer(EcoreLocation,"/");
		int steps = st.countTokens();
		// miss out the last two folders and file name, i.e the last 3 steps
		String path = "";
		for (int i = 0; i < steps -3; i++) {path = path + st.nextToken() + "/";}
		return path + fileName;
	}
	
	/**
	 * count the total number of permission paths and classes
	 * @return
	 */
	private int totalPermissions()
	{
		int permissions = 0;
		for(Enumeration<String> en = allPermittedPaths.keys();en.hasMoreElements();)
		{
			String className = en.nextElement();
			Hashtable<String,String[]> paths = allPermittedPaths.get(className);
			permissions = permissions + paths.size();
		}
		return permissions;
	}
	
	/**
	 * 
	 * @param EcoreLocation
	 * @throws MapperException
	 */
	private void showAllPermissions(String resourceECoreLocation) throws MapperException
	{
		// initialise the csv file of inferred template permissions to be written out
		templatePermissions = new Vector<String[]>();
		templatePermissions.add(templatePermissionsHeader);
		String allPermissionsFileLocation = getCSVFileLocation(resourceECoreLocation, allPermissionsFileName);
		
		// work through in package order, writing permissions in  each package to the csv file and console
		for (int i = 0; i < packageOrder().length; i++) writeAllPermissionsInPackage(i);

		// write out the csv file
		trace("writing csv file to " + allPermissionsFileLocation);
		IFile csvFile = EclipseFileUtil.getFile(allPermissionsFileLocation);
		EclipseFileUtil.writeCSVFile(templatePermissions, csvFile);
	}
	
	/**
	 * write all permissions form classes in a package to a csv file
	 * @param packageNumber
	 */
	private void writeAllPermissionsInPackage(int packageNumber) throws MapperException
	{
		String packageName = packageOrder()[packageNumber];
		// trace("----- All permissions in package " + packageName);
		for(Enumeration<String> en = allPermittedPaths.keys();en.hasMoreElements();)
		{
			String className = en.nextElement();
			StringTokenizer st = new StringTokenizer(className,".");
			if (st.nextToken().equals(packageName))
			{
				String line = "Class " + className + ": ";
				Hashtable<String,String[]> paths = allPermittedPaths.get(className);
				// only write lines for classes that have some permissions to contain other classes
				if (paths.size() > 0)
				{
					for (Enumeration<String[]> ep = paths.elements();ep.hasMoreElements();)
					{
						// build up the line to be written to the console (several permissions per line)
						String[] cons = ep.nextElement();
						line = line + "[" + cons[0] + "," + cons[1] + "," + cons[2] + "]";
						
						// write a new line to the csv file (one permission per line)
						String[] csvLine = new String[6];
						csvLine[0] = className; // the containing class name
						csvLine[1] = cons[0]; // the path 
						csvLine[2] = cons[1];  // the permitted class
						csvLine[3] = getTemplateId(className); // the containing template id
						csvLine[4] = getTemplateId(cons[1]); // the permitted template id
						csvLine[5] = cons[2]; // constraint names
						templatePermissions.add(csvLine);
					}
					// trace(line);									
				}
			}
		}
	}
	
	/**
	 * 
	 * @param mdhtClassName
	 * @return
	 * @throws MapperException
	 */
	private String getTemplateId(String mdhtClassName) throws MapperException
	{
		String templateId = "";
		EClass mdhtClass = getNamedMDHTClass(mdhtClassName);
		EAnnotation ann = mdhtClass.getEAnnotation(MDHT_ANNOTATION_SOURCE);
		if (ann != null)
		{
			String template = ann.getDetails().get("templateId.root");
			if (template != null) templateId = template;
		}
		return templateId;
	}
	
	
	/**
	 * 
	 */
	private void extendPermissionsBySubClasses() throws MapperException
	{
		for (int fromPackageNo = 0; fromPackageNo < packageOrder().length;fromPackageNo++)
		{
			EPackage fromPackage = mdhtPackages.get(packageOrder()[fromPackageNo]);
			for (int toPackageNo = fromPackageNo; toPackageNo < packageOrder().length; toPackageNo++)
			{
				EPackage toPackage = mdhtPackages.get(packageOrder()[toPackageNo]);
				if ((fromPackage != null) && (toPackage != null))
					extendPermissionsBySubClasses(fromPackage,toPackage);
			}
		}
	}

	/**
	 * 
	 * @param fromPackage
	 * @param toPackage
	 */
	private void extendPermissionsBySubClasses(EPackage fromPackage,EPackage toPackage) throws MapperException
	{
		for (Iterator<EClassifier> it = fromPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			if (next instanceof EClass)
			{
				EClass fromClass = (EClass)next;
				Hashtable<String,String[]> permittedPaths = getOCLPermissions(fromClass);
				if (permittedPaths.size() > 0)
				{
					Vector<EClass> ownerSubclasses = getSubclassesInPackage(fromClass,toPackage);
					extendPermissionsBySubClasses(fromClass,ownerSubclasses,permittedPaths,toPackage);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param fromClass
	 * @param ownerSubclasses
	 * @param permittedPaths
	 */
	private void extendPermissionsBySubClasses(EClass fromClass,
			Vector<EClass>  ownerSubclasses, Hashtable<String,String[]> permittedPaths, EPackage toPackage) throws MapperException
	{
		for (Enumeration<String[]> en = permittedPaths.elements();en.hasMoreElements();)
		{
			String[] cons = en.nextElement();
			String path = cons[0];
			String className = cons[1];
			String constraintName = cons[2];
			EClass target = getNamedMDHTClass(cons[1]);
			Vector<EClass> subTargets = getSubclassesInPackage(target,toPackage);

			// there are no subclasses of the target class; add permissions for subclasses of the owner class, to the one target class
			if (subTargets.size() == 0)
			{
				for (int i = 0; i < ownerSubclasses.size();i++)
				{
					EClass ownerSubclass = ownerSubclasses.get(i);
					Hashtable<String,String[]> subPaths = getOCLPermissions(ownerSubclass);
					addPermission(path,className,constraintName,subPaths);
					allPermittedPaths.put(ModelUtil.getQualifiedClassName(ownerSubclass), subPaths);
				}
			}

			// if there are target subclasses, add a permission for every owner subclass and every target subclass
			else if (subTargets.size() > 0)
			{
				for (int i = 0; i < ownerSubclasses.size();i++)
				{
					EClass ownerSubclass = ownerSubclasses.get(i);
					Hashtable<String,String[]> subPaths = getOCLPermissions(ownerSubclass);
					for (int j = 0; j < subTargets.size();j++)
					{
						EClass subTarget = subTargets.get(j);
						addPermission(path,ModelUtil.getQualifiedClassName(subTarget),constraintName,subPaths);
					}
					allPermittedPaths.put(ModelUtil.getQualifiedClassName(ownerSubclass), subPaths);
				}				
			}
		}
	}

	
	/**
	 * 
	 * @param fromClass
	 * @param toPackage
	 * @return
	 */
	private Vector<EClass> getSubclassesInPackage(EClass fromClass,EPackage toPackage)
	{
		Vector<EClass> subclasses = new Vector<EClass>();
		for (Iterator<EClassifier> it = toPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			if (next instanceof EClass)
			{
				EClass subClass = (EClass)next;
				boolean isSubclass = false;
				for (Iterator<EClass> iu = subClass.getESuperTypes().iterator();iu.hasNext();)
					if (iu.next().equals(fromClass)) isSubclass = true;
				if (isSubclass) subclasses.add(subClass);
			}
		}
		return subclasses;
	}


	//---------------------------------------------------------------------------------------------------------------
	//                      Importing classes from an MDHT package to a constrained model package
	//---------------------------------------------------------------------------------------------------------------
	
	
	
	/**
	 * Revised version
	 * @param cdaPackage
	 */
	private void importCDAClasses(EPackage cdaPackage) throws MapperException
	{
		/* iterate over all packages in the MDHT model, except the RIM or datatypes package(we don't need RIM classes, and 'Act' causes problems)
		 * and the data types package (done already) */
		for (Enumeration<String> en = mdhtPackages.keys();en.hasMoreElements();)
		{
			String mdhtPackageName = en.nextElement();
			if ((!mdhtPackageName.equals("rim")) && (!mdhtPackageName.equals("datatypes")))
			{
				trace("*** Importing from MDHT package " + mdhtPackageName);
				EPackage mdhtPackage = mdhtPackages.get(mdhtPackageName);
				
				// iterate over all classes in the package, importing every one
				for (Iterator<EClassifier> it = mdhtPackage.getEClassifiers().iterator();it.hasNext();)
				{
					EClassifier next = it.next();
					if (next instanceof EClass)
					{
						EClass theClass = (EClass)next;
						// trace("Importing class " + theClass.getName());
						// import the class in the CDA package						
						@SuppressWarnings("unused")
						EClass importedClass = addImportedClass(cdaPackage,theClass,false);
					}
				}				
			}
		}
	}
	
	/* If this is true, add the MDHT package name to the class name generated in the ecore model */
	private boolean addMDHTPackageName = false;
	


	/**
	 * 
	 * @param toPackage the package in the constrained model, to which a class may be added
	 * @param fromClass the class in the MDHT model which may be added
	 * @param keepInheritance if false, give the imported class inherited features so it no longer needs to inherit them
	 * @return
	 */
	private EClass addImportedClass(EPackage toPackage, EClass fromClass, boolean keepInheritance) throws MapperException
	{
		// all imported classes have their original package name in the class name, except data type and cda classes
		String className = fromClass.getName();
		String mdhtPackageName = fromClass.getEPackage().getName();
		if ((!mdhtPackageName.equals("datatypes")) && (!mdhtPackageName.equals("cda")) && (addMDHTPackageName))
			className = className + "_" + mdhtPackageName;			
		className = newUniqueName(className);
		
		// make the bare class, with annotations to say what MDHT class it comes from
		EClass newClass = addBareClass(toPackage, fromClass, className);	

		// pick up all attributes and associations, including inherited ones if keepInheritance = false
		EList<EStructuralFeature> features = fromClass.getEAllStructuralFeatures();
		if (keepInheritance) features = fromClass.getEStructuralFeatures();
		for (Iterator<EStructuralFeature> it = features.iterator();it.hasNext();)
		{
			EStructuralFeature feature = it.next();
			if (feature instanceof EAttribute) addAttribute(newClass, (EAttribute)feature);
			else if (feature instanceof EReference) addUnresolvedAssociation(newClass, (EReference)feature, (EClass)((EReference)feature).getEType());
		}			
		return newClass;
	}
	
	/**
	 * 
	 * @param className
	 * @return a new name for the class that has not been used before
	 */
	private String newUniqueName(String className)
	{
		int index = 0;
		String newName = className;
		while (addedClasses.get(newName) !=  null)
		{
			index++;
			newName = className + "_" + index;
		}
		return newName;
	}
	
	/**
	 * 
	 * @param toPackage
	 * @param fromClass
	 * @return the new EClass 
	 * @throws MapperException if one with that name has already been made
	 */
	private EClass addBareClass(EPackage toPackage, EClass fromClass, String className) throws MapperException
	{
		EClass newClass = EcoreFactory.eINSTANCE.createEClass();
		newClass.setName(className);
		String rimClassName = rimClassName(fromClass);
		if (rimClassName != null) ModelUtil.addMIFAnnotation(newClass, "RIM Class",rimClassName);
		ModelUtil.addMIFAnnotation(newClass, MDHTCLASS, fromClass.getName());
		ModelUtil.addMIFAnnotation(newClass, MDHTPACKAGE, fromClass.getEPackage().getName());
		if (toPackage.getEClassifier(className) != null) throw new MapperException("Duplicate class with name " + className);
		toPackage.getEClassifiers().add(newClass);
		addedClasses.put(className, newClass);
		transferAnnotations(fromClass,newClass);
		return newClass;
	}
	
	/**
	 * 
	 * @param fromClass
	 * @param newClass
	 * @throws MapperException
	 */
	private void transferAnnotations(EClass fromClass,EClass newClass)  throws MapperException
	{
		// decide whether template ids are to be inherited from a superclass
		boolean inheritTemplateIdsHere = inheritTemplateIds; // usually false
		// the one case where two templatIds appear under one node - the head of the document
		if (fromClass.getName().equals("GeneralHeaderConstraints")) inheritTemplateIdsHere = true;
		
		EAnnotation note = fromClass.getEAnnotation(MDHT_ANNOTATION_SOURCE);
		if (note != null)
		{
			EMap<String,String> details =  note.getDetails();
			for (Iterator<String> ik = details.keySet().iterator(); ik.hasNext();)
			{
				String key = ik.next();
				String value = details.get(key);
				// special label/keys for template constraints
				if (key.equals("templateId.root")) 
				{
					String templateLabel = getNextTemplateLabel(newClass); // 'template' or 'template_1' etc.
					// only in special cases (ClinicalDocument node) add template_1 etc. annotations
					if ((templateLabel.equals("template"))||inheritTemplateIdsHere) 
						ModelUtil.addMIFAnnotation(newClass,templateLabel , value);
				}
				// ignore these annotations
				else if (key.startsWith("constraints")) {}
				else if (key.equals("contextDependent")) {}
				else if (fromClass.getName().equals("ProcedureActivity")) {} // strange class in MDHT model
				// path to a constrained value
				else
				{
					String newKey = "constraint:" + followPath(fromClass,key);
					ModelUtil.addMIFAnnotation(newClass, newKey, value);
				}
			}			
		}
		
		// transfer annotations from superclasses in templated packages
		for (Iterator<EClass> it = fromClass.getESuperTypes().iterator(); it.hasNext();)
		{
			EClass superFromClass = it.next();
			if (GenUtil.inVector(superFromClass.getEPackage().getName(),templatedPackages))
				transferAnnotations(superFromClass,newClass);
		}
	}
	
	/**
	 * 
	 * @param newClass
	 * @return a new template label that has not been used already for this class,
	 * in the sequence 'template', 'template_1', 'template_2' etc.
	 */
	private String getNextTemplateLabel(EClass newClass)
	{
		String label = "template";
		if (ModelUtil.getMIFAnnotation(newClass, label) == null) return label;
		int i = 1;
		label = "template_" + i;
		while (ModelUtil.getMIFAnnotation(newClass, label) != null)
		{
			i++;
			label = "template_" + i;
		}
		return label;
	}
	
	/**
	 * 
	 * @param fromClass
	 * @param path a path of form step1.step2.step3
	 * @return a path of form step1/step2/@step3
	 * @throws MapperException if the path cannot be followed from the class
	 */
	private String followPath(EClass fromClass,String path) throws MapperException
	{
		String newPath = "";
		StringTokenizer steps = new StringTokenizer(path,".");
		EClass currentClass = fromClass;
		while(steps.hasMoreTokens())
		{
			String step = steps.nextToken();
			EStructuralFeature feat = currentClass.getEStructuralFeature(step);
			// all steps except the last are associations
			if ((steps.hasMoreTokens()) && (feat != null) && (feat instanceof EReference))
			{
				newPath = newPath + step + "/";
				currentClass = (EClass)((EReference)feat).getEType();
			}
			// the last step is always an attribute
			else if ((!steps.hasMoreTokens()) && (feat != null) && (feat instanceof EAttribute))
			{
				newPath = newPath + "@" + step;
			}
			// turn off path checking for data type 'ANY;
			else if (currentClass.getName().equals("ANY"))
			{
				newPath = newPath + "@" + step;				
			}
			else throw new MapperException("Cannot follow annotation path '" + path + "' from class '" + fromClass.getName() + "' at step '" + step + "'");
		}
		return newPath;
	}
	
	
	/**
	 * 
	 * @param el
	 * @return from the annotation on an imported class or feature, get the mdht class it was derived from
	 */
	private EClass getMDHTClass(EModelElement  el)
	{
		EClass mdhtClass = null;
		String thePackage = ModelUtil.getMIFAnnotation(el, MDHTPACKAGE);
		String theClass = ModelUtil.getMIFAnnotation(el, MDHTCLASS);
		if ((thePackage != null) && (theClass != null))
		{
			EPackage mdhtPackage = mdhtPackages.get(thePackage);
			if (mdhtPackage != null) mdhtClass =  (EClass)mdhtPackage.getEClassifier(theClass);
		}
		return mdhtClass;
	}
	
	/**
	 * 
	 * @param mdhtClass
	 * @return
	 */
	private EClass getImportedClass(EClass mdhtClass)
	{
		EClass result = null;
		String mdhtClassName = mdhtClass.getName();
		
		// find the right package to look in
		EPackage packageToSearch = constrainedCDAPackage;
		if (mdhtClass.getEPackage().getName().equals("datatypes")) packageToSearch = constrainedDatatypesPackage;
		
		// check all classes in the package
		for (Iterator<EClassifier> it = packageToSearch.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			String nextName = next.getName();
			if ((next instanceof EClass) 
					&& (nextName.startsWith(mdhtClassName)) // class name may have been altered by adding '_1' etc.
					&& ((nextName.length() - mdhtClassName.length()) < 3) // avoid major templated extensions of MDHT class names
					&& (getMDHTClass(next) != null)
					&& (mdhtClass.equals(getMDHTClass(next))))
					      result = (EClass) next;				
		}
		return result;
	}
	
	/**
	 * 
	 * @param newClass
	 * @param att
	 * @return
	 */
	private EAttribute addAttribute(EClass newClass, EAttribute att) throws MapperException
	{
		EAttribute newAtt = EcoreFactory.eINSTANCE.createEAttribute();
		
		// set the attribute name, type and lower bound
		newAtt.setName(att.getName());
		newAtt.setLowerBound(att.getLowerBound());
		newAtt.setEType(EcorePackage.eINSTANCE.getEString());
		
		// annotate fixed values of the attribute got from its owning MDHT Class
		String fixedValue = null;
		EObject container = att.eContainer();
		if ((container != null) && (container instanceof EClass))
			fixedValue = getOCLFixedValue((EClass)container,att.getName());
		
		/* annotate any fixed value of the attribute, taken from an annotation on the imported class like 
		        <details key="constraint:@moodCode" value="EVN"/>                 */
		String annotationKey = "constraint:@" + att.getName();
		if (fixedValue == null) fixedValue = ModelUtil.getMIFAnnotation(newClass, annotationKey);
		// may want to remove or replace the  value 'implies' for contextConductionInd?
		if ((newAtt.getName().equals("contextConductionInd")) && (fixedValue != null)) fixedValue = fixedContextConductionInd;
		if (fixedValue != null) ModelUtil.addMIFAnnotation(newAtt, "fixed value", fixedValue);
		
		// attach the attribute
		newClass.getEStructuralFeatures().add(newAtt);
		return newAtt;
	}
	
	/**
	 * Process an annotation whose details are of the form
	 *         <details key="body" value="self.classCode=vocab::ActClinicalDocument::DOCCLIN"/>
	 * @param theClass the EClass with the annotation
	 * @param attName that attribute name, e.g 'classCode'
	 * @return the fixed value, e.g. 'DOCCLIN'
	 */
	private String getOCLFixedValue(EClass theClass, String attName) throws MapperException
	{
		String fixedValue = null;
		for (Iterator<EOperation> it = theClass.getEOperations().iterator();it.hasNext();)
		{
			EOperation op = it.next();
			// the EOperation must match the attribute name
			if (op.getName().equals(attName))
			{
				EAnnotation ann = op.getEAnnotation(UML_GENMODEL_SOURCE);
				if (ann != null)
				{
					String value = ann.getDetails().get("body");
					if (value != null)
					{
						String errorMessage = "****   Unexpected OCL string '" + value 
								+ "' for attribute '" + attName + "' of class " + theClass.getName();
						boolean expectedForm = false;
						StringTokenizer sides = new StringTokenizer(value,"= ");
						int count = sides.countTokens();
						/* expressions like 'self.isMoodCodeDefined() implies self.moodCode=vocab::ActMood::EVN' , possibly with 'not' at the front 
						 * Ignore everything up to and including 'implies' */
						if (count == 5) sides.nextToken(); // consume any initial 'not'
						if (count == 4) 
						{
							sides.nextToken();
							@SuppressWarnings("unused")
							String implies = sides.nextToken();
						}
						// expressions like 'self.moodCode=vocab::ActMood::EVN' 
						if ((count == 2)|(count == 4)|(count == 5))
						{
							String lhs =sides.nextToken();
							if (lhs.startsWith("self."))
							{
								if (!lhs.startsWith("self." + attName))
									trace("LHS " + lhs + " for attribute " + attName);
								String rhs = sides.nextToken();
								StringTokenizer st = new StringTokenizer(rhs,":'");
								int tokens = st.countTokens();
								if (tokens == 1)
								{
									fixedValue = st.nextToken();
									expectedForm = true;									
								}
								else if ((tokens == 3) && (st.nextToken().equals("vocab")))
								{
									st.nextToken();
									fixedValue = st.nextToken();
									expectedForm = true;
								}
							}
						}
						if (!expectedForm) trace(errorMessage);
					}
				}
			}
		}
		return fixedValue;
	}
	
	
	
	/**
	 * 
	 * @param newClass
	 * @return a new text content attribute, added to the class
	 */
	private EAttribute addTextAttribute(EClass newClass)
	{
		EAttribute newAtt = EcoreFactory.eINSTANCE.createEAttribute();
		newAtt.setName("textContent");
		newAtt.setLowerBound(0);
		newAtt.setEType(EcorePackage.eINSTANCE.getEString());
		newClass.getEStructuralFeatures().add(newAtt);
		return newAtt;
	}

	
	/**
	 * An unresolved association is one that does not yet have a target class.
	 * Add an EReference to a class; but do not yet set its type to the target class,
	 * because the target class may not exist in the constrained model. 
	 * In stead, annotate the EReference with the MDHT class and package of the target
	 * class, to be resolved later whan all target classes exist in the constrained model   
	 * @param theClass
	 * @param ref Reference in the MDHT model that the new EReferenceis based on
	 * @param mdhtTarget
	 * @return the new unresolved EReference
	 */
	private EReference addUnresolvedAssociation(EClass theClass, EReference ref, EClass mdhtTarget) throws MapperException
	{
		String location = " at EReference " + ref.getName() + " from class " + theClass.getName();
		String errorMessage = "";
		if (mdhtTarget == null) errorMessage = ("Null MDHT class" + location);
		if (mdhtTarget.getName() == null) errorMessage = ("No name for MDHT class" + location);
		if (mdhtTarget.getEPackage() == null) errorMessage = ("No package for MDHT class " + mdhtTarget.getName()  + location);

		EReference newRef = EcoreFactory.eINSTANCE.createEReference();
		newRef.setName(ref.getName());
		
		if (errorMessage.equals(""))
		{
			newRef.setLowerBound(ref.getLowerBound());
			newRef.setUpperBound(ref.getUpperBound());
			newRef.setContainment(ref.isContainment());
			ModelUtil.addMIFAnnotation(newRef, MDHTCLASS, mdhtTarget.getName());
			ModelUtil.addMIFAnnotation(newRef, MDHTPACKAGE, mdhtTarget.getEPackage().getName());			
			theClass.getEStructuralFeatures().add(newRef);			
		}
		else WorkBenchUtil.showMessage("MDHT class error", errorMessage);
		return newRef;
	}

	/**
	 * Add a revolved association, i.e one which has a target class
	 * @param theClass class to add the association to
	 * @param ref association in the MDHT model that this association is based on
	 * @param newName new name for this association - more complex than the MDHT association name
	 * @param cdaName CDA name of the association - to be put in an annotation
	 * @param target taregt class
	 * @return
	 */
	private EReference addAssociation(EClass theClass, EReference ref, String newName, String cdaName, EClass target)
	{
		EReference newRef = EcoreFactory.eINSTANCE.createEReference();
		newRef.setName(newName);
		newRef.setLowerBound(ref.getLowerBound());
		newRef.setUpperBound(ref.getUpperBound());
		newRef.setContainment(ref.isContainment());
		newRef.setEType(target);
		if (cdaName != null) ModelUtil.addMIFAnnotation(newRef, CDA_NAME, cdaName);
		theClass.getEStructuralFeatures().add(newRef);
		return newRef;
	}
	
	/**
	 * @param fromClass
	 * @return the name of the RIM class it is a clone of, or null if it is a datatype class
	 */
	private String rimClassName(EClass fromClass)
	{
		return packageSuperClassName(fromClass,"rim");
	}

	
	/**
	 * 
	 * @param fromClass
	 * @param packageName
	 * @return the name of the class in the package which this class is a subclass of
	 */
	private String packageSuperClassName(EClass fromClass, String packageName)
	{
		if (fromClass.getEPackage().getName().equals(packageName)) return fromClass.getName();
		for (Iterator<EClass> it = fromClass.getESuperTypes().iterator();it.hasNext(); )
		{
			String pClassName = packageSuperClassName(it.next(),packageName);
			if (pClassName != null) return pClassName;
		}
		return null;
	}

	/**
	 *  mark the entry class for the model - the class derived from an MDHT class
	 *  in the specified package, which is the subclass of  "ClinicalDocument"
	 *  selected by the user
	 * @param mdhtPackage
	 */
	private String markEntryClass(EPackage mdhtPackage) throws MapperException
	{
		String entryClassName = null;
		Vector<String> entryClassNames = new Vector<String>();
		Vector<EClass> entryClasses = new Vector<EClass>();

		// iterate over all classes in the imported Ecore model, derived from any MDHT package
		for (Iterator<EClassifier> it = constrainedCDAPackage.getEClassifiers().iterator(); it.hasNext();)
		{
			EClass theClass = (EClass)it.next();
			EClass mdhtClass = getMDHTClass(theClass);
			// classes derived from an MDHT class in the originally selected package
			if ((mdhtClass != null) && (mdhtPackage.getName().equals(mdhtClass.getEPackage().getName())))
			{
				// build up a list of classes derived from an MDHT class which is a subclass of ClinicalDocument
				for (Iterator<EClass> ix = mdhtClass.getEAllSuperTypes().iterator();ix.hasNext();)
				{
					if (ix.next().getName().equals("ClinicalDocument"))
					{
						entryClassNames.add(mdhtClass.getName());
						entryClasses.add(theClass);
					}
				}
			}
		}
		
		// allow the user to choose an entry class
		int chosen = WorkBenchUtil.chooseOneString("Chooose an entry class", targetPart, entryClassNames);
		
		if ((chosen > -1) && (chosen < entryClassNames.size()))
		{
			EClass theClass = entryClasses.get(chosen);
			// rename the entry class so it has no package suffix
			entryClassName = entryClassNames.get(chosen);
			theClass.setName(entryClassName);							

			// add annotations to the class, to make it the entry class with the correct wrapper class
			ModelUtil.addMIFAnnotation(theClass, "entry", "true");
			ModelUtil.addMIFAnnotation(theClass, "wrapperClass", MakeITSMappingsAction.CDAWrapperClass);
		}

		else throw new MapperException("Selected no subclass of ClinicalDocument as entry class");
		return entryClassName;
	}

	
	
	//---------------------------------------------------------------------------------------------------
	//                              Adding association from OCL constraints
	//---------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 */
	private void applyOCLConstraints() throws MapperException
	{
		// loop over all cda classes that have been imported - avoiding concurrent mods, as we will add to the list
		Vector<EClass> importedClasses = ModelUtil.getAllClasses(constrainedCDAPackage);
		for (Iterator<EClass> it = importedClasses.iterator();it.hasNext();)
		{
			EClass importedClass = it.next();
			EClass mdhtClass = getMDHTClass(importedClass); // every imported class has an MDHT class at this stage.
			// find paths defined by OCL permissions on this class
			Hashtable<String,String[]> permissions = getOCLPermissions(mdhtClass);
			for (Enumeration<String[]> en = permissions.elements();en.hasMoreElements();)
			{
				String[] constraint = en.nextElement();
				String path = constraint[0];
				String qualifiedMDHTClassName = constraint[1];
				EClass permittedMDHTClass = getNamedMDHTClass(qualifiedMDHTClassName);
				EClass endClass = getImportedClass(permittedMDHTClass);
				String constraintName = constraint[2];
				if (endClass == null) trace("Cannot find constrained class '" + constraint[1] + "'");
				else applyOCLConstraint(importedClass,path,endClass,constraintName);
			}
		}		
	}
	
	/**
	 * 
	 * @param qualifiedClassName
	 * @return
	 * @throws MapperException
	 */
	EClass getNamedMDHTClass(String qualifiedClassName) throws MapperException
	{
		String packageName = ModelUtil.getPackageName(qualifiedClassName);
		EPackage mdhtPackage = mdhtPackages.get(packageName);
		if (mdhtPackage == null) throw new MapperException("Cannot find MDHT package " + packageName);
		String className = ModelUtil.getBareClassName(qualifiedClassName);
		EClass permittedMDHTClass = (EClass)mdhtPackage.getEClassifier(className);	
		return permittedMDHTClass;
	}
	
	/**
	 * 
	 * @param importedClass
	 * @param path
	 * @param endClass
	 * @throws MapperException
	 */
	private void applyOCLConstraint(EClass importedClass,String path,EClass endClass,String constraintName) throws MapperException
	{
		boolean fromFile = (constraintName.startsWith("from"));
		// trace("From class " + importedClass.getName() + " OCL path " + path + " to class " + endClass.getName());
		EClass[] MDHTClass = new EClass[20];
		EReference[] MDHTRef = new EReference[20];
		MDHTClass[0] = getMDHTClass(importedClass); // every imported class has an MDHT class at this stage.
		
		// follow the association path, putting classes and EReferences in arrays
		int depth = 0;
		int unalteredAssociations = 0; // number of associations on the path down to the first one you will add
		StringTokenizer steps = new StringTokenizer(path,".");
		while (steps.hasMoreTokens())
		{
			String step = steps.nextToken();
			EStructuralFeature feat = MDHTClass[depth].getEStructuralFeature(step);
			if ((feat != null) && (feat instanceof EReference))
			{
				MDHTRef[depth] = (EReference) feat;
				MDHTClass[depth + 1] = (EClass)MDHTRef[depth].getEType();
				depth++; // final value of depth is path length
			}
			else throw new MapperException("Cannot follow OCL path '" + path + "' at step " + step);
		}
		
		if (fromFile) trace("Path check from " + importedClass.getName() + " via path " + path + " to " + endClass.getName() + ": " + MDHTClass[depth].getName());
		
		// case when the last step of the path is from an ActRelationship or Participation
		if ((depth > 1) && (isActRelationshipOrParticipation(MDHTClass[depth - 1])))
		{
			unalteredAssociations = depth - 2;
			EClass branchClass = getImportedClass(MDHTClass[depth - 2]); // imported class which the new association is to be added to
			EReference topAssoc = MDHTRef[depth -2];
			String newTopAssocName = topAssoc.getName() + "_" + endClass.getName();
			if (fromFile) {trace("Branch class: " + branchClass.getName() + "; New top association: " + newTopAssocName);}
			/* only add a new association and class to this branch class if it has not been added already, for a different 
			 * ancestor class at the beginning of the path. */
			if (branchClass.getEStructuralFeature(newTopAssocName) == null)
			{
				EClass topModelClass = MDHTClass[depth - 1]; //  class which the new class is to be modelled on
				EReference bottomAssoc = MDHTRef[depth -1];
				String newClassName = topModelClass.getName() + "_" + endClass.getName();
				
				// if an ActRelationship or Participation of this name does not exist already, make it and link it to the end class
				EClass newClass = (EClass)constrainedCDAPackage.getEClassifier(newClassName);
				if (newClass == null)
				{
					// make the new ActRelationship or Participation class
					newClass = addBareClass(constrainedCDAPackage, topModelClass, newClassName);
					// pick up its attributes, and associations to data type classes
					for (Iterator<EStructuralFeature> it = topModelClass.getEAllStructuralFeatures().iterator();it.hasNext();)
					{
						EStructuralFeature feature = it.next();
						if (feature instanceof EAttribute) addAttribute(newClass, (EAttribute)feature);
						else if (feature instanceof EReference)
						{
							EReference ref = (EReference)feature;
							EClass target = (EClass)ref.getEType();
							if (target.getEPackage().getName().equals("datatypes"))
								addUnresolvedAssociation(newClass, ref, target);
						}
					}			
										
					// link in the end class by a new association, and annotate the association for use by the CDA wrapper class
					String bottomAssocName = bottomAssoc.getName() + "_" + endClass.getName();
					@SuppressWarnings("unused")
					EReference newbottomRef = addAssociation(newClass, bottomAssoc, bottomAssocName, bottomAssoc.getName(),endClass);									
					if (fromFile) {trace("New class " + newClassName + "; New bottom association: " + bottomAssocName);}
				}
				
				// link the new or found class to the branch class by an association, and annotate the association for use by the CDA wrapper class
				@SuppressWarnings("unused")
				EReference newTopRef = addAssociation(branchClass, topAssoc, newTopAssocName, topAssoc.getName(), newClass);
			}
			boolean hasTheAssoc = (branchClass.getEStructuralFeature(newTopAssocName) != null);
			if (fromFile) trace("Class " + branchClass.getName() + " has association " + newTopAssocName + "? " + hasTheAssoc);
		}
		
		// simple case of linking in the end class by a new association
		else if (!isActRelationshipOrParticipation(MDHTClass[depth - 1]))
		{
			unalteredAssociations = depth - 1;
			EClass branchClass = getImportedClass(MDHTClass[depth - 1]); // imported class which the new association is to be added to
			EReference topAssoc = MDHTRef[depth -1];
			String topAssocName = topAssoc.getName() + "_" + endClass.getName();
			@SuppressWarnings("unused")
			EReference newTopRef = addAssociation(branchClass, topAssoc, topAssocName, topAssoc.getName(),endClass);			
		}
		
		/* if any associations were passed on to the path down to those added, rename them 
		 * and  annotate with their CDA name for the wrapper class. */
		for (int d = 0; d < unalteredAssociations;d++)
		{
			EClass impClass = getImportedClass(MDHTClass[d]);
			EReference ref = (EReference)impClass.getEStructuralFeature(MDHTRef[d].getName());
			if ((ref != null) && (ModelUtil.getMIFAnnotation(ref, CDA_NAME)== null))
			{
				String cdaName = ref.getName();
				String newName = cdaName + "_T";
				ref.setName(newName);
				ModelUtil.addMIFAnnotation(ref, CDA_NAME, cdaName);				
			}
		}
		
	}

	//-----------------------------------------------------------------------------------------
	//                       Attaching target classes to associations
	//-----------------------------------------------------------------------------------------
	

	/** attach target classes  to associations */
	private void resolveAllAssociations(EPackage thePackage) throws MapperException
	{
		for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator(); it.hasNext();)
		{
			EClass theClass = (EClass)it.next();
			for (Iterator<EStructuralFeature> iu = theClass.getEStructuralFeatures().iterator(); iu.hasNext();)
			{
				EStructuralFeature feature = iu.next();
				if (feature instanceof EReference)
				{
					EReference ref = (EReference)feature;
					
					// check if the association needs to be resolved; and do so
					String tClass = ModelUtil.getMIFAnnotation(ref, MDHTCLASS);
					String tPackage = ModelUtil.getMIFAnnotation(ref, MDHTPACKAGE);
					if ((tClass != null) && (tPackage != null))
					{
						ModelUtil.removeMIFAnnotation(ref, MDHTCLASS);
						ModelUtil.removeMIFAnnotation(ref, MDHTPACKAGE);
						EClass targetClass = null;
						// names of imported classes are not changed in the datatypes package
						if (tPackage.equals("datatypes")) 
						{
							targetClass = (EClass)constrainedDatatypesPackage.getEClassifier(tClass);
						}
						// haven't yet worked out how to handle this case of target class 'ecore.EStringToStringMapEntry'
						else if (tPackage.equals("ecore")) 
						{}
						// in other packages, find the imported class whose name may have changed
						else 
						{
							String qualifiedName = tPackage + "." + tClass;
							EClass mdhtClass = getNamedMDHTClass(qualifiedName);
							targetClass = getImportedClass(mdhtClass);
						}
						if (targetClass == null) trace("Cannot find target class " 
								+ tClass + " for association " + ref.getName() + " of class " + theClass.getName());
						else ref.setEType(targetClass);						
					}
				}
			}
		}
	}

	
	//-----------------------------------------------------------------------------------------
	//                  Removing CDA Associations that have templated versions
	//-----------------------------------------------------------------------------------------

	
	
	/**
	 * remove all CDA associations which have had templated associations made from them
	 * @param thePackage
	 * @throws MapperException
	 */
	private void prune_CDA_Associations(EPackage thePackage) throws MapperException
	{
		for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator(); it.hasNext();)
		{
			EClass theClass = (EClass)it.next();
			// names of CDA associations that have been templated
			Hashtable<String,String> renamedRefs = new Hashtable<String,String>();
			// pre-stored list of refs, because we are going to modify it
			Vector<EStructuralFeature> refs = new Vector<EStructuralFeature>();

			// make table of refs to remove, and pre-store list of all refs
			for (Iterator<EStructuralFeature> iu = theClass.getEStructuralFeatures().iterator(); iu.hasNext();)
			{
				EStructuralFeature feature = iu.next();
				if (feature instanceof EReference) 
				{
					refs.add(feature);
					String cdaName = ModelUtil.getMIFAnnotation(feature, CDA_NAME);
					if (cdaName != null) renamedRefs.put(cdaName, "1");
				}
			}
			
			// remove refs
			for (int i = 0; i < refs.size();i++)
			{
				EStructuralFeature feat = refs.get(i);
				if ((feat instanceof EReference) && (renamedRefs.get(feat.getName()) != null))	theClass.getEStructuralFeatures().remove(feat);			
			}
		}
		
	}

	//-----------------------------------------------------------------------------------------
	//      Move classes to packages depending on which section template they occur in
	//-----------------------------------------------------------------------------------------

	
	/**
	 * Move all constrained CDA classes to packages, depending on their section template
	 * @param entryClassName
	 */
	private void moveClassesToPackages(String entryClassName) throws MapperException
	{
		EClass entryClass = (EClass)constrainedCDAPackage.getEClassifier(entryClassName);
		if (entryClass == null) throw new MapperException("Cannot find entry class " + entryClassName);
		String entryPackageName = "cdaHeader";
		moveClassToPackage(entryClass,entryPackageName);
		moveChildClassesToPackages(entryClass,entryPackageName,entryPackageName);
	}
	
	/**
	 * recursive descent of the class tree, moving classes to new packages
	 * @param theClass
	 * @param packageName
	 * @param entryPackageName passed down the recursion unchanged
	 */
	private void moveChildClassesToPackages(EClass theClass,String packageName,String entryPackageName) throws MapperException
	{
		String nextPackageName = packageName;
		for (Iterator<EStructuralFeature> it = theClass.getEStructuralFeatures().iterator(); it.hasNext();)
		{
			EStructuralFeature next = it.next();
			if (next instanceof EReference)
			{
				EReference ref = (EReference)next;
				// only allow one change of package name from the top, at a new section (not an observation, etc.)
				if (packageName.equals(entryPackageName))
				{
					// if the association name is 'component_ProblemSection' make the package name 'problemSection'
					if ((ref.getName().startsWith("component_"))  && (ref.getName().length() > "component_T".length()))
						nextPackageName = GenUtil.initialLowerCase(ref.getName().substring("component_".length()));
					// do not make separate packages for these associations
					if (nextPackageName.endsWith("EntriesOptional")) 
						nextPackageName = nextPackageName.substring(0,nextPackageName.length() - "EntriesOptional".length());
				}
				EClass nextClass = (EClass)ref.getEType();
				// stop the recursion if this class has already been moved, or is in the datatypes package 
				if (moveClassToPackage(nextClass,nextPackageName)) moveChildClassesToPackages(nextClass,nextPackageName, entryPackageName);
			}
		}
	}

	
	/**
	 * 
	 * @param theClass
	 * @param packageName
	 */
	private boolean moveClassToPackage(EClass theClass, String packageName) throws MapperException
	{
		boolean moved = false;
		if (theClass == null) throw new MapperException("Moving null class to package " + packageName);
		if (theClass.getEPackage() == null) throw new MapperException("Class '" + theClass.getName() + "' starts in no package, when moving to to package " + packageName);

		// only move a class to a new package if it is in the initial CDA package it was made in (and not in the datatypes package)
		if (theClass.getEPackage().equals(constrainedCDAPackage))
		{
			// put 'Text' class in data types package
			if (theClass.getName().equals("Text")) packageName="datatypes";
			EPackage newPackage = findOrMakePackage(packageName);
			// adding the class to the new package should remove it from its old package
			newPackage.getEClassifiers().add(theClass);
			moved = true;
		}
		return moved;
	}
	
	/**
	 * find or make a package with a given name
	 * @param packageName
	 * @return the package
	 */
	private EPackage findOrMakePackage(String packageName)
	{
		EPackage thePackage = null;
		for (Iterator<EPackage> it  = topPackage.getESubpackages().iterator();it.hasNext();)
		{
			EPackage next = it.next();
			if (next.getName().equals(packageName)) thePackage = next;
		}
		if (thePackage == null)
		{
			thePackage = EcoreFactory.eINSTANCE.createEPackage();
			thePackage.setName(packageName);
			topPackage.getESubpackages().add(thePackage);
		}
		return thePackage;
	}



	//-----------------------------------------------------------------------------------------
	//                            Importing data types
	//-----------------------------------------------------------------------------------------

	
	/**
	 * 
	 */
	private void importDatatypes(EPackage mdhtDatatypesPackage) throws MapperException
	{
		// first pass; add data type classes with EAttributes, and EReferences not yet resolved
		for (Iterator<EClassifier> it = mdhtDatatypesPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			if (next instanceof  EClass)
			{
				EClass dtClass = (EClass) next;
				// no inheritance in data type classes yet
				EClass newClass = addImportedClass(constrainedDatatypesPackage,dtClass,false);
				// if this class has mixed data type, give it a textContent attribute
				if (isMixedDataType(dtClass)) addTextAttribute(newClass);
			}
		}
		
		// second pass; resolve target classes of EReferences, now that all data type classes have been imported
		for (Iterator<EClassifier> it = constrainedDatatypesPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClass next = (EClass)it.next();
			for (Iterator<EStructuralFeature> iu = next.getEStructuralFeatures().iterator();iu.hasNext();)
			{
				EStructuralFeature feature = iu.next();
				if (feature instanceof EReference)
				{
					EReference ref = (EReference)feature;
					String targetClassName = ModelUtil.getMIFAnnotation(ref, MDHTCLASS);
					EClass targetClass = (EClass)constrainedDatatypesPackage.getEClassifier(targetClassName);
					ref.setEType(targetClass);
				}
			}
		}
		
		// third pass not used ; assert the same inheritance as for MDHT data type classes
		/*
		for (Iterator<EClassifier> it = constrainedDatatypesPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClass next = (EClass)it.next();
			EClass mdhtClass = getMDHTClass(next);
			for (Iterator<EClass> iu = mdhtClass.getESuperTypes().iterator();iu.hasNext();)
			{
				EClass mdhtSuperClass = iu.next();
				EClass dtSuperClass = getImportedClass(mdhtSuperClass);
				if (dtSuperClass == null) throw new MapperException("Cannot find data type class '" + mdhtSuperClass.getName() + "'");
				next.getESuperTypes().add(dtSuperClass);
			}
		}
		*/
		
		// finally ensure that the 'ANY' data type class has every EAttribute or EReference that any other data type class has
		extendANYClass();
	}
	
	/**
	 * 
	 * @param dtClass
	 * @return
	 */
	private boolean isMixedDataType(EClass dtClass)
	{
		boolean mixed = false;
		EAnnotation ann = dtClass.getEAnnotation(EXTENDED_METADATA_SOURCE);
		if ((ann != null) && ("mixed".equals(ann.getDetails().get("kind")))) mixed = true;
		return mixed;
	}

	/**
	 * 
	 * @param dtClass
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean inheritsMixedDataType(EClass dtClass)
	{
		boolean inherits = false;
		for (Iterator<EClass> it = dtClass.getEAllSuperTypes().iterator();it.hasNext();)
			if (isMixedDataType(it.next())) inherits = true;
		return inherits;
	}

	/**
	 * Ensure the ANY data type class has every attribute and association that any other class has,
	 * except the boring association of AD and EN
	 */
	private void extendANYClass() throws MapperException
	{
		EClass anyClass = (EClass)constrainedDatatypesPackage.getEClassifier("ANY");
		if (anyClass == null) throw new MapperException("Cannot find 'ANY' datatype class");
		
		// set up Hashtables to avoid duplicate associations and EReferences
		Hashtable<String,EAttribute> allAttributes = new Hashtable<String,EAttribute>();
		Hashtable<String,EReference> allReferences = new Hashtable<String,EReference>();
		
		// pick up attributes and associations from all data type classes except AD and EN (boring long list, never used)
		String[] avoidNames = {"ANY","AD","EN"};
		for (Iterator<EClassifier> it = constrainedDatatypesPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			if ((next instanceof  EClass) && (!GenUtil.inArray(next.getName(), avoidNames)))
			{
				EClass dtClass = (EClass) next;
				for (Iterator<EStructuralFeature> iu = dtClass.getEStructuralFeatures().iterator();iu.hasNext();)
				{
					EStructuralFeature feature = iu.next();
					if ((feature instanceof EReference) && (allReferences.get(feature.getName()) == null))
					{
						EReference ref = (EReference)feature;
						addAssociation(anyClass, ref, ref.getName(), null, (EClass)ref.getEType());
						allReferences.put(ref.getName(),ref);
					}
					else if ((feature instanceof EAttribute) && (allAttributes.get(feature.getName()) == null))
					{
						EAttribute att = (EAttribute)feature;
						addAttribute(anyClass, att);
						allAttributes.put(att.getName(), att);
					}
				}
			}
		}		
	}
	
	/**
	 * The association 'typeId' points to a class 'InfrastructureRootTypeId' (a subclass of II, in MDHT)
	 * which has two strange attributes 'redefinedRoot' and 'redefinedExtension' deriving from the MDHT cda.ecore.
	 * Remove them, but make the fixed value of 'redefinedRoot' be a fixed value of 'root'
	 */
	private void sortTypeId() throws MapperException
	{
		EClass infraRoot = (EClass)constrainedCDAPackage.getEClassifier("InfrastructureRootTypeId");
		if (infraRoot != null)
		{
			EStructuralFeature redefined = infraRoot.getEStructuralFeature("redefinedRoot");
			EStructuralFeature root = infraRoot.getEStructuralFeature("root");
			String fixedRoot = ModelUtil.getMIFAnnotation(redefined, "fixed value");
			ModelUtil.addMIFAnnotation(root, "fixed value", fixedRoot);
			infraRoot.getEStructuralFeatures().remove(redefined);
		}
	}

	
	//-----------------------------------------------------------------------------------------
	//                       Finding classes that have no association pointing to them
	//-----------------------------------------------------------------------------------------
	

	
	private void findIsolatedClasses(String resourceECoreLocation) throws MapperException
	{
		Hashtable<String,EClass> classesPointedTo = new Hashtable<String,EClass>();
		Hashtable<String,String> classesInConstraints = new Hashtable<String,String>();
		
		// initialise the data for the csv file to be written out
		classesWithNoPermissions = new Vector<String[]>();
		classesWithNoPermissions.add(classesWithNoPermissionsHeader);
		String noPermissionsFileLocation = getCSVFileLocation(resourceECoreLocation, noPermissionsFileName);
		
		// first pass; find all classes with an association pointing to them
		for (Iterator<EClassifier> it = constrainedCDAPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClass next = (EClass)it.next();
			EClass mdhtClass = this.getMDHTClass(next);
			Hashtable<String,String[]> constraints = getOCLPermissions(mdhtClass);
			for (Enumeration<String[]> en = constraints.elements();en.hasMoreElements();)
			{
				String[] constraint = en.nextElement();
				classesInConstraints.put(constraint[1], "1");
			}
			for (Iterator<EStructuralFeature> iu = next.getEStructuralFeatures().iterator(); iu.hasNext();)
			{
				EStructuralFeature feature = iu.next();
				if (feature instanceof EReference)
				{
					EClassifier target = ((EReference)feature).getEType();
					if (target != null) classesPointedTo.put(target.getName(), (EClass)target);
				}
			}
		}
		
		// second pass; write out classes without any association pointing to them
		int totalClasses = 0;
		int isolatedClasses = 0;		
		for (Enumeration<String> ep = mdhtPackages.keys();ep.hasMoreElements();)
		{
			String packName = ep.nextElement();
			EPackage mdhtPackage = mdhtPackages.get(packName);
			if (GenUtil.inVector(packName, templatedPackages))
			{
				// trace("");
				// trace("---- Unlinked classes in package " + packName);
				for (Iterator<EClassifier> it = mdhtPackage.getEClassifiers().iterator();it.hasNext();)
				{
					EClassifier nc = it.next();
					if (nc instanceof EClass)
					{
						EClass mdhtClass = (EClass)nc;
						EClass next = getImportedClass(mdhtClass);
						totalClasses++;
						if ((next != null) && (classesPointedTo.get(next.getName()) == null))
						{
							String[] csvRow = new String[3];
							String className = mdhtClass.getEPackage().getName() + "."  + mdhtClass.getName();
							String line = ("Not linked: " + className + " ");
							csvRow[0] = className;
							csvRow[1] = getTemplateId(className);
							String supers = "";
							for (Iterator<EClass> iu = mdhtClass.getESuperTypes().iterator();iu.hasNext();)
							{
								EClass superC = iu.next();
								String pName = superC.getEPackage().getName() + "." + superC.getName();
								line = line + "[" + pName + "]";
								supers = supers + pName;
								if (iu.hasNext()) supers = supers + "; ";
							}
							csvRow[2] = supers;
							classesWithNoPermissions.add(csvRow);
							// trace(line);
							isolatedClasses++;
						}
					}
				}
				
			}
		}
		
		// write out the csv file
		trace("writing csv file to " + noPermissionsFileLocation);
		IFile csvFile = EclipseFileUtil.getFile(noPermissionsFileLocation);
		EclipseFileUtil.writeCSVFile(classesWithNoPermissions, csvFile);

		trace("Total classes: " + totalClasses + "; isolated classes: "  + isolatedClasses);
		trace("Classes in constraints: " + classesInConstraints.size());
		trace("Classes linked to: " + classesPointedTo.size());
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void showSubclassRelations()
	{
		for (Enumeration<String> en = mdhtPackages.keys();en.hasMoreElements();)
		{
			String packageName = en.nextElement();
			if (GenUtil.inVector(packageName, templatedPackages))
			{
				trace("----- Classes in package " + packageName);
				Hashtable<String,String> referencedPackages = new Hashtable<String,String>();
				EPackage mdhtPackage = mdhtPackages.get(packageName);
				for (Iterator<EClassifier> it = mdhtPackage.getEClassifiers().iterator();it.hasNext();)
				{
					EClassifier next = it.next();
					if (next instanceof EClass)
					{
						String line = "Class " + next.getName() + ": ";
						EClass theClass = (EClass)next;
						for (Iterator<EClass> iu = theClass.getESuperTypes().iterator();iu.hasNext();)
						{
							EClass superC = iu.next();
							String pName = superC.getEPackage().getName();
							referencedPackages.put(pName, "1");
							line = line + "[" + pName + "," + superC.getName() + "]";
						}
						trace(line);
					}
				}
				String packageSummary = "Package " + packageName + " depends on packages ";
				for (Enumeration<String> eg = referencedPackages.keys();eg.hasMoreElements();)
					packageSummary = packageSummary + eg.nextElement() + ",";
				trace(packageSummary);				
			}
		}
	}

	
	//-----------------------------------------------------------------------------------------
	//                      Filtering of included sections from a csv file
	//-----------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param EcoreLocation
	 */
	private void filterSections(String EcoreLocation) 
	{
		// read a csv file of required sections
		String sectionFilterFileLocation = "";
		Vector<String> sectionFilterFileLines = new Vector<String>();
		try {
			sectionFilterFileLocation = getCSVFileLocation(EcoreLocation, sectionFilterFileName);
			sectionFilterFileLines = FileUtil.textLines(sectionFilterFileLocation);	
			filterSections(sectionFilterFileLines);
		}
		catch (Exception ex) {WorkBenchUtil.showMessage("Warning","Error reading section filter file at "
				+ sectionFilterFileLocation + "': " + ex.getMessage() +  "; so all sections are included.");}
	}
	
	
	/**
	 * 
	 * @param sectionFilterFileLines
	 * @throws MapperException
	 */
	private void filterSections(Vector<String> sectionFilterFileLines) throws MapperException
	{
		Hashtable<String,EClass> owningClasses = getOwningClasses(sectionFilterFileLines);
		trace("Owning classes: " + owningClasses.size());
		
		for(Enumeration<String> en = owningClasses.keys();en.hasMoreElements();)
		{
			String owningClassName = en.nextElement();
			EClass theClass = owningClasses.get(owningClassName);
			filterClass(owningClassName,theClass,sectionFilterFileLines);
		}
	}
	
	/**
	 * 
	 * @param owningClassName
	 * @param theClass
	 * @param sectionFilterFileLines
	 */
	private void filterClass(String owningClassName,EClass theClass,Vector<String> sectionFilterFileLines)  throws MapperException
	{
		// empty list of EAttributes and EReferences for the class
		EList<EStructuralFeature> newFeatures = new BasicEList<EStructuralFeature>();
		
		// build up the list, including all EAttributes and only some EReferences
		for (Iterator<EStructuralFeature> it = theClass.getEStructuralFeatures().iterator(); it.hasNext();)
		{
			EStructuralFeature feature = it.next();
			if (feature instanceof EAttribute) {newFeatures.add(feature);}
			else if (feature instanceof EReference)
			{
				EReference ref = (EReference)feature;
				EClass target = (EClass)ref.getEType();
				EClass mdhtClass = getMDHTClass(target);
				
				/* if there is an equivalent MDHT class, whose name has not been altered on import, do not filter */
				if ((mdhtClass != null) && (target.getName().equals(mdhtClass.getName())))
					{newFeatures.add(feature);}
				else
				{
					boolean retained = false;
					/* find child nodes in templated packages; one of these must match in template ids 
					 * for the association to be retained. */
					for (Iterator<EStructuralFeature> iu = target.getEStructuralFeatures().iterator();iu.hasNext();)
					{
						EStructuralFeature f = iu.next();
						if (f instanceof EReference)
						{
							EReference r = (EReference)f;
							EClass sect = (EClass)r.getEType();
							if (matchesTemplates(sect,owningClassName,sectionFilterFileLines)) retained  = true;
						}
					}
					if (retained) newFeatures.add(feature);
				}
			}
		}
		
		// reset the whole list of EAttributes and EReferences for the class
		theClass.eSet(EcorePackage.eINSTANCE.getEClass_EStructuralFeatures(), newFeatures);
	}
	
	/**
	 * 
	 * @param sect
	 * @param parentClassName
	 * @param sectionFilterFileLines
	 * @return true if the templates on the class match those in any line of the file, with the correct parent class name
	 */
	private boolean matchesTemplates(EClass sect,String parentClassName,Vector<String> sectionFilterFileLines)
	{
		boolean matches = false;
		// try to match the section against any file line with the required class name
		for (int i = 1; i < sectionFilterFileLines.size();i++)
		{
			StringTokenizer st = new StringTokenizer(sectionFilterFileLines.get(i),",");
			String cName = st.nextToken();
			if (parentClassName.equals(cName))
			{
				String templates = st.nextToken();
				if (matchesTemplates(sect,templates)) matches = true;
			}
		}
		// message(parentClassName + "; " + sect.getName() + ": " + matches);
		return matches;
	}
	
	/**
	 * 
	 * @param sect a class in the imported Ecore class model
	 * @param templates String of template ids, separated by ';' and ' '
	 * @return true if the set of template ids on the annotations of the class exactly matches
	 * the templates in String 'templates'
	 */
	private boolean matchesTemplates(EClass sect,String templates)
	{
		boolean matches = false;
		EAnnotation ann = sect.getEAnnotation(ModelUtil.mifNamespaceURI());
		// no match if the class has no template annotations
		if (ann != null)
		{
			Hashtable<String,String> sectionTemplates = new Hashtable<String,String>();			
			// collect all template ids from the class annotations
			EMap<String,String> details = ann.getDetails();
			for (Iterator<String> it = details.keySet().iterator();it.hasNext();)
			{
				String key = it.next();
				// template annotations have keys 'template' , 'template_1', etc
				if (key.startsWith("template"))
				{
					String templateId = details.get(key);
					sectionTemplates.put(templateId, "1");
				}
			}
			
			// can only match if there is a matching number of template ids, separated by ';' and ' '
			StringTokenizer ids = new StringTokenizer(templates,"; ");
			if (ids.countTokens() == sectionTemplates.size())
			{
				matches = true;
				while (ids.hasMoreTokens())
					if (sectionTemplates.get(ids.nextToken()) == null) matches = false;
			}
		}
		return matches;
	}


	/**
	 * 
	 * @param sectionFilterFileLines
	 * @return a Hashtable of all the classes mentioned in the first column of the csv file
	 * @throws MapperException
	 */
	private Hashtable<String,EClass> getOwningClasses(Vector<String> sectionFilterFileLines) throws MapperException
	{
		Hashtable<String,EClass> owningClasses = new Hashtable<String,EClass>();
		
		for (int i = 1; i < sectionFilterFileLines.size();i++)
		{
			String line = sectionFilterFileLines.get(i);
			StringTokenizer st = new StringTokenizer(line,",");
			if (st.countTokens() != 3) throw new MapperException("Invalid line in csv file: '" + line + "'");
			String className = st.nextToken();
			EClass mdhtClass = getNamedMDHTClass(className);
			if (mdhtClass == null)  throw new MapperException("Cannot find MDHT class: '" + className + "'");
			EClass importedClass = this.getImportedClass(mdhtClass);
			if (importedClass == null)  throw new MapperException("Cannot find imported class: '" + className + "'");
			owningClasses.put(className, importedClass);
		}
		return owningClasses;
	}
		

	
	//-----------------------------------------------------------------------------------------
	//                        Plumbing and trivia
	//-----------------------------------------------------------------------------------------
	
	
	
	/**
	 * 
	 * @param theClass
	 * 
	 * @return true if the class is an ActRelationship clone or a Participation clone
	 */
	private boolean isActRelationshipOrParticipation(EClass mdhtClass)
	{
		boolean isBranch = false;
		for (Iterator<EClass> it = mdhtClass.getESuperTypes().iterator();it.hasNext();)
		{
			String superClassName = it.next().getName();
			if (superClassName.equals("ActRelationship")) isBranch = true;
			if (superClassName.equals("Participation")) isBranch = true;
		}
		return isBranch;
	}



	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	/**
	 * 
	 * @return either the ecore file which was right-clicked, or 
	 * if a mapper file was right-clicked, an ecore file chosen by the user
	 * @throws MapperException
	 */
	protected EPackage getECoreModel() throws MapperException
	{
		String resourceLocation = FileUtil.removeFilePrefix(getFilePath(selection));
		
		// if the user right-clicked an ecore file, return it
		if (resourceLocation.endsWith(".ecore")) return FileUtil.getClassModel(resourceLocation);

		// if the user right-clicked a mapper file, ask for the location of an ecore file
		if (resourceLocation.endsWith(".mapper"))
		{
			String[] exts = {"*.ecore"};
			String loc = FileUtil.getFilePathFromUser(targetPart, exts, "Select MDHT Ecore model", false);
			resourceLocation = FileUtil.forwardSlashForm(loc);
		}
		if (resourceLocation != null) return FileUtil.getClassModel(resourceLocation);
		
		// if the user cancelled
		return null;
	}
	
	/**
	 * @param selection the object the user right-clicked to get this action
	 * @return the file path to it
	 */
	public String getFilePath(ISelection selection)
	{
		String path = "not found";
		if (selection instanceof IStructuredSelection)
		{
			Object el = ((IStructuredSelection)selection).getFirstElement();
			if (el instanceof IFile)
			{
				IFile file = (IFile)el;
				path = file.getLocationURI().toString();
			}
		}
		return path;		
	}
	
	
	protected void message(String s)
	{
		System.out.println(s);
	}
	
	protected void trace(String s)
	{
		if (tracing) System.out.println(s);
	}

}
