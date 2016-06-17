package com.sitecake.contentmanager.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.sitecake.contentmanager.client.resources.NonLocaleMessages;

public class ModuleIE implements EntryPoint {

	private static NonLocaleMessages messages = GWT.create(NonLocaleMessages.class);
	
	private Timer timer;
	
	private boolean cfLoaded = false;
	
	public void onModuleLoad() {
		loadCFInstall();
	}
	
	private void showCFMissingNotice() {
		String cfMissingMessage = messages.cfMissingMessage();
		if ( Window.confirm(cfMissingMessage) ) {
			Location.assign("http://www.google.com/chromeframe");
		}
	}
	
	private void loadCFInstall() {
		final String url = "http://ajax.googleapis.com/ajax/libs/chrome-frame/1/CFInstall.min.js";
	    timer = new Timer() {
			@Override
			public void run() {
				onFailure("Failed to load " + url);
			}
		};
	    timer.schedule(10000);
		loadScript(url);
	}
	
	private native void loadScript(String url)/*-{
		var self = this;
		var body = $doc.getElementsByTagName('head')[0];
		var script = $doc.createElement('script');
		script.type = 'text/javascript';
		script.onreadystatechange= function () {
			if (this.readyState == 'loaded' || this.readyState == 'complete') {
				self.@com.sitecake.contentmanager.client.ModuleIE::scriptLoaded()();
			}
		}
		script.onload = function() {
			self.@com.sitecake.contentmanager.client.ModuleIE::scriptLoaded();
		}
		script.src = url + '?dummy='+(new Date()).getTime();
		body.appendChild(script);
	}-*/;
	
	private void scriptLoaded() {
		if ( cfLoaded ) return;
		
		cfLoaded = true;
		timer.cancel();
		
		String cfMissingMessage = messages.cfMissingMessage();
		if ( Window.confirm(cfMissingMessage) ) {
			startCFcheck();
		}
	}
	
	private void onFailure(String errorMessage) {
		Window.alert(errorMessage);
		showCFMissingNotice();
	}
	
	private native void startCFcheck()/*-{
		setTimeout(function() {
			window.parent.CFInstall.check({
				mode: "overlay"
			});
		}, 400);	
	}-*/;
	
}
