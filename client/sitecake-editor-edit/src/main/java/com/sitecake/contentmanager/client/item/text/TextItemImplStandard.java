package com.sitecake.contentmanager.client.item.text;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.editable.Range;
import com.sitecake.contentmanager.client.editable.Selection;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public class TextItemImplStandard extends TextItem {

	private DomSelector domSelector = GinInjector.instance.getDomSelector();
	
	private Selection selection = GinInjector.instance.getSelection();
	
	TextItemImplStandard() {
		super();
	}
	
	TextItemImplStandard(Element element) {
		super(element);
	}

	public TextItemImplStandard(String htmlText, Type type, String style, boolean defaultContent) {
		super(htmlText, type, style, defaultContent);
	}

	public TextItemImplStandard(String htmlText, Type type, String style) {
		super(htmlText, type, style);
	}

	@Override
	public ContentItem cloneItem() {
		TextItemImplStandard clone = new TextItemImplStandard(htmlText, type, style);
		
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
		element.onpaste = function(e) {
			self.@com.sitecake.contentmanager.client.item.text.TextItemImplStandard::onPaste()();
		};
	}-*/;
	
	protected void onPaste() {		
		// save the position of the paste carret
		final Range originalRange = selection.getRange().clone();
		
		// create a temp non-visible div elemement that will
		// receive the pasted html
		final Element pasteContainer = DOM.createDiv();
		RootPanel.getBodyElement().appendChild(pasteContainer);
		
		// make the div virtually invisible
		pasteContainer.getStyle().setPosition(Position.ABSOLUTE);
		pasteContainer.getStyle().setWidth(1, Unit.PX);
		pasteContainer.getStyle().setHeight(1, Unit.PX);
		pasteContainer.getStyle().setOverflow(Overflow.HIDDEN);
		pasteContainer.getStyle().setLeft(-20000, Unit.PX);
		
		// adjust the vertical position of the div to fall within the vertical visible
		// area of the window
		pasteContainer.getStyle().setTop(getElement().getAbsoluteTop(), Unit.PX);
		
		// add a dummy content just to be able to set the current inside of the element
		pasteContainer.setInnerHTML("&nbsp;");
		
		// assign a class name so the element could be found when the paste post-processing takes
		// place
		pasteContainer.setClassName(EditorClientBundle.INSTANCE.css().pasteContainer());
		
		// make it content-editable, so the browser actually can place something in the div
		pasteContainer.setAttribute("contenteditable", "true");
		
		// set the focus on the div to ensure the paste action succeed
		pasteContainer.focus();

		// set the caret inside of the temp div
		Range range = new Range(pasteContainer, 0, pasteContainer, 0);
		range.select();
		
		// let the browser to perform the actual paste action
		// after that, continue the processing of the pasted text
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				pastePostProcess(originalRange);
			}
		});

	}
	
	private void pastePostProcess(Range originalRange) {
		// WebKit splits the target content-editable div into multiple ones 
		// but keeps the original div's attributes (class, style, etc.)
		// so, find them all to extract the pasted text
		JsArray<Element> divs = domSelector.select("div." + EditorClientBundle.INSTANCE.css().pasteContainer());
		
		String html = "";
		for ( int i=0; i<divs.length(); i++ ) {
			Element div = divs.get(i);
			div.setAttribute("contenteditable", "false");
			// extract the pasted text fragment
			html += div.getInnerHTML();
			// and remove the temp div
			div.removeFromParent();
		}

		// purify the pasted text to remove all unsupported styling
		// and tags
		html = processPastedText(html, true);
		
		// return the focus on the edited element
		getElement().focus();
		
		// insert the processed pasted text at the position
		// of the original selection
		originalRange.select();
		selection.removeSelectedMarkup(0);
		selection.getRange().insertHtml(html);
	}

}
