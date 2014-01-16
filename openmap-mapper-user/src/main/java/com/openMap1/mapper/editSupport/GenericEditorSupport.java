package com.openMap1.mapper.editSupport;


import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class GenericEditorSupport {
	
	public static String PASS = "pass";
	
	
	// the root element of the file locations file
	protected Element locationFileRoot;
	
	// string to be added to file locations to get an absolute file location
	protected String locationBase;
	
	
	//----------------------------------------------------------------------------------------------------------
	//                                  constructor has two arguments
	//----------------------------------------------------------------------------------------------------------
	
	public GenericEditorSupport(Element locationFileRoot,String locationBase)
	{
		this.locationFileRoot = locationFileRoot;
		this.locationBase = locationBase;
	}
	
	
	
	//----------------------------------------------------------------------------------------------------------
	//                                     initialise and finalise methods
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * a trivial test of password failure - otherwise do nothing with the instance root to be edited
	 * @param instanceRoot
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public Element initialise(Element instanceRoot, String userName, String password) throws Exception
	{
		if (password.length() > 6) return null;
		return instanceRoot;
	}
	
	
	/**
	 * skeleton finalise method - do nothing with the edited instance
	 * @param instanceRoot
	 * @return
	 */
	public Boolean finalise(Element instanceRoot)
	{
		return new Boolean(true);
	}
	
	//----------------------------------------------------------------------------------------------------------
	//                                        lookups of derived values
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * look up the state of an address from the town
	 * @param refNode
	 * @param extra
	 * @return
	 */
	public  String lookupState(Node stateNode, String extra)
	{
		// navigate up from the 'state' node to the parent 'address'
		Element parent = (Element) stateNode.getParentNode();
		
		// navigate down to the 'city' node 
		Element city = EditUtil.getNamedChild(parent,"city");
		String cityName = null;
		if (city != null) cityName = city.getTextContent();
		
		String state = "empty"; // should always be superseded
		if (cityName == null) {state = "FL";} // node found with no text content
		else if (cityName != null) // node found with text content
		{
			if (cityName.equals("Ann Arbor")) state = "MI"; // special text content
			else state = "TX"; // any other text content
		}

		return state;
	}

	
	//----------------------------------------------------------------------------------------------------------
	//                                        validation
	//----------------------------------------------------------------------------------------------------------
	
	// empty template for all validation methods
	public  String template(Node node, String name)
	{
		String result = PASS;
		String value = EditUtil.getValue(node);
		if (value.length() == 0)
		{
			result = "should not be empty.";
			if (!(name.equals("null"))) result =  name + " " + result;
		}
		else if (value.length() > 0)
		{
			
		}
		return result;
	}
	
	/**
	 * check a field is not empty
	 * @param node
	 * @param name
	 * @return
	 */
	public  String isNotEmpty(Node node, String name)
	{
		String result = PASS;
		String value = EditUtil.getValue(node);
		if (value.length() == 0)
		{
			result = "should not be empty";
			if (!(name.equals("null"))) result =  name + " " + result;
		}
		return result;
	}
	
	/**
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public  String isInteger(Node node, String name)
	{
		String result = PASS;
		String value = EditUtil.getValue(node);
		if (value.length() == 0)
		{
			result = "should not be empty";
			if (!(name.equals("null"))) result =  name + " " + result;
		}
		else if (value.length() > 0)
		{
			try {new Integer(value);}
			catch (Exception ex) {result = "should be an integer number";}
			if (!(name.equals("null"))) result = "'"  + name + "'" + result;
		}
		return result;
	}
	
	
	/**
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public  String isIntegerOrEmpty(Node node, String name)
	{
		String result = PASS;
		String value = EditUtil.getValue(node);
		if (value.length() > 0)
		{
			try {new Integer(value);}
			catch (Exception ex) {result = "should be an integer number";}
			if (!(name.equals("null"))) result = "'"  + name + "'" + result;
		}
		return result;
	}

	/**
	 * check that this is a word made up of only upper case and lower case letters
	 * @param node
	 * @param name
	 * @return
	 */
	public  String isWord(Node node, String name)
	{
		String result = PASS;
		String value = EditUtil.getValue(node);
		if (value.length() == 0)
		{
			result = "should not be empty";
			if (!(name.equals("null"))) result =  name + " " + result;
		}
		else if (value.length() > 0)
		{
			char[] cc  = new char[1];
			char c = ' ';
			boolean invalidChar = false;
			for (int i = 0; i < value.length();i++) if (!invalidChar)
			{
				c = value.charAt(i);
				invalidChar = true;
				if ((c > 64) && (c < 91)) invalidChar = false; // upper case letter
				if ((c > 96) && (c < 123)) invalidChar = false; // lower case letter
				cc[0] = c;
			}
			if (invalidChar) 
			{
				result = "should not contain character '" + new String(cc) + "'";
				if (!(name.equals("null"))) result =  name + " " + result;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public  String isDate(Node node, String name)
	{
		String result = PASS;
		String value = EditUtil.getValue(node);
		if (value.length() == 0)
		{
			result = "should not be empty.";
		}
		if (value.length() < 4)
		{
			result = "should start with a 4-digit year";
		}
		else 
		{
			String yearString = value.substring(0, 4);
			try 
			{	
				int year  = new Integer(yearString).intValue();
				if ((year < 1900)||(year > 2100))
					result = "year should be between 1900 and 2100";
			}
			catch (Exception ex) {result = "first 4 digits should be a whole number year";}
		}
		return result;
	}


	//----------------------------------------------------------------------------------------------------------
	//                                 dynamic menus
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param genderNode
	 * @param spare
	 * @return a menu for assigning gender
	 */
	public  String[][] genderMenu(Node genderNode, String spare)
	{
		// this is not dynamic, but is just to test the API
		String[][] menu  = {{"Male","1"},{"Female","2"}};
		return menu;
	}
	
	protected void message(String s) {System.out.println(s);}


}
