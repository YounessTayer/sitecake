package com.sitecake.contentmanager.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import com.sitecake.commons.client.util.SynchronizationBarrier;
import com.sitecake.contentmanager.client.errors.FatalErrorOverlay;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.resources.Messages;
import com.sitecake.contentmanager.client.resources.NonLocaleMessages;

public class Module implements EntryPoint {

	private static NonLocaleMessages nonLocaleMessages = GWT.create(NonLocaleMessages.class);
	
	private Messages messages;
	
	private GinInjector injector;
	private SynchronizationBarrier barrier;
	
	@Override
	public void onModuleLoad() {

		// set uncaught exception handler
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable throwable) {
				showErrorMessageOverlay(throwable);
			}
		});

		// use a deferred command so that the handler catches onModuleLoad2() exceptions
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				onModuleLoad2();
			}
		});
	}
	
	private void onModuleLoad2() {
		// inject needed (but not locale-dependant) global resources
		StyleInjector.inject(EditorClientBundle.INSTANCE.css().getText(),true);
		
		// initialize Gin mechanism
		injector = GinInjector.instance;
		
		// create the top container that all standalone widgets should be located within
		TopContainer topContainer = injector.getTopContainer();
		topContainer.getElement().setId("sitecake-toolbox");
		RootPanel.get().add(topContainer);
		
		// inject SizzleDomSelector 
		injector.getDomSelector();

		// create sync barrier
		barrier = injector.getSynchronizationBarrier();

		barrier.lock();

		// postpone the main execution branch until all remote-dependent services are ready
		barrier.executeOnReady(new Command() {
			public void execute() {
				onModuleLoad3();
			}
		});
		
		injector.getConfigRegistry();

		barrier.release();
	}
	
	private void onModuleLoad3() {
		barrier.reset();
		barrier.lock();

		barrier.executeOnReady(new Command() {
			public void execute() {
				onModuleLoad4();
			}
		});
		
		messages = injector.getLocaleProxy().messages();		
		
		barrier.release();
	}
	
	private void onModuleLoad4() {
		barrier.reset();
		barrier.lock();
		
		// postpone the main execution branch until all remote-dependent services are ready
		barrier.executeOnReady(new Command() {
			public void execute() {
				injector.getPageEditor();
			}
		});
		
		injector.getErrorNotificationManager();
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable throwable) {
				
				String text = "\n" + throwable.getMessage() + "\n";
				while (throwable != null) {
					StackTraceElement[] stackTraceElements = throwable.getStackTrace();
					text += throwable.toString() + "\n";
					for (int i = 0; i < stackTraceElements.length; i++) {
						text += "    at " + stackTraceElements[i] + "\n";
					}
					throwable = throwable.getCause();
					if (throwable != null) {
						text += "Caused by: ";
					}
				}
				
				String message = (messages != null) ? messages.uncaughtException() :
					nonLocaleMessages.uncaughtException();
				injector.getEventBus().fireEvent(new ErrorNotificationEvent(Level.FATAL, 
						message, text));
			}
		});
		
		// load all services (also remote-dependent ones)
		injector.getConfigRegistry();
		injector.getSessionManager();
		injector.getUpdateManager();
		
		barrier.release();
	}
	
	private void showErrorMessageOverlay(Throwable throwable) {
		
		String text = "Uncaught exception: ";
		while (throwable != null) {
			text += throwable.getMessage() + "\n";
			StackTraceElement[] stackTraceElements = throwable.getStackTrace();
			for (int i = 0; i < stackTraceElements.length; i++) {
				text += "    at " + stackTraceElements[i] + "\n";
			}
			throwable = throwable.getCause();
			if (throwable != null) {
				text += "Caused by: ";
			}
		}
		System.err.print(text);
		
		FatalErrorOverlay overlay;
		String errorMsg1 = (messages != null) ? messages.errorMessage1() :
			nonLocaleMessages.errorMessage1();
		String errorMsg2 = (messages != null) ? messages.errorMessage2() :
			nonLocaleMessages.errorMessage2();
		
		overlay = new FatalErrorOverlay(errorMsg1, errorMsg2, text);		
		RootPanel.get().add(overlay);
	}

}
