package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UndoHandler extends EventHandler {
	public void onUndo(UndoEvent event);
}
