package com.openMap1.mapper.health.cda;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * represents a set of CDA templates from one source, eg the CCD templates or
 * HITP C32 templates.
 * 
 * @author robert
 *
 */
public class TemplateSet {
	
	public boolean tracing() {return collection.tracing();}
		
	/**
	 * @param templateId a global template id
	 * @return the template with that global id
	 */
	public CDATemplate getTemplateByFullId(String templateId) {return templates.get(templateId);}
	public Hashtable<String,CDATemplate> templates() {return templates;}
	private Hashtable<String,CDATemplate> templates;
	
	/* template patterns have phases like 'errors', 'warning, 'manual', 'note'; 
	 * some phases are to be ignored for the purposes of defining the constrained RMIM. */
	private String[] phasesRead = {"errors"};
	
	private Element templateSetEl;
	
	/**
	 * @return full id of the root template (defined by the 'rootTemplate' attribute of the <templateSet> element)
	 */
	public String rootTemplateId() {return rootTemplateId;}
	private String rootTemplateId;
	
	/**
	 * @return the string root
	 * of all non-root ids in the tempate set
	 * (defined by the attribute 'idBase' of the <templateSet> element)
	 */
	public String idBase() {return idBase;}
	private String idBase;
	
	public static String[] levelNames = {"CDA","section","entry","subEntry"};
	
	/**
	 * levels of templates
	 */
	public static int UNDEFINED = -1;
	public static int CDA = 0;
	public static int SECTION = 1;
	public static int ENTRY = 2;
	public static int SUB_ENTRY = 3;
	
	public String getName() {return templateSetEl.getAttribute("name");}
	
	public TemplateCollection collection() {return collection;}
	private TemplateCollection collection;
	
	//------------------------------------------------------------------------------------------------
	//                                       Constructor
	//------------------------------------------------------------------------------------------------
	
	/**
	 * @param topTemplatePath file path to the top template file for this template set
	 * @param templateSetEl element describing the template set in the template usage file
	 * 
	 */
	public TemplateSet(TemplateCollection collection, String topTemplatePath,Element templateSetEl)
	throws MapperException
	{
		String[] setAtts = {"name","schFileName","subFolder","rootTemplate","idBase"};
		String[] setChildren = {"templateLevel"};
		XMLUtil.checkChildElements(templateSetEl, setChildren);
		XMLUtil.checkAttributes(templateSetEl, setAtts);

		this.collection = collection;
		this.templateSetEl = templateSetEl;

		idBase = templateSetEl.getAttribute("idBase"); // may be "" if base ids are not used
		rootTemplateId = templateSetEl.getAttribute("rootTemplate");
		if (rootTemplateId.equals("")) rootTemplateId = idBase;

		templates = new Hashtable<String,CDATemplate>();
		
		// read the template file and store all templates mentioned in the template usage file
		readTemplateFile(topTemplatePath, false);		
	}
	
	/**
	 * @param topTemplatePath file path to the top template file for this template set
	 * read the template file and store all templates mentioned in the template usage file
	 * @param readAllTemplates if true, read all templates even if they are not mentioned 
	 * in the template usage file
	 * @throws MapperException
	 */
	public void readTemplateFile(String topTemplatePath, boolean readAllTemplates)
	throws MapperException
	{
		trace("Reading template file at " + topTemplatePath);
		Element rootElement = XMLUtil.getRootElement(topTemplatePath);
		
		// templates for which the .sch template file refers to a .ent template
		Vector<Element> children = XMLUtil.namedChildElements(rootElement, "pattern");
		trace("Pattern elements: " + children.size());
		for (Iterator<Element> it = children.iterator();it.hasNext();)
		{
			Element child = it.next();
			String patternId = child.getAttribute("id");
			String templateFullId = getFullTemplateId(patternId);
			trace("Template full id " + templateFullId);

			/* only read templates that are in the template usage file, 
			 * unless readAllTemplates is true when making an initial template usage file */
			Element usageElement = getTemplateUsageElement(templateFullId);
			int level = getTemplateLevel(templateFullId);
			if ((usageElement != null)|(readAllTemplates))
			{
				String phase = getTemplatePhase(patternId);
				// currently only read rules in the patterns for the 'errors' phase
				if (GenUtil.inArray(phase, phasesRead))
				{
					CDATemplate template = templates.get(templateFullId);
					if (template == null) template = new CDATemplate(this,templateFullId,child,usageElement,level);
					template.recordRules();
					templates.put(templateFullId, template);					
				}					
			}
		}
		
		// templates in the template usage file, for which no .ent file exists
		for (Iterator<Element> it = XMLUtil.namedChildElements(templateSetEl, "templateLevel").iterator(); it.hasNext();)
		{
			Element levelEl = it.next();
			String level = levelEl.getAttribute("level");
			int lev = UNDEFINED;
			for (int i = 0; i < levelNames.length; i++) if (level.equals(levelNames[i])) lev = i;
			trace("Templates at level " + level);
			for (Iterator<Element> iu = XMLUtil.namedChildElements(levelEl,"template").iterator();iu.hasNext();)
			{
				Element useElement = iu.next();
				if (useElement.getAttribute("fromFile").equals("no"))
				{
					CDATemplate templ = new CDATemplate(this, useElement, lev);
					templates.put(templ.fullTemplateId(), templ);	
					trace("Template with no file " + templ.fullTemplateId());
				}
			}
		}
		
		
		writeTemplateSummary();
	}
	
	/**
	 * @param patternId value of the 'id' attribute of a <pattern> element, 
	 * with values such as 'p-2.16.840.1.113883.10.20.1.2-errors' 
	 * @return the template id
	 * @throws MapperException
	 */
	private String getFullTemplateId(String patternId)
	throws MapperException
	{
		StringTokenizer st = new StringTokenizer(patternId,"-");
		if (st.countTokens() != 3) throw new MapperException("Unexpected form of pattern id: " + patternId);
		st.nextToken(); // ignore the initial 'p'
		return st.nextToken();
	}

	/**
	 * @param patternId value of the 'id' attribute of a <pattern> element
	 * with values such as 'p-2.16.840.1.113883.10.20.1.2-errors' 
	 * @return the phase of the template - 'errors', 'warning' and so on
	 * @throws MapperException
	 */
	private String getTemplatePhase(String patternId)
	throws MapperException
	{
		StringTokenizer st = new StringTokenizer(patternId,"-");
		if (st.countTokens() != 3) throw new MapperException("Unexpected form of pattern id: " + patternId);
		st.nextToken();st.nextToken(); // ignore the initial 'p' and the template id
		return st.nextToken();
	}
	
	/**
	 * @param fullTemplateId a full template id string
	 * @return the template element in the template usage file; or null if there is none
	 */
	private Element getTemplateUsageElement(String fullTemplateId) throws MapperException
	{
		Element usageElement = null;
		for (Iterator<Element> it = XMLUtil.namedChildElements(templateSetEl, "templateLevel").iterator();it.hasNext();)
		{
			Element levelEl = it.next();
			String[] levelAtts = {"level"};
			String[] levelChildren = {"template"};
			XMLUtil.checkAttributes(levelEl, levelAtts);
			XMLUtil.checkChildElements(levelEl, levelChildren);
			
			for (Iterator<Element> is = XMLUtil.namedChildElements(levelEl, "template").iterator();is.hasNext();)
			{
				Element candidate = is.next();
				if (candidate.getAttribute("id").equals(getLocalId(fullTemplateId))) usageElement = candidate;
			}
		}
		return usageElement;
	}

	/**
	 * @param fullTemplateId a full template id string
	 * @return the static integer 'CDA', 'SECTION' etc describing the level of the template
	 */
	private int getTemplateLevel(String fullTemplateId)
	{
		String level = "";
		for (Iterator<Element> it = XMLUtil.namedChildElements(templateSetEl, "templateLevel").iterator();it.hasNext();)
		{
			Element levelEl = it.next();
			String candLevel = levelEl.getAttribute("level");
			for (Iterator<Element> is = XMLUtil.namedChildElements(levelEl, "template").iterator();is.hasNext();)
			{
				Element candidate = is.next();
				if (candidate.getAttribute("id").equals(getLocalId(fullTemplateId))) level = candLevel;
			}
		}
		int lev = UNDEFINED;
		for (int i = 0; i < levelNames.length; i++) if (level.equals(levelNames[i])) lev = i;
		return lev;
	}

	/**
	 * @param fullTemplateId
	 * @return the local id used for template ids in this set in the template usage file
	 */
	private String getLocalId(String fullTemplateId)
	{
		String localId = fullTemplateId;
		
		// the local id of the root template is 'root'
		if (fullTemplateId.equals(rootTemplateId)) localId = "root";
		
		// for all other templates, strip off the base and '.' to get the local id (if you can)
		else if ((!idBase.equals("")) && (fullTemplateId.startsWith(idBase))) 
			localId = fullTemplateId.substring(idBase.length() + 1);

		return localId;
	}
	
	/**
	 * @param localId a local template id such as '5', as used in the template usage file
	 * for this template set
	 * @return the template with that local id
	 */
	public CDATemplate getTemplateByLocalId(String localId)
	{
		String fullTemplateId;
		if (localId.equals("root")) fullTemplateId = rootTemplateId;
		else
		{
			if (idBase.equals("")) fullTemplateId = localId;
			else fullTemplateId = idBase + "." + localId;
		}
		return getTemplateByFullId(fullTemplateId);
	}


	
	private void writeTemplateSummary() throws MapperException
	{
		trace("");
		trace("Summary of template set '" + getName() + "'");
		for (Enumeration<String> en = templates.keys();en.hasMoreElements();)
		{
			String fullTemplateId = en.nextElement();
			String localId = getLocalId(fullTemplateId);
			CDATemplate temp = templates.get(fullTemplateId);
			int defs = temp.definingAssertions().size();
			int cons = temp.constrainingAssertions().size();
			trace("template " + localId + "(" + fullTemplateId + ") defines: " + defs + "; constrains: " + cons);
			if ((defs == 0) && (cons > 0))
			{
				for (Iterator<TemplateAssertion> it = temp.definingAssertions().iterator();it.hasNext();)
				{
					TemplateAssertion ta = it.next();
					trace(ta.testNode().stringForm());
				}				
			}
		}
		trace("End of template summary");
	}
	
	private void trace(String s) {if (tracing()) System.out.println(s);}
	

}
