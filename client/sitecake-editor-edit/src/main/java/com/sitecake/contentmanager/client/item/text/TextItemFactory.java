package com.sitecake.contentmanager.client.item.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.inject.Inject;
import com.sitecake.commons.client.util.JavaScriptRegExp;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;

public class TextItemFactory implements ContentItemFactory {
	
	private static final String TAG_NAME_PATTERN = "^(?:p|h[1-6])$";
	
	@Inject
	public TextItemFactory() {
	}

	@Override
	public boolean isFor(Element element) {
		String tagName = element.getTagName().toLowerCase();
		return JavaScriptRegExp.test(TAG_NAME_PATTERN, tagName); 
	}

	@Override
	public ContentItem create(Element element) {
		TextItem textItem = GWT.create(TextItem.class);
		textItem.init(element);
		return textItem;
	}
	
}
