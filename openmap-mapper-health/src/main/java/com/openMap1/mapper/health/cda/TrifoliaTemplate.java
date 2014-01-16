package com.openMap1.mapper.health.cda;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XMLUtil;

public class TrifoliaTemplate {
	
	/**
	 * @return the oid identifier of the template
	 */
	public String oid() {return oid;}
	private String oid;
	
	/**
	 * @return the oid identifier of the C-CDA template which this template further constrains,
	 * or "" if there is none
	 */
	public String impliedTemplateOid() {return impliedTemplateOid;}
	private String impliedTemplateOid;
	
	/**
	 * @return true if this is an open template
	 */
	public boolean isOpen() {return isOpen;}
	private boolean isOpen = false;
	
	/**
	 * @return the template type - which can be 'document' 'section' or 'entry'
	 */
	public String templateType() {return templateType;}
	private String templateType;

	/**
	 * @return the name of the node which the templateId element appears under
	 */
	public String context() {return context;}
	private String context;
	
	/**
	 * @return a string similar to the title, but with underscores in stead of spaces
	 */
	public String bookmark() {return bookmark;}
	private String bookmark;
	
	/**
	 * @return human-readable title of the template
	 */
	public String title() {return title;}
	private String title;
	
	/**
	 * @return true if this template has already been handled (templated classes created for it)
	 */
	public boolean isHandled() {return handled;}
	private boolean handled = false;
	public void setHandled(boolean handled) {this.handled = handled;}


/**
 * @return List of template constraints directly inside the template
 */
public List<TrifoliaConstraint> getConstraints() {return constraints;}
private Vector<TrifoliaConstraint> constraints = new Vector<TrifoliaConstraint>();


	
	public TrifoliaTemplate(Element templateEl) throws MapperException
	{
		oid = templateEl.getAttribute("oid");
		impliedTemplateOid = templateEl.getAttribute("impliedTemplateOid");
		isOpen = (templateEl.getAttribute("isOpen").equals("true"));
		templateType = templateEl.getAttribute("templateType");
		context = templateEl.getAttribute("context");
		bookmark = templateEl.getAttribute("bookmark");
		title = templateEl.getAttribute("title");
		
		
		// constraints directly inside the template
		Vector<Element> nestedEls = XMLUtil.namedChildElements(templateEl, "Constraint");
		for (int i = 0; i < nestedEls.size();i++)
			constraints.add(new TrifoliaConstraint(nestedEls.get(i),0,""));

	}
	
	/**
	 * @return Hashtable with key = nested template id; value = path to that template
	 */
	public Hashtable<String,String> nestedTemplates()
	{
		Hashtable<String,String> templates = new Hashtable<String,String>();
		for (Iterator<TrifoliaConstraint> it = getConstraints().iterator();it.hasNext();)
			addNestedTemplate(templates,it.next());		
		return templates;
	}
	
	/**
	 * recursive search of all constraints, to find nested templates
	 * @param templates
	 * @param constraint
	 */
	private void addNestedTemplate(Hashtable<String,String> templates, TrifoliaConstraint constraint)
	{
		String templateOid = constraint.containedTemplateOid();
		if (!(templateOid.equals(""))) templates.put(templateOid, constraint.deepContext());
		for (Iterator<TrifoliaConstraint> it = constraint.nestedConstraints().iterator();it.hasNext();)
			addNestedTemplate(templates,it.next());
	}
	
	/**
	 * @return a Hashtable with key = path to a fixed value; value = the value
	 */
	public Hashtable<String,String> allFixedValueConstraints()
	{
		Hashtable<String,String> fvConstraints = new Hashtable<String,String>();
		for (Iterator<TrifoliaConstraint> it = constraints.iterator();it.hasNext();)
			it.next().addFixedValueConstraints(fvConstraints);		
		return fvConstraints;
	}

}
