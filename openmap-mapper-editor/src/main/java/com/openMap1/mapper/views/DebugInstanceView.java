package com.openMap1.mapper.views;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * Shows a tree-structured view of the XML instance 
 * being used to debug a mapping set. 
 * Highlights the current node.
 * 
 * @author robert
 *
 */
public class DebugInstanceView extends ViewPart {
	
	private boolean tracing = false;
	
	private TreeViewer viewer;

	private ArrayList<Node>  modelRoot = new ArrayList<Node>();
	

	//---------------------------------------------------------------------------------------------
	//                           constructor and initialisation
	//---------------------------------------------------------------------------------------------

	public DebugInstanceView() {
	}

	/**
	 * Callback to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		// this column shows the tree , and gets its label provider from the viewer
		TreeViewerColumn tv1 = new TreeViewerColumn(viewer,SWT.LEFT);
		tv1.getColumn().setWidth(300);
		tv1.getColumn().setText("Node Tree");
		
		TreeViewerColumn tv2 = null; 
		tv2 = new TreeViewerColumn(viewer,SWT.LEFT);
		tv2.getColumn().setWidth(300);
		tv2.getColumn().setText("Value");
		
		
	}
	
	public void setXMLRoot(Element root)
	{
		trace("Set XML root");
		modelRoot = new ArrayList<Node>();
		modelRoot.add(root);
		
		/* set up the viewer, and give it the root of the tree. */
		setUpViewer();
		viewer.setInput(modelRoot);		
	}
	
	private void setUpViewer()
	{
		viewer.setContentProvider(new DebugInstanceViewContentProvider());
		viewer.setLabelProvider(new DebugInstanceViewLabelProvider());
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);				
	}



	/*
	 * The provider (= adapter) which adapts the model (in this case an EMF model)
	 * to the viewer (in this case a TreeViewer). 
	 * There are two parts - a content provider which provides the tree structure
	 * and a label provider which provides the icons and text
	 */
	class DebugInstanceViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {

		// called by viewer.setInput(Object) but need do nothing
		public void inputChanged(Viewer v, Object oldInput, Object newInput) 
		{}

		public void dispose() {trace("vanilla dispose");}

		public Object[] getElements(Object parent) {
			// the only child of modelroot is the root node 
			if (parent.equals(modelRoot)) {
				return arrayOf(modelRoot);
			}
			// for all other objects use getChildren
			return getChildren(parent);
		}
		
		/**
		 * child Nodes:
		 */
		public Object [] getChildren(Object parent) {
			ArrayList<Node> result = new ArrayList<Node>();			
			if (parent instanceof Element)
			{
				Element el = (Element)parent;
				NodeList nl = el.getChildNodes();
				for (int i=0; i < nl.getLength();i++)
				{
					Node n = nl.item(i);
					if (n instanceof Element) result.add(n);
				}
				NamedNodeMap attMap = el.getAttributes();
				for (int ia= 0; ia < attMap.getLength(); ia++) result.add(attMap.item(ia));
			}
			// Attributes have no child nodes
			return arrayOf(result);
		}

		/**
		 * get the parent object of any node in the tree. 
		 */
		public Object getParent(Object child) {
			Node parent = null;
			if (child instanceof Node)
			{
				Node nd = (Node)child;
				parent = nd.getParentNode();
			}			
			return parent;
		}
		
		public boolean hasChildren(Object parent) {
			return(getChildren(parent).length > 0);
		}
	} // end of class ViewContentProvider
	
	private Node[] arrayOf (ArrayList<Node> aList) {return (Node[])aList.toArray(new Node[aList.size()]);} 


	class DebugInstanceViewLabelProvider extends LabelProvider implements ITableLabelProvider{

		public String getText(Object obj) {
			if (obj instanceof Node)
				return ((Node)obj).getNodeName();
			return "not a Node";
		}
		
		public String getColumnText(Object obj, int columnIndex) {
			switch(columnIndex){
				case 0: return getText(obj);
				case 1: {
					if (obj instanceof Element)
						return XMLUtil.getText((Element)obj);					
					else if (obj instanceof Attr)
						return ((Attr)obj).getValue();					
				}
			}
			return ("");
		}

		public Image getColumnImage(Object obj, int columnIndex) {
			switch(columnIndex){
				case 0: return getImage(obj);
				case 1: return null;
			}
			return null;			
		}

		public Image getImage(Object obj) {
			Image im = null;
			// images for XML Elements and Attributes
			if (obj instanceof Element)
				{im = FileUtil.getImage("Element");} 
			else if (obj instanceof Attr)
				{im = FileUtil.getImage("Attribute");} 
			return im;
		}	
	}
	
	
	/**
	 * Expand the tree to show a selected node
	 * @param nd
	 */
	public void showSelectedNode(Node nd)
	{
		if (nd != null) 
		{
			trace("Showing node " + nd.getNodeName());
			viewer.expandToLevel(nd, 1);
			viewer.setSelection(new StructuredSelection(nd));
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	private void trace(String s) {if (tracing) System.out.println(s);}
	

}
