package com.sitecake.contentmanager.client.item.flash;

import com.google.gwt.dom.client.Element;
import com.sitecake.contentmanager.client.item.ContentItem;

public class FlashItem extends ContentItem {
	
	public static final String DISCRIMINATOR = "sc-flash";
	
	public static final String CONTENT_TYPE_NAME = "FLASH";
	
	protected FlashItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected FlashItem(Element element) {
		super(element);
		// TODO Auto-generated constructor stub
	}

	public void init(Element element) {
		setElement(element);
	}
	
	@Override
	public ContentItem cloneItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}
	
	@Override
	public String getItemSelector() {
		return "div." + DISCRIMINATOR;
	}

	@Override
	public String getHtml() {
		// TODO Auto-generated method stub
		return null;
	}

}
