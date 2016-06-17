package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PageManagerEvent extends GwtEvent<PageManagerHandler> {
	private static final Type<PageManagerHandler> TYPE = new Type<PageManagerHandler>();
	
	public static Type<PageManagerHandler> getType() {
		return TYPE;
	}
	
	private boolean open;
		
	public boolean isOpen() {
		return open;
	}

	public PageManagerEvent(boolean open) {
		super();
		this.open = open;
	}
	
	@Override
	public final Type<PageManagerHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(PageManagerHandler handler) {
		handler.onPageManager(this);
	}
}
