package com.sitecake.commons.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.RootPanel;
import com.sitecake.commons.client.util.dom.CSSStyleDeclaration;


public class DomUtil {
	public static boolean hasClassName(Element element, String className) {
		if ( "".equals(element.getClassName()) ) {
			return false;
		} else {
			return JavaScriptRegExp.test("(^|.*\\s)" + className + "(\\s.*|$)", element.getClassName());
		}
	}
	
	public static void hideElement(Element element) {
		element.getStyle().setDisplay(Display.NONE);
	}
	
	public static void showElement(Element element) {
		element.getStyle().setDisplay(Display.BLOCK);
	}

	public static native void replaceElement(Element node, Element newNode) /*-{
		var p = node.parentNode;
		if (!p) {
			return;
		}
		p.insertBefore(newNode, node);
		p.removeChild(node);
	}-*/;

	private static native void execCommandNative(String cmd, String param)/*-{
		$doc.execCommand(cmd, false, param);
	}-*/;
	
	public static void execCommand(String cmd, String param) {
		try {
			execCommandNative(cmd, param);
		} catch (JavaScriptException e) {}
	}

	public static boolean queryCommandState(String cmd) {
		try {
			return queryCommandStateNative(cmd);
		} catch (JavaScriptException e) { 
			return false;
		}
	}

	private static native boolean queryCommandStateNative(String cmd) /*-{
    	return !!$doc.queryCommandState(cmd);
	}-*/;
	
	public static double getElementInnerWidth(Element element) {
		if (element == null) return 0;
		
		double width,
				clientWidth = element.getClientWidth(), // width + padding-left + padding-right
				paddingLeft = 0,
				paddingRight = 0;
		
		try {
			paddingLeft = CSSStyleDeclaration.get(element).getPropertyValueDouble("padding-left");
		} catch (Exception e) {
			paddingLeft = 0;
		}
		
		try {
			paddingRight = CSSStyleDeclaration.get(element).getPropertyValueDouble("padding-right");
		} catch (Exception e) {
			paddingRight = 0;
		}
		
		width = clientWidth - paddingLeft - paddingRight;
		
		return (width < 0) ? 0 : width;
	}
	
	public static double getElementInnerHeight(Element element) {
		if (element == null) return 0;
		
		double height,
				clientHeight = element.getClientHeight(), // height + padding-top + padding-bottom
				paddingTop = 0,
				paddingBottom = 0;
		
		try {
			paddingTop = CSSStyleDeclaration.get(element).getPropertyValueDouble("padding-top");
		} catch (Exception e) {
			paddingTop = 0;
		}
		
		try {
			paddingBottom = CSSStyleDeclaration.get(element).getPropertyValueDouble("padding-bottom");
		} catch (Exception e) {
			paddingBottom = 0;
		}
		
		height = clientHeight - paddingTop - paddingBottom;
		
		return (height < 0) ? 0 : height;
	}
	
	public static double getElementOuterWidth(Element element) {
		return (element == null) ? 0 : element.getOffsetWidth();
	}
	
	public static double getElementOuterHeight(Element element) {
		return (element == null) ? 0 : element.getOffsetHeight();
	}
	
	public static native String outerHtml(Element element)/*-{
		var txt, ax, el = $doc.createElement("div");
		el.appendChild(element.cloneNode(false));
		txt = el.innerHTML;      
		ax = txt.indexOf('>') + 1;
		txt = txt.substring(0, ax) + element.innerHTML + txt.substring(ax);
		el = null;
		return txt;	
	}-*/;
	
	/**
	 * Returns a list of parent nodes for the given node up to
	 * (but not included) the given <code>body</code> tag. The list 
	 * starts with the inner-most parent.
	 * 
	 * @param node a node which parents will be returned
	 * @return a list of parent nodes
	 */	
	public static List<Node> getParents(Node node) {
		return getParents(node, RootPanel.getBodyElement());
	}
	
	/**
	 * Returns a list of parent nodes for the given node up to
	 * (but not included) the given <code>topLimit</code> node.
	 * The list starts with the inner-most parent.
	 * 
	 * @param node a node which parents will be returned
	 * @param topLimit a node that acts as the parent list terminator
	 * @return a list of parent nodes
	 */
	public static List<Node> getParents(Node node, Node topLimit) {
		List<Node> parents = new ArrayList<Node>();
		
		Node parent = node.getParentNode();
		while ( parent != null && !parent.equals(topLimit) ) {
			parents.add(parent);
			parent = parent.getParentNode();
		}
		
		return parents;
	}
	
}
