package com.sitecake.contentmanager.client.resources;


public interface Messages {

	//@Description("The first line of error message shown in case of an unknown error (uncaught exception)")
	//@DefaultMessage("CMS encountered a problem!")
	String errorMessage1();
	
	//@Description("The second line of error message shown in case of an unknown error (uncaught exception)")
	//@DefaultMessage("Help us correct the issue and <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">tell us what happened</a>. Please include the report from below. To continue editing just <a href=javascript:location.reload()\">reload</a> the page.")
	String errorMessage2();
	
	//@Description("The error details prefix in case of an unknown error")
	//@DefaultMessage("Uncaught Exception")
	String uncaughtException();

	//@Description("The error details prefix in case of an invalid server-side response")
	//@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	//@Description("The initial text content of a new text item (heading, paragraph)")
	//@DefaultMessage("Click to edit")
	String newTextItemDefaultContent();
	
	//@Description("The initial text content of a new list item")
	//@DefaultMessage("Click to edit")
	String newListItemDefaultEntry();
	
	//@Description("The text label of the generic uploader's file selector")
	//@DefaultMessage("Select file(s) to upload")
	String selectFilesToUploadGeneric();

	//@Description("The text label of the image uploader's file selector")
	//@DefaultMessage("Select image(s) to upload")
	String selectFilesToUploadImage();

	//@Description("The text label of the video uploader's file selector")
	//@DefaultMessage("Select video(s) to upload")
	String selectFilesToUploadVideo();

	//@Description("The text label of the audio uploader's file selector")
	//@DefaultMessage("Select audio(s) to upload")
	String selectFilesToUploadAudio();

	//@Description("The text label of the custom HTML item creator")
	//@DefaultMessage("Click to add custom HTML")
	String pasteHtml();
	
	//@Description("The text label of the embedded video item creator")
	//@DefaultMessage("Click to embed video")
	String pasteVideo();

	//@Description("The text label of the embedded flash item creator")
	//@DefaultMessage("Click to embed Flash")
	String pasteFlash();
	
	//@Description("The text label of the google map item creator")
	//@DefaultMessage("Click to embed Google map")
	String pasteMap();
	
	//@Description("The file uploader's prefix label (in front of the uploaded file name)")
	//@DefaultMessage("Uploading:")
	String uploadingFiles();
	
	//@Description("A message that appears in case of IE browser that has no Chrome Frame installed - CF is needed by SiteCake to work correctly in IE")
	//@DefaultMessage("CMS editor needs Chrome Frame browser plugin to be installed.")
	String cfMissingMessage();
	
	//@Description("The text label of the default CSS style for a content item - it appears in the style drop-down list together with custom style names")
	//@DefaultMessage("Default Style")
	String defaultStyle();
	
	//@Description("The text label of the toolbar's link type toggle button - when it is a HTTP link")
	//@DefaultMessage("Web link")
	String linkEditorWebLink();
	
	//@Description("The text label of the toolbar's link type toggle button - when it is an email address")
	//@DefaultMessage("Email link")
	String linkEditorMailtoLink();
	
	//@Description("The warning notification shown in case the logoff button is clicked before all changes are saved")
	//@DefaultMessage("Not all changes have been saved yet. Are you sure that you want to proceed?")
	String confirmUnsafeLogout();
	
	//@Description("The error message that appears in case the license key file could not be found/obtain from the server")
	//@DefaultMessage("An error has occured while trying to obtain the license.")
	String failedToObtainLicense();
	
	//@Description("The error message that appears in case the license key is not valid")
	//@DefaultMessage("An error has occured while processing the license.")
	String failedToProcessLicense();
	
	//@Description("The text label of the link that expands the notification dialog")
	//@DefaultMessage("Read more")
	String notificationDialogReadMore();
	
	//@Description("The error message that appears in case of an problem related to the file uploading process")
	//@DefaultMessage("An error has occured during the file upload process.")
	String failedToUploadFiles();
	
	//@Description("The message shown in case an invalid file selected for upload (e.g. an empty file)")
	//@DefaultMessage("An invalid upload file selection {0} received.")
	String invalidUploadFileSelection(String selectionReference);
	
	//@Description("The message shown in case a file selected for upload exceeds the configured max file size limit ( {1} - the configured max limit )")
	//@DefaultMessage("The size of the selected file <strong>{0}</strong> exceeds the upload size limit ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	//@Description("The message shown in case the file type (extension) of a file selected for upload is not allowed ( {0} - file name )")
	//@DefaultMessage("The type of the selected file <strong>{0}</strong> is not allowed for uploading.")
	String invalidUploadFileType(String file);
	
	//@Description("The message shown in case a content modification has not been saved in the last attempt. A new attempt to save the modifications will be triggered.")
	//@DefaultMessage("An attempt to save content modifications failed. Retrying...")
	String failedAttemptToSaveContent();
	
	//@Description("The error message (that preceeds the error details) shown in case a content modification could not be saved")
	//@DefaultMessage("An error has occured while saving content modifications.")
	String failedToSaveContent();
	
	//@Description("The error message shown in case an error occured while trying to publish the modifcations (click on the publish button)")
	//@DefaultMessage("An error has occured while trying to publish the content.")
	String failedToPublishContent();
	
	//@Description("The warning message shown in case the background saving process gave up trying to save the latest content modification. No additional retries will be made")
	//@DefaultMessage("All attempts to save the last content modifications failed.")
	String giveUpContentSaving();
	
	//@Description("The text notice shown in case a new version/upgrade of SiteCake is available ( {0} - a version available )")
	//@DefaultMessage("A new version of CMS is <a href='http://sitecake.com/download.html' target='_blank'>available</a> ({0}).")
	String versionUpdateMessage(String version);
	
	//@Description("The warning message shown in case the SiteCake configuration contains an invalid paramter ( {0} - parameter name, {1} - parameter value")
	//@DefaultMessage("Invalid configuration parameter found <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	//@Description("The message shown in case a video could not be created/embedded out of the given text/code/URL")
	//@DefaultMessage("Could not create a video item from the given embed code or URL.")
	String invalidVideoInputCode();
	
	//@Description("The message shown in case a map could not be created/embedded out of the given code/URL")
	//@DefaultMessage("Could not create a map item from the given embed code.")
	String invalidMapInputCode();
	
	//@Description("The toolbar publish button's label")
	//@DefaultMessage("Publish")
	String publishButton();
}
