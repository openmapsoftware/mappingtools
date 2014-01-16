package com.openMap1.mapper.writer;

import com.openMap1.mapper.core.*;
import com.openMap1.mapper.util.*;

import org.w3c.dom.*;

/**
 * stores a possible value on the RHS of a when-condition,
 * and the root path to the node where it will appear in the XML.
 * 
 * If this node also represents a property,
 * stores the class/subset of the object and the name of the property.
 */


public class whenValue {

private Xpth rootPath; // path from the root the the node where the when-condition value is to be stored
private String value; // value to be stored there

// if the when-condition value is also a property....
private ClassSet cSet; // class and subset of the object
private String propName; // name of the property

public Xpth rootPath() {return rootPath;}
public String value() {return value;}
public ClassSet cSet() {return cSet;}
public String propName() {return propName;}

public whenValue(Xpth rp, String val, ClassSet cs, String pName)
{
    rootPath = rp;
    value = val;
    cSet = cs;
    propName = pName;
}

public String stringForm()
{
    String res = " value '" + value + "' at path '" + rootPath.stringForm() + "'";
    if (cSet != null)
        {res = res + " for property " + cSet.stringForm() + ":" + propName;}
    return res;
}

// constructor from XML input
public whenValue(Element el, NamespaceSet nss) throws XpthException
{
    value = el.getAttribute("value");
    rootPath = new Xpth(nss,el.getAttribute("rootPath"));
    propName = el.getAttribute("propName");
    Element cSetEl = XMLUtil.firstNamedChild(el,"ClassSet");
    if (cSetEl != null) {cSet = new ClassSet(cSetEl);}
}

    // method for output to XML
    public Element XMLOut(String tagName, XMLOutputFile xout)
    {
        Element el = xout.newElement(tagName);
        el.setAttribute("value",value);
        el.setAttribute("rootPath",rootPath.stringForm());
        el.setAttribute("propName",propName);
        if (cSet != null) {el.appendChild(cSet.XMLOut("ClassSet",xout)); }
        return el;
    }
    
public boolean equals(whenValue wv)
{
    /*  No need to test ClassSet and propName; two whenValues cannot be created
    with these different, if they have the same rootPath. */
    return ((rootPath.equalPath(wv.rootPath)) && (value.equals(wv.value)));
}

	/**
	 * replace aclassSet, when importing WProcs
	 * @param oldCSet
	 * @param newCSet
	 */
	public void replaceClassSet(ClassSet oldCSet, ClassSet newCSet)
	{
		if ((cSet != null) && (cSet.equals(oldCSet))) cSet = newCSet;	
	}

}
