package com.sitecake.contentmanager.client.select;

public interface SelectorHandler {
	public void onSelectStart(SelectContext context) throws VetoSelectException;
	public void onSelectDrag(SelectContext context);
	public void onSelectEnd(SelectContext context);
}
