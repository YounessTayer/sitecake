package com.sitecake.publicmanager.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface ClientBundle extends com.google.gwt.resources.client.ClientBundle {

	static final ClientBundle INSTANCE = GWT.create(ClientBundle.class);
	
	@Source("style.css")
	CssResource css();
	
	@Source("images/login-gradient.png")
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource loginDialogBackground();
	
	@Source("images/ajax-loader.gif")
	ImageResource progressAnimation();
	
	@Source("images/login-logo.png")
	ImageResource logo();
	
	@Source("images/login-x-link.png")
	ImageResource closeButtonUp();
	
	@Source("images/login-x-hover.png")
	ImageResource closeButtonUpHover();
	
	@Source("images/login-x-active.png")
	ImageResource closeButtonDown();
	
	@Source("images/login-btn-link.png")
	ImageResource loginButtonUp();
	
	@Source("images/login-btn-hover.png")
	ImageResource loginButtonUpHover();
	
	@Source("images/login-btn-active.png")
	ImageResource loginButtonDown();	
}
