package com.openMap1.mapper.structures;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import com.openMap1.mapper.util.ModelUtil;
/**
 * Class recording an actual association that is mappable,
 * noting the actual classes at both ends, which have class mappings and which may be 
 * different from the classes the association is inherited from
 */

public class MappableAssociation {
	
	/**
	 * if the EReference which is the basis of this association has no EOpposite, 
	 * then the role name which corresponds to no EReference (i.e to navigate to the class
	 * which, according to the Ecore model, you cannot navigate to) is this role name.
	 * This is always end 2 of the association; the role which points to end 1 is navigable,
	 * The association name is the same as the navigable role name
	 */
	public static String NON_NAVIGABLE_ROLE_NAME = "";

	private EClass thisEndClass;
	/**
	 * the class at one end of the association 
	 * that was selected in the mapped class model view when the action
	 * to create the association mapping was made
	 */ 
	public EClass thisEndClass() {return thisEndClass;}
	
	private String thisEndSubset;
	
	/**
	 * @return the mapped subset of the selected class in the class model view
	 */
	public String thisEndSubset() {return thisEndSubset;}

	private EClass otherEndClass;
	/**
	 * the class at one end of the association 
	 * - the end whose class was not the one selected in the mapped class model view when the action
	 * to create the association mapping was made
	 */ 
	public EClass otherEndClass() {return otherEndClass;}
	
	private String otherEndSubset;
	
	/**
	 * @return the mapped subset of class which is not the selected class in the class model view
	 */
	public String otherEndSubset() {return otherEndSubset;}

	private EReference thisEndReference;
	/**
	 * the eReference from the class at one end of the association 
	 * - the class that was selected in the mapped class model view when the action
	 * to create the association mapping was made. This is the EReference to 
	 * get to the class at the other end, and is never null
	 */ 
	public  EReference thisEndReference() {return thisEndReference;}

	/**
	 * the eReference from the class at one end of the association 
	 * - the end whose class was not the one selected in the mapped class model view when the action
	 * to create the association mapping was made
	 */ 
	public  EReference otherEndReference() {return thisEndReference.getEOpposite();}
	
	private boolean makeRequiredForThisEnd;
	
	public boolean requiredForEnd(int end)
	{
		boolean required = false;
		if (end == thisEndIndex()) required = makeRequiredForThisEnd;
		return required;
	}
	
	//------------------------------------------------------------------------------------
	//                                   constructor
	//------------------------------------------------------------------------------------
	
	public MappableAssociation(EClass thisEndClass, String thisEndSubset,
			EClass otherEndClass,String otherEndSubset, EReference thisEndReference, 
			boolean makeRequiredForThisEnd)
	{
		this.thisEndClass = thisEndClass;
		this.thisEndSubset = thisEndSubset;
		this.otherEndClass = otherEndClass;
		this.otherEndSubset= otherEndSubset;
		this.thisEndReference = thisEndReference;
		this.makeRequiredForThisEnd = makeRequiredForThisEnd;
	}
	
	/**
	 * Index 1 or 2 for 'this' end of the association - 
	 * the end of the class selected in the class model view
	 */
	private int thisEndIndex()
	{
		/* If there is no EReference back from the other end, the other end is end 1 
		 * so this end is end 2 */
		int thisEnd = 2;
		if (otherEndReference() != null)
		{
			// end 1 is the end whose role name is lexically first
			if (thisEndReference().getName().compareTo(otherEndReference().getName()) < 0) thisEnd = 1;
			// compareTo: The result is a negative integer if this String object lexicographically precedes the argument string
		}
		return thisEnd;
	}
	
	/**
	 * Index 1 or 2 for the 'other' end of the association - not the end of the class selected in the class model view
	 */
	private int otherEndIndex() {return (3 - thisEndIndex());}
	
	/**
	 * @return the name of the association, made by the rules:
	 * (1) if there is only one EReference, with no opposite, the name is the role name of the EReference
	 * (2) if there are two  role names <role1> and <role2>, the association name is '<role1>|<role2>', except that
	 * (3) (legacy) if the role names differ only by a final '_1' and '_2', 
	 * the association name is the first role name without the final '_1'
	 * 
	 */
	public String associationName()
	{
		if (otherEndReference() != null)
		{
			String n1 = endRef(1).getName();
			String n2 = endRef(2).getName();
			return ModelUtil.assocName(n1, n2);
		}
		// if there is no opposite reference, the association name is the one role name
		return thisEndReference().getName();
	}
	
	/**
	 * the EReference from one end (= 1 or 2) to the other
	 * @param end
	 * @return the EReference - may be null for a non-navigable association
	 */
	public EReference endRef(int end)
	{
		EReference ref = null;
		if ((end < 1)|(end > 2)) System.out.println("Invalid end for EReference: " + end);
		else if (end == thisEndIndex()) ref = thisEndReference;
		else if (end == otherEndIndex()) ref = otherEndReference();
		return ref;
	}
	
	/**
	 * @param end
	 * @return the role name for a role pointing _from_ a defined end of the association
	 */
	public String roleName(int end)
	{
		String name = "";
		if ((end < 1)|(end > 2)) System.out.println("Invalid end for role name: " + end);
		else if (end == thisEndIndex()) name = thisEndReference.getName();
		// the role pointing from the other end to this end might be non-navigable
		else if (end == otherEndIndex())
		{
			name = NON_NAVIGABLE_ROLE_NAME;
			if (otherEndReference() != null) name = otherEndReference().getName();
		}
		return name;		
	}
	
	/**
	 * the EClass pointed _to_ at one end of the association
	 * (Note the EReference at end 1 belongs to the EClass at end 1,
	 *  but points to the EClass at end 2)
	 * @param end = 1 or 2
	 * @return the mapped subset of the EClass pointed to by that EReference
	 */
	public String getSubset(int end)
	{
		String subset = "";
		if ((end < 1)|(end > 2)) System.out.println("Invalid end for subset name: " + end);
		// note the reversal of this and other ends, in the class pointed at
		else if (end == thisEndIndex()) subset = otherEndSubset;
		else if (end == otherEndIndex()) subset = thisEndSubset;
		return subset;				
	}
	
	/**
	 * the EClass pointed _to_ at one end of the association
	 * (Note the EReference at end 1 belongs to the EClass at end 1,
	 *  but points to the EClass at end 2)
	 * @param end = 1 or 2
	 * @return the EClass pointed to by that EReference
	 */
	public EClass endClass(int end)
	{
		EClass c = null;
		if ((end < 1)|(end > 2)) System.out.println("Invalid end for EClass: " + end);
		// note the reversal of this and other ends, in the class pointed at
		else if (end == thisEndIndex()) c = otherEndClass;
		else if (end == otherEndIndex()) c = thisEndClass;
		return c;
	}
	
	/**
	 * @return a label for the association to be mapped - which is  
	 * of the form [thisClass]thisRole[otherEndClass]. 
	 * ThisClass is the class selected in the mapped class model view.
	 */
	public String associationMenuLabel()
	{
		String thisEnd = thisEndClass.getName();
		if (!thisEndSubset.equals("")) thisEnd = thisEnd + "(" + thisEndSubset + ")";
		String otherEnd = otherEndClass.getName();
		if (!otherEndSubset.equals("")) otherEnd = otherEnd + "(" + otherEndSubset + ")";
		return ("[" + thisEnd + "]" 
		+ thisEndReference.getName() 
		+ "[" + otherEnd + "]");
	}

}
