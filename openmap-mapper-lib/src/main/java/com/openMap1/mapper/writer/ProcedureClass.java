package com.openMap1.mapper.writer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.emf.ecore.EPackage;

import com.openMap1.mapper.core.MDLWriteException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.mapping.MDLBase;
import com.openMap1.mapper.mapping.filterProp;
import com.openMap1.mapper.util.XMLOutputFile;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.MappedStructure;

/**
 * superclass of classes that use wproc procedures - to compile them,
 * execute them or generate XSLT from them.
 * 
 * (was abstract, but I need to make an instance for WProcs in merged .wproc files)
 * 
 * @author robert
 *
 */
public class ProcedureClass extends MDLBase{

    /* Hashtable of sub-Hashtables of WProc objects.
    All WProcs in a sub-Hashtable have the same root Xpth specification;  key of Hashtable = Xpth string form.
    WProcs in a sub-Hashtable may differ in being create or revisit, and in values of when-conditions.  */
    protected Hashtable<String, Hashtable<String, WProc>> procedureTables;

    /* information to be extracted from the steering file,
    to pick out a first object of some class. */
    protected String startObjectClass = null;
    protected String startObjectPath = null;
    protected Vector<filterProp> startFilters = new Vector<filterProp>();

	  //-------------------------------------------------------------------------------
	  //                      Constructor
	  //-------------------------------------------------------------------------------
    
	/**
	 * constructor for use in Eclipse, where the MappedStructure can be relied upon
	 * to find the class model
	 */
    public ProcedureClass(MappedStructure ms, messageChannel mChan)  throws MapperException
	{
		super(ms,mChan);
		procedureTables = new Hashtable<String, Hashtable<String, WProc>>();
	}
    
    
	/**
	 * constructor for use outside Eclipse, where the MappedStructure cannot be relied upon
	 * to find the class model
	 */
	public ProcedureClass(MappedStructure ms, EPackage classModel, messageChannel mChan)  throws MapperException
	{
		super(ms,classModel, mChan);
		procedureTables = new Hashtable<String, Hashtable<String, WProc>>();
	}

	  //-------------------------------------------------------------------------------
	  //                      Store and call WProc procedures
	  //-------------------------------------------------------------------------------

	      /**
	       * store a WProc procedure in procedureTables for fast access
	       * If writeXMLProcs = true, write it out as XML
	       */
	      public void storeProcedure(WProc wp, boolean writeXMLProcs) throws MapperException
	      {
	          // find if there is already a Hashtable of procedures matching the root path
	          String pathKey = wp.pathSpec().stringForm();
	          Hashtable<String, WProc> candidates = procedureTables.get(pathKey);
	          // if not, make an empty Hashtable
	          if (candidates == null) candidates = new Hashtable<String, WProc>();
	          /* add the new procedure to it;  Only store one procedure for each
	          combination of when-values and create/revisit flag; fail if there are duplicates */
	          if (candidates.get(wp.whenValueString()) != null)
	          {
	              throw new MDLWriteException(
	                "trying to store more than one procedure for path '"
	                + pathKey + "', when-values '" + wp.whenValueString() + "'");
	          }
	          candidates.put(wp.whenValueString(),wp);
	          procedureTables.put(pathKey,candidates);
	          codeTrace("Storing a procedure for path '" + pathKey + "' with further identifier '" + wp.whenValueString() + "'");
	          codeTrace("");
	          if (writeXMLProcs) writeXMLProc(wp);
	      }
	      
	      // override
	      protected void writeXMLProc(WProc wp) throws MapperException {} 

	      /** set true for detailed trace of WProc code execution; override or reset */
	      public boolean runTracing() {return runTracing;}
	      private boolean runTracing = false;
	      
	      public void setRunTracing(boolean runTracing) {this.runTracing = runTracing;}

	      /** override */
	      protected void codeTrace(String s) {}
	      
	      /**
	       * reset the XML output file for all procedures added
	       * (This is a bit of an overkill, because each time a WProc is called, its 
	       * XML output file is updated; so we only need to set it for the top 
	       * WProc of any XMLWriter)
	       * @param xout
	       */
	      protected void setOutputFileInAllProcs(XMLOutputFile xout)
	      {
	    	  for (Enumeration<Hashtable<String, WProc>> en = procedureTables.elements();en.hasMoreElements();)
	    	  {
	    		  Hashtable<String, WProc> table = en.nextElement();
	    		  for (Enumeration<WProc> ep = table.elements();ep.hasMoreElements();)
	    		  {
	    			  WProc wp = ep.nextElement();
	    			  wp.setOutputFile(xout);
	    		  }
	    	  }
	      }

	      
	protected void message(String s) {mChan().message(s);}

}
