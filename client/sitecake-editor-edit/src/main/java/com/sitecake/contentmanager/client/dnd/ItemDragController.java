package com.sitecake.contentmanager.client.dnd;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemDragController extends PickupDragController {

	public ItemDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
		
		setBehaviorBoundaryPanelDrop(false);
		setBehaviorMultipleSelection(true);
		setBehaviorDragProxy(true);
		setBehaviorDragStartSensitivity(3);
		setBehaviorScrollIntoView(false);
	}

	@Override
	public void makeNotDraggable(Widget draggable) {
		try {
			super.makeNotDraggable(draggable);
		} catch (RuntimeException e) {
			// preventing exception in case makeNotDraggable called more than once
			// do nothing
		}
	}

	public void unselect(Widget draggable) {
		assert draggable != null;
		
		if ( context.selectedWidgets.remove(draggable) ) {
			draggable.removeStyleName(DragClientBundle.INSTANCE.css().selected());
		}		
	}
	
	public void select(Widget draggable) {
		assert draggable != null;
		
		if ( !context.selectedWidgets.contains(draggable) ) {
			if ( getBehaviorMultipleSelection() ) {
				context.selectedWidgets.add(draggable);
				draggable.addStyleName(DragClientBundle.INSTANCE.css().selected());
			} else {
				context.selectedWidgets.clear();
				context.selectedWidgets.add(draggable);
			}
		}
	}

	public void hideSelectedWidgetsStyle() {
		for ( Widget draggable : context.selectedWidgets ) {
			draggable.removeStyleName(DragClientBundle.INSTANCE.css().selected());
		}
	}

	public void showSelectedWidgetsStyle() {
		for ( Widget draggable : context.selectedWidgets ) {
			draggable.addStyleName(DragClientBundle.INSTANCE.css().selected());
		}
	}

	@Override
	public void dragMove() {
		super.dragMove();
		if ( movablePanel != null ) {
			DOM.scrollIntoView(movablePanel.getElement());
		}
	}
	
	
}
