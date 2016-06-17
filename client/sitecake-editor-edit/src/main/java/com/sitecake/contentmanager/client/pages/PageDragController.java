package com.sitecake.contentmanager.client.pages;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageDragController extends PickupDragController {

	public PageDragController(final Panel navPages) {
		super(RootPanel.get(), false);
		setBehaviorBoundaryPanelDrop(false);
		setBehaviorMultipleSelection(false);
		setBehaviorDragProxy(false);
		setBehaviorDragStartSensitivity(1);
		setBehaviorScrollIntoView(true);
		
		addDragHandler(new DragHandler() {	
			@Override
			public void onPreviewDragStart(DragStartEvent event)
					throws VetoDragException {
				if (event.getContext().draggable.getParent() == navPages && 
						navPages.getElement().getChildCount() == 1)
					throw new VetoDragException();
			}
			
			@Override
			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			}
			
			@Override
			public void onDragStart(DragStartEvent event) {
			}
			
			@Override
			public void onDragEnd(DragEndEvent event) {
			}
		});
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
	

}
