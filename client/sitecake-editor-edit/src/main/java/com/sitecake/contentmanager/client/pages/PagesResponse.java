package com.sitecake.contentmanager.client.pages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.sitecake.commons.client.util.BasicServiceResponse;

public class PagesResponse extends BasicServiceResponse {
	protected PagesResponse() {}
	
	public final List<Page> pages() {
		List<Page> pages = new ArrayList<Page>();
		JsArray<Page> rawPages = rawPages();
		if (rawPages != null) {
			for (int i = 0; i < rawPages.length(); i++) {
				pages.add(rawPages.get(i));
			}
		}
		return pages;
	}
	
	private final native JsArray<Page> rawPages()/*-{
		return this.pages ? this.pages : null;
	}-*/;
}
