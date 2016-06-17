package com.sitecake.contentmanager.client.resources;




public interface MessagesPl extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("CMS napotkał problem!")
	String errorMessage1();
	
	@DefaultMessage("Pomóż nam poprawić ten błąd i <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">powiedz nam, co się stało</a>. Jeśli to możliwe, załącz raport poniżej. Aby kontynuować edycję, po prostu <a href=\"javascript:location.reload()\">przeładuj</a> stronę.")
	String errorMessage2();
	
	@DefaultMessage("Uncaught Exception")
	String uncaughtException();
	
	@DefaultMessage("Nieprawidłowa odpowiedź usługi")	
	String invalidServiceResponse();
	
	@DefaultMessage("Kliknij, aby edytować")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Kliknij, aby edytować")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Wybierz plik(i) do dodania")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Wybierz obraz(y) do dodania")
	String selectFilesToUploadImage();

	@DefaultMessage("Wybierz film(y) dodania")
	String selectFilesToUploadVideo();

	@DefaultMessage("Wybierz plik(i) muzyczne do dodania")
	String selectFilesToUploadAudio();

	@DefaultMessage("Kliknij, aby dodać własny HTML")
	String pasteHtml();
	
	@DefaultMessage("Kliknij, aby umieścić wideo")
	String pasteVideo();

	@DefaultMessage("Kliknij, aby umieścić Flash")
	String pasteFlash();
	
	@DefaultMessage("Kliknij, aby umieścić Mapę Google")
	String pasteMap();
	
	@DefaultMessage("Dodaję:")
	String uploadingFiles();
	
	@DefaultMessage("CMS potrzebuje zainstalować wtyczkę Chrome Frame browser.")
	String cfMissingMessage();
	
	@DefaultMessage("Domyślny styl")
	String defaultStyle();
	
	@DefaultMessage("Link do strony")
	String linkEditorWebLink();
	
	@DefaultMessage("Link do e-maila")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Jeszcze nie wszystkie zmiany zostały zapisane. Czy na pewno chcesz kontynuować?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Wystąpił błąd podczas uzyskiwania licencji.")
	String failedToObtainLicense();
	
	@DefaultMessage("Wystąpił błąd podczas przetwarzania licencji.")
	String failedToProcessLicense();
	
	@DefaultMessage("Czytaj dalej")
	String notificationDialogReadMore();
	
	@DefaultMessage("Wystąpił błąd podczas dodawania pliku.")
	String failedToUploadFiles();
	
	@DefaultMessage("Dodawany plik ma niepoprawny format {0}.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Wybrany plik <strong>{0}</strong> przekracza maksymalny dozwolony rozmiar ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Wybrany plik <strong>{0}</strong> nie jest dozwolny przy dodawaniu.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Próba zapisania wprowadzonych zmian treści nie powiodła się. Ponawianie...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Wystąpił błąd podczas zapisywania wprowadzonych zmian.")
	String failedToSaveContent();
	
	@DefaultMessage("Wystąpił błąd podczas próby publikacji treści.")
	String failedToPublishContent();
	
	@DefaultMessage("Wszystkie próby zapisu wprowadzonych zmian zawiodły.")
	String giveUpContentSaving();
	
	@DefaultMessage("Nowa wersja CMS jest <a href='http://sitecake.com/download.html' target='_blank'>dostępna</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Znaleziono niepoprawny parametr konfiguracji <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Nie udało się stworzyć elementu wideo z danego kodu lub adresu URL.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Nie udało się stworzyć mapy z danego kodu.")
	String invalidMapInputCode();
	
	@DefaultMessage("Opublikuj")
	String publishButton();
}
