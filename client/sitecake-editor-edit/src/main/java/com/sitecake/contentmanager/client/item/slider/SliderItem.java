package com.sitecake.contentmanager.client.item.slider;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.resources.Messages;

public class SliderItem extends ContentItem {

	public static final String DISCRIMINATOR = "sc-slider";

	public static final String CONTENT_TYPE_NAME = "SLIDER";

	interface SliderItemUiBinder extends UiBinder<Element, SliderItem> {}
	
	private static SliderItemUiBinder uiBinder = GWT.create(SliderItemUiBinder.class);
	
	interface CssStyle extends CssResource {
	}
	
	@UiField
	CssStyle cssStyle;
	
	@UiField
	DivElement codeContainer;
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	private DomSelector domSelector = GinInjector.instance.getDomSelector();

	private boolean dirty = false;
	
	private List<SliderEntry> entries;
	
	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}

	@Override
	public String getItemSelector() {
		return "div." + DISCRIMINATOR;
	}

	@Override
	public ContentItem cloneItem() {
		SliderItem clone = SliderItem.create(entries);
		cloneItem(clone);
		return clone;
	}

	@Override
	public String getHtml() {
		return "<div class=\"" + DISCRIMINATOR + " " + style + "\">" +
				generateSliderCode(entries) + "</div>";
	}
	
	@Override
	public void onInclusion() {
		super.onInclusion();
		triggerSliderInit();
	}

	public static SliderItem create(List<SliderEntry> entries) {
		SliderItem item = GWT.create(SliderItem.class);
		try {
			item.init(entries);
		} catch (Exception e) {
			item.eventBus.fireEventDeferred(new ErrorNotificationEvent(
					ErrorNotificationEvent.Level.WARNING, item.messages.uncaughtException()));
			item = null;
		}
		
		return item;
	}
		
	public SliderItem() {
	}
	
	public void init(Element origElement) {
		init(extractSliderEntries(origElement), origElement);
	}
	
	public void init(List<SliderEntry> entries) {
		init(entries, null);
	}
	
	public void init(List<SliderEntry> entries, Element origElement) {
		Element element = uiBinder.createAndBindUi(this);
		if ( origElement != null ) {
			DomUtil.replaceElement(origElement, element);
		}		
		setElement(element);
		this.entries = entries;
		
		if ( origElement != null && origElement.getClassName() != null )
			setStyle(origElement.getClassName().replaceAll(DISCRIMINATOR, "").trim());
		
		codeContainer.setInnerHTML(generateSliderCode(entries));
	}
	
	private List<SliderEntry> extractSliderEntries(Element element) {
		List<SliderEntry> entries = new ArrayList<SliderEntry>();
		JsArray<Element> imgs = domSelector.select("li img", element);
		for ( int i = 0; i < imgs.length(); i++ ) {
			Element img = imgs.get(i);
			SliderEntry entry = new SliderEntry();
			entry.setUrl(img.getAttribute("src"));
			entries.add(entry);
		}
		return entries;
	}
	
	private String generateSliderCode(List<SliderEntry> entries) {
		String html = "<div class=\"flexslider\"><ul class=\"slides\">";
		for (SliderEntry entry : entries) {
			html += "<li><img src=\"" + entry.getUrl() + "\" /></li>";
		}
		html += "</ul></div>";
		return html;
	}
	
	private String generateSliderCode() {
		return generateSliderCode(entries);
	}
		
	private native void triggerSliderInit()/*-{
		$doc.dispatchEvent(new CustomEvent('scSliderInit', { 
			detail: {
				target: this.@com.google.gwt.user.client.ui.Widget::getElement()(),
				markup: this.@com.sitecake.contentmanager.client.item.slider.SliderItem::generateSliderCode()()
			},
			bubbles: true, 
			cancelable: true
		}));
	}-*/;

	@Override
	public void setStyle(String style) {
		super.setStyle(style);
		dirty = true;
		triggerSliderInit();
	}

	@Override
	public boolean stopEditing(boolean cancel) {
		super.stopEditing(cancel);

		return dirty && !cancel;
	}

	
}
