package com.openMap1.mapper.presentation;

import com.openMap1.mapper.core.PropertyValueSupplier;
import com.openMap1.mapper.core.MapperException;

import java.util.Hashtable;

/**
 * An instance of this class is set up by the mapper editor when it is opened.
 * It then provides sets of String values for those properties where we want to override 
 * the normal text property editors in the properties page by drop-down combo 
 * editors, with the sets of allowed values changing dynamically.
 * 
 * The instance of this class works out what values are allowed, for each property using one
 * object supplied to it. The instance must be notified when a new object is made
 * @author robert
 *
 */
public class PropertyValueSetProvider {
	
	String[] unsetValue = {""};
	
	/**
	 * Model classes and their properties that have dynamic sets of values
	 */
	private String[][] dynamicProperties = {{"MappedStructure","Top Element Type"},
											{"MappedStructure","Top Element Name"}};
	
	/**
	 * those value supplier objects currently known, keyed by 
	 * <Model Class Name>$<Model Property Name>
	 */
	private Hashtable<String,PropertyValueSupplier> valueSuppliers =
	     new Hashtable<String,PropertyValueSupplier>();

	/**
	 * 
	 * @param className the name of the model class
	 * @param propertyName the display name of the property
	 * @return true only if this property needs a drop-down editor with a changeable set of values
	 */
	public boolean hasDynamicDropDownMenu(String className, String propertyName)
	{
		boolean hasDynamic = false;
		for (int i = 0; i < dynamicProperties.length;i++)
		{
			String[] dynamicProperty = dynamicProperties[i];
			if ((dynamicProperty[0].equals(className)) && 
					(dynamicProperty[1].equals(propertyName))) hasDynamic = true;
		}
		return hasDynamic;
	}
	
	/**
	 * 
	 * @param className the name of the model class
	 * @param propertyName the display name of the property
	 * @return String array of possible values; probably includes the 'unset' value ""
	 */
	public String[] values(String className, String propertyName)
	{
		String key = className + "$" + propertyName;
		PropertyValueSupplier ps = valueSuppliers.get(key);
		if (ps != null) return ps.propertyValues(className, propertyName);
		else return unsetValue;
	}
	
	/**
	 * the instance of this class, used by the editor, must be notified if there 
	 * is a new object which will supply the set of allowed values.
	 * 
	 * @param className the name of the model class
	 * @param featureName the display name of the property
	 * @param supplier the Object that will supply the values for this property
	 */
	public void notifyNewValueSupplier (String modelClassName, 
			String modelPropertyName, PropertyValueSupplier supplier)
			throws MapperException
	{
		if (supplier.suppliesPropertyValues(modelClassName, modelPropertyName))
		{
			String key = modelClassName + "$" + modelPropertyName;
			valueSuppliers.put(key, supplier);
		}
		else throw new MapperException
			("Property value supplier does not supply values for model class '"
					+ modelClassName + "' , property '" + modelPropertyName + "'");
	}

}
