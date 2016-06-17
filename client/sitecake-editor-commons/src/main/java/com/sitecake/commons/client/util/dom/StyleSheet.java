package com.sitecake.commons.client.util.dom;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class StyleSheet extends JavaScriptObject {
	
	public final static native JsArray<StyleSheet> getAll()/*-{
		return $doc.styleSheets;
	}-*/;	
	
	protected StyleSheet() {}
	
	public final native boolean disabled()/*-{
		return this.disabled;
	}-*/;
	
	public final native String href()/*-{
		return this.href;
	}-*/;
	
	public final native String type()/*-{
		return this.type;
	}-*/;
	
	public final native String title()/*-{
		return this.title;
	}-*/;

	public final native JsArray<CSSStyleRule> cssRules()/*-{
		return this.cssRules;
	}-*/;
	
	public final native StyleSheet parentStyleSheet()/*-{
		return this.parentStyleSheet;
	}-*/;
}
