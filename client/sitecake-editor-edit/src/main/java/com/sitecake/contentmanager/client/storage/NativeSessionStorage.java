package com.sitecake.contentmanager.client.storage;

/**
 * Implements a wrapper for the native HTML5 sessionStorage.
 */
public class NativeSessionStorage implements Storage {

	public NativeSessionStorage() {
		if ( !isSupported() ) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public native void clear()/*-{
		$wnd.sessionStorage.clear();
	}-*/;

	@Override
	public native String getItem(String key)/*-{
		return $wnd.sessionStorage.getItem(key);
	}-*/;

	@Override
	public native String key(int index)/*-{
		return $wnd.sessionStorage.key(index);
	}-*/;

	@Override
	public native void removeItem(String key)/*-{
		$wnd.sessionStorage.removeItem(key);
	}-*/;

	@Override
	public native void setItem(String key, String value)/*-{
		$wnd.sessionStorage.setItem(key, value);
	}-*/;

	/**
	 * Checks if the sessionStorage object is present.
	 * 
	 * @return true if sessionStorage is present
	 */
	public static native boolean isSupported()/*-{
		return ($wnd.sessionStorage ? true : false);
	}-*/;

}
