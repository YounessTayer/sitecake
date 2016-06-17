package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SaveRequestEvent extends GwtEvent<SaveRequestHandler> {
	private static final Type<SaveRequestHandler> TYPE = new Type<SaveRequestHandler>();
	
	public static Type<SaveRequestHandler> getType() {
		return TYPE;
	}
	
	public SaveRequestEvent() {
		super();
	}
	
	@Override
	public final Type<SaveRequestHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(SaveRequestHandler handler) {
		handler.onSaveRequest(this);
	}
}
