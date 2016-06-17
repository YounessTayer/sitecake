package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface LogoutHandler extends EventHandler {
	public void onLogout(LogoutEvent event);
}
