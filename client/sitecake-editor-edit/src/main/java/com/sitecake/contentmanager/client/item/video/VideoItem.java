package com.sitecake.contentmanager.client.item.video;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
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
import com.sitecake.commons.client.util.JsStringUtil;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.commons.Axis;
import com.sitecake.contentmanager.client.commons.Point;
import com.sitecake.contentmanager.client.commons.Rectangle;
import com.sitecake.contentmanager.client.event.EditCompleteEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.SaveRequestEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.video.EmbeddedVideo.VideoType;
import com.sitecake.contentmanager.client.resources.Messages;

public class VideoItem extends ContentItem {
	
	public static final String DISCRIMINATOR = "sc-video";
	
	public static final String CONTENT_TYPE_NAME = "VIDEO";
	
	private static final String YOUTUBE_URL_RE = "(www\\.)?(youtu\\.be/|youtube\\.com/(embed/|v/|watch\\?v=))([0-9a-zA-Z_-]{10,12})(\\b.*[&\\?]{1}(hd=[0-1]))?";
	
	private static final String VIMEO_URL_RE = "(player\\.)?vimeo\\.com(/video/|/moogaloop\\.swf\\?clip_id=|/)([0-9]+)";
	
	interface VideoItemUiBinder extends UiBinder<Element, VideoItem> {}
	
	private static VideoItemUiBinder uiBinder = GWT.create(VideoItemUiBinder.class);
	
	interface CssStyle extends CssResource {
		String working();
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
	
	private DomSelector domSelector = GinInjector.instance.getDomSelector();
	
	public static VideoItem create(String text) {
		VideoItem item = GWT.create(VideoItem.class);
		try {
			item.init(text);
		} catch (IllegalArgumentException ex) {
			item.eventBus.fireEventDeferred(new ErrorNotificationEvent(
					ErrorNotificationEvent.Level.WARNING, item.messages.invalidVideoInputCode()));
			item = null;
		}
		return item;
	}
	
	private EmbeddedVideo embeddedVideo;

	protected VideoItem() {
		super();
	}
	
	void init(String text) {
		init(text, null);
	}
	
	void init(Element origElement) {
		init(origElement.getInnerHTML(), origElement);
	}
	
	private void init(String text, Element origElement) throws IllegalArgumentException {
		Element element = uiBinder.createAndBindUi(this);
		double itemWidth = -1.0, itemHeight = -1.0;
		
		if ( origElement != null ) {
			itemWidth = DomUtil.getElementOuterWidth(origElement);
			itemHeight = DomUtil.getElementOuterHeight(origElement);
			DomUtil.replaceElement(origElement, element);
		}
		
		setElement(element);

		embeddedVideo = parse(text);
		if ( embeddedVideo == null ) {
			throw new IllegalArgumentException();
		}
		
		if ( itemWidth > 0 ) {
			embeddedVideo.width = itemWidth;
			if ( itemHeight > 0 ) {
				embeddedVideo.height = itemHeight;
				embeddedVideo.ratio = itemWidth/itemHeight;
			} else {
				embeddedVideo.height = itemWidth/1.68;	
			}
		} else if ( embeddedVideo.width > 0 ) {
			embeddedVideo.height = embeddedVideo.width/embeddedVideo.ratio;			
		} else {
			embeddedVideo.width = getMaxWidth();
			embeddedVideo.height = embeddedVideo.width/embeddedVideo.ratio;
		}
		codeContainer.setInnerHTML(getEditCode());
		
		mousePosStart = new Point(0, 0);
		mousePosCurrent = new Point(0, 0);
		
		resize(embeddedVideo.width, embeddedVideo.height);
		
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
			startWidth = embeddedVideo.width;
			startHeight = embeddedVideo.height;
			
			resizeRatioLocked = event.getNativeEvent().getShiftKey();
			
			RESIZE_MAX_RECT.setWidth(getMaxWidth());
			if ( resizeRatioLocked ) {
				RESIZE_MAX_RECT.setHeight(RESIZE_MAX_RECT.getWidth() / embeddedVideo.ratio);
				RESIZE_MIN_RECT.setHeight(RESIZE_MIN_RECT.getWidth() / embeddedVideo.ratio);
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
		eventBus.fireEventDeferred(new EditCompleteEvent());			
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
		VideoItem clone = VideoItem.create(getPublicCode());
		cloneItem(clone);
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
		double ratio = formatted(percentage(embeddedVideo.height, embeddedVideo.width));
		double widthRel = formatted(percentage(embeddedVideo.width, cnt));
		String width = (widthRel > 99.5) ? "100%" : (embeddedVideo.width + "px");
		
		return "<div class=\"" + DISCRIMINATOR + "\" style=\"width:" + width + ";max-width:100%;position:relative;\">" +
				"<div style=\"width:100%;position:relative;overflow:hidden;height:0;padding-bottom:" + ratio + "%\">" + 
				getPublicCode() + 
			"</div></div>";
	}
	
	@Override
	public void startEditing(String mode) {
		if ( edited ) return;
		
		edited = true;
		setSelected(false);
		
		modified = false;
		resizeControles.getStyle().setDisplay(Display.BLOCK);
	}

	@Override
	public boolean stopEditing(boolean cancel) {
		resizeControles.getStyle().setDisplay(Display.NONE);
		boolean dirty = super.stopEditing(cancel) | modified;
		if (dirty) {
			eventBus.fireEventDeferred(new SaveRequestEvent());
		}
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
		if ( embeddedVideo.width <= 0.0 || embeddedVideo.width > maxWidth ) {
			resize(maxWidth, maxWidth/embeddedVideo.ratio);
		}
	}
	
	private void resize() {
		// dx, dy, RESIZE_MAX_RECT, RESIZE_MIN_RECT, resizeRatioLocked, dragAxis
		if ( resizeRatioLocked ) {
			dy = dx / embeddedVideo.ratio;
		}
		
		double newWidth = startWidth + dx;
		double newHeight = startHeight + dy;
		
		if ( !resizeRatioLocked ) {
			embeddedVideo.ratio = newWidth / newHeight;
		}
		
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
	
	private void resize(double width, double height) {
		embeddedVideo.width = width;
		embeddedVideo.height = height;
		
		int targetWidth = Double.valueOf(width).intValue();
		int targetHeight = Double.valueOf(height).intValue();
		
		getElement().getStyle().setWidth(targetWidth, Unit.PX);
		getElement().getStyle().setHeight(targetHeight, Unit.PX);
		
		switch ( embeddedVideo.type ) {
		case YOUTUBE:
		case VIMEO:
			JsArray<Element> result = domSelector.select("iframe", codeContainer);
			if ( result.length() > 0 ) {
				Element iframe = result.get(0).cast();
				iframe.setAttribute("width", String.valueOf(targetWidth));
				iframe.setAttribute("height", String.valueOf(targetHeight));
			}
			break;
		}
	}
	
	private String getPublicCode() {
		String result = embeddedVideo.publicCode;
		//result = result.replaceAll("height=\"###\"", "height=\"" + Double.valueOf(embeddedVideo.height).intValue() + "\"");
		//result = result.replaceAll("width=\"###\"", "width=\"" + Double.valueOf(embeddedVideo.width).intValue() + "\"");
		result = result.replaceAll("height=\"###\"", "height=\"" + "100%" + "\"");
		result = result.replaceAll("width=\"###\"", "width=\"" + "100%" + "\" style=\"position:absolute;top:0;left:0\"");

		return result;
	}
	
	public String getEditCode() {
		String result = embeddedVideo.editCode;
		//result = result.replaceAll("height=\"###\"", "height=\"" + Double.valueOf(embeddedVideo.height).intValue() + "\"");
		//result = result.replaceAll("width=\"###\"", "width=\"" + Double.valueOf(embeddedVideo.width).intValue() + "\"");
		result = result.replaceAll("height=\"###\"", "height=\"" + "100%" + "\"");
		result = result.replaceAll("width=\"###\"", "width=\"" + "100%" + "\" style=\"position:absolute;top:0;left:0\"");

		return result;		
	}
	
	public static EmbeddedVideo parse(String input) {
		EmbeddedVideo embeddedVideo = parseYoutube(input);
		if ( embeddedVideo == null ) {
			embeddedVideo = parseVimeo(input);
		}
		
		return embeddedVideo;
	}
	
	public static boolean testText(String input) {
		return parse(input) != null;
	}
	
	private static EmbeddedVideo parseYoutube(String input) {
		// grp 4 = video id, opt grp 6 = hd 0/1
		List<String> matches = JsStringUtil.match(YOUTUBE_URL_RE, input);
		if ( matches == null || matches.size() < 5 || matches.get(4) == null ) return null;
		
		String videoId = matches.get(4);
		String playHD = ( matches.size() >= 7 && matches.get(6) != null ) ? matches.get(6) : "";
		
		EmbeddedVideo embeddedVideo = new EmbeddedVideo();
		embeddedVideo.type = VideoType.YOUTUBE;
		
		matches = JsStringUtil.match("width=\"?([0-9]+)\"", input);
		if ( matches != null && matches.size() > 0 ) {
			embeddedVideo.width = Double.parseDouble(matches.get(1));
		} else {
			embeddedVideo.width = -1;
		}
		
		matches = JsStringUtil.match("height=\"?([0-9]+)\"", input);
		if ( matches != null && matches.size() > 0 ) {
			embeddedVideo.height = Double.parseDouble(matches.get(1));
		} else {
			embeddedVideo.height = -1;
		}
		
		if ( embeddedVideo.width != -1 && embeddedVideo.height != -1 ) {
			embeddedVideo.ratio = embeddedVideo.width / embeddedVideo.height;
		} else {
			embeddedVideo.ratio = 1.68;
		}
		
		embeddedVideo.publicCode = "<iframe height=\"###\" frameborder=\"0\" width=\"###\" allowfullscreen=\"\" src=\"https://www.youtube.com/embed/" + videoId + 
			"?wmode=transparent&" + playHD + "\"></iframe>";
		embeddedVideo.editCode = "<iframe height=\"###\" frameborder=\"0\" width=\"###\" allowfullscreen=\"\" src=\"https://www.youtube.com/embed/" + videoId + 
			"?wmode=transparent&" + playHD + "\"></iframe>";

		return embeddedVideo;
	}
	
	private static EmbeddedVideo parseVimeo(String input) {
		// grp 3 = video id
		List<String> matches = JsStringUtil.match(VIMEO_URL_RE, input);
		if ( matches == null || matches.size() < 4 || matches.get(3) == null ) return null;
		
		String videoId = matches.get(3);
		
		EmbeddedVideo embeddedVideo = new EmbeddedVideo();
		embeddedVideo.type = VideoType.VIMEO;
		
		matches = JsStringUtil.match("width=\"?([0-9]+)", input);
		if ( matches != null && matches.size() > 0 ) {
			embeddedVideo.width = Double.parseDouble(matches.get(1));
		} else {
			embeddedVideo.width = -1;
		}
		
		matches = JsStringUtil.match("height=\"?([0-9]+)", input);
		if ( matches != null && matches.size() > 0 ) {
			embeddedVideo.height = Double.parseDouble(matches.get(1));
		} else {
			embeddedVideo.height = -1;
		}
		
		if ( embeddedVideo.width != -1 && embeddedVideo.height != -1 ) {
			embeddedVideo.ratio = embeddedVideo.width / embeddedVideo.height;
		} else {
			embeddedVideo.ratio = 1.68;
		}
		
		embeddedVideo.publicCode = "<iframe height=\"###\" frameborder=\"0\" width=\"###\" src=\"http://player.vimeo.com/video/" + videoId + "?title=0&byline=0&portrait=0" +
			"\"></iframe>";
		embeddedVideo.editCode = "<iframe height=\"###\" frameborder=\"0\" width=\"###\" src=\"http://player.vimeo.com/video/" + videoId + "?title=0&byline=0&portrait=0" +
			"\"></iframe>";

		return embeddedVideo;		
	}	
}

