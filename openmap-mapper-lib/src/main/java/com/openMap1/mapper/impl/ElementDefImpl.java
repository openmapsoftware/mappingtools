/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import org.eclipse.emf.ecore.EObject;


import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.XpthException;
import com.openMap1.mapper.structures.StructureDefinition;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.MapperValidator;
import com.openMap1.mapper.writer.TreeElement;
import com.openMap1.mapper.util.messageChannel;

import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperFactory;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.MaxMult;
import com.openMap1.mapper.MinMult;
import com.openMap1.mapper.NodeDef;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import java.util.Map;
import java.util.StringTokenizer;


import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.openMap1.mapper.impl.ElementDefImpl#isExpanded <em>Expanded</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ElementDefImpl#getMaxMultiplicity <em>Max Multiplicity</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ElementDefImpl#getChildElements <em>Child Elements</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ElementDefImpl#getAttributeDefs <em>Attribute Defs</em>}</li>
 *   <li>{@link com.openMap1.mapper.impl.ElementDefImpl#getImportMappingSet <em>Import Mapping Set</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ElementDefImpl extends NodeDefImpl implements ElementDef {
	/**
	 * The default value of the '{@link #isExpanded() <em>Expanded</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExpanded()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXPANDED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isExpanded() <em>Expanded</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExpanded()
	 * @generated
	 * @ordered
	 */
	protected boolean expanded = EXPANDED_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaxMultiplicity() <em>Max Multiplicity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxMultiplicity()
	 * @generated
	 * @ordered
	 */
	protected static final MaxMult MAX_MULTIPLICITY_EDEFAULT = MaxMult.ONE;

	/**
	 * The cached value of the '{@link #getMaxMultiplicity() <em>Max Multiplicity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxMultiplicity()
	 * @generated
	 * @ordered
	 */
	protected MaxMult maxMultiplicity = MAX_MULTIPLICITY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getChildElements() <em>Child Elements</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildElements()
	 * @generated
	 * @ordered
	 */
	protected EList<ElementDef> childElements;

	/**
	 * The cached value of the '{@link #getAttributeDefs() <em>Attribute Defs</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttributeDefs()
	 * @generated
	 * @ordered
	 */
	protected EList<AttributeDef> attributeDefs;

	/**
	 * The cached value of the '{@link #getImportMappingSet() <em>Import Mapping Set</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImportMappingSet()
	 * @generated
	 * @ordered
	 */
	protected ImportMappingSet importMappingSet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ElementDefImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.ELEMENT_DEF;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpanded(boolean newExpanded) {
		boolean oldExpanded = expanded;
		expanded = newExpanded;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ELEMENT_DEF__EXPANDED, oldExpanded, expanded));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MaxMult getMaxMultiplicity() {
		return maxMultiplicity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaxMultiplicity(MaxMult newMaxMultiplicity) {
		MaxMult oldMaxMultiplicity = maxMultiplicity;
		maxMultiplicity = newMaxMultiplicity == null ? MAX_MULTIPLICITY_EDEFAULT : newMaxMultiplicity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ELEMENT_DEF__MAX_MULTIPLICITY, oldMaxMultiplicity, maxMultiplicity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ElementDef> getChildElements() {
		if (childElements == null) {
			childElements = new EObjectContainmentEList<ElementDef>(ElementDef.class, this, MapperPackage.ELEMENT_DEF__CHILD_ELEMENTS);
		}
		return childElements;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<AttributeDef> getAttributeDefs() {
		if (attributeDefs == null) {
			attributeDefs = new EObjectContainmentEList<AttributeDef>(AttributeDef.class, this, MapperPackage.ELEMENT_DEF__ATTRIBUTE_DEFS);
		}
		return attributeDefs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImportMappingSet getImportMappingSet() {
		return importMappingSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetImportMappingSet(ImportMappingSet newImportMappingSet, NotificationChain msgs) {
		ImportMappingSet oldImportMappingSet = importMappingSet;
		importMappingSet = newImportMappingSet;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET, oldImportMappingSet, newImportMappingSet);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImportMappingSet(ImportMappingSet newImportMappingSet) {
		if (newImportMappingSet != importMappingSet) {
			NotificationChain msgs = null;
			if (importMappingSet != null)
				msgs = ((InternalEObject)importMappingSet).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET, null, msgs);
			if (newImportMappingSet != null)
				msgs = ((InternalEObject)newImportMappingSet).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET, null, msgs);
			msgs = basicSetImportMappingSet(newImportMappingSet, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET, newImportMappingSet, newImportMappingSet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * check that if the Element has not been expanded, it has no child Elements or Attributes
	 * <!-- end-user-doc -->
	 */
	public boolean noChildrenIfNotExpanded(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean reallyNotExpanded = true;
		int nChildElements = getChildElements().size();
		int nAttributes = getAttributeDefs().size();
		if ((!isExpanded()) && ((nAttributes + nChildElements) > 0)) reallyNotExpanded = false;
		if (!reallyNotExpanded) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.WARNING,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ELEMENT_DEF__NO_CHILDREN_IF_NOT_EXPANDED,
						 "Element is not expanded, but has " + nChildElements + " child elements and "
						 + nAttributes + " attributes",
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * If an element has been expanded, check that it has all the child nodes
	 * as given by the external structure definition
	 * <!-- end-user-doc -->
	 */
	public boolean hasAllChildrenIfExpanded(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean hasAllChildren = true;
		String message = "";
		/* If the node is not expanded, do not check.
		 * If you cannot find this node, do not try to check its structure */
		if ((isExpanded()) && (nodeInStructureDefinition() != null))
		{
			String me = missingElements();
			String ma = missingAttributes();
			if ((me.length() > 0)|(ma.length() > 0))
			{
				hasAllChildren = false;
				message = "Element '" + getName() + "' has missing ";
				if (me.length() > 0)
				{
					message = message + "child elements " + me;
					if (ma.length() > 0) message = message + " and ";
				}
				if (ma.length() > 0)
				{
					message = message + "attributes " + ma;
				}
			}
		}
		if (!hasAllChildren) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ELEMENT_DEF__HAS_ALL_CHILDREN_IF_EXPANDED,
						 message,
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}
	
	private String missingElements()
	{
		String me = "";
		for (Iterator<ElementDef> it = ((ElementDef)nodeInStructureDefinition()).getChildElements().iterator(); it.hasNext();)
		{
			String childName = it.next().getName();
			if (getNamedChildElement(childName) == null) me = me + "'" + childName + "' ";
		}
		return me;
	}
	
	private String missingAttributes()
	{
		String ma = "";
		for (Iterator<AttributeDef> it = ((ElementDef)nodeInStructureDefinition()).getAttributeDefs().iterator(); it.hasNext();)
		{
			String attName = it.next().getName();
			if (getNamedAttribute(attName) == null) ma = ma + "'" + attName + "' ";
		}
		return ma;
	}

	/**
	 * <!-- begin-user-doc -->
	 * check the maximum multiplicity of this node (element or attribute)
	 * against the max multiplicity as given by the external structure definition
	 * <!-- end-user-doc -->
	 */
	public boolean hasCorrectMaxMultiplicity(DiagnosticChain diagnostics, Map<?,?> context) {
		boolean maxMultiplicityOK = true;
		/* If this is the root node, do not check.
		 * If you cannot find this node, do not check; the next check, 
		 * or the check of some higher node, will give a message */
		if ((eContainer() instanceof NodeDef) && (nodeInStructureDefinition() != null))
		{
			maxMultiplicityOK = (getMaxMultiplicity() == ((ElementDef)nodeInStructureDefinition()).getMaxMultiplicity());
		}
		if (!maxMultiplicityOK) {
			if (diagnostics != null) {
				diagnostics.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 MapperValidator.DIAGNOSTIC_SOURCE,
						 MapperValidator.ELEMENT_DEF__HAS_CORRECT_MAX_MULTIPLICITY,
						 ("Max multiplicity mismatch at element '"  + getName() + "'; "
								 + getMaxMultiplicity().getName() + " is not  "
							     + ((ElementDef)nodeInStructureDefinition()).getMaxMultiplicity().getName()
								 + " as in the structure definition. "),
						 new Object [] { this }));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MapperPackage.ELEMENT_DEF__CHILD_ELEMENTS:
				return ((InternalEList<?>)getChildElements()).basicRemove(otherEnd, msgs);
			case MapperPackage.ELEMENT_DEF__ATTRIBUTE_DEFS:
				return ((InternalEList<?>)getAttributeDefs()).basicRemove(otherEnd, msgs);
			case MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET:
				return basicSetImportMappingSet(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MapperPackage.ELEMENT_DEF__EXPANDED:
				return isExpanded() ? Boolean.TRUE : Boolean.FALSE;
			case MapperPackage.ELEMENT_DEF__MAX_MULTIPLICITY:
				return getMaxMultiplicity();
			case MapperPackage.ELEMENT_DEF__CHILD_ELEMENTS:
				return getChildElements();
			case MapperPackage.ELEMENT_DEF__ATTRIBUTE_DEFS:
				return getAttributeDefs();
			case MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET:
				return getImportMappingSet();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MapperPackage.ELEMENT_DEF__EXPANDED:
				setExpanded(((Boolean)newValue).booleanValue());
				return;
			case MapperPackage.ELEMENT_DEF__MAX_MULTIPLICITY:
				setMaxMultiplicity((MaxMult)newValue);
				return;
			case MapperPackage.ELEMENT_DEF__CHILD_ELEMENTS:
				getChildElements().clear();
				getChildElements().addAll((Collection<? extends ElementDef>)newValue);
				return;
			case MapperPackage.ELEMENT_DEF__ATTRIBUTE_DEFS:
				getAttributeDefs().clear();
				getAttributeDefs().addAll((Collection<? extends AttributeDef>)newValue);
				return;
			case MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET:
				setImportMappingSet((ImportMappingSet)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MapperPackage.ELEMENT_DEF__EXPANDED:
				setExpanded(EXPANDED_EDEFAULT);
				return;
			case MapperPackage.ELEMENT_DEF__MAX_MULTIPLICITY:
				setMaxMultiplicity(MAX_MULTIPLICITY_EDEFAULT);
				return;
			case MapperPackage.ELEMENT_DEF__CHILD_ELEMENTS:
				getChildElements().clear();
				return;
			case MapperPackage.ELEMENT_DEF__ATTRIBUTE_DEFS:
				getAttributeDefs().clear();
				return;
			case MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET:
				setImportMappingSet((ImportMappingSet)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MapperPackage.ELEMENT_DEF__EXPANDED:
				return expanded != EXPANDED_EDEFAULT;
			case MapperPackage.ELEMENT_DEF__MAX_MULTIPLICITY:
				return maxMultiplicity != MAX_MULTIPLICITY_EDEFAULT;
			case MapperPackage.ELEMENT_DEF__CHILD_ELEMENTS:
				return childElements != null && !childElements.isEmpty();
			case MapperPackage.ELEMENT_DEF__ATTRIBUTE_DEFS:
				return attributeDefs != null && !attributeDefs.isEmpty();
			case MapperPackage.ELEMENT_DEF__IMPORT_MAPPING_SET:
				return importMappingSet != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (expanded: ");
		result.append(expanded);
		result.append(", maxMultiplicity: ");
		result.append(maxMultiplicity);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * return a named child element, or null if there is none
	 * @param name
	 * @return
	 */
	public ElementDef getNamedChildElement(String name)
	{
		ElementDef child = null;
		for (Iterator<ElementDef> it = getChildElements().iterator(); it.hasNext();)
		{
			ElementDef c = it.next();
			if (name.equals(c.getName())) child = c;
		}
		return child;		
	}
	
	/**
	 * return a named attribute, or null if there is none
	 * @param name
	 * @return
	 */
	public AttributeDef getNamedAttribute(String name)
	{
		AttributeDef at = null;
		for (Iterator<AttributeDef> it = getAttributeDefs().iterator(); it.hasNext();)
		{
			AttributeDef a = it.next();
			if (name.equals(a.getName())) at = a;
		}
		return at;
	}
	
	/**
	 * 
	 * @param path String form of a relative XPath, beginning with '/'
	 * @return the descendant Node reached by that path; or null if there is none
	 */
	public NodeDef getDescendantByPath(String path)
	{
		if (path == null) return null;
		if (!path.startsWith("/")) return null;
		StringTokenizer st = new StringTokenizer(path,"/");
		String first = st.nextToken();
		if (first.startsWith("@")) return getNamedAttribute(first.substring(1));
		else
		{
			ElementDef child = getNamedChildElement(first);
			if (child == null) return null;
			else if (path.length() == first.length() + 1) return child;
			else return child.getDescendantByPath(path.substring(first.length() + 1));
		}
	}
	
	//--------------------------------------------------------------------------------------------
	//         Interface TreeElement  - parts specific to ElementDefs 
	//--------------------------------------------------------------------------------------------

    
    public boolean isUnique() {return (getMaxMultiplicity() == MaxMult.ONE);}
    
    /** Vector of names of attributes of this element */
    public Vector<String> attributes() {
    	Vector<String> atts = new Vector<String>();
    	for (Iterator<AttributeDef> it = getAttributeDefs().iterator();it.hasNext();)
    		atts.add(it.next().getName());
    	return atts;
    }
    
    /** true if attribute number i is optional */
    public boolean isOptionalAtt(int i)
    {return (getAttributeDefs().get(i).getMinMultiplicity() == MinMult.ZERO);}
    
    /** vector of child tree elements */
    public Vector<TreeElement> childTreeElements()
    {
    	Vector<TreeElement> res = new Vector<TreeElement>();
    	for (Iterator<ElementDef> it = getChildElements().iterator();it.hasNext();)
    		res.add(it.next());
    	return res;
    }

    /** return a named descendant element of this element (possibly itself), or null if there is none.  */
    public TreeElement namedDescendant(String name)
    {
    	if (getName().equals(name)) return this;
    	for (Iterator<AttributeDef> it = getAttributeDefs().iterator();it.hasNext();)
    	{
    		AttributeDef at = it.next();
    		if (at.getName().equals(name)) return at;
    	}
    	for (Iterator<ElementDef> it = getChildElements().iterator();it.hasNext();)
    	{
    		TreeElement desc = it.next().namedDescendant(name);
    		if (desc != null) return desc;
    	}
    	return null;
    }
    
    /** return a named child element of this element, or null if there is none.  */
    public TreeElement namedChild(String name)
    {
    	for (Iterator<ElementDef> it = getChildElements().iterator();it.hasNext();)
    	{
    		ElementDef desc = it.next();
    		if (desc.getName().equals(name)) return desc;
    	}
    	return null;    	
    }

    /** Return the unique subtree of this tree.
     * This includes all descendant nodes which must appear once and only once. 
     * Only tag names are copied correctly into the subtree (e.g. no mappings)*/
    
    public TreeElement uniqueSubtree() {return uniqueSubtreeED();}

    // method must deliver an ElementDef to be used in recursion
    public ElementDef uniqueSubtreeED()
    {
    	ElementDef ed = (ElementDef)MapperFactory.eINSTANCE.create(MapperPackage.Literals.ELEMENT_DEF);
    	ed.setName(getName());
    	for (Iterator<AttributeDef> it = getAttributeDefs().iterator();it.hasNext();)
    	{
    		AttributeDef at = it.next();
    		if (at.getMinMultiplicity() == MinMult.ONE)
    		{
    	    	AttributeDef ad = (AttributeDef)MapperFactory.eINSTANCE.create(MapperPackage.Literals.ATTRIBUTE_DEF);
    	    	ad.setName(at.getName());
    			ed.getAttributeDefs().add(ad);
    		}
    	}
    	
    	for (Iterator<ElementDef> it = getChildElements().iterator();it.hasNext();)
    	{
    		ElementDef desc = it.next();
    		if ((desc.getMinMultiplicity() == MinMult.ONE) && 
    				(desc.getMaxMultiplicity() == MaxMult.ONE))
    					ed.getChildElements().add(desc.uniqueSubtreeED());
    	}
    	return ed;
    }
    
    /** tree element for the ith child */
    public TreeElement childTreeElement(int i)
    {
    	return getChildElements().get(i);
    }
    
    /** name of the ith attribute */
    public String attribute(int i)
    {
    	return getAttributeDefs().get(i).getName();
    }

    /** This treeElement represents a whole document.
     * return the treeElement rooted at a node, which is
     * reached from the root by the path XPath; or null if there is no such tree.
     * Write an error message if doMessage = true */
     public TreeElement fromRootPath(Xpth XPath, boolean doMessage) throws XpthException
     {
    	 NodeDef res = null;
    	 EObject parent = eContainer();
    	 if (parent instanceof MappedStructure)
    	 {
    		 res = ((MappedStructure)parent).getNodeDefByPath(XPath.stringForm());
    		 if (doMessage && (res == null))
    			 System.out.println("There is no path '" + XPath.stringForm() + "'");
    	 }
    	 else if (doMessage) System.out.println("fromRootPath called from a non-root node");
    	 return res;
     }

    //-----------------------------------------------------------------------------------------------
    //                   Tracing methods - not yet implemented
    //-----------------------------------------------------------------------------------------------

    /** write out the element tag names of one maximum-depth descent */
    public void writeOneDeepestBranch() {}
    
    /** write out all tag names in this tree, in order
    of increasing minimum depth. */
    public void writeAllTagNames() {}

     /** number of elements in the tree */
      public int size()
      {
    	 int size = 1;
      	for (Iterator<ElementDef> it = getChildElements().iterator();it.hasNext();)
      		size = size + it.next().size();
      	return size;
      }
      
      /** the maximum depth of this tree */
      public int maxDepth()
      {
    	  int depth = 1;
    	  if (getAttributeDefs().size() > 0) depth = 2;
          for (Iterator<ElementDef> it = getChildElements().iterator();it.hasNext();)
          {
        	  int nextDepth = it.next().maxDepth() + 1;
        	  if (nextDepth > depth) depth = nextDepth;
          }
          return depth;
      }
      
     
     /** number of elements and attributes in the tree */
     public int sizeWithAttributes()
     {
    	 int size = 1 + getAttributeDefs().size();
       	for (Iterator<ElementDef> it = getChildElements().iterator();it.hasNext();)
       		size = size + it.next().sizeWithAttributes();
       	return size;    	 
     }

     /** write out a nested form of the Element/attribute tree */
     public void writeNested(messageChannel mChan) {writeNested(mChan,"");}

     public void writeNested(messageChannel mChan, String indent) 
     {
    	 String fullName = getName() + ": ";
    	 for (Iterator<AttributeDef> it  = this.getAttributeDefs().iterator(); it.hasNext();)
    		 fullName = fullName + it.next().getName() + " ";
    	 mChan.message(indent + fullName);
    	 String newIndent = "\t" + indent;
    	 for (Iterator<ElementDef> it  = this.getChildElements().iterator();it.hasNext();)
    		 it.next().writeNested(mChan, newIndent);
     }
     
     protected boolean mixed = false;
     
     /**
      * @return true if the type of the Element has the XML schema 'mixed' property;
      * default is false.
      */
     public boolean isMixed() {return mixed;}
     
     /**
      * assert that the element does or does not have the XML schema 'mixed' property
      * @param mixed true if the Element is mixed
      */
     public void setIsMixed(boolean mixed) {this.mixed = mixed;}
     
     /**
      * when the ordinal position of an element amongst its siblings has meaning, 
      * the mapped structure has an AttributeDef of this name on the ElementDef.
      * XML Reading and writing behaves as if the attribute exists and its value is
      * the ordinal position of the element. 
      * Then the attribute must be mapped onto some property in the class model.
      */
     public static String ELEMENT_POSITION_ATTRIBUTE = "element_position_virtual_att";
     
     /**
      * @param typeNames: names of ElementDef types, to stop infinite recursion
      * @return the number of mappable nodes (ElementDefs and AttributeDefs)
      * in the subtree of this element, stopping the recursion at any repeated type
      */
     public int countNodesInSubtree(Vector<String> typeNames, StructureDefinition strucDef)
     	throws MapperException
     {
    	 int count = 0;
    	 if (!isExpanded() && (strucDef != null) && (getType() != null))
    	 {
    		 ElementDef expanded = strucDef.typeStructure(getType());
    		 expanded.setExpanded(true);
    		 return expanded.countNodesInSubtree(typeNames, strucDef);
    	 }
    	 else if (isExpanded())
    	 {
    		 // count this element and all its attributes
    		 count = 1 + getAttributeDefs().size();
    		 
    		 // count child subtrees
    		 if (childElements != null)
    			 for (Iterator<ElementDef> it = childElements.iterator();it.hasNext();)
    		 {
    			 ElementDef child = it.next();
    			 String type = child.getType();
    			 boolean visited = ((type != null) && (GenUtil.inVector(type, typeNames)));
    			 if (!visited)
    			 {
    				 Vector<String> nextTypeNames = new Vector<String>();
    				 for (Iterator<String> is = typeNames.iterator();is.hasNext();) nextTypeNames.add(is.next());
    				 nextTypeNames.add(getType());
    				 count = count + child.countNodesInSubtree(nextTypeNames,strucDef);    				 
    			 }
    		 }
    	 }
    	 return count;
     }



} //ElementDefImpl
