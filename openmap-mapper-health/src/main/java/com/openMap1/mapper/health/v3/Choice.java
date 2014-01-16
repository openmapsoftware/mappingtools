package com.openMap1.mapper.health.v3;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.ecore.EClass;

/**
 * A choice node in a V3 RMIM
 * 
 * @author robert
 *
 */
public class Choice extends V3Name{
	
	private Vector<String> choiceNames;
	
	private V3RMIM v3RMIM;
	
	public Choice(String name, V3RMIM v3RMIM)
	{
		super(name);
		this.v3RMIM = v3RMIM;
		choiceNames = new Vector<String>();
	}
	
	public void addItem(String itemName)
	{
		choiceNames.add(itemName);
	}
	
	public Iterator<V3Name> getItems()
	{
		Vector<V3Name> items = new Vector<V3Name>();
		for (int i = 0; i < choiceNames.size(); i++)
		{
			V3Name item = v3RMIM.getV3Name(choiceNames.get(i));
			if (item != null) items.add(item);
		}
		return items.iterator();
	}
	
	/**
	 * @return all EClasses nested directly or indirectly in this Choice
	 */
	public List<EClass> getAllEClasses()
	{
		Vector<EClass> classes = new Vector<EClass>();
		for (Iterator<V3Name> it = getItems();it.hasNext();)
		{
			List<EClass> partial = it.next().getAllEClasses();
			for (Iterator<EClass> ip = partial.iterator();ip.hasNext();)
				classes.add(ip.next());
		}
		return classes;
	}

	/**
	 * @return all ConcreteClasses nested directly or indirectly in this V3Name
	 */
	public List<ConcreteClass> getAllConcreteClasses()
	{
		Vector<ConcreteClass> classes = new Vector<ConcreteClass>();
		for (Iterator<V3Name> it = getItems();it.hasNext();)
		{
			List<ConcreteClass> partial = it.next().getAllConcreteClasses();
			for (Iterator<ConcreteClass> ip = partial.iterator();ip.hasNext();)
				classes.add(ip.next());
		}
		return classes;				
	}

	/**
	 * @return the number of items at the top level in this V3Name
	 */
	public int nItems() {return choiceNames.size();}


	/**
	 * @param name
	 * @return the V3Name child with that name, or null if there is none; 
	 * allow CMET references to match on the entry name as well as the CMET name
	 */
	public V3Name getNamedChild(String name)
	{
		V3Name child = null;
		
		for (int i = 0; i < choiceNames.size(); i++)
		{
			if (choiceNames.get(i).equals(name)) 
				child = v3RMIM.getV3Name(name);
		}
		// allow CMET references to match on the entry name as well as the CMET name
		for (Iterator<V3Name> it = getItems(); it.hasNext();)
		{
			V3Name candidate = it.next();
			if ((candidate instanceof CMETReference) &&
				(((CMETReference)candidate).getEntryName().equals(name)))
					child = candidate;
		}
		return child;
	}
	
	public String stringForm() 
	{
		String choices = "";
		for (int i = 0; i < choiceNames.size(); i++)
			choices = choices + "'" + choiceNames.get(i) + "' ";
		return ("Choice '" + name + "': " + choices);
	}

}
