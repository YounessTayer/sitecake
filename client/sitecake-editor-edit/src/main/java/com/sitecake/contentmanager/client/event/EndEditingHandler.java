package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface EndEditingHandler extends EventHandler {
	public void onEndEditing(EndEditingEvent event);
}
