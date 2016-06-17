package com.sitecake.contentmanager.client.toolbar;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.ConfigRegistry;
import com.sitecake.commons.client.config.Globals;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.ContentStyleRegistry;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GlobalConstants;
import com.sitecake.contentmanager.client.event.ContentManagamentEvent;
import com.sitecake.contentmanager.client.event.ContentManagamentEvent.EventType;
import com.sitecake.contentmanager.client.event.ContentManagamentHandler;
import com.sitecake.contentmanager.client.event.EditorHistoryChangeEvent;
import com.sitecake.contentmanager.client.event.EditoryHistoryChangeHandler;
import com.sitecake.contentmanager.client.event.EndEditingEvent;
import com.sitecake.contentmanager.client.event.EndEditingHandler;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;
import com.sitecake.contentmanager.client.event.LinkEditorEvent;
import com.sitecake.contentmanager.client.event.LinkEditorHandler;
import com.sitecake.contentmanager.client.event.LogoutEvent;
import com.sitecake.contentmanager.client.event.PublishEvent;
import com.sitecake.contentmanager.client.event.RedoEvent;
import com.sitecake.contentmanager.client.event.StartEditingEvent;
import com.sitecake.contentmanager.client.event.StartEditingHandler;
import com.sitecake.contentmanager.client.event.UndoEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.properties.PropertyManager;
import com.sitecake.contentmanager.client.properties.PropertyScope;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.resources.LocaleProxy;
import com.sitecake.contentmanager.client.resources.Messages;
import com.sitecake.contentmanager.client.toolbar.StyleSelector.StyleSelectorHandler;

// TODO: extract functionality of draggable fixed widget
public class ContentManagerToolbar extends Composite {
	private static final String POSITION_X = "ContentManagerToolbar.left";
	private static final String POSITION_Y = "ContentManagerToolbar.top";
	
	private static final String TOOLBAR_COMPONENTS = "Toolbar.components";
	private static final String DEFAULT_TOOLBAR_COMPONENTS = "HEADING1,HEADING2,HEADING3,TEXTLIST,TEXT,SEP,IMAGE,SLIDESHOW,SLIDER,VIDEO,SEP,FLASH,MAP,HTML,FILE,CBOX";
	
	private static ContentManagerToolbarUiBinder uiBinder = GWT
			.create(ContentManagerToolbarUiBinder.class);

	interface ContentManagerToolbarUiBinder extends
			UiBinder<Widget, ContentManagerToolbar> {
	}

	interface CssStyle extends CssResource {
		String dragHandle();
		String containerDragged();
	}
	
	@UiField 
	CssStyle style;
	
	@UiField
	PushButton undoButton;

	@UiField
	PushButton redoButton;

	@UiField
	PushButton publishButton;

	@UiField
	PushButton logoutButton;
	
	@UiField
	FlowPanel topContainer;
	
	@UiField
	Panel newItems;
	
	@UiField
	FlowPanel contextContainer;
	
	@UiField
	AnchorElement logoLink;
	
	private StyleSelector styleSelector;
	private LinkEditor linkEditor;
	
	private class MouseHandler implements MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler, MouseMoveHandler {
		
		public void onMouseDown(MouseDownEvent event) {
			dragStart(event);
			event.stopPropagation();
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
	
	private HandlerRegistration windowResizeHandlerRegistration;
	
	private PropertyManager propertyManager;
	private EventBus eventBus;
	private ConfigRegistry configRegistry;
	private ContentStyleRegistry contentStyleRegistry;
	private Messages messages;
	
	private ContentItem editedItem;
	
	@Inject
	public ContentManagerToolbar(EventBus eventBus, ConfigRegistry configRegistry, PropertyManager propertyManager,
			ContentStyleRegistry contentStyleRegistry, GlobalConstants globalConstants,
			LocaleProxy localeProxy) {
		this.propertyManager = propertyManager;
		this.eventBus = eventBus;
		this.configRegistry = configRegistry;
		this.contentStyleRegistry = contentStyleRegistry;
		this.messages = localeProxy.messages();
		
		initWidget(uiBinder.createAndBindUi(this));
		getElement().getStyle().setPosition(Position.FIXED);
		
		publishButton.getUpFace().setHTML(
				"<div class=\"" + EditorClientBundle.INSTANCE.css().toolbarPublishUp() + "\">" + 
				messages.publishButton() + "</div>"
		);
		publishButton.getDownFace().setHTML(
				"<div class=\"" + EditorClientBundle.INSTANCE.css().toolbarPublishDown() + "\">" + 
				messages.publishButton() + "</div>"
		);
		publishButton.getUpDisabledFace().setHTML(
				"<div class=\"" + EditorClientBundle.INSTANCE.css().toolbarPublishDisabled() + "\">" + 
				messages.publishButton() + "</div>"
		);

		logoLink.setTitle(globalConstants.version());
		
		createComponents();
		
		// set the initial state of the publish button
		// based on the draftPublished server-side flag
		publishButton.setEnabled(!Globals.get().getDraftPublished());
		
		linkEditor = new LinkEditor();
		linkEditor.setVisible(false);
		contextContainer.add(linkEditor);
		
		styleSelector = new StyleSelector();
		styleSelector.setVisible(false);
		contextContainer.add(styleSelector);
		styleSelector.setHandler(new StyleSelectorHandler() {
			
			@Override
			public void onSelectStart(String currentStyle) {
			}
			
			@Override
			public void onSelectEnd(String currentStyle) {
			}
			
			@Override
			public void onSelectChange(String currentStyle) {
				if ( editedItem != null ) {
					editedItem.setStyle(currentStyle);
				}
			}
		});
		
		MouseHandler mouseHandler = new MouseHandler();
		addDomHandler(mouseHandler, MouseDownEvent.getType());
		addDomHandler(mouseHandler, MouseUpEvent.getType());
		addDomHandler(mouseHandler, MouseMoveEvent.getType());
		addDomHandler(mouseHandler, MouseOverEvent.getType());
		addDomHandler(mouseHandler, MouseOutEvent.getType());	

		eventBus.addHandler(StartEditingEvent.getType(), new StartEditingHandler() {
			public void onStartEditing(StartEditingEvent event) {
				ContentManagerToolbar.this.onStartEditing(event);
			}
		});
		
		eventBus.addHandler(EndEditingEvent.getType(), new EndEditingHandler() {
			public void onEndEditing(EndEditingEvent event) {
				ContentManagerToolbar.this.onEndEditing(event);
			}
		});
		
		eventBus.addHandler(EditorHistoryChangeEvent.getType(), new EditoryHistoryChangeHandler() {
			@Override
			public void onEditoryHistoryChange(EditorHistoryChangeEvent event) {
				Boolean atStart = event.getAtStart();
				Boolean atEnd = event.getAtEnd();
				
				undoButton.setEnabled(!atStart);
				redoButton.setEnabled(!atEnd);
				publishButton.setEnabled(true);
			}
		});
		
		eventBus.addHandler(LinkEditorEvent.getType(), new LinkEditorHandler() {
			@Override
			public void onLinkEditor(LinkEditorEvent event) {
				if ( event.isHidden() ) {
					linkEditor.setLinkableItem(null);
					linkEditor.setEditableItem(null, false, false);
					linkEditor.setUrl("");
					linkEditor.setVisible(false);					
				} else {
					if ( editedItem instanceof EditableTextItem ) {
						linkEditor.setEditableItem((EditableTextItem)editedItem, event.isBold(), event.isItalic());
						linkEditor.setUrl(event.getUrl());
						linkEditor.setVisible(true);
					} else if ( editedItem instanceof LinkableItem ) {
						linkEditor.setLinkableItem((LinkableItem)editedItem);
						linkEditor.setUrl(event.getUrl());
						linkEditor.setVisible(true);
					}				
				}
			}
		});
		
		eventBus.addHandler(ContentManagamentEvent.getType(), new ContentManagamentHandler() {
			
			@Override
			public void onContentManagament(ContentManagamentEvent event) {
				if ( EventType.CONTENT_PUBLISHING_FAILED.equals(event.getEventType()) )
					publishButton.setEnabled(true);
			}
		});
	}

	@UiHandler("undoButton")
	void onUndo(ClickEvent e) {
		eventBus.fireEventDeferred(new UndoEvent());
	}
	
	@UiHandler("redoButton")
	void onRedo(ClickEvent e) {
		eventBus.fireEventDeferred(new RedoEvent());
	}
	
	@UiHandler("logoutButton")
	void onLogout(ClickEvent e) {
		eventBus.fireEventDeferred(new LogoutEvent());
	}
	
	@UiHandler("publishButton")
	void onPublish(ClickEvent e) {
		eventBus.fireEventDeferred(new PublishEvent());
		publishButton.setEnabled(false);
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		
		int posX = Window.getClientWidth() / 2 - this.getOffsetWidth() / 2;
		Integer left = propertyManager.getPropertyFloat(POSITION_X, (float)posX, PropertyScope.APPLICATION).intValue();
		Integer top = propertyManager.getPropertyFloat(POSITION_Y, (float)20, PropertyScope.APPLICATION).intValue();
		
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

	private void dragStart(MouseDownEvent event) {
		
		if ( !shouldDragStart(event.getNativeEvent().getEventTarget()) ) {
			return;
		}
		
		dragging = true;
		DOM.setCapture(getElement());

		newItems.getElement().getStyle().setDisplay(Display.NONE);
		addStyleName(style.containerDragged());
		
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

	private void dragEnd(MouseUpEvent event) {
		if ( dragging ) {
			dragging = false;
			DOM.releaseCapture(getElement());
			
			int posX = event.getClientX() - mouseOffsetX;
			int posY = event.getClientY() - mouseOffsetY;
			
			setPosition(posX, posY, true, true);
			
			newItems.getElement().getStyle().clearDisplay();
			removeStyleName(style.containerDragged());
		}
	}
	
	private void onWindowResize() {
		setPosition(getElement().getOffsetLeft(), getElement().getOffsetTop(), true, true);
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
		
		if ( y > windowHeight/2 ) {
			addStyleName(EditorClientBundle.INSTANCE.css().toolbarBottomPosition());
			newItems.addStyleName(EditorClientBundle.INSTANCE.css().toolbarIconsUp());
			newItems.removeStyleName(EditorClientBundle.INSTANCE.css().toolbarIconsDown());
		} else {
			removeStyleName(EditorClientBundle.INSTANCE.css().toolbarBottomPosition());
			newItems.addStyleName(EditorClientBundle.INSTANCE.css().toolbarIconsDown());
			newItems.removeStyleName(EditorClientBundle.INSTANCE.css().toolbarIconsUp());
		}
		
		if ( save ) {
			propertyManager.setProperty(POSITION_X, x, PropertyScope.APPLICATION);
			propertyManager.setProperty(POSITION_Y, y, PropertyScope.APPLICATION);
		}
	}
	
	public void setDragController(DragController dragController) {
		for ( Widget newItem : newItems ) {
			if ( newItem instanceof NewItem )
				dragController.makeDraggable(newItem);
		}
	}
	
	private boolean shouldDragStart(EventTarget eventTarget) {
		boolean result = false;

		if ( Element.is(eventTarget) ) {
			if ( DomUtil.hasClassName(Element.as(eventTarget), style.dragHandle())) {
				result = true;
			}
		}

		return result;
	}
	
	private void onStartEditing(StartEditingEvent event) {
		editedItem = event.getItem();
		
		List<String> styles = contentStyleRegistry.get(editedItem.getContainer().getName(), editedItem.getItemSelector());
		if ( styles != null && styleSelector != null ) {
			styleSelector.setStyles(styles, editedItem.getStyle());
			styleSelector.setVisible(true);
		}
	}
	
	private void onEndEditing(EndEditingEvent event) {
		editedItem = null;
		
		if ( styleSelector != null ) {
			styleSelector.setVisible(false);
		}
		
		linkEditor.setLinkableItem(null);
		linkEditor.setVisible(false);
	}

	private void createComponents() {
		String toolbarComponentsConfig = configRegistry.get(TOOLBAR_COMPONENTS, DEFAULT_TOOLBAR_COMPONENTS);
		String[] toolbarComponents = toolbarComponentsConfig.split(",");
		
		for ( String component : toolbarComponents ) {
			if ( component == null || "".equals(component) ) continue;
			
			ToolbarComponent toolbarComponent;
			try {
				toolbarComponent = ToolbarComponent.valueOf(component.trim().toUpperCase());
			} catch (Throwable e) {
				eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, 
						messages.invalidConfigParameter(TOOLBAR_COMPONENTS, component)));
				continue;
			}
			
			Widget toolbarItem = null;
			switch ( toolbarComponent ) {
				case HEADING1:
					toolbarItem = new NewHeading1Item("");
					break;
				case HEADING2:
					toolbarItem = new NewHeading2Item("");
					break;
				case HEADING3:
					toolbarItem = new NewHeading3Item("");
					break;
				case HEADING4:
					toolbarItem = new NewHeading4Item("");
					break;
				case HEADING5:
					toolbarItem = new NewHeading5Item("");
					break;
				case HEADING6:
					toolbarItem = new NewHeading6Item("");
					break;
				case TEXTLIST:
					toolbarItem = new NewListItem("");
					break;
				case TEXT:
					toolbarItem = new NewTextItem("");
					break;
				case FILE:
					toolbarItem = new NewFileItem();
					break;
				case IMAGE:
					toolbarItem = new NewImageItem();
					break;
				case SLIDESHOW:
					//toolbarItem = new NewSlideshowItem();
					break;
				case SLIDER:
					//toolbarItem = new NewSliderItem();
					break;
				case VIDEO:
					toolbarItem = new NewVideoItem();
					break;
				case FLASH:
					//toolbarItem = isLicensedVersion ? new NewFlashItem() : null;
					break;
				case MAP:
					toolbarItem = new NewMapItem();
					break;
				case HTML:
					toolbarItem = new NewHtmlItem();
					break;
				case CBOX:
					//toolbarItem = new NewContactItem();
					break;
				case SEP:
					toolbarItem = new NewItemSeparator();
					break;
			}
			
			if ( toolbarItem != null )
				newItems.add(toolbarItem);
		}
		
	}
}
