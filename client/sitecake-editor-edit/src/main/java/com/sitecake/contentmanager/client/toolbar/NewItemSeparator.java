package com.sitecake.contentmanager.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class NewItemSeparator extends Widget {

	private static NewItemSeparatorUiBinder uiBinder = GWT
			.create(NewItemSeparatorUiBinder.class);

	interface NewItemSeparatorUiBinder extends
			UiBinder<Element, NewItemSeparator> {
	}

	public NewItemSeparator() {
		setElement(uiBinder.createAndBindUi(this));
	}

}
