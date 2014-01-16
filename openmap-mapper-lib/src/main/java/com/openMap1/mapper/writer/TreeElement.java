package com.openMap1.mapper.writer;

import java.util.Vector;

import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.XpthException;
import com.openMap1.mapper.util.messageChannel;

/**
 * Interface describing a node in an XML tree 
 * and its child nodes.
 * 
 * @author robert
 *
 */
public interface TreeElement {

    /** tag name, including namespace prefix */
    public String tagName();
    
    public boolean isOptional();
    
    public boolean isUnique();
    
    /** vector of attributes of this element */
    public Vector<String> attributes();
    
    /** true if attribute number i is optional */
    public boolean isOptionalAtt(int i);
    
    /** vector of child tree elements */
    public Vector<TreeElement> childTreeElements();

    /** return a named descendant element of this element (possibly itself), or null if there is none.  */
    public TreeElement namedDescendant(String name);
    
    /** return a named child element of this element, or null if there is none.  */
    public TreeElement namedChild(String name);

    /** Return the unique subtree of this tree.
     * This includes all descendant nodes which must appear once and only once. */
    public TreeElement uniqueSubtree();
    
    /** tree element for the ith child */
    public TreeElement childTreeElement(int i);
    
    /** name of the ith attribute */
    public String attribute(int i);

    /** This treeElement represents a whole document.
     * return the treeElement rooted at a node, which is
     * reached from the root by the path XPath; or null if there is no such tree.
     * Write an error message if doMessage = true */
     public TreeElement fromRootPath(Xpth XPath, boolean doMessage) throws XpthException;

    //-----------------------------------------------------------------------------------------------
    //                                   Tracing methods
    //-----------------------------------------------------------------------------------------------

    /** write out the element tag names of one maximum-depth descent */
    public void writeOneDeepestBranch();
    
    /** write out all tag names in this tree, in order
    of increasing minimum depth. */
    public void writeAllTagNames();

     /** number of elements in the tree */
      public int size();
      
      /** the maximum depth of this tree */
      public int maxDepth();
     
     /** number of elements and attributes in the tree */
     public int sizeWithAttributes();

     /** write out a nested form of the tree */
     public void writeNested(messageChannel mChan);


}
