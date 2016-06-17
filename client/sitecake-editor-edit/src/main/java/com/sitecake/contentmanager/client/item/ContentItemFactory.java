package com.sitecake.contentmanager.client.item;

import com.google.gwt.dom.client.Element;

public interface ContentItemFactory {
	public boolean isFor(Element element);
	public ContentItem create(Element element);
}
