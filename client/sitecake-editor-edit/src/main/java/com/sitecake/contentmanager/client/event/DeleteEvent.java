package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class DeleteEvent extends GwtEvent<DeleteHandler> {

	private static final Type<DeleteHandler> TYPE = new Type<DeleteHandler>();

	public static Type<DeleteHandler> getType() {
		return TYPE;
	}
	
	private ContentItem[] items;
	
	public ContentItem[] getItems() {
		return items;
	}

	public DeleteEvent(ContentItem[] items) {
		this.items = items;
	}
	
	@Override
	public final Type<DeleteHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(DeleteHandler handler) {
		handler.onDelete(this);
	}

}
