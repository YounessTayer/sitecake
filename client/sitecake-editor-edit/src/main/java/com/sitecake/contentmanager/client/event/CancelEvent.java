package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelEvent extends GwtEvent<CancelHandler> {
	private static final Type<CancelHandler> TYPE = new Type<CancelHandler>();
	
	public static Type<CancelHandler> getType() {
		return TYPE;
	}
	
	public CancelEvent() {
		super();
	}
	
	@Override
	public final Type<CancelHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(CancelHandler handler) {
		handler.onCancel(this);
	}
}
