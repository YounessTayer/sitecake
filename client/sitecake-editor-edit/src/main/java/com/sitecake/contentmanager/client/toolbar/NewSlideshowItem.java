package com.sitecake.contentmanager.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.fileuploader.FileUploaderItem;
import com.sitecake.contentmanager.client.item.fileuploader.FileUploaderItem.Type;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public class NewSlideshowItem extends NewItem {

	private static NewSlideshowItemUiBinder uiBinder = GWT
			.create(NewSlideshowItemUiBinder.class);

	interface NewSlideshowItemUiBinder extends
			UiBinder<Element, NewSlideshowItem> {
	}
	
	public NewSlideshowItem() {
		setElement(uiBinder.createAndBindUi(this));
		addStyleName(EditorClientBundle.INSTANCE.css().newSlideshowItem());
		addActiveHandler();
	}

	@Override
	public ContentItemCreator getContentItemCreator() {
		return new ContentItemCreator() {
			public ContentItem create() {
				return FileUploaderItem.create(Type.PHOTOSET);
			}
		};
	}
	
	@Override
	public Widget getDragProxy() {
		HTML proxy = new HTML("<span/><div/>");
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newSlideshowItemActive());
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragProxy());
		return proxy;
	}

	@Override
	public void markAsActive() {
		addStyleName(EditorClientBundle.INSTANCE.css().newSlideshowItemActive());
	}

	@Override
	public void markAsInactive() {
		removeStyleName(EditorClientBundle.INSTANCE.css().newSlideshowItemActive());
	}	
	
}
