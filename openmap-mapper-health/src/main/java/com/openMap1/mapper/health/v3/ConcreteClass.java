package com.openMap1.mapper.health.v3;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.ecore.EClass;

import com.openMap1.mapper.util.GenUtil;

/**
 * declaration of a concrete class (i.e not a choice or a CMET reference)
 * in a V3 RMIM
 * 
 * @author robert
 *
 */
public class ConcreteClass extends V3Name {
	
	private EClass eClass;
	public EClass eClass() {return eClass;}

	public ConcreteClass(String name, EClass eClass)
	{
		super(name);
		this.eClass = eClass;
	}

	/**
	 * @return a List of the one EClass
	 */
	public List<EClass> getAllEClasses()
	{
		Vector<EClass> classes = new Vector<EClass>();
		classes.add(eClass);
		return classes;
	}

	/**
	 * @return all ConcreteClasses nested directly or indirectly in this V3Name
	 */
	public List<ConcreteClass> getAllConcreteClasses()
	{
		Vector<ConcreteClass> classes = new Vector<ConcreteClass>();
		classes.add(this);
		return classes;		
	}

	
	/**
	 * @return the number of items at the top level in this V3Name
	 */
	public int nItems() {return 1;}

	/**
	 * @param name
	 * @return the V3Name child with that name, or null if there is none
	 */
	public V3Name getNamedChild(String name) {return null;}
	
	public String stringForm() 
	{
		if (templateIds.size() == 0) return ("Class " + eClass.getName());
		return ("Class " + eClass.getName()) + "; templates: " + GenUtil.concatenate(templateIds, ",");
	}
	
	//--------------------------------------------------------------------------------------------------------------
	//                             Features used only for NHS Templated RMIMs
	//--------------------------------------------------------------------------------------------------------------
	
	private Vector<String> templateIds = new Vector<String>();

	private Hashtable<String,EClass> templateClones = new Hashtable<String,EClass>();
	
	private Hashtable<String,String> templateNameSuffixes = new Hashtable<String,String>();
	
	/**
	 * For use with NHS MIF files only
	 * @return the RMIM ids of templates invoked by this class
	 * (which must be an ActRelationship or a Participation)
	 */
	public Vector<String> templateNames() {return templateIds;}
	
	public void setTemplateIds(Vector<String> templateIds) {this.templateIds = templateIds;}
	
	/**
	 * @param templateName
	 * @return the clone of this class made for a template, with class name 
	 * modified by the temple entry class name
	 */
	public EClass getTemplateClone(String templateName)  {return templateClones.get(templateName);}
	
	/**
	 * 
	 * store the clone of this class made for a template, with class name 
	 * modified by the temple entry class name
	 * @param templateName
	 * @param clone
	 */
	public void addTemplateClone(String templateName, EClass clone) {templateClones.put(templateName,clone);}
	
	/**
	 * @param templateName
	 * @return the suffix to add to names of the template clone class, and to the name of the
	 * association to it, for the template
	 */
	public String getTemplateNameSuffix(String templateName) {return templateNameSuffixes.get(templateName);}
	
	/**
	 * store the suffix to add to names of the template clone class, and to the name of the
	 * association to it, for the template
	 * @param templateName
	 * @param suffix
	 */
	public void addTemplateNameSuffix(String templateName, String suffix) {templateNameSuffixes.put(templateName, suffix);}

}
