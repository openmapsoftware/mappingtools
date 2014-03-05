package com.openMap1.mapper.converters;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.ClassModelMaker;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.XSLOutputFile;
import com.openMap1.mapper.writer.TemplateFilter;
import com.openMap1.mapper.MappedStructure;

/**
 * This class converts in both directions between correct HL7 CDA
 * and a modified CDA, which has modified class and association names
 * and which has no <templateId> elements.
 * 
 * It applies tag name changes based on the following annotations in the template-constrained Ecore model:
 * 
 * (a) for nodes associated with templates:
 * 
  		on EReference nodes, with names like 'component3_Payers' modified by template names, and annotations :
  
        <eAnnotations source="urn:hl7-org:v3/mif2">
          <details key="CDA_Name" value="component"/>
        </eAnnotations>
        
      where the target class (or if it is an ActRelationship, its Act child) has an annotation defining the templates: 
      
      <eAnnotations source="urn:hl7-org:v3/mif2">
        <details key="RIM Class" value="Act"/>
        <details key="CDA_Name" value="Section"/>
        <details key="template" value="2.16.840.1.113883.10.20.1.9"/>
      </eAnnotations>
      
      For the in-transform, it checks the CDA tag name and the set of template ids under the node or its child.
      if these both match, it changes the CDA tag name to the EReference name, and removes the <templateId> elements.
      
      For the out=-transform, it checks only the (constrained) tag name, presumed to be unique; 
      if it matches, it converts it  to the CDA tag name and adds the <templateId> elements.
      
      (b) for other nodes, not associated with templates:
      
      If there is an EReference  with name 'measureSection' with an annotation

        <eAnnotations source="urn:hl7-org:v3/mif2">
          <details key="CDA_Name" value="section"/>
          <details key="XPathCondition" value="[code/@code='55186-1']"
        </eAnnotations>
        
      For the in-transform, it looks for nodes with tag name 'section' which also
      satisfy the XPath condition, and changes their tag names to 'measureSection'
      
      For the out-transform, it look for nodes with tag name 'measureSection' (assumed to be unique)
      and changes them to 'section'.
 * 
 * 
 * 
 * @author robert
 *
 */
public class CDAConverter extends AbstractMapperWrapper implements MapperWrapper{
	
	
	/**
	 * set true to make simplified messages stay in the V3 namespace.
	 */
	public static boolean SIMPLE_MESSAGE_IN_V3_NAMESPACE = true;

	protected boolean tracing() {return false;}
	
	private String topElementName() 
	{
		return "ClinicalDocument";
	}
	
	private Vector<TagNameConversion> tagNameConversions;
	
	// tag name conversions keyed by path in the constrained class model
	private Hashtable <String,TagNameConversion> conversionTable;
	
	/* 'match' variable of xslt templates written out - one template, with  one 'match' expression,
	 * may apply in the in wrapper transform at several CDA paths, via different 'when' elements*/
	private Hashtable<String,Element> templateMatches; 
	
	/* If true, include all tag conversion templates in the wrapper XSLT (both in and out)
	 * because the XSLT generator mappings involve completely different classes */
	private boolean includeAllTemplates = false;
	
	private int maxTagRepeats = 4;
	
	// name of the package containing the entry class
	public static String constrainedRMIMPackageName = "cdaHeader";
	
	// the top package of the RMIM class model
	private EPackage constrainedModel;
	
	// the package of the RMIM class model containing the entry class
	private EPackage rmimPackage;
	
	/** source of simplification annotations on the class model, if there are any.
	 * In that case, tag name conversions will only be stored for nodes with simplification annotations  */
	private String simplificationAnnotationSource = null;
	
	/**
	 * @param theClass an EClass
	 * @return true if it is an ActRelationship clone
	 */
	public static boolean isActRelationship(EClass theClass)
	{
		String RIMClassName = ModelUtil.getMIFAnnotation(theClass,"RIM Class");
		return ((RIMClassName != null) && (RIMClassName.equals("ActRelationship")));
	}

	//----------------------------------------------------------------------------------------
	//                     Constructor and initialisation from the Ecore model
	//----------------------------------------------------------------------------------------
	
	public CDAConverter(MappedStructure ms, Object spare) throws MapperException
	{
		super(ms,spare);
		findConstrainedModel(ms);
		if (constrainedModel == null) message("Null constrained model");
		// find package 'cdaHeader'
		rmimPackage = ModelUtil.getPackageInPackage(constrainedModel,constrainedRMIMPackageName);
		if (rmimPackage == null) message("Null RMIM package named " + constrainedRMIMPackageName);
		initialise();
	}
	
	
	/*
	private void findConstrainedModel(MappedStructure ms)
	throws MapperException
	{
		constrainedModel = ms.getClassModelRoot();
		String otherModelAbsoluteLocation = ModelUtil.getMIFAnnotation(constrainedModel, "CDAWrapperModelAbsoluteLocation");
		String otherModelEclipseLocation = ModelUtil.getMIFAnnotation(constrainedModel, "CDAWrapperModel");

		try
		{			
			// outside Eclipse; the application should have placed this annotation at runtime
			if (otherModelAbsoluteLocation != null)
			{
				constrainedModel = FileUtil.getClassModel(otherModelAbsoluteLocation);
			}
			// inside Eclipse
			else if (otherModelEclipseLocation != null)
			{
				URI classModelURI = URI.createURI(otherModelEclipseLocation);
				constrainedModel = (EPackage)ClassModelMaker.makeClassModelFromFile(classModelURI);
			}
			
		}
		catch (IOException ex)
		 	{throw new MapperException("Cannot find Ecore model for CDA Wrapper class: " + ex.getMessage());}

		// should not filter the tag conversion  templates according to what classes
		//  the mappings know about 
		includeAllTemplates = true;
	} 
	*/


	/**
	 * The Ecore model which has modified CDA class names and association names, 
	 * and which records the correspondence between them and unmodified CDA tag names
	 * (as used by this wrapper class) is either the class model of this mapping set,
	 * or is one it points to via a 'CDAWrapperModel' annotation.
	 * Find which one it is.
	 * @param ms
	 * @throws MapperException
	 */
	private void findConstrainedModel(MappedStructure ms)
	throws MapperException
	{
		constrainedModel = ms.getClassModelRoot();
		String fullModelFileName = ModelUtil.getMIFAnnotation(constrainedModel, "CDAWrapperModel");
		if (fullModelFileName != null)
		{
			/* calculate the url of the full ecore model, by changing the file name of the simplified ecore model,
			 * assuming the two ecore models are in the same folder */
			String simpleEcoreLocation = ms().getUMLModelURL();
			StringTokenizer st = new StringTokenizer(simpleEcoreLocation,"/");
			String fullEcoreLocation = "";
			while (st.hasMoreTokens())
			{
				String step = st.nextToken();
				if (st.hasMoreTokens()) fullEcoreLocation = fullEcoreLocation + step + "/";
				else fullEcoreLocation = fullEcoreLocation + fullModelFileName;
			}
			
			try {
				URI classModelURI = URI.createURI(fullEcoreLocation);
				/* if not running in Eclipse, assume that the Eclipse project folder
				 * structure defines the relative location of the class model and the mapped structure */
				if (!FileUtil.isInEclipse())
				{
					String mappingLocation = ms().eResource().getURI().toString();
					String classModelLocation = FileUtil.ecoreFileLocation(mappingLocation, fullEcoreLocation);
					classModelURI = URI.createURI("file:/" + classModelLocation);
				}
				EObject umlRoot = ClassModelMaker.makeClassModelFromFile(classModelURI);
				if (umlRoot instanceof EPackage) constrainedModel = (EPackage)umlRoot;
			}
			catch (Exception ex) 
			 {
				ex.printStackTrace();
				throw new MapperException("Exception getting full class model root: " + ex.getMessage());
			 } 
			
		}
		includeAllTemplates = true;
	}
	
	


	private void initialise() throws MapperException
	{
		conversionTable = new Hashtable <String,TagNameConversion>();
		tagNameConversions = new Vector<TagNameConversion>();
		templateMatches = new Hashtable<String,Element>(); 
		EClass topClass = getEntryClass(rmimPackage);
		if (topClass == null) trace("Null entry class for constrained model");
		trace("Entry class name: " + topClass.getName());
		simplificationAnnotationSource = findAnnotationSource(topClass);

		String CDARootPath = "/" + topElementName();
		String constrainedTagName = topElementName();
		String constrainedPath = "/" + topElementName();
		noteTagConversions(CDARootPath,constrainedPath,constrainedTagName,topClass);
		trace("Conversions stored: " + tagNameConversions.size());
	}
	
	/**
	 * 
	 * @param thePackage
	 * @return the entry class of an RMIM model
	 */
	private EClass getEntryClass(EPackage thePackage) throws MapperException
	{
		int entries = 0;
		EClass entryClass = null;
		String entryNames = "";
		for (Iterator<EClassifier> it = thePackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier next = it.next();
			if (ModelUtil.getMIFAnnotation(next, "entry") != null) 
			{
				entryClass = (EClass)next;
				entries++;
				entryNames = entryNames + entryClass.getName() + " ";
			}
		}
		if (entries != 1) throw new MapperException("Class model has " + entries + " entry classes: " + entryNames);
		return entryClass;
	}
	
	/**
	 * 
	 * @param topClass
	 * @return the unique source of simplification annotations on this class model
	 * @throws MapperException if there is more than one source.
	 */
	private String findAnnotationSource(EClass topClass) throws MapperException
	{
		String simpleSource = null;
		// Iterate over EReferences of the top class
		for (Iterator<EStructuralFeature> it = topClass.getEStructuralFeatures().iterator(); it.hasNext();)
		{
			EStructuralFeature ref = it.next(); // does not matter if it is an EAttribute
			// try all annotations on the EReference (or EAttribute)
			for (Iterator<EAnnotation> iu = ref.getEAnnotations().iterator();iu.hasNext();)
			{
				EAnnotation ann = iu.next();
				String source = ann.getSource();
				if (source.startsWith("urn:hl7-org:v3/microITS"))
				{
					if ((simpleSource != null) && (!simpleSource.equals(source)))
						throw new MapperException("Class model has more than one source of simplification annotations: " 
								+ simpleSource + " and " + source);
					simpleSource = source;
				}
			}
		}
		return simpleSource;
	}

	/**
	 * recursive descent of the containment relations of the constrained class model,
	 * noting all tag name conversions and template ids that need to be attached,
	 * to make a CDA instance from the modified CDA instance or vice versa.
	 * 
	 * Stop the descent of the tree when any tag name is nested inside itself 
	 * more than maxTagRepeats times, 
	 * or when any tag name is encountered that does not need conversion.
	 * 
	 * @param CDAPath
	 * @param constrainedTagName
	 * @param constrainedClass
	 */
	private void noteTagConversions(String CDAPath,String constrainedPath,String constrainedTagName,EClass constrainedClass)
	throws MapperException
	{
		if (innerTagRepeats(CDAPath) > maxTagRepeats) return;
		Hashtable<String,String> templateIds = getTemplateIds(constrainedClass);
		boolean isActRel = isActRelationship(constrainedClass);

		//trace("Noting conversions at " + CDAPath + " with " + templateIds.size() + " templates; " + isActRel);
		
		String CDARefName = null;
		int childNodes = 0;
		for (Iterator<EReference> ir = constrainedClass.getEAllReferences().iterator(); ir.hasNext();)
		{
			EReference ref = ir.next();
			CDARefName = ModelUtil.getMIFAnnotation(ref, "CDA_Name");
			/* do not carry on if the tag name needs no conversion, or if this EReference
			 * is not included in a simplification of the class model */
			if ((ref.isContainment()) && (CDARefName != null) && (includeEReference(ref)))
			{
				String newCDAPath = CDAPath + "/" + CDARefName;
				EClass constrainedChild = (EClass)ref.getEType();
				String newConstrainedPath = constrainedPath + "/" + ref.getName();
				noteTagConversions(newCDAPath,newConstrainedPath,ref.getName(),constrainedChild);
				childNodes++;
			}
		}

		/* Only if this node has one child node, note the CDA tag name of that child node
		 * in the tag name conversion. This is needed to resolve cases where more than one 
		 * tag name conversion may apply */
		if (childNodes != 1) CDARefName = "";
		
		TagNameConversion conv = new TagNameConversion(constrainedClass,CDAPath,constrainedPath,constrainedTagName,templateIds,isActRel,CDARefName);
		tagNameConversions.add(conv);
		conversionTable.put(conv.constrainedPath(), conv);
	}
	
	/**
	 * 
	 * @param ref
	 * @return true if you are to include and follow this EReference in the descent of the 
	 * class model looking for tag name conversions
	 */
	private boolean includeEReference(EReference ref)
	{ 
		// if there are no simplification annotations on this CDA class model, include every EReference 
		if (simplificationAnnotationSource == null) return true;
		// if there are any simplification annotations on this CDA class model, include only EReferences that have them
		return (ref.getEAnnotation(simplificationAnnotationSource) != null);
	}

	
	//----------------------------------------------------------------------------------------
	//                     Conversion from CDA form to constrained form
	//----------------------------------------------------------------------------------------
	
	/**
	 * @return the type of document transformed to and from;
	 * see static constants in class AbstractMapperWrapper.
	 */
	public int transformType() {return AbstractMapperWrapper.XML_TYPE;}
	
	/**
	 * @return the file extension of the outer document, with initial '*.'
	 */
	public String fileExtension() {return ("*.xml");}


	
	/**
	 * @param CDARoot root 'ClinicalDocument' element of a CDA
	 * @return the constrained CDA XML document, with altered tag names (depending
	 * on template ids) but no <templateId> elements.
	 * The same V3 namespace prefix is used for the constrained document 
	 * as in the original
	 */
	public Document transformIn(Object CDARootObj) throws MapperException
	{
		// initialise the table of xml subtrees to pass to the output, and the key for that table
		keptSubtrees = new Hashtable<String,Element>();
		keyIndex = 0;

		if (!(CDARootObj instanceof Element)) throw new MapperException("CDA root is not an Element");
		Element CDARoot = (Element)CDARootObj;
		Document doc = XMLUtil.makeOutDoc();
		inResultDoc = doc;
		String CDARootPath = "/" + topElementName();

		String namespaceURI = CDARoot.getNamespaceURI();
		if (namespaceURI == null) throw new MapperException("CDA root element has no namespace");
		if (!namespaceURI.equals(AbstractMapperWrapper.V3NAMESPACEURI))
			throw new MapperException("CDA root element namespace '" + namespaceURI 
					+ "' is not the HL7 V3 namespace '" + AbstractMapperWrapper.V3NAMESPACEURI + "'");

		EClass topClass = getEntryClass(rmimPackage);
		Vector<EClass> candidates = new Vector<EClass>();
		candidates.add(topClass);
		Element constrainedRoot = constrainedElement(candidates,CDARoot,CDARootPath,doc);
		doc.appendChild(constrainedRoot);
		
		return doc;
	}
	
	/**
	 * recursive descent, making the constrained CDA Element and its subtree from the 
	 * corresponding element in the CDA document
	 * @param candidates classes in the constrained class model which may correspond to this node of the CDA
	 * @param cdaElement Element in the CDA xml structure
	 * @param cdaPath CDA path down to the CDA element
	 * @param doc XML document where the constrained instance is being written
	 * @return Constrained element corresponding to the CDA element
	 */
	private Element constrainedElement(Vector<EClass> candidates,Element cdaElement, String cdaPath, Document doc)
	throws MapperException
	{
		Element constrainedEl = null;
		EClass theClass = null;
		TagNameConversion conversion = null;

		// get the tag name conversions for this path and CDA element (it or its child supplies template ids)
		Vector<TagNameConversion> allConversions = getByCDAPath(cdaPath,cdaElement);
				
		/* try to match candidate classes with tag name conversions; 
		 * there should be 0 or 1 matched classes. */
		Vector<TagNameConversion> filteredConversions = new Vector<TagNameConversion>();
		String classNames = "";
		for (Iterator<EClass> ie = candidates.iterator();ie.hasNext();)
		{
			EClass candidate = ie.next();
			boolean classMatched = false;
			/* It is OK if one class matches more than one tag name 
			 * conversion; only one is picked per class */
			for (Iterator<TagNameConversion> ic = allConversions.iterator();ic.hasNext();)
			{
				TagNameConversion next = ic.next();
				if (next.constrainedClass.getName().equals(candidate.getName())) 
				{
					conversion = next;
					theClass = candidate;
					classMatched = true;
				}
			}
			if (classMatched) 
			{
				classNames = classNames + theClass.getName() + " ";
				filteredConversions.add(conversion);
			}
		}
		
		// if there are no further templates in the subtree, make a deep copy		
		if (filteredConversions.size() == 0)
		{
			constrainedEl = deepCopy(doc,cdaElement,AbstractMapperWrapper.IN_TRANSFORM);
			// if there are any templates on the element or its Act clone child, write a message
			writeFailureMessage(candidates, cdaElement, cdaPath);
		}
		else if (filteredConversions.size() == 1)
		{
			// theClass and conversion are already set correctly
		}
		// still need to filter tag name conversions by the child Element tag name
		else if (filteredConversions.size() > 1) try
		{
			Vector<Element> cdaChildren = XMLUtil.childElements(cdaElement);
			if (cdaChildren.size() != 1) 
				throw new MapperException(cdaChildren.size() + " child nodes to resolve " 
						+ classNames + " at path " + cdaPath);
			String tagName = XMLUtil.getLocalName(cdaChildren.get(0));
			
			// final filter of tag name conversions by child tag name
			Vector<TagNameConversion> finalConversions = new Vector<TagNameConversion>();
			for (Iterator<TagNameConversion> tc = filteredConversions.iterator();tc.hasNext();)
			{
				TagNameConversion tnc = tc.next();
				if (tagName.equals(tnc.uniqueNextCDARefName)) finalConversions.add(tnc);
			}
			if (finalConversions.size() != 1)
					throw new MapperException("Found " + finalConversions.size()
							+ " classes from filtered list " + classNames + " at path " + cdaPath);				

			conversion = finalConversions.get(0);			
			theClass = conversion.constrainedClass;
		}
		catch (MapperException ex) // when something goes wrong, make an arbitrary choice to keep going
		{
			System.out.println(ex.getMessage());
			conversion = filteredConversions.get(0);			
			theClass = conversion.constrainedClass;			
		}

		// we have got a unique tag name conversion and class in the constrained model
		if (filteredConversions.size() > 0)
		{
			// assume that CDA elements with any templates beneath them have no text content
			String newName = withPrefix(conversion.constrainedTagName,cdaElement);
			constrainedEl = doc.createElementNS(AbstractMapperWrapper.V3NAMESPACEURI, newName);
			
			// set all attributes of the constrained element, including namespace attributes
			for (int a = 0; a < cdaElement.getAttributes().getLength();a++)
			{
				Attr at = (Attr)cdaElement.getAttributes().item(a);
				constrainedEl.setAttribute(at.getName(), at.getValue());
			}
								
			// add child Elements and recurse
			for (Iterator<Element> ie = XMLUtil.childElements(cdaElement).iterator();ie.hasNext();)
			{
				Element cdaChild = ie.next();
				String cdaName = XMLUtil.getLocalName(cdaChild);
				// do not copy <templateId> elements into the constrained form 
				if (!cdaName.equals("templateId"))
				{
					String childPath = cdaPath + "/" + cdaName;
					Vector<EClass> childClasses = constrainedChildClasses(theClass, cdaName);
					if (childClasses.size() > 0)// if there is anything to move across
					{
						Element constrainedChild = constrainedElement(childClasses,cdaChild,childPath,doc);
						constrainedEl.appendChild(constrainedChild);						
					}
				}
			}					
		}
		
		return constrainedEl;
	}
	
	private int failures = 0;
	
	/**
	 * If there were some templateId elements to match, and they were not matched, write a failure message
	 * @param candidates
	 * @param cdaElement
	 * @param cdaPath
	 */
	private void writeFailureMessage(Vector<EClass> candidates,Element cdaElement, String cdaPath)
	{
		// find if there are any templates to match
		Vector<Element> templateElements = XMLUtil.namedChildElements(cdaElement, "templateId");
		// for ActRelationship elements, look for template elements under its child elements
		if (templateElements.size() == 0)
		{
			for (Iterator<Element> ie = XMLUtil.childElements(cdaElement).iterator(); ie.hasNext();)
			{
				Vector<Element> childTemplateElements = XMLUtil.namedChildElements(ie.next(), "templateId");
				for (Iterator<Element> ig = childTemplateElements.iterator();ig.hasNext();)
					templateElements.add(ig.next());
			}
		}
		
		// if there are unmatched templates, write them out
		if (templateElements.size() > 0)
		{
			failures++;
			System.out.println("\nFailure " + failures + " to match templates at path " + cdaPath);
			/* for (Iterator<EClass> it = candidates.iterator();it.hasNext();)
				System.out.println("Class " + it.next().getName()); */
			for (Iterator<Element> iu = templateElements.iterator(); iu.hasNext();)
				System.out.println("Template " + iu.next().getAttribute("root"));
		}

		
	}

	
	/**
	 * @param constrainedParent a parent class in the constrained model
	 * @param cdaTagName a CDA association name
	 * @return the child class of the parent, reached through the 
	 * association whose name or CDA name matches the CDA association name
	 */
	private Vector<EClass> constrainedChildClasses(EClass constrainedParent, String cdaTagName)
	throws MapperException
	{
		Vector<EClass> children = new Vector<EClass>();
		for (Iterator<EReference> ir = constrainedParent.getEReferences().iterator();ir.hasNext();)
		{
			EReference ref = ir.next();
			String cdaRef = ModelUtil.getEAnnotationDetail(ref, "CDA_Name");
			if ((ref.getName().equals(cdaTagName))
				||((cdaRef != null) && (cdaRef.equals(cdaTagName))))
						children.add((EClass)ref.getEType());
		}
		return children;
	}
	
	
	/**
	 * 
	 * @param doc
	 * @param source
	 * @return a deep copy of the source, in the document, removing white space.
	 * Save and restore full trees under any <text> node, replacing them by string keys which can be handled by the Java transform
	 */
	private Element deepCopy(Document doc,Element source, int transformType) throws MapperException
	{
		// copy the element with namespaces, prefixed tag name, attributes but no text or child Elements
		Element copy = (Element)doc.importNode(source, false);
		
		// if the source element has no child elements but has text, copy the text
		String text = textOnly(source);
		if (!text.equals("")) copy.appendChild(doc.createTextNode(text));
		
		// if the source element has name <text>, save or recover the whole subtree, and do not recurse
		if (XMLUtil.getLocalName(source).equals("text"))
		{
			String path = "unknown path"; // only used in error messages

			/* <text> node in input; retain the subtree in a Hashtable, and do not pass the subtree to 
			 * the in-wrapped document. Pass only the text key. */
			if (transformType == AbstractMapperWrapper.IN_TRANSFORM)
				copy = saveInputTextSubtree(source);

			/* <text> node in output; look up the subtree in the input Hashtable, and pass it to 
			 * the out-wrapped document */
			if (transformType == AbstractMapperWrapper.OUT_TRANSFORM)
				copy = recoverInputTextSubtree(source,path);
		}
		
		// recursively copy child Elements
		else 
		{
			for (Iterator<Element> ie = XMLUtil.childElements(source).iterator();ie.hasNext();)
				copy.appendChild(deepCopy(doc,ie.next(),transformType));
		}

		return copy;
	}
	
	
	/**
	 * @param localName the local name of an Element
	 * @param el an element in a namespace
	 * @return the tag name with the namespace prefix, if any
	 */
	private String withPrefix(String localName,Element el)
	{
		String newName = localName;
		String prefix = el.getPrefix();
		if ((prefix != null) && (!prefix.equals(""))) newName = prefix + ":" + newName;
		return newName;		
	}

	
	
	//----------------------------------------------------------------------------------------
	//                     Conversion from constrained form to CDA form 
	//----------------------------------------------------------------------------------------
	
	/**
	 * @param constrainedCDARoot root element of the constrained CDA document
	 * @return a normal CDA document
	 */
	public Object transformOut(Element constrainedCDARoot) throws MapperException
	{
		Document doc = XMLUtil.makeOutDoc();
		outResultDoc = doc;

		String namespaceURI = constrainedCDARoot.getNamespaceURI();
		if (namespaceURI == null)
			throw new MapperException("CDA root element has no namespace.");
		if (!namespaceURI.equals(AbstractMapperWrapper.V3NAMESPACEURI))
			throw new MapperException("Root element namespace '" + namespaceURI 
					+ "' is not the HL7 V3 namespace '" + AbstractMapperWrapper.V3NAMESPACEURI + "'");

		Element constrainedRoot = CDAElement(constrainedCDARoot,doc);
		doc.appendChild(constrainedRoot);
		
		return doc;
		
	}
	
	/**
	 * 
	 * @param constrainedEl an Element of the constrained CDA document
	 * @param doc Document where the normal CDA is being made
	 * @return the corresponding element of the normal CDA document
	 */
	private Element CDAElement(Element constrainedEl, Document doc) throws MapperException
	{
		Element cdaElement = null;
		TagNameConversion conversion = getByConstrainedTag(constrainedEl.getLocalName());

		/* if the tag name has no conversion, there are no further 
		 * templates in the subtree, so make a deep copy without white space*/
		if (conversion == null)
		{
			cdaElement = deepCopy(doc,constrainedEl,AbstractMapperWrapper.OUT_TRANSFORM);
		}

		else if (conversion != null)
		{
			String cdaName = withPrefix(conversion.CDATagName(),constrainedEl);
			// assume that CDA elements with any templates beneath them have no text content
			cdaElement = doc.createElementNS(AbstractMapperWrapper.V3NAMESPACEURI, cdaName);
			
			// set all attributes of the constrained element, including namespace attributes
			for (int a = 0; a < constrainedEl.getAttributes().getLength();a++)
			{
				Attr at = (Attr)constrainedEl.getAttributes().item(a);
				cdaElement.setAttribute(at.getName(), at.getValue());
			}

			// for elements which are not ActRelationship clones, add the <templateId> child nodes
			if (!conversion.isActRelationship)
			{
				for (Enumeration<String> en = conversion.templateIds.keys();en.hasMoreElements();)
				{
					String templateId = en.nextElement();
					String tagName = withPrefix("templateId",constrainedEl);
					Element templateElement = doc.createElementNS(AbstractMapperWrapper.V3NAMESPACEURI, tagName);
					templateElement.setAttribute("root", templateId);
					cdaElement.appendChild(templateElement);
				}
			}
			
			// add child Elements and recurse
			for (Iterator<Element> ie = XMLUtil.childElements(constrainedEl).iterator();ie.hasNext();)
			{
				Element constrainedChild = ie.next();
				Element cdaChild = CDAElement(constrainedChild,doc);
				cdaElement.appendChild(cdaChild);					
			}
			
		}
		return cdaElement;
	}

	//----------------------------------------------------------------------------------------
	//                       XSLT for wrapper out conversion
	//----------------------------------------------------------------------------------------
	
	/**
	 * @param xout the file to which xslt templates are to be written
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'out' direction.
	 * Templates must have mode = "outWrapper"
	 * @throws MapperException
	 */
	public void addWrapperOutTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		templateMatches = new Hashtable<String,Element>();  // re-empty in case it has been left full from a prior XSLT

		int templatesWritten = 0;
		// add identity template with mode="outWrapper"
		super.addWrapperOutTemplates(xout,templateFilter);
		
		// add one template, modelled on the identity template, for each tag name conversion if the class is represented
		for (Iterator<TagNameConversion>  it = tagNameConversions.iterator();it.hasNext();)
		{
			TagNameConversion tagConversion = it.next();
			// (usually) only add a conversion template for the tag if the class is represented in the input
			if ((templateFilter.includeTemplate(tagConversion.qualifiedClassName()))|includeAllTemplates)
			{
				if (addOutConversionTemplate(xout, tagConversion)) 	templatesWritten++;			
			}
		}
		trace(templatesWritten + " out wrapper templates written");

		// alter generated templates to pass through subtrees under text nodes unchanged
		passThroughTextSubtrees(xout,false);
	}
	
	/**
	 * @param xout XSLT output file
	 * @param tagConversion a tag name conversion
	 * add a conversion template of the following form:

	<xsl:template xmlns:v3="urn:hl7-org:v3" match="v3:act_AuthorizationActivity" mode="outWrapper">
		<act>
			<xsl:copy-of select="@*" />
			<templateId root="2.16.840.1.113883.10.20.1.19"/>
			<xsl:apply-templates mode="outWrapper" />
		</act>
	</xsl:template>
	
	 * @throws MapperException
	 */
	private boolean addOutConversionTemplate(XSLOutputFile xout,TagNameConversion tagConversion)  throws MapperException
	{
		boolean written = false;
		Element templateEl = xout.XSLElement("template");
		templateEl.setAttribute("mode", "outWrapper");
		templateEl.setAttribute("xmlns:v3", AbstractMapperWrapper.V3NAMESPACEURI);
		templateEl.setAttribute("match","v3:" + tagConversion.constrainedTagName);
		
		// make element with modified tag name
		Element cdaEl = xout.newElement(tagConversion.CDATagName());
		templateEl.appendChild(cdaEl);

		// copy the attributes of the element with modified tag name
		Element copyOfEL = xout.XSLElement("copy-of");
		copyOfEL.setAttribute("select", "@*");
		cdaEl.appendChild(copyOfEL);
				
		// find which <templateId> child elements to add ('template' = CDA template, not XSL template)

		// normal case; the tag conversion holds child templateIds for the node
		Hashtable<String,String> tempIds = tagConversion.templateIds;
		// ActRelationship clones have no direct templateId children
		if (tagConversion.isActRelationship()) tempIds = new Hashtable<String,String>();
		// Act clones take templateId children from their parent ActRelationship clone
		TagNameConversion parent = tagConversion.getParent();
		if ((parent != null) && (parent.isActRelationship()))
			tempIds = parent.templateIds;
		
		// add <templateId> child elements
		for (Enumeration<String> en = tempIds.keys();en.hasMoreElements();)
		{
			String key = en.nextElement();
			Element CDATempEl = xout.newElement("templateId");
			CDATempEl.setAttribute("root", key);
			cdaEl.appendChild(CDATempEl);
		}
		
		// apply-templates to recurse down the output XML tree
		Element applyEl = xout.XSLElement("apply-templates");
		applyEl.setAttribute("mode", "outWrapper");
		cdaEl.appendChild(applyEl);
		
		// write the template to the xslt, if a template of this match has not already been written.
		if (templateMatches.get(tagConversion.constrainedTagName) == null)
		{
			xout.topOut().appendChild(templateEl);
			templateMatches.put(tagConversion.constrainedTagName,templateEl);
			written = true;
		}
		return written;
	}
	
	
	/**
	 * @param xout the file to which xslt templates are to be written
	 * append the templates and variables to be included in the XSL
	 * to do the full transformation, to apply the wrapper transform in the 'out' direction.
	 * Templates must have mode = "outWrapper"
	 * @throws MapperException
	 * 
	 * This template ensures that <templateID> elements are not copied in:
	 
	 <xsl:template xmlns:v3="urn:hl7-org:v3" match = "v3:templateId" mode="inWrapper"/>
	 
	 * 
	 */
	public void addWrapperInTemplates(XSLOutputFile xout, TemplateFilter templateFilter)  throws MapperException
	{
		templateMatches = new Hashtable<String,Element>();  // re-empty in case it has been left full from a prior XSLT

		// add identity template with mode="inWrapper", which passes down the path as a parameter
		xout.topOut().appendChild(identityPathTemplate(xout,"inWrapper"));
		
		// add the template which does not copy in templateId elements
		Element templateEl = xout.XSLElement("template");
		templateEl.setAttribute("mode", "inWrapper");
		templateEl.setAttribute("xmlns:v3", AbstractMapperWrapper.V3NAMESPACEURI);
		templateEl.setAttribute("match", "v3:templateId");
		xout.topOut().appendChild(templateEl);
		
		// add a template to add the SNOMED codeSystem attribute to observation  values.
		addCodeSystemToValue(xout);
		
		// add templates to add fixed values of RIM structural attributes to name parts
		fixNamePart("prefix","PRF",xout);
		fixNamePart("given","GIV",xout);
		fixNamePart("family","FAM",xout);
		fixNamePart("suffix","SUF",xout);

		/* add one template, modelled on the identity template, for each tag name conversion if the class is represented.
		 * Templates with the same 'match' but different CDA paths become different 'when' branches 
		 * of the same template. */
		for (Iterator<TagNameConversion>  it = tagNameConversions.iterator();it.hasNext();)
		{
			TagNameConversion tagConversion = it.next();
			// (usually) only add a conversion template for the tag if the class is represented in both the input and the output
			if ((templateFilter.includeTemplate(tagConversion.qualifiedClassName()))|includeAllTemplates)
				addInConversionTemplate(xout, tagConversion);
		}
		
		// alter generated templates to pass through subtrees under text nodes unchanged
		passThroughTextSubtrees(xout,true);
	}
	
	/**
	 * add a template to add the SNOMED codeSystem attribute to observation  values.
	 * The template looks like:
	 
	<xsl:template xmlns:v3="urn:hl7-org:v3" match="v3:organizer/v3:component/v3:observation/v3:value" mode="inWrapper">
		<v3:value codeSystem='2.16.840.1.113883.6.8' >
			<xsl:copy-of select="@*" />
		</v3:value>
	</xsl:template>
	
	 */
	public static void addCodeSystemToValue(XSLOutputFile xout) throws MapperException
	{
		Element templateEl = xout.XSLElement("template");
		templateEl.setAttribute("match", "v3:organizer/v3:component/v3:observation/v3:value");
		templateEl.setAttribute("mode","inWrapper");
		templateEl.setAttribute("xmlns:v3", AbstractMapperWrapper.V3NAMESPACEURI);
		
		Element valEl = xout.newElement("v3:value");
		valEl.setAttribute("codeSystem", "2.16.840.1.113883.6.8");
		templateEl.appendChild(valEl);
		
		Element copyEl = xout.XSLElement("copy-of");
		copyEl.setAttribute("select", "@*");
		valEl.appendChild(copyEl);
		
		xout.topOut().appendChild(templateEl);		
	}
	
	/**
	 * Add a template to add fixed values of RIM Structural attributes to name parts, 
	 * if they are not present already. This template looks like:
	 * 
	 
	<xsl:template xmlns:v3="urn:hl7-org:v3" match="v3:name/v3:given" mode="inWrapper">
		<v3:given representation="TXT" mediaType="text/plain" partType="GIV" >
			<xsl:value-of select="." />
		</v3:given>
	</xsl:template>
	
	 * 
	 * @param partType e.g. 'given'
	 * @param partType e.g. 'GIV'
	 * @param xout
	 */
	public static void fixNamePart(String partName, String partType, XSLOutputFile xout) throws MapperException
	{
		Element templateEl = xout.XSLElement("template");
		templateEl.setAttribute("match", "v3:name/v3:" + partName);
		templateEl.setAttribute("mode","inWrapper");
		templateEl.setAttribute("xmlns:v3", AbstractMapperWrapper.V3NAMESPACEURI);
		
		Element nameEl = xout.newElement("v3:" + partName);
		nameEl.setAttribute("representation", "TXT");
		nameEl.setAttribute("mediaType", "text/plain");
		nameEl.setAttribute("partType", partType);
		templateEl.appendChild(nameEl);
		
		Element valueEl = xout.XSLElement("value-of");
		valueEl.setAttribute("select", ".");
		nameEl.appendChild(valueEl);
		
		xout.topOut().appendChild(templateEl);
	}
	
	
	/**
	 * @param xout XSLT output file
	 * @param tagConversion a tag name conversion
	 * add a conversion template of the following form - 
	 * which passes down the path in the constrained CDA document as a parameter, 
	 * and only does something when the constrained path of the parent matches that expected for the tag name conversion
	 * 
	 * If several tagConversions with different parent paths all have the same match, then 
	 * there are several 'xsl:when' elements in the same template
	 * 
	 * On the top call, the parameter 'path' is not supplied, so takes its default value
	 * of the empty string

	<xsl:template xmlns:v3="urn:hl7-org:v3" match="v3:act[templateId@root="2.16.840.1.113883.10.20.1.19"] mode="outWrapper">
		<xsl:param name="path"/>
		<xsl:choose>
			<xsl:when test="$path='/ClinicalDocument/..../entry'>
				<xsl:variable name="newPath" select="concat($path,'/','act_AuthorizationActivity')"/>
				<v3:act_AuthorizationActivity>
					<xsl:copy-of select="@*" />
					<xsl:apply-templates mode="outWrapper">
						<xsl:with-param name="path" select="$newPath"/>
					</xsl:apply-templates>
				<v3:act_AuthorizationActivity>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	 * @throws MapperException
	 */
	private void addInConversionTemplate(XSLOutputFile xout,TagNameConversion tagConversion)  throws MapperException
	{
		
		String CDATagName = tagConversion.CDATagName();

		String requiredPath = "";
		if (tagConversion.getParent() != null) requiredPath = tagConversion.getParent().constrainedPath();

		String tagName = tagConversion.constrainedTagName;
		String addToPath = tagName;

		// normal case; the tag conversion holds child templateIds for the node
		Hashtable<String,String> tempIds = tagConversion.templateIds;
		// Act clones take templateId children from their parent ActRelationship clone
		TagNameConversion parent = tagConversion.getParent();
		if ((parent != null) && (parent.isActRelationship()))
			tempIds = parent.templateIds;
		
		String conditions = "[";
		for (Enumeration<String> en = tempIds.keys();en.hasMoreElements();)
		{
			String oneCond = "v3:templateId/@root='" + en.nextElement() + "'";
			String uniqueNextCDARefName = tagConversion.uniqueNextCDARefName();
			/* ActRelationship clones get constraint XPaths down to their single Act child, 
			 * because the templateId elements are on the child */
			if (tagConversion.isActRelationship()) oneCond = "v3:" + uniqueNextCDARefName + "/" + oneCond;
			conditions = conditions + oneCond;
			if (en.hasMoreElements()) conditions = conditions + " and ";
			else conditions = conditions + "]";
		}
		if (conditions.equals("[")) conditions = "";

		String priority = "1." + new Integer(tempIds.size()).toString();
		String mode="inWrapper";

		addPathTemplate(xout, 
				mode,
				CDATagName,
				conditions,
				requiredPath,
				addToPath,
				tagName,
				priority,
				templateMatches);
	}
	

	//----------------------------------------------------------------------------------------
	//                         Inner class for one class name conversion
	//----------------------------------------------------------------------------------------
	
	class TagNameConversion{
		
		// class in the constrained model with the template
		private EClass constrainedClass;
		
		// path to this node in the CDA XML
		private String CDAPath;
		
		// path to this node in the constrained model
		private String constrainedPath;
		public String constrainedPath() {return constrainedPath;}
		
		// tag name of this node in the constrained XML ( = association name leading to the class)
		private String constrainedTagName;
		
		/* template ids of templates on this node; 
		 * or if it is an ActRelationship clone, the templates on its Act clone child 
		 * Key = templateId; value = "1"*/
		private Hashtable<String,String> templateIds;
		
		// true if the class reached by this path is an ActRelationship clone
		private boolean isActRelationship;
		public boolean isActRelationship() {return isActRelationship;}
		
		/* if this node has only one child node in the constrained model 
		 * (e.g. if it is an ActRelationship or Participation), the CDA tag name
		 * of that unique child node . Otherwise "" */
		private String uniqueNextCDARefName;
		
		private TagNameConversion(EClass constrainedClass, String CDAPath,String constrainedPath,String constrainedTagName,
				Hashtable<String,String>  templateIds, 
				boolean isActRelationship, String uniqueNextCDARefName)
		{
			this.constrainedClass = constrainedClass;
			this.CDAPath = CDAPath;
			this.constrainedPath = constrainedPath;
			this.constrainedTagName = constrainedTagName;
			this.templateIds = templateIds;
			this.isActRelationship = isActRelationship;
			this.uniqueNextCDARefName = uniqueNextCDARefName;
			trace("Tag name conversion for class '" + constrainedClass.getName() + "' from constrained path '" + constrainedPath 
					+ "' to CDA path '" + CDAPath + "' with " + templateIds.size() + " templates");
		}
		
		private String CDATagName()
		{
			String tagName = "";
			StringTokenizer st = new StringTokenizer(CDAPath,"/");
			while (st.hasMoreTokens()) tagName = st.nextToken();
			return tagName;
		}
		
		public TagNameConversion getParent()
		{
			StringTokenizer st = new StringTokenizer(constrainedPath,"/");
			String parentPath = "";
			while (st.hasMoreTokens())
			{
				String step = st.nextToken();
				if (st.hasMoreTokens()) parentPath = parentPath + "/" + step; // miss out the last step
			}
			return conversionTable.get(parentPath); // null for the top conversion
		}
		
		public String qualifiedClassName()
		{
			return constrainedClass.getEPackage().getName() + "." + constrainedClass.getName();
		}
		
		public String uniqueNextCDARefName() {return uniqueNextCDARefName;}
	}
	
	//----------------------------------------------------------------------------------------
	//                                      Utilities
	//----------------------------------------------------------------------------------------
	
		private TagNameConversion getByConstrainedTag(String constrainedTagName)
		{
			TagNameConversion tnc = null;
			for (Iterator<TagNameConversion>  it = tagNameConversions.iterator();it.hasNext();)
			{
				TagNameConversion next = it.next();
				if (next.constrainedTagName.equals(constrainedTagName)) tnc = next;
			}
			return tnc;
		}

		
		/**
		 * @param cdaPath the path to a CDA Element
		 * @param cdaElement the CDA Element
		 * @return the tag name conversion appropriate for the Element,
		 * depending on the path to it and the templates on it.
		 */
		private Vector<TagNameConversion> getByCDAPath(String cdaPath, Element cdaElement)
		throws MapperException
		{
			Vector<TagNameConversion> conversions = new Vector<TagNameConversion>();
			for (Iterator<TagNameConversion>  it = tagNameConversions.iterator();it.hasNext();)
			{
				TagNameConversion next = it.next();
				if ((next.CDAPath.equals(cdaPath)) && (templatesMatch(next,cdaElement,true)))
						conversions.add(next);
			}
			return conversions;
		}
		
		/**
		 * @param conversion a node name conversion, whose path from the root
		 * matches that of the CDA Element
		 * @param cdaElement an element of the CDA
		 * @param topCall a boolean to stop it recursing more than once
		 * @return true if the templates expected in the conversion exactly match
		 * those on the Element. If the class reached by the path is an ActRelationship
		 * clone, which has no templates, then you need to look at its child elements to 
		 * find the <templateID> child nodes to match the templates of the conversion 
		 */
		private boolean templatesMatch(TagNameConversion conversion, Element cdaElement, boolean topCall)
		throws MapperException
		{
			boolean matches = false;
			// find template ids on the element
			Vector<Element> templateElements = XMLUtil.namedChildElements(cdaElement, "templateId");
			// trace("Template Elements: " + templateElements.size() + "; templates: " + conversion.templateIds.size());

			// for ActRelationship elements, there is only one child with templates; so if that child 's templates match, it is a match
			if ((topCall) && (conversion.isActRelationship))
			{
				for (Iterator<Element> ie = XMLUtil.childElements(cdaElement).iterator(); ie.hasNext();)
					if (templatesMatch(conversion,ie.next(),false)) return true;
				// inner topCall = false to stop recursion
			}

			// otherwise, match with this element's templates. the number of templates must match
			else if (templateElements.size() == conversion.templateIds.size())
			{
				matches = true;
				for (Iterator<Element>  it = templateElements.iterator(); it.hasNext();)
				{
					Element templateEl = it.next();
					// each template id found in the 'root' attribute must be one of those expected
					if (conversion.templateIds.get(templateEl.getAttribute("root"))== null) matches = false;
				}
			}
			return matches;				
		}
		
		/**
		 * @param path a path of tag names separated by '/'
		 * @return the number of times the innermost tag name occurs
		 */
		private int innerTagRepeats(String path)
		{
			// find the innermost tag name
			String inner = "";
			StringTokenizer st = new StringTokenizer(path,"/");
			while (st.hasMoreTokens()) inner = st.nextToken();

			// find how many times it repeats
			int repeats = 0;
			st = new StringTokenizer(path,"/");
			while (st.hasMoreTokens()) {if (st.nextToken().equals(inner)) repeats++;}
			return repeats;
		}
		
		/**
		 * @param constrainedClass
		 * @return a Hashtable of all the template ids on this EClass - 
		 * or if it is an ActRelationship clone, the templates on all Act clone child classes
		 * (there can sometimes be more than one)
		 */
		private Hashtable<String,String> getTemplateIds(EClass constrainedClass)
		throws MapperException
		{
			Hashtable<String,String> templateIds = new Hashtable<String,String>();
			int i = 0;
			boolean found = true;

			// for ActRelationships, collect template ids from all child classes (usually 1 Act clone)
			if (isActRelationship(constrainedClass))
				for (Iterator<EReference> ir = constrainedClass.getEAllReferences().iterator();ir.hasNext();)
				{
					EClass child = (EClass)ir.next().getEType();
					Hashtable<String,String> childTemplateIds = getTemplateIds(child);
					for (Enumeration<String> en = childTemplateIds.keys();en.hasMoreElements();)
						templateIds.put(en.nextElement(),"1");
				}

			// otherwise, find all template annotations on this class
			else while(found)
			{
				String templateKey = "template";
				if (i > 0) templateKey = templateKey + "_" + i;
				String templateId = ModelUtil.getMIFAnnotation(constrainedClass, templateKey);
				if (templateId == null) found = false;
				else templateIds.put(templateId,"1");
				i++;
			}
			return templateIds;
		}

	//----------------------------------------------------------------------------------------
	//                                trivia
	//----------------------------------------------------------------------------------------
		
	private void message(String s) {System.out.println(s);}

}
