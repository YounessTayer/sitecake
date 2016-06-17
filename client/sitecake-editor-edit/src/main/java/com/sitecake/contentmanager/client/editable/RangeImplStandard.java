package com.sitecake.contentmanager.client.editable;

import com.google.gwt.dom.client.Node;

public class RangeImplStandard extends Range {

	protected RangeImplStandard() {
		super();
	}
	
	public RangeImplStandard(Node startNode, int startOffset, Node endNode,
			int endOffset) {
		super(startNode, startOffset, endNode, endOffset);
	}

	@Override
	public native void select()/*-{
		// create a range
		var range = $doc.createRange();
		
		// set start and endContainer
		range.setStart(this.@com.sitecake.contentmanager.client.editable.Range::startContainer, 
			this.@com.sitecake.contentmanager.client.editable.Range::startOffset);	
		range.setEnd(this.@com.sitecake.contentmanager.client.editable.Range::endContainer, 
			this.@com.sitecake.contentmanager.client.editable.Range::endOffset);
		// update the selection
		$wnd.getSelection().removeAllRanges();
		$wnd.getSelection().addRange(range);		
	}-*/;
	
	
}
