package com.sitecake.contentmanager.client.dom;

import com.google.gwt.core.client.JavaScriptObject;

public class XMLHttpRequestUpload extends JavaScriptObject {

	protected XMLHttpRequestUpload() {}
	
	public final native void setOnProgressHandler(XMLHttpRequestProgressHandler handler)/*-{
		this.onprogress = $entry(function(event) {
			//console.log('progress', event.loaded);
			handler.@com.sitecake.contentmanager.client.dom.XMLHttpRequestProgressHandler::onProgress(Lcom/sitecake/contentmanager/client/dom/XMLHttpRequestProgressEvent;)(event);
		});		
	}-*/;

}
