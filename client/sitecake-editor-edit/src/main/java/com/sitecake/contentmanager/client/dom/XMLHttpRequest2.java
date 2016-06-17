package com.sitecake.contentmanager.client.dom;

import com.google.gwt.xhr.client.XMLHttpRequest;

public class XMLHttpRequest2 extends XMLHttpRequest {

	protected XMLHttpRequest2() {
		super();
	}
	
	public static XMLHttpRequest2 create2() {
		return (XMLHttpRequest2)create();
	}
	
	/**
	 * Initiates a request with a file.
	 * 
	 * @param file the native File object to be sent with the request
	 */
	public final native void send(File file) /*-{
		this.send(file);
	}-*/;
	
	public final native XMLHttpRequestUpload getUpload()/*-{
		return ( this.upload ) ? this.upload : null;
	}-*/;
	
	public final native boolean isSendAsBinarySupported()/*-{
		return ( this.sendAsBinary ) ? true : false;
	}-*/;
	
	public final native void sendAsBinary(String content)/*-{
		this.sendAsBinary(content);
	}-*/;
	
	public final native void setProgressHandler(XMLHttpRequestProgressHandler handler)/*-{
		this.onprogress = $entry(function(event) {
			handler.@com.sitecake.contentmanager.client.dom.XMLHttpRequestProgressHandler::onProgress(Lcom/sitecake/contentmanager/client/dom/XMLHttpRequestProgressEvent;)(event);
		});		
	}-*/;	
}
