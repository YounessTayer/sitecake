package com.sitecake.contentmanager.client.dom;

import com.google.gwt.user.client.Event;

public final class DropEvent extends Event {

	protected DropEvent() {}
	
	public native DataTransfer dataTransfer()/*-{
		return this.dataTransfer ? this.dataTransfer : null;
	}-*/;
}
