package com.openMap1.mapper.writer;

import com.openMap1.mapper.core.*;
import com.openMap1.mapper.mapping.*;

import java.util.*;

/**
 * The context of a node in the output XML tree, while 
 * generating wproc code.
 * 
 * @author robert
 *
 */
public class codingContext extends outputContext
{
      /* anticipated grouping criteria in force; each grouping
      criterion is represented by a (classSet, property name) pair
      where the property will be used to group objects beneath this node or a higher node.
      If property name = null, an association to an object in the classSet will be used. */
      private Vector<propSet> groupingCriteria;

      /* vector of the path specifications (definite or indefinite XPaths) for
      all outer procedures on the way down to the current one. */
      private Vector<Xpth> pathSpecs;

      // multiplicity of this element as defined by the XML tree structure
      private multiplicity mult;

      /* Last element of the vector = number identifier of the case currently being handled,
      according to the numbering of the table on page 35 of the development log for May 2002 */
      private Vector<String> caseNumbers;

      /* true if the element has not failed its case check. */
      private boolean elementOK;

      // if true, write code generation trace messages
      private boolean codeTracing;

      /* If this is true, a choice between several when-condition values has been made on this node,
      so the current procedure is one of several procedures for different when-values, to be called in succession.
      Therefore semantic maxOccurs = 1 (in one procedure) leads to actual maxOccurs = N
      (from the procedures called in series).
      whenChoiceMade is not propagated down to contexts for lower nodes by copyCC(). */
      //  private boolean whenChoiceMade; not used

      public Vector<propSet> groupingCriteria() {return groupingCriteria;}
      public Vector<String> caseNumbers() {return caseNumbers;}
      public String getCase() {return caseNumbers.elementAt(caseNumbers.size());}
      public multiplicity mult() {return mult;}
      public boolean elementOK() {return elementOK;}
      public boolean codeTracing() {return codeTracing;}


      public void addGroupingCriterion(propSet ps) {groupingCriteria.addElement(ps);}
      public void setMultiplicity(multiplicity m) {mult = m;}
      protected void setCaseNumbers(Vector<String> cn) {caseNumbers = cn;}
      protected void setTracing(boolean b) {codeTracing = b;}
      public void addPathSpec(Xpth ps) {pathSpecs.addElement(ps);}

      /* coding case number is usually added by checkCase, which also checks minOccurs and maxOccurs
      against the array below. */
      public void setCase(String caseNumber){caseNumbers.addElement(caseNumber);}

      public codingContext(MDLBase md, Xpth rp, boolean trace)
      {
          super(md, rp);
          groupingCriteria = new Vector<propSet>();
          pathSpecs = new Vector<Xpth>();
          elementOK = true;
          caseNumbers = new Vector<String>();
          codeTracing = trace;
      }

      /* deep copy as starting point for changes to a new version.
      However, this clone has 'revisit' false - not copied across from
      this context. Assume ab initio that  a child element
      will not require a revisit procedure.
      Do not copy over whenChoiceMade. */
      public codingContext copyCC() throws MDLWriteException
      {
          int i;
          codingContext cc = new codingContext(MD,rootPath().copy(),codeTracing);
          for (i = 0; i < uniqueCSets().size(); i++)
              {cc.addUniqueCSet(uniqueCSets().elementAt(i));}
          for (Enumeration<whenValue> en = whenValues().elements(); en.hasMoreElements();)
              {cc.setWhenValue(en.nextElement());}
          for (i = 0; i < groupingCriteria.size(); i++)
              {cc.addGroupingCriterion(groupingCriteria.elementAt(i));}
          for (i = 0; i < pathSpecs.size(); i++)
              {cc.addPathSpec(pathSpecs.elementAt(i));}
          cc.setCaseNumbers(caseNumbers);
          cc.setMultiplicity(mult);
          cc.setTracing(codeTracing);
          return cc;
      }

      // make a deep copy and append to the vector of whenValues
      public codingContext fixWhenValues(Vector<whenValue> whens) throws MDLWriteException
      {
          codingContext cc = copyCC();
          for (int i = 0; i < whens.size(); i++) try
          {
              whenValue wv = (whenValue)whens.elementAt(i);
              cc.setWhenValue(wv);
          }
          catch (Exception e)
              {codeError("fatal",rootPath(),"Vector in fixWhenValues is not a vector of when-values","");}
          return cc;
      }

      public Xpth latestPathSpec()
      {
          Xpth ps = null;
          if (pathSpecs.size() > 0)
            {ps = pathSpecs.elementAt(pathSpecs.size()-1);}
          return ps;
      }

      /*
      */
      public Xpth latestOuterDefinitePathSpec()  throws XpthException
      {
          Xpth ps = null;
          if (pathSpecs.size() > 0)
            {ps = pathSpecs.elementAt(pathSpecs.size()-1).outerDefinitePart();}
          return ps;
      }

      public boolean isStillDefinite()  throws XpthException
      {
          boolean res = true;
          if (pathSpecs.size() > 0)
            {res = pathSpecs.elementAt(pathSpecs.size()-1).definite();}
          return res;
      }

      public boolean alreadyHasPathSpec(Xpth ps)
      {
          boolean res = false;
          for (int i = 0; i < pathSpecs.size(); i++)
          {
              Xpth pt = pathSpecs.elementAt(i);
              if (pt.equalPath(ps)) res = true;
          }
          return res;
      }

    // message for errors detected in code generation
    void codeError(String type, Xpth p, String s1, String s2) throws MDLWriteException
    {
        String preface = "Fatal error";
        if (type.equals("warning")) {preface = "Warning";}
        else if (type.equals("warningCheck")){preface = "Warning";}
        else if (type.equals("check")){preface = "Error";}
        else if (type.equals("bad")){preface = "Error";}
        message("");
        message(preface + " at node '" + p.stringForm() + "': ");
        message(s1);
        if (!(s2.equals(""))) message(s2);
        if (type.equals("fatal")) throw new MDLWriteException("Fatal error terminated code generation.");
    }

  //------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------

      void message(String s) {System.out.println(s);}

}

