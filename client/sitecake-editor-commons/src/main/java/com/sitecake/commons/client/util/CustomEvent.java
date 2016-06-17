package com.sitecake.commons.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class CustomEvent extends JavaScriptObject {

	protected CustomEvent() {}
	
	public final static CustomEvent create(String name) {
		return create(name, null, true, true);
	}

	public final static CustomEvent create(String name, JavaScriptObject data) {
		return create(name, data, true, true);
	}
	
	public final static native CustomEvent create(String name, 
			JavaScriptObject data, boolean bubbles, boolean cancelable)/*-{
		return new CustomEvent(name, 
			{ 'detail': data, 'bubbles': bubbles, 'cancelable': cancelable });
	}-*/;
	
	public final native void dispatch(Element element)/*-{
		element.dispatchEvent(this);
	}-*/;
}
