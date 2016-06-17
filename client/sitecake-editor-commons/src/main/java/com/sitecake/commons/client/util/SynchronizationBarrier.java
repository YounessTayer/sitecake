package com.sitecake.commons.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

public class SynchronizationBarrier {
	
	private int counter = -1;
	
	private List<Command> commands = new ArrayList<Command>();
	
	public void lock() {
		counter = ( counter == -1 ) ? 1 : (counter + 1);
	}
	
	public void release() {
		counter = ( counter == -1 ) ? -1 : (counter - 1);
		if ( counter == 0 ) {
			counter = -1;
			execute();
		}
	}
	
	public void executeOnReady(Command command) {
		commands.add(command);
	}
	
	private void execute() {
		List<Command> copy = new ArrayList<Command>(commands);
		commands.clear();
		for ( Command command : copy ) {
			command.execute();
		}
	}
	
	public void reset() {
		counter = -1;
		commands.clear();
	}
}
