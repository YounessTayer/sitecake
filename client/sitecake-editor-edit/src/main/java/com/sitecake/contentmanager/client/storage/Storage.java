package com.sitecake.contentmanager.client.storage;

/**
 * The general storage API. It resembles the HTML5 Storage API,
 * so the native implementation could be realized easily.
 */
public interface Storage {
	
	/**
	 * Returns the item's key at the given index.
	 * 
	 * @param index the item's index
	 * @return the item's key at the given index or <code>null</code> if <code>index</code>
	 * 			is out of the range.
	 */
	public String key(int index);
	
	/**
	 * Stores a new item, with the given key and value, into the storage.
	 * 
	 * @param key the item's key
	 * @param value the item's value
	 */
	public void setItem(String key, String value);
	
	/**
	 * Returns an item with the given key from the storage.
	 * 
	 * @param key the item's key
	 * @return the item's value or <code>null</code> if not found
	 */
	public String getItem(String key);
	
	/**
	 * Removes an item with the given key.
	 * 
	 * @param key the item's key
	 */
	public void removeItem(String key);
	
	/**
	 * Empties the storage.
	 */
	public void clear();
}
