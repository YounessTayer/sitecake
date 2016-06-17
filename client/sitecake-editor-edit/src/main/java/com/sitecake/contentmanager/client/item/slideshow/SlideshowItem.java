package com.sitecake.contentmanager.client.item.slideshow;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
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
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.commons.client.util.Guid;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.commons.Axis;
import com.sitecake.contentmanager.client.commons.Point;
import com.sitecake.contentmanager.client.commons.Rectangle;
import com.sitecake.contentmanager.client.event.EditCompleteEvent;
import com.sitecake.contentmanager.client.item.ContentItem;

public class SlideshowItem extends ContentItem {

	public static final String DISCRIMINATOR = "sc-slideshow";
	public static final String SLIDESHOW_PLAYER_DISCRIMINATOR = "slideshow";
	public static final String CONTENT_TYPE_NAME = "SLIDESHOW";
	
	interface SlideshowItemUiBinder extends UiBinder<Element, SlideshowItem> {}

	interface CssStyle extends CssResource {
		String working();
	}
	
	public enum Mode {
		VIEW,
		RESIZE,
		ARRANGE
	}
	
	private class State {
		
		private Rectangle rect;
		
		public Rectangle getRect() {
			return rect;
		}

		public void set(State state) {
			rect.set(state.rect);
		}

		public State(Rectangle rect) {
			this.rect = rect.clone();
		}

		public State clone() {
			return new State(rect);
		}
		
		@Override
		public boolean equals(Object obj) {
			if ( obj == null || !(obj instanceof State) ) {
				return false;
			}
			
			if ( obj == this ) {
				return true;
			}

			State state = (State)obj;
			
			return rect.equals(state.rect);
		}
		
		public void resize(double dx, double dy, Axis axis, Rectangle outerLimit, Rectangle innerLimit) {
			
			rect.resize(dx, dy, axis, false);
			
			if ( innerLimit != null ) {
				rect.innerLimit(innerLimit, Axis.SE);
			}
			
			if ( outerLimit != null ) {
				rect.outerLimit(outerLimit, false);
			}
		}
	}
	
	private static SlideshowItemUiBinder uiBinder = GWT.create(SlideshowItemUiBinder.class);
	
	private String id;

	private Mode mode = Mode.VIEW;

	private State finalState;
	
	private State confirmedState;
	
	private List<SlideshowImage> images;
	
	private double coverImageRatio;
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	
	@UiField
	CssStyle cssStyle;
	
	@UiField
	ImageElement coverImageElement;
	
	@UiField
	Element selectOverlay;
	
	@UiField
	Element frame;
	
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
	
	private double mousePosDx;
	
	@SuppressWarnings("unused")
	private double mousePosDy;
	
	private boolean mouseDown;
	
	private boolean mouseMoved;
	
	private Axis dragAxis;
	
	private Rectangle RESIZE_MIN_RECT = new Rectangle(new Point(0, 0), 50, 30);
	
	private Rectangle RESIZE_MAX_RECT = new Rectangle(new Point(0, 0), 1, 1);
	
	List<SlideshowImage> getImages() {
		return images;
	}

	void setImages(List<SlideshowImage> images) {
		this.images = images;
	}

	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}
	
	@Override
	public String getItemSelector() {
		return "div." + DISCRIMINATOR;
	}

	public static SlideshowItem create(List<SlideshowImage> images) {
		SlideshowItem item = GWT.create(SlideshowItem.class);
		item.init(images, null);
		return item;
	}

	public static SlideshowItem create(Element element) {
		SlideshowItem item = GWT.create(SlideshowItem.class);
		item.init(element);
		return item;
	}
	
	protected SlideshowItem() {
		super();
	}

	private void init(Element element) {
		List<SlideshowImage> images = new ArrayList<SlideshowImage>();
		
		
		for ( int i = 0; i < element.getChildCount(); i++ ) {
			SlideshowImage slideshowImage = new SlideshowImage();
			
			Node childNode = element.getChild(i);
			if ( Node.ELEMENT_NODE != childNode.getNodeType() )
				continue;
			
			Element itemElement = element.getChild(i).cast();
			
			String url = itemElement.getAttribute("href");

			slideshowImage.setUrl(url);
			slideshowImage.setDescription(itemElement.getAttribute("title"));
			
			ImageElement imgElement = itemElement.getFirstChildElement().cast();
			
			if ( imgElement != null ) {
				String coverUrl = imgElement.getSrc();
				int width = imgElement.getWidth();
				int height = imgElement.getHeight();
				
				slideshowImage.setCover(true);
				slideshowImage.setCoverUrl(coverUrl);
				slideshowImage.setCoverWidth(width);
				slideshowImage.setCoverHeight(height);
			} else {
				slideshowImage.setCover(false);				
			}
			
			images.add(slideshowImage);
		}

		init(images, element);
	}

	private void init(List<SlideshowImage> images, Element origElement) {
		this.images = images;

		Element element = uiBinder.createAndBindUi(this);
		element.addClassName(DISCRIMINATOR);
		SlideshowImage cover = getCoverImage();
		coverImageElement.setSrc(cover.getCoverUrl());
		coverImageElement.setWidth((int)cover.getCoverWidth());
		coverImageElement.setHeight((int)cover.getCoverHeight());
		coverImageRatio = (double)cover.getCoverWidth() / cover.getCoverHeight();
		
		if ( origElement != null ) {
			id = origElement.getId();
			DomUtil.replaceElement(origElement, element);
		} else {
			id = Guid.get();
		}
		setElement(element);

		double width = cover.getCoverWidth();
		double height = cover.getCoverHeight();
		
		finalState = new State(new Rectangle(new Point(0, 0), width, height));
		confirmedState = finalState.clone();
		
		mousePosStart = new Point(0, 0);
		mousePosCurrent = new Point(0, 0);	
		mouseMoved = false;
		mouseDown = false;
		
		initHandlers();
	}
	
	@Override
	public void onInclusion() {
		super.onInclusion();
		
		adjustDimension();
	}
	
	@Override
	public ContentItem cloneItem() {
		SlideshowItem clone = SlideshowItem.create(images);
		cloneItem(clone);
		return clone;
	}

	@Override
	public String getHtml() {
		long finalWidth = Math.round(finalState.getRect().getWidth());
		long finalHeight = Math.round(finalState.getRect().getHeight());
		
		String html = "<div id=\"" + id +
			"\" style=\"width:" + finalWidth + "px;height:" + finalHeight + "px;" +
			"\" class=\"" + DISCRIMINATOR + "\">";
		
		for ( SlideshowImage image : images ) {
			
			html += "<a class=\"" + SLIDESHOW_PLAYER_DISCRIMINATOR + 
				"\" rel=\"" + id + 
				"\" title=\"" + image.getDescription() +
				"\" href=\"" + image.getUrl() + 
				"\" style=\"" + ( image.isCover() ? "" : "display:none;" ) + "\">";
			
			if ( image.isCover() ) {
				html += "<img width=\"" + finalWidth +
					"\" height=\"" + finalHeight + 
					"\" alt=\"" + image.getDescription() + 
					"\" src=\"" + image.getCoverUrl() + "\"/>";
			}
			
			html += "</a>";
		}
		
		html += "</div>";
		
		return html;
	}

	@Override
	public boolean isEditable() {
		return true;
	}
	
	@Override
	public boolean isResizable() {
		return true;
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
	public void startEditing(String mode) {
		if ( !edited ) {
			setSelected(false);
			edited = true;
			confirmedState.set(finalState);
			if ( mode == null ) {
				//setMode(Mode.ARRANGE);
				setMode(Mode.RESIZE);
			} else {
				//setMode(Mode.valueOf(mode));
				setMode(Mode.RESIZE);
			}
		}
	}

	@Override
	public boolean stopEditing(boolean cancel) {
		
		if ( !edited ) {
			return false;
		}
		
		boolean dirty = false;
		
		if ( !cancel ) {
			dirty = !confirmedState.equals(finalState);
			finalState.set(confirmedState);
		}
		
		if ( super.stopEditing(cancel) ) {
			dirty = true;
		}
		
		if ( mode.equals(Mode.ARRANGE) ) {
			GinInjector.instance.getSlideshowEditor().stopEdit();
		}
		
		setMode(Mode.VIEW);
		DOM.releaseCapture(getElement());
		
		return dirty;
	}
	
	private SlideshowImage getCoverImage() {
		SlideshowImage cover = null;
		
		for ( SlideshowImage image : images ) {
			if ( image.isCover() ) {
				cover = image;
				break;
			}
		}
		
		return cover;
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
			return;
		}
		
		mouseMoved = false;
		
		if ( !mouseDown ) {
			dragAxis = getDragAxis(event.getNativeEvent().getEventTarget());
			if ( !dragAxis.equals(Axis.NONE) ) {
				event.stopPropagation();
				mouseDown = true;
				
				int x = event.getClientX() + Window.getScrollLeft();
				int y = event.getClientY() + Window.getScrollTop();
				mousePosStart.setXY(x, y);
				
				DOM.setCapture(getElement());
			}
		}
		event.preventDefault();
	}
	
	private void mouseMove(MouseMoveEvent event) {
		if ( !edited ) {
			return;
		}
		
		if ( mouseDown ) {
			int x = event.getClientX() + Window.getScrollLeft();
			int y = event.getClientY() + Window.getScrollTop();
			mousePosCurrent.setXY(x, y);
			
			mousePosDx = mousePosCurrent.getX() - mousePosStart.getX();
			mousePosDy = mousePosCurrent.getY() - mousePosStart.getY();

			if ( !mouseMoved ) {
				mouseMoved = true;
				DOMUtil.cancelAllDocumentSelections();
				dragStart();
			}
			dragMove();
		}
		DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
	}	
	
	private void mouseUp(MouseUpEvent event) {
		if ( !edited ) {
			return;
		}
		
		if ( mouseDown ) {
			mouseDown = false;
			DOM.releaseCapture(getElement());
			dragStop();
		}
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
	
	private void setMode(Mode mode) {
		this.mode = mode;
		resetCssStyle();
		
		switch ( mode ) {
		case VIEW:
			enterViewMode();
			break;

		case RESIZE:
			enterResizeMode();
			break;
		
		case ARRANGE:
			enterArrangeMode();
			break;
		}
		
		refresh();
	}

	private void refresh() {
		switch ( mode ) {
		case VIEW:
			refreshViewMode();
			break;

		case RESIZE:
			refreshResizeMode();
			break;
		
		case ARRANGE:
			break;
		}
	}
	
	private void dragStart() {
		switch ( mode ) {
			case RESIZE:
				dragStartResize();
				break;
			
			default:
				break;
		}
	}

	private void dragMove() {
		switch ( mode ) {
			case RESIZE:
				dragMoveResize();
				break;
				
			default:
				break;				
		}
	}

	private void dragStop() {
		switch ( mode ) {
			case RESIZE:
				dragStopResize();
				break;
				
			default:
				break;				
		}
	}

	private void enterViewMode() {
	}
	
	private void enterResizeMode() {
		confirmedState.set(finalState);
		frame.getStyle().setDisplay(Display.BLOCK);
	}
	
	private void enterArrangeMode() {
		GinInjector.instance.getSlideshowEditor().startEdit(this);
	}

	private void refreshViewMode() {
		setTo(finalState);
	}
	
	private void refreshResizeMode() {
		setTo(confirmedState);
	}
	
	private void dragStartResize() {
		getElement().addClassName(cssStyle.working());
	}
	
	private void dragMoveResize() {
		
		if ( dragAxis.equals(Axis.NW) || dragAxis.equals(Axis.SW) ) {
			mousePosDx = -mousePosDx;
		}
		
		confirmedState.set(finalState);
		
		double maxWidth = DomUtil.getElementInnerWidth(container.getElement());
		double maxHeight = maxWidth / coverImageRatio;
		
		RESIZE_MAX_RECT.setWidth( maxWidth );
		RESIZE_MAX_RECT.setHeight( maxHeight );

		double dy = ( mousePosDx * confirmedState.getRect().getHeight() ) / confirmedState.getRect().getWidth();
		
		confirmedState.resize(mousePosDx, dy, Axis.SE, RESIZE_MAX_RECT, RESIZE_MIN_RECT);
		
		refresh();		
	}

	private void dragStopResize() {
		getElement().removeClassName(cssStyle.working());
		//finalState.set(confirmedState);
		
		eventBus.fireEventDeferred(new EditCompleteEvent());		
	}
	
	public void exitArrangeMode() {
		eventBus.fireEventDeferred(new EditCompleteEvent());
	}
	
	private void resetCssStyle() {
		frame.getStyle().clearDisplay();
		selectOverlay.getStyle().setDisplay(Display.NONE);
		getElement().removeClassName(cssStyle.working());
	}
	
	private void setTo(State state) {
		double width = state.getRect().getWidth();
		double height = state.getRect().getHeight();
		
		Style style = getElement().getStyle();
		style.setWidth(width, Unit.PX);
		style.setHeight(height, Unit.PX);
		
		coverImageElement.setWidth((int)Math.round(width));
		coverImageElement.setHeight((int)Math.round(height));
	}
	
	private void adjustDimension() {
		double maxWidth = DomUtil.getElementInnerWidth(container.getElement());
		double maxHeight = maxWidth / coverImageRatio;
		
		if ( finalState.getRect().getWidth() > maxWidth ) {
			finalState.getRect().setWidthHeight(maxWidth, maxHeight);
		}
		refresh();		
	}
}
