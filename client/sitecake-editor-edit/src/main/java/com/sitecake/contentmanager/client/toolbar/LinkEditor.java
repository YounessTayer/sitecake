package com.sitecake.contentmanager.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.resources.Messages;

public class LinkEditor extends Composite {

	private static LinkEditorUiBinder uiBinder = GWT
			.create(LinkEditorUiBinder.class);

	interface LinkEditorUiBinder extends UiBinder<Widget, LinkEditor> {
	}

	public enum LinkType {
		WEB_LINK,
		MAILTO_LINK
	}
	
	@UiField
	HTML typeButton;
	
	@UiField
	SpanElement typeButtonLabel;
	
	@UiField
	TextBox urlInput;
	
	@UiField
	PushButton unlinkButton;

	@UiField
	PushButton boldButton;

	@UiField
	PushButton italicButton;
	
	private boolean isBold;
	
	private boolean isItalic;
	
	private LinkableItem linkableItem;
	
	private EditableTextItem editableItem;
	
	private String webLinkBackup = "";
	
	private String mailtoLinkBackup = "";
	
	public void setLinkableItem(LinkableItem linkableItem) {
		this.linkableItem = linkableItem;
	}

	public void setEditableItem(EditableTextItem item, boolean isBold, boolean isItalic) {
		setLinkableItem(item);
		this.editableItem = item;
		
		if ( item == null ) {
			boldButton.setVisible(false);
			italicButton.setVisible(false);
		} else {
			this.isBold = isBold;
			this.isItalic = isItalic;
			if ( isBold ) {
				boldButton.addStyleName(EditorClientBundle.INSTANCE.css().linkEditorBoldButtonActive());
			} else {
				boldButton.removeStyleName(EditorClientBundle.INSTANCE.css().linkEditorBoldButtonActive());
			}
			if ( isItalic ) {
				italicButton.addStyleName(EditorClientBundle.INSTANCE.css().linkEditorItalicButtonActive());
			} else {
				italicButton.removeStyleName(EditorClientBundle.INSTANCE.css().linkEditorItalicButtonActive());
			}
			boldButton.setVisible(true);
			italicButton.setVisible(true);
		}
	}
	
	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	private LinkType linkType;
	
	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
		setTypeButtonLabel();
	}

	public void setUrl(String url) {
		if ( url == null ) return;
		
		if ( url.startsWith("mailto:") ) {
			setLinkType(LinkType.MAILTO_LINK);
			webLinkBackup = "";
			mailtoLinkBackup = url;
		} else {
			setLinkType(LinkType.WEB_LINK);
			webLinkBackup = url;
			mailtoLinkBackup = "";
		}
		
		setProcessedUrl(url);
	}
	
	public String getUrl() {
		return getProcessedUrl();
	}
	
	public LinkEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		setLinkType(LinkType.WEB_LINK);
	}

	@UiHandler("typeButton")
	void onTypeButtonMouseDown(MouseDownEvent event) {
		event.preventDefault();
		typeButton.addStyleName(EditorClientBundle.INSTANCE.css().linkEditorTypeSelectorButtonActive());
	}
	
	@UiHandler("typeButton")
	void onTypeButtonMouseUp(MouseUpEvent event) {
		typeButton.removeStyleName(EditorClientBundle.INSTANCE.css().linkEditorTypeSelectorButtonActive());
	}

	@UiHandler("typeButton")
	void onTypeButtonClick(ClickEvent event) {
		switch ( linkType ) {
		case WEB_LINK:
			linkType = LinkType.MAILTO_LINK;
			setProcessedUrl(mailtoLinkBackup);
			break;
		case MAILTO_LINK:
			linkType = LinkType.WEB_LINK;
			setProcessedUrl(webLinkBackup);
			break;
		}
		setTypeButtonLabel();
	}
	
	@UiHandler("urlInput")
	void onUrlInputFocus(FocusEvent event) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				if ( linkableItem != null ) {
					linkableItem.link(getProcessedUrl(), false);
					urlInput.getElement().focus();
				}
			}
		});
	}
	
	@UiHandler("urlInput")
	void onUrlInputChange(ChangeEvent event) {
		webLinkBackup = "";
		mailtoLinkBackup = "";
		if ( linkableItem != null ) {
			linkableItem.link(getProcessedUrl(), false);
		}		
	}
	
	@UiHandler("urlInput")
	void onUrlInputKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		
		if ( keyCode != KeyCodes.KEY_ENTER ) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						if ( linkableItem != null ) {
							linkableItem.link(getProcessedUrl(), false);
						}		
					}
				});
		} else {
			event.preventDefault();
			event.stopPropagation();
			
			if ( linkableItem != null ) {
				linkableItem.link(getProcessedUrl(), true);
			}
			urlInput.getElement().blur();
		}
	}
	
	@UiHandler("unlinkButton")
	void onUnlinkButtonClick(ClickEvent event) {
		if ( linkableItem != null ) {
			urlInput.setText("");
			linkableItem.unlink();
			urlInput.getElement().blur();
		}		
	}
	
	@UiHandler("boldButton")
	void onBoldButtonClick(ClickEvent event) {
		isBold = !isBold;
		if ( isBold ) {
			boldButton.addStyleName(EditorClientBundle.INSTANCE.css().linkEditorBoldButtonActive());
		} else {
			boldButton.removeStyleName(EditorClientBundle.INSTANCE.css().linkEditorBoldButtonActive());
		}
		editableItem.bold(isBold);
	}

	@UiHandler("italicButton")
	void onItalicButtonClick(ClickEvent event) {
		isItalic = !isItalic;
		if ( isItalic ) {
			italicButton.addStyleName(EditorClientBundle.INSTANCE.css().linkEditorItalicButtonActive());
		} else {
			italicButton.removeStyleName(EditorClientBundle.INSTANCE.css().linkEditorItalicButtonActive());
		}
		editableItem.italic(isItalic);
	}
	
	private void setTypeButtonLabel() {
		switch ( linkType ) {
			case WEB_LINK:
				typeButtonLabel.setInnerText(messages.linkEditorWebLink());
				break;
			case MAILTO_LINK:
				typeButtonLabel.setInnerText(messages.linkEditorMailtoLink());
				break;
		}
	}
	
	private String processOutputUrl(String url) {
		String result = (url == null) ? "" : url;
		
		if ( linkType.equals(LinkType.WEB_LINK) ) {
			if ( !result.startsWith("http://") && !result.startsWith("https://") && !"".equals(result) ) {
				result = "http://" + result;
			}
		} else {
			if ( !result.startsWith("mailto:") && !"".equals(result) ) {
				result = "mailto:" + result;
			}
		}
		
		return result;
	}
	
	private String processInputUrl(String url) {
		String result = (url == null) ? "" : url;
		
		if ( linkType.equals(LinkType.WEB_LINK) ) {
			if ( result.startsWith("http://") ) {
				result = result.substring("http://".length());
			} else if ( result.startsWith("https://") ) {
				result = result.substring("https://".length());
			}
		} else {
			if ( result.startsWith("mailto:") ) {
				result = result.substring("mailto:".length());
			}
		}
		
		return URL.encode(result);
	}
	
	private String getProcessedUrl() {
		return processOutputUrl(urlInput.getText());
	}
	
	private void setProcessedUrl(String url) {
		urlInput.setText(processInputUrl(url));
	}
}
