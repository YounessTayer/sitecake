package com.sitecake.contentmanager.client.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

public class DnDUploadDragControllerImplDummy extends DnDUploadDragController {

	protected DnDUploadDragControllerImplDummy() {
		// just do minimal set of initialization actions
		// to satisfy the API
		context = new DragContext(this);
		boundaryPanel = RootPanel.get();
		dropControllerCollection = new DropControllerCollection(dropControllerList);
		
		// a body child that is always under the cursor
		// allowing file DnD events to be triggered correctly
		Element underlay = DOM.createDiv();
		underlay.setAttribute("style", "position:absolute;top:0;left:0;right:0;bottom:0;z-index:-1;");
		RootPanel.getBodyElement().insertFirst(underlay);
	}

}
