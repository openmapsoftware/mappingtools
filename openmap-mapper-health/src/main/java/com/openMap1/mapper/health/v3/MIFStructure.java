package com.openMap1.mapper.health.v3;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.openMap1.mapper.converters.CDAConverter;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;

/**
 * Class to define ElementDef trees from the RMIM class model.
 * Each ElementDef tree is used to make one of the V3-V3 mapping sets.
 * @author robert
 *
 */
public class MIFStructure implements StructureDefinition {
	
	private RMIMReader rmimReader;
	
	// the overarching model package, not the package with the top classes in it
	private EPackage topPackage;
	
	// list of top classes; one of them, unless the RMIM has a choice on top
	private List<EClass> topClasses;
	
	/** key = type name; value = the ElementDef structure with that type */
	public Hashtable<String,ElementDef> allStructures() {return allStructures;}
	private Hashtable<String,ElementDef> allStructures;
	
	/** key = type name; value = the EClass to be mapped to the ElementDef with that type */
	private Hashtable<String,EClass> allEClasses;

	/**
	 * @param typeName name of a complex type in a MappedStructure
	 * @return the EClass mapped to that node
	 */
	public EClass getEClassForType(String typeName) {return allEClasses.get(typeName);}
	
	//private int treeSize;

	private boolean tracing = false;
	
	//-----------------------------------------------------------------------------------------------
	//                                          Constructor
	//-----------------------------------------------------------------------------------------------
	
	public MIFStructure(List<EClass> topClasses, EPackage topPackage,RMIMReader rmimReader)
	{
		this.rmimReader = rmimReader;
		this.topClasses = topClasses;
		this.topPackage = topPackage; // the overarching model package, not the package with the top classes in it
		countReferencesToClasses(this.topPackage);	// used to set 'references' which is not used	
		makeAllStructures();
	}
	
	//-----------------------------------------------------------------------------------------------
	//                Traverse the Ecore model to make all ElementDef structures needed
	//-----------------------------------------------------------------------------------------------

	private void makeAllStructures()
	{
		allStructures = new Hashtable<String,ElementDef>();
		allEClasses = new Hashtable<String,EClass>();
		
		// set up the initial set of type names and EClasses to make structures for
		for (Iterator<EClass> it = topClasses.iterator();it.hasNext();)
		{
			EClass topClass = it.next();

			// the type name is the top RMIM name followed by the top class name 
			String typeName = topClass.getEPackage().getName() + "_" + topClass.getName();
			
			allEClasses.put(typeName, topClass);			
		}
		
		/* Start making structures for type names. The Hashtable of type names will extend
		 * as you make further structures; but keep on passing through the Hashtable
		 * until there is a structure for every entry */
		int pass = 0;
		while (someStructuresNotMade())
		{
			pass++;
			trace("Pass " + pass);
			for (Enumeration<String> en = allEClasses.keys();en.hasMoreElements();)
			{
				String typeName = en.nextElement();
				EClass theClass = allEClasses.get(typeName);
				storeElementDef(typeName,theClass);
			}			
		}
	}
	
	/**
	 * @return true if some type names have been found, 
	 * but the ElementDef structures not yet made for them
	 */
	private boolean someStructuresNotMade()
	{
		boolean someNotMade = false;
		for (Enumeration<String> en = allEClasses.keys();en.hasMoreElements();)
		{
			String typeName = en.nextElement();
			if (allStructures.get(typeName) == null) someNotMade = true;			
		}
		return someNotMade;
	}
	
	/**
	 * Create and store an ElementDef under its type name, if it has not been stored already;
	 * and recursively fill in the structure under it
	 * @param typeName
	 * @param theClass
	 */
	private void storeElementDef(String typeName, EClass theClass)
	{
		// ensure you don't build any structure more than once
		if (allStructures.get(typeName) == null)
		{
			// make and store the ElementDef
			ElementDef elementDef = MapperFactory.eINSTANCE.createElementDef();
			elementDef.setName(theClass.getName());
			elementDef.setType(typeName);
			allStructures.put(typeName, elementDef);
			
			// add AttributeDefs and child ElementDefs recursively down the tree, noting RIM classes
			makeStructure(elementDef, theClass);
			// trace("Type " + typeName + " size: " + treeSize);
		}
	}
	
	/**
	 * @param elementDef an ElementDef whose name and type have been defined
	 * @param theClass the class it represents
	 * fills in the correct structure of child elements and attributes
	 */
	public void makeStructure(ElementDef elementDef, EClass theClass)
	{
		//treeSize = 0; // to keep track of the number of ElementDefs under this one
		Vector<String> rimClassNames = new Vector<String>();
		rimClassNames.add(RIMClassName(theClass));
		addAttributesAndChildren(elementDef, theClass,rimClassNames);			
	}
	
	/**
	 * @param theClass EClass for an RMIM class
	 * @return the name of the RIM class it is a clone of
	 */
	private String RIMClassName(EClass theClass)
		{return ModelUtil.getEAnnotationDetail(theClass, "RIM Class");}
	
	/**
	 * @param elementDef an ElementDef  to be put in the MappedStructure for an RMIM
	 * @param theClass the class it will represent
	 * @param rimClassNames Vector to avoid infinite recursion
	 * Add AttibuteDefs and for EAttributes of the class, and child 
	 * ElementDefs for EReferences of the class, recursively down
	 * the RMIM tree until you come to a class in another package (RMIM).
	 */
	private void addAttributesAndChildren(ElementDef elementDef, EClass theClass, Vector<String> rimClassNames)
	{
		// treeSize++;
		// trace("Depth "  + classNames.size() + " " + theClass.getName() + " " + elementCount);
		// as we are adding attributes and child elements to this ElementDef, it is expanded
		elementDef.setExpanded(true);

		/* for each EAttribute of the class (RIM Structural attribute), make a child AttributeDef; 
		 * and for each EReference, make a child ElementDef*/
		for (Iterator <EStructuralFeature> it = theClass.getEAllStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature next = it.next();
			
			if (next instanceof EAttribute)
			{
				EAttribute att = (EAttribute)next;
				AttributeDef atDef = MapperFactory.eINSTANCE.createAttributeDef();
				atDef.setName(att.getName());
				atDef.setMinMultiplicity(MinMult.get(att.getLowerBound()));
				elementDef.getAttributeDefs().add(atDef);
			}
			
			else if (next instanceof EReference)
			{
				EReference ref = (EReference)next;
				EClass childClass = (EClass)ref.getEType();
				EPackage childPackage = childClass.getEPackage();
				if (childPackage != null)
				{
					String childPackageName = childClass.getEPackage().getName();
					ElementDef childElementDef = MapperFactory.eINSTANCE.createElementDef();
					// the name of the child element is the association role name
					childElementDef.setName(ref.getName());
					childElementDef.setMinMultiplicity(MinMult.get(ref.getLowerBound())); // {0,1} => {0,1}
					// default MaxMultiplicity of ElementDef is 1
					if (ref.getUpperBound() == -1) childElementDef.setMaxMultiplicity(MaxMult.UNBOUNDED);

					// the type of the child element is "" unless it is a switch to another mapping set
					String typeName = "";

					if  (expandNode(theClass,childClass,rimClassNames))
					{
						Vector<String> newClassNames = new Vector<String>();
						for (Iterator<String> is = rimClassNames.iterator();is.hasNext();) newClassNames.add(is.next());
						newClassNames.add(RIMClassName(childClass));

						// recursion of this method, to fill out structure under the child element
						addAttributesAndChildren(childElementDef, childClass,newClassNames);
					}
					
					// switch to another mapping set ; stop recursion and note the structure needs to be made
					else
					{
						// the child element in this subtree is not expanded
						childElementDef.setExpanded(false);
						
						// if the type name is the data type name
						if (childPackageName.equals("datatypes")) typeName = childClass.getName();
						
						// mapped structures have already been made for all the data types
						else if (!childPackageName.equals("datatypes"))
						{
							// the type name for a non-datatype is the child package name followed by the class name
							typeName = childPackageName + "_" + childClass.getName();
							// this will cause the ElementDef structure for the type to be made later
							allEClasses.put(typeName, childClass);
						}
					}

					childElementDef.setType(typeName);
					elementDef.getChildElements().add(childElementDef);
					
				}
				else trace("Still no package for class " + childClass.getName());
			}
		}		
	}
	

	//-----------------------------------------------------------------------------------------------
	//                        Criteria for cutting off ElementDef trees
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * criterion to expand or not expand an ElementDef
	 */
	private boolean expandNode(EClass theClass, EClass childClass, Vector<String> rimClassNames)
	{
		boolean repeatedRIMClass = false;
		if (RIMClassName(childClass) != null) // untrue for data type classes
			repeatedRIMClass = GenUtil.inVector(RIMClassName(childClass), rimClassNames); 
		String packageName = theClass.getEPackage().getName();
		String childPackageName = childClass.getEPackage().getName();

		// avoid over-large trees by cutting off at depth 2,  or if the package changes
		boolean expandNode = 
			((childPackageName.equals(packageName)) && (rimClassNames.size() < 2) && !repeatedRIMClass);
		return expandNode;		
	}
	
	/**
	 * print out a list of how many EReferences there are to each class,
	 * in the top sub-packages of a main package
	 * @param topPackage
	 * @return references: for each class, the number of references to it plus one
	 */
	private Hashtable<String,Integer> countReferencesToClasses(EPackage topPackage)
	{
		Hashtable<String,Integer> references = new Hashtable<String,Integer>();
		for (Iterator<EPackage> ip = topPackage.getESubpackages().iterator();ip.hasNext();)
		{
			EPackage subPackage = ip.next();
			for (Iterator<EClassifier> ic = subPackage.getEClassifiers().iterator();ic.hasNext();)
			{
				EClassifier next = ic.next();
				saveCount(next.getName(),references); // one reference for existing
				if (next instanceof EClass)
				{
					EClass theClass = (EClass)next;
					for (Iterator<EReference> ir = theClass.getEReferences().iterator();ir.hasNext();)
						saveCount(ir.next().getEType().getName(),references);
				}
			}			
		}
		
		trace("Count of references");
		boolean isFalse = false;
		if (isFalse) for (Enumeration<String> en = references.keys(); en.hasMoreElements();)
		{
			String target = en.nextElement();
			//  for each class, remove the score 1 for existing
			trace(target + ", " + (references.get(target).intValue()- 1));
		}
		
		return references;
	}
	
	private void saveCount(String target, Hashtable<String,Integer> references)
	{
		Integer count = references.get(target);
		if (count == null) count = new Integer(1);
		else count = new Integer(count.intValue() + 1);
		references.put(target,count);		
	}
	
		
	/**
	 * @param elDef root of an ElementDef tree
	 * @return number of ElementDefs in the tree
	 */
	public int getElementSize(ElementDef elDef)
	{
		int size = 1;
		for (Iterator<ElementDef> it = elDef.getChildElements().iterator(); it.hasNext();)
			size = size + getElementSize(it.next());
		return size;
	}

	//-----------------------------------------------------------------------------------------------
	//                             Interface StructureDefinition
	//-----------------------------------------------------------------------------------------------

	/**
	 * find the Element and Attribute structure of some named top element (which may have a named
	 * complex type, or a locally defined anonymous type), stopping at the
	 * next complex type definitions it refers to
	 * @param String name the name of the element
	 * @return null, as this method is not used
	 */
	public ElementDef nameStructure(String name) throws MapperException
	{
		boolean isTrue = true;
		if (isTrue) throw new MapperException("Unexpected call for a MIF structure ElementDef of name '" + name + "'");
		return null;
	}

	/**
	 * find the Element and Attribute structure of some complex type, stopping at the
	 * next complex type definitions it refers to
	 * @param type the name of the complex type
	 * @return the EObject subtree (Element and Attribute EObjects) defined by the type
	 */
	public ElementDef typeStructure(String type) throws MapperException
	{
		return allStructures.get(type);
	}
	
	/**
	 * 
	 * @return an array of the top-level complex types defined in the structure definition - 
	 * any of which can be the type of a mapping set
	 */
	public String[] topComplexTypes()
	{
		ArrayList<String> allTypes = new ArrayList<String>();
		allTypes.add(""); // the default choice on the menu, before any choice is made, is ""
		for (Enumeration<String> en = allStructures.keys();en.hasMoreElements();)
			allTypes.add(en.nextElement());
		String[] res = new String[allTypes.size()];
		return allTypes.toArray(res);
	}
	

	/**
	 * @return the set of namespaces defined for the structure
	 */
	public NamespaceSet NSSet()
	{
		NamespaceSet nss = new NamespaceSet();
		try{
			// always add the xsi namespace
			nss.addNamespace(new namespace(XMLUtil.SCHEMAINSTANCEPREFIX,XMLUtil.SCHEMAINSTANCEURI));
						
			// for CDA RMIMs...(NHS or otherwise) 
			EPackage classPackage = topClasses.get(0).getEPackage();
			boolean addV3Namespace = ((classPackage.getNsPrefix() != null) && 
					(classPackage.getNsPrefix().equals(RMIMReader.CDAPREFIX)));
			if (rmimReader.isNHSMIF()) addV3Namespace = true;
					
			if (addV3Namespace)
			{
				// add the HL7 V3 namespace with no prefix
				nss.addNamespace(new namespace("",CDAConverter.V3NAMESPACEURI));
				
				// add the Vocabulary namespace
				nss.addNamespace(new namespace(RMIMReader.VOCABULARYNAMESPACEPREFIX,RMIMReader.VOCABULARYNAMESPACEURI));		
			}
		}
		catch(NamespaceException ex) {}
		return nss;
	}
	
	
	//-----------------------------------------------------------------------------------------------
	//                             Interface PropertyValueSupplier
	//-----------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return true if this property value supplier supplies values for the mapper
	 * model class and property
	 */
	public boolean suppliesPropertyValues(String modelClassName, String modelPropertyName)
	{
		if ((modelClassName.equals("MappedStructure")) && 
				(modelPropertyName.equals("Top Element Type"))) return true;
		if ((modelClassName.equals("MappedStructure")) && 
				(modelPropertyName.equals("Top Element Name"))) return true;
		return false;
	}


	/**
	 * 
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return the values supplied by this supplier for the mapper model class and property
	 */
	public String[] propertyValues(String modelClassName, String modelPropertyName)
	{
		String[] vals = {};
		if ((modelClassName.equals("MappedStructure")) && 
					(modelPropertyName.equals("Top Element Type"))) return topComplexTypes();
		if ((modelClassName.equals("MappedStructure")) && 
					(modelPropertyName.equals("Top Element Name"))) return new String[0];			
		return vals;
	}

	//---------------------------------------------------------------------------------------------------
	//                                         Trivia
	//---------------------------------------------------------------------------------------------------

	private void trace(String s) {if (tracing) System.out.println(s);}

}
