package com.sitecake.contentmanager.client.resources;



public interface MessagesSl extends com.google.gwt.i18n.client.Messages, Messages {
	
	@DefaultMessage("Prišlo je do napake!")
	String errorMessage1();
	
	@DefaultMessage("Pomagajte nam popraviti napake s tem, da <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">nam zaupate, kaj se je zgodilo</a>. Naprošamo vas, da vključite spodnje poročilo. Za nadaljevanje urejanja <a href=\"javascript:location.reload()\">ponovno naložite</a> stran.")
	String errorMessage2();
	
	@DefaultMessage("Nedefinirana napaka")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Klikni za urejanje")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Klikni za urejanje")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Izberite datoteke, ki jih želite naložiti")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Izberite slike, ki jih želite naložiti")
	String selectFilesToUploadImage();

	@DefaultMessage("Izberite video posnetke, ki jih želite naložiti")
	String selectFilesToUploadVideo();

	@DefaultMessage("Izberte audio posnetke, ki jih želite naložiti")
	String selectFilesToUploadAudio();

	@DefaultMessage("Klikni za dodajanje HTML kode")
	String pasteHtml();
	
	@DefaultMessage("Klikni za dodajanje videa")
	String pasteVideo();

	@DefaultMessage("Klikni za dodajanje Flash datoteke")
	String pasteFlash();
	
	@DefaultMessage("Klikni za dodajanje Google map lokacije")
	String pasteMap();
	
	@DefaultMessage("Nalaganje:")
	String uploadingFiles();
	
	@DefaultMessage("CMS urejevalnik potrebuje naložen Chrome Frame dodatek.")
	String cfMissingMessage();
	
	@DefaultMessage("Privzet stil")
	String defaultStyle();
	
	@DefaultMessage("Url povezava")
	String linkEditorWebLink();
	
	@DefaultMessage("E-pošta")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Vse spremembe še niso shranjene. Ste prepričani, da želite nadaljevati?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Pri pridobivanju licence je prišlo do napake.")
	String failedToObtainLicense();
	
	@DefaultMessage("Pri nalaganju licence je prišlo do napake.")
	String failedToProcessLicense();
	
	@DefaultMessage("Več")
	String notificationDialogReadMore();
	
	@DefaultMessage("Pri nalaganju datoteke je prišlo do napake.")
	String failedToUploadFiles();
	
	@DefaultMessage("Z datoteko {0}, ki ste jo poskušali naložiti, je nekaj narobe.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Velikost izbrane datoteke <strong>{0}</strong> je večja od dovoljene ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Datoteka, ki jo želite naložiti, ni primernega formata <strong>{0}</strong>.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Shranjevanje posodobitev vsebine ni uspelo. Poskušam znova...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Pri shranjevanju posodobitev vsebine je prišlo do napake.")
	String failedToSaveContent();
	
	@DefaultMessage("Pri objavi vsebine je prišlo do napake.")
	String failedToPublishContent();
	
	@DefaultMessage("Vsi poskusi shranjevanja posodobitev vsebine so spodleteli.")
	String giveUpContentSaving();
	
	@DefaultMessage("<a href='http://sitecake.com/download.html' target='_blank'>Na voljo</a> ({0}) je nova verzija CMS urejevalnika.")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Napaka v nastavitvah <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Iz dane povezave se ne da pridobit videa.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Iz dane povezave se ne da pridobit Google map lokacije.")
	String invalidMapInputCode();
	
	@DefaultMessage("Objavi")
	String publishButton();	
}
