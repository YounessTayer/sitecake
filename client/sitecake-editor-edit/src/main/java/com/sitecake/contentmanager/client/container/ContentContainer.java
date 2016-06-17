package com.sitecake.contentmanager.client.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.commons.Point;
import com.sitecake.contentmanager.client.contextmenu.ContextMenu;
import com.sitecake.contentmanager.client.dnd.ContentContainerDropController;
import com.sitecake.contentmanager.client.dnd.ItemDragController;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.OverItemEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.ContentItemFactory;
import com.sitecake.contentmanager.client.item.ContentItemFactoryRegistry;

public class ContentContainer extends ComplexPanel implements InsertPanel {

	private String name;
	
	private EventBus eventBus;
	
	private ItemDragController dragController;
	
	private ContentContainerDropController dropController;
	
	private MouseMoveHandler overItemHandler;
	
	private Map<ContentItem, HandlerRegistration> overItemHandlerRegistrations;
	
	private boolean extended;
	
	private String prevHtml = "";
	
	private Point mouseOverPoint = new Point(0, 0);
	
	private ContextMenu contextMenu = GinInjector.instance.getContextMenu();
	
	public String getName() {
		return name;
	}

	public DropController getDropController() {
		return dropController;
	}

	public ContentContainer(ContentItemFactoryRegistry registry, EventBus eventBus, Element element, String name) {
		final ContentContainer self = this;
		this.name = name;
		this.eventBus = eventBus;
		extended = false;
		
		overItemHandlerRegistrations = new HashMap<ContentItem, HandlerRegistration>();
		
		setElement(element);
		onAttach();
		NodeList<Node> nodes = element.getChildNodes();
		for ( int idx = 0; idx < nodes.getLength(); idx++ ) {
			Node node = nodes.getItem(idx);
			if ( node.getNodeType() == Node.ELEMENT_NODE ) {
				ContentItemFactory itemFactory = registry.getFactory((Element)node);
				ContentItem item = null;
				try {
					item = itemFactory.create((Element)node);
				} catch (Exception e) {
					try {
						item = registry.getDefaultFactory().create((Element)node);
						eventBus.fireEventDeferred(new ErrorNotificationEvent(ErrorNotificationEvent.Level.WARNING, 
								"[cnt:" + this.name + "]: error: " + e.getClass().getName() + " " + e.getMessage(), "<xmp>" + DomUtil.outerHtml((Element)node) + "</xmp>"));
					} catch (Exception ex) {
						eventBus.fireEventDeferred(new ErrorNotificationEvent(ErrorNotificationEvent.Level.WARNING,
								"[cnt:" + this.name + "]: error: " + e.getClass().getName() + ex.getMessage(), "<xmp>" + DomUtil.outerHtml((Element)node) + "</xmp>"));
					}
				}
				if (item != null) {
					accept(item);
				}
			}
		}		
		
		overItemHandler = new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				ContentItem item = (ContentItem)event.getSource();
				if ( item.equals(contextMenu.getItem()) ) {
					return;
				}
				
				//GWT.log("onMouseOver");
				mouseOverPoint.setXY(event.getClientX() + Window.getScrollLeft(), event.getClientY() + Window.getScrollTop());
				if ( item.getContenxtRect().intersect(mouseOverPoint) ) {
					//GWT.log("onMouseOver:overItem triggered");
					self.eventBus.fireEvent(new OverItemEvent(item));
				}
			}
		};
		
		for ( int index=0; index < getWidgetCount(); index++ ) {
			ContentItem contentItem = (ContentItem)this.getWidget(index);
			contentItem.setContainer(this);
			HandlerRegistration handlerRegistration = contentItem.addMouseMoveHandler(overItemHandler);
			overItemHandlerRegistrations.put(contentItem, handlerRegistration);
			contentItem.onInclusion();
		}
		
		this.dropController = new ContentContainerDropController(eventBus, this);
	}
	
	public void setDragController(ItemDragController dragController) {
		this.dragController = dragController;
		dragController.registerDropController(dropController);
		
		for ( int index=0; index < getWidgetCount(); index++ ) {
			dragController.makeDraggable(getWidget(index));
		}
	}
	
	public int getItemNumber() {
		return getWidgetCount();
	}
	
	public ContentItem getItemAt(int index) {
		return (ContentItem)getWidget(index);
	}

	public int getItemIndex(ContentItem item) {
		return getWidgetIndex(item);
	}

	public void addItem(ContentItem item) {
		add(item);
		include(item);
	}

	public void insertItem(ContentItem item, int beforeIndex) {
		insert(item, beforeIndex);
		//include(item);		
	}

	public void replaceItem(ContentItem oldItem, ContentItem newItem) {
		int index = getWidgetIndex(oldItem);
		remove(index);
		insert(newItem, index);
	}

	public ContentItem replaceItemAt(ContentItem newItem, int index) {
		ContentItem oldItem = (ContentItem)getWidget(index); 
		remove(index);
		insert(newItem, index);
		return oldItem;
	}

	public void removeItem(ContentItem item) {
		// than remove it
		remove(item);
	}

	public ContentItem removeItemAt(int index) {
		ContentItem item = (ContentItem)getWidget(index);
		remove(index);
		return item;
	}

	public void removeAllItems() {
		List<ContentItem> forRemoval = new ArrayList<ContentItem>();
		for ( int index=0; index < getWidgetCount(); index++ ) {
			ContentItem contentItem = (ContentItem)this.getWidget(index);
			forRemoval.add(contentItem);
		}
		
		for ( ContentItem item : forRemoval ) {
			removeItem(item);
		}
	}
	
	@Override
	public void insert(Widget item, int beforeIndex) {
		insert(item, getElement(), beforeIndex, true);
	}
	
	public void insert(ContentItem item, int beforeIndex) {
		insert(item, getElement(), beforeIndex, true);
	}
	
	public void add(ContentItem child) {
		add(child, getElement());
	}
	
	/**
	 * Extend the container's size to easy item dropping.
	 * The adjustment is done only if the container current
	 * height is less than the requested height.
	 * 
	 * @param deltaHeight the requested minimal container height
	 */
	public void extend(int deltaHeight) {
		if ( !extended ) {
			extended = true;
			
			int height = DOMUtil.getClientHeight(getElement());
			
			if ( height < deltaHeight ) {
				getElement().getStyle().setPropertyPx("minHeight", deltaHeight);
			}
		}
	}
	
	public void contract() {
		if ( extended ) {
			extended = false;
			getElement().getStyle().clearProperty("minHeight");
		}
	}
	
	private void accept(ContentItem item) {
		// Logical attach.
		getChildren().add(item);
		
		// Adopt.
		adopt(item);			
	}
	
	public void include(ContentItem item, boolean draggable) {
		if ( draggable ) {
			dragController.makeDraggable(item);
		}
		item.setContainer(this);
		HandlerRegistration handlerRegistration = item.addMouseMoveHandler(overItemHandler);
		overItemHandlerRegistrations.put(item, handlerRegistration);
		item.onInclusion();
	}
	
	public void exclude(ContentItem item, boolean draggable) {
		if ( draggable ) {
			dragController.makeNotDraggable(item);
		}
		item.setContainer(null);
		overItemHandlerRegistrations.remove(item).removeHandler();
	}

	private void include(ContentItem item) {
		include(item, true);
	}
	
	private void exclude(ContentItem item) {
		exclude(item, true);
	}
	
	@Override
	public boolean remove(Widget child) {
		if ( child instanceof ContentItem ) {
			exclude((ContentItem)child);
		}
		return super.remove(child);
	}

	@Override
	protected void add(Widget child, com.google.gwt.user.client.Element container) {
		super.add(child, container);
		if ( child instanceof ContentItem ) {
			include((ContentItem)child);
		}
	}

	@Override
	protected void insert(Widget child, com.google.gwt.user.client.Element container, int beforeIndex, boolean domInsert) {
		if ( beforeIndex <= this.getWidgetCount() ) {
			super.insert(child, container, beforeIndex, domInsert);
		} else {
			super.add(child, container);
		}
		
		if ( child instanceof ContentItem ) {
			include((ContentItem)child);
		}
	}

	public String getHtml() {
		StringBuilder builder = new StringBuilder("");
		for ( int index=0; index < getWidgetCount(); index++ ) {
			Widget widget = getWidget(index);
			if ( widget instanceof ContentItem ) {
				builder.append(((ContentItem)widget).getHtml());
			}
		}
		return builder.toString();
	}
	
	public String getHtmlIfDirty() {
		String newHtml = getHtml();
		if ( newHtml.equals(prevHtml) ) {
			return null;
		} else {
			return newHtml;
		}
	}
}
