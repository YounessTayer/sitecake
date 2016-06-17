package com.sitecake.contentmanager.client.item.slideshow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.util.Area;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.TopContainer;
import com.sitecake.contentmanager.client.select.LassoSelectorController;
import com.sitecake.contentmanager.client.select.SelectContext;
import com.sitecake.contentmanager.client.select.SelectorHandler;
import com.sitecake.contentmanager.client.select.VetoSelectException;

public class SlideshowEditor extends Composite {

	private static SlideshowEditorUiBinder uiBinder = GWT
			.create(SlideshowEditorUiBinder.class);

	interface SlideshowEditorUiBinder extends UiBinder<Widget, SlideshowEditor> {
	}

	@UiField
	HTML closeButton;

	@UiField
	AbsolutePanel boundryPanel;
	
	@UiField
	FlowPanel imageContainer;
	
	private Map<Element, String> savedDisplayProperty;
	
	private int savedScrollTop;
	private int savedScrollLeft;
	
	private SlideshowItem slideshow;
	
	private SlideshowEditorDragController dragController;
	
	private SlideshowEditorDropController dropController;
	
	private LassoSelectorController lassoSelectorController = GinInjector.instance.getLassoSelectorController();
	
	private TopContainer topContainer = GinInjector.instance.getTopContainer();
	
	private SelectorHandler originalLassoSelectorHandler;
	
	private SelectorHandler lassoSelectorHandler;
	
	public SlideshowEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		getElement().getStyle().setDisplay(Display.NONE);
		boundryPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		
		topContainer.add(this);
		savedDisplayProperty = new HashMap<Element, String>();
		dropController = new SlideshowEditorDropController(imageContainer);

		dragController = new SlideshowEditorDragController(boundryPanel);
		dragController.registerDropController(dropController);
		
		dragController.addDragHandler(new DragHandler() {
			
			@Override
			public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
			}
			
			@Override
			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			}
			
			@Override
			public void onDragStart(DragStartEvent event) {
			}
			
			@Override
			public void onDragEnd(DragEndEvent event) {
			}
		});
		
		lassoSelectorHandler = new SelectorHandler() {
			
			@Override
			public void onSelectStart(SelectContext context) throws VetoSelectException {
				lassoSelectStart(context);
			}
			
			@Override
			public void onSelectEnd(SelectContext context) {
				lassoSelectEnd(context);
			}
			
			@Override
			public void onSelectDrag(SelectContext context) {
				lassoSelectDrag(context);
			}
		};
	}

	@UiHandler("closeButton")
	void onClick(ClickEvent e) {
		stopEdit();
	}

	public void startEdit(SlideshowItem slideshow) {
		this.slideshow = slideshow;
		
		initImageContaier();
		show();
	}
	
	public void stopEdit() {
		hide();
		
		dragController.clear();
		
		slideshow.exitArrangeMode();
		emptyImageContainer();
	}
	
	private void show() {
		originalLassoSelectorHandler = lassoSelectorController.getSelectHandler();
		lassoSelectorController.setSelectHandler(lassoSelectorHandler);
		
		savedScrollTop = Window.getScrollTop();
		savedScrollLeft = Window.getScrollLeft();
		Window.scrollTo(0, 0);
		getElement().getStyle().setDisplay(Display.BLOCK);
		hideBodyContent();
	}

	private void hide() {
		lassoSelectorController.setSelectHandler(originalLassoSelectorHandler);
		showBodyContent();
		getElement().getStyle().setDisplay(Display.NONE);
		Window.scrollTo(savedScrollLeft, savedScrollTop);
	}
	
	private void hideBodyContent() {
		Element body = RootPanel.getBodyElement();
		Element bodyChild = body.getFirstChildElement();
		do {
			if ( !bodyChild.equals(getElement()) ) {
				savedDisplayProperty.put(bodyChild, bodyChild.getStyle().getDisplay());
				bodyChild.getStyle().setDisplay(Display.NONE);
			}
			bodyChild = bodyChild.getNextSiblingElement();
		} while ( bodyChild != null );
	}

	private void showBodyContent() {
		for ( Element bodyChild : savedDisplayProperty.keySet() ) {
			bodyChild.getStyle().setProperty("display", savedDisplayProperty.get(bodyChild));
		}
		savedDisplayProperty.clear();
	}
	
	private void emptyImageContainer() {
		imageContainer.clear();
	}
	
	private void initImageContaier() {
		List<SlideshowImage> images = slideshow.getImages();
		
		for ( SlideshowImage image : images ) {
			SlideshowEditorImage editorImage = new SlideshowEditorImage(image);
			imageContainer.add(editorImage);
			dragController.makeDraggable(editorImage);
		}
	}
	
	private void lassoSelectStart(SelectContext context) throws VetoSelectException {
		Location startLocation = new CoordinateLocation(context.getStartX(), context.getStartY());
		
		for ( int i = 0; i < imageContainer.getWidgetCount(); i++ ) {
			Widget widget = imageContainer.getWidget(i);
			Area widgetArea = new WidgetArea(widget, RootPanel.get());
			if ( widgetArea.intersects(startLocation) ) {
				throw new VetoSelectException();
			}
		}		
	}
	
	private void lassoSelectDrag(SelectContext context) {
		
	}
	
	private void lassoSelectEnd(SelectContext context) {
		
	}
}
