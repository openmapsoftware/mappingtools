package com.openMap1.mapper.writer;

import com.openMap1.mapper.mapping.*;
import com.openMap1.mapper.core.*;
import com.openMap1.mapper.util.*;
import com.openMap1.mapper.writer.ProcedureClass;
import com.openMap1.mapper.writer.TreeElement;
import com.openMap1.mapper.writer.USRepSet;
import com.openMap1.mapper.writer.WProc;
import com.openMap1.mapper.writer.codingContext;
import com.openMap1.mapper.writer.nodeRepSet;
import com.openMap1.mapper.writer.repSet;
import com.openMap1.mapper.writer.whenValue;
import com.openMap1.mapper.ElementDef;
import com.openMap1.mapper.ImportMappingSet;
import com.openMap1.mapper.MappedStructure;
import com.openMap1.mapper.NodeDef;
import com.openMap1.mapper.NodeMappingSet;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.ParameterClassValue;

import java.util.*;

import org.w3c.dom.*;

/**
 * This class generates an intermediate code of WProc procedures, which are
 * then either executed on the input object data, or converted to XSLT.
 * <p>
 * This class extends MDLBase. The MDL in question is the MDL for the output XML.
 */
public class ProcedureWriter extends ProcedureClass 
{	
    private TreeElement outTree; // tree structure of output XML

    /** true for detailed trace of WPR code generation */
    public boolean codeTracing() {return codeTracing;}
    private boolean codeTracing; // if true, write trace output from code generator
    
    /** 
     * Code generation warnings, indexed by XPath to the node involved 
     * Key = path string to the node
     * Value = List of compilation issues on that node 
     * */
    public Hashtable<String,List<CompilationIssue>> getCompilationIssues() 
    {
    	Hashtable<String,List<CompilationIssue>> allIssues 
    	 	= new Hashtable<String,List<CompilationIssue>>();
    	for (Enumeration<String> en = compilationIssues.keys();en.hasMoreElements();)
    	{
    		String path = en.nextElement();
    		Vector<CompilationIssue> issues = new Vector<CompilationIssue>();
    		Hashtable<String,CompilationIssue> iss = compilationIssues.get(path);
    		for (Enumeration<CompilationIssue> ep = iss.elements();ep.hasMoreElements();)
    			issues.add(ep.nextElement());
    		allIssues.put(path, issues);
    	}
    	return allIssues;
    }

    /**
     * initial empty set of CompilationIssues
     * key = path string to that node
     * value = a Hashtable with
     * key = a unique key for each copilation issue
     * value = the issue (noting how many times it has occurred?)
     */
    private Hashtable<String,Hashtable<String,CompilationIssue>> compilationIssues =
    	new Hashtable<String,Hashtable<String,CompilationIssue>>();

    /** XML file (typically with extension .wpr) to write out the XML writing procedures */
    public XMLOutputFile xProcOut() {return xProcOut;}
    private XMLOutputFile xProcOut;

    /** an abstract method in a superclass */
    public boolean runTracing() {return false;}
    

//-------------------------------------------------------------------------------
//                             constructor and initialisation
//-------------------------------------------------------------------------------


    public ProcedureWriter(MappedStructure ms, messageChannel mChan) 
    throws MapperException
    {
    	super(ms,mChan);

    	xProcOut = new XMLOutputFile();
        xProcOut.setTopOut(xProcOut.newElement("procedures"));

    	procedureTables = new Hashtable<String, Hashtable<String, WProc>>();
        outTree = ms().getRootElement();

        setMeaningInSubtree(); // setup for fast test of any mappings in the subtree of a node
    }

//-------------------------------------------------------------------------------
//               Generating procedure code; recursive descent backbone
//-------------------------------------------------------------------------------

	/**
	 * Generate XML writing procedures from a mapper file 
	 * (supplied in the constructor)
	 * @param codeTrace  if true, write out a code trace file (on the server)
	 * @return the root Element of the procedures file
	 * @throws MapperException
	 */
    public Element generateProcedures(boolean codeTrace) 
    throws MapperException
    {
        Xpth fromRoot; // path from root to current node
        codingContext topContextNoWhens; // initial code generation context
        USRepSet topRepSet; // mappings to the root node and its unique subtree
        WProc startupProcedure;

        setApexes(); // find the node at which each ClassSet needs to be put in the context

        codeTracing = codeTrace;
        doStartCodeTrace();
        
        compilationIssues = new Hashtable<String,Hashtable<String,CompilationIssue>>();

        // initial path from root to top element
        fromRoot = new Xpth(NSSet(),"/" + outTree.tagName());
        codeTrace("top node " + fromRoot.stringForm());

        // initial code-generation context; which has no when-conditions
        topContextNoWhens = new codingContext(this,fromRoot,codeTracing);
        codeTrace("Top context created");

        // create the startup procedure with an empty XPath
        Xpth ePath = new Xpth(NSSet());
        startupProcedure = new WProc(ePath,this,topContextNoWhens,true);
        topContextNoWhens.addPathSpec(ePath);
        codeTrace("Startup procedure created");

        // find if there are any objects represented on the root node or its unique subtree
        topRepSet = new USRepSet(this,fromRoot,outTree,topContextNoWhens);
        codeTrace("Found objects represented on root node");

        /* warn if there are any when-condition values in the unique subtree of the
        root node, as we have not yet worked out how to set those values */
        if (topRepSet.whenCombinations() > 1)
        {
        	int nature = CompilationIssue.COMPILE_MULTIPLE_CONDITION_VALUES;
        	codeError(nature,fromRoot,"There is no way to choose between " + topRepSet.whenCombinations()
              + " possible sets of when-condition values at the root node.");
        }
        
        // add when-conditions to the top context (only conditions on attributes of the top element)
        Vector<whenValue> whens = new Vector<whenValue>();
        if (topRepSet.whenCombinations() == 1) whens = topRepSet.whenCombination(0);
        codingContext topContext = topContextNoWhens.fixWhenValues(topWhens(whens));
        codeTrace("Added " + topWhens(whens).size() + " local when-values to top context.");

        // start recursive creation of procedures
        codeTrace("Start recursion");
        if ((topRepSet.hasObjects()) && (topRepSet.primaryObject() != null))
        {
            // find the class and subset of the primary object on the root node
            objectMapping om = topRepSet.primaryObject();
            codeTrace("Top node represents objects of class '" + om.className() + "'");

            /* check whether the top node is subject to special inclusion filters
            defined in the steering file. */
            boolean specialFilters = false;
            if ((om != null) && (startObjectPath != null) &&
                (startObjectPath.equals(fromRoot.stringForm())))
                {
                    if ((startObjectClass != null) && (startObjectClass.equals(om.className())))
                    {
                        specialFilters = true;
                        codeTrace("Filters from steering file");
                    }
                    else
                    {
                        throw new MDLWriteException("Class in steering file '" + startObjectClass + "' does not match class '"
                          + om.className() + "' represented on node '" + startObjectPath + "'");
                    }
                }
            
            // add instructions to set when-values for local value conditions on attributes of the top element
            for (Iterator<whenValue> iw = topWhens(whens).iterator();iw.hasNext();)
            {
            	whenValue wv = iw.next();
            	startupProcedure.as_setWVal(wv, "top element");
            }

            /* instruction to retrieve all those objects, make an element for each one,
            and hope there is only one of them */
            startupProcedure.as_allObj(om.cSet(),outTree.tagName(),specialFilters,"start");

            // record that the objects represented by the top node will be in the runtime context
            addApexObjectsToContext(topContext,topRepSet);
        }
        else
        {
            // instruction to make one root element, unconditionally
            startupProcedure.as_AddEl(outTree.tagName(),false,"start");
            codeTrace("Top node represents no objects.");
        }

        // store the startup procedure
        storeProcedure(startupProcedure,true);

        // generate an on-create procedure for the root element; this gets into the recursion
        createProcedure(fromRoot, topContext);
        codeTrace("End of code generation");codeTrace("");
        
        noteCompilationIssuesOnProcedures();
        
        return xProcOut.topOut();
    }
    
    /**
     * 
     * @param whens
     * @return only those when-conditions on attributes of the top element;
     * i.e those whose root path's second step is an attribute step
     */
    private Vector<whenValue> topWhens(Vector<whenValue> whens)
    {
    	Vector<whenValue> local = new Vector<whenValue>();
    	for (Iterator<whenValue> it = whens.iterator(); it.hasNext();) try
    	{
    		whenValue wv = it.next();
    		if ((wv.rootPath().size() > 1) && (wv.rootPath().step(1).isAttribute())) local.add(wv);
    	}
    	catch (MapperException ex) {}
    	return local;
    }
    
    private void doStartCodeTrace()
    {
        codeTrace(""); codeTrace("Trace of code generation");
        codeTrace("Output tree structure size: " + outTree.size() + ", depth: " + outTree.maxDepth());
        codeTrace("Deepest branch of output structure tree:");
        if (codeTracing) {outTree.writeOneDeepestBranch();}
        codeTrace("");
        codeTrace("Maximum path length of definite-XPath mappings: " + maxMappingDepth());
        codeTrace("Maximum inner path length of indefinite-XPath mappings: " + maxInnerDepth());    	
    }

    /* generate an XML writing procedure for the element with root path fromRoot,
    and defined current coding context.
    Generate an on-revisit procedure for the element as well as
    an on-create procedure; store the revisit procedure only if it has some steps. */
    private boolean createProcedure(Xpth fromRoot, codingContext context)
    throws MapperException
    {
          boolean success = true, s1,s2,s3,s4,s5;
          Xpth pathSpec = null;
          Xpth latest;
          WProc createProc, revisitProc;
          String tagName, rPath;

          tagName = fromRoot.innerStepString();

          // find the pathSpec for the new procedures
          USRepSet urs = new USRepSet(this,fromRoot, outTree, context);
          /* If we have already encountered some nodes with only indefinite-XPath mappings,
          and this node has no mappings in its unique subtree, then the path spec
          for this node's procedure must be indefinite. */
          if (urs.noMeaning() &&
              (context.latestPathSpec() != null) &&
              !context.latestPathSpec().definite())
          {
              pathSpec = context.latestPathSpec().copy();
              pathSpec.addStep(tagName);
          }

          /* If the node's unique subtree has no mappings (and we have not yet encountered a node with
          only indefinite-XPath mappings) or if it has some definite-XPath mappings, the
          path spec for this node's procedure must be definite. */
          else if ((urs.noMeaning())|(urs.hasDefinitePaths()))
              {pathSpec = fromRoot.copy();}
          /* Otherwise, when the node's unique subtree has all indefinite-XPath mappings,
           the path spec has a definite outer part, a  '//' step,
           and a definite inner part, with numbers of steps defined
          by the most definite mappings in the subtree.  */
          else if ((!urs.hasDefinitePaths()) && (urs.hasIndefinitePaths()))
          {
              if (urs.maxInnerDefiniteStepsToTopNode() == 0) {urs.write(mChan());} // case not allowed
              pathSpec = fromRoot.bridgeEnds(urs.maxOuterDefiniteSteps(),urs.maxInnerDefiniteStepsToTopNode());
          }
          else
          {
              urs.write(mChan());
              throw new MDLWriteException("Unexpected USRepSet case at path '"
                  + fromRoot.stringForm() + "'");
          }
          rPath = pathSpec.stringForm();

          /* pathSpec for this node, with inner step removed, should not be more specific
          than pathSpec for calling procedure; otherwise, the calling procedure may
          sometimes not be able to call this procedure.
          We should backtrack and make the higher pathspecs more definite to match this one -
          not yet implemented. */
          latest = context.latestPathSpec();
          if ((!latest.definite()) && (!latest.emptyPath()) &&
              (!latest.asSpecificAs(pathSpec.removeInnerStep())))
          {
          	int nature = CompilationIssue.COMPILE_OVER_SPECIFIC_PATH_SPEC;
              codeError(nature,fromRoot,"Path spec '" + pathSpec.stringForm()
                + "' for this node is more specific"
                + " than the path spec '" + latest.stringForm()
                + "' for the next outer node.");
          }

          /* do not make new procedures if procedures for this pathSpec have been made already */
          if (!context.alreadyHasPathSpec(pathSpec))
          {
              context.addPathSpec(pathSpec);
              // create and revisit procedures have the same procNumber
              createProc = new WProc(pathSpec, this, context, true);
              revisitProc = new WProc(pathSpec, this, context, false);

              codeTrace("");
              codeTrace("**********  Making procedure for path '" + rPath + "'");
              codeTrace(context.contextClasses());

              codeTrace("****  Code to add dependent objects to context at path specification '" + rPath + "'" );
              s1 = addToContextCode(createProc,revisitProc,fromRoot,context);

              /* attribute code has to come before element content code,
              because XSLT cannot add attributes to an element after its text has been added. Don't ask why. */
              codeTrace("****  Attribute code for path specification '" + rPath + "'" );
              s3 = attributeCode(createProc,fromRoot,context);

              codeTrace("****  Text content code for path specification '" + rPath + "'" );
              s2 = contentCode(createProc,revisitProc,fromRoot,context,"content");

              codeTrace("****  Child element code for path specification '" + rPath + "'" );
              s4 = childElementCode(createProc,revisitProc,fromRoot,context);
              
              codeTrace("****  ImportMapping set code for path specification '" + rPath + "'" );
              s5 = importMappingSetCode(createProc,fromRoot,context);

              storeProcedure(createProc,true);
              /* it is hard to avoid writing setWhenValue steps into revisit procedures,
              even if the revisit procedure will not be used.
              Only save the revisit procedure if some other steps have been put into it. */
              if (revisitProc.nonWhenSteps() > 0) storeProcedure(revisitProc,true);
              success = s1 & s2 & s3 & s4 & s5;
          }
          else {codeTrace("Not re-writing already-written procedure for path specification '" + rPath + "'");}

          return success;
    }

  //-------------------------------------------------------------------------------------------
  //             Generate code to call WProc procedures for an imported mapping set
  //-------------------------------------------------------------------------------------------
    
    
    /**
     * Case 5 - the Element has an imported mapping set.
     * The create procedure for the Element will contain just one step, 
     * to fill the Element using the WProc procedures for the imported 
     * mapping set.
     */
    private boolean importMappingSetCode(WProc createProc, Xpth fromRoot, codingContext context)
    throws MapperException
    {
    	// set false if anything goes wrong
    	boolean OK = true;
    	
    	// only do something if the node of the Mapping Set imports some other Mapping Set
    	NodeDef node = ms().getNodeDefByPath(fromRoot.stringForm());
    	if ((node instanceof ElementDef))
    	{
    		ElementDef ed = (ElementDef)node;
    		ImportMappingSet ims = ed.getImportMappingSet();
    		if (ims != null)
    		{
    			String importPath = ims.getMappingSetURI();
    			List<ParameterClassValue> params = ims.getParameterClassValues();
    			if (params.size() == 1)
    			{
    				String className = params.get(0).getQualifiedClassName();
    				String subset = params.get(0).getSubset();
        			createProc.as_FillEl(importPath, className, subset, "5");    				
    			}
    			else 
    			{
    				OK = false;
    				int nature = CompilationIssue.COMPILE_IMPORT_PARAMETER_CLASSES;
    				this.codeWarning(nature, fromRoot, "ImportMappingSet has " + params.size() + " parameter classes");
    			}
    		}
    	}
    	
    	return OK;
    }


//-------------------------------------------------------------------------------------------
//                         Recursive code generation for child elements
//-------------------------------------------------------------------------------------------


    /* Generate code in XML writing procedure cProc for elements with root path fromRoot
    and defined coding context, to add the element's child elements
    and call writing procedures for them.
    If revisit = true, create code in procedure rProc to do the same on revisit to the element.

    To understand the logic of this method and those it calls,
    see the table on page 35 of the development log for May 2002.

    Returns true if all child elements, for all when-values, have no code generation problems;
    but I do not think the return value is really used at all.
    (if false, the calling createProcedure returns false; but this method ignores the value returned
    by createProcedure).

    elementOK means 'a code step has been generated to write this element'.
    If elementOK = false,
    no procedure is generated for the element to give it attributes and child elements.
    */
    private boolean childElementCode(WProc cProc,WProc rProc, Xpth fromRoot, codingContext context)
    throws MapperException
    {
      int i,j;
      boolean elementOK = false;
      boolean allElementsOK = true;
      TreeElement localTree,child;
      codingContext cc;
      String tagName;
      Xpth newPath;
      USRepSet urs, uSubReps;
      objectMapping primary;

      // iterate over child element types
      localTree = outTree.fromRootPath(fromRoot,true);
      for (i = 0; i < localTree.childTreeElements().size(); i++)
      {
        child = localTree.childTreeElement(i);
        tagName = child.tagName();
        codeTrace("Generate code for child element '" + tagName + "'");
        newPath = fromRoot.addInnerStep(tagName);
        codeTrace("Full root path: " + newPath.stringForm());
        boolean opt = child.isOptional();
        boolean single = child.isUnique();
        context.setMultiplicity(new multiplicity(!opt,single));
        urs = new USRepSet(this,newPath,outTree,context);
        if (codeTracing) {urs.writeUniqueSubtree(mChan()); urs.writeMappings();urs.writeWhenVals(mChan());}

        // iterate over possible when-condition values new in the unique subtree
        for (j = 0; j < urs.whenCombinations(); j++)
        {
          codeTrace("When combination " + j);
          Vector<whenValue> whenComb = urs.whenCombination(j);
          // generate instructions to set all when-values in the context
          for (int w = 0; w < whenComb.size(); w++)
          {
              whenValue wv = (whenValue)whenComb.elementAt(w);
              cProc.as_setWVal(wv,"child");
              rProc.as_setWVal(wv,"child");
              codeTrace("Set when-value '" + wv.value() + "' at node " + wv.rootPath().stringForm());
          }
          // add the newly fixed when-values to those already fixed in the context
          cc = (codingContext)context.fixWhenValues(whenComb);
          cc.setRootPath(newPath);
          // find which mappings exist with a specific set of whenValues
          uSubReps = new USRepSet(this,newPath,outTree,cc);

          /* only if the node and its descendants carry some meaning,
          do we generate any code for it */
          boolean uniqueMeaning = !uSubReps.noMeaning();
          codeTrace("Mappings in unique subtree: " + uniqueMeaning);
          boolean subtreeMeaning = !uniqueMeaning && meaningInSubtree(newPath);
          codeTrace("Mappings in subtree: " + subtreeMeaning);
          if (uniqueMeaning|subtreeMeaning)
          {

              /* branches for different combinations of mappings;
              only one of these branches will execute.
              If either elementOK or cc.elementOK() comes back false, do not
              generate any procedure for the element. */

              // node and its unique subtree represent one or more objects
              if (uSubReps.hasObjects())
              {
                  /* The repSet may (rarely) have more than one independent (primary) object mapping.
                  Find them all, and for each one, form a repSet that has only mappings
                  dependent on the primary mapping. Generate code for each one. */
                  Vector<objectMapping> independentObjectMappings = uSubReps.getIndependentObjects();

                  /* make vector of independent object mappings in context and not in context,
                  and a Vector of independent object mappings not in context whose apex is this node */
                  Vector<objectMapping> independentInContext = new Vector<objectMapping>();
                  Vector<objectMapping> independentNotInContext = new Vector<objectMapping>();
                  Vector<objectMapping> independentNotInContextThisApex = new Vector<objectMapping>();
                  for (int prim = 0; prim < independentObjectMappings.size(); prim ++)
                  {
                      primary = (objectMapping) independentObjectMappings.elementAt(prim);
                      if (cc.hasObject(primary.cSet()))
                      {
                          independentInContext.addElement(primary);
                      }
                      else
                      {
                          independentNotInContext.addElement(primary);
                          if (newPath.equalPath(getApex(primary.cSet())))
                              independentNotInContextThisApex.addElement(primary);
                      }
                  }

                  /* Case 0:This is a special node defined in the steering file
                   as the top level of a recursively-nested XML; the steering file
                   defines what is the root object for this tree of related objects  */
                  if ((startObjectPath != null) &&
                     (startObjectPath.equals(newPath.stringForm())))
                 {
                     primary = (objectMapping)independentObjectMappings.elementAt(0);
                     int nMaps = independentObjectMappings.size();
                     if (nMaps != 1)
                         throw new MDLWriteException("Wrong number of independent mappings on start node: " + nMaps);
                     elementOK = doCase_0(cProc,uSubReps,tagName,primary);
                 }

                 /* Case 1:The unique subtree has some independent object mappings whose apex is on this node,
                  and each such primary object (there is probably only one) is already in the context object set
                   (for instance, we are in the unique subtree of some higher node)*/
                 else if (independentInContext.size() == 1)
                 {
                     primary = independentInContext.elementAt(0);
                     elementOK = doCase_1(cProc,rProc,cc,uSubReps,tagName,primary);
                 }

                 /* Case 2: One independent class not already in the subtree context,
                  (apex may be this node or a descendant) */
                 else if (independentNotInContext.size() == 1)
                 {
                     primary = independentNotInContext.elementAt(0);
                     elementOK = doCase_2(cProc, rProc, cc, uSubReps, tagName,primary);
                 }

                 /* Case 4: The unique subtree represents more than one independent class
                  not in the context */
                 else if (independentNotInContext.size() > 1)
                 {
                     /* no independent objects have apex on this node; expect them all to be dependent
                      on some higher node, i.e expect this node to be to be 1..1 */
                     if (independentNotInContextThisApex.size() == 0)
                     {
                         codeTrace("Case 4.1");
                         if (opt|!single)
                         {
                            String problem = ("Case 4.1 at path whose innermost node is has optional = " + opt + ", single = " + single);
                         	int nature = CompilationIssue.COMPILE_INDEPENDENT_OBJECTS;
                         	codeWarning(nature,newPath,problem);                        	 
                         }
                         elementOK = cProc.as_AddEl(tagName,false,"4.1");
                     }
                     /* one independent object has its apex on this node; a bit unexpected, because
                     why are the other objects not all dependent on it ?*/
                     else if (independentNotInContextThisApex.size() == 1)
                     {
                         codeTrace("Case 4.2 , converted to a case 2");
                         String problem = ("Case 4.2 treated as case 2");
                      	 int nature = CompilationIssue.COMPILE_INDEPENDENT_OBJECTS;
                      	 codeWarning(nature,newPath,problem);                        	 
                         primary = independentNotInContextThisApex.elementAt(0);
                         elementOK = doCase_2(cProc, rProc, cc, uSubReps, tagName,primary);
                     }
                     else if (independentNotInContextThisApex.size() > 1)
                     {
                         codeTrace("Case 4.3");
                         String problem = ("Case 4.3; more than one association which might determine the multiplicity of the node");
                      	 int nature = CompilationIssue.COMPILE_INDEPENDENT_OBJECTS;
                      	 codeWarning(nature,newPath,problem);                        	 
                     }
                 }

                 else
                 {
                	 String problem = ("Case not recognised as case 0, 1, 2 or 4");
                  	 int nature = CompilationIssue.COMPILE_NOT_RECOGNISED;
                 	 codeWarning(nature,newPath,problem);                        	 
                 }

             } // end of 'if (uSubReps.hasObjects())' section

              // node and its unique subtree have no object mappings
              else if (!uSubReps.hasObjects())
                {elementOK = doCase_3(cProc, rProc, cc, uSubReps, tagName);}

              /* code for one element type and one set of when-values
              has been successfully generated in this procedure.
              Generate a procedure for the new element.  */
              if (elementOK)
                  {createProcedure(uSubReps.rootPath(),cc);}
              else {codeTrace("Procedure not generated for element with path '"
                  + uSubReps.rootPath().stringForm() + "'");}

              allElementsOK = allElementsOK & elementOK;
          } // end of 'if the node and its subtree carry some meaning' section
        }// end of loop over when-condition values new in the unique subtree
      }// end of loop over child element types
      return allElementsOK;
    }

    // key = string form of XPath; value = "1" if there are mappings in the subtree
    private Hashtable<String, String> hasMeaningInSubtree;

    // test if there any mappings in the subtree of this node.
    // FIXME for indefinite paths
    private boolean meaningInSubtree(Xpth xp) throws MDLReadException
    {
        if (xp == null)
            {throw new MDLReadException("Tried to find meaning beneath a node with null XPath.");}
        return (hasMeaningInSubtree.get(xp.stringForm()) != null);
    }

    // mark any subpaths of this path as having mappings in their subtree
    private void markSubPaths(Xpth xp)
    {
        String path = xp.stringForm();
        StringTokenizer st = new StringTokenizer(path,"/");
        String sub = "";
        while (st.hasMoreTokens())
        {
            sub = sub + "/" + st.nextToken();
            hasMeaningInSubtree.put(sub,"1");
        }

    }

    // mark subpaths as having mappings in their subtree, for a Vector of paths.
    private void markAllSubPaths(Vector<Xpth> paths)
    {
        for (int p = 0; p < paths.size(); p++)
        {
            Xpth pp = (Xpth)paths.elementAt(p);
            markSubPaths(pp);
        }
    }


    /* Setup for fast test of meaning in this subtree;
    nothing else in needed if the MDL has no mappings with indefinite paths.
    FIXME - indefinite paths
    FIXME - are link condition paths relative? */
    private void setMeaningInSubtree() throws MapperException
    {
        int i;
        hasMeaningInSubtree = new Hashtable<String, String>();

        for (Enumeration<Vector<objectMapping>> e = objectMappingsByClassName.elements(); e.hasMoreElements();)
        {
        	Vector<objectMapping> v = (Vector<objectMapping>)e.nextElement();
           for (i = 0; i < v.size(); i++)
           {
              objectMapping om = v.elementAt(i);
              if (om.nodePath().definite())
              {
                  markSubPaths(om.nodePath());
                  markAllSubPaths(om.whenConditionPaths());
              }
           }
        }

        for (Enumeration<Vector<propertyMapping>> e = propertyMappingsByClassName.elements(); e.hasMoreElements();)
        {
        	Vector<propertyMapping> v = (Vector<propertyMapping>)e.nextElement();
           for (i = 0; i < v.size(); i++)
           {
              propertyMapping pm = v.elementAt(i);
              if (pm.nodePath().definite())
              {
                  markSubPaths(pm.nodePath());
                  markAllSubPaths(pm.whenConditionPaths());
                  markAllSubPaths(pm.linkConditionPaths());
              }
           }
        }

        for (Enumeration<Vector<AssociationMapping>> e = associationMappingsByName.elements(); e.hasMoreElements();)
        {
        	Vector<AssociationMapping> v = (Vector<AssociationMapping>)e.nextElement();
           for (i = 0; i < v.size(); i++)
           {
              AssociationMapping am = v.elementAt(i);
              if (am.nodePath().definite())
              {
                  markSubPaths(am.nodePath());
                  markAllSubPaths(am.whenConditionPaths());
                  markAllSubPaths(am.linkConditionPaths());
              }
           }
        }
    }


    /* Case 0 - special path defined in the steering file */
    private boolean doCase_0(WProc cProc, USRepSet uSubReps, String tagName, objectMapping primary) throws MDLWriteException
    {
        boolean elementOK = false;
        codeTrace("Case 0");
        if (startObjectClass.equals(primary.className()))
        {
            elementOK = cProc.as_allObj(primary.cSet(),tagName,true,"0");
        }
        else
        {
             throw new MDLWriteException("Class in steering file '" + startObjectClass + "' does not match class '"
              + primary.className() + "' represented on node '" + startObjectPath + "'");
        }
        return elementOK;
  }


    /* Case 1 - the element represents an object of a class which is already in the subtree context. */
    private boolean doCase_1(WProc cProc, WProc rProc, codingContext cc,
              USRepSet uSubReps, String tagName, objectMapping primary)
        throws MapperException
    {
        boolean elementOK = false;
        codeTrace("Case 1");
        AssociationMapping sam = uSubReps.selfAssociation(primary.cSet());
        if (sam == null) // the primary object is not involved in any self-association
        {
              // maxOccurs = 1 so the element only needs to be created once
              if (cc.mult().maxIs1())
              {
                  codeTrace("Case 1.1");
                  elementOK = cProc.as_AddEl(tagName,false,"1.1");
              }

              // maxOccurs = N so the element needs to be created several times, by the revisit procedure
              else  // case 1.2, on creation or revisit to the element
              {
                  codeTrace("Case 1.2");
                  elementOK = cProc.as_AddEl(tagName,false,"1.2");
                  rProc.as_AddEl(tagName,false,"1.2");
              }
          }
          else if (sam != null) // case 1.3
          {
            codeTrace("Case 1.3");
            int selfEnd = 0;
            /* find which end of the self-association corresponds to the lower(inner) node;
             the other end must have maxCardinality = 1 */
            if (sam.simpleNesting()) {selfEnd = sam.identityEnd() + 1;} // in 1,2 convention
            else 
            {
            	int nature = CompilationIssue.COMPILE_SELF_ASSOCIATION;
            	codeError(nature,cc.rootPath(),"Self-association '" + sam.assocName() 
            			+ "' is not a nested association.");
            }
            elementOK = cProc.as_assInst(primary.cSet(), sam.assocName(), primary.cSet(),
                selfEnd, new multiplicity(false,false),tagName,"1.3");
          }
        return elementOK;
    }

   /* Case 2 - element represents one or more objects which are not already in the subtree context. */
   private boolean doCase_2(WProc cProc,WProc rProc,codingContext cc,
                USRepSet uSubReps,String tagName, objectMapping primary)
   throws MapperException
    {
        boolean elementOK = false;
        codeTrace("Case 2");
        /* find all represented associations from objects already in context to the primary object,
        choosing the mapping to the top node if there is more than one mapping of any association*/
        Vector<AssociationMapping> primaryAssocs = uSubReps.bestAssocsTo(primary.cSet());
        Vector<AssociationMapping> primaryToContext = cc.vetAssocs(primaryAssocs);
        int pAssocs = primaryToContext.size();

        // the primary object has one or more associations to objects in the subtree context
        if (pAssocs > 0)
        {
        		  String caseString = "2.1";
        		  if (pAssocs > 1) caseString = "2.3";
        		  codeTrace("Case " + caseString);
        		  
                  /* check that the primary object is dependent on all the associations, i.e 
                   * will have implied inclusion filters on all associations */
        		  checkIsDependent(primaryToContext,primary);
        		  /* choose the best association to use to get the objects; inclusion
        		   * filters will look after the rest.  */
        		  AssociationMapping am = chooseBestMapping(cc, primaryToContext);

        		  int thisEnd = cc.endNotInContext(am);
                  // check the tree of linking associations of dependent objects, and add them to the context
                  if (addApexObjectsToContext(cc,uSubReps))
                  {
                      elementOK = cProc.as_assInst(am.assocEnd(0).cSet(),am.assocName(),am.assocEnd(1).cSet(),
                          thisEnd,new multiplicity(false,false),tagName,caseString);
                  }
        }

        // primary object has no associations to any object in the subtree context
        else if (pAssocs == 0)
        {
            codeTrace("Case 2.2");
            // check the tree of linking associations of dependent objects, and add them to the context
            if (addApexObjectsToContext(cc,uSubReps))
            {
                elementOK = cProc.as_allObj(primary.cSet(),tagName,false,"2.2");
            }
        }

        return elementOK;
    }
   
   /**
    * Check that the primary object is dependent on all the associations
    * it has to other objects which are in the context. Write a warning
    * when it is not.
    * @param primaryToContext Vector of association mappings
    * @param primary object mapping for the primary object
    * @throws MapperException
    */
   private void checkIsDependent(Vector<AssociationMapping> primaryToContext,
		   objectMapping primary) throws MapperException
   {
	   for (Iterator<AssociationMapping> it = primaryToContext.iterator();it.hasNext();)
	   {
		   AssociationMapping am = it.next();
		   for (int end = 0; end < 2; end++)
		   {
			   associationEndMapping aem = am.assocEnd(end);
			   if ((aem.cSet().equals(primary.cSet())) && (!aem.required()))
			   {
				   String problem = ("Association " + aem.roleName() + 
						   " should be required for class " + primary.className());				   
		        	int nature = CompilationIssue.COMPILE_REQUIRED_ASSOCIATION;
		        	codeWarning(nature,am.nodePath(),problem);
			   }
		   }
	   }
	   
   }
   
   /**
    * choose the association mapping whose object in context
    * is represented by the lowest node (with the longest path)
    * @param cc
    * @param primaryToContext Vector of candidate associations
    * @return the best association mapping
    */
   private AssociationMapping chooseBestMapping
   				(codingContext cc,
   				Vector<AssociationMapping> primaryToContext) throws MapperException
   {
	   AssociationMapping best = primaryToContext.get(0);
	   for (Iterator<AssociationMapping> it = primaryToContext.iterator();it.hasNext();)
	   {
		   AssociationMapping am = it.next();
		   if (objMappingPathLength(cc, am) > objMappingPathLength(cc, best)) best = am;
	   }
	   return best;
   }
   
   /**
    * @param cc coding context
    * @param am an association mapping; one end object is in context
    * @return the length  of the path to the end of an association that is in context
    * @throws MapperException
    */
   private int objMappingPathLength(codingContext cc, AssociationMapping am) throws MapperException
   {
	   int len = 0;
       int thisEnd = cc.endNotInContext(am); // 1 or 2
       if ((thisEnd > 0) && (thisEnd < 3))
       {
    	   ClassSet cs = am.assocEnd(2 - thisEnd).cSet(); 
    	   objectMapping om = namedObjectMapping(cs);
    	   len = om.nodePath().size();    	   
       }
	   return len;
   }

/* case 3 - node and its unique subtree have no object mappings */
private boolean doCase_3(WProc cProc, WProc rProc, codingContext cc, USRepSet uSubReps, String tagName)
throws MapperException
{
    boolean elementOK = false;
    codeTrace("Case 3");
    int assocs = uSubReps.assocMaps().size();

    // more than one association mapping; not allowed
    if (assocs > 1)
    {
    	int nature = CompilationIssue.COMPILE_ASSOCIATION_MAPPINGS;
    	String problem = "When there are no object mappings, there cannot be more than one association mapping on the node";
        codeWarning(nature,uSubReps.rootPath(),problem);
    }

    // one association mapping
    else if (assocs == 1)
    {
        AssociationMapping am = uSubReps.getAssociationRep(0);
        ClassSet c1 = am.assocEnd(0).cSet();
        ClassSet c2 = am.assocEnd(1).cSet();
        boolean has1 = cc.hasEndInContext(am.nodePath(),am.assocEnd(0));
        boolean has2 = cc.hasEndInContext(am.nodePath(),am.assocEnd(1));

        // objects at both ends are in the subtree context
        if ((has1) && (has2))
        {
            codeTrace("Case 3.2");
            elementOK = cProc.as_assInst(c1,am.assocName(),c2,0,new multiplicity(false,false),tagName,"3.2");
        } // end of case 3.2

        // an object at just one end is in the subtree context
        else if ((has1 & !has2)|(has2 & !has1))
        {
            codeTrace("Case 3.3");
            elementOK = doCase_3_3(cProc, rProc, cc, uSubReps, tagName);
        }// end of case 3.3

        // objects at neither end are in the subtree context
        else if (!has1 & !has2)
        {
            codeTrace("Case 3.4");
            elementOK = doCase_3_4(cProc, rProc, cc, uSubReps, tagName);
        }// end of case 3.4

    }// end of one association case

    // no associations
    else if (assocs == 0)
    {
        codeTrace("Case 3.5");
        elementOK = doCase_3_5(cProc, rProc, cc, uSubReps, tagName);
    }
    return elementOK;
}


   /* Case 3.3 - element represents no objects and an association between two objects,
   one of which is in the subtree context */
    private boolean doCase_3_3(WProc cProc, WProc rProc, codingContext cc, USRepSet uSubReps, String tagName) 
    throws MapperException
    {
          boolean elementOK = false;
          AssociationMapping am = uSubReps.getAssociationRep(0);
          ClassSet c1 = am.assocEnd(0).cSet();
          ClassSet c2 = am.assocEnd(1).cSet();
          boolean has1 = cc.hasEndInContext(am.nodePath(),am.assocEnd(0));
          boolean has2 = cc.hasEndInContext(am.nodePath(),am.assocEnd(1));
          int thisEnd = 0;
          // thisEnd is the end of the object not already in the subtree context
          if (has1) {thisEnd = 2;} else if (has2) {thisEnd = 1; }
          // number of link conditions in the unique subtree which are concatenations of multiple keys or properties
          int multiInstanceLinks = am.assocEnd(thisEnd - 1).multiInstanceLinks();

          // this node represents one instance of the association
          if (multiInstanceLinks == 0)
          {
              codeTrace("Case 3.3.1");
              elementOK = cProc.as_assInst(c1,am.assocName(),c2,thisEnd,
                  new multiplicity(false,false),tagName,"3.3.1");
              cc.addUniqueCSet(am.assocEnd(thisEnd-1).cSet());
          } //end of case 3.3.1

          /* this node represents multiple instances of the association,
          by concatenation in the link value. */
          else if (multiInstanceLinks == 1) // case 3.3.2
          {
              codeTrace("Case 3.3.2");
              /* case D.7 is what you are going to do to fill the text of the element,
              if it is created. D.7 generates a relProps step to navigate the association and
              concatenate keys or property values for all objects found.
              Here we just generate a relProps to concatenate keys (last arg empty).
              If any objects are found, the current string will be non-empty,
              so the next conditional add element step will fire. */
              Vector<MappingTwo> pMaps = new Vector<MappingTwo>();
              linkCondition lc = am.linkCondition(0); // arbitrary choice - trouble?
              doCase_D_6_and_7(cProc,lc, am, cc, pMaps,"3.3.2");
              elementOK = cProc.as_AddEl(tagName,true,"3.3.2");
          } //end of case 3.3.2

          // several link conditions are concatenations
          else if (multiInstanceLinks > 1)
          {
          	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
        	String problem = ("Case 3.3.3 (several cross conditions are concatenations) is not allowed.");
            codeWarning(nature,uSubReps.rootPath(),problem);
          }
          return elementOK;
    }

/* Case 3.4 - element represents no objects and an association between two objects,
neither of which is in the subtree context */
private boolean doCase_3_4(WProc cProc, WProc rProc, codingContext cc, USRepSet uSubReps, String tagName)
throws MapperException
 {
    boolean elementOK = false;
    AssociationMapping am = uSubReps.getAssociationRep(0);
    ClassSet c1 = am.assocEnd(0).cSet();
    ClassSet c2 = am.assocEnd(1).cSet();


    // number of link conditions in the unique subtree which are concatenations of multiple keys or properties
    int multiInstanceLinks1 = am.assocEnd(0).multiInstanceLinks();
    int multiInstanceLinks2 = am.assocEnd(1).multiInstanceLinks();
    codeTrace("Multilinks: " + multiInstanceLinks1 + " " + multiInstanceLinks2);

    // this node represents one instance of the association
    if ((multiInstanceLinks1 == 0) && (multiInstanceLinks2 == 0))
    {
        codeTrace("Case 3.4.1");
        // '3' because neither end object is yet in the context
        elementOK = cProc.as_assInst(c1,am.assocName(),c2,3,new multiplicity(false,false),tagName,"3.4.1");
        cc.addUniqueCSet(c1);
        cc.addUniqueCSet(c2);
    } //end of case 3.4.1

    /* this node represents several instances of the association,
    by concatenation in one link condition value.  */
    else if (((multiInstanceLinks1 + multiInstanceLinks2) == 1)) // case 3.4.2
    {
        codeTrace("Case 3.4.2");
        int groupedEnd = 0;
        /* the grouping object is the one whose link condition does not
        consist of a set of concatenated keys or property values */
        if (multiInstanceLinks1 == 1) {groupedEnd =2;} else {groupedEnd = 1;}
        codeTrace("Grouped end: " + groupedEnd);
        cProc.as_grProp(am.assocEnd(groupedEnd-1).cSet(),null,"3.4.2");
        elementOK  = cProc.as_assInst(c1,am.assocName(),c2,3,
            new multiplicity(false,false),tagName,"3.4.2");
        cProc.as_unPrime("3.4.2");
        /* put only the grouping object in the subtree context. Cases D7 and D8 will
        then fill in link condition values appropriately
        (on create of the grouping element; revisit does nothing)*/
        cc.addUniqueCSet(am.assocEnd(groupedEnd-1).cSet());
    } //end of case 3.4.2

    // several link conditions are concatenations
    else if (((multiInstanceLinks1 + multiInstanceLinks2) > 1)) //
    {
      	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
    	String problem = ("Case 3.4.3 (several cross conditions are concatenations) is not allowed.");
        codeWarning(nature,uSubReps.rootPath(),problem);
    }
    return elementOK;
}

/* case 3.5 - element represents no objects or associations. */
 private boolean doCase_3_5(WProc cProc, WProc rProc, codingContext cc, USRepSet uSubReps, String tagName)
     throws MapperException
 {
      boolean elementOK = false;

      // there are some property mappings
      int nProps = uSubReps.propertyMaps().size();
      if (nProps > 0)
      {
          boolean someInContext = false;
          boolean someNotInContext = false;
          for (int p = 0; p < nProps; p++)
          {
              propertyMapping pm = uSubReps.getPropertyRep(p);
              if (cc.hasObject(pm.cSet()))
                  {someInContext = true;}
              else {someNotInContext = true;}
          }
          boolean allInContext = !someNotInContext;

          //  one property of an object in context - node must occur
          if ((nProps == 1) && (allInContext) && (cc.mult().minIs1()))
          {
              codeTrace("Case 3.5.1");
              elementOK = cProc.as_AddEl(tagName,false,"3.5.1");
          }// end of case 3.5.1

          /* one property of an object in context; node only occurs if the property
          has a value */
          else if ((nProps == 1) && (allInContext) && (!cc.mult().minIs1()))
          {
             codeTrace("Case 3.5.2");
              propertyMapping pm = uSubReps.getPropertyRep(0);
              cProc.as_getProp(pm.cSet(),pm.propertyName(),new multiplicity(false,true),"3.5.2");
              elementOK = cProc.as_AddEl(tagName,true,"3.5.2"); // add element only if the property is present
          }// end of case 3.5.2

          /* more than one property of objects in context; assume the node always
          occurs, even if some or all of the properties are missing. */
          else if ((nProps > 1) && (allInContext))
          {
              codeTrace("Case 3.5.3");
              elementOK = cProc.as_AddEl(tagName,false,"3.5.3");
          }// end of case 3.5.3

          /* all properties are properties of objects not in context;
          these properties will be used to group other objects lower in the subtree */
          else if (!someInContext)
          {
              codeTrace("Case 3.5.4");
              elementOK = doCase_3_5_4(cProc, rProc, cc, uSubReps, tagName);
          }

          // some properties of objects in context, some properties of objects not in context
          else if ((someInContext) && (someNotInContext))
          {
              codeTrace("Case 3.5.5");
              // as for 3.5.1, add a node, and work out in text case A what to fill it with
              elementOK = cProc.as_AddEl(tagName,false,"3.5.5");
          }// end of case 3.5.5

      } // end of case where there are some property mappings

      else if (nProps == 0)
      {
          codeTrace("Case 3.5.6");
          elementOK = doCase_3_5_6(cProc, rProc, cc, uSubReps, tagName);
      }

      return elementOK;
  }

/* Element represents no objects or associations, but does represent some properties.
All properties are properties of objects not in context;
these properties will be used to group other objects lower in the subtree */
private boolean doCase_3_5_4(WProc cProc, WProc rProc, codingContext cc, USRepSet uSubReps, String tagName)
     throws MapperException
{
    boolean elementOK = false;
    int nProps = uSubReps.propertyMaps().size();

    // find the first repeated object to be grouped under this node
    objectMapping om = firstRepeatedObject(uSubReps.nodePaths(),cc);

    if (om == null) // firstRepeatedObject wrote an error message; add the property names
    {
        String gProps = "";
        for (int p = 0; p < nProps; p++)
        {
            propertyMapping pm = uSubReps.getPropertyRep(p);
            gProps = gProps + " " + pm.fullName();
        }
    }
    if (om != null)
    {
        /* All grouping properties should be properties of this object,
        or of objects reachable from it by M:1 associations */
        for (int p = 0; p < nProps; p++)
        {
            propertyMapping pm = uSubReps.getPropertyRep(p);
            if (uniquelyLinked(pm.cSet(),om.cSet(), uSubReps,true))
            {
                cProc.as_grProp(pm.cSet(),pm.propertyName(),"3.5.4");
            }
        }// end of loop over grouping properties

        // does the grouped object have any nesting association to an object in the subtree context?
        AssociationMapping gram = assocToContext(om,cc);
        if (gram == null) // no nesting association
        {
            elementOK = cProc.as_allObj(om.cSet(),tagName,false,"3.5.4");
        }
        else if (gram != null) // a nesting association
        {
            int thisEnd = 0;
            if (gram.assocEnd(0).cSet().equals(om.cSet())) {thisEnd = 1;}
            else if (gram.assocEnd(1).cSet().equals(om.cSet())) {thisEnd = 2;}
            else
            {
              	int nature = CompilationIssue.COMPILE_REQUIRED_ASSOCIATION;
            	String problem = ("Error finding association to objects in context");
                codeWarning(nature,uSubReps.rootPath(),problem);            	
            }
            elementOK = cProc.as_assInst(gram.assocEnd(0).cSet(),gram.assocName(),
                gram.assocEnd(1).cSet(),thisEnd,new multiplicity(false,false),tagName,"3.5.4");
        }
        cProc.as_unPrime("3.5.4"); // remove any grouping
        cc.addUniqueCSet(om.cSet());

      } // end of (om !=null) case
      return elementOK;
  }


/* Case 3.5.6 - element represents no objects, associations or properties -
only link-and when-condition values    */
 private boolean doCase_3_5_6(WProc cProc, WProc rProc, codingContext cc, USRepSet uSubReps, String tagName)
     throws MapperException
 {
      boolean elementOK = false;
      boolean caseFound = false;
      int nLinkVals = uSubReps.linkMaps().size();
      int nWhenVals = uSubReps.whenMaps().size();

      if ((nLinkVals == 0) && (nWhenVals == 0))
      {
          /* This node and its unique subtree represent nothing.
          Strictly, only if it has minOccurs = maxOccurs = 1 should you add it,
          and it may have repeated meaning-carrying nodes below it.
          However, we also allow the case minOccurs = 0; add an optional node
          even when we are not sure that the meaning below it will be populated. */
          // if (cc.mult().maxIs1()) // constraint removed 7/9/06, tried reinstating 19/8/09; no good
          {
              codeTrace("Case 3.5.6.1");
              elementOK = cProc.as_AddEl(tagName,false,"3.5.6.1");
          }
          /* else
          {
              codeTrace("Case 3.5.6.0: " + cc.mult().stringForm());
          } */
          caseFound = true;
      }

      else if ((nLinkVals == 0) && (nWhenVals > 0))
      {
          codeTrace("Case 3.5.6.2");
          elementOK = cProc.as_AddEl(tagName,false,"3.5.6.2");
          caseFound = true;
      }

      else if ((nLinkVals > 0) && (nWhenVals > 0))
      {
        	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
        	String problem = ("Case 3.5.6.3 (some when-conditions and some link conditions) is not allowed.");
            codeWarning(nature,uSubReps.rootPath(),problem);            	
          caseFound = true;
      }

      // objects grouped by their association to an object not in the subtree context
      else if ((nLinkVals == 1) && (nWhenVals == 0))
      {
          linkCondition lc = uSubReps.linkCondition(0);
          MappingTwo m = (MappingTwo)uSubReps.linkMaps().elementAt(0);
          /* link value must be an LHS (association node end) value for an
          association (not property) link condition */
          if (lc == null)
        	  codeWarning(CompilationIssue.COMPILE_CROSS_CONDITION,uSubReps.rootPath(),"missing link condition");
          else if (lc.rootToLeftValue() == null)
        	  codeWarning(CompilationIssue.COMPILE_CROSS_CONDITION,uSubReps.rootPath(),"null root path in link condition");
          else if ((m instanceof AssociationMapping) && (lc.rootToLeftValue().oneOf(uSubReps.nodePaths())))
          {
              AssociationMapping am = (AssociationMapping)m;
              ClassSet cs1 = am.assocEnd(0).cSet();
              ClassSet cs2 = am.assocEnd(1).cSet();
              // both end objects must be not in the subtree context (see argument '3' in as_assInst below)
              if ((!cc.hasEndInContext(am.nodePath(),am.assocEnd(0)))
                  && (!cc.hasEndInContext(am.nodePath(),am.assocEnd(1))))
              {
                  codeTrace("Case 3.5.6.4");
                  Vector<MappingTwo> pMaps = objectEndLinkProperties(lc);
                  String pName = null;
                  if (pMaps.size() > 0) {pName = ((propertyMapping)pMaps.elementAt(0)).propertyName();}
                  // group by the class in the link condition, or by the property used at the other end of the link
                  cProc.as_grProp(lc.objectCSet(),pName,"3.5.6.4");
                  elementOK = cProc.as_assInst(cs1,am.assocName(),cs2,3,
                      new multiplicity(false,false),tagName,"3.5.6.4");
                  //  add both objects to code generation context
                  cc.addUniqueCSet(cs1);
                  cc.addUniqueCSet(cs2);
                  caseFound = true;
              }
          }
      }

      if ((nLinkVals > 0) && (nWhenVals == 0) && (!caseFound))
      {
          codeTrace("Case 3.5.6.5");
          elementOK = cProc.as_AddEl(tagName,false,"3.5.6.5");
          caseFound = true;
      }

      if (!caseFound)
      {
      	int nature = CompilationIssue.COMPILE_CONDITION_VALUE_TYPE;
          codeWarning(nature,uSubReps.rootPath(),"could not identify type of condition value");
      }
      return elementOK;
  }


//-------------------------------------------------------------------------------------------
//              supporting methods for child element code generation
//-------------------------------------------------------------------------------------------



    /* find an object represented on a repeated node immediately below
    one of the nodes in the set of nodes nodePaths,
    which are the nodes in a unique subtree.
    There should be exactly one of these objects
    Writes a warning and returns null if there is a problem.*/
    private objectMapping firstRepeatedObject(Vector<Xpth> nodePaths, codingContext cc)
    throws MapperException
    {
        objectMapping res = null;
        int found = 0;
        // loop over elements in the unique subtree
        for (int i = 0; i < nodePaths.size(); i++)
        {
            Xpth nodePath = (Xpth)nodePaths.elementAt(i);
            if (!nodePath.isAttributePath())
            {
              TreeElement te = outTree.fromRootPath(nodePath,true);
              // loop over the child elements of an element in the unique subtree
              for (int j = 0; j < te.childTreeElements().size(); j++)
              {
                TreeElement tf = te.childTreeElement(j);
                // pick out repeating child elements
                if (!tf.isUnique())
                {
                    Xpth fullPath = nodePath.addInnerStep(tf.tagName());
                    // find all object mappings on the node, applicable in the context
                    nodeRepSet nr = new nodeRepSet(this,fullPath,cc);
                    for (int k = 0; k < nr.objectMaps().size(); k++)
                    {
                        res = nr.getObjectRep(k);
                        found++;
                    }
                }
              }
            }
        }
        // in all these loops, you should find exactly one eligible mapping
        if (found == 0)
        {
        	int nature = CompilationIssue.COMPILE_GROUPING;
        	codeWarning(nature,cc.rootPath(),"Node represents grouping properties, but no object found to own those properties");
        	res = null;
        }
        if (found > 1)
        {
        	int nature = CompilationIssue.COMPILE_GROUPING;
        	codeWarning(nature,cc.rootPath(),"Node represents grouping properties, but more than one repeating object class found under grouping node.");
        	res = null;
        }
        return res;
    }

    /* true if an object of class depObj is uniquely determined by an object
    of class refObj - that is, if they are the same class, or if
    there is an M:1 association from refObj to depObj represented in
     the output XML, or a chain of such associations.
     DOES NOT CHECK M:1 CARDINALITY */
    private boolean uniquelyLinked(ClassSet depObj, ClassSet refObj, USRepSet uSubReps, boolean writeMessage) throws MapperException
    {
        boolean res = false;
        if (depObj.equals(refObj)) {res = true;}
        else
        {
            for (int i= 0; i < associationMappings().size(); i++)
            {
                AssociationMapping am = (AssociationMapping)associationMappings().elementAt(i);
                if ((am.assocEnd(0).cSet().equals(depObj)) &&
                    (uniquelyLinked(am.assocEnd(1).cSet(),refObj, uSubReps,false))) res = true;
                if ((am.assocEnd(1).cSet().equals(depObj)) &&
                    (uniquelyLinked(am.assocEnd(0).cSet(),refObj, uSubReps, false))) res = true;
            }
        }
        if ((!res) & (writeMessage))
        {
        	String problem = ("A grouping property  belonging to class "
              + depObj.stringForm() + " is not uniquely determined by the grouped object"
              + " of class " + refObj.stringForm());
        	int nature = CompilationIssue.COMPILE_GROUPING;
        	codeWarning(nature,uSubReps.rootPath(),problem);
        }
        return res;
    }

    /* find a nesting association from object om to an object in the subtree context.*/
    private AssociationMapping assocToContext(objectMapping om, codingContext cc)
    throws MapperException
    {
        AssociationMapping gram = null;
        repSet gRep = new USRepSet(this,om.nodePath(),outTree,cc);
        for (int g = 0; g < gRep.assocMaps().size(); g++)
        {
            AssociationMapping gt = gRep.getAssociationRep(g);
            if ((gt.assocEnd(0).cSet().equals(om.cSet())) &&
                (cc.hasEndInContext(gt.nodePath(),gt.assocEnd(1)))) gram = gt;
            if ((gt.assocEnd(1).cSet().equals(om.cSet())) &&
                (cc.hasEndInContext(gt.nodePath(),gt.assocEnd(0)))) gram = gt;
        }
        return gram;
    }


//-------------------------------------------------------------------------------------------
//                  Code to add dependent objects to the runtime context
//-------------------------------------------------------------------------------------------

    /* Generate code in procedure proc to add the objects directly dependent on the object of
    (class,subset) cSet to the runtime set of context objects, and recursively add
    code to add the objects depending on them. */
    private boolean addToContextCode(WProc cProc, WProc rProc, Xpth fromRoot, codingContext cc)
    throws MapperException
    {
        boolean elementOK = true;
        USRepSet urs = new USRepSet(this,fromRoot,outTree,cc);
        Vector<objectMapping> independentObjectMaps = urs.getIndependentObjects();
        for (int i = 0; i < independentObjectMaps.size(); i++)
        {
            objectMapping primary = (objectMapping)independentObjectMaps.elementAt(i);
            Vector<String> done = new Vector<String>();
            done.addElement(primary.cSet().stringForm());
            addDependentsOn(cc,cProc,primary.cSet(),urs, done);
        }
        return elementOK;
    }

    /* Generate code in procedure proc to add the objects directly dependent on the object of
    (class,subset) cSet to the runtime set of context objects, and recursively add
    code to add the objects depending on them.
    Only add a class (and any of those dependent on it) if the current node is 
    somewhere on or below (change made 19/8/09) the apex node for all its mappings.
    done is the Vector of classSets done already. */
    private void addDependentsOn(codingContext cc, WProc proc, ClassSet cSet , USRepSet urs, Vector<String> done)
    throws MapperException
    {
        // loop over all associations involving this object
        Vector<AssociationMapping> assocs = urs.bestAssocsTo(cSet);
        // codeTrace("Adding dependents on " + cSet.stringForm() + "; " + assocs.size() + " associations");
        for (int i = 0; i < assocs.size();i++)
        {
            AssociationMapping am = (AssociationMapping)assocs.elementAt(i);
            // codeTrace("Trying association " + am.fullName());
            for (int depEnd = 0; depEnd < 2; depEnd++)
            {
                int thisEnd = 1 - depEnd;
                ClassSet depCSet = am.assocEnd(depEnd).cSet();
                if ((am.assocEnd(depEnd).required()) &&  // the association is required for the dependent end
                    (!GenUtil.inVector(depCSet.stringForm(),done)) && // the dependent end has not been added already
                    (cSet.equals(am.assocEnd(thisEnd).cSet())) && // it has the right (class,subset) at this end
                    (urs.namedObjectRep(depCSet) != null) && // the other-end class is represented in this unique subtree
                    (cc.rootPath().containsPath(getApex(depCSet)))) // the apex for all mappings to the other class is the current node or above
                {
                    proc.as_addToContext(am.assocEnd(0).cSet(),am.assocName(),
                      am.assocEnd(1).cSet(),depEnd + 1,"");
                    codeTrace("Adding dependent object " + depCSet.stringForm() + " via association " + am.fullName());
                    Vector<String> nowDone = GenUtil.vStringCopy(done);
                    nowDone.addElement(depCSet.stringForm());
                    addDependentsOn(cc,proc,depCSet,urs,nowDone);
                }

            }
        }
    }


    /* add to the coding context stack all represented objects represented in the unique
     subtree whose apex node is the current node or above it;
     (apex = the deepest node above all mappings to the ClassSet)
    return false of there is a problem. */
    private boolean addApexObjectsToContext(codingContext cc, USRepSet uSubReps) throws MapperException
    {
        boolean res = false;
        // check the linking associations of dependent objects
        if (checkDependentObjects(uSubReps))
        {
            res = true;
            String added = "classes added to context: ";
            String notAdded = "classes not added to context: ";
            /* add represented objects to the coding context stack, if their apex node is the current node
            (or higher than it, because of distant associations)*/
            for (int i = 0; i < uSubReps.allObjectReps().size(); i++)
            {
                objectMapping om = (objectMapping)uSubReps.allObjectReps().elementAt(i);
                if (cc.rootPath().containsPath(getApex(om.cSet())))
                {
                    cc.addUniqueCSet(om.cSet());
                    added = added + om.className() + " ";
                }
                else {notAdded = notAdded + om.className() + " ";}
            }
            codeTrace(added);
            codeTrace(notAdded);
        }
        else
        {
        	String problem = "Failure of dependent objects check";
        	codeTrace(problem); 
         	int nature = CompilationIssue.COMPILE_DEPENDENT_OBJECT;
            codeWarning(nature,cc.rootPath(),problem);
        }
        return res;
    }

    /* For each object represented in a unique subtree, except the non-dependent (primary) object,
    check that the object has one linking association, that it is dependent on that association.
    Checks involving the object source have now been postponed to code filtering time.
    This would still allow two classes to depend on one another in a circular manner.
    */
    private boolean checkDependentObjects(USRepSet uSubReps) throws MapperException
    {
        Hashtable<String, ClassSet> dependsOn = new Hashtable<String, ClassSet>();
        if (codeTracing) uSubReps.writeMappings();
        objectMapping primary = uSubReps.primaryObject();
        boolean res = (primary != null);
        Vector<MappingTwo> allObjects = uSubReps.allObjectReps(); // non-dependent object is first of this Vector
        // loop over each dependent object
        if (res && (allObjects.size() > 1)) for (int i = 1; i < allObjects.size(); i++) try
        {
            objectMapping dependent = (objectMapping)allObjects.elementAt(i);
            // find the one association it is dependent on
            int dependedOn = 0;
            for (int j = 0; j < uSubReps.assocMaps().size(); j++)
            {
                AssociationMapping am = (AssociationMapping)uSubReps.assocMaps().elementAt(j);
                //codeTrace(am.fullName());
                for (int depEnd = 0; depEnd < 2; depEnd++)
                {
                    int refEnd = 1 - depEnd;
                    associationEndMapping aem = am.assocEnd(depEnd);
                    ClassSet refClass = am.assocEnd(refEnd).cSet();
                    if ((aem.cSet().equals(dependent.cSet())) &&
                        (aem.required()) &&
                        (uSubReps.namedObjectRep(refClass) != null))
                    {
                        dependedOn++;
                        // store the class dependency graph
                        dependsOn.put(dependent.cSet().stringForm(),refClass);
                    }
                }
            }
            if (!(dependedOn == 1))
            {
                res = false;
                String description = ("The dependent object of class " + dependent.cSet().stringForm() + " on "
                     + dependedOn + " associations. There should be just one.");
             	int nature = CompilationIssue.COMPILE_TEXT_CONTENT_DISALLOWED;
                codeWarning(nature,dependent.nodePath(),description);
            }
        }
        catch (Exception ex)
        {
        	String problem = "Exception checking dependent objects: " + ex.getMessage();
         	int nature = CompilationIssue.COMPILE_DEPENDENT_OBJECT;
            codeWarning(nature,primary.nodePath(),problem);
        }
        checkNotCircular(dependsOn);
        codeTrace("Dependent object check: " + res);
        return res;
    }

    // to be written
    void checkNotCircular(Hashtable<String, ClassSet> dependsOn)
    {
    }

//-------------------------------------------------------------------------------------------
//                  Code for string content of elements and attributes
//-------------------------------------------------------------------------------------------

    /* Generate code in XML writing procedure cProc for elements with root path fromRoot
    and defined coding context cc, to fill the element's text content after it has been created.
    If revisit = true, create code in procedure rProc to modify the text on revisit to the element
    */
    private boolean contentCode(WProc cProc, WProc rProc, Xpth fromRoot, codingContext cc,String caseNo)
    throws MapperException
    {
    	String attName = null; 
        boolean elementOK = codeTextValue(cProc, fromRoot, cc, attName);
        if (elementOK && cc.elementOK())
        {
            /* Only add a 'set text of element' step if codeTextValue has added a step before
            to put some text in the current string. Setting text in step 0 is useless. 
            This logic is now encoded in as_sText. */
            cProc.as_sText(caseNo);
        }
        return elementOK;
    }

    /* Generate code in XML writing procedure cProc for elements with root path fromRoot
    and defined coding context cc, to add all attributes to an element.
    */
    private boolean attributeCode(WProc cProc, Xpth fromRoot, codingContext cc)
    throws MapperException
    {
        TreeElement localTree;
        String attName;
        Xpth newPath;
        nodeRepSet nrs;
        codingContext ac;
        boolean attributeOK = false;

        // find all attributes that have mappings, to check that every attribute is found in the XML structure
        Hashtable<String,Vector<MappingTwo>> checkChildMappings = this.mappingsWithParent(fromRoot.stringForm());
        int nChildMappings = checkChildMappings.size();

        // loop over all attributes
        localTree = outTree.fromRootPath(fromRoot,true);
        int nAttributes = localTree.attributes().size();
        for (int i = 0; i < nAttributes; i++)
        {
            attName = localTree.attribute(i);
            newPath = fromRoot.addInnerStep("@" + attName);
            // to check that every attribute with mappings is found
            if (checkChildMappings.get(newPath.stringForm()) != null) checkChildMappings.remove(newPath.stringForm());

            ac = cc.copyCC(); // fresh start for ac.elementOK
            ac.setRootPath(newPath);
            nrs = new nodeRepSet(this,newPath,ac);// things represented by the attribute node

            /* if an attribute does not represent anything in the object model,
            or a link or when-condition value, do  not generate code to write the attribute. */
            if (!nrs.noMeaning())
            {
                /* Generate code in cProc to put the text value destined for the attribute
                in the current String */
                attributeOK = codeTextValue(cProc, newPath, ac,attName);

                /* If this code generation was successful,
                generate code to add the attribute (only when the current string is non-empty)
                 put the current string in it, and reset the current string empty */
                if (attributeOK)
                {
                    cProc.as_addAtt(attName,"att");
                }
            }
        }

        // check that every child attribute with mappings has been found
        String unmatchedAtts = "";
        int nUnmatched = 0;
        for (Enumeration<String> en = checkChildMappings.keys(); en.hasMoreElements();)
        {
            String childPath = (String)en.nextElement();
            // remove the root of the path and an extra '/'
            String aName = childPath.substring(fromRoot.stringForm().length() + 1);
            // if the remaining node is an attribute...
            if (aName.startsWith("@"))  {unmatchedAtts = unmatchedAtts + aName + " "; nUnmatched++;}
        }
        if (!unmatchedAtts.equals(""))
            throw new MDLWriteException("Path " + fromRoot.stringForm()
                + " has " + nChildMappings + " child mappings, " + nAttributes + " child attributes, and "
                 + nUnmatched + " attributes: " +
                unmatchedAtts + " with mappings but not found in the XML structure");


        return attributeOK;
    }

    /* generate code to set the current string to the text value appropriate for a node -
    whether it is an element or attribute.
    If there is any problem, either this method will return false, or cc.elementOK() will be false. 
    attName = null for the text content of an element. */
    private boolean codeTextValue(WProc cProc,  Xpth fromRoot, codingContext cc, String attName)
    throws MapperException
    {
        boolean nodeOK = true;
        // we need only look at what is represented on this node, not its unique subtree
        nodeRepSet nrs = new nodeRepSet(this,fromRoot, cc);
        // fixed property mappings are ignored; should they be??
        int nProps =nrs.nonFixedPropertyMaps();
        codeTrace("code text value for node '" + fromRoot.stringForm() + "' with " + nProps + " properties");

        // multiple non-fixed properties on a node; issue a warning and try to find the value of one of them
        if (nProps > 1)
        {
            codeTrace("Text content case A");
            nodeOK = doCase_A(cProc, nrs,cc);
        } // end of case A

        // one non-fixed property on a node
        else if (nProps == 1)
        {
            propertyMapping pm = nrs.nonFixedPropertyMap(0);

            // object is not in the runtime context
            if (!cc.hasObject(pm.cSet()))
            {
            	int nature = CompilationIssue.COMPILE_TEXT_CONTENT_DISALLOWED;
                codeWarning(nature,fromRoot,"Text content case B (property of an object not in the runtime context) is not allowed.");
            }

            // object is in the runtime context
            else if (cc.hasObject(pm.cSet()))
            {
                codeTrace("Text content case C");
                int nWhens = nrs.whenMaps().size();
                int nLinks = nrs.linkMaps().size();
                // one property, no when-condition values, possibly some link condition values
                if (nWhens == 0)
                {
                    codeTrace("Text content case C.1");
                    cProc.as_getProp(pm.cSet(),pm.propertyName(),new multiplicity(false,true),"C.1");
                }
                // both when- and link-condition values
                else if ((nWhens > 0) && (nLinks > 0))
                {
                  	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
                	String problem = ("Text content case C.2 (both link and when-conditions) not allowed");
                    codeWarning(nature,cc.rootPath(),problem);
                }

                // one property, some when-values, no link values
                else if ((nWhens > 0) && (nLinks == 0))
                {
                    codeTrace("Text content case C.3");
                    cProc.as_getProp(pm.cSet(),pm.propertyName(),new multiplicity(false,true),"C.3");
                }
            } // end of case with one property of an object in the runtime context
        } // end of case with one property

        // no properties
        else if (nProps == 0)
        {
            codeTrace("Text content case D");
            nodeOK = doCase_D(cProc,nrs,cc,attName);
        }
        return nodeOK;
    }

    /* generate code to set the current string to the text value appropriate for a node -
    in case A, where the node represents multiple properties.
    Try to find the first property which is a property of an object in the coding context, and
    use that property value.
    Have not yet fully thought through when- and link-conditions.
    If there is any problem, either this will return false, or cc.elementOK() will be false. */
    private boolean doCase_A(WProc cProc, nodeRepSet nrs,codingContext cc) throws MapperException
    {
        boolean nodeOK = false;
        int nProps =nrs.nonFixedPropertyMaps();
        String propMaps = "";
        for (int i = 0; i < nProps; i++) if (!nodeOK)
        {

            propertyMapping pm = nrs.nonFixedPropertyMap(i);
            propMaps = propMaps + " " + pm.fullName();
            if (cc.hasObject(pm.cSet()))
            {
                cProc.as_getProp(pm.cSet(),pm.propertyName(),new multiplicity(false,true),"A");
                nodeOK = true;
            }
            else
            {
                propMaps = propMaps + "(not in context)";
            }
        }
        if (!nodeOK)
        {
          	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
        	String problem = "Node represents more than one property," +
        	" but none are properties of an object in context represented by the object source.";
            codeWarning(nature,cc.rootPath(),problem);
        }
        return nodeOK;
    }


    /* generate code to set the current string to the text value appropriate for a node -
    in case D, where the node represents no properties.
    If there is any problem, either this will return false, or cc.elementOK() will be false. */
    private boolean doCase_D(WProc cProc, nodeRepSet nrs, codingContext cc, String attName)
    throws MapperException 
    {
          boolean nodeOK = true;
          int nWhens = nrs.whenMaps().size();
          int nLinks = nrs.linkMaps().size();
          int nLHS = 0;
          int nRHS = 0;
          for (int l = 0; l < nLinks; l++)
          {
              linkCondition lc = nrs.linkCondition(l);
              if (lc == null) {throw new MDLWriteException("null link condition");}
              if (lc.rootToLeftValue().compatible(nrs.rootPath())) nLHS++;
              else if (lc.rootToRightValue().compatible(nrs.rootPath())) nRHS++;
          }

          // no properties, when-values or link values
          if ((nWhens ==0) && (nLinks ==0))
          {
              // allowed case; do nothing
              codeTrace("Text content case D.1");
          }
          else if (nWhens > 0)
          {
              // node only represents when-condition values
              if (nLinks == 0)
              {
                  codeTrace("Text content case D.2");
                  nodeOK = doCase_D_2(cProc,nrs,cc);
              }
              else if (nLinks > 0)
              {
                  String problem =("In case D.3 (some link and when-conditions) only the link conditions define value of node");
               	  int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
                  codeWarning(nature,cc.rootPath(),problem);
              }
          }   //end of (nWhens > 0) case

          // no when-condition values, or when-condition values overridden by link conditions
          if (nLinks > 0)
          {
              codeTrace("Nlinks = " + nLinks + "; nLHS = " + nLHS + "; nRHS = " + nRHS);
              // some RHS and some LHS link-condition values
              if ((nLHS > 0) && (nRHS > 0))
              {
            	  String problem =("A node defining some link condition LHS and some RHS is not allowed");
               	  int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
                  codeWarning(nature,cc.rootPath(),problem);
              }

              // some RHS (object-end) values and no properties
              else if ((nLHS == 0) && (nRHS > 0))
              {
                  codeTrace("Text content case D.5");
                  nodeOK = doCase_D_5(cProc,nrs,cc);
              }

              else if ((nLHS > 0) && (nRHS == 0)) // cases D.6, D.7 and D.8
              {
                  codeTrace("Text content cases D.6,7,8");
                  String caseNo = "unset";

                  /* all link conditions having this node as LHS should be link
                   * conditions for property mappings, or they should all be link conditions 
                   * for association mappings. mappingType is 'property' or 'association' in these two cases.
                   * The third value 'mixed' is an error and will cause a warning. */
                   String mappingType = linkConditionMappingType(nrs); 
                   if (mappingType.equals("mixed")) return false;

                   for (int i = 0; i < nrs.linkMaps().size(); i++)
                  {
                      linkCondition lc = nrs.linkCondition(i);
                      AssociationMapping am = (AssociationMapping)nrs.linkMaps().elementAt(i); //association mapping with link condition
                      ClassSet theCSet = lc.objectCSet();
                      codeTrace("Other end class " + theCSet.stringForm());

                      /* find whether the node at the other end also represents a property;
                      if not, the object key is used as a link condition value.*/
                      Vector<MappingTwo> pMaps = objectEndLinkProperties(lc);
                      String pName = null;
                      if (pMaps.size() > 0) for (int pm = 0; pm < pMaps.size(); pm++)
                      {
                          propertyMapping pMap = (propertyMapping)pMaps.elementAt(pm);
                          pName = pMap.propertyName();
                          codeTrace("Other end property '" + pName + "' of class " + pMap.cSet().stringForm());
                      }

                      // possibly many link conditions, all to the same object
                      if (!cc.hasObject(theCSet))
                      {
                          codeTrace("Text content case D.6");
                          caseNo = "D.6";
                          nodeOK = doCase_D_6_and_7(cProc,lc,am,cc,pMaps,caseNo);
                      }

                      // one association link condition
                      else if (mappingType.equals("association"))
                      {
                          int end = lc.end(); // 1 or 2
                          if ((!am.self()) && (!cc.hasEndInContext(am.nodePath(),am.assocEnd(end-1))))
                          {
                              codeTrace("Text content case D.7");
                              caseNo = "D.7";
                              nodeOK = doCase_D_6_and_7(cProc,lc,am,cc,pMaps,caseNo);
                          }
                          /* grouping objects by their association to another object
                          the association can be M:1 or M:N and it will still do a grouping
                          (if M:N, having objects repeated in different groups) */
                          else if ((!am.self()) && (cc.hasObject(theCSet)))
                          {
                              codeTrace("Text content case D.8");
                              caseNo = "D.8";
                              nodeOK = cProc.as_getProp(theCSet,pName,new multiplicity(false,true),caseNo);
                          }
                          /* an association to an object of the same class, not yet in context;
                          but one object of the class is in context. */
                          else if ((am.self()) && (cc.hasObject(theCSet)))
                          {
                              codeTrace("Text content case D.9");
                              caseNo = "D.9";
                              nodeOK = cProc.as_relProps(theCSet,am.assocName(),theCSet,end,pName,caseNo);
                          }
                      }
                      else
                      {
                          codeTrace("Could not find text content sub-case of case D");
                      	  int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
                          codeWarning(nature,cc.rootPath(),"Error finding a value for a cross-condition LHS value in class " + theCSet.stringForm());
                          nodeOK = false;
                      }
                      
                      /* for all but the final link condition, add a step to set the attribute value 
                       * or element text (for the final condition, the step is added outside this method) */
                      if (nodeOK && (i < nrs.linkMaps().size()- 1))
                      {
                    	  // attribute
                    	  if (attName != null)	{cProc.as_addAtt(attName,"att");}
                    	  // element text content
                    	  else 					{cProc.as_sText(caseNo);}
                      }
                      
                  } // end of iteration over link conditions
              } // end of ((nLHS > 0) && (nRHS == 0)) case (D.6..9)
          } //end of (nLinks > 0) case
          return nodeOK;
    }
    
    /** If the node is the LHS of several link conditions, 
     *  check that all the link conditions point to the same other
     *  node on their RHS 
     *  26/6/2009 - this restriction has been removed. 
    private boolean linkConditionsHaveSameRHSNode(nodeRepSet nrs) throws MapperException
    {
    	boolean sameRHSNode = true;
    	Xpth previousNodePath = null;
    	for (int l = 0; l < nrs.linkMaps().size(); l++)
    	{
    		Xpth RHSNodePath = nrs.linkCondition(l).rootToRightValue();
    		if ((l > 0) && (!(RHSNodePath.stringForm().equals(previousNodePath.stringForm()))))
    		{
            	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
    			codeWarning(nature,nrs.rootPath(),"Different cross conditions with this node as LHS have different RHS nodes");
    			sameRHSNode = false;
    		}
    		previousNodePath = RHSNodePath;
    	}
    	return sameRHSNode;
    } */
    
    /**
     * all link conditions having this node as LHS should be link
     * conditions for property mappings, or they should all be link conditions 
     * for association mappings; even though you sometimes wnat mixed cases, we cannot yet handle it
     * @param nrs 
     * @return 'property' or 'association' in these two cases.
     * If the types are 'mixed' it will issue a warning and set it  'mixed' (21/8/09; as 
     * in cases of doubt, choosing 'association' gives trouble at run time)
     */
    private String linkConditionMappingType(nodeRepSet nrs) throws MDLWriteException
    {
    	String type = "";
    	String previousType = "";
    	for (int i = 0; i < nrs.linkMaps().size(); i++)
    	{
    		MappingTwo m = nrs.linkMaps().get(i);
    		if (m instanceof propertyMapping) type = "property";
    		if (m instanceof AssociationMapping) type = "association";
    		if ((i > 0) && (!(type.equals(previousType))))
    		{
            	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
            	String warning = "Different link conditions with this node as LHS belong to both property and association mappings";
    			codeWarning(nature,nrs.rootPath(),warning);
    			codeTrace(warning);
    			return ("mixed");
    		}
    		previousType = type;
    	}
    	return type;
    }


// Case D.2: node  represents no properties, some when-condition values, no link condition values
 boolean doCase_D_2(WProc cProc, nodeRepSet nrs, codingContext cc)
  {
      boolean nodeOK = true;
      // find the when-value to be stored on this node
      int found = 0;
      whenValue wv = (whenValue)cc.whenValues().get(nrs.rootPath().stringForm());
      if (wv != null)
      {
          found++;
          codeTrace("Text content case D.2 with value '" + wv.value() + "'");
          cProc.as_getConst(wv.value(),"D.2");
      }
      if (!(found == 1)) nodeOK = false;
      return nodeOK;
  }

/* Case D.5: node  represents no properties, no when-condition values,
some link condition RHS (object end) values */
 boolean doCase_D_5(WProc cProc, nodeRepSet nrs, codingContext cc) throws MapperException
  {
      boolean nodeOK = true;
      // if the object end class of any link condition is in context, get its key
      for (int l = 0; l < nrs.linkMaps().size(); l++)
      {
          linkCondition lc = nrs.linkCondition(l);
          if (lc.rootToRightValue().compatible(nrs.rootPath()))
          {
              ClassSet theCSet = lc.objectCSet();
              if (cc.hasObject(theCSet))
              {
                  // get the key of the object
                  nodeOK = cProc.as_getProp(theCSet,null,new multiplicity(true,true),"D.5");
                  return nodeOK;
              }        	  
          }
      }
      
      /* if none match , don't throw an exception - just let it fail, and then there will be
       * no attempt to set the text value of the Element or attribute. */
      return false;
  }

 /* Case D.6: one or more link conditions, all to the same object, or properties
or associations with maxCardinality = 1 at the other end; and
Case D.7: node  represents no properties, no when-condition values,
and the LHS of one association link condition, to many objects
In both cases you follow the association and get the property or key of the object.
D.6 gets one property or key, D.7 may get several and concatenate them. */
boolean doCase_D_6_and_7(WProc cProc, linkCondition lc, AssociationMapping am, codingContext cc, Vector<MappingTwo> pMaps, String caseNo) 
	throws MapperException
{
	//cProc.
    boolean nodeOK = true;
    String pName = null;
    String pClass = null;
    ClassSet theCSet = lc.objectCSet(); // class at the object end of the link condition, not in context
    /*  the object at the start end of the association should be in context. */
    int thisEnd = 2 - am.classEnd(theCSet);
    ClassSet cThis = am.assocEnd(thisEnd).cSet(); // class at this end of the association, should be in context
    if (cc.hasObject(cThis))
    {
        Vector<AssociationMapping> assChain = null;
        /* if there is no property at the other end of the link, find the independent object represented
         * on the node which represents the ClassSet at the other end of the association,
         * and make an association chain to that object */
        if (pMaps.size() == 0)
        {
        	objectMapping om = getObjectMapping(theCSet);
        	if (om != null)
        	{
        		ObjMapping omm = (ObjMapping)om.map();
        		NodeMappingSet nms = (NodeMappingSet)omm.eContainer();
        		List<ObjMapping> independents = nms.independentObjectMappings();
        		if (independents.size() == 1)
        		{
        			ClassSet targetCSet = independents.get(0).getClassSet();
        			assChain = associationChain(am,theCSet,targetCSet);
        		}
        		else
        		{
        			String problem = "There are " + independents.size() + " independent objects on a node at the end of an association";
        	       	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
        	        codeWarning(nature,cc.rootPath(),problem);
        		}
        	}
        	else
        	{
    			String problem = "Found no object mapping for the end of an association mapping";
    	       	int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
    	        codeWarning(nature,cc.rootPath(),problem);
        	}
        }
        /* if there is a property at the other end of the link, the association chain may need to be
         extended by further associations to reach the class owning the property.
         There may be more than one property mapping - in which case you need to try all of them
         to see which ones have an association chain to the ClassSet of the end object of the association. 
         Heuristic - choose one of the shortest association chains (there may be irrelevant ones, which are
         likely to be longer) */
        else if (pMaps.size() > 0)
        {
        	int minLength  = 1000;
            for (int pm = 0; pm < pMaps.size(); pm++)
            {
                propertyMapping pMap = (propertyMapping)pMaps.elementAt(pm);
                Vector<AssociationMapping> candidateChain = associationChain(am,theCSet,pMap.cSet());
                if ((candidateChain != null) && (candidateChain.size() < minLength))
                {
                	assChain = candidateChain;
                	minLength = assChain.size();
                    pName = pMap.propertyName();
                    pClass = pMap.cSet().stringForm();                	
                }
            }       	
        }

        if (assChain == null)
        {
        	String problem =("Cannot extend a chain of associations from class " + theCSet.stringForm()
                + " to class " + pClass + " which has property '" + pName + "' in a link condition.");
       	  int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
          codeWarning(nature,cc.rootPath(),problem);
        }
        else
        {
            Vector<String[]> aChain = chainToArrays(cThis,assChain);
            if (codeTracing) writeArrayChain(aChain);
            nodeOK = cProc.as_relProps(cThis,aChain,pName,caseNo);
        }
    }
    else
    {
          String problem =("Object of class "
              + cThis.stringForm() + " at end " + (thisEnd+1) + " of the association "
              + am.fullName() + " for an association link condition "
              + " will not be in the runtime context.");
     	  int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
          codeWarning(nature,cc.rootPath(),problem);
    }
    return nodeOK;
}

private objectMapping getObjectMapping(ClassSet cSet)
{
	objectMapping om = null;
	Vector<objectMapping> mappings= this.objectMappingsByClassName(cSet.className());
	for (int i = 0; i < mappings.size(); i++)
	{
		objectMapping next = mappings.get(i);
		if (next.getClassSet().subset().equals(cSet.subset())) om = next;
	}
	return om;
}

    /* If the object-end of a link condition also represents any properties,
     return a Vector of the property mappings; otherwise return an empty Vector. */
    private Vector<MappingTwo> objectEndLinkProperties(linkCondition lc) throws MapperException
    {
        nodeRepSet otherEnd = new nodeRepSet(this,lc.rootToRightValue());
        return otherEnd.propertyMaps();
    }


    /* The node represents the LHS of one or more link conditions,
    for properties or associations.
    Check that the class/subset of the object involved is the same for all
    properties and associations - and return that ClassSet; or warn if there is more than one.
    Return the last classSet if there is any conflict of ClassSet.
    26/6/2009 this restriction has been removed. 
    
    private ClassSet linkedObjectCSet(repSet rs) throws MapperException
    {
        ClassSet cs = null;
        String warning = "Link condition LHS values refer to class/subsets: ";
        // collect all object classSets referred to
        Hashtable<String,ClassSet> classSetsFound = new Hashtable<String,ClassSet>();
        for (int i = 0; i < rs.linkMaps().size(); i++)
        {
            linkCondition lc = rs.linkCondition(i);
            classSetsFound.put(lc.objectCSet().stringForm(), lc.objectCSet());
        }
        
        // pick one classSet and warn if there is more than one 
        if (classSetsFound.size() > 0) 
        {
        	for (Enumeration<String> en = classSetsFound.keys();en.hasMoreElements();)
            {
        		String key = en.nextElement();
            	warning = warning + key + ", ";
            	cs = classSetsFound.get(key);
            }
        	if (classSetsFound.size() > 1)
        		codeWarning(CompilationIssue.COMPILE_CROSS_CONDITION,rs.rootPath(),warning);
        }
        return cs;
    } */

    /* You need to follow a chain of associations to get from class cThis to the class of the property mapping pMap
    for the property whose value is the object end of a link condition.
    The first of these associations is in the association mapping am which has the link condition.
    Return a Vector of association mappings which defines the chain of associations.
    */
    private Vector<AssociationMapping> associationChain(AssociationMapping am, ClassSet theCSet,ClassSet cEnd)
    throws MapperException
    {
        Vector<AssociationMapping> vStart = new Vector<AssociationMapping>();
        vStart.addElement(am);
        return extendAssociationChain(vStart,theCSet,cEnd);
    }

    /*  The chain of association mappings vSoFar ends in ClassSet latestCSet.
    If latestCSet is the target ClassSet cEnd, return the chain as it is.
    Otherwise, try recursively to extend the chain to reach cEnd.
    If it fails, return null.
    */
    private Vector<AssociationMapping> extendAssociationChain(Vector<AssociationMapping> vSoFar,ClassSet latestCSet,ClassSet cEnd)
    throws MapperException
    {
        Vector<AssociationMapping> vRes = null;
        Vector<AssociationMapping> allAMs = associationMappings(); // all association mappings involving any ClassSet
        if (latestCSet.equals(cEnd)) {vRes = vSoFar;}  // success - look no further
        else for (int i = 0; i < allAMs.size(); i++)
        {
            Vector<AssociationMapping> vTry = AssociationMapping.vCopyAssociationMapping(vSoFar);  // will be modified in ways that may fail
            AssociationMapping amNext = (AssociationMapping)allAMs.elementAt(i);
            if (vRes == null)
            {
                ClassSet cs1 = amNext.assocEnd(0).cSet();
                ClassSet cs2 = amNext.assocEnd(1).cSet();
                // association from the end of the chain to a ClassSet cs2 at end 2 which has not been met before
                if ((cs1.equals(latestCSet)) && (!alreadyFound(cs2,vTry)))
                {
                    vTry.addElement(amNext);
                    vRes =  extendAssociationChain(vTry,cs2,cEnd);
                }
                // association from the end of the chain to a ClassSet cs1 at end 1 which has not been met before
                else if ((cs2.equals(latestCSet)) && (!alreadyFound(cs1,vTry)))
                {
                    vTry.addElement(amNext);
                    vRes =  extendAssociationChain(vTry,cs1,cEnd);
                }
            }
        }
        return vRes;
    }

    // return true if a ClassSet appears in any association mappings of the chain so far
    private boolean alreadyFound(ClassSet cs, Vector<AssociationMapping> vTry)
    throws MapperException
    {
        boolean found = false;
        for (int i = 0; i < vTry.size(); i++)
        {
            AssociationMapping am = vTry.elementAt(i);
            if (cs.equals(am.assocEnd(0).cSet())) found = true;
            if (cs.equals(am.assocEnd(1).cSet())) found = true;
        }
        return found;
    }

    /* Convert a Vector of association mappings to a Vector of String arrays aLink as follows:
    aLink[0] = association name
    aLink[1] = target class name
    aLink[2] = target class subset
    aLink[3] = target end, "1" or "2"
    */
    private Vector<String[]> chainToArrays(ClassSet cStart, Vector<AssociationMapping> aChain)
    throws MapperException 
    {
        Vector<String[]> res = new Vector<String[]>();
        ClassSet cLatest = cStart;
        boolean failed = false;
        for (int i = 0; i < aChain.size(); i++) if (!failed)
        {
            AssociationMapping am = aChain.elementAt(i);
            int startEnd = am.classEnd(cLatest); //  1 or 2; the end at the start of this link
            if (startEnd > 0)
            {
                cLatest = am.assocEnd(2 - startEnd).cSet(); // assocEnd(0 or 1); the start if the next link

                String[] aLink = new String[4];
                aLink[0] = am.assocName();
                aLink[1] = cLatest.className();
                aLink[2] = cLatest.subset();
                aLink[3] = new Integer(3 - startEnd).toString(); // 1 or 2
                res.addElement(aLink);
            }
            else
            {
                failed = true;
                String problem = ("Failed to find association end class");
             	  int nature = CompilationIssue.COMPILE_CROSS_CONDITION;
                  codeWarning(nature,am.nodePath(),problem);
            }
        }
        return res;
    }

    private void writeArrayChain(Vector<String[]> bChain)
    {
        message("Link Condition Association chain:");
        for (int i = 0; i < bChain.size(); i++)
        {
            String[] aLink = bChain.elementAt(i);
            message("Link: " + aLink[0] + " " + aLink[1] + " "+ aLink[2] + " "+ aLink[3]);
        }
    }


    void writeAllFilters()
    {
        filter f;
        message("");
        message("Inclusion filters on all classes");
        for (Enumeration<Vector<objectMapping>> en = objectMappingsByClassName.elements(); en.hasMoreElements();)
        {
        	Vector<objectMapping> v = en.nextElement();
        	for (int i = 0; i < v.size(); i++)
        	{
        		objectMapping om = v.get(i);
                message("Class '" + om.className() + "' subset '" + om.subset() + "':");
                for ( int j = 0; j < om.inclusionFilters().size(); j++)
                {
                    f = (filter)om.inclusionFilters().elementAt(j);
                    f.write();
                }
        		
        	}
        }
        message("");
    }


//--------------------------------------------------------------------------
//               Writing out an XML form of the generation procedures
//--------------------------------------------------------------------------


    protected void writeXMLProc(WProc wp)
    throws XpthException
    {
        Xpth procPath = wp.pathSpec();
        if (procPath.size() == 0) // startup procedure has an empty path
            {wp.writeToXML(xProcOut.topOut(),xProcOut); }// write startup procedure just below the root
        else if ((procPath.definite()) && (procPath.size() > 0)) // all other procedures with definite paths
        {
            Element elProc = findOrMakeElement(procPath);
            if (elProc != null)
                {wp.writeToXML(elProc,xProcOut);}
        }
        else
        {
            message("Cannot yet output XML form of procedures for indefinite path " + procPath.stringForm());
        }
    }

    /**
     * 
     * @param procPath
     * @return
     * @throws XpthException
     */
    private Element findOrMakeElement(Xpth procPath)
    throws XpthException
    {
        // find how far you can go, matching elements with tag names in the XPath
        Element elCurrent = xProcOut.topOut();
        boolean matched = true;
        int matchedStep = -1;
        for (int stepNo = 0; stepNo < procPath.size(); stepNo++) if (matched)
        {
            String tagName = procPath.nodeName(stepNo);
            Vector<Element> children = xProcOut.namedChildElements(elCurrent,"element");
            matched = false;
            for (int cn = 0; cn < children.size();cn++)
            {
                Element child = children.elementAt(cn);
                String elName = child.getAttribute("name");
                if (tagName.equals(elName))
                {
                    matched = true;
                    elCurrent = child;
                    matchedStep = stepNo;
                }
            }
        }

        // now elCurrent is the element you need to add new elements to
        if (matchedStep < procPath.size() -1)
            for (int stepNo = matchedStep + 1; stepNo < procPath.size(); stepNo++)
            {
                Element elNew = xProcOut.newElement("element");
                elNew.setAttribute("name",procPath.nodeName(stepNo));
                elNew.setAttribute("path", procPath.truncateTo(stepNo).stringForm());
                Element elProcs = xProcOut.newElement("procedures");
                elNew.appendChild(elProcs);
                elCurrent.appendChild(elNew);
                elCurrent = elNew;
            }

        // find the <procedures> element under the latest matching <element> element
        Vector<Element> elps = xProcOut.namedChildElements(elCurrent,"procedures");
        Element elp = null;
        if (elps.size() == 0) {message("no <procedures> element at path " + procPath.stringForm());}
        else {elp = (Element)elps.elementAt(0);}
        return elp;
    }


//--------------------------------------------------------------------------
//                          odds & sods
//--------------------------------------------------------------------------

    // message for fatal errors detected in code generation
    void codeError(int nature, Xpth path, String problemType) throws MDLWriteException
    {
    	codeTrace("---Error--- " + nature + ": " + problemType);
    	codeTrace("Path: " + path.stringForm());
    	CompilationIssue ci = new CompilationIssue(nature,"","",problemType,path );
    	storeCompilationIssue(path,ci);
    	throw new MDLWriteException("Code generation error at node with XPath '"
            + path.stringForm() + "': " + problemType);
    }


    // message for non-fatal errors detected in code generation
    void codeWarning(int nature, Xpth path, String problemType) throws MDLWriteException
    {
    	codeTrace("---Warning--- " + nature + ": " + problemType);
    	codeTrace("Path: " + path.stringForm());
    	CompilationIssue ci = new CompilationIssue(nature,"","",problemType,path );
    	storeCompilationIssue(path,ci);
    }

    // message for non-fatal errors detected in code generation
    void codeWarning(int nature, Xpth path, String problemType,
    		String expected, String actual) 
    throws MDLWriteException
    {
    	codeTrace("---Warning--- " + nature + ": " + problemType);
    	codeTrace("Actual: '" + actual + "'; expected '" + expected + "'");
    	codeTrace("Path: " + path.stringForm());
    	CompilationIssue ci = new CompilationIssue(nature,expected,actual,problemType,path );
    	storeCompilationIssue(path,ci);
    }
    
	/**
	 * store all compilation issues in a Hashtable keyed by path.
	 * For each path, store a Hashtable of compilation issues, keyed by
	 * a unique identifier to avoid duplicates of the same issue
	 * @param path
	 * @param ci
	 */
    private void storeCompilationIssue(Xpth path, CompilationIssue ci)
	{
		Hashtable<String,CompilationIssue> issues = compilationIssues.get(path.stringForm());
		if (issues == null) issues = new Hashtable<String,CompilationIssue>();
		CompilationIssue existingIssue = issues.get(ci.key());
		if (existingIssue != null) existingIssue.addOccurrence();
		else issues.put(ci.key(), ci);
		compilationIssues.put(path.stringForm(),issues);		
	}
	
	/**
	 * attach compilation issues to the 'Element' nodes of the Wproc tree
	 */
    private void noteCompilationIssuesOnProcedures()
	throws MapperException
	{
		for (Enumeration<String> en = compilationIssues.keys();en.hasMoreElements();)
		{
			String pathString = en.nextElement();
			Hashtable<String,CompilationIssue> issues = compilationIssues.get(pathString);	
			noteIssuesOnNode(pathString, issues);
		}
	}

	/**
	 * note all the compilation issues which came up on one Element 
	 * of the MappedStructure,on that Element node of the Wproc file
	 * @param pathString path to the Element
	 * @param issues the CompilationIssues that came up
	 * @throws MapperException if you cannot find the Element in the Wproc XML.
	 */
    private void noteIssuesOnNode(String pathString,Hashtable<String,CompilationIssue> issues)
	throws MapperException
    {
		/* chase down through 'Element' nodes, matching the 'name' attribute 
		 * with the path as far as you can. Put the Element on the last node matched. */
		Element current = xProcOut().topOut(); // top node is <procedures>
		StringTokenizer st = new StringTokenizer(pathString,"/");
		boolean found = true;
		while ((st.hasMoreTokens()) && (found))
		{
			String elName = st.nextToken();
			found = false;
			List<Element> els = XMLUtil.namedChildElements(current, "element");
			for (Iterator<Element> it = els.iterator();it.hasNext();)
			{
				Element next = it.next();
				if (next.getAttribute("name").equals(elName)) 
				{
					found = true;
					current = next;
				}
			}
		}
		
		// the current Element node is now where you want to put the issues
		for (Enumeration<CompilationIssue> en = issues.elements();en.hasMoreElements();)
		{
			CompilationIssue ci = en.nextElement();
			current.appendChild(ci.procNode(xProcOut().outDoc()));
		}
	}


    // message for detailed tracing of code generation
    protected void codeTrace(String s) {if (codeTracing) mChan().message(s);}


    /** write a listing of all procedures to standard output file, in order of increasing XPath size */
    public void writeProcedures() throws MapperException
    {
        WProc WP;
        message("");
        int size = procedureTables.size();
        int procsFound = 0; // number of procedures found
        int found = 0; // number of XPaths found
        int XPathSize = 0; // at each pass, look only for procedures with XPaths of a given size
        while (found < size) // keep increasing the size until you have found all XPaths
        {
            // look for all XPaths of this size
            for (Enumeration<String> e = procedureTables.keys(); e.hasMoreElements();)
            {
                String xps = e.nextElement();
                Xpth xp = new Xpth(NSSet(),xps);
                if (xp.size() == XPathSize)
                {
                    found++; // you have found another XPath of the right size
                    Hashtable<String,WProc> v = procedureTables.get(xps);// get all its procedures and write them out
                    for (Enumeration<WProc> en = v.elements(); en.hasMoreElements();)
                    {
                        WP = en.nextElement();
                        WP.writeProcedure();
                        procsFound++;
                    }
                }
            }
            XPathSize++; // increase the size of path you are looking for
            if (XPathSize > 1000) {message("Failed to find all procedures.");found = size;} // backstop
        }
        message("");
        message("Generated " + procsFound + " procedures.");
        message("");
    }

    /* the subtree of elements and attributes rooted at the end node of rootPath.
    Namespace prefixes come from the output structure definition, not the MDL. */
    TreeElement subTree(Xpth rootPath)  throws XpthException
    {
        TreeElement currTree = null;
        if (rootPath.size() == 1) {currTree = outTree;}
        else {currTree = outTree.fromRootPath(rootPath,true);}
        return currTree;
    }

    



}
