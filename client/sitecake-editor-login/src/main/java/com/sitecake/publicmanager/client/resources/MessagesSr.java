package com.sitecake.publicmanager.client.resources;


public interface MessagesSr extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS je naišao na problem!")
	String errorMessage1();
	
	@DefaultMessage("Pomozite nam da otklonimo problem, recite nam <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">šta se dogodilo</a>. Molimo Vas da priložite i donji izvištaj. Za nastavak rada dovoljno je da <a href=javascript:location.reload()\">osvežite</a> stranicu.")
	String errorMessage2();
	
	@DefaultMessage("Lozinka")
	String loginDialogLabel();

	@DefaultMessage("Prijava")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Greška: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Neispravna lozinka")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Pogrešan odgovor od servera")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Admin je već prijavljen")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Prijavljivanje...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Promeni lozinku")
	String switchToChangePassword();

	@DefaultMessage("Lozinka zaboravljena?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Stara lozinka")
	String oldCredentialLabel();
	
	@DefaultMessage("Nova lozinka")
	String newCredentialLabel();

	@DefaultMessage("Nova lozinka (ponovljena)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Izmeni")
	String changeButton();
	
	@DefaultMessage("Odustani")
	String cancelChangeButton();
	
	@DefaultMessage("Nova lozinka nije dobro ponovljena")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Nova lozinka je prekratka")
	String newCredentialTooShort();
	
	@DefaultMessage("Menjam lozinku...")
	String chaningCredential();

	@DefaultMessage("Greška: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Lozinka je uspešno izmenjena. Prijavljujem se...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("Stara lozinka je pogrešna")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Nova lozinka nije prihvaćena")
	String changeAttemptOutcomeInvalidNewCredential();
}
