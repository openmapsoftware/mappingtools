package com.openMap1.mapper.reader;

import java.util.Iterator;
import java.util.Vector;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.messageChannel;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

/**
 * The main implementation of the interface objectToken, 
 * which provides tokens for objects represented in an XML
 * instance ,to be passed back and forth between an XOReader
 * and the application which uses it.
 * 
 * @author robert
 *
 */

public class objectRep implements objectToken
{
    private Node objNode;
    private String className;
    private String subset; // subset in the object source
    private XOReader reader; // the XOReader which found this objectRep

    
    /**
     * @param objNode the XML Node that represents an object
     * @param className the class of the object represented
     * @param subset the subset represented
     * @throws MapperException if the class name or subset is invalid
     */
    public objectRep(Node objNode, String className, String subset, XOReader reader) throws MapperException
    {
        this.objNode = objNode;

        if (className == null) throw new MapperException("Null class name in objectRep");
        if (className.equals("")) throw new MapperException("Empty class name in objectRep");
        if (subset == null) throw new MapperException("Null subset in objectRep");
        this.className = className;
        this.subset = subset;
        this.reader = reader;
    }
    
    /** an ObjectRep can never be empty */
    public boolean isEmpty() {return false;}

    /** the XML Node which represents the object */
    public Node objNode(){return objNode;}

    /** the class name of the represented object */
    public String className(){return className;}

    /** the subset of the represented object */
    public String subset(){return subset;}

    /** key of the object - to implement interface objectToken */
    public Object objectKey() {return objNode;}
    
    /** the XOReader which found this objectRep  */
    public XOReader reader() {return reader;}

    /** return ClassSet ( = class and subset) in the source of the object - e.g. the XML
    source document where the object is represented. */
    public ClassSet cSet()
    {
    	ClassSet cs = null;
        try {cs = new ClassSet(className,subset);} 
        catch (Exception ex) {} // null class name or subset are impossible - constructor
        return cs;
    }

    /* write details of the element which represents the object. */
    public void write(messageChannel mChan)
    {
        if (objNode instanceof Element)
        {
            Element el = (Element)objNode;
            String name = el.getLocalName();
            NamedNodeMap nm = el.getAttributes();
            String attVals = "";
            for (int i = 0; i < nm.getLength(); i++)
            {
                Node n = nm.item(i);
                if (n instanceof Attr)
                {
                    Attr att = (Attr)n;
                    attVals = attVals + att.getName() + ":'" + att.getValue() + "' ";
                }
            }
            mChan.message("Element '" + name + "'; attributes " + attVals);
        }
        else {mChan.message("Object node is not an Element");}
    }

    
    public static Vector<objectToken> vCopy(Vector<objectToken> v)
    {
   	 Vector<objectToken> res = new Vector<objectToken>();
   	 for (Iterator<objectToken> it = v.iterator();it.hasNext();) res.add(it.next());
   	 return res;
    }

}
