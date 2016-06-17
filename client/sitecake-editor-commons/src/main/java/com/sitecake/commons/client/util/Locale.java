package com.sitecake.commons.client.util;

import com.google.gwt.user.client.Window.Location;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.ConfigRegistry;

public class Locale {
	private static final String INTERFACE_LOCALE = "Locale.code";
	
	public static final String DEFAULT_LOCALE = "en-US";
	
	private ConfigRegistry configRegistry;
	
	@Inject
	public Locale(ConfigRegistry configRegistry) {
		this.configRegistry = configRegistry;
	}
	
	public String code() {
		String interfaceLocale = Location.getParameter("scln");
		if (interfaceLocale != null && !"".equals(interfaceLocale)) {
			return interfaceLocale;
		}
		
		interfaceLocale = configRegistry.get(INTERFACE_LOCALE);
		if (interfaceLocale == null || "".equals(interfaceLocale)) {
			return DEFAULT_LOCALE;
		}
		
		if ("auto".equalsIgnoreCase(interfaceLocale)) {
			return getUserLocale();
		}
		
		return interfaceLocale;
	}
	
	private native String getUserLocale()/*-{
		return $wnd.navigator.userLanguage || $wnd.navigator.language;		
	}-*/;
	
}
