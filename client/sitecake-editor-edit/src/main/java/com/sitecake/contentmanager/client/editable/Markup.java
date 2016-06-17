package com.sitecake.contentmanager.client.editable;

import com.google.gwt.dom.client.Node;

public interface Markup {
	public Node getNode();
	public boolean matches(Node aNode);
	public boolean isReplacing();
}
