package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class StartEditingEvent extends GwtEvent<StartEditingHandler> {

	private static final Type<StartEditingHandler> TYPE = new Type<StartEditingHandler>();
	
	public static Type<StartEditingHandler> getType() {
		return TYPE;
	}
	
	private ContentItem item;
	
	public ContentItem getItem() {
		return item;
	}

	public StartEditingEvent(ContentItem item) {
		this.item = item;
	}
	
	@Override
	public final Type<StartEditingHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(StartEditingHandler handler) {
		handler.onStartEditing(this);
	}

}
