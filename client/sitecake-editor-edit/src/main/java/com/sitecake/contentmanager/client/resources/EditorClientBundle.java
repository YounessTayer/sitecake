package com.sitecake.contentmanager.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface EditorClientBundle extends ClientBundle {

	static final EditorClientBundle INSTANCE = GWT.create(EditorClientBundle.class);
	
	@Source("editor.css")
	EditorCssResource css();

	@Source("isolation.css")
	IsolationCss isolationCss();
	
	@Source("images/blue-border-hor.gif")
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource blueBorderHorizontalBackground();
	
	@Source("images/blue-border-ver.gif")
	@ImageOptions(repeatStyle = RepeatStyle.Vertical)
	ImageResource blueBorderVerticalBackground();
	
	@Source("images/blank.gif")
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	ImageResource blankBackground();
	
	@Source("images/button-delete.gif")
	ImageResource deleteButton();
	
	@Source("images/button-resize.gif")
	ImageResource resizeButton();
	
	@Source("images/button-delete-over.gif")
	ImageResource deleteButtonOver();

	@Source("images/trash-big.png")
	ImageResource trashBinBig();

	@Source("images/trash-small.png")
	ImageResource trashBinSmall();

	@Source("images/aspect-ratio-16x9.png")
	ImageResource aspectRation16x9();

	@Source("images/flowborder.gif")
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	ImageResource flowBorder();

	@Source("images/mode-display-resize.gif")
	ImageResource modeDisplayResize();

	@Source("images/mode-display-crop.gif")
	ImageResource modeDisplayCrop();

	@Source("images/mode-display-frame.gif")
	ImageResource modeDisplayFrame();

	@Source("images/slideshow-type-overlay.png")
	ImageResource slideshowTypeOverlay();
	
	@Source("images/toolbar/logo.png")
	ImageResource toolbarLogo();
	
	@Source("images/toolbar/divider1.png")
	ImageResource toolbarDivider1();
	
	@Source("images/toolbar/divider2.png")
	ImageResource toolbarDivider2();
	
	@Source("images/toolbar/button-grad.png")
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource toolbarButtonBkgGradient();
	
	@Source("images/toolbar/undo-active-up.png")
	ImageResource toolbarUndoUp();

	@Source("images/toolbar/undo-active-hover.png")
	ImageResource toolbarUndoUpHover();

	@Source("images/toolbar/undo-active-press.png")
	ImageResource toolbarUndoDown();

	@Source("images/toolbar/undo-up.png")
	ImageResource toolbarUndoDisabled();

	@Source("images/toolbar/redo-active-up.png")
	ImageResource toolbarRedoUp();

	@Source("images/toolbar/redo-active-hover.png")
	ImageResource toolbarRedoUpHover();

	@Source("images/toolbar/redo-active-press.png")
	ImageResource toolbarRedoDown();

	@Source("images/toolbar/redo-up.png")
	ImageResource toolbarRedoDisabled();

	@Source("images/toolbar/publish-active-up.png")
	ImageResource toolbarPublishUp();

	@Source("images/toolbar/publish-active-press.png")
	ImageResource toolbarPublishDown();

	@Source("images/toolbar/publish-up.png")
	ImageResource toolbarPublishDisabled();

	@Source("images/toolbar/logout-link.png")
	ImageResource toolbarLogoutUp();

	@Source("images/toolbar/logout-hover.png")
	ImageResource toolbarLogoutUpHover();

	@Source("images/toolbar/logout-active.png")
	ImageResource toolbarLogoutDown();
	
	@Source("images/toolbar/style-up.png")
	ImageResource toolbarStyleSelectorButton();
	
	@Source("images/toolbar/style-press.png")
	ImageResource toolbarStyleSelectorButtonActive();
	
	@Source("images/toolbar/link-type-button-link.png")
	ImageResource linkEditorTypeSelectButtonUp();

	@Source("images/toolbar/link-type-button-active.png")
	ImageResource linkEditorTypeSelectButtonDown();	

	@Source("images/toolbar/x-link.png")
	ImageResource linkEditorUnlinkButtonUp();	

	@Source("images/toolbar/x-hover.png")
	ImageResource linkEditorUnlinkButtonUpHover();	
	
	@Source("images/toolbar/x-active.png")
	ImageResource linkEditorUnlinkButtonDown();
//****************
	@Source("images/toolbar/bold-up.png")
	ImageResource linkEditorBoldButtonUp();	

	@Source("images/toolbar/bold-hover.png")
	ImageResource linkEditorBoldButtonUpHover();	
	
	@Source("images/toolbar/bold-press.png")
	ImageResource linkEditorBoldButtonDown();

	@Source("images/toolbar/bold-active-up.png")
	ImageResource linkEditorBoldButtonActiveUp();	

	@Source("images/toolbar/bold-active-hover.png")
	ImageResource linkEditorBoldButtonActiveUpHover();	
	
	@Source("images/toolbar/bold-active-press.png")
	ImageResource linkEditorBoldButtonActiveDown();
//*************	
	@Source("images/toolbar/italic-up.png")
	ImageResource linkEditorItalicButtonUp();	

	@Source("images/toolbar/italic-hover.png")
	ImageResource linkEditorItalicButtonUpHover();	
	
	@Source("images/toolbar/italic-press.png")
	ImageResource linkEditorItalicButtonDown();

	@Source("images/toolbar/italic-active-up.png")
	ImageResource linkEditorItalicButtonActiveUp();	

	@Source("images/toolbar/italic-active-hover.png")
	ImageResource linkEditorItalicButtonActiveUpHover();	
	
	@Source("images/toolbar/italic-active-press.png")
	ImageResource linkEditorItalicButtonActiveDown();
//*************	
	@Source("images/fatal.png")
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource fatalErrorBackground();
	
	@Source("images/x-error-normal.png")
	ImageResource errorNotificationCloseButtonUp();	

	@Source("images/x-error-hover.png")
	ImageResource errorNotificationCloseButtonUpHover();	
	
	@Source("images/x-error-active.png")
	ImageResource errorNotificationCloseButtonDown();	

	@Source("images/placeholder/icon-image.png")
	ImageResource uploadImage();	

	@Source("images/placeholder/icon-slideshow.png")
	ImageResource uploadSlideshow();	

	@Source("images/placeholder/icon-slideshow.png")
	ImageResource uploadSlider();	
	
	@Source("images/placeholder/icon-file.png")
	ImageResource uploadFile();	

	@Source("images/placeholder/icon-html.png")
	ImageResource pasteHtml();	

	@Source("images/placeholder/icon-video.png")
	ImageResource pasteVideo();	

	@Source("images/placeholder/icon-flash.png")
	ImageResource pasteFlash();	

	@Source("images/placeholder/icon-map.png")
	ImageResource pasteMap();
	
// **** tolbar icons ****

	@Source("images/toolbar/icon-text.png")
	ImageResource toolbarIconText();

	@Source("images/toolbar/icon-text-active.png")
	ImageResource toolbarIconTextActive();

	@Source("images/toolbar/icon-h1.png")
	ImageResource toolbarIconHeading1();
	
	@Source("images/toolbar/icon-h1-active.png")
	ImageResource toolbarIconHeading1Active();
	
	@Source("images/toolbar/icon-h2.png")
	ImageResource toolbarIconHeading2();
	
	@Source("images/toolbar/icon-h2-active.png")
	ImageResource toolbarIconHeading2Active();
	
	@Source("images/toolbar/icon-h3.png")
	ImageResource toolbarIconHeading3();
	
	@Source("images/toolbar/icon-h3-active.png")
	ImageResource toolbarIconHeading3Active();
	
	@Source("images/toolbar/icon-h4.png")
	ImageResource toolbarIconHeading4();
	
	@Source("images/toolbar/icon-h4-active.png")
	ImageResource toolbarIconHeading4Active();
	
	@Source("images/toolbar/icon-h5.png")
	ImageResource toolbarIconHeading5();
	
	@Source("images/toolbar/icon-h5-active.png")
	ImageResource toolbarIconHeading5Active();
	
	@Source("images/toolbar/icon-h6.png")
	ImageResource toolbarIconHeading6();
	
	@Source("images/toolbar/icon-h6-active.png")
	ImageResource toolbarIconHeading6Active();
	
	@Source("images/toolbar/icon-contact.png")
	ImageResource toolbarIconContact();
	
	@Source("images/toolbar/icon-contact-active.png")
	ImageResource toolbarIconContactActive();
	
	@Source("images/toolbar/icon-file.png")
	ImageResource toolbarIconFile();
	
	@Source("images/toolbar/icon-file-active.png")
	ImageResource toolbarIconFileActive();
	
	@Source("images/toolbar/icon-flash.png")
	ImageResource toolbarIconFlash();
	
	@Source("images/toolbar/icon-flash-active.png")
	ImageResource toolbarIconFlashActive();
	
	@Source("images/toolbar/icon-html.png")
	ImageResource toolbarIconHtml();
	
	@Source("images/toolbar/icon-html-active.png")
	ImageResource toolbarIconHtmlActive();
	
	@Source("images/toolbar/icon-image.png")
	ImageResource toolbarIconImage();
	
	@Source("images/toolbar/icon-image-active.png")
	ImageResource toolbarIconImageActive();
	
	@Source("images/toolbar/icon-list.png")
	ImageResource toolbarIconList();
	
	@Source("images/toolbar/icon-list-active.png")
	ImageResource toolbarIconListActive();
	
	@Source("images/toolbar/icon-map.png")
	ImageResource toolbarIconMap();
	
	@Source("images/toolbar/icon-map-active.png")
	ImageResource toolbarIconMapActive();
	
	@Source("images/toolbar/icon-slideshow.png")
	ImageResource toolbarIconSlideshow();
	
	@Source("images/toolbar/icon-slideshow-active.png")
	ImageResource toolbarIconSlideshowActive();

	@Source("images/toolbar/icon-slider.png")
	ImageResource toolbarIconSlider();
	
	@Source("images/toolbar/icon-slider-active.png")
	ImageResource toolbarIconSliderActive();
	
	@Source("images/toolbar/icon-video.png")
	ImageResource toolbarIconVideo();
	
	@Source("images/toolbar/icon-video-active.png")
	ImageResource toolbarIconVideoActive();	
}
