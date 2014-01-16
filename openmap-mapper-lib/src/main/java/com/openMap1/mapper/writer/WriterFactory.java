package com.openMap1.mapper.writer;

import org.eclipse.emf.ecore.EPackage;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.SystemMessageChannel;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.MappedStructure;

/**
 * This class is for use in standalone applications.
 * It delivers XMLWriter instance for use in those applications.
 * @author robert
 *
 */
public class WriterFactory {

	/**
	 * 
	 * @param oGet objectGetter which supplies objects, properties and links (eg from input XML) to the writer
	 * @param outputMapperFileLocation full file path to the '.mapper' mappings file for the output XML
	 * @param classModelLocation full file path to the ECore class model (of both input and output)
	 * @param mChan channel (eg text file) for writing a trace of the writing process, if wanted; 
	 * if mChan is null they go to the system console
	 * @param runTracing if true a trace of the XML writing process is sent to mChan
	 * @return the XMLWriter which will make the translation
	 * @throws MapperException if anything goes wrong
	 */
	public static XMLWriter makeWriter(objectGetter oGet, 
			String outputMapperFileLocation, String classModelLocation, 
			 messageChannel mChan, boolean runTracing)
	throws MapperException
	{
		try
		{
			// get the output MappedStructure
			MappedStructure mappedStructure = FileUtil.getMappedStructure(outputMapperFileLocation);
			
			// if no message channel is supplied, run trace messages go to the system console
			if (mChan == null) mChan = new SystemMessageChannel();
			
			// get the class model
			EPackage classModel = FileUtil.getClassModel(classModelLocation);
			
			XMLWriter writer = mappedStructure.getXMLWriter(oGet, classModel, mChan, runTracing);
						
			return writer;			
		}		
		catch (Exception ex) 
		{
			ex.printStackTrace();
			throw new MapperException("Exception when creating XMLWriter: " + ex.getMessage());
		}		
	}


}
