package com.sitecake.contentmanager.client.editable;

import java.util.List;

import com.google.gwt.dom.client.Node;

public class RangeTreeNode {
	public enum Type {
		NONE,
		PARTIAL,
		FULL,
		COLLAPSED
	}
	
	public Node node;
	public Type type;
	public List<RangeTreeNode> children;
	public int startOffset;
	public int endOffset;
	
	public boolean wrappable;
}
