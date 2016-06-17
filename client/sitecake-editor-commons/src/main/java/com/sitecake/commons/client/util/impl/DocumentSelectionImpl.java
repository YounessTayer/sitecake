package com.sitecake.commons.client.util.impl;

import com.google.gwt.user.client.Element;
import com.sitecake.commons.client.util.DocumentSelection;
import com.sitecake.commons.client.util.SelectionRange;

public abstract class DocumentSelectionImpl implements DocumentSelection {

	@Override
	public abstract void collapseToEnd();

	@Override
	public abstract void collapseToStart();

	@Override
	public abstract boolean isCollapsed();
	
	@Override
	public abstract String getContent();
	
	@Override
	public abstract String getTextContent();
	
	@Override
	public abstract void setContent(String html, boolean moveAfter);
	
	@Override
	public abstract void deleteContent();

	@Override
	public abstract void expandToEnd(Element element, boolean inner);
	
	@Override
	public abstract void expandToStart(Element element, boolean inner);

	@Override
	public abstract void select(Element element, boolean inner);

	@Override
	public abstract void select(Element element, int offset, int length);

	@Override
	public abstract SelectionRange getSelectedRange();

	@Override
	public abstract void remove();

	@Override
	public abstract void select(SelectionRange range);

	@Override
	public abstract int getCaretOffset();
	
	@Override
	public abstract void setCaretOffset(Element element, int offset);

}
