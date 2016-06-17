package com.sitecake.contentmanager.client.resources;

import com.google.gwt.i18n.client.Messages;

public interface NonLocaleMessages extends Messages {
	@DefaultMessage("CMS encountered a problem!")
	String errorMessage1();
	
	@DefaultMessage("Help us correct the issue and <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">tell us what happened</a>. Please include the report from below. To continue editing just <a href=javascript:location.reload()\">reload</a> the page.")
	String errorMessage2();
	
	@DefaultMessage("Uncaught Exception")
	String uncaughtException();

	@DefaultMessage("CMS editor needs Chrome Frame browser plugin to be installed.")
	String cfMissingMessage();	
}
