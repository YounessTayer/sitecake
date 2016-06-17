package com.sitecake.contentmanager.client.dom;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class DataTransfer extends JavaScriptObject {

	protected DataTransfer() {}

	public native FileList files()/*-{
		return this.files;
	}-*/;
	
	public native String dropEffect()/*-{
		return this.dropEffect;
	}-*/;

	public native void dropEffect(String value)/*-{
		return this.dropEffect = value;
	}-*/;

	public native String effectAllowed()/*-{
		return this.effectAllowed;
	}-*/;
	
	public native void effectAllowed(String value)/*-{
		return this.effectAllowed = value;
	}-*/;
	
	public native String getData(String format)/*-{
		try {
			return this.getData(format);
		} catch (e) {
			return null;
		}
	}-*/;
	
	public native JsArrayString getFormats()/*-{
		var returnTypes = [];
		if ( !this.types ) return returnTypes;
		var types = this.types;
		if ( !types ) return returnTypes;
		for ( var i = 0; i < types.length; i++ ) {
			returnTypes.push(types[i]);
		}
		return returnTypes;
	}-*/;
	
	public Map<String, String> allData() {
		Map<String, String> data = new HashMap<String, String>();
		JsArrayString formats = getFormats();
		for (int i = 0; i < formats.length(); i++) {
			data.put(formats.get(i), getData(formats.get(i)));
		}
		return data;
	}
}
