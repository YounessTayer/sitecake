package com.sitecake.commons.client.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

public class JavaScriptMap implements Map<String, String> {

	private Map<String, String> map;
	
	public JavaScriptMap(JavaScriptObject source) {
		super();
		map = new HashMap<String, String>();
		initMap(source);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public String put(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<String> values() {
		return map.values();
	}
	
	protected void add(String key, String value) {
		map.put(key, value);
	}

	private native void initMap(JavaScriptObject source)/*-{
		if ( source ) {
			for ( var key in source ) {
				if ( source.hasOwnProperty(key) ) {
					// get the value and convert it into a string
					var value = source[key] + '';
					
					// store the value into the internal map
					this.@com.sitecake.commons.client.config.JavaScriptMap::add(Ljava/lang/String;Ljava/lang/String;)(key, value);
				}
			}
		}
	}-*/;
	
}
