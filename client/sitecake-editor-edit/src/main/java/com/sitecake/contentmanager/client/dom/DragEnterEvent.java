package com.sitecake.contentmanager.client.dom;

import com.google.gwt.user.client.Event;

public final class DragEnterEvent extends Event {

	protected DragEnterEvent() {}

	public native DataTransfer dataTransfer()/*-{
		return this.dataTransfer;
	}-*/;	
}
