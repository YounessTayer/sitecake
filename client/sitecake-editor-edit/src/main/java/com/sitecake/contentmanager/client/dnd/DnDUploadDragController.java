package com.sitecake.contentmanager.client.dnd;

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragHandlerCollection;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.TopContainer;
import com.sitecake.contentmanager.client.dom.DragEnterEvent;
import com.sitecake.contentmanager.client.dom.DragLeaveEvent;
import com.sitecake.contentmanager.client.dom.DragOverEvent;
import com.sitecake.contentmanager.client.dom.DropEvent;

public class DnDUploadDragController implements DragController {

	protected static final int DROP_OVERLAY_ZINDEX_HI = 2000010000;
	protected static final int DROP_OVERLAY_ZINDEX_LO = -1;
	
	/**
	  * The drag controller's drag context.
	  */
	protected DragContext context;
	  
	protected boolean cancelDocumentSelections = true;
	
	protected boolean scrollIntoView = true;
	
	/**
	 * The boundary panel to which all drag operations are constrained.
	 */
	protected AbsolutePanel boundaryPanel;
	  
	/**
	 * Collection of registered drag handlers.
	 */
	protected DragHandlerCollection dragHandlers;

	protected ArrayList<DropController> dropControllerList = new ArrayList<DropController>();
	
	protected DropControllerCollection dropControllerCollection;

	/**
	 * The current drag start event, created in {@link #previewDragStart()} and returned a second time
	 * in {@link #dragStart()}.
	 */
	protected DragStartEvent dragStartEvent;
	
	/**
	 * The current drag end event, created in {@link #previewDragEnd()} and returned a second time in
	 * {@link #dragEnd()}.
	 */
	protected DragEndEvent dragEndEvent;
	
	protected Element dropOverlay;
	
	protected boolean dragStartOcurred;
	protected boolean dragging;
	
	protected int lastDragOverX;
	protected int lastDragOverY;
	
	protected final static int CACHE_TIME_MILLIS = 100;
	protected long lastResetCacheTimeMillis;
	
	protected DnDUploadDragWidget dragItem;
	
	private TopContainer topContainer = GinInjector.instance.getTopContainer();
	
	@Override
	public boolean getBehaviorCancelDocumentSelections() {
		return cancelDocumentSelections;
	}

	@Override
	public boolean getBehaviorConstrainedToBoundaryPanel() {
		// unsupported behavior, always false
		return false;
	}

	@Override
	public int getBehaviorDragStartSensitivity() {
		// unsupported behavior, always 0
		return 0;
	}

	@Override
	public boolean getBehaviorMultipleSelection() {
		// unsupported behavior, always false
		return false;
	}

	@Override
	public boolean getBehaviorScrollIntoView() {
		return scrollIntoView;
	}
	
	@Override
	public void setBehaviorCancelDocumentSelections(boolean cancelDocumentSelections) {
		this.cancelDocumentSelections = cancelDocumentSelections;
	}

	@Override
	public void setBehaviorConstrainedToBoundaryPanel(boolean constrainedToBoundaryPanel) {
		// unsupported, ignore
	}

	@Override
	public void setBehaviorDragStartSensitivity(int pixels) {
		// unsupported, ignore
	}

	@Override
	public void setBehaviorMultipleSelection(boolean multipleSelectionAllowed) {
		// unsupported, ignore
	}

	@Override
	public void setBehaviorScrollIntoView(boolean scrollIntoView) {
		this.scrollIntoView = scrollIntoView;
	}
	
	
	
	public DnDUploadDragController() {
		super();
		
		boundaryPanel = RootPanel.get();
		dropControllerCollection = new DropControllerCollection(dropControllerList);
		
		// a body child that is always under the cursor
		// allowing file DnD events to be triggered correctly
		createDropOverlay();
		
		dragging = false;
		
		setNativeDnDEventHandlers();
		dragItem = new DnDUploadDragWidget();
	}

	protected void createDropOverlay() {
		dropOverlay = DOM.createDiv();
		dropOverlay.setAttribute("style", "position:fixed;top:0;left:0;right:0;bottom:0;");
		dropOverlay.getStyle().setZIndex(DROP_OVERLAY_ZINDEX_LO);
		RootPanel.getBodyElement().appendChild(dropOverlay);
	}
	
	@Override
	public void addDragHandler(DragHandler handler) {
		if (dragHandlers == null) {
			dragHandlers = new DragHandlerCollection();
		}
		dragHandlers.add(handler);	
	}

	@Override
	public void clearSelection() {
		// unsupported, ignore
	}

	@Override
	public AbsolutePanel getBoundaryPanel() {
		return boundaryPanel;
	}

	@Override
	public void makeDraggable(Widget draggable) {
		// unsupported, ignore
	}

	@Override
	public void makeDraggable(Widget draggable, Widget dragHandle) {
		// unsupported, ignore
	}

	@Override
	public void makeNotDraggable(Widget widget) {
		// unsupported, ignore
	}

	@Override
	public void removeDragHandler(DragHandler handler) {
		if (dragHandlers != null) {
			dragHandlers.remove(handler);
		}
	}

	@Override
	public void resetCache() {
		dropControllerCollection.resetCache(boundaryPanel, context);
	}

	@Override
	public void toggleSelection(Widget draggable) {
		// unsupported, ignore
	}

	/**
	 * Register a new DropController, representing a new drop target, with this drag controller.
	 * 
	 * @see #unregisterDropController(DropController)
	 * 
	 * @param dropController the controller to register
	 */
	public void registerDropController(DropController dropController) {
		dropControllerList.add(dropController);
	}
	
	/**
	 * Unregister a DropController from this drag controller.
	 * 
	 * @see #registerDropController(DropController)
	 * @see #unregisterDropControllers()
	 * 
	 * @param dropController the controller to register
	 */
	public void unregisterDropController(DropController dropController) {
		dropControllerList.remove(dropController);
	}
	
	/**
	 * Unregister all DropControllers from this drag controller.
	 * 
	 * @see #registerDropController(DropController)
	 * @see #unregisterDropController(DropController)
	 */
	public void unregisterDropControllers() {
		dropControllerList.clear();
	}
	  
	@Override
	public void dragStart() {
		//GWT.log("dragStart");
		context = new DragContext(this);
		context.draggable = dragItem;
		context.selectedWidgets.add(dragItem);
		context.mouseX = lastDragOverX;
		context.mouseY = lastDragOverY;
		
		resetCache();
		
		topContainer.add(dragItem);
		
		if (dragHandlers != null) {
			dragHandlers.fireDragStart(dragStartEvent);
			dragStartEvent = null;
		}
		assert dragStartEvent == null;
		
		context.dropController = dropControllerCollection.getIntersectDropController(context.mouseX, context.mouseY);
		if (context.dropController != null) {
			context.dropController.onEnter(context);
		}		
	}
	
	@Override
	public void previewDragStart() throws VetoDragException {
		//GWT.log("previewDragStart");
		assert dragStartEvent == null;
		if (dragHandlers != null) {
			dragStartEvent = new DragStartEvent(context);
			try {
				dragHandlers.firePreviewDragStart(dragStartEvent);
			} catch (VetoDragException ex) {
				dragStartEvent = null;
				throw ex;
			}
		}
	}
	
	@Override
	public void dragMove() {
		//GWT.log("dragMove");
	    long timeMillis = System.currentTimeMillis();
	    if (timeMillis - lastResetCacheTimeMillis >= CACHE_TIME_MILLIS) {
	      lastResetCacheTimeMillis = timeMillis;
	      resetCache();
	    }
	    
	    DOMUtil.fastSetElementPosition(dragItem.getElement(), context.mouseX, context.mouseY);
	    
		DropController newDropController = dropControllerCollection.getIntersectDropController(context.mouseX, context.mouseY);
		if (context.dropController != newDropController) {
			if (context.dropController != null) {
				//GWT.log("dragMove:onLeave");
				context.dropController.onLeave(context);
			}
			context.dropController = newDropController;
			if (context.dropController != null) {
				//GWT.log("dragMove:onEnter");
				context.dropController.onEnter(context);
			}
		}
		
		if (context.dropController != null) {
			//GWT.log("dragMove:onMove");
			context.dropController.onMove(context);
		}
		DOM.scrollIntoView(dragItem.getElement());
	}
	
	@Override
	public void previewDragEnd() throws VetoDragException {
		//GWT.log("previewDragEnd");
		
		try {
			try {
				if ( context.dropController != null ) {
					context.dropController.onPreviewDrop(context);
					context.finalDropController = context.dropController;
				}
			} finally {
				if (dragHandlers != null) {
					dragEndEvent = new DragEndEvent(context);
					dragHandlers.firePreviewDragEnd(dragEndEvent);
				}
			}
		} catch (VetoDragException ex) {
			context.finalDropController = null;
			throw ex;
		}
	}
	
	@Override
	public void dragEnd() {
		//GWT.log("dragEnd");
		if ( context.dropController != null ) {
			if (context.vetoException != null) {
				context.dropController.onLeave(context);
				context.dropController = null;
			} else {
				context.dropController.onDrop(context);
				context.dropController.onLeave(context);
				context.dropController = null;
			}
		}
		
		if (dragHandlers != null) {
			dragHandlers.fireDragEnd(dragEndEvent);
			dragEndEvent = null;
		}
		
		dragItem.removeFromParent();
	}

	protected void dragCancel() {
		//GWT.log("dragCancel");
		if ( context.dropController != null ) {
			context.dropController.onLeave(context);
			context.dropController = null;
		}
		
		if (dragHandlers != null) {
			dragEndEvent = new DragEndEvent(context);
			try {
				dragHandlers.firePreviewDragEnd(dragEndEvent);
			} catch (VetoDragException ex) {
				context.vetoException = ex;
			} finally {
				dragHandlers.fireDragEnd(dragEndEvent);
			}
			dragEndEvent = null;
		}
		
		dragItem.removeFromParent();
	}
	
	protected void onNativeDragEnter(DragEnterEvent event) {
		// is this an inter-browser DnD operation?
		if ( dragStartOcurred ) {
			dragStartOcurred = false;
		} else if ( !dragging ){
			dragging = true;
			dropOverlay.getStyle().setZIndex(DROP_OVERLAY_ZINDEX_HI);

			lastDragOverX = event.getClientX() + Window.getScrollLeft();
			lastDragOverY = event.getClientY() + Window.getScrollTop();
			
			dragStart();
		}
		event.preventDefault();
	}

	protected void onNativeDragOver(DragOverEvent event) {
		event.preventDefault();
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
	
	protected void onNativeDragLeave(final DragLeaveEvent event) {
		if ( dropOverlay.equals(event.getEventTarget()) && dragging ) {
			dragging = false;
			dragStartOcurred = false;
			dropOverlay.getStyle().setZIndex(DROP_OVERLAY_ZINDEX_LO);
			
			dragCancel();		
		}		
	}
	
	protected void onNativeDrop(DropEvent event) {
		event.preventDefault();
		if ( dragging ) {
			dragging = false;
			dragStartOcurred = false;
			dropOverlay.getStyle().setZIndex(DROP_OVERLAY_ZINDEX_LO);
		    
			dragItem.setDataTransfer(event.dataTransfer());
			
			// Does the DragController allow the drop?
		    try {
		      previewDragEnd();
		    } catch (VetoDragException ex) {
		      context.vetoException = ex;
		    }
	
		    dragEnd();		
		}		
	}
	
	protected native void setNativeDnDEventHandlers()/*-{
		var self = this;
		$wnd.addEventListener('dragstart', function(event) {
			console.log('dragstart', event.target);
			//event.dataTransfer.effectAllowed = 'move';
			//event.dataTransfer.dropEffect = 'move';
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragController::dragStartOcurred = true;
		}, false);
		$wnd.addEventListener('dragenter', function(event) {
			//console.log('dragenter', event.target, event.dataTransfer.files);
			event.dataTransfer.effectAllowed = 'move';
			event.dataTransfer.dropEffect = 'move';
			
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragController::onNativeDragEnter(Lcom/sitecake/contentmanager/client/dom/DragEnterEvent;)(event);
		}, false);
		$wnd.addEventListener('dragover', function(event) {
			event.dataTransfer.effectAllowed = 'move';
			event.dataTransfer.dropEffect = 'move';
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragController::onNativeDragOver(Lcom/sitecake/contentmanager/client/dom/DragOverEvent;)(event);
		}, false);
		$wnd.addEventListener('dragleave', function(event) {
			//console.log('dragleave', event, event.target);
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragController::onNativeDragLeave(Lcom/sitecake/contentmanager/client/dom/DragLeaveEvent;)(event);
		}, false);
		$wnd.addEventListener('drop', function(event) {
			//console.log('drop', event.target, event.dataTransfer.files);
			//console.log('drop', event.dataTransfer, event.dataTransfer.types, event.dataTransfer.getData('text/plain'));
			self.@com.sitecake.contentmanager.client.dnd.DnDUploadDragController::onNativeDrop(Lcom/sitecake/contentmanager/client/dom/DropEvent;)(event);
		}, false);
	}-*/;
}
