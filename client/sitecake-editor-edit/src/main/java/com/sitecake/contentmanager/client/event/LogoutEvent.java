package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class LogoutEvent extends GwtEvent<LogoutHandler> {
	
	private static final Type<LogoutHandler> TYPE = new Type<LogoutHandler>();
	
	public static Type<LogoutHandler> getType() {
		return TYPE;
	}
	
	public LogoutEvent() {
		super();
	}
	
	@Override
	public final Type<LogoutHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(LogoutHandler handler) {
		handler.onLogout(this);
	}

}
