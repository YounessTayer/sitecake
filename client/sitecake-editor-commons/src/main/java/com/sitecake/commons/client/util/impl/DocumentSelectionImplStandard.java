package com.sitecake.commons.client.util.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.sitecake.commons.client.util.SelectionRange;

public class DocumentSelectionImplStandard extends DocumentSelectionImpl {

	@Override
	public void collapseToEnd() {
		collapse(true);
	}

	@Override
	public void collapseToStart() {
		collapse(false);
	}

	@Override
	public native void expandToEnd(Element element, boolean inner)/*-{
		var range = this.@com.sitecake.commons.client.util.impl.DocumentSelectionImplStandard::getRange()();
		if ( range ) {
			var targetNode = element;
			if ( inner && element.lastChild ) {
				targetNode = element.lastChild;
			}
			range.setEndAfter(targetNode);
		}
	}-*/;

	@Override
	public native void expandToStart(Element element, boolean inner)/*-{
		var range = this.@com.sitecake.commons.client.util.impl.DocumentSelectionImplStandard::getRange()();
		if ( range ) {
			var targetNode = element;
			if ( inner && element.firstChild ) {
				targetNode = element.firstChild;
			}
			range.setStartBefore(targetNode);
		}
	}-*/;

	@Override
	public int getCaretOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public native void deleteContent()/*-{
		var range = this.@com.sitecake.commons.client.util.impl.DocumentSelectionImplStandard::getRange()();
		if ( range ) {
			range.deleteContents();
		}
	}-*/;
	
	@Override
	public native void setContent(String html, boolean moveAfter)/*-{
		var range = this.@com.sitecake.commons.client.util.impl.DocumentSelectionImplStandard::getRange()();
		if ( range ) {
			range.deleteContents();
			//var tmpRange = 
		}
	}-*/;
	
	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTextContent() {


		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public native boolean isCollapsed()/*-{
		var selection = this.@com.sitecake.commons.client.util.impl.DocumentSelectionImplStandard::getSelectionObject()();
		return ( selection ) ? selection.isCollapsed : true;
	}-*/;

	@Override
	public void select(Element element, boolean inner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void select(Element element, int offset, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCaretOffset(Element element, int offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SelectionRange getSelectedRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public native void remove()/*-{
		var selection = this.@com.sitecake.commons.client.util.impl.DocumentSelectionImplStandard::getSelectionObject()();
		if ( selection ) {
			selection.removeAllRanges();
		}
	}-*/;

	@Override
	public void select(SelectionRange range) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Returns the browser's Selection object.
	 * 
	 * @return JavaScriptObject the browser's Selection object
	 */
	private native JavaScriptObject getSelectionObject()/*-{
		return $wnd.getSelection();
	}-*/;
	
	/**
	 * Returns the browser's Range object.
	 * 
	 * @return JavaScriptObject the browser's Range object
	 */	
	private native JavaScriptObject getRange()/*-{
		var range;
		
		var selection = this.@com.sitecake.commons.client.util.impl.DocumentSelectionImplStandard::getSelectionObject()();
		if ( selection && selection.rangeCount > 0 ) {
			range = selection.getRangeAt(0);
		}

		return range;		
	}-*/;	
	
	private native void collapse(boolean toEnd)/*-{
		var selection = this.@com.sitecake.commons.client.util.impl.DocumentSelectionImplStandard::getSelectionObject()();
		if ( toEnd ) {
			selection.collapseToEnd();
		} else {
			selection.collapseToStart();
		}
	}-*/;	
}
