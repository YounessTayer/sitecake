package com.sitecake.contentmanager.client.resources;



public interface MessagesEn extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("CMS encountered a problem!")
	String errorMessage1();
	
	@DefaultMessage("Help us correct the issue and <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">tell us what happened</a>. Please include the report from below. To continue editing just <a href=\"javascript:location.reload()\">reload</a> the page.")
	String errorMessage2();
	
	@DefaultMessage("Uncaught Exception")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Click to edit")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Click to edit")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Select file(s) to upload")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Select image(s) to upload")
	String selectFilesToUploadImage();

	@DefaultMessage("Select video(s) to upload")
	String selectFilesToUploadVideo();

	@DefaultMessage("Select audio(s) to upload")
	String selectFilesToUploadAudio();

	@DefaultMessage("Click to add custom HTML")
	String pasteHtml();
	
	@DefaultMessage("Click to embed video")
	String pasteVideo();

	@DefaultMessage("Click to embed Flash")
	String pasteFlash();
	
	@DefaultMessage("Click to embed Google map")
	String pasteMap();
	
	@DefaultMessage("Uploading:")
	String uploadingFiles();
	
	@DefaultMessage("CMS editor needs Chrome Frame browser plugin to be installed.")
	String cfMissingMessage();
	
	@DefaultMessage("Default Style")
	String defaultStyle();
	
	@DefaultMessage("Web link")
	String linkEditorWebLink();
	
	@DefaultMessage("Email link")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Not all changes have been saved yet. Are you sure that you want to proceed?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("An error has occured while trying to obtain the license.")
	String failedToObtainLicense();
	
	@DefaultMessage("An error has occured while processing the license.")
	String failedToProcessLicense();
	
	@DefaultMessage("Read more")
	String notificationDialogReadMore();
	
	@DefaultMessage("An error has occured during the file upload process.")
	String failedToUploadFiles();
	
	@DefaultMessage("An invalid upload file selection {0} received.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("The size of the selected file <strong>{0}</strong> exceeds the upload size limit ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("The type of the selected file <strong>{0}</strong> is not allowed for uploading.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("An attempt to save content modifications failed. Retrying...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("An error has occured while saving content modificaitons.")
	String failedToSaveContent();
	
	@DefaultMessage("An error has occured while trying to publish the content.")
	String failedToPublishContent();
	
	@DefaultMessage("All attempts to save the last content modifications failed.")
	String giveUpContentSaving();
	
	@DefaultMessage("A new version of CMS is <a href='http://sitecake.com/download.html' target='_blank'>available</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Invalid configuration parameter found <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Could not create a video item from the given embed code or URL.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Could not create a map item from the given embed code.")
	String invalidMapInputCode();
	
	@DefaultMessage("Publish")
	String publishButton();
}
