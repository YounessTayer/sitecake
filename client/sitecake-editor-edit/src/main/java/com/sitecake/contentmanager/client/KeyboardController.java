package com.sitecake.contentmanager.client;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.sitecake.contentmanager.client.event.CancelEvent;
import com.sitecake.contentmanager.client.event.DeleteEvent;
import com.sitecake.contentmanager.client.event.EndEditingEvent;
import com.sitecake.contentmanager.client.event.EndEditingHandler;
import com.sitecake.contentmanager.client.event.MoveEvent;
import com.sitecake.contentmanager.client.event.MoveEvent.Direction;
import com.sitecake.contentmanager.client.event.RedoEvent;
import com.sitecake.contentmanager.client.event.SelectEvent;
import com.sitecake.contentmanager.client.event.StartEditingEvent;
import com.sitecake.contentmanager.client.event.StartEditingHandler;
import com.sitecake.contentmanager.client.event.StartPageManagerEvent;
import com.sitecake.contentmanager.client.event.StartPageManagerHandler;
import com.sitecake.contentmanager.client.event.StopPageManagerEvent;
import com.sitecake.contentmanager.client.event.StopPageManagerHandler;
import com.sitecake.contentmanager.client.event.UndoEvent;

public class KeyboardController implements CloseHandler<Window> {

	private static final int KEY_Z = 90;
	private static final int KEY_Y = 89;
	private static final int KEY_P = 80;
	private static final int KEY_ESC = 27;
	private static final int KEY_DEL = 46;
	private static final int KEY_BACKSPACE = 8;	
	private static final int KEY_A = 65;
	private static final int KEY_ARROW_LEFT = 37;
	private static final int KEY_ARROW_RIGHT = 39;
	private static final int KEY_ARROW_UP = 38;
	private static final int KEY_ARROW_DOWN = 40;
	
	private EventBus eventBus;
	
	private boolean editMode = false;
	
	private boolean pageManagerMode = false;
	
	@Inject
	public KeyboardController(EventBus eventBus) {
		this.eventBus = eventBus;
		Window.addCloseHandler(this);
		init();
		
		eventBus.addHandler(StartEditingEvent.getType(), new StartEditingHandler() {
			@Override
			public void onStartEditing(StartEditingEvent event) {
				editMode = true;
			}
		});
		
		eventBus.addHandler(EndEditingEvent.getType(), new EndEditingHandler() {
			@Override
			public void onEndEditing(EndEditingEvent event) {
				editMode = false;
			}
		});
		
		eventBus.addHandler(StartPageManagerEvent.getType(), new StartPageManagerHandler() {
			@Override
			public void onStartPageManager(StartPageManagerEvent event) {
				pageManagerMode = true;
			}
		});

		eventBus.addHandler(StopPageManagerEvent.getType(), new StopPageManagerHandler() {
			@Override
			public void onStopPageManager(StopPageManagerEvent event) {
				pageManagerMode = false;
			}
		});
		
	}

	@Override
	public void onClose(CloseEvent<Window> event) {
		cleanup();
	}

	private native void init()/*-{
		var self = this;
		$doc.onkeydown = function(evt) {
			var ret = self.@com.sitecake.contentmanager.client.KeyboardController::onKeyDown(Lcom/google/gwt/user/client/Event;)(evt || $wnd.event);
			return ret;
		}
		$doc.onkeypress = function(evt) {
			var ret = self.@com.sitecake.contentmanager.client.KeyboardController::onKeyPress(Lcom/google/gwt/user/client/Event;)(evt || $wnd.event);
			return ret;
		}
		$doc.onkeyup = function(evt) {
			var ret = self.@com.sitecake.contentmanager.client.KeyboardController::onKeyUp(Lcom/google/gwt/user/client/Event;)(evt || $wnd.event);
			return ret;
		}
	}-*/;
	
	private native void cleanup()/*-{
		$doc.onkeydown = null;
		$doc.onkeypress = null;
		$doc.onkeyup = null;		
	}-*/;
	
	private Boolean onKeyDown(Event event) {
		int keyCode = DOM.eventGetKeyCode(event);
		boolean ctrlKey = DOM.eventGetCtrlKey(event) || DOM.eventGetMetaKey(event);
		//GWT.log("onKeyDown:" + keyCode);
		boolean sink = false;
		
		switch ( keyCode ) {
			case KEY_ESC:
				eventBus.fireEvent(new CancelEvent());
				break;
			case KEY_Y:
				if ( ctrlKey && !editMode ) {
					eventBus.fireEvent(new RedoEvent());
				}
				break;
			case KEY_Z:
				if ( ctrlKey && !editMode ) {
					eventBus.fireEvent(new UndoEvent());
				}
				break;
			case KEY_A:
				if ( ctrlKey && !editMode ) {
					eventBus.fireEvent(new SelectEvent(null));
					sink = true;
				}
				break;
			case KEY_DEL:
			case KEY_BACKSPACE:
				if ( !editMode && !pageManagerMode) {
					eventBus.fireEvent(new DeleteEvent(null));
					sink = true;
				}
				break;
			case KEY_ARROW_LEFT:
				if ( !editMode && !pageManagerMode) {
					eventBus.fireEvent(new MoveEvent(Direction.LEFT));
					sink = true;
				}
				break;
			case KEY_ARROW_RIGHT:
				if ( !editMode && !pageManagerMode) {
					eventBus.fireEvent(new MoveEvent(Direction.RIGHT));
					sink = true;
				}
				break;
			case KEY_ARROW_UP:
				if ( !editMode && !pageManagerMode) {
					eventBus.fireEvent(new MoveEvent(Direction.UP));
					sink = true;
				}
				break;
			case KEY_ARROW_DOWN:
				if ( !editMode && !pageManagerMode) {
					eventBus.fireEvent(new MoveEvent(Direction.DOWN));
					sink = true;
				}
				break;
			/*
			case KEY_P:
				if ( !editMode && !pageManagerMode) {
					eventBus.fireEvent(new PageManagerEvent(true));
					sink = true;
				}
			*/				
		}
		
		if ( sink ) {
			event.preventDefault();
			event.stopPropagation();
			return false;
		}
		return true;
	}
	
	private Boolean onKeyPress(Event event) {
		int keyCode = DOM.eventGetKeyCode(event);
		//boolean ctrlKey = DOM.eventGetCtrlKey(event) || DOM.eventGetMetaKey(event);
		//GWT.log("onKeyDown:" + keyCode);
		boolean sink = false;
		
		switch ( keyCode ) {
		
			case KEY_BACKSPACE:
				if ( !editMode && !pageManagerMode) {
					sink = true;
				}
				break;		
		}
		
		if ( sink ) {
			event.preventDefault();
			event.stopPropagation();
			return false;
		}
		
		return true;
	}
	
	private Boolean onKeyUp(Event event) {
		int keyCode = DOM.eventGetKeyCode(event);
		//boolean ctrlKey = DOM.eventGetCtrlKey(event) || DOM.eventGetMetaKey(event);
		//GWT.log("onKeyDown:" + keyCode);
		boolean sink = false;
		
		switch ( keyCode ) {
		
			case KEY_BACKSPACE:
				if ( !editMode && !pageManagerMode) {
					sink = true;
				}
				break;		
		}
		
		if ( sink ) {
			event.preventDefault();
			event.stopPropagation();
			return false;
		}
		
		return true;
	}	
}
