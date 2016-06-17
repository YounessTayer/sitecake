package com.sitecake.contentmanager.client.item.slideshow;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class SlideshowEditorDragController extends PickupDragController {

	private List<Widget> draggables;
	
	public SlideshowEditorDragController(AbsolutePanel boundryPanel) {
		
		super(boundryPanel, false);
		
		setBehaviorBoundaryPanelDrop(false);
		setBehaviorMultipleSelection(true);
		setBehaviorDragProxy(true);
		setBehaviorDragStartSensitivity(1);
		setBehaviorScrollIntoView(true);
		
		draggables = new ArrayList<Widget>();
	}

	@Override
	public void makeDraggable(Widget draggable, Widget dragHandle) {
		super.makeDraggable(draggable, dragHandle);
		draggables.add(draggable);
	}

	@Override
	public void makeDraggable(Widget draggable) {
		super.makeDraggable(draggable);
	}

	@Override
	public void makeNotDraggable(Widget draggable) {
		super.makeNotDraggable(draggable);
		draggables.remove(draggable);
	}

	public void clear() {
		clearSelection();
		for ( Widget draggable : draggables ) {
			super.makeNotDraggable(draggable);
		}
		draggables.clear();
		resetCache();
	}
	
	@Override
	public void dragMove() {
		super.dragMove();
		if ( movablePanel != null ) {
			DOM.scrollIntoView(movablePanel.getElement());
		}
	}	
}
