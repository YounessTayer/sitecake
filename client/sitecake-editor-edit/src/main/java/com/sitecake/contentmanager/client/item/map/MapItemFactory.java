package com.sitecake.contentmanager.client.item.map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class MapItemFactory implements ContentItemFactory {

	@Override
	public ContentItem create(Element element) {
		MapItem item = GWT.create(MapItem.class);
		item.init(element);
		return item;
	}

	@Override
	public boolean isFor(Element element) {
		return DomUtil.hasClassName(element, MapItem.DISCRIMINATOR);
	}

}
