package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface EditoryHistoryChangeHandler extends EventHandler {
	public void onEditoryHistoryChange(EditorHistoryChangeEvent event);
}
