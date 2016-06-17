package com.sitecake.contentmanager.client.resources;



public interface MessagesDe extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("CMS hat ein Problem festgestellt!")
	String errorMessage1();
	
	@DefaultMessage("Hilf uns den Fehler zu beheben und <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">berichte uns was passiert ist</a>. Bitte füge den Bericht von unten hinzu. Du kannst weiter editieren in dem du einfach die Seite <a href=\"javascript:location.reload()\">neulädst</a>.")
	String errorMessage2();
	
	@DefaultMessage("Nicht abgefangener Ausnahmefehler")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Zum Bearbeiten klicken")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Zum Bearbeiten klicken")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Datei(en) zum Hochladen auswählen")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Bild(er) zum Hochladen auswählen")
	String selectFilesToUploadImage();

	@DefaultMessage("Video(s) zum Hochladen auswählen")
	String selectFilesToUploadVideo();

	@DefaultMessage("Audiodatei(en) zum Hochladen auswählen")
	String selectFilesToUploadAudio();

	@DefaultMessage("Klicken um eigenes HTML einzufügen")
	String pasteHtml();
	
	@DefaultMessage("Klicken um Video einzufügen")
	String pasteVideo();

	@DefaultMessage("Klicken um Flash einzufügen")
	String pasteFlash();
	
	@DefaultMessage("Klicken um Google Map einzufügen")
	String pasteMap();
	
	@DefaultMessage("Hochladen:")
	String uploadingFiles();
	
	@DefaultMessage("CMS Editor benötigt das Chrome Frame Plugin.")
	String cfMissingMessage();
	
	@DefaultMessage("Standard Design")
	String defaultStyle();
	
	@DefaultMessage("Web link")
	String linkEditorWebLink();
	
	@DefaultMessage("Email link")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Es wurden nicht alle Änderungen gespeichert. Sind Sie sicher, dass Sie fortfahren möchten?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Es ist ein Fehler bei der Lizenzabfrage aufgetreten.")
	String failedToObtainLicense();
	
	@DefaultMessage("Der Lizenzschlüssel wurde nicht akzeptiert.")
	String failedToProcessLicense();
	
	@DefaultMessage("Weiterlesen")
	String notificationDialogReadMore();
	
	@DefaultMessage("Während dem Dateiupload ist ein Fehler aufgetreten.")
	String failedToUploadFiles();
	
	@DefaultMessage("Die ausgewählte Datei ist ungültig oder defekt {0}.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Die Größe der ausgewählten Datei <strong>{0}</strong> überschreitet die maximal erlaubte Dateigröße ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Der ausgewählte Dateityp <strong>{0}</strong> ist für den Upload nicht erlaubt.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Das Speichern der Änderungen ist fehlgeschlagen. Versuche es erneut...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Während dem Speichern-Vorgang ist ein Fehler aufgetreten.")
	String failedToSaveContent();
	
	@DefaultMessage("Während dem Veröffentlichungs-Vorgang ist ein Fehler aufgetreten.")
	String failedToPublishContent();
	
	@DefaultMessage("Alle Versuche die letzten Änderungen zu speichern sind gescheitert.")
	String giveUpContentSaving();
	
	@DefaultMessage("Eine neue Version von CMS ist <a href='http://sitecake.com/download.html' target='_blank'>verfügbar</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Üngültige Einstellung gefunden <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Konnte kein Video Objekt erzeugen. Ist der Code oder die URL gültig?")
	String invalidVideoInputCode();
	
	@DefaultMessage("Konnte kein Karten Objekt erzeugen. Ist der Code oder die URL gültig?")
	String invalidMapInputCode();
	
	@DefaultMessage("Veröffent.")
	String publishButton();
}
