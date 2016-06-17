package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface PublishHandler extends EventHandler {
	public void onPublish(PublishEvent event);
}
