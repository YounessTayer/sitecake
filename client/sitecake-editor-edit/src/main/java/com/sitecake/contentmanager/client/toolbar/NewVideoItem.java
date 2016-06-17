package com.sitecake.contentmanager.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.pasteholder.PasteHolderItem;
import com.sitecake.contentmanager.client.item.pasteholder.PasteHolderItem.Type;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public class NewVideoItem extends NewItem {

	private static NewVideoItemUiBinder uiBinder = GWT.create(NewVideoItemUiBinder.class);

	interface NewVideoItemUiBinder extends UiBinder<Element, NewVideoItem> {}
	
	public NewVideoItem() {
		setElement(uiBinder.createAndBindUi(this));
		addStyleName(EditorClientBundle.INSTANCE.css().newVideoItem());
		addActiveHandler();
	}

	@Override
	public ContentItemCreator getContentItemCreator() {
		return new ContentItemCreator() {
			public ContentItem create() {
				return PasteHolderItem.create(Type.VIDEO);
			}
		};
	}

	@Override
	public Widget getDragProxy() {
		HTML proxy = new HTML("<span/><div/>");
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newVideoItemActive());
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragProxy());
		return proxy;
	}

	@Override
	public void markAsActive() {
		addStyleName(EditorClientBundle.INSTANCE.css().newVideoItemActive());
	}

	@Override
	public void markAsInactive() {
		removeStyleName(EditorClientBundle.INSTANCE.css().newVideoItemActive());
	}	
	
}
