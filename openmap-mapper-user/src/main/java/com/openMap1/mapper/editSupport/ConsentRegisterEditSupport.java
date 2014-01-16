package com.openMap1.mapper.editSupport;

import org.w3c.dom.Element;

public class ConsentRegisterEditSupport extends GenericEditorSupport{

	public ConsentRegisterEditSupport(Element locationFileRoot,String locationBase)
	{
		super(locationFileRoot,locationBase);
	}
	
	//----------------------------------------------------------------------------------------------------------
	//                                     initialise and finalise methods
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * a trivial test of password failure - then put the user name in the email slot
	 * @param instanceRoot
	 * @param userName
	 * @param password
	 * @return
	 */
	public Element initialise(Element instanceRoot, String userName, String password)
	{
		if (password.length() > 6) return null;
		
		// set the patient's email address to the user name he used to log in
		Element patient = EditUtil.getNamedChild(instanceRoot,"patient");
		Element email = EditUtil.getNamedChild(patient,"email");
		email.setAttribute("address", userName);

		return instanceRoot;
	}
	

}
