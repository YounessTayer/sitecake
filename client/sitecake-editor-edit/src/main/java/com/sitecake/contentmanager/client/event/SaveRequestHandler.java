package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SaveRequestHandler extends EventHandler {
	public void onSaveRequest(SaveRequestEvent event);
}