package com.sitecake.contentmanager.client.item.generic;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.sitecake.contentmanager.client.item.ContentItem;

public class GenericContentItem extends ContentItem {

	public static final String CONTENT_TYPE_NAME = "GENERIC";
	
	private String outerHTML;
	
	public GenericContentItem(Element element) {
		super();
		outerHTML = getOuterHTML(element);
		setElement(element);
	}

	protected GenericContentItem() {
		super();
	}
	
	@Override
	public ContentItem cloneItem() {
		
		GenericContentItem clone = new GenericContentItem();
		clone.outerHTML = this.outerHTML;
		clone.setElement(createElement());
		
		return super.cloneItem(clone);
	}
	
	private String getOuterHTML(Element element) {
		//Element tmpElement = DOM.createDiv();
		//DOM.appendChild((com.google.gwt.user.client.Element) tmpElement, DOM.clone((com.google.gwt.user.client.Element) element, true));
		//return tmpElement.getInnerHTML();
		return element.getString();
	}
	
	private Element createElement() {
		Element tmpElement = DOM.createDiv();
		tmpElement.setInnerHTML(outerHTML);
		return tmpElement.getFirstChildElement();
	}

	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}
		
	@Override
	public String getItemSelector() {
		return ".sc-generic";
	}

	@Override
	public String getHtml() {
		return outerHTML;
	}
	
}
