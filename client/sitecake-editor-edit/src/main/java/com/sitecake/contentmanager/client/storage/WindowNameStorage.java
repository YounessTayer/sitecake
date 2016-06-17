package com.sitecake.contentmanager.client.storage;


/**
 * Implements a browser-session-wide storage using <code>window.name</code> property
 * to maintain 
 */
public class WindowNameStorage extends AbstractStorage {

	public WindowNameStorage() {
		super();
	}
	
	/**
	 * Retrieves stored content from <code>window.name</code>.
	 */
	protected native String getRawStorage()/*-{
		return $wnd.name;
	}-*/;
	
	/**
	 * Stores new content into <code>window.name</code>.
	 */
	protected native void setRawStorage(String value)/*-{
		$wnd.name = value;
	}-*/;
}
