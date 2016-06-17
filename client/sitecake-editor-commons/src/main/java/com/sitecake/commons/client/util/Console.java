package com.sitecake.commons.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Console {
	
	public static void log(Object... args) {
		JsArray<JavaScriptObject> arguments = JavaScriptObject.createArray().cast();
		
		for ( Object arg : args ) {
			if ( arg instanceof JavaScriptObject ) {
				arguments.push((JavaScriptObject)arg);
			} else {
				pushString(arguments, arg.toString());
			}
		}
		
		log(arguments);
	}
	
	private static native void log(JsArray<JavaScriptObject> args)/*-{
		( $wnd.console && $wnd.console.log.call(this, args) );
	}-*/;
	
	private static native void pushString(JsArray<JavaScriptObject> array, String value)/*-{
		array.push(value);
	}-*/;
}
