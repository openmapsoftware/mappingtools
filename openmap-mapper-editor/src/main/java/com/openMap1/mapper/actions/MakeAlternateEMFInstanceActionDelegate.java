package com.openMap1.mapper.actions;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;

/**
 * Action to make an 'Alternate EMF instance' - like a usual EMF instance, 
 * except that the targets of non-containment references are represented by key 
 * values, not by their position in the document.
 * @author robert
 *
 */

public class MakeAlternateEMFInstanceActionDelegate extends MapperActionDelegate implements IObjectActionDelegate{

	public void run(IAction action)
	{
		tracing = false;
		trace("Making Alternate Ecore Instance ");
		
		IFile file = getSelectedFile();
		if (file != null) try
		{
			// (1) Make an IFile for the result - same folder, add extension and  '_alt' after file name, .xml extension
			IProject project = file.getProject();
			IFolder folder = project.getFolder(file.getProjectRelativePath().removeLastSegments(1));

			String fileName = file.getName();
			String extension = file.getFileExtension();
			String newFileName = fileName.substring(0,fileName.length() - extension.length()-1) + extension + "_alt.xml";
			IFile newFile = folder.getFile(newFileName);
			if (newFile.exists())
			{
				boolean confirm = WorkBenchUtil.askConfirm("File '" + newFileName + "' already exists", 
					"Do you want to replace the existing file?");
				if (!confirm) return;
				else newFile.delete(true, null); // not necessary?
			}
						
			// (2) read and process the instance
			Element rootElement = XMLUtil.getRootElement(file.getContents());
			makeAlternateInstance(rootElement,newFile);
		}
		catch (MapperException ex) {showMessage(ex.getMessage());ex.printStackTrace();}
		catch (CoreException ex) {showMessage(ex.getMessage());ex.printStackTrace();}
	}
	
	private void makeAlternateInstance(Element rootElement,IFile newFile) throws MapperException
	{
		// find out what kind of URL fragment this instance uses for non-containment eReferences
		String startDefaultFragment = "//@";
		boolean usesDefaultURLFragments = findAttributeValueStartingWith(rootElement,startDefaultFragment);
		String startOverrideFragment = "#//";
		boolean usesOverrideURLFragments = findAttributeValueStartingWith(rootElement,startOverrideFragment);
		if (usesDefaultURLFragments && usesOverrideURLFragments)
			throw new MapperException("EMF Instance uses both default and override URL fragments; cannot convert");
		
		// copy across the root element and its attributes
		Document doc = XMLUtil.makeOutDoc();
		Element importedRoot = (Element)doc.importNode(rootElement, false); // false = shallow copy

		// initialise the path strings that will form the URL fragments
		String path = "/";
		String start = startDefaultFragment;
		if (usesOverrideURLFragments) 
		{
			start = startOverrideFragment;
			path = "#/";
		}
		
		// deep copy of the documents, adding the attribute 'alt_id' with the URL fragment to each Element
		Hashtable<String,String> fragmentsFound = new Hashtable<String,String>();
		copyWithIdAttribute(doc,rootElement,importedRoot,path,usesOverrideURLFragments,fragmentsFound,start);

		// warn the user of any URL fragments found as attribute values, but not matched
		noteProblems(fragmentsFound);

		// write out the alternate form EMF instance
		doc.appendChild(importedRoot);
		EclipseFileUtil.writeOutputResource(doc,newFile,true); // true = formatted
	}
	
	/**
	 * @param el an element
	 * @param start the string which might be the start of an attribute value
	 * @return true if start is the start of any attribute value on the 
	 * element or the tree below it.
	 */
	private boolean findAttributeValueStartingWith(Element el, String start)
	{
		boolean found = false;
		for (int at = 0; at < el.getAttributes().getLength();at++)
		{
			Attr att = (Attr)el.getAttributes().item(at);
			String val = el.getAttribute(att.getName());
			if (val.startsWith(start)) found = true;
		}
		
		for (Iterator<Element> ie = XMLUtil.childElements(el).iterator();ie.hasNext();)
			if (findAttributeValueStartingWith(ie.next(),start)) found = true;
		return found;
	}
	
	/**
	 * 
	 * @param doc the Document for the alternate EMF form
	 * @param el an element in the original persisted form
	 * @param importedEl the corresponding element in the alternative persisted
	 * @param path the path to be used in the map_id attributes of all elements
	 * @param usesOverrideURLFragments;
	 * @param fragments found a record of all URL fragments found and their status
	 * @param start initial substring of any URL fragment
	 * Copies the child elements and their attributes across, and adds the alt_id attribute;
	 * and recursively copies all descendants across.
	 */
	private void copyWithIdAttribute(Document doc, Element el,Element importedEl,String path,
			boolean usesOverrideURLFragments, Hashtable<String,String> fragmentsFound,String start)
	throws MapperException
	{
		// note all the attributes of this element whose values are URL fragments
		addAttributeValuesFound(el,fragmentsFound,start);

		/* iterate over child elements; the child position, used in the URL fragments, must 
		 * be reset to zero whenever the element tag name changes. */
		int childPosition = 0;
		String lastTagName = "";
		for (Iterator<Element> it = XMLUtil.childElements(el).iterator();it.hasNext();)
		{
			Element child = it.next();
			Element importedChild = (Element)doc.importNode(child, false);
			
			// reset the child position to zero if the tag name changes
			if (!child.getLocalName().equals(lastTagName)) childPosition = 0;
			lastTagName = child.getLocalName();

			// extend the URL fragment appropriately
			String newPath = path + "/@" + child.getLocalName() + "." +  childPosition;
			if (usesOverrideURLFragments) newPath = path + "/" + child.getAttribute("name");
			childPosition++;

			// note the URL fragment of this child element, and set the attribute on the modified child
			addPathsFound(newPath,fragmentsFound);
			String idAttName = "alt_id";
			importedChild.setAttribute(idAttName, newPath);
			
			// create modified versions of the descendants of the child node
			copyWithIdAttribute(doc,child,importedChild,newPath,usesOverrideURLFragments,fragmentsFound,start);
			
			// attach the modified child to its parent to go in the output document
			importedEl.appendChild(importedChild);
		}
	}
	
	/**
	 * possible status values for each URL fragment
	 */
	private static String TRAIL_ONLY = "Trail only";
	private static String ATTRIBUTE_ONLY = "Attribute only";
	private static String TRAIL_AND_ATTRIBUTE = "Trail and attribute";
	
	/**
	 * 
	 * @param el
	 * @param fragmentsFound
	 * @param usesOverrideURLFragments
	 * record that any attribute values matching the URL fragment pattern have been found
	 * in the Element el
	 */
	private void addAttributeValuesFound(Element el,
			Hashtable<String,String>fragmentsFound,String start)
	{
		for (int at = 0; at < el.getAttributes().getLength();at++)
		{
			Attr att = (Attr)el.getAttributes().item(at);
			String val = el.getAttribute(att.getName());
			if (val.startsWith(start))
			{
				StringTokenizer st = new StringTokenizer(val," ");
				// treat each part of the value separated by ' ' as the value to match
				while (st.hasMoreTokens())
				{
					String valPart = st.nextToken();
					String previousStatus = fragmentsFound.get(valPart);
					String status = ATTRIBUTE_ONLY;
					if (previousStatus != null)
					{					
						if (previousStatus.equals(TRAIL_ONLY))status = TRAIL_AND_ATTRIBUTE;
						if (previousStatus.equals(TRAIL_AND_ATTRIBUTE))status = TRAIL_AND_ATTRIBUTE;
					}
					fragmentsFound.put(valPart, status);
				}				
			}
		}		
	}
	
	/**
	 * 
	 * @param newPath
	 * @param fragmentsFound
	 * record that a path has been found that will be used at the value of an 'alt_id' attribute,
	 * to match a URL fragment
	 */
	private void addPathsFound(String path,Hashtable<String,String> fragmentsFound)
	throws MapperException
	{
		String previousStatus = fragmentsFound.get(path);
		String status = TRAIL_ONLY;
		if (previousStatus != null)
		{
			if (previousStatus.equals(ATTRIBUTE_ONLY)) status = TRAIL_AND_ATTRIBUTE;
			if (previousStatus.equals(TRAIL_AND_ATTRIBUTE)) status = TRAIL_AND_ATTRIBUTE;
			/* if previous status is TRAIL_ONLY or TRAIL_AND_ATTRIBUTE,  DON'T throw an exception;
			 * some trails of 'name' values do occur more than once */
		}
		fragmentsFound.put(path, status);
	}
	
	/**
	 * @param fragmentsFound all URL fragments found in the instance, with their status
	 * If any fragments were found in an EReference attribute, but the node
	 * corresponding to the fragment was not found, write a warning.
	 */
	private void noteProblems(Hashtable<String,String> fragmentsFound)
	{
		Vector<String> unmatchedFragments = new Vector<String>();
		for (Enumeration<String> en = fragmentsFound.keys();en.hasMoreElements();)
		{
			String fragment = en.nextElement();
			String status = fragmentsFound.get(fragment);
			if (status.equals(ATTRIBUTE_ONLY)) unmatchedFragments.add(fragment);
		}
		
		if (unmatchedFragments.size() > 0) 
			showMessage("Warning: " + unmatchedFragments.size() 
					+ " EReference attributes were not matched, such as '" + unmatchedFragments.get(0) + "'");
	}




}
