package com.sitecake.contentmanager.client.item.contact;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class ContactItemFactory implements ContentItemFactory {

	@Override
	public ContentItem create(Element element) {
		ContactItem item = GWT.create(ContactItem.class);
		item.init(element);
		return item;
	}

	@Override
	public boolean isFor(Element element) {
		return DomUtil.hasClassName(element, ContactItem.DISCRIMINATOR);
	}

}
