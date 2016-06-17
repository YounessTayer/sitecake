package com.sitecake.contentmanager.client.dom;

import com.google.gwt.core.client.JavaScriptObject;

public final class File extends JavaScriptObject {

	protected File() {}

	public native String fileName()/*-{
		return this.fileName;
	}-*/;
	
	public native String mediaType()/*-{
		return this.mediaType;
	}-*/;
	
	public native String type()/*-{
		return this.type;
	}-*/;
	
	public native String name()/*-{
		return this.name;
	}-*/;
	
	public native int size()/*-{
		return this.size;
	}-*/;

	public native boolean isGetAsBinarySupported()/*-{
		return ( this.getAsBinary ) ? true : false;
	}-*/;
	
	public native String getAsBinary()/*-{
		return this.getAsBinary();
	}-*/;
	
	public native String getAsDataURL()/*-{
		return this.getAsDataURL();
	}-*/;

	public native String getAsText()/*-{
		return this.getAsText();
	}-*/;
	
}
