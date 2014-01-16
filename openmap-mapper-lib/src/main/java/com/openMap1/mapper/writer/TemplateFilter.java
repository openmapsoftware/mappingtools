package com.openMap1.mapper.writer;

public interface TemplateFilter {
	
	/**
	 * true if an XSLT template identified by this class name is to be included
	 * in the XSLT transform for a translation
	 * @param className
	 * @return
	 */
	public boolean includeTemplate(String className);

    /**
	 * @param xslFileName
	 * @return the file path to an XSL file which is in the same folder as the top mapping set
	 */
	public String getXSLLocation(String xslFileName, boolean isInWrapper);

}
