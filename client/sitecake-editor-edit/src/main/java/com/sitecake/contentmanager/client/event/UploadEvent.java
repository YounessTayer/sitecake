package com.sitecake.contentmanager.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.toolbar.ContentItemCreator;

public class UploadEvent extends GwtEvent<UploadHandler> {

	private static final Type<UploadHandler> TYPE = new Type<UploadHandler>();

	public static Type<UploadHandler> getType() {
		return TYPE;
	}

	private ContentItem originatingItem;
	
	private List<ContentItemCreator> contentItemCreators;
	
	public ContentItem getOriginatingItem() {
		return originatingItem;
	}

	public List<ContentItemCreator> getContentItemCreators() {
		return contentItemCreators;
	}

	public UploadEvent(ContentItem originatingItem, List<ContentItemCreator> contentItemCreators) {
		this.originatingItem = originatingItem;
		this.contentItemCreators = contentItemCreators;
	}

	@Override
	public final Type<UploadHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(UploadHandler handler) {
		handler.onUpload(this);
	}

}
