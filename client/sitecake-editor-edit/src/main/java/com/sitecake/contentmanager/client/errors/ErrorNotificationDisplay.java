package com.sitecake.contentmanager.client.errors;

import org.adamtacy.client.ui.effects.core.NMorphStyle;
import org.adamtacy.client.ui.effects.events.EffectCompletedEvent;
import org.adamtacy.client.ui.effects.events.EffectCompletedHandler;
import org.adamtacy.client.ui.effects.impl.Fade;
import org.adamtacy.client.ui.effects.impl.SlideDown;
import org.adamtacy.client.ui.effects.impl.css.Rule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.resources.Messages;

public class ErrorNotificationDisplay extends Composite {

	private static ErrorNotificationDisplayUiBinder uiBinder = GWT
			.create(ErrorNotificationDisplayUiBinder.class);

	interface ErrorNotificationDisplayUiBinder extends
			UiBinder<Widget, ErrorNotificationDisplay> {
	}

	interface CssStyle extends CssResource {
		String openedState();
	}
	
	@UiField
	CssStyle cssStyle;
	
	@UiField
	FlowPanel header;
	
	@UiField
	HTML lastNotification;
	
	@UiField
	Anchor detailsButton;
	
	@UiField
	PushButton closeButton;
	
	@UiField
	FlowPanel notifications;

	private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	private boolean isShown;
	
	private NMorphStyle showEffect;
	
	private NMorphStyle showNotificationsEffect;
	
	private Fade hideEffect;
	
	private Timer displayTimeout;
	
	public ErrorNotificationDisplay() {
		initWidget(uiBinder.createAndBindUi(this));
		getElement().getStyle().setPosition(Position.FIXED);
		detailsButton.setText(messages.notificationDialogReadMore());
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				setPosition();
			}
		});
		
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		showEffect = new NMorphStyle(
				new Rule("ss{ top: -30px; position:fixed; }"), 
				new Rule("se{ top: 0px; position:fixed; }"));
		showEffect.setDuration(0.25);
		showEffect.setEffectElement(getElement());
		
		showNotificationsEffect = new SlideDown();
		showNotificationsEffect.setDuration(0.25);
		showNotificationsEffect.setEffectElement(notifications.getElement());
		
		hideEffect = new Fade();
		hideEffect.setStartOpacity(100);
		hideEffect.setEndOpacity(0);
		hideEffect.setDuration(0.2);
		hideEffect.addEffectCompletedHandler(new EffectCompletedHandler() {
			public void onEffectCompleted(EffectCompletedEvent event) {
				setVisible(false);
				getElement().getStyle().setOpacity(1.0);
			}
		});
		hideEffect.setEffectElement(getElement());

		this.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if ( displayTimeout != null ) {
					displayTimeout.cancel();
					displayTimeout = null;
				}
			}
		}, MouseOverEvent.getType());
		
		isShown = false;
		setVisible(false);
	}

	public void addNotification(ErrorNotificationEvent event, int number) {
		lastNotification.setHTML("#" + String.valueOf(number) + " " + event.getMessage());
		
		String html = "<p>#" + String.valueOf(number) + " " + event.getMessage() + "</p>";
		if ( event.getDetails() != null ) {
			html += "<p>" + event.getDetails() + "</p>";
		}
		HTML notification = new HTML(html);
		if ( notifications.getWidgetCount() == 0 ) {
			notifications.add(notification);
		} else {
			notifications.insert(notification, 0);
		}
		
		show();
	}
	
	@UiHandler("detailsButton")
	void onReadMoreClick(ClickEvent event) {
		event.preventDefault();
		addStyleName(cssStyle.openedState());
		detailsButton.setVisible(false);
		notifications.setVisible(true);
		
		showNotificationsEffect.play();
	}
	
	/**
	 * Center the dialog position.
	 */
	private void setPosition() {
		int selfWidth = getElement().getClientWidth();
		int windowWidth = Window.getClientWidth();
		getElement().getStyle().setLeft((windowWidth - selfWidth)/2, Unit.PX);
	}
	
	private void hide() {
		if ( !isShown ) return;
		
		isShown = false;
		hideEffect.play();
	}
	
	private void show() {
		if ( !isShown ) {
			
			isShown = true;
			detailsButton.setVisible(true);
			notifications.setVisible(false);
			removeStyleName(cssStyle.openedState());
			setVisible(true);
			setPosition();
			showEffect.play();
		}
		
		restartDisplayTimeout();
	}
	
	private void restartDisplayTimeout() {
		if ( displayTimeout != null ) {
			displayTimeout.cancel();
		}
		
		displayTimeout = new Timer() {
			@Override
			public void run() {
				hide();
			}
		};
		displayTimeout.schedule(10000);		
	}
}
