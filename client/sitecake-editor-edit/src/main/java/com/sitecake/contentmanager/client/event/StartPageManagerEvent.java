package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class StartPageManagerEvent extends GwtEvent<StartPageManagerHandler> {

	private static final Type<StartPageManagerHandler> TYPE = new Type<StartPageManagerHandler>();
	
	public static Type<StartPageManagerHandler> getType() {
		return TYPE;
	}
	
	@Override
	public final Type<StartPageManagerHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(StartPageManagerHandler handler) {
		handler.onStartPageManager(this);
	}

}

