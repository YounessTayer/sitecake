package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface RedoHandler extends EventHandler {
	public void onRedo(RedoEvent event);
}
