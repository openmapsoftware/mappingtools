package com.openMap1.mapper.writer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;

import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MDLReadException;
import com.openMap1.mapper.core.MDLWriteException;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.RunIssue;
import com.openMap1.mapper.core.RunTranslateException;
import com.openMap1.mapper.core.XMLException;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.mapping.associationEndMapping;
import com.openMap1.mapper.mapping.AssociationMapping;
import com.openMap1.mapper.mapping.filter;
import com.openMap1.mapper.mapping.filterAssoc;
import com.openMap1.mapper.mapping.objectMapping;
import com.openMap1.mapper.mapping.propertyConversion;
import com.openMap1.mapper.mapping.propertyMapping;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.objectRep;
import com.openMap1.mapper.util.ModelUtil;
import com.openMap1.mapper.util.XMLUtil;

/**
 * An extension of the class Wproc for converting XML writing 
 * procedures to XSLT.
 * 
 * @author robert
 *
 */
public class WProcXSLT extends WProc {


	//---------------------------------------------------------------------------
	//	                          Constructor
	//---------------------------------------------------------------------------

	    /**
	     * create an empty procedure
	     */
	    public WProcXSLT(Xpth ps, ProcedureClass xwrit, codingContext cc, boolean create)
	    {
	    	super (ps, xwrit, cc, create);
	    }
	
	    /**
	     * constructor from a <proc> element and its subtree
	     */
	    public WProcXSLT(Xpth ps, ProcedureClass xwrit, XSLGenerator XX, Element procEl) throws MapperException
	    {
	    	super( ps, xwrit,  procEl);
	    	this.XX = XX;
	    	xslout = XX.xslout();
	    	xout = xslout;
	    }


	    /* clone a procedure to make a fresh runtime version
	     with the same set of steps; but return a WProcXSLT*/
	     public WProc pClone()
	     throws MapperException{
	         WProcXSLT wpc = new WProcXSLT(pathSpec, XW, creationContext, onCreate);
	         for (int i = 0; i < steps.size(); i++)
	         {
	             wpc.steps.addElement(wpc.copyStep(stepNumber(i)));
	             wpc.stepNumber(i).enabled = stepNumber(i).enabled;
	         }
	         wpc.XX = this.XX;
	         wpc.xslout = XX.xslout();
	         wpc.xout = wpc.xslout;
	         return wpc;
	     }

	//---------------------------------------------------------------------------
//	                   generating XSLT
	//---------------------------------------------------------------------------

	    public void setMatchPattern(String mp) {matchPattern = mp;}
	    public void setModeString(String ms) {modeString = ms;}
	    public void setCreatedElementName(String ce) {createdElementName = ce;}
	    public void setNameString(String ns) {nameString = ns;}

	/* generate XSLT corresponding to this procedure
	 The first instruction creates a variable set to the current node in the input XML,
	 which represents an object of a class, named after the class and its subset.
	 */
	public boolean generateXSLT(ClassSet cSet) throws MapperException
	{
	    boolean success = true;
	    if (XX == null)
	    {
	    	System.out.println("Null XSL Generator for class " + cSet.className());
	    	return success;
	    }
	    inputReader = XX.topInputReader();

	    // create a template for this procedure
	    templateNode = XSLElement("template");
	    if (modeString != null)
	      {templateNode.setAttribute("mode",modeString);}
	    if (nameString != null)
	      {templateNode.setAttribute("name",nameString);}
	    if (matchPattern != null)
	      {templateNode.setAttribute("match",matchPattern);}
	    xout.topOut().appendChild(templateNode);
	    nodeForXSLT = templateNode;

	    // create a variable named after the class, set to the current input node
	    if (cSet != null)
	    {
	      Element varNode = newElement(xout, "xsl:variable");
	      String varName = XX.variableName(cSet,true);
	      varNode.setAttribute("name", varName);
	      varNode.setAttribute("select",".");
	      if (xslout.clashes(varName,templateNode))
	      {
	      	int nature = RunIssue.RUN_XSLT_VARIABLE;
	      	runWarningMessage(nature, "Variable '" + varName + "' is already used in template '" 
	      			+ nameString + "' of mode '" + modeString + "' and match pattern '" + matchPattern + "'");
	      }
	      xslout.addVariableBeforeCallOrApply(templateNode, varNode);
	      // templateNode.appendChild(varNode);
	    }

	    /* create a top element to be output by this procedure, but do not attach it yet,
	    as the variable declarations from addToContext steps must come first. */
	    if (createdElementName != null)
	    {
	        outputElement = newElement(xout,createdElementName);
	        nodeForXSLT = outputElement;
	    }

	    success = true;
	    runTraceMessage("");
	    runTraceMessage("Writing XSLT in procedure for " + pathSpec.stringForm() + ".");
	    for (int i = 0; i < whenValues.size(); i++)
	    {
	        whenValue wv = whenValues.elementAt(i);
	        runTraceMessage("with when-value '" + wv.value() + "' at node " + wv.rootPath().stringForm());
	    }

	    if (context == null)
	    {
	      	int nature = RunIssue.RUN_XSLT_CONTEXT;
	      	runWarningMessage(nature, "No runtime context for XSLT");
	      }
	    else if (context.rootPath() == null)
	      {
	      	int nature = RunIssue.RUN_XSLT_ROOT_PATH;
	      	runWarningMessage(nature, "No runtime root path for XSLT");
	      }
	    else {rootPath = context.rootPath();}

	    // create code in the template for the steps in this procedure
	    for (int i = 0; i < steps.size(); i++) if (success)
	    {
	        try{
	            stepNo = i;
	            if (XW.runTracing()) {stepNumber(i).writeStep();}
	            success = success & stepNumber(i).XSLTForStep();
	        }
	        catch (MapperException e) // let  null pointer exceptions crash it so I can see the stack trace
	        {
	            success = false;
	            stepNumber(i).writeStep();
	            String problem =("There was an exception writing XSLT for this step at sub-step "
	                + stepNumber(i).subStep + " : " + e.getMessage());
	            e.printStackTrace();
	            int nature = RunIssue.RUN_XSLT_EXCEPTION;
	            runWarningMessage(nature,problem);
	        }
	    }

	    /* attach the top element to be output by this procedure,
	    after any variable declarations added by addToContext steps */
	    if (createdElementName != null)
	    {
	      // detect clashes between variables declared under the output node and other variables already in the template
	      String clashVarName = xslout.mergeClash(outputElement,templateNode);
	      if (clashVarName != null)
	      {
	        	int nature = RunIssue.RUN_XSLT_VARIABLE;
	        	runWarningMessage(nature, "Variable '" + clashVarName + " inside the output element of template '"
	                    + nameString + "' clashes with an existing variable");
	        }
	      templateNode.appendChild(outputElement);
	    }

	    // add <param> elements for any parameters needed by this template (they will come first)
	    addParametersToThisTemplate();

	    runTraceMessage("Completed writing XSLT for procedure for " + pathSpec.stringForm() + ".");
	    runTraceMessage("");
	    return success;
	}
	
	/**
	 * @param inCSet (class, subset) of objects in the input whose property or association mappings are needed
	 * @return the correct MDLXOReader to provide those mappings
	 * @throws MapperException
	 */
	protected MDLXOReader inputReader(ClassSet inXSLCSet) throws MapperException
	{
		return XX.inputReader(inXSLCSet);
	}
	
    /**
     * @return a Hashtable with
     * Key = full XPath to any object mappings for the class
     * Vector = all object mappings with that full XPath
     * or null if there are no object mappings for the class in any input mapping set
     */


	/**
	 *  put the input reader of the object mapping at the top of the input reader stack, if it is not already there
	 */
	protected void addReaderToStack( objectMapping om)  throws MapperException
	{XX.addReaderToStack(om);}

    /**
     * return a Hashtable of object mappings , 
     * keyed by full XPaths of all object mappings to the class
     */
	protected Hashtable<String,Vector<objectMapping>> objectMappingFullPaths(String className) 
		{return XX.objectMappingFullPaths(className);}	
	

    /**
	 * @param previousCSet the input class set, of the object from which the current object was
	 * reached by an association 
	 * @param currentCSet input class,subset of the current object
	 * @return objectRep for the current object, which has the correct mapping set having
	 * the property and association mappings of the current object.
	 * If the mapping set of the previous object has an import of a mapping set with parameter value
	 * class equal to the current class and subset, then the correct mapping set is the imported mapping set.
	 * In that case, the class and subset must agree with the parameter class of the imported mapping set.
	 * @throws MapperException
	 */
	protected objectRep makeObjectRepForContext(ClassSet XSLCSet)
			throws MapperException
	{
		MDLXOReader reader = XX.inputReader(XSLCSet);
		return new objectRep(null,XSLCSet.className(),XSLCSet.subset(),reader);
	}

	/* After a template has been called or applied by this template, and made (so that the template knows
	what parameters it needs) put the necessary <with-param> elements on the call or apply,
	and record that these variables have been used in the current template
	(so in turn might need to be passed to it as parameters) */
	protected void addParametersToCallOrApply(Element callOrApply, WProc calledTemplate) throws XMLException
	{
	    for (Enumeration<String> en = calledTemplate.parametersNeeded.keys(); en.hasMoreElements();)
	    {
	        // make the <xsl:with-param> element
	        String paramName = en.nextElement();
	        Element withEl = xslout.XSLElement("with-param");
	        withEl.setAttribute("name",paramName);
	        // add it to the <call-template> or <apply-templates> element
	        callOrApply.appendChild(withEl);
	        // record that the parameter has been used in this template
	        contextVariablesUsed.put(paramName,"1");
	    }
	}

	/* After all steps have been added to this template (so all the variables used in this template are known)
	if any of  those variables have not already been declared as variables, declare them as parameters;
	 and record that they need to be passed to this template as parameters*/
	private void addParametersToThisTemplate()  throws XMLException
	{
	    for (Enumeration<String> en = contextVariablesUsed.keys(); en.hasMoreElements();)
	    {
	        String paramName = en.nextElement();
	        trace("trying parameter " + paramName);
	        // this returns true only if the parameter or variable was not already declared
	        if (xslout.addParameter(paramName,templateNode))
	        {
	            parametersNeeded.put(paramName,"1");
	        }
	    }
	}

	// only called for the start procedure, to write the top elements of the XSLT
	public void writeTopXSLTElement() throws MapperException
	{
	    xout.NSSet().addNamespace(new namespace(xslout.XSLPrefix,XMLUtil.XSLURI));
	    Element el = XSLElement("stylesheet");
	    el.setAttribute("version","2.0");
	    xout.setTopOut(el);
	    // declare all output namespaces in the top element
	    xout.addNamespaceAttributes();

	    Element output = XSLElement("output");
	    output.setAttribute("method","xml");
	    output.setAttribute("indent","yes");
	    el.appendChild(output);
	}

	  protected Element XSLElement(String name) throws XMLException {return xslout.XSLElement(name);}

	public void checkContext(int i) throws RunTranslateException
	{
	    if (context != null) {}
	    else {fatalMessage("null context: " + i);}
	}


	/* Generate a new template (identified by a unique mode) which is to be applied for
	all input objects represented as in object mapping om;
	for each of them it is to create an output element with defined tagName,
	representing an object of output class/subset cSet,
	then do stuff to put whatever is required in the element.
	Return the WProc instance which made the template, and knows what parameters it requires.*/
	protected WProc createElementTemplate(ClassSet inPreviousCSet, ClassSet outputCSet,objectMapping om,String mode,String tagName)
	throws MapperException
	{
	    // produce a new context for the new template
	    Xpth newPath = rootPath.addInnerStep(tagName);
	    subtreeContext newContext = context.copySC();
	    newContext.setRootPath(newPath);
	    // make an objectRep with a null node and the correct XOReader, to record the input class and subset
	    objectRep oRep = makeObjectRepForContext(om.XSLCSet());
	    newContext.addObject(oRep,outputCSet);

	    // write the template being applied
	    WProcXSLT elProc = (WProcXSLT)XX.findProcedure(true,newPath,newContext);
	    elProc.setContext(newContext);
	    elProc.setRootPath(newPath);
	    elProc.setMatchPattern(XX.convertStepPrefix(om.nodePath().innerStepString()));
	    elProc.setModeString(mode);
	    elProc.setCreatedElementName(tagName);
	    // use the input ClassSet to label the variable denoting the current node
	    elProc.generateXSLT(om.cSet());
	    return elProc;
	}


	/* Generate a new template (identified by a unique mode) which is to be applied for
	all input objects represented as in object mapping om, and reached by some association;
	for each of them it is to append the value of property propName of the object, space-separated.
	If propName is null, it is to append unique keys of the objects, space-separated.

	This method violates the usual coding style, by making a new template without its own WProc object;
	so nodeForXSLT and templateNode need to be reset and restored.*/

	/**
	 * 
	 * @param inPreviousCSet
	 * @param om
	 * @param mode
	 * @param outputCSet
	 * @param propName
	 * @return
	 * @throws MapperException
	 */
	boolean appendPropertyTemplate(ClassSet inPreviousCSet, objectMapping om,String mode,ClassSet outputCSet, String propName, boolean addSpace)
	throws MapperException
	{
	    // save WProc state variables which will be changed in this method
	    Element keepNodeForXSLT = nodeForXSLT;
	    Element keepTemplateNode = templateNode;
	    String keepCurrentStringVar = currentStringVar;

	    boolean success = true;
	    // make an objectRep with a null node, to record the input class and subset
	    objectRep oRep = makeObjectRepForContext(om.XSLCSet());
	    context.addObject(oRep, outputCSet);
	    // should this be added to the context?

	    Element template = XSLElement("template");
	    template.setAttribute("mode",mode);
	    String match = XX.convertStepPrefix(om.nodePath().innerStepString());
	    template.setAttribute("match",match);
	    xout.topOut().appendChild(template);
	    nodeForXSLT = template;  // to put stuff in this template
	    templateNode = template;

	    /* declare a variable = the current node, named after the class
	    (this variable will be prepended on the path by makeinputPropertyVariable,
	    called by makeKeyVariable or makeOutputPropertyVariable) */
	    Element classVar = XSLElement("variable");
	    String varName = XX.variableName(outputCSet,true);
	    classVar.setAttribute("name",varName);
	    classVar.setAttribute("select",".");
	    if (xslout.clashes(varName,templateNode))
	    {
	    	int nature = RunIssue.RUN_XSLT_VARIABLE;
	    	runWarningMessage(nature, "Variable '" + varName + "' is already used in property template '" + nameString + "'");
	    }
	    xslout.addVariableBeforeCallOrApply(templateNode, classVar);
	    // templateNode.appendChild(classVar);

	    // make a variable in this template to hold the key or property value
	    String vName = null;
	    if (propName == null)
	        {vName = makeKeyVariable(oRep, outputCSet);} // (uses changed nodeForXSLT; changes currentStringVar)
	    else {vName = makeOutputPropertyVariable(oRep, outputCSet, propName);} // (uses changed nodeForXSLT; changes currentStringVar)

	    if (vName == null) success = false;
	    else if (vName != null)
	    {
	        // the template includes the value of the variable and a space, only if addSpace is true
	        Element v1 = XSLElement("value-of");
	        if (addSpace) v1.setAttribute("select","concat($" + vName + ",' ')");
	        else v1.setAttribute("select","$" + vName);
	        nodeForXSLT.appendChild(v1);    	
	    }

	    // restore WProc state variables
	    nodeForXSLT = keepNodeForXSLT;
	    templateNode = keepTemplateNode;
	    currentStringVar = keepCurrentStringVar;
	    return success;
	}

	    /** write XSLT to create a new XSLT variable and fill it with the value of property
	    * pName (of output class/subset cSet)
	    * for input object oRep at the top of the stack (represented by current node of input XML).
	    * pName is directly represented in the output, but may be an output XML converted property.
	    * If it is, call a template to calculate it from object model properties,
	    * which are got by makeObjectPropertyVariable.
	    * Leave the variable name in currentStringVar, and return it; or null if fail */
	    protected String makeOutputPropertyVariable(objectRep inORep, ClassSet outCSet, String pName)
	    throws MapperException
	    {
	        String varName = null; // to remain so if anything goes wrong
	        currentStringVar = null;  // to remain so if anything goes wrong
	        boolean success = false;
	        
	        // normal case - pName is a property in the object model
	        if (!XW.isPseudoProperty(outCSet,pName))
	        {
	            varName = makeObjectModelPropertyVariable(inORep, outCSet, pName);
	            // currentStringVar has been set by makeObjectModelPropertyVariable
	            success = true;
	        }
	        // if this is an output XML converted property, given by a property out-conversion
	        else if (XW.isPseudoProperty(outCSet,pName))
	        {
	            propertyConversion pc = XW.getOutConversion(outCSet,pName);
	            if (pc == null) 
	            {
	            	int nature = RunIssue.RUN_PROPERTY_CONVERSION;
	               	runWarningMessage(nature,"pseudo-property '" + pName + "' of class '" 
	             		   + outCSet.className() + "' has no out-conversion.");
	            }
	            else if (!pc.hasImplementation("XSLT"))
	            {
	            	int nature = RunIssue.RUN_XSLT_PROPERTY_CONVERSION;
	            	runWarningMessage(nature,"There is no declared XSLT output property conversion template to form property '"
	                        + pName + "' of class '" + inORep.className() + "'");
	            }
	            else if (pc.hasImplementation("XSLT"))
	            {
	                String tempName = pc.methodName("XSLT");
	                Element template = null;
	                try{
	                    template = XW.getConversionTemplate(tempName,"out_",xslout);
	                    Vector<String> tempParams = templateSet.getParameters(template);
	                    if (!(tempParams.size() == pc.arguments().size()))
	                    {
	                    	int nature = RunIssue.RUN_XSLT_PARAMETER;
	                    	runWarningMessage(nature,"Template '" + tempName + "' has " + tempParams.size()
	                                + " parameters; it should have " + pc.arguments().size());                    	
	                    }
	                    else
	                    {
	                    	success = true; // but it may not stay so...
	                        varName = XX.newVariable();
	                        Element varEl = XSLElement("variable");
	                        varEl.setAttribute("name",varName);
	                        Element callEl = XSLElement("call-template");
	                        callEl.setAttribute("name","out_" + tempName);
	                        varEl.appendChild(callEl);
	                        for (int i = 0; i < pc.arguments().size(); i++)
	                        {
	                            /* object model property whose value is the argument,
	                            and should also be the XSLT parameter name */
	                            String argProp = (String)pc.arguments().elementAt(i);
	                            String expectedArg = (String)tempParams.elementAt(i);
	                            if (argProp.equals(expectedArg))
	                            {
	                                // xslt variable name which will carry the property value
	                                String argName = makeObjectModelPropertyVariable(inORep,outCSet,argProp);
	                                success = success && (argName != null);
	                                if (success)
	                                {
	                                    Element paramEl = XSLElement("with-param");
	                                    paramEl.setAttribute("name",argProp);
	                                    paramEl.setAttribute("select","$" + argName);
	                                    callEl.appendChild(paramEl);                                	
	                                }
	                            }
	                            else
	                            {
	                            	success = false;
	                            	int nature = RunIssue.RUN_XSLT_PARAMETER;
	                            	runWarningMessage(nature,"Unexpected parameter for XSLT template '" + tempName
	                                    + "'; expected '"  + expectedArg + "' but got '" + argProp + "'");
	                            }
	                        }
	                        if (xslout.clashes(varName,templateNode))
	                        {
	                        	success = false;
	                        	int nature = RunIssue.RUN_XSLT_VARIABLE;
	                        	runWarningMessage(nature,"Variable '" + varName + "' is already used in template '" 
	                        			+ nameString + "' of mode '" + modeString + "' and match pattern '" + matchPattern + "'");
	                        }
	                        if (success)
	                        {
	                        	xslout.addVariableBeforeCallOrApply(nodeForXSLT, varEl);
	                            // nodeForXSLT.appendChild(varEl);
	                            currentStringVar = varName;
	                            // remember to add  the template to the output XSLT file
	                            XX.rememberTemplate("out_" + tempName,template);                        	
	                        }
	                    }
	                	
	                }
	                catch (Exception ex)
	                {
	                	int nature = RunIssue.RUN_XSLT_TEMPLATE;
	                	runWarningMessage(nature,"Could not find XSLT template '" + tempName
	                      + "' in supplied template file: " + ex.getMessage());
	                }
	            }
	        }
	        return varName;
	    }


	    /* 
	    
	    
	    
	    /**
	     * write XSLT to create a new XSLT variable and fill it with the value of property
	     * pName, which is in the object model,
	     * for input object oRep at the top of the stack (represented by current node of input XML).
	     * Leave the variable name in currentStringVar, and return it; or null if fail
	     * If pName is not directly represented in the input XML, look for a property
	     * in-conversion to get it from converted properties which are represented.
	     * 
	     * If this fails in any way, write a warning message and return null.
	     */
	    private String makeObjectModelPropertyVariable(objectRep inORep,ClassSet outCSet, String pName)
	    throws MapperException
	    {
	    	boolean success = false;
	        String varName = null; // to be returned if anything goes wrong
	        currentStringVar = null; // to remain null if anything goes wrong

	        propertyMapping pm = XX.getInputPropertyMapping(inORep.cSet(),pName); // cSet() is an XSL ClassSet
	        if (pm != null)// there is an input property mapping to this property
	        {
	            varName = makeInputPropertyVariable(inORep, pName);
	            // currentStringVar will be set to varName by makeInputPropertyVariable
	            success = true;
	        }
	        // if this property is not represented in the input, look for an input conversion
	        else if (pm == null)
	        {
	            propertyConversion pc = inputReader(inORep.cSet()).getInConversion(XX.trueClassSet(inORep.cSet()),pName);
	            // drop out; no input conversion
	            if (pc == null)
	            {
	            	/* this should not be a warning as it happens all the time - property not represented in input XML
	            	int nature = RunIssue.RUN_PROPERTY_REPRESENTED;
	            	runWarningMessage(nature,"Property '" + pName + "' of class '" + oRep.className()
	                    + "' is not represented in the input XML or given by an input property conversion.");
	                 */
	            }
	            else if (pc != null)
	            {
	            	// drop out; no XSLT implementation of the input conversion
	                if (!pc.hasImplementation("XSLT"))
	                {
	                	int nature = RunIssue.RUN_XSLT_PROPERTY_CONVERSION;
	                	runWarningMessage(nature,"There is no declared XSLT template implementing the input property conversion to form property '"
	                        + pName + "' of class '" + inORep.className() + "'");
	                }
	                else if (pc.hasImplementation("XSLT"))
	                {
	                    String tempName = pc.methodName("XSLT");
	                    Element template = null;
	                    try {
	                    	// drop out here if you cannot get the template; exception thrown
	                        template = inputReader(inORep.cSet()).getConversionTemplate(tempName,"in_",xslout);
	                        Vector<String> tempParams = templateSet.getParameters(template);
	                        // drop out if the template has the wrong number of parameters
	                        if (!(tempParams.size() == pc.arguments().size()))
	                        {
	                        	int nature = RunIssue.RUN_XSLT_PARAMETER;
	                        	runWarningMessage(nature,"Template '" + tempName + "' has " + tempParams.size()
	                                + " parameters; it should have " + pc.arguments().size());
	                        }
	                        else if (tempParams.size() == pc.arguments().size())
	                        {
	                        	success = true; // but it might not stay that way...
	                            varName = XX.newVariable();
	                            Element varEl = XSLElement("variable");
	                            varEl.setAttribute("name",varName);
	                            Element callEl = XSLElement("call-template");
	                            callEl.setAttribute("name","in_" + tempName);
	                            varEl.appendChild(callEl);
	                            for (int i = 0; i < pc.arguments().size(); i++)
	                            {
	                                /* input converted property whose value is the argument
	                                and should also be the XSLT parameter name */
	                                String argProp = (String)pc.arguments().elementAt(i);
	                                String expectedArg = (String)tempParams.elementAt(i);
	                                if (argProp.equals(expectedArg))
	                                {
	                                    // xslt variable name which will carry the property value
	                                    String argName = makeInputPropertyVariable(inORep,argProp);
	                                    Element paramEl = XSLElement("with-param");
	                                    paramEl.setAttribute("name",argProp);
	                                    paramEl.setAttribute("select","$" + argName);
	                                    callEl.appendChild(paramEl);
	                                }
	                                else
	                                {
	                                	success = false;
	                                	int nature = RunIssue.RUN_XSLT_PARAMETER;
	                                    runWarningMessage(nature,"Unexpected parameter for XSLT template '" + tempName
	                                        + "'; expected '"  + expectedArg + "' but got '" + argProp + "'");
	                                }
	                            }
	                            if (xslout.clashes(varName,templateNode))
	                            {
	                            	success = false;
	                            	int nature = RunIssue.RUN_XSLT_VARIABLE;
	                            	runWarningMessage(nature,"Variable '" + varName + "' is already used in template '" 
	                            			+ nameString + "' of mode '" + modeString + "' and match pattern '" + matchPattern + "'");
	                            }
	                            if (success)
	                            {
	                            	xslout.addVariableBeforeCallOrApply(nodeForXSLT, varEl);
	                                //nodeForXSLT.appendChild(varEl);
	                                currentStringVar = varName;
	                                // remember to add the template to the output XSLT file
	                                XX.rememberTemplate("in_" + tempName,template);                                	
	                            }
	                        }                    	
	                    }
	                    catch (Exception ex)
	                    {
	                    	int nature = RunIssue.RUN_XSLT_TEMPLATE;
	                    	runWarningMessage(nature,"Could not find XSLT template '" + tempName
	                          + "' in supplied template file." + ex.getMessage());
	                    }
	                }
	            }
	        }
	        return varName;
	    }

	    /* write XSLT to create a new XSLT variable and fill it with the value of property
	    pName (of output class and subset cSet),
	    which is a property or converted property directly represented in the input XML.
	    for input object oRep  (represented by a node variable named after its ClassSet).
	    Leave the variable name in currentStringVar, and return it; or null if fail */
	    private String makeInputPropertyVariable(objectRep oRep, String pName)
	    throws MapperException
	    {
	        String vName = null;
	        // the property mapping may come back with a different ClassSet, if there are import mapping sets in the input
	        propertyMapping pm = XX.getInputPropertyMapping(oRep.cSet(),pName); // cSet() is an XSL ClassSet
	        if (pm == null) // usually not reached
	        {
	        	int nature = RunIssue.RUN_PROPERTY_REPRESENTED;
	            runWarningMessage(nature,"The input XML does not represent property '"
	              + pName + "' of class '" + oRep.className() + "'");
	        }
	        else
	        {
	            vName = XX.newVariable(pName);
	            currentStringVar = vName;
	            Element varEl = null;
	            if (pm.fixed()) // property has a fixed value in the input, and no relative path
	            {
	              //varEl = newTextElement(xout,"xsl:variable",pm.value()); // does not work in XSLT 2.0
	              varEl = newElement(xout,"xsl:variable");
	              varEl.setAttribute("select","'" + pm.value() + "'");
	             //  varEl.setAttribute("as","xsl:string"); // causes a Saxon error 'no imported namespace'; and not necessary anyway
	              varEl.setAttribute("name",vName);
	            }
	            else // property has no fixed value, but the value is reached by a relative path
	            {
	              /* prefix path by a parameter whose value is the node representing the current object of the class 
	               * (with subset as in the input mapping set where it was found; not necessarily the same as in the property mapping*/
	              String classPar = XX.variableName(XX.trueClassSet(oRep.cSet()),true);
	              xslout.addParameter(classPar,templateNode); // 
	              //contextVariablesUsed.put(classPar,"1");
	              String XPath = "$" + classPar + "/" + XX.convertPathPrefixes(pm.objectToProperty().stringForm())
	                  + pm.XPathWhenTests(XX) + pm.XPathLinkTests(XX,true,classPar); // start from object

	              varEl = XSLElement("variable");
	              varEl.setAttribute("name",vName);
	              if (pm.hasDefault())
	              {
	                Element choosEl = XSLElement("choose");
	                varEl.appendChild(choosEl);
	                Element whenEl = XSLElement("when");
	                choosEl.appendChild(whenEl);
	                whenEl.setAttribute("test",XPath);
	                Element valEl = XSLElement("value-of");
	                valEl.setAttribute("select",XPath);
	                whenEl.appendChild(valEl);
	                Element otherEl= newTextElement(xout,"xsl:otherwise",pm.defaultValue());
	                choosEl.appendChild(otherEl);
	              }
	              else if (!pm.hasDefault())
	              {
	                varEl.setAttribute("select", XPath);
	              }
	            }
	            if (xslout.clashes(vName,templateNode))
	            {
	            	int nature = RunIssue.RUN_XSLT_VARIABLE;
	                runWarningMessage(nature,"Variable '" + vName + "' is already used in template '" 
	                		+ nameString + "' of mode '" + modeString + "' and match pattern '" + matchPattern + "'");
	            }
	            xslout.addVariableBeforeCallOrApply(nodeForXSLT, varEl);
	            //nodeForXSLT.appendChild(varEl);
	        }
	        return vName;
	    }


	    /* write XSLT to create a new XSLT variable and fill it with a unique key of a node,
	    which constitutes a unique identifier for object oRep of output class/subset cSet.
	    Leave the variable name in currentStringVar, and return it. */
	    protected String makeKeyVariable(objectRep oRep, ClassSet cSet)
	    throws MapperException
	    {
	        String vName = XX.newVariable();
	        currentStringVar = vName;
	        Element  varEl = XSLElement("variable");
	        varEl.setAttribute("name",vName);
	        String classPar = XX.variableName(XX.trueClassSet(oRep.cSet()),true); // variable named after the class/subset
	        xslout.addParameter(classPar,templateNode);// make this a parameter of the template if necessary
	        // get a unique id for the node representing the current object of the class
	        String setKey = "generate-id($" + classPar + ")" ;
	        varEl.setAttribute("select",setKey);
            xslout.addVariableBeforeCallOrApply(nodeForXSLT, varEl);
	        // nodeForXSLT.appendChild(varEl);
	        return vName;
	    }


	    /**
	     * Write XSLT to navigate an association, given that one end has an object of ClassSet endCSet in the
	     *   subtree context, and the other 'target' end, of output class/subset cSet, does not.
	     *   If tagName is not null, apply a template to make an element with tagName for each object found.
	     *   If propName is not null, apply a template to append the property value,
	     *   space-separated, for each object found.
	     *   It is an error for both tagName and propName to be non-null
	     *
	     * @param inEndCSet ClassSet input XML class and subset of the objectRep at the start end
	     * @param assocName String name of the association
	     * @param outCSet ClassSet output (class,subset) of the target object
	     * @param targetEnd int target end 1 or 2 of the association
	     * @param tagName String If tagName is not null, apply a template to make an element with tagName for each object found.
	     * @param propName String If propName is not null, apply a template to append the property value,
	     *   space-separated, for each object found.
	     * @return boolean
	     * @throws MDLWriteException
	     * @throws MDLReadException
	     */
	    protected boolean associationXSL(ClassSet  inEndCSet, String assocName, ClassSet outCSet, int targetEnd,
	            String tagName, String propName) throws MapperException
	        {
	    		trace("Association xsl for " + assocName);
	            boolean success = true;
	            runTraceMessage("association XSL from object of input ClassSet " + inEndCSet.stringForm() + " by association "
	                    + assocName + " to object of output ClassSet " + outCSet.stringForm() + " at end " + targetEnd + " with tag '" + tagName
	                    + "' and property " + propName);

	            // find all the input association mappings, and for each one, find the target end input object mapping
	            Vector<AssociationMapping> inAssocMaps = inputAssocMappings(inEndCSet, assocName, outCSet.className(), targetEnd);

	            for (int targetSubsetIndex = 0; targetSubsetIndex < inAssocMaps.size(); targetSubsetIndex++)
	            {
	                AssociationMapping am = inAssocMaps.elementAt(targetSubsetIndex);
	                objectMapping om = inputTargetObjectMapping(am,targetEnd);
	              boolean firstOnly = false;
	              boolean attachToTemplate = false;
	              boolean addSpace = true;
	              // if (((AssocMapping)am.map()). FIXME: if max multiplicity at target end is 1, set addSpace false

	              // make a variable for the association node
	              String assocVarName = assocVariable(inEndCSet,am,targetEnd,attachToTemplate);

	              // make a variable for the target end object node. Use iteration index to distinguish variable names
	              String targetClassVar = targetObjectVariable(assocVarName, am,  targetEnd, outCSet,firstOnly,attachToTemplate);

	              // no need to use iteration index to distinguish template modes; they are all made unique
	              String mode = XX.newMode(om.className());

	              Element applyEl = XSLElement("apply-templates");
	              applyEl.setAttribute("mode",mode);
	              applyEl.setAttribute("select","$" + targetClassVar);
	              nodeForXSLT.appendChild(applyEl);

	              // create the template which is to be applied, and add the necessary parameters to the <apply-templates>
	              if ((propName == null) && (tagName != null))
	              {
	                  WProc elProc = createElementTemplate(inEndCSet,outCSet,om,mode,tagName);
	                  addParametersToCallOrApply(applyEl,elProc);
	              }
	              // currently templates for appending properties cannot demand parameters
	              else if (tagName == null)
	                          {success = success & appendPropertyTemplate(inEndCSet,om, mode, outCSet, propName,addSpace);}
	              else if ((propName != null) && (tagName !=null))
	              {
	              	int nature = RunIssue.RUN_XSLT_ASSOCIATION;
	                  runWarningMessage(nature,"Cannot call associationXSL with both propName and tagName non-null.");
	              }
	            }
	             return success;
	        }

	       /**
	        * find the Vector of all input association mappings which represent the association required for
	        * the output
	        *
	        * @param endCSet ClassSet input XML (class,subset) for the start end of the association
	        * @param assocName String  name of the association
	        * @param targetClassName String name of class at the target end of the association
	        * @param targetEnd int target end of the association
	        * @return associationMapping  that association mapping
	        */
	       protected Vector<AssociationMapping> inputAssocMappings(ClassSet endCSet, 
	    		   String assocName, String targetClassName, int targetEnd)
	    		   throws MapperException
	       {
	    	   boolean topCall = true;
	    	   return XX.inputAssocMappings(endCSet, assocName, targetClassName, targetEnd,topCall);
	       }

	       /* introduce a variable whose value is the node-set of association nodes in the input XML;
	       return the name of the variable. */
	       protected String assocVariable(ClassSet inputCSet, AssociationMapping am,int targetEnd, boolean attachToTemplate)
	           throws MapperException
	       {
	          // variable to represent the association nodes
	          String varName = XX.newVariable();

	          // navigate from start object to association node
	          // prefix the path with the variable denoting the node representing the start object
	          String startVarName = XX.variableName(XX.trueClassSet(inputCSet), true);
	          String firstPath = selectStartToAssoc(startVarName, am, targetEnd);

	          // add the variable for the start object nodes as a parameter of the template, if it has not been added already
	          xslout.addParameter(startVarName,templateNode);
	          /* note that the calling template may need to pass in the variable as a parameter */
	          contextVariablesUsed.put(startVarName,"1");
	          trace("context variable " + startVarName);

	          attachVariableNode(varName, firstPath, attachToTemplate);

	          return varName;
	       }

	       /**
	        * introduce a variable whose value will be the node-set of target objects of an association in the input XML; return the name of the variable
	        * @param assocVar String XSL variable for the set of association nodes
	        * @param am associationMapping input association mapping
	        * @param inputSubsetIndex and index 0,1,... to make different variable names for different subsets of the class in the input XML mappings
	        * @param targetEnd int target end 1 or 2 of the association
	        * @param targetCSet ClassSet (class,subset) of the target end in the output mappings
	        * @param firstOnly boolean if firstOnly = true, make sure the node-set has at most one node.
	        * @param attachToTemplate boolean if true attach to current template; otherwise attach to current XSLT node
	        * @return String the name of the variable
	        * @throws MDLWriteException
	        */
	      protected String targetObjectVariable(String assocVar, AssociationMapping am, 
	             int targetEnd, ClassSet targetCSet, boolean firstOnly, boolean attachToTemplate)
	      throws MapperException
	       {
	 
               //declare the variable, giving it different names if there are several subsets of the class in the input XML
	         String varName = classVarName(am,targetEnd,targetCSet.subset());
	         if (xslout.clashes(varName,templateNode))
	         {
	           	int nature = RunIssue.RUN_XSLT_VARIABLE;
	           	String message = "Variable '" + varName + "' is already used in template '" 
     		   + nameString + "' of mode '" + modeString + "' and match pattern '" + matchPattern + "'";
	            runWarningMessage(nature,message);
	          }

	         // select expression to navigate from association node to target object, and apply property inclusion filters
	         String secondPath = selectAssocToEnd(assocVar, am, targetEnd,targetCSet);
	         if (firstOnly) secondPath = "(" + secondPath + ")[1]";

	         // find the association inclusion filters of the output object mapping of the target end of the association
	         Vector<filterAssoc> assocFilters = XW.namedObjectMapping(targetCSet).filterAssocsForXSLT(am);

	         // find the input XML ClassSet of the target end of the association
	         ClassSet inputTargetCSet = am.assocEnd(targetEnd-1).XSLCSet();

	         // if there are some association inclusion filters to be applied, introduce new variables for them
	         if (assocFilters.size() > 0)
	         {
	             // make a variable for the node set unfiltered by any association inclusion filters
	             String unfiltered = XX.newVariable();
	             attachVariableNode(unfiltered, secondPath, attachToTemplate);

	             // filter that node set by the 'and' of all the association inclusion filter  nodesets
	             makeAssocInclusionFilters(inputTargetCSet, assocFilters, unfiltered, varName, attachToTemplate,am);
	         }

	         // if there are no association inclusion filters, introduce the class variable with the select for the association
	         else if (assocFilters.size() == 0)
	         {
	             attachVariableNode(varName, secondPath, attachToTemplate);
	         }
	         return varName;
	       }

	       /**
	        * set the variable filtered to be the variable unfiltered followed by AND of variables representing nodesets
	        * for association filters
	        *
	        * @param inputCSet input (class,subset) of the objects being filtered
	        * @param assocFilters Vector association inclusion filters for an object mapping
	        * @param unfiltered String nodeset of objects before filtering
	        * @param filtered String variable representing the filtered nodeset
	        * @param attachToTemplate boolean if true, attach to current template
	        */
	       protected void makeAssocInclusionFilters(ClassSet inputCSet, Vector<filterAssoc> assocFilters, 
	    		   String unfiltered, String filtered, boolean attachToTemplate,AssociationMapping am)
	               throws MapperException
	       {
	    	   String prefix = "$" + unfiltered + "/self::node()";
	           String filters = "[";
	           for (int af = 0; af < assocFilters.size(); af++)
	           {
	               filterAssoc fa = (filterAssoc)assocFilters.elementAt(af);
	               /* If the target object has been reached by some association, there is no point in filtering
	                * it by the same association  */
	               if (!((am != null) && (fa.assocName().equals(am.assocName()))))
	               {
		               String oneFilter = oneAssocFilterXSLT( unfiltered, inputCSet, fa,attachToTemplate);
		               // if any association filter fails, just leave it out
		               if (!(oneFilter.equals("failed")))
		               {
			               if (af > 0) filters = filters + " and ";
			               filters = filters + "($" + oneFilter + ")";	            	   
		               }	            	   
	               }
	           }
	           filters = filters + "]";
	           // if there are no filters or all filters fail, put in no []
	           if (filters.equals("[]")) filters = "";
	           attachVariableNode(filtered, (prefix + filters), attachToTemplate);


	       }

	       /**
	        * make a variable which is the nodeSet got by navigating the association of an association inclusion filter
	        *
	        * @param unfiltered String xslt variable for the node set unfiltered by any association filters
	        * @param inputCSet input XML ClassSet of the objects being filtered
	        * @param fa filterAssoc the association inclusion filter
	        * @param attachToTemplate if true, attach all variables to the current template
	        * @return String the name of the variable representing the node set, 
	        * or return the value 'failed' if you fial to find the filter
	        */
	       private String oneAssocFilterXSLT(String unfiltered,ClassSet inputCSet, filterAssoc fa, boolean attachToTemplate)
	               throws MapperException
	       {
	    	   trace("Association filter for " + fa.stringForm());
	           // input XML association mapping for the association filter
	           Vector<AssociationMapping> inAssocMaps = inputAssocMappings(inputCSet,fa.assocName(),fa.depCSet().className(),fa.otherEnd());
	           if (inAssocMaps.size() != 1)
	           {
	               String problem =("Found " +  inAssocMaps.size() + " input association mappings "
	                       + "for output inclusion filter " + fa.stringForm());
	               trace("Association filter failed: " + problem);
	               return "failed";
	        	   //int nature = RunIssue.RUN_INCLUSION_FILTER;
	        	   //runWarningMessage(nature, problem);
	           }
	           AssociationMapping fam = inAssocMaps.elementAt(0);

	           // variable to represent the association nodes
	           String assocVar = XX.newVariable();
	           // navigate from start object to association node
	           String firstPath = selectStartToAssoc(unfiltered, fam, fa.otherEnd());
	           attachVariableNode(assocVar, firstPath, attachToTemplate);

	           // navigate from the association node to the independent object
	           boolean firstOnly = true; // value does not matter; node set evaluates to true if it has any elements
	           // (may recurse if the independent object itself has association inclusion filters)
	           return targetObjectVariable(assocVar,fam,fa.otherEnd(),fa.depCSet(),firstOnly, attachToTemplate);
	       }
	       
	        
	        /**
	         * @param inputMap mapping to a class in the input XML
	         * @param outputMap mapping to the same class or a superclass in the output XML
	         * @param depth depth of recursion through filters of associated objects
	         * @return true if the model association filters on the input mapping are as powerful or more
	         * powerful than those on the output mapping, so that all objects returned by the input mapping 
	         * must satisfy the association filters of the output mapping.
	         * FIXME - in principle this method should recurse through filters on objects at the other
	         * end of association filters - but it currently only recurses through one level
	         * (deeper recursion would require retrieval of other input mapping sets)
	         */
	        protected boolean hasStrongerAssociationFilters(objectMapping inputMap, objectMapping outputMap, int depth)
	        throws MapperException
	        {
	        	int maxDepth = 1;
	        	boolean stronger = true;
	        	for (Iterator<filter> it = outputMap.inclusionFilters().iterator();it.hasNext();) if (stronger)
	        	{
	        		filter outFilt = it.next();
	        		// check all association filters of the output mapping; if it has any, 'stronger' becomes false until proven true
	        		if (outFilt instanceof filterAssoc)
	        		{
	        			stronger = false;
    	        		// find a matching association filter of the input mapping
	    	        	for (Iterator<filter> iu = inputMap.inclusionFilters().iterator();iu.hasNext();) if (!stronger)
	    	        	{
	    	        		filter inFilt = iu.next();
	    	        		if (inFilt instanceof filterAssoc)
	    	        		{
	    	        			filterAssoc outAF = (filterAssoc)outFilt;
	    	        			filterAssoc inAF = (filterAssoc)inFilt;
	    	        			// if it matches in classes and association name...
	    	        			if (outAF.sameFilter(inAF))
	    	        			{
	    	        				if (depth == maxDepth) stronger = true; // maximum depth of recursion; success
	    	        				// check that for the object the filter depends on, the input mapping has stronger association filters
	    	        				else if (depth < maxDepth)
	    	        				{
	    	        					MDLXOReader inputReader = new MDLXOReader(null,ModelUtil.getMappedStructure(inputMap.map()),XX.mChan());
	    	        					objectMapping nextInMap = inputReader.namedObjectMapping(inAF.depCSet());
	    	        					objectMapping nextOutMap = XX.namedObjectMapping(outAF.depCSet());
	    	        					// stronger stays false if either mapping cannot be found (should not happen)
	    	        					if ((nextInMap != null) && (nextOutMap != null))
	    	        					{
	    	        						/* if the input mapping is a parameter class mapping,  it will have no association filters
	    	        						 * and we need to find the importing mapping in stead */
	    	        						if (isParameterMapping(inputReader,nextInMap))
	    	        							nextInMap = getImportingMapping(nextInMap,inputMap);
	    	        						if (nextInMap != null)
	    	        							stronger = hasStrongerAssociationFilters(nextInMap,nextOutMap,depth+1);	    	        						
	    	        					}
	    	        				}
	    	        			}
	    	        		}
	    	        	}
	        		}
	        	}
	        	return stronger;
	        }
	        
	        /**
	         * @param inputReader
	         * @param nextInMap
	         * @return true if the mapping is to the paramter class of the reader
	         * @throws MapperException
	         */
	        private boolean isParameterMapping(MDLXOReader inputReader,objectMapping nextInMap)
	        throws MapperException
	        {
	        	ClassSet pcs = XX.getParameterClassSet(inputReader);
	        	return (nextInMap.cSet().equals(pcs));
	        }
	        
	        /**
	         * 
	         * @param nextInMap the parameter class mapping of some mapping set
	         * @param inputMap another object mapping in the same classSet, used to find the root XPath to it
	         * @return the mapping in an outer mapping set which imports nextInMap
	         */
	        private objectMapping getImportingMapping(objectMapping nextInMap,objectMapping inputMap)
	        	throws MapperException
	        {
	        	objectMapping om = null;

	        	// find the full path to the importing mapping
	        	// path of form '/root/..../importer/a/b'
	        	String fullPathToOtherMapping = inputMap.getFullXPath(); 
	        	// path of form  '/dummy/a/b'  'dummy' is the root node of an imported mapping set
	        	String shortPathToOtherMapping = inputMap.nodePath().stringForm();
	        	StringTokenizer st = new StringTokenizer(shortPathToOtherMapping,"/");
	        	// of form 'dummy'
	        	String firstStep = st.nextToken();
	        	// of form '/a/b' ,with the dummy node removed
	        	String noFirstStep = shortPathToOtherMapping.substring(firstStep.length() + 1);
	        	
	        	// check
	        	if (!(fullPathToOtherMapping.endsWith(noFirstStep)))
	        		throw new MapperException("Full path " + fullPathToOtherMapping + " does not end in " + noFirstStep);

	        	int length = fullPathToOtherMapping.length() - noFirstStep.length();
	        	// path of form '/root/...../importer'
	        	String fullPathToImportingMapping = fullPathToOtherMapping.substring(0,length);
	        	trace("Path to importing mapping: " + fullPathToImportingMapping);
	        	
	        	Hashtable<String,Vector<objectMapping>> paths  = XX.objectMappingFullPaths(nextInMap.className());
	        	if (paths != null)
	        	{
	        		Vector<objectMapping> mappings = paths.get(fullPathToImportingMapping);
	        		if (mappings != null) om = mappings.get(0);
		        	else throw new MapperException("No mappings for class " + nextInMap.className() + " at path " + fullPathToImportingMapping);
	        	}
	        	else throw new MapperException("No mappings found for class " + nextInMap.className());
	        	
	        	return om;
	        }
	       

	       /**
	        * add an  element <xsl: variable name = "varName" select = "selectExpression: /> , either to
	        * the current template or the current XSLT node
	        *
	        * @param varName String the variable name
	        * @param selectExpression String the select expression
	        * @param attachToTemplate boolean if true, attache the element to the template; if false, to the current XSLT element
	        */
	       protected void attachVariableNode(String varName, String selectExpression, boolean attachToTemplate)
	       throws XMLException
	       {
	           Element endObject = XSLElement("variable");
	           endObject.setAttribute("name", varName);
	           endObject.setAttribute("select", selectExpression);
	           if (attachToTemplate)
	           {
		            xslout.addVariableBeforeCallOrApply(templateNode, endObject);
	        	   // templateNode.appendChild(endObject);
	           }
	           else
	           {
		            xslout.addVariableBeforeCallOrApply(nodeForXSLT, endObject);
	        	   //nodeForXSLT.appendChild(endObject);
	           }

	          runTraceMessage("Variable: " + varName + ";  path: " + selectExpression);
	       }

	       /* make an XSLT variable named after the class at end 1 or 2 of the association mapped in mapping am */
	       private String classVarName(AssociationMapping am, int end,String outputSubset) throws MapperException
	       {
	           associationEndMapping endMap = am.assocEnd(end - 1);
	           String varName = XX.variableName(endMap.cSet(),true);
	           if ((outputSubset != null) && (!outputSubset.equals(""))) varName = varName + "_" + outputSubset;
	           return  varName;
	       }

	       /**
	        * select expression for the variable denoting the association node in XSLT to navigate an association
	        *
	        * @param startVarName String name of the variable denoting the start variable (usually named after the start class)
	        * @param am associationMapping association mapping in the input XML
	        * @param targetEnd int target end of the association, 1 or 2
	        * @return String the select expression
	        */
	       private String selectStartToAssoc(String startVarName,AssociationMapping am,int targetEnd)
	       throws MapperException
	       {
	           int startEnd = 3 - targetEnd;
	           associationEndMapping stMap = am.assocEnd(startEnd-1);
	           String firstPath = "$" + startVarName + "/" 
	               + XX.convertPathPrefixes(stMap.objToAssoc().stringForm()) // node path
	               + am.XPathWhenTests(XX) // when-conditions in association mapping
	               + stMap.XPathLinkTests(XX,true,startVarName); // link conditions, starting at the object
	          return firstPath;
	       }

	       /**
	        * select expression for the second half of an association path - from association node to the target object node.
	        * <p>
	        * the expression has in it predicates for any property inclusion filters on the final object,
	        * but not for any association inclusion filters, which are dealt with by introducing more variables
	        *
	        * @param assocVar String name of the start variable (association node - anonymous variable)
	        * @param am associationMapping association mapping in the input XML
	        * @param targetEnd int target end of the association, 1 or 2
	        * @param targetCSet ClassSet class and subset in the output mappings at the target end of the association, 1 or 2
	        * @return String the select expression
	        */
	       private String selectAssocToEnd(String assocVar, AssociationMapping am, int targetEnd, ClassSet targetCSet)
	               throws MapperException
	       {
	           objectMapping om = inputTargetObjectMapping(am,targetEnd);
	           associationEndMapping endMap = am.assocEnd(targetEnd - 1);
	           if (endMap == null) 
	           {
	               String problem =("No mapping at end " + targetEnd + " of association;");
	        	   int nature = RunIssue.RUN_OUTPUT_MAPPING;
	        	   runWarningMessage(nature, problem);
	           }
	           if (!(endMap.className().equals(targetCSet.className())))
	           {
	               String problem =("Classes differ at ends of input and output association mappings: '"
	                       + endMap.className() + "' and '" + targetCSet.className() + "'");
	        	   int nature = RunIssue.RUN_OUTPUT_MAPPING;
	        	   runWarningMessage(nature, problem);
	           }

	           // find the output object mapping, for its inclusion filters
	           objectMapping outputMap = XW.namedObjectMapping(targetCSet);
	           if (outputMap == null) 
	           {
	               String problem =("No output mapping for class " + endMap.cSet().stringForm());
	        	   int nature = RunIssue.RUN_OUTPUT_MAPPING;
	        	   runWarningMessage(nature, problem);
	           }

	           String secondPath = "$" + assocVar + "/" // start at the node-set of association nodes
	               + XX.convertPathPrefixes(endMap.assocToObj().stringForm()) // node path
	               + endMap.XPathLinkTests(XX,false, assocVar) // link conditions, starting at the association node
	               + om.XPathWhenTests(XX) // value conditions on the input object mapping
	               + outputMap.XPathPropertyInclusionFilter(XX, om); // output property inclusion filters
	           return secondPath;
	       }


	       // find the input object mapping for the target of the association to be navigated
	       protected objectMapping inputTargetObjectMapping(AssociationMapping am, int targetEnd)
	       throws MapperException
	       {
	         objectMapping om = null;
	         if (am != null)
	         {
	           ClassSet targetCSet = am.assocEnd(targetEnd -1).XSLCSet();
	           om = XX.getInputObjectMapping(targetCSet);
	         }
	         if (om == null) throw new MapperException("No target object mapping for " + am.fullName());
	         return om;
	       }

	   	protected propertyMapping getInputPropertyMapping(ClassSet cSet, String pName) throws MapperException
	   	{
	   		return XX.getInputPropertyMapping(cSet,pName);
	   	}

}
