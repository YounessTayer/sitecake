package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class TextBlockEvent extends GwtEvent<TextBlockHandler> {
	
	private static final Type<TextBlockHandler> TYPE = new Type<TextBlockHandler>();
	
	public static Type<TextBlockHandler> getType() {
		return TYPE;
	}
	
	public enum BlockType {
		HTML,
		VIDEO,
		FLASH,
		MAP
	}
	
	private ContentItem originItem;
	
	private String text;
	
	private BlockType blockType;

	public String getText() {
		return text;
	}

	public BlockType getBlockType() {
		return blockType;
	}

	public ContentItem getOriginItem() {
		return originItem;
	}

	public TextBlockEvent(String text, BlockType blockType, ContentItem originItem) {
		this.text = text;
		this.blockType = blockType;
		this.originItem = originItem;
	}

	@Override
	public final Type<TextBlockHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(TextBlockHandler handler) {
		handler.onTextBlock(this);
	}
}
