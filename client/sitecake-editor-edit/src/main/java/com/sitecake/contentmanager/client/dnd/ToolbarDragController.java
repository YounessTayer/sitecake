package com.sitecake.contentmanager.client.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.toolbar.NewItem;

public class ToolbarDragController extends PickupDragController {

	public ToolbarDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
		
		setBehaviorBoundaryPanelDrop(false);
		setBehaviorMultipleSelection(false);
		setBehaviorDragProxy(true);
		setBehaviorDragStartSensitivity(1);
		setBehaviorScrollIntoView(true);
		
		addDragHandler(new DragHandler() {
			
			@Override
			public void onPreviewDragStart(DragStartEvent event)
					throws VetoDragException {
			}
			
			@Override
			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			}
			
			@Override
			public void onDragStart(DragStartEvent event) {
				event.getContext().draggable.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragging());
			}
			
			@Override
			public void onDragEnd(DragEndEvent event) {
				event.getContext().draggable.removeStyleName(EditorClientBundle.INSTANCE.css().newItemDragging());
				event.getContext().draggable.removeStyleName(EditorClientBundle.INSTANCE.css().newItemActive());
				if ( event.getContext().draggable instanceof NewItem ) {
					((NewItem)event.getContext().draggable).markAsInactive();
				}
			}
		});
	}
	
	protected Widget newDragProxy(DragContext context) {
		Widget dragProxy;
		
		if ( context.draggable instanceof NewItem ) {
			dragProxy = ((NewItem)context.draggable).getDragProxy();
		} else {
			dragProxy = super.newDragProxy(context);
		}
		
		return dragProxy;
	}
	
	@Override
	public void dragMove() {
		super.dragMove();
		if ( movablePanel != null ) {
			DOM.scrollIntoView(movablePanel.getElement());
		}
	}	
}
