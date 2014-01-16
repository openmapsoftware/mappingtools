package com.openMap1.mapper.reader;



import org.eclipse.emf.ecore.EPackage;

import com.openMap1.mapper.core.MapperException;

import com.openMap1.mapper.query.RDBReader;
import com.openMap1.mapper.structures.DBStructure;

import com.openMap1.mapper.writer.objectGetter;
import com.openMap1.mapper.writer.XMLObjectGetter;

import com.openMap1.mapper.util.SystemMessageChannel;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.StructureType;

import org.w3c.dom.Element;

/**
 * This class is for use in standalone applications, to 
 * delivers XOReader and objectGetter instances for use by applications.
 * @author robert
 *
 */
public class ReaderFactory {
	
	/**
	 * 
	 * @param mapperFileLocation full file path to the '.mapper' mappings file
	 * @param classModelLocation full file path to the ECore class model
	 * @param instanceLocation full file path to the XML instance
	 * @return the XOReader which reads the instance via the mappings
	 * @throws MapperException if anything goes wrong
	 */
	public static XOReader makeReader(String mapperFileLocation, String classModelLocation, String instanceLocation)
	throws MapperException
	{
		try
		{
			// get the MappedStructure
			MappedStructure mappedStructure = FileUtil.getMappedStructure(mapperFileLocation);
			if (mappedStructure == null) throw new MapperException("No mapped structure at " + mapperFileLocation);

			/* get the root element of the instance to be read (applying an input wrapper transform if necessary), 
			 * or connect to the database */
			Element instanceRoot = null;
			if (mappedStructure.getStructureType() == StructureType.RDBMS)
			{
				// nothing to do here as we do not have a user name and password??
				// typically the connection to the database will be checked later when we have them
			}
			else
			{
				if (instanceLocation != null) instanceRoot = XMLUtil.getRootElement(instanceLocation);
				if (instanceRoot == null) throw new MapperException("No instance at " + instanceLocation);
			}
			

			// this method makeReader will apply an in-wrapper transform to the XML, if appropriate
			XOReader reader = makeReader(mappedStructure, classModelLocation, instanceRoot);
			return reader;

		}		
		catch (Exception ex) {throw new MapperException("Exception when creating XOReader: " + ex.getMessage());}
		
	}
	
	/**
	 * 
	 * @param mappedStructure mapping set
	 * @param classModelLocation full file path to the ECore class model
	 * @param instanceRoot root element of the XML instance (this method will apply an in-wrapper transform if needed); 
	 * @return the XOReader which reads the instance via the mappings
	 * @throws MapperException if anything goes wrong
	 */
	public static XOReader makeReader(MappedStructure mappedStructure, String classModelLocation, Element instanceRoot)
	throws MapperException
	{
		try
		{
			// get the class model
			EPackage classModel = FileUtil.getClassModel(classModelLocation);
			if (classModel == null) throw new MapperException("No class model at " + classModelLocation);
			// message("made class model");
			
			// reader messages (there should be none) go to the system console
			SystemMessageChannel sm = new SystemMessageChannel();
						
			// apply the in-wrapper transform to the XML, if there is one
			Element inWrapped = mappedStructure.getInWrappedXML(instanceRoot);
			XOReader reader = mappedStructure.getXOReader(inWrapped, classModel, sm);
			return reader;			
		}		
		catch (Exception ex) {throw new MapperException("Exception when creating XOReader: " + ex.getMessage());}		
	}

	
	/**
	 * 
	 * @param mapperFileLocation full file path to the '.mapper' mappings file
	 * @param classModelLocation full file path to the ECore class model
	 * @param instanceLocation full file path to the XML instance
	 * @return the objectGetter which reads the instance via the mappings (like XOReader,
	 * but removes duplicates for classes whose instances might be multiply represented)
	 * @throws MapperException if anything goes wrong
	 */
	public static objectGetter makeObjectGetter(String mapperFileLocation, String classModelLocation, String instanceLocation)
	throws MapperException
	{
		objectGetter oGet = null;
		XOReader reader = makeReader(mapperFileLocation, classModelLocation, instanceLocation);
		if (reader instanceof MDLXOReader)
			oGet = new XMLObjectGetter((MDLXOReader)reader);
		else if (reader instanceof objectGetter)
			oGet = (objectGetter)reader;
		else throw new MapperException("Reader does not implement interface objectGetter");
		return oGet;
	}
	
	
	/**
	 * 
	 * @param mappedStructure set of mappings
	 * @param classModelLocation full file path to the ECore class model
	 * @param instanceRoot root element of the XML instance
	 * @return the objectGetter which reads the instance via the mappings (like XOReader,
	 * but removes duplicates for classes whose instances might be multiply represented)
	 * @throws MapperException if anything goes wrong
	 */
	public static objectGetter makeObjectGetter(MappedStructure mappedStructure, String classModelLocation, Element instanceRoot)
	throws MapperException
	{
		objectGetter oGet = null;
		XOReader reader = makeReader(mappedStructure, classModelLocation, instanceRoot);
		if (reader instanceof MDLXOReader)
			oGet = new XMLObjectGetter((MDLXOReader)reader);
		else if (reader instanceof objectGetter)
			oGet = (objectGetter)reader;
		else throw new MapperException("Reader does not implement interface objectGetter");
		return oGet;
	}
	
	public static RDBReader makeRDBReader(XOReader reader, String userName, String password) throws MapperException
	{
		if (!(reader instanceof MDLXOReader))
			throw new MapperException("Reader is not an MDLXOReader");
		MDLXOReader mr = (MDLXOReader) reader;
		
		if (!(mr.ms().getStructureType() == StructureType.RDBMS))
			throw new MapperException("Mappings are not mappings to a database");

		DBStructure dbStructure = (DBStructure)mr.ms().connectToRDB(userName, password);
		RDBReader rdbToXML = new RDBReader(dbStructure,null);
		return rdbToXML;	
	}
	
	static void message(String s) {System.out.println(s);}

}
