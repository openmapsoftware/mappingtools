package com.openMap1.mapper.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.graphics.Image;

import org.eclipse.core.runtime.IPath;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.dialogs.SaveAsDialog;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.emf.edit.ui.util.EditUIUtil;

import org.eclipse.xsd.XSDNamedComponent;

import com.openMap1.mapper.presentation.MapperEditor;
import com.openMap1.mapper.presentation.QueryEditor;
import com.openMap1.mapper.presentation.MapperActionBarContributor;

import com.openMap1.mapper.util.EclipseFileUtil;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.Timer;

import com.openMap1.mapper.mapping.MDLBase;
import com.openMap1.mapper.mapping.objectMapping;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.util.SystemMessageChannel;

import com.openMap1.mapper.actions.CopySimplificationsAction;
import com.openMap1.mapper.actions.ImportSimplificationsAction;
import com.openMap1.mapper.actions.MakeEcoreMappingsAction;
import com.openMap1.mapper.actions.MergeModelsAction;
import com.openMap1.mapper.actions.PasteSimplificationsAction;
import com.openMap1.mapper.actions.RemoveSimplificationsAction;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.ParameterClass;


/**
 * Shows a tree-structured view of the mapped class model, and the mappings.
 * 
 * Usually the tree structure is the class hierarchy.
 * For HL7 RMIM applications, it is the RMIM tree.
 * 
 * @author robert
 *
 */

public class ClassModelView extends ViewPart implements ISelectionChangedListener{
	
	private boolean tracing  = false;
	
	private boolean writeMappingsColumn = true;
	
	private Timer timer;

	private TreeViewer viewer;
	
	private ArrayList<EObject>  modelRoot = new ArrayList<EObject>();
	
	private ArrayList<LabelledEClass> rmimRoot = new ArrayList<LabelledEClass>();
	
	/* record the class tree children of each named class in the model, including 'Object' 
	 * These are the classes which have that class as 'main' superclass - only for purposes
	 * of tree display. Multiple inheritance of attributes and associations is not ignored. */
	private Hashtable<String,ArrayList<EClass>> childVanillaClasses;

	/** the package which is the root of the model */
	public EPackage ecoreRoot() {return ecoreRoot;}
	private EPackage ecoreRoot;
	
	private URI classModelURI = null;
	/** the URI address of the stored class model */
	public URI classModelURI() {return classModelURI;}
	
	private URI mappingSetURI = null;
	/** the URI address of the stored mapping set, if the view is currently linked to one */
	public URI mappingSetURI() {return mappingSetURI;}
	public void setMappingSetURI(URI uri) {mappingSetURI = uri; queryURI = null;}
	
	private URI queryURI = null;
	/** the URI address of the stored query, if the view is currently linked to one */
	public URI queryURI() {return queryURI;}
	public void setQueryURI(URI uri) {queryURI = uri; mappingSetURI = null;}
	
	private MapperEditor mapperEditor = null;
	public MapperEditor mapperEditor() {return mapperEditor;}

	private MDLBase mdlBase = null;
	
	public LabelledEClass topLabelledEClass() {return topLabelledEClass;}
	private LabelledEClass topLabelledEClass;
		
    private Hashtable<String,Vector<String>> parentTable;

	//---------------------------------------------------------------------------------------------
	//                           constructor and initialisation
	//---------------------------------------------------------------------------------------------

	public ClassModelView() {
	}

	/**
	 * Callback to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		trace("creating part control");
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		// this column shows the tree , and gets its label provider from the viewer
		TreeViewerColumn tv1 = new TreeViewerColumn(viewer,SWT.LEFT);
		tv1.getColumn().setWidth(300);
		tv1.getColumn().setText("Class");
		
		TreeViewerColumn tv2 = null; 
		if (writeMappingsColumn)
		{
			tv2 = new TreeViewerColumn(viewer,SWT.LEFT);
			tv2.getColumn().setWidth(300);
			tv2.getColumn().setText("Mappings/Templates");
			tv2.setLabelProvider(new MappingLabelProvider());			
		}
		
		// set up this part to be a selection provider for editors and other views
		getSite().setSelectionProvider(viewer);
		//make this view listen to its own selections, to remember what was selected
		viewer.addPostSelectionChangedListener(this);
		
		if (classModelURI != null) try{

			// set up the class model and connect the viewer to it
			EObject root = FileUtil.getEMFModelRoot(classModelURI);
			if (!(root instanceof EPackage)) throw new MapperException("Class model root is not an EPackage");
			ecoreRoot = (EPackage)root;

			// set up the appropriate content provider and label provider for the class model
			setupViewer((EPackage)root, classModelURI);
			
			/* if at the previous shutdown, the class model view was connected to a mapper editor,
			 * re-connect it to the same one. */
			if (mappingSetURI != null)
			{
				MapperEditor me = WorkBenchUtil.getMapperEditor(mappingSetURI.toString());
				if ((me != null) && (root != null))
					viewer.addPostSelectionChangedListener((MapperActionBarContributor)me.getActionBarContributor());
			}
			
			/* if at the previous shutdown, the class model view was connected to a query editor,
			 * re-connect it to the same one. */
			if (queryURI != null)
			{
				QueryEditor qe = WorkBenchUtil.getQueryEditor(queryURI.toString());
				if ((qe != null) && (root != null)) 
					connectToQueryEditor(qe);
			}
			
		}
		catch (Exception ex) 
		{
			System.out.println("Failed to open class model for class model view, at  location '" 
					+ classModelURI.toString() + "'");
		}		
		
		
		
		// make the menu actions
		makeActions();
		contributeToActionBars();
		
		
	}
	
	/* connect to the Attribute and Association Views, if you can; 
	 * 'false' means do not force creation; Eclipse objects to the potential recursion */
	private void connectToFeatureViews()
	{
		AttributeView atv = WorkBenchUtil.getAttributeView(false);
		if (atv != null) connectToAttributeView(atv);
		AssociationView asv = WorkBenchUtil.getAssociationView(false);
		if (asv != null) connectToAssociationView(asv);				
	}
	
	private void setupViewer(EPackage ecoreRoot, URI  uri)
	{
		trace("Set up viewer");

		// viewing a class model as an RMIM tree
		if (isRMIMRoot(ecoreRoot))
		{
			setRMIMClassModel(ecoreRoot, uri);				
			setupRMIMViewer(viewer);
		}

		// viewing a class model grouped by packages
		else if (groupInPackages(ecoreRoot))
		{
			setPackagedClassModel(ecoreRoot,uri);				
			setupPackagedViewer(viewer);
			
		}

		// viewing a class model as an inheritance hierarchy
		else
		{
			setVanillaClassModel(ecoreRoot,uri);				
			setupVanillaViewer(viewer);
		}
		
		// connect to the Associations and Attributes views
		connectToFeatureViews();
	}
	
	/**
	 * Set up the TreeViewer with the appropriate content provider and label provider,
	 * for a vanilla class model; the tree structure follows the inheritance structure
	 * @param viewer
	 */
	private void setupVanillaViewer(TreeViewer viewer)
	{
		trace("set up vanilla viewer");
		viewer.setContentProvider(new ClassModelViewContentProvider());
		viewer.setLabelProvider(new ClassModelViewLabelProvider());
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);		
	}
	/**
	 * Set up the TreeViewer with the appropriate content provider and label provider,
	 * for a class model to be viewed by package
	 * @param viewer
	 */
	private void setupPackagedViewer(TreeViewer viewer)
	{
		trace("set up packaged viewer");
		viewer.setContentProvider(new PackagedViewContentProvider());
		viewer.setLabelProvider(new PackagedViewLabelProvider());
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);		
	}
	
	/**
	 * Set up the TreeViewer with the appropriate content provider and label provider,
	 * for an RMIM class model; the tree structure follows the RMIM associations
	 * @param viewer
	 */
	private void setupRMIMViewer(TreeViewer viewer)
	{
		trace("set up RMIM viewer");
		viewer.setContentProvider(new RMIMViewContentProvider());
		viewer.setLabelProvider(new RMIMViewLabelProvider());
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);		
	}
	
	/**
	 * 
	 * @param root
	 * @return true if the class model is to be shown in an RMIM hierarchy view
	 */
	public static boolean isRMIMRoot(EObject root)
	{
		if ((root != null) && (root instanceof EPackage))
			return (ModelUtil.getMIFAnnotation((EPackage)root,"RMIM") != null);
		return false;		
	}
	
	/* replaced by calls to ModelUtil.getMIFAnnotation
	private String getAnnotation(EModelElement eo, String key)
	{
		String mifNamespaceURI = "urn:hl7-org:v3/mif2";
		String value = null;
		EAnnotation ann = eo.getEAnnotation(mifNamespaceURI);
		if (ann != null) value = ann.getDetails().get(key);
		return value;
	}
	*/
	
	/**
	 * 
	 * @param eo an EMF model object - root of the package tree
	 * @return true if the classes are to be viewed grouped by packages,
	 * rather than by an inheritacne hierarchy
	 */
	private Boolean groupInPackages(EModelElement eo)
	{
		boolean group = false;
		EAnnotation ann = eo.getEAnnotation("urn:com.openMap1.mapper");
		String value = null;
		if (ann != null) value = ann.getDetails().get("viewByPackages");
		if (value != null)group = (value.equals("true"));
		return group;		
	}
	
	
	/**
	 * do the startup activities when this view is associated with a mapping set
	 * and its active mapper editor
	 * @param MapperEditor me the active mapper editor
	 * @param EObject umlRoot the root of the UML model being shown in this view
	 * @param String path the path to the UML model file, for use in the view title
	 */
	public void initiateForMapperEditor(MapperEditor me, EObject root, String classModelURIString)
	{
		trace("initiate for mapper editor");
		if ((root != null) && (root instanceof EPackage))
		{
			ecoreRoot = (EPackage)root;
			URI uri = URI.createURI(classModelURIString);
			
			setupViewer(ecoreRoot,uri);

			setContentDescriptionFromURL(classModelURIString);	
			setMappingSetURI(EditUIUtil.getURI(me.getEditorInput()));
			
			// hook up the mapper editor so it can listen to selections in this class model view
			viewer.addPostSelectionChangedListener((MapperActionBarContributor)me.getActionBarContributor());
			
			// record the editor 
			mapperEditor = me;
			mdlBase = null;
			
			viewer.refresh();
		}
		else if (root == null)
		{
			//System.out.println("Class Model Root is null in ClassModelView.initiateForMapperEditor"	);		
		}
		else if (!(root instanceof EPackage)) 
		{
			System.out.println("Class Model Root is not an EPackage, but a "
					+ root.eClass().getName());
			return;
		}
	}
	
	/**
	 * do the startup activities when this view is associated with a query
	 * and its active query editor
	 * @param QueryEditor qe the active query editor
	 * @param EObject umlRoot the root of the UML model being shown in this view
	 * @param String path the path to the query file, for use in the view title
	 */
	public void initiateForQueryEditor(QueryEditor qe, EObject root, String classModelURIString)
	{
		trace("initiate for query editor");
		if (timer == null) timer = new Timer("Class model view");
		timer.start(Timer.COMPILE); 
		if ((root != null) && (root instanceof EPackage))
		{
			this.ecoreRoot = (EPackage)root;
			URI uri = URI.createURI(classModelURIString);
			setupViewer(ecoreRoot,uri);
			setContentDescriptionFromURL(classModelURIString);	
			setQueryURI(EditUIUtil.getURI(qe.getEditorInput()));			
			connectToQueryEditor(qe);
			mapperEditor = null; // sever any previous link to a mapping set
			mdlBase = null;
			viewer.refresh();
		}
		else if (root == null)
		{
			System.out.println("Class Model Root is null in ClassModelView.initiateForQueryEditor"	);		
		}
		else if (!(root instanceof EPackage)) 
		{
			System.out.println("Ecore Class Model Root is not an EPackage, but a "
					+ root.eClass().getName());
			return;
		}
	}
	
	/**
	 * hook up the query editor so it can listen to selections in this class model view
	 * @param qe
	 */
	public void connectToQueryEditor(QueryEditor qe) 
		{
			viewer.addPostSelectionChangedListener(qe);
			mapperEditor = null; // sever any previous link to a mapping set
		}
	
	
	/**
	 * hook up the Attribute View so it can listen to selections in this class model view
	 * @param qe
	 */
	public void connectToAttributeView(AttributeView av) 
		{viewer.addPostSelectionChangedListener(av);}
	
	/**
	 * hook up the Association View so it can listen to selections in this class model view
	 * @param qe
	 */
	public void connectToAssociationView(AssociationView av) 
		{viewer.addPostSelectionChangedListener(av);}
	
	
	
	/**
	 * Strip out a file name from a URL, and use it as the description of the view
	 */
	public void setContentDescriptionFromURL(String url) 
	  {super.setContentDescription(FileUtil.getFileName(url));}
	
	//---------------------------------------------------------------------------------------------
	//           Setting up a class model to be viewed in packages for the content provider and label provider                   
	//---------------------------------------------------------------------------------------------
	
	/**
	 * record a UML class model in the form used by the ViewContentProvider -
	 * in the Hashtable of 'main' subclasses.
	 * This method assumes the class model is in a single package
	 * 
	 * @param root the root object of the UML model, assumed to be an EPackage
	 */
	public void setPackagedClassModel(EPackage root, URI classModelURI)
	{
		trace("Set packaged class model");
		this.classModelURI = classModelURI;
		modelRoot = new ArrayList<EObject>();
		modelRoot.add(root);
		
		/* ensure the viewer is expecting the right kind of content provider and label provider, 
		 * and give it the root of the tree. */
		setupPackagedViewer(viewer);
		viewer.setInput(modelRoot);		
	}

	//---------------------------------------------------------------------------------------------
	//           content provider and label provider for a class model to be viewed by package
	//---------------------------------------------------------------------------------------------

	/*
	 * The provider (= adapter) which adapts the model (in this case an EMF model)
	 * to the viewer (in this case a TreeViewer). 
	 * There are two parts - a content provider which provides the tree structure
	 * and a label provider which provides the icons and text
	 */
	class PackagedViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {

		// called by viewer.setInput(Object) but need do nothing
		public void inputChanged(Viewer v, Object oldInput, Object newInput) 
		{}

		public void dispose() {trace("packaged view dispose");}

		public Object[] getElements(Object parent) {
			// the only child of modelroot is the EPackage object 
			if (parent.equals(modelRoot)) {
				return arrayOf(modelRoot);
			}
			// for all other objects (including the package) use getChildren
			return getChildren(parent);
		}
		
		/**
		 * child EObjects:
		 * For a package, the first children are its sub-packages; next children are the classes in it
		 * For any class, the there are no children
		 * For any other EObject, get the EObject child nodes
		 */
		public Object [] getChildren(Object parent) {
			ArrayList<EObject> result = new ArrayList<EObject>();
			if (parent instanceof EPackage)
			{
				EPackage pack = (EPackage)parent;
				// sub-packages are the first children
				for (Iterator<EPackage> ip = pack.getESubpackages().iterator();ip.hasNext();)
					result.add(ip.next());
				// next follow classes in the package
				for (Iterator<EClassifier> ic = pack.getEClassifiers().iterator();ic.hasNext();)
				{
					EClassifier ec = ic.next();
					if (ec instanceof EClass) result.add(ec);
				}
			}
			return arrayOf(result);
		}

		/**
		 * get the parent object of any object in the tree. For EClasses, this is the 
		 * its containing package. for packages, the parent is its superpackage
		 */
		public Object getParent(Object child) {
			EObject parent = null;
			if (child instanceof EClass)
			{
				parent = ((EClass)child).getEPackage();
			}			
			else if (child instanceof EPackage)
			{
				parent = ((EPackage)child).getESuperPackage();
				if (parent == null) parent = ecoreRoot;
			}			
			return parent;
		}
		
		public boolean hasChildren(Object parent) {
			return(getChildren(parent).length > 0);
		}
	} // end of class PackagedViewContentProvider
	

	class PackagedViewLabelProvider extends LabelProvider implements ITableLabelProvider{

		public String getText(Object obj) {
			if (obj instanceof EObject)
				return getTextLabel((EObject)obj);
			return "not an EObject";
		}
		
		public String getColumnText(Object obj, int columnIndex) {
			switch(columnIndex){
				case 0: return getText(obj);
				case 1: {
					if (obj instanceof EClass)
						return getObjectMappingText((EClass)obj);					
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
			// default 'Folder' image if type is not recognised (eg packages)
			String imageKey = ISharedImages.IMG_OBJ_FOLDER;
			im = PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);			

			// images for Ecore classes
			if (obj instanceof EClass)
				{im = FileUtil.getImage("Class");} 
			return im;
		}
	
	private String getTextLabel(EObject eo)
	{
		if (eo instanceof ENamedElement) // for named parts of Ecore models: classes and packages
		{
			return ((ENamedElement)eo).getName(); 
		}
		else return eo.eClass().getName();
	}
	
	}

	
	//---------------------------------------------------------------------------------------------
	//           Setting up a  vanilla class model for the content provider and label provider                   
	//---------------------------------------------------------------------------------------------

	
	/**
	 * record a UML class model in the form used by the ViewContentProvider -
	 * in the Hashtable of 'main' subclasses.
	 * This method assumes the class model is in a single package
	 * 
	 * @param root the root object of the UML model, assumed to be an EPackage
	 */
	public void setVanillaClassModel(EPackage root, URI classModelURI)
	{
		trace("Set vanilla class model");
		this.classModelURI = classModelURI;
		childVanillaClasses = new Hashtable<String,ArrayList<EClass>>();
		modelRoot = new ArrayList<EObject>();
		modelRoot.add(root);
		
		addClasses(root);
		
		/* ensure the viewer is expecting the right kind of content provider and label provider, 
		 * and give it the root of the tree. */
		setupVanillaViewer(viewer);
		viewer.setInput(modelRoot);		
	}
	
	/**
	 * add the classes in a package to the model, and recursively add
	 * the classes in all its sub-packages
	 * @param aPackage
	 */
	private void addClasses(EPackage aPackage)
	{
		// add the classes in this package
		for (Iterator<EObject> it = aPackage.eContents().iterator(); it.hasNext();)
		{
			EObject child = it.next();

			// record each Class under its superclasses, or under class Object
			if (child instanceof EClass)  recordVanillaClass((EClass)child);
			
			// ignore other things in the package - imports etc.
			else {}
		}
		
		//do the same recursively for its sub-packages
		for (Iterator<EPackage> it = aPackage.getESubpackages().iterator();it.hasNext();)
			addClasses(it.next());
	}
	
	
	/**
	 * record each class under its first superclass, or under class 'Object' if it has none
	 * @param c
	 */
	private void recordVanillaClass(EClass c)
	{
		if (c.getESuperTypes().size() == 0)
		{
			addVanillaClassToSubClasses("Object",c);
		}
		// if the class has more than one superclass, choose the main superclass that it inherits most from
		else addVanillaClassToSubClasses(getMainSuperClass(c).getName(),c);
	}
	
	/**
	 * When an EClass has more than one superclass, the 'main' superclass,
	 * which will be its parent in the tree diagram of the mapped class model, is the 
	 * superclass with the largest number of features (i.e from which which it inherits most features). 
	 * When there is a tie for number of features, the class whose name is lexically first wins.
	 * @param c the EClass whose main superclass is sought
	 * @return the main EClass, or null if there are no superclasses
	 */
	private EClass getMainSuperClass(EClass c)
	{
		EClass best = null;
		for (Iterator<EClass> it = c.getESuperTypes().iterator();it.hasNext();)
		{
			EClass current = it.next();
			if (best == null) {best = current;}
			else if (current.getFeatureCount() > best.getFeatureCount()) {best = current;}
			else if ((current.getFeatureCount() == best.getFeatureCount())
					&&(current.getName().compareTo(best.getName()) < 0)) {best = current;}
			// compareTo: The result is a negative integer if this String object lexicographically precedes the argument string
		}
		return best;	
	}
	
	/**
	 * record a class under one of its superclasses
	 * @param superClass
	 * @param c
	 */
	private void addVanillaClassToSubClasses(String superClass,EClass c)
	{
		ArrayList<EClass> subs = childVanillaClasses.get(superClass);
		if (subs == null) subs = new ArrayList<EClass>();
		ArrayList<EClass> newSubs = addAClass(subs,c);
		childVanillaClasses.put(superClass,newSubs);
	}
	
	/**
	 * @param subs existing list of subclasses
	 * @param c new subclass
	 * @return enlarged list of subclasses, preserving alphabetical order of class names
	 */
	private ArrayList<EClass> addAClass(ArrayList<EClass> subs, EClass c)
	{
		ArrayList<EClass> newSubs = new ArrayList<EClass>();
		String cName = c.getName();
		boolean placed = false; // new class has not yet been added
		for (Iterator<EClass> it = subs.iterator();it.hasNext();)
		{
			EClass next = it.next();
			// if the new class comes before the current class and has not been added, add it once
			if ((next.getName().compareTo(cName) > 0) && (!placed))
			{
				newSubs.add(c);
				placed = true;
			}
			// add the current class in any case
			newSubs.add(next);
		}
		// if the new class is last, add it
		if (!placed) newSubs.add(c);
		return newSubs;
	}

	//---------------------------------------------------------------------------------------------
	//                content provider and label provider for a vanilla class model
	//---------------------------------------------------------------------------------------------


	/*
	 * The provider (= adapter) which adapts the model (in this case an EMF model)
	 * to the viewer (in this case a TreeViewer). 
	 * There are two parts - a content provider which provides the tree structure
	 * and a label provider which provides the icons and text
	 */
	class ClassModelViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {

		// called by viewer.setInput(Object) but need do nothing
		public void inputChanged(Viewer v, Object oldInput, Object newInput) 
		{}

		public void dispose() {trace("vanilla dispose");}

		public Object[] getElements(Object parent) {
			// the only child of modelroot is the EPackage object 
			if (parent.equals(modelRoot)) {
				return arrayOf(modelRoot);
			}
			// for all other objects (including the package) use getChildren
			return getChildren(parent);
		}
		
		/**
		 * child EObjects:
		 * For the package, the children are the classes with no superclass
		 * For any class, the children are (a) the subclasses, (b) the association markers (c) the properties
		 * For any other EObject, get the EObject child nodes
		 */
		public Object [] getChildren(Object parent) {
			ArrayList<EObject> result = new ArrayList<EObject>();

			String className = "NoClass"; // there will be no entry in subClasses map for this class name
			// direct children of the package are the subclasses of 'Object' = classes with no superclass
			if (parent instanceof EPackage) {className = "Object";}
			else if (parent instanceof EClass) {className = ((EClass)parent).getName();}
			if (childVanillaClasses.get(className) != null)
				for (Iterator<EClass> it = childVanillaClasses.get(className).iterator(); it.hasNext();)
						{result.add(it.next());}
			return arrayOf(result);
		}

		/**
		 * get the parent object of any object in the tree. For EClasses, this is the 
		 * 'main' superclass, or the package if there are no superclasses
		 */
		public Object getParent(Object child) {
			EObject parent = null;
			if (child instanceof EClass)
			{
				parent = getMainSuperClass((EClass)child);
				if (parent == null) parent = ecoreRoot;
			}			
			return parent;
		}
		
		public boolean hasChildren(Object parent) {
			return(getChildren(parent).length > 0);
		}
	} // end of class ViewContentProvider
	
	private EObject[] arrayOf (ArrayList<EObject> aList) {return (EObject[])aList.toArray(new EObject[aList.size()]);} 


	class ClassModelViewLabelProvider extends LabelProvider implements ITableLabelProvider{

		public String getText(Object obj) {
			if (obj instanceof EObject)
				return getTextLabel((EObject)obj);
			return "not an EObject";
		}
		
		public String getColumnText(Object obj, int columnIndex) {
			switch(columnIndex){
				case 0: return getText(obj);
				case 1: {
					if (obj instanceof EClass)
						return getObjectMappingText((EClass)obj);					
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
			// default 'Folder' image if type is not recognised (eg packages)
			String imageKey = ISharedImages.IMG_OBJ_FOLDER;
			im = PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);			

			// images for Ecore classes
			if (obj instanceof EClass)
				{im = FileUtil.getImage("Class");} 
			return im;
		}
	
	private String getTextLabel(EObject eo)
	{
		if (eo instanceof ENamedElement) // for named parts of Ecore models: classes and packages
		{
			return ((ENamedElement)eo).getName(); 
		}
		else if (eo instanceof XSDNamedComponent) // for XML schemas
		{
			return eo.eClass().getName() +  ": " + ((XSDNamedComponent)eo).getName(); 
		}
		else return eo.eClass().getName();
	}
	
	}

	//---------------------------------------------------------------------------------------------
	//                             setup for an RMIM  class model
	//---------------------------------------------------------------------------------------------
	
	/**
	 * find the LabelledEClass which is the root of the class tree of the RMIM
	 */
	public static LabelledEClass getRootLabelledEClass(EPackage topPackage)
	{
		LabelledEClass rootLabelledEClass = null;

		/* find the one EClass marked as the entry class; 
		 * iterate over all nested packages to find it. */
		for (Iterator<EPackage> ip = topPackage.getESubpackages().iterator();ip.hasNext();)
		{
			EPackage rmimPackage = ip.next();
			// Usually there is on package inside the top package. Iterate over classes in any packages found
			for (Iterator<EClassifier> ic = rmimPackage.getEClassifiers().iterator(); ic.hasNext();)
			{
				EClassifier ec = ic.next();
				if ((ec instanceof EClass) && (ModelUtil.getMIFAnnotation(ec, "entry") != null))
				{
					rootLabelledEClass = new LabelledEClass((EClass)ec, null, null);
				}
			}
		}
		
		/* if the class is not found in any packages below the top package  (possibly because there are no such packages)
		 * look for the class directly in the top package. */
		if (rootLabelledEClass == null)
		{
			// iterate over classes in the top package
			for (Iterator<EClassifier> ic = topPackage.getEClassifiers().iterator(); ic.hasNext();)
			{
				EClassifier ec = ic.next();
				if ((ec instanceof EClass) && (ModelUtil.getMIFAnnotation(ec, "entry") != null))
				{
					rootLabelledEClass = new LabelledEClass((EClass)ec, null, null);
				}
			}
			
		}
		
		return rootLabelledEClass;		
	}
	
	/**	 
	 * @param topPackage the root package of the RMIM class model,
	 * which has a single layer of sub-packages below it
	 */
	public void setRMIMClassModel(EPackage topPackage, URI classModelURI)
	{
		timer = new Timer("Class model view");
		this.classModelURI = classModelURI;
		rmimRoot = new ArrayList<LabelledEClass>();
		trace("Setting RMIM " + classModelURI);
		parentTable = ModelUtil.parentTable(topPackage);
		
		topLabelledEClass = getRootLabelledEClass(topPackage);
		rmimRoot.add(topLabelledEClass);
				
		/* ensure the viewer is expecting the right kind of content provider and label provider, 
		 * and give it the root of the tree. */
		setupRMIMViewer(viewer);
		viewer.setInput(rmimRoot);	
		
		/*
		Vector<String> noDups = new Vector<String>();
		int maximum = 100000000;
		int dataTypeDepth = 0;
		int start = 0;
		int leafCount = countLeafNodes(start,dataTypeDepth,maximum,topLabelledEClass.eClass(),noDups);
		System.out.println("Leaf nodes in class model at '" + classModelURI.toString() + "': " +  leafCount);
		*/
	}
	
	/**
	 * count leaf nodes in the RMIM class tree, truncating any recursion, 
	 * ignoring text content of elements, and ignoring occurrences of data type ANY
	 * @param start the count up to this point
	 * @param maximum a maximum it will not count beyond, to avoid memory overflow
	 * @param theClass class whose subtree is being added to the count
	 * @param noDups to truncate recursion whenever the same class name is encountered twice
	 * @return
	 */
	@SuppressWarnings("unused")
	private int countLeafNodes(int start,int dataTypeDepth, int maximum, EClass theClass,Vector<String> noDups)
	{
		// control depth into data type classes
		int MAX_DATATYPE_DEPTH = 1;
		int nextDTDepth = dataTypeDepth;
		if (theClass.getEPackage().getName().equals("datatypes"))nextDTDepth++;
		if (nextDTDepth > MAX_DATATYPE_DEPTH) return start;

		// control recursion of RMIM classes
		Vector<String> nextNoDups = new Vector<String>();
		for (Iterator<String> it = noDups.iterator();it.hasNext();)nextNoDups.add(it.next());
		nextNoDups.add(theClass.getName());

		// attribute leaf nodes of this class
		int count = start + theClass.getEAttributes().size();

		// leaf nodes of classes in subtrees
		for (Iterator<EReference> ir = theClass.getEAllReferences().iterator();ir.hasNext();) 
		{
			EReference ref = ir.next();
			EClassifier ec = ref.getEType();
			if ((ec instanceof EClass) && (ref.isContainment()) && (count < maximum))
			{
				EClass next = (EClass)ec;
				String name = next.getName();
				if ((!GenUtil.inVector(name, nextNoDups)) && (!name.equals("ANY"))) 
					count = countLeafNodes(count,nextDTDepth,maximum, next,nextNoDups);
			}
		}
		return count;
	}
	

	//---------------------------------------------------------------------------------------------
	//                content provider and label provider for an RMIM class model
	//---------------------------------------------------------------------------------------------
	


	/*
	 * The provider (= adapter) which adapts the model (in this case an EMF model)
	 * to the viewer (in this case a TreeViewer). 
	 * There are two parts - a content provider which provides the tree structure
	 * and a label provider which provides the icons and text
	 */
	class RMIMViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {

		// called by viewer.setInput(Object) but need do nothing
		public void inputChanged(Viewer v, Object oldInput, Object newInput) 
			{}

		public void dispose() {trace("RMIM dispose");}

		public Object[] getElements(Object parent) {
			// the only child of rmimRoot is the EPackage object 
			if (parent.equals(rmimRoot)) {
				return array2Of(rmimRoot);
			}
			// for all other objects (including the package) use getChildren
			return getChildren(parent);
		}
		
		/**
		 * child LabelledEClass objects:
		 * For the package, the children are the RMIM root class
		 * For any other LabelledEClass, get the LabelledEClass nodes for the classes linked by RMIM associations
		 */
		public Object [] getChildren(Object parent) {
			ArrayList<LabelledEClass> children = new ArrayList<LabelledEClass>();
			if (parent instanceof LabelledEClass)
				children = ((LabelledEClass)parent).getChildren();
			return array2Of(children);
		}
		
		private LabelledEClass[] array2Of(ArrayList<LabelledEClass> aList) 
			{return (LabelledEClass[])aList.toArray(new LabelledEClass[aList.size()]);} 

		/**
		 * get the parent object of any object in the tree. 
		 */
		public Object getParent(Object child) {
			LabelledEClass parent = null;
			if (child instanceof LabelledEClass)
			{
				parent = ((LabelledEClass)child).parent();
				if (parent == null) 
				{
					trace("null parent");
					// parent = (LabelledEClass)child; // ??
				}
			}			
			return parent;
		}
		
		public boolean hasChildren(Object parent) {
			return(getChildren(parent).length > 0);
		}
		
	} // end of class RMIMViewContentProvider
	

	class RMIMViewLabelProvider extends LabelProvider implements ITableLabelProvider{
		
		public String getColumnText(Object obj, int columnIndex) {
			switch(columnIndex){
				case 0: return getText(obj);
				case 1: {
					if (obj instanceof LabelledEClass)
						return getObjectMappingText((LabelledEClass)obj);					
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

		// label an RMIM class  by the association leading to it, followed by the class name
		public String getText(Object obj) {
			if (obj instanceof LabelledEClass)
			{
				LabelledEClass lc = (LabelledEClass)obj;
				String name = lc.eClass().getName();
				if (lc.associationName() != null) name = lc.associationName() + "." + name;
				EPackage CMETPackage = lc.eClass().getEPackage();
				String CMETName = ModelUtil.getEAnnotationDetail(CMETPackage, "name");
				if ((CMETName != null) && (!CMETName.equals("")))
					name = name + " (" + CMETName + ")";
				return name;
			}
			return "not a LabelledEClass";
		}

		public Image getImage(Object obj) {
			Image im = null;
			// default 'Folder' image if type is not recognised
			String imageKey = ISharedImages.IMG_OBJ_FOLDER;
			im = PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);			

			// images for RMIM classes and data type classes
			if (obj instanceof LabelledEClass)
			{
				EClass theClass = ((LabelledEClass)obj).eClass();
				String RIMClass = ModelUtil.getMIFAnnotation(theClass,"RIM Class");
				String imageName = "Class" + getColour(RIMClass); // "ClassGreen", "ClassRed", etc.
				
				String packageName = theClass.getEPackage().getName();
				if (packageName.equals("datatypes")) imageName = "DataType";
				im = FileUtil.getImage(imageName);				
			}
			return im;
		}
		
		/**
		 * @param RIMClass
		 * @return a string for the colour which clones of that class have in an RMIM diagram
		 */
		private String getColour(String RIMClass)
		{
			String colour = "";
			if (RIMClass != null)
			{
				if (GenUtil.inArray(RIMClass, redClasses)) colour = "Red";
				if (GenUtil.inArray(RIMClass, pinkClasses)) colour = "Pink";
				if (GenUtil.inArray(RIMClass, blueClasses)) colour = "Blue";
				if (GenUtil.inArray(RIMClass, yellowClasses)) colour = "Yellow";
				if (GenUtil.inArray(RIMClass, greenClasses)) colour = "Green";
			}
			return colour;
		}
		
	}
	
	
	// HL7 colouring of RIM classes
	static String[] redClasses = {"Act", "ControlAct", 
		"Observation","DiagnosticImage","PublicHealthCase",
		"Supply", "Diet", "DeviceTask",
		"FinancialContract","InvoiceElement","FinancialTransaction","Account",
		"PatientEncounter","SubstanceAdministration","WorkingList","Exposure",
		"Procedure"};
	static String[] pinkClasses = {"ActRelationship"};
	static String[] blueClasses = {"Participation","ManagedParticipation"};
	static String[] yellowClasses = {"Role", "RoleLink","Patient",
		"LicensedEntity","QualifiedEntity","Access","Employee"};
	static String[] greenClasses = {"Entity","Place","Person",
		"LivingSubject","NonPersonLivingSubject","Organization",
		"Material","Device","ManufacturedMaterial","Container"};
	
	//----------------------------------------------------------------------------------------
	//                  Label Provider for mapping columns 
	//----------------------------------------------------------------------------------------
	
	/**
	 * @return MDLBase the mappings in the mapping set being edited, 
	 * with fast retrieval methods
	 */
	public MDLBase mdlBase() 
	{
		// if there is no current Mapper editor, return no MDLBase
		if (mapperEditor == null) return null;
		if (mdlBase == null) refreshMDLBase();
		else if (mdlBase != null)
		{
			// if the mappings have been altered since the MDBase was last refreshed, refresh it now
			if (!WorkBenchUtil.mappingRoot(mapperEditor).classModelViewIsRefreshed())
				refreshMDLBase();
		}
		return mdlBase;
	}
	
	private void refreshMDLBase()
	{
		messageChannel mc = new SystemMessageChannel();
		try 
		{
			mdlBase = new MDLBase(WorkBenchUtil.mappingRoot(mapperEditor),mc);
			// record that the MDLBase has been refreshed
			WorkBenchUtil.mappingRoot(mapperEditor).setClassModelViewIsRrefreshed(true);
		}
		catch (MapperException ex) {GenUtil.surprise(ex, "ClassModelView.mdlBase");}					
	}

	
	/**
	 * @param ec a class
	 * @return a text description of the object mappings to the class and its
	 * subclasses, of the form (S)paths where S is the number of object mappings to 
	 * its proper subclasses, and 'paths' is the concatenated paths of the mappings to the class itself.
	 */
	private String getObjectMappingText(EClass ec) 
	{
		String end = ";   ";
		String text = "";
		if (mdlBase() != null) try
		{
			String className = ModelUtil.getQualifiedClassName(ec);
			for (Iterator<objectMapping> it = mdlBase().objectMappings(className).iterator(); it.hasNext();)
			{
				objectMapping om = it.next();
				String path = om.nodePath().stringForm();
				if (om.hasSubset()) path =  path + "(" + om.subset() + ")";
				text = text + path + end;
			}
			int mappingsToSubclasses = 0;
			for (Iterator<EClass> it = mdlBase().ms().getAllSubClasses(ec).iterator(); it.hasNext();)
			{
				EClass ed = it.next();
				if ((ec.isSuperTypeOf(ed)) && (ec != ed))
				{
					String subclassName = ModelUtil.getQualifiedClassName(ed);
					mappingsToSubclasses = 
						mappingsToSubclasses + mdlBase().objectMappings(subclassName).size();				
				}
			}
			if (mappingsToSubclasses > 0) text = "[" + mappingsToSubclasses + "] " + text;			
		}
		catch (MapperException ex) {}
		if (text.endsWith(end)) text = text.substring(0,text.length() - end.length());
		return text;
	}
	

	
	/**
	 * @param lec a labelled EClass
	 * @return a text description of the object mappings to this occurrence of the class
	 * at this point of the RMIM tree;
	 * or its template id if it has one
	 */
	private String getObjectMappingText(LabelledEClass lec)
	{
		String path= "";
		EClass ec = lec.eClass();
		LabelledEClass parent = lec.parent();
		String className = ModelUtil.getQualifiedClassName(ec);
		
		// modification to show template ids in stead of a mapping, if there are any
		String templateId = ModelUtil.getMIFAnnotation(ec, "template");
		if (templateId != null) 
		{
			boolean found = true;
			int index = 1; 
			// find all annotations with keys 'template_1','template_2', etc.
			while (found)
			{
				String nextId = ModelUtil.getMIFAnnotation(ec, "template_" + new Integer(index).toString());
				if (nextId != null) templateId = templateId + ";  " + nextId;
				else found = false;
				index++;
			}
			return templateId;
		}

		/* a class may only show some mapping text if (a) it is the parameter class 
		 * of the mapping set or (b) it is the root class or (c) its parent has 
		 * a mapping (already determined, as the parent node is visible)*/
		boolean eligible = ((isParameterClass(lec))||(parent == null)||
				((parent != null) && (parent.isMapped())));

		if ((mdlBase() != null) && eligible) try
		{
			// don't carry on looking after you have found the right object mapping
			boolean isLinkedToParent = false; 
			// there may be many object mappings to this class, most of which are elsewhere in the tree
			for (Iterator<objectMapping> it = mdlBase().objectMappings(className).iterator(); it.hasNext();)
			{
				objectMapping om = it.next();
				if (!isLinkedToParent)
				{

					// top of the RMIM tree, or top (parameter) class of an imported mapping set
					if ((isParameterClass(lec))|(parent == null)) 
						path = setTheObjectMappingText(lec, om);
					
					/* for any  class not at the top of the RMIM tree, its parent class must be 
					 * mapped, and the named association to this class must also be mapped 
					 * with the correct subsets.  */
					else
					{
						String parentClass= ModelUtil.getQualifiedClassName(parent.eClass());
						String parentSubset = parent.getMappedSubset();
						isLinkedToParent = (mdlBase().representsAssociationRoleLocally(parentClass, parentSubset,
								lec.associationName(), className,om.subset()));
						if (isLinkedToParent) path = setTheObjectMappingText(lec, om);
					}					
				}
			}
		}
		catch (Exception ex) {} // odd null pointers can occur when mappings have been deleted
		return path;
	}
	
	private String setTheObjectMappingText(LabelledEClass lec, objectMapping om)
	{
		String path = om.nodePath().stringForm();
		if (om.hasSubset()) path =  path + "(" + om.subset() + ")";
		lec.setObjectMappingText(path);
		lec.setMappedSubset(om.subset());						
		return path;
	}
	
	/**	 * 
	 * @param lec
	 * @return true if the EClass is the parameter class of the mapping set
	 */
	private boolean isParameterClass(LabelledEClass lec)
	{
		boolean isParamClass = false;
		if (mapperEditor != null)
		{
			MappedStructure ms = WorkBenchUtil.mappingRoot(mapperEditor);
			if (ms.getParameterClasses().size() > 0)
			{
				ParameterClass pc = ms.getParameterClasses().get(0);
				String paramClassName = pc.getQualifiedClassName();
				String mappedClassName = ModelUtil.getQualifiedClassName(lec.eClass());
				isParamClass = (mappedClassName.equals(paramClassName));
			}			
		}
		return isParamClass;
	}
	
	/**
	 * 
	 * @return if the mapping set has a  parameter class, return the 
	 * qualified parameter class name; otherwise return null
	 */
	private String parameterClassName()
	{
		String paramClassName = null;
		if (mapperEditor != null)
		{
			MappedStructure ms = WorkBenchUtil.mappingRoot(mapperEditor);
			if (ms.getParameterClasses().size() > 0)
			{
				ParameterClass pc = ms.getParameterClasses().get(0);
				paramClassName = pc.getQualifiedClassName();
			}			
		}
		return paramClassName;		
	}
	

	
	/**
	 * label provider for the mappings column of the class model or RMIM model
	 * @author robert
	 *
	 */
	class MappingLabelProvider extends ColumnLabelProvider{
		
		public MappingLabelProvider()
		{
		}
		
		

		public String getText(Object element) 
		{
			String text = "";
			System.out.println("getting text from label provider");
			
			// view of a vanilla class model
			if ((element instanceof EClass) && (mdlBase() != null))
			{
				EClass ec = (EClass)element;
				text = getObjectMappingText(ec);
			}
			// view of an RMIM class model
			else if ((element instanceof LabelledEClass) && (mdlBase() != null))
			{
				EClass ec = ((LabelledEClass)element).eClass();
				text = getObjectMappingText(ec);
			}
			return text;
		}
	}
	
	//----------------------------------------------------------------------------------------
	//                  Actions for the pull-down menu
	//----------------------------------------------------------------------------------------
	
	private Action saveClassModelChangesAction;
	private Action saveClassModelAsAction;
	private Action showLastSelectedClassAction;
	private MakeEcoreMappingsAction makeEcoreMappingsAction;
	private ImportSimplificationsAction importSimplificationsAction;
	private CopySimplificationsAction copySimplificationsAction;
	private PasteSimplificationsAction pasteSimplificationsAction;
	private RemoveSimplificationsAction removeSimplificationsAction;
	private MergeModelsAction mergeModelsAction;


	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(importSimplificationsAction);
		manager.add(copySimplificationsAction);
		manager.add(pasteSimplificationsAction);
		manager.add(removeSimplificationsAction);
		manager.add(mergeModelsAction);
		manager.add(saveClassModelChangesAction);
		manager.add(saveClassModelAsAction);
		manager.add(showLastSelectedClassAction);
		manager.add(makeEcoreMappingsAction);
	}

	/**
	 * make the actions for the pulldown menu in the Mapped Class Model view
	 */
	private void makeActions() {

		saveClassModelChangesAction = new Action() {
			public void run() {
				FileUtil.saveResource(ecoreRoot().eResource());
			}
		};
		saveClassModelChangesAction.setText("Save Changes in Class Model");
		saveClassModelChangesAction.setToolTipText("Save simplification annotation changes in class model");
		saveClassModelChangesAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		
		saveClassModelAsAction = new Action() {
			public void run() {
				doWriteClassModel();
			}
		};
		saveClassModelAsAction.setText("Save Class Model as..");
		saveClassModelAsAction.setToolTipText("Write out a class model to be used to generate Java classes");
		saveClassModelAsAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		
		showLastSelectedClassAction = new Action() {
			public void run() {
				String path = EclipseFileUtil.getSelectedClassPath(classModelURI.toString());
				TreePath pathToLastClass = getLabelledTreePath(path);
				if (pathToLastClass != null)
				{
					viewer.setSelection(new TreeSelection(pathToLastClass),true); // make it visible					
				}
			}
		};
		showLastSelectedClassAction.setText("Show Last Selected Class");
		showLastSelectedClassAction.setToolTipText("Expand the tree to show the class model last selected by the user");
		showLastSelectedClassAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		
		makeEcoreMappingsAction = new MakeEcoreMappingsAction(this);
		
		importSimplificationsAction = new ImportSimplificationsAction();
		copySimplificationsAction = new CopySimplificationsAction();
		pasteSimplificationsAction = new PasteSimplificationsAction();
		removeSimplificationsAction = new RemoveSimplificationsAction();
		mergeModelsAction = new MergeModelsAction();
	}
	
	/**
	 * run the action to write the class model as an ecore file, for later code generation
	 */
	private void doWriteClassModel()
	{
		SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
		saveAsDialog.open();
		IPath path = saveAsDialog.getResult();
		if (path != null) {
			// use the platform URI, not the absolute one, and Eclipse will see the resource
			URI resURI = URI.createPlatformResourceURI(path.toString(),true);
			saveAtURI(ecoreRoot,resURI,"ecore");
		}		
	}
	
	/**
	 * Save an Ecore model at a given URI
	 * @param model an Ecore model
	 * @param resURI the URI it is to be saved at
	 */
	public void saveAtURI(EPackage model,URI resURI,String extension)
	{
		ResourceSet resourceSet = new ResourceSetImpl();
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().
			put(extension, new XMIResourceFactoryImpl());

		Resource resource = resourceSet.createResource(resURI);
		resource.getContents().add(model);
		try {
			resource.save(null);
		}
		catch (IOException ex)
		{
			System.out.println("Exception saving ecore resource at " 
					+ resURI.toString() + ": " + ex.getMessage());
		}		
	}

	

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	

	//------------------------------------------------------------------------------------------
	//                        saving and restoring the state of the view
	//------------------------------------------------------------------------------------------
	
	private static final String TAG_MAPPING_SET_URI = "mappingSetURI";
	private static final String TAG_CLASS_MODEL_URI = "instanceURI";
	private static final String TAG_QUERY_URI = "queryURI";
	
	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		if (classModelURI() != null)
			memento.putString(TAG_CLASS_MODEL_URI, classModelURI().toString());
		
		// at most one of mappingSetURI and queryURI will be non-null
		if (mappingSetURI() != null)
			memento.putString(TAG_MAPPING_SET_URI, mappingSetURI().toString());
		if (queryURI() != null)
			memento.putString(TAG_QUERY_URI, queryURI().toString());
	}
	
	/**
	 * re-initialise the model from its state at last closedown
	 * Note the viewer does not exist when this runs.
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);
		
		if (memento != null)
		{
			String cmURI = memento.getString(TAG_CLASS_MODEL_URI);
			if (cmURI != null) classModelURI = URI.createURI(cmURI);

			// at most one of mappingSetURI and queryURI will be non-null
			String msURI = memento.getString(TAG_MAPPING_SET_URI);
			if (msURI != null) setMappingSetURI(URI.createURI(msURI));

			String quURI = memento.getString(TAG_QUERY_URI);
			if (quURI != null) setQueryURI(URI.createURI(quURI));			
		}
	}
	
	//----------------------------------------------------------------------------------------
	//                  Expanding the tree view to show a mapped class
	//----------------------------------------------------------------------------------------
	
	/**
	 * @param qualifiedClassName the package name and class name of the class whose mapping has been selected
	 */
	public void showMappedClass(String qualifiedClassName,String subset)
	{
		// RMIM case; find the named LabelledEClass and expand to it
		if ((isRMIMRoot(ecoreRoot)) && (subset != null))
		{
			TreePath startPath = TreePath.EMPTY.createChildPath(topLabelledEClass);
			if (parameterClassName() != null) startPath = extendToParameterClass(startPath,parameterClassName());
			TreePath namedPath = findLabelledPath(qualifiedClassName,subset,startPath);
			if (namedPath != null)
			{
				viewer.setSelection(new TreeSelection(namedPath),true); // make it visible
			}
			else {System.out.println("Null tree path");}
		}

		// non-RMIM case; find the named EClass and expand to it
		if (!(isRMIMRoot(ecoreRoot)))
		{
			EClass namedClass = ModelUtil.getNamedClass(ecoreRoot, qualifiedClassName);
			if (namedClass != null)
			{
				viewer.expandToLevel(namedClass, 0);
				viewer.setSelection(new StructuredSelection(namedClass));
			}			
		}
	}
	
	
	/**
	 * recursive descent of a tree of mapped LabelledEClasses,
	 * looking for a class with the required class name and subset
	 * @param className
	 * @param subset 
	 * @param path
	 */
	private TreePath findLabelledPath(String className, String subset, TreePath path)
	{
		if (path == null) return null;
		LabelledEClass lc = (LabelledEClass)path.getLastSegment();
		// find out if the class is mapped, and if so, set its subset
		getObjectMappingText(lc);
		/* only try mapped classes and their mapped descendants 
		 * (usually any mapped class has all mapped ancestors) */
		if (lc.getMappedSubset() != null)
		{
			String cName = ModelUtil.getQualifiedClassName(lc.eClass());
			if ((subset.equals(lc.getMappedSubset())) && (className.equals(cName))) 
				return path;
			else for (Iterator<LabelledEClass> it = lc.getChildren().iterator();it.hasNext();)
			{
				TreePath childPath = path.createChildPath(it.next());
				TreePath targetPath = findLabelledPath(className, subset, childPath);
				if (targetPath != null) return targetPath;
			}
		}
		return null;
	}
	
	/**
	 * @param startPath a TreePath with one step - the entry class of the RMIM
	 * @param parameterClassName  name of the parameter class of the mapping set
	 * @return a TreePath to one example of the parameter class (there may be several)
	 * found by a tree search cut off at repeated class names
	 */
	private TreePath extendToParameterClass(TreePath startPath,String parameterClassName)
	{
		TreePath currentPath = startPath;
		
		// from parents of each class, find a path of parent classes to the top class
		Vector<String> parentsToRoot = new Vector<String>();
		parentsToRoot.add(parameterClassName);
		String topClassName = ModelUtil.getQualifiedClassName(topLabelledEClass.eClass());
		parentsToRoot = extendToAncestorClass(parentsToRoot,topClassName);

		// use these to construct a TreePath of LabelledEClass objects
		if (parentsToRoot != null) 
		{
			for (int i = 1; i < parentsToRoot.size(); i++)
			{
				LabelledEClass current = (LabelledEClass)currentPath.getLastSegment();
				boolean found = false;
				for (Iterator<LabelledEClass> ic = current.getChildren().iterator();ic.hasNext();)
				{
					LabelledEClass child = ic.next();
					if (ModelUtil.getQualifiedClassName(child.eClass()).equals(parentsToRoot.get(i)))
					{
						found = true;
						currentPath = currentPath.createChildPath(child);
					}
				}
				if (!found)  return null;
			}
			return currentPath;
		}
		return null;
	}
	
	private Vector<String> extendToAncestorClass(Vector<String>parentsToRoot,String className)
	{
		if (parentsToRoot.get(0).equals(className)) return parentsToRoot;
		else
		{
			Vector<String> parents = parentTable.get(parentsToRoot.get(0));
			for (Iterator<String> ip = parents.iterator(); ip.hasNext();)
			{
				String parent = ip.next();
				if (!GenUtil.inVector(parent, parentsToRoot))
				{
					Vector<String> newTrail = new Vector<String>();
					newTrail.add(parent);
					for (Iterator<String> is = parentsToRoot.iterator();is.hasNext();)
						newTrail.add(is.next());
					Vector<String> result = extendToAncestorClass(newTrail,className);
					if (result != null) return result;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param path
	 * @return the treePath of LabelledEClasses got by following a string path,
	 * or null if it cannot be followed
	 */
	private TreePath getLabelledTreePath(String path)
	{
		TreePath result = null;
		if (path != null)
		{
			StringTokenizer steps = new StringTokenizer(path,"/");
			String topName = steps.nextToken(); // check the first step, which should be the name of the top class
			if (topName.equals(topLabelledEClass.eClass().getName()))
			{
				result = TreePath.EMPTY.createChildPath(topLabelledEClass);
				LabelledEClass currentClass = topLabelledEClass;
				while ((steps.hasMoreTokens()) && (currentClass != null))
				{
					String step = steps.nextToken();
					currentClass = currentClass.getNamedAssocChild(step);
					if (currentClass != null) result = result.createChildPath(currentClass);
					else result = null;
				}
			}			
		}
		return result;
	}
	
	//-----------------------------------------------------------------------------------------------------------------
	//     Listening for the selection of a new class in this View, for copy and paste of simplification annotations,
	//     and also to reopen the class model at the last selected class
	//-----------------------------------------------------------------------------------------------------------------
    
    private LabelledEClass selectedLabelledEClass = null;
    /** get the node that has been selected, for copy and paste of simplification annotations */
    public LabelledEClass getSelectedLabelledEClass() {return selectedLabelledEClass;}
    
    private LabelledEClass copiedLabelledEClass = null;
    /** set the node that has been copied, for later paste of simplification annotations */
    public void setCopiedLabelledEClass(LabelledEClass copied) {copiedLabelledEClass= copied;}
    /** get the node that has been copied, for later paste of simplification annotations */
    public LabelledEClass getCopiedLabelledEClass() {return copiedLabelledEClass;}
	
	/**
	 * remember the node selected in this view, as a local variable
	 * and remember the path to it as a local variable and in an XML file in the workspace
	 */
    public void selectionChanged(SelectionChangedEvent event)
	{
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection && ((IStructuredSelection)selection).size() == 1) {
			Object object = ((IStructuredSelection)selection).getFirstElement();


			// RMIM class model view
			if (object instanceof LabelledEClass)
			{
				selectedLabelledEClass = (LabelledEClass)object;
				String path  = selectedLabelledEClass.getPath();
				try {EclipseFileUtil.saveClassPath(classModelURI().toString(), path);}
				catch (MapperException ex) {trace("Exception saving path to selected class:" + ex.getMessage());}
			}
		}				
	}


	//----------------------------------------------------------------------------------------
	//                          Trivia
	//----------------------------------------------------------------------------------------
	
	private void trace(String s)
	{ if (tracing) System.out.println(s);}

}