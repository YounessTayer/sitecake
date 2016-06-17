package com.sitecake.publicmanager.client.resources;


public interface MessagesRu extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS обнаружил проблему!")
	String errorMessage1();
	
	@DefaultMessage("Помогите нам исправить проблему и <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">расскажите, что произошло</a>. Пожалуйста, включите ошибку в отчет ниже. Чтобы продолжить редактирование просто <a href=\"javascript:location.reload()\">обновите</a> страницу.")
	String errorMessage2();
	
	@DefaultMessage("Пароль")
	String loginDialogLabel();

	@DefaultMessage("Логин")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("Ошибка: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("Неправильный пароль")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("Неправильная реакция службы")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("Администратор уже авторизирован")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("Авторизируемся...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("Изменить пароль")
	String switchToChangePassword();

	@DefaultMessage("Забыли пароль?")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("Старый пароль")
	String oldCredentialLabel();
	
	@DefaultMessage("Новый пароль")
	String newCredentialLabel();

	@DefaultMessage("Новый пароль (повторно)")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("Изменить")
	String changeButton();
	
	@DefaultMessage("Отмена")
	String cancelChangeButton();
	
	@DefaultMessage("Пароли не совпадают")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("Пароль слишком короткий")
	String newCredentialTooShort();
	
	@DefaultMessage("Изменяем пароль...")
	String chaningCredential();

	@DefaultMessage("Ошибка: ")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("Пароль успешно изменён. Авторизируемся...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("Старай пароль введён не верно")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("Новый пароль не является приемлемым")
	String changeAttemptOutcomeInvalidNewCredential();
}
