package com.sitecake.contentmanager.client.contextmenu;

import org.adamtacy.client.ui.effects.events.EffectCompletedEvent;
import org.adamtacy.client.ui.effects.events.EffectCompletedHandler;
import org.adamtacy.client.ui.effects.impl.Fade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.TopContainer;
import com.sitecake.contentmanager.client.commons.Rectangle;
import com.sitecake.contentmanager.client.event.DeleteEvent;
import com.sitecake.contentmanager.client.event.EditItemEvent;
import com.sitecake.contentmanager.client.event.SelectEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.fileuploader.FileUploaderItem;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public class ContextMenu extends Widget {

	private static ContextMenuUiBinder uiBinder = GWT
			.create(ContextMenuUiBinder.class);

	interface ContextMenuUiBinder extends UiBinder<Element, ContextMenu> {
	}

	@UiField
	DivElement itemCommands;

	@UiField
	DivElement deleteCommand;

	@UiField
	DivElement resizeCommand;
	
	private ContentItem item;
	
	private Fade showEffect;
	private Fade hideEffect;
	
	private boolean isShown;
	
	private EventBus eventBus;
	
	public ContentItem getItem() {
		return item;
	}

	@Inject
	public ContextMenu(EventBus eventBus, TopContainer topContainer) {
		this.eventBus = eventBus;
		
		final Element element = uiBinder.createAndBindUi(this);

		showEffect = new Fade();
		showEffect.setStartOpacity(0);
		showEffect.setEndOpacity(100);
		showEffect.setDuration(0.2);

		hideEffect = new Fade();
		hideEffect.setStartOpacity(100);
		hideEffect.setEndOpacity(0);
		hideEffect.setDuration(0.2);
		hideEffect.addEffectCompletedHandler(new EffectCompletedHandler() {
			public void onEffectCompleted(EffectCompletedEvent event) {
				element.getStyle().setDisplay(Style.Display.NONE);
				element.getStyle().setWidth(0, Style.Unit.PX);
				element.getStyle().setHeight(0, Style.Unit.PX);				
			}
		});
		
		element.getStyle().setDisplay(Style.Display.NONE);
		
		setElement(element);
		isShown = false;
		item = null;
		final ContextMenu self = this;
		
		this.addDomHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				hide(false);
			}
		}, MouseOutEvent.getType());
		
		this.addDomHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				if ( self.item != null ) {
					event.stopPropagation();
					item.fireEvent(event);
				}
			}
		}, MouseDownEvent.getType());

		this.addDomHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if ( self.item != null ) {
					item.fireEvent(event);
				}
			}
		}, MouseMoveEvent.getType());

		this.addDomHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				if ( self.item != null ) {
					item.fireEvent(event);
				}
			}
		}, MouseUpEvent.getType());
		
		this.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String editMode = null;
				if ( event.getNativeEvent().getEventTarget().cast() == deleteCommand ) {
					self.eventBus.fireEvent(new DeleteEvent(new ContentItem[] {self.item} ));
				} else if ( event.getNativeEvent().getEventTarget().cast() == resizeCommand ) {
					self.eventBus.fireEvent( new EditItemEvent( self.item, "RESIZE" ) );
				} else if ( !event.getNativeEvent().getCtrlKey() && !event.getNativeEvent().getMetaKey() ) { 
					self.eventBus.fireEvent(new EditItemEvent(self.item, editMode));
				} else if ( event.getNativeEvent().getCtrlKey() || event.getNativeEvent().getMetaKey() ) {
					self.eventBus.fireEvent(new SelectEvent(self.item));
				}
			}
		}, ClickEvent.getType());
		
		showEffect.setEffectElement(element);
		hideEffect.setEffectElement(element);
		
		topContainer.add(this);
	}

	public void hide(boolean forced) {
		if ( !isShown ) 
			return;
		
		isShown = false;
		item = null;
		showEffect.cancel();
		
		if ( forced ) {
			getElement().getStyle().setDisplay(Style.Display.NONE);
			getElement().getStyle().setWidth(0, Style.Unit.PX);
			getElement().getStyle().setHeight(0, Style.Unit.PX);			
		} else {
			getElement().getStyle().setOpacity(100);
			hideEffect.play();
		}
	}
	
	public void show(ContentItem item) {
		if ( item != this.item ) {
			if ( this.item != null ) {
				hide(true);
				this.item = item;
				show(true);
			} else {
				this.item = item;
				show(false);
			}
		} else if ( this.item != null && !isShown) {
			show(false);
		}
	}
	
	private void show(boolean forced) {
		if ( item == null ) return;
		
		hideEffect.cancel();
		
		setTargetStyle(item);
		Style contextMenuStyle = getElement().getStyle();

		if (item instanceof FileUploaderItem) {
			contextMenuStyle.clearZIndex();
		} else {
			contextMenuStyle.setZIndex(2000000000);
		}
		
		if ( forced ) {
			contextMenuStyle.setOpacity(100);
			contextMenuStyle.setDisplay(Style.Display.BLOCK);
		} else {
			contextMenuStyle.setOpacity(0);
			contextMenuStyle.setDisplay(Style.Display.BLOCK);
			showEffect.play();
		}
		isShown = true;
	}
	
	private void setTargetStyle(ContentItem targetItem) {
		
		Rectangle contextRect = targetItem.getContenxtRect();

		double targetHeight = contextRect.getHeight();
		
		if ( targetHeight < EditorClientBundle.INSTANCE.deleteButton().getHeight() + 2*3 ) {
			targetHeight = EditorClientBundle.INSTANCE.deleteButton().getHeight() + 2*3;
		}
		
		Element contextElement = getElement();
		Style targetStyle = contextElement.getStyle();
		
		targetStyle.setTop(contextRect.getStart().getY(), Style.Unit.PX);
		targetStyle.setLeft(contextRect.getStart().getX(), Style.Unit.PX);
		targetStyle.setWidth(contextRect.getWidth(), Style.Unit.PX);
		targetStyle.setHeight(targetHeight, Style.Unit.PX);
		
		resizeCommand.getStyle().setDisplay(Display.NONE);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				show(true);
			}
		});		
	}
	
	
}
