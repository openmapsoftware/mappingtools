package com.openMap1.mapper.health.actions;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.FixedPropertyValue;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.actions.MakeITSMappingsAction;
import com.openMap1.mapper.actions.MapperActionDelegate;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.structures.ITSAssociation;
import com.openMap1.mapper.structures.MapperWrapper;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.views.ClassModelView;
import com.openMap1.mapper.views.FeatureView;
import com.openMap1.mapper.views.LabelledEClass;
import com.openMap1.mapper.views.WorkBenchUtil;

public class ITSMappingsFrom2ExamplesAction  extends MapperActionDelegate implements IObjectActionDelegate{
	
	private MapperWrapper wrapper;
	
	// wrapper class and package as defined in a mapping set - e.g 'com.openMap1.mapper.converters.NHS_CDA_Converter'
	private String wrapperClassName = null;

	private MapperEditor mapperEditor;
	
	private MappedStructure mappedStructure;
    
    private ClassModelView classModelView;
    
    private Hashtable<String,String> universalFixedValues;
    
    private boolean includeAllTextNodes = true;
	
	public ITSMappingsFrom2ExamplesAction()
	{
		super();
	}

	public void run(IAction action)
	{
		/* Ensure that the mapping set showing in the editor is the one which the user
		 * right-clicked  - so you alter the correct mapping set and Ecore model */
		OpenMapperEditor(selection);

		classModelView = WorkBenchUtil.getClassModelView(false);
		if (classModelView != null) try
		{
			String mappingSetURIString = classModelView.mappingSetURI().toString();
			mapperEditor = WorkBenchUtil.getMapperEditor(mappingSetURIString);
			if (mapperEditor != null)
			{
				Element XMLRoot2 = getExampleMessageAndWrapIn("Select Annotated Message Instance", true);
				if (XMLRoot2 == null) return;
				Element XMLRoot1 = getExampleMessageAndWrapIn("Select Unmodified Message Instance", false);
				
				if (XMLRoot1 != null)
				{
					// collect fixed values, to be applied on all nodes wherever they appear in the instances
					universalFixedValues = new Hashtable<String,String>() ;
					trace("Collect Universal Fixed Values");
					collectUniversalFixedValues(XMLRoot1,XMLRoot2);					
					
					// make annotations on the Ecore model from the two example messages
					trace("Annotate Ecore model");
					annotateEcoreModel(XMLRoot1,XMLRoot2);
					
					// apply automatic flattening rules, except where overridden
					LabelledEClass topClass = classModelView.topLabelledEClass();
					trace("Apply flattening rules");
					applyFlatteningRules(topClass,XMLRoot2);
					
					// use the altered Ecore model to make a mapping set
					trace("Make mapping set");
					new MakeITSMappingsAction().run();
					mappedStructure = mappedStructure();
					
					// add fixed values defined in the RMIM as requested in the example. 'null' means no previous class to link to and no ref name
					trace("Add fixed attribute values");
					addAllFixedValues(topClass, mappedStructure, null, null);

					// save the updated mapping set
					trace("Save mapping set");
					FileUtil.saveResource(mappedStructure.eResource());
					
				}
			}
		}
		catch (Exception ex) {WorkBenchUtil.showMessage("Error",ex.getMessage());}
	}
	
	/**
	 * get the root element of one of the two example messages, and if there is a wrapper transform,
	 * apply the in-wrapper transform to it
	 * @param dialogTitle
	 * @param findWrapperClass
	 * @return
	 * @throws MapperException
	 */
	private Element getExampleMessageAndWrapIn(String dialogTitle, boolean findWrapperClass) throws MapperException
	{
		// get the root element of the unmodified or annotated XML instance
		String[] exts = {"*.xml"}; 
		String path = FileUtil.getFilePathFromUser(targetPart,exts,dialogTitle,false);
		if (path.equals("")) return null;				
		Element XMLRoot = XMLUtil.getRootElement(path);
		if (XMLRoot == null) throw new MapperException("Could not open requested file");
		
		/* If the example message uses a wrapper class, the annotated example message should define
		 * the location in the workspace of the mapping set that identifies the wrapper class,
		 * in an attribute "mappingSet" on its root element */
		if (findWrapperClass)
		{
			wrapper = null;
			String mappingSetURI = XMLRoot.getAttribute("mappingSet");
			if ((mappingSetURI != null) && (!mappingSetURI.equals(""))) try
			{
				String absLoc = FileUtil.absoluteLocation(mappingSetURI);
				MappedStructure otherMS = FileUtil.getMappedStructure(absLoc);
			    // get an instance of the wrapper class; spare argument is the root element name
			    String rootName = "";
			    if (otherMS.getRootElement() != null) rootName = otherMS.getRootElement().getName();
			    wrapper = otherMS.getWrapper(rootName);
			    if (wrapper != null) wrapperClassName = otherMS.getMappingParameters().getWrapperClass();
			}
			catch (IOException ex) {throw new MapperException("Cannot find mapping set at '" + mappingSetURI + "'");}
			
		}
		
		if (wrapper != null) return wrapper.transformIn(XMLRoot).getDocumentElement();
		else return XMLRoot;		
	}
	
	/**
	 * 
	 * @param XMLRoot1
	 * @param XMLRoot2
	 * @throws MapperException
	 */
	private void annotateEcoreModel(Element XMLRoot1,Element XMLRoot2) throws MapperException
	{
		String rootError = "Root element tag name of XML instance does not match top class of Ecore model: ";
		LabelledEClass topClass = classModelView.topLabelledEClass();
		String path = topClass.eClass().getName();
		if (!(XMLRoot1.getLocalName().equals(path))) throw new MapperException(rootError + path);
		if (!(XMLRoot2.getLocalName().equals(path))) throw new MapperException(rootError + path);
		
		if (wrapperClassName != null)  ModelUtil.addMIFAnnotation(topClass.eClass(), "wrapperClass", wrapperClassName);
		
		annotateModel(topClass, XMLRoot1,XMLRoot2);
	}
	
	/**
	 * 
	 * @param val
	 * @return true if this value has been marked to be a fixed value wherever it occurs in the example
	 */
	private boolean universalFixedValue(String val) {return (universalFixedValues.get(val) != null);}

	/**
	 * recursive descent of two example messages, using the differences between them to annotate the Ecore model,
	 * so it can be used to make a mapping set for a simplified XML
	 * @param theClass class in the ECore model corresponding to current nodes of the two example messages
	 * @param XMLRoot1 current element in the unmodified example message
	 * @param XMLRoot2 current element in the annotated example message
	 * @return true if there are any attributes marked to be included in the simplified message,
	 * in the subtree below this class
	 */
	private boolean annotateModel(LabelledEClass theClass, Element el1,Element el2) throws MapperException
	{
		boolean hasUsedAttributes = false;
		// look for any attributes which have different values in the two examples, and mark them in the model
		for (int a = 0; a < el1.getAttributes().getLength(); a++)
		{
			Attr att = (Attr)el1.getAttributes().item(a);
			String val1= att.getValue();
			String val2= el2.getAttribute(att.getName());
			if ((!val1.equals(val2))|universalFixedValue(val1))
			{
				if (markAttributeUsedOrFixed(theClass,att.getName(), val1, val2)) hasUsedAttributes = true;
			}
		}
		
		/* for elements with no descendant elements, the text content in the two examples may differ.
		 * If so, make sure there is a 'textContent' EAttribute in the Ecore model, and mark it as used,
		 * possibly with renaming */
		if (XMLUtil.childElements(el1).size() == 0)
		{
			String val1 = XMLUtil.getText(el1);
			String val2 = XMLUtil.getText(el2);
			if ((!val1.equals(val2))|universalFixedValue(val1))
			{
				// add a 'textContent' EAttribute to the class if it does not yet exist
				addTextContentAttribute(theClass.eClass());
				if (markAttributeUsedOrFixed(theClass,"textContent", val1, val2))  hasUsedAttributes = true;				
			}
		}
		
		/* Classes reached by a <text> element should have a 'textContent' attribute which is 
		 * marked as used, if 'includeAllTextNodes' is true. */
		if ((XMLUtil.getLocalName(el1).equals("text")) && includeAllTextNodes)
		{
			addTextContentAttribute(theClass.eClass());
			if (markAttributeUsedOrFixed(theClass,"textContent", "a", "a#"))  hasUsedAttributes = true;							
		}
				
		// iterate over child elements, in step
		Vector<Element> c1 = XMLUtil.childElements(el1);
		Vector<Element> c2 = XMLUtil.childElements(el2);
		for (int c = 0; c < c1.size(); c++)
		{
			Element cEl1 = c1.get(c);
			Element cEl2 = c2.get(c);
			LabelledEClass theChild = theClass.getNamedAssocChild(cEl1.getLocalName());
			if (theChild != null)
			{
				if (annotateModel(theChild,cEl1,cEl2))  hasUsedAttributes = true;
			}
			// ED  and ANY nodes (eg <text>) can have any kind of child node
			else if (theChild == null) 
			{
				if (!canHaveAnyChild(theClass)) throw new MapperException("Cannot find child labelled EClass '" 
						+ cEl1.getLocalName() + "' from class '" + theClass.eClass().getName() + "'");
			}			
		}
		
		if (hasUsedAttributes) mark_CV_II_Attributes(theClass);
		
		return hasUsedAttributes;
	}
	
	/**
	 * This LabelledEClass is going to be mapped, as it has some used attributes below it.
	 * If it has any EReferences marked with the annotation 'fixed att value' they should
	 * lead only to an II or CV data type object. Mark the 'extension' or 'code' attribute
	 * of that data type object as having a fixed value; and mark the EReference as used, so 
	 * that the child class and the association get mapped. 
	 * @param theClass
	 */
	private void mark_CV_II_Attributes(LabelledEClass theClass) throws MapperException
	{
		for (Iterator<EReference> ir = theClass.eClass().getEAllReferences().iterator(); ir.hasNext();)
		{
			EReference ref = ir.next();
			String fixedVal = ModelUtil.getEAnnotationDetail(ref,"fixed att value");
			if (fixedVal != null)
			{
				LabelledEClass child = theClass.getNamedAssocChild(ref.getName());
				if (child != null)
				{
					String val1=fixedVal;
					String val2= val1+ "#f";
					String nextCName = child.eClass().getName();
					// this is what NHS template RMIMs mean when they put 'fixedValue' on a data type class: II or CV or BL
					if (nextCName.equals("II")) markAttributeUsedOrFixed(child,"extension",val1,val2);
					else if (nextCName.equals("CV")) markAttributeUsedOrFixed(child,"code",val1,val2);
					else if (nextCName.equals("BL")) markAttributeUsedOrFixed(child,"value",val1,val2);
					else throw new MapperException("Cannot place a fixed attribute value on class '" + nextCName 
							+ "' from class " + theClass.eClass().getName() + " in package " + theClass.eClass().getEPackage().getName());
					child.markAsUsedInMicroITS(true); // mark the EReference to this data type child
				}
			}				
		}
	}
	
	/**
	 * add a 'textContent' EAttribute to the class if it does not yet exist
	 * @param aClass
	 */
	private void addTextContentAttribute(EClass aClass)
	{
		String attName = "textContent";
		EAttribute eText = (EAttribute)aClass.getEStructuralFeature(attName);
		if (eText == null)  
		{
			eText = EcoreFactory.eINSTANCE.createEAttribute();
			eText.setName(attName);
			eText.setLowerBound(0); 
			eText.setEType(EcorePackage.eINSTANCE.getEString());					
			aClass.getEStructuralFeatures().add(eText);
		}		
	}
	
	/**
	 * mark an EAttribute of a class as to be used in the simple ITS, 
	 * possibly with renaming; and mark all associations on the path down
	 * top the EAttribute as used.
	 * @param theClass
	 * @param attName
	 * @param val1
	 * @param val2
	 * @throws MapperException
	 */
	private boolean markAttributeUsedOrFixed(LabelledEClass theClass,String attName, String val1, String val2) 
	throws MapperException
	{
		boolean used = false;
		// find the EAttribute to be affected
		EAttribute eAtt = (EAttribute)theClass.eClass().getEStructuralFeature(attName);
		if (eAtt == null)
		{
			String warning = "Cannot find Eattribute '" + attName + "' of class '" + theClass.eClass().getName() + "'";
			/* sometimes the annotated message will not precisely match the RMIM, 
			 * and this should not be a fatal error. For the moment, just write a warning message.  */
			// throw new MapperException(warning);
			System.out.println(warning);
			return false;
		}
		
		/* if the attribute is to be renamed, indicate by '#<new name> added to the value in the altered example
		 * where <newName> is not 'f' or 'fa' */
		String newName = "";
		if ((val2.length() > val1.length()) && (val2.startsWith(val1)))
		{
			String remainder = val2.substring(val1.length());
			if (remainder.startsWith("#")) newName = remainder.substring(1);
		}

		// some additions mean a fixed value; annotation to add a fixed property value to the object mapping
		if ((newName.equals("f"))|(newName.equals("fa"))|(universalFixedValue(val1)))
		{
			FeatureView.addMicroITSAnnotation(eAtt, "fixed:" + theClass.getPath(), val1);						
		}
		
		/* additions which do not mean a fixed value; annotation to add a property mapping to an attribute
		 * with the existing name or name 'newName' */
		else
		{
			String annotation = "T:" + newName;
			FeatureView.addMicroITSAnnotation(eAtt, theClass.getPath(), annotation);
			used = true;
		}
		
		/* If an attribute is used for a non-fixed value, for all ancestor EAssociations, set the used flag.
		 * Ascend the tree until you find a flag that is already set ,
		 * or you reach the top of the tree */
		LabelledEClass current = theClass;
		if (used) while (current != null)
		{
			boolean oldUsedState = current.isMarkedUsedInMicroITS();
			if (!oldUsedState) // need to mark and ascend
			{
				current.markAsUsedInMicroITS(true);
				current = current.parent(); // null if current was top of the tree
			}
			else if (oldUsedState) current = null; // need go no further
		}
		return used;
	}
	
	/**
	 * The automatic flattening rules are that any EReference which is marked as used
	 * and has max multiplicity 1, gets flattened, unless it has been marked 
	 * with an attribute flatten="no" or has a textContent attribute
	 */
	private void applyFlatteningRules(LabelledEClass theClass, Element el) throws MapperException
	{
		for (Iterator<Element> it = XMLUtil.childElements(el).iterator();it.hasNext();)
		{
			Element childEl = it.next();
			String tagName = childEl.getLocalName();
			EStructuralFeature sRef = theClass.eClass().getEStructuralFeature(tagName);
			if ((sRef != null) && (sRef instanceof EReference))
			{
				EReference ref = (EReference)sRef;
				ITSAssociation itsa = FeatureView.getITSAssociation(ref, theClass.getPath());
				// do nothing , and recurse no further, unless this association is marked as having used attributes below it
				if (itsa.attsIncluded())
				{
					/* automatic flattening rule. Flatten if max multiplicity is 1,
					 * and if the child EClass has no textContent attribute */
					EClass child = (EClass)ref.getEType();
					boolean collapsed = ((ref.getUpperBound() == 1) && (child.getEStructuralFeature("textContent")== null));
					
					// manual override of flattening rules
					String flatten = childEl.getAttribute("flatten");
					if (flatten.equals("no"))  collapsed = false;
					if (flatten.equals("yes"))  collapsed = true;
					itsa.setCollapsed(collapsed);
					
					String rename = childEl.getAttribute("rename");
					itsa.setBusinessName(rename);
					
					FeatureView.addMicroITSAnnotation(ref, theClass.getPath(), itsa.stringForm());

					LabelledEClass theChild = theClass.getNamedAssocChild(tagName);
					if (theChild != null) applyFlatteningRules(theChild,childEl);
					// ED nodes (eg <text>) can have any kind of child node
					else if ((theChild == null) && (!(theClass.eClass().getName().equals("ED")))) 
					{
						throw new MapperException("Cannot find child labelled EClass '" 
								+ tagName + "' from class '" + theClass.eClass().getName() + "'");
					}					
				}				
			}
			// there are all sorts of elements below ED  or ANY nodes, whose names we do not know
			if ((sRef == null) && (!(canHaveAnyChild(theClass)))) 
				throw new MapperException("Cannot find link '" 
					+ tagName + "' from class '" + theClass.eClass().getName() + "'");
		}
	}
	
	/**
	 * 
	 * @param theClass
	 * @return true if the class is allowed to have all sort of child links
	 */
	private boolean canHaveAnyChild(LabelledEClass theClass)
	{
		String cName = theClass.eClass().getName();
		return ((cName.equals("ED"))|(cName.equals("ANY")));
	}

	/**
	 * collect all text values which have been marked to be fixed values
	 * wherever they occur in the instances, by recursive descent
	 * @param el1
	 * @param el2
	 */
	private void collectUniversalFixedValues(Element el1,Element el2)
	{
		// remember any attribute where '#fa' has been added to the value in the annotated example
		for (int a = 0; a < el1.getAttributes().getLength(); a++)
		{
			Attr att = (Attr)el1.getAttributes().item(a);
			String val1= att.getValue();
			String val2= el2.getAttribute(att.getName());
			if (val2.equals(val1 + "#fa")) universalFixedValues.put(val1, "1");
		}
		
		// remember any text content of an element where '#fa' has been added to the value in the annotated example
		if (XMLUtil.childElements(el1).size() == 0)
		{
			String val1 = XMLUtil.getText(el1);
			String val2 = XMLUtil.getText(el2);
			if (val2.equals(val1 + "#fa")) universalFixedValues.put(val1, "1");
		}
				
		// iterate over child elements, in step
		Vector<Element> c1 = XMLUtil.childElements(el1);
		Vector<Element> c2 = XMLUtil.childElements(el2);
		for (int c = 0; c < c1.size(); c++)
		{
			Element cEl1 = c1.get(c);
			Element cEl2 = c2.get(c);
			collectUniversalFixedValues(cEl1,cEl2);
		}		
	}
	
	/**
	 * recursive descent of the LabelledEClass tree, adding fixed property values
	 * to object mappings wherever requested on the tree
	 * @param theClass current LabelledEClass
	 * @param mappedStructure
	 */
	private void addAllFixedValues(LabelledEClass theClass, MappedStructure mappedStructure, ObjMapping previous, String refName)
	throws MapperException
	{
		ObjMapping oMap = linkedMappingForClass(refName,theClass,mappedStructure,previous);
		if (oMap != null)
		{
			// If any EAttributes for the class have fixed values for the association path, add the fixed values
			String attKey = "fixed:" + theClass.getPath();
			for (Iterator<EAttribute> it = theClass.eClass().getEAllAttributes().iterator(); it.hasNext();)
			{
				EAttribute att = it.next();
				String attName= att.getName();
				// general fixed value defined by the RMIM, for any path
				String fixedVal = ModelUtil.getEAnnotationDetail(att,"fixed value");
				// specific fixed value requested for this path on the annotated example instance
				String fixedVal1 = FeatureView.getMicroITSAnnotation(att, attKey);
				//specific fixed value takes precedence
				if (fixedVal1 != null) fixedVal = fixedVal1;
				if (fixedVal != null) addFixedValue(oMap,attName,fixedVal);
			}
			
			// recurse to the next LabelledEClass, and pick up fixed values of II and CV data types
			for (Iterator<EReference> it = theClass.eClass().getEAllReferences().iterator(); it.hasNext();)
			{
				EReference ref = it.next();
				String nextRefName = ref.getName();
				LabelledEClass nextClass = theClass.getNamedAssocChild(nextRefName);
				if (nextClass == null)  throw new MapperException("Cannot find Labelled Class by link " + nextRefName 
						+ " from class '" + theClass.eClass().getName() + "'");

				// recursive step; stops if there is no mapping
				addAllFixedValues(nextClass,mappedStructure,oMap,nextRefName); 
				
				// annotations on EReferences set a property of the child data type class
				String nextCName = nextClass.eClass().getName();
				String fixedVal = ModelUtil.getEAnnotationDetail(ref,"fixed att value");
				if (fixedVal != null)
				{
					ObjMapping nextMap = linkedMappingForClass(nextRefName,nextClass,mappedStructure,oMap);
					if (nextMap != null)
					{
						// this is what NHS template RMIMs mean when they put 'fixedValue' on a data type class
						if (nextCName.equals("II")) addFixedValue(nextMap,"extension",fixedVal);
						else if (nextCName.equals("CV")) addFixedValue(nextMap,"code",fixedVal);
						else if (nextCName.equals("BL")) addFixedValue(nextMap,"value",fixedVal);
						else throw new MapperException("Cannot place a fixed attribute value on class '" + nextCName + "'");						
					}
				}				
			}
		}
	}
	
	/**
	 * add a fixed property value to an object mapping, 
	 * if there is not already one for the same attribute
	 * @param oMap
	 * @param attName
	 * @param fixedVal
	 */
	private void addFixedValue(ObjMapping oMap, String attName, String fixedVal)
	{
		trace("fixed " + attName + " to " + fixedVal);
		// do not add a fixed value mapping if there is one already for this attribute
		boolean hasFixedValue = false;
		for (Iterator<FixedPropertyValue> iv = oMap.getFixedPropertyValues().iterator();iv.hasNext();)
			if (iv.next().getMappedProperty().equals(attName)) hasFixedValue = true;
		
		if (!hasFixedValue)
		{
			FixedPropertyValue fpv = MapperFactory.eINSTANCE.createFixedPropertyValue();
			fpv.setMappedProperty(attName);
			fpv.setFixedValue(fixedVal);
			fpv.setValueType("string");
			oMap.getFixedPropertyValues().add(fpv);
		}		
	}
	
	/**
	 * @param refName
	 * @param theClass
	 * @param ms
	 * @param previous
	 * @return an Object mapping for the class, which is linked to the previous
	 * object mapping by an association mapping with the defined role name, if it exists; null otherwise
	 * If the previous object mapping is null, choose any object mapping to the present class (it is the top class,
	 * so assume there is only one object mapping to it)
	 */
	private ObjMapping linkedMappingForClass(String refName, LabelledEClass theClass, MappedStructure ms, ObjMapping previous)
	throws MapperException
	{

		/* if there is a previous object mapping to link to, find what subset of the current class has 
		 * an object mapping linked by an association mapping to the previous object mapping, with the correct role name */
		String subset = null;
		if ((previous != null) && (refName != null))
		{
			for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(ms, MapperPackage.eINSTANCE.getAssocMapping()).iterator();it.hasNext();)
			{
				AssocMapping am = (AssocMapping)it.next();
				for (int e = 0; e < 2; e++)
				{
					AssocEndMapping aem = am.getMappedEnd(e);
					if (aem.getClassSet().equals(previous.getClassSet()))
					{
						AssocEndMapping bem = am.getMappedEnd(1-e);
						if ((bem.getClassSet().className().equals(ModelUtil.getQualifiedClassName(theClass.eClass()))) &&
								(refName.equals(bem.getMappedRole())))
									subset = bem.getClassSet().subset();
					}
				}
			}			
		}
		
		
		// failure - there was a previous object mapping, and there is no association mapping linking it to the current class
		if ((previous != null) && (subset == null)) return null;
		
		// find all object mappings to the class; and if the subset is known, filter by the subset
		for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(ms, MapperPackage.eINSTANCE.getObjMapping()).iterator();it.hasNext();)
		{
			ObjMapping om = (ObjMapping)it.next();
			if (om.getClassSet().className().equals(ModelUtil.getQualifiedClassName(theClass.eClass())))
			{
				if (subset == null) return om;
				if (om.getClassSet().subset().equals(subset)) return om;
			}
		}
				
		return null;
	}


}
