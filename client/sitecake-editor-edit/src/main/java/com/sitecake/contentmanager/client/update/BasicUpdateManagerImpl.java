package com.sitecake.contentmanager.client.update;

import java.util.Date;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.Globals;
import com.sitecake.commons.client.util.MimeBase64;
import com.sitecake.commons.client.util.UrlBuilder;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;
import com.sitecake.contentmanager.client.properties.PropertyManager;
import com.sitecake.contentmanager.client.properties.PropertyScope;
import com.sitecake.contentmanager.client.resources.LocaleProxy;
import com.sitecake.contentmanager.client.resources.Messages;

public class BasicUpdateManagerImpl implements UpdateManager {

	private static final String LAST_UPDATE_CHECK = "UpdateManager.lastCheck";
	
	/**
	 * The min time (in milliseconds) between two successful update checks.
	 */
	private static final long updateCheckTimeMargine = 24*60*60*1000L;
	
	private static String version  = GinInjector.instance.getGlobalConstants().rawVersion();
	
	private static String hostSystem = GinInjector.instance.getGlobalConstants().hostSystem();
	
	private static String updateCheckUrl = GinInjector.instance.getGlobalConstants().updateCheckUrl();
	
	private static String serverVersion = Globals.get().getServerVersionId();
	
	private PropertyManager propertyManager;
	
	private EventBus eventBus;
	
	private Messages messages;
	
	@Inject
	public BasicUpdateManagerImpl(PropertyManager propertyManager, EventBus eventBus, LocaleProxy localeProxy) {
		this.propertyManager = propertyManager;
		this.eventBus = eventBus;
		this.messages = localeProxy.messages();
		
		String lastCheckStr = propertyManager.getProperty(LAST_UPDATE_CHECK, PropertyScope.APPLICATION);
		long lastTime = ( lastCheckStr == null ) ? 0L : Long.valueOf(lastCheckStr);
		long currentTime = new Date().getTime();
		if ( (currentTime - lastTime) > updateCheckTimeMargine ) {
			checkForUpdate();
		}
	}
	
	private void checkForUpdate() {
		UrlBuilder urlBuilder;
		
		try {
			urlBuilder = new UrlBuilder(updateCheckUrl);
		} catch (IllegalArgumentException e) {
			eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, 
					e.getMessage()));
			return;
		}
		
		String data = "version=" + version +
			"&hostSystem=" + hostSystem +
			"&serverVersion=" + serverVersion;
		data = MimeBase64.encode(data);
		
		urlBuilder.setParameter("data", URL.encodeQueryString(data));
		
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		rb.setCallback(new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {}
			
			@Override
			public void onError(Request request, Throwable exception) {}
		});
		try {
			rb.send();
		} catch (RequestException e) {
			// silently skip
		}		
	}
	
	private void errorHandler(String message) {
		// TODO: notify about error
	}

	private void successHandler(UpdateServiceResponse response) {
		
		// remember the time of the last update check
		long currentTime = new Date().getTime();
		propertyManager.setProperty(LAST_UPDATE_CHECK, String.valueOf(currentTime), PropertyScope.APPLICATION);
		/*
		if ( response.getUpdate() ) {
			eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, messages.versionUpdateMessage(response.getUpdateVersion())));
		}
		*/
	}
}
