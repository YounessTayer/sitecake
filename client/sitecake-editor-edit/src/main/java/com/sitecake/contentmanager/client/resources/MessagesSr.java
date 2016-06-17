package com.sitecake.contentmanager.client.resources;




public interface MessagesSr extends com.google.gwt.i18n.client.Messages, Messages {
	
	@DefaultMessage("CMS je naišao na problem!")
	String errorMessage1();
	
	@DefaultMessage("Pomozite nam da otklonimo problem, recite nam <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">šta se dogodilo</a>. Molimo Vas da priložite i donji izvištaj. Za nastavak rada dovoljno je da <a href=\"javascript:location.reload()\">osvežite</a> stranicu.")
	String errorMessage2();
	
	@DefaultMessage("Nepoznata greška")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Kliknite da bi ste izmenili")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Kliknite da bi ste izmenili")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Odaberite datoteke za prenos")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Odaberite slike za prenos")
	String selectFilesToUploadImage();

	@DefaultMessage("Odaberite video zapise za prenos")
	String selectFilesToUploadVideo();

	@DefaultMessage("Odaberite audio zapise za prenos")
	String selectFilesToUploadAudio();

	@DefaultMessage("Kliknite za dodavanje HTML koda")
	String pasteHtml();
	
	@DefaultMessage("Kliknite za dodavanje video zapisa")
	String pasteVideo();

	@DefaultMessage("Kliknite za dodavanje Flash-a")
	String pasteFlash();
	
	@DefaultMessage("Kliknite za dodavanje Google mape")
	String pasteMap();
	
	@DefaultMessage("Prenos:")
	String uploadingFiles();
	
	@DefaultMessage("CMS-u je potreban Chrome Frame dodatak za pretraživač da bi se mogao koristiti.")
	String cfMissingMessage();
	
	@DefaultMessage("Podrazumevani stil")
	String defaultStyle();
	
	@DefaultMessage("Web veza")
	String linkEditorWebLink();
	
	@DefaultMessage("Email veza")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Poslednje izmene nisu još sačuvane. Da li ste sigurni da želite da nastavite?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Prilikom dopremanja licence se dogodila greška.")
	String failedToObtainLicense();
	
	@DefaultMessage("Prilikom obrade licence se dogodila greška.")
	String failedToProcessLicense();
	
	@DefaultMessage("Više")
	String notificationDialogReadMore();
	
	@DefaultMessage("Prilikom otpremanja se dogodila greška.")
	String failedToUploadFiles();
	
	@DefaultMessage("Odabran tip datoteke ({0}) nije podržan.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Veličina odabrane datoteke <strong>{0}</strong> je veća od dozvoljene ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Odabrani tip datoteke <strong>{0}</strong> nije dozvoljen za otpremanje.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Pokušaj da se snime poslednje izmene nije uspeo. Pokušavam ponovo...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Došlo je do greške prilikom snimanja poslednjih izmena.")
	String failedToSaveContent();
	
	@DefaultMessage("Došlo je do greške prilikom objavljivanja.")
	String failedToPublishContent();
	
	@DefaultMessage("Ni jedan pokušaj snimanja poslednjih izmena nije uspeo.")
	String giveUpContentSaving();
	
	@DefaultMessage("Nova verzija CMS-a je <a href='http://sitecake.com/download.html' target='_blank'>dostupna</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Konfiguracioni parametar <strong>{0} {1}</strong> nije ispravan.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Nije moguće kreirati video na osnovu unetog koda.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Nije moguće kreirati mapu na osnovu unetog koda.")
	String invalidMapInputCode();
	
	@DefaultMessage("Objavi")
	String publishButton();
}
