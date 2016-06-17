package com.sitecake.contentmanager.client.dnd;

import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sitecake.contentmanager.client.dom.DragLeaveEvent;
import com.sitecake.contentmanager.client.dom.DragOverEvent;
import com.sitecake.contentmanager.client.dom.FileInput;

public class DnDUploadDragControllerImplSafari extends DnDUploadDragController {
	
	@Override
	protected void createDropOverlay() {
		dropOverlay = DOM.createElement("input");
		dropOverlay.setAttribute("type", "file");
		dropOverlay.setAttribute("size", "500");
		dropOverlay.setAttribute("multiple", "true");
		dropOverlay.setAttribute("style", "position:fixed;top:0;left:0;right:0;bottom:0;opacity:0;font-size:2000px;");
		dropOverlay.getStyle().setZIndex(DROP_OVERLAY_ZINDEX_LO);
		RootPanel.getBodyElement().appendChild(dropOverlay);
	}
	
	@Override
	protected void onNativeDragOver(DragOverEvent event) {
		//event.preventDefault();
		int mouseX = event.getClientX() + Window.getScrollLeft();
		int mouseY = event.getClientY() + Window.getScrollTop();
		if ( dragging && (lastDragOverX != mouseX || lastDragOverY != mouseY) ) {
			lastDragOverX = mouseX;
			lastDragOverY = mouseY;

			if ( context != null ) {
				context.mouseX = mouseX;
				context.mouseY = mouseY;
				dragMove();
			}
		}
	}
	
	@Override
	protected void onNativeDragLeave(final DragLeaveEvent event) {
		if ( dropOverlay.equals(event.getEventTarget()) && dragging ) {
			dragging = false;
			dragStartOcurred = false;
			dropOverlay.getStyle().setZIndex(DROP_OVERLAY_ZINDEX_LO);
			
			dragCancel();		
		}		
	}
	
	protected void onNativeInputChange(Event event) {
		if ( dragging ) {
			dragging = false;
			dragStartOcurred = false;
			dropOverlay.getStyle().setZIndex(DROP_OVERLAY_ZINDEX_LO);
		    
			dragItem.setFileList(FileInput.valueOf(dropOverlay).files());
			
			// Does the DragController allow the drop?
		    try {
		      previewDragEnd();
		    } catch (VetoDragException ex) {
		      context.vetoException = ex;
		    }
	
		    dragEnd();		
		}		
		
		// recreate dropOverlay input tag in order to be
		// ready for the next 'onchange' event
		restoreDropOverlay();
		
		event.preventDefault();
	}

	private void restoreDropOverlay() {
		if ( dropOverlay != null ) {
			dropOverlay.removeFromParent();
		}
		createDropOverlay();
		setChangeHandler();
	}
	
	private native void setChangeHandler()/*-{
		var self = this;
		var input = self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragController::dropOverlay;
		input.addEventListener('change', function(event) {
			console.log('drop on input');
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragControllerImplSafari::onNativeInputChange(Lcom/google/gwt/user/client/Event;)(event);
		}, false);
	}-*/;
	
	@Override
	protected native void setNativeDnDEventHandlers()/*-{
		var self = this;
		$wnd.addEventListener('dragenter', function(event) {
			//console.log('dragenter', event.target);
			event.dataTransfer.effectAllowed = 'move';
			event.dataTransfer.dropEffect = 'move';
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragController::onNativeDragEnter(Lcom/sitecake/contentmanager/client/dom/DragEnterEvent;)(event);
		}, false);
		
		$wnd.addEventListener('dragover', function(event) {
			//event.dataTransfer.effectAllowed = 'move';
			//event.dataTransfer.dropEffect = 'move';
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragControllerImplSafari::onNativeDragOver(Lcom/sitecake/contentmanager/client/dom/DragOverEvent;)(event);
		}, false);
		
		$wnd.addEventListener('dragleave', function(event) {
			//console.log('dragleave', event, event.target);
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragControllerImplSafari::onNativeDragLeave(Lcom/sitecake/contentmanager/client/dom/DragLeaveEvent;)(event);
		}, false);
				
		var input = self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragController::dropOverlay;
		input.addEventListener('change', function(event) {
			//console.log('drop on input');
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragControllerImplSafari::onNativeInputChange(Lcom/google/gwt/user/client/Event;)(event);
		}, false);
				
	}-*/;

}
