package com.sitecake.contentmanager.client.editable;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class TagWrappingRules extends JavaScriptObject {

	protected TagWrappingRules() {}
	
	public static native TagWrappingRules create()/*-{
		var none = [];
		var span = [ '#text', 'span', 'a', 'img', 'br', 'b', 'strong', 'i', 'em', 'sup', 'sub', 'del', 'ins', 'u', 'cite', 'q', 'code' ];
		var h = [ '#text', 'br', 'a', 'span', 'img', 'b', 'strong', 'i', 'em', 'sup', 'sub', 'del', 'ins', 'u' ];
		var li = [ '#text', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'br', 'span', 'img', 'b', 'i', 'em', 'sup', 'sub', 'del', 'ins', 'u' ];
		var pre = [ '#text', 'br', 'span', 'a', 'img', 'b', 'strong', 'i', 'em', 'sup', 'sub', 'del', 'ins', 'u', 'cite', 'q', 'code' ];
		var ins = [ '#text', 'p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'br', 'span', 'a', 'img', 'b', 'strong', 'i', 'em', 'sup', 'sub', 'u' ];
		var div = ['#text', 'div', 'br', 'span', 'img', 'p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'table', 'b', 'strong', 'i', 'em', 'sup', 'sub', 'del', 'ins', 'u', 'pre', 'blockquote'];

		var wrappingMap = {
			'#text' : none,
			'div' : div,
			'span' : span,
			'a' : span,
			'br' : none,
			'img' : none,
			'b' : span,
			'strong' : span,
			'i' : span,
			'em' : span,
			'code' : span,
			'blockquote' : ins,
			'cite' : span,
			'sup' : span,
			'sub' : span,
			'pre' : pre,
			'ins' : ins,
			'del' : ins,
			'u' : span,
			'p' : span,
			'h1' : h,
			'h2' : h,
			'h3' : h,
			'h4' : h,
			'h5' : h,
			'h6' : h,
			'ul' : [ 'li' ],
			'ol' : [ 'li' ],
			'li' : li,
			'table' : [ 'tr' ],
			'tr' : [ 'th', 'td' ],
			'td' : li
		}
			
		return wrappingMap;
	}-*/;
	
	public native JsArrayString get(String tag)/*-{
		return this[tag] ? this[tag] : null;
	}-*/;
	
	public native boolean canWrapp(String wrappingTag, String wrappedTag)/*-{
		if ( this[wrappingTag] && this[wrappingTag].indexOf(wrappedTag) == -1 ) {
			return false;
		} else {
			return true;
		}
	}-*/;
}
