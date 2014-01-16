package com.openMap1.mapper.editSupport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EditUtil {

	//----------------------------------------------------------------------------------------------------------
	//                                   support for all methods -  XML utilities
	//----------------------------------------------------------------------------------------------------------
	
	
	/**
	 * return the root Element of an XML file with given location
	 * @param location
	 * @return Element the root element, or null if there is any problem
	 */
	public static Element getRootElement(String location) throws Exception
	{
		Element root = null;
        try {
            FileInputStream fi = new FileInputStream(location);
            DocumentBuilderFactory builderFac = DocumentBuilderFactory.newInstance();
            builderFac.setNamespaceAware(true);
            root = builderFac.newDocumentBuilder().parse(fi).getDocumentElement();
        }
        catch (SAXException ex) {notify(location,ex);}
        catch (FileNotFoundException ex) {notify(location,ex);}
        catch (IOException ex) {notify(location,ex);}
        catch (ParserConfigurationException ex) {notify(location,ex);}
		return root;
	}
	
	private static void notify(String location, Exception ex) throws Exception
	{
		throw new Exception ("Exception getting XML root element from "
				+ location + "; " + ex.getMessage());
	}


	public static  Element getNamedChild(Element parent, String name)
	{
		Element child = null;
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node nd = nl.item(i);
			if ((nd instanceof Element) && (nd.getLocalName().equals(name))) child = (Element)nd;
		}
		return child;
	}
	
	/**
	 * 
	 * @param node
	 * @return the value of any XML node, or the empty string "" if the node is null
	 */
	public static  String getValue(Node node)
	{
		String val = "";
		if (node != null)
		{
			if (node instanceof Element) {val = node.getTextContent();}
			else if (node instanceof Attr) {val = ((Attr)node).getValue();}
			else {val = node.getNodeValue();}			
		}
		return val;
	}
	

	//----------------------------------------------------------------------------------------------------------
	//                                   support for all methods - reading csv files
	//----------------------------------------------------------------------------------------------------------
	

	/**
	 * 
	 * @param relativeLocation
	 * @return the parsed rows of a csv file
	 */
	public static Vector<String[]> readCSVRows(String absoluteLocation)
	{
		// read the lines of the csv file
		Vector<String> lines = textLines(absoluteLocation);
		
		// find the number of columns, from the header row
		StringTokenizer header = new StringTokenizer(lines.get(0),",");
		int columns = header.countTokens();

		// parse each row
		Vector<String[]> rows = new Vector<String[]>();
		for (int i = 0; i < lines.size(); i++) rows.add(parseCSVLine(columns,lines.get(i)));
		return rows;
	}
	
	/**
	 * 
	 * @param location an absolute file location
	 * @return the lines of a text file at the location
	 * @throws MapperException
	 */
	public static Vector<String> textLines(String location)
	{
		Vector<String> lines = new Vector<String>();
		FileInputStream fiz = getTextFile(location);
        InputStreamReader isr = new InputStreamReader(fiz);

        LineNumberReader lnr = new LineNumberReader(isr);
        try {
            String line = lnr.readLine();
            while (line != null)
            {
                lines.add(line);
                line = lnr.readLine();
            }
            lnr.close();        	
        }
		catch (Exception ex) {message("Failure reading read text file at '" + location + "': " + ex.getMessage());}

        return lines;
	}
	
	/**
	 * read a text file at an absolute file location
	 */
	public static FileInputStream getTextFile(String location)
	{
		FileInputStream fi = null;
		try {
			fi = new FileInputStream(location);
		}
		catch (Exception ex) {message("Cannot read text file at '" + location + "': " + ex.getMessage());}
		return fi;
	}
	
	
	/**
	 * read a line of a csv file, expected to have not more than columns separated fields,
	 * and return a string array of the fields, including "" for and initial ',' or two successive ',',
	 * or for final fields not supplied.
	 * FIXME: does not deal with commas within the csv fields.
	 * @param columns max number of columns allowed
	 * @param line
	 * @return String array of field values
	 * @throws MapperException
	 */
	public static String[] parseCSVLine(int columns, String line) 
	{
		String[] field = new String[columns];
		StringTokenizer st = new StringTokenizer(line,",",true);
		int col = 0;
		boolean emptyField = true;
		while (st.hasMoreTokens())
		{
			if (col > columns -1) message("Too many columns in csv line '" + line + "'");
			String val = st.nextToken();
			if (val.equals(","))
			{
				if (emptyField) // initial ',', or two successive ','
				{
					field[col]="";
					col++;					
				}
				emptyField = true;
			}
			else // non-empty field
			{
				field[col] = val;
				col++;
				emptyField = false;
			}
		}
		// trailing fields not even given ','
		if (col < columns)
			for (int c = col; c < columns; c++) field[c] = "";
		return field;
	}

	
	private static void message(String s) {System.out.println(s);}

}
