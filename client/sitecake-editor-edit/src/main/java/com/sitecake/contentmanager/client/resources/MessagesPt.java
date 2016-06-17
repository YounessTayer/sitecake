package com.sitecake.contentmanager.client.resources;



public interface MessagesPt extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("Ops! O CMS teve um problema!")
	String errorMessage1();
	
	@DefaultMessage("Ajude a corrigir este problema nos informando <a target=\"_blank\" href=\"http://suporte.sitecake.com.br/pt/solicitacoes_anonimas/nova\">o que aconteceu</a>. Por favor inclua o relatório abaixo. Para continuar editando apenas <a href=\"javascript:location.reload()\">atualize</a> a página.")
	String errorMessage2();
	
	@DefaultMessage("Detalhes do problema")
	String uncaughtException();
	
	@DefaultMessage("Invalid response received from the server")	
	String invalidServiceResponse();
	
	@DefaultMessage("Clique para editar")
	String newTextItemDefaultContent();
	
	@DefaultMessage("Clique para editar")
	String newListItemDefaultEntry();
	
	@DefaultMessage("Clique para inserir arquivo(s)")
	String selectFilesToUploadGeneric();

	@DefaultMessage("Clique para inserir imagens")
	String selectFilesToUploadImage();

	@DefaultMessage("Clique para inserir vídeo(s)")
	String selectFilesToUploadVideo();

	@DefaultMessage("Clique para inserir áudio(s)")
	String selectFilesToUploadAudio();

	@DefaultMessage("Clique para adicionar HTML personalizado")
	String pasteHtml();
	
	@DefaultMessage("Clique para inserir vídeo")
	String pasteVideo();

	@DefaultMessage("Clique para inserir Flash")
	String pasteFlash();
	
	@DefaultMessage("Clique para inserir mapa do Google")
	String pasteMap();
	
	@DefaultMessage("Carregando:")
	String uploadingFiles();
	
	@DefaultMessage("Para o CMS funcionar no Internet Explorer você instalar o plugin Google Chrome Frame.")
	String cfMissingMessage();
	
	@DefaultMessage("Estilo padrão")
	String defaultStyle();
	
	@DefaultMessage("Web link")
	String linkEditorWebLink();
	
	@DefaultMessage("Email link")
	String linkEditorMailtoLink();
	
	@DefaultMessage("Nem todas as alterações foram salvas. Tem certeza que deseja continuar?")
	String confirmUnsafeLogout();
	
	@DefaultMessage("Ocorreu um erro ao tentar obter a licença.")
	String failedToObtainLicense();
	
	@DefaultMessage("Ocorreu um erro ao processar a licença.")
	String failedToProcessLicense();
	
	@DefaultMessage("leia mais")
	String notificationDialogReadMore();
	
	@DefaultMessage("Ocorreu um erro durante o processo de carregamento de arquivos.")
	String failedToUploadFiles();
	
	@DefaultMessage("O arquivo {0} não é válido.")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("O arquivo selecionado tem <strong>{0}</strong>, o máximo permitido é de ({1}).")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("O arquivo selecionado é do tipo <strong>{0}</strong>, este formato não é permitido.")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("A tentativa de salvar as modificações de conteúdo falhou. Tentando novamente ...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("Ocorreu um erro ao salvar as modificações de conteúdo.")
	String failedToSaveContent();
	
	@DefaultMessage("Ocorreu um erro ao tentar publicar o conteúdo.")
	String failedToPublishContent();
	
	@DefaultMessage("Todas as tentativas de salvar as modificações de conteúdo falharam.")
	String giveUpContentSaving();
	
	@DefaultMessage("Uma nova versão do CMS <a href='http://sitecake.com.br/pt/download' target='_blank'>está disponível</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("Este parâmetro de configuração esta incorreto: <strong>{0} {1}</strong>.")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("Não foi possível adicionar um vídeo a partir dos dados informados.")
	String invalidVideoInputCode();
	
	@DefaultMessage("Não foi possível adicionar um mapa a partir dos dados informados.")
	String invalidMapInputCode();
	
	@DefaultMessage("Publicar")
	String publishButton();
}
