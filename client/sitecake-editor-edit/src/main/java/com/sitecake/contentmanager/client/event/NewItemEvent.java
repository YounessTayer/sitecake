package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.container.ContentContainer;
import com.sitecake.contentmanager.client.toolbar.ContentItemCreator;

public class NewItemEvent extends GwtEvent<NewItemHandler> {

	private static final Type<NewItemHandler> TYPE = new Type<NewItemHandler>();
	
	public static Type<NewItemHandler> getType() {
		return TYPE;
	}
	
	private ContentItemCreator contentItemCreator;
	
	private ContentContainer container;
	
	private int index;
	
	public ContentItemCreator getContentItemCreator() {
		return contentItemCreator;
	}

	public ContentContainer getContainer() {
		return container;
	}

	public int getIndex() {
		return index;
	}

	public NewItemEvent(ContentItemCreator contentItemCreator, ContentContainer container, int index) {
		super();
		this.contentItemCreator = contentItemCreator;
		this.container = container;
		this.index = index;
	}

	@Override
	public final Type<NewItemHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(NewItemHandler handler) {
		handler.onNewItem(this);
	}

}
