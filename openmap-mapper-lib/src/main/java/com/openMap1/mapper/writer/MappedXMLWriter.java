package com.openMap1.mapper.writer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EPackage;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.MDLWriteException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.notRepresentedException;
import com.openMap1.mapper.impl.ElementDefImpl;
import com.openMap1.mapper.mapping.filterAssoc;
import com.openMap1.mapper.mapping.filterProp;
import com.openMap1.mapper.mapping.objectMapping;
import com.openMap1.mapper.reader.objectToken;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.Timer;
import com.openMap1.mapper.util.XMLOutputFile;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.MappedStructure;

/**
 * XMLWriter using mappings and wproc files.
 * 
 * @author robert
 *
 */
public class MappedXMLWriter extends ProcedureUser implements XMLWriter{


    private Vector<Hashtable<Object, String>> keyTables; // vector of Hashtables (Node => string key) for objects in classes
    private Vector<String> keyedClasses; // Vector of classes for which key Hashtables exist
    private int keyInteger; // increment this to generate new keys
    
    private Hashtable<String,XMLWriter> importedWriters;
    
	/* if true, WProcs in imported .wproc files all become part of this XMLWriter, rather than calling other XMLWriters 
	 * (normally only set true only for XSLT generation. Set true here only to test in runtime translator)
	 * do not set true here - the runtime WProcs do not work, for subtle reasons */
    private boolean mergeProcedures = false;
    
    //----------------------------------------------------------------------------------------
    //                                constructors
    //-----------------------------------------------------------------------------------------
    
    /**
     * 
     * @param procsFile contains the writing procedures for the target XML
     * @param oGet delivers objects and properties from the source XML
     * @param ms Mappings and structure of the target XML
     * @param mChan a channel to write out messages
     * @param resultFile will contain the result of the translation
     * @param runTracing if true write a trace of running operations
     */
    public MappedXMLWriter(IFile procsFile, objectGetter oGet, 
    		MappedStructure ms, messageChannel mChan,
    		 IFile resultFile, boolean runTracing)  throws MapperException
    {
    	super(procsFile, oGet, ms, mChan, resultFile, runTracing);
    	initialiseWriter();
    } 

    
    /**
     * 
     * @param oGet delivers objects and properties from the source XML
     * @param ms Mappings and structure of the target XML
     * @param classModel the class model
     * @param mChan a channel to write out messages
     * @param runTracing if true write a trace of running operations
     */
    public MappedXMLWriter(objectGetter oGet, 
    		MappedStructure ms, EPackage classModel, messageChannel mChan,
    		  boolean runTracing)  throws MapperException
    {
    	super(oGet, ms, classModel, mChan,  runTracing);
    	initialiseWriter();
    }
    
    private void initialiseWriter() throws MapperException
    {
		// initialise runtime generation of unique object keys
		keyTables = new Vector<Hashtable<Object, String>>();
		keyedClasses = new Vector<String>();
		keyInteger = 0;  
		
		// read in the wproc procedures
		boolean isXSLT = false;
		readProcedures(mergeProcedures,isXSLT);
		
		// no imported XML writers have been made yet by this XMLWriter
		importedWriters = new Hashtable<String,XMLWriter>();
    }


    protected void createXMLOutputFile()  throws MapperException
    {
    	xout = new XMLOutputFile();
    	setOutputNamespaces();
    }

    
    /**
     * set the root of the XML instance being read, for this writer and all that it imports
     * @param el
     * @throws MapperException
     */
         public void setInputRoot(Node el)  throws MapperException
         {
        	 oGet.setRoot(el);
        	 for (Enumeration<XMLWriter> en = importedWriters.elements(); en.hasMoreElements();)
        	 {
        		 en.nextElement().setInputRoot(el);
        	 }
        	 createXMLOutputFile();
        	 setOutputFileInAllProcs(xout);
         }
    
    //----------------------------------------------------------------------------------------
    //                               imported XML writers
    //----------------------------------------------------------------------------------------
    
    /**
     * cached creation of XMLWriters for imported mapping sets
     */
    public XMLWriter getImportedXMLWriter(String mapperPath) throws MapperException
    {
    	XMLWriter writer = importedWriters.get(mapperPath);
    	if (writer == null) 
    	{
    		// this should work both inside Eclipse and outside
    		MappedStructure importMS = FileUtil.getImportedMappedStructure(ms(), mapperPath);
    		// pass in the class model root (for use in standalone applications)
    		writer = importMS.getXMLWriter(oGet(), ms().getClassModelRoot(), mChan(), runTracing);
    		importedWriters.put(mapperPath,writer);
    	}
		Runtime.getRuntime().gc();
		// make the imported writer have the same XML Output file as the importer
		writer.setXMLOutputFile(xout);
    	return writer;
    }
    
    //----------------------------------------------------------------------------------------
    //                               do the translation
    //----------------------------------------------------------------------------------------

    /**
	 * write the object model information from the objectGetter to an
	 * output XML
	 *
	 *@return the root Element of the created XML document
	 * @exception MDLWriteException - any major problem detected in making the translation
	 */
	public Element makeXMLDOM() throws MapperException
	{
		Element result = null;
	    executeProcedures(runTracing());
	    // after all WProcs have executed, post-process to order child elements correctly
	    timer.start(Timer.CURRENT_SUSPECT);
	    if (xout.topOut() != null) result  = orderOutputElements(xout.topOut());	
	    timer.stop(Timer.CURRENT_SUSPECT);
	    return result;
	}
    
    /**
	 * translate the object model information from the objectGetter to an
	 * output XML
	 *
	 * @param writeXMLFile - if true, write the output XML to a file (otherwise
	 *              consume it as a DOM)
	 *
	 * @exception MDLWriteException - any major problem detected in making the translation
	 */
	public void writeXML() throws MapperException
	{
	    executeProcedures(runTracing());
	    xout.writeOutput(resultFile);
	}

	
	/**
	 * Extend some Element of an output XML DOM (which represents some object
	 * in the object model, or has an ancestor element which represents that object)
	 * producing a subtree which represents the properties of that object, subordinate
	 * objects related to it, and their properties.
	 * 
	 * @param bareElement the Element to be extended
	 * @param oTok objectToken for the parameter class object, which the Element 
	 * to be extended (or one of its ancestors) represents
	 * @return the extended Element
	 * @throws MapperException if there is any major problem
	 */
	public Element extendXMLDOM(Element bareElement, objectToken oTok) throws MapperException
	{
		/* find out the top tag name of the mapping set. (This is not the tag name of
		 * the element passed in, but we pretend it is, so all the WProcs can be retrieved by path) */
		String topTagName = ms().getRootElement().getName();
		
		// make the path for the WProc which will add structure to this Element
		Xpth topPath = new Xpth(NSSet(),"/" + topTagName);
		
		// make a subtree context including the objectToken passed in
        subtreeContext context = new subtreeContext(this,topPath,oGet,this);
        // the subset in the calling importing set can be forgotten
        context.addObject(oTok, new ClassSet(oTok.className(),""));
        
        // call the WProc for the top Element of this mapping set; true = 'created'
        callProcedure(Timer.WPROC_STEP, bareElement,true,topPath,context,allRunIssues());
        
        // return the Element, now with substructure
        return bareElement;
	}

	//---------------------------------------------------------------------------
	//	                          Testing Inclusion Filters
	//---------------------------------------------------------------------------


	  /* Runtime test of inclusion filters;
	  Return true if the object represented by oTok passes the inclusion filters
	  for the subset of its class.

	  Also test any other filters arising from current when-condition values, as stored
	  in the subtree context.(Should these when-value filters be applied recursively to
	  objects involved in association filters?)

	  If assocName is not null, the object was got by that association,
	  which need not be checked again. */
	  boolean passesFilter(objectToken oTok, String subset, String assocName, subtreeContext context)
	      throws MapperException
	  {
		  timer.start(Timer.INCLUSION_FILTER);
	      Vector<filterAssoc> circCheck = new Vector<filterAssoc>();
	      boolean result = recursiveFilter(oTok,subset,assocName,circCheck,context);
		  timer.stop(Timer.INCLUSION_FILTER);
	      return result;
	  }

	  /* recursive version of the above, to check through several
	  association filters, with a check against circular revisiting of associations.
	  circCheck is a Vector of String arrays [class1,assocName,class2]  */
	  private boolean recursiveFilter(objectToken oTok, String subset,
	      String assocName, Vector<filterAssoc> circCheck, subtreeContext context)
	  throws MapperException
	  {
	      boolean res = true;
	      runTrace("Recursive Inclusion filters on an object of class '"
	              + oTok.className() + "', output subset '" + subset + "'");

	      /* first check any implied filters from nodes fixing when-condition values,
	      which also represent properties of this object. */
	      int nFilters = context.whenValues().size();
	      if (nFilters == 0) {runTrace("no when-condition filters");}
	      else if (nFilters > 0)
	      {
		      res = passesWhenFilters(oTok,subset,context);
		      if (res) {runTrace("passed when-condition filters");}
		      else {runTrace("failed when-condition filters");}	    	  
	      }

	      if (res)
	      {

	        // test the property filters applicable to this subset in the output
	        objectMapping om = namedObjectMapping(new ClassSet(oTok.className(),subset));
	        if (om == null)
	        {
	        	System.out.println("When testing filters, no object mapping for class " + oTok.className() 
	        			+ " subset '" + subset + "'");
	        	res = false;
	        }
	        else for (int i = 0; i < om.filterProps().size(); i++) if (res)
	        {
	          filterProp fp = (filterProp)om.filterProps().elementAt(i);
	          if (runTracing) {fp.write();}
	          boolean passed = false;
	          try
	          {
	              passed = passFilterProp(oTok,fp);
	              if (runTracing & !passed) {message("Failed filter; wrong value of property");}
	          }
	          catch (notRepresentedException ex) {if (runTracing) message("Failed filter; property is not represented");}
	          res = res & passed;
	        }

	        // test the association filters applicable to this subset in the output
	        if (om != null) for (int i = 0; i < om.filterAssocs().size(); i++) if (res)
	        {
	          boolean passed = true;
	          filterAssoc fa = (filterAssoc)om.filterAssocs().elementAt(i);
	          if (runTracing) fa.write();
	          /* check for a circular chain of association filters;
	          the same association name should not appear twice. */
	          if (isCircular(fa,circCheck))
	          {
	              throw new MDLWriteException("Circular set of association filters involving association '" + fa.assocName() + "'");
	          }
	          /* do not rely on association filters in the input; re-test all of them,
	          except if one is specifically excluded by the call. */
	          else if (!(fa.assocName().equals(assocName))) try
	          {
	              passed = passFilterAssoc(oTok,subset,fa,circCheck,context);
	              if (runTracing & !passed) message("Failed filter on association");
	          }
	          catch (notRepresentedException ex)
	          {
	        	  /* if an object is not to have an association, 
	        	   * and the association is not represented for that input subset, then it is OK */
	        	  if (fa.failToInclude()) passed = true;
	        	  else
	        	  {
		        	  passed = false; 
		        	  if (runTracing) message("Failed filter; association is not represented");	        		  
	        	  }
	          }
	       
	          res = res & passed;
	        }
	      }
	      return res;
	  }

	  /* return true if the association filter fa would make the existing chain
	  of association filters circular. */
	  private boolean isCircular(filterAssoc fa, Vector<filterAssoc> circCheck)
	  {
	      boolean res = false;
	      for (int i = 0; i < circCheck.size(); i++)
	      {
	          filterAssoc fb = circCheck.elementAt(i);
	          // if you encounter the same association filter twice, it is circular
	          if ((fa.cSet1().stringForm().equals(fb.cSet1().stringForm())) &&
	              (fa.cSet2().stringForm().equals(fb.cSet2().stringForm())) &&
	              (fa.assocName().equals(fb.assocName()))) res = true;
	          // if you encounter the same association filter in the opposite direction, it is circular
	          if ((fa.cSet1().stringForm().equals(fb.cSet2().stringForm())) &&
	              (fa.cSet2().stringForm().equals(fb.cSet1().stringForm())) &&
	              (fa.assocName().equals(fb.assocName()))) res = true;
	      }
	      return res;
	  }

	  /* the subtree context contains a set of choices of values for nodes defining
	  when-condition values, currently in force.
	  Some of these nodes may also represent properties.
	  If such a node represents a property of this class/subset object, which has therefore been
	  fixed, there is an extra inclusion filter for this object - that it
	  should have the correct value of the property. Test any such filters. */
	  private boolean passesWhenFilters(objectToken oTok, String subset, subtreeContext context)
	  throws MapperException
	  {
	      boolean passes = true;
	      int conds = context.whenValues().size();
	      if (conds > 0) runTrace("testing " + conds + " when conditions in context");
	      for (Enumeration<whenValue> en = context.whenValues().elements(); en.hasMoreElements();)
	      {
	          whenValue wv = (whenValue)en.nextElement();
	          if ((wv.propName() != null) && (wv.cSet() != null) &&
	              (wv.cSet().className().equals(oTok.className())) &&
	              (wv.cSet().subset().equals(subset))) try
	          {
	              runTrace("Testing property filter from when condition: " + wv.stringForm());
	              String actValue = oGet.getPropertyValue(oTok,wv.propName());
	              if (!(actValue.equals(wv.value())))
	              {
	                  passes = false;
	                  runTrace("Property filter failed: property '" + wv.propName() + "' has value '" + wv.value() + "'");
	              }
	          }
	          catch (notRepresentedException ex)
	          {
	              passes = false;
	              runTrace("Property filter failed: property '" + wv.propName() + "' is not represented");
	          }
	          else {runTrace("Property filter from when-condition " + wv.stringForm() + " is not applicable");}
	      }
	      return passes;
	  }

	  /** true if an object passes a property filter.
	  Currently the only tests supported are String '=',and real '<' and '>'. 
	  All others fail. */
	  public boolean passFilterProp(objectToken oTok, filterProp fp)  throws MapperException
	  {
	      boolean res = false;
	      String actValue = oGet.getPropertyValue(oTok,fp.property());
	      if (actValue !=null)
	      {
	            if (fp.test().equals("="))
	            {
	            	res =  (actValue.equals(fp.value()));
	            }
	            // any exception converting Strings to numbers leads to failure
	            else if (isInequality(fp.test())) try
	            {
	            	double actual = new Double(actValue).doubleValue();
	            	double comp = new Double(fp.value()).doubleValue();
	            	if (fp.test().equals(">")) res = (actual > comp);
	            	if (fp.test().equals("<")) res = (actual < comp);
	            }
	            catch (Exception ex)
	            {
	            	res = false;
	            	message("Exception testing inequality " 
	            			+ actValue + " " + fp.test() + " " + fp.value() + ": "  
	            			+ ex.getMessage() + "; led to failure");
	            }
	            else 
	            {
	            	message("Inclusion filters do not yet support test '" + fp.test() + "'");
	            }
	      }
	      runTrace("Value '" + actValue + "' gives pass " + res);
	      return res;
	  }
	  
	  private boolean isInequality(String test)
	  {
		  return ((test.equals("<"))|
				  (test.equals(">")));
	  }

	  // special filter to get start objects as defined in the steering file
	  public boolean specialFilter(objectToken oTok)  throws MapperException
	  {
	      boolean res = true;
	      for (int i = 0; i < startFilters.size(); i++)
	      {
	          filterProp fp = startFilters.elementAt(i);
	          res = res && passFilterProp(oTok,fp);
	      }
	      return res;
	  }

	  /* true if an object passes an association filter.
	  Recursively follow the association and test if the objects you find pass
	  their inclusion filters for the relevant output subset (as defined in this filter) */
	  private boolean passFilterAssoc(objectToken oTok, String subset, filterAssoc fa,
	          Vector<filterAssoc> circCheck, subtreeContext context)
	  throws MapperException
	  {
	      String otherClass="",otherSubset="", thisSubset="";
	      boolean res = false;
	      Vector<filterAssoc> newCircCheck = filterAssoc.vCopy(circCheck);
	      newCircCheck.addElement(fa); // to avoid going round and round the same association

	      /* find the class and subset at the other end of the association;
	      and redundantly find the subset at this end. */
	      if (fa.otherEnd() == 1)
	      {
	          otherClass = fa.cSet1().className();
	          otherSubset = fa.cSet1().subset();
	          thisSubset = fa.cSet2().subset();
	      }
	      else if (fa.otherEnd() == 2)
	      {
	          otherClass = fa.cSet2().className();
	          otherSubset = fa.cSet2().subset();
	          thisSubset = fa.cSet1().subset();
	      }
	      if (subset.equals(thisSubset)) // check should always pass
	      {
	          int thisEnd = 3 - fa.otherEnd();
	          
	          if (fa.failToInclude())
	          {
	        	  int j = 1; // for a debugging break point
	        	  if (j == 1) {}
	          }
	    

	          Vector<objectToken> assocObjects = oGet.getAssociatedObjects(oTok,fa.assocName(),otherClass,thisEnd);
	          
	          /* 'fail to include' associations: if there are any associated objects, fail immediately 
	           * (do not check their inclusion conditions before failing, because this might go circular.
	           * FIXME: if there is no danger of it going circular, should we test their inclusion conditions before failing ?*/
	          if (fa.failToInclude())
	          {
	        	  runTrace("Object of class " + oTok.className() + " should not be at end " 
	        			  + thisEnd + " of association " + fa.assocName() + " to class " + otherClass);
	        	  if (assocObjects.size() > 0)
	        	  {
	        		  runTrace("found " + assocObjects.size());
	        		  return false;
	        	  }
	        	  else return true;
	          }
	          
	          /* try out all objects at the other end of the association,
	          to see if any of them passes its inclusion filters. */
	          for (int i = 0; i <assocObjects.size(); i++) if (!res)
	          {
	              objectToken other = (objectToken)assocObjects.elementAt(i);
	              res = recursiveFilter(other,otherSubset,null,newCircCheck,context);
	          }
	      }
	      else {throw new MDLWriteException("Mismatching subsets in association filter: '"
	            + subset + "' and '" + thisSubset + "'");}
	      return res;
	  }

	//--------------------------------------------------------------------------
//	               Unique and reproducible keys for objects
	//--------------------------------------------------------------------------


	  /* return the Hashtable of object keys for a given class, or null if there is none.
	  */
	  Hashtable<Object, String> getKeyTable(String className)
	  {
	      int i;
	      Hashtable<Object, String> res = null;
	      for (i = 0; i < keyedClasses.size(); i++)
	      {
	          if (className.equals(keyedClasses.elementAt(i)))
	              {res = keyTables.elementAt(i);}
	      }
	      return res;
	  }

	  /*	depends on Vector keyTables of node => key Hashtable mappings;
	   on Vector keyedClasses saying which classes have Hashtables;
	   and on integer keyInteger for generating new keys.
	*/

	// return a unique and reproducible key for any object in the class model
	public String getKey(objectToken oTok)
	{
	    String res = null;
	    Hashtable<Object, String> classKeys;
	    classKeys = getKeyTable(oTok.className());
	    if (classKeys != null)
	    {
	        res = classKeys.get(oTok.objectKey());
	        if (res == null)
	        {
	            res = "k_" + oTok.className() + "_" + keyInteger;
	            keyInteger++;
	            classKeys.put(oTok.objectKey(),res);
	        }
	    }
	    else
	    {
	        classKeys = new Hashtable<Object, String>();
	        res = "k_" + oTok.className() + "_" + keyInteger;
	        keyInteger++;
	        classKeys.put(oTok.objectKey(),res);
	        keyTables.addElement(classKeys);
	        keyedClasses.addElement(oTok.className());
	    }
	    return res;
	}
	
	//------------------------------------------------------------------------------------------
	//    final post-processing of XML output to get the order of all child Elements correct,
	//    and eliminate the attributes which denote ordinal position
	//------------------------------------------------------------------------------------------
	
	/**
	 * @param unordered root element of XML produced by executing WProc procedures - 
	 * some child elements may have a 'virtual attribute' defining the position they should have
	 * in the final output XML
	 * @result root element of output in which the child elements have been ordered correctly,
	 * as defined by the virtual attribute, and the virtual attribute removed
	 */
	public static Element orderOutputElements(Element unordered) throws MapperException
	{
		Document newDoc = XMLUtil.makeOutDoc();
		Element newRoot = orderOutputElements(newDoc, unordered);
		newDoc.appendChild(newRoot);
		return newRoot;
	}

	private static Element orderOutputElements(Document outDoc, Element unordered)
	{
		// importNode with (deep = false) deals with namespaces and attributes. Text content?
		boolean deep = false;
		Element ordered = (Element)outDoc.importNode(unordered, deep);
		
		// remove (if present) the virtual attribute which defined the ordinal position of this element
		ordered.removeAttribute(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE);
		
		int len = unordered.getChildNodes().getLength();
		Hashtable<Integer,Vector<Element>> childEls = new Hashtable<Integer,Vector<Element>>();
		for (int n = 0; n < len; n++) childEls.put(new Integer(n), new Vector<Element>());
		
		for (int n = 0; n < len; n++)
		{
			Node nd = unordered.getChildNodes().item(n);
			if (nd instanceof Element)
			{
				Element child = (Element)nd;
				Element importedChild = orderOutputElements(outDoc,child); // recursive step

				int pos = -1; // out of range value
				String childPos = child.getAttribute(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE);
				try {pos = new Integer(childPos).intValue() - 1;} // range upward from 0, not 1
				catch (Exception ex) {} // pos stays out of range if the position attribute is empty

				/* if the defined ordinal position of the child is in range, store it for ordered output 
				 * (if two child elements have the same defined ordinal position, only one will be output)*/
				if ((pos > -1) && (pos < len)) 
					childEls.get(new Integer(pos)).add(importedChild);
				// otherwise output the child element immediately
				else {ordered.appendChild(importedChild);}
			}
			/* carry across attributes except the ordinal position attribute 
			 * (Note - W3C.dom package documentation said import element would do this */
			else if  (nd instanceof Attr)
			{
				String attName = ((Attr)nd).getName();
				if (!attName.equals(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE))
					ordered.appendChild(outDoc.importNode(nd, true));
			}
			else if (nd instanceof Text)
			{
				ordered.appendChild(outDoc.importNode(nd, true));				
			}
		} // end of loop over child nodes
			
		// ordered output of any child elements found with an ordinal position defined (after those without)
		if (hasOrderedChildNodes(unordered)) for (int i = 0; i < len; i++)
		{
			Vector<Element> els = childEls.get(new Integer(i));
			for (int p = 0; p < els.size();p++)
				{ordered.appendChild(els.get(p));}
		}
				
		return ordered;
	}
	
	/**
	 * @param el an Element
	 * @return true if any of its  child Elements have an ordinal position attribute.
	 */
	private static boolean hasOrderedChildNodes(Element el)
	{
		boolean ordered = false;
		for (int n = 0; n < el.getChildNodes().getLength(); n++)
		{
			Node nd = el.getChildNodes().item(n);
			if ((nd instanceof Element) && (((Element)nd)).hasAttribute(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE))
					ordered = true;
		}
		return ordered;
	}

}
