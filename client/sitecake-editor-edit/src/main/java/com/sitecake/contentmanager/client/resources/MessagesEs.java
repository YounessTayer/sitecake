package com.sitecake.contentmanager.client.resources;




public interface MessagesEs extends com.google.gwt.i18n.client.Messages, Messages {
	
	@DefaultMessage("Ups! CMS ha encontrado un problema!")
	String errorMessage1();
	
	@DefaultMessage("Ayúdanos a resolverlo <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">cuentanos que ha pasado</a>. Por favour, adjunta el informe de abajo. Para seguir editando <a href=\"javascript:location.reload()\">recargue</a> la página.")
	String errorMessage2();
	
	@DefaultMessage("Error desconocido")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Click para editar")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Click para editar")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Selecciona archivo(s) a cargar")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Selecciona imagen(es) a cargar")
	String selectFilesToUploadImage();

	@DefaultMessage("Selecciona video(s) a cargar")
	String selectFilesToUploadVideo();

	@DefaultMessage("Selecciona audio(s) a cargar")
	String selectFilesToUploadAudio();

	@DefaultMessage("Click para añadir HTML")
	String pasteHtml();
	
	@DefaultMessage("Click para añadir video")
	String pasteVideo();

	@DefaultMessage("Click para añadir Flash")
	String pasteFlash();
	
	@DefaultMessage("Click para añadir mapa de Google")
	String pasteMap();
	
	@DefaultMessage("Cargando:")
	String uploadingFiles();
	
	@DefaultMessage("El editor de CMS necesita el plugin Chrome Frame para funcionar adecuadamamente en Internet Explorer.")
	String cfMissingMessage();
	
	@DefaultMessage("Estilo por defecto")
	String defaultStyle();
	
	@DefaultMessage("Web link")
	String linkEditorWebLink();
	
	@DefaultMessage("Email link")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Todavía no se han guardado los cambios. ¿Seguro que quieres salir?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("A ocurrido un error al tratar de obtener la licencia.")
	String failedToObtainLicense();
	
	@DefaultMessage("A ocurrido un error al procesar la licencia.")
	String failedToProcessLicense();
	
	@DefaultMessage("Leer mas")
	String notificationDialogReadMore();
	
	@DefaultMessage("A ocurrido un error durante el proceso de carga.")
	String failedToUploadFiles();
	
	@DefaultMessage("El archivo ha cargar no es válido o se encuentra vacío ({0}).")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("El tamaño del archivo <strong>{0}</strong> supera el límite de carga ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("El tipo de archivo <strong>{0}</strong> no permite la carga.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("No se han podido guardar los cambios. Intentando de nuevo...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Ha ocurrido un error al tratar de guardar los cambios.")
	String failedToSaveContent();
	
	@DefaultMessage("Ha ocurrido un error al tratar de publicar el contenido.")
	String failedToPublishContent();
	
	@DefaultMessage("Los intentos de guardar los cambios han fallado.")
	String giveUpContentSaving();
	
	@DefaultMessage("Hay una nueva versión de CMS disponible <a href='http://sitecake.com/download.html' target='_blank'>aquí</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Parámetro de configuaración invalido <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("No se puede incluir un vídeo con ese código o URL.")
	String invalidVideoInputCode();
	
	@DefaultMessage("No se puede incluir un mapa con ese código o URL.")
	String invalidMapInputCode();
	
	@DefaultMessage("Publicar")
	String publishButton();
}
