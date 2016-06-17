package com.sitecake.contentmanager.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;

public class EventBus extends SimpleEventBus {

	public EventBus() {
		super();
	}

	public void fireEventDeferred(final GwtEvent<?> event) {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				fireEvent(event);
			}
		});
	}

	
}
