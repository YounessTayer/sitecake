package com.sitecake.publicmanager.client.resources;

public interface CssResource extends com.google.gwt.resources.client.CssResource {

	@ClassName("login-dialog")
	String loginDialog();
	
	@ClassName("login-dialog-background")
	String loginDialogBackground();

	@ClassName("login-label")
	String loginLabel();

	@ClassName("login-dialog-logo")
	String loginDialogLogo();

	@ClassName("login-dialog-logo-space")
	String loginDialogLogoSpace();
	
	@ClassName("login-credential")
	String loginCredential();

	@ClassName("login-button")
	String loginButton();
	
	@ClassName("login-button-up")
	String loginButtonUp();

	@ClassName("login-button-down")
	String loginButtonDown();

	@ClassName("login-dialog-close")
	String loginCloseButton();

	@ClassName("login-dialog-close-up")
	String loginCloseButtonUp();

	@ClassName("login-dialog-close-up-hover")
	String loginCloseButtonUpHover();

	@ClassName("login-dialog-close-down")
	String loginCloseButtonDown();
	
	@ClassName("login-status-label")
	String loginStatusLabel();

	@ClassName("login-dialog-version")
	String loginDialogVersion();
	
	@ClassName("link-button")
	String linkButton();
	
	@ClassName("change-credential")
	String changeCredential();
	
	@ClassName("change-button")
	String changeButton();
	
	@ClassName("change-cancel-button")
	String changeCancelButton();
	
	@ClassName("credential-input-error")
	String credentialInputError();
	
	@ClassName("with-progress-animation")
	String inputWithProgressAnimation();
}
