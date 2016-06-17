package com.sitecake.contentmanager.client.dom;

import com.google.gwt.user.client.Event;

public class XMLHttpRequestProgressEvent extends Event {
	
	protected XMLHttpRequestProgressEvent() {}
	
	public final native int loaded()/*-{
		return this.loaded;
	}-*/;
}