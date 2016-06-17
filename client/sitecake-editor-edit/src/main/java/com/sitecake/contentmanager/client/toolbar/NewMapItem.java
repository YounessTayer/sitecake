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

public class NewMapItem extends NewItem {

	private static NewMapItemUiBinder uiBinder = GWT.create(NewMapItemUiBinder.class);

	interface NewMapItemUiBinder extends UiBinder<Element, NewMapItem> {}
	
	public NewMapItem() {
		setElement(uiBinder.createAndBindUi(this));
		addStyleName(EditorClientBundle.INSTANCE.css().newMapItem());
		addActiveHandler();
	}

	@Override
	public ContentItemCreator getContentItemCreator() {
		return new ContentItemCreator() {
			public ContentItem create() {
				return PasteHolderItem.create(Type.MAP);
			}
		};
	}

	@Override
	public Widget getDragProxy() {
		HTML proxy = new HTML("<span/><div/>");
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newMapItemActive());
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragProxy());
		return proxy;
	}

	@Override
	public void markAsActive() {
		addStyleName(EditorClientBundle.INSTANCE.css().newMapItemActive());
	}

	@Override
	public void markAsInactive() {
		removeStyleName(EditorClientBundle.INSTANCE.css().newMapItemActive());
	}	
	
}
