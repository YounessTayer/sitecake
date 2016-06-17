package com.sitecake.publicmanager.client.resources;

public interface Messages {

	//@Description("A general message shown on the crash page in case of an unknown error")
	//@DefaultMessage("CMS encountered a problem!")
	String errorMessage1();
	
	//@Description("The second, more detail message shown on the crash page in case of an unknown error")
	//@DefaultMessage("Help us correct the issue and <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">tell us what happened</a>. Please include the report from below. To continue editing just <a href=javascript:location.reload()\">reload</a> the page.")
	String errorMessage2();
	
	//@Description("The password input label displayed in the login dialog")
	//@DefaultMessage("Password")
	String loginDialogLabel();

	//@Description("The submit button's label in the login dialog")
	//@DefaultMessage("Login")
	String loginDialogSubmitLabel();
	
	//@Description("The prefix of an error outcome while trying to log in")
	//@DefaultMessage("Error: ")
	String loginAttemptOutcomeError();

	//@Description("An error message in case the provided password is incorrect")
	//@DefaultMessage("Wrong password")
	String loginAttemptOutcomeInvalidCredential();

	//@Description("A general error message in case of a login service's error response")
	//@DefaultMessage("Incorrect service response")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	//@Description("A login attempt outcome notice in case there's another active admin session")
	//@DefaultMessage("Admin is already logged in")
	String loginAttemptOutcomeLocked();
	
	//@Description("A login process notice in case the given password is accepted")
	//@DefaultMessage("Logging in...")
	String loginAttemptOutcomeGranted();
	
	//@Description("The label of the button (link) in the login dialog that switches to the change-password dialog")
	//@DefaultMessage("Change password")
	String switchToChangePassword();

	//@Description("The label of the button (link) in the login dialog that should be activated in case the password is forgotten")
	//@DefaultMessage("Can't remember?")
	String forgotPassword();

	//@Description("A language specific URL that leads to the password-recovery instruction page")
	//@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	//@Description("The current password's input label in the change-password dialog")
	//@DefaultMessage("Old Password")
	String oldCredentialLabel();
	
	//@Description("The new password's input label in the change-password dialog")
	//@DefaultMessage("New Password")
	String newCredentialLabel();

	//@Description("The new, repeated password's input label in the change-password dialog")
	//@DefaultMessage("New Password (repeated)")
	String newCredentialRepeatedLabel();
	
	//@Description("The change-password dialog's submitt button label")
	//@DefaultMessage("Change")
	String changeButton();
	
	//@Description("The change-password dialog's cancel button label")
	//@DefaultMessage("Cancel")
	String cancelChangeButton();
	
	//@Description("The change-password dialog's validation message displayed in case the given password strings do not match")
	//@DefaultMessage("New passwords do not match")
	String newCredentialDoesNotMatch();
	
	//@Description("The change-password dialog's validation message displayed in case the given password is too short")
	//@DefaultMessage("New password is too short")
	String newCredentialTooShort();
	
	//@Description("The change-password dialog's message when the password is valid and the submission is in process")
	//@DefaultMessage("Changing password...")
	String chaningCredential();

	//@Description("The change-password dialog's error message prefix")
	//@DefaultMessage("Error: ")
	String changeAttemptOutcomeError();
	
	//@Description("The change-password dialog's message displayed in case the password is changed successfully")
	//@DefaultMessage("Password changed successfully. Logging in...")
	String changeAttemptOutcomeGranted();
	
	//@Description("The change-password dialog's error message shown in case the current (old) password is not correct")
	//@DefaultMessage("The old password is not correct")
	String changeAttemptOutcomeInvalidCredential();
	
	//@Description("The change-password dialog's error message shown in case the new password is rejected by the server")
	//@DefaultMessage("The new password is not acceptable")
	String changeAttemptOutcomeInvalidNewCredential();
}
