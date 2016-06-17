package com.sitecake.contentmanager.client.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.inject.Inject;
import com.sitecake.commons.client.util.JavaScriptRegExp;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.item.ContentItemFactoryRegistry;

public class ClassTaggedContentContainerFactoryImpl implements
		ContentContainerFactory {

	public static final String CONTENT_CONTAINER_SELECTOR = "*[class*=sc-content]";
	public static final String CONTENT_CONTAINER_NAME_PATTERN = ".*sc-content-([a-zA-z0-9_\\-]+).*";
	
	private EventBus eventBus;
	
	private ContentItemFactoryRegistry registry;
	
	@Inject
	public ClassTaggedContentContainerFactoryImpl(ContentItemFactoryRegistry registry, EventBus eventBus) {
		this.registry = registry;
		this.eventBus = eventBus;
	}

	@Override
	public List<ContentContainer> create() {
		List<ContentContainer> result = new ArrayList<ContentContainer>();
		
		JsArray<Element> containerElements = GinInjector.instance.getDomSelector().select(CONTENT_CONTAINER_SELECTOR);
		for ( int i = 0; i < containerElements.length(); i++ ) {
			Element element = containerElements.get(i);
			String className = element.getClassName();
			List<String> matches = JavaScriptRegExp.match(CONTENT_CONTAINER_NAME_PATTERN, className);
			if ( matches != null && matches.size() == 2 ) {
				String name = matches.get(1);
				ContentContainer container = new ContentContainer(registry, eventBus, element, name);
				result.add(container);
			}
		}
		return result;
	}

	@Override
	public Map<String, Element> list() {
		Map<String, Element> containers = new HashMap<String, Element>();
		JsArray<Element> containerElements = GinInjector.instance.getDomSelector().select(CONTENT_CONTAINER_SELECTOR);
		for ( int i = 0; i < containerElements.length(); i++ ) {
			Element element = containerElements.get(i);
			String className = element.getClassName();
			List<String> matches = JavaScriptRegExp.match(CONTENT_CONTAINER_NAME_PATTERN, className);
			if ( matches != null && matches.size() == 2 ) {
				String name = matches.get(1);
				containers.put(name, element);
			}
		}
		return containers;
	}

}
