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

public class NewHtmlItem extends NewItem {

	private static NewHtmlItemUiBinder uiBinder = GWT.create(NewHtmlItemUiBinder.class);

	interface NewHtmlItemUiBinder extends UiBinder<Element, NewHtmlItem> {}
	
	public NewHtmlItem() {
		setElement(uiBinder.createAndBindUi(this));
		addStyleName(EditorClientBundle.INSTANCE.css().newHtmlItem());
		addActiveHandler();
	}

	@Override
	public ContentItemCreator getContentItemCreator() {
		return new ContentItemCreator() {
			public ContentItem create() {
				return PasteHolderItem.create(Type.HTML);
			}
		};
	}

	@Override
	public Widget getDragProxy() {
		HTML proxy = new HTML("<span/><div/>");
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newHtmlItemActive());
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragProxy());
		return proxy;
	}

	@Override
	public void markAsActive() {
		addStyleName(EditorClientBundle.INSTANCE.css().newHtmlItemActive());
	}

	@Override
	public void markAsInactive() {
		removeStyleName(EditorClientBundle.INSTANCE.css().newHtmlItemActive());
	}	
	
}
