package com.sitecake.contentmanager.client.errors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.commons.client.config.Globals;

public class FatalErrorOverlay extends Widget {

	private static ErrorMessageOverlayUiBinder uiBinder = GWT
			.create(ErrorMessageOverlayUiBinder.class);

	interface ErrorMessageOverlayUiBinder extends
			UiBinder<Element, FatalErrorOverlay> {
	}

	@UiField
	public Element firstRow;
	
	@UiField
	public Element secondRow;
	
	@UiField
	public Element errorDump;
	
	public FatalErrorOverlay(String firstRow, String secondRow, String errorDump) {
		setElement(uiBinder.createAndBindUi(this));
		
		this.firstRow.setInnerHTML(firstRow);
		this.secondRow.setInnerHTML(secondRow);

		errorDump = "Sitecake Version: " + Globals.get().getServerVersionId() + 
				"; PHP Version: " + Globals.get().getPhpVersion() + 
				"; User Agent: " + getUserAgent() + "; \n\r" + errorDump;
		this.errorDump.setInnerHTML(errorDump);
	}
	
	private native String getUserAgent()/*-{
		return $wnd.navigator.userAgent;
	}-*/;

}
