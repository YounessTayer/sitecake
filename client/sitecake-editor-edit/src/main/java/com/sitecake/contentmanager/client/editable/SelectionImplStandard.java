package com.sitecake.contentmanager.client.editable;

public class SelectionImplStandard extends SelectionImpl {

	public SelectionImplStandard() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see aloha.client.Selection#getRange()
	 */
	@Override
	public Range getRange() {
		return super.getRange();
	}

	/* (non-Javadoc)
	 * @see aloha.client.Selection#getNativeSelection()
	 */
	@Override
	protected native NativeSelection getNativeSelection()/*-{
		return $wnd.getSelection();
	}-*/;	
}
