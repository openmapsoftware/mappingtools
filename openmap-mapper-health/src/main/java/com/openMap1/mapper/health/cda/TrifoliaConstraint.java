package com.openMap1.mapper.health.cda;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.XMLUtil;

public class TrifoliaConstraint {

/**
 * @return the path from the template node or outer constraint node, to the node this constraint applies to
 */
public String context() {return context;}
private String context;

/**
 * @return the deep context, including a path through outer constraints from the template
 */
public String deepContext() {return deepContext;}
private String deepContext;

private String cardinality;

private String[] allowedCardinalities = {"0..0","1..1","0..1","0..*","1..*",""};

public boolean hasCardinalities() {return hasCardinalities;}
private boolean hasCardinalities = false;

/**
 * @return min cardinality, 0 or 1
 */
public int minCardinality() {return minCardinality;}
private int minCardinality;

/**
 * @return max cardnality, 1 or -1 (= *)
 */
public int maxCardinality() {return maxCardinality;}
private int maxCardinality;

private String conformance;
private String[] allowedConformance = {"SHALL","SHOULD","MAY",""};

public boolean hasConformance() {return hasConformance;}
private boolean hasConformance = false;

static int SHALL = 0;
static int SHOULD = 1;
static int MAY = 2;

/**
 * @return int constant for SHALL, SHOULD, or MAY
 */
public int conformance()
{
	if (conformance.equals("SHALL")) return SHALL;
	if (conformance.equals("SHOULD")) return SHOULD;
	if (conformance.equals("MAY")) return MAY;
	return -1;
}

/**
 * @return constraint number as in the IG
 */
public int number() {return number;}
private int number;

/**
 * @return true if this constraint defines a single value for the node
 */
public boolean hasSingleValue() {return hasSingleValue;}
private boolean hasSingleValue = false;

/**
 * @return  the code for a single value defined by this constraint
 */
public String singleValueCode() {return singleValueCode;}
private String singleValueCode = null;


/**
 * @return  the display name for a single value defined by this constraint
 */
public String singleValueDisplayName() {return singleValueDisplayName;}
private String singleValueDisplayName = null;

/**
 * @return the oid of a template required by this constraint
 */
public String containedTemplateOid() {return containedTemplateOid;}
private String containedTemplateOid;

/**
 * @return List of immediately nested constraints
 */
public List<TrifoliaConstraint> nestedConstraints() {return nestedConstraints;}
private Vector<TrifoliaConstraint> nestedConstraints = new Vector<TrifoliaConstraint>();

/**
 * @return depth of nesting of this constraint - from 0 upwards
 */
public int depth() {return depth;}
private int depth;

	
	public TrifoliaConstraint(Element constraintEl, int depth, String outerContext) throws MapperException
	{
		try
		{
			this.depth = depth;
			context = constraintEl.getAttribute("context");
			deepContext = context;
			if (!(outerContext.equals(""))) deepContext = outerContext + "/" + context;
						
			containedTemplateOid = constraintEl.getAttribute("containedTemplateOid");

			cardinality = constraintEl.getAttribute("cardinality");
			if (!(cardinality.equals(""))) hasCardinalities = true;
			if (!GenUtil.inArray(cardinality, allowedCardinalities)) 
				throw new MapperException("Disallowed cardinality: " + cardinality);
			minCardinality = 0;
			maxCardinality = -1;
			if (cardinality.startsWith("1")) minCardinality = 1;
			if (cardinality.endsWith("1")) maxCardinality = 1;
			if (cardinality.endsWith("0")) maxCardinality = 0;

			number = new Integer(constraintEl.getAttribute("number")).intValue();
			
			conformance = constraintEl.getAttribute("conformance");
			if (!(conformance.equals(""))) hasConformance = true;
			if (!GenUtil.inArray(conformance, allowedConformance)) 
				throw new MapperException("Disallowed conformance: " + conformance);
			
			// single value constraints
			Element valueEl = XMLUtil.firstNamedChild(constraintEl, "SingleValueCode");
			if (valueEl != null)
			{
				hasSingleValue = true;
				singleValueCode = valueEl.getAttribute("code");
				singleValueDisplayName = valueEl.getAttribute("displayName");
			}
			
			// nested constraints
			Vector<Element> nestedEls = XMLUtil.namedChildElements(constraintEl, "Constraint");
			for (int i = 0; i < nestedEls.size();i++)
				nestedConstraints.add(new TrifoliaConstraint(nestedEls.get(i),depth + 1,deepContext));

		}
		catch (Exception ex) {throw new MapperException("Cannot make Trifolia Constraint: " + ex.getMessage());}
	}
	
	/**
	 * if a constraint requires some nested template, then that constraint has a nested constraint
	 * giving the required template oid as its 'containedTemplateOid' attribute 
	 * @return the required template oid; or null if none is specified
	 */
	public String requiredTemplateOid()
	{
		String oid = null;	
		// there may be several nested constraints, only one of which is a contained template oid
		if (nestedConstraints.size() > 1) for (int c = 0; c < nestedConstraints.size(); c++)
		{
			String possibleOid = nestedConstraints.get(c).containedTemplateOid();
			if (!(possibleOid.equals(""))) oid = possibleOid;
		}		
		return oid;
	}
	
	/**
	 * add to a table of fixed value constraints, for this template and its nested templates
	 * @param fvConstraints
	 */
	public void addFixedValueConstraints(Hashtable<String,String> fvConstraints)
	{
		if (hasSingleValue)
		{
			String path = deepContext;
			
			// the attribute 'code' can stand for some other attribute, if the context is that attribute
			if (!(singleValueCode.equals(""))) 
			{
				if (!context.startsWith("@")) path = path + "/@code";
				fvConstraints.put(path, singleValueCode);
			}
			
			// 'displayName' is ignored if the context is an attribute
			if ((!(singleValueDisplayName.equals(""))) && (!context.startsWith("@"))) 
				fvConstraints.put(path + "/@displayName", singleValueDisplayName);
		}
		for (Iterator<TrifoliaConstraint> it = nestedConstraints.iterator();it.hasNext();)
			it.next().addFixedValueConstraints(fvConstraints);
	}
	
	// private String[] csvHeader = {"Template_Name","Template_id","XPath","Constraint_id","Card.","Value","Comment","Response"};
	
	/**
	 * add rows to the csv file for this constraint and all its nested constraints
	 * @param csvOfConstraints
	 */
	public void addToCSVFile(String templateName, String templateId, Vector<String[]> csvOfConstraints)
	{
		String[] row = new String[8];
		
		row[0] = templateName;
		row[1] = templateId;
		row[2] = deepContext();
		row[3] = new Integer(number).toString();
		row[4] = "";
		if (cardinality != null) row[4] = cardinality;
		row[5] = "";
		if (singleValueCode() != null) row[5] = singleValueCode();
		row[6] = "";
		row[7] = "";
		
		csvOfConstraints.add(row);
		for (Iterator<TrifoliaConstraint> it = nestedConstraints().iterator();it.hasNext();)
			it.next().addToCSVFile(templateName, templateId, csvOfConstraints);
	}
	
	private void message(String s) {System.out.println(s);}

}
