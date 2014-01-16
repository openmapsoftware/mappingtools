package com.openMap1.mapper.writer;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.util.FileUtil;

public class BasicTemplateFilter implements TemplateFilter{
	
	private MappedStructure ms;
	
	public BasicTemplateFilter(MappedStructure ms)
	{
		this.ms = ms;
	}
	
	/**
	 * true if an XSLT template identified by this class name is to be included
	 * in the XSLT transform for a translation
	 * @param className
	 * @return
	 */
	public boolean includeTemplate(String className) {return true;}

    /**
	 * @param xslFileName
	 * @return the file path to an XSL file which is in the same folder as the top mapping set
	 */
	public String getXSLLocation(String xslFileName, boolean isInWrapper)
	{
		return FileUtil.getXSLLocation(xslFileName, ms);
	}

}
