package com.sitecake.contentmanager.client.editable;

import com.google.gwt.core.client.JavaScriptObject;

public final class StealingTags extends JavaScriptObject {

	protected StealingTags() {}
	
	public native static StealingTags create()/*-{
		var text = [ '#text' ];
		var p = [ '#text', 'span', 'a', 'img', 'br', 'b', 'strong', 'i', 'em', 'sup', 'sub', 'del', 'ins', 'u', 'cite', 'q', 'code' ];
		
		var stealingMap = {
			'p' : p, 
			'h1' : text,
			'h2' : text,
			'h3' : text,
			'h4' : text,
			'h5' : text,
			'h6' : text
		};
		
		return stealingMap;
	}-*/;
	
	public native boolean canSteal(String stealingTag, String aTagToSteal)/*-{
		// if nothing is defined for the markup, it's now allowed
		if ( !this[stealingTag] ) {
			return false;
		}
		// if something is defined, but the specifig tag is not in the list
		if ( this[stealingTag].indexOf(aTagToSteal) == -1) {
			return false;
		}
		return true;		
	}-*/;
}
