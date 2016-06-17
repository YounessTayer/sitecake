package com.sitecake.commons.client.config;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;

public class ClientConfigRegistry extends AbstractConfigRegistry {

	private static final String CONFIG_OBJECT_NAME = "sitecakeGlobals.config";
	
	@Inject
	public ClientConfigRegistry() {
		registry = new JavaScriptMap(getSource(CONFIG_OBJECT_NAME));
	}
	
	private native JavaScriptObject getSource(String name)/*-{
		return eval('$wnd.' + name);
	}-*/;

}
