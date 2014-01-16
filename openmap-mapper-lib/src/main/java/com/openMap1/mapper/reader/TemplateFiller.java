package com.openMap1.mapper.reader;

/**
 * class to fill out template Word documents saved as XML (and other XML structures)
 * from an Ecore model instance before editing,
 * and conversely after editing to populate an Ecore model instance
 * from the edited Word XML
 */

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.SystemMessageChannel;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.writer.EMFObjectGetter;
import com.openMap1.mapper.writer.XMLWriter;
import com.openMap1.mapper.writer.objectGetter;


public class TemplateFiller {
	
	// template  Word document, annotated with symbols beginning with a special character String
	private Element templateRoot;
	
	// distinctive character string at the start of any symbol
	private String startString;
	
	// lookup table from symbols to paths in the Ecore model
	private Hashtable<String,String> lookupTable;
	
	// Ecore class model
	private EPackage classModel;
	
	// entry class of class model (which is an RMIM-like tree of containment associations)
	private EClass entryClass;
	
	// ECore EObject with pre-edit information
	private EObject preEditObject;
	
	// input mapping set, to create pre-edit Ecore model from some XML
	private MappedStructure inputMappingSet;
	
	// true when the XML is MS Word which needs special treatment
	private boolean isMSWord;
	
	// path at which to put any non-XML body
	private String nonXMLBodyPath = "ClinicalDocument/component/nonXMLBody/text";
	
	
	static String NO_SYMBOL = "£$No symbol";
	static String SYMBOL_PATH_NOT_IN_INSTANCE = "£$symbol path not found";
	static String EDIT_PROMPT = "-";
	

	
	//-------------------------------------------------------------------------------------------------------
	//                              Constructor and initial checks
	//-------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param templateRoot
	 * @param lookup
	 * @param inputMappingSet
	 * @param startString
	 * @param isMSWord
	 * @throws MapperException
	 */
	public TemplateFiller(Element templateRoot,
			Vector<String[]> lookup, 
			MappedStructure inputMappingSet, 
			String startString,
			boolean isMSWord)  throws MapperException
	{
		this.templateRoot = templateRoot;
		this.startString = startString;
		this.inputMappingSet = inputMappingSet;
		this.isMSWord = isMSWord;
		preEditObject = null;
		
		classModel = inputMappingSet.getClassModelRoot();
		
		// find the entry class of the class model
		findEntryClass();
		
		// read the lookup csv into a table, and check all paths in the table exist in the class model
		makeLookupTable(lookup);
		
		// check that every symbol in the template Word XML is in the lookup table
		checkSymbol(this.templateRoot);
	}
	
	/**
	 * 
	 * @param templateRoot
	 * @param lookup
	 * @param classModel
	 * @param startString
	 * @throws MapperException
	 */
	public TemplateFiller(Element templateRoot,Vector<String[]> lookup, EPackage classModel, String startString)  throws MapperException
	{
		this.templateRoot = templateRoot;
		this.startString = startString;
		this.classModel = classModel;
		preEditObject = null;
		
		inputMappingSet = null;
		
		// find the entry class of the class model
		findEntryClass();
		
		// read the lookup csv into a table, and check all paths in the table exist in the class model
		makeLookupTable(lookup);
		
		// check that every symbol in the Word XML is in the lookup table
		checkSymbol(this.templateRoot);
	}
	
	/**
	 * find the entry class of the class model
	 * @throws MapperException
	 */
	private void findEntryClass()  throws MapperException
	{
		entryClass = null;
		for (Iterator<EClass> it = ModelUtil.getAllClasses(classModel).iterator();it.hasNext();)
		{
			EClass next = it.next();
			String entry = ModelUtil.getMIFAnnotation(next, "entry");
			if (entry != null) entryClass = next;
		}
		if (entryClass == null)  throw new MapperException("Class model has no entry class");
	}

	
	/**
	 * 
	 * @param lookup
	 * @throws MapperException
	 */
	private void makeLookupTable(Vector<String[]> lookup) throws MapperException
	{
		lookupTable  = new Hashtable<String,String>(); 
		for (int i = 1; i < lookup.size(); i++)
		{
			String[] row = lookup.get(i);
			if (row.length != 2) throw new MapperException("Lookup table row length should not be " + row.length);
			if (!(isSyntacticSymbol(row[0]))) 
				throw new MapperException("Symbol '" + row[0] + "' should start with '" + startString + "'");
			checkValidPath(row[1]);
			
			lookupTable.put(row[0], row[1]);
		}
	}
	
	/**
	 * check that a path is a valid path in the class model.
	 * Does not yet check conditions on steps.
	 * @param path
	 */
	private void checkValidPath(String path) throws MapperException
	{
		String[] steps = pathSteps(path);
		// writeSteps(steps, path);
		int stepNo = 0;
		EClass currentClass = entryClass;
		int nSteps = steps.length;
		for (int ist = 0; ist < nSteps; ist++)
		{
			String step = steps[ist];
			if (stepNo == 0)
			{
				if (!(step.equals(entryClass.getName()))) 
					throw new MapperException("Path '" + path + "' does not start with the root class name '" + entryClass.getName() + "'");
			}
			else if (stepNo > 0)
			{
				StringTokenizer bits = new StringTokenizer(step,"[]");
				String stem = bits.nextToken();
				EStructuralFeature feat = currentClass.getEStructuralFeature(stem);
				if (feat == null) throw new MapperException("Path '" + path + "' does not match the class model at step '" + stem + "'");
				if (feat instanceof EReference)
				{
					if (stepNo == nSteps - 1) throw new MapperException("Path '" + path + "' cannot end in an EReference " + stem);
					EReference ref = (EReference) feat;
					currentClass = (EClass)ref.getEType();
				}
				else if (feat instanceof EAttribute)
				{
					if (stepNo < nSteps - 1) 
						throw new MapperException("Attribute '" + stem + "' is before the end of path '" + path + "'");
				}
			}
			stepNo++;
		}
	}
	
	
	/**
	 * recursive check that all symbols (which satisfy the syntactic criteria for a symbol) are in the lookup table
	 * @param el
	 * @throws MapperException if any symbol is not in the lookup table
	 */
	private void checkSymbol(Element el) throws MapperException
	{
		String content = removeSurroundingSpaces(getTextOnNode(el));
		if ((isSyntacticSymbol(content)) && (lookupTable.get(content) == null)) 
			throw new MapperException("Word XML contains symbol '" + content + "' which is not in the lookup table");

		for (Iterator<Element> it = XMLUtil.childElements(el).iterator();it.hasNext();)
			checkSymbol(it.next());
	}
	
	/**
	 * 
	 * @param content
	 * @return true if  content satisfies the syntactic criteria for a symbol:
	 *  - starts with the start string (e.g. '$')
	 *  - has some following characters
	 */
	private boolean isSyntacticSymbol(String content)
	{
		return ((content != null) && (content.startsWith(startString)) && (content.length() > startString.length()));
	}
	
	
	/**
	 * remove preceding and succeeding spaces from a single-word string,
	 * in case symbols have had spaces added inadvertently
	 * @param sym
	 * @return
	 */
	private String removeSurroundingSpaces(String sym)
	{
		String s = "";
		StringTokenizer st = new StringTokenizer(sym," ");
		if (st.countTokens() == 1) s = st.nextToken();
		return s;
	}
	
	/**
	 * 
	 * @param el
	 * @return the text under a node, taking account of how MS Word may split up text;
	 * cannot return null
	 */
	private String getTextOnNode(Element el)
	{
		String paraText = "";
		
		// MS Word may split text under a <w:p> node; need to reassemble it before testing for a symbol 
		if (isMSWord)
		{
			paraText = reassembleText(el);
		}
		// other XML representations
		else
		{
			paraText = XMLUtil.getText(el);
		}
		if (paraText == null) paraText = "";
		return paraText;
		
	}
	
	/**
	 * reassemble text under an MS Word <w:p> element, 
	 * which may have been split up e.g. to highlight spelling errors
	 * @param para
	 * @return concatenated text under all <w:r> elements;
	 * or "" if this is not a <w:p> element
	 */
	private  String reassembleText(Element para)
	{
		String paraText = "";
		if (para.getLocalName().equals("p"))
		{
			Vector<Element> rEls = XMLUtil.namedChildElements(para, "r");
			for (int i = 0; i < rEls.size();i++)
			{
				Element tEl = XMLUtil.firstNamedChild(rEls.get(i), "t");
				if (tEl != null) paraText = paraText + XMLUtil.getText(tEl);
			}
		}				
		return paraText;
	}


	
	//----------------------------------------------------------------------------------------------------------------------
	//      Substituting symbols in a Word XML from a class model instance or XML instance, to make the pre-edit word XML
	//----------------------------------------------------------------------------------------------------------------------
	
	/**
	 * populate the the pre-edit word xml from a mapped XML instance
	 * @param inputRoot
	 * @return
	 * @throws MapperException
	 */
	public Element fillFromInputXML(Element inputRoot)  throws MapperException
	{
		setPreEditEcoreInstance(inputRoot);
		return fillFromPreEditInstance();
	}
	
	/**
	 * create the Green model instance preEditObject from an instance of some mapped data source
	 * @param inputRoot
	 * @throws MapperException
	 */
	public void setPreEditEcoreInstance(Element inputRoot) throws MapperException
	{
		if (inputMappingSet == null) throw new MapperException("No input mapping set for TemplateFiller");
		XOReader reader = new MDLXOReader(inputRoot, inputMappingSet, null);
		Vector<objectToken> topObjectTokens = reader.getAllObjectTokens(ModelUtil.getQualifiedClassName(entryClass));
		
		if (topObjectTokens.size() != 1) throw new MapperException("Input XML represents " 
				+ topObjectTokens.size() + " objects of the entry class " + entryClass.getName());
		
		EMFInstanceFactory factory = new GenericEMFInstanceFactoryImpl();
		// create the Ecore instance with the special URI so it is not saved anywhere
		Resource res = factory.createModelInstance(reader, EMFInstanceFactoryImpl.DO_NOT_SAVE_URI(), topObjectTokens.get(0));
		preEditObject = res.getContents().get(0);		
	}
	
	
	/**
	 * populate the pre-edit word xml from an EMF Ecore instance
	 * whenever you find a symbol in the XML document, try to follow the path for the symbol in the 
	 * model instance, and if you can, substitute the value.
	 * Otherwise substitute a small editing symbol like '-'
	 * @return root element of modified pre-edit Word XML document
	 * @throws MapperException
	 */
	public Element fillFromPreEditInstance() throws MapperException
	{
		Document doc = XMLUtil.makeOutDoc();
		
		// import the whole word XML to another document, and word still reads it
		Element preEditRoot = (Element)doc.importNode(templateRoot, true);
		doc.appendChild(preEditRoot);

		// change text values wherever a symbol has a value in the pre-edit instance
		changeTextFromModel(preEditRoot, preEditObject);
		
		return preEditRoot;
	}

	/**
	 * recursive descent of the template word xml, creating the pre-edit word xml
	 * @param doc
	 * @param el
	 * @param instance
	 * @return
	 * @throws MapperException
	 */
	private void changeTextFromModel(Element el, EObject instance)  throws MapperException
	{
		String newText = NO_SYMBOL;
		String symbol = getSymbolOnNode(el);
		if (symbol != null) newText = findSubstituteString(symbol, instance);
		// now newText cannot be null
		
		// no symbol found  - make no change to the element
		if (newText.equals(NO_SYMBOL)) {}
		// symbol found, but a value for it is not in the instance - replace it by the edit prompt
		else if (newText.equals(SYMBOL_PATH_NOT_IN_INSTANCE)) {replaceOneSymbol(el, EDIT_PROMPT);}
		// symbol found, and a value for it found in the instance; substitute the value
		else {replaceOneSymbol(el,newText);}
		
		// recursion through child elements
		for (Iterator<Element> it = XMLUtil.childElements(el).iterator();it.hasNext();)
			changeTextFromModel(it.next(), instance);
	}
	
	/**
	 * 
	 * @param el
	 * @return
	 */
	private String getSymbolOnNode(Element el)
	{
		String symbol = null;
		String paraText = removeSurroundingSpaces(getTextOnNode(el));
		
		if (lookupTable.get(paraText) != null) symbol = paraText;
		return symbol;
	}
 
	/**
	 * 
	 * @param el
	 * @param replacement
	 */
	private void replaceOneSymbol(Element el, String replacement) throws MapperException
	{
		/* For MS Word, this must be a <w:p> element. Remove all its child elements, 
		 * and replace them by one <w:r><w:t> nested element pair with the new text */
		if (isMSWord)
		{
			NodeList nl = el.getChildNodes();
			int len = nl.getLength();
			// remove nodes in descending order, not re-evaluating the list length
			for (int i = 0; i < len; i++) 
			{
				int j = len - i - 1;
				el.removeChild(nl.item(j));
			}

			String uri = el.getNamespaceURI(); // these elements are all in the 'w' namespace			
			Document doc = el.getOwnerDocument();
			Element rChild = XMLUtil.NSElement(doc, "w", "r", uri);
			Element tChild = XMLUtil.textNSElement(doc, "w", "t", uri, replacement);
			rChild.appendChild(tChild);
			el.appendChild(rChild);
		}
		else
		{
			throw new MapperException("Non MS Word case not yet supported");
		}
	}
	
	/**
	 * substitute the value from an Ecore model instance, if the path for a symbol can be followed;
	 * if not, return the distinctive value SYMBOL_PATH_NOT_IN_INSTANCE.
	 * Never return null.
	 * @param symbol
	 * @param instance
	 * @return
	 */
	private String findSubstituteString(String symbol, EObject instance) throws MapperException
	{
		// result returned if you fail to follow the path 
		String newText = SYMBOL_PATH_NOT_IN_INSTANCE;
		
		String path = lookupTable.get(symbol); // path cannot be null, and must be a valid path
		String[] steps = pathSteps(path);
		
		String firstStep = steps[0];
		String instanceClass = instance.eClass().getName();
		// this will have been detected earlier
		if (!firstStep.equals(entryClass.getName())) throw new MapperException("Invalid first step of path");
		if (!instanceClass.equals(firstStep)) 
			throw new MapperException("Model instance has entry class " + instanceClass);
		
		// follow the path in the instance
		EObject current = instance;
		for (int st = 1; st < steps.length; st++) if (current != null)
		{
			String step = steps[st];
			EStructuralFeature feat = getFeature(current, step);
			if (feat instanceof EReference)
			{
				// set the current object to the next along the path if you can follow it, or null otherwise
				current = followRef(current, step);
			}
			// last link in the path must be an EAttribute to get a value
			else if (feat instanceof EAttribute) 
			{
				String attVal  = (String)current.eGet(feat);
				if (attVal != null) newText = attVal;
			}
		}
		
		return newText;
	}
	
	/**
	 * @param instance
	 * @param step
	 * @return the EAttribute or ERefererence in a step
	 * @throws MapperException
	 */
	private EStructuralFeature getFeature(EObject instance, String step) throws MapperException
	{
		StringTokenizer st = new StringTokenizer(step,"[]");
		String stem = st.nextToken();
		EStructuralFeature feat = instance.eClass().getEStructuralFeature(stem);
		if (feat == null) throw new MapperException("Missing feature in step '" + step + "'");
		return feat;
	}
	
	/**
	 * Follow an EReference from the current object.
	 * If it delivers an EObject which passes the tests in the step, return that EObject,
	 * Otherwise return null
	 * @param current
	 * @param ref
	 * @param step
	 * @return
	 */
	private EObject followRef(EObject current, String step) throws MapperException
	{
		EObject res = null;
		EReference ref = (EReference)getFeature(current, step);
		if (ref.getUpperBound() == 1)
		{
			EObject obj = (EObject)current.eGet(ref);
			if (testConditions(step,obj,0)) res = obj;
		}
		// multiple EReference; if any of the target objects satisfy all the tests, set the result to that; null otherwise
		else if (ref.getUpperBound() == -1)
		{
			Object  obj = current.eGet(ref);
			if (obj instanceof EList<?>) 
			{
				EList<?> listRef = (EList<?>)obj;
				int pos = 0;
				for (Iterator<?> it = listRef.iterator();it.hasNext();)
				{
					Object next = it.next();
					if (next instanceof EObject)
					{
						if (testConditions(step,(EObject)next,pos)) res = (EObject)next;
					}
					else throw new MapperException("Multiple reference result is not an EList of EObjects");
					pos++;
				}
			}
			else throw new MapperException("Multiple reference result is not an EList of anything");
		}
		return res;
	}

	/**
	 * test a set of conditions on an EObject
	 * @param step
	 * @param obj
	 * @param pos
	 * @return
	 */
	private boolean testConditions(String step, EObject obj, int pos) throws MapperException
	{
		boolean result = true;
		StringTokenizer parts = new StringTokenizer(step,"[]");
		parts.nextToken(); // stem has already been dealt with
		while (parts.hasMoreTokens())
		{
			String cond = parts.nextToken();
			result = result && testOneCondition(cond,obj,pos);
		}		
		return result;
	}
	
	/**
	 * 
	 * @param cond
	 * @param obj
	 * @param pos
	 * @return
	 */
	private boolean testOneCondition(String cond, EObject obj, int pos) throws MapperException
	{
		boolean result = true;
		
		StringTokenizer sides = new StringTokenizer(cond,"= ");
		// no equality condition; can only be an Integer test of the position
		if (sides.countTokens() == 1)
		{
			try
			{
				Integer iv = new Integer(cond);
				result = (iv.intValue() == pos);
			}
			catch (Exception ex) {throw new MapperException("Condition value '" + cond + "' is not an Integer position");}
		}
			
		// equality condition; each side can be an attribute value or a constant
		else if (sides.countTokens() == 2)
		{
			String[] values = new String[2];
			for (int side = 0; side < 2; side++)
			{
				String toEval = sides.nextToken();
				// constant in single quotes; strip them off
				if ((toEval.startsWith("'")) && (toEval.endsWith("'")))
					values[side] = toEval.substring(1,toEval.length()-1);
				else
				{
					EStructuralFeature feat = obj.eClass().getEStructuralFeature(toEval);
					if (feat == null) throw new MapperException("Condition side '" + toEval + "' is not a feature of the owner object");
					if (feat instanceof EReference) throw new MapperException("Condition side '" + toEval + "' is an association");
					values[side] = (String)obj.eGet(feat);
				}
			}
			result = ((values[0] != null) && (values[1] != null) && (values[0].equals(values[1])));
		}
		else throw new MapperException("Too many '=' in condition '" + cond + "'");
		
		return result;
	}

	
	//-------------------------------------------------------------------------------------------------------
	//       Putting values from the post-edit word XML into the model instance or an output XML instance
	//-------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param postEditRoot root element of word XML, after editing
	 * @param outputMappingSet
	 * @param nonXMLBody e.g. a base-64 encoded pdf
	 * @return
	 * @throws MapperException
	 */
	public Element makeDOMFromPostEditXML(Element postEditRoot, MappedStructure outputMappingSet, String nonXMLBody)  throws MapperException
	{
		// make an EObject from the post-edit XML
		EObject result = makeEObjectFromPostEditXML(postEditRoot);

		// generate output XML from the extended Ecore object
		objectGetter eOGetter = new EMFObjectGetter(classModel,result);
		XMLWriter writer = outputMappingSet.getXMLWriter(eOGetter, classModel, new SystemMessageChannel(), false);
		
		Element inWrappedResult = writer.makeXMLDOM();
		
		Object outWrappedDoc = outputMappingSet.makeOutputObject(inWrappedResult, null);
		if (!(outWrappedDoc instanceof Document)) 
			throw new MapperException("Out-wrapped result is not an XML document");
		
		Element rootElement  = ((Document)outWrappedDoc).getDocumentElement();
		
		if (nonXMLBody != null) addBody(rootElement,nonXMLBody);

		return rootElement;
	}
	
	/**
	 * add a non-xml body, as base64 encoded text, at the end of the correct path in the non-coded CDA
	 * @param rootElement
	 * @param nonXMLBody
	 * @throws MapperException
	 */
	private void addBody(Element rootElement, String nonXMLBody) throws MapperException
	{
		Element current = rootElement;
		Document doc = rootElement.getOwnerDocument();
		String v3NamespaceURI = rootElement.getNamespaceURI();
		StringTokenizer steps = new StringTokenizer(nonXMLBodyPath,"/");

		// the root element tag name must be the first step of the path to the non XML body
		String rootTag = steps.nextToken();
		if (!rootTag.equals(rootElement.getLocalName()))
			throw new MapperException("Root element name " + rootElement.getLocalName() + " does not match '" + rootTag + "'");

		// follow the path to the non XML body, adding elements if you do not find them
		while (steps.hasMoreTokens())
		{
			String step = steps.nextToken();
			Element next = XMLUtil.firstNamedChild(current, step);
			if (next == null)
			{
				next = XMLUtil.NSElement(doc,"", step, v3NamespaceURI);
				current.appendChild(next);
			}
			current = next;
		}
		
		// add the non-XML body to the last element in the chain
		Text textContent  = doc.createTextNode(nonXMLBody);
		current.appendChild(textContent);
	}
	
	/**
	 * @param postEditRoot
	 * @return an EObject made from the post-edit XML - 
	 * possible merging it with the pre-Edit EObject 
	 * (for cases where not all information in the pre-edit Ecore object is put in the pre-edit XML)
	 */
	public EObject makeEObjectFromPostEditXML(Element postEditRoot)  throws MapperException
	{
		// if no pre-edit Ecore object has been made, make the minimal one
		if (preEditObject == null) preEditObject = createModelObject(entryClass);

		// clone the pre-edit Ecore object, so you can extend the clone from the post-edit XML
		EObject result = cloneEObject(preEditObject);

		// extend or modify the cloned pre-edit object from post-edit XML
		addFromPostEditXML(postEditRoot,templateRoot, result);
		
		return result;
	}
	
	
	/**
	 * recursive descent of the template XML and the edited XML, 
	 * making additions to the result EObject wherever an edit has been made
	 * @param postEditEl
	 * @param templateEl
	 * @param result
	 * @throws MapperException
	 */
	private void addFromPostEditXML(Element postEditEl,Element templateEl, EObject result)  throws MapperException
	{
		// deal with any edit on this node, if this node in the template contains a symbol
		String symbol = getTextOnNode(templateEl);
		if (isSyntacticSymbol(symbol))
		{
			String path = lookupTable.get(symbol);
			String editedValue = getTextOnNode(postEditEl);
			// if the edit prompt is still present, no edit has been made (not quite reliable, without looking at the pre-edit node!)
			if (!editedValue.equals(EDIT_PROMPT)) addEditedValue(editedValue,path,result);
		}
		
		// recursive descent of word XML trees; currently makes strong assumptions that they match, or edits may be silently lost
		Vector<Element> templateEls = XMLUtil.childElements(templateEl);
		Vector<Element> postEditEls = XMLUtil.childElements(postEditEl);
		
		 if (templateEls.size() == postEditEls.size())
			 for (int i = 0; i < templateEls.size(); i++) if (i < postEditEls.size())
		{			
			Element templateChild = templateEls.get(i);
			Element postEditChild = postEditEls.get(i);
			if (templateChild.getLocalName().equals(postEditChild.getLocalName()))
			{
				addFromPostEditXML(postEditChild,templateChild, result);
			}
		}
	}
	
	/**
	 * navigate and/or extend the result EObject to add or change an edited value
	 * @param editedValue
	 * @param path
	 * @param result
	 */
	private void addEditedValue(String editedValue,String path,EObject result) throws MapperException
	{
		String[] steps = pathSteps(path);
		EObject current = result;
		for (int ist = 1; ist < steps.length; ist++)
		{
			String step = steps[ist];
			StringTokenizer parts = new StringTokenizer(step,"[]");
			String stem = parts.nextToken();
			EStructuralFeature feat = getFeature(current, stem);
			if (feat == null) throw new MapperException("Cannot find feature '" + stem + "' in the class model.");
			if (feat instanceof EReference)
			{
				EObject nextObj = followRef(current, step);
				// if you cannot find an EObject for this step of the path, make one and add it to the current object feature
				if (nextObj == null)
				{
					EClass nextClass = (EClass)((EReference)feat).getEType();
					nextObj = createModelObject(nextClass);
					addEObject(current,nextObj,(EReference)feat);
				}
				current = nextObj;
			}
			else if (feat instanceof EAttribute)
			{
				current.eSet(feat, editedValue);
			}
		}
	}
	
	/**
	 * 
	 * @param theClass
	 * @return
	 */
	private EObject createModelObject(EClass theClass)
	{
		EPackage thePackage = theClass.getEPackage();
		return thePackage.getEFactoryInstance().create(theClass);
	}
	
	/**
	 * add an EObject as a child on a feature of a parent
	 * @param parent
	 * @param child
	 * @param ref
	 */
	private void addEObject(EObject parent, EObject child, EReference ref)
	{
		if (ref.getUpperBound() == 1)
		{
			parent.eSet(ref, child);
		}
		else if (ref.getUpperBound() == -1)
		{
			EList<EObject> objs = (EList<EObject>)parent.eGet(ref);
			if (objs == null) objs = new BasicEList<EObject>();
			objs.add(child);
			parent.eSet(ref, objs);
		}
	}
	
	/**
	 * clone an EObject
	 * @param start
	 * @return
	 */
	private EObject cloneEObject(EObject start)  throws MapperException
	{
		EObject root = createModelObject(entryClass);
		extendClonedObject(root, start);
		return root;
	}
	
	/**
	 * recursive cloning of an EObject - extend an empty EObject to have the same sub-structure as the start object
	 * @param resultObj an empty EObject
	 * @param startObj
	 */
	private void extendClonedObject(EObject resultObj, EObject startObj) throws MapperException
	{
		EClass resultClass = resultObj.eClass();
		for (Iterator<EStructuralFeature> it = resultClass.getEAllStructuralFeatures().iterator();it.hasNext();)
		{
			EStructuralFeature feat = it.next();
			if (feat instanceof EAttribute)
			{
				if (startObj.eGet(feat) != null) resultObj.eSet(feat, startObj.eGet(feat));
			}
			if (feat instanceof EReference)
			{
				EReference ref = (EReference)feat;
				if (ref.getUpperBound() == 1)
				{
					EObject target = (EObject)startObj.eGet(ref);
					if (target != null)
					{
						EObject resultTarget = createModelObject(target.eClass());
						extendClonedObject(resultTarget,target);
						resultObj.eSet(feat, resultTarget);
					}
				}
				else if (ref.getUpperBound() == -1)
				{
					Object res = startObj.eGet(ref);
					if (res instanceof EList<?>)
					{
						BasicEList<EObject> build = new BasicEList<EObject>();
						EList<?> resList = (EList<?>)res;
						for (Iterator<?> iu = resList.iterator(); iu.hasNext();)
						{
							Object nextObj = iu.next();
							if (nextObj instanceof EObject)
							{
								EObject target = (EObject)nextObj;
								EObject resultTarget = createModelObject(target.eClass());
								extendClonedObject(resultTarget,target);
								build.add(resultTarget);
							}
							else throw new MapperException("Another boring issue - not a list of EObjects");
						}
						if (build.size() > 0) resultObj.eSet(feat, build);
					}
					else throw new MapperException("Boring issue - ref result is not a list");
				}
			}
		}
	}
	
	//------------------------------------------------------------------------------------------------------
	//                                          Utilities
	//------------------------------------------------------------------------------------------------------
	
	/**
	 * a path consists of steps separated by '.', 
	 * but each step may have conditions in [], which may also contain '.'.
	 * Split the path into an array of steps
	 * @param path
	 * @return
	 */
	static String[] pathSteps(String path)
	{
		// substitute a key for each condition, remembering the condition
		StringTokenizer st = new StringTokenizer(path,"[]",true);
		Hashtable<String,String> conditions = new Hashtable<String,String>();
		int c = 0;
		String subs = "";
		boolean inCond = false;
		while (st.hasMoreTokens())
		{
			String next = st.nextToken();
			if (next.equals("]")) inCond = false;
			if (!inCond) subs = subs + next;
			else if (inCond)
			{
				String key = "k" + c;
				conditions.put(key, next);
				subs = subs + key;
				c++;
			}
			if (next.equals("[")) inCond = true;
		}
		
		// split the string into steps, and reinsert the conditions
		StringTokenizer su = new StringTokenizer(subs,".");
		int steps = su.countTokens();
		String[] result = new String[steps];
		int stepNo = 0;
		while (su.hasMoreTokens())
		{
			String nStep = su.nextToken();
			StringTokenizer sv = new StringTokenizer(nStep,"[]",true);
			String step = "";
			inCond = false;
			while (sv.hasMoreTokens())
			{
				String part = sv.nextToken();
				if (part.equals("]")) inCond = false;
				if (!inCond) step = step + part;
				else if (inCond) step = step + conditions.get(part);
				if (part.equals("[")) inCond = true;
			}
			result[stepNo] = step;
			stepNo++;
		}
		return result;
	}
	
	private void writeSteps(String[] steps, String path)
	{
		message("writing steps of path " + path);
		for (int i = 0; i < steps.length; i++) message("Step: " + steps[i]);
	}

	private void message(String s) {System.out.println(s);}
	



}
