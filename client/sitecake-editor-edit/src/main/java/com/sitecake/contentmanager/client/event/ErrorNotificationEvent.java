package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ErrorNotificationEvent extends GwtEvent<ErrorNotificationHandler> {

	private static final Type<ErrorNotificationHandler> TYPE = new Type<ErrorNotificationHandler>();

	public static Type<ErrorNotificationHandler> getType() {
		return TYPE;
	}
	
	public enum Level {
		WARNING,
		ERROR,
		FATAL
	}
	
	private Level level;
	
	private String message;
	
	private String details;
	
	public Level getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return details;
	}

	public ErrorNotificationEvent(Level level, String message, String details) {
		super();
		this.level = level;
		this.message = message;
		this.details = details;
	}

	public ErrorNotificationEvent(Level level, String message) {
		super();
		this.level = level;
		this.message = message;
	}

	@Override
	public final Type<ErrorNotificationHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(ErrorNotificationHandler handler) {
		handler.onErrorNotification(this);
	}

}
