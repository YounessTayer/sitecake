package com.sitecake.publicmanager.client.resources;


public interface MessagesSk extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS narazil na problém!")
	String errorMessage1();
	
	@DefaultMessage("Pomôžte nám opraviť túto chybu a <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">napište co sa stalo</a>. Uveďte prosím aj nižšie uvedenú správu.  Ak chcete pokračovať v úpravách, <a href=\"javascript:location.reload()\">obnovte</a> stránku.")
	String errorMessage2();
	
	@DefaultMessage("Heslo")
	String loginDialogLabel();

	@DefaultMessage("Login")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Chyba: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Nesprávne heslo")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Nesprávna odpoveď služby")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Admin je už prihlásený")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Prihlasujem...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Zmeniť heslo")
	String switchToChangePassword();

	@DefaultMessage("Zabudnuté heslo?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Súčastné heslo")
	String oldCredentialLabel();
	
	@DefaultMessage("Nové heslo")
	String newCredentialLabel();

	@DefaultMessage("Nové heslo (zopakovať)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Zmeniť")
	String changeButton();
	
	@DefaultMessage("Zrušiť")
	String cancelChangeButton();
	
	@DefaultMessage("Nové zadané heslá nesúhlasia")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Nové heslo je príliš krátke")
	String newCredentialTooShort();
	
	@DefaultMessage("Prebieha zmena hesla...")
	String chaningCredential();

	@DefaultMessage("Chyba: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Heslo bolo úspešne zmenené. Prihlasujem sa...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("Staré heslo je nesprávne")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Nové heslo je neprípustné")
	String changeAttemptOutcomeInvalidNewCredential();
}
