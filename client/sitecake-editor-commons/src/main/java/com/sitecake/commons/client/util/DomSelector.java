package com.sitecake.commons.client.util;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

public interface DomSelector {

	public JsArray<Element> select(String selector);
	public JsArray<Element> select(String selector, Node context);

}