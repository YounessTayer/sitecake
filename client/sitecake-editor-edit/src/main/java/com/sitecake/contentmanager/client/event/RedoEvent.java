package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class RedoEvent extends GwtEvent<RedoHandler> {
	private static final Type<RedoHandler> TYPE = new Type<RedoHandler>();
	
	public static Type<RedoHandler> getType() {
		return TYPE;
	}
	
	public RedoEvent() {
		super();
	}
	
	@Override
	public final Type<RedoHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(RedoHandler handler) {
		handler.onRedo(this);
	}
}
