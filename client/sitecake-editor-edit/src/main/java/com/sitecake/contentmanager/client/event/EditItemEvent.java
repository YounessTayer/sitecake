package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class EditItemEvent extends GwtEvent<EditItemHandler> {
	
	private static final Type<EditItemHandler> TYPE = new Type<EditItemHandler>();
	
	public static Type<EditItemHandler> getType() {
		return TYPE;
	}
	
	private ContentItem item;
	
	private String editMode;
	
	public ContentItem getItem() {
		return item;
	}
	
	public String getEditMode() {
		return editMode;
	}


	public EditItemEvent(ContentItem item) {
		this.item = item;
	}

	public EditItemEvent(ContentItem item, String editMode) {
		this.item = item;
		this.editMode = editMode;
	}
	
	@Override
	public final Type<EditItemHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(EditItemHandler handler) {
		handler.onEditItem(this);
	}
}
