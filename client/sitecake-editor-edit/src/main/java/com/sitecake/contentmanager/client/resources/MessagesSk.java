package com.sitecake.contentmanager.client.resources;



public interface MessagesSk extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("V CMS došlo k preblému!")
	String errorMessage1();
	
	@DefaultMessage("Pomôžte nám opraviť túto chybu a <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">napíšte čo sa stalo</a>. Uveďte prosím aj nižšie uvedenú správu.  Ak chcete pokračovať v úpravách, <a href=\"javascript:location.reload()\">obnovte</a> stránku.")
	String errorMessage2();
	
	@DefaultMessage("Nezachytená výnimka")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Kliknite pre editáciu")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Kliknite pre editáciu")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Vyberte súbory k nahrávaniu")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Vyberte obrázky k nahrávaniu")
	String selectFilesToUploadImage();

	@DefaultMessage("Vyberte video súbory k nahrávaniu")
	String selectFilesToUploadVideo();

	@DefaultMessage("Vyberte zvukové súbory k nahrávaniu")
	String selectFilesToUploadAudio();

	@DefaultMessage("Kliknite pre pridanie HTML kódu")
	String pasteHtml();
	
	@DefaultMessage("Kliknite pre vloženie videa")
	String pasteVideo();

	@DefaultMessage("Kliknite pre vloženie Flash animácie")
	String pasteFlash();
	
	@DefaultMessage("Kliknite pre vloženie Google mapy")
	String pasteMap();
	
	@DefaultMessage("Nahrávam:")
	String uploadingFiles();
	
	@DefaultMessage("CMS editor potrebuje k svojmu chodu nainštalovať Chrome Frame browser plugin.")
	String cfMissingMessage();
	
	@DefaultMessage("Predvolený štýl")
	String defaultStyle();
	
	@DefaultMessage("Web")
	String linkEditorWebLink();
	
	@DefaultMessage("Email")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Zmeny neboli zaznamenané. Ste si istý, že chcete pokračovať?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Pri pokuse o získaní licencie došlo k chybe.")
	String failedToObtainLicense();
	
	@DefaultMessage("Pri spracovaní licencie došlo k chybe.")
	String failedToProcessLicense();
	
	@DefaultMessage("Rozbaliť")
	String notificationDialogReadMore();
	
	@DefaultMessage("Pri nahrávaní súboru došlo k chybe.")
	String failedToUploadFiles();
	
	@DefaultMessage("Vybraný súbor {0} je neplatný.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Veľkosť súboru <strong>{0}</strong> prekračuje povolený limit ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Typ súboru <strong>{0}</strong> nieje povolený na nahrávanie.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Pokus o uloženie obsahu zlyhal. Skúšam znovu...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Pri ukladaní zmien došlo k chybe.")
	String failedToSaveContent();
	
	@DefaultMessage("Pri pokuse o publikovanie obsahu došlo k chybe.")
	String failedToPublishContent();
	
	@DefaultMessage("Všetky pokusy o uloženie posledných zmien zlyhali.")
	String giveUpContentSaving();
	
	@DefaultMessage("Nová verzia CMS je <a href='http://sitecake.com/download.html' target='_blank'>dostupná</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Neznámy konfiguračný parameter <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Zo zadaného kódu alebo URL adresy sa nedá vytvoriť video.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Zo zadaného kódu alebo URL adresy sa nedá vytvoriť mapa.")
	String invalidMapInputCode();
	
	@DefaultMessage("Publikovať")
	String publishButton();
}
