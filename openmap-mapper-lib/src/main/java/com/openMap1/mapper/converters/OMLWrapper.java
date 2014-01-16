package com.openMap1.mapper.converters;

import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.MappedStructure;

public class OMLWrapper extends AbstractMapperWrapper implements MapperWrapper {
	
	
	/**
	 * @param ms set of mappings which uses this wrapper transform
	 * @param spare spare argument, set to name of topElementDef
	 */
	public OMLWrapper(MappedStructure ms, Object spare) throws MapperException
	{
		super(ms,spare);
	}

	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	public int transformType() {return AbstractMapperWrapper.XML_TYPE;}
	
	/**
	 * 
	 * @return the file extension of the outer document, with initial '*.'
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
	public Document transformIn(Object incoming) throws MapperException
	{
		if (!(incoming instanceof Element)) throw new MapperException("Document root is not an Element");
		Element mappingRoot = (Element)incoming;

		String mappingRootPath = "/MappingSet";
		inResultDoc = XMLUtil.makeOutDoc();
				
		// vanilla copy unless overridden for specific paths
		Element inRoot = scanDocument(mappingRoot, mappingRootPath, AbstractMapperWrapper.IN_TRANSFORM);
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
		String mappingRootPath = "/MappingSet";
		outResultDoc = XMLUtil.makeOutDoc();

		// vanilla copy unless overridden for specific paths
		Element outRoot = scanDocument(outgoing, mappingRootPath, AbstractMapperWrapper.OUT_TRANSFORM);
		outResultDoc.appendChild(outRoot);
		return outResultDoc;
		
	}
	
	/**
	 * copy for each node in the in transform
	 * super.inTransformNode makes a vanilla copy, 
	 * which this method overrides for specific XPaths
	 */
	protected Element inTransformNode(Element el, String path)  throws MapperException
	{
		String cellPath = "/MappingSet/Mappings/row/";
		String content = XMLUtil.getText(el);
		if (content == null) content="";

		/* parse out package name, class name and subset 
		 * FIXME - what should this do for 'Class' and 'Feature' when class names contain a '.' ?*/
		if (path.equals(cellPath + "Class"))
		{
			Element classEl = inResultDoc.createElement("Class");
			if (content.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(content,".");
				// if there are any '.', the package name is before the first '.'
				if (st.countTokens() > 1)
				{
					String packageName = st.nextToken();
					classEl.appendChild(XMLUtil.textElement(inResultDoc, "package",packageName));
					// strip off the package name and '.'
					content = content.substring(packageName.length() + 1);
				}
				classEl.appendChild(XMLUtil.textElement(inResultDoc, "class", className(content)));
				classEl.appendChild(XMLUtil.textElement(inResultDoc, "subset", subset(content)));
			}
			return classEl;
		}

		// parse out role name, target class and subset for associations
		else if (path.equals(cellPath + "Feature"))
		{
			Element featureEl = inResultDoc.createElement("Feature");
			if (content.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(content,".");
				if (st.countTokens() == 1)
				{
					featureEl.appendChild(XMLUtil.textElement(inResultDoc, "attribute", st.nextToken()));
				}
				else if (st.countTokens() > 1)
				{
					String roleName = st.nextToken();
					featureEl.appendChild(XMLUtil.textElement(inResultDoc, "role", roleName));
					// strip off the role name and '.'
					content = content.substring(roleName.length() + 1);
					featureEl.appendChild(XMLUtil.textElement(inResultDoc, "class", className(content)));
					featureEl.appendChild(XMLUtil.textElement(inResultDoc, "subset", subset(content)));
				}
			}
			return featureEl;
		}
		
		else if (path.equals(cellPath + "Condition"))
		{
			Element conditionEl = inResultDoc.createElement("Condition");
			if (content.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(content,";");
				while (st.hasMoreTokens())
				{
					String cond = st.nextToken();
					Element condEl = inResultDoc.createElement("Cond");
					conditionEl.appendChild(condEl);
					makeCondition(cond,condEl);
				}
			}
			return conditionEl;
		}
		
		else if (path.equals(cellPath + "Key"))
		{
			Element keyEl = inResultDoc.createElement("Key");
			if (content.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(content,";");
				while (st.hasMoreTokens())
				{
					String key = st.nextToken();
					Element kEl = XMLUtil.textElement(inResultDoc, "keyField", key);
					keyEl.appendChild(kEl);
				}
			}
			return keyEl;
		}

		
		else if (path.equals(cellPath + "Filter"))
		{
			Element filterEl = inResultDoc.createElement("Filter");
			if (content.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(content,";");
				while (st.hasMoreTokens())
				{
					String filt = st.nextToken();
					Element filtEl = inResultDoc.createElement("Filt");
					filterEl.appendChild(filtEl);
					makeFilter(filt,filtEl);
				}
			}
			return filterEl;
		}
		
		else if (path.equals(cellPath + "Convert_In"))
		{
			Element convertEl = inResultDoc.createElement("Convert_In");
			if (content.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(content,";");
				while (st.hasMoreTokens())
				{
					String convert = st.nextToken();
					Element convEl = XMLUtil.textElement(inResultDoc, "convert", convert);
					convertEl.appendChild(convEl);
				}
			}
			return convertEl;
		}
		
		else if (path.equals(cellPath + "Convert_Out"))
		{
			Element convertEl = inResultDoc.createElement("Convert_Out");
			if (content.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(content,";");
				while (st.hasMoreTokens())
				{
					String convert = st.nextToken();
					Element convEl = XMLUtil.textElement(inResultDoc, "convert", convert);
					convertEl.appendChild(convEl);
				}
			}
			return convertEl;
		}

		// other paths - vanilla copy
		else return super.inTransformNode(el, path);
	}
	
	/**
	 * @param cond string condition
	 * @param condEl Element, whose child elements are to represent the condition
	 */
	private void makeCondition(String cond,Element condEl) throws MapperException
	{
		// strip off initial blanks
		while (cond.startsWith(" ")) cond = cond.substring(1);
		StringTokenizer st = new StringTokenizer(cond,"()");
		if (st.countTokens() == 3) 
		{
			condEl.setAttribute("type", "cross");
			condEl.appendChild(XMLUtil.textElement(inResultDoc, "LHSPath", st.nextToken()));
			condEl.appendChild(XMLUtil.textElement(inResultDoc, "test", stripBlanks(st.nextToken())));
			condEl.appendChild(XMLUtil.textElement(inResultDoc, "RHSPath", st.nextToken()));
		}
		else if (st.countTokens() == 2) 
		{
			condEl.setAttribute("type", "value");
			condEl.appendChild(XMLUtil.textElement(inResultDoc, "LHSPath", st.nextToken()));
			String remainder = st.nextToken();
			StringTokenizer rm =new StringTokenizer(remainder,"'");
			if (rm.countTokens() != 2) throw new MapperException("Invalid condition '" + cond + "'");
			condEl.appendChild(XMLUtil.textElement(inResultDoc, "test", stripBlanks(rm.nextToken())));
			condEl.appendChild(XMLUtil.textElement(inResultDoc, "RHSValue", rm.nextToken()));
		}
		else throw new MapperException("Invalid condition '" + cond + "' " + st.countTokens());
	}
	
	/**
	 * 
	 * @param filt string filter; can only handle '=' conditions so far
	 * @param filtEl - filter element
	 * FIXME - drops any blanks from within the quoted string
	 */
	private void makeFilter(String filt,Element filtEl) throws MapperException
	{
		StringTokenizer st = new StringTokenizer(filt,"=' ");
		if (st.countTokens() == 2)
		{
			filtEl.appendChild(XMLUtil.textElement(inResultDoc, "attribute", stripBlanks(st.nextToken())));
			filtEl.appendChild(XMLUtil.textElement(inResultDoc, "value", st.nextToken()));
		}
	}
	
	/**
	 * 
	 * @param s a string
	 * @return the first non-blank substring
	 */
	private String stripBlanks(String s)
	{
		StringTokenizer st = new StringTokenizer(s," ");
		return st.nextToken();
	}

	
	/**
	 * 
	 * @param classSet  class name, with or without subset in brackets
	 * @return class name
	 */
	private String className(String classSet)
	{
		StringTokenizer cs = new StringTokenizer(classSet,"()");
		return cs.nextToken();
	}
	
	/**
	 * 
	 * @param classSet  class name, with or without subset in brackets
	 * @return subset ("" if no brackets)
	 */
	private String subset(String classSet)
	{
		String subset = "";
		StringTokenizer cs = new StringTokenizer(classSet,"()");
		if (cs.countTokens() == 2) {cs.nextToken();subset = cs.nextToken();}
		return subset;
	}

}
