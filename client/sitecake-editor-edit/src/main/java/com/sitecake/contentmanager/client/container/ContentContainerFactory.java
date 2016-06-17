package com.sitecake.contentmanager.client.container;

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;

public interface ContentContainerFactory {
	public List<ContentContainer> create();
	public Map<String, Element> list();
}
