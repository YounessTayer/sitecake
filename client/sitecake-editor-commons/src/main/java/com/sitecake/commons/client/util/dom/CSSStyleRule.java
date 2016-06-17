package com.sitecake.commons.client.util.dom;

import com.google.gwt.core.client.JavaScriptObject;

public class CSSStyleRule extends JavaScriptObject {
	
	public final static int UNKNOWN_RULE = 0;
	public final static int STYLE_RULE = 1;
	public final static int CHARSET_RULE = 2;
	public final static int IMPORT_RULE = 3;
	public final static int MEDIA_RULE = 4;
	public final static int FONT_FACE_RULE = 5;
	public final static int PAGE_RULE = 6;
	public final static int MOZ_KEYFRAMES_RULE = 7;
	public final static int MOZ_KEYFRAME_RULE = 8;
	
	protected CSSStyleRule() {}
	
	public final native int type()/*-{
		return this.type;
	}-*/;
	
	public final native String selectorText()/*-{
		return this.selectorText;
	}-*/;
	
	public final native String cssText()/*-{
		return this.cssText;
	}-*/;
	
	public final native CSSStyleRule parentRule()/*-{
		return this.parentRule;
	}-*/;
	
	public final native StyleSheet parentStyleSheet()/*-{
		return this.parentStyleSheet;
	}-*/;
	
	public final native CSSStyleDeclaration style()/*-{
		return this.style;
	}-*/;
	
}
