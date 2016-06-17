package com.sitecake.contentmanager.client.editable;

public class SelectionImplIE extends SelectionImpl {

	public SelectionImplIE() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see aloha.client.Selection#getRange()
	 */
	@Override
	public Range getRange() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see aloha.client.Selection#getNativeSelection()
	 */
	@Override
	protected native NativeSelection getNativeSelection()/*-{
		return $wnd.getSelection();
	}-*/;

	
}
