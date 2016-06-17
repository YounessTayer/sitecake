package com.sitecake.contentmanager.client.item.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class ListItemFactory implements ContentItemFactory {

	@Override
	public ContentItem create(Element element) {
		ListItem item = GWT.create(ListItem.class);
		item.init(element);
		return item;
	}

	@Override
	public boolean isFor(Element element) {
		String tagName = element.getTagName().toLowerCase();
		return tagName.equals("ul");
	}

}
