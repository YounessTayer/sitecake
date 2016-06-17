package com.sitecake.contentmanager.client.item.html;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.DeleteEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class HtmlItem extends ContentItem {

	public static final String DISCRIMINATOR = "sc-html";
	
	public static final String CONTENT_TYPE_NAME = "HTML";
	
	interface HtmlItemUiBinder extends UiBinder<Element, HtmlItem> {}
	
	private static HtmlItemUiBinder uiBinder = GWT.create(HtmlItemUiBinder.class);
	
	@UiField
	DivElement codeWrapper;
	
	@UiField
	TextAreaElement codeEditor;
	
	private String html;
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	
	public static HtmlItem create(String html) {
		HtmlItem item = GWT.create(HtmlItem.class);
		item.init(html, null);
		return item;
	}

	public static HtmlItem create(Element origElement) {
		HtmlItem item = GWT.create(HtmlItem.class);
		item.init(origElement.getInnerHTML(), origElement);
		return item;
	}
	
	protected HtmlItem() {
		super();
	}

	public void init(String html, Element origElement) {
		Element element = uiBinder.createAndBindUi(this);
		if ( origElement != null ) {
			DomUtil.replaceElement(origElement, element);
		}
		setElement(element);
		codeEditor.getStyle().setDisplay(Display.NONE);
		codeWrapper.setInnerHTML(html);
		this.html = codeWrapper.getInnerHTML();
	}

	@Override
	public ContentItem cloneItem() {
		HtmlItem clone = HtmlItem.create(html);
		return super.cloneItem(clone);
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
		return "<div class=\"sc-html\">" + html + "</div>";
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.item.ContentItem#startEditing(java.lang.String)
	 */
	@Override
	public void startEditing(String mode) {
		super.startEditing(mode);
		
		codeWrapper.getStyle().setDisplay(Display.NONE);

		int width = getElement().getClientWidth() - 10;
		codeEditor.setValue(html);
		codeEditor.getStyle().setDisplay(Display.BLOCK);
		codeEditor.getStyle().setPropertyPx("maxWidth", width);
		codeEditor.getStyle().setPropertyPx("minWidth", width);
		codeEditor.focus();
	}

	/* (non-Javadoc)
	 * @see com.sitecake.contentmanager.client.item.ContentItem#stopEditing(boolean)
	 */
	@Override
	public boolean stopEditing(boolean cancel) {
		boolean dirty = super.stopEditing(cancel);
		
		String newHtml = codeEditor.getValue();
		if ( !html.equals(newHtml) ) {
			if ( "".equals(newHtml) ) {
				eventBus.fireEventDeferred(new DeleteEvent(new ContentItem[] {this}));
				dirty = false;
			} else {
				html = newHtml;
				codeWrapper.setInnerHTML(newHtml);
				dirty = true;
			}
		}
		
		codeWrapper.getStyle().setDisplay(Display.BLOCK);
		codeEditor.getStyle().setDisplay(Display.NONE);
		
		return dirty;
	}
	
}
