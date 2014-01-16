package com.openMap1.mapper.health.v3;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.w3c.dom.Element;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.WorkBenchUtil;

/**
 * represents one V3 RMIM, which may be either a top RMIM or a CMET
 * 
 * @author robert
 *
 */
public class V3RMIM {
	
	private String rmimId; // e.g. 'PORX_HD980030UV'
	public String rmimId() {return rmimId;}
	
	private String rmimTrail;// the trail of RMIM ids leading from the top RMIM to this one 
	
	private RMIMReader rmimReader;
	
	private String path;
	
	private Element rootElement;
	
	private EPackage RMIMPackage;
	
	private Hashtable<String,V3Name> v3Names;
	
	public V3Name getV3Name(String name) {return v3Names.get(name);}
	
	/**
	 * @return name of the entry class or choice
	 */
	public String entryClassName() 
	{
		String name = entryClassNameInMIF;
		if (replacedEntryClassName != null) 
		{
			name = replacedEntryClassName;
		}
		return name;
	}
		
	private String entryClassNameInMIF; // name of the entry class or choice
		
	private String replacedEntryClassName = null;
	private String expectedOriginalEntryClassName = null;

	public V3Name getEntryV3Name()
	{
		return v3Names.get(entryClassName());
	}
	
	private boolean isTopRMIM;
	
	//-------------------------------------------------------------------------------------------
	//                                constructor
	//-------------------------------------------------------------------------------------------
	
	/**
	 * @param rmimReader the reader which is reading in the current MIF file
	 * @param rootElement the root element of the current MIF file
	 * @param path file path to the current MIF file
	 * @param RMIMtrail names of CMETs from the root RMIM down to this RMIM/CMET
	 * @param keepTopCnoice; if true, make an EClass object for the top Choice
	 */
	public V3RMIM(RMIMReader rmimReader, Element rootElement, String path, String rmimTrail, boolean isTopRMIM)
	throws MapperException
	{
		this.path = path;
		this.rootElement = rootElement;
		this.rmimReader = rmimReader;
		this.isTopRMIM = isTopRMIM;
		if (this.isTopRMIM) {} // seems not to be used

		v3Names = new Hashtable<String,V3Name>();

		if (checkHeader(rmimTrail,isTopRMIM))
			readClasses();
		else throw new MapperException("Stopped creation of Ecore model");
	}
	
	private boolean checkHeader(String rmimTrail, boolean isTopRMIM)
	throws MapperException
	{
		boolean checked = false;
		String uri = rootElement.getNamespaceURI();
		boolean headerURIChecks = ((uri != null) && (uri.equals(rmimReader.mifNamespaceURI())));
		// skip the URI check for NHS MIM (has MIF 2.0 URI; otherwise like MIF 2.1)
		if (rmimReader.isNHSMIF()) headerURIChecks = true;
		if (headerURIChecks)
		{
			if (!rootElement.getLocalName().equals("staticModel"))
				throw new MapperException
				("Root element of file at " + path + " is not 'staticModel'");
			if (!rootElement.getAttribute("representationKind").equals("flat"))
					throw new MapperException
					("File at " + path + " is not a flat MIF file");
			
			readPackageElement();

			Element entryEl = XMLUtil.firstNamedChild(rootElement,"entryPoint");
			if ((entryEl == null) && (rmimReader.isNHSMIF())) entryEl = XMLUtil.firstNamedChild(rootElement,"ownedEntryPoint");
			if (entryEl == null)			
				throw new MapperException ("File at " + path + " has no 'entryPoint' element");
			String entryName = entryEl.getAttribute("name");
			rmimId = entryEl.getAttribute("id");
			/* the top RMIM should have an id; otherwise, ask the user if he is prepared 
			 * to use its name as an id */
			if (isTopRMIM && (rmimId.equals("")))
			{
				boolean confirm = WorkBenchUtil.askConfirm("RMIM has no id", "The chosen RMIM has no id on its entry point." + 
						" Use its name '" + entryName + "' in stead, for the Ecore file name?");
				if (!confirm) return false;
				else rmimId = entryName;
			}
			this.rmimTrail = rmimTrail + "/" + rmimId;

			RMIMPackage = EcoreFactory.eINSTANCE.createEPackage();
			RMIMPackage.setName(rmimId);
			entryClassNameInMIF = entryEl.getAttribute("className");
			if (rmimReader.isNHSMIF()) 
			{
				expectedOriginalEntryClassName = rmimReader.getOriginalNHSEntryClassName(rmimId);
				replacedEntryClassName = rmimReader.getAlteredNHSEntryClassName(rmimId);
				if ((expectedOriginalEntryClassName != null) && (!entryClassNameInMIF.equals(expectedOriginalEntryClassName)) )
					throw new MapperException("Error in template entry names csv file: the entry class of template '"
						+ rmimId + "' is '" + entryClassNameInMIF + "', not '" + expectedOriginalEntryClassName + "'");
			}
			ModelUtil.addMIFAnnotation(RMIMPackage, "name", entryName);
			String description = getMIFAnnotation(entryEl);
			if (!description.equals(""))
				ModelUtil.addMIFAnnotation(RMIMPackage, "description", description);
			rmimReader.topPackage().getESubpackages().add(RMIMPackage);
			checked = true;

		}
		else throw new MapperException
			("Root element of file at " + path + " is not in the MIF2 namespace");
		return checked;
	}
	
	/**
	 * @param el an Element in a MIF file
	 * @return the text of a (highly nested) annotation on it, or ""
	 * if the form of the annotation does not match the nested form
	 */
	private String getMIFAnnotation(Element el)
	{
		String description = "";
		Element annotations = XMLUtil.firstNamedChild(el, "annotations");
		if (annotations != null)
		{
			Element documentation = XMLUtil.firstNamedChild(annotations, "documentation");
			if (documentation != null)
			{
				Element desc = XMLUtil.firstNamedChild(documentation, "description");
				if (desc != null)
				{
					Element text = XMLUtil.firstNamedChild(desc, "text");
					if (text != null) description = XMLUtil.getText(text);
				}
			}
		}
		return description;
	}
	
	/**
	 * put an annotation on the entry class or choice of this RMIM, to say that it
	 * is the entry class of the whole model
	 */
	public void markEntryClass()
	{
		if (getEntryV3Name() instanceof ConcreteClass)
		{
			EClass entryClass = ((ConcreteClass)getEntryV3Name()).eClass();
			ModelUtil.addMIFAnnotation(entryClass, "entry", "true");
		}
		/* If the entry point is a choice, create an artificial EClass for it, and 
		 * give it EReferences to the classes the choice resolves to  */
		else if (getEntryV3Name() instanceof Choice)
		{
			EClass entryChoice = EcoreFactory.eINSTANCE.createEClass();
			entryChoice.setName(getEntryV3Name().name());
			ModelUtil.addMIFAnnotation(entryChoice, "entry", "true");
			ModelUtil.addMIFAnnotation(entryChoice, "choice", "true");
			RMIMPackage.getEClassifiers().add(entryChoice);
			// give the choice an unnamed association to each of the concrete classes it resolves to
			for (Iterator<EClass> it = ((Choice)getEntryV3Name()).getAllEClasses().iterator();it.hasNext();)
			{
				EClass child = it.next();
				EReference ref = EcoreFactory.eINSTANCE.createEReference();
				ref.setName("");
				ref.setEType(child);
				ref.setContainment(true);
				entryChoice.getEStructuralFeatures().add(ref);
			}
		}
	}
	
	private void readPackageElement() throws MapperException
	{
		/*
		Element packageEl = XMLUtil.firstNamedChild(rootElement,"packageLocation");
		if (packageEl == null)
			throw new MapperException
			("File at " + path + " has no 'packageLocation' element");

		String realmNamespace = packageEl.getAttribute("realmNamespace");
		String artifact = packageEl.getAttribute("artifact");
		String subSection = packageEl.getAttribute("subSection");
		String domain = packageEl.getAttribute("domain");
		String id = packageEl.getAttribute("id");
		*/		
	}

	//--------------------------------------------------------------------------------------------
	//                    Reading classes and properties, choices and CMETs
	//--------------------------------------------------------------------------------------------
	
	
	private void readClasses() throws MapperException
	{
		for (Iterator<Element> it = XMLUtil.namedChildElements(rootElement,classElementName()).iterator();it.hasNext();)
		{
			Element contained = it.next();
			Element classEl = XMLUtil.firstNamedChild(contained, "class");
			if (classEl != null)
			{
				// concrete classes
				boolean isConcrete = classEl.getAttribute("isAbstract").equals("false");
				// temporary, to see where templates link in to NHS RMIMs
				if ((rmimReader.isNHSMIF()) && (classEl.getAttribute("name").startsWith("Template"))) isConcrete = true;
				if (isConcrete)
				{
					EClass theClass = EcoreFactory.eINSTANCE.createEClass();
					String className = classEl.getAttribute("name");
					
					// NHS templates may have concrete class names replaced
					if ((replacedEntryClassName != null) && (className.equals(entryClassNameInMIF)))
						className = replacedEntryClassName;

					recordClassOccurrence(className);
					theClass.setName(className);
					String RIMClassName = derivedValue(classEl,1,"className");
					ModelUtil.addMIFAnnotation(theClass,"RIM Class",RIMClassName);
					warnIfDuplicateClass(RMIMPackage,className,rmimId);
					RMIMPackage.getEClassifiers().add(theClass);
					addAttributes(theClass,classEl,RIMClassName);
					ConcreteClass conClass = new ConcreteClass(className,theClass);
					v3Names.put(className, conClass);
					
					// if the class may import NHS template RMIMs, mark its V3Name and read them
					if (rmimReader.isNHSMIF()) readNHSTemplateRMIMs(classEl, conClass);
				}
				
				// choice elements
				else if (classEl.getAttribute("isAbstract").equals("true"))
				{
					String choiceName = classEl.getAttribute("name");
					// NHS templates may have choice class names replaced (no use case yet)
					if ((replacedEntryClassName != null) && (choiceName.equals(entryClassNameInMIF)))
						choiceName = replacedEntryClassName;

					Choice theChoice = new Choice(choiceName,this);
					v3Names.put(choiceName, theChoice);
					for (Iterator<Element> ic = XMLUtil.namedChildElements(classEl,choiceChildClassElementName()).iterator();ic.hasNext();)
						theChoice.addItem(ic.next().getAttribute(choiceClassNameAttribute()));
				}				
			}
			Element cmetEl = XMLUtil.firstNamedChild(contained, "commonModelElementRef");
			// store all referenced CMETs in the RMIMReader
			if (cmetEl != null)
			{
				String CMETName = cmetEl.getAttribute(cmetNameAttribute());
				// only read any CMET once
				if (rmimReader.startedCMETs().get(CMETName) == null)
				{
					rmimReader.startedCMETs().put(CMETName, "1");
					String CMETFileName = rmimReader.getCMETFilename(CMETName);
					String CMETFilePath = FileUtil.siblingFilePath(path, CMETFileName);
					Element CMETRoot = null;
					try {CMETRoot = XMLUtil.getRootElement(CMETFilePath);}
					catch (Exception ex) {CMETRoot = null;}
					if (CMETRoot != null)
					{
						boolean isTop = false;
						V3RMIM theCMET = new V3RMIM(rmimReader,CMETRoot,CMETFilePath,rmimTrail,isTop);
						rmimReader.referencedCMETs().put(CMETName, theCMET);
					}
					else if (CMETRoot == null) rmimReader.missingCMETs().put(CMETName, "1");
				}
			}
		}
		
		// mark the entry class of the template (NHS MIFs only)
		if ((getEntryV3Name() instanceof ConcreteClass) && (rmimReader.isNHSMIF()))
		{
			EClass entryClass = ((ConcreteClass)getEntryV3Name()).eClass();
			ModelUtil.addMIFAnnotation(entryClass, "templateEntry", "true");
		}
		//writeV3Names();
	}
	
	/**
	 * 
	 * @param thePackage
	 * @param className
	 * @param fromPackage
	 */
	private void warnIfDuplicateClass(EPackage thePackage, String className, String fromPackage) throws MapperException
	{
		EClass existingClass = (EClass)thePackage.getEClassifier(className);
		if (existingClass != null)
		{
			String message = "Duplicate class '" + className + "' from CMET '" 
				+ fromPackage + "' will be added to package '" + thePackage.getName() + "'." ;
			boolean carryOn = WorkBenchUtil.askConfirm("Warning", message + " Do you want to carry on?");
			System.out.println(message);
			if (!carryOn) throw new MapperException("User cancelled because: " + message);
		}		
	}
	
	
	/**
	 * Look up the template RMIMs allowed by a constraint; mark the V3Name for the class
	 * as being invoked by the class; and recurse to read in the template RMIMs
	 * @param classEl
	 * @param conClass
	 */
	private void readNHSTemplateRMIMs(Element classEl, ConcreteClass conClass) throws MapperException
	{
		//String cName = conClass.name();
		String constraint  = getNHSConstraint(classEl);
		if (constraint != null)
		{
			// record the templateIds allowed by the constraint; there should be some
			Vector<String> templateIds = new Vector<String>();
			boolean idsFound = false;
			/* try all domains which are in the template config file, 
			 * and pick the first that has any template ids for the RMIM.  */
			for (Enumeration<String> en = rmimReader.NHSDomains().keys(); en.hasMoreElements();)
			{
				String domainId = en.nextElement();
				if  (templateIds.size() == 0)
				{
					idsFound = true;
					templateIds = rmimReader.getTemplateIds(constraint, domainId, rmimId);					
				}
			}
			if (!idsFound) throw new MapperException("No suggested domain ids found in template config file");
			if (templateIds. size() == 0) throw new MapperException("No template ids found in template config file for constraint '"
					+ constraint + "' in any domain for RMIM '" + rmimId + "'");
			conClass.setTemplateIds(templateIds);
			
			// read the template RMIMs
			for (Iterator<String> it = conClass.templateNames().iterator();it.hasNext();)
			{
				String templateName = it.next();
				// only read any CMET once
				if (rmimReader.startedCMETs().get(templateName) == null)
				{
					rmimReader.startedCMETs().put(templateName, "1");
					String CMETFileName = rmimReader.getTemplateFilename(templateName);
					String CMETFilePath = FileUtil.siblingFilePath(path, CMETFileName);
					Element CMETRoot = null;
					try {CMETRoot = XMLUtil.getRootElement(CMETFilePath);}
					catch (Exception ex) 
					{
						CMETRoot = null; // the lack of the MIF file  is noted later
					}
					if (CMETRoot != null)
					{
						boolean isTop = false;
						V3RMIM theCMET = new V3RMIM(rmimReader,CMETRoot,CMETFilePath,rmimTrail,isTop);
						rmimReader.referencedCMETs().put(templateName, theCMET);
					}
					else if (CMETRoot == null) rmimReader.missingCMETs().put(templateName, "1");
				}
				
				// use the CMET, whether you have just made it or not
				V3RMIM theCMET = rmimReader.referencedCMETs().get(templateName);
				if (theCMET != null)
				{
					String CMETId = theCMET.rmimId;
					String cloneNameSuffix = theCMET.entryClassName();
					conClass.addTemplateNameSuffix(templateName, cloneNameSuffix);
					conClass.addTemplateClone(templateName, templateCloneClass(conClass.eClass(),cloneNameSuffix,classEl,CMETId));					
				}
			}			
		}
	}
	
	/**
	 * 
	 * @param plainClass
	 * @param cloneNameSuffix
	 * @param classEl
	 * @return a clone of a class, made from the same Element, but with a suffix after the class name
	 * @throws MapperException
	 */
	private EClass templateCloneClass(EClass plainClass,String cloneNameSuffix, Element classEl, String CMETId) throws MapperException
	{
		EClass theClass = EcoreFactory.eINSTANCE.createEClass();
		String className = plainClass.getName() + "_" + cloneNameSuffix;
		recordClassOccurrence(className);
		theClass.setName(className);
		String RIMClassName = derivedValue(classEl,1,"className");
		ModelUtil.addMIFAnnotation(theClass,"RIM Class",RIMClassName);
		warnIfDuplicateClass(RMIMPackage,className,CMETId);
		RMIMPackage.getEClassifiers().add(theClass);
		addAttributes(theClass,classEl,RIMClassName);
		return theClass;
	}
	
	/**
	 * If a class is marked with an NHS constraint id in a MIF file, return the constraint id
	 * @param classEl
	 * @return
	 */
	private String getNHSConstraint(Element classEl)
	{
		String constraint = null;
		Element annot = XMLUtil.firstNamedChild(classEl, "annotations");
		if (annot != null)
		{
			Vector<Element> consEls = XMLUtil.namedChildElements(annot, "constraint");
			for (Iterator<Element> it = consEls.iterator();it.hasNext();)
			{
				Element cons = it.next();
				if (cons.getAttribute("name").equals("contentId"))
				{
					Element textEl = XMLUtil.firstNamedChild(cons, "text");
					Element para = XMLUtil.firstNamedChild(textEl, "p");
					if (para != null) constraint = XMLUtil.getText(para);				
				}
			}			
		}
		return constraint;
	}

	
	
	/**
	 * add the attributes of an RMIM class, as EReferences to the data type class
	 * @param theClass an RMIM class
	 * @param classEl the 'class' element representing it in the MIF
	 * @param RIMClassName the name of the RIM class this is a clone of
	 * @throws MapperException
	 */
	private void addAttributes(EClass theClass,Element classEl,String RIMClassName) throws MapperException
	{
		for (Iterator<Element> it = XMLUtil.namedChildElements(classEl, "attribute").iterator();it.hasNext();)
		{
			Element attEl = it.next();
			String attName = attEl.getAttribute("name");
			Element typeEl = XMLUtil.firstNamedChild(attEl, "type");
			String typeName = getUnderScoreTypeName(typeEl);
			boolean groupingType = isGroupingType(typeName);
			if (groupingType) typeName = groupedType(typeName);

			String fixedValue = ""; // single fixed value, defined in various ways
			// for NHS MIF files, multiple fixed values defined in html for the tabular view
			Hashtable<String,String> htmlFixedValues = getHtmlFixedValues(attEl);
			if (!rmimReader.isNHSMIF())
			{
				// can pick up fixed values from a <vocabulary> child of the <attribute>
				Element vocabularyEl = XMLUtil.firstNamedChild(attEl, vocabularyElementName());
				if (vocabularyEl != null)
				{
					// two ways of indicating a fixed value
					Element codeEl = XMLUtil.firstNamedChild(vocabularyEl, "code");
					if (codeEl != null) fixedValue = codeEl.getAttribute("code");

					// as found in the May 2009 ballot pack
					Element valueSetEl = XMLUtil.firstNamedChild(vocabularyEl, "valueSet");
					if (valueSetEl != null) fixedValue = valueSetEl.getAttribute("rootCode");
				}
				
				// particularly for the CDA MIF from Jiva, define a fixed value if there is exactly one <enumerationValue> child
				Vector<Element> values = XMLUtil.namedChildElements(attEl, "enumerationValue");
				if (values.size() == 1) fixedValue = XMLUtil.getText(values.get(0));
			}
			else if (rmimReader.isNHSMIF())
			{
				fixedValue=attEl.getAttribute("fixedValue");
			}
			
			// RIM structural attributes are attributes of the EClass in the Ecore model
			if (V3RIM.isRIMStructuralAttribute(RIMClassName, attName))
			{
				EAttribute att = EcoreFactory.eINSTANCE.createEAttribute();
				att.setName(attName);
				att.setEType(getRIMEcoreDataType(attName));
				if (attEl.getAttribute("minimumMultiplicity").equals("1"))  att.setLowerBound(1);
				if (attEl.getAttribute("minimumMultiplicity").equals("0"))  att.setLowerBound(0);
				if (!fixedValue.equals("")) ModelUtil.addMIFAnnotation(att, "fixed value", fixedValue);
				theClass.getEStructuralFeatures().add(att);				
			}
			
			// Attributes which are not RIM structural attributes are associations to data type classes
			else
			{
				EClass dTypeClass  = ModelUtil.getEClass(rmimReader.dataTypePackage(),typeName);
				
				/* if any data type cannot be found, replace it by the type 'ANY'
				 * (e.g. GTS  usually cannot be found; 'ANY' will allow you to use IVL_TS, etc.  */
				if (dTypeClass == null)
				{
					rmimReader.missingDataTypes().put(typeName, rmimId);
					dTypeClass  = ModelUtil.getEClass(rmimReader.dataTypePackage(),"ANY");
				}				
				if (dTypeClass != null) // should now always be non-null
				{
					EReference ref = EcoreFactory.eINSTANCE.createEReference();
					ref.setName(attName);
					ref.setEType(dTypeClass);
					ref.setContainment(true);
					if (!attEl.getAttribute("maximumMultiplicity").equals("1"))  ref.setUpperBound(-1);
					if (groupingType)  ref.setUpperBound(-1);
					if (attEl.getAttribute("minimumMultiplicity").equals("1"))  ref.setLowerBound(1);
					if (attEl.getAttribute("minimumMultiplicity").equals("0"))  ref.setLowerBound(0);
					/* if a fixed value is put on an EReference to a data type, we do not yet know 
					 * which attribute of the data type it refers to. For II in NHS template RMIMs , it is usually 'extension'
					 * Use a different annotation key, and decide how to use it later  */
					if (!fixedValue.equals("")) ModelUtil.addMIFAnnotation(ref, "fixed att value", fixedValue);
					theClass.getEStructuralFeatures().add(ref);	
					
					// add multiple fixed values of the data type class which have been found in html in the MIF file
					for (Enumeration<String> en = htmlFixedValues.keys(); en.hasMoreElements();)
					{
						String dtAttName = en.nextElement(); // attribute of the data type class
						// fixed values along some associations are not to be captured from html
						String[] doNotCapture = {"contentId","templateId"};
						if (!GenUtil.inArray(ref.getName(), doNotCapture))
						{
							String fixedVal = htmlFixedValues.get(dtAttName);
							String key = "constraint:" + ref.getName() + "/@" + dtAttName;
							ModelUtil.addMIFAnnotation(theClass,key,fixedVal);							
						}
					}
				}
			}
		}
	}
	
	/**
	 * find fixed values in NHS MIFS, defined in html for the tabular view like:
	 <li>The  XML attribute <b>code </b>shall contain the value "<font color="#ff0000"><b>OA</b> </font>" </li>
      embedded inside an annotation element.
	 * @param attEl
	 * @return
	 */
	private Hashtable<String,String> getHtmlFixedValues(Element attEl)
	{
		Hashtable<String,String> htmlFixedValues = new Hashtable<String,String>();
		Element annotations = XMLUtil.firstNamedChild(attEl,"annotations");
		if (annotations != null)
		{
			Element other = XMLUtil.firstNamedChild(annotations, "otherAnnotation");
			if (other != null)
			{
				Element text = XMLUtil.firstNamedChild(other, "text");
				if (text != null)
				{
					Element listHolder = text;
					// there can be 0, 1, or 2 nested <div> elements
					Element div1 = XMLUtil.firstNamedChild(text, "div");
					if (div1 != null) 
					{
						listHolder = div1;
						Element div2 = XMLUtil.firstNamedChild(div1, "div");
						if (div2 != null) listHolder = div2;
					}
					Element ul = XMLUtil.firstNamedChild(listHolder, "ul");
					if (ul != null)
					{
						Vector<Element> vals = XMLUtil.namedChildElements(ul, "li");
						for (Iterator<Element> it = vals.iterator(); it.hasNext();)
						{
							Element li = it.next();
							
							Vector<Element> bolds = XMLUtil.namedChildElements(li, "b");
							// <font color="#ff0000"> identifies red text
							Vector<Element> reds = XMLUtil.namedChildElements(li, "font");
							if ((bolds.size() > 0) && (reds.size() > 0))
							{
								// attribute name is the first bold text in the lest element
								String attName = XMLUtil.getText(bolds.get(0));
								// sometimes there is a  final space which must be stripped off
								if (attName.endsWith(" ")) attName = attName.substring(0, attName.length()-1);
								
								// fixed values are bold inside the <font> element
								Element redBold = XMLUtil.firstNamedChild(reds.get(0), "b");
								String colour = reds.get(0).getAttribute("color");
								if ((redBold != null) && (colour.equals("#ff0000")))
								{
									String attValue = XMLUtil.getText(redBold);
									htmlFixedValues.put(attName,attValue);										
								}
							}
						}							
					}
				}
			}
		}		
		return htmlFixedValues;		
	}
	
	
	/**
	 * 
	 * @param RIMStructuralAttributeName name of RIM Structural attribute
	 * @return corresponding ECore data type.
	 */
	private EDataType getRIMEcoreDataType(String RIMStructuralAttributeName)
	{
		EDataType aType = EcorePackage.eINSTANCE.getEString();
		// negationInd and other 'Ind' are boolean
		if (RIMStructuralAttributeName.endsWith("Ind")) aType = EcorePackage.eINSTANCE.getEBoolean();
		return aType;		
	}
	
	
	
	/**
	 * 
	 * @param typeEl
	 * @return a simple type name like 'II' or a type name like SET_II
	 */
	private String getUnderScoreTypeName(Element typeEl)
	{
		String fName = typeEl.getAttribute("name");
		for (Iterator<Element> it = XMLUtil.namedChildElements(typeEl, nestedTypeElementName()).iterator();it.hasNext();)
		{
			Element child = it.next();
			fName = fName + "_" + child.getAttribute("name");
		}
		return fName;
	}
	
	/**
	 * @param typeName
	 * @return true if this underscored type name is one of the grouping types,
	 * that refers to multiple occurrences of the grouped type
	 */
	private boolean isGroupingType(String typeName)
	{
		boolean group = false;
		if (typeName.startsWith("LIST_")) group = true;
		if (typeName.startsWith("SET_")) group = true;
		if (typeName.startsWith("BAG_")) group = true;
		return group;
	}
	
	/**
	 * @param typeName
	 * @return any part of the name after the first '_'; or the whole name if there is no '_'
	 */
	private String groupedType(String typeName)
	{
		String type = typeName;
		StringTokenizer st = new StringTokenizer(typeName,"_");
		if (st.countTokens() == 2) {st.nextToken(); type = st.nextToken();}
		return type;
	}

	
	/**
	 * @param el an Element in a MIF file, which has child elements like
	 * <mif:derivedFrom staticModelDerivationId="1" className="Role"/>
	 * with different staticModelDerivationId values
	 * @param id the integer value (here 1) in 'staticModelDerivationId="1" '
	 * @param attName the attribute name (here 'className')
	 * @return the value of the attribute for the element with the given id (here 'Role')
	 */
	private String derivedValue(Element el, int id, String attName)
	{
		String dValue = null;
		for (Iterator<Element> it = XMLUtil.namedChildElements(el,derivationElementName()).iterator();it.hasNext();)
		{
			Element derived = it.next();
			if (derived.getAttribute("staticModelDerivationId").equals(new Integer(id).toString()))
				dValue = derived.getAttribute(attName);
		}
		return dValue;
	}
	//----------------------------------------------------------------------------------------------------
	//                         Reading associations - done in a second pass
	//----------------------------------------------------------------------------------------------------

	/**
	 * ensure this RMIM knows about all its CMET references, as V3Name objects
	 */
	public void linkCMETs()
	{
		// 
		for (Iterator<Element> it = XMLUtil.namedChildElements(rootElement, classElementName()).iterator();it.hasNext();)
		{
			Element contained = it.next();
			Element cmetEl = XMLUtil.firstNamedChild(contained, "commonModelElementRef");
			if (cmetEl != null)
			{
				String CMETName = cmetEl.getAttribute(cmetNameAttribute());
				if (rmimReader.referencedCMETs().get(CMETName) != null)
				{
					V3RMIM theCMET = rmimReader.referencedCMETs().get(CMETName);
					v3Names.put(CMETName, new CMETReference(CMETName, theCMET));
				}
			}
		}		
	}
	
	@SuppressWarnings("unused")
	private void writeV3Names()
	{
		System.out.println("V3Names in " + rmimId + " with entry class " + entryClassName());
		for (Enumeration<V3Name> en = v3Names.elements();en.hasMoreElements();)
		{
			V3Name vn = en.nextElement();
			System.out.println(vn.stringForm());
		}
	}
	
	/**
	 * read and store all the associations in this RMIM or CMET
	 */
	public void readAssociations() throws MapperException
	{
		trace("Doing associations for " + rmimId);
		// if (tracing()) writeV3Names();

		for (Iterator<Element> it = XMLUtil.namedChildElements(rootElement,associationElementName()).iterator();it.hasNext();)
		{
			Element assocEl = it.next();

			Element traversable = getTraversableConnection(assocEl);
			String roleName = traversable.getAttribute("name");
			boolean maxIs1 = true;
			String maxMult = traversable.getAttribute("maximumMultiplicity");
			// all max multiplicity values like 2, 3 etc. or '*' are treated as infinity
			if (!maxMult.equals("1")) maxIs1 = false;
			boolean minIs1 = true;
			if (traversable.getAttribute("minimumMultiplicity").equals("0")) minIs1 = false;

			Element nonTraversable = getNonTraversableConnection(assocEl);
			String owningClassName = nonTraversable.getAttribute("participantClassName");
			// deal with substitutions of entry class names for NHS CDA templates
			if ((owningClassName.equals(entryClassNameInMIF))&& (replacedEntryClassName != null))
				owningClassName = replacedEntryClassName;
			//trace("Owning class: " + owningClassName + "; Role " + roleName);
			V3Name owner = getV3Name(owningClassName);
			
			// the owning V3Name might be a missing CMET
			List<ConcreteClass> owningClasses = new Vector<ConcreteClass>();
			if (owner != null) owningClasses = owner.getAllConcreteClasses();
			else rmimReader.missingClasses().put(owningClassName, rmimId);
			
			if (owningClasses.size() > 0)
			{
				String endV3Name = traversable.getAttribute("participantClassName");
				System.out.println("Outer class: " + owningClassName + " inner: " + endV3Name);
				V3Name innerEnd = getV3Name(endV3Name);
				if (innerEnd == null) rmimReader.missingClasses().put(endV3Name, rmimId);
				else
				{
					// find the choiceItem elements at the next level
					List<Element> choiceEnds = XMLUtil.namedChildElements(traversable, choiceClassElementName());
					// no choices; should resolve to one inner end class
					if (choiceEnds.size() == 0)
					{
						if (innerEnd.nItems() == 1)
						{
							ConcreteClass endClass = null;
							if (innerEnd.getAllConcreteClasses().size() == 1) endClass = innerEnd.getAllConcreteClasses().get(0);
							storeAssociations(owningClasses,roleName,endClass,minIs1,maxIs1);
						}
						else throw new MapperException("Association to " 
								+ innerEnd.name() + " has " + innerEnd.nItems() + " items");
					}
					if (choiceEnds.size() > 0)
						handleChoiceEnds(innerEnd, choiceEnds, owningClasses,minIs1,maxIs1);						
				}

			}			
		}
	}
	
	/**
	 * 
	 * @param choiceEnds
	 * @param owningClasses
	 * @param roleName
	 * @param maxIs1
	 */
	private void handleChoiceEnds(V3Name innerEnd, List<Element> choiceEnds, List<ConcreteClass> owningClasses,
			boolean minIs1, boolean maxIs1)
	throws MapperException
	{
		// for debugging
		@SuppressWarnings("unused")
		String owningClassName = "";
		if (owningClasses.size() == 1) owningClassName = owningClasses.get(0).eClass().getName();

		/* if there is only one choice, then the minimum multiplicity of the association may remain 1.
		 * if there is more than one choice, they must all have min multiplicity 0. */
		boolean trueMinIs1 = minIs1;
		if (choiceEnds.size() > 1) trueMinIs1 = false;

		for (Iterator<Element> ie = choiceEnds.iterator();ie.hasNext();)
		{
			Element choiceEnd = ie.next();
			String roleName = choiceEnd.getAttribute("traversalName");
			String endClassName = choiceEnd.getAttribute("className");
			V3Name endV3Name = innerEnd.getNamedChild(endClassName);
			if (endV3Name == null) {rmimReader.missingClasses().put(endClassName, rmimId);}
			// ignore choices with no choices (abstract classes like 'Template1' in NHS RMIMs)
			else if (endV3Name != null)
			{
				// find the choiceItem elements at the next level
				List<Element> nextChoiceEnds = XMLUtil.namedChildElements(choiceEnd, choiceClassElementName());
				// no choices; should resolve to one inner end class
				if (nextChoiceEnds.size() == 0)
				{
					if (endV3Name.nItems() == 1)
					{
						ConcreteClass endClass = null;
						// in the next line, 'endV3Name' was 'innerEnd' and I don't understand how it could have worked
						if (endV3Name.getAllConcreteClasses().size() == 1) endClass = endV3Name.getAllConcreteClasses().get(0);
						storeAssociations(owningClasses,roleName,endClass,trueMinIs1,maxIs1);
					}
					else System.out.println("Number of inner choices is " + endV3Name.nItems() + " in association to " 
							+ endClassName + " from class " + owningClasses.get(0).name);
				}
				else if (nextChoiceEnds.size() > 0)
					{handleChoiceEnds(endV3Name, nextChoiceEnds, owningClasses,trueMinIs1,maxIs1);	}			
			}
		}
	}
	
	@SuppressWarnings("unused")
	private boolean emptyChoice(V3Name v3Name)
	{
		 return (v3Name.getAllEClasses().size() == 0);
	}
	
	
	/**
	 * store associations in the Ecore model of the RMIM, from one or more
	 * outer (owning) classes to one inner class
	 * @param owningClasses: Vector of  EClasses at the outer end of the association
	 * @param roleName association role name
	 * @param endClass EClass at the inner end of the association
	 * @param isInChoice true if this is inside a choice, so must have lowerBound = 0
	 */
	private void storeAssociations( List<ConcreteClass> owningClasses,String roleName,ConcreteClass endClass,
			boolean minIs1, boolean maxIs1)
	throws MapperException
	{
		for (int i = 0; i < owningClasses.size();i++)
		{
			ConcreteClass owningConcreteClass = owningClasses.get(i);
			EClass owningClass = owningConcreteClass.eClass();
			String owningClassName = owningClass.getName();

			if (endClass == null)  throw new MapperException("Null inner class when storing association from class " + owningClassName);
			String innerClassName = endClass.eClass().getName();
			boolean outerHasTemplates = (owningConcreteClass.templateNames().size() > 0);
			boolean innerHasTemplates = (endClass.templateNames().size() > 0);
			if (outerHasTemplates && innerHasTemplates)
				throw new MapperException("Outer and inner class both have templates in association from class " 
			+ owningClassName + " to class " + innerClassName);

			// normal case for non-NHS RMIMs; neither outer or inner classes have template clones
			if (!outerHasTemplates && !innerHasTemplates)
			{
				EClass ownedClass = endClass.eClass();
				addOneAssociation(owningClass,ownedClass, roleName, null, minIs1,maxIs1);
			}
			
			/* NHS RMIMs; case where the inner class has template clones. Make one association
			 * from the outer class to each template clone, with a special role name */
			else if (innerHasTemplates) 
			{
				for (Iterator<String> it = endClass.templateNames().iterator();it.hasNext();)
				{
					String templateName = it.next();
					EClass oneOwnedClass = endClass.getTemplateClone(templateName);

					// this role name is introduced by me, and the wrapper transform needs to remove it on the way out
					String oneRoleName = roleName + "_" + endClass.getTemplateNameSuffix(templateName);

					// if there is more than one template clone class (a choice) each one must have min multiplicity 0
					boolean min1 = minIs1;
					if (endClass.templateNames().size() > 1) min1 = false;

					addOneAssociation(owningClass,oneOwnedClass, oneRoleName, roleName, min1,maxIs1);				
				}
			}


			/* NHS RMIMs; case where the outer class has template clones. Make one 
			 * association from each of the template clones of the outer class to each of the the entry classes
			 * of its template RMIM, with role name as in the NHS templated form */
			else if (outerHasTemplates)
				for (Iterator<String> iu = owningConcreteClass.templateNames().iterator();iu.hasNext();)
			{
				String templateName = iu.next();
				V3RMIM templateRMIM = rmimReader.getReferencedCMET(templateName);
				if (templateRMIM == null) throw new MapperException("Cannot find template RMIM '" + templateName + "'");

				EClass oneOwnerClass = owningConcreteClass.getTemplateClone(templateName);
				if (oneOwnerClass == null) trace("No template clone class for template '"  + templateName
						+ "' of class " + owningClassName);
				else for (Iterator<EClass> ic = templateRMIM.getEntryV3Name().getAllEClasses().iterator();ic.hasNext();)
				{
					EClass oneOwnedClass = ic.next();
					/* tag name used in NHS templated form has a '.' in it; 
					 * I use it as an association role name to get the same tag name and so keep the wrapper transform simple */
					String oneRoleName = templateName + "." + oneOwnedClass.getName();
					addOneAssociation(oneOwnerClass,oneOwnedClass, oneRoleName, roleName, minIs1,maxIs1);				
				}				
			}
		}
	}
	
	/**
	 * add one association to the ecore model - but ensure that there is only one EReference with 
	 * a given role name
	 * @param owningClass
	 * @param ownedClass
	 * @param roleName
	 * @param originalRoleName if NHS templates have changed the role name, 
	 * record the original name in an annotation
	 * @param minIs1
	 * @param maxIs1
	 */
	private void addOneAssociation(EClass owningClass, EClass ownedClass, String roleName,String originalRoleName,boolean minIs1,boolean maxIs1)
	{
		if (ModelUtil.getReferencedClass(owningClass, roleName) == null)
		{
			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setName(roleName);
			if (originalRoleName != null)
				ModelUtil.addMIFAnnotation(ref, "NHSOriginalRole", originalRoleName);
			ref.setEType(ownedClass);
			ref.setContainment(true);
			if (minIs1) ref.setLowerBound(1); else ref.setLowerBound(0);
			if (maxIs1) ref.setUpperBound(1); else ref.setUpperBound(-1);
			owningClass.getEStructuralFeatures().add(ref);									
		}
		else
		{
			String message = "Duplicate association '" + roleName + "' from class '" 
			+ owningClass.getName() + "' has not been stored.";
			WorkBenchUtil.showMessage("Warning", message);
			System.out.println(message);
		}
	}

	
	
	/**
	 * record that a class with this name occurs in one or more CMETs
	 * @param className
	 */
	private void recordClassOccurrence(String className)
	{
		Vector<String> occurrences = rmimReader.classOccurrences().get(className);
		if (occurrences == null) occurrences = new Vector<String>();
		occurrences.add(rmimId);
		rmimReader.classOccurrences().put(className, occurrences);		
	}
	
	/**
	 * list the packages which a class has occurred in
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unused")
	private String packages(String className)
	{
		String packages = "";
		Vector<String> occurrences = rmimReader.classOccurrences().get(className);
		if (occurrences != null) for (int i = 0; i < occurrences.size(); i++)
			packages  = packages + occurrences.get(i) + " ";
		return packages;
	}
	
	//------------------------------------------------------------------------------------------
	//                             InfrastructureRoot attributes
	//------------------------------------------------------------------------------------------
	
	/**
	 * Add the four infrastructure root attributes to all classes in the RMIM.
	 * This is done last to make them show last in the RMIM class view.
	 */
	public void addInfrastructureRootAttributes()
	{
		for (Iterator<EClassifier> it = RMIMPackage.getEClassifiers().iterator();it.hasNext();)
		{
			EClassifier ec = it.next();
			if (ec instanceof EClass)
			{
				EClass theClass = (EClass)ec;

				// nullFlavor is an attribute in the Ecore model
				EAttribute att = EcoreFactory.eINSTANCE.createEAttribute();
				att.setName("nullFlavor");
				att.setEType(EcorePackage.eINSTANCE.getEString());
				att.setLowerBound(0);
				theClass.getEStructuralFeatures().add(att);	
				
				/* other InfrastructureRoot attributes which are associations to data type classes. 
				 * The NHS MIFs add typeId and templateId explicitly when they want them - don't add them again.  */
				if (!rmimReader.isNHSMIF())
				{
					addRootAttribute(theClass,"realmCode","CS",true);
					addRootAttribute(theClass,"typeID","II",false);
					addRootAttribute(theClass,"templateId","II",true);					
				}
			}
		}
	}
	
	/**
	 * 
	 * @param theClass EClass to which the attribute is added
	 * @param attName name of the infrastructure root attribute
	 * @param typeName its data type name
	 * @param canRepeat true if its multiplicity is unbounded
	 */
	private void addRootAttribute(EClass theClass, String attName, String typeName, boolean canRepeat)
	{
		EClass dTypeClass  = ModelUtil.getEClass(rmimReader.dataTypePackage(),typeName);
		if (dTypeClass != null)
		{
			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setName(attName);
			ref.setEType(dTypeClass);
			ref.setContainment(true);
			if (canRepeat)  ref.setUpperBound(-1);
			ref.setLowerBound(0);
			theClass.getEStructuralFeatures().add(ref);									
		}
	}
	
	//------------------------------------------------------------------------------------------
	//                             differences between MIF versions
	//------------------------------------------------------------------------------------------
	
	private String cmetNameAttribute()
	{
		int version = rmimReader.mifVersion();
		if (version == RMIMReader.MIF_2_0) return "name";
		if (version == RMIMReader.MIF_2_1) return "name";
		if (version == RMIMReader.MIF_2_1_3) return "cmetName";
		if (version == RMIMReader.MIF_2_1_4) return "cmetName";
		if (version == RMIMReader.MIF_2_1_5) return "cmetName";
		if (version == RMIMReader.MIF_2_1_6) return "cmetName";
		return "";
	}
	
	private String choiceClassElementName()
	{
		int version = rmimReader.mifVersion();
		if (version == RMIMReader.MIF_2_0) return "participantClassSpecialization";
		if (rmimReader.isNHSMIF()) return "participantClassSpecialization";
		if (version == RMIMReader.MIF_2_1) return "choiceItem";
		if (version == RMIMReader.MIF_2_1_3) return "choiceItem";
		if (version == RMIMReader.MIF_2_1_4) return "choiceItem";
		if (version == RMIMReader.MIF_2_1_5) return "choiceItem";
		if (version == RMIMReader.MIF_2_1_6) return "choiceItem";
		return "";
	}
	
	private String choiceChildClassElementName()
	{
		if (rmimReader.isNHSMIF()) return "specializationChild";
		return "childClass";
	}
	
	private String choiceClassNameAttribute()
	{
		if (rmimReader.isNHSMIF()) return "childClassName";
		int version = rmimReader.mifVersion();
		if (version == RMIMReader.MIF_2_0) return "className";
		if (version == RMIMReader.MIF_2_1) return "name";
		if (version == RMIMReader.MIF_2_1_3) return "name";
		if (version == RMIMReader.MIF_2_1_4) return "name";
		if (version == RMIMReader.MIF_2_1_5) return "name";
		if (version == RMIMReader.MIF_2_1_6) return "name";
		return "";
	}
	
	/**
	 * @return inner tag name for compound types
	 */
	private String nestedTypeElementName()
	{
		if (rmimReader.isNHSMIF()) return "supplierBindingArgumentDatatype";
		return "argumentDatatype";		
	}
	
	private String vocabularyElementName()
	{
		int version = rmimReader.mifVersion();
		if (version == RMIMReader.MIF_2_0) return "supplierVocabulary";
		if (version == RMIMReader.MIF_2_1) return "supplierVocabulary";
		if (version == RMIMReader.MIF_2_1_3) return "vocabulary";
		if (version == RMIMReader.MIF_2_1_4) return "vocabulary";
		if (version == RMIMReader.MIF_2_1_5) return "vocabulary";
		if (version == RMIMReader.MIF_2_1_6) return "vocabulary";
		return "";
	}

	private Element getTraversableConnection(Element assocEl)
	{
		int version = rmimReader.mifVersion();
		Element traversable = XMLUtil.firstNamedChild(assocEl, "traversableConnection");
		// only MIF 2.1 has an intermediate <connections> element
		if (version == RMIMReader.MIF_2_1)
		{
			Element connections = XMLUtil.firstNamedChild(assocEl, "connections");
			traversable = XMLUtil.firstNamedChild(connections, "traversableConnection");			
		}
		return traversable;
	}

	private Element getNonTraversableConnection(Element assocEl)
	{
		int version = rmimReader.mifVersion();
		Element nonTraversable = XMLUtil.firstNamedChild(assocEl, "nonTraversableConnection");
		// only MIF 2.1 has an intermediate <connections> element
		if (version == RMIMReader.MIF_2_1)
		{
			Element connections = XMLUtil.firstNamedChild(assocEl, "connections");
			nonTraversable = XMLUtil.firstNamedChild(connections, "nonTraversableConnection");			
		}
		return nonTraversable;
	}

	
	private String classElementName()
	{
		String classElementName = "containedClass";
		if (rmimReader.isNHSMIF()) classElementName = "ownedClass";
		return classElementName;
	}
	
	private String associationElementName()
	{
		String associationElementName = "association";
		if (rmimReader.isNHSMIF()) associationElementName = "ownedAssociation";
		return associationElementName;
	}
	
	private String derivationElementName()
	{
		String derivationElementName= "derivedFrom";
		if (rmimReader.isNHSMIF()) derivationElementName = "derivationSupplier";
		return derivationElementName;
	}

	//---------------------------------------------------------------------------------------------------
	//                                         Trivia
	//---------------------------------------------------------------------------------------------------

	private boolean tracing() 
	{
		return  (rmimReader.tracing());
		//return ((rmimId != null) && (rmimId.equals("COCD_TP146065UK02")));
	}
	
	private void trace(String s) {if (tracing()) System.out.println(s);}

}


