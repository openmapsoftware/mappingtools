package com.openMap1.mapper.health.v3;

import java.util.List;

import org.eclipse.emf.ecore.EClass;

/**
 * A reference to a CMET in a V3 RMIM
 * 
 * @author robert
 *
 */
public class CMETReference extends V3Name{
	
	private V3RMIM cmet;
	
	public CMETReference(String name, V3RMIM cmet)
	{
		super(name);
		this.cmet = cmet;
	}

	/**
	 * @return all EClasses nested directly or indirectly in this CMETReference
	 */
	public List<EClass> getAllEClasses()
	{
		return cmet.getEntryV3Name().getAllEClasses();
	}

	/**
	 * @return all ConcreteClasses nested directly or indirectly in this V3Name
	 */
	public List<ConcreteClass> getAllConcreteClasses()
	{
		return cmet.getEntryV3Name().getAllConcreteClasses();		
	}

	/**
	 * @return the number of items at the top level in this V3Name
	 */
	public int nItems() {return cmet.getEntryV3Name().nItems();}

	/**
	 * @param name
	 * @return the V3Name child with that name, or null if there is none
	 */
	public V3Name getNamedChild(String name) {return cmet.getEntryV3Name().getNamedChild(name);}
	
	public String getEntryName() {return cmet.getEntryV3Name().name();}
	
	public String stringForm() {return ("CMET ref with entry class " + cmet.getEntryV3Name());}

}
