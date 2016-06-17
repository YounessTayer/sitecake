package com.sitecake.contentmanager.client.item.twitter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.SaveRequestEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.resources.Messages;

public class TwitterStatusItem extends ContentItem {

	public static final String DISCRIMINATOR = "sc-twitter-status";
	
	public static final String CONTENT_TYPE_NAME = "TWITTER_STATUS";
	
	interface TwitterStatusItemUiBinder extends UiBinder<Element, TwitterStatusItem> {}
	
	private static TwitterStatusItemUiBinder uiBinder = GWT.create(TwitterStatusItemUiBinder.class);
	
	interface CssStyle extends CssResource {
		String status();
		String initialized();		
	}
	
	@UiField
	CssStyle cssStyle;
	
	@UiField
	Element statusContainer;
	
	@UiField
	Element statusPlaceholder;
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	
	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	private DomSelector domSelector = GinInjector.instance.getDomSelector();
	
	private String tweetId;
	
	private String tweetUrl;
	
	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}

	@Override
	public String getItemSelector() {
		return "div." + DISCRIMINATOR;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean isResizable() {
		return false;
	}

	@Override
	public ContentItem cloneItem() {
		TwitterStatusItem clone = new TwitterStatusItem();
		super.cloneItem(clone);
		clone.init(tweetId, null);		
		clone.tweetUrl = tweetUrl;
		return clone;
	}

	@Override
	public String getHtml() {
		return "<div class=\"" + DISCRIMINATOR + "\" style=\"position:relative;max-width:99.2%;min-width:220px;width:537px;\">" +
					"<blockquote class=\"twitter-tweet\">" +
					"<a href=\"" + tweetUrl + "\"></a>" +
					"</blockquote>" +
					"<script async src=\"//platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>" +
				"</div>";
	}

	private static RegExp testRe = RegExp.compile("\\/\\/twitter\\.com\\/(?:.*?)\\/status\\/([0-9a-zA-Z]+)");
	
	public static boolean testText(String text) {
		return (testRe.exec(text) != null);
	}
	
	public static TwitterStatusItem create(String text) {
		MatchResult matches = testRe.exec(text);
		if (matches == null) {
			return null;
		}
		TwitterStatusItem item = new TwitterStatusItem();
		String tweetId = matches.getGroup(1);
		item.init(tweetId, null);
		return item;
	}
	
	public static TwitterStatusItem create(Element element) {
		if (element == null) {
			return null;
		}
		MatchResult matches = testRe.exec(element.getInnerHTML());
		if (matches == null) {
			return null;
		}		
		TwitterStatusItem item = new TwitterStatusItem();
		String tweetId = matches.getGroup(1);
		item.init(tweetId, element);
		return item;		
	}
	
	public void init(String tweetId, Element origElement) {
		Element element = uiBinder.createAndBindUi(this);
		if ( origElement != null ) {
			DomUtil.replaceElement(origElement, element);
		}
		setElement(element);
		
		this.tweetId = tweetId;
	}
	
	@Override
	public void onInclusion() {
		super.onInclusion();
		
		statusContainer.setInnerHTML("");
		statusContainer.setClassName(cssStyle.status());
		getElement().removeClassName(cssStyle.initialized());
		twitterWidgetLoad(tweetId, statusContainer, true);
	}
	
	private native void twitterWidgetLoad(String tweetId, Element container, boolean notify)/*-{
		var self = this;
		$wnd.twttr.widgets.createTweet(tweetId, container, {})
			.then(function(el) {
				if (notify)
					self.@com.sitecake.contentmanager.client.item.twitter.TwitterStatusItem::onTweetLoaded()();
			});
	}-*/;
	
	protected void onTweetLoaded() {
		tweetUrl = obtainStatusUrl();
		statusPlaceholder.addClassName(cssStyle.initialized());
		getElement().addClassName(cssStyle.initialized());
		eventBus.fireEventDeferred(new SaveRequestEvent());
	}
	
	private String obtainStatusUrl() {
		return obtainStatusUrlFromIframe(domSelector.select("iframe", statusContainer).get(0));
	}
	
	private native String obtainStatusUrlFromIframe(Element iframe)/*-{
		return iframe.contentWindow.document.getElementsByClassName('tweet')[0].getAttribute('cite');
	}-*/;
	
}
