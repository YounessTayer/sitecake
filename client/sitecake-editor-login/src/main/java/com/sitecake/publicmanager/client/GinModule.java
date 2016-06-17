package com.sitecake.publicmanager.client;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.sitecake.commons.client.config.ConfigRegistry;
import com.sitecake.commons.client.config.ServerConfigRegistry;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.Locale;
import com.sitecake.commons.client.util.SynchronizationBarrier;
import com.sitecake.commons.client.util.impl.SizzleDomSelector;
import com.sitecake.publicmanager.client.resources.LocaleProxy;
import com.sitecake.publicmanager.client.resources.LocaleProxyImpl;

public class GinModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(DomSelector.class).to(SizzleDomSelector.class).in(Singleton.class);
		bind(LoginManager.class).to(LoginManagerImpl.class).in(Singleton.class);
		bind(TopContainer.class).in(Singleton.class);
		bind(SynchronizationBarrier.class).in(Singleton.class);
		bind(ConfigRegistry.class).to(ServerConfigRegistry.class).in(Singleton.class);		
		bind(LocaleProxy.class).to(LocaleProxyImpl.class).in(Singleton.class);
		bind(Locale.class).in(Singleton.class);
	}

}
