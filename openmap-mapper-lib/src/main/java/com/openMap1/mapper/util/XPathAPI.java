package com.openMap1.mapper.util;

import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openMap1.mapper.core.MapperException;
import com.openMap1.mapper.core.NamespaceSet;
import com.openMap1.mapper.core.Xpth;
import com.openMap1.mapper.core.namespace;
import com.openMap1.mapper.impl.ElementDefImpl;
import com.openMap1.mapper.mapping.linkCondition;
import com.openMap1.mapper.AssocEndMapping;
import com.openMap1.mapper.ConditionTest;
import com.openMap1.mapper.CrossCondition;
import com.openMap1.mapper.Mapping;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathConstants;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * class used by the XOReader to follow XPaths
 * in the input instance, with various performance tweaks,
 * 
 * @author robert
 *
 */

public class XPathAPI
{
	  
	  /** 
	   * version of selectNodeVector with a time for performance tuning 
	   * */
	  public static Vector<Node> selectNodeVector(Timer timer, Node contextNode, String str, NamespaceSet context)
	  throws XPathExpressionException,MapperException
	  {
		  timer.start(Timer.XPATH);
		  Vector<Node> nodes = selectNodeVector(contextNode, str, context);
		  timer.stop(Timer.XPATH);
		  return nodes;
	  }

 /**
   * Use an XPath string to select a Vector of nodes.
   * XPath namespace prefixes are resolved from the namespaceNode.
   * 
   * If  the namespace set contains a namespace with prefix "", then
   * that prefix is changed to an non-empty one in the namespace set
   * and in the XPath.
   * This conversion assumes that all element names have form = qualified.
   *
   * @param contextNode The node to start searching from.
   * @param str A valid XPath string.
   * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces; note this argument is not used
   * @return A Vector nodes,  never null.
   */
  public static Vector<Node> selectNodeVector(Node contextNode, String pathString, NamespaceSet context)
    throws XPathExpressionException,MapperException
  {
	  
	  /* if the path ends in the special virtual attribute introduced to track the ordinal
	   * position of an Element beneath its parent, because the attribute does not exist
	   * use the shorter path to the element, and return a Nodelist of the elements. */
	  String path = pathString;
	  if (pathString.endsWith(ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE))
	  {
		  // strip off the virtual attribute name and '/@'
		  int len = pathString.length() - (ElementDefImpl.ELEMENT_POSITION_ATTRIBUTE).length() - 2;
		  if (len < 0) path = "."; // common case when pathString is just '@..'; and len = -1
		  else path = pathString.substring(0, len);
	  }
	  
    // Execute the XPath, and have it return the result, in two distinct cases:
    boolean hasDefaultNamespace = ((context.getByPrefix("") != null) && 
    		(!context.getByPrefix("").URI().equals("no target namespace")));

    // (1) if there is no default namespace in the set
    if (!hasDefaultNamespace)
    {
    	return fastNodeVector(contextNode,path,context);
    }
    
    /* (2) if there is a default namespace, change it to have a prefix, 
     * because XPath does not work without them */
    else if (hasDefaultNamespace)
    {
    	String prefix = context.nonClashPrefix();
    	NamespaceSet newContext = context.withPrefixForDefaultNamespace(prefix);
    	String newPath = new Xpth(context,path).convertPrefixes(newContext).stringForm();
    	return fastNodeVector(contextNode,newPath,newContext);
    }
    return new Vector<Node>();
  }
  
  /**
   * retrieve the nodes at the end of an XPath , treating certain cases without
   * using XPath, as a speedup
   * @param contextNode
   * @param str
   * @param context
   * @return
   * @throws MapperException
   * FIXME - I don't think this implementation is careful enough about namespaces 
   */
  private static Vector<Node> fastNodeVector(Node contextNode, String str, NamespaceSet context)
  throws MapperException
  {
	  Vector<Node> nodes = new Vector<Node>();
	  // don't need to follow the trivial 'stay still' XPath
	  if ((str.equals("."))|(str.equals("self::node()"))) nodes.add(contextNode);

	  else
	  {
		  boolean ascending = ((str.startsWith("parent"))||
				  				(str.startsWith("ancestor"))||
				  				(str.startsWith("..")));

		  StringTokenizer s1 = new StringTokenizer(str,"/");
		  int steps = s1.countTokens();
		  if (str.startsWith("/")) steps = 0; // don't try fast evaluation of absolute paths

		  StringTokenizer s3 = new StringTokenizer(str,"@");
		  boolean hasAttribute = ((s3.countTokens() == 2)|(str.startsWith("@")));

		  boolean elementStart = (contextNode instanceof Element);

		  // For one-step descending XPaths to an Element, with no namespace prefix, fast evaluation 
		  if ((steps == 1) && (!hasAttribute) && (!ascending) && (elementStart))
		  {
			  Vector<Element> els = XMLUtil.namedChildElements((Element)contextNode, noPrefix(str));
			  for (int i = 0; i < els.size(); i++) nodes.add(els.get(i));				  
		  }

		  
		  // For two-step descending XPaths to an Element, with no namespace prefix, fast evaluation 
		  else if ((steps == 2) && (!hasAttribute) && (!ascending) && (elementStart))
		  {
			  String step1 = noPrefix(s1.nextToken());
			  String step2 = noPrefix(s1.nextToken());
			  Vector<Element> children = XMLUtil.namedChildElements((Element)contextNode, step1);
			  for (int k = 0; k < children.size();k++)
			  {
				  Element child = children.get(k);
				  Vector<Element> els = XMLUtil.namedChildElements(child, step2);
				  for (int i = 0; i < els.size(); i++) nodes.add(els.get(i));				  
			  }
		  }
		  
		  
		  /* single step to an attribute; this has been bypassed because it goes wrong when the attribute
		   * is in a namespace
		  else if ((steps == 1) && (hasAttribute) && (!ascending) && (elementStart))
		  {
			  Attr att = ((Element)contextNode).getAttributeNode(str.substring(1));
			  nodes.add(att);
		  }
		  */

		  // All other cases; follow the XPath
		  else try
		  {
			    XPath xp = XPathFactory.newInstance().newXPath();
		        xp.setNamespaceContext(context);
		        NodeList nl = (NodeList)xp.evaluate(str, contextNode, XPathConstants.NODESET);    	
			    for (int i = 0; i < nl.getLength();i++) nodes.add(nl.item(i));		  			  				  
		  }
		 catch (Exception ex) {throw new MapperException(ex.getMessage());}
	  }
	  
	  // diagnostics for mysterious failures
	  boolean tracing = false;
	  if (tracing)
	  {
		  if (nodes.size() > 0) message("FastNodeVector " + str + "; " + nodes.size());
		  else if ((nodes.size() == 0) && (contextNode instanceof Element))
		  {
			  String cands = "";
			  Vector<Element> els = XMLUtil.childElements((Element)contextNode);
			  for (int i = 0; i < els.size(); i++) cands = cands + els.get(i).getLocalName() + "; " ;
			  message("*** Child elements of " + ((Element)contextNode).getLocalName() + ": " + cands
					  + " when looking for " + noPrefix(str));
		  }
	  }
		  
	  return nodes;
  }
  
  private static String noPrefix(String s)
  {
	  StringTokenizer st = new StringTokenizer(s,":");
	  String result = st.nextToken();
	  if (st.hasMoreTokens()) result = st.nextToken();
	  return result;
  }
  


  
  //----------------------------------------------------------------------------------------------
  //      delivering the node set in the presence of indexed link conditions
  //----------------------------------------------------------------------------------------------
  
  /**
   * follow a cross path from a node, where there may be link conditions which have been indexed
   * for efficient retrieval of a small set of nodes.
   * If there are indexes, use one of them to find a small node set, 
   * and then check that each node in the node set can be reached by the XPath.
   * 
   * The link conditions will all need to be tested again, because 
   * (a) only one condition will be used for the indexed retrieval, and
   * (b) sometimes (when there are further conditions on the link condition paths) the indexing
   * lets in some nodes which do not satisfy the link condition used for indexing
   * 
   * @param contextNode the start node of the path
   * @param str string form of the XPath
   * @param context Namespace set - used by XPath to handle e.g. changed namespace prefixes
   * @param parent the parent object in the mapping set;
   * either the mapping for which this is a cross path; and which may have 
   * one or more cross conditions (= link conditions);
   * or  the condition which this is a nested condition on
   * @param pathIsToLHS true if the XPath leads to the node on the LHS of any 
   * cross conditions (for property mappings, this is the property node; for association
   * mappings, this is the association node)
   * @param nodeIndex The set of all nodes in the document, indexed by (1) the XPath
   * from the node to the string value in a link condition (outer key) and the string value 
   * (inner key)
   * @return a set of nodes - should be small - which are reachable by the XPath and are highly likely
   * to satisfy the selected link condition; and include all those which do
   * @throws XPathExpressionException
   */
  public static Vector<Node> indexedSelectNodeVector(Timer timer, Node contextNode, String str, NamespaceSet context,
		  EObject parent, boolean pathIsToLHS, 
		  Hashtable<String,Hashtable<String,Hashtable<String,Vector<Node>>>> nodeIndex)
  throws XPathExpressionException,MapperException
  {
	  timer.start(Timer.INDEXED_TOTAL);
	  
	  /* find out which path from the condition end to a node containing the string value 
	   * (if any) is best to use for the indexed retrieval,
	   * giving the smallest node set; and find its node set*/
	  Vector<Node> nodesForBestIndex = nodesForBestIndex(contextNode, str, context,
			   parent, pathIsToLHS, 
			   nodeIndex);

	  Vector<Node> res = new Vector<Node>();

	  //no useful index was found; just follow the XPath to get all nodes
	  if (nodesForBestIndex == null)
	  {
		  timer.start(Timer.UNINDEXED_XPATH);
		  res = selectNodeVector(contextNode, str, context);
		  timer.stop(Timer.UNINDEXED_XPATH);
	  }
	  
	  //check all nodes in the smallest node set, to see if they can be reached by the XPath
	  else 
	  {
		  timer.start(Timer.INDEXED_XPATH);
		  for (Iterator<Node> it = nodesForBestIndex.iterator();it.hasNext();)
		  { 
			  Node candidate = it.next();
			  if (canReachByPath(contextNode,candidate,str,context)) res.add(candidate);
		  }
		  timer.stop(Timer.INDEXED_XPATH);		  
	  }
	  timer.stop(Timer.INDEXED_TOTAL);
	  return res;	  
  }
  
  /**
   * 
   * @param contextNode the start node of the XPath
   * @param str  string form of the XPath
   * @param context the current set of namespaceset
   * @param parent the parent object in the mapping set;
   * either the mapping for which this is a cross path; and which may have 
   * one or more cross conditions (= link conditions);
   * or  the condition which this is a nested condition on
   * @param pathIsToLHS true if the XPath leads to the node on the LHS of any 
   * cross conditions (for property mappings, this is the property node; for association
   * mappings, this is the association node)
   * @param nodeIndex The set of all nodes in the document, indexed by (1) the XPath
   * from the node to the string value in a link condition (outer key) and the string value 
   * (inner key)
   * @return
   * @throws XPathExpressionException
   * @throws MapperException
   */
  private static Vector<Node> nodesForBestIndex(Node contextNode, String str, NamespaceSet context,
		  EObject parent, boolean pathIsToLHS, 
		  Hashtable<String,Hashtable<String,Hashtable<String,Vector<Node>>>> nodeIndex)
  throws XPathExpressionException,MapperException
  {
	  Mapping mapping = null;
	  if (parent instanceof Mapping) mapping = (Mapping)parent;
	  else throw new MapperException("Cannot yet do indexed node retrievals for nested link conditions");

	  // end (0 for property mappings, 1 or 2 for association ends) is needed to construct linkConditions
	  int end = 0;
	  if (parent instanceof AssocEndMapping) end = ((AssocEndMapping)parent).getEnd();

	  /* find out which path from the condition end to a node containing the string value 
	   * (if any) is best to use for the indexed retrieval,
	   * giving the smallest node set; and find its node set*/
	  int smallestNodeSetFound = -1; 
	  Vector<Node> nodesForBestIndex = new Vector<Node>();
	  for (Iterator<CrossCondition> it = mapping.getCrossConditions().iterator(); it.hasNext();)
	  {
		  CrossCondition crossCondition = it.next();
		  linkCondition linkCond = new linkCondition(crossCondition,end);
		  
		  // find which string value, if any, can be passed to the index
		  String valueSought = null;
		  String indexedRelPath = null;
		  String indexedRootPath = null;

		  /* only consider equality link conditions in which no function 
		   * is applied to the string value of the target node  */
		  if ((pathIsToLHS)&&(crossCondition.getLeftFunction().equals(""))
				  && (crossCondition.getTest().equals(ConditionTest.EQUALS)))
		  {
			  indexedRootPath = linkCond.rootToLHSNode().stringForm();
			  indexedRelPath = linkCond.lhsEndToLeftValue().stringForm();
			  valueSought = linkCond.rightValue(contextNode, context);	
		  }
		  else if ((!pathIsToLHS)&&(crossCondition.getRightFunction().equals(""))
				  && (crossCondition.getTest().equals(ConditionTest.EQUALS)))
		  {
			  indexedRootPath = linkCond.rootToRHSEnd().stringForm();
			  indexedRelPath = linkCond.rhsEndToRightValue().stringForm();
			  valueSought = linkCond.leftValue(contextNode, context);			  
		  }
		  
		  // find how many nodes satisfy the index condition, to choose the smallest set
		  if (valueSought != null)
		  {
			  Hashtable<String,Hashtable<String,Vector<Node>>> indexForAbsPath = nodeIndex.get(indexedRootPath);
			  if (indexForAbsPath != null)
			  {
				  Hashtable<String,Vector<Node>> indexForRelPath = indexForAbsPath.get(indexedRelPath);
				  if (indexForRelPath != null)
				  {
					  Vector<Node> nodesForValue = indexForRelPath.get(valueSought);
					  // if any index eliminates all candidate nodes, return an empty node set
					  if (nodesForValue == null) 
					  {
						  return new Vector<Node>();
					  }

					  int size = nodesForValue.size();
					  // best candidate index so far...
					  if ((smallestNodeSetFound == -1)|(size < smallestNodeSetFound))
					  {
						  smallestNodeSetFound = size;
						  nodesForBestIndex = nodesForValue;
					  }				  
				  }				  
			  }
		  }		  
	  } // end of loop over link conditions on the path

	  if (smallestNodeSetFound == -1) nodesForBestIndex = null;
	  return nodesForBestIndex;
  }
  
  
  /**
   * 
   * @param mapping a property mapping or association end mapping
   * @param pathIsToLHS true if the path leads to the LHS of cross-conditions
   * (= the property node or association node, not the object node)
   * i.e only false for the second leg of following an association mapping
   * @return true if any cross-conditions in the mapping allow the XOReader to use 
   * an index, rather than follow the XPath
   * @throws MapperException
   */
  public static boolean isIndexed(Mapping mapping, boolean pathIsToLHS)
  throws MapperException
  {
	  boolean indexed = false;

	  for (Iterator<CrossCondition> it = mapping.getCrossConditions().iterator(); it.hasNext();)
	  {
		  CrossCondition crossCondition = it.next();

		  /* only consider equality link conditions in which no function 
		   * is applied to the string value of the target node  */
		  if ((pathIsToLHS)&&(crossCondition.getLeftFunction().equals(""))
				  && (crossCondition.getTest().equals(ConditionTest.EQUALS)))
		  {
			  indexed = true;
		  }
		  else if ((!pathIsToLHS)&&(crossCondition.getRightFunction().equals(""))
				  && (crossCondition.getTest().equals(ConditionTest.EQUALS)))
		  {
			  indexed = true;
		  }
		  
	  }
	  return indexed;
  }


  /**
   * 
   * @param start the start node of an XPath
   * @param end the end node of an XPath
   * @param path string form of an XPath
   * @param context the namespace Set
   * @return true if the end node can be reached by the path from the 
   * start node. There are restrictions on the form of the path
   * (which could be removed given time):
   * (1) It must consist of a set of ascending steps (possibly empty)
   * followed by a set of descending steps
   * (2) If the ascending steps contain an 'ancestor' step, it must be the last of them
   * and must define a node name (i.e not be 'ancestor::node()')
   * (3) If the descending steps contain as 'descendant' step, it must be the first of them
   * The test is made in three stages:
   * (a) the initial ascending steps fit
   * (b) both nodes are under the path apex
   * (c) the descending steps work
   */
  public static boolean canReachByPath(Node start, Node end, String pathString, NamespaceSet context)
  throws MapperException
  {
	  Xpth path = new Xpth(context,pathString);
	  int firstDescendingStep = 0;
	  Node current = start;
	  String apexName = current.getNodeName();
	  boolean ascending = true;
	  
	  /* (a) match ascending steps. They should all be 'parent' except the last, 
	   * which may be 'ancestor' (-or-self) */
	  for (int s = 0; s < path.size(); s++)
	  {
		  String axis = path.step(s).axis();
		  String test = path.step(s).nodeTest();
		  if (axis.equals("parent"))
		  {
			  current = parentElement(current);
			  if (current == null) return false; // ran out at the top of the document
			  if ((test.equals("node()"))|(namespaceEqualName(current,test,context))) 
			  	{apexName = current.getNodeName();}
			  else return false; // could not match a parent step
		  }
		  else if (Xpth.ascending(axis))
		  {
			  // fail to match an ancestor step
			  if (!hasAncestorOrSelf(current,test,context)) return false;
			  apexName = test;
		  }
		  else if (Xpth.descending(axis))
		  {
			  if (ascending) firstDescendingStep = s;
			  ascending = false;
		  }
	  }
	  
	  // (b) check the end node is somewhere under the apex node
	  if (!hasAncestorOrSelf(end,apexName,context)) return false;
	  
	  /* (c) check descending steps in reverse order. 
	   * They should all be 'child' except the last of the reverse order */
	  current = end;
	  for (int s = path.size()-1; s > firstDescendingStep-1; s--)
	  {
		  String axis = path.step(s).axis();
		  String test = path.step(s).nodeTest();		  
		  if (Xpth.descending(axis))
		  {
			  // this test works for Elements and attributes
			  if ((test.equals("node()"))|(namespaceEqualName(current,test,context))) 
				  {current = parentElement(current);} // pass; prepare for next step up
			  else return false; // could not match a child or descendant step
		  }
		  else return false; // some other axis, such as 'sibling'
	  }
	  
	  // survived all tests
	  return true; 
  }
  
  /**
   * 
   * @param node
   * @return the parent element of the Node; or null if its parent node
   * is not an Element
   */
  private static Element parentElement(Node node)
  {
	  Node par = null;
	  Element parent = null;
	  if (node instanceof Element) par = node.getParentNode(); 
	  if ((par != null) && (par instanceof Element)) parent = (Element)par;
	  else if (node instanceof Attr) parent = ((Attr)node).getOwnerElement();
	  return parent;
  }
  
  
  /**
   * @param el an node
   * @param name a name
   * @return true if the node, or any of its ancestors, has the name
   */
  private static boolean hasAncestorOrSelf(Node el, String name, NamespaceSet context)
  {
	  if (el == null) return false; // you have run off the top of the document
	  if (namespaceEqualName(el,name,context)) return true;
	  return hasAncestorOrSelf(parentElement(el),name, context);
  }
  
  /**
   * 
   * @param nd a node in a document
   * @param name a node name, using namespace prefixes as in the mapping set (not the document)
   * @param context the set of namespaces with prefixes as in the mapping set
   * @return true if the node name matches the suplied name, taking account of 
   * possible different namespace prefixes in the document and the mapping set
   */
  private static boolean namespaceEqualName(Node nd, String name, NamespaceSet context)
  {
	  /* if the node is in no namespace, or the namespace has no prefix in the mapping set
	   * the converted name will be the local name. */
	  String convertedName = "";
	  if (nd instanceof Element) {convertedName = XMLUtil.getLocalName((Element)nd);}
	  else if (nd instanceof Attr) {convertedName = nd.getLocalName();}
	  if (convertedName == null) {System.out.println("Null local name for node '" + nd.getNodeName() + "'");}
		  

	  String nsURI = nd.getNamespaceURI();
	  // if the node is in a namespace..
	  if (nsURI != null)
	  {
		  namespace ns = context.getByURI(nsURI); // find the namespace in the mapping set
		  // if the namespace URI is not in the namespace set of the mapping set, the names cannot match
		  if (ns == null) return false;
		  /* if the namespace prefix in the mapping set is non-empty, add it before the local name; 
		   * otherwise, use just the local name */
		  if (!ns.prefix().equals("")) convertedName = ns.prefix() + ":" + convertedName;
	  }
	  
	  // compare the converted name with the name from the mapping set
	  return (convertedName.equals(name));
  }
  
  static void message(String s) {System.out.println(s);}
  

}
