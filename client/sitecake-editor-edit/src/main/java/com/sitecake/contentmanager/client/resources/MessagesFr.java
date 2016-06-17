package com.sitecake.contentmanager.client.resources;



public interface MessagesFr extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("CMS a rencontré un problème!")
	String errorMessage1();
	
	@DefaultMessage("Aidez-nous à corriger ce problème et <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">dites-nous ce qui s’est passé</a>. Merci d’inclure le rapport d’erreur ci-dessous. Pour continuer à éditer, vous n’avez qu’a <a href=\"javascript:location.reload()\">recharger</a> la page.")
	String errorMessage2();
	
	@DefaultMessage("Erreur inconnue")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Cliquer pour éditer")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Cliquer pour éditer")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Selectionner les fichier(s) à envoyer")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Selectionner les image(s) à envoyer")
	String selectFilesToUploadImage();

	@DefaultMessage("Selectionner les video(s) à envoyer")
	String selectFilesToUploadVideo();

	@DefaultMessage("Selectionner les son(s) à envoyer")
	String selectFilesToUploadAudio();

	@DefaultMessage("Cliquer pour ajouter du code HTML")
	String pasteHtml();
	
	@DefaultMessage("Cliquer pour intégrer une vidéo")
	String pasteVideo();

	@DefaultMessage("Cliquer pour intégrer une animation Flash")
	String pasteFlash();
	
	@DefaultMessage("Cliquer pour intégrer une carte Google map")
	String pasteMap();
	
	@DefaultMessage("Envoi en cours:")
	String uploadingFiles();
	
	@DefaultMessage("Le plugin Chrom Frame doit être installé sur votre navigateur web pour que l’éditeur de CMS fonctionne.")
	String cfMissingMessage();
	
	@DefaultMessage("Style par défaut")
	String defaultStyle();
	
	@DefaultMessage("Web link")
	String linkEditorWebLink();
	
	@DefaultMessage("Email link")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Toutes les modifications n’ont pas encore été toutes sauvegardées. Êtes-vos sûr(e) de vouloir continuer?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Une erreur s’est produite pendant la récupération de la licence.")
	String failedToObtainLicense();
	
	@DefaultMessage("Une erreur s’est produite pendant l’analyse de votre licence.")
	String failedToProcessLicense();
	
	@DefaultMessage("Lire la suite")
	String notificationDialogReadMore();
	
	@DefaultMessage("Une erreur s’est produite pendant l’envoi du fichier.")
	String failedToUploadFiles();
	
	@DefaultMessage("La sélection de fichier est invalide: {0}.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("La taille du fichier sélectionné <strong>{0}</strong> dépasse la taille limite d’envoi ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Le type du fichier sélectionné <strong>{0}</strong> n’est pas autorisé à l’envoi.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("La tentative de sauvegarde des modification à echoué. Merci de bien vouloir retenter...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Une erreur s’est produite pendant la sauvegarde des modifications.")
	String failedToSaveContent();
	
	@DefaultMessage("Une erreur s’est produite pendant la publication du contenu.")
	String failedToPublishContent();
	
	@DefaultMessage("Toutes les tentatives de sauvegarde des dernières modifications ont échoué.")
	String giveUpContentSaving();
	
	@DefaultMessage("Une nouvelle version de CMS est <a href='http://sitecake.com/download.html' target='_blank'>disponible</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Paramètre de configuration invalide trouvé <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Impossible de créer un élément vidéo depuis l’URL ou le code d’insertion fourni.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Impossible de créer un élément de carte depuis le code d’insertion fourni.")
	String invalidMapInputCode();
	
	@DefaultMessage("Publier")
	String publishButton();
}
