package com.sitecake.contentmanager.client.toolbar;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.resources.Messages;

public class StyleSelector extends Composite {

	private static StyleSelectorUiBinder uiBinder = GWT
			.create(StyleSelectorUiBinder.class);

	interface StyleSelectorUiBinder extends UiBinder<Widget, StyleSelector> {
	}

	interface CssStyle extends CssResource {
		
	}
	
	public interface StyleSelectorHandler {
		void onSelectStart(String currentStyle);
		void onSelectEnd(String currentStyle);
		void onSelectChange(String currentStyle);
	}
	
	private StyleSelectorHandler handler;
	
	private String currentStyle;
	
	private boolean active = false;
	
	private Widget selectedStyleOption;
	
	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	@UiField 
	CssStyle style;
	
	@UiField
	SpanElement buttonLabel;
	
	@UiField
	FlowPanel options;
	
	public void setStyles(List<String> styles, String currentStyle) {
		options.clear();
		
		String defaultStyleLabel = messages.defaultStyle();
		String currentStyleLabel = "".equals(currentStyle) ? defaultStyleLabel : currentStyle;
		options.add(new HTML("<span>" + defaultStyleLabel + "</span>"));
		
		if ( styles != null ) {
			for ( String style : styles ) {
				options.add(new HTML("<span>" + style + "</span>"));
			}
		}
		
		this.currentStyle = currentStyle;
		buttonLabel.setInnerText(currentStyleLabel);
	}
	
	void setHandler(StyleSelectorHandler handler) {
		this.handler = handler;
	}
	
	public StyleSelector() {
		initWidget(uiBinder.createAndBindUi(this));
		setStyles(null, "");
		init();
	}
	
	private void init() {
		addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				StyleSelector.this.onMouseDown(event);
			}
		}, MouseDownEvent.getType());
		
		addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				StyleSelector.this.onMouseUp(event);
			}
		}, MouseUpEvent.getType());
		
		addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				StyleSelector.this.onMouseMove(event);
			}
		}, MouseMoveEvent.getType());		
	}
	
	private String getCurrentStyle() {
		if ( messages.defaultStyle().equals(currentStyle) ) {
			return "";
		} else {
			return currentStyle;
		}
	}
	
	void onMouseDown(MouseDownEvent event) {
		DOM.setCapture(getElement());
		event.preventDefault();
		DOMUtil.cancelAllDocumentSelections();
		addStyleName(EditorClientBundle.INSTANCE.css().styleSelectorActive());
		active = true;
		selectedStyleOption = null;
		if ( handler != null ) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					handler.onSelectStart(getCurrentStyle());
				}
			});				
		}
	}
	
	void onMouseMove(MouseMoveEvent event) {
		if ( !active ) return;
		
		String selectedStyle = null;
		Widget selectedStyleOption = null;
		for ( Widget option : options ) {
			if ( option.getElement().isOrHasChild(Node.as(event.getNativeEvent().getEventTarget())) ) {
				selectedStyle = option.getElement().getInnerText();
				selectedStyleOption = option;
			}
		}
		
		if ( selectedStyleOption != null && this.selectedStyleOption != selectedStyleOption ) {
			for ( Widget option : options ) {
				if ( option != selectedStyleOption ) {
					option.removeStyleName(EditorClientBundle.INSTANCE.css().styleSelectorActiveOption());
				}
			}
			selectedStyleOption.addStyleName(EditorClientBundle.INSTANCE.css().styleSelectorActiveOption());
			this.selectedStyleOption = selectedStyleOption;
		}
		
		if ( selectedStyle == null || selectedStyle.equals(currentStyle) ) return;
		
		currentStyle = selectedStyle;
		buttonLabel.setInnerText(currentStyle);
		if ( handler != null ) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					handler.onSelectChange(getCurrentStyle());
				}
			});
		}
	}

	void onMouseUp(MouseUpEvent event) {

		if ( !active ) return;
		active = false;
		DOM.releaseCapture(getElement());
		removeStyleName(EditorClientBundle.INSTANCE.css().styleSelectorActive());
		
		if ( handler != null ) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					handler.onSelectEnd(getCurrentStyle());
				}
			});
		}		
	}
	
}
