package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ContentManagamentEvent extends GwtEvent<ContentManagamentHandler> {

	private static final Type<ContentManagamentHandler> TYPE = new Type<ContentManagamentHandler>();

	public static Type<ContentManagamentHandler> getType() {
		return TYPE;
	}
	
	public enum EventType {
		CONTENT_PUBLISHED,
		CONTENT_PUBLISHING_FAILED,
		CONTENT_SAVED,
		CONTENT_SAVING_FAILED
	}
	
	private EventType eventType;
	
	public EventType getEventType() {
		return eventType;
	}

	public ContentManagamentEvent(EventType eventType) {
		super();
		this.eventType = eventType;
	}

	@Override
	public final Type<ContentManagamentHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(ContentManagamentHandler handler) {
		handler.onContentManagament(this);
	}

}
