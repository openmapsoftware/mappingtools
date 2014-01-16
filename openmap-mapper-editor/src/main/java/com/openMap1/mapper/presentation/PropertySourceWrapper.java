package com.openMap1.mapper.presentation;

import java.util.Hashtable;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;

/**
 * This class is a wrapper around the PropertySource used by the mapper editor.
 * For some properties of some model classes, it replaces the supplied text editor
 * by a combo box editor allowing certain values; the allowed values may change
 * @author robert
 *
 */
public class PropertySourceWrapper implements IPropertySource {
	
    // the class which provides sets of allowed values for some properties
	private PropertyValueSetProvider propertyValueSetProvider;

	private IPropertySource ps; // the supplied property source which this class is a wrapper around 
	private String className; // the class name of the selected object, owning the properties

	// for converting property ids to their display names
	private Hashtable<Object,String> DNameOfId = new Hashtable<Object,String>();
	
	public PropertySourceWrapper(IPropertySource ps, String className, PropertyValueSetProvider pvsp)
	{
		this.ps = ps;
		propertyValueSetProvider = pvsp;
		this.className = className;

		// link property ids to display names
		IPropertyDescriptor[] pda = ps.getPropertyDescriptors();
		for (int i = 0; i < pda.length; i++)
		{
			IPropertyDescriptor pd = pda[i];
			DNameOfId.put(pd.getId(),pd.getDisplayName());
		}
	}

	/**
	 * pass though the editable value unchanged from the underlying property source
	 */
	public Object getEditableValue() {
		return ps.getEditableValue();
	}

	@Override
	/**
	 * pass through the array of PropertyDescriptors unchanged, except for properties
	 * which have a set of values. For these, substitute a ComboBoxPropertyDescriptor
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] pda = ps.getPropertyDescriptors();
		for (int i = 0; i < pda.length; i++)
		{
			IPropertyDescriptor pd = pda[i];
			String dName = pd.getDisplayName();
			if (propertyValueSetProvider.hasDynamicDropDownMenu(className,dName))
			{
				Object id = pd.getId();
				pd = new ComboBoxPropertyDescriptor(id,dName,
						propertyValueSetProvider.values(className,dName));
			}
			pda[i] = pd;
		}
		return pda;
	}

	/**
	 * For properties with a set of allowed values,
	 * this converts a value in the stored resource to an Integer index, 
	 * to make the ComboBox editor display the correct String (or the default 
	 * with index 0 if no match is found)
	 */ 
	public Object getPropertyValue(Object id) {
		String dName = DNameOfId.get(id);
		if (propertyValueSetProvider.hasDynamicDropDownMenu(className,dName))
		{
			ItemPropertyDescriptor.PropertyValueWrapper pvw = 
				(ItemPropertyDescriptor.PropertyValueWrapper)ps.getPropertyValue(id);
			if (pvw == null) return new Integer(0); // return index 0 if the property has not been set yet
			Object edVal = pvw.getEditableValue(pvw);
			if (!(edVal instanceof String))
			{
				String cName = edVal.getClass().getName();
				System.out.println("Property value is not a String but a '" + cName 
				+ "' for property '" + dName + "' in class '" + className + "'");
			}
			else
			{
				String sv = (String)edVal;
				String[] values = propertyValueSetProvider.values(className,dName);
				for (int i = 0; i < values.length; i++)
				{
					if (sv.equals(values[i])) 
						return new Integer(i); // return the index of the matched value
				}
				/*
				 * 12/4/09 the following message has been suppressed for the field 'Top Element Type'
				 * because for v3 mapping sets, there
				 * is currently no schema used to define the structure, so no value appears valid
				 */
				if (!("Top Element Type").equals(dName))
					System.out.println("Value '" + sv 
						+ "' is not allowed for property '" + dName + "' in class '" + className + "'");
			}
			return new Integer(0); // return index 0 is there is no match
		}
		return ps.getPropertyValue(id);
	}

	/**
	 * pass though the boolean value unchanged from the underlying property source
	 */
	public boolean isPropertySet(Object id) {
		return ps.isPropertySet(id);
	}

	/**
	 * resetPropertyValue is as for the underlying property source
	 */
	public void resetPropertyValue(Object id) {
		ps.resetPropertyValue(id);
	}

	/**
	 * For properties with a set of allowed values,
	 * this converts the Combo box selection to a value to go in the stored resource
	 */
	public void setPropertyValue(Object id, Object value) {
		String dName = DNameOfId.get(id);
		if (propertyValueSetProvider.hasDynamicDropDownMenu(className,dName))
		{
			String[] values = propertyValueSetProvider.values(className,dName);
			if (value instanceof Integer)
			{
				int iv = ((Integer)value).intValue(); // the index of the combo selection
				String sv = values[iv]; // the value at that index
				ps.setPropertyValue(id, sv);
			}
			else 
			{
				System.out.println("Set of unexpected value; not an Integer"
				+ " for property '" + dName + "' in class '" + className + "'");
				resetPropertyValue(id);
			}
		}
		else ps.setPropertyValue(id,value);

	}

}
