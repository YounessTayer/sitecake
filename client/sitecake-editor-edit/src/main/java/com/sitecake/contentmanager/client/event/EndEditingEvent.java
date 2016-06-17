package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EndEditingEvent extends GwtEvent<EndEditingHandler> {

	private static final Type<EndEditingHandler> TYPE = new Type<EndEditingHandler>();
	
	public static Type<EndEditingHandler> getType() {
		return TYPE;
	}
	
	public EndEditingEvent() {
	}
	
	@Override
	public final Type<EndEditingHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(EndEditingHandler handler) {
		handler.onEndEditing(this);
	}

}
