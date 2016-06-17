package com.sitecake.commons.client.util;

import com.google.gwt.user.client.Element;

public interface DocumentSelection {

	/**
	 * Removes any existing selection.
	 */
	void remove();
	
	/**
	 * Returns the content of the current selection.
	 * 
	 * @return the html text of the current selection. An empty string in case the selection is collapsed or there is no selection at all
	 */
	String getContent();

	/**
	 * Returns the text content of the current selection.
	 * 
	 * @return the text of the current selection. An empty string in case the selection is collapsed or there is no selection at all
	 */
	String getTextContent();
	
	/**
	 * Sets the given html text into the current selection, replacing the existing content.
	 * 
	 * @param text html text to be set
	 * @param moveAfter signals whether the caret should be moved just after the content
	 */
	void setContent(String html, boolean moveAfter);

	/**
	 * Deletes the content of the current selection.
	 */
	void deleteContent();
	
	/**
	 * Checks if the current selection is collapsed.
	 * 
	 * @return false if the selection is present and not empty
	 */
	boolean isCollapsed();
	
	/**
	 * Collapses the current selection to start/left.
	 */
	void collapseToStart();
	
	/**
	 * Collapses the current selection to end/right.
	 */
	void collapseToEnd();
	
	/**
	 * Returns a range that represents the current selection.
	 * 
	 * @return range
	 */
	SelectionRange getSelectedRange();
	
	/**
	 * Expands the current selection to the start position of the given element.
	 * 
	 * @param element
	 * @param inner signals whether the selection should start just before the element or just before its first child node 
	 */
	void expandToStart(Element element, boolean inner);
	
	/**
	 * Expands the current selection to the end position of the given element.
	 * 
	 * @param element
	 * @param inner signals whether the selection should end just after the element or just after its last child node 
	 */
	void expandToEnd(Element element, boolean inner);

	/**
	 * Sets the selection over the given element.
	 * 
	 * @param element
	 * @param inner signals whether the selection should wrap the element itself or just its child nodes
	 */
	void select(Element element, boolean inner);
	
	/**
	 * Sets the selection over the given range.
	 * 
	 * @param range
	 */
	void select(SelectionRange range);
	
	/**
	 * Sets the selection in the given element, using the given offset from the element start and
	 * the given length.
	 * 
	 * @param element
	 * @param offset
	 * @param length
	 */
	void select(Element element, int offset, int length);
	
	int getCaretOffset();
	
	void setCaretOffset(Element element, int offset);
}
