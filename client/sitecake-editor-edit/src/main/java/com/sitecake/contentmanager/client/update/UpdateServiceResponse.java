package com.sitecake.contentmanager.client.update;

import com.google.gwt.core.client.JavaScriptObject;

public final class UpdateServiceResponse extends JavaScriptObject {
	
	protected UpdateServiceResponse() {}

	public native boolean getUpdate()/*-{
		return this.update;
	}-*/;
	
	public native String getUpdateVersion()/*-{
		return this.updateVersion;
	}-*/;
	
	public native boolean getValid()/*-{
		return this.valid;
	}-*/;
	
	public native boolean getErrorMessage()/*-{
		return this.errorMessage;
	}-*/;
	
}
