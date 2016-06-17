package com.sitecake.contentmanager.client.item.html;

import com.google.gwt.dom.client.Element;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class HtmlItemFactory implements ContentItemFactory {

	@Override
	public ContentItem create(Element element) {
		HtmlItem item = HtmlItem.create(element);
		return item;
	}

	@Override
	public boolean isFor(Element element) {
		return DomUtil.hasClassName(element, HtmlItem.DISCRIMINATOR);
	}

}
