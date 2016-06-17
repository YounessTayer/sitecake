package com.sitecake.contentmanager.client.item.text;

import com.google.gwt.dom.client.Element;
import com.sitecake.contentmanager.client.item.ContentItem;

public class TextItemImplIE extends TextItem {

	TextItemImplIE() {
		super();
	}
	
	TextItemImplIE(Element element) {
		super(element);
	}

	public TextItemImplIE(String htmlText, Type type, String style, boolean defaultContent) {
		super(htmlText, type, style, defaultContent);
	}

	public TextItemImplIE(String htmlText, Type type, String style) {
		super(htmlText, type, style);
	}

	@Override
	public ContentItem cloneItem() {
		TextItemImplIE clone = new TextItemImplIE(htmlText, type, style);
		
		return super.cloneItem(clone);
	}
	
	@Override
	protected native void removePasteHandler()/*-{
		var element = this.@com.google.gwt.user.client.ui.Widget::getElement()();
		element.onpaste = null;
	}-*/;

	@Override
	protected native void setupPasteHandler()/*-{
		var element = this.@com.google.gwt.user.client.ui.Widget::getElement()();
		var self = this;
		element.onpaste = function() {
			var clipboardText = $wnd.clipboardData.getData('Text');
			clipboardText = self.@com.sitecake.contentmanager.client.item.text.TextItemImplIE::onPaste(Ljava/lang/String;)(clipboardText); 
			$wnd.clipboardData.setData('Text', clipboardText);
		};
	}-*/;
	
	protected String onPaste(String text) {
		return processPastedText(text, true);
	}	
}
