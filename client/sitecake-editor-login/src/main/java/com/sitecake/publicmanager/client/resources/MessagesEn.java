package com.sitecake.publicmanager.client.resources;


public interface MessagesEn extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS encountered a problem!")
	String errorMessage1();
	
	@DefaultMessage("Help us correct the issue and <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">tell us what happened</a>. Please include the report from below. To continue editing just <a href=\"javascript:location.reload()\">reload</a> the page.")
	String errorMessage2();
	
	@DefaultMessage("Password")
	String loginDialogLabel();

	@DefaultMessage("Login")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Error: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Wrong password")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Incorrect service response")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Admin is already logged in")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Logging in...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Change password")
	String switchToChangePassword();

	@DefaultMessage("Cannot remember?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Old Password")
	String oldCredentialLabel();
	
	@DefaultMessage("New Password")
	String newCredentialLabel();

	@DefaultMessage("New Password (repeated)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Change")
	String changeButton();
	
	@DefaultMessage("Cancel")
	String cancelChangeButton();
	
	@DefaultMessage("New passwords do not match")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("New password is too short")
	String newCredentialTooShort();
	
	@DefaultMessage("Changing password...")
	String chaningCredential();

	@DefaultMessage("Error: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Password changed successfully. Logging in...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("The old password is not correct")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("The new password is not acceptable")
	String changeAttemptOutcomeInvalidNewCredential();
}
