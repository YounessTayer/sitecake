package com.sitecake.contentmanager.client.dom;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public final class FileInput extends JavaScriptObject {

	protected FileInput() {}

	public static native FileInput valueOf(Element element)/*-{
		return element;
	}-*/;
	
	public native FileList files()/*-{
		return this.files;
	}-*/;
}
