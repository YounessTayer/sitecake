package com.sitecake.contentmanager.client.toolbar;

/**
 * This interface is used by the toolbar link editor to
 * notify the currently-edited item about user actions.
 */
public interface LinkableItem {
	
	/**
	 * User triggered the linking action.
	 * @param url the actual URL value
	 * @param confiremd signals if the URL value is confiremd/completed
	 */
	public void link(String url, boolean confiremd);
	
	/**
	 * User triggered the unlink action.
	 */
	public void unlink();

}