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

public class NewHeading6Item extends NewItem {

	private static NewHeading6ItemUiBinder uiBinder = GWT.create(NewHeading6ItemUiBinder.class);

	interface NewHeading6ItemUiBinder extends UiBinder<Element, NewHeading6Item> {}

	private String style;
	
	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	public NewHeading6Item(String style) {
		this.style = style;
		setElement(uiBinder.createAndBindUi(this));
		addStyleName(EditorClientBundle.INSTANCE.css().newHeading6Item());
		addActiveHandler();
	}

	@Override
	public ContentItemCreator getContentItemCreator() {
		return new ContentItemCreator() {
			public ContentItem create() {
				return TextItem.create(messages.newTextItemDefaultContent(), Type.H6, style, true);
			}
		};
	}

	@Override
	public Widget getDragProxy() {
		HTML proxy = new HTML("<span/><div/>");
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newHeading6ItemActive());
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragProxy());
		return proxy;
	}

	@Override
	public void markAsActive() {
		addStyleName(EditorClientBundle.INSTANCE.css().newHeading6ItemActive());
	}

	@Override
	public void markAsInactive() {
		removeStyleName(EditorClientBundle.INSTANCE.css().newHeading6ItemActive());
	}	
	
}
