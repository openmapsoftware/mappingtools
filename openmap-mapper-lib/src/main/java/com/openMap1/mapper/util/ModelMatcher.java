package com.openMap1.mapper.util;

import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.common.util.EList;

/**
 * Methods in this class compare two EMF model structures to find discrepancies.
 * Generally the methods a pragmatic rough match of two structures, 
 * so as to point out the most useful discrepancies even if they do not match exactly
 * 
 * @author robert
 *
 */
public class ModelMatcher {
	
	static boolean sameClass(EObject obj1, EObject obj2)
		{return (obj1.eClass().getName().equals(obj2.eClass().getName()));}
	
	static int shallowMatch(EObject obj1, EObject obj2)
	{
		// of two EObjects are not of the same class, return a huge discrepancy
		if (!sameClass(obj1,obj2)) return 100;

		int match = 0;
		
		// accumulate discrepancies of EAttribute values
		for (Iterator<EAttribute> it = obj1.eClass().getEAllAttributes().iterator();it.hasNext();)
		{
			EAttribute att = it.next();
			if (obj1.eGet(att) == null)
			{
				if (obj2.eGet(att) != null)	match++;			
			}
			else 
			{
				String v1 = stringValue(att,obj1.eGet(att));
				String v2 = stringValue(att,obj2.eGet(att));
				if (!v1.equals(v2)) match++;
			}

		}
		
		// accumulate discrepancies of number of eReference targets, by class
		for (Iterator<EReference> it = obj1.eClass().getEReferences().iterator();it.hasNext();)
		{
			EReference ref = it.next();
			// find mismatches of target class or existence for any single-valued reference
			if (ref.getUpperBound() == 1)
			{
				if ((obj1.eGet(ref) == null) && (obj2.eGet(ref) != null)) match++;			
				if ((obj1.eGet(ref) != null) && (obj2.eGet(ref) == null)) match++;			
				if ((obj1.eGet(ref) != null) && (obj2.eGet(ref) != null)) 
				{
					EObject c1 = (EObject)obj1.eGet(ref);
					EObject c2 = (EObject)obj2.eGet(ref);
					if (!sameClass(c1,c2)) match++;					
				}
			}
			// count target objects of the reference by class for each owing object
			else if (ref.getUpperBound() == -1)
			{
				Hashtable<String,Vector<EObject>> h1 = targetsByClass(obj1,ref);
				Hashtable<String,Vector<EObject>> h2 = targetsByClass(obj2,ref);
				match = match + imbalancesByClass(h1,h2);
			}
		}
		return match;
	}
	
	/**
	 * sort the target objects of a multi-valued feature into objects of the same class
	 * @param owner the owning object
	 * @param ref the feature
	 * @return key = class name; value = Vector of target objects of that class
	 */
	private static Hashtable<String,Vector<EObject>> targetsByClass(EObject owner, EReference ref)
	{
		Hashtable<String,Vector<EObject>> res = new Hashtable<String,Vector<EObject>>();
		Object feature = owner.eGet(ref);
		if (feature instanceof EList<?>)
		{
			EList<?> lf = (EList<?>)feature;
			for (Iterator<?> it = lf.iterator();it.hasNext();)
			{
				Object next = it.next();
				if (next instanceof EObject)
				{
					EObject eo = (EObject)next;
					String className = eo.eClass().getName();
					Vector<EObject> soFar = res.get(className);
					if (soFar == null) soFar = new Vector<EObject>();
					soFar.add(eo);
					res.put(className, soFar);
				}
			}
		}
		return res;
	}
	
	/**
	 * count the discrepancies in number of target object by class
	 * @param h1 target objects of a feature in one object, grouped by class
	 * @param h2 target objects of the same feature in another object, grouped by class
	 * @return
	 */
	private static int imbalancesByClass(Hashtable<String,Vector<EObject>> h1,
			Hashtable<String,Vector<EObject>> h2)
	{
		int score = 0;

		// classes present in h1, may be absent from h2
		for (Iterator<String> it = h1.keySet().iterator();it.hasNext();)
		{
			String className = it.next();
			Vector<EObject> o1 = h1.get(className);
			Vector<EObject> o2 = h2.get(className);
			if (o2 == null) score = score + o1.size(); // none in h2
			if (o1.size() > o2.size()) score = score + o1.size() - o2.size();
			if (o2.size() > o1.size()) score = score + o2.size() - o1.size();
		}

		// classes present in h2, absent from h1
		for (Iterator<String> it = h2.keySet().iterator();it.hasNext();)
		{
			String className = it.next();
			Vector<EObject> o2 = h2.get(className);
			if (h1.get(className) == null) score = score + o2.size(); // none in h1
		}
		return score;
	}
	
	private static String stringValue(EAttribute att,Object value)
	{
		String sVal = "";
		if (value == null) return sVal;
		if (att.getEType().getName().equals("EString")) 
			sVal = (String)value;
		else if (att.getEType().getName().equals("EInt")) 
			sVal = value.toString();
		else System.out.println("New data type '" + att.getEType().getName()
				+ "' of attribute '" + att.getName() + "'" );
		return sVal;
	}
	
	/* private static int[] bagMatch(EObject obj1, EObject obj2)
	{
		// to placate the compiler
		int[] dummy = new int[1];
		dummy[0] = 0;
		return dummy;
	} */
	
	class ValueTriple{

		String path; // path of class names of objects down from root
		String attName; // name of the attribute
		String stringValue; // value of the attribute, converted to a String
		
		ValueTriple(String path, EAttribute att, Object value)
		{
			this.path = path;
			this.attName = att.getName();
			stringValue = stringValue(att,value);
		}
		
		// key for hashed storage
		String key() {return (path + "_" + attName + "_" + stringValue);}
		
		// depth = length of path -1
		int depth()
			{return (new StringTokenizer(path,"/").countTokens() -1);}
	}
	

}
