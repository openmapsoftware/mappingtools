package com.openMap1.mapper.health.v3;

import java.util.List;

import org.eclipse.emf.ecore.EClass;

/**
 * superclass for classes Choice, CMETReference, and ConcreteClass, 
 * which can be represented agt the same place in a MIF file
 * @author robert
 *
 */
abstract public class V3Name {
	
	protected String name;
	public String name() {return name;}
	
	public V3Name(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return all EClasses nested directly or indirectly in this V3Name
	 */
	abstract public List<EClass> getAllEClasses();
	
	/**
	 * @return all ConcreteClasses nested directly or indirectly in this V3Name
	 */
	abstract public List<ConcreteClass> getAllConcreteClasses();
	
	/**
	 * @return the number of items at the top level in this V3Name
	 */
	abstract public int nItems();
	
	/**
	 * @param name
	 * @return the V3Name child with that name, or null if there is none
	 */
	abstract V3Name getNamedChild(String name);
	
	/**
	 * string form to be written out
	 * @return
	 */
	abstract String stringForm();

}
