package com.sitecake.commons.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class JavaScriptRegExp extends JavaScriptObject {
	
	public native static boolean test(String regexp, String string)/*-{
		return (new RegExp(regexp)).test(string);
	}-*/;

	public native static boolean test(String regexp, String regexpAttrs, String string)/*-{
		return (new RegExp(regexp, regexpAttrs)).test(string);
	}-*/;

	public native static String exec(String regexp, String string)/*-{
		return (new RegExp(regexp)).exec(string);
	}-*/;

	public native static String exec(String regexp, String regexpAttrs, String string)/*-{
		return (new RegExp(regexp, regexpAttrs)).exec(string);
	}-*/;

	private native static JsArrayString matchJs(String regexp, String string)/*-{
		if ( string )
			return string.match(new RegExp(regexp));
		else
			return null;
	}-*/;

	private native static JsArrayString matchJs(JavaScriptRegExp regexp, String string)/*-{
		if ( string )
			return string.match(regexp);
		else
			return null;
	}-*/;
	
	public static List<String> match(String regexp, String string) {
		List<String> result;
		
		JsArrayString jsResult = matchJs(regexp, string);
		
		if ( jsResult != null ) {
			result = new ArrayList<String>();
			for (int index = 0; index < jsResult.length(); index++ ) {
				result.add(jsResult.get(index));
			}
		} else {
			result = null;
		}
		
		return result;		
	}
	
	public native static JsArrayString match(String regexp, String regexpAttrs, String string)/*-{
		if ( string )
			return string.match(new RegExp(regexp, regexpAttrs));
		else
			return null;
	}-*/;
	
	public static List<String> match(JavaScriptRegExp regexp, String string) {
		List<String> result;
		
		JsArrayString jsResult = matchJs(regexp, string);
		
		if ( jsResult != null ) {
			result = new ArrayList<String>();
			for (int index = 0; index < jsResult.length(); index++ ) {
				result.add(jsResult.get(index));
			}
		} else {
			result = null;
		}
		
		return result;		
	}
	
	public native static String replace(String input, String regexp, String regexpOptions, String replacement)/*-{
		return input.replace(new RegExp(regexp, regexpOptions), replacement);
	}-*/;
	
	public static native int search(String string, String regexp)/*-{
		return string ? string.search(regexp) : -1;
	}-*/;
	
	protected JavaScriptRegExp() {
	}

	public static native JavaScriptRegExp create(String regexp)/*-{
		return new RegExp(regexp);
	}-*/;

	public static native JavaScriptRegExp create(String regexp, String regexpAttrs)/*-{
		return new RegExp(regexp, regexpAttrs);
	}-*/;
	
	public final native boolean test(String string)/*-{
		return this.test(string);
	}-*/;

	public final native JsArrayString exec(String string)/*-{
		return this.exec(string);
	}-*/;
	
	public final native JsArrayString match(String string)/*-{
		return string.match(this);
	}-*/;
}
