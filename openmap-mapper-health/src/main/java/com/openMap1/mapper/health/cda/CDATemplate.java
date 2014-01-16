package com.openMap1.mapper.health.cda;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.w3c.dom.Element;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.health.v3.RMIMReader;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * represents a CDA template, read from a Schematron templates file
 * 
 * @author robert
 *
 */
public class CDATemplate {
	
	public boolean tracing() {return templateSet.tracing();}
	
	/**
	 * @return  the full String id of the template
	 */
	public String fullTemplateId() {return templateId;}
	private String templateId;
	
	/**
	 * @return  the local String id of the template, within the template set
	 */
	public String getLocalId() {return usageElement.getAttribute("id");}

	
	/**
	 * @return the possible names of the node the template appears on, if known;
	 * empty Hashtable if not known
	 */
	public Hashtable<String,String> nodeNames() {return nodeNames;}
	private Hashtable<String,String> nodeNames = new Hashtable<String,String>();
	
	public Vector<TemplateRule> getRules() {return rules;}
	private Vector<TemplateRule> rules;
	
	// the element in the schematron file (usually a .ent file) defining the template)
	private Element templateElement;
	
	// the element in my template usage file defining the template
	private Element usageElement;
	
	/**
	 * @return the level of the template.
	 * Levels are static constants in CDATemplateSet: CDA, SECTION, etc.
	 * CDA is the lowest defined level (0) and UNDEFINED is -1.
	 */
	public int level() {return level;}
	private int level;
	
	public String name() {return usageElement.getAttribute("name");}
	
	/**
	 * @return the template set (eg CCD, HITSP C32) that this template belongs to
	 */
	public TemplateSet templateSet() {return templateSet;}
	private TemplateSet templateSet;
	
	private Vector<TemplatedPath> ownContexts;
	
	/**
	 * @return true if the full set of CDAContexts at which this template can appear
	 * has been found, even if it is empty
	 */
	public boolean resolved() {return resolved;}
	private boolean resolved;
	
	/**
	 * @return descriptive name for this template
	 */
	public String descriptiveName() {return ("template " + localId() + " (" + name() + ") of set " + templateSet.getName());}
	
	/**
	 * 
	 * @param temps a Vector of CDATemplates
	 * @return true if this is one of them
	 */
	public boolean oneOf(Vector<CDATemplate> temps)
	{
		boolean oneOf = false;
		for (Iterator<CDATemplate> it = temps.iterator();it.hasNext();)
		{
			CDATemplate temp = it.next();
			if ((temp.localId().equals(localId())) 
					&& (temp.templateSet().getName().equals(templateSet().getName()))) oneOf = true;
		}
		return oneOf;
	}
	
	public String key() {return (name() + "_" + templateSet().getName());}
	
	//--------------------------------------------------------------------------------------------
	//                                 Constructors
	//--------------------------------------------------------------------------------------------

	/**
	 * usual constructor when there is a .ent file for the template
	 */
	public CDATemplate(TemplateSet templateSet, String templateId, Element templateElement, Element usageElement, int level)
	throws MapperException
	{
		this.templateSet = templateSet;
		this.templateId = templateId;
		this.templateElement = templateElement;
		this.usageElement = usageElement;
		checkUsageElement(usageElement);
		this.level = level;
		rules = new Vector<TemplateRule>();
		ownContexts = new Vector<TemplatedPath>();
		nodeNames = new Hashtable<String,String>();
		resolved = false;
		// trace("");
		// trace("Reading template " + templateId + " in set " + templateSet.getName());
	}
	
	/**
	 * constructor for when there is no .ent file defining the template, but it only  constrains
	 * some existing templates.
	 * @param templateSet
	 * @param usageElement
	 * @param level
	 * @throws MapperException
	 */
	public CDATemplate(TemplateSet templateSet, Element usageElement, int level)
	throws MapperException
	{
		this.templateSet = templateSet;

		templateId = usageElement.getAttribute("id");
		if (!templateSet.idBase().equals("")) templateId = templateSet.idBase() + "." + templateId;

		this.usageElement = usageElement;
		checkUsageElement(usageElement);
		this.level = level;
		rules = new Vector<TemplateRule>();
		ownContexts = new Vector<TemplatedPath>();
		nodeNames = new Hashtable<String,String>();
		resolved = false;
	}
	
	private void checkUsageElement(Element usageElement) throws MapperException
	{
		String[] usageAtts = {"id","name","fromFile","nodeNames"};
		String[] usageChildren = {"constrains","in","canCoexist"};
		XMLUtil.checkAttributes(usageElement, usageAtts);
		XMLUtil.checkChildElements(usageElement, usageChildren);
	}
	

	
	//--------------------------------------------------------------------------------------------
	//                                 recording rules
	//--------------------------------------------------------------------------------------------
	
	public void recordRules() throws MapperException
	{
		Vector<Element> ruleEls = XMLUtil.namedChildElements(templateElement, "rule");
		for (Iterator<Element> it  = ruleEls.iterator();it.hasNext();)
			rules.add(new TemplateRule(this,templateId,it.next()));
	}
	
	/**
	 * @return the assertions on rules which have the standard context (i.e rules
	 * which apply on the template node) and which define the node the rule applies on -
	 * and therefore define the template node
	 */
	public Vector<TemplateAssertion> definingAssertions()
	{
		Vector<TemplateAssertion> defining = new Vector<TemplateAssertion>();
		for (Iterator<TemplateRule> ir = rules.iterator();ir.hasNext();)
		{
			TemplateRule tr = ir.next();
			if (tr.isStandardContext())
				for (Iterator<TemplateAssertion> ia = tr.definingAssertions().iterator();ia.hasNext();)
					defining.add(ia.next());
		}
		return defining;
	}

	public Vector<TemplateAssertion> constrainingAssertions()
	{
		Vector<TemplateAssertion> constraining = new Vector<TemplateAssertion>();
		for (Iterator<TemplateRule> ir = rules.iterator();ir.hasNext();)
		{
			TemplateRule tr = ir.next();
				for (Iterator<TemplateAssertion> ia = tr.constrainingAssertions().iterator();ia.hasNext();)
					constraining.add(ia.next());
		}
		return constraining;
		
	}
	
	//--------------------------------------------------------------------------------------------
	//                        resolving possible contexts for this template
	//--------------------------------------------------------------------------------------------
	
	/**
	 * attempt to set the possible node names of the template from its defining assertions
	 */
	public void setNodeNames()
	{
		addNodeNamesFromUsageFile();
		for (Iterator<TemplateAssertion> it = definingAssertions().iterator(); it.hasNext();)
		{
			TemplateAssertion assertion = it.next();
			TestNode assertNode = assertion.testNode();
			// 'self::' XPath; should be only one possible node name
			if (assertNode.connector() == TestNode.XPATH) addNodeName(assertNode);

			// 'and' or 'or' of XPaths; if any have an initial 'self' step, add possible node names
			if ((assertNode.connector() == TestNode.OR)|(assertNode.connector() == TestNode.AND))
			{
				for (Iterator<TestNode> ic = assertNode.childNodes().iterator();ic.hasNext();)
				{
					TestNode child = ic.next();
					if (child.connector() == TestNode.XPATH) addNodeName(child);
				}
			}
		}
	}
	
	/**
	 * Define some node names on which this template may appear, 
	 * using an attribute in the template usage file
	 * (for cases where the template itself fails to define the nodes it appears on).
	 * It is OK if the template usage file duplicates node names defined in the template assertions
	 */
	private void addNodeNamesFromUsageFile()
	{
		nodeNames = new Hashtable<String,String>();
		String names = usageElement.getAttribute("nodeNames");
		if (!names.equals(""))
		{
			StringTokenizer st = new StringTokenizer(names," ");
			while (st.hasMoreTokens())
			{
				String nodeName = st.nextToken();
				nodeNames.put(nodeName,nodeName);
			}
		}		
	}
	
	/**
	 * if the TestNode is an XPath with an initial 'self::' step,
	 * add the defined node name to the list of possible node names for this template
	 * @param assertNode a TestNode
	 */
	private void addNodeName(TestNode assertNode)
	{
		if (assertNode.connector() == TestNode.XPATH)
		{
			TestNode firstStep = assertNode.childNodes().get(0);
			if ((firstStep.axis() == TestNode.SELF) && (firstStep.hasNodeTest()))
			{
				StringTokenizer st = new StringTokenizer(firstStep.nodeTest(),":");
				// remove the namespace prefix if there is any
				String nodeName = st.nextToken();
				if (st.hasMoreTokens()) nodeName = st.nextToken();
				nodeNames.put(nodeName,nodeName);
			}
		}		
	}
	
	/**
	 * @return a Vector of all the templates (in any template set)
	 * that this template can appear directly inside, as defined directly for this
	 * template in the template usage file
	 */
	public Vector<CDATemplate> ownParentTemplates() throws MapperException
	{
		Vector<CDATemplate> parents = new Vector<CDATemplate>();
		for (Iterator<Element> it = XMLUtil.namedChildElements(usageElement, "in").iterator();it.hasNext();)
		{
			CDATemplate temp = getTemplateFromInElement(it.next());
			if (temp != null) parents.add(temp);
		}
		return parents;
	}
	
	/**
	 * 
	 * @return all parent templates of this template, or of any others
	 * that it constrains
	 * @throws MapperException
	 */
	public Vector<CDATemplate> allParentTemplates() throws MapperException
	{
		Vector<CDATemplate> parents = ownParentTemplates();
		for (Iterator<CDATemplate> it = constrainedTemplates().iterator();it.hasNext();)
		{
			for (Iterator<CDATemplate> iu = it.next().allParentTemplates().iterator(); iu.hasNext();)
			{
				CDATemplate parent = iu.next();
				if (!parent.oneOf(parents)) parents.add(parent);
			}			
		}
		return parents;
	}
	
	
	/**
	 * @return a Vector of all the templates (in any template set)
	 * that this template provides further constraints on; wherever this
	 * template appears ,those templates must appear also
	 */
	public Vector<CDATemplate> directConstrainedTemplates() throws MapperException
	{
		Vector<CDATemplate> constrained = new Vector<CDATemplate>();
		for (Iterator<Element> it = XMLUtil.namedChildElements(usageElement, "constrains").iterator();it.hasNext();)
		{
			String constId = XMLUtil.getText(it.next());
			CDATemplate temp = templateSet.collection().getTemplate(constId);
			if (temp == null) throw new MapperException("Cannot find template '" + constId 
					+ "' directly constrained by template '" + fullTemplateId() + "'");
			constrained.add(temp);
		}
		return constrained;
	}
	
	
	/**
	 * recursive descent of the 'constrains' relation (which must not be circular - that gets checked)
	 * @return all templates that this template constrains, directly or indirectly
	 * @throws MapperException
	 */
	public Vector<CDATemplate> constrainedTemplates() throws MapperException
	{
		Vector<CDATemplate> constrained = directConstrainedTemplates();
		Vector<CDATemplate> constrained2 = directConstrainedTemplates();
		for (Iterator<CDATemplate> it = constrained.iterator();it.hasNext();)
		{
			CDATemplate con = it.next();
			if (con==null) throw new MapperException("null template");
			Vector<CDATemplate> all = con.constrainedTemplates();
			for (Iterator<CDATemplate> iu = all.iterator();iu.hasNext();)
			{
				CDATemplate next = iu.next();
				if (!hasTemplateId(next.fullTemplateId(),constrained2)) constrained2.add(next);
			}
		}
		return constrained2;
	}
	
	/**
	 * @return a Vector of all the templates (in any template set)
	 * that may appear on the same node as this template,
	 * but need not necessarily do so
	 */
	public Vector<CDATemplate> coexistentTemplates() throws MapperException
	{
		Vector<CDATemplate> constrained = new Vector<CDATemplate>();
		for (Iterator<Element> it = XMLUtil.namedChildElements(usageElement, "canCoexist").iterator();it.hasNext();)
		{
			String constId = XMLUtil.getText(it.next());
			CDATemplate temp = templateSet.collection().getTemplate(constId);
			if (temp == null) throw new MapperException("Cannot find template '" + constId 
					+ "' stated to coexist with template '" + fullTemplateId() + "'");
			constrained.add(temp);
		}
		return constrained;
	}
	
	public boolean canCoexist(CDATemplate other) throws MapperException
	{
		boolean coexist = false;
		for (Iterator<CDATemplate> it = coexistentTemplates().iterator(); it.hasNext();)
			if (it.next().fullTemplateId().equals(other.fullTemplateId())) coexist = true;
		return coexist;
	}

	/**
	 * @param id
	 * @param temps
	 * @return true if the template id is in the set of templates
	 */
	private boolean hasTemplateId(String id, Vector<CDATemplate> temps)
	{
		boolean hasId = false;
		for (Iterator<CDATemplate> it = temps.iterator();it.hasNext();)
		{
			CDATemplate tem = it.next();
			if (tem.fullTemplateId().equals(id)) hasId = true;
		}
		return hasId;
	}

	
	/**
	 * 
	 * @param inEl
	 * @return
	 * @throws MapperException
	 */
	private CDATemplate getTemplateFromInElement(Element inEl) throws MapperException
	{
		// find the right template set - this one if not specified
		String setName = inEl.getAttribute("set");
		String localId = XMLUtil.getText(inEl);
		if (setName.equals(""))  setName = templateSet.getName();
		TemplateSet ts = templateSet.collection().getTemplateSet(setName);
		if (ts == null) throw new MapperException("There is no template set " + setName);

		// use the local id in the template set, only if that set uses local ids
		CDATemplate temp = ts.getTemplateByLocalId(localId);
		// otherwise look for the full template id, in the whole collection
		if (temp == null) temp = ts.collection().getTemplate(localId);

		// failure
		if (temp == null) throw new MapperException("There is no template with local or full id " + localId 
				+ " in template set " + setName + " or in the whole collection");
		return temp;
	}
	
	
	/**
	 * @param parentTemplate a template that is a defined parent of this one
	 * @return the allowed paths from the parent template to this one,
	 * if they have been defined in the appropriate 'nodePaths' attribute of the Template usage file;
	 * or an empty Vector if they have not.
	 */
	public Vector<String> definedPathsFromTemplate(CDATemplate parentTemplate)  throws MapperException
	{
		Vector<String> paths = new Vector<String>();
		for (Iterator<Element> it = XMLUtil.namedChildElements(usageElement, "in").iterator();it.hasNext();)
		{
			Element inEl = it.next();
			String[] inAtts = {"nodePaths","set"};
			XMLUtil.checkAttributes(inEl, inAtts);
			CDATemplate temp = getTemplateFromInElement(inEl);
			if ((temp != null) && (temp.fullTemplateId().equals(parentTemplate.fullTemplateId())))
			{
				StringTokenizer st = new StringTokenizer(inEl.getAttribute("nodePaths")," ");
				while (st.hasMoreTokens())  paths.add(st.nextToken());
			}
		}
		return paths;		
	}
	
	/**
	 * @param paths a Vector of paths from a start class
	 * @param startClass the start class
	 * @param lastAssoc the last association leading to the start class
	 * @param nodeName must match the final step of a path, for the path to count
	 * @return a Vector of CDAContexts corresponding to those paths that can be followed
	 * through associations and which end in the required node name
	 */
	private Vector<TemplatedPath> definedContexts(Vector<String>paths, EClass startClass,
			String lastAssoc, String nodeName)
	{
		Vector<TemplatedPath> contexts = new Vector<TemplatedPath>();
		for (Iterator<String> ip = paths.iterator(); ip.hasNext();)
		{
			TemplatedPath con = new TemplatedPath().addStep(new ContextStep(lastAssoc,startClass.getName()));
			EClass current = startClass;
			String path = ip.next();
			String step = "";
			StringTokenizer steps = new StringTokenizer(path,"/");
			boolean found = true;
			while (steps.hasMoreTokens())
			{
				step= steps.nextToken();
				if (found)
				{
					found = false; // remains false if you cannot follow any step in the path
					EClass nextClass = null;
					for (Iterator<EReference> ir = current.getEAllReferences().iterator();ir.hasNext();)
					{
						EReference ref = ir.next();
						String refName = ref.getName();
						if ((!found) && (refName.equals(step)))
						{
							found = true;
							nextClass = (EClass)ref.getEType();
							String className = nextClass.getName();
							con.addStep(new ContextStep(step,className));
						}
					}
					if (found) current = nextClass;
				}
			}
			// if all steps could be followed and the last step matched the required node name...
			if ((found) && (step.equals(nodeName))) contexts.add(con);
		}
		return contexts;
	
		
	}
	
	/**
	 * @return true if all the templates that this template can appear directly 
	 * inside have been resolved
	 */
	public boolean parentsAllResolved() throws MapperException
	{
		boolean parentsResolved = true;
		for (Iterator<CDATemplate> it = allParentTemplates().iterator();it.hasNext();)
			if (!it.next().resolved()) parentsResolved = false;
		return parentsResolved;
	}
	
	/**
	 * For the root template of some template set, set its context to be the root context. 
	 * @param rootContext
	 */
	public void resolveRootContext(TemplatedPath rootContext)
	{
		ownContexts = new Vector<TemplatedPath>();
		ownContexts.add(rootContext);
		resolved = true;
	}
	
	/**
	 * resolve this template: find all the full contexts in which it can appear.
	 * made from the unmodified Ecore model
	 */
	public void resolve()  throws MapperException
	{
		resolved = true; // record you have attempted resolution, whether or not any contexts are found
		ownContexts = new Vector<TemplatedPath>();
		
		for (Iterator<CDATemplate> it = allParentTemplates().iterator(); it.hasNext();)
		{
			CDATemplate parent = it.next();
			Vector<String> paths = definedPathsFromTemplate(parent);
			for (Iterator<TemplatedPath> ip = parent.allContexts().iterator();ip.hasNext();)
			{
				TemplatedPath parentContext = ip.next();
				int last = parentContext.length() - 1;
				String lastAssoc = parentContext.step(last).associationName();
				EClass startClass = findEndClass(parentContext);

				/* key = a node name; element = all the CDAContexts that lead from the start class
				 * (the first step) to the node name, without repeating node names */
				Hashtable<String,Vector<TemplatedPath>> extensionContexts = 
					getContextExtensions(startClass,lastAssoc);
				// traceKeys(extensionContexts);
				
				// for each possible node name of this template....
				for (Enumeration<String> en = nodeNames.keys();en.hasMoreElements();)
				{
					String nodeName = en.nextElement();

					// heuristic: find the contexts ending in the node name...
					Vector<TemplatedPath> targetContexts = extensionContexts.get(nodeName);
					/* .. and pick out the joint shortest of those contexts, but with length > 1,
					 * because length = 1 ends on the start class */
					Vector<TemplatedPath> chosenContexts = shortestLongerThan1(targetContexts);

					/*  If the template usage file defines any paths from the parent class,
					 * convert these paths into CDAContexts, which override those made by the heuristic  */
					if (paths.size() > 0) chosenContexts = definedContexts(paths,startClass,lastAssoc,nodeName);

					// for each of the chosen extensions, extend the cloned parent context
					for (int s = 0; s < chosenContexts.size(); s++)
					{
						TemplatedPath extension = chosenContexts.get(s);
						TemplatedPath parentClone = parentContext.clone();
						// note that the parent template must now appear on its last step
						parentClone.step(last).addTemplateId(parent.fullTemplateId());
						/* do not add step 0 of the extension context, 
						 * which duplicates the last step of the parent context; but 
						 * because the extension has length > 1, you will add some steps */
						for (int w = 1; w < extension.length();w++)
							parentClone.addStep(extension.step(w));
						ownContexts.add(parentClone);
					}
				}
			}
		}		
	}
	
	
	
	/**
	 * @param longList a list of CDAContexts, of different lengths
	 * @return all those that tie for the shortest length > 1
	 */
	private Vector<TemplatedPath> shortestLongerThan1(Vector<TemplatedPath> longList)
	{
		Vector<TemplatedPath> shortest = new Vector<TemplatedPath>();
		if (longList != null)
		{
			// find the shortest length > 1
			int minLength = 1000;
			for (int i = 0; i < longList.size(); i++)
			{
				TemplatedPath context = longList.get(i);
				if ((context.length() > 1)  && (context.length() < minLength)) 
							minLength = context.length();
			}

			// collect all those that have the shortest length > 1
			for (int i = 0; i < longList.size(); i++)
				if (longList.get(i).length() == minLength) shortest.add(longList.get(i));			
		}
		return shortest;
	}
	
	//---------------------------------------------------------------------------------------------
	//                                    Extending template contexts
	//---------------------------------------------------------------------------------------------
	
	/**
	 * @param eClass a class which has a template on it
	 * @param assocName the association that leads to the EClass
	 * @return key = a node name; element = all the CDAContexts that lead from above the start class
	 * via the association name to the node name, without repeating node names
	 */
	private Hashtable<String,Vector<TemplatedPath>> getContextExtensions(EClass eClass, String assocName)
	{
		Hashtable<String,Vector<TemplatedPath>> extensionContexts = new Hashtable<String,Vector<TemplatedPath>>();
		TemplatedPath empty = new TemplatedPath();
		extendContexts(extensionContexts,empty,eClass,assocName,0);	
		return extensionContexts;
	}
	
	/**
	 * Recursively extend the Hashtable 'contexts' for the new class - avoiding contexts that repeat any class
	 * @param contexts for each node name, a Vector of all the contexts that reach that node. 
	 * Initially called with contexts empty, but recurses
	 * @param context a CDAContext that leads down to some EClass;
	 * initially called with context empty (no steps), but recurses
	 * @param eClass another EClass, reached by a containment association from  
	 * the EClass at the end of the context
	 * @param assocName the association name
	 * @param depth to limit recursion depth
	 */
	private void extendContexts(Hashtable<String,Vector<TemplatedPath>> contexts,
			TemplatedPath context, EClass eClass, String assocName,int depth)
	{
		int maxDepth = 5; // no templates nested inside other templates as depth > 4
		// add a context with a final step for the new class
		ContextStep step = new ContextStep(assocName,eClass.getName());
		step.setFixedValues(eClass);
		TemplatedPath newContext = context.clone();
		newContext.addStep(step);

		// contexts are stored by inner association name, except the top context that has no association
		String keyName = assocName;
		if (keyName == null) keyName = eClass.getName();
		Vector<TemplatedPath> namedContexts = contexts.get(keyName);
		if (namedContexts == null) namedContexts = new Vector<TemplatedPath>();
		namedContexts.add(newContext);
		contexts.put(keyName,namedContexts);
		
		/* extend for all the containment relations of the new class, which do not repeat classes more than twice; 
		 * exclude all data type classes. */
		for (Iterator<EReference> it = eClass.getEAllReferences().iterator();it.hasNext();)
		{
			EReference ref = it.next();
			EClassifier target = ref.getEType();
			if (       (ref.isContainment()) 
					&& (depth < maxDepth)
					&& (newContext.classCount(target.getName()) < 3)
					&& (target instanceof EClass)
					&& (!(target.getEPackage().getName().equals(RMIMReader.DATATYPE_PACKAGE_NAME))))
				extendContexts(contexts,newContext,(EClass)target,ref.getName(),depth + 1);
		}
	}
	
	//--------------------------------------------------------------------------------------------
	//                                 access methods
	//--------------------------------------------------------------------------------------------
	
	/**
	 * @return the set of all contexts at which this template may appear; 
	 * assuming that if it constrains another template, it can appear in all the same
	 * contexts as that other template
	 * warning - will blow up if two contexts constrain one another; the 'constrains'
	 * relation must not be circular
	 */
	public Vector<TemplatedPath> allContexts() throws MapperException
	{
		Vector<TemplatedPath> all = ownContexts;
		for (Iterator<CDATemplate> it = constrainedTemplates().iterator();it.hasNext();)
		{
			CDATemplate templ = it.next();
			Vector<TemplatedPath> other = templ.allContexts();
			for (Iterator<TemplatedPath> iu = other.iterator();iu.hasNext();)
			{
				TemplatedPath con = iu.next();
				if (!con.inContexts(all)) all.add(con);
			}
		}
		return all;
	}
	
	
	/**
	 * This template is compatible with a CDAContext if for one of the resolved contexts
	 * of this template:
	 * (a) There is the same number of steps
	 * (b) For each step, the step name (association name or class name) matches
	 * (c) For each step, all the template ids required by this template's context are supplied by the context
	 * (d) For each step, all the fixed values required by this template's context are supplied by the context
	 */
	public boolean isCompatibleWithContext(TemplatedPath context) throws MapperException
	{
		for (Iterator<TemplatedPath>  it = allContexts().iterator();it.hasNext();)
		{
			TemplatedPath thisContext = it.next();
			if (context.length() == thisContext.length())
			{
				boolean matches = true;
				for (int s = 0; s < context.length(); s++)
					if (!(thisContext.step(s).compatibleStep(context.step(s)))) matches = false;
				if (matches) return true;
			}
		}
		return false;
	}
	
	/**
	 * @return the subset of the contexts of this template which could compatibly match 
	 * or extend the given context, in that:
	 * (a) They have the same number of steps as the given context, or more steps
	 * (b) For each step in the given context, the step name (association name or class name) matches
	 * (c) For each step in the given context, all the template ids required by this template's context are supplied by the context
	 * (d) For each step in the given context, all the fixed values required by this template's context are supplied by the context
	 */
	public Vector<TemplatedPath> extendingContexts(TemplatedPath context) throws MapperException
	{
		Vector<TemplatedPath> extenders = new Vector<TemplatedPath>();
		for (Iterator<TemplatedPath>  it = allContexts().iterator();it.hasNext();)
		{
			TemplatedPath thisContext = it.next();
			if (context.length() < thisContext.length() + 1)
			{
				boolean matches = true;
				for (int s = 0; s < context.length(); s++)
					if (!(thisContext.step(s).compatibleStep(context.step(s)))) matches = false;
				if (matches) extenders.add(thisContext);
			}
		}
		return extenders;		
	}
	
	/**
	 * @param temp some other template
	 * @return true if this template and the other can be on the same node
	 * This is true if either template constrains the other, or if it has been stated
	 * in the template usage file that either can coexist with the other.
	 * A template can coexist with itself.
	 */
	public boolean canBeOnSameNodeAs(CDATemplate temp)  throws MapperException
	{
		boolean OK = ((constrains(temp))|(temp.constrains(this)));

		if (canCoexist(temp)) OK = true;
		if (temp.canCoexist(this)) OK = true;

		if (temp.templateId.equals(this.fullTemplateId())) OK = true;
		return OK;
	}
	
	/**
	 * 
	 * @param temp some other template
	 * @return true if this template constrains the other template, so can appear on the 
	 * same node
	 */
	public boolean constrains(CDATemplate temp) throws MapperException
	{
		boolean constrains = false;
		Vector<CDATemplate> constrained = constrainedTemplates();
		for (Iterator<CDATemplate> it = constrained.iterator(); it.hasNext();)
			if (it.next().fullTemplateId().equals(temp.fullTemplateId())) constrains = true;
		return constrains;
	}
	
	/**
	 * @return all possible node names for the template, concatenated and separated by Strings
	 */
	public String allNodeNames()
	{
		String allNames = "";
		for (Enumeration<String> en = nodeNames.keys();en.hasMoreElements();)
			allNames = allNames + en.nextElement() + " ";
		return allNames;
	}
	
	/**
	 * 
	 * @param context a CDAContext starting at the root class
	 * @return the EClass at the end of the context path
	 */
	private EClass findEndClass(TemplatedPath context)
	{
		EClass currentClass = templateSet().collection().getEntryClass();
		for (int s = 1; s < context.length();s++)
		{
			ContextStep step = context.step(s);
			for (Iterator<EReference> ir = currentClass.getEAllReferences().iterator();ir.hasNext();)
			{
				EReference ref = ir.next();
				if ((ref.isContainment()) && (ref.getName().equals(step.associationName())))
						currentClass = (EClass)ref.getEType();
			}
		}
		return currentClass;
	}
	
	/**
	 * @return the last numeral in a template id
	 */
	public String localId()
	{
		String localId = "";
		StringTokenizer st = new StringTokenizer(fullTemplateId(),".");
		while (st.hasMoreTokens()) localId = st.nextToken();
		return localId;
	}

	
	//--------------------------------------------------------------------------------------------
	//                        annotating the Ecore model with template constraints
	//--------------------------------------------------------------------------------------------
	
	/**
	 * 	add annotations for each constraint the template puts on descendant nodes
	 * @param templatedClass the EClass to receive the annotations
	 * @param suffix a String "", or "_1", "_2" etc defining which of the templates
	 * on the EClass provides each constraint
	 */
	public void addConstraintAnnotations(TemplatedPath context,EClass templatedClass,String suffix)
	{
		for (Iterator<TemplateRule> it = rules.iterator();it.hasNext();)
		{
			TemplateRule rule = it.next();
			rule.addRuleConstraintAnnotations(context, templatedClass, suffix);
		}
	}
	
	//--------------------------------------------------------------------------------------------
	//            Extracting information for an initial template usage file, to be edited
	//--------------------------------------------------------------------------------------------
	
	private int heuristicLevel;
	
	/**
	 * @return a guess at the level of a template, based on words in its title
	 */
	public int heuristicLevel()
	{
		getTrialTitle();
		return heuristicLevel;
	}
	
	/**
	 * return the title of a template, excluding spaces and common words
	 */
	public String getTrialTitle()
	{
		heuristicLevel = 3;
		String[] dropWords= {"IHE", "PCC", "-", "errors", "validation", "phase"};
		String title = "";
		Element titEl = XMLUtil.firstNamedChild(templateElement, "title");
		if (titEl != null)
		{
			StringTokenizer st = new StringTokenizer(XMLUtil.getText(titEl)," ");
			while (st.hasMoreTokens())
			{
				String word = st.nextToken();
				if (!GenUtil.inArray(word, dropWords)) title = title + word;
				
				// allocate heuristic levels, depending on what is in the title
				if (word.equalsIgnoreCase("Section")) heuristicLevel = 1;
				if (word.equalsIgnoreCase("Entry")) heuristicLevel = 2;
				if (word.equalsIgnoreCase("Activity")) heuristicLevel = 2;
				if (word.equalsIgnoreCase("Organizer")) heuristicLevel = 2;
				// default is 3
			}
		}
		return title;
	}
	
	
	
	public Vector<String> constrainedTemplateIds()
	{
		String start = "cda:templateId[@root=";
		Vector<String> ids = new Vector<String>();
		Vector<Element> rules = XMLUtil.namedChildElements(templateElement, "rule");
		if (rules.size() == 1)
		{
			Vector<Element> asserts = XMLUtil.namedChildElements(rules.get(0), "assert");
			for (Iterator<Element> it = asserts.iterator(); it.hasNext();)
			{
				String test = it.next().getAttribute("test");
				StringTokenizer parts = new StringTokenizer(test," ");
				while (parts.hasMoreTokens())
				{
					String part = parts.nextToken();
					if (part.startsWith(start))
					{
						// strip off start and first quote
						String id = part.substring(start.length() + 1);
						// strip off any trailing 'and'
						if (id.endsWith("and")) id = id.substring(0, id.length()-3);
						// strip off last quote and bracket
						id = id.substring(0,id.length()-2);
						ids.add(id);
					}
				}
			}
		}
		return ids;
	}
	
	//--------------------------------------------------------------------------------------------
	//                                 trivia
	//--------------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private void trace(String s) {if (tracing()) System.out.println(s);}
	

}
