package com.sitecake.contentmanager.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.list.ListItem;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.resources.Messages;

public class NewListItem extends NewItem {

	private static NewListItemUiBinder uiBinder = GWT
			.create(NewListItemUiBinder.class);

	interface NewListItemUiBinder extends UiBinder<Element, NewListItem> {}

	private String style;
	
	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	public NewListItem(String style) {
		this.style = style;
		setElement(uiBinder.createAndBindUi(this));
		addStyleName(EditorClientBundle.INSTANCE.css().newListItem());
		addActiveHandler();
	}

	@Override
	public ContentItemCreator getContentItemCreator() {
		return new ContentItemCreator() {
			public ContentItem create() {
				return ListItem.create("<li>" + messages.newTextItemDefaultContent() + "</li>", style, true);
			}
		};
	}
	
	@Override
	public Widget getDragProxy() {
		HTML proxy = new HTML("<span/><div/>");
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newListItemActive());
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragProxy());
		return proxy;
	}

	@Override
	public void markAsActive() {
		addStyleName(EditorClientBundle.INSTANCE.css().newListItemActive());
	}

	@Override
	public void markAsInactive() {
		removeStyleName(EditorClientBundle.INSTANCE.css().newListItemActive());
	}	
	
}
