package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NewParagraphEvent extends GwtEvent<NewParagraphHandler> {

	private static final Type<NewParagraphHandler> TYPE = new Type<NewParagraphHandler>();

	public static Type<NewParagraphHandler> getType() {
		return TYPE;
	}
	
	private String html;
	
	public String getHtml() {
		return html;
	}

	public NewParagraphEvent(String html) {
		super();
		this.html = html;
	}
	
	@Override
	public final Type<NewParagraphHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(NewParagraphHandler handler) {
		handler.onNewParagraph(this);
	}

}
