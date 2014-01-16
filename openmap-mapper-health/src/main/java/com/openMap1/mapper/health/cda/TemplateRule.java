package com.openMap1.mapper.health.cda;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.ecore.EClass;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * A rule within a template, which may make several assertions
 * @author robert
 *
 */
public class TemplateRule {
	
	public boolean tracing() {return template.tracing();}
	
	public static String CDA_PREFIX = "cda";
	
	public String contextString() {return contextString();}
	private String contextString;
	
	public TestNode contextFromTemplate() {return contextFromTemplate;}
	private TestNode contextFromTemplate;
	
	private String templateId;
	
	/**
	 * @return assertions which define which node this template can appear on
	 */
	public Vector<TemplateAssertion> definingAssertions() {return definingAssertions;}
	private Vector<TemplateAssertion> definingAssertions;
	
	/**
	 * @return assertions which constrain the structure of the tree below the node of the template
	 */
	public Vector<TemplateAssertion> constrainingAssertions() {return constrainingAssertions;}
	private Vector<TemplateAssertion> constrainingAssertions;
	
	/**
	 * @return the step 0..N of a context at which this rule applies.
	 * This needs to be set using setContextStep when the rule is matched with a context; 
	 * it usually has only one possible value, but may have more if its template
	 * can appear at different depths
	 */
	public int getContextStep() {return contextStep;}
	public void setContextStep(int step) {contextStep = step;}
	private int contextStep;
	
	private CDATemplate template;
	
	//--------------------------------------------------------------------------------------
	//                                Constructor
	//--------------------------------------------------------------------------------------
		
	public TemplateRule(CDATemplate template, String templateId, Element ruleEl) throws MapperException
	{
		this.template = template;
		this.templateId = templateId;

		try
		{
			contextString = ruleEl.getAttribute("context");
			contextFromTemplate = new TestNode(contextString);
			if (isStandardContext()) trace ("Rule with standard context");
			else if (extendsStandardContext()) trace ("Rule extends standard context:  '" + contextString + "' " + contextFromTemplate.structure());
			else trace("Rule with non-standard context '" + contextString + "' " + contextFromTemplate.structure());
		}
		catch (Exception ex) {trace("Failed to parse context '" + contextString + "' in template '" + template.getLocalId() + "' : " + ex.getMessage());}
		
		definingAssertions = new Vector<TemplateAssertion>();
		constrainingAssertions = new Vector<TemplateAssertion>();
		Vector<Element> assertEls = XMLUtil.namedChildElements(ruleEl, "assert");
		for (Iterator<Element> it = assertEls.iterator();it.hasNext();)
		{
			TemplateAssertion ta = new TemplateAssertion(this,it.next());
			if (ta.isNodeDefining()) definingAssertions.add(ta);
			else constrainingAssertions.add(ta);
		}
	}
	
	//-----------------------------------------------------------------------------------------------
	//                       rule contexts and applicability
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * @param TemplatedPath a context in which this rule's template is on a step
	 * @param templateStep the step number on which the template appears
	 * @return true if the rule applies in the context, i.e.
	 * either the rule has the same standard context as the template
	 * or the rule extends the context of the template, and the extension
	 * is compatible with the rest of the supplied context.
	 * As a side-effect, set the rule's context step.
	 */
	public boolean matchesContext(TemplatedPath context, int templateStep)
	{
		if (isStandardContext()) 
		{
			// side-effect; note that the rule step is the same as the template step
			setContextStep(templateStep);
			return true;
		}
		else if ((extendsStandardContext()) && (contextFromTemplate.connector() == TestNode.XPATH))
		{
			int ruleSteps = contextFromTemplate.childNodes().size(); // 2 or more
			// the rule cannot apply if its context XPath is too long for the supplied context
			if ((templateStep + ruleSteps) > context.length()) return false;
			boolean matches = true;
			// the first step of the rule context merely checks the template id; must pass
			for (int r = 1; r < ruleSteps; r++)
			{
				TestNode ruleStep = contextFromTemplate.childNodes().get(r);
				ContextStep contextStep = context.step(r);
				if (!ruleStep.isCompatible(contextStep)) matches = false;
			}
			// side-effect; note that the rule step is greater than the template step
			if (matches) setContextStep(templateStep + ruleSteps - 1);
			return matches;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param context
	 * @param ruleStep
	 * @return true if this rule implies some constraint on a node
	 * in the subtree below the final node of the context
	 */
	public boolean constrainsSubtreeBelow(TemplatedPath context, int ruleStep)
	{
		boolean constrains = false;
		for (Iterator<TemplateAssertion> it = constrainingAssertions.iterator();it.hasNext();)
		{
			TemplateAssertion assertion = it.next();
			if (assertion.testNode().constrainsSubtreeBeneath(context, ruleStep)) constrains = true;
		}
		return constrains;
	}

	
	/**
	 * @return true if the context of this rule is the standard' context which just 
	 * says that the appropriate <templateId> Element is a child of the node.
	 * This test does not yet allow for spaces around the '='.
	 */
	public boolean isStandardContext()
	{
		return (contextString.equals(standardContext()));
	}
	
	public boolean extendsStandardContext()
	{
		return (contextString.startsWith(standardContext()));
	}
	
	/**
	 * @return the 'standard' context string for a template, of 
	 * the form '*[cda:templateId/@root="<template id>"]',
	 * which picks out the template node
	 */
	private String standardContext() {return ("*[" + CDA_PREFIX + ":templateId/@root=\"" + templateId + "\"]");}
	
	//--------------------------------------------------------------------------------------------
	//                        annotating the Ecore model with template constraints
	//--------------------------------------------------------------------------------------------
	
	/**
	 * 	add annotations for each constraint the template puts on descendant nodes
	 * @param context - the association path to the template class (not used)
	 * @param templatedClass the EClass to receive the annotations
	 * @param suffix a String "", or "_1", "_2" etc defining which of the templates
	 * on the EClass provides each constraint
	 */
	public void addRuleConstraintAnnotations(TemplatedPath context,EClass templatedClass,String suffix)
	{
		for (Iterator<TemplateAssertion> it = constrainingAssertions.iterator();it.hasNext();)
		{
			TemplateAssertion assertion = it.next();
			TestNode testNode = assertion.testNode();
			Vector<AttributeValueConstraint> avcs = new Vector<AttributeValueConstraint>();
			// trace("\nFor " + templatedClass.getName() + " assert: " + assertion.test() + "\nStructure "  + testNode.structure());
			
			/* find the attribute value constraints in two cases (a) rule has same context as template
			 * and (b) rule context is an extension of template context. The two could be united easily. */
			if (isStandardContext())
			{
				avcs = testNode.getStringValueConstraints();
			}
			else if (extendsStandardContext())
			{
				//trace("***Extension " + contextString);
				avcs = testNode.getStringValueConstraints(contextFromTemplate);
			}
			
			//put each attribute value constraint on the template EClass
			for (int a = 0; a < avcs.size(); a++)
			{
				AttributeValueConstraint avc = avcs.get(a);
				// do not constrain templateId root attributes; will be done later
				if (!avc.templateIdInPath())
				{
					// System.out.println("Constraint from " + templatedClass.getName() + ": " + avc.stringForm());	
					String constraintKey = "constraint" + suffix + ":" + avc.path();
					ModelUtil.addMIFAnnotation(templatedClass, constraintKey, avc.value());					
				}
			}
		}
		
	}
	
	//--------------------------------------------------------------------------------------------
	//                                   trivia
	//--------------------------------------------------------------------------------------------

	private void trace(String s) {if (tracing()) System.out.println(s);}

}
