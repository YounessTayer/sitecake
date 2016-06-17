package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class PostEditingEndEvent extends GwtEvent<PostEditingEndHandler> {

	private static final Type<PostEditingEndHandler> TYPE = new Type<PostEditingEndHandler>();
	
	public static Type<PostEditingEndHandler> getType() {
		return TYPE;
	}
	
	private ContentItem prev;
	private ContentItem curr;
	
	public ContentItem getPrev() {
		return prev;
	}

	public ContentItem getCurr() {
		return curr;
	}

	public PostEditingEndEvent(ContentItem prev, ContentItem curr) {
		this.prev = prev;
		this.curr = curr;
	}
	
	@Override
	public final Type<PostEditingEndHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(PostEditingEndHandler handler) {
		handler.onPostEditingEnd(this);
	}

}
