package com.openMap1.mapper.health.cda;

import java.util.Iterator;
import java.util.Vector;

/**
 * A TemplatedPath is a path of steps from the root class ClinicalDocument
 * to the class/node holding a template; for each step of the path,
 * if there are any other templates on the node, they are noted. 
 * 
 * @author robert
 *
 */
public class TemplatedPath {
	
	public Vector<ContextStep> steps() {return steps;}
	private Vector<ContextStep> steps;
	
	public int length() {return steps.size();}
	
	public ContextStep step(int i) {return steps.get(i);}
	
	public TemplatedPath()
	{
		steps = new Vector<ContextStep>();
	}
	
	/**
	 * @param step inner step to be added to this context
	 */
	public TemplatedPath addStep(ContextStep step)
	{
		steps.add(step);
		return this;
	}
	
	/**
	 * @return a clone of this context
	 */
	public TemplatedPath clone()
	{
		TemplatedPath clone = new TemplatedPath();
		for (int i = 0; i < steps.size(); i++)
			clone.addStep(steps.get(i).clone());
		return clone;
	}
	
	/**
	 * 
	 * @param cName the name of an EClass
	 * @return true if this context contains the class at any step
	 */
	public boolean containsClass(String cName)
	{
		boolean contains = false;
		for (int i = 0; i < steps.size(); i++)
			if (steps.get(i).className().equals(cName)) contains = true;
		return contains;
	}
	
	/**
	 * 
	 * @param cName the name of an EClass
	 * @return the number of steps that contain the class
	 */
	public int classCount(String cName)
	{
		int count = 0;
		for (int i = 0; i < steps.size(); i++)
			if (steps.get(i).className().equals(cName)) count++;
		return count;
	}
	
	/**
	 * @return string form of the context , for writing out
	 */
	public String stringForm()
	{
		String sf = "";
		for (int i = 0; i < steps.size(); i++) sf = sf + "/" + steps.get(i).stringForm();
		return sf;
	}
	
	/**
	 * @return short string form of the context , for writing out
	 */
	public String shortStringForm()
	{
		String sf = "";
		for (int i = 0; i < steps.size(); i++) sf = sf + "/" + steps.get(i).shortStringForm();
		return sf;
	}
	
	/**
	 * @param other another CDAContext
	 * @return true if the other context has at least as many steps as
	 * this one, and all the steps match in step name.
	 */
	public boolean matches(TemplatedPath other)
	{
		boolean matches = false;
		if (other.length() > length() - 1)
		{
			matches = true;
			for (int i = 0; i < length(); i++)
				if (!other.step(i).stepName().equals(step(i).stepName())) matches = false;
		}
		return matches;
	}
	
	/**
	 * 
	 * @param templateId
	 * @return true if this context contains the template on any step
	 */
	public boolean containsTemplate(String templateId)
	{
		for (int i = 0; i < steps.size();i++) if (step(i).containsTemplate(templateId)) return true;
		return false;
	}
	
	/**
	 * 
	 * @param other
	 * @return true if this context and the other c0ontext are equal in all their steps
	 */
	public boolean equalContext(TemplatedPath other)
	{
		boolean equal = false;
		if (length() == other.length())
		{
			equal = true;
			for (int i = 0; i < length(); i++)
				if (!step(i).equalStep(other.step(i))) equal = false;
		}
		return equal;
	}
	
	/**
	 * 
	 * @param others
	 * @return true if this context is one of the others
	 */
	public boolean inContexts(Vector<TemplatedPath> others)
	{
		boolean among = false;
		for (Iterator<TemplatedPath> it = others.iterator();it.hasNext();)
			if (equalContext(it.next())) among = true;
		return among;
	}

}
