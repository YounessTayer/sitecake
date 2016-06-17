package com.sitecake.contentmanager.client.item.text;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.JavaScriptRegExp;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.editable.EditableDomUtils;
import com.sitecake.contentmanager.client.editable.Markup;
import com.sitecake.contentmanager.client.editable.Range;
import com.sitecake.contentmanager.client.editable.Selection;
import com.sitecake.contentmanager.client.event.LinkEditorEvent;
import com.sitecake.contentmanager.client.event.MergeParagraphEvent;
import com.sitecake.contentmanager.client.event.NewParagraphEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.toolbar.EditableTextItem;

public class TextItem extends ContentItem implements EditableTextItem {

	public enum Type {
		P, H1, H2, H3, H4, H5, H6
	}
	
	public static final Type DEFAULT_TYPE = Type.P;
	
	protected Type type;
	
	protected String htmlText;
	
	protected boolean dirty = false;
	
	protected boolean defaultContent;
	
	protected Selection selection = GinInjector.instance.getSelection();
	
	protected DomSelector domSelector = GinInjector.instance.getDomSelector();
	
	protected EditableDomUtils domUtil = GinInjector.instance.getEditableDomUtils();
	
	protected boolean linking;
	
	protected Element linkElement;
	
	protected String linkUrlBackup;
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	
	public static final boolean DEFAULT_BEHAVIOUR_SHIFT_ENTER_KEY = true;
	public static final boolean DEFAULT_BEHAVIOUR_ENTER_KEY = true;
	
	private boolean behaviourEnterKey = DEFAULT_BEHAVIOUR_ENTER_KEY;
	
	private boolean behaviourShiftEnterKey = DEFAULT_BEHAVIOUR_SHIFT_ENTER_KEY;
	
	private List<HandlerRegistration> handlerRegistrations = new ArrayList<HandlerRegistration>();
	
	public Type getType() {
		return type;
	}

	@Override
	public boolean intiateEditUponCreation() {
		return true;
	}

	@Override
	public String getContentTypeName() {
		String contentTypeName = null;
		
		switch ( type ) {
		case P: 
			contentTypeName = "TEXT"; 
			break;
		case H1: 
			contentTypeName = "HEADING1"; 
			break;
		case H2: 
			contentTypeName = "HEADING2"; 
			break;
		case H3: 
			contentTypeName = "HEADING3"; 
			break;
		case H4: 
			contentTypeName = "HEADING4"; 
			break;
		case H5: 
			contentTypeName = "HEADING5"; 
			break;
		case H6: 
			contentTypeName = "HEADING6"; 
			break;
		}
		
		return contentTypeName;
	}
	
	@Override
	public String getItemSelector() {
		return type.toString().toLowerCase();
	}

	@Override
	public void setStyle(String style) {
		if ( !this.style.equals(style) ) {
			dirty = true;
		}
		super.setStyle(style);
	}

	public void setType(Type type) {
		if ( !this.type.equals(type) ) {
			
			boolean isEdited = edited;
			
			if ( isEdited ) {
				stopEditing(false);
			}
			
			this.type = type;
			Element newElement = createElement(type);
			newElement.setInnerHTML(getElement().getInnerHTML());
			replaceElement(newElement);

			if ( isEdited ) {
				startEditing(null);
				dirty = true;
			}
		}
	}

	public String getHtmlText() {
		return htmlText;
	}

	public void updateHtmlText() {
		htmlText = getElement().getInnerHTML();
	}
	
	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
		getElement().setInnerHTML(htmlText);
	}

	public static TextItem create(String htmlText, Type type, String style, boolean defaultContent) {
		TextItem textItem = GWT.create(TextItem.class);
		textItem.init(htmlText, type, style, defaultContent);
		return textItem;
	}
	
	TextItem() {
		super();
	}
	
	TextItem(Element element) {
		super();
		init(element, true);
	}
	
	TextItem(String htmlText, Type type, String style) {
		super();
		init(htmlText, type, style, false);
	}

	TextItem(String htmlText, Type type, String style, boolean defaultContent) {
		super();
		init(htmlText, type, style, defaultContent);
	}
	
	void init(String htmlText, Type type, String style, boolean defaultContent) {
		this.defaultContent = defaultContent;
		Element element = createElement(type);
		element.setInnerHTML(htmlText);
		if ( !element.getLastChild().getNodeName().equalsIgnoreCase("br") ) {
			element.appendChild(DOM.createElement("br"));
		}
		
		if ( style != null && !style.equals("") ) {
			element.addClassName(style);
		}
		init(element, false);
	}
	
	void init(Element element) {
		init(element, true);
	}
	
	void init(Element element, boolean fromOrigElement) {
		htmlText = processPastedText(element.getInnerHTML(), !fromOrigElement);
		type = extractType(element);
		style = element.getClassName(); 
		element.setInnerHTML(htmlText);
		if ( !element.hasChildNodes() || !element.getLastChild().getNodeName().equalsIgnoreCase("br") ) {
			element.appendChild(DOM.createElement("br"));
			htmlText = element.getInnerHTML();
		}
		setElement(element);
	}
	
	@Override
	public ContentItem cloneItem() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void startEditing(String mode) {
		super.startEditing(mode);
		
		selection.setTopNode(getElement());
		getElement().setAttribute("contentEditable", "true");
		
		HandlerRegistration keyDownHandlerRegistration = addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				onKeyDownEvent(event);
			}
		});
		handlerRegistrations.add(keyDownHandlerRegistration);
		
		HandlerRegistration keyUpHandlerRegistration = addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				onKeyUpEvent(event);
			}
		});
		handlerRegistrations.add(keyUpHandlerRegistration);		
		
		HandlerRegistration mouseDownHandlerRegistration = addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				onMouseDownEvent(event);
			}
		});
		handlerRegistrations.add(mouseDownHandlerRegistration);

		HandlerRegistration mouseUpHandlerRegistration = addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				onMouseUpEvent(event);
			}
		});
		handlerRegistrations.add(mouseUpHandlerRegistration);

		if ( defaultContent ) {
			selection.select(getElement());
		}
		
		setupPasteHandler();
		
		getElement().focus();

		if ( !defaultContent ) {
			selection.setCursorInto(getElement());
		}
		
		defaultContent = false;
		dirty = false;
	}

	@Override
	public boolean stopEditing(boolean cancel) {
		if ( linking = true && linkUrlBackup != null ) {
			link(linkUrlBackup, true);
		}
		
		getElement().setAttribute("contentEditable", "false");
		selection.setTopNode(null);
		
		for ( HandlerRegistration handlerRegistration : handlerRegistrations ) {
			handlerRegistration.removeHandler();
		}
		handlerRegistrations.clear();
		
		removePasteHandler();
		getElement().blur();
		
		super.stopEditing(cancel);
		
		boolean result = false;
		
		if ( dirty ) {
			result = true;
			htmlText = getElement().getInnerHTML();
		} else if ( cancel ) {
			result = false;
		} else if ( !htmlText.equals(getElement().getInnerHTML()) ) {
			result = true;
			htmlText = getElement().getInnerHTML();
		}

		return result;
	}
	
	@Override
	public String getHtml() {
		String tagName = getElement().getTagName();
		Element tmp = DOM.createDiv();
		tmp.setInnerHTML(htmlText);
		if ( tmp.getLastChild().getNodeName().equalsIgnoreCase("br") ) {
			tmp.getLastChild().removeFromParent();
		}
		return "<" + tagName + " " +
				(!"".equals(style) ? "class=\"" + style + "\" ": "") + 
				">" + tmp.getInnerHTML() + "</" + tagName + ">";
	}

	public Node appendHtml(String html) {
		Node splicePoint = null;
		
		if ( getElement().hasChildNodes() ) {
			Node lastNode = getElement().getLastChild();
			if ( lastNode.getNodeName().equalsIgnoreCase("br") ) {
				lastNode.removeFromParent();
			}
					
			Element newContentContainer = DOM.createDiv();
			newContentContainer.setInnerHTML(html);
			splicePoint = newContentContainer.getFirstChild();

			JsArray<Node> newContent = domUtil.children(newContentContainer);
			for ( int i = 0; i < newContent.length(); i++ ) {
				getElement().appendChild(newContent.get(i));
			}
		} else {
			getElement().setInnerHTML(html);
			splicePoint = getElement();
		}
		updateHtmlText();
		return splicePoint;
	}
	
	protected void setupPasteHandler() {
		throw new UnsupportedOperationException();
	}
	
	protected void removePasteHandler() {
		throw new UnsupportedOperationException();
	}
	
	// TODO: use external paste processor and use it only upon a real paste event
	protected String processPastedText(String rawText, boolean convertNL) {
		String output = rawText;
		
		boolean msoContent = false;
		// detect MS Office content
		if ( JavaScriptRegExp.test("class=\"?Mso|style=\"[^\"]*\\bmso-|w:WordDocument", "i", output) ) {
			msoContent = true;
		}
		
		// strip xml comment (e.g. MSO style def)
		output = JavaScriptRegExp.replace(output, "<!--[\\s\\S]*?-->", "g", "");

		if ( msoContent ) {
			// remove all crlf
			output = JavaScriptRegExp.replace(output, "\r?\n", "g", "");
			// as all tags (except <a> and <br> are to be removed), save at least some basic
			// text formating by inserting a <br> after each <p> and <h1-6> tag
			output = JavaScriptRegExp.replace(output, "(/p>|/h[1-6]>)", "gi", " $1<br/>");
		}
		
		// strip all but <a>, <strong>, <em> and <br> tags
		output = JavaScriptRegExp.replace(output, "<(?!(a |\\/a|br|\\/br|strong |strong|\\/strong|em |em|\\/em))[^>]+>", "gi", "");
		
		// strip class names
		output = JavaScriptRegExp.replace(output, " class=\"([^\"]+)\"", "gi", "");
		output = JavaScriptRegExp.replace(output, " class=(\\w+)", "gi", "");
		
		// strip style
		output = JavaScriptRegExp.replace(output, " style=\"([^\"]+)\"", "gi", "");

		if ( !msoContent && convertNL) {
			// replace crlf with breaks
			output = JavaScriptRegExp.replace(output, "\r?\n", "g", "<br/>");
		}
		
		return output;
	};
	
	protected Type extractType(Element element) {
		return Type.valueOf(element.getTagName().toUpperCase());
	}
	
	protected Element createElement(Type type) {
		return DOM.createElement(type.name());
	}
	
	protected void onMouseDownEvent(MouseDownEvent event) {
		if ( linking && linkUrlBackup != null ) {
			link(linkUrlBackup, true);
		} else {
			linking = false;
			linkElement = null;
			linkUrlBackup = null;
		}
	}
	
	protected void onMouseUpEvent(MouseUpEvent event) {
		(new Timer() {
			@Override
			public void run() {
				selectionHandler();
			}
		}).schedule(10);
	}
	
	protected void onKeyDownEvent(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		boolean shiftKey = event.isShiftKeyDown();

		boolean sink = false;
		
		switch ( keyCode ) {
			case KeyCodes.KEY_ENTER:
				if ( shiftKey && behaviourShiftEnterKey ) {
					executeShiftEnterKey();
				} else if ( !shiftKey && behaviourEnterKey ) {
					executeEnterKey();
				}
				sink = true;
				break;

			case KeyCodes.KEY_BACKSPACE:
				executeBackspaceKey();
				break;
				
			case KeyCodes.KEY_DELETE:
				executeDeleteKey();
				break;
		}
		
		if ( sink ) {
			event.preventDefault();
		}
		event.stopPropagation();		
	}
	
	protected void onKeyUpEvent(KeyUpEvent event) {
		int keyCode = event.getNativeKeyCode();
		
		
		if ( keyCode == KeyCodes.KEY_ENTER ) return;
		
		(new Timer() {
			@Override
			public void run() {
				selectionHandler();
			}
		}).schedule(10);
	}
	
	protected void executeShiftEnterKey() {
		Range range = selection.getRange();
		if ( range == null ) return;
		
		range.insertHtmlBreak();
	}
	
	protected void executeEnterKey() {
		String html = "";
		
		Range range = selection.getRange();
		if ( !range.isCollapsed() ) {
			selection.removeSelectedMarkup();
		}
		range = selection.getRange();
		if ( getElement().hasChildNodes() ) {
			Node endContainer = getElement();
			range.setEndContainer(endContainer);
			int endOffset = endContainer.getChildCount();
			range.setEndOffset(endOffset);
			range.normalize();
			selection.setRange(range);
			range.select();
			
			Element extractMarkup = DOM.createSpan();
			extractMarkup.setClassName("sc-extract-markup");
			range.addMarkup(extractMarkup, false);
			
			JsArray<Element> wrappers = domSelector.select(".sc-extract-markup", getElement());
			for ( int i = 0; i < wrappers.length(); i++ ) {
				Element wrapper = wrappers.get(i);
				html += wrapper.getInnerHTML();
				wrapper.removeFromParent();
			}
		}
		
		if ( "".equals(html) ) {
			html = "&nbsp;";
		}
		
		// prevent the current edited element for losing its complete content
		if ( "".equals(getElement().getInnerHTML()) ) {
			getElement().setInnerHTML("&nbsp;<br/>");
		} else {
			if ( !getElement().getLastChild().getNodeName().equalsIgnoreCase("br") ) {
				getElement().appendChild(DOM.createElement("br"));
			}
		}
		
		// trigger the new TextItem creation action
		eventBus.fireEventDeferred(new NewParagraphEvent(html));
	}
	
	protected void executeBackspaceKey() {
		Range range = selection.getRange();
		if ( range != null && range.isCollapsed() &&
				range.getTextBeforeStart().length() == 0 ) {
			eventBus.fireEventDeferred(new MergeParagraphEvent());
		}
	}
	
	protected void executeDeleteKey() {
		
	}
	
	private Markup boldMarkup = new Markup() {
		private Node node = DOM.createElement("strong");
		
		@Override
		public boolean matches(Node aNode) {
			return "strong".equalsIgnoreCase(aNode.getNodeName());
		}
		
		@Override
		public boolean isReplacing() {
			return false;
		}
		
		@Override
		public Node getNode() {
			return node;
		}
	};

	private Markup italicMarkup = new Markup() {
		private Node node = DOM.createElement("em");
		
		@Override
		public boolean matches(Node aNode) {
			return "em".equalsIgnoreCase(aNode.getNodeName());
		}
		
		@Override
		public boolean isReplacing() {
			return false;
		}
		
		@Override
		public Node getNode() {
			return node;
		}
	};

	private Markup linkMarkup = new Markup() {
		private Node node = DOM.createElement("a");
		
		@Override
		public boolean matches(Node aNode) {
			return "a".equalsIgnoreCase(aNode.getNodeName());
		}
		
		@Override
		public boolean isReplacing() {
			return false;
		}
		
		@Override
		public Node getNode() {
			return node;
		}
	};
	
	protected void selectionHandler() {
		Range range = selection.getRange();
		// if, for any reason, there's no a range, cancel the operation
		if ( range == null ) return;
		
		boolean isBold = (range.findMarkup(boldMarkup, true) != null);
		boolean isItalic = (range.findMarkup(italicMarkup, true) != null);
		String linkUrl;
 
		clearMarkedLinks();
		Node link = range.findMarkup(linkMarkup, true);
		if ( link != null ) {
			linkElement = Element.as(link);
			linkUrl = AnchorElement.as(linkElement).getHref();
		} else {
			linkElement = null;
			linkUrl = "";
		}
		
		eventBus.fireEventDeferred(new LinkEditorEvent(linkUrl, false, isBold, isItalic));
	}

	@Override
	public void link(String url, boolean confirmed) {
		linkUrlBackup = url;
		dirty = true;
		boolean focus = false;
		
		if ( !linking && !confirmed ) {
			url = "#";
			confirmed = true;
			focus = true;
		}
		
		linking = true;
		if ( linkElement != null ) {
			linkElement.addClassName("sc-new-link-element");
		}
		
		if ( !confirmed ) {
			return;
		}
		
		getElement().focus();
		Range range = selection.getRange();
		
		if ( linkElement != null ) {
			if ( url == null || "".equals(url) ) {
				selection.select(linkElement);
				range = selection.getRange();
				range.removeMarkup(linkMarkup.getNode());
				linking = false;
				linkElement = null;
				linkUrlBackup = null;
				clearMarkedLinks();
				htmlText = getElement().getInnerHTML();				
			} else if ( !focus ) {
				AnchorElement.as(linkElement).setHref(url);
			}
		} else {
			if ( url != null && !"".equals(url) ) {
				if ( range.isCollapsed() ) {
					range.extendToWord();
				}
				AnchorElement newLink = AnchorElement.as(DOM.createAnchor());
				newLink.setHref(url);
				newLink.setClassName("sc-new-link-element");
				range.addMarkup(newLink, false);
				linkElement = domSelector.select(".sc-new-link-element", getElement()).get(0);
				selection.select(linkElement);
			}
		}
		
		if ( !focus ) {
			linking = false;
			linkElement = null;
			linkUrlBackup = null;
			clearMarkedLinks();
			htmlText = getElement().getInnerHTML();
		}
	}

	@Override
	public void unlink() {
		Range range = selection.getRange();
		if ( range == null ) return;
		
		if ( linkElement != null ) {
			selection.select(linkElement);
			range = selection.getRange();
		} else {
			if ( range.isCollapsed() ) {
				range.extendToWord();
			}
		}
		range.removeMarkup(linkMarkup.getNode());
		
		linking = false;
		linkElement = null;
		htmlText = getElement().getInnerHTML();
		dirty = true;
	}
	
	@Override
	public void bold(boolean isBold) {
		applySimpleMarkup(boldMarkup, isBold);
	}

	@Override
	public void italic(boolean isItalic) {
		applySimpleMarkup(italicMarkup, isItalic);
	}

	private void clearMarkedLinks() {
		JsArray<Element> newLinks = domSelector.select(".sc-new-link-element", getElement());
		for ( int i = 0; i < newLinks.length(); i++ ) {
			Element newLink = newLinks.get(i);
			newLink.removeClassName("sc-new-link-element");
		}		
	}
	
	private void applySimpleMarkup(Markup markup, boolean add) {
		Range range = selection.getRange();
		getElement().focus();
		if ( range == null ) return;

		if ( add ) {
			if ( range.isCollapsed() ) {
				range.extendToWord();
			}
			range.addMarkup(markup.getNode(), false);
		} else {
			if ( range.isCollapsed() ) {
				Node object = range.findMarkup(markup, true);
				if ( object != null ) {
					range.removeFromDOM(object);
				}
			} else {
				range.removeMarkup(markup.getNode());
			}
		}
		range.select();
		
		htmlText = getElement().getInnerHTML();
		dirty = true;
	}
}
