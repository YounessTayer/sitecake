package com.sitecake.contentmanager.client.item.list;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
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
import com.sitecake.contentmanager.client.editable.Markup;
import com.sitecake.contentmanager.client.editable.Range;
import com.sitecake.contentmanager.client.editable.Selection;
import com.sitecake.contentmanager.client.event.LinkEditorEvent;
import com.sitecake.contentmanager.client.item.ContentEditableBaseItem;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.toolbar.EditableTextItem;

public abstract class ListItem extends ContentEditableBaseItem implements EditableTextItem {
	
	public static final String CONTENT_TYPE_NAME = "LIST";
	
	protected DomSelector domSelector = GinInjector.instance.getDomSelector();
	
	protected String htmlText;
	
	protected boolean dirty = false;
	
	protected boolean defaultContent;
	
	protected boolean linking;
	
	protected Element linkElement;
	
	protected String linkUrlBackup;
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	
	private List<HandlerRegistration> handlerRegistrations = new ArrayList<HandlerRegistration>();
	
	protected Selection selection = GinInjector.instance.getSelection();
	
	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}
	
	@Override
	public String getItemSelector() {
		return "ul";
	}

	public String getHtmlText() {
		return htmlText;
	}

	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
		getElement().setInnerHTML(htmlText);
	}

	public static ListItem create(String htmlText, String style, boolean defaultContent) {
		ListItem item = GWT.create(ListItem.class);
		item.init(htmlText, style, defaultContent);
		return item;
	}
	
	ListItem() {
		super();
	}
	
	ListItem(Element element) {
		super();
		init(element);
	}
	
	ListItem(String htmlText, String style) {
		super();
		init(htmlText, style, false);
	}

	ListItem(String htmlText, String style, boolean defaultContent) {
		super();
		init(htmlText, style, defaultContent);
	}
	
	void init(String htmlText, String style, boolean defaultContent) {
		this.defaultContent = defaultContent;
		Element element = DOM.createElement("ul");
		element.setInnerHTML(htmlText);
		
		if ( style != null && !style.equals("") ) {
			element.addClassName(style);
		}
		init(element);
	}
	
	void init(Element element) {
		htmlText = element.getInnerHTML();
		style = element.getClassName(); 
		element.setInnerHTML(htmlText);
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
			@Override
			public void onKeyDown(KeyDownEvent event) {
				onKeyDownEvent(event);
			}
		});
		handlerRegistrations.add(keyDownHandlerRegistration);
		
		HandlerRegistration keyPressHandlerRegistration = addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				onKeyPressEvent(event);
			}
		});
		handlerRegistrations.add(keyPressHandlerRegistration);
		
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
			if (getElement().hasChildNodes())
				selection.select(getElement().getFirstChild());
		}
		
		setupPasteHandler();
		
		getElement().focus();

		if ( !defaultContent ) {
			if (getElement().hasChildNodes())
				selection.select(getElement().getFirstChild());
		}
		
		defaultContent = false;		
	}

	@Override
	public boolean stopEditing(boolean cancel) {
		
		if (linking) {
			link(linkUrlBackup, true);
		}
		
		getElement().setAttribute("contentEditable", "false");
		
		for ( HandlerRegistration handlerRegistration : handlerRegistrations ) {
			handlerRegistration.removeHandler();
		}
		handlerRegistrations.clear();
		
		removePasteHandler();
		
		super.stopEditing(cancel);
		
		// sanitize the inner HTML
		String sanitizedHtml = getSanitizedHtml();
		if ( "".equals(sanitizedHtml) ) {
			getElement().setInnerHTML("<li>&nbsp;</li>");
		} else {
			getElement().setInnerHTML(sanitizedHtml);
		}
		
		boolean result = dirty;
		
		if ( cancel ) {
			dirty = false;
			result = false;
		} else {
			if ( !htmlText.equals(getElement().getInnerHTML()) ) {
				result = true;
				htmlText = getElement().getInnerHTML();
			}
		}

		return result;
	}
	
	@Override
	public String getHtml() {
		String html = getSanitizedHtml();
		
		// if the sanitized HTML is an empty string, than something
		// went wrong and the element should not be stored/saved.
		if ( !"".equals(html) ) {
			html = 
				"<ul " + (( style != null && !style.equals("") ) ? "class=\"" + style + "\">" : ">") +
				html + 
				"</ul>";
		}
		
		return html;
	}

	/**
	 * Processes the inner HTML of the UL element and strips all
	 * leftovers of the browser editing action (contenteditable mode).
	 *  
	 * @return sanitized html text
	 */
	private String getSanitizedHtml() {
		String html = "";
		
		NodeList<Node> childNodes = getElement().getChildNodes();
		for ( int i = 0; i < childNodes.getLength(); i++ ) {
			if ( Element.is(childNodes.getItem(i)) ) {
				Element childElement = Element.as(childNodes.getItem(i));
				if ( "li".equalsIgnoreCase(childElement.getTagName()) ) {
					html += "<li>" + childElement.getInnerHTML() + "</li>";
				}
			}
		}
		
		return html;
	}
	
	protected abstract void setupPasteHandler();
	
	protected abstract void removePasteHandler();
	
	protected String processPastedText(String rawText) {
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
		
		// strip all but <a> and <br> tags
		output = JavaScriptRegExp.replace(output, "<(?!(a |\\/a|br|\\/br))[^>]+>", "gi", "");
		
		// strip class names
		output = JavaScriptRegExp.replace(output, " class=\"([^\"]+)\"", "gi", "");
		output = JavaScriptRegExp.replace(output, " class=(\\w+)", "gi", "");
		
		// strip style
		output = JavaScriptRegExp.replace(output, " style=\"([^\"]+)\"", "gi", "");

		if ( !msoContent ) {
			// replace crlf with breaks
			output = JavaScriptRegExp.replace(output, "\r?\n", "g", "<br/>");
		}
		
		return output;
	};
	
	protected void onMouseDownEvent(MouseDownEvent event) {
		
	}
	
	protected void onMouseUpEvent(MouseUpEvent event) {
		(new Timer() {
			@Override
			public void run() {
				selectionHandler();
			}
		}).schedule(10);		
	}
	
	protected void onKeyPressEvent(KeyPressEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		int keyCode = nativeEvent.getKeyCode();

		boolean sink = false;
		Range range;
		
		switch ( keyCode ) {
			case KeyCodes.KEY_ENTER:
				break;

			case KeyCodes.KEY_BACKSPACE:
				range = selection.getRange();
				if ( (range != null && range.isCollapsed() && range.getTextBeforeStart().length() == 0)) {
					sink = true;
				}				
				break;
				
			case KeyCodes.KEY_DELETE:
				break;
		}
		
		Node prev;
		while ((prev = getElement().getPreviousSibling()) != null) {
			if (prev.getNodeType() == Node.ELEMENT_NODE &&
					"br".equalsIgnoreCase(Element.as(prev).getTagName()) &&
					Element.as(prev).hasAttribute("_moz_dirty")) {
					prev.getParentElement().removeChild(prev);
			} else {
				break;
			}
		}
		
		if ( sink ) {
			event.preventDefault();
		}
		event.stopPropagation();		
	}

	protected void onKeyDownEvent(KeyDownEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		int keyCode = nativeEvent.getKeyCode();

		boolean sink = false;
		Range range;
		
		switch ( keyCode ) {
			case KeyCodes.KEY_ENTER:
				break;

			case KeyCodes.KEY_BACKSPACE:
				range = selection.getRange();
				if ( (range != null && range.isCollapsed() && range.getTextBeforeStart().length() == 0)) {
					sink = true;
				}				
				break;
				
			case KeyCodes.KEY_DELETE:
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
		
		if ( keyCode == KeyCodes.KEY_DELETE || keyCode == KeyCodes.KEY_BACKSPACE ) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					if (!getElement().hasChildNodes() ||
							getElement().getFirstChild().getNodeType() == Node.TEXT_NODE) {
						getElement().setInnerHTML("<li>&nbsp;</li>");
						selection.setCursorInto(getElement().getFirstChild());
					}
					Node prev;
					while ((prev = getElement().getPreviousSibling()) != null) {
						if (prev.getNodeType() == Node.ELEMENT_NODE &&
								"br".equalsIgnoreCase(Element.as(prev).getTagName()) &&
								Element.as(prev).hasAttribute("_moz_dirty")) {
								prev.getParentElement().removeChild(prev);
						} else {
							break;
						}
					}
				}
			});
		}
		
		(new Timer() {
			@Override
			public void run() {
				selectionHandler();
			}
		}).schedule(10);		
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
