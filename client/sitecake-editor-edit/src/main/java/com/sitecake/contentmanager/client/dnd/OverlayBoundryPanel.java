package com.sitecake.contentmanager.client.dnd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class OverlayBoundryPanel extends AbsolutePanel {

	public OverlayBoundryPanel() {
	    super(DOM.createDiv());

	    DOM.setStyleAttribute(getElement(), "position", "absolute");
	    DOM.setStyleAttribute(getElement(), "top", "0");
	    DOM.setStyleAttribute(getElement(), "left", "0");
	    DOM.setStyleAttribute(getElement(), "right", "0");
	    DOM.setStyleAttribute(getElement(), "bottom", "0");
	    GWT.log("" + Document.get().getClientHeight());
	    //DOM.setStyleAttribute(getElement(), "zIndex", "100000000");
	}

}
