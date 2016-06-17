package com.sitecake.contentmanager.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.text.TextItem;
import com.sitecake.contentmanager.client.item.text.TextItem.Type;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.resources.Messages;

public class NewTextItem extends NewItem {

	private static NewTextItemUiBinder uiBinder = GWT.create(NewTextItemUiBinder.class);

	interface NewTextItemUiBinder extends UiBinder<Element, NewTextItem> {}

	private String style;
	
	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	public NewTextItem(String style) {
		this.style = style;
		
		setElement(uiBinder.createAndBindUi(this));
		addStyleName(EditorClientBundle.INSTANCE.css().newTextItem());
		addActiveHandler();
	}

	@Override
	public ContentItemCreator getContentItemCreator() {
		return new ContentItemCreator() {
			public ContentItem create() {
				return TextItem.create(messages.newTextItemDefaultContent(), Type.P, style, true);
			}
		};
	}

	@Override
	public Widget getDragProxy() {
		HTML proxy = new HTML("<span/><div/>");
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newTextItemActive());
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragProxy());
		return proxy;
	}

	@Override
	public void markAsActive() {
		addStyleName(EditorClientBundle.INSTANCE.css().newTextItemActive());
	}

	@Override
	public void markAsInactive() {
		removeStyleName(EditorClientBundle.INSTANCE.css().newTextItemActive());
	}	
	
}
