package com.openMap1.mapper.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EDataType;

import org.eclipse.emf.common.util.EList;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.SemanticMismatch;
import com.openMap1.mapper.reader.XOReader;

/**
 * Methods in this class find and evaluate the best possible match
 * between two Ecore model instances, where the child nodes may be in different orders
 * in the two instances.
 * @author robert
 *
 */
public class EcoreMatcher {
	
	private EObject eObj1, eObj2;
	
	private String sourceCode, resultCode;
	
	private XOReader sourceReader, resultReader;
	
	private boolean tracing = false;
	
	//-------------------------------------------------------------------------------------
	//                              constructor
	//-------------------------------------------------------------------------------------
	
	public EcoreMatcher(EObject eObj1, EObject eObj2, 
			String sourceCode,String resultCode,
			XOReader sourceReader, XOReader resultReader)
	{
		this.eObj1 = eObj1;
		this.eObj2 = eObj2;
		this.sourceCode = sourceCode;
		this.resultCode = resultCode;
		this.sourceReader = sourceReader;
		this.resultReader = resultReader;
	}
	
	//-------------------------------------------------------------------------------------
	//                               public access methods
	//-------------------------------------------------------------------------------------

	public int[] scores()
		throws MapperException
	{
		int[] scores = new int[3];
		Hashtable<String,Integer>  fBag1 = featureBag(eObj1,true);
		Hashtable<String,Integer>  fBag2 = featureBag(eObj2,true);
		scores[0] = bagMatch(fBag1,fBag1);
		scores[1] = bagMatch(fBag2,fBag2);
		
		Hashtable<String, SemanticMismatch> mismatches = new Hashtable<String, SemanticMismatch>();
		scores[2] = treeMatch(eObj1,eObj2, mismatches);
		return scores;
	}
	
	//-------------------------------------------------------------------------------------
	//                              structured matching
	//-------------------------------------------------------------------------------------
	
	/**
	 * match the structures beneath the EObjects as well as possible
	 * (using a heuristic bag-matching to guide the search) 
	 * and build up the table of all mismatches.
	 * @param e1 an EObject
	 * @param e2 another EObjjecgt which should match it partially
	 * @param mismatches the set of all mismatches found so far, to be extended
	 * @return the number of matching features.
	 * @throws MapperException
	 */
	public int treeMatch(EObject e1, EObject e2, 
			Hashtable<String, SemanticMismatch> mismatches)
		throws MapperException
	{
		return doTreeMatch(e1,e2,  mismatches, "");
	}
		
	/**
	 * 
	 * @param e1 an EObject
	 * @param e2 another EObjjecgt which should match it partially
	 * @param mismatches the set of all mismatches found so far, to be extended
	 * @param path the path of containment associations from the root EObject 
	 * of each tree to e1 and e2 -  it was to be used in semantic mismatch messages,
	 * but it turns out to be unnecessary ,as the path can be deduced from either EObject.
	 * @return the number of matching nodes in the trees below the two EObjects
	 * @throws MapperException
	 */
	private int doTreeMatch(EObject e1, EObject e2, 
				Hashtable<String, SemanticMismatch> mismatches, String path)
			throws MapperException
		{
		
		Hashtable<String,Integer>  fBag1 = featureBag(e1,false);
		Hashtable<String,Integer>  fBag2 = featureBag(e2,false);
		// initially, just the match on the top node - includes attributes and non-containment features
		int score = bagMatch(fBag1,fBag2);
		if (score > 0) recordMismatches(e1,e2,mismatches);

		EClass ec1 = e1.eClass(); //assume e1 and e2 are of the same eClass
		EClass ec2 = e2.eClass(); //needed because the two EObjects don't like each others' EStructuralFeatures
		trace("Matching " + ec1.getName());
		// match child objects only within each containment feature
		for (Iterator<EStructuralFeature> it = ec1.getEAllStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature ef1 = it.next();
			if ((ef1 instanceof EReference) && (((EReference)ef1).isContainment()))
			{
				String refName = ef1.getName();
				String newPath = path + "/" + refName;
				Object o1 = e1.eGet(ef1);
				// get the corresponding feature of the other EObject
				Object o2 = null;
				EStructuralFeature ef2 = ec2.getEStructuralFeature(refName);
				if (ef2 != null) o2 = e2.eGet(ef2);
				if (((o1 != null) && (o2 != null))) 
				{
					// single child - match only if they are of the same class
					if ((((EReference)ef1).getUpperBound() == 1) && 
							(o1.getClass().getName().equals(o2.getClass().getName())))
					{
						score = score + 1 + doTreeMatch((EObject)o1, (EObject)o2, mismatches,newPath);
					}
					// lists of children - may contain several classes
					else if (((EReference)ef1).getUpperBound() == -1)
					{
						score = score + featureMatch(o1, o2, mismatches);
					}					
				}
			}
		}
		return score;		
	}
	
	/**
	 * o1 and o2 are Lists of EObjects reached by some EReference feature.
	 * Split them into sub-lists of the same class, and match the sub-lists
	 * @param o1
	 * @param o2
	 * @return
	 */
	private int featureMatch(Object o1, Object o2, 
			Hashtable<String, SemanticMismatch> mismatches)
		throws MapperException
	{
		int score = 0;
		Hashtable<String,Vector<BaggedEObject>> lbc1 = listsByClass(o1);
		Hashtable<String,Vector<BaggedEObject>> lbc2 = listsByClass(o2);
		for (Enumeration<String> en = lbc1.keys();en.hasMoreElements();)
		{
			String className = en.nextElement();
			Vector<BaggedEObject> v1 = lbc1.get(className);
			Vector<BaggedEObject> v2 = lbc2.get(className);
			if ((v1 != null) && (v2 != null)) score = score + classMatch(v1,v2, mismatches);
		}
		return score;
	}
	
	/** 
	 * split a List of objects of different classes into
	 * separate Lists for each class
	 * @param o
	 * @return
	 */
	private Hashtable<String,Vector<BaggedEObject>> listsByClass(Object o)
	{
		Hashtable<String,Vector<BaggedEObject>> lbc = new Hashtable<String,Vector<BaggedEObject>>();
		if (o instanceof List<?>)
			for (Iterator<?> it = ((List<?>)o).iterator();it.hasNext();)
			{
				Object next = it.next();
				if (next instanceof EObject)
				{
					EObject eo = (EObject)next;
					String className = eo.eClass().getName();
					Vector<BaggedEObject> soFar = lbc.get(className);
					if (soFar == null) soFar = new Vector<BaggedEObject>();
					soFar.add(new BaggedEObject(eo));
					lbc.put(className,soFar);
				}
			}
		return lbc;
	}
	
	/**
	 * Find the best match of two Vectors of EObjects, all of the same class. 
	 * Compute all pairwise matches, then pick off the best matches in order,
	 * making sure that no EObject from either list is ever used more than once.
	 * @param v1
	 * @param v2
	 * @return
	 */
	private int classMatch(Vector<BaggedEObject> v1, Vector<BaggedEObject> v2, 
			Hashtable<String, SemanticMismatch> mismatches)
		throws MapperException
	{
		// compute all pairwise bag matches
		int[][] bagMatch = new int[v1.size()][v2.size()];
		trace("class match " + v1.size() + " " + v2.size());
		for (int i1 = 0; i1 < v1.size(); i1++)
			for (int i2 = 0; i2 < v2.size(); i2++)
			{
				BaggedEObject b1 = v1.get(i1);
				BaggedEObject b2 = v2.get(i2);
				bagMatch[i1][i2] = bagMatch(b1.fBag,b2.fBag);
			}

		// pick out the best of the pairwise matches, in order of descending score
		int score = 0;
		// the maximum number of matches - when one list is exhausted
		int maxMatches = Math.min(v1.size(), v1.size());
		for (int m = 0; m < maxMatches; m++)
			score = score + bestRemainingMatch(bagMatch,v1,v2, mismatches);
		return score;
	}
	
	/**
	 * Each time this method is called, it picks out the best bag match in the matrix
	 * between BaggedEObjects that are not yet matched, marks them as matched
	 * so they  will not match again, and computes their structure match
	 * @param bagMatch array of bag match scores
	 * @param v1
	 * @param v2
	 * @return the structure match of the best match pair
	 */
	private int bestRemainingMatch(int[][] bagMatch,
			Vector<BaggedEObject> v1,Vector<BaggedEObject> v2, 
			Hashtable<String, SemanticMismatch> mismatches)
		throws MapperException
	{
		// find the best remaining bag match
		int iBest=0;
		int jBest=0;
		int bestMatch = 0;
		for (int i = 0; i < v1.size(); i++)
			for (int j = 0; j < v2.size(); j++)
				if ((!v1.get(i).matched) && (!v2.get(j).matched) && 
						(bagMatch[i][j] > bestMatch))
				{
					bestMatch = bagMatch[i][j];
					iBest = i;
					jBest = j;
				}
		
		// record that the best pair are both matched, and find their structure match
		int score = 0;
		if (bestMatch > 0)
		{
			v1.get(iBest).matched = true;
			v2.get(jBest).matched = true;
			score = treeMatch(v1.get(iBest).obj,v2.get(jBest).obj, mismatches);
		}			
		return score;		
	}
	
	/**
	 * Inner class of an EObject and the extra information 
	 * needed to find a best tree match
	 * @author robert
	 *
	 */
	class BaggedEObject
	{
		Hashtable <String,Integer>  fBag;
		EObject obj;
		boolean matched;
		
		BaggedEObject(EObject obj)
		{
			this.obj = obj;
			fBag = featureBag(obj, true);
			matched = false;
		}
		
		int size() {return bagMatch(fBag,fBag);}
	}
	
	//---------------------------------------------------------------------------------------------------
	//                       recording mismatches between two Ecore instances
	//---------------------------------------------------------------------------------------------------
	
	/**
	 * record the mismatches between two Ecore structures 
	 * that occur locally on this node. 
	 * Mismatches that occur on descendant nodes will be done by recursive calls to this
	 * method
	 * @param e1 one of the EObjects - the one from the source of a translation, which 
	 * is generally expected to have more features
	 * @param e2 the other EObject that is expected to match e1; this comes from the 
	 * result of a test translation, so is generally expected to have fewer features.
	 * @param mismatches a Hashtable of SemanticMismatch objects found so far -
	 * to be extended
	 */
	private void recordMismatches(EObject e1, EObject e2, 
			Hashtable<String, SemanticMismatch> mismatches)
		throws MapperException
	{

		EClass ec1 = e1.eClass(); //assume e1 and e2 are of the same eClass
		EClass ec2 = e2.eClass(); //needed because the two EObjects don't like each others' EStructuralFeatures

		// look at all structural features, inherited or not
		for (Iterator<EStructuralFeature> it = ec1.getEAllStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature ef1 = it.next();
			Object o1 = e1.eGet(ef1);
			// get the corresponding feature of the other EObject
			Object o2 = null;
			EStructuralFeature ef2 = ec2.getEStructuralFeature(ef1.getName());
			if (ef2 != null)  o2 = e2.eGet(ef2);
			boolean isAttribute = (ef1 instanceof EAttribute);
			if ((isAttribute) && !(ef2 instanceof EAttribute))
			{
				String message = "Feature " + ef1.getName() + " of class " + ec1.getName() + " is both an attribute and an association";
				// FIXME - do something useful with the message
				System.out.println(message);
				return;
			}
			
			// no mismatch if both features are missing
			if (((o1 == null) && (o2 == null))) {}
			
			// common case - a feature present in the source, not in the result
			else if (((o1 != null) && (o2 == null)))
			{
				boolean bizarre = false;
				recordMissingFeature(e1,ef1,o1,mismatches,bizarre);	
			}
			
			// bizarre case - a feature absent in the source, but present in the result
			else if (((o1 == null) && (o2 != null)))
			{
				boolean bizarre = true;
				recordMissingFeature(e1,ef1,o1,mismatches,bizarre);	
					
			}
				
			// both features are present; need to check if their values differ
			else if (((o1 != null) && (o2 != null))) 
			{
				if (isAttribute)
				{
					EDataType et1 = ((EAttribute)ef1).getEAttributeType();
					EDataType et2 = ((EAttribute)ef2).getEAttributeType();
					if (et1.isSerializable())
					{
						String s1 = stringValue(o1,et1);
						String s2 = stringValue(o2,et2);
						boolean matched = ((s1 != null) && (s2 != null) && (s1.equals(s2)));
						if (!matched)
						{
							int nature = SemanticMismatch.SEMANTIC_INCORRECT_PROPERTY_VALUE;
							SemanticMismatch sm = new SemanticMismatch
								(nature,s1,s2,e1,ef1,null,sourceCode, resultCode);
							storeMismatch(sm,mismatches);							
						}
					}
				}
				// an EReference present in both source and translation result
				else if (!isAttribute)
				{
					// single child - mismatch if they are not of the same class
					if ((((EReference)ef1).getUpperBound() == 1) && 
							(!o1.getClass().getName().equals(o2.getClass().getName())))
					{
						int nature = SemanticMismatch.SEMANTIC_INCORRECT_TARGET_CLASS;
						SemanticMismatch sm = new SemanticMismatch
							(nature,o1.getClass().getName(),o2.getClass().getName(),
									e1,ef1,null,
									sourceCode, resultCode);
						storeMismatch(sm,mismatches);							
					}
					
					// lists of children - may contain several classes
					else if (((EReference)ef1).getUpperBound() == -1)
					{
						Hashtable<String,Vector<BaggedEObject>> l1 = listsByClass(o1);
						Hashtable<String,Vector<BaggedEObject>> l2 = listsByClass(o2);
						for (Enumeration<String> en1 = l1.keys();en1.hasMoreElements();)
						{
							String targetClass = en1.nextElement();
							Vector<BaggedEObject> v1 = l1.get(targetClass);
							Vector<BaggedEObject> v2 = l2.get(targetClass);
							// common case - links and objects missing in translation
							if (v2 == null) for (int i = 0; i < v1.size(); i++)
							{
								EObject target = v1.get(i).obj;
								boolean bizarre = false;
								recordMissingAssociationOrClass(e1,ef1,target,mismatches,bizarre);								
							}
							// both source and target have the association, but numbers differ
							else if (v1.size() != v2.size())
							{
								EObject target = v1.get(0).obj;
								int nature = SemanticMismatch.SEMANTIC_INCORRECT_LINK_CARDINALITY;
								SemanticMismatch sm = new SemanticMismatch
									(nature,new Integer(v1.size()).toString(),
											new Integer(v2.size()).toString(),
											e1,ef1, target,sourceCode, resultCode);
								storeMismatch(sm,mismatches);															
							}
						}
						for (Enumeration<String> en2 = l2.keys();en2.hasMoreElements();)
						{
							String targetClass = en2.nextElement();
							Vector<BaggedEObject> v1 = l1.get(targetClass);
							Vector<BaggedEObject> v2 = l2.get(targetClass);
							// bizarre case - links and objects missing in source, present in translation
							if (v1 == null) for (int i = 0; i < v2.size(); i++)
							{
								EObject target = v2.get(i).obj;
								boolean bizarre = true;
								recordMissingAssociationOrClass(e1,ef1,target,mismatches,bizarre);								
							}
						}
					}										
				}
			}
		}
		
		
	}

	/**
	 * record that the StructuralFeature ef1 on the EObject e1
	 * has a value in the translation source, but no value in the result
	 * @param e1
	 * @param ef1
	 * @param o1
	 * @param mismatches
	 */
	private void recordMissingFeature(EObject e1, EStructuralFeature ef1,
			Object o1, Hashtable<String, SemanticMismatch> mismatches, boolean bizarre)
	 		throws MapperException
	{
		boolean isAttribute = (ef1 instanceof EAttribute);
		// record a missing attribute
		if (isAttribute)
		{
			int nature = SemanticMismatch.SEMANTIC_MISSING_PROPERTY_VALUE;
			if (bizarre) nature = SemanticMismatch.SEMANTIC_EXTRA_PROPERTY_VALUE;
			SemanticMismatch sm = new SemanticMismatch
				(nature,"","",e1,ef1,null,sourceCode, resultCode);
			sm.checkMissingMapping(sourceReader, resultReader);
			storeMismatch(sm,mismatches);
		}
		
		
		// record one or more missing classes or missing links
		else if ((ef1.getUpperBound() == 1) && (o1 instanceof EObject))
		{
			EObject target = (EObject) o1;
			recordMissingAssociationOrClass(e1,ef1,target,mismatches,bizarre);
		}
		else if ((ef1.getUpperBound() == -1) && (o1 instanceof List<?>))
		{
			for (Iterator<?> it = ((List<?>)o1).iterator();it.hasNext();)
			{
				Object next = it.next();
				if (next instanceof EObject)
				{
					EObject target = (EObject) next;					
					recordMissingAssociationOrClass(e1,ef1,target,mismatches,bizarre);
				}
			}
		}					
	}
	
	/**
	 * record a link or class which is missing (usually) or (if bizarre = true)
	 * extra, in the translation result compared to the source
	 * @param e1 the object owning the link
	 * @param ef1 the structural feature for the link
	 * @param target object at the end of the link
	 * @param mismatches set of SemanticMismatch object to build up
	 * @param bizarre if true, the error is 'extra' rather than 'missing'
	 */
	private void recordMissingAssociationOrClass(EObject e1,
			EStructuralFeature ef1,EObject target,
			Hashtable<String, SemanticMismatch> mismatches, boolean bizarre)
	 		throws MapperException
	 {
		SemanticMismatch sm = null;
		EReference  ref = (EReference)ef1;
		boolean containment = (ref.isContainment());
		/* for containments, record the missing target objects by class; 
		 * for non-containment association, record the missing links
		 * by role name and target class. */
		if (containment)
		{
			int nature = SemanticMismatch.SEMANTIC_MISSING_CLASS;
			if (bizarre) nature = SemanticMismatch.SEMANTIC_EXTRA_CLASS;
			sm = new SemanticMismatch(nature,"","",target,null,null,sourceCode, resultCode);
		}
		else
		{
			int nature = SemanticMismatch.SEMANTIC_MISSING_LINK;
			if (bizarre) nature = SemanticMismatch.SEMANTIC_EXTRA_LINK;
			sm = new SemanticMismatch(nature,"","",e1,ef1,target,sourceCode, resultCode);
		}		
		sm.checkMissingMapping(sourceReader, resultReader);
		storeMismatch(sm,mismatches);			
	}

	
	/**
	 * store a SemanticMismatch - or if it duplicates an
	 * existing one, increase the appropriate occurrence 
	 * count for that mismatch
	 * @param sm
	 * @param mismatches
	 */
	private void storeMismatch(SemanticMismatch sm,
			Hashtable<String, SemanticMismatch> mismatches)
	{
		SemanticMismatch existing = mismatches.get(sm.key());
		if (existing != null)  existing.addOccurrence();
		else mismatches.put(sm.key(), sm);
	}

	
	//-------------------------------------------------------------------------------------
	//                              bag matching
	//-------------------------------------------------------------------------------------
	
	/**
	 * compute the bag match between two EObject structures
	 * @param fBag1
	 * @param fBag2
	 * @return integer bag match score
	 */
	private int bagMatch(Hashtable<String,Integer> fBag1, Hashtable<String,Integer> fBag2)
	{
		trace("bag match " + fBag1.size() + " " + fBag2.size());
		int match = 0;
		for (Enumeration<String> en = fBag1.keys();en.hasMoreElements();)
		{
			String triplet = en.nextElement();
			Integer i2 = fBag2.get(triplet);
			if (i2 != null)
			{
				int occ1 = fBag1.get(triplet).intValue();
				int occ2 = i2.intValue();
				match = match + Math.min(occ1, occ2);
			}
		}
		return match;
	}
	
	/**
	 * For a first match of two EObjects and all their descendants, regard each 
	 * object as a bag  of features. 
	 * This first match is used to pair up child nodes of any node in a full
	 * structure match.
	 * featureBag has key = [feature-stringValue-depth] triplet
	 * and Value = Integer number of occurrences
	 * @param eo any EObject
	 * @param drilldown if true, drill down to child nodes
	 * @return its feature bag
	 */
	private Hashtable<String,Integer> featureBag(EObject eo, boolean drillDown)
	{
		Hashtable<String,Integer> fBag = new Hashtable<String,Integer>();
		buildFeatureBag(fBag,eo,0,drillDown);
		return fBag;
	}
	
	/**
	 * recursive building of a feature bag for an EObject
	 * @param fBag feature bag being built up
	 * @param eo object at this depth of recursion
	 * @param depth depth of nesting in original EObject tree
	 * @param drilldown if true, drill down to child nodes
	 */
	private void buildFeatureBag(Hashtable<String,Integer> fBag, EObject eo, int depth, boolean drillDown)
	{
		for (Iterator<EStructuralFeature> it = eo.eClass().getEAllStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature ef = it.next();
			Object oValue = eo.eGet(ef);
			String fName = ef.getName();
			if ((ef instanceof EAttribute)&& (oValue != null))
			{
				EDataType type = ((EAttribute)ef).getEAttributeType();
				if (type.isSerializable()) // i.e if there is some 'toString' method for the type
				{
					String sValue = stringValue(oValue,type);
					if (sValue != null)
					{
						String triplet = fName + "$" + sValue + "@" + depth;
						addToBag(triplet, fBag);
					}
				}
			}
			else if ((ef instanceof EReference) && (oValue != null))
			{
				EReference er = (EReference)ef;
				if (er.getUpperBound() == -1) // unbounded
				{
					EList<?> values = (EList<?>)oValue;
					for (Iterator<?> iv = values.iterator();iv.hasNext();)
					{
						Object next = iv.next();
						if (next instanceof EObject)
						{
							EObject reffed = (EObject)next;
							String triplet = fName + "$" + reffed.eClass().getName() + "@" + depth;
							addToBag(triplet, fBag);
							// recursion to lower levels of the structure
							if ((er.isContainment()) && drillDown) buildFeatureBag(fBag,reffed,depth + 1,true);							
						}
					}
				}
				else if ((er.getUpperBound() == 1) && 
						(oValue != null) &&
						(oValue instanceof EObject)) // bounded
				{
					EObject reffed = (EObject)oValue;
					String triplet = fName + "$" + reffed.eClass().getName() + "@" + depth;
					addToBag(triplet, fBag);
					if ((er.isContainment()) && drillDown) buildFeatureBag(fBag,reffed,depth + 1,true);							
				}
			}
		}
	}
	
	/**
	 * record a triplet in the feature bag
	 * @param triplet
	 * @param fBag
	 */
	private void addToBag(String triplet, Hashtable<String,Integer> fBag)
	{
		Integer soFar = fBag.get(triplet);
		if (soFar == null) soFar = new Integer(1);
		else soFar = new Integer(soFar.intValue() + 1);
		fBag.put(triplet, soFar);
	}
	
	private String stringValue(Object oValue, EDataType type)
	{
		String sValue = null;
		if (oValue != null)
		{
			if (type.getName().equals("EString")) sValue = (String)oValue;
			else if (type.getName().equals("EInt")) sValue = ((Integer)oValue).toString();
			else if (type.getName().equals("EBoolean")) sValue = ((Boolean)oValue).toString();
			else if (type.getName().equals("EFloat")) sValue = ((Float)oValue).toString();
			else {System.out.println("Unrecognised data type: " + type.getName());}			
		}
		return sValue;
	}
	
	
	//---------------------------------------------------------------------------------------------------------
	//                                Equalising text keys of objects
	//---------------------------------------------------------------------------------------------------------
	
	/**
	 * Some EObjects, made by using mappings to read a CDA XML into Ecore after the XML has been through an in-wrapper transform,
	 * have objects of class 'Text' or 'Text_1' etc, with attributes 'textContent' or 'textContent_1' etc, where the
	 * values of the attributes are keys like 'key_1' etc.
	 * 
	 * These keys are generated by the in-wrapper transform as keys for html-like subtrees (rendered text in the CDA) stored by the wrapper transform.
	 * But the assignment of keys by the wrapper transform is arbitrary, so that two text subtrees in different instances, which 
	 * are identical and should match, get assigned different keys and fail to match.
	 * 
	 * To avoid such failure to match, this method replaces all such key values by a single value, 
	 * so that the matching done by this class will succeed, and can get 100% matches if all other values match.
	 * 
	 * @param eo
	 */
	public static void equaliseTextKeys(EObject eo)
	{
		EClass ec = eo.eClass();
		for (Iterator<EStructuralFeature> it = ec.getEAllStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature ef = it.next();
			
			/* Any attribute whose name begins with 'textContent', of an object whose class name begins with 'Text',
			 * has values like 'Key_5' set by an in-wrapper transform (the value is a key pointing to a text subtree) ,
			 * where the index is determined at random and will fail to match.
			 * Remove the attribute value so there will be no failure (and no spurious extra score) */
			if (ef instanceof EAttribute)
			{
				if ((ec.getName().startsWith("Text")) && (ef.getName().startsWith("textContent"))) 
					eo.eUnset(ef);
			}

			// recurse downward through containment relations
			else if (ef instanceof EReference)
			{
				Object val = eo.eGet(ef);
				EReference er = (EReference)ef;
				if (er.isContainment())
				{
					// follow to the single EObject at the end of the association
					if (er.getUpperBound() == 1)
					{
						if (val instanceof EObject) equaliseTextKeys((EObject)val);
					}

					// follow to multiple EObjects at the end of the association
					else if (er.getUpperBound() == -1)
					{
						if (val instanceof List<?>)
							for (Iterator<?> iu = ((List<?>)val).iterator();iu.hasNext();)
							{
								Object next = iu.next();
								if (next instanceof EObject) equaliseTextKeys((EObject)next);
							}						
					}					
				}
			}
		}
	}

	
	
	private void trace(String s) {if (tracing) System.out.println(s);}

}
