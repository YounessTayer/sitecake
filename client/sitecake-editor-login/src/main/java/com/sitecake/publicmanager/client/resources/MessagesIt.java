package com.sitecake.publicmanager.client.resources;


public interface MessagesIt extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS ha riscontrato un problema!")
	String errorMessage1();
	
	@DefaultMessage("Aiutaci a correggere il problema <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">dicendoci cosa è successo</a>. Includi il rapporto qui in basso. Per continuare le modifiche basta <a href=\"javascript:location.reload()\">ricaricare</a> la pagina.")
	String errorMessage2();
	
	@DefaultMessage("Password")
	String loginDialogLabel();

	@DefaultMessage("Accedi")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Errore: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Password errata")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Incorrect service response")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Un amministratore risulta già autenticato")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Accesso...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Cambia password")
	String switchToChangePassword();

	@DefaultMessage("Hai dimenticato la password?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Vecchia Password")
	String oldCredentialLabel();
	
	@DefaultMessage("Nuova Password")
	String newCredentialLabel();

	@DefaultMessage("Nuova Password (ripeti)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Cambia")
	String changeButton();
	
	@DefaultMessage("Annulla")
	String cancelChangeButton();
	
	@DefaultMessage("La nuova password non corrisponde")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("La nuova password è troppo breve")
	String newCredentialTooShort();
	
	@DefaultMessage("Cambio password...")
	String chaningCredential();

	@DefaultMessage("Errore: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Password modificata correttamente. Accesso...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("La vecchia password non è corretta")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("La nuova password non è soddisfacente")
	String changeAttemptOutcomeInvalidNewCredential();
}
