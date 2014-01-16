package com.openMap1.mapper.structures;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

// EObjects in the EMF XSD plugin
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDForm;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema; 
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDFeature;

import org.eclipse.xsd.XSDFactory;

import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDConstraint;

import org.eclipse.xsd.util.XSDResourceImpl;


import com.openMap1.mapper.core.PropertyValueSupplier;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;



/**
 * This class has methods to convert an 
 * XML Schema into a tree representation of allowed XML structures.
 * 
 * The tree representation is in some places less restrictive than the XML schema -
 * eg where the schema defines choices -
 * i.e an XML instance may conform to the tree but not to the schema.
 * 
 * But any instance which conforms to the schema should conform to the tree. 
 * The tree is used to map nodes to a class model, not to test conformance of 
 * instances.
 * 
 * @author robert
 *
 */
public class XSDStructure implements StructureDefinition, PropertyValueSupplier {
	
	/* where types can be extended, allow elements in instances to have an attribute
	 * xsi:type which determines the type of an element as an extension or restriction of its base type */
	private static String defaultSchemaInstancePrefix = "xsi";
	
	private String schemaInstancePrefix = defaultSchemaInstancePrefix;
	
	private XSDSchema schema;
	
	// set this true to send to the console a detailed trace of the schema-to-tree operations
	boolean tracing = false;
	
	private void trace(String trail)
		{if (tracing) System.out.println(trail);}
	
    
    /* to cut off infinite recursions before they blow the stack 
     * (they should not occur in any case)*/
    private int MAX_REPEATS = 3;
    
    /**
     * @param trail
     * @return true id any ed(name) is repeated more than MAX_REPEATS times
     */
    private boolean tooDeep(String trail) 
    {
   	 boolean tooDeep = (maxRepeats(trail) > MAX_REPEATS);
   	 if (tooDeep) trace("Cut off recursion at trail " + trail);
   	 return tooDeep;
    }
    
    
    /**
     * @param trail
     * @return The maximum number of times any element name repeats in an 'ed(name)' step in a trail
     */
    private int maxRepeats(String trail)
    {
    	int max = 0;
    	Hashtable<String,Integer> repeats = new Hashtable<String,Integer>();
    	StringTokenizer st = new StringTokenizer(trail, "/");
    	while (st.hasMoreTokens())
    	{
    		String step = st.nextToken();
    		if (step.startsWith("ed"))
    		{
        		Integer times = repeats.get(step);
        		if (times == null) times = new Integer(0);
        		int next = times.intValue() + 1;
        		repeats.put(step, new Integer(next));
        		if (next > max) max = next;    			
    		}
    	}
    	return max;
    }
    
    /**
     * extend the trail of operations by adding  a new step;
     * but whenever the step is an 'ed' (element definition) step, 
     * drop the non-ed steps before it.
     * If tracing, write out the trail.
     * @param trail
     * @param nextStep
     * @return
     */
    private String extendTrail(String trail, String nextStep)
    {
    	String newTrail = trail + "/" + nextStep;
    	if (nextStep.startsWith("ed"))
    	{
    		newTrail = "";
    		StringTokenizer st = new StringTokenizer(trail,"/");
    		while(st.hasMoreTokens())
    		{
    			String step = st.nextToken();
    			if (step.startsWith("ed")) newTrail = newTrail + step + "/";
    		}
    		newTrail = newTrail + nextStep + "/";
    	}
    	trace(newTrail);
    	return newTrail;
    }

    private NamespaceSet NSSet;
	public NamespaceSet NSSet() {return NSSet;}
	
	private Hashtable<String,XSDComplexTypeDefinition> allComplexTypes;
	
	/**
	 * 
	 * @param topSchema
	 * @throws MapperException
	 */
	public XSDStructure(XSDSchema topSchema) throws MapperException
	{
		this.schema = topSchema;
		
		// record whether this schema has any types derived from other types in it
		recordComplexTypes();
		recordDerivedTypes();
		if (tracing) writeDerivedTypes();
		
		// set up the namespaces (adding the XML Schema instance namespace only if there are derived types)
		NSSet = new NamespaceSet();
		makeNamespaceSet(schema);
	}
	
    private void recordComplexTypes()
    {
    	allComplexTypes = new Hashtable<String,XSDComplexTypeDefinition>();
		for (Iterator<XSDTypeDefinition> types = schema.getTypeDefinitions().iterator(); types.hasNext();)
		{
			XSDTypeDefinition td = types.next();
			trace("type " + td.getName());
			if (td instanceof XSDComplexTypeDefinition)
			{
				XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)td;
				allComplexTypes.put(ctd.getName(), ctd);
				trace("complex type");
			}
		}
    }
    
    /**
     * @param typeName
     * @return the XSD complex type, if there is one
     * This method and the previous one are a workaround for a 
     * strange feature of the EMF XSD package. 
     * When a complex type A extends another complex type B, sometimes
     * (when, I cannot predict) the method A.getBaseType() returns
     * an XSDTypeDefinition with the name of type B, but whose class is XSDSimpleTypeDefinition.
     * Thus I have to note its name, and find a complex type of that name - 
     * which this method does.
     */
    private XSDComplexTypeDefinition getComplexType(String typeName)
    	{return allComplexTypes.get(typeName);}

	
//------------------------------------------------------------------------------------------
//       Setting up the full set of namespaces from all schema documents
//------------------------------------------------------------------------------------------
	
	private void makeNamespaceSet(XSDSchema topSchema) throws MapperException
	{
		NSSet = new NamespaceSet();	
		/* If the top schema has no namespace, do not allow imported schemas 
		 * to use the empty prefix for some actual namespace URI . 
		 * remove this namespace when all imported schemas have been done */
		if (topSchema.getTargetNamespace() == null)
			NSSet.addNamespace(new namespace("","no target namespace"));

		/* XSD recognises a top schema and those it imports or includes,
		 * directly or indirectly, as a single resource set.
		 * Iterate over it , taking namespaces from all of them. */
		ResourceSet schemaSet = topSchema.eResource().getResourceSet();
		for (Iterator<Resource> resources = schemaSet.getResources().iterator(); resources.hasNext();)  
		{
			Resource resource = resources.next();
			if (resource instanceof XSDResourceImpl)
			{
				XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
				XSDSchema oneSchemaDoc =  xsdResource.getSchema();
				addToNamespaceSet(oneSchemaDoc);
			}
		}
		
		// remove this artificial namespace if it is present - reasons above.
		NSSet.removeOneNamespace("no target namespace");
		
		/* only add the W3C schema instance namespace if the schema has some derived types, 
		 * so that the xsi:type attribute might be used. */
		if (schemaHasDerivedTypes())
		{
			namespace instanceNamespace = NSSet.getByURI(XMLUtil.SCHEMAINSTANCEURI);
			// if there is no schema instance namespace, add one with a non-clashing prefix
			if (instanceNamespace == null)
			{
				schemaInstancePrefix = noClash(defaultSchemaInstancePrefix, NSSet);
				NSSet.addNamespace(new namespace(schemaInstancePrefix, XMLUtil.SCHEMAINSTANCEURI));
			}
			// if there is a schema instance namespace, find its prefix, to use when adding the type attribute
			else if (instanceNamespace != null)
			{
				schemaInstancePrefix = instanceNamespace.prefix();
			}			
		}
	}
	
	/**
	 * Make additions to the Namespace set for one schema document, 
	 * in a way that will resolve any namespace prefix clashes between different 
	 * schema documents.
	 * 
	 * (1) If a namespace URI has been encountered before, do nothing; the 
	 * previous prefix will be used for the URI
	 * (2) If a new namespace URI has a prefix that has not been encountered before, 
	 * add it
	 * (3) If a new namespace URI has a prefix that has been encountered before, 
	 * make up a new prefix which does not clash
	 * (4) the empty String '' is a valid prefix for any namespace
	 * (5) '' is the only valid prefix for 'no target namespace' (which we assume 
	 * is encountered first, in the top schema)
	 * If '' is used for 'no target namespace', none of the actual namespaces 
	 * can have the same prefix ''
	 * 
	 * @param oneSchema
	 * @throws MapperException
	 */
	private void addToNamespaceSet(XSDSchema oneSchema) throws MapperException
	{
		Map<String,String> namespaces = oneSchema.getQNamePrefixToNamespaceMap();

		/* If this schema document has a target namespace, then it should declare 
		 * that namespace with a prefix or with the empty prefix */
		String targetNamespace = oneSchema.getTargetNamespace();
		boolean targetNamespaceNotMatched = false;
		if (targetNamespace != null) targetNamespaceNotMatched= true;

		for (Iterator<String> it = namespaces.keySet().iterator();it.hasNext();) 
		{
			String prefix = it.next();
			// for a no-prefix namespace, prefix == null; but the get still works
			String uriString = namespaces.get(prefix);
			if (prefix == null) prefix = "";
			if (targetNamespaceNotMatched)
				targetNamespaceNotMatched = (!(uriString.equals(targetNamespace)));
			// if this namespace URI has already been encountered, do nothing else
			if (NSSet.getByURI(uriString) == null)
			{
				// give it a prefix which does not clash with any previous prefix, including ''
				String actualPrefix = noClash(prefix,NSSet);
				NSSet.addNamespace(new namespace(actualPrefix,uriString));
			}
		}
		
		/*  If no namespace prefix has been allocated in this document for its target namespace ,
		 * and no prefix has been allocated in any previous document, pick a
		 * non-clashing prefix out of the air and allocate it to the target namespace.*/
		if ((targetNamespaceNotMatched) && (NSSet.getByURI(targetNamespace) == null))
		{
			String madeUpPrefix = noClash("madeup",NSSet);
			
			/* the prefix 'xml' must be used for the XML default namespace; 
			 * but do not add it in any case */
			String XMLDefaultNamespace = "http://www.w3.org/XML/1998/namespace";
			if (!targetNamespace.equals(XMLDefaultNamespace)) 
				NSSet.addNamespace(new namespace(madeUpPrefix,targetNamespace));			
		}
	}
	
	/* generate a namespace prefix which does not clash with any previous prefix in the set */
	private String noClash(String prefix, NamespaceSet NSSet)
	{
		String noClash = prefix;
		int index = 1;
		while (NSSet.getByPrefix(noClash) != null)
		{
			noClash = prefix + "_" + index;
			index++;
		}
		return noClash;
	}
	
	/**
	 * @param ed an Element declaration or Attribute declaration
	 * @return the element or attribute name, with a namespace prefix as defined from the whole schema
	 * set by XSDStructure.makeNamespaceSet. 
	 * This method is used in stead of getQName() because getQName seems to return a prefix as in 
	 * the schema holding the declaration, whereas the single prefix I have allocated 
	 * may be different. This ensures just one prefix for every namespace, and 
	 * resolves prefix clashes between different schema documents.
	 * @throws MapperException if the namespace uri is not recognised as having a prefix
	 */
	private String getMappedStructureName(XSDFeature ed) throws MapperException
	{
		String name = ed.getName();
		String namespaceURI = ed.getTargetNamespace();
		/* The element name in instances will have a namespace prefix  if the Element is in a 
		 * target namespace, and either the scope of the declaration is schema-wide 
		 * or the element form is qualified. */
		if ((namespaceURI != null) &&
			((ed.getScope() instanceof XSDSchema)|(ed.getForm() == XSDForm.QUALIFIED_LITERAL)))
		{
			namespace ns = NSSet.getByURI(namespaceURI);
			if (ns == null)throw new MapperException("Cannot find namespace with URI '"
					+ namespaceURI + "'");
			if (!(ns.prefix().equals(""))) name = ns.prefix() + ":" + name;
		}
		return name;
	}
	
	//------------------------------------------------------------------------------------------
	//	                         PropertyValueSupplier interface
	//------------------------------------------------------------------------------------------
	
	/**
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return true if this property value supplier supplies values for the 
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
	 * @param modelClassName
	 * @param modelPropertyName
	 * @return the values supplied by this supplier for the model class and property
	 */
	public String[] propertyValues(String modelClassName, String modelPropertyName)
	{
		String[] vals = {};
		try{
			if ((modelClassName.equals("MappedStructure")) && 
					(modelPropertyName.equals("Top Element Type"))) return topComplexTypes();
			if ((modelClassName.equals("MappedStructure")) && 
					(modelPropertyName.equals("Top Element Name"))) return topElementNames();			
		}
		catch (MapperException ex){System.out.println(ex.getMessage());}
		return vals;
	}
	
	//------------------------------------------------------------------------------------------
	//	                         StructureDefinition interface
	//------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @return an array of the top-level Element names in Element Declarations defined in the schema
	 */
	public String[] topElementNames() throws MapperException
	{
		ArrayList<String> allTopNames = new ArrayList<String>();
		allTopNames.add(""); // the default choice on the menu, before any choice is made, is ""
		for (Iterator<XSDElementDeclaration> names = schema.getElementDeclarations().iterator(); 
		names.hasNext();)
		{
			XSDElementDeclaration ed = names.next();
			allTopNames.add(getMappedStructureName(ed));
		}
		String[] res = new String[allTopNames.size()];
		return allTopNames.toArray(res);
	}
	/**
	 * 
	 * @param type
	 * @return true if type is one of the top types defined in the schema
	 */
	public boolean isTopElementName(String name)
	{
		boolean found = false;
		try{
			String[] tn = topElementNames();
			for (int i = 1; i < tn.length; i++) // tn[0] = "" does not count as a match
				if (tn[i].equals(name)) found = true;			
		}
		catch (MapperException ex){System.out.println(ex.getMessage());}
		return found;
	}
	
	/**
	 * 
	 * @return an array of the top-level complex types defined in the schema
	 */
	public String[] topComplexTypes()
	{
		ArrayList<String> allTypes = new ArrayList<String>();
		allTypes.add(""); // the default choice on the menu, before any choice is made, is ""
		for (Iterator<XSDTypeDefinition> types = schema.getTypeDefinitions().iterator(); 
		types.hasNext();)
		{
			XSDTypeDefinition type = types.next();
			if (type instanceof XSDComplexTypeDefinition) allTypes.add(type.getName());
		}
		String[] res = new String[allTypes.size()];
		return allTypes.toArray(res);
	}
	
	/**
	 * 
	 * @param type
	 * @return true if type is one of the top types defined in the schema
	 */
	public boolean isTopComplexType(String type)
	{
		String[] tt = topComplexTypes();
		boolean found = false;
		for (int i = 1; i < tt.length; i++) // tt[0] = "" does not count as a match
			if (tt[i].equals(type)) found = true;
		return found;
	}
	
	/**
	 * find the Element and Attribute structure of some named top element (which may have a named
	 * complex type, or a locally defined anonymous type), stopping at the
	 * next complex type definitions it refers to
	 * @param String name the name of the element
	 * @return Element the EObject subtree (Element and Attribute EObjects) defined by the name
	 */
	public ElementDef nameStructure(String name) throws MapperException
	{
		trace("Getting structure for element name '" + name + "'");
		ElementDef el = MapperFactory.eINSTANCE.createElementDef();
		el.setName(name);
		if (isTopElementName(name))
		{
			XSDElementDeclaration elDec = null;
			for (Iterator<XSDElementDeclaration> eds = schema.getElementDeclarations().iterator(); 
			eds.hasNext();)
			{
				XSDElementDeclaration ed = eds.next();
				if(getMappedStructureName(ed).equals(name)) elDec = ed;
			}
			extendForElementDeclaration(el,elDec,"");
		}
		else if (name.equals("")) {}
		else {trace("Unexpected name '" + name + "'");}
		return el;		
	}
	
	
	/**
	 * find the Element and Attribute structure of some complex type, stopping at the
	 * next complex type definitions it refers to
	 * @param type the name of the complex type
	 * @return the EObject subtree (Element and Attribute EObjects) defined by the type.
	 * If there are other types which extend or restrict the type, then return the union of
	 * the trees of all those types, and allow an attribute xsi:type on the top element
	 */
	public ElementDef typeStructure(String typeName) throws MapperException
	{
		trace("\nGetting structure for element type '" + typeName + "' and derived types");
		Vector<String> derivedTypes = allDerivedTypes(typeName);
		if (derivedTypes.size() == 1)
		{
			return ownTypeStructure(typeName);
		}
		else if (derivedTypes.size() > 1)
		{
			Vector<ElementDef> derivedTypeStructures = new Vector<ElementDef>();
			for (Iterator<String> it = derivedTypes.iterator();it.hasNext();)
				derivedTypeStructures.add(ownTypeStructure(it.next())); 
			
			ElementDef union = unionOfTypeStructures(derivedTypeStructures);
			
			// because the type has extensions or restrictions, let its top element have an xsi:type attribute
			AttributeDef xsiType = MapperFactory.eINSTANCE.createAttributeDef();
			xsiType.setName(schemaInstancePrefix + ":type");
			xsiType.setType("string");
			// the xsi:type attribute must be made obligatory; if allowed, it always appears on the element,
			xsiType.setMinMultiplicity(MinMult.ONE);
			union.getAttributeDefs().add(xsiType);

			return union;
		}
		else throw new MapperException("Cannot find type '" + typeName + "'");
	}

	
	/**
	 * find the Element and Attribute structure of some complex type, stopping at the
	 * next complex type definitions it refers to
	 * @param type the name of the complex type
	 * @return the EObject subtree (Element and Attribute EObjects) defined by the type itself;
	 * if there are other types that extend or restrict the type, the result is unaffected
	 */
	public ElementDef ownTypeStructure(String type)
	throws MapperException
	{
		trace("Getting structure for element type '" + type + "'");
		ElementDef el = MapperFactory.eINSTANCE.createElementDef();
		if (type == null) return el;
		el.setType(type);
		if (isTopComplexType(type))
		{
			XSDComplexTypeDefinition typeDef = null;
			for (Iterator<XSDTypeDefinition> types = schema.getTypeDefinitions().iterator(); 
			types.hasNext();)
			{
				XSDTypeDefinition td = types.next();
				if(td.getName().equals(type)) typeDef = (XSDComplexTypeDefinition)td;
			}
			extendForComplexType(el,typeDef,"");
			trace("TypeStructure " + type + " " + el.isMixed());
		}
		else if (type.equals("")) {}
		else if (type.equals("string")) {}
		else {trace("Unexpected type '" + type + "'");}
		return el;		
	}
	
	// -----------------------------------------------------------------------------------
	//                               Analysing the schema
	// -----------------------------------------------------------------------------------
	
	/**
	 * Handle an element declaration, which may either invoke a named type 
	 * or define the element structure with an anonymous type
	 * @param el the element whose declaration this is
	 * @param ed the XSD element declaration
	 * @param trail String trace of the recursion for finding out what went wrong
	 */
	private void extendForElementDeclaration(ElementDef el, XSDElementDeclaration ed, String trail)
	throws MapperException
	{
		if (tooDeep(trail)) return;
		/* resolve this Element declaration in case it is a ref to another declaration,
		 * and use the resolved declaration from now on  */
		XSDElementDeclaration resEd = ed.getResolvedElementDeclaration();
		if (resEd == null) resEd = ed; // not sure if this is necessary

		el.setName(getMappedStructureName(resEd));
		trail = extendTrail(trail,"ed(" + resEd.getName() + ")");
		String typeName = "";
		XSDTypeDefinition td = resEd.getTypeDefinition();
		if (td != null)
		{
			typeName = td.getName();
			el.setExpanded(false);
		}
		el.setType(typeName); // set the type name to "" if there is no type attribute
		
		/* the complex type will only be part of the contents immediately expanded
		 * if it is defined locally inside the element; otherwise it is left for expansion later */
		for (Iterator<EObject> it = resEd.eContents().iterator(); it.hasNext();)
		{
			EObject edPart = it.next();
			if (edPart instanceof XSDComplexTypeDefinition)
			{
				XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)edPart;
				typeName = ctd.getName();
				// named complex type; do not expect this case to happen
				if (typeName != null)
					{System.out.println("Named complex type inside element declaration: '" + typeName + "'");}
				// anonymous complex type; keep on expanding the tree
				else if (typeName == null)
				{
					el.setExpanded(true);
					String newTrail = trail;
					extendForComplexType(el,ctd,newTrail);
				}
			}
			else if (edPart instanceof XSDAnnotation) {}
			// ignore identity constraints under element definitions
			else if (edPart instanceof XSDIdentityConstraintDefinition) {}
			// FIXME: we should really work out what to do with simple type definitions here
			else if (edPart instanceof XSDSimpleTypeDefinition) {}
			else unexpectedChild(resEd,edPart,1,trail);
		}		
	}
	
	/**
	 * Extend the tree of Elements and Attributes for a complex type
	 * @param el
	 * @param typeDef
	 * @param trail String trace of the recursion for finding out what went wrong
	 */
	private void extendForComplexType(ElementDef el, XSDComplexTypeDefinition typeDef, String trail)
	throws MapperException
	{
		if (tooDeep(trail)) return;
		
		boolean needToRestrict = false;
		ElementDef newEl = el; // make extensions direct on the tree unless this is a restriction
		ElementDef restrictEl = MapperFactory.eINSTANCE.createElementDef();

		int method = typeDef.getDerivationMethod().getValue();
		trail = extendTrail(trail,"td(" + typeDef.getName() + ":" + XSDDerivationMethod.get(method).getLiteral() + ")");

		/* If this complex type extends another, extend the tree structure for that type 
		 * first, before adding the extra stuff from this extension. */
		if (method == XSDDerivationMethod.EXTENSION)
		{
			// WORKAROUND: getBaseType() sometimes turns complex types into simple types 
			XSDComplexTypeDefinition base = getComplexType(typeDef.getBaseType().getName());
			if (base != null)
			{
				String newTrail = extendTrail(trail, "[" + base.getName() + "]");
				extendForComplexType(newEl,base,newTrail);				
			}
			else if (typeDef.getBaseType() instanceof XSDSimpleTypeDefinition)
			{
				/* FIXME: This treatment of complex types which are extensions of simple types is 
				 * probably over-simplified. The Simple type which is being extended is 
				 * replaced by an 'empty' complex type. This is OK when the simple type
				 * is 'string' because the mapper editor allows you to map string values to 
				 * the text content of any Element, without checking its type.  */
				XSDSimpleTypeDefinition sBase = (XSDSimpleTypeDefinition)typeDef.getBaseType();
				// note that sBase now gets completely ignored
				XSDComplexTypeDefinition newEmpty = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
				String newTrail = extendTrail(trail, "[(ignored)" + sBase.getName() + "]");
				extendForComplexType(newEl,newEmpty,newTrail);				
			}
		}

		/* if this complex type restricts another type (apart from 'anyType'), 
		 * first construct a tree representing the type to be restricted, 
		 * then construct a tree representing the restriction, and then combine them
		 * on the element being extended in the tree.*/
		else if (method == XSDDerivationMethod.RESTRICTION)
		{
			XSDComplexTypeDefinition base = null;
			if (typeDef != null)
			{
				if  (typeDef.getBaseType() == null) base = null;
				else if (typeDef.getBaseType() instanceof XSDComplexTypeDefinition) 
					base = (XSDComplexTypeDefinition)typeDef.getBaseType();
				else base = getComplexType(typeDef.getBaseType().getName());
			}

			// restriction of 'anyType' means just read this type definition; don't try to restrict anything
			if ((base!=null) && !(base.getName().equals("anyType")))
			{
				trail = extendTrail(trail,"[" + base.getName() + "]");
				String newTrail = trail;
				needToRestrict = true;
				extendForComplexType(restrictEl,base,newTrail);
				// the information about the restriction should not be put directly on the element being extended 
				newEl = MapperFactory.eINSTANCE.createElementDef();				
			}
		}

		/* make the extensions directly on newEl = el if this type is not a restriction of another type; 
		 * or if it is a restriction, define in newEl what the restrictions are . */
		for (Iterator<EObject> it = typeDef.eContents().iterator(); it.hasNext();)
		{
			EObject typePart = it.next();
			String newTrail = trail;
			if (typePart instanceof XSDParticle)
			{
				extendForParticle(newEl, (XSDParticle)typePart,newTrail);
			}
			else if (typePart instanceof XSDAttributeUse)
			{
				extendForAttributeUse(newEl,(XSDAttributeUse)typePart,newTrail);
			}
			else if (typePart instanceof XSDAttributeGroupDefinition)
			{
				extendForAttributeGroupDefinition(newEl,(XSDAttributeGroupDefinition)typePart,newTrail);
			}
			else if (typePart instanceof XSDWildcard)
			{
				trace("Wild card in type '" + typeDef.getName() + "' at trail '"
						+ trail + "'");
			}
			else if (typePart instanceof XSDAnnotation)
			{} // nothing to do
			else if (typePart instanceof XSDTypeDefinition)
			{} // the extension cases dealt with above; nothing to do
			else unexpectedChild(typeDef,typePart,2,trail);
		}
		
		/* if this type is a restriction of another type, add the type to be restricted,
		 * making the appropriate restrictions */
		if (needToRestrict) makeRestriction(el,restrictEl,newEl,trail);
		
		/* record the 'mixed' property of the complex type, so that for V3 data types
		 * (where the Ecore class model is derived from the schema structure)
		 * the class derived from a mixed element can be given an extra 'textContent' attribute */
		el.setIsMixed(typeDef.isMixed());
		trace("type " + typeDef.getName() + "; mixed = " + typeDef.isMixed());
	}
	
	
	/**
	 * Extend the tree from element el, using the structure of subtree
	 * restrictEl, except where it is restricted by newEl.
	 * 
	 * This uses a simple override by name, at the level of the immediate 
	 * children of the element, and reports if it was unable to make any overrides at all.
	 * 
	 * @param el the element in the tree being constructed
	 * @param restrictEl the tree structure which needs to be added with restrictions
	 * @param newEl the tree structure which defines the restrictions
	 * @param trail String trace of the recursion for finding out what went wrong
	 */
	private void makeRestriction(ElementDef el, ElementDef restrictEl, ElementDef newEl, String trail)
	{
		if (tooDeep(trail)) return;

		/* extend the element by the restricted child attributes, using an intermediate 
		 * Vector to avoid corrupting the source EList */
		Vector<AttributeDef> ats = new Vector<AttributeDef>();
		for (Iterator<AttributeDef> it = restrictEl.getAttributeDefs().iterator();it.hasNext();)
		{
			AttributeDef mightBeRestricted = it.next();
			AttributeDef mightRestrict = newEl.getNamedAttribute(mightBeRestricted.getName());
			/* If the restricting structure has an attribute of the right name, use
			 * it in stead of the attribute from the structure being restricted. */
			if (mightRestrict == null) ats.add(mightBeRestricted);
			else if (mightRestrict != null) 
			{
				// if the restricting type prohibits this attribute, do not add it
				if (!mightRestrict.useIsProhibited()) ats.add(mightRestrict);
			}
		}
		for (Iterator<AttributeDef> it = ats.iterator();it.hasNext();)
			{el.getAttributeDefs().add(it.next());}

		/* extend the element by the restricted child elements, using an intermediate 
		 * Vector to avoid corrupting the source EList */
		Vector<ElementDef> els = new Vector<ElementDef>();
		for (Iterator<ElementDef> it = restrictEl.getChildElements().iterator();it.hasNext();)
		{
			ElementDef mightBeRestricted = it.next();
			ElementDef mightRestrict = newEl.getNamedChildElement(mightBeRestricted.getName());
			/* If the restricting structure has an element of the right name, use
			 * it in stead of the element from the structure being restricted. */
			if (mightRestrict == null) els.add(mightBeRestricted);
			else if (mightRestrict != null) 
			{
				// if the restricting type prohibits this element (maxOccurs = 0), do not add it
				if (!mightRestrict.useIsProhibited()) els.add(mightRestrict);
			}
		}
		for (Iterator<ElementDef> it = els.iterator();it.hasNext();)
			{el.getChildElements().add(it.next());}

		/* System.out.println("Made " + restrictions + " restrictions on " 
				+ features + " features in element at trail '" + trail + "'"); */

		// pass XML schema 'mixed type' information through a restriction
		el.setIsMixed(restrictEl.isMixed());
	}

	/**
	 * 
	 * @param el
	 * @param attGroup
	 * @param trail String trace of the recursion for finding out what went wrong
	 */
	private void extendForAttributeGroupDefinition(ElementDef el, XSDAttributeGroupDefinition attGroup, String trail)
	throws MapperException
	{
		if (tooDeep(trail)) return;
		// attribute group references need to be resolved
		XSDAttributeGroupDefinition expandable = attGroup;
		if (attGroup.isAttributeGroupDefinitionReference())
		{
			trail = extendTrail(trail , "/resolveAG");
			trace(trail);
			expandable = attGroup.getResolvedAttributeGroupDefinition();
		}
		
		trail = extendTrail(trail,"agr(" + expandable.getName() + ")");

		for (Iterator<EObject> it = expandable.eContents().iterator(); it.hasNext();)
		{
			EObject groupPart = it.next();
			if (groupPart instanceof XSDAttributeUse)
			{
				String newTrail = trail;
				extendForAttributeUse(el, (XSDAttributeUse)groupPart,newTrail);
			}
			else if (groupPart instanceof XSDAnnotation) {} // silently ignore annotations
			else unexpectedChild(attGroup,groupPart,3,trail);
		}		
	}

	/**
	 * 
	 * @param el
	 * @param attUse
	 * @param trail String trace of the recursion for finding out what went wrong
	 */
	private void extendForAttributeUse(ElementDef el, XSDAttributeUse attUse, String trail)
	throws MapperException
	{
		if (tooDeep(trail)) return;
		// no further recursion so the next statement is not very useful, unless we need traces in this method
		trail = extendTrail(trail,"au(" + attUse.getUse().getName() + ")");
		boolean isRequired = attUse.isRequired();
		XSDAttributeDeclaration attDecl = attUse.getAttributeDeclaration();
		
		AttributeDef att = MapperFactory.eINSTANCE.createAttributeDef();
		if (attUse.getConstraint()!= null)
		{
			//Convert the default or fixed value to a String
			String stringValue = "";
			Object value = attUse.getValue();
			if (value == null) {stringValue = "null";}
			else
			{
				if (value instanceof String) {stringValue = (String)value;}
				else if (value instanceof Boolean) {stringValue = value.toString();}
				else if (value instanceof Integer) {stringValue = value.toString();}
				else if (value instanceof java.math.BigDecimal) 
					{stringValue = ((java.math.BigDecimal)value).toString();}
				else System.out.println("Cannot yet convert attribute value of class " + value.getClass().getName() + " to a String");				
			}

			if (attUse.getConstraint().getValue() == XSDConstraint.DEFAULT)
				att.setDefaultValue(stringValue);
			if (attUse.getConstraint().getValue() == XSDConstraint.FIXED)
				att.setFixedValue(stringValue);
		}
		att.setName(getMappedStructureName(attDecl));
		att.setType(getAttributeType(attDecl)); 
		att.setMinMultiplicity(MinMult.get(isRequired));
		// record if the attribute is to be prohibited
		if (attUse.getUse().getValue() == XSDAttributeUseCategory.PROHIBITED) att.setUseIsProhibited(true);
		el.getAttributeDefs().add(att);
	}
	
	private String getAttributeType(XSDAttributeDeclaration attDecl)
	{
		return attDecl.getTypeDefinition().getName();
	}

	/**
	 * Handle XSD particles encountered in schemas. From W3C schema recommendation:
	 * 
	 * A particle is a term in the grammar for element content, consisting of either 
	 * an element declaration, a wildcard or a model group, 
	 * together with occurrence constraints (= minOccurs and maxOccurs)
	 * @param el the Element whose structure is being determined
	 * @param typePart the XSD particle
	 * @param refCount the depth of cross-references that have been followed in model groups; limit to 3
	 * @param trail String trace of the recursion for finding out what went wrong
	 */
	private void extendForParticle(ElementDef el, XSDParticle typePart, String trail)
	throws MapperException
	{
		if (tooDeep(trail)) return;
		Integer min = new Integer(typePart.getMinOccurs());  // 0 or 1
		Integer max = new Integer(typePart.getMaxOccurs());   // 1 or -1 (for unbounded) or 0 (prohibited)
		
		// nesting of prohibited nodes is not useful; cut off recursion at any particle inside a maxOccurs = 0 particle
		if ((trail.contains("pt(0:0"))|((trail.contains("pt(1:0")))) return;
		
		trail = extendTrail(trail,"pt(" + min + ":" + max + ")");
		int contained = 0;
		for (Iterator<EObject> iu = typePart.eContents().iterator(); iu.hasNext();)
		{
			String newTrail = trail;
			EObject pChild = iu.next();
			contained++;
			if ((pChild instanceof XSDModelGroup)|(pChild instanceof XSDModelGroupDefinition))
			{
				XSDModelGroup modelGroup = null;
				if (pChild instanceof XSDModelGroup) 
					modelGroup = (XSDModelGroup)pChild;
				else if (pChild instanceof XSDModelGroupDefinition)
				{
					XSDModelGroupDefinition mgd = (XSDModelGroupDefinition)pChild;
					modelGroup = mgd.getModelGroup();
					if (modelGroup == null)
						modelGroup = mgd.getResolvedModelGroupDefinition().getModelGroup();					
				}
				if (modelGroup == null)
				{
					System.out.println("Null model group at trail " + trail);
					return;
				}

				extendForModelGroup(el, modelGroup,newTrail);
				// apply the multiplicities to the added elements
				for (Iterator<ElementDef> it = el.getChildElements().iterator(); it.hasNext();)
				{
					ElementDef child = it.next();
					if (min == 0) child.setMinMultiplicity(MinMult.ZERO);
					if (max == -1) child.setMaxMultiplicity(MaxMult.UNBOUNDED);
				}
				// apply the min multiplicities to the added attributes
				for (Iterator<AttributeDef> it = el.getAttributeDefs().iterator(); it.hasNext();)
				{
					AttributeDef at = it.next();
					if (min == 0) at.setMinMultiplicity(MinMult.ZERO);
				}
			}
			else if (pChild instanceof XSDElementDeclaration)
			{
				XSDElementDeclaration edd = (XSDElementDeclaration)pChild;
				ElementDef newEl = MapperFactory.eINSTANCE.createElementDef();
				newEl.setMinMultiplicity(MinMult.get(min.toString()));
				// maxOccurs = 0 means use is prohibited
				if (max == 0) newEl.setUseIsProhibited(true);
				else newEl.setMaxMultiplicity(MaxMult.get(max.toString()));		

				extendForElementDeclaration(newEl,edd,newTrail);
				el.getChildElements().add(newEl);
			}
			else if (pChild instanceof XSDWildcard)
			{
				trace("Wild card in particle at trail '"
						+ trail + "'");
			}
			else unexpectedChild(typePart,pChild,4,trail);
		}
		if (!(contained == 1)) trace("XSD Particle with " + contained 
				+ " contents at trail " + trail + "; unexpected");
	}

	/**
	 * Handle model groups encountered in schemas. From the W3C recommendation:
	 * 
	 * A model group is a constraint in the form of a grammar fragment that 
	 * applies to lists of element information items. 
	 * It consists of a list of particles, i.e. element declarations, wildcards and model groups. 
	 * There are three varieties of model group:
	 * Sequence: <xs:sequence>
	 * Conjunction: <xs:all>
	 * Disjunction: <xs:choice>
	 * @param el the Element whose structure is being determined
	 * @param modelGroup the XSD model group
	 * @param refCount the depth of cross-references that have been followed in model groups; limit to 3
	 * @param trail String trace of the recursion for finding out what went wrong
	 */
	private void extendForModelGroup(ElementDef el, XSDModelGroup modelGroup, String trail)
	throws MapperException
	{
		if (tooDeep(trail)) {trace("trail '" + trail + "' too deep");return;}
		if (modelGroup.getCompositor() == null) 
		{
			System.out.println("Null compositor for model group at trail " + trail);
			return;
		}
		String compositor = modelGroup.getCompositor().getName();
		trail = extendTrail(trail,"mg(" + compositor + ")");
		boolean isChoice = (compositor.equals("choice"));
		/* for 'sequence' and 'all' model groups, simply add the different subtrees
		 * occurring in the group */
		if (!isChoice) for (Iterator<EObject> iv = modelGroup.eContents().iterator(); iv.hasNext();)
		{
			String newTrail = trail;
			EObject part2 = iv.next();
			if (part2 instanceof XSDParticle)
			{
				// count2++;
				extendForParticle(el,(XSDParticle)part2,newTrail);
			}
			else unexpectedChild(modelGroup,part2,5,trail);
		}
		/* this warning sometimes appeared in the V3 data types schema */
		// if (count2 == 0) System.out.println(compositor + " with no contained particles in Element at trail " + trail);

		/* 'choice' model groups; cannot simply add nodes to the subtree when the same 
		 * element name and type appears in more than one choice. Must add nodes without repetition. */
		else if (isChoice) 
		{
			//  to store subtrees without duplication
			Hashtable<String,ElementDef> choiceTrees = new Hashtable<String,ElementDef>();
			Hashtable<String,AttributeDef> choiceAtts = new Hashtable<String,AttributeDef>();

			for (Iterator<EObject> iv = modelGroup.eContents().iterator(); iv.hasNext();)
			{
				ElementDef newEl = MapperFactory.eINSTANCE.createElementDef();				
				String newTrail = trail;
				EObject part = iv.next();
				if (part instanceof XSDParticle)
				{
					extendForParticle(newEl,(XSDParticle)part,newTrail);
					saveSubTreeStructures(newEl, choiceTrees);
					saveAttributes(newEl, choiceAtts);
				}
				else unexpectedChild(modelGroup,part,6,trail);
			}
			
			// add the non-duplicated subtrees to the tree
			for (Enumeration<ElementDef> en = choiceTrees.elements(); en.hasMoreElements();)
				el.getChildElements().add(en.nextElement());
			for (Enumeration<AttributeDef> en = choiceAtts.elements();en.hasMoreElements();)
				el.getAttributeDefs().add(en.nextElement());
			
			/* if there is more than one choice with different subtrees, they must have MinMult = 0.
			 * This implementation is a quick fudge. 
			 * The proper implementation should look at each top element 
			 * and ask if it appears in every choice; only then might it have MinMult = 1.
			 * This just sets MinMult zero anyway ,assuming the choice makes everything optional. */
			for (Iterator<ElementDef> it = el.getChildElements().iterator();it.hasNext();)
				it.next().setMinMultiplicity(MinMult.ZERO);
			for (Iterator<AttributeDef> it = el.getAttributeDefs().iterator();it.hasNext();)
				it.next().setMinMultiplicity(MinMult.ZERO);
		}
	}
	
	//----------------------------------------------------------------------------------------
	//                                   other stuff
	//----------------------------------------------------------------------------------------

	/**
	 * save all the subtrees, merging duplicates with the same name and type
	 * @param newEl element which has all the subtrees for one choice attached
	 * @param choiceTrees Hashtable of subtrees to be built up
	 */
	private void saveSubTreeStructures(ElementDef el, Hashtable<String,ElementDef> choiceTrees)
	{
		for (Iterator<ElementDef> it = el.getChildElements().iterator();it.hasNext();)
		{
			ElementDef child = it.next();
			String key = child.getName() + "|" + child.getType();
			ElementDef previous = choiceTrees.get(key);
			child = mergeSubtrees(child, previous); // OK if previous == null
			choiceTrees.put(key, child);
		}		
	}
	
	/**
	 * merge two subtrees that have been found under one choice node - 
	 * assuming that they belong to different branches of the choice.
	 * 
	 * Fixed and default values are only carried through if the two choices agree.
	 * 
	 * @param el a subtree with given name and type found under the current choice
	 * @param previous the merge of subtrees with this name and type found under previous
	 * choices, or null if none have been found yet.
	 * @return the merge
	 */
	private ElementDef mergeSubtrees(ElementDef el, ElementDef previous)
	{
		if (previous == null) return el;

		/* merge the property values of this and the previous element for the choice  */
		ElementDef newEl = MapperFactory.eINSTANCE.createElementDef();
		newEl.setName(el.getName()); // must be equal to previous
		newEl.setType(el.getType()); // must be equal to previous
		newEl.setExpanded(el.isExpanded()|previous.isExpanded()); 
		newEl.setFixedValue(mergeFixedValues(el.getFixedValue(),previous.getFixedValue()));
		newEl.setDefaultValue(mergeFixedValues(el.getDefaultValue(),previous.getDefaultValue())); 

		// if either el or previous departs from (min,max) = (1,1), the result must depart
		newEl.setMinMultiplicity(el.getMinMultiplicity());
		if (previous.getMinMultiplicity() == MinMult.ZERO) el.setMinMultiplicity(MinMult.ZERO);
		newEl.setMaxMultiplicity(el.getMaxMultiplicity());
		if (previous.getMaxMultiplicity() == MaxMult.UNBOUNDED) el.setMaxMultiplicity(MaxMult.UNBOUNDED);
		
		// merge the child elements; first those from el, which may or may not be matched
		for (Iterator<ElementDef> it = el.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef child = it.next();
			ElementDef previousChild = childWithNameAndType(previous, child.getName(),child.getType());
			if (previousChild == null) 
			{
				// as the child does not occur in one of the choices, it is optional in the tree
				child.setMinMultiplicity(MinMult.ZERO);
				newEl.getChildElements().add(child);
			}
			else newEl.getChildElements().add(mergeSubtrees(child,previousChild));
		}
		// then child elements from previous which are not children of el
		for (Iterator<ElementDef> it = previous.getChildElements().iterator(); it.hasNext();)
		{
			ElementDef prev = it.next();
			if (childWithNameAndType(el,prev.getName(),prev.getType()) == null)
			{
				// as the child does not occur in one of the choices, it is optional in the tree
				prev.setMinMultiplicity(MinMult.ZERO);
				newEl.getChildElements().add(prev);				
			}
		}
		
		// merge the attributes. First those in el which may or may not be matched in previous
		for (Iterator<AttributeDef> it = el.getAttributeDefs().iterator(); it.hasNext();)
		{
			AttributeDef at = it.next();
			AttributeDef atp = previous.getNamedAttribute(at.getName());
			/* if the attribute does not occur in one of the choices, 
			 * it is optional and has no fixed or default value */
			if (atp == null) 
			{
				at.setFixedValue(null); // when the attribute does not occur, it cannot have a fixed or default value
				at.setDefaultValue(null);
				at.setMinMultiplicity(MinMult.ZERO);
			}
			else
			{
				// store fixed and default values only if the two choices are equal
				at.setFixedValue(mergeFixedValues(at.getFixedValue(),atp.getFixedValue()));
				at.setDefaultValue(mergeFixedValues(at.getDefaultValue(),atp.getDefaultValue()));
				if (atp.getMinMultiplicity() == MinMult.ZERO) at.setMinMultiplicity(MinMult.ZERO);
			}
			newEl.getAttributeDefs().add(at);			
		}
		// next the attributes in previous which are not matched in el
		for (Iterator<AttributeDef> it = previous.getAttributeDefs().iterator(); it.hasNext();)
		{
			AttributeDef atp = it.next();
			AttributeDef at = el.getNamedAttribute(atp.getName());
			if (at == null)
			{
				atp.setFixedValue(null); // when the attribute does not occur, it cannot have a fixed or default value
				atp.setDefaultValue(null);
				// if the attribute does not occur in one of the choices, it is optional
				atp.setMinMultiplicity(MinMult.ZERO);
				newEl.getAttributeDefs().add(atp);
			}
		}
		return el;
	}
	
	/**
	 * Merge fixed or default values arising from two different choices, in the same attribute
	 * in different choices.
	 * If either choice has no fixed value, the structure tree has no fixed value (null). 
	 * If the choices provide different fixed values, there is no fixed value.
	 * Only if both choices define the same value should the tree record a fixed value.
	 * @param value1
	 * @param value2
	 * @return
	 */
	private String mergeFixedValues(String value1, String value2)
	{
		String res = null;
		if (value1 == null) return res;
		if (value2 == null) return res;
		if (value1.equals(value2)) res = value1;
		return res;
	}
	
	private ElementDef childWithNameAndType(ElementDef el,String name, String type)
	{
		ElementDef child = el.getNamedChildElement(type);
		if ((child != null) && (child.getType().equals(type))) return child;
		return null;
	}

	/**
	 * save all the attributes by name alone - more than one attribute of the same
	 * name but different types is not allowed (?)
	 * @param newEl
	 * @param choiceTrees
	 */
	private void saveAttributes(ElementDef el, Hashtable<String,AttributeDef> choiceAtts)
	{
		for (Iterator<AttributeDef> it = el.getAttributeDefs().iterator();it.hasNext();)
		{
			AttributeDef at = it.next();
			AttributeDef atp = choiceAtts.get(at.getName());
			// if the attribute is new, add it
			if (atp == null) choiceAtts.put(at.getName(), at);
			// if it exists already with MinMult zero, keep MinMult zero
			else if (atp != null)
			{
				if (atp.getMinMultiplicity() == MinMult.ZERO)
					at.setMinMultiplicity(MinMult.ZERO);
				choiceAtts.put(at.getName(), at);
			}
		}
	}
	
	/**
	 * message if some child class is unexpected in the XSD package;
	 * write out the classes (and names if possible) of the whole subtree
	 * @param parent
	 * @param child
	 */
	private void unexpectedChild(EObject parent, EObject child, int type, String trail)
	{
		System.out.println("New subtree under " + description(parent) 
				+ " at call " + type + " with trail '" + trail + "'");
		showDescendants(child,0);
	}
	
	// basic description of an EObject - class name, and 'getName()' if defined
	private String description(EObject eo)
	{
		String cName = null;
		StringTokenizer cst = new StringTokenizer(eo.getClass().getName(),".");
		while (cst.hasMoreTokens()) {cName = cst.nextToken();}
		if (eo instanceof XSDNamedComponent)  
			{cName = cName + " '" + ((XSDNamedComponent)eo).getName() + "'";} 
		return cName;
	}
	
	/**
	 * write out a basic description of the subtree of an EObject
	 * @param eo the EObject
	 * @param level keeps track of the depth in the tree; truncate
	 */
	private void showDescendants(EObject eo, int level)
	{
		if (level > 1) return;
		System.out.println(level + ": " + description(eo));
		for (Iterator<EObject> it = eo.eContents().iterator(); it.hasNext();)
		 {showDescendants(it.next(),level + 1);}
	}
	
	//-------------------------------------------------------------------------------
	//        Static method to return a top XSDSchema object
	//-------------------------------------------------------------------------------

	/**
	 * Open a file as a XSD model
	 * @param uri the URI of the XSD model file
	 * @return the XSDSchema (EObject subclass) root of the model; or null if failed to open
	 */
	public static XSDSchema getXSDRoot(URI uri)
	{
		XSDSchema theSchema = null;
	    ResourceSet resourceSet = new ResourceSetImpl();

		// load the main schema file into the resourceSet.			
		//XSDResourceImpl xsdRes = 
			resourceSet.getResource(uri, true);
		// getResources() returns an iterator over all the resources, therefore, the main resource
		// and those that have been included, imported, or redefined.
		for (Iterator<Resource> resources = resourceSet.getResources().iterator(); 
		    resources.hasNext(); /* no-op */)  
		{
		    // Return the first schema object found, which is the main schema 
		    //   loaded from the provided schemaURL
		    Resource resource = resources.next();
		    if ((resource instanceof XSDResourceImpl) && (theSchema == null))
		    {
		        XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
		        theSchema =  xsdResource.getSchema();
		    }
		}
		return theSchema;
	}

         /** key = string form of a non-definite path;
         *  value = the shortest compatible definite path  */
         Hashtable<String, Xpth> shortestPaths = new Hashtable<String, Xpth>();
         
         //------------------------------------------------------------------------------------------------------
         //                   Dealing with xsi:type; extended types
         //------------------------------------------------------------------------------------------------------
         
         /*  If the schema  has complex types that are extensions or restrictions on other types,
          * then instances may use xsi:type on elements to say what types the elements are.
          * In this case the tree of allowed structure is the union of the structure trees
          * of the base type and all its extensions */
         
         /**
          * key = a type name;
          * value = names of all the types derived directly from it, by extension or restriction
          */
         private Hashtable<String,Vector<String>> directDerivedTypes = new Hashtable<String,Vector<String>>();
         
         /**
          * @return true if this schema has any complex types derived from others by extension or restriction
          */
         public boolean schemaHasDerivedTypes() {return (directDerivedTypes.size() > 0);}
         
         /**
          * set up the Hashtable directDerivedTypes, recording which types are derived directly 
          * from others.
          * Note the workaround below for a strange feature of the XSD package
          */
         private void recordDerivedTypes()
         {
 			for (Iterator<XSDTypeDefinition> types = schema.getTypeDefinitions().iterator(); 
			types.hasNext();)
			{
				XSDTypeDefinition td = types.next();
				if (td instanceof XSDComplexTypeDefinition)
				{
					XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)td;
					String derivedName = ctd.getName();
					int method = ctd.getDerivationMethod().getValue();
					if ((method == XSDDerivationMethod.EXTENSION)|(method == XSDDerivationMethod.RESTRICTION))
					{
						String baseName = ctd.getBaseType().getName();
						// WORKAROUND; getBaseType() sometimes wrongly returns an XSDSimpleType with the name of the XSDComplexType
						XSDComplexTypeDefinition base = getComplexType(baseName);
						if (base != null)
						{
							Vector<String> derivedNames = directDerivedTypes.get(baseName);
							if (derivedNames == null) derivedNames = new Vector<String>();
							derivedNames.add(derivedName);
							directDerivedTypes.put(baseName,derivedNames);
						}
					}
				}
			}        	 
         }
         
         /**
          * basic diagnostic write of derived types
          */
         private void writeDerivedTypes()
         {
        	 for (Enumeration<String> en = directDerivedTypes.keys(); en.hasMoreElements();)
        	 {
        		 String typeLine = en.nextElement();
        		 Vector<String> derived = directDerivedTypes.get(typeLine);
        		 typeLine = typeLine + "\t";
        		 for (Iterator<String> it = derived.iterator();it.hasNext();)
        		 {
        			 typeLine = typeLine + it.next() + ",";
        		 }
        		 System.out.println(typeLine);
        	 }
         }
         
         /**
          * @param typeName the name of a complex type
          * @return a Vector starting with that type, and containing the names of all types
          * derived from it, directly or indirectly, such that any type always precedes
          * the types derived from it.
          */
         private Vector<String> allDerivedTypes(String typeName)
         {
        	 Vector<String> types = new Vector<String>();
        	 types.add(typeName);
        	 addDerivedTypes(types, typeName,0);
        	 return types;
         }
         
         /**
          * @param types a Vector of type names
          * @param typeName a type name
          * Add to the Vector all types derived directly from the type,
          * and recursively add those derived indirectly
          */
         private void addDerivedTypes(Vector<String> types,String typeName, int depth)
         {
        	 int MAXDEPTH = 20; // some silly schemas self-recurse
        	 if (typeName != null)
        	 {
            	 Vector<String> directDerived = directDerivedTypes.get(typeName);
            	 if (directDerived != null)
            		 for (Iterator<String> it = directDerived.iterator();it.hasNext();)
            		 {
            			 String dType = it.next();
            			 types.add(dType); // add a type derived directly from the type
            			 if (depth < MAXDEPTH) // has been known to blow up
            				 addDerivedTypes(types,dType,depth + 1); // recursively add those derived from the derived type
            		 }       		 
        	 }
         }
         
	     /**
	      * @param derivedTypeStructures Vector of ElementDefs for a type, and all types
	      * derived from it by extension or restriction. Any type comes before the types
	      * derived from it.
	      * @return the union of the ElementDef trees, preserving order of descendant elements,
	      * and putting elements from extended types after elements from their base types
	      */
         private ElementDef unionOfTypeStructures(Vector<ElementDef>derivedTypeStructures)
	     {
        	Hashtable<String,String> usedChildElementNames = new Hashtable<String,String>();
        	Hashtable<String,String> usedAttributeNames = new Hashtable<String,String>();

        	// copy properties of any element from the base type that introduces it
        	ElementDef el = MapperFactory.eINSTANCE.createElementDef();
        	ElementDef first = derivedTypeStructures.get(0);
			el.setType(first.getType());
			el.setName(first.getName());
			el.setMinMultiplicity(first.getMinMultiplicity());
			el.setMaxMultiplicity(first.getMaxMultiplicity());
			el.setDefaultValue(first.getDefaultValue());
			el.setFixedValue(first.getFixedValue());

			/* iterate over all derived types and all their child Elements, 
			 * addressing each child element name only once */
			boolean isBaseType = true; // remains true only for the first (base) structure
			for (Iterator<ElementDef> ip = derivedTypeStructures.iterator();ip.hasNext();)
			{
				ElementDef parent = ip.next();
				// if any extended type allows unbounded max multiplicity,allow it for the union
				if (parent.getMaxMultiplicity() == MaxMult.UNBOUNDED) el.setMaxMultiplicity(MaxMult.UNBOUNDED);

				for (Iterator <ElementDef> ic = parent.getChildElements().iterator();ic.hasNext();)
				{
					String cName = ic.next().getName();
					if (usedChildElementNames.get(cName) == null) // first encounter of this element name
					{
						usedChildElementNames.put(cName, "1");
						// pick up child elements of this name from all parents, and put them in a Vector
						Vector<ElementDef> namedChildren = new Vector<ElementDef>();
						for (Iterator<ElementDef> ipp = derivedTypeStructures.iterator();ipp.hasNext();)
						{
							ElementDef par = ipp.next();
							for (Iterator <ElementDef> icc = par.getChildElements().iterator();icc.hasNext();)
							{
								ElementDef ch = icc.next();
								if (ch.getName().equals(cName)) namedChildren.add(ch);
							}
						}
						// make the union of type structures for all child elements with a given name
						ElementDef childStruct = unionOfTypeStructures(namedChildren);
						// if the name does not come from the base type, make the ElementDef optional
						if (!isBaseType) childStruct.setMinMultiplicity(MinMult.ZERO);
						el.getChildElements().add(childStruct);
						
					} // end of first encounter with a child element name
				} // end of loop over child elements of derived type structures
				
				for (Iterator<AttributeDef> ia = parent.getAttributeDefs().iterator();ia.hasNext();)
				{
					AttributeDef ad = ia.next();
					String attName = ad.getName();
					/* if this is the first encounter with this attribute name,  don't bother
					 * to find all later occurrences of the attribute name in all derived types;
					 * take the properties from the first occurrence. Incorrect? */
					if (usedAttributeNames.get(attName) == null)
					{
						usedAttributeNames.put(attName,"1");
			        	AttributeDef at = MapperFactory.eINSTANCE.createAttributeDef();
			        	at.setName(attName);
			        	at.setType(ad.getType());
			        	at.setMinMultiplicity(ad.getMinMultiplicity());
			        	/* if an attribute does not appear in the base type, make it optional 
			        	 * (restricted types cannot make an obligatory attribute optional) */
			        	if (!isBaseType) at.setMinMultiplicity(MinMult.ZERO);
			        	at.setDefaultValue(ad.getDefaultValue());
			        	at.setFixedValue(ad.getFixedValue());
			        	el.getAttributeDefs().add(at);
			        	
					} // end of first encounter with an attribute of given name
				} // end of loop over attributes of derived type ElementDefs
				
				// so that elements and attributes first encountered in non-base types can min multiplicity = 0
				isBaseType = false; 
				
			} // end of loop over derived type structures
			
			return el;	    	 
	     }
         
         //------------------------------------------------------------------------------------------------------
         //                             Getting Enumeration values of simple types
         //------------------------------------------------------------------------------------------------------

         /**
          * @param simpleTypeName the name of a simple type
          * @return a list of its enumerated values, if it has any; otherwise an empty list
          * @throws MapperException if there is no such simple type
          */
         public Vector<String> getSimpleTypeEnumeratedValues(String simpleTypeName) throws MapperException
         {
        	 Vector<String> values = new Vector<String>();
        	 XSDSimpleTypeDefinition theType = null;
        	 EList<XSDTypeDefinition> types = schema.getTypeDefinitions();
        	 
        	 for (Iterator<XSDTypeDefinition> it = types.iterator();it.hasNext();)
        	 {
        		 XSDTypeDefinition next = it.next();
        		 if (theType == null)
        		 {
            		 if ((next instanceof XSDSimpleTypeDefinition) && (next.getName().equals(simpleTypeName)))
            			 theType = (XSDSimpleTypeDefinition)next;
            		 if ((theType != null) && (theType.getEnumerationFacets() != null))
            		 {
                		 int size = theType.getEnumerationFacets().size();
                		 if (size > 0)
                		 {
                			 for (Iterator<XSDEnumerationFacet> iu = theType.getEnumerationFacets().iterator();iu.hasNext();)
                			 {
                				 XSDEnumerationFacet facet = iu.next();
                				 EList<Object> valueSet = facet.getValue();
                				 if ((valueSet != null) && (valueSet.size() == 1))
                				 {
                					 Object value = valueSet.get(0);
                					 if (value instanceof String) values.add((String)value);
                				 }
                			 }
                		 }
            		 }        			 
        		 }
        	 }        	 
        	 if (theType == null) throw new MapperException("Simple type '" + simpleTypeName + "' not found.");        	 
        	 return values;
         }
         
         //------------------------------------------------------------------------------------------------------
         //                            Bits and bobs
         //------------------------------------------------------------------------------------------------------
         
         protected void message(String s) {System.out.println(s);}
         

}
