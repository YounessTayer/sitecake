package com.sitecake.contentmanager.client.item.pasteholder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.EditCompleteEvent;
import com.sitecake.contentmanager.client.event.TextBlockEvent;
import com.sitecake.contentmanager.client.event.TextBlockEvent.BlockType;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.map.MapItem;
import com.sitecake.contentmanager.client.item.video.VideoItem;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.resources.Messages;

public class PasteHolderItem extends ContentItem {
	public static final String CONTENT_TYPE_NAME = "PASTEHOLDER";
	
	private static PasteHolderItemUiBinder uiBinder = GWT.create(PasteHolderItemUiBinder.class);

	interface PasteHolderItemUiBinder extends UiBinder<Element, PasteHolderItem> {}
	
	public enum Type {
		HTML,
		VIDEO,
		FLASH,
		MAP
	}
	
	private Type type;
	
	@UiField
	DivElement text;
	
	@UiField
	TextAreaElement code;
	
	@UiField
	DivElement overlay;
	
	protected Messages messages = GinInjector.instance.getLocaleProxy().messages();
	protected EventBus eventBus = GinInjector.instance.getEventBus();
	
	public static PasteHolderItem create(Type type) {
		PasteHolderItem item = GWT.create(PasteHolderItem.class);
		item.init(type);
		return item;
	}
	
	private void init(Type type) {
		Element element = uiBinder.createAndBindUi(this);
		setElement(element);
		this.type = type;
		
		switch ( type ) {
		case HTML:
			text.addClassName(EditorClientBundle.INSTANCE.css().pasteHolderTextHtml());
			text.setInnerHTML(messages.pasteHtml());
			break;

		case VIDEO:
			text.addClassName(EditorClientBundle.INSTANCE.css().pasteHolderTextVideo());
			text.setInnerHTML(messages.pasteVideo());
			break;

		case FLASH:
			text.addClassName(EditorClientBundle.INSTANCE.css().pasteHolderTextFlash());
			text.setInnerHTML(messages.pasteFlash());
			break;

		case MAP:
			text.addClassName(EditorClientBundle.INSTANCE.css().pasteHolderTextMap());
			text.setInnerHTML(messages.pasteMap());
			break;
		}
	}
	
	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}

	@Override
	public String getItemSelector() {
		return "." + CONTENT_TYPE_NAME;
	}

	@Override
	public ContentItem cloneItem() {
		PasteHolderItem clone = PasteHolderItem.create(type);
		return clone;
	}

	@Override
	public String getHtml() {
		// this kind of item should not be saved
		return "";
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void setSelected(boolean select) {
		if ( select && !selected ) {
			selected = true;
			overlay.getStyle().setDisplay(Display.BLOCK);
		} else if ( !select && selected ){
			selected = false;
			overlay.getStyle().setDisplay(Display.NONE);
		}
	}
	
	@Override
	public void startEditing(String mode) {
		super.startEditing(mode);
		setupPasteHandler(code);
		getElement().addClassName(EditorClientBundle.INSTANCE.css().pasteHolderSelected());
		int width = getElement().getClientWidth() - 10;
		code.getStyle().setPropertyPx("maxWidth", width);
		code.getStyle().setPropertyPx("minWidth", width);
		
		code.focus();
	}

	@Override
	public boolean stopEditing(boolean cancel) {
		if ( !edited ) return false;
		super.stopEditing(cancel);
		removePasteHandler(code);
		
		getElement().removeClassName(EditorClientBundle.INSTANCE.css().pasteHolderSelected());
		
		if ( !"".equals(code.getValue()) ) {
			processCode();
		}
		
		return false;
	}
	
	private void processCode() {
		String text = code.getValue();
		BlockType textType;
		
		switch ( type ) {
			case HTML:
				textType = BlockType.HTML;
				break;
	
			case VIDEO:
				textType = BlockType.VIDEO;
				break;
	
			case FLASH:
				textType = BlockType.FLASH;
				break;
	
			case MAP:
				textType = BlockType.MAP;
				break;
			default:
				return;
		}		
		eventBus.fireEventDeferred(new TextBlockEvent(text, textType, this));
	}
	
	protected native void removePasteHandler(Element element)/*-{
		element.onpaste = null;
	}-*/;

	protected native void setupPasteHandler(Element element)/*-{
		var self = this;
		element.onpaste = function(e) {
			setTimeout(function() {
				self.@com.sitecake.contentmanager.client.item.pasteholder.PasteHolderItem::onPaste()();
			}, 1);
		};
	}-*/;
	
	protected void onPaste() {
		String text = code.getValue();
		boolean isValidInput = false;
		
		switch ( type ) {
			case HTML:
				break;
	
			case VIDEO:
				isValidInput = VideoItem.testText(text);
				break;
	
			case FLASH:
				break;
	
			case MAP:
				isValidInput = MapItem.testText(text);
				break;
				
			default:
				return;
		}
		
		if ( isValidInput ) {
			eventBus.fireEventDeferred(new EditCompleteEvent());
		}
	}
}
