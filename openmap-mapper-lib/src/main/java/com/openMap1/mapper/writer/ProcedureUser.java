package com.openMap1.mapper.writer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;

import org.w3c.dom.Element;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MDLWriteException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.RunIssue;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.util.FileUtil;
import com.openMap1.mapper.util.GenUtil;
import com.openMap1.mapper.util.Timer;
import com.openMap1.mapper.util.XMLInputFile;
import com.openMap1.mapper.util.XMLOutputFile;
import com.openMap1.mapper.util.XMLUtil;
import com.openMap1.mapper.util.messageChannel;
import com.openMap1.mapper.MappedStructure;

/**
 * superclass of classes that use wproc procedures - to 
 * execute them or generate XSLT from them.
 * 
 * @author robert
 *
 */

abstract public class ProcedureUser extends ProcedureClass {

    // Hashtable of all procedures found to be missing at run time
	protected Hashtable<String, String> missingProcedures;
	
	// cache mappings for V3 data types to avoid reloading them many times
	private Hashtable<String,ProcedureClass> dataTypeProcClasses = new Hashtable<String,ProcedureClass>();
	
	/**
	 * All issues that were noted when running the translation 
	 * or generating XSLT. 
	 * outer key = string form of root path
	 * Inner key = a unique identifier for the issue
	 * @return
	 */
	public Hashtable<String,Hashtable<String,RunIssue>> allRunIssues() {return allRunIssues;}
	protected Hashtable<String,Hashtable<String,RunIssue>> allRunIssues;
	
    /** XML file for the output XML (result) */
    protected XMLOutputFile xout() {return xout;}

    protected XMLOutputFile xout;  // XML output file

    public void setXMLOutputFile(XMLOutputFile xout) 
    {
    	this.xout = xout;
    	setOutputFileInAllProcs(xout); 
    }


    /** true for detailed trace of WPR code execution */
    public boolean runTracing() {return runTracing;}
    protected boolean runTracing; // if true, write trace output from running XW procedures

    /** XML file (typically with extension .wpr) to read the XML writing procedures,
     * if they have been compiled before */
    public XMLInputFile xProcIn() {return xProcIn;}
    protected XMLInputFile xProcIn;
    
    //protected Element procsFileRoot;

    /** gets objects, attributes and association instances from the input XML document */
    public objectGetter oGet() {return oGet;}
    protected objectGetter oGet; // gets object model data to be written out

    protected boolean readXMLProcs = false;

    protected IFile resultFile;
    
    // dummies of methods not relevant to procedure users
    
    public boolean codeTracing() {return false;}
    protected void codeTrace(String s) {}
    
    protected void writeXMLProc(WProc proc) {}
    
    private boolean tracing = true;
    
    //----------------------------------------------------------------------------------------
    //                                constructor
    //-----------------------------------------------------------------------------------------
    
    /**
     * constructor for use inside Eclipse
     */
    public ProcedureUser(IFile procsFile, objectGetter oGet,
    		MappedStructure ms, messageChannel mChan, 
    		IFile resultFile, boolean runTracing)  throws MapperException
    {
    	super(ms,mChan);
        if ((procsFile != null) && (procsFile.exists())) try
        {
            xProcIn = new XMLInputFile();
            xProcIn.readXMLFile(procsFile.getContents()); // this sets instance variable root
            readXMLProcs = true;
        }
        catch (Exception ex)
        	{throw new MapperException("Failed to open write procedures file: " + ex.getMessage());}

        // create empty output XML file
        createXMLOutputFile();
        
        this.oGet = oGet;
        this.resultFile = resultFile;  
        
        this.runTracing = runTracing;
        
    }

    
    /**
     * constructor for use outside Eclipse, in standalone applications
     */
    public ProcedureUser(objectGetter oGet,
    		MappedStructure ms, EPackage classModel, messageChannel mChan, 
    		boolean runTracing)  throws MapperException
    {
    	super(ms,classModel,mChan);
    	
        xProcIn = new XMLInputFile();
        xProcIn.setRootElement(ms.procedureFileRoot());
        readXMLProcs = true;
    	
        // create empty output XML file
        createXMLOutputFile();
        
        this.oGet = oGet;
        
        this.runTracing = runTracing;
    }

    protected abstract void createXMLOutputFile()  throws MapperException;

  //--------------------------------------------------------------------------
//                 Reading in an XML form of the generation procedures
  //--------------------------------------------------------------------------

  /**
   * read the WProc procedures from one or more supplied WProc files, and store them in
   * Hashtable<String, Hashtable<String, WProc>> procedureTables (from ProcedureClass)
   * 
   * @param mergeImports if true, wherever a WProc has a fillElement step invoking an imported Wproc file,
   * merge in the WProcs from the imported file
   * @param isXSLTGeneration: if true, the WProcs will be of class WProcXSLT
   */
   public void readProcedures(boolean mergeImports, boolean isXSLTGeneration) throws MapperException
  {
      if (readXMLProcs)
      {
          if (xProcIn.root() == null)
            {throw new MDLWriteException("Could not read file of write procedures.");}
          else
          {
              Element topProcs = (Element)xProcIn.root();
              if (!(xProcIn.getLocName(topProcs).equals("procedures")))
                  {throw new MDLWriteException("Root element is not a <procedures> element.");}
              Xpth startPath = new Xpth(NSSet());
              startPath.setFromRoot(true);

              // read the startup procedure
              Element startProcEl = XMLUtil.firstNamedChild(topProcs,"proc");
              if (startProcEl != null)
              {
                  WProc wp = new WProc(startPath,this,startProcEl);
                  if (isXSLTGeneration)
                      wp = new WProcXSLT(startPath,this,(XSLGenerator)this, startProcEl);
                  storeProcedure(wp,false);
              }
              else {message("No startup procedure in file");}

              // recursive descent of XML tree to read all other procedures
              Vector<String> wProcFiles = new Vector<String>();
              Vector<String[]> elementClasses = null; // makes all element tag names pass the inclusion test
              readProceduresInSubtree(topProcs,startPath,elementClasses,mergeImports,isXSLTGeneration,this,wProcFiles,null, null, null);
          }
      }
      else
          {throw new MDLWriteException("No XML file of write procedures has been specified");}
  }

  /**
   * 
   * @param el the <element> element currently being analysed for its one or two WProcs
   * @param pathToEl XPath from the root to this element
   * @param elementClasses Vector of [tag name, className] pairs from the WProc for this element
   * 
   * @param mergeImports if true, when fillElement steps are encountered, 
   * expand the referenced WProc file in line
   * @param isXSLTGeneration if true, store WProxXSLT objects
   * @param PC procedureClass to be used in WProc constructors, so they have the right output mappings
   * @param wProcFiles  when mergeImports = true, the stack of wproc files read so far,
   * to avoid infinite self-recursion
   * @param outerWProc when mergeImports = true, the WProc which made this import, from which when-values must be copied 
   * @param oldCSet when mergeImports = true, the classSet to be replaced by the parameter classSet in all steps
   * @param newCSet when mergeImports = true, the parameter classSet to replace the old one in all steps
   * @throws MapperException
   */
   private void readProceduresInSubtree(Element el, Xpth pathToEl,
		   Vector<String[]> elementClasses,
		   boolean mergeImports,boolean isXSLTGeneration,
		   ProcedureClass PC,
		   Vector<String> wProcFiles, WProc outerWProc,
		   ClassSet oldCSet, ClassSet newCSet) throws MapperException
  {
      Vector<Element> elements = XMLUtil.namedChildElements(el,"element");
      for (int i = 0; i < elements.size(); i++)
      {
          Element child = (Element)elements.elementAt(i);
          String tagName = child.getAttribute("name");
          
          // only include the WProc for this child if its class is represented in the input (for XSLT generation)
          if (includeThisChild(tagName,elementClasses))
          {
              Xpth nextPath = pathToEl.addInnerStep(tagName);

              // prepare to record which classes are required to be represented to populate each element of the child element's children
              Vector<String[]> nextElementClasses = new Vector<String[]>();

              Element procsEl = XMLUtil.firstNamedChild(child,"procedures");
              if (procsEl != null)
              {
                  Vector<Element> procs = XMLUtil.namedChildElements(procsEl,"proc");
                  for (int j = 0; j < procs.size(); j++)
                  {
                      Element procEl = (Element)procs.elementAt(j);
                      // if this WProc imports another WProc file, and imported WProc files are to be merged, do so
                      if ((mergeImports) && (fillElementStep(procEl) != null))
                      {
                    	  // garbage collect
              			  Runtime.getRuntime().gc();
                    	  doMergedImports(procEl,nextPath, isXSLTGeneration, wProcFiles, oldCSet, newCSet);
                      }
                      // in all other cases, store the WProc (and increment the Vector nextElementClasses)
                      else
                      {
                    	  readOneProcedure(procEl, nextPath, nextElementClasses,mergeImports, isXSLTGeneration, PC, outerWProc, oldCSet, newCSet);
                      }
                  }
              }
              
              // recursive step to child elements introduced in the same WProc file
              readProceduresInSubtree(child,nextPath, nextElementClasses,mergeImports,isXSLTGeneration, PC, wProcFiles,outerWProc, oldCSet, newCSet);
        	  
          }
      }
  }
   
   /**
    * @param tagName the tag name of a child element
    * @param elementClasses a Vector of [tag name,class name] pairs
    * @return true if the child tag name is in the list, with a class that is represented in the input; 
    * or with class name "" from an AddElement step
    * or true if the Vector is null (as it is on the top call to readProceduresInSubtree)
    */
   private boolean includeThisChild(String tagName,Vector<String[]>elementClasses)
   {
	   if (elementClasses == null) return true;
	   boolean include = false;
	   for (int i = 0; i < elementClasses.size(); i++)
	   {
		   String[] group = elementClasses.get(i);
		   if (group[0].equals(tagName))
		   {
			   // element introduced by AddElement step; no test
			   if (group.length == 1) include = true;

			   // element introduced by AllObjects step; test class is mapped
			   else if (group.length == 2) 
				   include = filterbyDoubleClassMappings(group[1]);

			   // element introduced by AssociationInstance step; test association is mapped
			   else if (group.length == 4) 
				   include = filterbyInputAssociationMappings(group[1],group[2],group[3]);
		   }
	   }
	   return include;
   }
   
   /*
    * ProcedureUser does not have access to the input mappings for XSLT generation, so cannot filter
    * .wproc files on that basis. This method is overridden by one in XSLGenerator which does filter
    */
   protected boolean filterbyDoubleClassMappings(String className)
   {
   		return true;
   }
   
   /*
    * ProcedureUser does not have access to the input mappings for XSLT generation, so cannot filter
    * .wproc files on that basis. This method is overridden by one in XSLGenerator which does filter
    */
   protected boolean filterbyInputAssociationMappings(String class1,String class2,String assocName)
   {
   		return true;
   }
   
   /**
    * 
    * @param procEl <proc> element whose contents define the WProc
    * @param nextPath path to the element of the WProc
    * @param elementClasses Vector of [tag name, class name] for shle elements; to be added to
    * @param mergeImports true if imported .wproc files from fillElement steps are to be merged in
    * @param isXSLTGeneration true for XSLT generation
    * @param outerWProc used to get further when values from
    * @param oldCSet classSet to be substituted
    * @param newCSet classSet to substitute it with
    * @throws MapperException
    */
   private WProc readOneProcedure(Element procEl, Xpth nextPath, Vector<String[]> elementClasses, boolean mergeImports, boolean isXSLTGeneration,
		   ProcedureClass PC,
		   WProc outerWProc,ClassSet oldCSet, ClassSet newCSet) throws MapperException
   {
       WProc wp = new WProc(nextPath,PC,procEl);
       if (isXSLTGeneration)
           wp = new WProcXSLT(nextPath,PC,(XSLGenerator)this,procEl);
       if (mergeImports)
       {
     	   if (newCSet != null) wp.replaceClassSet(oldCSet, newCSet);
     	   if (outerWProc != null) wp.takeWhenValues(outerWProc);
       }
       storeProcedure(wp,false); 
       
       // add to the list of [tag name, class] pairs
       Vector<String[]> newPairs = wp.tagConditions();
       for (int i = 0; i < newPairs.size(); i++) elementClasses.add(newPairs.get(i));
       return wp;
       
   }
   
   /**
    * @param procEl an element defining a WProc
    * @return if the described WProc has one 'fillElement' step (importing another WProc file),
    * the Element defining the step, which must be its last step; 
    * or null if there is no FillElement step in the WProc.
    * 
    */
   private Element fillElementStep(Element procEl) throws MapperException
   {
	   Element  fillElement = null;
       Vector<Element> steps = XMLUtil.namedChildElements(procEl,"step");
       for (int i = 0; i < steps.size(); i++)
       {
    	   Element el = steps.get(i);
    	   boolean isFill = el.getAttribute("stepType").equals("fillElement");
    	   if (isFill && (i < steps.size() -1))
    		   throw new MapperException("FillElement step can only be the last step of the WProc for element '" 
    				   + procEl.getAttribute("name") + "'");
    	   if (isFill) fillElement = el;
       }
       return fillElement;
   }

   /**
    * @param procEl the <proc> element containing a fillElement step
    * @param pathToEl XPath from the root to this element
    * 
    * @param isXSLTGeneration if true, store WProxXSLT objects rather than just WProc objects
    * @param wProcFiles  when mergeImports = true, the stack of .wproc files read so far,
    * to avoid infinite self-recursion
    * @param oldCSet the previous classSet to be replaced by the parameter classSet in all steps
    * @param newCSet the previous parameter classSet to replace the old one in all steps
    * @throws MapperException
    */
    private void doMergedImports(Element procEl, Xpth pathToEl,
 		   boolean isXSLTGeneration,
 		   Vector<String> wProcFiles, 
 		   ClassSet oldCSet, ClassSet newCSet) throws MapperException
 	{
    	
		/* make the WProc which made the import (to hand on its when values, 
		 * and append any other steps to the topw WProc of the imported file) */
        WProc outerWProc = new WProc(pathToEl,this,procEl);
        if ((oldCSet != null) && (newCSet != null))
        	outerWProc.replaceClassSet(oldCSet, newCSet);
		
        // extract the fillElement step from the XML
    	Element step = fillElementStep(procEl);

    	// calculate the ClassSet to substitute in the imported WProcs, from the importing WProc
    	ClassSet nextNewCSet = new ClassSet(step.getAttribute("className"),step.getAttribute("subset"));
    	
    	// if there are no input mappings to this class, stop the recursion
		if (!filterbyDoubleClassMappings(nextNewCSet.className()))
		{
		    storeProcedure(outerWProc,false);                	  	   
			return;
		}

    	// location of the imported mapper file (& determines location of the imported wproc file)
    	String mapperFileLocation = step.getAttribute("mapperPath");
    	
    	/* guard against infinite self-recursion; and calculate the new Vector to pass on in recursion 
    	 * Allow any .wproc file to be used up to 3 times */
    	if (GenUtil.countOccurrences(mapperFileLocation, wProcFiles) > 2) 
		{
		    storeProcedure(outerWProc,false);                	  	   
			return;
		}
    	Vector<String> newProcFiles = new Vector<String>();
    	for (int i = 0; i < wProcFiles.size(); i++) newProcFiles.add(wProcFiles.get(i));
    	newProcFiles.add(mapperFileLocation);
    	
    	
    	/* try to find the imported ProcedureClass (for the output mappings) 
    	 * and the root element of the imported WProc file */
    	Element importRoot = null;
    	String procLocation = "";
    	ProcedureClass importPC = null;
    	try 
    	{
    		importPC = getProcedureClass(mapperFileLocation);
    		importPC.setRunTracing(this.runTracing());

    		procLocation = FileUtil.wProcLocation(mapperFileLocation);
    		URI uri = URI.createURI(procLocation,true);
    		String location = FileUtil.removeFilePrefix(FileUtil.editURIConverter().normalize(uri).toString());  // strip off 'file:/'
    		importRoot = XMLUtil.getRootElement(location);
    		if (importRoot == null) throw new MapperException("Cannot find imported WProc file at '" + procLocation + "'");    		
    	}
    	
    	// if the imported WProc file cannot be found, terminate the recursion safely
    	catch (Exception ex)
    	{
    		System.out.println(ex.getMessage());
		    storeProcedure(outerWProc,false); 
		    return;
    	}
    	
    	if ((oldCSet != null) &&(nextNewCSet.equals(oldCSet))) nextNewCSet = newCSet; // if this classSet is already in a ClassSet substitution
		
		// find the parameter classSet to be replaced in all WProcs of this .wproc file
		Element impProc = XMLUtil.firstNamedChild(importRoot, "proc");
		Element impStep = XMLUtil.firstNamedChild(impProc, "step");
		if (!impStep.getAttribute("stepType").equals("getAllObjects"))
			throw new MapperException("Unexpected first step in top WProc of imported wproc file at '" + procLocation + "'");
		ClassSet nextOldCSet= new ClassSet(impStep.getAttribute("class"),impStep.getAttribute("subset"));
		
		// find the top <element> element of the imported WProc file, and the <proc> element inside it
		Element topImportElement = XMLUtil.firstNamedChild(importRoot, "element");
		if (topImportElement == null)
			throw new MapperException("No top <element> element in imported wproc file at '" + procLocation + "'");
		Element importProcs = XMLUtil.firstNamedChild(topImportElement, "procedures");
		Element importProc = XMLUtil.firstNamedChild(importProcs, "proc");
		if (importProc == null) throw new MapperException("No top WProc in imported wproc file at '" + procLocation + "'");
		
  	    // read the top WProc of the imported .wproc file, with mergeImports = true.
        Vector<String[]> elementClasses = new Vector<String[]>(); 
        WProc topImportProc = readOneProcedure(importProc, pathToEl, elementClasses,true, 
        		isXSLTGeneration, importPC, outerWProc, nextOldCSet, nextNewCSet);
        
        // append at the front any steps from the importing WProc, except its FillElement step
        topImportProc.preAppendSteps(outerWProc);
        
        // carry on the recursion in the imported .wproc file, storing WProcs, with mergeImports = true.
        readProceduresInSubtree(topImportElement,pathToEl,elementClasses, true,isXSLTGeneration, 
        		importPC, newProcFiles, outerWProc,nextOldCSet, nextNewCSet);			
		
 	}
    
    
    /**
     * retrieve procedure classes (sets of output mappings) 
     * caching the mappings for V3 data type classes
     */
    private ProcedureClass getProcedureClass(String mapperFileLocation) throws MapperException
    {
    	boolean isDT = isV3DataTypeMappingSet(mapperFileLocation);
    	ProcedureClass importPC = null;
    	
    	//  using private Hashtable<String,ProcedureClass> dataTypeProcClasses = new Hashtable<String,ProcedureClass>();
    	// if this is a set of V3 data type mappings, try to get it from the cache
    	if (isDT) importPC = dataTypeProcClasses.get(mapperFileLocation);
    	
    	if (importPC == null)
    	{
    		MappedStructure importMS = FileUtil.getImportedMappedStructure(ms(), mapperFileLocation);
    		if (importMS == null) throw new MapperException("Cannot find imported output mappings at '" + mapperFileLocation + "'");    		
    		importPC = new ProcedureClass(importMS, mChan()); 
    		
        	// if this is a new set of V3 data type mappings, store it in the cache
        	if (isDT) dataTypeProcClasses.put(mapperFileLocation,importPC);

        	// trace of memory usage
        	System.out.println("Import mappings at " + mapperFileLocation);
        	GenUtil.writeMemory();
    	}
    	
    	return importPC;
    }
    
    private boolean isV3DataTypeMappingSet(String mapperFileLocation)
    {
    	boolean isV3 = false;
    	StringTokenizer st = new StringTokenizer(mapperFileLocation,"/");
    	while (st.hasMoreTokens()) if (st.nextToken().equals("V3DataTypes")) isV3 = true;
    	return isV3;
    }
    


  /**
   * return the top element of the Domain Object Model (DOM)
   * of the output XML.
   */
  public Element outputDOM()
  {
      return xout.topOut();
  }

//--------------------------------------------------------------------------
//                          writing XML Output
//--------------------------------------------------------------------------

    /** execute all procedures to create output XML,
    starting from the start procedure*/
    public void executeProcedures(boolean runTracing) throws MapperException
    {
    	timer.start(Timer.EXECUTE_PROCEDURES);
    	//housekeeping
        if (oGet == null) throw new MDLWriteException("Null objectGetter for OXWriter");
        allRunIssues = new Hashtable<String,Hashtable<String,RunIssue>>() ;
        missingProcedures = new Hashtable<String, String>();
        this.runTracing = runTracing;
        
        // do the action
        boolean isXSLT = false;
        WProc startProc = getStartProcedure(isXSLT);
        startProc.executeProcedure(allRunIssues);
        
        // more housekeeping
        writeMissingProcedures();
    	timer.stop(Timer.EXECUTE_PROCEDURES);
    }

    /// return the start procedure, with the correct start context
    public WProc getStartProcedure(boolean isXSLT)  throws MapperException
    {
        WProc startProc = null;
        Xpth startPath = new Xpth(NSSet());
        Hashtable<String,WProc> sp = procedureTables.get(startPath.stringForm());
        // there is just one (start) procedure for the empty path
        for (Enumeration<WProc> en = sp.elements(); en.hasMoreElements();)
            {startProc = ((WProc)en.nextElement()).pClone();}
        if (startProc == null) {throw new MDLWriteException("Failed to find start procedure");}
        startProc.setContext(getStartContext());
        startProc.giveTimer(timer, false);
        return startProc;
    }

    subtreeContext getStartContext() throws MDLWriteException
    {
        Xpth startPath = new Xpth(NSSet());
        subtreeContext context = new subtreeContext(this,startPath,oGet,this);
        return context;
    }


      /* find a procedure which matches the root path, the create/revisit flag, and the
      when-condition values of the subtree context.
      Write an error message if none can be found.
      Then execute it.  */
      boolean callProcedure(int timerToStop,Element el, boolean createdElement, Xpth newPath, 
    		  subtreeContext context, Hashtable<String,Hashtable<String,RunIssue>> runIssues)
      throws MapperException
      {
    	  timer.start(Timer.CALL_PROCEDURES);
    	  /* stop the timer of the calling step, so that it does not double-count 
    	   * what is counted in the steps of this procedure */
    	  timer.stop(timerToStop);
          boolean result = false;

          runTrace("Call depth: " + newPath.size());
          WProc runProc = findProcedure(createdElement,newPath,context);
          if (runProc != null)
          {
              // pass the runtime environment to it
              runProc.setContext(context);
              runProc.setCurrentElement(el);
              runProc.setRootPath(newPath);
              runProc.setOutputFile(xout);
              // execute it
        	  timer.stop(Timer.CALL_PROCEDURES);
              result = runProc.executeProcedure(runIssues);
          }
          // You should always find a create procedure, but it is OK not to find a revisit procedure
          else  if (createdElement)
          {
                  throw new MDLWriteException("Amongst the procedures for root path "
                          + newPath.stringForm() + " none is appropriate.");
          }
          else  if (!createdElement) {result = true;}
          // restart the timer of the calling step
    	  timer.start(timerToStop);
          return result;
      }

      public WProc findProcedure(boolean createdElement, Xpth newPath, subtreeContext context)
      throws MapperException
      {
    	  timer.start(Timer.FIND_PROCEDURES);
          Hashtable<String, WProc> candidates = null;
          String tryPathString;
          Xpth tryPath,bestPath = null;
          WProc runProc = null;
          boolean found = false;
          
          /* normal case: XPaths are definite, so there are some WProcs stored under 
           * exactly this XPath */
          if (procedureTables.get(newPath.stringForm()) != null) bestPath = newPath;

          /* otherwise, if procedures are stored with indefinite paths,
           * find the vector of candidate procedures
          with the most specific path specification matching the root path */
          else for (Enumeration<String> en = procedureTables.keys();en.hasMoreElements();)
          {
              tryPathString = en.nextElement();
              tryPath = new Xpth(newPath.NSSet(),tryPathString);
              if ((tryPath.compatible(newPath)) && (tryPath.asSpecificAs(bestPath)))
                  {bestPath = tryPath;}
          }

          if (bestPath == null) // this exception will be trapped and the path stored for a later message
              {throw new MDLWriteException("Cannot find any WProc for path '" + newPath.stringForm() + "'");}
          else
          {
              candidates = procedureTables.get(bestPath.stringForm());
              // exhaustive search of all candidates to find which one matches the when-values and create/revisit flag
              for (Enumeration<WProc> en = candidates.elements(); en.hasMoreElements();)
              {
                  WProc template = en.nextElement();
                  if ((template.onCreate() ==  createdElement) && (template.matchWhenValues(context)))
                  {
                      if (found) {throw new MDLWriteException("Found more than one matching procedure for root path " + newPath.stringForm());}
                      // make a new runtime copy of the procedure
                      else {runProc = template.pClone();}
                      found = true;
                  }
              }
          }
          if (runProc == null) {throw new MapperException("No write procedure matched at path '" + newPath.stringForm() + "'");}
          runProc.giveTimer(timer, false);
    	  timer.stop(Timer.FIND_PROCEDURES);
          return runProc;
      }

      void runTrace(String s) {if (runTracing) message(s);}

	  /** set the namespaces in the output XML file to be
	  * the same as those in the output structure definition, in both prefix and URI
	  * - except we do not want the XML Schema namespace in the output namespaces. */
	  public void setOutputNamespaces() throws NamespaceException
	  {
	      xout.setNSSet(new NamespaceSet());
	      for (int i = 0; i < NSSet().size(); i++)
	      {
	          namespace ns = NSSet().getByIndex(i);
	          if (!(ns.URI().equals(XMLUtil.SCHEMAURI))) xout.NSSet().addNamespace(ns);
	      }
	  }

	    // store missing procedures without duplicates.
	    public void recordMissingProcedure(String pathString)
	        {missingProcedures.put(pathString,"1");}

	    // write a list of missing procedures, if there were any.
	    private void writeMissingProcedures()
	    {
	        if (missingProcedures.size() > 0)
	        {
	            message("Procedures for the following nodes were missing: ");
	            for (Enumeration<String> en = missingProcedures.keys(); en.hasMoreElements();)
	            {
	                String procPath = en.nextElement();
	                message(procPath);
	            }
	        }
	    }
	    
		@SuppressWarnings("unused")
		private void trace(String s) {if (tracing) System.out.println(s);}

}
