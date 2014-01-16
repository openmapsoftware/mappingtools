package com.openMap1.mapper.structures;


import com.openMap1.mapper.core.PropertyValueSupplier;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.ElementDef;


/**
 * Every mapping set must have some class implementing this 
 * interface, to define the tree structure of the XML for
 * the mapping set.
 * 
 * @author robert
 *
 */
public interface StructureDefinition extends PropertyValueSupplier{

	/**
	 * find the Element and Attribute structure of some named top element (which may have a named
	 * complex type, or a locally defined anonymous type), stopping at the
	 * next complex type definitions it refers to
	 * @param String name the name of the element
	 * @return Element the EObject subtree (Element and Attribute EObjects) defined by the name
	 */
	public ElementDef nameStructure(String name) throws MapperException;

	/**
	 * find the Element and Attribute structure of some complex type, stopping at the
	 * next complex type definitions it refers to
	 * @param type the name of the complex type
	 * @return the EObject subtree (Element and Attribute EObjects) defined by the type
	 */
	public ElementDef typeStructure(String type) throws MapperException;
	
	/**
	 * 
	 * @return an array of the top-level complex types defined in the structure definition - 
	 * any of which can be the type of a mapping set
	 */
	public String[] topComplexTypes();
	

	/**
	 * @return the set of namespaces defined for the structure
	 */
	public NamespaceSet NSSet();
	
	

}
