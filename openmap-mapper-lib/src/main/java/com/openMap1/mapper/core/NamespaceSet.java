package com.openMap1.mapper.core;

import java.util.Iterator;
import java.util.Vector;
import com.openMap1.mapper.util.GenUtil;
import javax.xml.namespace.NamespaceContext;


import com.openMap1.mapper.core.NamespaceException;
import com.openMap1.mapper.GlobalMappingParameters;
import com.openMap1.mapper.Namespace;

/**
 * The set of namespaces used in an XML document
 * @author robert
 *
 */
public class NamespaceSet implements NamespaceContext
{
    private Vector<namespace> namespaces;

    /**
     * constructor for an empty set of namespaces
     */
    public NamespaceSet()
    {
        namespaces = new Vector<namespace>();
    }
    
    /**
     * constructor from the namespaces of a mapping set
     * @param gmp
     */
    public NamespaceSet(GlobalMappingParameters gmp)throws NamespaceException
    {
        namespaces = new Vector<namespace>();
        if (gmp != null)
        for (Iterator<Namespace> it = gmp.getNameSpaces().iterator();it.hasNext();)
        	{namespaces.add(new namespace(it.next()));}
    }
    

    /**
     * add a new namespace to the set of namespaces
     * @param ns the namespace to be added. 
     * It is not allowed to add a namespace with the same prefix 
     * as an existing namespace, and a different URI; 
     * but you can have several prefixes for the same URI
     */
    public void addNamespace(namespace ns) throws NamespaceException
    {
    	if (getByPrefix(ns.prefix()) != null)
    	{
    		String previousURI = getByPrefix(ns.prefix()).URI();
    		if (!(previousURI.equals(ns.URI())))
    			{throw new NamespaceException("Cannot add another namespace with existing prefix '" 
    					+ ns.prefix() + "' and URI '" + ns.URI() + "' not equal to '" + previousURI + "'");}
    	}
    	else {namespaces.addElement(ns);}
    }
    
    /**
     * remove from the set one namespace with the uri, if it exists
     * @param uri
     */
    public void removeOneNamespace(String uri)
    {
    	int remove = -1;
    	for (int i = 0; i < namespaces.size();i++)
    	{
    		namespace ns = namespaces.get(i);
    		if (ns.URI().equals(uri)) remove = i;
    	}    	
    	if (remove > -1) namespaces.remove(remove);
    }
    
    /**
     * @param prefix
     * @return a namespace set identical to this one, except that if this 
     * set contains any default namespace with prefix "", that namespace is
     * altered to have the given prefix
     * @throws NamespaceException
     */
    public NamespaceSet withPrefixForDefaultNamespace(String prefix)
    throws NamespaceException
    {
    	NamespaceSet withPrefix = new NamespaceSet();
    	for (Iterator<namespace> it = namespaces.iterator(); it.hasNext();)
    	{
    		namespace next = it.next();
    		if (next.prefix().equals("")) withPrefix.addNamespace(new namespace(prefix,next.URI()));
    		else withPrefix.addNamespace(next);
    	}
    	return withPrefix;
    }
    
    /**
     * @return a namespace prefix which does not clash with any existing prefix in the set
     */
    public String nonClashPrefix()
    {
    	int index = 0;
    	boolean clash = true;
    	String prefix = "p";
    	while (clash)
    	{
        	prefix = "p" + index;
        	clash = (getByPrefix(prefix)!= null);
        	index++;    		
    	}
    	return prefix;  	  
    }

    
    //-----------------------------------------------------------------------------------------------
    //                                 interface NamespaceContext
    //-----------------------------------------------------------------------------------------------

    /**
     * Get Namespace URI bound to a prefix 
     */
    public String getNamespaceURI(String prefix)
    {
    	if (getByPrefix(prefix) != null) return getByPrefix(prefix).URI();
    	return null;
    }
    
    /** 
     * get a prefix bound to a namespace URI.
     * If there is more than one, pick an arbitrary one
     */
    public String getPrefix(String namespaceURI)
    {
    	if (getByURI(namespaceURI) != null) return getByURI(namespaceURI).prefix();
    	return null;
    }
    
    /** 
     * get all prefixes bound to a namespace URI.
     */
    public Iterator<String> getPrefixes(String namespaceURI)
    {
    	Vector<String> prefixes = new Vector<String>();
        for (int i = 0; i < size(); i++)
        {
            namespace temp = namespaces.elementAt(i);
            if (temp.URI().equals(namespaceURI)) prefixes.add(temp.prefix());
        }
    	return prefixes.iterator();
    }
    
    //-----------------------------------------------------------------------------------------------
    //                            
    //-----------------------------------------------------------------------------------------------

    /**
     * @return the number of namespaces in the set
     */
    public int size() {return namespaces.size();}

    /**
     * write the namespaces to some output (generally System.out, for test purposes)
     */
    public void write()
    {
    	GenUtil.message("Writing namespaces: ");
        for (int i = 0; i < size(); i++)
        {
            namespace temp = namespaces.elementAt(i);
            GenUtil.message(temp.prefix() + ": '" + temp.URI() + "'");
        }
    }

    /**
     * get the namespace with given prefix, or null if there is none
     */
    public namespace getByPrefix(String prefix)
    {
        int i;
        namespace res, temp;
        res = null;
        for (i = 0; i < size(); i++)
        {
            temp = namespaces.elementAt(i);
            if (temp.prefix().equals(prefix)) res = temp;
        }
        return res;
    }

    /**
     * get an arbitrary one of the namespaces with given URI, or null if there is none
     */
    public namespace getByURI(String URI)
    {
        int i;
        namespace res, temp;
        res = null;
        for (i = 0; i < size(); i++)
        {
            temp = namespaces.elementAt(i);
            if (temp.URI().equals(URI)) res = temp;
        }
        return res;
    }

    /**
     * get the namespace with given index
     */
    public namespace getByIndex(int i)
    {
        namespace res = null;
        if ((i > -1) && (i < size()))
        {
            res = namespaces.elementAt(i);
        }
        else
            {GenUtil.message("Namespace index " + i
                + " is not between 0 and " + size() + ".");}
        return res;
    }

    /**
     * true if this namespace set has a default namespace
     */
    public boolean hasDefault()
        {return (getByPrefix("") != null);}

    
    
    /**
    * convert the prefix of a tag name from the prefix used
    * in this namespace set, to the prefix used for the same URI
    * in some other namespace set 'toNS'.
    * 
    * Write an error message and return null if:
    * (a) the prefix does not occur in this namespace set, or
    * (b) the URI does not occur in the target namespace set.
    *
    * If a tag name has no prefix (prefix(tagName) = "")
    * and this namespace set has a default namespace (prefix = ""),
    * the tag name is assumed to be in the default namespace of this set,
    * and its prefix converted appropriately.
    *
    * If a tag name has no prefix (prefix(tagName) == "")
    * and this namespace set has no default prefix,
    * the tag name is assumed to be in no namespace, and is not changed.
     * 
     */
    public String convertPrefix(String tagName, NamespaceSet toNSS) throws NamespaceException
    {
        String res,pref;
        namespace thisNS, thatNS;

        res = null;
        pref = prefix(tagName); //cannot be null
        thisNS = getByPrefix(pref);
        /* if pref = "" and this namespace set has a default namespace,
        thisNS will be the default namespace. */
        if (thisNS != null)
        {
            thatNS = toNSS.getByURI(thisNS.URI());
            if (thatNS != null)
            {
                res = newPrefix(tagName,thatNS.prefix());
            }
            else
            {
            	throw new NamespaceException("Error converting prefixes: no namespace with URI '"
                    + thisNS.URI() + "' in target namespace set, "
                    + "to match namespace in source namespace set with prefix '"
                    + pref + "'");
            }
        }
        else
        {
            if (pref.equals(""))
            // as this NS was not found, the node is in no namespace
            {
                res = tagName;
            }
            else throw new NamespaceException("Error converting prefixes: no namespace with prefix '"
                + pref + "' from node name '" + tagName
                + "'. in source namespace set.");
        }
        return res;
    }

    
    /**
     * compare a nodeName in an XPath in this XMLFile (with its namespace set)
     * with a nodeName in an XPath which may be in another XMLFile
     */
    public boolean equalNodeName(String thisNodeName,NamespaceSet thatNSSet,String thatNodeName)
    throws NamespaceException
    {
         return (convertPrefix(thisNodeName,thatNSSet).equals(thatNodeName));
    }

    /*
    * Extract a namespace prefix, or "" if there is none,
    * taking account of 'axes' before the prefix.
    * The prefix does not have to be in this namespace set.
    * Cannot return null.
    */
    public static String prefix(String nodeName)
    {
        int i;
        String res, temp;
        res = "";

        temp = nodeName;
        if (temp.length() > 2) for (i = 1; i < temp.length()-1; i++)
        {
            // pick up substrings before a single ':'
            if  (temp.charAt(i) == ':')
                {res = temp.substring(0,i);}
        }
        return res;
    }

    /**
     * Strip off the prefix to give the local name
     */
    public static String localName(String nodeName)
    {
        String res, temp, pref;
        res = null;
        if (nodeName != null)
        {
        temp = nodeName;
        pref = prefix(nodeName); // cannot be null
        if (pref.equals("")) // no prefix to strip off
        {
            res = temp;
        }
        else // strip off prefix and ':'
        {
            res = temp.substring(pref.length()+1);
        }
        }
        else GenUtil.message("Null node name");
        return res;
    }

   /* convert a node name, so it has a new prefix,
    * taking account axes before the prefix.
    * newPrefix = "" if there is to be no prefix. */
   public static String newPrefix(String nodeName, String newPrefix) throws NamespaceException
   {
        String res;
        res = null;
        if ((nodeName != null) && (newPrefix !=null))
        {
            if (newPrefix.equals(""))
                {res = localName(nodeName);}
            else
                {res = newPrefix + ":" + localName(nodeName);}
        }
        else throw new NamespaceException("Null node or prefix name");
        return res;
   }
   
   /**
    * Find the namespace with this URI in the namespaceSet;
    * or if you cannot find it, make up a new prefix, create the namespace,
    * and add it to the set.
    * Return the namespace.
    */
    public namespace findOrAddNamespace(String URI) throws NamespaceException
    {
        namespace ns = getByURI(URI);
        if (ns == null)
        {
        	int i = 0; // keep boosting i until there is no prefix name clash
        	boolean added = false;
        	while (!added)
        	{
        		String prefix = "p" + (size() + i);
        		i++;
        		if (getByPrefix(prefix)== null)// only add if there is no prefix clash
        		{
        			ns = new namespace(prefix,URI);
        			addNamespace(ns);
        			added = true;
        		}
        	}
        }
        return ns;
    }


}
