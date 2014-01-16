package com.openMap1.mapper.actions;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.WorkBenchUtil;

public class MergeModelsAction  extends Action implements IAction{
	
	private MapperEditor mapperEditor;
	
    private ClassModelView classModelView;
    
	/** the package which is the root of the model in the class model view */
	private EPackage ecoreRoot;
	
	boolean tracing = true;
	private void trace(String s) {if (tracing) System.out.println(s);}
	
	//  key = package_class_feature
	private Hashtable<String,String[]> mergeRecord;
	
	private int numberOfModels;
	
	public MergeModelsAction()
	{
		super("Combine Ecore Models");
	}

		public void run()
		{
			classModelView = WorkBenchUtil.getClassModelView(false);
			Vector<EPackage> classModels = new Vector<EPackage>();
			Vector<String> messageNames = new Vector<String>();
			
			if (classModelView != null) try
			{
				ecoreRoot = classModelView.ecoreRoot();
				StringTokenizer xt = new StringTokenizer( ecoreRoot.eResource().getURI().toPlatformString(true),"/");
				String projectName = xt.nextToken();
				String[] exts = {"*.csv"}; 
				String resultLocation = "";

				// user selects a csv file of Ecore models to merge with the model in the class model view
				String mappingSetURIString = classModelView.mappingSetURI().toString();
				mapperEditor = WorkBenchUtil.getMapperEditor(mappingSetURIString);
				if (mapperEditor != null)
				{
					String path = FileUtil.getFilePathFromUser(mapperEditor,exts,
							"Select csv file of Ecore models to merge, from project " + projectName,false);
					if (!path.equals(""))
					{ 
						InputStream inputFile = new FileInputStream(path);
						Vector<String> lines = FileUtil.getLines(inputFile);
						if (lines.size() > 3) for (int ln = 0; ln < lines.size(); ln++)
						{
							int cols = 2;
							String[] cells = FileUtil.parseCSVLine(cols,lines.get(ln));
							// check header
							if (ln == 0)
							{
								String[] header = {"Role","Model File"};
								for (int col = 0; col < cols; col++)
									if (!header[col].equals(cells[col]))
										throw new MapperException ("Column header " + cells[col] + " is not " + header[col]);
							}
							// location for result model
							else if (ln == 1)
							{
								if (!cells[0].equals("result")) 
									throw new MapperException("first non-header row must start with 'result' and define the result file");
								if (!cells[1].endsWith(".ecore")) 
									throw new MapperException("Result model must be stored in a .ecore file, not in " + cells[1]);
								resultLocation = FileUtil.platformPreface() + projectName + "/" + "ClassModel/" + cells[1];
							}
							// locations and message names of models to be merged
							else if (ln < lines.size())
							{
								if (!cells[1].endsWith(".ecore")) 
									throw new MapperException("All models to merge must be stored in a .ecore file, not in " + cells[1]);
								String modelLocation = FileUtil.platformPreface() + projectName + "/" + "ClassModel/" + cells[1];
								EPackage model = FileUtil.getClassModel(FileUtil.absoluteLocation(modelLocation));
								classModels.add(model);
								messageNames.add(ModelUtil.getMIFAnnotation(model, "messageName"));
							}
						}
						else if (lines.size() < 3)
						{
							throw new MapperException ("csv file needs to have at least 4 rows - header, result, 2 inputs.");
						}
					}						
				}
				
				numberOfModels = classModels.size();
				if (numberOfModels > 1)
				{
					// check that all models have the same top package name (e.g. 'cda')
					String topPackageName = commonTopPackage(classModels);
					
					// start the new model that will be made by a merge
					EPackage newModel = EcoreFactory.eINSTANCE.createEPackage();
					newModel.setName(topPackageName);
					newModel.setNsPrefix(ecoreRoot.getNsPrefix());
					newModel.setNsURI(ecoreRoot.getNsURI());
					/* note the CDAWrapperModel annotation on the top package cannot be used;so the merged class model 
					 * cannot be attached to a mapping set, but must only be used to generate Java API classes.
					 * Similarly the messageName annotation on the top cda package is arbitrary and will be ignored */
					ModelUtil.copyMifAnnotations(ecoreRoot, newModel);
					
					// start the csv file which will record all model merging operations.
					int cols = classModels.size() + 5;
					String[] header = new String[cols];
					header[0] = "Package";
					header[1] = "Class";
					header[2] = "Feature";
					header[3] = "Sources";
					header[4] = "Multiplicity";
					for (int c = 5; c < cols; c++) header[c] = messageNames.get(c-5);
					// keys = package,class,feature
					mergeRecord = new Hashtable<String,String[]>() ;
					
					// merge all the models - packages, classes and attributes
					for (int m = 0; m < classModels.size(); m++)
						mergeModels(newModel, classModels.get(m), m);
					
					// merge all the model associations (they can be done now, as all target classes are now in the merged model)
					for (int m = 0; m < classModels.size(); m++)
						mergeAssociations(newModel, classModels.get(m), m);
					
					// make any feature optional if it comes from fewer models than its class comes from
					makeFeaturesOptional(newModel);
					
					// save the merged model
					String savePath = FileUtil.absoluteLocation(resultLocation);
					String fullPath = "file:/" + savePath;
					ModelUtil.savePackage(fullPath, newModel);
					
					// save the record of the merging process
					saveMergeRecords(savePath, header);
					
					WorkBenchUtil.showMessage("Completed","Refresh folder to see merged model in Eclipse");
				}
			}
			catch (Exception ex) 
			{
				WorkBenchUtil.showMessage("Error",ex.getMessage());
				ex.printStackTrace();
			}			
		}

		
	/**
	 * merge two ecore models, leaving the result in the new model
	 * @param newTopPackage the new model (top package)
	 * @param sourceTopPackage the source model being merged
	 * @throws MapperException
	 */
	private void mergeModels(EPackage newTopPackage,EPackage sourceTopPackage, int modelNumber) throws MapperException
	{
		// loop over packages in the  model being merged in
		for (Iterator<EPackage> it = sourceTopPackage.getESubpackages().iterator();it.hasNext();)
		{
			EPackage sourcePack = it.next();
			String packName = sourcePack.getName();
			EPackage newPack = getSubPackage(newTopPackage,packName);
			
			// if no such package exists in the new model, make one
			if (newPack == null)
			{
				newPack = EcoreFactory.eINSTANCE.createEPackage();
				newPack.setName(packName);
				newPack.setNsPrefix(sourcePack.getNsPrefix());
				newPack.setNsURI(sourcePack.getNsURI());
				copyAnnotations(sourcePack, newPack);
				newTopPackage.getESubpackages().add(newPack);
				trace("Making package " + packName);
			}
			
			// record that this package came from this model
			noteMerge(packName,"_","_","","Y",modelNumber);
			
			// merge classes and their attributes from this package
			mergeClasses(newPack, sourcePack, modelNumber);
		}		
	}
	
	
	private boolean containsEntryClass(EPackage thePackage)
	{
		boolean contains = false;
		for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator();it.hasNext();)
			if (ModelUtil.getMIFAnnotation(it.next(), "entry") != null) contains = true;
		return contains;
	}
	
	/**
	 * 
	 * @param newPack
	 * @param sourcePack
	 * @param modelNumber
	 * @throws MapperException
	 */
	private void mergeClasses(EPackage newPack, EPackage sourcePack, int modelNumber) throws MapperException
	{
		for (Iterator<EClassifier> it = sourcePack.getEClassifiers().iterator(); it.hasNext();)
		{
			EClassifier next = it.next();
			if (next instanceof EClass)
			{
				EClass sourceClass = (EClass)next;
				String className = sourceClass.getName();
				EClass newClass = (EClass)newPack.getEClassifier(className);

				// if the class is not in the merged model, make it with all the annotations of the original
				if (newClass == null)
				{
					newClass = EcoreFactory.eINSTANCE.createEClass();
					newClass.setName(className);
					copyAnnotations(sourceClass,newClass);
					newPack.getEClassifiers().add(newClass);
					trace("Making class " + className + " in package " + newPack.getName());
				}
				// otherwise, append to the documentation of the class in the new model
				else appendDocumentation(newClass,sourceClass);
				
				// record that this class came from this model
				noteMerge(newPack.getName(),className,"_","","Y",modelNumber);
				
				// merge attributes of the class
				mergeAttributes(newClass,sourceClass,modelNumber);
				
			}
		}
	}
	
	
	/**
	 * copy or check all attributes from a source class to a target class
	 * @param sourceClass
	 * @param targetClass
	 * @throws MapperException
	 */
	private void mergeAttributes(EClass newClass,EClass sourceClass, int modelNumber) throws MapperException
	{
		String className = sourceClass.getName();
		// find all attributes of the source class
		for (Iterator<EStructuralFeature> it = sourceClass.getEStructuralFeatures().iterator(); it.hasNext();)
		{
			EStructuralFeature next = it.next();
			if (next instanceof EAttribute)
			{
				EAttribute sourceAtt = (EAttribute)next;
				String attName = sourceAtt.getName();
				EStructuralFeature newAtt = newClass.getEStructuralFeature(attName);
				/* if the target class has a feature of the same name, check it is an attribute with type; 
				 * and  broaden the multiplicity if necessary */
				if (newAtt != null)
				{
					if (newAtt instanceof EReference) 
						throw new MapperException("EReference of master class '" + className + "' has the same name as attribute '" + attName + "'");
					if (!newAtt.getEType().getName().equals(sourceAtt.getEType().getName()))
						throw new MapperException("Attribute '" + attName + "' of master class '" + className + "' has wrong type ");
					if (sourceAtt.getLowerBound() == 0) newAtt.setLowerBound(0); // broaden the allowed multiplicity
					
					// add to the documentation of the new model attribute
					appendDocumentation(newAtt,sourceAtt);
				}
				
				// if the target class does not have the attribute, add it.
				else if (newAtt == null)
				{
					newAtt = EcoreFactory.eINSTANCE.createEAttribute();
					newAtt.setName(attName);
					newAtt.setLowerBound(sourceAtt.getLowerBound());
					newAtt.setEType(sourceAtt.getEType()); 
					copyAnnotations(sourceAtt, newAtt);
					newClass.getEStructuralFeatures().add(newAtt);
				}
				
				// record that this attribute came from this model
				noteMerge(newClass.getEPackage().getName(),newClass.getName(),attName,multString(newAtt),multString(sourceAtt),modelNumber);
			}
		}
	}
	
	/**
	 * 
	 * @param newTopPackage
	 * @param sourceTopPackage
	 * @param modelNumber
	 * @throws MapperException
	 */
	private void mergeAssociations(EPackage newTopPackage,EPackage sourceTopPackage, int modelNumber) throws MapperException
	{
		// loop over all packages of the source model
		for (Iterator<EPackage> it = sourceTopPackage.getESubpackages().iterator();it.hasNext();)
		{
			EPackage sourcePackage = it.next();
			EPackage newPackage = getSubPackage(newTopPackage,sourcePackage.getName());
			
			// loop over all classes in this package
			for (Iterator<EClassifier> iu = sourcePackage.getEClassifiers().iterator();iu.hasNext();)
			{
				EClassifier next = iu.next();
				if (next instanceof EClass)
				{
					EClass sourceClass = (EClass)next;
					EClass newClass = (EClass)newPackage.getEClassifier(sourceClass.getName());
					
					// loop over all associations of this class
					for (Iterator<EStructuralFeature> iv = sourceClass.getEStructuralFeatures().iterator();iv.hasNext();)
					{
						EStructuralFeature feat = iv.next();
						if (feat instanceof EReference)
						{
							EReference sourceRef = (EReference)feat;
							EClass sourceTarget = (EClass)sourceRef.getEType();
							String targetClassName = sourceTarget.getName();
							String targetPackageName = sourceTarget.getEPackage().getName();
							trace("Association to " + targetClassName + " in package " + targetPackageName);
							EStructuralFeature newFeat = newClass.getEStructuralFeature(sourceRef.getName());
							EReference newRef = null;

							// make a new association if necessary
							if (newFeat == null)
							{
								newRef = EcoreFactory.eINSTANCE.createEReference();
								newRef.setName(sourceRef.getName());
								newRef.setLowerBound(sourceRef.getLowerBound());
								newRef.setUpperBound(sourceRef.getUpperBound());
								newRef.setContainment(sourceRef.isContainment());
								copyAnnotations(sourceRef,newRef);
								
								EPackage newPack = getSubPackage(newTopPackage,targetPackageName);
								EClass newTarget = (EClass)newPack.getEClassifier(targetClassName);
								newRef.setEType(newTarget);
								newClass.getEStructuralFeatures().add(newRef);
							}
							
							else if (newFeat instanceof EReference)
							{
								newRef = (EReference)newFeat;
															
								// checks to make when there is an existing association
								EClass newTarget = (EClass)newRef.getEType();
								if (!newTarget.getName().equals(sourceTarget.getName()))
									throw new MapperException("Association " + newRef.getName()
											+ " of class " + newClass.getName() + " has target class names "
											+ newTarget.getName() + " and " + sourceTarget.getName());
								if (!newTarget.getEPackage().getName().equals(sourceTarget.getEPackage().getName()))
									throw new MapperException("Association " + newRef.getName()
											+ " of class " + newClass.getName() + " has target package names "
											+ newTarget.getEPackage().getName() + " and " + sourceTarget.getEPackage().getName());
								
								// broaden multiplicities
								if (sourceRef.getLowerBound() == 0) newRef.setLowerBound(0);
								if (sourceRef.getUpperBound() == -1) newRef.setUpperBound(-1);
								
								// append to the documentation of the merged model association
								appendDocumentation(newRef,sourceRef);
								
							}
							
							else if (!(newFeat instanceof EReference))
							{
								WorkBenchUtil.showMessage("Association Error", "Feature " + sourceRef.getName() 
										+ " of class " + targetClassName + " is also an attribute.");
							}
							
							// record that this attribute came from this model
							if (newRef != null) 
								noteMerge(newClass.getEPackage().getName(),newClass.getName(),newRef.getName(),multString(newRef),multString(sourceRef),modelNumber);
						}
					}
				}
			}
		}
	}
	
	/**
	 *  make any feature optional if it comes from fewer models than its class
	 * @param newModel
	 */
	private void makeFeaturesOptional(EPackage newModel) throws MapperException
	{
		for (Iterator<EPackage> it = newModel.getESubpackages().iterator();it.hasNext();)
		{
			EPackage thePack = it.next();
			for (Iterator<EClassifier> iu = thePack.getEClassifiers().iterator();iu.hasNext();)
			{
				EClass theClass = (EClass)iu.next();
				String classKey = thePack.getName() + ";" + theClass.getName();
				String[] classRec = mergeRecord.get(classKey + ";_"); // should not be null
				if (classRec == null) throw new MapperException("No merge record for class " + classKey);
				int classCount = new Integer(classRec[3]).intValue();
				
				for (Iterator<EStructuralFeature> iv = theClass.getEStructuralFeatures().iterator(); iv.hasNext();)
				{
					EStructuralFeature feat = iv.next();
					String featureKey = classKey + ";" + feat.getName();
					String[] featRec = mergeRecord.get(featureKey); // should not be null
					if (featRec == null) throw new MapperException("No merge record for feature " + featureKey);
					int featCount = new Integer(featRec[3]).intValue();
					
					if (featCount < classCount)
					{
						feat.setLowerBound(0);
						noteMerge(thePack.getName(), theClass.getName(), feat.getName(), multString(feat), "", -1);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param topPackage
	 * @param packageName
	 * @return
	 */
	private EPackage getSubPackage(EPackage topPackage, String packageName)
	{
		EPackage newPackage = null;
		for (Iterator<EPackage> ix = topPackage.getESubpackages().iterator();ix.hasNext();)
		{
			EPackage pack = ix.next();
			if (pack.getName().equals(packageName)) newPackage = pack;
		}
		return newPackage;
	}


	
	/**
	 * 
	 * @param from
	 * @param to
	 */
	private void copyAnnotations(EModelElement from, EModelElement to)
	{
		ModelUtil.copySomeAnnotations(from, to, ModelUtil.genModelURI());
		ModelUtil.copySomeAnnotations(from, to, ModelUtil.mifNamespaceURI());
	}
	
	/**
	 * keep appending to the documentation of the first model element
	 * @param firstEl
	 * @param secondEl
	 */
	private void appendDocumentation(EModelElement firstEl,EModelElement secondEl)
	{
		EAnnotation firstAnn = firstEl.getEAnnotation(ModelUtil.genModelURI()); 
		String firstDoc = null;
		if (firstAnn != null) firstDoc = firstAnn.getDetails().get("documentation");

		EAnnotation secondAnn = secondEl.getEAnnotation(ModelUtil.genModelURI()); 
		String secondDoc = null;
		if (secondAnn != null) secondDoc = secondAnn.getDetails().get("documentation");

		// append documentation strings if they are both non-null
		String fullDoc = null;
		if (firstDoc == null) fullDoc = secondDoc;
		if (secondDoc == null) fullDoc = firstDoc;
		if ((firstDoc != null) && (secondDoc != null)) fullDoc = firstDoc + secondDoc;
		
		if (fullDoc != null) firstEl.getEAnnotation(ModelUtil.genModelURI()).getDetails().put("documentation",fullDoc);
		
	}

	
	
	/**
	 * 
	 * @param feat
	 * @return
	 */
	private String multString(EStructuralFeature feat)
	{
		String mult = new Integer(feat.getLowerBound()).toString();
		if (feat instanceof EReference) mult = mult + ".." + new Integer(((EReference)feat).getUpperBound()).toString();
		else mult = "min" + mult; // to make all values align right in Excel
		return mult;
	}

	
	/**
	 * create a new row for the csv record of merge operations if necessary, and update the row
	 * @param packageName the package in the merged model being created or updated
	 * @param className the class in the merged model being created or updated
	 * @param featureName the feature (attribute or association) in the merged model being created or updated
	 * @param mergedMult the new merged multiplicity  or existence flag (eg '0' or '1' for an attribute; '0..*' for an association; 'Y' for a class or package)
	 * @param modelMult the multiplicity or existence flag from this model
	 * @param model integer index of the model being merged in
	 */
	private void noteMerge(String packageName, String className, String featureName, String mergedMult, String modelMult, int model)
	{
		String key = packageName + ";" + className + ";"  + featureName;
		String[] row = mergeRecord.get(key);

		// make a new row if necessary; every cell must be populated and so non-null
		if (row == null)
		{
			row = new String[numberOfModels + 5];
			row[0] = packageName;
			row[1] = className;
			row[2] = featureName;
			row[3] = "0";
			// row[4] will be written below
			for (int m = 0; m < numberOfModels; m++) row[5 + m] = ""; // empty all model columns
		}

		// updates to make, whether or not the row existed before
		row[4] = mergedMult; // changed by makeFeaturesOptional, as well as other calls
		if (model > -1)  // only makeFeaturesOptional uses model = -1, because it is not adding a model
		{
			row[3] = new Integer(new Integer(row[3]).intValue() + 1).toString();
			row[5 + model] = modelMult;
		}

		mergeRecord.put(key, row);
	}
	
	
	/**
	 * check that all class models have the same top package name, and return it.
	 * @param classModels
	 * @return
	 * @throws MapperException
	 */
	private String  commonTopPackage(Vector<EPackage> classModels) throws MapperException
	{
		String packageName = classModels.get(0).getName();
		for (int i = 1; i < classModels.size(); i++)
		{
			String name = classModels.get(i).getName();
			if (!name.equals(packageName)) throw new MapperException("Class models have differing top package names: " + packageName + " and " + name);
		}
		
		return packageName;
	}
	
	private String extractFileRoot(String path)
	{
		String fileName = "";
		StringTokenizer st = new StringTokenizer(path,"/\\");
		while (st.hasMoreTokens()) fileName = st.nextToken();
		StringTokenizer su = new StringTokenizer(fileName,".");
		String root = su.nextToken();
		return root;
	}
	
	
	/**
	 * save the csv file recording the merge operations (unordered; need to sort in Excel)
	 * @param savePath
	 * @param header
	 * @throws MapperException
	 */
	private void saveMergeRecords(String savePath, String[] header) throws MapperException
	{
		Vector<String[]> rows = new Vector<String[]>();
		rows.add(header);
		for (Enumeration<String[]> en = mergeRecord.elements();en.hasMoreElements();)
			rows.add(en.nextElement());
		
		String csvPath = savePath.substring(0, savePath.length() - ".ecore".length()) + ".csv";
		FileUtil.writeCSVFile(csvPath, rows);
	}



}
