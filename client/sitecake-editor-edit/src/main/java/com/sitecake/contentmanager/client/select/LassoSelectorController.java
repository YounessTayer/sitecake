package com.sitecake.contentmanager.client.select;

import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.sitecake.contentmanager.client.TopContainer;
import com.sitecake.contentmanager.client.select.SelectContext.Phase;

public class LassoSelectorController {
	
	private SelectorHandler selectHandler;
	
	private SelectContext context;
	
	private boolean isStarted;
	
	private LassoSelector selector;
	
	private SelectorOverlay selectorOverlay;
	
	private String originalCursor;
	
	private boolean enabled = true;
	
	@Inject
	public LassoSelectorController(TopContainer topContainer) {
		
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				int type = event.getTypeInt();
				switch ( type ) {
				case Event.ONMOUSEDOWN:
					onMouseDown(event.getNativeEvent());
					break;
				case Event.ONMOUSEMOVE:
					onMouseMove(event.getNativeEvent());
					break;
				case Event.ONMOUSEUP:
					onMouseUp(event.getNativeEvent());
					break;
				}
			}
		});

		selectorOverlay = new SelectorOverlay();
		selectorOverlay.hide();
		topContainer.add(selectorOverlay);

		selector = new LassoSelector();
		topContainer.add(selector);
		
		context = new SelectContext();
		isStarted = false;
	}

	public void setSelectHandler(SelectorHandler selectHandler) {
		this.selectHandler = selectHandler;
	}

	public SelectorHandler getSelectHandler() {
		return selectHandler;
	}

	public void enable(boolean enable) {
		this.enabled = enable;
	}
	
	private void onMouseMove(NativeEvent event) {
		if ( !isStarted ) return;
		
		context.setPhase(Phase.DRAG);
		context.setCurrentX(event.getClientX() + Window.getScrollLeft());
		context.setCurrentY(event.getClientY() + Window.getScrollTop());
		drag();
		if ( selectHandler != null ) {
			selectHandler.onSelectDrag(context);
		}
	}

	private void onMouseUp(NativeEvent event) {
		if ( !isStarted ) return;
		
		context.setPhase(Phase.END);
		context.setEndX(event.getClientX() + Window.getScrollLeft());
		context.setEndY(event.getClientY() + Window.getScrollTop());
		if ( selectHandler != null ) {
			selectHandler.onSelectEnd(context);
		}		
		end();
		
	}

	private void onMouseDown(NativeEvent event) {
		if ( !enabled ) return;
		
		EventTarget target = event.getEventTarget();
		
		if ( target == null || Node.as(target).getParentElement() == null ) return;
		
		context.setStartTargetElement(target);
		context.setPhase(Phase.START);
		context.setStartX(event.getClientX() + Window.getScrollLeft());
		context.setStartY(event.getClientY() + Window.getScrollTop());
		
		if ( selectHandler != null ) {
			try {
				selectHandler.onSelectStart(context);
				
				start();
			} catch (VetoSelectException e) {
			}
		}
	}
	
	private void start() {
		if ( isStarted ) return;
		
		DOM.setCapture(RootPanel.get().getElement());
		
		isStarted = true;
		selectorOverlay.show();
		selector.setSize(context.getStartY(), context.getStartX(), 0, 0);
		selector.show();
		originalCursor = DOM.getStyleAttribute(RootPanel.getBodyElement(), "cursor");
		RootPanel.getBodyElement().getStyle().setCursor(Cursor.POINTER);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				DOMUtil.cancelAllDocumentSelections();
			}
		});		
	}
	
	private void drag() {
		calculateSelectorSize();
	}
	
	private void end() {
		if ( !isStarted ) return;
		
		DOM.releaseCapture(RootPanel.get().getElement());
		
		isStarted = false;
		context = new SelectContext();
		selector.hide();
		selectorOverlay.hide();
		if ( originalCursor != null && !"".equals(originalCursor) ) {
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", originalCursor);
		} else {
			RootPanel.getBodyElement().getStyle().setCursor(Cursor.AUTO);
		}
	}
	
	private void calculateSelectorSize() {
		int x0 = context.getStartX();
		int y0 = context.getStartY();
		int x1 = context.getCurrentX();
		int y1 = context.getCurrentY();
		
		int w = Math.abs( x1 - x0 );
		int h = Math.abs( y1 - y0 );
		
		int x = ( x1 >= x0 ) ? x0 : x1;
		int y = ( y1 >= y0 ) ? y0 : y1;

		selector.setSize(y, x, w, h);
	}
}
