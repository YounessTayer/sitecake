package com.sitecake.contentmanager.client.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.Globals;
import com.sitecake.commons.client.util.JavaScriptRegExp;
import com.sitecake.commons.client.util.UrlBuilder;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;
import com.sitecake.contentmanager.client.resources.LocaleProxy;
import com.sitecake.contentmanager.client.resources.Messages;

public class PageManager extends Composite {

	private static PageManagerUiBinder uiBinder = GWT
			.create(PageManagerUiBinder.class);

	interface PageManagerUiBinder extends
			UiBinder<Widget, PageManager> {
	}
	
	interface CssStyle extends CssResource {
	}
	
	@UiField 
	CssStyle cssStyle;

	@UiField 
	FlowPanel navPages;

	@UiField 
	FlowPanel draftPages;
	
	@UiField
	PageDetails pageDetails;
	
	private EventBus eventBus;
	private Messages messages;
	
	private RequestBuilder serviceRequest;
	
	private List<Page> pages;
	private List<PageWidget> pageWidgets;
	
	private PageDragController pageDragController;
	
	private String initialPagesData;
	private PageWidget targetPageWidget;
	
	@Inject
	public PageManager(EventBus eventBus, LocaleProxy localeProxy) {
		this.eventBus = eventBus;
		this.messages = localeProxy.messages();
		
		pages = new ArrayList<Page>();
		pageWidgets = new ArrayList<PageWidget>();
		
		initWidget(uiBinder.createAndBindUi(this));
		pageDragController = new PageDragController(navPages);
		
		pageDragController.registerDropController(new PageDropController(navPages));
		pageDragController.registerDropController(new PageDropController(draftPages));
		
		disable();
		initPageService();
		obtainPages();
		//simulate();
		
		pageDetails.setPageManager(this);
		
		//eventBus.fireEvent(new StartPageManagerEvent());
	}
	
	public void disable() {
		this.setVisible(false);
		GinInjector.instance.getLassoSelectorController().enable(true);
		cleanup();		
	}
	
	public void close() {
		String pid = getCurrentPageId();
		PageWidget page = null;
		for (Widget nw : navPages) {
			PageWidget pw = (PageWidget)nw;
			if (pw.getId().equals(pid)) {
				page = pw;
				break;
			}
		}
		for (Widget nw : draftPages) {
			PageWidget pw = (PageWidget)nw;
			if (pw.getId().equals(pid)) {
				page = pw;
				break;
			}
		}
		
		if (page == null) {
			gotoUrl(((PageWidget)navPages.getWidget(0)).getUrl());
		} else {
			if (!initialPagesData.equals(constructPagesData())) {
				gotoUrl(page.getUrl());
			} else {
				disable();				
			}
		}
	}
	
	public void enable() {
		drawExistingPages();
		GinInjector.instance.getLassoSelectorController().enable(false);
		this.setVisible(true);
	}
	
	public void clonePage(PageWidget page) {
		PageWidget newPage = page.clone(page);
		newPage.setPageTitle(constructClonePageTitle(newPage.getPageTitle()));
		newPage.setNavTitle(constructCloneNavTitle(newPage.getNavTitle()));
		newPage.setUrl(constructCloneUrl(newPage.getUrl()));
		FlowPanel pages = (FlowPanel)page.getParent();
		pages.insert(newPage, pages.getWidgetIndex(page) + 1);
		pageWidgets.add(newPage);
		pageDragController.makeDraggable(newPage);
		save();
	}
	
	public void deletePage(PageWidget page) {
		pageDragController.makeNotDraggable(page);
		pageWidgets.remove(page);
		if (page.getParent() == navPages &&
				navPages.getElement().getChildCount() > 1) {
			navPages.remove(page);
		}
		else {
			draftPages.remove(page);
		}
		ensureHomePage();
		save();
		pageDetails.select((PageWidget)navPages.getWidget(0));
	}
	
	public void save() {
		String currentPagesData = constructPagesData();
		savePages(currentPagesData);
	}
	
	public void confirm(PageWidget page) {
		gotoUrl(page.getUrl());
	}
	
	/**
	 * Goes through all pages and ensure that the
	 * first nav page has 'index.html' URL. Also, makes sure 
	 * that no other page has that URL.
	 */
	public void ensureHomePage() {
		int idx = 0;
		for (Widget nw : navPages) {
			PageWidget pageWidget = (PageWidget)nw;
			if (idx == 0) {
				pageWidget.setUrl("index.html");
			} else {
				if (pageWidget.getUrl().equals("index.html")) {
					pageWidget.setUrl(constructUrlFrom(
							pageWidget.getNavTitle().toLowerCase().replaceAll("\\(.+\\)", "")));
				}
			}
			idx++;
		}
		for (Widget nw : draftPages) {
			PageWidget pageWidget = (PageWidget)nw;
			if (pageWidget.getUrl().equals("index.html")) {
				pageWidget.setUrl(constructUrlFrom(
						pageWidget.getNavTitle().toLowerCase().replaceAll("\\(.+\\)", "")));
			}
		}		
	}
	
	public void select(PageWidget page) {
		pageDragController.clearSelection();
		pageDragController.select(page);
		pageDetails.select(page);
	}
	
	public void drop(final PageWidget page) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				ensureHomePage();
				select(page);
				save();
			}
		});
	}
	
	private void initPageService() {
		UrlBuilder urlBuilder = new UrlBuilder(Globals.get().getServiceUrl());
		urlBuilder.setParameter("service", "_pages");
		urlBuilder.setParameter("action", "pages");
		
		serviceRequest = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());		
		serviceRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");		
	}
	
	private void obtainPages() {
		try {
			serviceRequest.sendRequest("", new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					onExistingPages(response);
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					onExistingPagesError(exception);
				}
			});
		} catch (RequestException e) {
			onExistingPagesError(e);
		}
	}
	
	private void onExistingPages(Response response) {
		if ( response.getStatusCode() == 200 ) {
			PagesResponse pagesResponse = PagesResponse.get(response.getText()).cast();
			if ( pagesResponse.isSuccess() ) {
				pages = pagesResponse.pages();
				Collections.sort(pages, new Comparator<Page>() {
					public int compare(Page p1, Page p2) {
						return p1.getIdx() - p2.getIdx();
					}
				});				
			} else {
				eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, 
						messages.invalidServiceResponse(), pagesResponse.getErrorMessage()));				
			}
		} else {
			eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, 
					messages.invalidServiceResponse(), response.getStatusText() + " " + response.getText()));			
		}		
	}
	
	private void onExistingPagesError(Throwable exception) {
		eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, 
				messages.invalidServiceResponse(), exception.getMessage()));
	}
	
	private void drawExistingPages() {		
		PageWidget pageWidget;
		for (Page page : pages) {
			pageWidget = PageWidget.create(page);
			pageWidgets.add(pageWidget);
			if (page.getIdx() >= 0) {
				navPages.add(pageWidget);				
			} else {
				draftPages.add(pageWidget);
			}
			pageDragController.makeDraggable(pageWidget);
		}
		initialPagesData = constructPagesData();
		pageDetails.select((PageWidget)navPages.getWidget(0));
	}
	
	private void cleanup() {
		for (PageWidget w : pageWidgets) {
			pageDragController.makeNotDraggable(w);
		}
		pageWidgets.clear();
		navPages.clear();
		draftPages.clear();
	}
	
	private void simulate() {
		String txt = "{\"status\":0,\"pages\":[{\"id\":\"522928d58f6db6a0d2793d9273299a025fde7e5b\",\"idx\":1,\"title\":\"About us\",\"navtitle\":\"About\",\"home\":false,\"url\":\"about.html\",\"desc\":\"description of about page\",\"keywords\":\"\"},{\"id\":\"0ae64c190dd76e785de19f3b23f007da4d88d0fb\",\"idx\":2,\"title\":\"Contact us\",\"navtitle\":\"Contact\",\"home\":false,\"url\":\"contact.html\",\"desc\":\"description of contact page\",\"keywords\":\"contact, phone, email\"},{\"id\":\"42fe0c2bcb67b575fee0ddbfb707fce5ce102f7a\",\"idx\":0,\"title\":\"Home page\",\"navtitle\":\"Home\",\"home\":true,\"url\":\"index.html\",\"desc\":\"description of home page\",\"keywords\":\"home\"}]}";
		PagesResponse pagesResponse = PagesResponse.get(txt).cast();
		pages = pagesResponse.pages();
		Collections.sort(pages, new Comparator<Page>() {
			public int compare(Page p1, Page p2) {
				return p1.getIdx() - p2.getIdx();
			}
		});		
	}
	
	private String constructPagesData() {
		JSONArray pages = new JSONArray();
		int idx = 0;
		for (Widget nw : navPages) {
			PageWidget pageWidget = (PageWidget)nw;
			JSONObject page = new JSONObject();
			page.put("idx", new JSONNumber(idx));
			page.put("title", new JSONString(pageWidget.getPageTitle()));
			page.put("navtitle", new JSONString(pageWidget.getNavTitle()));
			page.put("desc", new JSONString(pageWidget.getPageDescription()));
			page.put("keywords", new JSONString(pageWidget.getPageKeywords()));			
			page.put("url", new JSONString(pageWidget.getUrl()));
			page.put("home", JSONParser.parseStrict((idx == 0) ? "true" : "false"));
			if (pageWidget.isCloned()) {
				page.put("tid", new JSONString(pageWidget.getId()));
				page.put("cid", new JSONString(pageWidget.getCid()));				
			} else {
				page.put("id", new JSONString(pageWidget.getId()));
			}
			pages.set(idx, page);
			idx++;
		}
		for (Widget dw : draftPages) {
			PageWidget pageWidget = (PageWidget)dw;
			JSONObject page = new JSONObject();
			page.put("idx", new JSONNumber(-1));
			page.put("title", new JSONString(pageWidget.getPageTitle()));
			page.put("navtitle", new JSONString(pageWidget.getNavTitle()));
			page.put("desc", new JSONString(pageWidget.getPageDescription()));
			page.put("keywords", new JSONString(pageWidget.getPageKeywords()));			
			page.put("url", new JSONString(pageWidget.getUrl()));
			page.put("home", JSONParser.parseStrict("false"));
			if (pageWidget.isCloned()) {
				page.put("tid", new JSONString(pageWidget.getId()));
				page.put("cid", new JSONString(pageWidget.getCid()));
			} else {
				page.put("id", new JSONString(pageWidget.getId()));
			}
			pages.set(idx, page);
			idx++;			
		}
		return pages.toString();
	}
	
	private void savePages(String pagesData) {
		try {
			serviceRequest.sendRequest("pages=" + pagesData, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					onSavePages(response);
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					onSavePagesError(exception);
				}
			});
		} catch (RequestException e) {
			onExistingPagesError(e);
		}
	}
	
	private void onSavePages(Response response) {
		if ( response.getStatusCode() == 200 ) {
			PagesResponse pagesResponse = PagesResponse.get(response.getText()).cast();
			if ( pagesResponse.isSuccess() ) {
				for (Page page : pagesResponse.pages()) {
					if ((targetPageWidget.isCloned() && targetPageWidget.getCid().equals(page.getCid())) ||
							(!targetPageWidget.isCloned() && targetPageWidget.getId().equals(page.getId()))) {
						gotoUrl(page.getUrl());
					}
				}
			} else {
				eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, 
						messages.invalidServiceResponse(), pagesResponse.getErrorMessage()));				
			}
		} else {
			eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, 
					messages.invalidServiceResponse(), response.getStatusText() + " " + response.getText()));			
		}		
	}
	
	private void onSavePagesError(Throwable exception) {
		eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.WARNING, 
				messages.invalidServiceResponse(), exception.getMessage()));
	}
	
	private String constructUrlFrom(String text) {
		return constructCloneUrl(URL.encode(text) + ".html");
	}
	
	public String constructCloneUrl(String url) {
		String cloneUrl;
		List<String> matches = JavaScriptRegExp.match("^(.+)(\\-([0-9]+))?\\.html$", url);
		String urlBase = matches.get(1);
		int cnt = 0;
		if (matches.size() >= 4 && matches.get(3) != null) {
			cnt = Integer.valueOf(matches.get(3));
		}
		do {
			cnt++;
			cloneUrl = urlBase + "-" + cnt + ".html";
		} while (pageUrlExists(cloneUrl));
		return cloneUrl;	
	}

	private String constructClonePageTitle(String title) {
		String cloneTitle;
		List<String> matches = JavaScriptRegExp.match("^(.+)(\\(([0-9]+)\\))?$", title);
		String titleBase = matches.get(1);
		int cnt = 0;
		if (matches.size() >= 4 && matches.get(3) != null) {
			cnt = Integer.valueOf(matches.get(3));
		}
		do {
			cnt++;
			cloneTitle = titleBase + "(" + cnt + ")";
		} while (pageTitleExists(cloneTitle));
		return cloneTitle;	
	}

	private String constructCloneNavTitle(String title) {
		String cloneTitle;
		List<String> matches = JavaScriptRegExp.match("^(.+)(\\(([0-9]+)\\))?$", title);
		String titleBase = matches.get(1);
		int cnt = 0;
		if (matches.size() >= 4 && matches.get(3) != null) {
			cnt = Integer.valueOf(matches.get(3));
		}
		do {
			cnt++;
			cloneTitle = titleBase + "(" + cnt + ")";
		} while (navTitleExists(cloneTitle));
		return cloneTitle;	
	}
	
	private boolean pageUrlExists(String url) {
		for (PageWidget w : pageWidgets) {
			if (url.equals(w.getUrl())) 
					return true;
		}
		return false;
	}
	
	private boolean pageTitleExists(String title) {
		for (PageWidget w : pageWidgets) {
			if (title.equals(w.getPageTitle())) 
					return true;
		}
		return false;
	}

	private boolean navTitleExists(String title) {
		for (PageWidget w : pageWidgets) {
			if (title.equals(w.getNavTitle())) 
					return true;
		}
		return false;
	}
	
	private void gotoUrl(String url) {
		Window.Location.assign("sitecake.php?page=" + url);		
	}
	
	private native String getCurrentPageId()/*-{
		return $wnd.scpageid;
	}-*/;	
}
