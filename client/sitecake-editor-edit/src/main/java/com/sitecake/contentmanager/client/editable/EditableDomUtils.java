package com.sitecake.contentmanager.client.editable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Contains various DOM helper/util methods.
 */
public class EditableDomUtils {

	public static final int STOP_AT_BLOCKLEVEL = 1;
	public static final int STOP_AT_LIST = 2;
	public static final int STOP_AT_LINEBREAK = 4;
	
	public static final String[] FLOW_TAGS = new String[] {
		"a", "b", "blockquote", "br", "button", "canvas", "cite", "code",
		"div", "dl", "em", "embed", "fieldset", "form", "h1", "h2", "h3",
		"h4", "h5", "h6", "header", "hgroup", "hr", "i", "iframe", "img",
		"input", "label", "map", "object", "ol", "p", "pre", "small", 
		"span", "strong", "sub", "sup", "table", "textarea", "ul", "#text"		
	};
	
	public static final String[] PHRASING_TAGS = new String[] {
		"a", "b", "br", "button", "canvas", "cite", "code", 
		"em", "embed", "i", "iframe", "img", "input", 
		"label", "map", "object", "small", "span", "strong", 
		"sub", "sup", "textarea", "#text"		
	};
	
	public static final Map<String, String[]> NESTING_RULES = new HashMap<String, String[]>();
	{
		NESTING_RULES.put("a", PHRASING_TAGS);
		NESTING_RULES.put("b", PHRASING_TAGS);
		NESTING_RULES.put("blockquote", FLOW_TAGS);
		NESTING_RULES.put("button", PHRASING_TAGS);
		NESTING_RULES.put("code", PHRASING_TAGS);
		NESTING_RULES.put("div", FLOW_TAGS);
		NESTING_RULES.put("em", PHRASING_TAGS);
		NESTING_RULES.put("h1", PHRASING_TAGS);
		NESTING_RULES.put("h2", PHRASING_TAGS);
		NESTING_RULES.put("h3", PHRASING_TAGS);
		NESTING_RULES.put("h4", PHRASING_TAGS);
		NESTING_RULES.put("h5", PHRASING_TAGS);
		NESTING_RULES.put("h6", PHRASING_TAGS);
		NESTING_RULES.put("i", PHRASING_TAGS);
		NESTING_RULES.put("li", FLOW_TAGS);
		NESTING_RULES.put("ol", new String[] { "li" });
		NESTING_RULES.put("p", PHRASING_TAGS);
		NESTING_RULES.put("pre", PHRASING_TAGS);
		NESTING_RULES.put("small", PHRASING_TAGS);
		NESTING_RULES.put("span", PHRASING_TAGS);
		NESTING_RULES.put("strong", PHRASING_TAGS);
		NESTING_RULES.put("sub", PHRASING_TAGS);
		NESTING_RULES.put("sup", PHRASING_TAGS);
		NESTING_RULES.put("textarea", new String[] { "#text" });
		NESTING_RULES.put("ul", new String[] { "li" });
	}
	
	protected EditableDomUtils() {
	}
	
	public Text getNextAdjacentTextNode(Node parent, int index) {
		return getAdjacentTextNode(parent, index, 
				STOP_AT_BLOCKLEVEL | STOP_AT_LINEBREAK | STOP_AT_LIST, false);
	}
	
	public Text getNextAdjacentTextNode(Node parent, int index, int stopAt) {
		return getAdjacentTextNode(parent, index, stopAt, false);
	}
	
	public Text getPrevAdjacentTextNode(Node parent, int index) {
		return getAdjacentTextNode(parent, index, 
				STOP_AT_BLOCKLEVEL | STOP_AT_LINEBREAK | STOP_AT_LIST, true);
	}
	
	public Text getPrevAdjacentTextNode(Node parent, int index, int stopAt) {
		return getAdjacentTextNode(parent, index, stopAt, true);
	}
	
	public Text getAdjacentTextNode(Node parent, int index, boolean left) {
		return getAdjacentTextNode(parent, index, 
				STOP_AT_BLOCKLEVEL | STOP_AT_LINEBREAK | STOP_AT_LIST, left);
	}
	
	/**
	 * Starting with the given position (between nodes), search in the given direction to an adjacent notempty text node
	 * @param Node parent parent node containing the position
	 * @param int index index of the position within the parent node
	 * @param boolean searchleft true when search direction is 'left' (default), false for 'right'
	 * @param int stop define at which types of element we shall stop, may be a combination of the following flags:
	 * 				STOP_AT_BLOCKLEVEL, STOP_AT_LIST, STOP_AT_LINEBREAK
	 * 
	 * @return Text the found text node or false if none found
	 */	
	public Text getAdjacentTextNode(Node parent, int index, int stop, boolean left) {
		Text text = null;
		if ( parent != null && Element.is(parent) && index >= 0 && index <= parent.getChildCount() ) {
			text = findAdjacentTextNode(Element.as(parent), index, stop, left);
		}
		return text;
	}
		
	private Text findAdjacentTextNode(Element parent, int index, int stop, boolean left) {
		Node nextNode = null;
		Element currentParent = parent;

		// start at the node left/right of the given position
		if (left && index > 0) {
			nextNode = parent.getChild(index - 1);
		} else if ( !left && index < parent.getChildCount() ) {
			nextNode = parent.getChild(index);
		}

		while ( currentParent != null ) {
			if ( nextNode == null ) {
				// no next node found, check whether the parent is a blocklevel element
				if ( (stop & STOP_AT_BLOCKLEVEL) != 0 && isBlockLevelElement(currentParent) ) {
					// do not leave block level elements
					return null;
				} else if ( (stop & STOP_AT_LIST) != 0 && this.isListElement(currentParent)) {
					// do not leave list elements
					return null;
				} else {
					// continue with the parent
					nextNode = left ? currentParent.getPreviousSibling() : currentParent.getNextSibling();
					currentParent = currentParent.getParentElement();
				}
			} else if ( nextNode.getNodeType() == 3 && Text.as(nextNode).getData().trim().length() > 0 ) {
				// we are lucky and found a notempty text node
				return Text.as(nextNode);
			} else if ( (stop & STOP_AT_BLOCKLEVEL) != 0 && isBlockLevelElement(nextNode) ) {
				// we found a blocklevel element, stop here
				return null;
			} else if ( (stop & STOP_AT_LINEBREAK) != 0 && isLineBreakElement(nextNode) ) {
				// we found a linebreak, stop here
				return null;
			} else if ( (stop & STOP_AT_LIST) != 0 && isListElement(nextNode) ) {
				// we found a list element, stop here
				return null;
			} else if (nextNode.getNodeType() == 3) {
				// we found an empty text node, so step to the next
				nextNode = left ? nextNode.getPreviousSibling() : nextNode.getNextSibling();
			} else {
				// we found a non-blocklevel element, step into
				currentParent = Element.as(nextNode);
				nextNode = left ? currentParent.getLastChild() : currentParent.getFirstChild();
			}
		}
		return null;
	}
	
	private static String[] blockLevelElements = { "div", "h1", "h2", "h3", "h4", "h5", "h6", "p", "pre", "blockquote" };
	
	/**
	 * Tests if an element is a block element.
	 * 
	 * @param element
	 * @return true if the element is a block element
	 */
	public boolean isBlockLevelElement(Node element) {
		if ( element != null && Element.is(element) ) {
			for ( String aTagName : blockLevelElements ) {
				if ( aTagName.equals(Element.as(element).getTagName().toLowerCase()) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Tests if an element is any of list tags (UL, LI or OL).
	 * 
	 * @param element
	 * @return true if the element is a list element
	 */
	public boolean isListElement(Node element) {
		if ( element != null && Element.is(element) ) {
			String tagName = Element.as(element).getTagName().toLowerCase();
			if ( "ul".equals(tagName) || "ul".equals(tagName) || "ul".equals(tagName) ) {
				return true;
			}
		}		
		return false;
	}

	/**
	 * Tests if an element is the line break element (BR tag).
	 * 
	 * @param element
	 * @return true if the element is a BR element
	 */
	public boolean isLineBreakElement(Node element) {
		if ( element != null && Element.is(element) ) {
			String tagName = Element.as(element).getTagName().toLowerCase();
			if ( "br".equals(tagName) ) {
				return true;
			}
		}		
		return false;
	}	
	
	/**
	 * Gets the (zero-based) index of the given node within its parent node
	 * @param Node node node to check
	 * @return int index in the parent node or -1 if no node given or node has no parent
	 */
	public int getNodeIndex(Node node) {
		if ( node == null || node.getParentNode() == null ) {
			return -1;
		}
		
		int index = 0;
		Node siblingNode = node.getPreviousSibling();
		while ( siblingNode != null ) {
			siblingNode = siblingNode.getPreviousSibling();
			index++;
		}
		return index;
	}
	
	/**
	 * Find the highest occurrence of a node with given nodeName within the parents
	 * of the start. When limit objects are given, the search stops there.
	 * The limiting object is of the found type, it won't be considered.
	 * 
	 * @param Node start start object
	 * @param String nodeName name of the node to search for (case-insensitive)
	 * @param Node limit Limiting node (if none given, the search will stop when there are no more parents)
	 * @return Node the found DOM node or null
	 */
	public Node findHighestElement(Node start, String nodeName, Node limit) {
		Node testNode = start;

		// this will be the highest found markup object (up to a limit object)
		Node highestElement = null;

		// now get the highest parent that has the given markup (until we reached
		// one of the limit objects or there are no more parent nodes)
		while ( (limit == null || !limit.equals(testNode)) && testNode != null ) {
			if ( nodeName.equalsIgnoreCase(testNode.getNodeName()) ) {
				highestElement = testNode;
			}
			testNode = testNode.getParentNode();
		}

		return highestElement;
	}
	
	/**
	 * Check whether the HTML 5 (partly) specification allows direct nesting of the given DOM
	 * objects.
	 * 
	 * @param Node outerDOMObject
	 *            outer (nesting) DOM Object
	 * @param Node innerDOMObject
	 *            inner (nested) DOM Object
	 * @return boolean true when the nesting is allowed, false if not
	 */
	public boolean allowsNesting(Node outerDOMObject, Node innerDOMObject) {
		if ( outerDOMObject == null || innerDOMObject == null || outerDOMObject.getNodeName() == null ||
				innerDOMObject.getNodeName() == null ) {
			return false;
		}

		String outerNodeName = outerDOMObject.getNodeName().toLowerCase();
		String innerNodeName = innerDOMObject.getNodeName().toLowerCase();

		if ( !NESTING_RULES.containsKey(outerNodeName) ) {
			return false;
		}
		
		for ( String allowedInnerName : NESTING_RULES.get(outerNodeName) ) {
			if ( allowedInnerName.equals(innerNodeName) ) {
				return true;
			}
		}
		
		return false;		
	}
	
	/**
	 * Returns a list of parent nodes for the given node up to
	 * (but not included) the given <code>body</code> tag. The list 
	 * starts with the inner-most parent.
	 * 
	 * @param node a node which parents will be returned
	 * @return a list of parent nodes
	 */	
	public List<Node> getParents(Node node) {
		return getParents(node, RootPanel.getBodyElement());
	}
	
	/**
	 * Returns a list of parent nodes for the given node up to
	 * (but not included) the given <code>topLimit</code> node.
	 * The list starts with the inner-most parent.
	 * 
	 * @param node a node which parents will be returned
	 * @param topLimit a node that acts as the parent list terminator
	 * @return a list of parent nodes
	 */
	public List<Node> getParents(Node node, Node topLimit) {
		List<Node> parents = new ArrayList<Node>();
		
		Node parent = node.getParentNode();
		while ( parent != null && !parent.equals(topLimit) ) {
			parents.add(parent);
			parent = parent.getParentNode();
		}
		
		return parents;
	}
	
	private static String[] splitableTags = new String[] {
		"p", "li", "h1", "h2", "h3", "h4", "h5", "h6"
	};
	
	/**
	 * Checks if the given element is an element that will
	 * be split if the enter key is pressed in the content-editable mode.
	 * 
	 * @param element an element to be checked
	 * @return true if the element is split-able, false if not
	 */
	public boolean isSplitable(Node element) {
		boolean result = false;
		if ( element.getNodeType() == 1 ) {
			for ( String splitableTag : splitableTags ) {
				if ( splitableTag.equalsIgnoreCase(element.getNodeName()) ) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	private static String[] replacingTags = new String[] {
		"p", "h1", "h2", "h3", "h4", "h5", "h6", "pre"
	};
	
	/**
	 * Checks if the given element is a replacing element, that is,
	 * an element that has to replace existing elements.
	 * 
	 * @param element
	 * @return true if a replacing element
	 */
	public boolean isReplacingElement(Node element) {
		boolean result = false;
		if ( element.getNodeType() == 1 ) {
			for ( String tag : replacingTags ) {
				if ( tag.equalsIgnoreCase(element.getNodeName()) ) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	public List<Node> getTextNodes(Node node) {
		return getTextNodes(node, false, false);
	}
	
	public List<Node> getTextNodes(Node node, boolean excludeBreaks) {
		return getTextNodes(node, excludeBreaks, false);
	}
	
	public List<Node> getTextNodes(Node rootNode, boolean excludeBreaks, boolean includeEmptyTextNodes) {
		List<Node> result = new ArrayList<Node>();
		getTextNodesRecursively(result, rootNode, excludeBreaks, includeEmptyTextNodes);
		return result;
	}
	
	private void getTextNodesRecursively(List<Node> nodes, Node currentNode, boolean excludeBreaks, boolean includeEmptyTextNodes) {
        if ( (currentNode.getNodeType() == 3 && !"".equals(Text.as(currentNode).getData().trim()) && !includeEmptyTextNodes) || 
        		(currentNode.getNodeType() == 3 && includeEmptyTextNodes) || 
        		("br".equalsIgnoreCase(currentNode.getNodeName()) && !excludeBreaks) ) {
            nodes.add(currentNode);
        } else {
            for ( int i = 0; i < currentNode.getChildCount(); i++ ) {
                getTextNodesRecursively(nodes, currentNode.getChild(i), excludeBreaks, includeEmptyTextNodes);
            }
        }
	}
	
	public boolean textLevelComparator(Node aNode, Node bNode) {
		if ( bNode.getNodeType() != 1 ) {
			return false;
		}
		
		String[] classes = null;
		String[] classes2 = null;
		
		String className = Element.as(aNode).getClassName();
		if ( className != null && !"".equals(className) ) {
			classes = className.split(" ");
		}
		
		String className2 = Element.as(bNode).getClassName();
		if ( className2 != null && !"".equals(className2) ) {
			classes2 = className2.split(" ");
		}
		
		if ( ( classes == null && classes2 != null ) || ( classes != null && classes2 == null ) ) {
			return false;
		}
		
		if ( classes != null && classes.length != classes2.length ) {
			return false;
		}
		
		if ( classes != null && classes.length > 0 ) {
			for ( String aClass : classes ) {
				boolean found = false;
				for ( String bClass : classes2 ) {
					if ( aClass.equals(bClass) ) {
						found = true;
						break;
					}
				}
				if ( !found ) {
					return false;
				}
			}
		}
		return true;			
	}
	
	public native JsArray<Node> children(Node node)/*-{
		var result = [];
		
		var children = node.childNodes;
		for ( var i=0; i<children.length; i++ ) {
			result.push(children[i]); 
		}
		
		return result;
	}-*/;
	
	public List<Node> childrenList(Node node) {
		List<Node> result = new ArrayList<Node>();
		for ( int i = 0; i < node.getChildCount(); i++ ) {
			result.add(node.getChild(i));
		}
		return result;
	}
	
}
