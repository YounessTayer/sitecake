package com.sitecake.publicmanager.client.resources;


public interface MessagesSl extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("Prišlo je do napake!")
	String errorMessage1();
	
	@DefaultMessage("Pomagajte nam popraviti napake s tem, da <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">nam zaupate, kaj se je zgodilo</a>. Naprošamo vas, da vključite spodnje poročilo. Za nadaljevanje urejanja <a href=\"javascript:location.reload()\">ponovno naložite</a> stran.")
	String errorMessage2();
	
	@DefaultMessage("Geslo")
	String loginDialogLabel();

	@DefaultMessage("Vstopi")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Napaka: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Napačno geslo")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Napačna povratna informacija")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Admin je že prijavljen")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Vpis poteka...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Spremeni geslo")
	String switchToChangePassword();

	@DefaultMessage("Ste pozabili geslo?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Obstoječe geslo")
	String oldCredentialLabel();
	
	@DefaultMessage("Novo geslo")
	String newCredentialLabel();

	@DefaultMessage("Ponovite novo geslo")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Spremeni")
	String changeButton();
	
	@DefaultMessage("Preklic")
	String cancelChangeButton();
	
	@DefaultMessage("Novi gesli se ne ujemata")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Novo geslo je prekratko")
	String newCredentialTooShort();
	
	@DefaultMessage("Spreminjanje gesla...")
	String chaningCredential();

	@DefaultMessage("Napaka: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Geslo je spremenjeno. Vpis poteka...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("Obstoječe geslo ni pravilno")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Z vašim novim geslom nekaj ni vredu")
	String changeAttemptOutcomeInvalidNewCredential();
}
