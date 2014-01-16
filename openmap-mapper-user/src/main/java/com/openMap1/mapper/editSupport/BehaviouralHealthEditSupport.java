package com.openMap1.mapper.editSupport;

import org.w3c.dom.Element;

public class BehaviouralHealthEditSupport extends GenericEditorSupport{

	public BehaviouralHealthEditSupport(Element locationFileRoot,String locationBase)
	{
		super(locationFileRoot,locationBase);
	}
	
	//----------------------------------------------------------------------------------------------------------
	//                                     initialise and finalise methods
	//----------------------------------------------------------------------------------------------------------
	/**
	 * @param instanceRoot
	 * @param userName
	 * @param password
	 * @return
	 */
	public Element initialise(Element instanceRoot, String userName, String password)
	{
		if (instanceRoot == null) message("Null instance root");

		return instanceRoot;
	}
	

}
