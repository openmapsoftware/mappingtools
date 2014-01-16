/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package com.openMap1.mapper.impl;

import java.util.Vector;

import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.XpthException;
import com.openMap1.mapper.writer.TreeElement;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.AttributeDef;
import com.openMap1.mapper.MapperPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class AttributeDefImpl extends NodeDefImpl implements AttributeDef {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttributeDefImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MapperPackage.Literals.ATTRIBUTE_DEF;
	}
	
	//--------------------------------------------------------------------------------------------
	//         Interface TreeElement  - parts specific to AttributeDefs
	//--------------------------------------------------------------------------------------------

	public boolean isUnique() {return true;}
    
    /** vector of attributes of this element */
    public Vector<String> attributes() {return new Vector<String>();}
    
    /** true if attribute number i is optional */
    public boolean isOptionalAtt(int i) {return false;}
    
    /** vector of child tree elements */
    public Vector<TreeElement> childTreeElements() {return new Vector<TreeElement>();}

    /** return a named descendant element of this element (possibly itself), or null if there is none.  */
    public TreeElement namedDescendant(String name) {return null;}
    
    /** return a named child element of this element, or null if there is none.  */
    public TreeElement namedChild(String name) {NodeDefImpl ad = null; return ad;}

    /** Return the unique subtree of this tree.
     * This includes all descendant nodes which must appear once and only once. */
    public TreeElement uniqueSubtree() {return this;}
    
    /** tree element for the ith child */
    public TreeElement childTreeElement(int i) {return null;}
    
    /** name of the ith attribute */
    public String attribute(int i) {return null;}

    /** This treeElement represents a whole document.
     * return the treeElement rooted at a node, which is
     * reached from the root by the path XPath; or null if there is no such tree.
     * Write an error message if doMessage = true */
     public TreeElement fromRootPath(Xpth XPath, boolean doMessage) throws XpthException {return null;}

    //-----------------------------------------------------------------------------------------------
    //                                   Tracing methods
    //-----------------------------------------------------------------------------------------------

    /** write out the element tag names of one maximum-depth descent */
    public void writeOneDeepestBranch(){}
    
    /** write out all tag names in this tree, in order
    of increasing minimum depth. */
    public void writeAllTagNames(){}

     /** number of elements in the tree */
      public int size(){return 1;}
      
      /** the maximum depth of this tree */
      public int maxDepth(){return 1;}
     
     /** number of elements and attributes in the tree */
     public int sizeWithAttributes(){return 1;}

     /** write out a nested form of the tree */
     public void writeNested(messageChannel mChan){}


} //AttributeImpl
