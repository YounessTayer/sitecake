package com.sitecake.contentmanager.client.toolbar;

public interface EditableTextItem extends LinkableItem {
	
	/**
	 * Sets or unsets the bold-ness of the selected text. 
	 * @param isBold specifies if the text should be bold or not
	 */
	public void bold(boolean isBold);

	/**
	 * Sets or unsets the italic-ness of the selected text. 
	 * @param isItalic specifies if the text should be italic or not
	 */
	public void italic(boolean isItalic);
}
