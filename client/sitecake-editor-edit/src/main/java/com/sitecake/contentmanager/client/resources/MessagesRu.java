package com.sitecake.contentmanager.client.resources;



public interface MessagesRu extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("CMS обнаржил ошибку!")
	String errorMessage1();
	
	@DefaultMessage("Помогите нам исправить проблему и <a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">расскажите, что произошло</a>. Пожалуйста, включите ошибку в отчет ниже. Чтобы продолжить редактирование просто <a href=\"javascript:location.reload()\">обновите</a> страницу.")
	String errorMessage2();
	
	@DefaultMessage("Неизвестная ошибка")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Нажмите для редактирования")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Нажмите для редактирования")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Выберите файл(лы) для загрзки")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Выберите изображение(я) для загрузки")
	String selectFilesToUploadImage();

	@DefaultMessage("Выберите видео для загрузки")
	String selectFilesToUploadVideo();

	@DefaultMessage("Выберите аудио для загрузки")
	String selectFilesToUploadAudio();

	@DefaultMessage("Нажмите чтобы добавить стандартный код HTML")
	String pasteHtml();
	
	@DefaultMessage("Нажмите чтобы добавить видео")
	String pasteVideo();

	@DefaultMessage("Нажмите чтобы добавить флеш анимацию")
	String pasteFlash();
	
	@DefaultMessage("Нажмите чтобы добавить карту Google")
	String pasteMap();
	
	@DefaultMessage("Загрузка:")
	String uploadingFiles();
	
	@DefaultMessage("Редактор CMS рекомендует установить плагин для Google Chrome, Frame.")
	String cfMissingMessage();
	
	@DefaultMessage("По умолчанию")
	String defaultStyle();
	
	@DefaultMessage("Ссылка")
	String linkEditorWebLink();
	
	@DefaultMessage("Email")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Изменения не сохранены. Вы уверены, что хотите продолжить?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Произошла ошибка при попытке получить лицензию.")
	String failedToObtainLicense();
	
	@DefaultMessage("Произошла ошибка при обработке лицензии.")
	String failedToProcessLicense();
	
	@DefaultMessage("Читать далее")
	String notificationDialogReadMore();
	
	@DefaultMessage("Произошла ошибка при загрузке файла.")
	String failedToUploadFiles();
	
	@DefaultMessage("Недействительным загрузки файла <strong>{0}</strong> sdfjlsd получил.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("Размер выбранного <strong>{0}</strong> файла превышает лимит отдачи размера ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("Типа выбранного файла <strong>{0}</strong> не допускается для загрузки.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("Попытка сохранить содержимое не удалась. Пробуем ещё раз...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Возникла ошибка при сохранении изменений содержимого.")
	String failedToSaveContent();
	
	@DefaultMessage("Возникла ошибка при публикации содержимого.")
	String failedToPublishContent();
	
	@DefaultMessage("Все попытки сохранить последние изменения содержимого не увенчались успехом.")
	String giveUpContentSaving();
	
	@DefaultMessage("<a href='http://sitecake.com/download.html' target='_blank'>Доступна</a> новая версия CMS ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Обнаружена неправильная конфигурация параметров <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Не удалось создать видео из данного кода или ссылки.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Не удалось создать объект на карте по заданному коду.")
	String invalidMapInputCode();
	
	@DefaultMessage("Опубликовать")
	String publishButton();
}
