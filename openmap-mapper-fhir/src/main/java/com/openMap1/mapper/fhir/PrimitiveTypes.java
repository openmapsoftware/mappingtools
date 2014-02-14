package com.openMap1.mapper.fhir;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.Factory;
import org.hl7.fhir.instance.model.Type;
import org.hl7.fhir.instance.model.Instant;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;

/**
 * Class to define the relationships between:
 * 
 * (a) FHIR Primitive types
 * (b) values assigned to them in the Java reference implementation
 * (c) EMF Ecore EAttribute types
 * (b) String values in the XML
 * 
 * @author Robert
 *
 */

public class PrimitiveTypes {
	
	private static boolean tracing = false;
	
	
	// primitive types for leaves of data type and resource trees, and a type for their superclass	
	public static String[] PRIMITIVETYPES = 
		{"decimal","integer","boolean",
			"instant","element","date",
			"base64Binary","string","uri","dateTime",
			"id","code","oid","uuid","Type"};

	/**
	 * equivalences between  primitive types, values types in the Java reference implementation,
	 * and EMF Ecore attribute types
	 */
	private static String[][] TYPE_EQUIVALENCE =
		{{"decimal","java.math.BigDecimal","EBigDecimal"},
		 {"integer","java.lang.Integer","EInt"},
		 {"boolean","java.lang.Boolean","EBoolean"},
		 {"instant","java.util.Calendar","EString"},
		 {"element","java.lang.String","EString"},
		 {"date","java.lang.String","EString"},
		 {"base64Binary","byte[]","EString"},
		 {"string","java.lang.String","EString"},
		 {"uri","java.net.URI","EString"},
		 {"dateTime","java.lang.String","EString"},
		 {"id","java.lang.String","EString"},
		 {"code","org.hl7.fhir.instance.model.Enumeration","EString"},
		 {"oid","java.lang.String","EString"},
		 {"uuid","java.lang.String","EString"},
		 {"Type","java.lang.String","EString"}
        };
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isPrimitiveType(String type) {return GenUtil.inArray(type, PRIMITIVETYPES);}
	
	/**
	 * 
	 * @param type
	 * @throws MapperException
	 */
	public static void checkPrimitiveType(String type) throws MapperException
	{
		if (type == null) throw new MapperException("Null type");
		if (!GenUtil.inArray(type, PRIMITIVETYPES)) 
			throw new MapperException("Type '" + type + "' is not a FHIR primitive type");
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws MapperException
	 */
	public static String referenceClassResultType(String type)  throws MapperException
	{
		checkPrimitiveType(type);
		String refClass = "";
		for (int i = 0; i < TYPE_EQUIVALENCE.length; i++)
		{
			String[] equiv = TYPE_EQUIVALENCE[i];
			if (type.equals(equiv[0])) refClass = equiv[1];
		}
		return refClass;		
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws MapperException
	 */
	public static String referenceClassName(String type) throws MapperException
	{
		checkPrimitiveType(type);
		String className = GenUtil.initialUpperCase(type);
		if (className.equals("String")) className = "String_";
		return className;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws MapperException
	 */
	public static String EMFTypeName(String type)  throws MapperException
	{
		checkPrimitiveType(type);
		String elfType = "";
		for (int i = 0; i < TYPE_EQUIVALENCE.length; i++)
		{
			String[] equiv = TYPE_EQUIVALENCE[i];
			if (type.equals(equiv[0])) elfType = equiv[2];
		}
		return elfType;		
	}
	
	public static EDataType attributeType(String type) throws MapperException
	{
		checkPrimitiveType(type);
		EDataType dType = null;
		String emfType = EMFTypeName(type);
		if (emfType.equals("EString")) dType = EcorePackage.eINSTANCE.getEString();
		else if (emfType.equals("EFloat")) dType = EcorePackage.eINSTANCE.getEFloat();
		else if (emfType.equals("EBigDecimal")) dType = EcorePackage.eINSTANCE.getEBigDecimal();
		else if (emfType.equals("EInt")) dType = EcorePackage.eINSTANCE.getEInt();
		else if (emfType.equals("EBoolean")) dType = EcorePackage.eINSTANCE.getEBoolean();
		else throw new MapperException("Ecore data type '" + emfType + "' not yet supported");
		return dType;
		
	}
	

	
	public static Type typeValue(Object value, String primitiveType) throws MapperException
	{
		Type result = null;
		checkPrimitiveType(primitiveType);
		
		try
		{
			if (primitiveType.equals("boolean")) result = Factory.newBoolean(((Boolean)value).booleanValue()) ;
			else if (primitiveType.equals("code")) result = Factory.newCode((String)value) ;
			else if (primitiveType.equals("date")) result = Factory.newDate((String)value) ;
			else if (primitiveType.equals("dateTime")) result = Factory.newDateTime((String)value) ;
			else if (primitiveType.equals("id")) result = Factory.newId((String)value) ;
			else if (primitiveType.equals("decimal")) result = Factory.newInteger((Integer)value) ;
			else if (primitiveType.equals("uri")) result = Factory.newUri((String)value) ;
			else if (primitiveType.equals("string")) result = Factory.newString_(value.toString()) ;			
			else if (primitiveType.equals("integer")) result = Factory.newInteger((Integer)value) ;			
			else if (primitiveType.equals("instant")) 
			{
				if (value instanceof String) try
				{
					result = new Instant();
					Calendar cal = xmlToDate((String)value);
					((Instant)result).setValue(new DateAndTime(cal));					
					//((Instant)result).setValue(cal);					
				}
				catch (Exception ex) 
				{
					ex.printStackTrace();
					throw new MapperException("Error in Primitive data type 'instant': " + ex.getMessage());
				}
				else throw new MapperException("Value for instant is not a string, but is " + value.getClass().getName());
			}			
		}
		catch (Exception ex) 
		{ 
			ex.printStackTrace();
			throw new MapperException("Exception applying reference model class for " + primitiveType + "; " + ex.getMessage());
		}
		
		// need to use Class.forName() here...
		if (result == null) throw new MapperException("Primitive type " + primitiveType  + " not yet handled by Factory");
		
		return result;
		
	}
	
	// code lifted from the FHIR java reference implementation, class XMLBase 
	static String dateToXml(java.util.Calendar date) {
		    return javax.xml.bind.DatatypeConverter.printDateTime(date);
		  }
		  
	// code lifted from the FHIR java reference implementation, class XMLBase 
	static java.util.Calendar xmlToDate(String date) throws ParseException {
		    return javax.xml.bind.DatatypeConverter.parseDateTime(date);
		}

	
	/**
	 * 
	 * @param result
	 * @param feat
	 * @param valObj
	 */
	public static void setEcoreFeature(EObject result, EAttribute feat, Object valObj) throws MapperException
	{
		String primitiveType = ModelUtil.getMIFAnnotation(feat, "PrimitiveType");
		if (primitiveType == null) throw new MapperException("Null primitive type of feature " + feat.getName());
		checkPrimitiveType(primitiveType);
		
		/* for many of the primitive types, the java class delivered by the reference model class
		 * is the same class as consumed by the eSet.  Exceptions are handled below */
		
		if (primitiveType.equals("base64Binary"))
		{
			// convert byte[] to String
			throw new MapperException("Type " + primitiveType + " not handled yet");
		}
		else if (primitiveType.equals("instant"))
		{
			// convert Calendar to String
			if (valObj instanceof Calendar)
			{
				String instant = javax.xml.bind.DatatypeConverter.printDateTime((Calendar)valObj);
				result.eSet(feat, instant);
			}
			else throw new MapperException("Value of primitive type 'instant' is not java.util.Calendar");
		}
		else if (primitiveType.equals("uri"))
		{
			// convert URI to string if needed
			if (valObj instanceof java.net.URI)
			{
				URI uriVal = (URI)valObj;
				result.eSet(feat, uriVal.toString());
			}
			else if (valObj instanceof java.lang.String)
			{
				result.eSet(feat, valObj);
			}
			else throw new MapperException("uri value is not a URI or String, but is a " + valObj.getClass().getName());
		}
		
		// other cases include primitive type 'code' where a string value has been delivered
		else try 
		{
			trace("Primitive type " + primitiveType + " has a value of class " + valObj.getClass().getName());
			result.eSet(feat, valObj);
		}
		catch (Exception ex) 
			{throw new MapperException("Failed to set reference model data type " 
					+ primitiveType + " to value " + valObj + "; " + ex.getMessage());}
	}
	
	/**
	 * 
	 * @param theClass
	 * @return
	 */
	public static boolean isPrimitiveDataTypeClass(Class<?> theClass)
	{
		boolean isPrimitive = false;
		String className = theClass.getName();
		if (className.startsWith("org.hl7.fhir.instance.model."))
		{
			String bareClassName = className.substring("org.hl7.fhir.instance.model.".length());
			isPrimitive = GenUtil.inArray(GenUtil.initialLowerCase(bareClassName), PRIMITIVETYPES);
			if (bareClassName.equals("String_")) isPrimitive = true;
		}
		return isPrimitive;
	}


	
	
	private static void message(String s) {System.out.println(s);}
	
	private static void trace(String s) {if (tracing) message(s);}


}
