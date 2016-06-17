package com.sitecake.contentmanager.client.resources;




public interface MessagesIt extends com.google.gwt.i18n.client.Messages, Messages {
	
	@DefaultMessage("CMS ha riscontrato un problema!")
	String errorMessage1();
	
	@DefaultMessage("Aiutaci a correggere il problema <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">dicendoci cosa è successo</a>. Includi il rapporto qui in basso. Per continuare le modifiche basta <a href=\"javascript:location.reload()\">ricaricare</a> la pagina.")
	String errorMessage2();
	
	@DefaultMessage("Uncaught Exception")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Clicca per modificare")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Clicca per modificare")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Clicca e carica uno o più file")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Clicca e carica una o più immagini")
	String selectFilesToUploadImage();

	@DefaultMessage("Clicca e carica uno o più video")
	String selectFilesToUploadVideo();

	@DefaultMessage("Clicca e carica uno o più audio")
	String selectFilesToUploadAudio();

	@DefaultMessage("Clicca per aggiungere codice HTML")
	String pasteHtml();
	
	@DefaultMessage("Clicca per inserire un video")
	String pasteVideo();

	@DefaultMessage("Clicca per inserire codice Flash")
	String pasteFlash();
	
	@DefaultMessage("Clicca per inserire una Google map")
	String pasteMap();
	
	@DefaultMessage("Caricamento in corso:")
	String uploadingFiles();
	
	@DefaultMessage("L’editor di CMS necessita del plugin Chrome Frame browser.")
	String cfMissingMessage();
	
	@DefaultMessage("Stile predefinito")
	String defaultStyle();
	
	@DefaultMessage("Web link")
	String linkEditorWebLink();
	
	@DefaultMessage("Email link")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Non tutte le modifiche sono state salvate. Sei sicuro di voler continuare?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Si è verificato un errore nel tentativo di acquisire la licenza.")
	String failedToObtainLicense();
	
	@DefaultMessage("Si è verificato un errore durante l’elaborazione della licenza.")
	String failedToProcessLicense();
	
	@DefaultMessage("Leggi tutto")
	String notificationDialogReadMore();
	
	@DefaultMessage("Si è verificato un errore nella fase di caricamento del file.")
	String failedToUploadFiles();
	
	@DefaultMessage("Il file selezionato non è valido {0}.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Le dimensioni del file selezionato <strong>{0}</strong> superano la dimensione massima consentita per il caricamento di file ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Il file selezionato <strong>{0}</strong> non è un tipo di file consentito per il caricamento.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Un tentativo di salvare le modifiche è fallito. Nuovo tentativo...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Si è verificato un errore durante il salvataggio delle modifiche.")
	String failedToSaveContent();
	
	@DefaultMessage("Si è verificato un errore nel tentativo di pubblicare un contenuto.")
	String failedToPublishContent();
	
	@DefaultMessage("Tutti i tentativi di salvare le ultime modifiche sono falliti.")
	String giveUpContentSaving();
	
	@DefaultMessage("Una nuova versione di CMS è <a href='http://sitecake.com/download.html' target='_blank'>disponibile</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Trovato un parametro di configurazione non valido <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Impossibile creare un video dal codice o dall’URL inseriti.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Impossibile creare una mappa dal codice inserito.")
	String invalidMapInputCode();
	
	@DefaultMessage("Pubblica")
	String publishButton();
}
