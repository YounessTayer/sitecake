package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class UndoEvent extends GwtEvent<UndoHandler> {
	private static final Type<UndoHandler> TYPE = new Type<UndoHandler>();
	
	public static Type<UndoHandler> getType() {
		return TYPE;
	}
	
	public UndoEvent() {
		super();
	}
	
	@Override
	public final Type<UndoHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(UndoHandler handler) {
		handler.onUndo(this);
	}
}
