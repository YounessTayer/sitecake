package com.sitecake.publicmanager.client.resources;


public interface MessagesFr extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS a rencontré un problème!")
	String errorMessage1();
	
	@DefaultMessage("Aidez-nous à corriger ce problème et <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">dites-nous ce qui s’est passé</a>. Merci d’inclure le rapport d’erreur ci-dessous. Pour continuer à éditer, vous n’avez qu’a <a href=\"javascript:location.reload()\">recharger</a> la page.")
	String errorMessage2();
	
	@DefaultMessage("Mot de passe")
	String loginDialogLabel();

	@DefaultMessage("Identifiant")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Erreur: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Mot de passe incorrect")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Réponse incorrecte du service")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("L’Admin est déjà connecté")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Identification en cours...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Changer")
	String switchToChangePassword();

	@DefaultMessage("Mot de passe oublié?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Ancien mot de passe")
	String oldCredentialLabel();
	
	@DefaultMessage("Nouveau mot de passe")
	String newCredentialLabel();

	@DefaultMessage("Nouveau mot de passe (confirmation)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Changer")
	String changeButton();
	
	@DefaultMessage("Annuler")
	String cancelChangeButton();
	
	@DefaultMessage("La confirmation du nouveau mot de passe ne correspond pas.")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Le nouveau mot de passe est trop court.")
	String newCredentialTooShort();
	
	@DefaultMessage("Changement de mot de passe...")
	String chaningCredential();

	@DefaultMessage("Erreur: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Mot de passe changé avec succès. Identification en cours...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("L’ancien mot de passe est incorrect")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Le nouveau mot de passe n’est pas acceptable")
	String changeAttemptOutcomeInvalidNewCredential();
}
