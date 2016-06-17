package com.sitecake.publicmanager.client.resources;


public interface MessagesZhCn extends Messages, com.google.gwt.i18n.client.Messages {
	
	@DefaultMessage("CMS 出错了！")
	String errorMessage1();
	
	@DefaultMessage("<a target=\"_blank\" href=\"http://support.sitecake.com/anonymous_requests/new\">提交</a>发现的问题以便我们做得更好，请您附上下面的错误报告。 您可以<a href=\"javascript:location.reload()\">刷新</a>此页面继续编辑。")
	String errorMessage2();
	
	@DefaultMessage("密码")
	String loginDialogLabel();

	@DefaultMessage("登录")
	String loginDialogSubmitLabel();
	
	@DefaultMessage("错误: ")
	String loginAttemptOutcomeError();

	@DefaultMessage("密码错误")
	String loginAttemptOutcomeInvalidCredential();

	@DefaultMessage("错误登录服务响应")
	String loginAttemptOutcomeInvalidServiceResponse();
	
	@DefaultMessage("管理员已经登录")
	String loginAttemptOutcomeLocked();
	
	@DefaultMessage("登录...")
	String loginAttemptOutcomeGranted();
	
	@DefaultMessage("修改密码")
	String switchToChangePassword();

	@DefaultMessage("忘记密码？")
	String forgotPassword();

	@DefaultMessage("http://sitecake.com/faq/password-forgotten")
	String forgotPasswordLinkUrl();
	
	@DefaultMessage("原密码")
	String oldCredentialLabel();
	
	@DefaultMessage("新密码")
	String newCredentialLabel();

	@DefaultMessage("确认密码")
	String newCredentialRepeatedLabel();
	
	@DefaultMessage("修改")
	String changeButton();
	
	@DefaultMessage("取消")
	String cancelChangeButton();
	
	@DefaultMessage("两次输入的新密码不同")
	String newCredentialDoesNotMatch();
	
	@DefaultMessage("新密码长度过短")
	String newCredentialTooShort();
	
	@DefaultMessage("更新密码...")
	String chaningCredential();

	@DefaultMessage("错误：")
	String changeAttemptOutcomeError();
	
	@DefaultMessage("成功修改密码。重新登录...")
	String changeAttemptOutcomeGranted();
	
	@DefaultMessage("原密码错误")
	String changeAttemptOutcomeInvalidCredential();
	
	@DefaultMessage("新密码未被服务器接受")
	String changeAttemptOutcomeInvalidNewCredential();
}
