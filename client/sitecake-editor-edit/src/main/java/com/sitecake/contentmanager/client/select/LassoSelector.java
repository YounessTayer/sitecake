package com.sitecake.contentmanager.client.select;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class LassoSelector extends Widget {

	private static LassoSelectorUiBinder uiBinder = GWT.create(LassoSelectorUiBinder.class);

	interface LassoSelectorUiBinder extends UiBinder<Element, LassoSelector> {
	}

	public LassoSelector() {
		final Element element = uiBinder.createAndBindUi(this);
		element.getStyle().setDisplay(Display.NONE);
		setElement(element);
	}
	
	public void setSize(int top, int left, int width, int height) {
		Style style = getElement().getStyle();
		style.setTop(top, Unit.PX);
		style.setLeft(left, Unit.PX);
		style.setWidth(width, Unit.PX);
		style.setHeight(height, Unit.PX);
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
