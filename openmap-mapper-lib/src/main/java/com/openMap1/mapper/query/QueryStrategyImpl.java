package com.openMap1.mapper.query;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import com.openMap1.mapper.AssocMapping;
import com.openMap1.mapper.ObjMapping;
import com.openMap1.mapper.core.ClassSet;
import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.mapping.AssociationMapping;
import com.openMap1.mapper.mapping.objectMapping;
import com.openMap1.mapper.mapping.propertyConversion;
import com.openMap1.mapper.mapping.propertyMapping;
import com.openMap1.mapper.reader.MDLXOReader;
import com.openMap1.mapper.reader.XOReader;

public class QueryStrategyImpl implements QueryStrategy{

    private QueryParser parser;
    
    private Vector<Vector<QueryClass>> allStrategies = new Vector<Vector<QueryClass>>();
    
    private Vector<QueryClass> bestStrategy; 


//----------------------------------------------------------------------------------
//                             Constructor
//----------------------------------------------------------------------------------

    /** a query strategy consists just of the order in which the various
    classes in the query are visited.
    */
    public QueryStrategyImpl(QueryParser qParse)
    {
    	this.parser = qParse;
    }

	//----------------------------------------------------------------------------------
	//	                             Defining the Strategy
	//----------------------------------------------------------------------------------
    
    public void defineStrategy() throws MapperException, QueryStrategyException
    {
    	Vector<Vector<QueryClass>> current = firstPartialStrategy(parser.queryClasses());
    	
    	// progressively make all merges of groups which can be made from link associations only
    	boolean progress = true;
    	while (progress)
    	{
    		Vector<Vector<QueryClass>> next = growGroups(current);
    		progress = (next.size() < current.size());
    		current = next;
    	}
    	
    	// if there is only one list of  QueryClasses connected by link associations, that list is the strategy.
    	if (current.size() == 1) bestStrategy = current.get(0);
    	
    	/* if there is more than one list of  QueryClasses connected by link associations, 
    	 * merge the lists in order and check you have got them all */
    	else if (current.size() > 1)
    	{
    		bestStrategy = mergeInFollowOrder(current);
    		if (bestStrategy.size() < parser.queryClasses().size())
    		{
    			String error = ("Best strategy has " + bestStrategy.size() + " classes out of " + parser.queryClasses().size());
    			message(error);
    			throw new MapperException(error);
    		}
    	}
    }
    
    /**
     * refine a partial query strategy, by grouping the query classes into fewer and larger groups
     * @param previous
     * @return
     */
    private Vector<Vector<QueryClass>> growGroups(Vector<Vector<QueryClass>> previous) throws MapperException
    {
    	Vector<Vector<QueryClass>> result = new Vector<Vector<QueryClass>>();
    	
    	for (int g = 0; g < previous.size();g++)
    	{
			boolean found = false;
    		Vector<QueryClass> thisGroup = previous.get(g);
    		LinkAssociation link = linkLeadingTo(thisGroup.get(0));
    		
    		/* there is a link leading to the head of this group. See if that link comes from any previous group in the result */
    		if (link != null)
    		{
    			QueryClass start = link.startClass();
    			for (int gg = 0; gg < result.size(); gg++) if (!found)
    			{
    				Vector<QueryClass> resultGroup = result.get(gg);
    				for (int c = 0; c < resultGroup.size();c++) if (!found)
    				{
    					QueryClass resClass = resultGroup.get(c);
    					if (resClass.matches(start))
    					{
    						found = true;
    						// merge this group on the tail of the result group
    						for (int i = 0; i < thisGroup.size();i++) resultGroup.add(thisGroup.get(i));
    					}
    				}
    			}
    		}
    		
    		/* this group has not been merged to the tail of any other group, so is still a distinct group. 
    		 * See if the heads of any existing groups in the result are targets of links from this group. 
    		 * More than one group may pass this test, but only the first to pass is merged. 
    		 * Others will be caught on later calls of growGroups.*/
			for (int gg = 0; gg < result.size(); gg++) if (!found)
			{
				Vector<QueryClass> resultGroup = result.get(gg);
				LinkAssociation resultLink = linkLeadingTo(resultGroup.get(0));
				if (resultLink != null)
				{
	    			QueryClass start = resultLink.startClass();
	    			for (int t = 0; t < thisGroup.size(); t++) if (!found)
	    			{
	    				QueryClass cand = thisGroup.get(t);
	    				if (cand.matches(start))
	    				{
	    					found = true;
	    					// merge the result group on the tail of this group
	    					for (int r = 0; r < resultGroup.size();r++)  thisGroup.add(resultGroup.get(r));
	    					// remove the result group from the result (OK as we will not iterate further over the result)
	    					result.remove(resultGroup);
	    					//add this enlarged group to the result
	    					result.add(thisGroup);
	    				}
	    			}
				}
			}
			
			/* this group has not been merged with any other group, so add it to the list of groups */
			if (!found) result.add(thisGroup);   		
    	}
    	
    	return result;
    }
    
    /**
     * @param queryClasses
     * @return a Vector of Vectors of QueryClasses,
     * where each inner Vector has one element
     */
    private Vector<Vector<QueryClass>> firstPartialStrategy(Vector<QueryClass> queryClasses)
    {
    	Vector<Vector<QueryClass>>  noStrat = new Vector<Vector<QueryClass>>();
    	for (int c = 0; c < queryClasses.size();c++)
    	{
    		Vector<QueryClass> vq = new Vector<QueryClass>();
    		vq.add(queryClasses.get(c));
    		noStrat.add(vq);
    	}
    	return noStrat;
    }
    
    /**
     * FIXME - this algorithm does not yet work for hops backwards in the list - 
     * an esoteric case; but might need to increase the multi-pass limit
     * @param groups
     * @return
     */
    private Vector<QueryClass> mergeInFollowOrder(Vector<Vector<QueryClass>> groups)
    {
    	// set up the first group as seed, to grow a merged list of all groups from
    	Vector<QueryClass> result = groups.get(0);
    	boolean[] isMerged = new boolean[groups.size()];
    	isMerged[0] = true;
    	for (int g = 1; g < groups.size();g++) isMerged[g] = false;
    	
    	// can only merge new groups if they connect to already merged groups. Multi-pass may be needed
    	for (int pass = 0; pass < 3; pass++)
    	{
        	for (int g1 = 0; g1 < groups.size(); g1++) if (isMerged[g1])
        	{
        		// group1 has already been merged into the result
        		Vector<QueryClass> group1 = groups.get(g1);
        		// try all different groups g2 which have not yet been merged
            	for (int g2 = 0; g2 < groups.size(); g2++) if ((g2 != g1) && (!isMerged[g2]))
            	{
            		Vector<QueryClass> group2 = groups.get(g2);
            		// if group 1 can follow group 2, and group 2 has not yet been merged, put group 2 first
            		if (canFollow(group1,group2))
            		{
            			isMerged[g2] = true;
            			result = vAppend(group2,result);
            		}
            		// if group 2 can follow group 1, and group 2 has not yet been merged, put group 2 last
            		else if (canFollow(group2,group1))
            		{
            			isMerged[g2] = true;
            			result = vAppend(result,group2);
            		}
            	}    		
        	}
    	}
    	return result;
    }
    
    private Vector<QueryClass> vAppend(Vector<QueryClass> v1, Vector<QueryClass> v2)
    {
    	Vector<QueryClass> res = v1;
    	for (int i = 0; i < v2.size();i++) res.add(v2.get(i));
    	return res;
    }
    
    
    /**
     * 
     * @param group1
     * @param group2
     * @return true is there is a cross-condition linking the head of group 1 to any class in group 2, 
     * so that group1 may follow group2 in the strategy list
     */
    private boolean canFollow(Vector<QueryClass> group1, Vector<QueryClass> group2)
    {
    	boolean isConnected = false;
    	for (int i = 0; i < parser.conditions().size();i++)
    	{
    		QueryCondition qc = parser.conditions().get(i);
    		if ((!qc.isConstantCondition()) && (qc.relation().equals("=")))
    		{
    			// see if this cross-condition links the head class of group1 to  any class of group2
    			for (int g2 = 0; g2 < group2.size(); g2++)
    			{
        			if ((qc.queryClass().matches(group1.get(0))) && (qc.otherQueryClass().matches(group2.get(g2)))) isConnected = true;
        			if ((qc.queryClass().matches(group2.get(g2))) && (qc.otherQueryClass().matches(group1.get(0)))) isConnected = true;
    			}
    		}
    	}
    	return isConnected;
    }


	/**
	 * define a query strategy - method superseded.
	 */
    public void defineStrategy_old() throws MapperException, QueryStrategyException {
    	
    	// if the natural ordering of query classes from the parser is a viable strategy, use it
    	if (isViableStrategy(parser.queryClasses()))
    	{
    		bestStrategy = new Vector<QueryClass>();
    		for (int i = 0; i < parser.queryClasses().size();i++) 
    			bestStrategy.add(parser.queryClasses().get(i));
    	}
    	
    	// otherwise, do an exhaustive search of all strategies (may be costly with many query classes)
    	else
    	{
    		// message("Exhaustive search of all query strategies for " + parser.queryClasses().size() + " query classes");
        	// find all possible strategies
    		for (Iterator<QueryClass> it = parser.queryClasses().iterator(); it.hasNext();)
    		{
    			QueryClass startClass = it.next();
    			Vector<QueryClass> partialStrategy = new Vector<QueryClass>();
    			partialStrategy.add(startClass);
    			extendStrategy(partialStrategy);
    		}
    		
    		// choice of best strategy - currently just choose the first
    		if (allStrategies.size() > 0)
    		{
    			bestStrategy = allStrategies.get(0);
    		}
    		else throw new QueryStrategyException("Could not find a viable query strategy linking all classes");
    	}
    		
	}
    
    /**
     * a query strategy (an ordering of the QueryClass objects) is a viable strategy if 
     * (1) the first queryClass has no LinkAssociations leading to it
     * (2) for every other QueryClass in the strategy, the one LinkAssociation leading to it comes from 
     * another QueryClass which has already appeared earlier in the strategy
     * @param strategy
     * @return
     */
    private boolean isViableStrategy(Vector<QueryClass> strategy) throws MapperException
    {
    	boolean viable = true;
    	// the first class in the strategy must have no links leading to it
    	if (linkLeadingTo(strategy.get(0)) != null) viable = false;

    	// all later classes in the strategy must have a link from an earlier class in the strategy
    	else for (int i = 1; i < strategy.size();i++) if (viable)
    	{
    		LinkAssociation link = linkLeadingTo(strategy.get(i));
    		if (link == null) 
    		{
    			message("Query class " + strategy.get(i).identifier() + " has no links to it in strategy " + strategyString(strategy));
    			viable = false;
    		}
    		else
    		{
        		boolean foundStart = false;
        		for (int k = 0; k < i; k++)
        		{
        			if (link.startClass().equals(strategy.get(k))) foundStart = true;
        		}
        		if (!foundStart) viable = false;
    		}
    	}
    	return viable;
    }
    
    /**
     * 
     * @param qc
     * @return the one link association leading to a query class - or null if there are none
     * @throws MapperException
     */
    private LinkAssociation linkLeadingTo(QueryClass qc) throws MapperException
    {
    	int foundLinks = 0;
    	LinkAssociation leadingTo = null;
    	for (Enumeration<LinkAssociation> en = parser.linkAssociations().elements();en.hasMoreElements();)
    	{
    		LinkAssociation next = en.nextElement();
    		if (next.endClass().equals(qc))
    		{
    			foundLinks++;
    			if (foundLinks > 1) 
    				throw new MapperException("Query class " + qc.identifier() + " has more than one link association leading to it");
    			else leadingTo = next;
    		}
    	}
    	return leadingTo;
    }
	
	/**
	 * recursive extension of a query strategy to include new query classes
	 * @param partialStrategy
	 */
    private void extendStrategy(Vector<QueryClass> partialStrategy)
	{
    	// message("Extending strategy " + summary(partialStrategy));
    	
    	// if the partial strategy is complete, save it as a candidate strategy
		if (partialStrategy.size() == parser.queryClasses().size()) allStrategies.add(partialStrategy);

		// otherwise recursively extend it by one class at a time
		else
		{
			// try all classes already in the strategy
			for (Iterator<QueryClass> it = partialStrategy.iterator();it.hasNext();)
			{
				QueryClass nextClass = it.next();
				// find an association from this class to a class not yet in the strategy
				for (Enumeration<LinkAssociation> en = parser.linkAssociations().elements();en.hasMoreElements();)
				{
					LinkAssociation la = en.nextElement();
					if (la.startClass().equals(nextClass))
					{
						QueryClass endClass = la.endClass();
						// keep on extending the extended strategy, if the new class is not already in the strategy
						if (!inStrategy(endClass,partialStrategy))
							extendStrategy(addOneClass(partialStrategy,endClass));
					}
				}
			}
		}
	}
    
    /**
     * @param aClass
     * @param classes
     * @return true if the class aClass is already in the partial strategy strategy
     */
    private boolean inStrategy(QueryClass aClass, Vector<QueryClass> classes)
    {
    	boolean inStrategy = false;
    	for (Iterator<QueryClass> it = classes.iterator();it.hasNext();)
    		if (it.next().equals(aClass)) inStrategy = true;
    	return inStrategy;
    }
    
    /**
     * 
     * @param strategy
     * @param nextClass
     * @return a new partial strategy , got by adding the class to the strategy
     */
    private Vector<QueryClass> addOneClass(Vector<QueryClass> strategy, QueryClass nextClass)
    {
    	Vector<QueryClass> newStrategy = new Vector<QueryClass>();
    	for (Iterator<QueryClass> it = strategy.iterator();it.hasNext();)
    		newStrategy.add(it.next());
    	newStrategy.add(nextClass);
    	return newStrategy;
    }


  //----------------------------------------------------------------------------------
  //                                New Access methods
  //----------------------------------------------------------------------------------
    
    /**
     * @return ordered list of QueryClasses for the best strategy
     */
    public Vector<QueryClass> bestStrategy() {return bestStrategy;}
    
    /**
     * @param order
     * @return the single link association leading to the QueryClass at position order in the best strategy
     */
    public LinkAssociation getLink(int order)
    {
    	LinkAssociation link = null;
    	if ((order > 0) && (order < bestStrategy.size()))
    	{
    		QueryClass target = bestStrategy.get(order);
    		for (Enumeration<LinkAssociation> en = parser.linkAssociations().elements();en.hasMoreElements();)
    		{
    			LinkAssociation next = en.nextElement();
    			if (target. equals(next.endClass())) link = next;
    		}
    	}
    	return link;
    }


//----------------------------------------------------------------------------------
//                              Old Access methods
//----------------------------------------------------------------------------------


	public int nClasses() {
		return parser.queryClasses().size();
	}

	public String old_strategyClass(int order) {
		return bestStrategy.get(order).className();
	}

	public String[] old_linkAssociation(String className,
			Vector<String> previousClasses, QueryParser qp)
			throws QueryStrategyException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void message(String s) {System.out.println(s);}
	
	private String summary(Vector<QueryClass> strategy)
	{
		String summary = "";
		for (int i = 0; i < strategy.size(); i++) summary = summary + strategy.get(i).className() + " ";
		return summary;
	}
	
	/**
	 * (used directly by FHIRSearchManager, indirectly in the query tool
	 * @param code
	 * @param mdl
	 * @throws MapperException
	 */
	public void setSubsets(String code, MDLXOReader mdl) throws MapperException
	{
		// find the unique object mapping for the start class of the strategy
		QueryClass startClass = bestStrategy.get(0);
		String startClassName = startClass.className();
		Vector<objectMapping> startMappings = mdl.objectMappings(startClassName);
		if (startMappings.size() != 1) throw new MapperException(startMappings.size() + " mappings for query strategy start class '" + startClassName + "'");
		bestStrategy.get(0).setMapping(code, (ObjMapping)startMappings.get(0).map());
		
		// find mappings for all other classes and link associations in the strategy, by following association names through mappings
		for (int c = 1; c < bestStrategy.size();c++) 
		{
			QueryClass nextClass = bestStrategy.get(c);
			LinkAssociation nextLink = getLink(c);
			// case where this QueryClass is joined to some previous Queryclass in the strategy by a link association
			if (nextLink != null)
			{
				QueryClass sourceClass = nextLink.startClass();
				String currentSubset = sourceClass.getSubset(code);
				// if there is no mapping to the source class, do not try to follow the link
				if (currentSubset != null)
				{
					// find all mappings between the two classes
					Vector<AssociationMapping> mappings = mdl.getMappings(sourceClass.className(), nextClass.className());
					int found = 0;
					String nextClassName = null;
					String nextSubset = null;
					AssociationMapping assocMap = null;
					// filter mappings by current subset and association name;  0 or 1 mappings should survive
					for (int m = 0; m < mappings.size(); m++)
					{
						AssociationMapping next = mappings.get(m);
						/* test the subset at end 0, and then use the subset at end 1 to find the next class mapping; 
						 * FIXME need to generalise when link associations can be followed from end 1 to end 0*/
						if ((next.assocEnd(0).subset().equals(currentSubset)) && (next.assocName().equals(nextLink.assocName())))
						{
							found++;
							nextClassName = next.assocEnd(1).className();
							nextSubset = next.assocEnd(1).subset();
							assocMap = next;
						}
					}
					if (found > 1) throw new MapperException("Found " + found + " links " 
							+ sourceClass.className() + "." + nextLink.assocName() + "." + nextClass.className());
					if (found == 1) 
					{
						objectMapping om = mdl.namedObjectMapping(new ClassSet(nextClassName,nextSubset));
						nextClass.setMapping(code, (ObjMapping)om.map());	
						nextLink.setMapping(code, (AssocMapping)assocMap.map());
					}
				} // end of case (currentSubset != null)
			} // end of case (nextLink != null)
			
			// case where this QueryClass is joined to some previous QueryClass in the strategy not by a LinkAssociation, but by a cross-condition
			else if (nextLink == null)
			{
				/* in this case there is nothing in the mappings to constrain the subset of the class, 
				 * but there is actually no need to; the query executor will get all objectReps of any subset. */
				Vector<objectMapping> oMaps = mdl.objectMappings(nextClass.className());
				// if (oMaps.size() > 1) throw new MapperException("Found " + oMaps.size() + " object mappings for class " + nextClass.className());
				// I don't think this serves any purpose
				if (oMaps.size() == 1) nextClass.setMapping(code, (ObjMapping)oMaps.get(0).map());
			}
			
		} // end of loop over QueryClasses in the strategy
		
		// find all property mappings for each write field in the query - taking account of  property conversions
		for (int f = 0; f < parser.writeFields().size(); f++)
		{
			WriteField field  = parser.writeFields().get(f);
			String className = field.queryClass().className();
			String subset = field.queryClass().getSubset(code);
			String propName = field.propName();
			if (subset != null) // do nothing if this object is not mapped in the data source
			{
				ClassSet cSet = new ClassSet(className,subset);
				storeRequiredMappings(field, cSet, propName, mdl, code);
			}
		}
		
		// find all property mappings for each query condition in the query - taking account of  property conversions
		for (int q = 0; q < parser.conditions().size(); q++)
		{
			QueryCondition field  = parser.conditions().get(q);
			
			// code for the LHS of the condition
			String className = field.queryClass().className();
			String subset = field.queryClass().getSubset(code);
			String propName = field.propName();
			if (subset != null) // do nothing if this object is not mapped in the data source
			{
				ClassSet cSet = new ClassSet(className,subset);
				storeRequiredMappings(field, cSet, propName, mdl, code);
			}
			
			// code for RHS of the condition, if variable
			if (!field.isConstantCondition())
			{
				className = field.otherQueryClass().className();
				subset = field.otherQueryClass().getSubset(code);
				propName = field.otherPropName();
				if (subset != null) // do nothing if this object is not mapped in the data source
				{
					ClassSet cSet = new ClassSet(className,subset);
					storeRequiredMappings(field, cSet, propName, mdl, code);
				}
				
			}
		}

		
	}

	
	/**
	 * define the mapping subsets for each queryClass in a DataSource (used in the query tool)
	 */
	public void setSubsets(DataSource ds) throws MapperException
	{
		XOReader reader = ds.getReader();
		String code = ds.getCode();
		// currently can only find subsets in this way for mapped readers; this is sufficient for mapped RDBs.
		if (reader instanceof MDLXOReader)
		{
			MDLXOReader mdl = (MDLXOReader)reader;
			setSubsets(code,mdl);
        }

	}
	
	/**
	 * store all mappings required for a write field, recursing through any property conversions
	 * @param field
	 * @param cSet
	 * @param propName
	 * @param mdl
	 * @param code
	 * @return true if the property mapping is direct, not through property conversions
	 * @throws MapperException
	 */
	private boolean storeRequiredMappings(QueryMappingUser field, ClassSet cSet, String propName, MDLXOReader mdl, String code) throws MapperException
	{
		boolean direct = false;
        Vector<propertyMapping> pms  = mdl.namedPropertyMappings(cSet.className(),cSet.subset(),propName);
        propertyConversion pc = mdl.getInConversion(cSet,propName);
        // when the property is directly mapped in the data source, with no conversions
        if (pms.size() > 0) 
        {
        	/* more than one property mapping can only occur for multiway and choice mappings, which we ignore pro tem */
        	propertyMapping mainPropMapping = pms.get(0);
        	field.addMappings(code, mainPropMapping);
        	direct = true;
        }
        // if this is the result of a property conversion
        else if ((pc != null) && (pc.hasImplementation("Java")) && (pc.canDoJavaConvert()))
        {
            Vector<String> argVect = pc.arguments();
            for (int k = 0; k < argVect.size(); k++)
            {
                String pseudoProp = argVect.elementAt(k);
                storeRequiredMappings(field,cSet,pseudoProp,mdl,code);
            }       	
        }
        return direct;
	}

	
	
	/**
	 * return a String representation of a strategy
	 */
	private String strategyString(Vector<QueryClass> strategy)
	{
		String strat = "[";
		for (int s = 0; s < strategy.size();s++)
		{
			QueryClass qc = strategy.get(s);
			strat = strat + qc.className();
			if (s < strategy.size() -1) strat = strat + ", ";
		}
		strat = strat + "]";
		return strat;
	}
	
	

}
