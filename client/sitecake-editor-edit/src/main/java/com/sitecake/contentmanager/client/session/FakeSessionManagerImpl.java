package com.sitecake.contentmanager.client.session;

public class FakeSessionManagerImpl implements SessionManager {

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public boolean isOnline() {
		return true;
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}
	
}
