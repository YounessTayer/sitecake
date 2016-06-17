package com.sitecake.contentmanager.client.item;

import com.google.inject.Provider;
import com.sitecake.contentmanager.client.item.generic.GenericContentItemFactory;
import com.sitecake.contentmanager.client.item.html.HtmlItemFactory;
import com.sitecake.contentmanager.client.item.image.ImageItemFactory;
import com.sitecake.contentmanager.client.item.list.ListItemFactory;
import com.sitecake.contentmanager.client.item.map.MapItemFactory;
import com.sitecake.contentmanager.client.item.slider.SliderItemFactory;
import com.sitecake.contentmanager.client.item.slideshow.SlideshowItemFactory;
import com.sitecake.contentmanager.client.item.text.TextItemFactory;
import com.sitecake.contentmanager.client.item.twitter.TwitterStatusItemFactory;
import com.sitecake.contentmanager.client.item.video.VideoItemFactory;

public class ContentItemFactoryRegistryProvider implements Provider<ContentItemFactoryRegistry> {

	@Override
	public ContentItemFactoryRegistry get() {
		ContentItemFactoryRegistry registry = new ContentItemFactoryRegistryImpl();

		// order is important, the last added factory will be consulted only if no factory
		// takes over
		registry.registerFactory(new TwitterStatusItemFactory());
		registry.registerFactory(new VideoItemFactory());
		registry.registerFactory(new MapItemFactory());
		registry.registerFactory(new SlideshowItemFactory());
		registry.registerFactory(new SliderItemFactory());
		registry.registerFactory(new ImageItemFactory());
		registry.registerFactory(new HtmlItemFactory());
		registry.registerFactory(new ListItemFactory());
		registry.registerFactory(new TextItemFactory());
		registry.registerDefaultFactory(new GenericContentItemFactory());
		return registry;
	}

}
