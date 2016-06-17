package com.sitecake.contentmanager.client.editable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.editable.RangeTreeNode.Type;


/**
 * Represents the browser (user) selection.
 */
public abstract class SelectionImpl implements Selection {

	protected EditableDomUtils domUtils = GinInjector.instance.getEditableDomUtils();
	protected DomSelector domSelector = GinInjector.instance.getDomSelector();
	
	private TagWrappingRules tagWrapping = TagWrappingRules.create();
	
	private StealingTags stealingTags = StealingTags.create();
	
	private class StandardMarkup implements Markup {
	
		private Node node;
		
		private int type;
		
		private boolean isReplacing;
		
		public StandardMarkup(Node node) {
			this.node = node.cloneNode(false);
			if ( this.node.getNodeType() == 1 ) {
				Element element = Element.as(this.node);
				element.removeClassName("to-be-removed");
				if ( "".equals(element.getClassName()) ) {
					element.removeAttribute("class");
				}
			}
			decideType();
		}

		@Override
		public Node getNode() {
			return node;
		}

		@Override
		public boolean isReplacing() {
			return isReplacing;
		}

		@Override
		public boolean matches(Node aNode) {
			boolean result;
			
			switch ( type ) {
				case 0:
					result = false;
					break;
				case 1:
					result = groupingComparator(aNode);
					break;
				case 2:
					result = textLevelComparator(aNode);
					break;
				default:
					result = false;
			}
			return result;
		}
		
		private void decideType() {
			String nodeName = node.getNodeName();
			isReplacing = SelectionImpl.this.domUtils.isReplacingElement(node);
			if ( "#text".equalsIgnoreCase(nodeName) ) {
				type = 0;
			} else if ( isReplacing ) {
				type = 1;
			//} else if ( Selection.this.domUtils.isInTagHierarchy(node.getNodeName().toLowerCase()) ) {
			// TODO: implement full check for type 2 using tagHierarchy
			} else if ( node.getNodeType() == 1 ) {
				type = 2;
			} else {
				type = 3; // unknown
			}
		}
		
		private boolean groupingComparator(Node aNode) {
			if ( aNode.getNodeType() == 1 && SelectionImpl.this.domUtils.isReplacingElement(aNode) ) {
				return true;
			} 
			return false;
		}
		
		private boolean textLevelComparator(Node aNode) {
			return SelectionImpl.this.domUtils.textLevelComparator(node, aNode);		
		}
	}
	
	private static final int SET_RANGE_TO_NEW_MARKUP = 2;
	private static final int SET_RANGE_TO_NEXT_SIBLING = 4;
	private static final int SET_RANGE_TO_PREV_SIBLING = 8;
	
	/**
	 * The current selected range.
	 */
	protected Range range;
	
	protected Node topNode;
	
	public SelectionImpl() {
		range = null;
		
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if ( topNode == null ) return;
				
				int type = event.getTypeInt();
				switch ( type ) {
				case Event.ONMOUSEUP:
				case Event.ONKEYUP:
					onSelectionChange();
					break;
				}
			}
		});		
	}

	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#setTopNode(com.google.gwt.dom.client.Node)
	 */
	@Override
	public void setTopNode(Node topNode) {
		this.topNode = topNode;
	}
	
	protected abstract NativeSelection getNativeSelection();

	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#getRange()
	 */
	@Override
	public Range getRange() {
		return range;
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#setRange(com.sitecake.commons.client.editor.RangeImpl)
	 */
	@Override
	public void setRange(Range range) {
		this.range = range;
		this.range.setTopNode(topNode);
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#update()
	 */
	@Override
	public void update() {
		getRange();
		if ( range != null ) {
			range.update(null);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#select(com.google.gwt.dom.client.Node)
	 */
	@Override
	public void select(Node node) {
		if ( node == null ) return;
		
		if ( node.hasChildNodes() ) {
			Node startContainer = node.getFirstChild();
			int startOffset = 0;
			Node endContainer = node.getLastChild();
			int endOffset = endContainer.getNodeType() == 3 ? 
					Text.as(endContainer).getLength() : domUtils.getNodeIndex(endContainer); 
			range = Range.create(startContainer, startOffset, endContainer, endOffset);
			range.setTopNode(topNode);
			range.select();
		} else {
			setCursorInto(node);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#setCursorInto(com.google.gwt.dom.client.Node)
	 */
	@Override
	public void setCursorInto(Node node) {
		if ( topNode.isOrHasChild(node) ) {
			// set a new range into the given dom object
			range = Range.create(node, 0, node, 0);
			range.setTopNode(topNode);
			range.select();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#setCursorAfter(com.google.gwt.dom.client.Node)
	 */
	@Override
	public void setCursorAfter(Node node) {
		if ( !topNode.equals(node) && topNode.isOrHasChild(node) ) {
			Node container = node.getParentNode();
			int offset = domUtils.getNodeIndex(node);
			Range range = Range.create(container, offset, container, offset);
			range.setTopNode(topNode);
			// select the range
			range.select();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#applyMarkup(com.google.gwt.dom.client.Node)
	 */
	@Override
	public void applyMarkup(Node markup) {
		applyMarkup(new StandardMarkup(markup));
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#applyMarkup(com.sitecake.commons.client.editor.Markup)
	 */
	@Override
	public boolean applyMarkup(Markup markup) {

		Range backupRange = null;
		
		// if the element is a replacing element (like p/h1/h2/h3/h4/h5/h6...), which must not wrap each other
		// use a clone of rangeObject
		if ( markup.isReplacing() ) {
			// backup rangeObject for later selection;
			backupRange = range.clone();
			
			// create a new range object to not modify the orginal
			range = range.clone();
			
			// select the top node as the new commonAncestor (CA)
			// update range by setting the CA and automatically recalculate the rangeTree
			range.update(topNode);
		} 
		// if the element is NOT a replacing element, then something needs to be selected, otherwise it can not be wrapped
		// therefore the method can return false, if nothing is selected ( = range is collapsed)
		else {
			if (range.isCollapsed()) {
				return false;
			}
		}

		List<Node> relevantMarkupObjectsAtSelectionStart = getWrappingMarkups(range, true, markup);
		List<Node> relevantMarkupObjectsAtSelectionEnd = getWrappingMarkups(range, false, markup);
		List<Node> relevantMarkupObjectBeforeSelection = null;
		List<Node> relevantMarkupObjectAfterSelection = null;
		
		if ( !markup.isReplacing() && range.getStartOffset() == 0 ) { // don't care about replacers, because they never extend
			Node prevSibling = getTextNodeSibling(false, range.getCommonAncestor(), range.getStartContainer());
			if ( prevSibling != null ) {
				Range tmpRange = new Range(prevSibling, 0, null, 0);
				tmpRange.setTopNode(topNode);
				relevantMarkupObjectBeforeSelection = getWrappingMarkups(tmpRange, true, markup);
			}
		}
		
		if ( !markup.isReplacing() && range.getEndContainer() != null && range.getEndOffset() == range.getEndContainer().getChildCount() ) { // don't care about replacers, because they never extend
			Node nextSibling = getTextNodeSibling(true, range.getCommonAncestor(), range.getEndContainer());
			if ( nextSibling != null ) {
				Range tmpRange2 = new Range(nextSibling, 0, null, 0);
				tmpRange2.setTopNode(topNode);
				relevantMarkupObjectAfterSelection = getWrappingMarkups(tmpRange2, true, markup);
			}
		}
		
		// decide what to do (expand or reduce markup)
		// Alternative A: from markup to no-markup: markup will be removed in selection; 
		// reapplied from original markup start to selection start
		if ( !markup.isReplacing() && relevantMarkupObjectsAtSelectionStart != null && relevantMarkupObjectsAtSelectionEnd == null ) {
			prepareForRemoval(range.getRangeTree(), markup);
			for ( Node aNode : relevantMarkupObjectsAtSelectionStart ) {
				Element.as(aNode).addClassName("to-be-removed");
			}
			insertCroppedMarkups(relevantMarkupObjectsAtSelectionStart, range, true, markup);
		} else
		
		// Alternative B: from markup to markup:
		// remove selected markup (=split existing markup if single, shrink if two different)
		if ( !markup.isReplacing() && relevantMarkupObjectsAtSelectionStart != null && relevantMarkupObjectsAtSelectionEnd != null ) {
			prepareForRemoval(range.getRangeTree(), markup);
			splitRelevantMarkupObject(relevantMarkupObjectsAtSelectionStart, relevantMarkupObjectsAtSelectionEnd, range, markup);
		} else
		
		// Alternative C: from no-markup to markup OR with next2markup: 
		// new markup is wrapped from selection start to end of originalmarkup, original is remove afterwards
		if ( !markup.isReplacing() && ((relevantMarkupObjectsAtSelectionStart == null && relevantMarkupObjectsAtSelectionEnd != null) || 
				relevantMarkupObjectAfterSelection != null || relevantMarkupObjectBeforeSelection != null) ) { // 
			// non-markup 2 markup OR with next2markup
			// move end of rangeObject to end of relevant markups
			if ( relevantMarkupObjectBeforeSelection != null && relevantMarkupObjectAfterSelection != null ) {
				// double extending previous markup(previous and after selection), actually wrapping it
				Range extendedRange = range.clone();
				extendedRange.setStartContainer(domUtils.getTextNodes(relevantMarkupObjectBeforeSelection.get(relevantMarkupObjectBeforeSelection.size() - 1)).get(0));
				extendedRange.setStartOffset(0);
				List<Node> endTextNodes = domUtils.getTextNodes(relevantMarkupObjectAfterSelection.get(relevantMarkupObjectAfterSelection.size() - 1));
				extendedRange.setEndContainer(endTextNodes.get(endTextNodes.size() - 1));
				extendedRange.endOffset = Text.as(extendedRange.endContainer).getLength();
				extendedRange.update();
				applyMarkup(extendedRange.getRangeTree(), range, markup);
				
			} else if ( relevantMarkupObjectBeforeSelection != null && relevantMarkupObjectAfterSelection == null && relevantMarkupObjectsAtSelectionEnd == null ) {
				// extending previous markup
				extendExistingMarkupWithSelection(relevantMarkupObjectBeforeSelection, range, true, markup);

			} else if ( relevantMarkupObjectBeforeSelection != null && relevantMarkupObjectAfterSelection == null && relevantMarkupObjectsAtSelectionEnd != null ) {
				// double extending previous markup(previous and relevant at the end), actually wrapping it
				Range extendedRange = range.clone();
				extendedRange.setStartContainer(domUtils.getTextNodes(relevantMarkupObjectBeforeSelection.get(relevantMarkupObjectBeforeSelection.size() - 1)).get(0));
				extendedRange.setStartOffset(0);
				List<Node> endTextNodes = domUtils.getTextNodes(relevantMarkupObjectsAtSelectionEnd.get(relevantMarkupObjectsAtSelectionEnd.size() - 1));
				extendedRange.setEndContainer(endTextNodes.get(endTextNodes.size() - 1));
				extendedRange.endOffset = Text.as(extendedRange.endContainer).getLength();
				extendedRange.update();
				applyMarkup(extendedRange.getRangeTree(), range, markup);
				
			} else if ( relevantMarkupObjectBeforeSelection == null && relevantMarkupObjectAfterSelection != null ) {
				// extending following markup backwards
				extendExistingMarkupWithSelection(relevantMarkupObjectAfterSelection, range, false, markup);
				
			} else {
				extendExistingMarkupWithSelection(relevantMarkupObjectsAtSelectionEnd, range, false, markup);
			}
		} else	
			
		// Alternative D: no-markup to no-markup: easy
		if ( markup.isReplacing() || ( relevantMarkupObjectsAtSelectionStart == null && relevantMarkupObjectsAtSelectionEnd == null && 
				relevantMarkupObjectBeforeSelection == null && relevantMarkupObjectAfterSelection == null ) ) {
			// non-markup 2 non-markup
			applyMarkup(range.getRangeTree(), range, markup, SET_RANGE_TO_NEW_MARKUP);
		}
		
		// remove all marked items
		removeMarkedElements();
		
		// recalculate CA and selectionTree
		range.update();
		
		// update selection
		if ( markup.isReplacing() ) {
			backupRange.select();
		} else {
			range.select();
		}
		
		return true;
	}
	
	/**
	 * Returns a list of the start or end container's parents (up to the topNode) of the 
	 * given range that match the given markup.
	 * 
	 * @param range
	 * @param start true to use the range's start container, false to use the end container
	 * @param markup
	 * @return a list of nodes or null if not found any parent
	 */
	private List<Node> getWrappingMarkups(Range range, boolean start, Markup markup) {
		List<Node> nodes = null;
		List<Node> parents = range.getContainerParents(start);
		
		if ( parents.size() == 0 ) {
			return nodes;
		}
		
		for ( Node aParent : parents ) {
			if ( markup.matches(aParent) ) {
				if ( nodes == null ) {
					nodes = new ArrayList<Node>();
				}
				nodes.add(aParent);
			}
		}
		
		return nodes;		
	}
	
	/**
	 * Returns a (recursively) next or previous text sibling node of the <code>currentNode</code>.
	 * 
	 * @param next indicates if the next or previous sibling is requested 
	 * @param container the root container of all siblings
	 * @param currentNode the referent node
	 * @return a sibling node or null
	 */
	private Node getTextNodeSibling(boolean next, Node container, Node currentTextNode) {
		List<Node> textNodes = domUtils.getTextNodes(container, true);
		
		int index = textNodes.indexOf(currentTextNode);
		if ( index == -1 ) {
			return null;
		}
	
		int newIndex = index + ( next ? 1 : -1 );
		if ( index < 0 || index >= textNodes.size() ) {
			return null;
		} else {
			return textNodes.get(newIndex);
		}
	}
	
	private void prepareForRemoval(List<RangeTreeNode> rangeTree, Markup markup) {
		for ( RangeTreeNode rangeTreeNode : rangeTree ) {
			if ( rangeTreeNode.node != null && (rangeTreeNode.type.equals(Type.FULL) || 
					(rangeTreeNode.type.equals(Type.PARTIAL) && markup.isReplacing())) ) {
				// mark for removal
				if ( rangeTreeNode.node.getNodeType() == 1 && markup.matches(rangeTreeNode.node) ) {
					Element.as(rangeTreeNode.node).addClassName("to-be-removed");
				}
			}
			if ( rangeTreeNode.type.equals(Type.NONE) && rangeTreeNode.children.size() > 0) {
				prepareForRemoval(rangeTreeNode.children, markup);
			}
			
		}
	}
	
	private void removeMarkedElements() {
		JsArray<Element> marked = domSelector.select(".to-be-removed", topNode);
		for ( int i = 0; i < marked.length(); i++ ) {
			Element element = marked.get(i);
			Node container = element.getParentNode();
			JsArray<Node> children = domUtils.children(element);
			for ( int ci = 0; ci < children.length(); ci++ ) {
				container.insertBefore(children.get(ci), element);
			}
			element.removeFromParent();
		}
	}
	
	private void extendExistingMarkupWithSelection(List<Node> relevantMarkups, Range range, boolean start, Markup markup) {
		for ( Node relevantMarkup : relevantMarkups ) {
			List<Node> textNodes = domUtils.getTextNodes(relevantMarkup, true);
			Range tmpRange = range.clone();
			if ( start ) {
				// start part of range should be used, therefor existing markups are cropped at the end
				tmpRange.setStartContainer(textNodes.get(0));
				tmpRange.setStartOffset(0);
			} else {
				// end part of range should be used, therefor existing markups are cropped at start (beginning)
				tmpRange.setEndContainer(textNodes.get(textNodes.size() - 1));
				tmpRange.setEndOffset(Text.as(tmpRange.getEndContainer()).getLength());
			}
			tmpRange.update();
			applyMarkup(tmpRange.getRangeTree(), range, new StandardMarkup(relevantMarkup), SET_RANGE_TO_NEW_MARKUP);
		}
	}
	
	private void insertCroppedMarkups(List<Node> relevantMarkups, Range range, boolean start, Markup markup) {
		for ( Node relevantMarkup : relevantMarkups ) {
			List<Node> textNodes = domUtils.getTextNodes(relevantMarkup, true);
			Range tmpRange = range.clone();
			if ( start ) {
				tmpRange.startContainer = range.endContainer; // jQuery(el).contents()[0];
				tmpRange.startOffset = range.endOffset;
				tmpRange.endContainer = textNodes.get(textNodes.size() - 1);
				tmpRange.endOffset = Text.as(tmpRange.endContainer).getLength();
				tmpRange.update();
				applyMarkup(tmpRange.getRangeTree(), range, new StandardMarkup(relevantMarkup), SET_RANGE_TO_PREV_SIBLING);				
			} else {
				tmpRange.startContainer = textNodes.get(0);
				tmpRange.startOffset = 0;
				// if the existing markup startContainer & startOffset are equal to the rangeObject startContainer and startOffset,
				// then markupobject does not have to be added again, because it would have no content (zero-length)
				if ( tmpRange.startContainer.equals(range.startContainer) && tmpRange.startOffset == range.startOffset ) {
					continue;
				}
				if ( range.startOffset == 0 ) {
					tmpRange.endContainer = getTextNodeSibling(false, relevantMarkup, range.startContainer);
					tmpRange.endOffset = Text.as(tmpRange.endContainer).getLength();
				} else {
					tmpRange.endContainer = range.startContainer;
					tmpRange.endOffset = range.startOffset;				
				}
				tmpRange.update();
				applyMarkup(tmpRange.getRangeTree(), range, new StandardMarkup(relevantMarkup), SET_RANGE_TO_NEXT_SIBLING);
			}
		}
	
	}
	
	private void splitRelevantMarkupObject(List<Node> relevantStartMarkups, List<Node> relevantEndMarkups, Range range, Markup markup) {
		// mark them to be deleted
		for ( Node node : relevantStartMarkups ) {
			Element.as(node).addClassName("to-be-removed");
		}
		for ( Node node : relevantEndMarkups ) {
			Element.as(node).addClassName("to-be-removed");
		}
		
		// check if the range is identical with the relevantMarkups (in this case the markup can simply be removed)
		if ( areMarkupsAsLongAsRange(relevantStartMarkups, relevantEndMarkups, range) ) {
			return;
		}

		// find intersection (this can always only be one dom element (namely the highest) because all others will be removed
		List<Node> relevantStartAndEndMarkup = intersectRelevantMarkups(relevantStartMarkups, relevantEndMarkups);
		
		if ( relevantStartAndEndMarkup != null ) {
			insertCroppedMarkups(relevantStartAndEndMarkup, range, true, markup);
			insertCroppedMarkups(relevantStartAndEndMarkup, range, false, markup);
		} else {
			insertCroppedMarkups(relevantStartMarkups, range, true, markup);
			insertCroppedMarkups(relevantEndMarkups, range, false, markup);		
		}
	}
	
	/**
	 * Compares a JS array of domobjects with a range object and decides, if the rangeObject spans 
	 * the whole markup objects. method is used to decide if a markup2markup selection can be completely 
	 * remove or if it must be splitted into 2 separate markups.
	 * 
	 * @param startMarkups
	 * @param endMarkups
	 * @param range
	 * @return
	 */
	private boolean areMarkupsAsLongAsRange(List<Node> startMarkups, List<Node> endMarkups, Range range) {
		if (range.startOffset != 0) {
			return false;
		}
		
		for ( Node aNode : startMarkups ) {
			List<Node> textNodes = domUtils.getTextNodes(aNode);
			if ( !range.startContainer.equals(textNodes.get(0)) ) {
				return false;
			}
		}

		for ( Node aNode : endMarkups ) {
			List<Node> textNodes = domUtils.getTextNodes(aNode);
			Text lastTextNode = Text.as(textNodes.get(textNodes.size() - 1));
			if ( !lastTextNode.equals(range.endContainer) || lastTextNode.getLength() != range.endOffset ) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Takes two arrays of bottom up DOM objects, compares them and returns either the object closest to the root or null.
	 * 
	 * @param startMarkups
	 * @param endMarkups
	 * @return a node list that contains only the intersection node (if any) or <code>null</code>
	 */
	private List<Node> intersectRelevantMarkups(List<Node> startMarkups, List<Node> endMarkups) {
		if ( startMarkups == null || endMarkups == null || startMarkups.size() == 0 || endMarkups.size() == 0 ) {
			return null;
		}
		
		List<Node> intersection = null;
		Node intersectionNode = null;
		
		for ( Node startNode : startMarkups ) {
			for ( Node endNode : endMarkups ) {
				if ( endNode.equals(startNode) ) {
					intersectionNode = startNode;
				}
			}
		}
		
		if ( intersectionNode != null ) {
			intersection = new ArrayList<Node>();
			intersection.add(intersectionNode);
		}
		return intersection;
	}
	
	private void applyMarkup(List<RangeTreeNode> rangeTree, Range range, Markup markup) {
		applyMarkup(rangeTree, range, markup, 0);
	}
	
	/**
	 * Applies a certain markup to the given range tree.
	 * 
	 * @param rangeTree SelectionTree Object markup should be applied to
	 * @param range Aloha rangeObject which will be modified to reflect the dom changes, after the the markup was applied (only if activated via options)
	 * @param markup jQuery object of the markup to be applied (e.g. created with obj = jQuery('<b></b>'); )
	 * @param options JS object, with the following boolean properties: setRangeObject2NewMarkup, setRangeObject2NextSibling, setRangeObject2PreviousSibling
	 */
	private void applyMarkup(List<RangeTreeNode> rangeTree, Range range, Markup markup, int options) {
		// first same tags from within fully selected nodes for removal
		prepareForRemoval(rangeTree, markup);
		
		// first let's optimize the selection Tree in useful groups which can be wrapped together
		List<RangeTreeGroup> optimizedRangeTreeGroups = optimizeSelectionTree4Markup(rangeTree, markup);
		
		// now iterate over grouped elements and either recursively dive into object or wrap it as a whole
		for ( RangeTreeGroup rangeTreeGroup : optimizedRangeTreeGroups ) {
			if ( rangeTreeGroup.wrappable ) {
				wrapMarkupAroundSelectionTree(rangeTreeGroup.elements, range, markup, options);
			} else {
				applyMarkup(rangeTreeGroup.element.children, range, markup, options);
			}
		}
	}
	
	private class RangeTreeGroup {
		public boolean wrappable;
		public RangeTreeNode element;
		public List<RangeTreeNode> elements;
	}
	
	/**
	 * Takes a selection tree and groups it into markup wrappable selection trees.
	 * 
	 * @param selectionTree rangeObject selection tree
	 * @param markupObject jQuery object of the markup to be applied (e.g. created with obj = jQuery('<b></b>'); )
	 * @return JS array of wrappable selection trees
	 */
	private List<RangeTreeGroup> optimizeSelectionTree4Markup(List<RangeTreeNode> rangeTree, Markup markup) {
		List<RangeTreeGroup> groupMap = new ArrayList<SelectionImpl.RangeTreeGroup>();
		RangeTreeGroup currentGroup = null;
	
		for ( int i = 0; i < rangeTree.size(); i++ ) {
			RangeTreeNode rangeTreeNode = rangeTree.get(i);
			// we are just interested in selected item
			if ( rangeTreeNode.node != null && !rangeTreeNode.type.equals(Type.NONE) ) {
				if ( markup.isReplacing() && markup.matches(rangeTreeNode.node) ) {
					currentGroup = new RangeTreeGroup();
					currentGroup.wrappable = true;
					currentGroup.elements = new ArrayList<RangeTreeNode>();
					currentGroup.elements.add(rangeTreeNode);
					groupMap.add(currentGroup);
					currentGroup = null;
				
				} else 
				// now check, if the children of our item could be wrapped all together by the markup object	
				if ( canMarkupBeApplied2ElementAsWhole(rangeTreeNode, markup) ) {
					// if yes, add it to the current group
					if ( currentGroup == null ) {
						currentGroup = new RangeTreeGroup();
						groupMap.add(currentGroup);
						currentGroup.wrappable = true;
						currentGroup.elements = new ArrayList<RangeTreeNode>();
					}
					if ( markup.isReplacing() ) { //  && selectionTree[i].domobj.nodeType === 3	
						/* we found the node to wrap for a replacing element. however there might 
						 * be siblings which should be included as well
						 * although they are actually not selected. example:
						 * li
						 * |-textNode ( .selection = 'none')
						 * |-textNode (cursor inside, therefor .selection = 'partial')
						 * |-textNode ( .selection = 'none')
						 * 
						 * in this case it would be useful to select the previous and following textNodes as well (they might result from a previous DOM manipulation)
						 * Think about other cases, where the parent is the Editable. In this case we propably only want to select from and until the next <br /> ??
						 * .... many possibilities, here I realize the two described cases
						 */

						// first find start element starting from the current element going backwards until sibling 0
						int startPosition = i;
						for ( int j = i - 1; j >= 0; j-- ) {
							if ( canMarkupBeApplied2ElementAsWhole(rangeTree.get(j), markup) && isMarkupAllowedToStealSelectionTreeElement(rangeTree.get(j), markup) ) {
								startPosition = j;
							} else {
								break;
							}
						}
						
						// now find the end element starting from the current element going forward until the last sibling
						int endPosition = i;
						for ( int j = i + 1; j < rangeTree.size(); j++) {
							if ( canMarkupBeApplied2ElementAsWhole(rangeTree.get(j), markup) && isMarkupAllowedToStealSelectionTreeElement(rangeTree.get(j), markup) ) {
								endPosition = j;
							} else {
								break;
							}
						}
						
						// now add the elements to the groupMap
						for ( int j = startPosition; j <= endPosition; j++ ) {
							currentGroup.elements.add(rangeTree.get(j));
							rangeTree.get(j).type = Type.FULL; // ??
						}
					} else {
						// normal text level semantics object, no siblings need to be selected
						currentGroup.elements.add(rangeTreeNode);
					}
				} else {
					// if no, isolate it in its own group
					currentGroup = new RangeTreeGroup();
					currentGroup.wrappable = false;
					currentGroup.element = rangeTreeNode;
					groupMap.add(currentGroup);
					currentGroup = null;
				}
			}
		}
		
		return groupMap;		
	}

	private void wrapMarkupAroundSelectionTree(List<RangeTreeNode> rangeTree, Range range, Markup markup, int options) {
		// first let's find out if theoretically the whole selection can be wrapped with one tag and save it for later use
		List<Node> objects2wrap = new ArrayList<Node>(); // // this will be used later to collect objects

		String preText = "";
		String postText = "";

		// now lets iterate over the elements
		for ( RangeTreeNode rangeTreeNode : rangeTree ) {
			
			// check if markup is allowed inside the element's parent
			if ( rangeTreeNode.node != null && !canTag1WrapTag2(rangeTreeNode.node.getParentNode(), markup.getNode()) ) {
				continue;
			}

			// skip empty text nodes
			if ( rangeTreeNode.node != null && rangeTreeNode.node.getNodeType() == 3 && Text.as(rangeTreeNode.node).getLength() == 0 ) {
				continue;
			}

			// partial element, can either be a textnode and therefore be wrapped (at least partially)
			// or can be a nodeType == 1 (tag) which must be dived into
			if ( rangeTreeNode.node != null && rangeTreeNode.type.equals(Type.PARTIAL) && !markup.isReplacing() ) {
				Text textNode = Text.as(rangeTreeNode.node);
				if ( rangeTreeNode.startOffset != -1 && rangeTreeNode.endOffset == -1 ) {
					preText += textNode.getData().substring(0, rangeTreeNode.startOffset);
					textNode.setData(textNode.getData().substring(rangeTreeNode.startOffset, textNode.getData().length() - rangeTreeNode.startOffset));
					objects2wrap.add(rangeTreeNode.node);
				} else if ( rangeTreeNode.endOffset != -1 && rangeTreeNode.startOffset == -1 ) {
					postText += textNode.getData().substring(rangeTreeNode.endOffset, textNode.getData().length() - rangeTreeNode.endOffset);
					textNode.setData(textNode.getData().substring(0, rangeTreeNode.endOffset));
					objects2wrap.add(rangeTreeNode.node);
				} else if ( rangeTreeNode.endOffset != -1 && rangeTreeNode.startOffset != -1 ) {
					if ( rangeTreeNode.startOffset == rangeTreeNode.endOffset ) { // do not wrap empty selections
						continue;
					}
					preText += textNode.getData().substring(0, rangeTreeNode.startOffset);
					String middleText = textNode.getData().substring(rangeTreeNode.startOffset, rangeTreeNode.endOffset - rangeTreeNode.startOffset);
					postText += textNode.getData().substring(rangeTreeNode.endOffset, textNode.getData().length() - rangeTreeNode.endOffset);
					textNode.setData(middleText);
					objects2wrap.add(rangeTreeNode.node);
				} else {
					// a partially selected item without selectionStart/EndOffset is a nodeType 1 Element on the way to the textnode
					applyMarkup(rangeTreeNode.children, range, markup, SET_RANGE_TO_NEW_MARKUP);
				}
			}
			// fully selected dom elements can be wrapped as whole element
			if ( rangeTreeNode.node != null && (rangeTreeNode.type.equals(Type.FULL) || (rangeTreeNode.type.equals(Type.PARTIAL) && markup.isReplacing()))) {
				objects2wrap.add(rangeTreeNode.node);
			}
		}

		if ( objects2wrap.size() > 0 ) {
			// wrap collected DOM object with markupObject

			// make a fix for text nodes in <li>'s in ie
			/*
			jQuery.each(objects2wrap, function(index, element) {
				if (jQuery.browser.msie && element.nodeType == 3
						&& !element.nextSibling && !element.previousSibling
						&& element.parentNode
						&& element.parentNode.nodeName.toLowerCase() == 'li') {
					element.data = jQuery.trim(element.data);
				}
			});
			*/
			
			// wrap all 
			Node parent = objects2wrap.get(0).getParentNode();
			Node insertMarkup = markup.getNode().cloneNode(true);
			parent.insertBefore(insertMarkup, objects2wrap.get(0));
			for ( Node aNode : objects2wrap ) {
				insertMarkup.appendChild(aNode);
			}
			
			Text preTextNode = Document.get().createTextNode(preText);
			parent.insertBefore(preTextNode, insertMarkup);
			Text postTextNode = Document.get().createTextNode(postText);
			parent.insertAfter(postTextNode, insertMarkup);
			
			List<Node> textNodes = domUtils.getTextNodes(insertMarkup);
			
			if ( (options & SET_RANGE_TO_NEW_MARKUP) != 0 ) { // this is used, when markup is added to normal/normal Text

				if ( textNodes.contains(range.startContainer) ) {
					range.startOffset = 0;
				}
				if ( textNodes.contains(range.endContainer) ) {
					range.endOffset = Text.as(range.endContainer).getLength();
				}			
			}
			
			if ( (options & SET_RANGE_TO_NEXT_SIBLING) != 0 ) {
				
				Text textNode2Start = Text.as(textNodes.get(textNodes.size() - 1));
				if ( objects2wrap.contains(range.startContainer) ) {
					range.startContainer = getTextNodeSibling(true, parent, textNode2Start);
					range.startOffset = 0;				
				}
				if ( objects2wrap.contains(range.endContainer) ) {
					range.endContainer = getTextNodeSibling(true, parent, textNode2Start);
					range.endOffset = range.endOffset - textNode2Start.getLength();
				}
			}
			
			if ( (options & SET_RANGE_TO_PREV_SIBLING) != 0 ) {
				Text textNode2Start = Text.as(textNodes.get(0));
				if ( objects2wrap.contains(range.startContainer) ) {
					range.startContainer = getTextNodeSibling(false, parent, textNode2Start);
					range.startOffset = 0;				
				}
				if ( objects2wrap.contains(range.endContainer) ) {
					range.endContainer = this.getTextNodeSibling(false, parent, textNode2Start);
					range.endOffset = Text.as(range.endContainer).getLength();
				}
			}		
		}		
	}
	
	private boolean canTag1WrapTag2(Node wrappingTag, Node wrappedTag) {
		String wrappingTagName = wrappedTag.getNodeName().toLowerCase();
		String wrappedTagName = wrappedTag.getNodeName().toLowerCase();

		return tagWrapping.canWrapp(wrappingTagName, wrappedTagName);		
	}
	
	private boolean canMarkupBeApplied2ElementAsWhole(List<RangeTreeNode> rangeTree, Markup markup) {
		for ( RangeTreeNode rangeTreeNode : rangeTree ) {
			if ( rangeTreeNode.node != null && ( !rangeTreeNode.type.equals(Type.NONE) || markup.isReplacing() ) ) {
				if ( !canTag1WrapTag2(markup.getNode(), rangeTreeNode.node) ) {
					return false;
				}
				if ( rangeTreeNode.children.size() > 0 && !canMarkupBeApplied2ElementAsWhole(rangeTreeNode.children, markup) ) {
					return false;
				}
			}
		}
		return true;		
	}
	
	private boolean canMarkupBeApplied2ElementAsWhole(RangeTreeNode rangeTreeNode, Markup markup) {
		if ( rangeTreeNode.node != null && ( !rangeTreeNode.type.equals(Type.NONE) || markup.isReplacing() ) ) {
			if ( !canTag1WrapTag2(markup.getNode(), rangeTreeNode.node) ) {
				return false;
			}
			if ( rangeTreeNode.children.size() > 0 && !canMarkupBeApplied2ElementAsWhole(rangeTreeNode.children, markup) ) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isMarkupAllowedToStealSelectionTreeElement(RangeTreeNode rangeTreeNode, Markup markup) {
		if ( rangeTreeNode.node == null ) {
			return false;
		}
		String stealingTag = markup.getNode().getNodeName().toLowerCase();
		String aTagToSteal = rangeTreeNode.node.getNodeName().toLowerCase();
		
		return stealingTags.canSteal(stealingTag, aTagToSteal);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#removeSelectedMarkup()
	 */
	@Override
	public void removeSelectedMarkup() {
		removeSelectedMarkup(Range.CLEANUP_MERGE_TEXT | Range.CLEANUP_REMOVE_EMPTY);
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#removeSelectedMarkup()
	 */
	@Override
	public void removeSelectedMarkup(int rangeCleanUpOption) {
		if ( range.isCollapsed() ) {
			return;
		}

		Range newRange = GWT.create(Range.class);
		
		// remove the selection
		removeFromSelectionTree(range.getRangeTree(), newRange);

		// do a cleanup now (starting with the CA)
		range.update();
		range.cleanUp(null, rangeCleanUpOption);
		range = newRange;

		// need to set the collapsed selection now
		range.normalize();
		range.update();
		range.select();
		update();
	}
	
	private void removeFromSelectionTree(List<RangeTreeNode> rangeTree, Range newRange) {
		// remember the first found partially selected element node (in case we need
		// to merge it with the last found partially selected element node)
		Node firstPartialElement = null;

		// iterate through the selection tree
		for ( RangeTreeNode rangeTreeNode : rangeTree ) {
			// check the type of selection
			if ( rangeTreeNode.type.equals(Type.PARTIAL) ) {
				if ( rangeTreeNode.node.getNodeType() == 3 ) {
					// partial text node selected, so remove the selected portion
					String newText = "";
					Text textNode = Text.as(rangeTreeNode.node);
					String data = textNode.getData();
					if ( rangeTreeNode.startOffset > 0 ) {
						newText += data.substring(0, rangeTreeNode.startOffset);
					}
					if ( rangeTreeNode.endOffset < data.length() ) {
						newText += data.substring(rangeTreeNode.endOffset, data.length());
					}
					textNode.setData(newText);

					// eventually set the new range (if not done before)
					if ( newRange.startContainer == null ) {
						newRange.startContainer = rangeTreeNode.node;
						newRange.startOffset = rangeTreeNode.startOffset;
						newRange.endContainer = newRange.startContainer;
						newRange.endOffset = newRange.startOffset;
					}
				} else if ( rangeTreeNode.node.getNodeType() == 1 && rangeTreeNode.children != null ) {
					// partial element node selected, so do the recursion into the children
					removeFromSelectionTree(rangeTreeNode.children, newRange);
					if ( firstPartialElement != null ) {
						// when the first parially selected element is the same type
						// of element, we need to merge them
						if ( firstPartialElement.getNodeName().equals(rangeTreeNode.node.getNodeName()) ) {
							// merge the nodes
							JsArray<Node> children = domUtils.children(rangeTreeNode.node);
							for ( int i = 0; i < children.length(); i++ ) {
								firstPartialElement.appendChild(children.get(i));
							}
							// and remove the latter one
							rangeTreeNode.node.removeFromParent();
						}
					} else {
						// remember this element as first partially selected element
						firstPartialElement = rangeTreeNode.node;
					}
				}
			} else if ( rangeTreeNode.type.equals(Type.FULL) ) {
				// eventually set the new range (if not done before)
				if ( newRange.startContainer == null ) {
					Text adjacentTextNode = domUtils.getNextAdjacentTextNode(rangeTreeNode.node.getParentNode(), 
							domUtils.getNodeIndex(rangeTreeNode.node) + 1, EditableDomUtils.STOP_AT_LINEBREAK | EditableDomUtils.STOP_AT_LIST);
					if ( adjacentTextNode != null ) {
						newRange.startContainer = adjacentTextNode;
						newRange.startOffset = 0;
						newRange.endContainer = newRange.startContainer;
						newRange.endOffset = newRange.startOffset;
					} else {
						newRange.startContainer = rangeTreeNode.node.getParentNode();
						newRange.startOffset = domUtils.getNodeIndex(rangeTreeNode.node) + 1;
						newRange.endContainer = newRange.startContainer;
						newRange.endOffset = newRange.startOffset;
					}
				}

				// full node selected, so just remove it (will also remove all children)
				rangeTreeNode.node.removeFromParent();
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.sitecake.commons.client.editor.Selection#executeEnterKey()
	 */
	@Override
	public void executeEnterKey() {
		if ( range.splitNode != null ) {
			range.split();
		} else {
			range.insertHtmlBreak();
		}		
	}
	
	private Timer updateRangeTimeout = null;
	
	private void onSelectionChange() {
		if ( updateRangeTimeout != null ) {
			updateRangeTimeout.cancel();
			updateRangeTimeout = null;
		}
		updateRangeTimeout = new Timer() {
			@Override
			public void run() {
				updateSelectedRange();
			}
		};
		updateRangeTimeout.schedule(8);
	}
	
	private void updateSelectedRange() {
		updateRangeTimeout = null;
		
		NativeSelection nativeSelection = getNativeSelection();
		NativeRange nativeRange = nativeSelection != null ? nativeSelection.getRangeAt(0) : null;
		if ( nativeSelection != null && nativeRange != null ) {
			
			if ( !topNode.isOrHasChild(nativeRange.getStartContainer()) && 
					!topNode.isOrHasChild(nativeRange.getEndContainer()) ) {
				return;
			}
			
			if ( range == null ) {
				range = Range.create(nativeRange.getStartContainer(), nativeRange.getStartOffset(), 
						nativeRange.getEndContainer(), nativeRange.getEndOffset());
				range.setTopNode(topNode);
			} else {
				range.startContainer = nativeRange.getStartContainer();
				range.startOffset = nativeRange.getStartOffset();
				range.endContainer = nativeRange.getEndContainer();
				range.endOffset = nativeRange.getEndOffset();
				range.normalize();
			}
		}
	}
}
