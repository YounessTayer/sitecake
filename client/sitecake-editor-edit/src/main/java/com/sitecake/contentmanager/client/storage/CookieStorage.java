package com.sitecake.contentmanager.client.storage;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.ConfigRegistry;

/**
 * Implements a quasi-permanent storage using browser cookie.
 * The storage size is limited by the size of a single cookie (4K).
 */
public class CookieStorage extends AbstractStorage {

	/**
	 * Default cookie name used when there is no name specified by the config.
	 */
	public static final String DEFAULT_COOKIE_NAME = "CookieStorage";
	
	/**
	 * The config key for the storage cookie name.
	 */
	public static final String COOKIE_NAME_CONFIG_KEY = "cookieStorageName";
	
	/**
	 * The cookie name.
	 */
	private String cookieName;
	
	/**
	 * The cookie expire date. The default value is one year from
	 * the storage instantiation.
	 */
	private Date cookieExpireDate;
	
	@Inject
	public CookieStorage(ConfigRegistry config) {
		super();
		
		if ( !isSupported() ) {
			throw new UnsupportedOperationException();
		}
		cookieName = config.get(COOKIE_NAME_CONFIG_KEY, DEFAULT_COOKIE_NAME);
		cookieExpireDate = new Date();
		cookieExpireDate.setTime(cookieExpireDate.getTime() + 365*24*60*60*1000);
	}

	/**
	 * Checks whether the storage can be instantiated.
	 * 
	 * @return true if the storage can be instantiated
	 */
	public static boolean isSupported() {
		return Cookies.isCookieEnabled();
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.storage.AbstractStorage#getRawValue()
	 */
	@Override
	protected String getRawStorage() {
		return Cookies.getCookie(cookieName);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.storage.AbstractStorage#setRawValue(java.lang.String)
	 */
	@Override
	protected void setRawStorage(String value) {
		Cookies.setCookie(cookieName, value, cookieExpireDate);
	}

}
