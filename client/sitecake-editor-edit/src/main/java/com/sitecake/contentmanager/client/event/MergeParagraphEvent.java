package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MergeParagraphEvent extends GwtEvent<MergeParagraphHandler> {

	private static final Type<MergeParagraphHandler> TYPE = new Type<MergeParagraphHandler>();

	public static Type<MergeParagraphHandler> getType() {
		return TYPE;
	}
	
	public MergeParagraphEvent() {
		super();
	}
	
	@Override
	public final Type<MergeParagraphHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(MergeParagraphHandler handler) {
		handler.onMergeParagraph(this);
	}

}
