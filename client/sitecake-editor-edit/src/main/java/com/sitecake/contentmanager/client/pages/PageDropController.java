package com.sitecake.contentmanager.client.pages;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public class PageDropController extends FlowPanelDropController {

	public PageDropController(FlowPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	protected Widget newPositioner(DragContext context) {
		HTML positioner = new HTML();
		positioner.addStyleName(EditorClientBundle.INSTANCE.css().pageManagerPositioner());
		return positioner;
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		if (context.draggable == null) return;
		
		PageWidget page = (PageWidget)context.draggable;
		GinInjector.instance.getPageManager().drop(page);
	}

}
