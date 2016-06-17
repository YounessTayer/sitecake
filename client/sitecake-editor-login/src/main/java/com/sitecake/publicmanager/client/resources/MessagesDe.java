package com.sitecake.publicmanager.client.resources;


public interface MessagesDe extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS hat ein Problem festgestellt!")
	String errorMessage1();
	
	@DefaultMessage("Hilf uns den Fehler zu beheben und <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">berichte uns was passiert ist</a>. Bitte füge den Bericht von unten hinzu. Du kannst weiter editieren in dem du einfach die Seite <a href=javascript:location.reload()\">neulädst</a>.")
	String errorMessage2();
	
	@DefaultMessage("Passwort")
	String loginDialogLabel();

	@DefaultMessage("Einloggen")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Fehler: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Falsches Passwort")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Fehlerhafte Serverantwort")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Admin ist bereits eingeloggt.")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Einloggen...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Passwort ändern")
	String switchToChangePassword();

	@DefaultMessage("Passwort vergessen?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/passwort-vergessen")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Altes Passwort")
	String oldCredentialLabel();
	
	@DefaultMessage("Neues Passwort")
	String newCredentialLabel();

	@DefaultMessage("Neues Passwort (wiederholen)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Ändern")
	String changeButton();
	
	@DefaultMessage("Abbrechen")
	String cancelChangeButton();
	
	@DefaultMessage("Neue Passwörter stimmen nicht überein.")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Neues Passwort ist zu kurz.")
	String newCredentialTooShort();
	
	@DefaultMessage("Ändere Passwort...")
	String chaningCredential();

	@DefaultMessage("Fehler: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Passwort erfolgreich geändert. Einloggen...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("Das alte Passwort stimmt nicht")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Das neue Passwort ist nicht gültig")
	String changeAttemptOutcomeInvalidNewCredential();
}
