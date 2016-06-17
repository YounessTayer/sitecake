package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class LinkEditorEvent extends GwtEvent<LinkEditorHandler> {

	private static final Type<LinkEditorHandler> TYPE = new Type<LinkEditorHandler>();

	public static Type<LinkEditorHandler> getType() {
		return TYPE;
	}
	
	private String url;
	
	private boolean hidden;
	
	private boolean isBold;
	private boolean isItalic;
	
	public String getUrl() {
		return url;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isBold() {
		return isBold;
	}
	
	public boolean isItalic() {
		return isItalic;
	}
	
	public LinkEditorEvent(String url, boolean hidden) {
		super();
		this.url = url;
		this.hidden = hidden;
	}

	public LinkEditorEvent(String url, boolean hidden, boolean isBold, boolean isItalic) {
		super();
		this.url = url;
		this.hidden = hidden;
		this.isBold = isBold;
		this.isItalic = isItalic;
	}
	
	@Override
	public final Type<LinkEditorHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(LinkEditorHandler handler) {
		handler.onLinkEditor(this);
	}

}
