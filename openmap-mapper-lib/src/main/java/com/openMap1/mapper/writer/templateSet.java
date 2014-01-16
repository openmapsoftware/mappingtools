package com.openMap1.mapper.writer;
import java.util.*;

import com.openMap1.mapper.core.*;
import com.openMap1.mapper.util.*;

import org.eclipse.core.resources.IFile;

import org.w3c.dom.*;


/**
 * A set of XSLT templates in one file
 * 
 * @author robert
 *
 */
public class templateSet extends XMLInputFile {

    // templates, keyed by template name
    private Hashtable<String, Element> templates;
    /**
     * 
     * @return templates, keyed by template name
     */
    public Hashtable<String, Element> templates() {return templates;}

    private boolean valid;

    public boolean valid() {return valid;}

  /** constructor attempts to open the template file, retrieve and store all templates.
  Sets valid = false if there is any problem. */
  public templateSet(IFile file) throws MapperException
  {
      templates = new Hashtable<String, Element>();
      try
      {
          readXMLFile(file);
          valid = true;
          if (!(getLocName(root()).equals("stylesheet")))
	            {throw new MapperException("Root element of template file '" + file.getName()
	            		+ "' is not a stylesheet element.");}
          if (valid)
          {
              getRootAttributes(root());
              Vector<Element> temps = namedChildElements(root(),"template");
              if (valid) for (int i = 0; i < temps.size(); i++)
              {
                  Element template = temps.elementAt(i);
                  String tempName = template.getAttribute("name");
                  if (templates.get(tempName) != null)
                  {
                	  throw new MapperException("There is more than one conversion template with name '" 
                			  + tempName + "' in template file '"+ file.getName() + "'");
                  }
                  else
                  {
                      templates.put(tempName, template);
                  }
              }
          }
      }
      catch (XMLException ex)
      {
    	  throw new MapperException("Failed to read XSLT template file from location '"
          + file.getName() + "'; " + ex.getMessage());
      }
  }


  // 
  /**
   * @return the template of given name, or null if there is none.
   */
  public Element getTemplate(String name)
      {return templates.get(name);}


    /** get attributes of the top stylesheet node.
    Currently assume that there is only one namespace declaration, for
    the one namespace of xsl elements such as xsl:template
    */
    private void getRootAttributes(Element rootEl) throws MapperException
    {
        int i;
        String attName;
        NamedNodeMap attrs;
        Attr att;
        attrs = rootEl.getAttributes();
        if (attrs != null) for (i = 0; i < attrs.getLength(); i++)
        {
            att = (Attr) attrs.item(i);
            attName = att.getName();
            /* attVal = att.getValue();
            if (attName.startsWith("xmlns:")) //assume there is only one
            {
                String pref = attName.substring(6);
                String uri = attVal;
            } */

            {
                valid = false;
                boolean isTrue = true;
                if (isTrue) throw new MapperException("Unrecognised attribute name in root element: " + attName);
            }
        }
        else throw new MapperException("Null attributes in root node.");
    }

    /**
    *  Vector of names of parameters of an XSLT template   */
    public static Vector<String> getParameters(Element template)
    {
        Vector<String> res = new Vector<String>();
        if (template != null)
        {
            Vector<Element> paramEls = XMLUtil.namedChildElements(template,"param");
            for (int i = 0; i < paramEls.size(); i++)
            {
                Element paramEl = paramEls.elementAt(i);
                res.addElement(paramEl.getAttribute("name"));
            }
        }
        return res;
    }

}
