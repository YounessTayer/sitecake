package com.sitecake.contentmanager.client.storage;

import com.google.inject.Inject;
import com.sitecake.commons.client.config.ConfigRegistry;

/**
 * <code>ClientPermanetStorageImpl</code> is a proxy for
 * an effective storage implementation: <code>NativeLocalStorage</code>
 * or <code>CookieStorage</code>.
 * 
 */
public class ClientPermanentStorageImpl implements ClientPermanentStorage {

	private Storage effectiveStorage;
	
	@Inject 
	public ClientPermanentStorageImpl(ConfigRegistry config) {
		if ( NativeLocalStorage.isSupported() ) {
			// if HTML5 localStorage is present, use it
			effectiveStorage = new NativeLocalStorage();
		} else if ( CookieStorage.isSupported() ) {
			// if not, fallback to CookieStorage
			effectiveStorage = new CookieStorage(config);
		} else {
			// otherwise, fall to DummyStorage
			effectiveStorage = new DummyStorage();
		}
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.storage.Storage#clear()
	 */
	@Override
	public void clear() {
		effectiveStorage.clear();
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.storage.Storage#getItem(java.lang.String)
	 */
	@Override
	public String getItem(String key) {
		return effectiveStorage.getItem(key);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.storage.Storage#key(int)
	 */
	@Override
	public String key(int index) {
		return effectiveStorage.key(index);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.storage.Storage#removeItem(java.lang.String)
	 */
	@Override
	public void removeItem(String key) {
		effectiveStorage.removeItem(key);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.storage.Storage#setItem(java.lang.String, java.lang.String)
	 */
	@Override
	public void setItem(String key, String value) {
		effectiveStorage.setItem(key, value);
	}
}
