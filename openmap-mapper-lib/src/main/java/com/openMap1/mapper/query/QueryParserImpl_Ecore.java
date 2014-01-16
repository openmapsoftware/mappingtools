package com.openMap1.mapper.query;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.openMap1.mapper.util.ModelUtil;

public class QueryParserImpl_Ecore extends QueryParserImpl_Super implements QueryParser{
	
	private EPackage classModel;
	
	
	//------------------------------------------------------------------------------------
	//	                                     Constructor
	//------------------------------------------------------------------------------------

	    
	    /**
	     * 
	     * @param m
	     * @param code
	     * @param errors
	     * @param tracing
	     */
	    public QueryParserImpl_Ecore (EPackage classModel, String code,Vector<String[]> errors, boolean tracing)
	    {
	        super(errors, tracing);

	        this.code = code;
	        this.classModel = classModel;	        
	    }

		
		//------------------------------------------------------------------------------------
		//	                    Parsing methods which depend on the Ecore model
		//------------------------------------------------------------------------------------


	    /** check that a field for writing out in the query is
	    of the form 'class.property', where 'class' is an unqualified name of a mapped class and
	    'property' is one of its properties, direct or inherited.
	    If so, return a three-element vector of (qualified class name,property,'present');

	    If the property is not represented in the Ecore model, return (class,property,'absent') (or null for now);
	    otherwise (if no full stop or class not represented in the Ecore model) return null;

	    The field may also be defined by some chain of associations from a start class. 
	    In this case, note the associations and return the Vector (qualified class name,property,'present')
	    for the last class in the chain.
	    if isCore = true, set the core status of all classes involved.
	    */
		Vector<WriteField> makeWriteFields(String fName, boolean isCore) {
			Vector<WriteField> res= new Vector<WriteField>();

	        // Vector of subfields separated by '.'
	        Vector<String> subFields = stopSeparated(fName);

	        // input field with no '.' is illegal before 'where'
	        if (subFields.size() < 2) {errorMessage("Field for query display should be of the form 'class.property', "
	            + " or 'class.role...property', but '" + fName + "' has no internal full stop.");}

	        // get all candidates for the first class in the chain (packages are not defined in queries)
	        String bareClassName = subFields.elementAt(0);
	        List<EClass> classes = ModelUtil.getAllNamedClasses(classModel, bareClassName);
	        if (classes.size() == 0) errorMessage("There is no class '" + bareClassName + "' in the class model");
	        
	        // the last field is always the property to be shown
            String inPropertyName = subFields.get(subFields.size() - 1);
	        
    		int links = subFields.size() - 2; // number of link associations - may be 0
            boolean foundALinkChain = (links == 0); // starts true if there are no links to find

            // try out all candidate start classes (there will usually be one)
	        for (Iterator<EClass> it = classes.iterator();it.hasNext();)
	        {
	        	EClass startClass  = it.next();
        		EClass nextClass = startClass; // will end up as the final class which has the property
        		boolean foundLinks = true; // remains true if there are no links to find
        		String assocChain = "";
        		QueryClass sourceQueryClass = makeOrFindQueryClass(startClass,assocChain);
        		// note this is guaranteed only for 'longhand' queries, where all write filed start with the start class
        		sourceQueryClass.setCore();
        		
        		// follow the association chain as far as you can
        		for (int link = 0; link < links; link++) if (foundLinks)
        		{
        			// allow for old notation '(ref)class' as well as 'ref'
        			String refName = getRefName(subFields.get(link + 1));
        			assocChain  = assocChain + "." + refName;
        			// follow one link - this does not allow for inherited associations
        			EStructuralFeature next = nextClass.getEStructuralFeature(refName);
        			// success
        			if ((next != null) && (next instanceof EReference))
        			{
        				EReference nextRef = (EReference)next;
        				nextClass = (EClass)nextRef.getEType();
        				QueryClass nextQueryClass = makeOrFindQueryClass(nextClass,assocChain);
        				if (isCore) nextQueryClass.setCore();
        				noteNewLinkAssociation(sourceQueryClass,nextRef,nextQueryClass);
        				sourceQueryClass = nextQueryClass;  //ready for next link in the chain
        			}
        			// failure at this link
        			else foundLinks = false;
        		}
        		
        		if (foundLinks) foundALinkChain = true;
        		// pick up the first class (with the start class name) which has both the association chain and the final property
        		if ((foundLinks) && (res.size() == 0)) 
        		{
        			// '*' stands for all properties of the class (including inherited properties)
        			if (inPropertyName.equals("*"))
        			{
        				for (Iterator<EStructuralFeature> iu = sourceQueryClass.getEClass().getEAllStructuralFeatures().iterator();iu.hasNext();)
        				{
        					EStructuralFeature feat = iu.next();
        					if (feat instanceof EAttribute)
        					{
        						String propName = feat.getName();
        						res.add(new WriteField(sourceQueryClass,propName,this)); 
        					}
        				}
        			}

        			// '**' stands for all properties of the class or any class got by a containment relation
        			else if (inPropertyName.equals("**"))
        			{
        				addAllNestedProperties(res,sourceQueryClass,assocChain);
        			}
 
        			// anything other than '*' or '**' is a single property
        			else 
        			{ 
        				EStructuralFeature feat = sourceQueryClass.getEClass().getEStructuralFeature(inPropertyName);
        				if ((feat != null) && (feat instanceof EAttribute))
        					res.add(new WriteField(sourceQueryClass,inPropertyName,this));   
        				// otherwise res.size() == 0 triggers an error message
        			}
        		}
	        }
	        
	        // detect error conditions, which no choice of start class solves
	        if (!foundALinkChain) errorMessage("Cannot follow the chain of associations in '" + fName + "'");
	        else if (res.size() == 0) errorMessage("Cannot find any final property '" + inPropertyName + "' in '" + fName + "'");
	        
	        return res;
		}
		
		/**
		 * add to the list of writeFields all properties of this class , 
		 * and any class got from it by a containment relation.
		 * For those classes, add the queryClass and the link association if necessary
		 * @param res
		 * @param sourceQueryClass
		 */
		private void addAllNestedProperties(Vector<WriteField>res, QueryClass sourceQueryClass,String assocChain)
		{
			for (Iterator<EStructuralFeature> iu = sourceQueryClass.getEClass().getEAllStructuralFeatures().iterator();iu.hasNext();)
			{
				EStructuralFeature feat = iu.next();
				if (feat instanceof EAttribute)
				{
					String propName = feat.getName();
					res.add(new WriteField(sourceQueryClass,propName,this)); 
				}
				else if (feat instanceof EReference)
				{
					EReference ref = (EReference)feat;
					if (ref.isContainment())
					{
						String newAssocChain = assocChain + "." + ref.getName();
						EClass nextClass = (EClass)ref.getEType();
						
						// note the next QueryClass and the link to it
        				QueryClass nextQueryClass = makeOrFindQueryClass(nextClass,newAssocChain);
        				noteNewLinkAssociation(sourceQueryClass,ref,nextQueryClass);
        				
        				// recursive step to add all WriteFields for the new class etc.
        				addAllNestedProperties(res,nextQueryClass,newAssocChain);
					}
				}

			}
		}
		
		/**
		 * make a new query class and return it, unless a matching query class can be found in the existing set;
		 * in that case return the matching query class
		 * @param baseClass
		 * @param assocChain
		 * @return
		 */
		private QueryClass makeOrFindQueryClass(EClass baseClass, String assocChain)
		{
			QueryClass newClass = new QueryClass(baseClass,assocChain,this);
			QueryClass matchingClass = newClass.matchInList(queryClasses);
			if (matchingClass != null) newClass = matchingClass;
			else queryClasses.add(newClass);
			return newClass;
		}
		
		/**
		 * if propName is a valid property of the class, note that it is to be output;
		 * otherwise return null
		 * @param theClass
		 * @param propName
		 * @return
		 */
		private Vector<String> propertyVect(EClass theClass, String propName)
		{
			Vector<String> res = null;
            // this does not allow for inherited properties
    		EStructuralFeature feat = theClass.getEStructuralFeature(propName);
    		if ((feat != null) && (feat instanceof EAttribute))
    	    {
    	        res = new Vector<String>();
    	        res.addElement(ModelUtil.getQualifiedClassName(theClass));
    	        res.addElement(propName);
    	        res.addElement("present");
    	    }
    		return res;
		}
		

		/**
		 * 
		 * @param sourceQueryClass
		 * @param nextRef
		 * @param nextQueryClass
		 */
	    private void noteNewLinkAssociation(QueryClass sourceQueryClass, EReference nextRef,QueryClass nextQueryClass)
		{
			LinkAssociation link = new LinkAssociation(sourceQueryClass,nextRef,nextQueryClass,this);
			linkAssociations.put(link.key(), link);
		}

		
	    /**
	     * for a link name which is either 'refName' or '(refName)className'
	     * return only the refName
	     * (so there is no check on className)
	     * @param linkName
	     * @return
	     */
	    private String getRefName(String linkName)
		{
			StringTokenizer linkParts = new StringTokenizer(linkName,"()");
			return linkParts.nextToken();
		}

		/**
		 * this is only called when some 'where' conditions are of the form 'Class.relation.class'.
		 * This occurs only rarely, as link associations are usually implied by write fields;
		 * so it has not yet been implemented
		 */
		int handleLinkAssociation(String w1, String w2, String w3,
				Vector<String> after, int pos) {
			// TODO Auto-generated method stub
			return 0;
		}







}
