package com.openMap1.mapper.converters;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.TemplateFilter;
import com.openMap1.mapper.MappedStructure;

/**
 * Wrapper class which converts any ASTM CCR message into a form 
 * which is more convenient for mapping, or does the reverse 'out'
 * transform.
 * 
 * @author robert
 *
 */

public class CCRConverter extends AbstractMapperWrapper implements MapperWrapper {
	
	protected boolean tracing() {return false;}
	
	private static String CCR_ROOT = "ContinuityOfCareRecord";
	
	/**
	 * key = Actor id string
	 * element: keys are roles played by that actor; element "1"
	 */
	private Hashtable<String,Hashtable<String,String>> actorRoles;
	
	// defined actor roles
	private static String PATIENT = "patient";
	private static String HCP = "hcp";
	private static String SUPPORT = "support";
	private static String FAMILY = "family";
	private static String HCP_ORGANISATION = "hcpOrganisation";
	private static String INSURER = "insurer";
	private static String SUPPLIER = "supplier";
	private static String DOCUMENT_URL = "documentURL";
	private static String IT_SYSTEM = "ITSystem";
	
	// ends of XPaths which imply that an actor (whose ID is at that location) plays a role
	private String[][] rolePaths = {
			{PATIENT, "/Patient/ActorID"},	
			{INSURER, "/PaymentProvider/ActorID"},	
			{SUPPORT, "/SupportProvider/ActorID"},	
			{FAMILY, "/FamilyMember/ActorID"},	
			{DOCUMENT_URL, "/ReferenceID"},	
			{SUPPLIER, "/Manufacturer/ActorID"},	
			{HCP, "/HealthCareProviders/Provider/ActorID"},	
			{HCP_ORGANISATION, "/Encounter/Locations/Location/Actor/ActorID"},	
			{IT_SYSTEM, "/Source/Actor/ActorID"},	
	};
	
	/**
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare spare argument, set to name of topElementDef
	 */
	public CCRConverter(MappedStructure ms, Object spare) throws MapperException
	{
		super(ms,spare);
	}
	
	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	public int transformType() {return AbstractMapperWrapper.XML_TYPE;}
	
	/**
	 * @return the file extension of the outer document
	 */
	public String fileExtension() {return "*.xml";}
	
	//--------------------------------------------------------------------------------------------
	//                     Transform methods in the MapperWrapper Interface
	//--------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param incoming; must be of class Element or InputStream
	 * @return the result of the in wrapper transform
	 */
	public Document transformIn(Object CCRRootObj) throws MapperException
	{
		// preliminaries
		if (!(CCRRootObj instanceof Element)) throw new MapperException("CCR root is not an Element");
		Element CCRRoot = (Element)CCRRootObj;
		if (!(CCR_ROOT.equals(XMLUtil.getLocalName(CCRRoot))))
			throw new MapperException("Root element name is not '" + CCR_ROOT + "'");
		String CCRRootPath = "/" + CCR_ROOT;
		inResultDoc = XMLUtil.makeOutDoc();
		
		// pre-scan to find the roles played by actors
		actorRoles = new Hashtable<String,Hashtable<String,String>>();
		scanDocument(CCRRoot, CCRRootPath, AbstractMapperWrapper.IN_PRE_SCAN);
		
		// vanilla copy unless overridden for specific paths
		Element inRoot = scanDocument(CCRRoot, CCRRootPath, AbstractMapperWrapper.IN_TRANSFORM);
		inResultDoc.appendChild(inRoot);
		return inResultDoc;
	}

	
	/**
	 * @param outgoing the root element produced by the XMLWriter when 
	 * writing out from a class model instance (seen through an objectGetter)
	 * @return the result of the out wrapper transform; 
	 * must be of class Document or OutputStream
	 */
	public Object transformOut(Element outgoing) throws MapperException
	{

		if (!(CCR_ROOT.equals(XMLUtil.getLocalName(outgoing))))
			throw new MapperException("Root element name of 'in' file is not '" + CCR_ROOT + "'");
		String CCRRootPath = "/" + CCR_ROOT;
		outResultDoc = XMLUtil.makeOutDoc();

		// vanilla copy unless overridden for specific paths
		Element outRoot = scanDocument(outgoing, CCRRootPath, AbstractMapperWrapper.OUT_TRANSFORM);
		outResultDoc.appendChild(outRoot);
		return outResultDoc;
	}

	
	//--------------------------------------------------------------------------------------------
	//                               Pre-scan before In Transform
	//--------------------------------------------------------------------------------------------
	
	/**
	 * action for each node in the pre-scan to find out which actor ids play which roles
	 */
	protected Element inPreScanNode(Element el, String path) throws MapperException
	{
		recordRole(el,path);
		return null;
	}
	
	
	/**
	 * if the path to an element is associated with a role, 
	 * record that the actor with id on the element plays the role
	 * @param el an element 
	 * @param path the path to the element
	 */
	private void recordRole(Element el, String path)
	{
		String id = XMLUtil.getText(el);
		for (int p = 0; p < rolePaths.length; p++)
		{
			String[] rp = rolePaths[p]; // [0] = role; [1] = path end
			if (path.endsWith(rp[1])) addRole(id, rp[0], path);
		}		
	}
	
	private void addRole(String id, String role, String path)
	{
		Hashtable<String,String> roles = actorRoles.get(id);
		if (roles == null) roles = new Hashtable<String,String>();
		roles.put(role,"1");
		actorRoles.put(id,roles);
		//trace("id '" + id + "'; role " + role + " at path " + path);		
	}

	
	//--------------------------------------------------------------------------------------------
	//                    Special methods for special XPaths in the In Transform
	//--------------------------------------------------------------------------------------------
	
	/**
	 * copy for each node in the in transform
	 * super.inTransformNode makes a vanilla copy, 
	 * which this method overrides for specific XPaths
	 */
	protected Element inTransformNode(Element el, String path)  throws MapperException
	{
		// attach role attributes to <Actor> elements and repeat any actor with more than one role
		if (path.equals("/ContinuityOfCareRecord/Actors"))
		{
			Element actorsInEl = XMLUtil.newElement(inResultDoc, "Actors");
			Vector<Element>actors = XMLUtil.namedChildElements(el, "Actor");
			String newPath = path + "/Actor";
			for (Iterator<Element> ia = actors.iterator();ia.hasNext();)
			{
				Element actorOut = ia.next();
				Element idEl = XMLUtil.firstNamedChild(actorOut,"ActorObjectID");
				if (idEl != null) // it never should be null; all actors should have ids
				{
					String id = XMLUtil.getText(idEl);
					// repeat each <Actor> child for all of its roles
					Hashtable<String,String> roles = actorRoles.get(id);
					// an actor with no roles identified elsewhere in the document does not get copied across
					if (roles != null) for (Enumeration<String> en = roles.keys(); en.hasMoreElements();)
					{
						String role = en.nextElement();
						// copy the tree below the <Actor>, maybe making changes
						Element actorIn = scanDocument(actorOut, newPath, AbstractMapperWrapper.IN_TRANSFORM);
						actorIn.setAttribute("role", role);
						actorsInEl.appendChild(actorIn);
					}
				}
			}
			return actorsInEl;
		}
		
		// if one of an actor's multiple IDs is the same as its ActorObjectId, change the tag name to ID_C 
		else if (path.equals("/ContinuityOfCareRecord/Actors/Actor/IDs"))
		{
			Element result = null;
			Element idChild = XMLUtil.firstNamedChild(el, "ID");
			if (idChild != null) //it never should be
			{
				String oneId = XMLUtil.getText(idChild);
				if (actorRoles.get(oneId) != null) result = XMLUtil.newElement(inResultDoc, "ID_C");
				else result = XMLUtil.newElement(inResultDoc, "IDs");

				/* deep import all its children; 
				 * no further change below the node with changed name. */
				for (Iterator<Element> it = XMLUtil.childElements(el).iterator();it.hasNext();)
					result.appendChild(inResultDoc.importNode(it.next(), true));
			}
			return result;
		}
		
		// add elements <CauseOfDeath>No</CauseOfDeath> if someone is not dead
		else if (path.equals("/ContinuityOfCareRecord/Body/FamilyHistory/FamilyProblemHistory/FamilyMember/HealthStatus"))
		{
			trace("Health status");
			Element result = super.inTransformNode(el, path);
			// deep copy ; no other changes below this node
			for (Iterator<Element> ie = XMLUtil.childElements(el).iterator();ie.hasNext();)
				result.appendChild(inResultDoc.importNode(ie.next(),true));

			Element desc = XMLUtil.firstNamedChild(result, "Description");
			if (desc != null)
			{
				String description = XMLUtil.getText(XMLUtil.firstNamedChild(desc, "Text"));
				trace(description);
				if (!(("Deceased").equals(description))) 
				{
					Element newCause = XMLUtil.textElement(inResultDoc, "CauseOfDeath","No");
					result.appendChild(newCause);
				}
			}
			return result;
		}
		
		// convert descriptions of family relationships to SNOMED controlled terms
		else if (path.equals("/ContinuityOfCareRecord/Body/FamilyHistory/FamilyProblemHistory/FamilyMember/ActorRole/Text"))
		{
			String relation = XMLUtil.getText(el);
			String snomedRelation = relation;
			if (relation.equalsIgnoreCase("mother")) snomedRelation = "Biological mother";
			if (relation.equalsIgnoreCase("father")) snomedRelation = "Biological father";
			return XMLUtil.textElement(inResultDoc, "Text", snomedRelation);
		}
		
		// other paths - vanilla copy
		else return super.inTransformNode(el, path);
	}


	
	//--------------------------------------------------------------------------------------------
	//                 Special methods for special XPaths in the Out Transform
	//--------------------------------------------------------------------------------------------
	
	
	/**
	 * copy for each node in the out transform
	 * super.inTransformNode makes a vanilla copy, 
	 * which this method overrides for specific XPaths
	 */
	protected Element outTransformNode(Element el, String path)  throws MapperException
	{
		// remove role attributes from <Actor> elements and do not repeat any actor with the same id
		if (path.equals("/ContinuityOfCareRecord/Actors"))
		{
			Element actorsOutEl = XMLUtil.newElement(outResultDoc, "Actors");
			Vector<Element>actors = XMLUtil.namedChildElements(el, "Actor");
			String newPath = path + "/Actor";
			// record all actor ids that have been written out
			Hashtable<String,String> writtenIds = new Hashtable<String,String>();
			for (Iterator<Element> ia = actors.iterator();ia.hasNext();)
			{
				Element actorIn = ia.next();
				Element idEl = XMLUtil.firstNamedChild(actorIn,"ActorObjectID");
				if (idEl != null) // it never should be null; all actors should have ids
				{
					String id = XMLUtil.getText(idEl);
					// if the actor with this id has not been written out already....
					if (writtenIds.get(id) == null)
					{
						Element actorOut = scanDocument(actorIn, newPath, AbstractMapperWrapper.OUT_TRANSFORM);
						actorOut.removeAttribute("role");
						actorsOutEl.appendChild(actorOut);
						writtenIds.put(id,"1");
					}
				}
			}
			return actorsOutEl;
		}
		
		/* if one of an actor's multiple ids was the same as its ActorObjectId, 
		 * so the tag name was changed to ID_C, change the tag name back again. */
		else if (path.equals("/ContinuityOfCareRecord/Actors/Actor/ID_C"))
		{
			Element result = XMLUtil.newElement(outResultDoc, "IDs");

			/* deep import all its children; 
			 * no further change below that node. */
			for (Iterator<Element> it = XMLUtil.childElements(el).iterator();it.hasNext();)
				result.appendChild(outResultDoc.importNode(it.next(), true));	
			return result;
		}
		
		// remove elements <CauseOfDeath>No</CauseOfDeath> if someone is not dead
		else if (path.equals("/ContinuityOfCareRecord/Body/FamilyHistory/FamilyProblemHistory/FamilyMember/HealthStatus"))
		{
			Element result = super.outTransformNode(el, path);
			// deep copy ; not other changes below this node
			for (Iterator<Element> ie = XMLUtil.childElements(el).iterator();ie.hasNext();)
				result.appendChild(outResultDoc.importNode(ie.next(),true));

			Element desc = XMLUtil.firstNamedChild(result, "Description");
			if (desc != null)
			{
				String description = XMLUtil.getText(XMLUtil.firstNamedChild(desc, "Text"));
				if (!(("Deceased").equals(description))) 
				{
					Element cause = XMLUtil.firstNamedChild(result, "CauseOfDeath");
					if (cause != null) result.removeChild(cause);
				}
			}
			return result;
		}
		
		// descriptions of family relationships converted to SNOMED controlled terms - no conversion back needed
		// else if (path.equals("/ContinuityOfCareRecord/Body/FamilyHistory/FamilyProblemHistory/FamilyMember/ActorRole/Text"))

		
		// other paths - vanilla copy
		else return super.outTransformNode(el, path);
	}
	
	//----------------------------------------------------------------------------------------
	//                      Sample property conversion methods
	//----------------------------------------------------------------------------------------
	
	private static String[][] frequencyConverter =
	{	{"Daily","24"},
		{"BID","12"},
		{"QID","6"}
	};

	/**
	 * Conversion from a frequency per day to an hours interval
	 * @param ht
	 * @param frequency
	 * @return
	 */
	public static String frequencyToHoursInterval(Hashtable<?,?> ht, String frequency)
	{
		String interval = frequency; // default if lookup fails
		for (int i = 0; i < frequencyConverter.length; i++)
		{
			String[] pair = frequencyConverter[i];
			if (pair[0].equals(frequency)) interval = pair[1];
		}
		return interval;
	}


	/**
	 * Conversion from an interval in hours to a daily frequency
	 * @param ht
	 * @param interval
	 * @return
	 */
	public static String hoursIntervalToFrequency(Hashtable<?,?> ht, String interval)
	{
		String frequency = interval; // default if lookup fails
		for (int i = 0; i < frequencyConverter.length; i++)
		{
			String[] pair = frequencyConverter[i];
			if (pair[1].equals(interval)) frequency = pair[0];
		}
		return frequency;
	}
	
	//----------------------------------------------------------------------------------------
	//      XSLT versions of wrapper transforms, for inclusion in full XSLT transforms
	//----------------------------------------------------------------------------------------
	

	/**
	 * @param xgen the XSL generator writing transformation templates
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'out' direction.
	 * Templates must have mode = "inWrapper"
	 * 
	 * This method works by reading a file which is a standalone XSL version
	 * of the in-wrapper transform, removing the top template,
	 * then adding all other templates and variables to the full XSLT.
	 * 
	 *  XSLGenerator provides a variable:

	<xsl:variable name="inWrapper_result">
		<xsl:apply-templates mode="inWrapper" />
	</xsl:variable>
	
	which replaces the top template:

    <xsl:template match="/">
		<xsl:apply-templates mode="inWrapper"/>
	</xsl:template>

	 *  of the standalone XSLT file
	 * @throws MapperException
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		
		/* the standalone CCR wrapper in-transform should be in a file CCRInWrapper.xsl, 
		 * in the same folder as the CCR mapper file. */
		boolean isInWrapper = true;
		String xslLocation = templateFilter.getXSLLocation("CCRInWrapper.xsl",isInWrapper);
		
		boolean removerStarterTemplate = true;
		addTemplatesFromFile(xout,xslLocation, removerStarterTemplate);		
	}
	
	/**
	 * @param xout the XSL output file
	 * @param templateFilter has a boolean method to say if a template should be included
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'out' direction.
	 * Templates must have mode = "outWrapper"
	 * @throws MapperException
	 */
	public void addWrapperOutTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		
		/* the standalone CCR wrapper out-transform should be in a file CCROutWrapper.xsl, 
		 * in the same folder as the CCR mapper file. */
		boolean isInWrapper = false;
		String xslLocation = templateFilter.getXSLLocation("CCROutWrapper.xsl",isInWrapper);
		
		// declare the namespace prefix 'ccr'  (used in the wrapper xsl) at the top level of the overall XSL
		xout.topOut().setAttribute("xmlns:ccr", "urn:astm-org:CCR");
		
		boolean removeStarterTemplate	= true;
		addTemplatesFromFile(xout,xslLocation,removeStarterTemplate);
		
	}



}
