package com.sitecake.commons.client.config;

public interface ConfigRegistry {
	public boolean contains(String key);
	public String get(String key);
	public String get(String key, String defaultValue);
	public Boolean getBoolean(String key);
	public Boolean getBoolean(String key, Boolean defaultValue);
	public Integer getInteger(String key);
	public Integer getInteger(String key, Integer defaultValue);
	public Double getDouble(String key);
	public Double getDouble(String key, Double defaultValue);
}
