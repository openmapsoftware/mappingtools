package com.openMap1.mapper.views;

import java.io.InputStream;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.openMap1.mapper.presentation.FileSaverWizard;
import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.XMLException;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Helper class for any tabular view whose contents may 
 * need to be saved as XML or a csv file.
 * 
 * @author robert
 *
 */
public class ViewSaver {
	
	private boolean tracing = false;

	
	private String wizardTitle;
	private String pageTitle;

	private SaveableView view;
	
	/**
	 * Any saveable view must create one of these in order to save itself
	 * @param view the view which creates this ViewSaver
	 * @param wizardTitle title of the wizard which asks for a save file name
	 * @param pageTitle title of the page which asks for a save file name (may be more detailed)
	 */
	public ViewSaver(SaveableView view, String wizardTitle, String pageTitle)
	{
		this.wizardTitle = wizardTitle;
		this.pageTitle = pageTitle;
		this.view = view;
	}
	
	/**
	 * constructor for when you only need the DOM of the saved view, and you do not need
	 * to save it in a separate file
	 * @param view
	 */
	public ViewSaver(SaveableView view)
	{
		this.view = view;
	}

	
	/**
	 * called to save the contents of a view - first asking the user for a destination file
	 * The extension must be 'xml' to make an xml file, or 'csv' to make a comma-separated value file
	 */
	public void saveResults()
	{
		FileSaverWizard wizard = new FileSaverWizard(wizardTitle,pageTitle);
		wizard.init(PlatformUI.getWorkbench(), null);
	    WizardDialog dialog = new WizardDialog(WorkBenchUtil.getShell(),wizard);
	    dialog.open();
	    IFile viewSaveFile = wizard.getViewSaveFile();
	    if (viewSaveFile != null) try
		{
	    	String extension = viewSaveFile.getFileExtension();
	    	if (extension == null)  throw new MapperException("No file extension");
	    	// csv (comma separated values, for Excel)
	    	else if (extension.equals("csv"))
	    	{
	    		writeViewContents(viewSaveFile,false);
	    	}
	    	else // any other extension is saved as an XML file
	    	{
				Document doc = XMLUtil.makeOutDoc();

				// prepare the DOM
				Element root = resultDOM(doc);
				doc.appendChild(root);
				
				// write out the XML. true = with formatting
				EclipseFileUtil.writeOutputResource(doc, viewSaveFile, true);	    		
	    	}
		}
		catch (Exception ex) 
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			WorkBenchUtil.showMessage("Failed to write view contents" , ex.getMessage());
		}
	}
	
	/**
	 * @param doc XML Document used for creating Elements
	 * @return Element tree describing the view contents
	 */
	public Element resultDOM(Document doc)
	{
		Vector<Vector<String>> sRes = sortedResults();
		Element root = null;
		try{
			// root of the document
			root = XMLUtil.newElement(doc, "ViewContents");
			root.setAttribute("view",view.getTitle());
			
			// view-specific header
			Element header  = XMLUtil.newElement(doc, "HeaderInformation");
			view.fillHeaderElement(doc, header);
			root.appendChild(header);
			
			// definition of column headers
			Element columns = XMLUtil.newElement(doc, "TableColumns");
			root.appendChild(columns);
			for (Iterator<String> ic = view.columnHeaders().iterator();ic.hasNext();)
				columns.appendChild(XMLUtil.textElement(doc, "column", XMLUtil.legalTagName(ic.next())));
						
			// rows of the view table
			Element content = XMLUtil.newElement(doc, "TableContent");
			root.appendChild(content);
			for (Iterator <Vector<String>> ir = sRes.iterator(); ir.hasNext();)
			{
				Element rowEl = XMLUtil.newElement(doc, "row");
				content.appendChild(rowEl);
				Vector<String> row = ir.next();
				for (int c = 0; c < row.size(); c++)
				{
					String tagName = XMLUtil.legalTagName(view.columnHeaders().get(c));
					rowEl.appendChild(XMLUtil.textElement(doc, tagName, row.get(c)));
				}
			}
		}
		catch (XMLException ex) {System.out.println("Failed to construct view contents XML: " + ex.getMessage());}			
		return root;		
	}
	
	/**
	 * Comma-separated value output of the contents of a view
	 */
	public void writeViewContents(IFile file, boolean createFile) throws MapperException
	{
		String separator = ",";
		Vector<Vector<String>> sRes = sortedResults();
		String headerString = "";

		for (Iterator<String> ic = view.columnHeaders().iterator();ic.hasNext();)
		{
			String header = noSeparators(ic.next(),separator);
			headerString = headerString + header ;
			if (ic.hasNext())headerString = headerString +  separator ;			
		}
		if (createFile)
		{
			InputStream firstLine = EclipseFileUtil.textStream(headerString);
			try {file.create(firstLine, false, null);}
			catch (Exception ex) {throw new MapperException("Failed to create IFile: " + ex.getMessage());}
		}
		else EclipseFileUtil.appendLine(headerString, file);
		trace(headerString);

		for (Iterator <Vector<String>> ir = sRes.iterator();ir.hasNext();)
		{
			Vector<String> row = ir.next();
			String rowString = "";
			for (Iterator<String> ic = row.iterator();ic.hasNext();)
			{
				String field = ic.next();
				String cell = noSeparators(field,separator);
				rowString = rowString + cell;
				if (ic.hasNext()) rowString = rowString + separator ;
			}
			EclipseFileUtil.appendLine(rowString, file);
			trace(rowString);
		}
	}
	
	/**
	 * @param s any string
	 * @param separator the character used to separate cells
	 * @return s with separators replaced by spaces
	 */
	private String noSeparators(String s, String separator)
	{
		String res = "";
		if (s != null)
		{
			StringTokenizer st = new StringTokenizer(s,separator);
			while (st.hasMoreTokens())
			{
				res = res + st.nextToken();
				if (st.hasMoreTokens()) res = res + " ";
			}			
		}
		else trace("null field when saving view");
		return res;
	}
	
	/**
	 * @return a Vector with one element for each row of the view,
	 * in correct sort order. 
	 * The element for each row is a Vector
	 * with a String for the contents of each cell 
	 */
	public Vector<Vector<String>> sortedResults()
	{
		Vector<Vector<String>> sRes = new Vector<Vector<String>>();
		int index = 0;
		boolean moreRows = true;
		while (moreRows)
		{
			Object row = view.tableViewer().getElementAt(index);
			index++;
			if (row != null)
			{
				Vector<String> rowVector = new Vector<String>();
				for (int col = 0; col < view.columnHeaders().size(); col++)
					rowVector.add(view.labelProvider().getColumnText(row, col));
				sRes.add(rowVector);
			}
			else moreRows = false;
		}
		return sRes;		
	}
	
	private void trace(String s)  {if (tracing) System.out.println(s);}

}
