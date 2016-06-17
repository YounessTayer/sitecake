package com.sitecake.contentmanager.client;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.sitecake.commons.client.config.ConfigRegistry;
import com.sitecake.commons.client.config.ServerConfigRegistry;
import com.sitecake.commons.client.util.DocumentSelection;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.Locale;
import com.sitecake.commons.client.util.SynchronizationBarrier;
import com.sitecake.commons.client.util.impl.SizzleDomSelector;
import com.sitecake.contentmanager.client.container.ClassTaggedContentContainerFactoryImpl;
import com.sitecake.contentmanager.client.container.ContentContainerFactory;
import com.sitecake.contentmanager.client.content.ContentManager;
import com.sitecake.contentmanager.client.content.ContentManagerImpl;
import com.sitecake.contentmanager.client.contextmenu.ContextMenu;
import com.sitecake.contentmanager.client.editable.EditableDomUtils;
import com.sitecake.contentmanager.client.editable.Selection;
import com.sitecake.contentmanager.client.editable.SelectionImpl;
import com.sitecake.contentmanager.client.errors.ErrorNotificationManager;
import com.sitecake.contentmanager.client.errors.ErrorNotificationManagerImpl;
import com.sitecake.contentmanager.client.history.EditorHistory;
import com.sitecake.contentmanager.client.history.EditorHistoryImpl;
import com.sitecake.contentmanager.client.item.ContentItemFactoryRegistry;
import com.sitecake.contentmanager.client.item.ContentItemFactoryRegistryProvider;
import com.sitecake.contentmanager.client.item.slideshow.SlideshowEditor;
import com.sitecake.contentmanager.client.pages.PageManager;
import com.sitecake.contentmanager.client.properties.BasicPropertyManager;
import com.sitecake.contentmanager.client.properties.PropertyManager;
import com.sitecake.contentmanager.client.resources.LocaleProxy;
import com.sitecake.contentmanager.client.resources.LocaleProxyImpl;
import com.sitecake.contentmanager.client.select.LassoSelectorController;
import com.sitecake.contentmanager.client.session.SessionManager;
import com.sitecake.contentmanager.client.session.SessionManagerImpl;
import com.sitecake.contentmanager.client.storage.ClientPermanentStorage;
import com.sitecake.contentmanager.client.storage.ClientPermanentStorageImpl;
import com.sitecake.contentmanager.client.storage.ClientSessionStorage;
import com.sitecake.contentmanager.client.storage.ClientSessionStorageImpl;
import com.sitecake.contentmanager.client.tooltip.TooltipManager;
import com.sitecake.contentmanager.client.tooltip.TooltipManagerImpl;
import com.sitecake.contentmanager.client.update.BasicUpdateManagerImpl;
import com.sitecake.contentmanager.client.update.UpdateManager;
import com.sitecake.contentmanager.client.upload.UploadManager;
import com.sitecake.contentmanager.client.upload.UploadManagerImpl;

public class GinModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(TopContainer.class).in(Singleton.class);
		
		bind(GlobalConstants.class).in(Singleton.class);
		
		bind(DomSelector.class).to(SizzleDomSelector.class).in(Singleton.class);
		//bind(DomSelector.class).to(JqueryDomSelector.class).in(Singleton.class);
		
		bind(DocumentSelection.class).in(Singleton.class);
		
		bind(SynchronizationBarrier.class).in(Singleton.class);
		bind(EventBus.class).in(Singleton.class);
		bind(ErrorNotificationManager.class).to(ErrorNotificationManagerImpl.class).in(Singleton.class);
		bind(ConfigRegistry.class).to(ServerConfigRegistry.class).in(Singleton.class);
		bind(LocaleProxy.class).to(LocaleProxyImpl.class).in(Singleton.class);
		bind(ContentStyleRegistry.class).to(ContentStyleRegistryImpl.class).in(Singleton.class);
		
		bind(ClientSessionStorage.class).to(ClientSessionStorageImpl.class).in(Singleton.class);
		bind(ClientPermanentStorage.class).to(ClientPermanentStorageImpl.class).in(Singleton.class);
		
		bind(SessionManager.class).to(SessionManagerImpl.class).in(Singleton.class);
		//bind(SessionManager.class).to(FakeSessionManagerImpl.class).in(Singleton.class);
		
		bind(UpdateManager.class).to(BasicUpdateManagerImpl.class).in(Singleton.class);
		
		bind(ContentManager.class).to(ContentManagerImpl.class).in(Singleton.class);
		
		bind(ContextMenu.class).in(Singleton.class);
		
		bind(PropertyManager.class).to(BasicPropertyManager.class).in(Singleton.class);
		bind(KeyboardController.class).in(Singleton.class);
		bind(ContentContainerFactory.class).to(ClassTaggedContentContainerFactoryImpl.class).in(Singleton.class);
		bind(PageEditor.class).in(Singleton.class);
		
		bind(ContentItemFactoryRegistry.class).toProvider(ContentItemFactoryRegistryProvider.class).in(Singleton.class);
		bind(EditorHistory.class).to(EditorHistoryImpl.class).in(Singleton.class);
		
		bind(UploadManager.class).to(UploadManagerImpl.class).in(Singleton.class);
		bind(TooltipManager.class).to(TooltipManagerImpl.class).in(Singleton.class);
		
		bind(SlideshowEditor.class).in(Singleton.class);
		
		bind(LassoSelectorController.class).in(Singleton.class);
		
		bind(EditableDomUtils.class).in(Singleton.class);
		bind(Selection.class).to(SelectionImpl.class).in(Singleton.class);
		
		bind(Locale.class).in(Singleton.class);
		
		bind(PageManager.class).in(Singleton.class);
	}

}
