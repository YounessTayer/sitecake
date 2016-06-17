package com.sitecake.contentmanager.client.item.photoset;

import com.google.gwt.dom.client.Element;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class PhotoSetItemFactory implements ContentItemFactory {

	@Override
	public boolean isFor(Element element) {
		return DomUtil.hasClassName(element, PhotoSetItem.DISCRIMINATOR);
	}

	@Override
	public ContentItem create(Element element) {
		PhotoSetItem item = PhotoSetItem.create(element);
		return item;
	}

}
