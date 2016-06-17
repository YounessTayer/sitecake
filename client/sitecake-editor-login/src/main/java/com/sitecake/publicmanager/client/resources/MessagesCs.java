package com.sitecake.publicmanager.client.resources;


public interface MessagesCs extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS narazil na problém!")
	String errorMessage1();
	
	@DefaultMessage("Pomozte nám opravit tuto chybu a <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">napište co se stalo</a>. Uveďte prosím zprávu níže. Chcete-li pokračovat v úpravách, <a href=\"javascript:location.reload()\">obnovte</a> stránku.")
	String errorMessage2();
	
	@DefaultMessage("Heslo")
	String loginDialogLabel();

	@DefaultMessage("Login")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Chyba: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Nesprávné heslo")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Nesprávná odpověď služby")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Admin je právě přihlášen")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Přihlašuji...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Změnit heslo")
	String switchToChangePassword();

	@DefaultMessage("Zapomenuté heslo?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Současné heslo")
	String oldCredentialLabel();
	
	@DefaultMessage("Nové heslo")
	String newCredentialLabel();

	@DefaultMessage("Nové heslo (zopakovat)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Změnit")
	String changeButton();
	
	@DefaultMessage("Zrušit")
	String cancelChangeButton();
	
	@DefaultMessage("Zadaná nová hesla nesouhlasí")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Nové heslo je příliš krátké")
	String newCredentialTooShort();
	
	@DefaultMessage("Probíhá změna hesla...")
	String chaningCredential();

	@DefaultMessage("Chyba: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Heslo bylo úspěšně změněno. Přihlašuji se...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("Současné heslo není správné")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Nové heslo je nepřípustné")
	String changeAttemptOutcomeInvalidNewCredential();
}
