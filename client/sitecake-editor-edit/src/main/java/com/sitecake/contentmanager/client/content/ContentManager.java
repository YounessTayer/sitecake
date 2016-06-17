package com.sitecake.contentmanager.client.content;

import java.util.List;
import java.util.Map;

public interface ContentManager {
	public void save(Map<String, String> content);
	public void publish(List<String> containers);
	
	/**
	 * Aborts all ongoing network activities.
	 */
	public void abortAll();
	
	/**
	 * Returns the status of the manager.
	 * 
	 * @return true if there are still some unfinished tasks.
	 */
	public boolean isActive();
}
