package com.sitecake.contentmanager.client.item.slideshow;

import com.google.gwt.dom.client.Element;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class SlideshowItemFactory implements ContentItemFactory {

	@Override
	public ContentItem create(Element element) {
		SlideshowItem item = SlideshowItem.create(element);
		return item;
	}

	@Override
	public boolean isFor(Element element) {
		return DomUtil.hasClassName(element, SlideshowItem.DISCRIMINATOR);
	}

}
