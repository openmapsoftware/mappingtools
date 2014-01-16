package com.openMap1.mapper.health.cda;

import java.util.Vector;

import com.openMap1.mapper.health.v3.RMIMReader;
import com.openMap1.mapper.util.GenUtil;

/**
 * represents a part of a template assertion 
 * that fixes the value of some attribute
 * 
 * @author robert
 *
 */

public class AttributeValueConstraint {
	
	/**
	 * @return the fixed value of the attribute
	 */
	public String value() {return value;}
	private String value;
	
	/**
	 * @return the attribute name
	 */
	public String attName() {return attName;}
	public void setAttName(String attName) {this.attName = attName;}
	private String attName;
	
	/**
	 * 
	 */
	public Vector<String> associationPath() {return associationPath;}
	private Vector<String> associationPath;
	
	public AttributeValueConstraint(Vector<String> associationPath,String value)
	{
		// clone the path in case it gets changed later (which can happen)
		this.associationPath = new Vector<String>();
		for (int i = 0; i < associationPath.size(); i++) addAssociationStep(associationPath.get(i));

		this.value = value;
	}
	
	public void addAssociationStep(String step)
	{
		String bareStep = step;
		if (step.startsWith(RMIMReader.CDAPREFIX)) bareStep = step.substring(RMIMReader.CDAPREFIX.length() + 1);
		associationPath.add(bareStep);
	}
	
	/**
	 * @return String form of the constraint, for diagnostic prints
	 */
	public String stringForm()
	{
		return (path() + "='" + value + "'");
	}
	
	/**
	 * @return the XPath to the constrained attribute
	 */
	public String path()
	{
		String path = "";
		for (int i = 0; i < associationPath.size(); i++) 
			{path = path + associationPath.get(i)  + "/";}
		path = path + "@" + attName;
		return path;
	}
	
	/**
	 * @return true if the path involves a 'templateId' node
	 * (these should not be put as EAnnotations on the EClass, as
	 * they should not lead to fixed-value mappings)
	 */
	public boolean templateIdInPath()
	{
		return GenUtil.inVector("templatId", associationPath);
	}

}
