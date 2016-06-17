package com.sitecake.contentmanager.client.resources;



public interface MessagesZhCn extends com.google.gwt.i18n.client.Messages, Messages {

	@DefaultMessage("CMS出错了！")
	String errorMessage1();

	@DefaultMessage("<a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">提交</a>发现的问题以便我们做得更好，请您附上下面的错误报告。您可以<a href=\"javascript:location.reload()\">刷新</a>此页面继续编辑。")
	String errorMessage2();
	
	@DefaultMessage("未被捕捉异常")
	String uncaughtException();
	
	@DefaultMessage("从服务器收到无效响应")	
	String invalidServiceResponse();
	
	@DefaultMessage("点击开始编辑")
	String newTextItemDefaultContent();
	
	@DefaultMessage("点击开始编辑")
	String newListItemDefaultEntry();
	
	@DefaultMessage("选择一个或多个文件上传")
	String selectFilesToUploadGeneric();

	@DefaultMessage("选择一个或多个图片上传")
	String selectFilesToUploadImage();

	@DefaultMessage("选择一个或多个视频上传")
	String selectFilesToUploadVideo();

	@DefaultMessage("选择一个或多个音频上传")
	String selectFilesToUploadAudio();

	@DefaultMessage("点击加入自定义 HTML")
	String pasteHtml();
	
	@DefaultMessage("点击插入视频")
	String pasteVideo();

	@DefaultMessage("点击插入Flash")
	String pasteFlash();
	
	@DefaultMessage("点击插入Google地图")
	String pasteMap();
	
	@DefaultMessage("上传：")
	String uploadingFiles();
	
	@DefaultMessage("使用CMS需要安装Chrome Frame 浏览器插件。")
	String cfMissingMessage();
	
	@DefaultMessage("缺省样式")
	String defaultStyle();
	
	@DefaultMessage("网页链接")
	String linkEditorWebLink();
	
	@DefaultMessage("邮件地址")
	String linkEditorMailtoLink();
	
	@DefaultMessage("还有更改尚未保存，要继续吗？")
	String confirmUnsafeLogout();
	
	@DefaultMessage("获取许可证出错了。")
	String failedToObtainLicense();
	
	@DefaultMessage("处理许可证出错了。")
	String failedToProcessLicense();
	
	@DefaultMessage("更多")
	String notificationDialogReadMore();
	
	@DefaultMessage("文件上传出错了。")
	String failedToUploadFiles();
	
	@DefaultMessage("选定的上传文件无效：{0}。")
	String invalidUploadFileSelection(String selectionReference);
	
	@DefaultMessage("选定上传文件 <strong>{0}</strong> 超过允许的最大限制 ({1})。")
	String invalidUploadFileMaxSize(String file, String limit);
	
	@DefaultMessage("选定文件 <strong>{0}</strong> 不允许上传。")
	String invalidUploadFileType(String file);
	
	@DefaultMessage("保存内容更改出错。重试...")
	String failedAttemptToSaveContent();
	
	@DefaultMessage("保存内容更改出错。")
	String failedToSaveContent();
	
	@DefaultMessage("发布内容出现错误。")
	String failedToPublishContent();
	
	@DefaultMessage("所有内容更改尝试都失败了。")
	String giveUpContentSaving();
	
	@DefaultMessage("检测到新的CMS更新发布<a href='http://sitecake.com/download.html' target='_blank'>链接</a> ({0}).")
	String versionUpdateMessage(String version);
	
	@DefaultMessage("发现无效配置参数：<strong>{0} {1}</strong>。")
	String invalidConfigParameter(String parameter, String value);
	
	@DefaultMessage("根据指定代码或链接生成视频失败。")
	String invalidVideoInputCode();
	
	@DefaultMessage("根据制定代码生成地图失败。")
	String invalidMapInputCode();
	
	@DefaultMessage("发表")
	String publishButton();
}
