package com.sitecake.contentmanager.client.item.slideshow;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class SlideshowImagePanel extends FlowPanel {

	@Override
	protected void insert(Widget child, com.google.gwt.user.client.Element container, int beforeIndex, boolean domInsert) {
		if ( beforeIndex <= this.getWidgetCount() ) {
			super.insert(child, container, beforeIndex, domInsert);
		} else {
			super.add(child, container);
		}
	}
}
