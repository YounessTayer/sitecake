package com.sitecake.publicmanager.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.commons.client.config.Globals;

public class ErrorMessageOverlay extends Widget {

	private static ErrorMessageOverlayUiBinder uiBinder = GWT
			.create(ErrorMessageOverlayUiBinder.class);

	interface ErrorMessageOverlayUiBinder extends
			UiBinder<Element, ErrorMessageOverlay> {
	}

	@UiField
	public DivElement firstRow;
	
	@UiField
	public DivElement secondRow;
	
	@UiField
	public DivElement errorDump;
	
	public ErrorMessageOverlay(String firstRow, String secondRow, String errorDump) {
		setElement(uiBinder.createAndBindUi(this));
		
		this.firstRow.setInnerHTML(firstRow);
		this.secondRow.setInnerHTML(secondRow);
		errorDump = "Sitecake version: " + Globals.get().getServerVersionId() + 
				"; PHP version: " + Globals.get().getPhpVersion() + 
				"; User Agent: " + getUserAgent() + "; \n\r" + errorDump;
		this.errorDump.setInnerHTML(errorDump);
	}
	
	private native String getUserAgent()/*-{
		return $wnd.navigator.userAgent;
	}-*/;

}
