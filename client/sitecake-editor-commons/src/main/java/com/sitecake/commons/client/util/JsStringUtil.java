package com.sitecake.commons.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArrayString;

public class JsStringUtil {
	
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
	
	private native static JsArrayString matchJs(String regexp, String string)/*-{
		if ( string )
			return string.match(new RegExp(regexp));
		else
			return null;
	}-*/;
	
	public native static JsArrayString match(String regexp, String regexpAttrs, String string)/*-{
		if ( string )
			return string.match(new RegExp(regexp, regexpAttrs));
		else
			return null;
	}-*/;
	
	public native static String replace(String input, String regexp, String regexpOptions, String replacement)/*-{
		return input.replace(new RegExp(regexp, regexpOptions), replacement);
	}-*/;
	
	public static native int search(String string, String regexp)/*-{
		return string ? string.search(regexp) : -1;
	}-*/;
	
}
