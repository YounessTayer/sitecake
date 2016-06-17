package com.sitecake.commons.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JSObject extends JavaScriptObject {
	protected JSObject() {}
	
	public final native String getProperty(String prop)/*-{
		return ( this[prop] ) ? this[prop] : null;
	}-*/;

	public final native boolean getBooleanProperty(String prop)/*-{
		return ( this[prop] ) ? this[prop] : false;
	}-*/;
	
	public final native double getNumberProperty(String prop)/*-{
		return ( this[prop] ) ? this[prop] : 0;
	}-*/;
	
	public final native JsArray<JSObject> getArrayProperty(String prop)/*-{
		return ( this[prop] ) ? this[prop] : null;	
	}-*/;
	
	public final native JSObject getObjectProperty(String prop)/*-{
		return ( this[prop] ) ? this[prop] : null;	
	}-*/;
}
