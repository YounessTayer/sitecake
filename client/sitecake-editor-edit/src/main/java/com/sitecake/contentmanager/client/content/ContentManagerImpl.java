package com.sitecake.contentmanager.client.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.Globals;
import com.sitecake.commons.client.util.MimeBase64;
import com.sitecake.commons.client.util.UrlBuilder;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.ContentManagamentEvent;
import com.sitecake.contentmanager.client.event.ContentManagamentEvent.EventType;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;
import com.sitecake.contentmanager.client.resources.LocaleProxy;
import com.sitecake.contentmanager.client.resources.Messages;

public class ContentManagerImpl implements ContentManager {
	
	private Map<String, String> pendingContent;
	
	private Map<String, String> preparedContent;
	
	private Map<String, String> savedContent;
	
	private boolean saving;

	private boolean publishing;
	
	private boolean abort;
	
	private RequestBuilder saveRequest;		
	
	private RequestBuilder publishRequest;		
	
	private int MAX_RETRY_ATTEMPTS = 5;

	private int saveRetryCnt;
	
	private int RETRY_BACKOFF_START = 4000;
	
	private int saveRetryBackoff;
	
	private Timer saveRetryTimer;
	
	private String pageId;
	
	private EventBus eventBus;
	
	private Messages messages;
	
	@Inject
	public ContentManagerImpl(EventBus eventBus, LocaleProxy localeProxy) {
		this.eventBus = eventBus;
		this.messages = localeProxy.messages();
		
		pendingContent = new HashMap<String, String>();
		preparedContent = new HashMap<String, String>();
		savedContent = new HashMap<String, String>();
		
		abort = false;
		publishing = false;
		saving = false;
		
		saveRetryCnt = 0;
		saveRetryBackoff = RETRY_BACKOFF_START;
		saveRetryTimer = new Timer() {
			public void run() {
				try {
					saveRequest.send();
				} catch (RequestException e) {
					// do nothing, we already have an error condition
				}
			}
		};
		
		
		pageId = URL.encodeQueryString(getCurrentPageId());
		
		UrlBuilder saveUrlBuilder = new UrlBuilder(Globals.get().getServiceUrl());
		saveUrlBuilder.setParameter("service", "_content");
		saveUrlBuilder.setParameter("action", "save");
		
		saveRequest = new RequestBuilder(RequestBuilder.POST, saveUrlBuilder.buildString());		
		saveRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");		
		saveRequest.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {				
				onSavingResponse(response);			
			}						
			
			@Override
			public void onError(Request request, Throwable exception) {				
				onSavingError(exception.getMessage());			
			}		
		});				

		UrlBuilder publishUrlBuilder = new UrlBuilder(Globals.get().getServiceUrl());
		publishUrlBuilder.setParameter("service", "_content");
		publishUrlBuilder.setParameter("action", "publish");
		
		publishRequest = new RequestBuilder(RequestBuilder.POST, publishUrlBuilder.buildString());		
		publishRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");		
		publishRequest.setCallback(new RequestCallback() {						
			
			@Override
			public void onResponseReceived(Request request, Response response) {				
				onPublishResponse(response);			
			}		
			
			@Override
			public void onError(Request request, Throwable exception) {				
				onPublishError(exception.getMessage());			
			}		
		});	
	}

	@Override
	public void save(Map<String, String> content) {
		abort = false;
		pendingContent.putAll(content);
		
		stopRetryingSave();
		
		if ( !saving ) {
			save();
		}
	}

	@Override
	public void publish(List<String> containers) {
		abort = false;
		if ( containers.size() > 0 ) {
			StringBuilder builder = new StringBuilder();			
			builder.append("scpageid=" + pageId);
			publishRequest.setRequestData(builder.toString());
			
			try {
				publishRequest.send();
				publishing = true;
			} catch (RequestException e) {
				onPublishError(e.getMessage());
			}			
		}
	}

	@Override
	public void abortAll() {
		// there's nothing we can do about aborting publishing
		
		// but with saving we can do something - prevent further processing
		// of the pending content queue
		abort = true;
	}

	@Override
	public boolean isActive() {
		return !pendingContent.isEmpty() || saving || publishing;
	}

	private void save() {
		if ( abort ) return;
		
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		
		for ( String container : pendingContent.keySet() ) {
			if ( !savedContent.containsKey(container) || !pendingContent.get(container).equals(savedContent.get(container)) ) {
				if ( first ) {
					first = false;
					builder.append("scpageid=" + pageId);
				}
				
				builder.append("&");
				builder.append( URL.encodeQueryString( container ) );
				builder.append("=");
				builder.append( URL.encodeQueryString(MimeBase64.encode(pendingContent.get(container))) );
			}
		}
		
		preparedContent.putAll(pendingContent);
		pendingContent.clear();
		
		if ( builder.length() > 0 ) {
			saveRequest.setRequestData(builder.toString());
			
			try {
				saveRequest.send();
				saving = true;
			} catch (RequestException e) {
				onSavingError(e.getMessage());
			}
		} else {
			eventBus.fireEventDeferred(new ContentManagamentEvent(EventType.CONTENT_SAVED));
		}
	}

	private void retrySave() {
		
		if ( !abort && saveRetryCnt < MAX_RETRY_ATTEMPTS ) {
			saveRetryCnt++;
			saveRetryTimer.schedule(saveRetryBackoff);
			saveRetryBackoff = saveRetryBackoff*2;
		} else {
			eventBus.fireEventDeferred(new ContentManagamentEvent(EventType.CONTENT_SAVING_FAILED));
			sendErrorNotification(Level.ERROR, messages.giveUpContentSaving());
			saveRetryCnt = 0;
			saveRetryBackoff = RETRY_BACKOFF_START;
		}
	}
	
	private void stopRetryingSave() {
		saveRetryTimer.cancel();
		saveRetryCnt = 0;
		saveRetryBackoff = RETRY_BACKOFF_START;
	}
	
	private void onSavingError(String message) {
		sendErrorNotification(Level.WARNING, messages.failedAttemptToSaveContent(), message);

		saving = false;
		
		if ( !abort ) {
			retrySave();
		}
	}
	
	private void onSavingResponse(Response response) {
		if ( response.getStatusCode() < 400 ) {
			SaveResponse saveResponse = SaveResponse.get(response.getText()).cast();
			if ( saveResponse.isSuccess() ) {
				savedContent.clear();
				savedContent.putAll(preparedContent);
				stopRetryingSave();
				saving = false;
				save();
			} else {
				sendErrorNotification(Level.WARNING, messages.failedAttemptToSaveContent(), 
						saveResponse.getErrorMessage());
				saving = false;
			}
		} else {
			sendErrorNotification(Level.WARNING, messages.failedAttemptToSaveContent(), response.getStatusText());
			saving = false;
			retrySave();
		}
	}
	
	private String getCurrentPageId() {
		JsArray<Element> metaTags = GinInjector.instance.getDomSelector().select("meta[content='sitecake']");
		return metaTags.get(0).getAttribute("data-pageid");
	};
	
	private void onPublishError(String message) {
		publishing = false;
		sendErrorNotification(Level.WARNING, messages.failedToPublishContent(), message);
		eventBus.fireEventDeferred(new ContentManagamentEvent(EventType.CONTENT_PUBLISHING_FAILED));
	}
	
	private void onPublishResponse(Response response) {
		publishing = false;
		if ( response.getStatusCode() < 400 ) {
			PublishResponse publishResponse = PublishResponse.get(response.getText()).cast();
			if ( publishResponse.isSuccess() ) {
				eventBus.fireEventDeferred(new ContentManagamentEvent(EventType.CONTENT_PUBLISHED));
			} else {
				onPublishError(publishResponse.getErrorMessage());
			}
		} else {
			onPublishError(response.getStatusText());
		}
	}
	
	private void sendErrorNotification(Level level, String message) {
		sendErrorNotification(level, message, null);
	}
	
	private void sendErrorNotification(Level level, String message, String details) {
		eventBus.fireEventDeferred(new ErrorNotificationEvent(level, message, details));
	}
}
