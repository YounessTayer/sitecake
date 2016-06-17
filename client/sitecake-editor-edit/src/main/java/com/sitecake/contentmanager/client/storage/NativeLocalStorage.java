package com.sitecake.contentmanager.client.storage;

/**
 * Implements a wrapper for the native HTML5 localStorage.
 */
public class NativeLocalStorage implements Storage {

	public NativeLocalStorage() {
		if ( !isSupported() ) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public native void clear()/*-{
		$wnd.localStorage.clear();
	}-*/;

	@Override
	public native String getItem(String key)/*-{
		return $wnd.localStorage.getItem(key);
	}-*/;

	@Override
	public native String key(int index)/*-{
		return $wnd.localStorage.key(index);
	}-*/;

	@Override
	public native void removeItem(String key)/*-{
		$wnd.localStorage.removeItem(key);
	}-*/;

	@Override
	public native void setItem(String key, String value)/*-{
		$wnd.localStorage.setItem(key, value);
	}-*/;

	/**
	 * Checks if the localStorage object is present.
	 * 
	 * @return true if localStorage is present
	 */
	public static native boolean isSupported()/*-{
		return ($wnd.localStorage ? true : false);
	}-*/;
}
