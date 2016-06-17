package com.sitecake.contentmanager.client.item.generic;

import com.google.gwt.dom.client.Element;
import com.google.inject.Inject;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class GenericContentItemFactory implements ContentItemFactory {

	@Inject
	public GenericContentItemFactory() {
	}

	@Override
	public boolean isFor(Element element) {
		return ( element != null ) ? true : false;
	}

	@Override
	public ContentItem create(Element element) {
		return new GenericContentItem(element);
	}

}
