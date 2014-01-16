package com.openMap1.mapper.health.cda;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;

import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;

/**
 * This class defines one step in the path defining the context of a CDA
 * template - i.e one step in the chain of associations from the top 
 * class 'ClinicalDocument' of CDA. It includes the templates on the step.
 * 
 * @author robert
 *
 */
public class ContextStep {
	
	/**
	 * @return name of the association leading to this class;
	 * or null for the first step to ClinicalDocument
	 */
	public String associationName() {return associationName;}
	private String associationName;
	
	/**
	 * @return name of the class, not qualified with a package name
	 */
	public String className() {return className;}
	private String className;
	
	public String stepName()
	{
		if (associationName != null) return associationName;
		return className;
	}
	
	/**
	 * @return Vector of template ids that are required for this step
	 */
	public Vector<String>  templateIds() {return templateIds;}
	private Vector<String>  templateIds;
	
	/**
	 * @return each fixed value is an array {attribute name, required value}
	 * for the instance of the class (could have used a Hashtable, stupid)
	 */
	public Vector<String[]> fixedValues() {return fixedValues;}
	private Vector<String[]> fixedValues;
	
	//------------------------------------------------------------------------------------------------
	//                                           constructor
	//------------------------------------------------------------------------------------------------
	
	public ContextStep(String associationName, String className)
	{
		this.associationName =associationName;
		this.className = className;
		templateIds = new Vector<String>();
		fixedValues = new Vector<String[]>();
	}
	
	//------------------------------------------------------------------------------------------------
	//                                          
	//------------------------------------------------------------------------------------------------
	
	/**
	 * @param templateId note that this template id is required on this step of the path
	 */
	public void addTemplateId(String templateId)
	{
		templateIds.add(templateId);
	}
	
	/**
	 * @param attName an attribute name
	 * @return its fixed value if it has one; otherwise null
	 */
	public String getFixedValue(String attName)
	{
		String value = null;
		for (Iterator<String[]> it = fixedValues.iterator(); it.hasNext();)
		{
			String[] fixedValue = it.next();
			if (fixedValue[0].equals(attName)) value = fixedValue[1];
		}
		return value;
	}
	
	/**
	 * 
	 * @param attName
	 * @param value
	 * If no fixed value has yet been set for the attribute, set it and return true.
	 * If a value has already been set, ignore the new value and return false.
	 */
	public boolean setFixedValue(String attName, String value)
	{
		boolean found = false;
		for (Iterator<String[]> it = fixedValues.iterator(); it.hasNext();)
			if (it.next()[0].equals(attName)) found = true;
		if (!found)
		{
			String[] newValue = new String[2];
			newValue[0] = attName;
			newValue[1] = value;
			fixedValues.add(newValue);
		}
		return !found;
	}

	
	
	/**
	 * @param attName an attribute name
	 * @param attValue a fixed value of the attribute
	 * @return true if the fixed value conflicts with the existing fixed value
	 */
	public boolean fixedValueConflict(String attName, String attValue)
	{
		boolean conflict = false;
		for (Iterator<String[]> it = fixedValues.iterator(); it.hasNext();)
		{
			String[] fixedValue = it.next();
			if ((fixedValue[0].equals(attName)) && (!fixedValue[1].equals(attValue)))
				conflict = true;
		}
		return conflict;
	}
	
	
	/**
	 * @return a clone of this ContextStep
	 */
	public ContextStep clone()
	{
		ContextStep clone = new ContextStep(associationName, className);
		for (int i = 0; i < fixedValues.size(); i++) 
			clone.setFixedValue(fixedValues.get(i)[0], fixedValues.get(i)[1]);
		for (int i = 0; i < templateIds.size(); i++)
			clone.addTemplateId(templateIds.get(i));
		return clone;
	}
	
	/**
	 * @return String form of the step, for writing out
	 */
	public String stringForm()
	{
		String stringForm = associationName;
		if (stringForm == null) stringForm = className;
		for (int i = 0; i < fixedValues.size(); i++)
		{
			String[] fv = fixedValues.get(i);
			stringForm = stringForm + "[" + fv[0] + "='" + fv[1] + "']";
		}
		if (templateIds.size() > 0)
		{
			stringForm = stringForm + "{";
			for (int i = 0; i < templateIds.size(); i++)
			{
				stringForm = stringForm + templateIds.get(i);
				if (i < templateIds.size() -1) stringForm = stringForm + ",";
			}
			stringForm = stringForm + "}";
		}
		return stringForm;
	}
	
	public String shortStringForm()
	{
		String res = associationName;
		if (templateIds.size() > 0) res = res + "[" + templateIds.size() + "]";
		return res;
	}
	
	/**
	 * some step from another context is compatible with this step if:
	 * (a) The step name (association name or class name) matches
	 * (b) All the template ids required by this template's context are supplied by the context
	 * (c) All the fixed values required by this template's context are supplied by the context
	 * The other context step may have more template ids and fixed values.
	 * @param other the other context step
	 * @return true if it is compatible with this one
	 */
	public boolean compatibleStep(ContextStep other)
	{
		if (other.stepName().equals(stepName()))
		{
			boolean compatible = true;
			for (int i = 0; i < templateIds.size();i++)
				if (!GenUtil.inVector(templateIds.get(i), other.templateIds())) compatible = false;
			for (int i = 0; i < fixedValues.size(); i++)
			{
				String[] fv = fixedValues.get(i);
				if (!other.hasFixedValue(fv)) compatible = false;
			}
			return compatible;
		}
		return false;
	}
	
	/**
	 * 
	 * @param other
	 * @return true if the other step is equal to this in association name, template ids and fixedvalues
	 */
	public boolean equalStep(ContextStep other)
	{
		return ((compatibleStep(other)) && (other.compatibleStep(this)));
	}
	
	/**
	 * 
	 * @param fixVal a fixed value pair {attribute name, value}
	 * @return true if this step has the same value for the same attribute
	 */
	public boolean hasFixedValue(String[] fixVal)
	{
		boolean hasValue = false;
		for (int i = 0; i < fixedValues.size(); i++)
		{
			String[] fv = fixedValues.get(i);
			if ((fv[0].equals(fixVal[0])) && (fv[1].equals(fixVal[1]))) hasValue = true;
		}
		return hasValue;
	}
	
	
	/**
	 * @param step a ContextStep leading to an EClass
	 * @param eClass the EClass at the end of the step
	 * set all the fixed attribute values of the EClass on the ContextStep
	 */
	public void setFixedValues(EClass eClass)
	{
		for (Iterator<EAttribute> ia = eClass.getEAllAttributes().iterator();ia.hasNext();)
		{
			EAttribute att = ia.next();
			String attName = att.getName();
			String fixedValue = ModelUtil.getEAnnotationDetail(att,"fixed value");
			if (fixedValue != null) setFixedValue(attName, fixedValue);
		}		
	}

	
	
	/**
	 * @param templateId
	 * @return true if this step contains the context
	 */
	public boolean containsTemplate(String templateId)
	{
		return GenUtil.inVector(templateId, templateIds);
	}
	



}
