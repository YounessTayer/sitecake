package com.sitecake.contentmanager.client.item;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.WidgetEx;
import com.sitecake.contentmanager.client.ContentStyleRegistry;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.commons.Point;
import com.sitecake.contentmanager.client.commons.Rectangle;
import com.sitecake.contentmanager.client.container.ContentContainer;
import com.sitecake.contentmanager.client.contextmenu.CommandWidget;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public abstract class ContentItem extends WidgetEx implements HasClickHandlers, HasAllMouseHandlers,
	HasAllKeyHandlers {

	protected ContentContainer container;

	protected boolean selected = false;
	
	protected boolean edited = false;

	public static final String DEFAULT_STYLE = "";
	
	protected String style = DEFAULT_STYLE;
	
	protected Rectangle contextRect;
	
	private ContentStyleRegistry contentStyleRegistry = GinInjector.instance.getContentStyleRegistry();
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		assert style != null;
		style = style.trim();
		
		if ( !this.style.equals(style) ) {
			if ( "".equals(style) ) {
				getElement().removeClassName(this.style);
			} else if ( "".equals(this.style) ) {
				getElement().addClassName(style);
			} else {
				getElement().replaceClassName(this.style, style);
			}
			this.style = style;
		}
	}

	public abstract String getContentTypeName();
	
	public abstract String getItemSelector();
	
	public ContentContainer getContainer() {
		return container;
	}
	
	public void setContainer(ContentContainer container) {
		this.container = container;
	}

	public ContentItem(Element element) {
		this();
		setElement(element);
		init();
	}

	protected ContentItem() {
		super();
		init();
	}
	
	private void init() {
		contextRect = new Rectangle(new Point(0, 0), new Point(0, 0));
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return addDomHandler(handler, MouseWheelEvent.getType());
	}
	
	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return addDomHandler(handler, KeyUpEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return addDomHandler(handler, KeyPressEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addDomHandler(handler, KeyDownEvent.getType());
	}

	public abstract ContentItem cloneItem();
	
	protected ContentItem cloneItem(ContentItem clone) {
		clone.container = this.container;
		clone.selected = false;
		clone.edited = false;
		clone.style = style;
		return clone;
	}

	/**
	 * Compares item with the given instance.
	 * 
	 * @param target
	 * @return true if two item objects are equal concerning their properties
	 */
	public boolean compare(ContentItem target) {
		
		if ( target == null || target.getElement() == null || getElement() == null )
			return false;
		
		return style.equals(target.style) && getElement().getString().equals(target.getElement().getString());
	}
	
	public void setSelected(boolean select) {

		if ( select && !selected ) {
			selected = true;
			addStyleName(EditorClientBundle.INSTANCE.css().itemSelected());
		} else if ( !select && selected ){
			selected = false;
			removeStyleName(EditorClientBundle.INSTANCE.css().itemSelected());
		}
	}

	public boolean isSelected() {
		return selected;
	}
	
	public boolean isEditable() {
		return true;
	}
	
	public boolean intiateEditUponCreation() {
		return false;
	}
	
	public void startEditing(String mode) {
		if ( edited ) return;
		
		setSelected(false);
		addStyleName(EditorClientBundle.INSTANCE.css().itemEdited());
		edited = true;
	}
	
	/**
	 * 
	 * @param cancel signals whether the current editing should be canceled
	 * @return
	 */
	public boolean stopEditing(boolean cancel) {
		if ( !edited ) return false;
		
		edited = false;
		removeStyleName(EditorClientBundle.INSTANCE.css().itemEdited());

		// TODO: should calculate dirty in case, for example, style is changed
		return false;
	}

	public void startDragging() {
	}
	
	public void stopDragging() {
	}
	
	public void onDeletion() {
		
	}
	
	public void onInclusion() {
		adjustContentStyle();
	}
	
	protected void adjustContentStyle() {
		if ( !DEFAULT_STYLE.equals(getStyle()) ) {
			List<String> styles = contentStyleRegistry.get(container.getName(), getItemSelector());
			if ( styles == null || !styles.contains(getStyle()) ) {
				setStyle(DEFAULT_STYLE);
			}
		}
	}
	
	public abstract String getHtml();
	
	public Rectangle getContenxtRect() {
		contextRect.getStart().setX(getElement().getAbsoluteLeft() - 1);
		contextRect.getStart().setY(getElement().getAbsoluteTop() - 1);
		contextRect.setWidth(getElement().getOffsetWidth() + 2);
		contextRect.setHeight(getElement().getOffsetHeight() + 2);
		
		return contextRect;
	}
	
	public List<CommandWidget> getContextCommands() {
		return null;
	}
	
	public boolean isResizable() {
		return false;
	}
	
	@Override
	protected void replaceElement(Element elem) {
		ContentContainer container = this.container;
		
		// TODO: remove the need for exclude/include
		if ( container != null ) {
			container.exclude(this, false);
		}
		
		super.replaceElement(elem);
		
		if ( container != null ) {
			container.include(this, false);
		}
	}
	
}
