package com.sitecake.publicmanager.client.resources;


public interface MessagesPl extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS napotkał problem!")
	String errorMessage1();
	
	@DefaultMessage("Pomóż nam poprawić ten błąd i <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">powiedz nam, co się stało</a>. Jeśli to możliwe, załącz raport poniżej. Aby kontynuować edycję, po prostu <a href=\"javascript:location.reload()\">przeładuj</a> stronę.")
	String errorMessage2();
	
	@DefaultMessage("Hasło")
	String loginDialogLabel();

	@DefaultMessage("Login")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Błąd: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Nieprawidłowe hasło")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Nieprawidłowa odpowiedź usługi")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Admin jest już zalogowany")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Trwa logowanie...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Zmień hasło")
	String switchToChangePassword();

	@DefaultMessage("Nie pamiętasz?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Stare hasło")
	String oldCredentialLabel();
	
	@DefaultMessage("Nowe hasło")
	String newCredentialLabel();

	@DefaultMessage("Nowe hasło (powtórz)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Zmień")
	String changeButton();
	
	@DefaultMessage("Anuluj")
	String cancelChangeButton();
	
	@DefaultMessage("Nowe hasła nie pasują do siebie")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Nowe hasło jest zbyt krótkie")
	String newCredentialTooShort();
	
	@DefaultMessage("Zmieniam hasło...")
	String chaningCredential();

	@DefaultMessage("Błąd: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Poprawnie zmieniono hasło. Trwa logowanie...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("Stare hasło jest niepoprawne.")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Nowe hasło nie jest dopuszczalne.")
	String changeAttemptOutcomeInvalidNewCredential();
}
