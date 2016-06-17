package com.sitecake.commons.client.config;

import java.util.Map;

public abstract class AbstractConfigRegistry implements ConfigRegistry {

	protected Map<String, String> registry;
	
	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.config.ConfigRegistry#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		return registry.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.config.ConfigRegistry#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		return registry.get(key);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.config.ConfigRegistry#get(java.lang.String, java.lang.String)
	 */
	@Override
	public String get(String key, String defaultValue) {
		String result = get(key);
		return ( result != null ) ? result : defaultValue;
	}

	@Override
	public Boolean getBoolean(String key, Boolean defaultValue) {
		Boolean result = getBoolean(key);
		return ( result != null ) ? result : defaultValue;
	}

	@Override
	public Boolean getBoolean(String key) {
		Boolean result = null;
		String value = get(key);
		
		if ( value != null ) {
			result = Boolean.parseBoolean(String.valueOf(value));
		}
		
		return result;
	}

	@Override
	public Double getDouble(String key, Double defaultValue) {
		Double result = getDouble(key);
		return ( result != null ) ? result : defaultValue;
	}

	@Override
	public Double getDouble(String key) {
		Double result = null;
		String value = get(key);
		
		if ( value != null ) {
			result = Double.valueOf(String.valueOf(value));
		}
		
		return result;
	}

	@Override
	public Integer getInteger(String key, Integer defaultValue) {
		Integer result = getInteger(key);
		return ( result != null ) ? result : defaultValue;
	}

	@Override
	public Integer getInteger(String key) {
		Integer result = null;
		String value = get(key);
		
		if ( value != null ) {
			result = Integer.valueOf(String.valueOf(value));
		}
		
		return result;
	}
	
	
}
