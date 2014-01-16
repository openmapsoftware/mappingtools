package com.openMap1.mapper.reader;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;


import com.openMap1.mapper.impl.ElementDefImpl;
import com.openMap1.mapper.mapping.DebugRow;
import com.openMap1.mapper.mapping.MDLBase;
import com.openMap1.mapper.mapping.AssociationMapping;
import com.openMap1.mapper.mapping.associationEndMapping;
import com.openMap1.mapper.mapping.MappingTwo;
import com.openMap1.mapper.mapping.propertyMapping;
import com.openMap1.mapper.mapping.objectMapping;
import com.openMap1.mapper.mapping.Condition;
import com.openMap1.mapper.mapping.whenCondition;
import com.openMap1.mapper.mapping.linkCondition;
import com.openMap1.mapper.mapping.propertyConversion;

import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.Timer;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.util.XPathAPI;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.ModelUtil;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MDLReadException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.notRepresentedException;
import com.openMap1.mapper.core.PropertyConversionException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.MapperPackage;
import com.openMap1.mapper.Mapping;
import com.openMap1.mapper.NodeDef;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Enumeration;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
*  For any XML documents with an MDL definitions,
*  reads  the document in terms of object model used in the MDL.
*  Makes available the objects, properties and associations represented
*  in the document, by a generic interface.
*
*  @author  Robert Worden
*  @version 2.06
*/
public class MDLXOReader extends MDLBase implements XOReader
{

    /* root of the XML file.
    Set up in the constructor, or can be set up in setRoot() */
    private org.w3c.dom.Element theXMLFileRoot = null;

    // declared below for handling (class,subset)s that are not uniquely represented
    //private Hashtable nonUniqueClassSets = new Hashtable();
    
    /* When finding all objects of a class in an imported mapping set, the start node for 
     * XPaths is set to a node which is not the root of the full document. 
     * In this case, absolute XPaths must have their initial '/' removed to make them into
     * relative XPaths; this is done be setting treatAbsolutePathsAsRelative true */
    private boolean treatAbsolutePathsAsRelative = false;
    
	/* to write out a summary of the node indexing process  */
    private boolean traceNodeIndexing = false;
    
    // general tracing of anything
    private boolean tracing = false;

   

  //--------------------------------------------------------------------------------------
  //                                Constructor
  //--------------------------------------------------------------------------------------

    
    /**
     * constructor for use within Eclipse, where it is expected that the MappedStructure
     * enables you to find the class model (in the standard project file structure)
     */
    public MDLXOReader(org.w3c.dom.Element XMLFileRoot, MappedStructure ms, messageChannel mChan)  throws MapperException
    {
    	super(ms,mChan);
    	if (XMLFileRoot != null) setRoot(XMLFileRoot);
    }
    
    /**
     * constructor for use outside Eclipse, where it is not expected that the MappedStructure
     * enables you to find the class model, so the class model is supplied separately
     */
    public MDLXOReader(org.w3c.dom.Element XMLFileRoot, MappedStructure ms, EPackage classModel, messageChannel mChan)  throws MapperException
    {
    	super(ms,classModel,mChan);
    	if (XMLFileRoot != null) setRoot(XMLFileRoot);
    }


    /**
     * reset the DOM this XOReader is reading from, by resetting the root node.
     * Unset any pre-prepared information which may have been set up for the previous DOM
     */
    public void setRoot(Node rootNode) throws MapperException
    {
    	timer.start(Timer.SET_XML_ROOT);
        theXMLFileRoot = (Element)rootNode;
        nonUniqueClassSets = new Hashtable<String, Hashtable<String, Vector<objectRep>>>(); // have not yet grouped non-unique representations of objects
        makeNodeIndex();
        
        
        /* When finding all objects of a class in an imported mapping set, the start node for 
         * XPaths is set to a node which is not the root of the full document. 
         * In this case, absolute XPaths must have their initial '/' removed to make them into
         * relative XPaths; this is done be setting treatAbsolutePathsAsRelative  = true */
        if (rootNode != null)
        {
            Element trueRoot = rootNode.getOwnerDocument().getDocumentElement();  
            if (trueRoot == rootNode) treatAbsolutePathsAsRelative = false;
            else 
            {
            	treatAbsolutePathsAsRelative = true;
            }        	
        }
    	timer.stop(Timer.SET_XML_ROOT);
    }


    //--------------------------------------------------------------------------------------
    //                 Support for imported mapping sets and their XOReaders
    //--------------------------------------------------------------------------------------
    
    /* Pool of XOReader objects, shared between all MDLXOReaders imported directly or indirectly
     * by some root MDLXOReader. */
    private Hashtable<String,XOReader> readerPool = new Hashtable<String,XOReader>();
    
    /**
     * Inform this MDLBase of the pool of MDLBases already defined
     * @param readerPool a pool of XOReader objects, shared between all MDLXOReaders imported 
     * directly or indirectly by some root MDLXOReader.
     * key = string form of URI of mapping set, followed by the name of the root element
     */
    public void setReaderPool(Hashtable<String,XOReader> readerPool)
    	{this.readerPool = readerPool;}
    
    /**
     * 
     * @return String identifying an XOReader, apart from the importing element name
     */
    public String readerIdentifier()
    	{return ms().eResource().getURI().toString();}
     
     
     /**
      * @param ims an importMappingSet node of the mappedStructure
      * @return the MDLBase for that node; get it from the pool of MDLBases
      * if you can, otherwise make it and add it to the pool
      * @throws MapperException
      * 
      * The root element of an imported XOReader is not important, because 
      * when getting properties or traversing associations, 
      * it will be passed the start Element in the objectRep; 
      * so the same root element as for the top mapping set is  used
      */
     public XOReader getImportedReader(ImportMappingSet ims) throws MapperException
     {
 		MappedStructure impMS = ims.getImportedMappingSet();
 		if (impMS == null) throw new MapperException("Cannot find imported mapping set at: " + ims.getMappingSetURI());
 		String URIString = impMS.eResource().getURI().toString();
 		String elName = ims.getImportingElement().getName();
 		String key = URIString + "_" + elName; // reader identifier plus importing element name
 		XOReader reader = readerPool.get(key);
 		
 		if (reader == null)
 		{
 			
 			/* set the name of the root element of the imported mapping set to be the name
 			 * of the importing element, before making the reader */
 			impMS.getRootElement().setName(elName);
 			reader = impMS.getXOReader(theXMLFileRoot, ms().getClassModelRoot(), mChan());
 			
 			// make the new reader share in the pool of readers, and the pool of MDLBases
			if (reader instanceof MDLXOReader)
			{
				MDLXOReader mReader = (MDLXOReader)reader;
				mReader.setReaderPool(readerPool);
				mReader.setMDLBasePool(mdlBasePool);
	 			mdlBasePool.put(key, mReader);
			}
 			
 			// record the new reader in the pool
 			readerPool.put(key, reader);			
 			
 		}
 		
 		// pass down the debug postbox if you can
 		if ((reader != null) && (reader instanceof MDLXOReader))
 		{
 			((MDLXOReader)reader).setDebugPostbox(debugPostBox);
 		}
 		
 		return reader;   	
     }
    
     
     /** 
      * Find the XOReader (this one, or one it imports directly or indirectly)
      * which locally represents objects of this class (not subclasses) 
      * This method recurses down through all imported readers, which might be expensive.
      * If an object is represented on tw omapping sets (importing and imported)
      * this method returns only the importing mapping set - not always what you want.
      * */
     public XOReader readerRepresentingObject(String qualifiedClassName)
     {
     	Hashtable<String,String> readersUsed = new Hashtable<String,String>();
     	return readerRepresentingObject(qualifiedClassName, readersUsed);
     }
     

     /** 
      * True if the XML directly represents objects of this class (not subclasses) 
      * This method recurses down through all imported readers, which might be expensive.
      * @param readersUsed Hashtable of readers already tested, to avoid infinite recursion
      * */
     public XOReader readerRepresentingObject(String qualifiedClassName, Hashtable<String,String> readersUsed)
     {
     	// record that this mapping set has been used, to avoid infinite recursion
     	readersUsed.put(readerIdentifier(), "1");
     	
     	if (representsObjectLocally(qualifiedClassName)) return this;

     	// if this mapping set does not represent the object, check out all imported mapping sets
     	else
     	{
         	for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(ms(), MapperPackage.Literals.IMPORT_MAPPING_SET).iterator();it.hasNext();) try
         	{
         		ImportMappingSet ims = (ImportMappingSet)it.next();
         		XOReader xReader = getImportedReader(ims); //cached
         		if (xReader instanceof MDLXOReader)
         		{
         			MDLXOReader reader = (MDLXOReader)xReader;
             		// recursive step, avoiding visiting the same reader twice
             		if (readersUsed.get(reader.readerIdentifier()) == null)
             		{
             			XOReader repReader = reader.readerRepresentingObject(qualifiedClassName, readersUsed);
             			if (repReader != null) return repReader;
             		}
         		}
         	}
         	catch (MapperException ex) {GenUtil.surprise(ex, "MDLBase.representsObject");}
     	}
     	
     	// if none of the imported mapping sets represented the object
     	return null;
     }

    //--------------------------------------------------------------------------------------
    //                                Retrieval APIs
    //--------------------------------------------------------------------------------------

    
    /**
     * Vector  of objectReps for all nodes representing objects
    *  in any subclasses of a given class, in all subsets of those subclasses, 
    *  represented in this mapping set only - does not search any imported mapping sets
    *
    *  @param className  - the name of the class
    *  @exception MDLReadException  - class not represented in the XML
    *                - you ignored some exception on creating XOReader
    */
    public Vector<objectToken> getAllLocalObjectTokens(String className) throws MapperException
    {
    	timer.start(Timer.GET_OBJECTS);
        Vector<objectToken> v = new Vector<objectToken>();
        if (theXMLFileRoot == null)
          {throw new MDLReadException(
              "No XML File opened when asking XML Reader to retrieve all objects of class '"
              + className + "'.");}
        v = getObjectReps(className);
    	timer.stop(Timer.GET_OBJECTS);
        return v;
    }

    
    /**
     * Vector  of objectReps for all nodes representing objects
    *  in any subclasses of a given class, in all subsets of those subclasses.
    *
    *  @param className  - the name of the class
    *  @exception MDLReadException  - class not represented in the XML
    *                - you ignored some exception on creating XOReader
    */
    public Vector<objectToken> getAllObjectTokens(String className) throws MapperException
    {
    	timer.start(Timer.GET_OBJECTS);
        Vector<objectToken> v = new Vector<objectToken>();
        if (theXMLFileRoot == null)
          {throw new MDLReadException(
              "No XML File opened when asking XML Reader to retrieve all objects of class '"
              + className + "'.");}
        if (!checkIsRepresented(className))
        	{throw new notRepresentedException("Class '" + className + "' is not represented in the XML.");}
        /*
        15/5/03 I have removed the following exception, as it seems too fussy. Better to
        return objectReps with possible duplicated objects than to return nothing at all.
        if (!uniqueOrHasUId(className))
            {throw new notRepresentedException(
                "Some objects of class '" + className + "' are not uniquely represented "
                    + "and have no unique identifier; therefore we do not know how many are represented.");}
        */
        v = getObjectReps(className);
        Vector<objectToken> vToken = v;
        addImportedObjectReps(vToken, className);
    	timer.stop(Timer.GET_OBJECTS);
        return vToken;
    }
    
    /**
     * Extend the Vector v of objectReps for a class and its subclasses, 
     * by finding all such objectReps in imported mapping sets, 
     * below all node instances where they are imported
     * @param v the Vector of objectReps to be extended
     * @param className the class for which objects are being retrieved
     */
    private void addImportedObjectReps(Vector<objectToken> v, String className) throws MapperException
    {
    	// find all XPaths of Elements which import mapping sets, and cache XOReaders for the ClassSets
    	for (Iterator<EObject> it = ModelUtil.getEObjectsUnder(ms(), MapperPackage.Literals.IMPORT_MAPPING_SET).iterator();it.hasNext();)
    	{
    		ImportMappingSet ims = (ImportMappingSet)it.next();
    		String elPath = ((ElementDef)ims.eContainer()).getPath();
    		// each imported mapping set should have exactly one parameter class value
    		if ((ims.getParameterClassValues() != null) && (ims.getParameterClassValues().size() == 1))
    		{
    			// qualified class name of the parameter class
    			String paramClass = ims.getParameterClassValues().get(0).getQualifiedClassName();
    			XOReader reader = getImportedReader(ims);
    			if (reader != null)
    			{
    				// only do anything if the imported mappings represent the class or a subclass
    	   			// do not look for objects of the parameter class represented in the imported mapping set
    				if ((reader.checkIsRepresented(className)) && 
    						(!(paramClass.equals(className)))) try
    				{
    					// follow the XPath to find all node instances 
    		        	Vector<Node> importRoots = XPathAPI.selectNodeVector(timer,theXMLFileRoot,elPath,NSSet());
    		        	for (Iterator<Node> ix = importRoots.iterator();ix.hasNext();)
    		        	{
    		        		// set the root node of the imported reader to be the current node, and find all objects below it
    		        		reader.setRoot(ix.next());
    		        		Vector<objectToken> impObjects = reader.getAllObjectTokens(className);
    		        		//extend the global list of objects
    		        		for (Iterator<objectToken> iw = impObjects.iterator();iw.hasNext();) v.add(iw.next());
    		        	}
    				}
    		        catch (Exception e)
    	            {throw new MDLReadException("XPath exception finding root node of imported mapping set at path "
    	                        + elPath + ": " + e.toString());}
    			}
    			else throw new MapperException("Cannot find imported mapping set at " + ims.getMappingSetURI());
    		}
    		else 
    		{ 
    			int nParams = 0;
    			if (ims.getParameterClassValues() != null) 
    				nParams = ims.getParameterClassValues().size();
    			throw new MapperException("Importing mapping set has " + nParams + " parameter class values");
    		}
    	}
    	
    }
    
    /** false if a class or its subclasses are not represented */
    public boolean checkIsRepresented(String className) 
    {
    	timer.start(Timer.GET_METADATA);
    	boolean isRep = false;
    	for (Iterator<String> it = inheritors(className).iterator();it.hasNext();)
    		if (representsObject(it.next())) isRep = true;
    	timer.stop(Timer.GET_METADATA);
    	return isRep;
    }

    private Vector<objectToken> getObjectReps(String className) throws MapperException
    {
        int i;
        String scName;
        Vector<objectToken> nl,np;
        nl = new Vector<objectToken>();
        // iterate over classes that inherit from this class
        for (Iterator<String> it = inheritors(className).iterator();it.hasNext();)
        {
            scName = it.next();
            Vector<objectMapping> mappings = objectMappings(scName);
            // iterate over subsets (different mappings for this subclass)
            if (mappings.size() > 0) for (i = 0; i < mappings.size();i++)
            {
                objectMapping om = (objectMapping)mappings.elementAt(i);
                ClassSet cs = om.cSet();
                // uniquely represented ClassSet - follow XPaths to find all objectReps
                if (om.isUnique())
                    {np = theObjectReps(om);}
                /* non-uniquely represented ClassSet with a unique identifier - return
                only one objectRep per value of the unique identifier fields*/
                else if (hasUniqueIdentifier(cs))
                    {np = getDistinctObjectReps(cs);}
                /* non-uniquely represented ClassSet with no unique identifier;
                return all objectReps with possible duplicates. */
                else {np = theObjectReps(om);}
                for (Iterator<objectToken> ix = np.iterator();ix.hasNext();) {nl.add(ix.next());}
            }
        }
        return nl;
    }
    

    // find all objectReps for a specific object mapping
    private Vector<objectToken> theObjectReps(objectMapping om) throws MapperException
    {
        Vector<Node> fNodes;
        Node oNode;
        Vector<objectToken> nl = new Vector<objectToken>();
        String s = om.nodePath().stringForm();
        /* if this is an imported mapping set, so paths start from a node which is not
         * the true root of the document, absolute paths must be converted to relative paths 
         * and their outermost step removed. */
        if (treatAbsolutePathsAsRelative) 
        {
        	// one-step paths with the name of the start node are reduced to 'stay here' paths
        	if (om.nodePath().size() == 1) s = ".";
        	else s = om.nodePath().removeOuterStep().stringForm();
        }
        if (theXMLFileRoot == null) {throw new MDLReadException("No XML File root");}
        try
        {
            breakForMapping(om,theXMLFileRoot,DebugRow.TO_OBJECT,false);
        	Vector<Node> unfiltered = XPathAPI.selectNodeVector(timer,theXMLFileRoot,s,NSSet());
        	resultForMappingBreak(om,unfiltered.size());
            // filter for conditional representations
            fNodes = filterNodeVector(om,VALUE_CONDITIONS,unfiltered,theXMLFileRoot,false);
            // mChan().message("found " + unfiltered.size() + " nodes for object mapping " + om.className() + " down to " + fNodes.size());
            // add objectReps for filtered nodes
            for (int k = 0; k < fNodes.size(); k++)
            {
                oNode = fNodes.elementAt(k);
                nl.addElement(new objectRep(oNode, om.className(),om.subset(),this));
            }
        }
        catch (Exception e)
            {throw new MDLReadException("XPath exception getting objects in subclass "
                        + om.cSet().stringForm() + ": " + e.toString());}
        breakToInform(om,DebugRow.RETURN_OBJECTS);
        resultForInformBreak(om,nl.size(),OBJECT_COUNT);
        stopRunOn();
        return nl;
    }
    
    //---------------------------------------------------------------------------------------------
    //                            Property Mapping Retrievals
    //---------------------------------------------------------------------------------------------


    /**
     * String value of a property of some represented object
    *
    *  @param oRep  - the objectRep for the object
    *  @param propertyName  - the name of the property
    *
    *  @exception MDLReadException   - XML does not represent the property for this class
    *               - XML does not represent the property for this instance
    *               - XML represents multiple values for the property
    *               - Java class or method for a property format conversion cannot be found
    *               - An input value for a property format conversion is not represented
    */
    public String getPropertyValue(objectToken oTok, String propertyName) throws MapperException
    {
    	timer.start(Timer.GET_PROPERTIES);
    	objectRep oRep = ModelUtil.toRep(oTok);
    	// preliminary check of absurdities
        if (oRep == null)
            {throw new MDLReadException("Null objectRep when retrieving property value");}
        if (propertyName == null)
            {throw new MDLReadException("Null property name when retrieving property value ");}

        // if this objectRep was found by a delegated XOReader, use that XOReader to find the property value
        if (!(oRep.reader().equals(this)))
        {
        	oRep.reader().setRoot(theXMLFileRoot);
        	return oRep.reader().getPropertyValue(oRep, propertyName);
        }

        /* if there is no property mapping or conversion in this mapping set, try out any 
         * imported mapping set before throwing a notRepresentedException; or
         * the imported mapping set may throw one.  */
        if (!checkIsRepresentedLocally(oRep,propertyName)) 
        {
        	/* FIXME - this assumes the delegated reader root node is the object mapping node, 
        	 * not a child in its unique subtree - which it might be. In 
        	 * principle one should search descendant nodes, restricting imports
        	 * to have the same parameter class as the property */
        	String elPath = namedObjectMapping(oRep.cSet()).nodePath().stringForm();
        	NodeDef el = ms().getNodeDefByPath(elPath);
        	if ((el instanceof ElementDef) && (((ElementDef)el).getImportMappingSet()!= null))
        	{
        		ImportMappingSet ims = ((ElementDef)el).getImportMappingSet();
            	XOReader delegateReader = getImportedReader(ims);
        		// make an objectRep for the parameter class and subset ""
        		String supName = delegateReader.parameterClassName();
        		// FIXME - I think the node should be the root node of the delegated mappings
        		objectRep dRep = new objectRep(oRep.objNode(),supName,"",delegateReader);
        		/* note this will not recurse indefinitely, because an imported mapping set 
        		 * will not import another on its root node. After the FIXME above, this will no longer 
        		 * true, but the restriction on the parameter class will in practice cut off recursion */
        		return delegateReader.getPropertyValue(dRep, propertyName);
        	}
        	else throw new notRepresentedException("Found no mapping or property conversion for property " + oRep.cSet().stringForm()
                + ":" + propertyName);
        }

        // all delegated XOReaders have the same root Element, so this should not fail on delegation
        org.w3c.dom.Element currentRoot = oRep.objNode().getOwnerDocument().getDocumentElement();
        if (!(currentRoot == theXMLFileRoot))
          {
        	// System.out.println("XML Root issue with property " + propertyName);
        	/* throw new MDLReadException("Retrieving property '"
                + propertyName + "' from a different XML DOM without calling setRoot"); */
          }

        String result = getPropValue(oRep,propertyName);
    	timer.stop(Timer.GET_PROPERTIES);
        return result;
    }

    /*  determines whether a property is represented in this mapping set (not in any nested mapping sets).*/
    private boolean checkIsRepresentedLocally(objectRep oRep,String propertyName) 
    {
    	boolean isRep = false;
        Vector<propertyMapping> pms  = namedPropertyMappings(oRep.className(),oRep.subset(),propertyName);
        int mappings = pms.size();
        if (mappings > 0)  {isRep = true;}
        // there is no mapping, but a property format conversion
        else if (getInConversion(oRep.cSet(),propertyName) != null)  {isRep = true;}
        return isRep;
    }

    /* I think if the XML represents the property in principle, but not for this instance,
    this method returns null. */
    private String getPropValue(objectRep oRep, String propertyName) throws MapperException
    {
        Vector<Node> unfiltered,filtered;
        Node propertyNode;
        String res = null;
        Vector<propertyMapping> pms  = namedPropertyMappings(oRep.className(),oRep.subset(),propertyName);
        int mappings = pms.size();
        if (mappings > 0)
        {
        	propertyMapping pm = pms.elementAt(0);
          if (pm.fixed()) // if the property has a fixed value for this representation of the object
          {
              breakForMapping(pm,oRep.objNode(),DebugRow.OBJECT_TO_PROPERTY,false);
              res = pm.value(); // just return that value
              resultForReturnValueBreak(pm,res);
              stopRunOn();
          }
          else // no fixed value; find it from the XML.
          {
            /* if there are several redundant property mappings, arbitrarily choose the first,
            as they should all give the same value. */
            if ((mappings > 1) && (pm.multiWay().equals("redundant"))) {mappings = 1;}
            /* otherwise for multiWay = 'choice', try every mapping,looking for any non-empty value. */
            res = "";
            for (int i = 0; i < mappings; i++) if (res.equals(""))
            {
              pm = (propertyMapping)pms.elementAt(i);
              try
              {
            	  /* use the Node retrieval which may use an index on one of the link conditions, 
            	   * if there are any. */
              	String pathString = pm.objectToProperty().stringForm();
          	    boolean indexed = XPathAPI.isIndexed(pm.map(),true);
                breakForMapping(pm,oRep.objNode(),DebugRow.OBJECT_TO_PROPERTY,indexed);
                unfiltered = XPathAPI.indexedSelectNodeVector(timer,oRep.objNode(),
                		pathString,NSSet(),pm.map(),true,nodeIndex);
                resultForMappingBreak(pm,unfiltered.size());
                // LHS of link conditions is got from property nodes, not object node
                filtered = filterNodeVector(pm,ALL_CONDITIONS,unfiltered,oRep.objNode(),false);
                /* if filtered.size() == 0, res does not get reset, and may stay null.
                There is no exception thrown for filtered.size()) == 0  */
                if (filtered.size() > 0)
                {
                    propertyNode = filtered.elementAt(0);
                    breakForReturnValue(pm,propertyNode);
                    /* if the path leads to the virtual attribute for the element ordinal position,
                     * return the ordinal position of the element; otherwise return the text on the node,
                     * converted by a local property conversion if necessary */
                    if (pathString.endsWith(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE))
                    {
                        res = XMLUtil.ordinalPosition((Element)propertyNode);                    	
                    }
                    else 
                    {
                    	// get the un-converted property value from the XML
                    	String unconverted = XMLUtil.textValue(propertyNode);

                    	// if there is a local Java property in-conversion, apply it to the un-converted value
                    	propertyConversion localConversion = pm.localInConversion();
                    	if ((localConversion != null) && (localConversion.canDoJavaConvert()))
                    	{
                            String[] args = new String[1];
                            args[0] = unconverted;
                            res = localConversion.doJavaConvert(args);
                    	}
                    	
                    	// if the property mapping has a lookup table, use it in the 'in' direction
                    	else if (pm.hasLookupTable()) res = pm.getModelValue(unconverted);
                    	
                    	// otherwise pass through the value from the XML to the model
                    	else res = unconverted;
                    }
                    resultForReturnValueBreak(pm,res);
                    stopRunOn();
                }
                // if the path leads to zero nodes, apply any default value
                else if ((filtered.size() == 0) && (pm.hasDefault()))
                  {res = pm.defaultValue();}
                if (filtered.size() > 1)
                    {throw new MDLReadException("Property " + oRep.cSet().stringForm() + ":" + propertyName
                        + " has " + filtered.size() + " values");}
                }
                catch (javax.xml.xpath.XPathExpressionException e)
                    {throw new MDLReadException("XPath exception getting property "
                    + oRep.className() + ":" + propertyName + " " + e.toString());}
            } // end of loop over choice mappings
          } // end of pm.fixed = false
        } // end of mappings > 0

        // there is no mapping, but a property format conversion
        else if (getInConversion(oRep.cSet(),propertyName) != null)
        {
            propertyConversion pc = getInConversion(oRep.cSet(),propertyName);
            if ((pc.hasImplementation("Java")) && (pc.canDoJavaConvert()))
            {
                Vector<String> argVect = pc.arguments();
                String[] args = new String[argVect.size()];
                boolean allArgsFound = true;
                for (int k = 0; k < argVect.size(); k++)
                {
                    String pseudoProp = (String)argVect.elementAt(k);
                    args[k] = getPropValue(oRep,pseudoProp);
                    if (args[k] == null) allArgsFound = false;
                }
                if (allArgsFound) {res = pc.doJavaConvert(args);}
                else if (!allArgsFound)
                    {throw new PropertyConversionException("Property in-conversion for property " + oRep.cSet().stringForm()
                        + ":" + propertyName + " failed for lack of some argument.");}
            }
            else
                {throw new PropertyConversionException("Property in-conversion for property " + oRep.cSet().stringForm()
                 + ":" + propertyName + " has no accessible Java implementation.");}
        }
        // no mapping or property format conversion
        else {throw new notRepresentedException("Found no mapping or property conversion for property " + oRep.cSet().stringForm()
                 + ":" + propertyName);}
        return res;
    }
    
    //---------------------------------------------------------------------------------------------
    //                            Association Mapping Retrievals
    //---------------------------------------------------------------------------------------------

    
    /**
      * Vector of objectReps representing objects related to the current object by some association.
      *
      * @param oRep - the input object at one end of the association
      * @param assocName - name of the association
      * @param otherClass - class or superclass of the objects to be retrieved
      * @param otherRole - the role played by the other-end object in the association
      *
      * @exception MDLReadException - any argument null
      *                     - the XML does not represent the association between the classes
      */
    public Vector<objectToken> getAssociatedObjectTokens(objectToken oTok,
        String otherClass, String otherRole) throws MapperException
    {
    	String assocName = ModelUtil.getAssociationName(oTok.className(), otherRole, otherClass,ms());
        return getTheAssociatedObjectReps(oTok,assocName, otherClass, -1, otherRole);
    }

    
    /**
      * Vector of objectReps representing objects related to the current object by some association.
      *
      * @param oRep - the input object at one end of the association
      * @param assocName - name of the association
      * @param otherClass - class or superclass of the objects to be retrieved
      * @param thisEnd - end of the association for the input object, = 1 or 2
      *
      * @exception MDLReadException - any argument null
      *                     - the XML does not represent the association between the classes
      */
    public Vector<objectToken> getAssociatedObjectTokens(objectToken oTok, String assocName,
        String otherClass, int thisEnd) throws MapperException
    {
        return getTheAssociatedObjectReps(oTok,assocName,otherClass, thisEnd, "");
    }

    
    /**
     * Main method for following associations through mappings; the other two methods in the XOReader
     * interface can be got by simple calls to this method, as shown in MDLXOReader.
     * 
     * @param oTok object token for the start object
     * @param assocName association name, usually composed from the two end role names
     * @param otherClass qualified class name at the target end of the association
     * @param thisEnd end of the start object, if the role name is not specified; or -1 if it is
     * @param otherRole role name leading to the target end; or "" if not specified
     * @return object tokens for objects reached by the association
     * @throws MapperException
     */
    public Vector<objectToken> getTheAssociatedObjectReps(objectToken oTok, String assocName,
        String otherClass, int thisEnd, String otherRole) throws MapperException
    {
    	timer.start(Timer.GET_ASSOCIATIONS);
    	
    	timer.start(Timer.ASSOCIATION_PRELIMINARIES);
    	objectRep oRep = ModelUtil.toRep(oTok);
    	
    	// general tracing
    	trace("Following association from object of class " + oTok.className() 
    			+ " by assocation " + assocName + " to class " + otherClass
    			+ ";  this end  is " + thisEnd + "; otherRole " + otherRole);
    	
        // do some preliminary checks and throw exceptions if they fail
        doAssociationChecks(oRep,assocName,otherClass,otherRole,thisEnd);
        
        // check that this is the XOReader that found the start object; if not, delegate
        if (!(oTok.reader().equals(this)))
        {
        	oTok.reader().setRoot(theXMLFileRoot);
        	return oTok.reader().getTheAssociatedObjectReps(oTok,assocName,
        	        otherClass, thisEnd, otherRole);
        }

        // initialise
        Vector<objectRep> res = new Vector<objectRep>();
        Hashtable<String, Hashtable<Node, objectRep>> oReps 
        	= new Hashtable<String, Hashtable<Node, objectRep>>();
        int allMappings = 0;
    	timer.stop(Timer.ASSOCIATION_PRELIMINARIES);

        // iterate over all inheriting classes of the other object class
        EClass otherEClass = ModelUtil.getNamedClass(classModel(), otherClass);
        if (otherEClass == null) throw new MapperException("Cannot find other end class '" 
        		+ otherClass + "' through association '" + assocName + "'");
        for (Iterator<EClass> it = ms().getAllSubClasses(otherEClass).iterator();it.hasNext();)
        {
                String scName = ModelUtil.getQualifiedClassName(it.next());
                Vector<objectMapping> mappings = objectMappings(scName);
                // iterate over subsets (different mappings for this subclass)
                for (int i = 0; i < mappings.size();i++)
                {
                	objectMapping om = mappings.elementAt(i);
                    Vector<AssociationMapping> ams = new Vector<AssociationMapping>();
                    if (thisEnd == 1)
                        {ams = namedAssociationMappings(oRep.cSet(), assocName, om.cSet());}
                    else if (thisEnd == 2)
                        {ams = namedAssociationMappings(om.cSet(), assocName, oRep.cSet());}
                    else if (thisEnd == -1) // role is defined
                        {ams = namedAssociationMappings(oRep.cSet(),om.cSet(), assocName, otherRole);}
                    int maps = ams.size();
                    allMappings = allMappings + maps;
                    if (maps > 0)
                    {
                    	AssociationMapping am = ams.elementAt(0);
                        /* If there are several redundant association mappings, arbitrarily use only
                        the first, as they should all give the same result.  */
                        if (am.multiWay().equals("redundant")) {maps = 1;}
                        // otherwise, accumulate other-end objects from all the alternative mappings
                        for (int j =0; j < maps; j++)
                        {
                            am = (AssociationMapping)ams.elementAt(j);
                            int end = thisEnd;
                            /* if the association name has been defined by a role name, find the
                            end which does not have that role - changing (1,2) to (2,1)*/
                            if (end == -1) {end = 3 - am.endForRole(otherRole);}
                            Vector<objectRep> resAdd = getAssociatedObjectRepVector(oRep,am,new ClassSet(scName,om.subset()),end);
                            addOReps(oReps,resAdd);
                        }
                    } /* Possibly there is no association mapping for some subclass and subset.    */
                } // end of iteration over subsets of a class
        } // end of enumeration over cr.allInheritors()

        // transfer from the Hashtable of Hashtables of objectReps to result vector of objectReps
        for (Enumeration<Hashtable<Node, objectRep>> ec = oReps.elements(); ec.hasMoreElements();)
        {
            Hashtable<Node, objectRep> innerHash = ec.nextElement();
            for (Enumeration<objectRep> en = innerHash.elements(); en.hasMoreElements();)
            {
                objectRep or = en.nextElement();
                res.addElement(or);
            }
        }

        /* if there are no association mappings in this mapping set, try out any 
         * imported mapping set before throwing a notRepresentedException; or
         * the imported mapping set may throw one.  */
        if (allMappings ==0) 
        {
        	String elPath = namedObjectMapping(oRep.cSet()).nodePath().stringForm();
        	/* FIXME the node that imports a mapping set might not the node that represents
        	 * the object; I may in future want to allow it to be  a descendant. In this 
        	 * case the possible imported mapping sets should be restricted to have parameter 
        	 * class equal to the objectRep */
        	NodeDef el = ms().getNodeDefByPath(elPath);
        	if ((el instanceof ElementDef) && (((ElementDef)el).getImportMappingSet()!= null))
        	{
        		ImportMappingSet ims = ((ElementDef)el).getImportMappingSet();
        		XOReader delegateReader = getImportedReader(ims);
        		// make an objectRep for the parameter class and subset ""
        		String supName = delegateReader.parameterClassName();
        		objectRep dRep = new objectRep(oRep.objNode(),supName,"",delegateReader);
        		/* Note this currently cannot recurse indefinitely, because the imported
        		 * mapping set will not import another on its root node. After the FIXME  above,
        		 * there can be some recursion, but in practice severely cut off by the 
        		 * parameter class matching */
            	timer.stop(Timer.GET_ASSOCIATIONS);
        		return delegateReader.getTheAssociatedObjectReps(dRep,assocName,
        		        otherClass, thisEnd, otherRole);
        	}
        	else throw new notRepresentedException("The XML does not represent association '"
                    + assocName + "' between classes " +  oRep.className() + " and " + otherClass + ".");
        }
    	timer.stop(Timer.GET_ASSOCIATIONS);
        return ModelUtil.toTokens(res);
    }
    
    // some preliminary checks before trying to traverse an association
    private void  doAssociationChecks(objectRep oRep,String assocName,
    		String otherClass,String otherRole, int thisEnd)
    throws MDLReadException
    {
        if (oRep == null) {throw new MDLReadException("Null object node when retrieving association");}
        if (assocName == null) {throw new MDLReadException("Null association name ");}
        if (otherClass == null) {throw new MDLReadException("Null other class name when retrieving association");}
        /*
        org.w3c.dom.Element currentRoot = oRep.objNode().getOwnerDocument().getDocumentElement();
        if (!(currentRoot == theXMLFileRoot))
            {throw new MDLReadException("Retrieving association '"
                + assocName + "' from a different XML DOM without calling setRoot");}
        */
        /* can only navigate an association from this object if it is
        uniquely represented or has a unique identifier. */
        /*
        21/05/03 I eliminated this scruple; it is often awkward in the query facility (which
        eliminates duplicate rows from query answers anyway); and for general XML Reader use, I think
        it is better to return an answer with potential duplicates than to return none at all.
        objectMapping omc = namedObjectMapping(cs);
        ClassSet cs = oRep.cSet();
        if ((!omc.isUnique()) && (!hasUniqueIdentifier(cs)))
            {throw new notRepresentedException(
                "Some objects of class " + cs.stringForm() + " are not uniquely represented "
                    + "and have no unique identifier; therefore we cannot reliably get objects associated to them.");}
        */
        if (thisEnd == -1)
        {
            if ((otherRole == null)|((otherRole != null) && (otherRole.equals(""))))
                {throw new MDLReadException("Empty or null other role name when retrieving association");}
        }
        else if (!((thisEnd == 1)|(thisEnd == 2)))
            {throw new MDLReadException("Association end " + thisEnd + " should be 1 or 2 when retrieving association");}
        
        // I have removed this check because it is very expensive when there are many large mapping sets; it might load all of them
        /* if (!checkIsRepresented(otherClass))    	
    		{throw new notRepresentedException("Class '" + otherClass + "' is not represented in the XML.");}
    	*/
    }


    /* add objectReps from a vector r to a Hashtable of Hashtables.
    outer key = ClassSet, to store oReps of different ClassSets separately
    inner key = DOM node, to avoid duplicates (objects represented by the same node) within a ClassSet.
    (the same DOM node may represent two objects in different ClassSets)*/
    void addOReps(Hashtable<String, Hashtable<Node, objectRep>> oReps, Vector<objectRep> r)
    {
        if (r != null) for (int i = 0; i < r.size(); i++)
        {
            objectRep oRep = r.elementAt(i);
            String cSetKey = oRep.cSet().stringForm();
            Hashtable<Node, objectRep> innerHash = oReps.get(cSetKey);
            if (innerHash == null) innerHash = new Hashtable<Node, objectRep>();
            innerHash.put(oRep.objNode(), oRep);
            oReps.put(cSetKey,innerHash);
        }
    }

    
  /* get all associated objects for a given inheritor class and subset of the other end
  of the association.
  oRep = objectRep for this object, whose associated objects are being found
  otherEndCSet = class and subset of the other end class
  oneOrTwo - if 1, this object is at end 1 of the association.
  */
  private Vector<objectRep> getAssociatedObjectRepVector(objectRep oRep,AssociationMapping am, ClassSet otherEndCSet, int oneOrTwo)
    throws MapperException
{
	  trace("found association mapping");
	  
    ClassSet cs = oRep.cSet();
    objectMapping om = namedObjectMapping(cs);
    Vector<objectRep> res = new Vector<objectRep>();
    /* if the (class,subset) is uniquely represented, follow the XPaths
    from the node of the objectRep you are given.*/
    if (om.isUnique())
    {
        res = followAssocXPaths(oRep,am,otherEndCSet,oneOrTwo);
    }
    /* if the (class,subset) is not uniquely represented but has a unique identifier,
    find all objectReps for this object, and follow the XPaths from all of them.*/
    else if (hasUniqueIdentifier(cs))
    {
        /* this method needs to know the root ,because it may need to get all
        the different representations of a non-uniquely represented object. */
        res = followAllAssocXPaths(oRep,am,otherEndCSet,oneOrTwo);
    }
    /* otherwise return the objects associated to this objectRep,
    which may be only one of those representing the object. This is imperfect
    but probably better than nothing - it often gives the answer you want */
    else
    {
        res = followAssocXPaths(oRep,am,otherEndCSet,oneOrTwo);
    }
    return res;
}

  /* Follow XPaths in the XML document from this object to the association node,
 then from the association node to the other end object, applying link conditions
 where needed, to get all associated objects of a given class and subset.
 */
private Vector<objectRep> followAssocXPaths(objectRep oRep,AssociationMapping am,
    ClassSet otherEndCSet, int oneOrTwo)
    throws MDLReadException
    {
	    timer.start(Timer.ASSOCIATION_CORE);	
        int k,l;
        associationEndMapping thisEnd, otherEnd;
        Node assocNode;
        Vector<Node> a1; // unfiltered list of association nodes got from start object node
        Vector<Node> a3; // link and when-filtered list of association nodes got from start object node
        Vector<Node> b1; // unfiltered list of end object nodes got from one association node
        Vector<Node> b2; // filtered list of end object nodes got from one association node
        Vector<Node> b3; // list filtered by when-condition of target object mapping


        Vector<objectRep> res = new Vector<objectRep>();
        
        try
        {
    	    timer.start(Timer.OTHER_CORE);	
            thisEnd = am.assocEnd(oneOrTwo - 1); // = 0,1 for oneOrTwo = 1,2
            otherEnd = am.assocEnd(2 - oneOrTwo); // = 1,0 for oneOrTwo = 1,2
            String firstPath = thisEnd.objToAssoc().stringForm();
    		trace("Following first path " + firstPath);
    	    timer.stop(Timer.OTHER_CORE);	

    	    timer.start(Timer.FIRST_END);
    	    boolean indexed = XPathAPI.isIndexed(thisEnd.aem(),true);
    	    breakForMapping(thisEnd,oRep.objNode(),DebugRow.OBJECT_TO_ASSOCIATION,indexed);
            /* follow XPath from start object node to get a list of association nodes, 
             * possibly using an index on one of the link conditions, if there are any. 
             * 'true' means the path goes to the LHS node of link conditions. */
            a1 = XPathAPI.indexedSelectNodeVector(timer,oRep.objNode(),
            		firstPath,NSSet(),thisEnd.aem(),true,nodeIndex);
            trace("found " + a1.size() + " nodes");
            resultForMappingBreak(thisEnd,a1.size());
            /* apply any 'when' and link conditions on the association node,
            to get a fully filtered list of association nodes.
            LHS of  link conditions comes from association nodes (last arg = false) */
            a3 = filterNodeVector(thisEnd,ALL_CONDITIONS,a1,oRep.objNode(),false);
            trace("filtered to " + a3.size() + " nodes");
    	    timer.stop(Timer.FIRST_END);	

    	    /* for each filtered association node, get a list of unfiltered
            object nodes, filter it, and add the result to the main list. */
    	    timer.start(Timer.SECOND_END);	
    	    if (a3.size() > 0) // if no association nodes are found, debug trace goes straight to final result
    	    {
                breakToInform(am,DebugRow.FOUND_ASSOCIATION_NODES);
                resultForInformBreak(am,a3.size(),NODE_COUNT);    	    	
    	    }
            for (k = 0; k < a3.size(); k++)
            {
                 assocNode = a3.elementAt(k);
                 // follow XPath from association node to get an unfiltered list of other end object nodes
                 b1 = followPathToObjNodes(assocNode,otherEnd);
                 /* filter end object nodes with link conditions on the second link; 
                 LHS of link conditions comes from association nodes (last arg = true) */
                 b2 = filterNodeVector(otherEnd,CROSS_CONDITIONS,b1,assocNode,true);
                 // filter the end object nodes with when-conditions for the object mappings of the target class
                 objectMapping endMap = namedObjectMapping(otherEndCSet);
                 // if the association mapping is a break point, temporarily set the object mapping to have one
                 boolean objectMappingWasBreakPoint = endMap.map().isBreakPoint();
                 endMap.map().setBreakPoint(am.map().isBreakPoint());
                 b3 = filterNodeVector(endMap,VALUE_CONDITIONS,b2,assocNode,false);
                 trace("following and filtering: " + b1.size() + "; " + b2.size() + "; " + b3.size());
                 endMap.map().setBreakPoint(objectMappingWasBreakPoint);
                 
                 // add all filtered nodes in objectReps to the result
                 for (l = 0; l < b3.size(); l++)
                 {
                    String scName = otherEndCSet.className();
                    String subset = otherEndCSet.subset();
                    res.addElement(new objectRep(b3.elementAt(l),scName,subset,this));
                 }
            }
    	    timer.stop(Timer.SECOND_END);	
        }
        catch (Exception e)
            {
        		e.printStackTrace();        	
        		throw new MDLReadException("Exception following XPaths for association "
                + am.fullName());
        	}
	    timer.stop(Timer.ASSOCIATION_CORE);	
        breakToInform(am,DebugRow.RETURN_OBJECTS);
        resultForInformBreak(am,res.size(),OBJECT_COUNT);
        stopRunOn();
        return res;
    }

	/* Follow path from an association node to nodes representing objects
    at the other end of the association - or get the same result using an association index.
    If the association end mapping has link conditions, and if these conditions have been
    used to construct an association index, use this index to return a short list
    of candidate nodes.
    Otherwise, just follow the XPath.  */
    private Vector<Node> followPathToObjNodes(Node assocNode,associationEndMapping otherEnd)
    throws MapperException
    {
        Vector<Node> b1 = new Vector<Node>();
        try
        {
    	    boolean indexed = XPathAPI.isIndexed(otherEnd.aem(),false);
            breakForMapping(otherEnd,assocNode,DebugRow.ASSOCIATION_TO_OBJECT,indexed);
        	// 'false' means the path is a path to the LHS of any link conditions
          b1 = XPathAPI.indexedSelectNodeVector(timer,assocNode,
        		  otherEnd.assocToObj().stringForm(),NSSet(),otherEnd.aem(),false,nodeIndex);
          resultForMappingBreak(otherEnd,b1.size());
        }
        catch (Exception e)
            {
        		throw new MDLReadException("Exception following XPath '"
                + otherEnd.assocToObj().stringForm() + "': " + e.getMessage());
        	}
       return b1;
   }


   /* true if Node 'end' is one of the nodes you reach
   from Node 'start' by path crossPath.
   Assumes crossPath has been validated by the MDL validator */
   boolean linkedByCrossPath(Node start, Node end, Xpth crossPath) throws MapperException
   {
      Xpth ascent;
      String pathString;
      Node apexNode;
      boolean res = true;
      /* If the crossPath is a validated path from the root node,
      then it must be able to reach Node 'end' regardless of 'start'.*/
      if (crossPath.fromRoot()) {res = true;}
      else if (crossPath.selfPath()) {res = (start == end);}
      else
      {
          //mChan().message("Real test of method linkedByCrossPath, with path '" + crossPath.stringForm() + "'");
          int apex = crossPath.apexIndex();
          if (apex == -1) // pure descending path
          {
              res = isAncestorOf(start,end);
          }
          else if (apex == crossPath.size() - 1) // pure ascending path
          {
              res = isAncestorOf(end,start);
          }
          else if ((apex > -1) && (apex < crossPath.size() - 1)) // path with a true apex
          {
              // from node 'start', find all possible apex nodes (there may be more than one)
              ascent = crossPath.truncateTo(apex);
              pathString = ascent.stringForm();
              try{
                  Vector<Node>  vn = XPathAPI.selectNodeVector(timer,start,pathString,NSSet());
                  for (int i = 0; i < vn.size(); i++) if (!res)
                  {
                      apexNode = vn.get(i);
                      if (isAncestorOf(apexNode,end)) res = true;
                  }
              }
              catch(Exception e)
                  { throw new MDLReadException("Xpath exception following path '" + pathString + "'; " + e.getMessage());}
          }
          else
              {throw new MDLReadException("Invalid crossPath detected in linkedByCrossPath: " + crossPath.stringForm());}
      }
      return res;
   }

   // true if node 'anc' is an ancestor node of node 'desc'
   boolean isAncestorOf(Node anc, Node desc)
   {
        boolean res = false;
        if (anc == desc) {res = true;}
        else
        {
            Node up = desc.getParentNode();
            if (up != null) res = isAncestorOf(anc,up);
        }
        return res;
   }

   //-------------------------------------------------------------------------------------
   //    Support for getting objects, properties and associations
   //-------------------------------------------------------------------------------------
   
   private static int ALL_CONDITIONS = 0;
   private static int VALUE_CONDITIONS = 1;
   private static int CROSS_CONDITIONS = 2;

   /* filter a NodeList to satisfy some conditions.
    The Nodelist is got from a single startNode by some XPath.
    if startIsLHS = true, the start node is used to find the value of the LHS of each condition,
    and the end nodes are used for the RHS of any link condition;
    if startIsLHS is false,  the end nodes of the path are used for the LHS of any condition,
     and the start node is used for the RHS of  any link condition.
    */
    protected Vector<Node> filterNodeVector(MappingTwo m, int conditionType, Vector<Node> a, Node startNode, boolean startIsLHS)
    throws MapperException
    {
    	timer.start(Timer.FILTER_NODE_VECTOR);

    	// set up the correct set of conditions to test
    	Vector<Condition> conditions = new Vector<Condition>();
    	if (conditionType == ALL_CONDITIONS)
    		conditions = m.allConditions();
    	else if (conditionType == CROSS_CONDITIONS)
        	for (Iterator<linkCondition> il = m.linkConditions().iterator();il.hasNext();)
        		conditions.add(il.next());
    	else if (conditionType == VALUE_CONDITIONS)
        	for (Iterator<whenCondition> iw = m.whenConditions().iterator();iw.hasNext();)
        		conditions.add(iw.next());
    	else throw new MapperException("Invalid condition type for filterNodeVector");

    	Vector<Node> res = null;
        if (conditions.size() == 0) {res = a;}
        else
        {
            res = new Vector<Node>();
            if (a != null) for (int i = 0; i < a.size(); i++)
            {
                Node n = a.elementAt(i);
                breakForNodeFound(n,i,a.size(),m,conditions.size());
                boolean passed = true;
                // node must pass all conditions to get on the filtered output
                for (int j = 0; j < conditions.size(); j++) if (passed)
                {
                	Condition cond = (Condition)conditions.elementAt(j);
                	breakForTestCondition(n,m,j,cond);
                    if (cond instanceof whenCondition)
                    {
                    	timer.start(Timer.WHEN_FILTER);
                    	whenCondition wc = (whenCondition) cond;
                    	// value = wc.rightValue();
                        if (startIsLHS) {passed = passed && wc.eval1(startNode,NSSet());}
                        else {passed = passed && wc.eval1(n,NSSet());}
                    	timer.stop(Timer.WHEN_FILTER);
                    }
                    else if (cond instanceof linkCondition)
                    {
                    	timer.start(Timer.LINK_FILTER);
                    	linkCondition lc = (linkCondition) cond;
                    	// value = lc.rhsEndToRightValue().stringForm();
                        if (startIsLHS) {passed = passed && lc.eval2(startNode,n,NSSet());}
                        else {passed = passed && lc.eval2(n,startNode,NSSet());}
                    	timer.stop(Timer.LINK_FILTER);
                    }
                    resultForConditionBreak(m,passed);
                }
                if (passed) res.addElement(n);
            }
            // System.out.println("Filter on '" + value + "'; " + res.size() + " out of " + a.size());
        }
    	timer.stop(Timer.FILTER_NODE_VECTOR);
        return res;
    }


  //-----------------------------------------------------------------------------------------------
  //                    indexes for link conditions
  //-----------------------------------------------------------------------------------------------
    
    private boolean useLinkIndexing = true; // default is that indexes are used for everything except testing
    
    /**
     * turn indexing of link conditions on an off (used in testing ,to 
     * test that retrievals with indexes give identical results 
     * to retrievals without)
     * @param useLinkIndexing if true, link indexes will be created and used
     */
    public void setLinkIndexing(boolean useLinkIndexing) throws MapperException
    {
    	this.useLinkIndexing = useLinkIndexing;
    	makeNodeIndex(); // make or empty the index
    }
    

    /* First String key = absolute XPath to the node
     * Second String = relative XPath from node to the condition value (left path or right path); 
     * Third String key = condition value;
     * Vector = Vector of all Nodes in the document for which the XPath leads to the value 
     * 
     * Includes only nodes whose String values are one side of a link condition
     * with no function applied. 
     * */
    private Hashtable<String,Hashtable<String,Hashtable<String,Vector<Node>>>> nodeIndex;
    
    /**
     * create the whole node index for link conditions,
     * @throws MapperException
     */
    private void makeNodeIndex() throws MapperException
    {
    	timer.start(Timer.MAKE_NODE_INDEX);
    	nodeIndex = new Hashtable<String,Hashtable<String,Hashtable<String,Vector<Node>>>>();
    	
    	// find all absolute and relative paths to be indexed
    	Hashtable<String,Vector<String>> pathTable = pathsToIndex();
    	
    	// follow each absolute path to find the full node set
    	if (useLinkIndexing)
    	for (Enumeration<String> en = pathTable.keys();en.hasMoreElements();) try
    	{
    		String absPath = en.nextElement();
    		if (traceNodeIndexing) System.out.println("Absolute path: " + absPath);
    		Hashtable<String,Hashtable<String,Vector<Node>>> 
    			absPathIndex = new Hashtable<String,Hashtable<String,Vector<Node>>>();
    		Vector<Node> nodes = XPathAPI.selectNodeVector(timer,theXMLFileRoot, absPath, NSSet()); 
    		Vector<String> relPaths = pathTable.get(absPath);
    		
			for (Iterator<String> ip = relPaths.iterator(); ip.hasNext();)
			{
				String relPath = ip.next();
				int nodesFound = 0;
				Hashtable<String,Vector<Node>> relPathIndex = new Hashtable<String,Vector<Node>>();
				for (Iterator<Node> it = nodes.iterator(); it.hasNext();)
	    		{
	    			Node node = it.next();
	    			Vector<Node> valueNodes = XPathAPI.selectNodeVector(timer,node, relPath, NSSet());
	    			for (Iterator<Node> in = valueNodes.iterator();in.hasNext();)
	    			{
	    				String value = XMLUtil.textValue(in.next());
	    				if (!(value.equals("")))
	    				{
	    					nodesFound++;
		    				Vector<Node> nodesWithValue = relPathIndex.get(value);
		    				if (nodesWithValue == null) nodesWithValue = new Vector<Node>();
		    				nodesWithValue.add(node);
		    				relPathIndex.put(value,nodesWithValue);	    					
	    				}
	    			}
	    		}
				absPathIndex.put(relPath, relPathIndex);
				if (traceNodeIndexing) System.out.println("Relative path: " + relPath + ": " + relPathIndex.size() + " values, " + nodesFound + " nodes");
			}
			nodeIndex.put(absPath, absPathIndex);
    	}
    	catch (XPathExpressionException ex) {throw new MapperException(ex.getMessage());}
    	timer.stop(Timer.MAKE_NODE_INDEX);    	
    }
    
    
    /**
     * 
     * @return table of all relative paths which need to be indexed.
     * These are LHS and RHS paths of cross conditions 
     * which have no functions.
     */
    private Hashtable<String,Vector<String>> pathsToIndex() throws MapperException
    {
    	Hashtable<String,Vector<String>> pathTable = new Hashtable<String,Vector<String>>();
    	List<EObject> crossConditions = ModelUtil.getEObjectsUnder(ms(), MapperPackage.Literals.CROSS_CONDITION);
    	for (Iterator<EObject> it = crossConditions.iterator();it.hasNext();)
    	{
    		EObject next = it.next();
    		CrossCondition crossCondition = (CrossCondition)next;
    		if (crossCondition.eContainer() instanceof Mapping)
    		{
    			int end = 0; // being lazy; I don't think it matters
        		linkCondition linkCond = new linkCondition(crossCondition,end);

        		if (crossCondition.getLeftFunction().equals(""))
            	{
            		String absPath = linkCond.rootToLHSNode().stringForm();
            		String relPath = linkCond.lhsEndToLeftValue().stringForm();
            		Vector<String> paths = pathTable.get(absPath);
            		if (paths == null) paths = new Vector<String>();
            		if (!GenUtil.inVector(relPath, paths)) paths.add(relPath);
            		pathTable.put(absPath,paths);
            	}
            	
            	if (crossCondition.getRightFunction().equals(""))
            	{
            		String absPath = linkCond.rootToRHSEnd().stringForm();
            		String relPath = linkCond.rhsEndToRightValue().stringForm();
            		Vector<String> paths = pathTable.get(absPath);
            		if (paths == null) paths = new Vector<String>();
            		if (!GenUtil.inVector(relPath, paths)) paths.add(relPath);
            		pathTable.put(absPath,paths);
            	}   			
    		}
    	}
    	return pathTable;
    }


//-----------------------------------------------------------------------------------------------
//              Handling (class,subset)s that are not uniquely represented
//-----------------------------------------------------------------------------------------------

    
 /* this Hashtable has key = 'className'$'subset', and the value is a Hashtable
whose key is a unique identifier made of concatenated property values, and whose value
is a Vector of objectReps for all the representations of the object with that identifier.  */
private Hashtable<String, Hashtable<String, Vector<objectRep>>> nonUniqueClassSets = new Hashtable<String, Hashtable<String, Vector<objectRep>>>();


/* a string which uniquely identifies this object, and
will be the same for other representations of the same non-uniquely represented object*/
private String uniqueId(objectRep oRep) throws MapperException
{
    String res = "";
    Vector<String> pKey = primaryKey(oRep.cSet());
    if (pKey != null) for (int i = 0; i < pKey.size(); i++)
    {
        String field = pKey.elementAt(i);
        String val = getPropertyValue(oRep,field);
        res = res + val + "$";
    }
    return res;
}


/* A Hashtable of distinct objects of some (class,subset) which may be non-uniquely
represented in the document.
Key = the uniqueId string for the object, made of concatenated primary key property values.
Value = a Vector of objectReps, for all the different representations of one object. */
private Hashtable<String, Vector<objectRep>> distinctObjects(ClassSet cs)
throws MapperException
{
    Hashtable<String, Vector<objectRep>> dObj = new Hashtable<String, Vector<objectRep>>();
    objectMapping om = namedObjectMapping(cs);
    Vector<objectToken> oReps = theObjectReps(om);
    for (int i = 0; i < oReps.size(); i++)
    {
        objectRep oRep = (objectRep)oReps.elementAt(i);
        String uid = uniqueId(oRep);
        Vector<objectRep> previous = dObj.get(uid);
        if (previous == null)
            {previous = new Vector<objectRep>();}
        previous.addElement(oRep);
        dObj.put(uid,previous);
    }
    return dObj;
}


/* for a non-uniquely represented ClassSet which has a unique identifier,
return a Vector of objectReps including only one objectRep for each
distinct object that is represented. */
private Vector<objectToken> getDistinctObjectReps(ClassSet cs) throws MapperException
{
    Vector<objectToken> dov = new Vector<objectToken>();
    Hashtable<String, Vector<objectRep>> dObjs = getOrCreateORepGroups(cs);
    // for each prime key, choose the first of its Vector of objectReps.
    for (Enumeration<Vector<objectRep>> en = dObjs.elements(); en.hasMoreElements();)
    {
        Vector<objectRep> oReps = en.nextElement();
        objectRep oRep = oReps.elementAt(0);
        dov.addElement(oRep);
    }
    return dov;
}


// if the Hashtable of objectReps keyed by prime key does not yet exist, create it and store it
private Hashtable<String, Vector<objectRep>> getOrCreateORepGroups(ClassSet cs)
throws MapperException
{
    Hashtable<String, Vector<objectRep>> dObjs = nonUniqueClassSets.get(cs.stringForm());
    if (dObjs == null)
    {
        dObjs = distinctObjects(cs);
        nonUniqueClassSets.put(cs.stringForm(),dObjs);
    }
    return dObjs;
}


/* The object represented by oRep is in a (class,subset) which is not uniquely represented, but
has available unique identifier properties.
To find all objectReps at the other end of an association:
 - find and concatenate the primary key property values
 - look up the Vector of all objectReps which (non-uniquely) represent the same object
 - for each one, follow the XPaths to find all objects at the other end of the association
 - combine all of these, without duplicate Nodes
 */
private Vector<objectRep> followAllAssocXPaths(objectRep oRep, AssociationMapping am,
    ClassSet otherEndCSet, int oneOrTwo)
    throws MapperException
{
    String primaryKey = uniqueId(oRep); // primary key of concatenated property values of this-end object
    Hashtable<String, Vector<objectRep>> dObjs = getOrCreateORepGroups(oRep.cSet());
    Vector<objectRep> oReps = dObjs.get(primaryKey); // all objectReps of the same object (same primary key)
    if ((oReps == null)|((oReps != null) && (oReps.size() == 0)))
        {throw new MDLReadException("Cannot find any objectReps with unique identifier '" + primaryKey + "'");}
    // loop over all representations of the same object, building up the Hashtable of other-end objects
    Hashtable<Node, objectRep> otherEndObjs = new Hashtable<Node, objectRep>();
    for (int i = 0; i < oReps.size(); i++)
    {
        objectRep oneRepresentation = (objectRep)oReps.elementAt(i);
        // follow the association XPaths from one objectRep
        Vector<objectRep> fromOneObjectRep = followAssocXPaths(oneRepresentation,am, otherEndCSet,oneOrTwo);
        for (int j = 0; j < fromOneObjectRep.size(); j++)
        {
            objectRep otherEndRep = fromOneObjectRep.elementAt(j);
            // cannot store two objectReps with the same node
            otherEndObjs.put(otherEndRep.objNode(),otherEndRep);
        }
    }
    // convert the Hashtable to a Vector
    Vector<objectRep> res = new Vector<objectRep>();
    for (Enumeration<objectRep> en = otherEndObjs.elements(); en.hasMoreElements();)
    {
        objectRep otherEndRep = en.nextElement();
        res.addElement(otherEndRep);
    }
    return res;
}

	//----------------------------------------------------------------------------------------
	//                                   Debugging
	//----------------------------------------------------------------------------------------

	public DebugPostBox getDebugPostBox() {return debugPostBox;}
	private DebugPostBox debugPostBox = null;
	
	/**
	 * Give access to the Debug View via the Debug PostBox, 
	 * and attend to break points if it is non-null
	 * @param debugView
	 */
	public void setDebugPostbox(DebugPostBox debugPostBox) 
	{
		this.debugPostBox = debugPostBox;
		if (debugPostBox != null) attendToBreakPoints = true;
	}

    
    /* the default behaviour of the MDLXOReader is to ignore break points, i.e. 
     * carry on as normal even if a mapping has a break point.*/
    private boolean attendToBreakPoints = false;


/**
 * 
 * @param m
 * @return true if you are to break on encountering a mapping; i.e 
 * if you are attending to break points, and if either the mapping
 * or its parent AssociationMapping has been made a break point
 */
private boolean breakInMapping(MappingTwo m)
{
	if ((attendToBreakPoints) && (m!= null))
	{
		Mapping modelMap = m.map();
		/*  AssociationEndMappings act as breakpoints if their parent AssociationMapping 
		 * has been made a breakpoint*/
		EObject parent = modelMap.eContainer();
		if ((parent instanceof Mapping) && ((Mapping)parent).isBreakPoint()) return true;
		// otherwise look at the mapping itself
		return modelMap.isBreakPoint();
	}
	else return false;
}

/**
 * Break point on encountering a mapping marked with a break point.
 * The next step shown in the debug row, is always to follow 
 * an XPath to pick up a number of nodes.
 * The result of the step shows the number of nodes found.
 * @param m
 */
private void breakForMapping(MappingTwo m, Node startNode, int xPathType, boolean isIndexed) throws MapperException
{
    if (breakInMapping(m)) 
    {
    	DebugRow row = new DebugRow(m,xPathType,isIndexed);
    	debugPostBox.setDebugRow(row);
    	debugPostBox.setDebugNode(startNode);
    	haltAndSuspend();
    }
}

@SuppressWarnings("deprecation")
	private void haltAndSuspend()
	{
    	debugPostBox.setHalted(true);
    	debugPostBox.getReaderThread().suspend();		
	}

	private void stopRunOn()
	{
		if (debugPostBox != null) debugPostBox.setRunOn(false);
	}


	/**
	 * post the result to be shown for a mapping break -
	 * @param found: the number of records found by following the XPath
	 */
	private void resultForMappingBreak(MappingTwo m, int found)
	{
        if (breakInMapping(m)) 
        {
    		String end = " nodes";
    		if (found == 1) end = " node";
    		debugPostBox.setLastResult("Found " + found + end);        	
        }
	}
	
    /**
     * Break point on finding a node at the end of an XPath that needs to be tested against one or more 
     * conditions
     * @param node
     * @param m
     * @param nConditions
     */
	private void breakForNodeFound(Node node, int nodeNumber, int nodes, MappingTwo m, int nConditions)
    {
        if (breakInMapping(m)) 
        {
        	DebugRow row = new DebugRow(node,nodeNumber, nodes, nConditions);
        	debugPostBox.setDebugRow(row);
        	debugPostBox.setDebugNode(node);
        	haltAndSuspend();
        }    	
    }
	
	private void breakForTestCondition(Node node, MappingTwo m, int conditionNo, Condition cond)
	{
        if (breakInMapping(m)) 
        {
        	DebugRow row = new DebugRow(node, conditionNo, cond);
        	debugPostBox.setDebugRow(row);
        	debugPostBox.setDebugNode(node);
        	haltAndSuspend();
        }    			
	}
	
	private void resultForConditionBreak(MappingTwo m, boolean passed)
	{
        if (breakInMapping(m)) 
        {
    		String result = "Passed";
    		if (!passed) result = "Failed";
    		debugPostBox.setLastResult(result);        	
        }
	}
	
	private void breakForReturnValue(MappingTwo m, Node node)
	{
        if (breakInMapping(m)) 
        {
        	DebugRow row = new DebugRow(m,node);
        	debugPostBox.setDebugRow(row);
        	debugPostBox.setDebugNode(node);
        	haltAndSuspend();
        }    					
	}
	
	private void resultForReturnValueBreak(MappingTwo m, String value)
	{
        if (breakInMapping(m)) 
        {
    		debugPostBox.setLastResult("Return '" + value + "'");        	        	
        }
		
	}
	
	private void breakToInform(MappingTwo m, int rowType)
	{
        if (breakInMapping(m)) 
        {
        	DebugRow row = new DebugRow(rowType);
        	debugPostBox.setDebugRow(row);
        	haltAndSuspend();
        }    							
	}
	
	static int OBJECT_COUNT = 0;
	static int NODE_COUNT = 1;
	
	private void resultForInformBreak(MappingTwo m, int count, int countType)
	{
		String [] countString = {" object"," node"};
        if (breakInMapping(m)) 
        {
        	String result = count + countString[countType];
        	if (count != 1) result = result + "s";
    		debugPostBox.setLastResult(result);        	        	
        }
	}

	private void trace(String s) {if (tracing) System.out.println(s);}

}
