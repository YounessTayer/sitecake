package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ErrorNotificationHandler extends EventHandler {
	public void onErrorNotification(ErrorNotificationEvent event);
}
