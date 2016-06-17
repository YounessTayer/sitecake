package com.sitecake.contentmanager.client.item.map;

import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.Window;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.commons.Axis;
import com.sitecake.contentmanager.client.commons.Point;
import com.sitecake.contentmanager.client.commons.Rectangle;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.resources.Messages;

public class MapItem extends ContentItem {
	
	public static final String DISCRIMINATOR = "sc-map";
	
	public static final String CONTENT_TYPE_NAME = "MAP";
	
	interface MapItemUiBinder extends UiBinder<Element, MapItem> {}
	
	private static MapItemUiBinder uiBinder = GWT.create(MapItemUiBinder.class);
	
	interface CssStyle extends CssResource {
		String working();
		String edit();
	}
	
	@UiField
	CssStyle cssStyle;
	
	@UiField
	DivElement codeContainer;
	
	@UiField
	DivElement selectOverlay;
	
	@UiField
	DivElement resizeControles;
	
	@UiField
	DivElement controlOverlay;
	
	@UiField
	Element handleNW;
	
	@UiField
	Element handleNE;
	
	@UiField
	Element handleSW;
	
	@UiField
	Element handleSE;
	
	private Point mousePosStart;
	
	private Point mousePosCurrent;
	
	private double startWidth;
	
	private double startHeight;
	
	private double dx;
	
	private double dy;
	
	private boolean mouseDown = false;
	
	private boolean mouseMoved = false;
	
	private boolean modified = false;
	
	private boolean resizeRatioLocked;
	
	private Axis dragAxis;
	
	private Rectangle RESIZE_MIN_RECT = new Rectangle(new Point(0, 0), 50, 30);
	
	private Rectangle RESIZE_MAX_RECT = new Rectangle(new Point(0, 0), 1, 1);
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	
	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	private GoogleEmbeddedMap embeddedMap;
	
	private double width;
	
	private double height;
	
	private String origText;
	
	public static MapItem create(String text) {
		MapItem item = GWT.create(MapItem.class);
		try {
			item.init(text);
		} catch (IllegalArgumentException ex) {
			item.eventBus.fireEventDeferred(new ErrorNotificationEvent(
					ErrorNotificationEvent.Level.WARNING, item.messages.invalidMapInputCode()));
			item = null;
		}
		return item;
	}
	
	protected MapItem() {
		super();
	}

	void init(String text) {
		init(text, null);
	}
	
	void init(Element origElement) {
		String text = GinInjector.instance.getDomSelector().select("iframe", origElement).get(0).getParentElement().getInnerHTML();
		init(text, origElement);
	}
	
	private void init(String text, Element origElement) throws IllegalArgumentException {
		double width = 0, height = 0;
		
		if (origElement != null) {
			width = origElement.getClientWidth();
			height = origElement.getClientHeight();
		}

		Element element = uiBinder.createAndBindUi(this);
		if ( origElement != null ) {
			DomUtil.replaceElement(origElement, element);
		}
		setElement(element);

		origText = text;
		embeddedMap = parse(text);
		if ( embeddedMap == null ) {
			throw new IllegalArgumentException();
		}
		codeContainer.setInnerHTML(embeddedMap.getCode());
		
		mousePosStart = new Point(0, 0);
		mousePosCurrent = new Point(0, 0);
		
		if (width != 0) {
			resize(width, height);
		} else {
			resizeDefault();
		}
		
		initHandlers();
	}
	
	private void initHandlers() {
		addDomHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				mouseDown(event);
			}
		}, MouseDownEvent.getType());

		addDomHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				mouseUp(event);
			}
		}, MouseUpEvent.getType());
		
		addDomHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				mouseMove(event);
			}
		}, MouseMoveEvent.getType());
	}
	
	private void mouseDown(MouseDownEvent event) {
		if ( !edited ) {
			event.stopPropagation();
			event.preventDefault();
			return;
		}
		
		mouseMoved = false;
		if ( !mouseDown && shouldCapture(event) ) {
			event.stopPropagation();
			mouseDown = true;
			dragAxis = getDragAxis(event.getNativeEvent().getEventTarget());
			
			int x = event.getClientX() + Window.getScrollLeft();
			int y = event.getClientY() + Window.getScrollTop();
			mousePosStart.setXY(x, y);
			startWidth = width;
			startHeight = height;
			
			resizeRatioLocked = event.getNativeEvent().getShiftKey();
			
			RESIZE_MAX_RECT.setWidth(getMaxWidth());
			if ( resizeRatioLocked ) {
				RESIZE_MAX_RECT.setHeight(RESIZE_MAX_RECT.getWidth() / (width/height));
				RESIZE_MIN_RECT.setHeight(RESIZE_MIN_RECT.getWidth() / (width/height));
			} else {
				RESIZE_MAX_RECT.setHeight(10000);
			}
			
			
			DOM.setCapture(getElement());
		}
		event.preventDefault();
	}
	
	private void mouseUp(MouseUpEvent event) {
		if ( !edited ) {
			event.stopPropagation();
			event.preventDefault();
			return;
		}
		
		if ( mouseDown ) {
			mouseDown = false;
			DOM.releaseCapture(getElement());
			if ( mouseMoved ) {
				dragStop();
			}
		}
	}
	
	private void mouseMove(MouseMoveEvent event) {
		if ( !edited ) {
			event.stopPropagation();
			event.preventDefault();
			return;
		}
		
		if ( mouseDown ) {
			modified = true;
			int x = event.getClientX() + Window.getScrollLeft();
			int y = event.getClientY() + Window.getScrollTop();
			mousePosCurrent.setXY(x, y);
			dx = mousePosCurrent.getX() - mousePosStart.getX();
			dy = mousePosCurrent.getY() - mousePosStart.getY();

			if ( !mouseMoved ) {
				mouseMoved = true;
				DOMUtil.cancelAllDocumentSelections();
				dragStart();
			} else {
				dragMove();
			}
		
		}
		DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
	}
	
	private boolean shouldCapture(MouseDownEvent event) {
		boolean result = false;
		
		EventTarget eventTarget = event.getNativeEvent().getEventTarget();
		result = ( handleNW.equals(eventTarget) || handleNE.equals(eventTarget) || 
			handleSW.equals(eventTarget) || handleSE.equals(eventTarget) );
		
		return result;
	}
	
	private Axis getDragAxis(EventTarget eventTarget) {
		Axis axis = Axis.NONE;
		
		if ( handleNW.equals(eventTarget) ) {
			axis = Axis.NW;
		} else if ( handleNE.equals(eventTarget) ) {
			axis = Axis.NE;
		} else if ( handleSW.equals(eventTarget) ) {
			axis = Axis.SW;
		} else if ( handleSE.equals(eventTarget) ) {
			axis = Axis.SE;
		}

		return axis;
	}
	
	private void dragStart() {
		getElement().addClassName(cssStyle.working());
	}
	
	private void dragStop() {
		getElement().removeClassName(cssStyle.working());
		//eventBus.fireEventDeferred(new EditCompleteEvent());			
	}
	
	private void dragMove() {
		if ( dragAxis.equals(Axis.NW) || dragAxis.equals(Axis.SW) ) {
			dx = -dx;
		}
		if ( dragAxis.equals(Axis.NW) || dragAxis.equals(Axis.NE) ) {
			dy = -dy;
		}
		
		resize();			
	}

	
	@Override
	public void setSelected(boolean select) {
		if ( select && !selected ) {
			selected = true;
			selectOverlay.getStyle().setDisplay(Display.BLOCK);
		} else if ( !select && selected ){
			selected = false;
			selectOverlay.getStyle().setDisplay(Display.NONE);
		}
	}
	
	@Override
	public ContentItem cloneItem() {
		MapItem clone = new MapItem();
		cloneItem(clone);
		clone.init(origText);
		clone.embeddedMap = embeddedMap.cloneMap();
		clone.width = width;
		clone.height = height;
		return clone;
	}

	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}
	
	@Override
	public String getItemSelector() {
		return "div." + DISCRIMINATOR;
	}

	private double percentage(double val, double ref) {
		return (val * 100.0 / ref);
	}
	
	private double formatted(double val) {
		return Math.round(val * 1000.0)/1000.0;
	}
	
	@Override
	public String getHtml() {
		double cnt = DomUtil.getElementInnerWidth(container.getElement());
		double ratio = formatted(percentage(height, width));
		double widthRel = formatted(percentage(width, cnt));
		String widthStr = (widthRel > 99.5) ? "100%" : (width + "px");
		
		return "<div class=\"" + DISCRIMINATOR + "\" style=\"width:" + widthStr + ";max-width:100%;position:relative;\">" + 
				"<div style=\"width:100%;position:relative;overflow:hidden;height:0;padding-bottom:" + ratio + "%\">" + 
					embeddedMap.getCode() + 
				"</div></div>";
	}
	
	@Override
	public void startEditing(String mode) {
		if ( edited ) return;
		
		edited = true;
		setSelected(false);
		
		modified = false;
		getElement().addClassName(cssStyle.edit());
	}

	@Override
	public boolean stopEditing(boolean cancel) {
		getElement().removeClassName(cssStyle.edit());
		redraw();
		boolean dirty = super.stopEditing(cancel) | modified;
		return dirty;
	}

	private int getMaxWidth() {
		Double maxWidth = 0.0;
		
		if ( container != null ) {
			maxWidth = DomUtil.getElementInnerWidth(container.getElement());
		}
		
		return maxWidth.intValue();
	}
	
	@Override
	public void onInclusion() {
		super.onInclusion();
		// TODO: check why is this called three times on every drag/drop
		int maxWidth = getMaxWidth();
		if ( width <= 0.0 || width > maxWidth ) {
			resize(maxWidth, maxWidth/(width/height));
			redraw();
		}
	}
	
	private void resize() {
		// dx, dy, RESIZE_MAX_RECT, RESIZE_MIN_RECT, resizeRatioLocked, dragAxis
		if ( resizeRatioLocked ) {
			dy = dx / (width/height);
		}
		
		double newWidth = startWidth + dx;
		double newHeight = startHeight + dy;
		
		if ( resizeRatioLocked ) {
			if ( newWidth < RESIZE_MIN_RECT.getWidth() ) {
				newWidth = RESIZE_MIN_RECT.getWidth();
				newHeight = RESIZE_MIN_RECT.getHeight();
			} else if ( newWidth > RESIZE_MAX_RECT.getWidth() ) {
				newWidth = RESIZE_MAX_RECT.getWidth();
				newHeight = RESIZE_MAX_RECT.getHeight();
			}			
		} else {
			if ( newWidth < RESIZE_MIN_RECT.getWidth() ) {
				newWidth = RESIZE_MIN_RECT.getWidth();
			} else if ( newWidth > RESIZE_MAX_RECT.getWidth() ) {
				newWidth = RESIZE_MAX_RECT.getWidth();
			}
			if ( newHeight < RESIZE_MIN_RECT.getHeight() ) {
				newHeight = Double.valueOf(RESIZE_MIN_RECT.getHeight()).intValue();
			} else if ( newHeight > RESIZE_MAX_RECT.getHeight() ) {
				newHeight = Double.valueOf(RESIZE_MAX_RECT.getHeight()).intValue();
			}
		}
		
		resize(newWidth, newHeight);
	}
	
	private void resizeDefault() {
		double width = 800;
		if (container != null) {
			width = DomUtil.getElementInnerWidth(container.getElement());
		}
		double height = width * (9.0/16.0);
		resize(width, height);
	}
	
	private void redraw() {
		codeContainer.setInnerHTML(embeddedMap.getCode());
	}
	
	private void resize(double width, double height) {
		this.width = width;
		this.height = height;
		
		getElement().getStyle().setWidth(width, Unit.PX);
		getElement().getStyle().setHeight(height, Unit.PX);
	}
	
	public static GoogleEmbeddedMap parse(String input) {
		//GoogleEmbeddedMap map = new GoogleEmbeddedMap();
		return GoogleEmbeddedMap.create(input);
		//return null;
	}
	
	public static boolean testText(String input) {
		return parse(input) != null;
	}
	
}
