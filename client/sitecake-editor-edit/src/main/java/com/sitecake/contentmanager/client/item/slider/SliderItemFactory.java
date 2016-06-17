package com.sitecake.contentmanager.client.item.slider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class SliderItemFactory implements ContentItemFactory {

	@Override
	public boolean isFor(Element element) {
		return DomUtil.hasClassName(element, SliderItem.DISCRIMINATOR);
	}

	@Override
	public ContentItem create(Element element) {
		SliderItem item = GWT.create(SliderItem.class);
		item.init(element);
		return item;
	}

}
