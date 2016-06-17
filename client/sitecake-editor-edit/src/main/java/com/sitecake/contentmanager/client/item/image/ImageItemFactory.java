package com.sitecake.contentmanager.client.item.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class ImageItemFactory implements ContentItemFactory {

	@Override
	public ContentItem create(Element element) {
		ImageItem item = GWT.create(ImageItem.class);
		item.init(element);
		return item;
	}

	@Override
	public boolean isFor(Element element) {
		String tagName = element.getTagName().toLowerCase();
		
		if ( tagName.equals("img") ) {
			return true;
		} else if ( tagName.equals("a") && element.getFirstChildElement() != null ) {
			
			String subtagName = element.getFirstChildElement().getTagName().toLowerCase();
			
			if ( subtagName.equals("img") ) {
				return true;
			}
		}
		
		return false;
	}

}
