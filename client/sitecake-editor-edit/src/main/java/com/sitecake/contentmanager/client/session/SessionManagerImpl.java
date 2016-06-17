package com.sitecake.contentmanager.client.session;

import java.util.Date;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.ConfigRegistry;
import com.sitecake.commons.client.config.Globals;
import com.sitecake.commons.client.util.UrlBuilder;

public class SessionManagerImpl implements SessionManager {

	private static final String KEEP_ALIVE_INTERVAL = "SessionManager.keepAliveInterval";
	
	private static final int DEFAULT_KEEP_ALIVE_INTERVAL_VALUE = 20000;
	
	private final int keepAliveInterval;
	
	private Timer keepAliveTimer;
	
	private boolean alive;
	
	private RequestBuilder aliveRequestBuilder;
	
	@Inject
	public SessionManagerImpl(ConfigRegistry configRegistry) {
		keepAliveInterval = configRegistry.getInteger(KEEP_ALIVE_INTERVAL, DEFAULT_KEEP_ALIVE_INTERVAL_VALUE);
		
		UrlBuilder aliveUrlBuilder = new UrlBuilder(Globals.get().getServiceUrl());
		aliveUrlBuilder.setParameter("service", "_session");
		aliveUrlBuilder.setParameter("action", "alive");
		aliveRequestBuilder = new RequestBuilder(RequestBuilder.GET, aliveUrlBuilder.buildString());
		
		aliveRequestBuilder.setCallback( new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				success();
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				fail();
				errorHandler(exception.getMessage());
			}
		});
		
		keepAliveTimer = new Timer() {
			public void run() {
				keepAlive();
			}
		};
		keepAliveTimer.scheduleRepeating(keepAliveInterval);
		alive = true;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public boolean isOnline() {
		return true;
	}

	@Override
	public void logout() {
		UrlBuilder urlBuilder = new UrlBuilder(Globals.get().getServiceUrl());
		urlBuilder.setParameter("service", "_session");
		urlBuilder.setParameter("action", "logout");
		
		RequestBuilder logoutRequest = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		
		logoutRequest.setCallback(new RequestCallback() {
			
			public void onResponseReceived(Request request, Response response) {
				if ( 200 == response.getStatusCode()) {
					LogoutResponse logoutResponse = LogoutResponse.get(response.getText()).cast();
					if ( logoutResponse.isSuccess() )
						doLogout();
					else
						errorHandler(logoutResponse.getErrorMessage());
				} else {
					errorHandler(response.getStatusText());
				}
			}
			
			public void onError(Request request, Throwable exception) {
				errorHandler(exception.getMessage());
			}
		});
		
		try {
			logoutRequest.send();
		} catch (RequestException e) {
			errorHandler(e.getMessage());
		}		
	}

	private void keepAlive() {
		UrlBuilder urlBuilder = new UrlBuilder(Globals.get().getServiceUrl());
		urlBuilder.setParameter("service", "_session");
		urlBuilder.setParameter("action", "alive");
		
		try {
			aliveRequestBuilder.send();
		} catch (RequestException e) {
			fail();
			errorHandler(e.getMessage());
		}
	}
	
	private void success() {
		alive = true;
	}
	
	private void fail() {
		alive = false;
	}
	
	private void doLogout() {
		RootPanel.getBodyElement().getStyle().setCursor(Cursor.WAIT);
		String path = Location.getPath();
		path = path.replaceAll("[^\\/]+\\.php", "");
		String page = Location.getParameter("page");
		if (page == null) {
			page = "";
		}
		Location.replace(path + page + "?refresh=" + (new Date().getTime()));
	}
	
	private void errorHandler(String errorMessage) {
		// TODO: notify about the error
	}
}
