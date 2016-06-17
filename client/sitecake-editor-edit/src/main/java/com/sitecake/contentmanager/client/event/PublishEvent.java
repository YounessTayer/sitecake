package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PublishEvent extends GwtEvent<PublishHandler> {

	private static final Type<PublishHandler> TYPE = new Type<PublishHandler>();
	
	public static Type<PublishHandler> getType() {
		return TYPE;
	}
	
	public PublishEvent() {
		super();
	}
	
	@Override
	public final Type<PublishHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(PublishHandler handler) {
		handler.onPublish(this);
	}

}
