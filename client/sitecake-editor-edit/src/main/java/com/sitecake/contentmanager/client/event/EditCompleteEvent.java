package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditCompleteEvent extends GwtEvent<EditCompleteHandler> {

	private static final Type<EditCompleteHandler> TYPE = new Type<EditCompleteHandler>();
	
	public static Type<EditCompleteHandler> getType() {
		return TYPE;
	}
	
	public EditCompleteEvent() {
		super();
	}
	
	@Override
	public final Type<EditCompleteHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(EditCompleteHandler handler) {
		handler.onEditComplete(this);
	}
	
}
