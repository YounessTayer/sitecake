package com.sitecake.contentmanager.client.resources;



public interface MessagesCs extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("CMS narazil na problém!")
	String errorMessage1();
	
	@DefaultMessage("Pomozte nám opravit tuto chybu a <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">napište co se stalo</a>. Uveďte prosím zprávu níže. Chcete-li pokračovat v úpravách, <a href=\"javascript:location.reload()\">obnovte</a> stránku.")
	String errorMessage2();
	
	@DefaultMessage("Nezachycená výjimka")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Klikněte pro editaci")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Klikněte pro editaci")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Vyberte soubory k nahrání")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Vyberte obrázky k nahrání")
	String selectFilesToUploadImage();

	@DefaultMessage("Vyberte video soubory k nahrání")
	String selectFilesToUploadVideo();

	@DefaultMessage("Vyberte zvukové soubory k nahrání")
	String selectFilesToUploadAudio();

	@DefaultMessage("Klikněte pro přidání HTML kódu")
	String pasteHtml();
	
	@DefaultMessage("Klikněte pro vložení videa")
	String pasteVideo();

	@DefaultMessage("Klikněte pro vložení animace Flash")
	String pasteFlash();
	
	@DefaultMessage("Klikněte pro vložení Google mapy")
	String pasteMap();
	
	@DefaultMessage("Nahrávám:")
	String uploadingFiles();
	
	@DefaultMessage("CMS potřebuje ke svému chodu nainstalovat Chrome Frame browser plugin.")
	String cfMissingMessage();
	
	@DefaultMessage("Výchozí styl")
	String defaultStyle();
	
	@DefaultMessage("Web")
	String linkEditorWebLink();
	
	@DefaultMessage("Email")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Všechny změny nebyly doposud zaznamenány. Jste si jisti, že chcete pokračovat?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Při pokusu o získání licence došlo k chybě.")
	String failedToObtainLicense();
	
	@DefaultMessage("Při zpracování licence došlo k chybě.")
	String failedToProcessLicense();
	
	@DefaultMessage("Rozbalit")
	String notificationDialogReadMore();
	
	@DefaultMessage("Při nahrávání souboru došlo k chybě.")
	String failedToUploadFiles();
	
	@DefaultMessage("Vybraný soubor {0} je neplatný.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Velikost souboru <strong>{0}</strong> překračuje povolený limit ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Typ souboru <strong>{0}</strong> není povolen k nahrání.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Pokus o uložení obsahu selhal. Zkouším znovu...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Při ukládání změn došlo k chybě.")
	String failedToSaveContent();
	
	@DefaultMessage("Při pokusu o publikování obsahu došlo k chybě.")
	String failedToPublishContent();
	
	@DefaultMessage("Všechny pokusy o uložení posledních změn selhaly.")
	String giveUpContentSaving();
	
	@DefaultMessage("Nová verze CMS je <a href='http://sitecake.com/download.html' target='_blank'>dostupná</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Nalezen neznámý konfigurační parametr <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Ze zadného kódu nebo URL adresy nelze vytvořit video.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Ze zadného kódu nebo URL adresy nelze vytvořit mapu.")
	String invalidMapInputCode();
	
	@DefaultMessage("Publikovat")
	String publishButton();
}
