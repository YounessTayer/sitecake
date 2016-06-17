package com.sitecake.contentmanager.client.editable;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;

/**
 * Represents the browser's native Range object.
 */
public final class NativeRange extends JavaScriptObject {
	
	protected NativeRange() {}

	public native Node getStartContainer()/*-{
		return this.startContainer ? this.startContainer : null;
	}-*/;

	public native int getStartOffset()/*-{
		return this.startOffset;
	}-*/;

	public native Node getEndContainer()/*-{
		return this.endContainer ? this.endContainer : null;
	}-*/;

	public native int getEndOffset()/*-{
		return this.endOffset;
	}-*/;
	
}
