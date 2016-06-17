package com.sitecake.contentmanager.client.editable;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents the browser's native Selection object.
 */
public final class NativeSelection extends JavaScriptObject {

	protected NativeSelection() {}

	public native NativeRange getRangeAt(int index)/*-{
		return ( this.rangeCount > 0 && index < this.rangeCount && index >= 0 ) ? this.getRangeAt(index) : null;
	}-*/;
	
}