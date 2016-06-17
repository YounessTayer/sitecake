package com.sitecake.commons.client.config;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Provides access to the SiteCake's global settings. 
 */
public class Globals extends JavaScriptObject {

	protected Globals() {
	}

	/**
	 * Returns the overlay's reference.
	 * @return a reference to <code>Globals</code> overlay
	 */
	public final native static Globals get()/*-{
		return $wnd.sitecakeGlobals;
	}-*/;
	
	public final native boolean getForceLoginDialog()/*-{
		if ( this.forceLoginDialog ) {
			return this.forceLoginDialog;
		} else {
			return false;
		} 
	}-*/;
	
	public final native boolean getDraftPublished()/*-{
		if ( this.draftPublished ) {
			return this.draftPublished;
		} else {
			return false;
		} 
	}-*/;

	public final native String getServiceUrl()/*-{
		return this.serviceUrl;
	}-*/;
	
	public final native String getServerVersionId()/*-{
		return ( this.serverVersionId ) ? this.serverVersionId : "<unknown>";
	}-*/;
	
	public final native String getPhpVersion()/*-{
		return ( this.phpVersion ) ? this.phpVersion : "<unknown>";
	}-*/;	
}
