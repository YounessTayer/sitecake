package com.sitecake.contentmanager.client.item.video;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class VideoItemFactory implements ContentItemFactory {

	@Override
	public ContentItem create(Element element) {
		VideoItem item = GWT.create(VideoItem.class);
		item.init(element);
		return item;
	}

	@Override
	public boolean isFor(Element element) {
		return DomUtil.hasClassName(element, VideoItem.DISCRIMINATOR);
	}

}
