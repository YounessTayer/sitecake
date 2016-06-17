package com.sitecake.contentmanager.client.editable;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;

public class RangeImplIE extends Range {

	protected RangeImplIE() {
		super();
	}
	
	public RangeImplIE(Node startNode, int startOffset, Node endNode,
			int endOffset) {
		super(startNode, startOffset, endNode, endOffset);
	}

	@Override
	public void select() {
		// when the startcontainer is a textnode, which is followed by a blocklevel node (p, h1, ...), 
		// we need to add a <br> in between
		if ( startContainer.getNodeType() == 3 &&
				domUtil.isBlockLevelElement(startContainer.getNextSibling()) ) {
			Node br = DOM.createElement("br");
			startContainer.getParentNode().insertAfter(br, startContainer);

			// we eventually also need to update the offset of the end container
			if ( endContainer.equals(startContainer.getParentNode()) &&
					domUtil.getNodeIndex(startContainer) < endOffset ) {
				endOffset++;
			}
		}
		// now do the JS range/selection stuff
		selectNative();
	}
	
	private native void selectNative()/*-{
		// create a text range
		var ieRange = $doc.body.createTextRange();
	
		// get the start as collapsed range
		var startRange = this.@com.sitecake.contentmanager.client.editable.RangeImplIE::getCollapsedIERange(Lcom/google/gwt/dom/client/Node;I)(
			this.@com.sitecake.contentmanager.client.editable.Range::startContainer, 
			this.@com.sitecake.contentmanager.client.editable.Range::startOffset);
		ieRange.setEndPoint('StartToStart', startRange);
	
		if ( this.@com.sitecake.contentmanager.client.editable.Range::isCollapsed()() ) {
			// collapse the range
			ieRange.collapse();
		} else {
			// get the end as collapsed range
			var endRange = this.@com.sitecake.contentmanager.client.editable.RangeImplIE::getCollapsedIERange(Lcom/google/gwt/dom/client/Node;I)(
				this.@com.sitecake.contentmanager.client.editable.Range::endContainer, 
				this.@com.sitecake.contentmanager.client.editable.Range::endOffset);
			ieRange.setEndPoint('EndToStart', endRange);
		}
	
		// select our range now
		ieRange.select();		
	}-*/;
	
	private native JavaScriptObject getCollapsedIERange(Node startNode, int offset)/*-{
		// create a text range
		var ieRange = document.body.createTextRange();
	
		// search to the left for the next element
		var left = this.@com.sitecake.contentmanager.client.editable.RangeImplIE::searchElementToLeft(Lcom/google/gwt/dom/client/Node;I)(
			startNode, offset);
			
		if (left.element) {
			// found an element, set the start to the end of that element
			var tmpRange = document.body.createTextRange();
			tmpRange.moveToElementText(left.element);
			ieRange.setEndPoint('StartToEnd', tmpRange);
	
			// and correct the start
			if (left.characters != 0) {
				ieRange.moveStart('character', left.characters);
			} else {
				// this is a hack, when we are at the start of a text node, move the range anyway
				ieRange.moveStart('character', 1);
				ieRange.moveStart('character', -1);
			}
		} else {
			// found nothing to the left, so search right
			var right = this.@com.sitecake.contentmanager.client.editable.RangeImplIE::searchElementToRight(Lcom/google/gwt/dom/client/Node;I)(
				startNode, offset);
				
			if (false && right.element) {
				// found an element, set the start to the start of that element
				var tmpRange = document.body.createTextRange();
				tmpRange.moveToElementText(right.element);
				ieRange.setEndPoint('StartToStart', tmpRange);
	
				// and correct the start
				if (right.characters != 0) {
					ieRange.moveStart('character', -right.characters);
				} else {
					ieRange.moveStart('character', -1);
					ieRange.moveStart('character', 1);
				}
			} else {
				// also found no element to the right, use the container itself
				var parent = container.nodeType == 3 ? container.parentNode : container;
				var tmpRange = document.body.createTextRange();
				tmpRange.moveToElementText(parent);
				ieRange.setEndPoint('StartToStart', tmpRange);
	
				// and correct the start
				if (left.characters != 0) {
					ieRange.moveStart('character', left.characters);
				}
			}
		}
		ieRange.collapse();
	
		return ieRange;		
	}-*/;
	
	private native JavaScriptObject searchElementToLeft(Node container, int offset)/*-{
		var checkElement = undefined;
		var characters = 0;
	
		if (container.nodeType == 3) {
			// start is in a text node
			characters = offset;
			// begin check at the element to the left (if any)
			checkElement = container.previousSibling;
		} else {
			// start is between nodes, begin check at the element to the left (if any)
			if (offset > 0) {
				checkElement = container.childNodes[offset - 1];
			}
		}
	
		// move to the right until we find an element
		while (checkElement && checkElement.nodeType == 3) {
			characters += checkElement.data.length;
			checkElement = checkElement.previousSibling;
		}
	
		return {'element' : checkElement, 'characters' : characters};		
	}-*/;
	
	private native JavaScriptObject searchElementToRight(Node container, int offset)/*-{
		var checkElement = undefined;
		var characters = 0;
	
		if (container.nodeType == 3) {
			// start is in a text node
			characters = container.data.length - offset;
	
			// begin check at the element to the right (if any)
			checkElement = container.nextSibling;
		} else {
			// start is between nodes, begin check at the element to the right (if any)
			if (offset < container.childNodes.length) {
				checkElement = container.childNodes[offset];
			}
		}
	
		// move to the right until we find an element
		while (checkElement && checkElement.nodeType == 3) {
			characters += checkElement.data.length;
			checkElement = checkElement.nextSibling;
		}
	
		return {'element' : checkElement, 'characters' : characters};	
	}-*/;
	
}
