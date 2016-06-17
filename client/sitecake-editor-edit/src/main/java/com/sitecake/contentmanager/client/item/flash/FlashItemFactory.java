package com.sitecake.contentmanager.client.item.flash;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class FlashItemFactory implements ContentItemFactory {

	@Override
	public ContentItem create(Element element) {
		FlashItem item = GWT.create(FlashItem.class);
		item.init(element);
		return item;
	}

	@Override
	public boolean isFor(Element element) {
		return DomUtil.hasClassName(element, FlashItem.DISCRIMINATOR);
	}

}
