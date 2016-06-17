package com.sitecake.commons.client.util.impl;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.sitecake.commons.client.util.DomSelector;

public class JqueryDomSelector implements DomSelector {

	@Override
	public native JsArray<Element> select(String selector)/*-{
		return $wnd.jQuery(selector).get();
	}-*/;
	
	@Override
	public native JsArray<Element> select(String selector, Node node)/*-{
		return $wnd.jQuery(node).find(selector).get();
	}-*/;	
}
