package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NewItemHandler extends EventHandler {
	public void onNewItem(NewItemEvent event);
}
