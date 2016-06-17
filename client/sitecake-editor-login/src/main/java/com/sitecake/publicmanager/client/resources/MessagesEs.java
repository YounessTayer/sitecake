package com.sitecake.publicmanager.client.resources;


public interface MessagesEs extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("Ups! CMS ha encontrado un problema!")
	String errorMessage1();
	
	@DefaultMessage("Ayúdanos a resolverlo y <a target=\"_blank\"href=\"http://support.sitecake.com/anonymous_requests/new\">cuentanos que ha pasado </a>. Por favor, adjunta el informe de abajo. Para seguir editando <a href=javascript:location.reload()\">recargue</a> la página.")
	String errorMessage2();
	
	@DefaultMessage("Contraseña")
	String loginDialogLabel();

	@DefaultMessage("Iniciar")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Error: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Contraseña erronea")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Error al conectarse al servidor")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Esta sesión ya se encuentra activa")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Iniciando...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Cambiar contraseña")
	String switchToChangePassword();

	@DefaultMessage("¿No la recuerdas?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Contraseña antigua")
	String oldCredentialLabel();
	
	@DefaultMessage("Contraseña nueva")
	String newCredentialLabel();

	@DefaultMessage("Contraseña nueva (otra vez)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Cambiar")
	String changeButton();
	
	@DefaultMessage("Cancelar")
	String cancelChangeButton();
	
	@DefaultMessage("Las contraseñas nuevas no coinciden")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("La nueva contraseña es demasiado corta")
	String newCredentialTooShort();
	
	@DefaultMessage("Cambiando contraseña...")
	String chaningCredential();

	@DefaultMessage("Error: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("La contraseña se ha cambiado correctamente. Iniciando...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("La contraseña antigua no es correcta")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("La contraseña nueva no es válida")
	String changeAttemptOutcomeInvalidNewCredential();
}
