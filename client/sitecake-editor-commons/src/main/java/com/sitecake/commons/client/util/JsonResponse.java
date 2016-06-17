package com.sitecake.commons.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class JsonResponse extends JSObject {

	protected JsonResponse() {}
	
	final static native JavaScriptObject getEmptyObject()/*-{
		return {};
	}-*/;
	
	public final static JavaScriptObject get(String jsonResponse) {
		JavaScriptObject object = null;

		try {
			JSONValue jsonValue = JSONParser.parseStrict(jsonResponse);
			object = jsonValue.isObject().getJavaScriptObject();
		} catch (Throwable exception) {
			object = getEmptyObject();
		}

		return object;
	}	
}
