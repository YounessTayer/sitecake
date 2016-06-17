package com.sitecake.contentmanager.client.dnd;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.sitecake.contentmanager.client.dom.DragLeaveEvent;

public class DnDUploadDragControllerImplMozilla extends DnDUploadDragController {
	
	@Override
	protected void onNativeDragLeave(final DragLeaveEvent event) {
		// As FF triggers a dragleave event immediately before a drop event,
		// this postpone is necessary to avoid drag canceling in case of a regular drop
		event.preventDefault();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				if ( dropOverlay.equals(event.getEventTarget()) && dragging ) {
					dragging = false;
					dragStartOcurred = false;
					dropOverlay.getStyle().setZIndex(DROP_OVERLAY_ZINDEX_LO);
					
					dragCancel();		
				}		
			}
		});
	}
	

}
