package com.sitecake.contentmanager.client.editable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.JsStringUtil;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.editable.RangeTreeNode.Type;

/**
 * Represents a selection range in browser.
 * 
 */
public class Range {

	public static final int CLEANUP_MERGE_TEXT = 1;
	public static final int CLEANUP_REMOVE_EMPTY = 2;
	
	protected EditableDomUtils domUtil = GinInjector.instance.getEditableDomUtils();
	
	protected DomSelector domSelector = GinInjector.instance.getDomSelector();
	
	private TagWrappingRules tagWrapping = TagWrappingRules.create();
	
	/**
	 * The DOM text node that represent the start container of
	 * the selection.
	 */
	protected Node startContainer;
	
	/**
	 * The offset of the selection within the start container.
	 */
	protected int startOffset;
	
	/**
	 * The DOM text node that represent the start container of
	 * the selection.
	 */
	protected Node endContainer;

	/**
	 * The offset of the selection within the start container.
	 */
	protected int endOffset;

	/**
	 * An inner helper class that represent an end point.
	 */
	protected class EndPoint {
		public Node container;
		public int offset;

		public EndPoint(Node container, int offset) {
			this.container = container;
			this.offset = offset;
		}
	}
	
	protected Node commonAncestor = null;
	
	protected Map<Node, List<Node>> startContainerParents = new HashMap<Node, List<Node>>();
	
	protected Map<Node, List<Node>> endContainerParents = new HashMap<Node, List<Node>>();
	
	protected Map<Node, List<RangeTreeNode>> rangeTree = new HashMap<Node, List<RangeTreeNode>>();
	
	protected Node splitNode = null;
	
	protected Node topNode = null;
	
	protected List<Node> startMarkup = new ArrayList<Node>();
	
	protected List<Node> unmodifiableStartMarkup = new ArrayList<Node>();
	
	private boolean inSelection;
	
	public Node getStartContainer() {
		return startContainer;
	}

	public void setStartContainer(Node startContainer) {
		this.startContainer = startContainer;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public Node getEndContainer() {
		return endContainer;
	}

	public void setEndContainer(Node endContainer) {
		this.endContainer = endContainer;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public Node getTopNode() {
		return topNode;
	}

	public void setTopNode(Node topNode) {
		this.topNode = topNode;
		// TODO update range
	}

	protected Range() {
	}
	
	/**
	 * Creates a range using the given start and end parameters.
	 * 
	 * @param startNode
	 * @param startOffset
	 * @param endNode
	 * @param endOffset
	 */
	public Range(Node startNode, int startOffset, Node endNode, int endOffset) {
		this.startContainer = startNode;
		this.startOffset = startOffset;
		this.endContainer = endNode;
		this.endOffset = endOffset;
	}

	public static Range create(Node startContainer, int startOffset, Node endContainer, int endOffset) {
		Range range = GWT.create(Range.class);
		range.startContainer = startContainer;
		range.startOffset = startOffset;
		range.endContainer = endContainer;
		range.endOffset = endOffset;
		
		range.normalize();
		
		return range;
	}
	
	/**
	 * Returns a clone of the given range.
	 */
	public Range clone() {
		Range clone = Range.create(startContainer, startOffset, endContainer, endOffset);
		clone.commonAncestor = commonAncestor;
		clone.splitNode = splitNode;
		clone.topNode = topNode;
		clone.rangeTree = rangeTree;
		clone.startMarkup = startMarkup;
		clone.unmodifiableStartMarkup = unmodifiableStartMarkup;
		return clone;
	}
	
	/**
	 * Checks if the range is collapsed. A range is collapsed if the
	 * endContainer is not specified or the start and end containers
	 * are equal and the start and end offset are equal.
	 * 
	 * @return true if the range is collapsed
	 */
	public boolean isCollapsed() {
		return ( endContainer == null || 
				(endContainer.equals(startContainer) && startOffset == endOffset) );		
	}
	
	/**
	 * Normalizes the range setting the range start and end container
	 * in text nodes if possible. 
	 */
	public void normalize() {
		resetCache();
		
		if ( isCollapsed() ) {
			// first check if the range is not in a text node
			if ( startContainer.getNodeType() == 1) {
				if ( startOffset > 0 && !startContainer.hasChildNodes() ) {
					startOffset = 0;
					endOffset = 0;
				}
				
				if ( startOffset > 0 && startContainer.getChild(startOffset - 1).getNodeType() == 3 ) {
					// when the range is between nodes (container is an element
					// node) and there is a text node to the left -> move into this text
					// node (at the end)
					startContainer = startContainer.getChild(startOffset - 1);
					startOffset = Text.as(startContainer).getData().length();
					endContainer = startContainer;
					endOffset = startOffset;
					return;
				}

				if ( startOffset > 0 && startContainer.getChild(startOffset - 1).getNodeType() == 1) {
					// search for the next text node to the left
					Text adjacentTextNode = domUtil.getPrevAdjacentTextNode(startContainer, startOffset);
					if ( adjacentTextNode != null ) {
						startContainer = adjacentTextNode;
						startOffset = adjacentTextNode.getData().length();
						endContainer = startContainer;
						endOffset = startOffset;						
						return;
					}
					// search for the next text node to the right
					adjacentTextNode = domUtil.getNextAdjacentTextNode(startContainer, startOffset);
					if ( adjacentTextNode != null ) {
						startContainer = adjacentTextNode;
						startOffset = 0;
						endContainer = startContainer;
						endOffset = startOffset;						
						return;
					}
				}

				if ( startOffset < startContainer.getChildCount() && startContainer.getChild(startOffset).getNodeType() == 3 ) {
					// when the range is between nodes and there is a text node
					// to the right -> move into this text node (at the start)
					startContainer = startContainer.getChild(startOffset);
					startOffset = 0;
					endContainer = startContainer;
					endOffset = startOffset;
					return;
				}
			}

			// when the selection is in a text node at the start, look for an adjacent text node and if one found, move into that at the end
			if ( startContainer.getNodeType() == 3 && startOffset == 0) {
				Text adjacentTextNode = domUtil.getPrevAdjacentTextNode(startContainer.getParentNode(), domUtil.getNodeIndex(startContainer));
				if ( adjacentTextNode != null ) {
					startContainer = adjacentTextNode;
					startOffset = adjacentTextNode.getData().length();
					endContainer = startContainer;
					endOffset = startOffset;
					return;
				}
			}
		} else {

			// correct the start, but only if between nodes
			if ( startContainer.getNodeType() == 1) {
				// if there is a text node to the right, move into this
				if ( startOffset < startContainer.getChildCount() && 
						startContainer.getChild(startOffset).getNodeType() == 3 ) {
					startContainer = startContainer.getChild(startOffset);
					startOffset = 0;
				} else if ( startOffset < startContainer.getChildCount() && 
						startContainer.getChild(startOffset).getNodeType() == 1 ) {
					// there is an element node to the right, so recursively check all first child nodes until we find a text node
					Node textNode = null;
					Node checkedElement = startContainer.getChild(startOffset);
					while ( textNode == null && checkedElement.hasChildNodes() ) {
						// go to the first child of the checked element
						checkedElement = checkedElement.getFirstChild();
						// when this element is a text node, we are done
						if (checkedElement.getNodeType() == 3) {
							textNode = checkedElement;
						}
					}

					// found a text node, so move into it
					if ( textNode != null ) {
						startContainer = textNode;
						startOffset = 0;
					}
				}
			}

			// check whether the start is inside a text node at the end
			if ( startContainer.getNodeType() == 3 && startOffset == Text.as(startContainer).getData().length() ) {
				// check whether there is an adjacent text node to the right and if
				// yes, move into it
				Text adjacentTextNode = domUtil.getNextAdjacentTextNode(startContainer.getParentNode(), domUtil.getNodeIndex(startContainer) + 1);

				if ( adjacentTextNode != null ) {
					startContainer = adjacentTextNode;
					startOffset = 0;
				}
			}

			// now correct the end
			if ( endContainer.getNodeType() == 3 && endOffset == 0) {
				// we are in a text node at the start
				if ( endContainer.getPreviousSibling() != null && endContainer.getPreviousSibling().getNodeType() == 3 ) {
					// found a text node to the left -> move into it (at the end)
					endContainer = endContainer.getPreviousSibling();
					endOffset = Text.as(endContainer).getData().length();
				} else if ( endContainer.getPreviousSibling() != null && endContainer.getPreviousSibling().getNodeType() == 1 && endContainer.getParentNode() != null ) {
					// found an element node to the left -> move in between
					Node parentNode = endContainer.getParentNode();
					for ( int offset = 0; offset < parentNode.getChildCount(); offset++ ) {
						if ( parentNode.getChild(offset).equals(endContainer) ) {
							endOffset = offset;
							break;
						}
					}
					endContainer = parentNode;
				}
			}

			if ( endContainer.getNodeType() == 1 && endOffset == 0) {
				// we are in an element node at the start, possibly move to the previous sibling at the end
				if ( endContainer.getPreviousSibling() != null ) {
					if ( endContainer.getPreviousSibling().getNodeType() == 3) {
						// previous sibling is a text node, move end into here (at the end)
						endContainer = endContainer.getPreviousSibling();
						endOffset = Text.as(endContainer).getData().length();
					} else if ( endContainer.getPreviousSibling().getNodeType() == 1
							&& endContainer.getPreviousSibling().hasChildNodes() ) {
						// previous sibling is another element node with children,
						// move end into here (at the end)
						endContainer = endContainer.getPreviousSibling();
						endOffset = endContainer.getChildCount();
					}
				}
			}

			// correct the end, but only if between nodes
			if ( endContainer.getNodeType() == 1  ) {
				if ( endOffset > 0 && !endContainer.hasChildNodes() ) {
					endOffset = 0;
				} else if ( endOffset > 0 && endContainer.getChild(endOffset - 1).getNodeType() == 3) {
					// if there is a text node to the left, move into this
					endContainer = endContainer.getChild(endOffset - 1);
					endOffset = Text.as(endContainer).getData().length();
				} else if ( endOffset > 0 && endContainer.getChild(endOffset - 1).getNodeType() == 1) {
					// there is an element node to the left, so recursively check all last child nodes until we find a text node
					Node textNode = null;
					Node checkedElement = endContainer.getChild(endOffset - 1);
					while ( textNode == null && checkedElement.hasChildNodes() ) {
						// go to the last child of the checked element
						checkedElement = checkedElement.getChild(checkedElement.getChildCount() - 1);
						// when this element is a text node, we are done
						if ( checkedElement.getNodeType() == 3) {
							textNode = checkedElement;
						}
					}

					// found a text node, so move into it
					if ( textNode != null ) {
						endContainer = textNode;
						endOffset = Text.as(endContainer).getData().length();
					} else {
						endContainer = checkedElement;
						endOffset = 0;
					}
				}
			}
		}
		
	}
	
	public void resetCache() {
		commonAncestor = null;
		startContainerParents.clear();
		endContainerParents.clear();
		rangeTree.clear();
	}
	
	/**
	 * Returns the common ancestor element/container for both the start and end container.
	 * 
	 * @return the common ancestor element or <code>null</code> if not found
	 */
	public Node getCommonAncestor() {
		if ( commonAncestor == null ) {
			List<Node> startContainerParents = getStartContainerParents();
			List<Node> endContainerParents = getEndContainerParents();

			int startParentsNum = startContainerParents.size();
			int endParentsNum = endContainerParents.size();
			
			// find the crossing
			
			if ( startParentsNum == 0 || endParentsNum == 0 ) {
				return null;
			}
			
			List<Node> shorter = ( startParentsNum < endParentsNum ) ? startContainerParents : endContainerParents;
			List<Node> longer = ( startParentsNum >= endParentsNum ) ? startContainerParents : endContainerParents;
			
			for ( Node aNode : longer ) {
				if ( shorter.contains(aNode) ) {
					commonAncestor = aNode;
					break;
				}
			}
		}
		
		return commonAncestor;
	}
	
	/**
	 * Returns all parents elements of the start container,
	 * including the container itself in case it is not a text node, up to
	 * (but not including) the <code>body</code> tag. The list starts with
	 * the inner-most parent.
	 * 
	 * @return a list of parent nodes
	 */
	public List<Node> getStartContainerParents() {
		return getContainerParents(true, RootPanel.getBodyElement());
	}

	/**
	 * Returns all parents elements of the end container,
	 * including the container itself in case it is not a text node, up to
	 * (but not including) the <code>body</code> tag. The list starts with
	 * the inner-most parent.
	 * 
	 * @return a list of parent nodes
	 */	
	public List<Node> getEndContainerParents() {
		return getContainerParents(false, RootPanel.getBodyElement());
	}
	
	public List<Node> getContainerParents(boolean start) {
		return getContainerParents(start, topNode);
	}
	
	/**
	 * Returns parents elements of either the start or end container,
	 * including the container itself in case it is not a text node, up to
	 * (but not including) the <code>topContainer</code>. The list starts with
	 * the inner-most parent.
	 * The result list is cached.
	 * 
	 * @param start boolean indicates if the start or end container's parents are requested
	 * @param topNode a node that limits the parent list
	 * @return a list of parent nodes
	 */
	public List<Node> getContainerParents(boolean start, Node topNode) {
		Node container = start ? startContainer : endContainer;
		Map<Node, List<Node>> containerParents = start ? startContainerParents : endContainerParents;

		if ( container == null ) {
			return new ArrayList<Node>();
		}

		if ( topNode == null ) {
			topNode = RootPanel.getBodyElement();
		}
		
		if ( !containerParents.containsKey(topNode) ) {
			List<Node> parents = domUtil.getParents(container, topNode);
			
			if (container.getNodeType() != 3) {
				parents.add(0, container);
			}

			containerParents.put(topNode, parents);
		}

		return containerParents.get(topNode);		
	}
	
	/**
	 * Extend the given range to start and end at the nearest word 
	 * boundaries to the left (start) and right (end).
	 */
	public void extendToWord() {
		extendToWord(false);
	}
	
	/**
	 * Extend the given range to have start and end at the nearest word 
	 * boundaries to the left (start) and right (end) taking the
	 * <code>fromBondaries</code> param in account.
	 * 
	 * @param fromBoundaries true if extending will also be done, if one or both ends of the range already are at a word boundary
	 */
	public void extendToWord(boolean fromBoundaries) {
		// search the word boundaries to the left and right
		EndPoint leftBoundary = findWordBoundary(startContainer, startOffset, true);
		EndPoint rightBoundary = findWordBoundary(endContainer, endOffset, false);

		// check whether we must not extend the range from word boundaries
		if ( !fromBoundaries ) {
			// we only extend the range if both ends would be different
			if (startContainer.equals(leftBoundary.container) && startOffset == leftBoundary.offset) {
				return;
			}
			
			if (endContainer.equals(rightBoundary.container) && endOffset == rightBoundary.offset) {
				return;
			}
		}

		// set the new boundaries
		startContainer = leftBoundary.container;
		startOffset = leftBoundary.offset;
		endContainer = rightBoundary.container;
		endOffset = rightBoundary.offset;

		normalize();
		resetCache();		
	}
	
	
	private static final String[] nonWordBoundaryTags = 
		new String[] { "a", "code", "em", "i", "span", "b", "strong", "sub", "sup" };
	
	/**
	 * Helper method to check whether the given DOM object is a word boundary.
	 * 
	 * @param element the node to be examined
	 * @return boolean true when the given node is a word boundary, false if not
	 */
	private boolean isWordBoundaryElement(Node element) {
		if ( element == null || !Node.is(element) ) {
			return false;
		}
		
		// a text node
		if ( element.getNodeType() == 3 ) {
			return false;
		}
		
		// check if it's any of non word boundary tags
		String elementName = element.getNodeName().toLowerCase();
		for ( String tagName : nonWordBoundaryTags ) {
			if ( tagName.equals(elementName) ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Looking for the next word boundary, starting at the given position.
	 * 
	 * @param fromPoint the search start poing
	 * @param directionLeft the search direction
	 * @return the found word boundary
	 */
	private EndPoint findWordBoundary(Node container, int offset, boolean directionLeft) {
		boolean boundaryFound = false;
		int position;

		while ( !boundaryFound ) {
			
			// check the node type
			if (container.getNodeType() == 3) {
				// we are currently in a text node
				Text textNode = Text.as(container);
				String nodeText;
				
				// find the nearest word boundary character
				if ( !directionLeft ) {
					// search right
					nodeText = textNode.getData().substring(offset);
					position = JsStringUtil.search(nodeText, "\\W");
					if (position != -1) {
						// found a word boundary
						offset = offset + position;
						boundaryFound = true;
					} else {
						// found no word boundary, so we set the position after the container
						offset = domUtil.getNodeIndex(container) + 1;
						container = container.getParentNode();
					}
				} else {
					// search left
					nodeText = textNode.getData().substring(0, offset);
					position = JsStringUtil.search(nodeText, "\\W");
					int tempPosition = position;
					while (tempPosition != -1) {
						position = tempPosition;
						String textFragment = textNode.getData().substring(position + 1, offset);
						tempPosition = JsStringUtil.search(textFragment, "\\W");
						if (tempPosition != -1) {
							tempPosition = tempPosition + position + 1;
						}
					}

					if (position != -1) {
						// found a word boundary
						offset = position + 1;
						boundaryFound = true;
					} else {
						// found no word boundary, so we set the position before the container
						offset = domUtil.getNodeIndex(container);
						container = container.getParentNode();
					}
				}
			} else if (container.getNodeType() == 1) {
				// we are currently in an element node (between nodes)

				if ( !directionLeft ) {
					// check whether there is an element to the right
					if ( offset < container.getChildCount() ) {
						// there is an element to the right, check whether it is a word boundary element
						if ( isWordBoundaryElement(container.getChild(offset)) ) {
							// we are done
							boundaryFound = true;
						} else {
							// element to the right is no word boundary, so enter it
							container = container.getChild(offset);
							offset = 0;
						}
					} else {
						// no element to the right, check whether the element itself is a boundary element
						if ( isWordBoundaryElement(container) ) {
							// we are done
							boundaryFound = true;
						} else {
							// element itself is no boundary element, so go to parent
							offset = domUtil.getNodeIndex(container) + 1;
							container = container.getParentNode();
						}
					}
				} else {
					// check whether there is an element to the left
					if (offset > 0) {
						// there is an element to the left, check whether it is a word boundary element
						if ( isWordBoundaryElement(container.getChild(offset - 1)) ) {
							// we are done
							boundaryFound = true;
						} else {
							// element to the left is no word boundary, so enter it
							container = container.getChild(offset - 1);
							if ( container.getNodeType() == 3 ) {
								offset = Text.as(container).getData().length();
							} else {
								offset = container.getChildCount();
							}
						}
					} else {
						// no element to the left, check whether the element itself is a boundary element
						if ( isWordBoundaryElement(container) ) {
							// we are done
							boundaryFound = true;
						} else {
							// element itself is no boundary element, so go to parent
							offset = domUtil.getNodeIndex(container);
							container = container.getParentNode();
						}
					}
				}
			}
		}

		if (container.getNodeType() != 3) {
			Text textNode = domUtil.getAdjacentTextNode(container, offset, directionLeft);
			if ( textNode != null ) {
				container = textNode;
				offset = directionLeft ? 0 : textNode.getData().length();
			}
		}

		return new EndPoint(container, offset);		
	}
	
	public Node findMarkup(Markup markup, boolean start) {
		return findMarkup(markup, start, topNode);
	}
	
	/**
	 * Find certain the first occurrence of some markup within the parents of either the start or the end of this range.
	 * The markup is identified by the tag name.
	 * 
	 * @param Node markup markup to be found
	 * @param Node limit limit objects for limit the parents taken into consideration
	 * @param boolean start true for searching at the start of the range, false otherwise
	 * @return Node the found dom object or null if nothing found.
	 */
	public Node findMarkup(Markup markup, boolean start, Node topNode) {
		List<Node> parents = getContainerParents(start, topNode);
		
		for ( Node parent : parents ) {
			if ( markup.matches(parent) ) {
				return parent;
			}
		}

		return null;		
	}
	
	/**
	 * Apply the given markup additively to the given range.
	 * 
	 * @param String markup markup to be applied as jQuery object
	 * @param boolean allowNesting true when nesting of the added markup is allowed, false if not (default: false)
	 */
	public void addMarkup(Node markup, boolean allowNesting) {
		// split partially contained text nodes at the start and end of the range
		if ( startContainer.getNodeType() == 3 && startOffset > 0
				&& startOffset < Text.as(startContainer).getData().length() ) {
			split(startContainer.getParentElement(), false);
		}
		if ( endContainer.getNodeType() == 3 && endOffset > 0
				&& endOffset < Text.as(endContainer).getData().length() ) {
			split(endContainer.getParentElement(), true);
		}

		List<RangeTreeNode> rangeTree = getRangeTree(null);
		recursiveAddMarkup(rangeTree, markup, allowNesting);

		// cleanup the DOM
		cleanUp(null, CLEANUP_MERGE_TEXT | CLEANUP_REMOVE_EMPTY);		
	}

	public void removeMarkup(Node markup) {
		removeMarkup(markup, topNode);
	}
	
	/**
	 * Remove the given markup from the given range.
	 * 
	 * TODO: add parameter deep/shallow
	 * @param {jQuery} markup markup to be removed as jQuery object
	 * @param {jQuery} limit Limiting node(s) as jQuery object
	 */
	public void removeMarkup(Node markup, Node topNode) {
		String nodeName = markup.getNodeName();
		Node startSplitLimit = domUtil.findHighestElement(startContainer, nodeName, topNode);
		Node endSplitLimit = domUtil.findHighestElement(endContainer, nodeName, topNode);
		boolean didSplit = false;

		if ( startSplitLimit != null ) {
			// when the start is in the start of its container, we don't split
			split(startSplitLimit.getParentNode(), false);
			didSplit = true;
		}

		if ( endSplitLimit != null ) {
			split(endSplitLimit.getParentNode(), true);
			didSplit = true;
		}

		// when we split the DOM, we maybe need to correct the range
		if ( didSplit ) {
			normalize();
		}

		// find the highest occurrence of the markup
		Node highestObject = domUtil.findHighestElement(getCommonAncestor(), nodeName, topNode);
		Node root = highestObject != null ? highestObject.getParentNode() : null;

		// construct the range tree
		List<RangeTreeNode> rangeTree = getRangeTree(root);
		
		// remove the markup from the range tree
		recursiveRemoveMarkup(rangeTree, markup);
		
		// cleanup DOM
		cleanUp(root, CLEANUP_MERGE_TEXT | CLEANUP_REMOVE_EMPTY);		
	}
	
	/**
	 * Remove the given DOM object from the DOM and modify the given range to reflect the user expected range after the object was removed.
	 * 
	 * @param Node object DOM object to remove
	 * @param boolean preserveContent true if the contents of the removed DOM object shall be preserved, false if not (default: false)
 	 */
	public void removeFromDOM(Node object) {
		// check whether the range will need modification
		int indexInParent = domUtil.getNodeIndex(object);
		int numChildren = object.getChildCount();
		Node parent = object.getParentNode();

		if ( startContainer.equals(parent) && startOffset > indexInParent ) {
			startOffset += numChildren - 1;
		} else if (startContainer.equals(object) ) {
			startContainer = parent;
			startOffset = indexInParent + startOffset;
		}

		if ( endContainer.equals(parent) && endOffset > indexInParent) {
			endOffset += numChildren - 1;
		} else if ( endContainer.equals(object) ) {
			endContainer = parent;
			endOffset = indexInParent + endOffset;
		}

		// we simply unwrap the children of the object
		JsArray<Node> children = domUtil.children(object);
		Node insertPointParent = object.getParentNode();
		for ( int i = 0; i < children.length(); i++ ) {
			insertPointParent.insertBefore(children.get(i), object);
		}
		object.removeFromParent();

		// eventually do cleanup
		cleanUp(parent, CLEANUP_MERGE_TEXT);
	}
	
	/**
	 * Recursive helper method to add the given markup to the range.
	 * 
	 * @param rangeTree rangetree at the current level
	 * @param markup markup to be applied
	 * @param nesting true when nesting of the added markup is allowed, false if not
	 */
	private void recursiveAddMarkup(List<RangeTreeNode> rangeTree, Node markup, boolean nesting) {
		// iterate through all rangetree objects of that level
		for ( RangeTreeNode rangeTreeNode : rangeTree ) {
			// check whether the rangetree object is fully contained and the markup may be wrapped around the object
			if ( Type.FULL.equals(rangeTreeNode.type) && domUtil.allowsNesting(markup, rangeTreeNode.node) ) {
				// we wrap the object, when
				// 1. nesting of markup is allowed or the node is not of the markup to be added
				// 2. the node an element node or a non-empty text node
				if ( (nesting || !rangeTreeNode.node.getNodeName().equals(markup.getNodeName()))
						&& ( rangeTreeNode.node.getNodeType() != 3 || Text.as(rangeTreeNode.node).getData().trim().length() != 0) ) {
					// wrap the object
					Node wrappingMarkup = markup.cloneNode(true);
					rangeTreeNode.node.getParentNode().insertAfter(wrappingMarkup, rangeTreeNode.node);
					wrappingMarkup.appendChild(rangeTreeNode.node);

					// TODO eventually update the range (if it changed)

					// when nesting is not allowed, we remove the markup from the inner element
					if ( !nesting && rangeTreeNode.node.getNodeType() != 3) {
						Range innerRange = GWT.create(Range.class);
						innerRange.startContainer = innerRange.endContainer = rangeTreeNode.node.getParentNode();
						innerRange.startOffset = 0;
						innerRange.endOffset = innerRange.endContainer.getChildCount();
						innerRange.removeMarkup(markup, rangeTreeNode.node.getParentNode());
					}
				}
			} else if (!Type.COLLAPSED.equals(rangeTreeNode.type)) {
				// TODO check whether the object may be replaced by the given markup
				// recurse into the children (if any), but not if nesting is not
				// allowed and the object is of the markup to be added
				if ( nesting || !rangeTreeNode.node.getNodeName().equals(markup.getNodeName()) ) {
					if ( rangeTreeNode.children != null && rangeTreeNode.children.size() > 0 ) {
						recursiveAddMarkup(rangeTreeNode.children, markup, false);
					}
				}
			}
		}		
	}

	/**
	 * TODO: pass the range itself and eventually update it if necessary
	 * Recursive helper method to remove the given markup from the range
	 * @param rangeTree rangetree at the current level
	 * @param markup markup to be applied
	 */
	private void recursiveRemoveMarkup(List<RangeTreeNode> rangeTree, Node markup) {
		// iterate over the rangetree objects of this level
		for ( RangeTreeNode rangeTreeNode : rangeTree ) {
			// check whether the object is the markup to be removed and is fully into the range
			if ( Type.FULL.equals(rangeTreeNode.type) && rangeTreeNode.node.getNodeName().equalsIgnoreCase(markup.getNodeName()) ) {
				// found the markup, so remove it
				if ( rangeTreeNode.node.getChildCount() > 0) {
					// when the object has children, unwarp them 
					JsArray<Node> children = domUtil.children(rangeTreeNode.node);
					Node insertPointParent = rangeTreeNode.node.getParentNode();
					for ( int i = 0; i < children.length(); i++) {
						insertPointParent.insertBefore(children.get(i), rangeTreeNode.node);
					}
				}
				rangeTreeNode.node.removeFromParent();
			}

			// if the object has children, we do the recursion now
			if ( rangeTreeNode.children != null && rangeTreeNode.children.size() > 0 ) {
				recursiveRemoveMarkup(rangeTreeNode.children, markup);
			}
		}		
	}
	
	/**
	 * Splits a DOM element at the given position up until the limiting object, 
	 * so that it is valid HTML again afterwards.
	 * 
	 * @param Node limit Limiting node(s) for the split. 
	 * 				The limiting node will not be included in the split itself.
	 * @param boolean atEnd If set to true, the DOM will be splitted at the end of the range otherwise at the start.
	 * @return List<Node> a node list containing the two root DOM objects of the split, an empty list if the DOM did 
	 * 						not need to be split or null if the DOM could not be split
	 */
	private List<Node> split(Node limit, boolean atEnd) {
		Node splitElement;
		int splitPosition;
		
		if ( atEnd ) {
			splitElement = endContainer;
			splitPosition = endOffset;
		} else {
			splitElement = startContainer;
			splitPosition = startOffset;			
		}
		
		// we may have to update the range if it is not collapsed and we are splitting at the start
		boolean updateRange = (!isCollapsed() && !atEnd);
		
		// find the path up to the highest object that will be splitted
		boolean pathFound = false;
		List<Node> parents = new ArrayList<Node>();
		parents.add(splitElement);
		Node parent = splitElement.getParentNode();
		// add parents up to the limit object
		while ( parent != null ) {
			if ( parent.equals(limit) ) {
				pathFound = true;
				break;
			}
			parents.add(parent);
			parent = parent.getParentNode();
		}
		
		// nothing found to split -> return here
		if ( !pathFound ) {
			return new ArrayList<Node>();
		}
		
		List<Node> path = parents;

		Node newDom = null;
		Node insertElement = null;
		Node secondPart = null;
		
		// iterate over the path, create new dom nodes for every element and move 
		// the contents right of the split to the new element 
		for(int i = path.size()-1; i >= 0; i--) {
			Node element = path.get(i);
			
			if ( i == 0 ) {
				// last element in the path -> we have to split it
				
				// split the last part into two parts
				if (element.getNodeType() == 3) {
					// text node
					Text textNode = Text.as(element);
					String text = textNode.getData().substring(splitPosition);
					secondPart = Document.get().createTextNode(text);
					textNode.setData(textNode.getData().substring(0, splitPosition));
				} else {
					// other nodes
					secondPart = element.cloneNode(false);

					for ( int id = splitPosition; id < element.getChildCount(); id++ ) {
						secondPart.appendChild(element.getChild(id));
					}
				}
				
				// update the range if necessary
				if ( updateRange && element.equals(endContainer) ) {
					endContainer = secondPart;
					endOffset -= splitPosition;
					resetCache();
				}
				
				// add the second part
				if ( insertElement != null ) {
					insertElement.insertFirst(secondPart);
				} else {
					element.getParentNode().insertAfter(secondPart, element);
				}
			} else {
				// create the new element of the same type and prepend it to the previously created element
				Node newElement = element.cloneNode(false);
				
				if ( newDom == null ) {
					newDom = newElement;
					insertElement = newElement;
				} else {
					insertElement.insertFirst(newElement);
					insertElement = newElement;
				}
				
				// move all contents right of the split to the new element
				Node next;
				while ( (next = path.get(i - 1).getNextSibling()) != null ) {
					insertElement.appendChild(next);
				}
				
				// update the range if necessary
				if ( updateRange && element.equals(endContainer) ) {
					endContainer = newElement;
					Node prev = path.get(i + 1);
					int offset = 0;
					while ( (prev = prev.getPreviousSibling()) != null ) {
						offset++;
					}
					endOffset -= offset;
					resetCache();
				}
			}
		}
		
		// append the new dom
		if ( newDom != null ) {
			Node insertPoint = path.get(path.size() - 1);
			insertPoint.getParentNode().insertAfter(newDom, insertPoint);
		}

		List<Node> result = new ArrayList<Node>();
		result.add(path.get(path.size() - 1));
		if ( newDom != null ) {
			result.add(newDom);
		} else {
			result.add(secondPart);
		}
		
		return result;
	}
	
	public List<RangeTreeNode> getRangeTree() {
		return getRangeTree(null);
	}
	
	/**
	 * Get the range tree of this range. The range tree will be cached for every root object.
	 * 
	 * @param Node root root object of the range tree, if non given, the common ancestor container of the start and end containers will be used
	 * @return {RangeTree} array of RangeTree object for the given root object
	 */
	public List<RangeTreeNode> getRangeTree(Node root) {
		if ( root == null ) {
			root = getCommonAncestor();
		}

		if ( rangeTree.containsKey(root) ) {
			// sometimes it's cached
			return rangeTree.get(root);
		}

		inSelection = false;
		rangeTree.put(root, recursiveGetRangeTree(root));

		return rangeTree.get(root);		
	}
	
	/**
	 * Recursive inner function for generating the range tree.
	 * 
	 * @param currentObject current DOM object for which the range tree shall be generated
	 * @return array of RangeTreeNode objects for the children of the current DOM object
	 */
	private List<RangeTreeNode> recursiveGetRangeTree(Node currentObject) {
		// get all direct children of the given object
		List<RangeTreeNode> currentElements = new ArrayList<RangeTreeNode>();
		RangeTreeNode rangeTreeNode = null;
		
		for ( int index = 0; index < currentObject.getChildCount(); index++) {
			Node thisNode = currentObject.getChild(index);
			Type type = Type.NONE;
			int startOffset = -1;
			int endOffset = -1;
			boolean collapsedFound = false;

			// check for collapsed selections between nodes
			if ( isCollapsed() && currentObject.equals(this.startContainer) && this.startOffset == index) {
				// insert an extra rangetree object for the collapsed range here
				rangeTreeNode = new RangeTreeNode();
				rangeTreeNode.type = Type.COLLAPSED;
				rangeTreeNode.node = null;
				currentElements.add(rangeTreeNode);
				inSelection = false;
				collapsedFound = true;
			}

			if ( !inSelection && !collapsedFound ) {
				// the start of the selection was not yet found, so look for it now
				// check whether the start of the selection is found here

				// check is dependent on the node type
				switch ( thisNode.getNodeType() ) {
				case 3: // text node
					if ( thisNode.equals(this.startContainer) ) {
						// the selection starts here
						inSelection = true;

						// when the startoffset is > 0, the selection type is only partial
						type = this.startOffset > 0 ? Type.PARTIAL : Type.FULL;
						startOffset = this.startOffset;
						endOffset = Text.as(thisNode).getLength();
					}
					break;
				case 1: // element node
					if ( thisNode.equals(this.startContainer) && this.startOffset == 0 ) {
						// the selection starts here
						inSelection = true;
						type = Type.FULL;
					}
					if ( currentObject.equals(this.startContainer) && this.startOffset == index ) {
						// the selection starts here
						inSelection = true;
						type = Type.FULL;
					}
					break;
				}
			}

			if ( inSelection && !collapsedFound ) {
				if ( Type.NONE.equals(type) ) {
					type = Type.FULL;
				}
				// we already found the start of the selection, so look for the end of the selection now
				// check whether the end of the selection is found here

				switch ( thisNode.getNodeType() ) {
				case 3: // text node
					if ( thisNode.equals(this.endContainer) ) {
						// the selection ends here
						inSelection = false;

						// check for partial selection here
						if ( this.endOffset < Text.as(thisNode).getLength() ) {
							type = Type.PARTIAL;
						}
						if ( startOffset == -1 ) {
							startOffset = 0;
						}
						endOffset = this.endOffset;
					}
					break;
				case 1: // element node
					if ( thisNode.equals(this.endContainer) && this.endOffset == 0 ) {
						inSelection = false;
					}
					break;
				}
				if ( currentObject.equals(this.endContainer) && this.endOffset <= index ) {
					inSelection = false;
					type = Type.NONE;
				}
			}

			// create the current selection tree entry
			rangeTreeNode = new RangeTreeNode();
			rangeTreeNode.node = thisNode;
			rangeTreeNode.type = type;
			if ( Type.PARTIAL.equals(type) ) {
				rangeTreeNode.startOffset = startOffset;
				rangeTreeNode.endOffset = endOffset;
			}
			currentElements.add(rangeTreeNode);

			// now do the recursion step into the current object
			rangeTreeNode.children = recursiveGetRangeTree(thisNode);
			
			// check whether a selection was found within the children
			if ( rangeTreeNode.children.size() > 0) {
				boolean noneFound = false;
				boolean partialFound = false;
				boolean fullFound = false;
				
				for ( int i = 0; i < rangeTreeNode.children.size(); i++) {
					switch ( rangeTreeNode.children.get(i).type ) {
						case NONE:
							noneFound = true;
							break;
						case FULL:
							fullFound = true;
							break;
						case PARTIAL:
							partialFound = true;
							break;
							
						default:
							break;
					}
				}

				if ( partialFound || (fullFound && noneFound) ) {
					// found at least one 'partial' DOM object in the children, or both 'full' and 'none', so this element is also 'partial' contained
					rangeTreeNode.type = Type.PARTIAL;
				} else if ( fullFound && !partialFound && !noneFound ) {
					// only found 'full' contained children, so this element is also 'full' contained
					rangeTreeNode.type = Type.FULL;
				}
			}
		}

		// extra check for collapsed selections at the end of the current element
		if ( isCollapsed()
				&& currentObject.equals(this.startContainer)
				&& this.startOffset == currentObject.getChildCount() ) {
			rangeTreeNode = new RangeTreeNode();
			rangeTreeNode.node = null;
			rangeTreeNode.type = Type.COLLAPSED;
			currentElements.add(rangeTreeNode);
		}

		return currentElements;
	}
	
	private static String[] mergeableTags = new String[] {
		"a", "b", "strong", "i", "em", "sub", "sup", "#text", "code"
	};
	
	/**
	 * Cleanup the DOM, starting with the given startobject (or the common ancestor container of the given range)
	 * Cleanup modes (given as properties in 'cleanup'):
	 * <pre>
	 * - merge: merges multiple successive nodes of same type, if this is allowed, starting at the children of the given node (defaults to false)
	 * - removeempty: removes empty element nodes (defaults to false)
	 * </pre>
	 * Example for calling this method:<br/>
	 * <code>GENTICS.Utils.Dom.doCleanup({merge:true,removeempty:false}, range)</code>
	 * @param {object} cleanup type of cleanup to be done
	 * @param {GENTICS.Utils.RangeObject} rangeObject range which is eventually updated
	 * @param {DOMObject} start start object, if not given, the commonancestorcontainer is used as startobject insted
	 * @return {boolean} true when the range (startContainer/startOffset/endContainer/endOffset) was modified, false if not
	 */
	public boolean cleanUp(Node start, int options) {

		if ( start == null ) {
			start = getCommonAncestor();
		}
		
		// remember the previous node here (successive nodes of same type will be merged into this)
		Node prevNode = null;
		// check whether the range needed to be modified during merging
		boolean modifiedRange = false;
		// get the start object
		Node startObject = start;

		// iterate through all sub nodes
		JsArray<Node> children = domUtil.children(startObject);
		for ( int index = 0; index < children.length(); index++ ) {
			Node thisNode = children.get(index);
			
			// decide further actions by node type
			switch ( thisNode.getNodeType() ) {
			// found a non-text node
			case 1:
				if ( prevNode != null && prevNode.getNodeName().equals(thisNode.getNodeName()) ) {
					// found a successive node of same type

					// now we check whether the selection starts or ends in the mother node after the current node
					if ( startContainer.equals(startObject) && startOffset > index ) {
						// there will be one less object, so reduce the startOffset by one
						startOffset -= 1;
						// set the flag for range modification
						modifiedRange = true;
					}
					if ( endContainer.equals(startObject) && endOffset > index) {
						// there will be one less object, so reduce the endOffset by one
						endOffset -= 1;
						// set the flag for range modification
						modifiedRange = true;
					}

					// merge the contents of this node into the previous one
					JsArray<Node> subNodesToBeMoved = domUtil.children(thisNode);
					for ( int i = 0; i < subNodesToBeMoved.length(); i++ ) {
						prevNode.appendChild(subNodesToBeMoved.get(i));
					}

					// after merging, we eventually need to cleanup the prevNode again
					modifiedRange |= cleanUp(prevNode, options);

					// remove this node
					thisNode.removeFromParent();
				} else {
					// do the recursion step here
					modifiedRange |= cleanUp(thisNode, options);

					// eventually remove empty elements
					boolean removed = false;
					boolean isMergeableTag = false;
					if ( (options & CLEANUP_REMOVE_EMPTY) != 0 ) {
						if ( domUtil.isBlockLevelElement(thisNode) && thisNode.getChildCount() == 0 ) {
							thisNode.removeFromParent();
							removed = true;
						}
						
						for ( String mergeableTag : mergeableTags ) {
							if ( mergeableTag.equalsIgnoreCase(thisNode.getNodeName()) ) {
								isMergeableTag = true;
								break;
							}
						}
						
						if ( isMergeableTag && (thisNode.getChildCount() == 0 ||
								Element.as(thisNode).getInnerText().length() == 0) ) {
							removed = true;
							thisNode.removeFromParent();
						}
					}

					// when the current node was not removed, we eventually store it as previous (mergeable) tag
					if ( !removed ) {
						if ( isMergeableTag ) {
							prevNode = thisNode;
						} else {
							prevNode = null;
						}
					}
				}
				break;
			// found a text node
			case 3:
				// found a text node
				if ( prevNode != null && prevNode.getNodeType() == 3 && (options & CLEANUP_MERGE_TEXT) != 0 ) {
					// the current text node will be merged into the last one, so
					// check whether the selection starts or ends in the current
					// text node
					if ( startContainer.equals(thisNode) ) {
						// selection starts in the current text node

						// update the start container to the last node
						startContainer = prevNode;

						// update the start offset
						startOffset += Text.as(prevNode).getLength();

						// set the flag for range modification
						modifiedRange = true;
					}
					
					if ( endContainer.equals(thisNode) ) {
						// selection ends in the current text node

						// update the end container to be the last node
						endContainer = prevNode;

						// update the end offset
						endOffset += Text.as(prevNode).getLength();

						// set the flag for range modification
						modifiedRange = true;
					}

					// now we check whether the selection starts or ends in the mother node after the current node
					if ( startContainer.equals(startObject) && startOffset > index ) {
						// there will be one less object, so reduce the startOffset by one
						startOffset -= 1;
						// set the flag for range modification
						modifiedRange = true;
					}
					if ( endContainer.equals(startObject) && endOffset > index ) {
						// there will be one less object, so reduce the endOffset by one
						endOffset -= 1;
						// set the flag for range modification
						modifiedRange = true;
					}

					// now append the contents of the current text node into the previous
					String prevData = Text.as(prevNode).getData();
					Text.as(prevNode).setData( prevData + Text.as(thisNode).getData());

					// remove this text node
					thisNode.removeFromParent();
				} else {
					// remember it as the last text node
					prevNode = thisNode;
				}
				break;
			}
		}

		// eventually remove the startnode itself
		if ( (options & CLEANUP_REMOVE_EMPTY) != 0 && 
				domUtil.isBlockLevelElement(start) && start.getChildCount() == 0 ) {
			if ( startContainer.equals(start) ) {
				startContainer = start.getParentNode();
				startOffset = domUtil.getNodeIndex(start);
			}
			if ( endContainer.equals(start) ) {
				endContainer = start.getParentNode();
				endOffset = domUtil.getNodeIndex(start);
			}
			startObject.removeFromParent();
			modifiedRange = true;
		}

		if ( modifiedRange ) {
			resetCache();
		}

		return modifiedRange;		
	}
		
	/**
	 * Sets the visible selection in the Browser based on the range object.
	 * If the selection is collapsed, this will result in a blinking cursor, 
	 * otherwise in a text selection.
	 */
	public void select() {
		
	}
	
	public void update() {
		update(null);
	}
	
	public void update(Node newCommonAncestor) {
		updateStartMarkupsAndSplitNode();
		
		if ( newCommonAncestor != null ) {
			commonAncestor = newCommonAncestor;
		} else {
			commonAncestor = null;
			getCommonAncestor();
		}
		
		rangeTree.clear();
	}
	
	/**
	 * Recalculates the <code>startMarkup</code>, <code>umodifiableStartMarkup</code> and
	 * <code>splitNode</code>.
	 */
	private void updateStartMarkupsAndSplitNode() {
		startMarkup.clear();
		unmodifiableStartMarkup.clear();
		splitNode = null;
		
		List<Node> parents = getStartContainerParents();
		boolean topNodeReached = false;

		for ( Node aNode : parents ) {
			if (!topNodeReached && !aNode.equals(topNode) ) {
				startMarkup.add(aNode);
				if ( splitNode == null && domUtil.isSplitable(aNode) ) {
					splitNode = aNode;
				}
			} else {
				topNodeReached = true;
				unmodifiableStartMarkup.add(aNode);
			}
		}
	}
	
	/**
	 * Returns a list of nodes from the range tree that are fully or partially selected
	 * and that belong to the same range tree as the given node.
	 * 
	 * @param node
	 * @return a list of selected sibling nodes
	 */
	public List<Node> getSelectedSiblings(Node node) {
		List<Node> result = recursionGetSelectedSiblings(node, getRangeTree(null));
		if ( result == null ) {
			result = new ArrayList<Node>();
		}
		return result;
	}
	
	/**
	 * A helper metod for getting selected sibling nodes recursively.
	 * 
	 * @param node
	 * @param rangeTree
	 * @return
	 */
	private List<Node> recursionGetSelectedSiblings(Node node, List<RangeTreeNode> rangeTree) {
		List<Node> siblings = null;
		
		boolean nodeConfirmed = false;

		for ( RangeTreeNode rangeTreeNode : rangeTree ) {
			if ( rangeTreeNode.node.equals(node) ) {
				nodeConfirmed = true;
				siblings = new ArrayList<Node>();
			} else if ( !nodeConfirmed && rangeTreeNode.children != null && rangeTreeNode.children.size() > 0 ) {
				// do the recursion
				siblings = recursionGetSelectedSiblings(node, rangeTreeNode.children);
				if ( siblings != null ) {
					break;
				}
			} else if ( nodeConfirmed && rangeTreeNode.node != null && 
					rangeTreeNode.type.equals(Type.PARTIAL) && 
					rangeTreeNode.type.equals(Type.FULL) ) {
				siblings.add(rangeTreeNode.node);
			} else if ( nodeConfirmed && rangeTreeNode.type.equals(Type.NONE) ) {
				break;
			}
		}

		return siblings;		
	}
	
	public void insertHtmlBreak() {
		insertHtml(DOM.createElement("br"));
	}
	
	public void insertHtml(String html) {
		Element div = DOM.createDiv();
		div.setInnerHTML(html);
		
		List<Node> markups = new ArrayList<Node>();
		for ( int i = 0; i < div.getChildCount(); i++ ) {
			markups.add(div.getChild(i));
		}
		insertMarkup(markups);
	}
	
	public void insertHtml(Node markup) {
		List<Node> markups = new ArrayList<Node>();
		markups.add(markup);
		insertMarkup(markups);
	}
	
	public void insertMarkup(List<Node> markup) {
		List<RangeTreeNode> rangeTree = getRangeTree();
		for ( int i = 0; i < rangeTree.size(); i++ ) {
			RangeTreeNode rangeTreeNode = rangeTree.get(i);
			if ( !Type.NONE.equals(rangeTreeNode.type) ) { // before cursor, leave this part inside the splitObject
				if ( Type.COLLAPSED.equals(rangeTreeNode.type) ) {
					// collapsed selection found (between nodes)
					if ( i > 0 ) {
						// not at the start, so get the element to the left
						Node prevNode = rangeTree.get(i - 1).node;
						// and insert the markup after it
						Node currentNode = prevNode;
						for ( Node newNode : markup ) {
							prevNode.getParentNode().insertAfter(newNode, currentNode);
							currentNode = newNode;
						}
					} else {
						if ( rangeTree.size() > 1 ) {
							// at the start, so get the element to the right
							Node nextNode = rangeTree.get(1).node;
							// and insert the break before it
							for ( Node newNode : markup ) {
								nextNode.getParentNode().insertBefore(newNode, nextNode);
							}
						} else {
							// special case when the collapsed selection is within an empty node
							for ( Node newNode : markup ) {
								startContainer.appendChild(newNode);
							}							
						}
					}

					// now set the range
					startContainer = markup.get(0).getParentNode();
					startOffset = domUtil.getNodeIndex(markup.get(0)) + 1;
					endContainer = startContainer;
					endOffset = startOffset;
					normalize();
				} else if ( rangeTreeNode.node != null && rangeTreeNode.node.getNodeType() == 3 ) {
					// when the textnode is immediately followed by a blocklevel element (like p, h1, ...) we need to add an additional br in between
					Node nextSibling = rangeTreeNode.node.getNextSibling();
					if ( nextSibling != null && domUtil.isReplacingElement(nextSibling) ) {
						rangeTreeNode.node.getParentNode().insertAfter(DOM.createElement("br"), rangeTreeNode.node);
					}
					// when the textnode is the last inside a blocklevel element
					// (like p, h1, ...) we need to add an additional br as very
					// last object in the blocklevel element
					Node checkNode = rangeTreeNode.node;
					while ( checkNode != null ) {
						if ( checkNode.getNextSibling() != null ) {
							checkNode = null;
						} else {
							// go to the parent
							checkNode = checkNode.getParentNode();
							// reached the limit object, we are done
							if ( checkNode.equals(topNode) ) {
								checkNode = null;
							}
							// found a blocklevel element, we are done
							if ( domUtil.isBlockLevelElement(checkNode) ) {
								break;
							}
						}
					}
					// when we found a blocklevel element, insert a break at the end
					if ( checkNode != null ) {
						checkNode.appendChild(DOM.createElement("br"));
					}

					// insert the markup
					insertBetween(rangeTreeNode.node, rangeTreeNode.startOffset, markup);

					// correct the range
					// count the number of previous siblings
					int offset = 0;
					Node cntNode = markup.get(0);
					while ( cntNode != null ) {
						cntNode = cntNode.getPreviousSibling();
						offset++;
					}

					startContainer = markup.get(0).getParentNode();
					startOffset = offset;
					endContainer = startContainer;
					endOffset = startOffset;
					normalize();
				} else if ( rangeTreeNode.node != null && rangeTreeNode.node.getNodeType() == 1 ) { // other node, normally a break
					if ( domSelector.select(".height-holder", rangeTreeNode.node.getParentNode()).length() == 0 ) {
						// but before putting it, remove all:
						JsArray<Element> forRemoval = domSelector.select(".height-holder", topNode);
						for ( int j = 0; j < forRemoval.length(); j++ ) {
							forRemoval.get(j).removeFromParent();
						}
						//  now put it:
						Element heightHolder = DOM.createElement("br");
						heightHolder.addClassName("height-holder");
						commonAncestor.appendChild(heightHolder);
					}
					Node target = rangeTreeNode.node;
					for ( Node newNode : markup ) {
						target.getParentNode().insertAfter(newNode, target);
						target = newNode;
					}					
					
					// now set the selection. Since we just added one break do the current el
					// the new position must be el's position + 1. el's position is the index 
					// of the el in the selection tree, which is i. then we must add
					// another +1 because we want to be AFTER the object - therefore +2
					startContainer = commonAncestor;
					startOffset = i + 2;
					endContainer = startContainer;
					endOffset = startOffset;			
					update();
				}
			}
		}
		select();		
	}
	
	private void insertBetween(Node target, int offset, List<Node> markup) {
		if ( target.getNodeType() != 3 ) {
			// we are not in a text node, just insert the element at the corresponding position
			if ( offset > target.getChildCount() ) {
				offset = target.getChildCount();
			}
			if (offset <= 0) {
				for ( Node newNode : markup ) {
					target.getParentNode().insertBefore(newNode, target);
				}
			} else {
				for ( Node newNode : markup ) {
					target.getParentNode().insertAfter(newNode, target);
					target = newNode;
				}
			}
		} else {
			// we are in a text node so we have to split it at the correct position
			if ( offset <= 0 ) {
				for ( Node newNode : markup ) {
					target.getParentNode().insertBefore(newNode, target);
				}
			} else if ( offset >= Text.as(target).getLength() ) {
				for ( Node newNode : markup ) {
					target.getParentNode().insertAfter(newNode, target);
					target = newNode;
				}		
			} else {
				String text = Text.as(target).getData();
				Text.as(target).setData(text.substring(0, offset));
				target.getParentNode().insertAfter(Document.get().createTextNode(text.substring(offset, text.length())), target);
				for ( Node newNode : markup ) {
					target.getParentNode().insertAfter(newNode, target);
					target = newNode;
				}
			}
		}		
	}

	public void split() {
		// first check where the markup can be inserted, then decide where to split

		// update the commonAncestor with the split object (so that the range tree is correct)
		update(splitNode);

		// calculate the selection tree. NOTE: it is necessary to do this before
		// getting the followupcontainer, since getting the selection tree might
		// possibly merge text nodes, which would lead to differences in the followupcontainer
		getRangeTree();

		// object to be inserted after the splitObject
		Element followUpContainer = getSplitFollowUpContainer();

		// now split up the splitNode into itself and the followUpContainer
		splitHelper(getRangeTree(), followUpContainer);

		// check whether the followupcontainer is still marked for removal
		String className = followUpContainer.getClassName();
		if ( className != null && className.indexOf("to-be-removed") != -1 ) {
			// TODO shall we just remove the class or shall we not use the followupcontainer?
			followUpContainer.setClassName(className.replaceAll("to-be-removed", ""));
		}

		// now let's find the place, where the followUp is inserted afterwards. normally that's the splitNode itself, but in
		// some cases it might be their parent (e.g. inside a list, a <p> followUp must be inserted outside the list)
		Node insertAfterObject = getInsertAfterObject(followUpContainer);
		
		// now insert the followUpContainer
		insertAfterObject.getParentNode().insertAfter(followUpContainer, insertAfterObject);
		
		// in some cases, we want to remove the "empty" splitNode (e.g. LIs, if enter was hit twice)
		if ( "li".equalsIgnoreCase(splitNode.getNodeName()) && !domUtil.textLevelComparator(splitNode, followUpContainer) ) {
			splitNode.removeFromParent();
		}
		
		// find a possible text node in the followUpContainer and set the selection to it
		// if no textnode is available, set the selection to the followup container itself
		List<Node> textNodes = domUtil.getTextNodes(followUpContainer, true, true);
		if ( textNodes != null && textNodes.size() > 0 ) {
			startContainer = textNodes.get(0);
		}
		if ( startContainer == null ) { // if no text node was found, select the parent object of the height-holder element
			textNodes = domUtil.getTextNodes(followUpContainer, false, false);
			if ( textNodes != null && textNodes.size() > 0 ) {
				startContainer = textNodes.get(0).getParentNode();
			}
		}
		
		if ( startContainer != null ) {
			// the cursor is always at the beginning of the followUp
			startOffset = 0;
			endContainer = startContainer;
			endOffset = 0;
		} else {
			startContainer = followUpContainer.getParentNode();
			startOffset = domUtil.getNodeIndex(followUpContainer);
			endContainer = startContainer;
			endOffset = startOffset;
		}
		
		// finally update the range object again
		update();
		
		// now set the selection
		select();
	}
	
	/**
	 * recursive method to parallelly walk through two dom subtrees, leave elements before startContainer in first subtree and move rest to other
	 * @param selectionTree tree to iterate over as contained in rangeObject. must be passed separately to allow recursion in the selection tree, but not in the rangeObject
	 * @param rangeObject GENTICS.Aloha.Selection.SelectionRange of the current selection
	 * @param followUpContainer optional jQuery object; if provided the rangeObject will be split and the second part will be insert inside of this object
	 */
	private void splitHelper(List<RangeTreeNode> rangeTree, Node followUpContainer) {
		Element fillUpElement = DOM.createElement("br");
		fillUpElement.setClassName("height-holder");
		
		boolean startMoving = false;
		
		if ( rangeTree.size() > 0) {
			List<Node> mirrorLevel = new ArrayList<Node>();
			for ( int i = 0; i < followUpContainer.getChildCount(); i++ ) {
				Node child = followUpContainer.getChild(i);
				if ( !isElementContentWhitespace(child) ) {
					mirrorLevel.add(child);
				}
			}
			
			for (int i = 0; i < rangeTree.size(); i++) {
				RangeTreeNode rangeTreeNode = rangeTree.get(i);
				// remove all objects in the mirrorLevel, which are BEFORE the cursor
				// OR if the cursor is at the last position of the last Text node (causing an empty followUpContainer to be appended)
				if ( (rangeTreeNode.type.equals(Type.NONE) && !startMoving) || 
						(rangeTreeNode.node != null && rangeTreeNode.node.getNodeType() == 3 && 
								rangeTreeNode.node.equals(rangeTree.get(rangeTree.size() - 1)) && 
								rangeTreeNode.startOffset == Text.as(rangeTreeNode.node).getLength()) ) {
					// iteration is before cursor, leave this part inside the splitObject, remove from followUpContainer
					// however if the object to remove is the last existing textNode within the followUpContainer, insert a BR instead
					// otherwise the followUpContainer is invalid and takes up no vertical space
					if ( !"".equals(Element.as(followUpContainer).getInnerText()) ) {
						mirrorLevel.get(i).removeFromParent();
					} else if ( domUtil.isSplitable(followUpContainer) ) {
						if ( fillUpElement != null ) {
							followUpContainer.appendChild(fillUpElement.cloneNode(true));
						} else {
							Element.as(followUpContainer).setInnerHTML("");
						}
					} else {
						Element.as(followUpContainer).setInnerHTML("");
						Element.as(followUpContainer).setClassName("to-be-removed");
					}
					continue;
				} else
					
				// split objects, which are AT the cursor Position or directly above
				if ( !rangeTreeNode.type.equals(Type.NONE) ) { // before cursor, leave this part inside the splitObject
					// TODO better check for selection == 'partial' here?
					if ( rangeTreeNode.node != null && rangeTreeNode.node.getNodeType() == 3 && rangeTreeNode.startOffset != -1 ) {
						String completeText = Text.as(rangeTreeNode.node).getData();
						if ( rangeTreeNode.startOffset > 0 ) {// first check, if there will be some text left in the splitNode 
							Text.as(rangeTreeNode.node).setData( completeText.substring(0, rangeTreeNode.startOffset) );
						} else if ( rangeTree.size() > 1 ) { // if not, check if the splitObject contains more than one node, because then it can be removed. this happens, when ENTER is pressed inside of a textnode, but not at the borders
							rangeTreeNode.node.removeFromParent();
						} else { // if the "empty" textnode is the last node left in the splitObject, replace it with a height-holder
							// if the parent is a blocklevel element, we insert the fillup element
							Node parent = rangeTreeNode.node.getParentNode();
							if ( domUtil.isSplitable(parent) ) {
								if ( fillUpElement != null ) {
									parent.appendChild(fillUpElement.cloneNode(true));
								} else {
									Element.as(parent).setInnerHTML("");
								}
							} else {
								// if the parent is no blocklevel element and would be empty now, we completely remove it
								parent.removeFromParent();
							}
						}
						if ( (completeText.length() - rangeTreeNode.startOffset) > 0 ) {
							// first check if there is text left to put in the followUpContainer's textnode. this happens, when ENTER is pressed inside of a textnode, but not at the borders
							Text.as(mirrorLevel.get(i)).setData( completeText.substring(rangeTreeNode.startOffset, completeText.length()) );
						} else if ( mirrorLevel.size() > 1 ) {
							// if not, check if the followUpContainer contains more than one node, because if yes, the "empty" textnode can be removed
							mirrorLevel.get(i).removeFromParent();
						} else if ( domUtil.isBlockLevelElement(followUpContainer) ) {
							// if the "empty" textnode is the last node left in the followUpContainer (which is a blocklevel element), replace it with a ephemera break
							if ( fillUpElement != null ) {
								followUpContainer.appendChild(fillUpElement.cloneNode(true));
							} else {
								Element.as(followUpContainer).setInnerHTML("");
							}
						} else {
							// if the "empty" textnode is the last node left in a non-blocklevel element, mark it for removal
							Element.as(followUpContainer).setInnerHTML("");
							Element.as(followUpContainer).setClassName("to-be-removed");
						}
					}
					startMoving = true;
					if ( rangeTreeNode.children != null && rangeTreeNode.children.size() > 0 ) {
						splitHelper(rangeTreeNode.children, mirrorLevel.get(i));
					}
				} else 
					
				// remove all objects in the origin, which are AFTER the cursor
				if ( rangeTreeNode.type.equals(Type.NONE) && startMoving ) { 
					// iteration is after cursor, remove from splitObject and leave this part inside the followUpContainer
					rangeTreeNode.node.removeFromParent();
				}
			}
		}
		
		// and finally cleanup: remove all fillUps > 1
		// remove all elements greater than (gt) 0, that also means: leave one
		JsArray<Element> fillUpElements = domSelector.select("br.height-holder:gt(0)", splitNode);
		for ( int i = 0; i < fillUpElements.length(); i++ ) {
			fillUpElements.get(i).removeFromParent();
		}
		fillUpElements = domSelector.select("br.height-holder:gt(0)", followUpContainer);
		for ( int i = 0; i < fillUpElements.length(); i++ ) {
			fillUpElements.get(i).removeFromParent();
		}

		// remove objects prepared for removal
		JsArray<Element> toBeRemoveds = domSelector.select(".to-be-removed", splitNode);
		for ( int i = 0; i < fillUpElements.length(); i++ ) {
			toBeRemoveds.get(i).removeFromParent();
		}
		toBeRemoveds = domSelector.select(".to-be-removed", followUpContainer);
		for ( int i = 0; i < fillUpElements.length(); i++ ) {
			toBeRemoveds.get(i).removeFromParent();
		}
		
		// if splitObject / followUp are empty, place a fillUp inside
		if ( !splitNode.hasChildNodes() && domUtil.isSplitable(splitNode) && fillUpElement != null ) {
			splitNode.appendChild(fillUpElement.cloneNode(true));
		}
		if ( !followUpContainer.hasChildNodes() && domUtil.isSplitable(followUpContainer) && fillUpElement != null ) {
			followUpContainer.appendChild(fillUpElement.cloneNode(true));
		}
	}
	
	/**
	 * TODO: it only applies when the parent Element is defined as containing only other elements, not MIXED content. So eg. a whitespace-only TextNode inside <div> will never set isElementContentWhitespace true
	 * @param node
	 * @return
	 */
	private native boolean isElementContentWhitespace(Node node)/*-{
		return node.isElementContentWhitespace;
	}-*/;
	
	/**
	 * Returns a jQuery object fitting the passed splitObject as follow up object
	 * examples: 
	 * - when passed a p it will return an empty p (clone of the passed p)
	 * - when passed an h1, it will return either an h1 (clone of the passed one) or a new p (if the collapsed selection was at the end)
	 */
	private Element getSplitFollowUpContainer() {
		String tagName = splitNode.getNodeName().toLowerCase();
		if ( "h1".equals(tagName) || "h2".equals(tagName) || "h3".equals(tagName) || 
				"h4".equals(tagName) || "h5".equals(tagName) || "h6".equals(tagName) ) {
			List<Node> textNodes = domUtil.getTextNodes(splitNode);
			Node lastNode = null;
			if ( textNodes.size() > 0 ) {
				lastNode = textNodes.get(textNodes.size() - 1);
			}
			// special case: when enter is hit at the end of a heading, the followUp should be a <p>
			if ( lastNode != null && startContainer.equals(lastNode) && startOffset == Text.as(lastNode).getLength() ) {
				Element resultNode = DOM.createElement("p");
				for ( int i = 0; i < splitNode.getChildCount(); i++ ) {
					resultNode.appendChild(splitNode.getChild(i).cloneNode(true));
				}
				return resultNode;
			}
		} else if ( "li".equals(tagName) ) {
			// TODO check whether the li is the last one
			// special case: if enter is hit twice inside a list, the next item should be a <p> (and inserted outside the list)
			if ( "br".equalsIgnoreCase(startContainer.getNodeName()) && Element.as(startContainer).getClassName().indexOf("height-holder") != -1 ) {
				Element resultNode = DOM.createElement("p");
				for ( int i = 0; i < splitNode.getChildCount(); i++ ) {
					resultNode.appendChild(splitNode.getChild(i).cloneNode(true));
				}
				return resultNode;				
			}
			// when the li is the last one and empty, we also just return a <p>
			if ( splitNode.getNextSibling() == null && Element.as(splitNode).getInnerText().length() == 0 ) {
				return DOM.createElement("p");
			}
		}
		
		return DOM.createElement(tagName);
	}
	
	/**
	 * Returns the object after which the followUpContainer can be inserted during splitup
	 * 
	 * @param followUpContainer
	 * @return object after which the followUpContainer can be inserted
	 */
	private Node getInsertAfterObject(Node followUpContainer) {
		boolean splitNodePassed = false;
		for ( Node markup : startMarkup ) {
			// check if we have already passed the splitObject (some other markup might come before)
			if ( markup.equals(splitNode) ) {
				splitNodePassed = true;
			}
			// if not passed splitObject, skip this markup
			if ( splitNodePassed ) {
				// once we are passed, check if the followUpContainer is allowed to be inserted into the current markup's parent
				if ( tagWrapping.canWrapp(markup.getParentNode().getNodeName().toLowerCase(), followUpContainer.getNodeName()) ) {
					return markup;
				}
			}
		}
		return null;		
	}
	
	public String getTextBeforeStart() {
		List<RangeTreeNode> rangeTree = getRangeTree(topNode);
		return getTextBeforeStartRecursively(rangeTree);
	}
	
	private String getTextBeforeStartRecursively(List<RangeTreeNode> rangeTree) {
		String text = "";
		for ( RangeTreeNode rangeTreeNode : rangeTree ) {
			if ( rangeTreeNode.type.equals(Type.NONE) ) {
				if ( rangeTreeNode.children != null && rangeTreeNode.children.size() > 0 ) {
					text += getTextBeforeStartRecursively(rangeTreeNode.children);
				} else if ( rangeTreeNode.node != null && rangeTreeNode.node.getNodeType() == 3 ) {
					text += Text.as(rangeTreeNode.node).getData();
				} else if ( rangeTreeNode.node != null && "br".equalsIgnoreCase(rangeTreeNode.node.getNodeName()) ) {
					text += "\n";
				}
			} else if ( rangeTreeNode.type.equals(Type.PARTIAL) || 
					rangeTreeNode.type.equals(Type.COLLAPSED) ) {
				if ( rangeTreeNode.children != null && rangeTreeNode.children.size() > 0 ) {
					text += getTextBeforeStartRecursively(rangeTreeNode.children);
				} else if ( rangeTreeNode.node != null && rangeTreeNode.node.getNodeType() == 3 && rangeTreeNode.startOffset > 0 ) {
					text += Text.as(rangeTreeNode.node).getData().substring(0, rangeTreeNode.startOffset);
				} else if ( rangeTreeNode.node != null && "br".equalsIgnoreCase(rangeTreeNode.node.getNodeName()) ) {
					text += "\n";
				}
				break;
			} else if ( rangeTreeNode.type.equals(Type.FULL) ) {
				// stop searching
				break;
			}
		}
		return text;
	}
}
