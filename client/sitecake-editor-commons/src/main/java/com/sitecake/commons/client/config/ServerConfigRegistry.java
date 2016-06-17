package com.sitecake.commons.client.config;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.inject.Inject;
import com.sitecake.commons.client.util.SynchronizationBarrier;

public class ServerConfigRegistry extends AbstractConfigRegistry {

	private SynchronizationBarrier synchronizationBarrier;
	
	@Inject
	public ServerConfigRegistry(SynchronizationBarrier synchronizationBarrier) {
		super();
		registry = new HashMap<String, String>();
		this.synchronizationBarrier = synchronizationBarrier;
		
		synchronizationBarrier.lock();
		
		String configUrl = getConfigUrl();
		if (configUrl == null || "".equals(configUrl)) {
			throw new IllegalArgumentException("sitecakeGlobals.configUrl not set");
		}
		
		configUrl += (configUrl.contains("?") ? "&" : "?") + "cacherefresh=" + (new Date().getTime());
		
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, configUrl);
		
		rb.setCallback( new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				if ( 200 == response.getStatusCode()) {
					parseConfig(response.getText());
					ServerConfigRegistry.this.synchronizationBarrier.release();
				} else {
					throw new IllegalStateException("Unable to load configuration from " + 
							getConfigUrl() + ": " + response.getStatusText());
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				throw new IllegalStateException("Unable to load configuration from " + getConfigUrl(), exception);
			}
		});
		
		try {
			rb.send();
		} catch (RequestException e) {
			throw new IllegalStateException("Unable to load configuration from " + getConfigUrl(), e);
		}
	}

	private void parseConfig(String text) {
		if ( "".equals(text) ) return;
		String[] lines = text.split("\n");
		for (String line : lines) {
			line = line.trim();
			if ( "".equals(line) || line.startsWith("#") ) continue;
			String[] comps = line.split("=", 2);
			String key = comps[0].trim();
			String val = comps[1].trim();
			registry.put(key, val);
		}
	}
	
	private native String getConfigUrl() /*-{
		return $wnd.sitecakeGlobals.configUrl;
	}-*/;
	
}
