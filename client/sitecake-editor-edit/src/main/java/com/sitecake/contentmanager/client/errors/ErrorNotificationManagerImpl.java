package com.sitecake.contentmanager.client.errors;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.TopContainer;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationHandler;
import com.sitecake.contentmanager.client.resources.LocaleProxy;
import com.sitecake.contentmanager.client.resources.Messages;

public class ErrorNotificationManagerImpl implements ErrorNotificationManager {

	private Messages messages;
	
	private ErrorNotificationDisplay errorDisplay;
	
	private TopContainer topContainer;
	
	private int notificationNumber = 1;
	
	@Inject
	public ErrorNotificationManagerImpl(EventBus eventBus, TopContainer topContainer,
			LocaleProxy localeProxy) {
		this.topContainer = topContainer;
		this.messages = localeProxy.messages();
		
		errorDisplay = GWT.create(ErrorNotificationDisplay.class);
		
		topContainer.add(errorDisplay);
		
		eventBus.addHandler(ErrorNotificationEvent.getType(), new ErrorNotificationHandler() {
			@Override
			public void onErrorNotification(ErrorNotificationEvent event) {
				ErrorNotificationManagerImpl.this.onErrorNotification(event);
			}
		});
	}
	
	private void onErrorNotification(ErrorNotificationEvent event) {
		switch ( event.getLevel() ) {
			case WARNING:
			case ERROR:
				nonFatalErrorNotification(event);
				break;
			case FATAL:
				fatalErrorNotification(event);
				break;
		}
	}
	
	private void fatalErrorNotification(ErrorNotificationEvent event) {
		String text = event.getMessage();
		
		if ( event.getDetails() != null ) {
			text += "<br/>" + event.getDetails();
		}
		
		if ( !GWT.isScript() ) {
			System.err.print(text.replaceAll("<br/>", "\n"));			
		}
		
		FatalErrorOverlay overlay = new FatalErrorOverlay(messages.errorMessage1(), messages.errorMessage2(), text);
		topContainer.add(overlay);		
	}
	
	private void nonFatalErrorNotification(ErrorNotificationEvent event) {
		errorDisplay.addNotification(event, notificationNumber++);
	}

}
