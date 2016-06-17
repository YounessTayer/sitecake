package com.sitecake.contentmanager.client.select;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class SelectorOverlay extends Widget {

	private static SelectorOverlayUiBinder uiBinder = GWT
			.create(SelectorOverlayUiBinder.class);

	interface SelectorOverlayUiBinder extends
			UiBinder<Element, SelectorOverlay> {
	}

	public SelectorOverlay() {
		setElement(uiBinder.createAndBindUi(this));
	}

	public void show() {
		Style style = getElement().getStyle();
		style.setDisplay(Display.BLOCK);
	}
	
	public void hide() {
		Style style = getElement().getStyle();
		style.setDisplay(Display.NONE);
	}
}
