package com.openMap1.mapper.health.v3;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchema;

import com.openMap1.mapper.converters.CDAConverter;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.impl.ElementDefImpl;
import com.openMap1.mapper.structures.XSDStructure;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MinMult;

/**
 * reads V3 data types from a data types schema; makes the 
 * datatypes package in the ECore model, and the mappings of the
 * V3 XML ITS onto these classes (the mappings are generally not used,
 * because the V3 XML ITS is encapsulated in a Java mapper class)
 * 
 * @author robert
 *
 */
public class V3DataTypeHandler {
	
	boolean tracing = false;
	
	private String projectName;
	
	private XSDStructure dataTypeStructureDefinition;
	
	private RMIMReader rmimReader;
	
	private EPackage dataTypePackage;
	
	public static String[] DATATYPES_WITH_ORDERED_CHILDREN = {"AD","EN","PN"};
	
	/* normally mixed type in the schema indicates possible text content; but it appears some 
	 * other types have text content as well. */
	public static String[] DATATYPES_WITH_TEXT_CONTENT = {"AD","EN","PN","ED","ST"};
	
	private boolean hasOrderedChildren(String typeName)
		{return GenUtil.inArray(typeName, DATATYPES_WITH_ORDERED_CHILDREN);}
	
	public static String V3_CHILD_ORDER_PROPERTY = "element_position";
	
	// if true, elements without prefixes are in the HL7 V3 namespace
	private boolean putElementsInV3Namespace;
	
	
	protected MapperPackage mapperPackage = MapperPackage.eINSTANCE;
	protected MapperFactory mapperFactory = mapperPackage.getMapperFactory();

	public V3DataTypeHandler(RMIMReader rmimReader,String projectName,boolean putElementsInV3Namespace)
	{
		this.rmimReader = rmimReader;
		this.projectName = projectName;
		this.putElementsInV3Namespace = putElementsInV3Namespace;
	}
	
	
	/**
	 * 
	 * @param ecoreFilePath
	 * @param saveMappings
	 * @return
	 * @throws MapperException
	 */
	public EPackage readDataTypeSchema(String ecoreFilePath, boolean saveMappings) throws MapperException
	{
		// (1) Make the data types package and attach it the the RMIM ecore model
		dataTypePackage = EcoreFactory.eINSTANCE.createEPackage();
		dataTypePackage.setName(RMIMReader.DATATYPE_PACKAGE_NAME);
		rmimReader.topPackage().getESubpackages().add(dataTypePackage);

		// (2) Open the schema as an XSDStructure
		String schemaLocation = "platform:/resource/" + projectName + "/Structures/coreschemas/datatypes.xsd";
		URI uri = URI.createURI(schemaLocation);
		XSDSchema theSchema = XSDStructure.getXSDRoot(uri);
		if (theSchema != null)  dataTypeStructureDefinition = new XSDStructure(theSchema);
		else throw new MapperException("Cannot open V3 data types schema at '" + schemaLocation + "'");
		// if elements without prefixes are to be in the HL7 V3 namespace....(eg for CDA)
		if (putElementsInV3Namespace)
			dataTypeStructureDefinition.NSSet().addNamespace(new namespace("",CDAConverter.V3NAMESPACEURI));
		
		// (3) Make a 'V3DataTypes' subfolder to hold the data type V3-V3 mappings
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);		
		IFolder mappingsFolder = project.getFolder("MappingSets");
		IFolder V3DataTypesFolder = mappingsFolder.getFolder("V3DataTypes");
		if ((saveMappings) && (!V3DataTypesFolder.exists())) 
		{
			try {V3DataTypesFolder.create(true, true, null);} // force, local, no progress monitor 
			catch (Exception ex) {throw new MapperException("Cannot make folder for data type mapping sets");}
		}
		
		// (4) iterate over all data types
		String[] typeNames = dataTypeStructureDefinition.topComplexTypes();
		String names = "Data types: ";
		for (int i = 0; i < typeNames.length; i++)
		{
			String typeName = typeNames[i];
			handleDataType(typeName,schemaLocation,V3DataTypesFolder,ecoreFilePath,saveMappings);
			names = names + typeName + " ";
		}
		
		// (4) for data types with ordered children, add the property to child classes and map it
		
		return dataTypePackage;
	}
	
	/**
	 * Make the mapping set, Ecore class, and mappings for a data type,
	 * if they have not been made already
	 * @param typeName
	 */
	private void handleDataType(String typeName, String schemaLocation,
			IFolder V3DataTypesFolder, String ecoreFilePath, boolean saveMappings) 
	throws MapperException
	{
		IFile existingMappingSet = V3DataTypesFolder.getFile(typeName + ".mapper");
		/* if the mapping set exists, make it but do not save it, 
		 * so as to add the data type classes to the class model */
		if (hasOwnMappingSet(typeName))
		{
			// (1) Make an empty mapping set in a 'V3DataTypes' folder of the MappingSets folder
			String mappingSetLocation = RMIMReader.getDataTypeMappingSetLocation(projectName,typeName);
			URI uri = URI.createURI(mappingSetLocation);			
			Resource mappingSet = ModelUtil.makeNewMappingSet(uri);
			MappedStructure mappedStructure = (MappedStructure)mappingSet.getContents().get(0);
			mappedStructure.setUMLModelURL(ecoreFilePath);
			
			// (2) make the data types schema the structure definition for the mapping set
			/* if the structure URL is set to the true location of the data types schema, then
			 * there is a memory overflow on validating more than one data type schema. 
			 * So the location is set to "", which produces one validation error per mapping set*/
			mappedStructure.setStructureURL("");
			mappedStructure.setStructureDefinition(dataTypeStructureDefinition);
			
			// (3) make the data type name the top complex type for the schema, and expand it
			mappedStructure.setTopElementType(typeName);
			ElementDef newStructure = dataTypeStructureDefinition.typeStructure(typeName);
			newStructure.setExpanded(true);
			newStructure.setName("top"); // needs some name to prevent '//' in XPaths
			mappedStructure.setRootElement(newStructure);
			
			/* (4) make the data type class, with its properties and associations, and make mappings 
			 * if saveMappings = false, make the mappings but do not save them. */
			EClass dataTypeClass = makeDataTypeClassAndMappings(mappedStructure,typeName);
			
			// (5) Add a parameter class to the top node, to make this mapping set importable by others
			RMIMReader.addParameterClass(mappedStructure, dataTypeClass);

			// (6) save the mapping set, if it does not exist already
			if ((saveMappings) && (!existingMappingSet.exists())) ModelUtil.saveMappingSet(mappingSet);
		}
	}
	
	private boolean hasOwnMappingSet(String typeName)
	{
		boolean hasOwn = true;
		if (typeName.startsWith("adxp")) hasOwn = false;
		if (typeName.startsWith("en")) hasOwn = false;
		if (typeName.startsWith("thumb")) hasOwn = false;
		if (typeName.equals("")) hasOwn = false;
		return hasOwn;
	}

	/**
	 * @param type a V3 data type
	 * @return  true if this type has to be some aggregate of a parameter type
	 */
	public static boolean isAggregateType(String type)
	{
		if (type.equals("LIST")) return true;
		if (type.equals("BAG")) return true;
		if (type.equals("SET")) return true;
		if (type.equals("DSET")) return true;
		return false;
	}
	
	//------------------------------------------------------------------------------------------
	//             making the data type class, properties, associations and mappings
	//------------------------------------------------------------------------------------------
	
	private EClass makeDataTypeClassAndMappings(MappedStructure mappedStructure,String typeName)
	throws MapperException
	{
		trace("Data type '" + typeName + "'");
		// find or make the class (it might have been made already)
		EClass dataTypeClass = findOrMakeEClass(dataTypePackage,typeName);
		
		// make the object mapping on the top node
		ElementDef root = mappedStructure.getRootElement();
		String subset = rmimReader.getSubset(typeName,typeName,RMIMReader.DATATYPE_PACKAGE_NAME);
		RMIMReader.addObjectMapping(root,dataTypePackage,typeName,subset);
		
		// for each attribute of the root, make a property of the EClass and a mapping to it
		boolean isOrdered = false; // this class has no ordering property; its children might have
		makePropertiesAndMappings(dataTypePackage,root, dataTypeClass,subset,isOrdered);
		
		// for each child element of the root, make an object mapping and an association mapping
		Vector<String> classNames = new Vector<String>();
		classNames.add(dataTypeClass.getName()); // to avoid infinite recursion
		makeChildObjectMappings(typeName,dataTypePackage,root, dataTypeClass,subset,classNames,hasOrderedChildren(typeName));
		
		return dataTypeClass;

	}
	
	/**
	 * find a named class in a package, if it exists; or if it does not, make it.
	 * @param aPackage a package
	 * @param className a class name
	 * @return
	 */
	private EClass findOrMakeEClass(EPackage aPackage,String className)
	{
		EClass aClass = null;
		EClassifier ec = aPackage.getEClassifier(className);
		if ((ec != null) && (ec instanceof EClass)) aClass = (EClass)ec;
		
		if (aClass == null)
		{
			aClass = EcoreFactory.eINSTANCE.createEClass();
			aClass.setName(className);
			aPackage.getEClassifiers().add(aClass);			
		}
		return aClass;
	}
	
	/**
	 * @param aPackage the package that all classes are to be in
	 * @param node an ElementDef in the mapped structure
	 * @param aClass the EClass it is mapped to
	 * @param isOrdered if true, the ElementDef should have an AttributeDef
	 * for a virtual attribute, mapped to an ordinal position property.
	 * 
	 * For every AttributeDef under an ElementDef, make a property on the EClass
	 * and a mapping to it.
	 */
	private void makePropertiesAndMappings(EPackage aPackage,ElementDef node, EClass aClass, String subset, boolean isOrdered)
	{
		boolean makeAttributeTypes = true;
		
		// add a property for every AttributeDef of the ElementDef
		for (Iterator<AttributeDef> it = node.getAttributeDefs().iterator(); it.hasNext();)
		{
			AttributeDef attDef = it.next();
			String propertyName = attDef.getName();
			String propertyType = attDef.getType();
			boolean isOptional = (attDef.getMinMultiplicity() == MinMult.ZERO);
			String fixedValue = attDef.getFixedValue();
			
			// add the property to the EClass
			trace("Add property " + propertyName + " with type " + propertyType);
			addProperty(aClass,propertyName,propertyType,makeAttributeTypes,isOptional,fixedValue);
						
			// add the mapping from the AttributeDef to the property
			RMIMReader.addPropertyMapping(attDef, aClass, propertyName,subset);			
		}
		
		// add some xsi:type properties to class PQ, which somehow does not get read in from the data types schema
		addXSITypeProperty(aClass,makeAttributeTypes);
		
		/* for child classes with a virtual position attribute, add the virtual attribute to the
		 * structure, add the property to the EClass, and add the mapping between them. */
		if (isOrdered)
		{
			// make the virtual AttributeDef for ordinal position and and it to the ElementDef
			AttributeDef positionAtt = MapperFactory.eINSTANCE.createAttributeDef();
			positionAtt.setName(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE);
			node.getAttributeDefs().add(positionAtt);
			
			// add the property to the EClass
			addProperty(aClass,V3_CHILD_ORDER_PROPERTY,"EString",makeAttributeTypes,true,"");
			
			// add the mapping
			RMIMReader.addPropertyMapping(positionAtt, aClass, V3_CHILD_ORDER_PROPERTY,subset);			
		}
		
		// if the Element has mixed type, add a property 'textContent' mapped to the Element
		boolean hasTextContent = ((node.isMixed()|
				(GenUtil.inArray(node.getType(), DATATYPES_WITH_TEXT_CONTENT))));
		if (hasTextContent)
		{
			String propertyName = "textContent";
			String propertyType = "st";
			// add the property to the EClass
			addProperty(aClass,propertyName,propertyType,makeAttributeTypes,true, null);	
			// add the mapping
			RMIMReader.addPropertyMapping(node, aClass, propertyName,subset);			
		}
	}
	
	/**
	 * for data types PQ (but not INT), there should be a property 'xsi:type' with fixed value 'PQ' or 'INT'.
	 * For some reason this is not picked up for these classes when reading the data types schema. Add it.
	 * @param aClass
	 * @param makeAttributeTypes
	 */
	private void addXSITypeProperty(EClass aClass,boolean makeAttributeTypes)
	{
		String className = aClass.getName();
		if ((className.equals("PQ"))/* |(className.equals("INT"))*/)
			addProperty(aClass,"xsi:type","string",makeAttributeTypes,false,className);
		// fixed value is the same as the class name
	}

	
	// add a property to the class, if  it has not been added already
	private void addProperty(EClass aClass,String propertyName,String propertyType,
			boolean makeAttributeTypes, boolean isOptional, String fixedValue)
	{
		if (aClass.getEStructuralFeature(propertyName) == null)
		{
			EAttribute att = EcoreFactory.eINSTANCE.createEAttribute();
			att.setName(propertyName);
			if (isOptional) att.setLowerBound(0); else att.setLowerBound(1);
			if (makeAttributeTypes) att.setEType(getEcoreDataType(propertyType));					
			if ((fixedValue != null) && (!fixedValue.equals("")))
				ModelUtil.addMIFAnnotation(att, "fixed value", fixedValue);
					
			aClass.getEStructuralFeatures().add(att);				
		}		
	}
	
	/**
	 * 
	 * @param propertyType V3 data type attribute type
	 * @return corresponding ECore data type.
	 * Most of the strange V3 types like 'set_TelecommunicationAddressUse'
	 * just convert to EString.
	 */
	private EDataType getEcoreDataType(String propertyType)
	{
		EDataType aType = EcorePackage.eINSTANCE.getEString();
		if (propertyType != null)
		{
			if (propertyType.equals("real")) aType = EcorePackage.eINSTANCE.getEFloat();
			if (propertyType.equals("probability")) aType = EcorePackage.eINSTANCE.getEFloat();
			if (propertyType.equals("bl")) aType = EcorePackage.eINSTANCE.getEBoolean();
			if (propertyType.equals("int")) aType = EcorePackage.eINSTANCE.getEInt();			
		}
		return aType;		
	}

	
	/**
	 * @param mappingSetName the name of the mapping set
	 * @param aPackage the package that all classes are to be in
	 * @param node an ElementDef in the mapped structure
	 * @param aClass the EClass it is mapped to
	 * @param aSubset the subset allocated to that class in its mapping
	 * @param hasChildOrder: if true, the child classes must have an order property, mapped to
	 * a virtual order element.
	 * for every child ElementDef of an ElementDef, make or find the class,
	 * make an object mapping to it and an association mapping.
	 */
	private void makeChildObjectMappings(String mappingSetName,EPackage aPackage,
			ElementDef node, EClass aClass, String aSubset, Vector<String> classNames, boolean hasChildOrder)
	throws MapperException
	{ 
		for (Iterator<ElementDef> id = node.getChildElements().iterator();id.hasNext();)
		{
			// add the child class and its object mapping
			ElementDef child = id.next();
			EClass childClass = null;
			String childType = child.getType();

			/* if the schema uses anonymous local types, make up a type name from the node name,
			 * and give the child class attributes to match those of the child node */
			if (childType == null) 
			{
				childType = child.getName();
				childClass = findOrMakeEClass(aPackage,childType);
				addAttributesToClass(childClass, child);
			}
			else childClass = findOrMakeEClass(aPackage,childType);

			String childSubset = rmimReader.getSubset(mappingSetName, childType, RMIMReader.DATATYPE_PACKAGE_NAME);
			RMIMReader.addObjectMapping(child,aPackage,childType,childSubset);
			
			/* add the containment association to the child class 
			 * (with no EOpposite association), if it has not been added already */
			if (aClass.getEStructuralFeature(child.getName()) == null)
			{
				EReference ref = EcoreFactory.eINSTANCE.createEReference();
				ref.setName(child.getName());
				ref.setEType(childClass);
				int min = new Integer(child.getMinMultiplicity().getLiteral()).intValue();
				int max = new Integer(child.getMaxMultiplicity().getLiteral()).intValue();
				ref.setLowerBound(min);
				ref.setUpperBound(max);
				ref.setContainment(true);
				aClass.getEStructuralFeatures().add(ref);				
			}
			
			// add the association mapping on the child node
			RMIMReader.addAssociationMapping(child, aClass, aSubset, childClass, childSubset);
			
			// if the child data type has its own mapping set, import the mapping set on the child node
			if (hasOwnMappingSet(childType))
			{
				String mappingSetURI = RMIMReader.getDataTypeMappingSetLocation(projectName,childType);
				RMIMReader.addImportMappingSet(child, mappingSetURI, childClass, childSubset);
			}
			
			/* if the child data type does not have has its own mapping set, 
			 * extend this mapping set tree;  */
			else if (!hasOwnMappingSet(childType))
			{
				// extend the mappedStructure tree by the structure for the type
				ElementDef newStructure = dataTypeStructureDefinition.typeStructure(childType);
				child.setExpanded(true);
				
				// two-stage transfer of child elements
				Vector<ElementDef> els = new Vector<ElementDef>();
				for (Iterator<ElementDef> it = newStructure.getChildElements().iterator(); it.hasNext();)
					els.add(it.next());
				for (Iterator<ElementDef> it = els.iterator();it.hasNext();)
					child.getChildElements().add(it.next());

				// two-stage transfer of attributes
				Vector<AttributeDef> atts = new Vector<AttributeDef>();
				for (Iterator<AttributeDef> it = newStructure.getAttributeDefs().iterator(); it.hasNext();)
					atts.add(it.next());
				for (Iterator<AttributeDef> it = atts.iterator();it.hasNext();)
					child.getAttributeDefs().add(it.next());
				
				// make the child have mixed content if its type does
				child.setIsMixed(newStructure.isMixed());
				
				// for each attribute of the child node, make a property of the EClass and a mapping to it
				makePropertiesAndMappings(dataTypePackage,child, childClass,childSubset,hasChildOrder);

				/* for each child element of the child node, make an object mapping and association mapping,
				 * avoiding infinite recursion through data types that don't have their own mapping sets */
				if (!GenUtil.inVector(childClass.getName(), classNames))
				{
					Vector<String> newClassNames = new Vector<String>();
					for (Iterator<String> is = classNames.iterator();is.hasNext();) newClassNames.add(is.next());
					newClassNames.add(childClass.getName());
					// 'false' mean children of child classes are never ordered.
					makeChildObjectMappings(mappingSetName,dataTypePackage,child, childClass,childSubset,newClassNames,false);					
				}
			}

		}
	}
	
	/**
	 * A class has been made without any attributes or associations, because it has no associated complex type.
	 * But the necessary attributes are already present in the mapped structure for the parent type.
	 * Add those attributes to the child class, and add a textContent attribute in case it is needed.
	 * FIXME does not yet handle associations of the child class? (could easily do so)
	 * writes a trace if there are any needed
	 * @param childClass
	 * @param child
	 */
	private void addAttributesToClass(EClass childClass, ElementDef child) throws MapperException
	{
		boolean makeAttributeTypes = true;
		trace("Adding properties to class '" + childClass.getName() + "' with anonymous type.");

		for (Iterator<AttributeDef> it = child.getAttributeDefs().iterator();it.hasNext();)
		{
			AttributeDef attDef = it.next();
			EStructuralFeature ea = childClass.getEStructuralFeature(attDef.getName());
			if (ea == null) // the class should never have the attribute already, but just to be safe....
			{
				String propertyName = attDef.getName();
				String propertyType = attDef.getType();
				boolean isOptional = (attDef.getMinMultiplicity() == MinMult.ZERO);
				String fixedValue = attDef.getFixedValue();
				
				// add the property to the EClass
				trace("Add property " + propertyName + " with type " + propertyType);
				addProperty(childClass,propertyName,propertyType,makeAttributeTypes,isOptional,fixedValue);				
			}
			
			// add a textContent property, in case it is needed
			String propertyName = "textContent";
			String propertyType = "st";
			addProperty(childClass,propertyName,propertyType,makeAttributeTypes,true, null);	
		}
		int childEls = child.getChildElements().size();
		if (childEls > 0) 
			trace("Does not yet handle the " + childEls + " associations of class '" + childClass.getName() + "' with anonymous types");
	}

	
	private void trace(String s) {if (tracing) System.out.println(s);}
}
