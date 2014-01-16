package com.openMap1.mapper.health.cda;

import java.util.Vector;

import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XMLUtil;

/**
 * a single assertion within a template rule
 * 
 * @author robert
 *
 */
public class TemplateAssertion {
	
	/**
	 * @return true if this assertion helps to define the node the rule and template applies to
	 */
	public boolean isNodeDefining() {return isNodeDefining;}
	private boolean isNodeDefining = false;
	
	/**
	 * @return true if this assertion makes a structural constraint on the tree 
	 * beneath the template node
	 */
	public boolean isNodeConstraining() {return isNodeConstraining;}
	private boolean isNodeConstraining = false;
	
	/**
	 * @return the explanation of what the assertion means
	 */
	public String explanation() {return explanation;}
	private String explanation;
	
	private String test;
	public String test() {return test;}
	
	/**
	 * @return root of the tree of TestNode objects which represent the 'test' attribute
	 * of this assertion.
	 */
	public TestNode testNode() {return testNode;}
	private TestNode testNode = null;
	
	private TemplateRule rule;
	
	public TemplateAssertion(TemplateRule rule,Element assertEl)  throws MapperException
	{
		this.rule = rule;
		test = assertEl.getAttribute("test");
		explanation = XMLUtil.getText(assertEl);
		analyseTest();
	}
	
	/**
	 * Analyse the 'test' attribute by recursive parse into a tree of TestNode objects.
	 * @throws MapperException
	 */
	private void analyseTest() throws MapperException
	{
		try
		{
			testNode = new TestNode(test);
			@SuppressWarnings("unused")
			String effects = "ignore: ";
			if (testNode.definesNode())
			{
				isNodeDefining = true;
				effects = "defines: ";
			}
			else if (testNode.constrainsSubtree()) 
			{
				isNodeConstraining = true;
				effects = "constrains: ";
			}
			// if (templateReferences().size() > 0) trace(effects + "'" + test + "' " + testNode.structure());
		}
		catch (Exception ex)
		{
			trace("Failed to parse '" + test + "' : " + ex.getMessage());
			ex.printStackTrace();
			throw new MapperException(ex.getMessage());
		}
	}
	
	public Vector<String> templateReferences()
	{
		Vector<String> templateRefs = new Vector<String>();
		testNode.addTemplateReferences(templateRefs);
		return templateRefs;
	}
	
	private void trace(String s) {if (rule.tracing()) System.out.println(s);}

}
