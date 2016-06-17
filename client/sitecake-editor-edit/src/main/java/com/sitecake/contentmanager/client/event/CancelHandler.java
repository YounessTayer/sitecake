package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CancelHandler extends EventHandler {
	public void onCancel(CancelEvent event);
}
