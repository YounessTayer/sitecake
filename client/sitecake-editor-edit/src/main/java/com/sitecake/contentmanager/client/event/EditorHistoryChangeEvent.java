package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditorHistoryChangeEvent extends GwtEvent<EditoryHistoryChangeHandler> {

	private static final Type<EditoryHistoryChangeHandler> TYPE = new Type<EditoryHistoryChangeHandler>();

	public static Type<EditoryHistoryChangeHandler> getType() {
		return TYPE;
	}
	
	private Boolean atStart;
	private Boolean atEnd;
	
	public Boolean getAtStart() {
		return atStart;
	}

	public Boolean getAtEnd() {
		return atEnd;
	}
	
	public EditorHistoryChangeEvent(Boolean atStart, Boolean atEnd) {
		this.atStart = atStart;
		this.atEnd = atEnd;
	}
	
	@Override
	public final Type<EditoryHistoryChangeHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(EditoryHistoryChangeHandler handler) {
		handler.onEditoryHistoryChange(this);
	}
}