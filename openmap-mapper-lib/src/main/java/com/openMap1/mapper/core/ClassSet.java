package com.openMap1.mapper.core;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XMLOutputFile;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Element;

/** the combination of a class name and a subset name.
 * <p>
 * When there are several mappings to the same class in one XML structure, subset distinguishes those mappings.
 *  */
public class ClassSet
{
    private String className;
    private String subset;

    /** the class name */
    public String className() {return className;}
    /** the subset name */
    public String subset() {return subset;};
    
    /**
     * a non-clashing String key, for use in Hashtables, using separator characters
     * which are assumed never to occur in class names
     * @return
     */
    public String key() {return (className + "|£" + subset);}

    /**
     *
     * @param qualifiedClassName class name - preceded by package name and '.' if the package name is
     * non-empty
     * @param subset String subset name (must not be null)
     */
    public ClassSet(String qualifiedClassName, String subset) throws MapperException
    {
        if (qualifiedClassName == null)
        	{throw new MapperException("Cannot form a classSet with null className");}
        if (subset == null)
        	{throw new MapperException("Cannot form a classSet with null subset");}
        className = qualifiedClassName;
        this.subset = subset;
    }

    /**
     *
     * @param className String class name (must not be null)
     * @param packageName name of the package the class is in
     * @param subset String subset name (must not be null)
     */
    public ClassSet(String className, String packageName, String subset) throws MapperException
    {
        if (className == null)
        	{throw new MapperException("Cannot form a classSet with null className");}
        if (subset == null)
        	{throw new MapperException("Cannot form a classSet with null subset");}
        this.className = className;
        if ((packageName != null) && (!packageName.equals("")))
        	this.className = packageName + "." + className;
        this.subset = subset;
    }


    /**
     * constructor for input from XML
     * @param el Element: the XML element, which has attributes 'class' and 'subset'
     */
    public ClassSet(Element el)
    {
        className = el.getAttribute("class");
        subset = el.getAttribute("subset");
    }

    /**
     * equality test
     * @param cs classSet the other classSet
     * @return boolean true if the class name and subset are equal
     */
    public boolean equals(ClassSet cs)
    {
    	if (cs == null) return false;
        return ((className.equals(cs.className())) && (subset.equals(cs.subset())));
    }

    /**
     * String form of classSet name
     * @return String 'class', subset 'subset', unless subset is empty
     */
    public String stringForm()
    {
        String res = null;
        if (subset.equals("")) {res = "'" + className + "'";}
        else {res = "'" + className + "', subset '" + subset + "'";}
        return res;
    }
    /**
     * prettier form of classSet name
     * @return String class(subset), unless subset is empty
     */
    public String prettyForm()
    {
        String res = null;
        if (subset.equals("")) {res = className ;}
        else {res = className + "(" + subset + ")";}
        return res;
    }

    /** true if this classSet is in a vector v of classSets */
    public boolean inVector(Vector<ClassSet> v)
    {
        int i;
        ClassSet c;
        boolean res = false;
        for (i= 0; i < v.size(); i++)
        {
            c = v.elementAt(i);
            if (equals(c)) res = true;
        }
        return res;
    }
    

    /**
     * method for output to XML, of an element with attributes 'class' and 'subset'
     * @param tagName String
     * @param xout XMLFile
     * @return Element
     */
    public Element XMLOut(String tagName, XMLOutputFile xout)
    {
        Element el = xout.newElement(tagName);
        el.setAttribute("class",className);
        el.setAttribute("subset",subset);
        return el;
    }
    
    public static Vector<ClassSet> vCopy(Vector<ClassSet> v)
    {
   	 Vector<ClassSet> res = new Vector<ClassSet>();
   	 for (Iterator<ClassSet> it = v.iterator();it.hasNext();) res.add(it.next());
   	 return res;
    }

}
