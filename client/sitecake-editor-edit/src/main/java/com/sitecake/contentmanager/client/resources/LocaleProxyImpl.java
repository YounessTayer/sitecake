package com.sitecake.contentmanager.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.ConfigRegistry;
import com.sitecake.commons.client.util.Locale;
import com.sitecake.commons.client.util.SynchronizationBarrier;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;

public class LocaleProxyImpl implements LocaleProxy {

	private Messages messages;
	
	private SynchronizationBarrier synchronizationBarrier;
	
	private EventBus eventBus;

	@Override
	public Messages messages() {
		return messages;
	}
	
	@Inject
	protected LocaleProxyImpl(SynchronizationBarrier synchronizationBarrier,
			EventBus eventBus, ConfigRegistry configRegistry, Locale locale) {
		this.synchronizationBarrier = synchronizationBarrier;
		this.eventBus = eventBus;
		
		synchronizationBarrier.lock();
		
		String code = locale.code().toLowerCase();
		
		if (code.startsWith("sl")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesSl.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("sr")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesSr.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("es")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesEs.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("de")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesDe.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("fr")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesFr.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("dk")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesDk.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("it")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesIt.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("ru")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesRu.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("cs")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesCs.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("sk")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesSk.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.equals("pt-br")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesPtBr.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("pt")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesPt.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});			
		} else if (code.startsWith("pl")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesPl.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});		
		} else if (code.startsWith("zh-cn")) {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesZhCn.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});
		} else {
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					messages = GWT.create(MessagesEn.class);
					completeProcess();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					failureCompletition(reason);
				}
			});
		}		
	}
	
	private void completeProcess() {
		synchronizationBarrier.release();
	}

	private void failureCompletition(Throwable reason) {
		LocaleProxyImpl.this.eventBus.fireEvent(new ErrorNotificationEvent(Level.FATAL, reason.getMessage()));
		synchronizationBarrier.release();
	}
	
}
