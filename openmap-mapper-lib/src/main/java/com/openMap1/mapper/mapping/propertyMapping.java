package com.openMap1.mapper.mapping;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.ConversionImplementation;
import com.openMap1.mapper.ConversionSense;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.JavaConversionImplementation;
import com.openMap1.mapper.LocalPropertyConversion;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.PropMapping;
import com.openMap1.mapper.ValuePair;


import java.util.*;
/**
 * A  property mapping - describes how a property of object in some class is represented in XML
 *
 * @author Robert Worden
 * @version 1.0
 */
public class propertyMapping extends MappingTwo
{
	public PropMapping pMap() {return (PropMapping)map();}
	
	private FixedPropertyValue fixedPropertyValue = null; // only reset for fixed property mappings
	public FixedPropertyValue fixedPropertyValue() {return fixedPropertyValue;}
	
    private String propertyName; // property represented
    private Xpth objectToProperty; // path from object-rep node to property-rep node
    private String type; // type of property (as in XML Schema?)
    private boolean fixed; // true if this is a property with a fixed value
    private String value; // the fixed value
    private boolean hasDefault = false; // true if there is  a default value for the property
    private String defaultValue; // the default value
    /* whether or not the property is optional in the object model. This is not defined in MDL,
    but in the class model; and is only set when validating the MDL, in check 3. */
    private boolean optional;
    /* property optionality as defined in the MDL.
    This is either undefined  (value '') or 'no' - i.e the MDL can only
    make the property more constrained, than either the
    object model or the XML structure would imply.
    It cannot make it less constrained. */
    private String optionalFromMDL = "";

    /**
     * @return a non-null property conversion, if this property has a local conversion with a Java implementation
     */
    public propertyConversion localInConversion() {return localInConversion;}
    private propertyConversion localInConversion = null;

    /**
     * @return a non-null property conversion, if this property has a local conversion with a Java implementation
     */
    public propertyConversion localOutConversion() {return localOutConversion;}
    private propertyConversion localOutConversion = null;
    
    /*
     * @return true if this property has a conversion defined as a lookup table of values
     */
    public boolean hasLookupTable() {return hasLookupTable;}
    private boolean hasLookupTable = false;
    
    // key = XML value; value = model value
    private Hashtable<String,String> inLookupTable = new Hashtable<String,String>();
    // key = model value; value = XML value
    private Hashtable<String,String> outLookupTable = new Hashtable<String,String>();
    
    /** the name of the property */
    public String propertyName() {return propertyName;}

    /** path from object-rep node to property-rep node */
    public Xpth objectToProperty() {return objectToProperty;}

    /** type of property (as in XML Schema?) */
    public String type() {return type;}

    /** true if this is a property with a fixed value */
    public boolean fixed() {return fixed;}

    /** the fixed value, if the property has one */
    public String value() {return value;}

    /** whether or not the property is optional in the object model. This is not defined in MDL,
    but in the class model; and is only set when validating the MDL, in check 3. */
    public boolean optional() {return optional;}

    /** property optionality as defined in the MDL.
    This is either undefined  (value '') or 'no' - i.e the MDL can only
    make the property more constrained, than either the
    object model or the XML structure would imply.
    It cannot make it less constrained. */
    public String optionalFromMDL() {return optionalFromMDL;}

    /** true if the property has a default value (in the object model instance, if not defined in the XML) */
    public boolean hasDefault() {return hasDefault;}

    /** the default value of the property in the object model, if it has one */
    public String defaultValue() {return defaultValue;}

    /** set the path from object-rep node to property-rep node */
    public void setObjectToProperty(Xpth p) {objectToProperty = p;}

    /** make the property optional  or not in the object model */
    public void setOptional(boolean b) {optional = b;}

    
    /**
     * constructor for non-fixed property mappings
     *
     * @param md messageChannel: for writing messages
     */

    public propertyMapping(PropMapping pMap, messageChannel md) throws MapperException
    {
        super(pMap, md);
        mappingType = MappingTwo.PROPERTY;
        propertyName = pMap().getMappedProperty();
        type = pMap().getPropertyType();
        fixed = false;
        optional = true; // default until known otherwise, when validating
        defaultValue = pMap().getDefaultValue();
        hasDefault = ((defaultValue != null) && !(defaultValue.equals("")));
        objectToProperty = pMap().getObjectToPropertyXPath(); 
        
        setLocalConversions();
    }

    /**
     * constructor for fixed values
     *
     * @param md messageChanel: the set of in-memory mappings
     * @param xp Xpth: path to the node mapped
     * @param cc classSet: class name and subset
     * @param pn String: the property name
     * @param t String: property type
     * @param v String: the fixed value
     */
    public propertyMapping(FixedPropertyValue fixedPropertyValue, messageChannel md)  throws MapperException
    {
        super((ObjMapping)fixedPropertyValue.eContainer(),md);
        mappingType = MappingTwo.PROPERTY;
        this.fixedPropertyValue = fixedPropertyValue;
        propertyName = fixedPropertyValue.getMappedProperty();
        type = fixedPropertyValue.getValueType();
        fixed = true;
        value = fixedPropertyValue.getFixedValue();
        optional = false;
        // 'self()' path from object node to property node
        objectToProperty = new Xpth(ModelUtil.getGlobalNamespaceSet(fixedPropertyValue),".");
    }

    /** the full name is 'class(subset):property'  */
    public String fullName()
      {return (className() + "(" + subset() + "):" + propertyName);}

    /** write a text message describing the property mapping */
    public void write()
    {
        mChan().message("");
        mChan().message("Property mapping of " + fullName() + " to node " + nodePath().stringForm());
        writeConditions();
    }
    /** XPaths to nodes required to evaluate link conditions for this representation */
    public Vector<Xpth> linkConditionPaths()
    {
        int i;
        Vector<Xpth> res = new Vector<Xpth>();
        for (i = 0; i < linkConditions().size(); i++)
        {
            res.addElement(linkConditions().elementAt(i).rootToLeftValue());
            res.addElement(linkConditions().elementAt(i).rootToRightValue());
        }
        return res;
    }
    
    /** brief string description */
    public String description()
    {
          return("mapping for property '" + className() + "." + propertyName);
    }
    
    //--------------------------------------------------------------------------------------------------
    //               Initialising local property conversions
    //--------------------------------------------------------------------------------------------------
    
    private void setLocalConversions()
    {
    	boolean inFound = false;
    	boolean outFound = false;
    	LocalPropertyConversion localPropertyConversion = pMap().getLocalPropertyConversion();
    	if (localPropertyConversion != null)
    	{
    		
    		// find the one Java implementation of the in-conversion, if it exists
    		for (Iterator<ConversionImplementation> ic = localPropertyConversion.getInConversionImplementations().iterator(); ic.hasNext();)
    		{
    			ConversionImplementation ci = ic.next();
    			// take the first Java in conversion as defining the conversion; there should not be more than one
    			if ((ci instanceof JavaConversionImplementation) && !inFound)
    			{
    				inFound = true;
    				localInConversion = new propertyConversion(this,(JavaConversionImplementation)ci,ConversionSense.IN);
    			}
    		}

    		// find the one Java implementation of the out-conversion, if it exists
    		for (Iterator<ConversionImplementation> ic = localPropertyConversion.getOutConversionImplementations().iterator(); ic.hasNext();)
    		{
    			ConversionImplementation ci = ic.next();
    			// take the first Java in conversion as defining the conversion; there should not be more than one
    			if ((ci instanceof JavaConversionImplementation) && !outFound)
    			{
    				outFound = true;
    				localOutConversion = new propertyConversion(this,(JavaConversionImplementation)ci,ConversionSense.OUT);
    			}
    		}
    		
    		// lookup table values are ignored if there are any local Java conversion implementations
    		if (!inFound && !outFound && (localPropertyConversion.getValuePairs().size()> 0))
    		{
    			hasLookupTable = true;
    			
    			for (Iterator<ValuePair> ip = localPropertyConversion.getValuePairs().iterator();ip.hasNext();)
    			{
    				ValuePair vp = ip.next();
    				String inValue = vp.getModelValue();
    				String outValue = vp.getStructureValue();
    				String prevInValue = inLookupTable.get(outValue);
    				String prevOutValue = outLookupTable.get(inValue);

    				/* if there is more than one in value given for any out value,
    				 * select one of the preferred in values (there should be only one) */
    				if ((prevInValue == null)|(vp.isPreferredIn())) inLookupTable.put(outValue, inValue);

    				/* if there is more than one out value given for any in value,
    				 * select one of the preferred out values (there should be only one) */
    				if ((prevOutValue == null)|(vp.isPreferredOut())) outLookupTable.put(inValue,outValue);
    			}
    		}
    	}
    }
    
    /**
     * look up a value from the XML structure in the lookup table
     * to find the model value.
     * If there is no entry in the table, pass the structure value through
     * @param structureValue
     * @return value converted to the model value by the lookup table
     */
    public String getModelValue(String structureValue)
    {
    	String tableValue = inLookupTable.get(structureValue);
    	if (tableValue == null) tableValue = structureValue;
    	return tableValue;
    }
    
    /**
     * look up a value from the model in the lookup table
     * to find the XML structure value
     * If there is no entry in the table, pass the model value through
     * @param modelValue
     * @return value converted to the structure value by the lookup table
     */
    public String getStructureValue(String modelValue)
    {
    	String tableValue = outLookupTable.get(modelValue);
    	if (tableValue == null) tableValue = modelValue;
    	return tableValue;
    }


}
