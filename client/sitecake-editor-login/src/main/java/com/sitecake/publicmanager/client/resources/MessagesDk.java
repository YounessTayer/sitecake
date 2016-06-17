package com.sitecake.publicmanager.client.resources;


public interface MessagesDk extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("Der opstod et problem med CMS!")
	String errorMessage1();
	
	@DefaultMessage("Fortľl os om problemet og gżr CMS bedre.  <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">Skriv til os her</a> (Engelsk). Inkluder venligst den nedenstŚende rapport. Żnsker du at forsľtte med at redigere, sŚ  <a href=javascript:location.reload()\">opdater siden</a>.")
	String errorMessage2();
	
	@DefaultMessage("Adgangskode")
	String loginDialogLabel();

	@DefaultMessage("Log ind")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Fejl: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Forkert adgangskode")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Fejl ved login")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Dette login er allerede i brug")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Logger ind...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Skift adgangskode")
	String switchToChangePassword();

	@DefaultMessage("Glemt adgangskode?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Gammel adgangskode")
	String oldCredentialLabel();
	
	@DefaultMessage("Ny adgangskode")
	String newCredentialLabel();

	@DefaultMessage("Gentag ny adgangskode")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Skift")
	String changeButton();
	
	@DefaultMessage("Annuler")
	String cancelChangeButton();
	
	@DefaultMessage("De 2 adgangskoder er ikke identiske")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Ny adgangskode er for kort.")
	String newCredentialTooShort();
	
	@DefaultMessage("Skifter adgangskode...")
	String chaningCredential();

	@DefaultMessage("Fejl: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Din adgangskode er ľndret. Du bliver nu logget ind...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("Den gamle adgangskode er ikke korrekt.")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Den nye adgangskode blev ikke blevet accepteret")
	String changeAttemptOutcomeInvalidNewCredential();
}
