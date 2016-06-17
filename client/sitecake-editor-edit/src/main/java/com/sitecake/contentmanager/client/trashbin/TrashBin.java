package com.sitecake.contentmanager.client.trashbin;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.event.DeleteEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.properties.PropertyManager;
import com.sitecake.contentmanager.client.properties.PropertyScope;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public class TrashBin extends Widget implements DropController {

	private static final String POSITION_X = "TrashBin.left";
	private static final String POSITION_Y = "TrashBin.top";
	
	private static TrashBinUiBinder uiBinder = GWT
			.create(TrashBinUiBinder.class);

	interface TrashBinUiBinder extends UiBinder<Element, TrashBin> {
	}

	private class MouseHandler implements MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler, MouseMoveHandler {
	
		public void onMouseDown(MouseDownEvent event) {
			dragStart(event);
		}
		
		public void onMouseMove(MouseMoveEvent event) {
			dragMove(event);
		}
		
		public void onMouseOut(MouseOutEvent event) {
		}
		
		public void onMouseOver(MouseOverEvent event) {
		}
		
		public void onMouseUp(MouseUpEvent event) {
			dragEnd(event);
		}
	}	
	
	private boolean dragging = false;
	
	private int mouseOffsetX;
	
	private int mouseOffsetY;
	
	private EventBus eventBus;
	
	private HandlerRegistration windowResizeHandlerRegistration;
	
	private PropertyManager propertyManager;
	
	public TrashBin(EventBus eventBus, PropertyManager propertyManager) {
		this.eventBus = eventBus;
		this.propertyManager = propertyManager;
		
		setElement(uiBinder.createAndBindUi(this));
		
		MouseHandler mouseHandler = new MouseHandler();
		addDomHandler(mouseHandler, MouseDownEvent.getType());
		addDomHandler(mouseHandler, MouseUpEvent.getType());
		addDomHandler(mouseHandler, MouseMoveEvent.getType());
		addDomHandler(mouseHandler, MouseOverEvent.getType());
		addDomHandler(mouseHandler, MouseOutEvent.getType());	
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		
		Integer left = propertyManager.getPropertyFloat(POSITION_X, (float)Integer.MAX_VALUE, PropertyScope.APPLICATION).intValue();
		Integer top = propertyManager.getPropertyFloat(POSITION_Y, (float)Integer.MAX_VALUE, PropertyScope.APPLICATION).intValue();
		
		setPosition(left, top, true, true);
		
		windowResizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				onWindowResize();
			}
		});		
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		windowResizeHandlerRegistration.removeHandler();
	}
	
	@Override
	public Widget getDropTarget() {
		return this;
	}


	@Override
	public void onDrop(DragContext context) {
		// do nothing
	}


	@Override
	public void onEnter(DragContext context) {
		addStyleName(EditorClientBundle.INSTANCE.css().trashBinEngaged());
	}


	@Override
	public void onLeave(DragContext context) {
		removeStyleName(EditorClientBundle.INSTANCE.css().trashBinEngaged());
	}

	@Override
	public void onMove(DragContext context) {
		// do nothing
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		final List<Widget> items = new ArrayList<Widget>(context.selectedWidgets);
 
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				List<ContentItem> forDeletion = new ArrayList<ContentItem>();
				for ( Widget widget : items ) {
					if ( widget instanceof ContentItem ) {
						forDeletion.add((ContentItem)widget);
					}
				}
				eventBus.fireEvent(new DeleteEvent(forDeletion.toArray(new ContentItem[0])));
			}
		});		
		
		// finally, throw Veto because we don't want actuall drop 
		throw new VetoDragException();
	}

	private void dragStart(MouseDownEvent event) {
		dragging = true;
		DOM.setCapture(getElement());
		addStyleName(EditorClientBundle.INSTANCE.css().trashBinDragged());
		mouseOffsetX = event.getRelativeX(getElement());
		mouseOffsetY = event.getRelativeY(getElement());
		event.stopPropagation();
		event.preventDefault();
	}
	
	private void dragMove(MouseMoveEvent event) {
		if ( !dragging ) return;
		
		int posX = event.getClientX() - mouseOffsetX;
		int posY = event.getClientY() - mouseOffsetY;
		
		setPosition(posX, posY, false, false);
	}

	private void setPosition(int x, int y, boolean constraint, boolean save) {
		int windowWidth = Window.getClientWidth();
		int windowHeight = Window.getClientHeight();
		int elementWidth = getElement().getOffsetWidth();
		int elementHeight = getElement().getOffsetHeight();
		int maxWidth = windowWidth - elementWidth;
		int maxHeight = windowHeight - elementHeight; 
		
		if ( constraint ) {
			x = ( x < 0 ) ? 0 : ( ( x > maxWidth ) ? maxWidth : x );
			y = ( y < 0 ) ? 0 : ( ( y > maxHeight ) ? maxHeight : y );
		}
		
		Style style = getElement().getStyle();
		style.setLeft(x, Unit.PX);
		style.setTop(y, Unit.PX);
		
		if ( save ) {
			propertyManager.setProperty(POSITION_X, x, PropertyScope.APPLICATION);
			propertyManager.setProperty(POSITION_Y, y, PropertyScope.APPLICATION);
		}
	}
	
	private void dragEnd(MouseUpEvent event) {
		dragging = false;
		DOM.releaseCapture(getElement());
		removeStyleName(EditorClientBundle.INSTANCE.css().trashBinDragged());
		
		int posX = event.getClientX() - mouseOffsetX;
		int posY = event.getClientY() - mouseOffsetY;
		
		setPosition(posX, posY, true, true);
	}
	
	private void onWindowResize() {
		setPosition(getElement().getOffsetLeft(), getElement().getOffsetTop(), true, true);
	}
}
