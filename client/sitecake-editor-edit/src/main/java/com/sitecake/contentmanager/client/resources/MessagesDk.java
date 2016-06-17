package com.sitecake.contentmanager.client.resources;



public interface MessagesDk extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("Der opstod et problem med CMS!")
	String errorMessage1();
	
	@DefaultMessage("Fortľl os om problemet og hjľlp til at gżre CMS bedre.  <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">Skriv til os her</a> (Engelsk). Inkluder venligst den nedenstŚende rapport. Żnsker du at forsľtte med at redigere, sŚ  <a href=\"javascript:location.reload()\">updater siden</a>.")
	String errorMessage2();
	
	@DefaultMessage("Der opstod en ukendt fejl.")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Klik her for at tilfżje tekst")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Klik her for at tilfżje en liste")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Vľlg fil(er) du vil uploade")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Vľlg billede(r) du vil uploadee")
	String selectFilesToUploadImage();

	@DefaultMessage("Vľlg video(er) du vil uploade")
	String selectFilesToUploadVideo();

	@DefaultMessage("Vľlg hvilke(t) lydklip du vil uploade")
	String selectFilesToUploadAudio();

	@DefaultMessage("Klik her for at tilfżje HTML kode")
	String pasteHtml();
	
	@DefaultMessage("Klik her for at indsľtte en video")
	String pasteVideo();

	@DefaultMessage("Klik her for at indsľtte en Flash-fil")
	String pasteFlash();
	
	@DefaultMessage("Klik her for at indsľtte kode fra Google Maps")
	String pasteMap();
	
	@DefaultMessage("Uploader:")
	String uploadingFiles();
	
	@DefaultMessage("For at bruge IE skal du have Chrome Frame browser plugin.")
	String cfMissingMessage();
	
	@DefaultMessage("Standard style")
	String defaultStyle();
	
	@DefaultMessage("Web link")
	String linkEditorWebLink();
	
	@DefaultMessage("Email link")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Du mangler at gemme dine ľndringer - vil du fortsľtte?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Der opstod et problem med din licens.")
	String failedToObtainLicense();
	
	@DefaultMessage("Der opstod et problem under hŚndteringen af din licens.")
	String failedToProcessLicense();
	
	@DefaultMessage("Lľs mere")
	String notificationDialogReadMore();
	
	@DefaultMessage("Der opstod en fejl ved upload af en fil.")
	String failedToUploadFiles();
	
	@DefaultMessage("Der opstod et problem med filen du vil uploade: {0}.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Den valgte fil <strong>{0}</strong> er for stor til at blive uploadet ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("<strong>{0}</strong> kan ikke uploades pga. filtypen.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Der er sket en fejl mens du prżvede at gemme indholdet - prżver igen...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Der er sket en fejl mens du prżvede at gemme indholdet.")
	String failedToSaveContent();
	
	@DefaultMessage("Der er sket en fejl under udgivelsen af hjemmesiden.")
	String failedToPublishContent();
	
	@DefaultMessage("Efter flere forsżg kunne hjemmesiden stadig ikke uploades - prżv igen senere.")
	String giveUpContentSaving();
	
	@DefaultMessage("En ny version af CMS er klar til download <a href='http://sitecake.com/download.html' target='_blank'>Hent den her</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Der er problemer med parametrene i konfigurationen af CMS. Der er problemer med <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Videoen kunne ikke indsľttes pga. problemer med koden eller URL’en.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Kortet kunne ikke oprettes pga. fejl i koden/URL’en.")
	String invalidMapInputCode();
	
	@DefaultMessage("Publish")
	String publishButton();
}
