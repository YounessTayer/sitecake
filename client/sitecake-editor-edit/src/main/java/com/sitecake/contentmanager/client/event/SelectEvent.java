package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class SelectEvent extends GwtEvent<SelectHandler> {
	private static final Type<SelectHandler> TYPE = new Type<SelectHandler>();
	
	public static Type<SelectHandler> getType() {
		return TYPE;
	}
	
	private ContentItem item;
	
	public ContentItem getItem() {
		return item;
	}

	public SelectEvent(ContentItem item) {
		this.item = item;
	}
	
	@Override
	public final Type<SelectHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(SelectHandler handler) {
		handler.onSelect(this);
	}
}
