package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class StopPageManagerEvent extends GwtEvent<StopPageManagerHandler> {

	private static final Type<StopPageManagerHandler> TYPE = new Type<StopPageManagerHandler>();
	
	public static Type<StopPageManagerHandler> getType() {
		return TYPE;
	}
	
	@Override
	public final Type<StopPageManagerHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(StopPageManagerHandler handler) {
		handler.onStopPageManager(this);
	}

}

