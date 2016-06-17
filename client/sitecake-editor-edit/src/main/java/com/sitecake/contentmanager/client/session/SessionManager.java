package com.sitecake.contentmanager.client.session;

public interface SessionManager {
	public boolean isAlive();
	public boolean isOnline();
	public void logout();
}
