package com.openMap1.mapper.editSupport;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GiveConsentEditSupport extends GenericEditorSupport{
	
	private Vector<String[]> recipientDetailRows;

	public GiveConsentEditSupport(Element locationFileRoot,String locationBase)
	{
		super(locationFileRoot,locationBase);
	}
	
	//----------------------------------------------------------------------------------------------------------
	//                                     initialise and finalise methods
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * a trivial test of password failure
	 * If it passes, read two csv files 
	 * - one to get the initial green consent file instance
	 * - the other to support a drop-down menu of receiver email addresses, and fill in other receiver details
	 * @param instanceRoot
	 * @param userName
	 * @param password
	 * @return
	 */
	public Element initialise(Element instanceRoot, String userName, String password) throws Exception
	{
		Element consentFileRoot = null;
		if (password.length() > 6) return null;
		
		// read the csv file of patient ids and file locations
		Element patientFileEl = EditUtil.getNamedChild(locationFileRoot,"patientFile");
		String patientFileLocation = patientFileEl.getAttribute("location");
		Vector<String[]> patientFileRows = EditUtil.readCSVRows(locationBase + patientFileLocation);
		
		// get the green consent directive for this patient
		boolean patientFound = false;
		for (int i = 1; i < patientFileRows.size(); i++)
		{
			String[] row = patientFileRows.get(i);
			if (row[0].equals(userName))
			{
				patientFound = true;
				String consentFileLocation = locationBase + row[1];
				consentFileRoot = EditUtil.getRootElement(consentFileLocation);				
			}
		}
		if (!patientFound) throw new Exception("Found no consent directive for '" + userName + "'");
		
		// read the csv file of recipient details
		Element recipientFileEl = EditUtil.getNamedChild(locationFileRoot,"recipientFile");
		String recipientFileLocation = recipientFileEl.getAttribute("location");
		recipientDetailRows = EditUtil.readCSVRows(locationBase + recipientFileLocation);
		
		return consentFileRoot;
	}

	//----------------------------------------------------------------------------------------------------------
	//                                 dynamic menus
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param recipientNode
	 * @param spare
	 * @return a menu for setting the email address of a recipient
	 */
	public  String[][] recipientMenu(Node recipientNode, String spare)
	{
		// make menu items for all rows of the csv file except the header row
		String[][] menu = new String[recipientDetailRows.size()-1][2];
		for (int i = 1; i < recipientDetailRows.size(); i++)
		{
			// first element of a recipient row is an email address
			menu[i-1][0] = recipientDetailRows.get(i)[0];
			// stored value is the same as visible value
			menu[i-1][1] = menu[i-1][0];
		}
		return menu;
	}

	
	//----------------------------------------------------------------------------------------------------------
	//                                        lookups of derived values
	//----------------------------------------------------------------------------------------------------------
	
	
	/**
	 * lookup a name part from an email address in a csv file
	 * @param givenNode
	 * @param partName
	 * @return
	 */
	public  String lookupNamePart(Node nameNode, String partName)
	{
		String emailAddress = getEmailAddress(nameNode);
		return lookupRecipientDetail(emailAddress,partName);
	}

	/**
	 * starting from a name part node, find the person's email address
	 * @param nameNode
	 * @return
	 */
	private String getEmailAddress(Node nameNode)
	{
		// navigate up from the name part node to the parent 'name'; then to the receiver node
		Element parent = (Element) nameNode.getParentNode();
		Element receiver = (Element) parent.getParentNode();		

		// navigate down to the 'email' node  and find the address attribute
		Element email = EditUtil.getNamedChild(receiver,"email");
		return email.getAttribute("address");
	}
	
	/**
	 * look up the value in some column of the recipient csv file, from the email address
	 * @param value
	 * @param colName
	 * @return
	 */
	private String lookupRecipientDetail(String value,String colName)
	{
		String detail = "";
		// look up the column number in the csv file
		String[] headers = recipientDetailRows.get(0);
		int col = -1;
		for (int c = 0; c < headers.length; c++) if (colName.equals(headers[c])) col = c;
		
		// find the row with matching value, and look up the detail
		if (col > -1) for (int row = 1; row < recipientDetailRows.size(); row++)
			if (recipientDetailRows.get(row)[0].equals(value)) detail = recipientDetailRows.get(row)[col];
		return detail;
	}
	
}
