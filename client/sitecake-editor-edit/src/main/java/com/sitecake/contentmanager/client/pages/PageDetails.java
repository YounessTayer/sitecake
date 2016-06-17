package com.sitecake.contentmanager.client.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class PageDetails extends Composite {

	private static PageDetailsUiBinder uiBinder = GWT
			.create(PageDetailsUiBinder.class);

	interface PageDetailsUiBinder extends
			UiBinder<Widget, PageDetails> {
	}
	
	interface CssStyle extends CssResource {
	}

	@UiField 
	CssStyle cssStyle;

	@UiField
	TextArea title;

	@UiField
	TextArea url;

	@UiField
	TextArea description;

	@UiField
	TextArea keywords;
	
	private PageWidget selectedPage;
	
	private PageManager pageManager;
	
	public void setPageManager(PageManager pageManager) {
		this.pageManager = pageManager;
	}

	public PageDetails() {
		initWidget(uiBinder.createAndBindUi(this));
		setDefaults();
		title.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				change();
			}
		});
		url.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				change();
			}
		});
		description.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				change();
			}
		});
		keywords.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				change();
			}
		});		
	}
	
	public void select(PageWidget page) {
		selectedPage = page;
		title.setText(page.getPageTitle());
		url.setText(page.getUrl());
		description.setText(page.getPageDescription());
		keywords.setText(page.getPageKeywords());
	}
	
	public void unselect(PageWidget page) {
		if (page.equals(selectedPage)) {
			selectedPage = null;
			setDefaults();
		}
	}
	
	private void setDefaults() {
		title.setText("page title");
		url.setText("some-url.html");
		description.setText("Some text that describes the page content");
		keywords.setText("key, words");		
	}
	
	private void change() {
		if (selectedPage == null) return;
		
		if (!"".equals(title.getText())) {
			selectedPage.setPageTitle(title.getText());
		}
		
		if (!"".equals(url.getText())) {
			String normUrl = normalizeUrl(url.getText());
			url.setText(normUrl);
			selectedPage.setUrl(normUrl);
		}
		
		selectedPage.setPageDescription(description.getText());
		selectedPage.setPageKeywords(keywords.getText());
		
		if (pageManager != null) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					pageManager.ensureHomePage();
					pageManager.save();
				}
			});
		}
	}
	
	private String normalizeUrl(String text) {
		String url = URL.encode(text.toLowerCase()
			.replaceAll("/", "-"));
		if (!url.endsWith(".html")) {
			url = url + ".html";
		}
		return pageManager.constructCloneUrl(url);
	}
}
