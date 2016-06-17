package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class OverItemEvent extends GwtEvent<OverItemHandler> {
	private static final Type<OverItemHandler> TYPE = new Type<OverItemHandler>();
	
	public static Type<OverItemHandler> getType() {
		return TYPE;
	}
	
	private ContentItem item;
	
	public ContentItem getItem() {
		return item;
	}

	public OverItemEvent(ContentItem item) {
		this.item = item;
	}
	
	@Override
	public final Type<OverItemHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(OverItemHandler handler) {
		handler.onOverItem(this);
	}
}
