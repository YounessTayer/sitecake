package com.sitecake.contentmanager.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.sitecake.commons.client.config.ConfigRegistry;
import com.sitecake.commons.client.util.DocumentSelection;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.Locale;
import com.sitecake.commons.client.util.SynchronizationBarrier;
import com.sitecake.contentmanager.client.content.ContentManager;
import com.sitecake.contentmanager.client.contextmenu.ContextMenu;
import com.sitecake.contentmanager.client.dnd.DnDUploadDragController;
import com.sitecake.contentmanager.client.editable.EditableDomUtils;
import com.sitecake.contentmanager.client.editable.Selection;
import com.sitecake.contentmanager.client.errors.ErrorNotificationManager;
import com.sitecake.contentmanager.client.item.slideshow.SlideshowEditor;
import com.sitecake.contentmanager.client.pages.PageManager;
import com.sitecake.contentmanager.client.resources.LocaleProxy;
import com.sitecake.contentmanager.client.select.LassoSelectorController;
import com.sitecake.contentmanager.client.session.SessionManager;
import com.sitecake.contentmanager.client.tooltip.TooltipManager;
import com.sitecake.contentmanager.client.update.UpdateManager;
import com.sitecake.contentmanager.client.upload.UploadManager;

@GinModules(GinModule.class)
public interface GinInjector extends Ginjector {
	public static final GinInjector instance = GWT.create(GinInjector.class);
	
	TopContainer getTopContainer();
	GlobalConstants getGlobalConstants();
	EventBus getEventBus();
	SynchronizationBarrier getSynchronizationBarrier();
	ConfigRegistry getConfigRegistry();
	ContextMenu getContextMenu();
	PageEditor getPageEditor();
	DocumentSelection getDocumentSelection();
	DomSelector getDomSelector();
	TooltipManager getTooltipManager();
	UploadManager getUploadManager();
	SessionManager getSessionManager();
	UpdateManager getUpdateManager();
	ContentManager getContentManager();
	DnDUploadDragController getDnDUploadDragController();
	SlideshowEditor getSlideshowEditor();
	LassoSelectorController getLassoSelectorController();
	ContentStyleRegistry getContentStyleRegistry();
	ErrorNotificationManager getErrorNotificationManager();
	EditableDomUtils getEditableDomUtils();
	Selection getSelection();
	LocaleProxy getLocaleProxy();
	Locale getLocale();
	PageManager getPageManager();
}
