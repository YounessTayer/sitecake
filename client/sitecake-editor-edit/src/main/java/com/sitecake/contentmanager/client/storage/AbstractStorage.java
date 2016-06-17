package com.sitecake.contentmanager.client.storage;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStorage implements Storage {

	private class Item {
		public String key;
		public String value;
	}
	
	/**
	 * The default string that delimits storage items (key-value pairs).
	 */
	protected static String ITEM_DELIMITER = "::";
	
	/**
	 * The default string that delimits item's key and value.
	 */
	protected static String VALUE_DELIMITER = "~~";
	
	protected List<Item> items;
	
	protected AbstractStorage() {
	}

	@Override
	public void clear() {
		if ( items != null ) {
			items.clear();
		}
		setRawStorage("");
	}

	@Override
	public String getItem(String key) {
		String value = null;
		int index = getItemIndex(key);
		
		if ( index >= 0 ) {
			value = items.get(index).value;
		}
		
		return value;
	}

	@Override
	public String key(int index) {
		getItems();
		String key = null;
		
		if ( items != null ) {
			key = items.get(index).key;
		}
		
		return key;
	}

	@Override
	public void removeItem(String key) {
		int index = getItemIndex(key);
		
		if ( index >= 0 ) {
			items.remove(index);
		}
		serialize();
	}

	@Override
	public void setItem(String key, String value) {
		Item item;
		int index = getItemIndex(key);
		
		if ( index >= 0 ) {
			item = items.get(index);
			item.key = key;
			item.value = value;
		} else {
			item = new Item();
			item.key = key;
			item.value = value;
			items.add(item);
		}
		
		serialize();
	}

	private List<Item> getItems() {
		if ( items == null ) {
			items = new ArrayList<Item>();
			initRawStorage();
			deserialize();
		}
		
		return items;
	}
	
	private void deserialize() {
		String nameValue = getRawStorage();
		items.clear();
		if ( nameValue != null && !"".equals(nameValue) ) {
			String[] rawItems = nameValue.split(ITEM_DELIMITER);
			for ( int i = 0; i < rawItems.length; i++ ) {
				String[] itemParts = rawItems[i].split(VALUE_DELIMITER);
				Item item = new Item();
				item.key = itemParts[0];
				item.value = (itemParts.length > 1) ? itemParts[1] : "";
				items.add(item);
			}			
		}		
	}
	
	private void serialize() {
		String nameValue = "";
		boolean first = true;
		for ( Item item : items ) {
			nameValue += (!first ? ITEM_DELIMITER : "") + (item.key + VALUE_DELIMITER + item.value);
			first = false;
		}
		setRawStorage(nameValue);			
	}
	
	protected void initRawStorage() {};
	
	protected abstract String getRawStorage();
	
	protected abstract void setRawStorage(String value);

	private int getItemIndex(String key) {
		int index = -1;
		
		List<Item> items = getItems();
		
		for ( int i = 0; i < items.size(); i++ ) {
			if ( items.get(i).key.equals(key) ) {
				index = i;
				break;
			}
		}
		return index;
	}
}
