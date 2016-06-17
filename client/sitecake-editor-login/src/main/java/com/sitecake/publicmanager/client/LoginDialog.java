package com.sitecake.publicmanager.client;

import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ESCAPE;

import org.adamtacy.client.ui.effects.events.EffectCompletedEvent;
import org.adamtacy.client.ui.effects.events.EffectCompletedHandler;
import org.adamtacy.client.ui.effects.impl.Fade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.commons.client.util.SiteCakeVersion;
import com.sitecake.publicmanager.client.resources.ClientBundle;
import com.sitecake.publicmanager.client.resources.Messages;

public class LoginDialog extends Composite {

	private static LoginDialogUiBinder uiBinder = GWT
			.create(LoginDialogUiBinder.class);

	interface LoginDialogUiBinder extends UiBinder<Widget, LoginDialog> {
	}

	public interface LoginHandler {
		public void onLogin(String credential);
		public void onChange(String credential, String newCredential, String newCredential2);
		public void onSwitchToChangeBox();
		public void onSwitchToLoginBox();
	}
	
	private LoginHandler handler;

	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	@UiField
	PushButton closeButton;
 
	@UiField
	Label loginLabel;

	@UiField
	Image logo;
	
	@UiField
	PasswordTextBox credentialInput;
 
	@UiField
	PushButton loginButton;
 
	@UiField
	Label statusLabel;
 
	@UiField
	Label versionLabel;
	
	@UiField
	FlowPanel loginBox;
	
	@UiField
	FlowPanel changeBox;
	
	@UiField
	PasswordTextBox newCredentialInput;
	
	@UiField
	PasswordTextBox newCredential2Input;
	
	@UiField
	PasswordTextBox oldCredentialInput;
	
	@UiField
	Label oldCredentialLabel;
	
	@UiField
	Label newCredentialLabel;
	
	@UiField
	Label newCredentialRepeatedLabel;
	
	@UiField
	PushButton changeButton;
	
	@UiField
	Anchor switchToLoginBox;
	
	@UiField
	Anchor switchToChangeBox;
	
	private Fade showEffect;
	
	private Fade hideEffect;
	
	private boolean loginMode;
	
	public LoginDialog(LoginHandler handler) {

		this.handler = handler;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		loginButton.getUpFace().setHTML(
			"<div class=\"" + ClientBundle.INSTANCE.css().loginButtonUp() + "\">" + messages.loginDialogSubmitLabel()
		);
		loginButton.getDownFace().setHTML(
				"<div class=\"" + ClientBundle.INSTANCE.css().loginButtonDown() + "\">" + messages.loginDialogSubmitLabel()
		);
		loginLabel.setText(messages.loginDialogLabel());
		oldCredentialLabel.setText(messages.oldCredentialLabel());
		newCredentialLabel.setText(messages.newCredentialLabel());
		newCredentialRepeatedLabel.setText(messages.newCredentialRepeatedLabel());
		changeButton.getUpFace().setHTML(
				"<div class=\"" + ClientBundle.INSTANCE.css().loginButtonUp() + "\">" + messages.changeButton() + "</div>"
			);
		changeButton.getDownFace().setHTML(
				"<div class=\"" + ClientBundle.INSTANCE.css().loginButtonDown() + "\">" + messages.changeButton() + "</div>"
		);
		switchToLoginBox.setText(messages.cancelChangeButton());
		switchToChangeBox.setText(messages.switchToChangePassword());
		
		changeBox.setVisible(false);
		loginMode = true;
		
		String version = "ver " + SiteCakeVersion.id;
		versionLabel.setText(version);
		
		setVisible(false);
	
		showEffect = new Fade();
		showEffect.setStartOpacity(0);
		showEffect.setEndOpacity(100);
		showEffect.setDuration(0.2);
		showEffect.setEffectElement(getElement());
		
		hideEffect = new Fade();
		hideEffect.setStartOpacity(100);
		hideEffect.setEndOpacity(0);
		hideEffect.setDuration(0.2);
		hideEffect.addEffectCompletedHandler(new EffectCompletedHandler() {
			public void onEffectCompleted(EffectCompletedEvent event) {
				hide(true);				
			}
		});
		hideEffect.setEffectElement(getElement());
		
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if ( event.getTypeInt() == Event.ONKEYDOWN ) {
					NativeEvent nativeEvent = event.getNativeEvent();
					int keyCode = nativeEvent.getKeyCode();

					if ( keyCode == KEY_ESCAPE ) {
						hide(false);
					}
				}
			}
		});
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				setPosition();
			}
		});
	}

	public void setMessage(String message) {
		statusLabel.setText(message);
	}

	public void setMode(boolean loginMode) {
		this.loginMode = loginMode;
		
		if ( loginMode ) {
			changeBox.setVisible(false);			
			loginBox.setVisible(true);
			credentialInput.setFocus(true);
		} else {
			loginBox.setVisible(false);
			changeBox.setVisible(true);
			oldCredentialInput.setFocus(true);
		}
	}
	
	public void setNewCredentialErrorStyle(boolean error) {
		if ( error ) {
			newCredentialInput.addStyleName(ClientBundle.INSTANCE.css().credentialInputError());
			newCredential2Input.addStyleName(ClientBundle.INSTANCE.css().credentialInputError());
		} else {
			newCredentialInput.removeStyleName(ClientBundle.INSTANCE.css().credentialInputError());
			newCredential2Input.removeStyleName(ClientBundle.INSTANCE.css().credentialInputError());
		}
	}
	
	public void setProgress(boolean enable) {
		Widget target = loginMode ? credentialInput : newCredential2Input;
		Widget oldTarget = loginMode ? newCredential2Input : credentialInput;
		oldTarget.removeStyleName(ClientBundle.INSTANCE.css().inputWithProgressAnimation());
		
		if ( enable ) {
			target.addStyleName(ClientBundle.INSTANCE.css().inputWithProgressAnimation());
		} else {
			target.removeStyleName(ClientBundle.INSTANCE.css().inputWithProgressAnimation());
		}
	}
	
	public void resetCredential() {
		credentialInput.setText("");
		newCredentialInput.setText("");
		newCredential2Input.setText("");
		oldCredentialInput.setText("");
	}
	
	public void show() {
		getElement().getStyle().setOpacity(0);
		setVisible(true);
		setPosition();
		showEffect.play();
		credentialInput.setFocus(true);
	}
	
	private void setPosition() {
		int left = Window.getClientWidth()/2 - getElement().getClientWidth()/2;
		int top = Window.getClientHeight()/2 - getElement().getClientHeight()/2;
		getElement().getStyle().setTop(top, Unit.PX);
		getElement().getStyle().setLeft(left, Unit.PX);
	}
	
	@UiHandler("credentialInput")
	void onInputKeyDown(KeyDownEvent event) {
		if ( event.getNativeKeyCode() == KEY_ENTER ) {
			submitLogin();
		}		
	}
	
	@UiHandler("loginButton")
	void onLoginButtonClick(ClickEvent event) {
		submitLogin();
	}

	@UiHandler("closeButton")
	void onCloseButtonClick(ClickEvent event) {
		hide(false);
	}
	
	@UiHandler("switchToChangeBox")
	void onSwitchToChangeBoxClick(ClickEvent event) {
		handler.onSwitchToChangeBox();
	}
	
	@UiHandler("switchToLoginBox")
	void onSwitchToLoginBoxClick(ClickEvent event) {
		handler.onSwitchToLoginBox();
	}
	
	@UiHandler("changeButton")
	void onChangeButtonClick(ClickEvent event) {
		submitChange();
	}
	
	@UiHandler("newCredentialInput")
	void onCredentialInputKeyDown(KeyDownEvent event) {
		if ( event.getNativeKeyCode() == KEY_ENTER ) {
			submitChange();
		}		
	}
	
	@UiHandler("newCredential2Input")
	void onCredential2InputKeyDown(KeyDownEvent event) {
		if ( event.getNativeKeyCode() == KEY_ENTER ) {
			submitChange();
		}		
	}
	
	@UiHandler("oldCredentialInput")
	void onOldCredentialInputKeyDown(KeyDownEvent event) {
		if ( event.getNativeKeyCode() == KEY_ENTER ) {
			submitChange();
		}		
	}
	
	public void hide(boolean forced) {
		resetCredential();
		if ( forced ) {
			setVisible(false);
		} else {
			hideEffect.play();
		}
	}
	
	private void submitLogin() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				if ( handler != null ) {
					String credential = credentialInput.getValue();
					handler.onLogin(credential);
				}
				
			}
		});
	}
	
	private void submitChange() {
		final String credential = oldCredentialInput.getValue();
		final String newCredential = newCredentialInput.getValue();
		final String newCredential2 = newCredential2Input.getValue();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				if ( handler != null ) {
					handler.onChange(credential, newCredential, newCredential2);
				}
				
			}
		});
	}	
}
