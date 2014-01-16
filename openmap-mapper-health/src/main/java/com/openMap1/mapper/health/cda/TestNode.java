package com.openMap1.mapper.health.cda;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.openMap1.mapper.core.MapperException;

/**
 * Represents a node in a tree of connectives AND, OR or NOT
 * which the test attribute of a Schematron <assert> element represents, 
 * as part of an assertion
 * 
 * @author robert
 *
 */
public class TestNode {
	
	private boolean tracing = false;
	
	// Possible connectives for a node
	public static int AND = 0;
	public static int OR = 1;
	public static int NOT = 2;
	public static int COUNT = 3;
	public static int POSITION = 4;
	public static int STRING_EQUALITY = 5;
	public static int NODE_EQUALITY = 6;
	public static int XPATH = 7;
	public static int STEP = 8;
	public static int CONTAINS = 9;
	public static int STRING_LENGTH = 10;
	public static int UNKNOWN = 11;
	
	private String[] connective = {"AND","OR","NOT","COUNT",
			"POS","STRING=","NODE=","XPATH","STEP",
			"CONTAINS","STRING_LENGTH","UNKNOWN"};
	
	// possible axes for a step
	public static int UNDEFINED = 0;
	public static int SELF = 1;
	public static int ANCESTOR = 2;
	public static int PARENT = 3;
	public static int CHILD = 4;
	public static int DESCENDANT = 5;
	public static int ANY = 6;
	public static int ROOT = 7;
	public static int ATTRIBUTE = 8;
	
	private String[] axisText = {"UNDEFINED","SELF","ANCESTOR",
			"PARENT","CHILD","DESCENDANT","ANY","ROOT","ATTRIBUTE"};
	
	/**
	 * @return for a STEP TestNode, the axis of the step
	 */
	public int axis() {return axis;}
	private int axis = UNDEFINED;
	
	/**
	 * @return for a STEP TestNode, the node test 
	 */
	public String nodeTest() {return nodeTest;}
	private String nodeTest = "node()"; // the node test that always passes
	
	/**
	 * @return for a STEP TestNode, 
	 * true if the name of the node is constrained in this step
	 */
	public boolean hasNodeTest()
	{
		if (nodeTest.equals("node()")) return false;
		if (nodeTest.equals("*")) return false;
		return true;
	}
	
	/**
	 * @return for an EQUALITY TestNode , the String value that the node at the end of the XPath is equal to
	 */
	public String rhsValue() {return rhsValue;}
	private String rhsValue = "";
	
	/**
	 * @return for a COUNT TestNode, the integer that the count of nodes is compared to
	 */
	public int integerConstant() {return integerConstant;}
	private int integerConstant = 0;
	
	/**
	 * @return for a COUNT TestNode, the relation used to compare the number of nodes with the integer constant.
	 * Can be '=', '<=' , '>=' , '<' , '>' 
	 */
	public String relation() {return relation;}
	private String relation = "";
	
	// this TestNode has one connector connecting its child TestNodes
	public int connector() {return connector;}
	private int connector = UNKNOWN;  // until proved otherwise
	
	/**
	 * @return  child nodes connected by the connector , or otherwise used (e.g. steps of an XPath)
	 */
	public Vector<TestNode> childNodes() {return childNodes;}
	private Vector<TestNode> childNodes = new Vector<TestNode>();
	
	public int children() {return childNodes().size();}
	
	/**
	 * @return a child TestNode
	 */
	public TestNode child(int c) {return childNodes().get(c);}
	
	public String stringForm() {return stringForm;}
	private String stringForm;
	
	//-------------------------------------------------------------------------------------------
	//                                      Constructors
	//-------------------------------------------------------------------------------------------
	
	public TestNode(String test)
	throws MapperException
	{
		trace("Test Node '" + test + "'");
		stringForm = test;
		// strip off outer brackets and spaces 
		String stripped = stripBrackets(test);

		// deal with outermost 'and' or 'or'
		if (handleOuterANDOR(stripped)) {}
		
		// deal with outermost 'not'
		else if (handleNOT(stripped)) {}
		
		// specific constructs such as count, contains, '='
		else if (handleCondition(stripped)) {}
		
		//parse an XPath with  one or more steps
		else handleXPath(stripped);
	}
	
	/**
	 * Constructor when the test string is known to contain just a single step of an XPath
	 * @param test
	 * @param knownToBeStep should always be true
	 * @throws MapperException
	 */
	public TestNode(String test, boolean knownToBeStep) throws MapperException
	{
		trace ("Single step TestNode '" + test + "'");
		stringForm = test;
		// strip off outer brackets and spaces
		String stripped = stripBrackets(test);
		
		handleSingleStep(stripped);
	}
	
	//-------------------------------------------------------------------------------------------
	//                           Access methods testing structure
	//-------------------------------------------------------------------------------------------
	
	public boolean isLeaf() {return (childNodes.size() == 0);}
	
	/**
	 * true if this assertion helps define the set of nodes the rule applies to
	 * (1) an XPath self::(nodeName), (possibly followed by [] conditions on descendant nodes)
	 * (2) an XPath parent::(nodeName)
	 * (3) an XPath ../(nodeName)
	 * (4) an XPath ancestor::(nodeName)
	 * (5) an AND of OR of TestNodes , some of which are XPATHs with first axis SELF, PARENT or ANCESTOR
	 */
	public boolean definesNode()
	{
		if ((connector == XPATH) && !isLeaf())
		{
			TestNode step0 = childNodes.get(0);
			if (childNodes.size() == 1)
			{
				if (step0.axis == SELF) return true; // there may be [] conditions after the self test
				if (step0.axis == PARENT) return true;
				if (step0.axis == ANCESTOR) return true;
			}
			if (childNodes.size() == 2)
			{
				TestNode step1 = childNodes.get(0);
				if ((step0.axis == PARENT) && step0.isLeaf()&& 
					(step1.axis == CHILD) && step1.isLeaf()) return true;
				
			}
		}
		
		// check for any AND of terms; if one of them is defining ,the combination is defining
		else if (connector == AND)
		{
			for (int c = 0; c < childNodes.size();c++)
			{
				TestNode child = childNodes.get(c);
				if (child.definesNode()) return true;
			}
		}
		
		// check for any OR of terms; if they are all defining ,the combination is defining
		else if (connector == OR)
		{
			boolean defining = true;
			for (int c = 0; c < childNodes.size();c++)
			{
				TestNode child = childNodes.get(c);
				defining = defining &&  (child.definesNode()); 
			}
			return defining;
		}
		return false;
	}
	
	/**
	 * @return true if this assertion leads to some structural constraints on the subtree
	 * beneath its context node
	 * (1) constrains the string values of some nodes found by definite paths
	 * (2) constrains the min cardinality to be 1 down some definite path
	 * (3) constrains the max cardinality to be 1 down some definite path
	 */
	public boolean constrainsSubtree()
	{
		if (constrainsStringValues()) return true;
		if (constrainsMinCardinality()) return true;
		if (constrainsMaxCardinality()) return true;
		return false;
	}
	
	/**
	 * @return true if this TestNode constrains the string values of a number of 
	 * fully defined nodes in a 'narrow tree' down from the context node. Two cases:
	 * (1) The main XPath (trunk of the tree) must be made only of CHILD steps.
	 * Each step may have any number of [] constraints on it. At least one of 
	 * these [] constraints must consist another 'pure child' path, 
	 * leading to a node whose value is equal to a  string constant
	 * (2) the TestNode is a STRING_EQUALITY with a pure child XPsth
	 */
	public boolean constrainsStringValues()
	{
		boolean constrainsValues = false;
		
		/* (1) the path must have only CHILD steps, but some of them must have 
		 * constraints which set one value on a descendant node 
		 * (other constraints might allow two or more values; ignore them) */
		if ((isPureChildPath(false)) && (!isPureChildPath(true)))
		{
			constrainsValues = true;
			int constrainedValues = 0;
			for (int s = 0; s < childNodes.size();s++)
			{
				TestNode step = childNodes.get(s); // because of isPureChildPath(), known to be a STEP with axis CHILD
				for (int c = 0; c < step.childNodes.size(); c++)
				{
					TestNode constraint = step.childNodes.get(c);
					if (constraint.connector() == STRING_EQUALITY)
					{
						TestNode sidePath = constraint.childNodes.get(0); // path to the constrained node
						if (sidePath.isPureChildPath(true)) constrainedValues++;
					}
				}
			}
			if (constrainedValues == 0) constrainsValues = false; // no simple constraints have been found			
		}
		
		// (2) STRING_EQUALITY of a pure child path
		else if (connector == STRING_EQUALITY)
		{
			TestNode xpath = childNodes.get(0);
			constrainsValues = xpath.isPureChildPath(true);
		}
		return constrainsValues;
	}
	
	/**
	 * @return all the constraints that this assertion implies on values of attributes
	 */
	public Vector<AttributeValueConstraint> getStringValueConstraints()
	{
		Vector<String> associationPath = new Vector<String>();
		return getStringValueConstraints(associationPath);		
	}
	
	/**
	 * @return all the constraints that this assertion implies on values of attributes
	 * @param contextFromTemplate TestNode expressing the context of the rule from the template node;
	 * this is an XPATH whose first CHILD step checks the templateID element
	 */
	public Vector<AttributeValueConstraint> getStringValueConstraints(TestNode contextFromTemplate)
	{
		Vector<String> associationPath = new Vector<String>();
		Vector<AttributeValueConstraint> constraints = new Vector<AttributeValueConstraint>();
		if (contextFromTemplate.isPureChildPath(false))
		{
			// ignore the first step, which only checks the template id
			for (int s = 1; s < contextFromTemplate.childNodes().size();s++)
			{
				TestNode step = contextFromTemplate.childNodes().get(s);
				associationPath.add(step.nodeTest());
			}
			constraints =  getStringValueConstraints(associationPath);				
		}
		return constraints;
	}
	
	/**
	 * @return all the constraints that this assertion implies on values of attributes
	 * @param associationPath the path from the template node to the rule node
	 */
	private Vector<AttributeValueConstraint> getStringValueConstraints(Vector<String> associationPath)
	{
		Vector<AttributeValueConstraint> constraints = new Vector<AttributeValueConstraint>();

		/* Case (1): the path must have only CHILD steps, but some of them must have 
		 * constraints which set one value on a descendant node 
		 * (other constraints might allow two or more values; ignore them) */
		if ((isPureChildPath(false)) && (!isPureChildPath(true)))
		{
			for (int s = 0; s < childNodes.size();s++)
			{
				TestNode step = childNodes.get(s); // because of isPureChildPath(), known to be a STEP with axis CHILD
				associationPath.add(step.nodeTest);
				for (int c = 0; c < step.childNodes.size(); c++)
				{
					TestNode constraint = step.childNodes.get(c);
					if (constraint.connector() == STRING_EQUALITY)
					{
						TestNode sidePath = constraint.childNodes.get(0); // path to the constrained node
						if (sidePath.isPureChildPath(true)) 
						{
							AttributeValueConstraint avc = new AttributeValueConstraint(associationPath,constraint.rhsValue);
							for (int d = 0; d < sidePath.childNodes.size(); d++)
							{
								TestNode node = sidePath.childNodes.get(d);
								if (node.axis == CHILD) avc.addAssociationStep(node.nodeTest());
								else if (node.axis == ATTRIBUTE) avc.setAttName(node.nodeTest());
							}
							constraints.add(avc);
						}
					}
				}
			}
		}
		
		// Case (2): STRING_EQUALITY of a pure child path
		else if (connector == STRING_EQUALITY)
		{
			TestNode xpath = childNodes.get(0);
			if (xpath.isPureChildPath(true)) 
			{
				AttributeValueConstraint avc = new AttributeValueConstraint(associationPath,rhsValue);
				for (int d = 0; d < xpath.childNodes.size(); d++)
				{
					TestNode node = xpath.childNodes.get(d);
					if (node.axis == CHILD) avc.addAssociationStep(node.nodeTest());
					else if (node.axis == ATTRIBUTE) avc.setAttName(node.nodeTest());
				}
				constraints.add(avc);							
			}
		}
		return constraints;
	}


	/**
	 * @return true if this assertion constrains the min cardinality to be 1
	 * down some path from the node
	 * (1) An XPath made of only child steps, with no substructure on any step, defines that the
	 * final node of the path must exist
	 * (2) a COUNT on an XPATH with all child steps, no constraint on any step, which rules out maxOccurs = many
	 */
	public boolean constrainsMinCardinality()
	{
		// a pure child XPath implies the final node must exist
		if (isPureChildPath(true))  return true;
		// a COUNT which rules out zero, based on a pure child XPath (possibly with conditions on the steps)
		if (rulesOutZero())
		{
			TestNode path = childNodes.get(0);
			if (path.isPureChildPath(false)) return true;
		}
		return false;		
	}
	
	/**
	 * @param context the full context path to a final node
	 * @param ruleStep the step number of the context to which the rule applies
	 * @return true if the rule implies the final node must exist, for certain specialised form of assertion:
	 * (1) an XPath of all CHILD steps (which may have [] conditions; these do not affect the test)
	 * (2) a COUNT > 0 of an XPath of all CHILD steps (which may have [] conditions)
	 */
	public boolean finalNodeMustExist(TemplatedPath context, int ruleStep)
	{
		// if the rule is on the final step of the context, it cannot imply that node exists
		if (ruleStep < context.length() -1)
		{
			// if the test is an XPath, every step to the final node must match names
			if (connector == XPATH)
			{
				return matchesContext(context, ruleStep);
			}
			// if a COUNT rules out zero, its XPath must match names on every step to the final node
			else if ((connector == COUNT) && (rulesOutZero()))
			{
				return childNodes().get(0).matchesContext(context, ruleStep);				
			}
		}
		return false;
	}
	
	/**
	 * @param context the full context path to a final node
	 * @param ruleStep the step number of the context to which the rule applies
	 * @return true if the rule implies the final node must be single, for certain specialised form of assertion:
	 * (2) a COUNT < N of an XPath of all CHILD steps (which cannot have [] conditions)
	 */
	public boolean finalNodeMustBeSingle(TemplatedPath context, int ruleStep)
	{
		// if the rule is on the final step of the context, it cannot imply that node exists
		if (ruleStep < context.length() -1)
		{
			// if a COUNT rules out many, its XPath must match names on every step to the final node
			if ((connector == COUNT) && (rulesOutMany()))
			{
				TestNode xpath = childNodes().get(0);
				boolean constrains = xpath.matchesContext(context, ruleStep);
				/* If any of the steps of the XPath has [] conditions, the 
				 * XPath does not constrain max cardinality */
				for (Iterator<TestNode> ic = xpath.childNodes().iterator();ic.hasNext();)
					if (ic.next().childNodes().size() > 0) constrains = false;
				return constrains;
			}
		}
		return false;
	}
	
	/**
	 * @param context a CDAContext
	 * @param ruleStep step at which the rule applies
	 * @return for an XPATH, true if every step of the XPath as far as the final step of the context
	 * is a CHILD and matches the name of the context step.
	 * The XPath may or may not go beyond the context. 
	 */
	public boolean matchesContext(TemplatedPath context, int ruleStep)
	{
		boolean matches = true;
		// if the XPath will run out of steps before the end of the context, it cannot match
		if (ruleStep + childNodes().size() < context.length()-1)  matches = false;
		// iterate over context steps which must be matched, to the final node
		else for (int s = ruleStep + 1; s < context.length();s++)
		{
			ContextStep step = context.step(s);
			int assertStep = s - ruleStep -1; // steps 0...N of the XPath
			TestNode pathStep = childNodes().get(assertStep);
			if (pathStep.axis() !=  CHILD) matches = false; // must be all CHILD or ATTRIBUTE steps
			if (!(pathStep.nodeTest().equals(step.associationName()))) matches = false;
		}
		return matches;		
	}
	
	/**
	 * @param context a CDAContext
	 * @param ruleStep step at which the rule applies
	 * @return for an XPATH, true if every step of the XPath as far as the final step of the context
	 * is a CHILD and matches the name of the context step, and 
	 * EITHER the XPath goes beyond the context to constrain a node in the subtree below it.
	 * OR the XPath has a [] on the last step of the context 
	 */
	public boolean extendsContext(TemplatedPath context, int ruleStep)
	{
		/* (ruleStep + childNodes().size() == context.length()-1) is the case
		 * where the assertion XPath ends on the final node of the context  - 
		 * e.g. ruleStep = 0, childNodes().size() = 1, context.length() = 2 */ 
		if ((matchesContext(context,ruleStep)))
		{
			// if the rule's XPath goes beyond the context
			if (ruleStep + childNodes().size() > context.length()-1) return true;
			
			// if the rule's final step (at the end of the context) has a [] condition
			else if (childNodes.get(childNodes().size()-1).childNodes().size() > 0) return true;
		}
		return false;
	}
	
	/**
	 * @return true if this assertion constrains some node or nodes in the subtree
	 * below the final node of the context. Cases included:
	 * (1) XPath with all child steps matching the context, and some steps beyond it 
	 * (2) Multiplicity-constraining COUNT of such an XPath
	 * (3) String equality of some XPath
	 */
	public boolean constrainsSubtreeBeneath(TemplatedPath context, int ruleStep)
	{
		if (connector == STRING_EQUALITY) 
			return childNodes().get(0).extendsContext(context, ruleStep);
		else if ((connector == COUNT) && (constrainsMaxCardinality()|constrainsMinCardinality()))
			return childNodes().get(0).extendsContext(context, ruleStep);
		else if (connector == XPATH) 
			return extendsContext(context, ruleStep);
		else return false;
	}


	/**
	 * @return true if this assertion constrains the max cardinality to be 1
	 * down some path from the node
	 * (1) a COUNT on an XPATH with all child steps, no constraint on any step, which rules out maxOccurs = many
	 */
	public boolean constrainsMaxCardinality()
	{
		// a COUNT which rules out many, based on a pure child XPath
		if (rulesOutMany())
		{
			TestNode path = childNodes.get(0);
			if (path.isPureChildPath(true)) return true;
		}
		return false;		
	}
	
	/**
	 * @return true if a COUNT constraint does not allow zero
	 */
	public boolean rulesOutZero()
	{
		if (connector == COUNT)
		{
			if ((relation.equals("=")) && (integerConstant > 0)) return true;
			if ((relation.equals(">=")) && (integerConstant > 0)) return true;
			if (relation.equals(">")) return true;
		}
		return false;
	}
	
	/**
	 * @return true if a COUNT constraint does not allow greater than 1
	 */
	public boolean rulesOutMany()
	{
		if (connector == COUNT)
		{
			if ((relation.equals("=")) && (integerConstant < 2)) return true;
			if ((relation.equals("<=")) && (integerConstant < 2 )) return true;
			if ((relation.equals("<")) && (integerConstant < 3 )) return true;
		}
		return false;
	}
	
	/**
	 * @param bareSteps if true, each step is required to have no constraints on it
	 * @return true if this is an XPATH with all steps having axis CHILD,
	 * except for the first step which may be SELF (as long as there are other steps)
	 * (a) if bareSteps = true, no step can have any other [] constraints 
	 * (b) if bareSteps = false, any step can have other [] constraints 
	 */
	public boolean isPureChildPath(boolean bareSteps)
	{
		boolean pureChild = false;
		if ((connector== XPATH) && !isLeaf())
		{
			pureChild = true;
			for (int c = 0; c < childNodes.size(); c++)
			{
				TestNode step = childNodes.get(c);
				if (!((step.axis == CHILD)|
					  (step.axis == ATTRIBUTE)|
					  ((c == 0) && (step.axis == SELF))))   pureChild = false;
				if ((bareSteps) && (!step.isLeaf())) pureChild = false;
			}
		}
		return pureChild;
	}
	
	//-------------------------------------------------------------------------------------------
	//                           References to other templates
	//-------------------------------------------------------------------------------------------
	
	public void addTemplateReferences (Vector<String> templateRefs)
	{
		String templateIdName = TemplateRule.CDA_PREFIX + ":templateId";
		// check this node; find the outer XPATH containing the [] condition on its last step
		if ((connector == XPATH) && (childNodes.size() > 0))
		{
			TestNode lastStep = childNodes.get(childNodes.size() - 1);
			// find the last step and check it has some [] conditions
			if (lastStep.childNodes.size() > 0) for (int c = 0; c < lastStep.childNodes.size(); c++)
			{
				TestNode equality = lastStep.childNodes.get(c);
				// find the [] condition
				if (equality.connector == STRING_EQUALITY)
				{
					// find the XPath inside the [] and check it has some steps
					TestNode innerPath = equality.childNodes.get(0);
					if ((innerPath.connector == XPATH) && (innerPath.childNodes.size() > 0))
					{
						// find the last step and check it is '@root'
						TestNode lastInnerStep = innerPath.childNodes.get(innerPath.childNodes.size() - 1);
						if ((lastInnerStep.axis == ATTRIBUTE) & (lastInnerStep.nodeTest().equals("root")))
						{
							boolean templateConstraint = false;
							// if there is a previous step on the inner XPath it must be 'templateId'
							if (innerPath.childNodes.size() > 1)
							{
								TestNode nextInnerStep = innerPath.childNodes.get(innerPath.childNodes.size() - 2);
								templateConstraint = nextInnerStep.nodeTest().equals(templateIdName);
							}
							// otherwise the last step of the outer path must be 'templateId'
							else
							{
								templateConstraint = lastStep.nodeTest().equals(templateIdName);								
							}
							if (templateConstraint) templateRefs.add(equality.rhsValue);							
						}
					}
				}
			}
		}
		
		// recursive descent checking all nodes
		for (Iterator<TestNode> it = childNodes.iterator(); it.hasNext();)
			it.next().addTemplateReferences(templateRefs);
	}
	
	//-------------------------------------------------------------------------------------------
	//                           Methods directly supporting constructors
	//-------------------------------------------------------------------------------------------
	
	/**
	 * @param test test string
	 * @return process the test string, looking for any 'and' or 'or' at the outer level,
	 * not inside any square or round brackets.
	 * If any are found and they don't clash with one another, make the child TestNodes
	 * of this TestNode and return true. 
	 * Otherwise return false, so the test String can be re-analysed.
	 * @throws MapperException if not 'and and 'or' are found at the outer level.
	 */
	private boolean handleOuterANDOR(String test)
	throws MapperException
	{
		String segmentText = "";
		String previousSegmentText = "";
		StringTokenizer st = new StringTokenizer(test," ()[]|",true);
		int roundBracketDepth = 0;
		int squareBracketDepth = 0;
		while (st.hasMoreTokens())
		{
			previousSegmentText = segmentText;
			String token = st.nextToken();
			segmentText = segmentText + token;
			
			// keep track of bracket nesting depth; 'and' or 'or' inside brackets don't count
			if (token.equals("(")) roundBracketDepth++;
			if (token.equals(")")) roundBracketDepth--;
			if (token.equals("[")) squareBracketDepth++;
			if (token.equals("]")) squareBracketDepth--;

			// things to do if you are at the outer level, not just collecting text for some inner level
			if ((roundBracketDepth ==0) & (squareBracketDepth == 0))
			{
				if ((token.equals("or"))|(token.equals("|")))
				{
					if (!mayBeOR()) throw new MapperException("Mixed AND and OR in expression '" + test + "'");
					connector = OR;
					addChildNode(previousSegmentText);
					segmentText = "";
				}				

				if (token.equals("and"))
				{
					if (!mayBeAND()) throw new MapperException("Mixed AND and OR in expression '" + test + "'");
					connector = AND;
					addChildNode(previousSegmentText);
					segmentText = "";
				}				
			} // end of if (outer) section
		} // end of loop over tokens
		
		// if any outer 'and' or 'or' was found, add the last  child TestNode and return true.
		boolean andOrFound = ((connector == AND)|(connector == OR));
		if (andOrFound) 
		{
			addChildNode(segmentText);	
			trace(" completed AND_OR '" + test + "'");
		}
		return andOrFound;
	}
	
	/**
	 * @param test
	 * @return if the test string is of the form "not()", add the child node for what is negated
	 * and return true.
	 * Otherwise return false to the test string can be analysed as an XPath
	 */
	private boolean handleNOT(String test) throws MapperException
	{
		String negatedText = "";
		// FIXME: what about new lines?
		StringTokenizer st = new StringTokenizer(test," ()[]",true);
		int roundBracketDepth = 0;
		int squareBracketDepth = 0;
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			negatedText = negatedText + token;
			
			// keep track of bracket nesting depth; 'not' inside brackets doesn't count
			if (token.equals("(")) roundBracketDepth++;
			if (token.equals(")")) roundBracketDepth--;
			if (token.equals("[")) squareBracketDepth++;
			if (token.equals("]")) squareBracketDepth--;

			// to do if you are at the outer level, not just collecting text for an inner level
			if ((roundBracketDepth ==0) && (squareBracketDepth == 0))
			{
				if (token.equals("not"))
				{
					connector = NOT;
					negatedText = ""; // start the negated text - usually has brackets
				}
			} // end of outside () section
		} // end of loop over tokens
		
		boolean notFound = (connector == NOT);
		if (notFound) 
		{
			addChildNode(negatedText);
			trace("recognised NOT '" + test + "'");
		}
		return notFound;		
	}
	
	/**
	 * @param stripped a String
	 * recognise a condition, whose form is either <XPath> = 'String' or 
	 * count(<XPath>) relation integer.
	 * In either case
	 * @return
	 */
	private boolean handleCondition(String stripped) throws MapperException
	{
		trace("trying condition '" + stripped + "'");
		boolean relationFound = false;

		// test for initial 'count' or  'position'
		String[] intFunction = {"contains","count","position","string-length"};
		int[] intConnector = {CONTAINS,COUNT,POSITION,STRING_LENGTH};
		for (int i = 0; i < intFunction.length; i++)
		{
			if (stripped.startsWith(intFunction[i])) // one of the strings above
			{
				connector = intConnector[i];
				if (i == 0) relationFound = true; // 'contains' requires no relation such as '='
				StringTokenizer st = new StringTokenizer(stripped.substring(intFunction[i].length())," ()",true);
				String inBrackets = "";
				String previousInBrackets = "";
				int bracketLevel = 0;
				int charsRead = intFunction[i].length(); // accounts for 'count', 'position' etc.
				while (st.hasMoreTokens())
				{
					String token = st.nextToken();
					previousInBrackets = inBrackets;
					inBrackets = inBrackets + token;
					charsRead = charsRead + token.length();
					
					// build up and process the expression in brackets
					if (token.equals("("))
					{
						bracketLevel++;
						if (bracketLevel == 1) inBrackets = "";
					}
					if (token.equals(")"))
					{
						bracketLevel--;
						if (bracketLevel == 0) 
						{
							addChildNode(previousInBrackets); // may be empty - if so, no node added
							String remainder = stripped.substring(charsRead);
							trace("Remainder: " + remainder);
							relation = "";
							StringTokenizer rm = new StringTokenizer(remainder,"=<> ",true);
							while (rm.hasMoreTokens())
							{
								String bit = rm.nextToken();
								if (bit.equals(" ")){}
								else if (bit.equals("="))
								{
									if (relation.equals(">")) relation = ">=";
									else if (relation.equals("<")) relation = "<=";
									else relation = "=";
								}
								else if (bit.equals(">")) relation = bit;
								else if (bit.equals("<")) relation = bit;
								else try
								{
									trace("Integer: " + bit);
									integerConstant = new Integer(bit).intValue();
								}
								catch (Exception ex) 
								{
									relation=""; // to set relationFound false later
									// don't want to throw an exception on a 'count = count' constraint
									// throw new MapperException("Found no integer constant in '" + stripped + "'");
								}
							} // end of loop over remainder tokens

							relationFound = ((!relation.equals(""))|(i==0)); // 'contains' needs no relation
							// if (!relationFound) {System.out.println("Found no relation in '" + stripped + "'");}
						} // end of bracketLevel = 0 section					
					}// end of token = ')' section				
				}  // end of loop over tokens
			} // end of section where string starts with 'contains', 'count' etc			
		} // end of loop over i to look for 'count' and 'position'
		
		// otherwise, look for an '=' not in square brackets
		if (connector == UNKNOWN)
		{
			StringTokenizer st = new StringTokenizer(stripped,"[]=", true);
			int bracketLevel = 0;
			String xPath = "";
			while (st.hasMoreTokens())
			{
				String token = st.nextToken();
				xPath = xPath + token;
				if (token.equals("[")) bracketLevel++;
				if (token.equals("]")) bracketLevel--;
				if ((bracketLevel ==0) && (token.equals("="))) 
				{
					relationFound = true;
					xPath = xPath.substring(0,xPath.length()-1); // strip off final '='
					addChildNode(xPath);
					String remainder = stripped.substring(xPath.length() + 1);
					try {
						rhsValue = stripQuotes(remainder);
						connector = STRING_EQUALITY;						
					}
					catch(Exception ex) // remainder has no quotes
					{
						addChildNode(remainder);
						connector = NODE_EQUALITY;												
					}
					trace("Equality between '" + xPath + " ' and '" + remainder + "'");
				}
			}
		}

		if (!relationFound)  trace ("No relation found in '" + stripped + "'");
		return relationFound;
	}
	
	/**
	 * @param stripped a String with no outer 'and', 'or' , 'not' or round brackets,
	 * and which is not an equality or a count relation
	 * parse it as an XPath, of steps separated by outer '/' (not inside [])
	 */
	private void handleXPath(String stripped) throws MapperException
	{
		trace("XPATH '" + stripped + "'");
		connector = XPATH;
		StringTokenizer st = new StringTokenizer(stripped,"/[]",true);
		String stepText = "";
		String previousStepText = "";
		int squareBracketDepth = 0;

		// initial './/' denotes a descendant step from the current node
		if (stripped.startsWith(".//"))
		{
			childNodes.add(new TestNode("descendant::node()",true)); // true mean it must be a step
			st.nextToken();st.nextToken();st.nextToken(); // consume the '.' and two '/'
		}
		// initial '//' denotes a descendant step from the root node
		else if (stripped.startsWith("//"))
		{
			childNodes.add(new TestNode("ROOT",true)); // true means it must be a step
			childNodes.add(new TestNode("descendant::node()",true)); // true mean it must be a step
			st.nextToken();st.nextToken(); // consume the two '/'
		}
		// initial '/' not followed by '/' denotes a ROOT step
		else if (stripped.startsWith("/"))
		{
			childNodes.add(new TestNode("ROOT",true)); // true means it must be a step
			st.nextToken(); // consume the '/'			
		}
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			previousStepText = stepText;
			stepText = stepText + token;
			
			// keep track of bracket nesting depth; '/' inside square brackets doesn't count
			if (token.equals("[")) squareBracketDepth++;
			if (token.equals("]")) squareBracketDepth--;

			// to do if you are at the outer level, not just collecting text for an inner level
			if ((squareBracketDepth ==0) && (token.equals("/")))
			{
				// two consecutive '/' are a descendant step
				if (previousStepText.equals("")) previousStepText = "descendant::node()";
				childNodes.add(new TestNode(previousStepText,true));
				stepText = ""; // re-initialise text for the next step
			} // end of 'outside any square bracket' section
		} // end of loop over tokens
		
		// add the last step (which might be the first)
		childNodes.add(new TestNode(stepText,true));
	}
	
	/**
	 * Handle a single step. 
	 * Text is the node test, followed by any number of [].
	 * Set variables for the axis and node name, with child TestNode objects for each [] test.
	 * @param stripped
	 */
	private void handleSingleStep(String stripped) throws MapperException
	{
		trace("STEP '" + stripped + "'");
		connector = STEP;
		
		// special case where '/' was found at the start of the XPath
		if (stripped.equals("ROOT"))
		{
			axis = ROOT;
			return;
		}

		StringTokenizer st = new StringTokenizer(stripped,"[]", true);
		int squareBracketDepth = 0;
		String preBracket = st.nextToken(); // text before the first '['
		handleNodeTest(preBracket);

		String bracketText = "";
		String previousBracketText = "";
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			previousBracketText = bracketText;
			bracketText = bracketText + token;
			if (token.equals("[")) 
			{
				squareBracketDepth++;
				// start collecting text inside an outer bracket
				if (squareBracketDepth == 1) bracketText = "";
			}
			else if (token.equals("]")) 
			{
				squareBracketDepth--;
				// process text inside an outer bracket
				if (squareBracketDepth == 0) addChildNode(previousBracketText);
			}
		}
	}
	
	/**
	 * 
	 * @param preBracket the initial part of an Path step, with any trailing '[]' or '/' removed
	 */
	private void handleNodeTest(String preBracket)
	{
		if (preBracket.equals("*")) axis = CHILD; // leave nodeTest as 'node()'
		else if (preBracket.startsWith("self::")) 
		{
			axis = SELF;
			nodeTest = preBracket.substring(6);
		}
		else if (preBracket.equals(".")) axis = SELF; // leave nodeTest as 'node()'
		else if (preBracket.equals("..")) axis = PARENT; // leave nodeTest as 'node()'
		else if (preBracket.startsWith("parent::")) 
		{
			axis = PARENT;
			nodeTest = preBracket.substring(8);
		}
		else if (preBracket.startsWith("ancestor::")) 
		{
			axis = ANCESTOR;
			nodeTest = preBracket.substring(10);
		}
		else if (preBracket.startsWith("descendant::")) 
		{
			axis = DESCENDANT;
			nodeTest = preBracket.substring(12);
		}
		else if (preBracket.startsWith("child::")) 
		{
			axis = CHILD;
			nodeTest = preBracket.substring(7);
		}
		else if (preBracket.startsWith("@")) 
		{
			axis = ATTRIBUTE;
			nodeTest = preBracket.substring(1);
		}
		else
		{
			axis = CHILD;
			nodeTest = preBracket;
		}
	}

	
	//-------------------------------------------------------------------------------------------
	//                                  Utility Methods
	//-------------------------------------------------------------------------------------------

	
	/**
	 * add a child TestNode if there is any test to make it from
	 */
	private void addChildNode(String text) throws MapperException
	{
		if (!text.equals("")) childNodes.add(new TestNode(text));
	}
	
	private boolean mayBeOR() {return ((connector == UNKNOWN)|(connector == OR));}
	private boolean mayBeAND() {return ((connector == UNKNOWN)|(connector == AND));}
	
	/**
	 * Strip outer round brackets, square brackets, and any spaces outside them from a test string
	 * For this, it needs to detect whether an initial '(' is matched at any time before
	 * the final ')'; and only remove the two if they are not matched in between;
	 * i.e not strip the outer brackets from  '(fred) and (joe)'
	 * @param test
	 * @return
	 */
	private String stripBrackets(String test)
	{
		char first = ' ';
		char last = ' ';
		int start = 0; // to be the position of first non-blank character
		int end = test.length() - 1; // to be the position of last non-blank character
		boolean firstNonBlank = false;
		boolean lastNonBlank = false;
		for (int c = 0; c < test.length(); c++)
		{
			char early = test.charAt(c);
			char late = test.charAt(test.length() - 1 - c);
			// find the first non-blank character and its position
			if ((!firstNonBlank) && (early != ' '))
			{
				first = early;
				start = c;
				firstNonBlank = true;
			}
			// find the last non-blank character and its position
			if ((!lastNonBlank) && (late != ' '))
			{
				last = late;
				end = test.length() - 1 - c;
				lastNonBlank = true;
			}
		}
		
		// at this stage, going from start to end+1 would trim blanks off the outside
		
		// if the first and last non-blank characters are brackets of the same kind, test if they really match
		if (((first == '(') && (last == ')'))|
			((first == '[') && (last == ']')))
		{
			boolean matching = true;
			int bracketDepth = 0;
			// test all characters from the first open bracket to just before the last closing bracket
			for (int c = start; c < end; c++)
			{
				if (test.charAt(c) == first) bracketDepth++;
				if (test.charAt(c) == last) bracketDepth--;
				if (bracketDepth == 0) matching = false;
			}
			// if the bracket depth never goes zero between the two end brackets, trim them off.
			if (matching) {start++; end--;}
		}
		
		String result = test;
		if (firstNonBlank) result = test.substring(start,end + 1);
		return result;
	}
	
	/**
	 * @param remainder a String that contains two double or single quotes
	 * @return the string between the two '"'
	 */
	private String stripQuotes(String remainder) throws MapperException
	{
		int start = -1;
		int end = 0;
		for (int i = 0; i < remainder.length();i++) 
		{
			char c = remainder.charAt(i);
			if ((c == '"')|(c == '\''))
			{
				if (start == -1) start = i;
				else end = i;				
			}
		}
		if ((start == -1)|(end == 0)) throw new MapperException("Cannot find two quotes in '" + remainder + "'");
		return remainder.substring(start + 1, end);
	}
	
	//-----------------------------------------------------------------------------------------------
	//                     Testing a STEP TestNode against a ContextStep
	//-----------------------------------------------------------------------------------------------
	
	public boolean isCompatible(ContextStep contextStep)
	{
		// this must be a step 
		if (connector() == STEP) 
		{
			// if its name is defined, it must have the same name as the context step
			if ((nodeTest.equals("node()"))|(nodeTest().equals(contextStep.associationName())))
			{
				boolean compatible = true;
				
				/* if this step has any other [] tests, they can only be String equalities on 
				 * attributes, which are exactly duplicated in the context step */
				for (int c = 0; c < childNodes().size(); c++)
				{
					TestNode child = childNodes().get(c);
					boolean childOK = false;
					if (child.connector() == STRING_EQUALITY)
					{
						TestNode path = child.childNodes().get(0);
						// the XPATH can have only one step which is an attribute
						if (path.childNodes().size() == 1)
						{
							TestNode pathStep = path.childNodes().get(0);
							if (pathStep.axis == ATTRIBUTE)
							{
								String[] fv = new String[2];
								fv[0] = pathStep.nodeTest(); // attribute name
								fv[1] = child.rhsValue(); // fixed value it must have
								// this step is OK only if the context step requires the same value for the same attribute
								childOK = contextStep.hasFixedValue(fv);
							}
						}
					}
					if (!childOK) compatible = false;
				}
				return compatible; // OK if all the tests in this step have been matched in the context step
			}			
		}
		return false;
		
	}
	
	
	//-----------------------------------------------------------------------------------------------
	//                                          trivia
	//-----------------------------------------------------------------------------------------------
	
	private void trace(String s) {if (tracing) System.out.println(s);}
	
	/**
	 * @return the structure tree of this TestNode
	 */
	public String structure()
	{
		String structure = connective[connector]; // 'AND', 'XPATH', etc.
		if (connector == STEP) structure = axisText[axis]; // in stead of STEP, give the axis PARENT, CHILD, etc.

		if (childNodes.size() > 0)
		{
			structure = structure + "[";
			for (int i = 0; i < childNodes.size(); i++)
			{
				structure = structure + childNodes.get(i).structure();
				if (i < childNodes.size() - 1) structure = structure + ",";
			}
			structure = structure + "]";
		}
		return structure;
	}

}
