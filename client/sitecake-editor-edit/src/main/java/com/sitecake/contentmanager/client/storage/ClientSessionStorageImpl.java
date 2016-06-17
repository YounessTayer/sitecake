package com.sitecake.contentmanager.client.storage;


/**
 * <code>ClientSessionStorageImpl</code> is a proxy for
 * an effective storage implementation: <code>NativeSessionStorage</code>
 * or <code>WindowNameStorage</code>.
 */
public class ClientSessionStorageImpl implements ClientSessionStorage {

	private Storage effectiveStorage;
	
	public ClientSessionStorageImpl() {
		if ( NativeSessionStorage.isSupported() ) {
			// if HTML5 sessionStorage is present, use it
			effectiveStorage = new NativeSessionStorage();
		} else {
			// otherwise, fallback to WindowNameStorage
			effectiveStorage = new WindowNameStorage();
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
	}}
