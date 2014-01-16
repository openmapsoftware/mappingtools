package com.openMap1.mapper.core;

public interface PropertyValueSupplier {

	/**
	 * 
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return true if this property value supplier supplies values for the mapper
	 * model class and property
	 */
	public boolean suppliesPropertyValues(String modelClassName, String modelPropertyName);

	/**
	 * 
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return the values supplied by this supplier for the mapper model class and property
	 */
	public String[] propertyValues(String modelClassName, String modelPropertyName);

}
