package com.sitecake.contentmanager.client.editable;

import com.google.gwt.dom.client.Node;

public interface Selection {

	public void setTopNode(Node topNode);

	/**
	 * Returns the current user selection's range.
	 * 
	 * @return the first range or <code>null</code> if the selection
	 * 			is empty
	 */
	public Range getRange();

	public void setRange(Range range);

	public void update();

	/**
	 * Selects the given node.
	 *  
	 * @param node a node to be selected
	 */
	public void select(Node node);

	/**
	 * Set the cursor (collapsed selection) at the start into the given DOM object.
	 * 
	 * @param node
	 */
	public void setCursorInto(Node node);

	/**
	 * Set the cursor (collapsed selection) right after the given DOM object.
	 * 
	 * @param node
	 */
	public void setCursorAfter(Node node);

	public void applyMarkup(Node markup);

	public boolean applyMarkup(Markup markup);

	public void removeSelectedMarkup();
	
	public void removeSelectedMarkup(int rangeCleanUpOption);

	public void executeEnterKey();

}