package com.sitecake.commons.client.util;

import com.google.gwt.dom.client.Node;

public class SelectionEndPoint {
	private Node node;
	private int offset;
	
	public Node getNode() {
		return node;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public SelectionEndPoint(Node node, int offset) {
		super();
		this.node = node;
		this.offset = offset;
	}
	
}
