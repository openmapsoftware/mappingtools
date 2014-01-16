package com.openMap1.mapper.mapping;

import java.util.*;
import java.lang.reflect.*;


import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.PropertyConversionException;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.ClassDetails;
import com.openMap1.mapper.ConversionArgument;
import com.openMap1.mapper.ConversionImplementation;
import com.openMap1.mapper.ConversionSense;
import com.openMap1.mapper.JavaConversionImplementation;
import com.openMap1.mapper.PropertyConversion;
import com.openMap1.mapper.XSLTConversionImplementation;


/**
 * class for a two-way conversion of a property value -
 * between the form used in the XML instance, 
 * and the form used in the EMF Ecore model instance.
 * 
 * @author robert
 *
 */
public class propertyConversion {

	
  /* sense = 'in' for conversions from XML to the object model;
  'out' for conversions from the object model to XML. */
  private String sense;
  private Vector<String> arguments; // string names of properties or converted properties used as arguments
  private String resultProperty; // name of property or converted property returned
  private ClassSet cSet; // class and subset this conversion applies to

  // supplied implementations of the conversion, in Java or XSLT; key = language name
  private Hashtable<String, ConversionImplementation> implementations;


  public String resultProperty() {return resultProperty;}
  public Vector<String> arguments() {return arguments;}
  public boolean in() {return(sense.equals("in"));}
  public boolean out() {return(sense.equals("out"));}
  public String sense() {return sense;}
  public ClassSet cSet() {return cSet;}

  // to keep data in this instance between calls to the static Java conversion method
  private Hashtable<?,?> lookupTable = new Hashtable<Object,Object>();
  // to ensure kept data is created only once

    /**
     * Constructor for non-local property conversions, 
     * which may have several arguments
     */
  	public propertyConversion (PropertyConversion propCon)
    {
    	sense = propCon.getSense().getLiteral();
        String className = ((ClassDetails)propCon.eContainer()).getQualifiedClassName();
        try {cSet = new ClassSet(className,propCon.getSubset());}
        catch (MapperException ex) {GenUtil.surprise(ex,"new propertyConversion");} // nulls unexpected
        resultProperty = propCon.getResultSlot();

        arguments = new Vector<String>();
        for (Iterator<ConversionArgument> it = propCon.getConversionArguments().iterator();it.hasNext();)
        	{arguments.add(it.next().getPropertyName());}

        implementations = new Hashtable<String, ConversionImplementation>();
        for (Iterator<ConversionImplementation> it = propCon.getConversionImplementations().iterator();it.hasNext();)
        {
        	ConversionImplementation ci = it.next();
        	String lang = language(ci);
        	implementations.put(lang,ci);
        }

    }
    
    /**
     * Constructor for local property conversions, which have only one implementation with one argument
     * @param pm the propertyMapping that this conversion belongs to
     * @param jci the Java conversion implementation
     * @param sense in or out
     */
  	public propertyConversion(propertyMapping pm, JavaConversionImplementation jci, ConversionSense sense)
    {
    	this.sense = sense.getLiteral();
    	cSet = pm.cSet();
    	resultProperty = pm.propertyName();
    	
    	// the conversion has one argument, with the mapped property name
        arguments = new Vector<String>();
        arguments.add(resultProperty);
        
        // there is one Java implementation
        implementations = new Hashtable<String, ConversionImplementation>();
        implementations.put(language(jci),jci);
    }
    
    // note upper case 'J' for Java
    private String language(ConversionImplementation ci)
    {
    	String lang = null;
    	if (ci instanceof JavaConversionImplementation) lang = "Java";
    	if (ci instanceof XSLTConversionImplementation) lang = "XSLT";
    	return lang;
    }

  public String methodName(String language)
  {
      String meth = null;
      ConversionImplementation ci = implementations.get(language);
  	if (ci instanceof JavaConversionImplementation)
  		{meth = ((JavaConversionImplementation)ci).getMethodName();}
	if (ci instanceof XSLTConversionImplementation)
		{meth = ((XSLTConversionImplementation)ci).getTemplateName();}
      return meth;
  }

  /**
   * For Java, the container name is the fully qualified class name,
   * i.e preceded by the full package name
   * @param language
   * @return
   */
  public String containerName(String language)
  {
      String cont = null;
      ConversionImplementation ci = implementations.get(language);
      if (ci instanceof JavaConversionImplementation) 
      {
    	  JavaConversionImplementation jc = (JavaConversionImplementation)ci;
    	  cont = jc.getPackageName() + "." + jc.getClassName();
      }
  	  if (ci instanceof XSLTConversionImplementation)
  		{cont = "";}
      return cont;
  }

  public boolean hasImplementation(String language)
      {return(implementations.get(language) != null);}

  /**
   * 
   * @return fully qualified method name - preceded by the class, which is preceded 
   * by the package
   */
  public String javaImplementation()
  {
      String res = null;
      JavaConversionImplementation javaImp = (JavaConversionImplementation)implementations.get("Java");
      if (javaImp != null) 
        {res = javaImp.getPackageName()+ "." + javaImp.getClassName() + "." + javaImp.getMethodName();}
      return res;
  }

 
  /**
   * This assumes that class.forName will work with a fully qualified class name
   * @param className
   * @return
   * @throws MapperException
   */  
  private Class<?> getNamedClass(String className) throws MapperException
    {
        Class<?> theClass = null;
        if (className == null)
            {throw new MapperException("Looking for null class");}
        try {theClass = Class.forName(className);}
        catch (ClassNotFoundException ex)
            {throw new PropertyConversionException("Failed to find property conversion class '" + className + "': " + ex.getMessage());}
        return theClass;
    }

    private Method getStaticPublicMethod(Class<?> theClass, String methodName, Class<?>[] argClasses) throws MapperException
    {
        Method theMethod = null;
        if (methodName == null)
            {throw new PropertyConversionException("Null method name sought in class '" + theClass.getName() + "'");}
        try {theMethod = theClass.getDeclaredMethod(methodName,argClasses);}
        catch (NoSuchMethodException ex)
            {throw new PropertyConversionException("Failed to find  method '" + methodName + "' in class '" + theClass.getName() + "': " + ex.getMessage());}
        catch (SecurityException ex)
            {throw new PropertyConversionException("Security exception finding  method '" + methodName + "' in class '" + theClass.getName() + "': " + ex.getMessage());}
        if (!Modifier.isStatic(theMethod.getModifiers()))
            {throw new PropertyConversionException("java conversion method '" + methodName + "' in class '" + theClass.getName()
                           + "' must be static.");}
        if (!Modifier.isPublic(theMethod.getModifiers()))
            {throw new PropertyConversionException("java conversion method '" + methodName + "' in class '" + theClass.getName()
                           + "' must be public.");}
        return theMethod;
    }

    private Object invokeStaticMethod(Method theMethod, Object[] args, String javaClass, String pName) throws MapperException
    {
        Object result = null;
        String methodName = theMethod.getName();
        try
        {
            result = theMethod.invoke(null,args);
        }
        catch (IllegalAccessException ex)
            {throw new PropertyConversionException("Illegal access to java conversion method '" + methodName + "' in class '" + javaClass
                      + "' for property " + pName + " - " + ex.getMessage());}
        catch (IllegalArgumentException ex)
            {throw new PropertyConversionException("Illegal argument for java conversion method '" + methodName + "' in class '" + javaClass
                      + "' for property " + pName + " - " + ex.getMessage());}
        catch (InvocationTargetException ex)
        {
            Throwable ta = ex.getTargetException();
            throw new PropertyConversionException("java conversion method '" + methodName + "' in class '" + javaClass
                      + "' for property " + pName + " has thrown an exception: " + ta.getMessage());
        }
        catch (Exception ex)
            {throw new PropertyConversionException("Exception converting property " + pName + " - " + ex.getMessage());}
        return result;
    }

    /* true if the system can do the Java implementation of this conversion -
    i.e if the necessary class and method are available now.
    If it cannot for any reason, throws exceptions or returns false. */
    public boolean canDoJavaConvert() throws MapperException
    {
        int size = arguments.size();
        Class<?> convClass;
        Class<?>[] argTypes = new Class<?>[size + 1]; // extra for first Hashtable argument
        String javaClass = containerName("Java");
        String javaMethod = methodName("Java");

        // conversion method expects a first Hashtable argument, followed by one or more String arguments
        argTypes[0] = getNamedClass("java.util.Hashtable");
        for (int i = 0; i < size; i++)
            {argTypes[i+1] = getNamedClass("java.lang.String");}

        /* find the class containing the static method, then find the method and the initialisation method
        These will throw MapperExceptions if anything is wrong. */
        convClass = getNamedClass(javaClass);
        getStaticPublicMethod(convClass,javaMethod,argTypes);

        return true; // if you get this far
    }

    /* do the Java implementation of this conversion */
    public String doJavaConvert(String[] args) throws MapperException
    {
        Class<?> convClass;
        Method jMethod = null;
        int size = arguments.size(); // number of String arguments

        // conversion method has an initial Hashtable argument, followed by String arguments
        Object[] fullArgs = new Object[size + 1];
        Class<?>[] argTypes = new Class<?>[size + 1];

        String res = null;
        String javaClass = containerName("Java");
        String javaMethod = methodName("Java");
        String pName = resultProperty; // property or converted property

        // check correct number of arguments
        if (args.length != size)
          {throw new PropertyConversionException("Supplied " + args.length + " arguments to conversion method '"
              + javaMethod + "' in Java class '" + javaClass
              + "'; but the conversion expects " + size + " arguments.");}

        // method expects a Hashtable argument, followed by one or more String arguments
        argTypes[0] = getNamedClass("java.util.Hashtable");
        for (int i = 0; i < size; i++)
                {argTypes[i+1] = getNamedClass("java.lang.String");}

        // find the class containing the static method, then find the method and possibly the initialisation method
        convClass = getNamedClass(javaClass);
        jMethod = getStaticPublicMethod(convClass, javaMethod, argTypes);


        /* lookup table is the first argument of the conversion method. The conversion method may either 
         * ignore it, or may detect it is empty on the first call, and call some initialisation method
         * to set it up (and thereafter, see it is not empty and so not set it up again) */
        fullArgs[0] = lookupTable;
        for (int i = 0; i < size; i++) {fullArgs[i+1] = args[i];}

        // static method, so no object in the class need be specified when invoking it
        res = (String)invokeStaticMethod(jMethod,fullArgs,javaClass, pName);

        return res;
    }




}
