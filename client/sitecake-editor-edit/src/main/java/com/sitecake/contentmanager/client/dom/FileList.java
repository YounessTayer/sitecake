package com.sitecake.contentmanager.client.dom;

import com.google.gwt.core.client.JavaScriptObject;

public final class FileList extends JavaScriptObject {

	protected FileList() {
	}

	public native int length()/*-{
		return this.length;
	}-*/;
	
	public native File index(int index)/*-{
		return this[index]
	}-*/;
	
}
