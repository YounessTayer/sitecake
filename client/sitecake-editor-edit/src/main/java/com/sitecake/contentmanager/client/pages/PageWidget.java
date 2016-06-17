package com.sitecake.contentmanager.client.pages;

import java.util.Date;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;

public class PageWidget extends Composite implements HasMouseDownHandlers, 
		HasMouseUpHandlers, HasMouseMoveHandlers, HasMouseOutHandlers, HasDragHandle {
	
	private static PageWidgetUiBinder uiBinder = GWT
			.create(PageWidgetUiBinder.class);

	interface PageWidgetUiBinder extends
			UiBinder<Widget, PageWidget> {
	}
	
	interface CssStyle extends CssResource {
	}
	
	@UiField 
	CssStyle cssStyle;

	@UiField
	HTML dndHandle;
	
	@UiField
	Label title;

	@UiField
	Label navTitle;
	
	@UiField
	Label url;
	
	@UiField
	Label pageDescription;
	
	@UiField
	Label pageKeywords;
	
	@UiField
	Button cloneButton;
	
	@UiField
	Button deleteButton;
	
	private String id;
	
	private String cid;
	
	private boolean cloned;
	
	private String origUrl;
	
	public String getPageTitle() {
		return title.getText();
	}
	
	public void setPageTitle(String title) {
		this.title.setText(title);
	}
	
	public String getNavTitle() {
		return navTitle.getText();
	}
	
	public void setNavTitle(String title) {
		this.navTitle.setText(title);
	}
	
	public String getUrl() {
		return url.getText();
	}
	
	public void setUrl(String url) {
		this.url.setText(url);
	}
	
	public String getPageDescription() {
		return pageDescription.getText();
	}
	
	public void setPageDescription(String description) {
		pageDescription.setText(description);
	}

	public String getPageKeywords() {
		return pageKeywords.getText();
	}
	
	public void setPageKeywords(String keywords) {
		pageKeywords.setText(keywords);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCid() {
		return cid;
	}
	
	public boolean isCloned() {
		return cloned;
	}

	public void setCloned(boolean cloned) {
		this.cloned = cloned;
	}

	public String getOrigUrl() {
		return origUrl;
	}
	
	public static PageWidget create(Page page) {
		PageWidget pageWidget = GWT.create(PageWidget.class);
		pageWidget.init(page, false);
		return pageWidget;
	}

	public static PageWidget create(Page page, boolean cloned) {
		PageWidget pageWidget = GWT.create(PageWidget.class);
		pageWidget.init(page, false);
		return pageWidget;
	}
	
	public PageWidget clone(PageWidget page) {
		PageWidget clone = GWT.create(PageWidget.class);
		clone.cloned = true;
		clone.id = page.id;
		clone.cid = String.valueOf((new Date()).getTime());
		clone.setPageTitle(page.getPageTitle());
		clone.setNavTitle(page.getNavTitle());
		clone.setUrl(page.getUrl());
		clone.origUrl = page.origUrl;
		clone.setPageDescription(page.getPageDescription());
		clone.setPageKeywords(page.getPageKeywords());
		return clone;
	}
	
	public PageWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		cloneButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GinInjector.instance.getPageManager().clonePage(PageWidget.this);
			}
		});
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GinInjector.instance.getPageManager().deletePage(PageWidget.this);
			}
		});
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GinInjector.instance.getPageManager().select(PageWidget.this);				
			}
		}, ClickEvent.getType());
		addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.preventDefault();
				event.stopPropagation();
				GinInjector.instance.getPageManager().confirm(PageWidget.this);
			}
		}, DoubleClickEvent.getType());
	}
	
	private void init(Page page, boolean cloned) {
		id = page.getId();
		cid = String.valueOf((new Date()).getTime());
		this.cloned = cloned;
		title.setText(page.getTitle());
		navTitle.setText(page.getNavTitle());
		url.setText(page.getUrl());
		origUrl = page.getUrl();
		setPageDescription(page.getDesc());
		setPageKeywords(page.getKeywords());
	}

	@Override
	public Widget getDragHandle() {
		return dndHandle;
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
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}
	
	public int getParentIdx() {
		FlowPanel panel = (FlowPanel)this.getParent();
		return (panel != null) ? panel.getWidgetIndex(this) : -1;
	}
	
}
